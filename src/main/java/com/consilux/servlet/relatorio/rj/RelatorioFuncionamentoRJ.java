package com.consilux.servlet.relatorio.rj;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.relatorio.rj.DadosRelatorioFuncionamento;
import com.consilux.model.relatorio.rj.ItemRelatorioFuncionamento;

/**
 * Servlet implementation class RelatorioFuncionamento
 */
public class RelatorioFuncionamentoRJ extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RelatorioFuncionamentoRJ.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String strDataInicio = request.getParameter("data_ini");
		String strDataFim = request.getParameter("data_fim");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
		SimpleDateFormat mesExtenso = new SimpleDateFormat("MMMM");
		SimpleDateFormat formatoAno = new SimpleDateFormat("yyyy");
		
		Date dtDataIni = null, dtDataFim = null;
		
		String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");
		String contrato = String.format("%03d", Integer.parseInt(numeroContrato)) + "/" + anoContrato;

		// Valida o(s) parâmetros.
	    if (strDataInicio != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", strDataInicio))
	    {
	        new Mensagem(response).showErro("Data inicial enviada inválida!");
	        return;
	    }

	    if (strDataFim != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", strDataFim))
	    {
	        new Mensagem(response).showErro("Data final enviada inválida!");
	        return;
	    }	
			
		try {
			
			dtDataIni = new Date(dateFormat.parse(strDataInicio + " 00:00:00.000").getTime());
			dtDataFim = new Date(dateFormat.parse(strDataFim + " 23:59:59.997").getTime());
			
			if (dtDataFim.before(dtDataIni)) {
				new Mensagem(response).showErro("Data inicial deve ser maior que a data final!");
		        return;
			}
			
			String strLimiteDiasRelatorio = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("rel_funcionamento_limite_dias");
			Integer limiteDiasRelatorio = Integer.parseInt((strLimiteDiasRelatorio == null || strLimiteDiasRelatorio.trim().equals("") ? "0" : strLimiteDiasRelatorio));
			
			long diff = Math.abs(dtDataFim.getTime() - dtDataIni.getTime());
			long qtdeDiasPeriodo = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			
			if (qtdeDiasPeriodo > limiteDiasRelatorio) {
				new Mensagem(response).showErro("O período não deve ser maior que " + String.valueOf(limiteDiasRelatorio) + " dias!");
				return;
			}
			
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		try {
			
			// Criando o arquivo fisico
	        String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_RelatorioFuncionamento_" + mesExtenso.format(dtDataIni).substring(0,3).toLowerCase() + formatoAno.format(dtDataIni) + ".xlsx";
	        
	        response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");  			
 
	        // Criando area de trabalho para o excel
			SXSSFWorkbook wb = new SXSSFWorkbook();
	        wb.setCompressTempFiles(true);
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        Sheet sheet = wb.createSheet("Funcionamento-"+mesExtenso.format(dtDataIni).substring(0,3).toLowerCase() + formatoAno.format(dtDataIni)); 
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
	        
	        ColecaoEstilos ce = new ColecaoEstilos(wb);
            
            //Filtros informados
            Row cabecalho1 = sheet.createRow(0);
            cabecalho1.getCell(0).setCellValue("RELATÓRIO DE FUNCIONAMENTO");
            cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
            
            Row cabecalho2 = sheet.createRow(1);
            cabecalho2.getCell(0).setCellValue("INFORMAÇÕES DIÁRIAS, PARA TODOS OS ENDEREÇOS");
            cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));
            
            Row cabecalhoContrato = sheet.createRow(2);
            cabecalhoContrato.getCell(0).setCellValue("Nº DO CONTRATO:");
            cabecalhoContrato.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoContrato.getCell(1).setCellValue(contrato);
            cabecalhoContrato.getCell(1).setCellStyle(ce.estiloFiltro);
            
            int intLinha = 4;
            
            //Gerar relatório por equipamento
            intLinha = GerarRelatorio(dtDataIni, dtDataFim, intLinha, response, sheet, wb, nomeArquivo, ce);
            
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

	
	public int GerarRelatorio(Date dtDataIni, Date dtDataFim, int intLinha,
							  HttpServletResponse response, Sheet sheet, SXSSFWorkbook wb, 
					 		  String nomeArquivo, ColecaoEstilos ce
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	    
    	Row cabecalho1 = sheet.createRow((short)intLinha++);
		Row cabecalho2 = sheet.createRow((short)intLinha++);
		Row cabecalho3 = sheet.createRow((short)intLinha++);
		Row cabecalho4 = sheet.createRow((short)intLinha++);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatoMesAno = new SimpleDateFormat("MM/yyyy");
		Locale BRAZIL = new Locale("pt","BR");
		SimpleDateFormat diaSemana = new SimpleDateFormat("EEEE",BRAZIL);
		
		cabecalho1.getCell(0).setCellValue(formatoMesAno.format(dtDataIni));
	    cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo1);
	    
	    cabecalho2.getCell(0).setCellValue("Data");
	    cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(0).setCellValue("Dia da Semana");
	    cabecalho3.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(0).setCellValue("CÓDIGO");
	    cabecalho4.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(0, 4000);
	    
	    
	    cabecalho1.getCell(1).setCellValue("");
	    cabecalho1.getCell(1).setCellStyle(ce.estiloCabecalhoCorpo3);
	    
	    cabecalho2.getCell(1).setCellValue("Data");
	    cabecalho2.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(1).setCellValue("Dia da Semana");
	    cabecalho3.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(1).setCellValue("EQUIPAMENTO");
	    cabecalho4.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(1, 4000);
	    
	    
	    cabecalho1.getCell(2).setCellValue("");
	    cabecalho1.getCell(2).setCellStyle(ce.estiloCabecalhoCorpo3);
	    
	    cabecalho2.getCell(2).setCellValue("Data");
	    cabecalho2.getCell(2).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(2).setCellValue("Dia da Semana");
	    cabecalho3.getCell(2).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(2).setCellValue("INÍCIO OPERAÇÃO");
	    cabecalho4.getCell(2).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(2, 3000);
	    
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), 0, 2));
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho3.getRowNum(), 0, 2));
	    
	    Integer celula = 3;
	    String strData = null, coluna_ref_total_inicio = null, coluna_ref_total_fim = null;
	    Date dtData = null, dtDataAtual = new Date();
	    
        //Buscando informações para popular planilhas
	    logger.info("Aguardando DADOS");
	    Date dt_inicio_req = Calendar.getInstance().getTime();
	    List<ItemRelatorioFuncionamento> dadosRelatorio = DadosRelatorioFuncionamento.ObterItensRelatorioFuncionamento(dtDataIni, dtDataFim);
	    Date dt_fim_req = Calendar.getInstance().getTime();
	    logger.info("TEMPO para obter DADOS : " + (dt_fim_req.getTime() - dt_inicio_req.getTime()) / 1000);
	    
	    
	    for (Integer i = 0; i < dadosRelatorio.get(0).getQtdeColunas(); i++) {
	    	
	    	strData = dadosRelatorio.get(i).getCelulasColunasRelatorio()[i];
	    	dtData = sdf.parse(strData);
	    	
	    	cabecalho1.getCell(celula).setCellValue("");
	    	cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);

	    	cabecalho2.getCell(celula).setCellValue(strData);
	    	cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    	
	    	cabecalho3.getCell(celula).setCellValue(diaSemana.format(dtData));
	    	cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    	
	    	cabecalho4.getCell(celula).setCellValue("HORAS FUNCIONAMENTO");
	    	cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    	
	    	sheet.setColumnWidth(celula, 4000);
	    	
	    	if (coluna_ref_total_inicio == null)
	    		coluna_ref_total_inicio = CellReference.convertNumToColString(celula);
	    	
	    	coluna_ref_total_fim = CellReference.convertNumToColString(celula);
		    
		    celula++;
	    }
	    
	    cabecalho1.getCell(celula).setCellValue("");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
    	cabecalho2.getCell(celula).setCellValue("TOTAL HORAS");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2800);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho4.getRowNum(), celula, celula));
	    celula++;
	    
	    cabecalho1.getCell(celula).setCellValue("");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
    	cabecalho2.getCell(celula).setCellValue("TOTAL DIAS");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2800);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho4.getRowNum(), celula, celula));
		celula++;
		
		cabecalho1.getCell(celula).setCellValue("");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo2);
    	cabecalho2.getCell(celula).setCellValue("APROVEITAMENTO");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 4000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho4.getRowNum(), celula, celula));
		celula++;
		
		int intLinhaInicioDados = intLinha, intLinhaRetorno = intLinhaInicioDados;
	    
	    if (dadosRelatorio.size() == 0) {
        	Row linhaSemInfo = sheet.createRow((short)intLinha);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        	linhaSemInfo.getCell(0).setCellStyle(ce.estiloItensLinhaSemInfo);
        } else {
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
    			Integer valorHorasFuncionamento;
    			
    			for(int h = 0; h < dadosRelatorio.get(0).getQtdeColunas(); h++) {
    				
    				strData = dadosRelatorio.get(i).getCelulasColunasRelatorio()[h];
    		    	dtData = sdf.parse(strData);
			    	
			    	valorHorasFuncionamento = dadosRelatorio.get(i).getCelulasHorasFuncionamento()[h];
			    	
    				linhaInfo.getCell(celula).setCellValue(valorHorasFuncionamento);
    				if (dtData.after(dtDataAtual)) {
    					linhaInfo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoComZero);
            		} else {
            			if (valorHorasFuncionamento == 0 || valorHorasFuncionamento < 12) {
            				linhaInfo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoVermelhoComZero);
                		} else {
                			linhaInfo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoVerdeComZero);
                		}
            		}
					celula++;
            	}
    			
    			linhaInfo.getCell(celula).setCellFormula("SUM(" + coluna_ref_total_inicio + (linhaInfo.getRowNum()+1) + ":" + coluna_ref_total_fim + (linhaInfo.getRowNum()+1) + ")");
				linhaInfo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoComZero);
				celula++;
				
				linhaInfo.getCell(celula).setCellFormula("COUNTIF(" + coluna_ref_total_inicio + (linhaInfo.getRowNum()+1) + ":" + coluna_ref_total_fim + (linhaInfo.getRowNum()+1) + ",\">= 12\")");
				linhaInfo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoComZero);
				celula++;
    			
				if (dadosRelatorio.get(i).getAproveitamento() > 0) {
					linhaInfo.getCell(celula).setCellValue(dadosRelatorio.get(i).getAproveitamento());
				}
    			linhaInfo.getCell(celula).setCellStyle(ce.estiloPorcentagem);
    			celula++;
    			
        	}
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
