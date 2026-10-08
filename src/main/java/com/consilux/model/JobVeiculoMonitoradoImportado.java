package com.consilux.model;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

/**
 * Job responsável por pegar os veículos monitorados que entraram via importador
 * e enviar emails (caso necessário). 
 * @author raoni
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobVeiculoMonitoradoImportado implements Job {

  
	private static Semaphore semaforoExecucao = new Semaphore(1);
	private static Logger logger = Logger.getLogger(JobVeiculoMonitoradoImportado.class);
	
	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		
		boolean lockExclusivo = false;
		
		try {
			
			// Loga o início da operação.
			logger.info("JobVeiculoMonitoradoImportado iniciando...");
			
			// Tenta obter o lock.
			lockExclusivo = semaforoExecucao.tryAcquire();
			
			if (!lockExclusivo) {
				logger.info("Já existe um job de JobVeiculoMonitoradoImportado em execução.");
				return;
			}
			
			// Dispara o envio de algum VeiculoMonitorado que exista. 
			VeiculoMonitorado.enviaEmailVeiculosMonitorados();
			
		} catch (Exception ex) {
			logger.error("Erro ao executar JobVeiculoMonitoradoImportado.", ex);
		}
		
		finally {
			if (lockExclusivo)
			{
				semaforoExecucao.release();
			}
			logger.info("JobVeiculoMonitoradoImportado terminado.");
		}
		
	}
	
}
