/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 16/01/2007

  Descricao: Classe para exceção gerada quando o gráfico esta vazio.

  Historico:

    $Log: GraficoException.java,v $
    Revision 1.3  2009/01/12 12:49:47  fos
    Recuperação de repositório.

    Revision 1.1  2008/02/06 19:20:26  fos
    Carga da infração na nova tela funcional.

    Revision 1.2  2007/03/16 12:56:16  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.lib.exception;

/**
 * Classe para exceção gerada quando o gráfico esta vazio.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.3 $ $Date: 2009/01/12 12:49:47 $ $Author: fos $
 */
public class GraficoException extends Exception {
	private static final long serialVersionUID = 1L;
	/**
	 * Constrói a exceção.
	 */
	public GraficoException() { }
}
