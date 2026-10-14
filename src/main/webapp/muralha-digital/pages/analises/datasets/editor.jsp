<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<style>
    .wizard-step { display: none; }
    .wizard-step.active { display: block; }
    .step-indicator { cursor: pointer; }
    .step-indicator.completed { color: #198754; }
    .step-indicator.active { font-weight: bold; color: #0d75bf; }
    .campo-item {
        border: 1px solid var(--bs-border-color);
        border-radius: 0.375rem;
        padding: 0.5rem 0.75rem;
        margin-bottom: 0.5rem;
        background: var(--bs-body-bg);
    }
    .campo-item.selecionado { border-color: #0d75bf; background: rgba(13,117,191,0.05); }
    .relacao-item {
        border: 1px solid var(--bs-border-color);
        border-radius: 0.375rem;
        padding: 0.5rem 0.75rem;
        margin-bottom: 0.5rem;
        cursor: pointer;
    }
    .relacao-item.incluida { border-color: #198754; background: rgba(25,135,84,0.05); }
    .preview-table { max-height: 400px; overflow: auto; }
    .campo-calculado-form { background: var(--bs-tertiary-bg); padding: 1rem; border-radius: 0.5rem; }
</style>

<div class="container py-3">
    <nav aria-label="breadcrumb" class="mb-2">
        <ol class="breadcrumb mb-0" style="font-size:0.85rem">
            <li class="breadcrumb-item"><a href="/muralha-digital/pages/analises/datasets/index.jsp"><i class="bi bi-database"></i> Datasets</a></li>
            <li class="breadcrumb-item active" id="breadcrumbDataset">Editor</li>
        </ol>
    </nav>
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="mb-0" id="tituloEditor">
            <i class="bi bi-database-add me-2"></i>Novo Dataset
        </h4>
        <div class="d-flex gap-1">
            <a href="/muralha-digital/pages/analises/datasets/modelador.jsp${not empty param.id ? '?id='.concat(param.id) : ''}"
               class="btn btn-outline-primary btn-sm" title="Modelador visual">
                <i class="bi bi-diagram-3"></i> Modelador
            </a>
            <a href="/muralha-digital/pages/analises/manual.jsp" target="_blank" class="btn btn-outline-info btn-sm" title="Manual do Dashboard Builder">
                <i class="bi bi-book"></i> Manual
            </a>
            <a href="/muralha-digital/pages/analises/datasets/index.jsp" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-arrow-left"></i> Voltar
            </a>
        </div>
    </div>

    <!-- Indicadores de passo -->
    <nav class="mb-4">
        <ol class="breadcrumb">
            <li class="breadcrumb-item step-indicator active" data-step="1">1. Tabela Base</li>
            <li class="breadcrumb-item step-indicator" data-step="2">2. Relacoes</li>
            <li class="breadcrumb-item step-indicator" data-step="3">3. Campos</li>
            <li class="breadcrumb-item step-indicator" data-step="4">4. Calculados</li>
            <li class="breadcrumb-item step-indicator" data-step="5">5. Preview</li>
            <li class="breadcrumb-item step-indicator" data-step="6">6. Publicar</li>
        </ol>
    </nav>

    <!-- Passo 1: Tabela Base -->
    <div class="wizard-step active" data-step="1">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Selecione a tabela base</h5>
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">Nome do dataset</label>
                        <input type="text" class="form-control" id="dsNome" placeholder="Ex: Passagens por Dia">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Descricao (opcional)</label>
                        <input type="text" class="form-control" id="dsDescricao" placeholder="Breve descricao">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Tabela base</label>
                        <select class="form-select" id="dsTabelaBase">
                            <option value="">Selecione...</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Visibilidade</label>
                        <select class="form-select" id="dsVisibilidade">
                            <option value="privado">Privado</option>
                            <option value="publico">Publico</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Passo 2: Relacoes -->
    <div class="wizard-step" data-step="2">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Tabelas relacionadas</h5>
                <p class="text-muted">Clique para incluir/excluir tabelas relacionadas no dataset.</p>
                <div id="listaRelacoes">
                    <div class="text-muted">Selecione uma tabela base primeiro.</div>
                </div>
            </div>
        </div>
    </div>

    <!-- Passo 3: Campos -->
    <div class="wizard-step" data-step="3">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Selecione os campos</h5>
                <p class="text-muted">Marque os campos e defina se sao dimensao ou medida.</p>
                <div id="listaCampos">
                    <div class="text-muted">Nenhum campo disponivel.</div>
                </div>
            </div>
        </div>
    </div>

    <!-- Passo 4: Campos calculados -->
    <div class="wizard-step" data-step="4">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Campos calculados (opcional)</h5>
                <div id="listaCamposCalculados"></div>
                <button class="btn btn-outline-primary btn-sm mt-2" id="btnNovoCampoCalc">
                    <i class="bi bi-plus-lg"></i> Adicionar campo calculado
                </button>

                <div class="campo-calculado-form mt-3" id="formCampoCalc" style="display:none;">
                    <div class="row g-2">
                        <div class="col-md-4">
                            <label class="form-label">Alias</label>
                            <input type="text" class="form-control form-control-sm" id="calcAlias" placeholder="meu_campo">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Rotulo</label>
                            <input type="text" class="form-control form-control-sm" id="calcRotulo" placeholder="Meu Campo">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Tipo</label>
                            <select class="form-select form-select-sm" id="calcTipo">
                                <option value="dimensao">Dimensao</option>
                                <option value="medida">Medida</option>
                            </select>
                        </div>
                        <div class="col-md-12">
                            <label class="form-label">Expressao</label>
                            <input type="text" class="form-control form-control-sm" id="calcExpressao"
                                   placeholder="CASE WHEN velocidade > 60 THEN 'alta' ELSE 'normal' END">
                            <div class="form-text" id="calcValidacao"></div>
                        </div>
                        <div class="col-md-12 text-end">
                            <button class="btn btn-sm btn-secondary" id="btnCancelarCalc">Cancelar</button>
                            <button class="btn btn-sm btn-primary" id="btnSalvarCalc">Adicionar</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Passo 5: Preview -->
    <div class="wizard-step" data-step="5">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">Pre-visualizacao (50 linhas)</h5>
                <button class="btn btn-outline-primary btn-sm mb-3" id="btnPreview">
                    <i class="bi bi-eye"></i> Carregar preview
                </button>
                <div class="preview-table" id="previewArea">
                    <div class="text-muted text-center p-3">Clique em "Carregar preview" para ver dados de exemplo.</div>
                </div>
            </div>
        </div>
    </div>

    <!-- Passo 6: Publicar -->
    <div class="wizard-step" data-step="6">
        <div class="card">
            <div class="card-body text-center">
                <h5 class="card-title">Pronto para publicar</h5>
                <div class="mb-3" id="resumoDataset"></div>
                <button class="btn btn-success btn-lg" id="btnPublicar">
                    <i class="bi bi-upload"></i> Publicar Dataset
                </button>
                <button class="btn btn-outline-secondary btn-lg ms-2" id="btnSalvarRascunho">
                    <i class="bi bi-save"></i> Salvar Rascunho
                </button>
            </div>
        </div>
    </div>

    <!-- Navegacao -->
    <div class="d-flex justify-content-between mt-3">
        <button class="btn btn-outline-secondary" id="btnAnterior" style="display:none;">
            <i class="bi bi-arrow-left"></i> Anterior
        </button>
        <button class="btn btn-primary ms-auto" id="btnProximo">
            Proximo <i class="bi bi-arrow-right"></i>
        </button>
    </div>
</div>

<script type="module" src="/static/js/dashboards/dataset-editor.js"></script>
