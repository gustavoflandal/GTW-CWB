<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="keywords" content="javascript, dynamic, grid, layout, jquery plugin, flex layouts, normal grid layouts"/>
        
        <title>Histórico de Abordagens</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        
        <link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
        <link rel="stylesheet" href="/muralha-digital/pages/blitz-abordagem/assets/css/listagem-abordagem.css">
        
        <script type="text/javascript" src="/muralha-digital/assets/jquery/jquery-3.6.0.min.js"></script>
    </head>
    <body class="loading">
        <div class="container mt-4">
            <div class="row gy-3">
                <div class="col-sm-12">
                    <div class="mb-3">
                        <h2 class="text-center"><strong>Histórico de Abordagens</strong></h2>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="container">
            <div class="row gy-3 mb-2">
                <div class="col-sm-4">
                    <div class="form-group">
                        <label for="dataInicio" class="form-label">Data Início:</label>
                        <input type="date" id="dataInicio" class="form-control">
                    </div>
                </div>
                <div class="col-sm-4">
                    <div class="form-group">
                        <label for="dataFim" class="form-label">Data Fim:</label>
                        <input type="date" id="dataFim" class="form-control">
                    </div>
                </div>
                <div class="col-sm-4">
                    <div class="form-group">
                        <label for="selectBlitz" class="form-label">Blitz:</label>
                        <select id="selectBlitz" class="form-select">
                            <option value="">Todas as Blitz</option>
                        </select>
                    </div>
                </div>
            </div>
            
            <div class="row mb-3 gy-3">
                <div class="col-sm-2 col-12">
                    <div class="d-grid">
                        <button id="btnVoltar" class="btn btn-secondary" type="button">
                            <i class="bi bi-arrow-left"></i> VOLTAR
                        </button>
                    </div>
                </div>

                <div class="col-sm-2 col-12">
                    <div class="d-grid">
                        <button id="btnFiltrar" class="btn btn-primary" type="button">FILTRAR</button>
                    </div>
                </div>

                <div class="col-sm-6 d-none d-sm-block"></div>

                <div class="col-sm-2 col-12">
                    <div class="d-grid">
                        <button id="btnNovaAbordagem" class="btn btn-success" type="button">
                            <i class="bi bi-plus-circle"></i> NOVA
                        </button>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="container-fluid">
            <div class="row">
                <div class="col-sm-12 container">
                    <div class="mb-3">
                        <h5><small>Lista de abordagens:</small></h5>
                    </div>
                    
                    <div class="table-responsive">         
                        <table id="tabela-abordagem" class="table table-bordered table-hover align-middle">
                            <thead class="table-secondary">
                                <tr>
                                    <th scope="col" style="width: 10%"><small>Placa</small></th>
                                    <th scope="col" style="width: 25%"><small>Blitz</small></th>
                                    <th scope="col" style="width: 20%"><small>Data Abordagem</small></th>
                                    <th scope="col" style="width: 20%"><small>Agente</small></th>
                                    <th scope="col" style="width: 10%"><small>Status</small></th>
                                    <th scope="col" style="width: 15%"><small>Ação</small></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="6" class="text-center">Carregando...</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="overlay"></div>
        
        <script src="/muralha-digital/pages/blitz-abordagem/assets/js/listagem-abordagem.js"></script>
        <script>
            if (!document.querySelector('link[href*="bootstrap-icons"]')) {
                const link = document.createElement('link');
                link.rel = 'stylesheet';
                link.href = 'https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css';
                document.head.appendChild(link);
            }
        </script>
    </body>
</html>