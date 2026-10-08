package muralha.digital.veiculo.imagem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


@WebServlet("/MuralhaDigital/Veiculo/Imagem")
public class ImgVeiculoTempoReal extends HttpServlet {
	
	private static final long serialVersionUID = 1L;	
	private static Logger logger = Logger.getLogger(ImgVeiculoTempoReal.class); 
	
	@SuppressWarnings("unused")
	private static final String IMG_INDISPONIVEL 		= "/muralha-digital/assets/images/consilux_grande_transparent.png";
	private static final String IMG_VEICULO_SEM_IMAGEM 	= "/muralha-digital/assets/images/consilux_imagem_sem_veiculo.png";		
	public static final float 	QUALIDADE_JPEG 			= 0.9f;
	
	
	/**
	 * Constrói o objeto
	 */
	public ImgVeiculoTempoReal() {
		super();
	}
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String sAcao 		= request.getParameter("acao");		
		String sIdVeic 		= request.getParameter("idVeic");
		String sTpImagem 	= request.getParameter("tpImagem");
		String sIdAlerta 	= request.getParameter("idAlerta");
		
		//Apenas para obtençao da imagem com ID
		String sIdImagem 	= request.getParameter("id");
		
		logger.info("Acao:: " + sAcao + " TpImagem:: " + sTpImagem + " IdImagem: " + sIdImagem + " IdVeic: " + sIdVeic);		
		
		try 
		{
			if( (sAcao == null && (sIdImagem != null && !sIdImagem.equals(""))) || sAcao.equals("ImagemByIdImg")) { 
				EnviarImagemPorId(response, (sIdImagem != null ? UUID.fromString(sIdImagem) : null)); 
				return;
			}
			
			if(sAcao.equals("ImagemByIdVeic")){
				 EnviarImagemPorId(response,  ObterIdImagem(sIdVeic, sTpImagem)); 
				return;				
			}
			
			if(sAcao.equals("ImagemByIdAlerta")){
				 EnviarImagemPorId(response,  ObterImagemByIdAlerta(sIdAlerta)); 
				return;				
			}
			
			if(sAcao.equals("prepararImgGrade")){
				EnviarImagemPorIdTipo(response, request, (sIdImagem != null ? UUID.fromString(sIdImagem) : null)); 
				return;				
			}

		}
		catch(Exception err) {
			logger.error("Erro ao processar chamada doGet().", err);
			throw new ServletException("Erro ao enviar a imagem: " + err.getMessage(), err);
		}
	}
	
	
	private UUID ObterIdImagem(String sIdVeic, String sTpImagem) throws ServletException
	{		
		try 
		{
			UUID idVeic = UUID.fromString(sIdVeic);
			UUID idImgRet = null;
			
			VeiculoImagem imagemV = VeiculoImagens.ObterImagemPorIdVeicTpImagem(idVeic, Integer.parseInt(sTpImagem));			
			idImgRet = imagemV.getId();
			
			if(idImgRet == null){
				logger.error("XXX - ObterIdImagem():: Id de Imagem nao encontrada no banco. idVeic: " + sIdVeic + " TpImg: " + sTpImagem);
			}
			
			return idImgRet;			
			
		}
		catch(Exception err) {
			logger.error("Erro ao processar chamada doGet():: obterIdImagem(): ", err);
			throw new ServletException("Erro ao enviar a imagem: " + err.getMessage(), err);
		}
	}
	
	private void EnviarImagemPorId(HttpServletResponse response, UUID id) throws ServletException, IOException 
	{		
		long inicio = System.currentTimeMillis();
		
		try 
		{	
			if( id == null ) 
			{	
				EnviarImagemIndisponivel(response);
				return;
			}
			
			logger.debug("Buscando imagem no repositorio [" + id.toString() + "]");
			
			VeiculoImagem imagem = VeiculoImagens.obterImagemPorId(id);

			logger.debug("Retornando imagem para cliente [" + id.toString() + "] ...");
			
			byte[] blobImagem;
			response.setContentType("image/jpeg");
			
			if (imagem != null && imagem.getImagem() != null) {
				
				// Imagem veio do repositorio, definir cache.
				response.setHeader("Cache-Control", "max-age=86400");
				blobImagem = imagem.getImagem();
				
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
			else 
			{			
				EnviarImagemIndisponivel(response);			
			}
			logger.info("[TEMPO] Imagem: "+( System.currentTimeMillis() - inicio));
		}
		catch(Exception err) {
			logger.error("Erro ao enviar imagem.", err);
			throw new ServletException("Erro ao enviar a imagem: " + err.getMessage(), err);
		}
	}
	
	private void EnviarImagemIndisponivel(HttpServletResponse response)
	{
		// Nao achou imagem no repositorio. Mostra uma imagem "indisponível". 
		InputStream ris = getServletContext().getResourceAsStream(IMG_VEICULO_SEM_IMAGEM);
		byte[] blobImagem = new byte[4096];
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
	
	private UUID ObterImagemByIdAlerta(String sIdAlerta) throws ServletException
	{		
		try 
		{
			UUID idAlerta = UUID.fromString(sIdAlerta);
			UUID IdImagem = null;
			
			IdImagem = VeiculoImagens.obterImagemByIdAlerta(idAlerta);			
			
			if(IdImagem == null){
				logger.error("XXX - ObterImagemByIdAlerta():: Id de Imagem nao encontrada no banco. idAlerta: " + sIdAlerta);
			}
			
			return IdImagem;			
			
		}
		catch(Exception err) {
			logger.error("Erro ao processar chamada doGet():: ObterImagemByIdAlerta(): ", err);
			throw new ServletException("Erro ao enviar a imagem: " + err.getMessage(), err);
		}
	}
	
	private void EnviarImagemPorIdTipo(HttpServletResponse response, HttpServletRequest request, UUID id) throws ServletException, IOException 
	{		
		long inicio = System.currentTimeMillis();
		
		String texto = request.getParameter("texto");
		
		try 
		{	
			if( id == null ) 
			{	
				EnviarImagemIndisponivel(response);
				return;
			}
			
			logger.debug("Buscando imagem no repositorio [" + id.toString() + "]");
			
			VeiculoImagem imagem = VeiculoImagens.obterImagemPorId(id);

			logger.debug("Retornando imagem para cliente [" + id.toString() + "] ...");
			
			byte[] blobImagem, bImg;
			response.setContentType("image/jpeg");
			
			if (imagem != null && imagem.getImagem() != null) {
				
				// Imagem veio do repositorio, definir cache.
				response.setHeader("Cache-Control", "max-age=86400");
				blobImagem = imagem.getImagem();
				bImg = blobImagem;
				
				if (texto != null && texto.trim() != "")
				{
					String[] textos = texto.split(";");
				
					BufferedImage bufImage = ImageIO.read(new ByteArrayInputStream(blobImagem));
					Graphics2D g = bufImage.createGraphics();
	
					g.setColor(Color.WHITE);
					g.fillRect(4, 4, 550, 75);
					g.setColor(Color.BLACK);
					g.setFont(new Font("Verdana", Font.BOLD, 16));
					g.drawString(textos[0], 7, 21);
					g.drawString(textos[1], 7, 46);
					g.drawString(textos[2], 7, 71);
					g.setStroke(new BasicStroke(2));
					g.setColor(Color.BLUE);
					g.drawRect(4, 4, 550, 75);
					
					bImg = ObterBytesImagem(bufImage);
				}
				
				//Passando o content length agora que sabe o tamanho do blob. 
				response.setContentLength(bImg.length);
				try {
					response.getOutputStream().write(bImg, 0, bImg.length);
					response.flushBuffer();
				}
				catch (Exception ex) {
					logger.warn("Erro de IO!", ex);
				}				
			} 
			else 
			{			
				EnviarImagemIndisponivel(response);			
			}
			logger.info("[TEMPO] Imagem: "+( System.currentTimeMillis() - inicio));
		}
		catch(Exception err) {
			logger.error("Erro ao enviar imagem.", err);
			throw new ServletException("Erro ao enviar a imagem: " + err.getMessage(), err);
		}
	}
	
	private static byte[] ObterBytesImagem(BufferedImage bufImage) throws IOException {
		byte[] bRet = null;
		
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
		if (iter.hasNext()) {
		
			ImageWriter writer = iter.next();
			ImageWriteParam writeParams = writer.getDefaultWriteParam();
			writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			writeParams.setCompressionQuality(0.9f);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageOutputStream ios = new MemoryCacheImageOutputStream(baos);
            writer.setOutput(ios);
			writer.write(null, new IIOImage(bufImage, null, null), writeParams);
			writer.dispose();
			
			bRet = baos.toByteArray();
		}
		
		return bRet;
	}
}