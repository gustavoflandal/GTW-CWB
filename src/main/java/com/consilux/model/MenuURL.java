/**********************************************************************************

  Projeto: GTW

  Nome do Modulo: com.consilux.model

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 26/06/2009

  Descricao: Classe que controla o acesso dos menus por URL.

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.MenuUrlGwtBean;

/**
 * XXX
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public class MenuURL {
	
	private int idMenu;
	private int idURL;
	private String descricao;
	private String URL;
	/**
	* Constrói o objeto MenuURL a partir dos parâmetros dados.
	* @param idMenu
	* @param idURL
	* @param descricao
	* @param uRL
	*/
	private MenuURL(int idMenu, int idURL, String descricao, String uRL) {
		this.idMenu = idMenu;
		this.idURL = idURL;
		this.descricao = descricao;
		this.URL = uRL;
	}
	
	/**
	 * Busca url no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: url.
	 * @return Lista de objetos MenuURL
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static List<MenuURL> buscaLocalPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException {
		List<MenuURL> lRet = new ArrayList<MenuURL>();
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("SELECT id_menu,");
		sbSQL.append("		id_url,");
		sbSQL.append("		descricao,");
		sbSQL.append("		url");
		sbSQL.append("	FROM");
		sbSQL.append("		sis_menu_url");
		sbSQL.append("	WHERE ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			mRegras.put("url", "url LIKE ?");
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			rs = ps.executeQuery();
			String descricao;
			
			while (rs.next()) {
				
				descricao = rs.getString("descricao");
				
				lRet.add(new MenuURL(
						rs.getInt("id_menu"),
						rs.getInt("id_url"),
						descricao != null ? descricao.trim() :descricao,
						rs.getString("url").trim()
					)
				);
			}
		} catch (ModelException e) {
			throw new SQLException("Erro ao montar SQL.", e);
		}				
		finally {
				if (conn != null)
					conn.close();							
		}

		return lRet;
	}

	
	/**
	 * Vefirica se o usuário possui acesso na URL dada.
	 * @param mFiltros Filtros para a busca, regras implementadas: url.
	 * @return Lista de objetos MenuURL
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static Boolean verificaAcesso(Integer idUsuario, String url) throws ConexaoException, SQLException {
		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT top 1 u.id_menu ");
		sbSQL.append("	FROM sis_menu_url u"); 
		sbSQL.append("	JOIN sis_menu_direitos d ON d.id_menu = u.id_menu ");
		sbSQL.append("WHERE");
		sbSQL.append("	? LIKE u.url AND (");
		sbSQL.append("	d.id_usuario = ? OR");
		sbSQL.append("	d.id_grupo in (");
		sbSQL.append("		SELECT id_grupo");
		sbSQL.append("			FROM sis_usuario_grupo"); 
		sbSQL.append("		WHERE id_usuario = ?)");
		sbSQL.append("	)");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, url);
			ps.setInt(2, idUsuario);
			ps.setInt(3, idUsuario);

			rs = ps.executeQuery();
			bRet = rs.next();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return bRet;
	}

	
	/**
	 * Retorna o valor do campo 'idMenu' atual.
	 * @return the idMenu
	 */
	public int getIdMenu() {
		return this.idMenu;
	}
	/**
	 * Retorna o valor do campo 'idURL' atual.
	 * @return the idURL
	 */
	public int getIdURL() {
		return this.idURL;
	}
	/**
	 * Retorna o valor do campo 'descricao' atual.
	 * @return the descricao
	 */
	public String getDescricao() {
		return this.descricao;
	}
	/**
	 * Retorna o valor do campo 'URL' atual.
	 * @return the uRL
	 */
	public String getURL() {
		return this.URL;
	}
	
	public void setIdMenu(int idMenu) {
		this.idMenu = idMenu;
	}

	public void setIdURL(int idURL) {
		this.idURL = idURL;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setURL(String url) {
		URL = url;
	}

	/**
	 * Obtém a lista de MenuURL de um dado menu.
	 * @param idMenu
	 * @return A lista de MenuURL de um dado menu.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<MenuURL> obterUrlsPorIdMenu(int idMenu)
	throws ConexaoException, SQLException {
		
		List<MenuURL> lRet = new ArrayList<MenuURL>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("select id_url, descricao, url from sis_menu_url where id_menu = ?");
			ps.setInt(1, idMenu);
			rs = ps.executeQuery();
			
			String descricao;
			
			while (rs.next()) {
				
				descricao = rs.getString(2);
				
				lRet.add(new MenuURL(
						idMenu,
						rs.getInt(1),
						descricao != null ? descricao.trim() : descricao,
						rs.getString(3).trim()
					)
				);
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return lRet;		
	}
	
	/**
	 * Converte um menu-url em um bean do GWT.
	 * @return Um bean de menu do GWT.
	 * @throws ModelException 
	 */		
	public static MenuUrlGwtBean toGwtBean(MenuURL menuUrl)
	throws ModelException {
	
		if (menuUrl == null)
			throw new ModelException("Argumento nulo: menuUrl");
			
		return new MenuUrlGwtBean(
				menuUrl.getIdMenu(),
				menuUrl.getIdURL(),
				menuUrl.getURL(),
				menuUrl.getDescricao());
	}
	
	/**
	 * Converte uma coleção de beans do GTW para uma lista de bean do GWT.
	 * @param listaMenus Uma coleção de beans de menu.
	 * @return Uma lista de bean de menu-url do GWT.
	 * @throws ModelException
	 */	
	public static List<MenuUrlGwtBean> toGwtBean(Iterable<MenuURL> listaMenuUrl)
	throws ModelException {
		
		if (listaMenuUrl == null)
			throw new ModelException("Argumento nulo: listaMenuUrl");
		
		List<MenuUrlGwtBean> lRet = new ArrayList<MenuUrlGwtBean>();
		MenuUrlGwtBean gwtBean = null;
		for (MenuURL mnu : listaMenuUrl) {
			gwtBean = toGwtBean(mnu);
			lRet.add(gwtBean);
		}
		return lRet;
			
	}

	public static List<MenuURL> toMenuURL(Iterable<MenuUrlGwtBean> listaGwtBean)
	throws ModelException {
		
		if (listaGwtBean == null)
			throw new ModelException("Argumento nulo: listaGwtBean");
		
		List<MenuURL> lRet = new ArrayList<MenuURL>();
		
		for (MenuUrlGwtBean menuUrl : listaGwtBean) {
			lRet.add(toMenuURL(menuUrl));
		}
		
		return lRet;
	}
	
	public static MenuURL toMenuURL(MenuUrlGwtBean gwtBean)
	throws ModelException {
		
		if (gwtBean == null)
			throw new ModelException("Argumento nulo: gwtBean");
		
		MenuURL menuURL = new MenuURL(
				gwtBean.getIdMenu(),
				gwtBean.getId(),
				gwtBean.getDescricao(),
				gwtBean.getUrl()
			);
		return menuURL;
	}	
	
	public static void inserirMenuURL(Connection conn, MenuURL menuUrl)
	throws ModelException, SQLException {

		if (menuUrl == null)
			throw new ModelException("Argumento nulo: menuUrl");

		if (conn == null)
			throw new ModelException("Argumento nulo: conn");				

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" INSERT INTO sis_menu_url (");
		sbSQL.append(" id_menu, id_url, descricao, url)");
		sbSQL.append(" VALUES (?,?,?,?)");

		PreparedStatement ps = null;
		ResultSet rs = null;			
		
		try {
			
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, menuUrl.getIdMenu());
			ps.setInt(2, menuUrl.getIdURL());
			
			if (menuUrl.getDescricao() != null) {
				ps.setString(3, menuUrl.getDescricao());
			} else {
				ps.setNull(3, Types.CHAR);
			}
			
			ps.setString(4, menuUrl.getURL());

			ps.executeUpdate();
			
		}
		finally {
			if (rs != null)					
				rs.close();
			if (ps != null)
				ps.close();
		}	
		
	}

	
	public static int removerUrlsDoMenu(Connection conn, int idMenu)
	throws SQLException {
	
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement("DELETE FROM sis_menu_url where id_menu = ?");
			ps.setInt(1, idMenu);
			return ps.executeUpdate();
		}
		finally {
			if (ps != null)
				ps.close();
		}
	}
	
	
	public static void atualizarMenuURL(Connection conn, MenuURL menuUrl)
	throws ModelException, SQLException {

		if (menuUrl == null)
			throw new ModelException("Argumento nulo: menuUrl");

		if (conn == null)
			throw new ModelException("Argumento nulo: conn");				

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" UPDATE sis_menu_url SET ");
		sbSQL.append(" descricao = ?, url = ?");
		sbSQL.append(" WHERE id_menu = ? AND id_url = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;			
		
		try {
			
			ps = conn.prepareStatement(sbSQL.toString());


			if (menuUrl.getDescricao() != null) {
				ps.setString(1, menuUrl.getDescricao());
			} else {
				ps.setNull(1, Types.CHAR);
			}
			
			ps.setString(2, menuUrl.getURL());

			ps.setInt(3, menuUrl.getIdMenu());
			ps.setInt(4, menuUrl.getIdURL());

		}
		finally {
			if (rs != null)					
				rs.close();
			if (ps != null)
				ps.close();
		}
		
	}
}
