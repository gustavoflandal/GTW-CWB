/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 22/07/2008

  Descricao: Classe para amostra de mensagens com javascript.

  Historico:

    $Log: MensagemJS.java,v $
    Revision 1.3  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.1  2008/07/23 14:29:32  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

/**
 * Classe para amostra de mensagens com prompt JavaScript.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.3 $ $Date: 2009/01/12 12:49:42 $ $Author: fos $
 */
public class MensagemJS {
	private HttpServletResponse response;
	/**
	 * Constrói o objeto Mensagem com os seus respectivos atributos.
	 * @param response Referência a resposta HTTP
	 */
	public MensagemJS(HttpServletResponse response) {
		this.response = response;
	}
	/**
	 * Mostra uma mensagem de erro.
	 * @param sMensagem Mensagem a ser mostrada
	 * @throws IOException
	 * @see #showErro(String, Boolean)
	 */
	public void showErro(String sMensagem) throws IOException {
		showErro(sMensagem, false);
	}
	/**
	 * Mostra uma mensagem de erro.
	 * @param sMensagem Mensagem a ser mostrada
	 * @param fecharJanela Se true, o botão OK irá fechar a janela.
	 * @throws IOException
	 * @see #showErro(String)
	 */
	public void showErro(String sMensagem, Boolean fecharJanela) throws IOException {
		String closeUrl = "javascript: window.close()";
		String goUrl = "javascript: history.back()";
		String sMens = "";
		try {
			if (fecharJanela)
				goUrl = URLEncoder.encode(closeUrl, "UTF-8");
				
			sMens = URLEncoder.encode(sMensagem, "UTF-8");
		}
		catch (Exception e) {}
		response.sendRedirect("/includes/erro_vazio.jsp?m="+sMens+"&p="+goUrl);
	}
	/**
	 * Mostra uma mensagem de sucesso.
	 * @param sMensagem Mensagem a ser mostrada
	 * @throws IOException
	 * @see #showSucesso(String, Boolean)
	 */
	public void showSucesso(String sMensagem) throws IOException {
		showSucesso(sMensagem, false);
	}
	/**
	 * Mostra uma mensagem de sucesso.
	 * @param sMensagem Mensagem a ser mostrada
	 * @param fecharJanela Se true, o botão OK irá fechar a janela.
	 * @throws IOException
	 * @see #showErro(String)
	 */
	public void showSucesso(String sMensagem, Boolean fecharJanela) throws IOException {
		String closeUrl = "javascript: window.close()";
		String goUrl = "javascript: history.back()";
		String sMens = "";
		try {
			if (fecharJanela)
				goUrl = URLEncoder.encode(closeUrl, "UTF-8");
				
			sMens = URLEncoder.encode(sMensagem, "UTF-8");
		}
		catch (Exception e) {}
		response.sendRedirect("/includes/sucesso_vazio.jsp?m="+sMens+"&p="+goUrl);
	}
}
