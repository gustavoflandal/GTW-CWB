package com.consilux.model;

import java.sql.SQLException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.ConfiguracaoServidorSmtp;
import com.consilux.infra.ServicoEmail;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.EventoCSX.TipoEvento;
import com.consilux.model.beans.EmailEnviarBean;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobEnviaEmail implements Job {

	private static Logger logger = Logger.getLogger(JobEnviaEmail.class);	  
	private static Semaphore semaforoExecucao = new Semaphore(1);
	private static Semaphore semaforoServidores;

	// Contador para round-robin, serve para escolher qual server será
	// utilizado para o envio dos emails.O server é instanciado baseado neste índice. 
	private static int servidorEnvio = 0;

	// Listas de string, com as configurações dos servidores
	private static List<ConfiguracaoServidorSmtp> listaServidores;

	// ThreadPool com workers para realizar o envio do email propriamente dito.
	private static ScheduledThreadPoolExecutor mailSender;
	
	// Construtor de classe
	static {
		listaServidores = ConfiguracaoProvider.getInstance().getListaServidoresSmtp();
		mailSender = new ScheduledThreadPoolExecutor(listaServidores.size());
		semaforoServidores = new Semaphore(listaServidores.size());
	}	
	
	private static long lastException = 0; // Iniciado com zero.
	
	private static long TEMPO_MIN_ENTRE_EXCECOES  = 1000 * 60 * 10;  // 10 Minutos
	
	private static synchronized int getIndiceServidorEnvio() throws InterruptedException {

		semaforoServidores.acquire();

		if (servidorEnvio >= listaServidores.size())
			servidorEnvio = 0;
		
		return servidorEnvio++;
	}	
	
	/**
	 * Obtém um servidor para envio de emails.
	 * @return
	 * @throws InterruptedException 
	 */
	private static ServicoEmail getServidorEnvio() throws InterruptedException {
		
		// Pega um índice para realizar round-robin nos servidores.
		int indiceSrv = getIndiceServidorEnvio();

		// Recupera as informações do servidor selecionado.
		String smtpUser = listaServidores.get(indiceSrv).getUser();
		String smtpPassword = listaServidores.get(indiceSrv).getPassword();
		String smtpHost = listaServidores.get(indiceSrv).getHost();					
		
		// Obtém um serviço de email para despachar (o email).
		ServicoEmail servicoEmail = new ServicoEmail(smtpUser, smtpPassword, smtpHost);
		
		return servicoEmail;
	}
	
	@Override
	public void execute(JobExecutionContext jobContext) throws JobExecutionException {
		
		Queue<EmailEnviarBean> emailsEnviar;
		boolean lockExclusivo = false;
		
		try {
			
			// Loga o início da operação.
			logger.info("Job envia email. Iniciando...");
			
			// Tenta obter o lock.
			lockExclusivo = semaforoExecucao.tryAcquire();
			
			if (!lockExclusivo) {
				logger.info("Já existe um job de envio de emails em execução.");
				return;
			}
			
			// Recupera os emails que estão pendentes (a serem enviados).
			emailsEnviar = EmailEnviar.listarEmails(true);
			int nEmails = emailsEnviar.size();
			
			if (nEmails > 0)
			{
				logger.info(String.format("[%d] emails a serem enviados.", nEmails));
			}
			else
			{
				logger.info("Nenhum email a ser enviado.");
				return;
			}
			
			for (int i = 0; i < nEmails; i++) {
			
				final EmailEnviarBean eml = emailsEnviar.poll();				
				
				// Obtém um servidor (que efetivamente envia, via SMTP).
				final ServicoEmail servicoEmail = getServidorEnvio();
				
				// Dispara (em uma outra thread).
				mailSender.submit(new Runnable() {
						
					@Override
					public void run() {
						
						EventoCSX eventoCSX = null;
						
						try {
							
							// Recupera os anexos do email.
							eml.setAnexos(AnexoEmail.buscaAnexos(eml.getIdEmail()));
							
							// Loga o início da operação.
							logger.info(String.format("Enviando email (idEmail = %d)",
								eml.getIdEmail()));
							
							// Tenta enviar o email
							servicoEmail.enviarEmail(eml);
							
							// Se enviou, grava nos eventos do sistema
							eventoCSX = new EventoCSX(TipoEvento.EMAIL_SENT, 
								"Sistema", "Agendador de Tarefas",
								"Email enviado com sucesso. [" + eml.getIdEmail() + "]");
							
							logger.debug(String.format("Email enviado com sucesso (idEmail = %d, servidor = %s).",
								eml.getIdEmail(), servicoEmail.getSmtpHost()));
						}
						catch (Exception ex) {
							
							// Mecanismo para evitar que fiquem sendo gravadas infinitas exceções.
							if (System.currentTimeMillis() > (lastException + TEMPO_MIN_ENTRE_EXCECOES))
							{
								// Atualiza o contador.
								lastException = System.currentTimeMillis();

								// Como deu erro, grava no log...
								logger.error(String.format("Erro ao enviar email (idEmail = %d, servidor = %s).",
									eml.getIdEmail(), servicoEmail.getSmtpHost()), ex);
							
								// E também nos eventos do sistema.
								eventoCSX = new EventoCSX(TipoEvento.EMAIL_ERROR, 
									"Sistema", "Agendador de Tarefas",
									String.format("Erro ao enviar email (idEmail = %d, servidor = %s).",
									eml.getIdEmail(), servicoEmail.getSmtpHost()));
							}
						}
						finally {
							semaforoServidores.release();
							try {
								if (eventoCSX != null)
								{
									Evento.incluirEventoCSX(eventoCSX);
								}
							} catch (ConexaoException ce) {
								logger.error("Erro de conexão ao incluir evento.", ce);
							} catch (SQLException se) {
								logger.error("Erro de SQL ao inserir evento.", se);
							}	
						}
					}
				});
				
			}
			
		} catch (Exception ex) {
			logger.error("Erro inesperado ao enviar emails.", ex);	
		}
		finally {
			if (lockExclusivo)
				semaforoExecucao.release();
		}
		
	}
	
}
