/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.conf

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 03/03/2010

  Descricao: XXX

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.conf;

import java.io.Serializable;

/**
 * XXX
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public class ConfiguracaoWS implements Serializable {
	
	private static final long serialVersionUID = -765675391379716911L;
	
	private String nomeInterface = null;
	private String endereco = null;
	/**
	 * Retorna o valor do campo 'nomeInterface' atual.
	 * @return the nomeInterface
	 */
	public String getNomeInterface() {
		return this.nomeInterface;
	}
	/**
	 * Ajusta o valor do campo 'nomeInterface' no objeto.
	 * @param nomeInterface the nomeInterface to set
	 */
	public void setNomeInterface(String nomeInterface) {
		this.nomeInterface = nomeInterface;
	}
	/**
	 * Retorna o valor do campo 'endereco' atual.
	 * @return the endereco
	 */
	public String getEndereco() {
		return this.endereco;
	}
	/**
	 * Ajusta o valor do campo 'endereco' no objeto.
	 * @param endereco the endereco to set
	 */
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

}
