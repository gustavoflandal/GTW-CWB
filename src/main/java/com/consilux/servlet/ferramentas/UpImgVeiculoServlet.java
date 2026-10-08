package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.infra.Funcoes;
import com.consilux.infra.UpArq;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.VeiculoImagem;

/**
 * Servlet implementation class UpImgVeiculoServlet
 */
public class UpImgVeiculoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(UpImgVeiculoServlet.class);  
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpImgVeiculoServlet() {
        super();
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return; //O usuário não tem acesso...então cai fora!
		}

		String sIdImagem = request.getParameter("id_imagem");

		if (sIdImagem == null || !ExpValida.NATURAL_COM_ZERO.validar(sIdImagem))
			throw new ServletException("Identificador da Imagem enviado inválido!");
		
		UpArq ua = null;
		try {
			ua = new UpArq(request);
		} 
		catch (FileUploadException e) {
			logger.error("Erro a analizar request de arquivos.", e);
			throw new ServletException("Erro a analizar request de arquivos.");
		}
        
        if (ua.getArqs().size() != 1) {
			logger.warn("Arquivo não enviado corretamente.");
			throw new ServletException("Arquivo não enviado corretamente.");
        }
        
        List<FileItem> arqs = ua.getArqs();
        
        for (FileItem fi : arqs) {
    		String md5 = Funcoes.geraMD5(fi.getInputStream());
        	/*
    		File tmp = File.createTempFile("TESTEUPARQ", ".jpg");
    		
    		System.out.println("ARQUIVO TESTE: "+tmp.getName());
    		FileOutputStream arqTmp = new FileOutputStream(tmp);
    		
    		Funcoes.copyBytes(fi.getInputStream(), arqTmp);
    		arqTmp.close();
    		*/
    		Integer idImagem = Integer.valueOf(sIdImagem);
    		
    		try {
    	        if (!VeiculoImagem.verificaMD5Imagem(idImagem, md5)) {
    				logger.warn("Imagem não encontrada no BD.");
    				throw new ServletException("Imagem não encontrada no BD.");
    	        }
				VeiculoImagem.reAdicionaImagem(idImagem, md5, fi);
			}
    		catch (Exception e) {
    			logger.error("Erro ao readicionar a imagem no BD.", e);
    			throw new ServletException("Erro ao readicionar a imagem no BD: "+e.getMessage(), e);
			}
    		
    		System.out.println("MD5: "+md5);
		}
	}

}
