package com.consilux.infra;

public interface IExpValida {
	
	public static final String PLACA = "[A-Z]{3}[0-9]{4}";
	public static final String PLACA_MERCOSUL = "[A-Z]{3}[0-9][A-Z][0-9]{2}";
	public static final String PLACA_PARCIAL = "^(([A-Z]{0,3}))([0-9]{1}[A-Z0-9]{1})?([0-9]{0,2})$";
	public static final String PLACA_PARCIAL_ESPECIAL = "^(([A-Z*]{0,3}))([0-9*]{1}[A-Z0-9*]{1})?([0-9*]{0,2})$";
	public static final String PLACA_PARCIAL_LETRAS = "[A-Z]{3}";
	public static final String PLACA_PARCIAL_ALFANUMERICO = "[0-9][A-Z0-9][0-9]{2}";
	
	public Boolean validar(String entrada);
	public Boolean validar(String entrada, Integer tamMaximo);
}
