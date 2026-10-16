package muralha.digital.integridade;

import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

public class FiltroIntegridadeTest {

    @Test
    public void montaClausulaComPeriodoEquipamentosEStatus() {
        FiltroIntegridade f = FiltroIntegridade.de("2026-10-01", "2026-10-07", "10, 20,10,", "divergente");
        FiltroIntegridade.Clausula c = f.clausula("g.dt_captura", "g.id_local", "g.status_integridade");
        assertEquals(c.sql, "g.dt_captura >= ? AND g.dt_captura < ? AND g.id_local IN (?,?) AND g.status_integridade = ?");
        assertEquals(c.parametros, Arrays.<Object>asList(
            java.sql.Date.valueOf("2026-10-01"), java.sql.Date.valueOf("2026-10-08"), 10, 20, "DIVERGENTE"));
    }

    @Test
    public void semEquipamentosNemStatusSoFiltraPeriodo() {
        FiltroIntegridade.Clausula c = FiltroIntegridade.de("2026-10-01", "2026-10-01", "", null)
            .clausula("r.dia", "r.id_local", null);
        assertEquals(c.sql, "r.dia >= ? AND r.dia < ?");
        assertEquals(c.parametros.size(), 2);
    }

    @Test
    public void statusIgnoradoQuandoColunaNaoInformada() {
        FiltroIntegridade.Clausula c = FiltroIntegridade.de("2026-10-01", "2026-10-02", null, "INTEGRO")
            .clausula("r.dia", "r.id_local", null);
        assertFalse(c.sql.contains("status"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exigeDatas() {
        FiltroIntegridade.de(null, "2026-10-01", null, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void recusaPeriodoInvertido() {
        FiltroIntegridade.de("2026-10-05", "2026-10-01", null, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void recusaPeriodoAcimaDoMaximo() {
        FiltroIntegridade.de("2026-01-01", "2026-10-01", null, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void recusaEquipamentoNaoNumerico() {
        FiltroIntegridade.de("2026-10-01", "2026-10-02", "10,1 OR 1=1", null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void recusaStatusDesconhecido() {
        FiltroIntegridade.de("2026-10-01", "2026-10-02", null, "OK");
    }
}
