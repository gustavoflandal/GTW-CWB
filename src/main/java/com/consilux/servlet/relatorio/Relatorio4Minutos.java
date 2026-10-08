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
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellRangeAddress;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.medicao.Arquivos4Minutos;
import com.consilux.model.medicao.DadosMedicao;

/**
 * Servlet para a geração de um relatório de Pacotes de 4 Minutos em Excel
 * @author Luiz Amaral - Consilux Tecnologia
 * Data: 03/09/2014
 */
public class Relatorio4Minutos extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	public static final Double VALOR_FAIXA_DIA = 83.33;
	private static final Logger logger = Logger.getLogger(Relatorio4Minutos.class);
	private static int INICIO_DADOS = 5;

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

        String userGerador = "Relatório gerado em: " + formatador.format(data);
		
		String dataIni = request.getParameter("dataini");
		String dataFinal = request.getParameter("datafim");
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
	        String nomeArquivo = "Relatorio D - Pacotes de 4 Minutos - Consorcio LCL 06-2014-SMT de 0" + 
	        					 String.valueOf(mes) + "-" +
	        					 String.valueOf(ano);
	        
			if(velsis != null)
				nomeArquivo = nomeArquivo + "_GC";

	        String tipoArquivo = "D";
	        
	        response.setContentType(TipoMime.XLS.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + ".xls\"");  			
 
	        // Criando area de trabalho para o excel
	        HSSFWorkbook wb = new HSSFWorkbook();
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        HSSFSheet sheet = wb.createSheet("4 Minutos"); 
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
            titulo.getCell(0).setCellValue("RELATÓRIO DE 4 MINUTOS");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));
            titulo.getCell(0).setCellStyle(estilo);
            
            Row titulo2 = sheet.createRow(1);
            titulo2.getCell(0).setCellValue("CONSÓRCIO LCL 06/2014-SMT - " + strDataInicio + " à " + strDataFim);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 14));
            titulo2.getCell(0).setCellStyle(estilo);

            GerarRelatorio(dtIni, dtFim, mes, ano, response, sheet, wb, 
            			   userGerador, tipoArquivo, nomeArquivo, 
            			   idUsuario, data, usuarioURL, velsis);

			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	

	public void GerarRelatorio(Date dtIni, Date dtFim, Long mes, Long ano,  
					 		   HttpServletResponse response,
					 		   HSSFSheet sheet, HSSFWorkbook wb, String userGerador,
					 		   String tipoArquivo, String nomeArquivo,
					 		   Integer idUsuario, Date data, String usuarioURL, String velsis) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
        //Fonte para o excel
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
        estiloCorpo.setAlignment(HorizontalAlignment.CENTER);
        estiloCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCorpo.setFont(f2);
        
        HSSFCellStyle porcentagem;
        porcentagem = wb.createCellStyle();
        porcentagem.setAlignment(HorizontalAlignment.CENTER);
        porcentagem.setBorderBottom(BorderStyle.MEDIUM);
        porcentagem.setBorderTop(BorderStyle.MEDIUM);
        porcentagem.setBorderRight(BorderStyle.MEDIUM);
        porcentagem.setBorderLeft(BorderStyle.MEDIUM);
        porcentagem.setDataFormat(wb.createDataFormat().getFormat("0%"));
        porcentagem.setFont(f3);
        
        HSSFCellStyle porcentagem_vermelho;
        porcentagem_vermelho = wb.createCellStyle();
        porcentagem_vermelho.setAlignment(HorizontalAlignment.CENTER);
        porcentagem_vermelho.setBorderBottom(BorderStyle.MEDIUM);
        porcentagem_vermelho.setBorderTop(BorderStyle.MEDIUM);
        porcentagem_vermelho.setBorderRight(BorderStyle.MEDIUM);
        porcentagem_vermelho.setBorderLeft(BorderStyle.MEDIUM);
        porcentagem_vermelho.setFillForegroundColor(IndexedColors.RED.index);
        porcentagem_vermelho.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        porcentagem_vermelho.setDataFormat(wb.createDataFormat().getFormat("0%"));
        porcentagem_vermelho.setFont(f3);
		
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
        	cabecalho.getCell(i+INICIO_DADOS).setCellValue(Integer.toString(i+1));	
            cabecalho.getCell(i+INICIO_DADOS).setCellStyle(estiloCorpo);
        }

        //Buscando informações para popular planilhas
		DadosMedicao dadosMedicao = new DadosMedicao();
		ArrayList<Arquivos4Minutos> dadosRelatorio = null;
		if (ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("habilitar_funcionalidade_cav").equals("1"))
			dadosRelatorio = dadosMedicao.arquivos4Minutos_CAV(mes, ano, velsis);
		else 
			dadosRelatorio = dadosMedicao.arquivos4Minutos_CAI(mes, ano, velsis);
		
        HSSFCellStyle estiloItens;
        estiloItens = wb.createCellStyle();
        estiloItens.setBorderBottom(BorderStyle.MEDIUM);
        estiloItens.setBorderTop(BorderStyle.MEDIUM);
        estiloItens.setBorderRight(BorderStyle.MEDIUM);
        estiloItens.setBorderLeft(BorderStyle.MEDIUM);
        estiloItens.setFont(f3);
        
        HSSFCellStyle estiloItensCentralizado;
        estiloItensCentralizado = wb.createCellStyle();
        estiloItensCentralizado.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizado.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizado.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizado.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizado.setFont(f3);
        
        if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)5);
        	linhaSemInfo.getCell(0).setCellValue("Não há arquivos de 4 Minutos neste intervalo de datas!");
        }else{
            for(int i=0; i<dadosRelatorio.size(); i++){
            	
            	Row linhaSheet = sheet.createRow((short)(i+5));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getIdLocal());
            	linhaSheet.getCell(0).setCellStyle(estiloCorpo);
            	linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getLocal());
            	linhaSheet.getCell(1).setCellStyle(estiloItens);
            	linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getCodPistaProdam());
            	linhaSheet.getCell(2).setCellStyle(estiloCorpo);
            	linhaSheet.getCell(3).setCellValue(dadosRelatorio.get(i).getFaixaTarja());
            	linhaSheet.getCell(3).setCellStyle(estiloCorpo);
            	linhaSheet.getCell(4).setCellValue(dadosRelatorio.get(i).getDtPublicacao());
            	linhaSheet.getCell(4).setCellStyle(estiloCorpo);
            	
            	String s_porcentagem;
            	Integer i_porcentagem;
            	Double d_porcentagem;
        	   for(int h = 0; h < 31; h++){
        		   s_porcentagem = dadosRelatorio.get(i).getCelulas()[h];
        		   if (s_porcentagem != null) {
	        		   i_porcentagem = Integer.parseInt(s_porcentagem.replace("%", ""));
	        		   d_porcentagem = i_porcentagem / 100.0;
	        		   
	        		   linhaSheet.getCell(h+INICIO_DADOS).setCellValue(d_porcentagem);
	        		   
	        		   if (i_porcentagem < 90)
	        			   linhaSheet.getCell(h+INICIO_DADOS).setCellStyle(porcentagem_vermelho);
	        		   else
	        			   linhaSheet.getCell(h+INICIO_DADOS).setCellStyle(porcentagem);
        		   }
               }
            }
        }
        
        //Setando tamanho automatico das colunas
        for(int i=INICIO_DADOS; i<=40; i++){
        	sheet.setColumnWidth(i, 1200);
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
			e.printStackTrace();
			throw new ModelException("ERRO ao gravar histórico", e);
		}
		
        // Salvando o arquivo
        ServletOutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
	}
}
