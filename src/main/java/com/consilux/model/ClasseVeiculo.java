/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 05/02/2007

  Descricao: Classe de negócio para busca de classes de veículos.

  Historico:

    $Log: ClasseVeiculo.java,v $
    Revision 1.9  2009/03/24 21:37:48  fos
    Agora possui método para listar todas as classes de veículo.

    Revision 1.8  2009/03/12 13:07:37  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.7  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.5  2008/08/12 12:59:10  fos
    Agora a classe já trabalha com o banco de dados novo.

    Revision 1.4  2007/07/06 13:04:15  fos
    Colocado unlocks explicitos nas querys.

    Revision 1.3  2007/05/08 12:37:57  fos
    Solução temporária até que a base estaja íntegra com informações sobre a classe dos veículos.

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


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
 * Classe de negócio para busca de classes de veículos.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.9 $ $Date: 2009/03/24 21:37:48 $ $Author: fos $
 */
public class ClasseVeiculo {
	private String idClasse;
	private String descricao;
	
	/**
  	 * Constrói o objeto ClasseVeiculo com os seus respectivos atributos.
	 * @param idClasse Identificador da classe no BD.
	 * @param descricao Descrição da classe no BD.
	 */
	private ClasseVeiculo(String idClasse, String descricao) {
		this.idClasse = idClasse;
		this.descricao = descricao;
	}
	/**
	 * Busca uma classe de veículo no BD.
	 * @param idClasse Identificador da classe no BD. 
	 * @return Objeto materializado ou null se não encontrar
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static ClasseVeiculo buscaClasseVeiculo(String idClasse) throws ConexaoException  {
		
		StringBuilder sbSQL = new StringBuilder();
		
		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("SELECT id_classe,");
		sbSQL.append("		descricao");
		sbSQL.append("	FROM");
		sbSQL.append("		classe_veiculo WITH (NOLOCK) ");
		sbSQL.append("	WHERE");
		sbSQL.append("		id_classe = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idClasse);
			rs = ps.executeQuery();
			if (rs.next()) {
				return new ClasseVeiculo(
						rs.getString("id_classe"),
						rs.getString("descricao"));
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
	 * Busca todos as classes no BD.
	 * @return Lista com os objetos ClasseVeiculo materializados.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<ClasseVeiculo> buscaTodasClassesVeiculo() throws ConexaoException  {
		List<ClasseVeiculo> lRet = new ArrayList<ClasseVeiculo>();
		StringBuilder sbSQL = new StringBuilder();
		
		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....			
		sbSQL.append("SELECT id_classe,");
		sbSQL.append("		descricao");
		sbSQL.append("	FROM");
		sbSQL.append("		classe_veiculo WITH (NOLOCK) ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			while (rs.next()) {
				lRet.add(new ClasseVeiculo(
						rs.getString("id_classe"),
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
	 * Busca todos as classes no BD - Versão CAV.
	 * @return Lista com os objetos ClasseVeiculo materializados.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<ClasseVeiculo> buscaTodasClassesVeiculoCAV() throws ConexaoException  {
		
		List<ClasseVeiculo> lRet = new ArrayList<ClasseVeiculo>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT cs.id_classe, ");
		sbSQL.append(" 		  cs.descricao ");
		sbSQL.append(" FROM   ( ");
		sbSQL.append(" 			SELECT LTRIM(RTRIM(id_classe)) AS id_classe, ");
		sbSQL.append(" 		  		   LTRIM(RTRIM(descricao)) AS descricao ");
		sbSQL.append(" 			FROM   classe_veiculo (NOLOCK) ");
		sbSQL.append(" 			WHERE  LTRIM(RTRIM(id_classe)) != '' ");
		sbSQL.append(" 			UNION ");
		sbSQL.append(" 			SELECT LTRIM(RTRIM(CAST(id_tipo AS VARCHAR(2)))) AS id_classe, ");
		sbSQL.append(" 		  		   LTRIM(RTRIM(dbo.fcn_InitCap(descricao))) AS descricao ");
		sbSQL.append(" 			FROM   cad_tipo (NOLOCK) ");
		sbSQL.append(" 			WHERE  id_tipo = 7 ");
		sbSQL.append(" ) AS cs ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  cs.descricao ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();
			
			while (rs.next()) {
				lRet.add(new ClasseVeiculo(
						rs.getString("id_classe"),
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
	 * Identificador da Classe de Veículo.
	 * @return Identificador
	 */
	public String getCodigo() {
		return idClasse;
	}
	/**
	 * Descrição da Classe de Veículo.
	 * @return Descrição
	 */
	public String getDescricao() {
		return descricao;
	}
	
	
	/**
	 * Identificador da Classe de Veículo.
	 * @return Identificador
	 */
	public String getIdClasse() {
		return idClasse;
	}

}
