<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <h4 class="mb-3"><i class="bi bi-person-check me-2"></i>Fila de Análise de Infrações</h4>

  <!-- Indicadores de status -->
  <div class="row g-2 mb-3" id="indicadores">
    <div class="col-auto text-muted small">Carregando indicadores...</div>
  </div>

  <div class="d-flex gap-2 mb-3 align-items-center">
    <button class="btn btn-primary" onclick="proxima()">
      <i class="bi bi-play-circle me-1"></i>Próxima Infração
    </button>
    <span id="msgFila" class="text-muted small"></span>
  </div>
</div>

<script src="/muralha-digital/assets/js/processamento/dupla-analise.js"></script>
<%@ include file="/includes/rodape.jsp" %>
