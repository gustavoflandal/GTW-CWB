/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 14/01/2008

  Descricao: Classe que busca o grupos no BD.

  Historico:

    $Log: Grupo.java,v $
    Revision 1.8  2009/04/22 14:58:33  raoni
    Modificado função de busca para retornar um bean correspondente ao Grupo.

    Revision 1.7  2009/04/16 20:38:59  raoni
    Adicionado mais tipos de buscas.

    Revision 1.6  2009/03/12 13:07:37  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.5  2009/01/16 13:58:37  fos
    Identação da query.

    Revision 1.4  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.2  2008/07/23 14:24:49  fos
    Ajustado o nome da função.

    Revision 1.1  2008/01/15 14:21:17  fos
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
import com.consilux.model.beans.GrupoBean;
import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.GrupoGwtBean;

/**
 * Classe que busca o grupos no BD.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.8 $ $Date: 2009/04/22 14:58:33 $ $Author: raoni $
 */
public class Grupo {
	Integer id;
	String descricao;
	
	/**
	 * Constrói o objeto com seus respectivos atributos.
	 * @param id Identificador do grupo
	 * @param descricao Descrição do grupo
	 */
	private Grupo(Integer id, String descricao) {
		super();
		this.id = id;
		this.descricao = descricao;
	}
	
	/**
	 * @param bean Bean que possui os atributos do grupo.
	 */
	public Grupo(GrupoBean bean) {
		this.id = bean.getId();
		this.descricao = bean.getDescricao();
	}

	/**
	 * Insere um novo registro de grupo.
	 * @param bean Bean que possui os atributos do grupo.
	 * @return Objeto Grupo materializado.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static Grupo incluiGrupo(GrupoBean bean) throws ConexaoException, SQLException {
		Grupo uRet = null;
		
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("INSERT INTO sis_grupo (descricao) VALUES (?)");
			
			//Ajustando os valores dos parametros:
			ps.setString(1, bean.getDescricao());
			if (ps.executeUpdate() > 0) {
				uRet = new Grupo(bean);
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}		
		finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}				

		return uRet;
	}
	
	/**
	 * Altera um registro de grupo.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public void alteraGrupo() throws ConexaoException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("UPDATE sis_grupo SET descricao=? WHERE id_grupo=?");
			
			//Ajustando os valores dos parametros:
			ps.setString(1, this.descricao);
			ps.setInt(2, this.id);
			
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}		
		finally {
			try {
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
	 * Remove um registro de grupo.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public void removeGrupo() throws ConexaoException, SQLException {

		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("DELETE FROM sis_grupo WHERE id_grupo=?");
			
			//Ajustando os valores dos parametros:
			ps.setInt(1, this.id);
			
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}		
		finally {
			try {
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
	 * Busca grupos no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: descricao.
	 * @return Lista de objetos Grupo
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Grupo> buscaGrupoPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException {
		List<Grupo> lRet = new ArrayList<Grupo>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT id_grupo,");
		sbSQL.append("		descricao");
		sbSQL.append("	FROM");
		sbSQL.append("		sis_grupo WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("descricao", "descricao LIKE ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Grupo(
					rs.getInt("id_grupo"),
					rs.getString("descricao"))
				);
			}
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
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
	 * Busca um grupo do BD por id.
	 * @param idGrupo Identificador do grupo
	 * @return Objeto Grupo materializado.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static Grupo buscaGrupoPorIdGrupo(Integer idGrupo) throws ConexaoException {
		Grupo uRet = null;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("		id_grupo,");
		sbSQL.append("		descricao");
		sbSQL.append("	FROM");
		sbSQL.append("		sis_grupo WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_grupo = ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idGrupo);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				uRet =  new Grupo(
						rs.getInt("id_grupo"),
						rs.getString("descricao")
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
		return uRet;
	}
	
	/**
	 * Busca os grupos a quais um usuário pertence.
	 * @param idUsuario Identificador do usuário
	 * @return Uma lista de objeto GrupoBean materializados.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<GrupoBean> buscaGruposPorIdUsuario(int idUsuario) throws ConexaoException {

		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT");
		sbSQL.append("   sg.id_grupo,");
		sbSQL.append("   sg.descricao");
		sbSQL.append(" FROM");
		sbSQL.append("   sis_usuario_grupo sug WITH (NOLOCK)");
		sbSQL.append("   INNER JOIN  sis_grupo sg WITH (NOLOCK)");
		sbSQL.append("     ON sg.id_grupo = sug.id_grupo");
		sbSQL.append(" WHERE");
		sbSQL.append("   sug.id_usuario = ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<GrupoBean> lRet = new ArrayList<GrupoBean>();
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idUsuario);

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new GrupoBean(
						rs.getInt("id_grupo"),
						rs.getString("descricao")
				));
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
	 * Busca todos os grupos do BD.
	 * @return Uma lista de objeto GrupoBean materializados.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<GrupoBean> buscaTodosGrupos() throws ConexaoException {
		List<GrupoBean> lRet = new ArrayList<GrupoBean>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("		id_grupo,");
		sbSQL.append("		descricao");
		sbSQL.append("	FROM");
		sbSQL.append("		sis_grupo WITH (NOLOCK) ");
		sbSQL.append("	ORDER BY descricao");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new GrupoBean(
						rs.getInt("id_grupo"),
						rs.getString("descricao")
						));			
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
	 * Busca todos os grupos do BD.
	 * @return Uma lista de objeto GrupoBean materializados.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<GrupoBean> buscaGruposAbaixo(Integer idUsuario) throws ConexaoException {
		List<GrupoBean> lRet = new ArrayList<GrupoBean>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT"); 
		sbSQL.append("		id_grupo,");
		sbSQL.append("		descricao");
		sbSQL.append("	FROM");
		sbSQL.append("		fcn_getGruposAbaixo(?)");
		sbSQL.append("	ORDER BY descricao");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idUsuario);
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new GrupoBean(
						rs.getInt("id_grupo"),
						rs.getString("descricao")
						));			
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
	 * Preenche um bean com os dados deste grupo.
	 * @param bean Objeto bean que será preenchido.
	 */
	public void getToGrupoBean(GrupoBean bean) {
		bean.setId(this.id);
		bean.setDescricao(this.descricao);
	}

	/**
	 * Converte um bean do GTW para um bean do GWT.
	 * @param grupo Um bean de grupo.
	 * @return Um bean de grupo do GWT. 
	 * @throws ModelException 
	 */
	public static GrupoGwtBean toGwtBean(GrupoBean grupo)
	throws ModelException {
		
		if (grupo == null)
			throw new ModelException("Argumento nulo: grupo");
		
		GrupoGwtBean gwtBean = new GrupoGwtBean();
		gwtBean.setDescricao(grupo.getDescricao());
		gwtBean.setIdGrupo(grupo.getId());
		
		return gwtBean;
	}
	
	/**
	 * Converte uma coleção de beans do GTW para uma lista de bean do GWT.
	 * @param listaGrupo Uma coleção de beans de grupo.
	 * @return Uma lista de bean de grupo do GWT.
	 * @throws ModelException   
	 */
	public static List<GrupoGwtBean> toGwtBean(Iterable<GrupoBean> listaGrupo)
	throws ModelException {
		
		if (listaGrupo == null)
			throw new ModelException("Argumento nulo: listaGrupo");
		
		List<GrupoGwtBean> lRet = new ArrayList<GrupoGwtBean>();
		GrupoGwtBean gwtBean = null;
		for (GrupoBean grp : listaGrupo) {
			gwtBean = new GrupoGwtBean();
			gwtBean.setDescricao(grp.getDescricao());
			gwtBean.setIdGrupo(grp.getId());
			lRet.add(gwtBean);
		}
		
		return lRet;
	}	
	
	/**
	 * Preenche os dados deste grupo vindos de um bean.
	 * @param bean Objeto bean que contém as informações.
	 */
	public void setFromGrupoBean(GrupoBean bean) {
		this.descricao = bean.getDescricao();
	}

	/**
	 * @return Retorna o valor de id atual.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return Retorna o valor de descricao atual.
	 */
	public String getDescricao() {
		return descricao;
	}
	
}
