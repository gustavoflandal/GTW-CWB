<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Carrossel de Veículos</title>
  <link rel="stylesheet" href="assets/css/correcao-placas-lote.css">
 <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>

<div class="filter" id="filter">
  <div class="container-fluid">
    <div class="col-md-12 d-grid pt-1">
      <span class="p-1 badge bg-secondary text-white text-center rounded">
        <p class="h5 p-1 mb-2"><strong>Correção de placas por lote</strong></p>
      </span>
    </div>
    <div class="row mb-2 mt-2 gap-2">
       <div class="col-md-4">
        <label class="legenda">Equipamento</label>
        <select id="dispositivo" class="selectpicker form-control border"
					data-live-search="true" multiple data-style="btn-white" data-size="20"
					data-none-selected-text="Todos os equipamentos"
					data-actions-box="true" data-select-all-text="Marcar todos"
					data-deselect-all-text="Desmarcar todos"></select>
      </div>
      <div class="col-md-2">
        <label class="legenda">Data Início</label>
        <input type="date" id="dataInicio" class="form-control form-control-md">
      </div>
      <div class="col-md">
        <label class="legenda">Data Fim</label>
        <input type="date" id="dataFim" class="form-control form-control-md">
      </div>
      <div class="col-md-2">
        <label class="legenda">Lote</label>
        <select id="limite" class="form-control">
        <option value="2">Com 2 veículos</option>
          <option value="5">Com 5 veículos</option>
          <option value="10">Com 10 veículos</option>
          <option value="50">Com 50 veículos</option>
          <option value="100">Com 100 veículos</option>
        </select>
      </div>
      <div class="col-md d-flex align-items-end">
        <button class="btn btn-primary" onClick="gerarLote()">GERAR LOTE</button>
      </div>
    </div>
  </div>
</div>

<div id="processar-div" class="container mt-1" hidden>
  <div class="row">
    <button class="btn btn-success" onclick="mostrarResumo()">Gravar lote</button>
  </div>
</div>

  <div id="carouselLegenda" class="text-center mb-1">
    <span class="badge bg-primary"></span>
  </div>

<div id="carouselCards" class="carousel slide" data-bs-ride="false" hidden>
  <div class="carousel-inner">
    <!-- Cards serão gerados dinamicamente via JS -->
  </div>

  <button class="carousel-control-prev" type="button" data-bs-target="#carouselCards" data-bs-slide="prev">
    <span class="carousel-control-prev-icon"></span>
    <span class="visually-hidden">Anterior</span>
  </button>
  <button class="carousel-control-next" type="button" data-bs-target="#carouselCards" data-bs-slide="next">
    <span class="carousel-control-next-icon"></span>
    <span class="visually-hidden">Próximo</span>
  </button>
</div>

<div class="modal fade" id="modalResumo" tabindex="-1" aria-labelledby="modalResumoLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="modalResumoLabel">Resumo de Placas Alteradas</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>
      <div class="modal-body">
        <ul id="listaResumo" class="list-group"></ul>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
        <button type="button" class="btn btn-primary" onclick="salvarAlteracoes()">Salvar Alterações</button>
      </div>
    </div>
  </div>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="assets/js/correcao-placas-lote.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
</body>
</html>
