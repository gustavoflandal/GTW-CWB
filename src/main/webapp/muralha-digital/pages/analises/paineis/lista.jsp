<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<style>
    .card-painel {
        transition: transform 0.15s, box-shadow 0.15s;
        cursor: pointer;
        height: 100%;
    }
    .card-painel:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
    .badge-versao {
        font-size: 0.7rem;
    }
</style>

<div class="container-fluid mt-3 px-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h4 class="mb-0"><i class="bi bi-grid-1x2"></i> Paineis Analiticos</h4>
            <small class="text-muted">Dashboard Builder — Muralha Digital</small>
        </div>
        <div class="d-flex gap-2">
            <a href="/muralha-digital/pages/analises/manual.jsp" target="_blank" class="btn btn-outline-info btn-sm" title="Manual do Dashboard Builder">
                <i class="bi bi-book"></i> Manual
            </a>
            <button class="btn btn-primary" id="btnNovo">
                <i class="bi bi-plus-lg"></i> Novo Painel
            </button>
        </div>
    </div>

    <!-- Tabs -->
    <ul class="nav nav-tabs mb-3" id="tabsPainel" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" data-bs-toggle="tab" data-bs-target="#tabMeus" type="button">
                <i class="bi bi-person"></i> Meus Paineis <span class="badge bg-primary" id="countMeus">0</span>
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" data-bs-toggle="tab" data-bs-target="#tabCompartilhados" type="button">
                <i class="bi bi-people"></i> Compartilhados <span class="badge bg-info" id="countComp">0</span>
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" data-bs-toggle="tab" data-bs-target="#tabTodos" type="button">
                <i class="bi bi-grid"></i> Todos
            </button>
        </li>
    </ul>

    <div class="tab-content">
        <div class="tab-pane fade show active" id="tabMeus" role="tabpanel">
            <div class="row g-3" id="gridMeus">
                <div class="col-12 text-center text-muted p-5">
                    <div class="spinner-border" role="status"></div>
                </div>
            </div>
        </div>
        <div class="tab-pane fade" id="tabCompartilhados" role="tabpanel">
            <div class="row g-3" id="gridComp">
                <div class="col-12 text-center text-muted p-5">Carregando...</div>
            </div>
        </div>
        <div class="tab-pane fade" id="tabTodos" role="tabpanel">
            <div class="row g-3" id="gridTodos">
                <div class="col-12 text-center text-muted p-5">Carregando...</div>
            </div>
        </div>
    </div>
</div>

<script type="module">
    import { api } from '/static/js/dashboards/api.js';

    var paineis = [];

    async function carregar() {
        var r = await api.get('/api/dash/paineis?acao=listar');
        if (!r.ok) return;
        paineis = r.paineis || [];

        var meus = paineis.filter(function(p) { return p.proprio; });
        var comp = paineis.filter(function(p) { return !p.proprio; });

        document.getElementById('countMeus').textContent = meus.length;
        document.getElementById('countComp').textContent = comp.length;

        renderizarCards('gridMeus', meus, 'Nenhum painel criado. Clique em "Novo Painel" para comecar.');
        renderizarCards('gridComp', comp, 'Nenhum painel compartilhado com voce.');
        renderizarCards('gridTodos', paineis, 'Nenhum painel disponivel.');
    }

    function renderizarCards(containerId, lista, msgVazia) {
        var container = document.getElementById(containerId);
        if (!container) return;

        if (lista.length === 0) {
            container.innerHTML = '<div class="col-12 text-center text-muted p-5">' +
                '<i class="bi bi-inbox" style="font-size:2rem"></i>' +
                '<p class="mt-2">' + msgVazia + '</p></div>';
            return;
        }

        var html = '';
        for (var i = 0; i < lista.length; i++) {
            var p = lista[i];
            html += '<div class="col-sm-6 col-md-4 col-lg-3">';
            html += '<div class="card card-painel" data-id="' + p.id + '">';
            html += '<div class="card-body">';
            html += '<div class="d-flex justify-content-between align-items-start mb-2">';
            html += '<h6 class="card-title mb-0">' + escapeHtml(p.nome) + '</h6>';
            html += '<span class="badge bg-secondary badge-versao">v' + (p.versao || 1) + '</span>';
            html += '</div>';
            if (p.descricao) {
                html += '<p class="card-text text-muted" style="font-size:0.85rem">' + escapeHtml(p.descricao) + '</p>';
            }
            html += '<div class="d-flex gap-1">';
            if (p.proprio) {
                html += '<span class="badge bg-primary"><i class="bi bi-person"></i> Meu</span>';
            } else {
                html += '<span class="badge bg-info"><i class="bi bi-share"></i> Compartilhado</span>';
            }
            if (p.refreshSeg > 0) {
                html += '<span class="badge bg-success"><i class="bi bi-arrow-clockwise"></i> ' + formatarRefresh(p.refreshSeg) + '</span>';
            }
            html += '</div>';
            html += '</div></div></div>';
        }
        container.innerHTML = html;

        container.querySelectorAll('.card-painel').forEach(function(card) {
            card.addEventListener('click', function() {
                window.location.href = 'index.jsp?id=' + this.dataset.id;
            });
        });
    }

    function formatarRefresh(seg) {
        if (seg >= 60) return Math.round(seg / 60) + 'min';
        return seg + 's';
    }

    document.getElementById('btnNovo').addEventListener('click', async function() {
        var nome = prompt('Nome do novo painel:');
        if (!nome || !nome.trim()) return;

        var r = await api.post('/api/dash/paineis', {
            acao: 'salvarPainel',
            nome: nome.trim()
        });
        if (r.ok) {
            window.location.href = 'index.jsp?id=' + r.id;
        } else {
            alert(r.erro || 'Erro ao criar painel');
        }
    });

    function escapeHtml(s) {
        var d = document.createElement('div');
        d.appendChild(document.createTextNode(String(s || '')));
        return d.innerHTML;
    }

    carregar();
</script>
