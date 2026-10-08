/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW - Relatório de Fluxo de Passagens Veiculares

  Empresa: Consilux Tecnologia

  Autor: GitHub Copilot
  Data: 26/09/2025

*********************************************************************************/

package muralha.digital.relatorios;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Local;
import com.consilux.model.Mensagem;
import com.google.gson.Gson;

@WebServlet("/relatorio/RelatorioFluxoPassagensVeiculares")
public class RelatorioFluxoPassagensVeicularesServlet extends javax.servlet.http.HttpServlet {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(RelatorioFluxoPassagensVeicularesServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validando acesso do usuário
        final Acesso acessoUsuario = new Acesso(request, response, true);
        if (!acessoUsuario.verificaAcesso()) {
            new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String acao = request.getParameter("acao");

            if ("gerarRelatorio".equals(acao)) {
                gerarRelatorioFluxo(request, out);
            } else if ("carregarLocais".equals(acao)) {
                carregarLocais(out);
            } else {
                out.write("{\"erro\": \"Ação não especificada\"}");
            }

        } catch (Exception e) {
            logger.error("Erro no relatório de fluxo de passagens veiculares", e);
            out.write("{\"erro\": \"" + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }

    private void gerarRelatorioFluxo(HttpServletRequest request, PrintWriter out) throws Exception {
        String local = request.getParameter("local");
        String dataInicio = request.getParameter("dataInicio");
        String dataFim = request.getParameter("dataFim");

        // Validar parâmetros
        if (local == null || local.trim().isEmpty() ||
            dataInicio == null || dataInicio.trim().isEmpty() ||
            dataFim == null || dataFim.trim().isEmpty()) {
            out.write("{\"erro\": \"Todos os parâmetros são obrigatórios: local, dataInicio, dataFim\"}");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = Conexao.getConexao();
            
            // Executar procedure que retorna 4 recordsets
            String sql = "{call Muralha.spu_RelatorioFluxoPassagensVeiculares(?, ?, ?)}";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(local));
            pstmt.setString(2, dataInicio);
            pstmt.setString(3, dataFim);

            Map<String, Object> resultado = new HashMap<>();
            
            // Executar e processar múltiplos recordsets
            boolean hasResults = pstmt.execute();
            int recordsetCount = 0;

            while (hasResults) {
                ResultSet rs = pstmt.getResultSet();
                
                switch (recordsetCount) {
                    case 0:
                        resultado.put("fluxo_por_hora", processarFluxoPorHora(rs));
                        break;
                    case 1:
                        resultado.put("resumo_classificacao", processarResumoClassificacao(rs));
                        break;
                    case 2:
                        resultado.put("distribuicao_horaria", processarDistribuicaoHoraria(rs));
                        break;
                    case 3:
                        resultado.put("distribuicao_sentido", processarDistribuicaoSentido(rs));
                        break;
                }
                
                rs.close();
                recordsetCount++;
                hasResults = pstmt.getMoreResults();
            }

            // Adicionar informações do período e local
            resultado.put("info_relatorio", criarInfoRelatorio(local, dataInicio, dataFim));

            // Retornar JSON
            Gson gson = new Gson();
            String json = gson.toJson(resultado);
            logger.info("JSON gerado com sucesso para local=" + local + ", período=" + dataInicio + " a " + dataFim);
            out.write(json);

        } catch (Exception e) {
            logger.error("Erro ao gerar relatório de fluxo para local=" + local + ", período=" + dataInicio + " a " + dataFim, e);
            out.write("{\"erro\": \"Erro ao executar consulta: " + e.getMessage() + "\"}");
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { logger.error("Erro ao fechar PreparedStatement", e); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { logger.error("Erro ao fechar Connection", e); }
        }
    }



    private List<Map<String, Object>> processarFluxoPorHora(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        java.sql.ResultSetMetaData metaData = rs.getMetaData();
        java.util.Set<String> colunas = obterNomesColunas(metaData);
        
        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("data", getStringSeguro(rs, colunas, "data"));
            registro.put("id_local", getIntSeguro(rs, colunas, "id_local"));
            registro.put("nome_local", obterNomeLocal(rs, colunas));
            registro.put("sentido", getStringSeguro(rs, colunas, "sentido"));
            registro.put("hora", getIntSeguro(rs, colunas, "hora"));
            registro.put("classificacao_veiculo", getStringSeguro(rs, colunas, "classificacao_veiculo"));
            registro.put("total_passagens", getIntSeguro(rs, colunas, "total_passagens"));
            dados.add(registro);
        }
        
        return dados;
    }

    private List<Map<String, Object>> processarResumoClassificacao(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        java.sql.ResultSetMetaData metaData = rs.getMetaData();
        java.util.Set<String> colunas = obterNomesColunas(metaData);
        
        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("id_local", getIntSeguro(rs, colunas, "id_local"));
            registro.put("nome_local", obterNomeLocal(rs, colunas));
            registro.put("classificacao_veiculo", getStringSeguro(rs, colunas, "classificacao_veiculo"));
            registro.put("total_passagens", getIntSeguro(rs, colunas, "total_passagens"));
            
            // Obter percentual diretamente como double do banco
            double percentual = getDoubleSeguro(rs, colunas, "percentual");
            // Arredondar para 2 casas decimais
            registro.put("percentual", Math.round(percentual * 100.0) / 100.0);
            dados.add(registro);
        }
        
        return dados;
    }

    private List<Map<String, Object>> processarDistribuicaoHoraria(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        java.sql.ResultSetMetaData metaData = rs.getMetaData();
        java.util.Set<String> colunas = obterNomesColunas(metaData);
        
        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("data", getStringSeguro(rs, colunas, "data"));
            registro.put("id_local", getIntSeguro(rs, colunas, "id_local"));
            registro.put("nome_local", obterNomeLocal(rs, colunas));
            registro.put("hora", getIntSeguro(rs, colunas, "hora"));
            registro.put("total_passagens", getIntSeguro(rs, colunas, "total_passagens"));
            dados.add(registro);
        }
        
        return dados;
    }

    private List<Map<String, Object>> processarDistribuicaoSentido(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        java.sql.ResultSetMetaData metaData = rs.getMetaData();
        java.util.Set<String> colunas = obterNomesColunas(metaData);
        
        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("id_local", getIntSeguro(rs, colunas, "id_local"));
            registro.put("nome_local", obterNomeLocal(rs, colunas));
            registro.put("sentido", getStringSeguro(rs, colunas, "sentido"));
            registro.put("total_passagens", getIntSeguro(rs, colunas, "total_passagens"));
            
            // Obter percentual diretamente como double do banco
            double percentual = getDoubleSeguro(rs, colunas, "percentual");
            // Arredondar para 2 casas decimais
            registro.put("percentual", Math.round(percentual * 100.0) / 100.0);
            dados.add(registro);
        }
        
        return dados;
    }

    // Métodos auxiliares para acesso seguro às colunas do ResultSet
    private java.util.Set<String> obterNomesColunas(java.sql.ResultSetMetaData metaData) throws SQLException {
        java.util.Set<String> colunas = new java.util.HashSet<>();
        int numColunas = metaData.getColumnCount();
        for (int i = 1; i <= numColunas; i++) {
            colunas.add(metaData.getColumnLabel(i).toLowerCase());
        }
        return colunas;
    }

    private String getStringSeguro(ResultSet rs, java.util.Set<String> colunas, String nomeColuna) throws SQLException {
        if (colunas.contains(nomeColuna.toLowerCase())) {
            return rs.getString(nomeColuna);
        }
        return null;
    }

    private int getIntSeguro(ResultSet rs, java.util.Set<String> colunas, String nomeColuna) throws SQLException {
        if (colunas.contains(nomeColuna.toLowerCase())) {
            return rs.getInt(nomeColuna);
        }
        return 0;
    }

    private double getDoubleSeguro(ResultSet rs, java.util.Set<String> colunas, String nomeColuna) throws SQLException {
        if (colunas.contains(nomeColuna.toLowerCase())) {
            return rs.getDouble(nomeColuna);
        }
        return 0.0;
    }

    private String obterNomeLocal(ResultSet rs, java.util.Set<String> colunas) throws SQLException {
        // Tentar diferentes nomes de coluna para o nome do local
        // Ordem de prioridade baseada na procedure spu_RelatorioFluxoPassagensVeiculares
        if (colunas.contains("nome_local")) {
            return rs.getString("nome_local");
        } else if (colunas.contains("nome")) {
            // Recordset 1 usa "nome" ao invés de "nome_local"
            return rs.getString("nome");
        } else if (colunas.contains("local")) {
            return rs.getString("local");
        } else if (colunas.contains("descricao_local")) {
            return rs.getString("descricao_local");
        } else if (colunas.contains("desc_local")) {
            return rs.getString("desc_local");
        } else if (colunas.contains("nm_local")) {
            return rs.getString("nm_local");
        }
        return null;
    }

    private Map<String, Object> criarInfoRelatorio(String local, String dataInicio, String dataFim) {
        Map<String, Object> info = new HashMap<>();
        
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy");
            
            Date dtInicio = formatoEntrada.parse(dataInicio);
            Date dtFim = formatoEntrada.parse(dataFim);
            
            info.put("local_id", Integer.parseInt(local));
            info.put("data_inicio_formatada", formatoSaida.format(dtInicio));
            info.put("data_fim_formatada", formatoSaida.format(dtFim));
            info.put("periodo", formatoSaida.format(dtInicio) + " a " + formatoSaida.format(dtFim));
            
        } catch (Exception e) {
            logger.error("Erro ao formatar datas do relatório", e);
            info.put("data_inicio_formatada", dataInicio);
            info.put("data_fim_formatada", dataFim);
            info.put("periodo", dataInicio + " a " + dataFim);
        }
        
        return info;
    }

    private void carregarLocais(PrintWriter out) throws Exception {
        logger.info("=== CARREGANDO LOCAIS ===");
        Connection conn = null;
        PreparedStatement pstmt = null;
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
}
