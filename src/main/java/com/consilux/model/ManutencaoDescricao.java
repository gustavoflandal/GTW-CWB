package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.ui.client.beans.ManutencaoDescricaoGwtBean;

/**
 * Classe de negócio, utilizada para representar a descricao das
 * atividades que ocorrem na manutenção de equipamentos.
 * @author raoni
 */
public class ManutencaoDescricao {

	private int idManutencaoDescricao;
	private String descricao;
	
	public ManutencaoDescricao() {
		idManutencaoDescricao = 0;
		descricao = "";		
	}
	
	public ManutencaoDescricao(int idManutencaoDescricao, String descricao) {
		this.idManutencaoDescricao = idManutencaoDescricao;
		this.descricao = descricao;
	}
	
	public int getIdManutencaoDescricao() {
		return idManutencaoDescricao;
	}
	
	public void setIdManutencaoDescricao(int idManutencaoDescricao) {
		this.idManutencaoDescricao = idManutencaoDescricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
	/**
	 * Converte esta manutenção-descriçao para um bean.
	 * @return Um bean de manutenção-descrição.
	 */	
	public ManutencaoDescricaoGwtBean toManutencaoDescricaoBean()
	{
		ManutencaoDescricaoGwtBean bean = new ManutencaoDescricaoGwtBean(
				this.idManutencaoDescricao, this.getDescricao());

		return bean;
	}	
	
	/**
	 * Busca uma descrição de manutenção do BD por id.
	 * @param idManutencaoDescricao Identificador da descrição de manutenção.
	 * @return Objeto descrição de manutenção materializado.
	 * @throws ConexaoException
	 */	
	public static ManutencaoDescricao buscaManutencaoPorIdManutencao(Integer idManutencaoDescricao) throws ConexaoException {
		
		ManutencaoDescricao ret = null;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("  id_manutencao_descricao,");
		sbSQL.append("  descricao,");
		sbSQL.append(" FROM");
		sbSQL.append("		manutencao_descricao WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		id_manutencao_descricao = ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idManutencaoDescricao);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				ret = new ManutencaoDescricao(rs.getInt("id_manutencao_descricao"), rs.getString("descricao"));
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
		
		return ret;
	}

	/**
	 * Lista todas as manutenção descrição existentes.
	 * @return Lista com objetos manutenção-descrição materializados.
	 * @throws ConexaoException
	 */		
	public static List<ManutencaoDescricao> listarManutencaoDescricao() throws ConexaoException {
		
		List<ManutencaoDescricao> lRet = new ArrayList<ManutencaoDescricao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("  id_manutencao_descricao,");
		sbSQL.append("  descricao");
		sbSQL.append(" FROM");
		sbSQL.append("	manutencao_descricao WITH (NOLOCK)");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			while (rs.next()) {
				lRet.add(new ManutencaoDescricao(rs.getInt("id_manutencao_descricao"),
						rs.getString("descricao")));
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
