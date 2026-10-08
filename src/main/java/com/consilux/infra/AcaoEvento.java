/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.infra

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 21/05/2010

  Descricao: XXX

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.infra;

/**
 * XXX
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public class AcaoEvento {
	private Object src;
	private Object tipo;
	/**
	* Constrói o objeto AcaoEvento a partir dos parâmetros dados.
	* @param src
	* @param tipo
	*/
	public AcaoEvento(Object src, Object tipo) {
		super();
		this.src = src;
		this.tipo = tipo;
	}
	/**
	 * Retorna o valor do campo 'src' atual.
	 * @return the src
	 */
	public Object getSrc() {
		return this.src;
	}
	/**
	 * Retorna o valor do campo 'tipo' atual.
	 * @return the tipo
	 */
	public Object getTipo() {
		return this.tipo;
	}
	
}
