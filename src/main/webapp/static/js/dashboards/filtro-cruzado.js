var filtroAtivo = null;
var onFiltroAlterado = null;

export function inicializarFiltroCruzado(opcoes) {
    onFiltroAlterado = opcoes.onFiltroAlterado || null;
    return {
        registrarClique: registrarClique,
        obterFiltro: function() { return filtroAtivo; },
        limpar: limpar,
        estaAtivo: function() { return filtroAtivo !== null; }
    };
}

function registrarClique(widgetId, chart, widget) {
    if (!chart || !chart.on) return;

    chart.on('click', function(params) {
        if (!params.name && params.value == null) return;

        var dimensao = null;
        var consulta = widget.consulta || {};
        if (consulta.dimensions && consulta.dimensions.length > 0) {
            dimensao = consulta.dimensions[0];
        }
        if (!dimensao) return;

        var valor = params.name || String(params.value);

        if (filtroAtivo && filtroAtivo.widgetId === widgetId &&
            filtroAtivo.dimensao === dimensao && filtroAtivo.valor === valor) {
            limpar();
            return;
        }

        filtroAtivo = {
            widgetId: widgetId,
            dimensao: dimensao,
            valor: valor
        };

        atualizarIndicadorVisual(widgetId, true);
        if (onFiltroAlterado) onFiltroAlterado(filtroAtivo);
    });
}

function limpar() {
    if (filtroAtivo) {
        atualizarIndicadorVisual(filtroAtivo.widgetId, false);
    }
    filtroAtivo = null;
    if (onFiltroAlterado) onFiltroAlterado(null);
}

function atualizarIndicadorVisual(widgetId, ativo) {
    var card = document.querySelector('[data-widget-id="' + widgetId + '"]');
    if (!card) return;

    var header = card.querySelector('.widget-header');
    if (!header) return;

    var badge = header.querySelector('.badge-filtro-cruzado');
    if (ativo && !badge) {
        badge = document.createElement('span');
        badge.className = 'badge bg-warning text-dark badge-filtro-cruzado ms-2';
        badge.style.fontSize = '0.7rem';
        badge.innerHTML = '<i class="bi bi-funnel-fill"></i> Filtro ativo';
        badge.style.cursor = 'pointer';
        badge.addEventListener('click', function(e) {
            e.stopPropagation();
            limpar();
        });
        header.querySelector('span').appendChild(badge);
    } else if (!ativo && badge) {
        badge.remove();
    }
}

export function aplicarFiltroCruzadoAConsulta(consulta, filtro) {
    if (!filtro) return consulta;

    var nova = JSON.parse(JSON.stringify(consulta));
    if (!nova.filters) nova.filters = [];
    nova.filters.push({
        member: filtro.dimensao,
        operator: 'equals',
        values: [filtro.valor]
    });
    return nova;
}
