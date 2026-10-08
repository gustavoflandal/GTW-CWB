/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 06/05/2008

  Descricao: Servlet que dispara o fechamento do processo.


  Historico:

    $Log: FinalizarProcesso.java,v $
    Revision 1.6  2009/02/17 19:05:39  fos
    Agora finaliza processo de liberação também.

    Revision 1.5  2009/01/12 12:49:52  fos
    Recuperação de repositório.

    Revision 1.3  2008/08/20 13:41:28  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66

    Revision 1.2  2008/07/23 14:33:24  fos
    Agora caso a finalização seja chamada sem um processo na sessão, volta para página inicial.

    Revision 1.1  2008/05/08 21:34:04  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.servlet.processamento;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.Processamento;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.RemessaIteracao;

 /**
 * Servlet que dispara o fechamento do processo.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.6 $ $Date: 2009/02/17 19:05:39 $ $Author: fos $
 */
public class FinalizarProcesso extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(FinalizarProcesso.class); 
	/**
	 * Constrói o objeto
	 */
	public FinalizarProcesso() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Acesso acesso = new Acesso(request, response, true); 
		
		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/gtw_principal.jsp");
			return; //O usuário não tem acesso...então cai fora!
		}
		
		logger.info("Finalizando Processamento [" + acesso.getUsuario().getUsuario() + "]");
		
		try {
			EtapaProcesso etapa = finaliza(request);

			if (etapa == null) {
				logger.warn("EtapaProcesso retornou NULL");
				response.sendRedirect("/login/gtw_principal.jsp");
				return;
			}

			switch (etapa) {
				case TRIAGEM:
					response.sendRedirect("/processo/listar_infracao_triagem.jsp");
					break;
				case DIGITACAO:
					response.sendRedirect("/processo/listar_infracao_digitacao.jsp");
					break;
				case VALIDACAO:
					response.sendRedirect("/processo/concluir_validacao.jsp");
					break;
				case LIBERACAO:
					response.sendRedirect("/processo/listar_infracao_liberacao.jsp");
					break;
				case IMAGENS_TESTE:
					response.sendRedirect("/processo/listar_imgteste_digitacao.jsp");
					break;
				case DIGITACAO_SUPERVISOR:
					response.sendRedirect("/processo/listar_infracao_digitacao_supervisor.jsp");
					break;
				default:
					response.sendRedirect("/login/gtw_principal.jsp");
					break;
			}
		}
		catch(Exception err) {
			logger.error("Erro ao finalizar o processamento.", err);
			new Mensagem(response).showErro("Erro ao finalizar processamento: "+err.getMessage());
			return; //O usuário não tem acesso...então cai fora!
		}
		
	}  	

	private EtapaProcesso finaliza(HttpServletRequest request) throws ServletException {
		Processamento proc = (Processamento) request.getSession().getAttribute("[processamento]");
		
		if (proc == null)
		{
			logger.warn("EtapaProcesso [processamento] retornou NULL");
			RemessaIteracao ri = (RemessaIteracao) request.getSession().getAttribute("remessa_iteracao");
			if (ri != null) {
				logger.warn("EtapaProcesso 'remessa_iteracao' retornou - redirecionando para VALIDACAO");
				try {
					logger.info("Fechando Janela da Validacao");
					Processamento.finalizaJanelaProcessamento(new Acesso(request, null, false).getUsuario().getId(), EtapaProcesso.VALIDACAO.getId());
				}
				catch(Exception e) {
					logger.error("Erro ao fechar Janela", e);
				}
				return EtapaProcesso.VALIDACAO;
			}
			else {
				logger.warn("EtapaProcesso 'remessa_iteracao' retornou NULL");
				return null;
			}
		}
		
		EtapaProcesso eRet =  proc.getEtapa(); 
		if(eRet == EtapaProcesso.REMESSA_VALIDADA)
			eRet = EtapaProcesso.VALIDACAO;

		
		try {
			proc.finalizaProcessamento();
		} 
		catch (Exception err) {
			throw new ServletException(err);
		}
			
		request.getSession().removeAttribute("[processamento]");
		
		return eRet;
	}
}
