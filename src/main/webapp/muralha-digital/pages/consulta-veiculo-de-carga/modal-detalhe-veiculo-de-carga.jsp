<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal fade" id="modalDetalheVeiculo" tabindex="-1" aria-labelledby="modalDetalheVeiculoLabel" aria-hidden="true">
	<script type="text/javascript" src="/muralha-digital/pages/consulta-veiculo-de-carga/js/modal-detalhe-veiculo-de-carga.js"></script>
    <div class="modal-dialog modal-dialog-centered modal-xl">
        <div class="modal-content" style="position: relative;">
            <div class="modal-header pt-2 pb-2">
                <h5 class="modal-title" id="staticBackdropLabel">Detalhes do Veículo de Carga</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            
            <div class="modal-body pt-1 pb-1">
                <div id="error_container_modal_detalhe_veiculo"></div>

                <!-- NOVO: Carrossel de Imagens -->
                <div class="row mb-3">
                    <div class="col-sm-12">
                        <div class="container border border-2 pb-2">
                            <div class="row">
                                <div class="col-sm-12">
                                    <h6 class="text-center"><strong>Imagens do Veículo</strong></h6>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-1 d-flex align-items-center justify-content-center">
                                    <div id="controleGaleriaPrev"></div>
                                </div>
                                <div class="col-sm-10">
                                    <div id="galeriaImagensVeiculo" class="carousel slide carousel-fade" data-bs-interval="false">
                                        <div class="carousel-inner">
                                            <!-- As imagens serão inseridas dinamicamente aqui -->
                                        </div>
                                    </div>
                                </div>
                                <div class="col-sm-1 d-flex align-items-center justify-content-center">
                                    <div id="controleGaleriaNext"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-12">
                        <div class="container border border-2 pb-2">
                            <div class="row">
                                <div class="col-sm-12">
                                    <h5 class="text-center"><strong>Informações do Veículo</strong></h5>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-sm-4">
                                    <div class="form-group">
                                        <label for="equipamentoModal" class='form-label col-form-label col-form-label-sm'>Equipamento:</label>
                                        <input id="equipamentoModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="pistaModal" class='form-label col-form-label col-form-label-sm'>Pista:</label>
                                        <input id="pistaModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="sentidoModal" class='form-label col-form-label col-form-label-sm'>Sentido:</label>
                                        <input id="sentidoModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="classificacaoModal" class='form-label col-form-label col-form-label-sm'>Classificação:</label>
                                        <input id="classificacaoModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="classificacaoArt96Modal" class='form-label col-form-label col-form-label-sm'>Classif. Art.96:</label>
                                        <input id="classificacaoArt96Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-sm-3">
                                    <div class="form-group">
                                        <label for="dataModal" class='form-label col-form-label col-form-label-sm'>Data:</label>
                                        <input id="dataModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="placaModal" class="form-label col-form-label col-form-label-sm">Placa:</label>
                                        <input id="placaModal" class="form-control form-control-sm text-uppercase" disabled>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="marcaModal" class='form-label col-form-label col-form-label-sm'>Marca:</label>
                                        <input id="marcaModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="modeloModal" class='form-label col-form-label col-form-label-sm'>Modelo:</label>
                                        <input id="modeloModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-1">
                                    <div class="form-group">
                                        <label for="velocidadeModal" class='form-label col-form-label col-form-label-sm'>Velocidade:</label>
                                        <input id="velocidadeModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="comprimentoModal" class='form-label col-form-label col-form-label-sm'>Comprimento:</label>
                                        <input id="comprimentoModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-sm-4">
                                    <div class="form-group">
                                        <label for="latitudeModal" class='form-label col-form-label col-form-label-sm'>Latitude:</label>
                                        <input id="latitudeModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-4">
                                    <div class="form-group">
                                        <label for="longitudeModal" class='form-label col-form-label col-form-label-sm'>Longitude:</label>
                                        <input id="longitudeModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="comPesagemModal" class='form-label col-form-label col-form-label-sm'>Com Pesagem:</label>
                                        <input id="comPesagemModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="comImagemModal" class='form-label col-form-label col-form-label-sm'>Com Imagem:</label>
                                        <input id="comImagemModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row mt-3">
                                <div class="col-sm-12">
                                    <h6 class="text-center"><strong>Dados de Pesagem</strong></h6>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="pbtModal" class='form-label col-form-label col-form-label-sm'>PBT (kg):</label>
                                        <input id="pbtModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="pbtcModal" class='form-label col-form-label col-form-label-sm'>PBTC (kg):</label>
                                        <input id="pbtcModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="numeroEixosModal" class='form-label col-form-label col-form-label-sm'>Nº Eixos:</label>
                                        <input id="numeroEixosModal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row mt-3">
                                <div class="col-sm-12">
                                    <h6 class="text-center"><strong>Pesos por Eixo (kg)</strong></h6>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-1">
                                    <div class="form-group">
                                        <label for="e1Modal" class='form-label col-form-label col-form-label-sm'>E1:</label>
                                        <input id="e1Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-1">
                                    <div class="form-group">
                                        <label for="e2Modal" class='form-label col-form-label col-form-label-sm'>E2:</label>
                                        <input id="e2Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-1">
                                    <div class="form-group">
                                        <label for="e3Modal" class='form-label col-form-label col-form-label-sm'>E3:</label>
                                        <input id="e3Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-1">
                                    <div class="form-group">
                                        <label for="e4Modal" class='form-label col-form-label col-form-label-sm'>E4:</label>
                                        <input id="e4Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-1">
                                    <div class="form-group">
                                        <label for="e5Modal" class='form-label col-form-label col-form-label-sm'>E5:</label>
                                        <input id="e5Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-1">
                                    <div class="form-group">
                                        <label for="e6Modal" class='form-label col-form-label col-form-label-sm'>E6:</label>
                                        <input id="e6Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-1">
                                    <div class="form-group">
                                        <label for="e7Modal" class='form-label col-form-label col-form-label-sm'>E7:</label>
                                        <input id="e7Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-1">
                                    <div class="form-group">
                                        <label for="e8Modal" class='form-label col-form-label col-form-label-sm'>E8:</label>
                                        <input id="e8Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-1">
                                    <div class="form-group">
                                        <label for="e9Modal" class='form-label col-form-label col-form-label-sm'>E9:</label>
                                        <input id="e9Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row mt-3">
                                <div class="col-sm-12">
                                    <h6 class="text-center"><strong>Distâncias entre Eixos (m)</strong></h6>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="distanciaE1E2Modal" class='form-label col-form-label col-form-label-sm'>E1-E2:</label>
                                        <input id="distanciaE1E2Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="distanciaE2E3Modal" class='form-label col-form-label col-form-label-sm'>E2-E3:</label>
                                        <input id="distanciaE2E3Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="distanciaE3E4Modal" class='form-label col-form-label col-form-label-sm'>E3-E4:</label>
                                        <input id="distanciaE3E4Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="distanciaE4E5Modal" class='form-label col-form-label col-form-label-sm'>E4-E5:</label>
                                        <input id="distanciaE4E5Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="distanciaE5E6Modal" class='form-label col-form-label col-form-label-sm'>E5-E6:</label>
                                        <input id="distanciaE5E6Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-2">
                                    <div class="form-group">
                                        <label for="distanciaE6E7Modal" class='form-label col-form-label col-form-label-sm'>E6-E7:</label>
                                        <input id="distanciaE6E7Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-3">
                                    <div class="form-group">
                                        <label for="distanciaE7E8Modal" class='form-label col-form-label col-form-label-sm'>E7-E8:</label>
                                        <input id="distanciaE7E8Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                                <div class="col-sm-3">
                                    <div class="form-group">
                                        <label for="distanciaE8E9Modal" class='form-label col-form-label col-form-label-sm'>E8-E9:</label>
                                        <input id="distanciaE8E9Modal" class="form-control form-control-sm" disabled></input>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div id="id_veic" hidden></div>
    
            <div class="modal-footer pt-2 pb-2">
                <button type="button" class="btn btn-secondary me-1" onclick="ObterVeiculoAnterior()">Anterior</button>
                <button type="button" class="btn btn-secondary me-auto ms-1" onclick="ObterVeiculoProximo()">Próximo</button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
            </div>
        </div>
    </div>
</div>