package Utils.Interface;

public interface IExpValida {
	
	public static final String PLACA = "[A-Z]{3}[0-9]{4}";
	public static final String PLACA_MERCOSUL = "[A-Z]{3}[0-9][A-Z][0-9]{2}";
	
	public Boolean validar(String entrada);
	public Boolean validar(String entrada, Integer tamMaximo);
}