/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 06/05/2011

  Descricao: Servlet para listagem de solicitações e auditoria.

  Historico:

    $Log$


*********************************************************************************/
package com.consilux.servlet.processamento;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.MensagemJS;
import com.consilux.model.SolicitacaoAuditoria;

 /**
 * Servlet para listagem de solicitações e auditoria.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.2 $ $Date: 2009/03/18 17:22:01 $ $Author: fos $
 */
public class ListarSolicitacaoAuditoria extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	
	private static final long serialVersionUID = 8412512406909507677L;
	private static Logger logger = Logger.getLogger(ListarSolicitacaoAuditoria.class);
	
	/**
	 * Constrói o objeto 
	 */
	public ListarSolicitacaoAuditoria() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!

		String sDataImagensIni = request.getParameter("dataini");
		String sDataImagensFim = request.getParameter("datafim");
		
		if (sDataImagensIni == null || !ExpValida.DATA.validar(sDataImagensIni)) {
			new MensagemJS(response).showErro("Data inicial das imagens enviada inválida!");
			return;
		}
		if (sDataImagensFim == null || !ExpValida.DATA.validar(sDataImagensFim)) {
			new MensagemJS(response).showErro("Data final das imagens enviada inválida!");
			return;
		}

		Date dataImagensIni;
		Date dataImagensFim;
				
		try {
			dataImagensIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataImagensIni + " 00:00:00");
			dataImagensFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataImagensFim + " 00:00:00");
		}
		catch (Exception e) {
			logger.error("Erro realizar parser da data", e);
			new MensagemJS(response).showErro("Erro realizar parser da data: "+e.getMessage());
			return;
		}
		
		try {
			Map<String,Object> mapFiltros = new HashMap<String, Object>();
			mapFiltros.put("data_ini_imagem", dataImagensIni);
			mapFiltros.put("data_fim_imagem", dataImagensFim);
			
			List<SolicitacaoAuditoria> lsa = SolicitacaoAuditoria.buscaSolicitacaoAuditoriaPor(mapFiltros);
			
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/templates/processo/list_solicitacao_auditoria.jsp");
			request.setAttribute("solicitacoes_auditoria", lsa);
			rd.forward(request, response);
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar a solição de auditoria.", e);
			new MensagemJS(response).showErro("Erro ao gerar ao listar as solicitações de auditoria: "+e.getMessage());
			return;
		}
		
	}  	  	
	
}