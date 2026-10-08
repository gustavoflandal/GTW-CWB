package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;

/**
 * Servlet para a geração de um relatório de Válidas X Enquadramento X Faixa
 * @author Raoni Meira Gabriel - Consilux Tecnologia
 */
public class RelatorioSegurancaTransito extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	public static final String DIR_RELATORIOS = "/WEB-INF/relatorio/";
	private static final Logger logger = Logger.getLogger(RelatorioSegurancaTransito.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		final Acesso acessoUsuario = new Acesso(request, response, true);
		
		if (!acessoUsuario.verificaAcesso())
		{
			return; //O usuário não tem acesso...então cai fora!
		}
		
		// Valida o(s) parâmetros.
		final String sMes = request.getParameter("mes");
	    if (sMes != null && !Pattern.matches("([1-9]|1[0-2])", sMes))
	    {
	        new Mensagem(response).showErro("Mês enviado inválido!", "javascript:window.close();");
	        return;
	    }

		final String sAno = request.getParameter("ano");
	    if (sAno != null && !Pattern.matches("([12][0-9]{3})", sAno))
	    {
	        new Mensagem(response).showErro("Ano enviado inválido!", "javascript:window.close();");
	        return;
	    }
		
	    final String sDiario = request.getParameter("diario"); 
	    if (sDiario != null && !Pattern.matches("on|off|ON|OFF", sDiario))
	    {
	        new Mensagem(response).showErro("Detalhamento enviado inválido!", "javascript:window.close();");
	        return;
	    }	    
	    
	    // Monta o Jasper
	    try {
	    
	    	// Recupera os parâmetros.
	    	int ano = Integer.parseInt(sAno);
	    	int mes = Integer.parseInt(sMes);
	    	boolean diario = "on".equalsIgnoreCase(sDiario);
	    	
			String identificacaoCliente = ConfiguracaoProvider.getInstance().getIdentificacaoCliente();
			
			TipoMime tipoArquivo = TipoMime.PDF;
			
			final String sArquivoJasper = DIR_RELATORIOS + "RelatorioSegurancaTransito"+(diario ? "Diario" : "") + ".jasper";
			
			final String sArquivoSaida = String.format("RelatorioSegurancaTransito-%1$02d-%2$04d%3$s."+tipoArquivo.getExtensao(), mes, ano, diario ? "D" : "");			 
			
			final ServletContext servletContext = getServletContext();
			final String arquivoLayout = servletContext.getRealPath(sArquivoJasper);
			
			if (arquivoLayout == null || arquivoLayout.length() == 0)
				raiseAndLogError("Não foi possível determinar o layout deste relatório.", null, response);
				
			final RelatorioVisual relatorio = new RelatorioVisual(arquivoLayout);
			
	    	relatorio.adicParametro("IDENTIFICACAO_CLIENTE", identificacaoCliente);
	    	relatorio.adicParametro("ANO", ano);
	    	relatorio.adicParametro("MES", mes);
	    	
			relatorio.preencheRelatorio();
			response.setContentType(tipoArquivo.getTipo());
			response.setHeader("Content-Disposition", "inline; filename=\"" + sArquivoSaida + "\"");
			
			relatorio.exportReportToPdfStream(response.getOutputStream());				
		    
		} catch (NumberFormatException pe) {
			raiseAndLogError("Erro ao converter parâmetro: ano/mês inválido.", pe, response);
		} catch (JRException jse) {
			raiseAndLogError("Erro ao gerar o relatório visual.", jse, response);
		} catch (ConfiguracaoException ce) {
			raiseAndLogError("Erro ao obter o nome do contrato.", ce, response);
		} catch (ConexaoException coe) {
			raiseAndLogError("Erro ao estabelecer conexão com o banco de dados.", coe, response);
		}
	}
	
	private void raiseAndLogError(String message, Throwable rootCause, HttpServletResponse response) throws IOException {
		logger.error(message, rootCause);
		new Mensagem(response).showErro(message, "javascript:window.close();");
	}
	
	
}
