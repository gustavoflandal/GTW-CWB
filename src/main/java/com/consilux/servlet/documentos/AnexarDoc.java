package com.consilux.servlet.documentos;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.infra.UpArq;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.MensagemJS;
import com.consilux.model.documento.Documento;

/**
 * Servlet implementation class AnexarDoc
 */
public class AnexarDoc extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AnexarDoc.class);  
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AnexarDoc() {
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

		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/templates/documentos/anexar_doc.jsp");
		rd.forward(request, response);
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return; //O usuário não tem acesso...então cai fora!
		}
		
		UpArq ua = null;
		try {
			ua = new UpArq(request);
		} 
		catch (FileUploadException e) {
			logger.error("Erro a analizar request de arquivos.", e);
			return;
		}
		
		Map<String, String> campos = ua.getCampos();

		String sClassificador = campos.get("classificador");
		String sIdentificador = campos.get("identificador");
		
		if (sClassificador == null || !ExpValida.NATURAL.validar(sClassificador)) {
			new MensagemJS(response).showErro("Classificador enviado inválido!");
			return;
		}
		if (sIdentificador == null || !ExpValida.NATURAL.validar(sIdentificador)) {
			new MensagemJS(response).showErro("Identificador enviado inválido!");
			return;
		}
        
        if (ua.getArqs().size() != 1) {
			new Mensagem(response).showErro("Arquivo não enviado corretamente.");
			return;
        }
        
        FileItem fi = ((List<FileItem>) ua.getArqs()).get(0);
        
        Documento doc = null;
		try {
	        if (Documento.verificaExistencia(fi)) {
				new Mensagem(response).showErro("Arquivo já existe.");
				return;
	        }
			doc = Documento.adicionar(sIdentificador, Integer.valueOf(sClassificador), acesso.getUsuario().getId(), fi);
		}
		catch (Exception e) {
			logger.error("Erro ao adicionar o documento no BD.", e);
			new Mensagem(response).showErro("Erro ao adicionar o documento no BD: "+e.getMessage(), "javascript:window.close();");
		}
        
        if (doc != null) {
			new Mensagem(response).showErro("Documento adicionado com sucesso.", "javascript:window.close();");
        }
	}
}
