package muralha.digital.acesso;

import com.consilux.lib.Conexao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import muralha.digital.util.HashUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public final class SenhaService {

    private SenhaService() {}

    // ── Configuração ──────────────────────────────────────────────────────────

    public static int getConfig(String chave, int padrao) {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT valor FROM dbo.sis_senha_config WHERE chave = ?")) {
                ps.setString(1, chave);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return Integer.parseInt(rs.getString("valor").trim());
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return padrao;
    }

    // ── Validação de complexidade ─────────────────────────────────────────────

    /** Retorna null se válida; mensagem de erro se inválida. */
    public static String validarComplexidade(String senha) {
        if (senha == null || senha.isEmpty()) return "Senha não pode ser vazia.";
        int min = getConfig("min_tamanho", 8);
        if (senha.length() < min) return "Senha deve ter pelo menos " + min + " caracteres.";
        if (getConfig("requer_maiuscula", 1) == 1 && !senha.matches(".*[A-Z].*"))
            return "Senha deve conter pelo menos uma letra maiúscula.";
        if (getConfig("requer_numero", 1) == 1 && !senha.matches(".*[0-9].*"))
            return "Senha deve conter pelo menos um número.";
        if (getConfig("requer_especial", 1) == 1 && !senha.matches(".*[!@#$%^&*()_+\\-=].*"))
            return "Senha deve conter pelo menos um caractere especial (!@#$%^&*()_+-=).";
        return null;
    }

    // ── Histórico de reutilização ─────────────────────────────────────────────

    public static boolean jaUsouRecentemente(int idUsuario, String novaSenha) {
        int qtde = getConfig("historico_qtde", 5);
        if (qtde == 0) return false;
        String novoHash = HashUtil.sha256Hex(novaSenha.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT senha_hash, senha_historico FROM dbo.sis_usuario WHERE id = ?")) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String atual = rs.getString("senha_hash");
                        if (novoHash.equals(atual)) return true;
                        String histJson = rs.getString("senha_historico");
                        if (histJson != null && !histJson.isEmpty()) {
                            List<String> hist = new Gson().fromJson(histJson,
                                new TypeToken<List<String>>(){}.getType());
                            int checar = Math.min(qtde - 1, hist.size());
                            for (int i = 0; i < checar; i++) {
                                if (novoHash.equals(hist.get(hist.size() - 1 - i))) return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return false;
    }

    // ── Atualizar senha ───────────────────────────────────────────────────────

    public static void atualizarSenha(int idUsuario, String novaSenha) throws Exception {
        String novoHash = HashUtil.sha256Hex(novaSenha.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String histJson = "[]";
            String hashAtual = null;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT senha_hash, senha_historico FROM dbo.sis_usuario WHERE id = ?")) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        hashAtual = rs.getString("senha_hash");
                        String h = rs.getString("senha_historico");
                        if (h != null && !h.isEmpty()) histJson = h;
                    }
                }
            }
            List<String> hist = new Gson().fromJson(histJson,
                new TypeToken<List<String>>(){}.getType());
            if (hashAtual != null) hist.add(hashAtual);
            int max = getConfig("historico_qtde", 5);
            while (hist.size() > max) hist.remove(0);

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE dbo.sis_usuario SET senha_hash=?, senha_historico=?, " +
                    "dt_ultima_troca_senha=CAST(SYSDATETIME() AS DATE), fl_troca_obrigatoria=0, " +
                    "tentativas_invalidas=0, bloqueado_ate=NULL WHERE id=?")) {
                ps.setString(1, novoHash);
                ps.setString(2, new Gson().toJson(hist));
                ps.setInt(3, idUsuario);
                ps.executeUpdate();
            }
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
    }

    // ── Verificar bloqueio ────────────────────────────────────────────────────

    /** Retorna null se desbloqueado; mensagem de erro se bloqueado. */
    public static String verificarBloqueio(String login) throws Exception {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT bloqueado_ate FROM dbo.sis_usuario WHERE login=?")) {
                ps.setString(1, login);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp bloqueado = rs.getTimestamp("bloqueado_ate");
                        if (bloqueado != null && bloqueado.after(new java.util.Date()))
                            return "Usuário bloqueado até " + bloqueado + ". Contate o administrador.";
                    }
                }
            }
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return null;
    }

    // ── Registrar falha de autenticação ───────────────────────────────────────

    public static void registrarFalha(String login) throws Exception {
        int max = getConfig("max_tentativas", 5);
        int min = getConfig("bloqueio_minutos", 30);
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String sql = min > 0
                ? "UPDATE dbo.sis_usuario SET tentativas_invalidas = tentativas_invalidas+1, " +
                  "bloqueado_ate = CASE WHEN tentativas_invalidas+1 >= " + max +
                  " THEN DATEADD(MINUTE," + min + ",SYSDATETIME()) ELSE bloqueado_ate END WHERE login=?"
                : "UPDATE dbo.sis_usuario SET tentativas_invalidas = tentativas_invalidas+1, " +
                  "bloqueado_ate = CASE WHEN tentativas_invalidas+1 >= " + max +
                  " THEN CAST('9999-12-31' AS DATETIME2) ELSE bloqueado_ate END WHERE login=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, login);
                ps.executeUpdate();
            }
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
    }

    // ── Verificar expiração de senha ──────────────────────────────────────────

    public static boolean senhaExpirada(int idUsuario) {
        int validade = getConfig("validade_dias", 90);
        if (validade == 0) return false;
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT dt_ultima_troca_senha FROM dbo.sis_usuario WHERE id=?")) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Date dt = rs.getDate("dt_ultima_troca_senha");
                        if (dt == null) return true;
                        long dias = (System.currentTimeMillis() - dt.getTime()) / 86400000L;
                        return dias >= validade;
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return false;
    }

    // ── Zerar tentativas após login bem-sucedido ──────────────────────────────

    public static void zerarTentativas(String login) {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE dbo.sis_usuario SET tentativas_invalidas=0, bloqueado_ate=NULL WHERE login=?")) {
                ps.setString(1, login);
                ps.executeUpdate();
            }
        } catch (Exception ignored) {
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
    }
}
