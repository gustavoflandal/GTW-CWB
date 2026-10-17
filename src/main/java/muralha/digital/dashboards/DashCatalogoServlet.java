package muralha.digital.dashboards;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.*;
import muralha.digital.dashboards.consulta.MontadorSql;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * CRUD do catalogo de tabelas, colunas e relacoes.
 * Acesso restrito a administradores (CATALOGO_ADMIN).
 */
@WebServlet("/api/dash/catalogo")
public class DashCatalogoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Acesso acesso = new Acesso(req, resp, true);
        if (!acesso.verificaAcesso(false)) return;

        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String acao = req.getParameter("acao");

            if ("listarTabelas".equals(acao)) {
                result = listarTabelas(conn);

            } else if ("listarColunas".equals(acao)) {
                int idTabela = Integer.parseInt(req.getParameter("idTabela"));
                result = listarColunas(conn, idTabela);

            } else if ("listarRelacoes".equals(acao)) {
                result = listarRelacoes(conn);

            } else if ("importarSugestoes".equals(acao)) {
                result = importarSugestoes(conn);

            } else if ("amostraTabela".equals(acao)) {
                int idTabela = Integer.parseInt(req.getParameter("idTabela"));
                result = amostraTabela(conn, idTabela);

            } else {
                result.addProperty("ok", false);
                result.addProperty("erro", "acao invalida");
            }

        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Acesso acesso = new Acesso(req, resp, true);
        if (!acesso.verificaAcesso(false)) return;

        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            StringBuilder body = new StringBuilder();
            String line;
            var reader = req.getReader();
            while ((line = reader.readLine()) != null) body.append(line);

            JsonObject dados = JsonParser.parseString(body.toString()).getAsJsonObject();
            String acao = dados.has("acao") ? dados.get("acao").getAsString() : "";

            if ("salvarTabela".equals(acao)) {
                result = salvarTabela(conn, dados);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    String idReg = result.has("id") ? result.get("id").getAsString() : null;
                    AuditoriaService.registrar(req, "dashboard-catalogo", "salvar-tabela", idReg, "Tabela do catalogo salva");
                }

            } else if ("salvarRelacao".equals(acao)) {
                result = salvarRelacao(conn, dados);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-catalogo", "salvar-relacao", null, "Relacao salva no catalogo");
                }

            } else if ("salvarColuna".equals(acao)) {
                result = salvarColuna(conn, dados);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-catalogo", "salvar-coluna", null, "Coluna salva no catalogo");
                }

            } else if ("excluirRelacao".equals(acao)) {
                result = excluirRelacao(conn, dados);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-catalogo", "excluir-relacao", null, "Relacao excluida do catalogo");
                }

            } else if ("promoverRelacao".equals(acao)) {
                result = promoverRelacao(conn, dados);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-catalogo", "promover-relacao", null, "Relacao promovida no catalogo");
                }

            } else {
                result.addProperty("ok", false);
                result.addProperty("erro", "acao invalida");
            }

        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    private JsonObject listarTabelas(Connection conn) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, nome_fisico, schema_fisico, rotulo, descricao, " +
                "       chave_primaria, sensivel, ativo " +
                "FROM dash.catalogo_tabela ORDER BY rotulo")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject t = new JsonObject();
                    t.addProperty("id", rs.getInt("id"));
                    t.addProperty("nomeFisico", rs.getString("nome_fisico"));
                    t.addProperty("schemaFisico", rs.getString("schema_fisico"));
                    t.addProperty("rotulo", rs.getString("rotulo"));
                    t.addProperty("descricao", rs.getString("descricao"));
                    t.addProperty("chavePrimaria", rs.getString("chave_primaria"));
                    t.addProperty("sensivel", rs.getBoolean("sensivel"));
                    t.addProperty("ativo", rs.getBoolean("ativo"));
                    arr.add(t);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("tabelas", arr);
        return result;
    }

    private JsonObject listarColunas(Connection conn, int idTabela) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, nome_fisico, rotulo, tipo, sensivel, papel_sugerido, ativo " +
                "FROM dash.catalogo_coluna WHERE id_tabela = ? ORDER BY id")) {
            ps.setInt(1, idTabela);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject c = new JsonObject();
                    c.addProperty("id", rs.getInt("id"));
                    c.addProperty("nomeFisico", rs.getString("nome_fisico"));
                    c.addProperty("rotulo", rs.getString("rotulo"));
                    c.addProperty("tipo", rs.getString("tipo"));
                    c.addProperty("sensivel", rs.getBoolean("sensivel"));
                    c.addProperty("papelSugerido", rs.getString("papel_sugerido"));
                    c.addProperty("ativo", rs.getBoolean("ativo"));
                    arr.add(c);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("colunas", arr);
        return result;
    }

    private JsonObject listarRelacoes(Connection conn) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT cr.id, cr.id_tabela_origem, co.rotulo AS origem_rotulo, " +
                "       cr.id_tabela_destino, cd.rotulo AS destino_rotulo, " +
                "       cr.cardinalidade, cr.coluna_origem, cr.coluna_destino " +
                "FROM dash.catalogo_relacao cr " +
                "JOIN dash.catalogo_tabela co ON cr.id_tabela_origem = co.id " +
                "JOIN dash.catalogo_tabela cd ON cr.id_tabela_destino = cd.id " +
                "WHERE cr.ativo = 1 ORDER BY co.rotulo")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject r = new JsonObject();
                    r.addProperty("id", rs.getInt("id"));
                    r.addProperty("idTabelaOrigem", rs.getInt("id_tabela_origem"));
                    r.addProperty("origemRotulo", rs.getString("origem_rotulo"));
                    r.addProperty("idTabelaDestino", rs.getInt("id_tabela_destino"));
                    r.addProperty("destinoRotulo", rs.getString("destino_rotulo"));
                    r.addProperty("cardinalidade", rs.getString("cardinalidade"));
                    r.addProperty("colunaOrigem", rs.getString("coluna_origem"));
                    r.addProperty("colunaDestino", rs.getString("coluna_destino"));
                    arr.add(r);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("relacoes", arr);
        return result;
    }

    private JsonObject importarSugestoes(Connection conn) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray tabelas = new JsonArray();
        JsonArray relacoes = new JsonArray();

        // Importar tabelas de INFORMATION_SCHEMA
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TABLE_SCHEMA, TABLE_NAME " +
                "FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_TYPE = 'BASE TABLE' " +
                "AND TABLE_SCHEMA IN ('dbo','muralha') " +
                "AND TABLE_NAME NOT LIKE 'sys%' " +
                "AND TABLE_NAME NOT IN (SELECT nome_fisico FROM dash.catalogo_tabela) " +
                "ORDER BY TABLE_SCHEMA, TABLE_NAME")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject t = new JsonObject();
                    t.addProperty("schema", rs.getString("TABLE_SCHEMA"));
                    t.addProperty("tabela", rs.getString("TABLE_NAME"));
                    tabelas.add(t);
                }
            }
        }

        // Importar relacoes de sys.foreign_keys
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT " +
                "  OBJECT_SCHEMA_NAME(fk.parent_object_id) AS schema_origem, " +
                "  OBJECT_NAME(fk.parent_object_id) AS tabela_origem, " +
                "  COL_NAME(fkc.parent_object_id, fkc.parent_column_id) AS coluna_origem, " +
                "  OBJECT_SCHEMA_NAME(fk.referenced_object_id) AS schema_destino, " +
                "  OBJECT_NAME(fk.referenced_object_id) AS tabela_destino, " +
                "  COL_NAME(fkc.referenced_object_id, fkc.referenced_column_id) AS coluna_destino " +
                "FROM sys.foreign_keys fk " +
                "JOIN sys.foreign_key_columns fkc ON fk.object_id = fkc.constraint_object_id " +
                "WHERE OBJECT_SCHEMA_NAME(fk.parent_object_id) IN ('dbo','muralha') " +
                "ORDER BY tabela_origem")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject r = new JsonObject();
                    r.addProperty("schemaOrigem", rs.getString("schema_origem"));
                    r.addProperty("tabelaOrigem", rs.getString("tabela_origem"));
                    r.addProperty("colunaOrigem", rs.getString("coluna_origem"));
                    r.addProperty("schemaDestino", rs.getString("schema_destino"));
                    r.addProperty("tabelaDestino", rs.getString("tabela_destino"));
                    r.addProperty("colunaDestino", rs.getString("coluna_destino"));
                    relacoes.add(r);
                }
            }
        }

        result.addProperty("ok", true);
        result.add("tabelas", tabelas);
        result.add("relacoes", relacoes);
        return result;
    }

    private JsonObject salvarTabela(Connection conn, JsonObject dados) throws Exception {
        JsonObject result = new JsonObject();
        String nomeFisico = dados.get("nomeFisico").getAsString();
        String schemaFisico = dados.has("schemaFisico") ? dados.get("schemaFisico").getAsString() : "dbo";
        String rotulo = dados.get("rotulo").getAsString();
        String descricao = dados.has("descricao") ? dados.get("descricao").getAsString() : null;
        String chavePrimaria = dados.has("chavePrimaria") ? dados.get("chavePrimaria").getAsString() : "id";
        boolean sensivel = dados.has("sensivel") && dados.get("sensivel").getAsBoolean();

        try (PreparedStatement ps = conn.prepareStatement(
                "MERGE dash.catalogo_tabela AS alvo " +
                "USING (SELECT ? AS sf, ? AS nf) AS src " +
                "ON alvo.schema_fisico = src.sf AND alvo.nome_fisico = src.nf " +
                "WHEN MATCHED THEN UPDATE SET rotulo=?, descricao=?, chave_primaria=?, sensivel=?, atualizado_em=SYSDATETIME() " +
                "WHEN NOT MATCHED THEN INSERT (nome_fisico, schema_fisico, rotulo, descricao, chave_primaria, sensivel) " +
                "VALUES (?, ?, ?, ?, ?, ?);")) {
            ps.setString(1, schemaFisico);
            ps.setString(2, nomeFisico);
            ps.setString(3, rotulo);
            ps.setString(4, descricao);
            ps.setString(5, chavePrimaria);
            ps.setBoolean(6, sensivel);
            ps.setString(7, nomeFisico);
            ps.setString(8, schemaFisico);
            ps.setString(9, rotulo);
            ps.setString(10, descricao);
            ps.setString(11, chavePrimaria);
            ps.setBoolean(12, sensivel);
            ps.executeUpdate();
        }
        result.addProperty("ok", true);
        return result;
    }

    private JsonObject salvarRelacao(Connection conn, JsonObject dados) throws Exception {
        JsonObject result = new JsonObject();
        int idOrigem = dados.get("idTabelaOrigem").getAsInt();
        int idDestino = dados.get("idTabelaDestino").getAsInt();
        String cardinalidade = dados.get("cardinalidade").getAsString();
        String colOrigem = dados.get("colunaOrigem").getAsString();
        String colDestino = dados.get("colunaDestino").getAsString();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO dash.catalogo_relacao " +
                "(id_tabela_origem, id_tabela_destino, cardinalidade, coluna_origem, coluna_destino) " +
                "VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, idOrigem);
            ps.setInt(2, idDestino);
            ps.setString(3, cardinalidade);
            ps.setString(4, colOrigem);
            ps.setString(5, colDestino);
            ps.executeUpdate();
        }
        result.addProperty("ok", true);
        return result;
    }

    private JsonObject salvarColuna(Connection conn, JsonObject dados) throws Exception {
        JsonObject result = new JsonObject();
        int id = dados.has("id") ? dados.get("id").getAsInt() : 0;
        int idTabela = dados.get("idTabela").getAsInt();
        String nomeFisico = dados.get("nomeFisico").getAsString();
        String rotulo = dados.get("rotulo").getAsString();
        String tipo = dados.get("tipo").getAsString();
        String papelSugerido = dados.has("papelSugerido") && !dados.get("papelSugerido").isJsonNull()
                ? dados.get("papelSugerido").getAsString() : null;
        boolean sensivel = dados.has("sensivel") && dados.get("sensivel").getAsBoolean();

        if (nomeFisico == null || nomeFisico.trim().isEmpty()) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Nome fisico obrigatorio");
            return result;
        }
        if (rotulo == null || rotulo.trim().isEmpty()) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Rotulo obrigatorio");
            return result;
        }

        if (id > 0) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE dash.catalogo_coluna SET nome_fisico=?, rotulo=?, tipo=?, " +
                    "papel_sugerido=?, sensivel=? WHERE id=?")) {
                ps.setString(1, nomeFisico);
                ps.setString(2, rotulo);
                ps.setString(3, tipo);
                ps.setString(4, papelSugerido);
                ps.setBoolean(5, sensivel);
                ps.setInt(6, id);
                ps.executeUpdate();
            }
        } else {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO dash.catalogo_coluna " +
                    "(id_tabela, nome_fisico, rotulo, tipo, papel_sugerido, sensivel) " +
                    "VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, idTabela);
                ps.setString(2, nomeFisico);
                ps.setString(3, rotulo);
                ps.setString(4, tipo);
                ps.setString(5, papelSugerido);
                ps.setBoolean(6, sensivel);
                ps.executeUpdate();
            }
        }

        result.addProperty("ok", true);
        return result;
    }

    private JsonObject excluirRelacao(Connection conn, JsonObject dados) throws Exception {
        JsonObject result = new JsonObject();
        int id = dados.get("id").getAsInt();
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE dash.catalogo_relacao SET ativo = 0 WHERE id = ?")) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                result.addProperty("ok", false);
                result.addProperty("erro", "Relacao nao encontrada");
                return result;
            }
        }
        result.addProperty("ok", true);
        return result;
    }

    private JsonObject amostraTabela(Connection conn, int idTabela) throws Exception {
        JsonObject result = new JsonObject();

        // Buscar nome fisico e schema do catalogo (dados confiaveis)
        String nomeFisico = null;
        String schemaFisico = null;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT nome_fisico, schema_fisico FROM dash.catalogo_tabela WHERE id = ?")) {
            ps.setInt(1, idTabela);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Tabela nao encontrada no catalogo");
                    return result;
                }
                nomeFisico = rs.getString("nome_fisico");
                schemaFisico = rs.getString("schema_fisico");
            }
        }

        // Os nomes vem do catalogo, que e editavel pela tela: valida antes de montar o SQL.
        schemaFisico = MontadorSql.identificador(schemaFisico);
        nomeFisico = MontadorSql.identificador(nomeFisico);

        JsonArray colunas = new JsonArray();
        JsonArray linhas = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP 5 * FROM [" + schemaFisico + "].[" + nomeFisico + "] WITH (NOLOCK)")) {
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int qtdColunas = meta.getColumnCount();
                for (int i = 1; i <= qtdColunas; i++) {
                    colunas.add(meta.getColumnName(i));
                }
                while (rs.next()) {
                    JsonArray linha = new JsonArray();
                    for (int i = 1; i <= qtdColunas; i++) {
                        Object val = rs.getObject(i);
                        if (val == null) {
                            linha.add(JsonNull.INSTANCE);
                        } else {
                            linha.add(val.toString());
                        }
                    }
                    linhas.add(linha);
                }
            }
        }

        result.addProperty("ok", true);
        result.add("colunas", colunas);
        result.add("linhas", linhas);
        return result;
    }

    private JsonObject promoverRelacao(Connection conn, JsonObject dados) throws Exception {
        JsonObject result = new JsonObject();
        int idRelacao = dados.get("idRelacao").getAsInt();

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO dash.catalogo_relacao " +
                "(id_tabela_origem, id_tabela_destino, coluna_origem, coluna_destino, cardinalidade) " +
                "SELECT id_tabela_origem, id_tabela_destino, coluna_origem, coluna_destino, cardinalidade " +
                "FROM dash.dataset_relacao WHERE id = ? AND ativo = 1")) {
            ps.setInt(1, idRelacao);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                result.addProperty("ok", false);
                result.addProperty("erro", "Relacao nao encontrada ou inativa");
                return result;
            }
        }

        result.addProperty("ok", true);
        return result;
    }
}
