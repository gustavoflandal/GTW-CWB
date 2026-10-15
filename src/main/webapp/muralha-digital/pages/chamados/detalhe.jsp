<%-- src/main/webapp/muralha-digital/pages/chamados/detalhe.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <nav aria-label="breadcrumb"><ol class="breadcrumb small">
    <li class="breadcrumb-item"><a href="index.jsp">Chamados</a></li>
    <li class="breadcrumb-item active" id="bcProtocolo" aria-current="page"></li>
  </ol></nav>

  <div class="d-flex align-items-center gap-2 flex-wrap mb-3">
    <h4 class="mb-0" id="tTitulo"></h4>
    <span id="tStatus"></span>
    <div class="ms-auto d-flex gap-2 flex-wrap" id="acoes"></div>
  </div>

  <div class="row g-3">
    <div class="col-lg-4">
      <div class="card mb-3"><div class="card-body small">
        <dl class="row mb-0">
          <dt class="col-5">Protocolo</dt><dd class="col-7" id="dProtocolo"></dd>
          <dt class="col-5">Prioridade</dt><dd class="col-7" id="dPrioridade"></dd>
          <dt class="col-5">Categoria</dt><dd class="col-7" id="dCategoria"></dd>
          <dt class="col-5">Equipamento</dt><dd class="col-7" id="dEquipamento"></dd>
          <dt class="col-5">Solicitante</dt><dd class="col-7" id="dSolicitante"></dd>
          <dt class="col-5">Responsável</dt><dd class="col-7" id="dResponsavel"></dd>
          <dt class="col-5">Abertura</dt><dd class="col-7" id="dAbertura"></dd>
        </dl>
      </div></div>
      <div class="card mb-3"><div class="card-header">Prazos (24x7)</div><div class="card-body small">
        <p class="mb-1">Atendimento: <span id="pAtendimento"></span> <span id="sAtendimento"></span></p>
        <p class="mb-1 text-muted" id="cAtendimento"></p>
        <p class="mb-1">Resolução: <span id="pResolucao"></span> <span id="sResolucao"></span></p>
        <p class="mb-0 text-muted" id="cResolucao"></p>
      </div></div>
      <div class="card mb-3 d-none" id="cardAvaliacao"><div class="card-header">Avaliação</div>
        <div class="card-body small" id="dAvaliacao"></div></div>
      <div class="card"><div class="card-header">Anexos</div>
        <ul class="list-group list-group-flush small" id="lstAnexos"></ul></div>
    </div>
    <div class="col-lg-8">
      <div class="card mb-3"><div class="card-header">Descrição</div>
        <div class="card-body" id="dDescricao" style="white-space:pre-wrap"></div></div>
      <div class="card"><div class="card-header">Histórico</div>
        <ul class="list-group list-group-flush" id="lstHistorico"></ul></div>
    </div>
  </div>
</div>

<script>window.CTX = '<%= request.getContextPath() %>';</script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-comum.js"></script>
<script src="<%= request.getContextPath() %>/muralha-digital/assets/js/chamados/chamados-detalhe.js"></script>
