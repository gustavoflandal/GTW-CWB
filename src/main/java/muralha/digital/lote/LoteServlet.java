package muralha.digital.lote;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.acessos.Usuario;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/MuralhaDigital/Lote")
public class LoteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String acao = req.getParameter("acao");

            if ("itens".equals(acao)) {
                long idLote = Long.parseLong(req.getParameter("idLote"));
                JsonArray itens = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT li.id_infracao, vtr.placa, vtr.id_local, vtr.id_pista, " +
                        "CONVERT(VARCHAR(19), vtr.data, 120) AS dt_captura " +
                        "FROM muralha.lote_infracao_item li " +
                        "JOIN muralha.veiculo_tempo_real vtr ON vtr.id = li.id_infracao " +
                        "WHERE li.id_lote = ?")) {
                    ps.setLong(1, idLote);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            JsonObject item = new JsonObject();
                            item.addProperty("id", rs.getString("id_infracao"));
                            item.addProperty("placa", rs.getString("placa"));
                            item.addProperty("idLocal", rs.getInt("id_local"));
                            item.addProperty("idPista", rs.getInt("id_pista"));
                            item.addProperty("dtCaptura", rs.getString("dt_captura"));
                            itens.add(item);
                        }
                    }
                }
                result.addProperty("ok", true);
                result.add("itens", itens);

            } else {
                JsonArray lotes = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT l.id, l.codigo, l.descricao, l.status, " +
                        "CONVERT(VARCHAR(19), l.dt_criacao, 120) AS dt_criacao, " +
                        "CONVERT(VARCHAR(19), l.dt_envio, 120) AS dt_envio, " +
                        "(SELECT COUNT(*) FROM muralha.lote_infracao_item WHERE id_lote = l.id) AS qtd " +
                        "FROM muralha.lote_infracao l ORDER BY l.id DESC");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject l = new JsonObject();
                        l.addProperty("id", rs.getLong("id"));
                        l.addProperty("codigo", rs.getString("codigo"));
                        l.addProperty("descricao", rs.getString("descricao"));
                        l.addProperty("status", rs.getString("status"));
                        l.addProperty("dtCriacao", rs.getString("dt_criacao"));
                        l.addProperty("dtEnvio", rs.getString("dt_envio"));
                        l.addProperty("qtd", rs.getInt("qtd"));
                        lotes.add(l);
                    }
                }
                result.addProperty("ok", true);
                result.add("lotes", lotes);
            }

        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String acao = req.getParameter("acao");
            Usuario usu = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
            int idUsuario = usu.getIdUsuario();

            if ("criar".equals(acao)) {
                String codigo = "LOTE-" + LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String descricao = req.getParameter("descricao");
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO muralha.lote_infracao (codigo, descricao, id_usuario_criou) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, codigo);
                    ps.setString(2, descricao);
                    ps.setInt(3, idUsuario);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        result.addProperty("idLote", keys.getLong(1));
                    }
                }
                result.addProperty("ok", true);
                result.addProperty("codigo", codigo);
                AuditoriaService.registrar(req, "Lote", "criar", codigo, "Lote criado");

            } else if ("adicionarInfracoes".equals(acao)) {
                long idLote = Long.parseLong(req.getParameter("idLote"));
                String[] idsStr = req.getParameter("ids").split(",");
                int adicionados = 0;
                for (String idStr : idsStr) {
                    String id = idStr.trim();
                    if (id.isEmpty()) continue;
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO muralha.lote_infracao_item (id_lote, id_infracao) " +
                            "SELECT ?, CAST(? AS UNIQUEIDENTIFIER) " +
                            "WHERE NOT EXISTS (SELECT 1 FROM muralha.lote_infracao_item WHERE id_infracao = CAST(? AS UNIQUEIDENTIFIER))")) {
                        ps.setLong(1, idLote);
                        ps.setString(2, id);
                        ps.setString(3, id);
                        adicionados += ps.executeUpdate();
                    }
                }
                result.addProperty("ok", true);
                result.addProperty("adicionados", adicionados);

            } else if ("enviar".equals(acao)) {
                long idLote = Long.parseLong(req.getParameter("idLote"));
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE muralha.lote_infracao SET status = 'ENVIADO', " +
                        "dt_envio = SYSDATETIME(), id_usuario_enviou = ? " +
                        "WHERE id = ? AND status = 'RASCUNHO'")) {
                    ps.setInt(1, idUsuario);
                    ps.setLong(2, idLote);
                    int rows = ps.executeUpdate();
                    result.addProperty("ok", rows > 0);
                    if (rows == 0) result.addProperty("erro", "Lote não encontrado ou já enviado");
                }
                AuditoriaService.registrar(req, "Lote", "enviar",
                        String.valueOf(idLote), "Lote enviado para DETRAN");

            } else if ("cancelar".equals(acao)) {
                long idLote = Long.parseLong(req.getParameter("idLote"));
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE muralha.lote_infracao SET status = 'CANCELADO' " +
                        "WHERE id = ? AND status = 'RASCUNHO'")) {
                    ps.setLong(1, idLote);
                    int rows = ps.executeUpdate();
                    result.addProperty("ok", rows > 0);
                    if (rows == 0) result.addProperty("erro", "Lote não encontrado ou não pode ser cancelado");
                }

            } else {
                result.addProperty("ok", false);
                result.addProperty("erro", "Ação inválida");
            }

        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }
}
