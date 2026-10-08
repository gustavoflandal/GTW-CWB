package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.PermissaoMenuBean;
import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.PermissaoGwtBean;

/**
 * Classe de acesso a dados referentes à permissão de menu.
 * @author raoni
 */
public class PermissaoMenu {

	/**
	 * Busca uma permissão baseado no id informado.
	 * @param idPermissao o id da permissão que se deseja buscar.
	 * @return a permissão desejada ou null, caso não encontre.
	 * @throws ConexaoException
	 */
	public static PermissaoMenuBean buscaPermissaoMenuPorIdPermissao(int idPermissao)
	throws ConexaoException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("   per.id_menu_direito,");
		sbSQL.append("   per.id_menu,");
		sbSQL.append("   per.id_usuario,");
		sbSQL.append("   per.id_grupo");
		sbSQL.append(" FROM");
		sbSQL.append("   sis_menu_direitos per WITH (NOLOCK)");
		sbSQL.append(" WHERE");
		sbSQL.append("   per.id_menu_direito = ?");

		PermissaoMenuBean ret = null;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idPermissao);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				ret = new PermissaoMenuBean(
						rs.getInt("id_menu_direito"),
						rs.getInt("id_menu"),
						rs.getInt("id_usuario"),
						rs.getInt("id_grupo"));
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}
		finally {
			try {
		
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
		
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL" , e);
			}				
		}		
		return ret;
	}
	
	/**
	 * Remmove todas as permissões de menu de um determinado usuário
	 * @param conn Uma conexão de banco de dados. 
	 * @param idUsuario o id do usuário que se deseja remover as permissões.
	 * @throws SQLException
	 */
	public static void removerPermissoesByIdUsuario(Connection conn, int idUsuario)
	throws SQLException {
		PreparedStatement ps = conn.prepareStatement("DELETE FROM sis_menu_direitos WHERE id_usuario = ?");
		ps.setInt(1, idUsuario);
		ps.executeUpdate();
	}
	
	/**
	 * Remmove todas as permissões de menu de um determinado usuário
	 * @param idUsuario o id do usuário que se deseja remover as permissões.
	 * @throws SQLException 
	 * @throws ConexaoException 
	 */
	public static void removerPermissoesByIdUsuario(int idUsuario)
	throws SQLException, ConexaoException {
	
		Connection conn = Conexao.getConexao();
		conn.setAutoCommit(false);
		
		try {
			removerPermissoesByIdUsuario(conn, idUsuario);
			conn.commit();
		} catch (SQLException ex) {
			conn.rollback();
			throw ex;
		}			
		finally {
			conn.close();
		}
	
	}
	
	/**
	 * Busca as permissões de menu de um determinado usuário.
	 * @param idUsuario o id do usuário que se deseja buscar as permissões.
	 * @return uma lista com as permissões do usuário em questão.
	 * @throws ConexaoException
	 */
	public static List<PermissaoMenuBean> buscaPermissoesMenuPorIdUsuario(int idUsuario)
	throws ConexaoException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("   per.id_menu_direito,");
		sbSQL.append("   per.id_menu,");
		sbSQL.append("   per.id_usuario,");
		sbSQL.append("   per.id_grupo");
		sbSQL.append(" FROM");
		sbSQL.append("   sis_menu_direitos per WITH (NOLOCK)");
		sbSQL.append(" WHERE");
		sbSQL.append("   per.id_usuario = ?");

		PermissaoMenuBean bean = null;
		List<PermissaoMenuBean> lRet = new ArrayList<PermissaoMenuBean>();
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idUsuario);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				bean = new PermissaoMenuBean(
						rs.getInt("id_menu_direito"),
						rs.getInt("id_menu"),
						rs.getInt("id_usuario"),
						rs.getInt("id_grupo"));
				lRet.add(bean);
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}
		finally {
			try {
		
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
		
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL" , e);
			}				
		}		
		return lRet;
	}	

	/**
	 * Busca as permissões de menu de um determinado grupo.
	 * @param idGrupo o id do grupo que se deseja buscar as permissões.
	 * @return uma lista com as permissões do grupo em questão.
	 * @throws ConexaoException
	 */
	public static List<PermissaoMenuBean> buscaPermissoesMenuPorIdGrupo(int idGrupo)
	throws ConexaoException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("   per.id_menu_direito,");
		sbSQL.append("   per.id_menu,");
		sbSQL.append("   per.id_usuario,");
		sbSQL.append("   per.id_grupo");
		sbSQL.append(" FROM");
		sbSQL.append("   sis_menu_direitos per WITH (NOLOCK)");
		sbSQL.append(" WHERE");
		sbSQL.append("   per.id_grupo = ?");

		PermissaoMenuBean bean = null;
		List<PermissaoMenuBean> lRet = new ArrayList<PermissaoMenuBean>();
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idGrupo);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				bean = new PermissaoMenuBean(
						rs.getInt("id_menu_direito"),
						rs.getInt("id_menu"),
						rs.getInt("id_usuario"),
						rs.getInt("id_grupo"));
				lRet.add(bean);
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}
		finally {
			try {
		
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
		
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL" , e);
			}				
		}		
		return lRet;
	}	

	/**
	 * Converte uma permissão em um bean do GWT.
	 * @param permissao A permissão que se deseja converter.
	 * @return Um bean de usuário do GWT.
	 * @throws ModelException caso o argumento passado seja nulo.
	 * @throws ModelException caso ocorra um erro de banco de dados. 
	 * @throws SQLException 
	 */	
	public static PermissaoGwtBean toGwtBean(PermissaoMenuBean permissao)
	throws ModelException, ConexaoException, SQLException {
		
		if (permissao == null)
			throw new ModelException("Argumento nulo: permissao");			
		
		PermissaoGwtBean gwtBean = new PermissaoGwtBean();
		gwtBean.setIdPermissao(permissao.getIdPermissao());
		
		Menu menu = Menu.buscaMenuPorIdMenu(permissao.getIdMenu());
		if (menu != null)
			gwtBean.setMenu(menu.toGwtBean());
		
		gwtBean.setIdGrupo(permissao.getIdGrupo());
		gwtBean.setIdUsuario(permissao.getIdUsuario());
		return gwtBean;
	}
	
	/**
	 * Converte uma coleção de beans do GTW para uma lista de bean do GWT.
	 * @param listaPermissao Uma coleção de beans de grupo.
	 * @return Uma lista de bean de permissão do GWT.
	 * @throws ModelException caso o argumento passado seja nulo.
	 * @throws SQLException 
	 */	
	public static List<PermissaoGwtBean> toGwtBean(Iterable<PermissaoMenuBean> listaPermissao)
	throws ModelException, ConexaoException, SQLException {
		
		Map<Integer,Menu> localResults = new TreeMap<Integer,Menu>();
		
		if (listaPermissao == null)
			throw new ModelException("Argumento nulo: listaPermissao");			
		
		PermissaoGwtBean gwtBean = null;
		List<PermissaoGwtBean> lRet = new ArrayList<PermissaoGwtBean>();
		for (PermissaoMenuBean perm : listaPermissao)
		{
			Integer idMenu = perm.getIdMenu();
			
			gwtBean = new PermissaoGwtBean();
			gwtBean.setIdPermissao(perm.getIdPermissao());
			
			Menu menu = null;
			if (!localResults.containsKey(idMenu))
			{
				menu = Menu.buscaMenuPorIdMenu(idMenu);
				if (menu != null)
				{
					localResults.put(idMenu, menu);
				}
			}
			
			if (menu != null)
				gwtBean.setMenu(menu.toGwtBean());

			
			gwtBean.setIdGrupo(perm.getIdGrupo());
			gwtBean.setIdUsuario(perm.getIdUsuario());
			lRet.add(gwtBean);
		}
		return lRet;
	}

	/**
	 * Insere uma permissão de menu no banco de dados.
	 * @param conn Uma conexão de banco de dados.
	 * @param permissaoMenuBean O bean de permissão que se deseja inserir.
	 * @throws ModelException
	 * @throws SQLException 
	 */
	public static void inserir(Connection conn, PermissaoMenuBean permissaoMenuBean)
	throws ModelException, SQLException {
		
		if (permissaoMenuBean == null)
			throw new ModelException("Argumento nulo: permissaoMenuBean");
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("INSERT INTO sis_menu_direitos (");
		sbSQL.append("   id_menu");
		
		if(permissaoMenuBean.getIdUsuario() != null)
			sbSQL.append(", id_usuario");
		
		if(permissaoMenuBean.getIdGrupo() != null)
			sbSQL.append(", id_grupo");
		
		sbSQL.append(") VALUES (?");

		if(permissaoMenuBean.getIdUsuario() != null)
			sbSQL.append(",?");

		if(permissaoMenuBean.getIdGrupo() != null)
			sbSQL.append(",?");
		
		sbSQL.append(")");
		
		PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
		ps.setInt(1, permissaoMenuBean.getIdMenu());
			
		if(permissaoMenuBean.getIdUsuario() != null)
			ps.setInt(2, permissaoMenuBean.getIdUsuario());
		if(permissaoMenuBean.getIdGrupo() != null)
			ps.setInt(3, permissaoMenuBean.getIdGrupo());
		
		ps.executeUpdate();
	}	
	
	/**
	 * Insere uma permissão de menu no banco de dados.
	 * @param permissaoMenuBean O bean de permissão que se deseja inserir.
	 * @throws ModelException
	 * @throws SQLException 
	 * @throws ConexaoException 
	 */
	public static void inserir(PermissaoMenuBean permissaoMenuBean)
	throws ModelException, SQLException, ConexaoException {
		
		if (permissaoMenuBean == null)
			throw new ModelException("Argumento nulo: permissaoMenuBean");			
		
		Connection conn = Conexao.getConexao();
		conn.setAutoCommit(false);
		
		try {
			inserir(conn, permissaoMenuBean);
			conn.commit();
		} catch (SQLException ex) {
			conn.rollback();
			throw ex;
		}			
		finally {
			conn.close();
		}		
	}
}
