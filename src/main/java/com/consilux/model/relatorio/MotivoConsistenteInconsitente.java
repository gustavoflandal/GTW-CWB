/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 10/09/2008

  Descrição: Classe que controla o relatório de consistente x inconsistente.

  Histórico:

    $Log: MotivoConsistenteInconsitente.java,v $
    Revision 1.7  2009/06/08 20:11:39  fos
    Criado métodos para liberar as conexão quando elas deixarem de ser usadas.

    Revision 1.6  2009/03/19 23:06:52  fos
    Busca em todos os locais também.

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
 * Classe que controla o relatório de consistente x inconsistente.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.7 $ $Date: 2009/06/08 20:11:39 $ $Author: fos $
 */
public class MotivoConsistenteInconsitente extends Relatorio {
	private PreparedStatement ps;
	private Integer idInconsistencia;
	private Integer idEnquadramento;
	private Integer quantidade;
	private Connection conn = null;
	/**
	 * Monta estrutura para busca dos dados a partir dos filtros.
	 * @param pista Pista do local ou null para todas.
	 * @param iLocal Código do local
	 * @param idEnquadramento Identificador do enquadramento, ou null para todos.
	 * @param dtIni Data inicial
	 * @param dtFim Data final
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public MotivoConsistenteInconsitente(Integer iLocal, Integer pista, Integer idEnquadramento, Timestamp dtIni, Timestamp dtFim, boolean processoConcluido) throws ConexaoException {

		String sFiltro = "";
		StringBuilder sbSQL = new StringBuilder();

		if (pista != null)
			sFiltro = " AND pista = ? ";
		if (idEnquadramento != null)
			sFiltro = " AND id_enquadramento = ? ";


		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("SELECT");
		sbSQL.append("	id_inconsistencia,");
		sbSQL.append("   id_enquadramento,");
		sbSQL.append("	count(*) AS conta ");
		sbSQL.append("FROM ");
		sbSQL.append("	infracao i WITH (NOLOCK) ");
		sbSQL.append("	JOIN processo p ON p.id_processo = i.id_processo ");
		sbSQL.append("WHERE "); 
		sbSQL.append("	id_inconsistencia is not NULL AND ");
		sbSQL.append("	data BETWEEN ? AND ? AND");
		
		if( processoConcluido ){
			sbSQL.append("	p.id_processo_proximo IS NULL AND ativo = 1 AND ");
		}
		
		sbSQL.append(iLocal > 0 ? "	id_local=? AND " : "");
		sbSQL.append("	id_usuario_atual IS NULL " + sFiltro); 
		sbSQL.append("GROUP BY ");
		sbSQL.append("	id_inconsistencia,");
		sbSQL.append("   id_enquadramento ");
		sbSQL.append("ORDER BY 1,2");

		try {
			System.out.println(sbSQL.toString());
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setTimestamp(1, dtIni);
			ps.setTimestamp(2, dtFim);
			
			//ps.setInt(3, EtapaProcesso.VALIDACAO.getId());
			
			Integer contaParam = 2; // inicia com o último parâmetro utilizado
			if (iLocal > 0)
				ps.setInt(++contaParam, iLocal);
			if (pista != null)
				ps.setInt(++contaParam, pista);
			if (idEnquadramento != null)
				ps.setInt(++contaParam, idEnquadramento);
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
		idInconsistencia = rs.getString("id_inconsistencia") == null ? null : rs.getInt("id_inconsistencia");
		idEnquadramento = rs.getInt("id_enquadramento") == 0 ? null : rs.getInt("id_enquadramento");
		quantidade = rs.getString("conta") == null ? null : rs.getInt("conta");
	}
	/**
	 * @return Retorna o valor de idInconsistencia atual.
	 */
	public Integer getIdInconsistencia() {
		return idInconsistencia;
	}
	/**
	 * @return Retorna o valor de idEnquadramento atual.
	 */
	public Integer getIdEnquadramento() {
		return idEnquadramento;
	}
	/**
	 * @return Retorna o valor de quantidade atual.
	 */
	public Integer getQuantidade() {
		return quantidade;
	}
	/* (non-Javadoc)
	 * @see com.consilux.infra.Relatorio#finaliza()
	 */
	@Override
	protected void finaliza() throws SQLException {
		this.conn.close();		
	}
}
