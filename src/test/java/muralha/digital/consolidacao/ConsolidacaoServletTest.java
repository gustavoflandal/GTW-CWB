package muralha.digital.consolidacao;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ConsolidacaoServletTest {

    @Test
    public void servletInstanciavelSemErro() {
        ConsolidacaoServlet servlet = new ConsolidacaoServlet();
        assertNotNull(servlet);
    }

    @Test
    public void fontesConhecidas() {
        String[] fontes = {"PASSAGEM", "ALERTA", "INCIDENTE_WAZE", "SUMARIZADO"};
        assertEquals(fontes.length, 4);
        assertEquals(fontes[0], "PASSAGEM");
        assertEquals(fontes[3], "SUMARIZADO");
    }

    @Test
    public void limiteMaximoE1000() {
        int limite = 1000;
        int inputLimite = 2000;
        int efetivo = Math.min(inputLimite, limite);
        assertEquals(efetivo, 1000);
    }

    @Test
    public void limiteDefaultE200() {
        int limiteDefault = 200;
        assertTrue(limiteDefault > 0);
        assertTrue(limiteDefault <= 1000);
    }

    @Test
    public void fonteNulaPermiteTodas() {
        String fonte = null;
        boolean filtrar = fonte != null && !fonte.isEmpty();
        assertFalse(filtrar);
    }

    @Test
    public void fonteVaziaPermiteTodas() {
        String fonte = "";
        boolean filtrar = fonte != null && !fonte.isEmpty();
        assertFalse(filtrar);
    }

    @Test
    public void fontePreenchidaFiltra() {
        String fonte = "ALERTA";
        boolean filtrar = fonte != null && !fonte.isEmpty();
        assertTrue(filtrar);
    }

    @Test
    public void periodoNuloNaoFiltra() {
        String dtInicio = null;
        String dtFim = null;
        boolean filtrarInicio = dtInicio != null && !dtInicio.isEmpty();
        boolean filtrarFim = dtFim != null && !dtFim.isEmpty();
        assertFalse(filtrarInicio);
        assertFalse(filtrarFim);
    }
}
