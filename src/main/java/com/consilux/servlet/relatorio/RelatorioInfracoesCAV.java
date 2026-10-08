package com.consilux.servlet.relatorio;

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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.consilux.infra.ExpValida;
//import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.medicao.DadosRelatorioInfracoesCAV;
import com.consilux.model.medicao.ItemRelatorioInfracoesCAV;

/**
 * Servlet para a geração de um relatório de Divergências CAI-CAV em Excel
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 10/08/2016
 */
public class RelatorioInfracoesCAV extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatorioInfracoesCAV.class);

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
//        String userGerador = "Relatório gerado em: " + formatador.format(data) + ", pelo usuário: " + usuarioURL;
        String userGerador = "Relatório gerado em: " + formatador.format(data);
		
        
		String strDataInicio = request.getParameter("dataini");
		String strDataFim = request.getParameter("datafim");
		
		String tipoRelatorio = request.getParameter("selTipoPeriodo");
		tipoRelatorio = tipoRelatorio != null ? tipoRelatorio : "";
		String chkPeriodoInfracao = tipoRelatorio.equals("chkPeriodoInfracao") ? tipoRelatorio : null;
		String chkPeriodoAuditoria = tipoRelatorio.equals("chkPeriodoAuditoria") ? tipoRelatorio : null;
		
		Boolean filtroInfracao = chkPeriodoInfracao != null ? true : false;
		Boolean filtroAuditoria = chkPeriodoAuditoria != null ? true : false;
		
		String strCodProdamArray = request.getParameter("sel_cod_prodam");
		String strCodPistaProdamArray = request.getParameter("sel_cod_equip_prodam");
		
		String strMotivoInconsistenciaArray = request.getParameter("sel_inconsistencia");
		String strEnquadramentoArray = request.getParameter("sel_enquadramento");
		String strCodOperadorArray = request.getParameter("sel_operador");
		String strCodAuditorArray = request.getParameter("sel_auditor");
		
		//Agrupamento
		String agruparDiaMes = request.getParameter("selAgruparDiaMes");
		agruparDiaMes = agruparDiaMes != null ? agruparDiaMes : "";
		
		String chkAgruparDia = agruparDiaMes.equals("chkAgruparDia") ? agruparDiaMes : null;
		String chkAgruparMes = agruparDiaMes.equals("chkAgruparMes") ? agruparDiaMes : null;
		String chkAgruparNenhum = agruparDiaMes.equals("chkAgruparNenhum") ? agruparDiaMes : null;
		
		Boolean agruparDia = chkAgruparDia != null ? true : false;
		Boolean agruparMes = chkAgruparMes != null ? true : false;
		Boolean agruparNenhum = chkAgruparNenhum != null ? true : false;
		
		
		String selAgruparLocal = request.getParameter("selAgruparLocal");
		selAgruparLocal = selAgruparLocal != null ? selAgruparLocal : "";
		String selAgruparEquipamento = request.getParameter("selAgruparEquipamento");
		selAgruparEquipamento = selAgruparEquipamento != null ? selAgruparEquipamento : "";
		String selAgruparEnquadramento = request.getParameter("selAgruparEnquadramento");
		selAgruparEnquadramento = selAgruparEnquadramento != null ? selAgruparEnquadramento : "";
		
		String chkAgruparLocal = selAgruparLocal.equals("chkLocal") ? selAgruparLocal : null;
		String chkAgruparEquipamento = selAgruparEquipamento.equals("chkEquipamento") ? selAgruparEquipamento : null;
		String chkAgruparEnquadramento = selAgruparEnquadramento.equals("chkEnquadramento") ? selAgruparEnquadramento : null;
		
		Boolean agruparLocal = chkAgruparLocal != null ? true : false;
		Boolean agruparEquipamento = chkAgruparEquipamento != null ? true : false;
		Boolean agruparEnquadramento = chkAgruparEnquadramento != null ? true : false;
		
		
		Date dtDataInicio = null;
		Date dtDataFim = null;
		
		//Validações Necessárias para montar relatório

		if((strDataInicio.equals("")) || (strDataFim.equals(""))){
			new Mensagem(response).showErro("O período deve ser informado!", "javascript:window.close();");
			return;
		}
		
		if (strDataInicio == null || !ExpValida.DATA.validar(strDataInicio)) {
			new Mensagem(response).showErro("Data Início enviada inválida!", "javascript:window.close();");
			return;
		}
		if (strDataInicio == null || !ExpValida.DATA.validar(strDataInicio)) {
			new Mensagem(response).showErro("Data Fim enviada inválida!", "javascript:window.close();");
			return;
		}
		
		try {
			dtDataInicio = new SimpleDateFormat("dd/MM/yyyy").parse(strDataInicio);
			dtDataFim = new SimpleDateFormat("dd/MM/yyyy").parse(strDataFim);
			
			Calendar calendarioInicio = Calendar.getInstance();
			calendarioInicio.setTime(dtDataInicio);
			Calendar calendarioFim = Calendar.getInstance();
			calendarioFim.setTime(dtDataFim);
			
			if (calendarioInicio.after(calendarioFim)) {
				new Mensagem(response).showErro("A Data Início deve ser menor ou igual da Data Fim!", "javascript:window.close();");
				return; 
			}
			
			strMotivoInconsistenciaArray = strMotivoInconsistenciaArray == null || strMotivoInconsistenciaArray.trim().equals("") ? null : strMotivoInconsistenciaArray.trim();
			strCodProdamArray = strCodProdamArray.trim().equals("") ? null : strCodProdamArray.trim();
			strCodPistaProdamArray = strCodPistaProdamArray.trim().equals("") ? null : strCodPistaProdamArray.trim();
			strEnquadramentoArray = strEnquadramentoArray == null || strEnquadramentoArray.trim().equals("") ? null : strEnquadramentoArray.trim();
			strCodOperadorArray = strCodOperadorArray == null || strCodOperadorArray.trim().equals("") ? null : strCodOperadorArray.trim();
			strCodAuditorArray = strCodAuditorArray == null || strCodAuditorArray.trim().equals("") ? null : strCodAuditorArray.trim();
			
		}
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		if (!filtroInfracao && !filtroAuditoria) {
			new Mensagem(response).showErro("Informar ao menos uma opção do tipo de período (infração ou auditoria)!", "javascript:window.close();");
			return;
		}
		if (filtroInfracao && filtroAuditoria) {
			new Mensagem(response).showErro("Informar apenas uma opção do tipo de período (infração ou auditoria)!", "javascript:window.close();");
			return;
		}
		
		if (!agruparDia && !agruparMes && !agruparNenhum) {
			new Mensagem(response).showErro("Informar ao menos uma opção de agrupamento (dia, mês ou nenhum)!", "javascript:window.close();");
			return;
		}
		if (agruparDia && agruparMes && agruparNenhum) {
			new Mensagem(response).showErro("Informar apenas uma opção de agrupamento (dia, mês ou nenhum)!", "javascript:window.close();");
			return;
		}
		
		try {
			
			//Buscar os dados necessários do relatório
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String strDataInicioFormatada = "";
			String strDataFimFormatada = "";
			
			strDataInicioFormatada = sdf.format(dtDataInicio);
			strDataFimFormatada = sdf.format(dtDataFim);
			
			// Criando o arquivo fisico
	        String nomeArquivo = "Quantitativo por Motivo de Invalidacao";
	        
			response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + ".xlsx\"");
			response.setHeader("Refresh", "300");
			response.setHeader("Pragma","no-cache"); //HTTP 1.0
			response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
			response.setDateHeader("Expires", 0); //prevents caching at the proxy server
			  			
 
	        // Criando area de trabalho para o excel
			XSSFWorkbook wb = new XSSFWorkbook();
			wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
			XSSFSheet sheet = wb.createSheet("Qtde. Motivo Invalidação");
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
	        
	        //Fonte
            XSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 12);
            f.setColor(XSSFFont.COLOR_NORMAL);
            f.setBold(true);
            f.setFontName("Calibri");
            
            XSSFCellStyle estilo;
            estilo = wb.createCellStyle();
            estilo.setAlignment(HorizontalAlignment.CENTER);
            estilo.setVerticalAlignment(VerticalAlignment.CENTER);
            estilo.setFont(f);
            
            XSSFFont f2 = wb.createFont();
            f2.setFontHeightInPoints((short) 10);
            f2.setFontName("Calibri");
            f2.setColor(XSSFFont.COLOR_NORMAL);
            f.setBold(true);
            
            XSSFFont f3 = wb.createFont();
            f3.setFontHeightInPoints((short) 10);
            f3.setFontName("Calibri");
            f3.setColor(XSSFFont.COLOR_NORMAL);
            
            XSSFCellStyle estiloFiltro;
            estiloFiltro = wb.createCellStyle();
            estiloFiltro.setAlignment(HorizontalAlignment.LEFT);
            estiloFiltro.setVerticalAlignment(VerticalAlignment.CENTER);
            estiloFiltro.setBorderBottom(BorderStyle.MEDIUM);
            estiloFiltro.setBorderTop(BorderStyle.MEDIUM);
            estiloFiltro.setBorderRight(BorderStyle.MEDIUM);
            estiloFiltro.setBorderLeft(BorderStyle.MEDIUM);
            estiloFiltro.setFont(f2);
            
            XSSFCellStyle estiloFiltroItem;
            estiloFiltroItem = wb.createCellStyle();
            estiloFiltroItem.setAlignment(HorizontalAlignment.LEFT);
            estiloFiltroItem.setVerticalAlignment(VerticalAlignment.CENTER);
            estiloFiltroItem.setBorderBottom(BorderStyle.MEDIUM);
            estiloFiltroItem.setBorderTop(BorderStyle.MEDIUM);
            estiloFiltroItem.setBorderRight(BorderStyle.MEDIUM);
            estiloFiltroItem.setBorderLeft(BorderStyle.MEDIUM);
            estiloFiltroItem.setFont(f3);
            
            
	        //Cria uma linha na Planilha.
            Row titulo = sheet.createRow(0);
            titulo.getCell(0).setCellValue("RELATÓRIO QUANTITATIVO POR MOTIVO DE INVALIDAÇÃO");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            titulo.getCell(0).setCellStyle(estilo);
            
            Row titulo2 = sheet.createRow(1);
            titulo2.getCell(0).setCellValue("CONSÓRCIO LCL 06/2014-SMT - " + strDataInicioFormatada + " à " + strDataFimFormatada);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
            titulo2.getCell(0).setCellStyle(estilo);
            
            //Filtros informados
            Row filtrosData = sheet.createRow(3);
            filtrosData.getCell(0).setCellValue(filtroInfracao ? "Data Infração" : "Data Auditoria");
            filtrosData.getCell(0).setCellStyle(estiloFiltro);
            filtrosData.getCell(1).setCellValue("");
            filtrosData.getCell(1).setCellStyle(estiloFiltro);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));
            
            Row filtroDataInicio = sheet.createRow(4);
            filtroDataInicio.getCell(0).setCellValue("Data Início");
            filtroDataInicio.getCell(0).setCellStyle(estiloFiltroItem);
            filtroDataInicio.getCell(1).setCellValue(strDataInicioFormatada);
            filtroDataInicio.getCell(1).setCellStyle(estiloFiltroItem);
            
            Row filtroDataFim = sheet.createRow(5);
            filtroDataFim.getCell(0).setCellValue("Data Fim");
            filtroDataFim.getCell(0).setCellStyle(estiloFiltroItem);
            filtroDataFim.getCell(1).setCellValue(strDataFimFormatada);
            filtroDataFim.getCell(1).setCellStyle(estiloFiltroItem);
            
            sheet.setColumnWidth(0, 2400);
            sheet.setColumnWidth(1, 3000);
            
            GerarRelatorio(dtDataInicio, dtDataFim, strMotivoInconsistenciaArray, strCodProdamArray, strCodPistaProdamArray, strEnquadramentoArray,
            			   strCodOperadorArray, strCodAuditorArray, filtroInfracao, filtroAuditoria, agruparDia, agruparMes, agruparNenhum, agruparLocal,
            			   agruparEquipamento, agruparEnquadramento, response, sheet, wb, userGerador, nomeArquivo, idUsuario, data, usuarioURL);

			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	

	public void GerarRelatorio(Date dtDataInicio, Date dtDataFim, String strMotivoInconsistenciaArray, String strCodProdamArray, String strCodPistaProdamArray,
							   String strEnquadramentoArray, String strCodOperadorArray, String strCodAuditorArray,
							   Boolean filtroInfracao, Boolean filtroAuditoria, Boolean agruparDia, Boolean agruparMes, Boolean agruparNenhum,
							   Boolean agruparLocal, Boolean agruparEquipamento, Boolean agruparEnquadramento,
							   HttpServletResponse response, XSSFSheet sheet, XSSFWorkbook wb, String userGerador,
					 		   String nomeArquivo, Integer idUsuario, Date data, String usuarioURL
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
        //Fonte para cabeçalho do excel
		XSSFFont f4 = wb.createFont();
        f4.setFontHeightInPoints((short) 10);
        f4.setBold(true);
        f4.setFontName("Calibri");
        f4.setColor(XSSFFont.COLOR_NORMAL);

        //Fonte para corpo do excel
        XSSFFont f5 = wb.createFont();
        f5.setFontHeightInPoints((short) 10);
        f5.setFontName("Calibri");
        f5.setColor(XSSFFont.COLOR_NORMAL);
        
        //Estilo para cabeçalho do excel
        XSSFCellStyle estiloCabecalhoCorpo;
        estiloCabecalhoCorpo = wb.createCellStyle();
        estiloCabecalhoCorpo.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setFont(f4);
        
        XSSFCellStyle estiloCabecalhoCorpo2;
        estiloCabecalhoCorpo2 = wb.createCellStyle();
        estiloCabecalhoCorpo2.setAlignment(HorizontalAlignment.LEFT);
        estiloCabecalhoCorpo2.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo2.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setFont(f4);
        
        //Estilos para corpo do excel
        XSSFCellStyle estiloCorpoCentro;
        estiloCorpoCentro = wb.createCellStyle();
        estiloCorpoCentro.setAlignment(HorizontalAlignment.CENTER);
        estiloCorpoCentro.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCorpoCentro.setBorderBottom(BorderStyle.MEDIUM);
        estiloCorpoCentro.setBorderTop(BorderStyle.MEDIUM);
        estiloCorpoCentro.setBorderRight(BorderStyle.MEDIUM);
        estiloCorpoCentro.setBorderLeft(BorderStyle.MEDIUM);
        estiloCorpoCentro.setFont(f5);
        
        XSSFCellStyle estiloCorpoEsquerda;
        estiloCorpoEsquerda = wb.createCellStyle();
        estiloCorpoEsquerda.setAlignment(HorizontalAlignment.LEFT);
        estiloCorpoEsquerda.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCorpoEsquerda.setBorderBottom(BorderStyle.MEDIUM);
        estiloCorpoEsquerda.setBorderTop(BorderStyle.MEDIUM);
        estiloCorpoEsquerda.setBorderRight(BorderStyle.MEDIUM);
        estiloCorpoEsquerda.setBorderLeft(BorderStyle.MEDIUM);
        estiloCorpoEsquerda.setFont(f5);
        
        XSSFCellStyle porcentagem2;
        porcentagem2 = wb.createCellStyle();
        porcentagem2.setAlignment(HorizontalAlignment.CENTER);
        porcentagem2.setBorderBottom(BorderStyle.MEDIUM);
        porcentagem2.setBorderTop(BorderStyle.MEDIUM);
        porcentagem2.setBorderRight(BorderStyle.MEDIUM);
        porcentagem2.setBorderLeft(BorderStyle.MEDIUM);
        porcentagem2.setDataFormat(wb.createDataFormat().getFormat("0.00%"));
        porcentagem2.setFont(f5);
        

        Integer celula = 1, celulaInicio = celula, celulaFim = celula;
        Boolean primeiraColuna = false;
        
        //Criando as colunas do corpo
		Row cabecalho = sheet.createRow((short)7);
		Row cabecalho2 = sheet.createRow((short)8);
		
		
		if (agruparDia) {
			agruparMes = false;
			cabecalho2.getCell(celula).setCellValue("Dia");
		    cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		    sheet.setColumnWidth(celula, 3000);
		    celula++;
		}
		
		if (agruparMes) {
			agruparDia = false;
			cabecalho2.getCell(celula).setCellValue("Mês/Ano");
		    cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		    sheet.setColumnWidth(celula, 3000);
		    celula++;
		}
		
		if (agruparLocal) {
			if (celula == 1) {
				primeiraColuna = true;
				celulaInicio = celula;
				celulaFim = celula;
				cabecalho2.getCell(celula).setCellValue("Local");
			    cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
			    sheet.setColumnWidth(celula, 3000);
			    celula++;
			    celulaFim = celula;
			}
			cabecalho2.getCell(celula).setCellValue("Local");
		    cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		    sheet.setColumnWidth(celula, celula == 1 ? 17000 : 20000);
		    
		    if (primeiraColuna) {
		    	sheet.addMergedRegion(new CellRangeAddress(8, 8, celulaInicio, celulaFim));
		    }
		    
		    celula++;
		}
		
		primeiraColuna = false;
	    
		if (agruparEquipamento) {
			cabecalho2.getCell(celula).setCellValue("Equipamento");
		    cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		    sheet.setColumnWidth(celula, 3000);
		    celula++;
		}
	    
		if (agruparEnquadramento) {
			cabecalho2.getCell(celula).setCellValue("Enquadramento");
		    cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		    sheet.setColumnWidth(celula, 3600);
		    celula++;
		}
	    
		
		if (celula == 1) {
			primeiraColuna = true;
			celulaInicio = celula;
			celulaFim = celula;
			cabecalho2.getCell(celula).setCellValue("Motivo da Inconsistência");
		    cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		    sheet.setColumnWidth(celula, 3000);
		    celula++;
		    celulaFim = celula;
		}
		cabecalho2.getCell(celula).setCellValue("Motivo da Inconsistência");
		cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		sheet.setColumnWidth(celula, celula == 1 ? 13000 : 16000);
		
		if (primeiraColuna) {
	    	sheet.addMergedRegion(new CellRangeAddress(8, 8, celulaInicio, celulaFim));
	    }
		
		celula++;
		
		primeiraColuna = false;
		
	    
		celulaInicio = celula;
		celulaFim = celula;
	    cabecalho.getCell(celula).setCellValue("CAI");
		cabecalho.getCell(celula).setCellStyle(estiloCabecalhoCorpo);
		cabecalho2.getCell(celula).setCellValue("Imagens Consistentes");
		cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		sheet.setColumnWidth(celula, 5000);
		celula++;
		
		celulaFim = celula;
		cabecalho.getCell(celula).setCellValue("");
		cabecalho.getCell(celula).setCellStyle(estiloCabecalhoCorpo);
		cabecalho2.getCell(celula).setCellValue("Imagens Inconsistentes");
		cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		sheet.setColumnWidth(celula, 5000);
		celula++;
		
		celulaFim = celula;
		cabecalho.getCell(celula).setCellValue("");
		cabecalho.getCell(celula).setCellStyle(estiloCabecalhoCorpo);
		cabecalho2.getCell(celula).setCellValue("% de I.C x I.I.");
		cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		sheet.setColumnWidth(celula, 3000);
		sheet.addMergedRegion(new CellRangeAddress(7, 7, celulaInicio, celulaFim));
		celula++;
		
		
		celulaInicio = celula;
		celulaFim = celula;
	    cabecalho.getCell(celula).setCellValue("CAV");
		cabecalho.getCell(celula).setCellStyle(estiloCabecalhoCorpo);
		cabecalho2.getCell(celula).setCellValue("Imagens Válidas");
		cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		sheet.setColumnWidth(celula, 4000);
		celula++;
		
		celulaFim = celula;
		cabecalho.getCell(celula).setCellValue("");
		cabecalho.getCell(celula).setCellStyle(estiloCabecalhoCorpo);
		cabecalho2.getCell(celula).setCellValue("Imagens Inválidas");
		cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		sheet.setColumnWidth(celula, 4000);
		celula++;
		
		celulaFim = celula;
		cabecalho.getCell(celula).setCellValue("");
		cabecalho.getCell(celula).setCellStyle(estiloCabecalhoCorpo);
		cabecalho2.getCell(celula).setCellValue("% de I.V. x I.I.");
		cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		sheet.setColumnWidth(celula, 3000);
		sheet.addMergedRegion(new CellRangeAddress(7, 7, celulaInicio, celulaFim));
		celula++;
		
		
		cabecalho2.getCell(celula).setCellValue("Qtde. Amostragem");
		cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		sheet.setColumnWidth(celula, 4200);
		celula++;
		
		cabecalho2.getCell(celula).setCellValue("% Amostragem");
		cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		sheet.setColumnWidth(celula, 4200);
		celula++;
		
		cabecalho2.getCell(celula).setCellValue("Qtde. 100%");
		cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		sheet.setColumnWidth(celula, 3000);
		celula++;
		
		cabecalho2.getCell(celula).setCellValue("% 100%");
		cabecalho2.getCell(celula).setCellStyle(estiloCabecalhoCorpo2);
		sheet.setColumnWidth(celula, 3000);
		celula++;
        
        //Buscando informações para popular planilhas
		DadosRelatorioInfracoesCAV dadosRelatorioInfracoesCAV = new DadosRelatorioInfracoesCAV();
		ArrayList<ItemRelatorioInfracoesCAV> dadosRelatorio = dadosRelatorioInfracoesCAV.relInfracoesCAV(
																	dtDataInicio, dtDataFim, strMotivoInconsistenciaArray, strCodProdamArray, strCodPistaProdamArray,
																	strEnquadramentoArray, strCodOperadorArray, strCodAuditorArray, filtroInfracao, filtroAuditoria,
																	agruparDia, agruparMes, agruparNenhum, agruparLocal, agruparEquipamento,
																	agruparEnquadramento);
		
		
		if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)9);
        	linhaSemInfo.getCell(1).setCellValue("Não há dados para os filtros informados!");
        	sheet.addMergedRegion(new CellRangeAddress(9, 9, 1, 6));
        }else{
            for(int i=0; i<dadosRelatorio.size(); i++){
            	
            	celula = 1;
        		celulaInicio = celula;
        		celulaFim = celula;
            	
            	Row linhaSheet = sheet.createRow((short)(i+9));
            	
            	if (agruparDia) {
            		agruparMes = false;
	            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getData());
	            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoCentro);
	            	celula++;
            	}
            	
            	if (agruparMes) {
            		agruparDia = false;
	            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getMesAno());
	            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoCentro);
	            	celula++;
            	}
            	
            	if (agruparLocal) {
            		if (celula == 1) {
            			primeiraColuna = true;
            			celulaInicio = celula;
            			celulaFim = celula;
            			linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getDescLocal());
    	            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoEsquerda);
    	            	celula++;
    	            	celulaFim = celula;
            		}
	            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getDescLocal());
	            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoEsquerda);
	            	celula++;
	            	
	            	if (primeiraColuna) {
	            		sheet.addMergedRegion(new CellRangeAddress((i+9), (i+9), celulaInicio, celulaFim));
	            	}
            	}
            	
            	primeiraColuna = false;
            	
            	if (agruparEquipamento) {
	            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getDescEquipamento());
	            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoCentro);
	            	celula++;
            	}
            	
            	if (agruparEnquadramento) {
	        		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getIdEnquadramento());
	            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoCentro);
	            	celula++;
            	}
            	
            	if (celula == 1) {
            		primeiraColuna = true;
        			celulaInicio = celula;
        			celulaFim = celula;
            		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getMotivoInconsistencia());
                	linhaSheet.getCell(celula).setCellStyle(estiloCorpoEsquerda);
                	celula++;
	            	celulaFim = celula;
            	}
            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getMotivoInconsistencia());
            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoEsquerda);
            	celula++;
            	
            	if (primeiraColuna) {
            		sheet.addMergedRegion(new CellRangeAddress((i+9), (i+9), celulaInicio, celulaFim));
            	}
            	
            	primeiraColuna = false;
            	
            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getQtdeImagensConsistentesCAI());
            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoCentro);
            	celula++;
        		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getQtdeImagensInconsistentesCAI());
            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoCentro);
            	celula++;
        		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getPorcentagemConsistInconsist());
            	linhaSheet.getCell(celula).setCellStyle(porcentagem2);
            	celula++;
        		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getQtdeImagensValidasCAV());
            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoCentro);
            	celula++;
        		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getQtdeImagensInvalidasCAV());
            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoCentro);
            	celula++;
        		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getPorcentagemValidasInvalidas());
            	linhaSheet.getCell(celula).setCellStyle(porcentagem2);
            	celula++;
        		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getQtdeAmostragem());
            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoCentro);
            	celula++;
        		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getPorcentagemAmostragem());
            	linhaSheet.getCell(celula).setCellStyle(porcentagem2);
            	celula++;
        		linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getQtde100Porcento());
            	linhaSheet.getCell(celula).setCellStyle(estiloCorpoCentro);
            	celula++;
            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getPorcentagem100Porcento());
            	linhaSheet.getCell(celula).setCellStyle(porcentagem2);
            	celulaFim = celula;
            	celula++;
            }
        }
        
        int qtdeLinhas = dadosRelatorio.size()+9+2;
        Row linhaGerador = sheet.createRow((short)(qtdeLinhas));
        sheet.addMergedRegion(new CellRangeAddress(qtdeLinhas, qtdeLinhas, 1, celulaFim));
        linhaGerador.getCell(1).setCellValue(userGerador);
        
		sheet = wb.getSheetAt(0);

        // Salvando o arquivo
        ServletOutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
	}
}
