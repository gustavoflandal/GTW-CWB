/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 17/03/2009

  Descricao: Servlet para excluir uma remessa.

  Historico:

    $Log: ExcluirRemessa.java,v $
    Revision 1.2  2009/06/01 18:41:32  fos
    Consertado expressão regular de inteiros.

    Revision 1.1  2009/03/18 17:20:56  fos
    Primeira versão postada no CVS.



*********************************************************************************/
package com.consilux.servlet.ajax;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.model.Acesso;
import com.consilux.model.Processamento;
import com.consilux.model.Remessa;

 /**
 * Servlet para excluir uma remessa.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.2 $ $Date: 2009/06/01 18:41:32 $ $Author: fos $
 */
public class ExcluirRemessa extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Constrói o objeto
	 */
	public ExcluirRemessa() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (!new Acesso(request, response, true).verificaAcesso(false))
				throw new ServletException("Usuário não atenticado!");
			
			String sIdRemessa = request.getParameter("id_remessa");
			
			if (sIdRemessa == null || !Pattern.matches("[1-9][0-9]{0,8}",sIdRemessa))
				throw new ServletException("Identificador da Remessa enviado invalido!");
	
			try {
				Remessa remessa = Remessa.buscarRemessaPorId(Integer.parseInt(sIdRemessa));
	
				if (remessa == null)
					throw new ServletException("Remessa não existe!");
	
				if(remessa.isValidada())
					throw new ServletException("Não é permitido excluir esta remessa, porque ela já foi parcialmente validada!");
				
				remessa.reposicionarRemessa(Processamento.EtapaProcesso.VALIDACAO.getId(), 
						Processamento.EtapaProcesso.REMESSA_GERAL.getId());
				
				remessa.excluirRemessa();
			}
			catch(Exception err) {
				throw new ServletException("Erro ao remover remessa: "+err.getMessage());
			}
		}
		catch (Exception err) {
			AjaxXMLConstr xml;
			try {
				xml = new AjaxXMLConstr("remessa");
				xml.adicCampo("ERRO", err.getMessage());
				xml.dump(response);
			}
			catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
	}  	
}