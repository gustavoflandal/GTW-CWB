package muralha.digital.dashboards;

import com.consilux.lib.Conexao;
import muralha.digital.acessos.ControleAcesso;
import com.google.gson.*;
import muralha.digital.dashboards.consulta.MontadorSql;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/**
 * CRUD de datasets: listar, obter, salvar, publicar, excluir.
 */
@WebServlet("/api/dash/datasets")
public class DatasetServlet extends HttpServlet {

    private final DatasetService service = new DatasetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        if (!ControleAcesso.exigir(req, resp)) return;

        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            int idUsuario = ControleAcesso.usuario(req).getId();
            int tenantId = 1;
            String acao = req.getParameter("acao");

            if ("listar".equals(acao)) {
                result = service.listar(conn, idUsuario, tenantId);

            } else if ("obter".equals(acao)) {
                int id = Integer.parseInt(req.getParameter("id"));
                result = service.obter(conn, id);

            } else if ("relacoes".equals(acao)) {
                int idTabela = Integer.parseInt(req.getParameter("idTabela"));
                result = service.obterRelacoes(conn, idTabela);

            } else if ("colunas".equals(acao)) {
                int idTabela = Integer.parseInt(req.getParameter("idTabela"));
                result = service.obterColunas(conn, idTabela);

            } else if ("tabelasCatalogo".equals(acao)) {
                result = listarTabelasCatalogo(conn);

            } else if ("sugestoesRelacao".equals(acao)) {
                int idDataset = Integer.parseInt(req.getParameter("idDataset"));
                int idTabela = Integer.parseInt(req.getParameter("idTabela"));
                result = sugestoesRelacao(conn, idDataset, idTabela);

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
        if (!ControleAcesso.exigir(req, resp)) return;

        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            int idUsuario = ControleAcesso.usuario(req).getId();
            int tenantId = 1;

            StringBuilder body = new StringBuilder();
            String line;
            var reader = req.getReader();
            while ((line = reader.readLine()) != null) body.append(line);

            JsonObject dados = JsonParser.parseString(body.toString()).getAsJsonObject();
            String acao = dados.has("acao") ? dados.get("acao").getAsString() : "";

            if ("salvar".equals(acao)) {
                result = service.salvar(conn, dados, idUsuario, tenantId);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    String idReg = result.has("id") ? result.get("id").getAsString() : null;
                    AuditoriaService.registrar(req, "dashboard-dataset", "salvar", idReg, "Dataset salvo");
                }

            } else if ("publicar".equals(acao)) {
                int idDataset = dados.get("id").getAsInt();
                result = service.publicar(conn, idDataset, idUsuario);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-dataset", "publicar",
                            String.valueOf(idDataset), "Dataset publicado");
                }

            } else if ("excluir".equals(acao)) {
                int idDataset = dados.get("id").getAsInt();
                result = service.excluir(conn, idDataset, idUsuario);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-dataset", "excluir",
                            String.valueOf(idDataset), "Dataset excluido");
                }

            } else if ("validarExpressao".equals(acao)) {
                String expressao = dados.get("expressao").getAsString();
                result = service.validarExpressao(expressao);

            } else if ("validarRelacao".equals(acao)) {
                result = validarRelacao(conn, dados);

            } else if ("verificarCardinalidade".equals(acao)) {
                result = verificarCardinalidade(conn, dados);

            } else if ("salvarModelador".equals(acao)) {
                result = salvarModelador(conn, dados);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-dataset", "salvar-modelador", null, "Modelador do dataset salvo");
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

    private JsonObject listarTabelasCatalogo(Connection conn) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        try (var ps = conn.prepareStatement(
                "SELECT id, nome_fisico, schema_fisico, rotulo, descricao " +
                "FROM dash.catalogo_tabela WHERE ativo = 1 ORDER BY rotulo")) {
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject t = new JsonObject();
                    t.addProperty("id", rs.getInt("id"));
                    t.addProperty("nomeFisico", rs.getString("nome_fisico"));
                    t.addProperty("schemaFisico", rs.getString("schema_fisico"));
                    t.addProperty("rotulo", rs.getString("rotulo"));
                    t.addProperty("descricao", rs.getString("descricao"));
                    arr.add(t);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("tabelas", arr);
        return result;
    }

    // ==========================================
    // Modelador visual de datasets
    // ==========================================

    private JsonObject sugestoesRelacao(Connection conn, int idDataset, int idTabela) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray sugestoes = new JsonArray();

        // Obter tabelas ja no canvas do dataset + a tabela sendo adicionada
        Set<Integer> tabelasCanvas = new HashSet<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_tabela FROM dash.dataset_tabela WHERE id_dataset = ?")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabelasCanvas.add(rs.getInt("id_tabela"));
                }
            }
        }
        tabelasCanvas.add(idTabela);

        // 1. FKs reais do banco (confianca: banco)
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ct_o.id AS id_tabela_origem, ct_d.id AS id_tabela_destino, " +
                "       COL_NAME(fkc.parent_object_id, fkc.parent_column_id) AS coluna_origem, " +
                "       COL_NAME(fkc.referenced_object_id, fkc.referenced_column_id) AS coluna_destino " +
                "FROM sys.foreign_keys fk " +
                "JOIN sys.foreign_key_columns fkc ON fk.object_id = fkc.constraint_object_id " +
                "JOIN dash.catalogo_tabela ct_o ON ct_o.nome_fisico = OBJECT_NAME(fk.parent_object_id) " +
                "  AND ct_o.schema_fisico = OBJECT_SCHEMA_NAME(fk.parent_object_id) " +
                "JOIN dash.catalogo_tabela ct_d ON ct_d.nome_fisico = OBJECT_NAME(fk.referenced_object_id) " +
                "  AND ct_d.schema_fisico = OBJECT_SCHEMA_NAME(fk.referenced_object_id) " +
                "WHERE ct_o.ativo = 1 AND ct_d.ativo = 1")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idOrigem = rs.getInt("id_tabela_origem");
                    int idDestino = rs.getInt("id_tabela_destino");
                    if (tabelasCanvas.contains(idOrigem) && tabelasCanvas.contains(idDestino)) {
                        JsonObject s = new JsonObject();
                        s.addProperty("idTabelaOrigem", idOrigem);
                        s.addProperty("idTabelaDestino", idDestino);
                        s.addProperty("colunaOrigem", rs.getString("coluna_origem"));
                        s.addProperty("colunaDestino", rs.getString("coluna_destino"));
                        s.addProperty("cardinalidade", "many_to_one");
                        s.addProperty("origem", "banco");
                        s.addProperty("confianca", "banco");
                        sugestoes.add(s);
                    }
                }
            }
        }

        // 2. Relacoes existentes no catalogo (confianca: catalogo)
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_tabela_origem, id_tabela_destino, coluna_origem, " +
                "       coluna_destino, cardinalidade " +
                "FROM dash.catalogo_relacao WHERE ativo = 1")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idOrigem = rs.getInt("id_tabela_origem");
                    int idDestino = rs.getInt("id_tabela_destino");
                    if (tabelasCanvas.contains(idOrigem) && tabelasCanvas.contains(idDestino)) {
                        JsonObject s = new JsonObject();
                        s.addProperty("idTabelaOrigem", idOrigem);
                        s.addProperty("idTabelaDestino", idDestino);
                        s.addProperty("colunaOrigem", rs.getString("coluna_origem"));
                        s.addProperty("colunaDestino", rs.getString("coluna_destino"));
                        s.addProperty("cardinalidade", rs.getString("cardinalidade"));
                        s.addProperty("origem", "catalogo");
                        s.addProperty("confianca", "catalogo");
                        sugestoes.add(s);
                    }
                }
            }
        }

        // 3. Sugestao por nome: colunas id_X ou X_id que batem com PK de outra tabela (confianca: sugerida)
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT cc.id_tabela, cc.nome_fisico AS coluna, " +
                "       ct2.id AS id_tabela_ref, ct2.chave_primaria " +
                "FROM dash.catalogo_coluna cc " +
                "JOIN dash.catalogo_tabela ct ON cc.id_tabela = ct.id " +
                "JOIN dash.catalogo_tabela ct2 ON " +
                "  (cc.nome_fisico = 'id_' + ct2.nome_fisico " +
                "   OR cc.nome_fisico = ct2.nome_fisico + '_id') " +
                "WHERE cc.ativo = 1 AND ct.ativo = 1 AND ct2.ativo = 1 " +
                "AND cc.id_tabela <> ct2.id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idOrigem = rs.getInt("id_tabela");
                    int idDestino = rs.getInt("id_tabela_ref");
                    if (tabelasCanvas.contains(idOrigem) && tabelasCanvas.contains(idDestino)) {
                        JsonObject s = new JsonObject();
                        s.addProperty("idTabelaOrigem", idOrigem);
                        s.addProperty("idTabelaDestino", idDestino);
                        s.addProperty("colunaOrigem", rs.getString("coluna"));
                        s.addProperty("colunaDestino", rs.getString("chave_primaria"));
                        s.addProperty("cardinalidade", "many_to_one");
                        s.addProperty("origem", "sugerida");
                        s.addProperty("confianca", "sugerida");
                        sugestoes.add(s);
                    }
                }
            }
        }

        result.addProperty("ok", true);
        result.add("sugestoes", sugestoes);
        return result;
    }

    private JsonObject validarRelacao(Connection conn, JsonObject dados) throws Exception {
        JsonObject result = new JsonObject();
        int idDataset = dados.get("idDataset").getAsInt();
        int idOrigem = dados.get("idTabelaOrigem").getAsInt();
        int idDestino = dados.get("idTabelaDestino").getAsInt();
        String colOrigem = dados.get("colunaOrigem").getAsString();
        String colDestino = dados.get("colunaDestino").getAsString();

        // a) Verificar self-join
        if (idOrigem == idDestino) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Nao e permitido criar relacao de uma tabela consigo mesma");
            return result;
        }

        // b) Verificar compatibilidade de tipos
        String tipoOrigem = null;
        String tipoDestino = null;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT tipo FROM dash.catalogo_coluna WHERE id_tabela = ? AND nome_fisico = ?")) {
            ps.setInt(1, idOrigem);
            ps.setString(2, colOrigem);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) tipoOrigem = rs.getString("tipo");
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT tipo FROM dash.catalogo_coluna WHERE id_tabela = ? AND nome_fisico = ?")) {
            ps.setInt(1, idDestino);
            ps.setString(2, colDestino);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) tipoDestino = rs.getString("tipo");
            }
        }

        if (tipoOrigem == null || tipoDestino == null) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Coluna nao encontrada no catalogo");
            return result;
        }

        if (!tiposCompativeis(tipoOrigem, tipoDestino)) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Tipos incompativeis: " + tipoOrigem + " e " + tipoDestino);
            return result;
        }

        // c) Verificar caminho unico (nao existe relacao entre essas tabelas no dataset)
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM dash.dataset_relacao " +
                "WHERE id_dataset = ? AND ativo = 1 " +
                "AND ((id_tabela_origem = ? AND id_tabela_destino = ?) " +
                "  OR (id_tabela_origem = ? AND id_tabela_destino = ?))")) {
            ps.setInt(1, idDataset);
            ps.setInt(2, idOrigem);
            ps.setInt(3, idDestino);
            ps.setInt(4, idDestino);
            ps.setInt(5, idOrigem);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Ja existe uma relacao entre essas tabelas neste dataset");
                    return result;
                }
            }
        }

        result.addProperty("ok", true);
        result.addProperty("cardinalidade", "many_to_one");
        return result;
    }

    private boolean tiposCompativeis(String tipo1, String tipo2) {
        String t1 = tipo1.toLowerCase().trim();
        String t2 = tipo2.toLowerCase().trim();

        String base1 = t1.replaceAll("\\(.*\\)", "").trim();
        String base2 = t2.replaceAll("\\(.*\\)", "").trim();

        // Tipos rejeitados para join
        Set<String> rejeitados = Set.of(
                "float", "real", "text", "ntext", "image", "varbinary", "xml");
        if (rejeitados.contains(base1) || rejeitados.contains(base2)) return false;

        // Grupos compativeis
        Set<String> inteiros = Set.of("int", "bigint", "smallint", "tinyint");
        Set<String> textos = Set.of("varchar", "nvarchar", "char", "nchar");
        Set<String> datas = Set.of("datetime", "datetime2");

        if (inteiros.contains(base1) && inteiros.contains(base2)) return true;
        if (textos.contains(base1) && textos.contains(base2)) return true;
        if (datas.contains(base1) && datas.contains(base2)) return true;
        if ("date".equals(base1) && "date".equals(base2)) return true;
        if ("uniqueidentifier".equals(base1) && "uniqueidentifier".equals(base2)) return true;

        // decimal com escala 0 eh compativel com inteiros
        if ("decimal".equals(base1) && decimalEscalaZero(t1) && inteiros.contains(base2)) return true;
        if ("decimal".equals(base2) && decimalEscalaZero(t2) && inteiros.contains(base1)) return true;

        // Mesmo tipo base
        if (base1.equals(base2)) return true;

        return false;
    }

    private boolean decimalEscalaZero(String tipo) {
        int parenOpen = tipo.indexOf('(');
        if (parenOpen < 0) return true; // sem parametros, assume escala 0
        int comma = tipo.indexOf(',', parenOpen);
        if (comma < 0) return true; // decimal(p) sem escala = escala 0
        int parenClose = tipo.indexOf(')', comma);
        if (parenClose < 0) return false;
        String escala = tipo.substring(comma + 1, parenClose).trim();
        return "0".equals(escala);
    }

    private JsonObject verificarCardinalidade(Connection conn, JsonObject dados) throws Exception {
        JsonObject result = new JsonObject();
        int idTabelaOrigem = dados.get("idTabelaOrigem").getAsInt();
        int idTabelaDestino = dados.get("idTabelaDestino").getAsInt();
        String colOrigem = dados.get("colunaOrigem").getAsString();
        String colDestino = dados.get("colunaDestino").getAsString();

        // Resolver nomes fisicos do catalogo (dados confiaveis, nao do usuario)
        String schemaOrigem = null, tabelaOrigem = null;
        String schemaDestino = null, tabelaDestino = null;

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT nome_fisico, schema_fisico FROM dash.catalogo_tabela WHERE id = ?")) {
            ps.setInt(1, idTabelaOrigem);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tabelaOrigem = rs.getString("nome_fisico");
                    schemaOrigem = rs.getString("schema_fisico");
                }
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT nome_fisico, schema_fisico FROM dash.catalogo_tabela WHERE id = ?")) {
            ps.setInt(1, idTabelaDestino);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tabelaDestino = rs.getString("nome_fisico");
                    schemaDestino = rs.getString("schema_fisico");
                }
            }
        }

        if (tabelaOrigem == null || tabelaDestino == null) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Tabela nao encontrada no catalogo");
            return result;
        }

        // Validar que as colunas existem no catalogo antes de usar nos nomes
        boolean colunaOrigemValida = false;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM dash.catalogo_coluna WHERE id_tabela = ? AND nome_fisico = ?")) {
            ps.setInt(1, idTabelaOrigem);
            ps.setString(2, colOrigem);
            try (ResultSet rs = ps.executeQuery()) {
                colunaOrigemValida = rs.next();
            }
        }
        boolean colunaDestinoValida = false;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM dash.catalogo_coluna WHERE id_tabela = ? AND nome_fisico = ?")) {
            ps.setInt(1, idTabelaDestino);
            ps.setString(2, colDestino);
            try (ResultSet rs = ps.executeQuery()) {
                colunaDestinoValida = rs.next();
            }
        }

        if (!colunaOrigemValida || !colunaDestinoValida) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Coluna nao encontrada no catalogo");
            return result;
        }

        // Os nomes vem do catalogo, que e editavel pela tela: valida antes de montar o SQL.
        for (String nome : new String[]{colOrigem, schemaOrigem, tabelaOrigem, colDestino, schemaDestino, tabelaDestino}) {
            MontadorSql.identificador(nome);
        }

        long dupOrigem = 0;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP 1 [" + colOrigem + "] FROM [" + schemaOrigem + "].[" + tabelaOrigem + "] " +
                "GROUP BY [" + colOrigem + "] HAVING COUNT(*) > 1")) {
            ps.setQueryTimeout(5);
            try (ResultSet rs = ps.executeQuery()) {
                dupOrigem = rs.next() ? 1 : 0;
            }
        }

        // Verificar duplicatas no destino
        long dupDestino = 0;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP 1 [" + colDestino + "] FROM [" + schemaDestino + "].[" + tabelaDestino + "] " +
                "GROUP BY [" + colDestino + "] HAVING COUNT(*) > 1")) {
            ps.setQueryTimeout(5);
            try (ResultSet rs = ps.executeQuery()) {
                dupDestino = rs.next() ? 1 : 0;
            }
        }

        String cardinalidade;
        if (dupOrigem == 0 && dupDestino == 0) {
            cardinalidade = "one_to_one";
        } else if (dupOrigem > 0 && dupDestino == 0) {
            cardinalidade = "many_to_one";
        } else if (dupOrigem == 0) {
            cardinalidade = "one_to_many";
        } else {
            cardinalidade = "many_to_one";
        }

        result.addProperty("ok", true);
        result.addProperty("cardinalidade", cardinalidade);
        result.addProperty("duplicatasOrigem", dupOrigem);
        result.addProperty("duplicatasDestino", dupDestino);
        return result;
    }

    private JsonObject salvarModelador(Connection conn, JsonObject dados) throws Exception {
        JsonObject result = new JsonObject();
        int idDataset = dados.has("idDataset") ? dados.get("idDataset").getAsInt() : 0;
        JsonArray tabelas = dados.has("tabelas") ? dados.getAsJsonArray("tabelas") : new JsonArray();
        JsonArray relacoes = dados.has("relacoes") ? dados.getAsJsonArray("relacoes") : new JsonArray();
        JsonArray campos = dados.has("campos") ? dados.getAsJsonArray("campos") : new JsonArray();
        String layoutJson = dados.has("layoutJson")
                ? new Gson().toJson(dados.get("layoutJson")) : "{}";

        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            // Se dataset novo, criar primeiro
            if (idDataset == 0) {
                String nome = dados.has("nome") ? dados.get("nome").getAsString() : "Sem nome";
                String descricao = dados.has("descricao") && !dados.get("descricao").isJsonNull()
                        ? dados.get("descricao").getAsString() : null;
                int idTabelaBase = dados.has("idTabelaBase") ? dados.get("idTabelaBase").getAsInt() : 0;
                String visibilidade = dados.has("visibilidade") ? dados.get("visibilidade").getAsString() : "privado";

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO dash.dataset (nome, descricao, id_tabela_base, id_usuario, tenant_id, visibilidade) " +
                        "VALUES (?, ?, ?, 1, 1, ?); SELECT SCOPE_IDENTITY();")) {
                    ps.setString(1, nome);
                    ps.setString(2, descricao);
                    ps.setInt(3, idTabelaBase);
                    ps.setString(4, visibilidade);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        idDataset = rs.getInt(1);
                    }
                }
            } else {
                // Atualizar metadados se fornecidos
                if (dados.has("nome")) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE dash.dataset SET nome=?, visibilidade=?, atualizado_em=SYSDATETIME() WHERE id=?")) {
                        ps.setString(1, dados.get("nome").getAsString());
                        ps.setString(2, dados.has("visibilidade") ? dados.get("visibilidade").getAsString() : "privado");
                        ps.setInt(3, idDataset);
                        ps.executeUpdate();
                    }
                }
            }

            // a) DELETE e reinserir dataset_tabela
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM dash.dataset_tabela WHERE id_dataset = ?")) {
                del.setInt(1, idDataset);
                del.executeUpdate();
            }
            if (tabelas.size() > 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO dash.dataset_tabela (id_dataset, id_tabela, alias, tipo, ordem) " +
                        "VALUES (?, ?, ?, ?, ?)")) {
                    int ordem = 0;
                    for (var el : tabelas) {
                        JsonObject t = el.getAsJsonObject();
                        ps.setInt(1, idDataset);
                        ps.setInt(2, t.get("idTabela").getAsInt());
                        ps.setString(3, t.has("alias") ? t.get("alias").getAsString() : null);
                        ps.setString(4, t.has("tipo") ? t.get("tipo").getAsString() : "join");
                        ps.setInt(5, ordem++);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // b) Desativar relacoes existentes e inserir novas
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE dash.dataset_relacao SET ativo = 0 WHERE id_dataset = ?")) {
                ps.setInt(1, idDataset);
                ps.executeUpdate();
            }
            if (relacoes.size() > 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO dash.dataset_relacao " +
                        "(id_dataset, id_tabela_origem, id_tabela_destino, coluna_origem, " +
                        " coluna_destino, cardinalidade, origem, ativo, data_criacao) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, 1, SYSDATETIME())")) {
                    for (var el : relacoes) {
                        JsonObject r = el.getAsJsonObject();
                        ps.setInt(1, idDataset);
                        ps.setInt(2, r.get("idTabelaOrigem").getAsInt());
                        ps.setInt(3, r.get("idTabelaDestino").getAsInt());
                        ps.setString(4, r.get("colunaOrigem").getAsString());
                        ps.setString(5, r.get("colunaDestino").getAsString());
                        ps.setString(6, r.has("cardinalidade")
                                ? r.get("cardinalidade").getAsString() : "many_to_one");
                        ps.setString(7, r.has("origem")
                                ? r.get("origem").getAsString() : "manual");
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // c) Salvar campos selecionados
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM dash.dataset_campo WHERE id_dataset = ?")) {
                del.setInt(1, idDataset);
                del.executeUpdate();
            }
            if (campos.size() > 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO dash.dataset_campo " +
                        "(id_dataset, id_coluna, nome_alias, rotulo, agregacao, tipo_campo, ordem) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    int ordem = 0;
                    for (var el : campos) {
                        JsonObject c = el.getAsJsonObject();
                        ps.setInt(1, idDataset);
                        if (c.has("idColuna") && !c.get("idColuna").isJsonNull()) {
                            ps.setInt(2, c.get("idColuna").getAsInt());
                        } else {
                            ps.setNull(2, java.sql.Types.INTEGER);
                        }
                        ps.setString(3, c.has("nomeFisico") ? c.get("nomeFisico").getAsString() : "campo_" + ordem);
                        ps.setString(4, c.has("rotulo") ? c.get("rotulo").getAsString() : ps.toString());
                        ps.setString(5, c.has("agregacao") ? c.get("agregacao").getAsString() : "NONE");
                        ps.setString(6, c.has("papel") ? c.get("papel").getAsString() : "dimensao");
                        ps.setInt(7, ordem++);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // d) MERGE layout do modelador (upsert)
            try (PreparedStatement ps = conn.prepareStatement(
                    "MERGE dash.dataset_modelador AS alvo " +
                    "USING (SELECT ? AS id_ds) AS src " +
                    "ON alvo.id_dataset = src.id_ds " +
                    "WHEN MATCHED THEN UPDATE SET layout_json = ?, data_modificacao = SYSDATETIME() " +
                    "WHEN NOT MATCHED THEN INSERT (id_dataset, layout_json, data_criacao, data_modificacao) " +
                    "VALUES (?, ?, SYSDATETIME(), SYSDATETIME());")) {
                ps.setInt(1, idDataset);
                ps.setString(2, layoutJson);
                ps.setInt(3, idDataset);
                ps.setString(4, layoutJson);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }

        result.addProperty("ok", true);
        result.addProperty("id", idDataset);
        return result;
    }
}
