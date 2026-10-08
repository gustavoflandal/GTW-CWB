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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
 * Servlet para a geração de relatório de Fiscalização Eletrônica - Relatório 10 - Todos os endereços mensal por datas - fluxo, velocidade e autos. 
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 07/10/2016
 */
public class Relatorio10FluxoVeicular extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(Relatorio10FluxoVeicular.class);

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
	        String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_estat.10-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
	        
	        response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");  			
 
	        // Criando area de trabalho para o excel
			XSSFWorkbook wb = new XSSFWorkbook();
//	        wb.setCompressTempFiles(true);
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        Sheet sheet = wb.createSheet("10-Consilux-"+mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString()); 
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
	        
	        ColecaoEstilos ce = new ColecaoEstilos(wb);
            
            //Filtros informados
            Row cabecalho1 = sheet.createRow(0);
            cabecalho1.getCell(0).setCellValue("FISCALIZAÇÃO ELETRÔNICA - RELATÓRIO 10");
            cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
            
            Row cabecalho2 = sheet.createRow(1);
            cabecalho2.getCell(0).setCellValue("INFORMAÇÕES DIÁRIAS, PARA CADA MÊS, PARA TODOS OS ENDEREÇOS");
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
//	 	        wb.dispose();
	 	        
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
							  HttpServletResponse response, Sheet sheet, XSSFWorkbook wb, String userGerador,
					 		  String nomeArquivo, Integer idUsuario, Date data, String usuarioURL,
					 		  ColecaoEstilos ce, boolean porFaixa
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
        //Criando as colunas do corpo
        Row cabecalho1 = sheet.createRow((short)intLinha);
    	cabecalho1.getCell(0).setCellValue("Dados por " + (porFaixa == true ? "faixa" : "equipamento"));
    	cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoNegrito);
	    
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), 0, 1));
	    
    	intLinha = (intLinha + 1);
        
        
		Row cabecalho2 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho3 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho5 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho6 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho7 = sheet.createRow((short)intLinha);
        intLinha++;
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatoMesAno = new SimpleDateFormat("MM/yyyy");
		
		cabecalho2.getCell(0).setCellValue(formatoMesAno.format(sdf.parse(String.format("%04d", intAno) + "-" + String.format("%02d", intMes) + "-01")));
	    cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo1);
//		cabecalho3.getCell(0).setCellValue("ENDEREÇO, PISTA - SENTIDO" + (porFaixa == true ? " - FAIXA" : ""));
		cabecalho3.getCell(0).setCellValue("ENDEREÇO");
	    cabecalho3.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(0).setCellValue("");
	    cabecalho5.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(0, 4500);
	    cabecalho2.getCell(1).setCellValue("");
	    cabecalho2.getCell(1).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(1).setCellValue("");
	    cabecalho3.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(1).setCellValue("");
	    cabecalho5.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho6.getCell(1).setCellValue("");
	    cabecalho6.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho7.getCell(1).setCellValue("");
	    cabecalho7.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(1, 19500);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho7.getRowNum(), 0, 1));
	    
	    cabecalho2.getCell(2).setCellValue("");
	    cabecalho2.getCell(2).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(2).setCellValue("CÓDIGO EQUIPAMENTO");
	    cabecalho3.getCell(2).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(2).setCellValue("");
	    cabecalho5.getCell(2).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho6.getCell(2).setCellValue("");
	    cabecalho6.getCell(2).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho7.getCell(2).setCellValue("");
	    cabecalho7.getCell(2).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(2, 4000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho7.getRowNum(), 2, 2));
	    
    	cabecalho2.getCell(3).setCellValue("");
	    cabecalho2.getCell(3).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(3).setCellValue("COORDENADA");
	    cabecalho3.getCell(3).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(3).setCellValue("");
	    cabecalho5.getCell(3).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho6.getCell(3).setCellValue("");
	    cabecalho6.getCell(3).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho7.getCell(3).setCellValue("");
	    cabecalho7.getCell(3).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(3, 6000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho7.getRowNum(), 3, 3));
	    
	    cabecalho2.getCell(4).setCellValue("");
	    cabecalho2.getCell(4).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(4).setCellValue("VEL. PERMITIDA");
	    cabecalho3.getCell(4).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(4).setCellValue("");
	    cabecalho5.getCell(4).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho6.getCell(4).setCellValue("");
	    cabecalho6.getCell(4).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho7.getCell(4).setCellValue("");
	    cabecalho7.getCell(4).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(4, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho7.getRowNum(), 4, 4));
	    
	    Integer celula = 5, intUltimoDiaMes = 31;
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
	    	
	    	strData = String.valueOf(intAno) + "-" + (String.valueOf(intMes).length() == 1 ? "0" + String.valueOf(intMes) : String.valueOf(intMes)) + "-" + (String.valueOf(dia).length() == 1 ? "0" + String.valueOf(dia) : String.valueOf(dia));
	    	dtData = sdf.parse(strData);
	    	calendarioData.setTime(dtData);
	    	
	    	cabecalho2.getCell(celula).setCellValue("");
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho3.getCell(celula).setCellValue(dataFormat.format(dtData));
			cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho5.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho6.getCell(celula).setCellValue("FLUXO VEICULAR");
		    cabecalho6.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho7.getCell(celula).setCellValue("");
		    cabecalho7.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo);
		    sheet.setColumnWidth(celula, 3000);
		    sheet.addMergedRegion(new CellRangeAddress(cabecalho6.getRowNum(), cabecalho7.getRowNum(), celula, celula));
			celula++;
			cabecalho2.getCell(celula).setCellValue("");
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			cabecalho3.getCell(celula).setCellValue(dataFormat.format(dtData));
			cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho5.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho6.getCell(celula).setCellValue("VEL. MÉDIA");
		    cabecalho6.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho7.getCell(celula).setCellValue("");
		    cabecalho7.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo);
		    sheet.setColumnWidth(celula, 3000);
		    sheet.addMergedRegion(new CellRangeAddress(cabecalho6.getRowNum(), cabecalho7.getRowNum(), celula, celula));
			celula++;
			cabecalho2.getCell(celula).setCellValue("");
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			cabecalho3.getCell(celula).setCellValue(dataFormat.format(dtData));
			cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho5.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho6.getCell(celula).setCellValue("VEL. MÁXIMA");
		    cabecalho6.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho7.getCell(celula).setCellValue("");
		    cabecalho7.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo);
		    sheet.setColumnWidth(celula, 3000);
		    sheet.addMergedRegion(new CellRangeAddress(cabecalho6.getRowNum(), cabecalho7.getRowNum(), celula, celula));
			celula++;

		    cabecalho2.getCell(celula).setCellValue("");
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho3.getCell(celula).setCellValue(dataFormat.format(dtData));
			cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho5.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho6.getCell(celula).setCellValue("AUTOS DETECTADOS");
		    cabecalho6.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho7.getCell(celula).setCellValue("");
		    cabecalho7.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo);
		    sheet.setColumnWidth(celula, 3000);
		    sheet.addMergedRegion(new CellRangeAddress(cabecalho6.getRowNum(), cabecalho7.getRowNum(), celula, celula));
		    celula++;
		    
		    cabecalho2.getCell(celula).setCellValue("");
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho3.getCell(celula).setCellValue(dataFormat.format(dtData));
			cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho5.getCell(celula).setCellValue(diaSemana.format(dtData));
		    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho6.getCell(celula).setCellValue("AUTOS VÁLIDOS");
		    cabecalho6.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho7.getCell(celula).setCellValue("");
		    cabecalho7.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo);
		    sheet.setColumnWidth(celula, 3000);
		    sheet.addMergedRegion(new CellRangeAddress(cabecalho6.getRowNum(), cabecalho7.getRowNum(), celula, celula));
		    celula++;
		    
	    }
	    
    	cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("TOTAL DO MÊS");
		cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho6.getCell(celula).setCellValue("FLUXO VEICULAR");
	    cabecalho6.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho7.getCell(celula).setCellValue("");
	    cabecalho7.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo);
	    sheet.setColumnWidth(celula, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho6.getRowNum(), cabecalho7.getRowNum(), celula, celula));
		celula++;
		cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue("TOTAL DO MÊS");
		cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho6.getCell(celula).setCellValue("VEL. MÉDIA");
	    cabecalho6.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho7.getCell(celula).setCellValue("");
	    cabecalho7.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo);
	    sheet.setColumnWidth(celula, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho6.getRowNum(), cabecalho7.getRowNum(), celula, celula));
		celula++;
		cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		cabecalho3.getCell(celula).setCellValue("TOTAL DO MÊS");
		cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho6.getCell(celula).setCellValue("VEL. MÁXIMA");
	    cabecalho6.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho7.getCell(celula).setCellValue("");
	    cabecalho7.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo);
	    sheet.setColumnWidth(celula, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho6.getRowNum(), cabecalho7.getRowNum(), celula, celula));
		celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("TOTAL DO MÊS");
		cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho6.getCell(celula).setCellValue("AUTOS DETECTADOS");
	    cabecalho6.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho7.getCell(celula).setCellValue("");
	    cabecalho7.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo);
	    sheet.setColumnWidth(celula, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho6.getRowNum(), cabecalho7.getRowNum(), celula, celula));
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("TOTAL DO MÊS");
		cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho6.getCell(celula).setCellValue("AUTOS VÁLIDOS");
	    cabecalho6.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho7.getCell(celula).setCellValue("");
	    cabecalho7.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo);
	    sheet.setColumnWidth(celula, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho6.getRowNum(), cabecalho7.getRowNum(), celula, celula));
	    celula++;
	    
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo2);
	    cabecalho3.getCell(celula).setCellValue("TAXA DE APROVEITAMENTO");
		cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho5.getCell(celula).setCellValue("");
	    cabecalho5.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho6.getCell(celula).setCellValue("");
	    cabecalho6.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho7.getCell(celula).setCellValue("");
	    cabecalho7.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo);
	    sheet.setColumnWidth(celula, 4200);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho7.getRowNum(), celula, celula));
	    celula++;
	    
        
	    int intLinhaInicioDados = intLinha, intLinhaRetorno = intLinhaInicioDados;
	    
        //Buscando informações para popular planilhas
	    DadosFluxoVeicular dadosFluxoVeicular = new DadosFluxoVeicular();
	    ArrayList<ItemFluxoVeicular> dadosRelatorio = new ArrayList<ItemFluxoVeicular>(); 
	    
	    if (porFaixa == true) {
	    	dadosRelatorio = dadosFluxoVeicular.relatorio10FluxoVeicular_PorFaixa(intMes, intAno);
	    } else {
	    	dadosRelatorio = dadosFluxoVeicular.relatorio10FluxoVeicular_PorLocal(intMes, intAno);
	    }
		
	    Integer totalAutosInvalidosNaoTecnicos = 0;
	    
	    if (dadosRelatorio.size() == 0) {
        	Row linhaSemInfo = sheet.createRow((short)intLinha);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        	linhaSemInfo.getCell(0).setCellStyle(ce.estiloItensLinhaSemInfo);
        } else {
        	for (int i = 0; i < dadosRelatorio.size(); i++) {
            	
            	Row linhaSheet = sheet.createRow((short)(i+intLinha));
            	
            	if (porFaixa == true) {
            		linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getEnderecoPistaSentidoFaixa());
            	} else {
                	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getEnderecoPistaSentido());
            	}
            	linhaSheet.getCell(0).setCellStyle(ce.estiloItensEsquerda);
            	linhaSheet.getCell(1).setCellValue("");
            	linhaSheet.getCell(1).setCellStyle(ce.estiloItensEsquerda);
            	sheet.addMergedRegion(new CellRangeAddress(linhaSheet.getRowNum(), linhaSheet.getRowNum(), 0, 1));
            	
            	
            	if (porFaixa == true) {
            		linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getCodigoEquipamentoDER());
            	} else {
                	linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getCodigosEquipamentosDER());
            	}
            	linhaSheet.getCell(2).setCellStyle(ce.estiloItensCentralizado);
            	
            	linhaSheet.getCell(3).setCellValue(Funcoes.converterDecimalParaDMS(dadosRelatorio.get(i).getLatitude(), dadosRelatorio.get(i).getLongitude()));
            	linhaSheet.getCell(3).setCellStyle(ce.estiloItensCentralizado);
            	
            	if (porFaixa == true) {
            		linhaSheet.getCell(4).setCellValue(dadosRelatorio.get(i).getVelocidadePermitida() == 0 ? "" : dadosRelatorio.get(i).getVelocidadePermitida().toString());
            	} else {
            		linhaSheet.getCell(4).setCellValue(dadosRelatorio.get(i).getStrVelocidadesPermitidas());
            	}
            	linhaSheet.getCell(4).setCellStyle(ce.estiloItensNumCentralizado);
            	
            	Integer valorFluxo;
				Integer valorVelMedia;
				Integer valorVelMax;
				Integer valorAutosDetectados;
				Integer valorAutosValidos;
				
				celula = 5;

				for(int h = 0; h < intUltimoDiaMes; h++) {

					valorFluxo = dadosRelatorio.get(i).getCelulasFluxo()[h];
            		valorVelMedia = dadosRelatorio.get(i).getCelulasVelMedia()[h];
            		valorVelMax = dadosRelatorio.get(i).getCelulasVelMax()[h];
            		valorAutosDetectados = dadosRelatorio.get(i).getCelulasAutosDetectados()[h];
            		valorAutosValidos = dadosRelatorio.get(i).getCelulasAutosValidos()[h];
            		
            		if (valorFluxo != null && valorFluxo > 0) {
            			linhaSheet.getCell(celula).setCellValue(valorFluxo);
            		}
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
             		celula++;
            		
             		if (valorVelMedia != null && valorVelMedia > 0) {
            			linhaSheet.getCell(celula).setCellValue(valorVelMedia);
             		}
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
             		celula++;
            		
            		if (valorVelMax != null && valorVelMax > 0) {
            			linhaSheet.getCell(celula).setCellValue(valorVelMax);
            		} 
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
             		celula++;
            		
            		if (valorAutosDetectados != null && valorAutosDetectados > 0) {
            			linhaSheet.getCell(celula).setCellValue(valorAutosDetectados);
            		} 
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
             		celula++;
            		
             		if (valorAutosValidos != null && valorAutosValidos > 0) {
            			linhaSheet.getCell(celula).setCellValue(valorAutosValidos);
            		} 
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
             		celula++;
            	}
				
				Integer valorFluxoMes;
				Integer valorAutosDetectadosMes;
				Integer valorAutosValidosMes;
				Integer valorAutosInvalidosMotivoNaoTecnico;
				Double porcTaxaAproveitamento; //porcAproveitamento;
				
				valorFluxoMes = dadosRelatorio.get(i).getFluxoVeicular();
				valorAutosDetectadosMes = dadosRelatorio.get(i).getAutosDetectados();
				valorAutosValidosMes = dadosRelatorio.get(i).getAutosValidos();
				valorAutosInvalidosMotivoNaoTecnico = dadosRelatorio.get(i).getAutosInvalidosMotivoNaoTecnico();
//				porcAproveitamento = valorAutosDetectadosMes > 0 ? valorAutosValidosMes.doubleValue() / valorAutosDetectadosMes.doubleValue() : 0.0;
				porcTaxaAproveitamento = valorAutosValidosMes.doubleValue() / (valorAutosDetectadosMes.doubleValue() - valorAutosInvalidosMotivoNaoTecnico.doubleValue());
				
				if (valorFluxoMes != null && valorFluxoMes > 0) {
        			linhaSheet.getCell(celula).setCellValue(valorFluxoMes);
				}
        		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
         		celula++;
            	
            	if (dadosRelatorio.get(i).getVelocidadeMedia() != null && dadosRelatorio.get(i).getVelocidadeMedia() > 0) {
        			linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getVelocidadeMedia());
            	} 
        		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
         		celula++;
            	
            	if (dadosRelatorio.get(i).getVelocidadeMaxima() != null && dadosRelatorio.get(i).getVelocidadeMaxima() > 0) {
        			linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getVelocidadeMaxima());
            	} 
        		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
         		celula++;
            	
            	if (valorAutosDetectadosMes != null && valorAutosDetectadosMes > 0) {
        			linhaSheet.getCell(celula).setCellValue(valorAutosDetectadosMes);
            	} 
        		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
         		celula++;
            	
            	if (valorAutosValidosMes != null && valorAutosValidosMes > 0) {
        			linhaSheet.getCell(celula).setCellValue(valorAutosValidosMes);
            	} 
        		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
         		celula++;
            	
//            	if (porcAproveitamento != null && porcAproveitamento > 0.0) {
//        			linhaSheet.getCell(celula).setCellValue(porcAproveitamento);
//            	}
//        		linhaSheet.getCell(celula).setCellStyle(ce.estiloPorcentagem);
//         		celula++;
         		if (porcTaxaAproveitamento != null && porcTaxaAproveitamento > 0.0) {
        			linhaSheet.getCell(celula).setCellValue(porcTaxaAproveitamento);
            	} 
        		linhaSheet.getCell(celula).setCellStyle(ce.estiloPorcentagem);
         		celula++;
         		
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
    		linhaResumo.getCell(2).setCellValue("");
    		linhaResumo.getCell(2).setCellStyle(ce.estiloItensCentralizadoCinza);
    		linhaResumo.getCell(3).setCellValue("");
    		linhaResumo.getCell(3).setCellStyle(ce.estiloItensCentralizadoCinza);
    		linhaResumo.getCell(4).setCellValue("");
    		linhaResumo.getCell(4).setCellStyle(ce.estiloItensCentralizadoCinza);
    	    sheet.addMergedRegion(new CellRangeAddress(intLinhaResumo, intLinhaResumo, 0, 4));
    		
    		celula = 5;
    		
    		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
    		
    		String letraCelulaFluxo = null, letraCelulaVelMedia = null, letraCelulaVelMaxima = null,
    				letraCelulaAutosDetectados = null, letraCelulaAutosValidos = null;
    		
    		CellReference cellRefAutosValidos = null;
    		Cell cellAutosValidos = null;
    		CellValue valorAutosValidos = null;

    		
    		for(int h = 0; h < intUltimoDiaMes; h++) {
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
    			
    			//AUTOS VALIDOS
    			letraCelulaAutosValidos = CellReference.convertNumToColString(celula);
    			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosValidos+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum())+")");
    			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
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
			
			//AUTOS VALIDOS
			letraCelulaAutosValidos = CellReference.convertNumToColString(celula);
			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaAutosValidos+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum())+")");
			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
			
			cellRefAutosValidos = new CellReference(letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum() + 1));
			cellAutosValidos = linhaResumo.getCell(cellRefAutosValidos.getCol());
			valorAutosValidos = evaluator.evaluate(cellAutosValidos);
			celula++;
			
			//% APROVEITAMENTO
			if (valorAutosValidos.getNumberValue() > 0.0) {
				linhaResumo.getCell(celula).setCellFormula("("+letraCelulaAutosValidos+String.valueOf(linhaResumo.getRowNum() + 1)+")/("+letraCelulaAutosDetectados+String.valueOf(linhaResumo.getRowNum() + 1)+"-"+totalAutosInvalidosNaoTecnicos.toString()+")");
				linhaResumo.getCell(celula).setCellStyle(ce.estiloPorcentagemCinza);
			} else {
				linhaResumo.getCell(celula).setCellValue(0);
				linhaResumo.getCell(celula).setCellStyle(ce.estiloPorcentagemCinza);
			}
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
