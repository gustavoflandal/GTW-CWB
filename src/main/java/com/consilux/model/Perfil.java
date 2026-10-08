/**
 * 
 */
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
 * @author fos
 *
 */
public class Perfil {
	Integer idVeiculoUnic;
	Integer pista;
	Integer sensor;
	Integer quantidadeAmostras;
	Double tamanhoAmostra;
	Integer inicioDisparo;
	Integer finalDisparo;
	byte[] perfil;
	private Perfil(Integer idVeiculoUnic, Integer pista, Integer sensor,
			Integer quantidadeAmostras, Double tamanhoAmostra, Integer inicioDisparo,
			Integer finalDisparo, byte[] perfil) {
		super();
		this.idVeiculoUnic = idVeiculoUnic;
		this.pista = pista;
		this.sensor = sensor;
		this.quantidadeAmostras = quantidadeAmostras;
		this.tamanhoAmostra = tamanhoAmostra;
		this.inicioDisparo = inicioDisparo;
		this.finalDisparo = finalDisparo;
		this.perfil = perfil;
	}
	
	/**
	 * Busca perfis do veículo no BD.
	 * @return Lista de objetos Perfil
	 * @throws ConexaoException
	 * @throws SQLException 
	 * @throws SQLException
	 */
	public static List<Perfil> buscaPerfisPorIdVeiculo( Integer idVeiculo )
	throws ConexaoException, SQLException  {
		List<Perfil> lRet = new ArrayList<Perfil>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("  SELECT p.id_veiculo_unic, p.pista, p.sensor, ");
		sbSQL.append("		p.quantidade_amostras, p.tamanho_amostra, p.inicio_disparo, p.final_disparo, p.perfil");
		sbSQL.append("	FROM");
		sbSQL.append("		perfil p WITH (NOLOCK)" );
		sbSQL.append("		JOIN veiculo v WITH (NOLOCK) ON v.id_veiculo_unic = p.id_veiculo_unic" );
		sbSQL.append("	WHERE ");
		sbSQL.append("		v.id_veiculo = ?");
		
		sbSQL.append(" ORDER BY sensor");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idVeiculo);
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Perfil(
							rs.getInt("id_veiculo_unic"),
							rs.getInt("pista"),
							rs.getInt("sensor"),
							rs.getInt("quantidade_amostras"),
							rs.getDouble("tamanho_amostra"),
							rs.getInt("inicio_disparo"),
							rs.getInt("final_disparo"),
							rs.getBytes("perfil")
						));
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return lRet;
		
	}	
	public Integer getIdVeiculoUnic() {
		return idVeiculoUnic;
	}
	public Integer getPista() {
		return pista;
	}
	public Integer getSensor() {
		return sensor;
	}
	public Integer getQuantidadeAmostras() {
		return quantidadeAmostras;
	}
	public Double getTamanhoAmostra() {
		return tamanhoAmostra;
	}
	public Integer getInicioDisparo() {
		return inicioDisparo;
	}
	public Integer getFinalDisparo() {
		return finalDisparo;
	}
	public byte[] getPerfil() {
		return perfil;
	}
	
}
