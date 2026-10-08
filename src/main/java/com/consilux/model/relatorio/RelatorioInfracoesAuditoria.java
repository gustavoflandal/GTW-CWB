/**********************************************************************************


  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 05/05/2011

  Descricao: Classe para buscar infrações em processos disponíveis para auditoria;


 *********************************************************************************/
package com.consilux.model.relatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Processamento.EtapaProcesso;

/**
 * Classe para buscar infrações em processos disponíveis para auditoria
 * @author Fernando Oliveira da Silva
 */
public class RelatorioInfracoesAuditoria {
	
	
	private int idEnquadramento;
	private String descricaEnquadramento;
	private Date dataImagem;
	private int totalInconsistenteDisponivel;
	private int totalConsistenteDisponivel;
	private int totalImagensDisponivel;
	private int totalInconsistenteJaSolicitado;
	private int totalConsistenteJaSolicitado;
	private int totalImagensJaSolicitado;

	/**
	 * @param idEnquadramento
	 * @param descricaEnquadramento
	 * @param dataImagem
	 * @param totalInconsistenteDisponivel
	 * @param totalConsistenteDisponivel
	 * @param totalImagensDisponivel
	 * @param totalInconsistenteJaSolicitado
	 * @param totalConsistenteJaSolicitado
	 * @param totalImagensJaSolicitado
	 */
	public RelatorioInfracoesAuditoria(int idEnquadramento,
			String descricaEnquadramento, Date dataImagem,
			int totalInconsistenteDisponivel, int totalConsistenteDisponivel,
			int totalImagensDisponivel, int totalInconsistenteJaSolicitado,
			int totalConsistenteJaSolicitado, int totalImagensJaSolicitado) {
		super();
		this.idEnquadramento = idEnquadramento;
		this.descricaEnquadramento = descricaEnquadramento;
		this.dataImagem = dataImagem;
		this.totalInconsistenteDisponivel = totalInconsistenteDisponivel;
		this.totalConsistenteDisponivel = totalConsistenteDisponivel;
		this.totalImagensDisponivel = totalImagensDisponivel;
		this.totalInconsistenteJaSolicitado = totalInconsistenteJaSolicitado;
		this.totalConsistenteJaSolicitado = totalConsistenteJaSolicitado;
		this.totalImagensJaSolicitado = totalImagensJaSolicitado;
	}

	/**
	 * Busca a quantidade de infrações em cada processo.
	 * @return Relatório de processamento
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<RelatorioInfracoesAuditoria> buscaRelatorio() throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		List<RelatorioInfracoesAuditoria> lret = new ArrayList<RelatorioInfracoesAuditoria>();

		sbSQL.append("SELECT ");
		sbSQL.append(" e.id_enquadramento,");
		sbSQL.append(" e.descricao AS descricao_enquadramento,");
		sbSQL.append(" CAST(i.data AS DATE) AS data_imagem,");
		sbSQL.append(" count(ii.id_infracao) AS total_inconsistente_disponivel,");
		sbSQL.append(" count(ic.id_infracao) AS total_consistente_disponivel,");
		sbSQL.append(" count(ii.id_infracao)+count(ic.id_infracao) AS total_disponivel,");
		sbSQL.append(" count(sai.id_infracao) AS total_inconsistente_ja_solicitado,");
		sbSQL.append(" count(sac.id_infracao) AS total_consistente_ja_solicitado,");
		sbSQL.append(" count(sai.id_infracao)+count(sac.id_infracao) AS total_ja_solicitado ");
		sbSQL.append("FROM ");
		sbSQL.append(" enquadramento e");
		sbSQL.append(" JOIN infracao i ON i.id_enquadramento = e.id_enquadramento");
		sbSQL.append(" LEFT JOIN infracao ii ON ii.id_infracao = i.id_infracao AND ii.id_inconsistencia > 0");
		sbSQL.append(" LEFT JOIN infracao ic ON ic.id_infracao = i.id_infracao AND ic.id_inconsistencia = 0");
		sbSQL.append(" LEFT JOIN solicitacao_auditoria_infracao sai ON sai.id_infracao = ii.id_infracao");
		sbSQL.append(" LEFT JOIN solicitacao_auditoria_infracao sac ON sac.id_infracao = ic.id_infracao ");
		sbSQL.append("WHERE");
		sbSQL.append(" i.id_processo = ? ");
		sbSQL.append("GROUP BY");
		sbSQL.append(" CAST(i.data AS DATE),");
		sbSQL.append(" e.id_enquadramento,");
		sbSQL.append(" e.descricao ");
		sbSQL.append("HAVING");
		sbSQL.append(" (count(sai.id_infracao)+count(sac.id_infracao)) < (count(ii.id_infracao)+count(ic.id_infracao)) ");
		sbSQL.append("ORDER BY");
		sbSQL.append(" CAST(i.data AS DATE) DESC,");
		sbSQL.append(" e.descricao");
			
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		
		
		try {
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, EtapaProcesso.VALIDACAO.getId());
			rs = ps.executeQuery();
			
			while (rs.next()) {
					lret.add(new RelatorioInfracoesAuditoria(
								rs.getInt("id_enquadramento"),
								rs.getString("descricao_enquadramento"),
								rs.getDate("data_imagem"),
								rs.getInt("total_inconsistente_disponivel"),
								rs.getInt("total_consistente_disponivel"),
								rs.getInt("total_disponivel"),
								rs.getInt("total_inconsistente_ja_solicitado"),
								rs.getInt("total_consistente_ja_solicitado"),
								rs.getInt("total_ja_solicitado")
							));
			}
		}
		finally {
			if (conn != null)
				conn.close();							
		}
		return lret;
	}

	/**
	 * @return the idEnquadramento
	 */
	public int getIdEnquadramento() {
		return idEnquadramento;
	}

	/**
	 * @return the descricaEnquadramento
	 */
	public String getDescricaEnquadramento() {
		return descricaEnquadramento;
	}

	/**
	 * @return the dataImagem
	 */
	public Date getDataImagem() {
		return dataImagem;
	}

	/**
	 * @return the totalInonsistenteDisponivel
	 */
	public int getTotalInconsistenteDisponivel() {
		return totalInconsistenteDisponivel;
	}

	/**
	 * @return the totalConsistenteDisponivel
	 */
	public int getTotalConsistenteDisponivel() {
		return totalConsistenteDisponivel;
	}

	/**
	 * @return the totalImagensDisponivel
	 */
	public int getTotalImagensDisponivel() {
		return totalImagensDisponivel;
	}

	/**
	 * @return the totalInonsistenteJaSolicitado
	 */
	public int getTotalInconsistenteJaSolicitado() {
		return totalInconsistenteJaSolicitado;
	}

	/**
	 * @return the totalConsistenteJaSolicitado
	 */
	public int getTotalConsistenteJaSolicitado() {
		return totalConsistenteJaSolicitado;
	}

	/**
	 * @return the totalImagensJaSolicitado
	 */
	public int getTotalImagensJaSolicitado() {
		return totalImagensJaSolicitado;
	}
	
}