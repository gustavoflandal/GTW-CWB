# Dashboard Builder Self-Service — Plano de Implementação

> **Status: Implementado** — Entrega: [21-dashboard-builder.md](../entregas/21-dashboard-builder.md) (a numeração difere: Plano 20 → Entrega 21 → Teste 20; a camada de consulta foi refeita pelo [Plano 21](21-dashboards-consulta-sql-sem-cube.md))
>
> **Bibliotecas vendorizadas (exceção aprovada em 2026-10-16):** ECharts, GridStack, AG Grid, SortableJS, AntV X6 e dagre, em `src/main/webapp/static/vendor/`, podem ser usadas pelo construtor e os stubs podem ser substituídos pelas versões reais. É a única exceção à regra "sem novos frameworks"; ver [`CLAUDE.md`](../../CLAUDE.md).
>
> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Permitir que qualquer usuario autorizado (grupos 41/42) crie dashboards dinamicos com widgets configuraveis (cards KPI, graficos Chart.js, tabelas, mapas Leaflet), selecionando dados de um catalogo de datasets (views SQL pre-cadastradas), sem escrever SQL.

**Architecture:** Tres tabelas novas (`dashboard_dataset`, `dashboard_painel`, `dashboard_widget`) no schema `muralha`. Servlet unico (`DashboardBuilderServlet`) com CRUD de paineis/widgets e execucao de queries. Frontend com CSS Grid + HTML5 Drag API para posicionamento livre de widgets. Builder visual para configuracao de widgets (selecao de dataset, colunas, agregacao, tipo de grafico). Auto-refresh configuravel por painel.

**Tech Stack:** Java Servlet · Bootstrap 5.3 · Chart.js (CDN existente) · Leaflet (CDN existente) · JavaScript puro · CSS Grid · HTML5 Drag API

## Referência ao Edital (TR TRANSALVADOR)

| Ref. TR | Requisito |
|---|---|
| §5.4.2.4a-b | Filtros dinâmicos com atualização simultânea |
| §5.4.2.5 | Configuração de dashboards personalizados pela CONTRATANTE (ver também [Plano 15](15-kpis-configuráveis.md)) |
| §5.4.5 | Relatórios analíticos completos (tráfego, velocidade, infração) |
| §5.5.3.1 | Painel de controle operacional do pré-processamento (ver também [Plano 10](10-sla-preprocessamento.md)) |

> Análise de aderência completa: [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md)

## Global Constraints

- Sem nova dependencia Maven
- Sem novo framework/biblioteca JS (usar Chart.js e Leaflet ja presentes)
- Drag-and-drop via HTML5 Drag API nativo
- Datasets sao views SQL pre-cadastradas pelo admin — usuario final nunca escreve SQL
- Build: `..\.setup-gtw\build.ps1`

---

## Modelo de Dados

### Tabela `muralha.dashboard_dataset`

Catalogo de fontes de dados disponiveis (views SQL registradas pelo admin).

```sql
CREATE TABLE muralha.dashboard_dataset (
    id            INT IDENTITY(1,1) PRIMARY KEY,
    nome          VARCHAR(80)   NOT NULL,
    descricao     VARCHAR(200)  NULL,
    nome_view     VARCHAR(128)  NOT NULL,
    colunas_json  VARCHAR(MAX)  NOT NULL,
    ativo         BIT           NOT NULL DEFAULT 1
);
```

`colunas_json` armazena metadados das colunas da view:
```json
[
  {"nome": "data_hora", "tipo": "data", "label": "Data/Hora"},
  {"nome": "id_local", "tipo": "numerico", "label": "Equipamento"},
  {"nome": "total_passagens", "tipo": "numerico", "label": "Passagens"},
  {"nome": "latitude", "tipo": "geo", "label": "Latitude"},
  {"nome": "longitude", "tipo": "geo", "label": "Longitude"}
]
```

### Tabela `muralha.dashboard_painel`

Dashboards criados pelos usuarios.

```sql
CREATE TABLE muralha.dashboard_painel (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    nome             VARCHAR(80)   NOT NULL,
    descricao        VARCHAR(200)  NULL,
    id_usuario       INT           NOT NULL,
    refresh_segundos INT           NOT NULL DEFAULT 60,
    publico          BIT           NOT NULL DEFAULT 0,
    criado_em        DATETIME      NOT NULL DEFAULT GETDATE(),
    atualizado_em    DATETIME      NULL,
    FOREIGN KEY (id_usuario) REFERENCES dbo.sis_usuario(id)
);
```

### Tabela `muralha.dashboard_widget`

Widgets dentro de um painel com posicao no grid.

```sql
CREATE TABLE muralha.dashboard_widget (
    id             INT IDENTITY(1,1) PRIMARY KEY,
    id_painel      INT           NOT NULL,
    tipo           VARCHAR(10)   NOT NULL,
    titulo         VARCHAR(80)   NOT NULL,
    id_dataset     INT           NOT NULL,
    coluna_valor   VARCHAR(60)   NULL,
    coluna_label   VARCHAR(60)   NULL,
    coluna_lat     VARCHAR(60)   NULL,
    coluna_lng     VARCHAR(60)   NULL,
    agregacao      VARCHAR(10)   NOT NULL DEFAULT 'NONE',
    filtros_json   VARCHAR(MAX)  NULL,
    tipo_grafico   VARCHAR(15)   NULL,
    threshold_ok   FLOAT         NULL,
    threshold_warn FLOAT         NULL,
    grid_col       INT           NOT NULL DEFAULT 1,
    grid_row       INT           NOT NULL DEFAULT 1,
    grid_width     INT           NOT NULL DEFAULT 4,
    grid_height    INT           NOT NULL DEFAULT 2,
    ordem          INT           NOT NULL DEFAULT 0,
    FOREIGN KEY (id_painel) REFERENCES muralha.dashboard_painel(id),
    FOREIGN KEY (id_dataset) REFERENCES muralha.dashboard_dataset(id),
    CONSTRAINT chk_tipo CHECK (tipo IN ('CARD','CHART','TABLE','MAP')),
    CONSTRAINT chk_agregacao CHECK (agregacao IN ('SUM','AVG','COUNT','MAX','MIN','NONE')),
    CONSTRAINT chk_tipo_grafico CHECK (tipo_grafico IS NULL OR tipo_grafico IN ('bar','line','pie','doughnut'))
);
```

---

## Arquivos

| Acao | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261010_dashboard_builder.sql` |
| Criar | `src/main/java/muralha/digital/dashboard/DashboardBuilderServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/dashboard/builder/index.jsp` |
| Criar | `src/main/webapp/muralha-digital/pages/dashboard/builder/assets/js/dashboard-grid.js` |
| Criar | `src/main/webapp/muralha-digital/pages/dashboard/builder/assets/js/widget-renderer.js` |
| Criar | `src/main/webapp/muralha-digital/pages/dashboard/builder/assets/js/widget-config.js` |
| Criar | `src/main/webapp/muralha-digital/pages/dashboard/builder/assets/css/builder.css` |
| Criar | `src/main/webapp/muralha-digital/pages/admin/datasets/index.jsp` |
| Criar | `src/main/java/muralha/digital/dashboard/DatasetServlet.java` |

---

### Tarefa 1: Migracao SQL

- [x] Criar script `20261010_dashboard_builder.sql` com:
  - CREATE TABLE `muralha.dashboard_dataset`
  - CREATE TABLE `muralha.dashboard_painel`
  - CREATE TABLE `muralha.dashboard_widget`
  - INSERT de 3-5 datasets iniciais baseados em views existentes ou novas:
    - `muralha.v_passagens_diarias` (passagens agrupadas por dia/equipamento)
    - `muralha.v_alertas_resumo` (alertas por tipo/status/dia)
    - `muralha.v_disponibilidade_equipamentos` (uptime por equipamento)
  - INSERT menu no `sis_menu`, `sis_menu_infos`, `sis_menu_direitos`

### Tarefa 2: DatasetServlet (CRUD admin de datasets)

- [x] Criar `DatasetServlet.java` mapeado em `/MuralhaDigital/Dataset`
  - `acao=listar` — lista datasets ativos (GET, JSON)
  - `acao=listarTodos` — lista todos incluindo inativos (GET, admin)
  - `acao=obterColunas&id=X` — retorna `colunas_json` de um dataset (GET)
  - `acao=salvar` — INSERT/UPDATE dataset (POST, admin)
  - Verificacao de acesso admin

### Tarefa 3: DashboardBuilderServlet

- [x] Criar `DashboardBuilderServlet.java` mapeado em `/MuralhaDigital/DashboardBuilder`

**Acoes de painel:**
  - `acao=listarPaineis` — paineis do usuario + publicos (GET)
  - `acao=obterPainel&id=X` — painel com todos os widgets (GET)
  - `acao=salvarPainel` — criar/atualizar painel (POST)
  - `acao=excluirPainel&id=X` — soft delete (POST)

**Acoes de widget:**
  - `acao=salvarWidget` — criar/atualizar widget (POST, JSON body)
  - `acao=excluirWidget&id=X` — remover widget (POST)
  - `acao=atualizarLayout` — batch update de posicoes grid_col/grid_row/grid_width/grid_height (POST, JSON array)

**Acao de dados:**
  - `acao=executarWidget&id=X` — executa query do widget (SELECT sobre a view do dataset, aplicando coluna_valor, coluna_label, agregacao, filtros) e retorna JSON com dados

**Seguranca:**
  - Verificacao de sessao via `Acesso`
  - Usuario so edita/exclui paineis proprios
  - Query montada internamente (nunca SQL do usuario): `SELECT coluna_label, agregacao(coluna_valor) FROM nome_view WHERE filtros GROUP BY coluna_label`
  - Validacao de que coluna_valor/coluna_label existem no `colunas_json` do dataset

### Tarefa 4: Frontend — Pagina builder (JSP)

- [x] Criar `index.jsp` com layout:
  - **Barra superior:** titulo do painel, botao salvar, seletor de refresh, botao compartilhar (publico)
  - **Sidebar esquerda:** lista de paineis (meus + publicos), botao "+ Novo Painel"
  - **Area central:** grid CSS 12 colunas onde widgets sao renderizados
  - **Modal de widget:** formulario de configuracao (selecionar dataset, colunas, tipo, agregacao, filtros, titulo)

Includes: `cabecalho_bootstrap_simples.jsp`, Chart.js CDN, Leaflet CDN

### Tarefa 5: Frontend — Grid drag-and-drop (`dashboard-grid.js`)

- [x] Implementar grid de 12 colunas com CSS Grid
- [x] Drag via HTML5 Drag API:
  - `dragstart`: armazena widget ID
  - `dragover`/`drop`: calcula posicao na grid (col/row)
  - Resize via handles nos cantos do widget
- [x] Funcao `salvarLayout()`: coleta posicoes de todos os widgets e envia batch para `acao=atualizarLayout`
- [x] Auto-save apos cada drag/resize

### Tarefa 6: Frontend — Renderizador de widgets (`widget-renderer.js`)

- [x] Funcao `renderWidget(widget, dados)` que despacha por tipo:
  - **CARD**: numero grande + badge de status (verde/amarelo/vermelho via thresholds) + unidade
  - **CHART**: cria instancia Chart.js no canvas do widget (bar/line/pie/doughnut)
  - **TABLE**: tabela Bootstrap paginada com os dados retornados
  - **MAP**: mapa Leaflet com markers nos pontos (coluna_lat, coluna_lng)
- [x] Funcao `refreshDashboard()`: itera widgets, chama `executarWidget` para cada, re-renderiza
- [x] `setInterval(refreshDashboard, refresh_segundos * 1000)` configuravel

### Tarefa 7: Frontend — Configuracao de widget (`widget-config.js`)

- [x] Modal Bootstrap com formulario:
  1. Selecionar dataset (dropdown carregado de `acao=listar`)
  2. Ao selecionar dataset, carregar colunas (de `acao=obterColunas`)
  3. Selecionar tipo de widget (CARD/CHART/TABLE/MAP)
  4. Campos dinamicos por tipo:
     - CARD: coluna_valor, agregacao, threshold_ok, threshold_warn
     - CHART: coluna_label, coluna_valor, agregacao, tipo_grafico
     - TABLE: selecao multipla de colunas a exibir
     - MAP: coluna_lat, coluna_lng, coluna_label (tooltip)
  5. Filtros: adicionar condicoes (coluna, operador, valor)
  6. Titulo do widget
  7. Preview dos dados antes de salvar

### Tarefa 8: Tela admin de datasets

- [x] Criar `admin/datasets/index.jsp`:
  - Tabela com datasets cadastrados (nome, view, status)
  - Offcanvas para criar/editar dataset
  - Campo para nome da view SQL
  - Botao "Detectar Colunas" que executa `SELECT TOP 0 * FROM view` e preenche colunas_json automaticamente
  - Edicao manual dos labels e tipos das colunas

### Tarefa 9: Views SQL iniciais

- [x] Criar views no script de migracao:

```sql
CREATE VIEW muralha.v_passagens_diarias AS
SELECT
    CAST(vtr.data_hora AS DATE) AS data,
    lv.id AS id_local,
    lv.nome AS equipamento,
    COUNT(*) AS total_passagens
FROM dbo.veiculo_tempo_real vtr
JOIN dbo.local_vigente lv ON vtr.id_local = lv.id
GROUP BY CAST(vtr.data_hora AS DATE), lv.id, lv.nome;
```

(+ 2-4 views adicionais conforme dados disponiveis)

---

## Fluxo do usuario

1. Usuario acessa "Dashboard Builder" no menu
2. Cria um novo painel (nome + descricao + refresh)
3. Clica "+ Widget" no painel vazio
4. No modal: seleciona dataset → escolhe colunas → define tipo → salva
5. Widget aparece no grid, pode arrastar/redimensionar
6. Adiciona mais widgets, salva o layout
7. Dashboard atualiza automaticamente conforme refresh configurado
8. Pode marcar como publico para compartilhar

---

## Requisitos TR atendidos

| Ref. TR | Requisito | Como atende |
|---|---|---|
| 5.4.2.4a-b | Filtros dinamicos com atualizacao simultanea | Filtros por widget, refresh simultaneo |
| 5.4.2.5 | Dashboards personalizados pela CONTRATANTE | Builder self-service com datasets |
| 5.4.5 | Relatorios analiticos (parcial) | Widgets TABLE com dados tabulares |
| 5.5.3.1 | Painel operacional pre-processamento (parcial) | Dashboard montavel com views de pre-proc |

---

## Pendencias

- Executar migracao SQL no GTW_MURALHA_DEV
- Registrar menus no banco (sis_menu, sis_menu_infos, sis_menu_direitos)
- Testes manuais (ver `docs/testes/20-dashboard-builder.md`)
