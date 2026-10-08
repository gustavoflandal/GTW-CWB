package com.consilux.servlet.relatorio;


import java.io.IOException;
import java.sql.SQLException;
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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellRangeAddress;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.medicao.DadosMedicao;
import com.consilux.model.medicao.ItemMedicao;

/**
 * Servlet para a geração de um relatório de Válidas X Enquadramento X Faixa
 * @author Luiz Amaral - Consilux Tecnologia
 * Data: 20/08/2014
 */
public class RelatorioTotalInfracoes extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	public static final Double VALOR_FAIXA_DIA = 83.33;
	private static final Logger logger = Logger.getLogger(RelatorioTotalInfracoes.class);

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
		String velsis = request.getParameter("velsis");// Regra para gerar a medição apenas para o grupo C Velsis - Relatório importado para o CAI
		Date dtIni = null;
		Date dtFim = null;
		Calendar cal = Calendar.getInstance();

		//Validações Necessárias para montar relatório
		try {
			dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dataIni+" 00:00:00");
			dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dataFinal+" 23:59:59");
			
			cal.setTime(dtIni);
			cal.add(Calendar.DATE, 31);
			
			if (cal.before(dtFim)) {
				new Mensagem(response).showErro("O Período pode ser no máximo de 31 dias!", "javascript:window.close();");
				return; 
			}
			
			//Usuario precisa escolher uma opção e não pode escolher as duas
			if((fixo == null && estatico == null) || (fixo != null && estatico != null)){
				new Mensagem(response).showErro("É preciso escolher AO MENOS uma opção e não pode escolher as duas!", "javascript:window.close();");
				return; 
			}
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		cal.setTime(dtFim);
		Integer numDias = cal.get(Calendar.DATE);
		Calendar calIni = Calendar.getInstance();
		calIni.setTime(dtIni);
		Calendar calFim = Calendar.getInstance();
		calFim.setTime(dtFim);
		
		if (calIni.get(Calendar.MONTH) != calFim.get(Calendar.MONTH)) { //Se trocou o mês então adiciona a diferença em dias...
			calIni.add(Calendar.MONTH, 1);
			calIni.add(Calendar.DATE, -1); //ultimo dia do mês...
			numDias += calIni.get(Calendar.DATE);
		}
		
		try {
			
			//Buscar os dados necessários do relatório
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat strYear = new SimpleDateFormat("yyyy");
			SimpleDateFormat strMonth = new SimpleDateFormat("MM");
			Long ano = Long.valueOf(strYear.format(dtIni));
			Long mes = Long.valueOf(strMonth.format(dtIni));
			String strDataInicio = sdf.format(dtIni);
			String strDataFim = sdf.format(dtFim);
			
			// Criando o arquivo fisico
	        String nomeArquivo = "Relatorio B - Total de Imagens - Consorcio LCL 06-2014-SMT de 0" + 
	        					 String.valueOf(mes) + "-" +
	        					 String.valueOf(ano) + " por dia ";
	        
	        String tipoArquivo = "";
	        
	        if(estatico != null){
	        	tipoArquivo = "B2";
	        	nomeArquivo = nomeArquivo + "- Estatico";
	        }else{
	        	tipoArquivo = "B1";
	        	
	        	nomeArquivo = nomeArquivo + "- FixoBarreira";
	        	if(velsis != null)
					nomeArquivo = nomeArquivo + "_GC";
	        }
	        
	        response.setContentType(TipoMime.XLS.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + ".xls\"");  			
 
	        // Criando area de trabalho para o excel
	        HSSFWorkbook wb = new HSSFWorkbook();
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        HSSFSheet sheet = wb.createSheet("Imagens"); 
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
	        
	        //Fonte
            HSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 12);
            f.setColor(HSSFFont.COLOR_NORMAL);
            f.setBold(true);
            f.setFontName("Calibri");
            
            HSSFCellStyle estilo;
            estilo = wb.createCellStyle();
            estilo.setAlignment(HorizontalAlignment.CENTER);
            estilo.setFont(f);
            
	        //Cria uma linha na Planilha.
            Row titulo = sheet.createRow(0);
            titulo.getCell(0).setCellValue("RELATÓRIO DE IMAGENS");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
            titulo.getCell(0).setCellStyle(estilo);
            
            Row titulo2 = sheet.createRow(1);
            titulo2.getCell(0).setCellValue("CONSÓRCIO LCL 06/2014-SMT - " + strDataInicio + " à " + strDataFim);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 13));
            titulo2.getCell(0).setCellStyle(estilo);

            if(estatico != null){
            	Estatico(dtIni, estatico, mes, ano, response, sheet, wb, userGerador, tipoArquivo, nomeArquivo, idUsuario, data, dtFim, usuarioURL);
            }else{
            	Fixo(dtIni, estatico, mes, ano, response, sheet, wb, userGerador, tipoArquivo, nomeArquivo, idUsuario, data, dtFim, usuarioURL, velsis);
            }

			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	

	public void Fixo(Date dtIni, String estatico, Long mes, Long ano,  
					 HttpServletResponse response,
					 HSSFSheet sheet, HSSFWorkbook wb, String userGerador,
					 String tipoArquivo, String nomeArquivo, Integer idUsuario,
					 Date data, Date dtFim, String usuarioURL, String velsis) throws IOException, ConexaoException, SQLException, ModelException{
	
        //Fonte para CABEÇALHO do excel
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
		
		cabecalho.getCell(0).setCellValue("Cod.Faixa Prodam");
	    cabecalho.getCell(0).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(0, 3400);
		cabecalho.getCell(1).setCellValue("Faixa");
	    cabecalho.getCell(1).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(1, 1400);
		cabecalho.getCell(2).setCellValue("Série");
	    cabecalho.getCell(2).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(2, 2600);
		cabecalho.getCell(3).setCellValue("Cod.Local Prodam");
	    cabecalho.getCell(3).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(3, 3500);
		cabecalho.getCell(4).setCellValue("Descrição do Local");
		cabecalho.getCell(4).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(4, 16000);
	    cabecalho.getCell(5).setCellValue("Data Publicação");
		cabecalho.getCell(5).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(5, 3400);
        
        for(int i = 0; i < 31; i++){
        	cabecalho.getCell(i+6).setCellValue("Dia " + Integer.toString(i+1));	
            cabecalho.getCell(i+6).setCellStyle(estiloCorpo);
        }
        cabecalho.getCell(37).setCellValue("Total Geral");
        cabecalho.getCell(37).setCellStyle(estiloCorpo);
        sheet.setColumnWidth(37, 3200);

        //Buscando informações para popular planilhas
		DadosMedicao dadosMedicao = new DadosMedicao();
		ArrayList<ItemMedicao> dadosRelatorio = dadosMedicao.totalInfracoesFixo(mes, ano, velsis);
        
        if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)5);
        	linhaSemInfo.getCell(0).setCellValue("Não há imagens neste intervalo de datas!");
        }else{
	        for(int i=0; i<dadosRelatorio.size(); i++){
	        	Row linhaSheet = sheet.createRow((short)(i+5));
	           	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getCodProdamFaixa());	
	           	linhaSheet.getCell(0).setCellStyle(estiloItensCentralizado);
	        	linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getFaixaEquipamentoTarja());
	        	linhaSheet.getCell(1).setCellStyle(estiloItensCentralizado);
	        	linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getSerieEquipamento());
	        	linhaSheet.getCell(2).setCellStyle(estiloItensCentralizado);
	        	linhaSheet.getCell(3).setCellValue(dadosRelatorio.get(i).getCodProdamLocal());
	        	linhaSheet.getCell(3).setCellStyle(estiloItensCentralizado);
	        	linhaSheet.getCell(4).setCellValue(dadosRelatorio.get(i).getDescLocal());
	        	linhaSheet.getCell(4).setCellStyle(estiloItens);
	        	linhaSheet.getCell(5).setCellValue(dadosRelatorio.get(i).getDtPulicacao());
	        	linhaSheet.getCell(5).setCellStyle(estiloItensCentralizado);
	        	
	        	Long total = (long) 0;
	        	for(int h = 0; h < 31; h++){
        			if(dadosRelatorio.get(i).getFlagFuncionamento() <= (h+1)){
        				linhaSheet.getCell(h+6).setCellValue(dadosRelatorio.get(i).getCelulas()[h]);
        				linhaSheet.getCell(h+6).setCellStyle(estiloItensCentralizado);
        			}
        			total = total + Long.valueOf(dadosRelatorio.get(i).getCelulas()[h]);
	        	}
	        	linhaSheet.getCell(37).setCellValue(String.valueOf(total));
	        	linhaSheet.getCell(37).setCellStyle(estiloItensCentralizado);
	        }
        }
        
        //Setando tamanho automatico das colunas
        for(int i=6; i<=36; i++){
        	sheet.setColumnWidth(i, 1800);
        }
        
        int qtdeLinhas = dadosRelatorio.size()+5+2;
        Row linhaGerador = sheet.createRow((short)(qtdeLinhas));
        sheet.addMergedRegion(new CellRangeAddress(qtdeLinhas, qtdeLinhas, 0, 7));
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
		
        // Salvando o arquivo
        ServletOutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
	}
	

	public void Estatico(Date dtIni, String estatico, Long mes, Long ano,  
						 HttpServletResponse response,
						 HSSFSheet sheet, HSSFWorkbook wb, String userGerador,
						 String tipoArquivo, String nomeArquivo, Integer idUsuario,
						 Date data, Date dtFim, String usuarioURL) throws IOException, ConexaoException, SQLException, ModelException{

        //Fonte para CABEÇALHO do excel
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
        
		Row infoEstaico = sheet.createRow((short)4);
		infoEstaico.getCell(0).setCellValue("SÉRIE Equipamento Estático: 2014100006, 2014100008, 2014100009, 2014100010, 2014100011");
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 3));
		
		infoEstaico.getCell(4).setCellValue("Código PRODAM Equipamento Estático: 3459, 3460, 3461, 3462, 3463");
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 15));
		
		Row cabecalho = sheet.createRow((short)6);
		//Cria as células na linha
		cabecalho.getCell(0).setCellValue("Faixa");
	    cabecalho.getCell(0).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(0, 1500);
		cabecalho.getCell(1).setCellValue("Cd.Local Prodam");
	    cabecalho.getCell(1).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(1, 3400);
		cabecalho.getCell(2).setCellValue("Descrição do Local");
		cabecalho.getCell(2).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(2, 16000);
	    cabecalho.getCell(3).setCellValue("Data Publicação");
		cabecalho.getCell(3).setCellStyle(estiloCorpo);
	    sheet.setColumnWidth(3, 3400);
		
		for(int i = 0; i < 31; i++){
			cabecalho.getCell(i+4).setCellValue("Dia " + Integer.toString(i+1));	
            cabecalho.getCell(i+4).setCellStyle(estiloCorpo);
		}
		cabecalho.getCell(35).setCellValue("Total Geral");
        cabecalho.getCell(35).setCellStyle(estiloCorpo);
        sheet.setColumnWidth(35, 3200);
		
		//Buscando informações para popular planilhas
		DadosMedicao dadosMedicao = new DadosMedicao();
		ArrayList<ItemMedicao> dadosRelatorio = dadosMedicao.totalInfracoesEstatico(mes, ano);
		
        if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)7);
        	linhaSemInfo.getCell(0).setCellValue("Não há imagens neste intervalo de datas!");
        }else{
        	for(int i=0; i<dadosRelatorio.size(); i++){
				
    			Row linhaSheet = sheet.createRow((short)(i+7));
				linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getFaixaEquipamentoTarja());
				linhaSheet.getCell(0).setCellStyle(estiloItensCentralizado);
				linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getCodProdamLocal());
	           	linhaSheet.getCell(1).setCellStyle(estiloItensCentralizado);
				linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getDescLocal());
				linhaSheet.getCell(2).setCellStyle(estiloItens);
				linhaSheet.getCell(3).setCellValue(dadosRelatorio.get(i).getDtPulicacao());
				linhaSheet.getCell(3).setCellStyle(estiloItensCentralizado);
				
				Long total = (long) 0;
				for(int h = 0; h < 31; h++){
					if(dadosRelatorio.get(i).getFlagFuncionamento() <= (h+1)){
						linhaSheet.getCell(h+4).setCellValue(dadosRelatorio.get(i).getCelulas()[h]);
						linhaSheet.getCell(h+4).setCellStyle(estiloItensCentralizado);
					}
					total = total + Long.valueOf(dadosRelatorio.get(i).getCelulas()[h]);
				}
				linhaSheet.getCell(35).setCellValue(String.valueOf(total));
				linhaSheet.getCell(35).setCellStyle(estiloItensCentralizado);
			}
    	}
		
		//Setando tamanho automatico das colunas
		for(int i=4; i<=34; i++){
			sheet.setColumnWidth(i, 1800);
		}
		
        int qtdeLinhas = dadosRelatorio.size()+7+2;
        Row linhaGerador = sheet.createRow((short)(qtdeLinhas));
        sheet.addMergedRegion(new CellRangeAddress(qtdeLinhas, qtdeLinhas, 0, 7));
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
		
		// Salvando o arquivo
		ServletOutputStream out = response.getOutputStream();
		wb.write(out);
		out.close();

	}
	
}
