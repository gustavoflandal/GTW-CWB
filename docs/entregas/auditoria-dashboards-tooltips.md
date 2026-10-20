# Auditoria — Módulo de Dashboards: tooltips e referências cruzadas

> **Data:** 2026-10-17
> **Status:** 🟡 Correções implementadas — migração da demonstração executada (conferida em 2026-10-20); aguardando build no Windows e teste no navegador. Itens de segurança listados em "Pendências" seguem **abertos**.
> **Escopo:** módulo novo de análises (`static/js/dashboards/`, `pages/analises/`, `muralha.digital.dashboards`), dashboard legado (`pages/dashboards/`, `pages/dashboard/`), tooltips de interface do Muralha Digital e integração Superset (`analise/superset/`).

## Origem

- Plano: [`20-dashboard-builder.md`](../planos-salvador/20-dashboard-builder.md)
- Plano: [`21-dashboards-consulta-sql-sem-cube.md`](../planos-salvador/21-dashboards-consulta-sql-sem-cube.md)
- Plano: [`23-analises-superset.md`](../planos-salvador/23-analises-superset.md)

## Resumo

O backend de consulta já devolvia, em `annotation`, o rótulo amigável de cada campo, mas nenhum widget o usava. Por isso os tooltips, legendas, eixos e cabeçalhos mostravam o alias técnico (`total_passagens`), datas cruas (`2026-01-01T00:00:00.000`) e números no formato americano (`12,345.6`). Além disso, o editor sempre agrupava por mês quando o dataset tinha data, o que fazia o KPI exibir só o mês mais antigo e as barras e pizzas repetirem cada categoria uma vez por mês.

Foram corrigidos 39 defeitos, além dos dados de demonstração. Os itens de segurança (autorização, IDOR) e parte do Superset ficaram como pendências: dependem de decisão sobre perfis de acesso.

## Corrigido

### Tooltips e gráficos do módulo de análises

| Sev. | Defeito | Correção |
|---|---|---|
| Crítico | KPI e gauge mostravam o valor do **mês mais antigo**, não o total: o editor sempre mandava `timeDimensions` com granularidade mês | Editor ganhou "Sem agrupamento" e "Todo o período"; KPI, gauge, pizza, rosca e funil usam a data só como filtro (`ajustarConsultaAoTipo`) |
| Crítico | Barras, pizza e funil repetiam a mesma categoria uma vez por mês, e o tooltip não indicava o mês | `montarSeries` (`widgets/comum.js`): tempo e dimensão viram eixo e série |
| Crítico | Tabela dinâmica (pivot) sempre quebrava: `cabecalhos.size()` em array JS | `cabecalhos.length` |
| Crítico | Drill-down trocava a granularidade sem filtrar o período clicado: "out/2026 [Semana]" mostrava as semanas do ano todo | O clique passa a filtrar o intervalo do período (`intervaloDoPeriodo`) |
| Alto | Tooltips, legendas e cabeçalhos com alias técnico | Painel e editor repassam `annotation` (`widget.anotacao`); `rotuloCampo` |
| Alto | Números no formato americano no tooltip, nos rótulos, no gauge e no KPI ("1.2K") | `valueFormatter` e formatadores em pt-BR |
| Alto | Datas ISO cruas no eixo e no tooltip | `formatarTempo` por granularidade (`jan/2026`, `2º tri/2026`, `05/01/2026`) |
| Alto | XSS nos tooltips do heatmap e da dispersão (formatter em função, sem escape) | `escapeHtml` em todos os formatters de função |
| Alto | Zona "Cores / Séries" sem efeito e perdida ao reabrir o widget | Série vira `dimensions[1]` e é guardada em `visual.series` |
| Alto | Ao editar um KPI salvo, o editor acrescentava "mês / últimos 12 meses" | Período e granularidade ausentes são restaurados como vazios |
| Alto | Pivot reabria sem linhas, colunas e valores; salvar apagava a configuração | Zonas do pivot restauradas |
| Alto | Opções visuais (paleta, rótulos, empilhado) não refletiam o widget salvo e eram sobrescritas | `sincronizarOpcoesVisuais` |
| Alto | Filtro cruzado e drill-down disparavam no mesmo clique; filtro com valor formatado ou com "0,1,123" (heatmap) | Valor bruto via `__dashMeta`; tempo → drill, categoria → filtro cruzado |
| Médio | Campo de data arrastado para o Eixo X agrupava por instante exato | Vira a dimensão temporal do widget |
| Médio | Editor oferecia "OU" nos filtros, mas a consulta usava "E" (o backend rejeita OR) | Opção removida; o texto informa "Todas as condições (E)" |
| Médio | Editor oferecia "Ano passado", que o backend rejeitava (400) | `last year` em `PeriodoRelativo` (+ teste) |
| Médio | Tabela e "Ver como tabela" com cabeçalho técnico e coluna de data duplicada | Rótulos, valores pt-BR e coluna única de tempo |
| Médio | Manual prometia ordenação na Tabela, que não existia | Clique no cabeçalho ordena (`aria-sort`) |
| Médio | Mudança de frequência de atualização só valia após reabrir o painel | `configurarRefresh` ao salvar |
| Médio | Filtro cruzado e drill continuavam valendo ao trocar de painel | Reiniciados em `abrirPainel` |
| Médio | Faixas do gauge ignoravam o mínimo | Fração calculada entre `min` e `max` |
| Baixo | Ícone `bi-scatter` não existe no Bootstrap Icons 1.11.1 (Dispersão sem ícone) | `bi-dot` |
| Baixo | Rodapé "Cache hit"; textos sem acento em telas tocadas | "Resultado em cache"; acentuação |

### Editor de novo widget (revisão de 2026-10-17, segunda rodada)

O fluxo de **criar** um widget não funcionava como esperado. O teste anterior cobria só a edição.

| Sev. | Defeito | Correção |
|---|---|---|
| Crítico | Arrastar e soltar rodava sobre um *stub* de 4 KB (`PLACEHOLDER` no cabeçalho): o campo solto ficava semitransparente, não havia como removê-lo, o mesmo campo entrava várias vezes, uma medida caía em "Eixo X" sem aviso e, a cada troca de dataset, os handlers se acumulavam (uma soltura inseria N cópias) | SortableJS 1.15.0 real em `static/vendor/sortablejs/` (exceção aprovada no CLAUDE.md); instâncias criadas uma só vez; cada zona aceita só os tipos certos (`group.put`), destaque das zonas válidas durante o arraste, botão × para remover, sem repetição |
| Crítico | Trocar o dataset mantinha os campos antigos nas zonas: a consulta falhava com "Campo não existe no dataset" | Zonas e seleção limpas ao trocar |
| Alto | Granularidade "Mês" por padrão: um gráfico de barras novo nascia com 12 séries (uma por mês) | Padrão "Sem agrupamento"; o mês entra sozinho ao soltar um campo de data no Eixo X ou ao escolher Linhas/Área sem dimensão, e sai quando o motivo some |
| Alto | Mudar granularidade, período, limite ou filtros não atualizava o preview | Preview refeito em todas essas mudanças |
| Médio | Salvar sem medida gravava um widget vazio; validações com `alert()` | Validação de medida (tabela pode ter só dimensões; pivot exige Valores); SweetAlert2 |
| Médio | Widget novo era gravado em (0,0), sobre o primeiro do painel | `WidgetService.salvar` coloca o novo widget abaixo dos existentes |
| Médio | Editor aberto sem painel (`dashboardId`) só falhava ao salvar ("Sem permissao neste painel") | Aviso na tela e botões de salvar desabilitados; mensagem quando não há dataset publicado |
| Baixo | Datasets com mais de um campo de data não permitiam escolher qual usar | Seletor "Campo de data" (aparece só quando há mais de um) |
| Baixo | Tipos de visualização sem descrição; rótulos sem acento | Tooltip com a descrição de cada tipo; textos revisados |

Terceira rodada (teste em navegador real, Chromium 141 via Playwright):

| Sev. | Defeito | Correção |
|---|---|---|
| Crítico | O botão × de remover era um ícone de fonte (`bi-x-lg`, Bootstrap Icons via CDN): sem a fonte carregada ficava com largura zero — invisível e sem clique. Resultado: campo solto não saía mais da zona | `<button>` com o caractere "×" e estilo próprio em CSS; funciona sem fonte de ícones e pelo teclado |
| Alto | Módulos JS do dashboard sem controle de cache: após uma atualização o navegador podia misturar arquivos novos e antigos (ex.: stub antigo do Sortable em cache com o editor novo, que então quebrava em `Sortable.get`) | `SemCacheScriptsDashboardFilter` (`no-cache, must-revalidate` em `/static/js/dashboards/*`) e `?v=1.15.0` no script do Sortable |
| Crítico | **Preview só desenhava uma vez**: ao trocar o tipo (barras → linhas…), o editor substituía o conteúdo do preview sem descartar a instância do ECharts presa ao elemento; o ECharts reaproveitava a instância, cujo canvas já tinha saído da tela, e nada aparecia — nem voltando ao tipo inicial | `descartarGrafico`/`mostrarHtml` em `comum.js` (todo conteúdo HTML descarta o gráfico antes); `aplicarOpcao` recria a instância quando o canvas sumiu; o registro descarta o gráfico antes de KPI/tabela/pivot |

### Backend e integrações

| Sev. | Defeito | Correção |
|---|---|---|
| Crítico | **SSO do Superset nunca autenticava**: `(Usuario) req.getSession(false)` convertia a sessão em `Usuario` (`ClassCastException`, HTTP 500 em todo `auth_request`) | Cast corrigido em `SsoAnaliseServlet`; teste real com Mockito (antes era tautológico) |
| Crítico | Exportação CSV/XLSX do widget e ZIP do painel saíam **só com cabeçalho** (`dados = new JsonArray()`) | Executa a consulta pelo mesmo motor do painel |
| Crítico | Injeção SQL de segunda ordem: nomes do catálogo (editáveis pela tela) concatenados em `amostraTabela` e `verificarCardinalidade` | `MontadorSql.identificador` antes de montar o SQL |
| Crítico | Builder legado: colunas de `colunas_json` (gravado pelo usuário) entre colchetes sem escapar `]` | Lista de colunas aceita só identificadores simples |
| Alto | Corpos JSON da API lidos como ISO-8859-1: rótulos e filtros com acento gravados corrompidos (aparecem assim nos tooltips) | `CodificacaoUtf8Filter` em `/api/dash/*` |
| Alto | Histórico de versões e compartilhamentos sempre falhavam: `JOIN ... u.id` (a PK é `id_usuario`); a tela lia `autor`, o servidor enviava `autorNome` | SQL e chave corrigidos; data formatada; escape |
| Alto | Nome do arquivo exportado quebrava (`StringIndexOutOfBounds`) com acento ou parênteses | `ExportacaoService.nomeArquivo` (+ teste) |
| Alto | Superset: 34 dos 60 gráficos oficiais (30 big numbers e 4 pizzas) sem dados, pois liam `metric` e o provisionador gravava só `metrics` | `params["metric"]` para os tipos de métrica única |
| Alto | SweetAlert2 não carregado no modelador de datasets, nos KPIs e no builder: validar, salvar e publicar davam `ReferenceError` | Inclusão do padrão (`sweetalert2@11`) |

### Dashboard legado e tooltips de interface

| Sev. | Defeito | Correção |
|---|---|---|
| Alto | Calendário de intensidade em ISO-8859-1 dentro de página UTF-8 ("Ter�a-Feira", "S�bado") | Arquivo convertido para UTF-8 |
| Médio | "Comparativo diário passagens × infrações" empilhava grandezas diferentes (a altura somava passagens + infrações) | Barras lado a lado, `locale: 'pt-BR'` |
| Médio | Tooltip de "Apenas placas em comum" nunca inicializado e dentro do `<label>` (clicar no ícone alternava o filtro) | Botão fora do label + inicialização |
| Médio | Tooltip da consulta de veículos expunha "Para testes há dados para a data de 15/09/2025…" | Texto de teste removido; `tabindex`/`aria-label` |
| Médio | Tooltips com erros: "carcater coringa", "após a Confirmar a Irregularidade" | Textos revisados (`data-bs-title`) |
| Baixo | Botões só com ícone sem `title`/`aria-label` (menu do widget, menu do painel, remover condição/filtro/campo, zoom, fechar) | Rótulos acessíveis |
| Baixo | `aria-label="Example select with button addon"` (texto de exemplo do Bootstrap) | "Categoria" |

### Demonstração (painéis 101 e 102)

Script [`20261017_corrige_rotulos_demonstracao_dashboards.sql`](../banco-de-dados/migracoes/20261017_corrige_rotulos_demonstracao_dashboards.sql), **executado** (conferido em 2026-10-20 no `GTW_MURALHA_DEV`, ver [`migracoes/README.md`](../banco-de-dados/migracoes/README.md)):

- Todos os widgets usam os mesmos "últimos 12 meses". Antes, os KPIs somavam todo o histórico e as linhas só 12 meses, então os totais não batiam. O filtro global "De/Até" também só alcançava as linhas e áreas.
- "Vel. Média Infrações" dizia "km/h acima do limite", mas mostra a velocidade média considerada.
- "Alertas Ativos" contava todos os alertas.
- Rótulos de campo, títulos e nomes passam a ter acentuação.

## Verificação

| Verificação | Resultado |
|---|---|
| Testes Java dos pacotes `dashboards` e `analise.SsoAnalise` (compilados com Gson, POI 4.1.1, Servlet 3.0 e stubs do legado) | 101/101 |
| `SsoAnaliseServletTest` contra o servlet original | Falha com `ClassCastException`, como esperado |
| Harness Node + jsdom com o ECharts 5.5.0 vendorizado (SSR/SVG): 16 cenários de widgets, tooltips, drill e filtro cruzado | 16/16 |
| Harness do editor com o HTML real do `editor.jsp` e o SortableJS real (novo widget, sem painel, séries, KPI, pivot) | 5/5; o cenário "novo widget" e os 3 de edição falham no código original |
| **Navegador real** (Chromium 141 headless, Playwright, arraste com o mouse): soltar em zona certa/errada, repetição, ×, teclado, reordenar, soltar fora, trocar dataset, trocar o tipo com canvas e série conferidos a cada troca, "Ver como tabela"/"Atualizar", salvar, pivot, editar existente, sem erros de JS | 17/17; a troca de tipo falha no código anterior |
| `node --check` em todo JS alterado; `py_compile` em `provisionar.py` | OK |

**Não verificado aqui:** o build Maven completo (dependências no repositório interno da Consilux) e o navegador. Rodar `..\.setup-gtw\build.ps1` e testar os painéis 101 e 102 após a migração.

## Pendências (não corrigidas)

> **Atualização de 2026-10-20:** parte destas pendências foi tratada depois: as rotas `/api/dash/*` e o `GuestTokenServlet` foram registrados em `sis_menu` (`20261019_registro_modulos_novos.sql`) e passaram a exigir direito pela verificação estrita de `ControleAcesso`; o XSS de `kpis/index.jsp` foi corrigido; as procedures de recarga (`spu_carga_*`) e as views de mapa foram corrigidas no [gap da camada `analise`](gap-camada-analise-superset.md). Ver a [Entrega 35](35-correcoes-seguranca-funcionais.md). Os demais itens seguem abertos.

| Sev. | Item | Recomendação |
|---|---|---|
| Crítico | **Autorização inexistente em `/api/dash/*`**: `Acesso.verificaAcesso` libera quando a URI não está em `sis_menu` (`cnt == 0`), e nenhuma rota `/api/dash/*` está cadastrada | Cadastrar as ações em `sis_menu`/`sis_menu_direitos` (catálogo só para administradores) ou checar o grupo no servlet; responder 401/403 em JSON |
| Crítico | **IDOR**: compartilhar, revogar, excluir widget, atualizar layout, restaurar versão (de outro painel), salvar dataset de outro usuário; leitura de painéis, widgets, datasets e exportações sem checar o dono | Uma checagem única de quem pode ver ou editar, amarrando o pai no `WHERE` e conferindo as linhas afetadas |
| Crítico | **Chave do Google Maps versionada** em 17 arquivos (ex.: `pages/dashboards/dashboard.jsp:22`) | Revogar e recriar a chave, restringir por referrer e servir pela configuração (padrão do `MapaConfigServlet`) |
| Alto | Amostra do catálogo lê `TOP 5 *` de qualquer tabela cadastrada (ex.: `dbo.sis_usuario`, com `senha_hash`); a flag `sensivel` não é aplicada | Restringir a administradores e omitir colunas sensíveis |
| Alto | `salvarModelador` grava `id_usuario = 1` fixo; o modelador lê `id_tabela` e `coluna_origem`, mas o servidor envia camelCase (o dataset abre vazio) | Corrigir o contrato e gravar o usuário da sessão |
| Alto | Superset: filtros nativos com `datasetId` não resolvido, views de mapa com colunas inexistentes, RLS com coluna inexistente, procedures `sp_carga_*` × `spu_carga_*`, `X-Frame-Options: ALLOWALL`, `GuestTokenServlet` sem permissão | Ver o relatório da auditoria do Superset; portar os construtores do `demo_dashboards.py` para o `provisionar.py` |
| Médio | Mensagens de exceção do SQL Server devolvidas ao cliente; ausência de log4j no pacote | Mensagem genérica + log |
| Médio | Popup Leaflet e card do builder legado sem escape (`widget-renderer.js:49,166`); XSS menores em `datasets/index.jsp`, `dataset-editor.js`, `kpis/index.jsp` | Escapar texto vindo do banco |
| Médio | Tooltips Bootstrap 4 (`data-toggle`) nos botões de boletim e registro de fato, que nunca funcionam; Bootstrap carregado duas vezes em `tratar-alerta.jsp` | Migrar para `data-bs-*` e remover a carga duplicada |
| Baixo | CSV exportado com cabeçalho técnico e decimal com ponto | Usar os rótulos e o formato pt-BR |
| Baixo | Arquivos mortos: `pages/dashboards/assets/charts/apexcharts.min.js` (sem uso), `3d-bar/backup/` (inclui imagem de banco de imagens) | Remover |

## Arquivos

- **Widgets e núcleo:**
  - `static/vendor/sortablejs/Sortable.min.js` (SortableJS 1.15.0 real, no lugar do stub) e `LICENSE.txt`;
  - `static/js/dashboards/widgets/comum.js` (novo) e todos os renderizadores em `widgets/`;
  - `widget-registry.js`, `painel.js`, `editor-widget.js`, `editor-filtros.js`, `drill-down.js`, `filtro-cruzado.js`, `dataset-editor.js`.
- **Telas:**
  - `pages/analises/{widgets/editor.jsp, paineis/index.jsp, datasets/modelador.jsp, catalogo/index.jsp, manual.jsp}`;
  - `pages/dashboard/{kpis/index.jsp, builder/index.jsp, builder/assets/js/widget-config.js}`;
  - `pages/dashboards/{dashboard.jsp, calendario-intensidade.jsp, assets/js/grafico-comp-diario-fluxo-X-infracoes.js}`;
  - `pages/{veiculos-correlacionados, consulta-veiculo, consulta-veiculo-de-carga, alerta-tratativa}`.
- **Java:**
  - `CodificacaoUtf8Filter` e `SemCacheScriptsDashboardFilter` (novos), `WidgetService`, `ExportacaoDashboardServlet`, `ExportacaoService`, `HistoricoVersaoService`, `CompartilhamentoService`, `DashCatalogoServlet`, `DatasetServlet`;
  - `consulta/MontadorSql`, `consulta/PeriodoRelativo`, `dashboard/DashboardBuilderServlet`, `analise/SsoAnaliseServlet`.
- **Testes:** `PeriodoRelativoTest`, `ExportacaoServiceTest`, `SsoAnaliseServletTest`.
- **Superset:** `analise/superset/provisionar.py`.
- **Migração:** `docs/banco-de-dados/migracoes/20261017_corrige_rotulos_demonstracao_dashboards.sql`.
