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
import com.consilux.model.medicao.ErrosValidacao;

/**
 * Servlet para a geração de um relatório de erros verificados na validação
 * @author Luiz Amaral - Consilux Tecnologia
 * Data: 03/09/2014
 */
public class RelatorioErrosValidacao extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatorioErrosValidacao.class);

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
	        String nomeArquivo = "Relatorio F - Erros na Validacao - Consorcio LCL 06-2014-SMT de 0" + 
	        					 String.valueOf(mes) + "-" +
	        					 String.valueOf(ano);

	        String tipoArquivo = "F";
	        
	        response.setContentType(TipoMime.XLS.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + ".xls\"");  			
 
	        // Criando area de trabalho para o excel
	        HSSFWorkbook wb = new HSSFWorkbook();
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        HSSFSheet sheet = wb.createSheet("Erros de Análise"); 
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
            titulo.getCell(0).setCellValue("RELATÓRIO DE ERROS DE ANÁLISE");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            titulo.getCell(0).setCellStyle(estilo);
            
            Row titulo2 = sheet.createRow(1);
            titulo2.getCell(0).setCellValue("CONSÓRCIO LCL 06/2014-SMT - " + strDataInicio + " à " + strDataFim);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
            titulo2.getCell(0).setCellStyle(estilo);

            GerarRelatorio(dtIni, dtFim,  mes, ano, response, sheet, wb, userGerador, tipoArquivo, nomeArquivo, idUsuario, data, usuarioURL);

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
        
        HSSFCellStyle estiloTitulo;
        estiloTitulo = wb.createCellStyle();
        estiloTitulo.setAlignment(HorizontalAlignment.CENTER);
        estiloTitulo.setFont(f2);
        estiloTitulo.setBorderBottom(BorderStyle.MEDIUM);
        estiloTitulo.setBorderTop(BorderStyle.MEDIUM);
        estiloTitulo.setBorderRight(BorderStyle.MEDIUM);
        estiloTitulo.setBorderLeft(BorderStyle.MEDIUM);
        
        
        //Fonte para corpo do excel
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
		
        
        Row sDataIni = sheet.createRow((short)3);
        sDataIni.getCell(0).setCellValue("DATA INÍCIO:");
        sDataIni.getCell(0).setCellStyle(estiloCorpo);
        sDataIni.getCell(1).setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(dtIni));
        sDataIni.getCell(1).setCellStyle(estiloCorpo);
//        sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 2));
        
        Row sDataFim = sheet.createRow((short)4);
        sDataFim.getCell(0).setCellValue("DATA FIM:");
        sDataFim.getCell(0).setCellStyle(estiloCorpo);
        sDataFim.getCell(1).setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(dtFim));
        sDataFim.getCell(1).setCellStyle(estiloCorpo);
//        sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 2));
        
        
        //Buscando informações para popular planilhas
		DadosMedicao dadosMedicao = new DadosMedicao();
		ArrayList<ErrosValidacao> dadosRelatorio = dadosMedicao.errosValidacao(mes, ano);
		ArrayList<ErrosValidacao> dadosRelatorioTipoEquip = dadosMedicao.errosValidacaoPorTipoEquip(mes, ano);        
        
		//Criando as colunas do corpo
        Row tituloCabecalho = sheet.createRow((short)6);
        tituloCabecalho.getCell(0).setCellValue("QUANTIDADE DE ERROS POR MOVIMENTO DE LOTE");
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
		cabecalho.getCell(2).setCellValue("Qtde Infrações");
        cabecalho.getCell(2).setCellStyle(estiloTitulo);
        sheet.setColumnWidth(2, 3300);
        
        int totalErros = 0;
        int countLinhasRelatorioML = dadosRelatorio.size()+11;
        int linha = 0;
        
        if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)8);
        	linhaSemInfo.getCell(0).setCellValue("Não há Erros de Validação neste intervalo de datas!");
        }else{
	        //Populando as linhas do Excel
	        for(int i=0; i<dadosRelatorio.size(); i++){
	        	Row linhaSheet = sheet.createRow((short)(i+8));
	        	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getMovimentoLote());
	        	linhaSheet.getCell(0).setCellStyle(estiloItensCentralizado);
	        	linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getTipoApait());
	        	linhaSheet.getCell(1).setCellStyle(estiloItensCentralizado);
	        	linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getQtdeErros());
	        	linhaSheet.getCell(2).setCellStyle(estiloItensCentralizado);
	        	totalErros = totalErros + dadosRelatorio.get(i).getQtdeErros();
	        }
	        
	        
	        Row linhaTotal = sheet.createRow((short)countLinhasRelatorioML);
	        linhaTotal.getCell(0).setCellValue("TOTAL");
	        linhaTotal.getCell(0).setCellStyle(estiloTitulo);
	        linhaTotal.getCell(1);
	        linhaTotal.getCell(1).setCellStyle(estiloTitulo);
	        sheet.addMergedRegion(new CellRangeAddress(countLinhasRelatorioML, countLinhasRelatorioML, 0, 1));
	        linhaTotal.getCell(2).setCellValue(totalErros);
	        linhaTotal.getCell(2).setCellStyle(estiloTitulo);
	        
	        linha = countLinhasRelatorioML;
	        
        }
		
		
		//Criando as colunas do corpo
        linha = linha + 8 + 2;
        
        Row tituloCabecalhoTipoEquip = sheet.createRow((short)linha);
        tituloCabecalhoTipoEquip.getCell(0).setCellValue("QUANTIDADE DE ERROS POR TIPO DE EQUIPAMENTO");
        tituloCabecalhoTipoEquip.getCell(0).setCellStyle(estiloTitulo);
        tituloCabecalhoTipoEquip.getCell(1);
        tituloCabecalhoTipoEquip.getCell(1).setCellStyle(estiloTitulo);
        tituloCabecalhoTipoEquip.getCell(2);
        tituloCabecalhoTipoEquip.getCell(2).setCellStyle(estiloTitulo);
	    sheet.addMergedRegion(new CellRangeAddress((short)linha, (short)linha, 0, 2));
	    
	    
	    linha = linha + 1;
	    
	    Row cabecalhoTipoEquip = sheet.createRow((short)linha);
        cabecalhoTipoEquip.getCell(0).setCellValue("Tipo Equipamento");
	    cabecalhoTipoEquip.getCell(0).setCellStyle(estiloTitulo);
	    sheet.setColumnWidth(0, 5000);
	    cabecalhoTipoEquip.getCell(1);
	    cabecalhoTipoEquip.getCell(1).setCellStyle(estiloTitulo);
	    sheet.setColumnWidth(1, 3500);
	    sheet.addMergedRegion(new CellRangeAddress((short)linha, (short)linha, 0, 1));
        cabecalhoTipoEquip.getCell(2).setCellValue("Qtde. Infrações");
		cabecalhoTipoEquip.getCell(2).setCellStyle(estiloTitulo);
        sheet.setColumnWidth(2, 3300);
		

        linha = linha + 1;
        
        totalErros = 0;
        if(dadosRelatorioTipoEquip.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)linha);
        	linhaSemInfo.getCell(0).setCellValue("Não há Erros de Validação neste intervalo de datas!");
        }else{
	        //Populando as linhas do Excel
	        for(int i=0; i<dadosRelatorioTipoEquip.size(); i++){
	        	Row linhaSheet = sheet.createRow((short)(i+linha));
	        	
	        	linhaSheet.getCell(0).setCellValue(dadosRelatorioTipoEquip.get(i).getTipoEquipamento());
	        	linhaSheet.getCell(0).setCellStyle(estiloItens);
	        	linhaSheet.getCell(1);
	        	linhaSheet.getCell(1).setCellStyle(estiloItens);
	        	sheet.addMergedRegion(new CellRangeAddress((short)(i+linha), (short)(i+linha), 0, 1));
	        	linhaSheet.getCell(2).setCellValue(dadosRelatorioTipoEquip.get(i).getQtdeErros());
	        	linhaSheet.getCell(2).setCellStyle(estiloItens);
	        	totalErros = totalErros + dadosRelatorioTipoEquip.get(i).getQtdeErros();
	        	
	        }
	        
	        linha = (linha + dadosRelatorioTipoEquip.size());
	        Row linhaTotal = sheet.createRow((short)linha);
	        linhaTotal.getCell(0).setCellValue("TOTAL");
	        linhaTotal.getCell(0).setCellStyle(estiloTitulo);
	        linhaTotal.getCell(1);
	        linhaTotal.getCell(1).setCellStyle(estiloTitulo);
	        sheet.addMergedRegion(new CellRangeAddress(linha, linha, 0, 1));
	        linhaTotal.getCell(2).setCellValue(totalErros);
	        linhaTotal.getCell(2).setCellStyle(estiloTitulo);
	        
        }
        
        int qtdeLinhas = linha + 3;
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
