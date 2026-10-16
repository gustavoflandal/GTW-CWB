# Painéis de Assertividade e de Pré-Processamento — Plano 30 (Fase 8 da auditoria de 2026-10-16)

> **Status:** Executado (2026-10-16): ver [Entrega 30](../entregas/30-paineis-assertividade-preproc.md).
> **Errata:** ver correções em [`00c-errata-correcoes.md`](00c-errata-correcoes.md) (§1, §2, §7, §14 aplicadas).
> **Depende de:** [Plano 29](29-kpis-relatorio-mensal.md) (KPIs, histórico), [Plano 08](08-painel-assertividade.md) (painel original).

**Objetivo:** completar o painel de assertividade com indicadores em tempo real, alertas de aderência e atualização automática (5.2.2.3, 5.2.2.6), e criar painel dedicado de pré-processamento com as métricas operacionais do TR (5.5.3.1-4).

## Referência ao Edital (TR TRANSALVADOR)

Texto literal de [`TR-Salvador-Sistema.md`](../../docs-editais/edital-salvador/TR-Salvador-Sistema.md):

> 5.2.2.3. O mecanismo de pré-classificação automatizada deverá manter índice mínimo mensal de aderência operacional de **80%**, aferido mediante comparação entre o resultado automatizado e a validação final realizada pelos operadores e auditores da CONTRATANTE.
>
> 5.2.2.6. O sistema deverá disponibilizar **painel de controle operacional** contendo indicadores em tempo real relativos à taxa de assertividade, volume de registros processados, divergências identificadas e registros encaminhados para revisão manual.
>
> 5.5.3.1. A CONTRATADA deverá disponibilizar relatórios operacionais e gerenciais relativos ao serviço de pré-processamento, contemplando, no mínimo: a) volume de registros recebidos; b) volume de registros pré-processados; c) volume de registros disponibilizados para validação; d) volume de registros descartados; e) índice de aproveitamento dos registros; f) tempo médio de processamento por registro e por lote; g) produtividade operacional por operador; h) taxa de divergência entre operadores; i) taxa de retrabalho operacional; j) quantidade de registros pendentes de processamento; k) backlog operacional; l) cumprimento dos níveis mínimos de serviço (SLA); m) motivos de descarte ou inconsistência classificados por categoria; n) histórico de classificação, reclassificação e validação operacional.
>
> 5.5.3.4. Os relatórios deverão possuir visualização em tela e exportação nos formatos PDF, CSV e XLS.

## Situação encontrada (investigação de 2026-10-16)

- **Painel de assertividade (Plano 08):** implementado com taxa de concordância, ranking de analistas e gráfico de evolução, mas faltava: atualização automática (tempo real), volume de registros processados, divergências identificadas, registros para revisão manual, e alerta visual quando < 80%.
- **Painel de pré-processamento:** não existia. As 14 métricas de 5.5.3.1 são coletadas mensalmente pelo `RelatorioPreProcMensalJob` (Plano 29), mas não havia painel operacional em tempo real.
- **Tabelas:** `vtr_status_analise` (id_veiculo_tempo_real, status_analise), `infracao_analise` (id, id_infracao, id_usuario, sequencia, classificacao, justificativa, dt_analise), `alerta_sla`, `alerta_sla_preproc` — dados suficientes para visualização.

## Decisões

- **Completar painel de assertividade** (5.2.2.6): adicionar auto-refresh (30s, toggle), novos cards (volume processado, divergências, encaminhados para revisão manual), alerta visual SweetAlert2 + borda vermelha quando taxa < 80% (5.2.2.3), linha de meta 80% no gráfico de evolução via chartjs-plugin-annotation.
- **Criar painel de pré-processamento** (5.5.3.1): `PreProcessamentoServlet` (`/MuralhaDigital/PreProcessamento`) com endpoint `metricas` que calcula em tempo real: volumes (a-d), aproveitamento (e), SLA (l), pendentes/backlog (j-k), produtividade por operador (g), classificação (n). Auto-refresh de 60s.
- **Métricas parciais:** divergência (h), retrabalho (i) e motivos de descarte (m) dependem de colunas que `vtr_status_analise` ainda não possui; retornam 0. A coluna `data_analise` não existe em `vtr_status_analise`, então tempo médio (f) usa `infracao_analise.dt_analise` como aproximação.
- **Sem migração de dados:** apenas menu novo para pré-processamento.

## Tarefas

- [x] 1. Completar `AssertividadeServlet`: endpoint `volumeProcessado`, flag `alertaAbaixo80`, `calcularTaxaConcordancia` como static.
- [x] 2. Atualizar JSP de assertividade: auto-refresh 30s, 4 novos cards (volume, divergências, revisão manual, 1ª análise), alerta < 80%, linha 80% no gráfico.
- [x] 3. Criar `PreProcessamentoServlet`: metricas (volumes, SLA, pendentes, produtividade, classificação, evolução diária).
- [x] 4. Criar JSP do painel de pré-processamento: cards KPI, gráficos Chart.js, tabela de operadores, auto-refresh 60s.
- [x] 5. Migração: menu para pré-processamento.
- [x] 6. Testes TestNG: 8 cenários de `calcularTaxaConcordancia`.

## Critérios de aceite

1. `..\.setup-gtw\build.ps1` compila sem erro.
2. Testes: AssertividadeServletTest 8. Total novos 8; com fases anteriores, 393/393 (5 skips de infra).
3. Migração de menu executada e roteiro [`docs/testes/30-paineis-assertividade-preproc.md`](../testes/30-paineis-assertividade-preproc.md) no navegador.
4. `python -I docs/referencia/scripts/validar_aderencia.py` sem erro.

## Fora do escopo

- Métricas que dependem de colunas ausentes em `vtr_status_analise` (divergência h, retrabalho i, motivos de descarte m): exibem 0 até a tabela ser estendida.
- PDF programático (5.5.3.4 parcial): Ctrl+P no navegador como fallback; PDF nativo fica para a Fase 9.
- Mapa de calor de congestionamento (mencionado no Plano 28 como Fase 8): já existe como `mapa-calor.jsp`; refinamentos ficam para a Fase 9.
