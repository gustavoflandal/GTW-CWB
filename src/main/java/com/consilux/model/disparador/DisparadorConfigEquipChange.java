/**
 * 
 */
package com.consilux.model.disparador;

import org.apache.log4j.Logger;

import com.consilux.infra.DisparadorEventos;
import com.consilux.ws.IConfigEquipChangeserviceLocatorFactory;

/**
 * @author fernando
 *
 */
public class DisparadorConfigEquipChange extends DisparadorEventos {

	private static Logger logger = Logger.getLogger(DisparadorConfigEquipChange.class); 
	private Integer idEquipamento = 0;
	
	public DisparadorConfigEquipChange(Integer idEquipamento) {

		super( false ); // fazer o trabalho, e mais nada

		this.idEquipamento = idEquipamento;

	}

	/* (non-Javadoc)
	 * @see com.consilux.infra.DisparadorEventos#executar()
	 */
	@Override
	protected void executar() {

		try {
			
			IConfigEquipChangeserviceLocatorFactory.getIConfigEquipChangePort().notifyChange( this.idEquipamento );
			
			logger.info("Notificado o Servidor Captura sobre novo ConfigEquip.");
			
		}
		catch (Exception e) {
			logger.error("Exceção externa WS.", e);
		}
		
	}

}
