/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/03/2007

  Descricao: Servlet para envio de informações sobre infração.

  Historico:

    $Log: InfoInfracaoObliteracao.java,v $
    Revision 1.2  2009/06/01 18:42:08  fos
    Consertado expressão regular de inteiros.

    Revision 1.1  2009/03/03 21:39:46  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.servlet.ajax;

import java.awt.Rectangle;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.InfracaoObliteracao;

 /**
 * Servlet para envio de informações sobre obliteração na infração.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.2 $ $Date: 2009/06/01 18:42:08 $ $Author: fos $
 */
public class InfoInfracaoObliteracao extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(InfoInfracaoObliteracao.class); 
	/**
	 * Constrói o objeto
	 */
	public InfoInfracaoObliteracao() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, false).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		String sIdInfracao = request.getParameter("id_infracao");
		String sIdInfracaoProcesso = request.getParameter("id_infracao_processo");
		String sIdImagem = request.getParameter("id_imagem");
		sIdImagem = sIdImagem != null && sIdImagem.length() == 0 ? null : sIdImagem;
		
		if (sIdInfracao != null && !ExpValida.NATURAL.validar(sIdInfracao))
			throw new ServletException("Identificador da Infração enviado invalido!");
		
		if (sIdInfracaoProcesso != null && !ExpValida.NATURAL.validar(sIdInfracaoProcesso))
			throw new ServletException("Identificador da Infração no Processo enviado invalido!");
		
		if (sIdInfracao == null && sIdInfracaoProcesso == null)
			throw new ServletException("Não foi enviado nenhum identificador da infração!");

		if (sIdImagem == null || !ExpValida.NATURAL.validar(sIdImagem))
			throw new ServletException("Identificador da Infração enviado invalido!");

		try {
			InfracaoObliteracao obliteracao = null;
			if (sIdInfracao != null)
				obliteracao = InfracaoObliteracao.buscaInfracaoObliteracaoPorIdInfracao(Integer.valueOf(sIdInfracao), Integer.valueOf(sIdImagem));
			else if (sIdInfracaoProcesso != null)
				obliteracao = InfracaoObliteracao.buscaInfracaoObliteracaoPorIdInfracaoProcesso(Integer.valueOf(sIdInfracaoProcesso), Integer.valueOf(sIdImagem));
			
			AjaxXMLConstr xml = new AjaxXMLConstr("obliteracao");

			if (obliteracao != null) {
				xml.adicCampo("ID_IMAGEM", String.valueOf(obliteracao.getIdImagem()));
				xml.adicCampo("NUMERO_OBLITERACOES", String.valueOf(obliteracao.getListObliteracoes().size()));
				int t = 1;
				for(Rectangle r : obliteracao.getListObliteracoes()) {
//					xml.adicCampo("SEQUENCIA_OBLITERACAO", String.valueOf(t++));
					xml.adicCampo("X_" + t, String.valueOf(r.x));
					xml.adicCampo("Y_" + t, String.valueOf(r.y));
					xml.adicCampo("LARGURA_" + t, String.valueOf(r.width));
					xml.adicCampo("ALTURA_" + t, String.valueOf(r.height));
					t++;
				}
			}
			
			xml.dump(response);
			
		}
		catch(Exception err) {
			logger.error("Erro ao buscar a obliteracao: "+err.getMessage(), err);
			throw new ServletException("Erro ao montar o XML: "+err.getMessage());
		}
		
	}  	
}