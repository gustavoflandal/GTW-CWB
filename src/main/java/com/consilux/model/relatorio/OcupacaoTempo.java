/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 07/08/2008

  Descrição: Classe que controla o gráfico de ocupação x tempo.

  Histórico:

    $Log: OcupacaoTempo.java,v $
    Revision 1.6  2009/06/08 20:11:39  fos
    Criado métodos para liberar as conexão quando elas deixarem de ser usadas.

    Revision 1.5  2009/03/18 17:19:37  fos
    Consertado locks no banco de dados.

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
 * Classe que controla o gráfico de ocupação x tempo.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.6 $ $Date: 2009/06/08 20:11:39 $ $Author: fos $
 */
public class OcupacaoTempo extends Relatorio {
	private PreparedStatement ps;
	private Byte pista;
	private Integer ano;
	private Integer mes;
	private Integer dia;
	private Integer hora;
	private Integer dezMin;
	private Double percentualOcupacao;
	private Connection conn = null;
	/**
	 * Monta estrutura para busca dos dados a partir dos filtros.
	 * @param pista Pista do local ou null para todas.
	 * @param iLocal Código do local
	 * @param dtIni Data inicial
	 * @param dtFim Data final
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public OcupacaoTempo(Integer iLocal, Integer pista, Timestamp dtIni, Timestamp dtFim) throws ConexaoException, SQLException {

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT");
		sbSQL.append("	pista,");
		sbSQL.append("	datepart(year,data) AS ano,");
		sbSQL.append("	datepart(month,data) AS mes,");
		sbSQL.append("	datepart(day,data) AS dia,");
		sbSQL.append("	datepart(hour,data) AS hora,");
		sbSQL.append("   (datepart(minute,data)/10)*10 AS dez_min,");
		sbSQL.append("	(cast(sum(ocupacao) AS DECIMAL)/600000.00)*100 AS percentual_ocupacao "); //1000(ms)x60(s)x10(min) = 600000 
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
		sbSQL.append("	datepart(hour,data),");
		sbSQL.append("   (datepart(minute,data)/10)*10 ");
		sbSQL.append("ORDER BY 1,2,3,4,5");

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setTimestamp(1, dtIni);
			ps.setTimestamp(2, dtFim);
			ps.setInt(3, iLocal);
			if (pista != null)
				ps.setInt(4, pista);
		}
		catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}
		finally {
			/*
				try {
					if (ps != null)
						ps.close();
					if (conn != null)
						conn.close();							
				} catch (SQLException e) {
					throw new ConexaoException("ERRO de SQL", e);
				}
			 */					
		}
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
		dezMin = rs.getString("dez_min") == null ? null : rs.getInt("dez_min");
		percentualOcupacao = rs.getString("percentual_ocupacao") == null ? null : rs.getDouble("percentual_ocupacao");
	}

	/**
	 * @return Retorna o valor de pista atual.
	 */
	public Byte getPista() {
		return pista;
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
	 * Mês do agrupamento
	 * @return Mês
	 */
	public Integer getMes() {
		return mes;
	}

	/**
	 * @return Retorna o valor de dezMin atual.
	 */
	public Integer getDezMin() {
		return dezMin;
	}
	/**
	 * @return Retorna o valor de percentualOcupacao atual.
	 */
	public Double getPercentualOcupacao() {
		return percentualOcupacao;
	}
	/* (non-Javadoc)
	 * @see com.consilux.infra.Relatorio#finaliza()
	 */
	@Override
	protected void finaliza() throws SQLException {
		this.conn.close();		
	}
}
