package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.ui.client.beans.ManutencaoAtividadeGwtBean;
import com.consilux.ui.client.beans.ManutencaoDescricaoGwtBean;

/**
 * Classe de negócio, utilizada para representar as atividades
 * que ocorrem na manutenção de equipamentos.
 * @author raoni
 */
public class ManutencaoAtividade {

	private int idManutencaoAtividade;
	private int idManutencao;
	private byte pista;
	private int idManutencaoDescricao;
	
	public ManutencaoAtividade(int idManutencaoAtividade, int idManutencao,
			byte pista, int idManutencaoDescricao) {
		this.idManutencaoAtividade = idManutencaoAtividade;
		this.idManutencao = idManutencao;
		this.pista = pista;
		this.idManutencaoDescricao = idManutencaoDescricao;
	}
	
	public ManutencaoAtividadeGwtBean toManutencaoAtividadeBean() {
		ManutencaoAtividadeGwtBean bean = new ManutencaoAtividadeGwtBean();
		
		bean.setIdManutencaoAtividade(this.idManutencaoAtividade);
		bean.setIdManutencao(this.idManutencao);
		bean.setPista(this.pista);
		
		// TODO: Carregar a 
		// manutencaoDescricao = FindById(this.idManutencaoDescricao)
		ManutencaoDescricaoGwtBean manutencaoDescricao = null;
		bean.setManutencaoDescricao(manutencaoDescricao);
		return bean;
	}
	
	public int getIdManutencaoAtividade() {
		return idManutencaoAtividade;
	}
	
	public void setIdAtividade(int idManutencaoAtividade) {
		this.idManutencaoAtividade = idManutencaoAtividade;
	}

	public int getIdManutencao() {
		return idManutencao;
	}

	public void setIdManutencao(int idManutencao) {
		this.idManutencao = idManutencao;
	}
	
	public byte getPista() {
		return pista;
	}
	
	public void setPista(byte pista) {
		this.pista = pista;
	}

	public int getIdManutencaoDescricao() {
		return idManutencaoDescricao;
	}

	public void setIdManutencaoDescricao(int idManutencaoDescricao) {
		this.idManutencaoDescricao = idManutencaoDescricao;
	}	
	
	/**
	 * Busca uma lista de manutenção-atividade do BD por id da manutenção pai.
	 * @param idManutencao O identificador da manutenção que se deseja buscar as atividades.
	 * @return Lista com objetos ManutencaoAtividade da manutenção em questão.
	 * @throws ConexaoException
	 */	
	public static List<ManutencaoAtividade> buscaManutencaoAtividadePorIdManutencao(Integer idManutencao)
	throws ConexaoException {
		
		List<ManutencaoAtividade> listaRet = new ArrayList<ManutencaoAtividade>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("  id_manutencao_atividade,");
		sbSQL.append("  id_manutencao,");
		sbSQL.append("  pista,");
		sbSQL.append("  id_manutencao_descricao,");		
		sbSQL.append(" FROM");
		sbSQL.append("		manutencao_atividade WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		id_manutencao = ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idManutencao);
			rs = ps.executeQuery();
			
			ManutencaoAtividade tmpAtividade = null;
			while (rs.next()) {
				tmpAtividade = new ManutencaoAtividade(
					rs.getInt("id_manutencao_atividade"),
					rs.getInt("id_manutencao"),
					rs.getByte("pista"), rs.getInt("idManutencaoDescricao"));
				listaRet.add(tmpAtividade);
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
		
		return listaRet;
	}
}
