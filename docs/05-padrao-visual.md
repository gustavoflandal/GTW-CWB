# 05 — Padrão Visual (Design System do GTW / Muralha Digital)

> **Obrigatório** para toda tela nova ou alterada. O sistema é legado e tem inconsistências históricas; este documento fixa o **padrão a seguir** (extraído do cabeçalho oficial `cabecalho_bootstrap_simples.jsp`, das telas mais recentes e de contagens de uso reais no código) e registra o que **não** deve ser replicado. Quando houver conflito entre "o que existe" e "o que está aqui", vale este documento.

## 1. Princípios

1. **Bootstrap 5.3 puro como base.** Estilo próprio só para o que o Bootstrap não cobre; nunca recriar componentes.
2. **Sem novas bibliotecas de UI/ícones/fontes.** Usar apenas as listadas em §10.
3. **Tema claro** (`<html data-bs-theme="light">`). O modo escuro existe em `assets/js/modo-escuro*.js`, mas está **desativado** no cabeçalho — não ativar.
4. **Denso e funcional:** telas de operação (monitoramento, alertas) priorizam legibilidade de tabelas, filtros compactos e feedback imediato.
5. **Português do Brasil** em todos os textos, formatos `dd/MM/yyyy HH:mm:ss`, decimais com vírgula na exibição.
6. **Acessibilidade mínima:** `label for`, `aria-label` em botões só-ícone, `title` nos botões de ação, contraste ≥ 4.5:1 em texto.

## 2. Gerações visuais (qual usar)

| Geração | Uso | Fonte | Cor-base | Regra |
|---|---|---|---|---|
| **Muralha Digital (padrão atual)** | Tudo que é novo | Pilha padrão do Bootstrap 5.3 (`system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial, …`) | Azul `#0d75bf` (navbar/login) | **Seguir** |
| GTW clássico | Telas em `webapp/{cadastro,processo,…}` e GWT | `verdana, serif` 15px; controles 11px | Texto `#6b81a6`, cabeçalhos `#dadada`, menu GWT `#C3D9FF` | Somente manutenção; não copiar |
| Dashboards de relatório (2025) | Relatórios `pages/relatorios/*` | Bootstrap padrão | Azul escuro `#1e40af` + paleta Tailwind (ver §3.4) | Permitido **apenas** em relatórios/dashboards |

## 3. Cores

### 3.1 Marca / estrutura
| Token | Hex | Uso |
|---|---|---|
| `brand-primary` | **`#0d75bf`** | Navbar, dropdowns escuros da navbar, título do card de login, hover/realce de marca. É a cor institucional do Muralha |
| `bs-primary` | `#0d6efd` (padrão Bootstrap) | Botão `.btn-primary`, links, foco de inputs |
| `bs-success` | `#198754` | `.btn-success`, "Nova …", confirmações |
| `bs-danger` | `#dc3545` | Exclusão, badge de alertas (`.badge-custom`), erros |
| `bs-warning` | `#ffc107` | Atenção, `.btn-warning` (ex.: "Login com Google") |
| `bs-info` | `#0dcaf0` | Informações neutras |
| `bs-secondary` | `#6c757d` | Ações secundárias, rótulos auxiliares |
| `alert-accent` | **`orange`** (`#ffa500`) | Ícones/contadores de alerta na navbar (envelope e sino), menu de lembretes |
| `surface-page` | `#f8f9fa` | Fundo de áreas de dashboard e cabeçalhos de tabela claros |
| `border` | `#dee2e6` | Bordas (padrão Bootstrap) |
| `text-body` | `#212529` | Texto (padrão Bootstrap); `#6c757d` para auxiliar |

Regra: **usar as classes utilitárias do Bootstrap** (`btn-primary`, `text-danger`, `bg-light`, `table-light`…) antes de qualquer hex. Hex literais só para `#0d75bf` e para a paleta de dashboards.

### 3.2 Estados e severidade (alertas)
| Estado | Cor | Classe / Hex |
|---|---|---|
| Erro / crítico | vermelho | `danger` / `#dc3545` (dashboards: `#ef4444`, `#dc2626`) |
| Aviso | âmbar | `warning` / `#ffc107` (dashboards `#f59e0b`, `#d97706`) |
| Sucesso | verde | `success` / `#198754` (dashboards `#10b981`, `#059669`) |
| Informação | azul/ciano | `info` / `#0dcaf0` (dashboards `#0891b2`, `#06b6d4`) |
| Neutro | cinza | `secondary` / `#6c757d` |
Nunca comunicar estado **só por cor** — acompanhar de ícone e/ou texto.

### 3.3 Fundo do login
Imagens de `login/images/` (`abertura_muralha_*.jpg`, `Urban.png`…) com card `border-primary` e cabeçalho `#0d75bf` com texto branco.

### 3.4 Paleta de dashboards/relatórios (somente `pages/relatorios` e `dashboards`)
| Papel | Hex | Papel | Hex |
|---|---|---|---|
| Azul principal / botão primário custom | `#1e40af` (hover `#1e3a8a`) | Título de relatório / label | `#1e3a5f` |
| Verde | `#059669` / `#10b981` (hover `#047857`) | Subtítulo | `#64748b` |
| Âmbar | `#d97706` / `#f59e0b` | Borda de input | `#d1d5db` |
| Vermelho | `#dc2626` / `#ef4444` / `#dc3545` (hover `#b91c1c`) | Roxo | `#6f42c1` / `#8b5cf6` |
| Ciano | `#0891b2` / `#06b6d4` | Laranja | `#ea580c` / `#f97316` |
| Fundo da página | `#f8f9fa` | Foco de input | `border-color:#1e40af; box-shadow:0 0 0 .2rem rgba(30,64,175,.25)` |

Cartões de estatística: `.stat-card` (raio 10px, padding 25px, texto branco, sombra `0 4px 6px rgba(0,0,0,.1)`, min-height 120px) com variações `-blue -green -yellow -red -purple -cyan -orange`; valor `2.5rem bold`; ícone absoluto no canto superior direito (28px, opacidade .8). Séries de gráfico seguem a ordem azul → verde → âmbar → vermelho → roxo → ciano → laranja.

## 4. Tipografia

| Elemento | Padrão |
|---|---|
| Família | **Pilha padrão do Bootstrap 5.3** (não declarar `font-family` nas telas novas). **Não** usar Verdana/serif (GTW clássico), nem importar webfonts |
| Corpo | 1rem (16px), `line-height 1.5` |
| Título da tela | `<h2 class="text-center"><strong>Título</strong></h2>` (telas de consulta) ou `<h2 class="text-center mb-4">` (listagens) |
| Título de relatório | `1.6rem`, peso 600, cor `#1e3a5f`; subtítulo `.95rem` `#64748b` |
| Labels | `.form-label` (peso 600 nos relatórios) |
| Tabelas | `table-sm` para listas densas; cabeçalho `table-light` ou `table-dark` |
| Texto auxiliar | `<small>` / `.text-muted` |
| Mono (logs/código) | `Consolas, Monaco, 'Courier New', monospace` |
| Exceção histórica | `'Russo One'` aparece em 1 tela de vídeo-wall; não replicar |

## 5. Ícones

Duas famílias já carregadas — **escolher pela tela**:

| Família | Versão (CDN já usada) | Quando usar | Exemplos reais (frequência) |
|---|---|---|---|
| **Bootstrap Icons** (`bi bi-*`) | 1.11.3 (preferida; também há 1.7.2 e 1.10.5) | **Padrão para telas e botões de ação novos** | `bi-calendar`, `bi-file-earmark-excel`, `bi-file-earmark-pdf`, `bi-printer`, `bi-search`, `bi-bar-chart-line`, `bi-exclamation-triangle(-fill)`, `bi-info-circle`, `bi-eraser`, `bi-geo-alt`, `bi-clock-history`, `bi-check-circle`, `bi-x-lg`, `bi-arrow-left`, `bi-trash`, `bi-pen`, `bi-plus`, `bi-camera`, `bi-map`, `bi-car-front` |
| **Font Awesome 5.15.4** (`fas fa-*`) | 5.15.4 (navbar); 5.11.2/6.4.0 em telas pontuais; 4.7 em login/toastr | **Navbar e cabeçalhos existentes**; calendário do Tempus Dominus (`fas fa-calendar`) | `fa-user`, `fa-lock`, `fa-bell`, `fa-envelope`, `fa-search`, `fa-car`, `fa-sync-alt`, `fa-eye`, `fa-filter`, `fa-spinner fa-spin`, `fa-map-marker-alt`, `fa-clock`, `fa-file-pdf` |

Regras: não misturar famílias dentro do mesmo grupo de botões; tamanho padrão = o do botão (`btn-sm` → ícone herda); botão só-ícone leva `title` e `aria-label`. Spinner de carregamento em botão: `spinner-border spinner-border-sm`. Ícone SVG inline do Bootstrap Icons é aceito (usado em `modal-info-alert.js`).

Mapeamento ação → ícone (padrão): Pesquisar `bi-search`; Limpar `bi-eraser`; Excel `bi-file-earmark-excel`; PDF `bi-file-earmark-pdf`; Imprimir `bi-printer`; Voltar `bi-arrow-left`; Editar `bi-pen`; Excluir `bi-trash`; Restaurar `bi-arrow-counterclockwise`; Mapa `bi-map` / `bi-geo-alt`; Detalhe `bi-search`/`bi-eye`; Alerta `bi-exclamation-triangle-fill`; Info `bi-info-circle`; Data `bi-calendar`.

## 6. Layout de página

```
<body>
  [cabecalho_bootstrap_simples.jsp]  ← navbar azul #0d75bf (sempre incluir)
  <div class="container">  (ou container-fluid em listagens largas)
     <div class="row gy-3"><div class="col-sm-12"><h2 class="text-center"><strong>Título</strong></h2></div></div>
  </div>
  <div id="divFiltros" class="container">  filtros em .row.gy-3.mb-3 com .col-sm-N (grid de 12)
  [barra de botões de ação]  .btn-toolbar-acoes (margem 5px entre botões)
  <div id="error_container"></div>   ← alertas inline (bs_alert/bs_warning/…)
  <div class="table-responsive"> <table class="table table-hover table-sm">…</table> </div>
  [paginação twbsPagination em utils/paginacao]
  modais (include) ao final do <body>
  <div class="overlay"> (pagina-carregando) controlado por body.loading
```
- Grid: `container` > `row gy-3` > `col-sm-*`. Espaçamento vertical: `gy-3`, `mb-2/mb-3`, `.bottom-distance {margin-bottom:1.3rem}`.
- Margem lateral corrigida em dashboards: `padding-left:15px`.
- Responsividade: breakpoints do Bootstrap; telas operacionais assumem desktop ≥ 1280px, mas não quebrar abaixo de 768px.
- Nunca criar CSS global; CSS da tela em `assets/css/<tela>.css` e prefixado por ids/classes da tela.

## 7. Componentes

### 7.1 Navbar (`cabecalho_bootstrap_simples.jsp`)
`navbar navbar-expand-lg navbar-dark` com `style="background-color:#0d75bf"`; marca = `assets/images/gct_logo.png` (h=30). Dropdowns `dropdown-menu-dark` com fundo `#0d75bf` e itens `color:white`. À direita: busca rápida, envelope (contador de alertas não tratados, laranja), sino (alertas não assinados com `.badge-custom` vermelho 18px redondo), usuário (`fa-user`: Encerrar Sessão, Alterar Senha), nome do usuário e tempo restante de sessão (`∞` se "manter conectado"). Variantes: `_sem_menu`, `cabecalho_mdb_sem_menu` (MDB), `cabecalho_token_google`.

### 7.2 Botões
| Situação | Classe |
|---|---|
| Ação principal / Pesquisar / Salvar | `btn btn-primary` |
| Criar / Confirmar positivo / Exportar | `btn btn-success` |
| Cancelar / Fechar / Voltar | `btn btn-secondary` (ou `btn-outline-secondary`) |
| Excluir / Descartar | `btn btn-danger` (em linha de tabela: `btn-outline-danger`) |
| Atenção / Google | `btn btn-warning` |
| Limpar filtros | `btn btn-outline-secondary` |
| Ações em linha de tabela | `btn btn-outline-{primary|secondary|danger|success} btn-sm` com ícone e `title` |
Frequência real: `btn-sm` 172, `btn-primary` 157, `btn-success` 116, `btn-secondary` 83, `btn-outline-secondary` 55, `btn-warning` 53, `btn-danger` 46. Em relatórios podem usar `.btn-primary-custom / -success-custom / -danger-custom` (raio 8px, padding 10px 25px, peso 600, hover com `translateY(-1px)`).
Grupo de botões: `div.btn-toolbar-acoes` (margem 5px entre filhos). Botões de excluir/encerrar sempre pedem confirmação.

### 7.3 Formulários
- `.form-group` > `label.form-label` + `.form-control` / `.form-select`; checkbox `.form-check` > `.form-check-input` + `.form-check-label`.
- Multi-seleção com busca: **bootstrap-select 1.14** — `class="selectpicker form-control border" multiple data-live-search="true" data-actions-box="true" data-style="btn-white" data-size="10" data-none-selected-text="--Todos--" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"`.
- Data/hora: **Tempus Dominus 6.7.7** (`.input-group` com `data-td-target-input="nearest"`, `input[readonly]`, ícone `fas fa-calendar`); idioma/formatação via `moment-with-locales` e `assets/js/componente-data-hora.js`.
- Máscaras: `jquery.mask 1.14.16`; placa com curinga `assets/js/placa-caracter-coringa.js` (`*`), `maxlength=7`, `text-uppercase`.
- Tooltip: `data-bs-toggle="tooltip"` com `data-bs-placement`.
- Validação: HTML5 + mensagem inline; erros de servidor via alerta (§7.6).

### 7.4 Tabelas
- Listagem simples: `table table-striped align-middle` ou `table table-hover table-sm`; envolver em `.table-responsive`. Linha de estado vazio: `<td colspan="N" class="text-center">Nenhum … encontrado.</td>`; carregando: "Carregando…"; erro: `text-danger text-center`.
- Cabeçalho: `table-light` (claro) ou `table-dark`/`table-secondary` (enfatizado).
- Grandes volumes: **DataTables 1.11.5 (+ `dataTables.bootstrap5`)** ou paginação servidor com `twbsPagination` (`utils/paginacao`). Ordenação com `fa-sort`.
- Exportação: SheetJS (Excel) e jsPDF + autotable (PDF) — botões `bi-file-earmark-excel` / `bi-file-earmark-pdf` (CSS `assets/css/botoes-exportar-lista.css`).
- Dados sempre escapados ao montar HTML por template string (evitar XSS: nunca inserir texto vindo do servidor sem `.text()`/escape).

### 7.5 Modais
Estrutura Bootstrap 5: `.modal.fade` > `.modal-dialog.modal-lg.modal-dialog-centered` (usar `modal-xl` para mapas/detalhes, `modal-dialog-scrollable` para conteúdo longo, padrão sem sufixo para confirmações curtas) > `.modal-content` > `.modal-header` (`h5.modal-title` + `button.btn-close`), `.modal-body`, `.modal-footer` (Fechar `btn-secondary` à esquerda do primário). Abrir com `new bootstrap.Modal(el, {backdrop:'static', keyboard:false}).show()`. Conteúdo do modal em JSP própria (`modal-*.jsp`) incluída com `<%@ include file=… %>`. Modal de aviso genérico: `utils/modal-info-alert.jsp`.

### 7.6 Feedback ao usuário
| Tipo | Componente | Padrão |
|---|---|---|
| Confirmação/sucesso/erro pontual | **SweetAlert2 v11** (`Swal.fire`, 140 usos) | `confirmButtonColor:'#3085d6'`, cancelar `'#d33'`; ícone `success|error|warning|question|info`; textos em PT-BR |
| Mensagem inline no contêiner da tela | `#error_container` + `AlertCsx / WarningCsx / InfoCsx / SuccessCsx` (`utils/modal-info-alert.js`) | alert Bootstrap dispensável com ícone SVG |
| Notificação em tempo real | **toastr 2.1.3** (`utils/notificacao`) | alerta de veículo: miniatura 120px `object-fit:contain`, ícone FontAwesome 4 |
| Carregando página/ação longa | overlay `assets/css/pagina-carregando.css` (`body.loading .overlay`, fundo `rgba(255,255,255,.8)` + GIF) ou `spinner-border` | |
| Som | `assets/sounds` + `ConfiguracaoSons` | só para alertas críticos |
Não usar `alert()`/`confirm()` do navegador em telas novas (existe legado em `listagem-guarnicoes.js`; substituir por `Swal.fire` ao tocar nele). `bootbox` está disponível mas obsoleto.

### 7.7 Gráficos e mapas
- **Chart.js** (3.6.2 / jsdelivr latest) + `chartjs-plugin-datalabels 2.0.0`; cores da paleta §3.4; sempre título, legenda e unidade.
- **Google Maps JS** (chave via `maps-config.js`) para mapas operacionais; **Leaflet 1.9.3** para rotas/relatórios; mapa de calor via biblioteca do Maps.
- Ícones de marcadores usam as imagens de `muralha-digital/assets/images/`.

### 7.8 Badges e contadores
`badge-custom`: círculo `#dc3545`, texto branco 9px negrito, 18×18, sombra `0 0 3px rgba(0,0,0,.4)`, posicionado `top:3px; right:-7px`.

### 7.9 Estados vazios, carregamento e erro
Mensagem centralizada em linha de tabela; nunca deixar a tabela em branco. Erros de rede: `console.error` + mensagem ao usuário (não só log).

## 8. Imagens e identidade
- Logo no cabeçalho: `muralha-digital/assets/images/gct_logo.png` (também `gct_logo_transparent.png`, `cabecalho_gct.png`).
- Logos de cliente (multi-órgão): `consilux_*_transparent_<cliente>.png` em `assets/images/` e `src/main/resources/imagens/logo_<cliente>.png` (usados em relatórios Jasper/PDF).
- Imagem “sem veículo”: `consilux_imagem_sem_veiculo.png`.

## 9. Checklist de revisão visual (copiar para o PR)
- [ ] Inclui `cabecalho_bootstrap_simples.jsp` e `<html lang="pt-BR" data-bs-theme="light">`
- [ ] Apenas Bootstrap 5.3 + libs da §10; sem nova dependência
- [ ] Título `h2.text-center` e grid `container/row/col-sm-*`
- [ ] Botões conforme §7.2 com ícone Bootstrap Icons + `title`
- [ ] Filtros com `selectpicker`/Tempus Dominus conforme §7.3
- [ ] Tabela `table-hover table-sm` em `.table-responsive`, estados vazio/erro/carregando
- [ ] Modais conforme §7.5; confirmações com SweetAlert2
- [ ] Sem `font-family` custom, sem hex fora das paletas, sem `!important` desnecessário
- [ ] CSS/JS da tela em `assets/` da própria tela; nada global
- [ ] Textos PT-BR; datas `dd/MM/yyyy`; foco e `aria-label` em botões só-ícone

## 10. Bibliotecas front-end permitidas (versões em uso)
Bootstrap 5.3.0/5.3.2/5.3.3 (CDN jsdelivr; `popper.min.js` local) · jQuery 3.6.0 (local `assets/jquery`) · Font Awesome 5.15.4 (+4.7/5.11.2/6.4.0 pontuais) · Bootstrap Icons 1.11.3 (+1.7.2/1.10.5) · bootstrap-select 1.14.0-beta2 · SweetAlert2 11 · Tempus Dominus 6.7.7 + moment · DataTables 1.11.5/1.13.4 (BS5) · Chart.js + datalabels · Google Maps JS · Leaflet 1.9.3 · SheetJS 0.18.5/0.20.1 · jsPDF 2.5.1 + autotable 3.5.23 · twbsPagination · toastr 2.1.3 · jquery.mask 1.14.16 · select2 4.1 (pontual; preferir bootstrap-select) · MDB-Free (`assets/MDB-Free`, legado, só nas telas que já o usam).

> Inconsistências conhecidas (não replicar): três versões de Bootstrap simultâneas, jQuery 1.10 duplicado em `consulta-veiculo` (freewall), FA 4/5/6 misturados, `@charset ISO-8859-1` em CSS antigos, estilos inline com `!important`. Ao tocar numa tela antiga, alinhe ao padrão sem reescrevê-la inteira.
