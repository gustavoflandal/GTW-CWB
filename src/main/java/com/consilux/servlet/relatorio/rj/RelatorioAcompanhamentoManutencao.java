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

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
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
import com.consilux.model.relatorio.rj.DadosRelOcorrenciaManut;
import com.consilux.model.relatorio.rj.ItemRelOcorrenciaManut;

public class RelatorioAcompanhamentoManutencao extends HttpServlet {

	private static final long serialVersionUID = 8372726369600699060L;
	private static final Logger logger = Logger.getLogger(RelatorioAcompanhamentoManutencao.class);

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
		String strIdLocal = "";
		
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
			String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_RelatorioAcompanhamento-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
	        
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
			
			SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy");
			Date dataAtual = new Date();
			
			String strNomePlanilha = null;
			strNomePlanilha = localRelat.getSerieEquipamento().toString(); //Funcoes.verificaNomeAba(wb, localRelat, Relatorio1FluxoVeicular.class.getSimpleName());
			
			//Cria uma planilha Excel
		    Sheet sheet = wb.createSheet(strNomePlanilha);  
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
			
            String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
    		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");
    		String contrato = String.format("%03d", Integer.parseInt(numeroContrato)) + "/" + anoContrato;
    		
            //Nome relatório
            Row cabecalhoTituloRelatorio1 = sheet.createRow(0);
            cabecalhoTituloRelatorio1.getCell(0).setCellValue("FISCALIZAÇÃO ELETRÔNICA - RELATÓRIO DE ACOMPANHAMENTO");
            cabecalhoTituloRelatorio1.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
            
            Row cabecalhoTituloRelatorio2 = sheet.createRow(1);
            cabecalhoTituloRelatorio2.getCell(0).setCellValue("RELATÓRIO DE ACOMPANHAMENTO DE OCORRÊNCIAS");
            cabecalhoTituloRelatorio2.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));
            
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
            
            Row cabecalhoReferenciaNumEquip = sheet.createRow(4);
            cabecalhoReferenciaNumEquip.getCell(7).setCellValue("EQUIPAMENTO:");
            cabecalhoReferenciaNumEquip.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoReferenciaNumEquip.getCell(8).setCellValue(localRelat.getCodigosEquipamentosDER() == null ? "" : localRelat.getCodigosEquipamentosDER().trim());
            cabecalhoReferenciaNumEquip.getCell(8).setCellStyle(ce.estiloFiltro);
            
            Row cabecalhoPistaSentido = sheet.createRow(5);
			cabecalhoPistaSentido.getCell(0).setCellValue("ATUALIZAÇÃO:");
			cabecalhoPistaSentido.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
			cabecalhoPistaSentido.getCell(1).setCellValue(formatarData.format(dataAtual));
			cabecalhoPistaSentido.getCell(1).setCellStyle(ce.estiloFiltro);

			cabecalhoPistaSentido.getCell(7).setCellValue("COORDENADA:");
            cabecalhoPistaSentido.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoPistaSentido.getCell(8).setCellValue(Funcoes.converterDecimalParaDMS(localRelat.getLatitude(), localRelat.getLongitude()));
            cabecalhoPistaSentido.getCell(8).setCellStyle(ce.estiloFiltro);
            
            int intLinha = 7;
            
            intLinha = GerarRelatorio(intMes, intAno, intLinha, response, sheet, wb, userGerador, localRelat, null, ce);
            
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

		Row cabecalho1 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho2 = sheet.createRow((short)intLinha);
		intLinha++;
	    
	    Integer celula = 0, celulaInicio = celula;

	    celulaInicio = celula;
    	cabecalho1.getCell(celula).setCellValue("OCORRÊNCIAS");
		cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 4000);
		celula++;
		cabecalho1.getCell(celula).setCellValue("Descrição");
		cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3000);
	    celula++;
		cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 14000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), celulaInicio, celula));
		celula++;
		
		celulaInicio = celula;
		cabecalho1.getCell(celula).setCellValue("INÍCIO");
		cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho2.getCell(celula).setCellValue("DATA");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3000);
		celula++;
		cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho2.getCell(celula).setCellValue("HORA");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), celulaInicio, celula));
		celula++;
	    
		
		celulaInicio = celula;
	    cabecalho1.getCell(celula).setCellValue("TÉRMINO");
		cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho2.getCell(celula).setCellValue("DATA");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3000);
	    celula++;
		cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho2.getCell(celula).setCellValue("HORA");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), celulaInicio, celula));
	    celula++;
	    
	    celulaInicio = celula;
	    cabecalho1.getCell(celula).setCellValue("Descrição detalhada da ocorrência e das soluções adotadas");
		cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoSemBordaInferior);
		cabecalho2.getCell(celula).setCellValue("Quando em aberto, informar previsão de reparo");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoSemBordaSuperior);
	    sheet.setColumnWidth(celula, 3500);
	    celula++;
		cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoSemBordaInferior);
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoSemBordaSuperior);
	    sheet.setColumnWidth(celula, 14500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), celulaInicio, celula));
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), celulaInicio, celula));
	    celula++;
	    
	    
	    SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
	    SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
	    
        //Buscando informações para popular planilhas
	    DadosRelOcorrenciaManut dadosRelOcorrenciaManut = new DadosRelOcorrenciaManut();
	    ArrayList<ItemRelOcorrenciaManut> dadosRelatorio = dadosRelOcorrenciaManut.relatorioAcompanhamentoOcorrenciaManut(intMes, intAno, localRelat.getIdLocal());
		
	    int intLinhaInicioDados = intLinha, intLinhaRetorno = intLinhaInicioDados;
	    
	    if (dadosRelatorio.size() == 0) {
        	Row linhaSemInfo = sheet.createRow((short)intLinha);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        } else {
        	for (int i = 0; i < dadosRelatorio.size(); i++) {
            	
        		celula = 0;
        		String[] linhas = null;
        		Integer qtdeLinhas = 1;
        		
        		if (dadosRelatorio.get(i).getDescricaoDetalhada() != null) {
        			linhas = dadosRelatorio.get(i).getDescricaoDetalhada().split("\r\n|\r|\n");
        			qtdeLinhas = linhas.length;
        		}
        		
            	Row linhaSheet = sheet.createRow((short)(i+intLinha));
            	celulaInicio = celula;
            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getOcorrencia());
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensEsquerdaQuebraLinha);
            	celula++;
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensEsquerdaQuebraLinha);
            	celula++;
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensEsquerdaQuebraLinha);
            	sheet.addMergedRegion(new CellRangeAddress(linhaSheet.getRowNum(), linhaSheet.getRowNum(), celulaInicio, celula));
            	celula++;
            	linhaSheet.getCell(celula).setCellValue(dataFormat.format(dadosRelatorio.get(i).getDataInicio()));
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
            	celula++;
            	linhaSheet.getCell(celula).setCellValue(horaFormat.format(dadosRelatorio.get(i).getDataInicio()));
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
            	celula++;
            	linhaSheet.getCell(celula).setCellValue(dataFormat.format(dadosRelatorio.get(i).getDataTermino()));
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
            	celula++;
            	linhaSheet.getCell(celula).setCellValue(horaFormat.format(dadosRelatorio.get(i).getDataTermino()));
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
            	celula++;
            	celulaInicio = celula;
            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getDescricaoDetalhada());
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensEsquerdaQuebraLinha);
            	celula++;
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensEsquerdaQuebraLinha);
            	linhaSheet.setHeightInPoints(qtdeLinhas*sheet.getDefaultRowHeightInPoints());
            	sheet.addMergedRegion(new CellRangeAddress(linhaSheet.getRowNum(), linhaSheet.getRowNum(), celulaInicio, celula));
            	celula++;
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
