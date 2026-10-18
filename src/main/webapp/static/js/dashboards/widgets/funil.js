import {
    htmlVazio, mostrarHtml, garantirAltura, montarSeries, registrarMeta, obterPaleta,
    escapeHtml, formatarNumero, aplicarOpcao
} from './comum.js';

export function renderizarFunil(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        mostrarHtml(elemento, htmlVazio());
        return;
    }
    garantirAltura(elemento);

    var visual = widget.visual || {};
    var s = montarSeries(widget, dados);
    var valores = s.series[0] ? s.series[0].dados : [];
    var nomeMedida = s.series[0] ? s.series[0].nome : '';

    // Ordena mantendo o vinculo com a categoria bruta (usada pelo filtro cruzado).
    var itens = s.categorias.map(function(c, i) {
        return { bruto: c, name: s.rotulosCategorias[i], value: valores[i] == null ? 0 : valores[i] };
    });
    itens.sort(function(a, b) { return b.value - a.value; });
    s.categorias = itens.map(function(x) { return x.bruto; });
    s.rotulosCategorias = itens.map(function(x) { return x.name; });

    var option = {
        tooltip: {
            trigger: 'item',
            formatter: function(p) {
                return escapeHtml(nomeMedida) + '<br>' + p.marker + escapeHtml(p.name) + ': <b>' + formatarNumero(p.value) + '</b>';
            }
        },
        legend: { type: 'scroll', orient: 'vertical', right: '5%', top: 'middle' },
        series: [{
            name: nomeMedida,
            type: 'funnel',
            left: '5%',
            width: '60%',
            top: '10%',
            bottom: '10%',
            sort: 'none',
            gap: 2,
            data: itens.map(function(x) { return { name: x.name, value: x.value }; }),
            label: {
                show: visual.rotulos !== false,
                position: 'inside',
                formatter: function(p) { return p.name + ': ' + formatarNumero(p.value); }
            },
            emphasis: { label: { fontSize: 14 } }
        }],
        color: obterPaleta(visual.paleta)
    };

    registrarMeta(elemento, s);
    aplicarOpcao(elemento, option);
}
