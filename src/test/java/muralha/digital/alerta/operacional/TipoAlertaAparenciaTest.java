package muralha.digital.alerta.operacional;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class TipoAlertaAparenciaTest {

    private static Map<String, Object> config(String tipo, String cor, String icone) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", "11111111-1111-1111-1111-111111111111");
        m.put("tipo", tipo);
        m.put("descricao", "Descricao " + tipo);
        m.put("prioridade", 1);
        m.put("ativo", true);
        m.put("critico", true);
        m.put("cor", cor);
        m.put("icone", icone);
        m.put("tratamentos", 2);
        m.put("automaticos", 1);
        return m;
    }

    @Test
    public void devolveSoTipoCorEIconeNaMesmaOrdem() {
        List<Map<String, Object>> configs = new ArrayList<>(Arrays.asList(
            config("A", "vermelho", "bi-x"), config("B", null, null)));

        List<Map<String, Object>> r = TipoAlertaConfigServlet.aparencia(configs);

        assertEquals(r.size(), 2);
        assertEquals(r.get(0).keySet(), new java.util.HashSet<>(Arrays.asList("tipo", "cor", "icone")));
        assertEquals(r.get(0).get("tipo"), "A");
        assertEquals(r.get(0).get("cor"), "vermelho");
        assertEquals(r.get(0).get("icone"), "bi-x");
        assertEquals(r.get(1).get("tipo"), "B");
        assertNull(r.get(1).get("cor"));
        assertEquals(r.get(1).size(), 3);
    }

    @Test
    public void listaVaziaDevolveListaVazia() {
        assertTrue(TipoAlertaConfigServlet.aparencia(new ArrayList<>()).isEmpty());
    }
}
