import {
    htmlVazio, mostrarHtml, garantirAltura, montarSeries, registrarMeta, obterPaleta,
    tooltipEixo, eixoValor, rotuloValor, aplicarOpcao
} from './comum.js';

export function renderizarCombinado(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        mostrarHtml(elemento, htmlVazio());
        return;
    }
    garantirAltura(elemento);

    var visual = widget.visual || {};
    var s = montarSeries(widget, dados, { todasNumericas: true });
    // linhaIndices refere-se a posicao da medida; por padrao a ultima vira linha.
    var linhaIndices = visual.linhaIndices || [s.medidas.length - 1];

    var series = s.series.map(function(serie) {
        var isLinha = linhaIndices.indexOf(s.medidas.indexOf(serie.medida)) >= 0;
        return {
            name: serie.nome,
            type: isLinha ? 'line' : 'bar',
            data: serie.dados,
            yAxisIndex: isLinha ? 1 : 0,
            smooth: visual.suave !== false,
            label: rotuloValor({ show: visual.rotulos === true, fontSize: 11 })
        };
    });

    var option = {
        tooltip: tooltipEixo(),
        grid: { left: '8%', right: '8%', bottom: '12%', top: '14%', containLabel: true },
        legend: { type: 'scroll', data: series.map(function(x) { return x.name; }) },
        xAxis: { type: 'category', data: s.rotulosCategorias, axisLabel: { rotate: s.categorias.length > 8 ? 30 : 0 } },
        yAxis: [eixoValor({ position: 'left' }), eixoValor({ position: 'right' })],
        series: series,
        color: obterPaleta(visual.paleta)
    };

    registrarMeta(elemento, s);
    aplicarOpcao(elemento, option);
}
