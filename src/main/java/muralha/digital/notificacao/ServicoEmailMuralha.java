package muralha.digital.notificacao;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.consilux.lib.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;

import muralha.digital.ocorrencia.Ocorrencias;
import muralha.digital.veiculo.imagem.VeiculoImagem;

/**
 * Classe de serviço, que representa um servidor SMTP.
 * Utilizado para enviar emails.
 * @author thiago.surgik
 */
public class ServicoEmailMuralha {

	private static Logger logger = Logger.getLogger(ServicoEmailMuralha.class);

	// Chave lida da variavel de ambiente SENDGRID_API_KEY
	private static final String SENDGRID_API_KEY = System.getenv("SENDGRID_API_KEY");

	private String smtpUser;
	private String smtpPassword;
	private String smtpHost;
	private Integer smtpPort;

	private static final String ENCODING_EMAIL = "UTF-8";

	public ServicoEmailMuralha(String smtpUser, String smtpPassword, String smtpHost, Integer smtpPort)
	{
		this.smtpUser = smtpUser;
		this.smtpPassword = smtpPassword;
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
	}

	public void enviarEmail(Notificacao email, List<InternetAddress> listaEmailEnviar, boolean enviarEmail) throws ModelException, MessagingException, ConexaoException, SQLException
	{
		String assunto = Notificacoes.ObterAssuntoEmail(email);
		String corpo = Notificacoes.ObterCorpoEmail(email);

		try
		{
			if (email == null)
				throw new ModelException("Argumento nulo: email");

			if (corpo == null || corpo.trim().equals(""))
				throw new ModelException("Argumento inválido: email sem corpo");

			if (email.getDestinatarios() == null || email.getDestinatarios().size() == 0)
				throw new ModelException("Argumento inválido: email sem destinatários");

			if (enviarEmail)
			{
				// Smtp de destino
				Properties props = new Properties();

				props.put("mail.smtp.host", smtpHost);
//				props.put("mail.smtp.port", "587");
				props.put("mail.smtp.port", smtpPort);

				Session session;

				// Decide se vai ser necessário autenticação (ou não).
				if (smtpUser != null && smtpUser.length() > 0)
				{
					Authenticator auth = new SMTPAuthenticator();
					props.put("mail.smtp.auth", "true");
					props.put("mail.smtp.starttls.enable", "true");
					session = Session.getDefaultInstance(props, auth);
				}
				else
				{
					session = Session.getDefaultInstance(props);
				}

				// Cria a mensagem
				final MimeMessage msg = new MimeMessage(session);
				msg.setHeader("Content-Type", "text/html; charset=" + ENCODING_EMAIL);

				// Define o "from"
				msg.setFrom(email.getRemetente());

				// Define o "to" como cópias-ocultas
				msg.addRecipients(Message.RecipientType.BCC, listaEmailEnviar.toArray(new InternetAddress[0]));

				// Define o assunto da mensagem.
				msg.setSubject(assunto, ENCODING_EMAIL);

				// Cria um multipart, para conter o corpo e anexo
				MimeMultipart multipart = new MimeMultipart();

				// Cria o corpo da mensagem
				MimeBodyPart corpoMensagem = new MimeBodyPart();
				corpoMensagem.setContent(corpo, TipoMime.HTML.getTipo());

				// Adiciona o corpo no multipart
				multipart.addBodyPart(corpoMensagem);

				corpoMensagem = new MimeBodyPart();
				if (email.getImagens() != null && email.getImagens().size() > 0)
				{
					for (VeiculoImagem arquivoAnexo : email.getImagens())
					{
						// Cria o anexo da mensagem.
						MimeBodyPart anexoMensagem = new MimeBodyPart();
						DataSource dataSource = new ByteArrayDataSource(arquivoAnexo.getImagem(), TipoMime.JPG.getTipo());
						anexoMensagem.setDataHandler(new DataHandler(dataSource));
						anexoMensagem.setFileName(String.format("imagem_%1d.jpg", arquivoAnexo.getNumImagem()));

						// Adiciona o anexo no multipart
						multipart.addBodyPart(anexoMensagem);
					}
				}

				// Define o conteúdo da mensagem (que é multipart: corpo + anexo);
				msg.setContent(multipart);

				msg.setSentDate(new Date());

				// Manda o email. Pode lançar uma MessagingException
				Transport.send(msg);
			}

			// Marca a notificação como enviada.
			Notificacoes.AtualizarNotificacao(email.getIdTipoRegistro(), email.getId(), StatusNotificacao.Status.ENVIADO.GetID());

			// Marca a ocorrência como enviada.
			Ocorrencias.AtualizarStatus(email.getIdOcorrencia());

		}
		catch (Exception e)
		{
			logger.error("Erro ao enviar email de notificação!", e);
			// Marca a ocorrência como não enviada.
			Notificacoes.AtualizarNotificacao(email.getIdTipoRegistro(), email.getId(), StatusNotificacao.Status.NAO_ENVIADO.GetID());
		}

	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator
	{
		public PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication(ServicoEmailMuralha.this.smtpUser,
					ServicoEmailMuralha.this.smtpPassword);
		}
	}
	
	public boolean enviarEmailRecuperacaodeSenha(InternetAddress remetente, List<InternetAddress> destinatarios, String token, String baseUrl) {
	    try {
	        Properties props = new Properties();
	        props.put("mail.smtp.host", smtpHost);
	        props.put("mail.smtp.port", String.valueOf(smtpPort));
	        props.put("mail.smtp.starttls.enable", "true");

	        Session session;
	        if (smtpUser != null && !smtpUser.isEmpty()) {
	            props.put("mail.smtp.auth", "true");

	            Authenticator auth = new Authenticator() {
	                @Override
	                protected PasswordAuthentication getPasswordAuthentication() {
	                    return new PasswordAuthentication(smtpUser, smtpPassword);
	                }
	            };
	            session = Session.getInstance(props, auth);
	        } else {
	            session = Session.getInstance(props);
	        }

	        MimeMessage msg = new MimeMessage(session);
	        msg.setHeader("Content-Type", "text/html; charset=UTF-8");
	        msg.setFrom(remetente);
	        msg.addRecipients(Message.RecipientType.TO, destinatarios.toArray(new InternetAddress[0]));
	        msg.setSubject("Recuperação de senha", "UTF-8");

	        String corpoHtml = "Acesse o link para a recuperação de senha: "
	                + "<a href='" + baseUrl + "/login/recuperar_senha.jsp?token=" + token + "'>Clique aqui</a>";

	        msg.setContent(corpoHtml, "text/html; charset=UTF-8");
	        msg.setSentDate(new Date());

	        Transport.send(msg);
	        return true; 

	    } catch (Exception e) {
	        logger.error("Erro ao enviar e-mail: ", e);
	        return false; 
	    }
	}
	
	public boolean enviarEmailNotificacaoUsuarioResponsavel(InternetAddress remetente, List<InternetAddress> destinatarios,String placa, String tipo, String usuario) {
	    try {
	        Properties props = new Properties();
	        props.put("mail.smtp.host", smtpHost);
	        props.put("mail.smtp.port", String.valueOf(smtpPort));
	        props.put("mail.smtp.starttls.enable", "true");

	        Session session;
	        if (smtpUser != null && !smtpUser.isEmpty()) {
	            props.put("mail.smtp.auth", "true");

	            Authenticator auth = new Authenticator() {
	                @Override
	                protected PasswordAuthentication getPasswordAuthentication() {
	                    return new PasswordAuthentication(smtpUser, smtpPassword);
	                }
	            };
	            session = Session.getInstance(props, auth);
	        } else {
	            session = Session.getInstance(props);
	        }

	        MimeMessage msg = new MimeMessage(session);
	        msg.setHeader("Content-Type", "text/html; charset=UTF-8");
	        msg.setFrom(remetente);
	        msg.addRecipients(Message.RecipientType.TO, destinatarios.toArray(new InternetAddress[0]));
	        msg.setSubject("Anel de Segurança - Usuário responsavel - Alertas", "UTF-8");

	        String corpoHtml = "<div style='font-family: Arial, sans-serif; font-size:14px; color:#333;'>"
				        	    + "<h2 style='color:#2E86C1;'>Aviso a todos os usuários</h2>"
				        	    + "<p>Os alertas para o monitoramento da placa "
				        	    + "<span style='font-weight:bold; color:#27AE60;'>" + placa + "</span> "
				        	    + "(<span style='color:#C0392B;'>" + tipo + "</span>) "
				        	    + "estão sob responsabilidade do usuário: "
				        	    + "<strong>" + usuario + "</strong></p>"
				        	    + "<hr style='margin-top:20px; border:0; border-top:1px solid #ccc;'/>"
				        	    + "<p style='font-size:12px; color:#777;'>Mensagem automática - não responda este e-mail.</p>"
				        	    + "</div>";
				                

	        msg.setContent(corpoHtml, "text/html; charset=UTF-8");
	        msg.setSentDate(new Date());

	        Transport.send(msg);
	        return true; 

	    } catch (Exception e) {
	        logger.error("Erro ao enviar e-mail: ", e);
	        return false; 
	    }
	}

	public static boolean atualizarSenhaUsuario(Integer idUsuario, String senha) {
		String hashMD5 = gerarHashMD5(senha);

		String sql = "UPDATE sis_usuario SET senha = ? WHERE id_usuario = ?";

		try (Connection conn = Conexao.getConexao();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, hashMD5);
			ps.setInt(2, idUsuario);

			int rowsAffected = ps.executeUpdate();

			return rowsAffected > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String gerarHashMD5(String texto) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hashBytes = md.digest(texto.getBytes("UTF-8"));

			// Converte para String hexadecimal
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();

		} catch (Exception e) {
			throw new RuntimeException("Erro ao gerar hash MD5", e);
		}
	}

}
