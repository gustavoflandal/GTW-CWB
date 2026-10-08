package muralha.digital.integridade;

import com.consilux.lib.Conexao;
import muralha.digital.util.HashUtil;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Calcula e armazena SHA-256 de imagens que ainda não possuem hash em
 * muralha.vtr_imagem_complemento. Roda a cada 5 minutos e processa até
 * 200 imagens por execução para não sobrecarregar o banco.
 */
public class JobSha256Imagem implements Job {

    private static final Logger logger = Logger.getLogger(JobSha256Imagem.class);
    private static final int LOTE = 200;

    @Override
    public void execute(JobExecutionContext context) {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            // 1. Busca imagens sem hash
            String sqlSelect =
                "SELECT TOP " + LOTE + " vtri.id, vtri.imagem " +
                "FROM muralha.veiculo_tempo_real_imagem vtri " +
                "WHERE NOT EXISTS (" +
                "    SELECT 1 FROM muralha.vtr_imagem_complemento c " +
                "    WHERE c.id_imagem = vtri.id AND c.sha256 IS NOT NULL" +
                ")";

            List<String[]> pendentes = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlSelect);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    byte[] imagem = rs.getBytes("imagem");
                    if (imagem != null) {
                        pendentes.add(new String[]{id, HashUtil.sha256Hex(imagem)});
                    }
                }
            }

            if (pendentes.isEmpty()) return;

            // 2. UPSERT do hash na tabela complementar
            String sqlUpsert =
                "MERGE muralha.vtr_imagem_complemento AS t " +
                "USING (SELECT CAST(? AS UNIQUEIDENTIFIER) id_imagem, ? sha256, 'OK' status_integridade) AS s " +
                "ON t.id_imagem = s.id_imagem " +
                "WHEN MATCHED THEN UPDATE SET sha256 = s.sha256, status_integridade = s.status_integridade " +
                "WHEN NOT MATCHED THEN INSERT (id_imagem, sha256, status_integridade) " +
                "VALUES (s.id_imagem, s.sha256, s.status_integridade);";

            try (PreparedStatement ps = conn.prepareStatement(sqlUpsert)) {
                for (String[] par : pendentes) {
                    ps.setString(1, par[0]);
                    ps.setString(2, par[1]);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            logger.info("JobSha256Imagem: hash calculado para " + pendentes.size() + " imagens.");

        } catch (Exception e) {
            logger.error("Erro no JobSha256Imagem", e);
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
    }
}
