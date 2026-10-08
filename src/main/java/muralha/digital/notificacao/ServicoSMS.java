package muralha.digital.notificacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.MessagingException;

import org.apache.commons.lang3.Range;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.notify.v1.service.Notification;
import com.twilio.type.PhoneNumber;

import br.com.facilitamovel.bean.Retorno;
import br.com.facilitamovel.bean.SmsMultiplo;
import br.com.facilitamovel.service.SendMessage;
import muralha.digital._ini.Inicializacao;
import muralha.digital.ocorrencia.Ocorrencias;

public class ServicoSMS {

	private static Logger logger = Logger.getLogger(ServicoSMS.class);

	// Twilio
	private static final String ACCOUNT_SID = Inicializacao.AccountSID;
	private static final String AUTH_TOKEN = Inicializacao.AuthToken;
	private static final String MESSAGING_SERVICE_SID = Inicializacao.MessagingServiceSID;
	private static final String NOTIFY_SERVICE_SID = Inicializacao.NotifyServiceSID;

	// SMSDEV
	private static final String SMSDEV_KEY = Inicializacao.SmsDevKey;

	// Facilita Móvel
	private static final String FACILITA_MOVEL_USER = Inicializacao.FacilitaMovelUser; // System.getenv("FACILITA_MOVEL_USER")
	private static final String FACILITA_MOVEL_PASSWORD = Inicializacao.FacilitaMovelPassword; // System.getenv("FACILITA_MOVEL_PASSWORD")

	// Comtele
	private static final String COMTELE_ENDPOINT = Inicializacao.ComteleEndpoint;
	private static final String COMTELE_AUTH_KEY = Inicializacao.ComteleAuthKey;

	/******************************************************************************************************************************************************************************************/
	// Twilio
	public void EnviarSMS_Twilio(Notificacao sms, List<String> listaTelefoneEnviar, boolean enviarSMS)
			throws ModelException, MessagingException, ConexaoException, SQLException {
		try {
			if (ACCOUNT_SID == null || AUTH_TOKEN == null || MESSAGING_SERVICE_SID == null
					|| NOTIFY_SERVICE_SID == null) {
				throw new ModelException(
						"Credenciais de integração SMS não encontradas. Favor configurar corretamente!");
			}

			String msg = Notificacoes.ObterMsgSMS(sms);

			if (sms == null)
				throw new ModelException("Argumento nulo: SMS");

			if (msg == null || msg.trim().equals(""))
				throw new ModelException("Argumento inválido: SMS sem corpo");

			if (sms.getDestinatariosSMS() == null || sms.getDestinatariosSMS().size() == 0)
				throw new ModelException("Argumento inválido: SMS sem destinatários");

			if (enviarSMS) {
				String bindingFormat = "{\"binding_type\": \"sms\", \"address\": \"+55%s\"}";
				String numeros = listaTelefoneEnviar.stream().collect(Collectors.joining(","));

				List<String> bindings = Stream.of(
						numeros)
						.map(phoneNumber -> String.format(bindingFormat, phoneNumber))
						.collect(Collectors.toList());

				Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
				Notification notification = Notification.creator(NOTIFY_SERVICE_SID)
						.setToBinding(bindings)
						.setBody(msg)
						.create();

				logger.debug("SMS: " + notification.getSid());
			}

			// Marca a ocorrência como enviado.
			// Notificacoes.AtualizarNotificacao(sms.getIdTipoRegistro(), sms.getId(),
			// StatusNotificacao.Status.ENVIADO.GetID());

		} catch (Exception e) {
			logger.error("Erro ao enviar SMS de notificação!", e);
			// Notificacoes.AtualizarNotificacao(sms.getIdTipoRegistro(), sms.getId(),
			// StatusNotificacao.Status.NAO_ENVIADO.GetID());
		}
	}

	// Twilio
	public void EnviarSMS_SingleNumber_Twilio() {
		try {
			if (ACCOUNT_SID == null || AUTH_TOKEN == null || MESSAGING_SERVICE_SID == null
					|| NOTIFY_SERVICE_SID == null) {
				throw new ModelException(
						"Credenciais de integração SMS não encontradas. Favor configurar corretamente!");
			}

			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			Message message = Message.creator(
					new PhoneNumber("+5541988291902"),
					MESSAGING_SERVICE_SID,
					"Consilux Tecnologia - Mensagem de teste!")
					.create();

			logger.info("SMS enviado! " + message.getSid());
		} catch (Exception e) {
			logger.error("Erro ao enviar EnviarSMS_SingleNumber_Twilio!", e);
		}
	}

	public boolean EnviarSMS_RecuperacaoSenha_Twilio(String telefone, String codigoRecuperacao) {
		String telefoneTeste = "4199999999"; // trocar quando estiver em produção 
		String accountSid = System.getenv("TWILIO_TEST_ACCOUNT_SID"); // trocar quando estiver em produção
		String authToken = System.getenv("TWILIO_TEST_AUTH_TOKEN"); // trocar quando estiver em produção
		
		try {
			if (ACCOUNT_SID == null || AUTH_TOKEN == null) {
				throw new ModelException(
						"Credenciais de integração SMS não encontradas. Favor configurar corretamente!");
			}

			Twilio.init(accountSid, authToken);

			Message message = Message.creator(
					new PhoneNumber("+55" + telefoneTeste),
					new PhoneNumber("+12548560712"),
					"Consilux Tecnologia - Código de recuperação: " + codigoRecuperacao).create();

			logger.info("SMS enviado! SID: " + message.getSid());
			return true;
		} catch (Exception e) {
			logger.error("Erro ao enviar EnviarSMS_RecuperacaoSenha_Twilio!", e);
			return false;
		}
	}

	// Twilio
	public void EnviarSMS_MultiNumbers_Twilio() {
		try {
			if (ACCOUNT_SID == null || AUTH_TOKEN == null || MESSAGING_SERVICE_SID == null
					|| NOTIFY_SERVICE_SID == null) {
				throw new ModelException(
						"Credenciais de integração SMS não encontradas. Favor configurar corretamente!");
			}

			String bindingFormat = "{\"binding_type\": \"sms\", \"address\": \"%s\"}";

			List<String> bindings = Stream.of(
					"+5541988291902",
					"+5541988291902")
					.map(phoneNumber -> String.format(bindingFormat, phoneNumber))
					.collect(Collectors.toList());

			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			Notification notification = Notification.creator(NOTIFY_SERVICE_SID)
					.setToBinding(bindings)
					.setBody("Consilux Tecnologia - Mensagem de teste - Múltiplos números!")
					.create();

			logger.info("SMS enviado! " + notification.getSid());
		} catch (Exception e) {
			logger.error("Erro ao enviar EnviarSMS_MultiNumbers_Twilio!", e);
		}
	}

	/******************************************************************************************************************************************************************************************/

	/******************************************************************************************************************************************************************************************/
	// SMSDEV
	public void EnviarSMS_SMSDEV() {
		try {
			if (SMSDEV_KEY == null) {
				throw new ModelException(
						"Credenciais de integração SMS não encontradas. Favor configurar corretamente!");
			}

			String urlBase = "https://api.smsdev.com.br/v1/send?";

			String msg = "Consilux Tecnologia - Mensagem de teste";
			// msg = "Teste";

			List<String> numeros = new ArrayList<String>();
			numeros.add("41988291902");
			numeros.add("41988291902");

			String listaNumeros = "", numero = "&number=41988291902";
			int cnt = 1;
			for (String num : numeros) {
				listaNumeros = listaNumeros + "&number" + String.valueOf(cnt) + "=" + num;
				cnt++;
			}

			String url = urlBase + "key=" + SMSDEV_KEY + "&type=9" + numero + "&msg="
					+ URLEncoder.encode(msg, StandardCharsets.UTF_8);
			// String url = urlBase + "key=" + SMSDEV_KEY + "&type=9" + listaNumeros +
			// "&msg=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);

			HttpResponse<String> response = Unirest.post(url).asString();

			logger.info("SMS enviado! " + response.getStatusText());
		} catch (Exception e) {
			logger.error("Erro ao enviar SMS!", e);
		}
	}

	/******************************************************************************************************************************************************************************************/

	/******************************************************************************************************************************************************************************************/
	// Facilita Móvel
	public void EnviarSMS_FacilitaMovel(Notificacao sms, List<String> listaTelefoneEnviar, boolean enviarSMS)
			throws ModelException, MessagingException, ConexaoException, SQLException {
		try {
			boolean smsEnviado = false;

			if (FACILITA_MOVEL_USER == null || FACILITA_MOVEL_PASSWORD == null) {
				throw new ModelException(
						"Credenciais de integração SMS não encontradas. Favor configurar corretamente!");
			}

			String msg = Notificacoes.ObterMsgSMS(sms);

			if (sms == null)
				throw new ModelException("Argumento nulo: SMS");

			if (msg == null || msg.trim().equals(""))
				throw new ModelException("Argumento inválido: SMS sem corpo");

			if (sms.getDestinatariosSMS() == null || sms.getDestinatariosSMS().size() == 0)
				throw new ModelException("Argumento inválido: SMS sem destinatários");

			if (enviarSMS) {
				// Multiple Send
				SmsMultiplo smsEnviar = new SmsMultiplo();
				smsEnviar.setUser(FACILITA_MOVEL_USER);
				smsEnviar.setPassword(FACILITA_MOVEL_PASSWORD);

				// Multiplos destinatarios
				smsEnviar.setDestinatarios(listaTelefoneEnviar);

				smsEnviar.setMessage(msg);
				smsEnviar.setFlashsms(0); // 1 - flashsms | nulo ou zero, sms normal

				Retorno retorno = SendMessage.multipleSend(smsEnviar);

				Range<Integer> codigosSucesso = Range.between(5, 7);
				if (codigosSucesso.contains(retorno.getCodigo())) {
					smsEnviado = true;
					logger.debug("SMS enviado! Código: " + retorno.getCodigo() + " | Descrição: "
							+ retorno.getMensagem().trim());
				} else
					logger.error("Falha ao enviar SMS! Código: " + retorno.getCodigo() + " | Descrição: "
							+ retorno.getMensagem().trim());

				/*
				 * DOCUMENTAÇÃO FACILITA MÓVEL
				 * Respota HTTP Descrição da Resposta
				 * 1 - Login inválido Usuário ou Senha enviados na URL estão inválidos, ou a
				 * conta pode estar inativa/cancelada.
				 * 2 - Usuário sem Créditos Usuário não possúi créditos na plataforma
				 * 3 - Celulares Inválidos Todos envios foram invalidados, todos celulares são
				 * inválidos
				 * 4 - Campo Mensagem Inválida A mensagem passada está vazia, ou possui
				 * características de uma mensagem inválida
				 * 5 - Mensagem Agendada Caso você envie os parâmetros de agendamento, a
				 * mensagem será agendada
				 * 6 - Mensagem enviada Mensagem enviada imediatamente para a operadora.
				 * 7 - Enviada com advertências Algumas mensagens foram enviadas, mas alguns
				 * números estão com problemas
				 */
			}

			// Marca a notificação como enviada.
			UUID idStatusNotificacao = (smsEnviado ? StatusNotificacao.Status.ENVIADO.GetID()
					: StatusNotificacao.Status.NAO_ENVIADO.GetID());
			Notificacoes.AtualizarNotificacao(sms.getIdTipoRegistro(), sms.getId(), idStatusNotificacao);

			// Marca a ocorrência como enviada.
			if (smsEnviado)
				Ocorrencias.AtualizarStatus(sms.getIdOcorrencia());

		} catch (Exception e) {
			logger.error("Erro ao enviar SMS EnviarSMS_FacilitaMovel!", e);
			Notificacoes.AtualizarNotificacao(sms.getIdTipoRegistro(), sms.getId(),
					StatusNotificacao.Status.NAO_ENVIADO.GetID());
		}
	}

	/******************************************************************************************************************************************************************************************/

	/******************************************************************************************************************************************************************************************/
	// Comtele
	public void EnviarSMS_Comtele(Notificacao sms, List<String> listaTelefoneEnviar, boolean enviarSMS)
			throws ModelException, MessagingException, ConexaoException, SQLException {
		try {
			if (COMTELE_AUTH_KEY == null || COMTELE_ENDPOINT == null) {
				throw new ModelException(
						"Credenciais de integração SMS não encontradas. Favor configurar corretamente!");
			}

			String msg = Notificacoes.ObterMsgSMS(sms);

			if (sms == null)
				throw new ModelException("Argumento nulo: SMS");

			if (msg == null || msg.trim().equals(""))
				throw new ModelException("Argumento inválido: SMS sem corpo");

			if (sms.getDestinatariosSMS() == null || sms.getDestinatariosSMS().size() == 0)
				throw new ModelException("Argumento inválido: SMS sem destinatários");

			if (enviarSMS) {
				URL url = null;
				HttpURLConnection conn = null;
				OutputStream os = null;
				BufferedReader br = null;

				try {
					url = new URL(COMTELE_ENDPOINT);
					conn = (HttpURLConnection) url.openConnection();
					conn.setDoOutput(true);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Accept", "application/json");
					conn.setRequestProperty("Content-Type", "application/json");
					conn.setRequestProperty("auth-key", COMTELE_AUTH_KEY);

					String numeros = listaTelefoneEnviar.stream().collect(Collectors.joining(","));

					String data = "{\"Receivers\": \"" + numeros + "\", \"Content\": \"" + msg + "\"}";

					os = conn.getOutputStream();
					os.write(data.getBytes());
					os.flush();

					logger.debug(conn.getResponseCode() + " " + conn.getResponseMessage());

					if (conn.getResponseCode() == 200) {
						br = new BufferedReader(new InputStreamReader((conn.getInputStream()))); // Getting the response
																									// from the
																									// webservice

						String output;
						logger.debug("Output from Server ....");
						StringBuilder aux = new StringBuilder();
						while ((output = br.readLine()) != null) {
							aux.append(output);
						}

						logger.debug(aux.toString());

						JsonObject jobj = new Gson().fromJson(aux.toString(), JsonObject.class);
						logger.debug(jobj);
					}
				} catch (Exception e) {
					logger.error("Erro ao enviar SMS EnviarSMS_Comtele!", e);
				} finally {
					try {
						if (url != null)
							url = null;

						if (conn != null)
							conn.disconnect();

						if (os != null)
							os.close();

						if (br != null)
							br.close();
					} catch (IOException e) {
						logger.error("Erro ao destruir objetos!", e);
					}
				}
			}

			// Marca a notificação como enviada.
			// Notificacoes.AtualizarNotificacao(sms.getIdTipoRegistro(), sms.getId(),
			// StatusNotificacao.Status.ENVIADO.GetID());

		} catch (Exception e) {
			logger.error("Erro ao enviar SMS EnviarSMS_Comtele!", e);
			// Notificacoes.AtualizarNotificacao(sms.getIdTipoRegistro(), sms.getId(),
			// StatusNotificacao.Status.NAO_ENVIADO.GetID());
		}
	}

	/******************************************************************************************************************************************************************************************/

	public void EnviaCodigoDatabase(String telefone, String codigo) {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO muralha.codigo_verificacao ");
			sb.append("(telefone, codigo, data_geracao) ");
			sb.append("VALUES (?, ?, CURRENT_TIMESTAMP)");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, telefone);
			ps.setString(2, codigo);
			int linhasAfetadas = ps.executeUpdate();

			if (linhasAfetadas > 0) {
				logger.info("Código " + codigo + " enviado e salvo para o telefone: " + telefone);
			} else {
				logger.warn("Nenhuma linha foi afetada ao salvar código para telefone: " + telefone);
			}

		} catch (Exception e) {
			logger.error("Erro ao salvar código de verificação no banco!", e);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos (EnviaCodigoDatabase): " + e.getMessage(), e);
			}
		}
	}
	
	public boolean enviaSmsFacilitaMovel(String token, List<String> destinatarios, String baseUrl) throws ModelException {
		boolean smsEnviado = false;
		try {
		    String FACILITA_MOVEL_HASH = "ZZWZ6FQPNBX6VWKPBGLGFKEZTXH2ZA6TPAGQVVLOVGRMRH3OORRSZIYUGY8";
		    String FACILITA_MOVEL_PASSWORD = "562318";
		    String FACILITA_MOVEL_USER_TEST = "antony1992";

			if (FACILITA_MOVEL_USER_TEST == null || FACILITA_MOVEL_HASH == null) {
				throw new ModelException(
						"Credenciais de integração SMS não encontradas. Favor configurar corretamente!");
			}
			
			String msg = "Acesse o link para a recuperacao de senha" + baseUrl + "/login/recuperar_senha.jsp?token=" + token;

				SmsMultiplo smsEnviar = new SmsMultiplo();
				smsEnviar.setUser(FACILITA_MOVEL_USER_TEST);
				smsEnviar.setPassword(FACILITA_MOVEL_PASSWORD);
				
				smsEnviar.setDestinatarios(destinatarios);

				smsEnviar.setMessage(msg);
				smsEnviar.setFlashsms(0); 

				Retorno retorno = SendMessage.multipleSend(smsEnviar);

				Range<Integer> codigosSucesso = Range.between(5, 7);
				if (codigosSucesso.contains(retorno.getCodigo())) {
					smsEnviado = true;
					logger.debug("SMS enviado! Código: " + retorno.getCodigo() + " | Descrição: "
							+ retorno.getMensagem().trim());
				} else {
					smsEnviado = false;
					logger.error("Falha ao enviar SMS! Código: " + retorno.getCodigo() + " | Descrição: "
							+ retorno.getMensagem().trim());
				}
		} catch (Exception e) {
			logger.error("Erro ao enviar SMS EnviarSMS_FacilitaMovel!", e);
			smsEnviado = false;
		}
		return smsEnviado;
	}



}
