package muralha.digital.alerta.operacional;

import com.consilux.lib.Conexao;
import muralha.digital.util.ConfigChaveValor;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aplica os tratamentos marcados como automáticos (Tipos de Alerta → Tratamentos) aos alertas
 * recentes. Cada par alerta × tratamento é executado uma única vez (muralha.alerta_tratamento_execucao).
 * Só considera alertas dos últimos TRATAMENTO_AUTO_JANELA_MIN minutos, para não reprocessar o histórico.
 */
@DisallowConcurrentExecution
public class JobTratamentoAutomatico implements Job {

    private static final Logger logger = Logger.getLogger(JobTratamentoAutomatico.class);
    static final int JANELA_PADRAO_MIN = 60;
    static final int LOTE = 500;

    @Override
    public void execute(JobExecutionContext ctx) {
        try (Connection conn = Conexao.getConexao()) {
            int janela = ConfigChaveValor.obterInt(conn, "TRATAMENTO_AUTO_JANELA_MIN", JANELA_PADRAO_MIN);
            int executados = processar(conn, janela > 0 ? janela : JANELA_PADRAO_MIN);
            if (executados > 0) {
                logger.info("Tratamentos automáticos executados: " + executados);
            }
        } catch (Exception e) {
            logger.error("Erro no job de tratamentos automáticos: " + e.getMessage(), e);
        }
    }

    static int processar(Connection conn, int janelaMin) throws SQLException {
        List<Pendente> pendentes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP (" + LOTE + ") a.id, t.tratamento "
                + "FROM muralha.alerta a "
                + "JOIN muralha.tipo_alerta_tratamento t ON t.id_tipo_alerta_ocorrencia = a.id_tipo_alerta_ocorrencia "
                + "  AND t.ativo = 1 AND t.automatico = 1 "
                + "WHERE a.data >= DATEADD(MINUTE, -?, GETDATE()) "
                + "  AND NOT EXISTS (SELECT 1 FROM muralha.alerta_tratamento_execucao e "
                + "                  WHERE e.id_alerta = a.id AND e.tratamento = t.tratamento) "
                + "ORDER BY a.data")) {
            ps.setInt(1, janelaMin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pendentes.add(new Pendente(UUID.fromString(rs.getString(1)), rs.getString(2)));
                }
            }
        }

        int executados = 0;
        for (Pendente p : pendentes) {
            String resultado;
            try {
                TratamentoAlerta t = TratamentoAlerta.interpretar(p.tratamento);
                resultado = ExecutorTratamento.executar(conn, p.idAlerta, t);
                executados++;
            } catch (IllegalArgumentException e) {
                resultado = "Tratamento desconhecido";
            } catch (SQLException e) {
                logger.error("Falha ao executar " + p.tratamento + " no alerta " + p.idAlerta + ": " + e.getMessage(), e);
                continue;
            }
            registrar(conn, p, resultado);
        }
        return executados;
    }

    private static void registrar(Connection conn, Pendente p, String resultado) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.alerta_tratamento_execucao (id_alerta, tratamento, resultado) VALUES (?, ?, ?)")) {
            ps.setString(1, p.idAlerta.toString());
            ps.setString(2, p.tratamento);
            ps.setString(3, resultado.length() > 200 ? resultado.substring(0, 200) : resultado);
            ps.executeUpdate();
        }
    }

    private static final class Pendente {
        final UUID idAlerta;
        final String tratamento;

        Pendente(UUID idAlerta, String tratamento) {
            this.idAlerta = idAlerta;
            this.tratamento = tratamento;
        }
    }
}
