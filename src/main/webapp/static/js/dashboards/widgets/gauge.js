export function renderizarGauge(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        elemento.innerHTML = '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:var(--bs-secondary-color)"><div><i class="bi bi-inbox" style="font-size:2rem"></i><div>Sem dados</div></div></div>';
        return;
    }
    if (elemento.clientHeight < 100) elemento.style.height = '300px';

    var visual = widget.visual || {};
    var consulta = widget.consulta || {};
    var keys = Object.keys(dados[0]);

    var valueKey = (consulta.measures && consulta.measures[0]) || null;
    if (!valueKey) { for (var k of keys) { if (typeof dados[0][k] === 'number') { valueKey = k; break; } } }
    if (!valueKey) valueKey = keys[0];

    var valor = dados[0][valueKey] || 0;
    var min = visual.min || 0;
    var max = visual.max || 100;

    var option = {
        tooltip: { formatter: '{a}: {c}' },
        series: [{
            name: widget.titulo || valueKey,
            type: 'gauge',
            min: min,
            max: max,
            detail: {
                formatter: '{value}',
                fontSize: 20,
                offsetCenter: [0, '70%']
            },
            data: [{ value: valor, name: widget.titulo || valueKey }],
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
            pointer: { length: '60%', width: 6 },
            title: { fontSize: 13, offsetCenter: [0, '90%'] }
        }]
    };

    if (visual.faixas) {
        try {
            var faixas = visual.faixas;
            var cores = [];
            for (var i = 0; i < faixas.length; i++) {
                cores.push([faixas[i].ate / max, faixas[i].cor]);
            }
            option.series[0].axisLine.lineStyle.color = cores;
        } catch (e) { /* usa faixas padrao */ }
    }

    try {
        if (typeof echarts === 'undefined') { elemento.innerHTML = '<div class="text-danger text-center p-3"><i class="bi bi-exclamation-triangle"></i> ECharts nao carregado</div>'; return; }
        var chart = echarts.getInstanceByDom(elemento) || echarts.init(elemento);
        chart.setOption(option, true);
        new ResizeObserver(function() { chart.resize(); }).observe(elemento);
    } catch (e) {
        elemento.innerHTML = '<div class="text-danger text-center p-3"><i class="bi bi-exclamation-triangle"></i> Erro: ' + e.message + '</div>';
    }
}
