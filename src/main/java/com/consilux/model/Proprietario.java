package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

public class Proprietario {

	private static Logger logger = LogManager.getLogger(Proprietario.class);
	
	private String placa;
	private Integer idMarca;
	private String marca;
	private Integer idTipo;
	private String tipo;
	private String proprietario;
	private String observacao;
	private byte[] imagem;
	private Date data_atualizado;
	public String getPlaca() {
		return placa;
	}
	public Integer getIdMarca() {
		return idMarca;
	}
	public String getMarca() {
		return marca;
	}
	public Integer getIdTipo() {
		return idTipo;
	}
	public String getTipo() {
		return tipo;
	}
	public String getProprietario() {
		return proprietario;
	}
	public String getObservacao() {
		return observacao;
	}
	public byte[] getImagem() {
		return imagem;
	}
	public Date getData_atualizado() {
		return data_atualizado;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	public void setIdMarca(Integer idMarca) {
		this.idMarca = idMarca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public void setIdTipo(Integer idTipo) {
		this.idTipo = idTipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public void setProprietario(String proprietario) {
		this.proprietario = proprietario;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}
	public void setData_atualizado(Date data_atualizado) {
		this.data_atualizado = data_atualizado;
	}
	
	
	public static Proprietario ObterProprietario(String placa) {
		
		Proprietario res = null;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sb1 = new StringBuilder();
		sb1.append("SELECT placa, marca, tipo, proprietario, observacao, data_atualizado ");
		sb1.append("FROM v_cad_veiculo_proprietario (NOLOCK) WHERE placa = ?");
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sb1.toString());
			ps.setString(1, placa);
			rs = ps.executeQuery();
			if (rs.next()) {
				res = new Proprietario();
				res.setPlaca(rs.getString("placa"));
				res.setMarca(rs.getString("marca"));
				res.setTipo(rs.getString("tipo"));
				res.setProprietario(rs.getString("proprietario"));
				res.setObservacao(rs.getString("observacao"));
				res.setData_atualizado(rs.getTimestamp("data_atualizado"));
			}
		}
		catch(Exception e) {
			logger.error("Erro ao obter Proprietario", e);
			res = null;
		}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			}
			catch(Exception e) {}
		}
		
		
		return res;
		
	}
	
}
