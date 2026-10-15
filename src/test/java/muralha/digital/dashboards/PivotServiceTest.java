package muralha.digital.dashboards;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class PivotServiceTest {

    private final PivotService service = new PivotService();

    @Test
    public void montarConsultaPivotDeveIncluirTodasDimensoes() {
        JsonObject config = new JsonObject();
        JsonArray linhas = new JsonArray();
        linhas.add("equipamento");
        config.add("linhas", linhas);

        JsonArray colunas = new JsonArray();
        colunas.add("turno");
        config.add("colunas", colunas);

        JsonArray valores = new JsonArray();
        valores.add("total_passagens");
        config.add("valores", valores);

        JsonObject result = service.montarConsultaPivot(config);
        assertTrue(result.get("ok").getAsBoolean());

        JsonObject query = result.getAsJsonObject("query");
        JsonArray dims = query.getAsJsonArray("dimensions");
        assertEquals(dims.size(), 2);
        assertEquals(dims.get(0).getAsString(), "equipamento");
        assertEquals(dims.get(1).getAsString(), "turno");

        JsonArray meds = query.getAsJsonArray("measures");
        assertEquals(meds.size(), 1);
        assertEquals(meds.get(0).getAsString(), "total_passagens");
    }

    @Test
    public void pivotarDeveCriarCabecalhosDinamicos() {
        JsonArray dados = criarDadosPivot();

        JsonObject config = new JsonObject();
        JsonArray linhas = new JsonArray();
        linhas.add("equipamento");
        config.add("linhas", linhas);

        JsonArray colunas = new JsonArray();
        colunas.add("turno");
        config.add("colunas", colunas);

        JsonArray valores = new JsonArray();
        valores.add("total");
        config.add("valores", valores);
        config.addProperty("subtotais", true);

        JsonObject result = service.pivotar(dados, config);
        assertTrue(result.get("ok").getAsBoolean());

        JsonArray cabecalhos = result.getAsJsonArray("cabecalhos");
        assertNotNull(cabecalhos);
        assertTrue(cabecalhos.size() >= 3, "Cabecalhos: equipamento + turno cols + total");

        assertTrue(cabecalhos.toString().contains("equipamento"));
    }

    @Test
    public void pivotarComSubtotaisDeveIncluirLinhaTotal() {
        JsonArray dados = criarDadosPivot();

        JsonObject config = new JsonObject();
        JsonArray linhas = new JsonArray();
        linhas.add("equipamento");
        config.add("linhas", linhas);

        JsonArray colunas = new JsonArray();
        colunas.add("turno");
        config.add("colunas", colunas);

        JsonArray valores = new JsonArray();
        valores.add("total");
        config.add("valores", valores);
        config.addProperty("subtotais", true);

        JsonObject result = service.pivotar(dados, config);
        JsonArray linhasResult = result.getAsJsonArray("dados");

        boolean temSubtotal = false;
        for (var el : linhasResult) {
            JsonObject row = el.getAsJsonObject();
            if (row.has("_subtotal") && row.get("_subtotal").getAsBoolean()) {
                temSubtotal = true;
                break;
            }
        }
        assertTrue(temSubtotal, "Deve ter linha de subtotal/total");
    }

    @Test
    public void pivotarSemDadosDeveRetornarVazio() {
        JsonArray dados = new JsonArray();
        JsonObject config = new JsonObject();
        config.add("linhas", new JsonArray());
        config.add("colunas", new JsonArray());
        config.add("valores", new JsonArray());

        JsonObject result = service.pivotar(dados, config);
        assertTrue(result.get("ok").getAsBoolean());
        assertEquals(result.getAsJsonArray("dados").size(), 0);
    }

    @Test
    public void pivotarSemColunasDeveFuncionar() {
        JsonArray dados = new JsonArray();
        JsonObject r1 = new JsonObject();
        r1.addProperty("equip", "Radar A");
        r1.addProperty("total", 100);
        dados.add(r1);

        JsonObject config = new JsonObject();
        JsonArray linhas = new JsonArray();
        linhas.add("equip");
        config.add("linhas", linhas);
        config.add("colunas", new JsonArray());
        JsonArray valores = new JsonArray();
        valores.add("total");
        config.add("valores", valores);
        config.addProperty("subtotais", false);

        JsonObject result = service.pivotar(dados, config);
        assertTrue(result.get("ok").getAsBoolean());
        assertEquals(result.get("totalLinhas").getAsInt(), 1);
    }

    private JsonArray criarDadosPivot() {
        JsonArray arr = new JsonArray();
        String[] equips = {"Radar A", "Radar B"};
        String[] turnos = {"Manha", "Tarde"};
        int val = 100;
        for (String eq : equips) {
            for (String turno : turnos) {
                JsonObject row = new JsonObject();
                row.addProperty("equipamento", eq);
                row.addProperty("turno", turno);
                row.addProperty("total", val);
                arr.add(row);
                val += 50;
            }
        }
        return arr;
    }
}
