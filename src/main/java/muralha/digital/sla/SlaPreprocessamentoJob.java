package muralha.digital.sla;

import com.consilux.lib.Conexao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SlaPreprocessamentoJob implements Job {

    @Override
    public void execute(JobExecutionContext ctx) {
        try {
            verificarSla();
        } catch (Exception e) {
            System.err.println("[SlaPreprocessamentoJob] Erro: " + e.getMessage());
        }
    }

    private void verificarSla() throws Exception {
        Connection conn = Conexao.getConexao();
        try {
            String sql =
                "SELECT COUNT(*) AS total_pendentes, " +
                "  SUM(CASE WHEN DATEDIFF(HOUR, vtr.data, SYSDATETIME()) > 72 THEN 1 ELSE 0 END) AS acima_72h, " +
                "  SUM(CASE WHEN DATEDIFF(HOUR, vtr.data, SYSDATETIME()) > 48 THEN 1 ELSE 0 END) AS acima_48h, " +
                "  SUM(CASE WHEN DATEDIFF(HOUR, vtr.data, SYSDATETIME()) > 24 THEN 1 ELSE 0 END) AS acima_24h " +
                "FROM muralha.veiculo_tempo_real vtr " +
                "LEFT JOIN muralha.vtr_status_analise vsa " +
                "  ON vsa.id_veiculo_tempo_real = vtr.id " +
                "WHERE COALESCE(vsa.status_analise, 'AGUARDANDO_ANALISE') = 'AGUARDANDO_ANALISE' " +
                "  AND vtr.data IS NOT NULL";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try (PreparedStatement ins = conn.prepareStatement(
                            "INSERT INTO muralha.alerta_sla_preproc " +
                            "(total_pendentes, acima_72h, acima_48h, acima_24h) VALUES (?,?,?,?)")) {
                        ins.setInt(1, rs.getInt("total_pendentes"));
                        ins.setInt(2, rs.getInt("acima_72h"));
                        ins.setInt(3, rs.getInt("acima_48h"));
                        ins.setInt(4, rs.getInt("acima_24h"));
                        ins.executeUpdate();
                    }
                }
            }
        } finally {
            conn.close();
        }
    }
}
