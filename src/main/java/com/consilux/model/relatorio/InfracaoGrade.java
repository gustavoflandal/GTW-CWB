/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data:16/09/2008

  Descrição: Classe que controla o relatório infrações x grade tempo.

  Histórico:

    $Log: ConsistenteGrade.java,v $
    Revision 1.6  2009/06/08 20:11:39  fos
    Criado métodos para liberar as conexão quando elas deixarem de ser usadas.

    Revision 1.5  2009/03/12 13:07:39  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.4  2009/01/28 19:27:33  fos
    Agora a tabela infracao carrega os atributos de data e local.

    Revision 1.3  2009/01/12 12:49:48  fos
    Recuperação de repositório.

    Revision 1.1  2008/09/17 14:15:52  fos
    ASSIGNED - bug 90: Passar relatórios CET Excel para o GTW.
    http://bugzilla.consilux.net/show_bug.cgi?id=90


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
 * Classe que controla o relatório infração x grade tempo.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.6 $ $Date: 2009/06/08 20:11:39 $ $Author: fos $
 */
public class InfracaoGrade extends Relatorio {
	private PreparedStatement ps;
	private Integer x;
	private Integer y;
	private Integer idLocal;
	private Integer pista;
	private Integer conta;
	private Connection conn = null;
	private Boolean comGrupoLocal = false;
	private Boolean comGrupoPista = false;
	/**
	 * Tipos de agrupamento.
	 * @author Fernando Oliveira da Silva - Consilux Tecnologia
	 * @version $Revision: 1.6 $ $Date: 2009/06/08 20:11:39 $ $Author: fos $
	 */
	public enum Tipo { ANO, MES, DIA, HORA}
	/**
	 * Monta estrutura para busca dos dados a partir dos filtros.
	* @param iLocal
	* @param iFaixa
	* @param dtIni
	* @param dtFim
	* @param tipo
	* @param idProcesso
	* @param idEnquadramento
	* @param comInconsistente
	* @param grupoLocal
	* @param grupoPista
	* @throws ConexaoException
	* @throws SQLException
	*/
	public InfracaoGrade(int iLocal, int iFaixa, Timestamp dtIni, Timestamp dtFim, Tipo tipo, Integer idProcesso, Integer idEnquadramento, Boolean comInconsistente, Boolean grupoLocal, Boolean grupoPista) throws ConexaoException, SQLException {
		String sGruposLocaisPista = "";
		String sSubDatas = "";
		
		sGruposLocaisPista = (grupoLocal ? ", i.id_local" : "");
		sGruposLocaisPista += (grupoPista ? ", v.pista" : "");
		this.comGrupoLocal = grupoLocal;
		this.comGrupoPista = grupoPista;
		
		switch(tipo) {
			case ANO: sSubDatas = "datepart(year,i.data)"+sGruposLocaisPista+", datepart(month,i.data)"; break;
			case MES: sSubDatas = "datepart(month,i.data)"+sGruposLocaisPista+", datepart(day,i.data)"; break;
			case DIA: sSubDatas = "datepart(day,i.data)"+sGruposLocaisPista+", datepart(hour,i.data)"; break;
			case HORA: sSubDatas = "datepart(hour,i.data)"+sGruposLocaisPista+", datepart(minute,i.data)"; break;
		}
		String sSQL =	"SELECT" +
					  	"	"+sSubDatas+", "+
						"	count(*) AS conta " +
						"FROM"+
						"	infracao i WITH (NOLOCK) "+
						"	JOIN veiculo v WITH (NOLOCK) ON v.id_veiculo = i.id_veiculo "+
						"	JOIN local_vigente lv WITH (NOLOCK) ON lv.id_local = i.id_local "+
						"	JOIN configuracao_equipamento_pista p WITH (NOLOCK) ON p.id_configuracao_equipamento = lv.id_configuracao_equipamento AND p.id_pista = v.pista "+
						"WHERE" +
						(!comInconsistente ? " id_inconsistencia = 0 AND " : "") +
						"	i.data BETWEEN ? AND ? " +
						(idProcesso > 0 ? " AND i.id_processo=? " : "") +
						(idEnquadramento > 0 ? " AND i.id_enquadramento=? " : "") +
						(iLocal > 0 ? " AND i.id_local=? " : "") +
						(iFaixa > 0 ? "	AND v.pista=? " : " ")+
						"GROUP BY"+
						"	"+sSubDatas+" "+
						"ORDER BY "+sSubDatas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sSQL);
			ps.setTimestamp(1, dtIni);
			ps.setTimestamp(2, dtFim);
			Integer contaParam = 2;
			if (idProcesso > 0)
				ps.setInt(++contaParam, idProcesso);
			if (idEnquadramento > 0)
				ps.setInt(++contaParam, idEnquadramento);
			if (iLocal > 0)
				ps.setInt(++contaParam, iLocal);
			if (iFaixa > 0)
				ps.setInt(++contaParam, iFaixa);
			
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
		}	}
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
		y = rs.getString(1) == null ? null : rs.getInt(1);
		idLocal = !comGrupoLocal || rs.getString("id_local") == null ? null : rs.getInt("id_local"); //Se não está agrupando por pista então não pega a coluna
		pista = !comGrupoPista || rs.getString("pista") == null ? null : rs.getInt("pista");; //Se não está agrupando por local então não pega a coluna
		x = rs.getString(rs.findColumn("conta")-1) == null ? null : rs.getInt(rs.findColumn("conta")-1); //Sempre será uma coluna antes da contegem.
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
	 * @return Retorna o valor de x atual.
	 */
	public Integer getX() {
		return x;
	}
	/**
	 * @return Retorna o valor de y atual.
	 */
	public Integer getY() {
		return y;
	}
	/**
	 * Retorna o valor do campo 'idLocal' atual.
	 * @return the idLocal
	 */
	public Integer getIdLocal() {
		return this.idLocal;
	}
	/**
	 * @return Retorna o valor de pista atual.
	 */
	public Integer getPista() {
		return pista;
	}
	/* (non-Javadoc)
	 * @see com.consilux.infra.Relatorio#finaliza()
	 */
	@Override
	protected void finaliza() throws SQLException {
		this.conn.close();		
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "{"+this.idLocal+","+this.pista+","+this.x+","+this.y+","+this.conta+"}";
	}
}
