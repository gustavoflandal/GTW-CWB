package com.consilux.servlet.documentos;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.MensagemJS;
import com.consilux.model.TipoMime;
import com.consilux.model.documento.Documento;

/**
 * Servlet implementation class AnexarDoc
 */
public class BaixarDoc extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(BaixarDoc.class);  
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BaixarDoc() {
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
		String sIdDocumento = request.getParameter("id_documento");
		
		if (sIdDocumento == null || !ExpValida.NATURAL.validar(sIdDocumento)) {
			new MensagemJS(response).showErro("Identificador de documento enviado inválido!");
			return;
		}
		
		Documento documento;
		try {
			documento = Documento.buscarDocumentoPorId(Integer.valueOf(sIdDocumento));
		}
		catch (Exception e) {
			logger.error("Erro ao buscar o documento no BD.", e);
			new Mensagem(response).showErro("Erro ao buscar o documento no BD: "+e.getMessage(), "javascript:window.close();");
			return;
		}

		byte[] blobDoc;
		try {
			blobDoc = Documento.getDocumentoBD(Integer.valueOf(sIdDocumento));
		}
		catch (Exception e) {
			logger.error("Erro ao buscar o documento no BD.", e);
			new Mensagem(response).showErro("Erro ao buscar o documento no BD: "+e.getMessage(), "javascript:window.close();");
			return;
		}

		response.setContentType(TipoMime.BIN.getTipo());
		response.setHeader("Content-Disposition","inline; filename=\""+documento.getNomeArquivo()+"\"");

		//Passando o content length agora que sabe o tamanho do blob. 
		response.setContentLength(blobDoc.length);
		try {
			response.getOutputStream().write(blobDoc, 0, blobDoc.length);
			response.flushBuffer();
		}
		catch (Exception ex) {
			logger.warn("Erro de IO!", ex);
		}
		return;
	}
}
