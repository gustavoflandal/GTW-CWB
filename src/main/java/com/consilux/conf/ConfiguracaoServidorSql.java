package com.consilux.conf;

import java.io.Serializable;

/**
 * Classe que representa a configuração de um servidor Microsoft SQL Server.
 * Utilizado para abrir conexões para consulta e persistência de dados.
 * @author raoni
 */
public class ConfiguracaoServidorSql implements Serializable {

	private static final long serialVersionUID = 5683256263874935241L;
	
	private String host;
	private String database;
	private String user;
	private String password;
	
	public ConfiguracaoServidorSql() {
		// Construtor padrão sem parâmetros, padrão JavaBeans
	}
	
	public ConfiguracaoServidorSql(String host, String database, String user,
			String password) {
		this.host = host;
		this.database = database;
		this.user = user;
		this.password = password;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getDatabase() {
		return database;
	}
	
	public void setDatabase(String database) {
		this.database = database;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}
