package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.AgendamentoProcessamentoBean;
import com.consilux.model.exception.ModelException;

/**
 * Implementacao de um job do Quartz para processamento (agendado) de infrações.  
 * @author raoni
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobProcessaInfracaoAgendamento implements Job {

	private static Logger logger = Logger.getLogger(JobProcessaInfracaoAgendamento.class);
	public static Semaphore semaforoExecucao = new Semaphore(1);

	public void execute(JobExecutionContext cetx) throws JobExecutionException {

		logger.info("Iniciando JobProcessaInfracaoAgendamento");

		try {
			boolean lockExclusivo = false;
			try {
				// Tenta obter o lock.
				lockExclusivo = semaforoExecucao.tryAcquire();
				logger.debug("Semáforo Job: aquis: "+lockExclusivo);
				if (!lockExclusivo) {
					logger.info("Já existe um JobProcessaInfracaoAgendamento em execução.");
					return;
				}

				logger.info("Processando infrações agendadas ...");
				processaInfracaoAgendamento();
				logger.info("Finalizando processamento de infrações agendadas.");
			}
			finally {
				logger.debug("Semáforo Job: relis.");
				if (lockExclusivo)
					semaforoExecucao.release();
			}
		}
		catch (Exception ex) {
			logger.error("Erro ao executar JobProcessaInfracaoAgendamento.", ex);
		}
		logger.info("JobAgendamentoProcessamento terminado.");
	}

	/**
	 * Método que realiza o trabalho do processamento automático propriamente dito.
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws ModelException
	 */
	private void processaInfracaoAgendamento() throws JobExecutionException  {

		Connection conn = null;
		CallableStatement csProcessaAgendamento = null;
		CallableStatement csRemoverAgendamento = null;
		
		List<AgendamentoProcessamentoBean> listaAgendados;
		
		try {
			// Buscar o que estiver no agendamento
			listaAgendados = AgendamentoProcessamento.listarTodos();
			logger.info("Quantidade de infrações que estão agendadas: [" + listaAgendados.size()  + "]");
			
			// Se tiver infração com agendamento....
			if (listaAgendados.size() > 0) {
				
				// Obtém uma conexão.
				conn = Conexao.getConexao();

				// Prepara um statement para executar a procedure de processamento
				csProcessaAgendamento = conn.prepareCall("{? = call spu_processa_infracao_agendamento(?, ?, ?, ?)}");
				csProcessaAgendamento.registerOutParameter(1, java.sql.Types.BOOLEAN);

				// Prepara os statements para remoção do agendamento
				csRemoverAgendamento = conn.prepareCall("{? = call spu_remover_agendamento (?)}");
				csRemoverAgendamento.registerOutParameter(1, java.sql.Types.BOOLEAN);				
				
				// Cria uma data limite (janela de 24 horas para as requisições). O que for anterior a isso não será processado
				// autmaticamente e retornará ao processamento normal.
				Date janelaLimite = DateUtils.addHours(new Date(), -24);

				// Itera os agendamentos, disparando a procedure ou cancelando o agendamento.			
				for (AgendamentoProcessamentoBean bean : listaAgendados) {

					if (bean.getDataRequisicao().after(janelaLimite)) {
						// Requisição ainda vale. Usar a procedure (para realizar o processamento)
						csProcessaAgendamento.setInt(2, bean.getIdInfracao());
						csProcessaAgendamento.setInt(3, bean.getIdUsuario());
						csProcessaAgendamento.setInt(4, bean.getIdProcesso());
						
						// Id inconsistência é opcional (está com o default = NULL na procedure)
						if (bean.getIdInconsistencia() == null)
						{
							csProcessaAgendamento.setNull(5, Types.INTEGER);
						} else {
							csProcessaAgendamento.setInt(5, bean.getIdInconsistencia());
						}
						
						// Dispara a procedure
						csProcessaAgendamento.execute();
						boolean processou = csProcessaAgendamento.getBoolean(1);
						
						if (!processou) {
							// Se não processou, grava na tabela de aGENDAMENTO
							String msgErro = "";
							if (csProcessaAgendamento.getWarnings() != null)
								msgErro = csProcessaAgendamento.getWarnings().getMessage();
							if (msgErro.length() > 200)
								msgErro= msgErro.substring(0, 200);
							
							PreparedStatement psGravaErro = conn.prepareStatement(
								"UPDATE agendamento_processamento SET status_agendamento = 1, msg_erro = ? " +
								"WHERE id_infracao = ?");
	
							psGravaErro.setString(1, msgErro);
							psGravaErro.setInt(2, bean.getIdInfracao());
							psGravaErro.executeUpdate();
						}
					} else {
						// Requisição 'caducou'.
						
						//Retirar esta infração do agendamento.
						csRemoverAgendamento.setInt(2, bean.getIdInfracao());

						// Dispara a procedure
						csRemoverAgendamento.execute();
					
						logger.warn("Infração [" + bean.getIdInfracao()  + "] caducou o agendamento e retornou ao processamento.");
					}
				}
			}
		} catch (ConexaoException ce) {
			logger.error("Erro ao obter conexão para execução da JobProcessaInfracaoAgendamento.", ce);
			throw new JobExecutionException("Erro ao obter conexão para execução da JobProcessaInfracaoAgendamento.", ce);
		} catch (SQLException se) {
			logger.error("Erro de SQL na execução da spu_processa_infracao_agendamento.", se);
			throw new JobExecutionException("Erro de SQL na execução da JobProcessaInfracaoAgendamento.", se);
		} finally {
			if (conn != null)
			{
				try {
					// Fecha a conexão.
					conn.close();
				} catch (SQLException se) {
					logger.error("Erro ao fechar a conexão após a execução da JobProcessaInfracaoAgendamento.", se);
					throw new JobExecutionException("Erro ao fechar a conexão após a execução da JobProcessaInfracaoAgendamento.", se);
				}
			}
		}
	}
}
