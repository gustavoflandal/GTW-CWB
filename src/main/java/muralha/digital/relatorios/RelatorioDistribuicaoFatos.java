package muralha.digital.relatorios;

import java.io.IOException;
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
import com.google.gson.Gson;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;

/**
 * Servlet para relatório de distribuição de fatos
 * @author Sistema GTW-CWB
 */
@WebServlet("/MuralhaDigital/RelatorioDistribuicaoFatos")
public class RelatorioDistribuicaoFatos extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(RelatorioDistribuicaoFatos.class);
    
    /**
     * Valida e converte data para o formato yyyy-MM-dd
     * Aceita formatos: dd/MM/yyyy ou yyyy-MM-dd
     * Adiciona logs detalhados para depuração.
     */
    private String validarEConverterData(String data) {
        if (data == null || data.trim().isEmpty() || "--".equals(data)) {
            logger.warn("Data nula ou vazia recebida: " + data);
            return null;
        }

        data = data.trim();

        // Se já está no formato yyyy-MM-dd, valida e retorna
        if (data.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                sdf.parse(data);
                logger.info("Data validada no formato yyyy-MM-dd: " + data);
                return data;
            } catch (java.text.ParseException e) {
                logger.warn("Data inválida no formato yyyy-MM-dd: " + data);
                return null;
            }
        }

        // Se está no formato dd/MM/yyyy, converte para yyyy-MM-dd
        if (data.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                inputFormat.setLenient(false);

                Date parsedDate = inputFormat.parse(data);
                String convertedDate = outputFormat.format(parsedDate);
                logger.info("Data convertida de dd/MM/yyyy para yyyy-MM-dd: " + convertedDate);
                return convertedDate;
            } catch (java.text.ParseException e) {
                logger.warn("Data inválida no formato dd/MM/yyyy: " + data);
                return null;
            }
        }

        logger.warn("Formato de data não reconhecido: " + data);
        return null;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        logger.info("=== RelatorioDistribuicaoFatos.doPost EXECUTADO ===");
        logger.info("Request URI: " + request.getRequestURI());
        logger.info("Request Method: " + request.getMethod());
        
        String acao = request.getParameter("acao");
        logger.info("Parâmetro 'acao': " + acao);
        
        // Log de todos os parâmetros recebidos
        java.util.Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            logger.info("Parâmetro recebido: " + paramName + " = " + paramValue);
        }
        
        if ("buscarDados".equals(acao)) {
            logger.info("Chamando buscarDadosDistribuicaoFatos");
            buscarDadosDistribuicaoFatos(request, response);
        } else if ("exportarExcel".equals(acao)) {
            logger.info("Chamando exportarParaExcel");
            exportarParaExcel(request, response);
        } else {
            logger.info("Redirecionando para JSP");
            // Redireciona para a página JSP do relatório
            request.getRequestDispatcher("/muralha-digital/pages/relatorios/relatorio-distribuicao-fatos.jsp")
                    .forward(request, response);
        }
    }
    
    /**
     * Busca os dados de distribuição de fatos usando a procedure sp_RelatorioDistribuicaoFatos
     */
    private void buscarDadosDistribuicaoFatos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();
            
            // Define datas padrão (últimos 30 dias)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dataFimDate = new Date();
            Date dataInicioDate = new Date();
            dataInicioDate.setTime(dataInicioDate.getTime() - (30L * 24 * 60 * 60 * 1000)); // 30 dias atrás
            
            String dataInicio = sdf.format(dataInicioDate);
            String dataFim = sdf.format(dataFimDate);
            
            logger.info("Usando datas padrão - Data Início: '" + dataInicio + "', Data Fim: '" + dataFim + "'");
            
            String sql = "EXEC muralha.sp_RelatorioDistribuicaoFatos ?, ?";
            logger.info("Executando procedure: " + sql + " com parâmetros: [" + dataInicio + ", " + dataFim + "]");
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, dataInicio);
            stmt.setString(2, dataFim);
            
            rs = stmt.executeQuery();

            List<Map<String, Object>> dados = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> registro = new HashMap<>();
                registro.put("tipoFato", rs.getString("TipoDeFato"));
                registro.put("totalRegistros", rs.getInt("TotalRegistros"));
                registro.put("dataPrimeiraOcorrencia", rs.getString("DataPrimeiraOcorrencia"));
                registro.put("dataUltimaOcorrencia", rs.getString("DataUltimaOcorrencia"));
                registro.put("percentualSobreTotal", rs.getBigDecimal("PercentualSobreTotal"));
                
                dados.add(registro);
                
                // Log detalhado dos primeiros registros para debug
                if (dados.size() <= 3) {
                    logger.info("Registro " + dados.size() + ": " + registro.toString());
                }
            }

            // Retorna os dados em formato JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(dados);

            response.getWriter().write(jsonResponse);

            logger.info("Relatório de distribuição de fatos executado. Total de registros: " + dados.size());

        } catch (SQLException e) {
            logger.error("Erro ao executar procedure de distribuição de fatos: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorMsg = String.format("{\"erro\": \"Erro ao buscar dados de distribuição de fatos: %s\"}", 
                e.getMessage().replace("\"", "'"));
            response.getWriter().write(errorMsg);
        } catch (Exception e) {
            logger.error("Erro geral no servlet de distribuição de fatos: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"erro\": \"Erro interno do servidor\"}");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.error("Erro ao fechar conexões: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Exporta os dados para Excel
     */
    private void exportarParaExcel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        XSSFWorkbook workbook = null;
        ByteArrayOutputStream out = null;

        try {
            conn = Conexao.getConexao();
            
            // Define datas padrão (últimos 30 dias)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dataFimDate = new Date();
            Date dataInicioDate = new Date();
            dataInicioDate.setTime(dataInicioDate.getTime() - (30L * 24 * 60 * 60 * 1000)); // 30 dias atrás
            
            String dataInicio = sdf.format(dataInicioDate);
            String dataFim = sdf.format(dataFimDate);
            
            logger.info("Exportação Excel - Usando datas padrão - Data Início: '" + dataInicio + "', Data Fim: '" + dataFim + "'");
            
            String sql = "EXEC muralha.sp_RelatorioDistribuicaoFatos ?, ?";
            logger.info("Exportação Excel - Executando procedure: " + sql);
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, dataInicio);
            stmt.setString(2, dataFim);
            
            rs = stmt.executeQuery();

            // Criar workbook Excel
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Distribuição de Fatos");

            // Criar estilo para cabeçalho
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Criar estilo para dados
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);

            // Criar estilo para números
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);

            // Criar estilo para datas
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy"));
            dateStyle.setAlignment(HorizontalAlignment.CENTER);

            // Criar estilo para percentuais
            CellStyle percentStyle = workbook.createCellStyle();
            percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
            percentStyle.setAlignment(HorizontalAlignment.RIGHT);

            // Criar cabeçalho
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Tipo de Fato", "Total de Registros", "Primeira Ocorrência", "Última Ocorrência", "% sobre o Total"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Adicionar dados
            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                
                // Tipo de Fato
                Cell tipoFatoCell = row.createCell(0);
                tipoFatoCell.setCellValue(rs.getString("TipoDeFato"));
                tipoFatoCell.setCellStyle(dataStyle);
                
                // Total de Registros
                Cell totalCell = row.createCell(1);
                totalCell.setCellValue(rs.getInt("TotalRegistros"));
                totalCell.setCellStyle(numberStyle);
                
                // Data Primeira Ocorrência
                Cell dataInicioCell = row.createCell(2);
                dataInicioCell.setCellValue(rs.getDate("DataPrimeiraOcorrencia"));
                dataInicioCell.setCellStyle(dateStyle);
                
                // Data Última Ocorrência
                Cell dataFimCell = row.createCell(3);
                dataFimCell.setCellValue(rs.getDate("DataUltimaOcorrencia"));
                dataFimCell.setCellStyle(dateStyle);
                
                // Percentual sobre o Total
                Cell percentualCell = row.createCell(4);
                double percentual = rs.getBigDecimal("PercentualSobreTotal").doubleValue() / 100.0;
                percentualCell.setCellValue(percentual);
                percentualCell.setCellStyle(percentStyle);
            }

            // Auto-ajustar colunas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Configurar resposta para download
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=distribuicao_fatos_" + 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");

            // Escrever o workbook na resposta
            out = new ByteArrayOutputStream();
            workbook.write(out);
            response.getOutputStream().write(out.toByteArray());
            response.getOutputStream().flush();

        } catch (SQLException e) {
            logger.error("Erro ao executar consulta SQL: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"erro\": \"Erro ao executar consulta no banco de dados: " + 
                e.getMessage().replace("\"", "'") + "\"}");
        } catch (IOException e) {
            logger.error("Erro de E/S ao gerar o arquivo Excel: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"erro\": \"Erro ao gerar o arquivo Excel\"}");
        } catch (Exception e) {
            logger.error("Erro inesperado ao exportar para Excel: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"erro\": \"Erro inesperado ao processar a requisição\"}");
        } finally {
            try {
                if (out != null) out.close();
                if (workbook != null) workbook.close();
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
            }
        }
    }
}
