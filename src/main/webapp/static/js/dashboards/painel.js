import { api } from './api.js';
import { estado } from './estado.js';
import { renderizarWidget } from './widget-registry.js';
import { inicializarFiltrosGlobais, aplicarFiltrosAConsulta } from './filtros-globais.js';
import { inicializarFiltroCruzado, aplicarFiltroCruzadoAConsulta } from './filtro-cruzado.js';
import { inicializarDrillDown, aplicarDrillAConsulta } from './drill-down.js';

var painelAtual = null;
var grid = null;
var refreshInterval = null;
var badgeAtualizadoTimer = null;
var ultimaAtualizacao = null;
var filtrosGlobais = null;
var filtroCruzado = null;
var drillDown = null;
var chartsAtivos = {};

export async function inicializar() {
    filtroCruzado = inicializarFiltroCruzado({
        onFiltroAlterado: function() { recarregarTodosWidgets(); }
    });

    drillDown = inicializarDrillDown({
        onDrill: function(widgetId) { recarregarWidget(widgetId); }
    });

    await carregarListaPaineis();

    var params = new URLSearchParams(window.location.search);
    var id = params.get('id');
    if (id) abrirPainel(parseInt(id));
}

async function carregarListaPaineis() {
    var r = await api.get('/api/dash/paineis?acao=listar');
    if (!r.ok) return;

    var el = document.getElementById('listaPaineis');
    if (!el) return;
    el.innerHTML = '';

    for (var i = 0; i < r.paineis.length; i++) {
        var p = r.paineis[i];
        var btn = document.createElement('a');
        btn.className = 'list-group-item list-group-item-action d-flex justify-content-between align-items-center';
        btn.href = '#';
        btn.dataset.id = p.id;

        var nomeSpan = document.createElement('span');
        nomeSpan.textContent = p.nome;
        btn.appendChild(nomeSpan);

        if (p.proprio) {
            var badge = document.createElement('span');
            badge.className = 'badge bg-primary rounded-pill';
            badge.textContent = 'Meu';
            btn.appendChild(badge);
        } else {
            var badgeComp = document.createElement('span');
            badgeComp.className = 'badge bg-info rounded-pill';
            badgeComp.textContent = 'Comp.';
            btn.appendChild(badgeComp);
        }

        btn.addEventListener('click', function(e) {
            e.preventDefault();
            abrirPainel(parseInt(this.dataset.id));
        });
        el.appendChild(btn);
    }

    if (r.paineis.length === 0) {
        el.innerHTML = '<div class="text-center text-muted p-3">Nenhum painel criado</div>';
    }
}

export async function abrirPainel(id) {
    var r = await api.get('/api/dash/paineis?acao=obter&id=' + id);
    if (!r.ok) {
        mostrarErro(r.erro || 'Erro ao carregar painel');
        return;
    }

    painelAtual = r.painel;
    estado.set({ painel: r.painel });

    document.getElementById('tituloPainel').textContent = r.painel.nome;
    var breadcrumb = document.getElementById('breadcrumbPainel');
    if (breadcrumb) breadcrumb.textContent = r.painel.nome;
    document.getElementById('barraPainel').style.display = 'flex';
    document.getElementById('placeholderVazio').style.display = 'none';
    document.getElementById('areaGrid').style.display = 'block';
    document.getElementById('seletorRefresh').value = r.painel.refreshSeg || 0;

    inicializarFiltrosGlobaisUI(r.painel);
    inicializarGrid();
    renderizarWidgetsNoGrid(r.painel.widgets || []);
    configurarRefresh(r.painel.refreshSeg);
    atualizarBadgeAtualizado();

    marcarPainelAtivoNaLista(id);
}

function inicializarFiltrosGlobaisUI(painel) {
    var container = document.getElementById('filtrosGlobaisContainer');
    if (!container) return;

    filtrosGlobais = inicializarFiltrosGlobais(container, {
        onAplicar: function() { recarregarTodosWidgets(); }
    });

    if (painel.filtros) {
        // futuramente carregar campos dos datasets usados
    }
}

function inicializarGrid() {
    if (grid) {
        grid.destroy(true);
    }

    var el = document.getElementById('gridStack');
    if (!el) return;
    el.innerHTML = '';

    grid = GridStack.init({
        column: 12,
        cellHeight: 80,
        margin: 8,
        float: false,
        animate: true,
        disableResize: false,
        disableDrag: false
    }, el);

    grid.on('change', function(items) {
        salvarLayoutDebounced();
    });
}

function renderizarWidgetsNoGrid(widgets) {
    if (!grid) return;
    grid.removeAll(true);
    chartsAtivos = {};

    for (var i = 0; i < widgets.length; i++) {
        adicionarWidgetAoGrid(widgets[i]);
    }
}

function adicionarWidgetAoGrid(w) {
    var cardEl = criarCardWidget(w);

    grid.addWidget({
        id: String(w.id),
        x: w.gridX || 0,
        y: w.gridY || 0,
        w: w.gridW || 4,
        h: w.gridH || 3,
        el: cardEl
    });

    carregarDadosWidget(w);
}

function criarCardWidget(w) {
    var card = document.createElement('div');
    card.className = 'widget-card';
    card.setAttribute('data-widget-id', w.id);

    card.innerHTML =
        '<div class="widget-header">' +
            '<span>' + escapeHtml(w.titulo || 'Widget') + '</span>' +
            '<div class="dropdown">' +
                '<button class="btn btn-sm btn-link p-0 text-body-secondary" data-bs-toggle="dropdown">' +
                    '<i class="bi bi-three-dots-vertical"></i>' +
                '</button>' +
                '<ul class="dropdown-menu dropdown-menu-end">' +
                    '<li><a class="dropdown-item acao-editar" href="#" data-wid="' + w.id + '">' +
                        '<i class="bi bi-pencil"></i> Editar</a></li>' +
                    '<li><a class="dropdown-item acao-tabela" href="#" data-wid="' + w.id + '">' +
                        '<i class="bi bi-table"></i> Ver como tabela</a></li>' +
                    '<li><a class="dropdown-item acao-atualizar" href="#" data-wid="' + w.id + '">' +
                        '<i class="bi bi-arrow-clockwise"></i> Atualizar</a></li>' +
                    '<li><hr class="dropdown-divider"></li>' +
                    '<li><a class="dropdown-item acao-csv" href="#" data-wid="' + w.id + '">' +
                        '<i class="bi bi-filetype-csv"></i> Exportar CSV</a></li>' +
                    '<li><a class="dropdown-item acao-xlsx" href="#" data-wid="' + w.id + '">' +
                        '<i class="bi bi-file-earmark-excel"></i> Exportar XLSX</a></li>' +
                    '<li><hr class="dropdown-divider"></li>' +
                    '<li><a class="dropdown-item text-danger acao-remover" href="#" data-wid="' + w.id + '">' +
                        '<i class="bi bi-trash"></i> Remover</a></li>' +
                '</ul>' +
            '</div>' +
        '</div>' +
        '<div class="widget-body">' +
            '<div class="view-grafico" id="grafico-' + w.id + '" style="width:100%;height:100%"></div>' +
            '<div class="view-tabela" id="tabela-' + w.id + '"></div>' +
            '<div class="widget-loading">' +
                '<div class="spinner-border text-primary spinner-border-sm" role="status"></div>' +
            '</div>' +
        '</div>' +
        '<div class="widget-footer" id="footer-' + w.id + '"></div>';

    card.addEventListener('click', function(e) {
        var target = e.target.closest('[data-wid]');
        if (!target) return;
        e.preventDefault();
        var wid = parseInt(target.dataset.wid);

        if (target.classList.contains('acao-editar')) {
            editarWidget(wid);
        } else if (target.classList.contains('acao-tabela')) {
            toggleTabela(wid);
        } else if (target.classList.contains('acao-atualizar')) {
            recarregarWidget(wid);
        } else if (target.classList.contains('acao-csv')) {
            exportarCsvWidget(wid);
        } else if (target.classList.contains('acao-xlsx')) {
            exportarXlsxWidget(wid);
        } else if (target.classList.contains('acao-remover')) {
            removerWidget(wid);
        }
    });

    return card;
}

async function carregarDadosWidget(widget) {
    var inicio = performance.now();
    var graficoEl = document.getElementById('grafico-' + widget.id);
    var footerEl = document.getElementById('footer-' + widget.id);
    var bodyEl = graficoEl ? graficoEl.closest('.widget-body') : null;
    var loadingEl = bodyEl ? bodyEl.querySelector('.widget-loading') : null;

    if (loadingEl) loadingEl.style.display = 'flex';

    try {
        var consulta = widget.consulta || {};

        if (filtrosGlobais) {
            consulta = aplicarFiltrosAConsulta(consulta, filtrosGlobais.obterFiltros());
        }

        if (filtroCruzado && filtroCruzado.estaAtivo()) {
            var fc = filtroCruzado.obterFiltro();
            var origem = fc ? (painelAtual.widgets || []).find(function(w) { return w.id === fc.widgetId; }) : null;
            if (fc && fc.widgetId !== widget.id && origem && origem.idDataset === widget.idDataset) {
                consulta = aplicarFiltroCruzadoAConsulta(consulta, fc);
            }
        }

        var drill = drillDown ? drillDown.obterNivel(widget.id) : null;
        if (drill) {
            consulta = aplicarDrillAConsulta(consulta, drill);
        }

        var r = await api.post('/api/dash/consultas', {
            widgetId: widget.id,
            datasetId: widget.idDataset,
            query: consulta
        });

        var tempoMs = Math.round(performance.now() - inicio);
        var cacheHit = r.cacheHit || false;
        if (footerEl) {
            var footerHtml = '<span>' + tempoMs + 'ms</span>';
            if (cacheHit) {
                footerHtml += ' <span class="text-success" title="Cache hit"><i class="bi bi-lightning-charge-fill"></i></span>';
            }
            if (tempoMs > 5000) {
                footerHtml += ' <span class="text-warning" title="Consulta lenta (>' + 5 + 's)"><i class="bi bi-exclamation-triangle-fill"></i></span>';
            }
            footerEl.innerHTML = footerHtml;
        }
        if (loadingEl) loadingEl.style.display = 'none';

        if (!graficoEl) return;

        if (!r.ok) {
            graficoEl.innerHTML = '<div class="widget-erro"><i class="bi bi-exclamation-triangle"></i> ' + escapeHtml(r.erro || 'Erro') + '</div>';
            return;
        }

        if (!r.data || r.data.length === 0) {
            graficoEl.innerHTML = '<div class="widget-vazio"><div class="text-center"><i class="bi bi-inbox" style="font-size:1.5rem"></i><div>Sem dados</div></div></div>';
            return;
        }

        graficoEl.innerHTML = '';
        renderizarWidget(graficoEl, widget, r.data);

        var chart = echarts.getInstanceByDom(graficoEl);
        if (chart) {
            chartsAtivos[widget.id] = chart;
            filtroCruzado.registrarClique(widget.id, chart, widget);
            drillDown.registrar(widget.id, chart, widget);
        }

        gerarTabelaAuxiliar(widget.id, r.data);

    } catch (e) {
        if (loadingEl) loadingEl.style.display = 'none';
        if (graficoEl) {
            graficoEl.innerHTML = '<div class="widget-erro"><i class="bi bi-exclamation-triangle"></i> ' + escapeHtml(e.message) + '</div>';
        }
    }
}

function recarregarWidget(widgetId) {
    if (!painelAtual || !painelAtual.widgets) return;
    var w = painelAtual.widgets.find(function(x) { return x.id === widgetId; });
    if (w) carregarDadosWidget(w);
}

function recarregarTodosWidgets() {
    if (!painelAtual || !painelAtual.widgets) return;
    painelAtual.widgets.forEach(function(w) { carregarDadosWidget(w); });
    atualizarBadgeAtualizado();
}

function gerarTabelaAuxiliar(widgetId, data) {
    var el = document.getElementById('tabela-' + widgetId);
    if (!el || !data || data.length === 0) return;

    var keys = Object.keys(data[0]);
    var html = '<table class="table table-sm table-striped mb-0" style="font-size:0.8rem"><thead><tr>';
    for (var i = 0; i < keys.length; i++) html += '<th>' + escapeHtml(keys[i]) + '</th>';
    html += '</tr></thead><tbody>';
    var max = Math.min(data.length, 100);
    for (var j = 0; j < max; j++) {
        html += '<tr>';
        for (var k = 0; k < keys.length; k++) {
            html += '<td>' + (data[j][keys[k]] != null ? escapeHtml(String(data[j][keys[k]])) : '') + '</td>';
        }
        html += '</tr>';
    }
    html += '</tbody></table>';
    if (data.length > 100) html += '<div class="text-muted text-center" style="font-size:0.75rem">100 de ' + data.length + '</div>';
    el.innerHTML = html;
}

function toggleTabela(widgetId) {
    var g = document.getElementById('grafico-' + widgetId);
    var t = document.getElementById('tabela-' + widgetId);
    if (!g || !t) return;

    if (t.classList.contains('active')) {
        t.classList.remove('active');
        g.classList.remove('hidden');
    } else {
        t.classList.add('active');
        g.classList.add('hidden');
    }
}

function editarWidget(widgetId) {
    var base = window.location.pathname.replace(/\/paineis\/.*/, '/widgets/editor.jsp');
    window.location.href = base + '?dashboardId=' + painelAtual.id + '&widgetId=' + widgetId;
}

async function removerWidget(widgetId) {
    if (!confirm('Remover este widget?')) return;
    var r = await api.post('/api/dash/paineis', { acao: 'excluirWidget', id: widgetId });
    if (r.ok) abrirPainel(painelAtual.id);
}

var salvarLayoutTimer = null;
function salvarLayoutDebounced() {
    if (salvarLayoutTimer) clearTimeout(salvarLayoutTimer);
    salvarLayoutTimer = setTimeout(function() {
        salvarLayout();
    }, 1500);
}

async function salvarLayout() {
    if (!grid || !painelAtual) return;
    var items = grid.getGridItems();
    var widgets = items.map(function(item) {
        return {
            id: parseInt(item.id || item.el.querySelector('[data-widget-id]').dataset.widgetId),
            gridX: item.x,
            gridY: item.y,
            gridW: item.w,
            gridH: item.h
        };
    });

    await api.post('/api/dash/paineis', {
        acao: 'atualizarLayout',
        widgets: widgets
    });
}

function configurarRefresh(seg) {
    if (refreshInterval) clearInterval(refreshInterval);
    if (seg > 0 && painelAtual) {
        refreshInterval = setInterval(function() {
            recarregarTodosWidgets();
        }, seg * 1000);
    }
}

function marcarPainelAtivoNaLista(idPainel) {
    var lista = document.getElementById('listaPaineis');
    if (!lista) return;
    lista.querySelectorAll('.list-group-item').forEach(function(el) {
        el.classList.toggle('active', el.dataset.id === String(idPainel));
    });
}

export async function novoPainel() {
    var nome = prompt('Nome do novo painel:');
    if (!nome || !nome.trim()) return;

    var r = await api.post('/api/dash/paineis', {
        acao: 'salvarPainel',
        nome: nome.trim()
    });
    if (r.ok) {
        await carregarListaPaineis();
        abrirPainel(r.id);
    } else {
        mostrarErro(r.erro);
    }
}

export async function salvarPainel() {
    if (!painelAtual) return;
    var r = await api.post('/api/dash/paineis', {
        acao: 'salvarPainel',
        id: painelAtual.id,
        nome: painelAtual.nome,
        refreshSeg: parseInt(document.getElementById('seletorRefresh').value)
    });
    if (r.ok) {
        mostrarSucesso('Painel salvo');
    } else {
        mostrarErro(r.erro);
    }
}

export function adicionarWidget() {
    if (!painelAtual) return;
    var base = window.location.pathname.replace(/\/paineis\/.*/, '/widgets/editor.jsp');
    window.location.href = base + '?dashboardId=' + painelAtual.id;
}

export async function versionar() {
    if (!painelAtual) return;
    var r = await api.post('/api/dash/paineis', {
        acao: 'versionar',
        idPainel: painelAtual.id
    });
    if (r.ok) {
        mostrarSucesso('Versao ' + (r.versao || '') + ' criada');
    } else {
        mostrarErro(r.erro);
    }
}

export async function duplicar() {
    if (!painelAtual) return;
    var r = await api.post('/api/dash/paineis', {
        acao: 'duplicar',
        idPainel: painelAtual.id
    });
    if (r.ok) {
        await carregarListaPaineis();
        abrirPainel(r.id);
        mostrarSucesso('Painel duplicado');
    }
}

export async function compartilhar() {
    if (!painelAtual) return;
    var idUsuario = prompt('ID do usuario:');
    if (!idUsuario) return;
    var nivel = prompt('Nivel (ver/editar):') || 'ver';

    var r = await api.post('/api/dash/paineis', {
        acao: 'compartilhar',
        idPainel: painelAtual.id,
        idUsuarioDestino: parseInt(idUsuario),
        nivel: nivel
    });
    if (r.ok) {
        mostrarSucesso('Compartilhado');
    } else {
        mostrarErro(r.erro);
    }
}

export async function listarVersoes() {
    if (!painelAtual) return;
    var r = await api.get('/api/dash/paineis?acao=listarVersoes&id=' + painelAtual.id);
    if (!r.ok) { mostrarErro(r.erro); return; }
    return r.versoes || [];
}

export async function restaurarVersao(idVersao) {
    if (!painelAtual) return;
    var r = await api.post('/api/dash/paineis', {
        acao: 'restaurarVersao',
        idPainel: painelAtual.id,
        idVersao: idVersao
    });
    if (r.ok) {
        abrirPainel(painelAtual.id);
        mostrarSucesso('Versao restaurada');
    } else {
        mostrarErro(r.erro);
    }
}

function atualizarBadgeAtualizado() {
    ultimaAtualizacao = Date.now();
    renderizarBadgeAtualizado();
    if (badgeAtualizadoTimer) clearInterval(badgeAtualizadoTimer);
    badgeAtualizadoTimer = setInterval(renderizarBadgeAtualizado, 30000);
}

function renderizarBadgeAtualizado() {
    var el = document.getElementById('badgeAtualizado');
    if (!el || !ultimaAtualizacao) return;

    var diffMs = Date.now() - ultimaAtualizacao;
    var diffMin = Math.floor(diffMs / 60000);
    var texto, cor;

    if (diffMin < 1) {
        texto = 'Atualizado agora';
        cor = 'success';
    } else if (diffMin < 5) {
        texto = 'Atualizado ha ' + diffMin + ' min';
        cor = 'success';
    } else if (diffMin < 15) {
        texto = 'Atualizado ha ' + diffMin + ' min';
        cor = 'warning';
    } else {
        texto = 'Atualizado ha ' + diffMin + ' min';
        cor = 'danger';
    }

    el.className = 'badge bg-' + cor + '-subtle text-' + cor + ' ms-2';
    el.innerHTML = '<i class="bi bi-clock"></i> ' + texto;
    el.style.display = 'inline-block';
}

function mostrarSucesso(msg) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({ icon: 'success', title: msg, timer: 1500, showConfirmButton: false });
    } else {
        alert(msg);
    }
}

function mostrarErro(msg) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({ icon: 'error', title: 'Erro', text: msg || 'Erro desconhecido' });
    } else {
        alert(msg || 'Erro');
    }
}

export function exportarCsvWidget(widgetId) {
    window.open('/api/dash/exportar?acao=csvWidget&id=' + widgetId, '_blank');
}

export function exportarXlsxWidget(widgetId) {
    window.open('/api/dash/exportar?acao=xlsxWidget&id=' + widgetId, '_blank');
}

export function exportarCsvPainel() {
    if (!painelAtual) return;
    window.open('/api/dash/exportar?acao=csvPainel&id=' + painelAtual.id, '_blank');
}

export function imprimirPainel() {
    window.print();
}

function escapeHtml(s) {
    var d = document.createElement('div');
    d.appendChild(document.createTextNode(String(s || '')));
    return d.innerHTML;
}
