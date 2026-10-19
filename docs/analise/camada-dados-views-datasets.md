# Camada de dados da plataforma de Análises — views, datasets e tabelas de origem

**Data:** 2026-10-19
**Escopo:** schema `analise` do banco `GTW_MURALHA_DEV` e datasets do Apache Superset que o consomem.
**Natureza:** documento técnico de referência (somente leitura; nenhuma alteração de banco ou de código).

**Fontes consultadas**

- Banco `GTW_MURALHA_DEV` (SQL Server, 10.0.0.200), apenas `SELECT` em `sys.objects`, `sys.columns`, `sys.sql_expression_dependencies`, `sys.partitions`, `OBJECT_DEFINITION` e `analise.carga_log` (consulta de 2026-10-19).
- Scripts de migração em [`docs/banco-de-dados/migracoes/`](../banco-de-dados/migracoes/): [fundação](../banco-de-dados/migracoes/20261016_analise_fundacao.sql), [camada de dados](../banco-de-dados/migracoes/20261017_analise_camada_dados.sql), [correção da camada](../banco-de-dados/migracoes/20261017_analise_correcao_camada.sql), [versão do equipamento](../banco-de-dados/migracoes/20261017_analise_versao_equipamento.sql) e [mapas geo](../banco-de-dados/migracoes/20261017_analise_mapas_geo.sql).
- Datasets declarativos em [`analise/superset/datasets/`](../../analise/superset/datasets/), datasets dos painéis em [`fontes.py`](../../analise/superset/paineis/fontes.py) e [`paineis/datasets/*.sql`](../../analise/superset/paineis/datasets/), painéis em [`operacionais.py`](../../analise/superset/paineis/operacionais.py) e [`demonstracao.py`](../../analise/superset/paineis/demonstracao.py), RLS em [`rls.yaml`](../../analise/superset/governanca/rls.yaml), alertas e relatórios em [`analise/superset/alertas/`](../../analise/superset/alertas/) e carga em [`refresh-materialized.sh`](../../analise/scripts/refresh-materialized.sh).
- Superset em execução (container `analise-postgres-1`, consulta somente leitura às tabelas `tables`, `slices`, `dashboards`, `dashboard_slices` e `row_level_security_filters`).
- Contexto: [README](README.md), [Guia do Administrador](guia-administrador.md), [SUP-2](sessoes/SUP-02-camada-dados.md) e [SUP-3](sessoes/SUP-03-datasets-semantica.md).

## Visão geral do fluxo

As tabelas operacionais (`dbo` e `muralha`) alimentam, por procedures de carga, três tabelas materializadas no schema `analise`. As views `analise.v_*` leem as materializadas ou diretamente as tabelas de origem. O Superset expõe as views como datasets físicos (declarativos) e, nos painéis, como datasets virtuais `gtw_*` (SELECT guardado no Superset), que alimentam os gráficos e dashboards.

```mermaid
flowchart LR
    A["Tabelas dbo e muralha"] --> B["Procedures spu_carga"]
    B --> C["Tabelas materializadas mat"]
    A --> D["Views analise v_*"]
    C --> D
    D --> E["Datasets do Superset"]
    A --> E
    E --> F["Graficos"]
    F --> G["Dashboards"]
```

Observação: parte dos datasets virtuais (`gtw_infracoes`, `gtw_trafego_dia`, `gtw_faixa_velocidade`, `gtw_descartes`, `gtw_waze_alertas`) lê tabelas `dbo`/`mobilidade` diretamente, sem passar por view de `analise` (seta A para E).

---

## 1. Inventário do schema `analise`

Total: 12 views, 3 tabelas materializadas, 1 tabela de log e 3 procedures (nenhuma função). Datas de criação/alteração conforme `sys.objects`. Linhas (≈) conforme `sys.partitions` em 2026-10-19 (views não têm contagem própria).

| Objeto | Tipo | Finalidade | Linhas (≈) |
|---|---|---|---|
| `analise.v_local_versao` | View | Versões históricas de cada equipamento com vigência (início/fim) | não se aplica |
| `analise.v_local_vigente` | View | Versão atual de cada equipamento, com a última coordenada conhecida | não se aplica |
| `analise.v_equipamentos` | View | Cadastro de equipamentos com flag de atividade em 24 h e área monitorada | não se aplica |
| `analise.v_areas_monitoradas_geojson` | View | Áreas monitoradas com polígono para mapas | não se aplica |
| `analise.v_alertas` | View | Alertas da Muralha Digital sem dados pessoais (sem placa) | não se aplica |
| `analise.v_assertividade_dia` | View | Resultado da dupla análise por dia | não se aplica |
| `analise.v_disponibilidade_dia` | View | Disponibilidade diária por equipamento (horas com captura / 24) | não se aplica |
| `analise.v_preprocessamento` | View | Pré-processamento de infrações por dia, equipamento e operador | não se aplica |
| `analise.v_sla_latencia_dia` | View | SLA de latência por dia | não se aplica |
| `analise.v_sla_preproc_dia` | View | SLA de pré-processamento por dia (snapshots de pendências) | não se aplica |
| `analise.v_passagens_geolocalizado` | View | Passagens por hora e equipamento com coordenadas | não se aplica |
| `analise.v_infracoes_geolocalizado` | View | Infrações por dia e equipamento com coordenadas | não se aplica |
| `analise.mat_passagens_hora` | Tabela | Passagens agregadas por hora, equipamento, faixa, classificação e tipo de placa | 62.116 |
| `analise.mat_infracoes_dia` | Tabela | Infrações agregadas por dia, equipamento, faixa e enquadramento | 1.020 |
| `analise.mat_areas_monitoradas` | Tabela | Polígonos das áreas monitoradas convertidos de JSON | 4 |
| `analise.carga_log` | Tabela | Log de execução das procedures de carga | 58 |
| `analise.spu_carga_passagens_hora` | Procedure | Carrega `mat_passagens_hora` | não se aplica |
| `analise.spu_carga_infracoes_dia` | Procedure | Carrega `mat_infracoes_dia` | não se aplica |
| `analise.spu_carga_areas_monitoradas` | Procedure | Carrega `mat_areas_monitoradas` | não se aplica |

Datas de criação: objetos da fundação e das views originais em 2026-10-16; `v_local_vigente`, `mat_areas_monitoradas`, `spu_carga_areas_monitoradas` e as views geolocalizadas em 2026-10-17; `v_local_versao` criada em 2026-10-17 22:35. Várias views foram recriadas em 2026-10-17 (ver migrações de correção e de versão do equipamento).

---

## 2. Views

Convenção: "Origem" lista tabelas e views lidas diretamente; "Tabelas finais" resolve a cadeia até as tabelas físicas. As origens vêm de `sys.sql_expression_dependencies` e foram conferidas com a definição (`OBJECT_DEFINITION`).

### 2.1 `analise.v_local_versao`

- **Finalidade:** uma linha por versão (`sequencia_local`) de cada equipamento, com a janela de vigência. Base para resolver o nome e a coordenada do equipamento "na data" do evento.
- **Granularidade:** uma linha por `id_local` + `sequencia_local` de `dbo.local`.
- **Colunas principais:** `id_local` (int), `sequencia_local` (tinyint), `vigencia_inicio` (datetime), `vigencia_fim` (datetime), `nome` (varchar 100), `tipo` (char 1), `localidade` (varchar 50), `lat`, `lon` (decimal).
- **Regras:** a primeira versão começa em 1900-01-01; as demais começam em `data_atualizacao`; `vigencia_fim` é a `data_atualizacao` da versão seguinte (`LEAD`) ou 9999-12-31; `lat`/`lon` vêm da própria versão e, se nulos, da versão anterior de mesmo nome.
- **Origem / tabelas finais:** `dbo.local`.
- **Datasets que a usam:** nenhum dataset próprio; é lida por `spu_carga_passagens_hora`, `spu_carga_infracoes_dia`, `v_disponibilidade_dia`, `v_preprocessamento` e pelos SQLs `gtw_infracoes`, `gtw_descartes` e `gtw_trafego_dia`.

### 2.2 `analise.v_local_vigente`

- **Finalidade:** versão atual de cada equipamento.
- **Granularidade:** uma linha por `id_local`.
- **Colunas principais:** `id_local`, `nome`, `tipo`, `localidade`, `lat`, `lon` (tipos como em `v_local_versao`).
- **Regras:** versão de maior `sequencia_local` (`ROW_NUMBER`); coordenada = a mais recente não nula em qualquer versão.
- **Origem / tabelas finais:** `dbo.local`.
- **Datasets:** nenhum diretamente; é lida por `v_equipamentos`.

### 2.3 `analise.v_equipamentos`

- **Finalidade:** cadastro de equipamentos para mapas e KPIs.
- **Granularidade:** uma linha por equipamento e área monitorada vinculada (um equipamento com mais de uma área gera mais de uma linha; sem área, `area_monitorada` fica nulo).
- **Colunas principais:** `id_local` (int), `nome` (varchar 100), `lat`, `lon` (decimal), `tipo` (char 1), `ativo_24h` (int; 1 se houve passagem nas últimas 24 h), `area_monitorada` (nvarchar 200).
- **Regras:** `ativo_24h` consulta `muralha.veiculo_tempo_real` com `NOLOCK` (`EXISTS`, últimas 24 h); áreas com `deletado = 0`.
- **Origem:** `analise.v_local_vigente`, `muralha.veiculo_tempo_real`, `muralha.equipamentos_area_monitorada`, `muralha.area_monitorada`.
- **Tabelas finais:** `dbo.local`, `muralha.veiculo_tempo_real`, `muralha.equipamentos_area_monitorada`, `muralha.area_monitorada`.
- **Datasets:** declarativo `v_equipamentos` ([equipamentos.yaml](../../analise/superset/datasets/equipamentos.yaml)); virtual `gtw_equipamentos`.

### 2.4 `analise.v_areas_monitoradas_geojson`

- **Finalidade:** áreas monitoradas com polígono já convertido para `[[lng, lat], ...]`.
- **Granularidade:** uma linha por área monitorada não excluída.
- **Colunas principais:** `id` (int), `nome` (nvarchar 200), `dados_json` (nvarchar max; JSON original), `cor` (varchar 20), `poligono` (nvarchar max).
- **Regras:** `am.deletado = 0`; `cor` e `poligono` vêm de `mat_areas_monitoradas` (`LEFT JOIN`, nulos se a carga não processou a área).
- **Origem:** `muralha.area_monitorada`, `analise.mat_areas_monitoradas`.
- **Tabelas finais:** `muralha.area_monitorada` (diretamente e pela materializada).
- **Datasets:** declarativo `v_areas_monitoradas_geojson` ([areas_monitoradas.yaml](../../analise/superset/datasets/areas_monitoradas.yaml)); virtual `gtw_areas_monitoradas`.

### 2.5 `analise.v_alertas`

- **Finalidade:** alertas de veículos de interesse, sem placa nem dados pessoais (LGPD).
- **Granularidade:** uma linha por alerta (`muralha.alerta`); tipos com mais de uma linha em `tipo_alerta_config` duplicariam o alerta (não verificado se ocorre).
- **Colunas principais:** `dt_alerta` (datetime), `tipo` (varchar 40), `tipo_descricao` (varchar 200), `critico` (bit), `prioridade` (int; padrão 50), `status` (varchar 20), `origem` (varchar 200), `descartado` (int 0/1, se há motivo de descarte), `motivo_descarte` (varchar 40), `data_descarte` (datetime), `tratado` (int 0/1, se há usuário responsável), `usuario_responsavel` (char 25), `observacao` (varchar 200).
- **Origem / tabelas finais:** `muralha.alerta`, `muralha.tipo_alerta_ocorrencia`, `muralha.status_alerta`, `muralha.motivo_descarte`, `muralha.tipo_alerta_config`, `dbo.sis_usuario` (todas tabelas físicas).
- **Datasets:** declarativo `v_alertas` ([alertas.yaml](../../analise/superset/datasets/alertas.yaml)); virtual `gtw_alertas`; alerta agendado "Alertas críticos sem tratamento".

### 2.6 `analise.v_assertividade_dia`

- **Finalidade:** resultado da dupla análise por dia.
- **Granularidade:** uma linha por dia (`dt_analise`).
- **Colunas principais:** `dia` (date), `total_analisadas` (int; `COUNT DISTINCT id_infracao`), `pre_aprovadas`, `reprovadas`, `desempate` (int; contagem por `status_analise`), `pct_assertividade` (decimal 5,2 = (PRE_APROVADA + REPROVADA) / analisadas).
- **Regras:** apenas `ia.sequencia = 1`.
- **Origem / tabelas finais:** `muralha.infracao_analise`, `muralha.vtr_status_analise`.
- **Datasets:** declarativo `v_assertividade_dia` ([assertividade_dia.yaml](../../analise/superset/datasets/assertividade_dia.yaml)); virtual `gtw_assertividade`; alerta "Assertividade abaixo de 80%".

### 2.7 `analise.v_disponibilidade_dia`

- **Finalidade:** disponibilidade diária por equipamento.
- **Granularidade:** uma linha por dia e equipamento (nome), para todos os dias do sistema entre a primeira e a última captura do equipamento (dias sem captura contam como 0 hora).
- **Colunas principais:** `dia` (date), `id_local` (int), `local_nome` (varchar 100), `verificacoes` (fixo 24), `verificacoes_ok` (horas distintas com captura), `pct_disponibilidade` (decimal 5,2 = horas/24), `max_minutos_offline` ((24 − horas) × 60).
- **Regras:** agrega primeiro `dbo.veiculo_sumarizado` por dia/equipamento/versão (cerca de 4,1 milhões de linhas) e só então resolve o nome; num dia com duas versões, vale o maior número de horas.
- **Origem:** `dbo.veiculo_sumarizado`, `analise.v_local_versao`.
- **Tabelas finais:** `dbo.veiculo_sumarizado`, `dbo.local`.
- **Datasets:** declarativo `v_disponibilidade_dia` ([disponibilidade_dia.yaml](../../analise/superset/datasets/disponibilidade_dia.yaml)); virtual `gtw_disponibilidade`; alerta "Disponibilidade abaixo de 95%".

### 2.8 `analise.v_preprocessamento`

- **Finalidade:** indicadores de pré-processamento de infrações (14 alíneas do TR, conforme o YAML).
- **Granularidade:** uma linha por dia de entrada (`data_entrada`), equipamento, operador (`id_usuario_final`).
- **Colunas principais:** `dia` (date), `id_local`, `local_nome`, `id_operador`, `operador` (varchar 25; "Processamento automático" se sem usuário), `recebidos`, `preprocessados`, `disponibilizados`, `descartados`, `pct_aproveitamento` (disponibilizados / recebidos), `tempo_medio_min`, `produtividade` (igual a `recebidos`), `divergencias` e `retrabalho` (sempre nulos, ainda não implementados), `pendentes`, `backlog_24h`, `pct_sla_72h`.
- **Regras:** exclui imagens de teste (`id_enquadramento <> 1`); nome do equipamento pela versão da própria infração (`id_local` + `sequencia_local`); `descartados` = infrações com `id_inconsistencia` diferente de zero.
- **Origem:** `dbo.infracao`, `analise.v_local_versao`, `dbo.sis_usuario`.
- **Tabelas finais:** `dbo.infracao`, `dbo.local`, `dbo.sis_usuario`.
- **Datasets:** declarativo `v_preprocessamento` ([preprocessamento.yaml](../../analise/superset/datasets/preprocessamento.yaml)); virtual `gtw_preprocessamento`.

### 2.9 `analise.v_sla_latencia_dia`

- **Finalidade:** SLA de latência por dia (janelas avaliadas pelo job de monitoramento).
- **Granularidade:** uma linha por dia (`dt_alerta`).
- **Colunas principais:** `dia`, `janelas_avaliadas`, `janelas_ok`, `janelas_violacao`, `pct_cumprimento` (decimal 5,2), `media_p95_ms`, `threshold_ms`, `total_passagens`.
- **Origem / tabelas finais:** `muralha.alerta_sla`.
- **Datasets:** declarativo `v_sla_latencia_dia` ([sla_latencia_dia.yaml](../../analise/superset/datasets/sla_latencia_dia.yaml)); virtual `gtw_sla_latencia`; alerta "SLA de latência abaixo de 95%".

### 2.10 `analise.v_sla_preproc_dia`

- **Finalidade:** SLA diário de pré-processamento, a partir de snapshots de pendências.
- **Granularidade:** uma linha por dia (`dt_alerta`).
- **Colunas principais:** `dia`, `snapshots`, `media_pendentes`, `max_acima_72h`, `max_acima_48h`, `max_acima_24h`, `pct_dentro_sla` (decimal 5,2 = 100 × (1 − média acima de 72 h / média de pendentes); 100 se sem pendentes).
- **Origem / tabelas finais:** `muralha.alerta_sla_preproc`.
- **Datasets:** declarativo `v_sla_preproc_dia` ([sla_preproc_dia.yaml](../../analise/superset/datasets/sla_preproc_dia.yaml)); não há dataset virtual nem gráfico que a use.

### 2.11 `analise.v_passagens_geolocalizado`

- **Finalidade:** passagens por hora e equipamento com coordenadas, para mapas.
- **Granularidade:** uma linha por hora, equipamento e coordenada.
- **Colunas principais:** `hora` (datetime2), `local_nome`, `lat`, `lon` (float), `total_passagens` (soma), `velocidade_media` (decimal 6,2 = soma das velocidades / passagens).
- **Regras:** só linhas com `lat` e `lon` não nulos.
- **Origem:** `analise.mat_passagens_hora`.
- **Tabelas finais:** `muralha.veiculo_tempo_real` e `dbo.local` (via carga de `mat_passagens_hora`).
- **Datasets:** declarativo `v_passagens_geolocalizado` ([passagens_geolocalizado.yaml](../../analise/superset/datasets/passagens_geolocalizado.yaml)); virtual `gtw_passagens_geo`.

### 2.12 `analise.v_infracoes_geolocalizado`

- **Finalidade:** infrações por dia e equipamento com coordenadas, para mapas.
- **Granularidade:** uma linha por linha de `mat_infracoes_dia` com coordenadas (dia, equipamento, faixa, enquadramento).
- **Colunas principais:** `dia` (date), `local_nome`, `lat`, `lon` (float), `enquadramento`, `total_infracoes` (int), `excesso_medio` (decimal).
- **Regras:** só linhas com `lat` e `lon` não nulos.
- **Origem:** `analise.mat_infracoes_dia`.
- **Tabelas finais:** `dbo.infracao`, `dbo.enquadramento` e `dbo.local` (via carga de `mat_infracoes_dia`).
- **Datasets:** declarativo `v_infracoes_geolocalizado` ([infracoes_geolocalizado.yaml](../../analise/superset/datasets/infracoes_geolocalizado.yaml)); virtual `gtw_infracoes_geo`.

---

## 3. Tabelas materializadas e procedures de carga

Execução: [`refresh-materialized.sh`](../../analise/scripts/refresh-materialized.sh) chama, em sequência, `spu_carga_passagens_hora`, `spu_carga_infracoes_dia` e `spu_carga_areas_monitoradas` com `sqlcmd`. O script sugere agendamento no cron a cada 30 minutos (`*/30 * * * *`); a existência efetiva desse agendamento não foi verificada. Cada procedure grava uma linha em `analise.carga_log` (também em caso de erro, capturado por `TRY/CATCH`).

| Tabela materializada | Procedure | Origens | Janela / estratégia | Chave de granularidade |
|---|---|---|---|---|
| `analise.mat_passagens_hora` (11 colunas) | `analise.spu_carga_passagens_hora(@horas=25, @dt_inicio, @dt_fim)` | `muralha.veiculo_tempo_real` (com `NOLOCK`), `analise.v_local_versao` (→ `dbo.local`) | Padrão: últimas 25 horas truncadas à hora, até a próxima hora; apaga a janela e reinsere; parâmetros permitem recarga de período | hora, `id_local`, faixa (`id_pista`), classificação, placa Mercosul |
| `analise.mat_infracoes_dia` (11 colunas) | `analise.spu_carga_infracoes_dia(@dias=7, @dt_inicio, @dt_fim)` | `dbo.infracao`, `dbo.enquadramento`, `analise.v_local_versao` (→ `dbo.local`) | Padrão: últimos 7 dias até amanhã; apaga a janela e reinsere | dia, `id_local`, faixa (`pista`), `id_enquadramento` |
| `analise.mat_areas_monitoradas` (5 colunas) | `analise.spu_carga_areas_monitoradas` | `muralha.area_monitorada` (apenas `deletado = 0`) | Recarga total (apaga tudo e reinsere); converte `dados_json` em polígono `[[lng, lat], ...]` via XML (nível de compatibilidade 110, sem `OPENJSON`) | `id` da área |

Detalhes relevantes:

- **Nome e coordenada** do equipamento são gravados na materializada pela versão vigente no momento do evento (`v_local_versao`); sem correspondência, o nome vira "Equipamento N".
- `mat_passagens_hora`: `velocidade_media` é coluna decimal além de `soma_velocidade`; as métricas dos datasets recalculam a média ponderada por `soma_velocidade / passagens`.
- `mat_infracoes_dia`: `excesso_medio` = média de (velocidade considerada − limite) apenas nas infrações com excesso (nulo quando não há), `limite` = máximo do limite; **inclui** o enquadramento 1 (imagem de teste), que os datasets virtuais filtram.

### 3.1 `analise.carga_log`

| Coluna | Tipo | Significado |
|---|---|---|
| `id` | bigint | Identificador |
| `processo` | varchar(100) | Nome da procedure |
| `dt_inicio` | datetime2 | Início da execução |
| `dt_fim` | datetime2 | Fim da execução |
| `linhas` | int | Linhas inseridas |
| `erro` | varchar(2000) | Mensagem de erro (nulo se sucesso) |
| `sucesso` | bit | 1 = sucesso |

Situação em 2026-10-19 (consulta ao log):

| Processo | Execuções | Falhas | Última execução registrada |
|---|---|---|---|
| `spu_carga_passagens_hora` | 53 | 0 | 2026-10-17 22:39 |
| `spu_carga_infracoes_dia` | 4 | 0 | 2026-10-17 22:39 |
| `spu_carga_areas_monitoradas` | 1 | 0 | 2026-10-17 22:24 |

Não há registro de carga posterior a 2026-10-17 22:39, ou seja, nenhum agendamento periódico estava gravando no log neste ambiente de desenvolvimento.

---

## 4. Datasets do Superset

No Superset em execução há 29 datasets (`tables`): 13 físicos (declarativos, a partir dos YAML) e 16 virtuais (`gtw_*`), além de 14 dashboards. Os 13 datasets físicos não têm nenhum gráfico associado; todos os gráficos dos dashboards usam os datasets virtuais.

### 4.1 Declarativos (YAML em `analise/superset/datasets/`)

Todos no schema `analise`, exceto o último. "Cache" = `cache_timeout` em segundos. Métricas: nome e expressão (resumida quando longa).

| Dataset | Objeto físico | Coluna temporal | Cache (s) | Métricas (nome: expressão) | RLS |
|---|---|---|---|---|---|
| [alertas](../../analise/superset/datasets/alertas.yaml) | `v_alertas` | `dt_alerta` (também `data_descarte`) | 60 | `total_alertas`: `COUNT(*)`; `alertas_criticos`: `SUM(CAST(critico AS INT))`; `pct_descartados`: `100.0 * SUM(descartado) / NULLIF(COUNT(*), 0)`; `pct_tratados`: idem com `tratado`; `alertas_pendentes`: `SUM(CASE WHEN tratado = 0 AND descartado = 0 THEN 1 ELSE 0 END)` | sim |
| [areas_monitoradas](../../analise/superset/datasets/areas_monitoradas.yaml) | `v_areas_monitoradas_geojson` | nenhuma | 600 | `total_areas`: `COUNT(*)` | não |
| [assertividade_dia](../../analise/superset/datasets/assertividade_dia.yaml) | `v_assertividade_dia` | `dia` | 60 | `assertividade_media`: `100.0 * SUM(pre_aprovadas + reprovadas) / NULLIF(SUM(total_analisadas), 0)`; `total_analisadas_sum`: `SUM(total_analisadas)`; `total_desempates`: `SUM(desempate)`; `pct_desempate`: `100.0 * SUM(desempate) / NULLIF(SUM(total_analisadas), 0)` | não |
| [disponibilidade_dia](../../analise/superset/datasets/disponibilidade_dia.yaml) | `v_disponibilidade_dia` | `dia` | 60 | `media_disponibilidade`: `AVG(pct_disponibilidade)`; `total_verificacoes`: `SUM(verificacoes)`; `total_verificacoes_ok`: `SUM(verificacoes_ok)`; `pct_disponibilidade_global`: `100.0 * SUM(verificacoes_ok) / NULLIF(SUM(verificacoes), 0)` | sim |
| [equipamentos](../../analise/superset/datasets/equipamentos.yaml) | `v_equipamentos` | nenhuma | 300 | `total_equipamentos`: `COUNT(*)`; `equipamentos_ativos`: `SUM(CAST(ativo_24h AS INT))`; `pct_ativos`: `100.0 * SUM(CAST(ativo_24h AS INT)) / NULLIF(COUNT(*), 0)` | não |
| [infracoes_dia](../../analise/superset/datasets/infracoes_dia.yaml) | `mat_infracoes_dia` | `dia` | 60 | `total_infracoes`: `SUM(total)`; `excesso_medio_ponderado`: `SUM(excesso_medio * total) / NULLIF(SUM(CASE WHEN excesso_medio IS NOT NULL THEN total ELSE 0 END), 0)`; `qtd_enquadramentos`: `COUNT(DISTINCT id_enquadramento)`; `qtd_equipamentos_infracoes`: `COUNT(DISTINCT id_local)` | sim |
| [infracoes_geolocalizado](../../analise/superset/datasets/infracoes_geolocalizado.yaml) | `v_infracoes_geolocalizado` | `dia` | 300 | `total_infracoes_geo`: `SUM(total_infracoes)`; `excesso_medio_geo`: `AVG(excesso_medio)` | não |
| [passagens_geolocalizado](../../analise/superset/datasets/passagens_geolocalizado.yaml) | `v_passagens_geolocalizado` | `hora` | 300 | `total_passagens_geo`: `SUM(total_passagens)`; `velocidade_media_geo`: `AVG(velocidade_media)` | não |
| [passagens_hora](../../analise/superset/datasets/passagens_hora.yaml) | `mat_passagens_hora` | `hora` | 60 | `total_passagens`: `SUM(passagens)`; `velocidade_media_ponderada`: `CAST(SUM(soma_velocidade) AS FLOAT) / NULLIF(SUM(passagens), 0)`; `passagens_por_faixa`: `CAST(SUM(passagens) AS FLOAT) / NULLIF(COUNT(DISTINCT faixa), 0)`; `qtd_equipamentos`: `COUNT(DISTINCT id_local)`; `pct_mercosul`: `100.0 * SUM(CASE WHEN placa_mercosul = 1 THEN passagens ELSE 0 END) / NULLIF(SUM(passagens), 0)` | sim |
| [preprocessamento](../../analise/superset/datasets/preprocessamento.yaml) | `v_preprocessamento` | `dia` | 60 | `total_recebidos`: `SUM(recebidos)`; `total_preprocessados`: `SUM(preprocessados)`; `total_descartados`: `SUM(descartados)`; `aproveitamento_global`: `100.0 * (SUM(recebidos) - SUM(descartados)) / NULLIF(SUM(recebidos), 0)`; `tempo_medio_global`: `AVG(tempo_medio_min)`; `produtividade_media`: `AVG(produtividade)`; `sla_72h_global`: `100.0 * SUM(CAST(pct_sla_72h * recebidos / 100.0 AS INT)) / NULLIF(SUM(recebidos), 0)`; `total_pendentes`: `SUM(pendentes)`; `total_backlog`: `SUM(backlog_24h)` | não |
| [sla_latencia_dia](../../analise/superset/datasets/sla_latencia_dia.yaml) | `v_sla_latencia_dia` | `dia` | 60 | `pct_sla_cumprido`: `100.0 * SUM(janelas_ok) / NULLIF(SUM(janelas_avaliadas), 0)`; `media_p95`: `AVG(media_p95_ms)`; `total_violacoes`: `SUM(janelas_violacao)` | não |
| [sla_preproc_dia](../../analise/superset/datasets/sla_preproc_dia.yaml) | `v_sla_preproc_dia` | `dia` | 60 | `sla_preproc_medio`: `AVG(pct_dentro_sla)`; `media_pendentes_global`: `AVG(media_pendentes)`; `pior_backlog_72h`: `MAX(max_acima_72h)` | não |
| [veiculo_tempo_real_admin](../../analise/superset/datasets/veiculo_tempo_real_admin.yaml) | `muralha.veiculo_tempo_real` (tabela bruta, 8,19 milhões de linhas em 2026-10-19) | `data` | 0 (sem cache) | `contagem`: `COUNT(*)`; `velocidade_media_bruta`: `AVG(CAST(velocidade AS FLOAT))` | não (uso restrito ao papel Admin, conforme o YAML) |

Regra de RLS (dataset marcado "sim"): ver [§6](#6-observações-técnicas).

### 4.2 Virtuais dos painéis (`gtw_*`)

Definidos em [`fontes.py`](../../analise/superset/paineis/fontes.py) (rótulos, métricas, cache, coluna temporal) e no SQL de [`paineis/datasets/`](../../analise/superset/paineis/datasets/). São SELECTs guardados no Superset, somente leitura, usados por [`operacionais.py`](../../analise/superset/paineis/operacionais.py) (dashboards operacionais) e [`demonstracao.py`](../../analise/superset/paineis/demonstracao.py) (dashboards `demo-*`). O arquivo [`construtor.py`](../../analise/superset/paineis/construtor.py) apenas os provisiona. Colunas "Gráficos" = quantidade de gráficos no Superset em execução.

| Dataset | SQL resumido | Origem (views/tabelas) | Cache (s) | Métricas | Gráficos | Dashboards (slug) |
|---|---|---|---|---|---|---|
| `gtw_passagens_hora` | Passagens por hora com hora do dia, dia da semana, faixa, classificação (`dbo.classe_veiculo`) e tipo de placa | `analise.mat_passagens_hora`, `dbo.classe_veiculo` | 3600 | passagens, velocidade_media, pct_mercosul, equipamentos | 21 | `kpis-operacionais`, `passagens-veiculares`, `demo-fluxo-veicular` |
| `gtw_infracoes` | Infrações em nível de registro, com tipo/categoria por enquadramento, excesso (km/h) e faixa de excesso; exclui `id_enquadramento = 1` | `dbo.infracao`, `analise.v_local_versao`, `dbo.enquadramento` | 1800 | infracoes, excesso_medio, pct_velocidade, equipamentos | 11 | `demo-infracoes` |
| `gtw_infracoes_dia` | Infrações por dia, equipamento, faixa e enquadramento; categoria; exclui enquadramento 1 | `analise.mat_infracoes_dia` | 1800 | infracoes, excesso_medio (ponderado), pct_velocidade, equipamentos | 11 | `kpis-operacionais`, `infracoes`, `mapa-equipamentos-infracoes` |
| `gtw_trafego_dia` | Tráfego diário por equipamento (versão do registro) e classe; soma ponderada de velocidade; placas lidas | `dbo.veiculo_sumarizado`, `analise.v_local_versao`, `dbo.classe_veiculo` | 3600 | volume, media_diaria, velocidade_media, taxa_leitura, equipamentos | 10 | `demo-trafego-historico` |
| `gtw_faixa_velocidade` | Tráfego mensal por faixa de velocidade (faixas de 10 km/h, acima de 100) e classe | `dbo.veiculo_sumarizado`, `dbo.classe_veiculo` | 3600 | volume | 1 | `demo-trafego-historico` |
| `gtw_waze_alertas` | Alertas do Waze em nível de registro, com tipo e subtipo traduzidos | `mobilidade.alertas` | 900 | alertas, acidentes, congestionamentos, interdicoes | 11 | `demo-mobilidade-waze` |
| `gtw_alertas` | Alertas com hora do dia, dia da semana, criticidade, status e responsável | `analise.v_alertas` | 900 | alertas, criticos, pct_tratados, descartados, pendentes | 20 | `kpis-operacionais`, `alertas`, `demo-alertas-veiculos` |
| `gtw_sla_latencia` | Colunas da view, com `dia` convertido para datetime | `analise.v_sla_latencia_dia` | 600 | pct_sla, p95_medio, violacoes, janelas_ok, limite_ms | 8 | `kpis-operacionais`, `sla-latencia` |
| `gtw_assertividade` | Colunas da view, `dia` como datetime | `analise.v_assertividade_dia` | 600 | assertividade, analisadas, aprovadas, reprovadas, desempates | 7 | `kpis-operacionais`, `assertividade-dupla-analise` |
| `gtw_disponibilidade` | Renomeia `local_nome` para `equipamento`, `verificacoes` para `horas_monitoradas` etc. | `analise.v_disponibilidade_dia` | 3600 | disponibilidade, horas_monitoradas, horas_captura, dias_sem_captura, dias_operacao, equipamentos | 9 | `kpis-operacionais`, `disponibilidade-equipamentos` |
| `gtw_preprocessamento` | Subconjunto de colunas da view | `analise.v_preprocessamento` | 1800 | recebidas, disponibilizadas, descartadas, pendentes, aproveitamento, sla_72h, horas_medias | 8 | `preprocessamento-imagens` |
| `gtw_descartes` | Infrações descartadas (`id_inconsistencia` diferente de zero) com o motivo; exclui enquadramento 1 | `dbo.infracao`, `analise.v_local_versao`, `dbo.inconsistencia` | 1800 | descartes | 1 | `preprocessamento-imagens` |
| `gtw_equipamentos` | Equipamentos com coordenadas não nulas | `analise.v_equipamentos` | 3600 | equipamentos | 1 | `mapa-equipamentos-infracoes` |
| `gtw_areas_monitoradas` | Áreas com polígono não nulo | `analise.v_areas_monitoradas_geojson` | 3600 | areas | 1 | `mapa-equipamentos-infracoes` |
| `gtw_passagens_geo` | Colunas da view | `analise.v_passagens_geolocalizado` | 3600 | passagens | 1 | `mapa-equipamentos-infracoes` |
| `gtw_infracoes_geo` | Colunas da view, filtrando `enquadramento <> 'Imagem teste'` | `analise.v_infracoes_geolocalizado` | 1800 | infracoes | 1 | `mapa-equipamentos-infracoes` |

Dashboards (slug: título): `kpis-operacionais` (KPIs Operacionais, 10 gráficos), `assertividade-dupla-analise` (6), `sla-latencia` (7), `disponibilidade-equipamentos` (7), `preprocessamento-imagens` (9), `alertas` (8), `infracoes` (8), `passagens-veiculares` (8), `mapa-equipamentos-infracoes` (5), `demo-fluxo-veicular` (11), `demo-infracoes` (11), `demo-trafego-historico` (11), `demo-mobilidade-waze` (11) e `demo-alertas-veiculos` (10). Os relatórios agendados em [`alertas/`](../../analise/superset/alertas/) enviam e-mail dos dashboards `kpis-operacionais` (resumo diário), `infracoes` (resumo semanal) e `sla-latencia` (resumo semanal de SLA; o YAML cita também o painel de disponibilidade).

### 4.3 Alertas agendados (consultas diretas ao banco)

| Alerta | Consulta sobre | Condição |
|---|---|---|
| Alertas críticos sem tratamento | `analise.v_alertas` (`critico = 1`) | valor > 0 |
| Assertividade abaixo de 80% | `analise.v_assertividade_dia` (últimas 24 h) | valor < 80 |
| Disponibilidade abaixo de 95% | `analise.v_disponibilidade_dia` (últimas 24 h) | valor < 95 |
| SLA de latência abaixo de 95% | `analise.v_sla_latencia_dia` (últimas 24 h) | valor < 95 |

Todos usam a conexão "GTW Muralha Digital (somente leitura)".

---

## 5. Matriz de rastreabilidade

Tabelas de origem agrupadas; "Views/materializadas" lista os objetos `analise` que as leem (diretamente ou pela cadeia). Dashboards pelo slug; `demo-*` são os de demonstração.

| Tabela de origem (dbo/muralha) | Views/materializadas analise | Datasets | Dashboards |
|---|---|---|---|
| `dbo.local` | `v_local_versao`, `v_local_vigente`, `mat_passagens_hora`, `mat_infracoes_dia`, `v_equipamentos`, `v_disponibilidade_dia`, `v_preprocessamento`, `v_passagens_geolocalizado`, `v_infracoes_geolocalizado` | `gtw_passagens_hora`, `gtw_infracoes_dia`, `gtw_infracoes`, `gtw_trafego_dia`, `gtw_descartes`, `gtw_equipamentos`, `gtw_disponibilidade`, `gtw_preprocessamento`, `gtw_passagens_geo`, `gtw_infracoes_geo` (e os declarativos das mesmas views) | `kpis-operacionais`, `passagens-veiculares`, `infracoes`, `disponibilidade-equipamentos`, `preprocessamento-imagens`, `mapa-equipamentos-infracoes`, `demo-fluxo-veicular`, `demo-infracoes`, `demo-trafego-historico` |
| `muralha.veiculo_tempo_real` | `mat_passagens_hora`, `v_passagens_geolocalizado`, `v_equipamentos` (flag `ativo_24h`) | `gtw_passagens_hora`, `gtw_passagens_geo`, `gtw_equipamentos`, `veiculo_tempo_real` (admin) | `kpis-operacionais`, `passagens-veiculares`, `demo-fluxo-veicular`, `mapa-equipamentos-infracoes` |
| `dbo.infracao` | `mat_infracoes_dia`, `v_infracoes_geolocalizado`, `v_preprocessamento` | `gtw_infracoes_dia`, `gtw_infracoes`, `gtw_infracoes_geo`, `gtw_preprocessamento`, `gtw_descartes` | `kpis-operacionais`, `infracoes`, `mapa-equipamentos-infracoes`, `demo-infracoes`, `preprocessamento-imagens` |
| `dbo.enquadramento` | `mat_infracoes_dia`, `v_infracoes_geolocalizado` | `gtw_infracoes_dia`, `gtw_infracoes`, `gtw_infracoes_geo` | `kpis-operacionais`, `infracoes`, `mapa-equipamentos-infracoes`, `demo-infracoes` |
| `dbo.inconsistencia` | nenhuma (lida direto pelo SQL do dataset) | `gtw_descartes` | `preprocessamento-imagens` |
| `dbo.classe_veiculo` | nenhuma (lida direto pelos SQLs dos datasets) | `gtw_passagens_hora`, `gtw_trafego_dia`, `gtw_faixa_velocidade` | `kpis-operacionais`, `passagens-veiculares`, `demo-fluxo-veicular`, `demo-trafego-historico` |
| `dbo.veiculo_sumarizado` | `v_disponibilidade_dia` | `gtw_disponibilidade`; `gtw_trafego_dia` e `gtw_faixa_velocidade` (leitura direta) | `kpis-operacionais`, `disponibilidade-equipamentos`, `demo-trafego-historico` |
| `dbo.sis_usuario` | `v_alertas`, `v_preprocessamento` | `gtw_alertas`, `gtw_preprocessamento` | `kpis-operacionais`, `alertas`, `demo-alertas-veiculos`, `preprocessamento-imagens` |
| `muralha.alerta` | `v_alertas` | `gtw_alertas` | `kpis-operacionais`, `alertas`, `demo-alertas-veiculos` |
| `muralha.tipo_alerta_ocorrencia`, `muralha.tipo_alerta_config`, `muralha.status_alerta`, `muralha.motivo_descarte` | `v_alertas` | `gtw_alertas` | `kpis-operacionais`, `alertas`, `demo-alertas-veiculos` |
| `muralha.infracao_analise`, `muralha.vtr_status_analise` | `v_assertividade_dia` | `gtw_assertividade` | `kpis-operacionais`, `assertividade-dupla-analise` |
| `muralha.alerta_sla` | `v_sla_latencia_dia` | `gtw_sla_latencia` | `kpis-operacionais`, `sla-latencia` |
| `muralha.alerta_sla_preproc` | `v_sla_preproc_dia` | nenhum dataset virtual (somente o declarativo `v_sla_preproc_dia`) | não identificado |
| `muralha.area_monitorada` | `mat_areas_monitoradas`, `v_areas_monitoradas_geojson`, `v_equipamentos` | `gtw_areas_monitoradas`, `gtw_equipamentos` | `mapa-equipamentos-infracoes` |
| `muralha.equipamentos_area_monitorada` | `v_equipamentos` | `gtw_equipamentos` | `mapa-equipamentos-infracoes` |
| `mobilidade.alertas` | nenhuma (lida direto pelo SQL do dataset) | `gtw_waze_alertas` | `demo-mobilidade-waze` |

---

## 6. Observações técnicas

**Versão vigente e versionamento do equipamento**

- `dbo.local` guarda uma linha por versão do equipamento (`sequencia_local`). `v_local_versao` resolve o nome e a coordenada na data do evento (vigência por `data_atualizacao`); `v_local_vigente` resolve a versão atual e é usada somente por `v_equipamentos`.
- `v_disponibilidade_dia` e `v_preprocessamento` resolvem o nome pela `sequencia_local` do próprio registro (`veiculo_sumarizado` / `infracao`); as cargas de `mat_passagens_hora` e `mat_infracoes_dia` usam a janela de vigência por data. Eventos sem versão correspondente aparecem como "Equipamento N".

**Tabelas grandes consultadas** (linhas ≈ em 2026-10-19)

- `muralha.veiculo_tempo_real`: 8.188.803 (carga horária de 25 h com `NOLOCK`; `v_equipamentos` faz `EXISTS` nas últimas 24 h; dataset admin lê a tabela bruta).
- `dbo.veiculo_sumarizado`: 4.151.335 (agregada por `v_disponibilidade_dia`, `gtw_trafego_dia` e `gtw_faixa_velocidade`, sem materialização; `gtw_trafego_dia` e `gtw_faixa_velocidade` têm cache de 3600 s).
- `dbo.infracao`: 74.883 (`v_preprocessamento` e os SQLs `gtw_infracoes` e `gtw_descartes` leem em nível de registro).
- Demais: `mobilidade.alertas` 17.545; `muralha.alerta` 11.555; `dbo.local` 470; `muralha.alerta_sla` 2.173; `muralha.alerta_sla_preproc` 227; `muralha.area_monitorada` 45; `muralha.infracao_analise` 60; `muralha.vtr_status_analise` 30.

**Somente leitura**

- Os datasets virtuais são SELECTs guardados no Superset; a conexão usada pelos alertas é "GTW Muralha Digital (somente leitura)". Apenas as três procedures `spu_carga_*` gravam, e somente em `analise.mat_*` e `analise.carga_log`.
- A definição das views e procedures está versionada nas migrações em [`docs/banco-de-dados/migracoes/`](../banco-de-dados/migracoes/); o banco `GTW_MURALHA_DEV` não deve receber DDL/DML fora de script de migração.

**Pontos de atenção encontrados (fatos verificados em 2026-10-19)**

1. **RLS do papel Operador aponta para objeto e coluna inexistentes.** A regra "Restringir por região do operador" ([rls.yaml](../../analise/superset/governanca/rls.yaml), idêntica no Superset em execução) usa a coluna `area_monitorada_id` e a view `analise.v_usuario_area_monitorada`. Nenhum objeto com esse nome existe no banco, e `mat_passagens_hora`, `mat_infracoes_dia`, `v_alertas` e `v_disponibilidade_dia` (datasets físicos aos quais a regra se aplica) não têm a coluna `area_monitorada_id`. A regra só atinge os datasets físicos; os virtuais `gtw_*`, usados pelos gráficos, não estão associados a ela no Superset.
2. **Datasets físicos sem uso em gráficos.** Os 13 datasets declarativos existem no Superset, mas nenhum gráfico os usa; os dashboards leem apenas os `gtw_*`. Os declarativos servem à exploração livre (SQL Lab/charts novos) e ao RLS.
3. **`v_sla_preproc_dia` sem dataset virtual nem gráfico** e **`v_local_vigente`/`v_local_versao` sem dataset**: consumidas apenas por outros objetos. O alerta e o dashboard de SLA de pré-processamento não foram identificados.
4. **Cargas sem execução recente.** O `carga_log` não registra execuções depois de 2026-10-17 22:39; os dados de `mat_*` refletem aquela data até que o agendamento seja ativado (ver `refresh-materialized.sh`).
5. **`v_preprocessamento`: colunas `divergencias` e `retrabalho` sempre nulas**, e `produtividade` repete `recebidos`. A métrica declarativa `aproveitamento_global` usa (recebidos − descartados) / recebidos, enquanto a coluna `pct_aproveitamento` da view usa disponibilizados / recebidos; as métricas do dataset virtual `gtw_preprocessamento` usam disponibilizadas / recebidas.
6. **Filtro de imagens de teste por critérios diferentes.** `gtw_infracoes_dia` e `gtw_infracoes` filtram `id_enquadramento <> 1`; `gtw_infracoes_geo` filtra pela descrição `'Imagem teste'`, e `v_infracoes_geolocalizado` não filtra. Não foi verificado se a descrição do enquadramento 1 em `dbo.enquadramento` é exatamente esse texto.
7. **Relatório semanal de SLA** cita no texto o painel de disponibilidade, mas o YAML referencia apenas o dashboard `sla-latencia`.
8. **Sem evidência sobre o agendamento cron** do `refresh-materialized.sh` neste ambiente: não identificado.
