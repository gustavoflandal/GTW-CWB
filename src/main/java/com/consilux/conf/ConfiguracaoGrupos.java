package com.consilux.conf;

import java.io.Serializable;

/**
 * Classe que representa a configuração de grupos 'especiais' do sistema.
 * Utilizado para definir lógica específica para cada grupo em questão.
 * Ex: Apenas usuários do grupo 'Técnicos' poderão executar manutenções. 
 * @author raoni
 */
public class ConfiguracaoGrupos implements Serializable {

	private static final long serialVersionUID = 7848653237348559786L;
	
	private int idGrupoDigitadores;
	private int idGrupoAuditores;
	private int idGrupoTecnicos;
	private int idGrupoEditarConfigEquip;
	private int idGrupoNovoEquipamento;
	private int idGrupoDesenvolvedores;
	private int idGrupoGerenteAuditores;
	
	public ConfiguracaoGrupos() {
		// Construtor padrão sem parâmetros, padrão JavaBeans
	}

	/**
	* Constrói o objeto ConfiguracaoGrupos a partir dos parâmetros dados.
	* @param idGrupoDigitadores
	* @param idGrupoAuditores
	* @param idGrupoTecnicos
	* @param idGrupoEditarConfigEquip
	* @param idGrupoNovoEquipamento
	* @param idGrupoDesenvolvedores
	* @param idGrupoGerenteAuditores
	*/
	public ConfiguracaoGrupos(int idGrupoDigitadores, int idGrupoAuditores,
			int idGrupoTecnicos, int idGrupoEditarConfigEquip,
			int idGrupoNovoEquipamento, int idGrupoDesenvolvedores,
			int idGrupoGerenteAuditores) {
		super();
		this.idGrupoDigitadores = idGrupoDigitadores;
		this.idGrupoAuditores = idGrupoAuditores;
		this.idGrupoTecnicos = idGrupoTecnicos;
		this.idGrupoEditarConfigEquip = idGrupoEditarConfigEquip;
		this.idGrupoNovoEquipamento = idGrupoNovoEquipamento;
		this.idGrupoDesenvolvedores = idGrupoDesenvolvedores;
		this.idGrupoGerenteAuditores = idGrupoGerenteAuditores;
	}

	/**
	 * Retorna o valor do campo 'idGrupoDigitadores' atual.
	 * @return the idGrupoDigitadores
	 */
	public int getIdGrupoDigitadores() {
		return this.idGrupoDigitadores;
	}

	/**
	 * Ajusta o valor do campo 'idGrupoDigitadores' no objeto.
	 * @param idGrupoDigitadores the idGrupoDigitadores to set
	 */
	public void setIdGrupoDigitadores(int idGrupoDigitadores) {
		this.idGrupoDigitadores = idGrupoDigitadores;
	}

	/**
	 * Retorna o valor do campo 'idGrupoAuditores' atual.
	 * @return the idGrupoAuditores
	 */
	public int getIdGrupoAuditores() {
		return this.idGrupoAuditores;
	}

	/**
	 * Ajusta o valor do campo 'idGrupoAuditores' no objeto.
	 * @param idGrupoAuditores the idGrupoAuditores to set
	 */
	public void setIdGrupoAuditores(int idGrupoAuditores) {
		this.idGrupoAuditores = idGrupoAuditores;
	}

	/**
	 * Retorna o valor do campo 'idGrupoTecnicos' atual.
	 * @return the idGrupoTecnicos
	 */
	public int getIdGrupoTecnicos() {
		return this.idGrupoTecnicos;
	}

	/**
	 * Ajusta o valor do campo 'idGrupoTecnicos' no objeto.
	 * @param idGrupoTecnicos the idGrupoTecnicos to set
	 */
	public void setIdGrupoTecnicos(int idGrupoTecnicos) {
		this.idGrupoTecnicos = idGrupoTecnicos;
	}

	/**
	 * Retorna o valor do campo 'idGrupoEditarConfigEquip' atual.
	 * @return the idGrupoEditarConfigEquip
	 */
	public int getIdGrupoEditarConfigEquip() {
		return this.idGrupoEditarConfigEquip;
	}

	/**
	 * Ajusta o valor do campo 'idGrupoEditarConfigEquip' no objeto.
	 * @param idGrupoEditarConfigEquip the idGrupoEditarConfigEquip to set
	 */
	public void setIdGrupoEditarConfigEquip(int idGrupoEditarConfigEquip) {
		this.idGrupoEditarConfigEquip = idGrupoEditarConfigEquip;
	}

	/**
	 * Retorna o valor do campo 'idGrupoNovoEquipamento' atual.
	 * @return the idGrupoNovoEquipamento
	 */
	public int getIdGrupoNovoEquipamento() {
		return this.idGrupoNovoEquipamento;
	}

	/**
	 * Ajusta o valor do campo 'idGrupoNovoEquipamento' no objeto.
	 * @param idGrupoNovoEquipamento the idGrupoNovoEquipamento to set
	 */
	public void setIdGrupoNovoEquipamento(int idGrupoNovoEquipamento) {
		this.idGrupoNovoEquipamento = idGrupoNovoEquipamento;
	}

	/**
	 * Retorna o valor do campo 'idGrupoDesenvolvedores' atual.
	 * @return the idGrupoDesenvolvedores
	 */
	public int getIdGrupoDesenvolvedores() {
		return this.idGrupoDesenvolvedores;
	}

	/**
	 * Ajusta o valor do campo 'idGrupoDesenvolvedores' no objeto.
	 * @param idGrupoDesenvolvedores the idGrupoDesenvolvedores to set
	 */
	public void setIdGrupoDesenvolvedores(int idGrupoDesenvolvedores) {
		this.idGrupoDesenvolvedores = idGrupoDesenvolvedores;
	}

	/**
	 * Retorna o valor do campo 'idGrupoGerenteAuditores' atual.
	 * @return the idGrupoGerenteAuditores
	 */
	public int getIdGrupoGerenteAuditores() {
		return this.idGrupoGerenteAuditores;
	}

	/**
	 * Ajusta o valor do campo 'idGrupoGerenteAuditores' no objeto.
	 * @param idGrupoGerenteAuditores the idGrupoDesenvolvedores to set
	 */
	public void setIdGrupoGerenteAuditores(int idGrupoGerenteAuditores) {
		this.idGrupoGerenteAuditores = idGrupoGerenteAuditores;
	}

}
