package muralha.digital.logtecnico;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class HistoricoParametrosTest {

    @Test
    public void comparaMapasGravandoAnteriorENovo() {
        Map<String, String> antes = new HashMap<>();
        antes.put("nome", "Fluxo");
        antes.put("threshold_ok", "80.0");
        antes.put("removido", "x");
        Map<String, String> depois = new HashMap<>();
        depois.put("nome", "Fluxo");
        depois.put("threshold_ok", "85.0");
        depois.put("incluido", "y");
        List<AlteracaoParametro> r = HistoricoParametros.comparar(antes, depois);
        assertEquals(r.size(), 3);
        assertEquals(r.get(0).toString(), "incluido: null -> y");
        assertEquals(r.get(1).toString(), "removido: x -> null");
        assertEquals(r.get(2).getValorAnterior(), "80.0");
        assertEquals(r.get(2).getValorNovo(), "85.0");
    }

    @Test
    public void valorIgualNaoGeraAlteracao() {
        assertTrue(HistoricoParametros.comparar("valor", "5", "5").isEmpty());
        assertTrue(HistoricoParametros.comparar("valor", null, null).isEmpty());
        assertEquals(HistoricoParametros.comparar("valor", "5", "6").get(0).toString(), "valor: 5 -> 6");
        assertEquals(HistoricoParametros.comparar("valor", null, "6").size(), 1);
    }

    private static final String XML_ANTES =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><classes><class classname=\"TConfigEquip\" name=\"TConfigEquip\"><properties>" +
        "<property name=\"serie\" type=\"Integer\">1000001</property>" +
        "<property name=\"dataHoraConfiguracao\" type=\"Double\">45000.1</property>" +
        "<property name=\"local\" type=\"Object\" class=\"TLocal\"><class classname=\"TLocal\" name=\"local\"><properties>" +
        "<property name=\"velocidadeMaxima\" type=\"Integer\">60</property></properties></class></property>" +
        "<property name=\"pistas\" type=\"Object\" class=\"TCollection\"><class classname=\"TCollection\" name=\"pistas\">" +
        "<customdata count=\"1\"><class classname=\"TPista\" name=\"Item_0\"><properties>" +
        "<property name=\"limite\" type=\"Integer\">60</property></properties></class></customdata></class></property>" +
        "</properties></class></classes>";

    @Test
    public void planificaXmlDoSerializador() {
        Map<String, String> m = HistoricoParametros.planificarXmlSerializador(XML_ANTES);
        assertEquals(m.get("serie"), "1000001");
        assertEquals(m.get("local.velocidadeMaxima"), "60");
        assertEquals(m.get("pistas.Item_0.limite"), "60");
        assertEquals(m.size(), 4);
    }

    @Test
    public void comparaConfigEquipIgnorandoDataDaConfiguracao() {
        String depois = XML_ANTES.replace(">45000.1<", ">45001.9<")
            .replace("\"velocidadeMaxima\" type=\"Integer\">60<", "\"velocidadeMaxima\" type=\"Integer\">50<")
            .replace("\"limite\" type=\"Integer\">60<", "\"limite\" type=\"Integer\">40<");
        List<AlteracaoParametro> r = HistoricoParametros.compararConfigEquip(XML_ANTES, depois, Arrays.asList("dataHoraConfiguracao"));
        assertEquals(r.size(), 2);
        assertEquals(r.get(0).toString(), "local.velocidadeMaxima: 60 -> 50");
        assertEquals(r.get(1).toString(), "pistas.Item_0.limite: 60 -> 40");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void xmlComDoctypeEhRecusado() {
        HistoricoParametros.planificarXmlSerializador("<!DOCTYPE x [<!ENTITY e SYSTEM \"file:///c:/windows/win.ini\">]><classes>&e;</classes>");
    }

    @Test
    public void truncaValoresLongos() {
        char[] c = new char[1500];
        Arrays.fill(c, 'a');
        assertEquals(HistoricoParametros.truncar(new String(c), HistoricoParametros.MAX_VALOR).length(), 1000);
        assertNull(HistoricoParametros.truncar(null, 10));
    }
}
