package muralha.digital.chamado;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;

/** Confirmacao de abertura e aviso de reclassificacao (spec §1.4, D14). */
public final class EmailChamado {

    public static final String CONFIRMACAO_ABERTURA = "CONFIRMACAO_ABERTURA";
    public static final String RECLASSIFICACAO = "RECLASSIFICACAO";
    public static final int MAX_TENTATIVAS = 5;
    private static final String TIMEOUT_MS = "10000";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private EmailChamado() {
    }

    public static String assunto(String tipo, String protocolo) {
        return RECLASSIFICACAO.equals(tipo)
                ? "[Chamado " + protocolo + "] Prioridade reclassificada"
                : "[Chamado " + protocolo + "] Abertura confirmada";
    }

    public static String corpoHtml(String tipo, String protocolo, String titulo, String prioridade,
                                   LocalDateTime prazoAtendimento, LocalDateTime prazoResolucao,
                                   String justificativa) {
        boolean reclass = RECLASSIFICACAO.equals(tipo);
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family:Arial,sans-serif;font-size:14px;color:#333'>")
          .append("<h2 style='color:#0d75bf'>")
          .append(reclass ? "Prioridade do chamado reclassificada" : "Chamado registrado com sucesso")
          .append("</h2>")
          .append("<table style='border-collapse:collapse'>")
          .append(linha("Protocolo", "<strong>" + esc(protocolo) + "</strong>"))
          .append(linha("Título", esc(titulo)))
          .append(linha(reclass ? "Nova prioridade" : "Prioridade", esc(prioridade)))
          .append(linha("Prazo previsto para atendimento", prazoAtendimento.format(FMT)))
          .append(linha("Prazo previsto para resolução", prazoResolucao.format(FMT)));
        if (reclass && justificativa != null) {
            sb.append(linha("Justificativa", esc(justificativa)));
        }
        sb.append("</table>")
          .append("<p>Acompanhe o chamado no sistema, menu <strong>Chamados</strong>.</p>")
          .append("<hr style='border:0;border-top:1px solid #ccc'/>")
          .append("<p style='font-size:12px;color:#777'>Mensagem automática — não responda este e-mail.</p>")
          .append("</div>");
        return sb.toString();
    }

    public static String statusAposTentativa(boolean enviado, int tentativasAntes) {
        if (enviado) return "ENVIADO";
        return tentativasAntes + 1 >= MAX_TENTATIVAS ? "ERRO" : "PENDENTE";
    }

    /** Envia com a configuracao em vigor (Chamados ou confGTW.xml); lanca excecao em caso de falha. */
    public static void enviar(Connection conn, String destinatario, String assunto, String html) throws Exception {
        enviar(ConfiguracaoSmtpChamado.resolver(conn), destinatario, assunto, html);
    }

    public static void enviar(ConfiguracaoSmtpChamado cfg, String destinatario, String assunto, String html)
            throws MessagingException {
        Properties props = propriedades(cfg);
        Session session;
        if (cfg.usuario != null && !cfg.usuario.isEmpty()) {
            props.put("mail.smtp.auth", "true");
            final String usuario = cfg.usuario;
            final String senha = cfg.senha == null ? "" : cfg.senha;
            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(usuario, senha);
                }
            });
        } else {
            session = Session.getInstance(props);
        }
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(cfg.remetente));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
        msg.setSubject(assunto, "UTF-8");
        msg.setContent(html, "text/html; charset=UTF-8");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }

    /**
     * Propriedades JavaMail. Com o SMTP configurado nos Chamados e STARTTLS ligado, o TLS e obrigatorio
     * (sem downgrade) e o certificado e conferido; o caminho confGTW.xml mantem o comportamento legado.
     */
    static Properties propriedades(ConfiguracaoSmtpChamado cfg) {
        Properties props = new Properties();
        props.put("mail.smtp.host", cfg.host);
        props.put("mail.smtp.port", String.valueOf(cfg.porta));
        props.put("mail.smtp.starttls.enable", String.valueOf(cfg.starttls));
        if (cfg.starttls && ConfiguracaoSmtpChamado.ORIGEM_CHAMADOS.equals(cfg.origem)) {
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.checkserveridentity", "true");
        }
        props.put("mail.smtp.connectiontimeout", TIMEOUT_MS);
        props.put("mail.smtp.timeout", TIMEOUT_MS);
        props.put("mail.smtp.writetimeout", TIMEOUT_MS);
        return props;
    }

    /** Mensagem legivel da falha (com a causa raiz), sem o segredo informado. */
    public static String mensagemErro(Throwable e, String segredo) {
        String msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        Throwable raiz = e;
        while (raiz.getCause() != null && raiz.getCause() != raiz) raiz = raiz.getCause();
        if (raiz != e) {
            msg += " (" + raiz.getClass().getSimpleName() + (raiz.getMessage() == null ? "" : ": " + raiz.getMessage()) + ")";
        }
        if (segredo != null && !segredo.isEmpty()) msg = msg.replace(segredo, "***");
        return msg;
    }

    static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static String linha(String rotulo, String valorHtml) {
        return "<tr><td style='padding:4px 12px 4px 0;color:#555'>" + rotulo + "</td><td style='padding:4px 0'>"
                + valorHtml + "</td></tr>";
    }
}
