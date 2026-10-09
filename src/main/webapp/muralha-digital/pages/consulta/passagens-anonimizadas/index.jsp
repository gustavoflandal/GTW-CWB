<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2 flex-wrap">
    <h4 class="mb-0"><i class="bi bi-eye-slash me-2"></i>Consulta de Passagens (LGPD)</h4>
    <span id="badgeAnonimizado" class="badge bg-success">Anonimizado</span>
  </div>

  <div class="card mb-3">
    <div class="card-body">
      <div class="row g-2 align-items-end">
        <div class="col-md-2">
          <label for="dtInicio" class="form-label small">Data início</label>
          <input type="date" id="dtInicio" class="form-control form-control-sm">
        </div>
        <div class="col-md-2">
          <label for="dtFim" class="form-label small">Data fim</label>
          <input type="date" id="dtFim" class="form-control form-control-sm">
        </div>
        <div class="col-md-2">
          <label for="idLocal" class="form-label small">ID Local</label>
          <input type="number" id="idLocal" class="form-control form-control-sm" placeholder="Todos">
        </div>
        <div class="col-md-2">
          <label for="selAnonimizar" class="form-label small">Anonimização</label>
          <select id="selAnonimizar" class="form-select form-select-sm">
            <option value="">Padrão (config)</option>
            <option value="1">Ativada</option>
            <option value="0">Desativada</option>
          </select>
        </div>
        <div class="col-auto">
          <button class="btn btn-sm btn-primary" onclick="consultar()">
            <i class="bi bi-search me-1"></i>Consultar
          </button>
        </div>
      </div>
    </div>
  </div>

  <div class="card">
    <div class="card-header d-flex justify-content-between align-items-center">
      <span>Resultado</span>
      <span id="lblTotal" class="badge bg-secondary">0 registros</span>
    </div>
    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table table-sm table-striped mb-0" id="tblPassagens">
          <thead>
            <tr>
              <th>Placa</th>
              <th>ID Local</th>
              <th>Pista</th>
              <th>Data Captura</th>
              <th>Data Recepção</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody></tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<script>
var ctx = '<%= request.getContextPath() %>';

async function consultar() {
  var params = new URLSearchParams();
  var v;
  v = document.getElementById('dtInicio').value;  if (v) params.set('dtInicio', v);
  v = document.getElementById('dtFim').value;      if (v) params.set('dtFim', v);
  v = document.getElementById('idLocal').value;    if (v) params.set('idLocal', v);
  v = document.getElementById('selAnonimizar').value; if (v) params.set('anonimizar', v);

  var r = await fetch(ctx + '/MuralhaDigital/ConsultaAnonimizada?' + params.toString());
  var d = await r.json();
  if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }

  var badge = document.getElementById('badgeAnonimizado');
  if (d.anonimizado) {
    badge.className = 'badge bg-success';
    badge.textContent = 'Anonimizado';
  } else {
    badge.className = 'badge bg-warning text-dark';
    badge.textContent = 'Dados completos';
  }

  document.getElementById('lblTotal').textContent = d.total + ' registros';

  var tbody = document.querySelector('#tblPassagens tbody');
  tbody.innerHTML = '';
  (d.rows || []).forEach(function(r) {
    var statusBadge;
    switch (r.status) {
      case 'PRE_APROVADA':        statusBadge = '<span class="badge bg-success">Pré-aprovada</span>'; break;
      case 'PRIMEIRA_ANALISE':    statusBadge = '<span class="badge bg-info">1ª Análise</span>'; break;
      case 'REPROVADA':           statusBadge = '<span class="badge bg-danger">Reprovada</span>'; break;
      case 'AGUARDANDO_ANALISE':  statusBadge = '<span class="badge bg-secondary">Aguardando</span>'; break;
      default:                    statusBadge = '<span class="badge bg-secondary">' + (r.status || '—') + '</span>';
    }
    tbody.insertAdjacentHTML('beforeend',
      '<tr>' +
        '<td><code>' + (r.placa || '—') + '</code></td>' +
        '<td>' + r.idLocal + '</td>' +
        '<td>' + r.idPista + '</td>' +
        '<td class="small">' + (r.dtCaptura || '') + '</td>' +
        '<td class="small">' + (r.dtRecepcao || '') + '</td>' +
        '<td>' + statusBadge + '</td>' +
      '</tr>');
  });
}
</script>
