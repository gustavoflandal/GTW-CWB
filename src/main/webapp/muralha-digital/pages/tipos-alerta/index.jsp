<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<link rel="stylesheet" href="/muralha-digital/pages/tipos-alerta/assets/css/tipos-alerta.css">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<div class="container-fluid" id="telaTiposAlerta">
    <div class="row gy-3 mt-1">
        <div class="col-sm-12">
            <h2 class="text-center"><strong>Tipos de Alerta — Criticidade, Cores e Tratamentos</strong></h2>
        </div>
    </div>

    <div class="alert alert-info d-flex gap-2 align-items-start" role="note">
        <i class="bi bi-info-circle fs-5"></i>
        <div class="small">
            <strong>Cor e ícone</strong> aparecem no aviso de alerta exibido aos operadores (atualizado ao recarregar a página).
            <strong>Crítico</strong> marca o tipo nos painéis e relatórios de alertas.
            <strong>Tratamentos</strong> definem o encaminhamento de cada tipo; os marcados como automáticos são executados pelo sistema
            em até 1 minuto após a chegada do alerta. Prioridade e descrição vêm do cadastro de tipos de ocorrência e não são editadas aqui.
        </div>
    </div>

    <div class="row gy-2 mb-2 align-items-end">
        <div class="col-sm-4">
            <label for="buscaTipo" class="form-label">Buscar tipo</label>
            <input type="search" id="buscaTipo" class="form-control form-control-sm" placeholder="Digite parte do nome">
        </div>
        <div class="col-sm-8 text-sm-end">
            <span class="small text-muted" id="resumoTipos"></span>
        </div>
    </div>

    <div class="table-responsive">
        <table class="table table-hover table-sm align-middle" id="tabelaTipos">
            <thead class="table-light">
                <tr>
                    <th scope="col" class="text-center">Prioridade</th>
                    <th scope="col">Tipo de alerta</th>
                    <th scope="col">Pré-visualização</th>
                    <th scope="col" class="text-center">Crítico</th>
                    <th scope="col">Cor</th>
                    <th scope="col">Ícone</th>
                    <th scope="col">Tratamentos</th>
                    <th scope="col" class="text-center">Ações</th>
                </tr>
            </thead>
            <tbody>
                <tr><td colspan="8" class="text-center">Carregando…</td></tr>
            </tbody>
        </table>
    </div>

    <datalist id="sugestoesIcone"></datalist>
</div>

<!-- Modal de tratamentos -->
<div class="modal fade" id="modalTratamentos" tabindex="-1" aria-labelledby="modalTratamentosTitulo" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTratamentosTitulo">Tratamentos</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
            </div>
            <div class="modal-body">
                <p class="small text-muted mb-2">
                    Marque os tratamentos aplicáveis a este tipo de alerta. Apenas <em>registro estatístico</em>,
                    <em>priorização para auditoria</em> e <em>geração de ocorrência</em> podem ser executados automaticamente;
                    os demais dependem do operador. Parâmetros são opcionais (objeto JSON).
                </p>
                <div class="table-responsive">
                    <table class="table table-sm align-middle" id="tabelaTratamentos">
                        <thead class="table-light">
                            <tr>
                                <th scope="col" class="text-center">Ativo</th>
                                <th scope="col">Tratamento</th>
                                <th scope="col" class="text-center">Automático</th>
                                <th scope="col">Parâmetros (JSON, opcional)</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                <button type="button" class="btn btn-primary" id="btnSalvarTratamentos">
                    <i class="bi bi-check-circle me-1"></i>Salvar tratamentos
                </button>
            </div>
        </div>
    </div>
</div>

<script src="/muralha-digital/pages/tipos-alerta/assets/js/tipos-alerta.js"></script>
