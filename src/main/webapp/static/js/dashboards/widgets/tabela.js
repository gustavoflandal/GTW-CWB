import { rotuloCampo, infoTempo, colunasVisiveis, formatarCelula } from './comum.js';

export function renderizarTabela(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        elemento.innerHTML = '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:var(--bs-secondary-color)"><div><i class="bi bi-inbox" style="font-size:2rem"></i><div>Sem dados</div></div></div>';
        return;
    }

    var visual = widget.visual || {};
    var itensPorPagina = visual.itensPorPagina || 15;
    var paginaAtual = 0;
    var ordem = { chave: null, desc: false };
    var keys = colunasVisiveis(widget, dados);
    var tempo = infoTempo(widget, dados);
    var totalPaginas = Math.ceil(dados.length / itensPorPagina);

    function ordenados() {
        if (!ordem.chave) return dados;
        var k = ordem.chave;
        var fator = ordem.desc ? -1 : 1;
        return dados.slice().sort(function(a, b) {
            var va = a[k], vb = b[k];
            if (va == null && vb == null) return 0;
            if (va == null) return 1;
            if (vb == null) return -1;
            if (typeof va === 'number' && typeof vb === 'number') return (va - vb) * fator;
            return String(va).localeCompare(String(vb), 'pt-BR', { numeric: true }) * fator;
        });
    }

    function renderizar() {
        var inicio = paginaAtual * itensPorPagina;
        var fatia = ordenados().slice(inicio, inicio + itensPorPagina);

        var html = '<div style="overflow-x:auto">';
        html += '<table class="table table-sm table-striped table-hover mb-2" style="font-size:0.85rem">';
        html += '<thead class="table-light"><tr>';
        for (var i = 0; i < keys.length; i++) {
            var seta = ordem.chave === keys[i] ? (ordem.desc ? ' <i class="bi bi-caret-down-fill"></i>' : ' <i class="bi bi-caret-up-fill"></i>') : '';
            var aria = ordem.chave === keys[i] ? (ordem.desc ? 'descending' : 'ascending') : 'none';
            html += '<th style="white-space:nowrap;cursor:pointer" data-col="' + i + '" aria-sort="' + aria + '" title="Clique para ordenar">' +
                escapeHtml(rotuloCampo(widget, keys[i])) + seta + '</th>';
        }
        html += '</tr></thead><tbody>';

        for (var r = 0; r < fatia.length; r++) {
            html += '<tr>';
            for (var c = 0; c < keys.length; c++) {
                var val = fatia[r][keys[c]];
                var alinhamento = typeof val === 'number' ? ' style="text-align:right"' : '';
                html += '<td' + alinhamento + '>' + escapeHtml(formatarCelula(widget, keys[c], val, tempo)) + '</td>';
            }
            html += '</tr>';
        }
        html += '</tbody></table></div>';

        if (totalPaginas > 1) {
            html += '<nav><ul class="pagination pagination-sm justify-content-center mb-0">';
            html += '<li class="page-item ' + (paginaAtual === 0 ? 'disabled' : '') + '">';
            html += '<a class="page-link" href="#" data-pg="' + (paginaAtual - 1) + '">&laquo;</a></li>';

            var inicioP = Math.max(0, paginaAtual - 2);
            var fimP = Math.min(totalPaginas, inicioP + 5);
            for (var p = inicioP; p < fimP; p++) {
                html += '<li class="page-item ' + (p === paginaAtual ? 'active' : '') + '">';
                html += '<a class="page-link" href="#" data-pg="' + p + '">' + (p + 1) + '</a></li>';
            }

            html += '<li class="page-item ' + (paginaAtual >= totalPaginas - 1 ? 'disabled' : '') + '">';
            html += '<a class="page-link" href="#" data-pg="' + (paginaAtual + 1) + '">&raquo;</a></li>';
            html += '</ul></nav>';

            html += '<div class="text-center text-muted" style="font-size:0.75rem">';
            html += (inicio + 1) + '-' + Math.min(inicio + itensPorPagina, dados.length) + ' de ' + dados.length;
            html += '</div>';
        }

        elemento.innerHTML = html;

        elemento.querySelectorAll('th[data-col]').forEach(function(th) {
            th.addEventListener('click', function() {
                var k = keys[parseInt(th.dataset.col)];
                ordem = { chave: k, desc: ordem.chave === k ? !ordem.desc : false };
                paginaAtual = 0;
                renderizar();
            });
        });

        elemento.querySelectorAll('[data-pg]').forEach(function(el) {
            el.addEventListener('click', function(e) {
                e.preventDefault();
                var pg = parseInt(e.currentTarget.dataset.pg);
                if (pg >= 0 && pg < totalPaginas) {
                    paginaAtual = pg;
                    renderizar();
                }
            });
        });
    }

    renderizar();
}

function escapeHtml(str) {
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(str == null ? '' : String(str)));
    return div.innerHTML;
}
