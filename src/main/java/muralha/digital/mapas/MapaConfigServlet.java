package muralha.digital.mapas;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

/**
 * Configurações dos mapas lidas de muralha.config_chave_valor, para não deixar
 * endereços fixos no JavaScript (antes em utils/maps-config.js).
 */
@WebServlet("/MuralhaDigital/MapaConfig")
public class MapaConfigServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MapaConfigServlet.class);
    private static final Gson gson = new Gson();

    /** Chave com o endereço (host:porta) da API de situação do trânsito por trecho. */
    static final String CHAVE_API_MONITORAMENTO_TRECHO = "mapa_api_monitoramento_trecho";

    /** host ou IP, com porta opcional; sem esquema, caminho ou espaço. */
    private static final Pattern HOST_PORTA = Pattern.compile("^[A-Za-z0-9.-]{1,253}(:\\d{1,5})?$");

    /** Devolve o endereço limpo, ou "" se estiver vazio ou fora do formato host[:porta]. */
    static String validarHostPorta(String valor) {
        if (valor == null) return "";
        String v = valor.trim();
        return HOST_PORTA.matcher(v).matches() ? v : "";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) { resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
        if (!new Acesso(req, resp, true).verificaAcesso(false)) { return; }

        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");
        try {
            if ("obterApiMonitoramentoTrecho".equals(acao)) {
                String endereco = validarHostPorta(lerChave(CHAVE_API_MONITORAMENTO_TRECHO));
                if (endereco.isEmpty()) {
                    logger.warn("Chave " + CHAVE_API_MONITORAMENTO_TRECHO + " ausente ou inválida em muralha.config_chave_valor");
                }
                JsonObject obj = new JsonObject();
                obj.addProperty("endereco", endereco);
                resp.getWriter().write(gson.toJson(obj));
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida.");
            }
        } catch (Exception e) {
            logger.error("Erro em MapaConfigServlet", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno.");
        }
    }

    private static String lerChave(String chave) throws Exception {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT valor FROM muralha.config_chave_valor WHERE chave = ?")) {
            ps.setString(1, chave);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("valor") : null;
            }
        }
    }
}
