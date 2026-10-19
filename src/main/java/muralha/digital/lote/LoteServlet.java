package muralha.digital.lote;

import com.consilux.lib.Conexao;
import com.consilux.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.acessos.ControleAcesso;
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
import java.util.List;
import java.util.UUID;

@WebServlet("/MuralhaDigital/Lote")
public class LoteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ControleAcesso.exigir(req, resp, LoteRegras.ACAO_TELA)) return;

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
                        "LEFT JOIN muralha.veiculo_tempo_real vtr ON vtr.id = li.id_infracao " +
                        "WHERE li.id_lote = ?")) {
                    ps.setLong(1, idLote);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            JsonObject item = new JsonObject();
                            item.addProperty("id", rs.getString("id_infracao"));
                            item.addProperty("placa", rs.getString("placa"));
                            item.addProperty("idLocal", (Number) rs.getObject("id_local"));
                            item.addProperty("idPista", (Number) rs.getObject("id_pista"));
                            item.addProperty("dtCaptura", rs.getString("dt_captura"));
                            itens.add(item);
                        }
                    }
                }
                result.addProperty("ok", true);
                result.add("itens", itens);

            } else if ("disponiveis".equals(acao)) {
                JsonArray itens = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT TOP 500 CAST(vsa.id_veiculo_tempo_real AS VARCHAR(36)) AS id, vtr.placa, vtr.id_local, " +
                        "CONVERT(VARCHAR(19), vtr.data, 120) AS dt_captura " +
                        "FROM muralha.vtr_status_analise vsa " +
                        "LEFT JOIN muralha.veiculo_tempo_real vtr ON vtr.id = vsa.id_veiculo_tempo_real " +
                        "WHERE vsa.status_analise = 'PRE_APROVADA' " +
                        "AND NOT EXISTS (SELECT 1 FROM muralha.lote_infracao_item li WHERE li.id_infracao = vsa.id_veiculo_tempo_real) " +
                        "ORDER BY vtr.data");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject item = new JsonObject();
                        item.addProperty("id", rs.getString("id"));
                        item.addProperty("placa", rs.getString("placa"));
                        item.addProperty("idLocal", (Number) rs.getObject("id_local"));
                        item.addProperty("dtCaptura", rs.getString("dt_captura"));
                        itens.add(item);
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
        resp.getWriter().print(GSON.toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ControleAcesso.exigir(req, resp, LoteRegras.ACAO_TELA)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String acao = req.getParameter("acao");
            Usuario usu = ControleAcesso.usuario(req);
            int idUsuario = usu.getId();

            if ("criar".equals(acao)) {
                String codigo = "LOTE-" + LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String descricao = req.getParameter("descricao");
                if (descricao != null && descricao.length() > 200) descricao = descricao.substring(0, 200);
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
                List<UUID> ids = null;
                try {
                    ids = LoteRegras.idsValidos(req.getParameter("ids"));
                } catch (IllegalArgumentException e) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", e.getMessage());
                }
                if (ids != null) {
                    String status = null;
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT status FROM muralha.lote_infracao WHERE id = ?")) {
                        ps.setLong(1, idLote);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) status = rs.getString(1);
                        }
                    }
                    if (!"RASCUNHO".equals(status)) {
                        result.addProperty("ok", false);
                        result.addProperty("erro", "Só é possível incluir infrações em lote em rascunho.");
                    } else {
                        int adicionados = 0;
                        boolean autoCommitOriginal = conn.getAutoCommit();
                        conn.setAutoCommit(false);
                        try {
                            for (UUID id : ids) {
                                try (PreparedStatement ps = conn.prepareStatement(
                                        "INSERT INTO muralha.lote_infracao_item (id_lote, id_infracao) " +
                                        "SELECT ?, ? WHERE EXISTS (SELECT 1 FROM muralha.vtr_status_analise " +
                                        "WHERE id_veiculo_tempo_real = ? AND status_analise = 'PRE_APROVADA') " +
                                        "AND NOT EXISTS (SELECT 1 FROM muralha.lote_infracao_item WHERE id_infracao = ?)")) {
                                    ps.setLong(1, idLote);
                                    ps.setString(2, id.toString());
                                    ps.setString(3, id.toString());
                                    ps.setString(4, id.toString());
                                    adicionados += ps.executeUpdate();
                                }
                            }
                            conn.commit();
                        } catch (Exception e) {
                            try { conn.rollback(); } catch (Exception ignored) {}
                            throw e;
                        } finally {
                            try { conn.setAutoCommit(autoCommitOriginal); } catch (Exception ignored) {}
                        }
                        result.addProperty("ok", true);
                        result.addProperty("adicionados", adicionados);
                        result.addProperty("ignorados", ids.size() - adicionados);
                        if (adicionados > 0) {
                            AuditoriaService.registrar(req, "Lote", "adicionar", String.valueOf(idLote),
                                    adicionados + " infrações incluídas");
                        }
                    }
                }

            } else if ("enviar".equals(acao)) {
                long idLote = Long.parseLong(req.getParameter("idLote"));
                int qtd = 0;
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT COUNT(*) FROM muralha.lote_infracao_item WHERE id_lote = ?")) {
                    ps.setLong(1, idLote);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) qtd = rs.getInt(1);
                    }
                }
                if (qtd == 0) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Lote sem infrações não pode ser marcado como enviado.");
                } else {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE muralha.lote_infracao SET status = 'ENVIADO', " +
                            "dt_envio = SYSDATETIME(), id_usuario_enviou = ? " +
                            "WHERE id = ? AND status = 'RASCUNHO'")) {
                        ps.setInt(1, idUsuario);
                        ps.setLong(2, idLote);
                        int rows = ps.executeUpdate();
                        result.addProperty("ok", rows > 0);
                        if (rows == 0) {
                            result.addProperty("erro", "Lote não encontrado ou já enviado");
                        } else {
                            AuditoriaService.registrar(req, "Lote", "enviar", String.valueOf(idLote),
                                    "Lote marcado como enviado (sem integração com o DETRAN)");
                        }
                    }
                }

            } else if ("cancelar".equals(acao)) {
                long idLote = Long.parseLong(req.getParameter("idLote"));
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE muralha.lote_infracao SET status = 'CANCELADO' " +
                        "WHERE id = ? AND status = 'RASCUNHO'")) {
                    ps.setLong(1, idLote);
                    int rows = ps.executeUpdate();
                    result.addProperty("ok", rows > 0);
                    if (rows == 0) {
                        result.addProperty("erro", "Lote não encontrado ou não pode ser cancelado");
                    } else {
                        AuditoriaService.registrar(req, "Lote", "cancelar", String.valueOf(idLote), "Lote cancelado");
                    }
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
        resp.getWriter().print(GSON.toJson(result));
    }
}
