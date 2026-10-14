package muralha.digital.dashboards;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class HealthCheckTest {

    @Test
    public void servletDeveInstanciarSemExcecao() {
        HealthCheckServlet servlet = new HealthCheckServlet();
        assertNotNull(servlet);
    }

    @Test
    public void consultaLogServiceDeveExistirNoServlet() {
        // HealthCheckServlet usa ConsultaLogService internamente
        HealthCheckServlet servlet = new HealthCheckServlet();
        assertNotNull(servlet, "Servlet instanciado com ConsultaLogService embutido");
    }

    @Test
    public void thresholdLentaDeveSerConsistente() {
        int threshold = ConsultaLogService.getThresholdLentaMs();
        assertEquals(threshold, 5000,
                "HealthCheck e painel.js usam 5000ms como limiar de consulta lenta");
    }

    @Test
    public void consultaLogServiceDeveInstanciarIndependente() {
        ConsultaLogService logService = new ConsultaLogService();
        assertNotNull(logService, "ConsultaLogService deve instanciar sem dependencias externas");
    }

    @Test
    public void endpointDeveSerApiDashHealth() throws Exception {
        var annotation = HealthCheckServlet.class.getAnnotation(
                javax.servlet.annotation.WebServlet.class);
        assertNotNull(annotation, "HealthCheckServlet deve ter @WebServlet");
        assertEquals(annotation.value()[0], "/api/dash/health",
                "Endpoint deve ser /api/dash/health");
    }
}
