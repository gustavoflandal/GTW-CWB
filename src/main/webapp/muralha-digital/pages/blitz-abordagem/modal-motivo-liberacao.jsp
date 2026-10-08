<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="modal fade" id="modalMotivoLiberacao" tabindex="-1" aria-labelledby="modalMotivoLiberacaoLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalMotivoLiberacaoLabel">Motivo da Liberação</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
            </div>
            <div class="modal-body">
                <form id="formMotivoLiberacao">
                    <div class="mb-3">
                        <label for="motivoLiberacao" class="form-label">Descreva o motivo da liberação *</label>
                        <textarea class="form-control" id="motivoLiberacao" rows="4" maxlength="200" required></textarea>
                        <div class="invalid-feedback">
                            Por favor, informe o motivo da liberação.
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-success" id="btnConfirmarLiberacao">Confirmar Liberação</button>
            </div>
        </div>
    </div>
</div>