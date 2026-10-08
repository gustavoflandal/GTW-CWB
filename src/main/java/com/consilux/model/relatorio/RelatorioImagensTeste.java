/**
 * 
 */
package com.consilux.model.relatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * @author fos
 *
 */
public class RelatorioImagensTeste {
	private Integer totalImagens;
	private Integer totalImagensVerificadas;
	private Integer totalPistas;
	private Integer totalPistasVerificadas;
	private Integer totalPistasCompletas;

	/**
	 * @param totalImagens
	 * @param totalImagensVerificadas
	 * @param totalPistas
	 * @param totalPistasVerificadas
	 * @param totalPistasCompletas
	 */
	public RelatorioImagensTeste(Integer totalImagens,
			Integer totalImagensVerificadas, Integer totalPistas,
			Integer totalPistasVerificadas, Integer totalPistasCompletas) {
		this.totalImagens = totalImagens;
		this.totalImagensVerificadas = totalImagensVerificadas;
		this.totalPistas = totalPistas;
		this.totalPistasVerificadas = totalPistasVerificadas;
		this.totalPistasCompletas = totalPistasCompletas;
	}

	/**
	 * Busca os dados do relatório no BD.
	 * @param dataInicio Data de inicio
	 * @param dataFim Data de fim
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws ModelException 
	 */
	public static RelatorioImagensTeste buscaRelatorioImagensTeste(Integer idProcesso, Date dataInicio, Date dataFim) throws ConexaoException, SQLException, ModelException {

		RelatorioImagensTeste ret = null;
		Integer totalImagens = 0;
		Integer totalImagensVerificadas = 0;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT");
		sbSQL.append("	count(i.id_infracao) as total_imagens,");
		sbSQL.append("	count(ic.id_infracao) as total_imagens_verificadas ");
		sbSQL.append("FROM");
		sbSQL.append("	infracao i ");
		sbSQL.append("	LEFT JOIN infracao_processo_concluido ic ON ic.id_processo = ? AND ic.id_infracao=i.id_infracao ");
		sbSQL.append("WHERE");
		sbSQL.append("	? IN (i.id_processo, ic.id_processo) AND i.data BETWEEN ? AND ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idProcesso);
			ps.setInt(2, idProcesso);
			ps.setTimestamp(3, new Timestamp(dataInicio.getTime()));
			ps.setTimestamp(4, new Timestamp(dataFim.getTime()));

			rs = ps.executeQuery();
			if (rs.next()) {
				totalImagens = rs.getInt("total_imagens"); 
				totalImagensVerificadas = rs.getInt("total_imagens_verificadas"); 
			}
			else
				throw new ModelException("Não foi possível buscar os totais de imagens!");
			
			ps.close();
			
			sbSQL.setLength(0);
			sbSQL.append("SELECT");
			sbSQL.append("	count(cp.id_pista) AS total_pistas,");
			sbSQL.append("	count(cpv.id_pista) AS total_pistas_verificadas,");
			sbSQL.append("	count(cpc.id_pista) AS total_pistas_completas ");
			sbSQL.append("FROM ");
			sbSQL.append("	configuracao_equipamento_pista cp");
			sbSQL.append("	JOIN local_vigente l ON l.id_configuracao_equipamento = cp.id_configuracao_equipamento");
			sbSQL.append("	LEFT JOIN (");
			sbSQL.append("		SELECT id_local, id_pista, count(*) as conta FROM (");
			sbSQL.append("			SELECT _l.id_local AS id_local, i.pista AS id_pista, (datepart(hour, i.data)/12) AS parte_dia, count(i.id_infracao) AS conta");
			sbSQL.append("				FROM local _l");
			sbSQL.append("				JOIN infracao i ON i.id_local = _l.id_local");
			sbSQL.append("				JOIN infracao_processo_concluido ic ON ic.id_processo = ? AND ic.id_infracao = i.id_infracao");
			sbSQL.append("			WHERE ");
			sbSQL.append("				i.data BETWEEN ? AND ?");
			sbSQL.append("			GROUP BY _l.id_local, i.pista, (datepart(hour, i.data)/12)");
			sbSQL.append("		) t");
			sbSQL.append("		GROUP BY id_local, id_pista");
			sbSQL.append("		HAVING count(*) > 1");
			sbSQL.append("	) cpv ON cpv.id_local = l.id_local AND cpv.id_pista = cp.id_pista");
			sbSQL.append("	LEFT JOIN (");
			sbSQL.append("		SELECT _l.id_local AS id_local, i.pista AS id_pista, count(ic.id_infracao) as completas, count(i.id_infracao) AS total");
			sbSQL.append("			FROM local _l");
			sbSQL.append("			JOIN infracao i ON i.id_local = _l.id_local");
			sbSQL.append("			LEFT JOIN infracao_processo_concluido ic ON ic.id_processo = ? AND ic.id_infracao = i.id_infracao");
			sbSQL.append("		WHERE ");
			sbSQL.append("			? IN (i.id_processo,ic.id_processo) AND i.data BETWEEN ? AND ?");
			sbSQL.append("		GROUP BY _l.id_local, i.pista");
			sbSQL.append("		HAVING count(ic.id_infracao) = count(i.id_infracao)");
			sbSQL.append("	) cpc ON cpc.id_local = l.id_local AND cpc.id_pista = cp.id_pista");

			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idProcesso);
			ps.setTimestamp(2, new Timestamp(dataInicio.getTime()));
			ps.setTimestamp(3, new Timestamp(dataFim.getTime()));
			ps.setInt(4, idProcesso);
			ps.setInt(5, idProcesso);
			ps.setTimestamp(6, new Timestamp(dataInicio.getTime()));
			ps.setTimestamp(7, new Timestamp(dataFim.getTime()));

			rs = ps.executeQuery();
			if (rs.next()) {
				ret = new RelatorioImagensTeste(
							totalImagens,
							totalImagensVerificadas,
							rs.getInt("total_pistas"), 
							rs.getInt("total_pistas_verificadas"), 
							rs.getInt("total_pistas_completas")
						);
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return ret;
	}

	/**
	 * @return the totalImagens
	 */
	public Integer getTotalImagens() {
		return totalImagens;
	}

	/**
	 * @return the totalImagensVerificadas
	 */
	public Integer getTotalImagensVerificadas() {
		return totalImagensVerificadas;
	}

	/**
	 * @return the totalPistas
	 */
	public Integer getTotalPistas() {
		return totalPistas;
	}

	/**
	 * @return the totalPistasVerificadas
	 */
	public Integer getTotalPistasVerificadas() {
		return totalPistasVerificadas;
	}

	public Double getPercPistasVerificadas() {
		return (totalPistasVerificadas > 0 ? ((double)totalPistasVerificadas) / totalPistas : 0.0);
	}

	/**
	 * @return the totalPistasCompletas
	 */
	public Integer getTotalPistasCompletas() {
		return totalPistasCompletas;
	}

	public Double getPercPistasCompletas() {
		return (totalPistasCompletas > 0 ? ((double)totalPistasCompletas) / totalPistas : 0.0);
	}

}
