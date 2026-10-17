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
            '<i class="bi bi-exclamation-triangle"></i> Tipo de widget não suportado: ' +
            escapeHtml(tipo) + '</div>';
        return;
    }

    fn(elemento, widget, dados);
}

// Tipos que exibem um valor por categoria (ou um valor unico): agrupar tambem por
// tempo repetiria cada categoria uma vez por periodo e o KPI mostraria so o
// primeiro periodo. Para eles o tempo atua apenas como filtro de periodo.
var TIPOS_SEM_EIXO_TEMPO = ['pizza', 'rosca', 'funil', 'gauge', 'kpi'];

/** Ajusta a consulta salva ao tipo do widget antes de envia-la ao backend. */
export function ajustarConsultaAoTipo(widget, consulta) {
    if (!consulta || !consulta.timeDimensions || consulta.timeDimensions.length === 0) return consulta;
    var tipo = widget.tipo || 'barras';
    var visual = widget.visual || {};
    var semEixo = TIPOS_SEM_EIXO_TEMPO.indexOf(tipo) >= 0 && !(tipo === 'kpi' && visual.comparacao);
    if (tipo === 'heatmap' && consulta.dimensions && consulta.dimensions.length >= 2) semEixo = true;
    if (!semEixo) return consulta;

    var nova = JSON.parse(JSON.stringify(consulta));
    nova.timeDimensions.forEach(function(td) { delete td.granularity; });
    return nova;
}

export function tiposDisponiveis() {
    return [
        { tipo: 'barras',     rotulo: 'Barras',        icone: 'bi-bar-chart' },
        { tipo: 'linhas',     rotulo: 'Linhas',        icone: 'bi-graph-up' },
        { tipo: 'area',       rotulo: 'Área',          icone: 'bi-graph-up-arrow' },
        { tipo: 'pizza',      rotulo: 'Pizza',         icone: 'bi-pie-chart' },
        { tipo: 'rosca',      rotulo: 'Rosca',         icone: 'bi-bullseye' },
        { tipo: 'dispersao',  rotulo: 'Dispersão',     icone: 'bi-dot' },
        { tipo: 'combinado',  rotulo: 'Combinado',     icone: 'bi-bar-chart-line' },
        { tipo: 'funil',      rotulo: 'Funil',         icone: 'bi-funnel' },
        { tipo: 'gauge',      rotulo: 'Gauge',         icone: 'bi-speedometer2' },
        { tipo: 'heatmap',    rotulo: 'Mapa de Calor', icone: 'bi-grid-3x3' },
        { tipo: 'kpi',        rotulo: 'KPI',           icone: 'bi-123' },
        { tipo: 'tabela',     rotulo: 'Tabela',        icone: 'bi-table' },
        { tipo: 'pivot',      rotulo: 'Tabela Dinâmica', icone: 'bi-grid-3x3-gap' }
    ];
}

function escapeHtml(str) {
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(str));
    return div.innerHTML;
}
