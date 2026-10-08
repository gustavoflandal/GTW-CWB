<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<html lang="pt-br">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Relatório de Sessão de Usuário</title>
        <%-- Estilos e ícones do Bootstrap 5 --%>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        
        <style>
            /* Estilo para impressão */
            @media print {
                /* Scroll containers - modais e divs com scroll */
                .modal-body {
                    max-height: none !important;
                    overflow: visible !important;
                }

                .card-body, .p-3 {
                    max-height: none !important;
                    overflow: visible !important;
                }

                /* Ocultar elementos não necessários na impressão */
                .btn, .no-print, button, form, .modal-header, .btn-close {
                    display: none !important;
                }

                * {
                    -webkit-print-color-adjust: exact !important;
                    print-color-adjust: exact !important;
                }

                body {
                    margin: 0;
                    padding: 0;
                    background: white !important;
                }

                .card {
                    box-shadow: none !important;
                    border: 1px solid #ddd !important;
                    page-break-inside: avoid;
                }

                .table {
                    font-size: 9pt !important;
                }
            }
        </style>
    </head>
	
	<body class="homepage">

        <%-- Container para notificações (toasts) --%>
    <div aria-live="polite" aria-atomic="true" class="position-relative">
        <div id="toast-container" class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 1100;"></div>
    </div>

    <div class="container mt-4" style="max-width: 960px;">
        <%-- Card de Filtros --%>
        <div class="card shadow-sm">
            <div class="card-header bg-light">
                <h4 class="mb-0">Relatório de sessão de usuário</h4>
            </div>
            <div class="card-body">
                <div class="row g-3 align-items-end">
                    
                    <div class="col-md-3">
                        <label for="data-inicial" class="form-label">Data Início</label>
                        <input type="date" id="data-inicial" class="form-control">
                    </div>
                    
                    <div class="col-md-3">
                        <label for="data-final" class="form-label">Data Fim</label>
                        <input type="date" id="data-final" class="form-control">
                    </div>
                
                </div>
                <div class="mt-4 pt-3 border-top">
                    <button id="gerar-relatorio" class="btn btn-primary" onclick="gerarRelatorio()"><i class="bi bi-search me-2"></i>Consultar</button>
                    <button id="limpar-pesquisa" class="btn btn-secondary" onclick="limparPesquisa()"><i class="bi bi-eraser me-2"></i>Limpar</button>
                </div>
            </div>
        </div>

        <%-- Card de Resultados --%>
        <div class="card mt-4 shadow-sm">
            
            <div class="card-header bg-light d-flex justify-content-between align-items-center">
                <div>
                    <h5 class="mb-0 d-inline-block">Resultados</h5>
                    <span id="record-count" class="badge bg-secondary ms-2">0 registros</span>
                </div>

                <div>
                    <button id="exportar-pdf" class="btn btn-outline-danger btn-sm"><i class="bi bi-file-earmark-pdf me-2"></i>Exportar PDF</button>
                    <button id="exportar-excel" class="btn btn-outline-success btn-sm"><i class="bi bi-file-earmark-excel me-2"></i>Exportar Excel</button>
                </div>
            </div>

            <div class="card-body">

                <div class="col-md-5">
                    <label for="usuario" class="form-label">Filtro por usuário</label>
                    <select id="usuario" class="form-select">
                        <option value="">Aguardando consulta</option>
                    </select>
                </div>
                <br>

                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>Usuário</th>
                                <th>Login</th>
                                <th>Logout</th>
                                <th>Duração</th>
                            </tr>
                        </thead>
                        <tbody id="report-table-body">
                            <tr><td colspan="5" class="text-center text-muted">Nenhum dado gerado.</td></tr>
                        </tbody>
                    </table>
                </div>

            </div>

        </div>
    </div>

    <script>
        window.addEventListener('DOMContentLoaded', function() {
            var dataFinalInput = document.getElementById('data-final');
            if (dataFinalInput) {
                var hoje = new Date();
                var yyyy = hoje.getFullYear();
                var mm = String(hoje.getMonth() + 1).padStart(2, '0');
                var dd = String(hoje.getDate()).padStart(2, '0');
                dataFinalInput.value = yyyy + '-' + mm + '-' + dd;
            }
        });
    </script>
        <!-- Modal para exibir navegação do usuário -->
        <div class="modal fade" id="modalNavegacao" tabindex="-1" aria-labelledby="modalNavegacaoLabel" aria-hidden="true">
            <div class="modal-dialog modal-xl" style="max-width:75vw;">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalNavegacaoLabel">Navegação do Usuário</h5>
                        <div class="ms-auto d-flex gap-2">
                            <button id="exportar-modal-pdf" 
                                class="btn btn-outline-danger btn-sm" 
                                onclick="exportarDetalhesParaPDF()">
                                <i class="bi bi-file-earmark-pdf me-1"></i>PDF</button>
                            <button id="exportar-modal-excel" 
                                class="btn btn-outline-success btn-sm" 
                                onclick="exportarDetalhesParaExcel()">
                                <i class="bi bi-file-earmark-excel me-1"></i>Excel</button>
                            <button type="button" class="btn-close" 
                                data-bs-dismiss="modal" aria-label="Fechar"></button>
                        </div>
                    </div>
                    <div class="modal-body" style="overflow-y:auto; max-height:80vh;">
                        <div id="modalNavegacaoBody">Carregando...</div>
                    </div>
                </div>
            </div>
        </div>

        <script src="js/sessao-usuario.js"></script> 
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.7.0/jspdf.plugin.autotable.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
        
    </body>

</html>