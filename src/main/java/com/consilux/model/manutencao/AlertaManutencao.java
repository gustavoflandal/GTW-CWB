package com.consilux.model.manutencao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class AlertaManutencao {
	
	public enum Acao {
		DESATIVAR,
		DESATIVAR_INEXISTENTES
	}

	public static Boolean alterarAlerta(Integer serieEquipamento, Integer pista,
			String sInformacaoAdicional, Boolean causaExternaEnergia,
			Boolean causaExternaPavimento, Boolean causaExternaVandalismo, Boolean causaFalsoPositivo) throws ConexaoException, SQLException {
		
		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("UPDATE");
		sbSQL.append("	painel_contrato_alerta");
		sbSQL.append(" SET");
		sbSQL.append("	informacao_adicional = ?,");
		sbSQL.append("	motivo_ext_energia = ?,");
		sbSQL.append("	motivo_ext_pavimento = ?,");
		sbSQL.append("	motivo_ext_vandalismo = ?,");
		sbSQL.append("	motivo_falso_positivo = ?");
		sbSQL.append(" WHERE");
		sbSQL.append("	serie_equipamento = ? AND");
		sbSQL.append("	(id_pista = ? OR ? = 0) AND");
		sbSQL.append("	ativo = 1");
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, sInformacaoAdicional);
			ps.setBoolean(2, causaExternaEnergia);
			ps.setBoolean(3, causaExternaPavimento);
			ps.setBoolean(4, causaExternaVandalismo);
			ps.setBoolean(5, causaFalsoPositivo);
			ps.setInt(6, serieEquipamento);
			ps.setInt(7, pista);
			ps.setInt(8, pista);
			
			bRet = ps.executeUpdate() > 0;
		}
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return bRet;
	}
	
	public static Boolean desativarAlerta(Integer serieEquipamento, Integer pista) throws ConexaoException, SQLException {
		
		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("UPDATE");
		sbSQL.append("	painel_contrato_alerta");
		sbSQL.append(" SET");
		sbSQL.append("	ativo = ?");
		sbSQL.append(" WHERE");
		sbSQL.append("	serie_equipamento = ? AND");
		sbSQL.append("	(id_pista = ? OR ? = 0) AND");
		sbSQL.append("	ativo = 1");
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setBoolean(1, false);
			ps.setInt(2, serieEquipamento);
			ps.setInt(3, pista);
			ps.setInt(4, pista);
			
			bRet = ps.executeUpdate() > 0;
		}
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return bRet;
	}
	
	public static Boolean desativarAlertasInexistentes(String sInformacaoAdicional) throws ConexaoException, SQLException {
		Boolean bRet = false;
		
		Connection conn = null;
		CallableStatement cs = null;

		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
					"{? = call spu_desativa_alerta_inexistente(?)}"
			);
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setString(2, sInformacaoAdicional);

			cs.execute();
			bRet = cs.getInt(1) > 0;

		}		
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return bRet;
	}
}
