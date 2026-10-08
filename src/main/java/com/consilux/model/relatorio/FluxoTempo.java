/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 30/01/2007

  Descricao: Classe que controla o grafico de contagem x tempo.

  Historico:

    $Log: FluxoTempo.java,v $
    Revision 1.5  2009/06/08 20:11:39  fos
    Criado métodos para liberar as conexão quando elas deixarem de ser usadas.

    Revision 1.4  2009/03/12 13:07:39  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.3  2009/01/12 12:49:48  fos
    Recuperação de repositório.

    Revision 1.1  2008/08/14 19:32:15  fos
    *** empty log message ***

    Revision 1.2  2008/08/13 18:58:21  fos
    Agora trabalha com filtro de pista também.

    Revision 1.1  2008/08/12 13:03:25  fos
    Renomeadas classes antigas.

    Revision 1.3  2007/07/06 13:05:34  fos
    Colocado unlocks explicitos nas querys.

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


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

/**
 * Classe que controla o grafico de contagem x tempo.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.5 $ $Date: 2009/06/08 20:11:39 $ $Author: fos $
 */
public class FluxoTempo extends Relatorio {
	private PreparedStatement ps;
	private Byte pista;
	private Integer ano;
	private Integer mes;
	private Integer dia;
	private Integer hora;
	private Integer conta;
	private Connection conn = null;
	/**
	 * Monta estrutura para busca dos dados a partir dos filtros.
	 * 
	 * @param pista
	 *            Pista do local ou null para todas.
	 * @param iLocal
	 *            Código do local
	 * @param dtIni
	 *            Data inicial
	 * @param dtFim
	 *            Data final
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public FluxoTempo(Integer iLocal, Integer pista, Timestamp dtIni,
			Timestamp dtFim) throws ConexaoException, SQLException {

		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("SELECT");
		sbSQL.append("   pista,");
		sbSQL.append("	datepart(year,data) AS ano,");
		sbSQL.append("	datepart(month,data) AS mes,");
		sbSQL.append("	datepart(day,data) AS dia,");
		sbSQL.append("	hora AS hora,");
		sbSQL.append("	sum(trafego) AS conta ");
		sbSQL.append("FROM ");
		sbSQL.append("	veiculo_sumarizado WITH (NOLOCK) ");
		sbSQL.append("WHERE ");
		sbSQL.append("	data BETWEEN ? AND ? AND");
		sbSQL.append("	id_local=? ");
		if (pista != null)
			sbSQL.append(" AND pista = ? ");
		sbSQL.append("GROUP BY ");
		sbSQL.append("  pista,");
		sbSQL.append("	datepart(year,data),");
		sbSQL.append("	datepart(month,data),");
		sbSQL.append("	datepart(day,data),");
		sbSQL.append("	hora ");
		sbSQL.append("ORDER BY 1,2,3,4");

		ps = null;
		conn = Conexao.getConexao();
		ps = conn.prepareStatement(sbSQL.toString());
		ps.setTimestamp(1, dtIni);
		ps.setTimestamp(2, dtFim);
		ps.setInt(3, iLocal);
		
		if (pista != null)
			ps.setInt(4, pista);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.consilux.infra.Relatorio#montaRel()
	 */
	@Override
	protected ResultSet montaRel() throws SQLException {
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
		} catch (SQLException e) {
			throw new SQLException("ERRO de SQL", e);
		}
		return rs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.consilux.infra.Relatorio#constroi(java.sql.ResultSet)
	 */
	@Override
	protected void constroi(ResultSet rs) throws SQLException {
		pista = rs.getByte("pista") == 0 ? null : rs.getByte("pista");
		ano = rs.getString("ano") == null ? null : rs.getInt("ano");
		mes = rs.getString("mes") == null ? null : rs.getInt("mes");
		dia = rs.getString("dia") == null ? null : rs.getInt("dia");
		hora = rs.getString("hora") == null ? null : rs.getInt("hora");
		conta = rs.getString("conta") == null ? null : rs.getInt("conta");
	}

	/**
	 * @return Retorna o valor de pista atual.
	 */
	public Byte getPista() {
		return pista;
	}

	/**
	 * Ano do agrupamento
	 * 
	 * @return Ano
	 */
	public Integer getAno() {
		return ano;
	}

	/**
	 * Quantidade de veículos
	 * 
	 * @return Quantidade
	 */
	public Integer getConta() {
		return conta;
	}

	/**
	 * Dia do agrupamento
	 * 
	 * @return Dia
	 */
	public Integer getDia() {
		return dia;
	}

	/**
	 * Hora do agrupamento
	 * 
	 * @return Hora
	 */
	public Integer getHora() {
		return hora;
	}

	/**
	 * Mes do agrupamento
	 * 
	 * @return Mes
	 */
	public Integer getMes() {
		return mes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.consilux.infra.Relatorio#finaliza()
	 */
	@Override
	protected void finaliza() throws SQLException {
		this.conn.close();
		this.ps.close();
	}
}
