/**
 * 
 */
package com.consilux.model;

import java.math.BigDecimal;
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
import com.consilux.model.beans.LogradouroBean;
import com.consilux.model.exception.ModelException;

/**
 * @author fos
 *
 */
public class Logradouro {
	Integer id;
	String descricao;
	BigDecimal latitude;
	BigDecimal longitude;
	
	/**
	 * @param bean Bean que possui os atributos do logradouro.
	 */
	private Logradouro(LogradouroBean bean) {
		this.id = bean.getId();
		this.descricao = bean.getDescricao();
		this.latitude = new BigDecimal(bean.getLatitude());
		this.longitude = new BigDecimal(bean.getLongitude());
	}

	public Logradouro(Integer id, String descricao, BigDecimal latitude,
			BigDecimal longitude) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.latitude = latitude;
		this.longitude = longitude;
	}


	/**
	 * Insere um novo registro de logradouro.
	 * @param bean Bean que possui os atributos do logradouro.
	 * @return Objeto Logradouro materializado.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static Logradouro incluiLogradouro(LogradouroBean bean) throws ConexaoException  {
		Logradouro lRet = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO logradouro (");
		sbSQL.append("		descricao,");
		sbSQL.append("		latitude,");
		sbSQL.append("		longitude");
		sbSQL.append("	) VALUES (?,?,?)");

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			//Ajustando os valores dos parametros:
			ps.setString(1, bean.getDescricao());
			BigDecimal latitude = new BigDecimal(bean.getLatitude()),
					   longitude= new BigDecimal(bean.getLongitude()); 
			ps.setBigDecimal(2, latitude);
			ps.setBigDecimal(3, longitude);

			if (ps.executeUpdate() > 0) {
				lRet = new Logradouro(bean);
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

		return lRet;
	}

	/**
	 * Busca logradouros no BD.
	 * @param mFiltros Filtros para a busca, regras implementadas: descricao.
	 * @return Lista de objetos Logradouro
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Logradouro> buscaLogradouroPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException {
		List<Logradouro> lRet = new ArrayList<Logradouro>();
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("SELECT id_logradouro,");
		sbSQL.append("		descricao,");
		sbSQL.append("		latitude,");
		sbSQL.append("		longitude");
		sbSQL.append("	FROM");
		sbSQL.append("		logradouro WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");

		//Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			mRegras.put("descricao", "descricao LIKE ?");
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			//Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Logradouro(
						rs.getInt("id_logradouro"),
						rs.getString("descricao"),
						rs.getBigDecimal("latitude"),
						rs.getBigDecimal("longitude")
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
	 * Busca um logradouro do BD por id.
	 * @param idLogradouro Identificador do logradouro
	 * @return Objeto Logradouro materializado.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static Logradouro buscaLogradouroPorIdLogradouro(Integer idLogradouro) throws ConexaoException {
		Logradouro lRet = null;
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("   SELECT ");
		sbSQL.append("		id_logradouro,");
		sbSQL.append("		descricao,");
		sbSQL.append("		latitude,");
		sbSQL.append("		longitude");
		sbSQL.append("	FROM");
		sbSQL.append("		logradouro WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_logradouro = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao(); 
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idLogradouro);

			rs = ps.executeQuery();
			if (rs.next()) {
				lRet =  new Logradouro(
						rs.getInt("id_logradouro"),
						rs.getString("descricao"),
						rs.getBigDecimal("latitude"),
						rs.getBigDecimal("longitude")
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

	public static List<Logradouro> buscaTodosLogradouros() throws ConexaoException, SQLException {
		Map<String,Object> mFiltro = new HashMap<String,Object>();
	    mFiltro.put("descricao","%"); //Lista todos.
		return Logradouro.buscaLogradouroPor(mFiltro);
	}

	/**
	 * Altera um registro de logradouro.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public void alteraLogradouro() throws ConexaoException {
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("UPDATE logradouro SET ");
		sbSQL.append("		descricao=?,");
		sbSQL.append("		latitude=?,");
		sbSQL.append("		longitude=? ");
		sbSQL.append("WHERE id_logradouro=?");

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			//Ajustando os valores dos parametros:
			ps.setString(1, this.descricao);
			ps.setBigDecimal(2, this.latitude);
			ps.setBigDecimal(3, this.longitude);
			ps.setInt(4, this.id);

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
	 * Preenche um bean com os dados deste logradouro.
	 * @param bean Objeto bean que será preenchido.
	 */
	public void getToLogradouroBean(LogradouroBean bean) {
		bean.setId(this.id);
		bean.setDescricao(this.descricao);
		bean.setLatitude(this.latitude.toString());
		bean.setLongitude(this.longitude.toString());
	}

	/**
	 * Preenche os dados deste logradouro vindos de um bean.
	 * @param bean Objeto bean que contém as informações.
	 */
	public void setFromLogradouroBean(LogradouroBean bean) {
		this.id = bean.getId();
		this.descricao = bean.getDescricao();
		this.latitude = new BigDecimal(bean.getLatitude());
		this.longitude = new BigDecimal(bean.getLongitude());
	}

	public Integer getId() {
		return id;
	}

	public String getDescricao() {
		return descricao;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}
	
}
