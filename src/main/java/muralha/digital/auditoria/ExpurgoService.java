package muralha.digital.auditoria;

import com.consilux.lib.Conexao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Expurgo configurável dos logs, conforme as políticas de {@code dbo.sis_log_retencao}.
 *
 * <p>Regras de segurança:
 * <ul>
 *   <li>Só remove eventos já <b>selados</b> ({@code selado=1}). Como o selo é
 *       diário e a retenção é de muitos dias, nunca se apaga evento recente nem
 *       se quebra a cadeia viva de verificação.</li>
 *   <li>Políticas com {@code hold_legal=1} bloqueiam o expurgo daquele nível.</li>
 *   <li>Quando {@code arquivar_antes=1}, exporta os eventos para um CSV + arquivo
 *       {@code .selo} (hash SHA-256 do CSV) antes de apagar, preservando a prova.</li>
 *   <li>O próprio expurgo é auditado em {@code dbo.sis_log_expurgo} e na trilha.</li>
 * </ul>
 */
public final class ExpurgoService {

    private static final Logger log = Logger.getLogger(ExpurgoService.class.getName());
    private static final int LOTE = 5000;

    private ExpurgoService() {}

    /** Diretório de arquivamento (configurável por -Dgtw.logs.expurgo.dir). */
    public static String diretorioArquivo() {
        String dir = System.getProperty("gtw.logs.expurgo.dir");
        if (dir == null || dir.trim().isEmpty()) {
            dir = System.getProperty("java.io.tmpdir") + File.separator + "gtw-expurgo-logs";
        }
        return dir;
    }

    /** Executa todas as políticas ativas. Se {@code simulacao=true}, apenas conta (não apaga). */
    public static JsonObject executar(String login, boolean simulacao) {
        JsonObject result = new JsonObject();
        JsonArray escopos = new JsonArray();
        long totalRemovido = 0;
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            List<Politica> politicas = carregarPoliticas(conn);

            // Níveis com política específica (para o escopo "padrão" não os incluir).
            List<String> niveisEspecificos = new ArrayList<>();
            for (Politica p : politicas) if (p.nivel != null) niveisEspecificos.add(p.nivel);

            for (Politica p : politicas) {
                JsonObject esc = new JsonObject();
                esc.addProperty("politica", p.id);
                esc.addProperty("nivel", p.nivel == null ? "(padrao)" : p.nivel);
                esc.addProperty("diasRetencao", p.dias);

                if (p.holdLegal) {
                    esc.addProperty("status", "RETIDO (hold legal)");
                    esc.addProperty("removidos", 0);
                    escopos.add(esc);
                    continue;
                }

                Timestamp cutoff = new Timestamp(System.currentTimeMillis() - (long) p.dias * 86400000L);
                CondicaoNivel cond = condicaoNivel(p, niveisEspecificos);

                long qtd = contar(conn, cutoff, cond);
                esc.addProperty("elegiveis", qtd);

                if (qtd == 0 || simulacao) {
                    esc.addProperty("status", simulacao ? "SIMULACAO" : "NADA A EXPURGAR");
                    esc.addProperty("removidos", 0);
                    escopos.add(esc);
                    continue;
                }

                String arquivo = null;
                if (p.arquivar) {
                    arquivo = arquivar(conn, cutoff, cond, p);
                    esc.addProperty("arquivo", arquivo);
                }

                long removido = apagar(conn, cutoff, cond);
                totalRemovido += removido;
                esc.addProperty("removidos", removido);
                esc.addProperty("status", "EXPURGADO");

                registrarExpurgo(conn, p, cutoff, removido, arquivo, login);
                escopos.add(esc);
            }

            if (!simulacao && totalRemovido > 0) {
                AuditoriaService.registrarSistema("Expurgo de Logs", "EXPURGO",
                        null, "Total de registros removidos: " + totalRemovido
                                + (login != null ? " (solicitado por " + login + ")" : ""));
            }

            result.addProperty("ok", true);
            result.addProperty("simulacao", simulacao);
            result.addProperty("totalRemovido", totalRemovido);
            result.add("escopos", escopos);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
            log.log(Level.SEVERE, "[MotorLogs] Falha no expurgo", e);
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return result;
    }

    // ------------------------------------------------------------

    private static List<Politica> carregarPoliticas(Connection conn) throws Exception {
        List<Politica> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, nivel, dias_retencao, arquivar_antes, hold_legal " +
                "FROM dbo.sis_log_retencao WHERE ativo = 1 ORDER BY CASE WHEN nivel IS NULL THEN 1 ELSE 0 END, id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Politica p = new Politica();
                p.id = rs.getInt("id");
                p.nivel = rs.getString("nivel");
                p.dias = rs.getInt("dias_retencao");
                p.arquivar = rs.getBoolean("arquivar_antes");
                p.holdLegal = rs.getBoolean("hold_legal");
                lista.add(p);
            }
        }
        return lista;
    }

    private static CondicaoNivel condicaoNivel(Politica p, List<String> niveisEspecificos) {
        CondicaoNivel c = new CondicaoNivel();
        if (p.nivel != null) {
            c.sql = "AND nivel = ? ";
            c.params.add(p.nivel);
        } else {
            // escopo padrão: níveis sem política específica (inclui null)
            if (niveisEspecificos.isEmpty()) {
                c.sql = "";
            } else {
                StringBuilder sb = new StringBuilder("AND (nivel IS NULL OR nivel NOT IN (");
                for (int i = 0; i < niveisEspecificos.size(); i++) {
                    sb.append(i == 0 ? "?" : ",?");
                    c.params.add(niveisEspecificos.get(i));
                }
                sb.append(")) ");
                c.sql = sb.toString();
            }
        }
        return c;
    }

    private static long contar(Connection conn, Timestamp cutoff, CondicaoNivel cond) throws Exception {
        String sql = "SELECT COUNT(*) FROM dbo.sis_log_auditoria " +
                     "WHERE selado = 1 AND dt_operacao < ? " + cond.sql;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setTimestamp(i++, cutoff);
            for (String p : cond.params) ps.setString(i++, p);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getLong(1) : 0; }
        }
    }

    private static long apagar(Connection conn, Timestamp cutoff, CondicaoNivel cond) throws Exception {
        String sql = "DELETE TOP (" + LOTE + ") FROM dbo.sis_log_auditoria " +
                     "WHERE selado = 1 AND dt_operacao < ? " + cond.sql;
        long total = 0;
        while (true) {
            int apagados;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int i = 1;
                ps.setTimestamp(i++, cutoff);
                for (String p : cond.params) ps.setString(i++, p);
                apagados = ps.executeUpdate();
            }
            total += apagados;
            if (apagados < LOTE) break;
        }
        return total;
    }

    private static String arquivar(Connection conn, Timestamp cutoff, CondicaoNivel cond, Politica p) throws Exception {
        File dir = new File(diretorioArquivo());
        if (!dir.exists()) dir.mkdirs();
        String carimbo = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nivel = p.nivel == null ? "PADRAO" : p.nivel;
        File csv = new File(dir, "expurgo_" + nivel + "_" + carimbo + ".csv");

        String sql = "SELECT id, dt_operacao, login, ip_terminal, funcionalidade, operacao, nivel, " +
                     "rota, status_http, id_registro, descricao, hash_anterior, hash_evento " +
                     "FROM dbo.sis_log_auditoria WHERE selado = 1 AND dt_operacao < ? " + cond.sql + " ORDER BY id";
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                new java.io.FileOutputStream(csv), StandardCharsets.UTF_8))) {
            w.write("id;dt_operacao;login;ip;funcionalidade;operacao;nivel;rota;status;id_registro;descricao;hash_anterior;hash_evento");
            w.write("\n");
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int i = 1;
                ps.setTimestamp(i++, cutoff);
                for (String par : cond.params) ps.setString(i++, par);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(rs.getLong("id")).append(';')
                          .append(c(String.valueOf(rs.getTimestamp("dt_operacao")))).append(';')
                          .append(c(rs.getString("login"))).append(';')
                          .append(c(rs.getString("ip_terminal"))).append(';')
                          .append(c(rs.getString("funcionalidade"))).append(';')
                          .append(c(rs.getString("operacao"))).append(';')
                          .append(c(rs.getString("nivel"))).append(';')
                          .append(c(rs.getString("rota"))).append(';')
                          .append(c(String.valueOf(rs.getObject("status_http")))).append(';')
                          .append(c(rs.getString("id_registro"))).append(';')
                          .append(c(rs.getString("descricao"))).append(';')
                          .append(c(rs.getString("hash_anterior"))).append(';')
                          .append(c(rs.getString("hash_evento")));
                        w.write(sb.toString());
                        w.write("\n");
                    }
                }
            }
        }

        // Selo do arquivo: SHA-256 do CSV, gravado em arquivo companheiro.
        String hash = sha256Arquivo(csv);
        File selo = new File(csv.getAbsolutePath() + ".selo");
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                new java.io.FileOutputStream(selo), StandardCharsets.UTF_8))) {
            w.write("arquivo=" + csv.getName() + "\n");
            w.write("sha256=" + hash + "\n");
            w.write("gerado_em=" + new Date() + "\n");
        }
        return csv.getAbsolutePath();
    }

    private static void registrarExpurgo(Connection conn, Politica p, Timestamp cutoff,
                                         long removido, String arquivo, String login) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO dbo.sis_log_expurgo (id_politica, nivel, intervalo_fim, qtd_removida, arquivo_gerado, login, observacao) " +
                "VALUES (?,?,?,?,?,?,?)")) {
            ps.setInt(1, p.id);
            if (p.nivel != null) ps.setString(2, p.nivel); else ps.setNull(2, java.sql.Types.VARCHAR);
            ps.setTimestamp(3, cutoff);
            ps.setLong(4, removido);
            if (arquivo != null) ps.setString(5, arquivo); else ps.setNull(5, java.sql.Types.VARCHAR);
            if (login != null) ps.setString(6, login); else ps.setNull(6, java.sql.Types.VARCHAR);
            ps.setString(7, "Retencao " + p.dias + " dias" + (p.arquivar ? " (arquivado)" : ""));
            ps.executeUpdate();
        }
    }

    private static String sha256Arquivo(File f) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (FileInputStream in = new FileInputStream(f)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) md.update(buf, 0, n);
        }
        byte[] d = md.digest();
        StringBuilder hex = new StringBuilder(64);
        for (byte b : d) hex.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
        return hex.toString();
    }

    private static String c(String s) {
        if (s == null) return "";
        return s.replace(';', ',').replace('\n', ' ').replace('\r', ' ');
    }

    private static final class Politica {
        int id; String nivel; int dias; boolean arquivar; boolean holdLegal;
    }
    private static final class CondicaoNivel {
        String sql = "";
        List<String> params = new ArrayList<>();
    }
}
