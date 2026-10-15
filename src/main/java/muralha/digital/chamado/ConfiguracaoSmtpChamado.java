package muralha.digital.chamado;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.ConfiguracaoServidorSmtp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SMTP usado pelos e-mails dos chamados: as chaves chamado_smtp_* de muralha.config_chave_valor
 * ou, com o host vazio, o primeiro servidor do confGTW.xml.
 */
public final class ConfiguracaoSmtpChamado {

    public static final String ORIGEM_CHAMADOS = "CHAMADOS";
    public static final String ORIGEM_CONFGTW = "CONFGTW";

    public static final String CHAVE_HOST = "chamado_smtp_host";
    public static final String CHAVE_PORTA = "chamado_smtp_porta";
    public static final String CHAVE_USUARIO = "chamado_smtp_usuario";
    public static final String CHAVE_SENHA = "chamado_smtp_senha";
    public static final String CHAVE_REMETENTE = "chamado_smtp_remetente";
    public static final String CHAVE_STARTTLS = "chamado_smtp_starttls";

    public static final int PORTA_PADRAO = 587;
    public static final int MAX_USUARIO = 100;
    private static final int MAX_REMETENTE = 255;

    private static final Pattern IPV4 = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$");
    private static final Pattern ROTULO_DNS = Pattern.compile("^[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?$");
    private static final Pattern SO_DIGITOS_E_PONTOS = Pattern.compile("^[0-9.]+$");
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public String host;
    public int porta;
    public String usuario;
    public String senha;
    public String remetente;
    public boolean starttls;
    public String origem;

    /** Configuracao em vigor; IllegalStateException com mensagem clara quando nao ha como enviar. */
    public static ConfiguracaoSmtpChamado resolver(Connection conn) throws SQLException {
        ConfiguracaoSmtpChamado c = new ConfiguracaoSmtpChamado();
        String host = ChamadoConfigDao.valor(conn, CHAVE_HOST, "");
        if (host.isEmpty()) {
            List<ConfiguracaoServidorSmtp> servidores = ConfiguracaoProvider.getInstance().getListaServidoresSmtp();
            if (servidores == null || servidores.isEmpty()) {
                throw new IllegalStateException("Nenhum servidor SMTP configurado (nem nos Chamados, nem no confGTW.xml).");
            }
            ConfiguracaoServidorSmtp s = servidores.get(0);
            c.host = s.getHost();
            c.porta = s.getPort() == null ? PORTA_PADRAO : s.getPort();
            c.usuario = s.getUser();
            c.senha = s.getPassword();
            c.remetente = s.getMail();
            c.starttls = true;
            c.origem = ORIGEM_CONFGTW;
            return c;
        }
        c.host = host;
        c.porta = ChamadoConfigDao.valorInt(conn, CHAVE_PORTA, PORTA_PADRAO);
        c.usuario = ChamadoConfigDao.valor(conn, CHAVE_USUARIO, "");
        c.remetente = ChamadoConfigDao.valor(conn, CHAVE_REMETENTE, "");
        c.starttls = !"0".equals(ChamadoConfigDao.valor(conn, CHAVE_STARTTLS, "1"));
        c.origem = ORIGEM_CHAMADOS;
        String cifrada = ChamadoConfigDao.valor(conn, CHAVE_SENHA, "");
        if (cifrada.isEmpty()) {
            c.senha = "";
        } else {
            byte[] chave = CifraSegredo.chaveDoAmbiente();
            if (chave == null) {
                throw new IllegalStateException("A senha do SMTP dos chamados está gravada cifrada, mas a variável de ambiente "
                        + CifraSegredo.VARIAVEL_CHAVE + " não está definida (ou é inválida) no servidor.");
            }
            try {
                c.senha = CifraSegredo.decifrar(cifrada, chave);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Não foi possível decifrar a senha do SMTP dos chamados com a chave atual de "
                        + CifraSegredo.VARIAVEL_CHAVE + ". Grave a senha novamente na Configuração.");
            }
        }
        return c;
    }

    /**
     * Validacao pura. Host vazio = usar o confGTW.xml: so o tamanho do usuario e o formato do remetente
     * (se informado) sao conferidos.
     */
    public static void validar(String host, int porta, String remetente, String usuario) {
        if (usuario != null && usuario.trim().length() > MAX_USUARIO) {
            throw new RegraChamadoException("O usuário do SMTP deve ter no máximo " + MAX_USUARIO + " caracteres.");
        }
        String h = host == null ? "" : host.trim();
        String r = remetente == null ? "" : remetente.trim();
        if (h.isEmpty()) {
            if (!r.isEmpty()) validarRemetente(r);
            return;
        }
        if (!hostValido(h)) {
            throw new RegraChamadoException("Servidor SMTP inválido: informe um nome DNS ou um endereço IPv4.");
        }
        if (porta < 1 || porta > 65535) {
            throw new RegraChamadoException("A porta do SMTP deve estar entre 1 e 65535.");
        }
        if (r.isEmpty()) throw new RegraChamadoException("Informe o e-mail do remetente.");
        validarRemetente(r);
    }

    private static void validarRemetente(String r) {
        if (r.length() > MAX_REMETENTE || !EMAIL.matcher(r).matches()) {
            throw new RegraChamadoException("E-mail do remetente inválido.");
        }
    }

    static boolean hostValido(String h) {
        if (h.length() > 253) return false;
        if (SO_DIGITOS_E_PONTOS.matcher(h).matches()) return IPV4.matcher(h).matches();
        for (String rotulo : h.split("\\.", -1)) {
            if (!ROTULO_DNS.matcher(rotulo).matches()) return false;
        }
        return true;
    }
}
