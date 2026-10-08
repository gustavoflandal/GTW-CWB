package com.consilux.servlet.relatorio.rj;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
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
 * Servlet para a geração de relatório de Fiscalização Eletrônica - Relatório 01 - Cada endereço mensal por data e horário - fluxo, velocidade e autos. 
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 26/09/2016
 */
public class Relatorio1FluxoVeicular extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(Relatorio1FluxoVeicular.class);
	
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
		
        Date data = new Date();  
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
        String userGerador = "Relatório gerado em: " + formatador.format(data) + ", pelo usuário: " + usuarioURL;
		
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
// 			ArrayList<LocalVigente> locais = new ArrayList<LocalVigente>();
// 			for (LocalVigente lv : locais1) {
//				locais.addAll(localVigente.buscaListaLocalVigenteFaixaRelFluxoRJ(lv.getIdLocal()));
//			}
            
 			if (locais.isEmpty()) {
				new Mensagem(response).showErro("Não existem dados para geração do relatório!", "javascript:window.close();");
				return;
			}
 			
 			
 			// Criando o arquivo fisico
			String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_estat.01-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
	        
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
	
	public void CriarPlanilha(Integer intMes, Integer intAno, HttpServletResponse response, SXSSFWorkbook wb, String userGerador, LocalVigente localRelat, ColecaoEstilos ce
			) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
		
		try {
			
	        //Cria uma planilha Excel
			String strNomePlanilha = null;
			strNomePlanilha = Funcoes.verificaNomeAba(wb, localRelat, Relatorio1FluxoVeicular.class.getSimpleName());
			
//			System.out.println("Id. Local: " + String.format("%02d", localRelat.getIdLocal()) + " / Nome planilha: " + strNomePlanilha);

			//Cria uma planilha Excel
		    Sheet sheet = wb.createSheet(strNomePlanilha);  
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
			
            String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
    		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");
    		String contrato = String.format("%03d", Integer.parseInt(numeroContrato)) + "/" + anoContrato;
    		
            //Nome relatório
            Row cabecalhoTituloRelatorio1 = sheet.createRow(0);
            cabecalhoTituloRelatorio1.getCell(0).setCellValue("FISCALIZAÇÃO ELETRÔNICA - RELATÓRIO 01");
            cabecalhoTituloRelatorio1.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
            
            Row cabecalhoTituloRelatorio2 = sheet.createRow(1);
            cabecalhoTituloRelatorio2.getCell(0).setCellValue("RELATÓRIO FLUXO DE VELOCIDADE E AUTOS");
            cabecalhoTituloRelatorio2.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));
            
            //Filtros informados
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
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 6));
            
            cabecalhoLogradouro.getCell(7).setCellValue("CÓDIGO:");
            cabecalhoLogradouro.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoLogradouro.getCell(8).setCellValue(localRelat.getSerieEquipamento());
            cabecalhoLogradouro.getCell(8).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(3, 3, 7, 8));
//            sheet.addMergedRegion(new CellRangeAddress(3, 3, 9, 10));
            
            Row cabecalhoReferenciaNumEquip = sheet.createRow(4);
//            cabecalhoReferenciaNumEquip.getCell(0).setCellValue("REFERÊNCIA:");
//            cabecalhoReferenciaNumEquip.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
//            cabecalhoReferenciaNumEquip.getCell(1).setCellValue(localRelat.getReferencia() == null ? "" : localRelat.getReferencia().trim());
//            cabecalhoReferenciaNumEquip.getCell(1).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 6));
            
            cabecalhoReferenciaNumEquip.getCell(7).setCellValue("EQUIPAMENTO:");
            cabecalhoReferenciaNumEquip.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoReferenciaNumEquip.getCell(8).setCellValue(localRelat.getCodigosEquipamentosDER() == null ? "" : localRelat.getCodigosEquipamentosDER().trim());
            cabecalhoReferenciaNumEquip.getCell(8).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(4, 4, 7, 8));
//            sheet.addMergedRegion(new CellRangeAddress(4, 4, 9, 10));
            
            Row cabecalhoPistaSentido = sheet.createRow(5);
//            cabecalhoPistaSentido.getCell(0).setCellValue("CÓD. EQUIPAMENTOS:");
//            cabecalhoPistaSentido.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
//            cabecalhoPistaSentido.getCell(1).setCellValue(localRelat.getCodigosEquipamentosDER() == null ? "" : localRelat.getCodigosEquipamentosDER().trim());
//            cabecalhoPistaSentido.getCell(1).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(5, 5, 1, 6));
            
			cabecalhoPistaSentido.getCell(0).setCellValue("TOTAL");
			cabecalhoPistaSentido.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            
            cabecalhoPistaSentido.getCell(7).setCellValue("COORDENADA:");
            cabecalhoPistaSentido.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoPistaSentido.getCell(8).setCellValue(Funcoes.converterDecimalParaDMS(localRelat.getLatitude(), localRelat.getLongitude()));
            cabecalhoPistaSentido.getCell(8).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(5, 5, 7, 8));
//            sheet.addMergedRegion(new CellRangeAddress(5, 5, 9, 10));
            
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
				   Sheet sheet, SXSSFWorkbook wb, String userGerador, LocalVigente localRelat, LocalVigente faixaRelat, ColecaoEstilos ce
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
//		    cabecalho1.getCell(8).setCellValue("");
//		    cabecalho1.getCell(8).setCellStyle(ce.estiloCabecalhoCorpoDireita);
		    cabecalho1.getCell(8).setCellValue(faixaRelat.getCodigoEquipamentoDER());
		    cabecalho1.getCell(8).setCellStyle(ce.estiloFiltro);
		    
//		    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), 7, 8));
		    
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
		SimpleDateFormat formatoMesAno = new SimpleDateFormat("MMM/yyyy");
		
		cabecalho2.getCell(0).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo1);
	    cabecalho3.getCell(0).setCellValue("Data");
	    cabecalho3.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoEsquerda);
	    cabecalho4.getCell(0).setCellValue("Dia da Semana");
	    cabecalho4.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoEsquerda);
	    cabecalho5.getCell(0).setCellValue("Horário (h)");
	    cabecalho5.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoEsquerda);
	    
	    sheet.setColumnWidth(0, 4600);
	    
	    Integer celula = 1, intUltimoDiaMes = 31;
	    String strData = null;
	    Date dtData = null;
	    
	    SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
	    Locale BRAZIL = new Locale("pt","BR");
	    SimpleDateFormat diaSemana = new SimpleDateFormat("EEEE",BRAZIL);
	    Calendar calendarioData = Calendar.getInstance();
	    
	    strData = String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01";
    	dtData = sdf.parse(strData);
    	calendarioData.setTime(dtData);
	    intUltimoDiaMes = calendarioData.getActualMaximum(Calendar.DATE);
	    
	    for (Integer dia = 1; dia <= intUltimoDiaMes; dia++) {
	    	
	    	strData = String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-" + String.format("%02d", dia);
	    	dtData = sdf.parse(strData);
	    	calendarioData.setTime(dtData);
	    	
	    	int cel_ini = celula, cel_fim;
	    	cabecalho2.getCell(celula).setCellValue("TODOS OS VEÍCULOS");
			cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho3.getCell(celula).setCellValue(dataFormat.format(dtData));
		    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho5.getCell(celula).setCellValue("FLUXO VEICULAR");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3800);
			celula++;
			cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			cabecalho3.getCell(celula).setCellValue(dataFormat.format(dtData));
		    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho5.getCell(celula).setCellValue("VEL. MÉDIA");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
			celula++;
			cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			cabecalho3.getCell(celula).setCellValue(dataFormat.format(dtData));
		    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho5.getCell(celula).setCellValue("VEL. MÁXIMA");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
		    cel_fim = celula;
			celula++;
			sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), cel_ini, cel_fim));
		    
			cel_ini = celula;
		    cabecalho2.getCell(celula).setCellValue("AUTOS DETECTADOS");
			cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho3.getCell(celula).setCellValue(dataFormat.format(dtData));
		    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho5.getCell(celula).setCellValue("AUTOS DETEC.");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
		    celula++;
		    
			cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			cabecalho3.getCell(celula).setCellValue(dataFormat.format(dtData));
		    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho5.getCell(celula).setCellValue("%");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
		    cel_fim = celula;
		    celula++;
		    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), cel_ini, cel_fim));
		    
		    cel_ini = celula;
		    cabecalho2.getCell(celula).setCellValue("AUTOS VÁLIDOS");
			cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			cabecalho3.getCell(celula).setCellValue(dataFormat.format(dtData));
		    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho5.getCell(celula).setCellValue("AUTOS VÁLIDOS");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
		    celula++;
		    
			cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			cabecalho3.getCell(celula).setCellValue(dataFormat.format(dtData));
		    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho5.getCell(celula).setCellValue("%");
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, 3500);
		    cel_fim = celula;
		    celula++;
		    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), cel_ini, cel_fim));

	    }
	    
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("FLUXO VEICULAR");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
		celula++;
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("VEL. MÉDIA");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
		celula++;
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("VEL. MÁXIMA");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
		celula++;
	    
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("AUTOS DETEC.");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
	    celula++;
	    
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("%");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("AUTOS VÁLIDOS");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
	    celula++;
	    
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo2);
		cabecalho3.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("%");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
	    celula++;

	    
	    
        Integer intIdPista = faixaRelat != null ? faixaRelat.getIdPista() : null;
        		
        //Buscando informações para popular planilhas
	    DadosFluxoVeicular dadosFluxoVeicular = new DadosFluxoVeicular();
	    ArrayList<ItemFluxoVeicular> dadosRelatorio = dadosFluxoVeicular.relatorio1FluxoVeicular(intMes, intAno, localRelat.getIdLocal(), intIdPista);
		
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

				for(int h = 0; h < intUltimoDiaMes; h++) {

					Integer valorFluxo = dadosRelatorio.get(i).getCelulasFluxo()[h];
					Integer valorVelMedia = dadosRelatorio.get(i).getCelulasVelMedia()[h];
					Integer valorVelMax = dadosRelatorio.get(i).getCelulasVelMax()[h];
					Integer valorAutosDetectados = dadosRelatorio.get(i).getCelulasAutosDetectados()[h];
					Integer valorAutosValidos = dadosRelatorio.get(i).getCelulasAutosValidos()[h];
					
					Double porcAutosDetectados = 0.0;
					Double porcAutosValidos = 0.0;
            		
            		if (valorFluxo != null && valorFluxo > 0) {
            			if (valorAutosDetectados != null && valorAutosDetectados > 0) {
            				porcAutosDetectados = valorAutosDetectados.doubleValue() / valorFluxo.doubleValue();
            			}
            			if (valorAutosValidos != null && valorAutosValidos > 0) {
            				porcAutosValidos = valorAutosValidos.doubleValue() / valorFluxo.doubleValue();
            			}
            		}

            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            		if (valorFluxo != null && valorFluxo > 0) {
        				linhaSheet.getCell(celula).setCellValue(valorFluxo);
            		} 
            		celula++;

        		   	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            		if (valorVelMedia != null && valorVelMedia > 0) {
        		   		linhaSheet.getCell(celula).setCellValue(valorVelMedia);
        			} 
             		celula++;

            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            		if (valorVelMax != null && valorVelMax > 0) {
 	        		   	linhaSheet.getCell(celula).setCellValue(valorVelMax);
            		} 
            		celula++;

            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            		if (valorAutosDetectados != null && valorAutosDetectados > 0) {
 	        		   	linhaSheet.getCell(celula).setCellValue(valorAutosDetectados);
            		} 
            		celula++;

            		linhaSheet.getCell(celula).setCellStyle(ce.estiloPorcentagem);
            		if (porcAutosDetectados != null && porcAutosDetectados > 0.0) {
 	        		   	linhaSheet.getCell(celula).setCellValue(porcAutosDetectados);
            		} 
            		celula++;

            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            		if (valorAutosValidos != null && valorAutosValidos > 0) {
 	        		   	linhaSheet.getCell(celula).setCellValue(valorAutosValidos);
            		} 
            		celula++;

            		linhaSheet.getCell(celula).setCellStyle(ce.estiloPorcentagem);
            		if (porcAutosValidos != null && porcAutosValidos > 0) {
 	        		   	linhaSheet.getCell(celula).setCellValue(porcAutosValidos);
            		} 
            		celula++;
            		
            	}
				

            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
				if (dadosRelatorio.get(i).getFluxoVeicular() != null && dadosRelatorio.get(i).getFluxoVeicular() > 0) {
					linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicular());
				} 
            	celula++;

            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	if (dadosRelatorio.get(i).getVelocidadeMedia() != null && dadosRelatorio.get(i).getVelocidadeMedia() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getVelocidadeMedia());
            	}
            	celula++;

            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	if (dadosRelatorio.get(i).getVelocidadeMaxima() != null && dadosRelatorio.get(i).getVelocidadeMaxima() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getVelocidadeMaxima());
            	} 
            	celula++;

            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	if (dadosRelatorio.get(i).getAutosDetectados() != null && dadosRelatorio.get(i).getAutosDetectados() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectados());
            	} 
            	celula++;

            	linhaSheet.getCell(celula).setCellStyle(ce.estiloPorcentagem);
            	if (dadosRelatorio.get(i).getFluxoVeicular() > 0 && dadosRelatorio.get(i).getAutosDetectados() > 0) {
	            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectados().doubleValue() / dadosRelatorio.get(i).getFluxoVeicular().doubleValue());
            	} 
            	celula++;

            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	if (dadosRelatorio.get(i).getAutosValidos() != null && dadosRelatorio.get(i).getAutosValidos() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidos());
            	} 
            	celula++;

            	linhaSheet.getCell(celula).setCellStyle(ce.estiloPorcentagem);
            	if (dadosRelatorio.get(i).getFluxoVeicular() > 0 && dadosRelatorio.get(i).getAutosValidos() > 0) {
	            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidos().doubleValue() / dadosRelatorio.get(i).getFluxoVeicular().doubleValue());
            	} 
            	celula++;
        	}
        	
        	int intLinhaResumo = 0, intLinhaInicioSomaResumo = (intLinhaInicioDados + 1);
        	
    		intLinhaResumo = dadosRelatorio.size() + (intLinha);
    		intLinhaRetorno = intLinhaResumo + 1;
    		
    		Row linhaResumo = sheet.createRow((short)(intLinhaResumo));
    		linhaResumo.getCell(0).setCellValue("RESUMO");
    		linhaResumo.getCell(0).setCellStyle(ce.estiloItensCentralizadoCinza);
    		
    		celula = 1;
    		
    		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
    		
    		String letraCelulaFluxo = null, letraCelulaVelMedia = null, letraCelulaVelMaxima = null, 
    				letraCelulaAutosDetectados = null, letraCelulaAutosValidos = null;
    		
    		CellReference cellRefAutosDetectados = null, cellRefAutosValidos = null;
    		Cell cellAutosDetectados = null, cellAutosValidos = null;
    		CellValue valorAutosDetectados = null, valorAutosValidos = null;
    		
    		for(int h = 0; h < intUltimoDiaMes; h++) {
    			//FLUXO VEICULAR
    			letraCelulaFluxo = CellReference.convertNumToColString(celula);
    			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaFluxo+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaFluxo+String.valueOf(linhaResumo.getRowNum())+")");
    			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
    			celula++;
    			
    			//VEL. MÉDIA
    			letraCelulaVelMedia = CellReference.convertNumToColString(celula);
    			linhaResumo.getCell(celula).setCellFormula("AVERAGE("+letraCelulaVelMedia+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaVelMedia+String.valueOf(linhaResumo.getRowNum())+")");
    			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
    			celula++;
    			
    			//VEL. MÁXIMA
    			letraCelulaVelMaxima = CellReference.convertNumToColString(celula);
    			linhaResumo.getCell(celula).setCellFormula("MAX("+letraCelulaVelMaxima+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaVelMaxima+String.valueOf(linhaResumo.getRowNum())+")");
    			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
    			celula++;
    			
    			//AUTOS DETECTADOS
    			letraCelulaAutosDetectados = CellReference.convertNumToColString(celula);
    			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosDetectados+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosDetectados+String.valueOf(linhaResumo.getRowNum())+")");
    			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
    			celula++;
    			
    			cellRefAutosDetectados = new CellReference(letraCelulaAutosDetectados+String.valueOf(linhaResumo.getRowNum() + 1));
    			cellAutosDetectados = linhaResumo.getCell(cellRefAutosDetectados.getCol());
    			valorAutosDetectados = evaluator.evaluate(cellAutosDetectados);
    			
    			//% AUTOS DETECTADOS
    			if (valorAutosDetectados.getNumberValue() > 0.0) {
    				linhaResumo.getCell(celula).setCellFormula(letraCelulaAutosDetectados+String.valueOf(linhaResumo.getRowNum() + 1)+"/"+letraCelulaFluxo+String.valueOf(linhaResumo.getRowNum() + 1));
    				linhaResumo.getCell(celula).setCellStyle(ce.estiloPorcentagemCinza);
    			} else {
    				linhaResumo.getCell(celula).setCellStyle(ce.estiloPorcentagemCinza);
    			}
    			celula++;
    			
    			//AUTOS VALIDOS
    			letraCelulaAutosValidos = CellReference.convertNumToColString(celula);
    			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosValidos+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum())+")");
    			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
    			celula++;
    			
    			cellRefAutosValidos = new CellReference(letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum() + 1));
    			cellAutosValidos = linhaResumo.getCell(cellRefAutosValidos.getCol());
    			valorAutosValidos = evaluator.evaluate(cellAutosValidos);
    			
    			//% AUTOS VALIDOS
    			if (valorAutosValidos.getNumberValue() > 0.0) {
    				linhaResumo.getCell(celula).setCellFormula(letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum() + 1)+"/"+letraCelulaFluxo+String.valueOf(linhaResumo.getRowNum() + 1));
    				linhaResumo.getCell(celula).setCellStyle(ce.estiloPorcentagemCinza);
    			} else {
    				linhaResumo.getCell(celula).setCellStyle(ce.estiloPorcentagemCinza);
    			}
    			celula++;
    		}
    		
    		//TOTAL MÊS
   			//FLUXO VEICULAR
			letraCelulaFluxo = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaFluxo+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaFluxo+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//FLUXO VEL. MEDIA
			letraCelulaVelMedia = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("AVERAGE("+letraCelulaVelMedia+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaVelMedia+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//FLUXO VEL. MAXIMA
			letraCelulaVelMaxima = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("MAX("+letraCelulaVelMaxima+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaVelMaxima+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//AUTOS DETECTADOS
			letraCelulaAutosDetectados = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosDetectados+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosDetectados+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			cellRefAutosDetectados = new CellReference(letraCelulaAutosDetectados+String.valueOf(linhaResumo.getRowNum() + 1));
			cellAutosDetectados = linhaResumo.getCell(cellRefAutosDetectados.getCol());
			valorAutosDetectados = evaluator.evaluate(cellAutosDetectados);
			
			//% AUTOS DETECTADOS
			if (valorAutosDetectados.getNumberValue() > 0.0) {
				linhaResumo.getCell(celula).setCellFormula(letraCelulaAutosDetectados+String.valueOf(linhaResumo.getRowNum() + 1)+"/"+letraCelulaFluxo+String.valueOf(linhaResumo.getRowNum() + 1));
				linhaResumo.getCell(celula).setCellStyle(ce.estiloPorcentagemCinza);
			} else {
				linhaResumo.getCell(celula).setCellStyle(ce.estiloPorcentagemCinza);
			}
			celula++;
			
			//AUTOS VALIDOS
			letraCelulaAutosValidos = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosValidos+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			cellRefAutosValidos = new CellReference(letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum() + 1));
			cellAutosValidos = linhaResumo.getCell(cellRefAutosValidos.getCol());
			valorAutosValidos = evaluator.evaluate(cellAutosValidos);
			
			//% AUTOS VALIDOS
			if (valorAutosValidos.getNumberValue() > 0.0) {
				linhaResumo.getCell(celula).setCellFormula(letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum() + 1)+"/"+letraCelulaFluxo+String.valueOf(linhaResumo.getRowNum() + 1));
				linhaResumo.getCell(celula).setCellStyle(ce.estiloPorcentagemCinza);
			} else {
				linhaResumo.getCell(celula).setCellStyle(ce.estiloPorcentagemCinza);
			}
			celula++;
        }
        
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
