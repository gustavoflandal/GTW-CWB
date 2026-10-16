package muralha.digital.relatorio;

import com.consilux.lib.Conexao;
import com.google.gson.JsonArray;
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
import java.time.LocalDate;

public class RelatorioGerencialJob implements Job {

    private static final Logger logger = Logger.getLogger(RelatorioGerencialJob.class);

    @Override
    public void execute(JobExecutionContext ctx) {
        try {
            verificarEGerar();
        } catch (Exception e) {
            logger.error("Erro ao gerar relatório gerencial", e);
        }
    }

    private void verificarEGerar() throws Exception {
        LocalDate hoje = LocalDate.now();
        Connection conn = Conexao.getConexao();
        try {
            int diaGeracao = ConfigChaveValor.obterInt(conn, "RELATORIO_GERENCIAL_DIA", 1);
            if (hoje.getDayOfMonth() != diaGeracao) return;

            LocalDate inicioMes = hoje.minusMonths(1).withDayOfMonth(1);
            LocalDate fimMes = hoje.withDayOfMonth(1).minusDays(1);

            if (jaExiste(conn, "GERENCIAL", inicioMes, fimMes)) {
                logger.info("Relatório gerencial já existe para " + inicioMes + " a " + fimMes);
                return;
            }

            JsonObject dados = coletarDados(conn, inicioMes, fimMes);
            String titulo = "Relatório Gerencial — " + inicioMes.getMonth() + "/" + inicioMes.getYear();
            gravar(conn, "GERENCIAL", titulo, inicioMes, fimMes, dados.toString());

            AuditoriaService.registrarSistema("RELATORIO", "GERAR_GERENCIAL",
                    inicioMes + "/" + fimMes, "Relatório gerencial periódico gerado automaticamente");
            logger.info("Relatório gerencial gerado: " + inicioMes + " a " + fimMes);
        } finally {
            conn.close();
        }
    }

    static boolean jaExiste(Connection conn, String tipo, LocalDate inicio, LocalDate fim) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM muralha.relatorio_periodico " +
                "WHERE tipo = ? AND periodo_inicio = ? AND periodo_fim = ?")) {
            ps.setString(1, tipo);
            ps.setDate(2, java.sql.Date.valueOf(inicio));
            ps.setDate(3, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    static void gravar(Connection conn, String tipo, String titulo, LocalDate inicio,
                        LocalDate fim, String dadosJson) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.relatorio_periodico " +
                "(tipo, titulo, periodo_inicio, periodo_fim, dados_json, dt_geracao, gerado_por) " +
                "VALUES (?, ?, ?, ?, ?, GETDATE(), 'SISTEMA')")) {
            ps.setString(1, tipo);
            ps.setString(2, titulo);
            ps.setDate(3, java.sql.Date.valueOf(inicio));
            ps.setDate(4, java.sql.Date.valueOf(fim));
            ps.setString(5, dadosJson);
            ps.executeUpdate();
        }
    }

    private JsonObject coletarDados(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonObject dados = new JsonObject();

        dados.add("resumoKpis", resumoKpis(conn, inicio, fim));
        dados.add("comparacaoPeriodo", comparacaoPeriodo(conn, inicio, fim));
        dados.add("alertasAnaliticos", alertasAnaliticos(conn, inicio, fim));
        dados.add("volumeTrafego", volumeTrafego(conn, inicio, fim));
        dados.add("disponibilidade", disponibilidade(conn, inicio, fim));

        return dados;
    }

    private JsonArray resumoKpis(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT k.id, k.nome, k.unidade, " +
                "AVG(h.valor) AS media, MIN(h.valor) AS minimo, MAX(h.valor) AS maximo, " +
                "COUNT(h.id) AS amostras, " +
                "SUM(CASE WHEN h.status = 'OK' THEN 1 ELSE 0 END) AS ok_count, " +
                "SUM(CASE WHEN h.status = 'WARN' THEN 1 ELSE 0 END) AS warn_count, " +
                "SUM(CASE WHEN h.status = 'CRIT' THEN 1 ELSE 0 END) AS crit_count " +
                "FROM muralha.kpi_config k " +
                "JOIN muralha.kpi_historico h ON h.id_kpi = k.id " +
                "WHERE h.dt_coleta >= ? AND h.dt_coleta < DATEADD(DAY, 1, ?) " +
                "GROUP BY k.id, k.nome, k.unidade ORDER BY k.id")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject kpi = new JsonObject();
                    kpi.addProperty("id", rs.getInt("id"));
                    kpi.addProperty("nome", rs.getString("nome"));
                    kpi.addProperty("unidade", rs.getString("unidade"));
                    kpi.addProperty("media", rs.getDouble("media"));
                    kpi.addProperty("minimo", rs.getDouble("minimo"));
                    kpi.addProperty("maximo", rs.getDouble("maximo"));
                    kpi.addProperty("amostras", rs.getInt("amostras"));
                    kpi.addProperty("okCount", rs.getInt("ok_count"));
                    kpi.addProperty("warnCount", rs.getInt("warn_count"));
                    kpi.addProperty("critCount", rs.getInt("crit_count"));
                    arr.add(kpi);
                }
            }
        }
        return arr;
    }

    private JsonObject comparacaoPeriodo(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonObject obj = new JsonObject();
        LocalDate inicioAnterior = inicio.minusMonths(1);
        LocalDate fimAnterior = inicio.minusDays(1);

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT k.id, k.nome, " +
                "AVG(CASE WHEN h.dt_coleta >= ? AND h.dt_coleta < DATEADD(DAY,1,?) THEN h.valor END) AS media_atual, " +
                "AVG(CASE WHEN h.dt_coleta >= ? AND h.dt_coleta < DATEADD(DAY,1,?) THEN h.valor END) AS media_anterior " +
                "FROM muralha.kpi_config k " +
                "JOIN muralha.kpi_historico h ON h.id_kpi = k.id " +
                "WHERE h.dt_coleta >= ? AND h.dt_coleta < DATEADD(DAY,1,?) " +
                "GROUP BY k.id, k.nome ORDER BY k.id")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            ps.setDate(3, java.sql.Date.valueOf(inicioAnterior));
            ps.setDate(4, java.sql.Date.valueOf(fimAnterior));
            ps.setDate(5, java.sql.Date.valueOf(inicioAnterior));
            ps.setDate(6, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                JsonArray kpis = new JsonArray();
                while (rs.next()) {
                    JsonObject kpi = new JsonObject();
                    kpi.addProperty("id", rs.getInt("id"));
                    kpi.addProperty("nome", rs.getString("nome"));
                    kpi.addProperty("mediaAtual", rs.getDouble("media_atual"));
                    kpi.addProperty("mediaAnterior", rs.getDouble("media_anterior"));
                    kpis.add(kpi);
                }
                obj.add("kpis", kpis);
            }
        }
        obj.addProperty("periodoAtual", inicio + " a " + fim);
        obj.addProperty("periodoAnterior", inicioAnterior + " a " + fimAnterior);
        return obj;
    }

    private JsonArray alertasAnaliticos(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT funcionalidade, operacao, id_registro, descricao, data " +
                "FROM muralha.sis_log_auditoria " +
                "WHERE funcionalidade = 'KPI' AND operacao = 'ALERTA_ANALITICO' " +
                "AND data >= ? AND data < DATEADD(DAY, 1, ?) " +
                "ORDER BY data DESC")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject alerta = new JsonObject();
                    alerta.addProperty("idRegistro", rs.getString("id_registro"));
                    alerta.addProperty("descricao", rs.getString("descricao"));
                    alerta.addProperty("data", rs.getString("data"));
                    arr.add(alerta);
                }
            }
        }
        return arr;
    }

    private JsonObject volumeTrafego(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonObject obj = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) AS total_passagens, " +
                "COUNT(DISTINCT id_local) AS locais_ativos, " +
                "AVG(CAST(trafego AS FLOAT)) AS trafego_medio_hora " +
                "FROM dbo.veiculo_sumarizado " +
                "WHERE data >= ? AND data <= ?")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    obj.addProperty("totalPassagens", rs.getLong("total_passagens"));
                    obj.addProperty("locaisAtivos", rs.getInt("locais_ativos"));
                    obj.addProperty("trafegoMedioHora", rs.getDouble("trafego_medio_hora"));
                }
            }
        }
        return obj;
    }

    private JsonObject disponibilidade(Connection conn, LocalDate inicio, LocalDate fim) throws SQLException {
        JsonObject obj = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(DISTINCT id_local) AS equipamentos_ativos " +
                "FROM dbo.veiculo_sumarizado " +
                "WHERE data >= ? AND data <= ? AND trafego > 0")) {
            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fim));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) obj.addProperty("equipamentosAtivos", rs.getInt("equipamentos_ativos"));
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(DISTINCT id_local) AS total_equipamentos " +
                "FROM dbo.local_vigente WHERE ativo = 1")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) obj.addProperty("totalEquipamentos", rs.getInt("total_equipamentos"));
            }
        }
        return obj;
    }
}
