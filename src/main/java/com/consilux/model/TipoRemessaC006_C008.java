package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe que define os tipos de remessa do sistema.
 * @author raoni
 *
 */
public class TipoRemessaC006_C008 {

	private String tipo;


	/**
	* Constrói o objeto TipoRemessaC006_C008 a partir dos parâmetros dados.
	* @param tipo
	*/
	public TipoRemessaC006_C008(String tipo) {
		super();
		this.tipo = tipo;
	}


	public String getTipo() {
		return this.tipo;
	}


	/**
	 * Autor: Thiago Surgik 18/02/2016
	 * Obter tipos de remessa dos contratos C006 e C008.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */	
	public static Collection<TipoRemessaC006_C008> buscarTiposRemessa() throws ConexaoException, SQLException {

		ArrayList<TipoRemessaC006_C008> listTiposRemessa = new ArrayList<TipoRemessaC006_C008>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT rc6.tipo ");
		sbSQL.append(" FROM   recursos_C006 rc6 ");
		sbSQL.append(" GROUP BY ");
		sbSQL.append("    	  rc6.tipo ");

		sbSQL.append(" UNION ");
		
		sbSQL.append(" SELECT rc8.tipo ");
		sbSQL.append(" FROM   recursos_C008 rc8 ");
		sbSQL.append(" GROUP BY ");
		sbSQL.append("    	  rc8.tipo ");
		
		
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  tipo ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				TipoRemessaC006_C008 tipoRemessa = new TipoRemessaC006_C008(rs.getString("tipo"));
				
				listTiposRemessa.add(tipoRemessa);
				
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		
		return listTiposRemessa;
	}

}
