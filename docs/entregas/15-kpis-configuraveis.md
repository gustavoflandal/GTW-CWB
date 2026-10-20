# Entrega — Plano 15: KPIs Configuráveis

**Data:** 2026-10-08  
**Status:** 🟡 Implementado — aguardando teste (critérios de aceite ainda não executados; revisão de 2026-10-16)

> **Atualização de 2026-10-18** ([`20261018_remove_cards_duplicados.sql`](../banco-de-dados/migracoes/20261018_remove_cards_duplicados.sql)): A Configuração de KPIs não tem mais card em Cadastro e Configuração; é aberta pelo botão **Configurar** do painel KPIs (mesmas permissões, grupos 41 e 42).

> **Atualização de 2026-10-19** ([Entrega 35](35-correcoes-seguranca-funcionais.md), SEG-03, SEG-04, SEG-05 e FUN-04): o SQL dos KPIs é validado por `ValidadorSqlKpi` ao salvar, no painel e no `KpiSnapshotJob` (timeout de 30 s); `listarConfig` e `salvar` exigem o direito do menu 372; os valores do servidor são escapados nas telas; a migração `20261019_kpis_corrige_consultas.sql` corrigiu o SQL dos KPIs 5, 7, 8, 9 e 11. Desde `20261019_registro_modulos_novos.sql`, a Configuração de KPIs (menu 372) é dos grupos 11 e 42 (o grupo 41 foi retirado); o painel e o servlet `/MuralhaDigital/Kpi` (menu 429) seguem com 11, 41 e 42.

## Escopo

Dashboard de KPIs configuráveis com thresholds de alerta personalizáveis, CRUD de configuração e atualização automática.

## Origem

- Plano [`15-kpis-configuráveis.md`](../planos-salvador/15-kpis-configuráveis.md) — KPIs Configuráveis
- **Requisitos TR:** §5.2.8.4 (dashboards analíticos em tempo real), §5.4.2.1–6 (dashboards interativos), §5.4.6.1–3 (indicadores parametrizáveis com metas e alertas)
- **Análise de aderência:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) §2 e §4

## Arquivos produzidos

| Arquivo | Descrição |
|---|---|
| `docs/banco-de-dados/migracoes/20261008_lotes_kpis.sql` | Migração combinada (Planos 14+15): CREATE TABLE `muralha.kpi_config` com 4 KPIs padrão |
| `src/main/java/muralha/digital/kpi/KpiServlet.java` | GET: valores (executa queries) e listarConfig. POST: salvar (INSERT/UPDATE). Validação SELECT-only |
| `src/main/webapp/muralha-digital/pages/dashboard/kpis/index.jsp` | Dashboard com cards coloridos (OK/WARN/CRIT), auto-refresh 60s |
| `src/main/webapp/muralha-digital/pages/admin/kpis/index.jsp` | CRUD de KPIs com offcanvas, campos para SQL/thresholds/ordem/ativo |

## Arquitetura

- **Tabela `kpi_config`**: armazena query SQL, thresholds (OK/Warn), unidade, ordem e flag ativo
- **Execução segura**: valida que `query_sql` inicia com SELECT antes de executar
- **Lógica de threshold bidirecional**: suporta "menor é melhor" (latência) e "maior é melhor" (taxa)
- **4 KPIs padrão**: Passagens Hoje, Pendentes de Análise, Pré-aprovadas (7d), Equipamentos Ativos (24h)
- **Queries usando colunas reais**: `veiculo_tempo_real.data`, `vtr_status_analise.status_analise`, `id_local`

## Critérios de aceite

- [x] Build compila sem erros
- [ ] Dashboard exibe cards com valores e cores corretas
- [ ] Auto-refresh a cada 60 segundos
- [ ] Tela admin lista KPIs configurados
- [ ] Criar/editar KPI com query SQL customizada
- [ ] Validação: rejeitar query que não inicia com SELECT

## Pendências

- Registrar menu no banco via INSERT em `dbo.sis_menu_infos`
- Teste manual completo (ver `docs/testes/15-kpis-configuraveis.md`)
