import {
    htmlVazio, mostrarHtml, garantirAltura, montarSeries, registrarMeta, obterPaleta,
    escapeHtml, formatarNumero, aplicarOpcao
} from './comum.js';

export function renderizarPizza(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        mostrarHtml(elemento, htmlVazio());
        return;
    }
    garantirAltura(elemento);

    var visual = widget.visual || {};
    var isRosca = (widget.tipo || 'pizza') === 'rosca';
    var s = montarSeries(widget, dados);
    var valores = s.series[0] ? s.series[0].dados : [];
    var nomeMedida = s.series[0] ? s.series[0].nome : '';

    var seriesData = s.rotulosCategorias.map(function(rotulo, i) {
        return { name: rotulo, value: valores[i] == null ? 0 : valores[i] };
    });

    var option = {
        tooltip: {
            trigger: 'item',
            formatter: function(p) {
                return escapeHtml(nomeMedida) + '<br>' + p.marker + escapeHtml(p.name) + ': <b>' +
                    formatarNumero(p.value) + '</b> (' + formatarNumero(p.percent, 1) + '%)';
            }
        },
        legend: { type: 'scroll', orient: 'vertical', right: '5%', top: 'middle' },
        series: [{
            name: nomeMedida,
            type: 'pie',
            radius: isRosca ? ['40%', '70%'] : '65%',
            center: ['40%', '50%'],
            data: seriesData,
            label: {
                show: visual.rotulos !== false,
                formatter: function(p) { return p.name + ': ' + formatarNumero(p.percent, 1) + '%'; }
            },
            emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.3)' } }
        }],
        color: obterPaleta(visual.paleta)
    };

    registrarMeta(elemento, s);
    aplicarOpcao(elemento, option);
}
