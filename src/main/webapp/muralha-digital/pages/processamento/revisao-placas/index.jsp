<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<link rel="stylesheet" href="assets/css/revisao-placas.css">

<div class="container-fluid" id="telaRevisaoPlacas">
    <div class="row gy-3 mt-1">
        <div class="col-sm-12"><h2 class="text-center"><strong>Revisão de Placas</strong></h2></div>
    </div>

    <%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %>
    <div id="avisoEstrutura" class="alert alert-warning d-none" role="alert"></div>

    <ul class="nav nav-tabs mb-3" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" data-bs-toggle="tab" data-bs-target="#abaFila" type="button" role="tab">
                <i class="bi bi-list-check me-1"></i>Fila de revisão</button>
        </li>
        <li class="nav-item d-none" role="presentation" id="itemAbaConfig">
            <button class="nav-link" data-bs-toggle="tab" data-bs-target="#abaConfig" type="button" role="tab">
                <i class="bi bi-sliders me-1"></i>Score e limiar</button>
        </li>
    </ul>

    <div class="tab-content">
        <div class="tab-pane fade show active" id="abaFila" role="tabpanel">
            <div class="card mb-3"><div class="card-body">
                <div class="row gy-3 align-items-end">
                    <div class="col-sm-2">
                        <label for="fDtInicio" class="form-label">Data início</label>
                        <input type="date" id="fDtInicio" class="form-control form-control-sm">
                    </div>
                    <div class="col-sm-2">
                        <label for="fDtFim" class="form-label">Data fim</label>
                        <input type="date" id="fDtFim" class="form-control form-control-sm">
                    </div>
                    <div class="col-sm-4">
                        <label for="fEquipamentos" class="form-label">Equipamentos</label>
                        <select id="fEquipamentos" class="selectpicker form-control border" multiple data-live-search="true"
                                data-actions-box="true" data-style="btn-white" data-size="10" data-none-selected-text="--Todos--"
                                data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"></select>
                    </div>
                    <div class="col-sm-2">
                        <label for="fSituacao" class="form-label">Situação</label>
                        <select id="fSituacao" class="form-select form-select-sm">
                            <option value="PENDENTE" selected>Pendente</option>
                            <option value="CORRIGIDA">Corrigida</option>
                            <option value="CONFIRMADA">Conferida</option>
                            <option value="">Todas</option>
                        </select>
                    </div>
                    <div class="col-sm-2">
                        <label for="fMotivo" class="form-label">Motivo</label>
                        <select id="fMotivo" class="form-select form-select-sm">
                            <option value="">Todos</option>
                            <option value="SCORE_BAIXO">Score abaixo do limiar</option>
                            <option value="SEM_LEITURA">Sem placa lida</option>
                            <option value="DIVERGENCIA_CAMPO_CENTRAL">Divergência equipamento × central</option>
                            <option value="SCORE_AUSENTE">Score não informado</option>
                            <option value="MANUAL">Intervenção manual</option>
                        </select>
                    </div>
                    <div class="col-sm-1">
                        <label for="fScoreMin" class="form-label">Score de</label>
                        <input type="number" id="fScoreMin" class="form-control form-control-sm" min="0" max="100" step="0.1">
                    </div>
                    <div class="col-sm-1">
                        <label for="fScoreMax" class="form-label">até</label>
                        <input type="number" id="fScoreMax" class="form-control form-control-sm" min="0" max="100" step="0.1">
                    </div>
                    <div class="col-sm-2">
                        <label for="fIntervencao" class="form-label">Tipo de intervenção</label>
                        <select id="fIntervencao" class="form-select form-select-sm">
                            <option value="">Todos</option>
                            <option value="FILA">Fila de revisão</option>
                            <option value="LOTE">Correção em lote</option>
                            <option value="CONSULTA">Consulta de veículos</option>
                        </select>
                    </div>
                    <div class="col-sm-2">
                        <label for="fOperador" class="form-label">Operador</label>
                        <select id="fOperador" class="form-select form-select-sm"><option value="">Todos</option></select>
                    </div>
                    <div class="col-sm-4 btn-toolbar-acoes">
                        <button type="button" class="btn btn-primary btn-sm" id="btnPesquisar" title="Pesquisar"><i class="bi bi-search"></i> Pesquisar</button>
                        <button type="button" class="btn btn-outline-secondary btn-sm" id="btnLimpar" title="Limpar filtros"><i class="bi bi-eraser"></i> Limpar</button>
                        <button type="button" class="btn btn-success btn-sm" id="btnXls" title="Exportar Excel (página atual)" aria-label="Exportar Excel"><i class="bi bi-file-earmark-excel"></i></button>
                        <button type="button" class="btn btn-danger btn-sm" id="btnPdf" title="Exportar PDF (página atual)" aria-label="Exportar PDF"><i class="bi bi-file-earmark-pdf"></i></button>
                    </div>
                </div>
            </div></div>

            <div class="card mb-4">
                <div class="card-header"><i class="bi bi-list-check me-1"></i>Passagens <small class="text-muted" id="lblTotal"></small></div>
                <div class="card-body p-0"><div class="table-responsive">
                    <table class="table table-hover table-sm align-middle mb-0" id="tblFila">
                        <thead class="table-light"><tr>
                            <th>Passagem</th><th>Equipamento</th><th>Placas</th><th class="text-end">Score</th><th class="text-end">Limiar</th>
                            <th>Motivo</th><th>Situação</th><th>Intervenção</th><th>Operador</th><th class="no-export">Ação</th>
                        </tr></thead>
                        <tbody><tr><td colspan="10" class="text-center text-muted">Informe o período e clique em Pesquisar.</td></tr></tbody>
                    </table>
                </div></div>
                <div class="card-footer d-flex justify-content-between align-items-center">
                    <button type="button" class="btn btn-outline-secondary btn-sm" id="btnAnterior" disabled><i class="bi bi-chevron-left"></i> Anterior</button>
                    <span class="small text-muted" id="lblPagina"></span>
                    <button type="button" class="btn btn-outline-secondary btn-sm" id="btnSeguinte" disabled>Próxima <i class="bi bi-chevron-right"></i></button>
                </div>
            </div>
        </div>

        <div class="tab-pane fade" id="abaConfig" role="tabpanel">
            <div class="card mb-3">
                <div class="card-header"><i class="bi bi-sliders me-1"></i>Padrão</div>
                <div class="card-body">
                    <p class="small text-muted mb-2">
                        Leituras com score abaixo do limiar vão para a fila de revisão. <strong>TABELA_LEITURA</strong>: score gravado pela recepção;
                        <strong>DADO_BRUTO</strong>: campo do JSON de características da captura (número ou lista, um por placa);
                        <strong>AUSENTE</strong>: o equipamento não informa score.
                    </p>
                    <div class="row gy-2 align-items-end">
                        <div class="col-sm-3">
                            <label for="cOrigem" class="form-label">Origem do score</label>
                            <select id="cOrigem" class="form-select form-select-sm">
                                <option value="AUSENTE">AUSENTE</option>
                                <option value="TABELA_LEITURA">TABELA_LEITURA</option>
                                <option value="DADO_BRUTO">DADO_BRUTO</option>
                            </select>
                        </div>
                        <div class="col-sm-3">
                            <label for="cCampo" class="form-label">Campo do dado bruto</label>
                            <input type="text" id="cCampo" class="form-control form-control-sm" maxlength="100" placeholder="ex.: ocr.score">
                        </div>
                        <div class="col-sm-2">
                            <label for="cLimiar" class="form-label">Limiar (0-100)</label>
                            <input type="number" id="cLimiar" class="form-control form-control-sm" min="0" max="100" step="0.1">
                        </div>
                        <div class="col-sm-2">
                            <button type="button" class="btn btn-primary btn-sm" id="btnSalvarPadrao" title="Salvar padrão"><i class="bi bi-check-lg"></i> Salvar</button>
                        </div>
                    </div>
                    <p class="small text-muted mt-3 mb-0" id="lblParametros"></p>
                </div>
            </div>
            <div class="card mb-4">
                <div class="card-header"><i class="bi bi-hdd-network me-1"></i>Por equipamento <small class="text-muted">(vazio = usa o padrão)</small></div>
                <div class="card-body p-0"><div class="table-responsive">
                    <table class="table table-hover table-sm align-middle mb-0" id="tblConfig">
                        <thead class="table-light"><tr><th>Equipamento</th><th>Série</th><th style="width:200px">Origem</th>
                            <th style="width:220px">Campo</th><th style="width:130px">Limiar</th><th style="width:80px">Ação</th></tr></thead>
                        <tbody><tr><td colspan="6" class="text-center text-muted">Carregando…</td></tr></tbody>
                    </table>
                </div></div>
            </div>
        </div>
    </div>
</div>

<!-- Revisão de uma passagem -->
<div class="modal fade" id="modalRevisao" tabindex="-1" aria-labelledby="tituloRevisao" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="tituloRevisao"><i class="bi bi-car-front me-1"></i>Revisar passagem</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
            </div>
            <div class="modal-body">
                <div class="row g-3">
                    <div class="col-lg-7">
                        <div id="rvImagens" class="rv-imagens"></div>
                    </div>
                    <div class="col-lg-5">
                        <p class="small mb-1" id="rvCabecalho"></p>
                        <div class="alert alert-warning py-1 small d-none" id="rvMotivo"></div>
                        <h6 class="mt-2">Placas lidas</h6>
                        <table class="table table-sm mb-2" id="rvLeituras">
                            <thead class="table-light"><tr><th>Origem</th><th>Tipo</th><th>Placa</th><th class="text-end">Score</th></tr></thead>
                            <tbody></tbody>
                        </table>
                        <div class="row g-2">
                            <div class="col-6">
                                <label for="rvPlaca" class="form-label">Placa (tração)</label>
                                <input type="text" id="rvPlaca" class="form-control form-control-sm text-uppercase" maxlength="7">
                            </div>
                            <div class="col-6">
                                <label for="rvFrontal" class="form-label">Placa frontal</label>
                                <input type="text" id="rvFrontal" class="form-control form-control-sm text-uppercase" maxlength="7">
                            </div>
                        </div>
                        <div class="mt-2">
                            <div class="d-flex justify-content-between align-items-center">
                                <label class="form-label mb-0">Placas adicionais</label>
                                <button type="button" class="btn btn-outline-primary btn-sm" id="rvAdicionar" title="Adicionar placa de reboque ou semirreboque">
                                    <i class="bi bi-plus"></i> Adicionar</button>
                            </div>
                            <div id="rvAdicionais" class="mt-1"></div>
                        </div>
                        <div class="mt-2">
                            <label for="rvMotivoCorrecao" class="form-label">Motivo <span class="text-danger">*</span></label>
                            <select id="rvMotivoCorrecao" class="form-select form-select-sm"></select>
                        </div>
                        <div class="mt-2">
                            <label for="rvJustificativa" class="form-label">Justificativa <span id="rvObrig" class="text-danger d-none">*</span></label>
                            <textarea id="rvJustificativa" class="form-control form-control-sm" rows="2" maxlength="500"></textarea>
                        </div>
                        <h6 class="mt-3">Histórico de correções</h6>
                        <div class="table-responsive">
                            <table class="table table-sm mb-0" id="rvHistorico">
                                <thead class="table-light"><tr><th>Data</th><th>De → para</th><th>Usuário</th><th>Motivo</th></tr></thead>
                                <tbody></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                <button type="button" class="btn btn-success" id="rvConferir" title="Leitura correta, sem alteração"><i class="bi bi-check2-circle"></i> Conferir</button>
                <button type="button" class="btn btn-primary" id="rvSalvar"><i class="bi bi-pen"></i> Salvar correção</button>
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
<script src="assets/js/revisao-placas.js"></script>
