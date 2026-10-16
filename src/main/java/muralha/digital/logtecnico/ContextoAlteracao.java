package muralha.digital.logtecnico;

import com.consilux.infra.SessaoConstantes;
import com.consilux.model.Usuario;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/** Quem alterou, de onde e por qual funcionalidade (gravado com cada alteração de parâmetro). */
public final class ContextoAlteracao {

    private final Integer idUsuario;
    private final String login;
    private final String ip;
    private final String origem;

    public ContextoAlteracao(Integer idUsuario, String login, String ip, String origem) {
        this.idUsuario = idUsuario;
        this.login = login;
        this.ip = ip;
        this.origem = origem;
    }

    /** Usuário da sessão (errata §1) e IP da requisição. */
    public static ContextoAlteracao de(HttpServletRequest req, String origem) {
        Integer id = null;
        String login = null;
        HttpSession s = req.getSession(false);
        Object u = s == null ? null : s.getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (u instanceof Usuario) {
            id = ((Usuario) u).getId();
            login = ((Usuario) u).getUsuario();
        }
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = req.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return new ContextoAlteracao(id, login, ip, origem);
    }

    /** Alteração feita fora de requisição HTTP (web service, job). */
    public static ContextoAlteracao sistema(Integer idUsuario, String origem) {
        return new ContextoAlteracao(idUsuario, idUsuario == null ? "sistema" : null, null, origem);
    }

    public Integer getIdUsuario() { return idUsuario; }

    public String getLogin() { return login; }

    public String getIp() { return ip; }

    public String getOrigem() { return origem; }
}
