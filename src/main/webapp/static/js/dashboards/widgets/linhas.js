import {
    htmlVazio, garantirAltura, montarSeries, registrarMeta, obterPaleta,
    tooltipEixo, eixoValor, rotuloValor, aplicarOpcao
} from './comum.js';

export function renderizarLinhas(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        elemento.innerHTML = htmlVazio();
        return;
    }
    garantirAltura(elemento);

    var visual = widget.visual || {};
    var s = montarSeries(widget, dados, { tempoNoEixo: true });

    var series = s.series.map(function(serie) {
        return {
            name: serie.nome,
            type: 'line',
            data: serie.dados,
            connectNulls: false,
            smooth: visual.suave !== false,
            label: rotuloValor({ show: visual.rotulos === true, fontSize: 11 })
        };
    });

    var option = {
        tooltip: tooltipEixo(),
        grid: { left: '8%', right: '4%', bottom: '12%', top: series.length > 1 ? '14%' : '8%', containLabel: true },
        xAxis: { type: 'category', data: s.rotulosCategorias, axisLabel: { rotate: s.categorias.length > 8 ? 30 : 0 } },
        yAxis: eixoValor(),
        series: series,
        legend: series.length > 1 ? { type: 'scroll', data: series.map(function(x) { return x.name; }) } : undefined,
        color: obterPaleta(visual.paleta)
    };

    registrarMeta(elemento, s);
    aplicarOpcao(elemento, option);
}
