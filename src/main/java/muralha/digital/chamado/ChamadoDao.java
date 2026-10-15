package muralha.digital.chamado;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.chamado.CalculadoraSla.Marca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** SQL de muralha.chamado e tabelas satelite. Quem chama abre/fecha a conexao. */
public final class ChamadoDao {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    /** Versao para controle otimista: dt_atualizacao com milissegundos (DATETIME2(3)). */
    private static final DateTimeFormatter ISO_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int LIMITE_EXPORTACAO = 50_000;

    private static final String COLUNAS =
            "SELECT c.id, c.protocolo, c.titulo, c.descricao, c.id_categoria, c.id_prioridade, " +
            "c.id_prioridade_solicitada, c.status, c.origem, c.id_local, c.id_usuario_solicitante, " +
            "c.id_usuario_responsavel, c.dt_abertura, c.dt_prazo_atendimento, c.dt_prazo_resolucao, " +
            "c.dt_primeiro_atendimento, c.dt_resolucao, c.dt_fechamento, c.minutos_pausados, c.dt_inicio_pausa, " +
            "c.sla_atendimento, c.sla_resolucao, c.avaliacao_nota, c.avaliacao_comentario, c.dt_avaliacao, " +
            "c.fechado_automaticamente, c.dt_atualizacao, " +
            "COALESCE(cp.nome + N' / ', N'') + cat.nome AS nome_categoria, p.nome AS nome_prioridade, " +
            "p.cor AS cor_prioridade, p.minutos_resolucao AS minutos_resolucao_prioridade, " +
            "ps.nome AS nome_prioridade_solicitada, RTRIM(l.nome) AS nome_local, RTRIM(us.nome) AS nome_solicitante, " +
            "RTRIM(us.email) AS email_solicitante, RTRIM(ur.nome) AS nome_responsavel ";

    private static final String FROM =
            "FROM muralha.chamado c " +
            "JOIN muralha.chamado_categoria cat ON cat.id = c.id_categoria " +
            "LEFT JOIN muralha.chamado_categoria cp ON cp.id = cat.id_pai " +
            "JOIN muralha.chamado_prioridade p ON p.id = c.id_prioridade " +
            "JOIN muralha.chamado_prioridade ps ON ps.id = c.id_prioridade_solicitada " +
            "LEFT JOIN dbo.local l ON l.id_local = c.id_local AND l.sequencia_local = 1 " +
            "LEFT JOIN dbo.sis_usuario us ON us.id_usuario = c.id_usuario_solicitante " +
            "LEFT JOIN dbo.sis_usuario ur ON ur.id_usuario = c.id_usuario_responsavel ";

    /** Expressoes de agrupamento permitidas no relatorio (lista branca). 1900-01-01 foi segunda-feira. */
    private static final Map<String, String> AGRUPAMENTOS = Map.of(
            "dia", "CONVERT(VARCHAR(10), c.dt_abertura, 120)",
            "semana", "CONVERT(VARCHAR(10), DATEADD(DAY, -(DATEDIFF(DAY, '19000101', c.dt_abertura) % 7), CAST(c.dt_abertura AS DATE)), 120)",
            "mes", "CONVERT(VARCHAR(7), c.dt_abertura, 120)",
            "categoria", "COALESCE(cp.nome + N' / ', N'') + cat.nome",
            "equipamento", "COALESCE(RTRIM(l.nome), N'(sem equipamento)')",
            "prioridade", "p.nome");

    private ChamadoDao() {
    }

    public static long inserir(Connection conn, Chamado c) throws SQLException {
        // DATETIME2(3): truncar em milissegundos para o valor em memoria bater com o gravado
        c.dtAbertura = c.dtAbertura.truncatedTo(ChronoUnit.MILLIS);
        c.dtPrazoAtendimento = c.dtPrazoAtendimento.truncatedTo(ChronoUnit.MILLIS);
        c.dtPrazoResolucao = c.dtPrazoResolucao.truncatedTo(ChronoUnit.MILLIS);
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.chamado (protocolo, titulo, descricao, id_categoria, id_prioridade, " +
                "id_prioridade_solicitada, status, origem, id_local, id_usuario_solicitante, dt_abertura, " +
                "dt_prazo_atendimento, dt_prazo_resolucao, dt_atualizacao) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.protocolo);
            ps.setString(2, c.titulo);
            ps.setString(3, c.descricao);
            ps.setInt(4, c.idCategoria);
            ps.setInt(5, c.idPrioridade);
            ps.setInt(6, c.idPrioridadeSolicitada);
            ps.setString(7, c.status.name());
            ps.setString(8, c.origem);
            setInt(ps, 9, c.idLocal);
            ps.setInt(10, c.idUsuarioSolicitante);
            ps.setTimestamp(11, ts(c.dtAbertura));
            ps.setTimestamp(12, ts(c.dtPrazoAtendimento));
            ps.setTimestamp(13, ts(c.dtPrazoResolucao));
            ps.setTimestamp(14, ts(c.dtAbertura));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                c.dtAtualizacao = c.dtAbertura;
                return keys.getLong(1);
            }
        }
    }

    public static Chamado obterPorProtocolo(Connection conn, String protocolo) throws SQLException {
        if (!GeradorProtocolo.valido(protocolo)) return null;
        try (PreparedStatement ps = conn.prepareStatement(COLUNAS + FROM + "WHERE c.protocolo = ?")) {
            ps.setString(1, protocolo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Controle otimista: 0 linhas = chamado mudou desde a leitura (spec §1.2). */
    public static int atualizarEstado(Connection conn, Chamado c, StatusChamado esperado) throws SQLException {
        // DATETIME2(3): truncar em milissegundos para a comparacao do proximo UPDATE bater
        LocalDateTime nova = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE muralha.chamado SET status=?, id_prioridade=?, id_usuario_responsavel=?, " +
                "dt_prazo_atendimento=?, dt_prazo_resolucao=?, dt_primeiro_atendimento=?, dt_resolucao=?, " +
                "dt_fechamento=?, minutos_pausados=?, dt_inicio_pausa=?, sla_atendimento=?, sla_resolucao=?, " +
                "avaliacao_nota=?, avaliacao_comentario=?, dt_avaliacao=?, fechado_automaticamente=?, dt_atualizacao=? " +
                "WHERE id=? AND status=? AND dt_atualizacao=?")) {
            ps.setString(1, c.status.name());
            ps.setInt(2, c.idPrioridade);
            setInt(ps, 3, c.idUsuarioResponsavel);
            ps.setTimestamp(4, ts(c.dtPrazoAtendimento));
            ps.setTimestamp(5, ts(c.dtPrazoResolucao));
            ps.setTimestamp(6, ts(c.dtPrimeiroAtendimento));
            ps.setTimestamp(7, ts(c.dtResolucao));
            ps.setTimestamp(8, ts(c.dtFechamento));
            ps.setInt(9, c.minutosPausados);
            ps.setTimestamp(10, ts(c.dtInicioPausa));
            ps.setString(11, c.slaAtendimento.name());
            ps.setString(12, c.slaResolucao.name());
            setInt(ps, 13, c.avaliacaoNota);
            ps.setString(14, c.avaliacaoComentario);
            ps.setTimestamp(15, ts(c.dtAvaliacao));
            ps.setBoolean(16, c.fechadoAutomaticamente);
            ps.setTimestamp(17, ts(nova));
            ps.setLong(18, c.id);
            ps.setString(19, esperado.name());
            ps.setTimestamp(20, ts(c.dtAtualizacao));
            int linhas = ps.executeUpdate();
            if (linhas > 0) c.dtAtualizacao = nova;
            return linhas;
        }
    }

    public static long inserirHistorico(Connection conn, long idChamado, Integer idUsuario, String tipo,
                                        String statusAnterior, String statusNovo, String texto) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.chamado_historico (id_chamado, id_usuario, tipo, status_anterior, status_novo, texto) " +
                "VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, idChamado);
            setInt(ps, 2, idUsuario);
            ps.setString(3, tipo);
            ps.setString(4, statusAnterior);
            ps.setString(5, statusNovo);
            ps.setString(6, texto == null ? null : (texto.length() > 2000 ? texto.substring(0, 2000) : texto));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public static void inserirAnexo(Connection conn, long idChamado, Long idHistorico, AnexoGravado a, int idUsuario)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.chamado_anexo (id_chamado, id_historico, nome_original, caminho, tipo_mime, " +
                "tamanho_bytes, sha256, id_usuario) VALUES (?,?,?,?,?,?,?,?)")) {
            ps.setLong(1, idChamado);
            if (idHistorico == null) ps.setNull(2, Types.BIGINT);
            else ps.setLong(2, idHistorico);
            ps.setString(3, a.nomeOriginal);
            ps.setString(4, a.caminhoRelativo);
            ps.setString(5, a.tipoMime);
            ps.setLong(6, a.tamanho);
            ps.setString(7, a.sha256);
            ps.setInt(8, idUsuario);
            ps.executeUpdate();
        }
    }

    public static JsonArray historico(Connection conn, long idChamado) throws SQLException {
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT h.id, h.dt_evento, h.tipo, h.status_anterior, h.status_novo, h.texto, " +
                "COALESCE(RTRIM(u.nome), N'Sistema') AS usuario " +
                "FROM muralha.chamado_historico h LEFT JOIN dbo.sis_usuario u ON u.id_usuario = h.id_usuario " +
                "WHERE h.id_chamado = ? ORDER BY h.dt_evento DESC, h.id DESC")) {
            ps.setLong(1, idChamado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("id", rs.getLong("id"));
                    o.addProperty("dtEvento", iso(ldt(rs, "dt_evento")));
                    o.addProperty("tipo", rs.getString("tipo"));
                    o.addProperty("statusAnterior", rs.getString("status_anterior"));
                    o.addProperty("statusNovo", rs.getString("status_novo"));
                    o.addProperty("texto", rs.getString("texto"));
                    o.addProperty("usuario", rs.getString("usuario"));
                    arr.add(o);
                }
            }
        }
        return arr;
    }

    public static JsonArray anexos(Connection conn, long idChamado) throws SQLException {
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, id_historico, nome_original, tamanho_bytes, sha256, dt_envio " +
                "FROM muralha.chamado_anexo WHERE id_chamado = ? ORDER BY id")) {
            ps.setLong(1, idChamado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("id", rs.getLong("id"));
                    long idHist = rs.getLong("id_historico");
                    if (!rs.wasNull()) o.addProperty("idHistorico", idHist);
                    o.addProperty("nome", rs.getString("nome_original"));
                    o.addProperty("tamanho", rs.getLong("tamanho_bytes"));
                    o.addProperty("sha256", rs.getString("sha256"));
                    o.addProperty("dtEnvio", iso(ldt(rs, "dt_envio")));
                    arr.add(o);
                }
            }
        }
        return arr;
    }

    public static JsonObject obterAnexo(Connection conn, long idAnexo) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT a.nome_original, a.caminho, a.tipo_mime, c.protocolo FROM muralha.chamado_anexo a " +
                "JOIN muralha.chamado c ON c.id = a.id_chamado WHERE a.id = ?")) {
            ps.setLong(1, idAnexo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                JsonObject o = new JsonObject();
                o.addProperty("nome", rs.getString("nome_original"));
                o.addProperty("caminho", rs.getString("caminho"));
                o.addProperty("tipoMime", rs.getString("tipo_mime"));
                o.addProperty("protocolo", rs.getString("protocolo"));
                return o;
            }
        }
    }

    /** Sem destinatario a notificacao ja nasce com ERRO (spec §1.4). */
    public static void enfileirarNotificacao(Connection conn, long idChamado, String tipo, String destinatario)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.chamado_notificacao (id_chamado, tipo, destinatario, status, erro) VALUES (?,?,?,?,?)")) {
            ps.setLong(1, idChamado);
            ps.setString(2, tipo);
            ps.setString(3, destinatario);
            ps.setString(4, destinatario == null ? "ERRO" : "PENDENTE");
            ps.setString(5, destinatario == null ? "Solicitante sem e-mail cadastrado" : null);
            ps.executeUpdate();
        }
    }

    public static JsonObject listar(Connection conn, FiltroChamados f) throws SQLException {
        List<Object> params = new ArrayList<>();
        String where = f.where(params);
        JsonObject r = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) " + FROM + where)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                r.addProperty("total", rs.getInt(1));
            }
        }
        JsonArray itens = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(COLUNAS + FROM + where +
                " ORDER BY c.dt_abertura DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY")) {
            List<Object> p2 = new ArrayList<>(params);
            p2.add((f.pagina - 1) * FiltroChamados.TAMANHO_PAGINA);
            p2.add(FiltroChamados.TAMANHO_PAGINA);
            bind(ps, p2);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) itens.add(json(mapear(rs)));
            }
        }
        r.add("itens", itens);
        return r;
    }

    /** Linhas com as chaves de ExportacaoChamados.LISTA_CHAVES, datas no formato brasileiro. */
    public static JsonArray listarParaExportacao(Connection conn, FiltroChamados f) throws SQLException {
        List<Object> params = new ArrayList<>();
        String where = f.where(params);
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                COLUNAS.replaceFirst("SELECT ", "SELECT TOP " + LIMITE_EXPORTACAO + " ") + FROM + where +
                " ORDER BY c.dt_abertura DESC")) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Chamado c = mapear(rs);
                    JsonObject o = new JsonObject();
                    o.addProperty("protocolo", c.protocolo);
                    o.addProperty("titulo", c.titulo);
                    o.addProperty("categoria", c.nomeCategoria);
                    o.addProperty("equipamento", c.nomeLocal);
                    o.addProperty("prioridade", c.nomePrioridade);
                    o.addProperty("status", c.status.name());
                    o.addProperty("solicitante", c.nomeSolicitante);
                    o.addProperty("responsavel", c.nomeResponsavel);
                    o.addProperty("abertura", br(c.dtAbertura));
                    o.addProperty("prazoAtendimento", br(c.dtPrazoAtendimento));
                    o.addProperty("prazoResolucao", br(c.dtPrazoResolucao));
                    o.addProperty("slaAtendimento", c.slaAtendimento.name());
                    o.addProperty("slaResolucao", c.slaResolucao.name());
                    o.addProperty("resolucao", br(c.dtResolucao));
                    o.addProperty("fechamento", br(c.dtFechamento));
                    o.addProperty("nota", c.avaliacaoNota == null ? "" : String.valueOf(c.avaliacaoNota));
                    arr.add(o);
                }
            }
        }
        return arr;
    }

    /** Linhas com as chaves de ExportacaoChamados.INDICADORES_CHAVES. */
    public static JsonArray indicadores(Connection conn, FiltroChamados f, String agrupamento) throws SQLException {
        String grupo = AGRUPAMENTOS.get(agrupamento);
        if (grupo == null) throw new RegraChamadoException("Agrupamento inválido.");
        List<Object> params = new ArrayList<>();
        String where = f.where(params);
        String sql =
                "SELECT " + grupo + " AS grupo, COUNT(*) AS total, " +
                "SUM(CASE WHEN c.dt_resolucao IS NOT NULL THEN 1 ELSE 0 END) AS resolvidos, " +
                "SUM(CASE WHEN c.status = 'CANCELADO' THEN 1 ELSE 0 END) AS cancelados, " +
                "SUM(CASE WHEN c.status <> 'CANCELADO' AND c.sla_atendimento = 'CUMPRIDO' THEN 1 ELSE 0 END) AS at_ok, " +
                "SUM(CASE WHEN c.status <> 'CANCELADO' AND c.sla_atendimento IN ('CUMPRIDO','VIOLADO','VENCIDO') THEN 1 ELSE 0 END) AS at_medido, " +
                "SUM(CASE WHEN c.status <> 'CANCELADO' AND c.sla_resolucao = 'CUMPRIDO' THEN 1 ELSE 0 END) AS res_ok, " +
                "SUM(CASE WHEN c.status <> 'CANCELADO' AND c.sla_resolucao IN ('CUMPRIDO','VIOLADO','VENCIDO') THEN 1 ELSE 0 END) AS res_medido, " +
                "AVG(CAST(DATEDIFF(MINUTE, c.dt_abertura, c.dt_primeiro_atendimento) AS FLOAT)) AS tma, " +
                "AVG(CASE WHEN c.dt_resolucao IS NOT NULL THEN CAST(DATEDIFF(MINUTE, c.dt_abertura, c.dt_resolucao) - c.minutos_pausados AS FLOAT) END) AS tmr, " +
                "AVG(CAST(c.avaliacao_nota AS FLOAT)) AS nota, " +
                "SUM(CASE WHEN c.avaliacao_nota IS NOT NULL THEN 1 ELSE 0 END) AS avaliados, " +
                "SUM(CASE WHEN c.status = 'FECHADO' THEN 1 ELSE 0 END) AS fechados " +
                FROM + where + " GROUP BY " + grupo + " ORDER BY grupo";
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("grupo", rs.getString("grupo"));
                    o.addProperty("total", rs.getInt("total"));
                    o.addProperty("resolvidos", rs.getInt("resolvidos"));
                    o.addProperty("cancelados", rs.getInt("cancelados"));
                    o.addProperty("pctSlaAtendimento", pct(rs.getInt("at_ok"), rs.getInt("at_medido")));
                    o.addProperty("pctSlaResolucao", pct(rs.getInt("res_ok"), rs.getInt("res_medido")));
                    o.addProperty("tmaMin", arred(rs, "tma"));
                    o.addProperty("tmrMin", arred(rs, "tmr"));
                    o.addProperty("notaMedia", arred(rs, "nota"));
                    o.addProperty("pctAvaliados", pct(rs.getInt("avaliados"), rs.getInt("fechados")));
                    arr.add(o);
                }
            }
        }
        return arr;
    }

    public static List<Chamado> listarNaoTerminais(Connection conn) throws SQLException {
        return listarOnde(conn, "WHERE c.status NOT IN ('FECHADO','CANCELADO')", null);
    }

    public static List<Chamado> listarResolvidosAntesDe(Connection conn, LocalDateTime limite) throws SQLException {
        return listarOnde(conn, "WHERE c.status = 'RESOLVIDO' AND c.dt_inicio_pausa <= ?", limite);
    }

    private static List<Chamado> listarOnde(Connection conn, String where, LocalDateTime param) throws SQLException {
        List<Chamado> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(COLUNAS + FROM + where)) {
            if (param != null) ps.setTimestamp(1, ts(param));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public static JsonObject json(Chamado c) {
        JsonObject o = new JsonObject();
        o.addProperty("protocolo", c.protocolo);
        o.addProperty("titulo", c.titulo);
        o.addProperty("descricao", c.descricao);
        o.addProperty("idCategoria", c.idCategoria);
        o.addProperty("categoria", c.nomeCategoria);
        o.addProperty("idPrioridade", c.idPrioridade);
        o.addProperty("prioridade", c.nomePrioridade);
        o.addProperty("corPrioridade", c.corPrioridade);
        o.addProperty("prioridadeSolicitada", c.nomePrioridadeSolicitada);
        o.addProperty("status", c.status.name());
        o.addProperty("idLocal", c.idLocal);
        o.addProperty("equipamento", c.nomeLocal);
        o.addProperty("solicitante", c.nomeSolicitante);
        o.addProperty("responsavel", c.nomeResponsavel);
        o.addProperty("abertura", iso(c.dtAbertura));
        o.addProperty("prazoAtendimento", iso(c.dtPrazoAtendimento));
        o.addProperty("prazoResolucao", iso(c.dtPrazoResolucao));
        o.addProperty("primeiroAtendimento", iso(c.dtPrimeiroAtendimento));
        o.addProperty("resolucao", iso(c.dtResolucao));
        o.addProperty("fechamento", iso(c.dtFechamento));
        o.addProperty("pausado", c.dtInicioPausa != null);
        o.addProperty("slaAtendimento", c.slaAtendimento.name());
        o.addProperty("slaResolucao", c.slaResolucao.name());
        o.addProperty("avaliacaoNota", c.avaliacaoNota);
        o.addProperty("avaliacaoComentario", c.avaliacaoComentario);
        o.addProperty("fechadoAutomaticamente", c.fechadoAutomaticamente);
        o.addProperty("dtAtualizacao", c.dtAtualizacao == null ? null : c.dtAtualizacao.format(ISO_MS));
        return o;
    }

    private static Chamado mapear(ResultSet rs) throws SQLException {
        Chamado c = new Chamado();
        c.id = rs.getLong("id");
        c.protocolo = rs.getString("protocolo");
        c.titulo = rs.getString("titulo");
        c.descricao = rs.getString("descricao");
        c.idCategoria = rs.getInt("id_categoria");
        c.idPrioridade = rs.getInt("id_prioridade");
        c.idPrioridadeSolicitada = rs.getInt("id_prioridade_solicitada");
        c.status = StatusChamado.valueOf(rs.getString("status"));
        c.origem = rs.getString("origem");
        c.idLocal = intOuNulo(rs, "id_local");
        c.idUsuarioSolicitante = rs.getInt("id_usuario_solicitante");
        c.idUsuarioResponsavel = intOuNulo(rs, "id_usuario_responsavel");
        c.dtAbertura = ldt(rs, "dt_abertura");
        c.dtPrazoAtendimento = ldt(rs, "dt_prazo_atendimento");
        c.dtPrazoResolucao = ldt(rs, "dt_prazo_resolucao");
        c.dtPrimeiroAtendimento = ldt(rs, "dt_primeiro_atendimento");
        c.dtResolucao = ldt(rs, "dt_resolucao");
        c.dtFechamento = ldt(rs, "dt_fechamento");
        c.minutosPausados = rs.getInt("minutos_pausados");
        c.dtInicioPausa = ldt(rs, "dt_inicio_pausa");
        c.slaAtendimento = Marca.valueOf(rs.getString("sla_atendimento"));
        c.slaResolucao = Marca.valueOf(rs.getString("sla_resolucao"));
        c.avaliacaoNota = intOuNulo(rs, "avaliacao_nota");
        c.avaliacaoComentario = rs.getString("avaliacao_comentario");
        c.dtAvaliacao = ldt(rs, "dt_avaliacao");
        c.fechadoAutomaticamente = rs.getBoolean("fechado_automaticamente");
        c.dtAtualizacao = ldt(rs, "dt_atualizacao");
        c.nomeCategoria = rs.getString("nome_categoria");
        c.nomePrioridade = rs.getString("nome_prioridade");
        c.corPrioridade = rs.getString("cor_prioridade");
        c.minutosResolucaoPrioridade = rs.getInt("minutos_resolucao_prioridade");
        c.nomePrioridadeSolicitada = rs.getString("nome_prioridade_solicitada");
        c.nomeLocal = rs.getString("nome_local");
        c.nomeSolicitante = rs.getString("nome_solicitante");
        c.emailSolicitante = rs.getString("email_solicitante");
        c.nomeResponsavel = rs.getString("nome_responsavel");
        return c;
    }

    private static void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object v = params.get(i);
            if (v instanceof Integer) ps.setInt(i + 1, (Integer) v);
            else if (v instanceof Timestamp) ps.setTimestamp(i + 1, (Timestamp) v);
            else ps.setString(i + 1, String.valueOf(v));
        }
    }

    private static void setInt(PreparedStatement ps, int idx, Integer v) throws SQLException {
        if (v == null) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, v);
    }

    private static Integer intOuNulo(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }

    private static Timestamp ts(LocalDateTime d) {
        return d == null ? null : Timestamp.valueOf(d);
    }

    private static LocalDateTime ldt(ResultSet rs, String col) throws SQLException {
        Timestamp t = rs.getTimestamp(col);
        return t == null ? null : t.toLocalDateTime();
    }

    private static String iso(LocalDateTime d) {
        return d == null ? null : d.format(ISO);
    }

    private static String br(LocalDateTime d) {
        return d == null ? "" : d.format(BR);
    }

    private static Double pct(int parte, int total) {
        return total == 0 ? null : Math.round(1000.0 * parte / total) / 10.0;
    }

    private static Double arred(ResultSet rs, String col) throws SQLException {
        double v = rs.getDouble(col);
        return rs.wasNull() ? null : Math.round(v * 10.0) / 10.0;
    }
}
