# Gap — Camada `analise` dos painéis do Superset

> **Data:** 2026-10-17
> **Status:** ✅ Resolvido em 2026-10-17. A camada `analise` foi corrigida no DEV, e os 14 painéis (9 operacionais e 5 de demonstração) passaram a usá-la como fonte, com 122 gráficos validados.
> **Pendências de desenho** (fora do escopo dos painéis): ver o fim deste documento.

## Origem

- [Plano 23 — Análises com Apache Superset](../planos-salvador/23-analises-superset.md): SUP-2 (camada de dados `analise`), SUP-3 (datasets), SUP-4 (painéis operacionais), SUP-7 (alertas) e SUP-8 (mapas).
- Entrega relacionada: [23-analises-superset.md](23-analises-superset.md).

## Problema encontrado

Na tela de Análises do GTW, vários dos 9 **Painéis operacionais** apareciam vazios ou errados. Causas:

| Objeto | Defeito |
|---|---|
| `analise.spu_carga_passagens_hora` / `spu_carga_infracoes_dia` | Janela fixa de 25 h / 7 dias relativa à data atual (a última passagem do DEV é de 2026-10-01); as cargas gravavam 0 linhas. |
| `analise/scripts/refresh-materialized.sh` | Chamava `sp_carga_*`, mas as procedures se chamam `spu_carga_*`; a recarga agendada nunca funcionou. |
| Nome e coordenadas do equipamento | Vinham da versão `sequencia_local = 1` (quase sempre sem lat/lon). O `id_local` é reaproveitado em outro local ao longo do tempo (o 1 passou de Sete Lagoas para o PR, o 38 de Sete Lagoas para um teste no RJ), então o histórico ficava com o rótulo errado. |
| `analise.v_disponibilidade_dia` | Dependia de `muralha.equipamento_disponibilidade`, vazia no DEV. |
| `analise.v_preprocessamento` | Dependia de `muralha.infracao_analise` (60 registros de teste, sem correspondência no `JOIN`). |
| `analise.v_areas_monitoradas_geojson` | `dados_json` não é GeoJSON e o banco (compatibilidade 110) não tem `OPENJSON`. |
| `20261017_analise_mapas_geo.sql` | Views geolocalizadas com colunas e tabela inexistentes; nunca rodou. |
| YAMLs de `superset/dashboards/` | `form_data` gerado sem agrupamentos (pizzas e rankings com uma linha só). |
| Alertas SUP-7 | "Alertas críticos sem tratamento" usava a coluna inexistente `severidade` e o status `pendente` (o gravado é `PENDENTE`); "SLA de latência" usava a coluna inexistente `dentro_sla`. |

## Solução aplicada

### Banco (somente objetos do schema `analise`; nenhuma tabela legada alterada)

| Migração | Conteúdo |
|---|---|
| [`20261017_analise_correcao_camada.sql`](../banco-de-dados/migracoes/20261017_analise_correcao_camada.sql) | Cargas com janela parametrizável (`@horas`/`@dias` ou `@dt_inicio`/`@dt_fim`); `v_equipamentos` com coordenadas atuais (`v_local_vigente`); `v_disponibilidade_dia` sobre horas com captura (`dbo.veiculo_sumarizado`), incluindo os dias sem captura; `v_preprocessamento` sobre o processamento de `dbo.infracao`; `mat_areas_monitoradas` + `spu_carga_areas_monitoradas`, que converte as áreas em polígono `[[lng, lat], …]`; views geolocalizadas; carga inicial. |
| [`20261017_analise_versao_equipamento.sql`](../banco-de-dados/migracoes/20261017_analise_versao_equipamento.sql) | `v_local_versao`: cada versão do equipamento com vigência `[início, fim)`. Cargas, disponibilidade e pré-processamento passam a usar a versão vigente na data do registro. Recarga completa. |

Resultado no DEV:

| Objeto | Conteúdo |
|---|---|
| `mat_passagens_hora` | 4,05 milhões de passagens desde 2025-06 |
| `mat_infracoes_dia` | 74,9 mil infrações |
| `v_disponibilidade_dia` | 41 equipamentos/locais |
| `v_preprocessamento` | 74,3 mil infrações e 762 descartes |
| Equipamentos com coordenada | 70 de 72 |
| Áreas monitoradas | 4 polígonos |

### Superset

- `analise/superset/paineis/` (substitui a antiga pasta `superset/demo/` e os YAMLs de `superset/dashboards/`, que foram removidos):
  - `construtor.py`: form_data do Superset 6, filtros nativos, filtro cruzado, mapas OSM (pontos, grade e polígonos), validação da consulta de cada gráfico e remoção dos datasets e gráficos antigos.
  - `fontes.py` + `datasets/gtw_*.sql`: 16 datasets. Fonte: camada `analise` (`mat_passagens_hora`, `mat_infracoes_dia`, `v_disponibilidade_dia`, `v_preprocessamento`, `v_sla_latencia_dia`, `v_assertividade_dia`, `v_alertas`, `v_equipamentos`, `v_areas_monitoradas_geojson`, views geolocalizadas). `dbo.*` entra só onde é preciso o nível de registro: infrações por hora, tráfego consolidado e motivos de descarte. O Waze vem de `mobilidade.alertas`.
  - `operacionais.py` (9 painéis, 68 gráficos) e `demonstracao.py` (5 painéis, 54 gráficos).
- Regra única para os dois conjuntos: entram todos os equipamentos, com o nome da versão vigente, e só ficam de fora as imagens de teste (enquadramento 1). Os totais coincidem entre os painéis (por exemplo, 74.260 infrações nos dois).
- O `provisionar.py` deixou de tratar dashboards (sem a flag `--dashboards-only`). O `deploy.sh` publica os painéis antes dos relatórios agendados.
- Os alertas SUP-7 foram corrigidos. A assertividade retorna 100 quando não há análises no período, para não disparar sem dados.

## Pendências de desenho

- `v_usuario_area_monitorada` (RLS por região, SUP-5) não foi criada, porque depende da tabela `analise.area_monitorada_grupo`, que não existe. Definir o vínculo grupo × área antes de ativar a RLS por região.
- `muralha.equipamento_disponibilidade` (job do Plano 18) continua vazia no DEV. A disponibilidade passou a ser medida pelas horas com captura; se o job for alimentado, avaliar a combinação das duas fontes.
- Assertividade e SLA de latência têm só 7 dias de dados no DEV (`muralha.infracao_analise` e o job de SLA). Os painéis refletem esse volume real.
