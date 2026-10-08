/**********************************************************************************

  Projeto: GTW

  Nome do Modulo: com.consilux.model

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 01/04/2010

  Descricao: Processa infrações em processos que possuem processamento automático.

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * XXX
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobProcessaAutomatico implements Job {
	
	private static Logger logger = Logger.getLogger(JobProcessaAutomatico.class);
	private static Semaphore semaforoExecucao = new Semaphore(1);
	private static String MSG_ERRO = "Não foi possível definir o usuário padrão do sistema.";
	
	public void execute(JobExecutionContext cetx) throws JobExecutionException {
		
		logger.info("Iniciando JobProcessaAutomatico");

		try {
			boolean lockExclusivo = false;
			try {
				// Tenta obter o lock.
				lockExclusivo = semaforoExecucao.tryAcquire();
				if (!lockExclusivo) {
					logger.info("Já existe um JobProcessaAutomatico em execução.");
					return;
				}
	
				processaAutomatico();
				logger.info("Finalizando JobProcessaAutomatico");
			}
			finally {
				if (lockExclusivo)
					semaforoExecucao.release();
			}
		}
		catch (Exception ex) {
			logger.error("Erro ao executar JobProcessaAutomatico.", ex);
		}
		logger.info("JobProcessaAutomatico terminado.");
	}
	
	private void processaAutomatico() throws ConexaoException, SQLException, ModelException {
		
		Configuracao conf = ConfiguracaoProvider.getInstance();
		
		if (!conf.getConfiguracaoChaveValor().containsKey("usuario_sistema"))
			throw new ModelException(MSG_ERRO);
		
		Connection con = null;
		CallableStatement cs = null;

		try {
			int idUsuario = Integer.parseInt(conf.getConfiguracaoChaveValor().get("usuario_sistema"));
			
			con = Conexao.getConexao();
			List<Processo> processos = Processo.buscaProcessosExecucaoAutomatica();

			for (Processo processo : processos) {
				String sSQL = "{call " + processo.getSpuExecucaoAutomatica().trim() + "(?, ?)}";
				cs = con.prepareCall(sSQL);
				cs.setInt(1, processo.getIdProcesso());
				cs.setInt(2, idUsuario);
				cs.execute();
				
				SQLWarning w = cs.getWarnings();
				
				while (w != null) {
					logger.debug("SQLWarning ["+processo.getSpuExecucaoAutomatica().trim()+"("+processo.getIdProcesso()+","+idUsuario+")]: "+w.getMessage());
					w = w.getNextWarning();
				}
				
				cs.close();
			}
		}
		catch (NumberFormatException fe) {
			logger.error(MSG_ERRO, fe);
			throw new ModelException(MSG_ERRO, fe);
		}
		finally {
			if (con != null)
				con.close();
		}
	}
}
