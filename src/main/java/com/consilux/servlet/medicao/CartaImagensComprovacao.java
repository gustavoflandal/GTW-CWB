package com.consilux.servlet.medicao;

import java.io.IOException;

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
import com.consilux.model.medicao.ProcessoMedicao;

/**
 * Servlet implementation class CartaImagensComprovacao
 */
public class CartaImagensComprovacao extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	public static final String DIR_RELATORIOS = "/WEB-INF/relatorio/";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";
	private static final Logger logger = Logger.getLogger(CartaImagensComprovacao.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CartaImagensComprovacao() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final Acesso acessoUsuario = new Acesso(request, response, true);
		
		if (!acessoUsuario.verificaAcesso())
		{
			return; //O usuário não tem acesso...então cai fora!
		}

		// Valida o(s) parâmetros.
		final String sIdProcessoMedicao = request.getParameter("id_processo_medicao");
		
	    if (sIdProcessoMedicao == null || !ExpValida.NATURAL.validar(sIdProcessoMedicao)) {
	        new Mensagem(response).showErro("Identificador do processo enviado inválida!","javascript:window.close()");
	        return;
	    }

	    Configuracao conf;
	    
		try {
			conf = ConfiguracaoProvider.getInstance();
		}
		catch (Exception e) {
			logger.error("Erro ao carregar a configuração: "+e.getMessage(), e);
			new Mensagem(response).showErro("Erro ao carregar a configuração!","javascript:window.close()");
			return;
		}
	    
	    // Monta o Jasper
		final String sArquivoJasper = DIR_RELATORIOS + "CartaImagensComprovacao.jasper";
		final RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(sArquivoJasper));
		
		Integer idProcessoMedicao = Integer.valueOf(sIdProcessoMedicao);
		
		ProcessoMedicao pm;
		try {
			pm = ProcessoMedicao.buscarProcessoMedicaoPorId(idProcessoMedicao);
			pm.ajustaResponsavel(acessoUsuario.getUsuario().getId());
			pm.ajustaDataExportacao();
		}
		catch (Exception e) {
			logger.error("Erro ao ajustar o responsável."+e.getMessage(), e);
			new Mensagem(response).showErro("Não foi possível ajustar o responsável!","javascript:window.close()");
			return;
		}
		
		relatorio.adicParametro("ID_PROCESSO_MEDICAO", idProcessoMedicao);
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
			response.setHeader("Content-Disposition", "inline; filename=\"CartaImagensComprovacao.pdf\"");
		    relatorio.exportReportToPdfStream(response.getOutputStream());
		} 
		catch (Exception e) {
			String msgErro = "Erro ao gerar a solicitação de auditoria."; 
			logger.error(msgErro, e);
			new Mensagem(response).showErro("Erro ao carregar a configuração!");
		}
	}
}
