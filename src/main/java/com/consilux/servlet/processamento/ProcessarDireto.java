/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 16/02/2009

  Descricao:  Servlet que dispara o processamento de infrações automático.


  Historico:

    $Log: ProcessarDireto.java,v $
    Revision 1.2  2009/04/13 16:41:06  fos
    Agora mostra uma mensagem além de imprimir o stack.

    Revision 1.1  2009/03/19 23:08:18  fos
    Primeira versão postada no CVS.

    Revision 1.1  2009/02/17 19:06:29  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.servlet.processamento;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.AcaoAdapter;
import com.consilux.infra.AcaoEvento;
import com.consilux.infra.AjaxXMLConstr;
import com.consilux.infra.Progresso;
import com.consilux.infra.ProgressoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.JobProcessaInfracaoAgendamento;
import com.consilux.model.Processamento;

 /**
 * Servlet que dispara o processamento de infrações automático.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.2 $ $Date: 2009/04/13 16:41:06 $ $Author: fos $
 */
public class ProcessarDireto extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private Long dataIni = System.currentTimeMillis();
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ProcessarDireto.class);
	/**
	 * Constrói o objeto
	 */
	public ProcessarDireto() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, true).verificaAcesso(false))
			return;
		
		try {
			processaDireto(request, response);
		}
		catch(Exception err) {
			logger.error("Erro ao processar direto.", err);
			return;
		}
		
	}  	

	private void processaDireto(HttpServletRequest request, final HttpServletResponse response) throws ServletException, ConexaoException, SQLException, IOException, InterruptedException {
		final Processamento proc = (Processamento) request.getSession().getAttribute("[processamento]");
		
		if (proc == null)
			throw new ServletException("Sessão finalizada.");
		
		Integer idProgresso = proc.hashCode();
		
		final Progresso p = ProgressoProvider.getInstance().criaProgresso(request.getSession(), idProgresso);
		p.setStatus("Iniciando...");
		enviaIdProgresso(response, idProgresso);

		//Joga em outra thread, pois existe o mecanismo de andamento do progresso!
		Thread th = new Thread(new Runnable() {
			public void run() {
				try {
					logger.debug("Semáforo: aquis.");
					JobProcessaInfracaoAgendamento.semaforoExecucao.acquire();
					proc.processaDireto(new AcaoAdapter() {
						public void acaoExecutada(AcaoEvento evt) {
							
							if (evt.getTipo() instanceof String)
								p.setStatus((String)evt.getTipo());
							else if (evt.getTipo() instanceof Integer) {
								p.setProgresso(p.getProgresso()+(Integer)evt.getTipo());
								p.setStatus("Processado: "+String.valueOf(p.getProgresso()));
							}
							else
								System.out.println(evt.getTipo());
							
							if ((System.currentTimeMillis() - dataIni) > 1000) {//Somente envia 1 vez por segundo.
								dataIni = System.currentTimeMillis();
							}
						}
					});
					p.terminado();
				} 
				catch (Exception e) {
					p.setStatus("[ERRO]: "+e.getMessage());
					logger.warn("Erro ao processa direto: "+e.getMessage(), e);
				}
				finally {
					logger.debug("Semáforo: relis.");
					JobProcessaInfracaoAgendamento.semaforoExecucao.release();
				}
			}
		});
		th.start();
	}
	
	private void enviaIdProgresso(HttpServletResponse response, Integer idProgresso) {
		AjaxXMLConstr xml = null;
		try {
			xml = new AjaxXMLConstr("andamento");
			xml.adicCampo("ID_PROGRESSO", String.valueOf(idProgresso));
			xml.dump(response);	
		}
		catch (Exception e) {
			logger.warn("Não foi possível entragar o status ao cliente.", e);
		}
	}
}
