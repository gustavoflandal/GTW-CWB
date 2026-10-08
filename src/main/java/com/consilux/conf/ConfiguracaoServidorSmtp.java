package com.consilux.conf;

import java.io.Serializable;

/**
 * Classe que representa a configuração de um servidor SMTP.
 * Utilizado para abrir conexões para envio de email.
 * @author raoni
 *
 */
public class ConfiguracaoServidorSmtp implements Serializable {

	private static final long serialVersionUID = 1L;
	private String host;
	private String user;
	private String password;
	private String mail;
	private Integer port;
	
	public ConfiguracaoServidorSmtp() {
		// Construtor padrão sem parâmetros, padrão JavaBeans
	}
	
	public ConfiguracaoServidorSmtp(String host, String user, String password, String mail, Integer port) {
		this.host = host;
		this.user = user;
		this.password = password;
		this.mail = mail;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
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

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
}
