# SESSAO DASH-6 — Performance, Cache e Observabilidade

## Pre-requisito
- DASH-1 a DASH-5 concluidas (construtor de dashboards completo e funcional).

## Antes de qualquer codigo
1. Leia CLAUDE.md e docs/dashboards/construtor-dashboards-arquitetura.md.
2. Verifique: build OK, painel com todos os tipos de widget, pivot, exportacao.
3. Coletar baseline de performance: tempo medio de consulta, uso de CPU/memoria.
4. Liste em 10 linhas o que vai criar/modificar. Aguarde meu OK.

## Regras de stack (inviolaveis)
- Mesmas regras das sessoes anteriores.
- Sem ferramentas de APM pagas (Datadog, New Relic, etc.).
- Monitoramento via logs estruturados + tabela de auditoria existente.

## Objetivo
Otimizar performance do construtor de dashboards para uso em producao:
pre-agregacoes no Cube, cache inteligente, refreshKey, log de consultas lentas,
limites de concorrencia, e teste de carga para validar capacidade.

## Escopo

### Cube — Pre-agregacoes e cache
- **Pre-agregacoes** no Cube Store para os datasets mais usados:
  - Estrategia `simple` (unica suportada sem configuracao extra no SQL Server).
  - Definir pre-agregacoes nos modelos YAML gerados pelo GeradorModeloCube.
  - Criterio: datasets com mais de 100 consultas/dia ou mais de 10s de tempo medio.
  - Tabela `dash.dataset_estatistica` para contagem de uso e tempo medio.
- **refreshKey** baseado em `MAX(atualizado_em)` da tabela base do dataset:
  - Intervalo de verificacao: `every: '5 minutes'` para dados operacionais,
    `every: '1 hour'` para dados historicos.
  - Configuravel por dataset no editor (DASH-2 extensao).
- **CUBEJS_CONCURRENCY=4** (ja definido em DASH-1); avaliar se precisa ajustar.
- **Limites por consulta** (ja definidos no proxy):
  - Linhas: 10.000 padrao, 100.000 maximo para exportacao.
  - Timeout: 30s padrao, 120s maximo para exportacao.
  - `CUBEJS_DB_QUERY_TIMEOUT=120000`.

### Backend — Observabilidade
- **ConsultaLog**: tabela e servico para registrar consultas.
  ```
  dash.consulta_log (
      id BIGINT IDENTITY PRIMARY KEY,
      usuario_id INT NOT NULL,
      dataset_id INT NOT NULL,
      widget_id INT NULL,
      painel_id INT NULL,
      consulta_json NVARCHAR(MAX),
      tempo_ms INT NOT NULL,
      linhas_retornadas INT NOT NULL,
      cache_hit BIT NOT NULL DEFAULT 0,
      lenta BIT NOT NULL DEFAULT 0,
      erro VARCHAR(500) NULL,
      criado_em DATETIME2(3) NOT NULL DEFAULT SYSDATETIME()
  )
  ```
- **ConsultaLogService**: grava cada consulta no proxy (async, nao bloqueia resposta).
  - Marca como `lenta` se tempo_ms > threshold configuravel (padrao 5000ms).
  - Agrega estatisticas por dataset em `dataset_estatistica`.
- **HealthCheckServlet**: endpoint `/api/dash/health` retornando:
  - Status do Cube (`/readyz`).
  - Tempo de resposta do Cube.
  - Numero de consultas na ultima hora.
  - Consultas lentas na ultima hora.
  - Uso de pre-agregacoes (% de consultas atendidas por cache/pre-agregacao).

### Backend — Dashboard de monitoramento
- **Painel de monitoramento pre-configurado** (seed):
  - Widget KPI: consultas/hora.
  - Widget KPI: tempo medio de resposta.
  - Widget KPI: cache hit rate.
  - Widget linhas: evolucao de tempo de resposta por hora (ultimas 24h).
  - Widget barras: top 10 consultas mais lentas (dataset, tempo, usuario).
  - Widget tabela: consultas com erro na ultima hora.
  - Acessivel apenas por perfil CATALOGO_ADMIN.

### Frontend — Indicadores no painel
- **Indicador de performance por widget**:
  - Tempo de carregamento no rodape do widget (ex: "324ms" ou "2.1s").
  - Icone de cache hit (raio) quando servido por pre-agregacao.
  - Icone de alerta (triangulo) quando consulta demorou mais de 5s.
- **Indicador global no painel**:
  - Badge no canto: "Atualizado ha X min" com cor (verde < 5min, amarelo < 15min).

### Teste de carga
- **Script de teste** (em shell/PowerShell, sem ferramenta paga):
  - Simular N usuarios simultaneos fazendo consultas.
  - Medir: tempo medio, P95, P99, erros.
  - Cenarios:
    1. 10 usuarios, 1 consulta cada, datasets diferentes.
    2. 10 usuarios, refresh simultaneo do mesmo painel.
    3. 50 consultas sequenciais no mesmo dataset (avaliar cache).
    4. Consulta pesada (100k linhas, exportacao XLSX).
  - Salvar resultados em `docs/relatorios/carga-dash-6.md`.

### Migracao SQL
- `dash.consulta_log` — tabela de log de consultas.
- `dash.dataset_estatistica` — contadores agregados por dataset.
- Indices em `consulta_log`: `(criado_em)`, `(dataset_id, criado_em)`, `(lenta, criado_em) WHERE lenta = 1` (filtrado).

### Extensoes ao GeradorModeloCube
- Gerar `preAggregations` no YAML para datasets marcados como candidatos.
- Gerar `refreshKey` com `sql` customizado por dataset.

## Testes obrigatorios
1. Consulta repetida em <1s (atendida por cache Cube) — verificar com `x-cache` ou log.
2. Pre-agregacao construida com sucesso — Cube Store popula tabela.
3. Consulta lenta (>5s) registrada em `consulta_log` com flag `lenta`.
4. Health check retorna status OK quando Cube esta saudavel.
5. Health check retorna status DEGRADED quando Cube esta fora.
6. Dashboard de monitoramento renderiza com dados reais de consulta_log.
7. Tempo de resposta do painel com 10 widgets < 3s total (paralelo).
8. Indicador de cache hit aparece no widget quando dado veio de pre-agregacao.
9. Teste de carga: 10 usuarios simultaneos sem erro 5xx.
10. RefreshKey: dado alterado no banco reflete no dashboard apos intervalo configurado.

## Proibicoes
- Ferramentas de APM pagas.
- Pre-agregacao com estrategia que exija Enterprise do SQL Server.
- Log de consulta bloqueando a resposta ao usuario (async obrigatorio).
- Desabilitar cache para "resolver" problemas de dados desatualizados (ajustar refreshKey).
- Resultados de teste de carga fabricados (executar realmente e colar saida).

## Definition of Done
- Build com JDK 13 OK.
- Pre-agregacoes funcionando (evidencia no log do Cube).
- Cache hit rate > 50% em cenario de teste.
- Dashboard de monitoramento funcional.
- Teste de carga executado e documentado.
- Testes unitarios + integracao, zero ignorados.
- docs/relatorios/SESSAO-DASH-6-relatorio.md com baseline vs otimizado.

## Arquivos esperados

| Acao | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261013_dashboards_observabilidade.sql` |
| Criar | `src/main/java/muralha/digital/dashboards/ConsultaLogService.java` |
| Criar | `src/main/java/muralha/digital/dashboards/HealthCheckServlet.java` |
| Modificar | `src/main/java/muralha/digital/dashboards/DashConsultaServlet.java` (log async) |
| Modificar | `src/main/java/muralha/digital/dashboards/GeradorModeloCube.java` (preAggregations, refreshKey) |
| Modificar | `src/main/webapp/static/js/dashboards/painel.js` (indicadores performance) |
| Criar | `scripts/teste-carga-dashboards.ps1` |
| Criar | `docs/relatorios/carga-dash-6.md` |
| Criar | `src/test/java/muralha/digital/dashboards/ConsultaLogServiceTest.java` |
| Criar | `src/test/java/muralha/digital/dashboards/HealthCheckTest.java` |
| Criar | `docs/relatorios/SESSAO-DASH-6-relatorio.md` |
