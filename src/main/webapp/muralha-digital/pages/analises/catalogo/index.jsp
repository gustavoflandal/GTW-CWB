<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<style>
    .badge-schema-dbo { background-color: #6c757d; }
    .badge-schema-muralha { background-color: #0d75bf; }
    .badge-papel-dimensao { background-color: #0d6efd; }
    .badge-papel-medida { background-color: #198754; }
    .badge-papel-tempo { background-color: #f59e0b; color: #000; }
    .badge-papel-geo { background-color: #6f42c1; }
    .table th { font-size: 0.85rem; }
    .table td { font-size: 0.85rem; vertical-align: middle; }
</style>

<div class="container-fluid py-3">
    <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb mb-0" style="font-size:0.85rem">
            <li class="breadcrumb-item"><a href="/muralha-digital/pages/analises/datasets/index.jsp"><i class="bi bi-database"></i> Datasets</a></li>
            <li class="breadcrumb-item active"><i class="bi bi-journal-code"></i> Catalogo de Dados</li>
        </ol>
    </nav>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="mb-0"><i class="bi bi-journal-code me-2"></i>Catalogo de Dados</h4>
        <a href="/muralha-digital/pages/analises/manual.jsp" target="_blank" class="btn btn-outline-info btn-sm" title="Manual do Dashboard Builder">
            <i class="bi bi-book"></i> Manual
        </a>
    </div>

    <ul class="nav nav-tabs mb-3" id="tabsCatalogo" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" data-bs-toggle="tab" data-bs-target="#tabTabelas" type="button">
                <i class="bi bi-table"></i> Tabelas <span class="badge bg-secondary" id="countTabelas">0</span>
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" data-bs-toggle="tab" data-bs-target="#tabColunas" type="button">
                <i class="bi bi-list-columns"></i> Colunas
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" data-bs-toggle="tab" data-bs-target="#tabRelacoes" type="button">
                <i class="bi bi-diagram-3"></i> Relacoes <span class="badge bg-secondary" id="countRelacoes">0</span>
            </button>
        </li>
    </ul>

    <div class="tab-content">
        <!-- Aba Tabelas -->
        <div class="tab-pane fade show active" id="tabTabelas" role="tabpanel">
            <div class="d-flex justify-content-end mb-2">
                <button class="btn btn-primary btn-sm" onclick="abrirFormTabela(null)">
                    <i class="bi bi-plus-lg"></i> Nova Tabela
                </button>
            </div>
            <div class="card shadow-sm">
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover table-sm mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Rotulo</th>
                                    <th>Schema.Nome</th>
                                    <th>Chave Primaria</th>
                                    <th>Sensivel</th>
                                    <th>Ativo</th>
                                    <th class="text-end">Acoes</th>
                                </tr>
                            </thead>
                            <tbody id="corpoTabelas">
                                <tr><td colspan="6" class="text-center p-4">
                                    <div class="spinner-border spinner-border-sm" role="status"></div> Carregando...
                                </td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Aba Colunas -->
        <div class="tab-pane fade" id="tabColunas" role="tabpanel">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <select class="form-select form-select-sm" id="selTabelaColunas" style="max-width:350px;">
                    <option value="">Selecione uma tabela...</option>
                </select>
                <button class="btn btn-primary btn-sm" id="btnNovaColuna" onclick="abrirFormColuna(null)" style="display:none;">
                    <i class="bi bi-plus-lg"></i> Nova Coluna
                </button>
            </div>
            <div class="card shadow-sm">
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover table-sm mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Rotulo</th>
                                    <th>Nome Fisico</th>
                                    <th>Tipo</th>
                                    <th>Papel Sugerido</th>
                                    <th>Sensivel</th>
                                    <th class="text-end">Acoes</th>
                                </tr>
                            </thead>
                            <tbody id="corpoColunas">
                                <tr><td colspan="6" class="text-center text-muted p-4">Selecione uma tabela acima</td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Aba Relacoes -->
        <div class="tab-pane fade" id="tabRelacoes" role="tabpanel">
            <div class="d-flex justify-content-end mb-2">
                <button class="btn btn-primary btn-sm" onclick="abrirFormRelacao()">
                    <i class="bi bi-plus-lg"></i> Nova Relacao
                </button>
            </div>
            <div class="card shadow-sm">
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover table-sm mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Tabela Origem</th>
                                    <th></th>
                                    <th>Tabela Destino</th>
                                    <th>Colunas</th>
                                    <th>Cardinalidade</th>
                                    <th class="text-end">Acoes</th>
                                </tr>
                            </thead>
                            <tbody id="corpoRelacoes">
                                <tr><td colspan="6" class="text-center p-4">
                                    <div class="spinner-border spinner-border-sm" role="status"></div> Carregando...
                                </td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Offcanvas -->
<div class="offcanvas offcanvas-end" style="width:500px" id="ocCatalogo" tabindex="-1">
    <div class="offcanvas-header">
        <h5 class="offcanvas-title" id="ocTitulo">Formulario</h5>
        <button class="btn-close" data-bs-dismiss="offcanvas" aria-label="Fechar"></button>
    </div>
    <div class="offcanvas-body" id="ocCorpo"></div>
</div>

<script type="module">
    import { api } from '/static/js/dashboards/api.js';

    var tabelasCache = [];
    var offcanvas = null;

    // =============================================
    // Inicializacao
    // =============================================
    async function init() {
        offcanvas = new bootstrap.Offcanvas(document.getElementById('ocCatalogo'));
        await carregarTabelas();
        await carregarRelacoes();

        document.getElementById('selTabelaColunas').addEventListener('change', function() {
            var id = parseInt(this.value);
            document.getElementById('btnNovaColuna').style.display = id ? '' : 'none';
            if (id) carregarColunas(id);
            else document.getElementById('corpoColunas').innerHTML =
                '<tr><td colspan="6" class="text-center text-muted p-4">Selecione uma tabela acima</td></tr>';
        });

        document.querySelectorAll('[data-bs-toggle="tab"]').forEach(function(tab) {
            tab.addEventListener('shown.bs.tab', function(e) {
                if (e.target.dataset.bsTarget === '#tabRelacoes') carregarRelacoes();
            });
        });
    }

    // =============================================
    // Tabelas
    // =============================================
    async function carregarTabelas() {
        var r = await api.get('/api/dash/catalogo?acao=listarTabelas');
        var tbody = document.getElementById('corpoTabelas');

        if (!r.ok || !r.tabelas || r.tabelas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted p-4">Nenhuma tabela cadastrada</td></tr>';
            tabelasCache = [];
            atualizarDropdownTabelas();
            return;
        }

        tabelasCache = r.tabelas;
        document.getElementById('countTabelas').textContent = tabelasCache.length;

        tbody.innerHTML = '';
        for (var t of tabelasCache) {
            var tr = document.createElement('tr');
            if (!t.ativo) tr.classList.add('text-muted');
            var badgeSchema = t.schemaFisico === 'muralha' ? 'badge-schema-muralha' : 'badge-schema-dbo';
            tr.innerHTML =
                '<td><strong>' + esc(t.rotulo) + '</strong></td>' +
                '<td><span class="badge ' + badgeSchema + '">' + esc(t.schemaFisico) + '</span> ' + esc(t.nomeFisico) + '</td>' +
                '<td><code>' + esc(t.chavePrimaria || 'id') + '</code></td>' +
                '<td>' + (t.sensivel ? '<i class="bi bi-shield-lock text-warning"></i> Sim' : 'Nao') + '</td>' +
                '<td>' + (t.ativo ? '<span class="badge bg-success">Ativo</span>' : '<span class="badge bg-secondary">Inativo</span>') + '</td>' +
                '<td class="text-end">' +
                    '<button class="btn btn-sm btn-outline-primary me-1 btn-editar-tabela" data-id="' + t.id + '" title="Editar">' +
                        '<i class="bi bi-pencil"></i></button>' +
                '</td>';
            tbody.appendChild(tr);
        }

        tbody.addEventListener('click', function(e) {
            var btn = e.target.closest('.btn-editar-tabela');
            if (btn) {
                var tabela = tabelasCache.find(function(t) { return t.id === parseInt(btn.dataset.id); });
                if (tabela) abrirFormTabela(tabela);
            }
        });

        atualizarDropdownTabelas();
    }

    function atualizarDropdownTabelas() {
        var sel = document.getElementById('selTabelaColunas');
        var valorAtual = sel.value;
        sel.innerHTML = '<option value="">Selecione uma tabela...</option>';
        for (var t of tabelasCache) {
            if (!t.ativo) continue;
            var opt = document.createElement('option');
            opt.value = t.id;
            opt.textContent = t.rotulo + ' (' + t.schemaFisico + '.' + t.nomeFisico + ')';
            sel.appendChild(opt);
        }
        if (valorAtual) sel.value = valorAtual;
    }

    // =============================================
    // Colunas
    // =============================================
    async function carregarColunas(idTabela) {
        var r = await api.get('/api/dash/catalogo?acao=listarColunas&idTabela=' + idTabela);
        var tbody = document.getElementById('corpoColunas');

        if (!r.ok || !r.colunas || r.colunas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted p-4">Nenhuma coluna cadastrada para esta tabela</td></tr>';
            return;
        }

        tbody.innerHTML = '';
        var colunas = r.colunas;

        for (var c of colunas) {
            var tr = document.createElement('tr');
            if (!c.ativo) tr.classList.add('text-muted');

            var badgePapel = '';
            if (c.papelSugerido) {
                var cls = 'badge-papel-' + c.papelSugerido;
                badgePapel = '<span class="badge ' + cls + '">' + esc(c.papelSugerido) + '</span>';
            } else {
                badgePapel = '<span class="text-muted">-</span>';
            }

            tr.innerHTML =
                '<td><strong>' + esc(c.rotulo) + '</strong></td>' +
                '<td><code>' + esc(c.nomeFisico) + '</code></td>' +
                '<td>' + esc(c.tipo) + '</td>' +
                '<td>' + badgePapel + '</td>' +
                '<td>' + (c.sensivel ? '<i class="bi bi-shield-lock text-warning"></i> Sim' : 'Nao') + '</td>' +
                '<td class="text-end">' +
                    '<button class="btn btn-sm btn-outline-primary btn-editar-coluna" ' +
                        'data-col=\'' + JSON.stringify(c).replace(/'/g, '&#39;') + '\' title="Editar">' +
                        '<i class="bi bi-pencil"></i></button>' +
                '</td>';
            tbody.appendChild(tr);
        }

        tbody.addEventListener('click', function(e) {
            var btn = e.target.closest('.btn-editar-coluna');
            if (btn) {
                var col = JSON.parse(btn.dataset.col);
                abrirFormColuna(col);
            }
        });
    }

    // =============================================
    // Relacoes
    // =============================================
    async function carregarRelacoes() {
        var r = await api.get('/api/dash/catalogo?acao=listarRelacoes');
        var tbody = document.getElementById('corpoRelacoes');

        if (!r.ok || !r.relacoes || r.relacoes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted p-4">Nenhuma relacao cadastrada</td></tr>';
            document.getElementById('countRelacoes').textContent = '0';
            return;
        }

        document.getElementById('countRelacoes').textContent = r.relacoes.length;
        tbody.innerHTML = '';

        for (var rel of r.relacoes) {
            var tr = document.createElement('tr');
            tr.innerHTML =
                '<td>' + esc(rel.origemRotulo) + '</td>' +
                '<td class="text-center"><i class="bi bi-arrow-right"></i></td>' +
                '<td>' + esc(rel.destinoRotulo) + '</td>' +
                '<td><code>' + esc(rel.colunaOrigem) + ' = ' + esc(rel.colunaDestino) + '</code></td>' +
                '<td><span class="badge bg-info">' + esc(rel.cardinalidade) + '</span></td>' +
                '<td class="text-end">' +
                    '<button class="btn btn-sm btn-outline-danger btn-excluir-rel" data-id="' + rel.id + '" title="Excluir">' +
                        '<i class="bi bi-trash"></i></button>' +
                '</td>';
            tbody.appendChild(tr);
        }

        tbody.addEventListener('click', async function(e) {
            var btn = e.target.closest('.btn-excluir-rel');
            if (btn) {
                if (!confirm('Desativar esta relacao?')) return;
                var rr = await api.post('/api/dash/catalogo', { acao: 'excluirRelacao', id: parseInt(btn.dataset.id) });
                if (rr.ok) carregarRelacoes();
                else alert('Erro: ' + rr.erro);
            }
        });
    }

    // =============================================
    // Formularios (offcanvas)
    // =============================================
    window.abrirFormTabela = function(tabela) {
        var editando = tabela != null;
        document.getElementById('ocTitulo').textContent = editando ? 'Editar Tabela' : 'Nova Tabela';

        document.getElementById('ocCorpo').innerHTML =
            '<input type="hidden" id="frmTabelaId" value="' + (editando ? tabela.id : 0) + '">' +
            '<div class="mb-3">' +
                '<label class="form-label small">Nome Fisico <span class="text-danger">*</span></label>' +
                '<input type="text" class="form-control form-control-sm" id="frmNomeFisico" ' +
                    'value="' + esc(editando ? tabela.nomeFisico : '') + '" placeholder="veiculo_tempo_real">' +
            '</div>' +
            '<div class="mb-3">' +
                '<label class="form-label small">Schema</label>' +
                '<select class="form-select form-select-sm" id="frmSchema">' +
                    '<option value="dbo"' + (editando && tabela.schemaFisico === 'dbo' ? ' selected' : '') + '>dbo</option>' +
                    '<option value="muralha"' + (editando && tabela.schemaFisico === 'muralha' ? ' selected' : '') + '>muralha</option>' +
                    '<option value="dash"' + (editando && tabela.schemaFisico === 'dash' ? ' selected' : '') + '>dash</option>' +
                '</select>' +
            '</div>' +
            '<div class="mb-3">' +
                '<label class="form-label small">Rotulo <span class="text-danger">*</span></label>' +
                '<input type="text" class="form-control form-control-sm" id="frmRotulo" ' +
                    'value="' + esc(editando ? tabela.rotulo : '') + '" placeholder="Passagens de Veiculos">' +
            '</div>' +
            '<div class="mb-3">' +
                '<label class="form-label small">Descricao</label>' +
                '<textarea class="form-control form-control-sm" id="frmDescricao" rows="2">' +
                    esc(editando && tabela.descricao ? tabela.descricao : '') + '</textarea>' +
            '</div>' +
            '<div class="row mb-3">' +
                '<div class="col">' +
                    '<label class="form-label small">Chave Primaria</label>' +
                    '<input type="text" class="form-control form-control-sm" id="frmChavePrimaria" ' +
                        'value="' + esc(editando ? (tabela.chavePrimaria || 'id') : 'id') + '">' +
                '</div>' +
                '<div class="col-auto d-flex align-items-end">' +
                    '<div class="form-check">' +
                        '<input class="form-check-input" type="checkbox" id="frmSensivel" ' +
                            (editando && tabela.sensivel ? 'checked' : '') + '>' +
                        '<label class="form-check-label small" for="frmSensivel">Sensivel</label>' +
                    '</div>' +
                '</div>' +
            '</div>' +
            '<button class="btn btn-primary w-100" id="btnSalvarTabela">' +
                '<i class="bi bi-check-lg"></i> Salvar</button>';

        document.getElementById('btnSalvarTabela').addEventListener('click', async function() {
            var dados = {
                acao: 'salvarTabela',
                nomeFisico: document.getElementById('frmNomeFisico').value.trim(),
                schemaFisico: document.getElementById('frmSchema').value,
                rotulo: document.getElementById('frmRotulo').value.trim(),
                descricao: document.getElementById('frmDescricao').value.trim() || null,
                chavePrimaria: document.getElementById('frmChavePrimaria').value.trim() || 'id',
                sensivel: document.getElementById('frmSensivel').checked
            };

            if (!dados.nomeFisico || !dados.rotulo) {
                alert('Preencha nome fisico e rotulo');
                return;
            }

            var r = await api.post('/api/dash/catalogo', dados);
            if (r.ok) {
                offcanvas.hide();
                await carregarTabelas();
            } else {
                alert('Erro: ' + r.erro);
            }
        });

        offcanvas.show();
    };

    window.abrirFormColuna = function(coluna) {
        var editando = coluna != null;
        var idTabela = document.getElementById('selTabelaColunas').value;

        document.getElementById('ocTitulo').textContent = editando ? 'Editar Coluna' : 'Nova Coluna';

        var tipos = ['texto', 'numerico', 'inteiro', 'data', 'data_hora', 'booleano'];
        var papeis = [
            { valor: '', label: 'Nenhum' },
            { valor: 'dimensao', label: 'Dimensao' },
            { valor: 'medida', label: 'Medida' },
            { valor: 'tempo', label: 'Tempo' },
            { valor: 'geo', label: 'Geo' }
        ];

        var tiposHtml = tipos.map(function(t) {
            return '<option value="' + t + '"' + (editando && coluna.tipo === t ? ' selected' : '') + '>' + t + '</option>';
        }).join('');

        var papeisHtml = papeis.map(function(p) {
            var sel = editando && (coluna.papelSugerido || '') === p.valor ? ' selected' : '';
            if (!editando && p.valor === '') sel = ' selected';
            return '<option value="' + p.valor + '"' + sel + '>' + p.label + '</option>';
        }).join('');

        document.getElementById('ocCorpo').innerHTML =
            '<input type="hidden" id="frmColunaId" value="' + (editando ? coluna.id : 0) + '">' +
            '<div class="mb-3">' +
                '<label class="form-label small">Nome Fisico <span class="text-danger">*</span></label>' +
                '<input type="text" class="form-control form-control-sm" id="frmColNomeFisico" ' +
                    'value="' + esc(editando ? coluna.nomeFisico : '') + '" placeholder="velocidade">' +
            '</div>' +
            '<div class="mb-3">' +
                '<label class="form-label small">Rotulo <span class="text-danger">*</span></label>' +
                '<input type="text" class="form-control form-control-sm" id="frmColRotulo" ' +
                    'value="' + esc(editando ? coluna.rotulo : '') + '" placeholder="Velocidade">' +
            '</div>' +
            '<div class="row mb-3">' +
                '<div class="col">' +
                    '<label class="form-label small">Tipo</label>' +
                    '<select class="form-select form-select-sm" id="frmColTipo">' + tiposHtml + '</select>' +
                '</div>' +
                '<div class="col">' +
                    '<label class="form-label small">Papel Sugerido</label>' +
                    '<select class="form-select form-select-sm" id="frmColPapel">' + papeisHtml + '</select>' +
                '</div>' +
            '</div>' +
            '<div class="mb-3">' +
                '<div class="form-check">' +
                    '<input class="form-check-input" type="checkbox" id="frmColSensivel" ' +
                        (editando && coluna.sensivel ? 'checked' : '') + '>' +
                    '<label class="form-check-label small" for="frmColSensivel">Coluna sensivel (dados pessoais)</label>' +
                '</div>' +
            '</div>' +
            '<button class="btn btn-primary w-100" id="btnSalvarColuna">' +
                '<i class="bi bi-check-lg"></i> Salvar</button>';

        document.getElementById('btnSalvarColuna').addEventListener('click', async function() {
            var dados = {
                acao: 'salvarColuna',
                id: parseInt(document.getElementById('frmColunaId').value),
                idTabela: parseInt(idTabela),
                nomeFisico: document.getElementById('frmColNomeFisico').value.trim(),
                rotulo: document.getElementById('frmColRotulo').value.trim(),
                tipo: document.getElementById('frmColTipo').value,
                papelSugerido: document.getElementById('frmColPapel').value || null,
                sensivel: document.getElementById('frmColSensivel').checked
            };

            if (!dados.nomeFisico || !dados.rotulo) {
                alert('Preencha nome fisico e rotulo');
                return;
            }

            var r = await api.post('/api/dash/catalogo', dados);
            if (r.ok) {
                offcanvas.hide();
                await carregarColunas(parseInt(idTabela));
            } else {
                alert('Erro: ' + r.erro);
            }
        });

        offcanvas.show();
    };

    window.abrirFormRelacao = function() {
        document.getElementById('ocTitulo').textContent = 'Nova Relacao';

        var optsTabelas = tabelasCache.filter(function(t) { return t.ativo; }).map(function(t) {
            return '<option value="' + t.id + '">' + esc(t.rotulo) + ' (' + esc(t.schemaFisico) + '.' + esc(t.nomeFisico) + ')</option>';
        }).join('');

        document.getElementById('ocCorpo').innerHTML =
            '<div class="mb-3">' +
                '<label class="form-label small">Tabela Origem <span class="text-danger">*</span></label>' +
                '<select class="form-select form-select-sm" id="frmRelOrigem">' +
                    '<option value="">Selecione...</option>' + optsTabelas + '</select>' +
            '</div>' +
            '<div class="mb-3">' +
                '<label class="form-label small">Tabela Destino <span class="text-danger">*</span></label>' +
                '<select class="form-select form-select-sm" id="frmRelDestino">' +
                    '<option value="">Selecione...</option>' + optsTabelas + '</select>' +
            '</div>' +
            '<div class="row mb-3">' +
                '<div class="col">' +
                    '<label class="form-label small">Coluna Origem <span class="text-danger">*</span></label>' +
                    '<input type="text" class="form-control form-control-sm" id="frmRelColOrigem" placeholder="id_equipamento">' +
                '</div>' +
                '<div class="col">' +
                    '<label class="form-label small">Coluna Destino <span class="text-danger">*</span></label>' +
                    '<input type="text" class="form-control form-control-sm" id="frmRelColDestino" placeholder="id">' +
                '</div>' +
            '</div>' +
            '<div class="mb-3">' +
                '<label class="form-label small">Cardinalidade</label>' +
                '<select class="form-select form-select-sm" id="frmRelCard">' +
                    '<option value="many_to_one">many_to_one</option>' +
                    '<option value="one_to_many">one_to_many</option>' +
                    '<option value="one_to_one">one_to_one</option>' +
                '</select>' +
            '</div>' +
            '<button class="btn btn-primary w-100" id="btnSalvarRelacao">' +
                '<i class="bi bi-check-lg"></i> Salvar</button>';

        document.getElementById('btnSalvarRelacao').addEventListener('click', async function() {
            var idOrigem = document.getElementById('frmRelOrigem').value;
            var idDestino = document.getElementById('frmRelDestino').value;
            var colOrigem = document.getElementById('frmRelColOrigem').value.trim();
            var colDestino = document.getElementById('frmRelColDestino').value.trim();

            if (!idOrigem || !idDestino || !colOrigem || !colDestino) {
                alert('Preencha todos os campos');
                return;
            }

            var dados = {
                acao: 'salvarRelacao',
                idTabelaOrigem: parseInt(idOrigem),
                idTabelaDestino: parseInt(idDestino),
                colunaOrigem: colOrigem,
                colunaDestino: colDestino,
                cardinalidade: document.getElementById('frmRelCard').value
            };

            var r = await api.post('/api/dash/catalogo', dados);
            if (r.ok) {
                offcanvas.hide();
                await carregarRelacoes();
            } else {
                alert('Erro: ' + r.erro);
            }
        });

        offcanvas.show();
    };

    // =============================================
    // Util
    // =============================================
    function esc(s) {
        var d = document.createElement('div');
        d.appendChild(document.createTextNode(String(s || '')));
        return d.innerHTML;
    }

    init();
</script>
