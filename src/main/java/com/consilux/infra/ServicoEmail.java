package com.consilux.infra;
import java.sql.SQLException;
import java.util.Date;
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

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.EmailEnviar;
import com.consilux.model.beans.AnexoEmailBean;
import com.consilux.model.beans.EmailEnviarBean;
import com.consilux.model.exception.ModelException;

/**
 * Classe de serviço, que representa um servidor SMTP.
 * Utilizado para enviar emails.
 * @author raoni
 */
public class ServicoEmail {

	private String smtpUser;
	private String smtpPassword;
	private String smtpHost;
	
	private static final String ENCODING_EMAIL = "UTF-8";
	
	public ServicoEmail(String smtpUser, String smtpPassword, String smtpHost) {
		this.smtpUser = smtpUser;
		this.smtpPassword = smtpPassword;
		this.smtpHost = smtpHost;
	}

	public void enviarEmail(EmailEnviarBean email)
	throws ModelException, MessagingException, ConexaoException, SQLException {
		
		if (email == null)
			throw new ModelException("Argumento nulo: email");
	
		if (email.getCorpo() == null)
			throw new ModelException("Argumento inválido: email sem corpo");

		if (email.getDestinatarios() == null || email.getDestinatarios().size() == 0)
			throw new ModelException("Argumento inválido: email sem destinatários");		
		
		// Smtp de destino
		Properties props = new Properties();
		
		props.put("mail.smtp.host", smtpHost);
		
		Session session;
		
		// Decide se vai ser necessário autenticação (ou não).
		if (smtpUser != null && smtpUser.length() > 0)
		{
			Authenticator auth = new SMTPAuthenticator();
			props.put("mail.smtp.auth", "true");
			session = Session.getDefaultInstance(props, auth);
		} else {
			session = Session.getDefaultInstance(props);
		}
		
		// Cria a mensagem
		final MimeMessage msg = new MimeMessage(session);
		msg.setHeader("Content-Type", "text/plain; charset=" + ENCODING_EMAIL);
		
		// Define o "from"
		msg.setFrom(email.getRemetente());
			
		// Define o "to" como cópias-ocultas
		msg.addRecipients(Message.RecipientType.BCC, email.getDestinatarios().toArray(new InternetAddress[0]));

		// Define o assunto da mensagem.
		msg.setSubject(email.getAssunto(), ENCODING_EMAIL);
		
		// Cria um multipart, para conter o corpo e anexo
		MimeMultipart multipart = new MimeMultipart();

		// Cria o corpo da mensagem
		MimeBodyPart corpoMensagem = new MimeBodyPart();
		corpoMensagem.setText(email.getCorpo(), ENCODING_EMAIL);

		// Adiciona o corpo no multipart
		multipart.addBodyPart(corpoMensagem);

		if (email.getAnexos() != null && email.getAnexos().size() > 0) {
		for (AnexoEmailBean arquivoAnexo : email.getAnexos()) {
	
				// Cria o anexo da mensagem.
				MimeBodyPart anexoMensagem = new MimeBodyPart();
				DataSource dataSource = new ByteArrayDataSource(
						arquivoAnexo.getBytesArquivo(), arquivoAnexo.getContentType());
				anexoMensagem.setDataHandler(new DataHandler(dataSource));
				anexoMensagem.setFileName(arquivoAnexo.getNomeArquivo());
	
				// Adiciona o anexo no multipart
				multipart.addBodyPart(anexoMensagem);
			}
		}
		// Define o conteúdo da mensagem (que é multipart: corpo + anexo);
		msg.setContent(multipart);

		msg.setSentDate(new Date());
		
		// Manda o email. Pode lançar uma MessagingException
		Transport.send(msg);
		
		// Marca o email como enviado.
		EmailEnviar.marcarComoEnviado(email);
		
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
			return new PasswordAuthentication(ServicoEmail.this.smtpUser,
					ServicoEmail.this.smtpPassword);
		}
	}	
	
}
