package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.ExpValida;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.MensagemJS;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.TipoMime;
import com.consilux.model.relatorio.RelatorioImagensTeste;

/**
 * Servlet implementation class RelatorioImagensTeste
 */
public class RelatorioImagensTesteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	public static final String DIR_RELATORIOS = "/WEB-INF/relatorio/";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";
	private static final Logger logger = Logger.getLogger(RelatorioImagensTesteServlet.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RelatorioImagensTesteServlet() {
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
		String sDataIni = request.getParameter("dataini");
		String sDataFim = request.getParameter("datafim");

		if (sDataIni != null && !ExpValida.DATA.validar(sDataIni)) {
	        new MensagemJS(response).showErro("Data inicial enviada inválida!");
	        return;
	    }
		if (sDataFim != null && !ExpValida.DATA.validar(sDataFim)) {
	        new MensagemJS(response).showErro("Data final enviada inválida!");
	        return;
	    }

		EtapaProcesso etapa;
		Date dtIni;
		Date dtFim;
				
		try {
			etapa = EtapaProcesso.IMAGENS_TESTE;
			dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataIni + " 00:00:00");
			dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataFim + " 23:59:59");
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		RelatorioImagensTeste rit = null;
		try {
			rit = RelatorioImagensTeste.buscaRelatorioImagensTeste(etapa.getId(), dtIni, dtFim);
		} 
		catch (Exception e) {
			throw new ServletException("Não foi possível buscas os dados no BD: "+e.getMessage(), e);
		}
		
	    
	    Configuracao conf = ConfiguracaoProvider.getInstance();

		// Monta o Jasper
	    final ServletContext servletContext = getServletContext();
		final String sArquivoJasper = DIR_RELATORIOS + "RelatorioImagensTeste.jasper";
		final RelatorioVisual relatorio = new RelatorioVisual(servletContext.getRealPath(sArquivoJasper));
		
		Date dataRelatorio = new Date();
		relatorio.adicParametro("LOGO_EMPRESA", conf.getLogoEmpresa());
		relatorio.adicParametro("DIR_IMAGENS", getServletContext().getRealPath(DIR_IMAGENS) + System.getProperty("file.separator"));
		relatorio.adicParametro("DATA_INICIO", dtIni);
		relatorio.adicParametro("DATA_FIM", dtFim);
		relatorio.adicParametro("DATA_RELATORIO", new Timestamp(dataRelatorio.getTime()));
		relatorio.adicParametro("ID_PROCESSO", etapa.getId());
		relatorio.adicParametro("VERIFICADOR", acessoUsuario.getUsuario().getNome());
		relatorio.adicParametro("TOTAL_IMAGENS", rit.getTotalImagens());
		relatorio.adicParametro("TOTAL_IMAGENS_VERIFICADAS", rit.getTotalImagensVerificadas());
		relatorio.adicParametro("TOTAL_PISTAS", rit.getTotalPistas());
		relatorio.adicParametro("PERC_PISTAS_COMPLETAS", rit.getPercPistasCompletas());
		relatorio.adicParametro("PERC_PISTAS_VERIFICADAS", rit.getPercPistasVerificadas());

		try {
			relatorio.preencheRelatorio();
			response.setContentType(TipoMime.PDF.getTipo());
			response.setHeader("Content-Disposition", "inline; filename=\"RelatorioImagensTeste.pdf\"");
		    relatorio.exportReportToPdfStream(response.getOutputStream());
		} 
		catch (Exception e) {
			String msgErro = "Erro ao gerar o relatorio de imagens teste."; 
			logger.error(msgErro, e);
			new ServletException(msgErro + " " + e.getMessage());
		}
	}

}
