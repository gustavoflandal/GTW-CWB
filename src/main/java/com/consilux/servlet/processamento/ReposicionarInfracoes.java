package com.consilux.servlet.processamento;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.ReposicionamentoInfracao;

/**
 * Servlet implementation class ReposicionarInfracoes
 */
public class ReposicionarInfracoes extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReposicionarInfracoes() {
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

		String sIdProcesso = request.getParameter("id_processo") != null ? request.getParameter("id_processo").trim() : null;
		sIdProcesso = sIdProcesso != null && sIdProcesso.length() == 0 ? null : sIdProcesso;

		if (sIdProcesso != null && !ExpValida.NATURAL.validar(sIdProcesso)) {
		    new Mensagem(response).showErro("Identificador de processo enviado inválido!");
		    return;
		}
		
		Integer idProcesso = Integer.valueOf( sIdProcesso );
		
		try {
			
			if (reposicionamento.getInfracoesSelecionadas().size() == 0){
			    new Mensagem(response).showErro("Não existem imagens a serem reposicionadas!");
			    return;
			}
				
			
			reposicionamento.reposicioarListaInfracoes( idProcesso );
			reposicionamento.finalizaReposicionamento();
			
			new Mensagem(response).showSucesso("Infrações reposicionadas com sucesso!", "/processo/FinalizarReposicionamento");
		} 
		catch (Exception err) {
			err.printStackTrace();
		    new Mensagem(response).showErro("Erro ao reposicionar infração: "+err.getMessage());
		}
			
	}

}
