package com.consilux.model.beans;

import java.io.Serializable;

/**
 * Bean do GTW que representa um relatório dinâmico
 * @author raoni
 */
public class RelatorioDinamicoBean  implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String nome;
	private String functionSQL;
	
	public RelatorioDinamicoBean() {
	}
	
	public RelatorioDinamicoBean(int id, String nome, String functionSQL) {
		this.id = id;
		this.nome = nome;
		this.functionSQL = functionSQL;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getFunctionSQL() {
		return functionSQL;
	}
	
	public void setFunctionSQL(String functionSQL) {
		this.functionSQL = functionSQL;
	}
}
