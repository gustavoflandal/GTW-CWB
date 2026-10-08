/**
 * 
 */
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
 * @author fos
 *
 */
public class EstadoProcessamento {
	Integer idProcesso;
	String descricaoProcesso;
	Integer idInfracaoInicial;
	Date dataInicial;
	Integer idInfracaoFinal;
	Date dataFinal;
	Integer conta;

	public EstadoProcessamento(Integer idProcesso, String descricaoProcesso,
			Integer idInfracaoInicial, Date dataInicial,
			Integer idInfracaoFinal, Date dataFinal, Integer conta) {
		super();
		this.idProcesso = idProcesso;
		this.descricaoProcesso = descricaoProcesso;
		this.idInfracaoInicial = idInfracaoInicial;
		this.dataInicial = dataInicial;
		this.idInfracaoFinal = idInfracaoFinal;
		this.dataFinal = dataFinal;
		this.conta = conta;
	}


	/**
	 * Busca infrações no BD
	 * @param mFiltros Filtros para a busca.
	 * @return Lista de objetos Infracao
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<EstadoProcessamento> buscaEstadoProcessamento(Integer idLocal, Date dataIni, Date dataFim) throws ConexaoException {
		List<EstadoProcessamento> lRet = new ArrayList<EstadoProcessamento>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("{call spu_estado_processamento(?, ?, ?)}");

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
					lRet.add(new EstadoProcessamento(
							rs.getInt("id_processo"),
							rs.getString("nome"), 
							rs.getInt("id_infracao_inicial"),
							rs.getTimestamp("data_inicial"),
							rs.getInt("id_infracao_final"),
							rs.getTimestamp("data_final"),
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
				if (rs != null)
					rs.close();
				if (cs != null)
					cs.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}

		return lRet;
	}


	public Integer getIdProcesso() {
		return idProcesso;
	}


	public String getDescricaoProcesso() {
		return descricaoProcesso;
	}


	public Date getDataInicial() {
		return dataInicial;
	}


	public Integer getIdInfracaoInicial() {
		return idInfracaoInicial;
	}


	public Date getDataFinal() {
		return dataFinal;
	}


	public Integer getIdInfracaoFinal() {
		return idInfracaoFinal;
	}


	public Integer getConta() {
		return conta;
	}
	
	
}
