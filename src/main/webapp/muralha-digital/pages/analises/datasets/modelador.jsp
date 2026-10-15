<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<link rel="stylesheet" href="/static/vendor/x6/x6.css">
<style>
    .modelador-container { display: flex; height: calc(100vh - 160px); gap: 0; }
    .painel-esquerdo {
        width: 220px; min-width: 180px; max-width: 280px;
        border-right: 1px solid var(--bs-border-color);
        display: flex; flex-direction: column;
        background: var(--bs-body-bg);
    }
    .painel-esquerdo .busca { padding: 0.5rem; border-bottom: 1px solid var(--bs-border-color); }
    .painel-esquerdo .lista-tabelas { flex: 1; overflow-y: auto; }
    .canvas-area {
        flex: 1; position: relative; overflow: hidden;
        background: #f8f9fa;
    }
    .canvas-area .x6-container { width: 100%; height: 100%; }
    .painel-direito {
        width: 240px; min-width: 200px; max-width: 300px;
        border-left: 1px solid var(--bs-border-color);
        display: flex; flex-direction: column;
        background: var(--bs-body-bg);
    }
    .painel-direito .campos-header {
        padding: 0.5rem; border-bottom: 1px solid var(--bs-border-color);
        font-weight: 600; font-size: 0.9rem;
    }
    .painel-direito .campos-lista { flex: 1; overflow-y: auto; padding: 0.25rem; }
    .painel-previa {
        height: 220px; min-height: 120px;
        border-top: 1px solid var(--bs-border-color);
        overflow: hidden;
    }
    .toolbar {
        display: flex; align-items: center; gap: 0.25rem;
        padding: 0.4rem 0.75rem;
        background: var(--bs-body-bg);
        border-bottom: 1px solid var(--bs-border-color);
    }
    .toolbar .btn { padding: 0.2rem 0.5rem; font-size: 0.8rem; }
    .toolbar .separator { width: 1px; height: 20px; background: var(--bs-border-color); margin: 0 0.25rem; }
    .drop-hint {
        position: absolute; top: 50%; left: 50%;
        transform: translate(-50%, -50%);
        color: #adb5bd; font-size: 1rem; text-align: center;
        pointer-events: none; z-index: 1;
    }
    .drop-hint.hidden { display: none; }
</style>

<div class="toolbar">
    <input type="text" id="nomeDataset" class="form-control form-control-sm" placeholder="Nome do dataset" style="width:200px;">
    <div class="separator"></div>
    <button class="btn btn-outline-secondary" id="btnDesfazer" title="Desfazer (Ctrl+Z)"><i class="bi bi-arrow-counterclockwise"></i></button>
    <button class="btn btn-outline-secondary" id="btnRefazer" title="Refazer (Ctrl+Y)"><i class="bi bi-arrow-clockwise"></i></button>
    <div class="separator"></div>
    <button class="btn btn-outline-secondary" id="btnAlinhar" title="Alinhar automaticamente"><i class="bi bi-grid-3x3-gap"></i> Alinhar</button>
    <button class="btn btn-outline-secondary" id="btnZoomIn" title="Zoom +"><i class="bi bi-zoom-in"></i></button>
    <button class="btn btn-outline-secondary" id="btnZoomOut" title="Zoom -"><i class="bi bi-zoom-out"></i></button>
    <button class="btn btn-outline-secondary" id="btnZoomReset" title="Zoom 100%"><i class="bi bi-arrows-fullscreen"></i></button>
    <div class="separator"></div>
    <select id="visibilidade" class="form-select form-select-sm" style="width:120px;">
        <option value="privado">Privado</option>
        <option value="publico">Publico</option>
    </select>
    <div class="ms-auto d-flex gap-1">
        <a href="/muralha-digital/pages/analises/datasets/editor.jsp${not empty param.id ? '?id='.concat(param.id) : ''}"
           class="btn btn-outline-secondary btn-sm" title="Editor classico">
            <i class="bi bi-list-task"></i> Wizard
        </a>
        <a href="/muralha-digital/pages/analises/manual.jsp" target="_blank" class="btn btn-outline-info btn-sm" title="Manual do Dashboard Builder">
            <i class="bi bi-book"></i> Manual
        </a>
        <button class="btn btn-outline-success btn-sm" id="btnSalvar"><i class="bi bi-save"></i> Salvar</button>
        <button class="btn btn-success btn-sm" id="btnPublicar"><i class="bi bi-upload"></i> Publicar</button>
    </div>
</div>

<div class="modelador-container">
    <!-- Painel esquerdo: lista de tabelas -->
    <div class="painel-esquerdo">
        <div class="busca">
            <input type="text" id="buscaTabela" class="form-control form-control-sm" placeholder="Buscar tabela...">
        </div>
        <div class="lista-tabelas list-group list-group-flush" id="listaTabelas"></div>
    </div>

    <!-- Canvas central -->
    <div class="canvas-area" id="canvasArea">
        <div class="drop-hint" id="dropHint">
            <i class="bi bi-diagram-3 d-block" style="font-size:2rem;"></i>
            Arraste tabelas aqui para comecar
        </div>
        <div id="canvasContainer" style="width:100%; height:100%;"></div>
    </div>

    <!-- Painel direito: campos -->
    <div class="painel-direito">
        <div class="campos-header">
            <i class="bi bi-columns-gap me-1"></i>Campos do Dataset
        </div>
        <div class="campos-lista" id="listaCampos"></div>
    </div>
</div>

<!-- Previa -->
<div class="painel-previa" id="painelPrevia"></div>

<!-- Modal: Dialogo de relacao (alternativa por teclado) -->
<div class="modal fade" id="modalRelacao" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title">Adicionar Relacao</h6>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label class="form-label small">Tabela origem</label>
                    <select id="relTabelaOrigem" class="form-select form-select-sm"></select>
                </div>
                <div class="mb-3">
                    <label class="form-label small">Coluna origem</label>
                    <select id="relColunaOrigem" class="form-select form-select-sm"></select>
                </div>
                <div class="mb-3">
                    <label class="form-label small">Tabela destino</label>
                    <select id="relTabelaDestino" class="form-select form-select-sm"></select>
                </div>
                <div class="mb-3">
                    <label class="form-label small">Coluna destino</label>
                    <select id="relColunaDestino" class="form-select form-select-sm"></select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-primary btn-sm" id="btnConfirmarRelacao">Adicionar</button>
            </div>
        </div>
    </div>
</div>

<script src="/static/vendor/x6/x6.js"></script>
<script src="/static/vendor/dagre/dagre.min.js"></script>

<script type="module">
    import * as canvas from '/static/js/dashboards/modelador/canvas.js';
    import * as painelTabelas from '/static/js/dashboards/modelador/painel-tabelas.js';
    import * as camposPanel from '/static/js/dashboards/modelador/campos.js';
    import * as previa from '/static/js/dashboards/modelador/previa.js';
    import { api } from '/static/js/dashboards/api.js';

    var datasetId = 0;
    var colunasMap = {};

    var params = new URLSearchParams(window.location.search);
    if (params.get('id')) datasetId = parseInt(params.get('id'));

    var graph = canvas.inicializar(document.getElementById('canvasContainer'), function (estado) {
        var hint = document.getElementById('dropHint');
        if (estado.tabelas.length > 0) hint.classList.add('hidden');
        else hint.classList.remove('hidden');

        var tabelasInfo = estado.tabelas.map(function (t) {
            var tab = painelTabelas.obterTabela(t.idTabela);
            return { idTabela: t.idTabela, rotulo: tab ? (tab.rotulo || tab.nomeFisico || tab.nome_fisico) : t.alias, nomeTabela: t.alias, tipo: t.tipo };
        });
        camposPanel.atualizarCampos(tabelasInfo, colunasMap);
    });

    painelTabelas.inicializar(
        document.getElementById('listaTabelas'),
        document.getElementById('buscaTabela'),
        null
    );

    camposPanel.inicializar(document.getElementById('listaCampos'), function (campos) {
        previa.atualizar(datasetId, campos);
    });

    previa.inicializar(document.getElementById('painelPrevia'));

    // Drop de tabelas do painel para o canvas
    var canvasArea = document.getElementById('canvasArea');
    canvasArea.addEventListener('dragover', function (e) {
        e.preventDefault();
        e.dataTransfer.dropEffect = 'copy';
    });

    canvasArea.addEventListener('drop', async function (e) {
        e.preventDefault();
        var data;
        try { data = JSON.parse(e.dataTransfer.getData('text/plain')); } catch (err) { return; }
        if (!data || !data.id) return;

        var tabela = painelTabelas.obterTabela(data.id);
        if (!tabela) return;

        var colunas = await painelTabelas.carregarColunasTabela(data.id);
        tabela.colunas = colunas;
        colunasMap[data.id] = colunas;

        var rect = canvasArea.getBoundingClientRect();
        var x = e.clientX - rect.left;
        var y = e.clientY - rect.top;

        canvas.adicionarTabela(tabela, x, y);

        // Carregar sugestoes de relacao
        if (datasetId > 0) {
            var r = await api.get('/api/dash/datasets?acao=sugestoesRelacao&idDataset=' + datasetId + '&idTabela=' + data.id);
            if (r.ok && r.sugestoes) {
                r.sugestoes.forEach(function (s) {
                    var edge = canvas.adicionarSugestao(s);
                    if (edge) {
                        edge.on('click', function () {
                            canvas.confirmarSugestao(edge);
                        });
                    }
                });
            }
        }
    });

    // Toolbar
    document.getElementById('btnDesfazer').addEventListener('click', function () { canvas.desfazer(); });
    document.getElementById('btnRefazer').addEventListener('click', function () { canvas.refazer(); });
    document.getElementById('btnAlinhar').addEventListener('click', function () { canvas.alinharAutomatico(); });
    document.getElementById('btnZoomIn').addEventListener('click', function () { canvas.zoomIn(); });
    document.getElementById('btnZoomOut').addEventListener('click', function () { canvas.zoomOut(); });
    document.getElementById('btnZoomReset').addEventListener('click', function () { canvas.zoomReset(); });

    // Atalhos de teclado
    document.addEventListener('keydown', function (e) {
        if (e.ctrlKey && e.key === 'z') { e.preventDefault(); canvas.desfazer(); }
        if (e.ctrlKey && e.key === 'y') { e.preventDefault(); canvas.refazer(); }
    });

    // Salvar
    document.getElementById('btnSalvar').addEventListener('click', async function () {
        var nome = document.getElementById('nomeDataset').value.trim();
        if (!nome) { Swal.fire({ icon: 'warning', text: 'Informe o nome do dataset', confirmButtonColor: '#0d75bf' }); return; }

        var estado = canvas.obterEstado();
        if (estado.tabelas.length === 0) { Swal.fire({ icon: 'warning', text: 'Adicione pelo menos uma tabela ao canvas', confirmButtonColor: '#0d75bf' }); return; }

        var campos = camposPanel.obterCamposSelecionados();
        var tabelaBase = estado.tabelas.find(function (t) { return t.tipo === 'base'; });

        var payload = {
            acao: 'salvarModelador',
            idDataset: datasetId,
            nome: nome,
            descricao: '',
            visibilidade: document.getElementById('visibilidade').value,
            idTabelaBase: tabelaBase ? tabelaBase.idTabela : estado.tabelas[0].idTabela,
            tabelas: estado.tabelas,
            relacoes: estado.relacoes,
            campos: campos,
            layoutJson: estado.layoutJson
        };

        var r = await api.post('/api/dash/datasets', payload);
        if (r.ok) {
            if (r.id && !datasetId) {
                datasetId = r.id;
                history.replaceState(null, '', '?id=' + datasetId);
            }
            Swal.fire({ icon: 'success', text: 'Dataset salvo!', timer: 1500, showConfirmButton: false });
        } else {
            Swal.fire({ icon: 'error', text: r.erro || 'Erro ao salvar', confirmButtonColor: '#0d75bf' });
        }
    });

    // Publicar
    document.getElementById('btnPublicar').addEventListener('click', async function () {
        if (!datasetId) {
            Swal.fire({ icon: 'warning', text: 'Salve o dataset antes de publicar', confirmButtonColor: '#0d75bf' });
            return;
        }

        var resultado = await Swal.fire({
            title: 'Publicar dataset?',
            text: 'Todos os campos serao validados antes da publicacao.',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#198754',
            confirmButtonText: 'Publicar',
            cancelButtonText: 'Cancelar'
        });

        if (!resultado.isConfirmed) return;

        var r = await api.post('/api/dash/datasets', { acao: 'publicar', id: datasetId });
        if (r.ok) {
            Swal.fire({ icon: 'success', text: 'Dataset publicado!', timer: 1500, showConfirmButton: false });
        } else {
            Swal.fire({ icon: 'error', text: r.erro || 'Erro ao publicar', confirmButtonColor: '#0d75bf' });
        }
    });

    // Carregar dataset existente
    if (datasetId > 0) {
        carregarDataset(datasetId);
    }

    async function carregarDataset(id) {
        var r = await api.get('/api/dash/datasets?acao=obter&id=' + id);
        if (!r.ok) return;

        document.getElementById('nomeDataset').value = r.dataset.nome || '';
        document.getElementById('visibilidade').value = r.dataset.visibilidade || 'privado';

        // Carregar tabelas do dataset no canvas
        if (r.dataset.tabelas && r.dataset.tabelas.length > 0) {
            for (var i = 0; i < r.dataset.tabelas.length; i++) {
                var dt = r.dataset.tabelas[i];
                var tabela = painelTabelas.obterTabela(dt.id_tabela);
                if (!tabela) continue;
                var colunas = await painelTabelas.carregarColunasTabela(dt.id_tabela);
                tabela.colunas = colunas;
                colunasMap[dt.id_tabela] = colunas;

                var layoutData = r.modelador ? r.modelador.layout : null;
                var pos = layoutData && layoutData[dt.id_tabela] ? layoutData[dt.id_tabela] : { x: 100 + i * 260, y: 100 };
                canvas.adicionarTabela(tabela, pos.x, pos.y);
            }

            // Carregar relacoes existentes
            if (r.dataset.relacoes) {
                r.dataset.relacoes.forEach(function (rel) {
                    canvas.adicionarSugestao({
                        idTabelaOrigem: rel.id_tabela_origem,
                        idTabelaDestino: rel.id_tabela_destino,
                        colunaOrigem: rel.coluna_origem,
                        colunaDestino: rel.coluna_destino,
                        cardinalidade: rel.cardinalidade,
                        origem: rel.origem || 'catalogo'
                    });
                });
                // Confirmar relacoes existentes (nao sao sugestoes)
                canvas.getGraph().getEdges().forEach(function (e) {
                    canvas.confirmarSugestao(e);
                });
            }
        }
    }
</script>
