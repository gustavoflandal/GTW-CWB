/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.model

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 20/08/2009

  Descricao: Classe de controle para pistas.

  Historico:

    $Log$

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
 * Classe de controle para pistas.
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public class Pista implements Comparable<Pista> {
	private Integer idLocal;
	private Integer codPista;
	private Integer codPistaAlternativo;
	private String nomePista;
	private Integer pista;

	protected Pista(Integer idLocal, Integer codPista,
			Integer codPistaAlternativo, String nomePista, Integer pista) {
		super();
		this.idLocal = idLocal;
		this.codPista = codPista;
		this.codPistaAlternativo = codPistaAlternativo;
		this.nomePista = nomePista;
		this.pista = pista;
	}

	public static List<Pista> buscarPistaPor(Map<String,Object> mFiltros) throws ConexaoException, ModelException, SQLException {
		List<Pista> lRet = new ArrayList<Pista>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT ");
		sbSQL.append("lv.id_local, ");
		sbSQL.append("p.cod_pista, ");
		sbSQL.append("p.cod_pista_alternativo, ");
		sbSQL.append("p.nome_pista, ");
		sbSQL.append("p.id_pista "); 
		sbSQL.append("FROM configuracao_equipamento_pista p ");
		sbSQL.append("JOIN local_vigente lv ON lv.id_configuracao_equipamento = p.id_configuracao_equipamento ");

		// Ajustando os valores do where para os filtros:
		if (mFiltros.size() > 0)
			sbSQL.append(" WHERE ");
		
		Map<String, String> mRegras = new HashMap<String, String>();
		
		sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));

		sbSQL.append(" ORDER BY lv.id_local, p.cod_pista, p.id_pista");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
		
			rs = ps.executeQuery();

			while (rs.next()) {
				lRet.add(new Pista( 
						rs.getInt("id_local"),
						rs.getInt("cod_pista"),
						rs.getInt("cod_pista_alternativo"),
						rs.getString("nome_pista"),
						rs.getInt("id_pista")
					)
				);
			}
		}
		finally {
			conn.close();							
		}
		return lRet;
	}

	/**
	 * Retorna o valor do campo 'idLocal' atual.
	 * @return the idLocal
	 */
	public Integer getIdLocal() {
		return this.idLocal;
	}

	/**
	 * Retorna o valor do campo 'nomePista' atual.
	 * @return the nomePista
	 */
	public String getNomePista() {
		return this.nomePista;
	}

	/**
	 * Retorna o valor do campo 'pista' atual.
	 * @return the pista
	 */
	public Integer getPista() {
		return this.pista;
	}

	/**
	 * Retorna o valor do campo 'codPista' atual.
	 * @return the codPista
	 */
	public Integer getCodPista() {
		return this.codPista;
	}

	/**
	 * Retorna o valor do campo 'codPistaAlternativo' atual.
	 * @return the codPistaAlternativo
	 */
	public Integer getCodPistaAlternativo() {
		return this.codPistaAlternativo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Pista o) {
		Integer ret = this.codPista.compareTo(o.codPista);
		if (ret == 0)
			ret = this.codPistaAlternativo.compareTo(o.codPistaAlternativo);
		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idLocal == null) ? 0 : idLocal.hashCode());
		result = prime * result + ((pista == null) ? 0 : pista.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pista other = (Pista) obj;
		if (idLocal == null) {
			if (other.idLocal != null)
				return false;
		} else if (!idLocal.equals(other.idLocal))
			return false;
		if (pista == null) {
			if (other.pista != null)
				return false;
		} else if (!pista.equals(other.pista))
			return false;
		return true;
	}
	
}
