package muralha.digital.sensores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class IncidenteImportJobTest {

    private static final long AGORA = 1_760_000_000_000L;

    // Recorte no formato do feed da Waze (types=alerts,jams)
    private static final String FEED = "{"
        + "\"alerts\":[{\"uuid\":\"a-1\",\"type\":\"ACCIDENT\",\"subtype\":\"ACCIDENT_MAJOR\","
        + "  \"location\":{\"x\":-38.45,\"y\":-12.97},\"reportRating\":3,\"pubMillis\":1759999000000}],"
        + "\"jams\":[{\"uuid\":987654,\"street\":\"Av. Paralela\",\"city\":\"Salvador\",\"level\":4,"
        + "  \"speedKMH\":11.6,\"delay\":240,\"length\":1500,\"pubMillis\":1759999500000,"
        + "  \"line\":[{\"x\":-38.40,\"y\":-12.90},{\"x\":-38.41,\"y\":-12.91},{\"x\":-38.42,\"y\":-12.92}]},"
        + " {\"id\":\"j-2\",\"level\":2}]"
        + "}";

    private List<IncidenteImportJob.Incidente> extrair(String json) {
        return IncidenteImportJob.extrair(new Gson().fromJson(json, JsonObject.class), AGORA);
    }

    @Test
    public void processaAlertsEJams() {
        List<IncidenteImportJob.Incidente> lista = extrair(FEED);
        assertEquals(lista.size(), 3);
        assertEquals(lista.get(0).tipo, "ACCIDENT");
        assertEquals(lista.get(1).tipo, "JAM");
        assertEquals(lista.get(2).tipo, "JAM");
    }

    @Test
    public void jamUsaPontoCentralNivelEPubMillis() {
        IncidenteImportJob.Incidente jam = extrair(FEED).get(1);
        assertEquals(jam.idExterno, "987654");
        assertEquals(jam.latitude, -12.91, 1e-9);
        assertEquals(jam.longitude, -38.41, 1e-9);
        assertEquals(jam.severidade, 4);
        assertEquals(jam.epochMs, 1759999500000L);
        assertEquals(jam.descricao, "Av. Paralela, Salvador — nível 4, 12 km/h, atraso 240 s, 1500 m");
    }

    @Test
    public void jamSemUuidNemLinhaUsaIdEAgora() {
        IncidenteImportJob.Incidente jam = extrair(FEED).get(2);
        assertEquals(jam.idExterno, "j-2");
        assertEquals(jam.latitude, 0.0);
        assertEquals(jam.epochMs, AGORA);
        assertEquals(jam.descricao, "nível 2");
    }

    @Test
    public void alertaUsaPubMillisParaNaoDuplicar() {
        IncidenteImportJob.Incidente alerta = extrair(FEED).get(0);
        assertEquals(alerta.epochMs, 1759999000000L);
        assertEquals(alerta.latitude, -12.97, 1e-9);
        assertEquals(alerta.descricao, "ACCIDENT_MAJOR");
    }

    @Test
    public void feedSemArraysOuNulo() {
        assertTrue(extrair("{}").isEmpty());
        assertTrue(IncidenteImportJob.extrair(null, AGORA).isEmpty());
    }
}
