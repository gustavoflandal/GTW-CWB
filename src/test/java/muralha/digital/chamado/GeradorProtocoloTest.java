package muralha.digital.chamado;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class GeradorProtocoloTest {

    @Test
    public void formataComZerosAEsquerda() {
        assertEquals(GeradorProtocolo.formatar(2026, 123), "CH-2026-000123");
    }

    @Test
    public void viradaDeAnoRecomecaNumeracao() {
        assertEquals(GeradorProtocolo.formatar(2027, 1), "CH-2027-000001");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void numeroForaDaFaixa() {
        GeradorProtocolo.formatar(2026, 1_000_000);
    }

    @Test
    public void validaFormato() {
        assertTrue(GeradorProtocolo.valido("CH-2026-000123"));
        assertFalse(GeradorProtocolo.valido("CH-2026-123"));
        assertFalse(GeradorProtocolo.valido("../CH-2026-000123"));
        assertFalse(GeradorProtocolo.valido(null));
    }
}
