package com.consilux.servlet.ajax;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.model.Acesso;
import com.consilux.model.Inconsistencia;
import com.consilux.model.InfracaoSimplificada;

/**
 * Servlet implementation class Inconsistencias
 */
public class Inconsistencias extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(Inconsistencias.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Inconsistencias() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (!new Acesso(request, response, false).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		String sIdInfracao = request.getParameter("id_infracao");
		
		if (sIdInfracao == null || !Pattern.matches("[1-9][0-9]{0,8}",sIdInfracao))
			throw new ServletException("Identificador da Infração enviado invalido!");

		try {
			InfracaoSimplificada infracao = InfracaoSimplificada.buscaInfracaoPorId(Integer.parseInt(sIdInfracao));
			List<Inconsistencia> inconsistencias = Inconsistencia.buscaTodasInconsistenciaPorEnquadramento(infracao.getIdEnquadramento());
			
			AjaxXMLConstr xml = new AjaxXMLConstr("inconsistencias");
			for(Inconsistencia i : inconsistencias) {
				xml.adicCampo("inconsistencia_" + i.getIdInconsistencia(), i.getDescricao());
			}
			xml.dump(response);
			
		}
		catch(Exception err) {
			logger.error("Erro ao montar o XML.",err);
			throw new ServletException("Erro ao montar o XML: "+err.getMessage());
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
