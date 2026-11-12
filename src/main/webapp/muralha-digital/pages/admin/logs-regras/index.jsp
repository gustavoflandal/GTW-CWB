<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<style>
    .table td, .table th { font-size: .85rem; vertical-align: middle; }
    code { font-size: .8rem; }
</style>

<div class="container-fluid py-3">
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h4 class="mb-0"><i class="bi bi-funnel me-2"></i>Captura de Logs — Regras</h4>
        <div class="d-flex gap-1">
            <button class="btn btn-success btn-sm" onclick="novaRegra()"><i class="bi bi-plus-lg"></i> Nova regra</button>
            <button class="btn btn-outline-secondary btn-sm" onclick="recarregarCache()" title="Aplica as regras imediatamente"><i class="bi bi-arrow-clockwise"></i> Recarregar cache</button>
        </div>
    </div>

    <!-- Painel: tipos de evento (valem para TODAS as telas) -->
    <div class="card mb-3 shadow-sm">
        <div class="card-header fw-semibold" style="background:#eef5fb;">
            <i class="bi bi-ui-checks-grid me-1"></i>Tipos de evento capturados — valem para <b>todas as telas</b>
        </div>
        <div class="card-body">
            <p class="small text-muted mb-2">
                Ligue ou desligue a captura de cada tipo de operação no sistema inteiro. Todas as telas podem capturar estes eventos.
            </p>
            <div id="tiposEvento" class="row g-2"></div>
            <button class="btn btn-primary btn-sm mt-3" onclick="salvarTipos()">
                <i class="bi bi-save"></i> Salvar tipos de evento
            </button>
        </div>
    </div>

    <h6 class="mt-4 mb-2"><i class="bi bi-sliders me-1"></i>Regras avançadas (por rota)</h6>
    <div class="alert alert-info py-2 small">
        Controle fino por rota/método. As regras são avaliadas por <b>ordem crescente</b>; a <b>primeira</b> cujo padrão de rota (e método) casar decide a captura.
        Use <code>%</code> como curinga. Regras de exclusão (Capturar = Não) ignoram ruído (estáticos, polling). O <b>nível</b> <code>AUTO</code> deixa o sistema
        classificar o evento (acesso, visualização, cadastro, alteração, exclusão) automaticamente pela ação.
    </div>

    <div class="table-responsive">
        <table class="table table-sm table-hover table-bordered align-middle">
            <thead style="background-color:#0d75bf; color:#fff;">
                <tr>
                    <th>Ordem</th><th>Descrição</th><th>Padrão de rota</th><th>Método</th>
                    <th>Captura</th><th>Nível</th><th>Parâmetros</th><th>Mascarar</th>
                    <th>Garantido</th><th>Ativo</th><th>Ações</th>
                </tr>
            </thead>
            <tbody id="tbodyRegras">
                <tr><td colspan="11" class="text-center text-muted">Carregando...</td></tr>
            </tbody>
        </table>
    </div>
</div>

<!-- Modal -->
<div class="modal fade" id="modalRegra" tabindex="-1">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header" style="background-color:#0d75bf; color:#fff;">
        <h5 class="modal-title" id="modalTitulo">Regra de captura</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <input type="hidden" id="rId">
        <div class="row g-2">
          <div class="col-md-2"><label class="form-label form-label-sm">Ordem</label>
            <input id="rOrdem" type="number" class="form-control form-control-sm" value="100"></div>
          <div class="col-md-10"><label class="form-label form-label-sm">Descrição</label>
            <input id="rDescricao" class="form-control form-control-sm"></div>
          <div class="col-md-8"><label class="form-label form-label-sm">Padrão de rota (use % como curinga)</label>
            <input id="rPadrao" class="form-control form-control-sm" placeholder="/MuralhaDigital/%"></div>
          <div class="col-md-4"><label class="form-label form-label-sm">Método (vazio = qualquer)</label>
            <select id="rMetodo" class="form-select form-select-sm">
              <option value="">Qualquer</option><option>GET</option><option>POST</option><option>PUT</option><option>DELETE</option>
            </select></div>
          <div class="col-md-4"><label class="form-label form-label-sm">Capturar?</label>
            <select id="rCapturar" class="form-select form-select-sm"><option value="1">Sim</option><option value="0">Não (ignorar)</option></select></div>
          <div class="col-md-4"><label class="form-label form-label-sm">Nível</label>
            <select id="rNivel" class="form-select form-select-sm">
              <option value="AUTO">AUTO (classifica pela ação)</option>
              <option>ACESSO</option><option>VISUALIZACAO</option><option>CADASTRO</option>
              <option>ALTERACAO</option><option>EXCLUSAO</option><option>EXPORTACAO</option><option>IMPRESSAO</option><option>ERRO</option>
            </select></div>
          <div class="col-md-4"><label class="form-label form-label-sm">Funcionalidade (opcional)</label>
            <input id="rFuncionalidade" class="form-control form-control-sm"></div>
          <div class="col-md-6"><label class="form-label form-label-sm">Campos a mascarar (separados por vírgula)</label>
            <input id="rMascarar" class="form-control form-control-sm" placeholder="senha,cpf,token"></div>
          <div class="col-md-6 d-flex align-items-end gap-3 pt-2">
            <div class="form-check"><input class="form-check-input" type="checkbox" id="rParametros">
              <label class="form-check-label" for="rParametros">Registrar parâmetros</label></div>
            <div class="form-check"><input class="form-check-input" type="checkbox" id="rGarantido">
              <label class="form-check-label" for="rGarantido">Captura garantida (síncrona)</label></div>
            <div class="form-check"><input class="form-check-input" type="checkbox" id="rAtivo" checked>
              <label class="form-check-label" for="rAtivo">Ativa</label></div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>
        <button class="btn btn-primary btn-sm" onclick="salvarRegra()"><i class="bi bi-save"></i> Salvar</button>
      </div>
    </div>
  </div>
</div>

<script src="/muralha-digital/assets/js/admin/logs-regras.js?v=2"></script>
