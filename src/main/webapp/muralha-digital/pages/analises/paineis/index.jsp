<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<link rel="stylesheet" href="/static/vendor/gridstack/gridstack.min.css">
<link rel="stylesheet" href="/static/css/dashboards/print.css" media="print">
<style>
    .dash-area {
        min-height: calc(100vh - 200px);
        background: var(--bs-body-bg);
    }
    .widget-card {
        background: var(--bs-body-bg);
        border: 1px solid var(--bs-border-color);
        border-radius: 0.5rem;
        height: 100%;
        display: flex;
        flex-direction: column;
    }
    .widget-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.5rem 0.75rem;
        border-bottom: 1px solid var(--bs-border-color);
        font-size: 0.875rem;
        font-weight: 600;
        cursor: move;
    }
    .widget-body {
        flex: 1;
        padding: 0.5rem;
        position: relative;
        min-height: 0;
        overflow: hidden;
    }
    .widget-body .view-grafico {
        width: 100%;
        height: 100%;
    }
    .widget-footer {
        font-size: 0.75rem;
        color: var(--bs-secondary-color);
        padding: 0.25rem 0.75rem;
        text-align: right;
    }
    .widget-loading {
        display: flex;
        align-items: center;
        justify-content: center;
        position: absolute;
        inset: 0;
        background: var(--bs-body-bg);
        z-index: 2;
    }
    .widget-vazio {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100%;
        color: var(--bs-secondary-color);
    }
    .widget-erro {
        color: var(--bs-danger);
        padding: 1rem;
        text-align: center;
    }
    .dash-sidebar {
        border-right: 1px solid var(--bs-border-color);
        background: var(--bs-tertiary-bg);
    }
    .view-tabela { display: none; overflow: auto; height: 100%; }
    .view-tabela.active { display: block; }
    .view-grafico.hidden { display: none; }
    .grid-stack-item-content { padding: 0; }
</style>

<div class="container-fluid mt-3">
    <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb mb-0" style="font-size:0.85rem">
            <li class="breadcrumb-item"><a href="/muralha-digital/pages/analises/paineis/lista.jsp"><i class="bi bi-grid-1x2"></i> Paineis</a></li>
            <li class="breadcrumb-item active" id="breadcrumbPainel">Editor</li>
        </ol>
    </nav>
    <div class="row">
        <!-- Sidebar: lista de paineis -->
        <div class="col-md-2 dash-sidebar p-3" id="sidebar">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h6 class="mb-0"><i class="bi bi-grid-1x2"></i> Paineis</h6>
                <button class="btn btn-sm btn-primary" id="btnNovoPainel" title="Novo painel">
                    <i class="bi bi-plus-lg"></i>
                </button>
            </div>
            <div id="listaPaineis" class="list-group list-group-flush">
                <div class="text-center text-muted p-3">
                    <div class="spinner-border spinner-border-sm" role="status"></div>
                </div>
            </div>
        </div>

        <!-- Area principal -->
        <div class="col-md-10 p-0">
            <!-- Barra superior do painel -->
            <div class="d-flex align-items-center p-2 border-bottom bg-body-tertiary" id="barraPainel" style="display:none !important;">
                <h5 class="mb-0 me-3" id="tituloPainel">Selecione um painel</h5>
                <span id="badgeAtualizado" class="badge bg-success-subtle text-success ms-2" style="display:none;"></span>
                <div class="ms-auto d-flex gap-2 flex-wrap">
                    <select class="form-select form-select-sm" id="seletorRefresh" style="width:auto;">
                        <option value="0">Sem refresh</option>
                        <option value="60" selected>1 min</option>
                        <option value="300">5 min</option>
                        <option value="900">15 min</option>
                    </select>
                    <a href="/muralha-digital/pages/analises/manual.jsp" target="_blank" class="btn btn-sm btn-outline-info" title="Manual do Dashboard Builder">
                        <i class="bi bi-book"></i> Manual
                    </a>
                    <button class="btn btn-sm btn-outline-primary" id="btnAdicionarWidget">
                        <i class="bi bi-plus-square"></i> Widget
                    </button>
                    <div class="btn-group">
                        <button class="btn btn-sm btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown">
                            <i class="bi bi-gear"></i>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="#" id="btnVersionar"><i class="bi bi-clock-history"></i> Criar versao</a></li>
                            <li><a class="dropdown-item" href="#" id="btnHistorico"><i class="bi bi-list-ul"></i> Historico</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="#" id="btnDuplicar"><i class="bi bi-copy"></i> Duplicar</a></li>
                            <li><a class="dropdown-item" href="#" id="btnCompartilhar"><i class="bi bi-share"></i> Compartilhar</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="#" id="btnExportarCsvPainel"><i class="bi bi-filetype-csv"></i> Exportar todos (CSV)</a></li>
                            <li><a class="dropdown-item" href="#" id="btnImprimir"><i class="bi bi-printer"></i> Imprimir / PDF</a></li>
                        </ul>
                    </div>
                    <button class="btn btn-sm btn-outline-success" id="btnSalvarPainel">
                        <i class="bi bi-check-lg"></i> Salvar
                    </button>
                </div>
            </div>

            <!-- Filtros globais -->
            <div class="p-2 border-bottom bg-body-tertiary" id="filtrosGlobaisContainer" style="display:none;"></div>

            <!-- Cabecalho de impressao -->
            <div class="print-header" id="printHeader"></div>
            <div class="print-date" id="printDate"></div>

            <!-- Area do grid -->
            <div class="dash-area" id="dashArea">
                <div class="d-flex align-items-center justify-content-center h-100 text-muted" id="placeholderVazio">
                    <div class="text-center">
                        <i class="bi bi-bar-chart-line" style="font-size: 3rem;"></i>
                        <p class="mt-2">Selecione um painel na lateral ou crie um novo</p>
                    </div>
                </div>
                <div id="areaGrid" style="display:none; padding:0.5rem;">
                    <div class="grid-stack" id="gridStack"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal: historico de versoes -->
<div class="modal fade" id="modalHistorico" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="bi bi-clock-history"></i> Historico de Versoes</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body" id="listaVersoes">
                <div class="text-center text-muted p-3">Carregando...</div>
            </div>
        </div>
    </div>
</div>

<!-- Vendor -->
<script src="/static/vendor/echarts/echarts.min.js"></script>
<script src="/static/vendor/gridstack/gridstack-all.min.js"></script>
<script src="/static/vendor/ag-grid/ag-grid-community.min.js"></script>

<!-- Modulos do painel -->
<script type="module">
    import { inicializar, novoPainel, salvarPainel, adicionarWidget,
             versionar, duplicar, compartilhar, listarVersoes, restaurarVersao,
             exportarCsvPainel, imprimirPainel
    } from '/static/js/dashboards/painel.js';

    document.getElementById('btnNovoPainel').addEventListener('click', novoPainel);
    document.getElementById('btnSalvarPainel').addEventListener('click', salvarPainel);
    document.getElementById('btnAdicionarWidget').addEventListener('click', adicionarWidget);
    document.getElementById('btnVersionar').addEventListener('click', function(e) {
        e.preventDefault();
        versionar();
    });
    document.getElementById('btnDuplicar').addEventListener('click', function(e) {
        e.preventDefault();
        duplicar();
    });
    document.getElementById('btnCompartilhar').addEventListener('click', function(e) {
        e.preventDefault();
        compartilhar();
    });
    document.getElementById('btnExportarCsvPainel').addEventListener('click', function(e) {
        e.preventDefault();
        exportarCsvPainel();
    });
    document.getElementById('btnImprimir').addEventListener('click', function(e) {
        e.preventDefault();
        var titulo = document.getElementById('tituloPainel').textContent;
        document.getElementById('printHeader').textContent = titulo;
        document.getElementById('printDate').textContent = 'Impresso em: ' + new Date().toLocaleString('pt-BR');
        imprimirPainel();
    });
    document.getElementById('btnHistorico').addEventListener('click', async function(e) {
        e.preventDefault();
        var modal = new bootstrap.Modal(document.getElementById('modalHistorico'));
        modal.show();
        var el = document.getElementById('listaVersoes');
        el.innerHTML = '<div class="text-center"><div class="spinner-border spinner-border-sm"></div></div>';

        var versoes = await listarVersoes();
        if (!versoes || versoes.length === 0) {
            el.innerHTML = '<div class="text-center text-muted">Nenhuma versao salva</div>';
            return;
        }

        var html = '<div class="list-group list-group-flush">';
        for (var i = 0; i < versoes.length; i++) {
            var v = versoes[i];
            html += '<div class="list-group-item d-flex justify-content-between align-items-center">';
            html += '<div><strong>v' + (v.versao || v.id) + '</strong>';
            if (v.autor) html += ' <small class="text-muted">por ' + v.autor + '</small>';
            if (v.criadoEm) html += '<br><small class="text-muted">' + v.criadoEm + '</small>';
            html += '</div>';
            html += '<button class="btn btn-sm btn-outline-primary btn-restaurar" data-id="' + v.id + '">Restaurar</button>';
            html += '</div>';
        }
        html += '</div>';
        el.innerHTML = html;

        el.querySelectorAll('.btn-restaurar').forEach(function(btn) {
            btn.addEventListener('click', function() {
                if (confirm('Restaurar esta versao?')) {
                    restaurarVersao(parseInt(this.dataset.id));
                    modal.hide();
                }
            });
        });
    });

    document.getElementById('seletorRefresh').addEventListener('change', function() {
        salvarPainel();
    });

    inicializar();
</script>
