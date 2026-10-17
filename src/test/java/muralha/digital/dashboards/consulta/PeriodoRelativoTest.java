package muralha.digital.dashboards.consulta;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import org.testng.annotations.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static org.testng.Assert.*;

public class PeriodoRelativoTest {

    private static PeriodoRelativo em(int ano, int mes, int dia) {
        ZonedDateTime z = ZonedDateTime.of(ano, mes, dia, 15, 30, 0, 0, PeriodoRelativo.FUSO);
        return new PeriodoRelativo(Clock.fixed(z.toInstant(), PeriodoRelativo.FUSO));
    }

    private static void verificar(PeriodoRelativo.Intervalo iv, String inicio, String fim) {
        assertEquals(iv.inicio, LocalDateTime.parse(inicio));
        assertEquals(iv.fim, LocalDateTime.parse(fim));
        assertFalse(iv.fimInclusivo);
    }

    private static PeriodoRelativo.Intervalo resolver(PeriodoRelativo p, String v) {
        return p.resolver(new JsonPrimitive(v));
    }

    @Test
    public void periodosRelativosEmDiaComum() {
        PeriodoRelativo p = em(2026, 10, 14);
        verificar(resolver(p, "today"), "2026-10-14T00:00", "2026-10-15T00:00");
        verificar(resolver(p, "yesterday"), "2026-10-13T00:00", "2026-10-14T00:00");
        verificar(resolver(p, "last 7 days"), "2026-10-08T00:00", "2026-10-15T00:00");
        verificar(resolver(p, "last 30 days"), "2026-09-15T00:00", "2026-10-15T00:00");
        verificar(resolver(p, "this month"), "2026-10-01T00:00", "2026-10-15T00:00");
        verificar(resolver(p, "last month"), "2026-09-01T00:00", "2026-10-01T00:00");
        verificar(resolver(p, "last 3 months"), "2026-08-01T00:00", "2026-10-15T00:00");
        verificar(resolver(p, "last 6 months"), "2026-05-01T00:00", "2026-10-15T00:00");
        verificar(resolver(p, "last 12 months"), "2025-11-01T00:00", "2026-10-15T00:00");
        verificar(resolver(p, "this year"), "2026-01-01T00:00", "2026-10-15T00:00");
        verificar(resolver(p, "last year"), "2025-01-01T00:00", "2026-01-01T00:00");
    }

    @Test
    public void viradaDeAno() {
        PeriodoRelativo p = em(2027, 1, 1);
        verificar(resolver(p, "yesterday"), "2026-12-31T00:00", "2027-01-01T00:00");
        verificar(resolver(p, "last month"), "2026-12-01T00:00", "2027-01-01T00:00");
        verificar(resolver(p, "this year"), "2027-01-01T00:00", "2027-01-02T00:00");
        verificar(resolver(p, "last year"), "2026-01-01T00:00", "2027-01-01T00:00");
    }

    @Test
    public void anoBissexto() {
        PeriodoRelativo p = em(2028, 3, 1);
        verificar(resolver(p, "yesterday"), "2028-02-29T00:00", "2028-03-01T00:00");
        verificar(resolver(p, "last month"), "2028-02-01T00:00", "2028-03-01T00:00");
    }

    @Test
    public void maiusculasEEspacosSaoIgnorados() {
        verificar(resolver(em(2026, 10, 14), "  Today "), "2026-10-14T00:00", "2026-10-15T00:00");
    }

    @Test
    public void intervaloExplicitoComDatasIncluiDiaFinal() {
        JsonArray arr = new JsonArray();
        arr.add("2026-07-01");
        arr.add("2026-07-31");
        verificar(em(2026, 10, 14).resolver(arr), "2026-07-01T00:00", "2026-08-01T00:00");
    }

    @Test
    public void intervaloExplicitoComHoraTemFimInclusivo() {
        JsonArray arr = new JsonArray();
        arr.add("2026-07-01T00:00:00.000");
        arr.add("2026-09-30T23:59:59.999");
        PeriodoRelativo.Intervalo iv = em(2026, 10, 14).resolver(arr);
        assertEquals(iv.inicio, LocalDateTime.parse("2026-07-01T00:00"));
        assertEquals(iv.fim, LocalDateTime.parse("2026-09-30T23:59:59.999"));
        assertTrue(iv.fimInclusivo);
    }

    @Test
    public void nuloNaoGeraIntervalo() {
        assertNull(em(2026, 10, 14).resolver(null));
    }

    @Test(expectedExceptions = ErroConsulta.class)
    public void periodoForaDaListaERejeitado() {
        resolver(em(2026, 10, 14), "last 2 days");
    }

    @Test(expectedExceptions = ErroConsulta.class)
    public void intervaloInvertidoERejeitado() {
        JsonArray arr = new JsonArray();
        arr.add("2026-08-01");
        arr.add("2026-07-01");
        em(2026, 10, 14).resolver(arr);
    }

    @Test(expectedExceptions = ErroConsulta.class)
    public void dataInvalidaERejeitada() {
        JsonArray arr = new JsonArray();
        arr.add("2026-13-01");
        arr.add("2026-07-01");
        em(2026, 10, 14).resolver(arr);
    }
}
