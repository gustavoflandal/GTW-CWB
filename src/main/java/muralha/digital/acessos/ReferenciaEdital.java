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
            Map.entry(383, List.of("7.2.3", "7.2.4")),
            // Plano 23 — Superset / Analises (id fixo na migracao)
            Map.entry(395, List.of("5.2.8.4", "5.4.2.1-6", "5.4.3.5-7", "5.4.4.1-3", "5.4.5.1-2", "5.4.6.1-3", "5.5.3.1-4"))
    );

    private static final Map<String, List<String>> ITENS_POR_HREF = Map.ofEntries(
            // Plano 23 / SUP-6 — Tipos de Alerta (id_menu dinamico)
            Map.entry("/MuralhaDigital/TipoAlertaConfig",
                    List.of("5.3.1.3", "5.3.2.1", "5.3.3.3-4", "5.3.4.2-3", "5.3.6", "5.3.7.4 d-e", "5.3.7.6", "5.3.7.8")),
            // Plano 23 / SUP-7 — Relatorios Gerenciais (id_menu dinamico)
            Map.entry("/MuralhaDigital/RelatorioGerencial",
                    List.of("5.4.3.5-7", "5.4.5.1-2", "5.4.6.2-3", "5.5.3.1-3")),
            // Plano 23 / SUP-8 — Painel de Pre-Processamento (id_menu dinamico)
            Map.entry("/MuralhaDigital/PreProcessamento",
                    List.of("5.2.2.3", "5.2.2.6", "5.5.3.1")),
            // Plano 25 — Conferencia SHA-256 na recepcao das imagens (id_menu dinamico)
            Map.entry("/muralha-digital/pages/monitoramento/integridade/index.jsp",
                    List.of("5.2.1.2", "5.2.1.3 a-f")),
            // Plano 26 — Logs tecnicos e historico de parametros (id_menu dinamico)
            Map.entry("/muralha-digital/pages/monitoramento/logs-tecnicos/index.jsp",
                    List.of("5.4.7.1-7", "5.2.7.2", "5.2.7.3")),
            // Plano 27 — Score de OCR e revisao de placas (id_menu dinamico)
            Map.entry("/muralha-digital/pages/processamento/revisao-placas/index.jsp",
                    List.of("5.2.2.5", "5.7.1.4", "5.7.4.1-4", "5.7.5.1-2", "5.7.5.4"))
    );

    private ReferenciaEdital() {
    }

    public static List<String> itens(int idMenu) {
        return ITENS_POR_MENU.getOrDefault(idMenu, Collections.emptyList());
    }

    public static List<String> itensPorHref(String href) {
        if (href == null) return Collections.emptyList();
        return ITENS_POR_HREF.getOrDefault(href, Collections.emptyList());
    }

    public static String titulo(int idMenu) {
        return tituloFromItens(itens(idMenu));
    }

    public static String tituloPorHref(String href) {
        return tituloFromItens(itensPorHref(href));
    }

    private static String tituloFromItens(List<String> itens) {
        if (itens.isEmpty()) {
            return "";
        }
        return "Itens do edital (TR TRANSALVADOR):<br>" + String.join("<br>", itens);
    }
}
