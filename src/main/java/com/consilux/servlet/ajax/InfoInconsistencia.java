/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 01/08/2008

  Descricao: Servlet para envio de informações sobre inconsistência.

  Historico:

    $Log: InfoInconsistencia.java,v $
    Revision 1.5  2009/01/12 12:49:46  fos
    Recuperação de repositório.

    Revision 1.3  2008/08/27 14:00:41  fos
    ASSIGNED - bug 17: Seleção/Visualização das múltiplas imagens da infração.
    http://bugzilla.consilux.net/show_bug.cgi?id=17

    Revision 1.2  2008/08/20 13:41:29  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66

    Revision 1.1  2008/08/12 13:03:36  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.servlet.ajax;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.model.Acesso;
import com.consilux.model.Inconsistencia;

 /**
 * Servlet para envio de informações sobre inconsistência.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.5 $ $Date: 2009/01/12 12:49:46 $ $Author: fos $
 */
public class InfoInconsistencia extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Constrói o objeto
	 */
	public InfoInconsistencia() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, false).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		String sIdInconsistencia = request.getParameter("id_inconsistencia");
		
		if (sIdInconsistencia == null || !Pattern.matches("[0-9]{1,8}",sIdInconsistencia))
			throw new ServletException("Identificador da Inconsistência enviado invalido!");

		try {
			Inconsistencia inconsistencia = Inconsistencia.buscaInconsistenciaPorId(Integer.parseInt(sIdInconsistencia));

			if (inconsistencia == null) {
				return;
			}

			AjaxXMLConstr xml = new AjaxXMLConstr("inconsistencia");
			xml.adicCampo("INCONSISTENCIA", inconsistencia.getDescricao());
			xml.dump(response);
		}
		catch(Exception err) {
			throw new ServletException("Erro ao montar o XML: "+err.getMessage(), err);
		}
	}  	
}