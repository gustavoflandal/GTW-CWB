package com.consilux.servlet.relatorio.rj;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
 * Servlet para a geração de relatório de Fiscalização Eletrônica - Relatório 02 - Cada endereço mensal por data - fluxo, velocidade e autos. 
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 26/09/2016
 */
public class Relatorio2FluxoVeicular extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(Relatorio2FluxoVeicular.class);

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
	        String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_estat.02-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
	        
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
			strNomePlanilha = Funcoes.verificaNomeAba(wb, localRelat, Relatorio2FluxoVeicular.class.getSimpleName());
			
//			System.out.println("Id. Local: " + String.format("%02d", localRelat.getIdLocal()) + " / Nome planilha: " + strNomePlanilha);

		    Sheet sheet = wb.createSheet(strNomePlanilha);  
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
			
            String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
    		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");
    		String contrato = String.format("%03d", Integer.parseInt(numeroContrato)) + "/" + anoContrato;

    		//Nome relatório
            Row cabecalhoTituloRelatorio1 = sheet.createRow(0);
            cabecalhoTituloRelatorio1.getCell(0).setCellValue("FISCALIZAÇÃO ELETRÔNICA - RELATÓRIO 02");
            cabecalhoTituloRelatorio1.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
            
            Row cabecalhoTituloRelatorio2 = sheet.createRow(1);
            cabecalhoTituloRelatorio2.getCell(0).setCellValue("RELATÓRIO FLUXO DE VELOCIDADE E AUTOS");
            cabecalhoTituloRelatorio2.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));
            
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
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 4));
            
            cabecalhoLogradouro.getCell(7).setCellValue("CÓDIGO:");
            cabecalhoLogradouro.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoLogradouro.getCell(8).setCellValue(localRelat.getSerieEquipamento());
            cabecalhoLogradouro.getCell(8).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(3, 3, 5, 6));
            
            Row cabecalhoReferenciaNumEquip = sheet.createRow(4);
//            cabecalhoReferenciaNumEquip.getCell(0).setCellValue("REFERÊNCIA:");
//            cabecalhoReferenciaNumEquip.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
//            cabecalhoReferenciaNumEquip.getCell(1).setCellValue(localRelat.getReferencia() == null ? "" : localRelat.getReferencia().trim());
//            cabecalhoReferenciaNumEquip.getCell(1).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 4));
            
            cabecalhoReferenciaNumEquip.getCell(7).setCellValue("EQUIPAMENTO:");
            cabecalhoReferenciaNumEquip.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoReferenciaNumEquip.getCell(8).setCellValue(localRelat.getCodigosEquipamentosDER() == null ? "" : localRelat.getCodigosEquipamentosDER().trim());
            cabecalhoReferenciaNumEquip.getCell(8).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(4, 4, 5, 6));
            
            Row cabecalhoPistaSentido = sheet.createRow(5);
//            cabecalhoPistaSentido.getCell(0).setCellValue("CÓD. EQUIPAMENTOS:");
//            cabecalhoPistaSentido.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
//            cabecalhoPistaSentido.getCell(1).setCellValue(localRelat.getCodigosEquipamentosDER() == null ? "" : localRelat.getCodigosEquipamentosDER().trim());
//            cabecalhoPistaSentido.getCell(1).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(5, 5, 1, 4));
            
			cabecalhoPistaSentido.getCell(0).setCellValue("TOTAL");
			cabecalhoPistaSentido.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            
            cabecalhoPistaSentido.getCell(7).setCellValue("COORDENADA:");
            cabecalhoPistaSentido.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoPistaSentido.getCell(8).setCellValue(Funcoes.converterDecimalParaDMS(localRelat.getLatitude(), localRelat.getLongitude()));
            cabecalhoPistaSentido.getCell(8).setCellStyle(ce.estiloFiltro);
//            sheet.addMergedRegion(new CellRangeAddress(5, 5, 5, 6));
			
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
//		    cabecalho1.getCell(6).setCellValue("");
//		    cabecalho1.getCell(6).setCellStyle(ce.estiloCabecalhoCorpoDireita);
		    cabecalho1.getCell(8).setCellValue(faixaRelat.getCodigoEquipamentoDER());
		    cabecalho1.getCell(8).setCellStyle(ce.estiloFiltro);
		    
//		    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), 5, 6));
		    
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
		
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatoMesAno = new SimpleDateFormat("MMM/yyyy");
		
		cabecalho2.getCell(0).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(0, 4600);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho3.getRowNum(), 0, 0));
	    
		cabecalho2.getCell(1).setCellValue("DIA DA SEMANA");
	    cabecalho2.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho3.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(1, 4500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho3.getRowNum(), 1, 1));
	    
	    cabecalho2.getCell(2).setCellValue("TODOS OS VEÍCULOS");
	    cabecalho2.getCell(2).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho3.getCell(2).setCellValue("FLUXO VEICULAR");
	    cabecalho3.getCell(2).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(2, 4500);
	    
	    cabecalho2.getCell(3).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(3).setCellValue("VEL. MÉDIA");
	    cabecalho3.getCell(3).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(3, 4500);
	    
	    cabecalho2.getCell(4).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(4).setCellValue("VEL. MÁXIMA");
	    cabecalho3.getCell(4).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(4, 4500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), 2, 4));

	    cabecalho2.getCell(5).setCellValue("AUTOS DETECTADOS");
	    cabecalho2.getCell(5).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho3.getCell(5).setCellValue("AUTOS DETECTADOS");
	    cabecalho3.getCell(5).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(5, 5000);
	    
	    cabecalho2.getCell(6).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(6).setCellValue("%");
	    cabecalho3.getCell(6).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(6, 2500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), 5, 6));
	    
	    cabecalho2.getCell(7).setCellValue("AUTOS VÁLIDOS");
	    cabecalho2.getCell(7).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho3.getCell(7).setCellValue("AUTOS VÁLIDOS");
	    cabecalho3.getCell(7).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(7, 5000);
	    
	    cabecalho2.getCell(8).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(8).setCellValue("%");
	    cabecalho3.getCell(8).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(8, 2500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), 7, 8));
	    
	    cabecalho2.getCell(9).setCellValue("TAXA DE APROVEITAMENTO");
	    cabecalho2.getCell(9).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho3.getCell(9).setCellValue("%");
	    cabecalho3.getCell(9).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(9, 5000);
	    
	    
	    Integer intIdPista = faixaRelat != null ? faixaRelat.getIdPista() : null;
	    
	    //Buscando informações para popular planilhas
		DadosFluxoVeicular dadosFluxoVeicular = new DadosFluxoVeicular();
		ArrayList<ItemFluxoVeicular> dadosRelatorio = dadosFluxoVeicular.relatorio2FluxoVeicular(intMes, intAno, localRelat.getIdLocal(), intIdPista);
		
		int intLinhaInicioDados = intLinha, intLinhaRetorno = intLinhaInicioDados;
		
		DateFormat data = new SimpleDateFormat ("dd/MM/yyyy");
		DateFormat diaSemana = new SimpleDateFormat ("EEEE");
		
		Integer totalAutosInvalidosNaoTecnicos = 0;
		
        if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)intLinha);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        }else{
            for(int i=0; i<dadosRelatorio.size(); i++){
            	
            	Integer valorFluxo;
				Integer valorAutosDetectados;
				Integer valorAutosValidos;
				Integer valorAutosInvalidosMotivoNaoTecnico;
            	
            	Double porcAutosDetectados = 0.0, porcAutosValidos = 0.0, porcTaxaAproveitamento = 0.0;
            	
            	valorFluxo = dadosRelatorio.get(i).getFluxoVeicular();
            	valorAutosDetectados = dadosRelatorio.get(i).getAutosDetectados();
            	valorAutosValidos = dadosRelatorio.get(i).getAutosValidos();
            	valorAutosInvalidosMotivoNaoTecnico = dadosRelatorio.get(i).getAutosInvalidosMotivoNaoTecnico();
            	
            	
            	if (valorFluxo > 0) {
            		porcAutosDetectados = valorAutosDetectados.doubleValue() / valorFluxo.doubleValue();
            	}
            	if (valorAutosDetectados > 0) {
                	porcAutosValidos = valorAutosValidos.doubleValue() / valorAutosDetectados.doubleValue();            		
            	}
            	
            	if (valorAutosDetectados > 0 && valorAutosValidos > 0) {
            		porcTaxaAproveitamento = valorAutosValidos.doubleValue() / (valorAutosDetectados.doubleValue() - valorAutosInvalidosMotivoNaoTecnico.doubleValue());
            	}
            	
               	Row linhaSheet = sheet.createRow((short)(i+intLinha));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getData());
            	linhaSheet.getCell(0).setCellStyle(ce.estiloItensCentralizado);
//            	linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getDiaSemanaDesc());
            	linhaSheet.getCell(1).setCellValue(diaSemana.format(data.parse(dadosRelatorio.get(i).getData())));
            	linhaSheet.getCell(1).setCellStyle(ce.estiloItensCentralizado);
            	
            	if (valorFluxo > 0) {
            		linhaSheet.getCell(2).setCellValue(valorFluxo);
            	} 
            	linhaSheet.getCell(2).setCellStyle(ce.estiloItensNumCentralizado);
            	
            	if (dadosRelatorio.get(i).getVelocidadeMedia() > 0) {
            		linhaSheet.getCell(3).setCellValue(dadosRelatorio.get(i).getVelocidadeMedia());
            	} 
            	linhaSheet.getCell(3).setCellStyle(ce.estiloItensNumCentralizado);
            	
            	if (dadosRelatorio.get(i).getVelocidadeMaxima() > 0) {
            		linhaSheet.getCell(4).setCellValue(dadosRelatorio.get(i).getVelocidadeMaxima());
            	} 
            	linhaSheet.getCell(4).setCellStyle(ce.estiloItensNumCentralizado);
            	
            	if (valorAutosDetectados > 0) {
            		linhaSheet.getCell(5).setCellValue(valorAutosDetectados);
            	} 
            	linhaSheet.getCell(5).setCellStyle(ce.estiloItensNumCentralizado);
            	
            	if (porcAutosDetectados > 0.0) {
            		linhaSheet.getCell(6).setCellValue(porcAutosDetectados);
            	} 
            	linhaSheet.getCell(6).setCellStyle(ce.estiloPorcentagem);
            	
            	if (valorAutosValidos > 0) {
            		linhaSheet.getCell(7).setCellValue(valorAutosValidos);
            	} 
            	linhaSheet.getCell(7).setCellStyle(ce.estiloItensNumCentralizado);
            	
            	if (porcAutosValidos > 0.0) {
            		linhaSheet.getCell(8).setCellValue(porcAutosValidos);
            	} 
            	linhaSheet.getCell(8).setCellStyle(ce.estiloPorcentagem);
            	
            	if (porcTaxaAproveitamento > 0.0) {
            		linhaSheet.getCell(9).setCellValue(porcTaxaAproveitamento);
            	} 
            	linhaSheet.getCell(9).setCellStyle(ce.estiloPorcentagem);
            	
            	totalAutosInvalidosNaoTecnicos += valorAutosInvalidosMotivoNaoTecnico;
            }
        }
        
        int intLinhaResumo = 0, intLinhaInicioSomaResumo = (intLinhaInicioDados + 1);
    	
    	if (dadosRelatorio.size() > 0) {
    		
    		intLinhaResumo = dadosRelatorio.size() + (intLinha);
    		intLinhaRetorno = intLinhaResumo + 1;
    		
    		Row linhaResumo = sheet.createRow((short)(intLinhaResumo));
    		linhaResumo.getCell(0).setCellValue("RESUMO");
    		linhaResumo.getCell(0).setCellStyle(ce.estiloItensCentralizadoCinza);
    		linhaResumo.getCell(1).setCellValue("");
    		linhaResumo.getCell(1).setCellStyle(ce.estiloItensCentralizadoCinza);
    		
    		sheet.addMergedRegion(new CellRangeAddress(linhaResumo.getRowNum(), linhaResumo.getRowNum(), 0, 1));
    		
    		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
    		
    		String letraCelulaFluxo = null, letraCelulaVelMedia = null, letraCelulaVelMaxima = null, 
    				letraCelulaAutosDetectados = null, letraCelulaAutosValidos = null;
    		
    		CellReference cellRefAutosDetectados = null, cellRefAutosValidos = null;
    		Cell cellAutosDetectados = null, cellAutosValidos = null;
    		CellValue valorAutosDetectados = null, valorAutosValidos = null;
    		
			//FLUXO VEICULAR
			letraCelulaFluxo = CellReference.convertNumToColString(2);
			linhaResumo.getCell(2).setCellFormula("SUM("+letraCelulaFluxo+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaFluxo+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(2).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			
			//VEL. MÉDIA
			letraCelulaVelMedia = CellReference.convertNumToColString(3);
			linhaResumo.getCell(3).setCellFormula("AVERAGE("+letraCelulaVelMedia+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaVelMedia+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(3).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			
			//VEL. MÁXIMA
			letraCelulaVelMaxima = CellReference.convertNumToColString(4);
			linhaResumo.getCell(4).setCellFormula("MAX("+letraCelulaVelMaxima+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaVelMaxima+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(4).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			
			//AUTOS DETECTADOS
			letraCelulaAutosDetectados = CellReference.convertNumToColString(5);
			linhaResumo.getCell(5).setCellFormula("SUM("+letraCelulaAutosDetectados+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosDetectados+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(5).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			
			cellRefAutosDetectados = new CellReference(letraCelulaAutosDetectados+String.valueOf(linhaResumo.getRowNum() + 1));
			cellAutosDetectados = linhaResumo.getCell(cellRefAutosDetectados.getCol());
			valorAutosDetectados = evaluator.evaluate(cellAutosDetectados);
			
			//% AUTOS DETECTADOS
			if (valorAutosDetectados.getNumberValue() > 0.0) {
				linhaResumo.getCell(6).setCellFormula(letraCelulaAutosDetectados+String.valueOf(linhaResumo.getRowNum() + 1)+"/"+letraCelulaFluxo+String.valueOf(linhaResumo.getRowNum() + 1));
				linhaResumo.getCell(6).setCellStyle(ce.estiloPorcentagemCinza);
			} else {
				linhaResumo.getCell(6).setCellStyle(ce.estiloPorcentagemCinza);
			}
			
			//AUTOS VALIDOS
			letraCelulaAutosValidos = CellReference.convertNumToColString(7);
			linhaResumo.getCell(7).setCellFormula("SUM("+letraCelulaAutosValidos+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(7).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			
			cellRefAutosValidos = new CellReference(letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum() + 1));
			cellAutosValidos = linhaResumo.getCell(cellRefAutosValidos.getCol());
			valorAutosValidos = evaluator.evaluate(cellAutosValidos);
			
			//% AUTOS VALIDOS
			if (valorAutosValidos.getNumberValue() > 0.0) {
				linhaResumo.getCell(8).setCellFormula(letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum() + 1)+"/"+letraCelulaFluxo+String.valueOf(linhaResumo.getRowNum() + 1));
				linhaResumo.getCell(8).setCellStyle(ce.estiloPorcentagemCinza);
			} else {
				linhaResumo.getCell(8).setCellStyle(ce.estiloPorcentagemCinza);
			}
			
			//TAXA DE APROVEITAMENTO			
			if (valorAutosDetectados.getNumberValue() > 0.0 && valorAutosValidos.getNumberValue() > 0.0) {
				linhaResumo.getCell(9).setCellFormula(letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum() + 1)+"/("+letraCelulaAutosDetectados+String.valueOf(linhaResumo.getRowNum() + 1)+"-" + totalAutosInvalidosNaoTecnicos.toString() +")");
				linhaResumo.getCell(9).setCellStyle(ce.estiloPorcentagemCinza);
			} else {
				linhaResumo.getCell(9).setCellStyle(ce.estiloPorcentagemCinza);
			}
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
