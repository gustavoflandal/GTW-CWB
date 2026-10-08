package com.consilux.conf;

import com.consilux.conf.exception.ConfiguracaoException;

/**
 * Classe responsável por prover uma instância de configuração.
 * É abstrata, para permitir os filhos customizarem a implementação
 * que desejarem.
 * @author raoni
 */
public abstract class ConfiguracaoProvider {

	protected static Configuracao instance = null;
	
	/**	
	* Contrutor protegido que chama o método de inicialiação.
	* As classes filhas devem implementar este método, devendo
	* fazer o necessitam no método de inicialização.
	 * @throws ConfiguracaoException 
	*/	
	protected ConfiguracaoProvider() throws ConfiguracaoException
	{
		initializar();
	}
	
	protected abstract void initializar() throws ConfiguracaoException;
	{
		// Método que permite aos filhos realizar operações
		// de inicialização e  ter acesso ao atributo estático.
	}
	
	/**
	 * Recupera a configuração do sistema (singleton).
	 * @throws ConfiguracaoException
	 */
	public static Configuracao getInstance()
	{
		return instance;
	}
	
}
