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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
 * Servlet para a geração de relatório de Fiscalização Eletrônica - Relatório 04 - Cada endereço mensal por data - volume veicular por porte veicular. 
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 26/09/2016
 */
public class Relatorio4FluxoVeicular extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(Relatorio4FluxoVeicular.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String usuarioURL = request.getParameter("sistema");
		if(usuarioURL == null){
			final Acesso acessoUsuario = new Acesso(request, response, true);
			if (!acessoUsuario.verificaAcesso()){return;}else{
				usuarioURL = acessoUsuario.getUsuario().getNome();
			}
		}else
			usuarioURL = "SISTEMA";
		
        Date data = new Date();  
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
        String userGerador = "Relatório gerado em: " + formatador.format(data) + ", pelo usuário: " + usuarioURL;
		
		String strMes = request.getParameter("mes");
		String strAno = request.getParameter("ano");
		String strIdLocal = request.getParameter("local").equals("0") ? "" : request.getParameter("local");
		
		Integer intMes = null, intAno = null, intIdLocal = null;
		
		String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");
		SimpleDateFormat mesExtenso = new SimpleDateFormat("MMMM");
		Date dtData = null;


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
			
			if(!(strIdLocal.equals(""))){
				if (!ExpValida.NATURAL.validar(strIdLocal)) {
					new Mensagem(response).showErro("Identificador de Local enviado inválido!", "javascript:window.close();");
					return;
				} else {
					intIdLocal = Integer.parseInt(strIdLocal);
				}
			}
			
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
 			LocalVigente localVigente = new LocalVigente();
 			ArrayList<LocalVigente> locais = localVigente.buscaListaLocalVigenteRelFluxoRJ(intIdLocal);
            
 			if (locais.isEmpty()) {
				new Mensagem(response).showErro("Não existem dados para geração do relatório!", "javascript:window.close();");
				return;
			}
 			
 			
			// Criando o arquivo fisico
			String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_estat.04-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
	        
			response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");  			
 
	        // Criando area de trabalho para o excel
			SXSSFWorkbook wb = new SXSSFWorkbook();
	        wb.setCompressTempFiles(true);
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
	        ColecaoEstilos ce = new ColecaoEstilos(wb);
             
 	        LocalVigente localRelat = new LocalVigente();
 	        for (Integer i = 0; i < locais.size(); i++) {
 	        	
 	        	localRelat = locais.get(i);
 	        	CriarPlanilha(intMes, intAno, response, wb, userGerador, localRelat, ce);
 	        	
 	        }
 	        
 	        {
	 	        // Salvando o arquivo
	 	        ServletOutputStream out = response.getOutputStream();
	 	        wb.write(out);
	 	        out.flush();
	 	        out.close();
	 	        wb.dispose();
	 	        
	 	        localVigente = null;
	 	        locais = null;
	 	        wb = null;
	 	        out = null;
	 	        response = null;
 	        }
 	        
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	
	
	public void CriarPlanilha(Integer intMes, Integer intAno, HttpServletResponse response, Workbook wb, String userGerador, LocalVigente localRelat, ColecaoEstilos ce
			) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
		
		try {
			
			//Cria uma planilha Excel
			String strNomePlanilha = null;	
			strNomePlanilha = Funcoes.verificaNomeAba(wb, localRelat, Relatorio4FluxoVeicular.class.getSimpleName());
			
//			System.out.println("Id. Local: " + String.format("%02d", localRelat.getIdLocal()) + " / Nome planilha: " + strNomePlanilha);
			
			Sheet sheet = wb.createSheet(strNomePlanilha);
			sheet.setVerticallyCenter(true);
			sheet.setHorizontallyCenter(true);
		  
		    String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
		    String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");
		    String contrato = String.format("%03d", Integer.parseInt(numeroContrato)) + "/" + anoContrato;
		
			//Nome relatório
			Row cabecalhoTituloRelatorio1 = sheet.createRow(0);
			cabecalhoTituloRelatorio1.getCell(0).setCellValue("FISCALIZAÇÃO ELETRÔNICA - RELATÓRIO 04");
			cabecalhoTituloRelatorio1.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
          
			Row cabecalhoTituloRelatorio2 = sheet.createRow(1);
			cabecalhoTituloRelatorio2.getCell(0).setCellValue("FLUXO VEICULAR DIÁRIO POR FAIXA HORÁRIA, CLASSIFICADO, PARA CADA MÊS, POR ENDEREÇO");
			cabecalhoTituloRelatorio2.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));
          
            Row cabecalhoContrato = sheet.createRow(2);
            cabecalhoContrato.getCell(0).setCellValue("Nº DO CONTRATO:");
            cabecalhoContrato.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoContrato.getCell(1).setCellValue(contrato);
            cabecalhoContrato.getCell(1).setCellStyle(ce.estiloFiltro);
            
            Row cabecalhoLogradouro = sheet.createRow(3);
            cabecalhoLogradouro.getCell(0).setCellValue("LOCAL:");
            cabecalhoLogradouro.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoLogradouro.getCell(1).setCellValue(localRelat.getNome().trim());
            cabecalhoLogradouro.getCell(1).setCellStyle(ce.estiloFiltro);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 5));
            
            cabecalhoLogradouro.getCell(7).setCellValue("CÓDIGO:");
            cabecalhoLogradouro.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoLogradouro.getCell(8).setCellValue(localRelat.getSerieEquipamento());
            cabecalhoLogradouro.getCell(8).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(3, 3, 6, 7));
//            sheet.addMergedRegion(new CellRangeAddress(3, 3, 8, 9));
            
            Row cabecalhoReferenciaNumEquip = sheet.createRow(4);
//            cabecalhoReferenciaNumEquip.getCell(0).setCellValue("REFERÊNCIA:");
//            cabecalhoReferenciaNumEquip.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
//            cabecalhoReferenciaNumEquip.getCell(1).setCellValue(localRelat.getReferencia() == null ? "" : localRelat.getReferencia().trim());
//            cabecalhoReferenciaNumEquip.getCell(1).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 5));
            
            cabecalhoReferenciaNumEquip.getCell(7).setCellValue("EQUIPAMENTO:");
            cabecalhoReferenciaNumEquip.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoReferenciaNumEquip.getCell(8).setCellValue(localRelat.getCodigosEquipamentosDER() == null ? "" : localRelat.getCodigosEquipamentosDER().trim());
            cabecalhoReferenciaNumEquip.getCell(8).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(4, 4, 6, 7));
//            sheet.addMergedRegion(new CellRangeAddress(4, 4, 8, 9));
            
            Row cabecalhoPistaSentido = sheet.createRow(5);
//            cabecalhoPistaSentido.getCell(0).setCellValue("EQUIPAMENTO:");
//            cabecalhoPistaSentido.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
//            cabecalhoPistaSentido.getCell(1).setCellValue(localRelat.getCodigosEquipamentosDER() == null ? "" : localRelat.getCodigosEquipamentosDER().trim());
//            cabecalhoPistaSentido.getCell(1).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(5, 5, 1, 5));
            
			cabecalhoPistaSentido.getCell(0).setCellValue("TOTAL");
			cabecalhoPistaSentido.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            
            cabecalhoPistaSentido.getCell(7).setCellValue("COORDENADA:");
            cabecalhoPistaSentido.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoPistaSentido.getCell(8).setCellValue(Funcoes.converterDecimalParaDMS(localRelat.getLatitude(), localRelat.getLongitude()));
            cabecalhoPistaSentido.getCell(8).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(5, 5, 6, 7));
//            sheet.addMergedRegion(new CellRangeAddress(5, 5, 8, 9));
			
			
			int intLinha = 7;
            
            intLinha = GerarRelatorio(intMes, intAno, intLinha, response, sheet, wb, userGerador, localRelat, null, ce);
			
            //Buscar as faixas para geração dos tabelas específicas
 			LocalVigente localVigente = new LocalVigente();
 			ArrayList<LocalVigente> faixas = localVigente.buscaListaLocalVigenteFaixaRelFluxoRJ(localRelat.getIdLocal());
             
 	        LocalVigente faixaRelat = new LocalVigente();
 	        for (Integer i = 0; i < faixas.size(); i++) {
 	        	
 	        	faixaRelat = faixas.get(i);
 	        	intLinha = GerarRelatorio(intMes, intAno, intLinha, response, sheet, wb, userGerador, localRelat, faixaRelat, ce);
 	        	
 	        }
            
            return;
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}

	
	public int GerarRelatorio(Integer intMes, Integer intAno, int intLinha, HttpServletResponse response,
			   Sheet sheet, Workbook wb, String userGerador, LocalVigente localRelat, LocalVigente faixaRelat, ColecaoEstilos ce
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
        
		//Criando as colunas do corpo
        Row cabecalho1 = null;
        
        if (faixaRelat != null) {
        	
        	cabecalho1 = sheet.createRow((short)intLinha);
        	
        	Integer intFaixa = faixaRelat.getFaixa();
        	
        	cabecalho1.getCell(0).setCellValue("PISTA-SENTIDO:");
		    cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
		    cabecalho1.getCell(1).setCellValue(intFaixa.toString());
		    cabecalho1.getCell(1).setCellStyle(ce.estiloFiltro);
		    
		    cabecalho1.getCell(7).setCellValue("EQUIPAMENTO:");
		    cabecalho1.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
//		    cabecalho1.getCell(7).setCellValue("");
//		    cabecalho1.getCell(7).setCellStyle(ce.estiloCabecalhoCorpoDireita);
		    cabecalho1.getCell(8).setCellValue(faixaRelat.getCodigoEquipamentoDER());
		    cabecalho1.getCell(8).setCellStyle(ce.estiloFiltro);
		    
//		    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), 6, 7));
		    
        	intLinha = (intLinha + 2);
        } /*else {
        	
        	cabecalho1 = sheet.createRow((short)intLinha);
        	
        	cabecalho1.getCell(0).setCellValue("Total das faixas");
		    cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoEsquerdaSemBorda);
		    
        	intLinha = (intLinha + 1);
        	
        }*/
        
		Row cabecalho2 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho3 = sheet.createRow((short)intLinha);
        intLinha++;
        Row cabecalho4 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho5 = sheet.createRow((short)intLinha);
        intLinha++;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatoMesAno = new SimpleDateFormat("MM/yyyy");
		
		cabecalho2.getCell(0).setCellValue("FLUXO VEICULAR CLASSIFICADO - " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo1);
		cabecalho3.getCell(0).setCellValue("Data");
	    cabecalho3.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(0).setCellValue("Dia da Semana");
	    cabecalho4.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(0).setCellValue("Horário (h)");
	    cabecalho5.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(0, 4600);
    
	    Integer celula = 1, intUltimoDiaMes = 31;
	    String strData = null;
	    String[] strDiaSemana = null;
	    Date dtData = null;
	    
	    SimpleDateFormat sdfDia = new SimpleDateFormat("dd/MM/yyyy");
	    SimpleDateFormat diaSemana = new SimpleDateFormat("EEEE");
	    Calendar calendarioData = Calendar.getInstance();
	    
	    strData = String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01";
    	dtData = sdf.parse(strData);
    	calendarioData.setTime(dtData);
	    intUltimoDiaMes = calendarioData.getActualMaximum(Calendar.DATE);
	    
	    for (Integer dia = 1; dia <= intUltimoDiaMes; dia++) {
	    	
	    	strData = String.valueOf(intAno) + "-" + (String.valueOf(intMes).length() == 1 ? "0" + String.valueOf(intMes) : String.valueOf(intMes)) + "-" + (String.valueOf(dia).length() == 1 ? "0" + String.valueOf(dia) : String.valueOf(dia));
	    	dtData = sdf.parse(strData);
	    	calendarioData.setTime(dtData);
	    	
	    	strDiaSemana = diaSemana.format(dtData).split("-");
	    	
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho3.getCell(celula).setCellValue(sdfDia.format(dtData));
			cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho4.getCell(celula).setCellValue(strDiaSemana[0].toUpperCase());
			cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho5.getCell(celula).setCellValue("MOTOS");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
			celula++;
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho3.getCell(celula).setCellValue(sdfDia.format(dtData));
			cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho4.getCell(celula).setCellValue(strDiaSemana[0].toUpperCase());
			cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho5.getCell(celula).setCellValue("VEÍCULO PEQUENO");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
			celula++;
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho3.getCell(celula).setCellValue(sdfDia.format(dtData));
			cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho4.getCell(celula).setCellValue(strDiaSemana[0].toUpperCase());
			cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho5.getCell(celula).setCellValue("VEÍCULO MÉDIO");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
			celula++;
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho3.getCell(celula).setCellValue(sdfDia.format(dtData));
			cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho4.getCell(celula).setCellValue(strDiaSemana[0].toUpperCase());
			cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho5.getCell(celula).setCellValue("VEÍCULO GRANDE");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
		    celula++;
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho3.getCell(celula).setCellValue(sdfDia.format(dtData));
			cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho4.getCell(celula).setCellValue(strDiaSemana[0].toUpperCase());
			cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho5.getCell(celula).setCellValue("SEM IDENTIFICAÇÃO");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
			celula++;
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho3.getCell(celula).setCellValue(sdfDia.format(dtData));
			cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho4.getCell(celula).setCellValue(strDiaSemana[0].toUpperCase());
			cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho5.getCell(celula).setCellValue("TOTAL");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
		    celula++;
		    
	    }
	    
	    
	    //TOTAIS DO MÊS
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
		cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho4.getCell(celula).setCellValue("");
		cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho5.getCell(celula).setCellValue("MOTOS");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2600);
		celula++;
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
		cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho4.getCell(celula).setCellValue("");
		cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho5.getCell(celula).setCellValue("VEÍCULO PEQUENO");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2600);
		celula++;
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
		cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho4.getCell(celula).setCellValue("");
		cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho5.getCell(celula).setCellValue("VEÍCULO MÉDIO");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		celula++;
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
		cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho4.getCell(celula).setCellValue("");
		cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho5.getCell(celula).setCellValue("VEÍCULO GRANDE");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2600);
	    celula++;
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
		cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho4.getCell(celula).setCellValue("");
		cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho5.getCell(celula).setCellValue("SEM IDENTIFICAÇÃO");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3400);
		celula++;
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo2);
	    cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
		cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho4.getCell(celula).setCellValue("");
		cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho5.getCell(celula).setCellValue("TOTAL");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    celula++;
	    
	    
	    Integer intIdPista = faixaRelat != null ? faixaRelat.getIdPista() : null;
        
        //Buscando informações para popular planilhas
		DadosFluxoVeicular dadosFluxoVeicular = new DadosFluxoVeicular();
		ArrayList<ItemFluxoVeicular> dadosRelatorio = dadosFluxoVeicular.relatorio4FluxoVeicular(intMes, intAno, localRelat.getIdLocal(), intIdPista);
		
		int intLinhaInicioDados = intLinha, intLinhaRetorno = intLinhaInicioDados;
		
	    if (dadosRelatorio.size() == 0) {
        	Row linhaSemInfo = sheet.createRow((short)intLinha);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        } else {
        	for (int i = 0; i < dadosRelatorio.size(); i++) {
            	
            	Row linhaSheet = sheet.createRow((short)(i+intLinha));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getHoraDesc());
            	linhaSheet.getCell(0).setCellStyle(ce.estiloItensCentralizado);
            	
            	Integer valorFluxoMoto;
				Integer valorFluxoPequeno;
				Integer valorFluxoMedio;
				Integer valorFluxoGrande;
				Integer valorFluxoSemId;
				Integer valorFluxo;
				
				celula = 1;

				for(int h = 0; h < intUltimoDiaMes; h++) {

					valorFluxoMoto = dadosRelatorio.get(i).getCelulasFluxoMoto()[h];
            		valorFluxoPequeno = dadosRelatorio.get(i).getCelulasFluxoPequeno()[h];
            		valorFluxoMedio = dadosRelatorio.get(i).getCelulasFluxoMedio()[h];
            		valorFluxoGrande = dadosRelatorio.get(i).getCelulasFluxoGrande()[h];
            		valorFluxoSemId = dadosRelatorio.get(i).getCelulasFluxoSemId()[h];
            		valorFluxo = dadosRelatorio.get(i).getCelulasFluxo()[h];

            		if (valorFluxoMoto != null && valorFluxoMoto > 0) {
        				linhaSheet.getCell(celula).setCellValue(valorFluxoMoto);
            		}
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
             		celula++;
             		
            		if (valorFluxoPequeno != null && valorFluxoPequeno > 0) {
 	        		   	linhaSheet.getCell(celula).setCellValue(valorFluxoPequeno);
            		}
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
             		celula++;
             		
            		if (valorFluxoMedio != null && valorFluxoMedio > 0) {
 	        		   	linhaSheet.getCell(celula).setCellValue(valorFluxoMedio);
            		} 
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
             		celula++;
             		
            		if (valorFluxoGrande != null && valorFluxoGrande > 0) {
 	        		   	linhaSheet.getCell(celula).setCellValue(valorFluxoGrande);
            		} 
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
             		celula++;
            		
             		if (valorFluxoSemId != null && valorFluxoSemId > 0) {
   	        		   	linhaSheet.getCell(celula).setCellValue(valorFluxoSemId);
             		} 
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
               		celula++;
               		
            		if (valorFluxo != null && valorFluxo > 0) {
  	        		   	linhaSheet.getCell(celula).setCellValue(valorFluxo);
            		}
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
              		celula++;
            	}
				
				if (dadosRelatorio.get(i).getFluxoVeicularMoto() > 0) {
					linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicularMoto());
				} 
      			linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
          		celula++;
          		
          		if (dadosRelatorio.get(i).getFluxoVeicularPequeno() > 0) {
          			linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicularPequeno());
          		} 
      			linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
          		celula++;
          		
          		if (dadosRelatorio.get(i).getFluxoVeicularMedio() > 0) {
          			linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicularMedio());
          		} 
      			linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
          		celula++;
          		
          		if (dadosRelatorio.get(i).getFluxoVeicularGrande() > 0) {
          			linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicularGrande());
          		} 
      			linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
          		celula++;
          		
          		if (dadosRelatorio.get(i).getFluxoVeicularSemId() > 0) {
          			linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicularSemId());
          		} 
      			linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
          		celula++;
          		
          		if (dadosRelatorio.get(i).getFluxoVeicular() > 0) {
          			linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicular());
          		} 
      			linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
          		celula++;
        	}
        }
	    
	    int intLinhaResumo = 0, intLinhaInicioSomaResumo = (intLinhaInicioDados + 1);
	    
	    if (dadosRelatorio.size() > 0) {
		    
	    	intLinhaResumo = dadosRelatorio.size() + (intLinha);
    		intLinhaRetorno = intLinhaResumo + 1;
    		
    		Row linhaResumo = sheet.createRow((short)(intLinhaResumo));
    		linhaResumo.getCell(0).setCellValue("RESUMO");
    		linhaResumo.getCell(0).setCellStyle(ce.estiloItensCentralizadoCinza);
    		
    		celula = 1;
    		
    		String letraCelulaSemId = null, letraCelulaMoto = null, letraCelulaVeiculoPequeno = null, letraCelulaVeiculoMedio = null,
    				letraCelulaVeiculoGrande = null, letraCelulaTotal = null;
    		
    		for(int h = 0; h < intUltimoDiaMes; h++) {
    			//MOTOS
    			letraCelulaMoto = CellReference.convertNumToColString(celula);
    			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaMoto+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaMoto+String.valueOf(linhaResumo.getRowNum())+")");
    			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
    			celula++;
    			
    			//VEÍCULO PEQUENO
    			letraCelulaVeiculoPequeno = CellReference.convertNumToColString(celula);
    			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaVeiculoPequeno+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaVeiculoPequeno+String.valueOf(linhaResumo.getRowNum())+")");
    			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
    			celula++;
    			
    			//VEÍCULO MÉDIO
    			letraCelulaVeiculoMedio = CellReference.convertNumToColString(celula);
    			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaVeiculoMedio+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaVeiculoMedio+String.valueOf(linhaResumo.getRowNum())+")");
    			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
    			celula++;
    			
    			//VEÍCULO GRANDE
    			letraCelulaVeiculoGrande = CellReference.convertNumToColString(celula);
    			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaVeiculoGrande+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaVeiculoGrande+String.valueOf(linhaResumo.getRowNum())+")");
    			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
    			celula++;
    			
    			//SEM IDENTIFICAÇÃO
    			letraCelulaSemId = CellReference.convertNumToColString(celula);
    			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaSemId+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaSemId+String.valueOf(linhaResumo.getRowNum())+")");
    			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
    			celula++;
    			
    			//TOTAL
    			letraCelulaTotal = CellReference.convertNumToColString(celula);
    			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaTotal+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaTotal+String.valueOf(linhaResumo.getRowNum())+")");
    			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
    			celula++;
    		}
    		
    		
			//MOTOS
			letraCelulaMoto = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaMoto+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaMoto+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//VEÍCULO PEQUENO
			letraCelulaVeiculoPequeno = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaVeiculoPequeno+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaVeiculoPequeno+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//VEÍCULO MÉDIO
			letraCelulaVeiculoMedio = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaVeiculoMedio+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaVeiculoMedio+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//VEÍCULO GRANDE
			letraCelulaVeiculoGrande = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaVeiculoGrande+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaVeiculoGrande+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//SEM IDENTIFICAÇÃO
			letraCelulaSemId = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaSemId+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaSemId+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//TOTAL
			letraCelulaTotal = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaTotal+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaTotal+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
    		
	    }
        
	    dadosRelatorio = null;
        dadosFluxoVeicular = null;
        
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
