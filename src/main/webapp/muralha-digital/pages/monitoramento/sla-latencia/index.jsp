<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <h4 class="mb-3"><i class="bi bi-speedometer2 me-2"></i>SLA de Latência — Equipamentos</h4>

  <div class="row g-3 mb-4">
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="card-title text-muted small">Latência Média (5 min)</h6>
          <h2 id="lblMedia" class="fw-bold">—</h2>
          <small class="text-muted">ms</small>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="card-title text-muted small">Latência Máxima (5 min)</h6>
          <h2 id="lblMaximo" class="fw-bold">—</h2>
          <small class="text-muted">ms</small>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="card-title text-muted small">Passagens (5 min)</h6>
          <h2 id="lblTotal" class="fw-bold">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center" id="cardStatus">
        <div class="card-body">
          <h6 class="card-title text-muted small">Status SLA</h6>
          <h2 id="lblStatus" class="fw-bold">—</h2>
        </div>
      </div>
    </div>
  </div>

  <div class="card mb-4">
    <div class="card-header fw-semibold">Histórico P95 (últimas 24h)</div>
    <div class="card-body"><canvas id="chartSla" height="80"></canvas></div>
  </div>

  <div class="card">
    <div class="card-header fw-semibold">Violações recentes</div>
    <div class="card-body p-0">
      <table class="table table-sm table-striped mb-0">
        <thead class="table-dark">
          <tr>
            <th>Data/Hora</th><th>P95 (ms)</th><th>Threshold</th>
            <th>Passagens</th><th>Top locais</th>
          </tr>
        </thead>
        <tbody id="tblViolacoes"></tbody>
      </table>
    </div>
  </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
<script>
var THRESHOLD = 4000;
var chart;

function carregarAtual() {
  fetch('/MuralhaDigital/SlaLatencia?acao=atual')
    .then(function(r) { return r.json(); })
    .then(function(d) {
      if (!d.ok) return;
      document.getElementById('lblMedia').textContent  = Math.round(d.media || 0);
      document.getElementById('lblMaximo').textContent = d.maximo || 0;
      document.getElementById('lblTotal').textContent  = d.total  || 0;
      var ok = (d.media || 0) <= THRESHOLD;
      document.getElementById('lblStatus').textContent = ok ? '✅ OK' : '⚠️ Violado';
      document.getElementById('cardStatus').className  =
        'card text-center border-' + (ok ? 'success' : 'danger');
    });
}

function carregarHistorico() {
  fetch('/MuralhaDigital/SlaLatencia?acao=historico')
    .then(function(r) { return r.json(); })
    .then(function(d) {
      if (!d.ok) return;
      var hist = d.historico.slice().reverse();

      var labels = hist.map(function(h) { return (h.dt || '').substring(11, 16); });
      var values = hist.map(function(h) { return h.p95; });
      if (chart) chart.destroy();
      chart = new Chart(document.getElementById('chartSla'), {
        type: 'line',
        data: {
          labels: labels,
          datasets: [{
            label: 'P95 (ms)', data: values,
            borderColor: '#0d75bf', tension: 0.3, fill: false
          }, {
            label: 'Threshold (' + THRESHOLD + 'ms)',
            data: hist.map(function() { return THRESHOLD; }),
            borderColor: '#dc3545', borderDash: [5,5], pointRadius: 0
          }]
        },
        options: { plugins: { legend: { position: 'top' } } }
      });

      var tbody = document.getElementById('tblViolacoes');
      tbody.innerHTML = '';
      d.historico.filter(function(h) { return h.violacao; }).slice(0, 20).forEach(function(h) {
        var top = '';
        try {
          top = JSON.parse(h.locaisTop || '[]')
            .map(function(e) { return 'Local ' + e.local + ': ' + e.avg + 'ms'; })
            .join(', ');
        } catch(e) {}
        tbody.insertAdjacentHTML('beforeend',
          '<tr class="table-danger">' +
          '<td>' + (h.dt || '') + '</td>' +
          '<td>' + h.p95 + '</td>' +
          '<td>' + h.threshold + '</td>' +
          '<td>' + h.total + '</td>' +
          '<td>' + top + '</td>' +
          '</tr>');
      });
    });
}

carregarAtual();
carregarHistorico();
setInterval(carregarAtual, 30000);
</script>

<%@ include file="/includes/rodape.jsp" %>
