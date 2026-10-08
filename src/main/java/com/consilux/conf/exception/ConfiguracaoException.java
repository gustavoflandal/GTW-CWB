/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 26/12/2006

  Descricao: Classe para exceção gerada na carga da configuração.

  Historico:

    $Log: ConfiguracaoException.java,v $
    Revision 1.3  2009/01/12 12:49:49  fos
    Recuperação de repositório.

    Revision 1.1  2007/04/17 18:00:38  fos
    Ajustado o pacote da classe ConfiguracaoException.

    Revision 1.2  2007/03/16 12:56:16  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.conf.exception;

/**
 * Classe para exceção gerada na carga da configuração.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.3 $ $Date: 2009/01/12 12:49:49 $ $Author: fos $
 */
public class ConfiguracaoException extends Exception {
	private static final long serialVersionUID = 1L;
	/**
	 * Constrói uma exceção com mensagem de erro.
	 * @param sErr Mensagem de erro.
	 */
	public ConfiguracaoException(String sErr) {
		super(sErr);
	}
	
	public ConfiguracaoException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
