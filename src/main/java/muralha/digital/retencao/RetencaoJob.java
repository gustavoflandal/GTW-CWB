package muralha.digital.retencao;

import com.consilux.lib.Conexao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class RetencaoJob implements Job {

    private static final int DEFAULT_RETENCAO_ANOS = 5;

    @Override
    public void execute(JobExecutionContext ctx) {
        try {
            executarExpurgo();
        } catch (Exception e) {
            System.err.println("[RetencaoJob] Erro: " + e.getMessage());
        }
    }

    private void executarExpurgo() throws Exception {
        Connection conn = Conexao.getConexao();
        int movidos = 0;
        String status = "SUCESSO";
        String mensagem = null;
        int anosRetencao = DEFAULT_RETENCAO_ANOS;

        try {
            anosRetencao = obterRetencaoAnos(conn);
            LocalDate dtCorte = LocalDate.now().minusYears(anosRetencao);

            String sqlCount =
                "SELECT COUNT(*) AS cnt " +
                "FROM muralha.veiculo_tempo_real vtr " +
                "INNER JOIN muralha.vtr_status_analise vsa " +
                "  ON vsa.id_veiculo_tempo_real = vtr.id " +
                "WHERE CAST(vtr.data AS DATE) < ? " +
                "  AND vsa.status_analise IN ('PRE_APROVADA','REPROVADA')";

            try (PreparedStatement ps = conn.prepareStatement(sqlCount)) {
                ps.setString(1, dtCorte.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) movidos = rs.getInt("cnt");
                }
            }

            registrarLog(conn, movidos, anosRetencao, dtCorte, status, mensagem);

        } catch (Exception e) {
            status = "ERRO";
            mensagem = e.getMessage();
            try {
                registrarLog(conn, 0, anosRetencao,
                    LocalDate.now().minusYears(anosRetencao), status, mensagem);
            } catch (Exception ignored) {}
            throw e;
        } finally {
            conn.close();
        }
    }

    private int obterRetencaoAnos(Connection conn) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT valor FROM muralha.config_chave_valor WHERE chave = 'retencao_anos'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return Integer.parseInt(rs.getString("valor"));
        }
        return DEFAULT_RETENCAO_ANOS;
    }

    private void registrarLog(Connection conn, int movidos, int anos,
            LocalDate dtCorte, String status, String mensagem) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.expurgo_log " +
                "(tabela, registros_movidos, retencao_anos, dt_corte, status, mensagem) " +
                "VALUES ('veiculo_tempo_real', ?, ?, ?, ?, ?)")) {
            ps.setInt(1, movidos);
            ps.setInt(2, anos);
            ps.setString(3, dtCorte.toString());
            ps.setString(4, status);
            ps.setString(5, mensagem);
            ps.executeUpdate();
        }
    }
}
