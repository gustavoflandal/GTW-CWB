/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/01/2007

  Descricao: Classe para amostra de mensagens.

  Historico:

    $Log: Mensagem.java,v $
    Revision 1.7  2009/02/17 19:04:42  fos
    Criada mensagem de confirmação para ser utilizada na liberação.

    Revision 1.6  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.4  2007/04/17 17:46:35  fos
    Consertado problema com link do IE

    Revision 1.3  2007/03/16 12:56:15  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.model;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

/**
 * Classe para amostra de mensagens.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.7 $ $Date: 2009/02/17 19:04:42 $ $Author: fos $
 */
public class Mensagem {
	private HttpServletResponse response;
	/**
	 * Constrói o objeto Mensagem com os seus respectivos atributos.
	 * @param response Referência a resposta HTTP
	 */
	public Mensagem(HttpServletResponse response) {
		this.response = response;
	}
	/**
	 * Mostra uma mensagem de erro.
	 * @param sMensagem Mensagem a ser mostrada
	 * @throws IOException
	 * @see #showErro(String, String)
	 */
	public void showErro(String sMensagem) throws IOException {
		showErro(sMensagem, null);
	}
	
	/**
	 * Mostra uma mensagem de erro.
	 * @param sMensagem Mensagem a ser mostrada
	 * @param urlDestino URL para onde deve apontar o botão voltar
	 * @param semCabecalho um boolean, para não incluir o cabecalho padrão do JSP.
	 * @throws IOException
	 * @see #showErro(String)
	 */
	public void showErro(String sMensagem, String urlDestino, boolean semCabecalho) throws IOException {
		
		String goUrl = "javascript: history.back()";
		String sMens = "";
		String rUrl = "/includes/erro.jsp?";

		try {
			if (urlDestino != null)
				goUrl = URLEncoder.encode(urlDestino, "UTF-8");
			sMens = URLEncoder.encode(sMensagem, "UTF-8");
		}
		catch (Exception e) {}
		
		if (semCabecalho)
			rUrl += "sc=true&";
			
		response.sendRedirect(rUrl + "m=" + sMens + "&p=" + goUrl);
	}	
	
	/**
	 * Mostra uma mensagem de erro.
	 * @param sMensagem Mensagem a ser mostrada
	 * @param urlDestino URL para onde deve apontar o botão voltar
	 * @throws IOException
	 * @see #showErro(String)
	 */
	public void showErro(String sMensagem, String urlDestino) throws IOException {
		showErro(sMensagem, urlDestino, false);
	}
	/**
	 * Mostra uma mensagem de sucesso.
	 * @param sMensagem Mensagem a ser mostrada
	 * @throws IOException
	 * @see #showSucesso(String, String)
	 */
	public void showSucesso(String sMensagem) throws IOException {
		showSucesso(sMensagem, null);
	}
	/**
	 * Mostra uma mensagem de sucesso.
	 * @param sMensagem Mensagem a ser mostrada
	 * @param urlDestino URL para onde deve apontar o botão voltar
	 * @throws IOException
	 * @see #showSucesso(String)
	 */
	public void showSucesso(String sMensagem, String urlDestino) throws IOException {
		String goUrl = "/login/gtw_principal.jsp";
		String sMens = "";
		try {
			if (urlDestino != null)
				goUrl = URLEncoder.encode(urlDestino, "UTF-8");
			sMens = URLEncoder.encode(sMensagem, "UTF-8");
		}
		catch (Exception e) {}
		response.sendRedirect("/includes/sucesso.jsp?m="+sMens+"&p="+goUrl);
	}
	/**
	 * Mostra uma mensagem de confirmação.
	 * @param sMensagem Mensagem a ser mostrada
	 * @param urlDestino URL para onde deve apontar o botão OK
	 * @throws IOException
	 */
	public void showConfirma(String sMensagem, String urlDestino) throws IOException {
		String goUrl = "/login/gtw_principal.jsp";
		String sMens = "";
		try {
			if (urlDestino != null)
				goUrl = URLEncoder.encode(urlDestino, "UTF-8");
			sMens = URLEncoder.encode(sMensagem, "UTF-8");
		}
		catch (Exception e) {}
		response.sendRedirect("/includes/confirma.jsp?m="+sMens+"&p="+goUrl);
	}
	/**
	 * Mostra uma mensagem de sim/não.
	 * @param sMensagem Mensagem a ser mostrada
	 * @param urlSim URL para onde deve apontar o botão SIM
	 * @param urlNaoURL para onde deve apontar o botão NÃO
	 * @throws IOException
	 */
	public void showSimNao(String sMensagem, String urlSim, String urlNao) throws IOException {
		String sMens = "";
		try {
			sMens = URLEncoder.encode(sMensagem, "UTF-8");
		}
		catch (Exception e) {}
		response.sendRedirect("/includes/sim_nao.jsp?m="+sMens+"&psim="+urlSim+"&pnao="+urlNao);
	}
	
	/**
	 * Mostra uma mensagem de erro.
	 * @param sMensagem Mensagem a ser mostrada
	 * @throws IOException
	 * @see #showErro(String, String)
	 */
	public void showErroMuralha(String sMensagem) throws IOException {
		showErroMuralha(sMensagem, null);
	}

	/**
	 * Mostra uma mensagem de erro.
	 * @param sMensagem Mensagem a ser mostrada
	 * @param urlDestino URL para onde deve apontar o botão voltar
	 * @throws IOException
	 * @see #showErro(String)
	 */
	public void showErroMuralha(String sMensagem, String urlDestino) throws IOException {
		showErroMuralha(sMensagem, urlDestino, false);
	}
	
	/**
	 * Mostra uma mensagem de erro.
	 * @param sMensagem Mensagem a ser mostrada
	 * @param urlDestino URL para onde deve apontar o botão voltar
	 * @param semCabecalho um boolean, para não incluir o cabecalho padrão do JSP.
	 * @throws IOException
	 * @see #showErro(String)
	 */
	public void showErroMuralha(String sMensagem, String urlDestino, boolean semCabecalho) throws IOException {
		
		String goUrl = "javascript: history.back()";
		String sMens = "";
		String rUrl = "/includes/erro_muralha.jsp?";

		try {
			if (urlDestino != null)
				goUrl = URLEncoder.encode(urlDestino, "UTF-8");
			sMens = URLEncoder.encode(sMensagem, "UTF-8");
		}
		catch (Exception e) {}
		
		if (semCabecalho)
			rUrl += "sc=true&";
			
		response.sendRedirect(rUrl + "m=" + sMens + "&p=" + goUrl);
	}
	
	public void showSucessoMuralha(String sMensagem) throws IOException {
		showSucessoMuralha(sMensagem, null);
	}
	/**
	 * Mostra uma mensagem de sucesso.
	 * @param sMensagem Mensagem a ser mostrada
	 * @param urlDestino URL para onde deve apontar o botão voltar
	 * @throws IOException
	 * @see #showSucesso(String)
	 */
	public void showSucessoMuralha(String sMensagem, String urlDestino) throws IOException {
		String goUrl = "/login/abertura-sistemas.jsp";
		String sMens = "";
		try {
			if (urlDestino != null)
				goUrl = URLEncoder.encode(urlDestino, "UTF-8");
			sMens = URLEncoder.encode(sMensagem, "UTF-8");
		}
		catch (Exception e) {}
		response.sendRedirect("/includes/sucesso_muralha.jsp?m="+sMens+"&p="+goUrl);
	}
}
