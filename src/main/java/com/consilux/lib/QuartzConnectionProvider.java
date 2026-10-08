package com.consilux.lib;

import java.sql.Connection;
import java.sql.SQLException;

import org.quartz.utils.ConnectionProvider;

import com.consilux.infra.exception.ConexaoException;

/**
 * Classe que implementa a interface ConnectionProvider, para
 * fornecer conexõeses ao quartz. É apenas um forwarder (proxy)
 * para a implementação que já existe no GTW. 
 * @author raoni
 *
 */
public class QuartzConnectionProvider implements ConnectionProvider {

	@Override
	public Connection getConnection() throws SQLException {
		try {
			return Conexao.getConexao();
		} catch (ConexaoException ex) {
			throw new SQLException(ex.getCause());
		}
	}

	@Override
	public void shutdown() throws SQLException {
	}

	@Override
	public void initialize() throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
