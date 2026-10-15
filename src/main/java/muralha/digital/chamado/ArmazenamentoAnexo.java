package muralha.digital.chamado;

import muralha.digital.util.HashUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/** Valida e grava anexos fora do webapp (spec §3.4). */
public final class ArmazenamentoAnexo {

    public static final int MAX_ARQUIVOS = 5;

    private static final Map<String, String> MIME = Map.of(
            "pdf", "application/pdf",
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg");

    private final Path raiz;
    private final long maxBytes;

    public ArmazenamentoAnexo(Path raiz, long maxBytes) {
        this.raiz = raiz.toAbsolutePath().normalize();
        this.maxBytes = maxBytes;
    }

    public static void validarQuantidade(List<ArquivoEnviado> arquivos) {
        if (arquivos != null && arquivos.size() > MAX_ARQUIVOS) {
            throw new RegraChamadoException("Envie no máximo " + MAX_ARQUIVOS + " arquivos por vez.");
        }
    }

    public AnexoGravado gravar(String protocolo, ArquivoEnviado arquivo) throws IOException {
        if (!GeradorProtocolo.valido(protocolo)) {
            throw new IllegalArgumentException("Protocolo inválido: " + protocolo);
        }
        String nome = nomeLimpo(arquivo.nome);
        String ext = validar(nome, arquivo.conteudo);
        String relativo = protocolo.substring(3, 7) + "/" + protocolo + "/" + UUID.randomUUID() + "." + ext;
        Path destino = resolver(relativo);
        Files.createDirectories(destino.getParent());
        Files.write(destino, arquivo.conteudo, StandardOpenOption.CREATE_NEW);
        return new AnexoGravado(nome, relativo, MIME.get(ext), arquivo.conteudo.length,
                HashUtil.sha256Hex(arquivo.conteudo));
    }

    public Path resolver(String caminhoRelativo) {
        Path p = raiz.resolve(caminhoRelativo).normalize();
        if (!p.startsWith(raiz)) {
            throw new IllegalArgumentException("Caminho fora do diretório de anexos: " + caminhoRelativo);
        }
        return p;
    }

    public void removerSilenciosamente(String caminhoRelativo) {
        try {
            Files.deleteIfExists(resolver(caminhoRelativo));
        } catch (Exception ignorada) {
            // limpeza de melhor esforco apos rollback
        }
    }

    private String validar(String nome, byte[] conteudo) {
        if (conteudo == null || conteudo.length == 0) {
            throw new RegraChamadoException("Arquivo vazio: " + nome);
        }
        if (conteudo.length > maxBytes) {
            throw new RegraChamadoException("Arquivo acima do limite de " + Math.max(1, maxBytes / (1024 * 1024))
                    + " MB: " + nome);
        }
        String ext = extensao(nome);
        if (!MIME.containsKey(ext)) {
            throw new RegraChamadoException("Tipo de arquivo não permitido (use PDF, PNG ou JPG): " + nome);
        }
        if (!assinaturaConfere(ext, conteudo)) {
            throw new RegraChamadoException("O conteúdo não corresponde à extensão do arquivo: " + nome);
        }
        return ext;
    }

    static String nomeLimpo(String nome) {
        String n = nome == null || nome.trim().isEmpty() ? "arquivo" : nome.replace('\\', '/');
        n = n.substring(n.lastIndexOf('/') + 1);
        return n.length() > 255 ? n.substring(n.length() - 255) : n;
    }

    static String extensao(String nome) {
        int i = nome.lastIndexOf('.');
        return i < 0 ? "" : nome.substring(i + 1).toLowerCase(Locale.ROOT);
    }

    static boolean assinaturaConfere(String ext, byte[] c) {
        switch (ext) {
            case "pdf":
                return comecaCom(c, 0x25, 0x50, 0x44, 0x46);   // %PDF
            case "png":
                return comecaCom(c, 0x89, 0x50, 0x4E, 0x47);   // .PNG
            case "jpg":
            case "jpeg":
                return comecaCom(c, 0xFF, 0xD8, 0xFF);
            default:
                return false;
        }
    }

    private static boolean comecaCom(byte[] c, int... assinatura) {
        if (c.length < assinatura.length) return false;
        for (int i = 0; i < assinatura.length; i++) {
            if ((c[i] & 0xFF) != assinatura[i]) return false;
        }
        return true;
    }
}
