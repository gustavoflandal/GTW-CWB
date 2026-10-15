package muralha.digital.chamado;

import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.Base64;

import static org.testng.Assert.*;

public class CifraSegredoTest {

    private static byte[] chave() {
        byte[] k = new byte[32];
        new SecureRandom().nextBytes(k);
        return k;
    }

    @Test
    public void idaEVolta() {
        byte[] k = chave();
        String c = CifraSegredo.cifrar("s3nh@ Fictícia", k);
        assertTrue(c.startsWith("v1:"));
        assertFalse(c.contains("s3nh@"));
        assertEquals(CifraSegredo.decifrar(c, k), "s3nh@ Fictícia");
    }

    @Test
    public void ivDiferenteACadaCifragem() {
        byte[] k = chave();
        String a = CifraSegredo.cifrar("mesma", k);
        String b = CifraSegredo.cifrar("mesma", k);
        assertNotEquals(a, b);
        byte[] ra = Base64.getDecoder().decode(a.substring(3));
        byte[] rb = Base64.getDecoder().decode(b.substring(3));
        boolean ivIgual = true;
        for (int i = 0; i < 12; i++) ivIgual &= ra[i] == rb[i];
        assertFalse(ivIgual, "o IV deve ser aleatorio por cifragem");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void adulteracaoRejeitada() {
        byte[] k = chave();
        String c = CifraSegredo.cifrar("segredo", k);
        byte[] bruto = Base64.getDecoder().decode(c.substring(3));
        bruto[bruto.length - 1] ^= 0x01;
        CifraSegredo.decifrar("v1:" + Base64.getEncoder().encodeToString(bruto), k);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void chaveErradaRejeitada() {
        String c = CifraSegredo.cifrar("segredo", chave());
        CifraSegredo.decifrar(c, chave());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void prefixoDesconhecidoRejeitado() {
        byte[] k = chave();
        String c = CifraSegredo.cifrar("segredo", k);
        CifraSegredo.decifrar("v2:" + c.substring(3), k);
    }

    @Test
    public void senhaDe120CaracteresCabeEm255() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CifraSegredo.MAX_CARACTERES; i++) sb.append((char) ('a' + i % 26));
        assertEquals(CifraSegredo.MAX_CARACTERES, 120);
        String c = CifraSegredo.cifrar(sb.toString(), chave());
        assertTrue(c.length() <= 255, "tamanho " + c.length());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void senhaAcimaDoLimiteRejeitada() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= CifraSegredo.MAX_CARACTERES; i++) sb.append('x');
        CifraSegredo.cifrar(sb.toString(), chave());
    }

    @Test
    public void chaveDoTextoBase64() {
        byte[] k = chave();
        assertEquals(CifraSegredo.chaveDe(Base64.getEncoder().encodeToString(k)), k);
        assertNull(CifraSegredo.chaveDe(null));
        assertNull(CifraSegredo.chaveDe(""));
        assertNull(CifraSegredo.chaveDe("nao-e-base64!!"));
        assertNull(CifraSegredo.chaveDe(Base64.getEncoder().encodeToString(new byte[16])));
    }
}
