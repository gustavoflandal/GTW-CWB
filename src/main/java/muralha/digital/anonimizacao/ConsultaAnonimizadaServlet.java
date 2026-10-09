package muralha.digital.anonimizacao;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/MuralhaDigital/ConsultaAnonimizada")
public class ConsultaAnonimizadaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();

        String dtInicio = req.getParameter("dtInicio");
        String dtFim = req.getParameter("dtFim");
        String idLocal = req.getParameter("idLocal");
        String anonimizar = req.getParameter("anonimizar");

        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            boolean usarAnonimizacao = !"0".equals(anonimizar);
            if (!"0".equals(anonimizar) && !"1".equals(anonimizar)) {
                usarAnonimizacao = obterConfigPadrao(conn);
            }

            String fonte = usarAnonimizacao
                ? "muralha.vw_passagem_anonimizada"
                : "muralha.veiculo_tempo_real vtr " +
                  "LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = vtr.id";

            String selectCols;
            if (usarAnonimizacao) {
                selectCols = "v.id, v.placa, v.id_local, v.id_pista, " +
                    "CONVERT(VARCHAR(19), v.data, 120) AS dt_captura, " +
                    "CONVERT(VARCHAR(19), v.data_importado, 120) AS dt_recepcao, " +
                    "v.status_analise";
                fonte = "muralha.vw_passagem_anonimizada v";
            } else {
                selectCols = "vtr.id, vtr.placa, vtr.id_local, vtr.id_pista, " +
                    "CONVERT(VARCHAR(19), vtr.data, 120) AS dt_captura, " +
                    "CONVERT(VARCHAR(19), vtr.data_importado, 120) AS dt_recepcao, " +
                    "COALESCE(vsa.status_analise, 'AGUARDANDO_ANALISE') AS status_analise";
                fonte = "muralha.veiculo_tempo_real vtr WITH (NOLOCK) " +
                    "LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = vtr.id";
            }

            List<String> params = new ArrayList<>();
            StringBuilder where = new StringBuilder(" WHERE 1=1");
            String alias = usarAnonimizacao ? "v" : "vtr";

            if (dtInicio != null && !dtInicio.isEmpty()) {
                where.append(" AND CAST(").append(alias).append(".data AS DATE) >= ?");
                params.add(dtInicio);
            }
            if (dtFim != null && !dtFim.isEmpty()) {
                where.append(" AND CAST(").append(alias).append(".data AS DATE) <= ?");
                params.add(dtFim);
            }
            if (idLocal != null && !idLocal.isEmpty()) {
                where.append(" AND ").append(alias).append(".id_local = ?");
                params.add(idLocal);
            }

            String sql = "SELECT TOP 5000 " + selectCols + " FROM " + fonte +
                where.toString() + " ORDER BY " + alias + ".data DESC";

            JsonArray rows = new JsonArray();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setString(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject row = new JsonObject();
                        row.addProperty("id", rs.getString("id"));
                        row.addProperty("placa", rs.getString("placa"));
                        row.addProperty("idLocal", rs.getInt("id_local"));
                        row.addProperty("idPista", rs.getInt("id_pista"));
                        row.addProperty("dtCaptura", rs.getString("dt_captura"));
                        row.addProperty("dtRecepcao", rs.getString("dt_recepcao"));
                        row.addProperty("status", rs.getString("status_analise"));
                        rows.add(row);
                    }
                }
            }

            result.add("rows", rows);
            result.addProperty("total", rows.size());
            result.addProperty("anonimizado", usarAnonimizacao);
            result.addProperty("ok", true);

        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    private boolean obterConfigPadrao(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT valor FROM muralha.config_chave_valor WHERE chave = 'anonimizar_placa_padrao'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return "1".equals(rs.getString("valor"));
        } catch (Exception ignored) {}
        return true;
    }
}
