import { api } from '/static/js/dashboards/api.js';

let containerEl = null;
let debounceTimer = null;
let ultimaConsulta = '';

export function inicializar(container) {
    containerEl = container;
    renderizarVazio();
}

function renderizarVazio() {
    containerEl.innerHTML = '<div class="text-center text-muted py-3"><i class="bi bi-table me-1"></i>Selecione campos para ver a previa</div>';
}

export function atualizar(datasetId, campos) {
    if (debounceTimer) clearTimeout(debounceTimer);
    debounceTimer = setTimeout(function () { executar(datasetId, campos); }, 600);
}

async function executar(datasetId, campos) {
    if (!campos || campos.length === 0) { renderizarVazio(); return; }
    if (!datasetId) { renderizarVazio(); return; }

    var chave = JSON.stringify(campos);
    if (chave === ultimaConsulta) return;
    ultimaConsulta = chave;

    containerEl.innerHTML = '<div class="text-center py-3"><div class="spinner-border spinner-border-sm" role="status"></div> Carregando previa...</div>';

    var dimensoes = campos.filter(function (c) { return c.papel !== 'medida'; });
    var medidas = campos.filter(function (c) { return c.papel === 'medida'; });

    if (dimensoes.length === 0 && medidas.length === 0) { renderizarVazio(); return; }

    var consulta = {
        dimensions: dimensoes.slice(0, 5).map(function (c) { return c.nomeFisico; }),
        measures: medidas.slice(0, 5).map(function (c) { return c.nomeFisico; }),
        limit: 50
    };
    var colunas = consulta.dimensions.concat(consulta.measures);

    try {
        var r = await api.post('/api/dash/consultas', { datasetId: datasetId, query: consulta });
        if (!r.ok) {
            containerEl.innerHTML = '<div class="text-center text-danger py-3"><i class="bi bi-exclamation-triangle me-1"></i>'
                + escHtml(r.erro || 'Erro na consulta') + '<br><small class="text-muted">A previa usa o dataset salvo.</small></div>';
            return;
        }
        renderizarTabela(colunas, r.data || [], r.tempoMs);
    } catch (e) {
        containerEl.innerHTML = '<div class="text-center text-danger py-3">Erro ao carregar previa</div>';
    }
}

function renderizarTabela(colunas, linhas, tempoMs) {
    if (linhas.length === 0) {
        containerEl.innerHTML = '<div class="text-center text-muted py-3">Sem resultados</div>';
        return;
    }

    var html = '<div class="d-flex justify-content-between align-items-center px-2 py-1 bg-light border-bottom">';
    html += '<small class="text-muted">' + linhas.length + ' linhas';
    if (tempoMs) html += ' em ' + tempoMs + ' ms';
    html += '</small></div>';

    html += '<div style="overflow:auto; max-height:200px;"><table class="table table-sm table-striped table-bordered mb-0" style="font-size:0.8rem;">';
    html += '<thead class="table-light sticky-top"><tr>';
    colunas.forEach(function (c) {
        html += '<th class="px-1 py-1">' + escHtml(c) + '</th>';
    });
    html += '</tr></thead><tbody>';

    linhas.forEach(function (row) {
        html += '<tr>';
        colunas.forEach(function (c) {
            html += '<td class="px-1 py-0">' + formatarCelula(row[c]) + '</td>';
        });
        html += '</tr>';
    });
    html += '</tbody></table></div>';

    containerEl.innerHTML = html;
}

function formatarCelula(v) {
    if (v === null || v === undefined) return '<span class="text-muted fst-italic">null</span>';
    return escHtml(String(v));
}

function escHtml(s) {
    var d = document.createElement('div');
    d.appendChild(document.createTextNode(s));
    return d.innerHTML;
}
