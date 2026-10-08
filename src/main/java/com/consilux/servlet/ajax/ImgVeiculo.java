/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 04/04/2007

  Descrição: Servlet para envio da imagem sobre infração.

  Histórico:

    $Log: ImgVeiculo.java,v $
    Revision 1.4  2009/01/12 12:49:46  fos
    Recuperação de repositório.

    Revision 1.2  2008/09/03 13:06:37  fos
    ASSIGNED - bug 92: Infração sem imagem
    http://bugzilla.consilux.net/show_bug.cgi?id=92

    Revision 1.1  2008/08/27 14:00:41  fos
    ASSIGNED - bug 17: Seleção/Visualização das múltiplas imagens da infração.
    http://bugzilla.consilux.net/show_bug.cgi?id=17

    Revision 1.3  2008/08/25 13:37:28  fos
    Agora separa as imagens em tipos para obter a panorâmica.

    Revision 1.2  2008/08/20 13:41:28  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66

    Revision 1.1  2008/05/08 21:32:07  fos
    Alterações dos nomes das classes.

    Revision 1.3  2008/02/28 18:47:16  fos
    Colocado aviso de futura implementação.

    Revision 1.2  2008/02/21 21:08:28  fos
    Renomeada a classe InfracaoImagem.

    Revision 1.1  2008/02/06 19:20:25  fos
    Carga da infração na nova tela funcional.

    Revision 1.1  2007/04/11 12:09:19  fos
    Primeira versão postada no CVS.



 *********************************************************************************/
package com.consilux.servlet.ajax;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.infra.Funcoes;
import com.consilux.model.Acesso;
import com.consilux.model.VeiculoImagem;

/**
 * Servlet para envio da imagem de veículos.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2009/01/12 12:49:46 $ $Author: fos $
 */
public class ImgVeiculo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(ImgVeiculo.class); 
	private static String IMG_INDISPONIVEL = "/images/img_indisponivel.jpg";
	public static String IMAGEM_LOADING = "/images/ajax-loader.gif";
	
	/**
	 * Constrói o objeto
	 */
	public ImgVeiculo() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Acesso acesso = new Acesso(request, response, false); 
		
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");

		String sIdImagem = request.getParameter("id_imagem");
		String sAltura = request.getParameter("altura");
		String sLargura = request.getParameter("largura");
		String sZoom = request.getParameter("zoom");
		int zoom = 1;

		if (sIdImagem == null || !ExpValida.NATURAL_COM_ZERO.validar(sIdImagem))
			throw new ServletException("Identificador da Imagem enviado inválido!");
		
		if (sZoom != null && ExpValida.NATURAL_COM_ZERO.validar(sIdImagem))
			zoom = Integer.parseInt(sZoom);

		if ((sAltura != null && sLargura != null) && 
			(!ExpValida.NATURAL.validar(sAltura) || !ExpValida.NATURAL.validar(sLargura)))
			throw new ServletException("Identificador de Altura/Largura da imagem enviado inválido!");

		Integer idImagem = Integer.valueOf(sIdImagem);

//		String sAplicavel = request.getParameter("aplicavel");
//		boolean aplicavel =  "0".equals(sAplicavel) ? false : true;
		
		boolean desobliterar = request.getParameter("desobliterado") != null;
		
		logger.debug("Buscando imagem" + (desobliterar ? " DESOBLITERADA " : " ") + "no repositorio [" + sIdImagem + "] [Zoom: " + zoom + "] [" + acesso.getUsuario().getUsuario() + "]");
		long inicio = System.currentTimeMillis();
		
		try {
			
			VeiculoImagem veiculoImagem = VeiculoImagem.buscaVeiculoImagemCache(idImagem, acesso.getUsuario().getId());

			logger.debug("Retornando imagem para cliente [" + sIdImagem + "] ...[" + acesso.getUsuario().getUsuario() + "]");
			
			byte[] blobImagem;
			response.setContentType("image/jpeg");
			
			if (veiculoImagem != null) {
				
				// Imagem veio do repositorio, definir cache.
				response.setHeader("Cache-Control", "max-age=86400");
				if(desobliterar)
					blobImagem = veiculoImagem.getImagemDesob();
				else
					blobImagem = veiculoImagem.getImagem();
					
				//Se recebemos os parâmetros, vamos redimensionar a imagem...
				if (sAltura != null && sLargura != null) {
					blobImagem = Funcoes.redimensionaImagem(blobImagem, Integer.valueOf(sLargura), Integer.valueOf(sAltura));
				}
				
				if (zoom > 1)
				{
					BufferedImage img = ImageIO.read(new ByteArrayInputStream(blobImagem));
					blobImagem = Funcoes.redimensionaImagem(blobImagem, img.getWidth() * zoom, img.getHeight() * zoom);
				}
		        // Se é uma imagem "não-aplicavel", precisa desenhar um X vermelho por cima da imagem
//		        if (!aplicavel)
//		        {
//		        	blobImagem = Funcoes.drawX(blobImagem);
//		        }				
				
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