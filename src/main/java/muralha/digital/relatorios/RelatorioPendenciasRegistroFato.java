package muralha.digital.relatorios;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.consilux.lib.Conexao;

@WebServlet("/MuralhaDigital/RelatorioPendenciasRegistroFato")
public class RelatorioPendenciasRegistroFato extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RelatorioPendenciasRegistroFato.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String acao = request.getParameter("acao");
        if ("exportarExcel".equals(acao)) {
            exportarParaExcel(request, response);
        } else {
            request.getRequestDispatcher("/muralha-digital/pages/relatorios/relatorio-pendencias-registro-fato.jsp")
                    .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String acao = request.getParameter("acao");
        logger.info("=== DOPOST CHAMADO - ACAO: " + acao + " ===");
        
        if ("buscarDados".equals(acao)) {
            logger.info("Chamando buscarDadosPendencias");
            buscarDadosPendencias(request, response);
        } else if ("exportarExcel".equals(acao)) {
            logger.info("Chamando exportarParaExcel");
            exportarParaExcel(request, response);
        } else {
            logger.info("Redirecionando para JSP");
            request.getRequestDispatcher("/muralha-digital/pages/relatorios/relatorio-pendencias-registro-fato.jsp")
                    .forward(request, response);
        }
    }

    private void buscarDadosPendencias(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try (Connection conn = Conexao.getConexao()) {
            logger.info("=== INICIANDO BUSCA DE DADOS ===");
            logger.info("Conexão obtida com sucesso");

            // Obter parâmetros de data
            String dataInicio = request.getParameter("dataInicio");
            String dataFim = request.getParameter("dataFim");
            
            logger.info("Parâmetros recebidos - Data Início: " + dataInicio + ", Data Fim: " + dataFim);

            // Executar a procedure com parâmetros
            String sql = "EXEC muralha.spu_RelatorioDePendenciasNosRegistrosDeFato ?, ?";

            stmt = conn.prepareStatement(sql);
            
            // Definir parâmetros (pode ser null se não informado)
            if (dataInicio != null && !dataInicio.trim().isEmpty()) {
                stmt.setString(1, dataInicio);
            } else {
                stmt.setNull(1, java.sql.Types.VARCHAR);
            }
            
            if (dataFim != null && !dataFim.trim().isEmpty()) {
                stmt.setString(2, dataFim);
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }
            rs = stmt.executeQuery();
            logger.info("Procedure executada com sucesso");

            List<Map<String, Object>> dados = new ArrayList<>();
            logger.info("Iniciando processamento do ResultSet...");

            // Processar dados

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                Map<String, Object> item = new HashMap<>();

                // Mapear as colunas conforme definido na procedure
                try {
                    item.put("id", rs.getObject("id"));
                    item.put("tipo", rs.getObject("tipo"));
                    item.put("status", rs.getObject("status"));
                    item.put("usuario", rs.getObject("usuario"));
                    item.put("dataCriacao", rs.getObject("data_criacao"));
                    item.put("dataEncerramento", rs.getObject("data_encerramento"));
                    item.put("privado", rs.getInt("privado") == 1);
                    item.put("faltas", rs.getObject("Faltas"));
                } catch (SQLException e) {
                    logger.warn("Erro ao mapear linha " + rowCount + ": " + e.getMessage());
                }

                dados.add(item);

            }

            logger.info("Total de linhas processadas: " + rowCount);
            logger.info("Total de itens na lista: " + dados.size());

            // Converter para JSON
            Gson gson = new Gson();
            String json = gson.toJson(dados);
            logger.info("JSON gerado com " + dados.size() + " registros");

            // Enviar resposta
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
            logger.info("=== RESPOSTA ENVIADA ===");

        } catch (SQLException e) {
            logger.error("Erro SQL ao buscar dados de pendências: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"erro\": \"Erro SQL: " + e.getMessage().replace("\"", "'") + "\"}");
        } catch (Exception e) {
            logger.error("Erro geral ao buscar dados de pendências: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"erro\": \"Erro interno: " + e.getMessage().replace("\"", "'") + "\"}");
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                logger.error("Erro ao fechar conexões: " + e.getMessage(), e);
            }
        }
    }

    private void exportarParaExcel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        XSSFWorkbook workbook = null;
        ByteArrayOutputStream out = null;

        try {
            conn = Conexao.getConexao();

            // Obter parâmetros de data
            String dataInicio = request.getParameter("dataInicio");
            String dataFim = request.getParameter("dataFim");
            
            logger.info("Exportação Excel - Parâmetros recebidos - Data Início: " + dataInicio + ", Data Fim: " + dataFim);

            // Executar a procedure com parâmetros
            String sql = "EXEC muralha.spu_RelatorioDePendenciasNosRegistrosDeFato ?, ?";
            logger.info("Exportação Excel - Executando procedure - SQL: " + sql);
            
            stmt = conn.prepareStatement(sql);
            
            // Definir parâmetros (pode ser null se não informado)
            if (dataInicio != null && !dataInicio.trim().isEmpty()) {
                stmt.setString(1, dataInicio);
            } else {
                stmt.setNull(1, java.sql.Types.VARCHAR);
            }
            
            if (dataFim != null && !dataFim.trim().isEmpty()) {
                stmt.setString(2, dataFim);
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }
            rs = stmt.executeQuery();

            // Criar workbook Excel
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Relatório de Pendências");

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

            // Criar cabeçalho
            Row headerRow = sheet.createRow(0);
            String[] headers = { "ID", "Tipo", "Status", "Usuário", "Data Criação", "Data Encerramento", "Privado", "Faltas" };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Adicionar dados
            int rowNum = 1;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);

                // ID
                Cell idCell = row.createCell(0);
                idCell.setCellValue(rs.getInt("id"));
                idCell.setCellStyle(dataStyle);

                // Tipo
                Cell tipoCell = row.createCell(1);
                tipoCell.setCellValue(rs.getString("tipo"));
                tipoCell.setCellStyle(dataStyle);

                // Status
                Cell statusCell = row.createCell(2);
                statusCell.setCellValue(rs.getString("status"));
                statusCell.setCellStyle(dataStyle);

                // Usuário
                Cell usuarioCell = row.createCell(3);
                usuarioCell.setCellValue(rs.getString("usuario"));
                usuarioCell.setCellStyle(dataStyle);

                // Data Criação
                Cell dataCriacaoCell = row.createCell(4);
                Timestamp dataCriacao = rs.getTimestamp("data_criacao");
                if (dataCriacao != null) {
                    dataCriacaoCell.setCellValue(dateFormat.format(dataCriacao));
                }
                dataCriacaoCell.setCellStyle(dataStyle);

                // Data Encerramento
                Cell dataEncerramentoCell = row.createCell(5);
                Timestamp dataEncerramento = rs.getTimestamp("data_encerramento");
                if (dataEncerramento != null) {
                    dataEncerramentoCell.setCellValue(dateFormat.format(dataEncerramento));
                }
                dataEncerramentoCell.setCellStyle(dataStyle);

                // Privado
                Cell privadoCell = row.createCell(6);
                privadoCell.setCellValue(rs.getInt("privado") == 1 ? "Sim" : "Não");
                privadoCell.setCellStyle(dataStyle);

                // Faltas
                Cell faltasCell = row.createCell(7);
                faltasCell.setCellValue(rs.getString("Faltas"));
                faltasCell.setCellStyle(dataStyle);
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
            String fileName = "relatorio_pendencias_registro_fato_" + timestamp + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // Escrever workbook para response
            out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] excelData = out.toByteArray();

            response.setContentLength(excelData.length);
            response.getOutputStream().write(excelData);
            response.getOutputStream().flush();

            logger.info("Exportação Excel concluída. Arquivo: " + fileName);

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