<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<style>
    .zona-drop {
        min-height: 48px;
        border: 2px dashed var(--bs-border-color);
        border-radius: 0.375rem;
        padding: 0.5rem;
        background: var(--bs-tertiary-bg);
        transition: background 0.15s;
    }
    .zona-drop:empty::after {
        content: 'Arraste campos aqui';
        color: var(--bs-secondary-color);
        font-size: 0.8rem;
    }
    .zona-drop.dragover { background: rgba(13,117,191,0.08); }
    .campo-arraste {
        cursor: grab;
        user-select: none;
        padding: 0.25rem 0.5rem !important;
        font-size: 0.8rem !important;
    }
    .campo-arraste:active { cursor: grabbing; }
    .tipo-check .form-check-label { cursor: pointer; padding: 0.25rem 0.5rem; border-radius: 0.25rem; }
    .tipo-check .form-check-input:checked + .form-check-label { background: rgba(13,117,191,0.1); font-weight: 600; }
    #previewWidget { min-height: 300px; border: 1px solid var(--bs-border-color); border-radius: 0.375rem; }
</style>

<div class="container-fluid py-3">
    <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb mb-0" style="font-size:0.85rem">
            <li class="breadcrumb-item"><a href="/muralha-digital/pages/analises/paineis/lista.jsp"><i class="bi bi-grid-1x2"></i> Paineis</a></li>
            <li class="breadcrumb-item" id="breadcrumbPainel"><a href="#" id="linkPainel">Painel</a></li>
            <li class="breadcrumb-item active" id="breadcrumbWidget">Editor de Widget</li>
        </ol>
    </nav>
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="mb-0" id="tituloEditor">
            <i class="bi bi-palette me-2"></i>Novo Widget
        </h4>
        <div class="d-flex gap-1 align-items-center">
            <span id="msgSalvo" class="text-success me-2" style="display:none"></span>
            <a href="/muralha-digital/pages/analises/manual.jsp" target="_blank" class="btn btn-outline-info btn-sm" title="Manual do Dashboard Builder">
                <i class="bi bi-book"></i> Manual
            </a>
            <button class="btn btn-primary btn-sm" id="btnSalvar"><i class="bi bi-save"></i> Salvar</button>
            <button class="btn btn-success btn-sm" id="btnSalvarFechar"><i class="bi bi-check-lg"></i> Salvar e Voltar</button>
        </div>
    </div>

    <div class="row g-3">
        <!-- Coluna esquerda: configuracao -->
        <div class="col-lg-5">
            <div class="card mb-3">
                <div class="card-body">
                    <div class="row g-2">
                        <div class="col-md-6">
                            <label class="form-label">Dataset</label>
                            <select class="form-select form-select-sm" id="selDataset">
                                <option value="">Selecione...</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Titulo</label>
                            <input type="text" class="form-control form-control-sm" id="inpTitulo" placeholder="Titulo do widget">
                        </div>
                    </div>
                </div>
            </div>

            <!-- Tipo de visualizacao -->
            <div class="card mb-3">
                <div class="card-header py-2"><small class="fw-bold">Tipo de Visualizacao</small></div>
                <div class="card-body tipo-check" id="seletorTipos"></div>
            </div>

            <!-- Campos disponiveis -->
            <div class="card mb-3">
                <div class="card-header py-2"><small class="fw-bold">Campos Disponiveis</small></div>
                <div class="card-body">
                    <div class="mb-2">
                        <small class="text-muted">Dimensoes</small>
                        <div id="listaDimensoes" class="d-flex flex-wrap"></div>
                    </div>
                    <div class="mb-2">
                        <small class="text-muted">Medidas</small>
                        <div id="listaMedidas" class="d-flex flex-wrap"></div>
                    </div>
                    <div>
                        <small class="text-muted">Tempo</small>
                        <div id="listaTempos" class="d-flex flex-wrap"></div>
                    </div>
                </div>
            </div>

            <!-- Zonas de arrastar (graficos normais) -->
            <div class="card mb-3" id="painelZonasNormais">
                <div class="card-header py-2"><small class="fw-bold">Eixos</small></div>
                <div class="card-body">
                    <div class="mb-2">
                        <small class="text-muted">Eixo X / Dimensoes</small>
                        <div class="zona-drop" id="zonaDimensoes"></div>
                    </div>
                    <div class="mb-2">
                        <small class="text-muted">Eixo Y / Medidas</small>
                        <div class="zona-drop" id="zonaMedidas"></div>
                    </div>
                    <div>
                        <small class="text-muted">Cores / Series</small>
                        <div class="zona-drop" id="zonaSeries"></div>
                    </div>
                </div>
            </div>

            <!-- Zonas de pivot (tabela dinamica) -->
            <div class="card mb-3" id="painelPivot" style="display:none">
                <div class="card-header py-2"><small class="fw-bold">Configuracao Pivot</small></div>
                <div class="card-body">
                    <div class="mb-2">
                        <small class="text-muted">Linhas</small>
                        <div class="zona-drop" id="zonaPivotLinhas"></div>
                    </div>
                    <div class="mb-2">
                        <small class="text-muted">Colunas</small>
                        <div class="zona-drop" id="zonaPivotColunas"></div>
                    </div>
                    <div class="mb-2">
                        <small class="text-muted">Valores (medidas)</small>
                        <div class="zona-drop" id="zonaPivotValores"></div>
                    </div>
                    <div class="form-check mt-2">
                        <input class="form-check-input" type="checkbox" id="chkSubtotais" checked>
                        <label class="form-check-label" for="chkSubtotais">Incluir subtotais</label>
                    </div>
                </div>
            </div>

            <!-- Dimensao temporal -->
            <div class="card mb-3" id="painelTimeDimension" style="display:none">
                <div class="card-header py-2"><small class="fw-bold">Dimensao Temporal</small></div>
                <div class="card-body">
                    <div class="row g-2">
                        <div class="col-md-6">
                            <label class="form-label">Granularidade</label>
                            <select class="form-select form-select-sm" id="selGranularidade" title="Agrupa os valores por período. &quot;Sem agrupamento&quot; usa a data apenas para filtrar o período.">
                                <option value="">Sem agrupamento</option>
                                <option value="day">Dia</option>
                                <option value="week">Semana</option>
                                <option value="month" selected>Mes</option>
                                <option value="quarter">Trimestre</option>
                                <option value="year">Ano</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Periodo</label>
                            <select class="form-select form-select-sm" id="selDateRange">
                                <option value="">Todo o período</option>
                                <option value="today">Hoje</option>
                                <option value="yesterday">Ontem</option>
                                <option value="last 7 days">Ultimos 7 dias</option>
                                <option value="last 30 days">Ultimos 30 dias</option>
                                <option value="this month">Este mes</option>
                                <option value="last month">Mes passado</option>
                                <option value="last 3 months">Ultimos 3 meses</option>
                                <option value="last 6 months">Ultimos 6 meses</option>
                                <option value="last 12 months" selected>Ultimos 12 meses</option>
                                <option value="this year">Este ano</option>
                                <option value="last year">Ano passado</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Filtros -->
            <div class="card mb-3">
                <div class="card-header py-2"><small class="fw-bold">Filtros</small></div>
                <div class="card-body" id="containerFiltros"></div>
            </div>

            <!-- Ordenacao e limite -->
            <div class="card mb-3">
                <div class="card-body">
                    <div class="row g-2">
                        <div class="col-md-6">
                            <label class="form-label">Limite (Top N)</label>
                            <input type="number" class="form-control form-control-sm" id="inpLimite" value="20" min="1" max="10000">
                        </div>
                    </div>
                </div>
            </div>

            <!-- Opcoes visuais -->
            <div class="card mb-3">
                <div class="card-header py-2"><small class="fw-bold">Opcoes Visuais</small></div>
                <div class="card-body" id="opcoesVisuais"></div>
            </div>
        </div>

        <!-- Coluna direita: preview -->
        <div class="col-lg-7">
            <div class="card">
                <div class="card-header py-2 d-flex justify-content-between align-items-center">
                    <small class="fw-bold">Preview</small>
                    <div>
                        <button class="btn btn-outline-secondary btn-sm" id="btnVerTabela">
                            <i class="bi bi-table"></i> Ver como tabela
                        </button>
                        <button class="btn btn-outline-primary btn-sm" id="btnPreview">
                            <i class="bi bi-arrow-clockwise"></i> Atualizar
                        </button>
                    </div>
                </div>
                <div class="card-body p-0">
                    <div id="previewWidget">
                        <div class="text-muted text-center p-4">
                            Selecione um dataset e arraste campos para os eixos.
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="/static/vendor/sortablejs/Sortable.min.js"></script>
<script src="/static/vendor/echarts/echarts.min.js"></script>
<script src="/static/vendor/ag-grid/ag-grid-community.min.js"></script>
<script type="module" src="/static/js/dashboards/editor-widget.js"></script>
