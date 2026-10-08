package com.consilux.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.consilux.lib.Conexao;

public class InfracaoProcessoObservacao {

	private Integer idInfracaoProcessoObservacao;
	private Integer idUsuario;
	private Integer idInfracao;
	private Integer idProcesso;
	private Integer idInfracaoProcesso;
	private String observacao;
	
	private String processo;
	private String operador;
	private Date dataProcesso;
	
	public InfracaoProcessoObservacao(
	Integer idInfracaoProcessoObservacao,
	Integer idUsuario,
	Integer idInfracao,
	Integer idProcesso,
	Integer idInfracaoProcesso,
	String observacao,
	String operador,
	String processo,
	Date dataProcesso) {
		this.idInfracaoProcessoObservacao = idInfracaoProcessoObservacao;
		this.idUsuario = idUsuario;
		this.idInfracao = idInfracao;
		this.idProcesso = idProcesso;
		this.idInfracaoProcesso = idInfracaoProcesso;
		this.observacao = observacao;
		
		this.processo = processo;
		this.operador = operador;
		this.dataProcesso = dataProcesso;
	}
	
	
	public Integer getIdInfracaoProcessoObservacao() {
		return idInfracaoProcessoObservacao;
	}
	public void setIdInfracaoProcessoObservacao(Integer idInfracaoProcessoObservacao) {
		this.idInfracaoProcessoObservacao = idInfracaoProcessoObservacao;
	}
	public Integer getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	public Integer getIdInfracao() {
		return idInfracao;
	}
	public void setIdInfracao(Integer idInfracao) {
		this.idInfracao = idInfracao;
	}
	public Integer getIdProcesso() {
		return idProcesso;
	}
	public void setIdProcesso(Integer idProcesso) {
		this.idProcesso = idProcesso;
	}
	public Integer getIdInfracaoProcesso() {
		return idInfracaoProcesso;
	}
	public void setIdInfracaoProcesso(Integer idInfracaoProcesso) {
		this.idInfracaoProcesso = idInfracaoProcesso;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public String getProcesso() {
		return processo;
	}


	public void setProcesso(String processo) {
		this.processo = processo;
	}


	public String getOperador() {
		return operador;
	}


	public void setOperador(String operador) {
		this.operador = operador;
	}


	public Date getDataProcesso() {
		return dataProcesso;
	}


	public void setDataProcesso(Date dataProcesso) {
		this.dataProcesso = dataProcesso;
	}


	public static InfracaoProcessoObservacao ObterInfracaoProcessoObservacao(Integer idInfracao, Integer idProcesso) {
		InfracaoProcessoObservacao ipo = null;
		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			
			StringBuilder sbSQL = new StringBuilder();
			sbSQL.append("IF EXISTS(SELECT 1 FROM infracao_processo_observacao (NOLOCK) WHERE id_infracao = ?) "); 
			sbSQL.append("BEGIN ");
			sbSQL.append("SELECT TOP(1) ipo.id_infracao_processo_observacao, ipo.id_usuario, ipo.id_infracao, ipo.id_processo, ");
			sbSQL.append(" ipo.id_infracao_processo, ipo.observacao, su.usuario, p.nome, ip.data ");
			sbSQL.append(" FROM infracao_processo_observacao ipo (NOLOCK) ");
			sbSQL.append(" JOIN sis_usuario su (NOLOCK) ON su.id_usuario = ipo.id_usuario ");
			sbSQL.append(" JOIN processo p (NOLOCK) ON p.id_processo = ipo.id_processo ");
			sbSQL.append(" JOIN infracao_processo ip (NOLOCK) ON ipo.id_infracao_processo = ip.id_infracao_processo ");
			sbSQL.append(" WHERE ipo.id_infracao = ? AND ipo.id_processo = ? ");
			sbSQL.append(" ORDER BY id_infracao_processo_observacao DESC ");
			sbSQL.append("END ");
			
			PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idInfracao);
			ps.setInt(2, idInfracao);
			ps.setInt(3, idProcesso);
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				ipo = new InfracaoProcessoObservacao(
						rs.getInt("id_infracao_processo_observacao"),	
						rs.getInt("id_usuario"),	
						rs.getInt("id_infracao"),	
						rs.getInt("id_processo"),	
						rs.getInt("id_infracao_processo"),	
						rs.getString("observacao"),
						rs.getString("usuario"),
						rs.getString("nome"),
						new Date(rs.getTimestamp("data").getTime())
						);
			}
			
		} catch(Exception e) {}
		finally {
			try {
			if(conn != null)
				conn.close();
			} catch(Exception e) {}
		}
		return ipo;
	}
	
	public static List<InfracaoProcessoObservacao> ObterInfracaoProcessoObservacao(Integer idInfracao) {
		List<InfracaoProcessoObservacao> ipo = new ArrayList<InfracaoProcessoObservacao>();
		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			
			StringBuilder sbSQL = new StringBuilder();
			sbSQL.append("SELECT ipo.id_infracao_processo_observacao, ipo.id_usuario, ipo.id_infracao, ipo.id_processo, ");
			sbSQL.append(" ipo.id_infracao_processo, ipo.observacao, su.usuario, p.nome, ip.data ");
			sbSQL.append(" FROM infracao_processo_observacao ipo (NOLOCK) ");
			sbSQL.append(" JOIN sis_usuario su (NOLOCK) ON su.id_usuario = ipo.id_usuario ");
			sbSQL.append(" JOIN processo p (NOLOCK) ON p.id_processo = ipo.id_processo ");
			sbSQL.append(" JOIN infracao_processo ip (NOLOCK) ON ipo.id_infracao_processo = ip.id_infracao_processo ");
			sbSQL.append(" WHERE ipo.id_infracao = ? ");
			sbSQL.append(" ORDER BY id_infracao_processo_observacao ");
			
			PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idInfracao);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				ipo.add(new InfracaoProcessoObservacao(
						rs.getInt("id_infracao_processo_observacao"),	
						rs.getInt("id_usuario"),	
						rs.getInt("id_infracao"),	
						rs.getInt("id_processo"),	
						rs.getInt("id_infracao_processo"),	
						rs.getString("observacao"),
						rs.getString("usuario"),
						rs.getString("nome"),
						new Date(rs.getTimestamp("data").getTime())
						));
			}
			
		} catch(Exception e) { e.printStackTrace();}
		finally {
			try {
			if(conn != null)
				conn.close();
			} catch(Exception e) {}
		}
		return ipo;
	}
	
}
