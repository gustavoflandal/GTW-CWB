/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 10/01/2007

  Descricao: Classe para busca de menus no BD.

  Historico:

    $Log: Menu.java,v $
    Revision 1.14  2009/04/22 14:57:32  raoni
    Adicionado funções de busca no banco e de conversão (para beans do GWT).

    Revision 1.13  2009/04/20 19:37:35  raoni
    Modificado campo nivel para short (WORD) para ficar compativel com o bean do GWT (no banco é smallint). Adicionado método e conversão para o bean do GWT.

    Revision 1.12  2009/04/13 20:21:25  raoni
    Adicionado ORDER BY, no id_menu, para evitar problemas de criação no javascript.

    Revision 1.11  2009/03/12 13:07:38  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.10  2009/03/10 20:00:21  raoni
    Otimizado o uso de StringBuilder.
    Utiliza LinkedList quando possível.
    Adicionado lógica para realizar o clean up (close) dos Statements.

    Revision 1.9  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.7  2008/01/17 11:11:15  fernando
    Criado método buscaMenuPorIdMenu, e alterado o buscaMenus por buscaMenusPorIdUsuario

    Revision 1.6  2007/12/14 12:45:33  fernando
    Adaptado para nova estrutura do banco de dados

    Revision 1.5  2007/07/06 13:18:16  fos
    Acerto de documentação.

    Revision 1.4  2007/07/06 13:04:54  fos
    Colocado unlocks explicitos nas querys.

    Revision 1.3  2007/05/04 13:57:03  fos
    Agora carrega os menus seguindo regras de acesso por grupo.

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.MenuBean;
import com.consilux.model.exception.ModelException;
import com.consilux.menu.client.beans.MenuGwtBean;

/**
 * Classe para busca de menus no BD.
 * @author Fernando Oliveira da Silva / Fernando de Souza - Consilux Tecnologia
 * @version $Revision: 1.14 $ $Date: 2009/04/22 14:57:32 $ $Author: raoni $
 */
public class Menu implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private Integer idPai;
	private short nivel;
	private String menu;
	private String descricao;
	private String tipo;
	private String acao;
	
	public Menu(int id, int idPai, short nivel, String menu, String descricao,
			String tipo, String acao) {
		
		super();
		
		this.id = id;
		this.idPai = idPai;
		this.nivel = nivel;
		this.menu = menu != null ? menu.trim() : null;
		this.descricao = descricao != null ? descricao.trim() : null;
		this.tipo = tipo != null ? tipo.trim() : null;
		this.acao = acao != null ? acao.trim() : null;
	}


	public Menu(MenuBean bean) {
		super();
		this.id = bean.getId();
		this.idPai = bean.getIdPai();
		this.nivel = bean.getNivel();
		this.menu = bean.getMenu();
		this.descricao = bean.getDescricao();
		this.tipo = bean.getTipo();
		this.acao = bean.getAcao();
	}


	public static List<MenuBean> buscaMenusPorIdUsuario(Integer idUsuario)
	throws ConexaoException, SQLException  {
		
		List<MenuBean> lRet = new ArrayList<MenuBean>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("  SELECT 	");
		sbSQL.append("		id_menu,");
		sbSQL.append("		descricao,");
		sbSQL.append("		acao,");
		sbSQL.append(" 		tipo,");
		sbSQL.append(" 		nivel,");
		sbSQL.append(" 		id_pai_menu, ");
		sbSQL.append("		menu");
		sbSQL.append("  FROM ");
		sbSQL.append("		sis_menu WITH (NOLOCK)");
		sbSQL.append("  WHERE ");
		sbSQL.append("	id_menu IN (");
		sbSQL.append("	SELECT id_menu	");
		sbSQL.append("	FROM sis_menu_direitos WITH (NOLOCK) ");
		sbSQL.append("	WHERE id_usuario = ? ");
		sbSQL.append("	OR id_grupo IN (");
		sbSQL.append("		SELECT id_grupo");
		sbSQL.append("		FROM sis_usuario_grupo WITH (NOLOCK) ");
		sbSQL.append("		WHERE id_usuario = ?)");
		sbSQL.append("	) ORDER BY id_menu");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1, idUsuario);
			ps.setInt(2, idUsuario);
			
			rs = ps.executeQuery();
			MenuBean menuBean = null;
			while (rs.next()) {
				menuBean = new MenuBean(
									rs.getInt("id_menu"),
									rs.getInt("id_pai_menu"),
									rs.getShort("nivel"),
									rs.getString("menu"),
									rs.getString("descricao"),
									rs.getString("tipo"),
									rs.getString("acao")
							   );
				if (menuBean.getIdPai() == 0){
					menuBean.setIdPai(null);
				}
				
				// Comentado, pois por enquanto não é realizuada verificação com base no menuURL 
				// menuBean.getListaURL().addAll(MenuURL.obterUrlsPorIdMenu(menuBean.getId()));
				lRet.add(menuBean);
				
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}		
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
		return lRet;
	}

	public static Menu buscaMenuPorIdMenu(Integer idMenu)
	throws ConexaoException, SQLException  {
		
		Menu mRet = null;
		StringBuilder sbSQL = new StringBuilder();

		
		sbSQL.append("  SELECT 	");
		sbSQL.append("		id_menu,");
		sbSQL.append("		descricao,");
		sbSQL.append("		acao,");
		sbSQL.append(" 		tipo,");
		sbSQL.append(" 		nivel,");
		sbSQL.append(" 		id_pai_menu, ");
		sbSQL.append("		menu");
		sbSQL.append("  FROM ");
		sbSQL.append("		sis_menu WITH (NOLOCK)");
		sbSQL.append("  WHERE ");
		sbSQL.append("	id_menu = ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
	
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1, idMenu);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				mRet = new Menu(
							rs.getInt("id_menu"),
							rs.getInt("id_pai_menu"),
							rs.getShort("nivel"),
							rs.getString("menu"),
							rs.getString("descricao"),
							rs.getString("tipo"),
							rs.getString("acao")
						);
			}
		}
		finally {
			if (rs != null)					
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}
		
		return mRet;
	}

	public static Menu incluiUsuario(MenuBean bean)
	throws ConexaoException, SQLException {
		
		Menu Ret = null; 
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("  INSERT INTO sis_menu	(");
		sbSQL.append("		descricao,");
		sbSQL.append("		acao,");
		sbSQL.append(" 		tipo,");
		sbSQL.append(" 		nivel,");
		sbSQL.append(" 		id_pai_menu, ");
		sbSQL.append("		menu");
		sbSQL.append("  ) VALUES (?,?,?,?,?,?)");

		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, bean.getDescricao());
			ps.setString(2, bean.getAcao());
			ps.setString(3, bean.getTipo());
			ps.setInt(4, bean.getNivel());
			ps.setInt(5, bean.getIdPai());
			ps.setString(6, bean.getMenu());
			
			if (ps.executeUpdate() > 0) {
				Ret = new Menu(bean);
			}
		}	
		finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}
		
		return Ret;
	}

	public int getId() {
		return id;
	}

	public int getIdPai() {
		return idPai;
	}

	public int getNivel() {
		return nivel;
	}

	public String getMenu() {
		return menu;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getTipo() {
		return tipo;
	}

	public String getAcao() {
		return acao;
	}
	
	/**
	 * Busca todos os menus do BD.
	 * @return Uma lista de objeto MenuBean materializados.
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static List<MenuBean> buscaTodosMenus()
	throws ConexaoException, SQLException {
		
		List<MenuBean> lRet = new ArrayList<MenuBean>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("  SELECT 	");
		sbSQL.append("		id_menu,");
		sbSQL.append("		descricao,");
		sbSQL.append("		acao,");
		sbSQL.append(" 		tipo,");
		sbSQL.append(" 		nivel,");
		sbSQL.append(" 		id_pai_menu, ");
		sbSQL.append("		menu");
		sbSQL.append("  FROM ");
		sbSQL.append("		sis_menu WITH (NOLOCK)");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
	
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			MenuBean menuBean = null;
			while (rs.next()) {
				menuBean = new MenuBean(
									rs.getInt("id_menu"),
									rs.getInt("id_pai_menu"),
									rs.getShort("nivel"),
									rs.getString("menu"),
									rs.getString("descricao"),
									rs.getString("tipo"),
									rs.getString("acao")
							   );

				menuBean.getListaURL().addAll(
						MenuURL.obterUrlsPorIdMenu(menuBean.getId())
					);
				
				if (menuBean.getIdPai() == 0){
					menuBean.setIdPai(null);
				}
				lRet.add(menuBean);
				
			}
		}
		finally {
			if (rs != null)					
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
		return lRet;
	}		
	
	/**
	 * Converte um menu em um bean do GWT.
	 * @param menuBean O menu que se deseja converter.
	 * @return Um bean de menu do GWT.
	 * @throws ModelException caso o argumento passado seja nulo.
	 */	
	public static MenuGwtBean toGwtBean(MenuBean menuBean)
	throws ModelException {
		
		if (menuBean == null)
			throw new ModelException("Argumento nulo: menuBean");
		
		MenuGwtBean gwtBean = new MenuGwtBean();
		String acao = menuBean.getAcao();
		gwtBean.setAcao(acao != null ? acao.trim() : "");
		gwtBean.setDescricao(menuBean.getDescricao().trim());
		gwtBean.setIdMenu(menuBean.getId());
		gwtBean.setIdMenuPai(menuBean.getIdPai());
		gwtBean.setNivel(menuBean.getNivel());
		gwtBean.setNome(menuBean.getMenu().trim());
		gwtBean.setTipo(menuBean.getTipo().trim());
		
//		gwtBean.getListaUrls().addAll(MenuURL.toGwtBean(menuBean.getListaURL()));
		
		return gwtBean;
	}

	/**
	 * Converte este menu em um bean do GWT.
	 * @return Um bean de menu do GWT.
	 */	
	public MenuGwtBean toGwtBean() throws ModelException {
		
		MenuGwtBean gwtBean = new MenuGwtBean();
		gwtBean.setAcao(acao != null ? acao.trim() : "");
		gwtBean.setDescricao(descricao.trim());
		gwtBean.setIdMenu(id);
		gwtBean.setIdMenuPai(idPai);
		gwtBean.setNivel(nivel);
		gwtBean.setNome(menu.trim());
		gwtBean.setTipo(tipo.trim());
		
		return gwtBean;
	}

	/**
	 * Converte uma coleção de beans do GTW para uma lista de bean do GWT.
	 * @param listaMenus Uma coleção de beans de menu.
	 * @return Uma lista de bean de menu do GWT.
	 * @throws ModelException
	 */
	public static List<MenuGwtBean> toGwtBean(Iterable<MenuBean> listaMenus) throws ModelException {
		
		if (listaMenus == null)
			throw new ModelException("Argumento nulo: listaMenus");
		
		List<MenuGwtBean> lRet = new ArrayList<MenuGwtBean>();
		MenuGwtBean gwtBean = null;
		for (MenuBean mnu : listaMenus) {
			gwtBean = toGwtBean(mnu);
			lRet.add(gwtBean);
		}
		return lRet;
		
	}	
	
	public static MenuBean atualizarMenu(MenuBean menuAtualizar)
	throws ModelException, ConexaoException, SQLException {
			
		Connection conn = null;
		
		try {
			
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			atualizarMenu(conn, menuAtualizar);
			conn.commit();
			return menuAtualizar;
				
		} catch (SQLException ex) {
			conn.rollback();
			throw ex;
		} catch (ModelException ex) {
			conn.rollback();
			throw ex;
		} catch (Exception ex) {
			conn.rollback();
			throw new ModelException("Erro não esperado.", ex);
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}	
	
	public static void atualizarMenu(Connection conn, MenuBean menuAtualizar)
	throws ModelException, SQLException {
		
		if (menuAtualizar == null)
			throw new ModelException("Argumento nulo: menuAtualizar");

		if (conn == null)
			throw new ModelException("Argumento nulo: conn");		
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" UPDATE sis_menu ");
		sbSQL.append(" SET descricao = ?, acao = ?, tipo = ?,");
		sbSQL.append(" nivel = ?, id_pai_menu = ?, menu = ? ");
		sbSQL.append(" WHERE id_menu = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;			
		
		try {
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, menuAtualizar.getDescricao());
			
			if (menuAtualizar.getAcao() != null) {
				ps.setString(2, menuAtualizar.getAcao());
			} else { 
				ps.setNull(2, Types.CHAR);
			}
			
			ps.setString(3, menuAtualizar.getTipo());
			ps.setShort(4, menuAtualizar.getNivel());
			
			if (menuAtualizar.getIdPai() != null) {
				ps.setInt(5, menuAtualizar.getIdPai());						
			} else {
				ps.setNull(5, Types.INTEGER);
			}			
			
			ps.setString(6, menuAtualizar.getMenu());
			ps.setInt(7, menuAtualizar.getId());
			
			ps.executeUpdate();
			
			MenuURL.removerUrlsDoMenu(conn, menuAtualizar.getId());
			
			// Ajusta lista-urls
			int id_url = 1;
			for (MenuURL menuUrl : menuAtualizar.getListaURL()) {
				
				if (menuUrl.getIdMenu() == 0)
					menuUrl.setIdMenu(menuAtualizar.getId());
				
				menuUrl.setIdURL(id_url);
				MenuURL.inserirMenuURL(conn, menuUrl);
				id_url++;
			}
			
		}
		finally {
			if (rs != null)					
				rs.close();
			if (ps != null)
				ps.close();
		}	
	}
	
	public static MenuBean inserirMenu(MenuBean novoMenu)
	throws ModelException, ConexaoException, SQLException {
		
		Connection conn = null;
		
		try {
			
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			MenuBean beanNoBanco = inserirMenu(conn, novoMenu);
			conn.commit();
			return beanNoBanco;
			
		} catch (SQLException ex) {
			conn.rollback();
			throw ex;
		} catch (ModelException ex) {
			conn.rollback();
			throw ex;
		} catch (Exception ex) {
			conn.rollback();
			throw new ModelException("Erro não esperado.", ex);
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	public static MenuBean inserirMenu(Connection conn, MenuBean novoMenu)
	throws ModelException, SQLException {
		
		if (novoMenu == null)
			throw new ModelException("Argumento nulo: novoMenu");

		if (conn == null)
			throw new ModelException("Argumento nulo: conn");		
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" INSERT INTO sis_menu (");
		sbSQL.append(" descricao, acao, tipo, nivel, id_pai_menu, menu) ");
		sbSQL.append(" VALUES (?,?,?,?,?,?)");

		PreparedStatement ps = null;
		ResultSet rs = null;			
		
		try {
			ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, novoMenu.getDescricao());
			
			if (novoMenu.getAcao() != null) {
				ps.setString(2, novoMenu.getAcao());
			} else { 
				ps.setNull(2, Types.CHAR);
			}
			
			ps.setString(3, novoMenu.getTipo());
			ps.setShort(4, novoMenu.getNivel());
			
			if (novoMenu.getIdPai() != null) {
				ps.setInt(5, novoMenu.getIdPai());						
			} else {
				ps.setNull(5, Types.INTEGER);
			}
			
			ps.setString(6, novoMenu.getMenu());
						
			rs = ps.executeQuery();
			
			// Recupera a chave (identity)
			rs.next();
			novoMenu.setId(rs.getInt(1));
			
			MenuURL.removerUrlsDoMenu(conn, novoMenu.getId());
			
			// Ajusta lista-urls
			int id_url = 1;
			for (MenuURL menuUrl : novoMenu.getListaURL()) {
				
				if (menuUrl.getIdMenu() == 0)
					menuUrl.setIdMenu(novoMenu.getId());
				
				menuUrl.setIdURL(id_url);
				MenuURL.inserirMenuURL(conn, menuUrl);
				id_url++;
			}
			
			// Verifica sub-menus
			for (MenuBean subMenu : novoMenu.getListaSubMenu()) {
				
				if (subMenu.getIdPai() == null)
					subMenu.setIdPai(novoMenu.getId());
				
				if (subMenu.getId() == 0)
					inserirMenu(conn, subMenu);
				else
					atualizarMenu(conn, subMenu);
			}
			
		}
		finally {
			if (rs != null)					
				rs.close();
			if (ps != null)
				ps.close();
		}			
		
		return novoMenu;
	}
	
	public static MenuBean toMenuBean(MenuGwtBean gwtBean) throws ModelException {
		
		if (gwtBean == null)
			throw new ModelException("Argumento nulo: novoMenu");
		
		// Converte o bean
		MenuBean menuBean = new MenuBean(
				gwtBean.getIdMenu(),
				gwtBean.getIdMenuPai(),
				gwtBean.getNivel(),
				gwtBean.getNome(),
				gwtBean.getDescricao(),
				gwtBean.getTipo(),
				gwtBean.getAcao()
			);
		
		// Converte os objetos menu-url
//		menuBean.getListaURL().addAll(MenuURL.toMenuURL(
//			gwtBean.getListaUrls()));
//		
		// Converte os sub-menus
		for (MenuGwtBean subMenu : gwtBean.getSubMenus()) {
			menuBean.getListaSubMenu().add(toMenuBean(subMenu));
		}
		
		return menuBean;
	}

}
