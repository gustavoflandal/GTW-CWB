<!DOCTYPE html>

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="pt-BR">
<head>
    <title>Dashboard Builder</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">
    <link rel="stylesheet" href="assets/css/builder.css">

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
</head>
<body>

<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %>

<div class="db-container">

    <!-- Sidebar: lista de paineis -->
    <div class="db-sidebar">
        <div class="db-sidebar-header d-flex align-items-center justify-content-between">
            <h6><i class="bi bi-grid-1x2 me-1"></i> Paineis</h6>
            <button class="btn btn-sm btn-light" onclick="novoPainel()" title="Novo Painel">
                <i class="bi bi-plus-lg"></i>
            </button>
        </div>
        <div class="db-painel-list" id="painelList">
            <div class="text-center text-muted p-3">
                <small>Carregando...</small>
            </div>
        </div>
    </div>

    <!-- Area principal -->
    <div class="db-main">
        <div class="db-toolbar" id="toolbar" style="display:none;">
            <h5 id="painelNome">-</h5>
            <span class="badge bg-secondary" id="widgetCount" title="Widgets no painel"></span>
            <span class="db-refresh-indicator" id="refreshIndicator"></span>
            <div class="ms-auto d-flex gap-1">
                <button class="btn btn-sm btn-outline-primary" onclick="abrirModalWidget()" title="Adicionar Widget">
                    <i class="bi bi-plus-circle me-1"></i> Widget
                </button>
                <button class="btn btn-sm btn-outline-secondary" onclick="editarPainel()" title="Configuracoes do Painel">
                    <i class="bi bi-gear"></i>
                </button>
                <button class="btn btn-sm btn-outline-info" onclick="refreshAllWidgets()" title="Atualizar agora">
                    <i class="bi bi-arrow-clockwise"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger" onclick="excluirPainel()" title="Excluir Painel">
                    <i class="bi bi-trash"></i>
                </button>
            </div>
        </div>
        <div class="db-grid-area" id="gridArea">
            <div class="db-empty-state" id="emptyState">
                <i class="bi bi-grid-1x2"></i>
                <p>Selecione ou crie um painel</p>
            </div>
            <div class="db-grid" id="widgetGrid" style="display:none;"></div>
        </div>
    </div>
</div>

<!-- Modal: Novo/Editar Painel -->
<div class="modal fade" id="modalPainel" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="modalPainelTitle">Novo Painel</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="painelId">
                <div class="mb-3">
                    <label class="form-label">Nome</label>
                    <input type="text" class="form-control" id="painelNomeInput" maxlength="80" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Descricao</label>
                    <input type="text" class="form-control" id="painelDescInput" maxlength="200">
                </div>
                <div class="mb-3">
                    <label class="form-label">Auto-refresh (segundos)</label>
                    <input type="number" class="form-control" id="painelRefreshInput" min="30" max="300" value="60">
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="painelPublicoInput">
                    <label class="form-check-label">Painel publico (visivel para todos)</label>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-primary" onclick="salvarPainel()">Salvar</button>
            </div>
        </div>
    </div>
</div>

<!-- Modal: Novo/Editar Widget -->
<div class="modal fade" id="modalWidget" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title">Configurar Widget</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="widgetId">
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Titulo</label>
                        <input type="text" class="form-control" id="wTitulo" maxlength="80" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Tipo</label>
                        <select class="form-select" id="wTipo" onchange="tipoWidgetChanged()">
                            <option value="CARD">Card KPI</option>
                            <option value="CHART">Grafico</option>
                            <option value="TABLE">Tabela</option>
                            <option value="MAP">Mapa</option>
                        </select>
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label">Dataset</label>
                    <select class="form-select" id="wDataset" onchange="datasetChanged()">
                        <option value="">Selecione...</option>
                    </select>
                </div>

                <!-- Campos dinamicos por tipo -->
                <div id="camposCard">
                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label class="form-label">Coluna Valor</label>
                            <select class="form-select" id="wColunaValor"></select>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label">Agregacao</label>
                            <select class="form-select" id="wAgregacao">
                                <option value="SUM">SUM</option>
                                <option value="AVG">AVG</option>
                                <option value="COUNT">COUNT</option>
                                <option value="MAX">MAX</option>
                                <option value="MIN">MIN</option>
                            </select>
                        </div>
                        <div class="col-md-2 mb-3">
                            <label class="form-label">Th. OK</label>
                            <input type="number" class="form-control" id="wThOk" step="any">
                        </div>
                        <div class="col-md-2 mb-3">
                            <label class="form-label">Th. Warn</label>
                            <input type="number" class="form-control" id="wThWarn" step="any">
                        </div>
                    </div>
                </div>

                <div id="camposChart" style="display:none;">
                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label class="form-label">Coluna Label (eixo X)</label>
                            <select class="form-select" id="wColunaLabel"></select>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label">Coluna Valor (eixo Y)</label>
                            <select class="form-select" id="wColunaValorChart"></select>
                        </div>
                        <div class="col-md-2 mb-3">
                            <label class="form-label">Agregacao</label>
                            <select class="form-select" id="wAgregacaoChart">
                                <option value="SUM">SUM</option>
                                <option value="AVG">AVG</option>
                                <option value="COUNT">COUNT</option>
                                <option value="MAX">MAX</option>
                                <option value="MIN">MIN</option>
                            </select>
                        </div>
                        <div class="col-md-2 mb-3">
                            <label class="form-label">Tipo Grafico</label>
                            <select class="form-select" id="wTipoGrafico">
                                <option value="bar">Barra</option>
                                <option value="line">Linha</option>
                                <option value="pie">Pizza</option>
                                <option value="doughnut">Rosca</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div id="camposTable" style="display:none;">
                    <div class="mb-3">
                        <label class="form-label">Colunas a exibir</label>
                        <div id="wColunasTabela" class="d-flex flex-wrap gap-2"></div>
                    </div>
                </div>

                <div id="camposMap" style="display:none;">
                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label class="form-label">Coluna Latitude</label>
                            <select class="form-select" id="wColunaLat"></select>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label">Coluna Longitude</label>
                            <select class="form-select" id="wColunaLng"></select>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label">Coluna Label (tooltip)</label>
                            <select class="form-select" id="wColunaLabelMap"></select>
                        </div>
                    </div>
                </div>

                <!-- Filtros -->
                <div class="mb-3">
                    <label class="form-label">Filtros <button type="button" class="btn btn-sm btn-outline-secondary" onclick="adicionarFiltro()"><i class="bi bi-plus"></i></button></label>
                    <div id="filtrosContainer"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-primary" onclick="salvarWidget()">Salvar</button>
            </div>
        </div>
    </div>
</div>

<script>
var baseUrl = '<%= request.getContextPath() %>';
var urlBuilder  = baseUrl + '/MuralhaDigital/DashboardBuilder';
var urlDataset  = baseUrl + '/MuralhaDigital/Dataset';
</script>
<script src="assets/js/dashboard-grid.js"></script>
<script src="assets/js/widget-renderer.js"></script>
<script src="assets/js/widget-config.js"></script>

</body>
</html>
