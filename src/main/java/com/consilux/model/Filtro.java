package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.FiltroBean;

public class Filtro {

	
	public List<FiltroBean> getTodosFiltros(Boolean incluirExpirados) throws ConexaoException, SQLException{
		
		List<FiltroBean> lRet = new ArrayList<FiltroBean>();
		StringBuilder sbSQL = new StringBuilder();
		
		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....			
		sbSQL.append("SELECT id_filtro");
		sbSQL.append(" ,nome_filtro ");
		sbSQL.append(" ,id_enquadramento ");
		sbSQL.append(" ,id_processo ");
		sbSQL.append(" ,id_local ");
		sbSQL.append(" ,id_classe ");
		sbSQL.append(" ,data_ini ");
		sbSQL.append(" ,data_fim ");
		sbSQL.append(" ,sql_criterio ");
		sbSQL.append(" ,set_id_inconsistencia ");
		sbSQL.append(" ,data_validade ");
		sbSQL.append("	FROM");
		sbSQL.append("		filtro WITH (NOLOCK) ");
		if (!incluirExpirados)
			sbSQL.append("	WHERE data_validade IS NULL OR data_validade >= getDate()");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			while (rs.next()) {
				FiltroBean fb = new FiltroBean();
				fb.setIdFiltro(rs.getInt("id_filtro"));
				fb.setNomeFiltro(rs.getString("nome_filtro"));
				fb.setIdFiltroEnquadramento(rs.getInt("id_enquadramento"));
				fb.setIdFiltroProcesso(rs.getInt("id_processo"));
				fb.setIdLocal(rs.getInt("id_local"));
				fb.setIdclasse(rs.getString("id_classe"));
				fb.setDtIni(rs.getTimestamp("data_ini"));
				fb.setDtFim(rs.getTimestamp("data_fim"));
				fb.setSqlCriterio(rs.getString("sql_criterio"));
				fb.setSetIdInconsistencia(rs.getInt("set_id_inconsistencia"));
				fb.setDtValidade(rs.getTimestamp("data_validade"));
				lRet.add(fb);
				
			}
		} 
		finally {
			if (conn != null)
				conn.close();
		}
		return lRet;
	}
	
	
	

	public boolean desabilitarFiltro(int id_filtro) throws ConexaoException{

		StringBuilder sbSQL = new StringBuilder();
				
		sbSQL.append(" UPDATE filtro ");
		sbSQL.append(" SET data_validade = ? ");
		sbSQL.append(" WHERE id_filtro = ? ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		Boolean rs = false;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setTimestamp(1, new Timestamp(new Date().getTime()));
			ps.setInt(2, id_filtro);
			
			rs = ps.execute();
			
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		} catch (ConexaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		return rs;
	}

}
