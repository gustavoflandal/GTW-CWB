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
 * Servlet para relatório de evolução semanal
 * 
 * @author Sistema GTW-CWB
 */
@WebServlet("/MuralhaDigital/RelatorioEvolucaoSemanal")
public class RelatorioEvolucaoSemanal extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(RelatorioEvolucaoSemanal.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");

        if ("buscarDados".equals(acao)) {
            buscarDadosEvolucaoSemanal(request, response);
        } else if ("exportarExcel".equals(acao)) {
            exportarParaExcel(request, response);
        } else {
            // Redireciona para a página JSP do relatório
            request.getRequestDispatcher("/muralha-digital/pages/relatorios/relatorio-evolucao-semanal.jsp")
                    .forward(request, response);
        }
    }

    /**
     * Busca os dados de evolução semanal usando a procedure
     * spu_sp_CalculaParticipacaoFatosPorSemana
     */
    private void buscarDadosEvolucaoSemanal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;
        PreparedStatement stmt = null;

        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();

            // Obter parâmetros
            String semanaParam = request.getParameter("semana");
            String anoParam = request.getParameter("ano");
            String forceRefresh = request.getParameter("_forceRefresh");
            String teste = request.getParameter("_teste");

            logger.info("Parâmetros recebidos - Semana: '" + semanaParam + "', Ano: '" + anoParam + "'" + 
                       (forceRefresh != null ? " (FORCE REFRESH)" : "") +
                       (teste != null ? " (TESTE: " + teste + ")" : ""));

            // Usar a procedure spu_RelatorioDeEvolucaoSemanalDeFatos com parâmetros
            String sql = "EXEC muralha.spu_RelatorioDeEvolucaoSemanalDeFatos ?, ?";
            
            logger.info("DEBUG - Executando procedure: " + sql);
            logger.info("DEBUG - Parâmetros que serão enviados: ano='" + anoParam + "', semana='" + semanaParam + "'");

            stmt = conn.prepareStatement(sql);
            
            // Definir parâmetro ano
            logger.info("DEBUG - Processando parâmetro ano: '" + anoParam + "' (length: " + (anoParam != null ? anoParam.length() : "null") + ")");
            if (anoParam != null && !anoParam.trim().isEmpty()) {
                try {
                    int ano = Integer.parseInt(anoParam.trim());
                    stmt.setInt(1, ano);
                    logger.info("Parâmetro ano definido com sucesso: " + ano);
                } catch (NumberFormatException e) {
                    stmt.setNull(1, java.sql.Types.INTEGER);
                    logger.error("Parâmetro ano inválido '" + anoParam + "', definido como NULL. Erro: " + e.getMessage());
                }
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
                logger.info("Parâmetro ano não informado ou vazio, definido como NULL");
            }
            
            // Definir parâmetro semana
            if (semanaParam != null && !semanaParam.trim().isEmpty()) {
                try {
                    int semana = Integer.parseInt(semanaParam);
                    stmt.setInt(2, semana);
                    logger.info("Parâmetro semana definido: " + semana);
                } catch (NumberFormatException e) {
                    stmt.setNull(2, java.sql.Types.INTEGER);
                    logger.info("Parâmetro semana inválido, definido como NULL");
                }
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
                logger.info("Parâmetro semana não informado, definido como NULL");
            }
            logger.info("PreparedStatement criado com sucesso. Executando query...");

            rs = stmt.executeQuery();

            List<Map<String, Object>> dados = new ArrayList<>();

            // Log detalhado das colunas retornadas
            try {
                java.sql.ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                logger.info("Procedure retornou " + columnCount + " colunas:");
                for (int i = 1; i <= columnCount; i++) {
                    logger.info("Coluna " + i + ": " + metaData.getColumnName(i) + " (Tipo: "
                            + metaData.getColumnTypeName(i) + ")");
                }
            } catch (SQLException e) {
                logger.error("Erro ao obter metadados das colunas: " + e.getMessage(), e);
            }

            while (rs.next()) {
                Map<String, Object> registro = new HashMap<>();
                try {
                    registro.put("ano", rs.getInt("ano"));
                    registro.put("semana", rs.getInt("semana"));
                    registro.put("tipoFato", rs.getString("tipofato"));
                    registro.put("quantidade", rs.getInt("quantidade"));
                    registro.put("distribuicaoPercentual", rs.getBigDecimal("percentualparticipacao"));
                    dados.add(registro);

                    // Log detalhado dos primeiros registros para debug
                    if (dados.size() <= 3) {
                        logger.info("Registro " + dados.size() + ": ano=" + rs.getInt("ano") +
                                ", semana=" + rs.getInt("semana") +
                                ", tipofato=" + rs.getString("tipofato") +
                                ", quantidade=" + rs.getInt("quantidade") +
                                ", percentualparticipacao=" + rs.getBigDecimal("percentualparticipacao"));
                    }
                } catch (SQLException e) {
                    logger.error("Erro ao processar registro do ResultSet: " + e.getMessage(), e);
                    // Tenta com nomes alternativos das colunas
                    try {
                        registro.put("ano", rs.getInt(1));
                        registro.put("semana", rs.getInt(2));
                        registro.put("tipoFato", rs.getString(3));
                        registro.put("quantidade", rs.getInt(4));
                        registro.put("distribuicaoPercentual", rs.getBigDecimal(5));
                        dados.add(registro);
                        logger.info("Registro processado usando índices de coluna");
                    } catch (SQLException e2) {
                        logger.error("Erro também ao usar índices: " + e2.getMessage(), e2);
                    }
                }
            }

            // Retorna os dados em formato JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Headers anti-cache para evitar problemas de retenção
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            
            // Headers extras para force refresh
            if (forceRefresh != null) {
                response.setHeader("X-Force-Refresh", "true");
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
                response.setHeader("Vary", "*");
            }

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(dados);

            response.getWriter().write(jsonResponse);

            // Log com informações dos filtros aplicados
            StringBuilder filtrosLog = new StringBuilder();
            if (anoParam != null && !anoParam.trim().isEmpty()) {
                filtrosLog.append("ano: ").append(anoParam);
            }
            if (semanaParam != null && !semanaParam.trim().isEmpty()) {
                if (filtrosLog.length() > 0) filtrosLog.append(", ");
                filtrosLog.append("semana: ").append(semanaParam);
            }
            
            String operacao = forceRefresh != null ? "LIMPEZA DE FILTROS" : "CONSULTA NORMAL";
            logger.info("Relatório de evolução semanal executado [" + operacao + "]. Total de registros: " + dados.size() +
                    (filtrosLog.length() > 0 ? " (filtros: " + filtrosLog.toString() + ")" : " (sem filtros)"));

        } catch (SQLException e) {
            logger.error("Erro ao executar procedure de evolução semanal: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"erro\": \"Erro ao buscar dados de evolução semanal\"}");
        } catch (Exception e) {
            logger.error("Erro geral no servlet de evolução semanal: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"erro\": \"Erro interno do servidor\"}");
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                logger.error("Erro ao fechar conexões: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Exporta os dados de evolução semanal para Excel
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

            // Obter parâmetros
            String semanaParam = request.getParameter("semana");
            String anoParam = request.getParameter("ano");

            logger.info("Exportação Excel - Parâmetros recebidos - Semana: '" + semanaParam + "', Ano: '" + anoParam + "'");

            // Usar a procedure spu_RelatorioDeEvolucaoSemanalDeFatos com parâmetros
            String sql = "EXEC muralha.spu_RelatorioDeEvolucaoSemanalDeFatos ?, ?";
            
            logger.info("Exportação Excel - Executando procedure: " + sql);

            stmt = conn.prepareStatement(sql);
            
            // Definir parâmetro ano
            if (anoParam != null && !anoParam.trim().isEmpty()) {
                try {
                    int ano = Integer.parseInt(anoParam);
                    stmt.setInt(1, ano);
                    logger.info("Exportação Excel - Parâmetro ano definido: " + ano);
                } catch (NumberFormatException e) {
                    stmt.setNull(1, java.sql.Types.INTEGER);
                    logger.info("Exportação Excel - Parâmetro ano inválido, definido como NULL");
                }
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
                logger.info("Exportação Excel - Parâmetro ano não informado, definido como NULL");
            }
            
            // Definir parâmetro semana
            if (semanaParam != null && !semanaParam.trim().isEmpty()) {
                try {
                    int semana = Integer.parseInt(semanaParam);
                    stmt.setInt(2, semana);
                    logger.info("Exportação Excel - Parâmetro semana definido: " + semana);
                } catch (NumberFormatException e) {
                    stmt.setNull(2, java.sql.Types.INTEGER);
                    logger.info("Exportação Excel - Parâmetro semana inválido, definido como NULL");
                }
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
                logger.info("Exportação Excel - Parâmetro semana não informado, definido como NULL");
            }
            rs = stmt.executeQuery();

            // Criar workbook Excel
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Evolução Semanal");

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

            // Criar estilo para percentuais
            CellStyle percentStyle = workbook.createCellStyle();
            percentStyle.setAlignment(HorizontalAlignment.RIGHT);
            DataFormat format = workbook.createDataFormat();
            percentStyle.setDataFormat(format.getFormat("0.00%"));

            // Criar cabeçalho
            Row headerRow = sheet.createRow(0);
            String[] headers = { "Ano", "Semana", "Tipo de Fato", "Quantidade", "Percentual de Participação" };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Adicionar dados
            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);

                // Ano
                Cell anoCell = row.createCell(0);
                anoCell.setCellValue(rs.getInt("ano"));
                anoCell.setCellStyle(numberStyle);

                // Semana
                Cell semanaCell = row.createCell(1);
                semanaCell.setCellValue(rs.getInt("semana"));
                semanaCell.setCellStyle(numberStyle);

                // Tipo de Fato
                Cell tipoFatoCell = row.createCell(2);
                tipoFatoCell.setCellValue(rs.getString("tipofato"));
                tipoFatoCell.setCellStyle(dataStyle);

                // Quantidade
                Cell quantidadeCell = row.createCell(3);
                quantidadeCell.setCellValue(rs.getInt("quantidade"));
                quantidadeCell.setCellStyle(numberStyle);

                // Percentual de Participação
                Cell percentualCell = row.createCell(4);
                double percentual = rs.getBigDecimal("percentualparticipacao").doubleValue() / 100.0;
                percentualCell.setCellValue(percentual);
                percentualCell.setCellStyle(percentStyle);
            }

            // Auto-ajustar colunas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Configurar response para download
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            // Nome do arquivo com timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = sdf.format(new Date());
            String fileName = "relatorio_evolucao_semanal_" + timestamp + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // Escrever workbook para response
            out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] excelData = out.toByteArray();

            response.setContentLength(excelData.length);
            response.getOutputStream().write(excelData);
            response.getOutputStream().flush();

            logger.info("Exportação Excel concluída. Arquivo: " + fileName +
                    (semanaParam != null ? " (filtro semana: " + semanaParam + ")" : " (sem filtro)"));

        } catch (SQLException e) {
            logger.error("Erro ao exportar para Excel: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro ao gerar arquivo Excel");
        } catch (Exception e) {
            logger.error("Erro geral na exportação Excel: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro interno do servidor");
        } finally {
            try {
                if (out != null)
                    out.close();
                if (workbook != null)
                    workbook.close();
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos na exportação Excel: " + e.getMessage(), e);
            }
        }
    }
}