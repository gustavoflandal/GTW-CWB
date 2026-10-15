package muralha.digital.chamado;

import com.consilux.lib.Conexao;
import muralha.digital.auditoria.AuditoriaService;
import muralha.digital.chamado.CalculadoraSla.Marca;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Paths;
import java.sql.Connection;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Regras do Sistema de Chamados; cada operacao roda em uma transacao (spec §3.1). */
public class ChamadoService {

    private static final Logger logger = Logger.getLogger(ChamadoService.class);

    private final Clock clock;

    public ChamadoService() {
        this(Clock.systemDefaultZone());
    }

    public ChamadoService(Clock clock) {
        this.clock = clock;
    }

    /** Milissegundos, como DATETIME2(3): evita divergencia entre memoria e banco. */
    private LocalDateTime agora() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.MILLIS);
    }

    public static class Operador {
        public final int idUsuario;
        public final Set<PerfilChamado> perfis;
        public final HttpServletRequest req;

        public Operador(int idUsuario, Set<PerfilChamado> perfis, HttpServletRequest req) {
            this.idUsuario = idUsuario;
            this.perfis = perfis;
            this.req = req;
        }

        public boolean veTodos() {
            return PerfilChamado.veTodos(perfis);
        }
    }

    public static class NovoChamado {
        public String titulo;
        public String descricao;
        public int idCategoria;
        public int idPrioridade;
        public Integer idLocal;
    }

    public static class ParametrosAcao {
        public String texto;
        public Integer idPrioridade;
        public Integer nota;
        public List<ArquivoEnviado> arquivos = new ArrayList<>();
    }

    public Chamado abrir(Operador op, NovoChamado d, List<ArquivoEnviado> arquivos) throws Exception {
        if (!op.perfis.contains(PerfilChamado.SOLICITANTE) && !op.perfis.contains(PerfilChamado.GESTOR)) {
            throw new RegraChamadoException("Seu perfil não permite abrir chamados.");
        }
        String titulo = obrigatorio(d.titulo, "Título", 150);
        String descricao = obrigatorio(d.descricao, "Descrição", 8000);
        ArmazenamentoAnexo.validarQuantidade(arquivos);

        Connection conn = null;
        ArmazenamentoAnexo armazenamento = null;
        List<String> gravados = new ArrayList<>();
        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false);
            Categoria cat = ChamadoConfigDao.obterCategoria(conn, d.idCategoria);
            if (cat == null || !cat.ativo) throw new RegraChamadoException("Categoria inválida.");
            Prioridade pri = ChamadoConfigDao.obterPrioridade(conn, d.idPrioridade);
            if (pri == null || !pri.ativo) throw new RegraChamadoException("Prioridade inválida.");
            if (cat.exigeEquipamento && d.idLocal == null) {
                throw new RegraChamadoException("Esta categoria exige a indicação do equipamento.");
            }
            if (d.idLocal != null && !ChamadoConfigDao.equipamentoExiste(conn, d.idLocal)) {
                throw new RegraChamadoException("Equipamento inválido.");
            }

            LocalDateTime agora = agora();
            Chamado c = new Chamado();
            c.protocolo = GeradorProtocolo.proximo(conn, agora.getYear());
            c.titulo = titulo;
            c.descricao = descricao;
            c.idCategoria = cat.id;
            c.idPrioridade = pri.id;
            c.idPrioridadeSolicitada = pri.id;
            c.status = StatusChamado.ABERTO;
            c.idLocal = d.idLocal;
            c.idUsuarioSolicitante = op.idUsuario;
            c.dtAbertura = agora;
            c.dtPrazoAtendimento = CalculadoraSla.prazoAtendimento(agora, pri.minutosAtendimento);
            c.dtPrazoResolucao = CalculadoraSla.prazoResolucao(agora, pri.minutosResolucao, 0);
            c.nomePrioridade = pri.nome;
            c.id = ChamadoDao.inserir(conn, c);

            long idHist = ChamadoDao.inserirHistorico(conn, c.id, op.idUsuario, "ABERTURA", null,
                    StatusChamado.ABERTO.name(), "Chamado aberto com prioridade " + pri.nome + ".");
            if (arquivos != null && !arquivos.isEmpty()) {
                armazenamento = armazenamento(conn);
                gravarAnexos(conn, armazenamento, c, idHist, op.idUsuario, arquivos, gravados);
            }
            ChamadoDao.enfileirarNotificacao(conn, c.id, EmailChamado.CONFIRMACAO_ABERTURA,
                    ChamadoConfigDao.emailUsuario(conn, op.idUsuario));
            conn.commit();
            auditar(op, "ABRIR", c.protocolo, "Chamado aberto: " + titulo);
            return c;
        } catch (Exception e) {
            desfazer(conn, armazenamento, gravados);
            throw e;
        } finally {
            fechar(conn);
        }
    }

    public Chamado executar(Operador op, String protocolo, AcaoChamado acao, ParametrosAcao p) throws Exception {
        if (acao == null) throw new RegraChamadoException("Ação inválida.");
        String texto = acao.exigeTexto() ? obrigatorio(p.texto, "Observação", 2000) : opcional(p.texto, 1000);
        List<ArquivoEnviado> arquivos = p.arquivos == null ? List.of() : p.arquivos;
        if (!arquivos.isEmpty() && acao != AcaoChamado.RESPONDER) {
            throw new RegraChamadoException("Anexos só podem ser enviados na abertura ou na resposta.");
        }
        ArmazenamentoAnexo.validarQuantidade(arquivos);

        Connection conn = null;
        ArmazenamentoAnexo armazenamento = null;
        List<String> gravados = new ArrayList<>();
        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false);
            Chamado c = obterVisivel(conn, op, protocolo);
            boolean autor = c.idUsuarioSolicitante == op.idUsuario;
            StatusChamado anterior = c.status;
            StatusChamado destino = MaquinaEstados.destino(anterior, acao, op.perfis, autor);
            LocalDateTime agora = agora();
            int risco = ChamadoConfigDao.valorInt(conn, "chamado_sla_risco_percentual", 80);
            String tipoHistorico = "TRANSICAO";
            boolean notificarReclassificacao = false;

            switch (acao) {
                case ASSUMIR:
                    c.idUsuarioResponsavel = op.idUsuario;
                    tipoHistorico = "ATRIBUICAO";
                    if (texto == null) texto = "Chamado assumido.";
                    break;
                case INICIAR:
                    if (c.idUsuarioResponsavel == null) c.idUsuarioResponsavel = op.idUsuario;
                    c.dtPrimeiroAtendimento = agora;
                    break;
                case AGUARDAR:
                    c.dtInicioPausa = agora;
                    break;
                case RESPONDER:
                    encerrarPausa(c, agora);
                    tipoHistorico = "RESPOSTA";
                    break;
                case RESOLVER:
                    c.dtResolucao = agora;
                    c.dtInicioPausa = agora;
                    break;
                case AVALIAR:
                    if (p.nota == null || p.nota < 1 || p.nota > 5) {
                        throw new RegraChamadoException("Informe uma nota de 1 a 5.");
                    }
                    c.avaliacaoNota = p.nota;
                    c.avaliacaoComentario = texto;
                    c.dtAvaliacao = agora;
                    c.dtFechamento = agora;
                    c.dtInicioPausa = null;
                    tipoHistorico = "AVALIACAO";
                    texto = "Nota " + p.nota + (texto == null ? "." : ": " + texto);
                    break;
                case REABRIR:
                    encerrarPausa(c, agora);
                    c.dtResolucao = null;
                    break;
                case CANCELAR:
                    c.dtFechamento = agora;
                    c.dtInicioPausa = null;
                    break;
                case RECLASSIFICAR:
                    if (p.idPrioridade == null) throw new RegraChamadoException("Informe a nova prioridade.");
                    Prioridade nova = ChamadoConfigDao.obterPrioridade(conn, p.idPrioridade);
                    if (nova == null || !nova.ativo) throw new RegraChamadoException("Prioridade inválida.");
                    if (nova.id == c.idPrioridade) throw new RegraChamadoException("O chamado já está com essa prioridade.");
                    texto = "Prioridade alterada de " + c.nomePrioridade + " para " + nova.nome + ". Justificativa: " + texto;
                    c.idPrioridade = nova.id;
                    c.nomePrioridade = nova.nome;
                    c.minutosResolucaoPrioridade = nova.minutosResolucao;
                    c.dtPrazoAtendimento = CalculadoraSla.prazoAtendimento(c.dtAbertura, nova.minutosAtendimento);
                    c.dtPrazoResolucao = CalculadoraSla.prazoResolucao(c.dtAbertura, nova.minutosResolucao, c.minutosPausados);
                    tipoHistorico = "RECLASSIFICACAO";
                    notificarReclassificacao = true;
                    break;
                default:
                    break;
            }
            c.status = destino;
            recalcularMarcas(c, agora, risco);

            if (ChamadoDao.atualizarEstado(conn, c, anterior) == 0) {
                throw new RegraChamadoException("Chamado alterado por outro usuário; recarregue a página.");
            }
            long idHist = ChamadoDao.inserirHistorico(conn, c.id, op.idUsuario, tipoHistorico,
                    anterior.name(), destino.name(), texto);
            if (!arquivos.isEmpty()) {
                armazenamento = armazenamento(conn);
                gravarAnexos(conn, armazenamento, c, idHist, op.idUsuario, arquivos, gravados);
            }
            if (notificarReclassificacao) {
                ChamadoDao.enfileirarNotificacao(conn, c.id, EmailChamado.RECLASSIFICACAO, c.emailSolicitante);
            }
            conn.commit();
            auditar(op, acao.name(), c.protocolo, texto);
            return c;
        } catch (Exception e) {
            desfazer(conn, armazenamento, gravados);
            throw e;
        } finally {
            fechar(conn);
        }
    }

    /** Lanca ChamadoNaoEncontradoException tambem quando o chamado e de outro solicitante (spec §3.3). */
    public Chamado obterVisivel(Connection conn, Operador op, String protocolo) throws Exception {
        Chamado c = ChamadoDao.obterPorProtocolo(conn, protocolo);
        if (c == null || (!op.veTodos() && c.idUsuarioSolicitante != op.idUsuario)) {
            throw new ChamadoNaoEncontradoException(protocolo);
        }
        return c;
    }

    /** Job: atualiza marcas de SLA e registra no historico a entrada em EM_RISCO/VENCIDO. */
    public void atualizarSla() throws Exception {
        try (Connection conn = Conexao.getConexao()) {
            int risco = ChamadoConfigDao.valorInt(conn, "chamado_sla_risco_percentual", 80);
            LocalDateTime agora = agora();
            for (Chamado c : ChamadoDao.listarNaoTerminais(conn)) {
                Marca antesAt = c.slaAtendimento;
                Marca antesRes = c.slaResolucao;
                recalcularMarcas(c, agora, risco);
                if (antesAt == c.slaAtendimento && antesRes == c.slaResolucao) continue;
                if (ChamadoDao.atualizarEstado(conn, c, c.status) == 0) continue; // alterado agora por alguem
                if (alerta(antesAt, c.slaAtendimento)) {
                    ChamadoDao.inserirHistorico(conn, c.id, null, "SLA", null, null,
                            "SLA de atendimento: " + c.slaAtendimento.name());
                }
                if (alerta(antesRes, c.slaResolucao)) {
                    ChamadoDao.inserirHistorico(conn, c.id, null, "SLA", null, null,
                            "SLA de resolução: " + c.slaResolucao.name());
                }
            }
        }
    }

    /** Job: fecha RESOLVIDO sem acao do solicitante ha N dias (spec D7). */
    public int fecharResolvidosAntigos() throws Exception {
        int fechados = 0;
        try (Connection conn = Conexao.getConexao()) {
            int dias = ChamadoConfigDao.valorInt(conn, "chamado_fechamento_auto_dias", 5);
            LocalDateTime agora = agora();
            for (Chamado c : ChamadoDao.listarResolvidosAntesDe(conn, agora.minusDays(dias))) {
                conn.setAutoCommit(false);
                try {
                    c.status = StatusChamado.FECHADO;
                    c.dtFechamento = agora;
                    c.fechadoAutomaticamente = true;
                    c.dtInicioPausa = null;
                    if (ChamadoDao.atualizarEstado(conn, c, StatusChamado.RESOLVIDO) == 0) {
                        conn.rollback();
                        continue;
                    }
                    ChamadoDao.inserirHistorico(conn, c.id, null, "TRANSICAO", StatusChamado.RESOLVIDO.name(),
                            StatusChamado.FECHADO.name(),
                            "Fechado automaticamente após " + dias + " dias sem avaliação do solicitante.");
                    conn.commit();
                    fechados++;
                } catch (Exception e) {
                    conn.rollback();
                    logger.error("Falha ao fechar automaticamente o chamado " + c.protocolo, e);
                } finally {
                    conn.setAutoCommit(true);
                }
            }
        }
        return fechados;
    }

    public static ArmazenamentoAnexo armazenamento(Connection conn) throws Exception {
        String dir = ChamadoConfigDao.valor(conn, "chamado_diretorio_anexos", null);
        if (dir == null) throw new RegraChamadoException("Diretório de anexos não configurado.");
        long maxBytes = ChamadoConfigDao.valorInt(conn, "chamado_anexo_max_mb", 10) * 1024L * 1024L;
        return new ArmazenamentoAnexo(Paths.get(dir), maxBytes);
    }

    static void encerrarPausa(Chamado c, LocalDateTime agora) {
        if (c.dtInicioPausa == null) return;
        c.minutosPausados += CalculadoraSla.minutosEntre(c.dtInicioPausa, agora);
        c.dtInicioPausa = null;
        c.dtPrazoResolucao = CalculadoraSla.prazoResolucao(c.dtAbertura, c.minutosResolucaoPrioridade, c.minutosPausados);
    }

    static void recalcularMarcas(Chamado c, LocalDateTime agora, int percentualRisco) {
        c.slaAtendimento = CalculadoraSla.marca(c.dtAbertura, c.dtPrazoAtendimento, c.dtPrimeiroAtendimento,
                agora, percentualRisco, false, c.slaAtendimento);
        c.slaResolucao = CalculadoraSla.marca(c.dtAbertura, c.dtPrazoResolucao, c.dtResolucao,
                agora, percentualRisco, c.dtInicioPausa != null, c.slaResolucao);
    }

    private static boolean alerta(Marca antes, Marca depois) {
        return antes != depois && (depois == Marca.EM_RISCO || depois == Marca.VENCIDO);
    }

    private static void gravarAnexos(Connection conn, ArmazenamentoAnexo armazenamento, Chamado c, long idHist,
                                     int idUsuario, List<ArquivoEnviado> arquivos, List<String> gravados)
            throws Exception {
        for (ArquivoEnviado arq : arquivos) {
            AnexoGravado a;
            try {
                a = armazenamento.gravar(c.protocolo, arq);
            } catch (java.io.IOException e) {
                logger.error("Falha ao gravar anexo do chamado " + c.protocolo, e);
                throw new RegraChamadoException("Não foi possível gravar os anexos; tente novamente.");
            }
            gravados.add(a.caminhoRelativo);
            ChamadoDao.inserirAnexo(conn, c.id, idHist, a, idUsuario);
        }
    }

    private static void desfazer(Connection conn, ArmazenamentoAnexo armazenamento, List<String> gravados) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (Exception e) {
                logger.error("Falha no rollback de chamado", e);
            }
        }
        if (armazenamento != null) {
            for (String g : gravados) armazenamento.removerSilenciosamente(g);
        }
    }

    private static void fechar(Connection conn) {
        if (conn == null) return;
        try {
            conn.setAutoCommit(true);
            conn.close();
        } catch (Exception ignorada) {
            // conexao devolvida ao pool
        }
    }

    private static void auditar(Operador op, String operacao, String protocolo, String descricao) {
        if (op.req == null) return;
        String d = descricao == null ? "" : (descricao.length() > 500 ? descricao.substring(0, 500) : descricao);
        AuditoriaService.registrar(op.req, "Chamado", operacao, protocolo, d);
    }

    private static String obrigatorio(String v, String campo, int max) {
        if (v == null || v.trim().isEmpty()) throw new RegraChamadoException(campo + " é obrigatório(a).");
        String t = v.trim();
        if (t.length() > max) throw new RegraChamadoException(campo + " deve ter até " + max + " caracteres.");
        return t;
    }

    private static String opcional(String v, int max) {
        if (v == null || v.trim().isEmpty()) return null;
        String t = v.trim();
        return t.length() > max ? t.substring(0, max) : t;
    }
}
