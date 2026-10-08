package com.consilux.servlet.relatorio.rj;

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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.ExpValida;
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.LocalVigente;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.relatorio.rj.DadosFluxoVeicular;
import com.consilux.model.relatorio.rj.ItemFluxoVeicular;

/**
 * Servlet para a geração de relatório de fluxo veicular por hora. 
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 10/01/2019
 */
public class RelatorioFluxoVeicularPorHora extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatorioFluxoVeicularPorHora.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		@SuppressWarnings("unused")
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
		
		String strMes = request.getParameter("mes");
		String strAno = request.getParameter("ano");
		String strIdLocal = request.getParameter("local").equals("0") ? "" : request.getParameter("local");
		
		Integer intMes = null, intAno = null, intIdLocal = null;
		
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
			
			if(!(strIdLocal.equals(""))){
				if (!ExpValida.NATURAL.validar(strIdLocal)) {
					new Mensagem(response).showErro("Identificador de Local enviado inválido!", "javascript:window.close();");
					return;
				} else {
					intIdLocal = Integer.parseInt(strIdLocal);
				}
			}
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		try {
			
			//Buscar os dados necessários do relatório
 			LocalVigente localVigente = new LocalVigente();
 			ArrayList<LocalVigente> locais = localVigente.buscaListaLocalVigenteRelFluxoRJ(intIdLocal);
            
 			if (locais.isEmpty()) {
				new Mensagem(response).showErro("Não existem dados para geração do relatório!", "javascript:window.close();");
				return;
			}
 			
 			
			// Criando o arquivo fisico
			String nomeArquivo = "Cont_" + numeroContrato + "-" + anoContrato.substring(2,4) + "fluxo_hora-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
	        
			response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");  			
 
	        // Criando area de trabalho para o excel
			SXSSFWorkbook wb = new SXSSFWorkbook();
	        wb.setCompressTempFiles(true);
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
	        //Fonte
            Font f1 = wb.createFont();
            f1.setFontHeightInPoints((short) 10);
            f1.setBold(true);
            f1.setFontName("Calibri");
            f1.setColor(Font.COLOR_NORMAL);
            
            Font f2 = wb.createFont();
            f2.setFontHeightInPoints((short) 10);
            f2.setFontName("Calibri");
            f2.setColor(Font.COLOR_NORMAL);
            
    		Font f3 = wb.createFont();
            f3.setFontHeightInPoints((short) 10);
            f3.setBold(true);
            f3.setFontName("Calibri");
            f3.setColor(Font.COLOR_NORMAL);
            
            Font f4 = wb.createFont();
            f4.setFontHeightInPoints((short) 10);
            f4.setFontName("Calibri");
            f4.setColor(Font.COLOR_NORMAL);
            
            Font f5 = wb.createFont();
            f5.setFontHeightInPoints((short) 14);
            f5.setColor(Font.COLOR_NORMAL);
            f5.setBold(true);
            f5.setFontName("Calibri");
            

            LocalVigente localRelat = new LocalVigente();
 	        for (Integer i = 0; i < locais.size(); i++) {
 	        	
 	        	localRelat = locais.get(i);
 	        	CriarPlanilha(intMes, intAno, response, wb, localRelat, f1, f2, f3, f4, f5);
 	        	
 	        }
 	        
 	        // Salvando o arquivo
 	        ServletOutputStream out = response.getOutputStream();
 	        wb.write(out);
 	        out.close();
 	        
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}

	
	public void CriarPlanilha(Integer intMes, Integer intAno, HttpServletResponse response, SXSSFWorkbook wb, LocalVigente localRelat,
			  Font f1, Font f2, Font f3, Font f4, Font f5
			) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
		
		try {
			
	      //Cria uma planilha Excel
        String strNomePlanilha = null;
		strNomePlanilha = Funcoes.verificaNomeAba(wb, localRelat, RelatorioFluxoVeicularPorHora.class.getSimpleName());
		
//		System.out.println("Id. Local: " + String.format("%02d", localRelat.getIdLocal()) + " / Nome planilha: " + strNomePlanilha);

		Sheet sheet = wb.createSheet(strNomePlanilha);  
        sheet.setVerticallyCenter(true);
        sheet.setHorizontallyCenter(true);
        
        CellStyle cabecalhoTituloRelatorio;
        cabecalhoTituloRelatorio = wb.createCellStyle();
        cabecalhoTituloRelatorio.setAlignment(HorizontalAlignment.CENTER);
        cabecalhoTituloRelatorio.setVerticalAlignment(VerticalAlignment.CENTER);
        cabecalhoTituloRelatorio.setFont(f5);
        
        CellStyle estiloCabelcalhoFiltro;
        estiloCabelcalhoFiltro = wb.createCellStyle();
        estiloCabelcalhoFiltro.setAlignment(HorizontalAlignment.LEFT);
        estiloCabelcalhoFiltro.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabelcalhoFiltro.setFont(f1);
        
        CellStyle estiloFiltro;
        estiloFiltro = wb.createCellStyle();
        estiloFiltro.setAlignment(HorizontalAlignment.LEFT);
        estiloFiltro.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloFiltro.setFont(f2);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoMesAno = new SimpleDateFormat("MM/yyyy");
        
        String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");
		String contrato = String.format("%03d", Integer.parseInt(numeroContrato)) + "/" + anoContrato;
		

		//Nome relatório
        Row cabecalhoTituloRelatorio1 = sheet.createRow(0);
        cabecalhoTituloRelatorio1.getCell(0).setCellValue("RELATÓRIO DE VEÍCULOS POR HORA");
        cabecalhoTituloRelatorio1.getCell(0).setCellStyle(cabecalhoTituloRelatorio);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
        
        Row cabecalhoTituloRelatorio2 = sheet.createRow(1);
        cabecalhoTituloRelatorio2.getCell(0).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
        cabecalhoTituloRelatorio2.getCell(0).setCellStyle(cabecalhoTituloRelatorio);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 13));
        
        //Filtros informados
        Row cabecalhoContrato = sheet.createRow(2);
        cabecalhoContrato.getCell(0).setCellValue("CONTRATO:");
        cabecalhoContrato.getCell(0).setCellStyle(estiloCabelcalhoFiltro);
        cabecalhoContrato.getCell(1).setCellValue(contrato);
        cabecalhoContrato.getCell(1).setCellStyle(estiloFiltro);
//        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 4));
        
        Row cabecalhoReferenciaNumEquip = sheet.createRow(3);
        cabecalhoReferenciaNumEquip.getCell(0).setCellValue("PROCESSADOR:");
        cabecalhoReferenciaNumEquip.getCell(0).setCellStyle(estiloCabelcalhoFiltro);
        cabecalhoReferenciaNumEquip.getCell(1).setCellValue(localRelat.getSerieEquipamento());
        cabecalhoReferenciaNumEquip.getCell(1).setCellStyle(estiloFiltro);
//        sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 4));
        
        Row cabecalhoLogradouro = sheet.createRow(4);
        cabecalhoLogradouro.getCell(0).setCellValue("LOCAL:");
        cabecalhoLogradouro.getCell(0).setCellStyle(estiloCabelcalhoFiltro);
        cabecalhoLogradouro.getCell(1).setCellValue(localRelat.getNome().trim());
        cabecalhoLogradouro.getCell(1).setCellStyle(estiloFiltro);
//        sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 12));
        
        int intLinha = 6;
        
        intLinha = GerarRelatorio(intMes, intAno, intLinha, response, sheet, wb, localRelat, null, f3, f4);
			
        //Buscar as faixas para geração dos tabelas específicas
		LocalVigente localVigente = new LocalVigente();
		ArrayList<LocalVigente> faixas = localVigente.buscaListaLocalVigenteFaixaRelFluxoRJ(localRelat.getIdLocal());
     
        LocalVigente faixaRelat = new LocalVigente();
        for (Integer i = 0; i < faixas.size(); i++) {
        	
        	faixaRelat = faixas.get(i);
        	intLinha = GerarRelatorio(intMes, intAno, intLinha, response, sheet, wb, localRelat, faixaRelat, f3, f4);
        	
        }
        
		return;
        
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	
	
	public int GerarRelatorio(Integer intMes, Integer intAno, int intLinha, HttpServletResponse response,
			   				  Sheet sheet, SXSSFWorkbook wb, LocalVigente localRelat, LocalVigente faixaRelat,
			   				  Font f3, Font f4
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
        ColecaoEstilos ce = new ColecaoEstilos(wb);
        
		//Criando as colunas do corpo
        Row cabecalho1 = null;
        
        if (faixaRelat != null) {
        	
        	cabecalho1 = sheet.createRow((short)intLinha);
        	
        	Integer intFaixa = faixaRelat.getFaixa();
        	
        	cabecalho1.getCell(0).setCellValue("Faixa:");
		    cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoDireita);
		    cabecalho1.getCell(1).setCellValue(intFaixa.toString());
		    cabecalho1.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoEsquerda);
		    
//		    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), 1, 3));
		    
        	intLinha = (intLinha + 2);
        } else {
        	
        	cabecalho1 = sheet.createRow((short)intLinha);
        	
        	cabecalho1.getCell(0).setCellValue("Total das faixas");
		    cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoEsquerdaSemBorda);
		    
        	intLinha = (intLinha + 1);
        	
        }
        
		Row cabecalho2 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho3 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho4 = sheet.createRow((short)intLinha);
        intLinha++;
		
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoMesAno = new SimpleDateFormat("MM/yyyy");
		
		cabecalho2.getCell(0).setCellValue("FLUXO VEICULAR - " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo1);
		cabecalho3.getCell(0).setCellValue("Data");
	    cabecalho3.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoEsquerda);
	    cabecalho4.getCell(0).setCellValue("Horário (h)");
	    cabecalho4.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(0, 3500);
	    
	    
	    Integer celula = 1, intUltimoDiaMes = 31;
	    String strData = null;
	    Date dtData = null;
	    
	    SimpleDateFormat sdfDia = new SimpleDateFormat("dd/MM/yyyy");
	    Calendar calendarioData = Calendar.getInstance();
	    
	    strData = String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01";
    	dtData = sdf.parse(strData);
    	calendarioData.setTime(dtData);
	    intUltimoDiaMes = calendarioData.getActualMaximum(Calendar.DATE);
	    
	    for (Integer dia = 1; dia <= intUltimoDiaMes; dia++) {
	    	
	    	strData = String.valueOf(intAno) + "-" + (String.valueOf(intMes).length() == 1 ? "0" + String.valueOf(intMes) : String.valueOf(intMes)) + "-" + (String.valueOf(dia).length() == 1 ? "0" + String.valueOf(dia) : String.valueOf(dia));
	    	dtData = sdf.parse(strData);
	    	calendarioData.setTime(dtData);
	    	
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho3.getCell(celula).setCellValue(sdfDia.format(dtData));
		    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue("FLUXO VEICULAR");
		    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
			celula++;

	    }
	    
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo2);
	    cabecalho3.getCell(celula).setCellValue("TOTAL");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
	    celula++;
	    
	    
	    
        
	    Integer intIdPista = faixaRelat != null ? faixaRelat.getIdPista() : null;
	    
        //Buscando informações para popular planilhas
	    DadosFluxoVeicular dadosFluxoVeicular = new DadosFluxoVeicular();
	    ArrayList<ItemFluxoVeicular> dadosRelatorio = dadosFluxoVeicular.relatorioFluxoVeicularPorHora(intMes, intAno, localRelat.getIdLocal(), intIdPista);
        
	    int intLinhaInicioDados = intLinha, intLinhaRetorno = intLinhaInicioDados;
	    
        if (dadosRelatorio.size() == 0) {
        	Row linhaSemInfo = sheet.createRow((short)intLinha);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        } else {
        	for (int i = 0; i < dadosRelatorio.size(); i++) {
            	
            	Row linhaSheet = sheet.createRow((short)(i+intLinha));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getHoraDesc());
            	linhaSheet.getCell(0).setCellStyle(ce.estiloItensCentralizado);
            	
            	celula = 1;
            	Integer valorFluxo;

            	for(int h = 0; h < intUltimoDiaMes; h++){
            		valorFluxo = dadosRelatorio.get(i).getCelulasFluxo()[h];
        		   
            		if (valorFluxo != null && valorFluxo > 0) {
	        		   linhaSheet.getCell(celula).setCellValue(valorFluxo);
            		} 
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            		celula++;
            	}
            	
            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicular());
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            }
        	
        	int intLinhaResumo = 0, intLinhaInicioSomaResumo = (intLinhaInicioDados + 1);
            
        	if (dadosRelatorio.size() > 0) {
        		
        		intLinhaResumo = dadosRelatorio.size() + (intLinha);
        		intLinhaRetorno = intLinhaResumo + 1;
        		
        		Row linhaResumo = sheet.createRow((short)(intLinhaResumo));
        		linhaResumo.getCell(0).setCellValue("TOTAL");
        		linhaResumo.getCell(0).setCellStyle(ce.estiloItensCentralizadoCinza);
        		
        		celula = 1;
        		
        		String letraCelulaFluxo = null;
        		
        		for(int h = 0; h <= intUltimoDiaMes; h++) {
        			//FLUXO VEICULAR
        			letraCelulaFluxo = CellReference.convertNumToColString(celula);
        			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaFluxo+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaFluxo+String.valueOf(linhaResumo.getRowNum())+")");
        			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
        			celula++;
        		}
        	}
        }
        
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
