<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="modal fade" id="modalHistoricoPlaca" tabindex="-1" aria-labelledby="modalHistoricoPlacaLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl">
        <div class="modal-content">
            <div class="modal-header pt-2 pb-2">
                <h5 class="modal-title" id="modalHistoricoPlacaLabel">Histórico por Placa</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body pt-1 pb-1">
                <div id="error_container_modal_historico_placa" class="alert alert-danger" style="display: none;"></div>
                
                <div class="mb-3">
                    <h6>Placa: <span id="placaSelecionada" class="fw-bold"></span></h6>
                </div>

                <div class="card mb-3">
                    <div class="card-header bg-light">
                        <h6 class="mb-0">Informações do Veículo</h6>
                    </div>
                    <div class="card-body">
                        <div id="containerInformacoesVeiculo" class="row small">
                            <p class="text-center text-muted">Carregando informações...</p>
                        </div>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-6">
                        <div class="card mb-3">
                            <div class="card-header bg-light">
                                <h6 class="mb-0">Registros de Fato</h6>
                            </div>
                            <div class="card-body" style="max-height: 300px; overflow-y: auto;">
                                <div id="containerRegistrosFatoPlaca">
                                    <p class="text-center text-muted">Carregando registros...</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-6">
                        <div class="card mb-3">
                            <div class="card-header bg-light">
                                <h6 class="mb-0">Alertas</h6>
                            </div>
                            <div class="card-body" style="max-height: 300px; overflow-y: auto;">
                                <div id="containerAlertas">
                                    <p class="text-center text-muted">Carregando alertas...</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer pt-2 pb-2">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
            </div>
        </div>
    </div>
</div>