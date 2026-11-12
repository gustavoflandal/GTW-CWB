<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<style>
    .table td, .table th { font-size: .85rem; vertical-align: middle; }
</style>

<div class="container-fluid py-3">
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h4 class="mb-0"><i class="bi bi-hourglass-split me-2"></i>Retenção e Expurgo de Logs</h4>
        <div class="d-flex gap-1 flex-wrap">
            <button class="btn btn-success btn-sm" onclick="novaPolitica()"><i class="bi bi-plus-lg"></i> Nova política</button>
            <button class="btn btn-outline-primary btn-sm" onclick="gerarSelo()" title="Consolida o selo de integridade dos dias fechados"><i class="bi bi-shield-check"></i> Gerar selo agora</button>
            <button class="btn btn-outline-secondary btn-sm" onclick="simular()"><i class="bi bi-calculator"></i> Simular expurgo</button>
            <button class="btn btn-outline-danger btn-sm" onclick="executar()"><i class="bi bi-trash3"></i> Executar expurgo</button>
        </div>
    </div>

    <div class="alert alert-warning py-2 small">
        O expurgo só remove eventos <b>já selados</b> (dias fechados), preservando a cadeia de integridade dos registros recentes.
        Políticas com <b>retenção legal</b> bloqueiam o expurgo. Quando <b>Arquivar antes</b> está ligado, os registros são exportados
        (CSV + arquivo <code>.selo</code>) antes de serem apagados.
        Diretório de arquivamento: <code id="dirArquivo">-</code>
    </div>

    <h6 class="mt-2"><i class="bi bi-list-check me-1"></i>Políticas de retenção</h6>
    <div class="table-responsive mb-4">
        <table class="table table-sm table-hover table-bordered">
            <thead style="background-color:#0d75bf; color:#fff;">
                <tr><th>Nível</th><th>Dias de retenção</th><th>Arquivar antes</th><th>Retenção legal</th><th>Ativa</th><th>Descrição</th><th>Ações</th></tr>
            </thead>
            <tbody id="tbodyPol"><tr><td colspan="7" class="text-center text-muted">Carregando...</td></tr></tbody>
        </table>
    </div>

    <h6 class="mt-2"><i class="bi bi-clock-history me-1"></i>Histórico de expurgos</h6>
    <div class="table-responsive">
        <table class="table table-sm table-hover table-bordered">
            <thead style="background-color:#0d75bf; color:#fff;">
                <tr><th>Data/Hora</th><th>Nível</th><th>Removidos</th><th>Arquivo</th><th>Usuário</th><th>Observação</th></tr>
            </thead>
            <tbody id="tbodyExp"><tr><td colspan="6" class="text-center text-muted">—</td></tr></tbody>
        </table>
    </div>
</div>

<!-- Modal -->
<div class="modal fade" id="modalPol" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header" style="background-color:#0d75bf; color:#fff;">
        <h5 class="modal-title" id="modalTituloPol">Política de retenção</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <input type="hidden" id="pId">
        <div class="row g-2">
          <div class="col-md-6"><label class="form-label form-label-sm">Nível (vazio = padrão)</label>
            <select id="pNivel" class="form-select form-select-sm">
              <option value="">(padrão — demais níveis)</option>
              <option>LEITURA</option><option>ALTERACAO</option><option>ACESSO</option>
              <option>ERRO</option><option>EXPORTACAO</option><option>SISTEMA</option>
            </select></div>
          <div class="col-md-6"><label class="form-label form-label-sm">Dias de retenção</label>
            <input id="pDias" type="number" class="form-control form-control-sm" value="730"></div>
          <div class="col-12"><label class="form-label form-label-sm">Descrição</label>
            <input id="pDescricao" class="form-control form-control-sm"></div>
          <div class="col-12 d-flex gap-3 pt-2">
            <div class="form-check"><input class="form-check-input" type="checkbox" id="pArquivar" checked>
              <label class="form-check-label" for="pArquivar">Arquivar antes de apagar</label></div>
            <div class="form-check"><input class="form-check-input" type="checkbox" id="pHold">
              <label class="form-check-label" for="pHold">Retenção legal (bloqueia expurgo)</label></div>
            <div class="form-check"><input class="form-check-input" type="checkbox" id="pAtivo" checked>
              <label class="form-check-label" for="pAtivo">Ativa</label></div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>
        <button class="btn btn-primary btn-sm" onclick="salvarPolitica()"><i class="bi bi-save"></i> Salvar</button>
      </div>
    </div>
  </div>
</div>

<script src="/muralha-digital/assets/js/admin/logs-retencao.js?v=1"></script>
