<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2 flex-wrap">
    <h4 class="mb-0"><i class="bi bi-hourglass-split me-2"></i>SLA de Pré-processamento (72h)</h4>
    <button class="btn btn-sm btn-primary ms-auto" onclick="carregar()">
      <i class="bi bi-arrow-clockwise"></i>
    </button>
  </div>

  <div class="row g-3 mb-4">
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted small">Total Pendentes</h6>
          <h2 id="lblTotal" class="fw-bold">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center border-danger">
        <div class="card-body">
          <h6 class="text-muted small text-danger">Acima de 72h</h6>
          <h2 id="lblAcima72" class="fw-bold text-danger">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center border-warning">
        <div class="card-body">
          <h6 class="text-muted small">Entre 48h e 72h</h6>
          <h2 id="lblEntre4872" class="fw-bold text-warning">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted small">Entre 24h e 48h</h6>
          <h2 id="lblEntre2448" class="fw-bold text-info">—</h2>
        </div>
      </div>
    </div>
  </div>

  <div class="row g-3 mb-4">
    <div class="col-md-8">
      <div class="card h-100">
        <div class="card-header">Evolução do aging (snapshots horários)</div>
        <div class="card-body"><canvas id="chartHistorico" height="100"></canvas></div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card h-100">
        <div class="card-header">Distribuição atual</div>
        <div class="card-body d-flex align-items-center justify-content-center">
          <canvas id="chartDoughnut" width="260" height="260"></canvas>
        </div>
      </div>
    </div>
  </div>

  <div class="card">
    <div class="card-header">Fila pendente por local/equipamento</div>
    <div class="card-body p-0">
      <table class="table table-sm table-striped mb-0" id="tblFila">
        <thead>
          <tr>
            <th>ID Local</th>
            <th>Pendentes</th>
            <th>Aging máx. (h)</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
<script>
var chartH, chartD;

async function carregar() {
  var r = await fetch('<%= request.getContextPath() %>/MuralhaDigital/SlaPreprocessamento');
  var d = await r.json();
  if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }

  document.getElementById('lblTotal').textContent     = d.totalPendentes || 0;
  document.getElementById('lblAcima72').textContent    = d.acima72h || 0;
  document.getElementById('lblEntre4872').textContent  = d.entre4872h || 0;
  document.getElementById('lblEntre2448').textContent  = d.entre2448h || 0;

  if (chartD) chartD.destroy();
  chartD = new Chart(document.getElementById('chartDoughnut'), {
    type: 'doughnut',
    data: {
      labels: ['> 72h', '48-72h', '24-48h', '< 24h'],
      datasets: [{
        data: [d.acima72h || 0, d.entre4872h || 0, d.entre2448h || 0, d.ate24h || 0],
        backgroundColor: ['#dc3545', '#ffc107', '#0dcaf0', '#198754']
      }]
    },
    options: { plugins: { legend: { position: 'bottom' } } }
  });

  var tbody = document.querySelector('#tblFila tbody');
  tbody.innerHTML = '';
  (d.fila || []).forEach(function(row) {
    var cls = row.maxHoras > 72 ? 'table-danger' : row.maxHoras > 48 ? 'table-warning' : '';
    var badge = row.maxHoras > 72
      ? '<span class="badge bg-danger">Violado</span>'
      : row.maxHoras > 48
      ? '<span class="badge bg-warning text-dark">Atenção</span>'
      : '<span class="badge bg-success">OK</span>';
    tbody.insertAdjacentHTML('beforeend',
      '<tr class="' + cls + '">' +
        '<td>' + row.idLocal + '</td>' +
        '<td>' + row.total + '</td>' +
        '<td>' + row.maxHoras + 'h</td>' +
        '<td>' + badge + '</td>' +
      '</tr>');
  });

  carregarHistorico();
}

async function carregarHistorico() {
  var r = await fetch('<%= request.getContextPath() %>/MuralhaDigital/SlaPreprocessamento?acao=historico');
  var d = await r.json();
  if (!d.ok || !d.historico || d.historico.length === 0) return;

  var hist = d.historico.reverse();
  if (chartH) chartH.destroy();
  chartH = new Chart(document.getElementById('chartHistorico'), {
    type: 'line',
    data: {
      labels: hist.map(function(h) { return h.dt.substring(5, 16); }),
      datasets: [
        {
          label: '> 72h',
          data: hist.map(function(h) { return h.acima72h; }),
          borderColor: '#dc3545',
          backgroundColor: 'rgba(220,53,69,0.1)',
          fill: true, tension: 0.3
        },
        {
          label: '> 48h',
          data: hist.map(function(h) { return h.acima48h; }),
          borderColor: '#ffc107',
          fill: false, tension: 0.3
        },
        {
          label: '> 24h',
          data: hist.map(function(h) { return h.acima24h; }),
          borderColor: '#0dcaf0',
          fill: false, tension: 0.3
        }
      ]
    },
    options: {
      scales: { y: { beginAtZero: true } },
      plugins: { legend: { position: 'bottom' } }
    }
  });
}

carregar();
setInterval(carregar, 60000);
</script>
