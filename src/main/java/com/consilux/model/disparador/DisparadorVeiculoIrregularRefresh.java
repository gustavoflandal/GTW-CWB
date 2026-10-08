/**
 * 
 */
package com.consilux.model.disparador;

import org.apache.log4j.Logger;

import com.consilux.infra.DisparadorEventos;
import com.consilux.ws.IWantedPlateListWSserviceLocatorFactory;

/**
 * @author fernando
 *
 */
public class DisparadorVeiculoIrregularRefresh extends DisparadorEventos {

	private static Logger logger = Logger.getLogger(DisparadorVeiculoIrregularRefresh.class); 

	public DisparadorVeiculoIrregularRefresh() {

		super( false ); // fazer o trabalho, e mais nada

	}

	/* (non-Javadoc)
	 * @see com.consilux.infra.DisparadorEventos#executar()
	 */
	@Override
	protected void executar() {

		try {
			
			IWantedPlateListWSserviceLocatorFactory.getIWantedPlateListWSPort().refresh();
			
		}
		catch (Exception e) {
			logger.error("Exceção externa WS.", e);
		}
		
	}

}
