package muralha.digital.relatorio;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;

public class RelatorioPreProcMensalJobTest {

    @Test
    public void quintoDiaUtilDeOutubro2026() {
        // Out 2026: 1(Q) 2(S) 3(Sab) 4(Dom) 5(Seg) 6(T) 7(Qua)
        // Dias úteis: 1, 2, 5, 6, 7 → 5o dia útil = 7
        Assert.assertTrue(RelatorioPreProcMensalJob.ehDiaUtilAlvo(LocalDate.of(2026, 10, 7), 5));
    }

    @Test
    public void naoEhQuintoDiaUtilSeFoiAntes() {
        Assert.assertFalse(RelatorioPreProcMensalJob.ehDiaUtilAlvo(LocalDate.of(2026, 10, 6), 5));
    }

    @Test
    public void naoEhQuintoDiaUtilSeForDepois() {
        Assert.assertFalse(RelatorioPreProcMensalJob.ehDiaUtilAlvo(LocalDate.of(2026, 10, 8), 5));
    }

    @Test
    public void primeiroDiaUtilDeMes() {
        // Out 2026: 1 é Quinta (dia útil)
        Assert.assertTrue(RelatorioPreProcMensalJob.ehDiaUtilAlvo(LocalDate.of(2026, 10, 1), 1));
    }

    @Test
    public void primeiroDiaUtilQuandoMesComecaNoSabado() {
        // Ago 2026: 1 é Sábado → 1o dia útil = 3 (Segunda)
        Assert.assertTrue(RelatorioPreProcMensalJob.ehDiaUtilAlvo(LocalDate.of(2026, 8, 3), 1));
        Assert.assertFalse(RelatorioPreProcMensalJob.ehDiaUtilAlvo(LocalDate.of(2026, 8, 1), 1));
    }

    @Test
    public void quintoDiaUtilDeNovembro2026() {
        // Nov 2026: 1(Dom) 2(Seg) 3(T) 4(Qua) 5(Qui) 6(Sex)
        // Dias úteis: 2, 3, 4, 5, 6 → 5o dia útil = 6
        Assert.assertTrue(RelatorioPreProcMensalJob.ehDiaUtilAlvo(LocalDate.of(2026, 11, 6), 5));
    }

    @Test
    public void sabadoNuncaEhDiaUtil() {
        Assert.assertFalse(RelatorioPreProcMensalJob.ehDiaUtilAlvo(LocalDate.of(2026, 10, 3), 1));
    }

    @Test
    public void domingoNuncaEhDiaUtil() {
        Assert.assertFalse(RelatorioPreProcMensalJob.ehDiaUtilAlvo(LocalDate.of(2026, 10, 4), 1));
    }
}
