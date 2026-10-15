export function renderizarHeatmap(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        elemento.innerHTML = '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:var(--bs-secondary-color)"><div><i class="bi bi-inbox" style="font-size:2rem"></i><div>Sem dados</div></div></div>';
        return;
    }
    if (elemento.clientHeight < 100) elemento.style.height = '300px';

    var visual = widget.visual || {};
    var consulta = widget.consulta || {};
    var keys = Object.keys(dados[0]);

    var dim1 = (consulta.dimensions && consulta.dimensions[0]) || null;
    var dim2 = (consulta.dimensions && consulta.dimensions[1]) || null;
    var valueKey = (consulta.measures && consulta.measures[0]) || null;

    if (!dim1) { for (var k of keys) { if (typeof dados[0][k] === 'string') { dim1 = k; break; } } }
    if (!dim2) { for (var k of keys) { if (typeof dados[0][k] === 'string' && k !== dim1) { dim2 = k; break; } } }
    if (!valueKey) { for (var k of keys) { if (typeof dados[0][k] === 'number') { valueKey = k; break; } } }

    if (!dim1 || !valueKey) {
        elemento.innerHTML = '<div class="text-warning text-center p-3"><i class="bi bi-info-circle"></i> Heatmap precisa de ao menos 1 dimensao e 1 medida</div>';
        return;
    }
    if (!dim2) dim2 = dim1;

    var xLabels = [];
    var yLabels = [];
    var xMap = {};
    var yMap = {};

    dados.forEach(function(r) {
        var x = String(r[dim1] || '');
        var y = String(r[dim2] || '');
        if (!(x in xMap)) { xMap[x] = xLabels.length; xLabels.push(x); }
        if (!(y in yMap)) { yMap[y] = yLabels.length; yLabels.push(y); }
    });

    var seriesData = [];
    var maxVal = 0;
    dados.forEach(function(r) {
        var x = xMap[String(r[dim1] || '')];
        var y = yMap[String(r[dim2] || '')];
        var v = r[valueKey] || 0;
        seriesData.push([x, y, v]);
        if (v > maxVal) maxVal = v;
    });

    var option = {
        tooltip: { position: 'top', formatter: function(p) { return xLabels[p.value[0]] + ' / ' + yLabels[p.value[1]] + ': ' + p.value[2]; } },
        grid: { left: '15%', right: '10%', bottom: '15%', top: '5%' },
        xAxis: { type: 'category', data: xLabels, splitArea: { show: true }, axisLabel: { rotate: xLabels.length > 8 ? 45 : 0 } },
        yAxis: { type: 'category', data: yLabels, splitArea: { show: true } },
        visualMap: { min: 0, max: maxVal || 1, calculable: true, orient: 'horizontal', left: 'center', bottom: '0%', inRange: { color: ['#f7fbff', '#0d75bf'] } },
        series: [{
            type: 'heatmap',
            data: seriesData,
            label: { show: visual.rotulos !== false, fontSize: 10 },
            emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' } }
        }]
    };

    try {
        if (typeof echarts === 'undefined') { elemento.innerHTML = '<div class="text-danger text-center p-3"><i class="bi bi-exclamation-triangle"></i> ECharts nao carregado</div>'; return; }
        var chart = echarts.getInstanceByDom(elemento) || echarts.init(elemento);
        chart.setOption(option, true);
        new ResizeObserver(function() { chart.resize(); }).observe(elemento);
    } catch (e) {
        elemento.innerHTML = '<div class="text-danger text-center p-3"><i class="bi bi-exclamation-triangle"></i> Erro: ' + e.message + '</div>';
    }
}
