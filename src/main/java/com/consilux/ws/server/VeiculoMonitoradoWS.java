package com.consilux.ws.server;

import org.apache.log4j.Logger;

import com.consilux.infra.LocalWS;
import com.consilux.model.VeiculoMonitorado;
import com.consilux.ui.server.MonitoramentoMapServiceImpl;
import com.consilux.ui.server.VeiculosMonitoradosServiceImpl;

/**
 * WebService que é chamado pelo servidor captura para notificar a presença de novos
 * veículos monitorados no sistema.
 * @author raoni
 */
public class VeiculoMonitoradoWS extends LocalWS {

	private static Logger logger = Logger.getLogger(VeiculoMonitoradoWS.class);

	public synchronized void notificarVeiculoMonitorado() throws Exception {

		try {
			
			// Loga o início da operação.
			logger.debug("Iniciando notificarVeiculoMonitorado.");
			
			// Notifica o mapa
			MonitoramentoMapServiceImpl.notificar();
			
			// Notifica o gerenciamento (a interface com o GRID) 
			VeiculosMonitoradosServiceImpl.notificar();
			
			// Dispara o envio de emails.
			VeiculoMonitorado.enviaEmailVeiculosMonitorados();

		}
		catch (Exception ex)
		{
			logger.error("Erro ao notificarVeiculoMonitorado.", ex);			
		}
		finally {
			logger.debug("Finalizado notificarVeiculoMonitorado.");
		}
	}

}
