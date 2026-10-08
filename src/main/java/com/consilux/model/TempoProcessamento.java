package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.TempoProcessamentoBean;
import com.consilux.model.exception.ModelException;

public class TempoProcessamento {

	public static void salvar(TempoProcessamentoBean beanSalvar) throws ModelException, ConexaoException, SQLException {
		
		if (beanSalvar == null)
			throw new ModelException("Argumento inválido: beanSalvar não pode ser nulo.");
		
		Connection conn = null;
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("INSERT INTO tempo_processamento (id_usuario, data_inicio_cliente, ");
		sbSQL.append(" tempo_gasto, classificacao, identificador, sub_identificador) ");
		sbSQL.append(" VALUES (?,?,?,?,?,?) ");
		
		try {
			conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, beanSalvar.getIdUsuario());
			ps.setTimestamp(2, new Timestamp(beanSalvar.getDataInicioCliente().getTime()));
			ps.setInt(3, beanSalvar.getTempoGasto());
			ps.setInt(4, beanSalvar.getClassificacao().getCodigo());
			
			if (beanSalvar.getIdentificador() != null) {
				ps.setInt(5, beanSalvar.getIdentificador());
			} else {
				ps.setNull(5, Types.INTEGER);
			}
			
			if (beanSalvar.getSubIdentificador() == null || beanSalvar.getSubIdentificador().length() > 0) {
				ps.setString(6, beanSalvar.getSubIdentificador());
			} else {
				ps.setNull(6, Types.CHAR);
			}			
			
			ps.executeUpdate();
		}
		finally {
			if (conn != null)
				conn.close();
		}
		
	}
	
}
