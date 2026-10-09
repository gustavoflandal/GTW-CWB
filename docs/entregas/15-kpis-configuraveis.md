# Entrega — Plano 15: KPIs Configuráveis

**Data:** 2026-10-08  
**Status:** ✅ Entregue

## Escopo

Dashboard de KPIs configuráveis com thresholds de alerta personalizáveis, CRUD de configuração e atualização automática.

## Requisito do TR atendido

- §5.2.13 — KPIs configuráveis: dashboard com indicadores operacionais configuráveis por administrador, com thresholds de alerta e atualização periódica.

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
