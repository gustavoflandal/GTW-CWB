package muralha.digital.mapas;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class MapaConfigServletTest {

    @Test
    public void aceitaHostComPorta() {
        assertEquals(MapaConfigServlet.validarHostPorta(" 10.0.0.5:8003 "), "10.0.0.5:8003");
        assertEquals(MapaConfigServlet.validarHostPorta("api.exemplo.local"), "api.exemplo.local");
    }

    @Test
    public void recusaValorForaDoFormato() {
        assertEquals(MapaConfigServlet.validarHostPorta(null), "");
        assertEquals(MapaConfigServlet.validarHostPorta(""), "");
        assertEquals(MapaConfigServlet.validarHostPorta("http://10.0.0.5:8003"), "");
        assertEquals(MapaConfigServlet.validarHostPorta("10.0.0.5:8003/obter"), "");
        assertEquals(MapaConfigServlet.validarHostPorta("evil.com\"><script>"), "");
    }
}
