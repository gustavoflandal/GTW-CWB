package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Filtro;
import com.consilux.model.Mensagem;

/**
 * Servlet implementation class CadastrarFiltroServlet
 */
public class DesabilitaFiltroServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DesabilitaFiltroServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    private final String ERRO_CONNECTION = "Erro na conexão.";
 private String enviar_erro = null;
    private final String SUCESSO = "Filtro desabilitado com sucesso.";
    
    private Logger logger = Logger.getLogger(DesabilitaFiltroServlet.class);
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		
		Boolean resultado = null;
		
		String sIdFiltro = request.getParameter("id_filtro");
				
		Integer id_filtro = null;
		if (null != sIdFiltro && !"".equalsIgnoreCase(sIdFiltro.trim())){
			id_filtro = Integer.parseInt(sIdFiltro);
		}
			Filtro f = new Filtro();

			try {
				resultado = f.desabilitarFiltro(id_filtro);
			} catch (ConexaoException e1) {
				enviar_erro = ERRO_CONNECTION;
			}

		
		String goUrl = "javascript: history.back()";;
		if (enviar_erro != null && resultado != true){
			try {
				goUrl = URLEncoder.encode(goUrl, "UTF-8");
				enviar_erro = URLEncoder.encode(enviar_erro, "UTF-8");
				response.sendRedirect("/includes/erro.jsp?m="+enviar_erro+"&p="+goUrl);
			}catch(Exception e){
				logger.error(e.getMessage());
			}
		}else{

			new Mensagem(response).showSucesso(SUCESSO, "/ferramenta/visualiza_filtros.jsp");
		}

	}

}
