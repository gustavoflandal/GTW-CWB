package muralha.digital.notificacao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.ConfiguracaoServidorSmtp;

import muralha.digital.consulta.TipoRegistro;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobEnviaEmailAlerta implements Job {

	private static Logger logger = Logger.getLogger(JobEnviaEmailAlerta.class);	  
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
	static
	{
		listaServidores = ConfiguracaoProvider.getInstance().getListaServidoresSmtp();
		mailSender = new ScheduledThreadPoolExecutor(listaServidores.size());
		semaforoServidores = new Semaphore(listaServidores.size());
	}	
	
	private static long lastException = 0; // Iniciado com zero.
	
	private static long TEMPO_MIN_ENTRE_EXCECOES  = 1000 * 60 * 10;  // 10 Minutos
	
	private static synchronized int getIndiceServidorEnvio() throws InterruptedException
	{
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
	private static ServicoEmailMuralha getServidorEnvio() throws InterruptedException
	{
		// Pega um índice para realizar round-robin nos servidores.
		int indiceSrv = getIndiceServidorEnvio();

		// Recupera as informações do servidor selecionado.
		String smtpUser = listaServidores.get(indiceSrv).getUser();
		String smtpPassword = listaServidores.get(indiceSrv).getPassword();
		String smtpHost = listaServidores.get(indiceSrv).getHost();
		Integer smtpPort = listaServidores.get(indiceSrv).getPort();
		
		// Obtém um serviço de email para despachar (o email).
		ServicoEmailMuralha servicoEmail = new ServicoEmailMuralha(smtpUser, smtpPassword, smtpHost, smtpPort);
		
		return servicoEmail;
	}
	
	@Override
	public void execute(JobExecutionContext jobContext) throws JobExecutionException
	{
		Queue<Notificacao> emailsEnviar;
		boolean lockExclusivo = false;
		
		try
		{
			// Loga o início da operação.
			logger.info("[Muralha] Job envia email de alertas. Iniciando...");
			
			// Tenta obter o lock.
			lockExclusivo = semaforoExecucao.tryAcquire();
			
			if (!lockExclusivo)
			{
				logger.info("[Muralha] Já existe um job de envio de emails de alertas em execução.");
				return;
			}
			
			// Recupera os emails que estão pendentes (a serem enviados).
			UUID idTipoRegistro = TipoRegistro.Tipo.ALERTA.GetID();
			emailsEnviar = Notificacoes.ObterNotificacoesPendentesEmail(idTipoRegistro);
			int nEmails = emailsEnviar.size();
			
			if (nEmails > 0)
			{
				logger.info(String.format("[Muralha] [%d] emails de alertas a serem enviados.", nEmails));
			}
			else
			{
				logger.info("[Muralha] Nenhum email de alertas a ser enviado.");
				return;
			}
			
			Map<String, String> listaEmailJaEnviado = new LinkedHashMap<String, String>();
			
			for (int i = 0; i < nEmails; i++)
			{
				final Notificacao eml = emailsEnviar.poll();
				
				// Obtém um servidor (que efetivamente envia, via SMTP).
				final ServicoEmailMuralha servicoEmail = getServidorEnvio();
				
				// Dispara (em uma outra thread).
				mailSender.submit(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							// Loga o início da operação.
							logger.info(String.format("[Muralha] Enviando email de alerta (idNotificacao = %s | idAlerta = %s)", eml.getId().toString(), eml.getIdAlerta().toString()));
							
							List<InternetAddress> listaEmailEnviar = new ArrayList<InternetAddress>();
							
							for (InternetAddress address : eml.getDestinatarios())
							{
								InternetAddress email = address;
								String key = eml.getIdAlerta().toString() + "_" + email.getAddress();
								
								if (listaEmailJaEnviado.containsKey(key))
								{
									listaEmailEnviar.remove(email);
								}
								else
								{
									listaEmailEnviar.add(email);
								}
							}
							
							boolean enviarEmail = (!listaEmailEnviar.isEmpty());
							
							// Tenta enviar o email
							servicoEmail.enviarEmail(eml, listaEmailEnviar, enviarEmail);
							

							for (InternetAddress email : listaEmailEnviar)
							{
								String key = eml.getIdAlerta().toString() + "_" + email.getAddress();
								
								if (!listaEmailJaEnviado.containsKey(key))
									listaEmailJaEnviado.put(key, email.getAddress());
							}
							
							logger.debug(String.format("[Muralha] Email de alerta enviado com sucesso (idNotificacao = %s | idAlerta = %s, servidor = %s).",
								eml.getId().toString(), eml.getIdAlerta().toString(), servicoEmail.getSmtpHost()));
						}
						catch (Exception ex)
						{
							// Mecanismo para evitar que fiquem sendo gravadas infinitas exceções.
							if (System.currentTimeMillis() > (lastException + TEMPO_MIN_ENTRE_EXCECOES))
							{
								// Atualiza o contador.
								lastException = System.currentTimeMillis();

								// Como deu erro, grava no log...
								logger.error(String.format("[Muralha] Erro ao enviar email de alerta (idNotificacao = %s | idAlerta = %s, servidor = %s).",
									eml.getId().toString(), eml.getIdAlerta().toString(), servicoEmail.getSmtpHost()), ex);
							}
						}
						finally
						{
							semaforoServidores.release();
						}
					}
				});
				
			}
			
		}
		catch (Exception ex)
		{
			logger.error("[Muralha] Erro inesperado ao enviar emails de alertas.", ex);	
		}
		finally
		{
			if (lockExclusivo)
				semaforoExecucao.release();
		}
	}
}
