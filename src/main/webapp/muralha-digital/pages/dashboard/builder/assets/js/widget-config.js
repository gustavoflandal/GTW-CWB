var colunasDoDataset = [];

function abrirModalWidget(widget) {
    document.getElementById('widgetId').value = widget ? widget.id : '';
    document.getElementById('wTitulo').value = widget ? widget.titulo : '';
    document.getElementById('wTipo').value = widget ? widget.tipo : 'CARD';

    var selectDs = document.getElementById('wDataset');
    selectDs.innerHTML = '<option value="">Selecione...</option>';
    datasetsCache.forEach(function(ds) {
        var opt = document.createElement('option');
        opt.value = ds.id;
        opt.textContent = ds.nome;
        if (widget && widget.idDataset === ds.id) opt.selected = true;
        selectDs.appendChild(opt);
    });

    limparFiltros();
    tipoWidgetChanged();

    if (widget && widget.idDataset) {
        datasetChanged(function() {
            preencherCamposWidget(widget);
        });
    }

    new bootstrap.Modal(document.getElementById('modalWidget')).show();
}

function tipoWidgetChanged() {
    var tipo = document.getElementById('wTipo').value;
    document.getElementById('camposCard').style.display  = tipo === 'CARD'  ? '' : 'none';
    document.getElementById('camposChart').style.display = tipo === 'CHART' ? '' : 'none';
    document.getElementById('camposTable').style.display = tipo === 'TABLE' ? '' : 'none';
    document.getElementById('camposMap').style.display   = tipo === 'MAP'   ? '' : 'none';
}

function datasetChanged(callback) {
    var dsId = document.getElementById('wDataset').value;
    if (!dsId) { colunasDoDataset = []; return; }

    fetch(urlDataset + '?acao=obterColunas&id=' + dsId)
        .then(function(r) { return r.json(); })
        .then(function(data) {
            if (!data.ok) return;
            colunasDoDataset = JSON.parse(data.colunas);
            preencherSelectsColunas();
            if (callback) callback();
        });
}

function preencherSelectsColunas() {
    var selects = ['wColunaValor', 'wColunaLabel', 'wColunaValorChart', 'wColunaLat', 'wColunaLng', 'wColunaLabelMap'];
    selects.forEach(function(id) {
        var sel = document.getElementById(id);
        sel.innerHTML = '<option value="">Selecione...</option>';
        colunasDoDataset.forEach(function(c) {
            var opt = document.createElement('option');
            opt.value = c.nome;
            opt.textContent = c.label || c.nome;
            sel.appendChild(opt);
        });
    });

    var checkContainer = document.getElementById('wColunasTabela');
    checkContainer.innerHTML = '';
    colunasDoDataset.forEach(function(c) {
        var div = document.createElement('div');
        div.className = 'form-check';
        div.innerHTML = '<input class="form-check-input" type="checkbox" value="' + c.nome + '" id="colTab_' + c.nome + '" checked>' +
            '<label class="form-check-label" for="colTab_' + c.nome + '">' + (c.label || c.nome) + '</label>';
        checkContainer.appendChild(div);
    });

    var wAgregacaoChart = document.getElementById('wAgregacaoChart');
    if (wAgregacaoChart.value === '') wAgregacaoChart.value = 'SUM';
}

function preencherCamposWidget(w) {
    if (w.tipo === 'CARD') {
        document.getElementById('wColunaValor').value = w.colunaValor || '';
        document.getElementById('wAgregacao').value = w.agregacao || 'SUM';
        document.getElementById('wThOk').value = w.thresholdOk != null ? w.thresholdOk : '';
        document.getElementById('wThWarn').value = w.thresholdWarn != null ? w.thresholdWarn : '';
    } else if (w.tipo === 'CHART') {
        document.getElementById('wColunaLabel').value = w.colunaLabel || '';
        document.getElementById('wColunaValorChart').value = w.colunaValor || '';
        document.getElementById('wAgregacaoChart').value = w.agregacao || 'SUM';
        document.getElementById('wTipoGrafico').value = w.tipoGrafico || 'bar';
    } else if (w.tipo === 'TABLE') {
        if (w.colunasTabela) {
            var cols = w.colunasTabela.split(',');
            var checks = document.querySelectorAll('#wColunasTabela input[type="checkbox"]');
            checks.forEach(function(chk) {
                chk.checked = cols.indexOf(chk.value) >= 0;
            });
        }
    } else if (w.tipo === 'MAP') {
        document.getElementById('wColunaLat').value = w.colunaLat || '';
        document.getElementById('wColunaLng').value = w.colunaLng || '';
        document.getElementById('wColunaLabelMap').value = w.colunaLabel || '';
    }

    if (w.filtrosJson) {
        try {
            var filtros = JSON.parse(w.filtrosJson);
            filtros.forEach(function(f) { adicionarFiltro(f); });
        } catch (e) {}
    }
}

function salvarWidget() {
    var tipo = document.getElementById('wTipo').value;
    var body = {
        idPainel: painelAtual.id,
        tipo: tipo,
        titulo: document.getElementById('wTitulo').value,
        idDataset: parseInt(document.getElementById('wDataset').value)
    };

    var idStr = document.getElementById('widgetId').value;
    if (idStr) body.id = parseInt(idStr);

    if (tipo === 'CARD') {
        body.colunaValor = document.getElementById('wColunaValor').value;
        body.agregacao = document.getElementById('wAgregacao').value;
        var thOk = document.getElementById('wThOk').value;
        var thWarn = document.getElementById('wThWarn').value;
        if (thOk !== '') body.thresholdOk = parseFloat(thOk);
        if (thWarn !== '') body.thresholdWarn = parseFloat(thWarn);
    } else if (tipo === 'CHART') {
        body.colunaLabel = document.getElementById('wColunaLabel').value;
        body.colunaValor = document.getElementById('wColunaValorChart').value;
        body.agregacao = document.getElementById('wAgregacaoChart').value;
        body.tipoGrafico = document.getElementById('wTipoGrafico').value;
    } else if (tipo === 'TABLE') {
        var cols = [];
        document.querySelectorAll('#wColunasTabela input[type="checkbox"]:checked').forEach(function(chk) {
            cols.push(chk.value);
        });
        body.colunasTabela = cols.join(',');
        body.agregacao = 'NONE';
    } else if (tipo === 'MAP') {
        body.colunaLat = document.getElementById('wColunaLat').value;
        body.colunaLng = document.getElementById('wColunaLng').value;
        body.colunaLabel = document.getElementById('wColunaLabelMap').value;
        body.agregacao = 'NONE';
    }

    body.filtrosJson = coletarFiltros();

    fetch(urlBuilder + '?acao=salvarWidget', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    })
    .then(function(r) { return r.json(); })
    .then(function(data) {
        if (!data.ok) { Swal.fire('Erro', data.erro, 'error'); return; }
        bootstrap.Modal.getInstance(document.getElementById('modalWidget')).hide();
        selecionarPainel(painelAtual.id);
    });
}

// --- Filtros ---
function adicionarFiltro(filtro) {
    var container = document.getElementById('filtrosContainer');
    var row = document.createElement('div');
    row.className = 'row mb-2 filtro-row';

    var colOpts = '<option value="">Coluna</option>';
    colunasDoDataset.forEach(function(c) {
        var sel = filtro && filtro.coluna === c.nome ? ' selected' : '';
        colOpts += '<option value="' + c.nome + '"' + sel + '>' + (c.label || c.nome) + '</option>';
    });

    row.innerHTML = '<div class="col-4"><select class="form-select form-select-sm filtro-col">' + colOpts + '</select></div>' +
        '<div class="col-3"><select class="form-select form-select-sm filtro-op">' +
        '<option value="="' + (filtro && filtro.operador === '=' ? ' selected' : '') + '>=</option>' +
        '<option value="!="' + (filtro && filtro.operador === '!=' ? ' selected' : '') + '>!=</option>' +
        '<option value=">"' + (filtro && filtro.operador === '>' ? ' selected' : '') + '>></option>' +
        '<option value="<"' + (filtro && filtro.operador === '<' ? ' selected' : '') + '><</option>' +
        '<option value=">="' + (filtro && filtro.operador === '>=' ? ' selected' : '') + '>>=</option>' +
        '<option value="<="' + (filtro && filtro.operador === '<=' ? ' selected' : '') + '><=</option>' +
        '<option value="LIKE"' + (filtro && filtro.operador === 'LIKE' ? ' selected' : '') + '>LIKE</option>' +
        '</select></div>' +
        '<div class="col-4"><input type="text" class="form-control form-control-sm filtro-val" value="' + (filtro ? filtro.valor : '') + '" placeholder="Valor"></div>' +
        '<div class="col-1"><button type="button" class="btn btn-sm btn-outline-danger" onclick="this.closest(\'.filtro-row\').remove()"><i class="bi bi-x"></i></button></div>';
    container.appendChild(row);
}

function limparFiltros() {
    document.getElementById('filtrosContainer').innerHTML = '';
}

function coletarFiltros() {
    var filtros = [];
    document.querySelectorAll('.filtro-row').forEach(function(row) {
        var col = row.querySelector('.filtro-col').value;
        var op = row.querySelector('.filtro-op').value;
        var val = row.querySelector('.filtro-val').value;
        if (col && val) filtros.push({ coluna: col, operador: op, valor: val });
    });
    return filtros.length > 0 ? JSON.stringify(filtros) : null;
}
