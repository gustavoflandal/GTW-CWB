package muralha.digital.chamado;

import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.testng.Assert.*;

public class EmailChamadoTest {

    private static final LocalDateTime PA = LocalDateTime.of(2026, 10, 15, 9, 0);
    private static final LocalDateTime PR = LocalDateTime.of(2026, 10, 15, 12, 0);

    @Test
    public void confirmacaoTrazProtocoloPrioridadeEPrazos() {
        String html = EmailChamado.corpoHtml(EmailChamado.CONFIRMACAO_ABERTURA, "CH-2026-000001",
                "Câmera parada", "Crítica", PA, PR, null);
        assertTrue(html.contains("CH-2026-000001"));
        assertTrue(html.contains("Crítica"));
        assertTrue(html.contains("15/10/2026 09:00"));
        assertTrue(html.contains("15/10/2026 12:00"));
        assertEquals(EmailChamado.assunto(EmailChamado.CONFIRMACAO_ABERTURA, "CH-2026-000001"),
                "[Chamado CH-2026-000001] Abertura confirmada");
    }

    @Test
    public void reclassificacaoTrazJustificativa() {
        String html = EmailChamado.corpoHtml(EmailChamado.RECLASSIFICACAO, "CH-2026-000001",
                "Câmera parada", "Alta", PA, PR, "Afeta só uma faixa");
        assertTrue(html.contains("Afeta só uma faixa"));
        assertEquals(EmailChamado.assunto(EmailChamado.RECLASSIFICACAO, "CH-2026-000001"),
                "[Chamado CH-2026-000001] Prioridade reclassificada");
    }

    @Test
    public void tituloComHtmlEEscapado() {
        String html = EmailChamado.corpoHtml(EmailChamado.CONFIRMACAO_ABERTURA, "CH-2026-000001",
                "<script>alert(1)</script>", "Alta", PA, PR, null);
        assertFalse(html.contains("<script>"));
        assertTrue(html.contains("&lt;script&gt;"));
    }

    @Test
    public void statusAposTentativa() {
        assertEquals(EmailChamado.statusAposTentativa(true, 3), "ENVIADO");
        assertEquals(EmailChamado.statusAposTentativa(false, 0), "PENDENTE");
        assertEquals(EmailChamado.statusAposTentativa(false, 3), "PENDENTE");
        assertEquals(EmailChamado.statusAposTentativa(false, 4), "ERRO");
    }
}
