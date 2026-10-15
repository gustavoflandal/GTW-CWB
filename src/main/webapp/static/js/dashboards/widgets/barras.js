/**
 * barras.js — renderizador de widget de barras (ECharts).
 * Interface: renderizarBarras(elemento, especificacao, dados)
 * Trata estados: carregando, vazio, erro.
 */

export function renderizarBarras(elemento, widget, dados) {
    if (!elemento) return;

    // Estado vazio
    if (!dados || dados.length === 0) {
        elemento.innerHTML = '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:var(--bs-secondary-color)"><div><i class="bi bi-inbox" style="font-size:2rem"></i><div>Sem dados</div></div></div>';
        return;
    }

    // Garantir altura minima
    if (elemento.clientHeight < 100) {
        elemento.style.height = '300px';
    }

    const visual = widget.visual || {};
    const consulta = widget.consulta || {};

    // Extrair labels e valores dos dados
    const keys = Object.keys(dados[0]);
    // Heuristica: primeiro campo string = label, primeiro campo numerico = valor
    let labelKey = null;
    let valueKeys = [];

    if (consulta.dimensions && consulta.dimensions.length > 0) {
        labelKey = consulta.dimensions[0];
    }
    if (consulta.measures && consulta.measures.length > 0) {
        valueKeys = consulta.measures;
    }

    // Fallback: inferir dos dados
    if (!labelKey) {
        for (const k of keys) {
            if (typeof dados[0][k] === 'string') { labelKey = k; break; }
        }
    }
    if (valueKeys.length === 0) {
        for (const k of keys) {
            if (typeof dados[0][k] === 'number') { valueKeys.push(k); break; }
        }
    }

    if (!labelKey) labelKey = keys[0];
    if (valueKeys.length === 0 && keys.length > 1) valueKeys = [keys[1]];

    const labels = dados.map(r => r[labelKey] || '');
    const orientacao = visual.orientacao || 'vertical';
    const empilhado = visual.empilhado || false;

    // Montar series
    const series = valueKeys.map(vk => ({
        name: vk,
        type: 'bar',
        data: dados.map(r => r[vk] || 0),
        stack: empilhado ? 'stack' : undefined,
        label: {
            show: visual.rotulos !== false,
            position: orientacao === 'horizontal' ? 'right' : 'top',
            fontSize: 11
        }
    }));

    const option = {
        tooltip: { trigger: 'axis' },
        grid: { left: '8%', right: '4%', bottom: '12%', top: '8%', containLabel: true },
        legend: valueKeys.length > 1 ? { data: valueKeys } : undefined
    };

    if (orientacao === 'horizontal') {
        option.yAxis = { type: 'category', data: labels };
        option.xAxis = { type: 'value' };
    } else {
        option.xAxis = { type: 'category', data: labels, axisLabel: { rotate: labels.length > 8 ? 30 : 0 } };
        option.yAxis = { type: 'value' };
    }

    option.series = series;

    // Paleta
    const paletas = {
        padrao: ['#0d75bf', '#28a745', '#ffc107', '#dc3545', '#17a2b8', '#6f42c1', '#fd7e14', '#20c997'],
        azul: ['#0d47a1', '#1565c0', '#1976d2', '#1e88e5', '#2196f3', '#42a5f5', '#64b5f6', '#90caf9'],
        verde: ['#1b5e20', '#2e7d32', '#388e3c', '#43a047', '#4caf50', '#66bb6a', '#81c784', '#a5d6a7']
    };
    option.color = paletas[visual.paleta] || paletas.padrao;

    // Renderizar com ECharts
    try {
        // Verificar se ECharts esta disponivel
        if (typeof echarts === 'undefined') {
            elemento.innerHTML = '<div style="color:var(--bs-danger);text-align:center;padding:1rem"><i class="bi bi-exclamation-triangle"></i> ECharts nao carregado</div>';
            return;
        }

        let chart = echarts.getInstanceByDom(elemento);
        if (!chart) {
            chart = echarts.init(elemento);
        }
        chart.setOption(option, true);

        if (!elemento.__observadorResize) {
            elemento.__observadorResize = new ResizeObserver(() => {
                const c = echarts.getInstanceByDom(elemento);
                if (c) c.resize();
            });
            elemento.__observadorResize.observe(elemento);
        }

    } catch (e) {
        elemento.innerHTML = '<div style="color:var(--bs-danger);text-align:center;padding:1rem"><i class="bi bi-exclamation-triangle"></i> Erro: ' + e.message + '</div>';
    }
}
