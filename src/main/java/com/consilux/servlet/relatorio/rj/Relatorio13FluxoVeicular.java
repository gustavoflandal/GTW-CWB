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
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.relatorio.rj.DadosFluxoVeicular;
import com.consilux.model.relatorio.rj.ItemFluxoVeicular;

/**
 * Servlet para a geração de relatório de Fiscalização Eletrônica - Relatório 13 - Todos os endeços (por pista-sentido) mensal - volume veicualar e autos (avanço, parada e velocidade). 
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 05/10/2016
 */
public class Relatorio13FluxoVeicular extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(Relatorio13FluxoVeicular.class);

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
		String contrato = String.format("%03d", Integer.parseInt(numeroContrato)) + "/" + anoContrato;


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
	        String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_estat.13-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
	        
	        response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");
 
	        // Criando area de trabalho para o excel
			SXSSFWorkbook wb = new SXSSFWorkbook();
	        wb.setCompressTempFiles(true);
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        Sheet sheet = wb.createSheet("13-Consilux-"+mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString()); 
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
	        
	        ColecaoEstilos ce = new ColecaoEstilos(wb);
            
            //Filtros informados
            Row cabecalho1 = sheet.createRow(0);
            cabecalho1.getCell(0).setCellValue("FISCALIZAÇÃO ELETRÔNICA - RELATÓRIO 13");
            cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
            
            Row cabecalho2 = sheet.createRow(1);
            cabecalho2.getCell(0).setCellValue("INFORMAÇÕES MENSAIS, PARA TODOS OS ENDEREÇOS - REGISTROS DETECTADOS E VÁLIDOS POR ENQUADRAMENTO");
            cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));
            
            Row cabecalhoContrato = sheet.createRow(2);
            cabecalhoContrato.getCell(0).setCellValue("Nº DO CONTRATO:");
            cabecalhoContrato.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoContrato.getCell(1).setCellValue(contrato);
            cabecalhoContrato.getCell(1).setCellStyle(ce.estiloFiltro);
            
            int intLinha = 4;
            
            //Gerar relatório por equipamento
            intLinha = GerarRelatorio(intMes, intAno, intLinha, response, sheet, wb, userGerador, nomeArquivo, idUsuario, data, usuarioURL, ce, false);
            
            //Gerar relatório por faixa
            intLinha = GerarRelatorio(intMes, intAno, intLinha, response, sheet, wb, userGerador, nomeArquivo, idUsuario, data, usuarioURL, ce, true);

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

	
	public int GerarRelatorio(Integer intMes, Integer intAno, int intLinha,
			  				  HttpServletResponse response, Sheet sheet, SXSSFWorkbook wb, String userGerador,
			  				  String nomeArquivo, Integer idUsuario, Date data, String usuarioURL,
			  				  ColecaoEstilos ce, boolean porFaixa
	 	) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
        Integer celula = 0, celulaInicio = celula, celulaFim = celula;
        
		//Criando as colunas do corpo
        Row cabecalho1 = sheet.createRow((short)intLinha);
    	cabecalho1.getCell(0).setCellValue("Dados por endereço-pista-sentido" + (porFaixa == true ? "-faixa" : ""));
	    cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoNegrito);
	    
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), 0, 1));
	    
    	intLinha = (intLinha + 1);
    	
		Row cabecalho2 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho3 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho4 = sheet.createRow((short)intLinha);
        intLinha++;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatoMesAno = new SimpleDateFormat("MM/yyyy");
		
		celulaInicio = celula;
		cabecalho2.getCell(celula).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo1);
//	    cabecalho3.getCell(celula).setCellValue("ENDEREÇO, PISTA - SENTIDO" + (porFaixa == true ? " - FAIXA" : ""));
	    cabecalho3.getCell(celula).setCellValue("ENDEREÇO");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 4500);
	    celula++;
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 19500);
	    celulaFim = celula;
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho4.getRowNum(), celulaInicio, celulaFim));
	    celula++;
	    
	    celulaInicio = celula;
    	cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("CÓDIGO EQUIPAMENTO");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho4.getRowNum(), celula, celula));
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("Nº EQUIPAMENTO");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3200);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho4.getRowNum(), celula, celula));
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("COORDENADA");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 6000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho4.getRowNum(), celula, celula));
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("FLUXO VEICULAR");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho4.getRowNum(), celula, celula));
	    celulaFim = celula;
	    celula++;
	    
	    
	    celulaInicio = celula;
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("AUTOS DETECTADOS NO MÊS " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("TOTAL");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("567-32");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("605-03");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("745-50");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("746-30");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("747-10");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celulaFim = celula;
	    celula++;
	    
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho3.getRowNum(), celulaInicio, celulaFim));
	    
	    
	    celulaInicio = celula;
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("AUTOS VÁLIDOS NO MÊS " + formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("TOTAL");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("567-32");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("605-03");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("745-50");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("746-30");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("747-10");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    celulaFim = celula;
	    celula++;
	    
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho3.getRowNum(), celulaInicio, celulaFim));

	    
        int intLinhaInicioDados = intLinha, intLinhaRetorno = intLinhaInicioDados;
	    
        //Buscando informações para popular planilhas
	    DadosFluxoVeicular dadosFluxoVeicular = new DadosFluxoVeicular();
	    ArrayList<ItemFluxoVeicular> dadosRelatorio = new ArrayList<ItemFluxoVeicular>(); 
	    
	    if (porFaixa == true) {
	    	dadosRelatorio = dadosFluxoVeicular.relatorio13FluxoVeicular_PorFaixa(intMes, intAno);
	    } else {
	    	dadosRelatorio = dadosFluxoVeicular.relatorio13FluxoVeicular_PorLocal(intMes, intAno);
	    }
        
       if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)intLinha);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        	linhaSemInfo.getCell(0).setCellStyle(ce.estiloItensLinhaSemInfo);
        }else{
            for(int i=0; i<dadosRelatorio.size(); i++){
            	
            	celula = 0;
            	
               	Row linhaSheet = sheet.createRow((short)(i+intLinha));
            	
               	celulaInicio = celula;
               	if (porFaixa == true) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getEnderecoPistaSentidoFaixa());
            	} else {
                	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getEnderecoPistaSentido());
            	}
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensEsquerda);
            	celula++;
            	
            	linhaSheet.getCell(celula).setCellValue("");
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensEsquerda);
            	celulaFim = celula;
            	celula++;
            	
            	sheet.addMergedRegion(new CellRangeAddress(linhaSheet.getRowNum(), linhaSheet.getRowNum(), celulaInicio, celulaFim));
            	
            	if (porFaixa == true) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getCodigoEquipamentoDER());
            	} else {
                	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getCodigosEquipamentosDER());
            	}
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
            	celula++;
            	
            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getSerieEquipamento());
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
            	celula++;
            	
            	linhaSheet.getCell(celula).setCellValue(Funcoes.converterDecimalParaDMS(dadosRelatorio.get(i).getLatitude(), dadosRelatorio.get(i).getLongitude()));
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
            	celula++;
            	
            	
            	if (dadosRelatorio.get(i).getFluxoVeicular() != null && dadosRelatorio.get(i).getFluxoVeicular() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicular());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;

            	
            	if (dadosRelatorio.get(i).getAutosDetectados() != null && dadosRelatorio.get(i).getAutosDetectados() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectados());
            	}
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	if (dadosRelatorio.get(i).getAutosDetectadosEnq56732() != null && dadosRelatorio.get(i).getAutosDetectadosEnq56732() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectadosEnq56732());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	if (dadosRelatorio.get(i).getAutosDetectadosEnq60503() != null && dadosRelatorio.get(i).getAutosDetectadosEnq60503() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectadosEnq60503());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	if (dadosRelatorio.get(i).getAutosDetectadosEnq74550() != null && dadosRelatorio.get(i).getAutosDetectadosEnq74550() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectadosEnq74550());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	if (dadosRelatorio.get(i).getAutosDetectadosEnq74630() != null && dadosRelatorio.get(i).getAutosDetectadosEnq74630() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectadosEnq74630());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	if (dadosRelatorio.get(i).getAutosDetectadosEnq74710() != null && dadosRelatorio.get(i).getAutosDetectadosEnq74710() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosDetectadosEnq74710());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	
            	if (dadosRelatorio.get(i).getAutosValidos() != null && dadosRelatorio.get(i).getAutosValidos() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidos());
            	}
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	if (dadosRelatorio.get(i).getAutosValidosEnq56732() != null && dadosRelatorio.get(i).getAutosValidosEnq56732() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidosEnq56732());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	if (dadosRelatorio.get(i).getAutosValidosEnq60503() != null && dadosRelatorio.get(i).getAutosValidosEnq60503() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidosEnq60503());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	if (dadosRelatorio.get(i).getAutosValidosEnq74550() != null && dadosRelatorio.get(i).getAutosValidosEnq74550() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidosEnq74550());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	if (dadosRelatorio.get(i).getAutosValidosEnq74630() != null && dadosRelatorio.get(i).getAutosValidosEnq74630() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidosEnq74630());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            	
            	if (dadosRelatorio.get(i).getAutosValidosEnq74710() != null && dadosRelatorio.get(i).getAutosValidosEnq74710() > 0) {
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getAutosValidosEnq74710());
            	} 
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            	celula++;
            }
        }
       
       	int intLinhaResumo = 0, intLinhaInicioSomaResumo = (intLinhaInicioDados + 1);
        
    	if (dadosRelatorio.size() > 0) {
    		
    		celula = 0;
    		
    		intLinhaResumo = dadosRelatorio.size() + (intLinha);
    		intLinhaRetorno = intLinhaResumo + 1;
    		
    		Row linhaResumo = sheet.createRow((short)(intLinhaResumo));
    		
    		celulaInicio = celula;
    		linhaResumo.getCell(celula).setCellValue("RESUMO");
    		linhaResumo.getCell(celula).setCellStyle(ce.estiloItensCentralizadoCinza);
    		celula++;
    		linhaResumo.getCell(celula).setCellValue("");
    		linhaResumo.getCell(celula).setCellStyle(ce.estiloItensCentralizadoCinza);
    		celula++;
    		linhaResumo.getCell(celula).setCellValue("");
    		linhaResumo.getCell(celula).setCellStyle(ce.estiloItensCentralizadoCinza);
    		celula++;
    		linhaResumo.getCell(celula).setCellValue("");
    		linhaResumo.getCell(celula).setCellStyle(ce.estiloItensCentralizadoCinza);
    		celula++;
    		linhaResumo.getCell(celula).setCellValue("");
    		linhaResumo.getCell(celula).setCellStyle(ce.estiloItensCentralizadoCinza);
    		celulaFim = celula;
    		celula++;
    	    sheet.addMergedRegion(new CellRangeAddress(intLinhaResumo, intLinhaResumo, celulaInicio, celulaFim));
    		

    		String letraCelulaFluxo = null, 
    			   letraCelulaAutosDetectados = null, letraCelulaAutosDetectadosEnq56732 = null, letraCelulaAutosDetectadosEnq60503 = null,
    			   letraCelulaAutosDetectadosEnq74550 = null,
    			   letraCelulaAutosDetectadosEnq74630 = null, letraCelulaAutosDetectadosEnq74710 = null,
    			   
    			   letraCelulaAutosValidos = null, letraCelulaAutosValidosEnq56732 = null, letraCelulaAutosValidosEnq60503 = null,
    	    	   letraCelulaAutosValidosEnq74550 = null,
    	    	   letraCelulaAutosValidosEnq74630 = null, letraCelulaAutosValidosEnq74710 = null;
    		
    		//FLUXO VEICULAR
			letraCelulaFluxo = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaFluxo+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaFluxo+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			celula++;
			
			//AUTOS DETECTADOS
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
			
			//AUTOS VALIDOS
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
