<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
    <div class="row mb-3 align-items-center">
        <div class="col">
            <h5 class="mb-0"><i class="bi bi-file-earmark-bar-graph me-2"></i>Relatórios Gerenciais</h5>
        </div>
        <div class="col-auto">
            <select class="form-select form-select-sm" id="filtroTipo" style="width:auto;display:inline-block">
                <option value="">Todos os tipos</option>
                <option value="GERENCIAL">Gerencial</option>
                <option value="PRE_PROCESSAMENTO">Pré-Processamento</option>
            </select>
            <button class="btn btn-sm btn-outline-primary ms-2" onclick="carregar()">
                <i class="bi bi-arrow-clockwise"></i> Atualizar
            </button>
        </div>
    </div>

    <div class="card">
        <div class="card-body p-0">
            <table class="table table-hover table-striped mb-0">
                <thead class="table-light">
                    <tr>
                        <th>Título</th>
                        <th>Tipo</th>
                        <th>Período</th>
                        <th>Gerado em</th>
                        <th>Por</th>
                        <th class="text-center">Ações</th>
                    </tr>
                </thead>
                <tbody id="tblRelatorios">
                    <tr><td colspan="6" class="text-center text-muted py-4">Carregando...</td></tr>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Modal Detalhe -->
    <div class="modal fade" id="modalDetalhe" tabindex="-1">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitulo"></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body" id="modalBody" style="max-height:70vh;overflow-y:auto"></div>
                <div class="modal-footer">
                    <button class="btn btn-outline-success btn-sm" id="btnExportCsv">
                        <i class="bi bi-filetype-csv"></i> CSV
                    </button>
                    <button class="btn btn-outline-primary btn-sm" id="btnExportXlsx">
                        <i class="bi bi-file-earmark-excel"></i> XLSX
                    </button>
                    <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Fechar</button>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
var ctx = '<%= request.getContextPath() %>';
var relData = [];
var modalInstance = null;

async function carregar() {
    var tipo = document.getElementById('filtroTipo').value;
    var url = ctx + '/MuralhaDigital/RelatorioGerencial?acao=listar';
    if (tipo) url += '&tipo=' + tipo;
    try {
        var r = await fetch(url);
        var d = await r.json();
        if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }
        relData = d.relatorios;
        renderTabela();
    } catch (e) {
        Swal.fire('Erro', e.message, 'error');
    }
}

function escaparHtml(v) { var d = document.createElement('div'); d.textContent = v == null ? '' : String(v); return d.innerHTML.replace(/"/g, '&quot;').replace(/'/g, '&#39;'); }

function renderTabela() {
    var tbody = document.getElementById('tblRelatorios');
    if (!relData || relData.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">Nenhum relatório encontrado.</td></tr>';
        return;
    }
    var html = '';
    for (var i = 0; i < relData.length; i++) {
        var r = relData[i];
        var tipoBadge = r.tipo === 'PRE_PROCESSAMENTO'
            ? '<span class="badge bg-info">Pré-Processamento</span>'
            : '<span class="badge bg-primary">Gerencial</span>';
        html += '<tr>' +
            '<td>' + escaparHtml(r.titulo) + '</td>' +
            '<td>' + tipoBadge + '</td>' +
            '<td>' + escaparHtml(r.periodoInicio) + ' a ' + escaparHtml(r.periodoFim) + '</td>' +
            '<td>' + escaparHtml(r.dtGeracao) + '</td>' +
            '<td>' + escaparHtml(r.geradoPor) + '</td>' +
            '<td class="text-center">' +
                '<button class="btn btn-sm btn-outline-primary" onclick="verDetalhe(' + Number(r.id) + ')">' +
                '<i class="bi bi-eye"></i></button>' +
            '</td></tr>';
    }
    tbody.innerHTML = html;
}

async function verDetalhe(id) {
    try {
        var r = await fetch(ctx + '/MuralhaDigital/RelatorioGerencial?acao=detalhe&id=' + id);
        var d = await r.json();
        if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }
        document.getElementById('modalTitulo').textContent = d.titulo;
        document.getElementById('modalBody').innerHTML = renderDetalhe(d);
        document.getElementById('btnExportCsv').onclick = function() {
            window.location = ctx + '/MuralhaDigital/RelatorioGerencial?acao=exportarCsv&id=' + id;
        };
        document.getElementById('btnExportXlsx').onclick = function() {
            window.location = ctx + '/MuralhaDigital/RelatorioGerencial?acao=exportarXlsx&id=' + id;
        };
        if (!modalInstance) modalInstance = new bootstrap.Modal(document.getElementById('modalDetalhe'));
        modalInstance.show();
    } catch (e) {
        Swal.fire('Erro', e.message, 'error');
    }
}

function renderDetalhe(d) {
    var dados = d.dados;
    var html = '<div class="mb-3"><small class="text-muted">Período: ' + escaparHtml(d.periodoInicio) + ' a ' + escaparHtml(d.periodoFim) +
        ' | Gerado: ' + escaparHtml(d.dtGeracao) + '</small></div>';

    if (d.tipo === 'PRE_PROCESSAMENTO') {
        html += secao('Volumes', [
            ['Recebidos', fmt(dados.recebidos)],
            ['Processados', fmt(dados.processados)],
            ['Disponibilizados', fmt(dados.disponibilizados)],
            ['Descartados', fmt(dados.descartados)],
            ['Índice de Aproveitamento', fmt(dados.indiceAproveitamento) + '%'],
            ['Tempo Médio Processamento', fmt(dados.tempoMedioProcessamentoSeg) + ' seg']
        ]);
        if (dados.produtividadePorOperador) {
            var p = dados.produtividadePorOperador;
            html += secao('Produtividade', [
                ['Total Analisados', fmt(p.totalAnalisados)],
                ['Operadores', fmt(p.operadores)],
                ['Média por Operador', fmt(p.mediaPorOperador)]
            ]);
        }
        if (dados.divergencia) {
            html += secao('Divergência', [
                ['Total', fmt(dados.divergencia.total)],
                ['Divergentes', fmt(dados.divergencia.divergentes)],
                ['Taxa', fmt(dados.divergencia.taxa) + '%']
            ]);
        }
        if (dados.retrabalho) {
            html += secao('Retrabalho', [
                ['Total', fmt(dados.retrabalho.total)],
                ['Retrabalhados', fmt(dados.retrabalho.retrabalhados)],
                ['Taxa', fmt(dados.retrabalho.taxa) + '%']
            ]);
        }
        if (dados.pendentesEBacklog) {
            html += secao('Pendentes e Backlog', [
                ['Pendentes', fmt(dados.pendentesEBacklog.pendentes)],
                ['Backlog > 72h', fmt(dados.pendentesEBacklog.backlog72h)]
            ]);
        }
        if (dados.sla) {
            html += secao('SLA', [
                ['Janelas', fmt(dados.sla.janelas)],
                ['Violações', fmt(dados.sla.violacoes)],
                ['Cumprimento', fmt(dados.sla.cumprimento) + '%']
            ]);
        }
        if (dados.motivosDescarte) {
            var mRows = [];
            for (var k in dados.motivosDescarte) mRows.push([k, fmt(dados.motivosDescarte[k])]);
            if (mRows.length) html += secao('Motivos de Descarte', mRows);
        }
        if (dados.historicoClassificacao) {
            var cRows = [];
            for (var k2 in dados.historicoClassificacao) cRows.push([k2, fmt(dados.historicoClassificacao[k2])]);
            if (cRows.length) html += secao('Classificação', cRows);
        }
    } else {
        if (dados.volumeTrafego) {
            var vt = dados.volumeTrafego;
            html += secao('Volume de Tráfego', [
                ['Total de Passagens', fmt(vt.totalPassagens)],
                ['Locais Ativos', fmt(vt.locaisAtivos)],
                ['Tráfego Médio/Hora', fmt(vt.trafegoMedioHora)]
            ]);
        }
        if (dados.disponibilidade) {
            html += secao('Disponibilidade', [
                ['Equipamentos Ativos', fmt(dados.disponibilidade.equipamentosAtivos)],
                ['Total', fmt(dados.disponibilidade.totalEquipamentos)]
            ]);
        }
        if (dados.resumoKpis && dados.resumoKpis.length) {
            var kpiRows = dados.resumoKpis.map(function(k) {
                return [k.nome, 'Média: ' + fmt(k.media) + ' ' + (k.unidade||'') +
                    ' | OK: ' + k.okCount + ' | Warn: ' + k.warnCount + ' | Crit: ' + k.critCount];
            });
            html += secao('Resumo de KPIs', kpiRows);
        }
        if (dados.alertasAnaliticos && dados.alertasAnaliticos.length) {
            var aRows = dados.alertasAnaliticos.map(function(a) { return [a.data, a.descricao]; });
            html += secao('Alertas Analíticos (' + dados.alertasAnaliticos.length + ')', aRows);
        }
        if (dados.comparacaoPeriodo && dados.comparacaoPeriodo.kpis) {
            var cmp = dados.comparacaoPeriodo;
            var cmpRows = cmp.kpis.map(function(k) {
                var variacao = k.mediaAnterior === 0 ? '—' :
                    ((k.mediaAtual - k.mediaAnterior) / k.mediaAnterior * 100).toFixed(1) + '%';
                return [k.nome, 'Atual: ' + fmt(k.mediaAtual) + ' | Anterior: ' + fmt(k.mediaAnterior) + ' | Var: ' + variacao];
            });
            html += secao('Comparação com Período Anterior', cmpRows);
        }
    }
    return html;
}

function secao(titulo, linhas) {
    var html = '<h6 class="mt-3 mb-2">' + escaparHtml(titulo) + '</h6><table class="table table-sm table-bordered"><tbody>';
    for (var i = 0; i < linhas.length; i++) {
        html += '<tr><td class="fw-semibold" style="width:40%">' + escaparHtml(linhas[i][0]) + '</td><td>' + escaparHtml(linhas[i][1]) + '</td></tr>';
    }
    return html + '</tbody></table>';
}

function fmt(v) {
    if (v == null || v === undefined) return '—';
    if (typeof v === 'number') return v.toLocaleString('pt-BR');
    return String(v);
}

document.getElementById('filtroTipo').addEventListener('change', carregar);
carregar();
</script>

<%@ include file="/muralha-digital/utils/credenciais/rodape_bootstrap_simples.jsp" %>
