package muralha.digital.notificacao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import muralha.digital.consulta.TipoRegistro;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobEnviaSmsOcorrencia implements Job {

	private static Logger logger = Logger.getLogger(JobEnviaSmsOcorrencia.class);
	private static Semaphore semaforoExecucao = new Semaphore(1);
	private static ScheduledThreadPoolExecutor smsSender;

	// Construtor de classe
	static
	{
		smsSender = new ScheduledThreadPoolExecutor(1);
	}	
	
	private static long lastException = 0; // Iniciado com zero.
	
	private static long TEMPO_MIN_ENTRE_EXCECOES  = 1000 * 60 * 10;  // 10 Minutos
	
	@Override
	public void execute(JobExecutionContext jobContext) throws JobExecutionException
	{
		Queue<Notificacao> smsEnviar;
		boolean lockExclusivo = false;
		
		try
		{
			// Loga o início da operação.
			logger.info("[Muralha] Job envia SMS de irregularidades. Iniciando...");
			
			// Tenta obter o lock.
			lockExclusivo = semaforoExecucao.tryAcquire();
			
			if (!lockExclusivo)
			{
				logger.info("[Muralha] Já existe um job de envio de SMS de irregularidades em execução.");
				return;
			}
			
			// Recupera os SMSs que estão pendentes (a serem enviados).
			UUID idTipoRegistro = TipoRegistro.Tipo.IRREGULARIDADES.GetID();
			smsEnviar = Notificacoes.ObterNotificacoesPendentesSMS(idTipoRegistro);
			int nSMS = smsEnviar.size();
			
			if (nSMS > 0)
			{
				logger.info(String.format("[Muralha] [%d] SMS de irregularidades a serem enviados.", nSMS));
			}
			else
			{
				logger.info("[Muralha] Nenhum SMS de irregularidades a ser enviado.");
				return;
			}
			
			Map<String, String> listaTelefoneJaEnviado = new LinkedHashMap<String, String>();
			
			for (int i = 0; i < nSMS; i++)
			{
				final Notificacao eml = smsEnviar.poll();
				
				// Obtém um servidor (que efetivamente envia, via SMTP).
				final ServicoSMS servicoSMS = new ServicoSMS();
				
				// Dispara (em uma outra thread).
				smsSender.submit(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							// Loga o início da operação.
							logger.info(String.format("[Muralha] Enviando SMS de irregularidade (idNotificacao = %s | idOcorrencia = %s)", eml.getId().toString(), eml.getIdOcorrencia().toString()));
							
							List<String> listaTelefoneEnviar = new ArrayList<String>();
							
							for (String telefone : eml.getDestinatariosSMS())
							{
								String key = eml.getIdOcorrencia().toString() + "_" + telefone;
								
								if (listaTelefoneJaEnviado.containsKey(key))
								{
									listaTelefoneEnviar.remove(telefone);
								}
								else
								{
									listaTelefoneEnviar.add(telefone);
								}
							}
							
							boolean enviarSMS = (!listaTelefoneEnviar.isEmpty());
							
							// Tenta enviar o SMS
//							servicoSMS.EnviarSMS_Twilio(eml, listaTelefoneEnviar, enviarSMS);
							servicoSMS.EnviarSMS_FacilitaMovel(eml, listaTelefoneEnviar, enviarSMS);
//							servicoSMS.EnviarSMS_Comtele(eml, listaTelefoneEnviar, enviarSMS);
							

							for (String telefone : listaTelefoneEnviar)
							{
								String key = eml.getIdOcorrencia().toString() + "_" + telefone;
								
								if (!listaTelefoneJaEnviado.containsKey(key))
									listaTelefoneJaEnviado.put(key, telefone);
							}
							
							logger.debug(String.format("[Muralha] Envio de SMS de irregularidade finalizado (idNotificacao = %s | idOcorrencia = %s).",
								eml.getId().toString(), eml.getIdOcorrencia().toString()));
						}
						catch (Exception ex)
						{
							// Mecanismo para evitar que fiquem sendo gravadas infinitas exceções.
							if (System.currentTimeMillis() > (lastException + TEMPO_MIN_ENTRE_EXCECOES))
							{
								// Atualiza o contador.
								lastException = System.currentTimeMillis();

								// Como deu erro, grava no log...
								logger.error(String.format("[Muralha] Erro ao enviar SMS de irregularidade (idNotificacao = %s | idOcorrencia = %s).",
									eml.getId().toString(), eml.getIdOcorrencia().toString()), ex);
							}
						}
					}
				});
				
			}
			
		}
		catch (Exception ex)
		{
			logger.error("[Muralha] Erro inesperado ao enviar SMSs de irregularidades.", ex);	
		}
		finally
		{
			if (lockExclusivo)
				semaforoExecucao.release();
		}
	}
}
