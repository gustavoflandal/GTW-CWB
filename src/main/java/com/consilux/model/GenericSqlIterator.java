package com.consilux.model;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Classe que implementa as funcionalidades básicas de um iterator que
 * é baseado em um ResultSet de banco de dados.   
 * @author raoni
 * @param <E> O tipo do elemento que este iterator vai obter.
 */
public abstract class GenericSqlIterator<E> implements Iterator<E>, Closeable {

	protected ResultSet rs;
	protected Connection conn;
	private boolean temProximo = false;
	private boolean autoCloseConnection;
	
	public GenericSqlIterator(ResultSet rs, Connection conn, boolean autoCloseConnection) throws IllegalStateException {

		this.autoCloseConnection = autoCloseConnection;
		
		try {
			if (rs.isBeforeFirst())
				temProximo = rs.next();
			
			if (temProximo)
			{
				this.rs = rs;
				this.conn = conn;
			}
			else
			{
				if (autoCloseConnection)
				{
					rs.close();
					conn.close();
				}
			}

		} catch (SQLException e) {
			throw new IllegalStateException("Erro ao posicionar o ResultSet no primeiro elemento.", e);
		}
	}
	
	@Override
	public boolean hasNext() {
		return temProximo;
	}

	@Override
	public E next() {
		
		if (!temProximo)
			throw new NoSuchElementException("Iterator chegou ao fim.");
			
		try {
			
			E elementoAtual = montarElemento();
			temProximo = rs.next();
			
			if (!temProximo)
				close();
			
			return elementoAtual;
			
		} catch(SQLException e) {
			throw new IllegalStateException("Erro ao obter elemento do ResultSet.", e);
		} catch (IOException e) {
			throw new IllegalStateException("Erro ao fechar o Iterator.", e);
		} catch (InstantiationException e) {
			throw new IllegalStateException("Erro ao instanciar um objeto no Iterator.", e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Erro ao instanciar um objeto no Iterator.", e);
		}
	}

	/* (non-Javadoc)
	 * Sempre vai lançar uma UnsupportedOperationException. 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Não é possível remover elementos deste iterator.");
	}

	/**
	 * Método responsável por montar uma instância de um elemento.
	 * Vai ser chamado a cada vez que for necessário materializar
	 * um objeto a partir do ResultSet.
	 * Deve ser implementado pelas classes filhas, que podem acessar
	 * o ResultSet "protected".
	 * @return uma instância do elemento
	 * @throws SQLException
	 */
	protected abstract E montarElemento() throws SQLException, InstantiationException, IllegalAccessException;


	public void close() throws IOException {
		try {
			if (rs != null)
			{
				rs.close();
				rs = null;
			}
			if (autoCloseConnection && conn != null)
			{
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}	
	
}
