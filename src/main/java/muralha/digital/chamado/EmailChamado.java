package muralha.digital.chamado;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.ConfiguracaoServidorSmtp;
import muralha.digital.notificacao.ServicoEmailMuralha;

import javax.mail.internet.InternetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Confirmacao de abertura e aviso de reclassificacao (spec §1.4, D14). */
public final class EmailChamado {

    public static final String CONFIRMACAO_ABERTURA = "CONFIRMACAO_ABERTURA";
    public static final String RECLASSIFICACAO = "RECLASSIFICACAO";
    public static final int MAX_TENTATIVAS = 5;

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

    public static boolean enviar(String destinatario, String assunto, String html) throws Exception {
        List<ConfiguracaoServidorSmtp> servidores = ConfiguracaoProvider.getInstance().getListaServidoresSmtp();
        if (servidores == null || servidores.isEmpty()) {
            throw new IllegalStateException("Nenhum servidor SMTP configurado em confGTW.xml.");
        }
        ConfiguracaoServidorSmtp s = servidores.get(0);
        ServicoEmailMuralha servico = new ServicoEmailMuralha(s.getUser(), s.getPassword(), s.getHost(), s.getPort());
        return servico.enviarEmailChamado(new InternetAddress(s.getMail()),
                List.of(new InternetAddress(destinatario)), assunto, html);
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
