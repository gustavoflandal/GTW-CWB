<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

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

<script>
var ctx = '<%= request.getContextPath() %>';
var COR = { OK: 'success', WARN: 'warning', CRIT: 'danger', ERRO: 'secondary' };

async function carregar() {
  var r = await fetch(ctx + '/MuralhaDigital/Kpi');
  var d = await r.json();
  if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }

  var container = document.getElementById('kpiCards');
  container.innerHTML = '';
  (d.kpis || []).forEach(function(kpi) {
    var cor = COR[kpi.status] || 'secondary';
    var valor = kpi.valor != null ? Number(kpi.valor).toLocaleString('pt-BR') : '—';
    container.insertAdjacentHTML('beforeend',
      '<div class="col-md-3 col-sm-6">' +
        '<div class="card border-' + cor + '">' +
          '<div class="card-body text-center">' +
            '<h6 class="card-title text-muted small">' + kpi.nome + '</h6>' +
            '<h2 class="fw-bold text-' + cor + '">' + valor + '</h2>' +
            '<small class="text-muted">' + (kpi.unidade || '') + '</small>' +
            '<div class="mt-1"><span class="badge bg-' + cor + '">' + kpi.status + '</span></div>' +
            '<small class="text-muted d-block mt-1">' + (kpi.descricao || '') + '</small>' +
          '</div>' +
        '</div>' +
      '</div>');
  });
  document.getElementById('lblAtualizacao').textContent =
    'Atualizado: ' + new Date().toLocaleTimeString('pt-BR');
}

carregar();
setInterval(carregar, 60000);
</script>
