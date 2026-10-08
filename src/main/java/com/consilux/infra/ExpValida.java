/**********************************************************************************

  Projeto: GTW_Taboao
  Nome do Modulo: com.consilux.infra

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 12/08/2009

  Descricao: Enumerador com expressões regulares prontas.

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.infra;

import java.util.regex.Pattern;


/**
 * Enumerador com expressões regulares prontas.
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public enum ExpValida implements IExpValida {
	
	NATURAL("1?[1-9][0-9]{0,8}"),

	NATURAL_COM_ZERO("0|"+NATURAL.pattern),

	INTEIRO("0|" +
			"[+-]?1?[1-9][0-9]{0,8}|" +
			"20([0-9]{8})|" +
			"21[0-3]([0-9]{7})|" +
			"214[0-6]([0-9]{6})|" +
			"2147[0-3]([0-9]{5})|" +
			"21474[0-7]([0-9]{4})|" +
			"214748[0-2]([0-9]{3})|" +
			"2147483[0-5]([0-9]{2})|" +
			"21474836[0-3]([0-9]{1})|" +
			"214748364[0-7]"),
	
	LONGO("[+-]?1?[0-9]{1,18}"),

	UF("A[CLMP]|BA|CE|DF|ES|GO|M[AGST]|P[ABEIR]|R[JNORS]|S[CEP]|TO"),
	
	DATA("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})"),

	DATA_HORA("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3}) ([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])"),
	
	HORA_MINUTO("([01][0-9]|2[0-3]):([0-5][0-9])"),
	
	PLACA(IExpValida.PLACA),
	
	PLACA_MERCOSUL(IExpValida.PLACA_MERCOSUL),
	
	PLACA_PARCIAL(IExpValida.PLACA_PARCIAL),
	
	PLACA_PARCIAL_ESPECIAL(IExpValida.PLACA_PARCIAL_ESPECIAL),
	
	PLACA_PARCIAL_LETRAS(IExpValida.PLACA_PARCIAL_LETRAS),
	
	PLACA_PARCIAL_ALFANUMERICO(IExpValida.PLACA_PARCIAL_ALFANUMERICO),
	
	PLACA_LIKE("([_A-Z]|\\[[A-Z]+\\]){3}([_0-9]|\\[[0-9]+\\]){4}"),
	
	PLACA_LIKE_MERCOSUL("([_A-Z]|\\[[A-Z]+\\]){3}([_0-9]|\\[[0-9]+\\])([_A-Z]|\\[[A-Z]+\\])([_0-9]|\\[[0-9]+\\]){2}"),

	MOEDA_CENTAVO("(([1-9][0-9]{0,8}|[0-9]),([0-9][0-9]))"),

	MOEDA_MILHAR_CENTAVO("(([1-9][0-9]{0,2}\\.(([0-9]{3})\\.)*)*|([1-9][0-9]{1,2}|[0-9])),([0-9][0-9])"),

	CPF_SOMENTE_NUMEROS("[0-9]{11}"),
	
	CNPJ_SOMENTE_NUMEROS("[0-9]{14}"),
	
	CPF_CNPJ_SOMENTE_NUMEROS(CPF_SOMENTE_NUMEROS.pattern+"|"+CNPJ_SOMENTE_NUMEROS.pattern),
	
	EMAIL(".+@.+\\.[a-z]+"),
	
	TEXTO(".*");

	String pattern;
	ExpValida(String pattern) {
		this.pattern = pattern;
	}
	public String getPattern() {
		return "^"+pattern+"$";
	}
	public Boolean validar(String entrada) {
		return validar(entrada, null);
	}
	public Boolean validar(String entrada, Integer tamMaximo) {
		Boolean bRet = true;
		
		if (tamMaximo != null)
			bRet = entrada.length() <= tamMaximo;
			
		bRet = bRet && Pattern.matches(pattern, entrada);
		
		return bRet;
	}
}
