/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.infra

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 09/06/2010

  Descricao: Interface que é armazenada na sessão e é chamada quando a sessão for finalizada.

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.infra;

import javax.servlet.http.HttpSession;

/**
 * Interface que é armazenada na sessão e é chamada quando a sessão for finalizada.
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public interface SessaoFinaliza {
	/**
	 * Função chamada quando a sessão é finalizada.
	 * @param sessao
	 */
	public void doFinaliza(HttpSession sessao) throws Exception;
}
