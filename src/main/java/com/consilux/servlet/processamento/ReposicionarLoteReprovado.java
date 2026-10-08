package com.consilux.servlet.processamento;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.LoteReprovado;
import com.consilux.model.Mensagem;
import com.consilux.model.Remessa;

/**
 * Servlet implementation class ReposicionarLoteReprovado
 */
public class ReposicionarLoteReprovado extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ReposicionarLoteReprovado.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReposicionarLoteReprovado() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Acesso acesso = new Acesso(request, response, true);
		
		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!");
			return; // O usuário não tem acesso...então cai fora!
		}

		logger.info(acesso.getUsuario().getNome());
		
		StringBuffer requestURL = request.getRequestURL();
		
		if (request.getQueryString() != null) {
		    requestURL.append("?").append(request.getQueryString());
		}
		
		String completeURL = requestURL.toString();
		logger.info(completeURL);
		
		//String mov_reposicionar = request.getParameter("mov_reposicionar");

		String[] movimentos = request.getParameterValues("sel_movimento");

		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		response.getWriter().write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		response.getWriter().write("<link rel=\"stylesheet\" href=\"/css/gtw.css\" media=\"screen\" type=\"text/css\">");
		response.getWriter().write("<div class=\"valor_campo\" style=\"text-align: center;\" >");
		
		response.getWriter().write("<script type=\"text/javascript\"> ");
		response.getWriter().write("	window.onunload = function(){ ");
		response.getWriter().write("		window.opener.location.reload(); ");
		response.getWriter().write("	}; ");
		response.getWriter().write("</script> ");	
		
		logger.info("Resultado do reposicionamento de lotes");
		response.getWriter().write("<h3>Resultado do reposicionamento de lotes</h3><BR/>");
		
		response.getWriter().write("<div class=\"valor_campo\" style=\"text-align: left; width: 600; margin: auto;\" >");
		
		if(movimentos == null || movimentos.length == 0) {
			logger.error("Nenhum lote foi selecionado!");
			response.getWriter().write("Nenhum lote foi selecionado!<BR/>");
		}else { 
			
			try{
				Boolean resultado = false;
				
				for (String movimento : movimentos) {
					
					Integer idRemessa = Integer.parseInt(movimento);
					Remessa remessa = Remessa.buscarRemessaPorId(idRemessa);
					
					String descLote = remessa.getTipo() + ". " + remessa.getCodigoExterno().toString();
					
					response.getWriter().write("<BR/>");
					logger.info(descLote + " : Reposicionando lote");
					response.getWriter().write(descLote + " : Reposicionando lote!<BR/>");
					
					resultado = LoteReprovado.reposicionaLoteReprovado(idRemessa);
					
					if (resultado) {
						logger.info(descLote + " : Reposicionamento do lote finalizado com sucesso!");
						response.getWriter().write(descLote + " : Reposicionamento do lote finalizado com sucesso!<BR/>");
					}else if (!resultado) {
						logger.info(descLote + " : Não foi possível reposicionar o lote!");
						response.getWriter().write(descLote + " : Não foi possível reposicionar o lote!<BR/>");
					}
					
				}
				
			} catch (Exception e) {
				logger.error("ERRO ao reposicionar lote!", e);
				response.getWriter().write("Erro ao reposicionar lote!<BR/>");
			}
		}
		response.getWriter().write("</div>");
		
		response.getWriter().write("<BR/>");
		response.getWriter().write("<button onclick=\"window.close()\">Retornar a tela de seleção</button>");
		response.getWriter().write("</div>");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
