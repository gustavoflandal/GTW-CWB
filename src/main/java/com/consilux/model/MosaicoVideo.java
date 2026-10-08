package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class MosaicoVideo {
	
	private int id;
	private int idLocal;
	private String descEquipamento;
	private int ativo;
	private int frameRate;
	private int qualidade;
	private String ip;
	private int intWidth;
	private int intHeight;

	public MosaicoVideo(int id, int idLocal, String desc, int ativo, int frameR, int quality, String ip, int wid, int heig) {
		this.id = id;
		this.idLocal = idLocal;
		this.descEquipamento = desc;
		this.ativo = ativo;
		this.frameRate = frameR;
		this.qualidade = quality;
		this.ip = ip;
		this.intWidth = wid;
		this.intHeight = heig;
	}

	public int getIdLocal() {
		return idLocal;
	}


	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}


	public int getAtivo() {
		return ativo;
	}


	public void setAtivo(int ativo) {
		this.ativo = ativo;
	}
	
	public int getIntWidth() {
		return intWidth;
	}


	public void setIntWidth(int intWidth) {
		this.intWidth = intWidth;
	}


	public int getIntHeight() {
		return intHeight;
	}


	public void setIntHeight(int intHeight) {
		this.intHeight = intHeight;
	}


	public MosaicoVideo() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescEquipamento() {
		return descEquipamento;
	}

	public void setDescEquipamento(String descEquipamento) {
		this.descEquipamento = descEquipamento;
	}

	public int getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}

	public int getQualidade() {
		return qualidade;
	}

	public void setQualidade(int qualidade) {
		this.qualidade = qualidade;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
		
	/**
	 * Busca todos os Equipamentos  no BD - Montagem do Mosaíco de Videos.
	 * @return Lista com os objetos de Equipamento no-ip
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public  ArrayList<MosaicoVideo> buscaTodosEquipamentosBD()   throws ConexaoException, SQLException  {

		ArrayList<MosaicoVideo> lstMosaico = new ArrayList<MosaicoVideo>();
		
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append(" select  ");
		sbSQL.append("	idMosaico, ");
		sbSQL.append("	idLocal, ");
		sbSQL.append("	descEquipamento, ");
		sbSQL.append("	ativo, ");
		sbSQL.append("	frameRate, ");
		sbSQL.append("	qualidade, ");
		sbSQL.append("	ip, ");
		sbSQL.append("	width, ");
		sbSQL.append("	height  ");
		sbSQL.append(" from mosaicoVideos ");
		sbSQL.append(" where ativo = 1 ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) 
			{
				lstMosaico.add(new MosaicoVideo(
						       rs.getInt("idMosaico"),
						       rs.getInt("idLocal"),
						       rs.getString("descEquipamento"),
						       rs.getInt("ativo"),
						       rs.getInt("frameRate"),
						       rs.getInt("qualidade"),
						       rs.getString("ip"), //IP do windows - Forwarding
						       rs.getInt("width"),
						       rs.getInt("height")) 
							);
			}
		}
		finally {
			if (conn != null)
				conn.close();
		}
		return lstMosaico;
	}
}



