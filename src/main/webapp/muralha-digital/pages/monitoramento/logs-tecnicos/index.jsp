<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<link rel="stylesheet" href="assets/css/logs-tecnicos.css">

<div class="container-fluid" id="telaLogsTecnicos">
    <div class="row gy-3 mt-1">
        <div class="col-sm-12"><h2 class="text-center"><strong>Logs Técnicos</strong></h2></div>
    </div>

    <%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %>
    <div id="avisoFontes" class="alert alert-warning d-none" role="alert"></div>

    <ul class="nav nav-tabs mb-3" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active" data-bs-toggle="tab" data-bs-target="#abaEventos" type="button" role="tab">
                <i class="bi bi-list-ul me-1"></i>Eventos</button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" data-bs-toggle="tab" data-bs-target="#abaLinhaTempo" type="button" role="tab">
                <i class="bi bi-clock-history me-1"></i>Linha do tempo</button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" data-bs-toggle="tab" data-bs-target="#abaRelatorio" type="button" role="tab">
                <i class="bi bi-file-earmark-text me-1"></i>Relatório técnico</button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link" data-bs-toggle="tab" data-bs-target="#abaParametros" type="button" role="tab">
                <i class="bi bi-sliders me-1"></i>Histórico de parâmetros</button>
        </li>
    </ul>

    <div class="tab-content">
        <!-- Eventos -->
        <div class="tab-pane fade show active" id="abaEventos" role="tabpanel">
            <div class="card mb-3"><div class="card-body">
                <div class="row gy-3 align-items-end">
                    <div class="col-sm-2">
                        <label for="evDtInicio" class="form-label">Data início</label>
                        <input type="date" id="evDtInicio" class="form-control form-control-sm">
                    </div>
                    <div class="col-sm-2">
                        <label for="evDtFim" class="form-label">Data fim</label>
                        <input type="date" id="evDtFim" class="form-control form-control-sm">
                    </div>
                    <div class="col-sm-4">
                        <label for="evEquipamentos" class="form-label">Equipamentos</label>
                        <select id="evEquipamentos" class="selectpicker form-control border" multiple data-live-search="true"
                                data-actions-box="true" data-style="btn-white" data-size="10" data-none-selected-text="--Todos--"
                                data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"></select>
                    </div>
                    <div class="col-sm-4">
                        <label for="evLocalidades" class="form-label">Localidades</label>
                        <select id="evLocalidades" class="selectpicker form-control border" multiple data-live-search="true"
                                data-actions-box="true" data-style="btn-white" data-size="10" data-none-selected-text="--Todas--"
                                data-select-all-text="Marcar todas" data-deselect-all-text="Desmarcar todas"></select>
                    </div>
                    <div class="col-sm-4">
                        <label for="evCategorias" class="form-label">Categorias</label>
                        <select id="evCategorias" class="selectpicker form-control border" multiple data-actions-box="true"
                                data-style="btn-white" data-size="10" data-none-selected-text="--Todas--"
                                data-select-all-text="Marcar todas" data-deselect-all-text="Desmarcar todas"></select>
                    </div>
                    <div class="col-sm-3">
                        <label for="evSeveridades" class="form-label">Severidades</label>
                        <select id="evSeveridades" class="selectpicker form-control border" multiple data-style="btn-white"
                                data-none-selected-text="--Todas--"></select>
                    </div>
                    <div class="col-sm-5 btn-toolbar-acoes">
                        <button type="button" class="btn btn-primary btn-sm" id="btnEvPesquisar" title="Pesquisar">
                            <i class="bi bi-search"></i> Pesquisar</button>
                        <button type="button" class="btn btn-outline-secondary btn-sm" id="btnEvLimpar" title="Limpar filtros">
                            <i class="bi bi-eraser"></i> Limpar</button>
                        <button type="button" class="btn btn-outline-secondary btn-sm" id="btnEvCsv" title="CSV completo com SHA-256">
                            <i class="bi bi-filetype-csv"></i> CSV</button>
                        <button type="button" class="btn btn-success btn-sm" id="btnEvXls" title="Exportar Excel (página atual)" aria-label="Exportar Excel">
                            <i class="bi bi-file-earmark-excel"></i></button>
                        <button type="button" class="btn btn-danger btn-sm" id="btnEvPdf" title="Exportar PDF (página atual)" aria-label="Exportar PDF">
                            <i class="bi bi-file-earmark-pdf"></i></button>
                    </div>
                </div>
            </div></div>

            <div class="card mb-4">
                <div class="card-header"><i class="bi bi-list-ul me-1"></i>Eventos <small class="text-muted" id="lblEvTotal"></small></div>
                <div class="card-body p-0"><div class="table-responsive">
                    <table class="table table-hover table-sm mb-0" id="tblEventos">
                        <thead class="table-light"><tr>
                            <th>Data/hora</th><th>Equipamento</th><th>Origem</th><th>Categoria</th><th>Severidade</th>
                            <th>Descrição</th><th>Usuário</th><th class="no-export">Correlação</th>
                        </tr></thead>
                        <tbody><tr><td colspan="8" class="text-center text-muted">Informe o período e clique em Pesquisar.</td></tr></tbody>
                    </table>
                </div></div>
                <div class="card-footer d-flex justify-content-between align-items-center">
                    <button type="button" class="btn btn-outline-secondary btn-sm" id="btnEvAnterior" disabled><i class="bi bi-chevron-left"></i> Anterior</button>
                    <span class="small text-muted" id="lblEvPagina"></span>
                    <button type="button" class="btn btn-outline-secondary btn-sm" id="btnEvSeguinte" disabled>Próxima <i class="bi bi-chevron-right"></i></button>
                </div>
            </div>
        </div>

        <!-- Linha do tempo -->
        <div class="tab-pane fade" id="abaLinhaTempo" role="tabpanel">
            <div class="card mb-3"><div class="card-body">
                <div class="row gy-3 align-items-end">
                    <div class="col-sm-4">
                        <label for="ltEquipamento" class="form-label">Equipamento</label>
                        <select id="ltEquipamento" class="selectpicker form-control border" data-live-search="true"
                                data-style="btn-white" data-size="10"></select>
                    </div>
                    <div class="col-sm-2">
                        <label for="ltDtInicio" class="form-label">Data início</label>
                        <input type="date" id="ltDtInicio" class="form-control form-control-sm">
                    </div>
                    <div class="col-sm-2">
                        <label for="ltDtFim" class="form-label">Data fim</label>
                        <input type="date" id="ltDtFim" class="form-control form-control-sm">
                    </div>
                    <div class="col-sm-4 btn-toolbar-acoes">
                        <button type="button" class="btn btn-primary btn-sm" id="btnLtGerar" title="Gerar linha do tempo">
                            <i class="bi bi-clock-history"></i> Gerar</button>
                    </div>
                </div>
            </div></div>
            <div class="card mb-3">
                <div class="card-header"><i class="bi bi-clock-history me-1"></i>Linha do tempo
                    <small class="text-muted">— barras: períodos (início → fim); pontos: eventos. Clique num ponto para ver os eventos de todos os equipamentos no mesmo intervalo.</small></div>
                <div class="card-body" id="linhaTempo"><p class="text-muted text-center mb-0">Selecione um equipamento e o período.</p></div>
            </div>
            <div class="card mb-4">
                <div class="card-header"><i class="bi bi-hourglass-split me-1"></i>Períodos</div>
                <div class="card-body p-0"><div class="table-responsive">
                    <table class="table table-hover table-sm mb-0" id="tblPeriodos">
                        <thead class="table-light"><tr><th>Grupo</th><th>Origem</th><th>Início</th><th>Fim</th><th class="text-end">Duração (min)</th><th>Descrição</th></tr></thead>
                        <tbody><tr><td colspan="6" class="text-center text-muted">—</td></tr></tbody>
                    </table>
                </div></div>
            </div>
        </div>

        <!-- Relatório técnico -->
        <div class="tab-pane fade" id="abaRelatorio" role="tabpanel">
            <div class="card mb-3"><div class="card-body">
                <div class="row gy-3 align-items-end">
                    <div class="col-sm-4">
                        <label for="rtEquipamento" class="form-label">Equipamento</label>
                        <select id="rtEquipamento" class="selectpicker form-control border" data-live-search="true"
                                data-style="btn-white" data-size="10"></select>
                    </div>
                    <div class="col-sm-2">
                        <label for="rtDtInicio" class="form-label">Data início</label>
                        <input type="date" id="rtDtInicio" class="form-control form-control-sm">
                    </div>
                    <div class="col-sm-2">
                        <label for="rtDtFim" class="form-label">Data fim</label>
                        <input type="date" id="rtDtFim" class="form-control form-control-sm">
                    </div>
                    <div class="col-sm-4 btn-toolbar-acoes">
                        <button type="button" class="btn btn-primary btn-sm" id="btnRtGerar" title="Gerar relatório">
                            <i class="bi bi-file-earmark-text"></i> Gerar</button>
                        <button type="button" class="btn btn-danger btn-sm" id="btnRtPdf" title="Exportar PDF" disabled>
                            <i class="bi bi-file-earmark-pdf"></i> PDF</button>
                    </div>
                </div>
            </div></div>
            <div id="relatorio" class="mb-4"><p class="text-muted text-center">Selecione um equipamento e o período.</p></div>
        </div>

        <!-- Histórico de parâmetros -->
        <div class="tab-pane fade" id="abaParametros" role="tabpanel">
            <div class="card mb-3"><div class="card-body">
                <div class="row gy-3 align-items-end">
                    <div class="col-sm-2">
                        <label for="phDtInicio" class="form-label">Data início</label>
                        <input type="date" id="phDtInicio" class="form-control form-control-sm">
                    </div>
                    <div class="col-sm-2">
                        <label for="phDtFim" class="form-label">Data fim</label>
                        <input type="date" id="phDtFim" class="form-control form-control-sm">
                    </div>
                    <div class="col-sm-2">
                        <label for="phTipo" class="form-label">Recurso</label>
                        <select id="phTipo" class="form-select form-select-sm">
                            <option value="">Todos</option>
                            <option value="EQUIPAMENTO">Equipamento</option>
                            <option value="CONFIG_CHAVE_VALOR">Configuração</option>
                            <option value="KPI">KPI</option>
                        </select>
                    </div>
                    <div class="col-sm-2">
                        <label for="phIdRecurso" class="form-label">Identificador</label>
                        <input type="text" id="phIdRecurso" class="form-control form-control-sm" maxlength="100" placeholder="id do local, chave…">
                    </div>
                    <div class="col-sm-2">
                        <label for="phParametro" class="form-label">Parâmetro contém</label>
                        <input type="text" id="phParametro" class="form-control form-control-sm" maxlength="100">
                    </div>
                    <div class="col-sm-2 btn-toolbar-acoes">
                        <button type="button" class="btn btn-primary btn-sm" id="btnPhPesquisar" title="Pesquisar"><i class="bi bi-search"></i></button>
                        <button type="button" class="btn btn-success btn-sm" id="btnPhXls" title="Exportar Excel (página atual)" aria-label="Exportar Excel"><i class="bi bi-file-earmark-excel"></i></button>
                        <button type="button" class="btn btn-danger btn-sm" id="btnPhPdf" title="Exportar PDF (página atual)" aria-label="Exportar PDF"><i class="bi bi-file-earmark-pdf"></i></button>
                    </div>
                </div>
            </div></div>
            <div class="card mb-4">
                <div class="card-header"><i class="bi bi-sliders me-1"></i>Alterações <small class="text-muted" id="lblPhTotal"></small></div>
                <div class="card-body p-0"><div class="table-responsive">
                    <table class="table table-hover table-sm mb-0" id="tblParametros">
                        <thead class="table-light"><tr><th>Data/hora</th><th>Recurso</th><th>Identificador</th><th>Parâmetro</th>
                            <th>Valor anterior</th><th>Valor novo</th><th>Usuário</th><th>IP</th><th>Origem</th></tr></thead>
                        <tbody><tr><td colspan="9" class="text-center text-muted">Informe o período e clique em Pesquisar.</td></tr></tbody>
                    </table>
                </div></div>
                <div class="card-footer d-flex justify-content-between align-items-center">
                    <button type="button" class="btn btn-outline-secondary btn-sm" id="btnPhAnterior" disabled><i class="bi bi-chevron-left"></i> Anterior</button>
                    <span class="small text-muted" id="lblPhPagina"></span>
                    <button type="button" class="btn btn-outline-secondary btn-sm" id="btnPhSeguinte" disabled>Próxima <i class="bi bi-chevron-right"></i></button>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Correlação temporal -->
<div class="modal fade" id="modalCorrelacao" tabindex="-1" aria-labelledby="tituloCorrelacao" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="tituloCorrelacao"><i class="bi bi-diagram-3 me-1"></i>Eventos no mesmo intervalo</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
            </div>
            <div class="modal-body">
                <p class="small text-muted" id="lblCorrelacao"></p>
                <div class="table-responsive">
                    <table class="table table-hover table-sm mb-0" id="tblCorrelacao">
                        <thead class="table-light"><tr><th>Data/hora</th><th>Equipamento</th><th>Origem</th><th>Categoria</th><th>Severidade</th><th>Descrição</th></tr></thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
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
<script src="assets/js/logs-tecnicos.js"></script>
