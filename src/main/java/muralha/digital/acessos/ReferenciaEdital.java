package muralha.digital.acessos;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Itens do edital (TR TRANSALVADOR) atendidos por cada funcionalidade nova do menu, por id_menu.
 * Fonte: secao "Referencia ao Edital" de cada plano em docs/planos-salvador/.
 */
public final class ReferenciaEdital {

    private static final Map<Integer, List<String>> ITENS_POR_MENU = Map.ofEntries(
            // Plano 01 — Log de Auditoria
            Map.entry(366, List.of("5.1.7-8", "5.1.9", "5.1.10", "5.4.7.1-7", "5.5.4.5 (parcial)")),
            // Plano 07 — SLA de Latencia
            Map.entry(367, List.of("5.2.7", "5.3.7.5", "5.6.2.3", "5.7.9.1", "5.8.6.1")),
            // Plano 08 — Painel de Assertividade
            Map.entry(368, List.of("5.2.2.3", "5.2.2.6")),
            // Plano 10 — SLA de Pre-processamento 72h
            Map.entry(369, List.of("5.2.1.6 (parcial)", "5.5.2.1", "5.5.3.1 (parcial)")),
            // Plano 14 — Gestao de Lotes
            Map.entry(370, List.of("5.2.5.1", "5.2.5.3", "5.2.5.4-5")),
            // Plano 15 — KPIs e Configuracao de KPIs
            Map.entry(371, List.of("5.2.8.4", "5.4.2.1-2", "5.4.2.5", "5.4.6.1-3")),
            Map.entry(372, List.of("5.2.8.4", "5.4.2.1-2", "5.4.2.5", "5.4.6.1-3")),
            // Plano 17 — Time-lapse de Passagens
            Map.entry(373, List.of("5.2.4.1-2", "5.7.7")),
            // Plano 18 — Disponibilidade dos Equipamentos
            Map.entry(374, List.of("5.2.7", "5.4.2.3.e", "5.6.1.5", "5.8.2.1 (parcial)", "5.8.6.1")),
            // Plano 19 — Mapa de Incidentes Externos
            Map.entry(375, List.of("5.3.7.1-2", "5.3.7.3", "5.3.7.7")),
            // Plano 20 — Paineis de Analise
            Map.entry(376, List.of("5.4.2.4a-b", "5.4.2.5", "5.4.5", "5.5.3.1")),
            // Plano 20 — Catalogos que permitem a CONTRATANTE configurar os paineis
            Map.entry(377, List.of("5.4.2.5")),
            Map.entry(378, List.of("5.4.2.5")),
            // Plano 22 — Sistema de Gerenciamento de Chamados (aba "Sist. Chamado")
            Map.entry(379, List.of("7.2.3.a", "7.2.3.b", "7.2.3.c", "7.2.3.f", "7.2.4")),
            Map.entry(380, List.of("7.2.3.b", "7.2.3.c", "7.2.3.e")),
            Map.entry(381, List.of("7.2.3.d", "7.2.3.f")),
            Map.entry(382, List.of("7.2.3.a")),
            // Categoria "Sistema de Chamados" na tela inicial (agrupa os cards 379, 381 e 382)
            Map.entry(383, List.of("7.2.3", "7.2.4"))
    );

    private ReferenciaEdital() {
    }

    public static List<String> itens(int idMenu) {
        return ITENS_POR_MENU.getOrDefault(idMenu, Collections.emptyList());
    }

    /** Texto da caixa exibida ao passar o mouse no asterisco (com quebras de linha em HTML). */
    public static String titulo(int idMenu) {
        List<String> itens = itens(idMenu);
        if (itens.isEmpty()) {
            return "";
        }
        return "Itens do edital (TR TRANSALVADOR):<br>" + String.join("<br>", itens);
    }
}
