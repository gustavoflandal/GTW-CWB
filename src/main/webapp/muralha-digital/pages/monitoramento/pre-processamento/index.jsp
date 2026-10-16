<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2 flex-wrap">
    <h4 class="mb-0"><i class="bi bi-cpu me-2"></i>Painel de Pré-Processamento</h4>
    <div class="ms-auto d-flex gap-2 align-items-center flex-wrap">
      <select id="selPeriodo" class="form-select form-select-sm" style="width:140px">
        <option value="7">7 dias</option>
        <option value="30" selected>30 dias</option>
        <option value="90">90 dias</option>
        <option value="-1">Customizado</option>
      </select>
      <span id="datesCustom" class="d-none d-flex gap-1">
        <input type="date" id="dtInicio" class="form-control form-control-sm">
        <input type="date" id="dtFim" class="form-control form-control-sm">
      </span>
      <div class="form-check form-switch ms-2">
        <input class="form-check-input" type="checkbox" id="chkAuto" checked>
        <label class="form-check-label small" for="chkAuto">Auto <span id="lblCd">60s</span></label>
      </div>
      <button class="btn btn-sm btn-primary" onclick="carregar()">
        <i class="bi bi-arrow-clockwise"></i>
      </button>
    </div>
  </div>

  <!-- Volumes (a-e) -->
  <div class="row g-3 mb-3">
    <div class="col-xl col-md-4">
      <div class="card text-center">
        <div class="card-body py-2">
          <small class="text-muted">Recebidos (a)</small>
          <h3 id="lblRecebidos" class="fw-bold mb-0">—</h3>
        </div>
      </div>
    </div>
    <div class="col-xl col-md-4">
      <div class="card text-center">
        <div class="card-body py-2">
          <small class="text-muted">Processados (b)</small>
          <h3 id="lblProcessados" class="fw-bold text-primary mb-0">—</h3>
        </div>
      </div>
    </div>
    <div class="col-xl col-md-4">
      <div class="card text-center">
        <div class="card-body py-2">
          <small class="text-muted">Disponibilizados (c)</small>
          <h3 id="lblDisponibilizados" class="fw-bold text-success mb-0">—</h3>
        </div>
      </div>
    </div>
    <div class="col-xl col-md-4">
      <div class="card text-center">
        <div class="card-body py-2">
          <small class="text-muted">Descartados (d)</small>
          <h3 id="lblDescartados" class="fw-bold text-danger mb-0">—</h3>
        </div>
      </div>
    </div>
    <div class="col-xl col-md-4">
      <div class="card text-center">
        <div class="card-body py-2">
          <small class="text-muted">Aproveitamento (e)</small>
          <h3 id="lblAproveitamento" class="fw-bold text-info mb-0">—</h3>
        </div>
      </div>
    </div>
  </div>

  <!-- SLA, Pendentes, Backlog (j-l) -->
  <div class="row g-3 mb-3">
    <div class="col-md-3">
      <div class="card text-center" id="cardSla">
        <div class="card-body py-2">
          <small class="text-muted">Cumprimento SLA (l)</small>
          <h3 id="lblSla" class="fw-bold text-success mb-0">—</h3>
          <small id="lblViolacoes" class="text-muted">— violações de — janelas</small>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body py-2">
          <small class="text-muted">Pendentes (j)</small>
          <h3 id="lblPendentes" class="fw-bold text-warning mb-0">—</h3>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body py-2">
          <small class="text-muted">Backlog > 72h (k)</small>
          <h3 id="lblBacklog" class="fw-bold text-danger mb-0">—</h3>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body py-2">
          <small class="text-muted">Produtividade média (g)</small>
          <h3 id="lblMediaOp" class="fw-bold mb-0">—</h3>
          <small id="lblOperadores" class="text-muted">— operadores ativos</small>
        </div>
      </div>
    </div>
  </div>

  <!-- Gráficos -->
  <div class="row g-3 mb-4">
    <div class="col-md-8">
      <div class="card h-100">
        <div class="card-header">Evolução diária: recebidos × processados</div>
        <div class="card-body"><canvas id="chartEvolucao" height="100"></canvas></div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card h-100">
        <div class="card-header">Classificação (n)</div>
        <div class="card-body d-flex align-items-center justify-content-center">
          <canvas id="chartClassificacao" width="260" height="260"></canvas>
        </div>
      </div>
    </div>
  </div>

  <!-- Produtividade por operador (g) -->
  <div class="card mb-4">
    <div class="card-header">Produtividade por operador (g)</div>
    <div class="card-body p-0">
      <table class="table table-sm table-striped mb-0" id="tblOperadores">
        <thead>
          <tr><th>#</th><th>Operador</th><th>Registros analisados</th></tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
<script>
var chartEv, chartCl;
var cdTimer = null, cdSeg = 60, INTERVALO = 60;

document.getElementById('selPeriodo').addEventListener('change', function() {
  document.getElementById('datesCustom').classList.toggle('d-none', this.value !== '-1');
  if (this.value !== '-1') carregar();
});

document.getElementById('chkAuto').addEventListener('change', function() {
  if (this.checked) iniciarAuto();
  else pararAuto();
});

function iniciarAuto() {
  pararAuto();
  cdSeg = INTERVALO;
  document.getElementById('lblCd').textContent = cdSeg + 's';
  cdTimer = setInterval(function() {
    cdSeg--;
    document.getElementById('lblCd').textContent = cdSeg + 's';
    if (cdSeg <= 0) { carregar(); cdSeg = INTERVALO; }
  }, 1000);
}

function pararAuto() {
  if (cdTimer) { clearInterval(cdTimer); cdTimer = null; }
  document.getElementById('lblCd').textContent = '—';
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

function fmt(n) { return (n || 0).toLocaleString('pt-BR'); }

var COR_STATUS = {
  'PRE_APROVADA': '#198754', 'APROVADA': '#20c997', 'DESEMPATE': '#ffc107',
  'REPROVADA': '#dc3545', 'DESCARTADA': '#6f42c1', 'PRIMEIRA_ANALISE': '#6c757d'
};

async function carregar() {
  try {
    var r = await fetch('<%= request.getContextPath() %>/MuralhaDigital/PreProcessamento?acao=metricas&' + periodoParams());
    var d = await r.json();
    if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }

    // Volumes
    var v = d.volumes || {};
    document.getElementById('lblRecebidos').textContent       = fmt(v.recebidos);
    document.getElementById('lblProcessados').textContent     = fmt(v.processados);
    document.getElementById('lblDisponibilizados').textContent = fmt(v.disponibilizados);
    document.getElementById('lblDescartados').textContent     = fmt(v.descartados);
    document.getElementById('lblAproveitamento').textContent  = (v.aproveitamento || 0).toFixed(1) + '%';

    // SLA
    var s = d.sla || {};
    document.getElementById('lblSla').textContent = (s.cumprimento || 100).toFixed(1) + '%';
    document.getElementById('lblViolacoes').textContent = fmt(s.violacoes) + ' violações de ' + fmt(s.janelas) + ' janelas';
    var cardSla = document.getElementById('cardSla');
    if (s.cumprimento < 95) {
      cardSla.classList.add('border-danger', 'border-2');
      document.getElementById('lblSla').classList.remove('text-success');
      document.getElementById('lblSla').classList.add('text-danger');
    } else {
      cardSla.classList.remove('border-danger', 'border-2');
      document.getElementById('lblSla').classList.remove('text-danger');
      document.getElementById('lblSla').classList.add('text-success');
    }

    // Pendentes
    var pen = d.pendentes || {};
    document.getElementById('lblPendentes').textContent = fmt(pen.pendentes);
    document.getElementById('lblBacklog').textContent   = fmt(pen.backlog72h);

    // Produtividade
    var pr = d.produtividade || {};
    document.getElementById('lblMediaOp').textContent   = fmt(pr.mediaPorOperador) + '/op';
    document.getElementById('lblOperadores').textContent = fmt(pr.qtdOperadores) + ' operadores ativos';
    var tbody = document.querySelector('#tblOperadores tbody');
    tbody.innerHTML = '';
    (pr.operadores || []).forEach(function(op, i) {
      tbody.insertAdjacentHTML('beforeend',
        '<tr><td>' + (i+1) + '</td><td>' + op.nome + '</td><td>' + fmt(op.total) + '</td></tr>');
    });

    // Evolução diária
    if (chartEv) chartEv.destroy();
    var ev = d.evolucaoDiaria || [];
    chartEv = new Chart(document.getElementById('chartEvolucao'), {
      type: 'bar',
      data: {
        labels: ev.map(function(e) { return e.dia; }),
        datasets: [
          { label: 'Recebidos', data: ev.map(function(e) { return e.recebidos; }),
            backgroundColor: 'rgba(13,117,191,0.5)', borderColor: '#0d75bf', borderWidth: 1 },
          { label: 'Processados', data: ev.map(function(e) { return e.processados; }),
            backgroundColor: 'rgba(25,135,84,0.5)', borderColor: '#198754', borderWidth: 1 }
        ]
      },
      options: { scales: { y: { beginAtZero: true } }, plugins: { legend: { position: 'top' } } }
    });

    // Classificação (doughnut)
    if (chartCl) chartCl.destroy();
    var cl = d.classificacao || {};
    var labels = Object.keys(cl);
    var vals   = labels.map(function(k) { return cl[k]; });
    var cores  = labels.map(function(k) { return COR_STATUS[k] || '#adb5bd'; });
    chartCl = new Chart(document.getElementById('chartClassificacao'), {
      type: 'doughnut',
      data: { labels: labels, datasets: [{ data: vals, backgroundColor: cores }] },
      options: { plugins: { legend: { position: 'bottom' } } }
    });

    if (document.getElementById('chkAuto').checked) cdSeg = INTERVALO;
  } catch (e) {
    Swal.fire('Erro', 'Falha ao carregar dados: ' + e.message, 'error');
  }
}

carregar();
iniciarAuto();
</script>
