/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 01/08/2008

  Descricao: Servlet para envio de informações sobre classe do veículo.

  Historico:

    $Log: InfoClasse.java,v $
    Revision 1.1  2009/03/24 21:39:03  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.servlet.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.model.Acesso;
import com.consilux.model.ClasseVeiculo;

 /**
 * Servlet para envio de informações sobre classe do veículo.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.1 $ $Date: 2009/03/24 21:39:03 $ $Author: fos $
 */
public class InfoClasse extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Constrói o objeto
	 */
	public InfoClasse() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, false).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		String sIdClasse = request.getParameter("id_classe");
		
		if (sIdClasse == null || sIdClasse.length() > 4)
			throw new ServletException("Identificador da classe enviado invalido!");

		try {
			ClasseVeiculo classe = ClasseVeiculo.buscaClasseVeiculo(sIdClasse);

			if (classe == null) {
				return;
			}

			AjaxXMLConstr xml = new AjaxXMLConstr("classe");
			xml.adicCampo("CLASSE", classe.getDescricao());
			xml.dump(response);
		}
		catch(Exception err) {
			throw new ServletException("Erro ao montar o XML: "+err.getMessage());
		}
	}  	
}