package com.consilux.lib;

/**
 * Classe que representa um inteiro de 16 bits, não assinado
 * (unsigned).
 * @author raoni
 *
 */
public class UShort
{
	int value;

	public static int MAX_VALUE = 65536;
	public static int MIN_VALUE = 0;
	
	public UShort(byte b) {
		if (b < MIN_VALUE)
			throw new ArithmeticException();
		this.value = b;
	}

	public UShort(short s) {
		if (s < MIN_VALUE)
			throw new ArithmeticException();
		this.value = s;
	}		

	public UShort(int i) {
		if (i < MIN_VALUE)
			throw new ArithmeticException();
		this.value = i % MAX_VALUE;
	}			

	public UShort Add(UShort outro) {
		this.value = (this.value + outro.value) % MAX_VALUE;
		return this;
	}

	public UShort Sub(UShort outro) {
		int i = (this.value - outro.value) % MAX_VALUE;
		if (i < 0)
			throw new ArithmeticException();
		this.value = i;
		return this;
	}

	public UShort Mul(UShort outro) {
		this.value = (this.value * outro.value) % MAX_VALUE;
		return this;
	}

	public UShort Div(UShort outro) {
		this.value = (this.value / outro.value) % MAX_VALUE;
		return this;
	}		

	public UShort Mod(UShort outro) {
		this.value = (this.value % outro.value);
		return this;
	}	

	public int getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
		return Integer.toString(this.value);
	}
}