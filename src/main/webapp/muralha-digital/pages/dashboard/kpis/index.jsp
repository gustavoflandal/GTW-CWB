<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<style>
  .kpi-widget {
    border: none; border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0,0,0,.08), 0 4px 12px rgba(0,0,0,.04);
    transition: box-shadow .2s, transform .2s;
    overflow: hidden;
  }
  .kpi-widget:hover {
    box-shadow: 0 4px 16px rgba(0,0,0,.12);
    transform: translateY(-2px);
  }

  /* ── Card ── */
  .kpi-card .kpi-accent { height: 4px; }
  .kpi-card .kpi-body { padding: 20px 16px 16px; text-align: center; }
  .kpi-card .kpi-titulo { font-size: .8rem; color: #6c757d; margin-bottom: 8px; font-weight: 500; }
  .kpi-card .kpi-valor { font-size: 2rem; font-weight: 700; line-height: 1.1; }
  .kpi-card .kpi-unidade { font-size: .75rem; color: #8d99a5; margin-top: 2px; }
  .kpi-card .kpi-status { margin-top: 10px; }
  .kpi-card .kpi-desc { font-size: .72rem; color: #9aa5b0; margin-top: 6px; }

  /* ── Compacto ── */
  .kpi-compacto { border-left: 4px solid; }
  .kpi-compacto .kpi-body { padding: 14px 16px; display: flex; align-items: center; gap: 12px; }
  .kpi-compacto .kpi-icone {
    width: 44px; height: 44px; border-radius: 10px;
    display: flex; align-items: center; justify-content: center;
    font-size: 1.2rem; color: #fff; flex-shrink: 0;
  }
  .kpi-compacto .kpi-info { flex: 1; min-width: 0; }
  .kpi-compacto .kpi-titulo { font-size: .75rem; color: #6c757d; margin-bottom: 2px; }
  .kpi-compacto .kpi-valor { font-size: 1.5rem; font-weight: 700; line-height: 1.1; }
  .kpi-compacto .kpi-unidade { font-size: .7rem; color: #8d99a5; margin-left: 4px; }
  .kpi-compacto .kpi-badge-col { text-align: right; flex-shrink: 0; }

  /* ── Medidor ── */
  .kpi-medidor .kpi-body { padding: 16px; text-align: center; }
  .kpi-medidor .kpi-titulo { font-size: .8rem; color: #6c757d; margin-bottom: 12px; font-weight: 500; }
  .kpi-medidor .gauge-wrap {
    position: relative; width: 160px; height: 88px;
    margin: 0 auto;
  }
  .kpi-medidor .gauge-wrap canvas { display: block; }
  .kpi-medidor .gauge-center {
    position: absolute; bottom: 0; left: 0; right: 0;
    text-align: center; line-height: 1;
  }
  .kpi-medidor .gauge-center .gv { font-size: 1.5rem; font-weight: 700; }
  .kpi-medidor .gauge-center .gu { font-size: .7rem; color: #8d99a5; }
  .kpi-medidor .kpi-rodape { margin-top: 12px; }

  /* ── Progresso ── */
  .kpi-progresso .kpi-body { padding: 16px; }
  .kpi-progresso .kpi-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
  .kpi-progresso .kpi-titulo { font-size: .85rem; font-weight: 600; margin: 0; }
  .kpi-progresso .progress { height: 24px; border-radius: 8px; background: #f0f2f5; }
  .kpi-progresso .progress-bar { border-radius: 8px; font-size: .78rem; font-weight: 600; line-height: 24px; }
  .kpi-progresso .kpi-desc { font-size: .72rem; color: #9aa5b0; margin-top: 6px; }

  /* ── Badge de status ── */
  .kpi-badge {
    display: inline-flex; align-items: center; gap: 4px;
    font-size: .7rem; font-weight: 600; padding: 3px 10px;
    border-radius: 20px; letter-spacing: .3px;
  }
  .kpi-badge .dot {
    width: 6px; height: 6px; border-radius: 50%;
    background: currentColor; opacity: .7;
  }
</style>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2">
    <h4 class="mb-0"><i class="bi bi-speedometer2 me-2"></i>Dashboard de KPIs</h4>
    <small class="text-muted" id="lblAtualizacao"></small>
    <a href="<%= request.getContextPath() %>/muralha-digital/pages/admin/kpis/index.jsp"
       class="btn btn-sm btn-outline-secondary ms-auto">
      <i class="bi bi-gear me-1"></i>Configurar
    </a>
    <button class="btn btn-sm btn-primary" onclick="carregar()">
      <i class="bi bi-arrow-clockwise"></i>
    </button>
  </div>
  <div class="row g-3" id="kpiCards"></div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
<script>
var ctx = '<%= request.getContextPath() %>';
var COR_STATUS = { OK: '#198754', WARN: '#e8a317', CRIT: '#dc3545', ERRO: '#6c757d' };
var ICONE_STATUS = { OK: 'bi-check-circle-fill', WARN: 'bi-exclamation-triangle-fill', CRIT: 'bi-x-circle-fill', ERRO: 'bi-question-circle-fill' };
var LABEL_STATUS = { OK: 'Normal', WARN: 'Atenção', CRIT: 'Crítico', ERRO: 'Erro' };
var gaugeCharts = [];

function limparGauges() {
  gaugeCharts.forEach(function(c) { c.destroy(); });
  gaugeCharts = [];
}

function hexDoKpi(kpi) {
  if (kpi.corPersonalizada) return kpi.corPersonalizada;
  return COR_STATUS[kpi.status] || COR_STATUS.ERRO;
}

function rgba(hex, a) {
  return 'rgba(' + parseInt(hex.slice(1,3),16) + ',' + parseInt(hex.slice(3,5),16) + ',' + parseInt(hex.slice(5,7),16) + ',' + a + ')';
}

function badge(kpi, hex) {
  return '<span class="kpi-badge" style="color:' + hex + ';background:' + rgba(hex, .1) + '">' +
    '<span class="dot"></span>' + (LABEL_STATUS[kpi.status] || '') + '</span>';
}

function fmtVal(kpi) {
  return kpi.valor != null ? Number(kpi.valor).toLocaleString('pt-BR') : '—';
}

/* ── Card ── */
function renderCard(kpi, hex) {
  var valor = fmtVal(kpi);
  return '<div class="col-xl-3 col-md-4 col-sm-6">' +
    '<div class="card kpi-widget kpi-card h-100">' +
      '<div class="kpi-accent" style="background:' + hex + '"></div>' +
      '<div class="kpi-body">' +
        '<div class="kpi-titulo">' + kpi.nome + '</div>' +
        '<div class="kpi-valor" style="color:' + hex + '">' + valor + '</div>' +
        '<div class="kpi-unidade">' + (kpi.unidade || '') + '</div>' +
        '<div class="kpi-status">' + badge(kpi, hex) + '</div>' +
        (kpi.descricao ? '<div class="kpi-desc">' + kpi.descricao + '</div>' : '') +
      '</div>' +
    '</div>' +
  '</div>';
}

/* ── Compacto ── */
function renderCompacto(kpi, hex) {
  var valor = fmtVal(kpi);
  return '<div class="col-xl-4 col-md-6">' +
    '<div class="card kpi-widget kpi-compacto h-100" style="border-left-color:' + hex + '">' +
      '<div class="kpi-body">' +
        '<div class="kpi-icone" style="background:' + hex + '">' +
          '<i class="bi ' + (ICONE_STATUS[kpi.status] || 'bi-bar-chart-fill') + '"></i>' +
        '</div>' +
        '<div class="kpi-info">' +
          '<div class="kpi-titulo">' + kpi.nome + '</div>' +
          '<div><span class="kpi-valor" style="color:' + hex + '">' + valor + '</span>' +
            '<span class="kpi-unidade">' + (kpi.unidade || '') + '</span></div>' +
        '</div>' +
        '<div class="kpi-badge-col">' + badge(kpi, hex) + '</div>' +
      '</div>' +
    '</div>' +
  '</div>';
}

/* ── Medidor ── */
function renderMedidor(kpi, hex) {
  var cid = 'gauge_' + kpi.id;
  var valor = fmtVal(kpi);
  return '<div class="col-xl-3 col-md-4 col-sm-6">' +
    '<div class="card kpi-widget kpi-medidor h-100">' +
      '<div class="kpi-body">' +
        '<div class="kpi-titulo">' + kpi.nome + '</div>' +
        '<div class="gauge-wrap">' +
          '<canvas id="' + cid + '" width="160" height="88"></canvas>' +
          '<div class="gauge-center">' +
            '<div class="gv" style="color:' + hex + '">' + valor + '</div>' +
            '<div class="gu">' + (kpi.unidade || '') + '</div>' +
          '</div>' +
        '</div>' +
        '<div class="kpi-rodape">' + badge(kpi, hex) + '</div>' +
      '</div>' +
    '</div>' +
  '</div>';
}

function criarGauge(kpi, hex) {
  var el = document.getElementById('gauge_' + kpi.id);
  if (!el) return;
  var numVal = kpi.valor != null ? Number(kpi.valor) : 0;
  var max = Math.max(kpi.thOk || 0, kpi.thWarn || 0, numVal) * 1.2 || 100;
  var pct = Math.min(numVal / max, 1);

  var ctxCanvas = el.getContext('2d');
  var grad = ctxCanvas.createLinearGradient(0, 0, 160, 0);
  grad.addColorStop(0, rgba(hex, .3));
  grad.addColorStop(1, hex);

  var chart = new Chart(el, {
    type: 'doughnut',
    data: {
      datasets: [{
        data: [pct, 1 - pct],
        backgroundColor: [grad, '#f0f2f5'],
        borderWidth: 0,
        borderRadius: 6
      }]
    },
    options: {
      circumference: 180, rotation: -90,
      cutout: '78%',
      responsive: false,
      maintainAspectRatio: false,
      plugins: { legend: { display: false }, tooltip: { enabled: false } },
      animation: { animateRotate: true, duration: 800 }
    }
  });
  gaugeCharts.push(chart);
}

/* ── Progresso ── */
function renderProgresso(kpi, hex) {
  var valor = fmtVal(kpi);
  var numVal = kpi.valor != null ? Number(kpi.valor) : 0;
  var max = Math.max(kpi.thOk || 0, kpi.thWarn || 0, numVal) * 1.2 || 100;
  var pct = Math.min(Math.round(numVal / max * 100), 100);
  return '<div class="col-xl-6 col-md-6">' +
    '<div class="card kpi-widget kpi-progresso h-100">' +
      '<div class="kpi-body">' +
        '<div class="kpi-header">' +
          '<div class="kpi-titulo">' + kpi.nome + '</div>' +
          badge(kpi, hex) +
        '</div>' +
        '<div class="progress">' +
          '<div class="progress-bar" style="width:' + pct + '%;background:linear-gradient(90deg,' + rgba(hex,.7) + ',' + hex + ')">' +
            valor + ' ' + (kpi.unidade || '') +
          '</div>' +
        '</div>' +
        (kpi.descricao ? '<div class="kpi-desc">' + kpi.descricao + '</div>' : '') +
      '</div>' +
    '</div>' +
  '</div>';
}

async function carregar() {
  limparGauges();
  var r = await fetch(ctx + '/MuralhaDigital/Kpi');
  var d = await r.json();
  if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }

  var container = document.getElementById('kpiCards');
  container.innerHTML = '';
  var medidores = [];

  (d.kpis || []).forEach(function(kpi) {
    var hex = hexDoKpi(kpi);
    var tipo = kpi.tipoVisual || 'card';

    if (tipo === 'compacto') {
      container.insertAdjacentHTML('beforeend', renderCompacto(kpi, hex));
    } else if (tipo === 'medidor') {
      container.insertAdjacentHTML('beforeend', renderMedidor(kpi, hex));
      medidores.push({ kpi: kpi, hex: hex });
    } else if (tipo === 'progresso') {
      container.insertAdjacentHTML('beforeend', renderProgresso(kpi, hex));
    } else {
      container.insertAdjacentHTML('beforeend', renderCard(kpi, hex));
    }
  });

  medidores.forEach(function(m) { criarGauge(m.kpi, m.hex); });

  document.getElementById('lblAtualizacao').textContent =
    'Atualizado: ' + new Date().toLocaleTimeString('pt-BR');
}

carregar();
setInterval(carregar, 60000);
</script>
