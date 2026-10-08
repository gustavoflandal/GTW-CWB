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
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellRangeAddress;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.exception.ModelException;
import com.consilux.model.medicao.DadosMedicaoPMESP;
import com.consilux.model.medicao.ItemMedicaoPMESP;

/**
 * Servlet implementation class RelatorioPMESPEnvioPlacas
 */
public class RelatorioPMESPEnvioPlacas extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RelatorioPMESPEnvioPlacas.class);

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
        String userGerador = "Relatório gerado em: " + formatador.format(data);
		
		String dataIni = request.getParameter("dataini");
		String dataFinal = request.getParameter("datafim");

		Date dtIni = null;
		Date dtFim = null;

		Calendar cal = Calendar.getInstance();

		//Validações Necessárias para montar relatório
		try {
			dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dataIni+" 00:00:00");
			dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dataFinal+" 23:59:59");
			
			cal.setTime(dtIni);
			cal.add(Calendar.DATE, 31);
			
			if (cal.before(dtFim)) {
				new Mensagem(response).showErro("O Período pode ser no máximo de 31 dias!", "javascript:window.close();");
				return; 
			}
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		cal.setTime(dtFim);
		Integer numDias = cal.get(Calendar.DATE);
		Calendar calIni = Calendar.getInstance();
		calIni.setTime(dtIni);
		Calendar calFim = Calendar.getInstance();
		calFim.setTime(dtFim);
		
		if (calIni.get(Calendar.MONTH) != calFim.get(Calendar.MONTH)) { //Se trocou o mês então adiciona a diferença em dias...
			calIni.add(Calendar.MONTH, 1);
			calIni.add(Calendar.DATE, -1); //ultimo dia do mês...
			numDias += calIni.get(Calendar.DATE);
		}
		
		try {
			
			//Buscar os dados necessários do relatório
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat strYear = new SimpleDateFormat("yyyy");
			SimpleDateFormat strMonth = new SimpleDateFormat("MM");
			Long ano = Long.valueOf(strYear.format(dtIni));  
			Long mes = Long.valueOf(strMonth.format(dtIni)); 
			String strDataInicio = sdf.format(dtIni);
			String strDataFim = sdf.format(dtFim);
			
			// Criando o arquivo fisico
	        String nomeArquivo = "Relatorio PMESP - Envio de Placas - Consorcio LCL 06-2014-SMT de 0" + 
	        					 String.valueOf(mes) + "-" +
	        					 String.valueOf(ano);
	        
	        String tipoArquivo = "PM";
	        
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + ".xls\"");  			
 
	        // Criando area de trabalho para o excel
	        HSSFWorkbook wb = new HSSFWorkbook();
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        HSSFSheet sheet = wb.createSheet("Envio de Placas PMESP"); 
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
            estilo.setFont(f);
            
	        //Cria uma linha na Planilha.
            Row titulo = sheet.createRow(0);
            titulo.getCell(0).setCellValue("RELATÓRIO DE ENVIO DE PLACAS PMESP");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 16));
            titulo.getCell(0).setCellStyle(estilo);
            
            Row titulo2 = sheet.createRow(1);
            titulo2.getCell(0).setCellValue("CONSÓRCIO LCL 06/2014-SMT - " + strDataInicio + " à " + strDataFim);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 16));
            titulo2.getCell(0).setCellStyle(estilo);

            GerarRelatorio(dtIni, dtFim, mes, ano, response, sheet, wb, 
            			   userGerador, tipoArquivo, nomeArquivo, 
            			   idUsuario, data, usuarioURL);

			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	

	public void GerarRelatorio(Date dtIni, Date dtFim, Long mes, Long ano,  
					 		   HttpServletResponse response,
					 		   HSSFSheet sheet, HSSFWorkbook wb, String userGerador,
					 		   String tipoArquivo, String nomeArquivo,
					 		   Integer idUsuario, Date data, String usuarioURL) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
        //Fonte para CABEÇALHO do excel
		HSSFFont f2 = wb.createFont();
        f2.setFontHeightInPoints((short) 8);
        f2.setBold(true);
        f2.setFontName("Arial");
        f2.setColor(HSSFFont.COLOR_NORMAL);
        
        HSSFColor corFundo = Funcoes.setColor(wb, (byte) 255, (byte)255,(byte) 255);
        
        HSSFCellStyle estiloCabecalhoCorpo;
        estiloCabecalhoCorpo = wb.createCellStyle();
        estiloCabecalhoCorpo.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setFont(f2);
        
        HSSFCellStyle estiloCabecalhoCorpo2;
        estiloCabecalhoCorpo2 = wb.createCellStyle();
        estiloCabecalhoCorpo2.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpo2.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo2.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setFont(f2);
        estiloCabecalhoCorpo2.setFillForegroundColor(corFundo.getIndex());
        estiloCabecalhoCorpo2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        HSSFCellStyle estiloCabecalhoCorpo3;
        estiloCabecalhoCorpo3 = wb.createCellStyle();
        estiloCabecalhoCorpo3.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpo3.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo3.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo3.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo3.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo3.setFont(f2);
        estiloCabecalhoCorpo3.setFillForegroundColor(corFundo.getIndex());
        estiloCabecalhoCorpo3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
		//Criando as colunas do corpo
		Row cabecalho = sheet.createRow((short)4);
		Row cabecalho2 = sheet.createRow((short)5);
		
		cabecalho.getCell(0).setCellValue("Item");
	    cabecalho.getCell(0).setCellStyle(estiloCabecalhoCorpo);
	    cabecalho2.getCell(0).setCellValue("");
	    cabecalho2.getCell(0).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(0, 1800);
	    sheet.addMergedRegion(new CellRangeAddress(4, 5, 0, 0));
	    
	    cabecalho.getCell(1).setCellValue("Serie Equipamento");
		cabecalho.getCell(1).setCellStyle(estiloCabecalhoCorpo);
		cabecalho2.getCell(1).setCellValue("");
	    cabecalho2.getCell(1).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(1, 4200);
	    sheet.addMergedRegion(new CellRangeAddress(4, 5, 1, 1));
	    
	    cabecalho.getCell(2).setCellValue("Cod.Local");
	    cabecalho.getCell(2).setCellStyle(estiloCabecalhoCorpo);
	    cabecalho2.getCell(2).setCellValue("");
	    cabecalho2.getCell(2).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(2, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(4, 5, 2, 2));
	    
	    cabecalho.getCell(3).setCellValue("Cod.Faixa");
	    cabecalho.getCell(3).setCellStyle(estiloCabecalhoCorpo);
	    cabecalho2.getCell(3).setCellValue("");
	    cabecalho2.getCell(3).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(3, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(4, 5, 3, 3));
	    
	    cabecalho.getCell(4).setCellValue("Faixa");
	    cabecalho.getCell(4).setCellStyle(estiloCabecalhoCorpo);
	    cabecalho2.getCell(4).setCellValue("");
	    cabecalho2.getCell(4).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(4, 1800);
	    sheet.addMergedRegion(new CellRangeAddress(4, 5, 4, 4));
	    
	    cabecalho.getCell(5).setCellValue("Descrição do Local");
		cabecalho.getCell(5).setCellStyle(estiloCabecalhoCorpo);
		cabecalho2.getCell(5).setCellValue("");
	    cabecalho2.getCell(5).setCellStyle(estiloCabecalhoCorpo);
		sheet.setColumnWidth(5, 18400);
		sheet.addMergedRegion(new CellRangeAddress(4, 5, 5, 5));
		
		for(int i = 0; i < 31; i++){
			if (i == 0) {
				cabecalho.getCell(i+6).setCellValue("Dia (% do total)");
			} else {
				cabecalho.getCell(i+6).setCellValue("");
			}
            cabecalho.getCell(i+6).setCellStyle(estiloCabecalhoCorpo);
        	cabecalho2.getCell(i+6).setCellValue(Integer.toString(i+1));	
            cabecalho2.getCell(i+6).setCellStyle(estiloCabecalhoCorpo);
        }
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 6, 36));
		
	    cabecalho.getCell(37).setCellValue("Dias >");
		cabecalho.getCell(37).setCellStyle(estiloCabecalhoCorpo2);
	    cabecalho2.getCell(37).setCellValue("90%");
	    cabecalho2.getCell(37).setCellStyle(estiloCabecalhoCorpo3);
	    sheet.setColumnWidth(37, 1800);
	    
//	    cabecalho.getCell(38).setCellValue("Placas enviadas");
//		cabecalho.getCell(38).setCellStyle(estiloCabecalhoCorpo2);
//	    cabecalho2.getCell(38).setCellValue("t <= 4s");
//	    cabecalho2.getCell(38).setCellStyle(estiloCabecalhoCorpo3);
//	    sheet.setColumnWidth(38, 3500);
//	    
//	    cabecalho.getCell(39).setCellValue("Placas enviadas");
//		cabecalho.getCell(39).setCellStyle(estiloCabecalhoCorpo2);
//	    cabecalho2.getCell(39).setCellValue("t > 4s");
//	    cabecalho2.getCell(39).setCellStyle(estiloCabecalhoCorpo3);
//	    sheet.setColumnWidth(39, 3500);

	    cabecalho.getCell(38).setCellValue("Total");
		cabecalho.getCell(38).setCellStyle(estiloCabecalhoCorpo2);
	    cabecalho2.getCell(38).setCellValue("Placas");
	    cabecalho2.getCell(38).setCellStyle(estiloCabecalhoCorpo3);
	    sheet.setColumnWidth(38, 1800);
	    
//	    cabecalho.getCell(41).setCellValue("Placas enviadas");
//		cabecalho.getCell(41).setCellStyle(estiloCabecalhoCorpo2);
//	    cabecalho2.getCell(41).setCellValue("t <= 4s");
//	    cabecalho2.getCell(41).setCellStyle(estiloCabecalhoCorpo3);
//	    sheet.setColumnWidth(41, 3500);
//	    
//	    cabecalho.getCell(42).setCellValue("Placas enviadas");
//		cabecalho.getCell(42).setCellStyle(estiloCabecalhoCorpo2);
//	    cabecalho2.getCell(42).setCellValue("t > 4s");
//	    cabecalho2.getCell(42).setCellStyle(estiloCabecalhoCorpo3);
//	    sheet.setColumnWidth(42, 3500);

		//Buscando informações para popular planilhas
		DadosMedicaoPMESP dadosMedicao = new DadosMedicaoPMESP();
		ArrayList<ItemMedicaoPMESP> dadosRelatorio = dadosMedicao.relatorioEnvioPlacasPMESP(mes, ano);
		
        //Fonte para corpo do excel
        HSSFFont f3 = wb.createFont();
        f3.setFontHeightInPoints((short) 8);
        f3.setFontName("Arial");
        f3.setColor(HSSFFont.COLOR_NORMAL);
        
        HSSFCellStyle estiloItensCentro;
        estiloItensCentro = wb.createCellStyle();
        estiloItensCentro.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentro.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentro.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentro.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentro.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentro.setFont(f3);
        
        HSSFCellStyle estiloItensEsquerda;
        estiloItensEsquerda = wb.createCellStyle();
        estiloItensEsquerda.setAlignment(HorizontalAlignment.LEFT);
        estiloItensEsquerda.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensEsquerda.setBorderTop(BorderStyle.MEDIUM);
        estiloItensEsquerda.setBorderRight(BorderStyle.MEDIUM);
        estiloItensEsquerda.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensEsquerda.setFont(f3);
        
        HSSFCellStyle porcentagem;
        porcentagem = wb.createCellStyle();
        porcentagem.setAlignment(HorizontalAlignment.CENTER);
        porcentagem.setBorderBottom(BorderStyle.MEDIUM);
        porcentagem.setBorderTop(BorderStyle.MEDIUM);
        porcentagem.setBorderRight(BorderStyle.MEDIUM);
        porcentagem.setBorderLeft(BorderStyle.MEDIUM);
        porcentagem.setDataFormat(wb.createDataFormat().getFormat("0%"));
        porcentagem.setFont(f3);
        
        HSSFCellStyle porcentagem_vermelho;
        porcentagem_vermelho = wb.createCellStyle();
        porcentagem_vermelho.setAlignment(HorizontalAlignment.CENTER);
        porcentagem_vermelho.setBorderBottom(BorderStyle.MEDIUM);
        porcentagem_vermelho.setBorderTop(BorderStyle.MEDIUM);
        porcentagem_vermelho.setBorderRight(BorderStyle.MEDIUM);
        porcentagem_vermelho.setBorderLeft(BorderStyle.MEDIUM);
        porcentagem_vermelho.setFillForegroundColor(IndexedColors.RED.index);
        porcentagem_vermelho.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        porcentagem_vermelho.setDataFormat(wb.createDataFormat().getFormat("0%"));
        porcentagem_vermelho.setFont(f3);
        
        
        if(dadosRelatorio.size() == 0) {
        	Row linhaSemInfo = sheet.createRow((short)6);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados neste intervalo de datas!");
        } else {
            
        	for(int i=0; i<dadosRelatorio.size(); i++) {
            	
            	Row linhaSheet = sheet.createRow((short)(i+6));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getItem());
            	linhaSheet.getCell(0).setCellStyle(estiloItensCentro);
            	linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getSerieEquipamento());
            	linhaSheet.getCell(1).setCellStyle(estiloItensCentro);
            	linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getCodLocalProdam());
            	linhaSheet.getCell(2).setCellStyle(estiloItensCentro);
            	linhaSheet.getCell(3).setCellValue(dadosRelatorio.get(i).getCodEquipamentoProdam());
            	linhaSheet.getCell(3).setCellStyle(estiloItensCentro);
            	linhaSheet.getCell(4).setCellValue(dadosRelatorio.get(i).getFaixaTarja());
            	linhaSheet.getCell(4).setCellStyle(estiloItensCentro);
            	linhaSheet.getCell(5).setCellValue(dadosRelatorio.get(i).getDescricaoLocal());
            	linhaSheet.getCell(5).setCellStyle(estiloItensEsquerda);
            	
            	String s_porcentagem;
            	Integer i_porcentagem;
            	Double d_porcentagem;
            	
            	String strDataItem = "";
            	Date dataItem = null;
            	Integer aux = 0;
            	
            	Date dataAtual = new Date();
            	Calendar calDataAtual = Calendar.getInstance();
            	calDataAtual.setTime(dataAtual);
            	Calendar calDataItem = Calendar.getInstance();
            	
            	for(int h = 0; h < 31; h++){
            		aux = h+1;
            		strDataItem = (aux < 10 ? "0" + String.valueOf((h+1)) : String.valueOf((h+1))) + "/" + (mes < 10 ? "0" + String.valueOf(mes) : String.valueOf(mes)) + "/" + String.valueOf(ano);
            		dataItem = new SimpleDateFormat("dd/MM/yyyy").parse(strDataItem);
            		calDataItem.setTime(dataItem);
            		
            		s_porcentagem = dadosRelatorio.get(i).getCelulas()[h];
            		
            		if (s_porcentagem != null) {
            			i_porcentagem = Integer.parseInt(s_porcentagem.replace("%", ""));
            			d_porcentagem = i_porcentagem / 100.0;
	        		   
            			linhaSheet.getCell(h+6).setCellValue(d_porcentagem);
	        		   
            			if (calDataItem.before(calDataAtual) && i_porcentagem < 90) {
            				linhaSheet.getCell(h+6).setCellStyle(porcentagem_vermelho);
            			} else {
            				linhaSheet.getCell(h+6).setCellStyle(porcentagem);
            			}
        			}
        		}
            	
            	linhaSheet.getCell(37).setCellValue(dadosRelatorio.get(i).getDiasOK());
            	linhaSheet.getCell(37).setCellStyle(estiloItensCentro);
//            	linhaSheet.getCell(38).setCellValue(dadosRelatorio.get(i).getQtdePlacasAte4s());
//            	linhaSheet.getCell(38).setCellStyle(estiloItensCentro);
//            	linhaSheet.getCell(39).setCellValue(dadosRelatorio.get(i).getQtdePlacasAcima4s());
//            	linhaSheet.getCell(39).setCellStyle(estiloItensCentro);
            	linhaSheet.getCell(38).setCellValue(dadosRelatorio.get(i).getTotalPlacas());
            	linhaSheet.getCell(38).setCellStyle(estiloItensCentro);
//            	linhaSheet.getCell(41).setCellValue(dadosRelatorio.get(i).getPorcentagemPlacasAte4s() / 100.0);
//            	linhaSheet.getCell(41).setCellStyle(porcentagem);
//            	linhaSheet.getCell(42).setCellValue(dadosRelatorio.get(i).getPorcentagemPlacasAcima4s() / 100.0);
//            	linhaSheet.getCell(42).setCellStyle(porcentagem);
            	
        	}
    	}
        
        //Setando tamanho automatico das colunas
        for(int i = 0; i < 31; i++){
        	sheet.setColumnWidth(i+6, 1200);
        }

//        int qtdeLinhas = dadosRelatorio.size()+6+2;
//        Row linhaGerador = sheet.createRow((short)(qtdeLinhas));
//        sheet.addMergedRegion(new CellRangeAddress(qtdeLinhas, qtdeLinhas, 0, 7));
//        linhaGerador.getCell(0).setCellValue(userGerador);
        
		sheet = wb.getSheetAt(0);

        //Gravando dados do histórico da geração do relatório
//		DadosMedicao gravaHistorico = new DadosMedicao();
//        try {
//        	if (!usuarioURL.equals("SISTEMA")) {
//        		gravaHistorico.gravarHistoricoGeracao(response, dtIni, dtFim, nomeArquivo, tipoArquivo, idUsuario, data);
//        	}
//        	
//		}catch (Exception e) {
//			e.printStackTrace();
//			throw new ModelException("ERRO ao gravar histórico", e);
//		}
		
        // Salvando o arquivo
        ServletOutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
	}
}
