<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2">
    <h4 class="mb-0"><i class="bi bi-gear me-2"></i>Configuração de KPIs</h4>
    <button class="btn btn-sm btn-primary ms-auto" onclick="abrirForm(null)">
      <i class="bi bi-plus me-1"></i>Novo KPI
    </button>
    <button class="btn btn-sm btn-outline-secondary" onclick="carregar()">
      <i class="bi bi-arrow-clockwise"></i>
    </button>
  </div>

  <div class="card">
    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table table-sm table-striped mb-0">
          <thead>
            <tr>
              <th>Ordem</th><th>Nome</th><th>Unidade</th>
              <th>Thresh. OK</th><th>Thresh. Warn</th><th>Ativo</th><th></th>
            </tr>
          </thead>
          <tbody id="tblKpis"></tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<div class="offcanvas offcanvas-end" style="width:500px" id="ocKpi" tabindex="-1">
  <div class="offcanvas-header">
    <h5 class="offcanvas-title" id="ocTitulo">KPI</h5>
    <button class="btn-close" data-bs-dismiss="offcanvas"></button>
  </div>
  <div class="offcanvas-body">
    <input type="hidden" id="kpiId">
    <div class="mb-2">
      <label class="form-label small">Nome</label>
      <input id="kpiNome" class="form-control form-control-sm">
    </div>
    <div class="mb-2">
      <label class="form-label small">Descrição</label>
      <input id="kpiDescricao" class="form-control form-control-sm">
    </div>
    <div class="mb-2">
      <label class="form-label small">SQL (deve retornar coluna "valor")</label>
      <textarea id="kpiSql" class="form-control form-control-sm font-monospace" rows="4"></textarea>
    </div>
    <div class="row mb-2">
      <div class="col">
        <label class="form-label small">Unidade</label>
        <input id="kpiUnidade" class="form-control form-control-sm">
      </div>
      <div class="col">
        <label class="form-label small">Ordem</label>
        <input type="number" id="kpiOrdem" class="form-control form-control-sm" value="0">
      </div>
    </div>
    <div class="row mb-2">
      <div class="col">
        <label class="form-label small">Threshold OK</label>
        <input type="number" step="any" id="kpiThOk" class="form-control form-control-sm">
      </div>
      <div class="col">
        <label class="form-label small">Threshold Warn</label>
        <input type="number" step="any" id="kpiThWarn" class="form-control form-control-sm">
      </div>
    </div>
    <div class="mb-3 form-check">
      <input type="checkbox" class="form-check-input" id="kpiAtivo" checked>
      <label class="form-check-label" for="kpiAtivo">Ativo</label>
    </div>
    <button class="btn btn-primary w-100" onclick="salvar()">Salvar</button>
  </div>
</div>

<script>
var ctx = '<%= request.getContextPath() %>';
var kpiData = [];

async function carregar() {
  var r = await fetch(ctx + '/MuralhaDigital/Kpi?acao=listarConfig');
  var d = await r.json();
  if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }

  kpiData = d.configs || [];
  var tbody = document.getElementById('tblKpis');
  tbody.innerHTML = '';
  kpiData.forEach(function(c, idx) {
    tbody.insertAdjacentHTML('beforeend',
      '<tr>' +
        '<td>' + c.ordem + '</td>' +
        '<td>' + c.nome + '</td>' +
        '<td>' + (c.unidade || '') + '</td>' +
        '<td>' + c.thOk + '</td>' +
        '<td>' + c.thWarn + '</td>' +
        '<td><span class="badge bg-' + (c.ativo ? 'success' : 'secondary') + '">' +
          (c.ativo ? 'Sim' : 'Não') + '</span></td>' +
        '<td><button class="btn btn-xs btn-outline-primary" onclick="abrirForm(' + idx + ')">' +
          '<i class="bi bi-pencil"></i></button></td>' +
      '</tr>');
  });
}

function abrirForm(idx) {
  var c = idx !== null ? kpiData[idx] : null;
  document.getElementById('ocTitulo').textContent = c ? 'Editar KPI' : 'Novo KPI';
  document.getElementById('kpiId').value = c ? c.id : '';
  document.getElementById('kpiNome').value = c ? c.nome : '';
  document.getElementById('kpiDescricao').value = c ? (c.descricao || '') : '';
  document.getElementById('kpiSql').value = c ? c.querySql : '';
  document.getElementById('kpiUnidade').value = c ? (c.unidade || '') : '';
  document.getElementById('kpiOrdem').value = c ? c.ordem : 0;
  document.getElementById('kpiThOk').value = c ? c.thOk : '';
  document.getElementById('kpiThWarn').value = c ? c.thWarn : '';
  document.getElementById('kpiAtivo').checked = c ? c.ativo : true;
  new bootstrap.Offcanvas(document.getElementById('ocKpi')).show();
}

async function salvar() {
  var body = new URLSearchParams({
    acao: 'salvar',
    id: document.getElementById('kpiId').value,
    nome: document.getElementById('kpiNome').value,
    descricao: document.getElementById('kpiDescricao').value,
    querySql: document.getElementById('kpiSql').value,
    unidade: document.getElementById('kpiUnidade').value,
    ordem: document.getElementById('kpiOrdem').value,
    thOk: document.getElementById('kpiThOk').value,
    thWarn: document.getElementById('kpiThWarn').value,
    ativo: document.getElementById('kpiAtivo').checked
  });
  var r = await fetch(ctx + '/MuralhaDigital/Kpi', { method: 'POST', body: body });
  var d = await r.json();
  if (d.ok) {
    bootstrap.Offcanvas.getInstance(document.getElementById('ocKpi')).hide();
    Swal.fire({icon: 'success', title: 'KPI salvo', timer: 1500, showConfirmButton: false});
    carregar();
  } else {
    Swal.fire('Erro', d.erro, 'error');
  }
}

carregar();
</script>
