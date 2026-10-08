package com.consilux.servlet.ferramentas;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Inibicao;
import com.consilux.model.exception.ModelException;

/**
 * Servlet implementation class DesativarInibicaoServlet
 */
public class DesativarInibicaoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(DesativarInibicaoServlet.class);

	/**
     * @see HttpServlet#HttpServlet()
     */
    public DesativarInibicaoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!

		String sIdInibicaoInfracao = request.getParameter("id_inibicao_infracao");
		
		if (sIdInibicaoInfracao == null || !ExpValida.NATURAL.validar(sIdInibicaoInfracao)) {
			throw new ServletException("Identificador de inibição enviado inválido!");
		}
		
		Inibicao inibicao = null;
		try {
			inibicao = Inibicao.buscaInibicaoPorId(Integer.valueOf(sIdInibicaoInfracao));
		}
		catch (Exception e) {
			logger.error("Não foi possível encontrar a inibição no BD.", e);
			throw new ServletException("Não foi possível encontrar a inibição no BD: "+e.getMessage());
		}

		try {
			if (!inibicao.desativarInibicao(acesso.getUsuario().getId()))
				throw new ModelException("Não foi possível desativar a inibição");
		}
		catch (Exception e) {
			logger.error("Não foi possível desativar a inibição.", e);
			throw new ServletException("Não foi possível desativar a inibição: "+e.getMessage());
		}
	}

}
