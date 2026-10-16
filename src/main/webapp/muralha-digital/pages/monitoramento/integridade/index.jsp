<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<link rel="stylesheet" href="assets/css/integridade.css">

<div class="container-fluid" id="telaIntegridade">
    <div class="row gy-3 mt-1">
        <div class="col-sm-12"><h2 class="text-center"><strong>Integridade das Imagens Recebidas</strong></h2></div>
    </div>

    <%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %>

    <ul class="nav nav-tabs mb-3" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" id="abaRecepcao-tab" data-bs-toggle="tab" data-bs-target="#abaRecepcao"
                    type="button" role="tab" aria-controls="abaRecepcao" aria-selected="true">
                <i class="bi bi-shield-check me-1"></i>Recepção
            </button>
        </li>
        <li class="nav-item d-none" role="presentation" id="itemAbaConfig">
            <button class="nav-link" id="abaConfig-tab" data-bs-toggle="tab" data-bs-target="#abaConfig"
                    type="button" role="tab" aria-controls="abaConfig" aria-selected="false">
                <i class="bi bi-gear me-1"></i>Configuração
            </button>
        </li>
    </ul>

    <div class="tab-content">
        <!-- Recepção -->
        <div class="tab-pane fade show active" id="abaRecepcao" role="tabpanel" aria-labelledby="abaRecepcao-tab">
            <div class="card mb-3">
                <div class="card-body">
                    <div class="row gy-3 align-items-end">
                        <div class="col-sm-2">
                            <label for="dtInicio" class="form-label">Data início</label>
                            <input type="date" id="dtInicio" class="form-control form-control-sm">
                        </div>
                        <div class="col-sm-2">
                            <label for="dtFim" class="form-label">Data fim</label>
                            <input type="date" id="dtFim" class="form-control form-control-sm">
                        </div>
                        <div class="col-sm-4">
                            <label for="selEquipamentos" class="form-label">Equipamentos</label>
                            <select id="selEquipamentos" class="selectpicker form-control border" multiple
                                    data-live-search="true" data-actions-box="true" data-style="btn-white" data-size="10"
                                    data-none-selected-text="--Todos--" data-select-all-text="Marcar todos"
                                    data-deselect-all-text="Desmarcar todos"></select>
                        </div>
                        <div class="col-sm-2">
                            <label for="selStatus" class="form-label">Status</label>
                            <select id="selStatus" class="form-select form-select-sm">
                                <option value="">Todos</option>
                                <option value="INTEGRO">Íntegro</option>
                                <option value="DIVERGENTE">Divergente</option>
                                <option value="SEM_CONFERENCIA">Sem conferência</option>
                                <option value="ERRO_LEITURA">Erro de leitura</option>
                            </select>
                        </div>
                        <div class="col-sm-2 btn-toolbar-acoes">
                            <button type="button" class="btn btn-primary btn-sm" id="btnPesquisar" title="Pesquisar">
                                <i class="bi bi-search"></i> Pesquisar
                            </button>
                            <button type="button" class="btn btn-outline-secondary btn-sm" id="btnLimpar" title="Limpar filtros">
                                <i class="bi bi-eraser"></i> Limpar
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-3 mb-3" id="cardsTotais">
                <div class="col"><div class="card text-center"><div class="card-body">
                    <h6 class="text-muted"><i class="bi bi-inbox me-1"></i>Recebidas</h6>
                    <h3 class="fw-bold mb-0" id="totRecebidos">&mdash;</h3></div></div></div>
                <div class="col"><div class="card text-center border-success"><div class="card-body">
                    <h6 class="text-muted"><i class="bi bi-check-circle me-1"></i>Íntegras</h6>
                    <h3 class="fw-bold text-success mb-0" id="totIntegros">&mdash;</h3></div></div></div>
                <div class="col"><div class="card text-center border-danger"><div class="card-body">
                    <h6 class="text-muted"><i class="bi bi-exclamation-triangle-fill me-1"></i>Divergentes</h6>
                    <h3 class="fw-bold text-danger mb-0" id="totDivergentes">&mdash;</h3></div></div></div>
                <div class="col"><div class="card text-center border-warning"><div class="card-body">
                    <h6 class="text-muted"><i class="bi bi-x-octagon me-1"></i>Erro de leitura</h6>
                    <h3 class="fw-bold mb-0" id="totErroLeitura">&mdash;</h3></div></div></div>
                <div class="col"><div class="card text-center border-secondary"><div class="card-body">
                    <h6 class="text-muted"><i class="bi bi-dash-circle me-1"></i>Sem conferência</h6>
                    <h3 class="fw-bold text-secondary mb-0" id="totSemConferencia">&mdash;</h3></div></div></div>
            </div>
            <p class="text-muted small" id="lblUltimaVerificacao"></p>

            <div class="card mb-3">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span><i class="bi bi-calendar me-1"></i>Por equipamento e dia</span>
                    <div class="btn-toolbar-acoes">
                        <button type="button" class="btn btn-success btn-sm" id="btnResumoXls" title="Exportar Excel" aria-label="Exportar Excel">
                            <i class="bi bi-file-earmark-excel"></i></button>
                        <button type="button" class="btn btn-danger btn-sm" id="btnResumoPdf" title="Exportar PDF" aria-label="Exportar PDF">
                            <i class="bi bi-file-earmark-pdf"></i></button>
                    </div>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover table-sm mb-0" id="tblResumo">
                            <thead class="table-light">
                                <tr>
                                    <th>Data</th><th>Equipamento</th><th class="text-end">Recebidas</th>
                                    <th class="text-end">Íntegras</th><th class="text-end">Divergentes</th>
                                    <th class="text-end">Erro de leitura</th><th class="text-end">Sem conferência</th>
                                    <th class="text-end">Rejeitadas</th>
                                </tr>
                            </thead>
                            <tbody><tr><td colspan="8" class="text-center text-muted">Informe o período e clique em Pesquisar.</td></tr></tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="card mb-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span><i class="bi bi-list-check me-1"></i>Imagens conferidas <small class="text-muted" id="lblTotalDetalhe"></small></span>
                    <div class="btn-toolbar-acoes">
                        <button type="button" class="btn btn-outline-secondary btn-sm" id="btnCsv" title="CSV completo com SHA-256">
                            <i class="bi bi-filetype-csv"></i> CSV</button>
                        <button type="button" class="btn btn-success btn-sm" id="btnDetalheXls" title="Exportar Excel (página atual)" aria-label="Exportar Excel">
                            <i class="bi bi-file-earmark-excel"></i></button>
                        <button type="button" class="btn btn-danger btn-sm" id="btnDetalhePdf" title="Exportar PDF (página atual)" aria-label="Exportar PDF">
                            <i class="bi bi-file-earmark-pdf"></i></button>
                    </div>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover table-sm mb-0" id="tblDetalhe">
                            <thead class="table-light">
                                <tr>
                                    <th>Captura</th><th>Recepção</th><th>Equipamento</th><th>Imagem</th><th>Status</th>
                                    <th>Origem do hash</th><th>Motivo</th><th>Verificação</th><th>SHA-256 calculado</th>
                                </tr>
                            </thead>
                            <tbody><tr><td colspan="9" class="text-center text-muted">Informe o período e clique em Pesquisar.</td></tr></tbody>
                        </table>
                    </div>
                </div>
                <div class="card-footer d-flex justify-content-between align-items-center">
                    <button type="button" class="btn btn-outline-secondary btn-sm" id="btnPaginaAnterior" disabled>
                        <i class="bi bi-chevron-left"></i> Anterior</button>
                    <span class="small text-muted" id="lblPagina"></span>
                    <button type="button" class="btn btn-outline-secondary btn-sm" id="btnPaginaSeguinte" disabled>
                        Próxima <i class="bi bi-chevron-right"></i></button>
                </div>
            </div>
        </div>

        <!-- Configuração -->
        <div class="tab-pane fade" id="abaConfig" role="tabpanel" aria-labelledby="abaConfig-tab">
            <div class="card mb-3">
                <div class="card-header"><i class="bi bi-sliders me-1"></i>Origem padrão do hash</div>
                <div class="card-body">
                    <p class="small text-muted mb-2">
                        Vale para os equipamentos sem configuração própria. <strong>PAYLOAD_SOAP</strong>: hash enviado pelo Captura junto
                        com a imagem; <strong>DADO_BRUTO</strong>: campo do JSON de características da captura; <strong>AUSENTE</strong>:
                        o equipamento não envia hash e as imagens ficam "sem conferência".
                    </p>
                    <div class="row gy-2 align-items-end">
                        <div class="col-sm-3">
                            <label for="selOrigemPadrao" class="form-label">Origem</label>
                            <select id="selOrigemPadrao" class="form-select form-select-sm">
                                <option value="AUSENTE">AUSENTE</option>
                                <option value="PAYLOAD_SOAP">PAYLOAD_SOAP</option>
                                <option value="DADO_BRUTO">DADO_BRUTO</option>
                            </select>
                        </div>
                        <div class="col-sm-4">
                            <label for="txtCampoPadrao" class="form-label">Campo do dado bruto</label>
                            <input type="text" id="txtCampoPadrao" class="form-control form-control-sm" maxlength="100"
                                   placeholder="ex.: integridade.sha256">
                        </div>
                        <div class="col-sm-2">
                            <button type="button" class="btn btn-primary btn-sm" id="btnSalvarPadrao" title="Salvar origem padrão">
                                <i class="bi bi-check-lg"></i> Salvar</button>
                        </div>
                    </div>
                    <p class="small text-muted mt-3 mb-0" id="lblParametros"></p>
                </div>
            </div>

            <div class="card mb-4">
                <div class="card-header"><i class="bi bi-hdd-network me-1"></i>Origem por equipamento</div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover table-sm align-middle mb-0" id="tblConfig">
                            <thead class="table-light">
                                <tr><th>Equipamento</th><th>Série</th><th style="width:220px">Origem</th>
                                    <th style="width:260px">Campo do dado bruto</th><th style="width:90px">Ação</th></tr>
                            </thead>
                            <tbody><tr><td colspan="5" class="text-center text-muted">Carregando…</td></tr></tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="/muralha-digital/assets/js/xlsx.core.min.js"></script>
<script src="/muralha-digital/assets/js/jspdf.umd.min.js"></script>
<script src="/muralha-digital/assets/js/jspdf.plugin.autotable.min.js"></script>
<script src="/muralha-digital/utils/export-table.js"></script>
<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
<script src="assets/js/integridade.js"></script>
