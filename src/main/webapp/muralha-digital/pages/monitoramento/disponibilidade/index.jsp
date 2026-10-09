<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <h4 class="mb-3"><i class="bi bi-hdd-network me-2"></i>Disponibilidade dos Equipamentos</h4>

  <div class="row g-3 mb-4">
    <div class="col-md-3">
      <div class="card text-center border-success">
        <div class="card-body">
          <h6 class="text-muted">Online agora</h6>
          <h2 id="lblOnline" class="fw-bold text-success">&mdash;</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center border-danger">
        <div class="card-body">
          <h6 class="text-muted">Offline agora</h6>
          <h2 id="lblOffline" class="fw-bold text-danger">&mdash;</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted">Uptime medio (24h)</h6>
          <h2 id="lblUptimeMedio" class="fw-bold">&mdash;</h2>
          <small class="text-muted">%</small>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted">Total monitorados</h6>
          <h2 id="lblTotal" class="fw-bold">&mdash;</h2>
        </div>
      </div>
    </div>
  </div>

  <div class="row g-3">
    <div class="col-md-6">
      <div class="card">
        <div class="card-header">Status atual</div>
        <div class="card-body p-0">
          <table class="table table-sm mb-0">
            <thead>
              <tr>
                <th>Equipamento</th>
                <th>Status</th>
                <th>Ultima passagem</th>
                <th>Offline ha</th>
              </tr>
            </thead>
            <tbody id="tblStatus"></tbody>
          </table>
        </div>
      </div>
    </div>
    <div class="col-md-6">
      <div class="card">
        <div class="card-header">Uptime ultimas 24h</div>
        <div class="card-body"><canvas id="chartUptime" height="300"></canvas></div>
      </div>
    </div>
  </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
<script>
var ctx = '<%= request.getContextPath() %>';
var chartU = null;

function carregar() {
  fetch(ctx + '/MuralhaDigital/Disponibilidade')
    .then(function(r) { return r.json(); })
    .then(function(d) {
      if (!d.ok) return;

      var online = 0, offline = 0;
      var tbody = document.getElementById('tblStatus');
      tbody.innerHTML = '';
      d.status.forEach(function(s) {
        if (s.disponivel) online++;
        else offline++;
        tbody.insertAdjacentHTML('beforeend',
          '<tr class="' + (s.disponivel ? '' : 'table-danger') + '">' +
            '<td>' + s.nome + '</td>' +
            '<td><span class="badge bg-' + (s.disponivel ? 'success' : 'danger') + '">' +
              (s.disponivel ? 'Online' : 'Offline') + '</span></td>' +
            '<td>' + (s.ultima ? s.ultima.substring(0, 16) : '—') + '</td>' +
            '<td>' + (s.disponivel ? '—' : s.minOffline + ' min') + '</td>' +
          '</tr>');
      });
      document.getElementById('lblOnline').textContent = online;
      document.getElementById('lblOffline').textContent = offline;
      document.getElementById('lblTotal').textContent = online + offline;

      var uptimeTotal = 0;
      if (d.uptime.length > 0) {
        d.uptime.forEach(function(u) { uptimeTotal += u.uptime; });
        uptimeTotal = uptimeTotal / d.uptime.length;
      }
      document.getElementById('lblUptimeMedio').textContent = uptimeTotal.toFixed(1);

      if (chartU) chartU.destroy();
      chartU = new Chart(document.getElementById('chartUptime'), {
        type: 'bar',
        data: {
          labels: d.uptime.map(function(u) { return u.nome; }),
          datasets: [{
            label: 'Uptime (%)',
            data: d.uptime.map(function(u) { return u.uptime; }),
            backgroundColor: d.uptime.map(function(u) {
              return u.uptime >= 95 ? '#198754' : u.uptime >= 80 ? '#ffc107' : '#dc3545';
            })
          }]
        },
        options: {
          indexAxis: 'y',
          plugins: { legend: { display: false } },
          scales: { x: { max: 100 } }
        }
      });
    });
}

carregar();
setInterval(carregar, 60000);
</script>
