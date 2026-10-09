<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2">
    <h4 class="mb-0"><i class="bi bi-archive me-2"></i>Política de Retenção de Dados</h4>
    <button class="btn btn-sm btn-primary ms-auto" onclick="carregar()">
      <i class="bi bi-arrow-clockwise"></i>
    </button>
  </div>

  <div class="row g-3 mb-4">
    <div class="col-md-6">
      <div class="card">
        <div class="card-body text-center">
          <h6 class="text-muted small">Retenção atual</h6>
          <h2 id="lblAnos" class="fw-bold">—</h2>
          <small class="text-muted">anos</small>
        </div>
      </div>
    </div>
    <div class="col-md-6">
      <div class="card">
        <div class="card-body text-center">
          <h6 class="text-muted small">Estimativa para próximo expurgo</h6>
          <h2 id="lblEstimativa" class="fw-bold text-warning">—</h2>
          <small class="text-muted">registros elegíveis</small>
        </div>
      </div>
    </div>
  </div>

  <div class="card mb-3">
    <div class="card-header">Configurar retenção</div>
    <div class="card-body d-flex gap-2 align-items-end">
      <div>
        <label class="form-label small">Período (anos)</label>
        <input type="number" id="inputAnos" class="form-control form-control-sm" min="1" max="20" style="width:100px">
      </div>
      <button class="btn btn-sm btn-primary" onclick="salvarConfig()">
        <i class="bi bi-check-lg me-1"></i>Salvar
      </button>
    </div>
  </div>

  <div class="card">
    <div class="card-header">Histórico de execuções do Job</div>
    <div class="card-body p-0">
      <table class="table table-sm table-striped mb-0" id="tblLogs">
        <thead>
          <tr><th>Data/Hora</th><th>Registros</th><th>Retenção</th><th>Data corte</th><th>Status</th><th>Mensagem</th></tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  </div>
</div>

<script>
var ctx = '<%= request.getContextPath() %>';

async function carregar() {
  var r = await fetch(ctx + '/MuralhaDigital/Retencao');
  var d = await r.json();
  if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }

  document.getElementById('lblAnos').textContent = d.retencaoAnos;
  document.getElementById('lblEstimativa').textContent = d.estimativaExpurgo || 0;
  document.getElementById('inputAnos').value = d.retencaoAnos;

  var tbody = document.querySelector('#tblLogs tbody');
  tbody.innerHTML = '';
  (d.logs || []).forEach(function(l) {
    var cls = l.status === 'ERRO' ? 'table-danger' : '';
    var badge = l.status === 'SUCESSO'
      ? '<span class="badge bg-success">Sucesso</span>'
      : '<span class="badge bg-danger">Erro</span>';
    tbody.insertAdjacentHTML('beforeend',
      '<tr class="' + cls + '">' +
        '<td class="small">' + (l.dt || '') + '</td>' +
        '<td>' + l.movidos + '</td>' +
        '<td>' + l.anos + ' anos</td>' +
        '<td>' + (l.corte || '') + '</td>' +
        '<td>' + badge + '</td>' +
        '<td class="small">' + (l.msg || '') + '</td>' +
      '</tr>');
  });
}

async function salvarConfig() {
  var anos = document.getElementById('inputAnos').value;
  var r = await fetch(ctx + '/MuralhaDigital/Retencao', {
    method: 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: 'anos=' + anos
  });
  var d = await r.json();
  if (d.ok) {
    Swal.fire({icon:'success', title:'Configuração salva', timer:1500, showConfirmButton:false});
    carregar();
  } else {
    Swal.fire('Erro', d.erro, 'error');
  }
}

carregar();
</script>
