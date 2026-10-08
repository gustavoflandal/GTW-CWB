/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 23/10/2019

  Descricao: Classe para buscar status de remessas automáticas


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe para buscar status de remessas automáticas
 * @author Thiago Surgik
 */
public class RelatorioRemessaAutomatica {
	
	
	private Date dataSolicitacao;
	private Date dataRemessa;
	private Date dataInicial;
	private Date dataFinal;
	private int infracoesPorLote;
	private int totalInfracoes;
	private boolean agendamentoGerado;
	private Date dataGeracao;
	private String status;
	
	/**
	 * Busca o status de agendamentos com geração pendente
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public List<RelatorioRemessaAutomatica> buscaRelatorioGeracaoPendente() throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT CAST(gra.data_solicitacao AS DATE) AS data_solicitacao, ");
		sbSQL.append("        CAST(gra.data_remessa AS DATE) AS data_remessa, ");
		sbSQL.append("        CAST(gra.data_inicial AS DATE) AS data_inicial, ");
		sbSQL.append("        CAST(gra.data_final AS DATE) AS data_final, ");
		sbSQL.append("        gra.infracoes_por_lote, ");
		sbSQL.append("        gra.total_infracoes, ");
		sbSQL.append("        CASE WHEN gra.flag_geracao = 0 AND g.qtde = 0 THEN 'AGUARDANDO' ");
		sbSQL.append("        	   WHEN gra.flag_geracao = 0 AND g.qtde > 0 THEN 'EM PROCESSO' ");
		sbSQL.append("        	   ELSE 'N/D' ");
		sbSQL.append("        END AS status ");
		sbSQL.append(" FROM   gera_remessa_automatico gra (NOLOCK) ");
		sbSQL.append(" 		  LEFT JOIN (  ");
		sbSQL.append(" 						SELECT gral.id_remessa_automatico, ");
		sbSQL.append(" 							   COUNT(*) AS qtde ");
		sbSQL.append(" 						FROM   gera_remessa_automatico_log gral (NOLOCK) ");
		sbSQL.append(" 							   INNER JOIN gera_remessa_automatico ga (NOLOCK) ");
		sbSQL.append(" 									ON  ga.id_remessa_automatico = gral.id_remessa_automatico ");
		sbSQL.append(" 						WHERE  gral.id_tipo = 1 ");
		sbSQL.append(" 							   AND ga.flag_geracao = 0 ");
		sbSQL.append(" 						GROUP BY ");
		sbSQL.append(" 							   gral.id_remessa_automatico ");
		sbSQL.append("        ) g ");
		sbSQL.append("        	   ON  g.id_remessa_automatico = gra.id_remessa_automatico ");
		sbSQL.append(" WHERE  gra.flag_geracao = 0 ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("        gra.data_solicitacao ");
			
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		
		
		List<RelatorioRemessaAutomatica> lrra = new ArrayList<RelatorioRemessaAutomatica>();
		RelatorioRemessaAutomatica rra;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			rs = ps.executeQuery();
			while (rs.next()){
				rra = new RelatorioRemessaAutomatica();
				rra.setDataSolicitacao(new Date(rs.getDate("data_solicitacao").getTime()));
				rra.setDataRemessa(new Date(rs.getDate("data_remessa").getTime()));
				rra.setDataInicial(new Date(rs.getDate("data_inicial").getTime()));
				rra.setDataFinal(new Date(rs.getDate("data_final").getTime()));
				rra.setInfracoesPorLote(rs.getInt("infracoes_por_lote")); 
				rra.setTotalInfracoes(rs.getInt("total_infracoes"));
				rra.setStatus(rs.getString("status"));
				lrra.add(rra);
			}
		}
		finally {
			if (conn != null)
				conn.close();							
		}
		return lrra;
	}

	
	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}
	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	
	public Date getDataRemessa() {
		return dataRemessa;
	}
	public void setDataRemessa(Date dataRemessa) {
		this.dataRemessa = dataRemessa;
	}

	
	public Date getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	
	public Date getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	
	public int getInfracoesPorLote() {
		return infracoesPorLote;
	}
	public void setInfracoesPorLote(int infracoesPorLote) {
		this.infracoesPorLote = infracoesPorLote;
	}

	
	public int getTotalInfracoes() {
		return totalInfracoes;
	}
	public void setTotalInfracoes(int totalInfracoes) {
		this.totalInfracoes = totalInfracoes;
	}

	
	public boolean isAgendamentoGerado() {
		return agendamentoGerado;
	}
	public void setAgendamentoGerado(boolean agendamentoGerado) {
		this.agendamentoGerado = agendamentoGerado;
	}


	public Date getDataGeracao() {
		return dataGeracao;
	}
	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
