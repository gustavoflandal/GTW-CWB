package muralha.digital.acessos;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.testng.Assert.*;

public class ReferenciaEditalTest {

    // Item específico do TR: 5.1.n (a seção 5.1 não tem subseções), 5.x.y.z com alínea opcional,
    // ou 7.2.3.a-f / 7.2.4 (aba "Sist. Chamado"). Recusa número de título de seção (5.2.7, 5.4.5, 7.2.3),
    // faixas (5.4.7.1-7) e anotações ("parcial").
    private static final Pattern ITEM_ESPECIFICO =
            Pattern.compile("^(5\\.1\\.\\d+|5\\.[2-8]\\.\\d+\\.\\d+(\\.[a-z])?|7\\.2\\.3\\.[a-f]|7\\.2\\.4)$");

    private static final String[] HREFS_DINAMICOS = {
            "/MuralhaDigital/TipoAlertaConfig",
            "/MuralhaDigital/RelatorioGerencial",
            "/MuralhaDigital/PreProcessamento",
            "/muralha-digital/pages/monitoramento/integridade/index.jsp",
            "/muralha-digital/pages/monitoramento/logs-tecnicos/index.jsp",
            "/muralha-digital/pages/processamento/revisao-placas/index.jsp"
    };

    private static List<List<String>> todasAsReferencias() {
        List<List<String>> r = new ArrayList<>();
        for (int idMenu = 1; idMenu <= 1000; idMenu++) {
            if (!ReferenciaEdital.itens(idMenu).isEmpty()) r.add(ReferenciaEdital.itens(idMenu));
        }
        for (String href : HREFS_DINAMICOS) r.add(ReferenciaEdital.itensPorHref(href));
        return r;
    }

    @Test
    public void todasAsReferenciasSaoItensEspecificos() {
        for (List<String> itens : todasAsReferencias()) {
            assertFalse(itens.isEmpty());
            for (String item : itens) {
                assertTrue(ITEM_ESPECIFICO.matcher(item).matches(), "referência não específica: " + item);
            }
        }
    }

    @Test
    public void funcionalidadesComCardTemReferencia() {
        for (int idMenu : new int[] {366, 367, 368, 369, 370, 371, 373, 374, 375, 376, 377, 379, 395}) {
            assertFalse(ReferenciaEdital.itens(idMenu).isEmpty(), "id_menu " + idMenu + " sem referencia ao edital");
        }
        for (String href : HREFS_DINAMICOS) {
            assertFalse(ReferenciaEdital.itensPorHref(href).isEmpty(), href + " sem referencia ao edital");
        }
    }

    @Test
    public void referenciasConferidasComOTexto() {
        assertEquals(ReferenciaEdital.itens(368), List.of("5.2.2.6"));
        assertEquals(ReferenciaEdital.itens(369), List.of("5.5.2.1"));
        assertEquals(ReferenciaEdital.itens(371), List.of("5.4.6.1", "5.4.6.3"));
        assertTrue(ReferenciaEdital.itens(379).contains("7.2.3.d"));
        assertEquals(ReferenciaEdital.itensPorHref("/muralha-digital/pages/monitoramento/integridade/index.jsp"),
                List.of("5.2.1.2", "5.2.1.3"));
    }

    @Test
    public void categoriaEMenuSemCardNaoTemAsterisco() {
        assertTrue(ReferenciaEdital.itens(383).isEmpty(), "categoria Sistema de Chamados");
        // Telas sem card: abertas por botão de outra tela (o asterisco fica no card dessa tela)
        for (int idMenu : new int[] {372, 378, 380, 381, 382}) {
            assertTrue(ReferenciaEdital.itens(idMenu).isEmpty(), "id_menu " + idMenu + " não tem card");
        }
    }

    @Test
    public void menuLegadoNaoTemReferencia() {
        Menu m = new Menu();
        m.setIdMenu(1);
        assertFalse(m.isAtendeEdital());
        assertEquals(m.getTituloEdital(), "");
    }

    @Test
    public void tooltipMostraSoAsReferencias() {
        Menu m = new Menu();
        m.setIdMenu(368);
        assertTrue(m.isAtendeEdital());
        assertEquals(m.getTituloEdital(), "TR 5.2.2.6");

        Menu revisao = new Menu();
        revisao.setIdMenu(389);
        revisao.setHref("/muralha-digital/pages/processamento/revisao-placas/index.jsp");
        assertEquals(revisao.getTituloEdital(), "TR 5.2.2.5<br>TR 5.7.4.3");
        assertEquals(revisao.getItensEditalTexto(), "5.2.2.5, 5.7.4.3");
    }
}
