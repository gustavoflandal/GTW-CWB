package com.consilux.servlet.ajax;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.VeiculoImagem;

/**
 * Servlet que retorna uma thumbnail (blob) de uma imagem (veículo).
 * @author raoni
 */
public class ImgVeiculoThumb extends HttpServlet {

	private static final long serialVersionUID = 136456158273153639L;
	private static final float QUALIDADE_JPEG = 0.9f;
	private static Logger logger = Logger.getLogger(ImgVeiculoThumb.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Acesso acesso = new Acesso(request, response, false); 
		
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");

		String sIdImagem = request.getParameter("id_imagem");
		String sAplicavel = request.getParameter("aplicavel");
		boolean aplicavel =  "0".equals(sAplicavel) ? false : true;

		if (sIdImagem != null && !Pattern.matches("[0-9]{1,8}",sIdImagem))
			throw new ServletException("Identificador da Imagem enviado inválido!");

		Integer idImagem = Integer.valueOf(sIdImagem);

		logger.debug("Buscando imagem no repositorio [" + sIdImagem + "] ...[" + acesso.getUsuario().getUsuario() + "]");
		Date inicio = new Date();
		
		InputStream is = null;
		Graphics2D g2 = null;
		
		try {
			
			VeiculoImagem veiculoImagem = VeiculoImagem.buscaVeiculoImagemCache(idImagem, acesso.getUsuario().getId());
			assert veiculoImagem != null;
			logger.debug("Retornando thumbnail para cliente [" + sIdImagem + "] ...[" + acesso.getUsuario().getUsuario() + "]");
			byte[] blobImagem;
			
			if (veiculoImagem != null) {
				
				// Define cache.
				response.setContentType("image/jpeg");
				response.setHeader("Cache-Control", "max-age=86400");
				
				InputStream imageStream = new ByteArrayInputStream(veiculoImagem.getImagem());
				BufferedImage image = ImageIO.read(imageStream);
				
				int thumbWidth = 115;
				int thumbHeight = 86;
				
		        BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
		        g2 = thumbImage.createGraphics();
		        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		        g2.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
		        
		        // Imagem "não-aplicavel", desenha um X vermelho
		        if (!aplicavel)
		        {
		        	g2.setColor(Color.RED);
		        	g2.drawLine(0, 0, thumbWidth, thumbHeight);
		        	g2.drawLine(0, thumbHeight, thumbWidth, 0);
		        }
		        
				Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
				
				if (iter.hasNext()) {
				
					ImageWriter writer = iter.next();
					ImageWriteParam writeParams = writer.getDefaultWriteParam();
					writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					writeParams.setCompressionQuality(QUALIDADE_JPEG);
					ByteArrayOutputStream thumbStream = new ByteArrayOutputStream();
					ImageOutputStream ios = new MemoryCacheImageOutputStream(thumbStream);
		            writer.setOutput(ios);
					writer.write(null, new IIOImage(thumbImage, null, null), writeParams);
					writer.dispose();
					
					blobImagem = thumbStream.toByteArray();
					response.setContentLength(blobImagem.length);
					response.getOutputStream().write(blobImagem, 0, blobImagem.length);
				}		        
				
			}
			logger.info("[TEMPO] Thumbinail: " + (new Date().getTime() - inicio.getTime()));
		}
		catch(Exception err) {
			logger.error("Erro ao gerar thumbnail da imagem.", err);
			throw new ServletException("Erro ao gerar thumbnail da imagem: " + err.getMessage(), err);
		}		
		finally {
			if (g2 != null)
				g2.dispose();			
			if (is != null)
				is.close();
			response.flushBuffer();
		}

	}
	
}

