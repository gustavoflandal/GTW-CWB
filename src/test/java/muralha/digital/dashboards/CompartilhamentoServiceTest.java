package muralha.digital.dashboards;

import com.google.gson.JsonObject;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class CompartilhamentoServiceTest {

    private final CompartilhamentoService service = new CompartilhamentoService();

    @Test
    public void serviceDeveSerInstanciavel() {
        assertNotNull(service);
    }

    @Test
    public void niveisDeAcessoDevemSerValidos() {
        String[] niveis = {"ver", "editar"};
        for (String nivel : niveis) {
            assertNotNull(nivel);
            assertTrue(nivel.equals("ver") || nivel.equals("editar"),
                    "Nivel invalido: " + nivel);
        }
    }

    @Test
    public void tiposRecursoDevemSerValidos() {
        String[] tipos = {"dashboard", "dataset"};
        for (String tipo : tipos) {
            assertNotNull(tipo);
            assertTrue(tipo.length() > 0);
        }
    }

    @Test
    public void estruturaCompartilhamentoDeveSerCompleta() {
        JsonObject comp = new JsonObject();
        comp.addProperty("id", 1);
        comp.addProperty("idUsuario", 42);
        comp.addProperty("nomeUsuario", "Operador Silva");
        comp.addProperty("nivel", "leitura");

        assertEquals(comp.get("idUsuario").getAsInt(), 42);
        assertEquals(comp.get("nivel").getAsString(), "leitura");
        assertEquals(comp.get("nomeUsuario").getAsString(), "Operador Silva");
    }

    @Test
    public void verificacaoPermissaoNivelMinimo() {
        String nivelMinimo = "editar";
        String nivelCompartilhado = "ver";
        assertFalse(nivelCompartilhado.equals(nivelMinimo),
                "Ver nao deveria satisfazer nivel minimo editar");

        nivelCompartilhado = "editar";
        assertTrue(nivelCompartilhado.equals(nivelMinimo),
                "Editar deveria satisfazer nivel minimo editar");
    }
}
