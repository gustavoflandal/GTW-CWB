export function renderizarTabela(elemento, widget, dados) {
    if (!elemento) return;
    if (!dados || dados.length === 0) {
        elemento.innerHTML = '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:var(--bs-secondary-color)"><div><i class="bi bi-inbox" style="font-size:2rem"></i><div>Sem dados</div></div></div>';
        return;
    }

    var visual = widget.visual || {};
    var itensPorPagina = visual.itensPorPagina || 15;
    var paginaAtual = 0;
    var keys = Object.keys(dados[0]);
    var totalPaginas = Math.ceil(dados.length / itensPorPagina);

    function renderizar() {
        var inicio = paginaAtual * itensPorPagina;
        var fatia = dados.slice(inicio, inicio + itensPorPagina);

        var html = '<div style="overflow-x:auto">';
        html += '<table class="table table-sm table-striped table-hover mb-2" style="font-size:0.85rem">';
        html += '<thead class="table-light"><tr>';
        for (var i = 0; i < keys.length; i++) {
            html += '<th style="white-space:nowrap">' + escapeHtml(keys[i]) + '</th>';
        }
        html += '</tr></thead><tbody>';

        for (var r = 0; r < fatia.length; r++) {
            html += '<tr>';
            for (var c = 0; c < keys.length; c++) {
                var val = fatia[r][keys[c]];
                var texto = val != null ? String(val) : '';
                html += '<td>' + escapeHtml(texto) + '</td>';
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
    div.appendChild(document.createTextNode(str));
    return div.innerHTML;
}
