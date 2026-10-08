<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="keywords" content="javascript, dynamic, grid, layout, jquery plugin, flex layouts, normal grid layouts"/>
        
        <title>Detalhes da Abordagem</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        
        <link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="/muralha-digital/pages/blitz-abordagem/assets/css/detalhes-abordagem.css">
        
        <script type="text/javascript" src="/muralha-digital/assets/jquery/jquery-3.6.0.min.js"></script>
        <%@ include	file="/muralha-digital/pages/registro_fato/comBoletim/modal/modal-registroDeFato.jsp" %>
        <%@ include file="/muralha-digital/pages/consulta-veiculo/modal-detalhe-veiculo.jsp" %>
    </head>
    <body class="loading">
        <div class="container-fluid mt-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="text-center mb-0">Detalhes da Abordagem</h2>
                <div class="d-flex flex-column flex-md-row gap-2">
                    <a href="listagem-abordagem.jsp" class="btn btn-secondary btn-sm mb-2 mb-md-0">
                        <i class="bi bi-arrow-left"></i> Voltar
                    </a>
                    <button id="btnAlerta" class="btn btn-warning btn-sm" style="display: none;">
                        <i class="bi bi-exclamation-triangle"></i> Ver Alerta Associado
                    </button>
                    <button id="btnVeiculoTempoReal" class="btn btn-info btn-sm" style="display: none;">
                        <i class="bi bi-car-front"></i> Ver Veículo Tempo Real
                    </button>
                    <button id="btnRegistroFato" class="btn btn-primary btn-sm" style="display: none;">
                        <i class="bi bi-file-text"></i> Ver Registro de Fato
                    </button>
                </div>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <div class="card mb-3">
                        <div class="card-header bg-light">
                            <h5 class="mb-0">Informações da Abordagem</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <p><strong>Placa:</strong> <span id="placaVeiculo">-</span></p>
                                    <p><strong>Marca:</strong> <span id="marcaVeiculo">-</span></p>
                                    <p><strong>Modelo:</strong> <span id="modeloVeiculo">-</span></p>
                                    <p><strong>Blitz:</strong> <span id="nomeBlitz">-</span></p>
                                    <p><strong>Local:</strong> <span id="nomeLocal">-</span></p>
                                    <p id="containerMotivoCancelamento" style="display: none;">
                                        <strong>Motivo da liberação:</strong> 
                                        <span id="motivoCancelamento">-</span>
                                    </p>
                                </div>
                                <div class="col-md-6">
                                    <p><strong>Tipo:</strong> <span id="tipoVeiculo">-</span></p>
                                    <p><strong>Cor:</strong> <span id="corVeiculo">-</span></p>
                                    <p><strong>Data/Hora:</strong> <span id="dataAbordagem">-</span></p>
                                    <p><strong>Agente:</strong> <span id="nomeAgente">-</span></p>
                                    <p><strong>Status:</strong> <span id="statusAbordagem">-</span></p>
                                    <p><strong>Resultado:</strong> <span id="resultadoAbordagem">-</span></p>
                                    <p><strong>Observações:</strong> <span id="observacoes">-</span></p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card mb-3" id="cardImagensAbordagem" style="display: none;">
                        <div class="card-header bg-light">
                            <h5 class="mb-0">Imagens da Abordagem</h5>
                        </div>
                        <div class="card-body">
                            <div id="containerImagensAbordagem"></div>
                        </div>
                    </div>

                    <div class="card mb-3">
                        <div class="card-header bg-light">
                            <h5 class="mb-0">Pessoas Envolvidas</h5>
                        </div>
                        <div class="card-body">
                            <div id="containerPessoas">
                                <p class="text-center">Carregando...</p>
                            </div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header bg-light">
                            <h5 class="mb-0">Documentos Verificados</h5>
                        </div>
                        <div class="card-body">
                            <div id="containerDocumentos">
                                <p class="text-center">Carregando...</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <script src="/muralha-digital/pages/blitz-abordagem/assets/js/detalhes-abordagem.js"></script>
        <div class="overlay"></div>
    </body>
</html>