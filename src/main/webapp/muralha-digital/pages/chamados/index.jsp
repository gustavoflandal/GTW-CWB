<%-- src/main/webapp/muralha-digital/pages/chamados/index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2 flex-wrap">
    <h4 class="mb-0"><i class="bi bi-headset me-2"></i>Chamados</h4>
    <div class="ms-auto d-flex gap-2">
      <a href="/muralha-digital/pages/chamados/manual.jsp" target="_blank" class="btn btn-outline-info btn-sm" title="Manual do Sistema de Chamados"><i class="bi bi-book"></i> Manual</a>
      <a id="lnkRelatorios" class="btn btn-sm btn-outline-secondary d-none" href="relatorios.jsp"><i class="bi bi-bar-chart me-1"></i>Relatórios</a>
      <a id="lnkConfig" class="btn btn-sm btn-outline-secondary d-none" href="configuracao.jsp"><i class="bi bi-gear me-1"></i>Configuração</a>
      <button id="btnNovo" class="btn btn-sm btn-primary d-none"><i class="bi bi-plus-circle me-1"></i>Novo chamado</button>
    </div>
  </div>

  <div class="card mb-3">
    <div class="card-body">
      <form id="formFiltros" class="row g-2 align-items-end">
        <div class="col-md-2"><label class="form-label small" for="fProtocolo">Protocolo</label>
          <input id="fProtocolo" name="protocolo" class="form-control form-control-sm" placeholder="CH-2026-..."></div>
        <div class="col-md-2"><label class="form-label small" for="fStatus">Status</label>
          <select id="fStatus" name="status" class="form-select form-select-sm"><option value="">Todos</option></select></div>
        <div class="col-md-1"><label class="form-label small" for="fPrioridade">Prioridade</label>
          <select id="fPrioridade" name="idPrioridade" class="form-select form-select-sm"><option value="">Todas</option></select></div>
        <div class="col-md-2"><label class="form-label small" for="fCategoria">Categoria</label>
          <select id="fCategoria" name="idCategoria" class="form-select form-select-sm"><option value="">Todas</option></select></div>
        <div class="col-md-2"><label class="form-label small" for="fEquipamento">Equipamento</label>
          <select id="fEquipamento" name="idLocal" class="form-select form-select-sm"><option value="">Todos</option></select></div>
        <div class="col-md-1"><label class="form-label small" for="fInicio">De</label>
          <input id="fInicio" name="dtInicio" type="date" class="form-control form-control-sm"></div>
        <div class="col-md-1"><label class="form-label small" for="fFim">Até</label>
          <input id="fFim" name="dtFim" type="date" class="form-control form-control-sm"></div>
        <div class="col-md-auto">
          <button type="submit" class="btn btn-sm btn-primary"><i class="bi bi-search"></i></button>
          <button type="reset" class="btn btn-sm btn-outline-secondary"><i class="bi bi-x-lg"></i></button>
        </div>
      </form>
    </div>
  </div>

  <div class="card">
    <div class="card-header d-flex align-items-center gap-2">
      <span class="small text-muted" id="lblTotal"></span>
      <span class="small text-muted ms-2" id="lblAtualizado"></span>
      <div class="ms-auto btn-group btn-group-sm">
        <button class="btn btn-outline-success" id="btnCsv"><i class="bi bi-filetype-csv me-1"></i>CSV</button>
        <button class="btn btn-outline-success" id="btnXls"><i class="bi bi-file-earmark-excel me-1"></i>XLS</button>
        <button class="btn btn-outline-danger" id="btnPdf"><i class="bi bi-file-earmark-pdf me-1"></i>PDF</button>
      </div>
    </div>
    <div class="card-body p-0">
      <div class="table-responsive">
        <table class="table table-sm table-hover align-middle mb-0">
          <thead class="table-light"><tr>
            <th>Protocolo</th><th>Título</th><th>Categoria</th><th>Equipamento</th><th>Prioridade</th>
            <th>Status</th><th>Responsável</th><th>Abertura</th><th>SLA atend.</th><th>SLA resol.</th>
          </tr></thead>
          <tbody id="tblChamados"></tbody>
        </table>
      </div>
    </div>
    <div class="card-footer"><ul id="paginacao" class="pagination pagination-sm mb-0"></ul></div>
  </div>
</div>

<div class="modal fade" id="modalNovo" tabindex="-1" aria-labelledby="modalNovoTitulo">
  <div class="modal-dialog modal-lg">
    <form class="modal-content" id="formNovo">
      <div class="modal-header">
        <h5 class="modal-title" id="modalNovoTitulo"><i class="bi bi-plus-circle me-2"></i>Novo chamado</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>
      <div class="modal-body row g-3">
        <div class="col-md-6"><label class="form-label" for="nCategoria">Categoria *</label>
          <select id="nCategoria" class="form-select" required><option value="">Selecione</option></select></div>
        <div class="col-md-6"><label class="form-label" for="nSubcategoria">Subcategoria *</label>
          <select id="nSubcategoria" name="idCategoria" class="form-select" required><option value="">Selecione a categoria</option></select></div>
        <div class="col-md-6 d-none" id="divEquipamento"><label class="form-label" for="nEquipamento">Equipamento *</label>
          <select id="nEquipamento" name="idLocal" class="form-select"><option value="">Selecione</option></select></div>
        <div class="col-md-6"><label class="form-label" for="nPrioridade">Prioridade *</label>
          <select id="nPrioridade" name="idPrioridade" class="form-select" required></select>
          <div class="form-text" id="nPrazos"></div></div>
        <div class="col-12"><label class="form-label" for="nTitulo">Título *</label>
          <input id="nTitulo" name="titulo" class="form-control" maxlength="150" required></div>
        <div class="col-12"><label class="form-label" for="nDescricao">Descrição *</label>
          <textarea id="nDescricao" name="descricao" class="form-control" rows="5" maxlength="8000" required></textarea></div>
        <div class="col-12"><label class="form-label" for="nArquivos">Anexos (PDF, PNG ou JPG — até 5)</label>
          <input id="nArquivos" name="arquivos" type="file" class="form-control" multiple accept=".pdf,.png,.jpg,.jpeg"></div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="submit" class="btn btn-primary" id="btnSalvarNovo"><i class="bi bi-send me-1"></i>Abrir chamado</button>
      </div>
    </form>
  </div>
</div>

<script>window.CTX = '<%= request.getContextPath() %>';</script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/jspdf.umd.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/jspdf.plugin.autotable.min.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-comum.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-lista.js"></script>
