import { lerDataIso } from './widgets/comum.js';

var drillState = {};
var onDrill = null;

export function inicializarDrillDown(opcoes) {
    onDrill = opcoes.onDrill || null;
    return {
        registrar: registrar,
        obterNivel: function(widgetId) { return drillState[widgetId] || null; },
        voltar: voltar,
        limpar: function(widgetId) {
            delete drillState[widgetId];
            atualizarBreadcrumb(widgetId);
        },
        limparTodos: function() { drillState = {}; }
    };
}

var HIERARQUIAS_TEMPO = {
    year: { granularidade: 'year', proximo: 'quarter', rotulo: 'Ano' },
    quarter: { granularidade: 'quarter', proximo: 'month', rotulo: 'Trimestre' },
    month: { granularidade: 'month', proximo: 'week', rotulo: 'Mês' },
    week: { granularidade: 'week', proximo: 'day', rotulo: 'Semana' },
    day: { granularidade: 'day', proximo: 'hour', rotulo: 'Dia' },
    hour: { granularidade: 'hour', proximo: null, rotulo: 'Hora' }
};

function registrar(widgetId, chart, widget) {
    if (!chart || !chart.on) return;

    var consulta = widget.consulta || {};
    var td = (consulta.timeDimensions || [])[0];
    if (!td || !td.granularity) return;

    chart.on('click', function(params) {
        // So graficos com o tempo no eixo descem de nivel; o valor bruto do periodo
        // vem dos metadados do renderizador (o rotulo exibido ja esta formatado).
        var meta = chart.getDom().__dashMeta;
        if (!meta || !meta.eixoEhTempo || params.componentType !== 'series') return;
        var bruto = meta.categorias[params.dataIndex];
        if (bruto == null) return;

        var estado = drillState[widgetId] || {
            nivelOriginal: td.granularity,
            nivelAtual: td.granularity,
            caminho: []
        };

        var hier = HIERARQUIAS_TEMPO[estado.nivelAtual];
        if (!hier || !hier.proximo) return;
        var intervalo = intervaloDoPeriodo(bruto, estado.nivelAtual);
        if (!intervalo) return;

        estado.caminho.push({
            rotulo: meta.rotulosCategorias[params.dataIndex],
            granularidade: estado.nivelAtual,
            intervalo: intervalo
        });
        estado.nivelAtual = hier.proximo;

        drillState[widgetId] = estado;
        atualizarBreadcrumb(widgetId);
        if (onDrill) onDrill(widgetId, estado);
    });
}

function voltar(widgetId, manter) {
    var estado = drillState[widgetId];
    if (!estado || estado.caminho.length === 0) return;

    var alvo = manter == null ? estado.caminho.length - 1 : Math.max(0, manter);
    estado.caminho.length = Math.min(estado.caminho.length, alvo);

    if (estado.caminho.length === 0) {
        delete drillState[widgetId];
    } else {
        var ultimo = estado.caminho[estado.caminho.length - 1];
        var hier = HIERARQUIAS_TEMPO[ultimo.granularidade];
        estado.nivelAtual = hier ? hier.proximo : estado.nivelOriginal;
    }

    atualizarBreadcrumb(widgetId);
    if (onDrill) onDrill(widgetId, drillState[widgetId] || null);
}

function atualizarBreadcrumb(widgetId) {
    var containerId = 'drill-bc-' + widgetId;
    var container = document.getElementById(containerId);
    var estado = drillState[widgetId];

    if (!estado || estado.caminho.length === 0) {
        if (container) container.remove();
        return;
    }

    var card = document.querySelector('[data-widget-id="' + widgetId + '"]');
    if (!card) return;

    if (!container) {
        container = document.createElement('div');
        container.id = containerId;
        container.className = 'px-2 py-1';
        container.style.fontSize = '0.75rem';
        container.style.borderBottom = '1px solid var(--bs-border-color)';
        var header = card.querySelector('.widget-header');
        if (header) header.after(container);
    }

    var html = '<nav aria-label="Níveis do detalhamento"><ol class="breadcrumb mb-0" style="font-size:0.75rem">';
    html += '<li class="breadcrumb-item"><a href="#" data-drill-nivel="0" title="Voltar ao nível inicial">Início</a></li>';
    for (var i = 0; i < estado.caminho.length; i++) {
        var isLast = i === estado.caminho.length - 1;
        if (isLast) {
            html += '<li class="breadcrumb-item active">' + escapeHtml(estado.caminho[i].rotulo) + '</li>';
        } else {
            html += '<li class="breadcrumb-item"><a href="#" data-drill-nivel="' + (i + 1) + '">' + escapeHtml(estado.caminho[i].rotulo) + '</a></li>';
        }
    }
    var hier = HIERARQUIAS_TEMPO[estado.nivelAtual];
    if (hier) {
        html += '<li class="breadcrumb-item text-muted">[' + hier.rotulo + ']</li>';
    }
    html += '</ol></nav>';
    container.innerHTML = html;

    container.querySelectorAll('[data-drill-nivel]').forEach(function(el) {
        el.addEventListener('click', function(e) {
            e.preventDefault();
            voltar(widgetId, parseInt(el.dataset.drillNivel));
        });
    });
}

export function aplicarDrillAConsulta(consulta, drillEstado) {
    if (!drillEstado) return consulta;

    var nova = JSON.parse(JSON.stringify(consulta));
    if (nova.timeDimensions && nova.timeDimensions.length > 0) {
        nova.timeDimensions[0].granularity = drillEstado.nivelAtual;
        var ultimo = drillEstado.caminho[drillEstado.caminho.length - 1];
        if (ultimo && ultimo.intervalo) nova.timeDimensions[0].dateRange = ultimo.intervalo;
    }
    return nova;
}

/** Intervalo [inicio, fim] (datas inclusivas, yyyy-MM-dd) coberto por um periodo da granularidade. */
export function intervaloDoPeriodo(valor, granularidade) {
    var d = lerDataIso(valor);
    if (!d) return null;
    var ini, fim;
    switch (granularidade) {
        case 'year':    ini = utc(d.ano, 0, 1);           fim = utc(d.ano, 11, 31); break;
        case 'quarter': ini = utc(d.ano, d.mes - 1, 1);   fim = utc(d.ano, d.mes + 2, 0); break;
        case 'month':   ini = utc(d.ano, d.mes - 1, 1);   fim = utc(d.ano, d.mes, 0); break;
        case 'week':    ini = utc(d.ano, d.mes - 1, d.dia); fim = utc(d.ano, d.mes - 1, d.dia + 6); break;
        case 'day':     ini = utc(d.ano, d.mes - 1, d.dia); fim = ini; break;
        default: return null;
    }
    return [dataIso(ini), dataIso(fim)];
}

function utc(ano, mes, dia) { return new Date(Date.UTC(ano, mes, dia)); }
function dataIso(dt) { return dt.toISOString().slice(0, 10); }

function escapeHtml(s) { var d = document.createElement('div'); d.appendChild(document.createTextNode(String(s == null ? '' : s))); return d.innerHTML; }
