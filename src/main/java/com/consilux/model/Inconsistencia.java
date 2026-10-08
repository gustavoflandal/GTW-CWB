/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 03/04/2007

  Descricao: Classe de negócio para busca de motivos de invalidação.

  Historico:

    $Log: Inconsistencia.java,v $
    Revision 1.8  2009/03/12 13:07:37  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.7  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.5  2008/09/17 14:06:51  fos
    Correção de acentuação.

    Revision 1.4  2008/08/12 12:59:52  fos
    Agora a classe já trabalha com o banco de dados novo.

    Revision 1.3  2008/05/08 21:26:27  fos
    Adequado o nome do método.

    Revision 1.2  2008/02/18 21:10:46  fos
    Agora a triagem envia a etapa para o processamento.

    Revision 1.1  2008/02/06 19:20:24  fos
    Carga da infração na nova tela funcional.

    Revision 1.2  2007/07/06 13:04:14  fos
    Colocado unlocks explicitos nas querys.

    Revision 1.1  2007/04/11 12:07:15  fos
    Primeira versão postada no CVS.


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para busca de motivos de inconsistencia.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.8 $ $Date: 2009/03/12 13:07:37 $ $Author: raoni $
 */
public class Inconsistencia {
	private int idInconsistencia;
	private String descricao;

	/**
	 * Constrói o objeto Inconsistencia com os seus respectivos atributos.
	 * @param idInconsistencia Identificador para o motivo
	 * @param descricao Descrição do motivo
	 */
	public Inconsistencia(int idInconsistencia, String descricao) {
		this.idInconsistencia = idInconsistencia;
		this.descricao = descricao;
	}

	public static List<Inconsistencia> buscaTodasInconsistenciaPorEnquadramento(Integer id_enquadramento) {
		List<Inconsistencia> lRet = new ArrayList<Inconsistencia>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT i.id_inconsistencia, i.descricao FROM inconsistencia i (NOLOCK) ");
		sbSQL.append("JOIN enquadramento_inconsistencia ei (NOLOCK) "); 
		sbSQL.append("ON i.id_inconsistencia = ei.id_inconsistencia "); 
		sbSQL.append("WHERE ei.id_enquadramento = ? ");
		sbSQL.append("ORDER BY i.descricao ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, id_enquadramento);

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Inconsistencia(
						rs.getInt("id_inconsistencia"),
						rs.getString("descricao")
				)
				);
			}

		} catch (Exception e) {	}		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) { }
		}

		return lRet;
	}
	
	/**
	 * Busca todos os motivos de inconsistência disponíveis.
	 * @return Lista de objetos Invalidacao
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Inconsistencia> buscaTodasInconsistenciaPorEtapaProcesso(EtapaProcesso etapa) throws ConexaoException  {
		List<Inconsistencia> lRet = new ArrayList<Inconsistencia>();
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("SELECT i.id_inconsistencia, i.descricao ");
		sbSQL.append("FROM inconsistencia i");
		sbSQL.append(" JOIN processo_inconsistencia pi WITH (NOLOCK)");
		sbSQL.append(" ON i.id_inconsistencia = pi.id_inconsistencia ");
		sbSQL.append(" WHERE pi.id_processo = ? ");
		sbSQL.append("ORDER BY i.descricao");
		

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, etapa.getId());

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Inconsistencia(
						rs.getInt("id_inconsistencia"),
						rs.getString("descricao")
				)
				);
			}

		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}

		return lRet;
	}
	
	public static List<Inconsistencia> buscaTodasInconsistenciaPorEtapaProcessoEnquadramento(EtapaProcesso etapa, Integer idEnquadramento) throws ConexaoException  {
		List<Inconsistencia> lRet = new ArrayList<Inconsistencia>();
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("SELECT * ");
		sbSQL.append("FROM f_inconsistencias_processo(?,?) i ");
		sbSQL.append("ORDER BY 3 DESC, 1 ASC ");
		

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, etapa.getId());
			
			if (idEnquadramento == null)
				ps.setNull(2, Types.INTEGER);
			else
				ps.setInt(2, idEnquadramento);

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Inconsistencia(
						rs.getInt("id_inconsistencia"),
						rs.getString("descricao")
				)
				);
			}

		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}

		return lRet;
	}

	/**
	 * Busca todos os motivos de consistência / inconsistência disponíveis.
	 * @return Lista de objetos Inconsistencia
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static List<Inconsistencia> buscaTodasInconsistencias()
	throws ConexaoException, SQLException  {

		return buscaTodasInconsistencias(false);
	}	
	
	/**
	 * Busca todos os motivos de inconsistência disponíveis.
	 * @param somenteInconsistencia (define se é somente inconsistentes ou não)
	 * @return Lista de objetos Inconsistencia
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Inconsistencia> buscaTodasInconsistencias(boolean somenteInconsistencia)
	throws ConexaoException, SQLException  {
		return buscaTodasInconsistencias(somenteInconsistencia, null);
	}
	/**
	 * Busca todos os motivos de inconsistência disponíveis.
	 * @param somenteInconsistencia (define se é somente inconsistentes ou não)
	 * @param somenteNaoTecnicas (define se é somente não técnicas)
	 * @return Lista de objetos Inconsistencia
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Inconsistencia> buscaTodasInconsistencias(Boolean somenteInconsistencia, Boolean naoTecnicas)
	throws ConexaoException, SQLException  {
		List<Inconsistencia> lRet = new ArrayList<Inconsistencia>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT i.id_inconsistencia, i.descricao ");
		sbSQL.append("FROM inconsistencia i WHERE 1=1");
		
		if (somenteInconsistencia) {
			sbSQL.append(" AND i.id_inconsistencia > 0 ");
		}

		if (naoTecnicas != null && naoTecnicas)
			sbSQL.append(" AND i.razao_tecnica = 0 ");
			
		sbSQL.append("ORDER BY i.descricao");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			while (rs.next()) {
				lRet.add(new Inconsistencia(
						rs.getInt("id_inconsistencia"),
						rs.getString("descricao").trim()
				)
				);
			}

		}	
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}

		return lRet;
	}	
	
	/**
	 * Busca uma inconsistência no BD.
	 * @param idInconsistencia Identificador da inconsistência
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws ModelException 
	 * @throws SQLException
	 */
	public static Inconsistencia buscaInconsistenciaPorId(Integer idInconsistencia) throws ConexaoException, ModelException  {
		StringBuilder sbSQL = new StringBuilder();

		if( idInconsistencia == null )
			throw new ModelException("idInconsistencia não pode ser nulo!");
			
		if( idInconsistencia < 0 )
			throw new ModelException("idInconsistencia não pode ser negativo!");
			
		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("SELECT i.id_inconsistencia,");
		sbSQL.append("		i.descricao");
		sbSQL.append("	FROM");
		sbSQL.append("		inconsistencia i WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		i.id_inconsistencia = ? ");
		sbSQL.append("ORDER BY i.descricao");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idInconsistencia);

			rs = ps.executeQuery();
			if (rs.next()) {
				return new Inconsistencia(
						rs.getInt("id_inconsistencia"),
						rs.getString("descricao")
				);
			}
			else {
				return null;
			}

		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
	}
	
	
	/**
	 * Busca todos os motivos de inconsistência para isentos.
	 * @return Lista de objetos Inconsistencia
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static Inconsistencia buscaInconsistenciasIsento(Integer idInconsistencia) throws ConexaoException, SQLException  {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT i.id_inconsistencia ");
		sbSQL.append(" 		 ,i.descricao ");
		sbSQL.append(" FROM   inconsistencia_isento i (NOLOCK) ");
		sbSQL.append(" WHERE  i.id_inconsistencia = ? ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  i.id_inconsistencia ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idInconsistencia);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				return new Inconsistencia(
						rs.getInt("id_inconsistencia"),
						rs.getString("descricao")
				);
			}
			else {
				return null;
			}

		}	
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();							
		}

	}	
	

	/**
	 * Identificador do motivo de invalidação.
	 * @return Identificador
	 */
	public int getIdInconsistencia() {
		return idInconsistencia;
	}
	/**
	 * Descrição do motivo de invalidação
	 * @return Descrição
	 */
	public String getDescricao() {
		return descricao;
	}

	public String getIdInconsistenciaStr() {
		return String.format("%02d", idInconsistencia);
	}

}
