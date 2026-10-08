package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

public class ChaveValor {

	public static int removerTudo() throws ConexaoException, SQLException {

		int ret = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("DELETE FROM chave_valor");
			ret = ps.executeUpdate();
		}
		finally
		{
			if (conn != null)
				conn.close();							
		}
		return ret;
	}
	
	public static void definirValor(String chave, String valor) throws ModelException, ConexaoException, SQLException {
		
		if (chave == null || chave.length() == 0 || valor == null)
			throw new ModelException("Argumento inválido.");
		
		Connection conn = null;
		PreparedStatement ps = null;

		try {

			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			
			// Remove do banco, caso já exista
			ps = conn.prepareStatement("DELETE FROM chave_valor WHERE chave = ?");
			ps.setString(1, chave);
			ps.executeUpdate();
			
			// Insere no banco
			ps = conn.prepareStatement("INSERT INTO chave_valor (chave,valor) VALUES (?,?)");
			ps.setString(1, chave);
			ps.setString(2, valor);
			ps.executeUpdate();
			
			conn.commit();
		}
		catch (SQLException ex)
		{
			if (conn != null)
				conn.rollback();
			throw ex;
		}
		finally
		{
			if (conn != null)
			{
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}

	public static Map<String,String> obterMapa() throws ConexaoException, SQLException {
		
		Map<String,String> lRet = new HashMap<String,String>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT chave,valor FROM chave_valor");
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.put(rs.getString("chave"), rs.getString("valor"));
			}
		}
		finally
		{
			if (conn != null)
				conn.close();
		}
		return lRet;
	}
	
	public static void salvarMapa(Map<String,String> mapaChaveValor) throws ModelException, ConexaoException, SQLException {
	
		if (mapaChaveValor == null)
			throw new ModelException("Argumento nulo.");

		// Verifica se por acaso todas as chaves estão OK
		for (Entry<String,String> entry : mapaChaveValor.entrySet())
		{
			if (entry.getKey() == null || entry.getKey().length() == 0 || entry.getValue() == null)
				throw new ModelException("Mapa com conteúdo inválido.");
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			conn.setAutoCommit(false);
			
			// Remove do banco tudo o que existe .
			ps = conn.prepareStatement("DELETE FROM chave_valor");
			ps.executeUpdate();
			
			// Insere no banco o novo mapa.
			ps = conn.prepareStatement("INSERT INTO chave_valor (chave,valor) VALUES (?,?)");
			for (Entry<String,String> entry : mapaChaveValor.entrySet()) {
				ps.setString(1, entry.getKey());
				ps.setString(2, entry.getValue());
				ps.executeUpdate();
			}
			conn.commit();
		}
		catch (SQLException ex)
		{
			if (conn != null)
				conn.rollback();
			throw ex;
		}		
		finally
		{
			if (conn != null)
			{
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}
}
