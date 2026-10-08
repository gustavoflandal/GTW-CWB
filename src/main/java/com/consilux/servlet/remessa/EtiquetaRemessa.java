package com.consilux.servlet.remessa;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.MensagemJS;

/**
 * Servlet implementation class EtiquetaRemessa
 */
public class EtiquetaRemessa extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String DIR_ETIQUETA = "/WEB-INF/relatorio/";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";
	private static Logger logger = Logger.getLogger(EtiquetaRemessa.class); 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EtiquetaRemessa() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new MensagemJS(response).showErro("Usuário não atenticado!");
			return; //O usuário não tem acesso...então cai fora!
		}

		String sIdRemessa = request.getParameter("id_remessa");
		
		if (sIdRemessa == null || !Pattern.matches("[0-9]{0,9}",sIdRemessa)) {
			new Mensagem(response).showErro("Identificador da Remessa enviado invalido!","javascript:window.close();");
			return;
		}
        
		try {
			Configuracao conf = ConfiguracaoProvider.getInstance();
	        
			RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(DIR_ETIQUETA+"Etiqueta Remessa.jasper"));
			
			relatorio.adicParametro("ID_REMESSA", Integer.parseInt(sIdRemessa));
			relatorio.adicParametro("DIR_IMAGENS", getServletContext().getRealPath(DIR_IMAGENS)+System.getProperty("file.separator"));
			relatorio.adicParametro("LOGO_EMPRESA", conf.getLogoEmpresa());
			relatorio.adicParametro("LOGO_EMPRESA_VERTICAL", conf.getLogoEmpresaVertical());

			relatorio.preencheRelatorio();
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition","attachment; filename=\"Etiqueta.pdf\"");
		    relatorio.exportReportToPdfStream(response.getOutputStream());
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar a etiqueta", e);
			new ServletException("Erro ao gerar o etiqueta: "+e.getMessage(), e);
		}	}

}
