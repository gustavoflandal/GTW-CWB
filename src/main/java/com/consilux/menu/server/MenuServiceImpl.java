package com.consilux.menu.server;

import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.SessaoConstantes;
import com.consilux.model.Menu;
import com.consilux.model.beans.MenuBean;
import com.consilux.servlet.gwt.GwtBaseServlet;
import com.consilux.menu.client.MenuService;
import com.consilux.menu.client.beans.MenuGwtBean;
//import com.consilux.ui.client.beans.MenuUrlGwtBean;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class MenuServiceImpl extends GwtBaseServlet implements MenuService {

	private static final long serialVersionUID = -4374946927304779848L;

	@Override
	public MenuGwtBean buscarMenuPorIdMenu(int idMenu) throws Exception {

		verificarSessaoLogada();

		throw new Exception("Método ainda não foi implementado: buscarMenuPorIdMenu");
		
	}

	@Override
	public List<MenuGwtBean> listarTodosMenus() throws Exception {
		verificarSessaoLogada();

		try {
			return Menu.toGwtBean(Menu.buscaTodosMenus());
		} catch (Exception ex) {
			throw new Exception(ex.getLocalizedMessage());
		}
			
	}	
	
	@Override
	public List<MenuGwtBean> listarMenusPorNivel(short nivelMenus)
			throws Exception {

		verificarSessaoLogada();
		
		List<MenuGwtBean> menus = Menu.toGwtBean(Menu.buscaTodosMenus());
		return buildMenuTree(menus, 0, null);		
	}

	@Override
	public List<MenuGwtBean> listarSubmenusDoMenu(int idMenu) throws Exception {
		
		verificarSessaoLogada();

		throw new Exception("Método ainda não foi implementado: listarSubmenusDoMenu");
		
	}

	public class Predicado implements Predicate<MenuGwtBean> {
		
		private int nivel;
		private Integer idPai = null;
		private boolean permiteNulos = false;
		
		public Predicado(int nivel, Integer idPai, boolean permiteNulos) {
			this.nivel = nivel;
			this.idPai = idPai;
			this.permiteNulos = permiteNulos;
		}

		@Override
		public boolean apply(MenuGwtBean input) {
			return (input.getNivel() == nivel) && (permiteNulos ||
				   (input.getIdMenuPai() != null && input.getIdMenuPai().equals(idPai)));
		}
	}
	
	public List<MenuGwtBean> buildMenuTree(Iterable<MenuGwtBean> menuEntries, int level, Integer idPai){
		Iterable<MenuGwtBean> menus = Iterables.filter(menuEntries, new Predicado(level, idPai, (level == 0) ));
		
		for (MenuGwtBean subMenuGwtBean : menus) {
			subMenuGwtBean.setSubMenus(buildMenuTree(menuEntries, level + 1, subMenuGwtBean.getIdMenu()));
		}
		return Lists.newArrayList(menus);
	}
	
	@Override
	public List<MenuGwtBean> obterMenusDoUsuario() throws Exception {
		
		List<MenuGwtBean> lRet;
		
		if (isEmSessao()) {
			lRet = super.getMenusUsuario();
			
			if (lRet == null) {
				List<MenuGwtBean> listaMenus = Menu.toGwtBean(Menu.buscaMenusPorIdUsuario(super.getIdUsuario()));
				lRet = buildMenuTree(listaMenus, 0, null);
		        super.getSession().setAttribute(SessaoConstantes.SESSAO_MENUS, lRet);
			}
		} else {
			lRet = new ArrayList<MenuGwtBean>(0);
		}
		
		return lRet;
	}

	@Override
	public MenuGwtBean salvarMenu(MenuGwtBean gwtBean) throws Exception {
		
		if (gwtBean == null)
			throw new Exception("Não é possível salvar um menu nulo.");

		if (gwtBean.getNome() == null || gwtBean.getNome().length() == 0)
			throw new Exception("Não é possível salvar um menu sem nome.");

		if (gwtBean.getDescricao() == null || gwtBean.getDescricao().length() == 0)
			throw new Exception("Não é possível salvar um menu sem descrição.");

		if (gwtBean.getTipo() == null)
			throw new Exception("Não é possível salvar um menu sem tipo.");		

//		for (MenuUrlGwtBean menuUrl : gwtBean.getListaUrls()) {
//			if (menuUrl.getUrl() == null || menuUrl.getUrl().length() == 0) {
//				throw new Exception("Não é possível salvar um menu\r\ncontendo uma URL vazia.");		
//			}
//		}
		
		try {
			
			MenuBean menuBean = Menu.toMenuBean(gwtBean);
			
			// Adiciona ou atualiza o menu, dependendo do caso
			if (menuBean.getId() == 0) {
				menuBean = Menu.inserirMenu(menuBean);
			} else {
				menuBean = Menu.atualizarMenu(menuBean);
			}
			
			return Menu.toGwtBean(menuBean);
			
		} catch (Exception ex) {
			throw new Exception("Erro ao salvar o menu.");
		}
	}

}
