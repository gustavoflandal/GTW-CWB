<%-- src/main/webapp/muralha-digital/pages/chamados/configuracao.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <nav aria-label="breadcrumb"><ol class="breadcrumb small">
    <li class="breadcrumb-item"><a href="index.jsp">Chamados</a></li>
    <li class="breadcrumb-item active" aria-current="page">Configuração</li>
  </ol></nav>
  <h4 class="mb-3"><i class="bi bi-gear me-2"></i>Configuração de Chamados</h4>

  <ul class="nav nav-tabs" role="tablist">
    <li class="nav-item"><button class="nav-link active" data-bs-toggle="tab" data-bs-target="#abaPri" type="button">Prioridades e prazos</button></li>
    <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#abaCat" type="button">Categorias</button></li>
  </ul>
  <div class="tab-content border border-top-0 p-3">
    <div class="tab-pane fade show active" id="abaPri">
      <button class="btn btn-sm btn-primary mb-2" id="btnNovaPri"><i class="bi bi-plus-circle me-1"></i>Nova prioridade</button>
      <table class="table table-sm align-middle"><thead><tr><th>Ordem</th><th>Código</th><th>Nome</th>
        <th>Atendimento (min)</th><th>Resolução (min)</th><th>Ativa</th><th></th></tr></thead><tbody id="tblPri"></tbody></table>
    </div>
    <div class="tab-pane fade" id="abaCat">
      <button class="btn btn-sm btn-primary mb-2" id="btnNovaCat"><i class="bi bi-plus-circle me-1"></i>Nova categoria</button>
      <table class="table table-sm align-middle"><thead><tr><th>Categoria</th><th>Subcategoria</th>
        <th>Exige equipamento</th><th>Ativa</th><th></th></tr></thead><tbody id="tblCat"></tbody></table>
    </div>
  </div>
</div>

<script>window.CTX = '<%= request.getContextPath() %>';</script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-comum.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-configuracao.js"></script>
