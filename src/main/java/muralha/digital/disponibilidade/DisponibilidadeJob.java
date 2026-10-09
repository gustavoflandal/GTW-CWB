package muralha.digital.disponibilidade;

import com.consilux.lib.Conexao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class DisponibilidadeJob implements Job {

    private static final int DEFAULT_THRESHOLD_MIN = 15;

    @Override
    public void execute(JobExecutionContext ctx) {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            int threshold = obterThreshold(conn);

            String sqlLocais =
                "SELECT DISTINCT vtr.id_local " +
                "FROM muralha.veiculo_tempo_real vtr " +
                "WHERE vtr.id_local IS NOT NULL " +
                "  AND vtr.data >= DATEADD(DAY, -7, SYSDATETIME())";

            try (PreparedStatement ps = conn.prepareStatement(sqlLocais);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idLocal = rs.getInt("id_local");
                    verificarLocal(conn, idLocal, threshold);
                }
            }
        } catch (Exception e) {
            System.err.println("[DisponibilidadeJob] Erro: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    private void verificarLocal(Connection conn, int idLocal, int threshold)
            throws Exception {
        String sql =
            "SELECT MAX(vtr.data) AS ultima_passagem " +
            "FROM muralha.veiculo_tempo_real vtr " +
            "WHERE vtr.id_local = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLocal);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;
                Timestamp ultima = rs.getTimestamp("ultima_passagem");
                boolean disponivel = true;
                Integer minutosOffline = null;

                if (ultima != null) {
                    long diffMin = (System.currentTimeMillis() - ultima.getTime()) / 60_000;
                    if (diffMin > threshold) {
                        disponivel = false;
                        minutosOffline = (int) diffMin;
                    }
                } else {
                    disponivel = false;
                }

                try (PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO muralha.equipamento_disponibilidade " +
                        "(id_local, disponivel, ultima_passagem, minutos_offline) " +
                        "VALUES (?, ?, ?, ?)")) {
                    ins.setInt(1, idLocal);
                    ins.setBoolean(2, disponivel);
                    ins.setTimestamp(3, ultima);
                    if (minutosOffline != null) ins.setInt(4, minutosOffline);
                    else ins.setNull(4, java.sql.Types.INTEGER);
                    ins.executeUpdate();
                }
            }
        }
    }

    private int obterThreshold(Connection conn) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT valor FROM muralha.config_chave_valor " +
                "WHERE chave = 'disponibilidade_threshold_min'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return Integer.parseInt(rs.getString("valor"));
        }
        return DEFAULT_THRESHOLD_MIN;
    }
}
