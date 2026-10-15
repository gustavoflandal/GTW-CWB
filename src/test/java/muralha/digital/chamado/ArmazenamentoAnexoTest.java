package muralha.digital.chamado;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.testng.Assert.*;

public class ArmazenamentoAnexoTest {

    private static final byte[] PDF = {0x25, 0x50, 0x44, 0x46, 0x2D, 0x31};
    private static final byte[] PNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A};
    private static final byte[] EXE = {0x4D, 0x5A, (byte) 0x90, 0x00};

    private Path raiz;
    private ArmazenamentoAnexo armazenamento;

    @BeforeMethod
    public void preparar() throws Exception {
        raiz = Files.createTempDirectory("anexos-chamado");
        armazenamento = new ArmazenamentoAnexo(raiz, 1024);
    }

    @Test
    public void gravaPdfDentroDaPastaDoProtocolo() throws Exception {
        AnexoGravado a = armazenamento.gravar("CH-2026-000001", new ArquivoEnviado("Relatório.PDF", PDF));
        assertTrue(a.caminhoRelativo.startsWith("2026/CH-2026-000001/"));
        assertTrue(a.caminhoRelativo.endsWith(".pdf"));
        assertEquals(a.tipoMime, "application/pdf");
        assertEquals(a.nomeOriginal, "Relatório.PDF");
        assertEquals(a.tamanho, PDF.length);
        assertEquals(a.sha256.length(), 64);
        assertTrue(Files.exists(raiz.resolve(a.caminhoRelativo)));
    }

    @Test
    public void nomeComCaminhoGuardaSoONomeDoArquivo() throws Exception {
        AnexoGravado a = armazenamento.gravar("CH-2026-000001", new ArquivoEnviado("..\\..\\windows\\foto.png", PNG));
        assertEquals(a.nomeOriginal, "foto.png");
        assertTrue(raiz.resolve(a.caminhoRelativo).normalize().startsWith(raiz));
    }

    @Test(expectedExceptions = RegraChamadoException.class, expectedExceptionsMessageRegExp = ".*não corresponde.*")
    public void executavelRenomeadoParaPdfERejeitado() throws Exception {
        armazenamento.gravar("CH-2026-000001", new ArquivoEnviado("nota.pdf", EXE));
    }

    @Test(expectedExceptions = RegraChamadoException.class, expectedExceptionsMessageRegExp = ".*não permitido.*")
    public void extensaoForaDaListaERejeitada() throws Exception {
        armazenamento.gravar("CH-2026-000001", new ArquivoEnviado("script.js", PDF));
    }

    @Test(expectedExceptions = RegraChamadoException.class, expectedExceptionsMessageRegExp = ".*limite.*")
    public void arquivoAcimaDoLimiteERejeitado() throws Exception {
        byte[] grande = new byte[2048];
        System.arraycopy(PDF, 0, grande, 0, PDF.length);
        armazenamento.gravar("CH-2026-000001", new ArquivoEnviado("grande.pdf", grande));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void protocoloInvalidoNaoViraCaminho() throws Exception {
        armazenamento.gravar("../../etc", new ArquivoEnviado("a.pdf", PDF));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void resolverNaoSaiDaRaiz() {
        armazenamento.resolver("../fora.pdf");
    }

    @Test(expectedExceptions = RegraChamadoException.class)
    public void maisDeCincoArquivosERejeitado() {
        ArmazenamentoAnexo.validarQuantidade(Collections.nCopies(6, new ArquivoEnviado("a.pdf", PDF)));
    }
}
