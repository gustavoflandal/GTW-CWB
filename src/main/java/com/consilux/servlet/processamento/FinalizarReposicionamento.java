package com.consilux.servlet.processamento;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.model.Acesso;
import com.consilux.model.ReposicionamentoInfracao;

/**
 * Servlet implementation class FinalizarReposicionamento
 */
public class FinalizarReposicionamento extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FinalizarReposicionamento() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (!new Acesso(request, response, true).verificaAcesso(false))
			return;
		
		ReposicionamentoInfracao reposicionamento = (ReposicionamentoInfracao) request.getSession().getAttribute("[reposicionamento]");
		
		if (reposicionamento == null)
			response.sendRedirect("/login/gtw_principal.jsp");

		try {
			reposicionamento.finalizaReposicionamento();
		} 
		catch (Exception err) {
			throw new ServletException("Erro ao finalizar o processamento: "+err.getMessage());
		}
			
		request.getSession().removeAttribute("[reposicionamento]");
		
		response.sendRedirect("/processo/listar_infracao_reposiciona.jsp");
		
	}

}
