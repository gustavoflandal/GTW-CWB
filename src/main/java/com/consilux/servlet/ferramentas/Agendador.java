package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.internet.AddressException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobPersistenceException;
import org.quartz.Scheduler;
import org.quartz.SchedulerConfigException;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoExportaImagens;
import com.consilux.conf.ConfiguracaoExportaTrafego;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.JobBuscaRemessasPendentes;
import com.consilux.model.JobEnviaEmail;
import com.consilux.model.JobEnviaMovimentoValidado;
import com.consilux.model.JobImportaXMLConfigEquip;
import com.consilux.model.JobProcessaAutomatico;
import com.consilux.model.JobProcessaInfracaoAgendamento;
import com.consilux.model.JobVeiculoMonitoradoImportado;
import com.consilux.model.descarga.ExportaDescarga;
import com.consilux.model.exception.ModelException;
import com.consilux.model.ferramenta.ExportaImagens;
import com.consilux.model.ferramenta.ExportaTrafego;

import muralha.digital._ini.Inicializacao;
import muralha.digital.notificacao.JobEnviaEmailAlerta;
import muralha.digital.notificacao.JobEnviaEmailOcorrencia;
import muralha.digital.notificacao.JobEnviaSmsAlerta;
import muralha.digital.notificacao.JobEnviaSmsOcorrencia;

public class Agendador extends HttpServlet {

	private static final long serialVersionUID = -710583699870957710L;
	private static final Logger logger = Logger.getLogger(Agendador.class);
	public static final String QUARTZ_FACTORY_KEY = "org.quartz.impl.StdSchedulerFactory.KEY";
	
	private static final boolean EMAIL_ATIVO_MURALHA = Inicializacao.emailAtivo;
	private static final boolean SMS_ATIVO_MURALHA = Inicializacao.smsAtivo;

	private static boolean performShutdown = true;
	private static Scheduler quartzScheduler = null;

	public static final String GRUPO_ENVIA_EMAILS = "Grupo Envia Emails";
	public static final String JOB_ENVIA_EMAILS = "Job Envia Emails";
	public static final String JOB_MONITORADO_IMPORTADO = "Job Envia Emails Monitorado Importado";

	public static final String GRUPO_TRAFEGO = "Trafego";
	public static final String JOB_TRAFEGO_TEMPO_REAL = "Trafego tempo real";

	public static final String GRUPO_EXPORTA_IMAGENS = "Exporta Imagens";
	public static final String JOB_EXPORTA_IMAGENS = "Exporta Imagens";

	public static final String GRUPO_CONFIG_EQUIP = "ConfigEquip";
	public static final String JOB_IMPORTA_CONFIG_EQUIP_XML = "Importa ConfigEquip pendentes";

	
	public static final String GRUPO_PROCESSAMENTO = "Processamento";
	public static final String JOB_PROCESSA_AUTOMATICO = "Processa Infrações Automaticamente";
	public static final String JOB_PROCESSA_INFRACAO_AGENDAMENTO = "Processa Infrações Agendadas";
	
	public static final String JOB_PROCESSA_MOVIMENTOS_VALIDADOS = "Envio automático de Movimentos Validados";
	public static final String JOB_BUSCA_REMESSAS_PENDENTES = "Busca remessas Pendentes";

	public static final String GRUPO_DESCARGA = "Descarga";
	public static final String JOB_EXPORTACAO_DESCARGA = "Exportação de Descarga";
	
	public static final String GRUPO_ENVIA_EMAILS_OCORRENCIA_MURALHA = "Grupo Envia Emails de Ocorrências";
	public static final String JOB_ENVIA_EMAILS_OCORRENCIA_MURALHA = "Job Envia Emails de Ocorrências";
	
	public static final String GRUPO_ENVIA_EMAILS_ALERTA_MURALHA = "Grupo Envia Emails de Alertas";
	public static final String JOB_ENVIA_EMAILS_ALERTA_MURALHA = "Job Envia Emails de Alertas";
	
	public static final String GRUPO_ENVIA_SMS_OCORRENCIA_MURALHA = "Grupo Envia SMS de Ocorrências";
	public static final String JOB_ENVIA_SMS_OCORRENCIA_MURALHA = "Job Envia SMS de Ocorrências";
	
	public static final String GRUPO_ENVIA_SMS_ALERTA_MURALHA = "Grupo Envia SMS de Alertas";
	public static final String JOB_ENVIA_SMS_ALERTA_MURALHA = "Job Envia SMS de Alertas";

	public static final String GRUPO_INTEGRIDADE = "Integridade";
	public static final String JOB_SHA256_IMAGEM = "Calcula SHA-256 de Imagens";

	public static final String GRUPO_MONITORAMENTO = "Monitoramento";
	public static final String JOB_SLA_LATENCIA = "Monitora SLA de Latência";
	public static final String JOB_SLA_PREPROC = "Monitora SLA Pré-processamento 72h";
	
	private static Integer INTERVALO_EXECUCAO_EMAIL_MINUTOS = (Inicializacao.IntervaloExecucaoEmailMinutos == null ? 1 : Inicializacao.IntervaloExecucaoEmailMinutos);
	private static Integer INTERVALO_EXECUCAO_SMS_MINUTOS = (Inicializacao.IntervaloExecucaoSmsMinutos == null ? 5 : Inicializacao.IntervaloExecucaoSmsMinutos);
	
 
	/**
	 * Verifica se existe uma determinada job agendada no Quartz.
	 * @param grupoJob o nome do grupo que se deseja verificar.
	 * @param nomeJob o nome da job que se deseja verificar.
	 * @return
	 */
	public static boolean possuiJob(String grupoJob, String nomeJob) {
 
		boolean encontrouJob = false;
		
		try {
			encontrouJob = quartzScheduler.checkExists(new JobKey(nomeJob, grupoJob));
		} catch (SchedulerException e) {
			logger.error("Erro ao verificar se um job existe.", e);
		}
		return encontrouJob;
	}

	/**
	 * Método utilizado para garantir que sempre teremos um job para enviar
	 * emails e outro para exportar o tráfego.
	 * @throws ModelException
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws AddressException
	 */
	private static void verificarJobsGTW()
	throws ModelException, ConexaoException, SQLException, AddressException {

		if (quartzScheduler == null)
		{
			logger.error("Não foi encontrado um agendador QUARTZ. Jobs não serão executadas.");
			return;
		}

		try {

			// Limpa qualquer coisa que exista agendada até então
			limparAgendador();

//			reajustaJobEnviaEmails();				-> Desabilitado em 16/03/2015
//			reajustaJobTrafegoTempoReal();			-> Desabilitado em 16/03/2015, visto que contratos novos já geram arquivo de tráfego (DT) nos equipamentos
			reajustaJobProcessamentoAutomatico();
//			reajustaJobEnviaEmailVeiculosMonitoradosImportados(); -> Desabilitado em 16/03/2015
			reajustaJobProcessaInfracaoAgendamento();
//			reajustaJobImportacaoConfigEquipXML(); 	-> Desabilitado em 16/03/2015, visto que somente o servidor atualiza o equipamento, e não o contrário
//			reajustaJobExportaDescarga(); 			-> Desabilitado em 16/03/2015, não existe descarga para os contratos novos (C011+)
//			reajustaJobExportaImagens();			-> Desabilitado em 16/03/2015
			
			reajustaJobEnviaEmailsAlertaMuralha();
			reajustaJobEnviaEmailsOcorrenciaMuralha();
			reajustaJobEnviaSmsAlertaMuralha();
			reajustaJobEnviaSmsOcorrenciaMuralha();
			reajustaJobSha256Imagem();
			reajustaJobSlaLatencia();
			reajustaJobSlaPreprocessamento();

			String modo_cav = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("habilitar_funcionalidade_cav");
			if(modo_cav != null && modo_cav.equals("1")) {
				reajustaJobExportaMovimentoValidado();
				reajustaJobBuscaRemessasPendentes();
			}
			
			// ***************** Um teste simples ***************** 
			//			EmailEnviarBean emailEnviar = new EmailEnviarBean();
			//			emailEnviar.setAssunto("[GTW - Teste] - Teste de Email");
			//			emailEnviar.setCorpo("Eu sou um email de teste\r\n\r\nAtenciosamente,\r\nRaoni.");
			//			emailEnviar.getDestinatarios().add(new InternetAddress("raoni@consilux.com.br"));
			//			emailEnviar.setRemetente(new InternetAddress("raoni@consilux.com.br"));
			//			EmailEnviar.inserir(emailEnviar);			
			// ***************** Um teste simples ***************** 

		} catch (SchedulerException ex) {
			logger.fatal("Erro ao verificar os jobs do GTW,", ex);
		}
	}
	
	public static Date ProximaExecucao(String grupo, String tarefa) {
		Date response = null;
		try {
			Trigger trigger = quartzScheduler.getTrigger(new TriggerKey(tarefa, grupo));
			response = trigger.getNextFireTime();
		} catch (Exception e) {
			logger.error("Não foi possível encontrar trigger para [" + grupo + "] [" + tarefa + "]", e);
		}
		return response;
	}

	private static void reajustaJobExportaMovimentoValidado() throws SchedulerException {

		boolean achouJob = possuiJob(GRUPO_PROCESSAMENTO, JOB_PROCESSA_MOVIMENTOS_VALIDADOS);
		
		if (!achouJob) {
			CronExpression cron = null;
			try {
				cron = new CronExpression("0 0/20 19-23 * * ? *");
			} catch(Exception e) {}
			
			JobDetail tarefa = JobBuilder.newJob(JobEnviaMovimentoValidado.class)
					.withIdentity(JOB_PROCESSA_MOVIMENTOS_VALIDADOS, GRUPO_PROCESSAMENTO)
					.build();
			
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(JOB_PROCESSA_MOVIMENTOS_VALIDADOS, GRUPO_PROCESSAMENTO)
					.startNow()
					.withSchedule(CronScheduleBuilder.cronSchedule(cron))
					.build();
			
			logger.info("Recriando a job de processo automático .");
			quartzScheduler.scheduleJob(tarefa, trigger);
		}
	}
	
	private static void reajustaJobBuscaRemessasPendentes() throws SchedulerException {
		
		boolean achouJob = possuiJob(GRUPO_PROCESSAMENTO, JOB_BUSCA_REMESSAS_PENDENTES);
		
		if (!achouJob) {
			
			JobDetail tarefa = JobBuilder.newJob(JobBuscaRemessasPendentes.class)
					.withIdentity(JOB_BUSCA_REMESSAS_PENDENTES, GRUPO_PROCESSAMENTO)
					.build();
			
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(JOB_BUSCA_REMESSAS_PENDENTES, GRUPO_PROCESSAMENTO)
					.startAt(DateBuilder.evenMinuteDateAfterNow())
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever())
					.build();
			
			logger.info("Recriando a job de busca de Remessas Pendentes.");
			quartzScheduler.scheduleJob(tarefa, trigger);
		}
	}

	@SuppressWarnings("unused")
	private static void reajustaJobImportacaoConfigEquipXML() throws SchedulerException {

		boolean achouJob = possuiJob(GRUPO_CONFIG_EQUIP, JOB_IMPORTA_CONFIG_EQUIP_XML);

		if (!achouJob)
		{
			JobDetail tarefa = JobBuilder.newJob(JobImportaXMLConfigEquip.class)
					.withIdentity(JOB_IMPORTA_CONFIG_EQUIP_XML, GRUPO_CONFIG_EQUIP)
					.build();
			
			Trigger trigger = TriggerBuilder.newTrigger()
					.startAt(DateBuilder.nextGivenMinuteDate(null, 5))
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(10))
					.build();
			
			quartzScheduler.scheduleJob(tarefa, trigger);
		}
		
	}

	@SuppressWarnings("unused")
	private static void reajustaJobEnviaEmailVeiculosMonitoradosImportados() throws SchedulerException {

		// Verifica e adiciona (caso não encontre) o job de veículos monitorados importados.
		boolean achouJob = possuiJob(GRUPO_ENVIA_EMAILS, JOB_MONITORADO_IMPORTADO);
		if (!achouJob) {

			logger.info("Recriando a job de tráfego.");

			JobDetail jobDetail = JobBuilder.newJob(JobVeiculoMonitoradoImportado.class)
					.withIdentity(JOB_MONITORADO_IMPORTADO, GRUPO_ENVIA_EMAILS)
					.build();

			Trigger jobTrigger = TriggerBuilder.newTrigger()
					.withIdentity("Trigger - A cada 5 minutos", GRUPO_TRAFEGO)
					.startNow()														// Começa agora.
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(5))	// Repete a cada 5 minutos
					.build();

//			jobTrigger.setVolatility(true);
//			jobDetail.setDurability(true); // true = vai permanecer no store mesmo depois da sua execução, false caso contrário
//			jobDetail.setVolatility(true); // true = vai ser persistido no banco, false caso contrário

			quartzScheduler.scheduleJob(jobDetail, jobTrigger);				
		}
	}

	/**
	 * Job de processamento automático (ex: filtros)
	 * @throws SchedulerException 
	 */
	private static void reajustaJobProcessamentoAutomatico() throws SchedulerException {

		boolean achouJob = possuiJob(GRUPO_PROCESSAMENTO, JOB_PROCESSA_AUTOMATICO);

		if (!achouJob)
		{
			JobDetail tarefa = JobBuilder.newJob(JobProcessaAutomatico.class)
					.withIdentity(JOB_PROCESSA_AUTOMATICO, GRUPO_PROCESSAMENTO)
					.build();
			
			Trigger trigger = TriggerBuilder.newTrigger()
					.startAt(DateBuilder.nextGivenMinuteDate(null, 10))
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(10))
					.build();
			
			logger.info("Recriando a job de processo automático .");
			quartzScheduler.scheduleJob(tarefa, trigger);
		}
	}

	/**
	 * Recria a Job de processamento automático das infrações agendadas (ex: liberação)
	 * @throws SchedulerException 
	 */
	private static void reajustaJobProcessaInfracaoAgendamento() throws SchedulerException {

		boolean achouJob = possuiJob(GRUPO_PROCESSAMENTO, JOB_PROCESSA_INFRACAO_AGENDAMENTO);

		if (!achouJob)
		{
			JobDetail tarefa = JobBuilder.newJob(JobProcessaInfracaoAgendamento.class)
					.withIdentity(JOB_PROCESSA_INFRACAO_AGENDAMENTO, GRUPO_PROCESSAMENTO)
					.build();
			
			Trigger trigger = TriggerBuilder.newTrigger()
					.startAt(DateBuilder.nextGivenMinuteDate(null, 10))
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(10))
					.build();
			
			logger.info("Recriando a job de agendamento de processamento.");
			quartzScheduler.scheduleJob(tarefa, trigger);
		}
	}	

	@SuppressWarnings("unused")
	private static void reajustaJobTrafegoTempoReal() throws SchedulerException {

		ConfiguracaoExportaTrafego conf = ConfiguracaoProvider.getInstance().getConfiguracaoExportaTrafego(); 
		if (!conf.getAtivo())
			return;

		Date dtInicio = null;
		try {
			dtInicio = new SimpleDateFormat("HH:mm").parse(conf.getHorarioJob());
		}
		catch (ParseException e) {
			logger.error("Erro ao reagendar o job de tráfego.", e);
			return;
		}
		
		// Verifica e adiciona (caso não encontre) o job de veículos monitorados importados.
		boolean achouJob = possuiJob(GRUPO_TRAFEGO, JOB_TRAFEGO_TEMPO_REAL);
		if (!achouJob) {

			logger.info("Recriando a job de tráfego tempo real.");

			JobDetail jobDetail = JobBuilder.newJob(ExportaTrafego.class)
					.withIdentity(JOB_TRAFEGO_TEMPO_REAL, GRUPO_TRAFEGO)
					.build();

			Trigger jobTrigger = TriggerBuilder.newTrigger()
					.startAt(dtInicio)															// Começa em...
					.withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
							.withIntervalInDays(1)) 											// Repete a cada 1 dia
					.build();

//			jobTrigger.setVolatility(true);
//			jobDetail.setDurability(true); // true = vai permanecer no store mesmo depois da sua execução, false caso contrário
//			jobDetail.setVolatility(true); // true = vai ser persistido no banco, false caso contrário

			quartzScheduler.scheduleJob(jobDetail, jobTrigger);				
		}
	}

	@SuppressWarnings("unused")
	private static void reajustaJobEnviaEmails() throws SchedulerException {

		// Verifica e adiciona (caso não encontre) o job de envio de emails.
		boolean achouJob = possuiJob(GRUPO_ENVIA_EMAILS, JOB_ENVIA_EMAILS);

		if (!achouJob) {

			logger.info("Recriando a job de envio de emails.");

			JobDetail jobDetail = JobBuilder.newJob(JobEnviaEmail.class)
					.withIdentity(JOB_ENVIA_EMAILS, GRUPO_ENVIA_EMAILS)
					.build();

			Trigger jobTrigger = TriggerBuilder.newTrigger()
					.startNow() 													// Começa agora.
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(5)) 	// Repete a cada 5 minutos
					.build();

//			jobTrigger.setVolatility(true);
//			jobDetail.setDurability(true); // true = vai permanecer no store mesmo depois da sua execução, false caso contrário
//			jobDetail.setVolatility(true); // true = vai ser persistido no banco, false caso contrário

			quartzScheduler.scheduleJob(jobDetail, jobTrigger);
		}

	}

	@SuppressWarnings("unused")
	private static void reajustaJobExportaDescarga() throws SchedulerException {

		Configuracao conf = ConfiguracaoProvider.getInstance(); 

//		Date dtInicio = null;
		Calendar cal = Calendar.getInstance();
		try {
			String[] partes = conf.getConfiguracaoDescarga().getHorarioJobExportacao().split(":");
			int hora = Integer.parseInt(partes[0]);
			int minutos = Integer.parseInt(partes[1]);
			cal.set(Calendar.HOUR_OF_DAY, hora);
			cal.set(Calendar.MINUTE, minutos);	
			cal.set(Calendar.SECOND, 0);
			
			if(cal.before(Calendar.getInstance()))
				cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		catch (NumberFormatException e) {
			logger.error("Erro ao reagendar o job de exportação de descarga.", e);
			return;
		}
		
		// Verifica e adiciona (caso não encontre) o job de exportação de descarga
		boolean achouJob = possuiJob(GRUPO_DESCARGA, JOB_EXPORTACAO_DESCARGA);
		if (!achouJob) {

			logger.info("Recriando a job de exportação de descarga.");

			JobDetail jobDetail = JobBuilder.newJob(ExportaDescarga.class)
					.withIdentity(JOB_EXPORTACAO_DESCARGA, GRUPO_DESCARGA)
					.build();

			Trigger jobTrigger = TriggerBuilder.newTrigger()
					.startAt(cal.getTime()) 															// Começa em...
					.withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule() 	
							.withIntervalInDays(1)) 											// Repete a cada 1 dia
					.build();

//			jobTrigger.setVolatility(true);
//			jobDetail.setDurability(true); // true = vai permanecer no store mesmo depois da sua execução, false caso contrário
//			jobDetail.setVolatility(true); // true = vai ser persistido no banco, false caso contrário

			quartzScheduler.scheduleJob(jobDetail, jobTrigger);				
		}
	}

	@SuppressWarnings("unused")
	private static void reajustaJobExportaImagens() throws SchedulerException {

		ConfiguracaoExportaImagens conf = ConfiguracaoProvider.getInstance().getConfiguracaoExportaImagens(); 
		if (!conf.isAtivo())
			return;

		Date dtInicio = null;
		try {
			dtInicio = new SimpleDateFormat("HH:mm").parse(conf.getHorarioJob());
		}
		catch (ParseException e) {
			logger.error("Erro ao reagendar o job de exportação de imagens.", e);
			return;
		}
		
		// Verifica e adiciona (caso não encontre) o job de exportação de imagens
		boolean achouJob = possuiJob(GRUPO_EXPORTA_IMAGENS, JOB_EXPORTA_IMAGENS);
		if (!achouJob) {

			logger.info("Recriando a job de exportação de imagens.");

			JobDetail jobDetail = JobBuilder.newJob(ExportaImagens.class)
					.withIdentity(JOB_EXPORTA_IMAGENS, GRUPO_EXPORTA_IMAGENS)
					.build();

			Trigger jobTrigger = TriggerBuilder.newTrigger()
					.startAt(dtInicio)
					.withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
							.withIntervalInDays(1))
					.build();

//			jobTrigger.setVolatility(true);
//			jobDetail.setDurability(true); // true = vai permanecer no store mesmo depois da sua execução, false caso contrário
//			jobDetail.setVolatility(true); // true = vai ser persistido no banco, false caso contrário

			quartzScheduler.scheduleJob(jobDetail, jobTrigger);				
		}
	}
	
	private static void reajustaJobEnviaEmailsAlertaMuralha() throws SchedulerException
	{
		if (EMAIL_ATIVO_MURALHA)
		{
			// Verifica e adiciona (caso não encontre) o job de envio de emails.
			boolean achouJob = possuiJob(GRUPO_ENVIA_EMAILS_ALERTA_MURALHA, JOB_ENVIA_EMAILS_ALERTA_MURALHA);
	
			if (!achouJob) {
	
				logger.info("Recriando a job de envio de emails de alertas.");
	
				JobDetail jobDetail = JobBuilder.newJob(JobEnviaEmailAlerta.class)
						.withIdentity(JOB_ENVIA_EMAILS_ALERTA_MURALHA, GRUPO_ENVIA_EMAILS_ALERTA_MURALHA)
						.build();
	
				Trigger jobTrigger = TriggerBuilder.newTrigger()
						.startNow() 																					// Começa agora.
						.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(INTERVALO_EXECUCAO_EMAIL_MINUTOS)) 	// Repete a cada 1 minuto
						.build();
	
				quartzScheduler.scheduleJob(jobDetail, jobTrigger);
			}
		}
	}
	
	private static void reajustaJobEnviaEmailsOcorrenciaMuralha() throws SchedulerException
	{
		if (EMAIL_ATIVO_MURALHA)
		{
			// Verifica e adiciona (caso não encontre) o job de envio de emails.
			boolean achouJob = possuiJob(GRUPO_ENVIA_EMAILS_OCORRENCIA_MURALHA, JOB_ENVIA_EMAILS_OCORRENCIA_MURALHA);
	
			if (!achouJob) {
	
				logger.info("Recriando a job de envio de emails de ocorrências.");
	
				JobDetail jobDetail = JobBuilder.newJob(JobEnviaEmailOcorrencia.class)
						.withIdentity(JOB_ENVIA_EMAILS_OCORRENCIA_MURALHA, GRUPO_ENVIA_EMAILS_OCORRENCIA_MURALHA)
						.build();
	
				Trigger jobTrigger = TriggerBuilder.newTrigger()
						.startNow() 																					// Começa agora.
						.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(INTERVALO_EXECUCAO_EMAIL_MINUTOS)) 	// Repete a cada 1 minuto
						.build();
	
				quartzScheduler.scheduleJob(jobDetail, jobTrigger);
			}
		}
	}
	
	private static void reajustaJobEnviaSmsAlertaMuralha() throws SchedulerException
	{
		if (SMS_ATIVO_MURALHA)
		{
			// Verifica e adiciona (caso não encontre) o job de envio de SMS.
			boolean achouJob = possuiJob(GRUPO_ENVIA_SMS_ALERTA_MURALHA, JOB_ENVIA_SMS_ALERTA_MURALHA);
	
			if (!achouJob) {
	
				logger.info("Recriando a job de envio de SMS de alertas.");
	
				JobDetail jobDetail = JobBuilder.newJob(JobEnviaSmsAlerta.class)
						.withIdentity(JOB_ENVIA_SMS_ALERTA_MURALHA, GRUPO_ENVIA_SMS_ALERTA_MURALHA)
						.build();
	
				Trigger jobTrigger = TriggerBuilder.newTrigger()
						.startNow() 																					// Começa agora.
						.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(INTERVALO_EXECUCAO_SMS_MINUTOS)) 	// Repete a cada 1 minuto
						.build();
	
				quartzScheduler.scheduleJob(jobDetail, jobTrigger);
			}
		}
	}
	
	private static void reajustaJobEnviaSmsOcorrenciaMuralha() throws SchedulerException
	{
		if (SMS_ATIVO_MURALHA)
		{
			// Verifica e adiciona (caso não encontre) o job de envio de SMS.
			boolean achouJob = possuiJob(GRUPO_ENVIA_SMS_OCORRENCIA_MURALHA, JOB_ENVIA_SMS_OCORRENCIA_MURALHA);
	
			if (!achouJob) {
	
				logger.info("Recriando a job de envio de SMS de ocorrências.");
	
				JobDetail jobDetail = JobBuilder.newJob(JobEnviaSmsOcorrencia.class)
						.withIdentity(JOB_ENVIA_SMS_OCORRENCIA_MURALHA, GRUPO_ENVIA_SMS_OCORRENCIA_MURALHA)
						.build();
	
				Trigger jobTrigger = TriggerBuilder.newTrigger()
					.startNow() 																					// Começa agora.
						.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(INTERVALO_EXECUCAO_SMS_MINUTOS)) 	// Repete a cada 1 minuto
						.build();
	
				quartzScheduler.scheduleJob(jobDetail, jobTrigger);
			}
		}
	}
	
//	public static void agendarTarefa(String jobGroup, 
//			String jobName, 
//			Class<? extends Job> clazz,
//			String parameterKey,
//			Object parameterValue,
//			long startTime,
//			int repeatCount,
//			long repeatInterval
//	) {
//
//		agendarTarefa(	jobGroup, 
//						jobName, 
//						clazz, 
//						parameterKey, 
//						parameterValue, 
//						new Date(startTime), // converte para Date
//						repeatCount, 
//						repeatInterval
//					);
//		
//	}
//	public static void agendarTarefa(String jobGroup, 
//			String jobName, 
//			Class<? extends Job> clazz,
//			String parameterKey,
//			Object parameterValue,
//			Date startTime,
//			int repeatCount,
//			long repeatInterval
//	) {
//
//		try {
//
//			// Define um novo job.
//			JobDetail jobDetails = new JobDetail(jobName, jobGroup, clazz); 
//
//			// Coloca o parâmetro no mapa de parâmetros do job.
//			if (parameterKey != null && parameterValue != null)
//				jobDetails.getJobDataMap().put(parameterKey, parameterValue);
//
//			jobDetails.setDurability(true);  // true = vai permanecer no store mesmo depois da sua execução, false caso contrário
//			jobDetails.setVolatility(true);  // true = vai ser persistido no banco, false caso contrário
//
//			// Observar que o scheduler vai remover o agendamento (trigger), se tudo ocorrer sem problemas.
//			// Para a job continuar executando, é necessário que a job seja "durável" (vide comentário acima) 
//			Trigger jobTrigger = new SimpleTrigger(jobName, jobGroup, startTime, null, repeatCount, repeatInterval);
//			jobTrigger.setVolatility(true);
//
//			logger.info("Agendando tarefa [" + jobName + "]");
//			quartzScheduler.scheduleJob(jobDetails, jobTrigger);
//			logger.info(jobName + " criado e agendado.");
//
//		} catch (SchedulerException e) {
//			logger.error("Erro ao criar agendamento de job de [" + jobName + "].", e);
//		} 
//	}	

	// *******************************************************************************
	// * Código responsável por inicializar o Quartz. 
	// *******************************************************************************
	public void init(ServletConfig cfg) throws javax.servlet.ServletException {

		super.init(cfg);

		// Se já existe o scheduller, não precisamos faezr mais nada.
		if (quartzScheduler != null)
			return;

		if(ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("habilitar_agendador").equals("0")) {
			logger.info("Agendador DESABILITADO");
			return;
		}
		
		logger.info("Quartz Initializer Servlet loaded, initializing Scheduler...");
		StdSchedulerFactory schedulerFactory;

		try {

			String configFile = cfg.getInitParameter("config-file");
			String shutdownPref = cfg.getInitParameter("shutdown-on-unload");

			if (shutdownPref != null) {
				performShutdown = Boolean.valueOf(shutdownPref).booleanValue();
			}

			// get Properties
			if (configFile != null) {
				schedulerFactory = new StdSchedulerFactory(configFile);
			} else {
				schedulerFactory = new StdSchedulerFactory();
			}

			try {

				// Tenta obter um Scheduler, baseado no arquivo quartz.properties
				// Neste caso, utiliza armazenamento em Jdbc (banco)
				quartzScheduler = schedulerFactory.getScheduler();

			} catch (SchedulerConfigException cnfe) {

				Throwable causa = cnfe.getCause();


				if (causa != null)
				{
					// Ocorreu um erro de configuração do quartz.
					logger.error("Erro de configuração do Quartz.", cnfe);

					// Se o erro foi causado por erros no banco, tenta limpar as tabelas.
					if (causa instanceof JobPersistenceException || causa instanceof SQLException) {
						quartzScheduler = schedulerFactory.getScheduler();
					}
				}
			}

			// Should the Scheduler being started now or later
			String startOnLoad = cfg
			.getInitParameter("start-scheduler-on-load");

			int startDelay = 0;
			String startDelayS = cfg.getInitParameter("start-delay-seconds");
			try {
				if(startDelayS != null && startDelayS.trim().length() > 0)
					startDelay = Integer.parseInt(startDelayS);
			} catch(Exception e) {
				logger.error("Cannot parse value of 'start-delay-seconds' to an integer: " + startDelayS + ", defaulting to 5 seconds.", e);
				startDelay = 5;
			}

			/*
			 * If the "start-scheduler-on-load" init-parameter is not specified,
			 * the scheduler will be started. This is to maintain backwards
			 * compatability.
			 */
			if (startOnLoad == null || (Boolean.valueOf(startOnLoad).booleanValue())) {
				if(startDelay <= 0) {
					// Start now
					quartzScheduler.start();
					logger.info("Scheduler has been started...");
				}
				else {
					// Start delayed
					quartzScheduler.startDelayed(startDelay);
					logger.info("Scheduler will start in " + startDelay + " seconds.");
				}
			} else {
				logger.info("Scheduler has not been started. Use scheduler.start()");
			}

			String factoryKey = cfg.getInitParameter("servlet-context-factory-key");
			if (factoryKey == null) {
				factoryKey = QUARTZ_FACTORY_KEY;
			}

			logger.info("Storing the Quartz Scheduler Factory in the servlet context at key: "
					+ factoryKey);

			cfg.getServletContext().setAttribute(factoryKey, schedulerFactory);

			verificarJobsGTW();

		} catch (Exception e) {
			logger.fatal("Quartz Scheduler failed to initialize: " + e.toString());
			throw new ServletException(e);
		}
	}

	// *******************************************************************************
	// * Código responsável por desligar o Quartz. 
	// *******************************************************************************
	@Override
	public void destroy() {

		synchronized (QUARTZ_FACTORY_KEY) {

			if (!performShutdown)
				return;

			try {
				if (quartzScheduler != null) {
					quartzScheduler.shutdown();
					quartzScheduler = null;
				}
			} catch (SchedulerException e) {
				logger.error("Quartz Scheduler failed to shutdown cleanly: " + e.toString());
			}

			logger.info("Quartz Scheduler successful shutdown.");
		}
	}

	private static void limparAgendador() throws SchedulerException {
		
		Map<String, List<String>> jobsExistentes = new HashMap<String, List<String>>();

		if (quartzScheduler != null) {
			List<String> jobsDoGrupo;

			for (String nomeGrupo : quartzScheduler.getJobGroupNames()) {
				jobsDoGrupo = new ArrayList<String>();
				for(JobKey jobGrupo : quartzScheduler.getJobKeys(GroupMatcher.jobGroupEquals(nomeGrupo)))
					jobsDoGrupo.add(jobGrupo.getName());
				jobsExistentes.put(nomeGrupo, jobsDoGrupo);
			}

			for (Entry<String, List<String>> grupo : jobsExistentes.entrySet()) {
				for (String job : grupo.getValue()) {
					quartzScheduler.deleteJob(new JobKey(job, grupo.getKey()));
				}
			}
		}
	}

	// *******************************************************************************
	// * Redireciona para uma página de erro "proibido"
	// *******************************************************************************
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	// *******************************************************************************
	// * Redireciona para uma página de erro "proibido"
	// *******************************************************************************

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	private static void reajustaJobSha256Imagem() throws SchedulerException {
		if (!possuiJob(GRUPO_INTEGRIDADE, JOB_SHA256_IMAGEM)) {
			JobDetail tarefa = JobBuilder.newJob(muralha.digital.integridade.JobSha256Imagem.class)
					.withIdentity(JOB_SHA256_IMAGEM, GRUPO_INTEGRIDADE)
					.build();
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(JOB_SHA256_IMAGEM, GRUPO_INTEGRIDADE)
					.startNow()
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(5))
					.build();
			quartzScheduler.scheduleJob(tarefa, trigger);
			logger.info("Job SHA-256 imagem agendada a cada 5 minutos.");
		}
	}

	private static void reajustaJobSlaLatencia() throws SchedulerException {
		if (!possuiJob(GRUPO_MONITORAMENTO, JOB_SLA_LATENCIA)) {
			JobDetail tarefa = JobBuilder.newJob(muralha.digital.sla.SlaLatenciaJob.class)
					.withIdentity(JOB_SLA_LATENCIA, GRUPO_MONITORAMENTO)
					.build();
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(JOB_SLA_LATENCIA, GRUPO_MONITORAMENTO)
					.startNow()
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(5))
					.build();
			quartzScheduler.scheduleJob(tarefa, trigger);
			logger.info("Job SLA Latência agendada a cada 5 minutos.");
		}
	}

	private static void reajustaJobSlaPreprocessamento() throws SchedulerException {
		if (!possuiJob(GRUPO_MONITORAMENTO, JOB_SLA_PREPROC)) {
			JobDetail tarefa = JobBuilder.newJob(muralha.digital.sla.SlaPreprocessamentoJob.class)
					.withIdentity(JOB_SLA_PREPROC, GRUPO_MONITORAMENTO)
					.build();
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(JOB_SLA_PREPROC, GRUPO_MONITORAMENTO)
					.startNow()
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(60))
					.build();
			quartzScheduler.scheduleJob(tarefa, trigger);
			logger.info("Job SLA Pré-processamento agendada a cada 60 minutos.");
		}
	}

}
