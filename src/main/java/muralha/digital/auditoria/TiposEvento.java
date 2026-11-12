package muralha.digital.auditoria;

import com.consilux.lib.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuração global dos TIPOS DE EVENTO capturados (dbo.sis_log_tipo_evento):
 * ACESSO, VISUALIZACAO, CADASTRO, ALTERACAO, EXCLUSAO, EXPORTACAO, ERRO.
 *
 * <p>Permite ligar/desligar a captura por tipo, valendo para TODAS as telas.
 * O cache é preguiçoso e resiliente: em caso de falha de leitura (ex.: conexão
 * ainda não configurada no startup) ou tipo desconhecido, o padrão é CAPTURAR
 * (fail-open), para nunca deixar de registrar por configuração ausente.
 */
public final class TiposEvento {

    private static final Logger log = Logger.getLogger(TiposEvento.class.getName());

    private static volatile Map<String, Boolean> cache = null;

    private TiposEvento() {}

    /** True se eventos deste nível devem ser capturados (padrão: true). */
    public static boolean capturaAtivo(String nivel) {
        if (nivel == null) return true;
        Map<String, Boolean> m = cache;
        if (m == null) m = carregar();
        Boolean b = m.get(nivel.toUpperCase());
        return (b == null) ? true : b; // tipo não cadastrado => captura
    }

    public static synchronized void recarregar() {
        Map<String, Boolean> m = carregarDoBanco();
        if (m != null) cache = m;
    }

    private static synchronized Map<String, Boolean> carregar() {
        if (cache != null) return cache;
        Map<String, Boolean> m = carregarDoBanco();
        if (m != null) { cache = m; return cache; }
        return new LinkedHashMap<>(); // transitório, sem envenenar o cache
    }

    /** Lê a tabela; devolve null em caso de falha (para nova tentativa depois). */
    private static Map<String, Boolean> carregarDoBanco() {
        Map<String, Boolean> m = new LinkedHashMap<>();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT codigo, capturar FROM dbo.sis_log_tipo_evento");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    m.put(rs.getString("codigo").toUpperCase(), rs.getBoolean("capturar"));
                }
            }
            return m;
        } catch (Exception e) {
            log.log(Level.WARNING, "[MotorLogs] Falha ao carregar tipos de evento; nova tentativa depois.", e);
            return null;
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
    }
}
