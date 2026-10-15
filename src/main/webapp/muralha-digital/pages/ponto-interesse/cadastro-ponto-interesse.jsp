<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer"/>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<style>
  #map { height: 400px; width: 100%; border-radius: 0.25rem; }
</style>

<div class="container py-3" style="max-width: 860px;">
  <div class="d-flex align-items-center mb-3 gap-2">
    <h4 class="mb-0"><i class="bi bi-geo-alt me-2"></i>Cadastro de Ponto de Interesse</h4>
    <a href="consulta-ponto-interesse.jsp" class="btn btn-sm btn-outline-secondary ms-auto">
      <i class="bi bi-arrow-left me-1"></i>Voltar
    </a>
  </div>

  <div class="card mb-3">
    <div class="card-header">
      <h6 class="mb-0"><i class="bi bi-map me-1"></i>Localização</h6>
    </div>
    <div class="card-body p-0">
      <div id="map" style="height:400px; width:100%;"></div>
    </div>
    <div class="card-footer bg-light">
      <small class="text-muted" id="latlng">Clique no mapa para selecionar a localização</small>
    </div>
  </div>

  <div class="card">
    <div class="card-header">
      <h6 class="mb-0"><i class="bi bi-pencil-square me-1"></i>Dados do Ponto</h6>
    </div>
    <div class="card-body">
      <div class="row mb-3">
        <div class="col-md-5">
          <label for="inputName" class="form-label small">Nome</label>
          <input id="inputName" type="text" class="form-control form-control-sm" placeholder="Nome do ponto">
        </div>
        <div class="col-md-7">
          <label for="inputDesc" class="form-label small">Descrição</label>
          <input id="inputDesc" type="text" class="form-control form-control-sm" placeholder="Descrição do ponto">
        </div>
      </div>

      <div class="row mb-3">
        <div class="col-md-5">
          <label for="inputType" class="form-label small">Tipo de Ponto</label>
          <select class="form-select form-select-sm" id="inputType">
            <option value="" selected>Selecione o tipo...</option>
            <option value="1">Banco</option>
            <option value="2">Órgão Governamental</option>
            <option value="3">Estabelecimento</option>
            <option value="4">Outros</option>
          </select>
        </div>
        <div class="col-md-7">
          <label for="inputEquip" class="form-label small">Equipamentos</label>
          <div class="input-group input-group-sm">
            <select class="selectpicker form-control border" data-live-search="true" id="inputEquip" data-style="btn-white" data-size="20" data-none-selected-text="Equipamentos...">
              <option value="0" selected>Selecione o equipamento...</option>
            </select>
            <button class="btn btn-success" type="button" onclick="adicionaEquip()">
              <i class="bi bi-plus-lg me-1"></i>Adicionar
            </button>
          </div>
        </div>
      </div>

      <div class="mb-3">
        <label for="inputNameEquip" class="form-label small">Equipamentos Selecionados</label>
        <textarea class="form-control form-control-sm" id="inputNameEquip" rows="3" readonly></textarea>
      </div>

      <div class="d-flex">
        <button class="btn btn-primary" onclick="salvaPonto()" type="button">
          <i class="bi bi-check-lg me-1"></i>Salvar
        </button>
      </div>
    </div>
  </div>
</div>

<script src="/muralha-digital/utils/maps-config.js"></script>
<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
<script src="assets/js/cadastro-ponto-interesse.js"></script>
<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk&callback=initMap"></script>
