/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW - Relatório de Fluxo Veicular por Rota
  
  Empresa: Consilux Tecnologia
  
  Autor: Sistema
  Data: 01/10/2025

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

@WebServlet("/muralha-digital/relatorio/RelatorioFluxoVeicularRota")
public class RelatorioFluxoVeicularRotaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RelatorioFluxoVeicularRotaServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Verificar acesso
            Acesso acesso = new Acesso(request, response, true);
            if (!acesso.verificaAcesso()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"erro\":\"Acesso não autorizado\"}");
                return;
            }

            String acao = request.getParameter("acao");

            if ("carregarLocais".equals(acao)) {
                carregarLocais(out);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"Ação não especificada ou inválida\"}");
            }

        } catch (Exception e) {
            logger.error("Erro ao processar requisição GET", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\":\"Erro ao processar requisição: " + e.getMessage() + "\"}");
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
            String dataInicio = request.getParameter("dataInicio");
            String dataFinal = request.getParameter("dataFinal");
            String idOrigemStr = request.getParameter("idOrigem");
            String idDestinoStr = request.getParameter("idDestino");

            // Validar parâmetros
            if (dataInicio == null || dataFinal == null || idOrigemStr == null || idDestinoStr == null ||
                    dataInicio.trim().isEmpty() || dataFinal.trim().isEmpty() || idOrigemStr.trim().isEmpty()
                    || idDestinoStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"Parâmetros dataInicio, dataFinal, idOrigem e idDestino são obrigatórios\"}");
                return;
            }

            int idOrigem;
            int idDestino;

            try {
                idOrigem = Integer.parseInt(idOrigemStr);
                idDestino = Integer.parseInt(idDestinoStr);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"erro\":\"Os parâmetros idOrigem e idDestino devem ser números inteiros\"}");
                return;
            }

            // Gerar JSON com dados do fluxo veicular
            String jsonResult = gerarRelatorioFluxoVeicularRota(dataInicio, dataFinal, idOrigem, idDestino);

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResult);

        } catch (Exception e) {
            logger.error("Erro ao gerar relatório de fluxo veicular por rota", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\":\"Erro interno do servidor: " + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }

    private String gerarRelatorioFluxoVeicularRota(String dataInicio, String dataFinal, int idOrigem, int idDestino) {
        Connection conn = null;
        CallableStatement stmt = null;

        try {
            conn = Conexao.getConexao();

            // Preparar chamada da procedure
            String sql = "{call muralha.spu_RelatorioFluxoVeicularRota(?, ?, ?, ?)}";
            stmt = conn.prepareCall(sql);

            // Converter datas para o formato SQL Server (yyyy-MM-dd)
            Date sqlDataInicio = Date.valueOf(dataInicio);
            Date sqlDataFinal = Date.valueOf(dataFinal);

            // Definir parâmetros
            stmt.setDate(1, sqlDataInicio);
            stmt.setDate(2, sqlDataFinal);
            stmt.setInt(3, idOrigem);
            stmt.setInt(4, idDestino);

            // Executar procedure e processar os 7 recordsets
            boolean hasResults = stmt.execute();

            Map<String, Object> resultado = new HashMap<>();
            int recordsetCount = 0;

            while (hasResults) {
                ResultSet rs = stmt.getResultSet();

                switch (recordsetCount) {
                    case 0: // RESULT SET 1: Resumo Geral
                        resultado.put("resumoGeral", processarResumoGeral(rs));
                        break;
                    case 1: // RESULT SET 2: Dados por Período do Dia
                        resultado.put("dadosPorPeriodo", processarDadosPorPeriodo(rs));
                        break;
                    case 2: // RESULT SET 3: Dados Horários
                        resultado.put("dadosHorarios", processarDadosHorarios(rs));
                        break;
                    case 3: // RESULT SET 4: Top 10 Placas
                        resultado.put("topPlacas", processarTopPlacas(rs));
                        break;
                    case 4: // RESULT SET 5: Distribuição de Tempo de Trânsito
                        resultado.put("distribuicaoTempo", processarDistribuicaoTempo(rs));
                        break;
                    case 5: // RESULT SET 6: Listagem Detalhada
                        resultado.put("passagensDetalhadas", processarPassagensDetalhadas(rs));
                        break;
                    case 6: // RESULT SET 7: Comparativo de Períodos
                        resultado.put("comparativoPeriodos", processarComparativoPeriodos(rs));
                        break;
                }

                rs.close();
                recordsetCount++;
                hasResults = stmt.getMoreResults();
            }

            // Adicionar informações da consulta
            resultado.put("parametrosConsulta", criarInfoParametros(dataInicio, dataFinal, idOrigem, idDestino));

            // Converter para JSON
            Gson gson = new Gson();
            return gson.toJson(resultado);

        } catch (SQLException e) {
            logger.error("Erro SQL ao executar procedure de fluxo veicular por rota", e);
            return criarJsonErro("Erro ao executar consulta no banco de dados: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro geral ao gerar relatório de fluxo veicular por rota", e);
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

    private Map<String, Object> processarResumoGeral(ResultSet rs) throws SQLException {
        Map<String, Object> resumo = new HashMap<>();

        if (rs.next()) {
            resumo.put("totalPassagens", rs.getInt("total_passagens"));
            resumo.put("totalVeiculosUnicos", rs.getInt("total_veiculos_unicos"));
            resumo.put("tempoMedioMinutos", rs.getDouble("tempo_medio_minutos"));
            resumo.put("tempoMinimoMinutos", rs.getInt("tempo_minimo_minutos"));
            resumo.put("tempoMaximoMinutos", rs.getInt("tempo_maximo_minutos"));
            resumo.put("desvioPadrao", rs.getDouble("desvio_padrao"));
            resumo.put("idLocalOrigem", rs.getInt("id_local_origem"));
            resumo.put("idLocalDestino", rs.getInt("id_local_destino"));
            // Remover referência a data_referencia que não existe mais
            // resumo.put("dataReferencia", rs.getDate("data_referencia"));
        }

        return resumo;
    }

    private List<Map<String, Object>> processarDadosPorPeriodo(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("periodoDia", rs.getString("periodo_dia"));
            registro.put("ordemPeriodo", rs.getInt("ordem_periodo"));
            registro.put("totalPassagens", rs.getInt("total_passagens"));
            registro.put("veiculosUnicos", rs.getInt("veiculos_unicos"));
            registro.put("tempoMedioMinutos", rs.getDouble("tempo_medio_minutos"));
            registro.put("tempoMinimoMinutos", rs.getInt("tempo_minimo_minutos"));
            registro.put("tempoMaximoMinutos", rs.getInt("tempo_maximo_minutos"));
            registro.put("percentualDoTotal", rs.getDouble("percentual_do_total"));
            dados.add(registro);
        }

        return dados;
    }

    private List<Map<String, Object>> processarDadosHorarios(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("hora", rs.getInt("hora"));
            registro.put("totalPassagens", rs.getInt("total_passagens"));
            registro.put("tempoMedioMinutos", rs.getDouble("tempo_medio_minutos"));
            registro.put("periodoDia", rs.getString("periodo_dia"));
            dados.add(registro);
        }

        return dados;
    }

    private List<Map<String, Object>> processarTopPlacas(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("placa", rs.getString("placa"));
            registro.put("totalPassagens", rs.getInt("total_passagens"));
            registro.put("tempoMedioMinutos", rs.getDouble("tempo_medio_minutos"));
            registro.put("tempoMinimoMinutos", rs.getInt("tempo_minimo_minutos"));
            registro.put("tempoMaximoMinutos", rs.getInt("tempo_maximo_minutos"));
            registro.put("primeiraPassagem", rs.getString("primeira_passagem"));
            registro.put("ultimaPassagem", rs.getString("ultima_passagem"));
            dados.add(registro);
        }

        return dados;
    }

    private List<Map<String, Object>> processarDistribuicaoTempo(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("faixaTempo", rs.getString("faixa_tempo"));
            registro.put("ordemFaixa", rs.getInt("ordem_faixa"));
            registro.put("totalPassagens", rs.getInt("total_passagens"));
            registro.put("percentual", rs.getDouble("percentual"));
            registro.put("tempoMedioFaixa", rs.getDouble("tempo_medio_faixa"));
            dados.add(registro);
        }

        return dados;
    }

    private List<Map<String, Object>> processarPassagensDetalhadas(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();

        // Limitar a 200 registros para não sobrecarregar o JSON
        int count = 0;
        while (rs.next() && count < 200) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("data", rs.getDate("data"));
            registro.put("placa", rs.getString("placa"));
            registro.put("idLocalOrigem", rs.getInt("id_local_origem"));
            registro.put("idLocalDestino", rs.getInt("id_local_destino"));
            registro.put("origem", rs.getString("origem"));
            registro.put("destino", rs.getString("destino"));
            registro.put("tempoTransitoMinutos", rs.getInt("tempo_tansito_minutos"));
            registro.put("periodoDia", rs.getString("periodo_dia"));
            registro.put("hora", rs.getInt("hora"));
            dados.add(registro);
            count++;
        }

        return dados;
    }

    private Map<String, Object> processarComparativoPeriodos(ResultSet rs) throws SQLException {
        Map<String, Object> comparativo = new HashMap<>();

        if (rs.next()) {
            comparativo.put("tipoResultado", rs.getString("tipo_resultado"));
            comparativo.put("passagensManha", rs.getInt("passagens_manha"));
            comparativo.put("passagensTarde", rs.getInt("passagens_tarde"));
            comparativo.put("passagensNoite", rs.getInt("passagens_noite"));
            comparativo.put("passagensMadrugada", rs.getInt("passagens_madrugada"));
            comparativo.put("tempoMedioManha", rs.getDouble("tempo_medio_manha"));
            comparativo.put("tempoMedioTarde", rs.getDouble("tempo_medio_tarde"));
            comparativo.put("tempoMedioNoite", rs.getDouble("tempo_medio_noite"));
            comparativo.put("tempoMedioMadrugada", rs.getDouble("tempo_medio_madrugada"));
        }

        return comparativo;
    }

    private Map<String, Object> criarInfoParametros(String dataInicio, String dataFinal, int idOrigem, int idDestino) {
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
            info.put("idLocalOrigem", idOrigem);
            info.put("idLocalDestino", idDestino);

        } catch (Exception e) {
            logger.error("Erro ao formatar datas", e);
            info.put("dataInicio", dataInicio);
            info.put("dataFinal", dataFinal);
            info.put("dataInicioSQL", dataInicio);
            info.put("dataFinalSQL", dataFinal);
            info.put("idLocalOrigem", idOrigem);
            info.put("idLocalDestino", idDestino);
        }

        return info;
    }

    private void carregarLocais(PrintWriter out) throws Exception {
        logger.info("=== CARREGANDO LOCAIS ===");
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();
            logger.info("Conexão estabelecida com sucesso");

            // Query SQL com DISTINCT no nome da tabela local conforme solicitado
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT DISTINCT ");
            sql.append("    l.id_local as id, ");
            sql.append("    l.nome ");
            sql.append("FROM dbo.local l ");
            sql.append("ORDER BY l.nome ");

            logger.info("Executando query: " + sql.toString());
            pstmt = conn.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            List<Map<String, Object>> locaisJson = new ArrayList<>();
            int contador = 0;

            while (rs.next()) {
                Map<String, Object> localMap = new HashMap<>();
                localMap.put("id", rs.getInt("id"));
                localMap.put("nome", rs.getString("nome"));
                locaisJson.add(localMap);
                contador++;

                if (contador <= 3) {
                    logger.info(String.format("Local #%d: id=%d, nome=%s",
                        contador,
                        rs.getInt("id"),
                        rs.getString("nome")
                    ));
                }
            }

            logger.info("Total de locais encontrados: " + locaisJson.size());

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("locais", locaisJson);

            Gson gson = new Gson();
            String json = gson.toJson(resposta);
            logger.info("JSON gerado com sucesso. Tamanho: " + json.length() + " bytes");
            out.write(json);

        } catch (Exception e) {
            logger.error("ERRO ao carregar locais", e);
            String mensagemErro = "{\"erro\": \"Erro ao carregar locais: " + e.getMessage().replace("\"", "'") + "\"}";
            logger.error("Retornando erro: " + mensagemErro);
            out.write(mensagemErro);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { logger.error("Erro ao fechar ResultSet", e); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { logger.error("Erro ao fechar PreparedStatement", e); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { logger.error("Erro ao fechar Connection", e); }
        }
    }

    private String criarJsonErro(String mensagem) {
        return "{\"erro\":\"" + mensagem.replace("\"", "\\\"") + "\"}";
    }
}
