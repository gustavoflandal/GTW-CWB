package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

public class RemessaIteracao {
	protected final static Logger logger = Logger.getLogger(RemessaIteracao.class);
	private final static Semaphore sem = new Semaphore(1);
	
	private int idIteracao;
	private int idRemessa;
	private int idUsuario;
	private Date dataInicio;
	private Date dataFim = null;
	private int tipoIteracao = 1;
	
	public RemessaIteracao() {
	}
	public RemessaIteracao(int idRemessa, int idUsuario) {
		this();
		
		this.idRemessa = idRemessa;
		this.idUsuario = idUsuario;
	}
	public RemessaIteracao(int idIteracao, int idRemessa, int idUsuario)  {
		this(idRemessa, idUsuario);
		
		this.idIteracao = idIteracao;
	}
	public RemessaIteracao(int idIteracao, int idRemessa, int idUsuario, Date dataInicio, int tipoIteracao) {
		this(idIteracao, idRemessa, idUsuario);
		
		this.dataInicio = dataInicio;
		this.tipoIteracao = tipoIteracao;
	}
	public int getIdIteracao() {
		return idIteracao;
	}
	public void setIdIteracao(int idIteracao) {
		this.idIteracao = idIteracao;
	}
	public int getIdRemessa() {
		return idRemessa;
	}
	public void setIdRemessa(int idRemessa) {
		this.idRemessa = idRemessa;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public int getTipoIteracao() {
		return tipoIteracao;
	}
	public void setTipoIteracao(int tipoIteracao) {
		this.tipoIteracao = tipoIteracao;
	}
	public void Finalizar() {
		
		Connection conn = null;
		CallableStatement cs = null;
		
		logger.debug("Finalizando iteracao "+idIteracao);
		
		
		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall("{call spu_finaliza_movimento_iteracao (?)}");
			cs.setInt(1, idIteracao);
			cs.execute();
		} catch(Exception e) {
			logger.error("Erro ao Finalizar", e);
			
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (cs != null)
					cs.close();
			}
			catch(Exception e) {}
		}
		
	}
	
	public static void BloqueioIteracao(Integer idUsuario, Remessa remessa) {
		
		Connection conn = null;
		try {
			DesbloqueioIteracao(remessa, false);
			
			conn = Conexao.getConexao();
			logger.debug("Bloquear id_remessa = " + remessa.getIdRemessa());
			CallableStatement cs = null;
			try 
			{
				cs = conn.prepareCall("{call spu_bloqueia_movimento (?,?)}");
				cs.setInt(1, idUsuario);
				cs.setInt(2, remessa.getIdRemessa());
				cs.execute();
				JobBuscaRemessasPendentes.AtualizarRemessa(remessa.getIdRemessa());
			}
			catch(Exception e) {
				logger.error("BloqueioIteracao : " + e.getMessage(), e);
			} finally {
				if (cs != null)
					cs.close();
			}
		}
		catch(Exception e) {
			logger.error("BloqueioIteracao : " + e.getMessage(), e);
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch(Exception e) {}
		}
	}
	
	public static void DesbloqueioIteracao(Remessa remessa, Boolean desbloquearUltimo) {
		
		Connection conn = null;
		try {
			conn = Conexao.getConexao();
			logger.debug("Desbloquear id_remessa = " + remessa.getIdRemessa());
			CallableStatement cs = null;
			try 
			{
				
				if (desbloquearUltimo) {
					cs = conn.prepareCall("{call spu_desbloqueia_movimento_ultimo_auditor (?)}");
				} else {
					cs = conn.prepareCall("{call spu_desbloqueia_movimento (?)}");
				}
				
				cs.setInt(1, remessa.getIdRemessa());
				cs.execute();
				JobBuscaRemessasPendentes.AtualizarRemessa(remessa.getIdRemessa());
			}
			catch(Exception e) {
				logger.error("DesbloqueioIteracao : " + e.getMessage(), e);
			} finally {
				if (cs != null)
					cs.close();
			}
		}
		catch(Exception e) {
			logger.error("DesbloqueioIteracao : " + e.getMessage(), e);
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch(Exception e) {}
		}
	}
	
	public static RemessaIteracao BuscarIteracao(int idUsuario) {
		
		long dtini = System.currentTimeMillis(), dtfim;
		RemessaIteracao ri = null;
		Connection conn = null;
		CallableStatement cs = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append("{call spu_obter_remessa_iteracao (?)}");
		
		StringBuilder sb1 = new StringBuilder();
		sb1.append("SELECT TOP(1) id_iteracao, id_remessa, id_usuario, data_inicio, tipo_iteracao FROM remessa_iteracao (NOLOCK) ");
		sb1.append("WHERE id_usuario = ? AND data_fim IS NULL AND tipo_iteracao IN (1, 3) ");
		sb1.append("ORDER BY tipo_iteracao, id_iteracao DESC ");
		
		try {
			conn = Conexao.getConexao();
			
			cs = conn.prepareCall(sb.toString());
			cs.setInt(1, idUsuario);
			
			try {
				sem.acquire();
				cs.execute();
			} finally {
				sem.release();
			}
			
			ps = conn.prepareStatement(sb1.toString());
			ps.setInt(1, idUsuario);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				ri = new RemessaIteracao(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getTimestamp(4), rs.getInt(5));
			}
		} catch(Exception e) {
			logger.error("Erro ao obter Remessa Iteracao: " + e.getMessage(), e);
		} finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
				if (cs != null)
					cs.close();
			}
			catch(Exception ee) {}
		}
		dtfim = System.currentTimeMillis();
		logger.debug("RemessaIteracao [" + idUsuario + "] em " + (dtfim - dtini) + " ms");
		
		return ri;
	}
}
