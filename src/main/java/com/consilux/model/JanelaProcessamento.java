/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.model

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 27/05/2009

  Descricao: XXX

  Historico:

    $Log: JanelaProcessamento.java,v $
    Revision 1.1  2009/05/28 13:50:18  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * XXX
 * @author fos
 * @version $Revision: 1.1 $ $Date: 2009/05/28 13:50:18 $ $Author: fos $
 */

public class JanelaProcessamento {
	private Integer idUsuario;
	private String usuario;
	private Integer idProcesso;
	private String nomeProcesso;
	private Integer conta;

	/**
	* Constrói o objeto JanelaProcessamento a partir dos parâmetros dados.
	* @param idUsuario
	* @param usuario
	* @param idProcesso
	* @param processo
	* @param conta
	*/
	public JanelaProcessamento(Integer idUsuario, String usuario,
			Integer idProcesso, String processo, Integer conta) {
		this.idUsuario = idUsuario;
		this.usuario = usuario;
		this.idProcesso = idProcesso;
		this.nomeProcesso = processo;
		this.conta = conta;
	}

	/**
	 * Busca a janela de infrações de cada usuário
	 * @param mFiltros Filtros para a busca.
	 * @return Lista de objetos Infracao
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<JanelaProcessamento> buscaJanelaProcessamento(Integer idLocal, Date dataIni, Date dataFim) throws ConexaoException {
		List<JanelaProcessamento> lRet = new ArrayList<JanelaProcessamento>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("{call spu_janela_processamento(?, ?, ?)}");

		Connection conn = null;
		CallableStatement cs = null;
		ResultSet rs = null;		

		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(sbSQL.toString());
			cs.setInt(1, idLocal);
			cs.setTimestamp(2, new Timestamp(dataIni.getTime()));
			cs.setTimestamp(3, new Timestamp(dataFim.getTime()));

			if (cs.execute()) {
				rs = cs.getResultSet();
				while (rs.next()) {
					lRet.add(new JanelaProcessamento(
							rs.getInt("id_usuario"), 
							rs.getString("usuario"), 
							rs.getInt("id_processo"), 
							rs.getString("nome_processo"), 
							rs.getInt("conta")
						)
					);
				}
			}

		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}		
		finally {
			try {
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}

		return lRet;
	}
	
	/**
	 * Retorna o valor do campo 'usuario' atual.
	 * @return the usuario
	 */
	public String getUsuario() {
		return this.usuario;
	}
	/**
	 * Retorna o valor do campo 'processo' atual.
	 * @return the processo
	 */
	public String getNomeProcesso() {
		return this.nomeProcesso;
	}
	/**
	 * Retorna o valor do campo 'conta' atual.
	 * @return the conta
	 */
	public Integer getConta() {
		return this.conta;
	}

	/**
	 * Retorna o valor do campo 'idUsuario' atual.
	 * @return the idUsuario
	 */
	public Integer getIdUsuario() {
		return this.idUsuario;
	}

	/**
	 * Retorna o valor do campo 'idProcesso' atual.
	 * @return the idProcesso
	 */
	public Integer getIdProcesso() {
		return this.idProcesso;
	}
	
}
