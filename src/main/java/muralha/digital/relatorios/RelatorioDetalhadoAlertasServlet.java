/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW - Relatório Detalhado de Alertas
  
  Empresa: Consilux Tecnologia
  
  Autor: Sistema
  Data: 14/10/2025

*********************************************************************************/

package muralha.digital.relatorios;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

@WebServlet("/muralha-digital/relatorio/RelatorioDetalhadoAlertas")
public class RelatorioDetalhadoAlertasServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RelatorioDetalhadoAlertasServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            // Verificar acesso
            Acesso acesso = new Acesso(request, response, true);
            if (!acesso.verificaAcesso()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"erro\":\"Acesso não autorizado\"}");
                return;
            }

            // Obter parâmetros
            String dataInicio = request.getParameter("dataInicio");
            String dataFinal = request.getParameter("dataFinal");

            // Validar parâmetros
            if (dataInicio == null || dataFinal == null ||
                    dataInicio.trim().isEmpty() || dataFinal.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"Parâmetros dataInicio e dataFinal são obrigatórios\"}");
                return;
            }

            // Gerar JSON com dados das irregularidades
            String jsonResult = gerarRelatorioDetalhadoAlertas(dataInicio, dataFinal);

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResult);

        } catch (Exception e) {
            logger.error("Erro ao gerar relatório detalhado de alertas", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\":\"Erro interno do servidor: " + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }

    private String gerarRelatorioDetalhadoAlertas(String dataInicio, String dataFinal) {
        Connection conn = null;
        CallableStatement stmt = null;

        try {
            conn = Conexao.getConexao();

            // Preparar chamada da procedure
            String sql = "{call muralha.spu_RelatorioIrregularidadesDashboard(?, ?)}";
            stmt = conn.prepareCall(sql);

            // Converter datas para o formato SQL Server (yyyy-MM-dd)
            Date sqlDataInicio = Date.valueOf(dataInicio);
            Date sqlDataFinal = Date.valueOf(dataFinal);

            // Definir parâmetros
            stmt.setDate(1, sqlDataInicio);
            stmt.setDate(2, sqlDataFinal);

            // Executar procedure e processar os recordset
            boolean hasResults = stmt.execute();

            Map<String, Object> resultado = new HashMap<>();
            int recordsetCount = 0;

            while (hasResults) {
                ResultSet rs = stmt.getResultSet();

                switch (recordsetCount) {
                    case 0: // RESULT SET 1: Listagem detalhada de irregularidades
                        resultado.put("irregularidadesDetalhadas", processarIrregularidadesDetalhadas(rs));
                        break;
                    case 1: // RESULT SET 2: Contagem por tipo
                        resultado.put("contagemPorTipo", processarContagemPorTipo(rs));
                        break;
                    case 2: // RESULT SET 3: Contagem por leitura
                        resultado.put("contagemPorLeitura", processarContagemPorLeitura(rs));
                        break;
                    case 3: // RESULT SET 4: Contagem por situação
                        resultado.put("contagemPorSituacao", processarContagemPorSituacao(rs));
                        break;
                    case 4: // RESULT SET 5: Status de finalização
                        resultado.put("statusFinalizacao", processarStatusFinalizacao(rs));
                        break;
                }

                rs.close();
                recordsetCount++;
                hasResults = stmt.getMoreResults();
            }

            // Adicionar informações da consulta
            resultado.put("parametrosConsulta", criarInfoParametros(dataInicio, dataFinal));

            // Converter para JSON
            Gson gson = new Gson();
            return gson.toJson(resultado);

        } catch (SQLException e) {
            logger.error("Erro SQL ao executar procedure de irregularidades", e);
            return criarJsonErro("Erro ao executar consulta no banco de dados: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro geral ao gerar relatório detalhado de alertas", e);
            return criarJsonErro("Erro ao processar dados: " + e.getMessage());
        } finally {
            // Fechar recursos
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                logger.error("Erro ao fechar recursos de banco", e);
            }
        }
    }

    private List<Map<String, Object>> processarIrregularidadesDetalhadas(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("data", formatarData(rs.getString("data")));
            registro.put("tipo", rs.getString("tipo"));
            registro.put("equipamento", rs.getString("equipamento"));
            registro.put("placaCad", rs.getString("placa_cad"));
            registro.put("placaCap", rs.getString("placa_cap"));
            registro.put("leitura", rs.getString("leitura"));
            registro.put("inicio", formatarData(rs.getString("inicio")));

            // Coluna fim pode vir como varchar, então precisa formatar
            String fimValue = rs.getString("fim");
            if (fimValue != null && !fimValue.trim().isEmpty() && !fimValue.equalsIgnoreCase("NULL")) {
                registro.put("fim", formatarData(fimValue));
            } else {
                registro.put("fim", "");
            }

            registro.put("situacao", rs.getString("situacao"));
            dados.add(registro);
        }

        return dados;
    }

    private List<Map<String, Object>> processarContagemPorTipo(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("tipo", rs.getString("tipo"));
            registro.put("total", rs.getInt("Total"));
            dados.add(registro);
        }

        return dados;
    }

    private List<Map<String, Object>> processarContagemPorLeitura(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("leitura", rs.getString("leitura"));
            registro.put("total", rs.getInt("Total"));
            dados.add(registro);
        }

        return dados;
    }

    private List<Map<String, Object>> processarContagemPorSituacao(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("situacao", rs.getString("situacao"));
            registro.put("total", rs.getInt("Total"));
            dados.add(registro);
        }

        return dados;
    }

    private List<Map<String, Object>> processarStatusFinalizacao(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("statusFinalizacao", rs.getString("StatusFinalizacao"));
            registro.put("total", rs.getInt("Total"));
            dados.add(registro);
        }

        return dados;
    }

    private Map<String, Object> criarInfoParametros(String dataInicio, String dataFinal) {
        Map<String, Object> info = new HashMap<>();

        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy");

            java.util.Date dataIni = formatoEntrada.parse(dataInicio);
            java.util.Date dataFim = formatoEntrada.parse(dataFinal);

            info.put("dataInicio", formatoSaida.format(dataIni));
            info.put("dataFinal", formatoSaida.format(dataFim));
            info.put("dataInicioSQL", dataInicio);
            info.put("dataFinalSQL", dataFinal);

        } catch (Exception e) {
            logger.error("Erro ao formatar datas", e);
            info.put("dataInicio", dataInicio);
            info.put("dataFinal", dataFinal);
            info.put("dataInicioSQL", dataInicio);
            info.put("dataFinalSQL", dataFinal);
        }

        return info;
    }

    private String formatarData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return "";
        }

        try {
            // Se a data já está no formato dd/MM/yyyy, retorna como está
            if (data.matches("\\d{2}/\\d{2}/\\d{4}.*")) {
                return data;
            }

            // Se está no formato yyyy-MM-dd, converte para dd/MM/yyyy
            if (data.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
                SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                java.util.Date date = formatoEntrada.parse(data);
                return formatoSaida.format(date);
            }

            return data;
        } catch (Exception e) {
            logger.error("Erro ao formatar data: " + data, e);
            return data;
        }
    }

    private String criarJsonErro(String mensagem) {
        return "{\"erro\":\"" + mensagem.replace("\"", "\\\"") + "\"}";
    }
}
