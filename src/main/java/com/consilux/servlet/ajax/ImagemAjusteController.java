/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Ederson Luiz Silva
  Data: 24/01/2012

  Descricao: Servlet para envio de informações sobre o ajuste de imagem.

  Historico:

    Revision 1.1  2009/03/03 21:39:46  fos
    Primeira versão postada no CVS.

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
import com.consilux.model.ImagemAjuste;

 /**
 * Servlet para envio de informações sobre ajuste da imagem.
 * @author Ederson Luiz Silva - Consilux Tecnologia
 * @version $Revision: 1.2 $ $Date: 2012/01/24 10:06:08 $ $Author: ederson.silva $
 */
public class ImagemAjusteController extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(InfoInfracaoObliteracao.class); 

	public ImagemAjusteController() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (!new Acesso(request, response, false).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		String sAction = request.getParameter("Action");
		
		if(sAction.equals("GetImagemAjuste")){
			String sIdImagem = request.getParameter("IdImagem");
			sIdImagem = sIdImagem != null && sIdImagem.length() == 0 ? null : sIdImagem;
			
			if (sIdImagem == null || !ExpValida.NATURAL.validar(sIdImagem))
				throw new ServletException("Identificador da imagem enviado invalido!");
	
			try {
				logger.info("GetImagemAjuste IDIMAGEM = " + sIdImagem);
				
				ImagemAjuste imgAjuste = null;
				
				imgAjuste = ImagemAjuste.GetImagemAjusteById(Integer.parseInt(sIdImagem));
				
				AjaxXMLConstr xml = new AjaxXMLConstr("ImagemAjuste");
	
				if (imgAjuste != null) {
					xml.adicCampo("IdImagem", String.valueOf(imgAjuste.getIdImagem()));
					xml.adicCampo("Brilho", String.valueOf(imgAjuste.getBrilho()));
					xml.adicCampo("Contraste", String.valueOf(imgAjuste.getContraste()));
					
					logger.info("GetImagemAjuste IDIMAGEM = " + imgAjuste.getIdImagem() + " ; BRILHO = " + imgAjuste.getBrilho() + " ; CONTRASTE = " + imgAjuste.getContraste());
				}
				
				xml.dump(response);
			}
			catch(Exception err) {
				logger.error("Erro ao buscar a ImagemAjuste: "+err.getMessage(), err);
				throw new ServletException("Erro ao montar o XML: "+err.getMessage());
			}
		}

		if(sAction.equals("SetImagemAjuste")){
			String sIdImagem = request.getParameter("IdImagem");
			sIdImagem = sIdImagem != null && sIdImagem.length() == 0 ? null : sIdImagem;
			
			if (sIdImagem == null || !ExpValida.NATURAL.validar(sIdImagem))
				throw new ServletException("Identificador da imagem enviado invalido!");
	
			try {
				Object obj_brilho = request.getParameter("Brilho");
				Object obj_contraste = request.getParameter("Contraste");
				
				logger.info("SetImagemAjuste IDIMAGEM = " + sIdImagem + " ; BRILHO = " + obj_brilho + " ; CONTRASTE = " + obj_contraste);
				
				int IDIMAGEM = Integer.parseInt(sIdImagem);
				int BRILHO = (obj_brilho != null && obj_brilho != "") ? Integer.parseInt(request.getParameter("Brilho")) : 0;
				float CONTRASTE = (obj_contraste != null && obj_contraste != "") ? Float.parseFloat(request.getParameter("Contraste")) : 0;
				
				ImagemAjuste.SetImagemAjuste(IDIMAGEM, BRILHO, CONTRASTE);
				
				AjaxXMLConstr xml = new AjaxXMLConstr("ImagemAjuste");
	
				xml.adicCampo("IdImagem", String.valueOf(IDIMAGEM));
				
				xml.dump(response);
			}
			catch(Exception err) {
				logger.error("Erro ao gravar a ImagemAjuste: "+err.getMessage(), err);
				throw new ServletException("Erro ao montar o XML: "+err.getMessage());
			}
		}
	}  	
}