package com.consilux.conf;

/**
 * Configuração que representa um grupo equipamento.
 * @author raoni
 */
public class ConfiguracaoEquipamento {

	private int idGrupo;
	private String nomeContrato;
	private String identificacaoCliente;
	
	public ConfiguracaoEquipamento() {
	}	
	
	/**
	 * Representa o id do grupo-equipamento.
	 * @return
	 */
	public int getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}

	/**
	 * Representa o nome do contrato.
	 * @return
	 */
	public String getNomeContrato() {
		return nomeContrato;
	}

	public void setNomeContrato(String nomeContrato) {
		this.nomeContrato = nomeContrato;
	}

	/**
	 * Representa a identificação do cliente
	 * @return
	 */	
	public String getIdentificacaoCliente() {
		return this.identificacaoCliente;
	}
	
	public void setIdentificacaoCliente(String identificacaoCliente) {
		this.identificacaoCliente = identificacaoCliente;
	}
	
}
