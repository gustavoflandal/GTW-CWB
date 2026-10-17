var filtroAtivo = null;
var onFiltroAlterado = null;

export function inicializarFiltroCruzado(opcoes) {
    onFiltroAlterado = opcoes.onFiltroAlterado || null;
    return {
        registrarClique: registrarClique,
        obterFiltro: function() { return filtroAtivo; },
        limpar: limpar,
        // Descarta o filtro sem recarregar (ex.: ao abrir outro painel).
        resetar: function() { filtroAtivo = null; },
        estaAtivo: function() { return filtroAtivo !== null; }
    };
}

function registrarClique(widgetId, chart, widget) {
    if (!chart || !chart.on) return;

    chart.on('click', function(params) {
        // O valor bruto da categoria vem dos metadados do renderizador: o rotulo
        // exibido pode estar formatado. Eixo de tempo e tratado pelo drill-down;
        // mapa de calor, dispersao e gauge nao tem categoria para filtrar.
        var meta = chart.getDom().__dashMeta;
        if (!meta || meta.eixoEhTempo || params.componentType !== 'series') return;

        var consulta = widget.consulta || {};
        var dimensao = meta.eixo;
        if (!consulta.dimensions || consulta.dimensions.indexOf(dimensao) < 0) return;

        var bruto = meta.categorias[params.dataIndex];
        if (bruto == null) return;
        var valor = String(bruto);

        if (filtroAtivo && filtroAtivo.widgetId === widgetId &&
            filtroAtivo.dimensao === dimensao && filtroAtivo.valor === valor) {
            limpar();
            return;
        }

        if (filtroAtivo && filtroAtivo.widgetId !== widgetId) {
            atualizarIndicadorVisual(filtroAtivo.widgetId, false);
        }

        filtroAtivo = {
            widgetId: widgetId,
            dimensao: dimensao,
            valor: valor,
            rotulo: meta.rotulosCategorias[params.dataIndex]
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
    if (ativo) {
        if (!badge) {
            badge = document.createElement('span');
            badge.className = 'badge bg-warning text-dark badge-filtro-cruzado ms-2';
            badge.style.fontSize = '0.7rem';
            badge.style.cursor = 'pointer';
            badge.addEventListener('click', function(e) {
                e.stopPropagation();
                limpar();
            });
            header.querySelector('span').appendChild(badge);
        }
        badge.innerHTML = '<i class="bi bi-funnel-fill"></i> ';
        badge.appendChild(document.createTextNode(filtroAtivo.rotulo || filtroAtivo.valor));
        badge.title = 'Filtrando os demais widgets do mesmo dataset por "' +
            (filtroAtivo.rotulo || filtroAtivo.valor) + '". Clique para remover o filtro.';
    } else if (badge) {
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
