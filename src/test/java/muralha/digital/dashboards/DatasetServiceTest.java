package muralha.digital.dashboards;

import com.google.gson.JsonObject;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class DatasetServiceTest {

    private final DatasetService service = new DatasetService();

    @Test
    public void validarExpressaoAceita() {
        JsonObject r = service.validarExpressao("YEAR(data_hora)");
        assertTrue(r.get("ok").getAsBoolean());
        assertTrue(r.get("valido").getAsBoolean());
    }

    @Test
    public void validarExpressaoRejeitada() {
        JsonObject r = service.validarExpressao("STRING_AGG(nome, ',')");
        assertTrue(r.get("ok").getAsBoolean());
        assertFalse(r.get("valido").getAsBoolean());
        assertNotNull(r.get("erro").getAsString());
    }

    @Test
    public void validarExpressaoInjection() {
        JsonObject r = service.validarExpressao("; DROP TABLE dash.dataset");
        assertTrue(r.get("ok").getAsBoolean());
        assertFalse(r.get("valido").getAsBoolean());
    }
}
