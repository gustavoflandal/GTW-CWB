import { htmlVazio, garantirAltura, escapeHtml, formatarNumero, medidasDe, rotuloCampo, aplicarOpcao } from './comum.js';

export function renderizarGauge(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        elemento.innerHTML = htmlVazio();
        return;
    }
    garantirAltura(elemento);

    var visual = widget.visual || {};
    var valueKey = medidasDe(widget, dados)[0] || Object.keys(dados[0])[0];
    var nome = widget.titulo || rotuloCampo(widget, valueKey);

    var valor = dados[0][valueKey] == null ? 0 : dados[0][valueKey];
    var min = visual.min || 0;
    var max = visual.max || 100;

    var option = {
        tooltip: {
            formatter: function(p) { return escapeHtml(rotuloCampo(widget, valueKey)) + ': <b>' + formatarNumero(p.value) + '</b>'; }
        },
        series: [{
            name: nome,
            type: 'gauge',
            min: min,
            max: max,
            detail: {
                formatter: function(v) { return formatarNumero(v); },
                fontSize: 20,
                offsetCenter: [0, '70%']
            },
            data: [{ value: valor, name: nome }],
            axisLine: {
                lineStyle: {
                    width: 15,
                    color: [
                        [0.3, '#28a745'],
                        [0.7, '#ffc107'],
                        [1, '#dc3545']
                    ]
                }
            },
            axisLabel: { formatter: function(v) { return formatarNumero(v, 0); } },
            pointer: { length: '60%', width: 6 },
            title: { fontSize: 13, offsetCenter: [0, '90%'] }
        }]
    };

    if (visual.faixas && visual.faixas.length > 0) {
        // Faixas absolutas convertidas para a fracao do arco entre min e max.
        var amplitude = (max - min) || 1;
        option.series[0].axisLine.lineStyle.color = visual.faixas.map(function(f) {
            return [Math.min(1, Math.max(0, (f.ate - min) / amplitude)), f.cor];
        });
    }

    aplicarOpcao(elemento, option);
}
