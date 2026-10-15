export function renderizarFunil(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        elemento.innerHTML = '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:var(--bs-secondary-color)"><div><i class="bi bi-inbox" style="font-size:2rem"></i><div>Sem dados</div></div></div>';
        return;
    }
    if (elemento.clientHeight < 100) elemento.style.height = '300px';

    var visual = widget.visual || {};
    var consulta = widget.consulta || {};
    var keys = Object.keys(dados[0]);

    var labelKey = (consulta.dimensions && consulta.dimensions[0]) || null;
    var valueKey = (consulta.measures && consulta.measures[0]) || null;

    if (!labelKey) { for (var k of keys) { if (typeof dados[0][k] === 'string') { labelKey = k; break; } } }
    if (!valueKey) { for (var k of keys) { if (typeof dados[0][k] === 'number') { valueKey = k; break; } } }
    if (!labelKey) labelKey = keys[0];
    if (!valueKey && keys.length > 1) valueKey = keys[1];

    var seriesData = dados.map(function(r) {
        return { name: r[labelKey] || '', value: r[valueKey] || 0 };
    });
    seriesData.sort(function(a, b) { return b.value - a.value; });

    var option = {
        tooltip: { trigger: 'item', formatter: '{b}: {c}' },
        legend: { orient: 'vertical', right: '5%', top: 'middle' },
        series: [{
            type: 'funnel',
            left: '5%',
            width: '60%',
            top: '10%',
            bottom: '10%',
            sort: 'descending',
            gap: 2,
            data: seriesData,
            label: { show: visual.rotulos !== false, position: 'inside', formatter: '{b}: {c}' },
            emphasis: { label: { fontSize: 14 } }
        }],
        color: obterPaleta(visual.paleta)
    };

    try {
        if (typeof echarts === 'undefined') { elemento.innerHTML = '<div class="text-danger text-center p-3"><i class="bi bi-exclamation-triangle"></i> ECharts nao carregado</div>'; return; }
        var chart = echarts.getInstanceByDom(elemento) || echarts.init(elemento);
        chart.setOption(option, true);
        if (!elemento.__observadorResize) { elemento.__observadorResize = new ResizeObserver(function() { var c = echarts.getInstanceByDom(elemento); if (c) c.resize(); }); elemento.__observadorResize.observe(elemento); }
    } catch (e) {
        elemento.innerHTML = '<div class="text-danger text-center p-3"><i class="bi bi-exclamation-triangle"></i> Erro: ' + e.message + '</div>';
    }
}

function obterPaleta(nome) {
    var p = { padrao: ['#0d75bf','#28a745','#ffc107','#dc3545','#17a2b8','#6f42c1','#fd7e14','#20c997'], azul: ['#0d47a1','#1565c0','#1976d2','#1e88e5','#2196f3','#42a5f5','#64b5f6','#90caf9'], verde: ['#1b5e20','#2e7d32','#388e3c','#43a047','#4caf50','#66bb6a','#81c784','#a5d6a7'] };
    return p[nome] || p.padrao;
}
