package muralha.digital.chamado;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Parametros, perfis e cadastros do Sistema de Chamados. */
public final class ChamadoConfigDao {

    private static final String SQL_CATEGORIAS =
            "SELECT c.id, c.nome, c.id_pai, p.nome AS nome_pai, c.ativo, " +
            "CAST(CASE WHEN c.id_pai IS NULL THEN c.exige_equipamento ELSE p.exige_equipamento END AS BIT) AS exige " +
            "FROM muralha.chamado_categoria c LEFT JOIN muralha.chamado_categoria p ON p.id = c.id_pai ";

    private static final String CHAVE_RISCO = "chamado_sla_risco_percentual";
    private static final String CHAVE_FECHAMENTO = "chamado_fechamento_auto_dias";
    public static final String CHAVE_EMAIL_ATIVO = "chamado_email_ativo";

    private ChamadoConfigDao() {
    }

    public static String valor(Connection conn, String chave, String padrao) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT valor FROM muralha.config_chave_valor WHERE chave = ?")) {
            ps.setString(1, chave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String v = rs.getString(1);
                    if (v != null && !v.trim().isEmpty()) return v.trim();
                }
            }
        }
        return padrao;
    }

    public static int valorInt(Connection conn, String chave, int padrao) throws SQLException {
        try {
            return Integer.parseInt(valor(conn, chave, String.valueOf(padrao)));
        } catch (NumberFormatException e) {
            return padrao;
        }
    }

    public static Set<PerfilChamado> perfis(Connection conn, int idUsuario) throws SQLException {
        Map<Integer, PerfilChamado> porGrupo = new HashMap<>();
        mapear(conn, porGrupo, "chamado_grupo_solicitante", PerfilChamado.SOLICITANTE);
        mapear(conn, porGrupo, "chamado_grupo_gestor", PerfilChamado.GESTOR);
        mapear(conn, porGrupo, "chamado_grupo_equipe", PerfilChamado.EQUIPE);
        mapear(conn, porGrupo, "chamado_grupo_admin", PerfilChamado.ADMIN);
        Set<PerfilChamado> perfis = EnumSet.noneOf(PerfilChamado.class);
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_grupo FROM dbo.sis_usuario_grupo WHERE id_usuario = ?")) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PerfilChamado p = porGrupo.get(rs.getInt(1));
                    if (p != null) perfis.add(p);
                }
            }
        }
        return perfis;
    }

    private static void mapear(Connection conn, Map<Integer, PerfilChamado> mapa, String chave, PerfilChamado perfil)
            throws SQLException {
        int idGrupo = valorInt(conn, chave, -1);
        if (idGrupo > 0) mapa.put(idGrupo, perfil);
    }

    private static String chaveGrupo(PerfilChamado perfil) {
        switch (perfil) {
            case SOLICITANTE: return "chamado_grupo_solicitante";
            case GESTOR: return "chamado_grupo_gestor";
            case EQUIPE: return "chamado_grupo_equipe";
            default: return "chamado_grupo_admin";
        }
    }

    /** Limites dos parametros de SLA (pura, testavel sem banco). */
    static void validarParametrosSla(int riscoPercentual, int fechamentoAutoDias) {
        if (riscoPercentual < 1 || riscoPercentual > 99) {
            throw new RegraChamadoException("O percentual de risco deve estar entre 1 e 99.");
        }
        if (fechamentoAutoDias < 1 || fechamentoAutoDias > 60) {
            throw new RegraChamadoException("O prazo de fechamento automático deve estar entre 1 e 60 dias.");
        }
    }

    public static JsonObject parametrosSla(Connection conn) throws SQLException {
        JsonObject o = new JsonObject();
        o.addProperty("riscoPercentual", valorInt(conn, CHAVE_RISCO, 80));
        o.addProperty("fechamentoAutoDias", valorInt(conn, CHAVE_FECHAMENTO, 5));
        return o;
    }

    /** Atualiza os parametros e registra cada mudanca no historico, na mesma transacao. */
    public static void salvarParametrosSla(Connection conn, int riscoPercentual, int fechamentoAutoDias,
                                           int idUsuario) throws SQLException {
        validarParametrosSla(riscoPercentual, fechamentoAutoDias);
        boolean autoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            atualizarParametro(conn, CHAVE_RISCO, 80, riscoPercentual, idUsuario);
            atualizarParametro(conn, CHAVE_FECHAMENTO, 5, fechamentoAutoDias, idUsuario);
            conn.commit();
        } catch (SQLException | RuntimeException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    private static void atualizarParametro(Connection conn, String chave, int padrao, int novo, int idUsuario)
            throws SQLException {
        int antigo = valorInt(conn, chave, padrao);
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE muralha.config_chave_valor SET valor = ? WHERE chave = ?")) {
            ps.setString(1, String.valueOf(novo));
            ps.setString(2, chave);
            if (ps.executeUpdate() == 0) {
                throw new RegraChamadoException("Parâmetro \"" + chave + "\" não cadastrado.");
            }
        }
        if (antigo == novo) return;
        registrarHistorico(conn, idUsuario, chave + ": " + antigo + " -> " + novo);
    }

    /** Configuracao do SMTP para a tela; a senha nunca sai, so se esta definida. */
    public static JsonObject configuracaoSmtp(Connection conn) throws SQLException {
        String host = valor(conn, ConfiguracaoSmtpChamado.CHAVE_HOST, "");
        JsonObject o = new JsonObject();
        o.addProperty("host", host);
        o.addProperty("porta", valorInt(conn, ConfiguracaoSmtpChamado.CHAVE_PORTA, ConfiguracaoSmtpChamado.PORTA_PADRAO));
        o.addProperty("usuario", valor(conn, ConfiguracaoSmtpChamado.CHAVE_USUARIO, ""));
        o.addProperty("remetente", valor(conn, ConfiguracaoSmtpChamado.CHAVE_REMETENTE, ""));
        o.addProperty("starttls", !"0".equals(valor(conn, ConfiguracaoSmtpChamado.CHAVE_STARTTLS, "1")));
        o.addProperty("emailAtivo", "1".equals(valor(conn, CHAVE_EMAIL_ATIVO, "1")));
        o.addProperty("senhaDefinida", !valor(conn, ConfiguracaoSmtpChamado.CHAVE_SENHA, "").isEmpty());
        o.addProperty("chaveDisponivel", CifraSegredo.chaveDoAmbiente() != null);
        o.addProperty("origemAtual", host.isEmpty() ? ConfiguracaoSmtpChamado.ORIGEM_CONFGTW
                : ConfiguracaoSmtpChamado.ORIGEM_CHAMADOS);
        return o;
    }

    /** Dados do formulario de SMTP. senha vazia = manter a atual; limparSenha = apagar. */
    public static final class DadosSmtp {
        public String host;
        public int porta;
        public String usuario;
        public String senha;
        public boolean limparSenha;
        public String remetente;
        public boolean starttls;
        public boolean emailAtivo;
    }

    /**
     * Valida, cifra a senha nova e grava as chaves + historico (sem a senha) numa transacao.
     * Retorna as chaves alteradas (a da senha aparece como chamado_smtp_senha, sem valor).
     */
    public static List<String> salvarSmtp(Connection conn, DadosSmtp d, int idUsuario) throws SQLException {
        String host = d.host == null ? "" : d.host.trim();
        String usuario = d.usuario == null ? "" : d.usuario.trim();
        String remetente = d.remetente == null ? "" : d.remetente.trim();
        ConfiguracaoSmtpChamado.validar(host, d.porta, remetente, usuario);
        String senhaNova = null; // null = nao mexe
        if (d.limparSenha) {
            senhaNova = "";
        } else if (d.senha != null && !d.senha.isEmpty()) {
            if (d.senha.length() > CifraSegredo.MAX_CARACTERES) {
                throw new RegraChamadoException("A senha do SMTP deve ter no máximo " + CifraSegredo.MAX_CARACTERES + " caracteres.");
            }
            byte[] chave = CifraSegredo.chaveDoAmbiente();
            if (chave == null) {
                throw new RegraChamadoException("Defina a variável " + CifraSegredo.VARIAVEL_CHAVE
                        + " no servidor para gravar a senha.");
            }
            try {
                senhaNova = CifraSegredo.cifrar(d.senha, chave);
            } catch (IllegalArgumentException e) {
                throw new RegraChamadoException("A senha do SMTP é longa demais para ser gravada.");
            }
        }
        String porta = host.isEmpty() && d.porta == 0 ? "" : String.valueOf(d.porta);

        Map<String, String> novos = new LinkedHashMap<>();
        novos.put(ConfiguracaoSmtpChamado.CHAVE_HOST, host);
        novos.put(ConfiguracaoSmtpChamado.CHAVE_PORTA, porta);
        novos.put(ConfiguracaoSmtpChamado.CHAVE_USUARIO, usuario);
        novos.put(ConfiguracaoSmtpChamado.CHAVE_REMETENTE, remetente);
        novos.put(ConfiguracaoSmtpChamado.CHAVE_STARTTLS, d.starttls ? "1" : "0");
        novos.put(CHAVE_EMAIL_ATIVO, d.emailAtivo ? "1" : "0");
        if (senhaNova != null) novos.put(ConfiguracaoSmtpChamado.CHAVE_SENHA, senhaNova);

        List<String> alteradas = new ArrayList<>();
        boolean autoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            for (Map.Entry<String, String> e : novos.entrySet()) {
                String chave = e.getKey();
                String antigo = valor(conn, chave, "");
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE muralha.config_chave_valor SET valor = ? WHERE chave = ?")) {
                    ps.setString(1, e.getValue());
                    ps.setString(2, chave);
                    if (ps.executeUpdate() == 0) {
                        throw new RegraChamadoException("Parâmetro \"" + chave + "\" não cadastrado.");
                    }
                }
                boolean ehSenha = ConfiguracaoSmtpChamado.CHAVE_SENHA.equals(chave);
                // a senha cifrada muda a cada gravacao (IV aleatorio): qualquer senha informada conta como alteracao
                if (!ehSenha && antigo.equals(e.getValue())) continue;
                if (ehSenha && antigo.isEmpty() && e.getValue().isEmpty()) continue;
                alteradas.add(chave);
                String evento = ehSenha
                        ? chave + ": " + (e.getValue().isEmpty() ? "removida" : "alterada")
                        : chave + ": " + antigo + " -> " + e.getValue();
                registrarHistorico(conn, idUsuario, evento);
            }
            conn.commit();
        } catch (SQLException | RuntimeException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
        return alteradas;
    }

    private static void registrarHistorico(Connection conn, int idUsuario, String evento) throws SQLException {
        if (evento.length() > 255) evento = evento.substring(0, 255);
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.config_chave_valor_hist (id_usuario, data_atualizacao, evento) " +
                "VALUES (?, GETDATE(), ?)")) {
            ps.setInt(1, idUsuario);
            ps.setString(2, evento);
            ps.executeUpdate();
        }
    }

    public static JsonArray membrosPerfis(Connection conn) throws SQLException {
        JsonArray arr = new JsonArray();
        for (PerfilChamado perfil : PerfilChamado.values()) {
            int idGrupo = valorInt(conn, chaveGrupo(perfil), -1);
            if (idGrupo <= 0) continue;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT u.id_usuario, RTRIM(u.nome) AS nome, RTRIM(u.usuario) AS login, " +
                    "RTRIM(u.email) AS email, u.ativo " +
                    "FROM dbo.sis_usuario_grupo ug JOIN dbo.sis_usuario u ON u.id_usuario = ug.id_usuario " +
                    "WHERE ug.id_grupo = ? ORDER BY u.nome")) {
                ps.setInt(1, idGrupo);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject o = new JsonObject();
                        o.addProperty("perfil", perfil.name());
                        o.addProperty("idUsuario", rs.getInt("id_usuario"));
                        o.addProperty("nome", rs.getString("nome"));
                        o.addProperty("login", rs.getString("login"));
                        o.addProperty("email", rs.getString("email"));
                        o.addProperty("ativo", rs.getBoolean("ativo"));
                        arr.add(o);
                    }
                }
            }
        }
        return arr;
    }

    /** Ate 20 usuarios ativos cujo nome ou login contem o termo. */
    public static JsonArray buscarUsuarios(Connection conn, String termo) throws SQLException {
        JsonArray arr = new JsonArray();
        String t = termo == null ? "" : termo.trim();
        if (t.isEmpty()) return arr;
        String padrao = "%" + t.replace("[", "[[]").replace("%", "[%]").replace("_", "[_]") + "%";
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP 20 id_usuario, RTRIM(nome) AS nome, RTRIM(usuario) AS login FROM dbo.sis_usuario " +
                "WHERE ativo = 1 AND (nome LIKE ? OR usuario LIKE ?) ORDER BY nome")) {
            ps.setString(1, padrao);
            ps.setString(2, padrao);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("idUsuario", rs.getInt("id_usuario"));
                    o.addProperty("nome", rs.getString("nome"));
                    o.addProperty("login", rs.getString("login"));
                    arr.add(o);
                }
            }
        }
        return arr;
    }

    /** "Nome (login)" do usuario, ou null se nao existir. */
    public static String nomeLogin(Connection conn, int idUsuario) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT RTRIM(nome), RTRIM(usuario) FROM dbo.sis_usuario WHERE id_usuario = ?")) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) + " (" + rs.getString(2) + ")" : null;
            }
        }
    }

    private static int grupoGerenciavel(Connection conn, PerfilChamado perfil) throws SQLException {
        if (perfil == null || perfil == PerfilChamado.ADMIN) {
            throw new RegraChamadoException("Este perfil não é gerenciado por aqui.");
        }
        int idGrupo = valorInt(conn, chaveGrupo(perfil), -1);
        if (idGrupo <= 0) throw new RegraChamadoException("Grupo do perfil não configurado.");
        return idGrupo;
    }

    public static void adicionarMembro(Connection conn, PerfilChamado perfil, int idUsuario) throws SQLException {
        int idGrupo = grupoGerenciavel(conn, perfil);
        if (nomeLogin(conn, idUsuario) == null) throw new RegraChamadoException("Usuário não encontrado.");
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO dbo.sis_usuario_grupo (id_usuario, id_grupo) SELECT ?, ? WHERE NOT EXISTS " +
                "(SELECT 1 FROM dbo.sis_usuario_grupo WHERE id_usuario = ? AND id_grupo = ?)")) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idGrupo);
            ps.setInt(3, idUsuario);
            ps.setInt(4, idGrupo);
            ps.executeUpdate();
        }
    }

    public static void removerMembro(Connection conn, PerfilChamado perfil, int idUsuario) throws SQLException {
        int idGrupo = grupoGerenciavel(conn, perfil);
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM dbo.sis_usuario_grupo WHERE id_usuario = ? AND id_grupo = ?")) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idGrupo);
            ps.executeUpdate();
        }
    }

    public static String emailUsuario(Connection conn, int idUsuario) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT RTRIM(email) FROM dbo.sis_usuario WHERE id_usuario = ?")) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String e = rs.getString(1);
                    return e == null || e.trim().isEmpty() ? null : e.trim();
                }
            }
        }
        return null;
    }

    public static boolean equipamentoExiste(Connection conn, int idLocal) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM local_vigente WHERE id_local = ? AND desativado = 0")) {
            ps.setInt(1, idLocal);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static JsonArray equipamentos(Connection conn) throws SQLException {
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_local, RTRIM(nome) AS nome FROM local_vigente WHERE desativado = 0 ORDER BY nome");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject o = new JsonObject();
                o.addProperty("idLocal", rs.getInt("id_local"));
                o.addProperty("nome", rs.getString("nome"));
                arr.add(o);
            }
        }
        return arr;
    }

    public static List<Prioridade> prioridades(Connection conn, boolean soAtivas) throws SQLException {
        List<Prioridade> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM muralha.chamado_prioridade" + (soAtivas ? " WHERE ativo = 1" : "") + " ORDER BY ordem");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(prioridade(rs));
        }
        return lista;
    }

    public static Prioridade obterPrioridade(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM muralha.chamado_prioridade WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? prioridade(rs) : null;
            }
        }
    }

    private static Prioridade prioridade(ResultSet rs) throws SQLException {
        Prioridade p = new Prioridade();
        p.id = rs.getInt("id");
        p.codigo = rs.getString("codigo");
        p.nome = rs.getString("nome");
        p.ordem = rs.getInt("ordem");
        p.minutosAtendimento = rs.getInt("minutos_atendimento");
        p.minutosResolucao = rs.getInt("minutos_resolucao");
        p.cor = rs.getString("cor");
        p.ativo = rs.getBoolean("ativo");
        return p;
    }

    public static List<Categoria> categorias(Connection conn, boolean soAtivas) throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        String where = soAtivas ? "WHERE c.ativo = 1 AND (p.id IS NULL OR p.ativo = 1) " : "";
        try (PreparedStatement ps = conn.prepareStatement(
                SQL_CATEGORIAS + where + "ORDER BY COALESCE(p.nome, c.nome), CASE WHEN c.id_pai IS NULL THEN 0 ELSE 1 END, c.nome");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(categoria(rs));
        }
        return lista;
    }

    public static Categoria obterCategoria(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_CATEGORIAS + "WHERE c.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? categoria(rs) : null;
            }
        }
    }

    private static Categoria categoria(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.id = rs.getInt("id");
        c.nome = rs.getString("nome");
        int idPai = rs.getInt("id_pai");
        c.idPai = rs.wasNull() ? null : idPai;
        c.nomePai = rs.getString("nome_pai");
        c.exigeEquipamento = rs.getBoolean("exige");
        c.ativo = rs.getBoolean("ativo");
        return c;
    }

    public static void salvarPrioridade(Connection conn, Prioridade p) throws SQLException {
        p.validar();
        String sql = p.id == 0
                ? "INSERT INTO muralha.chamado_prioridade (codigo, nome, ordem, minutos_atendimento, minutos_resolucao, cor) VALUES (?,?,?,?,?,?)"
                : "UPDATE muralha.chamado_prioridade SET codigo=?, nome=?, ordem=?, minutos_atendimento=?, minutos_resolucao=?, cor=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.codigo);
            ps.setString(2, p.nome.trim());
            ps.setInt(3, p.ordem);
            ps.setInt(4, p.minutosAtendimento);
            ps.setInt(5, p.minutosResolucao);
            ps.setString(6, p.cor);
            if (p.id != 0) ps.setInt(7, p.id);
            ps.executeUpdate();
        }
    }

    public static void salvarCategoria(Connection conn, Categoria c) throws SQLException {
        c.validar();
        if (c.idPai != null) {
            Categoria pai = obterCategoria(conn, c.idPai);
            if (pai == null || pai.idPai != null) {
                throw new RegraChamadoException("A categoria pai deve ser uma categoria de primeiro nível.");
            }
            if (c.id != 0 && c.idPai == c.id) {
                throw new RegraChamadoException("Uma categoria não pode ser pai de si mesma.");
            }
        }
        String sql = c.id == 0
                ? "INSERT INTO muralha.chamado_categoria (nome, id_pai, exige_equipamento) VALUES (?,?,?)"
                : "UPDATE muralha.chamado_categoria SET nome=?, id_pai=?, exige_equipamento=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.nome.trim());
            if (c.idPai == null) ps.setNull(2, java.sql.Types.INTEGER);
            else ps.setInt(2, c.idPai);
            ps.setBoolean(3, c.idPai == null && c.exigeEquipamento);
            if (c.id != 0) ps.setInt(4, c.id);
            ps.executeUpdate();
        }
    }

    public static void alternarAtivo(Connection conn, String tipo, int id) throws SQLException {
        String tabela;
        if ("prioridade".equals(tipo)) tabela = "muralha.chamado_prioridade";
        else if ("categoria".equals(tipo)) tabela = "muralha.chamado_categoria";
        else throw new RegraChamadoException("Tipo inválido.");
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE " + tabela + " SET ativo = CASE WHEN ativo = 1 THEN 0 ELSE 1 END WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
