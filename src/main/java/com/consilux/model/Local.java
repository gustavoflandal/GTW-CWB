/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 16/05/2007

  Descricao: Classe de negócio que busca informações básicas de um local.
  Historico:

    $Log: Local.java,v $
    Revision 1.15  2009/05/12 14:50:45  raoni
    Removido DaoException, para um controle mais granular em favor de ConexaoException, SQLException e ModelException.

    Revision 1.14  2009/05/11 17:32:29  raoni
    Cdiado overload para o método "buscaLocalPorIdConfigEquip", que permite receber uma conexão como parâmetro.

    Revision 1.13  2009/04/14 16:57:20  fos
    Não faz mais a busca na local_vigente, porque esta vindo a chave completa.

    Revision 1.12  2009/03/31 20:28:01  raoni
    Adicionado método para busca de locais para o cadastro de manutenção.

    Revision 1.11  2009/03/12 13:07:37  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.10  2009/03/10 14:35:46  raoni
    Otimizado o uso de StringBuilder.
    Utiliza LinkedList quando possível.
    Adicionado lógica para realizar o clean up (close) dos Statements.

    Revision 1.9  2009/01/22 14:24:20  fos
    Agora busca também o identificador do equipamento.

    Revision 1.8  2009/01/16 13:59:48  fos
    Agora possui método para ligar/desligar um local.

    Revision 1.7  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.5  2008/10/13 17:13:22  fos
    Agora busca na view local vigente que contém as últimas atualizações dos locais.

    Revision 1.4  2008/08/21 21:09:48  fos
    Alterada nome de tabelas e campos no BD.

    Revision 1.3  2008/08/12 13:01:31  fos
    Agora a classe já trabalha com o banco de dados novo.

    Revision 1.2  2008/07/23 14:24:50  fos
    Ajustado o nome da função.

    Revision 1.1  2007/07/06 13:05:17  fos
    Primeira versão postada no CVS.


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio que busca informações básicas de um local.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.15 $ $Date: 2009/05/12 14:50:45 $ $Author: raoni $
 */
public class Local implements Comparable<Local> {
	private Integer idLocal;
	private Byte sequenciaLocal;
	private Integer idConfiguracaoEquipamento;
	private String nome;

	public Local(Integer idLocal, Byte sequenciaLocal,
			Integer idConfiguracaoEquipamento, String nome) {
		super();
		this.idLocal = idLocal;
		this.sequenciaLocal = sequenciaLocal;
		this.idConfiguracaoEquipamento = idConfiguracaoEquipamento;
		this.nome = nome;
	}

	/**
	 * Busca locais no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: nome, grupo.
	 * @param iOrdem Número da coluna de ordem.
	 * @return Lista de objetos Local
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Local> buscaLocalPor(Map<String,Object> mFiltros, Integer iOrdem) throws ConexaoException, SQLException {
		List<Local> lRet = new ArrayList<Local>();
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("SELECT l.id_local,");
		sbSQL.append("		l.sequencia_local,");
		sbSQL.append("		l.id_configuracao_equipamento,");
		sbSQL.append("		l.nome");
		sbSQL.append("	FROM");
		sbSQL.append("		local l WITH (NOLOCK) ");
		sbSQL.append("		INNER JOIN configuracao_equipamento e ON e.id_configuracao_equipamento = l.id_configuracao_equipamento");
		sbSQL.append("	WHERE ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("nome", "l.nome LIKE ?");
		mRegras.put("grupo", "e.id_grupo_equipamento = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY " + iOrdem);
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Local(
						rs.getInt("id_local"),
						rs.getByte("sequencia_local"),
						rs.getInt("id_configuracao_equipamento"),
						rs.getString("nome")
				)
				);
			}
		} catch (ModelException e) {
			throw new SQLException("Erro ao montar SQL.", e);
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
	 * Busca um local no BD.
	 * @param id Identificador do local.
	 * @param sequencia Sequência do local.
	 * @return Objeto Local materializado, ou null se não encontrar.
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static Local buscaLocalPorId(Integer id, Integer sequencia) throws ConexaoException {
		Local lRet = null;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("SELECT l.id_local,");
		sbSQL.append("		  l.sequencia_local,");
		sbSQL.append("		  l.id_configuracao_equipamento,");
		sbSQL.append("	 	  l.nome");
		sbSQL.append("	FROM");
		sbSQL.append("		local l WITH (NOLOCK) ");
		sbSQL.append("	WHERE id_local = ? AND sequencia_local = ?");
		sbSQL.append("	ORDER BY l.nome");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, id);
			ps.setInt(2, sequencia);

			rs = ps.executeQuery();
			if (rs.next()) {
				lRet = new Local(
						rs.getInt("id_local"), 
						rs.getByte("sequencia_local"), 
						rs.getInt("id_configuracao_equipamento"), 
						rs.getString("nome"));
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
	 * Lista os locais vigentes.
	 * @return Lista com objetos Local materializados.
	 * @return Objeto Local materializado, ou null se não encontrar.
	 * @throws ConexaoException
	 */
	public static List<Local> listarLocaisVigentes() throws ConexaoException {
		
		List<Local> listaLocaisVigentes = new ArrayList<Local>();
		Local local = null;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("SELECT l.id_local,");
		sbSQL.append("		  l.sequencia_local,");
		sbSQL.append("		  l.id_configuracao_equipamento,");
		sbSQL.append("	 	  l.nome");
		sbSQL.append("	FROM");
		sbSQL.append("		local_vigente l WITH (NOLOCK) ");
		sbSQL.append("	ORDER BY l.nome");		

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			rs = ps.executeQuery();
			while (rs.next()) {
				local = new Local(
						rs.getInt("id_local"), 
						rs.getByte("sequencia_local"), 
						rs.getInt("id_configuracao_equipamento"), 
						rs.getString("nome"));
				listaLocaisVigentes.add(local);
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

		return listaLocaisVigentes;
	}	
	
	/**
	 * Busca um local no BD, baseado em um id de 
	 * configuração equipamento.
	 * @param conn Uma conexão a banco de dados.
	 * @param idConfiguracaoEquipamento Identificador
	 * da configuração do equipamento.
	 * @return Objeto Local materializado, ou null se não encontrar.
	 */
	public static Local buscaLocalPorIdConfigEquip(Connection conn, int idConfiguracaoEquipamento)
	throws SQLException {
		
		Local lRet = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT l.id_local,");
		sbSQL.append("		  l.sequencia_local,");
		sbSQL.append("		  l.id_configuracao_equipamento,");
		sbSQL.append("	 	  l.nome");
		sbSQL.append("	FROM");
		sbSQL.append("		[local] l WITH (NOLOCK) ");
		sbSQL.append("	WHERE id_configuracao_equipamento = ?");
		sbSQL.append("	ORDER BY l.nome");		
		
		PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
		ps.setInt(1, idConfiguracaoEquipamento);
		ResultSet rs = ps.executeQuery();
			
		if (rs.next()) {
			lRet = new Local(
				rs.getInt("id_local"), 
				rs.getByte("sequencia_local"), 
				rs.getInt("id_configuracao_equipamento"), 
				rs.getString("nome"));
		}
			
		return lRet;
	}
	
	/**
	 * Busca um local no BD, baseado em um id de 
	 * configuração equipamento.
	 * @param idConfiguracaoEquipamento Identificador
	 * da configuração do equipamento.
	 * @return Objeto Local materializado, ou null se não encontrar.
	 * @throws SQLException
	 * @throws ConexaoException 
	 */
	public static Local buscaLocalPorIdConfigEquip(int idConfiguracaoEquipamento)
	throws SQLException, ConexaoException {

		Connection conn = Conexao.getConexao();
		
		try {
			return buscaLocalPorIdConfigEquip(conn, idConfiguracaoEquipamento);
		}
		finally {
			conn.close();
		}
		
	}	
	
	public void setLocalAtivo(Boolean bValor) throws ConexaoException, ModelException {
		StringBuilder sbSQL = new StringBuilder();

		// desativa todas as configurações deste local.
		sbSQL.append(" UPDATE configuracao_equipamento SET ativo = 0");
		sbSQL.append(" WHERE id_configuracao_equipamento IN ");
		sbSQL.append("		(SELECT id_configuracao_equipamento FROM local WHERE");
		sbSQL.append("	 	 id_local = ?);");
		
		// coloca o valor correspondente na configuração específica
		sbSQL.append(" UPDATE configuracao_equipamento SET ativo = ?");
		sbSQL.append(" WHERE id_configuracao_equipamento = ?;");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, this.idLocal);
			ps.setBoolean(2, bValor);
			ps.setInt(3, this.idConfiguracaoEquipamento);

			if (!(ps.executeUpdate() > 0))
				throw new ModelException("Não foi possível ajustar o equipamento corretamente.");
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

	public String getNome() {
		return nome;
	} 

	public Integer getIdLocal() {
		return idLocal;
	}

	public Byte getSequenciaLocal() {
		return sequenciaLocal;
	}

	public Integer getIdConfiguracaoEquipamento() {
		return idConfiguracaoEquipamento;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Local o) {
		return this.idLocal.compareTo(o.idLocal);	
	}
}
