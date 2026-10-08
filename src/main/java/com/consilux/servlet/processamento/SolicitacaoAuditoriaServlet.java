package com.consilux.servlet.processamento;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoChaveValor;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.ExpValida;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;

/**
 * Servlet para a geração de uma solicitação de auditoria.
 * @author Raoni Meira Gabriel - Consilux Tecnologia
 */
public class SolicitacaoAuditoriaServlet extends HttpServlet implements Servlet {
	
	private static final long serialVersionUID = -5804552899067228543L;
	
	public static final String DIR_RELATORIOS = "/WEB-INF/relatorio/";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";
	private static final Logger logger = Logger.getLogger(SolicitacaoAuditoriaServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		final Acesso acessoUsuario = new Acesso(request, response, true);
		
		if (!acessoUsuario.verificaAcesso())
		{
			return; //O usuário não tem acesso...então cai fora!
		}

		// Valida o(s) parâmetros.
		final String sIdSolicitacaoAuditoria = request.getParameter("id_solicitacao_auditoria");
		
	    if (sIdSolicitacaoAuditoria == null || !ExpValida.NATURAL.validar(sIdSolicitacaoAuditoria)) {
	        new Mensagem(response).showErro("Identificador da solicitação enviada inválida!","javascript:window.close()");
	        return;
	    }

	    Configuracao conf;
	    
		try {
			conf = ConfiguracaoProvider.getInstance();
		}
		catch (Exception e) {
			logger.error("Erro ao carregar a configuração: "+e.getMessage(), e);
			new Mensagem(response).showErro("Erro ao carregar a configuração!");
			return;
		}
	    
	    // Monta o Jasper
		final String sArquivoJasper = DIR_RELATORIOS + "SolicitacaoAuditoria.jasper";
		final RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(sArquivoJasper));
		
		Integer idSolicitacaoAuditoria = Integer.valueOf(sIdSolicitacaoAuditoria);
		
		relatorio.adicParametro("ID_SOLICITACAO_AUDITORIA", idSolicitacaoAuditoria);
		relatorio.adicParametro("DIR_IMAGENS", getServletContext().getRealPath(DIR_IMAGENS) + System.getProperty("file.separator"));
		relatorio.adicParametro("LOGO_EMPRESA", conf.getLogoEmpresa());
		
		ConfiguracaoChaveValor chaveValor = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor();
		String destino = chaveValor.get("orgao_auditoria");
		
		if (destino == null) {
			destino = "";
		}
		relatorio.adicParametro("DESTINO", destino);
		
		String identificacao = chaveValor.get("identificacao_cliente");
		if (identificacao == null) {
			identificacao = "";
		}		
		relatorio.adicParametro("IDENTIFICACAO", identificacao);
		
		try {
			relatorio.preencheRelatorio();
			response.setContentType(TipoMime.PDF.getTipo());
			response.setHeader("Content-Disposition", "inline; filename=\"SolicitacaoAuditoria.pdf\"");
		    relatorio.exportReportToPdfStream(response.getOutputStream());
		} 
		catch (Exception e) {
			String msgErro = "Erro ao gerar a solicitação de auditoria."; 
			logger.error(msgErro, e);
			new Mensagem(response).showErro("Erro ao carregar a configuração!");
		}
	}
}
