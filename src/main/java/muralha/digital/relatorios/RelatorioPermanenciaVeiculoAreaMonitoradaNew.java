package muralha.digital.relatorios;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;

@WebServlet("/relatorio/RelatorioPermanenciaVeiculoAreaMonitoradaNew")
public class RelatorioPermanenciaVeiculoAreaMonitoradaNew extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RelatorioPermanenciaVeiculoAreaMonitoradaNew.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            String acao = request.getParameter("acao");
            logger.info("=== REQUISIÇÃO GET RECEBIDA ===");
            logger.info("Ação solicitada: " + acao);

            if ("getAreasMonitoradas".equals(acao)) {
                // Buscar lista de áreas monitoradas
                logger.info("Iniciando busca de áreas monitoradas...");
                String jsonAreas = buscarAreasMonitoradas();
                logger.info("JSON retornado: " + jsonAreas);
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonAreas);
                out.flush();
                return;
            }

            logger.warn("Ação inválida ou não reconhecida: " + acao);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"erro\":\"Ação inválida\"}");

        } catch (Exception e) {
            logger.error("Erro ao processar requisição GET", e);
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\":\"Erro interno do servidor: " + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }

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
            String areaMonitorada = request.getParameter("area_monitorada"); // Agora é o ID
            String placa = request.getParameter("placa");
            String dataInicio = request.getParameter("data_inicio");
            String dataFim = request.getParameter("data_fim");

            // Validar parâmetros obrigatórios
            if (dataInicio == null || dataFim == null || dataInicio.trim().isEmpty() || dataFim.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"Parâmetros data_inicio e data_fim são obrigatórios\"}");
                return;
            }

            // Gerar JSON com dados do relatório
            String jsonResult = gerarRelatorioPermanencia(areaMonitorada, placa, dataInicio, dataFim);

            logger.info("JSON gerado para relatório de permanência: "
                    + (jsonResult != null ? jsonResult.substring(0, Math.min(200, jsonResult.length())) + "..."
                            : "null"));

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResult);

        } catch (Exception e) {
            logger.error("Erro ao gerar relatório de permanência de veículos", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\":\"Erro interno do servidor: " + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }

    private String buscarAreasMonitoradas() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            logger.info("=== BUSCANDO ÁREAS MONITORADAS ===");
            conn = Conexao.getConexao();

            String sql = "SELECT id, nome FROM muralha.area_monitorada WHERE deletado = 0 ORDER BY nome";
            logger.info("SQL: " + sql);
            
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            StringBuilder json = new StringBuilder();
            json.append("[");

            int count = 0;
            while (rs.next()) {
                if (count > 0) {
                    json.append(",");
                }
                
                String id = rs.getString("id");
                String nome = rs.getString("nome");
                
                logger.info("Área encontrada - ID: " + id + ", Nome: " + nome);
                
                json.append("{");
                json.append("\"id\":\"").append(id).append("\",");
                json.append("\"nome\":\"").append(escapeJson(nome)).append("\"");
                json.append("}");
                count++;
            }

            json.append("]");

            logger.info("Total de áreas monitoradas carregadas: " + count);
            logger.info("JSON gerado: " + json.toString());

            return json.toString();

        } catch (Exception e) {
            logger.error("Erro ao buscar áreas monitoradas", e);
            e.printStackTrace();
            return "[]"; 
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
    }

    private String gerarRelatorioPermanencia(String areaMonitorada, String placa, String dataInicio, String dataFim) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        try {
            conn = Conexao.getConexao();

            logger.info("=== EXECUTANDO PROCEDURE DE PERMANÊNCIA ===");
            logger.info("Parâmetros:");
            logger.info("  @area_monitorada: " + (areaMonitorada != null && !areaMonitorada.trim().isEmpty() ? areaMonitorada : "NULL"));
            logger.info("  @placa: " + (placa != null && !placa.trim().isEmpty() ? placa : "NULL"));
            logger.info("  @data_ini: " + dataInicio);
            logger.info("  @data_fim: " + dataFim);

            // Preparar chamada da procedure
            String sql = "{call muralha.spu_RelatorioPermanenciaVeiculoAreaMonitoradaDetalhado(?, ?, ?, ?)}";
            stmt = conn.prepareCall(sql);

            // Definir parâmetros (NULL se vazio)
            if (areaMonitorada != null && !areaMonitorada.trim().isEmpty()) {
                stmt.setString(1, areaMonitorada.trim());
            } else {
                stmt.setNull(1, java.sql.Types.VARCHAR);
            }

            if (placa != null && !placa.trim().isEmpty()) {
                stmt.setString(2, placa.trim().toUpperCase());
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }

            stmt.setString(3, dataInicio);
            stmt.setString(4, dataFim);

            // Executar procedure
            boolean hasResultSet = stmt.execute();

            if (!hasResultSet) {
                logger.warn("Procedure não retornou ResultSet");
                return criarJsonVazio();
            }

            // Primeiro RecordSet: Dados Detalhados de Passagem
            rs = stmt.getResultSet();
            StringBuilder passagensJson = new StringBuilder();
            passagensJson.append("[");

            int countPassagens = 0;
            while (rs.next()) {
                if (countPassagens > 0) {
                    passagensJson.append(",");
                }

                passagensJson.append("{");
                passagensJson.append("\"placa\":\"").append(escapeJson(rs.getString("placa"))).append("\",");
                
                // Adicionar campo nome
                String nome = rs.getString("nome");
                if (nome != null && !nome.trim().isEmpty()) {
                    passagensJson.append("\"nome\":\"").append(escapeJson(nome)).append("\",");
                } else {
                    passagensJson.append("\"nome\":null,");
                }
                
                passagensJson.append("\"ocorrencia\":\"").append(escapeJson(rs.getString("ocorrencia"))).append("\",");
                passagensJson.append("\"data_entrada\":\"").append(rs.getString("data_entrada") != null ? rs.getString("data_entrada") : "").append("\",");
                
                // ID e Nome da área monitorada
                Object idArea = rs.getObject("id_area_monitorada");
                if (idArea != null) {
                    passagensJson.append("\"id_area_monitorada\":").append(idArea).append(",");
                } else {
                    passagensJson.append("\"id_area_monitorada\":null,");
                }
                
                // Tentar obter nome da área (pode vir como coluna 11 ou com alias específico)
                String nomeArea = null;
                try {
                    nomeArea = rs.getString(11); // Posição da coluna nome da área
                } catch (Exception e) {
                    nomeArea = null;
                }
                
                if (nomeArea != null && !nomeArea.trim().isEmpty()) {
                    passagensJson.append("\"nome_area_monitorada\":\"").append(escapeJson(nomeArea)).append("\",");
                } else {
                    passagensJson.append("\"nome_area_monitorada\":\"EQUIPAMENTO SEM ÁREA\",");
                }
                
                passagensJson.append("\"id_equipamento_entrada\":").append(rs.getInt("id_equipamento_entrada")).append(",");
                passagensJson.append("\"ocorrencia_saida\":\"").append(escapeJson(rs.getString("ocorrencia_saida"))).append("\",");
                passagensJson.append("\"data_saida\":\"").append(rs.getString("data_saida") != null ? rs.getString("data_saida") : "").append("\",");
                
                Object idEqSaida = rs.getObject("id_equipamento_saida");
                if (idEqSaida != null) {
                    passagensJson.append("\"id_equipamento_saida\":").append(idEqSaida).append(",");
                } else {
                    passagensJson.append("\"id_equipamento_saida\":null,");
                }
                
                passagensJson.append("\"tempo_permanencia\":\"").append(escapeJson(rs.getString("tempo_permanencia"))).append("\"");
                passagensJson.append("}");

                countPassagens++;
            }

            passagensJson.append("]");

            logger.info("Primeiro RecordSet processado: " + countPassagens + " passagens");

            // Segundo RecordSet: Dados Estatísticos
            if (!stmt.getMoreResults()) {
                logger.warn("Segundo RecordSet não encontrado");
                return montarJsonCompleto(passagensJson.toString(), "[]");
            }

            rs2 = stmt.getResultSet();
            StringBuilder estatisticasJson = new StringBuilder();
            estatisticasJson.append("[");

            int countEstatisticas = 0;
            while (rs2.next()) {
                if (countEstatisticas > 0) {
                    estatisticasJson.append(",");
                }

                estatisticasJson.append("{");
                estatisticasJson.append("\"id_area_monitorada\":").append(rs2.getInt("id_area_monitorada")).append(",");
                estatisticasJson.append("\"nome_area_monitorada\":\"").append(escapeJson(rs2.getString("nome_area_monitorada"))).append("\",");
                estatisticasJson.append("\"total_veiculos_distintos\":").append(rs2.getInt("total_veiculos_distintos")).append(",");
                estatisticasJson.append("\"total_permanencias\":").append(rs2.getInt("total_permanencias")).append(",");
                estatisticasJson.append("\"permanencias_sem_saida\":").append(rs2.getInt("permanencias_sem_saida")).append(",");
                estatisticasJson.append("\"permanencias_finalizadas\":").append(rs2.getInt("permanencias_finalizadas")).append(",");
                estatisticasJson.append("\"tempo_medio_permanencia\":\"").append(escapeJson(rs2.getString("tempo_medio_permanencia"))).append("\",");
                // Coluna tempo_minimo_permanencia foi removida pois não existe na procedure
                //estatisticasJson.append("\"tempo_maximo_permanencia\":\"").append(escapeJson(rs2.getString("tempo_maximo_permanencia"))).append("\",");
                estatisticasJson.append("\"tempo_total_acumulado\":\"").append(escapeJson(rs2.getString("tempo_total_acumulado"))).append("\",");
                estatisticasJson.append("\"tempo_mediano_permanencia\":\"").append(escapeJson(rs2.getString("tempo_mediano_permanencia"))).append("\",");
                estatisticasJson.append("\"primeira_entrada_periodo\":\"").append(rs2.getString("primeira_entrada_periodo") != null ? rs2.getString("primeira_entrada_periodo") : "").append("\",");
                estatisticasJson.append("\"ultima_saida_periodo\":\"").append(rs2.getString("ultima_saida_periodo") != null ? rs2.getString("ultima_saida_periodo") : "").append("\"");
                estatisticasJson.append("}");

                countEstatisticas++;
            }

            estatisticasJson.append("]");

            logger.info("Segundo RecordSet processado: " + countEstatisticas + " áreas");
            logger.info("✓ Relatório gerado com sucesso!");

            return montarJsonCompleto(passagensJson.toString(), estatisticasJson.toString());

        } catch (SQLException e) {
            logger.error("Erro SQL ao executar procedure de permanência", e);
            return criarJsonErro("Erro ao executar procedure: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro geral ao gerar relatório de permanência", e);
            return criarJsonErro("Erro ao gerar relatório: " + e.getMessage());
        } finally {
            // Fechar recursos
            try {
                if (rs != null)
                    rs.close();
                if (rs2 != null)
                    rs2.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                logger.error("Erro ao fechar recursos de banco", e);
            }
        }
    }

    private String montarJsonCompleto(String passagens, String estatisticas) {
        return "{"
                + "\"passagens\":" + passagens + ","
                + "\"estatisticas\":" + estatisticas
                + "}";
    }

    private String criarJsonVazio() {
        return "{"
                + "\"passagens\":[],"
                + "\"estatisticas\":[]"
                + "}";
    }

    private String criarJsonErro(String mensagem) {
        return "{"
                + "\"erro\":\"" + escapeJson(mensagem) + "\","
                + "\"passagens\":[],"
                + "\"estatisticas\":[]"
                + "}";
    }

    private String escapeJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
