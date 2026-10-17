import {
    htmlVazio, garantirAltura, escapeHtml, formatarNumero, formatarTempo, infoTempo,
    medidasDe, rotuloCampo, aplicarOpcao, ROTULO_NULO
} from './comum.js';

export function renderizarHeatmap(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        elemento.innerHTML = htmlVazio();
        return;
    }
    garantirAltura(elemento);

    var visual = widget.visual || {};
    var consulta = widget.consulta || {};
    var keys = Object.keys(dados[0]);
    var tempo = infoTempo(widget, dados);

    var dim1 = (consulta.dimensions && consulta.dimensions[0]) || null;
    var dim2 = (consulta.dimensions && consulta.dimensions[1]) || (tempo ? tempo.chave : null);
    var valueKey = medidasDe(widget, dados)[0] || null;

    if (!dim1) { for (var k of keys) { if (typeof dados[0][k] === 'string' && k !== dim2) { dim1 = k; break; } } }
    if (!dim2) { for (var k2 of keys) { if (typeof dados[0][k2] === 'string' && k2 !== dim1) { dim2 = k2; break; } } }

    if (!dim1 || !valueKey) {
        elemento.innerHTML = '<div class="text-warning text-center p-3"><i class="bi bi-info-circle"></i> O mapa de calor precisa de ao menos 1 dimensão e 1 medida</div>';
        return;
    }
    if (!dim2) dim2 = dim1;

    function rotulo(chave, v) {
        if (tempo && chave === tempo.chave) return formatarTempo(v, tempo.granularidade);
        return v == null || v === '' ? ROTULO_NULO : String(v);
    }

    var xLabels = [];
    var yLabels = [];
    var xMap = {};
    var yMap = {};
    var seriesData = [];
    var minVal = null;
    var maxVal = null;

    dados.forEach(function(r) {
        var x = rotulo(dim1, r[dim1]);
        var y = rotulo(dim2, r[dim2]);
        if (!(x in xMap)) { xMap[x] = xLabels.length; xLabels.push(x); }
        if (!(y in yMap)) { yMap[y] = yLabels.length; yLabels.push(y); }
        var v = r[valueKey];
        if (v == null) return;
        seriesData.push([xMap[x], yMap[y], v]);
        if (minVal == null || v < minVal) minVal = v;
        if (maxVal == null || v > maxVal) maxVal = v;
    });

    var rotuloMedida = rotuloCampo(widget, valueKey);
    var option = {
        tooltip: {
            position: 'top',
            formatter: function(p) {
                return escapeHtml(xLabels[p.value[0]]) + ' / ' + escapeHtml(yLabels[p.value[1]]) + '<br>' +
                    escapeHtml(rotuloMedida) + ': <b>' + formatarNumero(p.value[2]) + '</b>';
            }
        },
        grid: { left: '15%', right: '10%', bottom: '15%', top: '5%' },
        xAxis: { type: 'category', data: xLabels, splitArea: { show: true }, axisLabel: { rotate: xLabels.length > 8 ? 45 : 0 } },
        yAxis: { type: 'category', data: yLabels, splitArea: { show: true } },
        visualMap: {
            min: Math.min(0, minVal || 0), max: maxVal || 1, calculable: true, orient: 'horizontal', left: 'center', bottom: '0%',
            formatter: function(v) { return formatarNumero(v, 0); },
            inRange: { color: ['#f7fbff', '#0d75bf'] }
        },
        series: [{
            name: rotuloMedida,
            type: 'heatmap',
            data: seriesData,
            label: { show: visual.rotulos !== false, fontSize: 10, formatter: function(p) { return formatarNumero(p.value[2]); } },
            emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' } }
        }]
    };

    aplicarOpcao(elemento, option);
}
