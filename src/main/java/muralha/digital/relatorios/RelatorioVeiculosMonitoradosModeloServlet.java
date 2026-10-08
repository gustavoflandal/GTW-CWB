/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW - Relatório de Veículos Monitorados por Modelo
  
  Empresa: Consilux Tecnologia
  
  Autor: Sistema
  Data: 29/09/2025

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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.Gson;

@WebServlet("/muralha-digital/relatorio/RelatorioVeiculosMonitoradosModelo")
public class RelatorioVeiculosMonitoradosModeloServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RelatorioVeiculosMonitoradosModeloServlet.class);
    
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
            
            // Gerar JSON com dados dos veículos monitorados
            String jsonResult = gerarRelatorioVeiculosMonitorados(dataInicio, dataFim);
            
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResult);
            
        } catch (Exception e) {
            logger.error("Erro ao gerar relatório de veículos monitorados", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\":\"Erro interno do servidor: " + e.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }
    
    private String gerarRelatorioVeiculosMonitorados(String dataInicio, String dataFim) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = Conexao.getConexao();
            
            // Preparar chamada da procedure
            String sql = "{call muralha.spu_RelatorioVeiculosMonitoradosModelo(?, ?)}";
            stmt = conn.prepareCall(sql);
            
            // Definir parâmetros
            stmt.setString(1, dataInicio);
            stmt.setString(2, dataFim);
            
            // Executar procedure e processar os 5 recordsets tabulares
            boolean hasResults = stmt.execute();
            
            Map<String, Object> resultado = new HashMap<>();
            int recordsetCount = 0;
            
            while (hasResults) {
                ResultSet rs = stmt.getResultSet();
                
                switch (recordsetCount) {
                    case 0: // VeiculosMonitorados
                        resultado.put("veiculosMonitorados", processarVeiculosMonitorados(rs));
                        break;
                    case 1: // HistogramaTiposFatos
                        resultado.put("distribuicaoTiposFatos", processarHistogramaTiposFatos(rs));
                        break;
                    case 2: // HistogramaModelosVeiculos
                        resultado.put("distribuicaoModelos", processarHistogramaModelos(rs));
                        break;
                    case 3: // ResumoEstatistico
                        resultado.put("estatisticasGerais", processarResumoEstatistico(rs));
                        break;
                    case 4: // TopPlacasOcorrencias
                        resultado.put("topPlacas", processarTopPlacas(rs));
                        break;
                }
                
                rs.close();
                recordsetCount++;
                hasResults = stmt.getMoreResults();
            }
            
            // Adicionar informações do período
            resultado.put("periodoConsulta", criarInfoPeriodo(dataInicio, dataFim));
            
            // Converter para JSON
            Gson gson = new Gson();
            return gson.toJson(resultado);
            
        } catch (SQLException e) {
            logger.error("Erro SQL ao executar procedure de veículos monitorados", e);
            return criarJsonErro("Erro ao executar consulta no banco de dados: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro geral ao gerar relatório de veículos monitorados", e);
            return criarJsonErro("Erro ao processar dados: " + e.getMessage());
        } finally {
            // Fechar recursos
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.error("Erro ao fechar recursos de banco", e);
            }
        }
    }
    
    private List<Map<String, Object>> processarVeiculosMonitorados(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        
        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("idRegistroFato", rs.getInt("IdRegistroFato"));
            registro.put("dataCriacao", rs.getString("DataCriacao"));
            registro.put("tipoFato", rs.getString("TipoFato"));
            registro.put("placa", rs.getString("Placa"));
            registro.put("cor", rs.getString("Cor"));
            registro.put("marca", rs.getString("Marca"));
            registro.put("modelo", rs.getString("Modelo"));
            registro.put("descricao", rs.getString("DescricaoMonitoramento"));
            registro.put("dataInicioMonitoramento", rs.getString("DataInicioMonitoramento"));
            registro.put("dataFimMonitoramento", rs.getString("DataFimMonitoramento"));
            registro.put("statusFato", rs.getString("StatusFato"));
            registro.put("diasAndamento", rs.getInt("DiasEmAndamento"));
            dados.add(registro);
        }
        
        return dados;
    }
    
    private List<Map<String, Object>> processarHistogramaTiposFatos(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        
        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("tipoFato", rs.getString("TipoFato"));
            registro.put("quantidade", rs.getInt("QuantidadeFatos"));
            registro.put("percentual", rs.getDouble("PercentualFatos"));
            dados.add(registro);
        }
        
        return dados;
    }
    
    private List<Map<String, Object>> processarHistogramaModelos(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        
        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("marca", rs.getString("Marca"));
            registro.put("modelo", rs.getString("Modelo"));
            registro.put("veiculosUnicos", rs.getInt("QuantidadeVeiculosUnicos"));
            registro.put("totalFatos", rs.getInt("QuantidadeFatos"));
            registro.put("percentual", rs.getDouble("PercentualFatos"));
            dados.add(registro);
        }
        
        return dados;
    }
    
    private Map<String, Object> processarResumoEstatistico(ResultSet rs) throws SQLException {
        Map<String, Object> resumo = new HashMap<>();
        
        if (rs.next()) {
            resumo.put("totalVeiculosMonitorados", rs.getInt("TotalVeiculosMonitorados"));
            resumo.put("totalFatosRegistrados", rs.getInt("TotalFatosRegistrados"));
            resumo.put("tiposFatosDistintos", rs.getInt("TiposFatosDistintos"));
            resumo.put("modelosVeiculosDistintos", rs.getInt("ModelosVeiculosDistintos"));
            resumo.put("primeiroFatoRegistrado", rs.getString("PrimeiroFatoRegistrado"));
            resumo.put("ultimoFatoRegistrado", rs.getString("UltimoFatoRegistrado"));
            resumo.put("mediaDiasAndamento", rs.getDouble("MediaDiasAndamento"));
        }
        
        return resumo;
    }
    
    private List<Map<String, Object>> processarTopPlacas(ResultSet rs) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        
        while (rs.next()) {
            Map<String, Object> registro = new HashMap<>();
            registro.put("placa", rs.getString("Placa"));
            registro.put("marca", rs.getString("Marca"));
            registro.put("modelo", rs.getString("Modelo"));
            registro.put("ocorrencias", rs.getInt("QuantidadeOcorrencias"));
            registro.put("tiposFatosEnvolvidos", rs.getString("TiposFatosEnvolvidos"));
            registro.put("primeiraOcorrencia", rs.getString("PrimeiraOcorrencia"));
            registro.put("ultimaOcorrencia", rs.getString("UltimaOcorrencia"));
            dados.add(registro);
        }
        
        return dados;
    }
    
    private Map<String, Object> criarInfoPeriodo(String dataInicio, String dataFim) {
        Map<String, Object> info = new HashMap<>();
        
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy");
            
            Date dtInicio = formatoEntrada.parse(dataInicio);
            Date dtFim = formatoEntrada.parse(dataFim);
            
            info.put("dataInicio", formatoSaida.format(dtInicio));
            info.put("dataFim", formatoSaida.format(dtFim));
            info.put("periodo", formatoSaida.format(dtInicio) + " a " + formatoSaida.format(dtFim));
            
        } catch (Exception e) {
            logger.error("Erro ao formatar datas do período", e);
            info.put("dataInicio", dataInicio);
            info.put("dataFim", dataFim);
            info.put("periodo", dataInicio + " a " + dataFim);
        }
        
        return info;
    }
    
    private String criarJsonErro(String mensagem) {
        return "{\"erro\":\"" + mensagem.replace("\"", "\\\"") + "\"}";
    }
}
