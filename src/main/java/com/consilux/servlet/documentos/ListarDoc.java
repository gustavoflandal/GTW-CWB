package com.consilux.servlet.documentos;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.MensagemJS;
import com.consilux.model.documento.Documento;

/**
 * Servlet implementation class AnexarDoc
 */
public class ListarDoc extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ListarDoc.class);  
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListarDoc() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return; //O usuário não tem acesso...então cai fora!
		}
		String sClassificador = request.getParameter("classificador");
		String sIdentificador = request.getParameter("identificador");
		
		if (sClassificador == null || !ExpValida.NATURAL.validar(sClassificador)) {
			new MensagemJS(response).showErro("Classificador enviado inválido!");
			return;
		}
		if (sIdentificador == null || !ExpValida.NATURAL.validar(sIdentificador)) {
			new MensagemJS(response).showErro("Identificador enviado inválido!");
			return;
		}
		
		List<Documento> documentos;
		try {
			documentos = Documento.buscarDocumentoPorIdentificadorClassificador(sIdentificador, Integer.valueOf(sClassificador));
		}
		catch (Exception e) {
			logger.error("Erro ao listar o documento no BD.", e);
			new Mensagem(response).showErro("Erro ao listar o documento no BD: "+e.getMessage(), "javascript:window.close();");
			return;
		}

		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/templates/documentos/listar_doc.jsp");
		request.setAttribute("identificador", sIdentificador);
		request.setAttribute("classificador", sClassificador);
		request.setAttribute("documentos", documentos);
		rd.forward(request, response);
		return;
	}
}
