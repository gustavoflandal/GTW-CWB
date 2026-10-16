package muralha.digital.auditoria;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AuditoriaService {

    private static final Logger log = Logger.getLogger(AuditoriaService.class.getName());
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

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
                // Errata §1/§10: o login grava com.consilux.model.Usuario em SessaoConstantes.SESSAO_USUARIO.
                // "objUsuario" (muralha.digital.acessos.Usuario) só existe nas telas da Muralha e fica como reserva.
                Object obj = sess.getAttribute(SessaoConstantes.SESSAO_USUARIO);
                if (obj == null) {
                    obj = sess.getAttribute("objUsuario");
                }

                if (obj instanceof com.consilux.model.Usuario) {
                    com.consilux.model.Usuario u = (com.consilux.model.Usuario) obj;
                    login     = u.getUsuario();
                    idUsuario = u.getId();
                } else if (obj instanceof muralha.digital.acessos.Usuario) {
                    muralha.digital.acessos.Usuario u = (muralha.digital.acessos.Usuario) obj;
                    login     = u.getUsuario();
                    idUsuario = u.getIdUsuario();
                }
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "[Auditoria] Falha ao obter usuario da sessao", e);
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();

        final String loginFinal      = login;
        final Integer idUsuarioFinal = idUsuario;
        final String ipFinal         = ip;

        executor.submit(() -> {
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
            } catch (Exception e) {
                log.log(Level.SEVERE, "[Auditoria] Falha ao gravar log: " + funcionalidade + "/" + operacao, e);
            } finally {
                if (conn != null) try { conn.close(); } catch (Exception e2) {}
            }
        });
    }
}
