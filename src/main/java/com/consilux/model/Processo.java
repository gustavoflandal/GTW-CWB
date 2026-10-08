/**********************************************************************************

  Projeto: GTW
  Nome do Módulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 15/08/2008

  Descrição: {descr}

  Histórico:

    $Log: Processo.java,v $
    Revision 1.4  2009/03/12 13:07:39  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.3  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.1  2008/08/18 14:50:55  fos
    Primeira versão postada no CVS.


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 *
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2009/03/12 13:07:39 $ $Author: raoni $
 */
public class Processo {
	private Integer idProcesso;
	private String nome;
	private String spuExecucaoAutomatica;

	/**
	* Constrói o objeto Processo a partir dos parâmetros dados.
	* @param idProcesso
	* @param nome
	* @param spuExecucaoAutomatica
	*/
	private Processo(Integer idProcesso, String nome,
			String spuExecucaoAutomatica) {
		super();
		this.idProcesso = idProcesso;
		this.nome = nome;
		this.spuExecucaoAutomatica = spuExecucaoAutomatica;
	}

	/**
	 * Busca todos os processos no BD.
	 * @return Lista com os processos.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Processo> buscaTodosProcessos() throws ConexaoException  {
		List<Processo> lRet = new ArrayList<Processo>();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....		
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("SELECT id_processo,");
		sbSQL.append("		nome,");
		sbSQL.append("		spu_execucao_automatica");
		sbSQL.append("	FROM");
		sbSQL.append("		processo WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		ativo = 1 AND");
		sbSQL.append(" 	((numero_iteracoes_consistentes > 0 AND");
		sbSQL.append("		  numero_iteracoes_inconsistentes > 0) OR");
		sbSQL.append("		 id_processo_proximo IS NULL)");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Processo(
						rs.getInt("id_processo"),
						rs.getString("nome"),
						rs.getString("spu_execucao_automatica")
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
	 * Busca os processos de término no BD.
	 * @return Lista com os processos.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Processo> buscaProcessosRemessa() throws ConexaoException, SQLException {
		return buscaProcessosRemessa(true);
	}

	/**
	 * Busca os processos de término no BD.
	 * @return Lista com os processos.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Processo> buscaProcessosRemessa(Boolean incluiInconsistentes) throws ConexaoException, SQLException {
		Map<String, TipoRemessa> mapaTipos = ConfiguracaoProvider.getInstance().getConfiguracaoRemessa().getMapaTipos();
		
		List<String> ids = new ArrayList<String>();
		
	    for (Map.Entry<String, TipoRemessa> e: mapaTipos.entrySet()) {
	    	ids.add(String.valueOf(((TipoRemessa)e.getValue()).getIdProcesso()));
	    }
	    
		List<Processo> lRet = new ArrayList<Processo>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT id_processo,");
		sbSQL.append("		nome,");
		sbSQL.append("		spu_execucao_automatica");
		sbSQL.append("	FROM");
		sbSQL.append("		processo WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		ativo = 1 AND ");
		sbSQL.append(" id_processo in (" + Funcoes.concatStringArray(ids, ",") + ") AND ");
		sbSQL.append(!incluiInconsistentes ? " recebe_consistentes_inconsistentes = 1 AND " : "");
		sbSQL.append(" 	id_processo_proximo IS NULL");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Processo(
							rs.getInt("id_processo"),
							rs.getString("nome"),
							rs.getString("spu_execucao_automatica")
					)
				);
			}

		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return lRet;
	}

	/**
	 * Busca os processos que devem ser executados automaticamente.
	 * @return Lista com os processos.
	 * @throws ConexaoException
	 * @throws SQLException 
	 */
	public static List<Processo> buscaProcessosExecucaoAutomatica() throws ConexaoException, SQLException {
		List<Processo> lRet = new ArrayList<Processo>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT id_processo,");
		sbSQL.append("		nome,");
		sbSQL.append("		spu_execucao_automatica");
		sbSQL.append("	FROM");
		sbSQL.append("		processo WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		ativo = 1 AND execucao_automatica = 1");
		sbSQL.append("	ORDER BY id_processo DESC");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Processo(
						rs.getInt("id_processo"),
						rs.getString("nome"),
						rs.getString("spu_execucao_automatica")
				)
				);
			}

		}		
		finally {
			if (conn != null)
				conn.close();							
		}

		return lRet;
	}

	/**
	 * @return Retorna o valor de idProcesso atual.
	 */
	public Integer getIdProcesso() {
		return idProcesso;
	}

	/**
	 * @return Retorna o valor de nome atual.
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * Retorna o valor do campo 'sqlExecucaoAutomatica' atual.
	 * @return the sqlExecucaoAutomatica
	 */
	public String getSpuExecucaoAutomatica() {
		return spuExecucaoAutomatica;
	}

}
