/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 17/03/2009

  Descricao: Servlet para visualização do protocolo de remessa à prodam.

  Historico:

    $Log: PREM.java,v $
    Revision 1.3  2009/06/01 18:43:10  fos
    Consertado expressão regular de inteiros.

    Revision 1.2  2009/04/03 15:53:06  fos
    Passando o diretório de imagens, que não era passado anteriormente.

    Revision 1.1  2009/03/18 17:21:07  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.servlet.remessa;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.MensagemJS;
 
 /**
 * Servlet para visualização do protocolo de remessa à prodam.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.3 $ $Date: 2009/06/01 18:43:10 $ $Author: fos $
 */
public class PREM extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DIR_PREM = "/WEB-INF/relatorio/";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";
	private static Logger logger = Logger.getLogger(PREM.class); 
	
	/**
	 * Constrói o objeto 
	 */
	public PREM() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new MensagemJS(response).showErro("Usuário não atenticado!");
			return; //O usuário não tem acesso...então cai fora!
		}

		String sIdRemessa = request.getParameter("id_remessa");
		
		if (sIdRemessa == null || !Pattern.matches("[0-9]{0,9}",sIdRemessa)) {
			new Mensagem(response).showErro("Identificador da Remessa enviado invalido!","javascript:window.close();");
			return;
		}
        
		try {
			Configuracao conf = ConfiguracaoProvider.getInstance();
	        
			RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(DIR_PREM+conf.getLayoutPREM()+".jasper"));
			
			relatorio.adicParametro("ID_REMESSA", Integer.parseInt(sIdRemessa));
			relatorio.adicParametro("DIR_IMAGENS", getServletContext().getRealPath(DIR_IMAGENS)+System.getProperty("file.separator"));
			relatorio.adicParametro("LOGO_EMPRESA", conf.getLogoEmpresa());

			relatorio.preencheRelatorio();
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition","attachment; filename=\"PREM.pdf\"");
		    relatorio.exportReportToPdfStream(response.getOutputStream());
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o PREM", e);
			new ServletException("Erro ao gerar o prem: "+e.getMessage(), e);
		}
	}
}