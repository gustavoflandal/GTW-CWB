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
package com.consilux.servlet.medicao;

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
import com.consilux.model.medicao.ProcessoMedicao;

 /**
 * Servlet para listagem de solicitações e auditoria.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.2 $ $Date: 2009/03/18 17:22:01 $ $Author: fos $
 */
public class ListarProcessoMedicao extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	
	private static final long serialVersionUID = 8412512406909507677L;
	private static Logger logger = Logger.getLogger(ListarProcessoMedicao.class);
	
	/**
	 * Constrói o objeto 
	 */
	public ListarProcessoMedicao() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!

		String sDataIni = request.getParameter("dataini");
		String sDataFim = request.getParameter("datafim");
		
		if (sDataIni == null || !ExpValida.DATA.validar(sDataIni)) {
			new MensagemJS(response).showErro("Data inicial enviada inválida!");
			return;
		}
		if (sDataFim == null || !ExpValida.DATA.validar(sDataFim)) {
			new MensagemJS(response).showErro("Data final enviada inválida!");
			return;
		}

		Date dataIni;
		Date dataFim;
				
		try {
			dataIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataIni + " 00:00:00");
			dataFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataFim + " 00:00:00");
		}
		catch (Exception e) {
			logger.error("Erro realizar parser da data", e);
			new MensagemJS(response).showErro("Erro realizar parser da data: "+e.getMessage());
			return;
		}
		
		try {
			Map<String,Object> mapFiltros = new HashMap<String, Object>();
			mapFiltros.put("data_ini", dataIni);
			mapFiltros.put("data_fim", dataFim);
			
			List<ProcessoMedicao> lsa = ProcessoMedicao.buscarProcessoMedicaoPor(mapFiltros);
			
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/templates/medicao/list_processo_medicao.jsp");
			request.setAttribute("processos_medicao", lsa);
			rd.forward(request, response);
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar a solição de auditoria.", e);
			new MensagemJS(response).showErro("Erro ao gerar ao listar as solicitações de auditoria: "+e.getMessage());
			return;
		}
		
	}  	  	
	
}