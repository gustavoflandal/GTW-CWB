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
        }
    };
}

var HIERARQUIAS_TEMPO = {
    year: { granularidade: 'year', proximo: 'quarter', rotulo: 'Ano' },
    quarter: { granularidade: 'quarter', proximo: 'month', rotulo: 'Trimestre' },
    month: { granularidade: 'month', proximo: 'week', rotulo: 'Mes' },
    week: { granularidade: 'week', proximo: 'day', rotulo: 'Semana' },
    day: { granularidade: 'day', proximo: null, rotulo: 'Dia' }
};

function registrar(widgetId, chart, widget) {
    if (!chart || !chart.on) return;

    var consulta = widget.consulta || {};
    var temTempo = consulta.timeDimensions && consulta.timeDimensions.length > 0;
    if (!temTempo) return;

    chart.on('click', function(params) {
        if (!params.name) return;

        var estado = drillState[widgetId] || {
            nivelOriginal: consulta.timeDimensions[0].granularity || 'month',
            nivelAtual: consulta.timeDimensions[0].granularity || 'month',
            caminho: [],
            filtrosDate: []
        };

        var hier = HIERARQUIAS_TEMPO[estado.nivelAtual];
        if (!hier || !hier.proximo) return;

        estado.caminho.push({ rotulo: params.name, granularidade: estado.nivelAtual });
        estado.filtrosDate.push(params.name);
        estado.nivelAtual = hier.proximo;

        drillState[widgetId] = estado;
        atualizarBreadcrumb(widgetId);
        if (onDrill) onDrill(widgetId, estado);
    });
}

function voltar(widgetId) {
    var estado = drillState[widgetId];
    if (!estado || estado.caminho.length === 0) return;

    estado.caminho.pop();
    estado.filtrosDate.pop();

    if (estado.caminho.length === 0) {
        estado.nivelAtual = estado.nivelOriginal;
    } else {
        var ultimo = estado.caminho[estado.caminho.length - 1];
        var hier = HIERARQUIAS_TEMPO[ultimo.granularidade];
        estado.nivelAtual = hier ? hier.proximo : estado.nivelOriginal;
    }

    atualizarBreadcrumb(widgetId);
    if (onDrill) onDrill(widgetId, estado);
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

    var html = '<nav><ol class="breadcrumb mb-0" style="font-size:0.75rem">';
    html += '<li class="breadcrumb-item"><a href="#" data-drill-back="' + widgetId + '">Inicio</a></li>';
    for (var i = 0; i < estado.caminho.length; i++) {
        var isLast = i === estado.caminho.length - 1;
        if (isLast) {
            html += '<li class="breadcrumb-item active">' + escapeHtml(estado.caminho[i].rotulo) + '</li>';
        } else {
            html += '<li class="breadcrumb-item"><a href="#">' + escapeHtml(estado.caminho[i].rotulo) + '</a></li>';
        }
    }
    var hier = HIERARQUIAS_TEMPO[estado.nivelAtual];
    if (hier) {
        html += '<li class="breadcrumb-item text-muted">[' + hier.rotulo + ']</li>';
    }
    html += '</ol></nav>';
    container.innerHTML = html;

    container.querySelectorAll('[data-drill-back]').forEach(function(el) {
        el.addEventListener('click', function(e) {
            e.preventDefault();
            while (drillState[widgetId] && drillState[widgetId].caminho.length > 0) {
                voltar(widgetId);
            }
        });
    });
}

export function aplicarDrillAConsulta(consulta, drillEstado) {
    if (!drillEstado) return consulta;

    var nova = JSON.parse(JSON.stringify(consulta));
    if (nova.timeDimensions && nova.timeDimensions.length > 0) {
        nova.timeDimensions[0].granularity = drillEstado.nivelAtual;
    }
    return nova;
}

function escapeHtml(s) { var d = document.createElement('div'); d.appendChild(document.createTextNode(String(s || ''))); return d.innerHTML; }
