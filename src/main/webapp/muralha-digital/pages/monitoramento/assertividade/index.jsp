<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2 flex-wrap">
    <h4 class="mb-0"><i class="bi bi-bar-chart-line me-2"></i>Assertividade da Dupla Análise</h4>
    <div class="ms-auto d-flex gap-2 align-items-center flex-wrap">
      <select id="selPeriodo" class="form-select form-select-sm" style="width:140px">
        <option value="0">Hoje</option>
        <option value="7" selected>7 dias</option>
        <option value="30">30 dias</option>
        <option value="-1">Customizado</option>
      </select>
      <span id="datesCustom" class="d-none d-flex gap-1">
        <input type="date" id="dtInicio" class="form-control form-control-sm">
        <input type="date" id="dtFim" class="form-control form-control-sm">
      </span>
      <button class="btn btn-sm btn-primary" onclick="carregar()">
        <i class="bi bi-arrow-clockwise"></i>
      </button>
      <button class="btn btn-sm btn-outline-secondary" onclick="exportarCSV()">
        <i class="bi bi-download me-1"></i>CSV
      </button>
    </div>
  </div>

  <div class="row g-3 mb-4">
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted small">Taxa de Concordância</h6>
          <h2 id="lblConcordancia" class="fw-bold text-success">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted small">Total Pré-aprovadas</h6>
          <h2 id="lblPreAprovada" class="fw-bold">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted small">Desempates</h6>
          <h2 id="lblDesempate" class="fw-bold text-warning">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted small">Reprovadas</h6>
          <h2 id="lblReprovada" class="fw-bold text-danger">—</h2>
        </div>
      </div>
    </div>
  </div>

  <div class="row g-3 mb-4">
    <div class="col-md-4">
      <div class="card h-100">
        <div class="card-header">Distribuição por status</div>
        <div class="card-body d-flex align-items-center justify-content-center">
          <canvas id="chartDoughnut" width="260" height="260"></canvas>
        </div>
      </div>
    </div>
    <div class="col-md-8">
      <div class="card h-100">
        <div class="card-header">Evolução diária da taxa de concordância (%)</div>
        <div class="card-body"><canvas id="chartEvolucao" height="100"></canvas></div>
      </div>
    </div>
  </div>

  <div class="card">
    <div class="card-header">Ranking de analistas</div>
    <div class="card-body p-0">
      <table class="table table-sm table-striped mb-0" id="tblRanking">
        <thead>
          <tr>
            <th>#</th><th>Nome</th>
            <th>Total analisado</th><th>Concordantes</th><th>Taxa (%)</th>
          </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
<script>
let chartD, chartE, dadosGlobais;

document.getElementById('selPeriodo').addEventListener('change', function() {
  document.getElementById('datesCustom').classList.toggle('d-none', this.value !== '-1');
  if (this.value !== '-1') carregar();
});

function periodoParams() {
  const p = document.getElementById('selPeriodo').value;
  if (p === '-1') {
    return '&dtInicio=' + document.getElementById('dtInicio').value +
           '&dtFim='    + document.getElementById('dtFim').value;
  }
  const hoje = new Date();
  const fim  = hoje.toISOString().substring(0, 10);
  const ini  = new Date(hoje.getTime() - (parseInt(p) || 0) * 864e5).toISOString().substring(0, 10);
  return '&dtInicio=' + ini + '&dtFim=' + fim;
}

async function carregar() {
  const r = await fetch('<%= request.getContextPath() %>/MuralhaDigital/Assertividade?' + periodoParams().substring(1));
  const d = await r.json();
  if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }
  dadosGlobais = d;

  document.getElementById('lblConcordancia').textContent = (d.taxaConcordancia || 0).toFixed(1) + '%';
  document.getElementById('lblPreAprovada').textContent  = d.totais.PRE_APROVADA   || 0;
  document.getElementById('lblDesempate').textContent    = d.totais.DESEMPATE       || 0;
  document.getElementById('lblReprovada').textContent    = d.totais.REPROVADA       || 0;

  if (chartD) chartD.destroy();
  chartD = new Chart(document.getElementById('chartDoughnut'), {
    type: 'doughnut',
    data: {
      labels: ['Pré-aprovada', 'Desempate', 'Reprovada'],
      datasets: [{
        data: [
          d.totais.PRE_APROVADA || 0,
          d.totais.DESEMPATE || 0,
          d.totais.REPROVADA || 0
        ],
        backgroundColor: ['#198754', '#ffc107', '#dc3545']
      }]
    },
    options: { plugins: { legend: { position: 'bottom' } } }
  });

  if (chartE) chartE.destroy();
  chartE = new Chart(document.getElementById('chartEvolucao'), {
    type: 'line',
    data: {
      labels: d.evolucao.map(function(e) { return e.dia; }),
      datasets: [{
        label: 'Concordância (%)',
        data: d.evolucao.map(function(e) { return e.taxa.toFixed(1); }),
        borderColor: '#0d75bf',
        tension: 0.3,
        fill: false
      }]
    },
    options: { scales: { y: { beginAtZero: true, max: 100 } } }
  });

  var tbody = document.querySelector('#tblRanking tbody');
  tbody.innerHTML = '';
  d.ranking.forEach(function(row, i) {
    tbody.insertAdjacentHTML('beforeend',
      '<tr>' +
        '<td>' + (i + 1) + '</td>' +
        '<td>' + row.nome + '</td>' +
        '<td>' + row.total + '</td>' +
        '<td>' + row.concordantes + '</td>' +
        '<td>' + row.taxa.toFixed(1) + '</td>' +
      '</tr>');
  });
}

function exportarCSV() {
  if (!dadosGlobais) return;
  var csv = 'Nome,Total,Concordantes,Taxa\n';
  dadosGlobais.ranking.forEach(function(r) {
    csv += r.nome + ',' + r.total + ',' + r.concordantes + ',' + r.taxa.toFixed(1) + '\n';
  });
  var a = document.createElement('a');
  a.href = 'data:text/csv;charset=utf-8,' + encodeURIComponent(csv);
  a.download = 'assertividade.csv';
  a.click();
}

carregar();
</script>
