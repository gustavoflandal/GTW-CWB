<%@ page language="java" pageEncoding="utf-8" %>
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

        <%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

            <html lang="pt-BR">

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <title>GTW - Relatório de Fluxo de Passagens Veiculares</title>
                <meta name="viewport" content="width=device-width, initial-scale=1">

                <!-- Chart.js -->
                <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
                <!-- SheetJS para exportação Excel -->
                <script src="https://cdn.sheetjs.com/xlsx-0.20.1/package/dist/xlsx.full.min.js"></script>
                <!-- Bootstrap Icons -->
                <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css"
                    rel="stylesheet">
                <!-- DataTables CSS -->
                <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.11.5/css/dataTables.bootstrap5.min.css">
                <!-- DataTables JS -->
                <script type="text/javascript" src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
                <script type="text/javascript" src="https://cdn.datatables.net/1.11.5/js/dataTables.bootstrap5.min.js"></script>

                <style>
                    /* Dashboard customizado com Bootstrap */
                    .dashboard-container {
                        background: #f8f9fa;
                        min-height: 100vh;
                        padding: 20px;
                        padding-left: 15px;
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

                    .stat-card-purple {
                        background: #7c3aed;
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

                    .chart-card {
                        background: white;
                        border-radius: 10px;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        margin-bottom: 20px;
                    }

                    .chart-header {
                        background: #1e40af;
                        color: white;
                        padding: 15px 20px;
                        border-radius: 10px 10px 0 0;
                        font-weight: 600;
                        font-size: 1rem;
                    }

                    .grid-table {
                        width: 100%;
                        border-collapse: collapse;
                        font-size: 0.9rem;
                    }

                    .grid-table th {
                        background-color: #f8f9fa;
                        border: 1px solid #dee2e6;
                        padding: 12px 8px;
                        text-align: center;
                        font-weight: 600;
                        color: #495057;
                    }

                    .grid-table td {
                        border: 1px solid #dee2e6;
                        padding: 10px 8px;
                        text-align: center;
                    }

                    .grid-table tbody tr:nth-child(even) {
                        background-color: #f8f9fa;
                    }

                    .grid-table tbody tr:hover {
                        background-color: #e3f2fd;
                    }

                    .form-container {
                        background: white;
                        border-radius: 10px;
                        padding: 25px;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        margin-bottom: 20px;
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

                    .loading {
                        display: none;
                        text-align: center;
                        padding: 50px;
                        color: #6c757d;
                    }

                    .spinner-border-custom {
                        width: 3rem;
                        height: 3rem;
                        border-width: 0.3em;
                    }

                    /* Estilo para impressão */
                    @media print {
                        /* Ocultar elementos não necessários na impressão */
                        .form-container, 
                        .btn, 
                        .no-print,
                        .btn-primary-custom,
                        .dataTables_length,
                        .dataTables_filter,
                        .dataTables_info,
                        .dataTables_paginate {
                            display: none !important;
                        }
                        
                        /* Reset de estilos gerais para impressão */
                        * {
                            -webkit-print-color-adjust: exact !important;
                            print-color-adjust: exact !important;
                            color-adjust: exact !important;
                        }

                        /* Container principal */
                        body {
                            margin: 0;
                            padding: 10px;
                            background: white !important;
                        }

                        .dashboard-container {
                            background: white !important;
                            padding: 0 10px !important;
                            min-height: auto !important;
                        }

                        .container {
                            max-width: 100% !important;
                            width: 100% !important;
                            padding: 0 10px !important;
                            margin: 0 auto !important;
                        }

                        /* Cabeçalho */
                        .row.mb-4 {
                            margin-bottom: 15px !important;
                        }

                        h2 {
                            font-size: 18pt !important;
                            color: #000 !important;
                            margin-bottom: 5px !important;
                        }

                        p.text-muted {
                            font-size: 10pt !important;
                            color: #333 !important;
                        }

                        /* Cards de resumo - ajustar para caber na página */
                        #cards-resumo {
                            display: flex !important;
                            flex-wrap: wrap !important;
                            page-break-inside: avoid;
                            margin-bottom: 15px !important;
                        }

                        #cards-resumo .col-lg-3 {
                            width: 24% !important;
                            max-width: 24% !important;
                            flex: 0 0 24% !important;
                            padding: 0 3px !important;
                            margin-bottom: 8px !important;
                        }

                        .stat-card {
                            box-shadow: none !important;
                            border: 1.5px solid #333 !important;
                            border-radius: 5px !important;
                            padding: 12px !important;
                            page-break-inside: avoid;
                            margin-bottom: 0 !important;
                            min-height: 85px !important;
                            background: white !important;
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

                        .stat-card-purple {
                            background: #f3e5f5 !important;
                            border-color: #7c3aed !important;
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

                        .stat-card small {
                            font-size: 8pt !important;
                            color: #666 !important;
                        }

                        /* Alert de período */
                        .alert-info {
                            background: #e3f2fd !important;
                            border: 1px solid #1e40af !important;
                            color: #000 !important;
                            padding: 8px !important;
                            margin-bottom: 10px !important;
                            page-break-inside: avoid;
                        }

                        /* Cards dos gráficos e tabelas */
                        .chart-card {
                            box-shadow: none !important;
                            border: 1px solid #999 !important;
                            border-radius: 5px !important;
                            page-break-inside: avoid;
                            margin-bottom: 15px !important;
                            background: white !important;
                        }

                        .chart-header {
                            background: #f5f5f5 !important;
                            color: #000 !important;
                            border-bottom: 2px solid #333 !important;
                            padding: 8px 12px !important;
                            font-size: 11pt !important;
                            font-weight: bold !important;
                        }

                        /* Tabelas - estilização aprimorada */
                        .grid-table {
                            width: 100% !important;
                            border-collapse: collapse !important;
                            font-size: 8pt !important;
                            page-break-inside: auto;
                        }

                        .grid-table thead {
                            display: table-header-group;
                        }

                        .grid-table tbody {
                            display: table-row-group;
                        }

                        .grid-table tr {
                            page-break-inside: avoid;
                            page-break-after: auto;
                        }

                        .grid-table th {
                            background-color: #f0f0f0 !important;
                            border: 1px solid #666 !important;
                            padding: 6px 4px !important;
                            text-align: center !important;
                            font-weight: bold !important;
                            color: #000 !important;
                            font-size: 8pt !important;
                        }

                        .grid-table td {
                            border: 1px solid #999 !important;
                            padding: 5px 4px !important;
                            text-align: center !important;
                            color: #000 !important;
                            font-size: 8pt !important;
                        }

                        .grid-table tbody tr:nth-child(even) {
                            background-color: #f9f9f9 !important;
                        }

                        .grid-table tbody tr:hover {
                            background-color: inherit !important;
                        }

                        /* Scroll containers */
                        .chart-card .p-3 {
                            max-height: none !important;
                            overflow: visible !important;
                            padding: 10px !important;
                        }

                        /* Ajustes de layout em grid */
                        .row {
                            display: flex !important;
                            flex-wrap: wrap !important;
                            page-break-inside: avoid;
                        }

                        .col-lg-6 {
                            width: 49% !important;
                            max-width: 49% !important;
                            flex: 0 0 49% !important;
                            padding: 0 5px !important;
                        }

                        /* Gráficos - esconder ou mostrar aviso */
                        canvas {
                            max-width: 100% !important;
                            height: auto !important;
                            page-break-inside: avoid;
                        }

                        /* Adicionar aviso sobre gráficos */
                        #graficoClassificacao::after,
                        #graficoHorario::after {
                            content: "(Gráfico - consulte versão digital para visualização interativa)";
                            display: block;
                            text-align: center;
                            font-size: 8pt;
                            color: #666;
                            margin-top: 5px;
                        }

                        /* Quebras de página estratégicas */
                        #tabelaFluxo {
                            page-break-before: auto;
                        }

                        .row.mb-4:first-of-type {
                            page-break-after: avoid;
                        }

                        /* Garantir que linhas de total fiquem destacadas */
                        .grid-table tr[style*="font-weight"] td,
                        .grid-table tbody tr:last-child td {
                            font-weight: bold !important;
                            background-color: #e0e0e0 !important;
                            border-top: 2px solid #333 !important;
                        }

                        /* Ajustar espaçamento geral */
                        .mb-3, .mb-4 {
                            margin-bottom: 10px !important;
                        }

                        /* Remover gradientes e sombras */
                        * {
                            box-shadow: none !important;
                            text-shadow: none !important;
                            background-image: none !important;
                        }

                        /* Garantir visibilidade de ícones Bootstrap */
                        .bi::before {
                            color: #000 !important;
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

                        /* Evitar página em branco no final */
                        .container:last-child,
                        .row:last-child,
                        #dashboard-section:last-child {
                            page-break-after: avoid !important;
                            margin-bottom: 0 !important;
                            padding-bottom: 0 !important;
                        }

                        /* Impressão em orientação paisagem para relatórios largos */
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
                                    <i class="bi bi-truck"></i> Relatório de Fluxo de Passagens Veiculares
                                </h2>
                                <p class="text-center text-muted">Análise detalhada do fluxo de veículos por local e período</p>
                            </div>
                        </div>

                        <!-- Formulário de Parâmetros -->
                        <div class="form-container no-print">
                            <form id="formRelatorio" onsubmit="gerarRelatorio(); return false;">
                                <div class="row">
                                    <div class="col-md-4 mb-3">
                                        <label for="txt_local" class="form-label fw-bold">
                                            <i class="bi bi-geo-alt"></i> Local
                                        </label>
                                        <select class="form-control" id="txt_local" required>
                                            <option value="">Selecione um local...</option>
                                        </select>
                                        <div class="form-text">Selecione o local para análise</div>
                                    </div>
                                    <div class="col-md-4 mb-3">
                                        <label for="txt_data_inicio" class="form-label fw-bold">
                                            <i class="bi bi-calendar"></i> Data Início
                                        </label>
                                        <input type="date" class="form-control" id="txt_data_inicio" required>
                                    </div>
                                    <div class="col-md-4 mb-3">
                                        <label for="txt_data_fim" class="form-label fw-bold">
                                            <i class="bi bi-calendar"></i> Data Fim
                                        </label>
                                        <input type="date" class="form-control" id="txt_data_fim" required>
                                    </div>
                                </div>
                                
                                <div class="row">
                                    <div class="col-12 text-center">
                                        <button type="submit" class="btn btn-primary-custom">
                                            <i class="bi bi-bar-chart-line"></i> Gerar Relatório
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>

                        <!-- Loading -->
                        <div class="loading" id="loading">
                            <div class="spinner-border spinner-border-custom text-primary" role="status">
                                <span class="visually-hidden">Carregando...</span>
                            </div>
                            <p class="mt-3">Processando dados...</p>
                        </div>

                        <!-- Dashboard Section -->
                        <div id="dashboard-section" style="display: none;">
                            <!-- Cards de Resumo -->
                            <div class="row" id="cards-resumo">
                                <!-- Cards serão criados dinamicamente -->
                            </div>

                            <!-- Grid de Fluxo por Hora -->
                            <div class="chart-card">
                                <div class="chart-header">Fluxo Detalhado por Hora e Classificação</div>
                                <div class="p-3">
                                    <table class="grid-table" id="tabelaFluxo">
                                        <thead>
                                            <tr>
                                                <th>Data</th>
                                                <th>Hora</th>
                                                <th>Sentido</th>
                                                <th>Classificação</th>
                                                <th>Passagens</th>
                                            </tr>
                                        </thead>
                                        <tbody id="tbodyFluxo">
                                            <!-- Dados serão inseridos aqui -->
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <!-- Tabelas Resumo Complementares -->
                            <div class="row mb-4">
                                <div class="col-lg-6 mb-4">
                                    <div class="chart-card">
                                        <div class="chart-header">Resumo por Classificação (Tabela)</div>
                                        <div class="p-3">
                                            <table class="grid-table" id="tabelaResumoClassificacao">
                                                <thead>
                                                    <tr>
                                                        <th>Classificação</th>
                                                        <th>Passagens</th>
                                                        <th>%</th>
                                                    </tr>
                                                </thead>
                                                <tbody id="tbodyResumoClassificacao">
                                                    <!-- preenchido via JS -->
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-lg-6 mb-4">
                                    <div class="chart-card">
                                        <div class="chart-header">Distribuição Horária (Tabela)</div>
                                        <div class="p-3">
                                            <table class="grid-table" id="tabelaDistribuicaoHoraria">
                                                <thead>
                                                    <tr>
                                                        <th>Hora</th>
                                                        <th>Passagens</th>
                                                    </tr>
                                                </thead>
                                                <tbody id="tbodyDistribuicaoHoraria">
                                                    <!-- preenchido via JS -->
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Gráficos -->
                            <div class="row">
                                <!-- Gráfico de Classificação -->
                                <div class="col-lg-6 mb-4">
                                    <div class="chart-card">
                                        <div class="chart-header">Resumo por Classificação de Veículos</div>
                                        <div class="p-3">
                                            <canvas id="graficoClassificacao" width="400" height="300"></canvas>
                                        </div>
                                    </div>
                                </div>

                                <!-- Gráfico de Distribuição Horária -->
                                <div class="col-lg-6 mb-4">
                                    <div class="chart-card">
                                        <div class="chart-header">Distribuição Horária</div>
                                        <div class="p-3">
                                            <canvas id="graficoHorario" width="400" height="300"></canvas>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Botões de Ação (após os gráficos) -->
                            <div id="action-buttons" class="text-center mt-4 mb-4 no-print" style="display: none;">
                                <button type="button" class="btn btn-outline-secondary me-2" onclick="imprimirRelatorio()" id="btnImprimir">
                                    <i class="bi bi-printer"></i> Imprimir
                                </button>
                                <button type="button" class="btn btn-outline-success me-2" onclick="exportarParaExcel()" id="btnExportarExcel">
                                    <i class="bi bi-file-earmark-excel"></i> Excel
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <script>
                    let dadosRelatorio = null;

                    // Carrega a lista de locais quando a página inicia
                    document.addEventListener('DOMContentLoaded', function() {
                        carregarLocais();
                    });

                    // Função para carregar a lista de locais via AJAX
                    function carregarLocais() {
                        const xhr = new XMLHttpRequest();
                        xhr.open('GET', '/relatorio/RelatorioFluxoPassagensVeiculares?acao=carregarLocais', true);
                        
                        xhr.onreadystatechange = function() {
                            if (xhr.readyState === 4) {
                                if (xhr.status === 200) {
                                    try {
                                        const dados = JSON.parse(xhr.responseText);
                                        console.log('DEBUG: Locais carregados:', dados);
                                        
                                        if (dados.erro) {
                                            console.error('Erro ao carregar locais:', dados.erro);
                                        } else {
                                            preencherSelectLocais(dados.locais);
                                        }
                                    } catch (e) {
                                        console.error('Erro ao processar locais:', e);
                                    }
                                } else {
                                    console.error('Erro na requisição de locais. Status:', xhr.status);
                                }
                            }
                        };
                        
                        xhr.send();
                    }

                    // Preenche o select de locais com os dados retornados
                    function preencherSelectLocais(locais) {
                        const selectLocal = document.getElementById('txt_local');
                        
                        // Limpar opções existentes exceto a primeira
                        while (selectLocal.options.length > 1) {
                            selectLocal.remove(1);
                        }
                        
                        // Adicionar opções de locais
                        if (locais && Array.isArray(locais)) {
                            locais.forEach(function(local) {
                                const option = document.createElement('option');
                                option.value = local.id;
                                option.text = local.nome;
                                selectLocal.appendChild(option);
                            });
                            console.log('DEBUG: ' + locais.length + ' locais adicionados ao select');
                        }
                    }

                    // Função principal para gerar relatório
                    function gerarRelatorio() {
                        const local = document.getElementById('txt_local').value;
                        const dataInicio = document.getElementById('txt_data_inicio').value;
                        const dataFim = document.getElementById('txt_data_fim').value;

                        if (!local || !dataInicio || !dataFim) {
                            alert('Por favor, preencha todos os campos obrigatórios.');
                            return;
                        }

                        // Validar datas
                        if (new Date(dataInicio) > new Date(dataFim)) {
                            alert('A data de início deve ser anterior à data final.');
                            return;
                        }

                        // Mostrar loading
                        document.getElementById('loading').style.display = 'block';
                        document.getElementById('dashboard-section').style.display = 'none';
                        document.getElementById('action-buttons').style.display = 'none';

                        // Fazer requisição
                        const xhr = new XMLHttpRequest();
                        xhr.open('GET', '/relatorio/RelatorioFluxoPassagensVeiculares?acao=gerarRelatorio&local=' 
                                + encodeURIComponent(local) 
                                + '&dataInicio=' + encodeURIComponent(dataInicio)
                                + '&dataFim=' + encodeURIComponent(dataFim), true);
                        
                        xhr.onreadystatechange = function() {
                            if (xhr.readyState === 4) {
                                document.getElementById('loading').style.display = 'none';
                                
                                if (xhr.status === 200) {
                                    try {
                                        console.log('DEBUG: Resposta recebida do servidor:', xhr.responseText);
                                        const dados = JSON.parse(xhr.responseText);
                                        console.log('DEBUG: Dados parseados:', dados);
                                        
                                        if (dados.erro) {
                                            alert('Erro: ' + dados.erro);
                                        } else {
                                            dadosRelatorio = dados;
                                            console.log('DEBUG: Chamando criarDashboard com dados:', dados);
                                            criarDashboard(dados);
                                            document.getElementById('action-buttons').style.display = 'block';
                                        }
                                    } catch (e) {
                                        console.error('Erro ao processar dados:', e);
                                        console.error('Resposta do servidor:', xhr.responseText);
                                        alert('Erro ao processar dados: ' + e.message);
                                    }
                                } else {
                                    alert('Erro na comunicação com o servidor.');
                                }
                            }
                        };
                        
                        xhr.send();
                    }

                    // Função para criar dashboard
                    function criarDashboard(dados) {
                        console.log('DEBUG: criarDashboard iniciado com dados:', dados);
                        console.log('DEBUG: dados.fluxo_por_hora:', (dados.fluxo_por_hora ? dados.fluxo_por_hora.length : 0));
                        console.log('DEBUG: dados.resumo_classificacao:', (dados.resumo_classificacao ? dados.resumo_classificacao.length : 0));
                        console.log('DEBUG: dados.distribuicao_horaria:', (dados.distribuicao_horaria ? dados.distribuicao_horaria.length : 0));
                        console.log('DEBUG: dados.distribuicao_sentido:', (dados.distribuicao_sentido ? dados.distribuicao_sentido.length : 0));

                        try { criarCards(dados); } catch(e){ console.error('ERRO criarCards', e);}    
                        try { criarTabelaFluxo(dados.fluxo_por_hora || []); } catch(e){ console.error('ERRO criarTabelaFluxo', e);}  

                        // Preparar dados agregados e ordenados para gráfico horário
                        let distribuicaoHorariaOrdenada = [];
                        if (Array.isArray(dados.distribuicao_horaria)) {
                            const mapaHoras = new Map(); // chave: hora(0-23) valor: soma total_passagens
                            dados.distribuicao_horaria.forEach(item => {
                                var h = 0;
                                if (item.hora != null && item.hora !== '') {
                                    h = parseInt(item.hora,10);
                                }
                                if (!isNaN(h)) {
                                    var somaParcial = 0;
                                    if (item.total_passagens != null && item.total_passagens !== '') {
                                        somaParcial = parseInt(item.total_passagens,10);
                                        if (isNaN(somaParcial)) { somaParcial = 0; }
                                    }
                                    mapaHoras.set(h, (mapaHoras.get(h) || 0) + somaParcial);
                                }
                            });
                            // Garantir 0..23 presentes (mesmo com zero)
                            for (let h = 0; h < 24; h++) {
                                distribuicaoHorariaOrdenada.push({ hora: h, total_passagens: mapaHoras.get(h) || 0 });
                            }
                            console.log('DEBUG: distribuicaoHoraria agregada 0..23:', distribuicaoHorariaOrdenada);
                        }

                        // Preencher tabelas adicionais
                        try { criarTabelaResumoClassificacao(dados.resumo_classificacao || []); } catch(e){ console.error('ERRO criarTabelaResumoClassificacao', e);} 
                        try { criarTabelaDistribuicaoHoraria(distribuicaoHorariaOrdenada); } catch(e){ console.error('ERRO criarTabelaDistribuicaoHoraria', e);} 

                        // Gráficos
                        try { criarGraficoClassificacao(dados.resumo_classificacao || []); } catch(e){ console.error('ERRO criarGraficoClassificacao', e);} 
                        try { criarGraficoHorario(distribuicaoHorariaOrdenada); } catch(e){ console.error('ERRO criarGraficoHorario', e);} 

                        const dashboardSection = document.getElementById('dashboard-section');
                        if (dashboardSection) {
                            dashboardSection.style.display = 'block';
                            dashboardSection.classList.add('render-ready');
                        }

                        // Forçar reflow para alguns navegadores
                        void dashboardSection.offsetHeight;

                        setTimeout(()=>{
                            try { dashboardSection.scrollIntoView({behavior:'smooth'}); } catch(_){}
                        },150);
                        console.log('DEBUG: criarDashboard finalizado');
                    }

                    // Função para criar cards de resumo
                    function criarCards(dados) {
                        console.log('DEBUG: criarCards iniciado com dados:', dados);
                        const container = document.getElementById('cards-resumo');
                        if(!container){ console.error('cards-resumo não encontrado'); return; }
                        container.innerHTML = '';

                        let totalPassagens = 0;
                        (dados.resumo_classificacao||[]).forEach(function(it){
                            var v = 0;
                            if (it && it.total_passagens != null && it.total_passagens !== '') {
                                v = parseInt(it.total_passagens,10);
                                if (isNaN(v)) { v = 0; }
                            }
                            totalPassagens += v;
                        });
                        const totalClassificacoes = (dados.resumo_classificacao||[]).length;
                        const totalSentidos = (dados.distribuicao_sentido||[]).length;
                        // horas distintas de fluxo_por_hora
                        const horasDistintas = new Set((dados.fluxo_por_hora||[]).map(r=> r.hora + '|' + r.data)).size; 

                        const nomeLocal = (dados.fluxo_por_hora && dados.fluxo_por_hora[0]) ? ( (dados.fluxo_por_hora[0].nome_local||'') + '' ).trim() : ('Local ' + ((document.getElementById('txt_local') ? document.getElementById('txt_local').value : '')));
                        const periodo = (dados.info_relatorio && dados.info_relatorio.periodo) ? dados.info_relatorio.periodo : '';

                        var htmlCards = '';
                        htmlCards += '<div class="col-lg-3 col-md-6 mb-3">'+
                                     '<div class="stat-card stat-card-blue shadow-sm">'+
                                     '<div class="stat-icon"><i class="bi bi-truck"></i></div>'+ 
                                     '<div class="stat-value">'+ totalPassagens.toLocaleString('pt-BR') +'</div>'+ 
                                     '<div class="stat-title">Total de Passagens</div>'+ 
                                     '<small>'+ nomeLocal +'</small>'+ 
                                     '</div></div>';
                        htmlCards += '<div class="col-lg-3 col-md-6 mb-3">'+
                                     '<div class="stat-card stat-card-green shadow-sm">'+
                                     '<div class="stat-icon"><i class="bi bi-list"></i></div>'+ 
                                     '<div class="stat-value">'+ totalClassificacoes +'</div>'+ 
                                     '<div class="stat-title">Tipos de Veículos</div>'+ 
                                     '<small>Classificações</small>'+ 
                                     '</div></div>';
                        htmlCards += '<div class="col-lg-3 col-md-6 mb-3">'+
                                     '<div class="stat-card stat-card-yellow shadow-sm">'+
                                     '<div class="stat-icon"><i class="bi bi-clock"></i></div>'+ 
                                     '<div class="stat-value">'+ horasDistintas +'</div>'+ 
                                     '<div class="stat-title">Intervalos</div>'+ 
                                     '<small>Data/Hora distintos</small>'+ 
                                     '</div></div>';
                        htmlCards += '<div class="col-lg-3 col-md-6 mb-3">'+
                                     '<div class="stat-card stat-card-purple shadow-sm">'+
                                     '<div class="stat-icon"><i class="bi bi-arrows"></i></div>'+ 
                                     '<div class="stat-value">'+ totalSentidos +'</div>'+ 
                                     '<div class="stat-title">Sentidos</div>'+ 
                                     '<small>Direções</small>'+ 
                                     '</div></div>';
                        container.insertAdjacentHTML('afterbegin', htmlCards);

                        if (periodo) {
                            container.insertAdjacentHTML('beforeend', `<div class="col-12"><div class="alert alert-info text-center py-2"><i class="bi bi-calendar-range"></i> <strong>Período:</strong> ${periodo}</div></div>`);
                        }
                        console.log('DEBUG cards HTML length:', container.innerHTML.length);
                    }

                    // Função para criar tabela de fluxo por hora
                    function criarTabelaFluxo(dados) {
                        console.log('DEBUG: criarTabelaFluxo iniciado com', dados.length, 'registros');
                        
                        // Destruir DataTable existente se houver
                        if ($.fn.DataTable.isDataTable('#tabelaFluxo')) {
                            $('#tabelaFluxo').DataTable().destroy();
                        }
                        
                        const tbody = document.getElementById('tbodyFluxo');
                        if(!tbody){ console.error('tbodyFluxo não encontrado'); return; }
                        tbody.innerHTML='';
                        const frag = document.createDocumentFragment();

                        if(!Array.isArray(dados) || dados.length===0){
                            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Nenhum dado encontrado</td></tr>';
                            return;
                        }
                        let count=0;
                        dados.forEach(item => {
                            const tr = document.createElement('tr');
                            const dataFormatada = new Date(item.data + 'T00:00:00').toLocaleDateString('pt-BR');
                            const horaFormatada = String(item.hora).padStart(2,'0') + ':00';
                            var tot = 0; 
                            if (item.total_passagens != null && item.total_passagens !== '') {
                                tot = parseInt(item.total_passagens,10);
                                if (isNaN(tot)) { tot = 0; }
                            }
                            tr.innerHTML = '<td>'+dataFormatada+'</td><td>'+horaFormatada+'</td><td>'+((item.sentido||'').trim())+'</td><td>'+((item.classificacao_veiculo||'').trim())+'</td><td><strong>'+ tot.toLocaleString('pt-BR') +'</strong></td>';
                            frag.appendChild(tr);
                            if(++count<5) console.log('Linha exemplo', count, tr.innerHTML);
                        });
                        tbody.appendChild(frag);
                        console.log('DEBUG: tabela populada, linhas:', tbody.querySelectorAll('tr').length);
                        
                        // Inicializar DataTables
                        $('#tabelaFluxo').DataTable({
                            pageLength: 10,
                            lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, 'Todos']],
                            language: {
                                lengthMenu: 'Mostrar _MENU_ registros por página',
                                zeroRecords: 'Nenhum registro encontrado',
                                info: 'Mostrando página _PAGE_ de _PAGES_',
                                infoEmpty: 'Nenhum registro disponível',
                                infoFiltered: '(filtrado de _MAX_ registros no total)',
                                search: 'Buscar:',
                                paginate: {
                                    first: 'Primeiro',
                                    last: 'Último',
                                    next: 'Próximo',
                                    previous: 'Anterior'
                                }
                            },
                            order: [[0, 'asc'], [1, 'asc']]
                        });
                    }

                    // Função para criar gráfico de classificação
                    function criarGraficoClassificacao(dados) {
                        console.log('DEBUG: criarGraficoClassificacao iniciado com dados:', dados);
                        
                        if (typeof Chart === 'undefined') {
                            console.error('DEBUG: Chart.js não está carregado!');
                            return;
                        }
                        
                        const canvas = document.getElementById('graficoClassificacao');
                        console.log('DEBUG: Canvas graficoClassificacao encontrado:', canvas);
                        
                        if (!canvas) {
                            console.error('DEBUG: Canvas graficoClassificacao não encontrado!');
                            return;
                        }
                        
                        const ctx = canvas.getContext('2d');
                        
                        if (window.chartClassificacao) {
                            window.chartClassificacao.destroy();
                        }
                        
                        const labels = dados.map(item => item.classificacao_veiculo);
                        const values = dados.map(item => item.total_passagens);
                        const percentuais = dados.map(item => item.percentual);
                        
                        const cores = [
                            '#1e40af', '#059669', '#d97706', '#dc2626', '#7c3aed',
                            '#0891b2', '#be185d', '#374151', '#92400e', '#065f46'
                        ];
                        
                        window.chartClassificacao = new Chart(ctx, {
                            type: 'bar',
                            data: {
                                labels: labels,
                                datasets: [{
                                    label: 'Passagens',
                                    data: values,
                                    backgroundColor: cores.slice(0, labels.length),
                                    borderColor: cores.slice(0, labels.length),
                                    borderWidth: 1
                                }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    title: {
                                        display: false
                                    },
                                    legend: {
                                        display: false
                                    },
                                    tooltip: {
                                        callbacks: {
                                            label: function(context) {
                                                const percentual = percentuais[context.dataIndex];
                                                return `${context.formattedValue} passagens (${percentual}%)`;
                                            }
                                        }
                                    }
                                },
                                scales: {
                                    y: {
                                        beginAtZero: true,
                                        ticks: {
                                            callback: function(value) {
                                                return value.toLocaleString('pt-BR');
                                            }
                                        }
                                    },
                                    x: {
                                        ticks: {
                                            maxRotation: 45,
                                            minRotation: 0
                                        }
                                    }
                                }
                            }
                        });
                    }

                    // Tabela Resumo Classificação
                    function criarTabelaResumoClassificacao(lista){
                        console.log('DEBUG: criarTabelaResumoClassificacao', lista ? lista.length : 0);
                        
                        // Destruir DataTable existente se houver
                        if ($.fn.DataTable.isDataTable('#tabelaResumoClassificacao')) {
                            $('#tabelaResumoClassificacao').DataTable().destroy();
                        }
                        
                        var tbody = document.getElementById('tbodyResumoClassificacao');
                        if(!tbody){ console.warn('tbodyResumoClassificacao não encontrado'); return; }
                        tbody.innerHTML='';
                        if(!Array.isArray(lista) || lista.length===0){
                            tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">Sem dados</td></tr>';
                            return;
                        }
                        // ordenar por total desc
                        var ordenado = lista.slice().sort(function(a,b){ return (b.total_passagens||0) - (a.total_passagens||0); });
                        var totalGeral = 0; ordenado.forEach(function(it){ totalGeral += (parseInt(it.total_passagens,10)||0); });
                        var frag = document.createDocumentFragment();
                        ordenado.forEach(function(it){
                            var tr = document.createElement('tr');
                            var tot = parseInt(it.total_passagens,10)||0;
                            var perc = totalGeral>0 ? ((tot/totalGeral)*100).toFixed(2) : '0.00';
                            tr.innerHTML = '<td>'+ ((it.classificacao_veiculo||'').trim()) +'</td>'+
                                           '<td>'+ tot.toLocaleString('pt-BR') +'</td>'+
                                           '<td>'+ perc.replace('.',',') +'%</td>';
                            frag.appendChild(tr);
                        });
                        tbody.appendChild(frag);
                        // Linha total
                        var trTotal = document.createElement('tr');
                        trTotal.style.fontWeight='bold';
                        trTotal.innerHTML = '<td>Total</td><td>'+ totalGeral.toLocaleString('pt-BR') +'</td><td>100%</td>';
                        tbody.appendChild(trTotal);
                        
                        // Inicializar DataTables
                        $('#tabelaResumoClassificacao').DataTable({
                            pageLength: 10,
                            lengthMenu: [[10, 25, 50, -1], [10, 25, 50, 'Todos']],
                            language: {
                                lengthMenu: 'Mostrar _MENU_ registros por página',
                                zeroRecords: 'Nenhum registro encontrado',
                                info: 'Mostrando página _PAGE_ de _PAGES_',
                                infoEmpty: 'Nenhum registro disponível',
                                infoFiltered: '(filtrado de _MAX_ registros no total)',
                                search: 'Buscar:',
                                paginate: {
                                    first: 'Primeiro',
                                    last: 'Último',
                                    next: 'Próximo',
                                    previous: 'Anterior'
                                }
                            },
                            order: [[1, 'desc']]
                        });
                    }

                    // Tabela Distribuição Horária (agregada 0..23)
                    function criarTabelaDistribuicaoHoraria(lista){
                        console.log('DEBUG: criarTabelaDistribuicaoHoraria', lista ? lista.length : 0);
                        
                        // Destruir DataTable existente se houver
                        if ($.fn.DataTable.isDataTable('#tabelaDistribuicaoHoraria')) {
                            $('#tabelaDistribuicaoHoraria').DataTable().destroy();
                        }
                        
                        var tbody = document.getElementById('tbodyDistribuicaoHoraria');
                        if(!tbody){ console.warn('tbodyDistribuicaoHoraria não encontrado'); return; }
                        tbody.innerHTML='';
                        if(!Array.isArray(lista) || lista.length===0){
                            tbody.innerHTML = '<tr><td colspan="2" class="text-center text-muted">Sem dados</td></tr>';
                            return;
                        }
                        var total = 0; lista.forEach(function(r){ total += (parseInt(r.total_passagens,10)||0); });
                        var frag = document.createDocumentFragment();
                        lista.forEach(function(r){
                            var tr = document.createElement('tr');
                            var tot = parseInt(r.total_passagens,10)||0;
                            tr.innerHTML = '<td>'+ String(r.hora).padStart(2,'0') +':00</td><td>'+ tot.toLocaleString('pt-BR') +'</td>';
                            frag.appendChild(tr);
                        });
                        tbody.appendChild(frag);
                        var trTot = document.createElement('tr');
                        trTot.style.fontWeight='bold';
                        trTot.innerHTML = '<td>Total</td><td>'+ total.toLocaleString('pt-BR') +'</td>';
                        tbody.appendChild(trTot);
                        
                        // Inicializar DataTables
                        $('#tabelaDistribuicaoHoraria').DataTable({
                            pageLength: 10,
                            lengthMenu: [[10, 25, 50, -1], [10, 25, 50, 'Todos']],
                            language: {
                                lengthMenu: 'Mostrar _MENU_ registros por página',
                                zeroRecords: 'Nenhum registro encontrado',
                                info: 'Mostrando página _PAGE_ de _PAGES_',
                                infoEmpty: 'Nenhum registro disponível',
                                infoFiltered: '(filtrado de _MAX_ registros no total)',
                                search: 'Buscar:',
                                paginate: {
                                    first: 'Primeiro',
                                    last: 'Último',
                                    next: 'Próximo',
                                    previous: 'Anterior'
                                }
                            },
                            order: [[0, 'asc']]
                        });
                    }

                    // Função para criar gráfico horário
                    function criarGraficoHorario(dados) {
                        console.log('DEBUG: criarGraficoHorario iniciado com dados agregados:', dados);
                        if (typeof Chart === 'undefined') { console.error('Chart.js não carregado'); return; }
                        const canvas = document.getElementById('graficoHorario');
                        if(!canvas){ console.error('Canvas graficoHorario ausente'); return; }
                        const ctx = canvas.getContext('2d');
                        if (window.chartHorario) window.chartHorario.destroy();
                        const labels = dados.map(it => String(it.hora).padStart(2,'0')+':00');
                        const values = dados.map(it => it.total_passagens);
                        window.chartHorario = new Chart(ctx, { type:'line', data:{ labels, datasets:[{ label:'Passagens por Hora (0..23)', data: values, borderColor:'#1e40af', backgroundColor:'rgba(30,64,175,.15)', tension:.25, fill:true }]}, options:{ responsive:true, maintainAspectRatio:false, scales:{ y:{ beginAtZero:true, ticks:{ callback:v=> v.toLocaleString('pt-BR') }}}}});
                    }

                    // Função para imprimir relatório
                    function imprimirRelatorio() {
                        window.print();
                    }
                    
                    // Função para exportar para Excel
                    function exportarParaExcel() {
                        if (!dadosRelatorio) {
                            alert('Não há dados para exportar. Gere o relatório primeiro.');
                            return;
                        }
                        
                        try {
                            const wb = XLSX.utils.book_new();
                            
                            // Resumo
                            let totalPassagens = 0;
                            (dadosRelatorio.resumo_classificacao||[]).forEach(it => {
                                totalPassagens += parseInt(it.total_passagens,10) || 0;
                            });
                            
                            const resumoData = [
                                ['Relatório de Fluxo de Passagens Veiculares'],
                                [''],
                                ['Local', document.getElementById('txt_local').value],
                                ['Período', document.getElementById('txt_data_inicio').value + ' a ' + document.getElementById('txt_data_fim').value],
                                ['Total de Passagens', totalPassagens],
                                ['Classificações', (dadosRelatorio.resumo_classificacao||[]).length]
                            ];
                            XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(resumoData), 'Resumo');
                            
                            // Fluxo por hora
                            if (dadosRelatorio.fluxo_por_hora && dadosRelatorio.fluxo_por_hora.length > 0) {
                                const fluxoData = [['Data', 'Hora', 'Sentido', 'Classificação', 'Passagens']];
                                dadosRelatorio.fluxo_por_hora.forEach(item => {
                                    fluxoData.push([item.data, item.hora + ':00', item.sentido, item.classificacao_veiculo, item.total_passagens]);
                                });
                                XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(fluxoData), 'Fluxo por Hora');
                            }
                            
                            // Resumo por classificação
                            if (dadosRelatorio.resumo_classificacao && dadosRelatorio.resumo_classificacao.length > 0) {
                                const classData = [['Classificação', 'Passagens', 'Percentual']];
                                dadosRelatorio.resumo_classificacao.forEach(item => {
                                    classData.push([item.classificacao_veiculo, item.total_passagens, item.percentual + '%']);
                                });
                                XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(classData), 'Por Classificação');
                            }
                            
                            const nomeArquivo = 'Relatorio_Fluxo_Passagens_' + new Date().toISOString().split('T')[0] + '.xlsx';
                            XLSX.writeFile(wb, nomeArquivo);
                            
                        } catch (e) {
                            console.error('Erro ao exportar para Excel:', e);
                            alert('Erro ao exportar para Excel: ' + e.message);
                        }
                    }

                    // Definir datas padrão (últimos 7 dias)
                    window.onload = function () {
                        const hoje = new Date();
                        const seteDiasAtras = new Date();
                        seteDiasAtras.setDate(hoje.getDate() - 7);

                        document.getElementById('txt_data_fim').value = hoje.toISOString().split('T')[0];
                        document.getElementById('txt_data_inicio').value = seteDiasAtras.toISOString().split('T')[0];
                        document.getElementById('txt_local').value = '100'; // Valor padrão de exemplo
                    };
                </script>
            </body>

            </html>