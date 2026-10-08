/**
 * 
 */
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * @author fos
 *
 */
public class InfracaoRemessa {
	
	private Integer idRemessa;
	private Integer idInfracao;
	private Integer auto;
	private String serie;
	private String siglaInfracaoCliente;
	
	public InfracaoRemessa(Integer idRemessa, Integer idInfracao, Integer auto,
			String serie, String siglaInfracaoCliente) {
		super();
		this.idRemessa = idRemessa;
		this.idInfracao = idInfracao;
		this.auto = auto;
		this.serie = serie;
		this.siglaInfracaoCliente = siglaInfracaoCliente;
	}

	public static InfracaoRemessa buscarItemRemessaPorInfracao(Integer idInfracao) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT ");
		sbSQL.append("	TOP 1");						//Sempre será a ultima remessa.
		sbSQL.append("	id_remessa,");
		sbSQL.append("	auto,");
		sbSQL.append("	RTRIM(serie) AS serie, ");
		sbSQL.append("	sigla_infracao_cliente ");
		sbSQL.append("FROM");
		sbSQL.append("	infracao_remessa WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		id_infracao = ? ");
		sbSQL.append("ORDER BY id_remessa DESC");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setInt(1, idInfracao);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				
				String tmpString = rs.getString("sigla_infracao_cliente");
				if (rs.wasNull())
					tmpString = null;
				
				return new InfracaoRemessa (
					rs.getInt("id_remessa"),
					idInfracao,
					rs.getInt("auto"),
					rs.getString("serie"),
					tmpString
				);
			}
			else {
				return null;
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
	}

	public Integer getIdRemessa() {
		return idRemessa;
	}

	public Integer getIdInfracao() {
		return idInfracao;
	}

	public Integer getAuto() {
		return auto;
	}

	public String getSerie() {
		return serie;
	}

	public String getSiglaInfracaoCliente() {
		return siglaInfracaoCliente;
	}
	
}
