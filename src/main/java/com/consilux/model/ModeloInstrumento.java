package com.consilux.model;

public enum ModeloInstrumento {

	FIXO('F'),
	PORTATIL('P'),
	MOVEL('M'),
	ESTATICO('E');

	private final char codigoModelo;
	
	ModeloInstrumento(char codigoModelo)
	{
		this.codigoModelo = codigoModelo;
	}	
	
	public char getCodigoModelo()
	{
		return codigoModelo;
	}
}
