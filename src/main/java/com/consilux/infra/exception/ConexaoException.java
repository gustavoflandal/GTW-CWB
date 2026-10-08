/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 21/12/2006

  Descricao: Classe para exceção gerada na cominicação com o BD.

  Historico:

    $Log: ConexaoException.java,v $
    Revision 1.5  2009/03/06 20:26:07  raoni
    Adicionado mais um construtor.

    Revision 1.4  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.infra.exception;

/**
 * Classe para exceção gerada na cominicação com o BD
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.5 $ $Date: 2009/03/06 20:26:07 $ $Author: raoni $
 */
public class ConexaoException extends Exception {
	private static final long serialVersionUID = 1L;
	/**
	 * Constrói uma exceção com mensagem de erro.
	 * @param sErr Mensagem de erro.
	 */
	public ConexaoException(String sErr) {
		super(sErr);
	}
	
	
	/**
	 * Constrói uma exceção com mensagem de erro e a exceção interna.
	 * @param sErr Mensagem de erro.
	 * @param causa Exceção que causou o erro.
	 */
	public ConexaoException(String sErr, Throwable causa) {
		super(sErr, causa);
	}
}
