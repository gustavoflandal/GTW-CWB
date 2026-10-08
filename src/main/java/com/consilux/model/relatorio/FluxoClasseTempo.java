/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 05/02/2007

  Descricao: Classe que controla o grafico de contagem x classe x tempo.

  Historico:

    $Log: FluxoClasseTempo.java,v $
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
 * Classe que controla o grafico de contagem x classe x tempo.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.5 $ $Date: 2009/06/08 20:11:39 $ $Author: fos $
 */
public class FluxoClasseTempo extends Relatorio {
	private PreparedStatement ps;
	private Integer ano;
	private Integer mes;
	private Integer dia;
	private Integer hora;
	private String idClasse;
	private Integer conta;
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
	public FluxoClasseTempo(Integer iLocal, Integer pista, Timestamp dtIni, Timestamp dtFim) throws ConexaoException, SQLException {
		String sFiltro = "";
		if (pista != null)
			sFiltro = " AND pista = ? ";
		String sSQL =	"SELECT" +
						"	id_classe," +
					  	"	datepart(year,data) AS ano,"+
					  	"	datepart(month,data) AS mes,"+
					  	"	datepart(day,data) AS dia,"+
					  	"	datepart(hour,data) AS hora,"+
						"	count(*) AS conta " +
						"FROM "+
						"	veiculo_pesquisa WITH (NOLOCK) "+
						"WHERE "+ 
						"	data BETWEEN ? AND ? AND" +
						"	id_local=? "+sFiltro+
						"GROUP BY " +
						"	id_classe,"+
						"	datepart(year,data)," +
						"	datepart(month,data)," +
						"	datepart(day,data)," +
						"	datepart(hour,data) " +
						"ORDER BY 1,2,3,4,5";

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sSQL);
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
		ano = rs.getString("ano") == null ? null : rs.getInt("ano");
		mes = rs.getString("mes") == null ? null : rs.getInt("mes");
		dia = rs.getString("dia") == null ? null : rs.getInt("dia");
		hora = rs.getString("hora") == null ? null : rs.getInt("hora");
		conta = rs.getString("conta") == null ? null : rs.getInt("conta");
		idClasse = rs.getString("id_classe");
	}
	/**
	 * Ano do agrupamento
	 * @return Ano
	 */
	public Integer getAno() {
		return ano;
	}
	/**
	 * Quantidade de veículos
	 * @return Quantidade
	 */
	public Integer getConta() {
		return conta;
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
	 * Classe de agrupamento
	 * @return idClasse
	 */
	public String getIdClasse() {
		return idClasse;
	}
	/* (non-Javadoc)
	 * @see com.consilux.infra.Relatorio#finaliza()
	 */
	@Override
	protected void finaliza() throws SQLException {
		this.conn.close();		
	}
}
