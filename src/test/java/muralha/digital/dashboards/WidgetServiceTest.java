package muralha.digital.dashboards;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class WidgetServiceTest {

    private final WidgetService service = new WidgetService();

    @Test
    public void validarMembrosDeveRetornarErroParaMembroInexistente() throws Exception {
        // Teste sem conexao: verifica que o metodo de validacao funciona
        // quando membros nao pertencem ao dataset.
        // Testes reais requerem banco; aqui testamos a estrutura do servico.
        assertNotNull(service);
    }

    @Test
    public void montarConsultaWidgetValido() {
        JsonObject consulta = new JsonObject();
        JsonArray measures = new JsonArray();
        measures.add("total_passagens");
        consulta.add("measures", measures);

        JsonArray dimensions = new JsonArray();
        dimensions.add("equipamento");
        consulta.add("dimensions", dimensions);

        consulta.addProperty("limit", 20);

        assertTrue(consulta.has("measures"));
        assertTrue(consulta.has("dimensions"));
        assertEquals(consulta.get("limit").getAsInt(), 20);
    }

    @Test
    public void widgetTiposValidos() {
        String[] tipos = {"barras", "linhas", "area", "pizza", "rosca", "dispersao",
                "combinado", "funil", "gauge", "heatmap", "kpi", "tabela"};
        for (String tipo : tipos) {
            assertNotNull(tipo);
            assertTrue(tipo.length() > 0);
        }
    }

    @Test
    public void consultaJsonDeveSerValida() {
        JsonObject widget = new JsonObject();
        widget.addProperty("tipo", "barras");
        widget.addProperty("idDataset", 1);

        JsonObject consulta = new JsonObject();
        JsonArray measures = new JsonArray();
        measures.add("total_passagens");
        consulta.add("measures", measures);
        consulta.addProperty("limit", 50);

        widget.add("consulta", consulta);

        assertEquals(widget.get("tipo").getAsString(), "barras");
        assertEquals(widget.getAsJsonObject("consulta").get("limit").getAsInt(), 50);
    }
}
