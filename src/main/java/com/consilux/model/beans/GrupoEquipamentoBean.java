package com.consilux.model.beans;

/**
 * Class que representa um bean de GrupoEquipamento. 
 * @author raoni
 */
public class GrupoEquipamentoBean {
	
	Integer id = 0;
	String nome = "";
	
	public GrupoEquipamentoBean(Integer id, String nome) {
		this.id = id;
		this.nome = nome;
	}
	
	/**
	 * @return Retorna o valor de id atual.
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id Novo valor para o atributo id.
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return Retorna o valor de nome atual.
	 */
	public String getNome() {
		return nome;
	}
	/**
	 * @param descricao Novo valor para o atributo nome.
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}
}
