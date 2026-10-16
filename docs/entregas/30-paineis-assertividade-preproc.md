# Entrega — Plano 30: Painéis de Assertividade e de Pré-Processamento

> **Data:** 2026-10-16
> **Status:** 🟡 Implementado — migração executada, aguardando teste no navegador.
> **Fase:** 8 de 11 da correção da auditoria de 2026-10-16.

## Escopo

Conclusão do painel de assertividade (5.2.2.6) com indicadores em tempo real — taxa de concordância, volume processado, divergências, registros para revisão manual — alerta automático quando aderência < 80% (5.2.2.3) e auto-refresh. Criação de painel dedicado de pré-processamento (5.5.3.1) com métricas operacionais: volumes (a-d), aproveitamento (e), SLA (l), pendentes/backlog (j-k), produtividade por operador (g), classificação (n) e evolução diária.

## Origem

- Plano: [`30-paineis-assertividade-preproc.md`](../planos-salvador/30-paineis-assertividade-preproc.md)
- **Errata aplicada:** [`00c-errata-correcoes.md`](../planos-salvador/00c-errata-correcoes.md) §1/§2 (sessão e acesso), §14 (Agendador)

## O que mudou

- **Painel de assertividade completado** (5.2.2.6): 4 novos cards (volume processado, divergências, encaminhados para revisão, 1ª análise), auto-refresh de 30s com toggle, alerta visual (SweetAlert2 + borda vermelha) quando taxa de concordância < 80% (5.2.2.3), linha de meta 80% no gráfico de evolução via chartjs-plugin-annotation, CSV com BOM UTF-8 e separador `;`.
- **Painel de pré-processamento** (5.5.3.1): novo `PreProcessamentoServlet` (`/MuralhaDigital/PreProcessamento`) com endpoint `metricas` que calcula em tempo real: volumes recebidos/processados/disponibilizados/descartados (a-d), aproveitamento (e), SLA (l), pendentes e backlog > 72h (j-k), produtividade por operador (g), classificação (n), evolução diária. JSP com cards KPI, gráfico barras (recebidos × processados), doughnut de classificação, tabela de operadores, auto-refresh 60s.
- **Menu** para pré-processamento no grupo Análise de Dados (menu=394, infos=55).

## Arquivos

| Tipo | Arquivos |
|---|---|
| Servlet (alterado) | `src/main/java/muralha/digital/assertividade/AssertividadeServlet.java` (novos indicadores, `alertaAbaixo80`) |
| Servlet (novo) | `src/main/java/muralha/digital/preprocessamento/PreProcessamentoServlet.java` |
| Frontend (alterado) | `src/main/webapp/muralha-digital/pages/monitoramento/assertividade/index.jsp` (auto-refresh, novos cards, alerta 80%) |
| Frontend (novo) | `src/main/webapp/muralha-digital/pages/monitoramento/pre-processamento/index.jsp` |
| Testes | `src/test/java/muralha/digital/assertividade/AssertividadeServletTest.java` |
| Banco | `docs/banco-de-dados/migracoes/20261016_paineis_assertividade_preproc.sql` |

## Migrações

| Script | Situação |
|---|---|
| `20261016_paineis_assertividade_preproc.sql` | **Executado (2026-10-16)** — menu criado (id_menu=394, id_infos=55). Sem criação de tabelas (usa dados existentes). |

## Critérios de aceite

- [x] 1. `..\.setup-gtw\build.ps1` compila sem erro (Windows, 2026-10-16).
- [x] 2. Testes TestNG (Windows, 2026-10-16): `AssertividadeServletTest` 8. Total: 393/393 (5 skips de infra).
- [ ] 3. Migração executada e roteiro [`docs/testes/30-paineis-assertividade-preproc.md`](../testes/30-paineis-assertividade-preproc.md) no navegador.
- [x] 4. `python -I docs/referencia/scripts/validar_aderencia.py` sem erro.

## Situação dos itens do TR

| Item | Antes | Depois | Por quê |
|---|---|---|---|
| 5.2.2.3 | ✅ Implementado (taxa existe) | ✅ | Alerta visual quando < 80% agora funciona |
| 5.2.2.6 | ⚠️ Parcial | ✅ | Auto-refresh, volume, divergências, revisão manual, alerta < 80% |
| 5.5.3.1 | ⚠️ Parcial | ⚠️ | Painel operacional com a-e, g, j-l, n; h, i, m dependem de colunas ausentes |
| 5.5.3.2 | ⚠️ Parcial | ⚠️ | Segmentação por período; filtros adicionais na Fase 9 |
| 5.5.3.4 | ✅ | ✅ | Visualização em tela; exportação CSV/XLS nos relatórios (Fase 7) |

## Limitações conhecidas

- As métricas h (divergência), i (retrabalho) e m (motivos de descarte) dependem de colunas `resultado_ia`, `revisado` e `motivo_descarte` que `vtr_status_analise` não possui; retornam 0 no painel até a tabela ser estendida (Fase 9 ou complementar).
- O tempo médio de processamento (f) não aparece no painel porque `vtr_status_analise` não possui `data_analise`; a aproximação via `infracao_analise.dt_analise` é usada no relatório mensal (Fase 7) mas não exposta neste painel.
- PDF programático (5.5.3.4 parcial): Ctrl+P como fallback; implementação nativa na Fase 9.
