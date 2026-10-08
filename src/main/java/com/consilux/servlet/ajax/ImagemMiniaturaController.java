/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 13/07/2021

    Descricao: Servlet para envio de informações sobre a miniatura da imagem do AIT.

  Historico:

*********************************************************************************/
package com.consilux.servlet.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.ImagemMiniatura;
import com.consilux.model.ImagemMiniatura.PosicaoMiniatura;
import com.consilux.model.ImagemMiniatura.TamanhoMiniatura;

 /**
 * Servlet para envio de informações sobre ajuste da imagem.
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 13/07/2021
 */
public class ImagemMiniaturaController extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ImagemMiniaturaController.class); 

	public ImagemMiniaturaController() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (!new Acesso(request, response, false).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		String sAction = request.getParameter("Action");
		
		if(sAction.equals("GetImagemMiniatura")){
			String sIdInfracao = request.getParameter("IdInfracao");
			sIdInfracao = sIdInfracao != null && sIdInfracao.length() == 0 ? null : sIdInfracao;
			
			if (sIdInfracao == null || !ExpValida.NATURAL.validar(sIdInfracao))
				throw new ServletException("Identificador da infração enviado invalido!");
	
			try {
				logger.info("GetImagemMiniatura ID_INFRACAO = " + sIdInfracao);
				
				Integer idInfracao = Integer.parseInt(sIdInfracao);
				
				ImagemMiniatura imgMiniatura = null;
				imgMiniatura = ImagemMiniatura.obterImagemMiniaturaPorIdInfracao(idInfracao);
				
				AjaxXMLConstr xml = new AjaxXMLConstr("ImagemMiniatura");
	
				if (imgMiniatura != null) {
					xml.adicCampo("IdInfracao", String.valueOf(imgMiniatura.getIdInfracao()));
					xml.adicCampo("IdImagemPrincipal", String.valueOf(imgMiniatura.getIdImagemPrincipal()));
					xml.adicCampo("IdImagemMiniatura", String.valueOf(imgMiniatura.getIdImagemMiniatura()));
					xml.adicCampo("IdPosicao", String.valueOf(imgMiniatura.getIdPosicao()));
					xml.adicCampo("Posicao", String.valueOf(imgMiniatura.getPosicao()));
					xml.adicCampo("IdTamanho", String.valueOf(imgMiniatura.getIdTamanho()));
					xml.adicCampo("Porcentagem", String.valueOf(imgMiniatura.getPorcentagem()));
					xml.adicCampo("Tamanho", String.valueOf(imgMiniatura.getTamanho()));
					
					logger.debug("GetImagemMiniatura ID_INFRACAO = " + imgMiniatura.getIdInfracao() + 
							" ; ID_IMAGEM_PRINCIPAL = " + imgMiniatura.getIdImagemPrincipal() + 
							" ; ID_IMAGEM_INIATURA = " + imgMiniatura.getIdImagemMiniatura() +
							" ; POSICAO = " + String.format("%d (%s)", imgMiniatura.getIdPosicao(), imgMiniatura.getPosicao()) +
							" ; TAMANHO = " + String.format("%d (%s) - %s", imgMiniatura.getIdTamanho(), imgMiniatura.getTamanho(), String.valueOf(imgMiniatura.getPorcentagem())));
				}
				
				xml.dump(response);
			}
			catch(Exception err) {
				logger.error("Erro ao buscar a ImagemMiniatura: " + err.getMessage(), err);
				throw new ServletException("Erro ao montar o XML: " + err.getMessage());
			}
		}

		if(sAction.equals("SetImagemMiniatura")){
			String sIdInfracao = request.getParameter("id_infracao");
			String sIdImagemPrincipal = request.getParameter("id_imagem_principal");
			String sIdImagemMiniatura = request.getParameter("id_imagem_miniatura");
			String sPosicaoMiniatura = request.getParameter("posicao_miniatura");
			String sTamanhoMiniatura = request.getParameter("tamanho_miniatura");
			
			sIdInfracao = sIdInfracao != null && sIdInfracao.length() == 0 ? null : sIdInfracao;
			sIdImagemPrincipal = sIdImagemPrincipal != null && sIdImagemPrincipal.length() == 0 ? null : sIdImagemPrincipal;
			sIdImagemMiniatura = sIdImagemMiniatura != null && sIdImagemMiniatura.length() == 0 ? null : sIdImagemMiniatura;
			sPosicaoMiniatura = sPosicaoMiniatura != null && sPosicaoMiniatura.length() == 0 ? null : sPosicaoMiniatura;
			sTamanhoMiniatura = sTamanhoMiniatura != null && sTamanhoMiniatura.length() == 0 ? null : sTamanhoMiniatura;
			PosicaoMiniatura posicaoMiniatura = null;
			TamanhoMiniatura tamanhoMiniatura = null;
			
			if (sIdInfracao == null || !ExpValida.NATURAL.validar(sIdInfracao))
				throw new ServletException("Identificador da infração enviado invalido!");
			if (sIdImagemPrincipal == null || !ExpValida.NATURAL.validar(sIdImagemPrincipal))
				throw new ServletException("Identificador da imagem principal enviado invalido!");
			if (sIdImagemMiniatura != null && !ExpValida.NATURAL.validar(sIdImagemMiniatura))
				throw new ServletException("Identificador da imagem miniatura enviado invalido!");
			if (sPosicaoMiniatura != null && !ExpValida.NATURAL.validar(sPosicaoMiniatura))
				throw new ServletException("Identificador da posição da miniatura enviado invalido!");
			if (sTamanhoMiniatura != null && !ExpValida.NATURAL.validar(sTamanhoMiniatura))
				throw new ServletException("Identificador do tamanho da miniatura enviado invalido!");
	
			try {
				Integer idInfracao = Integer.parseInt(sIdInfracao);
				Integer idImagemPrincipal = Integer.parseInt(sIdImagemPrincipal);
				Integer idImagemMiniatura = sIdImagemMiniatura != null ? Integer.parseInt(sIdImagemMiniatura) : 0;
				
				if (sPosicaoMiniatura != null)
					posicaoMiniatura = PosicaoMiniatura.valueOfId(Integer.parseInt(sPosicaoMiniatura));
				
				if (sTamanhoMiniatura != null)
					tamanhoMiniatura = TamanhoMiniatura.valueOfId(Integer.parseInt(sTamanhoMiniatura));
				
				Integer idPosicao = posicaoMiniatura != null ? posicaoMiniatura.getId() : 0;
				Integer idTamanho = tamanhoMiniatura != null ? tamanhoMiniatura.getId() : 0;
				
				logger.debug("SetImagemMiniatura ID_INFRACAO = " + idInfracao + 
						" ; ID_IMAGEM_PRINCIPAL = " + idImagemPrincipal + 
						" ; ID_IMAGEM_INIATURA = " + idImagemMiniatura +
						" ; POSICAO = " + idPosicao +
						" ; TAMANHO = " + idTamanho);

				ImagemMiniatura.gravarImagemMiniatura(idInfracao, idImagemPrincipal, idImagemMiniatura, idPosicao, idTamanho);
				
				AjaxXMLConstr xml = new AjaxXMLConstr("ImagemMiniatura");
	
				xml.adicCampo("idInfracao", String.valueOf(idInfracao));
				
				xml.dump(response);
			}
			catch(Exception err) {
				logger.error("Erro ao gravar a ImagemMiniatura: " + err.getMessage(), err);
				throw new ServletException("Erro ao montar o XML: " + err.getMessage());
			}
		}
	}  	
}