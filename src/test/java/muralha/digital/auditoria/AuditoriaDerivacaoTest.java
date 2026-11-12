package muralha.digital.auditoria;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class AuditoriaDerivacaoTest {

    @Test public void pdfViraImpressao() {
        assertEquals(AuditoriaService.derivarNivel("GET", null, 200, "application/pdf"), "IMPRESSAO");
        assertEquals(AuditoriaService.derivarNivel("GET", "relatorio", 200, "application/pdf;charset=UTF-8"), "IMPRESSAO");
    }

    @Test public void planilhaContinuaExportacao() {
        assertEquals(AuditoriaService.derivarNivel("GET", "exportar", 200, "text/csv"), "EXPORTACAO");
        assertEquals(AuditoriaService.derivarNivel("GET", "xls", 200,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), "EXPORTACAO");
    }

    @Test public void contentTypeNuloNaoQuebra() {
        assertEquals(AuditoriaService.derivarNivel("GET", "consultar", 200, null), "VISUALIZACAO");
    }

    @Test public void formatoDerivado() {
        assertEquals(AuditoriaService.derivarFormato("application/pdf; name=auto.pdf"), "PDF");
        assertNull(AuditoriaService.derivarFormato("text/csv"));
        assertNull(AuditoriaService.derivarFormato(null));
    }
}
