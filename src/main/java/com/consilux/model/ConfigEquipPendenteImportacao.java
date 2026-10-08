/**
 * 
 */
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * @author Fernando de Souza
 *
 */
public class ConfigEquipPendenteImportacao {

	private Integer idLocal;
	private Integer sequenciaLocal;
	private String xmlConfiguracao;

	public ConfigEquipPendenteImportacao(Integer idLocal, Integer sequenciaLocal, String xmlConfiguracao) {

		this.idLocal = idLocal;
		this.sequenciaLocal = sequenciaLocal;
		this.xmlConfiguracao = xmlConfiguracao;
	}

	public static ConfigEquipPendenteImportacao getNextConfigEquipPendenteImportacao() throws ConexaoException {

		ConfigEquipPendenteImportacao lRet = null;

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" select TOP 1	id_local, sequencia_local, xml_configuracao ");
		sbSQL.append(" from configuracao_equipamento_pendente_importacao "); 
		sbSQL.append(" where sequencia_local is null ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			if (rs.next()) {
				lRet = new ConfigEquipPendenteImportacao(
						rs.getInt("id_local"),
						rs.getInt("sequencia_local"),
						rs.getString("xml_configuracao")
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

	public Integer getIdLocal() {
		return idLocal;
	}

	public Integer getSequenciaLocal() {
		return sequenciaLocal;
	}

	public String getXmlConfiguracao() {
		return xmlConfiguracao;
	}

}
