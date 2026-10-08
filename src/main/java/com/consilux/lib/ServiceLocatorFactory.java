/**********************************************************************************


  Projeto: GTW
  Nome do Modulo: com.consilux.lib

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 03/03/2010

  Descricao: Classe pai para criar Factorys de WS.

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.lib;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.model.exception.ModelException;

/**
 * Classe pai para criar Factorys de WS.
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public abstract class ServiceLocatorFactory {
	public String endereco = null;
	protected ServiceLocatorFactory(String nomeInterface) throws ModelException {
		Configuracao conf = ConfiguracaoProvider.getInstance();
		if (!conf.getMapWS().containsKey(nomeInterface)) {
			throw new ModelException("Não especificado o endereço para o WS: "+nomeInterface);
		}
		endereco = conf.getMapWS().get(nomeInterface).getEndereco();
	}

	/**
	 * Retorna o valor do campo 'endereco' atual.
	 * @return the endereco
	 */
	protected String trocaEndereco(String endAtual) {
		Integer iIni, iFim;
		if (endAtual == null) {
			return null;
		}
		
		iIni = endAtual.indexOf("://")+3;
		iFim = endAtual.indexOf("/", iIni);
		
		return endAtual.substring(0, iIni)+endereco+endAtual.substring(iFim);
	}
	
}
