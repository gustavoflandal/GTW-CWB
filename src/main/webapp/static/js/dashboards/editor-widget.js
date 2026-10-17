/**
 * editor-widget.js — editor de widget (novo e edicao).
 *
 * Os campos do dataset sao arrastados (SortableJS) das listas "Campos disponiveis"
 * para as zonas de eixo. Cada zona aceita so os tipos de campo que fazem sentido
 * (medidas em "Eixo Y"/"Valores"; dimensoes e tempo nas demais); um campo de data
 * solto em "Eixo X" vira a dimensao temporal do widget.
 */
import { api } from './api.js';
import { renderizarWidget, tiposDisponiveis, ajustarConsultaAoTipo } from './widget-registry.js';
import { criarEditorFiltros } from './editor-filtros.js';

var URL_PAINEL = '/muralha-digital/pages/analises/paineis/index.jsp?id=';
var URL_DATASETS = '/muralha-digital/pages/analises/datasets/index.jsp';

var VISUAL_PADRAO = { empilhado: false, rotulos: true, paleta: 'padrao', orientacao: 'vertical' };

// Zonas de soltar e os tipos de campo que cada uma aceita.
var ZONAS = {
    zonaDimensoes:    ['dimensao', 'tempo'],
    zonaMedidas:      ['medida'],
    zonaSeries:       ['dimensao', 'tempo'],
    zonaPivotLinhas:  ['dimensao', 'tempo'],
    zonaPivotColunas: ['dimensao', 'tempo'],
    zonaPivotValores: ['medida']
};
var LISTAS = ['listaDimensoes', 'listaMedidas', 'listaTempos'];

// Tipos que mostram evolucao no tempo: ganham granularidade mensal automaticamente.
var TIPOS_EVOLUCAO = ['linhas', 'area'];

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
    granularidade: '',
    // true quando o editor escolheu a granularidade sozinho (campo de data solto no eixo,
    // tipo linhas/area); nesse caso ela volta a "sem agrupamento" se o motivo sumir.
    granularidadeAutomatica: false,
    dateRange: 'last 12 months',
    ordenacao: {},
    limite: 20,
    visual: Object.assign({}, VISUAL_PADRAO)
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
        linkPainel.href = URL_PAINEL + estado.idDashboard;
    }

    renderizarSeletorTipo();
    renderizarOpcoesVisuais();
    inicializarArrastar();
    ligarEventos();

    var temDatasets = await carregarDatasets();

    if (estado.idWidget) {
        await carregarWidget(estado.idWidget);
    } else if (!estado.idDashboard) {
        bloquearEditor('Este editor foi aberto sem um painel. Abra-o pelo botão "+ Widget" de um painel para que o widget tenha onde ser salvo.');
    } else if (!temDatasets) {
        mostrarPreviewMensagem('Nenhum dataset publicado. <a href="' + URL_DATASETS + '">Publique um dataset</a> para montar widgets.');
    }
}

function ligarEventos() {
    document.getElementById('selDataset').addEventListener('change', function() {
        estado.idDataset = parseInt(this.value) || 0;
        // Os campos pertencem ao dataset: ao trocar, as zonas recomecam vazias.
        limparSelecao();
        if (estado.idDataset) {
            carregarCamposDataset(estado.idDataset);
        } else {
            limparCampos();
            mostrarPreviewMensagem('Selecione um dataset e arraste campos para os eixos.');
        }
    });

    document.getElementById('inpTitulo').addEventListener('input', function() {
        estado.titulo = this.value;
    });

    document.getElementById('inpLimite').addEventListener('change', function() {
        estado.limite = parseInt(this.value) || 20;
        agendarPreview();
    });

    document.getElementById('selGranularidade').addEventListener('change', function() {
        definirGranularidade(this.value, false);
        agendarPreview();
    });

    document.getElementById('selDateRange').addEventListener('change', function() {
        estado.dateRange = this.value;
        agendarPreview();
    });

    var selTempo = document.getElementById('selTempoCampo');
    if (selTempo) {
        selTempo.addEventListener('change', function() {
            estado.timeDimension = this.value || null;
            agendarPreview();
        });
    }

    var chkSubtotais = document.getElementById('chkSubtotais');
    if (chkSubtotais) {
        chkSubtotais.addEventListener('change', function() { atualizarSelecaoPivot(); });
    }

    // Filtros: qualquer mudanca de campo/operador/valor ou remocao de condicao refaz o preview.
    var containerFiltros = document.getElementById('containerFiltros');
    containerFiltros.addEventListener('change', function() { agendarPreview(); });
    containerFiltros.addEventListener('click', function(e) {
        if (e.target.closest('[data-role="remCondicao"]')) agendarPreview();
    });

    document.getElementById('btnPreview').addEventListener('click', function() { executarPreview(); });
    document.getElementById('btnSalvar').addEventListener('click', function() { salvar(false); });
    document.getElementById('btnSalvarFechar').addEventListener('click', function() { salvar(true); });
}

async function carregarDatasets() {
    var resp = await api.get('/api/dash/widgets?acao=datasetsPublicados');
    if (!resp.ok) {
        mostrarPreviewMensagem('<span class="text-danger">Não foi possível listar os datasets: ' + escapeHtml(resp.erro || '') + '</span>');
        return false;
    }
    var sel = document.getElementById('selDataset');
    var datasets = resp.datasets || [];
    datasets.forEach(function(ds) {
        var opt = document.createElement('option');
        opt.value = ds.id;
        opt.textContent = ds.nome + ' (' + ds.tabelaBase + ')';
        sel.appendChild(opt);
    });
    return datasets.length > 0;
}

async function carregarCamposDataset(idDataset) {
    var resp = await api.get('/api/dash/widgets?acao=metaDataset&idDataset=' + idDataset);
    if (!resp.ok) {
        mostrarPreviewMensagem('<span class="text-danger">Não foi possível carregar os campos do dataset: ' + escapeHtml(resp.erro || '') + '</span>');
        return;
    }

    estado.dimensoes = resp.dimensoes || [];
    estado.medidas = resp.medidas || [];
    estado.tempos = resp.tempos || [];

    renderizarCampos();

    var todosCampos = [].concat(estado.dimensoes, estado.medidas, estado.tempos);
    editorFiltros = criarEditorFiltros(document.getElementById('containerFiltros'), todosCampos);

    document.getElementById('painelTimeDimension').style.display = estado.tempos.length > 0 ? 'block' : 'none';
    estado.timeDimension = estado.tempos.length > 0 ? estado.tempos[0].alias : null;
    renderizarSeletorTempo();

    if (!estado.idWidget) {
        mostrarPreviewMensagem('Arraste os campos para os eixos. O preview atualiza sozinho.');
    }
}

function limparCampos() {
    estado.dimensoes = [];
    estado.medidas = [];
    estado.tempos = [];
    renderizarCampos();
    document.getElementById('containerFiltros').innerHTML = '';
    editorFiltros = null;
    document.getElementById('painelTimeDimension').style.display = 'none';
}

function limparSelecao() {
    Object.keys(ZONAS).forEach(function(id) {
        var zona = document.getElementById(id);
        if (zona) zona.innerHTML = '';
    });
    estado.camposSelecionados = { dimensoes: [], medidas: [], series: [] };
    estado.pivotConfig = { linhas: [], colunas: [], valores: [], subtotais: true };
    estado.timeDimension = null;
    if (estado.granularidadeAutomatica) definirGranularidade('', false);
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
    campos.forEach(function(c) { container.appendChild(criarChip(c, tipo)); });
    if (campos.length === 0) {
        var vazio = document.createElement('small');
        vazio.className = 'text-body-secondary fst-italic lista-vazia';
        vazio.textContent = 'nenhum';
        container.appendChild(vazio);
    }
}

function criarChip(campo, tipo) {
    var div = document.createElement('div');
    div.className = 'campo-arraste badge bg-light text-dark border me-1 mb-1';
    div.dataset.alias = campo.alias;
    div.dataset.rotulo = campo.rotulo;
    div.dataset.tipo = tipo;
    div.title = descreverCampo(campo, tipo);
    div.textContent = campo.rotulo;
    return div;
}

function descreverCampo(c, tipo) {
    var papel = tipo === 'medida' ? 'Medida' : (tipo === 'tempo' ? 'Data/hora' : 'Dimensão');
    var txt = papel + ' · campo "' + c.alias + '"';
    if (tipo === 'medida' && c.agregacao && c.agregacao !== 'NONE') txt += ' · ' + c.agregacao;
    return txt;
}

function tipoDoCampo(alias) {
    if (estado.medidas.some(function(c) { return c.alias === alias; })) return 'medida';
    if (estado.tempos.some(function(c) { return c.alias === alias; })) return 'tempo';
    return 'dimensao';
}

function ehCampoTempo(alias) {
    return estado.tempos.some(function(t) { return t.alias === alias; });
}

function renderizarSeletorTempo() {
    var sel = document.getElementById('selTempoCampo');
    if (!sel) return;
    sel.innerHTML = '';
    estado.tempos.forEach(function(t) {
        var opt = document.createElement('option');
        opt.value = t.alias;
        opt.textContent = t.rotulo;
        sel.appendChild(opt);
    });
    sel.value = estado.timeDimension || '';
    // Com um unico campo de data nao ha o que escolher.
    var grupo = document.getElementById('grupoTempoCampo');
    if (grupo) grupo.style.display = estado.tempos.length > 1 ? '' : 'none';
}

// ---------------------------------------------------------------------------
// Arrastar e soltar
// ---------------------------------------------------------------------------

function inicializarArrastar() {
    if (!window.Sortable) {
        bloquearEditor('A biblioteca de arrastar e soltar (SortableJS) não foi carregada.');
        return;
    }

    // As instancias sao criadas uma unica vez: recriar a cada dataset acumularia
    // handlers e cada soltura inseriria o campo varias vezes.
    LISTAS.forEach(function(id) {
        var el = document.getElementById(id);
        if (!el || Sortable.get(el)) return;
        Sortable.create(el, {
            group: { name: 'campos', pull: 'clone', put: false },
            sort: false,
            animation: 150,
            filter: '.lista-vazia',
            onStart: function(evt) { marcarZonasAceitas(evt.item.dataset.tipo); },
            onEnd: function() { marcarZonasAceitas(null); }
        });
    });

    Object.keys(ZONAS).forEach(function(id) {
        var el = document.getElementById(id);
        if (!el || Sortable.get(el)) return;
        Sortable.create(el, {
            group: {
                name: 'campos',
                pull: false,
                put: function(para, de, elementoArrastado) { return zonaAceita(id, elementoArrastado); }
            },
            animation: 150,
            ghostClass: 'bg-primary-subtle',
            filter: '.remover-campo',
            preventOnFilter: false,
            onAdd: function(evt) { aoSoltarCampo(id, evt.item); },
            onUpdate: function() { aoAlterarZonas(); }
        });

        el.addEventListener('click', function(e) {
            var btn = e.target.closest('.remover-campo');
            if (!btn) return;
            e.preventDefault();
            removerChip(btn);
        });
        el.addEventListener('keydown', function(e) {
            if ((e.key === 'Enter' || e.key === ' ') && e.target.classList.contains('remover-campo')) {
                e.preventDefault();
                removerChip(e.target);
            }
        });
    });
}

function zonaAceita(zonaId, chip) {
    var aceitos = ZONAS[zonaId] || [];
    return !!chip && aceitos.indexOf(chip.dataset.tipo) >= 0;
}

function marcarZonasAceitas(tipo) {
    document.body.classList.toggle('arrastando-campo', !!tipo);
    Object.keys(ZONAS).forEach(function(id) {
        var el = document.getElementById(id);
        if (el) el.classList.toggle('zona-aceita', !!tipo && ZONAS[id].indexOf(tipo) >= 0);
    });
}

function aoSoltarCampo(zonaId, chip) {
    var zona = document.getElementById(zonaId);
    if (!zona || !chip) return;

    if (!zonaAceita(zonaId, chip)) {
        chip.remove();
        return;
    }
    // O mesmo campo nao entra duas vezes na mesma zona.
    var repetido = Array.prototype.some.call(zona.querySelectorAll('.campo-arraste'), function(outro) {
        return outro !== chip && outro.dataset.alias === chip.dataset.alias;
    });
    if (repetido) {
        chip.remove();
        return;
    }
    decorarChipZona(chip);
    aoAlterarZonas();
}

function removerChip(botao) {
    var chip = botao.closest('.campo-arraste');
    if (chip) chip.remove();
    aoAlterarZonas();
}

function aoAlterarZonas() {
    atualizarSelecao();
    atualizarSelecaoPivot();
    agendarPreview();
}

/** Chip dentro de uma zona: ganha o botao de remover. */
function decorarChipZona(chip) {
    chip.style.opacity = '';
    if (chip.querySelector('.remover-campo')) return;
    var x = document.createElement('i');
    x.className = 'bi bi-x-lg remover-campo';
    x.setAttribute('role', 'button');
    x.setAttribute('tabindex', '0');
    x.setAttribute('aria-label', 'Remover ' + (chip.dataset.rotulo || 'campo'));
    x.title = 'Remover';
    chip.appendChild(x);
}

function adicionarCampoZona(zonaId, alias) {
    var todosCampos = [].concat(estado.dimensoes, estado.medidas, estado.tempos);
    var campo = todosCampos.find(function(c) { return c.alias === alias; });
    var zona = document.getElementById(zonaId);
    if (!campo || !zona) return;

    var chip = criarChip(campo, tipoDoCampo(alias));
    decorarChipZona(chip);
    zona.appendChild(chip);
}

function coletarAliases(containerId) {
    var container = document.getElementById(containerId);
    if (!container) return [];
    return Array.prototype.map.call(container.querySelectorAll('.campo-arraste'), function(el) {
        return el.dataset.alias;
    });
}

function atualizarSelecao() {
    var tempoSolto = null;
    function semTempo(alias) {
        if (ehCampoTempo(alias)) { tempoSolto = alias; return false; }
        return true;
    }
    estado.camposSelecionados.dimensoes = coletarAliases('zonaDimensoes').filter(semTempo);
    estado.camposSelecionados.medidas = coletarAliases('zonaMedidas');
    estado.camposSelecionados.series = coletarAliases('zonaSeries').filter(semTempo);

    if (tempoSolto) {
        // Data/hora como dimensao agruparia por instante exato: vira a dimensao temporal.
        estado.timeDimension = tempoSolto;
        var sel = document.getElementById('selTempoCampo');
        if (sel) sel.value = tempoSolto;
        if (!estado.granularidade) definirGranularidade('month', true);
    } else if (estado.granularidadeAutomatica && TIPOS_EVOLUCAO.indexOf(estado.tipo) < 0) {
        definirGranularidade('', false);
    }
}

function atualizarSelecaoPivot() {
    estado.pivotConfig.linhas = coletarAliases('zonaPivotLinhas');
    estado.pivotConfig.colunas = coletarAliases('zonaPivotColunas');
    estado.pivotConfig.valores = coletarAliases('zonaPivotValores');
    var chk = document.getElementById('chkSubtotais');
    estado.pivotConfig.subtotais = chk ? chk.checked : true;
}

function definirGranularidade(valor, automatica) {
    estado.granularidade = valor || '';
    estado.granularidadeAutomatica = !!automatica && estado.granularidade !== '';
    var sel = document.getElementById('selGranularidade');
    if (sel) sel.value = estado.granularidade;
}

// ---------------------------------------------------------------------------
// Tipo e opcoes visuais
// ---------------------------------------------------------------------------

function renderizarSeletorTipo() {
    var container = document.getElementById('seletorTipos');
    if (!container) return;
    var tipos = tiposDisponiveis();
    var html = '';
    tipos.forEach(function(t) {
        html += '<div class="form-check form-check-inline">';
        html += '<input class="form-check-input" type="radio" name="tipoWidget" id="tipo_' + t.tipo + '" value="' + t.tipo + '"' + (t.tipo === estado.tipo ? ' checked' : '') + '>';
        html += '<label class="form-check-label" for="tipo_' + t.tipo + '" title="' + escapeAttr(t.descricao || t.rotulo) + '"><i class="bi ' + t.icone + '"></i> ' + escapeHtml(t.rotulo) + '</label>';
        html += '</div>';
    });
    container.innerHTML = html;

    container.addEventListener('change', function(e) {
        if (e.target.name !== 'tipoWidget') return;
        estado.tipo = e.target.value;
        toggleZonasPivot();
        // Linhas/area sem dimensao mostram evolucao: entra a granularidade mensal.
        if (TIPOS_EVOLUCAO.indexOf(estado.tipo) >= 0 && !estado.granularidade &&
            estado.tempos.length > 0 && estado.camposSelecionados.dimensoes.length === 0) {
            definirGranularidade('month', true);
        } else if (estado.granularidadeAutomatica && TIPOS_EVOLUCAO.indexOf(estado.tipo) < 0 &&
                   !coletarAliases('zonaDimensoes').some(ehCampoTempo)) {
            definirGranularidade('', false);
        }
        agendarPreview();
    });
}

function renderizarOpcoesVisuais() {
    var container = document.getElementById('opcoesVisuais');
    if (!container) return;
    container.innerHTML =
        '<div class="row g-2">' +
        '<div class="col-md-3"><label class="form-label" for="optPaleta">Paleta</label>' +
        '<select class="form-select form-select-sm" id="optPaleta"><option value="padrao">Padrão</option><option value="azul">Azul</option><option value="verde">Verde</option></select></div>' +
        '<div class="col-md-3"><label class="form-label" for="optOrientacao">Orientação</label>' +
        '<select class="form-select form-select-sm" id="optOrientacao"><option value="vertical">Vertical</option><option value="horizontal">Horizontal</option></select></div>' +
        '<div class="col-md-3"><div class="form-check mt-4"><input class="form-check-input" type="checkbox" id="optRotulos" checked><label class="form-check-label" for="optRotulos" title="Exibe o valor sobre cada barra, ponto ou fatia">Rótulos</label></div></div>' +
        '<div class="col-md-3"><div class="form-check mt-4"><input class="form-check-input" type="checkbox" id="optEmpilhado"><label class="form-check-label" for="optEmpilhado" title="Empilha as séries em vez de exibi-las lado a lado">Empilhado</label></div></div>' +
        '</div>';

    sincronizarOpcoesVisuais();

    container.addEventListener('change', function() {
        estado.visual.paleta = document.getElementById('optPaleta').value;
        estado.visual.orientacao = document.getElementById('optOrientacao').value;
        estado.visual.rotulos = document.getElementById('optRotulos').checked;
        estado.visual.empilhado = document.getElementById('optEmpilhado').checked;
        agendarPreview();
    });
}

function sincronizarOpcoesVisuais() {
    var v = estado.visual || {};
    var el;
    if ((el = document.getElementById('optPaleta'))) el.value = v.paleta || 'padrao';
    if ((el = document.getElementById('optOrientacao'))) el.value = v.orientacao || 'vertical';
    if ((el = document.getElementById('optRotulos'))) el.checked = v.rotulos !== false;
    if ((el = document.getElementById('optEmpilhado'))) el.checked = !!v.empilhado;
}

function toggleZonasPivot() {
    var painelPivot = document.getElementById('painelPivot');
    var painelNormal = document.getElementById('painelZonasNormais');
    if (!painelPivot) return;
    var pivot = estado.tipo === 'pivot';
    painelPivot.style.display = pivot ? 'block' : 'none';
    if (painelNormal) painelNormal.style.display = pivot ? 'none' : 'block';
}

// ---------------------------------------------------------------------------
// Consulta, preview e gravacao
// ---------------------------------------------------------------------------

function montarConsulta() {
    var consulta;

    if (estado.tipo === 'pivot') {
        var pc = estado.pivotConfig;
        consulta = {
            measures: pc.valores,
            dimensions: [].concat(pc.linhas, pc.colunas),
            filters: [],
            order: estado.ordenacao,
            limit: estado.limite > 0 ? estado.limite : 10000
        };
    } else {
        consulta = {
            measures: estado.camposSelecionados.medidas,
            dimensions: estado.camposSelecionados.dimensoes.concat(estado.camposSelecionados.series),
            filters: [],
            order: estado.ordenacao,
            limit: estado.limite
        };
    }

    // Sem granularidade o tempo so filtra o periodo; sem os dois, nao entra na consulta.
    if (estado.timeDimension && estado.tempos.length > 0 && (estado.granularidade || estado.dateRange)) {
        var td = { dimension: estado.timeDimension };
        if (estado.granularidade) td.granularity = estado.granularidade;
        if (estado.dateRange) td.dateRange = estado.dateRange;
        consulta.timeDimensions = [td];
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

function faltaMedida(consulta) {
    if (consulta.measures.length > 0) return false;
    // Uma tabela pode listar so dimensoes.
    return !(estado.tipo === 'tabela' && consulta.dimensions.length > 0);
}

function mensagemFaltaMedida() {
    return estado.tipo === 'pivot'
        ? 'Arraste ao menos uma medida para "Valores".'
        : 'Arraste ao menos uma medida para "Eixo Y / Medidas".';
}

function agendarPreview() {
    if (previewTimer) clearTimeout(previewTimer);
    previewTimer = setTimeout(function() { executarPreview(); }, 600);
}

async function executarPreview() {
    var previewEl = document.getElementById('previewWidget');
    if (!previewEl) return;

    if (!estado.idDataset) {
        mostrarPreviewMensagem('Selecione um dataset primeiro.');
        return;
    }

    var widgetBase = { tipo: estado.tipo, titulo: estado.titulo, visual: estado.visual };
    var consulta = ajustarConsultaAoTipo(widgetBase, montarConsulta());
    if (faltaMedida(consulta)) {
        mostrarPreviewMensagem(mensagemFaltaMedida());
        return;
    }
    var td = (consulta.timeDimensions || [])[0];
    if (TIPOS_EVOLUCAO.indexOf(estado.tipo) >= 0 && !(td && td.granularity) && consulta.dimensions.length === 0) {
        mostrarPreviewMensagem('Para ver a evolução no tempo, arraste um campo de data para "Eixo X" ou escolha uma granularidade.');
        return;
    }

    previewEl.innerHTML = '<div class="text-center p-4"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Carregando...</span></div></div>';

    var resp = await api.post('/api/dash/consultas', {
        datasetId: estado.idDataset,
        query: consulta
    });

    if (!resp.ok) {
        previewEl.innerHTML = '<div class="text-danger text-center p-3"><i class="bi bi-exclamation-triangle"></i> ' + escapeHtml(resp.erro || 'Erro na consulta') + '</div>';
        return;
    }

    var widget = Object.assign({}, widgetBase, {
        consulta: consulta,
        visual: visualParaSalvar(),
        anotacao: resp.annotation || null
    });

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

function visualParaSalvar() {
    var visual = Object.assign({}, estado.visual);
    delete visual.pivot;
    delete visual.series;
    if (estado.tipo === 'pivot') {
        visual.pivot = {
            linhas: estado.pivotConfig.linhas,
            colunas: estado.pivotConfig.colunas,
            valores: estado.pivotConfig.valores,
            subtotais: estado.pivotConfig.subtotais
        };
    } else if (estado.camposSelecionados.series.length > 0) {
        // As series vao junto das dimensoes na consulta; guarda-se quais sao para reabrir o editor.
        visual.series = estado.camposSelecionados.series.slice();
    }
    return visual;
}

async function salvar(fechar) {
    if (!estado.idDashboard) {
        avisar('Este widget não está vinculado a um painel. Abra o editor pelo botão "+ Widget" do painel.');
        return;
    }
    if (!estado.idDataset) {
        avisar('Selecione um dataset.');
        return;
    }
    if (!estado.titulo.trim()) {
        avisar('Informe o título do widget.');
        document.getElementById('inpTitulo').focus();
        return;
    }

    var consulta = montarConsulta();
    if (faltaMedida(consulta)) {
        avisar(mensagemFaltaMedida());
        return;
    }

    var dados = {
        acao: 'salvar',
        id: estado.idWidget,
        idDashboard: estado.idDashboard,
        idDataset: estado.idDataset,
        titulo: estado.titulo.trim(),
        tipo: estado.tipo,
        consulta: consulta,
        visual: visualParaSalvar()
    };

    var resp = await api.post('/api/dash/widgets', dados);
    if (!resp.ok) {
        mostrarErro(resp.erro || 'Falha ao salvar o widget.');
        return;
    }

    estado.idWidget = resp.id;
    document.getElementById('tituloEditor').textContent = 'Editar Widget';

    if (fechar && estado.idDashboard) {
        window.location.href = URL_PAINEL + estado.idDashboard;
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
    if (!resp.ok || !resp.widget) {
        mostrarErro(resp.erro || 'Widget não encontrado.');
        return;
    }

    var w = resp.widget;
    estado.idDashboard = w.idDashboard;
    estado.idDataset = w.idDataset;
    estado.titulo = w.titulo;
    estado.tipo = w.tipo;
    estado.visual = Object.assign({}, VISUAL_PADRAO, w.visual || {});
    sincronizarOpcoesVisuais();

    var linkPainel = document.getElementById('linkPainel');
    if (linkPainel && estado.idDashboard) linkPainel.href = URL_PAINEL + estado.idDashboard;

    document.getElementById('selDataset').value = w.idDataset;
    document.getElementById('inpTitulo').value = w.titulo;
    document.getElementById('tituloEditor').textContent = 'Editar Widget';

    var tipoRadio = document.getElementById('tipo_' + w.tipo);
    if (tipoRadio) tipoRadio.checked = true;

    await carregarCamposDataset(w.idDataset);

    var consulta = w.consulta || {};
    var visual = w.visual || {};
    if (w.tipo === 'pivot') {
        var pv = visual.pivot || {};
        (pv.linhas || []).forEach(function(a) { adicionarCampoZona('zonaPivotLinhas', a); });
        (pv.colunas || []).forEach(function(a) { adicionarCampoZona('zonaPivotColunas', a); });
        (pv.valores || []).forEach(function(a) { adicionarCampoZona('zonaPivotValores', a); });
        var chk = document.getElementById('chkSubtotais');
        if (chk) chk.checked = pv.subtotais !== false;
    } else {
        var series = visual.series || [];
        (consulta.dimensions || []).forEach(function(d) {
            adicionarCampoZona(series.indexOf(d) >= 0 ? 'zonaSeries' : 'zonaDimensoes', d);
        });
        (consulta.measures || []).forEach(function(m) { adicionarCampoZona('zonaMedidas', m); });
    }
    toggleZonasPivot();

    // Periodo/granularidade ausentes no widget salvo significam "sem agrupamento"/"todo o periodo".
    var td = (consulta.timeDimensions || [])[0] || null;
    if (td) estado.timeDimension = td.dimension;
    definirGranularidade(td && td.granularity ? td.granularity : '', false);
    estado.dateRange = td && typeof td.dateRange === 'string' ? td.dateRange : '';
    document.getElementById('selDateRange').value = estado.dateRange;
    var selTempo = document.getElementById('selTempoCampo');
    if (selTempo) selTempo.value = estado.timeDimension || '';

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
    atualizarSelecaoPivot();
    executarPreview();
}

// ---------------------------------------------------------------------------
// Mensagens
// ---------------------------------------------------------------------------

function mostrarPreviewMensagem(html) {
    var previewEl = document.getElementById('previewWidget');
    if (!previewEl) return;
    previewEl.style.height = '';
    previewEl.innerHTML = '<div class="text-muted text-center p-4">' + html + '</div>';
}

function bloquearEditor(mensagem) {
    var aviso = document.getElementById('avisoEditor');
    if (aviso) {
        aviso.textContent = mensagem;
        aviso.classList.remove('d-none');
    }
    ['btnSalvar', 'btnSalvarFechar'].forEach(function(id) {
        var btn = document.getElementById(id);
        if (btn) btn.disabled = true;
    });
}

function avisar(msg) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({ icon: 'warning', title: 'Atenção', text: msg, confirmButtonColor: '#3085d6' });
    } else {
        alert(msg);
    }
}

function mostrarErro(msg) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({ icon: 'error', title: 'Erro', text: msg, confirmButtonColor: '#3085d6' });
    } else {
        alert(msg);
    }
}

function escapeHtml(s) {
    var d = document.createElement('div');
    d.appendChild(document.createTextNode(String(s == null ? '' : s)));
    return d.innerHTML;
}

function escapeAttr(s) {
    return String(s == null ? '' : s).replace(/&/g, '&amp;').replace(/"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
