package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

public class CadastroProcesso {
	private static Logger logger = Logger.getLogger(CadastroProcesso.class);

	private String placa;
	private Integer idMarcaCet;
	private String marca;
	private Integer idEspecie;
	private String especie;
	private String UF;

	public CadastroProcesso(String placa, Integer idMarcaCet, String marca,
			Integer idEspecie, String especie, String UF) {
		this.placa = placa;
		this.idMarcaCet = idMarcaCet;
		this.marca = marca;
		this.idEspecie = idEspecie;
		this.especie = especie;
		this.UF = UF;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public Integer getIdMarcaCet() {
		return idMarcaCet;
	}

	public void setIdMarcaCet(Integer idMarcaCet) {
		this.idMarcaCet = idMarcaCet;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public Integer getIdEspecie() {
		return idEspecie;
	}

	public void setIdEspecie(Integer idEspecie) {
		this.idEspecie = idEspecie;
	}

	public String getEspecie() {
		return especie;
	}

	public void setEspecie(String especie) {
		this.especie = especie;
	}

	public String getUF() {
		return UF;
	}

	public void setUF(String uF) {
		UF = uF;
	}

	public static CadastroProcesso buscarPorPlaca(String placa) {
		StringBuilder sb1 = new StringBuilder();

		sb1.append("SELECT cmcp.placa, cmcp.id_marca_cet, cmc.descricao AS marca, ");
		sb1.append("cep.id_especie, ce.descricao AS especie, ");
		sb1.append("cuf.uf FROM cad_marca_cet_processo cmcp (NOLOCK) ");
		sb1.append("JOIN cad_marca_cet cmc (NOLOCK) ON cmcp.id_marca_cet = cmc.id_marca_cet ");
		sb1.append("LEFT JOIN cad_especie_processo cep (NOLOCK) ON cep.placa = cmcp.placa ");
		sb1.append("LEFT JOIN cad_especie ce (NOLOCK) ON ce.id_especie = cep.id_especie ");
		sb1.append("LEFT JOIN cad_uf_processo cuf (NOLOCK) ON cuf.placa = cmcp.placa ");
		sb1.append("WHERE cmcp.placa = ?");

		CadastroProcesso cp = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sb1.toString());
			ps.setString(1, placa);
			rs = ps.executeQuery();
			if (rs.next()) {
				cp = new CadastroProcesso(rs.getString("placa"),
						rs.getInt("id_marca_cet"), rs.getString("marca"),
						rs.getInt("id_especie"), rs.getString("especie"),
						rs.getString("UF"));
			}
		} catch (Exception e) {
			logger.error("Não foi possível buscar CadastroProcesso", e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				logger.error("Não foi possível fechar conexão!", e);
			}
		}

		return cp;
	}
}
