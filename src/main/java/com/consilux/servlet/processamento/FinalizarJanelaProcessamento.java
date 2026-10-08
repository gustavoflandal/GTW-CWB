/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 27/05/2009

  Descricao: Servlet que dispara o fechamento de janelas de infrações


  Historico:

    $Log: FinalizarJanelaProcessamento.java,v $
    Revision 1.1  2009/05/28 13:51:05  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.servlet.processamento;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.Processamento;

 /**
 * Servlet que dispara o fechamento de janelas de infrações
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.1 $ $Date: 2009/05/28 13:51:05 $ $Author: fos $
 */
public class FinalizarJanelaProcessamento extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Constrói o objeto
	 */
	public FinalizarJanelaProcessamento() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, true).verificaAcesso(false))
			return;
		
		try {
			String sIdProcesso = request.getParameter("id_processo");
			String sIdUsuario = request.getParameter("id_usuario");
			
			if (sIdUsuario == null || !Pattern.matches("[0-9]{1,8}",sIdUsuario)) {
				new Mensagem(response).showErro("Identificador do usuário enviado invalido!");
				return;
			}

			if (sIdProcesso == null || !Pattern.matches("[0-9]{1,8}",sIdProcesso)) {
				new Mensagem(response).showErro("Identificador de etapa enviado invalido!");
				return;
			}
			
			Processamento.finalizaJanelaProcessamento(Integer.valueOf(sIdUsuario), Integer.valueOf(sIdProcesso));
			
			new Mensagem(response).showSucesso("Janela de infrações limpa.", request.getHeader("Referer"));
			
		}
		catch(Exception err) {
			err.printStackTrace();
			throw new ServletException("Erro ao processa requisição: "+err.getMessage());
		}
	}  	
}
