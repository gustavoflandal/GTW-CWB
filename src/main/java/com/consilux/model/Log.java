/**********************************************************************************

  Projeto: GTW
  Nome do Módulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 19/08/2008

  Descrição: Classe de negócio para gravação de logs no banco de dados.

  Histórico:

    $Log: Log.java,v $
    Revision 1.7  2009/05/11 17:48:48  raoni
    Removido close no statement.

    Revision 1.6  2009/05/11 12:50:40  raoni
    Removido os "close" no PreparedSatement. Estava conflitando com o pool do SQL Server.

    Revision 1.5  2009/03/12 13:10:21  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.4  2009/03/06 20:25:15  raoni
    Corrigido problemas com inserção do detalhe do log.

    Revision 1.3  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.1  2008/08/20 13:41:29  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66


*********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe de negócio para gravação de logs no banco de dados.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.7 $ $Date: 2009/05/11 17:48:48 $ $Author: raoni $
 */
public class Log {
	public enum TipoLog {
		
			TIPO_ACESSO("ACE"),
			TIPO_SEC("SEC"); // ?? Não sei para quê serve este.
			
			private String tipo;
			TipoLog(String tipo) {
				this.tipo = tipo;
			}
			public String toString() {
				return this.tipo;
			}
		};
		
	private TipoLog tipoLog;
	private Integer idUsuario;
	private String descricao;
	private String detalhe;
		
	/**
	 * Constrói o objeto Acesso com os seus respectivos atributos.
	 * @param tipoLog Tipo de log para uma classificação posterior.
	 * @param idUsuario Identificador do usuário que está fazendo a ação, pode ser null.
	 * @param descricao Descrição resumida do log.
	 * @param detalhe Descrição detalhada do log.
	 */
	private Log(TipoLog tipoLog, Integer idUsuario, String descricao,
			String detalhe) {
		super();
		this.tipoLog = tipoLog;
		this.idUsuario = idUsuario;
		this.descricao = descricao;
		this.detalhe = detalhe;
	}
	
	/**
	 * Grava um registro de log no banco de dados e retorna o objeto materializado para um uso posterior.
	 * @param tipoLog Tipo de log para uma classificação posterior.
	 * @param idUsuario Identificador do usuário que está fazendo a ação, pode ser null.
	 * @param descricao Descrição resumida do log.
	 * @param detalhe Descrição detalhada do log.
	 * @return Objeto Log materializado.
	 * @throws SQLException 
	 * @throws ConexaoException
	 */
	public static Log gravaLog(Connection conn, TipoLog tipo, Integer idUsuario, String descricao, String detalhe)
	throws SQLException {
		
		Log lRet = null;
		
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("INSERT INTO sis_log (descricao, data, tipo, id_usuario) ");
		sbSQL.append(" VALUES (?, GetDate(), ?, ?)");
		
		PreparedStatement ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);

		//Ajustando os valores dos parâmetros:
		ps.setString(1, descricao);
		ps.setString(2, String.valueOf(tipo));
		
		if (idUsuario != null)
			ps.setInt(3, idUsuario);
		else
			ps.setNull(3, Types.INTEGER);
			
		if (ps.executeUpdate() > 0)
		{
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
			{
				int idLog = rs.getInt(1);
				if (gravaLogDetalhe(conn, idLog, detalhe))
					lRet = new Log(tipo, idUsuario, descricao, detalhe);
			}			
		}
		
		return lRet;
	}
	
	/**
	 * Grava um registro de log no banco de dados e retorna o objeto materializado para um uso posterior.
	 * @param tipoLog Tipo de log para uma classificação posterior.
	 * @param idUsuario Identificador do usuário que está fazendo a ação, pode ser null.
	 * @param descricao Descrição resumida do log.
	 * @param detalhe Descrição detalhada do log.
	 * @return Objeto Log materializado.
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static Log gravaLog(TipoLog tipo, Integer idUsuario, String descricao, String detalhe)
	throws ConexaoException, SQLException {
		
		Log lRet = null;
		Connection conn = null;
		
		try {
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			lRet = gravaLog(conn, tipo, idUsuario, descricao, detalhe);
			conn.commit();
		} catch (SQLException ex) {
			conn.rollback();
			throw ex;
		}
		finally {
			if (conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}
		return lRet;
	}

	public static boolean gravaLogDetalhe(Connection conn, Integer idLog, String detalhe)
	throws SQLException {
		
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("INSERT INTO sis_log_detalhe (id_log, detalhe) ");
		sbSQL.append(" VALUES (?,?)");
		
		PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
		
		//Ajustando os valores dos parâmetros:
		ps.setInt(1, idLog);
		ps.setString(2, (detalhe != null ? detalhe.substring(0, Math.min(999, detalhe.length())) : null));
		
		return (ps.executeUpdate() > 0);
	}
	
	/**
	 * Grava a parte mais detalhada do log em um outra tabela por motivos de performance.
	 * @param idLog Identificador do registro de log praviamente grava com informações preliminares.
	 * @param detalhe Descrição detalhada do log.
	 * @return True se a gravação foi feita com sucesso ou False se falhou.
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static boolean gravaLogDetalhe(Integer idLog, String detalhe)
	throws ConexaoException, SQLException {
		
		boolean ret = false;
		Connection conn = null;
		
		try {
			conn = Conexao.getConexao();
			ret = gravaLogDetalhe(conn, idLog, detalhe);
		}
		finally {
			if (conn != null)
				conn.close();
		}
		return ret;
		
	}

	/**
	 * @return Retorna o valor de tipoLog atual.
	 */
	public TipoLog getTipoLog() {
		return tipoLog;
	}

	/**
	 * @return Retorna o valor de idUsuario atual.
	 */
	public Integer getIdUsuario() {
		return idUsuario;
	}

	/**
	 * @return Retorna o valor de descrição atual.
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @return Retorna o valor de detalhe atual.
	 */
	public String getDetalhe() {
		return detalhe;
	}
	
}