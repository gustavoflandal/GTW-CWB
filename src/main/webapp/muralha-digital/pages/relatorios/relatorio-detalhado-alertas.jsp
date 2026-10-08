<%@ page language="java" pageEncoding="utf-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<html lang="pt-BR">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>GTW - Relatório Detalhado de Alertas</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <!-- SheetJS para exportação Excel -->
    <script src="https://cdn.sheetjs.com/xlsx-0.20.1/package/dist/xlsx.full.min.js"></script>

    <style>
        /* Dashboard customizado com Bootstrap */
        .dashboard-container {
            background: #f8f9fa;
            min-height: 100vh;
            padding: 20px;
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
            background: linear-gradient(135deg, #1e40af, #3b82f6);
        }

        .stat-card-green {
            background: linear-gradient(135deg, #059669, #10b981);
        }

        .stat-card-yellow {
            background: linear-gradient(135deg, #d97706, #f59e0b);
        }

        .stat-card-red {
            background: linear-gradient(135deg, #dc2626, #ef4444);
        }

        .stat-card-purple {
            background: linear-gradient(135deg, #7c3aed, #a78bfa);
        }

        .stat-card-orange {
            background: linear-gradient(135deg, #ea580c, #f97316);
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
            background-color: #1e40af;
            color: white;
            font-weight: 600;
            padding: 10px 8px;
            /* Removido position: sticky para evitar overlay */
        }

        .grid-table td {
            padding: 8px;
            border-bottom: 1px solid #e5e7eb;
        }

        .grid-table tbody tr:hover {
            background-color: #e3f2fd;
        }

        .badge-situacao {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 0.75rem;
            font-weight: 600;
        }

        /* Badges com cores exatas sincronizadas com os charts */
        .badge-pendente { background-color: #f59e0b; color: #111827; }
        .badge-ocorrencia { background-color: #ef4444; color: #fff; }
        .badge-vinculado { background-color: #10b981; color: #fff; }
        .badge-default { background-color: #6b7280; color: #fff; }

        /* Badges de leitura */
        .badge-leitura-captura { background-color: #10b981; color: #fff; }
        .badge-leitura-divergente { background-color: #ef4444; color: #fff; }

        .table-container {
            position: relative;
            border: 1px solid #e5e7eb;
            border-radius: 8px;
            overflow: hidden;
        }

        .table-wrapper {
            overflow-x: auto;
        }

        /* NÃO transformar thead/tbody em blocos — mantém o alinhamento das colunas */

        /* Linha de filtros abaixo do cabeçalho */
        .grid-table thead tr.filter-row th {
            /* Removido position: sticky */
            background-color: #ffffff;
            color: #111827;
            padding: 6px 8px;
            border-bottom: 2px solid #1e40af;
        }

        .col-filter {
            width: 100%;
            box-sizing: border-box;
            font-size: 0.85rem;
        }

        .btn-export {
            margin-left: 10px;
        }

        /* Sombra leve para separar cabeçalho e conteúdo da tabela */
        .grid-table thead tr th {
            box-shadow: 0 2px 4px rgba(0,0,0,0.06);
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

            .filter-card, .btn, .no-print, button, form, #loading_msg, .btn-export,
            .form-container, .dataTables_length, .dataTables_filter, .dataTables_info, .dataTables_paginate {
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

            .container {
                max-width: 100% !important;
                width: 100% !important;
                padding: 0 !important;
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
            .stat-card-purple { background: #f3e5f5 !important; border-color: #7c3aed !important; }

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
            
            .table-responsive,
            .table-wrapper {
                overflow: visible !important;
            }
            
            .badge-situacao {
                font-size: 6pt !important;
                padding: 2px 4px !important;
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
                        <i class="bi bi-exclamation-triangle"></i> Relatório Detalhado de Alertas e Irregularidades
                    </h2>
                    <p class="text-center text-muted">Análise detalhada de alertas gerados pelo sistema de monitoramento</p>
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
    </div>

    <script type="text/javascript">
        // Variável global para armazenar os dados
        let dadosRelatorio = null;
        
        // Variáveis de controle de paginação
        let paginaAtual = 1;
        let registrosPorPagina = 25;
        let dadosFiltrados = [];

        // Função para validar parâmetros
        function validarParametros() {
            const dataInicio = document.getElementById('txt_data_inicio').value;
            const dataFinal = document.getElementById('txt_data_final').value;

            if (!dataInicio || !dataFinal) {
                mostrarMensagem('Por favor, preencha todos os campos.', 'error');
                return false;
            }

            if (new Date(dataInicio) > new Date(dataFinal)) {
                mostrarMensagem('A data início deve ser anterior ou igual à data final.', 'error');
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

            const dataInicio = document.getElementById('txt_data_inicio').value;
            const dataFinal = document.getElementById('txt_data_final').value;

            // Fazer requisição AJAX para o servlet
            const xhr = new XMLHttpRequest();
            xhr.open('POST', '/muralha-digital/relatorio/RelatorioDetalhadoAlertas', true);
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
                           '&dataFinal=' + encodeURIComponent(dataFinal);
            xhr.send(params);
        }

        // Função para criar o dashboard
        function criarDashboard(dados) {
            const container = document.getElementById('dashboard-container');

            // Verificar se os dados existem
            if (!dados || !dados.irregularidadesDetalhadas) {
                container.innerHTML = '<div class="alert alert-danger text-center">Erro: Dados não encontrados ou inválidos</div>';
                return;
            }

            const params = dados.parametrosConsulta;
            const irregularidades = dados.irregularidadesDetalhadas;

            // Calcular totais (usar recordsets agregados quando disponíveis)
            const totalIrregularidades = irregularidades.length;
            const totalTipos = dados.contagemPorTipo ? dados.contagemPorTipo.length : 0;

            // Pendentes: preferir contagem agregada por situação quando fornecida
            let totalPendentes = 0;
            if (dados.contagemPorSituacao && dados.contagemPorSituacao.length > 0) {
                const pend = dados.contagemPorSituacao.find(s => s.situacao && s.situacao.toUpperCase() === 'PENDENTE');
                totalPendentes = pend ? pend.total : 0;
            } else {
                totalPendentes = irregularidades.filter(i => i.situacao && i.situacao.toUpperCase() === 'PENDENTE').length;
            }

            // Finalizados: preferir recordset statusFinalizacao quando disponível
            let totalFinalizados = 0;
            if (dados.statusFinalizacao && dados.statusFinalizacao.length > 0) {
                // procurar chave que indique finalizado (p.ex. 'FINALIZADO' ou contendo 'FINALIZ')
                const sf = dados.statusFinalizacao.find(s => s.statusFinalizacao && s.statusFinalizacao.toUpperCase().includes('FINALIZ'));
                totalFinalizados = sf ? sf.total : 0;
            } else {
                // fallback: contar registros com campo 'fim' válido
                totalFinalizados = irregularidades.filter(i => i.fim && i.fim !== '' && i.fim !== '-' && i.fim.toUpperCase() !== 'NULL').length;
            }

            // Criar HTML usando Bootstrap
            let html = '';

            // Título com informações da consulta
            html += '<div class="row mb-4">';
            html += '<div class="col-12">';
            html += '<div class="alert alert-info text-center">';
            html += '<h5 class="mb-2"><i class="bi bi-calendar-date me-2"></i>Período: ' + (params.dataInicio || '') + ' a ' + (params.dataFinal || '') + '</h5>';
            html += '</div></div></div>';

            // Cards de Estatísticas Gerais
            html += '<div class="row mb-4">';
            
            html += '<div class="col-md-3">';
            html += '<div class="stat-card stat-card-blue">';
            html += '<i class="bi bi-exclamation-triangle stat-icon"></i>';
            html += '<div class="stat-value">' + totalIrregularidades + '</div>';
            html += '<div class="stat-title">Total de Alertas</div>';
            html += '</div></div>';

            html += '<div class="col-md-3">';
            html += '<div class="stat-card stat-card-green">';
            html += '<i class="bi bi-check-circle stat-icon"></i>';
            html += '<div class="stat-value">' + totalFinalizados + '</div>';
            html += '<div class="stat-title">Finalizados</div>';
            html += '</div></div>';

            html += '<div class="col-md-3">';
            html += '<div class="stat-card stat-card-yellow">';
            html += '<i class="bi bi-hourglass-split stat-icon"></i>';
            html += '<div class="stat-value">' + totalPendentes + '</div>';
            html += '<div class="stat-title">Pendentes</div>';
            html += '</div></div>';

            html += '<div class="col-md-3">';
            html += '<div class="stat-card stat-card-purple">';
            html += '<i class="bi bi-tag stat-icon"></i>';
            html += '<div class="stat-value">' + totalTipos + '</div>';
            html += '<div class="stat-title">Tipos de Alertas</div>';
            html += '</div></div>';
            
            html += '</div>';

            // Gráficos lado a lado (Tipos, Leitura, Situação)
            html += '<div class="row mb-4">';
            
            // Gráfico de Tipos
            html += '<div class="col-md-4">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Distribuição por Tipo</div>';
            html += '<div class="p-3"><canvas id="chartTipos" style="height: 300px;"></canvas></div>';
            html += '</div></div>';

            // Gráfico de Leitura
            html += '<div class="col-md-4">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Distribuição por Leitura</div>';
            html += '<div class="p-3"><canvas id="chartLeitura" style="height: 300px;"></canvas></div>';
            html += '</div></div>';

            // Gráfico de Situação
            html += '<div class="col-md-4">';
            html += '<div class="chart-card">';
            html += '<div class="chart-header">Distribuição por Situação</div>';
            html += '<div class="p-3"><canvas id="chartSituacao" style="height: 300px;"></canvas></div>';
            html += '</div></div>';

            html += '</div>';

            // Grid com dados detalhados (removida a barra de título; mantém cabeçalho fixo com filtros)
            html += '<div class="row mb-4">';
            html += '<div class="col-12">';
            
            // Controle de quantidade de registros (no topo)
            html += '<div class="d-flex justify-content-between align-items-center mb-2">';
            html += '<div class="d-flex align-items-center">';
            html += '<label class="me-2 mb-0">Mostrar</label>';
            html += '<select id="selectQtdLinhas" class="form-select form-select-sm" style="width: auto;" onchange="alterarQtdLinhas()">';
            html += '<option value="10">10</option>';
            html += '<option value="25" selected>25</option>';
            html += '<option value="50">50</option>';
            html += '<option value="100">100</option>';
            html += '<option value="-1">Todos</option>';
            html += '</select>';
            html += '<label class="ms-2 mb-0">registros por página</label>';
            html += '</div>';
            html += '</div>';
            
            html += '<div class="table-container">';
            html += '<div class="table-wrapper">';
            html += '<table class="table table-hover grid-table" id="tabelaIrregularidades" style="margin-bottom: 0;">';
            html += '<thead>';
            // linha principal do cabeçalho (fixa)
            html += '<tr>';
            html += '<th>Data</th>';
            html += '<th>Tipo</th>';
            html += '<th>Equipamento</th>';
            html += '<th>Placa Cadastrada</th>';
            html += '<th>Placa Capturada</th>';
            html += '<th>Leitura</th>';
            html += '<th>Início</th>';
            html += '<th>Fim</th>';
            html += '<th>Situação</th>';
            html += '</tr>';
            // linha de filtros
            html += '<tr class="filter-row">';
            html += '<th><input class="col-filter" data-col="0" placeholder="filtro Data"></th>';
            html += '<th><input class="col-filter" data-col="1" placeholder="filtro Tipo"></th>';
            html += '<th><input class="col-filter" data-col="2" placeholder="filtro Equip"></th>';
            html += '<th><input class="col-filter" data-col="3" placeholder="filtro Placa Cad"></th>';
            html += '<th><input class="col-filter" data-col="4" placeholder="filtro Placa Cap"></th>';
            html += '<th><input class="col-filter" data-col="5" placeholder="filtro Leitura"></th>';
            html += '<th><input class="col-filter" data-col="6" placeholder="filtro Início"></th>';
            html += '<th><input class="col-filter" data-col="7" placeholder="filtro Fim"></th>';
            html += '<th><input class="col-filter" data-col="8" placeholder="filtro Situação"></th>';
            html += '</tr>';
            html += '</thead>';
            html += '<tbody id="tabelaCorpo"></tbody>';
            html += '</table>';
            html += '</div>'; // fecha table-wrapper
            html += '</div>'; // fecha table-container
            
            // Rodapé da tabela com paginação
            html += '<div class="d-flex justify-content-between align-items-center mt-2 p-2" style="background: #f8f9fa; border: 1px solid #e5e7eb; border-radius: 8px;">';
            html += '<div id="infoPaginacao" class="text-muted small"></div>';
            html += '<nav><ul class="pagination pagination-sm mb-0" id="paginacaoNav"></ul></nav>';
            html += '</div>';
            
            html += '</div>'; // fecha col-12
            html += '</div>'; // fecha row

            container.innerHTML = html;

            // Preencher tabela
            preencherTabela(irregularidades);

            // Ajustar header sticky imediatamente após preencher a tabela
            setTimeout(() => {
                ajustarHeaderSticky();
                sincronizarLarguraColunas();
            }, 20);

            // Inicializar filtros da tabela (chamada separada para garantir que os inputs existam)
            setTimeout(() => {
                inicializarFiltros();
                ajustarHeaderSticky();
                sincronizarLarguraColunas();
            }, 80);

            // Criar gráficos
            setTimeout(() => {
                criarGraficos(dados);
            }, 100);

            // Reajustar sticky headers ao redimensionar a janela
            window.addEventListener('resize', function () {
                ajustarHeaderSticky();
                sincronizarLarguraColunas();
            });
        }

        // Sincroniza a largura das colunas (simplificado - tabela única)
        function sincronizarLarguraColunas() {
            // Não é mais necessário sincronizar entre tabelas separadas
            // A tabela agora é única e se ajusta automaticamente
        }

        // Função para formatar data para padrão brasileiro (somente data, sem hora)
        function formatarDataBR(data) {
            if (!data || data === 'NULL' || data === '') return '';
            
            // Se já está no formato dd/MM/yyyy, retorna apenas a parte da data
            if (data.match(/^\d{2}\/\d{2}\/\d{4}/)) {
                return data.split(' ')[0];
            }
            
            // Se está no formato yyyy-MM-dd, converte para dd/MM/yyyy (sem hora)
            if (data.match(/^\d{4}-\d{2}-\d{2}/)) {
                const dataPartes = data.split(/[\s-]/)[0].split('-');
                return dataPartes[2] + '/' + dataPartes[1] + '/' + dataPartes[0];
            }
            
            return data;
        }

        // Função para preencher tabela
        function preencherTabela(irregularidades) {
            const tbody = document.getElementById('tabelaCorpo');
            let html = '';
            
            // Armazenar dados filtrados para paginação
            dadosFiltrados = irregularidades || [];
            paginaAtual = 1;

            if (irregularidades && irregularidades.length > 0) {
                irregularidades.forEach((item, index) => {
                    const situacaoUpper = (item.situacao || '').toString().toUpperCase();
                    const badgeClass = situacaoUpper === 'PENDENTE' ? 'badge-pendente' : 
                                      situacaoUpper === 'OCORRÊNCIA' ? 'badge-ocorrencia' : 
                                      situacaoUpper === 'VINCULADO' ? 'badge-vinculado' : 'badge-default';
                    
                    // Badge para leitura
                    const leituraBadgeClass = (item.leitura || '').toString().toUpperCase().indexOf('CAPTURA') === 0 ? 'badge-leitura-captura' : 'badge-leitura-divergente';
                    
                    html += '<tr data-index="' + index + '">';
                    html += '<td data-col="0">' + formatarDataBR(item.data || '') + '</td>';
                    html += '<td data-col="1">' + (item.tipo || '') + '</td>';
                    html += '<td data-col="2">' + (item.equipamento || '') + '</td>';
                    html += '<td data-col="3"><strong>' + (item.placaCad || '') + '</strong></td>';
                    html += '<td data-col="4"><strong>' + (item.placaCap || '') + '</strong></td>';
                    html += '<td data-col="5"><span class="badge ' + leituraBadgeClass + '">' + (item.leitura || '') + '</span></td>';
                    html += '<td data-col="6">' + formatarDataBR(item.inicio || '') + '</td>';
                    html += '<td data-col="7">' + (item.fim && item.fim !== 'NULL' && item.fim !== 'undefined/undefined/NaN' && item.fim.trim() !== '' ? formatarDataBR(item.fim) : '-') + '</td>';
                    // mapear background leve para a célula de situação (opcional)
                    let situacaoBg = '';
                    if (situacaoUpper === 'PENDENTE') situacaoBg = 'background: rgba(245, 158, 11, 0.12);'; // #f59e0b
                    else if (situacaoUpper === 'OCORRÊNCIA') situacaoBg = 'background: rgba(239, 68, 68, 0.12);'; // #ef4444
                    else if (situacaoUpper === 'VINCULADO' || situacaoUpper.indexOf('FINALIZ') === 0) situacaoBg = 'background: rgba(16, 185, 129, 0.12);'; // #10b981
                    else situacaoBg = '';

                    html += '<td data-col="8" style="' + situacaoBg + '"><span class="badge ' + badgeClass + '">' + (item.situacao || '') + '</span></td>';
                    html += '</tr>';
                });
            } else {
                html = '<tr><td colspan="9" class="text-center">Nenhum dado encontrado</td></tr>';
            }

            tbody.innerHTML = html;
            
            // Aplicar paginação
            aplicarPaginacao();
        }
        
        // Função para alterar quantidade de linhas por página
        function alterarQtdLinhas() {
            const select = document.getElementById('selectQtdLinhas');
            registrosPorPagina = parseInt(select.value);
            paginaAtual = 1;
            aplicarPaginacao();
        }
        
        // Função para ir para uma página específica
        function irParaPagina(pagina) {
            paginaAtual = pagina;
            aplicarPaginacao();
        }
        
        // Função para aplicar paginação na tabela
        function aplicarPaginacao() {
            const tbody = document.getElementById('tabelaCorpo');
            const rows = Array.from(tbody.querySelectorAll('tr[data-index]'));
            
            // Primeiro, obter apenas as linhas visíveis (não filtradas)
            const linhasVisiveis = rows.filter(row => {
                // Verificar se a linha não está oculta por filtro
                const filtros = Array.from(document.querySelectorAll('.col-filter')).map(inp => inp.value.trim().toUpperCase());
                const cells = Array.from(row.querySelectorAll('td'));
                
                for (let i = 0; i < filtros.length; i++) {
                    const filtro = filtros[i];
                    if (!filtro) continue;
                    
                    const cell = cells.find(c => c.getAttribute('data-col') === String(i));
                    const texto = cell ? cell.textContent.trim().toUpperCase() : '';
                    
                    if (texto.indexOf(filtro) === -1) {
                        return false;
                    }
                }
                return true;
            });
            
            const totalRegistros = linhasVisiveis.length;
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
            
            // Ocultar todas as linhas primeiro
            rows.forEach(row => row.style.display = 'none');
            
            // Mostrar apenas as linhas da página atual (considerando filtros)
            linhasVisiveis.forEach((row, index) => {
                if (index >= inicio && index < fim) {
                    row.style.display = '';
                }
            });
            
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
        
        // Função para atualizar a navegação de páginas
        function atualizarNavegacaoPaginas(totalPaginas, totalRegistros) {
            const nav = document.getElementById('paginacaoNav');
            if (!nav) return;
            
            let html = '';
            
            // Se mostrar todos ou apenas uma página, ocultar navegação
            if (registrosPorPagina === -1 || totalPaginas <= 1) {
                nav.innerHTML = '';
                return;
            }
            
            // Botão Anterior
            html += '<li class="page-item ' + (paginaAtual === 1 ? 'disabled' : '') + '">';
            html += '<a class="page-link" href="javascript:void(0)" onclick="irParaPagina(' + (paginaAtual - 1) + ')" ' + (paginaAtual === 1 ? 'tabindex="-1"' : '') + '>Anterior</a>';
            html += '</li>';
            
            // Páginas
            let paginasExibir = [];
            if (totalPaginas <= 7) {
                for (let i = 1; i <= totalPaginas; i++) {
                    paginasExibir.push(i);
                }
            } else {
                paginasExibir.push(1);
                
                if (paginaAtual > 3) {
                    paginasExibir.push('...');
                }
                
                for (let i = Math.max(2, paginaAtual - 1); i <= Math.min(totalPaginas - 1, paginaAtual + 1); i++) {
                    if (!paginasExibir.includes(i)) {
                        paginasExibir.push(i);
                    }
                }
                
                if (paginaAtual < totalPaginas - 2) {
                    paginasExibir.push('...');
                }
                
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
            html += '<a class="page-link" href="javascript:void(0)" onclick="irParaPagina(' + (paginaAtual + 1) + ')" ' + (paginaAtual === totalPaginas ? 'tabindex="-1"' : '') + '>Próximo</a>';
            html += '</li>';
            
            nav.innerHTML = html;
        }

        // Função que aplica os filtros da linha de filtro na tabela
        function aplicarFiltrosTabela() {
            // Resetar para primeira página ao filtrar
            paginaAtual = 1;
            // Reaplicar paginação (que já considera os filtros)
            aplicarPaginacao();
        }

        // Vincular evento de input aos campos de filtro
        function inicializarFiltros() {
            const inputs = document.querySelectorAll('.col-filter');
            inputs.forEach(inp => {
                inp.addEventListener('input', function () {
                    aplicarFiltrosTabela();
                });
            });
        }

        // Limpa todos os filtros e reaplica (mostra todas as linhas)
        function limparFiltros() {
            const inputs = document.querySelectorAll('.col-filter');
            inputs.forEach(inp => inp.value = '');
            paginaAtual = 1;
            aplicarPaginacao();
            ajustarHeaderSticky();
            mostrarMensagem('Filtros limpos', 'success');
        }

        // Ajusta dinamicamente a largura das colunas (simplificado)
        function ajustarHeaderSticky() {
            // Agora apenas chama sincronização de larguras, pois não há mais sticky positioning
            sincronizarLarguraColunas();
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
                vermelho: '#ef4444',
                cinza: '#6b7280'
            };

            const coresPizza = [cores.azul, cores.verde, cores.amarelo, cores.laranja, cores.roxo, cores.ciano, cores.rosa, cores.vermelho];

            // Funções utilitárias para mapear cores por label (case-insensitive)
            function corParaLeitura(label) {
                // Retorna hex exato usado também nas classes de badge
                if (!label) return cores.cinza;
                const l = label.toString().toUpperCase();
                if (l.indexOf('DIVERG') === 0 || l === 'DIVERGENTE') return '#ef4444'; // vermelho
                if (l.indexOf('CAPTURA') === 0 || l === 'CAPTURA OK') return '#10b981'; // verde
                return coresPizza[Math.abs(hashCode(label)) % coresPizza.length];
            }

            function corParaSituacao(label) {
                // Retorna hex exato usado também nas classes de badge
                if (!label) return cores.cinza;
                const l = label.toString().toUpperCase();
                if (l === 'PENDENTE' || l.indexOf('PENDENT') === 0) return '#f59e0b'; // amarelo
                if (l.indexOf('FINALIZ') === 0 || l === 'FINALIZADO') return '#10b981'; // verde
                if (l === 'VINCULADO') return '#10b981';
                if (l === 'OCORRÊNCIA' || l.indexOf('OCORR') === 0) return '#ef4444'; // vermelho
                return coresPizza[Math.abs(hashCode(label)) % coresPizza.length];
            }

            // hash simples para espalhar cores quando necessário
            function hashCode(str) {
                let hash = 0;
                for (let i = 0; i < str.length; i++) {
                    const chr = str.charCodeAt(i);
                    hash = ((hash << 5) - hash) + chr;
                    hash |= 0; // conversão para 32bit
                }
                return hash;
            }

            // Gráfico de Tipos
            if (dados.contagemPorTipo && dados.contagemPorTipo.length > 0) {
                const ctxTipos = document.getElementById('chartTipos').getContext('2d');
                new Chart(ctxTipos, {
                    type: 'pie',
                    data: {
                        labels: dados.contagemPorTipo.map(t => t.tipo),
                        datasets: [{
                            data: dados.contagemPorTipo.map(t => t.total),
                            backgroundColor: coresPizza.slice(0, dados.contagemPorTipo.length),
                            borderWidth: 2,
                            borderColor: '#fff'
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'bottom',
                                labels: {
                                    boxWidth: 12,
                                    font: { size: 10 }
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const label = context.label || '';
                                        const value = context.parsed || 0;
                                        const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                        const percentage = ((value / total) * 100).toFixed(1);
                                        return label + ': ' + value + ' (' + percentage + '%)';
                                    }
                                }
                            },
                            datalabels: {
                                formatter: (value, ctx) => {
                                    return value;
                                },
                                color: '#fff',
                                font: {
                                    weight: 'bold',
                                    size: 12
                                }
                            }
                        }
                    },
                    plugins: [{
                        afterDatasetsDraw: function(chart) {
                            const ctx = chart.ctx;
                            chart.data.datasets.forEach((dataset, i) => {
                                const meta = chart.getDatasetMeta(i);
                                meta.data.forEach((element, index) => {
                                    const data = dataset.data[index];
                                    if (data > 0) {
                                        ctx.fillStyle = '#fff';
                                        ctx.font = 'bold 12px Arial';
                                        ctx.textAlign = 'center';
                                        ctx.textBaseline = 'middle';
                                        const position = element.tooltipPosition();
                                        ctx.fillText(data, position.x, position.y);
                                    }
                                });
                            });
                        }
                    }]
                });
            }

            // Gráfico de Leitura
            if (dados.contagemPorLeitura && dados.contagemPorLeitura.length > 0) {
                const ctxLeitura = document.getElementById('chartLeitura').getContext('2d');
                new Chart(ctxLeitura, {
                    type: 'doughnut',
                    data: {
                        labels: dados.contagemPorLeitura.map(l => l.leitura),
                        datasets: [{
                            data: dados.contagemPorLeitura.map(l => l.total),
                            backgroundColor: dados.contagemPorLeitura.map(l => corParaLeitura(l.leitura)),
                            borderWidth: 2,
                            borderColor: '#fff'
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'bottom',
                                labels: {
                                    boxWidth: 12,
                                    font: { size: 10 }
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const label = context.label || '';
                                        const value = context.parsed || 0;
                                        const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                        const percentage = ((value / total) * 100).toFixed(1);
                                        return label + ': ' + value + ' (' + percentage + '%)';
                                    }
                                }
                            }
                        }
                    },
                    plugins: [{
                        afterDatasetsDraw: function(chart) {
                            const ctx = chart.ctx;
                            chart.data.datasets.forEach((dataset, i) => {
                                const meta = chart.getDatasetMeta(i);
                                meta.data.forEach((element, index) => {
                                    const data = dataset.data[index];
                                    if (data > 0) {
                                        ctx.fillStyle = '#fff';
                                        ctx.font = 'bold 12px Arial';
                                        ctx.textAlign = 'center';
                                        ctx.textBaseline = 'middle';
                                        const position = element.tooltipPosition();
                                        ctx.fillText(data, position.x, position.y);
                                    }
                                });
                            });
                        }
                    }]
                });
            }

            // Gráfico de Situação (mantido) - cores sincronizadas com a coluna Situação do grid
            if (dados.contagemPorSituacao && dados.contagemPorSituacao.length > 0) {
                const ctxSituacao = document.getElementById('chartSituacao').getContext('2d');
                new Chart(ctxSituacao, {
                    type: 'pie',
                    data: {
                        labels: dados.contagemPorSituacao.map(s => s.situacao),
                        datasets: [{
                            data: dados.contagemPorSituacao.map(s => s.total),
                            backgroundColor: dados.contagemPorSituacao.map(s => corParaSituacao(s.situacao)),
                            borderWidth: 2,
                            borderColor: '#fff'
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'bottom',
                                labels: {
                                    boxWidth: 12,
                                    font: { size: 10 }
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const label = context.label || '';
                                        const value = context.parsed || 0;
                                        const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                        const percentage = ((value / total) * 100).toFixed(1);
                                        return label + ': ' + value + ' (' + percentage + '%)';
                                    }
                                }
                            }
                        }
                    },
                    plugins: [{
                        afterDatasetsDraw: function(chart) {
                            const ctx = chart.ctx;
                            chart.data.datasets.forEach((dataset, i) => {
                                const meta = chart.getDatasetMeta(i);
                                meta.data.forEach((element, index) => {
                                    const data = dataset.data[index];
                                    if (data > 0) {
                                        ctx.fillStyle = '#fff';
                                        ctx.font = 'bold 12px Arial';
                                        ctx.textAlign = 'center';
                                        ctx.textBaseline = 'middle';
                                        const position = element.tooltipPosition();
                                        ctx.fillText(data, position.x, position.y);
                                    }
                                });
                            });
                        }
                    }]
                });
            }
        }

        // Função para exportar para Excel
        function exportarParaExcel() {
            if (!dadosRelatorio || !dadosRelatorio.irregularidadesDetalhadas) {
                mostrarMensagem('Nenhum dado disponível para exportação.', 'error');
                return;
            }

            try {
                const irregularidades = dadosRelatorio.irregularidadesDetalhadas;
                
                // Preparar dados para o Excel
                const dadosExcel = irregularidades.map(item => ({
                    'Data': item.data || '',
                    'Tipo': item.tipo || '',
                    'Equipamento': item.equipamento || '',
                    'Placa Cadastrada': item.placaCad || '',
                    'Placa Capturada': item.placaCap || '',
                    'Leitura': item.leitura || '',
                    'Início': item.inicio || '',
                    'Fim': item.fim || '',
                    'Situação': item.situacao || ''
                }));

                // Criar workbook e worksheet
                const wb = XLSX.utils.book_new();
                const ws = XLSX.utils.json_to_sheet(dadosExcel);

                // Ajustar largura das colunas
                const colWidths = [
                    { wch: 12 }, // Data
                    { wch: 30 }, // Tipo
                    { wch: 25 }, // Equipamento
                    { wch: 12 }, // Placa Cad
                    { wch: 12 }, // Placa Cap
                    { wch: 15 }, // Leitura
                    { wch: 20 }, // Início
                    { wch: 10 }, // Fim
                    { wch: 15 }  // Situação
                ];
                ws['!cols'] = colWidths;

                // Adicionar worksheet ao workbook
                XLSX.utils.book_append_sheet(wb, ws, 'Alertas');

                // Gerar nome do arquivo com data
                const params = dadosRelatorio.parametrosConsulta;
                const nomeArquivo = 'Relatorio_Alertas_' + 
                                    params.dataInicioSQL.replace(/-/g, '') + '_' + 
                                    params.dataFinalSQL.replace(/-/g, '') + '.xlsx';

                // Fazer download
                XLSX.writeFile(wb, nomeArquivo);

                mostrarMensagem('Arquivo Excel gerado com sucesso!', 'success');
            } catch (e) {
                console.error('Erro ao exportar para Excel:', e);
                mostrarMensagem('Erro ao gerar arquivo Excel.', 'error');
            }
        }

        // Função para imprimir dashboard
        function imprimirDashboard() {
            window.print();
        }

        // Definir data padrão (últimos 7 dias)
        window.onload = function () {
            const hoje = new Date();
            const seteDiasAtras = new Date();
            seteDiasAtras.setDate(hoje.getDate() - 7);
            
            document.getElementById('txt_data_inicio').valueAsDate = seteDiasAtras;
            document.getElementById('txt_data_final').valueAsDate = hoje;
        };
    </script>
</body>

</html>
