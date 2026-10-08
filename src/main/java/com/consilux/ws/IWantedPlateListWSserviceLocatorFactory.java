/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.ws

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 03/03/2010

  Descricao: Factory para criar um IWantedPlateListWSserviceLocator

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.ws;

import javax.xml.rpc.ServiceException;

import com.consilux.lib.ServiceLocatorFactory;
import com.consilux.model.exception.ModelException;
import com.consilux.ws.client.IWantedPlateListWS;
import com.consilux.ws.client.IWantedPlateListWSserviceLocator;

/**
 * Factory para criar um IWantedPlateListWSserviceLocator
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public class IWantedPlateListWSserviceLocatorFactory extends
		ServiceLocatorFactory {
	
	public static IWantedPlateListWSserviceLocatorFactory instance = null;
	public IWantedPlateListWSserviceLocator serviceLocator = null;
	
	private IWantedPlateListWSserviceLocatorFactory() throws ModelException {
		super("IWantedPlateListWS");
		this.serviceLocator = new IWantedPlateListWSserviceLocator();
		this.serviceLocator.setIWantedPlateListWSPortEndpointAddress(super.trocaEndereco(serviceLocator.getIWantedPlateListWSPortAddress()));
	}
	
	/**
	 * Retorna o valor do campo 'serviceLocator' atual.
	 * @return the serviceLocator
	 */
	public IWantedPlateListWSserviceLocator getServiceLocator() {
		return this.serviceLocator;
	}

	public static IWantedPlateListWS getIWantedPlateListWSPort() throws ModelException, ServiceException {
		return getInstance().getServiceLocator().getIWantedPlateListWSPort();
	}
	
	private static IWantedPlateListWSserviceLocatorFactory getInstance() throws ModelException {
		if (instance == null) {
			instance = new IWantedPlateListWSserviceLocatorFactory();
		}
		return instance;
	}
	
}
