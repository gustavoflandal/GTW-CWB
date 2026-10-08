package com.consilux.model;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

/**
 * Job responsável por exportar uma descarga.
 * @author raoni
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobExportaDescarga implements Job {

	private static Logger logger = Logger.getLogger(JobExportaDescarga.class);	  
	private static Semaphore semaforoExecucao = new Semaphore(1);

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		
		boolean lockExclusivo = false;
		
		try {
			
			// Loga o início da operação.
			logger.info("Job export descarga. Iniciando...");
			
			// Tenta obter o lock.
			lockExclusivo = semaforoExecucao.tryAcquire();
			
			if (!lockExclusivo) {
				logger.info("Já existe um job de exportação de descarga em execução.");
				return;
			}
			//new ExportaDescarga(idDescarga);
			logger.info("Job export descarga. Finalizado.");
			
		} catch (Exception ex) {
			logger.error("Erro inesperado ao exportar descarga.", ex);	
		}
		finally {
			if (lockExclusivo)
				semaforoExecucao.release();
		}		
		
	}
	
}
