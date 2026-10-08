<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="modal fade" id="modalPassagensRelacionadas" tabindex="-1" aria-labelledby="modalPassagensRelacionadasLabel" aria-hidden="true" data-bs-backdrop="static">
    <div class="modal-dialog modal-xl">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalPassagensRelacionadasLabel">
                    <span id="placaPassagemAtual"></span> - Histórico de Passagens
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-12 text-center mb-3">
                        <div class="btn-group" role="group">
                            <button type="button" class="btn btn-outline-secondary" id="btnAnterior" onclick="navegarPassagem('anterior')" disabled>
                                <i class="bi bi-arrow-left"></i> Anterior
                            </button>
                            <span class="btn btn-outline-secondary disabled" id="indicePassagem">1 / 1</span>
                            <button type="button" class="btn btn-outline-secondary" id="btnProximo" onclick="navegarPassagem('proximo')" disabled>
                                Próximo <i class="bi bi-arrow-right"></i>
                            </button>
                        </div>
                    </div>
                </div>
                
                <div class="row" id="conteudoPassagemAtual">
                    <div class="col-md-8">
                        <img id="imagemPassagem" class="img-fluid rounded border" src="" alt="Imagem da passagem" style="max-height: 400px; width: 100%; object-fit: contain; background-color: #f8f9fa;">
                    </div>
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-header bg-light">
                                <strong>Informações da Passagem</strong>
                            </div>
                            <div class="card-body">
                                <table class="table table-sm table-borderless">
                                    <tr>
                                        <th style="width: 40%;">Placa:</th>
                                        <td id="infoPlacaModal" class="fw-bold text-uppercase" style="font-size: 1.2rem;"></td>
                                    </tr>
                                    <tr>
                                        <th>Data/Hora:</th>
                                        <td id="infoData"></td>
                                    </tr>
                                    <tr>
                                        <th>Local:</th>
                                        <td id="infoLocal"></td>
                                    </tr>
                                    <tr>
                                        <th>Equipamento:</th>
                                        <td id="infoEquipamento"></td>
                                    </tr>
                                    <tr>
                                        <th>Faixa:</th>
                                        <td id="infoFaixa"></td>
                                    </tr>
                                    <tr>
                                        <th>Velocidade:</th>
                                        <td id="infoVelocidade"></td>
                                    </tr>
                                    <tr>
                                        <th>Classificação:</th>
                                        <td id="infoClassificacao"></td>
                                    </tr>
                                </table>
                                <div id="alertaPassagem" class="alert alert-warning mt-2 mb-0" style="display: none;"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" onclick="criarAbordagemDoModal()">
                    <i class="bi bi-pencil-square"></i> Realizar Abordagem
                </button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
            </div>
        </div>
    </div>
</div>