/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 28/08/2017

  Descrição: Servlet para envio da imagem de classificação QFV do veículo.

 *********************************************************************************/
package com.consilux.servlet.ajax;


import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

//import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.ferramenta.Dimensao;

/**
 * Servlet para envio da imagem de classificação QFV do veículo.
 * @author Thiago Surgik
 * @since 28/08/2017
 */
public class ImgClassificacaoQFV extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(ImgClassificacaoQFV.class); 
	private static String IMG_INDISPONIVEL = "/utils/imagesClassificacaoQFV/SemClassif.bmp";
	public static String IMAGEM_LOADING = "/images/ajax-loader.gif";
	
	/**
	 * Constrói o objeto
	 */
	public ImgClassificacaoQFV() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Acesso acesso = new Acesso(request, response, false); 
		
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");

		String sIdClassificacao = request.getParameter("id_classificacao");
		
		sIdClassificacao = sIdClassificacao.trim().equals("") ? null : sIdClassificacao;

		long inicio = System.currentTimeMillis();
		
		try {
			
			Dimensao imagemQFV = null;
					
			if (sIdClassificacao != null) {
				imagemQFV = Dimensao.buscaImagemClassificacaoPorIdClassificacao(sIdClassificacao);
			}

			byte[] blobImagem;
			response.setContentType("image/jpeg");
			
			if (imagemQFV != null) {
				
				// Imagem veio do repositorio, definir cache.
				response.setHeader("Cache-Control", "max-age=86400");
				
				blobImagem = imagemQFV.getImagem();
					
				//Passando o content length agora que sabe o tamanho do blob. 
				response.setContentLength(blobImagem.length);
				try {
					response.getOutputStream().write(blobImagem, 0, blobImagem.length);
					response.flushBuffer();
				}
				catch (Exception ex) {
					logger.warn("Erro de IO!", ex);
				}
				
			} 
			else {
				
				// Não achou imagem no repositório. Mostra uma imagem "indisponível". 
				InputStream ris = getServletContext().getResourceAsStream(IMG_INDISPONIVEL);
				blobImagem = new byte[4096];
				int bytesLidos = 0;

				try {
					try {
						while ((bytesLidos = ris.read(blobImagem)) != -1) {
							response.getOutputStream().write(blobImagem, 0, bytesLidos);
						}
					}
					finally {
						ris.close();
					}
					//response.flushBuffer();
					response.getOutputStream().close();
				}
				catch (Exception ex) {
					logger.warn("Erro de IO!", ex);
				}
				
			}
			logger.info("[TEMPO] Imagem: "+( System.currentTimeMillis() - inicio));
		}
		catch(Exception err) {
			logger.error("Erro ao enviar imagem.", err);
			throw new ServletException("Erro ao enviar a imagem: " + err.getMessage(), err);
		}

	}
}