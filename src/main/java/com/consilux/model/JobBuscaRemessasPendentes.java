package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.consilux.lib.Conexao;

@DisallowConcurrentExecution
public class JobBuscaRemessasPendentes implements Job {

	private static Logger logger = Logger
			.getLogger(JobBuscaRemessasPendentes.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		logger.debug("Executando JOB");
		long ini = System.currentTimeMillis();
		
		Connection conn = null;
		CallableStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareCall("{call spu_atualiza_movimentos_pendentes}");
			ps.execute();
		}
		catch(Exception e) {
			logger.error("ao execute : " + e.getMessage(), e);
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch(Exception e) {
				logger.error("ao execute : " + e.getMessage(), e);
			}
		}
		
		logger.debug("Executado em " + (System.currentTimeMillis() - ini) + " ms");
		
	}

	public static void AtualizarRemessa(Integer idRemessa) {
		logger.debug("Atualizando Remessa " + idRemessa);
		long ini = System.currentTimeMillis();
		
		Connection conn = null;
		CallableStatement ps = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareCall("{call spu_atualiza_movimento_pendente (?)}");
			ps.setInt(1, idRemessa);
			ps.execute();
		}
		catch(Exception e) {
			logger.error("ao AtualizarRemessa : " + e.getMessage(), e);
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch(Exception e) {
				logger.error("ao AtualizarRemessa : " + e.getMessage(), e);
			}
		}
		
		logger.debug("Executado em " + (System.currentTimeMillis() - ini) + " ms");
	}
}
