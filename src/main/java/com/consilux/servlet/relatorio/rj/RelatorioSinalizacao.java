package com.consilux.servlet.relatorio.rj;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.ArquivoVideoBean;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.relatorio.rj.DadosRelatorioSinalizacao;

/**
 * Servlet implementation class RelatorioFuncionamento
 */
public class RelatorioSinalizacao extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RelatorioSinalizacao.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
//		String strMes = request.getParameter("mes");
//		String strAno = request.getParameter("ano");
		
//		Integer intMes = null, intAno = null;
		
//		SimpleDateFormat mesExtenso = new SimpleDateFormat("MMMM");
//		Date dtDataIni = null, dtDataFim = null;
		Calendar calendar = Calendar.getInstance();
		
		String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");
		String contrato = String.format("%03d", Integer.parseInt(numeroContrato)) + "/" + anoContrato;

		//Validações Necessárias para montar relatório

//		if((strMes.equals("")) || (strAno.equals(""))){
//			new Mensagem(response).showErro("O Mês e o Ano devem ser informados!", "javascript:window.close();");
//			return;
//		}
//		
//		if (!isDigit(strMes) || !isDigit(strAno)) {
//			new Mensagem(response).showErro("Favor informar os dados corretos de mês e ano! Somente números são válidos.", "javascript:window.close();");
//			return; 
//		}
			
		try {

//			intMes = Integer.parseInt(strMes);  
//			intAno = Integer.parseInt(strAno);

//			if(intMes < 1 || intMes > 12){
//				new Mensagem(response).showErro("Favor informar um valor de mês válido. Entre 1 e 12!", "javascript:window.close();");
//				return; 
//			}
//			if(intAno < 1900 || intAno > 2050){
//				new Mensagem(response).showErro("Favor informar um valor de ano válido. Maior que 1900 e menor que 2050!", "javascript:window.close();");
//				return; 
//			}
			
//			dtDataIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/" + (strMes.trim().length() == 1 ? "0"+strMes.trim() : strMes.trim()) +"/"+ strAno + " 00:00:00");

			calendar = Calendar.getInstance();
//	        calendar.setTime(dtDataIni);  

	        calendar.add(Calendar.MONTH, 1);  
	        calendar.set(Calendar.DAY_OF_MONTH, 1);  
	        calendar.add(Calendar.DATE, -1);  

//	        dtDataFim = calendar.getTime();

			
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		try {
			
			// Criando o arquivo fisico
	        String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_RelatorioSinalizacao.xlsx";
	        
	        response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");  			
 
	        // Criando area de trabalho para o excel
			SXSSFWorkbook wb = new SXSSFWorkbook();
	        wb.setCompressTempFiles(true);
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        Sheet sheet = wb.createSheet("Videos Sinalizacao"); 
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
	        
	        ColecaoEstilos ce = new ColecaoEstilos(wb);
            
            //Filtros informados
            Row cabecalho1 = sheet.createRow(0);
            cabecalho1.getCell(0).setCellValue("RELATÓRIO DE VÍDEOS DE SINALIZAÇÃO");
            cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
            
            Row cabecalhoContrato = sheet.createRow(1);
            cabecalhoContrato.getCell(0).setCellValue("Nº DO CONTRATO:");
            cabecalhoContrato.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoContrato.getCell(1).setCellValue(contrato);
            cabecalhoContrato.getCell(1).setCellStyle(ce.estiloFiltro);
            
            //Gerar relatório por equipamento
            GerarRelatorio(3, sheet, wb, nomeArquivo, ce, false);
            
          //Cria uma planilha Excel
	        Sheet sheet1 = wb.createSheet("Imagens Sinalizacao"); 
            sheet1.setVerticallyCenter(true);
            sheet1.setHorizontallyCenter(true);
            
            //Filtros informados
            Row cabecalho11 = sheet1.createRow(0);
            cabecalho11.getCell(0).setCellValue("RELATÓRIO DE IMAGENS DE SINALIZAÇÃO");
            cabecalho11.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
            
            Row cabecalhoContrato1 = sheet1.createRow(1);
            cabecalhoContrato1.getCell(0).setCellValue("Nº DO CONTRATO:");
            cabecalhoContrato1.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoContrato1.getCell(1).setCellValue(contrato);
            cabecalhoContrato1.getCell(1).setCellStyle(ce.estiloFiltro);
            
            //Gerar relatório por equipamento
            GerarRelatorio(3, sheet1, wb, nomeArquivo, ce, true);
            
            {
	 	        // Salvando o arquivo
	 	        ServletOutputStream out = response.getOutputStream();
	 	        wb.write(out);
	 	        out.flush();
	 	        out.close();
	 	        wb.dispose();
	 	        
	 	        wb = null;
	 	        out = null;
	 	        response = null;
 	        }
            
			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}

	
	public int GerarRelatorio(int intLinha,
							  Sheet sheet, SXSSFWorkbook wb, 
					 		  String nomeArquivo, ColecaoEstilos ce, boolean imagens
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
	    
//    	Row cabecalho1 = sheet.createRow((short)intLinha++);
		Row cabecalho2 = sheet.createRow((short)intLinha++);
		Row cabecalho3 = sheet.createRow((short)intLinha++);
//		Row cabecalho4 = sheet.createRow((short)intLinha++);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		SimpleDateFormat formatoMesAno = new SimpleDateFormat("MM/yyyy");
//		Locale BRAZIL = new Locale("pt","BR");
//		SimpleDateFormat diaSemana = new SimpleDateFormat("EEEE",BRAZIL);
		
		int c = 0;
		
//		cabecalho1.getCell(c).setCellValue(formatoMesAno.format(dtDataIni));
//	    cabecalho1.getCell(c).setCellStyle(ce.estiloCabecalhoCorpo1);
	    
	    cabecalho2.getCell(c).setCellValue("Equipamento");
	    cabecalho2.getCell(c).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
//	    cabecalho3.getCell(c).setCellValue("Dia da Semana");
//	    cabecalho3.getCell(c).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(c).setCellValue("CÓDIGO");
	    cabecalho3.getCell(c).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(c, 4000);
	    
	    c++;
	    
//	    cabecalho1.getCell(c).setCellValue("");
//	    cabecalho1.getCell(c).setCellStyle(ce.estiloCabecalhoCorpo3);
	    
	    cabecalho2.getCell(c).setCellValue("Data");
	    cabecalho2.getCell(c).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
//	    cabecalho3.getCell(c).setCellValue("Dia da Semana");
//	    cabecalho3.getCell(c).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(c).setCellValue("EQUIPAMENTO");
	    cabecalho3.getCell(c).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(c, 4000);
	    
	    c++;
	    
//	    cabecalho1.getCell(c).setCellValue("");
//	    cabecalho1.getCell(c).setCellStyle(ce.estiloCabecalhoCorpo3);
	    
	    cabecalho2.getCell(c).setCellValue("Data");
	    cabecalho2.getCell(c).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
//	    cabecalho3.getCell(c).setCellValue("Dia da Semana");
//	    cabecalho3.getCell(c).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(c).setCellValue("INÍCIO OPERAÇÃO");
	    cabecalho3.getCell(c).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(c, 3000);
	    
	    c++;
	    
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), 0, 2));
//	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho3.getRowNum(), 0, 2));
	    
	    Integer celula = 3;
	    String strData = null;
//	    Date dtData = null, dtDataAtual = new Date();
	    
//	    Calendar calendarioData = Calendar.getInstance();
//	    calendarioData.setTime(dtDataIni);
//	    intUltimoDiaMes = calendarioData.getActualMaximum(Calendar.DATE);
//	    
//	    String coluna_ref_total = "AH";
//	    if (intUltimoDiaMes == 30)
//	    	coluna_ref_total = "AG";
//	    if (intUltimoDiaMes == 29)
//	    	coluna_ref_total = "AF";
//	    if (intUltimoDiaMes == 28)
//	    	coluna_ref_total = "AE";
	    
	    /// XXX
	    SimpleDateFormat mesExtenso = new SimpleDateFormat("MMMM/yyyy");
	    SimpleEntry<Date, Date> data_Videos = ArquivoVideoBean.ObterDataVideos();
	    List<Date> listaDatas = new ArrayList<Date>();
	    Calendar inicio = Calendar.getInstance();
	    inicio.setTime(data_Videos.getKey());
	    Calendar fim = Calendar.getInstance();
	    fim.setTime(data_Videos.getValue());
	    inicio.set(Calendar.DAY_OF_MONTH, 1);
	    fim.set(Calendar.DAY_OF_MONTH, 1);
	    inicio.set(Calendar.HOUR, 0);
	    fim.set(Calendar.HOUR, 0);
	    inicio.set(Calendar.MINUTE, 0);
	    fim.set(Calendar.MINUTE, 0);
	    inicio.set(Calendar.SECOND, 0);
	    fim.set(Calendar.SECOND, 1);
	   
	    while(inicio.before(fim)) {
	    	listaDatas.add(inicio.getTime());
	    	inicio.add(Calendar.MONTH, 1);
	    }
//	    listaDatas.add(fim.getTime());
	    
	    Integer intervalo_ini = celula;
	    for (Integer mes = 0; mes < listaDatas.size(); mes++) {
	    	
	    	strData = mesExtenso.format(listaDatas.get(mes));
//	    	dtData = sdf.parse(strData);
//	    	calendarioData.setTime(dtData);
	    	
//	    	cabecalho1.getCell(celula).setCellValue("");
//	    	cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);

	    	cabecalho3.getCell(celula).setCellValue(strData);
	    	cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    	
//	    	cabecalho3.getCell(celula).setCellValue(diaSemana.format(dtData));
//	    	cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    	
	    	if (imagens)
	    		cabecalho2.getCell(celula).setCellValue("Data das Imagens");
	    	else
	    		cabecalho2.getCell(celula).setCellValue("Data do Vídeo");
	    	cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    	
	    	sheet.setColumnWidth(celula, 4000);
		    
		    celula++;
	    }
	    Integer intervalo_fim = celula - 1;
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), intervalo_ini, intervalo_fim));
	    
//	    cabecalho1.getCell(celula).setCellValue("");
//	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
    	cabecalho2.getCell(celula).setCellValue("TOTAL");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
//	    cabecalho3.getCell(celula).setCellValue("");
//	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2800);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho3.getRowNum(), celula, celula));
	    celula++;
	    
//	    cabecalho1.getCell(celula).setCellValue("");
//	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
//    	cabecalho2.getCell(celula).setCellValue("TOTAL DIAS");
//	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
//	    cabecalho3.getCell(celula).setCellValue("");
//	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
//	    cabecalho3.getCell(celula).setCellValue("");
//	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
//	    sheet.setColumnWidth(celula, 2800);
//	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho3.getRowNum(), celula, celula));
//		celula++;
		
//		cabecalho1.getCell(celula).setCellValue("");
//	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo2);
//    	cabecalho2.getCell(celula).setCellValue("APROVEITAMENTO");
//	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
//	    cabecalho3.getCell(celula).setCellValue("");
//	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
//	    cabecalho3.getCell(celula).setCellValue("");
//	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
//	    sheet.setColumnWidth(celula, 4000);
//	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho3.getRowNum(), celula, celula));
//		celula++;
		
		int intLinhaInicioDados = intLinha, intLinhaRetorno = intLinhaInicioDados;
	    
        //Buscando informações para popular planilhas
	    logger.info("Aguardando DADOS");
	    Date dt_inicio_req = Calendar.getInstance().getTime();
	    List<DadosRelatorioSinalizacao> dadosRelatorio = DadosRelatorioSinalizacao.ObterItensRelatorio(imagens);
	    Date dt_fim_req = Calendar.getInstance().getTime();
	    logger.info("TEMPO para obter DADOS : " + (dt_fim_req.getTime() - dt_inicio_req.getTime()) / 1000);
	    
	    Calendar cal = Calendar.getInstance();
	    Integer ano_mes = 0;
	    
        	for (int i = 0; i < dadosRelatorio.size(); i++) {
            	
        		Row linhaInfo = sheet.createRow((short)intLinhaInicioDados + i);
    			linhaInfo.getCell(0).setCellValue(dadosRelatorio.get(i).getNumeroEquipamento());
    			linhaInfo.getCell(0).setCellStyle(ce.estiloItensCentralizado);
    			linhaInfo.getCell(1).setCellValue(dadosRelatorio.get(i).getCodigoEquipamentoDER());
    			linhaInfo.getCell(1).setCellStyle(ce.estiloItensCentralizado);
    			
    			if (dadosRelatorio.get(i).getDataInicioOperacao() != null) {
    				linhaInfo.getCell(2).setCellValue(sdf.format(dadosRelatorio.get(i).getDataInicioOperacao()));
    			}
    			linhaInfo.getCell(2).setCellStyle(ce.estiloItensCentralizado);

    			celula = 3;
    			
    			for (Integer mes = 0; mes < listaDatas.size(); mes++) {
    				cal.setTime(listaDatas.get(mes));
    				ano_mes = (cal.get(Calendar.YEAR) * 100) + cal.get(Calendar.MONTH) + 1;
    				if (dadosRelatorio.get(i).getDatasVideos().containsKey(ano_mes)) {

    					linhaInfo.getCell(celula).setCellValue(sdf.format(dadosRelatorio.get(i).getDatasVideos().get(ano_mes)));
            			linhaInfo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoVerdeComZero);
    				} else {
        				linhaInfo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoVermelhoComZero);
    				}
    				
					celula++;
            	}
    			
    			linhaInfo.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
    			linhaInfo.getCell(celula).setCellValue(dadosRelatorio.get(i).getDatasVideos().size());
    			
        	}
	    
	    dadosRelatorio.clear();
    	dadosRelatorio = null;
	    
		sheet = wb.getSheetAt(0);
		
		return intLinhaRetorno + 1;
	}

	/**
	 * Validar se o valor digitado é um numero
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 27/09/2016
	 */
	boolean isDigit(String s) {  
	    for (int i = 0; i < s.length(); i++) {  
	          char ch = s.charAt(i);  
	          if (ch < 48 || ch > 57)  
	               return false;  
	    }  
	    return true;  
	}

}
