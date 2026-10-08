package com.consilux.servlet.relatorio.rj;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.relatorio.rj.DadosTempoPercurso;
import com.consilux.model.relatorio.rj.ItemTempoPercurso;

/**
 * Servlet para a geração de relatório de Fiscalização Eletrônica - Relatório 02 - Tempo de percurso, volume, velocidade e mobilidade de corredores por hora e data. 
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 22/11/2016
 */
public class Relatorio2TempoPercurso extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(Relatorio2TempoPercurso.class);

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

		SimpleDateFormat mesExtenso = new SimpleDateFormat("MMMM");
		Date dtData = null;
		
		String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");
		
		Integer intMes = null, intAno = null;

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
			
			//Buscar os dados necessários do relatório

			// Criando o arquivo fisico
	        String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_tempo.02-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
	        
	        response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");		
 
	        // Criando area de trabalho para o excel
			SXSSFWorkbook wb = new SXSSFWorkbook();
			wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        Sheet sheet = wb.createSheet("2-Consilux-Tempo-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString()); 
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
	        
	        //Fonte
            Font f = wb.createFont();
            f.setFontHeightInPoints((short) 14);
            f.setColor(Font.COLOR_NORMAL);
            f.setBold(true);
            f.setFontName("Calibri");
            
            CellStyle estilo;
            estilo = wb.createCellStyle();
            estilo.setAlignment(HorizontalAlignment.LEFT);
            estilo.setVerticalAlignment(VerticalAlignment.CENTER);
            estilo.setFont(f);
            
	        //Cria uma linha na Planilha.
            Row cabecalhoContrato = sheet.createRow(0);
            cabecalhoContrato.getCell(0).setCellValue("TEMPO DE PERCURSO, FLUXO VEICULAR, MOBILIDADE E VELOCIDADE DE CORREDORES POR HORA E DATA");
            cabecalhoContrato.getCell(0).setCellStyle(estilo);
            cabecalhoContrato.getCell(1).setCellValue("");
            cabecalhoContrato.getCell(1).setCellStyle(estilo);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
            
            GerarRelatorio(intMes, intAno, response, sheet, wb, userGerador, nomeArquivo, idUsuario, data, usuarioURL);

			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
//			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
			new Mensagem(response).showErro("Erro ao gerar o relatorio em Excel.", "javascript:window.close();");
		}
	}

	
	public void GerarRelatorio(Integer intMes, Integer intAno,
							   HttpServletResponse response, Sheet sheet, SXSSFWorkbook wb, String userGerador,
					 		   String nomeArquivo, Integer idUsuario, Date data, String usuarioURL
		) throws Exception{
	
        ColecaoEstilos ce = new ColecaoEstilos(wb);
        
		//Criando as colunas do corpo
        Row cabecalho0 = sheet.createRow((short)2);
		Row cabecalho1 = sheet.createRow((short)3);
		Row cabecalho2 = sheet.createRow((short)4);
		Row cabecalho3 = sheet.createRow((short)5);
		Row cabecalho4 = sheet.createRow((short)6);
		
		cabecalho0.getCell(0).setCellValue("Dados por pista-sentido");
		cabecalho0.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
		
		cabecalho1.getCell(0).setCellValue("Nome do Corredor");
	    cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho2.getCell(0).setCellValue("");
	    cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(0).setCellValue("");
	    cabecalho3.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(0).setCellValue("");
	    cabecalho4.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(0, 12000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho4.getRowNum(), 0, 0));
	    
	    cabecalho1.getCell(1).setCellValue("Horário");
	    cabecalho1.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho2.getCell(1).setCellValue("");
	    cabecalho2.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(1).setCellValue("");
	    cabecalho3.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(1).setCellValue("");
	    cabecalho4.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(1, 3500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho4.getRowNum(), 1, 1));
	    
	    Integer celula = 2, celulaInicio = celula, celulaFim = celula, intUltimoDiaMes = 31;
	    String strData = null;
	    Date dtData = null;
	    
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    SimpleDateFormat formatoMesAno = new SimpleDateFormat("MM/yyyy");
	    SimpleDateFormat formatoDia = new SimpleDateFormat("dd/MM/yyyy");
	    Locale BRAZIL = new Locale("pt","BR");
	    SimpleDateFormat diaSemana = new SimpleDateFormat("EEEE",BRAZIL);
	    
	    Calendar calendarioData = Calendar.getInstance();
	    List<Integer> finais_de_semana = new ArrayList<Integer>();
	    boolean fds = false;
	    
	    for (Integer dia = 1; dia <= intUltimoDiaMes; dia++) {
	    	
	    	strData = String.valueOf(intAno) + "-" + (String.valueOf(intMes).length() == 1 ? "0" + String.valueOf(intMes) : String.valueOf(intMes)) + "-" + (String.valueOf(dia).length() == 1 ? "0" + String.valueOf(dia) : String.valueOf(dia));
	    	dtData = sdf.parse(strData);
	    	
	    	calendarioData.setTime(dtData);
	    	
	    	if (calendarioData.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendarioData.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
	    		fds = true;
	    		finais_de_semana.add(celula);
	    	} else {
	    		fds = false;
	    	}
	    	
	    	celulaInicio = celula;
	    	cabecalho1.getCell(celula).setCellValue(celula == 2 ? formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")) : "");
		    cabecalho1.getCell(celula).setCellStyle(celula == 2 ? ce.estiloCabecalhoCorpo1 : ce.estiloCabecalhoCorpo3);
		    cabecalho2.getCell(celula).setCellValue(formatoDia.format(dtData));
			cabecalho2.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
			cabecalho3.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho3.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue("Tempo médio");
		    cabecalho4.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
		    celulaFim = celula;
			celula++;
			cabecalho1.getCell(celula).setCellValue("");
		    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			cabecalho2.getCell(celula).setCellValue(formatoDia.format(dtData));
			cabecalho2.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
			cabecalho3.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho3.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue("Indice de Mobilidade (IM)");
		    cabecalho4.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
		    celulaFim = celula;
			celula++;
			cabecalho1.getCell(celula).setCellValue("");
		    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			cabecalho2.getCell(celula).setCellValue(formatoDia.format(dtData));
			cabecalho2.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
			cabecalho3.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho3.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue("Velocidade de Percurso");
		    cabecalho4.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
		    celulaFim = celula;
			celula++;
		    
		    celulaInicio = celula;
		    cabecalho1.getCell(celula).setCellValue("");
		    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho2.getCell(celula).setCellValue(formatoDia.format(dtData));
			cabecalho2.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
			cabecalho3.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho3.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue("Volume Veicular");
		    cabecalho4.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
		    celulaFim = celula;
		    celula++;
	    }
	    
	    celulaInicio = celula;
	    cabecalho1.getCell(celula).setCellValue("");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho2.getCell(celula).setCellValue("MENSAL");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho3.getCell(celula).setCellValue("Mês " + String.valueOf(intMes) + "/" + String.valueOf(intAno));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("Tempo Médio");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
	    celulaFim = celula;
	    celula++;
	    
	    cabecalho1.getCell(celula).setCellValue("");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho2.getCell(celula).setCellValue("MENSAL");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho3.getCell(celula).setCellValue("Mês " + String.valueOf(intMes) + "/" + String.valueOf(intAno));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("Indice de Mobilidade (IM)");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
	    celulaFim = celula;
	    celula++;
	    
	    cabecalho1.getCell(celula).setCellValue("");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho2.getCell(celula).setCellValue("MENSAL");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho3.getCell(celula).setCellValue("Mês " + String.valueOf(intMes) + "/" + String.valueOf(intAno));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("Velocidade de Percurso");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
	    celulaFim = celula;
	    celula++;
	    
	    cabecalho1.getCell(celula).setCellValue("");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo2);
	    cabecalho2.getCell(celula).setCellValue("MENSAL");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho3.getCell(celula).setCellValue("Mês " + String.valueOf(intMes) + "/" + String.valueOf(intAno));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("Volume Veicular");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
	    celulaFim = celula;
	    celula++;
	    
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), celulaInicio, celulaFim));
	    
        
        //Buscando informações para popular planilhas
	    DadosTempoPercurso dadosTempoPercurso = new DadosTempoPercurso();
	    
	    logger.info("Aguardando dados relatorio2TempoPercurso");
	    Date dt_inicio_req = Calendar.getInstance().getTime();
	    ArrayList<ItemTempoPercurso> dadosRelatorio = dadosTempoPercurso.relatorio2TempoPercurso(intMes, intAno);
	    Date dt_fim_req = Calendar.getInstance().getTime();
	    logger.info("TEMPO para obter dados relatorio2TempoPercurso: " + (dt_fim_req.getTime() - dt_inicio_req.getTime()) / 1000);
	    
	    Integer intIdCorredorAnterior = null, intLinhaInicio = 7, intLinhaFim = 7;
	    int incremento = 7;
		
	    if (dadosRelatorio.size() == 0) {
        	Row linhaSemInfo = sheet.createRow((short)incremento);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        } else {
        	for (int i = 0; i < dadosRelatorio.size(); i++) {
            	
			   	if ( (intIdCorredorAnterior != null) && (!intIdCorredorAnterior.equals(dadosRelatorio.get(i).getIdPercurso())) ) {
        			
        			sheet.addMergedRegion(new CellRangeAddress(intLinhaInicio, intLinhaFim, 0, 0));

        			intLinhaInicio = (i+incremento);
        			
        		}
        		
        		intLinhaFim = (i+incremento);
        		
        		Row linhaSheet = sheet.createRow((short)(i+incremento));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getNomePercurso());
            	linhaSheet.getCell(0).setCellStyle(ce.estiloItensCentralizado);
            	linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getHoraDesc());
            	linhaSheet.getCell(1).setCellStyle(dadosRelatorio.get(i).getHora() == 99 ? ce.estiloItensCentralizadoCinza : ce.estiloItensCentralizado);
            	
            	Integer valorTempoMedio;
            	Double valorIndiceMobilidade;
				Integer valorVelocidadePercurso;
				Integer valorVolumeVeicular;
				
				celula = 2;

				for(int h = 0; h < intUltimoDiaMes; h++) {
					
					fds = finais_de_semana.contains(celula);

					valorTempoMedio = dadosRelatorio.get(i).getCelulasTempoMedio()[h];
            		valorIndiceMobilidade = dadosRelatorio.get(i).getCelulasIndiceMobilidade()[h];
            		valorVelocidadePercurso = dadosRelatorio.get(i).getCelulasVelocidadePercurso()[h];
            		valorVolumeVeicular = dadosRelatorio.get(i).getCelulasVolumeVeicular()[h];

               		if ((valorTempoMedio != null && valorTempoMedio > 0) && valorVolumeVeicular > 0) {
            			linhaSheet.getCell(celula).setCellValue(valorTempoMedio);
            		} 
            		linhaSheet.getCell(celula).setCellStyle(dadosRelatorio.get(i).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado);
            		celula++;
 	        		
            		if ((valorIndiceMobilidade != null && valorIndiceMobilidade > 0) && (valorTempoMedio != null && valorTempoMedio > 0) && valorVolumeVeicular > 0) {
            			linhaSheet.getCell(celula).setCellValue(valorIndiceMobilidade);
            		} 
         			linhaSheet.getCell(celula).setCellStyle(dadosRelatorio.get(i).getHora() == 99 ? ce.estiloItensIMCinza : fds ? ce.estiloItensIMVerde : ce.estiloItensIM);
            		celula++;
 	        		
            		if ((valorVelocidadePercurso != null && valorVelocidadePercurso > 0) && (valorTempoMedio != null && valorTempoMedio > 0) && valorVolumeVeicular > 0 ) {
            			linhaSheet.getCell(celula).setCellValue(valorVelocidadePercurso);
            		}
         			linhaSheet.getCell(celula).setCellStyle(dadosRelatorio.get(i).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado);
            		celula++;
            		
            		if (valorVolumeVeicular != null && valorVolumeVeicular > 0) {
            			linhaSheet.getCell(celula).setCellValue(valorVolumeVeicular);
            		} 
         			linhaSheet.getCell(celula).setCellStyle(dadosRelatorio.get(i).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado);
            		celula++;
            	}
				
				
				if ((dadosRelatorio.get(i).getTempoMedio() != null && dadosRelatorio.get(i).getTempoMedio() > 0) && dadosRelatorio.get(i).getVolumeVeicular() > 0) {
					linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getTempoMedio());
				} 
				linhaSheet.getCell(celula).setCellStyle(dadosRelatorio.get(i).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado);
  			   	celula++;
  			   	
  			   	if ((dadosRelatorio.get(i).getIndiceMobilidade() != null && dadosRelatorio.get(i).getIndiceMobilidade() > 0) && (dadosRelatorio.get(i).getTempoMedio() != null && dadosRelatorio.get(i).getTempoMedio() > 0) && dadosRelatorio.get(i).getVolumeVeicular() > 0) {
  			   		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getIndiceMobilidade());
  			   	} 
			   	linhaSheet.getCell(celula).setCellStyle(dadosRelatorio.get(i).getHora() == 99 ? ce.estiloItensIMCinza : fds ? ce.estiloItensIMVerde : ce.estiloItensIM);
			   	celula++;
			   	
			   	if ((dadosRelatorio.get(i).getVelocidadePercurso() != null && dadosRelatorio.get(i).getVelocidadePercurso() > 0) && (dadosRelatorio.get(i).getTempoMedio() != null && dadosRelatorio.get(i).getTempoMedio() > 0) && dadosRelatorio.get(i).getVolumeVeicular() > 0) {
			   		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getVelocidadePercurso());
			   	} 
  			   	linhaSheet.getCell(celula).setCellStyle(dadosRelatorio.get(i).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado);
  			   	celula++;
  			   	
  			   	if (dadosRelatorio.get(i).getVolumeVeicular() != null && dadosRelatorio.get(i).getVolumeVeicular() > 0) {
  			   		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getVolumeVeicular());
  			   	} 
			   	linhaSheet.getCell(celula).setCellStyle(dadosRelatorio.get(i).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado);
			   	celula++;
				
				intIdCorredorAnterior = dadosRelatorio.get(i).getIdPercurso();
        	}
        	
        	sheet.addMergedRegion(new CellRangeAddress(intLinhaInicio, intLinhaFim, 0, 0));
        }
	    
	    
	    
	    
	    int qtdeLinhas = dadosRelatorio.size()+incremento+3;
	    
        // TODO: Segunda parte
		//Criando as colunas do corpo
        Row cabecalho0b   = sheet.createRow(qtdeLinhas++); // qtdeLinhas_ref
		Row cabecalho1b   = sheet.createRow(qtdeLinhas++); // qtdeLinhas_ref + 1
		Row cabecalho2b   = sheet.createRow(qtdeLinhas++); // qtdeLinhas_ref + 2
		Row cabecalho3b   = sheet.createRow(qtdeLinhas++); // qtdeLinhas_ref + 3
		Row cabecalho4b   = sheet.createRow(qtdeLinhas++); // qtdeLinhas_ref + 4
		
		cabecalho0b.getCell(0).setCellValue("Dados por pista-sentido-faixa");
		cabecalho0b.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
		
		cabecalho1b.getCell(0).setCellValue("Nome do Corredor");
	    cabecalho1b.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho2b.getCell(0).setCellValue("");
	    cabecalho2b.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3b.getCell(0).setCellValue("");
	    cabecalho3b.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4b.getCell(0).setCellValue("");
	    cabecalho4b.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1b.getRowNum(), cabecalho4b.getRowNum(), 0, 0));
	    
	    cabecalho1b.getCell(1).setCellValue("Horário");
	    cabecalho1b.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho2b.getCell(1).setCellValue("");
	    cabecalho2b.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3b.getCell(1).setCellValue("");
	    cabecalho3b.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4b.getCell(1).setCellValue("");
	    cabecalho4b.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1b.getRowNum(), cabecalho4b.getRowNum(), 1, 1));
	    
	    celula = 2;
	    celulaInicio = celula;
	    celulaFim = celula;
	    intUltimoDiaMes = 31;
	    
	    strData = null;
	    dtData = null;
	    
	    for (Integer dia = 1; dia <= intUltimoDiaMes; dia++) {
	    	
	    	strData = String.valueOf(intAno) + "-" + (String.valueOf(intMes).length() == 1 ? "0" + String.valueOf(intMes) : String.valueOf(intMes)) + "-" + (String.valueOf(dia).length() == 1 ? "0" + String.valueOf(dia) : String.valueOf(dia));
	    	dtData = sdf.parse(strData);
	    	
	    	fds = finais_de_semana.contains(celula);
	    	
	    	celulaInicio = celula;
	    	cabecalho1b.getCell(celula).setCellValue(celula == 2 ? formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")) : "");
		    cabecalho1b.getCell(celula).setCellStyle(celula == 2 ? ce.estiloCabecalhoCorpo1 : ce.estiloCabecalhoCorpo3);
		    cabecalho2b.getCell(celula).setCellValue(formatoDia.format(dtData));
			cabecalho2b.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
			cabecalho3b.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho3b.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4b.getCell(celula).setCellValue("Tempo médio");
		    cabecalho4b.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    celulaFim = celula;
			celula++;
			cabecalho1b.getCell(celula).setCellValue("");
		    cabecalho1b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			cabecalho2b.getCell(celula).setCellValue(formatoDia.format(dtData));
			cabecalho2b.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
			cabecalho3b.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho3b.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4b.getCell(celula).setCellValue("Indice de Mobilidade (IM)");
		    cabecalho4b.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    celulaFim = celula;
			celula++;
			cabecalho1b.getCell(celula).setCellValue("");
		    cabecalho1b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			cabecalho2b.getCell(celula).setCellValue(formatoDia.format(dtData));
			cabecalho2b.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
			cabecalho3b.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho3b.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4b.getCell(celula).setCellValue("Velocidade de Percurso");
		    cabecalho4b.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    celulaFim = celula;
			celula++;
		    
		    celulaInicio = celula;
		    cabecalho1b.getCell(celula).setCellValue("");
		    cabecalho1b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho2b.getCell(celula).setCellValue(formatoDia.format(dtData));
			cabecalho2b.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
			cabecalho3b.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho3b.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4b.getCell(celula).setCellValue("Volume Veicular");
		    cabecalho4b.getCell(celula).setCellStyle(fds ? ce.estiloCabecalhoCorpoNegritoVerde : ce.estiloCabecalhoCorpoNegrito);
		    celulaFim = celula;
		    celula++;
	    }
	    
	    celulaInicio = celula;
	    cabecalho1b.getCell(celula).setCellValue("");
	    cabecalho1b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho2b.getCell(celula).setCellValue("MENSAL");
		cabecalho2b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho3b.getCell(celula).setCellValue("Mês " + String.valueOf(intMes) + "/" + String.valueOf(intAno));
	    cabecalho3b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4b.getCell(celula).setCellValue("Tempo Médio");
	    cabecalho4b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    celulaFim = celula;
	    celula++;
	    
	    cabecalho1b.getCell(celula).setCellValue("");
	    cabecalho1b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho2b.getCell(celula).setCellValue("MENSAL");
		cabecalho2b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho3b.getCell(celula).setCellValue("Mês " + String.valueOf(intMes) + "/" + String.valueOf(intAno));
	    cabecalho3b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4b.getCell(celula).setCellValue("Indice de Mobilidade (IM)");
	    cabecalho4b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    celulaFim = celula;
	    celula++;
	    
	    cabecalho1b.getCell(celula).setCellValue("");
	    cabecalho1b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho2b.getCell(celula).setCellValue("MENSAL");
		cabecalho2b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho3b.getCell(celula).setCellValue("Mês " + String.valueOf(intMes) + "/" + String.valueOf(intAno));
	    cabecalho3b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4b.getCell(celula).setCellValue("Velocidade de Percurso");
	    cabecalho4b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    celulaFim = celula;
	    celula++;
	    
	    cabecalho1b.getCell(celula).setCellValue("");
	    cabecalho1b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo2);
	    cabecalho2b.getCell(celula).setCellValue("MENSAL");
		cabecalho2b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho3b.getCell(celula).setCellValue("Mês " + String.valueOf(intMes) + "/" + String.valueOf(intAno));
	    cabecalho3b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4b.getCell(celula).setCellValue("Volume Veicular");
	    cabecalho4b.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    celulaFim = celula;
	    celula++;
	    
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2b.getRowNum(), cabecalho2b.getRowNum(), celulaInicio, celulaFim));
	    
        
        //Buscando informações para popular planilhas
	    logger.info("Aguardando dados relatorio2TempoPercursoFaixa");
	    dt_inicio_req = Calendar.getInstance().getTime();
	    ArrayList<ItemTempoPercurso> dadosRelatorioFaixa = dadosTempoPercurso.relatorio2TempoPercursoFaixa(intMes, intAno);
	    dt_fim_req = Calendar.getInstance().getTime();
	    logger.info("TEMPO para obter dados relatorio2TempoPercursoFaixa: " + (dt_fim_req.getTime() - dt_inicio_req.getTime()) / 1000);
	    
	    incremento = qtdeLinhas;
	    
	    String strCorredorAnterior = null;
	    intLinhaInicio = incremento;
	    intLinhaFim = incremento;
	    
	    if (dadosRelatorioFaixa.size() == 0) {
        	Row linhaSemInfo = sheet.createRow((short)incremento);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        } else {
        	for (int i = 0; i < dadosRelatorioFaixa.size(); i++) {
            	
			   	if ( (strCorredorAnterior != null) && (!strCorredorAnterior.trim().equals(dadosRelatorioFaixa.get(i).getNomePercurso().trim())) ) {
        			
        			sheet.addMergedRegion(new CellRangeAddress(intLinhaInicio, intLinhaFim, 0, 0));

        			intLinhaInicio = (i+incremento);
        			
        		}
        		
        		intLinhaFim = (i+incremento);
        		
        		Row linhaSheet = sheet.createRow((short)(i+incremento));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorioFaixa.get(i).getNomePercurso());
            	linhaSheet.getCell(0).setCellStyle(ce.estiloItensCentralizado);
            	linhaSheet.getCell(1).setCellValue(dadosRelatorioFaixa.get(i).getHoraDesc());
            	linhaSheet.getCell(1).setCellStyle(dadosRelatorioFaixa.get(i).getHora() == 99 ? ce.estiloItensCentralizadoCinza : ce.estiloItensCentralizado);
            	
            	Integer valorTempoMedio;
            	Double valorIndiceMobilidade;
				Integer valorVelocidadePercurso;
				Integer valorVolumeVeicular;
				
				celula = 2;

				for(int h = 0; h < intUltimoDiaMes; h++) {
					
					fds = finais_de_semana.contains(celula);

					valorTempoMedio = dadosRelatorioFaixa.get(i).getCelulasTempoMedio()[h];
            		valorIndiceMobilidade = dadosRelatorioFaixa.get(i).getCelulasIndiceMobilidade()[h];
            		valorVelocidadePercurso = dadosRelatorioFaixa.get(i).getCelulasVelocidadePercurso()[h];
            		valorVolumeVeicular = dadosRelatorioFaixa.get(i).getCelulasVolumeVeicular()[h];

            		if ((valorTempoMedio != null && valorTempoMedio > 0) && valorVolumeVeicular > 0) {
            			linhaSheet.getCell(celula).setCellValue(valorTempoMedio);
            		} 
            		linhaSheet.getCell(celula).setCellStyle(dadosRelatorioFaixa.get(i).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado);
            		celula++;
 	        		
            		if ((valorIndiceMobilidade != null && valorIndiceMobilidade > 0) && (valorTempoMedio != null && valorTempoMedio > 0) && valorVolumeVeicular > 0) {
            			linhaSheet.getCell(celula).setCellValue(valorIndiceMobilidade);
            		}
         			linhaSheet.getCell(celula).setCellStyle(dadosRelatorioFaixa.get(i).getHora() == 99 ? ce.estiloItensIMCinza : fds ? ce.estiloItensIMVerde : ce.estiloItensIM);
            		celula++;
 	        		
            		if ((valorVelocidadePercurso != null && valorVelocidadePercurso > 0) && (valorTempoMedio != null && valorTempoMedio > 0) && valorVolumeVeicular > 0 ) {
            			linhaSheet.getCell(celula).setCellValue(valorVelocidadePercurso);
            		} 
         			linhaSheet.getCell(celula).setCellStyle(dadosRelatorioFaixa.get(i).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado);
            		celula++;
            		
            		if (valorVolumeVeicular != null && valorVolumeVeicular > 0) {
            			linhaSheet.getCell(celula).setCellValue(valorVolumeVeicular);
            		} 
         			linhaSheet.getCell(celula).setCellStyle(dadosRelatorioFaixa.get(i).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado);
            		celula++;
            	}
				
				
				if ((dadosRelatorioFaixa.get(i).getTempoMedio() != null && dadosRelatorioFaixa.get(i).getTempoMedio() > 0) && dadosRelatorioFaixa.get(i).getVolumeVeicular() > 0) {
					linhaSheet.getCell(celula).setCellValue(dadosRelatorioFaixa.get(i).getTempoMedio());
				} 
				linhaSheet.getCell(celula).setCellStyle(dadosRelatorioFaixa.get(i).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado);
  			   	celula++;
  			   	
  			   	if ((dadosRelatorioFaixa.get(i).getIndiceMobilidade() != null && dadosRelatorioFaixa.get(i).getIndiceMobilidade() > 0) && (dadosRelatorioFaixa.get(i).getTempoMedio() != null && dadosRelatorioFaixa.get(i).getTempoMedio() > 0) && dadosRelatorioFaixa.get(i).getVolumeVeicular() > 0) {
  			   		linhaSheet.getCell(celula).setCellValue(dadosRelatorioFaixa.get(i).getIndiceMobilidade());
  			   	} 
			   	linhaSheet.getCell(celula).setCellStyle(dadosRelatorioFaixa.get(i).getHora() == 99 ? ce.estiloItensIMCinza : fds ? ce.estiloItensIMVerde : ce.estiloItensIM);
			   	celula++;
			   	
			   	if ((dadosRelatorioFaixa.get(i).getVelocidadePercurso() != null && dadosRelatorioFaixa.get(i).getVelocidadePercurso() > 0) && (dadosRelatorioFaixa.get(i).getTempoMedio() != null && dadosRelatorioFaixa.get(i).getTempoMedio() > 0) && dadosRelatorioFaixa.get(i).getVolumeVeicular() > 0) {
			   		linhaSheet.getCell(celula).setCellValue(dadosRelatorioFaixa.get(i).getVelocidadePercurso());
			   	} 
  			   	linhaSheet.getCell(celula).setCellStyle(dadosRelatorioFaixa.get(i).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado);
  			   	celula++;
  			   	
  			   	if (dadosRelatorioFaixa.get(i).getVolumeVeicular() != null && dadosRelatorioFaixa.get(i).getVolumeVeicular() > 0) {
  			   		linhaSheet.getCell(celula).setCellValue(dadosRelatorioFaixa.get(i).getVolumeVeicular());
  			   	} 
			   	linhaSheet.getCell(celula).setCellStyle(dadosRelatorioFaixa.get(i).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado);
			   	celula++;
				
			   	strCorredorAnterior = dadosRelatorioFaixa.get(i).getNomePercurso();
        	}
        	
        	sheet.addMergedRegion(new CellRangeAddress(intLinhaInicio, intLinhaFim, 0, 0));
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
