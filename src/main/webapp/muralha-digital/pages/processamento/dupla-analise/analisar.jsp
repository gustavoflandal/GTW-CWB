<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container py-3">
  <div class="d-flex align-items-center justify-content-between mb-3">
    <h5 class="mb-0"><i class="bi bi-image me-2"></i>Análise de Infração &mdash; <span id="lblPlaca" class="fw-bold text-primary"></span></h5>
    <a href="fila.jsp" class="btn btn-sm btn-outline-secondary">
      <i class="bi bi-arrow-left me-1"></i>Voltar à fila
    </a>
  </div>

  <div class="row mt-2">
    <!-- Coluna da imagem -->
    <div class="col-md-8">
      <div class="border rounded p-2 text-center bg-dark" style="min-height:320px">
        <img id="imgInfracao" src="" class="img-fluid" alt="Imagem da infração"
             style="max-height:480px; transition: filter .1s;">
      </div>
      <div class="mt-2 d-flex gap-4 align-items-center flex-wrap">
        <label class="mb-0 small">
          Brilho: <input type="range" id="brilho" min="0.3" max="2" step="0.1"
                         value="1" oninput="ajustarFiltro()">
        </label>
        <label class="mb-0 small">
          Contraste: <input type="range" id="contraste" min="0.3" max="2" step="0.1"
                            value="1" oninput="ajustarFiltro()">
        </label>
        <button class="btn btn-sm btn-outline-secondary" onclick="resetarFiltros()">
          <i class="bi bi-arrow-counterclockwise"></i> Resetar
        </button>
      </div>
    </div>

    <!-- Coluna de dados e classificação -->
    <div class="col-md-4 mt-3 mt-md-0">
      <div class="card p-3">
        <h6 class="border-bottom pb-2">Dados da Passagem</h6>
        <p class="mb-1 small"><strong>Data/Hora:</strong> <span id="lblData"></span></p>
        <p class="mb-1 small"><strong>Local (ID):</strong> <span id="lblLocal"></span></p>
        <p class="mb-3 small"><strong>Pista:</strong> <span id="lblPista"></span></p>

        <h6 class="border-bottom pb-2">Sua Classificação</h6>
        <div class="mb-3">
          <div class="form-check">
            <input class="form-check-input" type="radio" name="classif" value="VALIDA" id="rValida">
            <label class="form-check-label" for="rValida">
              <span class="text-success fw-semibold">✅ Válida</span>
            </label>
          </div>
          <div class="form-check">
            <input class="form-check-input" type="radio" name="classif" value="INVALIDA" id="rInvalida">
            <label class="form-check-label" for="rInvalida">
              <span class="text-danger fw-semibold">❌ Inválida</span>
            </label>
          </div>
          <div class="form-check">
            <input class="form-check-input" type="radio" name="classif" value="DUVIDA" id="rDuvida">
            <label class="form-check-label" for="rDuvida">
              <span class="text-warning fw-semibold">❓ Dúvida</span>
            </label>
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label small">Justificativa
            <span id="lblJustObrig" class="text-danger" style="display:none"> (obrigatória)</span>
          </label>
          <textarea id="justificativa" class="form-control" rows="3"
                    placeholder="Descreva o motivo da classificação..."></textarea>
        </div>

        <button class="btn btn-success w-100" onclick="salvar()">
          <i class="bi bi-check-lg me-1"></i>Confirmar Análise
        </button>
      </div>
    </div>
  </div>
</div>

<script src="/muralha-digital/assets/js/processamento/dupla-analise.js"></script>
<%@ include file="/includes/rodape.jsp" %>
