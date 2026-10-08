/**
 * 
 */
package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class ExportacaoTrafego {
	private Integer idExportacaoTrafego;
	private Date dataCriacao;
	private Integer totalTrafego;
	private Date dataExportacao;

	public ExportacaoTrafego(Integer idExportacaoTrafego, Date dataCriacao,
			Integer totalTrafego, Date dataExportacao) {
		super();
		this.idExportacaoTrafego = idExportacaoTrafego;
		this.dataCriacao = dataCriacao;
		this.totalTrafego = totalTrafego;
		this.dataExportacao = dataExportacao;
	}

	public static ExportacaoTrafego buscaExportacaoTrafegoPorId(Integer idExportacaoTrafego) throws ConexaoException, SQLException, ModelException {
		Map<String,Object> mFiltros = new HashMap<String, Object>();
		
		mFiltros.put("id_exportacao_trafego", idExportacaoTrafego);
		List<ExportacaoTrafego> lret = buscaExportacaoTrafegoPor(mFiltros);
		
		return lret != null && lret.size() > 0 ? lret.get(0) : null;   
	}
	
	public static List<ExportacaoTrafego> buscaExportacaoTrafegoPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException, ModelException {
		List<ExportacaoTrafego> lRet = new ArrayList<ExportacaoTrafego>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT id_exportacao_trafego,");
		sbSQL.append("		 data_criacao, ");
		sbSQL.append("		 total_trafego, ");
		sbSQL.append("		 data_exportacao");
		
		sbSQL.append("	FROM");
		sbSQL.append("		exportacao_trafego WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");

		//Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("nao_exportada", "data_exportacao IS NULL");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			//Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
	
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new ExportacaoTrafego(
						rs.getInt("id_exportacao_trafego"),
						rs.getTimestamp("data_criacao"),
						rs.getInt("total_trafego"),
						rs.getTimestamp("data_exportacao")
					));
			}
		}				
		finally {
			if (conn != null)
				conn.close();
		}
		
		return lRet;
	}
	/**
	 * @return the idExportacaoTrafego
	 */
	public Integer getIdExportacaoTrafego() {
		return idExportacaoTrafego;
	}
	/**
	 * @return the dataCriacao
	 */
	public Date getDataCriacao() {
		return dataCriacao;
	}
	/**
	 * @return the totalTrafego
	 */
	public Integer getTotalTrafego() {
		return totalTrafego;
	}
	/**
	 * @return the dataExportacao
	 */
	public Date getDataExportacao() {
		return dataExportacao;
	}
	

	public static ExportacaoTrafego criaExportacaoTrafegoParaData(Date data) throws ConexaoException, SQLException, ModelException {
		
		Integer id = null;
		
		Connection conn = null;
		CallableStatement cs = null;

		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{? = call spu_cria_exportacao_trafego(?)}"
			);
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setDate(2, new java.sql.Date(data.getTime()));

			cs.execute();
			id = cs.getInt(1);

		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return id != null ? ExportacaoTrafego.buscaExportacaoTrafegoPorId(id) : null;
	}

	public Boolean confirmaExportacao() throws ConexaoException, SQLException {
		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("UPDATE exportacao_trafego SET ");
		sbSQL.append("	data_exportacao = GETDATE()");
		sbSQL.append(" WHERE ");
		sbSQL.append("	id_exportacao_trafego = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setInt(1, this.idExportacaoTrafego);
			
			bRet = ps.executeUpdate() > 0;
			
			ps.close();
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		return bRet;
	}
}
