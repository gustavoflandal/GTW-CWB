package com.consilux.servlet.relatorio.rj;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.relatorio.rj.DadosFluxoVeicular;
import com.consilux.model.relatorio.rj.ItemFluxoVeicular;


/**
 * Servlet para a geração de relatório de Fiscalização Eletrônica - Relatório 03 - Todos os endereços mensal por data - volume veicular. 
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 26/09/2016
 */
public class Relatorio3FluxoVeicular extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(Relatorio3FluxoVeicular.class);

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
		
		String strMes = request.getParameter("mes");
		String strAno = request.getParameter("ano");
		
		Integer intMes = null, intAno = null;
		
		SimpleDateFormat mesExtenso = new SimpleDateFormat("MMMM");
		Date dtData = null;
		
		String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");

		//Validações Necessárias para montar relatório

		if((strMes.equals("")) || (strAno.equals(""))){
			new Mensagem(response).showErro("O Mês e o Ano devem ser informados!", "javascript:window.close();");
			return;
		}
		
		if (!isDigit(strMes) || !isDigit(strAno)) {
			new Mensagem(response).showErro("Favor informar os dados corretos de mês e ano! Somente números são válidos.", "javascript:window.close();");
			return; 
		}
			
		try {

			intMes = Integer.parseInt(strMes);  
			intAno = Integer.parseInt(strAno);
			
			if(intMes < 1 || intMes > 12){
				new Mensagem(response).showErro("Favor informar um valor de mês válido. Entre 1 e 12!", "javascript:window.close();");
				return; 
			}
			if(intAno < 1900 || intAno > 2050){
				new Mensagem(response).showErro("Favor informar um valor de ano válido. Maior que 1900 e menor que 2050!", "javascript:window.close();");
				return; 
			}
			
			dtData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/" + (strMes.trim().length() == 1 ? "0"+strMes.trim() : strMes.trim()) +"/"+ strAno + " 00:00:00");
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		try {
			
			// Criando o arquivo fisico
			String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_estat.03-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
	        
	        response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");  			
 
	        // Criando area de trabalho para o excel
			SXSSFWorkbook wb = new SXSSFWorkbook();
			wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
			Sheet sheet = wb.createSheet("3-Consilux-Volume-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString()); 
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
	        
	        ColecaoEstilos ce = new ColecaoEstilos(wb);
            
            String contrato = String.format("%03d", Integer.parseInt(numeroContrato)) + "/" + anoContrato;
            
	        //Cria uma linha na Planilha.
            //Nome relatório
            Row cabecalhoTituloRelatorio1 = sheet.createRow(0);
            cabecalhoTituloRelatorio1.getCell(0).setCellValue("FISCALIZAÇÃO ELETRÔNICA - RELATÓRIO 03");
            cabecalhoTituloRelatorio1.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));
            
            Row cabecalhoTituloRelatorio2 = sheet.createRow(1);
            cabecalhoTituloRelatorio2.getCell(0).setCellValue("FLUXO VEICULAR DIÁRIO, PARA CADA MÊS, PARA TODOS OS ENDEREÇOS");
            cabecalhoTituloRelatorio2.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 15));
            
            //Filtros informados
            Row cabecalhoContrato = sheet.createRow(2);
            cabecalhoContrato.getCell(0).setCellValue("Nº DO CONTRATO:");
            cabecalhoContrato.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoContrato.getCell(1).setCellValue(contrato);
            cabecalhoContrato.getCell(1).setCellStyle(ce.estiloFiltro);
            
            Row cab1 = sheet.createRow(4);
            cab1.getCell(0).setCellValue("Dados por equipamento");
            cab1.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            
            GerarRelatorio(intMes, intAno, response, sheet, wb, userGerador, nomeArquivo, idUsuario, data, usuarioURL, ce);

			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}

	
	public void GerarRelatorio(Integer intMes, Integer intAno, 
							   HttpServletResponse response,Sheet sheet,Workbook wb, String userGerador,
					 		   String nomeArquivo, Integer idUsuario, Date data, String usuarioURL, ColecaoEstilos ce
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
        //Criando as colunas do corpo
		Row cabecalho1   = sheet.createRow(5);
		Row cabecalho2   = sheet.createRow(6);
		Row cabecalho3   = sheet.createRow(7);
		Row cabecalho4   = sheet.createRow(8);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatoMesAno = new SimpleDateFormat("MM/yyyy");
		
		cabecalho1.getCell(0).setCellValue("FLUXO VEICULAR - " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo1);
		
	    cabecalho2.getCell(0).setCellValue("CÓDIGO EQUIPAMENTO");
	    cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    
	    cabecalho3.getCell(0).setCellValue("ENDEREÇO");
	    cabecalho3.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    
	    cabecalho4.getCell(0).setCellValue("DATA");
	    cabecalho4.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);

	    sheet.setColumnWidth(0, 4000);
	    
		cabecalho1.getCell(1).setCellValue("");
	    cabecalho1.getCell(1).setCellStyle(ce.estiloCabecalhoCorpo3);
		
	    cabecalho2.getCell(1).setCellValue("");
	    cabecalho2.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    
	    cabecalho3.getCell(1).setCellValue("");
	    cabecalho3.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    
	    cabecalho4.getCell(1).setCellValue("DIA DA SEMANA");
	    cabecalho4.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    
	    sheet.setColumnWidth(1, 4000);
	    
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), 0, 1));
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), 0, 1));
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho3.getRowNum(), 0, 1));

        //Buscando informações para popular planilhas
		DadosFluxoVeicular dadosFluxoVeicular = new DadosFluxoVeicular();
		ArrayList<ItemFluxoVeicular> dadosRelatorio       = dadosFluxoVeicular.relatorio3FluxoVeicular(intMes, intAno, false);
		ArrayList<ItemFluxoVeicular> dadosRelatorio_faixa = dadosFluxoVeicular.relatorio3FluxoVeicular(intMes, intAno, true);
	    
		Integer qtdeColunas = dadosRelatorio.get(0).getQtdeColunas();
	    Integer celula = 2;
	    
	    for(int h = 3; h < qtdeColunas; h++) {
	    	String nome_local = dadosRelatorio.get(0).getCelulasColunasRelatorio()[h];
	    	String[] partes_local = nome_local.split("_");
	    	if (partes_local.length == 3) {
	    		cabecalho2.getCell(celula).setCellValue(partes_local[0].trim() == "" ? "N/D" : partes_local[0].trim());
	    		cabecalho3.getCell(celula).setCellValue(partes_local[2].trim());
	    	} else {
	    		cabecalho2.getCell(celula).setCellValue("");
	    		cabecalho3.getCell(celula).setCellValue(nome_local);
	    	}
	    	
			cabecalho1.getCell(celula).setCellValue("");
		    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			
		    cabecalho4.getCell(celula).setCellValue("VOLUME DIÁRIO");
		    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);

		    sheet.setColumnWidth(celula, 10000);
		    celula++;
	    }
	    
	    cabecalho1.getCell((celula - 1)).setCellStyle(ce.estiloCabecalhoCorpo2);
	    
	    
	    int incremento = 9;
	    
	    if (dadosRelatorio.size() == 0) {
        	Row linhaSemInfo = sheet.createRow(incremento);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        } else {
            
        	for(int i = 0; i < dadosRelatorio.size(); i++) {
            	
               	Row linhaSheet = sheet.createRow((short)(i+incremento));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getData());
            	linhaSheet.getCell(0).setCellStyle(ce.estiloCorpo);
            	linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getDiaSemanaDesc());
            	linhaSheet.getCell(1).setCellStyle(ce.estiloCorpo);
            	
            	celula = 2;
            	
            	for(int h = 3; h < qtdeColunas; h++) {
            		if (dadosRelatorio.get(i).getCelulasValorColuna()[h] > 0) {
            			linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getCelulasValorColuna()[h]);
            		} 
                	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
                	celula++;
        	    }
            }
        }
	    
	    int linha_total_1 = dadosRelatorio.size()+incremento;
	    Row row_total_1 = sheet.createRow(linha_total_1);
	    row_total_1.getCell(0).setCellValue("TOTAL");
	    row_total_1.getCell(0).setCellStyle(ce.estiloItensCentralizadoCinza);
	    row_total_1.getCell(1).setCellValue("");
	    row_total_1.getCell(1).setCellStyle(ce.estiloItensCentralizadoCinza);
	    sheet.addMergedRegion(new CellRangeAddress(linha_total_1, linha_total_1, 0, 1));
	    
	    for(int h = 2; h < qtdeColunas - 1; h++) {
	    	Cell c = row_total_1.getCell(h);
	    	String nome_cel = c.getAddress().formatAsString();
	    	String numero_cel = nome_cel;
	    	while(!NumberUtils.isNumber(numero_cel)) {
	    		numero_cel = numero_cel.substring(1);
	    	}
	    	String nome_col = nome_cel.replace(numero_cel, "");
	    	String formula = "SUM(" + nome_col + String.valueOf( incremento + 1 ) + ":" + nome_col + String.valueOf(linha_total_1) + ")";
	    	
	    	c.setCellFormula(formula);
	    	c.setCellStyle(ce.estiloItensNumCentralizadoCinza);
	    }
        
        int qtdeLinhas = dadosRelatorio.size()+incremento+3;
        

        // TODO: Segunda parte
		//Criando as colunas do corpo
        Row cabecalho0b   = sheet.createRow(qtdeLinhas++); // qtdeLinhas_ref
		Row cabecalho1b   = sheet.createRow(qtdeLinhas++); // qtdeLinhas_ref + 1
		Row cabecalho2b   = sheet.createRow(qtdeLinhas++); // qtdeLinhas_ref + 2
		Row cabecalho3b   = sheet.createRow(qtdeLinhas++); // qtdeLinhas_ref + 3
		Row cabecalho4b   = sheet.createRow(qtdeLinhas++); // qtdeLinhas_ref + 4
		
		cabecalho0b.getCell(0).setCellValue("Dados por faixa");
		cabecalho0b.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
        
		cabecalho1b.getCell(0).setCellValue("FLUXO VEICULAR - " + (String.valueOf(intMes).length() == 1 ? "0" + String.valueOf(intMes) : String.valueOf(intMes)) + "/" + String.valueOf(intAno));
	    cabecalho1b.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo1);
		
	    cabecalho2b.getCell(0).setCellValue("CÓDIGO EQUIPAMENTO");
	    cabecalho2b.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    
	    cabecalho3b.getCell(0).setCellValue("ENDEREÇO");
	    cabecalho3b.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    
	    cabecalho4b.getCell(0).setCellValue("DATA");
	    cabecalho4b.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		
	    cabecalho1b.getCell(1).setCellValue("");
	    cabecalho1b.getCell(1).setCellStyle(ce.estiloCabecalhoCorpo3);
		
	    cabecalho2b.getCell(1).setCellValue("");
	    cabecalho2b.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    
	    cabecalho3b.getCell(1).setCellValue("");
	    cabecalho3b.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    
	    cabecalho4b.getCell(1).setCellValue("DIA DA SEMANA");
	    cabecalho4b.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1b.getRowNum(), cabecalho1b.getRowNum(), 0, 1));
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2b.getRowNum(), cabecalho2b.getRowNum(), 0, 1));
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3b.getRowNum(), cabecalho3b.getRowNum(), 0, 1));
	    
	    qtdeColunas = dadosRelatorio_faixa.get(0).getQtdeColunas();
	    celula = 2;
	    
	    for(int h = 3; h < qtdeColunas; h++) {
	    	String nome_local = dadosRelatorio_faixa.get(0).getCelulasColunasRelatorio()[h];
	    	String[] partes_local = nome_local.split("_");
	    	if (partes_local.length == 3) {
	    		cabecalho2b.getCell(celula).setCellValue(partes_local[0]);
	    		cabecalho3b.getCell(celula).setCellValue(partes_local[2].toUpperCase());
	    	} else {
	    		cabecalho2b.getCell(celula).setCellValue("CÓDIGO");
	    		cabecalho3b.getCell(celula).setCellValue(nome_local);
	    	}
	    	
			cabecalho1b.getCell(celula).setCellValue("");
		    cabecalho1b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			
		    cabecalho4b.getCell(celula).setCellValue("VOLUME DIÁRIO");
		    cabecalho4b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    
		    cabecalho2b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho3b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);

		    sheet.setColumnWidth(celula, 10000);
		    celula++;
	    }
	    
	    cabecalho1b.getCell((celula - 1)).setCellStyle(ce.estiloCabecalhoCorpo2);
	    
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1b.getRowNum(), cabecalho1b.getRowNum(), 2, (celula-1)));
	    
	    incremento = qtdeLinhas;
	    
	    if (dadosRelatorio_faixa.size() == 0) {
        	Row linhaSemInfo = sheet.createRow(incremento);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        } else {
            
        	for(int i = 0; i < dadosRelatorio_faixa.size(); i++) {
            	
               	Row linhaSheet = sheet.createRow((short)(i+incremento));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorio_faixa.get(i).getData());
            	linhaSheet.getCell(0).setCellStyle(ce.estiloCorpo);
            	linhaSheet.getCell(1).setCellValue(dadosRelatorio_faixa.get(i).getDiaSemanaDesc());
            	linhaSheet.getCell(1).setCellStyle(ce.estiloCorpo);
            	
            	celula = 2;
            	
            	for(int h = 3; h < qtdeColunas; h++) {
            		if (dadosRelatorio_faixa.get(i).getCelulasValorColuna()[h] > 0) {
            			linhaSheet.getCell(celula).setCellValue(dadosRelatorio_faixa.get(i).getCelulasValorColuna()[h]);
            		} 
                	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
                	celula++;
        	    }
            }
        }
	    
	    qtdeLinhas += dadosRelatorio_faixa.size() + 3;
	    
	    int linha_total_2 = qtdeLinhas - 3;
	    Row row_total_2 = sheet.createRow(linha_total_2);
	    row_total_2.getCell(0).setCellValue("TOTAL");
	    row_total_2.getCell(0).setCellStyle(ce.estiloItensCentralizadoCinza);
	    row_total_2.getCell(1).setCellValue("");
	    row_total_2.getCell(1).setCellStyle(ce.estiloItensCentralizadoCinza);
	    sheet.addMergedRegion(new CellRangeAddress(linha_total_2, linha_total_2, 0, 1));
	    
	    for(int h = 2; h < qtdeColunas - 1; h++) {
	    	Cell c = row_total_2.getCell(h);
	    	String nome_cel = c.getAddress().formatAsString();
	    	String numero_cel = nome_cel;
	    	while(!NumberUtils.isNumber(numero_cel)) {
	    		numero_cel = numero_cel.substring(1);
	    	}
	    	String nome_col = nome_cel.replace(numero_cel, "");
	    	String formula = "SUM(" + nome_col + String.valueOf( incremento + 1 ) + ":" + nome_col + String.valueOf(linha_total_2) + ")";
	    	
	    	c.setCellFormula(formula);
	    	c.setCellStyle(ce.estiloItensNumCentralizadoCinza);
	    }
	    
		sheet = wb.getSheetAt(0);

        // Salvando o arquivo
        ServletOutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
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
