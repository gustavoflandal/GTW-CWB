/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.infra

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 09/06/2010

  Descricao: Administrador de eventos de sessão finalizada.

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.infra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 * Administrador de eventos de sessão finalizada.
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public class SessaoFinalizaManager {
	private HttpSession sessao;
	private static Logger logger = Logger.getLogger(SessaoFinalizaManager.class); 	
	private List<SessaoFinaliza> listaSf = Collections.synchronizedList(new ArrayList<SessaoFinaliza>());

	/**
	* Constrói o objeto SessaoFinalizaManager a partir dos parâmetros dados.
	* @param sessao
	*/
	protected SessaoFinalizaManager(HttpSession sessao) {
		this.sessao = sessao;
	}
	
	private void addSessaoFinaliza(SessaoFinaliza sf) {
		listaSf.add(sf);	
	}
	
	protected void doFinaliza() {
		synchronized(listaSf) {
			for (SessaoFinaliza sf : listaSf) {
				try {
					sf.doFinaliza(sessao);
				}
				catch (Exception e) { 
					logger.error("Erro ao disparar evento ao finalizar sessão.", e);
				}
			}
		}
	}
	/**
	 * Adiciona um evento SessaoFinaliza, para ser chamado quando a sessão encerrar.
	 * @param sessao Sessão qual será adicionado o evento.
	 * @param sf Evento.
	 * @return True se conseguiu adicionar o evento, False se não existe um SessaoFinalizaManager na sessão.
	 */
	public static Boolean adicSessaoFinaliza(HttpSession sessao, SessaoFinaliza sf) {
		Boolean bRet = false;
		Object o = sessao.getAttribute("{SessaoFinalizaManager}");
		if (o != null && o instanceof SessaoFinalizaManager) {
			((SessaoFinalizaManager) o).addSessaoFinaliza(sf);	
			bRet = true;
		}
		return bRet;
	}
	/**
	 * Remove os eventos SessaoFinaliza evitando se chamado 2x.
	 * @param sessao Sessão qual serão removidos os eventos.
	 */
	public static void removeSessaoFinaliza(HttpSession sessao) {
		Object o = sessao.getAttribute("{SessaoFinalizaManager}");
		if (o != null && o instanceof SessaoFinalizaManager) {
			((SessaoFinalizaManager) o).doFinaliza();
			sessao.removeAttribute("{SessaoFinalizaManager}");
		}
		
	}
}
