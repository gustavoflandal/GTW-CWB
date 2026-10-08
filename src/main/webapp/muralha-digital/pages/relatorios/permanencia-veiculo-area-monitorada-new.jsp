<%@ page language="java" pageEncoding="utf-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<html lang="pt-BR">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>GTW - Relatório de Permanência de Veículos em Áreas Monitoradas</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- SheetJS para exportação Excel -->
    <script src="https://cdn.sheetjs.com/xlsx-0.20.1/package/dist/xlsx.full.min.js"></script>
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">

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
            min-height: 120px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }
        
        /* Padronização dos cards do dashboard */
        #dashboard-container > .row.mb-4:first-child {
            display: flex;
            flex-wrap: wrap;
        }
        
        #dashboard-container > .row.mb-4:first-child > .col {
            flex: 1 1 0;
            min-width: 150px;
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

        .stat-card-red {
            background: #dc3545;
        }

        .stat-card-purple {
            background: #6f42c1;
        }

        .stat-card-cyan {
            background: #0891b2;
        }

        .stat-card-orange {
            background: #ea580c;
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
            max-width: 900px;
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

        #dashboard-section {
            display: none;
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
            /* Scroll containers */
            .chart-card .p-3,
            .p-3 {
                max-height: none !important;
                overflow: visible !important;
                padding: 10px !important;
            }

            .filter-card, .btn, .no-print, button, form, #loading_msg,
            .form-container, .dataTables_length, .dataTables_filter, .dataTables_info, .dataTables_paginate,
            #selectQtdPassagens, #selectQtdEstatisticas, #paginacaoNavPassagens, #paginacaoNavEstatisticas,
            #infoPaginacaoPassagens, #infoPaginacaoEstatisticas,
            .d-flex.justify-content-between.align-items-center,
            .dashboard-container > .container > .row.mb-4:first-child {
                display: none !important;
            }
            
            * {
                -webkit-print-color-adjust: exact !important;
                print-color-adjust: exact !important;
            }

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
                min-height: 75px !important;
            }

            .stat-card-blue { background: #e3f2fd !important; border-color: #1e40af !important; }
            .stat-card-green { background: #e8f5e9 !important; border-color: #059669 !important; }
            .stat-card-yellow { background: #fff3e0 !important; border-color: #d97706 !important; }
            .stat-card-red { background: #ffebee !important; border-color: #dc3545 !important; }
            .stat-card-purple { background: #f3e5f5 !important; border-color: #6f42c1 !important; }
            .stat-card-cyan { background: #e0f7fa !important; border-color: #0891b2 !important; }
            .stat-card-orange { background: #ffe5d9 !important; border-color: #ea580c !important; }

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
            .grid-table,
            .table {
                font-size: 7pt !important;
                width: 100% !important;
                table-layout: fixed !important;
            }

            .grid-table th,
            .table th {
                background-color: #f0f0f0 !important;
                border: 1px solid #666 !important;
                padding: 4px 2px !important;
                color: #000 !important;
                word-wrap: break-word !important;
                overflow-wrap: break-word !important;
                white-space: normal !important;
            }

            .grid-table td,
            .table td {
                border: 1px solid #999 !important;
                padding: 3px 2px !important;
                color: #000 !important;
                word-wrap: break-word !important;
                overflow-wrap: break-word !important;
                white-space: normal !important;
            }
            
            /* Ajuste de largura das colunas da tabela de passagens */
            .grid-table th:nth-child(1),
            .grid-table td:nth-child(1) { width: 10% !important; } /* Placa */
            .grid-table th:nth-child(2),
            .grid-table td:nth-child(2) { width: 15% !important; } /* Nome */
            .grid-table th:nth-child(3),
            .grid-table td:nth-child(3) { width: 8% !important; }  /* Entrada */
            .grid-table th:nth-child(4),
            .grid-table td:nth-child(4) { width: 12% !important; } /* Data Entrada */
            .grid-table th:nth-child(5),
            .grid-table td:nth-child(5) { width: 8% !important; }  /* Equip. Entrada */
            .grid-table th:nth-child(6),
            .grid-table td:nth-child(6) { width: 8% !important; }  /* Saída */
            .grid-table th:nth-child(7),
            .grid-table td:nth-child(7) { width: 12% !important; } /* Data Saída */
            .grid-table th:nth-child(8),
            .grid-table td:nth-child(8) { width: 8% !important; }  /* Equip. Saída */
            .grid-table th:nth-child(9),
            .grid-table td:nth-child(9) { width: 12% !important; } /* Permanência */
            
            .table-responsive {
                overflow: visible !important;
            }
            
            .badge {
                font-size: 6pt !important;
                padding: 2px 4px !important;
            }

            .status-indicator {
                border: 1px solid #333 !important;
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
                        <i class="bi bi-clock-history"></i> Relatório de Permanência de Veículos em Áreas Monitoradas
                    </h2>
                    <p class="text-center text-muted">Análise de tempo de permanência de veículos em áreas específicas</p>
                </div>
            </div>

            <!-- Formulário de Parâmetros -->
            <div class="form-container">
                <form id="frm_filtro_relatorio">
                    <div class="row">
                        <div class="col-md-3 mb-3">
                            <label for="sel_area_monitorada" class="form-label fw-bold">
                                <i class="bi bi-geo-alt"></i> Área Monitorada
                            </label>
                            <select id="sel_area_monitorada" name="area_monitorada" class="form-select">
                                <option value="">Carregando...</option>
                            </select>
                            <div class="form-text">Filtrar por área (opcional)</div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label for="txt_placa" class="form-label fw-bold">
                                <i class="bi bi-credit-card"></i> Placa
                            </label>
                            <input id="txt_placa" type="text" name="placa" class="form-control" 
                                   placeholder="ABC1D23" maxlength="8" 
                                   style="text-transform: uppercase;">
                            <div class="form-text">Filtrar por placa (opcional)</div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label for="txt_data_inicio" class="form-label fw-bold">
                                <i class="bi bi-calendar"></i> Data Início
                            </label>
                            <input id="txt_data_inicio" type="date" name="dataInicio" class="form-control" required>
                        </div>
                        <div class="col-md-3 mb-3">
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
        <div id="dashboard-section">
            <div class="container dashboard-container">
                <h2 class="print-title">Relatório de Permanência de Veículos em Áreas Monitoradas</h2>
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
        
        // Carregar áreas monitoradas ao iniciar a página
        function carregarAreasMonitoradas() {
            const selectArea = document.getElementById('sel_area_monitorada');
            
            console.log('Iniciando carregamento de áreas monitoradas...');
            console.log('URL: /relatorio/RelatorioPermanenciaVeiculoAreaMonitoradaNew?acao=getAreasMonitoradas');
            
            fetch('/relatorio/RelatorioPermanenciaVeiculoAreaMonitoradaNew?acao=getAreasMonitoradas')
                .then(response => {
                    console.log('Resposta recebida - Status:', response.status);
                    if (!response.ok) {
                        throw new Error('Erro HTTP ' + response.status);
                    }
                    return response.text();
                })
                .then(text => {
                    console.log('Resposta (texto bruto):', text);
                    const data = JSON.parse(text);
                    console.log('JSON parseado:', data);
                    
                    selectArea.innerHTML = '<option value="">Todas as áreas</option>';
                    
                    if (data && data.length > 0) {
                        data.forEach(area => {
                            const option = document.createElement('option');
                            option.value = area.id;
                            option.textContent = area.nome;
                            selectArea.appendChild(option);
                            console.log('Adicionada área - ID:', area.id, 'Nome:', area.nome);
                        });
                        console.log('✓ Total de áreas carregadas:', data.length);
                    } else {
                        console.warn('⚠ Nenhuma área monitorada encontrada no banco de dados');
                        selectArea.innerHTML += '<option value="" disabled>Nenhuma área cadastrada</option>';
                    }
                })
                .catch(error => {
                    console.error('✗ Erro ao carregar áreas monitoradas:', error);
                    console.error('Stack trace:', error.stack);
                    selectArea.innerHTML = '<option value="">Erro ao carregar áreas</option>';
                    mostrarMensagem('Erro ao carregar lista de áreas monitoradas. Verifique o console do navegador.', 'error');
                });
        }
        
        // Variáveis de controle de paginação - Tabela Passagens
        let paginaAtualPassagens = 1;
        let registrosPorPaginaPassagens = 25;
        let dadosPassagensFiltrados = [];
        
        // Variáveis de controle de paginação - Tabela Estatísticas
        let paginaAtualEstatisticas = 1;
        let registrosPorPaginaEstatisticas = 25;
        let dadosEstatisticasFiltrados = [];

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
            const areaMonitorada = document.getElementById('sel_area_monitorada').value; // ID da área
            const placa = document.getElementById('txt_placa').value.trim();

            // Fazer requisição AJAX para o servlet
            const xhr = new XMLHttpRequest();
            xhr.open('POST', '/relatorio/RelatorioPermanenciaVeiculoAreaMonitoradaNew', true);
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

            let params = 'data_inicio=' + encodeURIComponent(dataInicio) + '&data_fim=' + encodeURIComponent(dataFim);
            if (areaMonitorada) params += '&area_monitorada=' + encodeURIComponent(areaMonitorada);
            if (placa) params += '&placa=' + encodeURIComponent(placa);
            
            xhr.send(params);
        }


        // Função para criar o dashboard
        function criarDashboard(dados) {
            const container = document.getElementById('dashboard-container');

            // Verificar se os dados existem
            if (!dados || !dados.passagens || !dados.estatisticas) {
                container.innerHTML = '<div class="alert alert-danger text-center">Erro: Dados não encontrados ou inválidos</div>';
                return;
            }

            // Calcular estatísticas gerais
            const totalPassagens = dados.passagens.length;
            const passagensSemSaida = dados.passagens.filter(p => !p.ocorrencia_saida || p.ocorrencia_saida === 'Sem Saída').length;
            const passagensFinalizadas = totalPassagens - passagensSemSaida;
            const totalAreas = dados.estatisticas.length;
            const totalVeiculosDistintos = dados.estatisticas.reduce((sum, e) => sum + (e.total_veiculos_distintos || 0), 0);
            const totalPermanencias = dados.estatisticas.reduce((sum, e) => sum + (e.total_permanencias || 0), 0);

            // Criar HTML usando Bootstrap
            let html = '';

            // Cards de Estatísticas
            html += '<div class="row mb-4">';
            html += '<div class="col">';
            html += '<div class="stat-card stat-card-green">';
            html += '<i class="bi bi-arrows-move stat-icon"></i>';
            html += '<div class="stat-value">' + totalPermanencias + '</div>';
            html += '<div class="stat-title">Total Permanências</div>';
            html += '</div></div>';

            html += '<div class="col">';
            html += '<div class="stat-card stat-card-orange">';
            html += '<i class="bi bi-check-circle stat-icon"></i>';
            html += '<div class="stat-value">' + passagensFinalizadas + '</div>';
            html += '<div class="stat-title">Finalizadas</div>';
            html += '</div></div>';

            html += '<div class="col">';
            html += '<div class="stat-card stat-card-red">';
            html += '<i class="bi bi-exclamation-circle stat-icon"></i>';
            html += '<div class="stat-value">' + passagensSemSaida + '</div>';
            html += '<div class="stat-title">Sem Saída</div>';
            html += '</div></div>';

            html += '<div class="col">';
            html += '<div class="stat-card stat-card-purple">';
            html += '<i class="bi bi-geo-alt stat-icon"></i>';
            html += '<div class="stat-value">' + totalAreas + '</div>';
            html += '<div class="stat-title">Áreas Monitoradas</div>';
            html += '</div></div>';

            html += '<div class="col">';
            html += '<div class="stat-card stat-card-cyan">';
            html += '<i class="bi bi-list-ol stat-icon"></i>';
            html += '<div class="stat-value">' + totalPassagens + '</div>';
            html += '<div class="stat-title">Total Registros</div>';
            html += '</div></div>';
            html += '</div>';

            // Grid de Passagens Detalhadas
            html += '<div class="row mb-4">';
            html += '<div class="col-12">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Passagens - Detalhamento</div>';
            html += '<div class="p-3">';
            
            // Controle de quantidade de registros - Passagens
            html += '<div class="d-flex justify-content-between align-items-center mb-2">';
            html += '<div class="d-flex align-items-center">';
            html += '<label class="me-2 mb-0">Mostrar</label>';
            html += '<select id="selectQtdPassagens" class="form-select form-select-sm" style="width: auto;" onchange="alterarQtdPassagens()">';
            html += '<option value="10">10</option>';
            html += '<option value="25" selected>25</option>';
            html += '<option value="50">50</option>';
            html += '<option value="100">100</option>';
            html += '<option value="-1">Todos</option>';
            html += '</select>';
            html += '<label class="ms-2 mb-0">registros por página</label>';
            html += '</div>';
            html += '</div>';
            
            html += '<div class="table-responsive">';
            html += '<table class="table table-hover grid-table">';
            html += '<thead><tr><th>Placa</th><th>Nome</th><th>Entrada</th><th>Data Entrada</th><th>Equip. Entrada</th><th>Saída</th><th>Data Saída</th><th>Equip. Saída</th><th>Permanência</th></tr></thead>';
            html += '<tbody id="tabelaPassagens"></tbody>';
            html += '</table>';
            html += '</div>';
            
            // Rodapé com paginação - Passagens
            html += '<div class="d-flex justify-content-between align-items-center mt-2 p-2" style="background: #f8f9fa; border-radius: 8px;">';
            html += '<div id="infoPaginacaoPassagens" class="text-muted small"></div>';
            html += '<nav><ul class="pagination pagination-sm mb-0" id="paginacaoNavPassagens"></ul></nav>';
            html += '</div>';
            
            html += '</div></div></div>';
            html += '</div>';

            // Gráficos
            html += '<div class="row mb-4">';
            html += '<div class="col-md-12">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Tempo Médio de Permanência por Área</div>';
            html += '<div class="p-3"><canvas id="chartTempoMedio" style="height: 300px;"></canvas></div>';
            html += '</div></div>';
            html += '</div>';

            // Tabela de Estatísticas por Área
            html += '<div class="row mb-4">';
            html += '<div class="col-12">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Estatísticas por Área Monitorada</div>';
            html += '<div class="p-3">';
            
            // Controle de quantidade de registros - Estatísticas
            html += '<div class="d-flex justify-content-between align-items-center mb-2">';
            html += '<div class="d-flex align-items-center">';
            html += '<label class="me-2 mb-0">Mostrar</label>';
            html += '<select id="selectQtdEstatisticas" class="form-select form-select-sm" style="width: auto;" onchange="alterarQtdEstatisticas()">';
            html += '<option value="10">10</option>';
            html += '<option value="25" selected>25</option>';
            html += '<option value="50">50</option>';
            html += '<option value="100">100</option>';
            html += '<option value="-1">Todos</option>';
            html += '</select>';
            html += '<label class="ms-2 mb-0">registros por página</label>';
            html += '</div>';
            html += '</div>';
            
            html += '<div class="table-responsive">';
            html += '<table class="table table-hover">';
            html += '<thead><tr><th>Local</th><th>Veículos</th><th>Passagens</th><th>Sem Saída</th><th>Finalizadas</th><th>Tempo Total Acumulado</th><th>Tempo Médio</th></tr></thead>';
            html += '<tbody id="tabelaEstatisticas"></tbody>';
            html += '</table>';
            html += '</div>';
            
            // Rodapé com paginação - Estatísticas
            html += '<div class="d-flex justify-content-between align-items-center mt-2 p-2" style="background: #f8f9fa; border-radius: 8px;">';
            html += '<div id="infoPaginacaoEstatisticas" class="text-muted small"></div>';
            html += '<nav><ul class="pagination pagination-sm mb-0" id="paginacaoNavEstatisticas"></ul></nav>';
            html += '</div>';
            
            html += '</div></div></div>';
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
            // Armazenar dados para paginação
            dadosPassagensFiltrados = dados.passagens || [];
            dadosEstatisticasFiltrados = dados.estatisticas || [];
            
            // Resetar páginas
            paginaAtualPassagens = 1;
            paginaAtualEstatisticas = 1;
            
            // Aplicar paginação em ambas as tabelas
            aplicarPaginacaoPassagens();
            aplicarPaginacaoEstatisticas();
        }
        
        // === PAGINAÇÃO TABELA PASSAGENS ===
        
        function alterarQtdPassagens() {
            const select = document.getElementById('selectQtdPassagens');
            registrosPorPaginaPassagens = parseInt(select.value);
            paginaAtualPassagens = 1;
            aplicarPaginacaoPassagens();
        }
        
        function irParaPaginaPassagens(pagina) {
            paginaAtualPassagens = pagina;
            aplicarPaginacaoPassagens();
        }
        
        function aplicarPaginacaoPassagens() {
            const tbody = document.getElementById('tabelaPassagens');
            const totalRegistros = dadosPassagensFiltrados.length;
            const totalPaginas = registrosPorPaginaPassagens === -1 ? 1 : Math.ceil(totalRegistros / registrosPorPaginaPassagens);
            
            // Ajustar página atual se necessário
            if (paginaAtualPassagens > totalPaginas) {
                paginaAtualPassagens = Math.max(1, totalPaginas);
            }
            
            // Calcular índices
            let inicio, fim;
            if (registrosPorPaginaPassagens === -1) {
                inicio = 0;
                fim = totalRegistros;
            } else {
                inicio = (paginaAtualPassagens - 1) * registrosPorPaginaPassagens;
                fim = Math.min(inicio + registrosPorPaginaPassagens, totalRegistros);
            }
            
            // Renderizar linhas da página atual
            let htmlPassagens = '';
            if (totalRegistros > 0) {
                const passagensPagina = dadosPassagensFiltrados.slice(inicio, fim);
                passagensPagina.forEach(p => {
                    const statusSaida = (!p.ocorrencia_saida || p.ocorrencia_saida === 'Sem Saída') ?
                        '<span class="badge bg-danger">Sem Saída</span>' :
                        '<span class="badge bg-success">' + p.ocorrencia_saida + '</span>';

                    htmlPassagens += '<tr>';
                    htmlPassagens += '<td><strong>' + (p.placa || '') + '</strong></td>';
                    htmlPassagens += '<td>' + (p.nome || '-') + '</td>';
                    htmlPassagens += '<td><span class="badge bg-info">' + (p.ocorrencia || '') + '</span></td>';
                    htmlPassagens += '<td>' + formatarDataHora(p.data_entrada) + '</td>';
                    htmlPassagens += '<td>' + (p.id_equipamento_entrada || '-') + '</td>';
                    htmlPassagens += '<td>' + statusSaida + '</td>';
                    htmlPassagens += '<td>' + (p.data_saida ? formatarDataHora(p.data_saida) : '-') + '</td>';
                    htmlPassagens += '<td>' + (p.id_equipamento_saida !== null && p.id_equipamento_saida !== undefined ? p.id_equipamento_saida : '-') + '</td>';
                    htmlPassagens += '<td><strong>' + (p.tempo_permanencia || '0') + '</strong></td>';
                    htmlPassagens += '</tr>';
                });
            } else {
                htmlPassagens = '<tr><td colspan="9" class="text-center">Nenhuma passagem encontrada</td></tr>';
            }
            tbody.innerHTML = htmlPassagens;
            
            // Atualizar informações de paginação
            const infoPaginacao = document.getElementById('infoPaginacaoPassagens');
            if (infoPaginacao) {
                if (totalRegistros === 0) {
                    infoPaginacao.textContent = 'Nenhum registro encontrado';
                } else {
                    infoPaginacao.textContent = 'Mostrando ' + (inicio + 1) + ' a ' + fim + ' de ' + totalRegistros + ' registros';
                }
            }
            
            // Atualizar navegação de páginas
            atualizarNavegacaoPaginasPassagens(totalPaginas, totalRegistros);
        }
        
        function atualizarNavegacaoPaginasPassagens(totalPaginas, totalRegistros) {
            const nav = document.getElementById('paginacaoNavPassagens');
            if (!nav) return;
            
            let html = '';
            
            if (registrosPorPaginaPassagens === -1 || totalPaginas <= 1) {
                nav.innerHTML = '';
                return;
            }
            
            // Botão Anterior
            html += '<li class="page-item ' + (paginaAtualPassagens === 1 ? 'disabled' : '') + '">';
            html += '<a class="page-link" href="javascript:void(0)" onclick="irParaPaginaPassagens(' + (paginaAtualPassagens - 1) + ')">Anterior</a>';
            html += '</li>';
            
            // Páginas
            let paginasExibir = [];
            if (totalPaginas <= 7) {
                for (let i = 1; i <= totalPaginas; i++) paginasExibir.push(i);
            } else {
                paginasExibir.push(1);
                if (paginaAtualPassagens > 3) paginasExibir.push('...');
                for (let i = Math.max(2, paginaAtualPassagens - 1); i <= Math.min(totalPaginas - 1, paginaAtualPassagens + 1); i++) {
                    if (!paginasExibir.includes(i)) paginasExibir.push(i);
                }
                if (paginaAtualPassagens < totalPaginas - 2) paginasExibir.push('...');
                paginasExibir.push(totalPaginas);
            }
            
            paginasExibir.forEach(p => {
                if (p === '...') {
                    html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
                } else {
                    html += '<li class="page-item ' + (p === paginaAtualPassagens ? 'active' : '') + '">';
                    html += '<a class="page-link" href="javascript:void(0)" onclick="irParaPaginaPassagens(' + p + ')">' + p + '</a>';
                    html += '</li>';
                }
            });
            
            // Botão Próximo
            html += '<li class="page-item ' + (paginaAtualPassagens === totalPaginas ? 'disabled' : '') + '">';
            html += '<a class="page-link" href="javascript:void(0)" onclick="irParaPaginaPassagens(' + (paginaAtualPassagens + 1) + ')">Próximo</a>';
            html += '</li>';
            
            nav.innerHTML = html;
        }
        
        // === PAGINAÇÃO TABELA ESTATÍSTICAS ===
        
        function alterarQtdEstatisticas() {
            const select = document.getElementById('selectQtdEstatisticas');
            registrosPorPaginaEstatisticas = parseInt(select.value);
            paginaAtualEstatisticas = 1;
            aplicarPaginacaoEstatisticas();
        }
        
        function irParaPaginaEstatisticas(pagina) {
            paginaAtualEstatisticas = pagina;
            aplicarPaginacaoEstatisticas();
        }
        
        function aplicarPaginacaoEstatisticas() {
            const tbody = document.getElementById('tabelaEstatisticas');
            const totalRegistros = dadosEstatisticasFiltrados.length;
            const totalPaginas = registrosPorPaginaEstatisticas === -1 ? 1 : Math.ceil(totalRegistros / registrosPorPaginaEstatisticas);
            
            // Ajustar página atual se necessário
            if (paginaAtualEstatisticas > totalPaginas) {
                paginaAtualEstatisticas = Math.max(1, totalPaginas);
            }
            
            // Calcular índices
            let inicio, fim;
            if (registrosPorPaginaEstatisticas === -1) {
                inicio = 0;
                fim = totalRegistros;
            } else {
                inicio = (paginaAtualEstatisticas - 1) * registrosPorPaginaEstatisticas;
                fim = Math.min(inicio + registrosPorPaginaEstatisticas, totalRegistros);
            }
            
            // Renderizar linhas da página atual
            let htmlEstatisticas = '';
            if (totalRegistros > 0) {
                const estatisticasPagina = dadosEstatisticasFiltrados.slice(inicio, fim);
                estatisticasPagina.forEach(e => {
                    htmlEstatisticas += '<tr>';
                    htmlEstatisticas += '<td><strong>' + (e.nome_area_monitorada || '') + '</strong></td>';
                    htmlEstatisticas += '<td>' + (e.total_veiculos_distintos || 0) + '</td>';
                    htmlEstatisticas += '<td>' + (e.total_permanencias || 0) + '</td>';
                    htmlEstatisticas += '<td><span class="badge bg-danger">' + (e.permanencias_sem_saida || 0) + '</span></td>';
                    htmlEstatisticas += '<td><span class="badge bg-success">' + (e.permanencias_finalizadas || 0) + '</span></td>';
                    htmlEstatisticas += '<td>' + (e.tempo_total_acumulado || '-') + '</td>';
                    htmlEstatisticas += '<td>' + (e.tempo_medio_permanencia || '-') + '</td>';
                    htmlEstatisticas += '</tr>';
                });
            } else {
                htmlEstatisticas = '<tr><td colspan="7" class="text-center">Nenhum dado encontrado</td></tr>';
            }
            tbody.innerHTML = htmlEstatisticas;
            
            // Atualizar informações de paginação
            const infoPaginacao = document.getElementById('infoPaginacaoEstatisticas');
            if (infoPaginacao) {
                if (totalRegistros === 0) {
                    infoPaginacao.textContent = 'Nenhum registro encontrado';
                } else {
                    infoPaginacao.textContent = 'Mostrando ' + (inicio + 1) + ' a ' + fim + ' de ' + totalRegistros + ' registros';
                }
            }
            
            // Atualizar navegação de páginas
            atualizarNavegacaoPaginasEstatisticas(totalPaginas, totalRegistros);
        }
        
        function atualizarNavegacaoPaginasEstatisticas(totalPaginas, totalRegistros) {
            const nav = document.getElementById('paginacaoNavEstatisticas');
            if (!nav) return;
            
            let html = '';
            
            if (registrosPorPaginaEstatisticas === -1 || totalPaginas <= 1) {
                nav.innerHTML = '';
                return;
            }
            
            // Botão Anterior
            html += '<li class="page-item ' + (paginaAtualEstatisticas === 1 ? 'disabled' : '') + '">';
            html += '<a class="page-link" href="javascript:void(0)" onclick="irParaPaginaEstatisticas(' + (paginaAtualEstatisticas - 1) + ')">Anterior</a>';
            html += '</li>';
            
            // Páginas
            let paginasExibir = [];
            if (totalPaginas <= 7) {
                for (let i = 1; i <= totalPaginas; i++) paginasExibir.push(i);
            } else {
                paginasExibir.push(1);
                if (paginaAtualEstatisticas > 3) paginasExibir.push('...');
                for (let i = Math.max(2, paginaAtualEstatisticas - 1); i <= Math.min(totalPaginas - 1, paginaAtualEstatisticas + 1); i++) {
                    if (!paginasExibir.includes(i)) paginasExibir.push(i);
                }
                if (paginaAtualEstatisticas < totalPaginas - 2) paginasExibir.push('...');
                paginasExibir.push(totalPaginas);
            }
            
            paginasExibir.forEach(p => {
                if (p === '...') {
                    html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
                } else {
                    html += '<li class="page-item ' + (p === paginaAtualEstatisticas ? 'active' : '') + '">';
                    html += '<a class="page-link" href="javascript:void(0)" onclick="irParaPaginaEstatisticas(' + p + ')">' + p + '</a>';
                    html += '</li>';
                }
            });
            
            // Botão Próximo
            html += '<li class="page-item ' + (paginaAtualEstatisticas === totalPaginas ? 'disabled' : '') + '">';
            html += '<a class="page-link" href="javascript:void(0)" onclick="irParaPaginaEstatisticas(' + (paginaAtualEstatisticas + 1) + ')">Próximo</a>';
            html += '</li>';
            
            nav.innerHTML = html;
        }


        // Função para criar gráficos
        function criarGraficos(dados) {
            const cores = ['#1e40af', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#84cc16', '#f97316'];

            // Gráfico de Tempo Médio por Área
            const ctxTempoMedio = document.getElementById('chartTempoMedio').getContext('2d');
            if (dados.estatisticas && dados.estatisticas.length > 0) {
                // Converter tempo médio para horas (aproximado)
                const labels = dados.estatisticas.map(e => e.nome_area_monitorada || 'N/A');
                const data = dados.estatisticas.map(e => {
                    // Parse formato: "46d 02h 11m 15s"
                    const tempo = e.tempo_medio_permanencia || '';
                    const match = tempo.match(/(\d+)d\s+(\d+)h\s+(\d+)m/);
                    if (match) {
                        const dias = parseInt(match[1]);
                        const horas = parseInt(match[2]);
                        return dias * 24 + horas; // Converter para horas
                    }
                    return 0;
                });

                new Chart(ctxTempoMedio, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Tempo Médio (horas)',
                            data: data,
                            backgroundColor: cores.slice(0, dados.estatisticas.length),
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
                                beginAtZero: true,
                                ticks: {
                                    callback: function(value) {
                                        return value + 'h';
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }

        // Função para formatar data/hora
        function formatarDataHora(dataStr) {
            if (!dataStr) return '-';

            try {
                const data = new Date(dataStr);
                const dia = String(data.getDate()).padStart(2, '0');
                const mes = String(data.getMonth() + 1).padStart(2, '0');
                const ano = data.getFullYear();
                const hora = String(data.getHours()).padStart(2, '0');
                const min = String(data.getMinutes()).padStart(2, '0');

                return dia + '/' + mes + '/' + ano + ' ' + hora + ':' + min;
            } catch (e) {
                return dataStr;
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
                    ['Relatório de Permanência de Veículos em Áreas Monitoradas'],
                    [''],
                    ['Área Monitorada', document.getElementById('sel_area_monitorada').selectedOptions[0]?.text || 'Todas'],
                    ['Placa', document.getElementById('txt_placa').value || 'Todas'],
                    ['Período', document.getElementById('txt_data_inicio').value + ' a ' + document.getElementById('txt_data_fim').value],
                    [''],
                    ['Total Passagens', dadosRelatorio.totalPassagens || 0],
                    ['Veículos Únicos', dadosRelatorio.totalVeiculosUnicos || 0],
                    ['Áreas Monitoradas', dadosRelatorio.totalAreas || 0],
                    ['Permanência Média (horas)', dadosRelatorio.permanenciaMedia || 0]
                ];
                XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(resumoData), 'Resumo');
                
                // Passagens Detalhadas
                if (dadosRelatorio.passagens && dadosRelatorio.passagens.length > 0) {
                    const passagensData = [['Placa', 'Área', 'Entrada', 'Saída', 'Duração (horas)']];
                    dadosRelatorio.passagens.forEach(item => {
                        passagensData.push([item.placa, item.area, formatarDataHora(item.entrada), formatarDataHora(item.saida), item.duracao]);
                    });
                    XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(passagensData), 'Passagens');
                }
                
                // Estatísticas por Área
                if (dadosRelatorio.estatisticas && dadosRelatorio.estatisticas.length > 0) {
                    const estatisticasData = [['Área', 'Total Passagens', 'Veículos Únicos', 'Permanência Média (horas)']];
                    dadosRelatorio.estatisticas.forEach(item => {
                        estatisticasData.push([item.area, item.totalPassagens, item.totalVeiculos, item.permanenciaMedia]);
                    });
                    XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(estatisticasData), 'Estatísticas');
                }
                
                const nomeArquivo = 'Relatorio_Permanencia_Veiculos_' + new Date().toISOString().split('T')[0] + '.xlsx';
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

            document.getElementById('txt_data_inicio').valueAsDate = seteDiasAtras;
            document.getElementById('txt_data_fim').valueAsDate = hoje;
            
            // Carregar áreas monitoradas
            carregarAreasMonitoradas();
        };
    </script>
</body>

</html>
