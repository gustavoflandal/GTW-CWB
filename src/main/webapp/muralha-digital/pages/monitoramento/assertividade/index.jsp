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
      <div class="form-check form-switch ms-2">
        <input class="form-check-input" type="checkbox" id="chkAutoRefresh" checked>
        <label class="form-check-label small" for="chkAutoRefresh">Auto <span id="lblCountdown">30s</span></label>
      </div>
      <button class="btn btn-sm btn-primary" onclick="carregar()">
        <i class="bi bi-arrow-clockwise"></i>
      </button>
      <button class="btn btn-sm btn-outline-secondary" onclick="exportarCSV()">
        <i class="bi bi-download me-1"></i>CSV
      </button>
    </div>
  </div>

  <!-- Alerta de aderência -->
  <div id="alertaAderencia" class="alert alert-danger d-flex align-items-center mb-3 d-none" role="alert">
    <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
    <div><strong>Aderência abaixo de 80%!</strong> A taxa de concordância está abaixo do mínimo exigido pelo TR §5.2.2.3.</div>
  </div>

  <!-- Cards principais -->
  <div class="row g-3 mb-3">
    <div class="col-xl-3 col-md-6">
      <div class="card text-center" id="cardConcordancia">
        <div class="card-body">
          <h6 class="text-muted small">Taxa de Concordância</h6>
          <h2 id="lblConcordancia" class="fw-bold">—</h2>
          <small class="text-muted">Mínimo exigido: 80%</small>
        </div>
      </div>
    </div>
    <div class="col-xl-3 col-md-6">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted small">Volume Processado</h6>
          <h2 id="lblProcessado" class="fw-bold text-primary">—</h2>
          <small id="lblRecebidos" class="text-muted">de — recebidos</small>
        </div>
      </div>
    </div>
    <div class="col-xl-3 col-md-6">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted small">Divergências</h6>
          <h2 id="lblDivergencias" class="fw-bold text-warning">—</h2>
          <small class="text-muted">Desempates identificados</small>
        </div>
      </div>
    </div>
    <div class="col-xl-3 col-md-6">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted small">Revisão Manual</h6>
          <h2 id="lblRevisao" class="fw-bold text-info">—</h2>
          <small class="text-muted">Aguardando operador</small>
        </div>
      </div>
    </div>
  </div>

  <!-- Cards secundários -->
  <div class="row g-3 mb-4">
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted small">Pré-aprovadas</h6>
          <h2 id="lblPreAprovada" class="fw-bold text-success">—</h2>
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
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted small">Primeira Análise</h6>
          <h2 id="lblPrimeira" class="fw-bold text-secondary">—</h2>
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
<script src="https://cdnjs.cloudflare.com/ajax/libs/chartjs-plugin-annotation/3.0.1/chartjs-plugin-annotation.min.js"></script>
<script>
var chartD, chartE, dadosGlobais;
var autoRefreshTimer = null;
var countdownTimer = null;
var countdownSeg = 30;
var REFRESH_INTERVALO = 30;

document.getElementById('selPeriodo').addEventListener('change', function() {
  document.getElementById('datesCustom').classList.toggle('d-none', this.value !== '-1');
  if (this.value !== '-1') carregar();
});

document.getElementById('chkAutoRefresh').addEventListener('change', function() {
  if (this.checked) iniciarAutoRefresh();
  else pararAutoRefresh();
});

function iniciarAutoRefresh() {
  pararAutoRefresh();
  countdownSeg = REFRESH_INTERVALO;
  document.getElementById('lblCountdown').textContent = countdownSeg + 's';
  countdownTimer = setInterval(function() {
    countdownSeg--;
    document.getElementById('lblCountdown').textContent = countdownSeg + 's';
    if (countdownSeg <= 0) {
      carregar();
      countdownSeg = REFRESH_INTERVALO;
    }
  }, 1000);
}

function pararAutoRefresh() {
  if (countdownTimer) { clearInterval(countdownTimer); countdownTimer = null; }
  document.getElementById('lblCountdown').textContent = '—';
}

function periodoParams() {
  var p = document.getElementById('selPeriodo').value;
  if (p === '-1') {
    return 'dtInicio=' + document.getElementById('dtInicio').value +
           '&dtFim='   + document.getElementById('dtFim').value;
  }
  var hoje = new Date();
  var fim  = hoje.toISOString().substring(0, 10);
  var ini  = new Date(hoje.getTime() - (parseInt(p) || 0) * 864e5).toISOString().substring(0, 10);
  return 'dtInicio=' + ini + '&dtFim=' + fim;
}

function formatNum(n) {
  return (n || 0).toLocaleString('pt-BR');
}

async function carregar() {
  try {
    var r = await fetch('<%= request.getContextPath() %>/MuralhaDigital/Assertividade?' + periodoParams());
    var d = await r.json();
    if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }
    dadosGlobais = d;

    var taxa = d.taxaConcordancia || 0;
    document.getElementById('lblConcordancia').textContent = taxa.toFixed(1) + '%';

    // Alerta de aderência < 80%
    var cardConc = document.getElementById('cardConcordancia');
    var alertaDiv = document.getElementById('alertaAderencia');
    if (d.alertaAbaixo80) {
      cardConc.classList.remove('border-success');
      cardConc.classList.add('border-danger', 'border-2');
      document.getElementById('lblConcordancia').classList.remove('text-success');
      document.getElementById('lblConcordancia').classList.add('text-danger');
      alertaDiv.classList.remove('d-none');
    } else {
      cardConc.classList.remove('border-danger', 'border-2');
      cardConc.classList.add('border-success');
      document.getElementById('lblConcordancia').classList.remove('text-danger');
      document.getElementById('lblConcordancia').classList.add('text-success');
      alertaDiv.classList.add('d-none');
    }

    // Volume processado
    var vol = d.volumeProcessado || {};
    document.getElementById('lblProcessado').textContent   = formatNum(vol.processados);
    document.getElementById('lblRecebidos').textContent    = 'de ' + formatNum(vol.recebidos) + ' recebidos';
    document.getElementById('lblDivergencias').textContent = formatNum(vol.divergencias);
    document.getElementById('lblRevisao').textContent      = formatNum(vol.revisaoManual);

    // Cards de status
    document.getElementById('lblPreAprovada').textContent = formatNum(d.totais.PRE_APROVADA);
    document.getElementById('lblDesempate').textContent   = formatNum(d.totais.DESEMPATE);
    document.getElementById('lblReprovada').textContent   = formatNum(d.totais.REPROVADA);
    document.getElementById('lblPrimeira').textContent    = formatNum(d.totais.PRIMEIRA_ANALISE);

    // Doughnut
    if (chartD) chartD.destroy();
    chartD = new Chart(document.getElementById('chartDoughnut'), {
      type: 'doughnut',
      data: {
        labels: ['Pré-aprovada', 'Desempate', 'Reprovada', '1ª Análise'],
        datasets: [{
          data: [
            d.totais.PRE_APROVADA || 0,
            d.totais.DESEMPATE || 0,
            d.totais.REPROVADA || 0,
            d.totais.PRIMEIRA_ANALISE || 0
          ],
          backgroundColor: ['#198754', '#ffc107', '#dc3545', '#6c757d']
        }]
      },
      options: { plugins: { legend: { position: 'bottom' } } }
    });

    // Evolução com linha de meta 80%
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
      options: {
        scales: { y: { beginAtZero: true, max: 100 } },
        plugins: {
          annotation: {
            annotations: {
              metaLine: {
                type: 'line',
                yMin: 80, yMax: 80,
                borderColor: '#dc3545',
                borderWidth: 2,
                borderDash: [6, 4],
                label: {
                  display: true,
                  content: 'Meta 80%',
                  position: 'start',
                  backgroundColor: '#dc3545',
                  color: '#fff',
                  font: { size: 11 }
                }
              }
            }
          }
        }
      }
    });

    // Ranking
    var tbody = document.querySelector('#tblRanking tbody');
    tbody.innerHTML = '';
    d.ranking.forEach(function(row, i) {
      tbody.insertAdjacentHTML('beforeend',
        '<tr>' +
          '<td>' + (i + 1) + '</td>' +
          '<td>' + row.nome + '</td>' +
          '<td>' + formatNum(row.total) + '</td>' +
          '<td>' + formatNum(row.concordantes) + '</td>' +
          '<td>' + row.taxa.toFixed(1) + '</td>' +
        '</tr>');
    });

    // Reiniciar contagem se auto-refresh ativo
    if (document.getElementById('chkAutoRefresh').checked) {
      countdownSeg = REFRESH_INTERVALO;
    }
  } catch (e) {
    Swal.fire('Erro', 'Falha ao carregar dados: ' + e.message, 'error');
  }
}

function exportarCSV() {
  if (!dadosGlobais) return;
  var csv = '﻿Nome;Total;Concordantes;Taxa\n';
  dadosGlobais.ranking.forEach(function(r) {
    csv += r.nome + ';' + r.total + ';' + r.concordantes + ';' + r.taxa.toFixed(1) + '\n';
  });
  var a = document.createElement('a');
  a.href = 'data:text/csv;charset=utf-8,' + encodeURIComponent(csv);
  a.download = 'assertividade.csv';
  a.click();
}

carregar();
iniciarAutoRefresh();
</script>
