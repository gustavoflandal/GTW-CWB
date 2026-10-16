package muralha.digital.integridade;

import muralha.digital.util.HashUtil;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;

import static org.testng.Assert.*;

public class ConferenciaIntegridadeTest {

    private static final byte[] IMAGEM = "bytes de uma imagem jpeg".getBytes(StandardCharsets.UTF_8);
    private static final OrigemHash PAYLOAD = OrigemHash.de(OrigemHash.Tipo.PAYLOAD_SOAP, null);

    @Test
    public void sha256ConhecidoDeTextoVazio() {
        assertEquals(HashUtil.sha256Hex(new byte[0]),
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
    }

    @Test
    public void hashIgualResultaIntegro() {
        String hash = HashUtil.sha256Hex(IMAGEM);
        ResultadoConferencia r = ConferenciaIntegridade.conferir(IMAGEM, PAYLOAD, hash);
        assertEquals(r.getStatus(), StatusIntegridade.INTEGRO);
        assertNull(r.getMotivo());
        assertEquals(r.getSha256Calculado(), hash);
    }

    @Test
    public void hashIgualEmMaiusculasEComEspacosResultaIntegro() {
        String hash = "  " + HashUtil.sha256Hex(IMAGEM).toUpperCase() + " ";
        assertEquals(ConferenciaIntegridade.conferir(IMAGEM, PAYLOAD, hash).getStatus(), StatusIntegridade.INTEGRO);
    }

    @Test
    public void hashDiferenteResultaDivergenteComMotivo() {
        String outro = HashUtil.sha256Hex("outra imagem".getBytes(StandardCharsets.UTF_8));
        ResultadoConferencia r = ConferenciaIntegridade.conferir(IMAGEM, PAYLOAD, outro);
        assertEquals(r.getStatus(), StatusIntegridade.DIVERGENTE);
        assertTrue(r.getMotivo().contains("difere"));
        assertEquals(r.getHashOrigem(), outro);
        assertEquals(r.getSha256Calculado(), HashUtil.sha256Hex(IMAGEM));
    }

    @Test
    public void origemAusenteResultaSemConferencia() {
        ResultadoConferencia r = ConferenciaIntegridade.conferir(IMAGEM, OrigemHash.AUSENTE, null);
        assertEquals(r.getStatus(), StatusIntegridade.SEM_CONFERENCIA);
        assertNotNull(r.getSha256Calculado());
        assertNotNull(r.getMotivo());
    }

    @Test
    public void origemConfiguradaSemHashRecebidoResultaSemConferencia() {
        ResultadoConferencia r = ConferenciaIntegridade.conferir(IMAGEM, PAYLOAD, null);
        assertEquals(r.getStatus(), StatusIntegridade.SEM_CONFERENCIA);
        assertTrue(r.getMotivo().contains("não recebido"));
    }

    @Test
    public void imagemVaziaResultaErroLeitura() {
        assertEquals(ConferenciaIntegridade.conferir(new byte[0], PAYLOAD, "abc").getStatus(), StatusIntegridade.ERRO_LEITURA);
        ResultadoConferencia r = ConferenciaIntegridade.conferir(null, OrigemHash.AUSENTE, null);
        assertEquals(r.getStatus(), StatusIntegridade.ERRO_LEITURA);
        assertNull(r.getSha256Calculado());
    }

    @Test
    public void hashMalformadoResultaDivergente() {
        assertEquals(ConferenciaIntegridade.conferir(IMAGEM, PAYLOAD, "1234").getStatus(), StatusIntegridade.DIVERGENTE);
        String naoHex = HashUtil.sha256Hex(IMAGEM).substring(0, 63) + "z";
        assertEquals(ConferenciaIntegridade.conferir(IMAGEM, PAYLOAD, naoHex).getStatus(), StatusIntegridade.DIVERGENTE);
    }

    @Test
    public void aceitaSha512DaOrigem() {
        String sha512 = HashUtil.hex(IMAGEM, "SHA-512");
        assertEquals(sha512.length(), 128);
        ResultadoConferencia r = ConferenciaIntegridade.conferir(IMAGEM, PAYLOAD, sha512);
        assertEquals(r.getStatus(), StatusIntegridade.INTEGRO);
        assertEquals(r.getSha256Calculado().length(), 64);
    }

    @Test
    public void extraiHashDoDadoBrutoPorCaminhoEIndice() {
        String json = "{\"integridade\":{\"sha256\":[\"aaa\",\"bbb\"]},\"hash\":\"ccc\"}";
        assertEquals(ConferenciaIntegridade.extrairDoDadoBruto(json, "integridade.sha256", 0), "aaa");
        assertEquals(ConferenciaIntegridade.extrairDoDadoBruto(json, "integridade.sha256", 1), "bbb");
        assertNull(ConferenciaIntegridade.extrairDoDadoBruto(json, "integridade.sha256", 2));
        assertEquals(ConferenciaIntegridade.extrairDoDadoBruto(json, "hash", 0), "ccc");
        assertNull(ConferenciaIntegridade.extrairDoDadoBruto(json, "hash", 1), "texto único só vale para a imagem 0");
        assertNull(ConferenciaIntegridade.extrairDoDadoBruto(json, "inexistente.campo", 0));
        assertNull(ConferenciaIntegridade.extrairDoDadoBruto("não é json {", "hash", 0));
        assertNull(ConferenciaIntegridade.extrairDoDadoBruto(null, "hash", 0));
    }
}
