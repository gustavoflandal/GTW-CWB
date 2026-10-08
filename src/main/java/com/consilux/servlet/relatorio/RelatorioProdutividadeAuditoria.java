package com.consilux.servlet.relatorio;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellRangeAddress;

import com.consilux.infra.ExpValida;
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.exception.ModelException;
import com.consilux.model.medicao.DadosProdutividadeAuditoria;
import com.consilux.model.medicao.ItemProdutividadeAuditoria;

/**
 * Servlet para a geração de um relatório de Divergências CAI-CAV em Excel
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 10/08/2016
 */
public class RelatorioProdutividadeAuditoria extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatorioProdutividadeAuditoria.class);

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Integer idUsuario = null;
		String usuarioURL = request.getParameter("sistema");
		if(usuarioURL == null){
			final Acesso acessoUsuario = new Acesso(request, response, true);
			if (!acessoUsuario.verificaAcesso()){return;}else{
				usuarioURL = acessoUsuario.getUsuario().getNome();
				idUsuario = acessoUsuario.getUsuario().getId();
			}
		}else
			usuarioURL = "SISTEMA";
		
        Date data = new Date();  
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
        String userGerador = "Relatório gerado em: " + formatador.format(data) + ", pelo usuário: " + usuarioURL;
		
		String strDataInicio = request.getParameter("dataini");
		String strDataFim = request.getParameter("datafim");
		
		Date dtDataInicio = null;
		Date dtDataFim = null;
		
		//Validações Necessárias para montar relatório

		if((strDataInicio.equals("")) || (strDataFim.equals(""))){
			new Mensagem(response).showErro("A Data Início e a Data Fim devem ser informadas!", "javascript:window.close();");
			return;
		}
		
		if (strDataInicio == null || !ExpValida.DATA.validar(strDataInicio)) {
			new Mensagem(response).showErro("Data Início enviada inválida!", "javascript:window.close();");
			return;
		}
		if (strDataInicio == null || !ExpValida.DATA.validar(strDataInicio)) {
			new Mensagem(response).showErro("Data Fim enviada inválida!", "javascript:window.close();");
			return;
		}
		
		try {
			dtDataInicio = new SimpleDateFormat("dd/MM/yyyy").parse(strDataInicio);
			dtDataFim = new SimpleDateFormat("dd/MM/yyyy").parse(strDataFim);
			
			Calendar calendarioInicio = Calendar.getInstance();
			calendarioInicio.setTime(dtDataInicio);
			Calendar calendarioFim = Calendar.getInstance();
			calendarioFim.setTime(dtDataFim);
			
			if (calendarioInicio.after(calendarioFim)) {
				new Mensagem(response).showErro("A Data Início deve ser menor ou igual da Data Fim!", "javascript:window.close();");
				return; 
			}
			
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		try {
			
			//Buscar os dados necessários do relatório
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String strDataInicioFormatada = "";
			String strDataFimFormatada = "";
			
			strDataInicioFormatada = sdf.format(dtDataInicio);
			strDataFimFormatada = sdf.format(dtDataFim);
			
			// Criando o arquivo fisico
	        String nomeArquivo = "Produtividade Auditoria";
	        
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + ".xls\"");  			
 
	        // Criando area de trabalho para o excel
	        HSSFWorkbook wb = new HSSFWorkbook();
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        HSSFSheet sheet = wb.createSheet("Produtividade Auditoria");
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
	        
	        //Fonte
            HSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 12);
            f.setColor(HSSFFont.COLOR_NORMAL);
            f.setBold(true);
            f.setFontName("Calibri");
            
            HSSFCellStyle estilo;
            estilo = wb.createCellStyle();
            estilo.setAlignment(HorizontalAlignment.CENTER);
            estilo.setVerticalAlignment(VerticalAlignment.CENTER);
            estilo.setFont(f);
            
            HSSFFont f3 = wb.createFont();
            f3.setFontHeightInPoints((short) 10);
            f3.setFontName("Calibri");
            f3.setColor(HSSFFont.COLOR_NORMAL);
            HSSFCellStyle estiloFiltro;
            
            estiloFiltro = wb.createCellStyle();
            estiloFiltro.setAlignment(HorizontalAlignment.LEFT);
            estiloFiltro.setVerticalAlignment(VerticalAlignment.CENTER);
            estiloFiltro.setBorderBottom(BorderStyle.MEDIUM);
            estiloFiltro.setBorderTop(BorderStyle.MEDIUM);
            estiloFiltro.setBorderRight(BorderStyle.MEDIUM);
            estiloFiltro.setBorderLeft(BorderStyle.MEDIUM);
            estiloFiltro.setFont(f3);
            
            
	        //Cria uma linha na Planilha.
            Row titulo = sheet.createRow(0);
            titulo.getCell(0).setCellValue("RELATÓRIO DE DIVERGÊNCIA CAI-CAV");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 28));
            titulo.getCell(0).setCellStyle(estilo);
            
            Row titulo2 = sheet.createRow(1);
            titulo2.getCell(0).setCellValue("CONSÓRCIO LCL 06/2014-SMT - " + strDataInicioFormatada + " à " + strDataFimFormatada);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 28));
            titulo2.getCell(0).setCellStyle(estilo);
            
            //Filtros informados
            Row filtroDataInicio = sheet.createRow(3);
            filtroDataInicio.getCell(0).setCellValue("Data Início");
            filtroDataInicio.getCell(0).setCellStyle(estiloFiltro);
            filtroDataInicio.getCell(1).setCellValue(strDataInicioFormatada);
            filtroDataInicio.getCell(1).setCellStyle(estiloFiltro);
            
            Row filtroDataFim = sheet.createRow(4);
            filtroDataFim.getCell(0).setCellValue("Data Fim");
            filtroDataFim.getCell(0).setCellStyle(estiloFiltro);
            filtroDataFim.getCell(1).setCellValue(strDataFimFormatada);
            filtroDataFim.getCell(1).setCellStyle(estiloFiltro);
            
            GerarRelatorio(dtDataInicio, dtDataFim, response, sheet, wb, userGerador, nomeArquivo, idUsuario, data, usuarioURL);

			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	

	public void GerarRelatorio(Date dtDataInicio, Date dtDataFim,
							   HttpServletResponse response, HSSFSheet sheet, HSSFWorkbook wb, String userGerador,
					 		   String nomeArquivo, Integer idUsuario, Date data, String usuarioURL
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
        //Fonte para cabeçalho do excel
		HSSFFont f4 = wb.createFont();
        f4.setFontHeightInPoints((short) 10);
        f4.setBold(true);
        f4.setFontName("Calibri");
        f4.setColor(HSSFFont.COLOR_NORMAL);

        //Fonte para corpo do excel
        HSSFFont f5 = wb.createFont();
        f5.setFontHeightInPoints((short) 10);
        f5.setFontName("Calibri");
        f5.setColor(HSSFFont.COLOR_NORMAL);
        
        HSSFColor corFundo = Funcoes.setColor(wb, (byte)190, (byte)190, (byte)190);
        
        //Estilo para cabeçalho do excel
        HSSFCellStyle estiloCabecalhoCorpo;
        estiloCabecalhoCorpo = wb.createCellStyle();
        estiloCabecalhoCorpo.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setFont(f4);
        estiloCabecalhoCorpo.setFillForegroundColor(corFundo.getIndex());
        estiloCabecalhoCorpo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        HSSFCellStyle estiloCabecalhoCorpo2;
        estiloCabecalhoCorpo2 = wb.createCellStyle();
        estiloCabecalhoCorpo2.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpo2.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo2.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setFont(f4);
        estiloCabecalhoCorpo2.setFillForegroundColor(corFundo.getIndex());
        estiloCabecalhoCorpo2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        HSSFCellStyle estiloCabecalhoCorpo3;
        estiloCabecalhoCorpo3 = wb.createCellStyle();
        estiloCabecalhoCorpo3.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpo3.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo3.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo3.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo3.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo3.setFont(f4);
        estiloCabecalhoCorpo3.setFillForegroundColor(corFundo.getIndex());
        estiloCabecalhoCorpo3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        //Estilos para corpo do excel
        HSSFCellStyle estiloCorpoCentro;
        estiloCorpoCentro = wb.createCellStyle();
        estiloCorpoCentro.setAlignment(HorizontalAlignment.CENTER);
        estiloCorpoCentro.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCorpoCentro.setBorderBottom(BorderStyle.MEDIUM);
        estiloCorpoCentro.setBorderTop(BorderStyle.MEDIUM);
        estiloCorpoCentro.setBorderRight(BorderStyle.MEDIUM);
        estiloCorpoCentro.setBorderLeft(BorderStyle.MEDIUM);
        estiloCorpoCentro.setFont(f5);
        
        HSSFCellStyle estiloCorpoEsquerda;
        estiloCorpoEsquerda = wb.createCellStyle();
        estiloCorpoEsquerda.setAlignment(HorizontalAlignment.LEFT);
        estiloCorpoEsquerda.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCorpoEsquerda.setBorderBottom(BorderStyle.MEDIUM);
        estiloCorpoEsquerda.setBorderTop(BorderStyle.MEDIUM);
        estiloCorpoEsquerda.setBorderRight(BorderStyle.MEDIUM);
        estiloCorpoEsquerda.setBorderLeft(BorderStyle.MEDIUM);
        estiloCorpoEsquerda.setFont(f5);
        

        //Criando as colunas do corpo
		Row cabecalho = sheet.createRow((short)6);
		Row cabecalho2 = sheet.createRow((short)7);
		
		cabecalho.getCell(0).setCellValue("Data");
	    cabecalho.getCell(0).setCellStyle(estiloCabecalhoCorpo);
	    cabecalho2.getCell(0).setCellValue("");
	    cabecalho2.getCell(0).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(0, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(6, 7, 0, 0));
	    
		cabecalho.getCell(1).setCellValue("Login");
	    cabecalho.getCell(1).setCellStyle(estiloCabecalhoCorpo);
	    cabecalho2.getCell(1).setCellValue("");
	    cabecalho2.getCell(1).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(1, 6000);
	    sheet.addMergedRegion(new CellRangeAddress(6, 7, 1, 1));
				
		cabecalho.getCell(2).setCellValue("Auditor");
	    cabecalho.getCell(2).setCellStyle(estiloCabecalhoCorpo);
	    cabecalho2.getCell(2).setCellValue("");
	    cabecalho2.getCell(2).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(2, 6000);
	    sheet.addMergedRegion(new CellRangeAddress(6, 7, 2, 2));
	    
	    cabecalho.getCell(3).setCellValue("Hora");
		cabecalho.getCell(3).setCellStyle(estiloCabecalhoCorpo2);
		cabecalho2.getCell(3).setCellValue("de Início");
	    cabecalho2.getCell(3).setCellStyle(estiloCabecalhoCorpo3);
		sheet.setColumnWidth(3, 3000);
	    
	    cabecalho.getCell(4).setCellValue("Hora");
		cabecalho.getCell(4).setCellStyle(estiloCabecalhoCorpo);
		
		for (int i = 5; i <= 27; i++) {
			cabecalho.getCell(i).setCellValue("");
			cabecalho.getCell(i).setCellStyle(estiloCabecalhoCorpo);
		}
		sheet.addMergedRegion(new CellRangeAddress(6, 6, 4, 27));
	    
		int valorColuna = 0;
		for (int i = 4; i <= 27; i++) {
			cabecalho2.getCell(i).setCellValue(String.valueOf(valorColuna));
		    cabecalho2.getCell(i).setCellStyle(estiloCabecalhoCorpo);
		    sheet.setColumnWidth(i, 1200);
		    valorColuna++;
		}
		
		cabecalho.getCell(28).setCellValue("Total");
	    cabecalho.getCell(28).setCellStyle(estiloCabecalhoCorpo);
	    cabecalho2.getCell(28).setCellValue("");
	    cabecalho2.getCell(28).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(28, 1500);
	    sheet.addMergedRegion(new CellRangeAddress(6, 7, 28, 28));
        
        //Buscando informações para popular planilhas
		DadosProdutividadeAuditoria dadosProdutividadeAuditoria = new DadosProdutividadeAuditoria();
		ArrayList<ItemProdutividadeAuditoria> dadosRelatorio = dadosProdutividadeAuditoria.produtividadeAuditoria(dtDataInicio, dtDataFim);
		
        if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)8);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        	sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, 28));
        }else{
            for(int i=0; i<dadosRelatorio.size(); i++){
            	
            	Row linhaSheet = sheet.createRow((short)(i+8));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getDataProcessamento());
            	linhaSheet.getCell(0).setCellStyle(estiloCorpoCentro);
            	linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getLogin());
            	linhaSheet.getCell(1).setCellStyle(estiloCorpoCentro);
            	linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getAuditor());
            	linhaSheet.getCell(2).setCellStyle(estiloCorpoEsquerda);
        		linhaSheet.getCell(3).setCellValue(dadosRelatorio.get(i).getHorarioInicioProcessamento());
            	linhaSheet.getCell(3).setCellStyle(estiloCorpoCentro);
            	linhaSheet.getCell(4).setCellValue(dadosRelatorio.get(i).getValor0());
            	linhaSheet.getCell(4).setCellStyle(estiloCorpoCentro);
            	linhaSheet.getCell(5).setCellValue(dadosRelatorio.get(i).getValor1());
            	linhaSheet.getCell(5).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(6).setCellValue(dadosRelatorio.get(i).getValor2());
            	linhaSheet.getCell(6).setCellStyle(estiloCorpoCentro);
            	linhaSheet.getCell(7).setCellValue(dadosRelatorio.get(i).getValor3());
            	linhaSheet.getCell(7).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(8).setCellValue(dadosRelatorio.get(i).getValor4());
            	linhaSheet.getCell(8).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(9).setCellValue(dadosRelatorio.get(i).getValor5());
            	linhaSheet.getCell(9).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(10).setCellValue(dadosRelatorio.get(i).getValor6());
            	linhaSheet.getCell(10).setCellStyle(estiloCorpoEsquerda);
        		linhaSheet.getCell(11).setCellValue(dadosRelatorio.get(i).getValor7());
            	linhaSheet.getCell(11).setCellStyle(estiloCorpoEsquerda);
        		linhaSheet.getCell(12).setCellValue(dadosRelatorio.get(i).getValor8());
            	linhaSheet.getCell(12).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(13).setCellValue(dadosRelatorio.get(i).getValor9());
            	linhaSheet.getCell(13).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(14).setCellValue(dadosRelatorio.get(i).getValor10());
            	linhaSheet.getCell(14).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(15).setCellValue(dadosRelatorio.get(i).getValor11());
            	linhaSheet.getCell(15).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(16).setCellValue(dadosRelatorio.get(i).getValor12());
            	linhaSheet.getCell(16).setCellStyle(estiloCorpoEsquerda);
        		linhaSheet.getCell(17).setCellValue(dadosRelatorio.get(i).getValor13());
            	linhaSheet.getCell(17).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(18).setCellValue(dadosRelatorio.get(i).getValor14());
            	linhaSheet.getCell(18).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(19).setCellValue(dadosRelatorio.get(i).getValor15());
            	linhaSheet.getCell(19).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(20).setCellValue(dadosRelatorio.get(i).getValor16());
            	linhaSheet.getCell(20).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(21).setCellValue(dadosRelatorio.get(i).getValor17());
            	linhaSheet.getCell(21).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(22).setCellValue(dadosRelatorio.get(i).getValor18());
            	linhaSheet.getCell(22).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(23).setCellValue(dadosRelatorio.get(i).getValor19());
            	linhaSheet.getCell(23).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(24).setCellValue(dadosRelatorio.get(i).getValor20());
            	linhaSheet.getCell(24).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(25).setCellValue(dadosRelatorio.get(i).getValor21());
            	linhaSheet.getCell(25).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(26).setCellValue(dadosRelatorio.get(i).getValor22());
            	linhaSheet.getCell(26).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(27).setCellValue(dadosRelatorio.get(i).getValor23());
            	linhaSheet.getCell(27).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(28).setCellValue(dadosRelatorio.get(i).getTotal());
            	linhaSheet.getCell(28).setCellStyle(estiloCorpoEsquerda);
            	
            }
        }
        
//        int qtdeLinhas = dadosRelatorio.size()+8+2;
//        Row linhaGerador = sheet.createRow((short)(qtdeLinhas));
//        sheet.addMergedRegion(new CellRangeAddress(qtdeLinhas, qtdeLinhas, 0, 10));
//        linhaGerador.getCell(0).setCellValue(userGerador);
        
		sheet = wb.getSheetAt(0);

        // Salvando o arquivo
        ServletOutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
	}
}
