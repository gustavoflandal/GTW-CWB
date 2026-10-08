<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>
<%@ include file="/muralha-digital/pages/blitz-abordagem/modal-historico-cpf.jsp" %>
<%@ include file="/muralha-digital/pages/blitz-abordagem/modal-historico-placa.jsp" %>
<%@ include file="/muralha-digital/pages/blitz-abordagem/modal-motivo-liberacao.jsp" %>
<%@ include	file="/muralha-digital/pages/registro_fato/comBoletim/modal/modal-registroDeFato.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="keywords" content="javascript, dynamic, grid, layout, jquery plugin, flex layouts, normal grid layouts"/>
        
        <title>Nova Abordagem</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        
        <link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="/muralha-digital/pages/blitz-abordagem/assets/css/criar-abordagem.css">
        
        <script type="text/javascript" src="/muralha-digital/assets/jquery/jquery-3.6.0.min.js"></script>
        <script type="text/javascript" src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/piexifjs"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
        <script src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>

        <script>
            function isMobileApp() {
                const cookies = document.cookie.split(';');
                for (let cookie of cookies) {
                    const [name, value] = cookie.trim().split('=');
                    if (name === 'isMobileApp' && value === 'true') {
                        return true;
                    }
                }
                return false;
            }

            document.addEventListener('DOMContentLoaded', function() {
                const urlParams = new URLSearchParams(window.location.search);
                const idAlerta = urlParams.get('idAlerta');
                
                if (idAlerta) {
                    document.getElementById('idAlertaHidden').value = idAlerta;
                }
            });
        </script>
    </head>
    <body class="loading">
        <div class="container-fluid mt-4">
            <div class="d-flex justify-content-between align-items-center mb-4 header-abordagem-mobile">
                <h2 class="text-center mb-0">Nova Abordagem</h2>
                <div class="d-flex flex-wrap gap-2 justify-content-md-end justify-content-start">
                    <a href="listagem-abordagem.jsp" class="btn btn-secondary btn-sm mb-2 mb-md-0">
                        <i class="bi bi-arrow-left"></i> Voltar para Listagem
                    </a>
                    <div id="acaoAbordagem"></div>
                </div>
            </div>

            <form id="formAbordagem">
                <div class="row">
                    <div class="col-md-6">
                        <div class="card mb-3">
                            <div class="card-header bg-light">
                                <h5 class="mb-0">Dados da Abordagem</h5>
                            </div>
                            <div class="card-body">
                                <div class="mb-3">
                                    <label class="form-label">Blitz *</label>
                                    <select id="selectBlitz" class="form-select" required>
                                        <option value="">Selecione uma blitz</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Placa do Veículo *</label>
                                    <div class="d-flex flex-wrap gap-2 align-items-start">
                                        <div class="flex-grow-1" style="min-width: 200px;">
                                            <input type="text" id="placaVeiculo" class="form-control" 
                                                maxlength="7" placeholder="ABC1234">
                                        </div>
                                        <button type="button" 
                                                class="btn btn-outline-primary" 
                                                id="btnCapturarPlaca"
                                                onclick="iniciarCapturaOCR()"
                                                style="display: none;">
                                            <i class="bi bi-camera"></i> Capturar placa
                                        </button>
                                    </div>
                                    <div class="mt-2" id="containerEditarPlaca"></div>
                                    <div class="form-text" id="textoAjudaPlaca"></div>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Marca do Veículo</label>
                                    <input type="text" id="marcaVeiculo" class="form-control" 
                                        placeholder="Ex: Toyota, Ford, etc." maxlength="50">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Modelo do Veículo</label>
                                    <input type="text" id="modeloVeiculo" class="form-control" 
                                        placeholder="Ex: Corolla, Fiesta, etc." maxlength="50">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Tipo do Veículo</label>
                                    <input type="text" id="tipoVeiculo" class="form-control" 
                                        placeholder="Ex: Carro, Moto, Caminhão, etc." maxlength="30">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Cor do Veículo</label>
                                    <input type="text" id="corVeiculo" class="form-control" 
                                        placeholder="Ex: Preto, Branco, Prata, etc." maxlength="30">
                                </div>
                                <div class="mb-3" id="containerResultadoAbordagem">
                                    <label class="form-label">Resultado da Abordagem *</label>
                                    <select class="form-select" id="selectResultadoAbordagem" required placeholder="Selecione o resultado da abordagem">
                                        <option value="">Selecione o resultado da abordagem</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Observações</label>
                                    <textarea id="observacoes" class="form-control" rows="3" 
                                              placeholder="Observações sobre a abordagem..."></textarea>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Imagens da Abordagem</label>

                                    <div class="file-upload-container">
                                        <input type="file"
                                            id="imagensAbordagem"
                                            class="form-control"
                                            accept="image/*"
                                            multiple
                                            style="display: none;">

                                        <div class="input-group">
                                            <input type="text"
                                                class="form-control"
                                                id="nomeArquivosAbordagem"
                                                placeholder="Nenhuma imagem selecionada"
                                                readonly>

                                            <button type="button"
                                                    class="btn btn-outline-secondary"
                                                    id="btnSelecionarImagensAbordagem">
                                                <i class="bi bi-camera"></i> Selecionar
                                            </button>
                                        </div>

                                        <small class="text-muted">
                                            É possível adicionar múltiplas imagens (máx. 10MB cada)
                                        </small>

                                        <div id="previewImagensAbordagem" class="row mt-2"></div>
                                    </div>
                                </div>

                                <%-- CAMPOS HIDDEN PARA DADOS DO ALERTA --%>
                                <input type="hidden" id="idAlertaHidden" name="idAlertaHidden" value="">
                                <input type="hidden" id="idLocalHidden" name="idLocalHidden" value="">
                                <input type="hidden" id="idRegistroFatoHidden" name="idRegistroFatoHidden" value="">
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <div class="card mb-3">
                            <div class="card-header bg-light d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">Pessoas Envolvidas</h5>
                                <button type="button" id="btnAddPessoa" class="btn btn-sm btn-success">
                                    <i class="bi bi-person-plus"></i> Adicionar
                                </button>
                            </div>
                            <div class="card-body">
                                <div id="containerPessoas">
                                    <p class="text-center text-muted">Nenhuma pessoa adicionada</p>
                                </div>
                            </div>
                        </div>

                        <div class="card">
                            <div class="card-header bg-light d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">Documentos Verificados</h5>
                                <button type="button" id="btnAddDocumento" class="btn btn-sm btn-success">
                                    <i class="bi bi-file-earmark-plus"></i> Adicionar
                                </button>
                            </div>
                            <div class="card-body">
                                <div id="containerDocumentos">
                                    <p class="text-center text-muted">Nenhum documento adicionado</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <div id="messageBox" class="message-box hidden mt-3"></div>
        </div>
        
        <script src="/muralha-digital/pages/blitz-abordagem/assets/js/criar-abordagem.js"></script>
        <script src="/muralha-digital/pages/blitz-abordagem/assets/js/localizacao-helper.js"></script>
        <script src="/muralha-digital/pages/blitz-abordagem/assets/js/historico-cpf.js"></script>
        <script src="/muralha-digital/pages/blitz-abordagem/assets/js/historico-placa.js"></script>
        <div class="overlay"></div>
    </body>
</html>