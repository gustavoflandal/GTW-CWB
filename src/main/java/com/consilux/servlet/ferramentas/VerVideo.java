package com.consilux.servlet.ferramentas;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.AcessoStorageInterface;
import com.consilux.model.AcessoStorageProvider;
import com.consilux.model.Video;

/**
 * Servlet implementation class VerVideo
 */
public class VerVideo extends HttpServlet {
	private static Logger logger = Logger.getLogger(VerVideo.class); 
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerVideo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
//		Acesso acesso = new Acesso(request, response, false); 
		AcessoStorageInterface acesso_storage = AcessoStorageProvider.ObterInterface();
		
//		if (!acesso.verificaAcesso(false))
//			throw new ServletException("Usuário não atenticado!");

		String sIdVideo = request.getParameter("id_video");
		String sIdVeiculo = request.getParameter("id_veiculo");
		String sVideoSel = request.getParameter("video_sel");

		if (sVideoSel == null) {
			logger.debug("Video Selecionado = 1 (padrao)");
			sVideoSel = "1";
		}

		Integer iVideoSel = Integer.parseInt(sVideoSel);
		logger.debug("Video Selecionado = " + iVideoSel);
		
		if(sIdVideo == null && sIdVeiculo == null)
			throw new ServletException("Identificador do video/veiculo enviado inválido!");
		
		if (sIdVideo != null && !ExpValida.NATURAL_COM_ZERO.validar(sIdVideo))
			throw new ServletException("Identificador do video enviado inválido!");
		
		if (sIdVeiculo != null && !ExpValida.NATURAL_COM_ZERO.validar(sIdVeiculo))
			throw new ServletException("Identificador do veiculo enviado inválido!");
		
		Integer idVideo = 0;
		Long idVeiculo = 0L;
		if(sIdVideo != null)
			idVideo = Integer.parseInt(sIdVideo);
		if(sIdVeiculo != null)
			idVeiculo = Long.parseLong(sIdVeiculo);
		
		byte bVideo[] = null;
		
		try {
			Video v = null;
			if (idVideo > 0)
				v = Video.buscaVideoPorId(idVideo);
			else if (idVeiculo > 0)
				v = Video.buscaVideoPorIdVeiculo(idVeiculo, iVideoSel);
			
			if (v != null) {
				bVideo = v.getVideo();
				
				if(bVideo == null && v.getCaminho() != null) {
					bVideo = acesso_storage.ObterArquivo(v.getCaminho());
				}
			}
			
		}
		catch (Exception ex) {
			logger.warn("Erro ao buscar o veículo!", ex);
			throw new ServletException("Erro ao buscar o veículo!", ex);
		}

		if (bVideo == null)
			throw new ServletException("Video não encontrado!");

		logger.debug("Iniciando conversão do Vídeo");
		byte[] nVideo = ConverterVideo.ConverteVideo(bVideo, Math.max(idVideo, idVeiculo));
		logger.debug("Finalizado conversão");
		
		if (nVideo != null)
			bVideo = nVideo;
		
		//response.setContentType("video/x-divx");
		response.setContentType("video/mp4");
		response.setContentLength(bVideo.length);
		try {
			response.getOutputStream().write(bVideo, 0, bVideo.length);
			response.flushBuffer();
		}
		catch (Exception ex) {
			logger.warn("Erro de IO!", ex);
			throw new ServletException("Erro de IO!", ex);
		}
	}
}
