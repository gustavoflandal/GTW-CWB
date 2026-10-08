/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 21/12/2006

  Descricao: Classe para exceção gerada na montagem de relatórios.

  Historico:

    $Log: RelatorioException.java,v $
    Revision 1.4  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.infra.exception;

/**
 * Classe para exceção gerada na montagem de relatórios.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2009/01/12 12:49:43 $ $Author: fos $
 */
public class RelatorioException extends Exception {
	private static final long serialVersionUID = 1L;
	/**
	 * Constrói uma exceção com mensagem de erro.
	 * @param sErr Mensagem de erro.
	 */
	public RelatorioException(String sErr) {
		super(sErr);
	}
}
