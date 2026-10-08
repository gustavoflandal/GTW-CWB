package com.consilux.model;

public class ParametroTratado<T> {
	private T valor;
	private String sValor;
	/**
	 * @param valor
	 * @param sValor
	 */
	protected ParametroTratado(T valor, String sValor) {
		super();
		this.valor = valor;
		this.sValor = sValor;
	}
}