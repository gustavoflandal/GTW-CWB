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
import com.consilux.model.medicao.LotesReprovados;

/**
 * Servlet para a geração de um relatório de Lotes Reprovados
 * @author Luiz Amaral - Consilux Tecnologia
 * Data: 22/04/2015
 */
public class RelatorioLotesReprovados extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatorioLotesReprovados.class);

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
	        String nomeArquivo = "Relatorio G - Lotes Reprovados - Consorcio LCL 06-2014-SMT de 0" + 
	        					 String.valueOf(mes) + "-" +
	        					 String.valueOf(ano);

	        String tipoArquivo = "G";
	        
	        response.setContentType(TipoMime.XLS.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + ".xls\"");  			
 
	        // Criando area de trabalho para o excel
	        HSSFWorkbook wb = new HSSFWorkbook();
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        HSSFSheet sheet = wb.createSheet("Lotes Reprovados"); 
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
            titulo.getCell(0).setCellValue("RELATÓRIO DE LOTES REPROVADOS");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
            titulo.getCell(0).setCellStyle(estilo);
            
            Row titulo2 = sheet.createRow(1);
            titulo2.getCell(0).setCellValue("CONSÓRCIO LCL 06/2014-SMT - " + strDataInicio + " à " + strDataFim);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));
            titulo2.getCell(0).setCellStyle(estilo);
            
            GerarRelatorio(dtIni, dtFim,  mes, ano, response, sheet, wb, userGerador, tipoArquivo, nomeArquivo, idUsuario, data, usuarioURL);

			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel de Lotes Reprovados.", e);
			new ServletException("Erro ao gerar o relatório de Lotes Reprovados: " + e.getMessage());
		}
	}
	

	public void GerarRelatorio(Date dtIni, Date dtFim, Long mes, Long ano,  
					 		   HttpServletResponse response,
					 		   HSSFSheet sheet, HSSFWorkbook wb, String userGerador,
					 		   String tipoArquivo, String nomeArquivo,
					 		   Integer idUsuario, Date data, String usuarioURL) throws IOException, ConexaoException, SQLException, ModelException{
	
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
        
        HSSFCellStyle estiloTitulo;
        estiloTitulo = wb.createCellStyle();
        estiloTitulo.setAlignment(HorizontalAlignment.CENTER);
        estiloTitulo.setFont(f2);
        estiloTitulo.setBorderBottom(BorderStyle.MEDIUM);
        estiloTitulo.setBorderTop(BorderStyle.MEDIUM);
        estiloTitulo.setBorderRight(BorderStyle.MEDIUM);
        estiloTitulo.setBorderLeft(BorderStyle.MEDIUM);
        
        
        Row sDataIni = sheet.createRow((short)3);
        sDataIni.getCell(0).setCellValue("DATA INÍCIO:");
        sDataIni.getCell(0).setCellStyle(estiloCorpo);
        sDataIni.getCell(1).setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(dtIni));
        sDataIni.getCell(1).setCellStyle(estiloCorpo);
//        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 2));
        
        Row sDataFim = sheet.createRow((short)4);
        sDataFim.getCell(0).setCellValue("DATA FIM:");
        sDataFim.getCell(0).setCellStyle(estiloCorpo);
        sDataIni.getCell(1).setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(dtFim));
        sDataIni.getCell(1).setCellStyle(estiloCorpo);
//        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 2));
        
        
        //Buscando informações para popular planilhas
		DadosMedicao dadosMedicao = new DadosMedicao();
		ArrayList<LotesReprovados> dadosRelatorio = dadosMedicao.LotesReprovados(mes, ano);
        
		//Criando as colunas do corpo
        Row tituloCabecalho = sheet.createRow((short)6);
        tituloCabecalho.getCell(0).setCellValue("QUANTIDADE LOTES REPROVADOS NO CAV");
        tituloCabecalho.getCell(0).setCellStyle(estiloTitulo);
        tituloCabecalho.getCell(1);
        tituloCabecalho.getCell(1).setCellStyle(estiloTitulo);
        tituloCabecalho.getCell(2);
        tituloCabecalho.getCell(2).setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress((short)6, (short)6, 0, 2));
        
        Row cabecalho = sheet.createRow((short)7);
		cabecalho.getCell(0).setCellValue("Nº Movimento Lote");
	    cabecalho.getCell(0).setCellStyle(estiloTitulo);
	    sheet.setColumnWidth(0, 5000);
		cabecalho.getCell(1).setCellValue("Grupo Autuador");
	    cabecalho.getCell(1).setCellStyle(estiloTitulo);
	    sheet.setColumnWidth(1, 3500);
		cabecalho.getCell(2).setCellValue("Data Movimento");
        cabecalho.getCell(2).setCellStyle(estiloTitulo);
        sheet.setColumnWidth(2, 3300);
        
        int totalLotes = 0;
        int countLinhasRelatorioML = dadosRelatorio.size()+8;
        
        if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)8);
        	linhaSemInfo.getCell(0).setCellValue("Não há Lotes Reprovados neste intervalo de datas!");
        }else{
	        //Populando as linhas do Excel
	        for(int i=0; i<dadosRelatorio.size(); i++){
	        	Row linhaSheet = sheet.createRow((short)(i+8));
	        	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getMovimentoLote());
	        	linhaSheet.getCell(0).setCellStyle(estiloItensCentralizado);
	        	linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getTipoApait());
	        	linhaSheet.getCell(1).setCellStyle(estiloItensCentralizado);
	        	linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getDtMovimento());
	        	linhaSheet.getCell(2).setCellStyle(estiloItensCentralizado);
	        	totalLotes++;
	        }
	        
	        
	        Row linhaTotal = sheet.createRow((short)countLinhasRelatorioML);
	        linhaTotal.getCell(0).setCellValue("TOTAL");
	        linhaTotal.getCell(0).setCellStyle(estiloTitulo);
	        linhaTotal.getCell(1);
	        linhaTotal.getCell(1).setCellStyle(estiloTitulo);
	        sheet.addMergedRegion(new CellRangeAddress(countLinhasRelatorioML, countLinhasRelatorioML, 0, 1));
	        linhaTotal.getCell(2).setCellValue(totalLotes);
	        linhaTotal.getCell(2).setCellStyle(estiloTitulo);
	        
        }
		
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
