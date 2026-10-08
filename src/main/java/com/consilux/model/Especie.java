/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 22/12/2007

  Descricao: Classe de negócio para busca de espécies.

  Historico:

    $Log: Especie.java,v $
    Revision 1.1  2009/05/08 18:52:54  fos
    Primeira versão postada no CVS.


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe de negócio para busca de espécies.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.1 $ $Date: 2009/05/08 18:52:54 $ $Author: fos $
 */
public class Especie {
	private int id;
	private String descricao;

	/**
	 * Constrói o objeto Especie com os seus respectivos atributos.
	 * @param id Identificador para a espécie
	 * @param descricao Descrição da espécie
	 */
	private Especie(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	/**
	 * Busca todas as espécies.
	 * @return Lista de objetos Especie
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<Especie> buscaTodasEspecies() throws ConexaoException  {
		List<Especie> lRet = new ArrayList<Especie>();

		String sSQL = "SELECT id_especie, descricao " +
		"FROM cad_especie ORDER BY descricao";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sSQL);
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new Especie(
						rs.getInt("id_especie"),
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
	 * Busca uma espécie no BD.
	 * @param idEspecie Identificador da especie CET
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static Especie buscaEspeciePorId(Integer idEspecie) throws ConexaoException {
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....		
		sbSQL.append("SELECT id_especie,");
		sbSQL.append("		descricao");
		sbSQL.append("	FROM");
		sbSQL.append("		cad_especie WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		id_especie = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());				
			ps.setInt(1, idEspecie);

			rs = ps.executeQuery();
			if (rs.next()) {
				return new Especie(
						rs.getInt("id_especie"),
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
	 * Identificador da espécie.
	 * @return Identificador
	 */
	public int getid() {
		return id;
	}
	/**
	 * Descrição da espécie.
	 * @return Descrição
	 */
	public String getDescricao() {
		return descricao;
	}
}
