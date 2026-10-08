package com.consilux.servlet.relatorio.rj;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.relatorio.rj.DadosRelatorioMedicao;
import com.consilux.model.relatorio.rj.ItemRelatorioMedicao;

/**
 * Servlet implementation class PlanilhaAcompanhamento
 */
public class PlanilhaAcompanhamento extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final Logger logger = Logger.getLogger(PlanilhaAcompanhamento.class);
	
	private static final String objeto = "Fiscalização e registro de infrações por avanço de semáforo, parada sobre a faixa de pedestres e velocidade";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
        //Date data = new Date();  
        String strDataInicio = request.getParameter("data_ini");
		String strDataFim = request.getParameter("data_fim");
		
		Locale local = new Locale("pt","BR");
		
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy", local);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS", local);
		SimpleDateFormat mesExtenso = new SimpleDateFormat("MMMM", local);
		SimpleDateFormat formatoAno = new SimpleDateFormat("yyyy", local);
		
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
			
			String strLimiteDiasRelatorio = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("planilha_acompanhamento_limite_dias");
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
	        String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_Planilha de Acompanhamento_" + mesExtenso.format(dtDataIni).substring(0,3).toLowerCase() + formatoAno.format(dtDataIni) + ".xlsx";
	        
	        response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");  			
 
	        // Criando area de trabalho para o excel
			SXSSFWorkbook wb = new SXSSFWorkbook();
	        wb.setCompressTempFiles(true);
	        
		    //Cria uma planilha Excel
	        Sheet sheet = wb.createSheet("Consilux-"+mesExtenso.format(dtDataIni).substring(0,3).toLowerCase() + formatoAno.format(dtDataIni)); 
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
	        
	        ColecaoEstilos ce = new ColecaoEstilos(wb);
            
            //Filtros informados
            Row cabecalho1 = sheet.createRow(0);
            cabecalho1.createCell(0).setCellValue("PLANILHA DE ACOMPANHAMENTO DOS REGISTROS");
            cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
            
            Row cabecalho2 = sheet.createRow(1);
            cabecalho2.createCell(0).setCellValue("Todos os endereços (por pista-sentido-faixa) mensal - por ocorrência");
            cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));
            
            Row cb1 = sheet.createRow(2);
            cb1.createCell(0).setCellValue("Contrato nº:");
            cb1.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cb1.createCell(1).setCellValue(contrato);
            cb1.getCell(1).setCellStyle(ce.estiloFiltro);
            cb1.createCell(3).setCellValue("Nº da Medição:");
            cb1.getCell(3).setCellStyle(ce.estiloCabecalhoFiltro);
            cb1.createCell(4).setCellValue("");
            cb1.getCell(4).setCellStyle(ce.estiloFiltro);
            
            Row cb2 = sheet.createRow(3);
            cb2.createCell(0).setCellValue("Objeto:");
            cb2.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cb2.createCell(1).setCellValue(objeto);
            cb2.getCell(1).setCellStyle(ce.estiloFiltro);
            cb2.createCell(3).setCellValue("Mês do serviço:");
            cb2.getCell(3).setCellStyle(ce.estiloCabecalhoFiltro);
            cb2.createCell(4).setCellValue(mesExtenso.format(dtDataIni) + " de " + formatoAno.format(dtDataIni));
            cb2.getCell(4).setCellStyle(ce.estiloFiltro);
            
            Row cb3 = sheet.createRow(4);
            cb3.createCell(0).setCellValue("Contratada:");
            cb3.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cb3.createCell(1).setCellValue("CONSILUX");
            cb3.getCell(1).setCellStyle(ce.estiloFiltro);
            cb3.createCell(3).setCellValue("Período:");
            cb3.getCell(3).setCellStyle(ce.estiloCabecalhoFiltro);
            cb3.createCell(4).setCellValue(formatador.format(dtDataIni) + " até " + formatador.format(dtDataFim));
            cb3.getCell(4).setCellStyle(ce.estiloFiltro);
            
            int intLinha = 6;
            
            //Gerar relatório por equipamento
            intLinha = GerarRelatorio(dtDataIni, dtDataFim, intLinha, response, sheet, wb, nomeArquivo, ce, false);
            
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
							  HttpServletResponse response, Sheet sheet, SXSSFWorkbook wb, String nomeArquivo, 
					 		  ColecaoEstilos ce, boolean porFaixa
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
    	intLinha = (intLinha + 1);
        
    	Row cabecalho1 = sheet.createRow((short)intLinha++);
		Row cabecalho2 = sheet.createRow((short)intLinha++);		
		
		cabecalho1.setHeight((short) (cabecalho1.getHeight() * 2));
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		cabecalho1.createCell(0).setCellValue("Número do equipamento");
	    cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho2.createCell(0).setCellValue("");
	    cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(0, 3200);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), 0, 0));
	    
	    cabecalho1.createCell(1).setCellValue("Endereço do equipamento");
	    cabecalho1.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho2.createCell(1).setCellValue("");
	    cabecalho2.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(1, 19500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), 1, 1));
	    
	    cabecalho1.createCell(2).setCellValue("Data de Publicação");
	    cabecalho1.getCell(2).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho2.createCell(2).setCellValue("");
	    cabecalho2.getCell(2).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(2, 3500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), 2, 2));
	    
	    cabecalho1.createCell(3).setCellValue("CÓDIGO CET");
	    cabecalho1.getCell(3).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho2.createCell(3).setCellValue("");
	    cabecalho2.getCell(3).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(3, 3500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), 3, 3));
	    
    	cabecalho1.createCell(4).setCellValue("Faixa");
	    cabecalho1.getCell(4).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho2.createCell(4).setCellValue("");
	    cabecalho2.getCell(4).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(4, 2000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), 4, 4));
	    
	    cabecalho1.createCell(5).setCellValue("Ocorrência");
	    cabecalho1.getCell(5).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho2.createCell(5).setCellValue("");
	    cabecalho2.getCell(5).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(5, 6000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), 5, 5));
	    
	    Integer celula = 6, celulaInicio = celula;
	    String strData = null, coluna_ref_total_inicio = null, coluna_ref_total_fim = null;
	    Date dtData = null;
	    Calendar calendarioData = Calendar.getInstance();
	    
	    
        //Buscando informações para popular planilhas
	    logger.info("Aguardando DADOS");
	    Date dt_inicio_req = Calendar.getInstance().getTime();
	    List<ItemRelatorioMedicao> dadosRelatorio = DadosRelatorioMedicao.ObterItensRelatorioMedicao(dtDataIni, dtDataFim);
	    Date dt_fim_req = Calendar.getInstance().getTime();
	    logger.info("TEMPO para obter DADOS : " + (dt_fim_req.getTime() - dt_inicio_req.getTime()) / 1000);
	    
	    
	    List<Integer> finais_de_semana = new ArrayList<Integer>();
	    
	    for (Integer i = 0; i < dadosRelatorio.get(0).getQtdeColunas(); i++) {
	    	
	    	strData = dadosRelatorio.get(i).getCelulasColunasRelatorio()[i];
	    	dtData = sdf.parse(strData);
	    	calendarioData.setTime(dtData);
	    	
	    	cabecalho1.createCell(celula).setCellValue("Quantidade de registros");
	    	cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    	
	    	cabecalho2.createCell(celula).setCellValue(strData);
	    	if (calendarioData.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendarioData.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
	    		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegritoVerde);
	    		finais_de_semana.add(celula);
	    	}
	    	else {
	    		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    	}
	    	
	    	sheet.setColumnWidth(celula, 3000);
	    	
	    	if (coluna_ref_total_inicio == null)
	    		coluna_ref_total_inicio = CellReference.convertNumToColString(celula);
	    	
	    	coluna_ref_total_fim = CellReference.convertNumToColString(celula);
		    
		    celula++;
	    }
	    
	    cabecalho1.createCell(celula).setCellValue("");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
    	cabecalho2.createCell(celula).setCellValue("Total");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), celulaInicio, celula));
	    
		celula++;
		cabecalho1.createCell(celula).setCellValue("Dias sem pleno funcionamento");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho2.createCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 4500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), celula, celula));
	    
		celula++;
		cabecalho1.createCell(celula).setCellValue("Justificativas");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho2.createCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 10000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), celula, celula));
        
	    int intLinhaInicioDados = intLinha, intLinhaRetorno = intLinhaInicioDados;
	    

	    
	    String strEnderecoAnterior = null;
	    Boolean novoEndereco = false;
	    Boolean ultimaLinha = false;
	    		
	    if (dadosRelatorio.size() == 0) {
        	Row linhaSemInfo = sheet.createRow((short)intLinha);
        	linhaSemInfo.createCell(0).setCellValue("Não há dados para os filtros informados!");
        	linhaSemInfo.getCell(0).setCellStyle(ce.estiloItensLinhaSemInfo);
        } else {
        	for (int i = 0; i < dadosRelatorio.size(); i++) {
            	
        		int dias_sem_funcionamento = 0, dias_funcionamento = 0;
        		
	    		if ( (strEnderecoAnterior != null) && (!strEnderecoAnterior.equals(dadosRelatorio.get(i).getEnderecoEquipamento())) ) {
	    			novoEndereco = true;
	    		} else {
	    			novoEndereco = false;
	    		}
        		
	    		ultimaLinha = (i == (dadosRelatorio.size() - 1) ? true : false);
	    		
        		Row linhaInfo = sheet.createRow((short)intLinhaInicioDados + i);
    			linhaInfo.createCell(0).setCellValue(dadosRelatorio.get(i).getNumeroEquipamento());
    			linhaInfo.getCell(0).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
    			linhaInfo.createCell(1).setCellValue(dadosRelatorio.get(i).getEnderecoEquipamento());
    			linhaInfo.getCell(1).setCellStyle(ultimaLinha ? ce.estiloItensEsquerdaUltimaLinha : (novoEndereco ? ce.estiloItensEsquerdaPlanAcomp : ce.estiloItensEsquerdaPontilhado));
        		linhaInfo.createCell(2).setCellValue(sdf.format(dadosRelatorio.get(i).getDataPublicacao()));
    			linhaInfo.getCell(2).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
        		linhaInfo.createCell(3).setCellValue(String.format("%010d", dadosRelatorio.get(i).getCodigoCET()));
    			linhaInfo.getCell(3).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
    			linhaInfo.createCell(4).setCellValue(dadosRelatorio.get(i).getFaixa());
    			linhaInfo.getCell(4).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
    			linhaInfo.createCell(5).setCellValue(dadosRelatorio.get(i).getOcorrencia());
    			linhaInfo.getCell(5).setCellStyle(ultimaLinha ? ce.estiloItensEsquerdaUltimaLinha : (novoEndereco ? ce.estiloItensEsquerdaPlanAcomp : ce.estiloItensEsquerdaPontilhado));
    			
    			celula = 6;
    			Integer valorCelula;
    			
    			for(int h = 0; h < dadosRelatorio.get(0).getQtdeColunas(); h++) {
    				
    				strData = dadosRelatorio.get(i).getCelulasColunasRelatorio()[h];
    		    	dtData = sdf.parse(strData);
			    	
			    	valorCelula = dadosRelatorio.get(i).getCelulasValores()[h];
			    	
	    			boolean fds = finais_de_semana.contains(celula);
	    			
	    			boolean publicado = !dtData.before(dadosRelatorio.get(i).getDataPublicacao());
	    			
	    			boolean contabilizar = 
	    					(dadosRelatorio.get(i).getIdOcorrencia() != DadosRelatorioMedicao.TipoOcorrencia.AVANCO.ordinal() &&
	    					 dadosRelatorio.get(i).getIdOcorrencia() != DadosRelatorioMedicao.TipoOcorrencia.PARADA.ordinal()) || !fds;
	    			
	    			boolean sem_medicao = false;
	    			
					if (!publicado || valorCelula == null) {
						linhaInfo.createCell(celula).setCellValue("");
						sem_medicao = true;
					} else if (valorCelula > 0) {
						linhaInfo.createCell(celula).setCellValue(valorCelula);
					}
					else {
						linhaInfo.createCell(celula).setCellValue("");
						sem_medicao = true;
					}
					
					if (sem_medicao && contabilizar && publicado) {
						dias_sem_funcionamento++;
					}
					
					if (!sem_medicao) {
						dias_funcionamento++;
					}
					
					if (!publicado) {
						linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoCinzaUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoCinzaPlanAcomp : ce.estiloItensCentralizadoCinzaPontilhado));
					}
					
					else if (finais_de_semana.contains(celula)) {
						linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoVerdeUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoVerdePlanAcomp : ce.estiloItensCentralizadoVerdePontilhado));
					} 
					
					else { 
						linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
					}
					
					celula++;
            	}
				
//				if ((dadosRelatorio.get(i).getIdOcorrencia() != 6) && (dias_funcionamento > 0))
    			if (dias_funcionamento > 0)
				{
					linhaInfo.createCell(celula).setCellFormula("SUM(" + coluna_ref_total_inicio + (linhaInfo.getRowNum()+1) + ":" + coluna_ref_total_fim + (linhaInfo.getRowNum()+1) + ")"); 
					linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
				} else {
					linhaInfo.createCell(celula).setCellValue("");
					linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
				}
				celula++;
				
				linhaInfo.createCell(celula).setCellValue(dias_sem_funcionamento);
				linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
				celula++;
				
				linhaInfo.createCell(celula).setCellValue(dadosRelatorio.get(i).getJustificativa());
				linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensEsquerdaUltimaLinha : (novoEndereco ? ce.estiloItensEsquerdaPlanAcomp : ce.estiloItensEsquerdaPontilhado));
				celula++;
				
				strEnderecoAnterior = dadosRelatorio.get(i).getEnderecoEquipamento();
        	}
        }
	    
	    
	    dadosRelatorio.clear();
    	dadosRelatorio = null;
	    
		sheet = wb.getSheetAt(0);
		
		return intLinhaRetorno + 2;
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
