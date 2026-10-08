package com.consilux.infra;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Listener para contar as sessões ativas.
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */
public class SessoesAtivas implements HttpSessionListener {
	
	private static int sessoesAtivas = 0;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent se) {
		sessoesAtivas++;
		se.getSession().setAttribute("{SessaoFinalizaManager}", new SessaoFinalizaManager(se.getSession()));
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent se) {
		Object o = se.getSession().getAttribute("{SessaoFinalizaManager}");
		if (o != null && o instanceof SessaoFinalizaManager)
			((SessaoFinalizaManager) o).doFinaliza();
		
		if(sessoesAtivas > 0)
			sessoesAtivas--;
	}

	/**
	 * @return the sessoesAtivas
	 */
	public static int getSessoesAtivas() {
		return sessoesAtivas;
	}
}
