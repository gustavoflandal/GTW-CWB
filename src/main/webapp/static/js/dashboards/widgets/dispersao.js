export function renderizarDispersao(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        elemento.innerHTML = '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:var(--bs-secondary-color)"><div><i class="bi bi-inbox" style="font-size:2rem"></i><div>Sem dados</div></div></div>';
        return;
    }
    if (elemento.clientHeight < 100) elemento.style.height = '300px';

    var visual = widget.visual || {};
    var consulta = widget.consulta || {};
    var keys = Object.keys(dados[0]);

    var xKey = (consulta.measures && consulta.measures[0]) || null;
    var yKey = (consulta.measures && consulta.measures[1]) || null;
    var labelKey = (consulta.dimensions && consulta.dimensions[0]) || null;

    if (!xKey) { for (var k of keys) { if (typeof dados[0][k] === 'number') { xKey = k; break; } } }
    if (!yKey) { for (var k of keys) { if (typeof dados[0][k] === 'number' && k !== xKey) { yKey = k; break; } } }
    if (!yKey) yKey = xKey;

    var seriesData = dados.map(function(r) {
        return [r[xKey] || 0, r[yKey] || 0];
    });

    var option = {
        tooltip: {
            trigger: 'item',
            formatter: function(p) { return (xKey + ': ' + p.value[0] + '<br>' + yKey + ': ' + p.value[1]); }
        },
        grid: { left: '10%', right: '8%', bottom: '12%', top: '10%', containLabel: true },
        xAxis: { type: 'value', name: xKey },
        yAxis: { type: 'value', name: yKey },
        series: [{
            type: 'scatter',
            data: seriesData,
            symbolSize: visual.tamanho || 10,
            label: { show: visual.rotulos === true, fontSize: 10 }
        }],
        color: obterPaleta(visual.paleta)
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

function obterPaleta(nome) {
    var p = { padrao: ['#0d75bf','#28a745','#ffc107','#dc3545','#17a2b8','#6f42c1','#fd7e14','#20c997'], azul: ['#0d47a1','#1565c0','#1976d2','#1e88e5','#2196f3','#42a5f5','#64b5f6','#90caf9'], verde: ['#1b5e20','#2e7d32','#388e3c','#43a047','#4caf50','#66bb6a','#81c784','#a5d6a7'] };
    return p[nome] || p.padrao;
}
