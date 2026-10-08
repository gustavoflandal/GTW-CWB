<%@ page language="java" pageEncoding="utf-8" %>
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

        <%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

            <html lang="pt-BR">

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <title>GTW - Relatório da Distribuição dos Tipos de Fatos Registrados</title>
                <meta name="viewport" content="width=device-width, initial-scale=1">

                <!-- Chart.js -->
                <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
                <!-- SheetJS para exportação Excel -->
                <script src="https://cdn.sheetjs.com/xlsx-0.20.1/package/dist/xlsx.full.min.js"></script>
                <!-- Bootstrap Icons -->
                <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css"
                    rel="stylesheet">
                <!-- Font Awesome -->
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

                <style>
                    /* Dashboard customizado com Bootstrap */
                    .dashboard-container {
                        background: #f8f9fa;
                        min-height: 100vh;
                        padding: 20px;
                        padding-left: 15px;
                    }

                    .form-container {
                        background: white;
                        border-radius: 10px;
                        padding: 25px;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        margin-bottom: 20px;
                        max-width: 700px;
                        margin-left: auto;
                        margin-right: auto;
                    }

                    .btn-primary-custom {
                        background: #1e40af;
                        border: none;
                        border-radius: 8px;
                        padding: 12px 24px;
                        font-weight: 600;
                        color: white;
                        transition: all 0.3s ease;
                    }

                    .btn-primary-custom:hover {
                        background: #1e3a8a;
                        box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3);
                    }

                    /* Estilos customizados para cards de estatísticas */
                    .stat-card {
                        border-radius: 10px;
                        padding: 25px;
                        color: white;
                        position: relative;
                        margin-bottom: 20px;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }

                    .stat-card-blue {
                        background: #1e40af;
                    }

                    .stat-card-green {
                        background: #059669;
                    }

                    .stat-card-yellow {
                        background: #d97706;
                    }

                    .stat-card-cyan {
                        background: #0891b2;
                    }

                    .stat-icon {
                        position: absolute;
                        top: 20px;
                        right: 20px;
                        font-size: 28px;
                        opacity: 0.8;
                    }

                    .stat-value {
                        font-size: 2.5rem;
                        font-weight: bold;
                        margin-bottom: 5px;
                    }

                    .stat-title {
                        font-size: 0.9rem;
                        opacity: 0.9;
                        margin-bottom: 10px;
                    }

                    .chart-container {
                        position: relative;
                        height: 400px;
                        padding: 20px;
                    }

                    .loading-overlay {
                        display: none;
                        position: fixed;
                        top: 0;
                        left: 0;
                        width: 100%;
                        height: 100%;
                        background: rgba(0, 0, 0, 0.5);
                        z-index: 9999;
                        justify-content: center;
                        align-items: center;
                    }

                    .loading-content {
                        background: white;
                        padding: 30px;
                        border-radius: 10px;
                        text-align: center;
                        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
                    }

                    .spinner {
                        border: 4px solid #f3f3f3;
                        border-top: 4px solid #3b82f6;
                        border-radius: 50%;
                        width: 40px;
                        height: 40px;
                        animation: spin 1s linear infinite;
                        margin: 0 auto 15px;
                    }

                    @keyframes spin {
                        0% { transform: rotate(0deg); }
                        100% { transform: rotate(360deg); }
                    }

                    /* Correção de margem esquerda para tela */
                    .container {
                        padding-left: 15px !important;
                        padding-right: 15px !important;
                    }
                    
                    .row {
                        margin-left: 0 !important;
                        margin-right: 0 !important;
                    }
                    
                    .row > [class*="col-"] {
                        padding-left: 10px !important;
                        padding-right: 10px !important;
                    }
                    
                    /* Estilo para linhas clicáveis */
                    #dados_tabela tr {
                        transition: background-color 0.2s ease;
                    }
                    
                    #dados_tabela tr:hover {
                        background-color: #e3f2fd !important;
                    }

                    /* Estilo para impressão */
                    @media print {
                        #divFiltrosConsulta, .btn, .no-print, button, .loading-overlay, .form-container,
                        .dataTables_length, .dataTables_filter, .dataTables_info, .dataTables_paginate,
                        #selectQtdRegistros, #paginacaoNav, #infoPaginacao,
                        .d-flex.justify-content-between.align-items-center {
                            display: none !important;
                        }

                        .dashboard-container {
                            background: white !important;
                            padding: 0 10px !important;
                            min-height: auto !important;
                        }
                        
                        * {
                            -webkit-print-color-adjust: exact !important;
                            print-color-adjust: exact !important;
                            color-adjust: exact !important;
                        }

                        body {
                            margin: 0;
                            padding: 10px;
                            background: white !important;
                        }

                        .container {
                            max-width: 100% !important;
                            width: 100% !important;
                            padding: 0 10px !important;
                            margin: 0 auto !important;
                        }

                        h2, h5 {
                            font-size: 16pt !important;
                            color: #000 !important;
                            margin-bottom: 10px !important;
                        }

                        p.text-muted {
                            font-size: 10pt !important;
                            color: #333 !important;
                        }

                        #stats_cards .row {
                            display: flex !important;
                            flex-wrap: wrap !important;
                            page-break-inside: avoid;
                        }

                        #stats_cards .col-md-3, #stats_cards .col-lg-3 {
                            width: 24% !important;
                            max-width: 24% !important;
                            flex: 0 0 24% !important;
                            padding: 0 3px !important;
                        }

                        .stat-card {
                            box-shadow: none !important;
                            border: 1.5px solid #333 !important;
                            border-radius: 5px !important;
                            padding: 10px !important;
                            page-break-inside: avoid;
                            margin-bottom: 8px !important;
                            min-height: 75px !important;
                        }

                        .stat-card-blue {
                            background: #e3f2fd !important;
                            border-color: #1e40af !important;
                        }

                        .stat-card-green {
                            background: #e8f5e9 !important;
                            border-color: #059669 !important;
                        }

                        .stat-card-yellow {
                            background: #fff3e0 !important;
                            border-color: #d97706 !important;
                        }

                        .stat-card-cyan {
                            background: #e0f7fa !important;
                            border-color: #0891b2 !important;
                        }

                        .stat-value {
                            font-size: 16pt !important;
                            color: #000 !important;
                        }

                        .stat-title {
                            font-size: 9pt !important;
                            color: #333 !important;
                        }

                        .stat-icon {
                            font-size: 18px !important;
                            color: #666 !important;
                        }

                        .card {
                            box-shadow: none !important;
                            border: 1px solid #999 !important;
                            border-radius: 5px !important;
                            page-break-inside: avoid;
                            margin-bottom: 15px !important;
                        }

                        .card-header {
                            background: #f5f5f5 !important;
                            color: #000 !important;
                            border-bottom: 2px solid #333 !important;
                            padding: 8px 12px !important;
                        }

                        .card-title {
                            font-size: 11pt !important;
                            font-weight: bold !important;
                            color: #000 !important;
                        }

                        .chart-container {
                            height: auto !important;
                            max-height: 300px !important;
                        }

                        canvas {
                            max-width: 100% !important;
                            height: auto !important;
                            page-break-inside: avoid;
                        }

                        table {
                            border-collapse: collapse !important;
                            font-size: 8pt !important;
                            page-break-inside: auto;
                        }

                        table thead {
                            display: table-header-group;
                        }

                        table tbody {
                            display: table-row-group;
                        }

                        table tr {
                            page-break-inside: avoid;
                            page-break-after: auto;
                        }

                        /* Tabelas - ajuste para não cortar colunas */
                        table {
                            font-size: 7pt !important;
                            width: 100% !important;
                            table-layout: fixed !important;
                        }

                        table th {
                            background-color: #f0f0f0 !important;
                            border: 1px solid #666 !important;
                            padding: 4px 2px !important;
                            color: #000 !important;
                            font-weight: bold !important;
                            word-wrap: break-word !important;
                            overflow-wrap: break-word !important;
                            white-space: normal !important;
                        }

                        table td {
                            border: 1px solid #999 !important;
                            padding: 3px 2px !important;
                            color: #000 !important;
                            word-wrap: break-word !important;
                            overflow-wrap: break-word !important;
                            white-space: normal !important;
                        }
                        
                        .table-responsive {
                            overflow: visible !important;
                        }

                        * {
                            box-shadow: none !important;
                            text-shadow: none !important;
                            background-image: none !important;
                        }

                        /* Evitar páginas em branco */
                        html, body {
                            height: auto !important;
                            overflow: visible !important;
                        }

                        .dashboard-container {
                            height: auto !important;
                            min-height: 0 !important;
                            overflow: visible !important;
                        }

                        #dashboard-section {
                            height: auto !important;
                            min-height: 0 !important;
                            page-break-after: avoid !important;
                        }

                        #dashboard-container {
                            height: auto !important;
                            min-height: 0 !important;
                        }

                        /* Esconder elementos vazios */
                        .row:empty,
                        .col-md-3:empty,
                        .col-md-4:empty,
                        .col-md-6:empty,
                        .col-12:empty,
                        div:empty:not(canvas) {
                            display: none !important;
                        }

                        /* Controlar órfãos e viúvas */
                        p, td, th {
                            orphans: 3;
                            widows: 3;
                        }

                        /* Evitar quebras desnecessárias */
                        .mb-4 {
                            margin-bottom: 10px !important;
                        }

                        .p-3 {
                            padding: 8px !important;
                        }

                        /* Evitar página em branco no final */
                        .container:last-child,
                        .row:last-child,
                        #dashboard-section:last-child {
                            page-break-after: avoid !important;
                            margin-bottom: 0 !important;
                            padding-bottom: 0 !important;
                        }

                        @page {
                            size: A4 landscape;
                            margin: 1cm;
                        }

                        /* Remover margem da última página */
                        @page :last {
                            margin-bottom: 0;
                        }
                    }
                </style>
            </head>

            <body>
                <div class="dashboard-container">
                    <div class="container">
                        <!-- Cabeçalho -->
                        <div class="row mb-4">
                            <div class="col-12">
                                <h2 class="text-center text-dark mb-1">
                                    <i class="bi bi-pie-chart"></i> Relatório da Distribuição dos Tipos de Fatos Registrados
                                </h2>
                                <p class="text-center text-muted">Análise da distribuição dos tipos de fatos por período</p>
                            </div>
                        </div>

                        <!-- Formulário de Parâmetros -->
                        <div class="form-container no-print">
                            <form id="formRelatorio" onsubmit="gerarRelatorio(); return false;">
                                <div class="row justify-content-center">
                                    <div class="col-md-6 mb-3">
                                        <label for="txt_data_inicio" class="form-label fw-bold">
                                            <i class="bi bi-calendar"></i> Data Início
                                        </label>
                                        <input type="date" class="form-control" id="txt_data_inicio" required>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label for="txt_data_fim" class="form-label fw-bold">
                                            <i class="bi bi-calendar"></i> Data Fim
                                        </label>
                                        <input type="date" class="form-control" id="txt_data_fim" required>
                                    </div>
                                </div>
                                
                                <div class="row">
                                    <div class="col-12 text-center">
                                        <button type="submit" class="btn btn-primary-custom" id="btn_gerar">
                                            <i class="bi bi-bar-chart-line"></i> Gerar Relatório
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>

                <!-- Cards de Estatísticas -->
                <div id="stats_cards" class="mt-4" style="display: none;">
                    <div class="row" id="stats_cards_content">
                        <!-- Cards serão inseridos dinamicamente -->
                    </div>
                </div>

                <!-- Gráfico de Pizza e Resumo -->
                <div class="mt-4">
                    <div class="row">
                        <div class="col-md-6">
                            <div id="chart_card" class="card" style="display: none;">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-pie-chart me-2"></i>
                                        Distribuição por Tipo de Fato
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <div class="chart-container">
                                        <canvas id="pieChart"></canvas>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div id="summary_card" class="card" style="display: none;">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-info-circle me-2"></i>
                                        Resumo do Período
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <div id="summary_content">
                                        <!-- Conteúdo do resumo será inserido dinamicamente -->
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Tabela de Dados -->
                <div class="mt-4">
                    <div class="row">
                        <div class="col-12">
                            <div id="table_card" class="card" style="display: none;">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-table me-2"></i>
                                        Detalhamento dos Dados
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <!-- Controle de quantidade de registros -->
                                    <div class="d-flex justify-content-between align-items-center mb-3">
                                        <div class="d-flex align-items-center">
                                            <label class="me-2 mb-0">Mostrar</label>
                                            <select id="selectQtdRegistros" class="form-select form-select-sm" style="width: auto;" onchange="alterarQtdRegistros()">
                                                <option value="10">10</option>
                                                <option value="25" selected>25</option>
                                                <option value="50">50</option>
                                                <option value="100">100</option>
                                                <option value="-1">Todos</option>
                                            </select>
                                            <label class="ms-2 mb-0">registros por página</label>
                                        </div>
                                    </div>
                                    
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover">
                                            <thead class="table-dark">
                                                <tr>
                                                    <th>Tipo de Fato</th>
                                                    <th>Total de Registros</th>
                                                    <th>Primeira Ocorrência</th>
                                                    <th>Última Ocorrência</th>
                                                    <th>Percentual</th>
                                                </tr>
                                            </thead>
                                            <tbody id="dados_tabela">
                                                <!-- Dados serão inseridos dinamicamente -->
                                            </tbody>
                                        </table>
                                    </div>
                                    
                                    <!-- Rodapé com paginação -->
                                    <div class="d-flex justify-content-between align-items-center mt-3 p-2" style="background: #f8f9fa; border-radius: 8px;">
                                        <div id="infoPaginacao" class="text-muted small"></div>
                                        <nav><ul class="pagination pagination-sm mb-0" id="paginacaoNav"></ul></nav>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Botões de Ação (após a tabela) -->
                <div id="action-buttons" class="text-center mt-4 mb-4 no-print" style="display: none;">
                    <button type="button" class="btn btn-outline-secondary me-2" onclick="imprimirRelatorio()" id="btn_imprimir">
                        <i class="bi bi-printer"></i> Imprimir
                    </button>
                    <button type="button" class="btn btn-outline-success me-2" onclick="exportarParaExcel()" id="btn_exportar_excel">
                        <i class="bi bi-file-earmark-excel"></i> Excel
                    </button>
                </div>

                <!-- Mensagem quando não há dados -->
                <div id="no_data_message" class="mt-4" style="display: none;">
                    <div class="row">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-body text-center">
                                    <i class="bi bi-info-circle mb-3 text-muted" style="font-size: 3rem;"></i>
                                    <h5>Nenhum dado encontrado</h5>
                                    <p class="text-muted">Não foram encontrados registros para o período selecionado.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                    </div><!-- Fecha container -->
                </div><!-- Fecha dashboard-container -->

                <!-- Loading Overlay -->
                <div id="loading_overlay" class="loading-overlay">
                    <div class="loading-content">
                        <div class="spinner"></div>
                        <h5>Gerando Relatório...</h5>
                        <p class="text-muted mb-0">Por favor, aguarde enquanto processamos os dados.</p>
                    </div>
                </div>

                <script>
                    let dadosRelatorio = [];
                    let pieChart = null;
                    
                    // Variáveis de paginação
                    let paginaAtual = 1;
                    let registrosPorPagina = 25;
                    let dadosFiltrados = [];

                    // Inicializar datas padrão (últimos 30 dias)
                    document.addEventListener('DOMContentLoaded', function() {
                        const hoje = new Date();
                        const trintaDiasAtras = new Date();
                        trintaDiasAtras.setDate(hoje.getDate() - 30);

                        document.getElementById('txt_data_fim').value = hoje.toISOString().split('T')[0];
                        document.getElementById('txt_data_inicio').value = trintaDiasAtras.toISOString().split('T')[0];
                    });

                    function validarDatas() {
                        const dataInicio = document.getElementById('txt_data_inicio').value;
                        const dataFim = document.getElementById('txt_data_fim').value;

                        if (!dataInicio || !dataFim) {
                            alert('Por favor, selecione as datas de início e fim.');
                            return false;
                        }

                        if (new Date(dataInicio) > new Date(dataFim)) {
                            alert('A data de início deve ser anterior à data de fim.');
                            return false;
                        }

                        return true;
                    }

                    function gerarRelatorio() {
                        if (!validarDatas()) {
                            return;
                        }

                        // Mostrar loading
                        document.getElementById('loading_overlay').style.display = 'flex';
                        document.getElementById('btn_gerar').disabled = true;

                        const dataInicio = document.getElementById('txt_data_inicio').value;
                        const dataFim = document.getElementById('txt_data_fim').value;

                        // Fazer requisição AJAX para o servlet
                        const xhr = new XMLHttpRequest();
                        xhr.open('POST', '/relatorio/RelatorioDistribuicaoTiposFatosServlet', true);
                        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

                        xhr.onreadystatechange = function () {
                            if (xhr.readyState === 4) {
                                // Esconder loading
                                document.getElementById('loading_overlay').style.display = 'none';
                                document.getElementById('btn_gerar').disabled = false;

                                if (xhr.status === 200) {
                                    try {
                                        dadosRelatorio = JSON.parse(xhr.responseText);
                                        console.log('Dados recebidos:', dadosRelatorio);

                                        if (dadosRelatorio && dadosRelatorio.length > 0) {
                                            exibirDados(dadosRelatorio);
                                            document.getElementById('action-buttons').style.display = 'block';
                                        } else {
                                            exibirMensagemSemDados();
                                        }
                                    } catch (e) {
                                        console.error('Erro ao processar resposta JSON:', e);
                                        alert('Erro ao processar dados do servidor.');
                                    }
                                } else {
                                    console.error('Erro na requisição:', xhr.status, xhr.responseText);
                                    alert('Erro ao buscar dados do servidor.');
                                }
                            }
                        };

                        // Enviar dados
                        const params = 'acao=buscarDados&dataInicio=' + encodeURIComponent(dataInicio) + 
                                      '&dataFim=' + encodeURIComponent(dataFim);
                        xhr.send(params);
                    }

                    function exibirDados(dados) {
                        // Esconder mensagem de sem dados
                        document.getElementById('no_data_message').style.display = 'none';

                        // Calcular estatísticas
                        const totalRegistros = dados.reduce((sum, item) => sum + item.TotalRegistros, 0);
                        const tiposFatos = dados.length;
                        const maiorTipo = dados.reduce((max, item) => item.TotalRegistros > max.TotalRegistros ? item : max, dados[0]);

                        // Exibir cards de estatísticas
                        exibirCardsEstatisticas(totalRegistros, tiposFatos, maiorTipo);

                        // Exibir gráfico de pizza
                        exibirGraficoPizza(dados);

                        // Exibir resumo
                        exibirResumo(dados, totalRegistros);

                        // Exibir tabela
                        exibirTabela(dados);
                    }

                    function exibirCardsEstatisticas(totalRegistros, tiposFatos, maiorTipo) {
                        const statsCardsContent = document.getElementById('stats_cards_content');
                        
                        statsCardsContent.innerHTML = 
                            '<div class="col-md-3">' +
                                '<div class="stat-card stat-card-blue">' +
                                    '<i class="fas fa-exclamation-triangle stat-icon"></i>' +
                                    '<div class="stat-value">' + totalRegistros.toLocaleString() + '</div>' +
                                    '<div class="stat-title">Total de Registros</div>' +
                                '</div>' +
                            '</div>' +
                            '<div class="col-md-3">' +
                                '<div class="stat-card stat-card-green">' +
                                    '<i class="fas fa-list-ul stat-icon"></i>' +
                                    '<div class="stat-value">' + tiposFatos + '</div>' +
                                    '<div class="stat-title">Tipos de Fatos</div>' +
                                '</div>' +
                            '</div>' +
                            '<div class="col-md-3">' +
                                '<div class="stat-card stat-card-yellow">' +
                                    '<i class="fas fa-crown stat-icon"></i>' +
                                    '<div class="stat-value">' + maiorTipo.TotalRegistros + '</div>' +
                                    '<div class="stat-title">Maior Incidência</div>' +
                                '</div>' +
                            '</div>' +
                            '<div class="col-md-3">' +
                                '<div class="stat-card stat-card-cyan">' +
                                    '<i class="fas fa-percentage stat-icon"></i>' +
                                    '<div class="stat-value">' + maiorTipo.PercentualSobreTotal.toFixed(1) + '%</div>' +
                                    '<div class="stat-title">Maior Percentual</div>' +
                                '</div>' +
                            '</div>';
                        
                        document.getElementById('stats_cards').style.display = 'block';
                    }

                    function exibirGraficoPizza(dados) {
                        const ctx = document.getElementById('pieChart').getContext('2d');
                        
                        // Destruir gráfico anterior se existir
                        if (pieChart) {
                            pieChart.destroy();
                        }

                        // Cores para o gráfico
                        const cores = [
                            '#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6',
                            '#06b6d4', '#84cc16', '#f97316', '#ec4899', '#6b7280'
                        ];

                        pieChart = new Chart(ctx, {
                            type: 'pie',
                            data: {
                                labels: dados.map(item => item.TipoDeFato),
                                datasets: [{
                                    data: dados.map(item => item.TotalRegistros),
                                    backgroundColor: cores.slice(0, dados.length),
                                    borderWidth: 2,
                                    borderColor: '#ffffff'
                                }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: {
                                        position: 'bottom',
                                        labels: {
                                            padding: 20,
                                            usePointStyle: true
                                        }
                                    },
                                    tooltip: {
                                        callbacks: {
                                            label: function(context) {
                                                const label = context.label || '';
                                                const value = context.parsed;
                                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                                const percentage = ((value / total) * 100).toFixed(1);
                                                return label + ': ' + value + ' (' + percentage + '%)';
                                            }
                                        }
                                    }
                                }
                            }
                        });

                        document.getElementById('chart_card').style.display = 'block';
                    }

                    function exibirResumo(dados, totalRegistros) {
                        const dataInicio = document.getElementById('txt_data_inicio').value;
                        const dataFim = document.getElementById('txt_data_fim').value;
                        
                        const formatarData = (data) => {
                            return new Date(data).toLocaleDateString('pt-BR');
                        };

                        const resumoContent = document.getElementById('summary_content');
                        resumoContent.innerHTML = 
                            '<div class="mb-3">' +
                                '<strong>Período Analisado:</strong><br>' +
                                formatarData(dataInicio) + ' a ' + formatarData(dataFim) +
                            '</div>' +
                            '<div class="mb-3">' +
                                '<strong>Total de Registros:</strong> ' + totalRegistros.toLocaleString() +
                            '</div>' +
                            '<div class="mb-3">' +
                                '<strong>Tipos de Fatos Diferentes:</strong> ' + dados.length +
                            '</div>' +
                            '<div class="mb-3">' +
                                '<strong>Tipo Mais Frequente:</strong><br>' +
                                dados[0].TipoDeFato + ' (' + dados[0].TotalRegistros + ' registros)' +
                            '</div>' +
                            '<div>' +
                                '<strong>Média por Tipo:</strong> ' + (totalRegistros / dados.length).toFixed(1) + ' registros' +
                            '</div>';

                        document.getElementById('summary_card').style.display = 'block';
                    }

                    function exibirTabela(dados) {
                        // Armazenar dados para paginação
                        dadosFiltrados = dados;
                        paginaAtual = 1;
                        
                        // Aplicar paginação
                        aplicarPaginacao();
                        
                        document.getElementById('table_card').style.display = 'block';
                    }
                    
                    function formatarDataTabela(dataStr) {
                        if (!dataStr) return '-';
                        const data = new Date(dataStr);
                        return data.toLocaleDateString('pt-BR') + ' ' + data.toLocaleTimeString('pt-BR');
                    }
                    
                    function alterarQtdRegistros() {
                        const select = document.getElementById('selectQtdRegistros');
                        registrosPorPagina = parseInt(select.value);
                        paginaAtual = 1;
                        aplicarPaginacao();
                    }
                    
                    function irParaPagina(pagina) {
                        paginaAtual = pagina;
                        aplicarPaginacao();
                    }
                    
                    function aplicarPaginacao() {
                        const tbody = document.getElementById('dados_tabela');
                        const totalRegistros = dadosFiltrados.length;
                        const totalPaginas = registrosPorPagina === -1 ? 1 : Math.ceil(totalRegistros / registrosPorPagina);
                        
                        // Ajustar página atual se necessário
                        if (paginaAtual > totalPaginas) {
                            paginaAtual = Math.max(1, totalPaginas);
                        }
                        
                        // Calcular índices
                        let inicio, fim;
                        if (registrosPorPagina === -1) {
                            inicio = 0;
                            fim = totalRegistros;
                        } else {
                            inicio = (paginaAtual - 1) * registrosPorPagina;
                            fim = Math.min(inicio + registrosPorPagina, totalRegistros);
                        }
                        
                        // Renderizar linhas da página atual
                        const dadosPagina = dadosFiltrados.slice(inicio, fim);
                        tbody.innerHTML = dadosPagina.map(item => 
                            '<tr>' +
                                '<td><strong>' + item.TipoDeFato + '</strong></td>' +
                                '<td><span class="badge bg-primary">' + item.TotalRegistros.toLocaleString() + '</span></td>' +
                                '<td>' + formatarDataTabela(item.DataPrimeiraOcorrencia) + '</td>' +
                                '<td>' + formatarDataTabela(item.DataUltimaOcorrencia) + '</td>' +
                                '<td>' + item.PercentualSobreTotal.toFixed(1) + '%</td>' +
                            '</tr>'
                        ).join('');
                        
                        // Atualizar informações de paginação
                        const infoPaginacao = document.getElementById('infoPaginacao');
                        if (infoPaginacao) {
                            if (totalRegistros === 0) {
                                infoPaginacao.textContent = 'Nenhum registro encontrado';
                            } else {
                                infoPaginacao.textContent = 'Mostrando ' + (inicio + 1) + ' a ' + fim + ' de ' + totalRegistros + ' registros';
                            }
                        }
                        
                        // Atualizar navegação de páginas
                        atualizarNavegacaoPaginas(totalPaginas, totalRegistros);
                    }
                    
                    function atualizarNavegacaoPaginas(totalPaginas, totalRegistros) {
                        const nav = document.getElementById('paginacaoNav');
                        if (!nav) return;
                        
                        let html = '';
                        
                        if (registrosPorPagina === -1 || totalPaginas <= 1) {
                            nav.innerHTML = '';
                            return;
                        }
                        
                        // Botão Anterior
                        html += '<li class="page-item ' + (paginaAtual === 1 ? 'disabled' : '') + '">';
                        html += '<a class="page-link" href="javascript:void(0)" onclick="irParaPagina(' + (paginaAtual - 1) + ')">Anterior</a>';
                        html += '</li>';
                        
                        // Páginas
                        let paginasExibir = [];
                        if (totalPaginas <= 7) {
                            for (let i = 1; i <= totalPaginas; i++) paginasExibir.push(i);
                        } else {
                            paginasExibir.push(1);
                            if (paginaAtual > 3) paginasExibir.push('...');
                            for (let i = Math.max(2, paginaAtual - 1); i <= Math.min(totalPaginas - 1, paginaAtual + 1); i++) {
                                if (!paginasExibir.includes(i)) paginasExibir.push(i);
                            }
                            if (paginaAtual < totalPaginas - 2) paginasExibir.push('...');
                            paginasExibir.push(totalPaginas);
                        }
                        
                        paginasExibir.forEach(p => {
                            if (p === '...') {
                                html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
                            } else {
                                html += '<li class="page-item ' + (p === paginaAtual ? 'active' : '') + '">';
                                html += '<a class="page-link" href="javascript:void(0)" onclick="irParaPagina(' + p + ')">' + p + '</a>';
                                html += '</li>';
                            }
                        });
                        
                        // Botão Próximo
                        html += '<li class="page-item ' + (paginaAtual === totalPaginas ? 'disabled' : '') + '">';
                        html += '<a class="page-link" href="javascript:void(0)" onclick="irParaPagina(' + (paginaAtual + 1) + ')">Próximo</a>';
                        html += '</li>';
                        
                        nav.innerHTML = html;
                    }

                    function exibirMensagemSemDados() {
                        // Esconder todos os elementos de dados
                        document.getElementById('stats_cards').style.display = 'none';
                        document.getElementById('chart_card').style.display = 'none';
                        document.getElementById('summary_card').style.display = 'none';
                        document.getElementById('table_card').style.display = 'none';
                        
                        // Mostrar mensagem de sem dados
                        document.getElementById('no_data_message').style.display = 'block';
                        
                        // Esconder botões de ação
                        document.getElementById('action-buttons').style.display = 'none';
                    }

                    function imprimirRelatorio() {
                        if (!dadosRelatorio || dadosRelatorio.length === 0) {
                            alert('Não há dados para imprimir. Gere o relatório primeiro.');
                            return;
                        }

                        // Imprimir a página
                        window.print();
                    }
                    
                    function exportarParaExcel() {
                        if (!dadosRelatorio || dadosRelatorio.length === 0) {
                            alert('Não há dados para exportar. Gere o relatório primeiro.');
                            return;
                        }
                        
                        try {
                            // Criar workbook
                            const wb = XLSX.utils.book_new();
                            
                            // Dados do resumo
                            const totalRegistros = dadosRelatorio.reduce((sum, item) => sum + item.TotalRegistros, 0);
                            const dataInicio = document.getElementById('txt_data_inicio').value;
                            const dataFim = document.getElementById('txt_data_fim').value;
                            
                            const resumoData = [
                                ['Relatório de Distribuição dos Tipos de Fatos Registrados'],
                                [''],
                                ['Período', dataInicio + ' a ' + dataFim],
                                ['Total de Registros', totalRegistros],
                                ['Tipos de Fatos', dadosRelatorio.length],
                                ['Média por Tipo', (totalRegistros / dadosRelatorio.length).toFixed(1)]
                            ];
                            const wsResumo = XLSX.utils.aoa_to_sheet(resumoData);
                            XLSX.utils.book_append_sheet(wb, wsResumo, 'Resumo');
                            
                            // Dados detalhados
                            const detalheData = [
                                ['Tipo de Fato', 'Total de Registros', 'Primeira Ocorrência', 'Última Ocorrência', 'Percentual (%)']
                            ];
                            
                            dadosRelatorio.forEach(item => {
                                detalheData.push([
                                    item.TipoDeFato,
                                    item.TotalRegistros,
                                    item.DataPrimeiraOcorrencia || '-',
                                    item.DataUltimaOcorrencia || '-',
                                    item.PercentualSobreTotal.toFixed(2)
                                ]);
                            });
                            
                            const wsDetalhe = XLSX.utils.aoa_to_sheet(detalheData);
                            XLSX.utils.book_append_sheet(wb, wsDetalhe, 'Detalhamento');
                            
                            // Gerar arquivo
                            const nomeArquivo = 'Relatorio_Distribuicao_Tipos_Fatos_' + new Date().toISOString().split('T')[0] + '.xlsx';
                            XLSX.writeFile(wb, nomeArquivo);
                            
                        } catch (e) {
                            console.error('Erro ao exportar para Excel:', e);
                            alert('Erro ao exportar para Excel: ' + e.message);
                        }
                    }
                </script>
            </body>
            </html>
