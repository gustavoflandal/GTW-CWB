var contadorGrupo = 0;
var contadorCondicao = 0;

export function criarEditorFiltros(container, camposDisponiveis) {
    container.innerHTML = '';
    var estado = { operador: 'AND', condicoes: [] };
    renderizar(container, estado, camposDisponiveis);
    return {
        obterFiltros: function() { return coletarFiltros(container); },
        definirFiltros: function(filtros) {
            estado = filtros && filtros.condicoes ? filtros : { operador: 'AND', condicoes: [] };
            renderizar(container, estado, camposDisponiveis);
        }
    };
}

function renderizar(container, estado, campos) {
    var grupoId = 'fg-' + (++contadorGrupo);
    var html = '<div class="filtro-grupo border rounded p-2 mb-2" data-gid="' + grupoId + '">';
    html += '<div class="d-flex align-items-center gap-2 mb-2">';
    html += '<select class="form-select form-select-sm" style="width:80px" data-role="operador">';
    html += '<option value="AND"' + (estado.operador === 'AND' ? ' selected' : '') + '>E</option>';
    html += '<option value="OR"' + (estado.operador === 'OR' ? ' selected' : '') + '>OU</option>';
    html += '</select>';
    html += '<button class="btn btn-outline-primary btn-sm" data-role="addCondicao"><i class="bi bi-plus-lg"></i> Condicao</button>';
    html += '</div>';

    html += '<div data-role="listaCondicoes">';
    if (estado.condicoes) {
        for (var i = 0; i < estado.condicoes.length; i++) {
            html += renderizarCondicao(estado.condicoes[i], campos);
        }
    }
    html += '</div>';
    html += '</div>';

    container.innerHTML = html;

    container.addEventListener('click', function(e) {
        var btn = e.target.closest('[data-role="addCondicao"]');
        if (btn) {
            e.preventDefault();
            var lista = container.querySelector('[data-role="listaCondicoes"]');
            lista.insertAdjacentHTML('beforeend', renderizarCondicao({}, campos));
        }

        var rem = e.target.closest('[data-role="remCondicao"]');
        if (rem) {
            e.preventDefault();
            var row = rem.closest('.filtro-condicao');
            if (row) row.remove();
        }
    });
}

function renderizarCondicao(cond, campos) {
    var cid = 'fc-' + (++contadorCondicao);
    var html = '<div class="filtro-condicao d-flex align-items-center gap-1 mb-1" data-cid="' + cid + '">';

    html += '<select class="form-select form-select-sm" style="width:30%" data-role="campo">';
    html += '<option value="">Campo...</option>';
    for (var i = 0; i < campos.length; i++) {
        var sel = (cond.campo === campos[i].alias) ? ' selected' : '';
        html += '<option value="' + escapeAttr(campos[i].alias) + '"' + sel + '>' + escapeHtml(campos[i].rotulo) + '</option>';
    }
    html += '</select>';

    var ops = ['=', '!=', '>', '<', '>=', '<=', 'IN', 'NOT IN', 'BETWEEN', 'LIKE', 'IS NULL', 'IS NOT NULL'];
    html += '<select class="form-select form-select-sm" style="width:20%" data-role="operadorCond">';
    for (var j = 0; j < ops.length; j++) {
        var selOp = (cond.operador === ops[j]) ? ' selected' : '';
        html += '<option value="' + ops[j] + '"' + selOp + '>' + ops[j] + '</option>';
    }
    html += '</select>';

    var valorStr = cond.valor != null ? String(cond.valor) : '';
    html += '<input type="text" class="form-control form-control-sm" style="width:35%" data-role="valor" placeholder="Valor" value="' + escapeAttr(valorStr) + '">';
    html += '<button class="btn btn-outline-danger btn-sm" data-role="remCondicao"><i class="bi bi-x"></i></button>';
    html += '</div>';
    return html;
}

function coletarFiltros(container) {
    var grupo = container.querySelector('.filtro-grupo');
    if (!grupo) return { operador: 'AND', condicoes: [] };

    var opSelect = grupo.querySelector('[data-role="operador"]');
    var operador = opSelect ? opSelect.value : 'AND';
    var condicoes = [];

    grupo.querySelectorAll('.filtro-condicao').forEach(function(row) {
        var campo = row.querySelector('[data-role="campo"]').value;
        var op = row.querySelector('[data-role="operadorCond"]').value;
        var valor = row.querySelector('[data-role="valor"]').value;
        if (campo) {
            condicoes.push({ campo: campo, operador: op, valor: valor });
        }
    });

    return { operador: operador, condicoes: condicoes };
}

function escapeHtml(str) {
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(String(str || '')));
    return div.innerHTML;
}

function escapeAttr(str) {
    return String(str || '').replace(/&/g, '&amp;').replace(/"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
