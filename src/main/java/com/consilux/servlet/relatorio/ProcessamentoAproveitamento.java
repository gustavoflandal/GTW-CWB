package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.Logger;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.MensagemJS;
import com.consilux.model.TipoMime;

/**
 * Servlet responsável por montar o relatório de aproveitamento do processamento.
 * @author raoni
 */
public class ProcessamentoAproveitamento extends HttpServlet {

	public static final String DIR_RELATORIOS = "/WEB-INF/relatorio/";
	private static final long serialVersionUID = 8356431304980137846L;
	private static final Logger logger = Logger.getLogger(ProcessamentoAproveitamento.class);
	
	private void raiseAndLogError(String message, Throwable rootCause, HttpServletResponse response) throws IOException {
		logger.error(message, rootCause);
		new MensagemJS(response).showErro(message);
	}	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		final Acesso acessoUsuario = new Acesso(request, response, true);
		
		if (!acessoUsuario.verificaAcesso())
		{
			return; //O usuário não tem acesso...então cai fora!
		}
		
		// Valida o(s) parâmetros.
		final String sDataIni = request.getParameter("data_ini");
	    if (sDataIni != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataIni))
	    {
	        new MensagemJS(response).showErro("Data inicial enviada inválida!");
	        return;
	    }

		final String sDataFim = request.getParameter("data_fim");
	    if (sDataFim != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataFim))
	    {
	        new MensagemJS(response).showErro("Data final enviada inválida!");
	        return;
	    }	    
	    
		try {

			final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
			
			final Configuracao conf = ConfiguracaoProvider.getInstance();
			
			// Realiza o parsing.
			final Timestamp dtIni = new Timestamp(dateFormat.parse(sDataIni + " 00:00:00.000").getTime());
			final Timestamp dtFim = new Timestamp(dateFormat.parse(sDataFim + " 23:59:59.997").getTime());
			
			final String sArquivoJasper = DIR_RELATORIOS + "ProcessamentoAproveitamento.jasper";
			
			// Este parâmetro poderia vir da request (se for o caso). No momento, só existe
			// a necessidade de gerar em PDF.
			TipoMime tipoArquivo = TipoMime.PDF;
			String sArquivoSaida = "RelatorioAproveitamentoProcessamento.xls";
			
			// Para qualquer formato que não seja XLS, então força a ser um PDF.
			if (tipoArquivo != TipoMime.XLS)
			{
				tipoArquivo = TipoMime.PDF;
				sArquivoSaida = "RelatorioAproveitamentoProcessamento.pdf";	
			}			
			response.setContentType(tipoArquivo.getTipo());
			response.setHeader("Content-Disposition", "inline; filename=\"" + sArquivoSaida + "\"");
			
			final ServletContext servletContext = getServletContext();
			final RelatorioVisual relatorio = new RelatorioVisual(servletContext.getRealPath(sArquivoJasper));
			
	    	relatorio.adicParametro("DATA_INICIAL", dtIni);
	    	relatorio.adicParametro("DATA_FINAL", dtFim);
	    	relatorio.adicParametro("CONTRATO", conf.getConfiguracaoChaveValor().get("identificacao_cliente"));
	    	relatorio.preencheRelatorio();
	    	
			if (tipoArquivo == TipoMime.XLS)
			{
				relatorio.exportReportToXlsStream(response.getOutputStream());
			}
			else
			{
				relatorio.exportReportToPdfStream(response.getOutputStream());
			}
			
		} catch (JRException jse) {
			raiseAndLogError("Erro ao gerar o relatório visual.", jse, response);
		} catch (ConexaoException coe) {
			raiseAndLogError("Erro ao estabelecer conexão com o banco de dados.", coe, response);
		} catch (ParseException pe) {
			raiseAndLogError("Erro ao converter parâmetro: data inválida.", pe, response);
		}
		
	}
}
