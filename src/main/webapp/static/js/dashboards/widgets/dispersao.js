import {
    htmlVazio, mostrarHtml, garantirAltura, obterPaleta, escapeHtml, formatarNumero, eixoValor,
    rotuloCampo, aplicarOpcao, ROTULO_NULO
} from './comum.js';

export function renderizarDispersao(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        mostrarHtml(elemento, htmlVazio());
        return;
    }
    garantirAltura(elemento);

    var visual = widget.visual || {};
    var consulta = widget.consulta || {};
    var keys = Object.keys(dados[0]);

    var xKey = (consulta.measures && consulta.measures[0]) || null;
    var yKey = (consulta.measures && consulta.measures[1]) || null;
    var labelKey = (consulta.dimensions && consulta.dimensions[0]) || null;

    if (!xKey) { for (var k of keys) { if (typeof dados[0][k] === 'number') { xKey = k; break; } } }
    if (!yKey) { for (var k2 of keys) { if (typeof dados[0][k2] === 'number' && k2 !== xKey) { yKey = k2; break; } } }
    if (!yKey) yKey = xKey;

    var rotuloX = rotuloCampo(widget, xKey);
    var rotuloY = rotuloCampo(widget, yKey);

    var seriesData = dados
        .filter(function(r) { return r[xKey] != null && r[yKey] != null; })
        .map(function(r) {
            var nome = labelKey ? (r[labelKey] == null ? ROTULO_NULO : String(r[labelKey])) : '';
            return { name: nome, value: [r[xKey], r[yKey]] };
        });

    var option = {
        tooltip: {
            trigger: 'item',
            formatter: function(p) {
                var html = p.name ? '<b>' + escapeHtml(p.name) + '</b><br>' : '';
                return html + escapeHtml(rotuloX) + ': ' + formatarNumero(p.value[0]) +
                    '<br>' + escapeHtml(rotuloY) + ': ' + formatarNumero(p.value[1]);
            }
        },
        grid: { left: '10%', right: '8%', bottom: '12%', top: '10%', containLabel: true },
        xAxis: eixoValor({ name: rotuloX }),
        yAxis: eixoValor({ name: rotuloY }),
        series: [{
            type: 'scatter',
            data: seriesData,
            symbolSize: visual.tamanho || 10,
            label: { show: visual.rotulos === true, fontSize: 10, formatter: '{b}' }
        }],
        color: obterPaleta(visual.paleta)
    };

    aplicarOpcao(elemento, option);
}
