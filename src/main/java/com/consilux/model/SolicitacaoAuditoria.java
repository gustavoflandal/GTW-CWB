/**
 * 
 */
package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * @author fos
 * 
 */
public class SolicitacaoAuditoria {
	private Integer idSolicitacaoAuditoria;
	private Date dataGeracao;
	private Date dataImagens;

	/**
	 * @param idSolicitacaoAuditoria
	 * @param dataGeracao
	 * @param dataImagens
	 */
	public SolicitacaoAuditoria(Integer idSolicitacaoAuditoria,
			Date dataGeracao, Date dataImagens) {
		super();
		this.idSolicitacaoAuditoria = idSolicitacaoAuditoria;
		this.dataGeracao = dataGeracao;
		this.dataImagens = dataImagens;
	}

	public static SolicitacaoAuditoria geraSolicitacaoAuditoria(Date dataImagens, Integer idProcesso, Usuario usu) throws ConexaoException, SQLException, ModelException {
		SolicitacaoAuditoria ret = null;

		Connection conn = Conexao.getConexao();
		
		try {
			CallableStatement cs = conn.prepareCall("{? = call spu_gera_solicitacao_auditoria(?, ?, ?)}");
		
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setDate(2, new java.sql.Date(dataImagens.getTime()));
			cs.setInt(3, idProcesso);
			cs.setInt(4, usu.getId());
			cs.execute();
			
			Integer id = cs.getInt(1); 
			if (id < 1) {
				SQLWarning warn = cs.getWarnings();
				String mens = ""; 
				if (warn != null) {
					mens = warn.getMessage();
				}
					
				throw new ModelException(mens);				
			}
			ret = buscaSolicitacaoAuditoriaId(id).get(0);
		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return ret;
	}

	public static List<SolicitacaoAuditoria> buscaSolicitacaoAuditoriaId(Integer idSolicitacaoAuditoria) throws ConexaoException, SQLException, ModelException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id_solicitacao_auditoria", idSolicitacaoAuditoria);
		return buscaSolicitacaoAuditoriaPor(map);
	}
	
	public static List<SolicitacaoAuditoria> buscaSolicitacaoAuditoriaPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException, ModelException {
		List<SolicitacaoAuditoria> lRet = new ArrayList<SolicitacaoAuditoria>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("	id_solicitacao_auditoria,");
		sbSQL.append("	data_geracao,");
		sbSQL.append("	data_imagens ");
		sbSQL.append("FROM");
		sbSQL.append("	solicitacao_auditoria sa WITH (NOLOCK)");
		sbSQL.append("	WHERE ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("data_ini", "data_geracao >= ?");
		mRegras.put("data_fim", "data_geracao <= ?");
		mRegras.put("data_ini_imagem", "data_imagens >= ?");
		mRegras.put("data_fim_imagem", "data_imagens <= ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY data_geracao");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
		
			rs = ps.executeQuery();

			while (rs.next()) {
				lRet.add(new SolicitacaoAuditoria (
						rs.getInt("id_solicitacao_auditoria"),
						rs.getTimestamp("data_geracao"),
						rs.getDate("data_imagens")
					)
				);
			}
		}				
		finally {
			if (conn != null)
				conn.close();							
		}
		return lRet;
	}

	/**
	 * @return the idSolicitacaoAuditoria
	 */
	public Integer getIdSolicitacaoAuditoria() {
		return idSolicitacaoAuditoria;
	}

	/**
	 * @return the dataGeracao
	 */
	public Date getDataGeracao() {
		return dataGeracao;
	}

	/**
	 * @return the dataImagens
	 */
	public Date getDataImagens() {
		return dataImagens;
	}

}
