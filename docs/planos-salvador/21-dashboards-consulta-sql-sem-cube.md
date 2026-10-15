# Construtor de Dashboards — Camada de Consulta SQL Própria (remoção do Cube Core)

> **Status: Aprovado (2026-10-14)** — em execução.
>
> **Para agentes:** use `superpowers:executing-plans`. Execute as tarefas na ordem; cada uma termina com build verde (`..\.setup-gtw\build.ps1`) e testes TestNG passando.

**Goal:** Fazer o construtor de dashboards (schema `dash`) consultar o SQL Server diretamente pelo Java, eliminando o Cube Core. As consultas passam a ser montadas a partir do catálogo, com lista branca de identificadores e valores sempre parametrizados. Telas, catálogo, datasets, widgets e painéis já existentes são mantidos.

**Architecture:** O navegador continua enviando a consulta no mesmo formato JSON de hoje (`measures`, `dimensions`, `timeDimensions`, `filters`, `order`, `limit`) para `/api/dash/consultas`. O servlet resolve cada campo pelo `dash.dataset_campo` e pelo catálogo, monta um `SELECT ... GROUP BY` parametrizado, executa via JDBC (`Conexao.getConexao()`) com timeout e limite de linhas e devolve as linhas no mesmo formato que o front já renderiza. Não existe mais geração de YAML, JWT, Docker nem serviço externo.

**Tech Stack:** Java 13 · JDBC · Gson · TestNG (já no projeto). Sem dependência nova.

## Referência ao Edital (TR TRANSALVADOR)

Este plano não acrescenta requisitos: ele corrige a camada de consulta que atende aos itens já mapeados no [Plano 20](20-dashboard-builder.md) (§5.4.2.4a-b, §5.4.2.5, §5.4.5, §5.5.3.1).

## Global Constraints

- Sem nova dependência Maven e sem framework novo (regras do `CLAUDE.md`).
- Sem `ALTER TABLE`. Se precisar de estrutura nova, criar tabela complementar via script em `docs/banco-de-dados/migracoes/`.
- Nenhum identificador SQL (tabela, coluna, schema) pode vir do navegador sem antes ser resolvido no catálogo. Valores sempre por `PreparedStatement`.
- Código novo com sessão e permissão (`Acesso`), recursos fechados com try-with-resources, UTF-8, PT-BR.
- Compatível com SQL Server 2016: **não usar `DATETRUNC`** (só existe a partir do 2022) nem `STRING_AGG ... WITHIN GROUP` em caminhos críticos.

---

## 1. Diagnóstico — por que reescrever a camada de consulta

Hoje a consulta atravessa 4 camadas que precisam concordar sobre nomes de campos, e nenhuma valida a outra:

```
dash.catalogo_* / dash.dataset_campo
   → GeradorModeloCube.java (gera YAML)
   → DashInternoServlet (/api/dash/interno/modelos)
   → cube/cube.js (repositoryFactory, checkAuth)
   → Cube Core 0.35.4 (transpila YAML, compila, gera SQL)
   → SQL Server
```

Falhas encontradas, todas verificáveis no código atual:

| # | Problema | Onde |
|---|---|---|
| 1 | Cada tela envia um contrato diferente para a mesma API: `previa.js` envia `{acao, idDataset, consulta}`, mas o servlet só lê `datasetId` e `query`. A prévia do modelador manda uma consulta vazia. | [previa.js:43](../../src/main/webapp/static/js/dashboards/modelador/previa.js), [DashConsultaServlet.java:82-98](../../src/main/java/muralha/digital/dashboards/DashConsultaServlet.java) |
| 2 | Nomes de campo divergentes: `dataset-editor.js` usa `nomeAlias` sem prefixo, `previa.js` usa `nomeFisico` e o editor de widget usa `dataset_N.campo`. O Cube só aceita o último formato. | [dataset-editor.js:446-459](../../src/main/webapp/static/js/dashboards/dataset-editor.js), [previa.js:36-40](../../src/main/webapp/static/js/dashboards/modelador/previa.js) |
| 3 | O editor de filtros envia operadores SQL (`=`, `LIKE`, `IN`...), mas o Cube espera `equals`, `contains` etc. Qualquer filtro de widget falha. | [editor-filtros.js:68](../../src/main/webapp/static/js/dashboards/editor-filtros.js), [editor-widget.js:293](../../src/main/webapp/static/js/dashboards/editor-widget.js) |
| 4 | Campos de dataset que não pertencem à tabela base são **descartados em silêncio** pelo Cube. Exemplo: `dataset_2` tem base `local_vigente`, mas pede `data`, `placa` e `velocidade`. | YAML servido por `/api/dash/interno/modelos` |
| 5 | O `consulta_json` gravado nos widgets usa aliases sem prefixo (seed: `total_passagens`, `equipamento`), incompatíveis com o formato exigido pelo Cube. | [20261013_dash_fundacao.sql:300](../banco-de-dados/migracoes/20261013_dash_fundacao.sql) |
| 6 | O isolamento por tenant não é aplicado: `queryRewrite` devolve a consulta intacta e o tenant está fixo em 1. | [cube/cube.js](../../cube/cube.js), [DashConsultaServlet.java:188-192](../../src/main/java/muralha/digital/dashboards/DashConsultaServlet.java) |
| 7 | Para executar consultas sem Cube Store foi preciso ligar `CUBEJS_DEV_MODE=true`, o que **desliga a autenticação do Cube** (log: "Authentication checks are disabled"). | [docker-compose.cube.yml](../../docker-compose.cube.yml) |
| 8 | Os ganhos que justificariam o Cube (pré-agregações e cache) não estão em uso: as pré-agregações foram removidas para o modelo compilar. | [GeradorModeloCube.java](../../src/main/java/muralha/digital/dashboards/GeradorModeloCube.java) |

**Conclusão:** o custo do Cube (Docker, segredo compartilhado, JWT, geração de YAML, regras do compilador) não traz benefício hoje e transforma erros de configuração em falhas difíceis de diagnosticar.

### Alternativas avaliadas

| Alternativa | Por que não agora |
|---|---|
| Apache Superset embarcado | Resolve o construtor, mas exige serviço novo (Python, Postgres/Redis), traz visual fora do padrão Bootstrap e obriga a sincronizar usuários e permissões. Depende de aprovação explícita (regra de frameworks). Continua viável se o cliente aceitar uma ferramenta externa. |
| Metabase | O construtor self-service embarcado é recurso pago; a versão gratuita só embarca dashboards prontos. |
| Grafana | Não é um construtor self-service por tenant para usuário de negócio. |
| Manter o Cube e corrigir | Os itens 1 a 5 da tabela teriam de ser corrigidos de qualquer forma, e o Cube continuaria exigindo Cube Store ou modo dev para executar consultas. |

---

## 2. O que fica, o que muda, o que sai

| Componente | Destino |
|---|---|
| Tabelas `dash.*` (catálogo, dataset, campos, widget, dashboard, compartilhamento, histórico, log, modelador, filtro fixo) | **Ficam sem alteração** |
| Telas `pages/analises/**` e JS em `static/js/dashboards/**` | **Ficam**; ajustes pontuais nas tarefas 5 e 6 |
| `PainelService`, `WidgetService`, `DatasetService`, `CompartilhamentoService`, `HistoricoVersaoService`, `ExportacaoService`, `ConsultaLogService`, `PivotService`, `GramaticaCampoCalculado` | **Ficam** (o `PivotService` já pivota em Java) |
| `DashConsultaServlet` | **Reescrito**: mesma URL e mesmo formato de resposta, sem HTTP para o Cube |
| `WidgetServlet.obterMetaDataset` | **Simplificado**: devolve o `nome_alias` puro |
| `HealthCheckServlet` | **Ajustado**: verifica o banco em vez do Cube |
| `GeradorModeloCube`, `JwtService`, `DashInternoServlet`, `cube/`, `docker-compose.cube.yml`, variáveis `CUBEJS_*`/`CUBE_URL` | **Removidos** (tarefa 9, só após aprovação) |

---

## 3. Contrato da consulta (mantido)

**Requisição** — `POST /api/dash/consultas`:

```json
{
  "widgetId": 12,
  "datasetId": 1,
  "query": {
    "measures":   ["total_passagens"],
    "dimensions": ["equipamento"],
    "timeDimensions": [{ "dimension": "data", "granularity": "day", "dateRange": "last 7 days" }],
    "filters":    [{ "member": "equipamento", "operator": "=", "values": ["Av. Paralela"] }],
    "order":      { "total_passagens": "desc" },
    "limit":      100
  }
}
```

Regras de nomes:

- O membro é o **`nome_alias` de `dash.dataset_campo`** do dataset informado.
- Para não quebrar widgets já gravados, o prefixo `dataset_<id>.` é aceito e removido antes da resolução. O prefixo de outro dataset é rejeitado.
- Membro que não existe no dataset gera erro **400 com o nome do campo** (nunca é descartado em silêncio).

**Resposta** (formato que o front já consome):

```json
{
  "ok": true,
  "data": [ { "equipamento": "Av. Paralela", "data.day": "2026-10-14T00:00:00.000", "data": "2026-10-14T00:00:00.000", "total_passagens": 1234 } ],
  "tempoMs": 85,
  "cacheHit": false,
  "annotation": { "measures": { "total_passagens": { "title": "Total de Passagens" } }, "dimensions": { }, "timeDimensions": { } }
}
```

Quando a requisição usa o prefixo `dataset_<id>.`, as chaves da resposta repetem o nome exatamente como foi enviado, para que os renderizadores atuais encontrem os valores.

### Operadores aceitos

Lista fechada, aceitando as duas grafias em uso:

| SQL (editor de filtros) | Cube (filtro cruzado e filtros globais) | SQL gerado |
|---|---|---|
| `=` | `equals` | `col = ?` (ou `IN (?,?)` com vários valores) |
| `!=` | `notEquals` | `col <> ?` |
| `>` `<` `>=` `<=` | `gt` `lt` `gte` `lte` | `col > ?` ... |
| `IN` / `NOT IN` | — | `col IN (?, ?, ...)` (máximo de 500 valores) |
| `BETWEEN` | `inDateRange` | `col >= ? AND col < ?` |
| `LIKE` | `contains` | `col LIKE ?` com `%valor%` e escape de `%`, `_` e `[` |
| `IS NULL` / `IS NOT NULL` | `notSet` / `set` | `col IS NULL` / `col IS NOT NULL` |

Os valores são convertidos para o tipo da coluna no catálogo (`inteiro`, `numerico`, `data`, `data_hora`, `booleano`, `texto`); se a conversão falhar, a consulta é rejeitada com erro claro.

### Períodos (`dateRange`)

Os valores do seletor em [editor.jsp](../../src/main/webapp/muralha-digital/pages/analises/widgets/editor.jsp): `today`, `yesterday`, `last 7 days`, `last 30 days`, `this month`, `last month`, `last 3 months`, `last 6 months`, `last 12 months`, `this year`, além de um intervalo explícito `["AAAA-MM-DD", "AAAA-MM-DD"]`. O intervalo é calculado em Java (`java.time`, fuso `America/Bahia`) e enviado como parâmetro, sempre no formato `>= início AND < fim`, para aproveitar índices.

### Granularidade (SQL Server 2016)

| Granularidade | Expressão |
|---|---|
| `day` | `CAST(col AS DATE)` |
| `week` | `DATEADD(DAY, -((DATEPART(WEEKDAY, col) + @@DATEFIRST - 2) % 7), CAST(col AS DATE))` (semana começando na segunda) |
| `month` | `DATEFROMPARTS(YEAR(col), MONTH(col), 1)` |
| `quarter` | `DATEFROMPARTS(YEAR(col), (DATEPART(QUARTER, col) - 1) * 3 + 1, 1)` |
| `year` | `DATEFROMPARTS(YEAR(col), 1, 1)` |
| `hour` | `DATEADD(HOUR, DATEDIFF(HOUR, 0, col), 0)` |

---

## 4. Montagem do SQL

```
SELECT TOP (@limite)
       <dimensões e tempos agrupados>, <medidas agregadas>
FROM   [schema].[tabela_base] AS t0
LEFT JOIN [schema].[tabela_n] AS t1 ON t0.[col_origem] = t1.[col_destino]   -- só as tabelas usadas na consulta
WHERE  <filtros fixos do dataset> AND <período> AND <filtros do usuário>
GROUP BY <dimensões e tempos>
ORDER BY <ordem pedida, somente membros da consulta>
```

- **Identificadores:** schema, tabela e coluna vêm exclusivamente de `dash.catalogo_tabela` e `dash.catalogo_coluna` (ativos), são validados pela regex `^[A-Za-z_][A-Za-z0-9_]*$` e envolvidos em `[...]`. O alias SQL de cada coluna é gerado (`c0`, `c1`...) e mapeado de volta ao `nome_alias` na resposta.
- **Joins:** caminho a partir da tabela base usando `dash.dataset_tabela` com as relações de `dash.dataset_relacao` (do dataset) e `dash.catalogo_relacao`, nessa ordem. Se o campo pertence a uma tabela sem caminho de join, a consulta é rejeitada com erro claro.
- **Agregações:** `SUM`, `AVG`, `COUNT`, `COUNT_DISTINCT` (`COUNT(DISTINCT ...)`), `MIN`, `MAX`. `AVG` sobre inteiro usa `AVG(CAST(col AS DECIMAL(18,4)))`.
- **Fan-out:** se uma medida de `t0` for agregada junto com um join `one_to_many`, a soma fica inflada. Na fase 1 essa combinação é **bloqueada** com mensagem explicativa (a mesma proteção que o Cube fazia com a chave primária).
- **Campos calculados:** `dash.dataset_campo.expressao` só é usada depois de `GramaticaCampoCalculado.validar()` aceitar e de **todos** os identificadores da expressão serem colunas do catálogo nas tabelas do dataset, reescritos como `tN.[col]`. Caso contrário, erro.
- **Filtros fixos:** `dash.dataset_filtro_fixo` sempre entra no `WHERE`, com a mesma lista de operadores e parâmetros.
- **Limites:** `TOP` no máximo 10.000 (`LIMITE_LINHAS` atual), `Statement.setQueryTimeout(30)`, `setFetchSize(500)`.
- **Conexão:** `Conexao.getConexao()` com `setReadOnly(true)` e `SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED` só para essas leituras analíticas (evita bloquear `veiculo_tempo_real`). Um login próprio somente leitura é recomendado (ver Pendências).

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `src/main/java/muralha/digital/dashboards/consulta/ConsultaDashboard.java` (modelo da requisição já validada) |
| Criar | `src/main/java/muralha/digital/dashboards/consulta/ResolvedorDataset.java` (carrega campos, tabelas, joins e filtros fixos de um dataset) |
| Criar | `src/main/java/muralha/digital/dashboards/consulta/MontadorSql.java` (gera SQL e lista de parâmetros, sem acesso a banco) |
| Criar | `src/main/java/muralha/digital/dashboards/consulta/ExecutorConsulta.java` (executa, aplica timeout e cache, monta a resposta) |
| Criar | `src/main/java/muralha/digital/dashboards/consulta/PeriodoRelativo.java` (`dateRange` → intervalo) |
| Criar | `src/test/java/muralha/digital/dashboards/consulta/MontadorSqlTest.java` |
| Criar | `src/test/java/muralha/digital/dashboards/consulta/PeriodoRelativoTest.java` |
| Alterar | `src/main/java/muralha/digital/dashboards/DashConsultaServlet.java` |
| Alterar | `src/main/java/muralha/digital/dashboards/WidgetServlet.java` (`obterMetaDataset`) |
| Alterar | `src/main/java/muralha/digital/dashboards/DatasetService.java` (validação ao publicar) |
| Alterar | `src/main/java/muralha/digital/dashboards/HealthCheckServlet.java` |
| Alterar | `src/main/webapp/static/js/dashboards/modelador/previa.js` |
| Alterar | `src/main/webapp/static/js/dashboards/dataset-editor.js` (textos sobre Cube, linhas ~467 e ~500) |
| Alterar | `src/main/webapp/muralha-digital/pages/analises/datasets/index.jsp`, `datasets/modelador.jsp`, `analises/manual.jsp` (menções ao Cube) |
| Criar | `docs/banco-de-dados/migracoes/<data>_dash_correcao_datasets_demo.sql` (se a validação da tarefa 7 apontar campos inválidos) |
| Remover (tarefa 9) | `GeradorModeloCube.java`, `JwtService.java`, `DashInternoServlet.java`, `cube/`, `docker-compose.cube.yml` |
| Atualizar | `docs/dashboards/construtor-dashboards-arquitetura.md`, `docs/manual-dashboard-builder.md`, `docs/entregas/21-dashboard-builder.md` |

---

### Tarefa 0: Fechar a brecha de autenticação (imediato)

- [x] Voltar `CUBEJS_DEV_MODE: "false"` em `docker-compose.cube.yml` e recriar o container (ou parar o container até a tarefa 9).
- [x] Remover o `console.log('[CUBE-DEBUG] ...')` de `cube/cube.js`.

### Tarefa 1: `PeriodoRelativo`

- [x] Converter os 10 valores de `dateRange` listados na seção 3 e o intervalo explícito em `[início, fim)` (`LocalDateTime`), com relógio injetável para teste.
- [x] Rejeitar valores fora da lista.
- [x] `PeriodoRelativoTest`: um caso por valor, virada de mês e de ano, ano bissexto.

### Tarefa 2: `ResolvedorDataset`

- [x] Para um `idDataset`: carregar `dash.dataset` (status, tenant, visibilidade), `dataset_campo` + `catalogo_coluna` + `catalogo_tabela`, `dataset_tabela`, `dataset_relacao`, `catalogo_relacao` e `dataset_filtro_fixo`, em no máximo 4 consultas parametrizadas.
- [x] Expor `campo(alias)` → tabela, coluna física, tipo, agregação, papel e expressão; `caminhoJoin(tabela)` → lista de joins a partir da base.
- [~] **Descartado na execução:** o custo das leituras de metadados é baixo e `salvar` não incrementa `versao`, o que deixaria o cache desatualizado. Cache em memória por `idDataset` + `dash.dataset.versao` (`ConcurrentHashMap`), invalidado ao salvar ou publicar.

### Tarefa 3: `MontadorSql` (núcleo, sem banco)

- [x] Entrada: `ConsultaDashboard` + `ResolvedorDataset`. Saída: `String sql` + `List<Object> parametros` + mapa `aliasSql → nomeResposta`.
- [x] Implementar as regras da seção 4 (identificadores, joins mínimos, agregações, granularidade, operadores, filtros fixos, ordem, `TOP`, bloqueio de fan-out, campos calculados).
- [x] `MontadorSqlTest` (TestNG), no mínimo:
  - [x] medida + dimensão + tempo geram o `GROUP BY` correto;
  - [x] join só aparece quando um campo da tabela relacionada é usado;
  - [x] membro inexistente → exceção com o nome do membro;
  - [x] prefixo `dataset_<outro>.` → exceção;
  - [x] valor `"x'; DROP TABLE t--"` em filtro vira parâmetro e nunca aparece no SQL;
  - [x] alias ou `order` com caracteres fora da regex → exceção;
  - [x] cada operador das duas grafias;
  - [x] `IN` com 501 valores → exceção;
  - [x] `LIKE` escapa `%`, `_` e `[`;
  - [x] combinação de fan-out bloqueada;
  - [x] expressão calculada com coluna fora do dataset → exceção.

### Tarefa 4: `ExecutorConsulta` + `DashConsultaServlet`

- [x] `ExecutorConsulta`: abre a conexão, aplica `setReadOnly`, timeout e fetch size, executa e converte os tipos (data → ISO `yyyy-MM-dd'T'HH:mm:ss.SSS`, `DECIMAL` → número) para JSON no formato da seção 3, preenchendo `annotation` com os rótulos do dataset.
- [x] Cache de resultado em memória: chave = SHA-256 de `sql + parâmetros`, TTL de 60 s, no máximo 200 entradas (descartar a mais antiga). `cacheHit` reflete o uso.
- [x] Reescrever `DashConsultaServlet.doPost`: manter `Acesso`, `validarAcessoDataset` e `ConsultaLogService.registrarAsync`; trocar `chamarCube` por `ExecutorConsulta`; remover `JwtService` e `HttpClient`.
- [x] Erros de validação → HTTP 400 com `{ok:false, erro:"..."}`; erro de banco → 500 com mensagem genérica para o usuário e detalhe só no log.
- [x] Aceitar também `idDataset`/`consulta` (formato antigo do `previa.js`) durante a transição.

### Tarefa 5: Unificar o front no contrato

- [x] `WidgetServlet.obterMetaDataset`: devolver `alias = nome_alias` (sem montar `dataset_N.*` nem sufixos `_avg`/`_sum`).
- [x] `modelador/previa.js`: enviar `{datasetId, query}` usando `nomeAlias`.
- [x] Conferir `dataset-editor.js`, `editor-widget.js`, `painel.js`, `filtro-cruzado.js` e `filtros-globais.js`: todos enviam aliases do dataset; nenhum precisa conhecer nome físico.

### Tarefa 6: Pivot, exportação e health check

- [x] `PivotService.montarConsultaPivot` continua gerando o mesmo JSON; confirmar que a resposta do executor alimenta `pivotar()` sem mudança (ajustar o comentário que cita o Cube).
- [x] `ExportacaoDashboardServlet`: confirmar que obtém dados pela mesma rota; se chamar o Cube, trocar pelo `ExecutorConsulta`.
- [x] `HealthCheckServlet`: substituir a verificação do Cube por `SELECT 1` com timeout de 5 s; atualizar `HealthCheckTest`.

### Tarefa 7: Validação ao publicar dataset

- [x] Em `DatasetService.publicar`: para cada campo, exigir que a coluna pertença a uma tabela do dataset com caminho de join a partir da base, e que as expressões passem pela regra da seção 4. Retornar a lista de campos inválidos e **não publicar** se houver erro.
- [x] Rodar a validação nos datasets existentes (somente leitura) e, se houver campos inválidos (ex.: `dataset_2`), gerar o script de correção em `docs/banco-de-dados/migracoes/` para execução autorizada.

### Tarefa 8: Testes manuais ponta a ponta

- [ ] Prévia no editor de dataset e no modelador.
- [ ] Widget de cada tipo (`barras`, `linhas`, `pizza`, `kpi`, `tabela`, `pivot`) com e sem período e filtro.
- [ ] Painel: filtro global, filtro cruzado, drill-down, auto-refresh, exportação.
- [x] Usuário sem permissão no dataset → 403.
- [x] Consulta pesada (`veiculo_tempo_real`, 12 meses, por dia) dentro do timeout; registrar o tempo no relatório.
- [x] Registro em `dash.consulta_log` e em `dbo.sis_log_auditoria` conferido.

### Tarefa 9: Remover o Cube (após aprovação do usuário)

- [x] Confirmar com o usuário antes de apagar arquivos (confirmado em 2026-10-14).
- [x] Remover `GeradorModeloCube.java`, `JwtService.java`, `DashInternoServlet.java`, a pasta `cube/` e `docker-compose.cube.yml`; parar e remover o container `dash-cube` e o volume `gtw-cwb_cube-store`. Os arquivos nunca tinham sido commitados; uma cópia ficou na pasta temporária da sessão.
- [x] Retirar `CUBEJS_API_SECRET`, `CUBE_URL` e `CUBE_INTERNAL_SECRET` de `docs/requisitos/07-configuracao-e-segredos.md` e dos `.example` (nenhum deles citava essas variáveis).
- [x] Corrigir e executar o script de demonstração `20261014_dados_demonstracao_dashboards.sql` (executado em 2026-10-14 com autorização):
  - aliases sem `tabela.` e contagens ligadas à chave primária; consultas dos 16 widgets com os nomes novos;
  - a limpeza por `nome LIKE '%Alertas%'` apagaria o dataset real 2 ("Alertas Resumo"): trocada por ids fixos (datasets 101-103, painéis 101-102, widgets 1001-1016);
  - a inserção fixa de `muralha.veiculo_tempo_real` com id 101 violaria a unicidade do catálogo (a tabela já é o id 1): agora reaproveita tabelas e colunas existentes e só insere o que falta;
  - `RETURN` com transação aberta trocado por `THROW`, com `XACT_ABORT`;
  - tipos corrigidos conforme o banco (`categoria` e `estado_veiculo` inteiros; `id` de passagem/alerta é `uniqueidentifier`);
  - executado duas vezes para provar idempotência; datasets 1 e 2 intactos; os 16 widgets respondem pela API em 0,4 a 1,6 s.
- [x] Retirar as menções ao Cube das telas e do manual (feito em 2026-10-14: `datasets/index.jsp`, `datasets/modelador.jsp`, `analises/manual.jsp`, `dataset-editor.js`).

### Tarefa 10: Documentação

- [x] Atualizar `docs/dashboards/construtor-dashboards-arquitetura.md` (novo fluxo, sem Cube).
- [x] Atualizar `docs/manual-dashboard-builder.md`.
- [x] Atualizar `docs/entregas/21-dashboard-builder.md`, com seção **Origem** apontando para este plano.
- [x] Atualizar `docs/referencia/servlets-e-endpoints.md` (remover `/api/dash/interno/*`).

---

## Registro de execução (2026-10-14)

**Resultado:** tarefas 0 a 7, 9 e 10 concluídas; Tarefa 8 feita via HTTP (falta o roteiro no navegador).

- **Correção da Tarefa 10:** a primeira checagem de menções ao Cube nos documentos usou um filtro que não casava com nenhum arquivo e concluiu, por engano, que `manual-dashboard-builder.md` e `referencia/*.md` estavam limpos. Na Tarefa 9 foram corrigidos de fato: `requisitos/04-modulos-funcionais.md`, `manual-dashboard-builder.md`, `referencia/servlets-e-endpoints.md`, `referencia/modulos-backend-muralha.md`, `referencia/inventario-pacotes-java.md` e `.gitignore`. Relatórios de sessão DASH, migrações já executadas e especificações antigas mantêm as menções como registro histórico.

- **Testes:** 101 testes TestNG do pacote `dashboards` passando, incluindo `MontadorSqlTest` (35), `PeriodoRelativoTest` (10) e `ConsultaIntegracaoTest` (4, somente leitura no `GTW_MURALHA_DEV`). Para rodar a integração: definir `DASH_TESTE_JDBC_URL`, `DASH_TESTE_USUARIO` e `DASH_TESTE_SENHA` e passar o driver com `-Dmaven.test.additionalClasspath=<mssql-jdbc-9.2.1.jre8.jar>`, porque o driver não é dependência de teste no `pom.xml`.
- **Desvio 1 — pré-agregação (não prevista):** agrupar `veiculo_tempo_real` (~8,2 milhões de linhas) pelo nome do equipamento levava 80 s no SQL Server 2016, mesmo com `HASH GROUP`. Agrupar antes pela chave `id_local` e só então juntar `local_vigente` leva 0,4 s. O `MontadorSql` passou a gerar essa subconsulta quando todas as medidas são da tabela base (`SUM`, `COUNT`, `MIN`, `MAX`, `AVG`) e todos os joins são N:1 ou 1:1. Medido pela aplicação: contagem por equipamento sem período, de erro após 30 s para 2,2 s; setembro por dia e equipamento, de 20 s para 2,3 s.
- **Desvio 2 — cache de metadados descartado** (Tarefa 2), pelo motivo registrado na tarefa.
- **Desvio 3 — 6 consultas de metadados em vez de 4:** cada uma é pequena e parametrizada.
- **Dados reais:** os datasets 1 e 2 são válidos; nenhum script de correção foi necessário. O `dataset_2` só falhava no Cube, que exigia campos da tabela base. A relação 2 de `dash.catalogo_relacao` (`local_vigente.id = veiculo_tempo_real.id`) é inválida, porque `local_vigente` não tem coluna `id`; o montador a ignora. Corrigir via script quando houver autorização.
- **Filtro cruzado:** passou a valer só para widgets do mesmo dataset; antes, aplicaria a dimensão a widgets de outros datasets.
- **Encontrados fora do escopo, não corrigidos:**
  - `DatasetServlet.salvarModelador` grava `origem = 'manual'` por padrão, valor que viola o `CHECK` de `dash.dataset_relacao` (`banco`, `catalogo`, `usuario`).
  - O mesmo método fixa `id_usuario = 1` e `tenant_id = 1` ao criar dataset.
  - `analises/manual.jsp` exibia o segredo de desenvolvimento do Cube; a linha foi removida antes de qualquer commit (`git log -S` não encontra o valor no histórico).

## Riscos e mitigação

| Risco | Mitigação |
|---|---|
| Injeção de SQL pelo montador dinâmico | Identificadores só do catálogo + regex + colchetes; valores só por parâmetro; testes de injeção na tarefa 3; revisão de código focada no `MontadorSql`. |
| Consultas pesadas em `veiculo_tempo_real` (~8 milhões de linhas) | `TOP`, timeout, período obrigatório para datasets marcados como volumosos, cache de 60 s, `READ UNCOMMITTED`. Se não bastar, criar tabelas-resumo via script (fase 2). |
| Soma inflada por join 1:N | Combinação bloqueada na fase 1. |
| Widgets gravados com nomes antigos | Prefixo `dataset_N.` aceito e removido; validação aponta o que não resolve. |
| Perda de recursos do Cube (pré-agregação, API SQL) | Não estão em uso hoje. |

## Pendências e decisões do usuário

- Aprovar este plano e a remoção do Cube (tarefa 9).
- Login SQL somente leitura: **decidido em 2026-10-14 não criar por enquanto**. Usar a conexão padrão com `setReadOnly(true)`, que é só uma dica ao driver e não impede escrita; a proteção real é o `MontadorSql` gerar apenas `SELECT`.
- Multi-tenant: o tenant continua fixo em 1 (como hoje). Quando houver vínculo usuário-tenant, o `ResolvedorDataset` passa a filtrar por ele.
- Executar o script de dados de demonstração e o eventual script de correção (tarefa 7) no `GTW_MURALHA_DEV`.
