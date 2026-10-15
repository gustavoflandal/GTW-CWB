package muralha.digital.dashboards;

import com.google.gson.*;
import muralha.digital.dashboards.consulta.DatasetResolvido;
import muralha.digital.dashboards.consulta.ErroConsulta;
import muralha.digital.dashboards.consulta.MontadorSql;
import muralha.digital.dashboards.consulta.PeriodoRelativo;
import muralha.digital.dashboards.consulta.ResolvedorDataset;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servico de CRUD de datasets com versionamento e publicacao.
 */
public final class DatasetService {

    private final GramaticaCampoCalculado gramatica = new GramaticaCampoCalculado();
    private final MontadorSql montador = new MontadorSql(new PeriodoRelativo());

    public JsonObject listar(Connection conn, int idUsuario, int tenantId) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT d.id, d.nome, d.descricao, d.status, d.versao, d.id_usuario, " +
                "       d.criado_em, d.atualizado_em, ct.rotulo AS tabela_base " +
                "FROM dash.dataset d " +
                "JOIN dash.catalogo_tabela ct ON d.id_tabela_base = ct.id " +
                "WHERE (d.id_usuario = ? OR d.visibilidade = 'publico') " +
                "AND d.tenant_id = ? " +
                "ORDER BY d.atualizado_em DESC, d.criado_em DESC")) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject ds = new JsonObject();
                    ds.addProperty("id", rs.getInt("id"));
                    ds.addProperty("nome", rs.getString("nome"));
                    ds.addProperty("descricao", rs.getString("descricao"));
                    ds.addProperty("status", rs.getString("status"));
                    ds.addProperty("versao", rs.getInt("versao"));
                    ds.addProperty("idUsuario", rs.getInt("id_usuario"));
                    ds.addProperty("tabelaBase", rs.getString("tabela_base"));
                    ds.addProperty("proprio", rs.getInt("id_usuario") == idUsuario);
                    arr.add(ds);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("datasets", arr);
        return result;
    }

    public JsonObject obter(Connection conn, int idDataset) throws Exception {
        JsonObject result = new JsonObject();
        JsonObject ds = new JsonObject();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT d.id, d.nome, d.descricao, d.id_tabela_base, d.status, " +
                "       d.visibilidade, d.versao, d.id_usuario, d.tenant_id, " +
                "       ct.nome_fisico AS tabela_base_fisico, ct.rotulo AS tabela_base_rotulo " +
                "FROM dash.dataset d " +
                "JOIN dash.catalogo_tabela ct ON d.id_tabela_base = ct.id " +
                "WHERE d.id = ?")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Dataset nao encontrado");
                    return result;
                }
                ds.addProperty("id", rs.getInt("id"));
                ds.addProperty("nome", rs.getString("nome"));
                ds.addProperty("descricao", rs.getString("descricao"));
                ds.addProperty("idTabelaBase", rs.getInt("id_tabela_base"));
                ds.addProperty("tabelaBaseFisico", rs.getString("tabela_base_fisico"));
                ds.addProperty("tabelaBaseRotulo", rs.getString("tabela_base_rotulo"));
                ds.addProperty("status", rs.getString("status"));
                ds.addProperty("visibilidade", rs.getString("visibilidade"));
                ds.addProperty("versao", rs.getInt("versao"));
                ds.addProperty("idUsuario", rs.getInt("id_usuario"));
                ds.addProperty("tenantId", rs.getInt("tenant_id"));
            }
        }

        // Tabelas incluidas
        JsonArray tabelas = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT dt.id, dt.id_tabela, dt.alias, dt.tipo, dt.ordem, " +
                "       ct.rotulo, ct.nome_fisico " +
                "FROM dash.dataset_tabela dt " +
                "JOIN dash.catalogo_tabela ct ON dt.id_tabela = ct.id " +
                "WHERE dt.id_dataset = ? ORDER BY dt.ordem")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject t = new JsonObject();
                    t.addProperty("id", rs.getInt("id"));
                    t.addProperty("idTabela", rs.getInt("id_tabela"));
                    t.addProperty("alias", rs.getString("alias"));
                    t.addProperty("tipo", rs.getString("tipo"));
                    t.addProperty("ordem", rs.getInt("ordem"));
                    t.addProperty("rotulo", rs.getString("rotulo"));
                    t.addProperty("nomeFisico", rs.getString("nome_fisico"));
                    tabelas.add(t);
                }
            }
        }
        ds.add("tabelas", tabelas);

        // Campos
        JsonArray campos = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT dc.id, dc.id_coluna, dc.nome_alias, dc.rotulo, " +
                "       dc.expressao, dc.agregacao, dc.formato, dc.tipo_campo, dc.ordem " +
                "FROM dash.dataset_campo dc " +
                "WHERE dc.id_dataset = ? ORDER BY dc.ordem")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject c = new JsonObject();
                    c.addProperty("id", rs.getInt("id"));
                    int idColuna = rs.getInt("id_coluna");
                    if (!rs.wasNull()) c.addProperty("idColuna", idColuna);
                    c.addProperty("nomeAlias", rs.getString("nome_alias"));
                    c.addProperty("rotulo", rs.getString("rotulo"));
                    c.addProperty("expressao", rs.getString("expressao"));
                    c.addProperty("agregacao", rs.getString("agregacao"));
                    c.addProperty("formato", rs.getString("formato"));
                    c.addProperty("tipoCampo", rs.getString("tipo_campo"));
                    c.addProperty("ordem", rs.getInt("ordem"));
                    campos.add(c);
                }
            }
        }
        ds.add("campos", campos);

        // Filtros fixos
        JsonArray filtros = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, coluna, operador, valor, ordem " +
                "FROM dash.dataset_filtro_fixo WHERE id_dataset = ? ORDER BY ordem")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject f = new JsonObject();
                    f.addProperty("id", rs.getInt("id"));
                    f.addProperty("coluna", rs.getString("coluna"));
                    f.addProperty("operador", rs.getString("operador"));
                    f.addProperty("valor", rs.getString("valor"));
                    f.addProperty("ordem", rs.getInt("ordem"));
                    filtros.add(f);
                }
            }
        }
        ds.add("filtros", filtros);

        // Relacoes do dataset (modelador visual)
        JsonArray relacoes = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, id_tabela_origem, id_tabela_destino, coluna_origem, " +
                "       coluna_destino, cardinalidade, origem " +
                "FROM dash.dataset_relacao WHERE id_dataset = ? AND ativo = 1")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject r = new JsonObject();
                    r.addProperty("id", rs.getInt("id"));
                    r.addProperty("idTabelaOrigem", rs.getInt("id_tabela_origem"));
                    r.addProperty("idTabelaDestino", rs.getInt("id_tabela_destino"));
                    r.addProperty("colunaOrigem", rs.getString("coluna_origem"));
                    r.addProperty("colunaDestino", rs.getString("coluna_destino"));
                    r.addProperty("cardinalidade", rs.getString("cardinalidade"));
                    r.addProperty("origem", rs.getString("origem"));
                    relacoes.add(r);
                }
            }
        }
        ds.add("relacoes", relacoes);

        // Layout do modelador visual
        JsonObject modelador = null;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT layout_json FROM dash.dataset_modelador WHERE id_dataset = ?")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    modelador = new JsonObject();
                    String layoutStr = rs.getString("layout_json");
                    if (layoutStr != null && !layoutStr.isEmpty()) {
                        modelador.add("layout", JsonParser.parseString(layoutStr));
                    }
                }
            }
        }

        result.addProperty("ok", true);
        result.add("dataset", ds);
        if (modelador != null) result.add("modelador", modelador);
        return result;
    }

    public JsonObject salvar(Connection conn, JsonObject dados, int idUsuario, int tenantId) throws Exception {
        JsonObject result = new JsonObject();
        boolean novo = !dados.has("id") || dados.get("id").getAsInt() == 0;

        String nome = dados.get("nome").getAsString();
        if (nome == null || nome.trim().isEmpty()) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Nome obrigatorio");
            return result;
        }

        String descricao = dados.has("descricao") && !dados.get("descricao").isJsonNull()
                ? dados.get("descricao").getAsString() : null;
        int idTabelaBase = dados.get("idTabelaBase").getAsInt();
        String visibilidade = dados.has("visibilidade") ? dados.get("visibilidade").getAsString() : "privado";

        int idDataset;
        if (novo) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO dash.dataset (nome, descricao, id_tabela_base, id_usuario, tenant_id, visibilidade) " +
                    "VALUES (?, ?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY();")) {
                ps.setString(1, nome);
                ps.setString(2, descricao);
                ps.setInt(3, idTabelaBase);
                ps.setInt(4, idUsuario);
                ps.setInt(5, tenantId);
                ps.setString(6, visibilidade);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    idDataset = rs.getInt(1);
                }
            }
        } else {
            idDataset = dados.get("id").getAsInt();
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE dash.dataset SET nome=?, descricao=?, id_tabela_base=?, " +
                    "visibilidade=?, atualizado_em=SYSDATETIME() " +
                    "WHERE id=? AND id_usuario=?")) {
                ps.setString(1, nome);
                ps.setString(2, descricao);
                ps.setInt(3, idTabelaBase);
                ps.setString(4, visibilidade);
                ps.setInt(5, idDataset);
                ps.setInt(6, idUsuario);
                ps.executeUpdate();
            }
        }

        // Salvar tabelas relacionadas
        if (dados.has("tabelas") && dados.get("tabelas").isJsonArray()) {
            salvarTabelas(conn, idDataset, dados.getAsJsonArray("tabelas"));
        }

        // Salvar campos
        if (dados.has("campos") && dados.get("campos").isJsonArray()) {
            JsonObject validacao = salvarCampos(conn, idDataset, dados.getAsJsonArray("campos"));
            if (!validacao.get("ok").getAsBoolean()) {
                return validacao;
            }
        }

        // Salvar filtros fixos
        if (dados.has("filtros") && dados.get("filtros").isJsonArray()) {
            salvarFiltros(conn, idDataset, dados.getAsJsonArray("filtros"));
        }

        result.addProperty("ok", true);
        result.addProperty("id", idDataset);
        return result;
    }

    public JsonObject publicar(Connection conn, int idDataset, int idUsuario) throws Exception {
        JsonObject result = new JsonObject();

        // Verificar que o dataset pertence ao usuario
        int contagem = 0;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM dash.dataset WHERE id = ? AND id_usuario = ?")) {
            ps.setInt(1, idDataset);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                contagem = rs.getInt(1);
            }
        }
        if (contagem == 0) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Dataset nao encontrado ou sem permissao");
            return result;
        }

        // Verificar que tem pelo menos 1 campo
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM dash.dataset_campo WHERE id_dataset = ?")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Dataset sem campos definidos");
                    return result;
                }
            }
        }

        List<String> erros;
        try {
            DatasetResolvido ds = ResolvedorDataset.carregar(conn, idDataset);
            erros = montador.validarDataset(ds);
        } catch (ErroConsulta e) {
            erros = List.of(e.getMessage());
        }
        if (!erros.isEmpty()) {
            JsonArray lista = new JsonArray();
            erros.forEach(lista::add);
            result.addProperty("ok", false);
            result.addProperty("erro", "Dataset com campos invalidos: " + String.join("; ", erros));
            result.add("errosCampos", lista);
            return result;
        }

        // Publicar: incrementar versao e mudar status
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE dash.dataset SET status='publicado', " +
                "versao = versao + 1, atualizado_em = SYSDATETIME() " +
                "WHERE id = ?")) {
            ps.setInt(1, idDataset);
            ps.executeUpdate();
        }

        result.addProperty("ok", true);
        result.addProperty("id", idDataset);
        return result;
    }

    public JsonObject excluir(Connection conn, int idDataset, int idUsuario) throws Exception {
        JsonObject result = new JsonObject();

        // Verificar dono
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_usuario FROM dash.dataset WHERE id = ?")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Dataset nao encontrado");
                    return result;
                }
                if (rs.getInt("id_usuario") != idUsuario) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Sem permissao para excluir");
                    return result;
                }
            }
        }

        // Verificar se tem widgets usando este dataset
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM dash.widget WHERE id_dataset = ?")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Dataset em uso por widgets. Remova os widgets primeiro.");
                    return result;
                }
            }
        }

        // Soft delete: arquivar
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE dash.dataset SET status='arquivado', atualizado_em=SYSDATETIME() WHERE id=?")) {
            ps.setInt(1, idDataset);
            ps.executeUpdate();
        }

        result.addProperty("ok", true);
        return result;
    }

    public JsonObject obterRelacoes(Connection conn, int idTabelaBase) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT cr.id, cr.id_tabela_destino, cr.cardinalidade, " +
                "       cr.coluna_origem, cr.coluna_destino, " +
                "       ct.rotulo, ct.nome_fisico " +
                "FROM dash.catalogo_relacao cr " +
                "JOIN dash.catalogo_tabela ct ON cr.id_tabela_destino = ct.id " +
                "WHERE cr.id_tabela_origem = ? AND cr.ativo = 1 " +
                "ORDER BY ct.rotulo")) {
            ps.setInt(1, idTabelaBase);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject r = new JsonObject();
                    r.addProperty("id", rs.getInt("id"));
                    r.addProperty("idTabelaDestino", rs.getInt("id_tabela_destino"));
                    r.addProperty("cardinalidade", rs.getString("cardinalidade"));
                    r.addProperty("colunaOrigem", rs.getString("coluna_origem"));
                    r.addProperty("colunaDestino", rs.getString("coluna_destino"));
                    r.addProperty("rotulo", rs.getString("rotulo"));
                    r.addProperty("nomeFisico", rs.getString("nome_fisico"));
                    arr.add(r);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("relacoes", arr);
        return result;
    }

    public JsonObject obterColunas(Connection conn, int idTabela) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, nome_fisico, rotulo, tipo, papel_sugerido, sensivel " +
                "FROM dash.catalogo_coluna WHERE id_tabela = ? AND ativo = 1 ORDER BY id")) {
            ps.setInt(1, idTabela);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject c = new JsonObject();
                    c.addProperty("id", rs.getInt("id"));
                    c.addProperty("nomeFisico", rs.getString("nome_fisico"));
                    c.addProperty("rotulo", rs.getString("rotulo"));
                    c.addProperty("tipo", rs.getString("tipo"));
                    c.addProperty("papelSugerido", rs.getString("papel_sugerido"));
                    c.addProperty("sensivel", rs.getBoolean("sensivel"));
                    arr.add(c);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("colunas", arr);
        return result;
    }

    public JsonObject validarExpressao(String expressao) {
        JsonObject result = new JsonObject();
        GramaticaCampoCalculado.Resultado r = gramatica.validar(expressao);
        result.addProperty("ok", true);
        result.addProperty("valido", r.valido);
        if (!r.valido) {
            result.addProperty("erro", r.erro);
        } else {
            result.addProperty("expressaoLimpa", r.expressaoLimpa);
        }
        return result;
    }

    // ==========================================
    // Metodos internos
    // ==========================================

    private void salvarTabelas(Connection conn, int idDataset, JsonArray tabelas) throws Exception {
        try (PreparedStatement del = conn.prepareStatement(
                "DELETE FROM dash.dataset_tabela WHERE id_dataset = ?")) {
            del.setInt(1, idDataset);
            del.executeUpdate();
        }

        if (tabelas.size() == 0) return;

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO dash.dataset_tabela (id_dataset, id_tabela, alias, tipo, ordem) " +
                "VALUES (?, ?, ?, ?, ?)")) {
            int ordem = 0;
            for (var el : tabelas) {
                JsonObject t = el.getAsJsonObject();
                ps.setInt(1, idDataset);
                ps.setInt(2, t.get("idTabela").getAsInt());
                ps.setString(3, t.get("alias").getAsString());
                ps.setString(4, t.has("tipo") ? t.get("tipo").getAsString() : "join");
                ps.setInt(5, ordem++);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private JsonObject salvarCampos(Connection conn, int idDataset, JsonArray campos) throws Exception {
        JsonObject result = new JsonObject();

        // Validar expressoes calculadas antes de salvar
        for (var el : campos) {
            JsonObject c = el.getAsJsonObject();
            if (c.has("expressao") && !c.get("expressao").isJsonNull()) {
                String expr = c.get("expressao").getAsString();
                if (!expr.trim().isEmpty()) {
                    GramaticaCampoCalculado.Resultado v = gramatica.validar(expr);
                    if (!v.valido) {
                        result.addProperty("ok", false);
                        result.addProperty("erro", "Campo calculado '" +
                                c.get("nomeAlias").getAsString() + "': " + v.erro);
                        return result;
                    }
                }
            }
        }

        try (PreparedStatement del = conn.prepareStatement(
                "DELETE FROM dash.dataset_campo WHERE id_dataset = ?")) {
            del.setInt(1, idDataset);
            del.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO dash.dataset_campo " +
                "(id_dataset, id_coluna, nome_alias, rotulo, expressao, agregacao, formato, tipo_campo, ordem) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            int ordem = 0;
            for (var el : campos) {
                JsonObject c = el.getAsJsonObject();
                ps.setInt(1, idDataset);

                if (c.has("idColuna") && !c.get("idColuna").isJsonNull()) {
                    ps.setInt(2, c.get("idColuna").getAsInt());
                } else {
                    ps.setNull(2, java.sql.Types.INTEGER);
                }

                ps.setString(3, c.get("nomeAlias").getAsString());
                ps.setString(4, c.get("rotulo").getAsString());

                if (c.has("expressao") && !c.get("expressao").isJsonNull()) {
                    ps.setString(5, c.get("expressao").getAsString());
                } else {
                    ps.setNull(5, java.sql.Types.NVARCHAR);
                }

                ps.setString(6, c.has("agregacao") ? c.get("agregacao").getAsString() : "NONE");
                ps.setString(7, c.has("formato") && !c.get("formato").isJsonNull()
                        ? c.get("formato").getAsString() : null);
                ps.setString(8, c.has("tipoCampo") ? c.get("tipoCampo").getAsString() : "dimensao");
                ps.setInt(9, ordem++);
                ps.addBatch();
            }
            ps.executeBatch();
        }

        result.addProperty("ok", true);
        return result;
    }

    private void salvarFiltros(Connection conn, int idDataset, JsonArray filtros) throws Exception {
        try (PreparedStatement del = conn.prepareStatement(
                "DELETE FROM dash.dataset_filtro_fixo WHERE id_dataset = ?")) {
            del.setInt(1, idDataset);
            del.executeUpdate();
        }

        if (filtros.size() == 0) return;

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO dash.dataset_filtro_fixo (id_dataset, coluna, operador, valor, ordem) " +
                "VALUES (?, ?, ?, ?, ?)")) {
            int ordem = 0;
            for (var el : filtros) {
                JsonObject f = el.getAsJsonObject();
                ps.setInt(1, idDataset);
                ps.setString(2, f.get("coluna").getAsString());
                ps.setString(3, f.get("operador").getAsString());
                ps.setString(4, f.get("valor").getAsString());
                ps.setInt(5, ordem++);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
