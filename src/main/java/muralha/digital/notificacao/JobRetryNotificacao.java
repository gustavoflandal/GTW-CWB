package muralha.digital.notificacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.consilux.lib.Conexao;
import muralha.digital.util.ConfigChaveValor;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobRetryNotificacao implements Job {

    private static final Logger logger = Logger.getLogger(JobRetryNotificacao.class);

    private static final UUID STATUS_NAO_ENVIADO = StatusNotificacao.Status.NAO_ENVIADO.GetID();
    private static final UUID STATUS_PENDENTE    = StatusNotificacao.Status.PENDENTE.GetID();

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            boolean habilitado = "true".equalsIgnoreCase(
                ConfigChaveValor.obter(conn, "RETRY_NOTIFICACAO_HABILITADO", "true"));
            if (!habilitado) {
                logger.debug("[Muralha] Retry de notificações desabilitado via config.");
                return;
            }

            int maxTentativas = ConfigChaveValor.obterInt(conn, "RETRY_NOTIFICACAO_MAX", 3);

            int totalAlerta     = reprocessar(conn, "muralha.alerta_notificacao",     "ALERTA",     maxTentativas);
            int totalOcorrencia = reprocessar(conn, "muralha.ocorrencia_notificacao", "OCORRENCIA", maxTentativas);

            if (totalAlerta + totalOcorrencia > 0) {
                logger.info(String.format(
                    "[Muralha] Retry: %d alertas e %d ocorrências reagendados para reenvio.",
                    totalAlerta, totalOcorrencia));
            }
        } catch (Exception e) {
            logger.error("[Muralha] Erro no job de retry de notificações.", e);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
        }
    }

    private int reprocessar(Connection conn, String tabela, String tipoRegistro, int maxTentativas)
            throws SQLException {
        int reprocessados = 0;

        String sqlBusca =
            "SELECT n.id FROM " + tabela + " n " +
            "WHERE n.id_status_notificacao = ? " +
            "  AND (SELECT COUNT(*) FROM muralha.notificacao_retry r " +
            "       WHERE r.id_notificacao = n.id AND r.tipo_registro = ?) < ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlBusca)) {
            ps.setString(1, STATUS_NAO_ENVIADO.toString());
            ps.setString(2, tipoRegistro);
            ps.setInt(3, maxTentativas);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UUID idNotificacao = UUID.fromString(rs.getString("id"));
                    int tentativa = registrarTentativa(conn, idNotificacao, tipoRegistro);
                    resetarParaPendente(conn, tabela, idNotificacao);
                    reprocessados++;
                    logger.info(String.format(
                        "[Muralha] Retry #%d para notificação %s (%s).",
                        tentativa, idNotificacao, tipoRegistro));
                }
            }
        }
        return reprocessados;
    }

    private int registrarTentativa(Connection conn, UUID idNotificacao, String tipoRegistro)
            throws SQLException {
        int tentativa;
        String sqlCount =
            "SELECT ISNULL(MAX(tentativa), 0) FROM muralha.notificacao_retry " +
            "WHERE id_notificacao = ? AND tipo_registro = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlCount)) {
            ps.setString(1, idNotificacao.toString());
            ps.setString(2, tipoRegistro);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                tentativa = rs.getInt(1) + 1;
            }
        }

        String sqlInsert =
            "INSERT INTO muralha.notificacao_retry " +
            "(id_notificacao, tipo_registro, tentativa, erro, sucesso) " +
            "VALUES (?, ?, ?, 'Reagendado para reenvio', 0)";
        try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            ps.setString(1, idNotificacao.toString());
            ps.setString(2, tipoRegistro);
            ps.setInt(3, tentativa);
            ps.executeUpdate();
        }
        return tentativa;
    }

    private void resetarParaPendente(Connection conn, String tabela, UUID idNotificacao)
            throws SQLException {
        String sql = "UPDATE " + tabela + " SET id_status_notificacao = ?, data_processado = NULL WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, STATUS_PENDENTE.toString());
            ps.setString(2, idNotificacao.toString());
            ps.executeUpdate();
        }
    }
}
