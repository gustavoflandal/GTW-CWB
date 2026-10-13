var painelAtual = null;
var refreshTimer = null;
var datasetsCache = [];

document.addEventListener('DOMContentLoaded', function() {
    carregarPaineis();
    carregarDatasetsCache();
});

function carregarDatasetsCache() {
    fetch(urlDataset + '?acao=listar')
        .then(function(r) { return r.json(); })
        .then(function(data) {
            if (data.ok) datasetsCache = data.datasets;
        });
}

function carregarPaineis() {
    fetch(urlBuilder + '?acao=listarPaineis')
        .then(function(r) { return r.json(); })
        .then(function(data) {
            if (!data.ok) return;
            var container = document.getElementById('painelList');
            container.innerHTML = '';
            if (data.paineis.length === 0) {
                container.innerHTML = '<div class="text-center text-muted p-3"><small>Nenhum painel criado</small></div>';
                return;
            }
            data.paineis.forEach(function(p) {
                var div = document.createElement('div');
                div.className = 'db-painel-item' + (painelAtual && painelAtual.id === p.id ? ' active' : '');
                div.onclick = function() { selecionarPainel(p.id); };
                var badges = '';
                if (p.publico) badges += '<span class="badge bg-info">Publico</span> ';
                if (!p.proprio) badges += '<span class="badge bg-secondary">Compartilhado</span>';
                div.innerHTML = '<div class="painel-nome">' + escHtml(p.nome) + '</div>' +
                    '<div class="painel-desc">' + escHtml(p.descricao || '') + '</div>' +
                    '<div class="mt-1">' + badges + '</div>';
                container.appendChild(div);
            });
        });
}

function selecionarPainel(id) {
    fetch(urlBuilder + '?acao=obterPainel&id=' + id)
        .then(function(r) { return r.json(); })
        .then(function(data) {
            if (!data.ok) { Swal.fire('Erro', data.erro, 'error'); return; }
            painelAtual = data.painel;
            renderizarPainel();
            carregarPaineis();
        });
}

function renderizarPainel() {
    if (!painelAtual) return;
    document.getElementById('toolbar').style.display = 'flex';
    document.getElementById('painelNome').textContent = painelAtual.nome;
    document.getElementById('emptyState').style.display = 'none';
    var grid = document.getElementById('widgetGrid');
    grid.style.display = 'grid';
    grid.innerHTML = '';

    if (painelAtual.widgets && painelAtual.widgets.length > 0) {
        painelAtual.widgets.forEach(function(w) {
            var el = criarWidgetElement(w);
            grid.appendChild(el);
            carregarDadosWidget(w, el);
        });
    } else {
        grid.innerHTML = '<div class="db-empty-state" style="grid-column: span 12; grid-row: span 3;">' +
            '<i class="bi bi-plus-circle"></i><p>Clique em "+ Widget" para adicionar</p></div>';
    }

    iniciarRefresh();
    atualizarRefreshIndicator();
}

function criarWidgetElement(w) {
    var div = document.createElement('div');
    div.className = 'db-widget';
    div.setAttribute('data-widget-id', w.id);
    div.style.gridColumn = w.gridCol + ' / span ' + w.gridWidth;
    div.style.gridRow = w.gridRow + ' / span ' + w.gridHeight;

    var proprio = painelAtual.proprio;

    var header = document.createElement('div');
    header.className = 'db-widget-header';
    if (proprio) {
        header.draggable = true;
        header.addEventListener('dragstart', onDragStart);
        header.addEventListener('dragend', onDragEnd);
    }

    var title = document.createElement('span');
    title.className = 'widget-title';
    title.textContent = w.titulo;
    header.appendChild(title);

    if (proprio) {
        var actions = document.createElement('span');
        actions.className = 'widget-actions';
        actions.innerHTML = '<button class="btn btn-sm btn-outline-secondary" onclick="editarWidget(' + w.id + ')" title="Editar"><i class="bi bi-pencil"></i></button>' +
            '<button class="btn btn-sm btn-outline-danger" onclick="removerWidget(' + w.id + ')" title="Excluir"><i class="bi bi-x"></i></button>';
        header.appendChild(actions);
    }

    var body = document.createElement('div');
    body.className = 'db-widget-body';
    body.id = 'widget-body-' + w.id;

    div.appendChild(header);
    div.appendChild(body);

    if (proprio) {
        var handle = document.createElement('div');
        handle.className = 'resize-handle';
        handle.addEventListener('mousedown', function(e) { iniciarResize(e, div, w); });
        div.appendChild(handle);
    }

    return div;
}

function carregarDadosWidget(w, el) {
    var body = el.querySelector('.db-widget-body');
    body.innerHTML = '<div class="text-center text-muted p-2"><small>Carregando...</small></div>';

    fetch(urlBuilder + '?acao=executarWidget&id=' + w.id)
        .then(function(r) { return r.json(); })
        .then(function(data) {
            if (!data.ok) {
                body.innerHTML = '<div class="text-center text-danger p-2"><small>' + escHtml(data.erro) + '</small></div>';
                return;
            }
            renderWidget(w, data.dados, body);
        })
        .catch(function(err) {
            body.innerHTML = '<div class="text-center text-danger p-2"><small>Erro de rede</small></div>';
        });
}

function iniciarRefresh() {
    if (refreshTimer) clearInterval(refreshTimer);
    if (!painelAtual) return;
    var seg = painelAtual.refreshSegundos || 60;
    refreshTimer = setInterval(function() {
        refreshAllWidgets();
        atualizarRefreshIndicator();
    }, seg * 1000);
}

function atualizarRefreshIndicator() {
    var el = document.getElementById('refreshIndicator');
    if (!painelAtual) { el.textContent = ''; return; }
    el.textContent = 'Refresh: ' + painelAtual.refreshSegundos + 's | ' + new Date().toLocaleTimeString();
}

function refreshAllWidgets() {
    if (!painelAtual || !painelAtual.widgets) return;
    painelAtual.widgets.forEach(function(w) {
        var el = document.querySelector('[data-widget-id="' + w.id + '"]');
        if (el) carregarDadosWidget(w, el);
    });
}

// --- Drag and Drop ---
var dragWidgetId = null;

function onDragStart(e) {
    var widgetEl = e.target.closest('.db-widget');
    dragWidgetId = widgetEl.getAttribute('data-widget-id');
    widgetEl.classList.add('dragging');
    e.dataTransfer.effectAllowed = 'move';
}

function onDragEnd(e) {
    var widgetEl = e.target.closest('.db-widget');
    if (widgetEl) widgetEl.classList.remove('dragging');
    dragWidgetId = null;
    document.getElementById('widgetGrid').classList.remove('drop-zone-highlight');
}

document.addEventListener('DOMContentLoaded', function() {
    var grid = document.getElementById('widgetGrid');
    grid.addEventListener('dragover', function(e) {
        e.preventDefault();
        e.dataTransfer.dropEffect = 'move';
        grid.classList.add('drop-zone-highlight');
    });
    grid.addEventListener('dragleave', function() {
        grid.classList.remove('drop-zone-highlight');
    });
    grid.addEventListener('drop', function(e) {
        e.preventDefault();
        grid.classList.remove('drop-zone-highlight');
        if (!dragWidgetId) return;
        var rect = grid.getBoundingClientRect();
        var colWidth = rect.width / 12;
        var rowHeight = 80 + 12;
        var x = e.clientX - rect.left;
        var y = e.clientY - rect.top + grid.scrollTop;
        var col = Math.max(1, Math.min(12, Math.floor(x / colWidth) + 1));
        var row = Math.max(1, Math.floor(y / rowHeight) + 1);

        var widgetEl = document.querySelector('[data-widget-id="' + dragWidgetId + '"]');
        var w = encontrarWidget(parseInt(dragWidgetId));
        if (!w || !widgetEl) return;
        w.gridCol = col;
        w.gridRow = row;
        widgetEl.style.gridColumn = col + ' / span ' + w.gridWidth;
        widgetEl.style.gridRow = row + ' / span ' + w.gridHeight;
        salvarLayout();
    });
});

// --- Resize ---
function iniciarResize(e, widgetEl, w) {
    e.preventDefault();
    var grid = document.getElementById('widgetGrid');
    var rect = grid.getBoundingClientRect();
    var colWidth = rect.width / 12;
    var rowHeight = 80 + 12;
    var startX = e.clientX;
    var startY = e.clientY;
    var startW = w.gridWidth;
    var startH = w.gridHeight;

    function onMove(e2) {
        var dx = e2.clientX - startX;
        var dy = e2.clientY - startY;
        var newW = Math.max(2, Math.min(12, startW + Math.round(dx / colWidth)));
        var newH = Math.max(1, startH + Math.round(dy / rowHeight));
        w.gridWidth = newW;
        w.gridHeight = newH;
        widgetEl.style.gridColumn = w.gridCol + ' / span ' + newW;
        widgetEl.style.gridRow = w.gridRow + ' / span ' + newH;
    }

    function onUp() {
        document.removeEventListener('mousemove', onMove);
        document.removeEventListener('mouseup', onUp);
        salvarLayout();
        var body = widgetEl.querySelector('.db-widget-body');
        if (body && w.tipo === 'CHART') {
            carregarDadosWidget(w, widgetEl);
        }
        if (body && w.tipo === 'MAP') {
            carregarDadosWidget(w, widgetEl);
        }
    }

    document.addEventListener('mousemove', onMove);
    document.addEventListener('mouseup', onUp);
}

function salvarLayout() {
    if (!painelAtual || !painelAtual.widgets) return;
    var items = painelAtual.widgets.map(function(w) {
        return { id: w.id, gridCol: w.gridCol, gridRow: w.gridRow, gridWidth: w.gridWidth, gridHeight: w.gridHeight };
    });
    fetch(urlBuilder + '?acao=atualizarLayout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(items)
    });
}

function encontrarWidget(id) {
    if (!painelAtual || !painelAtual.widgets) return null;
    for (var i = 0; i < painelAtual.widgets.length; i++) {
        if (painelAtual.widgets[i].id === id) return painelAtual.widgets[i];
    }
    return null;
}

// --- CRUD Painel ---
function novoPainel() {
    document.getElementById('painelId').value = '';
    document.getElementById('painelNomeInput').value = '';
    document.getElementById('painelDescInput').value = '';
    document.getElementById('painelRefreshInput').value = '60';
    document.getElementById('painelPublicoInput').checked = false;
    document.getElementById('modalPainelTitle').textContent = 'Novo Painel';
    new bootstrap.Modal(document.getElementById('modalPainel')).show();
}

function editarPainel() {
    if (!painelAtual || !painelAtual.proprio) return;
    document.getElementById('painelId').value = painelAtual.id;
    document.getElementById('painelNomeInput').value = painelAtual.nome;
    document.getElementById('painelDescInput').value = painelAtual.descricao || '';
    document.getElementById('painelRefreshInput').value = painelAtual.refreshSegundos;
    document.getElementById('painelPublicoInput').checked = painelAtual.publico;
    document.getElementById('modalPainelTitle').textContent = 'Editar Painel';
    new bootstrap.Modal(document.getElementById('modalPainel')).show();
}

function salvarPainel() {
    var id = document.getElementById('painelId').value;
    var params = new URLSearchParams();
    params.append('acao', 'salvarPainel');
    params.append('nome', document.getElementById('painelNomeInput').value);
    params.append('descricao', document.getElementById('painelDescInput').value);
    params.append('refreshSegundos', document.getElementById('painelRefreshInput').value);
    params.append('publico', document.getElementById('painelPublicoInput').checked ? 'true' : 'false');
    if (id) params.append('id', id);

    fetch(urlBuilder, { method: 'POST', body: params })
        .then(function(r) { return r.json(); })
        .then(function(data) {
            if (!data.ok) { Swal.fire('Erro', data.erro, 'error'); return; }
            bootstrap.Modal.getInstance(document.getElementById('modalPainel')).hide();
            Swal.fire({ icon: 'success', title: 'Salvo!', timer: 1200, showConfirmButton: false });
            carregarPaineis();
            selecionarPainel(data.id || id);
        });
}

function excluirPainel() {
    if (!painelAtual || !painelAtual.proprio) return;
    Swal.fire({
        title: 'Excluir painel?',
        text: 'Todos os widgets serao removidos.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        confirmButtonText: 'Sim, excluir'
    }).then(function(r) {
        if (!r.isConfirmed) return;
        var params = new URLSearchParams();
        params.append('acao', 'excluirPainel');
        params.append('id', painelAtual.id);
        fetch(urlBuilder, { method: 'POST', body: params })
            .then(function(r) { return r.json(); })
            .then(function(data) {
                if (!data.ok) { Swal.fire('Erro', data.erro, 'error'); return; }
                painelAtual = null;
                if (refreshTimer) clearInterval(refreshTimer);
                document.getElementById('toolbar').style.display = 'none';
                document.getElementById('widgetGrid').style.display = 'none';
                document.getElementById('emptyState').style.display = 'flex';
                carregarPaineis();
            });
    });
}

// --- CRUD Widget ---
function removerWidget(id) {
    Swal.fire({
        title: 'Excluir widget?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        confirmButtonText: 'Excluir'
    }).then(function(r) {
        if (!r.isConfirmed) return;
        var params = new URLSearchParams();
        params.append('acao', 'excluirWidget');
        params.append('id', id);
        fetch(urlBuilder, { method: 'POST', body: params })
            .then(function(r2) { return r2.json(); })
            .then(function(data) {
                if (data.ok) selecionarPainel(painelAtual.id);
                else Swal.fire('Erro', data.erro, 'error');
            });
    });
}

function editarWidget(id) {
    var w = encontrarWidget(id);
    if (!w) return;
    abrirModalWidget(w);
}

function escHtml(s) {
    if (!s) return '';
    var d = document.createElement('div');
    d.textContent = s;
    return d.innerHTML;
}
