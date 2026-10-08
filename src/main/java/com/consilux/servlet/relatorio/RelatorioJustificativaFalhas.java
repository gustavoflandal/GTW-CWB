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
import com.consilux.model.medicao.DadosEficienciaEquipamento;
import com.consilux.model.medicao.ItemEficienciaEquipamento;

/**
 * Servlet para a geração de um relatório de Justificativa de Falhas
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 12/07/2016
 */
public class RelatorioJustificativaFalhas extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatorioEquipamentoInconsistencias.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
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
		String strCodPista = request.getParameter("local").equals("0") ? "" : request.getParameter("local");
		String strCodPistaProdam = request.getParameter("equipamento").equals("0") ? "" : request.getParameter("equipamento");
		String strEnquadramento = request.getParameter("enquadramento").equals("") ? null : request.getParameter("enquadramento");
		
		
		Date dtDataInicio = null;
		Date dtDataFim = null;
		
		Integer intCodPista = null;
		Integer intCodPistaProdam = null;

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
			dtDataInicio = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(strDataInicio + " 00:00:00");
			dtDataFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(strDataFim + " 23:59:59");
			
			Calendar calendarioInicio = Calendar.getInstance();
			calendarioInicio.setTime(dtDataInicio);
			Calendar calendarioFim = Calendar.getInstance();
			calendarioFim.setTime(dtDataFim);
			
			if (calendarioInicio.after(calendarioFim)) {
				new Mensagem(response).showErro("A Data Início deve ser menor ou igual da Data Fim!", "javascript:window.close();");
				return; 
			}
			
			if(!(strCodPista.equals(""))){
				if (!ExpValida.NATURAL.validar(strCodPista)) {
					new Mensagem(response).showErro("Identificador de Local Prodam enviado inválido!", "javascript:window.close();");
					return;
				} else {
					intCodPista = Integer.parseInt(strCodPista);
				}
			}
			
			if(!(strCodPistaProdam.equals(""))){
				if (!ExpValida.NATURAL.validar(strCodPistaProdam)) {
					new Mensagem(response).showErro("Identificador de Equipamento Prodam enviado inválido!", "javascript:window.close();");
					return;
				} else {
					intCodPistaProdam = Integer.parseInt(strCodPistaProdam);
				}
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
	        String nomeArquivo = "Relatório de Eficiência do Equipamento - Falhas";
	        
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + ".xls\"");  			
 
	        // Criando area de trabalho para o excel
	        HSSFWorkbook wb = new HSSFWorkbook();
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        HSSFSheet sheet = wb.createSheet("Falhas"); 
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
            
            HSSFFont f2 = wb.createFont();
            f2.setFontHeightInPoints((short) 10);
            f2.setBold(true);
            f2.setFontName("Calibri");
            f2.setColor(HSSFFont.COLOR_NORMAL);
            HSSFCellStyle estiloCabelcalhoFiltro;
            
            HSSFFont f3 = wb.createFont();
            f3.setFontHeightInPoints((short) 10);
            f3.setFontName("Calibri");
            f3.setColor(HSSFFont.COLOR_NORMAL);
            HSSFCellStyle estiloFiltro;
            
            estiloCabelcalhoFiltro = wb.createCellStyle();
            estiloCabelcalhoFiltro.setAlignment(HorizontalAlignment.LEFT);
            estiloCabelcalhoFiltro.setVerticalAlignment(VerticalAlignment.CENTER);
            estiloCabelcalhoFiltro.setBorderBottom(BorderStyle.MEDIUM);
            estiloCabelcalhoFiltro.setBorderTop(BorderStyle.MEDIUM);
            estiloCabelcalhoFiltro.setBorderRight(BorderStyle.MEDIUM);
            estiloCabelcalhoFiltro.setBorderLeft(BorderStyle.MEDIUM);
            estiloCabelcalhoFiltro.setFont(f2);
            
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
            titulo.getCell(0).setCellValue("EFICIÊNCIA DO EQUIPAMENTO - FALHAS");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
            titulo.getCell(0).setCellStyle(estilo);
            
            Row titulo2 = sheet.createRow(1);
            titulo2.getCell(0).setCellValue("CONSÓRCIO LCL 06/2014-SMT - " + strDataInicioFormatada + " à " + strDataFimFormatada);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 11));
            titulo2.getCell(0).setCellStyle(estilo);
            
            //Filtros informados
            Row cabecalhoFiltro = sheet.createRow(3);
            cabecalhoFiltro.getCell(0).setCellValue("Filtros");
            cabecalhoFiltro.getCell(0).setCellStyle(estiloCabelcalhoFiltro);
            cabecalhoFiltro.getCell(1).setCellValue("");
            cabecalhoFiltro.getCell(1).setCellStyle(estiloCabelcalhoFiltro);
            cabecalhoFiltro.getCell(2).setCellValue("");
            cabecalhoFiltro.getCell(2).setCellStyle(estiloCabelcalhoFiltro);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 2));
            
            Row filtroLocalProdam = sheet.createRow(4);
            filtroLocalProdam.getCell(0).setCellValue("Local");
            filtroLocalProdam.getCell(0).setCellStyle(estiloFiltro);
            filtroLocalProdam.getCell(1).setCellValue("");
            filtroLocalProdam.getCell(1).setCellStyle(estiloFiltro);
            filtroLocalProdam.getCell(2).setCellValue(intCodPista == null ? "NÃO" : "SIM");
            filtroLocalProdam.getCell(2).setCellStyle(estiloFiltro);
            sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 1));
            
            Row filtroEquipProdam = sheet.createRow(5);
            filtroEquipProdam.getCell(0).setCellValue("Equipamento");
            filtroEquipProdam.getCell(0).setCellStyle(estiloFiltro);
            filtroEquipProdam.getCell(1).setCellValue("");
            filtroEquipProdam.getCell(1).setCellStyle(estiloFiltro);
            filtroEquipProdam.getCell(2).setCellValue(intCodPistaProdam == null ? "NÃO" : "SIM");
            filtroEquipProdam.getCell(2).setCellStyle(estiloFiltro);
            sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 1));
            
            Row filtroEnquadramento = sheet.createRow(6);
            filtroEnquadramento.getCell(0).setCellValue("Enquadramento");
            filtroEnquadramento.getCell(0).setCellStyle(estiloFiltro);
            filtroEnquadramento.getCell(1).setCellValue("");
            filtroEnquadramento.getCell(1).setCellStyle(estiloFiltro);
            filtroEnquadramento.getCell(2).setCellValue(strEnquadramento == null ? "NÃO" : "SIM");
            filtroEnquadramento.getCell(2).setCellStyle(estiloFiltro);
            sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 1));
            
            Row filtroDataInicio = sheet.createRow(7);
            filtroDataInicio.getCell(0).setCellValue("Data Início");
            filtroDataInicio.getCell(0).setCellStyle(estiloFiltro);
            filtroDataInicio.getCell(1).setCellValue("");
            filtroDataInicio.getCell(1).setCellStyle(estiloFiltro);
            filtroDataInicio.getCell(2).setCellValue(strDataInicioFormatada);
            filtroDataInicio.getCell(2).setCellStyle(estiloFiltro);
            sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 1));
            
            Row filtroDataFim = sheet.createRow(8);
            filtroDataFim.getCell(0).setCellValue("Data Fim");
            filtroDataFim.getCell(0).setCellStyle(estiloFiltro);
            filtroDataFim.getCell(1).setCellValue("");
            filtroDataFim.getCell(1).setCellStyle(estiloFiltro);
            filtroDataFim.getCell(2).setCellValue(strDataFimFormatada);
            filtroDataFim.getCell(2).setCellStyle(estiloFiltro);
            sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, 1));
            
            GerarRelatorio(dtDataInicio, dtDataFim, intCodPista, intCodPistaProdam, strEnquadramento, response, sheet, wb, userGerador, nomeArquivo, idUsuario, data, usuarioURL);

			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}

	
	public void GerarRelatorio(Date dtDataInicio, Date dtDataFim, Integer intCodPista, Integer intCodPistaProdam, String strEnquadramento,
							   HttpServletResponse response, HSSFSheet sheet, HSSFWorkbook wb, String userGerador,
					 		   String nomeArquivo, Integer idUsuario, Date data, String usuarioURL
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
        //Fonte para CABEÇALHO do excel
		HSSFFont f4 = wb.createFont();
        f4.setFontHeightInPoints((short) 10);
        f4.setBold(true);
        f4.setFontName("Calibri");
        f4.setColor(HSSFFont.COLOR_NORMAL);
        
        HSSFColor corFundo = Funcoes.setColor(wb, (byte) 255, (byte)255,(byte) 255);
        
        HSSFCellStyle estiloCabecalhoCorpo;
        estiloCabecalhoCorpo = wb.createCellStyle();
        estiloCabecalhoCorpo.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setFont(f4);
        
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
        
        HSSFFont f5 = wb.createFont();
        f5.setFontHeightInPoints((short) 10);
        f5.setFontName("Calibri");
        f5.setColor(HSSFFont.COLOR_NORMAL);
        
        HSSFCellStyle estiloCorpo;
        estiloCorpo = wb.createCellStyle();
        estiloCorpo.setAlignment(HorizontalAlignment.CENTER);
        estiloCorpo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCorpo.setFont(f5);
        
		//Criando as colunas do corpo
		Row cabecalho = sheet.createRow((short)10);
		Row cabecalho2 = sheet.createRow((short)11);
		
		cabecalho.getCell(0).setCellValue("Data");
	    cabecalho.getCell(0).setCellStyle(estiloCabecalhoCorpo);
	    cabecalho2.getCell(0).setCellValue("");
	    cabecalho2.getCell(0).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(0, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(10, 11, 0, 0));
	    
	    cabecalho.getCell(1).setCellValue("Local");
		cabecalho.getCell(1).setCellStyle(estiloCabecalhoCorpo);
		cabecalho.getCell(2).setCellValue("");
		cabecalho.getCell(2).setCellStyle(estiloCabecalhoCorpo);
		sheet.addMergedRegion(new CellRangeAddress(10, 10, 1, 2));
	    cabecalho2.getCell(1).setCellValue("Cód.");
	    cabecalho2.getCell(1).setCellStyle(estiloCabecalhoCorpo);
		sheet.setColumnWidth(1, 1500);
		cabecalho2.getCell(2).setCellValue("Descrição");
	    cabecalho2.getCell(2).setCellStyle(estiloCabecalhoCorpo);
		sheet.setColumnWidth(2, 16000);
				
		cabecalho.getCell(3).setCellValue("Equip.");
		cabecalho.getCell(3).setCellStyle(estiloCabecalhoCorpo2);
	    cabecalho.getCell(4).setCellValue("");
	    cabecalho.getCell(4).setCellStyle(estiloCabecalhoCorpo2);
	    cabecalho2.getCell(3).setCellValue("(Faixa)");
		cabecalho2.getCell(3).setCellStyle(estiloCabecalhoCorpo3);
	    cabecalho2.getCell(4).setCellValue("");
	    cabecalho2.getCell(4).setCellStyle(estiloCabecalhoCorpo3);
	    sheet.setColumnWidth(3, 1500);
	    sheet.setColumnWidth(4, 1000);
	    sheet.addMergedRegion(new CellRangeAddress(10, 10, 3, 4));
	    sheet.addMergedRegion(new CellRangeAddress(11, 11, 3, 4));
	    
	    cabecalho.getCell(5).setCellValue("Data Início");
		cabecalho.getCell(5).setCellStyle(estiloCabecalhoCorpo2);
	    cabecalho2.getCell(5).setCellValue("Operação");
	    cabecalho2.getCell(5).setCellStyle(estiloCabecalhoCorpo3);
	    sheet.setColumnWidth(5, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(10, 10, 5, 5));
	    sheet.addMergedRegion(new CellRangeAddress(11, 11, 5, 5));
	    
	    cabecalho.getCell(6).setCellValue("Enquadramento");
	    cabecalho.getCell(6).setCellStyle(estiloCabecalhoCorpo2);
	    cabecalho2.getCell(6).setCellValue("Habilitado");
	    cabecalho2.getCell(6).setCellStyle(estiloCabecalhoCorpo3);
	    sheet.setColumnWidth(6, 6000);
	    sheet.addMergedRegion(new CellRangeAddress(10, 10, 6, 6));
	    sheet.addMergedRegion(new CellRangeAddress(11, 11, 6, 6));

	    cabecalho.getCell(7).setCellValue("Enquadramento");
		cabecalho.getCell(7).setCellStyle(estiloCabecalhoCorpo2);
	    cabecalho2.getCell(7).setCellValue("Efetivo");
	    cabecalho2.getCell(7).setCellStyle(estiloCabecalhoCorpo3);
	    sheet.setColumnWidth(7, 5000);
	    sheet.addMergedRegion(new CellRangeAddress(10, 10, 7, 7));
	    sheet.addMergedRegion(new CellRangeAddress(11, 11, 7, 7));
	    
	    cabecalho.getCell(8).setCellValue("Falhas");
		cabecalho.getCell(8).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(8, 3000);
	    cabecalho.getCell(9).setCellValue("");
		cabecalho.getCell(9).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(9, 8000);
	    cabecalho.getCell(10).setCellValue("");
		cabecalho.getCell(10).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(10, 3000);
	    cabecalho.getCell(11).setCellValue("");
		cabecalho.getCell(11).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(11, 4000);
	    sheet.addMergedRegion(new CellRangeAddress(10, 10, 8, 11));
	    
	    cabecalho2.getCell(8).setCellValue("Início");
		cabecalho2.getCell(8).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(8, 3000);
	    cabecalho2.getCell(9).setCellValue("Justificativa");
		cabecalho2.getCell(9).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(9, 8000);
	    cabecalho2.getCell(10).setCellValue("Previsão ou Término");
		cabecalho2.getCell(10).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(10, 3000);
	    cabecalho2.getCell(11).setCellValue("");
		cabecalho2.getCell(11).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(11, 4000);
	    sheet.addMergedRegion(new CellRangeAddress(11, 11, 10, 11));
        
        //Buscando informações para popular planilhas
		DadosEficienciaEquipamento dadosEficienciaEquipamento = new DadosEficienciaEquipamento();
		ArrayList<ItemEficienciaEquipamento> dadosRelatorio = dadosEficienciaEquipamento.justificativaFalhas(dtDataInicio, dtDataFim, intCodPista, intCodPistaProdam, strEnquadramento);
		
        //Fonte para corpo do excel
		HSSFFont f3 = wb.createFont();
        f3.setFontHeightInPoints((short) 8);
        HSSFCellStyle estiloItens;
        estiloItens = wb.createCellStyle();
        estiloItens.setBorderBottom(BorderStyle.MEDIUM);
        estiloItens.setBorderTop(BorderStyle.MEDIUM);
        estiloItens.setBorderRight(BorderStyle.MEDIUM);
        estiloItens.setBorderLeft(BorderStyle.MEDIUM);
        estiloItens.setFont(f3);
        
        if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)12);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        }else{
            for(int i=0; i<dadosRelatorio.size(); i++){
            	
               	Row linhaSheet = sheet.createRow((i+12));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getDia());
            	linhaSheet.getCell(0).setCellStyle(estiloCorpo);
            	linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getCodProdamLocal());
            	linhaSheet.getCell(1).setCellStyle(estiloCorpo);
            	linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getDescricaoLocal());
            	linhaSheet.getCell(2).setCellStyle(estiloItens);
            	if (dadosRelatorio.get(i).getCodProdamFaixa() == 0) {
            		linhaSheet.getCell(3).setCellValue("N/D");
            	} else {
            		linhaSheet.getCell(3).setCellValue(dadosRelatorio.get(i).getCodProdamFaixa());
            	}
            	linhaSheet.getCell(3).setCellStyle(estiloCorpo);
            	linhaSheet.getCell(4).setCellValue(dadosRelatorio.get(i).getFaixa());
            	linhaSheet.getCell(4).setCellStyle(estiloCorpo);
            	linhaSheet.getCell(5).setCellValue(dadosRelatorio.get(i).getDataInicioOperacao());
            	linhaSheet.getCell(5).setCellStyle(estiloCorpo);
            	linhaSheet.getCell(6).setCellValue(dadosRelatorio.get(i).getEnquadramentoHabilitado());
            	linhaSheet.getCell(6).setCellStyle(estiloCorpo);
            	linhaSheet.getCell(7).setCellValue(dadosRelatorio.get(i).getEnquadramentoEfetivo());
            	linhaSheet.getCell(7).setCellStyle(estiloCorpo);
            	
            	//Falhas
            	//Início
            	linhaSheet.getCell(8).setCellValue(dadosRelatorio.get(i).getDataInicioManut());
            	linhaSheet.getCell(8).setCellStyle(estiloCorpo);
            	//Justificativa
            	linhaSheet.getCell(9).setCellValue(dadosRelatorio.get(i).getDescricaoManut());
            	linhaSheet.getCell(9).setCellStyle(estiloItens);
            	//Data Fim
            	linhaSheet.getCell(10).setCellValue(dadosRelatorio.get(i).getDataFimManut());
            	linhaSheet.getCell(10).setCellStyle(estiloCorpo);
            	//Previsão ou Término
            	linhaSheet.getCell(11).setCellValue(dadosRelatorio.get(i).getEstadoManut());
            	linhaSheet.getCell(11).setCellStyle(estiloCorpo);
            	
            }
        }
        
        int qtdeLinhas = dadosRelatorio.size()+12+2;
        Row linhaGerador = sheet.createRow(qtdeLinhas);
        sheet.addMergedRegion(new CellRangeAddress(qtdeLinhas, qtdeLinhas, 0, 11));
        linhaGerador.getCell(0).setCellValue(userGerador);
        
		sheet = wb.getSheetAt(0);

        // Salvando o arquivo
        ServletOutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
	}
}
