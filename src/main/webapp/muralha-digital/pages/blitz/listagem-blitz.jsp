<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="keywords" content="javascript, dynamic, grid, layout, jquery plugin, flex layouts, normal grid layouts"/>

        <title>Listagem de Blitz Digital</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />

        <script src="/muralha-digital/assets/jquery/jquery-3.6.0.min.js"></script>
        <script src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>
        <script src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
        <script src="/muralha-digital/pages/consulta-veiculo/freewall/js/jquery-1.10.2.min.js"></script>
        <script src="/muralha-digital/pages/consulta-veiculo/freewall/js/freewall.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js"></script>
        <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk&libraries=geometry,drawing"></script>	   
    </head>

    <body class="loading">

        <div class="container mt-4">
            <h2 class="text-center mb-4">Listagem de Blitz Digital</h2>
        </div>

        <div class="container mb-3">
            <div class="d-flex flex-wrap align-items-center justify-content-end gap-2">
                <button class="btn btn-sm btn-success me-2" id="btnNovaBlitzAutomatica" title="Criar Blitz Automática">
                    <i class="bi bi-robot"></i> Criar Blitz Automática
                </button>
                <button class="btn btn-sm btn-primary me-2" id="btnNovaBlitzManual" title="Criar Blitz Manual">
                    <i class="bi bi-person"></i> Criar Blitz Manual
                </button>
                <button class="btn btn-sm btn-secondary" id="btnVoltar" title="Voltar">
                    <i class="bi bi-arrow-left"></i> Voltar
                </button>
            </div>
        </div>

        <div class="container">
            <div class="table-responsive">
                <table class="table table-striped align-middle" id="tabela-blitz">
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Título Notificação</th>
                            <th>Data Início</th>
                            <th>Data Fim</th>
                            <th>Status</th>
                            <th>Ações</th>
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

        <div class="modal fade" id="modalCriarBlitz" tabindex="-1" aria-labelledby="modalCriarBlitzLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
                <div class="modal-content">
                    <%@ include file="modal-blitz.jsp" %>
                </div>
            </div>
        </div>

        <div class="overlay"></div>

        <script src="assets/js/listagem-blitz.js"></script>
    </body>
</html>
