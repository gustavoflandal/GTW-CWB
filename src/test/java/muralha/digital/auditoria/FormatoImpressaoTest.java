package muralha.digital.auditoria;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class FormatoImpressaoTest {

    @Test public void formatoPadraoQuandoVazio() {
        assertEquals(AuditoriaService.formatoOuPadrao(null), "Impressao de tela");
        assertEquals(AuditoriaService.formatoOuPadrao("  "), "Impressao de tela");
        assertEquals(AuditoriaService.formatoOuPadrao("PDF"), "PDF");
    }
}
