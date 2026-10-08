package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * @author Edson Jan Ferreira Lopes - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2010/03/12 13:07:39
 */
public class ValidarListaInfracoes {

	/**
	 * Valida a lista de infrações buscando por consistentes.
	 * @return Lista com os processos.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public List<Integer> buscaConsistentes(List<Integer> lInfracoes) throws ConexaoException  {
		
		List<Integer> lRet = new ArrayList<Integer>();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....		
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT id_inconsistencia,");
		sbSQL.append("		id_infracao");
		sbSQL.append("	FROM");
		sbSQL.append("		infracao WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		id_inconsistencia = 0 AND");
		sbSQL.append(" 	id_infracao in (");
		
		
		int i = 0;
		i = lInfracoes.size();
		for(Integer id : lInfracoes){
			sbSQL.append(String.valueOf(id));
			i--;
			if(i != 0){
				sbSQL.append(", ");
			}
		}
		
		sbSQL.append(")");
		
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(rs.getInt("id_infracao"));
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


}
