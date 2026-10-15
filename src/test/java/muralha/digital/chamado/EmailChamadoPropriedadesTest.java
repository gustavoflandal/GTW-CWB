package muralha.digital.chamado;

import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.*;

public class EmailChamadoPropriedadesTest {

    private static ConfiguracaoSmtpChamado cfg(String origem, boolean starttls) {
        ConfiguracaoSmtpChamado c = new ConfiguracaoSmtpChamado();
        c.host = "smtp.exemplo.com";
        c.porta = 587;
        c.usuario = "";
        c.senha = "";
        c.remetente = "chamados@exemplo.com";
        c.starttls = starttls;
        c.origem = origem;
        return c;
    }

    @Test
    public void chamadosComStarttlsExigeTlsEVerificaIdentidade() {
        Properties p = EmailChamado.propriedades(cfg(ConfiguracaoSmtpChamado.ORIGEM_CHAMADOS, true));
        assertEquals(p.getProperty("mail.smtp.starttls.enable"), "true");
        assertEquals(p.getProperty("mail.smtp.starttls.required"), "true");
        assertEquals(p.getProperty("mail.smtp.ssl.checkserveridentity"), "true");
        assertEquals(p.getProperty("mail.smtp.writetimeout"), "10000");
    }

    @Test
    public void chamadosSemStarttlsNaoExigeTls() {
        Properties p = EmailChamado.propriedades(cfg(ConfiguracaoSmtpChamado.ORIGEM_CHAMADOS, false));
        assertEquals(p.getProperty("mail.smtp.starttls.enable"), "false");
        assertNull(p.getProperty("mail.smtp.starttls.required"));
        assertNull(p.getProperty("mail.smtp.ssl.checkserveridentity"));
        assertEquals(p.getProperty("mail.smtp.writetimeout"), "10000");
    }

    @Test
    public void confGtwMantemComportamentoLegado() {
        Properties p = EmailChamado.propriedades(cfg(ConfiguracaoSmtpChamado.ORIGEM_CONFGTW, true));
        assertEquals(p.getProperty("mail.smtp.starttls.enable"), "true");
        assertNull(p.getProperty("mail.smtp.starttls.required"));
        assertNull(p.getProperty("mail.smtp.ssl.checkserveridentity"));
        assertEquals(p.getProperty("mail.smtp.writetimeout"), "10000");
    }
}
