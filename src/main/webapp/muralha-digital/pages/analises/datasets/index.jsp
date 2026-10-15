<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<style>
    .status-badge-publicado { background-color: #198754; }
    .status-badge-rascunho { background-color: #6c757d; }
    .status-badge-arquivado { background-color: #dc3545; }
</style>

<div class="container-fluid py-3">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="mb-0"><i class="bi bi-database me-2"></i>Datasets</h4>
        <div class="d-flex gap-2">
            <a href="/muralha-digital/pages/analises/manual.jsp" target="_blank" class="btn btn-outline-info btn-sm" title="Manual do Dashboard Builder">
                <i class="bi bi-book"></i> Manual
            </a>
            <a href="/muralha-digital/pages/analises/catalogo/index.jsp" class="btn btn-outline-secondary">
                <i class="bi bi-journal-code"></i> Catalogo de Dados
            </a>
            <a href="/muralha-digital/pages/analises/datasets/editor.jsp" class="btn btn-primary">
                <i class="bi bi-plus-lg"></i> Novo Dataset
            </a>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-body p-0">
            <table class="table table-hover mb-0" id="tabelaDatasets">
                <thead class="table-light">
                    <tr>
                        <th>Nome</th>
                        <th>Tabela Base</th>
                        <th>Status</th>
                        <th>Versao</th>
                        <th class="text-end">Acoes</th>
                    </tr>
                </thead>
                <tbody id="corpoTabela">
                    <tr>
                        <td colspan="5" class="text-center p-4">
                            <div class="spinner-border spinner-border-sm" role="status"></div>
                            Carregando...
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script type="module">
    import { api } from '/static/js/dashboards/api.js';

    async function carregarDatasets() {
        var r = await api.get('/api/dash/datasets?acao=listar');
        var tbody = document.getElementById('corpoTabela');

        if (!r.ok || !r.datasets || r.datasets.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted p-4">Nenhum dataset encontrado</td></tr>';
            return;
        }

        tbody.innerHTML = '';
        for (var ds of r.datasets) {
            var tr = document.createElement('tr');
            var badgeClass = 'status-badge-' + ds.status;
            tr.innerHTML =
                '<td><strong>' + ds.nome + '</strong>' +
                    (ds.proprio ? ' <span class="badge bg-primary">Meu</span>' : '') +
                '</td>' +
                '<td>' + (ds.tabelaBase || '-') + '</td>' +
                '<td><span class="badge ' + badgeClass + '">' + ds.status + '</span></td>' +
                '<td>v' + ds.versao + '</td>' +
                '<td class="text-end">' +
                    '<a href="/muralha-digital/pages/analises/datasets/editor.jsp?id=' + ds.id + '" ' +
                       'class="btn btn-sm btn-outline-primary me-1" title="Editar">' +
                        '<i class="bi bi-pencil"></i></a>' +
                    (ds.status === 'rascunho' && ds.proprio ?
                        '<button class="btn btn-sm btn-outline-success me-1 btn-publicar" data-id="' + ds.id + '" title="Publicar">' +
                            '<i class="bi bi-upload"></i></button>' : '') +
                    (ds.proprio ?
                        '<button class="btn btn-sm btn-outline-danger btn-excluir" data-id="' + ds.id + '" title="Excluir">' +
                            '<i class="bi bi-trash"></i></button>' : '') +
                '</td>';
            tbody.appendChild(tr);
        }

        tbody.addEventListener('click', function(e) {
            var btnPub = e.target.closest('.btn-publicar');
            if (btnPub) { publicarDataset(parseInt(btnPub.dataset.id)); return; }
            var btnExc = e.target.closest('.btn-excluir');
            if (btnExc) { excluirDataset(parseInt(btnExc.dataset.id)); return; }
        });
    }

    async function publicarDataset(id) {
        if (!confirm('Publicar este dataset? Todos os campos serao validados antes da publicacao.')) return;
        var r = await api.post('/api/dash/datasets', { acao: 'publicar', id: id });
        if (r.ok) {
            alert('Dataset publicado!');
            carregarDatasets();
        } else {
            alert('Erro: ' + r.erro);
        }
    }

    async function excluirDataset(id) {
        if (!confirm('Excluir (arquivar) este dataset?')) return;
        var r = await api.post('/api/dash/datasets', { acao: 'excluir', id: id });
        if (r.ok) {
            carregarDatasets();
        } else {
            alert('Erro: ' + r.erro);
        }
    }

    carregarDatasets();
</script>
