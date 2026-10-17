/**
 * barras.js — renderizador de widget de barras (ECharts).
 * Interface: renderizarBarras(elemento, especificacao, dados)
 * Trata estados: vazio e erro.
 */
import {
    htmlVazio, garantirAltura, montarSeries, registrarMeta, obterPaleta,
    tooltipEixo, eixoValor, rotuloValor, aplicarOpcao
} from './comum.js';

export function renderizarBarras(elemento, widget, dados) {
    if (!elemento) return;

    if (!dados || dados.length === 0) {
        elemento.innerHTML = htmlVazio();
        return;
    }
    garantirAltura(elemento);

    var visual = widget.visual || {};
    var orientacao = visual.orientacao || 'vertical';
    var empilhado = visual.empilhado || false;
    var s = montarSeries(widget, dados);

    var series = s.series.map(function(serie) {
        return {
            name: serie.nome,
            type: 'bar',
            data: serie.dados,
            stack: empilhado ? 'stack' : undefined,
            label: rotuloValor({
                show: visual.rotulos !== false,
                position: empilhado ? 'inside' : (orientacao === 'horizontal' ? 'right' : 'top'),
                fontSize: 11
            })
        };
    });

    var eixoCategorias = { type: 'category', data: s.rotulosCategorias };
    var option = {
        tooltip: tooltipEixo(),
        grid: { left: '8%', right: '4%', bottom: '12%', top: series.length > 1 ? '14%' : '8%', containLabel: true },
        legend: series.length > 1 ? { type: 'scroll', data: series.map(function(x) { return x.name; }) } : undefined,
        series: series,
        color: obterPaleta(visual.paleta)
    };

    if (orientacao === 'horizontal') {
        option.yAxis = eixoCategorias;
        option.xAxis = eixoValor();
    } else {
        eixoCategorias.axisLabel = { rotate: s.categorias.length > 8 ? 30 : 0 };
        option.xAxis = eixoCategorias;
        option.yAxis = eixoValor();
    }

    registrarMeta(elemento, s);
    aplicarOpcao(elemento, option);
}
