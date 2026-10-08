package com.consilux.conf;

import java.io.Serializable;

/**
 * Classe que representa as configurações de armazenamento do sistema.
 * @author raoni
 */
public class ConfiguracaoArmazenamento implements Serializable {

	private static final long serialVersionUID = -8770109869079272217L;

	private String diretorioImportadorTxt;
	private ConfiguracaoServidorSql configuracaoServidorSql;

	public ConfiguracaoArmazenamento() {
		// Construtor padrão sem parâmetros, padrão JavaBeans		
	}
	
	public ConfiguracaoArmazenamento(String diretorioImportadorTxt,
			ConfiguracaoServidorSql configuracaoServidorSql) {
		this.diretorioImportadorTxt = diretorioImportadorTxt;
		this.configuracaoServidorSql = configuracaoServidorSql;
	}

	public String getDiretorioImportadorTxt() {
		return diretorioImportadorTxt;
	}

	public void setDiretorioImportadorTxt(String diretorioImportadorTxt) {
		this.diretorioImportadorTxt = diretorioImportadorTxt;
	}

	public ConfiguracaoServidorSql getConfiguracaoServidorSql() {
		return configuracaoServidorSql;
	}

	public void setConfiguracaoServidorSql(
			ConfiguracaoServidorSql configuracaoServidorSql) {
		this.configuracaoServidorSql = configuracaoServidorSql;
	}
}
