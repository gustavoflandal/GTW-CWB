<%-- src/main/webapp/muralha-digital/pages/chamados/relatorios.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <nav aria-label="breadcrumb"><ol class="breadcrumb small">
    <li class="breadcrumb-item"><a href="index.jsp">Chamados</a></li>
    <li class="breadcrumb-item active" aria-current="page">Relatórios</li>
  </ol></nav>
  <h4 class="mb-3"><i class="bi bi-bar-chart me-2"></i>Relatórios de Atendimento</h4>

  <div class="card mb-3"><div class="card-body">
    <form id="formRel" class="row g-2 align-items-end">
      <div class="col-md-2"><label class="form-label small" for="rInicio">De</label>
        <input id="rInicio" name="dtInicio" type="date" class="form-control form-control-sm"></div>
      <div class="col-md-2"><label class="form-label small" for="rFim">Até</label>
        <input id="rFim" name="dtFim" type="date" class="form-control form-control-sm"></div>
      <div class="col-md-2"><label class="form-label small" for="rCategoria">Categoria</label>
        <select id="rCategoria" name="idCategoria" class="form-select form-select-sm"><option value="">Todas</option></select></div>
      <div class="col-md-2"><label class="form-label small" for="rEquipamento">Equipamento</label>
        <select id="rEquipamento" name="idLocal" class="form-select form-select-sm"><option value="">Todos</option></select></div>
      <div class="col-md-1"><label class="form-label small" for="rPrioridade">Prioridade</label>
        <select id="rPrioridade" name="idPrioridade" class="form-select form-select-sm"><option value="">Todas</option></select></div>
      <div class="col-md-2"><label class="form-label small" for="rAgrup">Agrupar por</label>
        <select id="rAgrup" name="agrupamento" class="form-select form-select-sm">
          <option value="mes">Mês</option><option value="semana">Semana</option><option value="dia">Dia</option>
          <option value="categoria">Categoria</option><option value="equipamento">Equipamento</option>
          <option value="prioridade">Prioridade</option></select></div>
      <div class="col-md-auto"><button class="btn btn-sm btn-primary" type="submit"><i class="bi bi-search me-1"></i>Gerar</button></div>
    </form>
  </div></div>

  <div class="row g-3 mb-3" id="cards"></div>

  <div class="card mb-3"><div class="card-body"><canvas id="grafico" height="90"></canvas></div></div>

  <div class="card">
    <div class="card-header d-flex">
      <span>Indicadores</span>
      <div class="ms-auto btn-group btn-group-sm">
        <button class="btn btn-outline-success" id="btnCsv"><i class="bi bi-filetype-csv me-1"></i>CSV</button>
        <button class="btn btn-outline-success" id="btnXls"><i class="bi bi-file-earmark-excel me-1"></i>XLS</button>
        <button class="btn btn-outline-danger" id="btnPdf"><i class="bi bi-file-earmark-pdf me-1"></i>PDF</button>
      </div>
    </div>
    <div class="card-body p-0"><div class="table-responsive">
      <table class="table table-sm table-striped mb-0">
        <thead class="table-light"><tr><th>Grupo</th><th>Total</th><th>Resolvidos</th><th>Cancelados</th>
          <th>% SLA atend.</th><th>% SLA resol.</th><th>TMA (min)</th><th>TMR (min)</th><th>Nota média</th><th>% avaliados</th></tr></thead>
        <tbody id="tblRel"></tbody>
      </table>
    </div></div>
  </div>
</div>

<script>window.CTX = '<%= request.getContextPath() %>';</script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/jspdf.umd.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/jspdf.plugin.autotable.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-comum.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-relatorios.js"></script>
