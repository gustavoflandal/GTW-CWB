import { api } from '/static/js/dashboards/api.js';
import { validarExpressao } from '/static/js/dashboards/gramatica-validador.js';

// === Estado ===
var passoAtual = 1;
var totalPassos = 6;
var datasetId = 0;
var tabelaBaseSelecionada = null;
var relacoesIncluidas = [];
var camposSelecionados = [];
var camposCalculados = [];

// === Inicializacao ===
async function init() {
    var params = new URLSearchParams(window.location.search);
    var id = params.get('id');
    if (id) {
        datasetId = parseInt(id);
        document.getElementById('tituloEditor').innerHTML =
            '<i class="bi bi-database me-2"></i>Editar Dataset';
    }

    await carregarTabelasCatalogo();

    if (datasetId > 0) {
        await carregarDatasetExistente();
    }

    configurarEventos();
    atualizarNavegacao();
}

// === Carregar tabelas do catalogo ===
async function carregarTabelasCatalogo() {
    var r = await api.get('/api/dash/datasets?acao=tabelasCatalogo');
    if (!r.ok) return;

    var sel = document.getElementById('dsTabelaBase');
    for (var t of r.tabelas) {
        var opt = document.createElement('option');
        opt.value = t.id;
        opt.textContent = t.rotulo + ' (' + t.schemaFisico + '.' + t.nomeFisico + ')';
        opt.dataset.nomeFisico = t.nomeFisico;
        sel.appendChild(opt);
    }
}

// === Carregar dataset existente ===
async function carregarDatasetExistente() {
    var r = await api.get('/api/dash/datasets?acao=obter&id=' + datasetId);
    if (!r.ok) return;

    var ds = r.dataset;
    document.getElementById('dsNome').value = ds.nome;
    document.getElementById('dsDescricao').value = ds.descricao || '';
    document.getElementById('dsTabelaBase').value = ds.idTabelaBase;
    document.getElementById('dsVisibilidade').value = ds.visibilidade;
    tabelaBaseSelecionada = ds.idTabelaBase;

    // Restaurar relacoes incluidas
    if (ds.tabelas) {
        for (var t of ds.tabelas) {
            if (t.tipo === 'join') {
                relacoesIncluidas.push({
                    idTabela: t.idTabela,
                    alias: t.alias,
                    rotulo: t.rotulo,
                    nomeFisico: t.nomeFisico
                });
            }
        }
    }

    // Restaurar campos
    if (ds.campos) {
        for (var c of ds.campos) {
            if (c.expressao) {
                camposCalculados.push({
                    nomeAlias: c.nomeAlias,
                    rotulo: c.rotulo,
                    expressao: c.expressao,
                    tipoCampo: c.tipoCampo,
                    agregacao: c.agregacao
                });
            } else {
                camposSelecionados.push({
                    idColuna: c.idColuna,
                    nomeAlias: c.nomeAlias,
                    rotulo: c.rotulo,
                    tipoCampo: c.tipoCampo,
                    agregacao: c.agregacao
                });
            }
        }
    }

    await carregarRelacoes(tabelaBaseSelecionada);
    await carregarCamposTabelas();
}

// === Eventos ===
function configurarEventos() {
    document.getElementById('btnProximo').addEventListener('click', function() {
        if (validarPasso(passoAtual)) {
            irParaPasso(passoAtual + 1);
        }
    });

    document.getElementById('btnAnterior').addEventListener('click', function() {
        irParaPasso(passoAtual - 1);
    });

    document.querySelectorAll('.step-indicator').forEach(function(el) {
        el.addEventListener('click', function() {
            var step = parseInt(el.dataset.step);
            if (step < passoAtual || validarPasso(passoAtual)) {
                irParaPasso(step);
            }
        });
    });

    document.getElementById('dsTabelaBase').addEventListener('change', async function() {
        tabelaBaseSelecionada = this.value ? parseInt(this.value) : null;
        relacoesIncluidas = [];
        if (tabelaBaseSelecionada) {
            await carregarRelacoes(tabelaBaseSelecionada);
        }
    });

    document.getElementById('btnNovoCampoCalc').addEventListener('click', function() {
        document.getElementById('formCampoCalc').style.display = 'block';
        document.getElementById('calcAlias').value = '';
        document.getElementById('calcRotulo').value = '';
        document.getElementById('calcExpressao').value = '';
        document.getElementById('calcValidacao').textContent = '';
    });

    document.getElementById('btnCancelarCalc').addEventListener('click', function() {
        document.getElementById('formCampoCalc').style.display = 'none';
    });

    document.getElementById('btnSalvarCalc').addEventListener('click', function() {
        adicionarCampoCalculado();
    });

    document.getElementById('calcExpressao').addEventListener('input', function() {
        var r = validarExpressao(this.value);
        var el = document.getElementById('calcValidacao');
        if (!this.value.trim()) {
            el.textContent = '';
            return;
        }
        if (r.valido) {
            el.textContent = 'Expressao valida';
            el.className = 'form-text text-success';
        } else {
            el.textContent = r.erro;
            el.className = 'form-text text-danger';
        }
    });

    document.getElementById('btnPreview').addEventListener('click', carregarPreview);
    document.getElementById('btnPublicar').addEventListener('click', function() { salvarEPublicar(true); });
    document.getElementById('btnSalvarRascunho').addEventListener('click', function() { salvarEPublicar(false); });
}

// === Navegacao ===
function irParaPasso(step) {
    if (step < 1 || step > totalPassos) return;

    document.querySelectorAll('.wizard-step').forEach(function(el) {
        el.classList.remove('active');
    });
    document.querySelector('.wizard-step[data-step="' + step + '"]').classList.add('active');

    document.querySelectorAll('.step-indicator').forEach(function(el) {
        el.classList.remove('active');
        if (parseInt(el.dataset.step) < step) {
            el.classList.add('completed');
        }
    });
    document.querySelector('.step-indicator[data-step="' + step + '"]').classList.add('active');

    passoAtual = step;
    atualizarNavegacao();

    if (step === 3) carregarCamposTabelas();
    if (step === 4) renderizarCamposCalculados();
    if (step === 6) renderizarResumo();
}

function atualizarNavegacao() {
    document.getElementById('btnAnterior').style.display = passoAtual > 1 ? '' : 'none';
    var btnProx = document.getElementById('btnProximo');
    if (passoAtual >= totalPassos) {
        btnProx.style.display = 'none';
    } else {
        btnProx.style.display = '';
    }
}

function validarPasso(step) {
    if (step === 1) {
        if (!document.getElementById('dsNome').value.trim()) {
            alert('Informe o nome do dataset');
            return false;
        }
        if (!tabelaBaseSelecionada) {
            alert('Selecione uma tabela base');
            return false;
        }
    }
    return true;
}

// === Passo 2: Relacoes ===
async function carregarRelacoes(idTabela) {
    var r = await api.get('/api/dash/datasets?acao=relacoes&idTabela=' + idTabela);
    var container = document.getElementById('listaRelacoes');

    if (!r.ok || !r.relacoes || r.relacoes.length === 0) {
        container.innerHTML = '<div class="text-muted">Nenhuma relacao encontrada para esta tabela.</div>';
        return;
    }

    container.innerHTML = '';
    for (var rel of r.relacoes) {
        var div = document.createElement('div');
        var incluida = relacoesIncluidas.some(function(ri) { return ri.idTabela === rel.idTabelaDestino; });
        div.className = 'relacao-item d-flex justify-content-between align-items-center' +
            (incluida ? ' incluida' : '');
        div.dataset.idTabela = rel.idTabelaDestino;
        div.dataset.nomeFisico = rel.nomeFisico;
        div.dataset.rotulo = rel.rotulo;
        div.innerHTML =
            '<div>' +
                '<strong>' + rel.rotulo + '</strong>' +
                ' <small class="text-muted">(' + rel.nomeFisico + ')</small>' +
                '<br><small class="text-muted">' + rel.cardinalidade +
                ' via ' + rel.colunaOrigem + ' = ' + rel.colunaDestino + '</small>' +
            '</div>' +
            '<div>' +
                '<i class="bi ' + (incluida ? 'bi-check-circle-fill text-success' : 'bi-plus-circle') + '"></i>' +
            '</div>';
        div.addEventListener('click', function() { toggleRelacao(this); });
        container.appendChild(div);
    }
}

function toggleRelacao(el) {
    var idTabela = parseInt(el.dataset.idTabela);
    var idx = relacoesIncluidas.findIndex(function(r) { return r.idTabela === idTabela; });

    if (idx >= 0) {
        relacoesIncluidas.splice(idx, 1);
        el.classList.remove('incluida');
        el.querySelector('i').className = 'bi bi-plus-circle';
    } else {
        relacoesIncluidas.push({
            idTabela: idTabela,
            alias: el.dataset.nomeFisico,
            rotulo: el.dataset.rotulo,
            nomeFisico: el.dataset.nomeFisico
        });
        el.classList.add('incluida');
        el.querySelector('i').className = 'bi bi-check-circle-fill text-success';
    }
}

// === Passo 3: Campos ===
async function carregarCamposTabelas() {
    var container = document.getElementById('listaCampos');
    container.innerHTML = '';

    var tabelasIds = [tabelaBaseSelecionada];
    for (var rel of relacoesIncluidas) {
        tabelasIds.push(rel.idTabela);
    }

    for (var idTab of tabelasIds) {
        var r = await api.get('/api/dash/datasets?acao=colunas&idTabela=' + idTab);
        if (!r.ok || !r.colunas) continue;

        var isBase = idTab === tabelaBaseSelecionada;
        var tabelaSel = document.getElementById('dsTabelaBase');
        var labelTabela = isBase ? tabelaSel.options[tabelaSel.selectedIndex].textContent
            : relacoesIncluidas.find(function(ri) { return ri.idTabela === idTab; }).rotulo;

        var secao = document.createElement('div');
        secao.className = 'mb-3';
        secao.innerHTML = '<h6 class="border-bottom pb-1">' +
            '<i class="bi bi-table me-1"></i>' + labelTabela + '</h6>';

        for (var col of r.colunas) {
            var jaSelecionado = camposSelecionados.some(function(cs) { return cs.idColuna === col.id; });
            var div = document.createElement('div');
            div.className = 'campo-item d-flex align-items-center gap-2' +
                (jaSelecionado ? ' selecionado' : '');
            div.innerHTML =
                '<input type="checkbox" class="form-check-input chk-campo" ' +
                    'data-id-coluna="' + col.id + '" ' +
                    'data-nome="' + col.nomeFisico + '" ' +
                    'data-rotulo="' + col.rotulo + '" ' +
                    'data-tipo="' + col.tipo + '" ' +
                    'data-papel="' + (col.papelSugerido || '') + '" ' +
                    (jaSelecionado ? 'checked' : '') + '>' +
                '<div class="flex-grow-1">' +
                    '<strong>' + col.rotulo + '</strong>' +
                    ' <small class="text-muted">(' + col.nomeFisico + ', ' + col.tipo + ')</small>' +
                '</div>' +
                '<select class="form-select form-select-sm sel-tipo-campo" style="width:auto;">' +
                    '<option value="dimensao"' +
                        (col.papelSugerido !== 'medida' ? ' selected' : '') + '>Dimensao</option>' +
                    '<option value="medida"' +
                        (col.papelSugerido === 'medida' ? ' selected' : '') + '>Medida</option>' +
                    '<option value="tempo"' +
                        (col.papelSugerido === 'tempo' ? ' selected' : '') + '>Tempo</option>' +
                '</select>' +
                '<select class="form-select form-select-sm sel-agregacao" style="width:auto;display:' +
                    (col.papelSugerido === 'medida' ? '' : 'none') + ';">' +
                    '<option value="NONE">Nenhuma</option>' +
                    '<option value="SUM">SUM</option>' +
                    '<option value="AVG">AVG</option>' +
                    '<option value="COUNT">COUNT</option>' +
                    '<option value="COUNT_DISTINCT">COUNT DISTINCT</option>' +
                    '<option value="MIN">MIN</option>' +
                    '<option value="MAX">MAX</option>' +
                '</select>';

            var selTipo = div.querySelector('.sel-tipo-campo');
            var selAgr = div.querySelector('.sel-agregacao');
            selTipo.addEventListener('change', function() {
                var agr = this.closest('.campo-item').querySelector('.sel-agregacao');
                agr.style.display = this.value === 'medida' ? '' : 'none';
            });

            div.querySelector('.chk-campo').addEventListener('change', function() {
                this.closest('.campo-item').classList.toggle('selecionado', this.checked);
                atualizarCamposSelecionados();
            });

            // Restaurar agregacao se existente
            if (jaSelecionado) {
                var campoExistente = camposSelecionados.find(function(cs) { return cs.idColuna === col.id; });
                if (campoExistente) {
                    selTipo.value = campoExistente.tipoCampo;
                    selAgr.value = campoExistente.agregacao;
                    if (campoExistente.tipoCampo === 'medida') selAgr.style.display = '';
                }
            }

            secao.appendChild(div);
        }

        container.appendChild(secao);
    }
}

function atualizarCamposSelecionados() {
    camposSelecionados = [];
    document.querySelectorAll('.chk-campo:checked').forEach(function(chk) {
        var item = chk.closest('.campo-item');
        camposSelecionados.push({
            idColuna: parseInt(chk.dataset.idColuna),
            nomeAlias: chk.dataset.nome,
            rotulo: chk.dataset.rotulo,
            tipoCampo: item.querySelector('.sel-tipo-campo').value,
            agregacao: item.querySelector('.sel-agregacao').value
        });
    });
}

// === Passo 4: Campos calculados ===
function adicionarCampoCalculado() {
    var alias = document.getElementById('calcAlias').value.trim();
    var rotulo = document.getElementById('calcRotulo').value.trim();
    var expressao = document.getElementById('calcExpressao').value.trim();
    var tipo = document.getElementById('calcTipo').value;

    if (!alias || !rotulo || !expressao) {
        alert('Preencha alias, rotulo e expressao');
        return;
    }

    var r = validarExpressao(expressao);
    if (!r.valido) {
        alert('Expressao invalida: ' + r.erro);
        return;
    }

    camposCalculados.push({
        nomeAlias: alias,
        rotulo: rotulo,
        expressao: expressao,
        tipoCampo: tipo,
        agregacao: 'NONE'
    });

    document.getElementById('formCampoCalc').style.display = 'none';
    renderizarCamposCalculados();
}

function renderizarCamposCalculados() {
    var container = document.getElementById('listaCamposCalculados');
    container.innerHTML = '';

    if (camposCalculados.length === 0) {
        container.innerHTML = '<div class="text-muted mb-2">Nenhum campo calculado adicionado.</div>';
        return;
    }

    for (var i = 0; i < camposCalculados.length; i++) {
        var c = camposCalculados[i];
        var div = document.createElement('div');
        div.className = 'campo-item d-flex justify-content-between align-items-center';
        div.innerHTML =
            '<div>' +
                '<strong>' + c.rotulo + '</strong> <code>' + c.nomeAlias + '</code>' +
                '<br><small class="text-muted">' + c.expressao + '</small>' +
                ' <span class="badge bg-secondary">' + c.tipoCampo + '</span>' +
            '</div>' +
            '<button class="btn btn-sm btn-outline-danger btn-remover-calc" data-idx="' + i + '">' +
                '<i class="bi bi-trash"></i></button>';
        container.appendChild(div);
    }

    container.addEventListener('click', function(e) {
        var btn = e.target.closest('.btn-remover-calc');
        if (btn) {
            camposCalculados.splice(parseInt(btn.dataset.idx), 1);
            renderizarCamposCalculados();
        }
    });
}

// === Passo 5: Preview ===
async function carregarPreview() {
    atualizarCamposSelecionados();
    var salvo = await salvarDataset();
    if (!salvo) return;

    document.getElementById('previewArea').innerHTML =
        '<div class="text-center p-3"><div class="spinner-border" role="status"></div></div>';

    var todosOsCampos = montarCamposParaSalvar();
    var medidas = todosOsCampos.filter(function(c) { return c.tipoCampo === 'medida'; })
        .map(function(c) { return c.nomeAlias; });
    var dimensoes = todosOsCampos.filter(function(c) { return c.tipoCampo !== 'medida'; })
        .map(function(c) { return c.nomeAlias; });

    var query = {
        measures: medidas.length > 0 ? medidas : [],
        dimensions: dimensoes.length > 0 ? dimensoes.slice(0, 5) : [],
        limit: 50
    };

    var r = await api.post('/api/dash/consultas', {
        datasetId: datasetId,
        query: query
    });

    var area = document.getElementById('previewArea');

    if (!r.ok) {
        area.innerHTML = '<div class="text-danger text-center p-3">' +
            '<i class="bi bi-exclamation-triangle"></i> ' + escaparHtml(r.erro || 'Erro ao carregar') + '</div>';
        return;
    }

    if (!r.data || r.data.length === 0) {
        area.innerHTML = '<div class="text-muted text-center p-3">Sem dados para exibir.</div>';
        return;
    }

    var keys = Object.keys(r.data[0]);
    var html = '<table class="table table-sm table-striped"><thead><tr>';
    for (var k of keys) html += '<th>' + escaparHtml(k) + '</th>';
    html += '</tr></thead><tbody>';
    for (var row of r.data) {
        html += '<tr>';
        for (var k2 of keys) html += '<td>' + (row[k2] != null ? escaparHtml(row[k2]) : '') + '</td>';
        html += '</tr>';
    }
    html += '</tbody></table>';
    html += '<div class="text-muted text-center"><small>' + r.data.length + ' linhas | ' + (r.tempoMs || '-') + 'ms</small></div>';
    area.innerHTML = html;
}

// === Passo 6: Resumo e publicacao ===
function renderizarResumo() {
    atualizarCamposSelecionados();
    var nome = document.getElementById('dsNome').value;
    var nCampos = camposSelecionados.length + camposCalculados.length;
    var nRelacoes = relacoesIncluidas.length;

    document.getElementById('resumoDataset').innerHTML =
        '<p><strong>' + escaparHtml(nome) + '</strong></p>' +
        '<p>' + nCampos + ' campos | ' + nRelacoes + ' tabelas relacionadas</p>' +
        '<p class="text-muted">Ao publicar, todos os campos sao validados contra o catalogo e as relacoes.</p>';
}

function escaparHtml(valor) {
    var d = document.createElement('div');
    d.appendChild(document.createTextNode(String(valor)));
    return d.innerHTML;
}

async function salvarEPublicar(publicar) {
    atualizarCamposSelecionados();
    var salvo = await salvarDataset();
    if (!salvo) return;

    if (publicar) {
        var r = await api.post('/api/dash/datasets', { acao: 'publicar', id: datasetId });
        if (r.ok) {
            alert('Dataset publicado com sucesso!');
            window.location.href = '/muralha-digital/pages/analises/datasets/index.jsp';
        } else {
            alert('Erro ao publicar: ' + r.erro);
        }
    } else {
        alert('Rascunho salvo!');
    }
}

function montarCamposParaSalvar() {
    var campos = [];
    for (var c of camposSelecionados) {
        campos.push({
            idColuna: c.idColuna,
            nomeAlias: c.nomeAlias,
            rotulo: c.rotulo,
            tipoCampo: c.tipoCampo,
            agregacao: c.agregacao || 'NONE',
            expressao: null
        });
    }
    for (var cc of camposCalculados) {
        campos.push({
            idColuna: null,
            nomeAlias: cc.nomeAlias,
            rotulo: cc.rotulo,
            tipoCampo: cc.tipoCampo,
            agregacao: cc.agregacao || 'NONE',
            expressao: cc.expressao
        });
    }
    return campos;
}

async function salvarDataset() {
    var campos = montarCamposParaSalvar();
    if (campos.length === 0) {
        alert('Selecione ao menos um campo');
        return false;
    }

    var tabelas = [];
    // Tabela base
    var selBase = document.getElementById('dsTabelaBase');
    var nomeFisicoBase = selBase.options[selBase.selectedIndex].dataset.nomeFisico || '';
    tabelas.push({ idTabela: tabelaBaseSelecionada, alias: nomeFisicoBase, tipo: 'base' });

    for (var rel of relacoesIncluidas) {
        tabelas.push({ idTabela: rel.idTabela, alias: rel.alias, tipo: 'join' });
    }

    var dados = {
        acao: 'salvar',
        id: datasetId,
        nome: document.getElementById('dsNome').value.trim(),
        descricao: document.getElementById('dsDescricao').value.trim() || null,
        idTabelaBase: tabelaBaseSelecionada,
        visibilidade: document.getElementById('dsVisibilidade').value,
        tabelas: tabelas,
        campos: campos
    };

    var r = await api.post('/api/dash/datasets', dados);
    if (r.ok) {
        if (r.id) datasetId = r.id;
        return true;
    } else {
        alert('Erro ao salvar: ' + r.erro);
        return false;
    }
}

init();
