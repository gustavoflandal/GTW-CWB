package muralha.digital.relatorios;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
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
 * Servlet para consultar passagens sequenciais.
 * <p>
 * POST – Chama {@code muralha.spu_PassagensSequenciais} e retorna rotas,
 * total_passagens e placas_distintas.
 * </p>
 */
@WebServlet("/relatorio/PassagensSequenciais")
public class PassagensSequenciaisServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(PassagensSequenciaisServlet.class);

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // -------------------------------------------------------------------------
    // POST – consulta a procedure spu_PassagensSequenciais
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
        out.print(consultarPassagensSequenciais(dataIni, dataFim, lista));
        out.flush();
    }

    // -------------------------------------------------------------------------
    // Chama a procedure e processa o ResultSet
    // -------------------------------------------------------------------------
    private String consultarPassagensSequenciais(String dataIni, String dataFim, String lista) {
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            if (dataIni == null || dataIni.isEmpty() || dataFim == null || dataFim.isEmpty()) {
                return criarJsonErro("Os parâmetros dataIni e dataFim são obrigatórios.");
            }
            if (lista == null || lista.isEmpty()) {
                return criarJsonErro("Nenhum local selecionado.");
            }

            conn = Conexao.getConexao();
            Map<String, Object> resultado = new HashMap<>();

            String sql = "{call muralha.spu_PassagensSequenciais(?, ?, ?)}";
            stmt = conn.prepareCall(sql);
            stmt.setString(1, dataIni);
            stmt.setString(2, dataFim);
            stmt.setString(3, lista);

            logger.info("Executando spu_PassagensSequenciais: dataIni=" + dataIni
                    + " | dataFim=" + dataFim + " | lista=" + lista);

            boolean hasRs = stmt.execute();

            if (hasRs) {
                rs = stmt.getResultSet();
                if (rs.next()) {
                    resultado.put("rotas", getStringByPossibleNames(rs, lista,
                        "rotas", "sequencia_locais", "rota", "rota_completa", "caminho"));
                    resultado.put("total_passagens", getLongByPossibleNames(rs, 0L,
                        "total_passagens", "total", "qtd_passagens"));
                    resultado.put("placas_distintas", getLongByPossibleNames(rs, 0L,
                        "placas_distintas", "total_placas_distintas", "total_distintas", "qtd_placas_distintas"));
                    logger.info("Resultado: total_passagens=" + resultado.get("total_passagens"));
                } else {
                    resultado.put("rotas", lista);
                    resultado.put("total_passagens", 0);
                    resultado.put("placas_distintas", 0);
                }
                rs.close();
            } else {
                resultado.put("rotas", lista);
                resultado.put("total_passagens", 0);
                resultado.put("placas_distintas", 0);
            }

            String json = gson.toJson(resultado);
            logger.info("JSON gerado com sucesso – tamanho: " + json.length());
            return json;

        } catch (SQLException e) {
            logger.error("Erro SQL ao executar spu_PassagensSequenciais", e);
            return criarJsonErro("Erro ao consultar banco de dados: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro geral ao consultar passagens sequenciais", e);
            return criarJsonErro("Erro ao processar dados: " + e.getMessage());
        } finally {
            fecharRecursos(rs, stmt, conn);
        }
    }

    // -------------------------------------------------------------------------
    // Utilitários
    // -------------------------------------------------------------------------
    private String criarJsonErro(String mensagem) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", mensagem);
        return gson.toJson(erro);
    }

    private long getLongByPossibleNames(ResultSet rs, long defaultValue, String... possibleNames) throws SQLException {
        String columnName = findExistingColumn(rs, possibleNames);
        if (columnName == null) {
            logger.warn("Nenhuma coluna encontrada entre: " + String.join(", ", possibleNames)
                    + ". Assumindo valor padrão " + defaultValue + '.');
            return defaultValue;
        }
        long value = rs.getLong(columnName);
        return rs.wasNull() ? 0L : value;
    }

    private String getStringByPossibleNames(ResultSet rs, String defaultValue, String... possibleNames) throws SQLException {
        String columnName = findExistingColumn(rs, possibleNames);
        if (columnName == null) {
            logger.warn("Nenhuma coluna encontrada entre: " + String.join(", ", possibleNames)
                    + ". Assumindo valor padrão.");
            return defaultValue;
        }
        String value = rs.getString(columnName);
        return value == null ? defaultValue : value;
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

    private void fecharRecursos(ResultSet rs, CallableStatement stmt, Connection conn) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { logger.warn("Erro ao fechar ResultSet", e); }
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException e) { logger.warn("Erro ao fechar Statement", e); }
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { logger.warn("Erro ao fechar Connection", e); }
        }
    }
}
