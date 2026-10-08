/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.ws

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 03/03/2010

  Descricao: Factory para criar um IConfigEquipChangeserviceLocator

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.ws;

import javax.xml.rpc.ServiceException;

import com.consilux.lib.ServiceLocatorFactory;
import com.consilux.model.exception.ModelException;
import com.consilux.ws.client.IConfigEquipChange;
import com.consilux.ws.client.IConfigEquipChangeserviceLocator;

/**
 * Factory para criar um IConfigEquipChangeserviceLocator
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public class IConfigEquipChangeserviceLocatorFactory extends
		ServiceLocatorFactory {
	
	public static IConfigEquipChangeserviceLocatorFactory instance = null;
	public IConfigEquipChangeserviceLocator serviceLocator = null;
	
	private IConfigEquipChangeserviceLocatorFactory() throws ModelException {
		super("IConfigEquipChange");
		this.serviceLocator = new IConfigEquipChangeserviceLocator();
		this.serviceLocator.setIConfigEquipChangePortEndpointAddress(super.trocaEndereco(serviceLocator.getIConfigEquipChangePortAddress()));
	}
	
	/**
	 * Retorna o valor do campo 'serviceLocator' atual.
	 * @return the serviceLocator
	 */
	public IConfigEquipChangeserviceLocator getServiceLocator() {
		return this.serviceLocator;
	}

	public static IConfigEquipChange getIConfigEquipChangePort() throws ModelException, ServiceException {
		return getInstance().getServiceLocator().getIConfigEquipChangePort();
	}
	
	private static IConfigEquipChangeserviceLocatorFactory getInstance() throws ModelException {
		if (instance == null) {
			instance = new IConfigEquipChangeserviceLocatorFactory();
		}
		return instance;
	}
	
}
