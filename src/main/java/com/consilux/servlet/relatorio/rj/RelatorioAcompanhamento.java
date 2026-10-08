package com.consilux.servlet.relatorio.rj;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.ExpValida;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.relatorio.rj.DadosRelatorioMedicao;
import com.consilux.model.relatorio.rj.ItemRelatorioMedicao;

import muralha.digital.relatorios.RelatorioValidacao;
import muralha.digital.util.RespostaRequisicaoXML;

/**
 * Servlet implementation class PlanilhaAcompanhamento
 */
@WebServlet("/relatorio/RelatorioAcompanhamento")
public class RelatorioAcompanhamento extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RelatorioAcompanhamento.class);
	private static final String objeto = "Fiscalização e registro de infrações e fluxo veicular";
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		if (!new Acesso(request, response, true).verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!
    	
		try
		{
			RelatorioValidacao relatorioValidacao = ValidarFiltros(request, response);
	        
			logger.debug(relatorioValidacao.getMensagem());	
			respostaXML.EnviarRespostaRequisicaoXML(response, relatorioValidacao.isFiltroValido(), relatorioValidacao.getMensagem());
		}
		catch (Exception e)
		{
			logger.debug("Erro ao gerar relatório: " + e.getMessage());
			respostaXML.EnviarRespostaRequisicaoXML(response, false, "Ocorreu um erro ao gerar o relatório. Tente novamente ou contate o administrador do sistema!");
		}
    }
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (!new Acesso(request, response, true).verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!
		
		try
		{
			RelatorioValidacao relatorioValidacao = ValidarFiltros(request, response);
	        
			if (!relatorioValidacao.isFiltroValido())
			{
				String msg = "Ocorreu um erro ao gerar o relatório. Tente novamente ou contate o administrador do sistema!";
				logger.debug(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			else
			{
				GerarArquivoRelatorio(response, relatorioValidacao);
			}
		}
		catch (Exception e)
		{
			logger.debug("Erro ao gerar relatório: " + e.getMessage());
			respostaXML.EnviarRespostaRequisicaoXML(response, false, "Ocorreu um erro ao gerar o relatório. Tente novamente ou contate o administrador do sistema!");
		}
	}
	
	private void GerarArquivoRelatorio(HttpServletResponse response, RelatorioValidacao relatorioValidacao)
	{
		Locale local = new Locale("pt","BR");
		
		SimpleDateFormat formatDataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm", local);
		SimpleDateFormat formatDataNomeArq = new SimpleDateFormat("ddMMyyyy", local);
		
		String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");
		String contrato = String.format("%03d", Integer.parseInt(numeroContrato)) + "/" + anoContrato;

		try
		{
			// Criando o arquivo fisico
	        String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_Relatorio de Acompanhamento_" +
	        						formatDataNomeArq.format(relatorioValidacao.getDataInicioFiltro()) + "_" +
	        						formatDataNomeArq.format(relatorioValidacao.getDataFimFiltro()) + ".xlsx";
	        
	        response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");  			
 
	        // Criando area de trabalho para o excel
			SXSSFWorkbook wb = new SXSSFWorkbook();
	        wb.setCompressTempFiles(true);
	        
		    //Cria uma planilha Excel
	        Sheet sheet = wb.createSheet("Rel-"+formatDataNomeArq.format(relatorioValidacao.getDataInicioFiltro()) + "_" + formatDataNomeArq.format(relatorioValidacao.getDataFimFiltro())); 
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
	        
	        ColecaoEstilos ce = new ColecaoEstilos(wb);
	        int celula = 0, celulaIniCabecalho = 0, celulaFimCabecalho = 5, linha = 0;
            
            //Filtros informados
            Row cabecalho1 = sheet.createRow(linha);
            cabecalho1.createCell(celula).setCellValue("RELATORIO DE ACOMPANHAMENTO DOS REGISTROS");
            cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
            sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), celulaIniCabecalho, celulaFimCabecalho));
            linha++;
            
            Row cb1 = sheet.createRow(linha);
            cb1.createCell(celula).setCellValue("Contrato nº:");
            cb1.getCell(celula).setCellStyle(ce.estiloCabecalhoFiltro);
            celula++;
            cb1.createCell(celula).setCellValue(contrato);
            cb1.getCell(celula).setCellStyle(ce.estiloFiltro);
            linha++;
            
            celula = 0;
            
            Row cb2 = sheet.createRow(linha);
            cb2.createCell(celula).setCellValue("Objeto:");
            cb2.getCell(celula).setCellStyle(ce.estiloCabecalhoFiltro);
            celula++;
            cb2.createCell(celula).setCellValue(objeto);
            cb2.getCell(celula).setCellStyle(ce.estiloFiltro);
            linha++;
            
            celula = 0;
            
            Row cb3 = sheet.createRow(linha);
            cb3.createCell(celula).setCellValue("Contratada:");
            cb3.getCell(celula).setCellStyle(ce.estiloCabecalhoFiltro);
            celula++;
            cb3.createCell(celula).setCellValue("CONSILUX");
            cb3.getCell(celula).setCellStyle(ce.estiloFiltro);
            linha++;
            
            celula = 0;
            
            Row cb4 = sheet.createRow(linha);
            cb4.createCell(celula).setCellValue("Período:");
            cb4.getCell(celula).setCellStyle(ce.estiloCabecalhoFiltro);
            celula++;
            cb4.createCell(celula).setCellValue(formatDataHora.format(relatorioValidacao.getDataInicioFiltro()) + " até " + formatDataHora.format(relatorioValidacao.getDataFimFiltro()));
            cb4.getCell(celula).setCellStyle(ce.estiloFiltro);
            linha++;
            linha++;
            linha++;
            
            
            //Gerar relatório por equipamento
            linha = GerarRelatorio(relatorioValidacao.getDataInicioFiltro(), relatorioValidacao.getDataFimFiltro(), linha, response, sheet, wb, nomeArquivo, ce, false);
            
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

	
	private int GerarRelatorio(Date dtDataIni, Date dtDataFim, int linha,
							  HttpServletResponse response, Sheet sheet, SXSSFWorkbook wb, String nomeArquivo, 
					 		  ColecaoEstilos ce, boolean porFaixa
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException
	{
    	Row cabecalho1 = sheet.createRow((short)linha++);
		Row cabecalho2 = sheet.createRow((short)linha++);		
		
		cabecalho1.setHeight((short) (cabecalho1.getHeight() * 2));
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		int celula = 0;
		
		cabecalho1.createCell(celula).setCellValue("Número do equipamento");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		cabecalho2.createCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3200);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), celula, celula));
	    celula++;
	    
	    cabecalho1.createCell(celula).setCellValue("Endereço do equipamento");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho2.createCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 28000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), celula, celula));
	    celula++;
	    
	    cabecalho1.createCell(celula).setCellValue("Data de Publicação");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho2.createCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3200);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), celula, celula));
	    celula++;
	    
    	cabecalho1.createCell(celula).setCellValue("Faixa");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho2.createCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 2000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), celula, celula));
	    celula++;
	    
	    cabecalho1.createCell(celula).setCellValue("Ocorrência");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho2.createCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 6000);
	    sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho2.getRowNum(), celula, celula));
	    

	    celula = 5;
	    int celulaInicio = celula;
	    String strData = null, coluna_ref_total_inicio = null, coluna_ref_total_fim = null;
	    Date dtData = null;
	    Calendar calendarioData = Calendar.getInstance();
	    
	    
        //Buscando informações para popular planilhas
	    logger.info("Aguardando DADOS");
	    Date dt_inicio_req = Calendar.getInstance().getTime();
	    List<ItemRelatorioMedicao> dadosRelatorio = DadosRelatorioMedicao.ObterItensRelatorioAcompanhamento(dtDataIni, dtDataFim);
	    Date dt_fim_req = Calendar.getInstance().getTime();
	    logger.info("TEMPO para obter DADOS : " + (dt_fim_req.getTime() - dt_inicio_req.getTime()) / 1000);
	    
	    
	    List<Integer> finais_de_semana = new ArrayList<Integer>();
	    
	    if (dadosRelatorio.size() > 0)
	    {
		    for (Integer i = 0; i < dadosRelatorio.get(0).getQtdeColunas(); i++)
		    {
		    	strData = dadosRelatorio.get(0).getCelulasColunasRelatorio()[i];
		    	dtData = sdf.parse(strData);
		    	calendarioData.setTime(dtData);
		    	
		    	cabecalho1.createCell(celula).setCellValue("Quantidade de registros");
		    	cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    	
		    	cabecalho2.createCell(celula).setCellValue(strData);
		    	if (calendarioData.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendarioData.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 
		    	{	
		    		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegritoVerde);
		    		finais_de_semana.add(celula);
		    	}
		    	else
		    		cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    	
		    	sheet.setColumnWidth(celula, 3000);
		    	
		    	if (coluna_ref_total_inicio == null)
		    		coluna_ref_total_inicio = CellReference.convertNumToColString(celula);
		    	
		    	coluna_ref_total_fim = CellReference.convertNumToColString(celula);
			    
			    celula++;
		    }
	    }
	    
	    cabecalho1.createCell(celula).setCellValue("Quantidade de registros");
	    cabecalho1.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
    	cabecalho2.createCell(celula).setCellValue("Total");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 3000);
	    if (celula > celulaInicio)
	    	sheet.addMergedRegion(new CellRangeAddress(cabecalho1.getRowNum(), cabecalho1.getRowNum(), celulaInicio, celula));
	    
        
	    int intLinhaInicioDados = linha, intLinhaRetorno = intLinhaInicioDados;
	    
	    
	    String strEnderecoAnterior = null;
	    Boolean novoEndereco = false;
	    Boolean ultimaLinha = false;
	    		
	    if (dadosRelatorio.size() == 0)
	    {
        	Row linhaSemInfo = sheet.createRow((short)linha);
        	linhaSemInfo.createCell(0).setCellValue("Não há dados para os filtros informados!");
        	linhaSemInfo.getCell(0).setCellStyle(ce.estiloItensLinhaSemInfo);
        }
	    else
	    {
        	for (int i = 0; i < dadosRelatorio.size(); i++)
        	{
	    		if ( (strEnderecoAnterior != null) && (!strEnderecoAnterior.equals(dadosRelatorio.get(i).getEnderecoEquipamento())) )
	    			novoEndereco = true;
	    		else
	    			novoEndereco = false;
        		
	    		ultimaLinha = (i == (dadosRelatorio.size() - 1) ? true : false);
	    		
	    		celula = 0;
	    		
        		Row linhaInfo = sheet.createRow((short)intLinhaInicioDados + i);
    			linhaInfo.createCell(celula).setCellValue(dadosRelatorio.get(i).getNumeroEquipamento());
    			linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
    			celula++;
    			linhaInfo.createCell(celula).setCellValue(dadosRelatorio.get(i).getEnderecoEquipamento());
    			linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensEsquerdaUltimaLinha : (novoEndereco ? ce.estiloItensEsquerdaPlanAcomp : ce.estiloItensEsquerdaPontilhado));
    			celula++;
        		linhaInfo.createCell(celula).setCellValue(sdf.format(dadosRelatorio.get(i).getDataPublicacao()));
    			linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
    			celula++;
        		linhaInfo.createCell(celula).setCellValue(dadosRelatorio.get(i).getFaixa());
    			linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
    			celula++;
    			linhaInfo.createCell(celula).setCellValue(dadosRelatorio.get(i).getOcorrencia());
    			linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensEsquerdaUltimaLinha : (novoEndereco ? ce.estiloItensEsquerdaPlanAcomp : ce.estiloItensEsquerdaPontilhado));
    			celula++;
    			
    			Integer valorCelula;
    			
    			for(int h = 0; h < dadosRelatorio.get(0).getQtdeColunas(); h++)
    			{
    				strData = dadosRelatorio.get(i).getCelulasColunasRelatorio()[h];
    		    	dtData = sdf.parse(strData);
			    	
			    	valorCelula = dadosRelatorio.get(i).getCelulasValores()[h];
			    	
	    			boolean publicado = !dtData.before(dadosRelatorio.get(i).getDataPublicacao());
	    			
					if (!publicado || valorCelula == null)
						linhaInfo.createCell(celula).setCellValue("");
					else if (valorCelula > 0)
						linhaInfo.createCell(celula).setCellValue(valorCelula);
					else
						linhaInfo.createCell(celula).setCellValue("");
					
					if (!publicado)
						linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoCinzaUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoCinzaPlanAcomp : ce.estiloItensCentralizadoCinzaPontilhado));
					else if (finais_de_semana.contains(celula))
						linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoVerdeUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoVerdePlanAcomp : ce.estiloItensCentralizadoVerdePontilhado));
					else 
						linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
					
					celula++;
            	}
				
    			String formulaSoma = "SUM(" + coluna_ref_total_inicio + (linhaInfo.getRowNum()+1) + ":" + coluna_ref_total_fim + (linhaInfo.getRowNum()+1) + ")";
				linhaInfo.createCell(celula).setCellFormula("IF(" + formulaSoma + " = 0, \"\", " + formulaSoma + ")"); 
				linhaInfo.getCell(celula).setCellStyle(ultimaLinha ? ce.estiloItensCentralizadoUltimaLinha : (novoEndereco ? ce.estiloItensCentralizadoPlanAcomp : ce.estiloItensCentralizadoPontilhado));
				celula++;
				
				strEnderecoAnterior = dadosRelatorio.get(i).getEnderecoEquipamento();
        	}
        }
	    
	    
	    dadosRelatorio.clear();
    	dadosRelatorio = null;
	    
		sheet = wb.getSheetAt(0);
		
		return intLinhaRetorno + 2;
	}
	
    public static RelatorioValidacao ValidarFiltros(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
    	RelatorioValidacao relatorioValidacao = new RelatorioValidacao();
    	
    	String sDataInicio = request.getParameter("dataIni") != null ? request.getParameter("dataIni").trim() : null;
    	sDataInicio = sDataInicio != null && sDataInicio.length() == 0 ? null : sDataInicio;

    	String sDataFim = request.getParameter("dataFim") != null ? request.getParameter("dataFim").trim() : null;
    	sDataFim = sDataFim != null && sDataFim.length() == 0 ? null : sDataFim;
    	
    	if (sDataInicio == null || sDataInicio.equals(""))
    	{
            relatorioValidacao.setFiltroValido(false);
    		relatorioValidacao.setMensagem("Data Inicio não informada!");
            logger.error(relatorioValidacao.getMensagem());
    		return relatorioValidacao;
        }
    	
    	sDataInicio = sDataInicio + ":00:00";
        if (sDataInicio != null && !ExpValida.DATA_HORA.validar(sDataInicio))
        {
        	relatorioValidacao.setFiltroValido(false);
    		relatorioValidacao.setMensagem("Data Inicio inválida!");
            logger.error(relatorioValidacao.getMensagem());
    		return relatorioValidacao;
        }
        
        if (sDataFim == null || sDataFim.equals(""))
        {
        	relatorioValidacao.setFiltroValido(false);
    		relatorioValidacao.setMensagem("Data Fim não informada!");
            logger.error(relatorioValidacao.getMensagem());
    		return relatorioValidacao;
        }
        
        sDataFim = sDataFim + ":59:59";
        if (sDataFim != null && !ExpValida.DATA_HORA.validar(sDataFim))
        {
        	relatorioValidacao.setFiltroValido(false);
    		relatorioValidacao.setMensagem("Data Fim inválida!");
            logger.error(relatorioValidacao.getMensagem());
    		return relatorioValidacao;
        }
        
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date dtIni = null, dtFim = null;
		
		try
		{
			dtIni = dateFormat.parse(sDataInicio);
			dtFim = dateFormat.parse(sDataFim);
			
			if (dtFim.before(dtIni))
			{
				relatorioValidacao.setFiltroValido(false);
	    		relatorioValidacao.setMensagem("A Data Inicio deve ser menor que a Data Fim!");
	            logger.error(relatorioValidacao.getMensagem());
	    		return relatorioValidacao;
			}
			
			relatorioValidacao.setDataInicioFiltro(new Timestamp(dtIni.getTime()));
			relatorioValidacao.setDataFimFiltro(new Timestamp(dtFim.getTime()));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			new ServletException("Erro ao converter data: " + e.getMessage());
		}

        relatorioValidacao.setFiltroValido(true);
        relatorioValidacao.setMensagem("Campos validados com sucesso!");
        
        return relatorioValidacao;
    }
}