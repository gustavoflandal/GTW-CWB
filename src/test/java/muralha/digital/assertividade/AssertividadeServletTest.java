package muralha.digital.assertividade;

import com.google.gson.JsonObject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AssertividadeServletTest {

    @Test
    public void taxaConcordancia_todosAprovados() {
        JsonObject t = new JsonObject();
        t.addProperty("PRE_APROVADA", 100);
        assertEquals(AssertividadeServlet.calcularTaxaConcordancia(t), 100.0);
    }

    @Test
    public void taxaConcordancia_metade() {
        JsonObject t = new JsonObject();
        t.addProperty("PRE_APROVADA", 50);
        t.addProperty("DESEMPATE", 30);
        t.addProperty("REPROVADA", 20);
        assertEquals(AssertividadeServlet.calcularTaxaConcordancia(t), 50.0);
    }

    @Test
    public void taxaConcordancia_nenhumDado() {
        JsonObject t = new JsonObject();
        assertEquals(AssertividadeServlet.calcularTaxaConcordancia(t), 0.0);
    }

    @Test
    public void taxaConcordancia_abaixo80() {
        JsonObject t = new JsonObject();
        t.addProperty("PRE_APROVADA", 70);
        t.addProperty("DESEMPATE", 20);
        t.addProperty("REPROVADA", 10);
        double taxa = AssertividadeServlet.calcularTaxaConcordancia(t);
        assertEquals(taxa, 70.0);
        assertTrue(taxa < 80.0);
    }

    @Test
    public void taxaConcordancia_exatamente80() {
        JsonObject t = new JsonObject();
        t.addProperty("PRE_APROVADA", 80);
        t.addProperty("DESEMPATE", 10);
        t.addProperty("REPROVADA", 10);
        double taxa = AssertividadeServlet.calcularTaxaConcordancia(t);
        assertEquals(taxa, 80.0);
    }

    @Test
    public void taxaConcordancia_acima80() {
        JsonObject t = new JsonObject();
        t.addProperty("PRE_APROVADA", 90);
        t.addProperty("DESEMPATE", 5);
        t.addProperty("REPROVADA", 5);
        double taxa = AssertividadeServlet.calcularTaxaConcordancia(t);
        assertEquals(taxa, 90.0);
        assertTrue(taxa >= 80.0);
    }

    @Test
    public void taxaConcordancia_somenteDesempateEReprovada() {
        JsonObject t = new JsonObject();
        t.addProperty("DESEMPATE", 60);
        t.addProperty("REPROVADA", 40);
        assertEquals(AssertividadeServlet.calcularTaxaConcordancia(t), 0.0);
    }

    @Test
    public void taxaConcordancia_somenteAprovada() {
        JsonObject t = new JsonObject();
        t.addProperty("PRE_APROVADA", 1);
        assertEquals(AssertividadeServlet.calcularTaxaConcordancia(t), 100.0);
    }
}
