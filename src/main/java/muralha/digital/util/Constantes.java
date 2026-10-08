package muralha.digital.util;

public class Constantes 
{

	
	//Clientes conectados ao sistema para envio de informações
	//via websocket
	public enum ClientesWS
	{
		
		CLIENTE_SOCK_ALERTA_NOTIFICACAO	("ALERTA-NOTIFICACAO"),
		CLIENTE_SOCK_VEICULO_TEMPO_REAL	("VEICULO-TEMPOREAL"),
		CLIENTE_SOCK_BLITZ_DIGITAL("BLITZ-DIGITAL"),
		CLIENTE_SOCK_BLITZ_ELETRONICA	("BLITZ-ELETRONICA");
		
		private final String valor;
		ClientesWS(String valorOpcao){valor = valorOpcao;}
	    public String getValor(){return valor;}
	}
	
}