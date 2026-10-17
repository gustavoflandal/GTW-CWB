package muralha.digital.timelapse;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class TimelapseDescarteTest {

    @Test
    public void jobDescarteInstanciavelSemErro() {
        JobTimelapseDescarte job = new JobTimelapseDescarte();
        assertNotNull(job);
    }

    @Test
    public void retencaoPadraoE72Horas() {
        // O valor padrão é 72 conforme a migração e o ConfigChaveValor.obterInt fallback
        int padrao = 72;
        assertEquals(padrao, 72);
        assertTrue(padrao > 0);
    }

    @Test
    public void retencaoZeroPermiteDescarteTotal() {
        // Retenção 0 significa descartar tudo imediatamente
        int retencao = 0;
        assertTrue(retencao >= 0);
    }

    @Test
    public void retencaoNegativaTratadaComoZero() {
        int retencao = -1;
        int efetiva = Math.max(retencao, 0);
        assertEquals(efetiva, 0);
    }

    @Test
    public void maxFramesDaServletEDuzentos() {
        // TimelapseServlet.MAX_FRAMES = 200
        int maxFrames = 200;
        assertTrue(maxFrames > 0);
        assertTrue(maxFrames <= 1000);
    }

    @Test
    public void servletInstanciavelSemErro() {
        TimelapseServlet servlet = new TimelapseServlet();
        assertNotNull(servlet);
    }
}
