package muralha.digital.chamado;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ConfiguracaoSmtpChamadoTest {

    private static String repetir(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }

    @Test
    public void configuracaoValida() {
        ConfiguracaoSmtpChamado.validar("smtp.empresa.com.br", 587, "chamados@empresa.com.br", "usuario@empresa.com.br");
        ConfiguracaoSmtpChamado.validar("10.0.0.25", 25, "chamados@empresa.com.br", "");
        ConfiguracaoSmtpChamado.validar("mail-01", 1, "a@b.co", null);
        ConfiguracaoSmtpChamado.validar("smtp.invalid", 65535, "a@b.co", repetir('u', 100));
    }

    @Test
    public void hostVazioVoltaAoConfGtwSemExigirDemaisCampos() {
        ConfiguracaoSmtpChamado.validar("", 0, "", "");
        ConfiguracaoSmtpChamado.validar(null, 0, null, null);
    }

    @DataProvider
    public Object[][] invalidos() {
        return new Object[][] {
                {"smtp empresa.com", 587, "a@b.co", ""},       // espaco no host
                {"smtp.empresa.com/x", 587, "a@b.co", ""},     // caractere invalido
                {"-smtp.empresa.com", 587, "a@b.co", ""},      // rotulo comeca com hifen
                {"smtp..empresa.com", 587, "a@b.co", ""},      // rotulo vazio
                {"256.1.1.1", 587, "a@b.co", ""},              // IPv4 fora da faixa
                {"10.0.0", 587, "a@b.co", ""},                 // IPv4 incompleto
                {repetir('a', 64) + ".com", 587, "a@b.co", ""}, // rotulo com mais de 63
                {"smtp.empresa.com", 0, "a@b.co", ""},          // porta 0
                {"smtp.empresa.com", 65536, "a@b.co", ""},      // porta acima do limite
                {"smtp.empresa.com", 587, "", ""},              // remetente obrigatorio
                {"smtp.empresa.com", 587, "sem-arroba", ""},    // remetente invalido
                {"smtp.empresa.com", 587, "a@b", ""},           // dominio sem ponto
                {"smtp.empresa.com", 587, "a b@c.com", ""},     // espaco no e-mail
                {"smtp.empresa.com", 587, "a@b.co", repetir('u', 101)}, // usuario > 100
                {"", 0, "", repetir('u', 101)}                  // usuario > 100 mesmo sem host
        };
    }

    @Test(dataProvider = "invalidos", expectedExceptions = RegraChamadoException.class)
    public void configuracaoInvalida(String host, int porta, String remetente, String usuario) {
        ConfiguracaoSmtpChamado.validar(host, porta, remetente, usuario);
    }
}
