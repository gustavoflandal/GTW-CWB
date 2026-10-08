/**********************************************************************************

  Projeto: GTW-1.4.1
  Nome do Modulo: com.consilux.model

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 23/04/2010

  Descricao: XXX

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe de negócio para controlar as imagens de croqui das pistas.
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

/**
 * Classe de negócio para controlar as imagens de croqui das pistas.
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */
public class LocalPistaCroqui {
	private Integer idLocal = null;
	private Integer pista = null;
	private byte imagem[];
	
	/**
	* Constrói o objeto LocalPistaCroqui a partir dos parâmetros dados.
	* @param idLocal
	* @param pista
	* @param imagem
	*/
	private LocalPistaCroqui(Integer idLocal, Integer pista, byte[] imagem) {
		super();
		this.idLocal = idLocal;
		this.pista = pista;
		this.imagem = imagem;
	}

	/**
	 * Busca imagens de croqui no BD.
	 * @param idLocal Identificador do local
	 * @param pista Identificador da pista.
	 * @return Objeto LocalPistaCroqui materializado.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static LocalPistaCroqui buscaLocalPistaCroquiPorIdLocalPista( Integer idLocal, Integer pista )
	throws ConexaoException, SQLException  {
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("  SELECT id_local, pista, imagem");
		sbSQL.append("	FROM");
		sbSQL.append("		local_pista_croqui WITH (NOLOCK)" );
		sbSQL.append("	WHERE ");
		sbSQL.append(   "id_local = ? AND pista = ?");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idLocal);		
			ps.setInt(2, pista);		
		
			rs = ps.executeQuery();
			if (rs.next()) {
				return new LocalPistaCroqui(
							rs.getInt("id_local"),
							rs.getInt("pista"),
							rs.getBytes("imagem")
						);
			}
			else
				return null;
			
		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		
	}		

	/**
	 * Retorna o valor do campo 'idLocal' atual.
	 * @return the idLocal
	 */
	public Integer getIdLocal() {
		return this.idLocal;
	}

	/**
	 * Retorna o valor do campo 'pista' atual.
	 * @return the pista
	 */
	public Integer getPista() {
		return this.pista;
	}

	/**
	 * Retorna o valor do campo 'imagem' atual.
	 * @return the imagem
	 */
	public byte[] getImagem() {
		return this.imagem;
	}
	
}
