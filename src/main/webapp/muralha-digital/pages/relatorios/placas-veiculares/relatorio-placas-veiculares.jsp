<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>Relatório Histórico de Correção de Placas Veiculares</title>

    <link rel="icon" href="data:,">

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />

    <!-- CSS customizado -->
    <link rel="stylesheet" href="assets/css/style.css" />
</head>

<body class="bg-light">
    <div class="container py-4">
        <!-- Card de Filtros -->
        <div class="card mb-4">
            <div class="card-header titulo-relatorio">
                <i class="fas fa-filter me-2"></i>Relatório Histórico de Correção de Placas Veiculares
            </div>
            <div class="card-body">
                <!-- Mensagens -->
                <div id="message-container" class="message-container hidden"></div>
                
                <form class="g-3" id="formRelatorio">
                    <div class="row">
                        <div class="col-md-4">
                            <label for="placas" class="form-label relatorio-label">
                                <i class="fas fa-car me-1"></i>Placas (Opcional)
                            </label>
                            <input 
                                id="placas" 
                                name="placas" 
                                type="text" 
                                class="form-control form-control-lg" 
                                placeholder="Informe as placas separadas por vírgula (opcional)"
                                title="Informe as placas separadas por vírgula. Deixe em branco para consultar todas as placas do período." />
                        </div>
                        
                        <div class="col-md-4">
                            <label for="dataInicio" class="form-label relatorio-label">
                                <i class="fas fa-calendar-alt me-1"></i>Data Inicial <span class="text-danger">*</span>
                            </label>
                            <input 
                                id="dataInicio" 
                                name="dataInicio" 
                                type="date" 
                                class="form-control form-control-lg" 
                                required />
                        </div>
                        
                        <div class="col-md-4">
                            <label for="dataFinal" class="form-label relatorio-label">
                                <i class="fas fa-calendar-alt me-1"></i>Data Final <span class="text-danger">*</span>
                            </label>
                            <input 
                                id="dataFinal" 
                                name="dataFinal" 
                                type="date" 
                                class="form-control form-control-lg" 
                                required />
                        </div>
                    </div>

                    <div class="row mt-4">
                        <div class="col-12 d-flex justify-content-between align-items-center">
                            <div class="form-text">
                                <i class="fas fa-info-circle text-primary me-1"></i>
                                Informe as datas obrigatoriamente. As placas são opcionais - deixe em branco para consultar todas as placas do período.
                            </div>
                            <div class="btn-group export-buttons" role="group">
                                <button type="button" class="btn btn-outline-secondary" onclick="limparFormulario()">
                                    <i class="fas fa-eraser me-2"></i>Limpar
                                </button>
                                <button type="button" class="btn btn-primary" onclick="consultarRelatorio()">
                                    <i class="fas fa-search me-2"></i>Consultar
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Card de Resultados -->
        <div class="card resultados-card" id="results-section">
            <div class="card-header resultados-card__header d-flex justify-content-between align-items-center flex-wrap gap-2">
                <div class="d-flex align-items-center gap-2 flex-wrap">
                    <span class="d-flex align-items-center resultados-card__title">
                        <i class="fas fa-table me-2"></i>Resultados da Consulta
                    </span>
                    <span class="badge bg-light text-dark border fw-semibold" id="totalRegistros">0 registros</span>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <div class="btn-group export-buttons" role="group">
                        <button type="button" class="btn btn-outline-danger" id="btnExportarPDF" onclick="exportarPDF()">
                            <i class="fas fa-file-pdf me-2"></i>Exportar PDF
                        </button>
                        <button type="button" class="btn btn-outline-success" id="btnExportarExcel" onclick="exportarExcel()">
                            <i class="fas fa-file-excel me-2"></i>Exportar Excel
                        </button>
                    </div>
                </div>
            </div>
            <div class="card-body resultados-card__body">
                <div class="table-responsive resultados-scroll">
                    <table class="table table-striped table-hover align-middle" id="tabela-relatorio-placas">
                        <thead id="resultados-thead"></thead>
                        <tbody id="resultados-tbody"></tbody>
                    </table>
                </div>

                <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mt-3">
                    <div class="d-flex align-items-center gap-3">
                        <div class="text-muted" id="infoPaginacao"></div>
                        <div class="pagination-size-selector">
                            <span>Exibir:</span>
                            <select id="tamanhoPagina" onchange="alterarTamanhoPagina()">
                                <option value="10">10</option>
                                <option value="50" selected>50</option>
                                <option value="100">100</option>
                                <option value="-1">Tudo</option>
                            </select>
                            <span>registros</span>
                        </div>
                    </div>
                    <div class="d-flex align-items-center gap-2 pagination-controls">
                        <button type="button" class="btn btn-outline-secondary btn-sm" id="btnPaginaAnterior">
                            <i class="fas fa-chevron-left"></i>
                        </button>
                        <span class="text-muted small" id="indicadorPagina">Página 1 de 1</span>
                        <button type="button" class="btn btn-outline-secondary btn-sm" id="btnPaginaProxima">
                            <i class="fas fa-chevron-right"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Loading Overlay -->
    <div id="loadingOverlay" class="loading-overlay hidden">
        <div class="loading-content">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Carregando...</span>
            </div>
            <p id="loadingMessage">Processando consulta...</p>
        </div>
    </div>

    <!-- Scripts -->
    <script src="/muralha-digital/assets/js/jspdf.umd.min.js"></script>
    <script src="/muralha-digital/assets/js/jspdf.plugin.autotable.min.js"></script>
    <script src="/muralha-digital/assets/js/xlsx.core.min.js"></script>
    
    <!-- Script customizado -->
    <script src="assets/js/script.js"></script>
</body>
</html>