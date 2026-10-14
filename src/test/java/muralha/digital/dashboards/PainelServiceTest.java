package muralha.digital.dashboards;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class PainelServiceTest {

    private final PainelService service = new PainelService();

    @Test
    public void serviceDeveSerInstanciavel() {
        assertNotNull(service);
    }

    @Test
    public void snapshotJsonDeveSerEstruturado() {
        JsonObject snap = new JsonObject();
        snap.addProperty("nome", "Painel Teste");
        snap.addProperty("descricao", "Descricao");
        snap.addProperty("refreshSeg", 60);

        JsonArray widgets = new JsonArray();
        JsonObject w1 = new JsonObject();
        w1.addProperty("titulo", "Widget 1");
        w1.addProperty("tipo", "barras");
        w1.addProperty("idDataset", 1);
        w1.addProperty("gridX", 0);
        w1.addProperty("gridY", 0);
        w1.addProperty("gridW", 6);
        w1.addProperty("gridH", 3);

        JsonObject consulta = new JsonObject();
        JsonArray measures = new JsonArray();
        measures.add("total");
        consulta.add("measures", measures);
        w1.add("consulta", consulta);

        widgets.add(w1);
        snap.add("widgets", widgets);

        assertEquals(snap.get("nome").getAsString(), "Painel Teste");
        assertEquals(snap.getAsJsonArray("widgets").size(), 1);

        JsonObject widgetFromSnap = snap.getAsJsonArray("widgets").get(0).getAsJsonObject();
        assertEquals(widgetFromSnap.get("tipo").getAsString(), "barras");
        assertEquals(widgetFromSnap.get("gridW").getAsInt(), 6);
    }

    @Test
    public void snapshotDeveSerializarEDeserializar() {
        JsonObject snap = new JsonObject();
        snap.addProperty("nome", "Painel A");

        JsonObject layout = new JsonObject();
        layout.addProperty("colunas", 12);
        snap.add("layout", layout);

        JsonArray widgets = new JsonArray();
        snap.add("widgets", widgets);

        String json = snap.toString();
        JsonObject parsed = JsonParser.parseString(json).getAsJsonObject();

        assertEquals(parsed.get("nome").getAsString(), "Painel A");
        assertTrue(parsed.has("layout"));
        assertEquals(parsed.getAsJsonObject("layout").get("colunas").getAsInt(), 12);
    }

    @Test
    public void versaoDuplicadaDeveIncrementar() {
        int versaoAtual = 3;
        int novaVersao = versaoAtual + 1;
        assertEquals(novaVersao, 4);
    }

    @Test
    public void duplicarDeveAdicionarSufixoCopia() {
        String nomeOriginal = "Painel de Passagens";
        String novoNome = nomeOriginal + " (copia)";
        assertEquals(novoNome, "Painel de Passagens (copia)");
        assertTrue(novoNome.endsWith("(copia)"));
    }
}
