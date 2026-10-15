import { api } from './api.js';
import { renderizarWidget, tiposDisponiveis } from './widget-registry.js';
import { criarEditorFiltros } from './editor-filtros.js';

var estado = {
    idWidget: 0,
    idDashboard: 0,
    idDataset: 0,
    titulo: '',
    tipo: 'barras',
    dimensoes: [],
    medidas: [],
    tempos: [],
    camposSelecionados: { dimensoes: [], medidas: [], series: [] },
    pivotConfig: { linhas: [], colunas: [], valores: [], subtotais: true },
    timeDimension: null,
    granularidade: 'month',
    dateRange: 'last 12 months',
    ordenacao: {},
    limite: 20,
    visual: { empilhado: false, rotulos: true, paleta: 'padrao', orientacao: 'vertical' }
};

var editorFiltros = null;
var previewTimer = null;

document.addEventListener('DOMContentLoaded', function() { inicializar(); });

async function inicializar() {
    var params = new URLSearchParams(window.location.search);
    estado.idDashboard = parseInt(params.get('dashboardId') || params.get('dashboard')) || 0;
    estado.idWidget = parseInt(params.get('widgetId') || params.get('id')) || 0;

    var linkPainel = document.getElementById('linkPainel');
    if (linkPainel && estado.idDashboard) {
        linkPainel.href = '/muralha-digital/pages/analises/paineis/index.jsp?id=' + estado.idDashboard;
    }

    await carregarDatasets();
    renderizarSeletorTipo();
    renderizarOpcoesVisuais();

    document.getElementById('selDataset').addEventListener('change', function() {
        estado.idDataset = parseInt(this.value) || 0;
        if (estado.idDataset) carregarCamposDataset(estado.idDataset);
    });

    document.getElementById('inpTitulo').addEventListener('input', function() {
        estado.titulo = this.value;
    });

    document.getElementById('inpLimite').addEventListener('change', function() {
        estado.limite = parseInt(this.value) || 20;
    });

    document.getElementById('selGranularidade').addEventListener('change', function() {
        estado.granularidade = this.value;
    });

    document.getElementById('selDateRange').addEventListener('change', function() {
        estado.dateRange = this.value;
    });

    document.getElementById('btnPreview').addEventListener('click', function() { executarPreview(); });
    document.getElementById('btnSalvar').addEventListener('click', function() { salvar(false); });
    document.getElementById('btnSalvarFechar').addEventListener('click', function() { salvar(true); });

    if (estado.idWidget) await carregarWidget(estado.idWidget);
}

async function carregarDatasets() {
    var resp = await api.get('/api/dash/widgets?acao=datasetsPublicados');
    if (!resp.ok) return;
    var sel = document.getElementById('selDataset');
    resp.datasets.forEach(function(ds) {
        var opt = document.createElement('option');
        opt.value = ds.id;
        opt.textContent = ds.nome + ' (' + ds.tabelaBase + ')';
        sel.appendChild(opt);
    });
}

async function carregarCamposDataset(idDataset) {
    var resp = await api.get('/api/dash/widgets?acao=metaDataset&idDataset=' + idDataset);
    if (!resp.ok) return;

    estado.dimensoes = resp.dimensoes || [];
    estado.medidas = resp.medidas || [];
    estado.tempos = resp.tempos || [];
    estado.camposSelecionados = { dimensoes: [], medidas: [], series: [] };

    renderizarCampos();
    inicializarSortable();

    var todosCampos = [].concat(estado.dimensoes, estado.medidas, estado.tempos);
    var containerFiltros = document.getElementById('containerFiltros');
    editorFiltros = criarEditorFiltros(containerFiltros, todosCampos);

    document.getElementById('painelTimeDimension').style.display = estado.tempos.length > 0 ? 'block' : 'none';
    if (estado.tempos.length > 0) {
        estado.timeDimension = estado.tempos[0].alias;
    }
}

function renderizarCampos() {
    renderizarListaCampos('listaDimensoes', estado.dimensoes, 'dimensao');
    renderizarListaCampos('listaMedidas', estado.medidas, 'medida');
    renderizarListaCampos('listaTempos', estado.tempos, 'tempo');
}

function renderizarListaCampos(containerId, campos, tipo) {
    var container = document.getElementById(containerId);
    if (!container) return;
    container.innerHTML = '';
    campos.forEach(function(c) {
        var div = document.createElement('div');
        div.className = 'campo-arraste badge bg-light text-dark border me-1 mb-1';
        div.style.cursor = 'grab';
        div.dataset.alias = c.alias;
        div.dataset.rotulo = c.rotulo;
        div.dataset.tipo = tipo;
        div.textContent = c.rotulo;
        container.appendChild(div);
    });
}

function inicializarSortable() {
    var zonas = ['zonaDimensoes', 'zonaMedidas', 'zonaSeries'];
    zonas.forEach(function(id) {
        var el = document.getElementById(id);
        if (!el || !window.Sortable) return;
        Sortable.create(el, {
            group: { name: 'campos', pull: false, put: true },
            animation: 150,
            ghostClass: 'bg-primary-subtle',
            onAdd: function(evt) {
                atualizarSelecao();
                agendarPreview();
            },
            onRemove: function() { atualizarSelecao(); }
        });
    });

    ['listaDimensoes', 'listaMedidas', 'listaTempos'].forEach(function(id) {
        var el = document.getElementById(id);
        if (!el || !window.Sortable) return;
        Sortable.create(el, {
            group: { name: 'campos', pull: 'clone', put: false },
            sort: false,
            animation: 150
        });
    });
}

function atualizarSelecao() {
    estado.camposSelecionados.dimensoes = coletarAliases('zonaDimensoes');
    estado.camposSelecionados.medidas = coletarAliases('zonaMedidas');
    estado.camposSelecionados.series = coletarAliases('zonaSeries');
}

function coletarAliases(containerId) {
    var els = document.getElementById(containerId).querySelectorAll('.campo-arraste');
    var result = [];
    els.forEach(function(el) { result.push(el.dataset.alias); });
    return result;
}

function renderizarSeletorTipo() {
    var container = document.getElementById('seletorTipos');
    if (!container) return;
    var tipos = tiposDisponiveis();
    var html = '';
    tipos.forEach(function(t) {
        html += '<div class="form-check form-check-inline">';
        html += '<input class="form-check-input" type="radio" name="tipoWidget" id="tipo_' + t.tipo + '" value="' + t.tipo + '"' + (t.tipo === estado.tipo ? ' checked' : '') + '>';
        html += '<label class="form-check-label" for="tipo_' + t.tipo + '"><i class="bi ' + t.icone + '"></i> ' + t.rotulo + '</label>';
        html += '</div>';
    });
    container.innerHTML = html;

    container.addEventListener('change', function(e) {
        if (e.target.name === 'tipoWidget') {
            estado.tipo = e.target.value;
            toggleZonasPivot();
            agendarPreview();
        }
    });
}

function renderizarOpcoesVisuais() {
    var container = document.getElementById('opcoesVisuais');
    if (!container) return;
    container.innerHTML =
        '<div class="row g-2">' +
        '<div class="col-md-3"><label class="form-label">Paleta</label>' +
        '<select class="form-select form-select-sm" id="optPaleta"><option value="padrao">Padrao</option><option value="azul">Azul</option><option value="verde">Verde</option></select></div>' +
        '<div class="col-md-3"><label class="form-label">Orientacao</label>' +
        '<select class="form-select form-select-sm" id="optOrientacao"><option value="vertical">Vertical</option><option value="horizontal">Horizontal</option></select></div>' +
        '<div class="col-md-3"><div class="form-check mt-4"><input class="form-check-input" type="checkbox" id="optRotulos" checked><label class="form-check-label" for="optRotulos">Rotulos</label></div></div>' +
        '<div class="col-md-3"><div class="form-check mt-4"><input class="form-check-input" type="checkbox" id="optEmpilhado"><label class="form-check-label" for="optEmpilhado">Empilhado</label></div></div>' +
        '</div>';

    container.addEventListener('change', function() {
        estado.visual.paleta = document.getElementById('optPaleta').value;
        estado.visual.orientacao = document.getElementById('optOrientacao').value;
        estado.visual.rotulos = document.getElementById('optRotulos').checked;
        estado.visual.empilhado = document.getElementById('optEmpilhado').checked;
        agendarPreview();
    });
}

function toggleZonasPivot() {
    var painelPivot = document.getElementById('painelPivot');
    var painelNormal = document.getElementById('painelZonasNormais');
    if (!painelPivot) return;

    if (estado.tipo === 'pivot') {
        painelPivot.style.display = 'block';
        if (painelNormal) painelNormal.style.display = 'none';
        inicializarSortablePivot();
    } else {
        painelPivot.style.display = 'none';
        if (painelNormal) painelNormal.style.display = 'block';
    }
}

function inicializarSortablePivot() {
    var zonas = ['zonaPivotLinhas', 'zonaPivotColunas', 'zonaPivotValores'];
    zonas.forEach(function(id) {
        var el = document.getElementById(id);
        if (!el || !window.Sortable) return;
        if (el._sortable) return;
        el._sortable = Sortable.create(el, {
            group: { name: 'campos', pull: false, put: true },
            animation: 150,
            ghostClass: 'bg-primary-subtle',
            onAdd: function() {
                atualizarSelecaoPivot();
                agendarPreview();
            },
            onRemove: function() { atualizarSelecaoPivot(); }
        });
    });
}

function atualizarSelecaoPivot() {
    estado.pivotConfig.linhas = coletarAliases('zonaPivotLinhas');
    estado.pivotConfig.colunas = coletarAliases('zonaPivotColunas');
    estado.pivotConfig.valores = coletarAliases('zonaPivotValores');
    var chk = document.getElementById('chkSubtotais');
    estado.pivotConfig.subtotais = chk ? chk.checked : true;
}

function montarConsulta() {
    var consulta;

    if (estado.tipo === 'pivot') {
        var pc = estado.pivotConfig;
        var allDims = [].concat(pc.linhas, pc.colunas);
        consulta = {
            measures: pc.valores,
            dimensions: allDims,
            filters: [],
            order: estado.ordenacao,
            limit: estado.limite > 0 ? estado.limite : 10000
        };
    } else {
        consulta = {
            measures: estado.camposSelecionados.medidas,
            dimensions: estado.camposSelecionados.dimensoes,
            filters: [],
            order: estado.ordenacao,
            limit: estado.limite
        };

        if (estado.camposSelecionados.series.length > 0) {
            consulta.dimensions = consulta.dimensions.concat(estado.camposSelecionados.series);
        }
    }

    if (estado.timeDimension && estado.tempos.length > 0) {
        consulta.timeDimensions = [{
            dimension: estado.timeDimension,
            granularity: estado.granularidade,
            dateRange: estado.dateRange
        }];
    }

    if (editorFiltros) {
        var filtros = editorFiltros.obterFiltros();
        if (filtros.condicoes && filtros.condicoes.length > 0) {
            consulta.filters = filtros.condicoes.map(function(c) {
                return { member: c.campo, operator: c.operador, values: [c.valor] };
            });
        }
    }

    return consulta;
}

function agendarPreview() {
    if (previewTimer) clearTimeout(previewTimer);
    previewTimer = setTimeout(function() { executarPreview(); }, 600);
}

async function executarPreview() {
    var previewEl = document.getElementById('previewWidget');
    if (!previewEl) return;

    if (!estado.idDataset) {
        previewEl.innerHTML = '<div class="text-muted text-center p-3">Selecione um dataset primeiro.</div>';
        return;
    }

    var consulta = montarConsulta();
    if (consulta.measures.length === 0 && estado.tipo !== 'tabela') {
        previewEl.innerHTML = '<div class="text-muted text-center p-3">Arraste ao menos uma medida.</div>';
        return;
    }

    previewEl.innerHTML = '<div class="text-center p-4"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Carregando...</span></div></div>';

    var resp = await api.post('/api/dash/consultas', {
        datasetId: estado.idDataset,
        query: consulta
    });

    if (!resp.ok) {
        previewEl.innerHTML = '<div class="text-danger text-center p-3"><i class="bi bi-exclamation-triangle"></i> ' + (resp.erro || 'Erro na consulta') + '</div>';
        return;
    }

    var widget = {
        tipo: estado.tipo,
        titulo: estado.titulo,
        consulta: consulta,
        visual: estado.visual
    };

    previewEl.innerHTML = '';
    previewEl.style.height = '350px';
    renderizarWidget(previewEl, widget, resp.data || []);

    var btnTabela = document.getElementById('btnVerTabela');
    if (btnTabela) {
        btnTabela.onclick = function() {
            var wTabela = Object.assign({}, widget, { tipo: 'tabela' });
            previewEl.style.height = 'auto';
            renderizarWidget(previewEl, wTabela, resp.data || []);
        };
    }
}

async function salvar(fechar) {
    if (!estado.idDataset) {
        alert('Selecione um dataset.');
        return;
    }
    if (!estado.titulo.trim()) {
        alert('Informe o titulo do widget.');
        return;
    }

    var consulta = montarConsulta();
    var visual = Object.assign({}, estado.visual);
    if (estado.tipo === 'pivot') {
        visual.pivot = {
            linhas: estado.pivotConfig.linhas,
            colunas: estado.pivotConfig.colunas,
            valores: estado.pivotConfig.valores,
            subtotais: estado.pivotConfig.subtotais
        };
    }
    var dados = {
        acao: 'salvar',
        id: estado.idWidget,
        idDashboard: estado.idDashboard,
        idDataset: estado.idDataset,
        titulo: estado.titulo,
        tipo: estado.tipo,
        consulta: consulta,
        visual: visual
    };

    var resp = await api.post('/api/dash/widgets', dados);
    if (!resp.ok) {
        alert('Erro: ' + (resp.erro || 'falha ao salvar'));
        return;
    }

    estado.idWidget = resp.id;

    if (fechar && estado.idDashboard) {
        window.location.href = '/muralha-digital/pages/analises/paineis/index.jsp?id=' + estado.idDashboard;
    } else {
        var msgEl = document.getElementById('msgSalvo');
        if (msgEl) {
            msgEl.textContent = 'Salvo!';
            msgEl.style.display = 'inline';
            setTimeout(function() { msgEl.style.display = 'none'; }, 2000);
        }
    }
}

async function carregarWidget(idWidget) {
    var resp = await api.get('/api/dash/widgets?acao=obter&id=' + idWidget);
    if (!resp.ok || !resp.widget) return;

    var w = resp.widget;
    estado.idDashboard = w.idDashboard;
    estado.idDataset = w.idDataset;
    estado.titulo = w.titulo;
    estado.tipo = w.tipo;
    estado.visual = w.visual || estado.visual;

    document.getElementById('selDataset').value = w.idDataset;
    document.getElementById('inpTitulo').value = w.titulo;
    document.getElementById('tituloEditor').textContent = 'Editar Widget';

    var tipoRadio = document.getElementById('tipo_' + w.tipo);
    if (tipoRadio) tipoRadio.checked = true;

    await carregarCamposDataset(w.idDataset);

    var consulta = w.consulta || {};
    if (consulta.dimensions) {
        consulta.dimensions.forEach(function(d) { adicionarCampoZona('zonaDimensoes', d); });
    }
    if (consulta.measures) {
        consulta.measures.forEach(function(m) { adicionarCampoZona('zonaMedidas', m); });
    }
    if (consulta.timeDimensions && consulta.timeDimensions[0]) {
        var td = consulta.timeDimensions[0];
        estado.timeDimension = td.dimension;
        estado.granularidade = td.granularity || 'month';
        estado.dateRange = td.dateRange || 'last 12 months';
        document.getElementById('selGranularidade').value = estado.granularidade;
        document.getElementById('selDateRange').value = estado.dateRange;
    }
    if (consulta.limit) {
        estado.limite = consulta.limit;
        document.getElementById('inpLimite').value = consulta.limit;
    }
    if (consulta.filters && editorFiltros) {
        var condsFiltro = consulta.filters.map(function(f) {
            return { campo: f.member, operador: f.operator, valor: (f.values && f.values[0]) || '' };
        });
        editorFiltros.definirFiltros({ operador: 'AND', condicoes: condsFiltro });
    }

    atualizarSelecao();
    executarPreview();
}

function adicionarCampoZona(zonaId, alias) {
    var todosCampos = [].concat(estado.dimensoes, estado.medidas, estado.tempos);
    var campo = todosCampos.find(function(c) { return c.alias === alias; });
    if (!campo) return;

    var zona = document.getElementById(zonaId);
    var div = document.createElement('div');
    div.className = 'campo-arraste badge bg-light text-dark border me-1 mb-1';
    div.dataset.alias = campo.alias;
    div.dataset.rotulo = campo.rotulo;
    div.textContent = campo.rotulo;
    zona.appendChild(div);
}
