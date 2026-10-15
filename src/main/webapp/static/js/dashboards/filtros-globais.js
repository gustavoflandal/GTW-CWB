import { api } from './api.js';

var estado = { filtros: {}, campos: [] };
var onAplicar = null;

export function inicializarFiltrosGlobais(container, opcoes) {
    onAplicar = opcoes.onAplicar || null;
    renderizar(container);
    return {
        definirCampos: function(campos) {
            estado.campos = campos;
            renderizar(container);
        },
        obterFiltros: function() { return estado.filtros; },
        limpar: function() {
            estado.filtros = {};
            renderizar(container);
            if (onAplicar) onAplicar(estado.filtros);
        }
    };
}

function renderizar(container) {
    var html = '<div class="d-flex align-items-center gap-2 flex-wrap">';
    html += '<span class="text-muted" style="font-size:0.8rem"><i class="bi bi-funnel"></i> Filtros:</span>';

    html += '<div class="d-flex gap-1 align-items-center">';
    html += '<label class="form-label mb-0" style="font-size:0.8rem">De:</label>';
    html += '<input type="date" class="form-control form-control-sm" id="filtroDataInicio" style="width:140px" value="' + (estado.filtros.dataInicio || '') + '">';
    html += '<label class="form-label mb-0" style="font-size:0.8rem">Ate:</label>';
    html += '<input type="date" class="form-control form-control-sm" id="filtroDataFim" style="width:140px" value="' + (estado.filtros.dataFim || '') + '">';
    html += '</div>';

    for (var i = 0; i < estado.campos.length; i++) {
        var c = estado.campos[i];
        if (c.tipo !== 'dimensao') continue;
        var valorAtual = estado.filtros[c.alias] || '';
        html += '<div class="d-flex gap-1 align-items-center">';
        html += '<label class="form-label mb-0" style="font-size:0.8rem">' + escapeHtml(c.rotulo) + ':</label>';
        html += '<input type="text" class="form-control form-control-sm filtro-dim" data-alias="' + escapeAttr(c.alias) + '" style="width:120px" placeholder="Filtrar..." value="' + escapeAttr(valorAtual) + '">';
        html += '</div>';
    }

    html += '<button class="btn btn-sm btn-primary" id="btnAplicarFiltros"><i class="bi bi-check"></i> Aplicar</button>';
    html += '<button class="btn btn-sm btn-outline-secondary" id="btnLimparFiltros"><i class="bi bi-x"></i> Limpar</button>';
    html += '</div>';

    container.innerHTML = html;

    document.getElementById('btnAplicarFiltros').addEventListener('click', function() {
        coletarFiltros(container);
        if (onAplicar) onAplicar(estado.filtros);
    });

    document.getElementById('btnLimparFiltros').addEventListener('click', function() {
        estado.filtros = {};
        renderizar(container);
        if (onAplicar) onAplicar(estado.filtros);
    });
}

function coletarFiltros(container) {
    var di = document.getElementById('filtroDataInicio');
    var df = document.getElementById('filtroDataFim');
    estado.filtros.dataInicio = di ? di.value : '';
    estado.filtros.dataFim = df ? df.value : '';

    container.querySelectorAll('.filtro-dim').forEach(function(el) {
        var alias = el.dataset.alias;
        if (el.value.trim()) {
            estado.filtros[alias] = el.value.trim();
        } else {
            delete estado.filtros[alias];
        }
    });
}

export function aplicarFiltrosAConsulta(consulta, filtrosGlobais) {
    if (!filtrosGlobais) return consulta;

    var nova = JSON.parse(JSON.stringify(consulta));
    if (!nova.filters) nova.filters = [];

    if (filtrosGlobais.dataInicio || filtrosGlobais.dataFim) {
        if (nova.timeDimensions && nova.timeDimensions.length > 0) {
            var range = [];
            if (filtrosGlobais.dataInicio) range.push(filtrosGlobais.dataInicio);
            else range.push('2000-01-01');
            if (filtrosGlobais.dataFim) range.push(filtrosGlobais.dataFim);
            else range.push('2099-12-31');
            nova.timeDimensions[0].dateRange = range;
        }
    }

    Object.keys(filtrosGlobais).forEach(function(key) {
        if (key === 'dataInicio' || key === 'dataFim') return;
        if (!filtrosGlobais[key]) return;
        nova.filters.push({
            member: key,
            operator: 'equals',
            values: [filtrosGlobais[key]]
        });
    });

    return nova;
}

function escapeHtml(s) { var d = document.createElement('div'); d.appendChild(document.createTextNode(String(s || ''))); return d.innerHTML; }
function escapeAttr(s) { return String(s || '').replace(/&/g,'&amp;').replace(/"/g,'&quot;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }
