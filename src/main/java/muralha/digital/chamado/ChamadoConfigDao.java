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
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Parametros, perfis e cadastros do Sistema de Chamados. */
public final class ChamadoConfigDao {

    private static final String SQL_CATEGORIAS =
            "SELECT c.id, c.nome, c.id_pai, p.nome AS nome_pai, c.ativo, " +
            "CAST(CASE WHEN c.id_pai IS NULL THEN c.exige_equipamento ELSE p.exige_equipamento END AS BIT) AS exige " +
            "FROM muralha.chamado_categoria c LEFT JOIN muralha.chamado_categoria p ON p.id = c.id_pai ";

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
