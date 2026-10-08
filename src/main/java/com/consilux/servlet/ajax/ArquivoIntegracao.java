package com.consilux.servlet.ajax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Servlet implementation class ArquivoIntegracao
 */
public class ArquivoIntegracao extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(ArquivoIntegracao.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ArquivoIntegracao() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		String URL = request.getParameter("URL");
		List<String> nome_arquivo = Arrays.asList(URL.split("/"));
		String nome_arquivo_str = nome_arquivo.get(nome_arquivo.size()-1);
		
		FTPClient client = new FTPClient();
		client.connect("ftp.gctnet.com.br");
		client.login("bhtranstemp", "BHtrans0204");
		client.enterLocalPassiveMode();
		
		byte[] dados = null;
		
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			client.retrieveFile(URL, baos);
			dados = baos.toByteArray();
		}
		catch(Exception e) {
			logger.error("erro ao tentar baixar arquivo", e);
		}
		
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition","filename=\""+nome_arquivo_str+"\"");
		response.getOutputStream().write(dados);
		
	}

}
