package muralha.digital.auditoria;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import muralha.digital.acessos.Usuario;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;

public final class AuditoriaService {

    private AuditoriaService() {}

    public static void registrar(
            HttpServletRequest request,
            String funcionalidade,
            String operacao,
            String idRegistro,
            String descricao) {

        String login = null;
        Integer idUsuario = null;
        try {
            HttpSession sess = request.getSession(false);
            if (sess != null) {
                Usuario u = (Usuario) sess.getAttribute(SessaoConstantes.SESSAO_USUARIO);
                if (u != null) {
                    login     = u.getUsuario();
                    idUsuario = u.getIdUsuario();
                }
            }
        } catch (Exception ignored) {}

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();

        final String loginFinal      = login;
        final Integer idUsuarioFinal = idUsuario;
        final String ipFinal         = ip;

        new Thread(() -> {
            Connection conn = null;
            try {
                conn = Conexao.getConexao();
                String sql =
                    "INSERT INTO dbo.sis_log_auditoria " +
                    "(id_usuario, login, ip_terminal, funcionalidade, operacao, id_registro, descricao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    if (idUsuarioFinal != null) ps.setInt(1, idUsuarioFinal);
                    else ps.setNull(1, java.sql.Types.INTEGER);
                    ps.setString(2, loginFinal);
                    ps.setString(3, ipFinal);
                    ps.setString(4, funcionalidade);
                    ps.setString(5, operacao);
                    ps.setString(6, idRegistro);
                    ps.setString(7, descricao);
                    ps.executeUpdate();
                }
            } catch (Exception ignored) {
            } finally {
                if (conn != null) try { conn.close(); } catch (Exception e2) {}
            }
        }).start();
    }
}
