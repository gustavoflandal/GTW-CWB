import { renderizarBarras } from './widgets/barras.js';
import { renderizarLinhas } from './widgets/linhas.js';
import { renderizarArea } from './widgets/area.js';
import { renderizarPizza } from './widgets/pizza.js';
import { renderizarDispersao } from './widgets/dispersao.js';
import { renderizarCombinado } from './widgets/combinado.js';
import { renderizarFunil } from './widgets/funil.js';
import { renderizarGauge } from './widgets/gauge.js';
import { renderizarHeatmap } from './widgets/heatmap.js';
import { renderizarKpi } from './widgets/kpi.js';
import { renderizarTabela } from './widgets/tabela.js';
import { renderizarPivot } from './widgets/pivot.js';

const REGISTRO = {
    barras:     renderizarBarras,
    linhas:     renderizarLinhas,
    area:       renderizarArea,
    pizza:      renderizarPizza,
    rosca:      renderizarPizza,
    dispersao:  renderizarDispersao,
    combinado:  renderizarCombinado,
    funil:      renderizarFunil,
    gauge:      renderizarGauge,
    heatmap:    renderizarHeatmap,
    kpi:        renderizarKpi,
    tabela:     renderizarTabela,
    pivot:      renderizarPivot
};

export function renderizarWidget(elemento, widget, dados) {
    if (!elemento) return;

    var tipo = widget.tipo || 'barras';
    var fn = REGISTRO[tipo];
    if (!fn) {
        elemento.innerHTML =
            '<div class="text-center text-danger p-3">' +
            '<i class="bi bi-exclamation-triangle"></i> Tipo de widget nao suportado: ' +
            escapeHtml(tipo) + '</div>';
        return;
    }

    fn(elemento, widget, dados);
}

export function tiposDisponiveis() {
    return [
        { tipo: 'barras',     rotulo: 'Barras',        icone: 'bi-bar-chart' },
        { tipo: 'linhas',     rotulo: 'Linhas',        icone: 'bi-graph-up' },
        { tipo: 'area',       rotulo: 'Area',          icone: 'bi-graph-up-arrow' },
        { tipo: 'pizza',      rotulo: 'Pizza',         icone: 'bi-pie-chart' },
        { tipo: 'rosca',      rotulo: 'Rosca',         icone: 'bi-bullseye' },
        { tipo: 'dispersao',  rotulo: 'Dispersao',     icone: 'bi-scatter' },
        { tipo: 'combinado',  rotulo: 'Combinado',     icone: 'bi-bar-chart-line' },
        { tipo: 'funil',      rotulo: 'Funil',         icone: 'bi-funnel' },
        { tipo: 'gauge',      rotulo: 'Gauge',         icone: 'bi-speedometer2' },
        { tipo: 'heatmap',    rotulo: 'Mapa de Calor', icone: 'bi-grid-3x3' },
        { tipo: 'kpi',        rotulo: 'KPI',           icone: 'bi-123' },
        { tipo: 'tabela',     rotulo: 'Tabela',        icone: 'bi-table' },
        { tipo: 'pivot',      rotulo: 'Tabela Dinamica', icone: 'bi-grid-3x3-gap' }
    ];
}

function escapeHtml(str) {
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(str));
    return div.innerHTML;
}
