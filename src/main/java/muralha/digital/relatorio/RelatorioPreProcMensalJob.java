package muralha.digital.relatorio;

import com.consilux.lib.Conexao;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;
import muralha.digital.util.ConfigChaveValor;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class RelatorioPreProcMensalJob implements Job {

    private static final Logger logger = Logger.getLogger(RelatorioPreProcMensalJob.class);

    @Override
    public void execute(JobExecutionContext ctx) {
        try {
            verificarEGerar();
        } catch (Exception e) {
            logger.error("Erro ao gerar relatório mensal de pré-processamento", e);
        }
    }

    private void verificarEGerar() throws Exception {
        LocalDate hoje = LocalDate.now();
        Connection conn = Conexao.getConexao();
        try {
            int diaUtilAlvo = ConfigChaveValor.obterInt(conn, "RELATORIO_PREPROC_DIA_UTIL", 5);
            if (!ehDiaUtilAlvo(hoje, diaUtilAlvo)) return;

            LocalDate inicioMes = hoje.minusMonths(1).withDayOfMonth(1);
            LocalDate fimMes = hoje.withDayOfMonth(1).minusDays(1);

            if (RelatorioGerencialJob.jaExiste(conn, "PRE_PROCESSAMENTO", inicioMes, fimMes)) {
                logger.info("Relatório de pré-processamento já existe para " + inicioMes + " a " + fimMes);
                return;
            }

            JsonObject dados = coletarMetricas(conn, inicioMes, fimMes);
            String titulo = "Relatório Mensal de Pré-Processamento — " + inicioMes.getMonth() + "/" + inicioMes.getYear();
            RelatorioGerencialJob.gravar(conn, "PRE_PROCESSAMENTO", titulo, inicioMes, fimMes, dados.toString());

            AuditoriaService.registrarSistema("RELATORIO", "GERAR_PREPROC_MENSAL",
                    inicioMes + "/" + fimMes, "Relatório mensal de pré-processamento gerado automaticamente");
            logger.info("Relatório mensal de pré-processamento gerado: " + inicioMes + " a " + fimMes);
        } finally {
            conn.close();
        }
    }

    static boolean ehDiaUtilAlvo(LocalDate data, int diaUtilAlvo) {
        LocalDate d = data.withDayOfMonth(1);
        int diasUteis = 0;
        while (!d.isAfter(data)) {
            DayOfWeek dow = d.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                diasUteis++;
                if (diasUteis == diaUtilAlvo && d.equals(data)) return true;
                if (diasUteis > diaUtilAlvo) return false;
            }
            d = d.plusDays(1);
        }
        return false;
    }

    private JsonObject coletarMetricas(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonObject dados = new JsonObject();

        // (a-d) Volumes: recebidos, processados, disponibilizados, descartados
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT " +
                "COUNT(*) AS recebidos, " +
                "SUM(CASE WHEN vsa.status_analise IS NOT NULL AND vsa.status_analise <> 'AGUARDANDO_ANALISE' THEN 1 ELSE 0 END) AS processados, " +
                "SUM(CASE WHEN vsa.status_analise IN ('PRE_APROVADA','APROVADA') THEN 1 ELSE 0 END) AS disponibilizados, " +
                "SUM(CASE WHEN vsa.status_analise = 'DESCARTADA' THEN 1 ELSE 0 END) AS descartados " +
                "FROM muralha.veiculo_tempo_real vtr " +
                "LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = vtr.id " +
                "WHERE vtr.data >= ? AND vtr.data < DATEADD(DAY, 1, ?)")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long recebidos = rs.getLong("recebidos");
                    long processados = rs.getLong("processados");
                    long disponibilizados = rs.getLong("disponibilizados");
                    long descartados = rs.getLong("descartados");

                    dados.addProperty("recebidos", recebidos);
                    dados.addProperty("processados", processados);
                    dados.addProperty("disponibilizados", disponibilizados);
                    dados.addProperty("descartados", descartados);

                    // (e) Índice de aproveitamento
                    double aproveitamento = recebidos == 0 ? 100.0
                            : Math.round((double) disponibilizados / recebidos * 10000.0) / 100.0;
                    dados.addProperty("indiceAproveitamento", aproveitamento);
                }
            }
        }

        // (f) Tempo médio de processamento por registro
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT AVG(DATEDIFF(SECOND, vtr.data, vsa.data_analise)) AS tempo_medio_seg " +
                "FROM muralha.veiculo_tempo_real vtr " +
                "JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = vtr.id " +
                "WHERE vtr.data >= ? AND vtr.data < DATEADD(DAY, 1, ?) " +
                "AND vsa.data_analise IS NOT NULL")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) dados.addProperty("tempoMedioProcessamentoSeg", rs.getDouble("tempo_medio_seg"));
            }
        }

        // (g) Produtividade por operador
        dados.add("produtividadePorOperador", produtividadePorOperador(conn, inicio, fim));

        // (h) Taxa de divergência (operadores que reclassificaram)
        dados.add("divergencia", divergencia(conn, inicio, fim));

        // (i) Taxa de retrabalho
        dados.add("retrabalho", retrabalho(conn, inicio, fim));

        // (j-k) Pendentes e backlog
        dados.add("pendentesEBacklog", pendentesEBacklog(conn));

        // (l) SLA
        dados.add("sla", sla(conn, inicio, fim));

        // (m) Motivos de descarte
        dados.add("motivosDescarte", motivosDescarte(conn, inicio, fim));

        // (n) Histórico de classificação
        dados.add("historicoClassificacao", historicoClassificacao(conn, inicio, fim));

        return dados;
    }

    // (g) Produtividade por operador
    private JsonObject produtividadePorOperador(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonObject obj = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT vsa.id_usuario, u.nome, COUNT(*) AS total_analisados " +
                "FROM muralha.vtr_status_analise vsa " +
                "JOIN muralha.veiculo_tempo_real vtr ON vtr.id = vsa.id_veiculo_tempo_real " +
                "LEFT JOIN dbo.sis_usuario u ON u.id_usuario = vsa.id_usuario " +
                "WHERE vtr.data >= ? AND vtr.data < DATEADD(DAY, 1, ?) " +
                "AND vsa.id_usuario IS NOT NULL " +
                "GROUP BY vsa.id_usuario, u.nome ORDER BY total_analisados DESC")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                int total = 0;
                int operadores = 0;
                while (rs.next()) {
                    total += rs.getInt("total_analisados");
                    operadores++;
                }
                obj.addProperty("totalAnalisados", total);
                obj.addProperty("operadores", operadores);
                obj.addProperty("mediaPorOperador", operadores == 0 ? 0 : total / operadores);
            }
        }
        return obj;
    }

    // (h) Taxa de divergência
    private JsonObject divergencia(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonObject obj = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) AS total, " +
                "SUM(CASE WHEN vsa.resultado_ia <> vsa.status_analise THEN 1 ELSE 0 END) AS divergentes " +
                "FROM muralha.vtr_status_analise vsa " +
                "JOIN muralha.veiculo_tempo_real vtr ON vtr.id = vsa.id_veiculo_tempo_real " +
                "WHERE vtr.data >= ? AND vtr.data < DATEADD(DAY, 1, ?) " +
                "AND vsa.resultado_ia IS NOT NULL AND vsa.status_analise IS NOT NULL")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int divergentes = rs.getInt("divergentes");
                    obj.addProperty("total", total);
                    obj.addProperty("divergentes", divergentes);
                    obj.addProperty("taxa", total == 0 ? 0 : Math.round((double) divergentes / total * 10000.0) / 100.0);
                }
            }
        }
        return obj;
    }

    // (i) Taxa de retrabalho
    private JsonObject retrabalho(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonObject obj = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) AS total, " +
                "SUM(CASE WHEN vsa.revisado = 1 THEN 1 ELSE 0 END) AS retrabalhados " +
                "FROM muralha.vtr_status_analise vsa " +
                "JOIN muralha.veiculo_tempo_real vtr ON vtr.id = vsa.id_veiculo_tempo_real " +
                "WHERE vtr.data >= ? AND vtr.data < DATEADD(DAY, 1, ?)")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int retrabalhados = rs.getInt("retrabalhados");
                    obj.addProperty("total", total);
                    obj.addProperty("retrabalhados", retrabalhados);
                    obj.addProperty("taxa", total == 0 ? 0 : Math.round((double) retrabalhados / total * 10000.0) / 100.0);
                }
            }
        }
        return obj;
    }

    // (j-k) Pendentes e backlog
    private JsonObject pendentesEBacklog(Connection conn) throws SQLException {
        JsonObject obj = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) AS pendentes, " +
                "SUM(CASE WHEN DATEDIFF(HOUR, vtr.data, SYSDATETIME()) > 72 THEN 1 ELSE 0 END) AS backlog_72h " +
                "FROM muralha.veiculo_tempo_real vtr " +
                "LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = vtr.id " +
                "WHERE COALESCE(vsa.status_analise, 'AGUARDANDO_ANALISE') = 'AGUARDANDO_ANALISE'")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    obj.addProperty("pendentes", rs.getLong("pendentes"));
                    obj.addProperty("backlog72h", rs.getLong("backlog_72h"));
                }
            }
        }
        return obj;
    }

    // (l) Cumprimento SLA
    private JsonObject sla(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonObject obj = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) AS janelas, " +
                "SUM(CASE WHEN violacao = 1 THEN 1 ELSE 0 END) AS violacoes " +
                "FROM muralha.alerta_sla " +
                "WHERE janela_inicio >= ? AND janela_fim < DATEADD(DAY, 1, ?)")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int janelas = rs.getInt("janelas");
                    int violacoes = rs.getInt("violacoes");
                    obj.addProperty("janelas", janelas);
                    obj.addProperty("violacoes", violacoes);
                    obj.addProperty("cumprimento", janelas == 0 ? 100.0 : Math.round((double) (janelas - violacoes) / janelas * 10000.0) / 100.0);
                }
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT AVG(CAST(total_pendentes AS FLOAT)) AS media_pendentes, " +
                "AVG(CAST(acima_72h AS FLOAT)) AS media_acima_72h " +
                "FROM muralha.alerta_sla_preproc " +
                "WHERE dt_alerta >= ? AND dt_alerta < DATEADD(DAY, 1, ?)")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    obj.addProperty("mediaPendentes", rs.getDouble("media_pendentes"));
                    obj.addProperty("mediaAcima72h", rs.getDouble("media_acima_72h"));
                }
            }
        }
        return obj;
    }

    // (m) Motivos de descarte
    private JsonObject motivosDescarte(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonObject obj = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COALESCE(vsa.motivo_descarte, 'Sem motivo') AS motivo, COUNT(*) AS total " +
                "FROM muralha.vtr_status_analise vsa " +
                "JOIN muralha.veiculo_tempo_real vtr ON vtr.id = vsa.id_veiculo_tempo_real " +
                "WHERE vsa.status_analise = 'DESCARTADA' " +
                "AND vtr.data >= ? AND vtr.data < DATEADD(DAY, 1, ?) " +
                "GROUP BY vsa.motivo_descarte ORDER BY total DESC")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    obj.addProperty(rs.getString("motivo"), rs.getInt("total"));
                }
            }
        }
        return obj;
    }

    // (n) Histórico de classificação
    private JsonObject historicoClassificacao(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonObject obj = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COALESCE(vsa.status_analise, 'AGUARDANDO_ANALISE') AS classificacao, COUNT(*) AS total " +
                "FROM muralha.vtr_status_analise vsa " +
                "JOIN muralha.veiculo_tempo_real vtr ON vtr.id = vsa.id_veiculo_tempo_real " +
                "WHERE vtr.data >= ? AND vtr.data < DATEADD(DAY, 1, ?) " +
                "GROUP BY vsa.status_analise ORDER BY total DESC")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    obj.addProperty(rs.getString("classificacao"), rs.getInt("total"));
                }
            }
        }
        return obj;
    }
}
