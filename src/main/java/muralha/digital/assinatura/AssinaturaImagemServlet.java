/**********************************************************************************
    Projeto: Muralha Digital
    Empresa: Consilux Tecnologia
    Autor: Thiago Guislotti
    Data: 17/09/2025
 *********************************************************************************/
package muralha.digital.assinatura;

import com.consilux.infra.UpArq;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servlet para assinar e verificar imagens via upload multipart.
 *
 * Endpoints:
 *  - POST /muralha-digital/assinatura/assinar    (part "imagem")
 *  - POST /muralha-digital/assinatura/verificar  (part "imagem" + part "assinatura" OU param "assinaturaBase64")
 */
@WebServlet(urlPatterns = "/muralha-digital/assinatura/*")
public class AssinaturaImagemServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    /** Cache de instâncias do serviço de assinatura */
    private static final ConcurrentHashMap<String, AssinaturaImagemService> SERVICE_CACHE = new ConcurrentHashMap<>();

    /**
     * Processa requisição POST para assinar ou verificar imagem.
     * Rota é determinada pela URL (assinar ou verificar).
     * 
     * @param req - Requisição HTTP
     * @param resp - Resposta HTTP
     * @throws ServletException - Em caso de erro no processamento
     * @throws IOException - Em caso de erro de I/O
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) {
            path = "";
        }
        
        String paginaDestino = null;
        try {
            if (path.endsWith("/assinar")) {
                paginaDestino = "/muralha-digital/pages/assinatura/assinar-imagem.jsp";
                req.setAttribute("acao", "assinar");
                processarAssinatura(req, resp);
            } else if (path.endsWith("/verificar")) {
                paginaDestino = "/muralha-digital/pages/assinatura/validacao-imagem.jsp";
                req.setAttribute("acao", "verificar");
                processarVerificacao(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Rota não encontrada: " + path);
            }
        } catch (Exception e) {
            req.setAttribute("erro", "Erro técnico durante a validação: " + e.getMessage());
        } finally {
            req.getRequestDispatcher(paginaDestino).forward(req, resp);
        }
    }

    /**
     * Processa requisição de assinatura de imagem digital.
     * @param req Requisição HTTP contendo arquivo de imagem
     * @param resp Resposta HTTP para redirecionamento
     * @throws Exception Em caso de erro no processamento ou validação
     */
    private void processarAssinatura(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        UpArq up = new UpArq(req);
        TipoSolicitanteAssinatura tipoSolicitante = determinarTipoSolicitante(req, up);

        FileItem imagemItem = obterItem(up.getArqs(), "imagem");
        if (imagemItem == null) {
            throw new IllegalArgumentException("Arquivo de imagem não enviado");
        }

        byte[] bytesImagem = imagemItem.get();
        String nomeArquivo = extrairNomeArquivo(imagemItem, "imagem.bin");

        req.setAttribute("nomeArquivo", nomeArquivo);
        req.setAttribute("tipoSolicitante", tipoSolicitante.getDescricao());
        
        if (!ehJPEG(bytesImagem, imagemItem.getContentType(), nomeArquivo)) {
            definirMetadadosResposta(req, false, StatusAssinatura.INVALIDA, "Formato inválido. Use apenas JPEG.");
            return;
        }

        AssinaturaImagemService svc = obterServicoAssinatura();
        
        if (svc.verificarImagemComAssinatura(bytesImagem)) {
            req.setAttribute("sha256", svc.obterHashImagemOriginal(bytesImagem));
            definirMetadadosResposta(req, false, StatusAssinatura.JA_ASSINADA, null);
            return;
        }

        byte[] imagemAssinada = svc.assinarImagem(bytesImagem, tipoSolicitante);

        req.setAttribute("sha256", AssinaturaImagemService.sha256Hex(bytesImagem));
        req.setAttribute("imagemAssinada", java.util.Base64.getEncoder().encodeToString(imagemAssinada));
        definirMetadadosResposta(req, true, StatusAssinatura.ASSINADA, null);
    }

    /**
     * Processa requisição de verificação de assinatura digital.
     * @param req Requisição HTTP contendo arquivo de imagem para verificação
     * @param resp Resposta HTTP para redirecionamento
     * @throws Exception Em caso de erro na verificação ou validação
     */
    private void processarVerificacao(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        UpArq up = new UpArq(req);
        
        FileItem imagemItem = obterItem(up.getArqs(), "imagem");
        if (imagemItem == null) {
            throw new IllegalArgumentException("Arquivo de imagem não enviado");
        }
        
        byte[] bytesImagem = imagemItem.get();
        String nomeArquivo = extrairNomeArquivo(imagemItem, "imagem.bin");

        if (!ehJPEG(bytesImagem, imagemItem.getContentType(), nomeArquivo)) {
            definirMetadadosResposta(req, false, StatusAssinatura.INVALIDA, "Formato inválido. Use apenas JPEG.");
            return;
        }

        AssinaturaImagemService svc = obterServicoAssinatura();
        boolean assinaturaValida = svc.verificarAssinaturaValida(bytesImagem);
        StatusAssinatura status = StatusAssinatura.VALIDA;
        
        if (assinaturaValida) {
            req.setAttribute("sha256", svc.obterHashImagemOriginal(bytesImagem));
        } else {
            boolean temAssinatura = svc.verificarImagemComAssinatura(bytesImagem);
            if (temAssinatura) {
                status = StatusAssinatura.CORROMPIDA;
            } else {
                status = StatusAssinatura.SEM_ASSINATURA;
            }
        }

        req.setAttribute("nomeArquivo", nomeArquivo);
        req.setAttribute("tipoSolicitante", svc.obterTipoSolicitante(bytesImagem).getDescricao());
        definirMetadadosResposta(req, assinaturaValida, status, null);
    }
    
    /**
     * Determina o tipo de solicitante baseado na origem obrigatória da requisição.
     * Valida se origem (nome do JSP) foi informada e é válida.
     * @param req - Requisição HTTP para análise
     * @param up - Dados do upload (pode ser null)
     * @return Tipo do solicitante baseado na origem informada
     * @throws IllegalArgumentException se origem não foi informada ou é inválida
     */
    private TipoSolicitanteAssinatura determinarTipoSolicitante(HttpServletRequest req, UpArq up) throws IllegalArgumentException {
        String mdOrigin = up.getCampos().get("md_origin");
        if (mdOrigin == null || mdOrigin.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo md_origin é obrigatório - deve informar o nome do JSP de origem");
        }
        
        if ("assinar-imagem.jsp".equals(mdOrigin)) {
            return TipoSolicitanteAssinatura.MANUAL;
        }
        
        return TipoSolicitanteAssinatura.SISTEMA;
    }

    /**
     * Obtém instância do serviço de assinatura com cache.
     */
    private AssinaturaImagemService obterServicoAssinatura() throws Exception {
        String cacheKey = "default";
        return SERVICE_CACHE.computeIfAbsent(cacheKey, k -> {
            try {
                AssinaturaConfig cfg = AssinaturaConfig.carregar();
                return new AssinaturaImagemService(cfg);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao criar serviço de assinatura", e);
            }
        });
    }

    /**
     * Define metadados da operação na requisição e redireciona para a página de resultado.
     * @param req Requisição HTTP
     * @param ok Status de sucesso da operação
     * @param status Status descritivo da operação
     * @param erro Mensagem de erro (opcional)
     * @throws Exception Em caso de erro no redirecionamento
     */
    private void definirMetadadosResposta(HttpServletRequest req,  
        boolean ok, StatusAssinatura status, String erro) throws Exception {
        req.setAttribute("ok", ok);
        req.setAttribute("status", status.getDescricao());

        if (erro != null) {
            req.setAttribute("erro", erro);
        }
    }

    /**
     * Busca um FileItem específico na lista de itens do upload por nome do campo.
     * @param itens - Lista de FileItems do upload multipart
     * @param fieldName - Nome do campo a ser procurado
     * @return FileItem encontrado ou null se não existir
     */
    private static FileItem obterItem(List<FileItem> itens, String fieldName) {
        if (itens == null) {
            return null;
        }
        for (FileItem it : itens) {
            if (!it.isFormField() && fieldName.equals(it.getFieldName())) {
                return it;
            }
        }
        return null;
    }

    /**
     * Extrai nome do arquivo do FileItem, aplicando sanitização básica.
     * @param item - FileItem contendo o arquivo
     * @param defaultName - Nome padrão caso não seja possível extrair
     * @return Nome do arquivo sanitizado ou nome padrão
     */
    private static String extrairNomeArquivo(FileItem item, String defaultName) {
        try {
            String n = item.getName();
            if (n == null || n.trim().isEmpty()) {
                return defaultName;
            }
            // Normaliza separadores substituindo \\ por /
            n = n.replace((char)92, '/');
            int idx = n.lastIndexOf('/');
            return idx >= 0 ? n.substring(idx + 1) : n;
        } catch (Exception e) {
            return defaultName;
        }
    }

    /**
     * Valida se o arquivo é uma imagem JPEG válida.
     * Verifica content-type, extensão e magic bytes do formato JPEG.
     * @param data - Bytes da imagem a ser validada
     * @param contentType - Content-Type HTTP do arquivo
     * @param fileName - Nome do arquivo para validação de extensão
     * @return true se for JPEG válido, false caso contrário
     */
    private static boolean ehJPEG(byte[] data, String contentType, String fileName) {
        try {
            // Content-Type deve ser image/jpeg quando disponível
            if (contentType != null && !contentType.toLowerCase().contains("image/jpeg")) {
                // Alguns browsers mandam vazio; então só bloqueie quando for claramente outro tipo
                if (!contentType.trim().isEmpty()) {
                    return false;
                }
            }

            // Extensão ajuda, mas não é determinante
            String fn = fileName != null ? fileName.toLowerCase() : "";
            boolean extOk = fn.endsWith(".jpg") || fn.endsWith(".jpeg");

            // Magic bytes JPEG: início FF D8 e, em geral, final FF D9
            if (data == null || data.length < 4) {
                return false;
            }
            int b0 = data[0] & 0xFF;
            int b1 = data[1] & 0xFF;
            boolean magicIniOk = (b0 == 0xFF && b1 == 0xD8);
            boolean magicFimOk = (data[data.length - 2] & 0xFF) == 0xFF && (data[data.length - 1] & 0xFF) == 0xD9;

            if (!magicIniOk) {
                return false;
            }

            // Validação adicional via ImageIO
            try (ByteArrayInputStream bin = new ByteArrayInputStream(data)) {
                BufferedImage img = ImageIO.read(bin);
                if (img == null) return false; // não é imagem válida
            }

            // Se passou nos magic bytes + leitura, aceitamos como JPEG mesmo se faltar FF D9 (arquivos truncados às vezes)
            return extOk || magicFimOk;
        } catch (Exception e) {
            return false;
        }
    }
}