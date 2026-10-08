<%@ page language="java" pageEncoding="utf-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<html lang="pt-BR">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>GTW - Relatório de Fluxo Veicular por Rota</title>
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

        .stat-card-purple {
            background: #7c3aed;
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
            text-align: center;
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
            margin-bottom: 0;
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

        .periodo-badge {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 0.8rem;
            font-weight: 600;
        }

        .periodo-manha {
            background-color: #fef3c7;
            color: #92400e;
        }

        .periodo-tarde {
            background-color: #fed7aa;
            color: #9a3412;
        }

        .periodo-noite {
            background-color: #dbeafe;
            color: #1e40af;
        }

        .periodo-madrugada {
            background-color: #e0e7ff;
            color: #3730a3;
        }

        /* Título visível apenas na impressão */
        .print-title {
            display: none;
        }

        /* Estilo para impressão */
        @media print {
            /* Scroll containers */
            .chart-card .p-3,
            .p-3 {
                max-height: none !important;
                overflow: visible !important;
                padding: 10px !important;
            }

            /* Ocultar elementos de filtro e controle */
            .filter-card, 
            .btn, 
            .no-print, 
            button, 
            #loading_msg, 
            #frm_filtro_relatorio,
            .form-container,
            .dataTables_length,
            .dataTables_filter,
            .dataTables_info,
            .dataTables_paginate {
                display: none !important;
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

            h2, h4, h5 {
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
                max-width: 24% !important;
                flex: 0 0 24% !important;
                padding: 0 3px !important;
            }

            .col-md-6, .col-lg-6 {
                width: 49% !important;
                max-width: 49% !important;
                flex: 0 0 49% !important;
                padding: 0 5px !important;
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

            .stat-card-purple {
                background: #f3e5f5 !important;
                border-color: #7c3aed !important;
            }

            .stat-card-orange {
                background: #ffe5d9 !important;
                border-color: #ea580c !important;
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

            .chart-card {
                box-shadow: none !important;
                border: 1px solid #999 !important;
                border-radius: 5px !important;
                page-break-inside: avoid;
                margin-bottom: 15px !important;
            }

            .chart-header {
                background: #f5f5f5 !important;
                color: #000 !important;
                border-bottom: 2px solid #333 !important;
                padding: 8px 12px !important;
                font-size: 11pt !important;
            }

            /* Tabelas - ajuste para não cortar colunas */
            .grid-table {
                font-size: 7pt !important;
                border-collapse: collapse !important;
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

            .periodo-badge {
                border: 1px solid #333 !important;
                font-size: 6pt !important;
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
            <div class="row mb-4 no-print">
                <div class="col-12">
                    <h2 class="text-center text-dark mb-1">
                        <i class="bi bi-diagram-3"></i> Relatório de Fluxo Veicular por Rota
                    </h2>
                    <p class="text-center text-muted">Análise de fluxo de veículos entre pontos de monitoramento</p>
                </div>
            </div>

            <!-- Formulário de Parâmetros -->
            <div class="form-container no-print">
                <form id="frm_filtro_relatorio">
                    <div class="row">
                        <div class="col-md-3 mb-3">
                            <label for="txt_local_origem" class="form-label fw-bold">
                                <i class="bi bi-geo-alt"></i> Local Origem
                            </label>
                            <select id="txt_local_origem" name="idOrigem" class="form-control" required>
                                <option value="">Selecione um local...</option>
                            </select>
                            <div class="form-text">Selecione o local de origem</div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label for="txt_local_destino" class="form-label fw-bold">
                                <i class="bi bi-geo-alt-fill"></i> Local Destino
                            </label>
                            <select id="txt_local_destino" name="idDestino" class="form-control" required>
                                <option value="">Selecione um local...</option>
                            </select>
                            <div class="form-text">Selecione o local de destino</div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label for="txt_data_inicio" class="form-label fw-bold">
                                <i class="bi bi-calendar"></i> Data Início
                            </label>
                            <input id="txt_data_inicio" type="date" name="dataInicio" class="form-control" required>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label for="txt_data_final" class="form-label fw-bold">
                                <i class="bi bi-calendar"></i> Data Fim
                            </label>
                            <input id="txt_data_final" type="date" name="dataFinal" class="form-control" required>
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

    <script type="text/javascript">
        // Variável global para armazenar os dados
        let dadosRelatorio = null;

        // Inicializar datas padrão (últimos 30 dias)
        document.addEventListener('DOMContentLoaded', function() {
            const hoje = new Date();
            const trintaDiasAtras = new Date();
            trintaDiasAtras.setDate(hoje.getDate() - 30);

            document.getElementById('txt_data_final').value = hoje.toISOString().split('T')[0];
            document.getElementById('txt_data_inicio').value = trintaDiasAtras.toISOString().split('T')[0];
            
            // Carregar locais para os selects
            carregarLocais();
        });

        // Função para carregar a lista de locais via AJAX
        function carregarLocais() {
            const xhr = new XMLHttpRequest();
            xhr.open('GET', '/muralha-digital/relatorio/RelatorioFluxoVeicularRota?acao=carregarLocais', true);
            
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

        // Preenche os selects de Local Origem e Destino com os dados retornados
        function preencherSelectLocais(locais) {
            const selectOrigem = document.getElementById('txt_local_origem');
            const selectDestino = document.getElementById('txt_local_destino');
            
            // Limpar opções existentes exceto a primeira
            while (selectOrigem.options.length > 1) {
                selectOrigem.remove(1);
            }
            while (selectDestino.options.length > 1) {
                selectDestino.remove(1);
            }
            
            // Adicionar opções de locais
            if (locais && Array.isArray(locais)) {
                locais.forEach(function(local) {
                    const optionOrigem = document.createElement('option');
                    optionOrigem.value = local.id;
                    optionOrigem.text = local.nome;
                    selectOrigem.appendChild(optionOrigem);
                    
                    const optionDestino = document.createElement('option');
                    optionDestino.value = local.id;
                    optionDestino.text = local.nome;
                    selectDestino.appendChild(optionDestino);
                });
                console.log('DEBUG: ' + locais.length + ' locais adicionados aos selects');
            }
        }

        // Função para validar parâmetros
        function validarParametros() {
            const dataInicio = document.getElementById('txt_data_inicio').value;
            const dataFinal = document.getElementById('txt_data_final').value;
            const idOrigem = document.getElementById('txt_local_origem').value;
            const idDestino = document.getElementById('txt_local_destino').value;

            if (!dataInicio || !dataFinal || !idOrigem || !idDestino) {
                mostrarMensagem('Por favor, preencha todos os campos.', 'error');
                return false;
            }

            if (new Date(dataInicio) > new Date(dataFinal)) {
                mostrarMensagem('A data início deve ser anterior ou igual à data final.', 'error');
                return false;
            }

            const origem = parseInt(idOrigem);
            const destino = parseInt(idDestino);

            if (origem === destino) {
                mostrarMensagem('O local de origem deve ser diferente do local de destino.', 'error');
                return false;
            }

            if (origem < 1 || destino < 1) {
                mostrarMensagem('Os IDs dos locais devem ser maiores que zero.', 'error');
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
            if (!validarParametros()) {
                return;
            }

            // Mostrar loading
            document.getElementById('btn_gerar').disabled = true;
            document.getElementById('loading_msg').style.display = 'block';

            const dataInicioEl = document.getElementById('txt_data_inicio');
            const dataFinalEl = document.getElementById('txt_data_final');
            const idOrigemEl = document.getElementById('txt_local_origem');
            const idDestinoEl = document.getElementById('txt_local_destino');

            if (!dataInicioEl || !dataFinalEl || !idOrigemEl || !idDestinoEl) {
                mostrarMensagem('Erro: Elementos do formulário não encontrados.', 'error');
                document.getElementById('btn_gerar').disabled = false;
                document.getElementById('loading_msg').style.display = 'none';
                return;
            }

            const dataInicio = dataInicioEl.value;
            const dataFinal = dataFinalEl.value;
            const idOrigem = idOrigemEl.value;
            const idDestino = idDestinoEl.value;

            // Fazer requisição AJAX para o servlet
            const xhr = new XMLHttpRequest();
            xhr.open('POST', '/muralha-digital/relatorio/RelatorioFluxoVeicularRota', true);
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

            const params = 'dataInicio=' + encodeURIComponent(dataInicio) + 
                           '&dataFinal=' + encodeURIComponent(dataFinal) + 
                           '&idOrigem=' + encodeURIComponent(idOrigem) + 
                           '&idDestino=' + encodeURIComponent(idDestino);
            xhr.send(params);
        }

        // Função para criar o dashboard
        function criarDashboard(dados) {
            const container = document.getElementById('dashboard-container');

            // Verificar se os dados existem
            if (!dados || !dados.resumoGeral) {
                container.innerHTML = '<div class="alert alert-danger text-center">Erro: Dados não encontrados ou inválidos</div>';
                return;
            }

            const resumo = dados.resumoGeral;
            const params = dados.parametrosConsulta;

            // Criar HTML usando Bootstrap
            let html = '';

            // Título com informações da consulta
            html += '<div class="row mb-4">';
            html += '<div class="col-12">';
            html += '<div class="alert alert-info text-center">';
            html += '<h5 class="mb-2"><i class="bi bi-calendar-date me-2"></i>Período: ' + (params.dataInicio || '') + ' a ' + (params.dataFinal || '') + '</h5>';
            html += '<p class="mb-0"><strong>Rota:</strong> Local ' + (params.idLocalOrigem || '') + ' → Local ' + (params.idLocalDestino || '') + '</p>';
            html += '</div></div></div>';

            // Cards de Estatísticas Gerais
            html += '<div class="row mb-4">';
            
            html += '<div class="col-md-2">';
            html += '<div class="stat-card stat-card-blue">';
            html += '<i class="bi bi-car-front stat-icon"></i>';
            html += '<div class="stat-value">' + (resumo.totalPassagens || 0) + '</div>';
            html += '<div class="stat-title">Total Passagens</div>';
            html += '</div></div>';

            html += '<div class="col-md-2">';
            html += '<div class="stat-card stat-card-green">';
            html += '<i class="bi bi-hash stat-icon"></i>';
            html += '<div class="stat-value">' + (resumo.totalVeiculosUnicos || 0) + '</div>';
            html += '<div class="stat-title">Veículos Únicos</div>';
            html += '</div></div>';

            html += '<div class="col-md-2">';
            html += '<div class="stat-card stat-card-cyan">';
            html += '<i class="bi bi-clock stat-icon"></i>';
            html += '<div class="stat-value">' + (resumo.tempoMedioMinutos || 0).toFixed(2) + '</div>';
            html += '<div class="stat-title">Tempo Médio (min)</div>';
            html += '</div></div>';

            html += '<div class="col-md-2">';
            html += '<div class="stat-card stat-card-green">';
            html += '<i class="bi bi-arrow-down-circle stat-icon"></i>';
            html += '<div class="stat-value">' + (resumo.tempoMinimoMinutos || 0) + '</div>';
            html += '<div class="stat-title">Tempo Mínimo (min)</div>';
            html += '</div></div>';

            html += '<div class="col-md-2">';
            html += '<div class="stat-card stat-card-orange">';
            html += '<i class="bi bi-arrow-up-circle stat-icon"></i>';
            html += '<div class="stat-value">' + (resumo.tempoMaximoMinutos || 0) + '</div>';
            html += '<div class="stat-title">Tempo Máximo (min)</div>';
            html += '</div></div>';

            html += '<div class="col-md-2">';
            html += '<div class="stat-card stat-card-purple">';
            html += '<i class="bi bi-bar-chart stat-icon"></i>';
            html += '<div class="stat-value">' + (resumo.desvioPadrao || 0).toFixed(2) + '</div>';
            html += '<div class="stat-title">Desvio Padrão</div>';
            html += '</div></div>';
            
            html += '</div>';

            // Gráficos lado a lado
            html += '<div class="row mb-4">';
            
            // Gráfico de Fluxo Horário
            html += '<div class="col-md-6">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Fluxo Horário de Passagens</div>';
            html += '<div class="p-3"><canvas id="chartFluxoHorario" style="height: 300px;"></canvas></div>';
            html += '</div></div>';

            // Gráfico de Tempo Médio por Hora
            html += '<div class="col-md-6">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Tempo Médio de Trânsito por Hora</div>';
            html += '<div class="p-3"><canvas id="chartTempoHorario" style="height: 300px;"></canvas></div>';
            html += '</div></div>';
            
            html += '</div>';

            // Análise por Período do Dia
            html += '<div class="row mb-4">';
            html += '<div class="col-md-6">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Distribuição por Período do Dia</div>';
            html += '<div class="p-3"><canvas id="chartPeriodos" style="height: 300px;"></canvas></div>';
            html += '</div></div>';

            // Distribuição de Tempo de Trânsito
            html += '<div class="col-md-6">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Distribuição de Tempo de Trânsito</div>';
            html += '<div class="p-3"><canvas id="chartDistribuicaoTempo" style="height: 300px;"></canvas></div>';
            html += '</div></div>';
            html += '</div>';

            // Tabelas de dados
            html += '<div class="row mb-4">';
            
            // Top 10 Placas
            html += '<div class="col-md-6">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Top 10 Placas Mais Frequentes</div>';
            html += '<div class="p-3">';
            html += '<table class="table table-hover grid-table" id="tabelaTopPlacasTable">';
            html += '<thead><tr><th>Placa</th><th>Passagens</th><th>Tempo Médio</th><th>Variação</th></tr></thead>';
            html += '<tbody id="tabelaTopPlacas"></tbody>';
            html += '</table></div></div></div>';;

            // Dados por Período
            html += '<div class="col-md-6">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Estatísticas por Período do Dia</div>';
            html += '<div class="p-3">';
            html += '<table class="table table-hover grid-table" id="tabelaPeriodosTable">';
            html += '<thead><tr><th>Período</th><th>Passagens</th><th>Veículos</th><th>Tempo Médio</th></tr></thead>';
            html += '<tbody id="tabelaPeriodos"></tbody>';
            html += '</table></div></div></div>';
            
            html += '</div>';

            // Passagens Detalhadas
            html += '<div class="row mb-4">';
            html += '<div class="col-12">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Passagens Detalhadas</div>';
            html += '<div class="p-3">';
            html += '<table class="table table-hover grid-table" id="tabelaPassagensTable">';
            html += '<thead><tr><th>Placa</th><th>Local Origem</th><th>Hora Origem</th><th>Local Destino</th><th>Hora Destino</th><th>Período</th><th>Tempo Trânsito</th></tr></thead>';
            html += '<tbody id="tabelaPassagens"></tbody>';
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
            // Destruir DataTables existentes
            if ($.fn.DataTable.isDataTable('#tabelaTopPlacasTable')) {
                $('#tabelaTopPlacasTable').DataTable().destroy();
            }
            if ($.fn.DataTable.isDataTable('#tabelaPeriodosTable')) {
                $('#tabelaPeriodosTable').DataTable().destroy();
            }
            if ($.fn.DataTable.isDataTable('#tabelaPassagensTable')) {
                $('#tabelaPassagensTable').DataTable().destroy();
            }
            
            // Top 10 Placas
            const tabelaTopPlacas = document.getElementById('tabelaTopPlacas');
            let htmlTopPlacas = '';
            if (dados.topPlacas && dados.topPlacas.length > 0) {
                dados.topPlacas.forEach(placa => {
                    const variacao = (placa.tempoMaximoMinutos - placa.tempoMinimoMinutos);
                    htmlTopPlacas += '<tr>';
                    htmlTopPlacas += '<td><strong>' + (placa.placa || '') + '</strong></td>';
                    htmlTopPlacas += '<td><strong>' + (placa.totalPassagens || 0) + '</strong></td>';
                    htmlTopPlacas += '<td>' + (placa.tempoMedioMinutos || 0).toFixed(2) + ' min</td>';
                    htmlTopPlacas += '<td>' + (placa.tempoMinimoMinutos || 0) + ' - ' + (placa.tempoMaximoMinutos || 0) + ' min</td>';
                    htmlTopPlacas += '</tr>';
                });
            } else {
                htmlTopPlacas = '<tr><td colspan="4" class="text-center">Nenhum dado encontrado</td></tr>';
            }
            tabelaTopPlacas.innerHTML = htmlTopPlacas;
            
            // Inicializar DataTables para Top Placas
            if (dados.topPlacas && dados.topPlacas.length > 0) {
                $('#tabelaTopPlacasTable').DataTable({
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

            // Tabela por Período
            const tabelaPeriodos = document.getElementById('tabelaPeriodos');
            let htmlPeriodos = '';
            if (dados.dadosPorPeriodo && dados.dadosPorPeriodo.length > 0) {
                dados.dadosPorPeriodo.forEach(periodo => {
                    const badgeClass = 'periodo-' + periodo.periodoDia.toLowerCase();
                    htmlPeriodos += '<tr>';
                    htmlPeriodos += '<td><span class="periodo-badge ' + badgeClass + '">' + (periodo.periodoDia || '') + '</span></td>';
                    htmlPeriodos += '<td><strong>' + (periodo.totalPassagens || 0) + '</strong> (' + (periodo.percentualDoTotal || 0).toFixed(1) + '%)</td>';
                    htmlPeriodos += '<td>' + (periodo.veiculosUnicos || 0) + '</td>';
                    htmlPeriodos += '<td>' + (periodo.tempoMedioMinutos || 0).toFixed(2) + ' min</td>';
                    htmlPeriodos += '</tr>';
                });
            } else {
                htmlPeriodos = '<tr><td colspan="4" class="text-center">Nenhum dado encontrado</td></tr>';
            }
            tabelaPeriodos.innerHTML = htmlPeriodos;
            
            // Inicializar DataTables para Períodos
            if (dados.dadosPorPeriodo && dados.dadosPorPeriodo.length > 0) {
                $('#tabelaPeriodosTable').DataTable({
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

            // Passagens Detalhadas
            const tabelaPassagens = document.getElementById('tabelaPassagens');
            let htmlPassagens = '';
            if (dados.passagensDetalhadas && dados.passagensDetalhadas.length > 0) {
                dados.passagensDetalhadas.forEach(passagem => {
                    const badgeClass = 'periodo-' + passagem.periodoDia.toLowerCase();
                    htmlPassagens += '<tr>';
                    htmlPassagens += '<td><strong>' + (passagem.placa || '') + '</strong></td>';
                    htmlPassagens += '<td><span class="badge bg-primary">ID: ' + (passagem.idLocalOrigem || '') + '</span></td>';
                    htmlPassagens += '<td>' + (passagem.origem || '') + '</td>';
                    htmlPassagens += '<td><span class="badge bg-success">ID: ' + (passagem.idLocalDestino || '') + '</span></td>';
                    htmlPassagens += '<td>' + (passagem.destino || '') + '</td>';
                    htmlPassagens += '<td><span class="periodo-badge ' + badgeClass + '">' + (passagem.periodoDia || '') + '</span></td>';
                    htmlPassagens += '<td><strong>' + (passagem.tempoTransitoMinutos || 0) + ' min</strong></td>';
                    htmlPassagens += '</tr>';
                });
            } else {
                htmlPassagens = '<tr><td colspan="7" class="text-center">Nenhum dado encontrado</td></tr>';
            }
            tabelaPassagens.innerHTML = htmlPassagens;
            
            // Inicializar DataTables para Passagens
            if (dados.passagensDetalhadas && dados.passagensDetalhadas.length > 0) {
                $('#tabelaPassagensTable').DataTable({
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
                    order: [[0, 'asc']]
                });
            }
        }

        // Função para criar gráficos
        function criarGraficos(dados) {
            const cores = {
                azul: '#3b82f6',
                verde: '#10b981',
                amarelo: '#f59e0b',
                laranja: '#f97316',
                roxo: '#8b5cf6',
                ciano: '#06b6d4',
                rosa: '#ec4899',
                vermelho: '#ef4444'
            };

            // Gráfico de Fluxo Horário
            const ctxFluxoHorario = document.getElementById('chartFluxoHorario').getContext('2d');
            if (dados.dadosHorarios && dados.dadosHorarios.length > 0) {
                // Completar array com 24 horas
                const dadosCompletos = Array(24).fill(0).map((_, i) => {
                    const dado = dados.dadosHorarios.find(d => d.hora === i);
                    return dado ? dado.totalPassagens : 0;
                });

                const coresBarras = Array(24).fill(0).map((_, i) => {
                    if (i >= 6 && i <= 11) return cores.amarelo; // Manhã
                    if (i >= 12 && i <= 17) return cores.laranja; // Tarde
                    if (i >= 18 && i <= 23) return cores.azul; // Noite
                    return cores.roxo; // Madrugada
                });

                new Chart(ctxFluxoHorario, {
                    type: 'bar',
                    data: {
                        labels: Array(24).fill(0).map((_, i) => i.toString().padStart(2, '0') + 'h'),
                        datasets: [{
                            label: 'Passagens',
                            data: dadosCompletos,
                            backgroundColor: coresBarras,
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
                                    precision: 0
                                }
                            }
                        }
                    }
                });
            }

            // Gráfico de Tempo Médio Horário
            const ctxTempoHorario = document.getElementById('chartTempoHorario').getContext('2d');
            if (dados.dadosHorarios && dados.dadosHorarios.length > 0) {
                const labels = dados.dadosHorarios.map(d => d.hora.toString().padStart(2, '0') + 'h');
                const tempos = dados.dadosHorarios.map(d => d.tempoMedioMinutos);

                new Chart(ctxTempoHorario, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Tempo Médio (min)',
                            data: tempos,
                            borderColor: cores.ciano,
                            backgroundColor: cores.ciano + '40',
                            tension: 0.3,
                            fill: true
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                display: true
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

            // Gráfico de Períodos
            const ctxPeriodos = document.getElementById('chartPeriodos').getContext('2d');
            if (dados.dadosPorPeriodo && dados.dadosPorPeriodo.length > 0) {
                new Chart(ctxPeriodos, {
                    type: 'doughnut',
                    data: {
                        labels: dados.dadosPorPeriodo.map(p => p.periodoDia),
                        datasets: [{
                            data: dados.dadosPorPeriodo.map(p => p.totalPassagens),
                            backgroundColor: [cores.amarelo, cores.laranja, cores.azul, cores.roxo],
                            borderWidth: 2,
                            borderColor: '#fff'
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'bottom'
                            }
                        }
                    }
                });
            }

            // Gráfico de Distribuição de Tempo
            const ctxDistribuicao = document.getElementById('chartDistribuicaoTempo').getContext('2d');
            if (dados.distribuicaoTempo && dados.distribuicaoTempo.length > 0) {
                new Chart(ctxDistribuicao, {
                    type: 'bar',
                    data: {
                        labels: dados.distribuicaoTempo.map(d => d.faixaTempo),
                        datasets: [{
                            label: 'Passagens',
                            data: dados.distribuicaoTempo.map(d => d.totalPassagens),
                            backgroundColor: cores.verde,
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
                                beginAtZero: true,
                                ticks: {
                                    precision: 0
                                }
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
                const params = dadosRelatorio.parametrosConsulta || {};
                const resumo = dadosRelatorio.resumoGeral || {};
                
                // Resumo
                const resumoData = [
                    ['Relatório de Fluxo Veicular por Rota'],
                    [''],
                    ['Período', (params.dataInicio || '') + ' a ' + (params.dataFinal || '')],
                    ['Local Origem', params.idLocalOrigem || ''],
                    ['Local Destino', params.idLocalDestino || ''],
                    [''],
                    ['Total Passagens', resumo.totalPassagens || 0],
                    ['Veículos Únicos', resumo.totalVeiculosUnicos || 0],
                    ['Tempo Médio (min)', resumo.tempoMedioMinutos || 0],
                    ['Tempo Mínimo (min)', resumo.tempoMinimoMinutos || 0],
                    ['Tempo Máximo (min)', resumo.tempoMaximoMinutos || 0],
                    ['Desvio Padrão', resumo.desvioPadrao || 0]
                ];
                XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(resumoData), 'Resumo');
                
                // Top Placas
                if (dadosRelatorio.topPlacas && dadosRelatorio.topPlacas.length > 0) {
                    const topPlacasData = [['Placa', 'Passagens', 'Tempo Médio (min)', 'Tempo Mínimo', 'Tempo Máximo']];
                    dadosRelatorio.topPlacas.forEach(item => {
                        topPlacasData.push([item.placa, item.totalPassagens, item.tempoMedioMinutos, item.tempoMinimoMinutos, item.tempoMaximoMinutos]);
                    });
                    XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(topPlacasData), 'Top Placas');
                }
                
                // Dados por Período
                if (dadosRelatorio.dadosPorPeriodo && dadosRelatorio.dadosPorPeriodo.length > 0) {
                    const periodosData = [['Período', 'Passagens', 'Veículos', 'Tempo Médio']];
                    dadosRelatorio.dadosPorPeriodo.forEach(item => {
                        periodosData.push([item.periodoDia, item.totalPassagens, item.totalVeiculos, item.tempoMedioMinutos]);
                    });
                    XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(periodosData), 'Por Período');
                }
                
                // Passagens Detalhadas
                if (dadosRelatorio.passagensDetalhadas && dadosRelatorio.passagensDetalhadas.length > 0) {
                    const passagensData = [['Placa', 'Local Origem', 'Hora Origem', 'Local Destino', 'Hora Destino', 'Período', 'Tempo Trânsito']];
                    dadosRelatorio.passagensDetalhadas.forEach(item => {
                        passagensData.push([item.placa, item.localOrigem, item.horaOrigem, item.localDestino, item.horaDestino, item.periodoDia, item.tempoTransito]);
                    });
                    XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(passagensData), 'Passagens Detalhadas');
                }
                
                const nomeArquivo = 'Relatorio_Fluxo_Veicular_Rota_' + new Date().toISOString().split('T')[0] + '.xlsx';
                XLSX.writeFile(wb, nomeArquivo);
                
            } catch (e) {
                console.error('Erro ao exportar para Excel:', e);
                alert('Erro ao exportar para Excel: ' + e.message);
            }
        }

        // Definir data padrão (hoje)
        window.onload = function () {
            const hoje = new Date();
            document.getElementById('txt_data_referencia').valueAsDate = hoje;
        };
    </script>
</body>

</html>
