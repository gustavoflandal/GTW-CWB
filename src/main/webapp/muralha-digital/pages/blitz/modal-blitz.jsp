<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="modal-header">
    <h5 class="modal-title" id="modalBlitzLabel">Nova Blitz Digital</h5>
    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
</div>
<div class="modal-body" style="max-height: 70vh; overflow-y: auto;">
    <form id="formBlitz">
        <div class="mb-3">
            <label for="nomeBlitz" class="form-label">Nome da Blitz</label>
            <input type="text" class="form-control" id="nomeBlitz" name="nomeBlitz" required>
        </div>

        <div class="mb-3">
            <label for="tituloNotificacao" class="form-label">Título da Notificação</label>
            <input type="text" class="form-control" id="tituloNotificacao" name="tituloNotificacao" required maxlength="20">
        </div>

        <div class="mb-3" id="containerEndereco" style="display: none;">
            <label for="endereco" class="form-label">Endereço</label>
            <input type="text" class="form-control" id="endereco" name="endereco" maxlength="255">
            <small class="form-text text-muted">Informe o endereço para blitz manual</small>
        </div>

        <div class="mb-3">
            <label for="descricao" class="form-label">Descrição</label>
            <textarea class="form-control" id="descricao" name="descricao" rows="3" maxlength="255"></textarea>
        </div>

        <input type="hidden" id="idTipoBlitz" name="idTipoBlitz" value="1">
        <input type="hidden" id="dataCriacao" name="dataCriacao">
        <input type="hidden" id="todosLocais" name="todosLocais">

        <div class="row">
            <div class="col-md-6">
                <div class="mb-3">
                    <label for="dataInicio" class="form-label">Data Início</label>
                    <input type="datetime-local" class="form-control" id="dataInicio" name="dataInicio">
                    <small class="form-text text-muted">Deixe em branco para usar a data/hora atual</small>
                </div>
            </div>
            <div class="col-md-6">
                <div class="mb-3">
                    <label for="dataFim" class="form-label">Data Fim</label>
                    <input type="datetime-local" class="form-control" id="dataFim" name="dataFim">
                </div>
            </div>
        </div>

        <div class="mb-3">
            <div class="form-check">
                <input class="form-check-input" type="checkbox" id="notificarAgentesProximos" name="notificarAgentesProximos">
                <label class="form-check-label" for="notificarAgentesProximos">
                    Notificar agente próximo ao local
                </label>
            </div>
        </div>

        <div class="mb-3" id="containerRaioNotificacao" style="display: none;">
            <label for="raioNotificacaoKm" class="form-label">Raio de Notificação (km) *</label>
            <input type="number" class="form-control" id="raioNotificacaoKm" name="raioNotificacaoKm" 
                step="0.1" min="0.1" max="100" placeholder="Ex: 5.0">
        </div>

        <div class="mb-3">
            <label for="selectLocais" class="form-label">Locais (Radares)</label>
            <select class="selectpicker form-control border" id="selectLocais" name="selectLocais" multiple 
                    data-live-search="true" data-style="btn-white" data-size="10" 
                    data-none-selected-text="--Selecione os locais--"
                    data-actions-box="true" data-select-all-text="Marcar todos" 
                    data-deselect-all-text="Desmarcar todos" required>
            </select>

            <button type="button"
                    class="btn btn-outline-primary btn-sm mt-2"
                    id="btnSelecionarViaMapa">
                Selecionar via mapa
            </button>

            <jsp:include page="componente-mapa-radares.jsp" />
        </div>

        <div class="mb-3">
            <label class="form-label">Associar a:</label>
            <div class="form-check">
                <input class="form-check-input" type="radio" name="tipoAssociacao" id="associacaoUsuario" value="usuario" checked>
                <label class="form-check-label" for="associacaoUsuario">
                    Usuários
                </label>
            </div>
            <div class="form-check">
                <input class="form-check-input" type="radio" name="tipoAssociacao" id="associacaoGuarnicao" value="guarnicao">
                <label class="form-check-label" for="associacaoGuarnicao">
                    Guarnições
                </label>
            </div>
        </div>

        <div class="mb-3" id="containerUsuarios">
            <label for="selectUsuarios" class="form-label">Usuários</label>
            <select class="selectpicker form-control border" id="selectUsuarios" name="selectUsuarios" multiple 
                    data-live-search="true" data-style="btn-white" data-size="10" 
                    data-none-selected-text="--Selecione os usuários--"
                    data-actions-box="true" data-select-all-text="Marcar todos" 
                    data-deselect-all-text="Desmarcar todos">
            </select>
        </div>

        <div class="mb-3" id="containerGuarnicoes" style="display: none;">
            <label for="selectGuarnicoes" class="form-label">Guarnições</label>
            <select class="selectpicker form-control border" id="selectGuarnicoes" name="selectGuarnicoes" multiple 
                    data-live-search="true" data-style="btn-white" data-size="10" 
                    data-none-selected-text="--Selecione as guarnições--"
                    data-actions-box="true" data-select-all-text="Marcar todos" 
                    data-deselect-all-text="Desmarcar todos">
            </select>
        </div>

        <div class="mb-3">
            <label for="selectTiposAlerta" class="form-label">Tipos de Alerta</label>
            <select class="selectpicker form-control border" id="selectTiposAlerta" name="selectTiposAlerta" multiple 
                    data-live-search="true" data-style="btn-white" data-size="10" 
                    data-none-selected-text="--Todos os tipos--"
                    data-actions-box="true" data-select-all-text="Marcar todos" 
                    data-deselect-all-text="Desmarcar todos">
            </select>
            <small class="form-text text-muted">Deixe vazio para incluir todos os tipos de alerta</small>
        </div>

        <div class="form-check mb-3" style="display: none;">
            <input class="form-check-input" type="checkbox" id="ativo" name="ativo" checked>
            <label class="form-check-label" for="ativo">
                Ativo
            </label>
        </div>

        <div id="messageBox" class="message-box hidden"></div>
    </form>
</div>
<div class="modal-footer">
    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
    <button type="button" class="btn btn-success" id="salvarBlitz">Salvar Blitz</button>
</div>

<script src="assets/js/modal-blitz.js"></script>
