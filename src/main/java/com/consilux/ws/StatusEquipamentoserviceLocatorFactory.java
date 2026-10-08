/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.ws

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 03/03/2010

  Descricao: Factory para criar um StatusEquipamentoserviceLocator

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.ws;

import javax.xml.rpc.ServiceException;

import com.consilux.lib.ServiceLocatorFactory;
import com.consilux.model.exception.ModelException;
import com.consilux.ws.client.StatusEquipamento;
import com.consilux.ws.client.StatusEquipamentoserviceLocator;

/**
 * Factory para criar um StatusEquipamentoserviceLocator
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public class StatusEquipamentoserviceLocatorFactory extends
		ServiceLocatorFactory {
	
	public static StatusEquipamentoserviceLocatorFactory instance = null;
	public StatusEquipamentoserviceLocator serviceLocator = null;
	
	private StatusEquipamentoserviceLocatorFactory() throws ModelException {
		super("StatusEquipamento");
		this.serviceLocator = new StatusEquipamentoserviceLocator();
		this.serviceLocator.setStatusEquipamentoPortEndpointAddress(super.trocaEndereco(serviceLocator.getStatusEquipamentoPortAddress()));
	}
	
	/**
	 * Retorna o valor do campo 'serviceLocator' atual.
	 * @return the serviceLocator
	 */
	public StatusEquipamentoserviceLocator getServiceLocator() {
		return this.serviceLocator;
	}

	public static StatusEquipamento getStatusEquipamentoPort() throws ModelException, ServiceException {
		return getInstance().getServiceLocator().getStatusEquipamentoPort();
	}
	
	private static StatusEquipamentoserviceLocatorFactory getInstance() throws ModelException {
		if (instance == null) {
			instance = new StatusEquipamentoserviceLocatorFactory();
		}
		return instance;
	}
	
}
