<%@ page language="java" pageEncoding="utf-8" %>
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

        <%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

            <html lang="pt-BR">

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <title>GTW - Relatório Estatístico de Alarmes</title>
                <meta name="viewport" content="width=device-width, initial-scale=1">

                <!-- Chart.js -->
                <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
                <!-- SheetJS para exportação Excel -->
                <script src="https://cdn.sheetjs.com/xlsx-0.20.1/package/dist/xlsx.full.min.js"></script>
                <!-- Bootstrap Icons -->
                <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css"
                    rel="stylesheet">

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

                        .filter-card, .btn, .no-print, button, form, #loading_msg, #frm_filtro_relatorio,
                        .form-container, .dataTables_length, .dataTables_filter, .dataTables_info, .dataTables_paginate {
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

                        .container {
                            max-width: 100% !important;
                            width: 100% !important;
                            padding: 0 10px !important;
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
                        table {
                            font-size: 7pt !important;
                            border-collapse: collapse !important;
                            width: 100% !important;
                            table-layout: fixed !important;
                        }

                        table th {
                            background-color: #f0f0f0 !important;
                            border: 1px solid #666 !important;
                            padding: 4px 2px !important;
                            color: #000 !important;
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
                                    <i class="bi bi-bar-chart-fill"></i> Relatório Estatístico de Alarmes/Alertas Gerados
                                </h2>
                                <p class="text-center text-muted">Estatísticas consolidadas de alarmes e alertas do sistema</p>
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
                                        <input id="txt_data_inicio" type="date" name="dataInicio"
                                            class="form-control" required>
                                    </div>
                                    <div class="col-md-4 mb-3">
                                        <label for="txt_data_fim" class="form-label fw-bold">
                                            <i class="bi bi-calendar"></i> Data Fim
                                        </label>
                                        <input id="txt_data_fim" type="date" name="dataFim" class="form-control"
                                            required>
                                    </div>
                                </div>
                                
                                <div class="row">
                                    <div class="col-12 text-center">
                                        <button type="button" class="btn btn-primary-custom"
                                            onclick="gerarEstatistica();" id="btn_gerar">
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
                        <h2 class="print-title">Relatório Estatístico de Alarmes</h2>
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
                    let dadosEstatistica = null;

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

                    // Função para gerar estatística
                    function gerarEstatistica() {
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
                        xhr.open('POST', '/muralha-digital/relatorio/RelatorioEstatisticoAlarmes', true);
                        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

                        xhr.onreadystatechange = function () {
                            if (xhr.readyState === 4) {
                                // Esconder loading
                                document.getElementById('btn_gerar').disabled = false;
                                document.getElementById('loading_msg').style.display = 'none';

                                if (xhr.status === 200) {
                                    try {
                                        dadosEstatistica = JSON.parse(xhr.responseText);
                                        criarDashboard(dadosEstatistica);

                                        // Mostrar dashboard
                                        document.getElementById('dashboard-section').style.display = 'block';

                                        // Scroll suave para o dashboard
                                        document.getElementById('dashboard-section').scrollIntoView({
                                            behavior: 'smooth'
                                        });

                                        mostrarMensagem('Estatística gerada com sucesso!', 'success');
                                    } catch (e) {
                                        console.error('Erro ao processar resposta:', e);
                                        mostrarMensagem('Erro ao processar os dados da estatística.', 'error');
                                    }
                                } else {
                                    mostrarMensagem('Erro ao gerar estatística. Código: ' + xhr.status, 'error');
                                }
                            }
                        };

                        xhr.onerror = function () {
                            // Esconder loading
                            document.getElementById('btn_gerar').disabled = false;
                            document.getElementById('loading_msg').style.display = 'none';

                            mostrarMensagem('Erro de conexão ao gerar estatística.', 'error');
                        };

                        const params = 'dataInicio=' + encodeURIComponent(dataInicio) + '&dataFim=' + encodeURIComponent(dataFim);
                        xhr.send(params);
                    }

                    // Função para criar o dashboard
                    function criarDashboard(dados) {
                        const container = document.getElementById('dashboard-container');

                        // Verificar se os dados existem e têm a estrutura esperada
                        if (!dados || !dados.estatisticasGerais) {
                            container.innerHTML = '<div class="alert alert-danger text-center">Erro: Dados não encontrados ou inválidos</div>';
                            return;
                        }

                        const stats = dados.estatisticasGerais;

                        // Criar HTML usando Bootstrap
                        let html = '';

                        // Cards de Estatísticas com Bootstrap
                        html += '<div class="row mb-4">';
                        html += '<div class="col-md-3">';
                        html += '<div class="stat-card stat-card-blue">';
                        html += '<i class="bi bi-exclamation-triangle stat-icon"></i>';
                        html += '<div class="stat-value">' + (stats.totalAlarmes || 0) + '</div>';
                        html += '<div class="stat-title">Total de Alarmes</div>';
                        html += '</div></div>';

                        html += '<div class="col-md-3">';
                        html += '<div class="stat-card stat-card-green">';
                        html += '<i class="bi bi-calendar-check stat-icon"></i>';
                        html += '<div class="stat-value">' + (stats.diasComAlarmes || 0) + '</div>';
                        html += '<div class="stat-title">Dias com Alarmes</div>';
                        html += '</div></div>';

                        html += '<div class="col-md-3">';
                        html += '<div class="stat-card stat-card-yellow">';
                        html += '<i class="bi bi-geo-alt stat-icon"></i>';
                        html += '<div class="stat-value">' + (stats.locaisComAlarmes || 0) + '</div>';
                        html += '<div class="stat-title">Locais com Alarmes</div>';
                        html += '</div></div>';

                        html += '<div class="col-md-3">';
                        html += '<div class="stat-card stat-card-cyan">';
                        html += '<i class="bi bi-graph-up stat-icon"></i>';
                        html += '<div class="stat-value">' + (stats.mediaAlarmesPorDia || 0).toFixed(1) + '</div>';
                        html += '<div class="stat-title">Média por Dia</div>';
                        html += '</div></div>';
                        html += '</div>';

                        // Gráficos com Bootstrap
                        html += '<div class="row mb-4">';
                        html += '<div class="col-md-6">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Distribuição por Período do Dia</div>';
                        html += '<div class="p-3"><canvas id="chartPeriodo" style="height: 300px;"></canvas></div>';
                        html += '</div></div>';

                        html += '<div class="col-md-6">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Distribuição por Dia da Semana</div>';
                        html += '<div class="p-3"><canvas id="chartDiaSemana" style="height: 300px;"></canvas></div>';
                        html += '</div></div>';
                        html += '</div>';

                        html += '<div class="row mb-4">';
                        html += '<div class="col-12">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Distribuição Temporal</div>';
                        html += '<div class="p-3"><canvas id="chartTemporal" style="height: 300px;"></canvas></div>';
                        html += '</div></div>';
                        html += '</div>';

                        html += '<div class="row mb-4">';
                        html += '<div class="col-md-6">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Distribuição por Hora</div>';
                        html += '<div class="p-3"><canvas id="chartHora" style="height: 300px;"></canvas></div>';
                        html += '</div></div>';

                        html += '<div class="col-md-6">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Tipos de Alarmes</div>';
                        html += '<div class="p-3"><canvas id="chartTipos" style="height: 300px;"></canvas></div>';
                        html += '</div></div>';
                        html += '</div>';

                        // Tabelas com Bootstrap
                        html += '<div class="row">';
                        html += '<div class="col-md-6">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Top 10 Locais com Mais Alarmes</div>';
                        html += '<div class="p-3">';
                        html += '<table class="table table-hover">';
                        html += '<thead><tr><th>Local</th><th>Total</th><th>Status</th></tr></thead>';
                        html += '<tbody id="tabelaLocais"></tbody>';
                        html += '</table></div></div></div>';

                        html += '<div class="col-md-6">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Estatísticas Detalhadas</div>';
                        html += '<div class="p-3">';
                        html += '<table class="table table-hover">';
                        html += '<thead><tr><th>Tipo</th><th>Qtd</th><th>%</th><th>Status</th></tr></thead>';
                        html += '<tbody id="tabelaTipos"></tbody>';
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
                        // Tabela de locais
                        const tabelaLocais = document.getElementById('tabelaLocais');
                        let htmlLocais = '';
                        if (dados.top10Locais && dados.top10Locais.length > 0) {
                            dados.top10Locais.forEach((local, index) => {
                                const nomeLocal = (local.nomeLocal || 'N/A').trim();
                                const total = local.totalAlarmes || 0;
                                let statusClass = 'status-success';
                                if (total > 100) statusClass = 'status-danger';
                                else if (total > 50) statusClass = 'status-warning';
                                else if (total > 20) statusClass = 'status-info';

                                htmlLocais += '<tr>';
                                htmlLocais += '<td>' + nomeLocal + '</td>';
                                htmlLocais += '<td><strong>' + total + '</strong></td>';
                                htmlLocais += '<td><span class="status-indicator ' + statusClass + '"></span></td>';
                                htmlLocais += '</tr>';
                            });
                        } else {
                            htmlLocais = '<tr><td colspan="3">Nenhum dado encontrado</td></tr>';
                        }
                        tabelaLocais.innerHTML = htmlLocais;

                        // Tabela de tipos
                        const tabelaTipos = document.getElementById('tabelaTipos');
                        let htmlTipos = '';
                        if (dados.tiposAlarmes && dados.tiposAlarmes.length > 0) {
                            dados.tiposAlarmes.forEach(tipo => {
                                const percentual = tipo.percentual || 0;
                                let statusClass = 'status-success';
                                if (percentual > 40) statusClass = 'status-danger';
                                else if (percentual > 25) statusClass = 'status-warning';
                                else if (percentual > 10) statusClass = 'status-info';

                                htmlTipos += '<tr>';
                                htmlTipos += '<td>' + (tipo.tipo || 'N/A') + '</td>';
                                htmlTipos += '<td><strong>' + (tipo.quantidade || 0) + '</strong></td>';
                                htmlTipos += '<td>' + percentual.toFixed(1) + '%</td>';
                                htmlTipos += '<td><span class="status-indicator ' + statusClass + '"></span></td>';
                                htmlTipos += '</tr>';
                            });
                        } else {
                            htmlTipos = '<tr><td colspan="4">Nenhum dado encontrado</td></tr>';
                        }
                        tabelaTipos.innerHTML = htmlTipos;
                    }

                    // Função para criar gráficos
                    function criarGraficos(dados) {
                        // Cores vibrantes como na imagem
                        const cores = ['#1e40af', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#84cc16', '#f97316'];

                        // Gráfico de períodos
                        const ctxPeriodo = document.getElementById('chartPeriodo').getContext('2d');
                        if (dados.distribuicaoPorPeriodo && dados.distribuicaoPorPeriodo.length > 0) {
                            new Chart(ctxPeriodo, {
                                type: 'doughnut',
                                data: {
                                    labels: dados.distribuicaoPorPeriodo.map(p => p.periodo || 'N/A'),
                                    datasets: [{
                                        data: dados.distribuicaoPorPeriodo.map(p => p.quantidade || 0),
                                        backgroundColor: cores.slice(0, dados.distribuicaoPorPeriodo.length),
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
                                                fontSize: 12,
                                                boxWidth: 15
                                            }
                                        }
                                    }
                                }
                            });
                        }

                        // Gráfico de dias da semana
                        const ctxDiaSemana = document.getElementById('chartDiaSemana').getContext('2d');
                        if (dados.distribuicaoPorDiaSemana && dados.distribuicaoPorDiaSemana.length > 0) {
                            new Chart(ctxDiaSemana, {
                                type: 'bar',
                                data: {
                                    labels: dados.distribuicaoPorDiaSemana.map(d => d.diaSemana || 'N/A'),
                                    datasets: [{
                                        label: 'Alarmes',
                                        data: dados.distribuicaoPorDiaSemana.map(d => d.quantidade || 0),
                                        backgroundColor: '#1e40af',
                                        borderColor: '#1e40af',
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

                        // Gráfico temporal
                        const ctxTemporal = document.getElementById('chartTemporal').getContext('2d');
                        if (dados.distribuicaoPorData && dados.distribuicaoPorData.length > 0) {
                            new Chart(ctxTemporal, {
                                type: 'line',
                                data: {
                                    labels: dados.distribuicaoPorData.map(d => d.data || 'N/A'),
                                    datasets: [{
                                        label: 'Alarmes por Data',
                                        data: dados.distribuicaoPorData.map(d => d.quantidade || 0),
                                        borderColor: '#10b981',
                                        backgroundColor: 'transparent',
                                        borderWidth: 3,
                                        pointBackgroundColor: '#10b981',
                                        pointBorderColor: '#ffffff',
                                        pointBorderWidth: 2,
                                        pointRadius: 5,
                                        tension: 0.1
                                    }]
                                },
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    scales: {
                                        y: {
                                            beginAtZero: true
                                        }
                                    }
                                }
                            });
                        }

                        // Gráfico de horas
                        const ctxHora = document.getElementById('chartHora').getContext('2d');
                        if (dados.distribuicaoPorHora && dados.distribuicaoPorHora.length > 0) {
                            new Chart(ctxHora, {
                                type: 'bar',
                                data: {
                                    labels: dados.distribuicaoPorHora.map(h => (h.hora || 0) + 'h'),
                                    datasets: [{
                                        label: 'Alarmes',
                                        data: dados.distribuicaoPorHora.map(h => h.quantidade || 0),
                                        backgroundColor: '#f59e0b',
                                        borderColor: '#f59e0b',
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

                        // Gráfico de tipos
                        const ctxTipos = document.getElementById('chartTipos').getContext('2d');
                        if (dados.tiposAlarmes && dados.tiposAlarmes.length > 0) {
                            new Chart(ctxTipos, {
                                type: 'bar',
                                data: {
                                    labels: dados.tiposAlarmes.map(t => t.tipo || 'N/A'),
                                    datasets: [{
                                        label: 'Quantidade',
                                        data: dados.tiposAlarmes.map(t => t.quantidade || 0),
                                        backgroundColor: cores.slice(0, dados.tiposAlarmes.length),
                                        borderColor: '#ffffff',
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
                        if (!dadosEstatistica) {
                            alert('Não há dados para exportar. Gere a estatística primeiro.');
                            return;
                        }
                        
                        try {
                            const wb = XLSX.utils.book_new();
                            
                            // Resumo
                            const resumoData = [
                                ['Relatório Estatístico de Alarmes'],
                                [''],
                                ['Período', document.getElementById('txt_data_inicio').value + ' a ' + document.getElementById('txt_data_fim').value],
                                [''],
                                ['Total de Alarmes', dadosEstatistica.totalAlarmes || 0],
                                ['Total de Locais', dadosEstatistica.totalLocais || 0],
                                ['Tipos de Alarmes', dadosEstatistica.totalTipos || 0]
                            ];
                            XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(resumoData), 'Resumo');
                            
                            // Por Local
                            if (dadosEstatistica.distribuicaoPorLocal && dadosEstatistica.distribuicaoPorLocal.length > 0) {
                                const locaisData = [['Local', 'Quantidade', 'Percentual']];
                                dadosEstatistica.distribuicaoPorLocal.forEach(item => {
                                    locaisData.push([item.local, item.quantidade, (item.percentual || 0) + '%']);
                                });
                                XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(locaisData), 'Por Local');
                            }
                            
                            // Por Tipo
                            if (dadosEstatistica.tiposAlarmes && dadosEstatistica.tiposAlarmes.length > 0) {
                                const tiposData = [['Tipo', 'Quantidade', 'Percentual']];
                                dadosEstatistica.tiposAlarmes.forEach(item => {
                                    tiposData.push([item.tipo, item.quantidade, (item.percentual || 0) + '%']);
                                });
                                XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(tiposData), 'Por Tipo');
                            }
                            
                            // Por Hora
                            if (dadosEstatistica.distribuicaoPorHora && dadosEstatistica.distribuicaoPorHora.length > 0) {
                                const horaData = [['Hora', 'Quantidade']];
                                dadosEstatistica.distribuicaoPorHora.forEach(item => {
                                    horaData.push([item.hora + ':00', item.quantidade]);
                                });
                                XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(horaData), 'Por Hora');
                            }
                            
                            const nomeArquivo = 'Relatorio_Estatistico_Alarmes_' + new Date().toISOString().split('T')[0] + '.xlsx';
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

                        document.getElementById('txt_data_fim').value = hoje.toISOString().split('T')[0];
                        document.getElementById('txt_data_inicio').value = trintaDiasAtras.toISOString().split('T')[0];
                    };
                </script>

            </body>

            </html>