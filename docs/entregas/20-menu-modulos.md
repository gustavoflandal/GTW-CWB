# Entrega 20 — Registro dos Modulos no Menu da Interface

**Data:** 2026-10-09  
**Status:** 🟡 Implementado — aguardando teste (critérios de aceite ainda não executados; revisão de 2026-10-16)

> **Atualização de 2026-10-18** ([`20261018_remove_cards_duplicados.sql`](../banco-de-dados/migracoes/20261018_remove_cards_duplicados.sql)): Os cards Configuração de KPIs (372), Catálogo de Dados (378), Guarnição (306), Relatórios de Chamados (381) e Configuração de Chamados (382) foram retirados; as telas são abertas pelos botões dos módulos (KPIs, Catálogo de Datasets, Central de Atendimento e Chamados).

## Escopo

Insercao das entradas de menu no banco de dados (`sis_menu`, `sis_menu_infos`, `sis_menu_direitos`) para que os 10 modulos desenvolvidos nos Planos 01–19 fiquem acessiveis pela interface web da Muralha Digital.

## Origem

- **Sem plano próprio.** Esta entrega nasceu da pendência registrada na execução do [Plano 17](../planos-salvador/17-timelapse.md), na seção [Pendências da Entrega 17](17-timelapse.md#pendencias): "Registrar menu no banco via INSERT em `dbo.sis_menu_infos`". A mesma pendência aparecia em outras entregas (ex.: [Entrega 05](05-dupla-analise.md#pendências)).
- Abrange todos os modulos com tela JSP propria que nao possuiam entrada no menu.
- Numeração: o número 20 desta entrega não corresponde ao Plano 20 (Dashboard Builder), cuja entrega é a [21](21-dashboard-builder.md). Ver [`rastreabilidade-tr.md`](rastreabilidade-tr.md#cobertura-por-plano).

## Modulos registrados

### Tabela `sis_menu` (id_menu 366–375)

| id_menu | Modulo | JSP |
|---|---|---|
| 366 | Log de Auditoria | `pages/auditoria/consulta-log.jsp` |
| 367 | SLA de Latencia | `pages/monitoramento/sla-latencia/index.jsp` |
| 368 | Painel de Assertividade | `pages/monitoramento/assertividade/index.jsp` |
| 369 | SLA Pre-processamento 72h | `pages/monitoramento/sla-preproc/index.jsp` |
| 370 | Gestao de Lotes | `pages/processamento/lotes/index.jsp` |
| 371 | Dashboard KPIs | `pages/dashboard/kpis/index.jsp` |
| 372 | Configuracao de KPIs | `pages/admin/kpis/index.jsp` |
| 373 | Time-lapse de Passagens | `pages/monitoramento/timelapse/index.jsp` |
| 374 | Disponibilidade dos Equipamentos | `pages/monitoramento/disponibilidade/index.jsp` |
| 375 | Mapa de Incidentes Externos | `pages/monitoramento/mapa/index.jsp` |

### Tabela `sis_menu_infos` (id_infos 33–42) — agrupamento por secao

| Secao pai | id_infos | Modulo | ordenacao |
|---|---|---|---|
| Analise de Dados (15) | 33 | SLA de Latencia | 1 |
| Analise de Dados (15) | 34 | Assertividade | 2 |
| Analise de Dados (15) | 35 | SLA Pre-processamento | 3 |
| Analise de Dados (15) | 36 | Time-lapse | 4 |
| Analise de Dados (15) | 37 | Disponibilidade | 5 |
| Analise de Dados (15) | 38 | Log de Auditoria | 6 |
| Mapas (7) | 39 | Mapa de Incidentes | 5 |
| Dashboards (1) | 40 | KPIs | 2 |
| Cadastro e Config (16) | 41 | Configuracao KPIs | 7 |
| Cadastro e Config (16) | 42 | Gestao de Lotes | 8 |

### Tabela `sis_menu_direitos` — 20 registros

Acesso liberado para os grupos:
- **41** — Anel de Seguranca
- **42** — Anel de Seguranca (Supervisores)

## Arquivos produzidos

| Arquivo | Descricao |
|---|---|
| `docs/banco-de-dados/migracoes/20261009_menu_modulos_desenvolvidos.sql` | Script SQL com INSERT nas 3 tabelas (sis_menu, sis_menu_infos, sis_menu_direitos) |
| `docs/entregas/20-menu-modulos.md` | Este documento de entrega |

## Criterios de aceite

- [x] Script SQL executado sem erros no GTW_MURALHA_DEV
- [x] 10 registros em sis_menu (verificado via COUNT)
- [x] 10 registros em sis_menu_infos (verificado via COUNT)
- [x] 20 registros em sis_menu_direitos (verificado via COUNT)
- [ ] Modulos visiveis no menu para usuarios dos grupos 41/42
- [ ] Navegacao para cada tela funciona corretamente

## Screenshots

Ver [`docs/screenshots/`](../screenshots/) para capturas das telas.
