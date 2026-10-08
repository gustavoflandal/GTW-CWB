/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 26/08/2011

  Descricao: Classe que controla o grafico de percentual de reconhecimento x tempo.

  Historico:

    $Log$

 *********************************************************************************/
package com.consilux.model.relatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.consilux.infra.Relatorio;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;


public class QualidadeImagemTempo extends Relatorio {
	private PreparedStatement ps;
	private Byte pista;
	private Integer ano;
	private Integer mes;
	private Integer dia;
	private Integer hora;
	private Integer quantidadeVeiculosTesteDefeituosa;
	private Integer quantidadeVeiculosTesteLap;
	private Double percentualImagemDefeituosa;
	private Double percentualNaoReconhecimento;
	private Connection conn = null;
	/**
	 * Monta estrutura para busca dos dados apartir dos filtros.
	 * @param pista Pista do local ou null para todas.
	 * @param iLocal Código do local
	 * @param dtIni Data inicial
	 * @param dtFim Data final
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public QualidadeImagemTempo(Integer iLocal, Integer pista, Timestamp dtIni, Timestamp dtFim) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT ");
		sbSQL.append("	pista,");
		sbSQL.append("	datepart(year,data) AS ano,");
		sbSQL.append("	datepart(month,data) AS mes,");
		sbSQL.append("	datepart(day,data) AS dia,");
		sbSQL.append("	datepart(hour,data) AS hora,");
		sbSQL.append("	count(case when (flag & 4194304) <> 0 then 1 end) AS qtd_veiculo_teste_defeituosa,");
		sbSQL.append("	count(case when (flag & 8388608) <> 0 then 1 end) AS qtd_veiculo_teste_lap,");
		sbSQL.append("	CAST(count(case when (flag & 4194304) <> 0  and (flag & 2097152) <> 0 then 1 end)/CAST(NULLIF(count(case when (flag & 4194304) <> 0 then 1 end),0) AS DECIMAL(15,3)) AS DECIMAL(15,3)) AS perc_defeituosa,");
		sbSQL.append("	CAST(count(case when (flag & 8388608) <> 0  and (placa is null) then 1 end)/CAST(NULLIF(count(case when (flag & 8388608) <> 0 then 1 end),0) AS DECIMAL(15,3)) AS DECIMAL(15,3)) AS perc_nao_reconhecido ");
		sbSQL.append("FROM ");
		sbSQL.append("	veiculo_pesquisa WITH (NOLOCK) "); 
		sbSQL.append("WHERE "); 
		sbSQL.append("	data BETWEEN ? AND ? AND");
		sbSQL.append("	id_local=? ");
		if (pista != null)
			sbSQL.append(" AND pista = ? ");
		sbSQL.append("GROUP BY ");
		sbSQL.append("	pista,");
		sbSQL.append("	datepart(year,data),");
		sbSQL.append("	datepart(month,data),");
		sbSQL.append("	datepart(day,data),");
		sbSQL.append("	datepart(hour,data) ");
		sbSQL.append("ORDER BY 1,2,3,4");

//		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setTimestamp(1, dtIni);
			ps.setTimestamp(2, dtFim);
			ps.setInt(3, iLocal);
			if (pista != null)
				ps.setInt(4, pista);
//		}
/*		finally {
			if (conn != null)
				conn.close();
		}*/			
	}
	/* (non-Javadoc)
	 * @see com.consilux.infra.Relatorio#montaRel()
	 */
	@Override
	protected ResultSet montaRel() throws SQLException {
		return ps.executeQuery();
	}

	/* (non-Javadoc)
	 * @see com.consilux.infra.Relatorio#constroi(java.sql.ResultSet)
	 */
	@Override
	protected void constroi(ResultSet rs) throws SQLException {
		pista = rs.getByte("pista") == 0 ? null : rs.getByte("pista");
		ano = rs.getString("ano") == null ? null : rs.getInt("ano");
		mes = rs.getString("mes") == null ? null : rs.getInt("mes");
		dia = rs.getString("dia") == null ? null : rs.getInt("dia");
		hora = rs.getString("hora") == null ? null : rs.getInt("hora");
		quantidadeVeiculosTesteDefeituosa = rs.getString("qtd_veiculo_teste_defeituosa") == null ? null : rs.getInt("qtd_veiculo_teste_defeituosa");
		quantidadeVeiculosTesteLap = rs.getString("qtd_veiculo_teste_lap") == null ? null : rs.getInt("qtd_veiculo_teste_lap");
		percentualImagemDefeituosa = rs.getString("perc_defeituosa") == null ? null : rs.getDouble("perc_defeituosa");
		percentualNaoReconhecimento = rs.getString("perc_nao_reconhecido") == null ? null : rs.getDouble("perc_nao_reconhecido");
	}
	/**
	 * Ano do agrupamento.
	 * @return Ano
	 */
	public Integer getAno() {
		return ano;
	}
	/**
	 * Dia do agrupamento
	 * @return Dia
	 */
	public Integer getDia() {
		return dia;
	}
	/**
	 * Hora do agrupamento
	 * @return Hora
	 */
	public Integer getHora() {
		return hora;
	}
	/**
	 * Mes do agrupamento
	 * @return Mes
	 */
	public Integer getMes() {
		return mes;
	}
	/**
	 * @return Retorna o valor de pista atual.
	 */
	public Byte getPista() {
		return pista;
	}
	/* (non-Javadoc)
	 * @see com.consilux.infra.Relatorio#finaliza()
	 */
	@Override
	protected void finaliza() throws SQLException {
		this.conn.close();		
	}
	/**
	 * @return the quantidadeVeiculosTesteDefeituosa
	 */
	public Integer getQuantidadeVeiculosTesteDefeituosa() {
		return quantidadeVeiculosTesteDefeituosa;
	}
	/**
	 * @return the quantidadeVeiculosTesteLap
	 */
	public Integer getQuantidadeVeiculosTesteLap() {
		return quantidadeVeiculosTesteLap;
	}
	/**
	 * @return the percentualImagemDefeituosa
	 */
	public Double getPercentualImagemDefeituosa() {
		return percentualImagemDefeituosa;
	}
	/**
	 * @return the percentualNaoReconhecimento
	 */
	public Double getPercentualNaoReconhecimento() {
		return percentualNaoReconhecimento;
	}
}
