package com.consilux.model;

/**
 * Enumerador que define alguns grupos especiais do sistema.
 * @author raoni
 *
 */
public enum TipoGrupo {

	DIGITADORES("DIG", "Grupo Digitadores"),
	AUDITORES("AUD", "Grupo Auditores"),
	TECNICOS("TEC","Grupo Técnicos"),
	EDITAR_EQUIPAMENTO("EEQ","Grupo Editar Config Equip"),
	NOVO_EQUIPAMENTO("NEQ","Grupo Novo Equipamento"),
	DESENVOLVEDORES("DEV", "Grupo Desenvolvedores"),
	GERENTE_AUDITORES("GAU", "Gerente Auditores");
	
	private String codigo;
	private String descricao;
	
	private TipoGrupo(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

}
