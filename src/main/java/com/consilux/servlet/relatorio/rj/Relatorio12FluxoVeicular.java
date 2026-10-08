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
 * Servlet para a geração de relatório de Fiscalização Eletrônica - Relatório 12 - Cada endereço mensal por data - registros detectados e válidos por enquadramento.
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 09/05/2018
 */
public class Relatorio12FluxoVeicular extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(Relatorio12FluxoVeicular.class);

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
			String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_estat.12-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
	        
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
			strNomePlanilha = Funcoes.verificaNomeAba(wb, localRelat, Relatorio12FluxoVeicular.class.getSimpleName());
			
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
  			cabecalhoTituloRelatorio1.getCell(0).setCellValue("FISCALIZAÇÃO ELETRÔNICA - RELATÓRIO 12");
  			cabecalhoTituloRelatorio1.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
  			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));
          
  			Row cabecalhoTituloRelatorio2 = sheet.createRow(1);
  			cabecalhoTituloRelatorio2.getCell(0).setCellValue("INFORMAÇÕES DIÁRIAS POR ENDEREÇO PARA CADA MÊS - REGISTROS DETECTADOS E VÁLIDOS POR ENQUADRAMENTO");
  			cabecalhoTituloRelatorio2.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
  			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 15));
          
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
//  			sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 8));
          
  			cabecalhoLogradouro.getCell(7).setCellValue("CÓDIGO:");
  			cabecalhoLogradouro.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
  			cabecalhoLogradouro.getCell(8).setCellValue(localRelat.getSerieEquipamento());
  			cabecalhoLogradouro.getCell(8).setCellStyle(ce.estiloFiltro);
//  			sheet.addMergedRegion(new CellRangeAddress(3, 3, 9, 12));
//  			sheet.addMergedRegion(new CellRangeAddress(3, 3, 13, 15));
  			
  			Row cabecalhoReferenciaNumEquip = sheet.createRow(4);
//  			cabecalhoReferenciaNumEquip.getCell(0).setCellValue("REFERÊNCIA:");
//  			cabecalhoReferenciaNumEquip.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
//  			cabecalhoReferenciaNumEquip.getCell(1).setCellValue(localRelat.getReferencia() == null ? "" : localRelat.getReferencia().trim());
//  			cabecalhoReferenciaNumEquip.getCell(1).setCellStyle(ce.estiloFiltro);
//  			sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 8));
          
  			cabecalhoReferenciaNumEquip.getCell(7).setCellValue("EQUIPAMENTO:");
  			cabecalhoReferenciaNumEquip.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
  			cabecalhoReferenciaNumEquip.getCell(8).setCellValue(localRelat.getCodigosEquipamentosDER() == null ? "" : localRelat.getCodigosEquipamentosDER().trim());
  			cabecalhoReferenciaNumEquip.getCell(8).setCellStyle(ce.estiloFiltro);
//  			sheet.addMergedRegion(new CellRangeAddress(4, 4, 9, 12));
//  			sheet.addMergedRegion(new CellRangeAddress(4, 4, 13, 15));
          
  			Row cabecalhoPistaSentido = sheet.createRow(5);
//  			cabecalhoPistaSentido.getCell(0).setCellValue("EQUIPAMENTO:");
//  			cabecalhoPistaSentido.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
//  			cabecalhoPistaSentido.getCell(1).setCellValue(localRelat.getCodigosEquipamentosDER() == null ? "" : localRelat.getCodigosEquipamentosDER().trim());
//  			cabecalhoPistaSentido.getCell(1).setCellStyle(ce.estiloFiltro);
//  			sheet.addMergedRegion(new CellRangeAddress(5, 5, 1, 8));
            
			cabecalhoPistaSentido.getCell(0).setCellValue("TOTAL");
			cabecalhoPistaSentido.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
          
  			cabecalhoPistaSentido.getCell(7).setCellValue("COORDENADA:");
  			cabecalhoPistaSentido.getCell(7).setCellStyle(ce.estiloCabecalhoFiltro);
  			cabecalhoPistaSentido.getCell(8).setCellValue(Funcoes.converterDecimalParaDMS(localRelat.getLatitude(), localRelat.getLongitude()));
  			cabecalhoPistaSentido.getCell(8).setCellStyle(ce.estiloFiltro);
//  			sheet.addMergedRegion(new CellRangeAddress(5, 5, 9, 12));
//  			sheet.addMergedRegion(new CellRangeAddress(5, 5, 13, 15));
          
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
//		    cabecalho1.getCell(10).setCellValue("");
//		    cabecalho1.getCell(10).setCellStyle(ce.estiloCabecalhoCorpoDireita);
//		    cabecalho1.getCell(11).setCellValue("");
//		    cabecalho1.getCell(11).setCellStyle(ce.estiloCabecalhoCorpoDireita);
		    cabecalho1.getCell(8).setCellValue(faixaRelat.getCodigoEquipamentoDER());
		    cabecalho1.getCell(8).setCellStyle(ce.estiloFiltro);
//		    cabecalho1.getCell(13).setCellValue("");
//		    cabecalho1.getCell(13).setCellStyle(ce.estiloCabecalhoCorpoEsquerda);
		    
//		    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), 9, 11));
//		    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), 12, 13));
		    
        	intLinha = (intLinha + 2);
        
        } /*else {
        	
        	cabecalho1 = sheet.createRow((short)intLinha);
        	
        	cabecalho1.getCell(0).setCellValue("Total das faixas");
		    cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoEsquerdaSemBorda);
		    
        	intLinha = (intLinha + 1);
        	
        }*/
	    
    	
        Integer celula = 0, celulaInicio = celula, celulaFim = celula;
        
        Row cabecalho2 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho3 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho4 = sheet.createRow((short)intLinha);
        intLinha++;
		
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatoMesAno = new SimpleDateFormat("MM/yyyy");
		
		// 1 1
		cabecalho2.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo1);
	    cabecalho3.getCell(celula).setCellValue("DATA");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 4600);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho4.getRowNum(), celula, celula));
	    celula++;
	    
	    // 2 2
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("DIA DA SEMANA");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho4.getRowNum(), celula, celula));
	    celula++;
	    
	    // 3 3
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("FLUXO VEICULAR");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho4.getRowNum(), celula, celula));
	    celula++;
	    
	    // 4 4
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("REGIST. OCR");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho4.getRowNum(), celula, celula));
	    celula++;
	    
	    //DETECTADOS
	    // 1 5
	    celulaInicio = celula;
	    cabecalho2.getCell(celula).setCellValue("");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue("AUTOS DETECTADOS EM " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("TOTAL");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    // 2 6
	    cabecalho2.getCell(celula).setCellValue("");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("AUTOS DETECTADOS EM " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("567-32");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;

	    // 3 7
		cabecalho2.getCell(celula).setCellValue("");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue("AUTOS DETECTADOS EM " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("605-03");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;

	    // 4 8
		cabecalho2.getCell(celula).setCellValue("");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue("AUTOS DETECTADOS EM " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("745-50");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    // 5 9
	    cabecalho2.getCell(celula).setCellValue("");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue("AUTOS DETECTADOS EM " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("746-30");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    // 6 10
	    cabecalho2.getCell(celula).setCellValue("");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue("AUTOS DETECTADOS EM " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("747-10");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celulaFim = celula;
	    celula++;
	    
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho3.getRowNum(), celulaInicio, celulaFim));
	    
	    //VÁLIDOS
	    // 1 11
	    celulaInicio = celula;
	    cabecalho2.getCell(celula).setCellValue("");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue("AUTOS VÁLIDOS EM " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("TOTAL");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    // 2 12
	    cabecalho2.getCell(celula).setCellValue("");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("AUTOS VÁLIDOS EM " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("567-32");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;

	    // 3 13
		cabecalho2.getCell(celula).setCellValue("");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue("AUTOS VÁLIDOS EM " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("605-03");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;

	    // 4 14
		cabecalho2.getCell(celula).setCellValue("");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue("AUTOS VÁLIDOS EM " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("745-50");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    // 5 15
	    cabecalho2.getCell(celula).setCellValue("");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue("AUTOS VÁLIDOS EM " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("746-30");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    // 6 16
	    cabecalho2.getCell(celula).setCellValue("");
		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue("AUTOS VÁLIDOS EM " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("747-10");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celulaFim = celula;
	    celula++;
	    
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho3.getRowNum(), celulaInicio, celulaFim));


	    Integer intIdPista = faixaRelat != null ? faixaRelat.getIdPista() : null;
		
        //Buscando informações para popular planilhas
	    DadosFluxoVeicular dadosFluxoVeicular = new DadosFluxoVeicular();
	    ArrayList<ItemFluxoVeicular> dadosRelatorio = dadosFluxoVeicular.relatorio12FluxoVeicular(intMes, intAno, localRelat.getIdLocal(), intIdPista);
		
	    int intLinhaInicioDados = intLinha, intLinhaRetorno = intLinhaInicioDados;
	    
	    if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)intLinha);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        }else{
            for(int i=0; i<dadosRelatorio.size(); i++){
            	
            	celula = 0;
            	
            	// 1 1
            	Row linhaSheet = sheet.createRow((short)(i+intLinha));
            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getData());
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
            	celula++;
            	// 2 2
            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getDiaSemanaDesc());
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
            	celula++;
            	
            	// 3 3
            	if (dadosRelatorio.get(i).getFluxoVeicular() != null && dadosRelatorio.get(i).getFluxoVeicular() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicular());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	// 4 4
            	if (dadosRelatorio.get(i).getRegistroOCR() != null && dadosRelatorio.get(i).getRegistroOCR() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getRegistroOCR());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;

            	// 1 5
            	if (dadosRelatorio.get(i).getAutosDetectados() != null && dadosRelatorio.get(i).getAutosDetectados() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectados());
            	}
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	// 2 6
            	if (dadosRelatorio.get(i).getAutosDetectadosEnq56732() != null && dadosRelatorio.get(i).getAutosDetectadosEnq56732() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectadosEnq56732());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	// 3 7
            	if (dadosRelatorio.get(i).getAutosDetectadosEnq60503() != null && dadosRelatorio.get(i).getAutosDetectadosEnq60503() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectadosEnq60503());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	// 4 8
            	if (dadosRelatorio.get(i).getAutosDetectadosEnq74550() != null && dadosRelatorio.get(i).getAutosDetectadosEnq74550() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectadosEnq74550());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	// 5 9
            	if (dadosRelatorio.get(i).getAutosDetectadosEnq74630() != null && dadosRelatorio.get(i).getAutosDetectadosEnq74630() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectadosEnq74630());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	// 6 10
            	if (dadosRelatorio.get(i).getAutosDetectadosEnq74710() != null && dadosRelatorio.get(i).getAutosDetectadosEnq74710() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectadosEnq74710());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;           	
				
            	// 1 11
            	if (dadosRelatorio.get(i).getAutosValidos() != null && dadosRelatorio.get(i).getAutosValidos() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidos());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	// 2 12
            	if (dadosRelatorio.get(i).getAutosValidosEnq56732() != null && dadosRelatorio.get(i).getAutosValidosEnq56732() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidosEnq56732());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	// 3 13
            	if (dadosRelatorio.get(i).getAutosValidosEnq60503() != null && dadosRelatorio.get(i).getAutosValidosEnq60503() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidosEnq60503());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	// 4 14
            	if (dadosRelatorio.get(i).getAutosValidosEnq74550() != null && dadosRelatorio.get(i).getAutosValidosEnq74550() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidosEnq74550());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	// 5 15
            	if (dadosRelatorio.get(i).getAutosValidosEnq74630() != null && dadosRelatorio.get(i).getAutosValidosEnq74630() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidosEnq74630());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	// 6 16
            	if (dadosRelatorio.get(i).getAutosValidosEnq74710() != null && dadosRelatorio.get(i).getAutosValidosEnq74710() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidosEnq74710());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;

            }
        }
       
       	int intLinhaResumo = 0, intLinhaInicioSomaResumo = (intLinhaInicioDados + 1);
       	
    	if (dadosRelatorio.size() > 0) {
    		
    		intLinhaResumo = dadosRelatorio.size() + (intLinha);
    		intLinhaRetorno = intLinhaResumo + 1;
    		
    		celula = 0;
    		
    		Row linhaResumo = sheet.createRow((short)(intLinhaResumo));
    		celulaInicio = celula;
    		linhaResumo.getCell(celula).setCellValue("RESUMO");
    		linhaResumo.getCell(celula).setCellStyle(ce.estiloItensCentralizadoCinza);
    		celula++;
    		
    		linhaResumo.getCell(celula).setCellValue("");
    		linhaResumo.getCell(celula).setCellStyle(ce.estiloItensCentralizadoCinza);
    		celulaFim = celula;
    		celula++;
    		
    		sheet.addMergedRegion(new CellRangeAddress(linhaResumo.getRowNum(), linhaResumo.getRowNum(), celulaInicio, celulaFim));

    		
    		String letraCelulaFluxoVeicular = null, letraCelulaRegistroOCR = null,
    		
    			   letraCelulaAutosDetectados = null, letraCelulaAutosDetectadosEnq56732 = null, letraCelulaAutosDetectadosEnq60503 = null,
     	    	   letraCelulaAutosDetectadosEnq74550 = null,
     	    	   letraCelulaAutosDetectadosEnq74630 = null, letraCelulaAutosDetectadosEnq74710 = null, 
    		
     	    	   letraCelulaAutosValidos = null, letraCelulaAutosValidosEnq56732 = null, letraCelulaAutosValidosEnq60503 = null,
    	    	   letraCelulaAutosValidosEnq74550 = null,
    	    	   letraCelulaAutosValidosEnq74630 = null, letraCelulaAutosValidosEnq74710 = null;
    		
    		
    		//FLUXO VEICULAR
    		letraCelulaFluxoVeicular = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaFluxoVeicular+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaFluxoVeicular+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//REGISTRO OCR
			letraCelulaRegistroOCR = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaRegistroOCR+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaRegistroOCR+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
    		
    		//DETECTADOS
			//AUTOS DETECTADOS TOTAL
			letraCelulaAutosDetectados = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosDetectados+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosDetectados+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
    		//AUTOS DETECTADOS ENQUADRAMENTO 56732
			letraCelulaAutosDetectadosEnq56732 = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosDetectadosEnq56732+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosDetectadosEnq56732+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//AUTOS DETECTADOS ENQUADRAMENTO 60503
			letraCelulaAutosDetectadosEnq60503 = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosDetectadosEnq60503+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosDetectadosEnq60503+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//AUTOS DETECTADOS ENQUADRAMENTO 74550
			letraCelulaAutosDetectadosEnq74550 = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosDetectadosEnq74550+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosDetectadosEnq74550+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//AUTOS DETECTADOS ENQUADRAMENTO 74630
			letraCelulaAutosDetectadosEnq74630 = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosDetectadosEnq74630+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosDetectadosEnq74630+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//AUTOS DETECTADOS ENQUADRAMENTO 74710
			letraCelulaAutosDetectadosEnq74710 = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosDetectadosEnq74710+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosDetectadosEnq74710+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
    		
    		
    		//VÁLIDOS
			//AUTOS VALIDOS TOTAL
			letraCelulaAutosValidos = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosValidos+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;

    		//AUTOS VALIDOS ENQUADRAMENTO 56732
			letraCelulaAutosValidosEnq56732 = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosValidosEnq56732+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosValidosEnq56732+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//AUTOS VALIDOS ENQUADRAMENTO 60503
			letraCelulaAutosValidosEnq60503 = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosValidosEnq60503+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosValidosEnq60503+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//AUTOS VALIDOS ENQUADRAMENTO 74550
			letraCelulaAutosValidosEnq74550 = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosValidosEnq74550+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosValidosEnq74550+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//AUTOS VALIDOS ENQUADRAMENTO 74630
			letraCelulaAutosValidosEnq74630 = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosValidosEnq74630+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosValidosEnq74630+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//AUTOS VALIDOS ENQUADRAMENTO 74710
			letraCelulaAutosValidosEnq74710 = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosValidosEnq74710+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosValidosEnq74710+String.valueOf(linhaResumo.getRowNum())+")");
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
