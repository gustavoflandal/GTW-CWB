/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 18/01/2007

  Descricao: Classe que controla o relatório contagem x data x velocidade.

  Historico:

    $Log: FluxoVelocidadeGrade.java,v $
    Revision 1.6  2009/06/08 20:11:39  fos
    Criado métodos para liberar as conexão quando elas deixarem de ser usadas.

    Revision 1.5  2009/03/12 13:07:39  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.4  2009/01/12 12:49:48  fos
    Recuperação de repositório.

    Revision 1.2  2008/09/04 19:31:10  fos
    Consertado problemas de hortografia.

    Revision 1.1  2008/08/13 18:58:49  fos
    Renomeada a classe.

    Revision 1.5  2007/07/06 13:05:45  fos
    Colocado unlocks explicitos nas querys.

    Revision 1.4  2007/04/18 13:10:15  fos
    Agora trabalha também com detalhamento de 15 em 15 minutos.

    Revision 1.3  2007/03/16 12:56:15  fos
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
 * Classe que controla o relatório contagem x data x velocidade.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.6 $ $Date: 2009/06/08 20:11:39 $ $Author: fos $
 */
public class FluxoVelocidadeGrade extends Relatorio {
	private PreparedStatement ps;
	private Integer velocidade;
	private Integer subData;
	private Integer conta;
	private Connection conn = null;
	/**
	 * Tipos de agrupamento.
	 * @author Fernando Oliveira da Silva - Consilux Tecnologia
	 * @version $Revision: 1.6 $ $Date: 2009/06/08 20:11:39 $ $Author: fos $
	 */
	public enum Tipo { ANO, MES, DIA, HORA, QUINZE, MINUTO, TODO }
	/**
	 * Monta estrutura para busca dos dados a partir dos filtros.
	 * @param iLocal Código do local
	 * @param iFaixa Faixa de trânsito
	 * @param dtIni Data inicial
	 * @param dtFim Data final
	 * @param tipo Tipo de agrupamento
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public FluxoVelocidadeGrade(int iLocal, int iFaixa, Timestamp dtIni, Timestamp dtFim, Tipo tipo) throws ConexaoException, SQLException {
		String sSubData = "";
		switch(tipo) {
			case ANO: sSubData = "datepart(year,data)"; break;
			case MES: sSubData = "datepart(month,data)"; break;
			case DIA: sSubData = "datepart(day,data)"; break;
			case HORA: sSubData = "datepart(hour,data)"; break;
			case QUINZE: sSubData = "((datepart(minute,data)/15)*15)"; break;
			case MINUTO: sSubData = "datepart(minute,data)"; break;
			case TODO: sSubData = "data"; break;
		}
		String sSQL =	"SELECT" +
					  	"	convert(integer, velocidade/10) AS velocidade," +
					  	"	"+sSubData+" AS subdata,"+
						"	count(*) AS conta " +
						"FROM"+
						"	veiculo_pesquisa WITH (NOLOCK) "+
						"WHERE"+ 
						"	data BETWEEN ? AND ? AND " +
						"	id_local=?"+ 
						(iFaixa > 0 ? "	AND pista=? " : " ")+
						"GROUP BY"+
						"	convert(integer, velocidade/10)," +
						"	"+sSubData+" "+
						"WITH ROLLUP " +
						"ORDER BY 1 DESC, 2 DESC";
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sSQL);
			ps.setTimestamp(1, dtIni);
			ps.setTimestamp(2, dtFim);
			ps.setInt(3, iLocal);
			if (iFaixa > 0)
				ps.setInt(4, iFaixa);
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
		velocidade = rs.getString("velocidade") == null ? null : rs.getInt("velocidade");
		subData = rs.getString("subData") == null ? null : rs.getInt("subData");
		conta = rs.getString("conta") == null ? null : rs.getInt("conta");
	}

	/**
	 * Quantidade de veículos
	 * @return Quantidade
	 */
	public Integer getConta() {
		return conta;
	}
	/**
	 * Data do agrupamento
	 * @return Data
	 */
	public Integer getSubData() {
		return subData;
	}
	/**
	 * Velocidade do agrupamento
	 * @return Velocidade
	 */
	public Integer getVelocidade() {
		return velocidade;
	}
	/* (non-Javadoc)
	 * @see com.consilux.infra.Relatorio#finaliza()
	 */
	@Override
	protected void finaliza() throws SQLException {
		this.conn.close();		
	}
}
