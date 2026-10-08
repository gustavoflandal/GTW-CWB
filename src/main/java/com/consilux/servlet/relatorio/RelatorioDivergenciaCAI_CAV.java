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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.util.CellRangeAddress;

import com.consilux.infra.ExpValida;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.exception.ModelException;
import com.consilux.model.medicao.DadosDivergenciaCAI_CAV;
import com.consilux.model.medicao.ItemDivergenciaCAI_CAV;

/**
 * Servlet para a geração de um relatório de Divergências CAI-CAV em Excel
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 10/08/2016
 */
public class RelatorioDivergenciaCAI_CAV extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatorioDivergenciaCAI_CAV.class);

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
		
		String strDataInicio = request.getParameter("dataini");
		String strDataFim = request.getParameter("datafim");
		
		Date dtDataInicio = null;
		Date dtDataFim = null;
		
		//Validações Necessárias para montar relatório

		if((strDataInicio.equals("")) || (strDataFim.equals(""))){
			new Mensagem(response).showErro("A Data Início e a Data Fim devem ser informadas!", "javascript:window.close();");
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
			
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
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
	        String nomeArquivo = "Divergencia CAI-CAV";
	        
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + ".xls\"");  			
 
	        // Criando area de trabalho para o excel
	        HSSFWorkbook wb = new HSSFWorkbook();
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        HSSFSheet sheet = wb.createSheet("Divergencia CAI-CAV"); 
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
            estilo.setVerticalAlignment(VerticalAlignment.CENTER);
            estilo.setFont(f);
            
            HSSFFont f3 = wb.createFont();
            f3.setFontHeightInPoints((short) 10);
            f3.setFontName("Calibri");
            f3.setColor(HSSFFont.COLOR_NORMAL);
            HSSFCellStyle estiloFiltro;
            
            estiloFiltro = wb.createCellStyle();
            estiloFiltro.setAlignment(HorizontalAlignment.LEFT);
            estiloFiltro.setVerticalAlignment(VerticalAlignment.CENTER);
            estiloFiltro.setBorderBottom(BorderStyle.MEDIUM);
            estiloFiltro.setBorderTop(BorderStyle.MEDIUM);
            estiloFiltro.setBorderRight(BorderStyle.MEDIUM);
            estiloFiltro.setBorderLeft(BorderStyle.MEDIUM);
            estiloFiltro.setFont(f3);
            
            
	        //Cria uma linha na Planilha.
            Row titulo = sheet.createRow(0);
            titulo.getCell(0).setCellValue("RELATÓRIO DE DIVERGÊNCIA CAI-CAV");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
            titulo.getCell(0).setCellStyle(estilo);
            
            Row titulo2 = sheet.createRow(1);
            titulo2.getCell(0).setCellValue("CONSÓRCIO LCL 06/2014-SMT - " + strDataInicioFormatada + " à " + strDataFimFormatada);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));
            titulo2.getCell(0).setCellStyle(estilo);
            
            //Filtros informados
            Row filtroDataInicio = sheet.createRow(3);
            filtroDataInicio.getCell(0).setCellValue("Data Início");
            filtroDataInicio.getCell(0).setCellStyle(estiloFiltro);
            filtroDataInicio.getCell(1).setCellValue(strDataInicioFormatada);
            filtroDataInicio.getCell(1).setCellStyle(estiloFiltro);
            filtroDataInicio.getCell(2).setCellValue("");
            filtroDataInicio.getCell(2).setCellStyle(estiloFiltro);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 2));
            
            Row filtroDataFim = sheet.createRow(4);
            filtroDataFim.getCell(0).setCellValue("Data Fim");
            filtroDataFim.getCell(0).setCellStyle(estiloFiltro);
            filtroDataFim.getCell(1).setCellValue(strDataFimFormatada);
            filtroDataFim.getCell(1).setCellStyle(estiloFiltro);
            filtroDataFim.getCell(2).setCellValue("");
            filtroDataFim.getCell(2).setCellStyle(estiloFiltro);
            sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 2));
            
            GerarRelatorio(dtDataInicio, dtDataFim, response, sheet, wb, userGerador, nomeArquivo, idUsuario, data, usuarioURL);

			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	

	public void GerarRelatorio(Date dtDataInicio, Date dtDataFim,
							   HttpServletResponse response, HSSFSheet sheet, HSSFWorkbook wb, String userGerador,
					 		   String nomeArquivo, Integer idUsuario, Date data, String usuarioURL
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
        //Fonte para cabeçalho do excel
		HSSFFont f4 = wb.createFont();
        f4.setFontHeightInPoints((short) 10);
        f4.setBold(true);
        f4.setFontName("Calibri");
        f4.setColor(HSSFFont.COLOR_NORMAL);

        //Fonte para corpo do excel
        HSSFFont f5 = wb.createFont();
        f5.setFontHeightInPoints((short) 10);
        f5.setFontName("Calibri");
        f5.setColor(HSSFFont.COLOR_NORMAL);
        
        //Estilo para cabeçalho do excel
        HSSFCellStyle estiloCabecalhoCorpo;
        estiloCabecalhoCorpo = wb.createCellStyle();
        estiloCabecalhoCorpo.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setFont(f4);
        
        //Estilos para corpo do excel
        HSSFCellStyle estiloCorpoCentro;
        estiloCorpoCentro = wb.createCellStyle();
        estiloCorpoCentro.setAlignment(HorizontalAlignment.CENTER);
        estiloCorpoCentro.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCorpoCentro.setBorderBottom(BorderStyle.MEDIUM);
        estiloCorpoCentro.setBorderTop(BorderStyle.MEDIUM);
        estiloCorpoCentro.setBorderRight(BorderStyle.MEDIUM);
        estiloCorpoCentro.setBorderLeft(BorderStyle.MEDIUM);
        estiloCorpoCentro.setFont(f5);
        
        HSSFCellStyle estiloCorpoEsquerda;
        estiloCorpoEsquerda = wb.createCellStyle();
        estiloCorpoEsquerda.setAlignment(HorizontalAlignment.LEFT);
        estiloCorpoEsquerda.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCorpoEsquerda.setBorderBottom(BorderStyle.MEDIUM);
        estiloCorpoEsquerda.setBorderTop(BorderStyle.MEDIUM);
        estiloCorpoEsquerda.setBorderRight(BorderStyle.MEDIUM);
        estiloCorpoEsquerda.setBorderLeft(BorderStyle.MEDIUM);
        estiloCorpoEsquerda.setFont(f5);
        

        //Criando as colunas do corpo
		Row cabecalho = sheet.createRow((short)6);
		
		cabecalho.getCell(0).setCellValue("Equipamento");
	    cabecalho.getCell(0).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(0, 3000);
	    
	    cabecalho.getCell(1).setCellValue("GA");
		cabecalho.getCell(1).setCellStyle(estiloCabecalhoCorpo);
		sheet.setColumnWidth(1, 1200);
				
		cabecalho.getCell(2).setCellValue("Lote");
		cabecalho.getCell(2).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(2, 1800);
	    
	    cabecalho.getCell(3).setCellValue("Registro no Lote");
		cabecalho.getCell(3).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(3, 3800);
	    
	    cabecalho.getCell(4).setCellValue("Data Infração");
	    cabecalho.getCell(4).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(4, 4800);

	    cabecalho.getCell(5).setCellValue("Nome da Imagem");
		cabecalho.getCell(5).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(5, 10000);
	    
	    cabecalho.getCell(6).setCellValue("Status CAI");
		cabecalho.getCell(6).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(6, 5000);
	    
	    cabecalho.getCell(7).setCellValue("Status CAV");
		cabecalho.getCell(7).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(7, 5000);
	    
	    cabecalho.getCell(8).setCellValue("Revisão");
		cabecalho.getCell(8).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(8, 1800);
	    
	    cabecalho.getCell(9).setCellValue("Tipo Erro");
		cabecalho.getCell(9).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(9, 5000);
	    
	    cabecalho.getCell(10).setCellValue("Motivo CAI");
		cabecalho.getCell(10).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(10, 13000);
	    
	    cabecalho.getCell(11).setCellValue("Motivo CAV");
		cabecalho.getCell(11).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(11, 13000);
	    
	    cabecalho.getCell(12).setCellValue("Placa CAI");
		cabecalho.getCell(12).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(12, 3000);
	    
	    cabecalho.getCell(13).setCellValue("Placa CAV");
		cabecalho.getCell(13).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(13, 3000);
	    
	    cabecalho.getCell(14).setCellValue("Marca CAI");
		cabecalho.getCell(14).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(14, 3000);
	    
	    cabecalho.getCell(15).setCellValue("Marca CAV");
		cabecalho.getCell(15).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(15, 3000);
	    
	    cabecalho.getCell(16).setCellValue("Digitador");
		cabecalho.getCell(16).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(16, 8000);
	    
	    cabecalho.getCell(17).setCellValue("Auditor");
		cabecalho.getCell(17).setCellStyle(estiloCabecalhoCorpo);
	    sheet.setColumnWidth(17, 8000);
        
        //Buscando informações para popular planilhas
		DadosDivergenciaCAI_CAV dadosDivergencia = new DadosDivergenciaCAI_CAV();
		ArrayList<ItemDivergenciaCAI_CAV> dadosRelatorio = dadosDivergencia.divergenciaCAICAV(dtDataInicio, dtDataFim);
		
        if(dadosRelatorio.size() == 0){
        	Row linhaSemInfo = sheet.createRow((short)7);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        	sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 17));
        }else{
            for(int i=0; i<dadosRelatorio.size(); i++){
            	
            	Row linhaSheet = sheet.createRow((short)(i+7));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getCodProdamLocal());
            	linhaSheet.getCell(0).setCellStyle(estiloCorpoCentro);
            	linhaSheet.getCell(1).setCellValue(dadosRelatorio.get(i).getGrupoAuditor());
            	linhaSheet.getCell(1).setCellStyle(estiloCorpoCentro);
            	linhaSheet.getCell(2).setCellValue(dadosRelatorio.get(i).getCodigoExterno());
            	linhaSheet.getCell(2).setCellStyle(estiloCorpoEsquerda);
        		linhaSheet.getCell(3).setCellValue(dadosRelatorio.get(i).getSequencia());
            	linhaSheet.getCell(3).setCellStyle(estiloCorpoCentro);
            	linhaSheet.getCell(4).setCellValue(dadosRelatorio.get(i).getDataInfracao());
            	linhaSheet.getCell(4).setCellStyle(estiloCorpoCentro);
            	linhaSheet.getCell(5).setCellValue(dadosRelatorio.get(i).getNomeImagem());
            	linhaSheet.getCell(5).setCellStyle(estiloCorpoEsquerda);
            	linhaSheet.getCell(6).setCellValue(dadosRelatorio.get(i).getStatusCAI());
            	linhaSheet.getCell(6).setCellStyle(estiloCorpoCentro);
            	linhaSheet.getCell(7).setCellValue(dadosRelatorio.get(i).getStatusCAV());
            	linhaSheet.getCell(7).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(8).setCellValue(dadosRelatorio.get(i).getRevisao());
            	linhaSheet.getCell(8).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(9).setCellValue(dadosRelatorio.get(i).getTipoErro());
            	linhaSheet.getCell(9).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(10).setCellValue(dadosRelatorio.get(i).getMotivoCAI());
            	linhaSheet.getCell(10).setCellStyle(estiloCorpoEsquerda);
        		linhaSheet.getCell(11).setCellValue(dadosRelatorio.get(i).getMotivoCAV());
            	linhaSheet.getCell(11).setCellStyle(estiloCorpoEsquerda);
        		linhaSheet.getCell(12).setCellValue(dadosRelatorio.get(i).getPlacaCAI());
            	linhaSheet.getCell(12).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(13).setCellValue(dadosRelatorio.get(i).getPlacaCAV());
            	linhaSheet.getCell(13).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(14).setCellValue(dadosRelatorio.get(i).getMarcaCAI());
            	linhaSheet.getCell(14).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(15).setCellValue(dadosRelatorio.get(i).getMarcaCAV());
            	linhaSheet.getCell(15).setCellStyle(estiloCorpoCentro);
        		linhaSheet.getCell(16).setCellValue(dadosRelatorio.get(i).getDigitador());
            	linhaSheet.getCell(16).setCellStyle(estiloCorpoEsquerda);
        		linhaSheet.getCell(17).setCellValue(dadosRelatorio.get(i).getAuditor());
            	linhaSheet.getCell(17).setCellStyle(estiloCorpoEsquerda);
            	
            }
        }
        
//        int qtdeLinhas = dadosRelatorio.size()+7+2;
//        Row linhaGerador = sheet.createRow((short)(qtdeLinhas));
//        sheet.addMergedRegion(new CellRangeAddress(qtdeLinhas, qtdeLinhas, 0, 10));
//        linhaGerador.getCell(0).setCellValue(userGerador);
        
		sheet = wb.getSheetAt(0);

        // Salvando o arquivo
        ServletOutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
	}
}
