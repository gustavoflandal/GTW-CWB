/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 16/01/2007

  Descricao: Classe para exceção gerada quando á uma exceção de modelo.

  Historico:

    $Log: ModelException.java,v $
    Revision 1.4  2009/05/08 18:25:17  raoni
    Adicionado ctor com mensagem e a exceção interna.

    Revision 1.3  2009/01/12 12:49:49  fos
    Recuperação de repositório.

    Revision 1.1  2008/10/16 21:15:44  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model.exception;

/**
 * Classe para exceção gerada quando á uma exceção de modelo.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2009/05/08 18:25:17 $ $Author: raoni $
 */
public class ModelException extends Exception {
	private static final long serialVersionUID = 1L;
	/**
	 * Constrói a exceção.
	 */
	public ModelException(String mensagem) {
		super(mensagem);
	}
	
	
	/**
	 * Constrói a exceção.
	 * @param message A mensagem de erro.
	 * @param cause A causa da exceção.
	 */
	public ModelException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
