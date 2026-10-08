package com.consilux.model.ferramenta;

/**********************************************************************************
Projeto: GTW
Nome do Modulo: GTW

Empresa: Consilux Tecnologia

Autor: Thiago Surgik
Data: 06/10/2015
*********************************************************************************/

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.EventoCSX;
import com.consilux.ui.client.beans.EventoGwtBean;

/**
* Classe de negócio para gravar Evento Manual
* @author Thiago Surgik - Consilux Tecnologia
* Data: 06/10/2015
*/
public  class EventoManual implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	
	public static Boolean gravarEvento(EventoCSX eventoCSX) throws ConexaoException, SQLException {
		
		Boolean bRet = false;
		Connection conn = null;
		
		try {
			
			conn = Conexao.getConexao();
			bRet = incluirEventoCSXManual(conn, eventoCSX);
		
		} catch (ConexaoException e) {
			throw new ConexaoException("ERRO de conexão", e);
			
		} finally {
			if (conn != null)
				conn.close();
		}

		return bRet;
	}
	
	
	public static boolean incluirEventoCSXManual(Connection conn, EventoCSX eventoCSX) throws ConexaoException, SQLException {

		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("{? = call spu_insere_evento_csx_manual(?, ?, ?, ?, ?)}");
				
		CallableStatement cs = conn.prepareCall(sbSQL.toString());

		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setString(2, eventoCSX.getProprietario() );
		cs.setTimestamp(3, new Timestamp( eventoCSX.getData_hora().getTime() ) );
		cs.setString(4, eventoCSX.getUsuario());
		cs.setInt(5, eventoCSX.getTipoEventoManual());
		cs.setInt(6, eventoCSX.getIdCategoriaEventoManual());
		
		try {
			cs.execute();
			bRet = cs.getInt(1) > 0;
			
		} catch (SQLException e) {
			throw new SQLException("ERRO de SQL", e);
		
		} finally {
			try {
				if (cs != null)
					cs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return bRet;
	}

	
	public static List<EventoGwtBean> listarEventoPorId(Integer idEvento) throws ConexaoException {
	
		List<EventoGwtBean> lRet = new ArrayList<EventoGwtBean>();
		StringBuilder sbSqlQuery = new StringBuilder();
		
		sbSqlQuery.append(" SELECT id_evento, ");
		sbSqlQuery.append(" 	   evento ");
		sbSqlQuery.append(" FROM   eventos_csx_desc_evento ");
		sbSqlQuery.append(" WHERE  id_evento = ? ");
		sbSqlQuery.append(" ORDER BY ");
		sbSqlQuery.append(" 	   evento ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			
			ps = conn.prepareStatement(sbSqlQuery.toString());
			ps.setInt(1, idEvento);
			
			rs = ps.executeQuery();
			
			while (rs.next()){
				lRet.add(new EventoGwtBean(rs.getInt(1),rs.getString(2)));
			}
			
			rs.close();
			
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL",e);
			
		} finally {
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
}