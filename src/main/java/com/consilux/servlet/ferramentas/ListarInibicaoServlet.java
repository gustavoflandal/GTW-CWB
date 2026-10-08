package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Inibicao;
import com.consilux.model.Mensagem;

/**
 * Servlet implementation class ListarInibicaoServlet
 */
public class ListarInibicaoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ListarInibicaoServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListarInibicaoServlet() {
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

		String sMostrarInativos = request.getParameter("mostrar_inativos");
		
		if (sMostrarInativos != null && !ExpValida.TEXTO.validar(sMostrarInativos,1)) {
			new Mensagem(response).showErro("Campo de filtro enviado inválido!");
			return;
		}
		
		Map<String,Object> mFiltro = new HashMap<String, Object>();
		if (sMostrarInativos == null) {
			mFiltro.put("somente_ativos", null);
		}
		
		List<Inibicao> lista = null;
		try {
			lista = Inibicao.buscaInibicaoPor(mFiltro);
		}
		catch (Exception e) {
			new Mensagem(response).showErro("Erro ao listar os dados: "+e.getMessage());
			logger.error("Erro ao listar os dados.", e);
			return;
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/templates/inibicao/listar_inibicao.jsp");
	    request.setAttribute("lista", lista);
		rd.forward(request, response);	
		
	}

}
