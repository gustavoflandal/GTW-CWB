package muralha.digital.relatorios;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet para o dashboard de Contagem de Passagens por Local.
 * <p>
 * GET  – Retorna a lista de locais ativos com coordenadas (para inicialização do mapa).<br>
 * POST – Chama {@code muralha.spu_ContagemPassagensPorLocal} e retorna os dois ResultSets como JSON.
 * </p>
 */
@WebServlet("/relatorio/ContagemPassagensPorLocal")
public class ContagemPassagensPorLocalServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ContagemPassagensPorLocalServlet.class);

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // -------------------------------------------------------------------------
    // GET – locais disponíveis para o mapa
    // -------------------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final Acesso acesso = new Acesso(request, response, true);
        if (!acesso.verificaAcesso()) {
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(carregarLocais());
        out.flush();
    }

    // -------------------------------------------------------------------------
    // POST – consulta a procedure e monta o dashboard
    // -------------------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final Acesso acesso = new Acesso(request, response, true);
        if (!acesso.verificaAcesso()) {
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String dataIni = request.getParameter("dataIni");
        String dataFim = request.getParameter("dataFim");
        String lista   = request.getParameter("lista");

        PrintWriter out = response.getWriter();
        out.print(consultarPassagens(dataIni, dataFim, lista));
        out.flush();
    }

    // -------------------------------------------------------------------------
    // Carrega locais da tabela local_vigente
    // -------------------------------------------------------------------------
    private String carregarLocais() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();

            String sql = "SELECT id_local, nome, posicao_lat, posicao_lon "
                       + "FROM local_vigente "
                       + "WHERE posicao_lat IS NOT NULL AND posicao_lon IS NOT NULL AND desativado = 0 "
                       + "ORDER BY id_local ASC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            List<Map<String, Object>> locais = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> local = new HashMap<>();
                local.put("id_local",    rs.getInt("id_local"));
                local.put("nome",        rs.getString("nome"));
                local.put("latitude",    rs.getBigDecimal("posicao_lat"));
                local.put("longitude",   rs.getBigDecimal("posicao_lon"));
                locais.add(local);
            }

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("locais", locais);
            logger.info("Locais carregados para mapa: " + locais.size());
            return gson.toJson(resultado);

        } catch (Exception e) {
            logger.error("Erro ao carregar locais para mapa", e);
            return criarJsonErro("Erro ao carregar locais: " + e.getMessage());
        } finally {
            fecharRecursos(rs, ps, null, conn);
        }
    }

    // -------------------------------------------------------------------------
    // Chama a procedure e processa os dois ResultSets
    // -------------------------------------------------------------------------
    private String consultarPassagens(String dataIni, String dataFim, String lista) {
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            if (dataIni == null || dataIni.isEmpty() || dataFim == null || dataFim.isEmpty()) {
                return criarJsonErro("Os parâmetros dataIni e dataFim são obrigatórios.");
            }
            if (lista == null || lista.isEmpty()) {
                return criarJsonErro("Nenhum local selecionado. Clique nos marcadores do mapa para selecionar.");
            }

            conn = Conexao.getConexao();
            Map<String, Object> resultado = new HashMap<>();

            String sql = "{call muralha.spu_ContagemPassagensPorLocal(?, ?, ?)}";
            stmt = conn.prepareCall(sql);
            stmt.setString(1, dataIni);
            stmt.setString(2, dataFim);
            stmt.setString(3, lista);

            logger.info("Executando spu_ContagemPassagensPorLocal: dataIni=" + dataIni
                    + " | dataFim=" + dataFim + " | lista=" + lista);

            boolean hasRs = stmt.execute();

            // ResultSet 1 – passagens por local
            List<Map<String, Object>> passagensPorLocal = new ArrayList<>();
            if (hasRs) {
                rs = stmt.getResultSet();
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id_local",               rs.getInt("id_local"));
                    item.put("total_passagens",         rs.getLong("total_passagens"));
                    passagensPorLocal.add(item);
                }
                rs.close();
            }
            resultado.put("passagens_por_local", passagensPorLocal);
            logger.info("RS1 processado: " + passagensPorLocal.size() + " locais");

            // ResultSet 2 – placas em comum entre pares de locais
            List<Map<String, Object>> placasEmComum = new ArrayList<>();
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id_local_a",           rs.getInt("id_local_a"));
                    item.put("id_local_b",           rs.getInt("id_local_b"));
                    item.put("total_placas_em_ambos", getLongByPossibleNames(rs,
                            "total_placas_em_ambos", "total_passagens", "total"));
                    item.put("total_placas_distintas", getLongByPossibleNames(rs,
                            "total_placas_distintas", "placas_distintas", "total_distintas"));
                    placasEmComum.add(item);
                }
                rs.close();
            }
            resultado.put("placas_em_comum", placasEmComum);
            logger.info("RS2 processado: " + placasEmComum.size() + " pares");

            String json = gson.toJson(resultado);
            logger.info("JSON gerado com sucesso – tamanho: " + json.length());
            return json;

        } catch (SQLException e) {
            logger.error("Erro SQL ao executar spu_ContagemPassagensPorLocal", e);
            return criarJsonErro("Erro ao consultar banco de dados: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro geral ao consultar passagens por local", e);
            return criarJsonErro("Erro ao processar dados: " + e.getMessage());
        } finally {
            fecharRecursos(rs, null, stmt, conn);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    private String criarJsonErro(String mensagem) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", mensagem);
        return gson.toJson(erro);
    }

    private long getLongByPossibleNames(ResultSet rs, String... possibleNames) throws SQLException {
        String columnName = findExistingColumn(rs, possibleNames);
        if (columnName == null) {
            throw new SQLException("Nenhuma coluna esperada encontrada entre: " + String.join(", ", possibleNames));
        }
        long value = rs.getLong(columnName);
        return rs.wasNull() ? 0L : value;
    }

    private String findExistingColumn(ResultSet rs, String... possibleNames) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        for (String expected : possibleNames) {
            for (int i = 1; i <= columnCount; i++) {
                String label = meta.getColumnLabel(i);
                String name = meta.getColumnName(i);
                if (expected.equalsIgnoreCase(label) || expected.equalsIgnoreCase(name)) {
                    return label;
                }
            }
        }
        return null;
    }

    private void fecharRecursos(ResultSet rs, PreparedStatement ps,
                                 CallableStatement cs, Connection conn) {
        try { if (rs  != null) rs.close();  } catch (SQLException ignored) {}
        try { if (ps  != null) ps.close();  } catch (SQLException ignored) {}
        try { if (cs  != null) cs.close();  } catch (SQLException ignored) {}
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
