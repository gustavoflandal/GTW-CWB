package muralha.digital.acessos;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Item do edital (TR TRANSALVADOR) que exige cada funcionalidade com card no menu: só o item
 * específico (nunca o número de título de seção, faixas ou itens apenas relacionados).
 * Fonte: TR-Salvador-Sistema.md; itens 7.2.x da planilha "Atividades Edital Salvador 2026", aba "Sist. Chamado".
 */
public final class ReferenciaEdital {

    private static final Map<Integer, List<String>> ITENS_POR_MENU = Map.ofEntries(
            Map.entry(366, List.of("5.1.10")),          // Log de Auditoria — consulta dos logs com filtros e exportação
            Map.entry(367, List.of("5.6.2.3")),         // SLA de Latência — recepção em até 4 segundos
            Map.entry(368, List.of("5.2.2.6")),         // Assertividade — painel de controle operacional
            Map.entry(369, List.of("5.5.2.1")),         // SLA de Pré-processamento — prazo de 72 horas
            Map.entry(370, List.of("5.2.5.1")),         // Gestão de Lotes — organização dos registros em lotes
            Map.entry(371, List.of("5.4.6.1")),         // KPIs — cálculo e acompanhamento contínuo de indicadores
            Map.entry(372, List.of("5.4.6.3")),         // Configuração de KPIs — indicadores parametrizáveis
            Map.entry(373, List.of("5.7.7.1")),         // Time-lapse — vídeos operacionais sintéticos
            Map.entry(374, List.of("5.2.7.1")),         // Disponibilidade — monitoramento do estado dos equipamentos
            Map.entry(375, List.of("5.3.7.3")),         // Mapa de Incidentes — integração com Waze/Google Maps
            Map.entry(376, List.of("5.4.2.5")),         // Painéis de Análise — dashboards personalizados
            Map.entry(377, List.of("5.4.2.4.e")),       // Catálogo de Datasets — parametrização de indicadores e painéis
            Map.entry(378, List.of("5.4.2.4.e")),       // Catálogo de Dados — parametrização de indicadores e painéis
            Map.entry(379, List.of("7.2.3.a", "7.2.3.b", "7.2.3.c", "7.2.3.e", "7.2.3.f", "7.2.4")), // Chamados
            Map.entry(381, List.of("7.2.3.d")),         // Relatórios de Chamados
            Map.entry(382, List.of("7.2.4")),           // Configuração de Chamados — prazos de atendimento e resolução
            Map.entry(395, List.of("5.4.2.1"))          // Análises (Superset) — dashboards analíticos interativos
    );

    // Menus com id_menu gerado na migração: identificados pelo href.
    private static final Map<String, List<String>> ITENS_POR_HREF = Map.ofEntries(
            Map.entry("/MuralhaDigital/TipoAlertaConfig",
                    List.of("5.3.1.3", "5.3.3.4", "5.3.7.6")),  // tratamentos · destaque dos críticos · parametrização
            Map.entry("/MuralhaDigital/RelatorioGerencial",
                    List.of("5.4.3.7", "5.5.3.3")),             // relatório gerencial periódico · consolidado mensal
            Map.entry("/MuralhaDigital/PreProcessamento",
                    List.of("5.5.3.1")),                        // indicadores do pré-processamento
            Map.entry("/muralha-digital/pages/monitoramento/integridade/index.jsp",
                    List.of("5.2.1.2", "5.2.1.3")),             // SHA-256 · recebidos, íntegros e rejeitados
            Map.entry("/muralha-digital/pages/monitoramento/logs-tecnicos/index.jsp",
                    List.of("5.4.7.1", "5.4.7.6")),             // repositório de logs · histórico de parâmetros
            Map.entry("/muralha-digital/pages/processamento/revisao-placas/index.jsp",
                    List.of("5.2.2.5", "5.7.4.3"))              // revisão por divergência · por score abaixo do limiar
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
}
