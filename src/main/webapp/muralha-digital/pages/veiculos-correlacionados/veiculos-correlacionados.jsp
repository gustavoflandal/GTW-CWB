<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>Correlação de Veículos</title>

    <link rel="icon" href="data:,">

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" />

    <link rel="stylesheet" href="css/style.css" />
</head>

<body>
    <div class="container py-4">
        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">Análise de Correlacionamento Veicular</h5>
                <button type="button" id="btn-info-regra-correlacionamento" class="btn btn-link text-primary p-0 border-0 d-flex align-items-center" data-bs-toggle="modal" data-bs-target="#modalRegraCorrelacionamento" aria-label="Informações da regra de correlacionamento" title="Informações da regra de correlacionamento">
                    <i class="bi bi-info-circle-fill fs-5"></i>
                </button>
            </div>
            <div class="card-body">
                <form class="g-3">
                    <div class="row align-items-end">
                        <div class="col-md-4">
                            <label for="txt_data_inicio" class="form-label fw-bold">Data Inicial</label>
                            <div class="input-group">
                                <input id="txt_data_inicio" name="data_inicio" type="date" class="form-control form-control-lg" oninput="verificarCampoTemporalAsync(this)" onblur="ajustarPeriodoDataHoraAsync();" />
                                <input id="txt_hora_inicio" name="hora_inicio" type="time" class="form-control form-control-lg" oninput="verificarCampoTemporalAsync(this)" onblur="ajustarPeriodoDataHoraAsync();" />
                            </div>
                        </div>
                        <div class="col-md-4">
                            <label for="txt_data_fim" class="form-label fw-bold">Data Final</label>
                            <div class="input-group">
                                <input id="txt_data_fim" name="data_fim" type="date" class="form-control form-control-lg" oninput="verificarCampoTemporalAsync(this)" onblur="ajustarPeriodoDataHoraAsync();" />
                                <input id="txt_hora_fim" name="hora_fim" type="time" class="form-control form-control-lg" oninput="verificarCampoTemporalAsync(this)" onblur="ajustarPeriodoDataHoraAsync();" />
                            </div>
                        </div>
                        <div class="col-md-4">
                            <label for="intervalo" class="form-label fw-bold">Intervalo</label>
                            <select id="intervalo" class="selectpicker form-control border" data-style="btn-white" onchange="aplicarIntervaloRapidoAsync(this.value)">
                                <option value="">Selecionar...</option>
                                <option value="7" selected>Última semana</option>
                                <option value="14">Últimas 2 semanas</option>
                                <option value="21">Últimas 3 semanas</option>
                                <option value="30">Último mês</option>
                                <option value="60">Últimos 2 meses</option>
                                <option value="90">Últimos 3 meses</option>
                                <option value="365">Últimos 12 meses</option>
                            </select>
                        </div>
                    </div>

                    <div class="row align-items-end mt-3">
                        <div class="col-md-2">
                            <label for="txt_placa_1" class="form-label fw-bold">Placa 1</label>
                            <input id="txt_placa_1" name="placa_1" type="text" class="form-control" maxlength="8" oninput="verificaDigitoPlacaAsync(this)" onkeypress="verifacaTeclaAsync(event)" placeholder="Placa 1" />
                        </div>
                        <div class="col-md-2">
                            <label for="txt_placa_2" class="form-label fw-bold">Placa 2 (Opcional)</label>
                            <input id="txt_placa_2" name="placa_2" type="text" class="form-control" maxlength="8" oninput="verificaDigitoPlacaAsync(this)" onkeypress="verifacaTeclaAsync(event)" placeholder="Placa 2" />
                        </div>
                        <div class="col-md-2">
                            <label for="txt_placa_3" class="form-label fw-bold">Placa 3 (Opcional)</label>
                            <input id="txt_placa_3" name="placa_3" type="text" class="form-control" maxlength="8" oninput="verificaDigitoPlacaAsync(this)" onkeypress="verifacaTeclaAsync(event)" placeholder="Placa 3" />
                        </div>
                        <div class="col-md-3">
                            <label for="txt_min_correlacoes" class="form-label fw-bold">Nº min. de passagens correlacionadas</label>
                            <input id="txt_min_correlacoes" name="min_correlacoes" type="number" class="form-control" value="3" min="1" oninput="onInputMinCorrelacoes()" onblur="validarMinCorrelacoes(this)" placeholder="Nº" style="max-width: 70px;" />
                        </div>
                        <div class="col-md-3 d-flex gap-2">
                            <button type="button" class="btn btn-warning w-100" onclick="limparFormularioPrincipal()">Limpar Buscas</button>
                            <button type="button" class="btn btn-primary w-100" onclick="executarAnalise()">Analisar</button>
                        </div>
                    </div>

                    <div id="aviso-limite-periodo" class="alert alert-warning mt-2 py-2 small" style="display:none;" role="alert"></div>
                </form>
            </div>
        </div>

        <div id="containerDeFiltros" class="filtros-container card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">Filtros Avançados</h5>
                <button type="button" class="btn btn-secondary" onclick="limparFiltrosAvancados()" title="Limpar todos os filtros avançados">
                    <i class="bi bi-eraser"></i> Limpar filtros
                </button>
            </div>
            <div class="card-body">
                <div class="filtros-grid">
                    <div class="filtro-card">
                        <label for="filtro-qtd-passagens">Por Qtd. Passagens</label>
                        <input type="number" id="filtro-qtd-passagens" class="form-control" placeholder="Digite a quantidade">
                    </div>
                    <div class="filtro-card">
                        <label for="filtro-tipo-veiculo">Por Tipo Veículo</label>
                        <select id="filtro-tipo-veiculo" class="selectpicker form-control border" multiple
                                data-style="btn-white" data-size="8" data-none-selected-text="--Selecione os tipos--"
                                data-actions-box="true" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos">
                        </select>
                    </div>
                    
                    <div class="filtro-card">
                        <label for="filtro-periodo-predominante">Por Período Predominante</label>
                        <select id="filtro-periodo-predominante" class="selectpicker form-control border" data-style="btn-white">
                            <option value="">Todos</option>
                            <option value="Diurno">Diurno</option>
                            <option value="Noturno">Noturno</option>
                        </select>
                    </div>
                    <div class="filtro-card">
                        <label for="filtro-registro-fato">Por Registro de fato</label>
                        <select id="filtro-registro-fato" class="selectpicker form-control border" multiple
                                data-style="btn-white" data-size="8" data-none-selected-text="--Selecione os tipos--"
                                data-actions-box="true" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos">
                        </select>
                    </div>
                    <div class="filtro-card">
                        <label for="filtro-alerta-ocorrencia">Por Alerta</label>
                        <select id="filtro-alerta-ocorrencia" class="selectpicker form-control border" multiple
                                data-style="btn-white" data-size="8" data-none-selected-text="--Selecione os alertas--"
                                data-actions-box="true" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos">
                        </select>
                    </div>
                    <div class="filtro-card">
                        <label for="filtro-mancha">Por mancha</label>
                        <select id="filtro-mancha" class="selectpicker form-control border" multiple
                                data-style="btn-white" data-size="8" data-none-selected-text="--Selecione as manchas--"
                                data-actions-box="true" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos">
                        </select>
                    </div>
                    <div class="filtro-card">
                        <label for="filtro-pcl-mancha">Por PCL da mancha</label>
                        <select id="filtro-pcl-mancha" class="selectpicker form-control border" multiple
                                data-style="btn-white" data-size="8" data-none-selected-text="--Selecione os PCLs--"
                                data-actions-box="true" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos">
                        </select>
                    </div>
                    <div class="filtro-card">
                        <label for="filtro-tempo-permanencia">Por tempo de permanência</label>
                        <input type="text" id="filtro-tempo-permanencia" class="form-control" placeholder="Ex: 30 minutos">
                    </div>
                    <div class="filtro-card">
                        <label for="filtro-nivel-correlacao">Por Nível de Correlação</label>
                        <select id="filtro-nivel-correlacao" class="selectpicker form-control border" multiple
                                data-style="btn-white" data-size="8" data-none-selected-text="Todos"
                                data-actions-box="true" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos">
                            <option value="Alta">Alta</option>
                            <option value="Média">Média</option>
                            <option value="Baixa">Baixa</option>
                            <option value="Sem correlacionamento">Sem correlacionamento</option>
                            <option value="Sem OCR">Sem OCR</option>
                            <option value="Sem OCR Registro de fato">Sem OCR Registro de fato</option>
                        </select>
                    </div>

                    <div class="filtro-card">
                        <label for="filtro-placas-selecionadas">Por placas</label>
                        <select id="filtro-placas-selecionadas" class="selectpicker form-control border" multiple
                                data-style="btn-white" data-size="10" data-none-selected-text="Todas as placas"
                                data-actions-box="true" data-select-all-text="Marcar todas" data-deselect-all-text="Desmarcar todas"
                                data-live-search="true" data-live-search-placeholder="Buscar placa...">
                        </select>
                    </div>
                </div>
            </div>
        </div>

        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0">Visualização de Correlação</h5>
            </div>
            <div class="card-body pb-2">
                <div class="d-flex justify-content-between align-items-center flex-wrap mb-3">
                    <span>
                        <span id="contador-placas-correlacionadas" class="text-primary fw-bold">0</span> placas correlacionadas
                        <span id="contador-placas-sem-correlacao" class="ms-2">
                            | <span id="contador-placas-sem-correlacao-valor" class="fw-bold text-warning">0</span> placas sem correlacionamentos
                        </span>
                    </span>
                    <div class="d-flex align-items-center gap-2">
                        <label class="form-check-label" for="chk-correlacao-comum">
                            Apenas placas em comum
                            <i class="bi bi-info-circle" data-bs-toggle="tooltip" data-bs-placement="top" 
                               title="Exibe apenas veículos que correlacionam com TODAS as placas alvo pesquisadas."></i>
                        </label>
                        <div class="form-check form-switch m-0">
                            <input class="form-check-input" type="checkbox" id="chk-correlacao-comum">
                        </div>
                    </div>
                </div>
            <div id="aviso-placa-alvo-correlacao" class="alert alert-info mx-0 mt-2 mb-0 py-2 small" style="display:none;" role="alert">
                <i class="bi bi-info-circle-fill me-1"></i> <strong>Placa(s) alvo:</strong> mantida(s) no resultado por ter correlação ativa com o filtro aplicado.
            </div>
            <div class="card-body">
                <div id="grafo-loading" style="display: none;">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                    <strong class="ms-3">Analisando correlações...</strong>
                </div>
                <div id="grafo-container">
                    <div id="grafo"></div>
                    <div id="controls">
                        <button class="control-button" onclick="resetViewAsync()">Centralizar</button>
                        <button class="control-button" onclick="zoomInAsync()">Zoom +</button>
                        <button class="control-button" onclick="zoomOutAsync()">Zoom -</button>
                        <button class="control-button" onclick="recarregarGrafo()">Recarregar</button>
                    </div>
                </div>
            </div>
        </div>

        <div id="loading-overlay" style="display: none;">
            <div class="loading-spinner"></div>
            <span id="loading-progress-text"></span>
        </div>

        <div class="modal fade" id="modalRegraCorrelacionamento" tabindex="-1" aria-labelledby="modalRegraCorrelacionamentoLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalRegraCorrelacionamentoLabel">
                            <i class="bi bi-info-circle-fill text-primary me-2"></i>Regra de Correlacionamento
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
                    </div>
                    <div class="modal-body">
                        <div id="conteudo-modal-regra-correlacionamento"></div>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="modalInfoVeiculo" tabindex="-1" aria-labelledby="modalInfoVeiculoLabel" aria-hidden="true">
            <div class="modal-dialog modal-xl">
                <div class="modal-content" style="position: relative;">
                    <div id="modal-loading-overlay" class="modal-loading-overlay" style="display: none;">
                        <div class="loading-spinner"></div>
                    </div>
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalInfoVeiculoLabel">Informações do Veículo</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3 p-3 border rounded bg-light">
                            <div class="row g-3 align-items-center">
                                <div class="col-md-3 text-center">
                                    <div class="border rounded p-2" style="background: #e9ecef; height: 160px; display: flex; align-items: center; justify-content: center;">
                                        <img id="modal-imagem-veiculo" src="" class="img-fluid" alt="Imagem do veículo" style="max-height: 150px; max-width: 100%; object-fit: contain; display: none;" />
                                        <p id="modal-imagem-placeholder" class="text-muted mb-0"><i class="bi bi-camera" style="font-size: 2rem;"></i><br><small>Sem imagem</small></p>
                                    </div>
                                </div>
                                <div class="col-md-5">
                                    <h6 class="border-bottom pb-1 mb-2"><i class="bi bi-car-front"></i> Dados do Veículo</h6>
                                    <div class="row">
                                        <div class="col-6">
                                            <p class="mb-1"><strong>Placa:</strong> <span id="modal-placa" class="badge bg-dark"></span></p>
                                            <p class="mb-1"><strong>Cor:</strong> <span id="modal-cor"></span></p>
                                            <p class="mb-0"><strong>Ano:</strong> <span id="modal-ano"></span></p>
                                        </div>
                                        <div class="col-6">
                                            <p class="mb-1"><strong>Marca:</strong> <span id="modal-marca"></span></p>
                                            <p class="mb-1"><strong>Modelo:</strong> <span id="modal-modelo"></span></p>
                                        </div>
                                    </div>

                                </div>
                                <div class="col-md-4">
                                    <h6 class="border-bottom pb-1 mb-2 d-flex justify-content-between align-items-center">
                                        <span><i class="bi bi-diagram-3"></i> Correlações</span>
                                        <span class="badge bg-primary" id="badge-total-passagens">0</span>
                                    </h6>
                                    <div id="correlacoes-lista-container" style="font-size: 0.9rem;">
                                        <div id="tabela-correlacoes-placa-body"></div>
                                    </div>
                                    <p id="modal-correlacoes-placeholder" class="text-muted text-center mb-0 small" style="display: none;">Placa alvo (sem correlações)</p>
                                </div>
                            </div>
                        </div>

                        <div id="modal-explicacao-sem-placa" class="alert alert-info mt-3" style="display: none;">
                            <h6 class="alert-heading"><i class="bi bi-info-circle"></i> O que são "Passagens sem leitura de placa"?</h6>
                            <p class="mb-2">Este resultado agrupa todas as passagens veiculares que <strong>não tiveram identificação de placa</strong>, mas que foram detectadas nos mesmos locais e períodos próximos à placa pesquisada.</p>
                        </div>

                        <div id="modal-secao-sem-ocr-registro-fato" class="alert alert-warning border-start border-warning border-5 mt-3" style="display: none;">
                            <div class="d-flex align-items-start">
                                <div class="me-3" style="font-size: 1.5rem;"><i class="bi bi-exclamation-triangle-fill"></i></div>
                                <div>
                                    <h6 class="alert-heading mb-1"><strong>Registro de Fato Sem OCR</strong></h6>
                                    <p class="mb-0">Existem <strong>Registros de Fato</strong> vinculados a passagens <strong>sem imagem</strong> (sem OCR) neste período. Verifique a aba <strong>Registro de Fato</strong> para mais detalhes.</p>
                                </div>
                            </div>
                        </div>

                        <ul class="nav nav-tabs" id="infoVeiculoTab" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="passagens-tab" data-bs-toggle="tab" data-bs-target="#passagens-tab-pane" type="button" role="tab" aria-controls="passagens-tab-pane" aria-selected="true">Passagens</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="alertas-tab" data-bs-toggle="tab" data-bs-target="#alertas-tab-pane" type="button" role="tab" aria-controls="alertas-tab-pane" aria-selected="false">Alertas</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="boletimOcorrencia-tab" data-bs-toggle="tab" data-bs-target="#boletimOcorrencia-tab-pane" type="button" role="tab" aria-controls="boletimOcorrencia-tab-pane" aria-selected="false">Registro de Fato</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="proprietario-tab" data-bs-toggle="tab" data-bs-target="#proprietario-tab-pane" type="button" role="tab" aria-controls="proprietario-tab-pane" aria-selected="false">Proprietário</button>
                            </li>
                        </ul>

                        <div class="tab-content pt-3" id="infoVeiculoTabContent">
                            <div class="tab-pane fade" id="alertas-tab-pane" role="tabpanel" aria-labelledby="alertas-tab" tabindex="0">
                                <div class="alertas-tabela-container">
                                    <div class="tabela-scroll">
                                        <table class="table table-bordered table-hover table-striped align-middle mb-0">
                                            <thead>
                                                <tr>
                                                    <th>Data</th>
                                                    <th>Tipo</th>
                                                    <th>Status</th>
                                                    <th>Origem</th>
                                                    <th>Link</th>
                                                </tr>
                                            </thead>
                                            <tbody id="tabela-alertas-body"></tbody>
                                        </table>
                                    </div>
                                    <div class="paginacao-alertas text-center mt-2">
                                        <button id="pagina-anterior" class="btn btn-outline-secondary btn-sm">&laquo; Anterior</button>
                                        <span class="fw-bold mx-2">Página <span id="pagina-atual">1</span> de <span id="total-paginas">1</span></span>
                                        <button id="pagina-proxima" class="btn btn-outline-secondary btn-sm">Próxima &raquo;</button>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane fade" id="boletimOcorrencia-tab-pane" role="tabpanel" aria-labelledby="boletimOcorrencia-tab" tabindex="0">
                                <div id="badges-registro-fato" class="mb-2" style="display: none;"></div>
                                <div class="boletim-tabela-container">
                                    <div class="tabela-scroll">
                                        <table class="table table-bordered table-hover table-striped align-middle mb-0">
                                            <thead>
                                                <tr>
                                                    <th>Data</th>
                                                    <th>Tipo da Ocorrência</th>
                                                    <th>Situação</th>
                                                    <th>Descrição</th>
                                                    <th class="text-center">Ver Registro</th>
                                                    <th class="text-center">Alerta</th>
                                                </tr>
                                            </thead>
                                            <tbody id="tabela-boletim-body"></tbody>
                                        </table>
                                    </div>
                                    <div class="paginacao-boletim text-center mt-2">
                                        <button id="boletim-pagina-anterior" class="btn btn-outline-secondary btn-sm">&laquo; Anterior</button>
                                        <span class="fw-bold mx-2">Página <span id="boletim-pagina-atual">1</span> de <span id="boletim-total-paginas">1</span></span>
                                        <button id="boletim-pagina-proxima" class="btn btn-outline-secondary btn-sm">Próxima &raquo;</button>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane fade" id="proprietario-tab-pane" role="tabpanel" aria-labelledby="proprietario-tab" tabindex="0">
                                <div class="proprietario-info-container">
                                    <p><strong>Nome:</strong> <span id="modal-nome-proprietario"></span> <span id="modal-sobrenome-proprietario"></span></p>
                                    <p><strong>CPF:</strong> <span id="modal-cpf-proprietario"></span></p>
                                    <p><strong>Data de Nascimento:</strong> <span id="modal-data-nascimento-proprietario"></span></p>
                                    <p><strong>Endereço:</strong> <span id="modal-endereco-proprietario"></span></p>
                                    <p><strong>Telefone:</strong> <span id="modal-telefone-proprietario"></span></p>
                                    <p><strong>Email:</strong> <span id="modal-email-proprietario"></span></p>
                                </div>
                                <div class="antecedentes-scroll-container">
                                    <h5 class="mt-4">Antecedentes Criminais</h5>
                                    <div class="table-responsive">
                                        <table class="table table-bordered table-striped align-middle">
                                            <thead>
                                                <tr>
                                                    <th>Tipo de Crime</th>
                                                    <th>Data</th>
                                                    <th>Local</th>
                                                    <th>Descrição</th>
                                                    <th>Sentença</th>
                                                </tr>
                                            </thead>
                                            <tbody id="tabela-antecedentes-body"></tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane fade" id="passagens-tab-pane" role="tabpanel" aria-labelledby="passagens-tab" tabindex="0">
                                <div class="passagens-container">
                                    <div class="d-flex justify-content-end align-items-center mb-2">
                                        <label class="form-check-label small text-muted me-2" for="chk-apenas-correlacionadas">Apenas correlacionadas</label>
                                        <div class="form-check form-switch m-0">
                                            <input class="form-check-input" type="checkbox" id="chk-apenas-correlacionadas">
                                        </div>
                                    </div>
                                    <div class="table-responsive" style="max-height: 350px; overflow-y: auto;">
                                        <table class="table table-bordered table-hover table-striped table-sm align-middle mb-0">
                                            <thead class="table-light sticky-top">
                                                <tr>
                                                    <th class="text-center" style="width: 65px;">#</th>
                                                    <th>Local</th>
                                                    <th class="text-center" style="width: 170px;">Data/Hora</th>
                                                    <th class="text-center" style="width: 80px;"></th>
                                                </tr>
                                            </thead>
                                            <tbody id="tabela-passagens-body"></tbody>
                                        </table>
                                    </div>
                                    <p id="modal-passagens-placeholder" class="text-muted text-center mt-2" style="display: none;">Nenhuma passagem encontrada no período</p>
                                    <div class="paginacao-passagens mt-2">
                                        <div class="text-center mb-1" id="passagens-paginacao-container">
                                        </div>
                                        <div class="text-center">
                                            <span class="text-muted small" id="passagens-info-paginacao">Exibindo 0 de 0 itens</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div id="modal-empty-message" class="text-center p-5" style="display: none;">
                            Não foram encontrados agravantes para esta placa no sistema.
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" id="btn-todos-boletins" class="btn btn-primary me-auto">Todos os Boletins</button>
                        <button type="button" id="btn-perfil-comportamental" class="btn btn-success">Perfil Comportamental</button>
                        <button type="button" id="btn-consultar-veiculo" class="btn btn-info">Consultar Veículo</button>
                        <button type="button" id="btn-remover-veiculo" class="btn btn-danger" data-placa="">Remover</button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Correção de Placa -->
        <div class="modal fade" id="modalCorrigirPlaca" tabindex="-1" aria-labelledby="modalCorrigirPlacaLabel" aria-hidden="true">
            <div class="modal-dialog modal-sm modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalCorrigirPlacaLabel"><i class="bi bi-pencil-square"></i> Corrigir Placa</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
                    </div>
                    <div class="modal-body">
                        <p class="mb-2 small text-muted">Placa atual: <strong id="corrigir-placa-atual-label">-</strong></p>
                        <label for="corrigir-nova-placa-input" class="form-label fw-semibold">Nova placa</label>
                        <input type="text" id="corrigir-nova-placa-input" class="form-control text-uppercase" maxlength="7" placeholder="Vazio = SEM PLACA">
                        <div id="corrigir-placa-erro" class="text-danger small mt-1" style="display:none;">Informe uma placa válida com 7 caracteres ou deixe vazio para SEM PLACA.</div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" id="btn-confirmar-correcao-placa" class="btn btn-success"><i class="bi bi-check-circle"></i> Salvar</button>
                    </div>
                </div>
            </div>
        </div>

        <div id="toast-container" style="position: fixed; top: 20px; right: 20px; z-index: 1080; display: flex; flex-direction: column; gap: 10px;"></div>

        <div id="link-modal" class="link-modal-container" style="display: none;">
            <div class="link-modal-content">
                <div class="link-modal-header">
                    <h5 id="link-modal-title">Detalhes</h5>
                    <button type="button" id="link-modal-close" class="btn-close" aria-label="Close"></button>
                </div>
                <div class="link-modal-body">
                    <iframe id="link-modal-iframe" src="about:blank" frameborder="0"></iframe>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js"></script>

  <script src="https://unpkg.com/cytoscape@3.24.0/dist/cytoscape.min.js"></script>

  <script src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>

  <script src="js/client.js"></script>
  <script src="js/utils.js"></script>
  <script src="js/filtros.js"></script>
  <script src="js/modal.js"></script>

  <script src="js/grafo/grafo-core.js"></script>
  <script src="js/grafo/grafo-filtros.js"></script>
  <script src="js/grafo/grafo-fetch.js"></script>
  <script src="js/grafo/grafo-zoom.js"></script>
  <script src="js/grafo/grafo-modal.js"></script>
  <jsp:include page="/muralha-digital/pages/registro_fato/comBoletim/modal/modal-registroDeFato.jsp" />
  <script>
        document.addEventListener('DOMContentLoaded', async function () {
            $('.selectpicker:visible').selectpicker();
            await inicializarConfiguracaoCorrelacaoAsync();
            await aplicarIntervaloRapidoAsync('7');
    });
  </script>
</body>
</html>

<%@ include file="/includes/rodape.jsp" %>