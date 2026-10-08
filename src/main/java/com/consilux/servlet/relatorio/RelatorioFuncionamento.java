package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellRangeAddress;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.medicao.DadosMedicao;
import com.consilux.model.medicao.ItemMedicao;

/**
 * Servlet para a geração de um relatório de Válidas X Enquadramento X Faixa
 * @author Raoni Meira Gabriel - Consilux Tecnologia
 */
public class RelatorioFuncionamento extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	public static final String ARQ_JASPER_FIXO = "/WEB-INF/relatorio/RelatorioFuncionamento.jasper";
	public static final String ARQ_JASPER_ESTATICO = "/WEB-INF/relatorio/RelatorioFuncionamentoEstatic.jasper";
	public static final Double VALOR_FAIXA_DIA = 83.33;
	private static final Logger logger = Logger.getLogger(RelatorioFuncionamento.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
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
		
		String dataIni = request.getParameter("dataini");
		String dataFinal = request.getParameter("datafim");
		String fixo = request.getParameter("relFixoHidden");
		String estatico = request.getParameter("relEstaticoHidden");
		String excel = request.getParameter("chkGerarExcel");
		String chk4Minutos = request.getParameter("chk4minutos");
		Date dtIni = null;
		Date dtFim = null;
		String identificacaoCliente = null;
		
		try {
			identificacaoCliente = ConfiguracaoProvider.getInstance().getIdentificacaoCliente();
			dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dataIni+" 00:00:00");
			dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dataFinal+" 23:59:59");
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtIni);
		cal.add(Calendar.DATE, 31);
		
		cal.setTime(dtFim);
		Calendar calIni = Calendar.getInstance();
		calIni.setTime(dtIni);
		Calendar calFim = Calendar.getInstance();
		calFim.setTime(dtFim);
		Integer numDias = cal.get(Calendar.DATE);
		
		if (cal.before(dtFim)) {
			new Mensagem(response).showErro("O Período pode ser no máximo de 31 dias!", "javascript:window.close();");
			return; //O usuário não tem acesso...então cai fora!
		}
		
		if (calIni.get(Calendar.MONTH) != calFim.get(Calendar.MONTH)) { //Se trocou o mês então adiciona a diferença em dias...
			calIni.add(Calendar.MONTH, 1);
			calIni.add(Calendar.DATE, -1); //ultimo dia do mês...
			numDias += calIni.get(Calendar.DATE);
		}

		//Usuario precisa escolher uma opção e não pode escolher as duas
		if((fixo == null && estatico == null) || (fixo != null && estatico != null)){
			new Mensagem(response).showErro("É preciso escolher AO MENOS uma opção e não pode escolher as duas!", "javascript:window.close();");
			return; 
		}
		
		//Montando os dados para download
		SimpleDateFormat strYear = new SimpleDateFormat("yyyy");
		SimpleDateFormat strMonth = new SimpleDateFormat("MM");
		Long ano = Long.valueOf(strYear.format(dtIni));  
		Long mes = Long.valueOf(strMonth.format(dtIni)); 
		
        String nomeArquivo = "Relatorio A - Funcionamento - Consorcio LCL 06-2014-SMT de 0" + 
				 String.valueOf(mes) + "-" +
				 String.valueOf(ano) + " por dia";
        
        String tipoArquivo = "";
        
        if(fixo != null){
	        nomeArquivo = nomeArquivo + " - FixoBarreira";
	        tipoArquivo = "A1";
		}else{
			nomeArquivo = nomeArquivo + " - Estatico";
			tipoArquivo = "A2";
		}
		
		if(excel != null)
			relatorioExcel(dtIni, estatico, response, nomeArquivo, chk4Minutos, userGerador, tipoArquivo, idUsuario, data, dtFim, usuarioURL);
		else
			relatorioPDF(fixo, estatico, response,identificacaoCliente, dtIni, dtFim, numDias, nomeArquivo);
	}
	
	
	/**
	 * Metodo para iniciar a geração do arquivo em PDF
	 * @author Luiz Amaral - Consilux Tecnologia
	 * Data: 04/09/2014
	 */
	private void relatorioPDF(String fixo, String estatico, 
							  HttpServletResponse response,
							  String identificacaoCliente, 
							  Date dtIni, Date dtFim, int numDias,
							  String nomeArquivo) throws IOException{
		
		RelatorioVisual relatorio;
		if(fixo != null){
			relatorio = new RelatorioVisual(getServletContext().getRealPath(ARQ_JASPER_FIXO));			
		}else{
			relatorio = new RelatorioVisual(getServletContext().getRealPath(ARQ_JASPER_ESTATICO));
		}

		System.out.println("DATA_INI: " + new Timestamp(dtIni.getTime()));
		System.out.println("DATA_FIM: " + new Timestamp(dtFim.getTime()));
		
		relatorio.adicParametro("IDENTIFICACAO_CLIENTE", identificacaoCliente);
		relatorio.adicParametro("DATA_INI",new Timestamp(dtIni.getTime()));
		relatorio.adicParametro("DATA_FIM",new Timestamp(dtFim.getTime()));
		relatorio.adicParametro("VALOR_FAIXA_DIA",VALOR_FAIXA_DIA);
		relatorio.adicParametro("NUMERO_DIAS",numDias);
		
		//Relatório dos equipamentos ESTATICO
		if(estatico != null){
			relatorio.adicParametro("SERIE_EQUIPAMENTO_ESTATICO","2014100006, 2014100008, 2014100009, 2014100010, 2014100011");
			relatorio.adicParametro("PRODAM_EQUIPAMENTO_ESTATICO","3459, 3460, 3461, 3462, 3463");
		}
		
		try {
			
			//Relatório dos equipamentos FIXOS E LOMBADA
			relatorio.preencheRelatorio();
			response.setContentType(TipoMime.PDF.getTipo());
			if(fixo != null){
				response.setHeader("Content-Disposition","inline; filename=\"" + nomeArquivo);
			}else{
				response.setHeader("Content-Disposition","inline; filename=\"" + nomeArquivo);
			}
		    relatorio.exportReportToPdfStream(response.getOutputStream());
		    
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório. Erro no Jasper.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	
	/**
	 * Metodo para iniciar a geração do arquivo em EXCEL
	 * @author Luiz Amaral - Consilux Tecnologia
	 * Data: 04/09/2014
	 */
	private void relatorioExcel(Date dtIni, String estatico, HttpServletResponse response, String nomeArquivo, String chk4Minutos, 
			  			   	    String userGerador, String tipoArquivo, Integer idUsuario, Date data, Date dtFim, String usuarioURL){
		try {	
			
			if(estatico != null){
				relEstatico(dtIni, estatico, response, nomeArquivo, userGerador, tipoArquivo, idUsuario, data, dtFim, usuarioURL);
			}else{
				relFixo(dtIni, estatico, response, nomeArquivo, chk4Minutos, userGerador, tipoArquivo, idUsuario, data, dtFim, usuarioURL);
			}
	      
			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	
	/**
	 * Metodo para gerar arquivos em EXCEL para radares Estaticos
	 * @author Luiz Amaral - Consilux Tecnologia
	 * Data: 04/09/2014
	 */
	public void relEstatico(Date dtIni, String estatico, HttpServletResponse response, String nomeArquivo, String userGerador,
							String tipoArquivo, Integer idUsuario, Date data, Date dtFim, String usuarioURL) throws IOException, ConexaoException, SQLException, ModelException{
		
		//Buscar os dados necessários do relatório
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat strYear = new SimpleDateFormat("yyyy");
		SimpleDateFormat strMonth = new SimpleDateFormat("MM");
		Long ano = Long.valueOf(strYear.format(dtIni));  
		Long mes = Long.valueOf(strMonth.format(dtIni)); 
		String strDataInicio = sdf.format(dtIni);
		String strDataFim = sdf.format(dtFim);

		response.setContentType(TipoMime.XLS.getTipo());
		response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + ".xls\"");  			
        ServletOutputStream out = response.getOutputStream();

        // Criando area de trabalho para o excel
        HSSFWorkbook wb = new HSSFWorkbook();
        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
        
	    //Cria uma planilha Excel
        HSSFSheet sheet = wb.createSheet("Operação"); 
        sheet.setVerticallyCenter(true);
        sheet.setHorizontallyCenter(true);
        
        //Fonte para cabecalho
        HSSFFont f = wb.createFont();
        f.setFontHeightInPoints((short) 12);
        f.setColor(HSSFFont.COLOR_NORMAL);
        f.setBold(true);
        f.setFontName("Calibri");
        
        HSSFCellStyle estilo;
        estilo = wb.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setFont(f);
        
        HSSFCellStyle estiloCabecalhoCorpo;
        estiloCabecalhoCorpo = wb.createCellStyle();
        estiloCabecalhoCorpo.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setFont(f);
        
		//Fonte para pintar os campos de funcionamento em VERMELHO
        HSSFFont fCor = wb.createFont();
        fCor.setBold(true);
        fCor.setColor(HSSFFont.COLOR_RED);
        fCor.setFontName("Calibri");
        
        HSSFCellStyle estiloRed = wb.createCellStyle();
        estiloRed.setAlignment(HorizontalAlignment.CENTER);
        estiloRed.setFillPattern(FillPatternType.SOLID_FOREGROUND );
        estiloRed.setFont(fCor);
        estiloRed.setBorderBottom(BorderStyle.MEDIUM);
        estiloRed.setBorderTop(BorderStyle.MEDIUM);
        estiloRed.setBorderRight(BorderStyle.MEDIUM);
        estiloRed.setBorderLeft(BorderStyle.MEDIUM);
        
		//Fonte para pintar os campos de funcionamento em VERMELHO
        HSSFCellStyle estiloGreen = wb.createCellStyle();
        estiloGreen.setAlignment(HorizontalAlignment.CENTER);
        estiloGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND );
        estiloGreen.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        estiloGreen.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.index);
        estiloGreen.setBorderBottom(BorderStyle.MEDIUM);
        estiloGreen.setBorderTop(BorderStyle.MEDIUM);
        estiloGreen.setBorderRight(BorderStyle.MEDIUM);
        estiloGreen.setBorderLeft(BorderStyle.MEDIUM);
        
		//Fonte para pintar os campos de funcionamento em GREY
        HSSFCellStyle estiloGrey = wb.createCellStyle();
        estiloGrey.setAlignment(HorizontalAlignment.CENTER);
        estiloGrey.setFillPattern(FillPatternType.SOLID_FOREGROUND );
        estiloGrey.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloGrey.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloGrey.setBorderBottom(BorderStyle.MEDIUM);
        estiloGrey.setBorderTop(BorderStyle.MEDIUM);
        estiloGrey.setBorderRight(BorderStyle.MEDIUM);
        estiloGrey.setBorderLeft(BorderStyle.MEDIUM);
        
        //Titulo de Planilha
        Row titulo = sheet.createRow(0);
        titulo.getCell(0).setCellValue("RELATÓRIO DE OPERAÇÃO");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
        titulo.getCell(0).setCellStyle(estilo);
        
        Row titulo2 = sheet.createRow(1);
        titulo2.getCell(0).setCellValue("CONSÓRCIO LCL 06/2014-SMT - " + strDataInicio + " à " + strDataFim);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 13));
        titulo2.getCell(0).setCellStyle(estilo);


        HSSFFont f2 = wb.createFont();
        f2.setFontHeightInPoints((short) 9);
        f2.setBold(true);
        f2.setFontName("Calibri");
        f2.setColor(HSSFFont.COLOR_NORMAL);
        
		HSSFFont f3 = wb.createFont();
        f3.setFontHeightInPoints((short) 8);
		f3.setFontName("Calibri");
        f3.setColor(HSSFFont.COLOR_NORMAL);
        
        HSSFCellStyle estiloCorpo;
        estiloCorpo = wb.createCellStyle();
        estiloCorpo.setAlignment(HorizontalAlignment.LEFT);
        estiloCorpo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCorpo.setFont(f2);
        
        HSSFCellStyle estiloItens;
        estiloItens = wb.createCellStyle();
        estiloItens.setAlignment(HorizontalAlignment.LEFT);
        estiloItens.setBorderBottom(BorderStyle.MEDIUM);
        estiloItens.setBorderTop(BorderStyle.MEDIUM);
        estiloItens.setBorderRight(BorderStyle.MEDIUM);
        estiloItens.setBorderLeft(BorderStyle.MEDIUM);
        estiloItens.setFont(f3);
        
        HSSFCellStyle estiloItensCentralizado;
        estiloItensCentralizado = wb.createCellStyle();
        estiloItensCentralizado.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizado.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizado.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizado.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizado.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizado.setFont(f3);
    	
//		Row infoEstaico = sheet.createRow((short)3);
//		infoEstaico.getCell(0).setCellValue("SÉRIE Equipamento Estático: 2014100006, 2014100008, 2014100009, 2014100010, 2014100011, 2014161003, 2014161004");
//		infoEstaico.getCell(0).setCellStyle(estiloCorpo);
//		sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 4));
//		
//		infoEstaico.getCell(4).setCellValue("Código PRODAM Equipamento Estático: 3459, 3460, 3461, 3462, 3463");
//		infoEstaico.getCell(4).setCellStyle(estiloCorpo);
//		sheet.addMergedRegion(new CellRangeAddress(4, 4, 5, 15));
        
        //Criando as colunas do corpo
		Row cabecalhoHoras = sheet.createRow((short)4);
		cabecalhoHoras.getCell(4).setCellValue("Horas TRABALHADAS / Horas da ESCALA");
		cabecalhoHoras.getCell(4).setCellStyle(estiloCabecalhoCorpo);
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 34));
		
        Row cabecalho = sheet.createRow((short)5);
        cabecalhoHoras.getCell(0).setCellValue("Cod.Prodam");
        cabecalhoHoras.getCell(0).setCellStyle(estiloCorpo);
	    cabecalho.getCell(0).setCellValue("");
	    cabecalho.getCell(0).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(0, 3000);
	    sheet.addMergedRegion(new CellRangeAddress(4, 5, 0, 0));
	    cabecalhoHoras.getCell(1).setCellValue("Descrição do Local");
	    cabecalhoHoras.getCell(1).setCellStyle(estiloCorpo);
		cabecalho.getCell(1).setCellValue("");
	    cabecalho.getCell(1).setCellStyle(estiloCorpo);
		sheet.setColumnWidth(1, 18400);
		sheet.addMergedRegion(new CellRangeAddress(4, 5, 1, 1));
	    cabecalhoHoras.getCell(2).setCellValue("Faixa");
	    cabecalhoHoras.getCell(2).setCellStyle(estiloCorpo);
	    cabecalho.getCell(2).setCellValue("");
	    cabecalho.getCell(2).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(2, 1800);
	    sheet.addMergedRegion(new CellRangeAddress(4, 5, 2, 2));
	    cabecalhoHoras.getCell(3).setCellValue("Data de Publicação");
	    cabecalhoHoras.getCell(3).setCellStyle(estiloCorpo);
		cabecalho.getCell(3).setCellValue("");
	    cabecalho.getCell(3).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(3, 4000);
	    sheet.addMergedRegion(new CellRangeAddress(4, 5, 3, 3));
               
        for(int i = 0; i < 31; i++){
        	if (i > 0) {
        		cabecalhoHoras.getCell(i+4).setCellValue("");	
        		cabecalhoHoras.getCell(i+4).setCellStyle(estiloCabecalhoCorpo);
        	}
        	cabecalho.getCell(i+4).setCellValue("dia " + Integer.toString(i+1));	
        	cabecalho.getCell(i+4).setCellStyle(estiloCorpo);
        }


        //Buscando informações para popular planilhas
		DadosMedicao dadosMedicao = new DadosMedicao();
		ArrayList<ItemMedicao> dadosRelatorio = dadosMedicao.funcionamentoEstatico(mes, ano);
        
		if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)6);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        }else{
	        for(int i=0; i<dadosRelatorio.size(); i++){
	        	
	        	Row linhaSheet = sheet.createRow((short)(i+6));
	        	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getCodProdamLocal());
	        	linhaSheet.getCell(0).setCellStyle(estiloItensCentralizado);
	        	linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getDescLocal());
	        	linhaSheet.getCell(1).setCellStyle(estiloItens);
	        	linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getFaixaEquipamentoTarja());
	        	linhaSheet.getCell(2).setCellStyle(estiloItensCentralizado);
	        	linhaSheet.getCell(3).setCellValue(dadosRelatorio.get(i).getDtPulicacao());
	        	linhaSheet.getCell(3).setCellStyle(estiloItensCentralizado);
	        	
	    	   for(int h = 0; h < 31; h++){
	    		   
					//Pintar a celula com VERMELHO - Não houve funcionamento
					//Pintar a celula com VERDE - Não houve funcionamento
					if(dadosRelatorio.get(i).getCelulaEstatico()[h] != "0" && dadosRelatorio.get(i).getCelulaEstatico()[h] != "-1"){
						linhaSheet.getCell(h+4).setCellValue(dadosRelatorio.get(i).getCelulaEstatico()[h]);
						linhaSheet.getCell(h+4).setCellStyle(estiloItens);
					}else if(dadosRelatorio.get(i).getCelulaEstatico()[h] == "0"){
						linhaSheet.getCell(h+4);	
						linhaSheet.getCell(h+4).setCellStyle(estiloGreen);
					}else{
						linhaSheet.getCell(h+4);	
						linhaSheet.getCell(h+4).setCellStyle(estiloGrey);
					}
	           }
	    	   
	           //Legenda
	           short leg = (short) (dadosRelatorio.size()+6+2);
	           Row linhaLeg = sheet.createRow(leg);
	           linhaLeg.getCell(4).setCellStyle(estiloGrey);	
	           linhaLeg.getCell(5).setCellValue("Equipamento NÃO Publicado");
	           
	           Row linhaLeg2 = sheet.createRow(leg+1);
		       linhaLeg2.getCell(4).setCellStyle(estiloGreen);
		       linhaLeg2.getCell(5).setCellValue("Equipamento NÃO Escalado no dia");
	           
	        }
        }
        
        //Setando tamanho automatico das colunas
        for(int i=4; i<=34; i++){
        	sheet.setColumnWidth(i, 2200);
        }
        
        int qtdeLinhas = dadosRelatorio.size()+6+2;
        Row linhaGerador = sheet.createRow((short)(qtdeLinhas));
        sheet.addMergedRegion(new CellRangeAddress(qtdeLinhas, qtdeLinhas, 0, 3));
        linhaGerador.getCell(0).setCellValue(userGerador);

        sheet = wb.getSheetAt(0);
        
        //Gravando dados do histórico da geração do relatório
        try {
        	if (!usuarioURL.equals("SISTEMA")) {
        		dadosMedicao.gravarHistoricoGeracao(response, dtIni, dtFim, nomeArquivo, tipoArquivo, idUsuario, data);
        	}
        	
		}catch (Exception e) {
			throw new ModelException("ERRO ao gravar histórico", e);
		}
        finally {
        	// Salvando o arquivo
            wb.write(out);
            out.close();
            wb.close();
		}
	}
	
	/**
	 * Metodo para gerar arquivos em EXCEL para radares Fixos
	 * @author Luiz Amaral - Consilux Tecnologia
	 * Data: 04/09/2014
	 */

	public void relFixo(Date dtIni, String estatico, HttpServletResponse response, String nomeArquivo, String chk4Minutos, String userGerador,
						String tipoArquivo, Integer idUsuario, Date data, Date dtFim, String usuarioURL) throws IOException, ConexaoException, SQLException, ModelException{

		//Buscar os dados necessários do relatório
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat strYear = new SimpleDateFormat("yyyy");
		SimpleDateFormat strMonth = new SimpleDateFormat("MM");
		Long ano = Long.valueOf(strYear.format(dtIni));  
		Long mes = Long.valueOf(strMonth.format(dtIni));
		String strDataInicio = sdf.format(dtIni);
		String strDataFim = sdf.format(dtFim);

		response.setContentType(TipoMime.XLS.getTipo());
		response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + ".xls\"");  			
		ServletOutputStream out = response.getOutputStream();
		
		// Criando area de trabalho para o excel
		HSSFWorkbook wb = new HSSFWorkbook();
		
		//Cria uma planilha Excel
		HSSFSheet sheet = wb.createSheet("Operação");
		sheet.setVerticallyCenter(true);
		sheet.setHorizontallyCenter(true);
		
		//Fonte para cabecalho
		HSSFFont f = wb.createFont();
		f.setFontHeightInPoints((short) 12);
		f.setColor(HSSFFont.COLOR_NORMAL);
		f.setBold(true);
		f.setFontName("Calibri");
		
        HSSFCellStyle estilo;
        estilo = wb.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setFont(f);
        
        HSSFCellStyle estiloCabecalhoCorpo;
        estiloCabecalhoCorpo = wb.createCellStyle();
        estiloCabecalhoCorpo.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setFont(f);
		
		//Fonte para pintar os campos de funcionamento em VERMELHO
        HSSFCellStyle estiloRed = wb.createCellStyle();
        estiloRed.setAlignment(HorizontalAlignment.CENTER);
        estiloRed.setFillPattern(FillPatternType.SOLID_FOREGROUND );
        estiloRed.setFillForegroundColor(IndexedColors.RED.index);
        estiloRed.setFillBackgroundColor(IndexedColors.RED.index);
        estiloRed.setBorderBottom(BorderStyle.MEDIUM);
        estiloRed.setBorderTop(BorderStyle.MEDIUM);
        estiloRed.setBorderRight(BorderStyle.MEDIUM);
        estiloRed.setBorderLeft(BorderStyle.MEDIUM);
        
		//Fonte para pintar os campos de funcionamento em VERMELHO
        HSSFCellStyle estiloGreen = wb.createCellStyle();
        estiloGreen.setAlignment(HorizontalAlignment.CENTER);
        estiloGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND );
        estiloGreen.setFillForegroundColor(IndexedColors.GREEN.index);
        estiloGreen.setFillBackgroundColor(IndexedColors.GREEN.index);
        estiloGreen.setBorderBottom(BorderStyle.MEDIUM);
        estiloGreen.setBorderTop(BorderStyle.MEDIUM);
        estiloGreen.setBorderRight(BorderStyle.MEDIUM);
        estiloGreen.setBorderLeft(BorderStyle.MEDIUM);
        
        //Fonte para pintar os campos de funcionamento em GREY
        HSSFCellStyle estiloGrey = wb.createCellStyle();
        estiloGrey.setAlignment(HorizontalAlignment.CENTER);
        estiloGrey.setFillPattern(FillPatternType.SOLID_FOREGROUND );
        estiloGrey.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloGrey.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloGrey.setBorderBottom(BorderStyle.MEDIUM);
        estiloGrey.setBorderTop(BorderStyle.MEDIUM);
        estiloGrey.setBorderRight(BorderStyle.MEDIUM);
        estiloGrey.setBorderLeft(BorderStyle.MEDIUM);
		
		//Titulo de Planilha
        Row titulo = sheet.createRow(0);
        titulo.getCell(0).setCellValue("RELATÓRIO DE OPERAÇÃO");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
        titulo.getCell(0).setCellStyle(estilo);
        
        Row titulo2 = sheet.createRow(1);
        titulo2.getCell(0).setCellValue("CONSÓRCIO LCL 06/2014-SMT - " + strDataInicio + " à " + strDataFim);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 13));
        titulo2.getCell(0).setCellStyle(estilo);
		
        //Fonte para corpo do excel
        HSSFFont f2 = wb.createFont();
        f2.setFontHeightInPoints((short) 9);
        f2.setBold(true);
        f2.setFontName("Calibri");
        f2.setColor(HSSFFont.COLOR_NORMAL);
        
		HSSFFont f3 = wb.createFont();
        f3.setFontHeightInPoints((short) 8);
		f3.setFontName("Calibri");
        f3.setColor(HSSFFont.COLOR_NORMAL);

        HSSFCellStyle estiloCorpo;
        estiloCorpo = wb.createCellStyle();
        estiloCorpo.setAlignment(HorizontalAlignment.LEFT);
        estiloCorpo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCorpo.setFont(f2);
        
        HSSFCellStyle estiloItens;
        estiloItens = wb.createCellStyle();
        estiloItens.setAlignment(HorizontalAlignment.LEFT);
        estiloItens.setBorderBottom(BorderStyle.MEDIUM);
        estiloItens.setBorderTop(BorderStyle.MEDIUM);
        estiloItens.setBorderRight(BorderStyle.MEDIUM);
        estiloItens.setBorderLeft(BorderStyle.MEDIUM);
        estiloItens.setFont(f3);
        
        HSSFCellStyle estiloItensCentralizado;
        estiloItensCentralizado = wb.createCellStyle();
        estiloItensCentralizado.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizado.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizado.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizado.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizado.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizado.setFont(f3);
		
		//Criando as colunas do corpo
		Row cabecalho = sheet.createRow((short)4);
		
		cabecalho.getCell(0).setCellValue("Cod.Prodam");
	    cabecalho.getCell(0).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(0, 3000);
		cabecalho.getCell(1).setCellValue("Descrição do Local");
	    cabecalho.getCell(1).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(1, 18400);
		cabecalho.getCell(2).setCellValue("Cód. Equipamento");
	    cabecalho.getCell(2).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(2, 4000);
		cabecalho.getCell(3).setCellValue("Faixa");
	    cabecalho.getCell(3).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(3, 1800);
		cabecalho.getCell(4).setCellValue("Data de Publicação");
		cabecalho.getCell(4).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(4, 4000);
		
		for(int i = 0; i < 31; i++){
		cabecalho.getCell(i+5).setCellValue(Integer.toString(i+1));	
        cabecalho.getCell(i+5).setCellStyle(estiloCorpo);
		}
		
		//Buscando informações para popular planilhas
		DadosMedicao dadosMedicao = new DadosMedicao();
		ArrayList<ItemMedicao> dadosRelatorio;
		if(chk4Minutos == null){
			dadosRelatorio = dadosMedicao.funcionamentoFixo(mes, ano);
		}else{
			dadosRelatorio = dadosMedicao.funcionamentoFixo4Minutos(mes, ano);
		}
				
        if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)5);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        }else{
		
			for(int i=0; i<dadosRelatorio.size(); i++){
				Row linhaSheet = sheet.createRow((short)(i+5));
				linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getCodProdamLocal());
				linhaSheet.getCell(0).setCellStyle(estiloItensCentralizado);
				linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getDescLocal());
				linhaSheet.getCell(1).setCellStyle(estiloItens);
				linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getCodProdamFaixa());
				linhaSheet.getCell(2).setCellStyle(estiloItensCentralizado);
				linhaSheet.getCell(3).setCellValue(dadosRelatorio.get(i).getFaixaEquipamentoTarja());
				linhaSheet.getCell(3).setCellStyle(estiloItensCentralizado);
				linhaSheet.getCell(4).setCellValue(dadosRelatorio.get(i).getDtPulicacao());
				linhaSheet.getCell(4).setCellStyle(estiloItensCentralizado);
				
				for(int h = 0; h < 31; h++){

					if(dadosRelatorio.get(i).getCelulas()[h] > 0){
						linhaSheet.getCell(h+5);	
						linhaSheet.getCell(h+5).setCellStyle(estiloGreen);
					}else if(dadosRelatorio.get(i).getCelulas()[h] == -1){
						linhaSheet.getCell(h+5);	
						linhaSheet.getCell(h+5).setCellStyle(estiloGrey);
					}else{
						linhaSheet.getCell(h+5);	
						linhaSheet.getCell(h+5).setCellStyle(estiloRed);

					}
				}
			}
			
	        //Legenda
	        short leg = (short) (dadosRelatorio.size()+5+3);
	        Row linhaLeg = sheet.createRow(leg);
	        linhaLeg.getCell(5).setCellStyle(estiloGrey);
	        linhaLeg.getCell(6).setCellValue("Equipamento não publicado");
	        
	        Row linhaLeg2 = sheet.createRow(leg+1);
	        linhaLeg2.getCell(5).setCellStyle(estiloRed);
	        linhaLeg2.getCell(6).setCellValue("Equipamento sem dados de trafego para o dia");
	        
	        Row linhaLeg3 = sheet.createRow(leg+2);
	        linhaLeg3.getCell(5).setCellStyle(estiloGreen);
	        linhaLeg3.getCell(6).setCellValue("Equipamento funcionando Corretamente");
	        
        }
		
		//Setando tamanho automatico das colunas
		for(int i=5; i<=35; i++){
		sheet.setColumnWidth(i, 1000);
		}
		
        int qtdeLinhas = dadosRelatorio.size()+5+2;
        Row linhaGerador = sheet.createRow((short)(qtdeLinhas));
        sheet.addMergedRegion(new CellRangeAddress(qtdeLinhas, qtdeLinhas, 0, 4));
        linhaGerador.getCell(0).setCellValue(userGerador);
		
		sheet = wb.getSheetAt(0);
		
        //Gravando dados do histórico da geração do relatório
        try {
        	if (!usuarioURL.equals("SISTEMA")) {
        		dadosMedicao.gravarHistoricoGeracao(response, dtIni, dtFim, nomeArquivo, tipoArquivo, idUsuario, data);
        	}
        	
		}catch (Exception e) {
			throw new ModelException("ERRO ao gravar histórico", e);
		}
        finally {
        	wb.close();
		}
		
		// Salvando o arquivo
		wb.write(out);
		out.close();
	}
	
}
