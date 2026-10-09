<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2 flex-wrap">
    <h4 class="mb-0"><i class="bi bi-file-earmark-arrow-down me-2"></i>Exportação de Passagens</h4>
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
          <label for="selStatus" class="form-label small">Status</label>
          <select id="selStatus" class="form-select form-select-sm">
            <option value="">Todos</option>
            <option value="AGUARDANDO_ANALISE">Aguardando</option>
            <option value="PRIMEIRA_ANALISE">1ª Análise</option>
            <option value="PRE_APROVADA">Pré-aprovada</option>
            <option value="DESEMPATE">Desempate</option>
            <option value="REPROVADA">Reprovada</option>
          </select>
        </div>
        <div class="col-md-auto">
          <button class="btn btn-sm btn-primary" onclick="consultar()">
            <i class="bi bi-search me-1"></i>Consultar
          </button>
        </div>
        <div class="col-md-auto ms-md-auto">
          <div class="btn-group">
            <button class="btn btn-sm btn-outline-secondary" onclick="baixarCsv()" title="CSV com hash SHA-256">
              <i class="bi bi-filetype-csv me-1"></i>CSV
            </button>
            <button class="btn btn-sm btn-outline-success" onclick="GerarRelatorioXLSX('tblResultado','passagens.xlsx',[])" title="Excel XLSX">
              <i class="bi bi-file-earmark-excel me-1"></i>XLS
            </button>
            <button class="btn btn-sm btn-outline-danger" onclick="GerarRelatorioPDF('tblResultado','passagens.pdf',[])" title="PDF">
              <i class="bi bi-file-earmark-pdf me-1"></i>PDF
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="card">
    <div class="card-header d-flex justify-content-between">
      <span>Resultado</span>
      <span id="lblTotal" class="text-muted small"></span>
    </div>
    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table table-sm table-striped mb-0" id="tblResultado">
          <thead>
            <tr>
              <th>ID</th><th>Placa</th><th>Local</th><th>Pista</th>
              <th>Data Captura</th><th>Data Recepção</th><th>Status</th><th>Latência (ms)</th>
            </tr>
          </thead>
          <tbody></tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/xlsx.core.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/jspdf.umd.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/jspdf.plugin.autotable.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/utils/export-table.js"></script>
<script>
var ctx = '<%= request.getContextPath() %>';

function obterFiltros() {
  return {
    dtInicio: document.getElementById('dtInicio').value,
    dtFim:    document.getElementById('dtFim').value,
    idLocal:  document.getElementById('idLocal').value,
    status:   document.getElementById('selStatus').value
  };
}

function buildParams(filtros, formato) {
  var p = new URLSearchParams();
  if (formato) p.append('formato', formato);
  if (filtros.dtInicio) p.append('dtInicio', filtros.dtInicio);
  if (filtros.dtFim) p.append('dtFim', filtros.dtFim);
  if (filtros.idLocal) p.append('idLocal', filtros.idLocal);
  if (filtros.status) p.append('status', filtros.status);
  return p.toString();
}

async function consultar() {
  var filtros = obterFiltros();
  if (!filtros.dtInicio || !filtros.dtFim) {
    Swal.fire('Atenção', 'Informe o período.', 'warning');
    return;
  }
  var r = await fetch(ctx + '/MuralhaDigital/Exportacao?' + buildParams(filtros, 'json'));
  var d = await r.json();
  if (!d.ok) { Swal.fire('Erro', d.erro, 'error'); return; }

  document.getElementById('lblTotal').textContent = d.total + ' registros';
  var tbody = document.querySelector('#tblResultado tbody');
  tbody.innerHTML = '';
  d.rows.forEach(function(row) {
    var cls = '';
    if (row.status === 'PRE_APROVADA') cls = 'text-success';
    else if (row.status === 'REPROVADA') cls = 'text-danger';
    else if (row.status === 'DESEMPATE') cls = 'text-warning';
    tbody.insertAdjacentHTML('beforeend',
      '<tr>' +
        '<td class="small">' + (row.id || '').substring(0, 8) + '…</td>' +
        '<td>' + (row.placa || '') + '</td>' +
        '<td>' + row.idLocal + '</td>' +
        '<td>' + row.idPista + '</td>' +
        '<td class="small">' + (row.dtCaptura || '') + '</td>' +
        '<td class="small">' + (row.dtRecepcao || '') + '</td>' +
        '<td class="' + cls + '">' + (row.status || '') + '</td>' +
        '<td>' + row.latenciaMs + '</td>' +
      '</tr>');
  });
}

function baixarCsv() {
  var filtros = obterFiltros();
  if (!filtros.dtInicio || !filtros.dtFim) {
    Swal.fire('Atenção', 'Informe o período.', 'warning');
    return;
  }
  window.location.href = ctx + '/MuralhaDigital/Exportacao?' + buildParams(filtros, 'csv');
}
</script>
