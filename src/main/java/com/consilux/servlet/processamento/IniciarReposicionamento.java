package com.consilux.servlet.processamento;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.ReposicionamentoInfracao;
import com.consilux.model.beans.FiltroBean;
import com.consilux.servlet.ferramentas.FiltroDAO;

/**
 * Servlet implementation class IniciarReposicionamento
 */
public class IniciarReposicionamento extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(IniciarReposicionamento.class); 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IniciarReposicionamento() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não autenticado!");

		String[] sIdInfracoes = request.getParameterValues("sel_infracao");

		if (sIdInfracoes == null) { //Se o parâmetro não foi passado, consideramos que nenhuma infração foi selecionada.
			sIdInfracoes = new String[0];
		}
		
	    List<Integer> lInfracoes = new ArrayList<Integer>();
		for (String sIdInfracao: sIdInfracoes) {
			if (!Pattern.matches("[1-9][0-9]{0,8}",sIdInfracao)) {
				new Mensagem(response).showErro("Infração enviada invalida!");
				return;
			}
			else
				lInfracoes.add(Integer.valueOf(sIdInfracao));
		}

		// é obrigado ter pelo menos 1 infração na lista
		if (lInfracoes.size() == 0){		
			new Mensagem(response).showErro("Infrações não selecionadas!");
	    	return;
		}
		
		reposicionarInfracoes( lInfracoes, request, response );

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não autenticado!");

		String sIdFiltro = request.getParameter("id_filtro");
		
		if (sIdFiltro == null || !ExpValida.NATURAL.validar(sIdFiltro)) {
			new Mensagem(response).showErro("Id Filtro enviado inválida!");
			return;
		}
		
		int idFiltro = Integer.valueOf(sIdFiltro);
		
		try {
			
			FiltroDAO dao = new FiltroDAO();
			
			FiltroBean fb = dao.buscarFiltro(idFiltro);
		    
			if (fb == null){
				new Mensagem(response).showErro("Id Filtro enviado inválido!");
				return;
			}
			
			List<Integer> lInfracoes = dao.buscarInfracoesExistentesParaFiltro(fb);
		    reposicionarInfracoes(lInfracoes, request, response);
			
		}
		catch (Exception e) {
			logger.error("Erro ao iniciar o reposicionamento das infrações!", e);
			new Mensagem(response).showErro("Erro ao iniciar o reposicionamento das infrações! " + e.getMessage());
		}
		
	
	}

	private void reposicionarInfracoes( List<Integer> listaInfracoes , HttpServletRequest request, HttpServletResponse response ) throws IOException{
		
		ReposicionamentoInfracao reposicionamento = null;

		try {

			reposicionamento = new ReposicionamentoInfracao( listaInfracoes );

			// coloca na sessão para ser utilizado pela confirmação
			request.getSession().setAttribute("[reposicionamento]", reposicionamento);
			response.sendRedirect("/processo/confirma_infracao_reposicionamento.jsp");
		
		} 
		catch (Exception e) {
			logger.error("Erro ao iniciar o reposicionamento das infrações!", e);
			new Mensagem(response).showErro("Erro ao iniciar o reposicionamento das infrações! " + e.getMessage());
		}
		
	}
	
}
