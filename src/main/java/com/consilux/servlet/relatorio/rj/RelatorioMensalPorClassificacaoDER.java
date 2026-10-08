package com.consilux.servlet.relatorio.rj;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.ExpValida;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.relatorio.rj.DadosFluxoVeicular;
import com.consilux.model.relatorio.rj.ItemFluxoVeicular;
/**
 * Servlet para a geração de relatório de fluxo mensal por classificação do DER-MG. 
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 26/04/2022
 */
@WebServlet("/relatorio/rj/RelatorioMensalClassificacaoDER")
public class RelatorioMensalPorClassificacaoDER extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatorioMensalPorClassificacaoDER.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		@SuppressWarnings("unused")
		Integer idUsuario = null;
		String usuarioURL = request.getParameter("sistema");
		if(usuarioURL == null)
		{
			final Acesso acessoUsuario = new Acesso(request, response, true);
			if (!acessoUsuario.verificaAcesso())
				return;
			else
			{
				usuarioURL = acessoUsuario.getUsuario().getNome();
				idUsuario = acessoUsuario.getUsuario().getId();
			}
		}
		else
			usuarioURL = "SISTEMA";
		
        Date data = new Date();  
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"), mesExtenso = new SimpleDateFormat("MMMM"), formatAno = new SimpleDateFormat("yyyy");

        String userGerador = "Relatório gerado em: " + formatador.format(data) + ", pelo usuário: " + usuarioURL;
		
		String strDataInicio = request.getParameter("dataini");
		String strDataFim = request.getParameter("datafim");
		
		Date dtDataInicio = null, dtDataFim = null;
		
		//Validações Necessárias para montar relatório
		if((strDataInicio.equals("")) || (strDataFim.equals("")))
		{
			new Mensagem(response).showErro("A Data Início e a Data Fim devem ser informadas!", "javascript:window.close();");
			return;
		}
		
		if (strDataInicio == null || !ExpValida.DATA.validar(strDataInicio))
		{
			new Mensagem(response).showErro("Data Início enviada inválida!", "javascript:window.close();");
			return;
		}
		if (strDataInicio == null || !ExpValida.DATA.validar(strDataInicio))
		{
			new Mensagem(response).showErro("Data Fim enviada inválida!", "javascript:window.close();");
			return;
		}
			
		try
		{
			dtDataInicio = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(strDataInicio + " 00:00:00");
			dtDataFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(strDataFim + " 23:59:59");
			
			Calendar calendarioInicio = Calendar.getInstance();
			calendarioInicio.setTime(dtDataInicio);
			Calendar calendarioFim = Calendar.getInstance();
			calendarioFim.setTime(dtDataFim);
			
			if (calendarioInicio.after(calendarioFim))
			{
				new Mensagem(response).showErro("A Data Início deve ser menor ou igual da Data Fim!", "javascript:window.close();");
				return; 
			}
			
		} 
		catch (Exception e)
		{
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		try
		{
 			// Criando o arquivo fisico
			String nomeArquivo = "RelatorioFluxoMensal-" + mesExtenso.format(dtDataInicio).substring(0,3).toLowerCase() + formatAno.format(dtDataInicio) + ".xlsx";
	        
			response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");  			
 
	        // Criando area de trabalho para o excel
			SXSSFWorkbook wb = new SXSSFWorkbook();
	        wb.setCompressTempFiles(true);
	        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
	        ColecaoEstilos ce = new ColecaoEstilos(wb);
	        
	        CriarPlanilha(dtDataInicio, dtDataFim, response, wb, userGerador, ce);
	        
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
 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	
	public void CriarPlanilha(Date dataInicio, Date dataFim, HttpServletResponse response, SXSSFWorkbook wb, String userGerador, ColecaoEstilos ce)
			throws IOException, ConexaoException, SQLException, ModelException, ParseException
	{
		try
		{
	        //Cria uma planilha Excel
			String strNomePlanilha = null;
			strNomePlanilha = "Fluxo Mensal";
			
			//Cria uma planilha Excel
		    Sheet sheet = wb.createSheet(strNomePlanilha);  
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
            
            boolean possuiConfigLogo = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("dir_logo_rel_fluxo") != null && ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("dir_logo_rel_fluxo") != "";
            
            if (possuiConfigLogo)
            {
            	InputStream inputStreamLogoDER = RelatorioMensalPorClassificacaoDER.class.getClassLoader().getResourceAsStream(ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("dir_logo_rel_fluxo"));
            	byte[] bytesLogoDER = IOUtils.toByteArray(inputStreamLogoDER);
            	int pictureLogoDER = wb.addPicture(bytesLogoDER, Workbook.PICTURE_TYPE_PNG);
            	SXSSFDrawing drawing = (SXSSFDrawing) sheet.createDrawingPatriarch();
            
	            XSSFClientAnchor anchorLogoDER = new XSSFClientAnchor();
	            anchorLogoDER.setCol1(1); // Sets the column (0 based) of the first cell.
	            anchorLogoDER.setCol2(3); // Sets the column (0 based) of the Second cell.
	            anchorLogoDER.setRow1(1); // Sets the row (0 based) of the first cell.
	            anchorLogoDER.setRow2(6); // Sets the row (0 based) of the Second cell.
	            
	            drawing.createPicture(anchorLogoDER, pictureLogoDER);
            }
            
            Row cabecalho1 = sheet.createRow(1);
            Row cabecalho2 = sheet.createRow(2);
            Row cabecalho3 = sheet.createRow(3);
            Row cabecalho4 = sheet.createRow(4);
            Row cabecalho5 = sheet.createRow(5);
            
            for (int i = 0; i <= 3; i++)
            {
            	cabecalho1.getCell(i).setCellStyle((between(i, 1, 2) ? ce.estiloCabecalhoCorpoApenasBordaSuperior : (i == 0 ? ce.estiloCabecalhoCorpoBordaSuperiorEsquerda : ce.estiloCabecalhoCorpoBordaSuperiorDireita)));
            }
            
            for (int i = 0; i <= 3; i++)
            {
            	cabecalho2.getCell(i).setCellStyle((between(i, 1, 2) ? ce.estiloCabecalhoCorpoSemBordaFundoBranco : (i == 0 ? ce.estiloCabecalhoCorpoApenasBordaEsquerda : ce.estiloCabecalhoCorpoApenasBordaDireita)));
            	cabecalho3.getCell(i).setCellStyle((between(i, 1, 2) ? ce.estiloCabecalhoCorpoSemBordaFundoBranco : (i == 0 ? ce.estiloCabecalhoCorpoApenasBordaEsquerda : ce.estiloCabecalhoCorpoApenasBordaDireita)));
                cabecalho4.getCell(i).setCellStyle((between(i, 1, 2) ? ce.estiloCabecalhoCorpoSemBordaFundoBranco : (i == 0 ? ce.estiloCabecalhoCorpoApenasBordaEsquerda : ce.estiloCabecalhoCorpoApenasBordaDireita)));
            }

            for (int i = 0; i <= 3; i++)
            {
            	cabecalho5.getCell(i).setCellStyle((between(i, 1, 2) ? ce.estiloCabecalhoCorpoApenasBordaInferior : (i == 0 ? ce.estiloCabecalhoCorpoBordaInferiorEsquerda : ce.estiloCabecalhoCorpoBordaInferiorDireita)));
            }
            
            sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho5.getRowNum(), 0, 3));
            
            //Nome relatório
            for (int i = 4; i <= 11; i++)
            {
            	cabecalho1.getCell(i).setCellStyle((between(i, 5, 10) ? ce.estiloCabecalhoCorpoApenasBordaSuperior : (i == 4 ? ce.estiloCabecalhoCorpoBordaSuperiorEsquerda : ce.estiloCabecalhoCorpoBordaSuperiorDireita)));
            }
            sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), 4, 11));
            
            cabecalho2.getCell(4).setCellValue(ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("titulo_1_rel_fluxo"));
            cabecalho2.getCell(4).setCellStyle(ce.estiloCabecalhoCorpoApenasBordaEsquerda);
            cabecalho2.getCell(11).setCellStyle(ce.estiloCabecalhoCorpoApenasBordaDireita);
            sheet.addMergedRegion(new CellRangeAddress(cabecalho2.getRowNum(), cabecalho2.getRowNum(), 4, 11));
            
            cabecalho3.getCell(4).setCellValue(ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("titulo_2_rel_fluxo"));
            cabecalho3.getCell(4).setCellStyle(ce.estiloCabecalhoCorpoApenasBordaEsquerda);
            cabecalho3.getCell(11).setCellStyle(ce.estiloCabecalhoCorpoApenasBordaDireita);
            sheet.addMergedRegion(new CellRangeAddress(cabecalho3.getRowNum(), cabecalho3.getRowNum(), 4, 11));
            
            cabecalho4.getCell(4).setCellValue(ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("titulo_3_rel_fluxo"));
            cabecalho4.getCell(4).setCellStyle(ce.estiloCabecalhoCorpoApenasBordaEsquerda);
            cabecalho4.getCell(11).setCellStyle(ce.estiloCabecalhoCorpoApenasBordaDireita);
            sheet.addMergedRegion(new CellRangeAddress(cabecalho4.getRowNum(), cabecalho4.getRowNum(), 4, 11));
            
            for (int i = 4; i <= 11; i++)
            {
            	cabecalho5.getCell(i).setCellStyle((between(i, 5, 10) ? ce.estiloCabecalhoCorpoApenasBordaInferior : (i == 4 ? ce.estiloCabecalhoCorpoBordaInferiorEsquerda : ce.estiloCabecalhoCorpoBordaInferiorDireita)));
            }
            sheet.addMergedRegion(new CellRangeAddress(cabecalho5.getRowNum(), cabecalho5.getRowNum(), 4, 11));

            for(int i = 0; i <= 3; i++)
            	sheet.setColumnWidth(i, (between(i, 1, 2) ? 2500 : 5000));

            for(int i = 4; i <= 11; i++)
            	sheet.setColumnWidth(i, 3200);
            
            
            Locale BRAZIL = new Locale("pt","BR");
    		SimpleDateFormat formatoMesAno = new SimpleDateFormat("MMMM 'de' yyyy", BRAZIL);
            
    		boolean possuiConfigSubTitulo = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("sub_titulo_rel_fluxo") != null && ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("sub_titulo_rel_fluxo") != "";
    		String subTitulo = (possuiConfigSubTitulo ? ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("sub_titulo_rel_fluxo") : "Veículos fiscalizados por radares") + " - " + formatoMesAno.format(dataInicio);
    		Row cabecalhoTitulo = sheet.createRow(7);
            cabecalhoTitulo.getCell(0).setCellValue(subTitulo);

            for(int i = 0; i <= 11; i++)
            	cabecalhoTitulo.getCell(i).setCellStyle(ce.estiloCabecalhoTituloRelatorioComBorda);
            
            sheet.addMergedRegion(new CellRangeAddress(cabecalhoTitulo.getRowNum(), cabecalhoTitulo.getRowNum(), 0, 11));
            
            int intLinha = 9;
            
            GerarRelatorio(dataInicio, dataFim, intLinha, response, sheet, wb, userGerador, ce);
            
			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}

	
	public int GerarRelatorio(Date dataInicio, Date dataFim, int intLinha, HttpServletResponse response, Sheet sheet, SXSSFWorkbook wb, String userGerador, ColecaoEstilos ce)
			throws IOException, ConexaoException, SQLException, ModelException, ParseException
	{
	
        Row cabecalho1 = sheet.createRow((short)intLinha);
        intLinha++;
		
        Locale BRAZIL = new Locale("pt","BR");
		SimpleDateFormat formatoMesAno = new SimpleDateFormat("MMM/yyyy", BRAZIL);
		
		Integer celula = 0;
		
		int celulaInicio = celula;
		cabecalho1.getCell(celula).setCellValue("Endereço");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoCinzaClaro);
	    celula++;
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoCinzaClaro);
	    celula++;
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoCinzaClaro);
	    celula++;
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoCinzaClaro);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), celulaInicio, celula));
	    celula++;
	    
	    boolean possuiConfigNomeColuna = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("nome_coluna_faixa_rel_fluxo") != null && ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("nome_coluna_faixa_rel_fluxo") != "";
	    cabecalho1.getCell(celula).setCellValue(possuiConfigNomeColuna ? ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("nome_coluna_faixa_rel_fluxo") : "Faixa");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoCinzaClaro);
	    celula++;
	    
	    cabecalho1.getCell(celula).setCellValue("Data");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoCinzaClaro);
	    celula++;
	    
	    cabecalho1.getCell(celula).setCellValue("Moto");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoCinzaClaro);
	    celula++;
	    
	    cabecalho1.getCell(celula).setCellValue("Passeio");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoCinzaClaro);
	    celula++;
	    
	    cabecalho1.getCell(celula).setCellValue("Médio");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoCinzaClaro);
	    celula++;
	    
	    cabecalho1.getCell(celula).setCellValue("Grande");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoCinzaClaro);
	    celula++;
	    
	    cabecalho1.getCell(celula).setCellValue("Outros");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoCinzaClaro);
	    celula++;
	    
	    cabecalho1.getCell(celula).setCellValue("Total");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoCinzaClaro);
	    celula++;
	    

	    //Buscando informações para popular planilhas
	    DadosFluxoVeicular dadosFluxoVeicular = new DadosFluxoVeicular();
	    ArrayList<ItemFluxoVeicular> dadosRelatorio = dadosFluxoVeicular.RelatorioFluxoMensalPorClassificacao(dataInicio, dataFim);
		
	    int intLinhaRetorno = intLinha;
	    
	    if (dadosRelatorio.size() == 0)
	    {
        	Row linhaSemInfo = sheet.createRow((short)intLinha);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        }
	    else
	    {
        	for (int i = 0; i < dadosRelatorio.size(); i++)
        	{
        		celula = 0;
        		celulaInicio = celula;
        		
            	Row linhaSheet = sheet.createRow((short)(i+intLinha));
            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getEnderecoPistaSentidoFaixa());
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensEsquerda);
            	celula++;
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensEsquerda);
            	celula++;
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensEsquerda);
            	celula++;
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensEsquerda);
            	sheet.addMergedRegion(new CellRangeAddress(linhaSheet.getRowNum(), linhaSheet.getRowNum(), celulaInicio, celula));
            	celula++;
            	
            	linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getCodigoGrinDER());
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
            	celula++;
            	
            	linhaSheet.getCell(celula).setCellValue(formatoMesAno.format(dadosRelatorio.get(i).getDtData()));
            	linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
            	celula++;
            	
        		if (dadosRelatorio.get(i).getFluxoVeicularMoto() != null && dadosRelatorio.get(i).getFluxoVeicularMoto() > 0) {
    				linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicularMoto());
        		} else {
        			linhaSheet.getCell(celula).setCellValue(0);
        		}
        		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
        		celula++;
        		
        		if (dadosRelatorio.get(i).getFluxoVeicularPequeno() != null && dadosRelatorio.get(i).getFluxoVeicularPequeno() > 0) {
    				linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicularPequeno());
        		} else {
        			linhaSheet.getCell(celula).setCellValue(0);
        		} 
        		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
        		celula++;
        		
        		if (dadosRelatorio.get(i).getFluxoVeicularMedio() != null && dadosRelatorio.get(i).getFluxoVeicularMedio() > 0) {
    				linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicularMedio());
        		} else {
        			linhaSheet.getCell(celula).setCellValue(0);
        		} 
        		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
        		celula++;
        		
        		if (dadosRelatorio.get(i).getFluxoVeicularGrande() != null && dadosRelatorio.get(i).getFluxoVeicularGrande() > 0) {
    				linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicularGrande());
        		} else {
        			linhaSheet.getCell(celula).setCellValue(0);
        		}
        		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
        		celula++;
        		
        		if (dadosRelatorio.get(i).getFluxoVeicularOutros() != null && dadosRelatorio.get(i).getFluxoVeicularOutros() > 0) {
    				linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicularOutros());
        		} else {
        			linhaSheet.getCell(celula).setCellValue(0);
        		} 
        		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
        		celula++;
        		
        		if (dadosRelatorio.get(i).getFluxoVeicularTotal() != null && dadosRelatorio.get(i).getFluxoVeicularTotal() > 0) {
    				linhaSheet.getCell(celula).setCellValue(dadosRelatorio.get(i).getFluxoVeicularTotal());
        		} else {
        			linhaSheet.getCell(celula).setCellValue(0);
        		} 
        		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensCentralizado);
        		celula++;

        		intLinhaRetorno++;
        	}
        }

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
	
	private boolean between (int valor, int limiteInferior, int limiteSuperior)
	{
		if (valor >= limiteInferior && valor <= limiteSuperior)
			return true;
		else
			return false;
	}
}
