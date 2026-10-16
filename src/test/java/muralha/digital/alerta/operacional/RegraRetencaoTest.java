package muralha.digital.alerta.operacional;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;

public class RegraRetencaoTest {

    private static RegraRetencao.DadoHora dado(int hora, double vel, int ocup, int traf) {
        return new RegraRetencao.DadoHora(10, hora, vel, ocup, traf);
    }

    @Test
    public void retencaoDetectadaComHorasConsecutivas() {
        List<RegraRetencao.DadoHora> dados = Arrays.asList(
            dado(10, 8.0, 600, 50),
            dado(11, 10.0, 700, 60),
            dado(12, 12.0, 550, 45)
        );
        RegraRetencao.Avaliacao a = RegraRetencao.avaliar(dados, 500, 15.0, 2);
        assertTrue(a.isRetencao());
        assertEquals(a.getHorasConsecutivas(), 3);
        assertTrue(a.getVelocidadeMedia() < 15.0);
        assertTrue(a.getDetalhe().contains("3 h consecutivas"));
    }

    @Test
    public void semRetencaoQuandoVelocidadeAcima() {
        List<RegraRetencao.DadoHora> dados = Arrays.asList(
            dado(10, 20.0, 600, 50),
            dado(11, 25.0, 700, 60)
        );
        RegraRetencao.Avaliacao a = RegraRetencao.avaliar(dados, 500, 15.0, 1);
        assertFalse(a.isRetencao());
    }

    @Test
    public void semRetencaoQuandoOcupacaoBaixa() {
        List<RegraRetencao.DadoHora> dados = Arrays.asList(
            dado(10, 5.0, 100, 2),
            dado(11, 8.0, 150, 3)
        );
        RegraRetencao.Avaliacao a = RegraRetencao.avaliar(dados, 500, 15.0, 1);
        assertFalse(a.isRetencao());
    }

    @Test
    public void horasNaoConsecutivasNaoContam() {
        List<RegraRetencao.DadoHora> dados = Arrays.asList(
            dado(10, 8.0, 600, 50),
            dado(11, 30.0, 600, 50),
            dado(12, 8.0, 600, 50)
        );
        RegraRetencao.Avaliacao a = RegraRetencao.avaliar(dados, 500, 15.0, 2);
        assertFalse(a.isRetencao());
    }

    @Test
    public void severidadeAltaParaLongaDuracao() {
        List<RegraRetencao.DadoHora> dados = Arrays.asList(
            dado(8, 5.0, 800, 100), dado(9, 6.0, 850, 110),
            dado(10, 4.0, 900, 120), dado(11, 7.0, 800, 90),
            dado(12, 3.0, 950, 130), dado(13, 5.0, 870, 100)
        );
        RegraRetencao.Avaliacao a = RegraRetencao.avaliar(dados, 500, 15.0, 2);
        assertTrue(a.isRetencao());
        assertEquals(a.getSeveridade(), RegraRetencao.Severidade.ALTA);
    }

    @Test
    public void semDadosNaoDetecta() {
        assertFalse(RegraRetencao.avaliar(Collections.emptyList(), 500, 15.0, 1).isRetencao());
        assertFalse(RegraRetencao.avaliar(null, 500, 15.0, 1).isRetencao());
    }

    @Test
    public void trafegoZeroNaoContaComoRetencao() {
        List<RegraRetencao.DadoHora> dados = Arrays.asList(
            dado(10, 0.0, 0, 0),
            dado(11, 0.0, 600, 0)
        );
        RegraRetencao.Avaliacao a = RegraRetencao.avaliar(dados, 500, 15.0, 1);
        assertFalse(a.isRetencao());
    }
}
