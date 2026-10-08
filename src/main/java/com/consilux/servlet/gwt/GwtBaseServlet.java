package com.consilux.servlet.gwt;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.consilux.infra.SessaoConstantes;
import com.consilux.model.Usuario;
import com.consilux.menu.client.beans.MenuGwtBean;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Classe base para todas as implementações dos serviços do GWT.
 * Oferece uma ponte para se obter de maneira fácil a sesison,
 * a request e informações a respeito do usuário.
 * @author raoni
 */
public abstract class GwtBaseServlet extends RemoteServiceServlet {

	private static final long serialVersionUID = -5112799946415650764L;

	/**
	 * Recupera a request para este serviço. 
	 * @return A request que está sendo utilizada.
	 */
	public final HttpServletRequest getRequest() {
		return getThreadLocalRequest();
	}

	/**
	 * Recupera (mas não cria) a sessão deste serviço. 
	 * @return A session que está sendo utilizada.
	 */
	public HttpSession getSession() {
		return getThreadLocalRequest().getSession(false);
	}

	/**
	 * Cria (ou recupera) a sessão deste serviço. 
	 * @return a sessão 'atual' ou uma nova sessão, se necessário
	 */
	public HttpSession createSession() {
		return getThreadLocalRequest().getSession(true);
	}
	
	/**
	 * Verifica se o usuário está em uma sessão.
	 * @return true se o usuário está logado, false caso contrário.
	 */
	public final boolean isEmSessao() {
		return (getSession() != null);
	}

	public final boolean isLogado(){
		return ( isEmSessao() ) && ( getIdUsuario() != null );
	}
	
	/**
	 * Recupera o id do usuário logado. 
	 * @return O id do usuário que está logado (ou nulo caso não esteja logado)
	 */
	public final Integer getIdUsuario() {
		Usuario usuario = (Usuario) getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO); 
		return usuario != null ? usuario.getId() : null;
	}	

	/**
	 * Recupera o (USERNAME) do usuário logado
	 * @return O nome do usuário que está logado (ou nulo caso não esteja logado)
	 */
	public final String getUsuario() {
		Usuario usuario = (Usuario) getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO); 
		return usuario != null ? usuario.getUsuario() : null;
	}

	/**
	 * Recupera os menus do usuário logado
	 * @return uma lista contendo os menus do usuário logado (ou nulo caso não esteja logado).
	 */
	@SuppressWarnings("unchecked")
	public final List<MenuGwtBean> getMenusUsuario() {
		List<MenuGwtBean> lRet = (List<MenuGwtBean>) getSession().getAttribute(SessaoConstantes.SESSAO_MENUS); 
		return lRet;
	}	
	
	protected void verificarSessaoLogada() throws Exception {
		if (!isLogado())
			throw new Exception("Usuário não está logado."); 		
	}
	
	@Override
	protected void checkPermutationStrongName() throws SecurityException {
		return;
	} 
}
