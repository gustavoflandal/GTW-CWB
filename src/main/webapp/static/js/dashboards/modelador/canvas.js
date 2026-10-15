import { api } from '/static/js/dashboards/api.js';

var CORES_SCHEMA = { dbo: '#6c757d', muralha: '#0d75bf', dash: '#198754', ia: '#6f42c1' };

var graph = null;
var containerEl = null;
var onChangeCallback = null;
var historicoAcoes = [];
var historicoPos = -1;

var ROW_H = 24;
var HEADER_H = 28;
var NODE_W = 220;

function registrarShapeTabela() {
    X6.Graph.registerNode('erd-table', {
        markup: [
            { tagName: 'rect', selector: 'body' },
            { tagName: 'rect', selector: 'header' },
            { tagName: 'text', selector: 'headerLabel' }
        ],
        attrs: {
            body: {
                x: 0, y: 0,
                width: NODE_W,
                height: HEADER_H + ROW_H + 8,
                fill: '#ffffff',
                stroke: '#6c757d',
                strokeWidth: 1.5,
                rx: 5,
                ry: 5
            },
            header: {
                x: 0, y: 0,
                width: NODE_W,
                height: HEADER_H,
                fill: '#6c757d',
                stroke: 'none',
                rx: 5,
                ry: 5
            },
            headerLabel: {
                x: NODE_W / 2,
                y: HEADER_H / 2 + 4,
                textAnchor: 'middle',
                fill: '#ffffff',
                fontSize: 12,
                fontWeight: 'bold',
                fontFamily: 'system-ui, -apple-system, sans-serif'
            }
        }
    }, true);
}

export function inicializar(container, onChange) {
    containerEl = container;
    onChangeCallback = onChange;

    registrarShapeTabela();

    graph = new X6.Graph({
        container: container,
        width: container.clientWidth,
        height: container.clientHeight,
        background: { color: '#f8f9fa' },
        grid: { visible: true, type: 'dot', args: { color: '#dee2e6', thickness: 1 } },
        panning: { enabled: true, modifiers: [] },
        mousewheel: { enabled: true, factor: 1.1, modifiers: [] },
        connecting: {
            snap: { radius: 30 },
            allowBlank: false,
            allowLoop: false,
            allowMulti: false,
            allowNode: false,
            highlight: true,
            anchor: 'center',
            connectionPoint: 'anchor',
            validateConnection: function (args) {
                if (!args.sourcePort || !args.targetPort) return false;
                if (args.sourceCell === args.targetCell) return false;
                return true;
            },
            createEdge: function () {
                return new X6.Shape.Edge({
                    attrs: {
                        line: { stroke: '#0d6efd', strokeWidth: 2, targetMarker: { name: 'classic', size: 8 } }
                    },
                    labels: [{ attrs: { text: { text: 'N:1', fontSize: 11, fill: '#495057' } }, position: 0.5 }]
                });
            }
        },
        highlighting: {
            magnetAvailable: { name: 'stroke', args: { attrs: { fill: '#198754', stroke: '#198754' } } }
        }
    });

    graph.on('edge:connected', tratarConexao);
    graph.on('node:removed', tratarRemocaoTabela);
    graph.on('edge:removed', notificarMudanca);
    graph.on('node:moved', notificarMudanca);

    var ro = new ResizeObserver(function () {
        if (graph && containerEl) {
            graph.resize(containerEl.clientWidth, containerEl.clientHeight);
        }
    });
    ro.observe(container);

    return graph;
}

export function getGraph() { return graph; }

async function tratarConexao(args) {
    var edge = args.edge;
    var sourceNode = edge.getSourceNode();
    var targetNode = edge.getTargetNode();
    if (!sourceNode || !targetNode) { graph.removeEdge(edge); return; }

    var sourcePort = edge.getSourcePortId();
    var targetPort = edge.getTargetPortId();
    if (!sourcePort || !targetPort) { graph.removeEdge(edge); return; }

    var colOrigem = sourcePort.replace(/_in$/, '');
    var colDestino = targetPort.replace(/_in$/, '');

    var dados = {
        acao: 'validarRelacao',
        idDataset: getDatasetId(),
        idTabelaOrigem: sourceNode.getData().idTabela,
        idTabelaDestino: targetNode.getData().idTabela,
        colunaOrigem: colOrigem,
        colunaDestino: colDestino
    };

    try {
        var r = await api.post('/api/dash/datasets', dados);

        if (!r.ok) {
            graph.removeEdge(edge);
            if (window.Swal) {
                Swal.fire({ icon: 'warning', title: 'Relacao invalida', text: r.erro || 'Erro ao validar', confirmButtonColor: '#0d75bf' });
            }
            return;
        }

        var card = r.cardinalidade || 'many_to_one';
        var label = card === 'many_to_one' ? 'N:1' : card === 'one_to_many' ? '1:N' : '1:1';
        edge.setLabels([{ attrs: { text: { text: label, fontSize: 11, fill: '#495057' } }, position: 0.5 }]);
        edge.setData({
            idTabelaOrigem: dados.idTabelaOrigem,
            idTabelaDestino: dados.idTabelaDestino,
            colunaOrigem: colOrigem,
            colunaDestino: colDestino,
            cardinalidade: card,
            origem: 'usuario'
        });
    } catch (e) {
        graph.removeEdge(edge);
        console.error('Erro ao validar relacao:', e);
    }

    salvarHistorico();
    notificarMudanca();
}

function tratarRemocaoTabela(args) {
    var nodeId = args.node.id;
    graph.getEdges().filter(function (e) {
        var s = e.getSourceNode();
        var t = e.getTargetNode();
        return (s && s.id === nodeId) || (t && t.id === nodeId);
    }).forEach(function (e) { graph.removeEdge(e); });
    notificarMudanca();
}

function notificarMudanca() {
    if (onChangeCallback) onChangeCallback(obterEstado());
}

export function adicionarTabela(tabela, x, y) {
    var existente = graph.getNodes().find(function (n) { return n.getData().idTabela === tabela.id; });
    if (existente) return existente;

    var colunas = tabela.colunas || [];
    var sf = tabela.schema_fisico || tabela.schemaFisico || 'dbo';
    var cor = CORES_SCHEMA[sf] || '#6c757d';
    var isBase = graph.getNodes().length === 0;
    var nomeTabela = tabela.rotulo || tabela.nome_fisico || tabela.nomeFisico || '';
    if (isBase) nomeTabela += ' (base)';
    var numCols = Math.max(colunas.length, 1);
    var alturaTotal = HEADER_H + numCols * ROW_H + 8;

    // Ports: coluna esquerda (entrada) e direita (saida), com label de texto
    var portItems = [];
    colunas.forEach(function (col, idx) {
        var nf = col.nome_fisico || '';
        var tipo = (col.tipo || '').toLowerCase();
        var isPK = col.chave_primaria || false;
        var isFK = !isPK && (nf.startsWith('id_') || nf.endsWith('_id'));
        var corPort = isPK ? '#dc3545' : isFK ? '#0d6efd' : '#495057';
        var prefixo = isPK ? '◆ ' : isFK ? '◇ ' : '• ';
        var corTexto = isPK ? '#dc3545' : isFK ? '#0d6efd' : '#212529';
        var yPort = HEADER_H + idx * ROW_H + ROW_H / 2 + 4;

        // Port direita (saida) — com label do nome da coluna a esquerda e tipo a direita
        portItems.push({
            id: nf,
            group: 'dir',
            args: { x: NODE_W, y: yPort },
            attrs: {
                portBody: { r: 5, magnet: true, fill: corPort, stroke: '#fff', strokeWidth: 1.5 },
                portLabel: { text: prefixo + nf, x: -12, y: 4, textAnchor: 'end', fill: corTexto, fontSize: 10, fontFamily: 'system-ui, sans-serif' },
                portType: { text: tipo, x: 12, y: 4, textAnchor: 'start', fill: '#adb5bd', fontSize: 9, fontFamily: 'system-ui, sans-serif' }
            }
        });
        // Port esquerda (entrada) — sem label
        portItems.push({
            id: nf + '_in',
            group: 'esq',
            args: { x: 0, y: yPort },
            attrs: {
                portBody: { r: 5, magnet: true, fill: corPort, stroke: '#fff', strokeWidth: 1.5 }
            }
        });
    });

    var node = graph.addNode({
        shape: 'erd-table',
        x: x || 100,
        y: y || 100,
        width: NODE_W,
        height: alturaTotal,
        attrs: {
            body: { width: NODE_W, height: alturaTotal, stroke: cor, strokeWidth: isBase ? 2.5 : 1.5 },
            header: { fill: cor },
            headerLabel: { text: nomeTabela }
        },
        data: {
            idTabela: tabela.id,
            schema: sf,
            nomeTabela: tabela.nome_fisico || tabela.nomeFisico || '',
            rotulo: tabela.rotulo,
            isBase: isBase
        },
        ports: {
            groups: {
                dir: {
                    position: { name: 'absolute' },
                    attrs: {
                        portBody: { magnet: true }
                    },
                    markup: [
                        { tagName: 'circle', selector: 'portBody' },
                        { tagName: 'text', selector: 'portLabel' },
                        { tagName: 'text', selector: 'portType' }
                    ]
                },
                esq: {
                    position: { name: 'absolute' },
                    attrs: {
                        portBody: { magnet: true }
                    },
                    markup: [
                        { tagName: 'circle', selector: 'portBody' }
                    ]
                }
            },
            items: portItems
        }
    });

    salvarHistorico();
    notificarMudanca();
    return node;
}

export function adicionarSugestao(sugestao) {
    var sourceNode = graph.getNodes().find(function (n) { return n.getData().idTabela === sugestao.idTabelaOrigem; });
    var targetNode = graph.getNodes().find(function (n) { return n.getData().idTabela === sugestao.idTabelaDestino; });
    if (!sourceNode || !targetNode) return null;

    var sourcePortId = sugestao.colunaOrigem;
    var targetPortId = sugestao.colunaDestino + '_in';

    if (!sourceNode.getPort(sourcePortId)) sourcePortId = sugestao.colunaOrigem + '_in';
    if (!targetNode.getPort(targetPortId)) targetPortId = sugestao.colunaDestino;
    if (!sourceNode.getPort(sourcePortId) || !targetNode.getPort(targetPortId)) return null;

    var jaTem = graph.getEdges().find(function (e) {
        var sd = e.getData();
        if (!sd) return false;
        return sd.colunaOrigem === sugestao.colunaOrigem && sd.colunaDestino === sugestao.colunaDestino
            && sd.idTabelaOrigem === sugestao.idTabelaOrigem && sd.idTabelaDestino === sugestao.idTabelaDestino;
    });
    if (jaTem) return null;

    var card = sugestao.cardinalidade || 'many_to_one';
    var labelTxt = card === 'many_to_one' ? 'N:1' : card === 'one_to_many' ? '1:N' : '1:1';
    var selos = { banco: '🔑', catalogo: '📋', sugerida: '💡' };
    var selo = selos[sugestao.origem] || '';

    var edge = graph.addEdge({
        source: { cell: sourceNode.id, port: sourcePortId },
        target: { cell: targetNode.id, port: targetPortId },
        attrs: {
            line: {
                stroke: sugestao.origem === 'sugerida' ? '#adb5bd' : '#0d6efd',
                strokeWidth: 1.5,
                strokeDasharray: sugestao.origem === 'sugerida' ? '6 3' : sugestao.origem === 'catalogo' ? '4 2' : '0',
                targetMarker: { name: 'classic', size: 6 }
            }
        },
        labels: [
            { attrs: { text: { text: selo + ' ' + labelTxt, fontSize: 10, fill: '#6c757d' } }, position: 0.5 }
        ],
        data: {
            idTabelaOrigem: sugestao.idTabelaOrigem,
            idTabelaDestino: sugestao.idTabelaDestino,
            colunaOrigem: sugestao.colunaOrigem,
            colunaDestino: sugestao.colunaDestino,
            cardinalidade: card,
            origem: sugestao.origem,
            sugestao: true
        }
    });
    return edge;
}

export function confirmarSugestao(edge) {
    var data = edge.getData();
    data.sugestao = false;
    edge.setData(data);
    edge.attr('line/strokeDasharray', '0');
    edge.attr('line/stroke', '#0d6efd');
    edge.attr('line/strokeWidth', 2);
    var card = data.cardinalidade || 'many_to_one';
    var labelTxt = card === 'many_to_one' ? 'N:1' : card === 'one_to_many' ? '1:N' : '1:1';
    edge.setLabels([{ attrs: { text: { text: labelTxt, fontSize: 11, fill: '#495057' } }, position: 0.5 }]);
    salvarHistorico();
    notificarMudanca();
}

export function removerTabela(nodeId) {
    var node = graph.getCellById(nodeId);
    if (node) graph.removeNode(nodeId);
}

export function obterEstado() {
    var nodes = graph.getNodes();
    var edges = graph.getEdges();

    var tabelas = nodes.map(function (n) {
        var d = n.getData();
        return { idTabela: d.idTabela, alias: d.nomeTabela, tipo: d.isBase ? 'base' : 'join', x: n.position().x, y: n.position().y };
    });

    var relacoes = edges.filter(function (e) { return e.getData() && !e.getData().sugestao; }).map(function (e) {
        var d = e.getData();
        return {
            idTabelaOrigem: d.idTabelaOrigem,
            idTabelaDestino: d.idTabelaDestino,
            colunaOrigem: d.colunaOrigem,
            colunaDestino: d.colunaDestino,
            cardinalidade: d.cardinalidade,
            origem: d.origem
        };
    });

    var layout = {};
    nodes.forEach(function (n) {
        var pos = n.position();
        layout[n.getData().idTabela] = { x: pos.x, y: pos.y };
    });

    return { tabelas: tabelas, relacoes: relacoes, layoutJson: layout };
}

export function alinharAutomatico() {
    if (typeof dagre === 'undefined') return;

    var g = new dagre.graphlib.Graph();
    g.setGraph({ rankdir: 'LR', nodesep: 60, ranksep: 120 });
    g.setDefaultEdgeLabel(function () { return {}; });

    graph.getNodes().forEach(function (node) {
        var size = node.getSize();
        g.setNode(node.id, { width: size.width, height: size.height });
    });

    graph.getEdges().forEach(function (edge) {
        var s = edge.getSourceNode();
        var t = edge.getTargetNode();
        if (s && t) g.setEdge(s.id, t.id);
    });

    dagre.layout(g);

    g.nodes().forEach(function (id) {
        var n = g.node(id);
        var cell = graph.getCellById(id);
        if (cell && n) cell.position(n.x - n.width / 2, n.y - n.height / 2, { silent: true });
    });
    graph.getNodes().forEach(function (n) { n.trigger('change:position'); });
    notificarMudanca();
}

export function zoomIn() { if (graph) graph.zoom(0.1); }
export function zoomOut() { if (graph) graph.zoom(-0.1); }
export function zoomReset() { if (graph) { graph.zoomTo(1); graph.centerContent(); } }

function salvarHistorico() {
    var estado = JSON.stringify(obterEstado());
    historicoAcoes = historicoAcoes.slice(0, historicoPos + 1);
    historicoAcoes.push(estado);
    historicoPos = historicoAcoes.length - 1;
}

export function desfazer() {
    if (historicoPos <= 0) return;
    historicoPos--;
}

export function refazer() {
    if (historicoPos >= historicoAcoes.length - 1) return;
    historicoPos++;
}

function getDatasetId() {
    var params = new URLSearchParams(window.location.search);
    return parseInt(params.get('id')) || 0;
}
