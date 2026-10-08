	
package com.consilux.servlet.relatorio;

import java.io.IOException;

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
 * Servlet para a geração de um relatório de próximas aferições
 * @author Raoni Meira Gabriel - Consilux Tecnologia
 */
public class RelatorioVencimentoAfericoes extends HttpServlet {

	private static final long serialVersionUID = -8736811154974199946L;
	
	public static final String DIR_RELATORIOS = "/WEB-INF/relatorio/";
	private static final Logger logger = Logger.getLogger(RelatorioVencimentoAfericoes.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		final Acesso acessoUsuario = new Acesso(request, response, true);
		
		if (!acessoUsuario.verificaAcesso())
		{
			return; //O usuário não tem acesso...então cai fora!
		}
		
		// Não é necessário validar parâmetros, pois neste momento não existe nenhum.
		// O relatório será baseado no(s) loca(is) vigente(s).
	    
	    // Monta o Jasper
	    try {
	    
			String nomeContrato = ConfiguracaoProvider.getInstance().getIdentificacaoCliente();
			
			// Pode vir dos parâmetros (se for o caso). No momento, só existe a necessidade de gerar em PDF.
			TipoMime tipoArquivo = TipoMime.PDF;
			
			final String sArquivoJasper = DIR_RELATORIOS + "RelatorioVencimentoAfericoes.jasper";
			final String sArquivoSaida = String.format("RelatorioVencimentoAfericoes%1$s",  tipoArquivo.getExtensao());			 
			
			final ServletContext servletContext = getServletContext();
			final RelatorioVisual relatorio = new RelatorioVisual(servletContext.getRealPath(sArquivoJasper));
			
			String codigoSecundario = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("codigo_secundario");
			if (codigoSecundario == null)
				codigoSecundario = "";
			
	    	relatorio.adicParametro("CONTRATO", nomeContrato);
	    	relatorio.adicParametro("CODIGO_SEGUNDARIO", codigoSecundario);
	    	
			relatorio.preencheRelatorio();
			response.setContentType(tipoArquivo.getTipo());
			response.setHeader("Content-Disposition", "inline; filename=\"" + sArquivoSaida + "\"");
			
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
