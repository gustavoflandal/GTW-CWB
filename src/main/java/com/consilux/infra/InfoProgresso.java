package com.consilux.infra;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;

/**
 * Servlet implementation class InfoProgresso
 */
public class InfoProgresso extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(InfoProgresso.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InfoProgresso() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, false).verificaAcesso(false))
			return;

		String sIdProgresso = request.getParameter("id_progresso");
		
		if (sIdProgresso == null || !ExpValida.INTEIRO.validar(sIdProgresso))
			throw new ServletException("Identificador da progresso enviado invalido!");

		Progresso p = ProgressoProvider.getInstance().getProgresso(Integer.valueOf(sIdProgresso));

		if (p != null)
			enviaProgresso(response, p);
	}
	
	private void enviaProgresso(HttpServletResponse response, Progresso p) {
		AjaxXMLConstr xml = null;
		try {
			xml = new AjaxXMLConstr("progresso");
			xml.adicCampo("PROGRESSO", String.valueOf(p.getProgresso()));
			xml.adicCampo("STATUS", p.getStatus());
			xml.adicCampo("TERMINADO", String.valueOf(p.getTerminado()));
			xml.dump(response);	
		}
		catch (Exception e) {
			logger.warn("Não foi possível entragar o progresso ao cliente.", e);
		}
		
	}

}
