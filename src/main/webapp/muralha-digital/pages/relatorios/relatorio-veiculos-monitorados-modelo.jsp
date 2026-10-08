<%@ page language="java" pageEncoding="utf-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<html lang="pt-BR">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>GTW - Relatório de Veículos Monitorados por Modelo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- SheetJS para exportação Excel -->
    <script src="https://cdn.sheetjs.com/xlsx-0.20.1/package/dist/xlsx.full.min.js"></script>
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    
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

        .status-indicator {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            display: inline-block;
            margin-right: 8px;
        }

        .status-success {
            background-color: #10b981;
        }

        .status-warning {
            background-color: #f59e0b;
        }

        .status-danger {
            background-color: #ef4444;
        }

        .status-info {
            background-color: #3b82f6;
        }

        .form-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            padding: 25px;
            margin-bottom: 30px;
            max-width: 700px;
            margin-left: auto;
            margin-right: auto;
        }

        .report-header {
            margin-bottom: 25px;
        }

        .report-title {
            color: #1e3a5f;
            font-size: 1.6rem;
            font-weight: 600;
            margin-bottom: 5px;
        }

        .report-subtitle {
            color: #64748b;
            font-size: 0.95rem;
        }

        .btn-primary-custom {
            background-color: #1e40af;
            border-color: #1e40af;
            border-radius: 8px;
            padding: 10px 25px;
            font-weight: 600;
            color: white !important;
            transition: all 0.3s ease;
        }

        .btn-primary-custom:hover {
            background-color: #1e3a8a;
            border-color: #1e3a8a;
            color: white !important;
            transform: translateY(-1px);
        }

        .grid-table {
            width: 100%;
            font-size: 0.85rem;
        }

        .grid-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #495057;
            padding: 10px 8px;
        }

        .grid-table td {
            padding: 8px;
        }

        .grid-table tbody tr:hover {
            background-color: #e3f2fd;
        }

        /* Título visível apenas na impressão */
        .print-title {
            display: none;
        }

        /* Estilo para impressão */
        @media print {
            /* Mostrar título na impressão */
            .print-title {
                display: block !important;
                text-align: center;
                font-size: 18pt;
                font-weight: bold;
                color: #000;
                margin: 0 0 20px 0;
                padding: 10px 0;
                border-bottom: 2px solid #333;
            }

            /* Ocultar elementos de filtro e controle */
            .form-container, .btn, .no-print, button, form, #loading_msg,
            .dataTables_length, .dataTables_filter, .dataTables_info, .dataTables_paginate {
                display: none !important;
            }

            /* Scroll containers */
            .chart-card .p-3,
            .p-3 {
                max-height: none !important;
                overflow: visible !important;
                padding: 10px !important;
            }
                
            /* Mostrar todas as linhas */
            table.dataTable tbody tr {
                display: table-row !important;
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

            h2, h4 {
                font-size: 16pt !important;
                color: #000 !important;
            }

            .row {
                display: flex !important;
                flex-wrap: wrap !important;
                page-break-inside: avoid;
            }

            .col-md-3, .col-lg-3 {
                width: 24% !important;
                flex: 0 0 24% !important;
                padding: 0 3px !important;
            }

            .col-md-6, .col-lg-6 {
                width: 49% !important;
                flex: 0 0 49% !important;
                padding: 0 5px !important;
            }

            .stat-card {
                box-shadow: none !important;
                border: 1.5px solid #333 !important;
                padding: 10px !important;
                page-break-inside: avoid;
                margin-bottom: 8px !important;
            }

            .stat-card-blue { background: #e3f2fd !important; border-color: #1e40af !important; }
            .stat-card-green { background: #e8f5e9 !important; border-color: #059669 !important; }
            .stat-card-yellow { background: #fff3e0 !important; border-color: #d97706 !important; }

            .stat-value {
                font-size: 16pt !important;
                color: #000 !important;
            }

            .stat-title {
                font-size: 9pt !important;
                color: #333 !important;
            }

            .chart-card {
                box-shadow: none !important;
                border: 1px solid #999 !important;
                page-break-inside: avoid;
                margin-bottom: 15px !important;
            }

            .chart-header {
                background: #f5f5f5 !important;
                color: #000 !important;
                border-bottom: 2px solid #333 !important;
                padding: 8px 12px !important;
            }

            /* Tabelas - ajuste para não cortar colunas */
            .grid-table {
                font-size: 7pt !important;
                width: 100% !important;
                table-layout: fixed !important;
            }

            .grid-table th {
                background-color: #f0f0f0 !important;
                border: 1px solid #666 !important;
                padding: 4px 2px !important;
                color: #000 !important;
                word-wrap: break-word !important;
                overflow-wrap: break-word !important;
                white-space: normal !important;
            }

            .grid-table td {
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

            canvas {
                max-width: 100% !important;
                height: auto !important;
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
                        <i class="bi bi-binoculars"></i> Relatório de Veículos Monitorados por Modelo
                    </h2>
                    <p class="text-center text-muted">Análise estatística de veículos monitorados agrupados por modelo</p>
                </div>
            </div>

            <!-- Formulário de Parâmetros -->
            <div class="form-container">
                <form id="frm_filtro_relatorio">
                    <div class="row justify-content-center">
                        <div class="col-md-4 mb-3">
                            <label for="txt_data_inicio" class="form-label fw-bold">
                                <i class="bi bi-calendar"></i> Data Início
                            </label>
                            <input id="txt_data_inicio" type="date" name="dataInicio" class="form-control" required>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="txt_data_fim" class="form-label fw-bold">
                                <i class="bi bi-calendar"></i> Data Fim
                            </label>
                            <input id="txt_data_fim" type="date" name="dataFim" class="form-control" required>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-12 text-center">
                            <button type="button" class="btn btn-primary-custom" onclick="gerarRelatorio();" id="btn_gerar">
                                <i class="bi bi-bar-chart-line"></i> Gerar Relatório
                            </button>
                        </div>
                    </div>
                </form>
                <div id="loading_msg" class="mt-3 text-center" style="display: none;">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                    <p class="mt-2">Carregando dados...</p>
                </div>
                <div class="row mt-3">
                    <div class="col-12">
                        <div id="div_mensagem"></div>
                    </div>
                </div>
            </div>

        <!-- Dashboard -->
        <div id="dashboard-section" style="display: none;">
            <h2 class="print-title">Relatório de Veículos Monitorados por Modelo</h2>
            <div id="dashboard-container">
                <!-- Conteúdo será carregado dinamicamente -->
            </div>
            <div class="row mt-4">
                <div class="col-12 text-center">
                    <button type="button" class="btn btn-outline-secondary me-2" onclick="imprimirDashboard()">
                        <i class="bi bi-printer me-2"></i>Imprimir
                    </button>
                    <button type="button" class="btn btn-outline-success me-2" onclick="exportarParaExcel()">
                        <i class="bi bi-file-earmark-excel me-2"></i>Excel
                    </button>
                </div>
            </div>
            </div>
        </div>
    </div>

    <script type="text/javascript">
        // Variável global para armazenar os dados
        let dadosRelatorio = null;

        // Função para validar datas
        function validarDatas() {
            const dataInicio = document.getElementById('txt_data_inicio').value;
            const dataFim = document.getElementById('txt_data_fim').value;

            if (!dataInicio || !dataFim) {
                mostrarMensagem('Por favor, preencha ambas as datas.', 'error');
                return false;
            }

            const dtInicio = new Date(dataInicio);
            const dtFim = new Date(dataFim);

            if (dtInicio > dtFim) {
                mostrarMensagem('A data de início deve ser anterior à data final.', 'error');
                return false;
            }

            // Verificar se o período não é muito longo (máximo 1 ano)
            const diffTime = Math.abs(dtFim - dtInicio);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

            if (diffDays > 365) {
                mostrarMensagem('O período máximo permitido é de 1 ano.', 'error');
                return false;
            }

            return true;
        }

        // Função para mostrar mensagens
        function mostrarMensagem(mensagem, tipo) {
            const divMensagem = document.getElementById('div_mensagem');
            const alertClass = tipo === 'error' ? 'alert-danger' : 'alert-success';
            divMensagem.innerHTML = '<div class="alert ' + alertClass + ' alert-dismissible fade show" role="alert">' +
                mensagem +
                '<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>';

            // Limpar mensagem após 5 segundos
            setTimeout(() => {
                divMensagem.innerHTML = '';
            }, 5000);
        }

        // Função para gerar relatório
        function gerarRelatorio() {
            if (!validarDatas()) {
                return;
            }

            // Mostrar loading
            document.getElementById('btn_gerar').disabled = true;
            document.getElementById('loading_msg').style.display = 'block';

            const dataInicio = document.getElementById('txt_data_inicio').value;
            const dataFim = document.getElementById('txt_data_fim').value;

            // Fazer requisição AJAX para o servlet
            const xhr = new XMLHttpRequest();
            xhr.open('POST', '/muralha-digital/relatorio/RelatorioVeiculosMonitoradosModelo', true);
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    // Esconder loading
                    document.getElementById('btn_gerar').disabled = false;
                    document.getElementById('loading_msg').style.display = 'none';

                    if (xhr.status === 200) {
                        try {
                            dadosRelatorio = JSON.parse(xhr.responseText);
                            
                            if (dadosRelatorio.erro) {
                                mostrarMensagem('Erro: ' + dadosRelatorio.erro, 'error');
                                return;
                            }
                            
                            criarDashboard(dadosRelatorio);

                            // Mostrar dashboard
                            document.getElementById('dashboard-section').style.display = 'block';

                            // Scroll suave para o dashboard
                            document.getElementById('dashboard-section').scrollIntoView({
                                behavior: 'smooth'
                            });

                            mostrarMensagem('Relatório gerado com sucesso!', 'success');
                        } catch (e) {
                            console.error('Erro ao processar resposta:', e);
                            mostrarMensagem('Erro ao processar os dados do relatório.', 'error');
                        }
                    } else {
                        mostrarMensagem('Erro ao gerar relatório. Código: ' + xhr.status, 'error');
                    }
                }
            };

            xhr.onerror = function () {
                // Esconder loading
                document.getElementById('btn_gerar').disabled = false;
                document.getElementById('loading_msg').style.display = 'none';

                mostrarMensagem('Erro de conexão ao gerar relatório.', 'error');
            };

            const params = 'dataInicio=' + encodeURIComponent(dataInicio) + '&dataFim=' + encodeURIComponent(dataFim);
            xhr.send(params);
        }

        // Função para criar o dashboard
        function criarDashboard(dados) {
            const container = document.getElementById('dashboard-container');

            // Verificar se os dados existem
            if (!dados || !dados.estatisticasGerais) {
                container.innerHTML = '<div class="alert alert-danger text-center">Erro: Dados não encontrados ou inválidos</div>';
                return;
            }

            const stats = dados.estatisticasGerais;

            // Criar HTML usando Bootstrap
            let html = '';

            // Cards de Estatísticas
            html += '<div class="row mb-4">';
            html += '<div class="col-md-3">';
            html += '<div class="stat-card stat-card-blue">';
            html += '<i class="bi bi-car-front stat-icon"></i>';
            html += '<div class="stat-value">' + (stats.totalVeiculosMonitorados || 0) + '</div>';
            html += '<div class="stat-title">Veículos Monitorados</div>';
            html += '</div></div>';

            html += '<div class="col-md-3">';
            html += '<div class="stat-card stat-card-green">';
            html += '<i class="bi bi-flag stat-icon"></i>';
            html += '<div class="stat-value">' + (stats.totalFatosRegistrados || 0) + '</div>';
            html += '<div class="stat-title">Fatos Registrados</div>';
            html += '</div></div>';

            html += '<div class="col-md-3">';
            html += '<div class="stat-card stat-card-yellow">';
            html += '<i class="bi bi-list-check stat-icon"></i>';
            html += '<div class="stat-value">' + (stats.tiposFatosDistintos || 0) + '</div>';
            html += '<div class="stat-title">Tipos de Fatos</div>';
            html += '</div></div>';

            html += '<div class="col-md-3">';
            html += '<div class="stat-card stat-card-cyan">';
            html += '<i class="bi bi-speedometer2 stat-icon"></i>';
            html += '<div class="stat-value">' + (stats.modelosVeiculosDistintos || 0) + '</div>';
            html += '<div class="stat-title">Modelos Distintos</div>';
            html += '</div></div>';
            html += '</div>';

            // Grid de Veículos Monitorados
            html += '<div class="row mb-4">';
            html += '<div class="col-12">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Veículos Monitorados - Detalhamento</div>';
            html += '<div class="p-3">';
            html += '<table id="tabelaVeiculosMonitorados" class="table table-hover grid-table">';
            html += '<thead><tr><th>Placa</th><th>Marca/Modelo</th><th>Tipo Fato</th><th>Data</th><th>Status</th><th>Dias</th></tr></thead>';
            html += '<tbody id="tabelaVeiculos"></tbody>';
            html += '</table></div></div></div>';
            html += '</div>';

            // Gráficos
            html += '<div class="row mb-4">';
            html += '<div class="col-md-6">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Distribuição por Tipo de Fato</div>';
            html += '<div class="p-3"><canvas id="chartTiposFatos" style="height: 300px;"></canvas></div>';
            html += '</div></div>';

            html += '<div class="col-md-6">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Top 10 Modelos Monitorados</div>';
            html += '<div class="p-3"><canvas id="chartModelos" style="height: 300px;"></canvas></div>';
            html += '</div></div>';
            html += '</div>';

            // Tabela de Top Placas
            html += '<div class="row mb-4">';
            html += '<div class="col-12">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Top 10 Placas com Mais Ocorrências</div>';
            html += '<div class="p-3">';
            html += '<table id="tabelaTopPlacasOcorrencias" class="table table-hover">';
            html += '<thead><tr><th>Placa</th><th>Marca/Modelo</th><th>Ocorrências</th><th>Tipos Envolvidos</th></tr></thead>';
            html += '<tbody id="tabelaTopPlacas"></tbody>';
            html += '</table></div></div></div>';
            html += '</div>';

            container.innerHTML = html;

            // Preencher tabelas
            preencherTabelas(dados);

            // Criar gráficos
            setTimeout(() => {
                criarGraficos(dados);
            }, 100);
        }

        // Função para preencher tabelas
        function preencherTabelas(dados) {
            // Destruir instâncias DataTables existentes
            if ($.fn.DataTable.isDataTable('#tabelaVeiculosMonitorados')) {
                $('#tabelaVeiculosMonitorados').DataTable().destroy();
            }
            if ($.fn.DataTable.isDataTable('#tabelaTopPlacasOcorrencias')) {
                $('#tabelaTopPlacasOcorrencias').DataTable().destroy();
            }
            
            // Tabela de veículos monitorados (todos os registros)
            const tabelaVeiculos = document.getElementById('tabelaVeiculos');
            let htmlVeiculos = '';
            if (dados.veiculosMonitorados && dados.veiculosMonitorados.length > 0) {
                dados.veiculosMonitorados.forEach(veiculo => {
                    const marcaModelo = (veiculo.marca || '') + ' ' + (veiculo.modelo || '');
                    const statusBadge = veiculo.statusFato === 'ABERTO' ? 
                        '<span class="badge bg-success">ABERTO</span>' : 
                        '<span class="badge bg-secondary">ENCERRADO</span>';
                    
                    htmlVeiculos += '<tr>';
                    htmlVeiculos += '<td><strong>' + (veiculo.placa || '') + '</strong></td>';
                    htmlVeiculos += '<td>' + marcaModelo.trim() + '</td>';
                    htmlVeiculos += '<td>' + (veiculo.tipoFato || '') + '</td>';
                    htmlVeiculos += '<td>' + (veiculo.dataCriacao || '').substring(0, 16) + '</td>';
                    htmlVeiculos += '<td>' + statusBadge + '</td>';
                    htmlVeiculos += '<td>' + (veiculo.diasAndamento || 0) + '</td>';
                    htmlVeiculos += '</tr>';
                });
            } else {
                htmlVeiculos = '<tr><td colspan="6" class="text-center">Nenhum veículo encontrado</td></tr>';
            }
            tabelaVeiculos.innerHTML = htmlVeiculos;
            
            // Inicializar DataTable para veículos monitorados
            if (dados.veiculosMonitorados && dados.veiculosMonitorados.length > 0) {
                $('#tabelaVeiculosMonitorados').DataTable({
                    language: {
                        lengthMenu: "Exibir _MENU_ registros por página",
                        zeroRecords: "Nenhum registro encontrado",
                        info: "Mostrando página _PAGE_ de _PAGES_",
                        infoEmpty: "Nenhum registro disponível",
                        infoFiltered: "(filtrado de _MAX_ registros totais)",
                        search: "Buscar:",
                        paginate: {
                            first: "Primeiro",
                            last: "Último",
                            next: "Próximo",
                            previous: "Anterior"
                        }
                    },
                    pageLength: 10,
                    lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, "Todos"]],
                    order: [[3, 'desc']] // Ordenar por data decrescente
                });
            }

            // Tabela de top placas
            const tabelaTopPlacas = document.getElementById('tabelaTopPlacas');
            let htmlTopPlacas = '';
            if (dados.topPlacas && dados.topPlacas.length > 0) {
                dados.topPlacas.forEach(placa => {
                    const marcaModelo = (placa.marca || '') + ' ' + (placa.modelo || '');
                    htmlTopPlacas += '<tr>';
                    htmlTopPlacas += '<td><strong>' + (placa.placa || '') + '</strong></td>';
                    htmlTopPlacas += '<td>' + marcaModelo.trim() + '</td>';
                    htmlTopPlacas += '<td><strong>' + (placa.ocorrencias || 0) + '</strong></td>';
                    htmlTopPlacas += '<td style="font-size:0.8rem;">' + (placa.tiposFatosEnvolvidos || '') + '</td>';
                    htmlTopPlacas += '</tr>';
                });
            } else {
                htmlTopPlacas = '<tr><td colspan="4" class="text-center">Nenhum dado encontrado</td></tr>';
            }
            tabelaTopPlacas.innerHTML = htmlTopPlacas;
            
            // Inicializar DataTable para top placas
            if (dados.topPlacas && dados.topPlacas.length > 0) {
                $('#tabelaTopPlacasOcorrencias').DataTable({
                    language: {
                        lengthMenu: "Exibir _MENU_ registros por página",
                        zeroRecords: "Nenhum registro encontrado",
                        info: "Mostrando página _PAGE_ de _PAGES_",
                        infoEmpty: "Nenhum registro disponível",
                        infoFiltered: "(filtrado de _MAX_ registros totais)",
                        search: "Buscar:",
                        paginate: {
                            first: "Primeiro",
                            last: "Último",
                            next: "Próximo",
                            previous: "Anterior"
                        }
                    },
                    pageLength: 10,
                    lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "Todos"]],
                    order: [[2, 'desc']] // Ordenar por ocorrências decrescente
                });
            }
        }

        // Função para criar gráficos
        function criarGraficos(dados) {
            const cores = ['#1e40af', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#84cc16', '#f97316'];

            // Gráfico de Tipos de Fatos
            const ctxTiposFatos = document.getElementById('chartTiposFatos').getContext('2d');
            if (dados.distribuicaoTiposFatos && dados.distribuicaoTiposFatos.length > 0) {
                new Chart(ctxTiposFatos, {
                    type: 'bar',
                    data: {
                        labels: dados.distribuicaoTiposFatos.map(t => t.tipoFato || 'N/A'),
                        datasets: [{
                            label: 'Quantidade',
                            data: dados.distribuicaoTiposFatos.map(t => t.quantidade || 0),
                            backgroundColor: cores.slice(0, dados.distribuicaoTiposFatos.length),
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                display: false
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                });
            }

            // Gráfico de Modelos (Top 10)
            const ctxModelos = document.getElementById('chartModelos').getContext('2d');
            if (dados.distribuicaoModelos && dados.distribuicaoModelos.length > 0) {
                const top10Modelos = dados.distribuicaoModelos.slice(0, 10);
                new Chart(ctxModelos, {
                    type: 'bar',
                    data: {
                        labels: top10Modelos.map(m => ((m.marca || '') + ' ' + (m.modelo || '')).trim()),
                        datasets: [{
                            label: 'Total de Fatos',
                            data: top10Modelos.map(m => m.totalFatos || 0),
                            backgroundColor: cores.slice(0, top10Modelos.length),
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        indexAxis: 'y',
                        plugins: {
                            legend: {
                                display: false
                            }
                        },
                        scales: {
                            x: {
                                beginAtZero: true
                            }
                        }
                    }
                });
            }
        }

        // Função para imprimir dashboard
        function imprimirDashboard() {
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
                const resumoData = [
                    ['Relatório de Veículos Monitorados por Modelo'],
                    [''],
                    ['Período', document.getElementById('txt_data_inicio').value + ' a ' + document.getElementById('txt_data_fim').value],
                    [''],
                    ['Total de Veículos', dadosRelatorio.totalVeiculos || 0],
                    ['Total de Fatos', dadosRelatorio.totalFatos || 0],
                    ['Modelos Únicos', dadosRelatorio.totalModelos || 0]
                ];
                XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(resumoData), 'Resumo');
                
                // Veículos Monitorados
                if (dadosRelatorio.veiculosMonitorados && dadosRelatorio.veiculosMonitorados.length > 0) {
                    const veiculosData = [['Placa', 'Marca', 'Modelo', 'Cor', 'Total Ocorrências', 'Última Ocorrência']];
                    dadosRelatorio.veiculosMonitorados.forEach(item => {
                        veiculosData.push([item.placa, item.marca, item.modelo, item.cor, item.totalOcorrencias, item.ultimaOcorrencia]);
                    });
                    XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(veiculosData), 'Veículos');
                }
                
                // Distribuição por Modelo
                if (dadosRelatorio.distribuicaoModelos && dadosRelatorio.distribuicaoModelos.length > 0) {
                    const modelosData = [['Marca', 'Modelo', 'Total de Fatos', 'Percentual']];
                    dadosRelatorio.distribuicaoModelos.forEach(item => {
                        modelosData.push([item.marca, item.modelo, item.totalFatos, item.percentual + '%']);
                    });
                    XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(modelosData), 'Por Modelo');
                }
                
                // Top Placas
                if (dadosRelatorio.topPlacasOcorrencias && dadosRelatorio.topPlacasOcorrencias.length > 0) {
                    const topPlacasData = [['Placa', 'Marca', 'Modelo', 'Total Ocorrências']];
                    dadosRelatorio.topPlacasOcorrencias.forEach(item => {
                        topPlacasData.push([item.placa, item.marca, item.modelo, item.totalOcorrencias]);
                    });
                    XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(topPlacasData), 'Top Placas');
                }
                
                const nomeArquivo = 'Relatorio_Veiculos_Monitorados_Modelo_' + new Date().toISOString().split('T')[0] + '.xlsx';
                XLSX.writeFile(wb, nomeArquivo);
                
            } catch (e) {
                console.error('Erro ao exportar para Excel:', e);
                alert('Erro ao exportar para Excel: ' + e.message);
            }
        }

        // Definir datas padrão (últimos 30 dias)
        window.onload = function () {
            const hoje = new Date();
            const trintaDiasAtras = new Date();
            trintaDiasAtras.setDate(hoje.getDate() - 30);

            document.getElementById('txt_data_inicio').valueAsDate = trintaDiasAtras;
            document.getElementById('txt_data_fim').valueAsDate = hoje;
        };
    </script>
</body>

</html>
