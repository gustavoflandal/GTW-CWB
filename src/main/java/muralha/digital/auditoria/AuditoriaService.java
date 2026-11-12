package muralha.digital.auditoria;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Motor de captura de logs de operação.
 *
 * <p>Centraliza a gravação em {@code dbo.sis_log_auditoria} com três entradas:
 * <ul>
 *   <li>{@link #registrar} — instrumentação MANUAL no código (trilha rica);</li>
 *   <li>{@link #registrarSistema} — eventos de jobs/processos (SISTEMA);</li>
 *   <li>{@link #capturarAutomatico} — captura AUTO feita pelo {@link CapturaLogFilter}
 *       em todas as telas, guiada por {@link MotorRegras}.</li>
 * </ul>
 *
 * <p><b>Integridade:</b> todas as gravações passam por uma fila de thread única
 * ({@code gravador}), o que garante ordenação e permite encadear o hash de cada
 * evento ao anterior em tempo real (SHA-256). Assim a cadeia à prova de
 * adulteração já existe no momento da gravação; o selo diário apenas a consolida.
 *
 * <p><b>Eficiência:</b> a gravação é assíncrona e não bloqueia a navegação. Apenas
 * eventos marcados como "garantidos" (login, exclusões, permissões) aguardam a
 * persistência, para não haver perda em operações sensíveis.
 */
public final class AuditoriaService {

    private static final Logger log = Logger.getLogger(AuditoriaService.class.getName());

    /** Fila de thread única: serializa as gravações e preserva a cadeia de hash. */
    private static final ExecutorService gravador = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "motor-logs-gravador");
        t.setDaemon(true);
        return t;
    });

    private static final String GENESIS;
    static {
        char[] z = new char[64];
        java.util.Arrays.fill(z, '0');
        GENESIS = new String(z);
    }

    /** Hash do último evento gravado (cabeça da cadeia). Lazy init a partir do banco. */
    private static volatile String ultimoHash = null;

    /** Marca na requisição: um log MANUAL já foi registrado (evita duplicar no AUTO). */
    public static final String ATTR_LOG_MANUAL = "muralha.log.manual";

    private AuditoriaService() {}

    // ============================================================
    // API pública (compatível com a instrumentação existente)
    // ============================================================

    /** Registro MANUAL a partir de uma requisição (trilha rica no código). */
    public static void registrar(HttpServletRequest request, String funcionalidade,
                                  String operacao, String idRegistro, String descricao) {
        try { if (request != null) request.setAttribute(ATTR_LOG_MANUAL, Boolean.TRUE); } catch (Exception ignore) {}

        Evento ev = new Evento();
        preencherUsuario(ev, request);
        ev.funcionalidade = funcionalidade;
        ev.operacao       = operacao;
        ev.idRegistro     = idRegistro;
        ev.descricao      = truncar(descricao, 500);
        ev.tipoCaptura    = "MANUAL";
        ev.nivel          = "ALTERACAO";
        enfileirar(ev, false);
    }

    /** Registro por processo automático (job), sem requisição HTTP. */
    public static void registrarSistema(String funcionalidade, String operacao,
                                         String idRegistro, String descricao) {
        Evento ev = new Evento();
        ev.login       = "sistema";
        ev.funcionalidade = funcionalidade;
        ev.operacao    = operacao;
        ev.idRegistro  = idRegistro;
        ev.descricao   = truncar(descricao, 500);
        ev.tipoCaptura = "SISTEMA";
        ev.nivel       = "SISTEMA";
        enfileirar(ev, false);
    }

    // ============================================================
    // Captura automática (chamada pelo CapturaLogFilter)
    // ============================================================

    /**
     * Captura automática de uma requisição HTTP já concluída.
     *
     * @param rota       caminho sem o context-path nem a query (ex.: /MuralhaDigital/Auditoria)
     * @param metodo     método HTTP
     * @param status     código de status da resposta
     * @param duracaoMs  tempo de processamento em milissegundos
     */
    public static void capturarAutomatico(HttpServletRequest request, HttpServletResponse response,
                                           String rota, String metodo, int status, long duracaoMs) {
        try {
            // Já há um log MANUAL (mais rico) nesta requisição: não duplica.
            Object manual = (request != null) ? request.getAttribute(ATTR_LOG_MANUAL) : null;
            if (Boolean.TRUE.equals(manual)) return;

            RegraCaptura regra = MotorRegras.avaliar(rota, metodo);
            if (regra != null && !regra.isCapturar()) {
                return; // excluído por regra (ruído: estáticos, polling, etc.)
            }

            Evento ev = new Evento();
            preencherUsuario(ev, request);
            ev.metodo      = metodo;
            ev.rota        = truncar(rota, 400);
            ev.statusHttp  = status;
            ev.duracaoMs   = (int) Math.min(duracaoMs, Integer.MAX_VALUE);
            ev.tipoCaptura = "AUTO";
            ev.idRegra     = (regra != null) ? regra.getId() : null;

            String acao = (request != null) ? request.getParameter("acao") : null;

            // Nível: regra explícita vence; senão deriva semanticamente (método + ação).
            String nivelRegra = (regra != null) ? regra.getNivel() : null;
            if (nivelRegra != null && !"AUTO".equalsIgnoreCase(nivelRegra)) {
                ev.nivel = nivelRegra;
            } else {
                ev.nivel = derivarNivel(metodo, acao, status,
                        (response != null) ? response.getContentType() : null);
            }

            // Tipo de evento desligado na configuração global: não captura.
            if (!TiposEvento.capturaAtivo(ev.nivel)) return;

            // Funcionalidade: da regra ou derivada da rota.
            String func = (regra != null) ? regra.getFuncionalidade() : null;
            if (func == null || func.isEmpty()) func = derivarFuncionalidade(rota);
            ev.funcionalidade = truncar(func, 100);

            ev.operacao  = truncar(metodo + (acao != null ? " " + acao : ""), 50);
            ev.idRegistro = (request != null) ? primeiroParam(request, "id", "idRegistro", "codigo") : null;
            ev.descricao = truncar("HTTP " + status + " " + Mascarador.mascararConteudo(rota), 500);

            // Dados da operação (mascarados): sempre para mutações (o "depois"),
            // ou quando a regra pedir explicitamente registrar os parâmetros.
            boolean mutacao = "CADASTRO".equals(ev.nivel) || "ALTERACAO".equals(ev.nivel) || "EXCLUSAO".equals(ev.nivel);
            boolean regParam = (regra != null && regra.isRegistrarParametros()) || mutacao;
            if (regParam && request != null) {
                Set<String> extras = Mascarador.parseCampos(regra != null ? regra.getMascararCampos() : null);
                String payload = montarParametros(request, extras);
                if (payload != null) {
                    if (mutacao) {
                        // Estrutura antes/depois: o filtro preenche "depois" (dados enviados).
                        // "antes" fica disponível para captura por imagem anterior (ver doc).
                        ev.dadosExtra = truncar("{\"depois\":" + payload + "}", 4000);
                    } else {
                        ev.dadosExtra = payload;
                    }
                }
            }

            // Impressão (ex.: PDF): registra formato, documento e tela de origem.
            if ("IMPRESSAO".equals(ev.nivel)) {
                String formato = derivarFormato((response != null) ? response.getContentType() : null);
                String telaOrigem = derivarTelaOrigem(
                        (request != null) ? request.getHeader("Referer") : null, rota);
                ev.dadosExtra = montarDadosImpressao(formato, ev.funcionalidade, telaOrigem);
            }

            boolean garantido = (regra != null) && regra.isGarantido();
            enfileirar(ev, garantido);

        } catch (Exception e) {
            // Captura nunca pode quebrar a requisição do usuário.
            log.log(Level.WARNING, "[MotorLogs] Falha na captura automatica", e);
        }
    }

    // ============================================================
    // Internos
    // ============================================================

    private static void preencherUsuario(Evento ev, HttpServletRequest request) {
        if (request == null) return;
        try {
            HttpSession sess = request.getSession(false);
            if (sess != null) {
                Object obj = sess.getAttribute(SessaoConstantes.SESSAO_USUARIO);
                if (obj == null) obj = sess.getAttribute("objUsuario");
                if (obj instanceof com.consilux.model.Usuario) {
                    com.consilux.model.Usuario u = (com.consilux.model.Usuario) obj;
                    ev.login = u.getUsuario();
                    ev.idUsuario = u.getId();
                } else if (obj instanceof muralha.digital.acessos.Usuario) {
                    muralha.digital.acessos.Usuario u = (muralha.digital.acessos.Usuario) obj;
                    ev.login = u.getUsuario();
                    ev.idUsuario = u.getIdUsuario();
                }
            }
        } catch (Exception e) {
            log.log(Level.FINE, "[MotorLogs] Falha ao obter usuario da sessao", e);
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        ev.ip = ip;
    }

    private static void enfileirar(final Evento ev, boolean garantido) {
        ev.dtOperacao = new Timestamp(System.currentTimeMillis());
        try {
            Future<?> f = gravador.submit(() -> gravar(ev));
            if (garantido) {
                try {
                    f.get(3, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "[MotorLogs] Falha ao garantir gravacao sincrona", e);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "[MotorLogs] Fila de gravacao indisponivel", e);
        }
    }

    /** Executado SEMPRE na thread única: calcula o hash encadeado e grava. */
    private static void gravar(Evento ev) {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            if (ultimoHash == null) {
                ultimoHash = carregarUltimoHash(conn);
            }
            String anterior = ultimoHash;
            String hash = calcularHash(anterior, ev);

            String sql =
                "INSERT INTO dbo.sis_log_auditoria " +
                "(id_usuario, login, dt_operacao, ip_terminal, funcionalidade, operacao, " +
                " id_registro, descricao, metodo, rota, status_http, duracao_ms, tipo_captura, " +
                " nivel, id_regra, dados_extra, hash_anterior, hash_evento, selado) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (ev.idUsuario != null) ps.setInt(1, ev.idUsuario); else ps.setNull(1, java.sql.Types.INTEGER);
                ps.setString(2, ev.login);
                ps.setTimestamp(3, ev.dtOperacao);
                ps.setString(4, ev.ip);
                ps.setString(5, ev.funcionalidade);
                ps.setString(6, ev.operacao);
                ps.setString(7, ev.idRegistro);
                ps.setString(8, ev.descricao);
                ps.setString(9, ev.metodo);
                ps.setString(10, ev.rota);
                if (ev.statusHttp != null) ps.setInt(11, ev.statusHttp); else ps.setNull(11, java.sql.Types.INTEGER);
                if (ev.duracaoMs != null) ps.setInt(12, ev.duracaoMs); else ps.setNull(12, java.sql.Types.INTEGER);
                ps.setString(13, ev.tipoCaptura);
                ps.setString(14, ev.nivel);
                if (ev.idRegra != null) ps.setInt(15, ev.idRegra); else ps.setNull(15, java.sql.Types.INTEGER);
                ps.setString(16, ev.dadosExtra);
                ps.setString(17, anterior);
                ps.setString(18, hash);
                ps.executeUpdate();
            }
            ultimoHash = hash; // só avança a cadeia após sucesso
        } catch (Exception e) {
            log.log(Level.SEVERE, "[MotorLogs] Falha ao gravar log: "
                    + ev.funcionalidade + "/" + ev.operacao, e);
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
    }

    private static String carregarUltimoHash(Connection conn) {
        String sql = "SELECT TOP 1 hash_evento FROM dbo.sis_log_auditoria " +
                     "WHERE hash_evento IS NOT NULL ORDER BY id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String h = rs.getString(1);
                if (h != null && !h.trim().isEmpty()) return h.trim();
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "[MotorLogs] Falha ao ler ultimo hash; usando genesis", e);
        }
        return GENESIS;
    }

    private static String calcularHash(String anterior, Evento ev) {
        return hashEvento(anterior, ev.dtOperacao.getTime(), ev.idUsuario, ev.login, ev.ip,
                ev.funcionalidade, ev.operacao, ev.metodo, ev.rota, ev.statusHttp,
                ev.nivel, ev.idRegistro, ev.descricao, ev.dadosExtra);
    }

    /**
     * Função canônica da cadeia de integridade. É a ÚNICA fonte da verdade do
     * hash de um evento, usada tanto na gravação quanto na verificação
     * ({@link SeloService}). Alterar a ordem/forma aqui invalida cadeias antigas.
     */
    public static String hashEvento(String anterior, long dtMillis, Integer idUsuario,
            String login, String ip, String funcionalidade, String operacao, String metodo,
            String rota, Integer statusHttp, String nivel, String idRegistro,
            String descricao, String dadosExtra) {
        StringBuilder sb = new StringBuilder(256);
        sb.append(nz(anterior)).append('|')
          .append(dtMillis).append('|')
          .append(nz(idUsuario)).append('|')
          .append(nz(login)).append('|')
          .append(nz(ip)).append('|')
          .append(nz(funcionalidade)).append('|')
          .append(nz(operacao)).append('|')
          .append(nz(metodo)).append('|')
          .append(nz(rota)).append('|')
          .append(nz(statusHttp)).append('|')
          .append(nz(nivel)).append('|')
          .append(nz(idRegistro)).append('|')
          .append(nz(descricao)).append('|')
          .append(nz(dadosExtra));
        return sha256Hex(sb.toString());
    }

    public static String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(64);
            for (byte b : d) hex.append(Character.forDigit((b >> 4) & 0xF, 16))
                                .append(Character.forDigit(b & 0xF, 16));
            return hex.toString();
        } catch (Exception e) {
            // Sem SHA-256 não há como encadear; devolve genesis para não gravar nulo.
            return GENESIS;
        }
    }

    private static String montarParametros(HttpServletRequest request, Set<String> extras) {
        try {
            JsonObject obj = new JsonObject();
            Enumeration<String> nomes = request.getParameterNames();
            int count = 0;
            while (nomes.hasMoreElements() && count < 50) {
                String nome = nomes.nextElement();
                if ("acao".equalsIgnoreCase(nome)) continue;
                String valor;
                if (Mascarador.campoSensivel(nome, extras)) {
                    valor = "***";
                } else {
                    String[] vs = request.getParameterValues(nome);
                    String v = (vs != null && vs.length > 0) ? vs[0] : "";
                    valor = Mascarador.mascararConteudo(truncar(v, 300));
                }
                obj.addProperty(nome, valor);
                count++;
            }
            return truncar(obj.toString(), 4000);
        } catch (Exception e) {
            return null;
        }
    }

    private static String derivarFuncionalidade(String rota) {
        if (rota == null || rota.isEmpty()) return "(indefinido)";
        String r = rota;
        int q = r.indexOf('?');
        if (q >= 0) r = r.substring(0, q);
        while (r.endsWith("/") && r.length() > 1) r = r.substring(0, r.length() - 1);
        String[] partes = r.split("/");
        int n = partes.length;
        if (n == 0) return r;
        String seg = partes[n - 1];
        if (seg.toLowerCase().endsWith(".jsp")) {
            seg = seg.substring(0, seg.length() - 4);
            // "index"/vazio: usa o nome da pasta da tela (mais significativo)
            if ((seg.isEmpty() || seg.equalsIgnoreCase("index")) && n >= 2) {
                seg = partes[n - 2];
            }
        }
        return seg.isEmpty() ? r : seg;
    }

    /**
     * Deriva o TIPO DE EVENTO (nível) a partir do método HTTP e da ação:
     * ACESSO (abertura de tela, via regra), VISUALIZACAO, CADASTRO, ALTERACAO,
     * EXCLUSAO, EXPORTACAO, ERRO. Base para a configuração por tipo de evento.
     */
    static String derivarNivel(String metodo, String acao, int status, String contentType) {
        if (status >= 500) return "ERRO";
        if (ehPdf(contentType)) return "IMPRESSAO"; // PDF e o sinal mais forte de impressao
        String a = (acao == null) ? "" : acao.toLowerCase();
        if ("DELETE".equalsIgnoreCase(metodo)
                || contemAlgum(a, "excluir", "remover", "deletar", "apagar", "inativar", "encerrar", "desativar", "cancelar"))
            return "EXCLUSAO";
        if (contemAlgum(a, "inserir", "incluir", "novo", "cadastrar", "criar", "adicionar", "gravarnovo", "salvarnovo", "registrar"))
            return "CADASTRO";
        if (contemAlgum(a, "atualizar", "editar", "alterar", "salvar", "update", "modificar", "aprovar", "reprovar", "confirmar", "processar", "mover", "reposicionar", "gravar"))
            return "ALTERACAO";
        if (contemAlgum(a, "exportar", "download", "baixar", "csv", "xls", "pdf", "relatorio", "imprimir", "gerarremessa"))
            return "EXPORTACAO";
        if (contemAlgum(a, "consultar", "listar", "obter", "buscar", "pesquisar", "carregar", "detalhe", "painel", "resumo", "dados", "filtros", "historico", "ver"))
            return "VISUALIZACAO";
        if ("POST".equalsIgnoreCase(metodo) || "PUT".equalsIgnoreCase(metodo) || "PATCH".equalsIgnoreCase(metodo))
            return "ALTERACAO";
        return "VISUALIZACAO"; // GET sem ação reconhecida = visualização
    }

    /** True se o Content-Type indica PDF (ignora caixa e espacos a esquerda). */
    static boolean ehPdf(String contentType) {
        if (contentType == null) return false;
        return contentType.trim().toLowerCase().startsWith("application/pdf");
    }

    /** Formato do documento imprimivel: "PDF" ou null. */
    static String derivarFormato(String contentType) {
        return ehPdf(contentType) ? "PDF" : null;
    }

    /** JSON {formato, documento, telaOrigem} de um evento de impressão; chaves nulas omitidas. */
    static String montarDadosImpressao(String formato, String documento, String telaOrigem) {
        JsonObject o = new JsonObject();
        if (formato != null) o.addProperty("formato", formato);
        if (documento != null) o.addProperty("documento", documento);
        if (telaOrigem != null) o.addProperty("telaOrigem", telaOrigem);
        return truncar(o.toString(), 4000);
    }

    /** Caminho do Referer (sem esquema, host e query); sem Referer, usa a rota de fallback. */
    static String derivarTelaOrigem(String referer, String rotaFallback) {
        if (referer == null || referer.trim().isEmpty()) return rotaFallback;
        try {
            String path = new java.net.URI(referer.trim()).getPath();
            if (path != null && !path.isEmpty()) return truncar(path, 400);
        } catch (Exception ignorada) {
            // Referer malformado: cai no fallback.
        }
        return rotaFallback;
    }

    private static boolean contemAlgum(String texto, String... termos) {
        for (String t : termos) if (texto.contains(t)) return true;
        return false;
    }

    private static String primeiroParam(HttpServletRequest request, String... nomes) {
        for (String n : nomes) {
            String v = request.getParameter(n);
            if (v != null && !v.isEmpty()) return truncar(v, 100);
        }
        return null;
    }

    private static String nz(Object o) { return (o == null) ? "" : String.valueOf(o); }

    private static String truncar(String s, int max) {
        if (s == null) return null;
        return (s.length() <= max) ? s : s.substring(0, max);
    }

    /** Dados de um evento em trânsito até a gravação. */
    private static final class Evento {
        Integer   idUsuario;
        String    login;
        Timestamp dtOperacao;
        String    ip;
        String    funcionalidade;
        String    operacao;
        String    idRegistro;
        String    descricao;
        String    metodo;
        String    rota;
        Integer   statusHttp;
        Integer   duracaoMs;
        String    tipoCaptura;
        String    nivel;
        Integer   idRegra;
        String    dadosExtra;
    }
}
