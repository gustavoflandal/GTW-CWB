<!DOCTYPE html>

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="pt-BR">
<head>
    <title>Admin Datasets</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body>

<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %>

<div class="container-fluid mt-3">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4><i class="bi bi-database-gear me-2"></i>Datasets para Dashboard Builder</h4>
        <button class="btn btn-primary" onclick="novoDataset()">
            <i class="bi bi-plus-circle me-1"></i> Novo Dataset
        </button>
    </div>

    <div class="table-responsive">
        <table class="table table-striped table-hover" id="tabelaDatasets">
            <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Nome</th>
                    <th>View SQL</th>
                    <th>Colunas</th>
                    <th>Status</th>
                    <th>Acoes</th>
                </tr>
            </thead>
            <tbody id="datasetBody">
                <tr><td colspan="6" class="text-center">Carregando...</td></tr>
            </tbody>
        </table>
    </div>
</div>

<!-- Offcanvas para criar/editar dataset -->
<div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasDataset" style="width: 500px;">
    <div class="offcanvas-header bg-primary text-white">
        <h5 class="offcanvas-title" id="offcanvasTitle">Novo Dataset</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas"></button>
    </div>
    <div class="offcanvas-body">
        <input type="hidden" id="dsId">
        <div class="mb-3">
            <label class="form-label fw-bold">Nome</label>
            <input type="text" class="form-control" id="dsNome" maxlength="80" required>
        </div>
        <div class="mb-3">
            <label class="form-label fw-bold">Descricao</label>
            <input type="text" class="form-control" id="dsDescricao" maxlength="200">
        </div>
        <div class="mb-3">
            <label class="form-label fw-bold">Nome da View SQL</label>
            <div class="input-group">
                <input type="text" class="form-control" id="dsNomeView" placeholder="muralha.v_nome_view" required>
                <button class="btn btn-outline-secondary" type="button" onclick="detectarColunas()">
                    <i class="bi bi-search me-1"></i> Detectar Colunas
                </button>
            </div>
            <small class="text-muted">Formato: schema.nome_view (ex: muralha.v_passagens_diarias)</small>
        </div>
        <div class="form-check mb-3">
            <input class="form-check-input" type="checkbox" id="dsAtivo" checked>
            <label class="form-check-label">Ativo</label>
        </div>

        <hr>
        <h6>Colunas</h6>
        <div id="colunasEditor">
            <p class="text-muted"><small>Clique em "Detectar Colunas" ou adicione manualmente.</small></p>
        </div>
        <button type="button" class="btn btn-sm btn-outline-secondary mt-2" onclick="adicionarColuna()">
            <i class="bi bi-plus"></i> Adicionar Coluna
        </button>

        <hr>
        <div class="d-grid">
            <button type="button" class="btn btn-primary" onclick="salvarDataset()">
                <i class="bi bi-check-circle me-1"></i> Salvar Dataset
            </button>
        </div>
    </div>
</div>

<script>
var baseUrl = '<%= request.getContextPath() %>';
var urlDataset = baseUrl + '/MuralhaDigital/Dataset';

document.addEventListener('DOMContentLoaded', carregarDatasets);

function carregarDatasets() {
    fetch(urlDataset + '?acao=listarTodos')
        .then(function(r) { return r.json(); })
        .then(function(data) {
            if (!data.ok) return;
            var body = document.getElementById('datasetBody');
            body.innerHTML = '';
            if (data.datasets.length === 0) {
                body.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Nenhum dataset cadastrado</td></tr>';
                return;
            }
            data.datasets.forEach(function(ds) {
                var colunas = [];
                try { colunas = JSON.parse(ds.colunasJson); } catch(e) {}
                var tr = document.createElement('tr');
                tr.innerHTML = '<td>' + ds.id + '</td>' +
                    '<td><strong>' + esc(ds.nome) + '</strong><br><small class="text-muted">' + esc(ds.descricao || '') + '</small></td>' +
                    '<td><code>' + esc(ds.nomeView) + '</code></td>' +
                    '<td>' + colunas.length + ' colunas</td>' +
                    '<td>' + (ds.ativo ? '<span class="badge bg-success">Ativo</span>' : '<span class="badge bg-secondary">Inativo</span>') + '</td>' +
                    '<td><button class="btn btn-sm btn-outline-primary" onclick="editarDataset(' + ds.id + ')"><i class="bi bi-pencil"></i></button></td>';
                body.appendChild(tr);
            });
        });
}

function novoDataset() {
    document.getElementById('dsId').value = '';
    document.getElementById('dsNome').value = '';
    document.getElementById('dsDescricao').value = '';
    document.getElementById('dsNomeView').value = '';
    document.getElementById('dsAtivo').checked = true;
    document.getElementById('colunasEditor').innerHTML = '<p class="text-muted"><small>Clique em "Detectar Colunas" ou adicione manualmente.</small></p>';
    document.getElementById('offcanvasTitle').textContent = 'Novo Dataset';
    new bootstrap.Offcanvas(document.getElementById('offcanvasDataset')).show();
}

function editarDataset(id) {
    fetch(urlDataset + '?acao=listarTodos')
        .then(function(r) { return r.json(); })
        .then(function(data) {
            if (!data.ok) return;
            var ds = data.datasets.find(function(d) { return d.id === id; });
            if (!ds) return;
            document.getElementById('dsId').value = ds.id;
            document.getElementById('dsNome').value = ds.nome;
            document.getElementById('dsDescricao').value = ds.descricao || '';
            document.getElementById('dsNomeView').value = ds.nomeView;
            document.getElementById('dsAtivo').checked = ds.ativo;
            document.getElementById('offcanvasTitle').textContent = 'Editar Dataset';

            var colunas = [];
            try { colunas = JSON.parse(ds.colunasJson); } catch(e) {}
            renderizarColunas(colunas);

            new bootstrap.Offcanvas(document.getElementById('offcanvasDataset')).show();
        });
}

function detectarColunas() {
    var nomeView = document.getElementById('dsNomeView').value.trim();
    if (!nomeView) { Swal.fire('Aviso', 'Informe o nome da view', 'warning'); return; }
    fetch(urlDataset + '?acao=detectarColunas&nomeView=' + encodeURIComponent(nomeView))
        .then(function(r) { return r.json(); })
        .then(function(data) {
            if (!data.ok) { Swal.fire('Erro', data.erro, 'error'); return; }
            renderizarColunas(data.colunas);
            Swal.fire({ icon: 'success', title: data.colunas.length + ' colunas detectadas', timer: 1500, showConfirmButton: false });
        });
}

function renderizarColunas(colunas) {
    var container = document.getElementById('colunasEditor');
    container.innerHTML = '';
    colunas.forEach(function(c) {
        adicionarColunaUI(c.nome, c.tipo, c.label);
    });
}

function adicionarColuna(nome, tipo, label) {
    adicionarColunaUI(nome || '', tipo || 'texto', label || '');
}

function adicionarColunaUI(nome, tipo, label) {
    var container = document.getElementById('colunasEditor');
    var row = document.createElement('div');
    row.className = 'row mb-1 coluna-row';
    row.innerHTML = '<div class="col-4"><input type="text" class="form-control form-control-sm col-nome" value="' + esc(nome) + '" placeholder="nome"></div>' +
        '<div class="col-3"><select class="form-select form-select-sm col-tipo">' +
        '<option value="texto"' + (tipo === 'texto' ? ' selected' : '') + '>Texto</option>' +
        '<option value="numerico"' + (tipo === 'numerico' ? ' selected' : '') + '>Numerico</option>' +
        '<option value="data"' + (tipo === 'data' ? ' selected' : '') + '>Data</option>' +
        '<option value="geo"' + (tipo === 'geo' ? ' selected' : '') + '>Geo</option>' +
        '</select></div>' +
        '<div class="col-4"><input type="text" class="form-control form-control-sm col-label" value="' + esc(label) + '" placeholder="label"></div>' +
        '<div class="col-1"><button type="button" class="btn btn-sm btn-outline-danger" onclick="this.closest(\'.coluna-row\').remove()"><i class="bi bi-x"></i></button></div>';
    container.appendChild(row);
}

function salvarDataset() {
    var colunas = [];
    document.querySelectorAll('.coluna-row').forEach(function(row) {
        var nome = row.querySelector('.col-nome').value.trim();
        if (!nome) return;
        colunas.push({
            nome: nome,
            tipo: row.querySelector('.col-tipo').value,
            label: row.querySelector('.col-label').value.trim() || nome
        });
    });

    var params = new URLSearchParams();
    params.append('acao', 'salvar');
    params.append('nome', document.getElementById('dsNome').value);
    params.append('descricao', document.getElementById('dsDescricao').value);
    params.append('nomeView', document.getElementById('dsNomeView').value);
    params.append('colunasJson', JSON.stringify(colunas));
    params.append('ativo', document.getElementById('dsAtivo').checked ? 'true' : 'false');
    var id = document.getElementById('dsId').value;
    if (id) params.append('id', id);

    fetch(urlDataset, { method: 'POST', body: params })
        .then(function(r) { return r.json(); })
        .then(function(data) {
            if (!data.ok) { Swal.fire('Erro', data.erro, 'error'); return; }
            bootstrap.Offcanvas.getInstance(document.getElementById('offcanvasDataset')).hide();
            Swal.fire({ icon: 'success', title: 'Dataset salvo!', timer: 1200, showConfirmButton: false });
            carregarDatasets();
        });
}

function esc(s) {
    if (!s) return '';
    var d = document.createElement('div');
    d.textContent = s;
    return d.innerHTML;
}
</script>

</body>
</html>
