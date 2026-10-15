import { api } from '../api.js';

export function renderizarPivot(elemento, widget, dados) {
    if (!elemento) return;

    if (!dados || dados.length === 0) {
        elemento.innerHTML = '<div class="text-center text-muted p-3"><i class="bi bi-inbox" style="font-size:1.5rem"></i><div>Sem dados</div></div>';
        return;
    }

    var visual = widget.visual || {};
    var configPivot = visual.pivot || {};
    var linhas = configPivot.linhas || [];
    var colunas = configPivot.colunas || [];
    var valores = configPivot.valores || [];
    var subtotais = configPivot.subtotais !== false;

    if (linhas.length === 0 && colunas.length === 0) {
        renderizarTabelaSimples(elemento, dados);
        return;
    }

    elemento.innerHTML = '<div class="text-center p-2"><div class="spinner-border spinner-border-sm text-primary"></div> Pivotando...</div>';

    api.post('/api/dash/exportar', {
        acao: 'pivotar',
        dados: dados,
        config: { linhas: linhas, colunas: colunas, valores: valores, subtotais: subtotais }
    }).then(function(resp) {
        if (!resp.ok) {
            elemento.innerHTML = '<div class="text-danger text-center p-3"><i class="bi bi-exclamation-triangle"></i> ' + escapeHtml(resp.erro || 'Erro no pivot') + '</div>';
            return;
        }
        renderizarAgGrid(elemento, resp.cabecalhos, resp.dados, visual);
    }).catch(function(err) {
        elemento.innerHTML = '<div class="text-danger text-center p-3">' + escapeHtml(err.message) + '</div>';
    });
}

function renderizarAgGrid(elemento, cabecalhos, dados, visual) {
    elemento.innerHTML = '';

    if (typeof agGrid === 'undefined') {
        renderizarTabelaHtml(elemento, cabecalhos, dados);
        return;
    }

    var columnDefs = [];
    for (var i = 0; i < cabecalhos.size(); i++) {
        var h = cabecalhos[i];
        var isNum = dados.length > 0 && typeof dados[0][h] === 'number';
        var def = {
            field: h,
            headerName: h,
            sortable: true,
            resizable: true
        };
        if (isNum) {
            def.type = 'numericColumn';
            def.valueFormatter = function(params) {
                if (params.value == null) return '';
                return Number(params.value).toLocaleString('pt-BR', { maximumFractionDigits: 2 });
            };
        }
        columnDefs.push(def);
    }

    var rowData = [];
    for (var j = 0; j < dados.length; j++) {
        var row = {};
        for (var k = 0; k < cabecalhos.length; k++) {
            var key = cabecalhos[k];
            row[key] = dados[j][key] != null ? dados[j][key] : null;
        }
        if (dados[j]._subtotal) row._subtotal = true;
        rowData.push(row);
    }

    agGrid.createGrid(elemento, {
        columnDefs: columnDefs,
        rowData: rowData,
        pagination: rowData.length > 100,
        paginationPageSize: 100,
        domLayout: 'normal',
        defaultColDef: {
            sortable: true,
            resizable: true,
            minWidth: 80
        }
    });
}

function renderizarTabelaSimples(elemento, dados) {
    if (!dados || dados.length === 0) return;
    var keys = Object.keys(dados[0]);
    renderizarTabelaHtml(elemento, keys, dados);
}

function renderizarTabelaHtml(elemento, cabecalhos, dados) {
    var html = '<div style="overflow:auto;height:100%">';
    html += '<table class="table table-sm table-striped table-bordered mb-0" style="font-size:0.8rem">';
    html += '<thead style="position:sticky;top:0;z-index:1"><tr>';
    for (var i = 0; i < cabecalhos.length; i++) {
        html += '<th style="white-space:nowrap;background:var(--bs-table-bg,#fff)">' + escapeHtml(cabecalhos[i]) + '</th>';
    }
    html += '</tr></thead><tbody>';

    var max = Math.min(dados.length, 500);
    for (var r = 0; r < max; r++) {
        var row = dados[r];
        var isSubtotal = row._subtotal || row['_subtotal'];
        html += '<tr' + (isSubtotal ? ' style="font-weight:bold;background:var(--bs-tertiary-bg,#f0f0f0)"' : '') + '>';
        for (var c = 0; c < cabecalhos.length; c++) {
            var val = row[cabecalhos[c]];
            var align = typeof val === 'number' ? ' style="text-align:right"' : '';
            if (typeof val === 'number') {
                val = val.toLocaleString('pt-BR', { maximumFractionDigits: 2 });
            }
            html += '<td' + align + '>' + (val != null ? escapeHtml(String(val)) : '') + '</td>';
        }
        html += '</tr>';
    }

    html += '</tbody></table></div>';
    if (dados.length > max) {
        html += '<div class="text-muted text-center" style="font-size:0.75rem">' + max + ' de ' + dados.length + ' linhas</div>';
    }
    elemento.innerHTML = html;
}

function escapeHtml(s) {
    var d = document.createElement('div');
    d.appendChild(document.createTextNode(String(s || '')));
    return d.innerHTML;
}
