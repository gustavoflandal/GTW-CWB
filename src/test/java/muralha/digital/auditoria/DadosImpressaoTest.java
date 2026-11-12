package muralha.digital.auditoria;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class DadosImpressaoTest {

    @Test public void montaJsonComFormatoDocumentoOrigem() {
        String j = AuditoriaService.montarDadosImpressao("PDF", "RelatorioValidacao", "/muralha-digital/pages/relatorios/x.jsp");
        assertTrue(j.contains("\"formato\":\"PDF\""));
        assertTrue(j.contains("\"documento\":\"RelatorioValidacao\""));
        assertTrue(j.contains("\"telaOrigem\":\"/muralha-digital/pages/relatorios/x.jsp\""));
    }

    @Test public void omiteChavesNulas() {
        String j = AuditoriaService.montarDadosImpressao(null, "Doc", null);
        assertFalse(j.contains("formato"));
        assertFalse(j.contains("telaOrigem"));
        assertTrue(j.contains("\"documento\":\"Doc\""));
    }

    @Test public void telaOrigemDoRefererSemHostNemQuery() {
        assertEquals(AuditoriaService.derivarTelaOrigem(
            "http://localhost:8080/muralha-digital/pages/relatorios/x.jsp?a=1", "/rota"),
            "/muralha-digital/pages/relatorios/x.jsp");
    }

    @Test public void telaOrigemCaiNoFallbackSemReferer() {
        assertEquals(AuditoriaService.derivarTelaOrigem(null, "/MuralhaDigital/Relatorio"), "/MuralhaDigital/Relatorio");
        assertEquals(AuditoriaService.derivarTelaOrigem("  ", "/r"), "/r");
    }
}
