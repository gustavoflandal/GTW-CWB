package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.GrupoUsuarioBean;
import com.consilux.model.exception.ModelException;

/**
 * Classe de acesso à dados da associação usuario-grupo.
 * @author raoni
 */
public class GrupoUsuario {

	/**
	 * Insere uma nova associação de usuário-grupo no banco.
	 * @param grupoUsuarioBean O bean que representa a associação.
	 * @throws SQLException
	 * @throws ConexaoException 
	 * @throws ModelException 
	 */
	public static void inserirGrupoUsuario(GrupoUsuarioBean grupoUsuarioBean)
	throws SQLException, ConexaoException, ModelException {

		Connection conn = Conexao.getConexao();
		conn.setAutoCommit(false);

		try {
			inserirGrupoUsuario(conn, grupoUsuarioBean);
			conn.commit();
		} catch (SQLException ex) {
			conn.rollback();
			throw ex;
		} catch (ModelException ex) {
			conn.rollback();
			throw ex;
		}			
		finally {
			conn.close();
		}
	}

	/**
	 * Insere uma nova associação de usuário-grupo no banco.
	 * @param conn Uma conexão ao banco de dados.. 
	 * @param grupoUsuarioBean O bean que representa a associação.
	 * @throws ModelException
	 * @throws SQLException 
	 */
	public static void inserirGrupoUsuario(Connection conn, GrupoUsuarioBean grupoUsuarioBean)
	throws ModelException, SQLException {

		if (grupoUsuarioBean == null)
			throw new ModelException("Argumento nulo: grupoUsuarioBean");

		if (conn == null)
			throw new ModelException("Argumento nulo: conn");

		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("INSERT INTO sis_usuario_grupo");
		sbSQL.append(" (id_usuario, id_grupo)");
		sbSQL.append(" VALUES (?,?)");

		PreparedStatement ps = null;

		ps = conn.prepareStatement(sbSQL.toString());

		// Ajustando os valores dos parametros:
		ps.setInt(1, grupoUsuarioBean.getIdUsuario());
		ps.setInt(2, grupoUsuarioBean.getIdGrupo());

		ps.executeUpdate() ;
	}
	
	/**
	 * Remove as associações com grupos de um determinado usuário
	 * @param idUsuario o id do usuário que se deseja remover os grupos.
	 * @throws SQLException 
	 * @throws ConexaoException 
	 */	
	public static void removerGrupoUsuarioByIdUsuario(int idUsuario)
	throws SQLException, ConexaoException {

		Connection conn = Conexao.getConexao();
		conn.setAutoCommit(false);

		try {
			removerGrupoUsuarioByIdUsuario(conn, idUsuario);
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
	 * Remove as associações com grupos de um determinado usuário
	 * @param conn Uma conexão de banco de dados. 
	 * @param idUsuario o id do usuário que se deseja remover os grupos.
	 * @throws SQLException 
	 */
	public static void removerGrupoUsuarioByIdUsuario(Connection conn, int idUsuario)
	throws SQLException {
		
		PreparedStatement ps = conn.prepareStatement("DELETE FROM sis_usuario_grupo WHERE id_usuario = ?");
		ps.setInt(1, idUsuario);
		ps.executeUpdate();
	}

}
