package muralha.digital.chamado;

import com.consilux.lib.Conexao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/MuralhaDigital/Chamado")
public class ChamadoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ChamadoServlet.class);
    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final ChamadoService service = new ChamadoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ContextoRequisicao ctx = ContextoRequisicao.de(req, resp);
        if (ctx == null) return;
        String acao = req.getParameter("acao");
        try (Connection conn = Conexao.getConexao()) {
            if ("anexo".equals(acao)) {
                baixarAnexo(conn, ctx, req, resp);
                return;
            }
            JsonObject r = new JsonObject();
            if ("perfil".equals(acao)) {
                JsonArray perfis = new JsonArray();
                ctx.operador.perfis.forEach(p -> perfis.add(p.name()));
                r.add("perfis", perfis);
                r.addProperty("idUsuario", ctx.operador.idUsuario);
            } else if ("opcoes".equals(acao)) {
                JsonArray pri = new JsonArray();
                ChamadoConfigDao.prioridades(conn, true).forEach(p -> pri.add(p.json()));
                JsonArray cat = new JsonArray();
                ChamadoConfigDao.categorias(conn, true).forEach(c -> cat.add(c.json()));
                r.add("prioridades", pri);
                r.add("categorias", cat);
                r.add("equipamentos", ChamadoConfigDao.equipamentos(conn));
            } else if ("listar".equals(acao)) {
                FiltroChamados f = FiltroChamados.deRequest(req);
                if (!ctx.operador.veTodos()) f.idSolicitante = ctx.operador.idUsuario;
                r = ChamadoDao.listar(conn, f);
                r.addProperty("servidorAgora", LocalDateTime.now().format(ISO));
            } else if ("detalhe".equals(acao)) {
                Chamado c = service.obterVisivel(conn, ctx.operador, req.getParameter("protocolo"));
                boolean autor = c.idUsuarioSolicitante == ctx.operador.idUsuario;
                r.add("chamado", ChamadoDao.json(c));
                r.add("historico", ChamadoDao.historico(conn, c.id));
                r.add("anexos", ChamadoDao.anexos(conn, c.id));
                JsonArray acoes = new JsonArray();
                MaquinaEstados.permitidas(c.status, ctx.operador.perfis, autor).forEach(a -> acoes.add(a.getCodigo()));
                r.add("acoesPermitidas", acoes);
                r.addProperty("servidorAgora", LocalDateTime.now().format(ISO));
            } else {
                throw new RegraChamadoException("Ação inválida.");
            }
            ContextoRequisicao.json(resp, r);
        } catch (Exception e) {
            tratar(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ContextoRequisicao ctx = ContextoRequisicao.de(req, resp);
        if (ctx == null) return;
        try {
            Formulario f = lerFormulario(req);
            String acao = f.campos.get("acao");
            JsonObject r = new JsonObject();
            if ("abrir".equals(acao)) {
                ChamadoService.NovoChamado d = new ChamadoService.NovoChamado();
                d.titulo = f.campos.get("titulo");
                d.descricao = f.campos.get("descricao");
                d.idCategoria = inteiro(f.campos.get("idCategoria"), "categoria");
                d.idPrioridade = inteiro(f.campos.get("idPrioridade"), "prioridade");
                String idLocal = f.campos.get("idLocal");
                d.idLocal = idLocal == null || idLocal.isEmpty() ? null : inteiro(idLocal, "equipamento");
                Chamado c = service.abrir(ctx.operador, d, f.arquivos);
                r.addProperty("protocolo", c.protocolo);
                r.addProperty("prioridade", c.nomePrioridade);
                r.addProperty("prazoAtendimento", c.dtPrazoAtendimento.format(ISO));
                r.addProperty("prazoResolucao", c.dtPrazoResolucao.format(ISO));
            } else {
                ChamadoService.ParametrosAcao p = new ChamadoService.ParametrosAcao();
                p.texto = f.campos.get("texto");
                String idPri = f.campos.get("idPrioridade");
                p.idPrioridade = idPri == null || idPri.isEmpty() ? null : inteiro(idPri, "prioridade");
                String nota = f.campos.get("nota");
                p.nota = nota == null || nota.isEmpty() ? null : inteiro(nota, "nota");
                p.arquivos = f.arquivos;
                Chamado c = service.executar(ctx.operador, f.campos.get("protocolo"), AcaoChamado.deCodigo(acao), p);
                r.addProperty("status", c.status.name());
                r.addProperty("prazoAtendimento", c.dtPrazoAtendimento.format(ISO));
                r.addProperty("prazoResolucao", c.dtPrazoResolucao.format(ISO));
            }
            ContextoRequisicao.json(resp, r);
        } catch (Exception e) {
            tratar(resp, e);
        }
    }

    private void baixarAnexo(Connection conn, ContextoRequisicao ctx, HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        JsonObject a = ChamadoDao.obterAnexo(conn, Long.parseLong(req.getParameter("id")));
        if (a == null) throw new ChamadoNaoEncontradoException("anexo");
        service.obterVisivel(conn, ctx.operador, a.get("protocolo").getAsString());
        Path arquivo = ChamadoService.armazenamento(conn).resolver(a.get("caminho").getAsString());
        if (!Files.exists(arquivo)) throw new ChamadoNaoEncontradoException("anexo");
        String nome = URLEncoder.encode(a.get("nome").getAsString(), StandardCharsets.UTF_8).replace("+", "%20");
        try (InputStream in = Files.newInputStream(arquivo)) {
            resp.setContentType(a.get("tipoMime").getAsString());
            resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + nome);
            resp.setHeader("X-Content-Type-Options", "nosniff");
            resp.setHeader("Content-Length", String.valueOf(Files.size(arquivo)));
            in.transferTo(resp.getOutputStream());
        }
    }

    private static final class Formulario {
        final Map<String, String> campos = new HashMap<>();
        final List<ArquivoEnviado> arquivos = new ArrayList<>();
    }

    private Formulario lerFormulario(HttpServletRequest req) throws Exception {
        Formulario f = new Formulario();
        if (!ServletFileUpload.isMultipartContent(req)) {
            req.setCharacterEncoding("UTF-8");
            for (Enumeration<String> nomes = req.getParameterNames(); nomes.hasMoreElements(); ) {
                String nome = nomes.nextElement();
                f.campos.put(nome, req.getParameter(nome));
            }
            return f;
        }
        long maxBytes;
        try (Connection conn = Conexao.getConexao()) {
            maxBytes = ChamadoConfigDao.valorInt(conn, "chamado_anexo_max_mb", 10) * 1024L * 1024L;
        }
        DiskFileItemFactory fabrica = new DiskFileItemFactory();
        fabrica.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(fabrica);
        upload.setHeaderEncoding("UTF-8");
        upload.setFileSizeMax(maxBytes);
        upload.setSizeMax(maxBytes * ArmazenamentoAnexo.MAX_ARQUIVOS + 1024L * 1024L);
        try {
            for (FileItem item : upload.parseRequest(req)) {
                if (item.isFormField()) {
                    f.campos.put(item.getFieldName(), item.getString("UTF-8"));
                } else if (item.getSize() > 0) {
                    f.arquivos.add(new ArquivoEnviado(item.getName(), item.get()));
                }
            }
        } catch (FileUploadBase.FileSizeLimitExceededException | FileUploadBase.SizeLimitExceededException e) {
            throw new RegraChamadoException("Arquivo(s) acima do limite de " + (maxBytes / (1024 * 1024)) + " MB.");
        }
        return f;
    }

    private static int inteiro(String v, String campo) {
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            throw new RegraChamadoException("Informe " + campo + ".");
        }
    }

    static void tratar(HttpServletResponse resp, Exception e) throws IOException {
        if (resp.isCommitted()) {
            logger.error("Erro apos inicio da resposta", e);
            return;
        }
        resp.reset();
        if (e instanceof ChamadoNaoEncontradoException) {
            ContextoRequisicao.erro(resp, HttpServletResponse.SC_NOT_FOUND, "Chamado não encontrado.");
        } else if (e instanceof RegraChamadoException) {
            ContextoRequisicao.erro(resp, HttpServletResponse.SC_OK, e.getMessage());
        } else {
            logger.error("Erro no Sistema de Chamados", e);
            ContextoRequisicao.erro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno.");
        }
    }
}
