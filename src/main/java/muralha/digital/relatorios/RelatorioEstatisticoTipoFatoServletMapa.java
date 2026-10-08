package muralha.digital.relatorios;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

@WebServlet("/relatorio/RelatorioEstatisticoTipoFatoMapa")
public class RelatorioEstatisticoTipoFatoServletMapa extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RelatorioEstatisticoTipoFatoServletMapa.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
            String dataFim = request.getParameter("dataFim");

            if (dataInicio == null || dataFim == null || dataInicio.trim().isEmpty() || dataFim.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"Parâmetros dataInicio e dataFim são obrigatórios\"}");
                return;
            }

            logger.info("Gerando relatório estatístico com mapa - Início: " + dataInicio + ", Fim: " + dataFim);

            // Gerar JSON com dados estatísticos
            String jsonResult = gerarEstatisticasTipoFatosMapa(dataInicio, dataFim);

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResult);

        } catch (Exception e) {
            logger.error("Erro ao gerar relatório estatístico por tipo de fatos com mapa", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\":\"Erro interno do servidor: " + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }

    private String gerarEstatisticasTipoFatosMapa(String dataInicio, String dataFim) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();

            logger.info("Executando procedure muralha.spu_RelatorioEstatisticoPorTipoFatoMapaNew - Início: " 
                    + dataInicio + ", Fim: " + dataFim);

            // Preparar chamada da procedure
            String sql = "{call muralha.spu_RelatorioEstatisticoPorTipoFatoMapaNew(?, ?)}";
            stmt = conn.prepareCall(sql);

            // Definir parâmetros
            stmt.setString(1, dataInicio);
            stmt.setString(2, dataFim);

            // Executar procedure
            boolean hasResultSet = stmt.execute();

            // Estrutura de resposta
            Map<String, Object> resultado = new HashMap<>();

            // ResultSet 1: OCORRÊNCIAS POR TIPO COM GEOLOCALIZAÇÃO ÚNICA
            if (hasResultSet) {
                rs = stmt.getResultSet();
                
                // Estruturas para agrupar dados
                Map<Integer, Map<String, Object>> tipoEventoMap = new HashMap<>();
                List<Map<String, Object>> detalhesGrid = new ArrayList<>();

                logger.info("Processando ResultSet 1: Ocorrências por tipo com geolocalização");

                while (rs.next()) {
                    int idTipo = rs.getInt("id_tipo");
                    String nomeTipoEvento = rs.getString("nome_tipo_evento");
                    int totalOcorrencias = rs.getInt("total_ocorrencias");
                    double latitude = rs.getDouble("latitude");
                    double longitude = rs.getDouble("longitude");
                    String rua = rs.getString("rua");
                    Integer numero = rs.getObject("numero") != null ? rs.getInt("numero") : null;
                    String bairro = rs.getString("bairro");

                    // Validar coordenadas
                    if (latitude == 0 && longitude == 0) {
                        logger.debug("Coordenadas zeradas ignoradas: idTipo=" + idTipo);
                        continue;
                    }

                    // Agrupar por tipo para o array ocorrencias_por_tipo
                    if (!tipoEventoMap.containsKey(idTipo)) {
                        Map<String, Object> tipoData = new HashMap<>();
                        tipoData.put("id_tipo_evento", idTipo);
                        tipoData.put("nome_tipo_evento", nomeTipoEvento);
                        tipoData.put("total_ocorrencias", 0);
                        tipoData.put("geolocalizacao", new ArrayList<Map<String, Object>>());
                        tipoEventoMap.put(idTipo, tipoData);
                    }

                    Map<String, Object> tipoData = tipoEventoMap.get(idTipo);
                    
                    // Incrementar total - cada linha do ResultSet é um fato único
                    int totalAtual = (int) tipoData.get("total_ocorrencias");
                    tipoData.put("total_ocorrencias", totalAtual + 1);

                    // Adicionar ponto de geolocalização
                    Map<String, Object> ponto = new HashMap<>();
                    ponto.put("latitude", latitude);
                    ponto.put("longitude", longitude);
                    ponto.put("rua", rua != null ? rua : "");
                    ponto.put("numero", numero);
                    ponto.put("bairro", bairro != null ? bairro : "");
                    
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> geoList = (List<Map<String, Object>>) tipoData.get("geolocalizacao");
                    geoList.add(ponto);

                    // Adicionar ao grid de detalhes
                    Map<String, Object> detalhe = new HashMap<>();
                    detalhe.put("nome_tipo_evento", nomeTipoEvento);
                    detalhe.put("rua", rua != null ? rua : "");
                    detalhe.put("numero", numero);
                    detalhe.put("bairro", bairro != null ? bairro : "");
                    detalhe.put("latitude", latitude);
                    detalhe.put("longitude", longitude);
                    detalhesGrid.add(detalhe);
                }

                resultado.put("ocorrencias_por_tipo", new ArrayList<>(tipoEventoMap.values()));
                resultado.put("detalhes_ocorrencias", detalhesGrid);

                logger.info("ResultSet 1 processado: " + tipoEventoMap.size() + " tipos de eventos, " 
                        + detalhesGrid.size() + " detalhes");
            }

            // ResultSet 2: HISTOGRAMA SEMANAL
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
                List<Map<String, Object>> histogramaSemanal = new ArrayList<>();

                logger.info("Processando ResultSet 2: Histograma semanal");

                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("semana", rs.getInt("semana"));
                    item.put("total_ocorrencias", rs.getInt("total_ocorrencias"));
                    histogramaSemanal.add(item);
                }

                resultado.put("histograma_semanal", histogramaSemanal);
                logger.info("ResultSet 2 processado: " + histogramaSemanal.size() + " semanas");
            }

            // ResultSet 3: HISTOGRAMA DIÁRIO (DIA DA SEMANA)
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
                List<Map<String, Object>> histogramaDiario = new ArrayList<>();

                logger.info("Processando ResultSet 3: Histograma diário");

                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    // Usar dia_semana_0a6 (0=Dom, 6=Sáb) para compatibilidade com JS
                    item.put("dia_semana", rs.getInt("dia_semana_0a6"));
                    item.put("total_ocorrencias", rs.getInt("total_ocorrencias"));
                    histogramaDiario.add(item);
                }

                resultado.put("histograma_diario", histogramaDiario);
                logger.info("ResultSet 3 processado: " + histogramaDiario.size() + " dias");
            }

            // ResultSet 4: HISTOGRAMA POR HORA
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
                List<Map<String, Object>> histogramaHora = new ArrayList<>();

                logger.info("Processando ResultSet 4: Histograma por hora");

                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("hora", rs.getInt("hora"));
                    item.put("total_ocorrencias", rs.getInt("total_ocorrencias"));
                    histogramaHora.add(item);
                }

                resultado.put("histograma_por_hora", histogramaHora);
                logger.info("ResultSet 4 processado: " + histogramaHora.size() + " horas");
            }

            // ResultSet 5: TIPOS DE EVENTOS (PARA MAPEAMENTO/LEGENDA)
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
                List<Map<String, Object>> tiposEventos = new ArrayList<>();

                logger.info("Processando ResultSet 5: Tipos de eventos");

                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getInt("id"));
                    item.put("tipo_evento", rs.getString("tipo_evento"));
                    tiposEventos.add(item);
                }

                resultado.put("tipo_eventos", tiposEventos);
                logger.info("ResultSet 5 processado: " + tiposEventos.size() + " tipos");
            }

            String jsonResult = gson.toJson(resultado);
            logger.info("JSON gerado com sucesso - Tamanho: " + jsonResult.length() + " caracteres");

            return jsonResult;

        } catch (SQLException e) {
            logger.error("Erro SQL ao executar procedure de estatísticas por tipo de fatos com mapa", e);
            return criarJsonErro("Erro ao consultar banco de dados: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro geral ao gerar estatísticas por tipo de fatos com mapa", e);
            return criarJsonErro("Erro ao processar dados: " + e.getMessage());
        } finally {
            // Fechar recursos
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                logger.error("Erro ao fechar recursos de banco", e);
            }
        }
    }

    private String criarJsonErro(String mensagem) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", mensagem);
        return gson.toJson(erro);
    }
}
