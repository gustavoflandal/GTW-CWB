/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Edson Jan Ferreira Lopes
  Data: 14/04/2010

  Descricao: Classe Retorna um relatório para a quantidade de infrações em processamento em processos que não sejam finais (remessas, Remessa de Inconsistencia).


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.consilux.infra.Relatorio;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Classe para buscar infrações em processos não finais
 * @author Edson Jan F Lopes- Consilux Tecnologia
 */
public class ImagensEmProcessamento extends Relatorio {
	
	private PreparedStatement ps;
	private int idProcesso;
	private String nomeProcesso;
	private int idEnquadramento;
	private Date data;
	private int total;
	private Boolean espera;
	private Connection conn = null;

	/**
	* Constrói o objeto RelatorioImagensEmProcessamento a partir dos parâmetros dados.
	*/
	public ImagensEmProcessamento(Boolean consistentes) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();

		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....	
		sbSQL.append("SELECT ");
		sbSQL.append("	  id_processo, ");
		sbSQL.append("	  id_enquadramento, ");
		sbSQL.append("	  nome_processo, ");
		sbSQL.append("	  data, ");
		sbSQL.append("	  espera,");
		sbSQL.append("	  total");
		if (consistentes != null)
			sbSQL.append(consistentes ? " FROM fcn_getImagensEmProcessamentoConsistentes() " : " FROM fcn_getImagensEmProcessamentoInconsistentes() ");
		else
			sbSQL.append(" FROM fcn_getImagensEmProcessamento() ");
		sbSQL.append(" ORDER BY 1, 4, 5, 2");	
		
		conn = Conexao.getConexao();
		ps = conn.prepareStatement(sbSQL.toString());
	}


	/**
	 * Retorna o valor do campo 'idProcesso' atual.
	 * @return the idProcesso
	 */
	public int getIdProcesso() {
		return this.idProcesso;
	}


	/**
	 * Retorna o valor do campo 'nomeProcesso' atual.
	 * @return the nomeProcesso
	 */
	public String getNomeProcesso() {
		return this.nomeProcesso;
	}


	/**
	 * Retorna o valor do campo 'idEnquadramento' atual.
	 * @return the idEnquadramento
	 */
	public int getIdEnquadramento() {
		return this.idEnquadramento;
	}


	/**
	 * Retorna o valor do campo 'data' atual.
	 * @return the data
	 */
	public Date getData() {
		return this.data;
	}

	/**
	 * Retorna o valor do campo 'espera' atual.
	 * @return the espera
	 */
	public Boolean getEspera() {
		return this.espera;
	}

	/**
	 * Retorna o valor do campo 'total' atual.
	 * @return the total
	 */
	public int getTotal() {
		return this.total;
	}

	/* (non-Javadoc)
	 * @see com.consilux.infra.Relatorio#montaRel()
	 */
	@Override
	protected ResultSet montaRel() throws SQLException {
		return ps.executeQuery();
	}

	/* (non-Javadoc)
	 * @see com.consilux.infra.Relatorio#constroi(java.sql.ResultSet)
	 */
	@Override
	protected void constroi(ResultSet rs) throws SQLException {
		idProcesso = rs.getByte("id_processo");
		idEnquadramento = rs.getInt("id_enquadramento");
		nomeProcesso = rs.getString("nome_processo");
		data = rs.getDate("data");
		espera = rs.getBoolean("espera");
		total = rs.getInt("total");
	}


	/* (non-Javadoc)
	 * @see com.consilux.infra.Relatorio#finaliza()
	 */
	@Override
	protected void finaliza() throws SQLException {
		this.conn.close();		
	}

}
