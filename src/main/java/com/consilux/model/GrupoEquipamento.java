package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.GrupoEquipamentoBean;
import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.GrupoEquipamentoGwtBean;

/**
 * Classe para busca de grupo-equipamento no BD.
 * @author raoni
 */
public class GrupoEquipamento {

	public static List<GrupoEquipamentoBean> buscaGrupoEquipamentoPorId(int idGrupoEquipamento)
	throws ConexaoException {
		
		List<GrupoEquipamentoBean> lRet = new ArrayList<GrupoEquipamentoBean>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("  SELECT 	");
		sbSQL.append("		id_grupo_equipamento,");
		sbSQL.append("		nome");
		sbSQL.append("  FROM ");
		sbSQL.append("		grupo_equipamento WITH (NOLOCK)");
		sbSQL.append("  WHERE ");
		sbSQL.append("		id_grupo_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idGrupoEquipamento);
			
			rs = ps.executeQuery();
			 while (rs.next()) {
				lRet.add(new GrupoEquipamentoBean(
							rs.getInt("id_grupo_equipamento"),
							rs.getString("nome")
						));
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
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		return lRet;
	}
	
	public static List<GrupoEquipamentoBean> buscaTodosGrupoEquipamento()
	throws ConexaoException {
		
		List<GrupoEquipamentoBean> lRet = new ArrayList<GrupoEquipamentoBean>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("  SELECT 	");
		sbSQL.append("		id_grupo_equipamento,");
		sbSQL.append("		nome");
		sbSQL.append("  FROM ");
		sbSQL.append("		grupo_equipamento WITH (NOLOCK)");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			 while (rs.next()) {
				lRet.add(new GrupoEquipamentoBean(
							rs.getInt("id_grupo_equipamento"),
							rs.getString("nome")
						));
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
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		return lRet;
	}
	
	/**
	 * Converte um bean de grupo-equipamento em um bean do GWT.
	 * @param grupoEquipamentoBean o bean de grupo-equipamento que se deseja converter.
	 * @return Um bean de grupo-equipamento do GWT.
	 * @throws ModelException caso o argumento passado seja nulo.
	 */	
	public static GrupoEquipamentoGwtBean toGwtBean(GrupoEquipamentoBean grupoEquipamentoBean)
	throws ModelException {
			if (grupoEquipamentoBean == null)
				throw new ModelException("Argumento nulo: grupoEquipamentoBean");
			
		GrupoEquipamentoGwtBean gwtBean = new GrupoEquipamentoGwtBean();
		gwtBean.setId(grupoEquipamentoBean.getId());
		gwtBean.setDescricao(grupoEquipamentoBean.getNome());
		return gwtBean;
	}
	
	/**
	 * Converte um bean de grupo-equipamento em um bean do GWT.
	 * @param grupoEquipamentoBean o bean de grupo-equipamento que se deseja converter.
	 * @return Um bean de grupo-equipamento do GWT.
	 * @throws ModelException caso o argumento passado seja nulo.
	 */	
	public static List<GrupoEquipamentoGwtBean> toGwtBean(Iterable<GrupoEquipamentoBean> listaGrupoEquipamentoBean)
	throws ModelException {
		if (listaGrupoEquipamentoBean == null)
			throw new ModelException("Argumento nulo: listaGrupoEquipamentoBean");
		
		List<GrupoEquipamentoGwtBean> lRet = new ArrayList<GrupoEquipamentoGwtBean>();
		for (GrupoEquipamentoBean geBean: listaGrupoEquipamentoBean) {
			lRet.add(toGwtBean(geBean));
		}
		return lRet;
	}
}
