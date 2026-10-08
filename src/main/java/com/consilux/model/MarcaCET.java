/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 22/12/2007

  Descricao: Classe de negócio para busca de marcas específicas para CET.

  Historico:

    $Log: MarcaCET.java,v $
    Revision 1.3  2009/05/08 18:54:14  fos
    Consertado erro de copy/paste.

    Revision 1.2  2009/03/12 13:07:38  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.1  2009/01/12 12:49:42  fos
    Recuperação de repositório.


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
 * Classe de negócio para busca de marcas específicas para CET.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.3 $ $Date: 2009/05/08 18:54:14 $ $Author: fos $
 */
public class MarcaCET {
	private int id;
	private String descricao;

	/**
	 * Constrói o objeto MarcaCET com os seus respectivos atributos.
	 * @param id Identificador para o motivo
	 * @param descricao Descrição do motivo
	 */
	private MarcaCET(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	/**
	 * Busca todos as marcas CET disponíveis.
	 * @return Lista de objetos MarcaCET
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<MarcaCET> buscaTodasMarcasCET() throws ConexaoException  {
		List<MarcaCET> lRet = new ArrayList<MarcaCET>();

		String sSQL = "SELECT id_marca_cet, descricao " +
		"FROM cad_marca_cet ORDER BY descricao";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sSQL);
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new MarcaCET(
						rs.getInt("id_marca_cet"),
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
	 * Busca uma marca CET no BD.
	 * @param idMarcaCET Identificador da marca CET
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static MarcaCET buscaMarcaCETPorId(Integer idMarcaCET) throws ConexaoException {
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....		
		sbSQL.append("SELECT id_marca_cet,");
		sbSQL.append("		descricao");
		sbSQL.append("	FROM");
		sbSQL.append("		cad_marca_cet WITH (NOLOCK)");
		sbSQL.append("	WHERE");
		sbSQL.append("		id_marca_cet = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());				
			ps.setInt(1, idMarcaCET);

			rs = ps.executeQuery();
			if (rs.next()) {
				return new MarcaCET(
						rs.getInt("id_marca_cet"),
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
	 * Identificador da marca CET.
	 * @return Identificador
	 */
	public int getid() {
		return id;
	}
	/**
	 * Descrição da marca CET.
	 * @return Descrição
	 */
	public String getDescricao() {
		return descricao;
	}
}
