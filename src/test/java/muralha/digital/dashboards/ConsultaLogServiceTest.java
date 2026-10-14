package muralha.digital.dashboards;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class ConsultaLogServiceTest {

    private final ConsultaLogService service = new ConsultaLogService();

    @Test
    public void thresholdLentaPadraoDeve5000() {
        assertEquals(ConsultaLogService.getThresholdLentaMs(), 5000,
                "Threshold padrao de consulta lenta deve ser 5000ms");
    }

    @Test
    public void instanciacaoNaoDeveLancarExcecao() {
        ConsultaLogService s = new ConsultaLogService();
        assertNotNull(s);
    }

    @Test
    public void registrarAsyncNaoDeveLancarExcecaoComNulls() {
        // registrarAsync e fire-and-forget; nao deve propagar excecao
        try {
            service.registrarAsync(0, 0, 0, 0, null, 0, 0, false, null);
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        } catch (Exception e) {
            fail("registrarAsync nao deve propagar excecao: " + e.getMessage());
        }
    }

    @Test
    public void registrarAsyncComErroNaoDevePropagar() {
        try {
            service.registrarAsync(1, 1, 1, 1,
                    "{\"measures\":[\"count\"]}", 6000, 500, false, "timeout");
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        } catch (Exception e) {
            fail("registrarAsync nao deve propagar excecao mesmo com erro: " + e.getMessage());
        }
    }

    @Test
    public void thresholdDeveSerPositivo() {
        assertTrue(ConsultaLogService.getThresholdLentaMs() > 0,
                "Threshold deve ser positivo");
    }
}
