/**
 * 
 */
package com.consilux.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * @author fos
 *
 */
public class ExportaDDL {
	private Integer id;
	private String comandoSQL;


	public ExportaDDL(Integer id, String comandoSQL) {
		super();
		this.id = id;
		this.comandoSQL = comandoSQL;
	}

	public Integer getId() {
		return id;
	}

	public String getComandoSQL() {
		return comandoSQL;
	}
	
	/**
	 * Busca as alteração de DDL no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: data_ini, data_fim.
	 * @param iOrdem Número da coluna de ordem.
	 * @return Lista de objetos ExportaDDL
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<ExportaDDL> buscaExportaDDLPor(Map<String,Object> mFiltros, Integer iOrdem) throws ConexaoException, SQLException {
		List<ExportaDDL> lRet = new ArrayList<ExportaDDL>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT id_mudanca_gtw, comando_sql ");
		sbSQL.append("	FROM");
		sbSQL.append("		mudanca_gtw ");
		sbSQL.append("	WHERE ");
		
		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("data_ini", "data_evento >= ?");
		mRegras.put("data_fim", "data_evento <= ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY " + iOrdem);
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new ExportaDDL(
							rs.getInt("id_mudanca_gtw"), 
							rs.getString("comando_sql")
						)
				);
			}
		} catch (ModelException e) {
			throw new SQLException("Erro ao montar SQL.", e);
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
