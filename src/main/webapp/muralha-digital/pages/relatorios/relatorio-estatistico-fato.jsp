S<%@ page language="java" pageEncoding="utf-8" %>
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

        <%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

            <html lang="pt-BR">

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <title>GTW - Relatório Estatístico de Fato</title>
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
                        div:empty:not(canvas):not(#chartSemanal):not(#chartDiario):not(#chartHora) {
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
                                    <i class="bi bi-bar-chart-fill"></i> Relatório Estatístico de Fato
                                </h2>
                                <p class="text-center text-muted">Estatísticas de ocorrências por tipo de fato registrado</p>
                            </div>
                        </div>

                        <!-- Formulário de Parâmetros -->
                        <div class="form-container">
                            <form id="frm_filtro_relatorio">
                                <div class="row">
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
                                    <div class="col-md-4 mb-3">
                                        <label for="sel_tipo_fato" class="form-label fw-bold">
                                            <i class="bi bi-tag"></i> Tipo de Fato
                                        </label>
                                        <select class="form-select" id="sel_tipo_fato" name="tipoFatoId">
                                            <option value="">Carregando tipos de fato...</option>
                                        </select>
                                        <div class="form-text">Selecione um tipo específico (opcional)</div>
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
                        <h2 class="print-title">Relatório Estatístico por Fato</h2>
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

                        // Validar se um tipo de fato foi selecionado
                        const tipoFatoId = document.getElementById('sel_tipo_fato').value;
                        if (!tipoFatoId || tipoFatoId.trim() === '') {
                            mostrarMensagem('Por favor, selecione um tipo de fato.', 'error');
                            return;
                        }

                        // Mostrar loading
                        document.getElementById('btn_gerar').disabled = true;
                        document.getElementById('loading_msg').style.display = 'block';

                        const dataInicio = document.getElementById('txt_data_inicio').value;
                        const dataFim = document.getElementById('txt_data_fim').value;

                        // Fazer requisição AJAX para o servlet
                        const xhr = new XMLHttpRequest();
                        xhr.open('POST', '/relatorio/RelatorioEstatisticoFatoServlet', true);
                        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

                        xhr.onreadystatechange = function () {
                            if (xhr.readyState === 4) {
                                // Esconder loading
                                document.getElementById('btn_gerar').disabled = false;
                                document.getElementById('loading_msg').style.display = 'none';

                                if (xhr.status === 200) {
                                    try {
                                        dadosEstatistica = JSON.parse(xhr.responseText);

                                        // DEBUG: Logs para diagnóstico
                                        console.log('DEBUG: Dados recebidos:', dadosEstatistica);
                                        console.log('DEBUG: ocorrencias_por_tipo existe?', !!dadosEstatistica.ocorrencias_por_tipo);
                                        console.log('DEBUG: estatisticas_por_tipo existe?', !!dadosEstatistica.estatisticas_por_tipo);
                                        console.log('DEBUG: histograma_semanal:', dadosEstatistica.histograma_semanal);
                                        console.log('DEBUG: histograma_diario:', dadosEstatistica.histograma_diario);
                                        console.log('DEBUG: histograma_por_hora:', dadosEstatistica.histograma_por_hora);
                                        console.log('DEBUG: tipo_eventos:', dadosEstatistica.tipo_eventos);

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
                                        console.log('DEBUG: Resposta raw:', xhr.responseText);
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

                        let params = 'dataInicio=' + encodeURIComponent(dataInicio) + '&dataFim=' + encodeURIComponent(dataFim);
                        if (tipoFatoId) {
                            params += '&tipoFatoId=' + encodeURIComponent(tipoFatoId);
                        }
                        xhr.send(params);
                    }

                    // Função para criar o dashboard
                    function criarDashboard(dados) {
                        console.log('DEBUG: criarDashboard chamada com dados:', dados);

                        const container = document.getElementById('dashboard-container');

                        // Verificar se os dados existem e têm a estrutura esperada
                        // Suporte para ambos os formatos: novo (ocorrencias_por_tipo) e antigo (estatisticas_por_tipo)
                        const ocorrenciasPorTipo = dados.ocorrencias_por_tipo || dados.estatisticas_por_tipo;
                        const tiposEventos = dados.tipo_eventos || dados.tipo_ocorrencia;

                        console.log('DEBUG: ocorrenciasPorTipo:', ocorrenciasPorTipo);
                        console.log('DEBUG: tiposEventos:', tiposEventos);

                        if (!dados || !ocorrenciasPorTipo) {
                            console.log('DEBUG: Dados inválidos ou ausentes');
                            container.innerHTML = '<div class="alert alert-danger text-center">Erro: Dados não encontrados ou inválidos</div>';
                            return;
                        }

                        // Calcular estatísticas gerais
                        const totalOcorrencias = ocorrenciasPorTipo.reduce((sum, item) => sum + (item.total_ocorrencias || 0), 0);
                        const tiposComOcorrencias = ocorrenciasPorTipo.length;

                        // Criar HTML usando Bootstrap
                        let html = '';

                        // Cards de Estatísticas com Bootstrap
                        html += '<div class="row mb-4">';
                        html += '<div class="col-md-3">';
                        html += '<div class="stat-card stat-card-blue">';
                        html += '<i class="bi bi-exclamation-triangle stat-icon"></i>';
                        html += '<div class="stat-value">' + totalOcorrencias + '</div>';
                        html += '<div class="stat-title">Total de Ocorrências</div>';
                        html += '</div></div>';

                        html += '<div class="col-md-3">';
                        html += '<div class="stat-card stat-card-green">';
                        html += '<i class="bi bi-list-ul stat-icon"></i>';
                        html += '<div class="stat-value">' + tiposComOcorrencias + '</div>';
                        html += '<div class="stat-title">Tipos de Fatos</div>';
                        html += '</div></div>';

                        html += '<div class="col-md-3">';
                        html += '<div class="stat-card stat-card-yellow">';
                        html += '<i class="bi bi-calendar-week stat-icon"></i>';
                        html += '<div class="stat-value">' + (dados.histograma_semanal ? dados.histograma_semanal.length : 0) + '</div>';
                        html += '<div class="stat-title">Semanas com Dados</div>';
                        html += '</div></div>';

                        html += '<div class="col-md-3">';
                        html += '<div class="stat-card stat-card-cyan">';
                        html += '<i class="bi bi-graph-up stat-icon"></i>';
                        html += '<div class="stat-value">' + (totalOcorrencias / Math.max(tiposComOcorrencias, 1)).toFixed(1) + '</div>';
                        html += '<div class="stat-title">Média por Tipo</div>';
                        html += '</div></div>';
                        html += '</div>';


                        // Gráficos com Bootstrap - Layout otimizado em 3 colunas
                        html += '<div class="row mb-4">';
                        html += '<div class="col-md-4">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Distribuição Semanal</div>';
                        html += '<div class="p-3"><canvas id="chartSemanal" style="height: 300px;"></canvas></div>';
                        html += '</div></div>';

                        html += '<div class="col-md-4">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Distribuição Diária</div>';
                        html += '<div class="p-3"><canvas id="chartDiario" style="height: 300px;"></canvas></div>';
                        html += '</div></div>';

                        html += '<div class="col-md-4">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Distribuição por Hora</div>';
                        html += '<div class="p-3"><canvas id="chartHora" style="height: 300px;"></canvas></div>';
                        html += '</div></div>';
                        html += '</div>';

                        // Cards com dados dos histogramas
                        html += '<div class="row mb-4">';
                        html += '<div class="col-md-4">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Dados Semanais</div>';
                        html += '<div class="p-3">';
                        html += '<table class="table table-hover table-sm">';
                        html += '<thead><tr><th>Semana</th><th>Ocorrências</th></tr></thead>';
                        html += '<tbody id="tabelaSemanal"></tbody>';
                        html += '</table></div></div></div>';

                        html += '<div class="col-md-4">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Dados Diários</div>';
                        html += '<div class="p-3">';
                        html += '<table class="table table-hover table-sm">';
                        html += '<thead><tr><th>Dia</th><th>Ocorrências</th></tr></thead>';
                        html += '<tbody id="tabelaDiaria"></tbody>';
                        html += '</table></div></div></div>';

                        html += '<div class="col-md-4">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Dados por Hora</div>';
                        html += '<div class="p-3">';
                        html += '<table class="table table-hover table-sm">';
                        html += '<thead><tr><th>Hora</th><th>Ocorrências</th></tr></thead>';
                        html += '<tbody id="tabelaHoraria"></tbody>';
                        html += '</table></div></div></div>';
                        html += '</div>';

                        container.innerHTML = html;

                        // Criar gráficos
                        setTimeout(() => {
                            criarGraficos(dados);
                            preencherTabelasHistogramas(dados);
                        }, 500);
                    }

                    // Função para criar gráficos
                    function criarGraficos(dados) {
                        // Definir variáveis compatíveis com ambos os formatos
                        const ocorrenciasPorTipo = dados.ocorrencias_por_tipo || dados.estatisticas_por_tipo;
                        const tiposEventos = dados.tipo_eventos || dados.tipo_ocorrencia;

                        // Cores vibrantes
                        const cores = ['#1e40af', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#84cc16', '#f97316'];

                        // Gráfico semanal
                        if (dados.histograma_semanal && dados.histograma_semanal.length > 0) {
                            const chartSemanalElement = document.getElementById('chartSemanal');
                            if (!chartSemanalElement) {
                                console.error('ERRO: Elemento chartSemanal não encontrado!');
                            } else {
                            const ctxSemanal = chartSemanalElement.getContext('2d');
                            new Chart(ctxSemanal, {
                                type: 'bar',
                                data: {
                                    labels: dados.histograma_semanal.map(s => 'Semana ' + s.semana),
                                    datasets: [{
                                        label: 'Ocorrências',
                                        data: dados.histograma_semanal.map(s => s.total_ocorrencias),
                                        backgroundColor: '#1e40af',
                                        borderColor: '#1e40af',
                                        borderWidth: 1
                                    }]
                                },
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    plugins: { legend: { display: false } },
                                    scales: { y: { beginAtZero: true } }
                                }
                            });
                            }
                        }

                        // Gráfico diário
                        if (dados.histograma_diario && dados.histograma_diario.length > 0) {
                            const chartDiarioElement = document.getElementById('chartDiario');
                            if (!chartDiarioElement) {
                                console.error('ERRO: Elemento chartDiario não encontrado!');
                            } else {
                            const diasSemana = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
                            const ctxDiario = chartDiarioElement.getContext('2d');
                            new Chart(ctxDiario, {
                                type: 'doughnut',
                                data: {
                                    labels: dados.histograma_diario.map(d => diasSemana[d.dia_semana] || 'N/A'),
                                    datasets: [{
                                        data: dados.histograma_diario.map(d => d.total_ocorrencias),
                                        backgroundColor: cores.slice(0, dados.histograma_diario.length),
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
                                            labels: { fontSize: 12, boxWidth: 15 }
                                        }
                                    }
                                }
                            });
                            }
                        }


                        // Gráfico por hora
                        if (dados.histograma_por_hora && dados.histograma_por_hora.length > 0) {
                            const chartHoraElement = document.getElementById('chartHora');
                            if (!chartHoraElement) {
                                console.error('ERRO: Elemento chartHora não encontrado!');
                            } else {
                            const ctxHora = chartHoraElement.getContext('2d');
                            new Chart(ctxHora, {
                                type: 'line',
                                data: {
                                    labels: dados.histograma_por_hora.map(h => h.hora + ':00'),
                                    datasets: [{
                                        label: 'Ocorrências',
                                        data: dados.histograma_por_hora.map(h => h.total_ocorrencias),
                                        backgroundColor: 'rgba(245, 158, 11, 0.2)',
                                        borderColor: '#f59e0b',
                                        borderWidth: 3,
                                        fill: true,
                                        tension: 0.4
                                    }]
                                },
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    plugins: { legend: { display: false } },
                                    scales: { y: { beginAtZero: true } }
                                }
                            });
                            }
                        }
                    }

                    // Função para preencher tabelas dos histogramas
                    function preencherTabelasHistogramas(dados) {
                        // Tabela Semanal
                        const tabelaSemanal = document.getElementById('tabelaSemanal');
                        
                        if (!tabelaSemanal) {
                            console.error('ERRO: Elemento tabelaSemanal não encontrado!');
                        } else {
                            let htmlSemanal = '';
                            if (dados.histograma_semanal && dados.histograma_semanal.length > 0) {
                                dados.histograma_semanal.forEach(item => {
                                    htmlSemanal += '<tr>';
                                    htmlSemanal += '<td><strong>Semana ' + (item.semana || 'N/A') + '</strong></td>';
                                    htmlSemanal += '<td><span class="badge bg-primary">' + (item.total_ocorrencias || 0) + '</span></td>';
                                    htmlSemanal += '</tr>';
                                });
                            } else {
                                htmlSemanal = '<tr><td colspan="2" class="text-muted text-center">Nenhum dado encontrado</td></tr>';
                            }
                            tabelaSemanal.innerHTML = htmlSemanal;
                        }

                        // Tabela Diária
                        const tabelaDiaria = document.getElementById('tabelaDiaria');
                        
                        if (!tabelaDiaria) {
                            console.error('ERRO: Elemento tabelaDiaria não encontrado!');
                        } else {
                            const diasSemana = ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'];
                            let htmlDiaria = '';
                            if (dados.histograma_diario && dados.histograma_diario.length > 0) {
                                dados.histograma_diario.forEach(item => {
                                    const nomeDia = diasSemana[item.dia_semana] || 'N/A';
                                    htmlDiaria += '<tr>';
                                    htmlDiaria += '<td><strong>' + nomeDia + '</strong></td>';
                                    htmlDiaria += '<td><span class="badge bg-success">' + (item.total_ocorrencias || 0) + '</span></td>';
                                    htmlDiaria += '</tr>';
                                });
                            } else {
                                htmlDiaria = '<tr><td colspan="2" class="text-muted text-center">Nenhum dado encontrado</td></tr>';
                            }
                            tabelaDiaria.innerHTML = htmlDiaria;
                        }

                        // Tabela Horária
                        const tabelaHoraria = document.getElementById('tabelaHoraria');
                        
                        if (!tabelaHoraria) {
                            console.error('ERRO: Elemento tabelaHoraria não encontrado!');
                        } else {
                            let htmlHoraria = '';
                            if (dados.histograma_por_hora && dados.histograma_por_hora.length > 0) {
                                dados.histograma_por_hora.forEach(item => {
                                    htmlHoraria += '<tr>';
                                    htmlHoraria += '<td><strong>' + (item.hora || 0) + ':00</strong></td>';
                                    htmlHoraria += '<td><span class="badge bg-warning text-dark">' + (item.total_ocorrencias || 0) + '</span></td>';
                                    htmlHoraria += '</tr>';
                                });
                            } else {
                                htmlHoraria = '<tr><td colspan="2" class="text-muted text-center">Nenhum dado encontrado</td></tr>';
                            }
                            tabelaHoraria.innerHTML = htmlHoraria;
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
                            const diasSemana = ['Domingo', 'Segunda-feira', 'Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sábado'];
                            
                            // Resumo
                            const resumoData = [
                                ['Relatório Estatístico de Fato'],
                                [''],
                                ['Período', document.getElementById('txt_data_inicio').value + ' a ' + document.getElementById('txt_data_fim').value],
                                ['Tipo de Fato', document.getElementById('sel_tipo_fato').options[document.getElementById('sel_tipo_fato').selectedIndex].text],
                                [''],
                                ['Total de Fatos', dadosEstatistica.total_fatos || 0],
                                ['Total de Locais', dadosEstatistica.total_locais || 0],
                                ['Média Diária', dadosEstatistica.media_diaria || 0]
                            ];
                            XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(resumoData), 'Resumo');
                            
                            // Histograma Semanal
                            if (dadosEstatistica.histograma_semanal && dadosEstatistica.histograma_semanal.length > 0) {
                                const semanalData = [['Semana', 'Ano', 'Ocorrências']];
                                dadosEstatistica.histograma_semanal.forEach(item => {
                                    semanalData.push(['Semana ' + item.semana, item.ano, item.total_ocorrencias]);
                                });
                                XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(semanalData), 'Por Semana');
                            }
                            
                            // Histograma Diário
                            if (dadosEstatistica.histograma_diario && dadosEstatistica.histograma_diario.length > 0) {
                                const diarioData = [['Dia da Semana', 'Ocorrências']];
                                dadosEstatistica.histograma_diario.forEach(item => {
                                    diarioData.push([diasSemana[item.dia_semana] || 'N/A', item.total_ocorrencias]);
                                });
                                XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(diarioData), 'Por Dia');
                            }
                            
                            // Histograma Por Hora
                            if (dadosEstatistica.histograma_por_hora && dadosEstatistica.histograma_por_hora.length > 0) {
                                const horaData = [['Hora', 'Ocorrências']];
                                dadosEstatistica.histograma_por_hora.forEach(item => {
                                    horaData.push([item.hora + ':00', item.total_ocorrencias]);
                                });
                                XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(horaData), 'Por Hora');
                            }
                            
                            const nomeArquivo = 'Relatorio_Estatistico_Fato_' + new Date().toISOString().split('T')[0] + '.xlsx';
                            XLSX.writeFile(wb, nomeArquivo);
                            
                        } catch (e) {
                            console.error('Erro ao exportar para Excel:', e);
                            alert('Erro ao exportar para Excel: ' + e.message);
                        }
                    }

                    // Inicialização quando a página carrega
                    window.onload = function() {
                        // Definir datas padrão (últimos 30 dias)
                        const hoje = new Date();
                        const trintaDiasAtras = new Date();
                        trintaDiasAtras.setDate(hoje.getDate() - 30);

                        document.getElementById('txt_data_fim').value = hoje.toISOString().split('T')[0];
                        document.getElementById('txt_data_inicio').value = trintaDiasAtras.toISOString().split('T')[0];

                        // Carregar tipos de fato
                        carregarTiposFato();
                    };

                    function carregarTiposFato() {
                        const xhr = new XMLHttpRequest();
                        xhr.open('POST', '/relatorio/RelatorioEstatisticoFatoServlet', true);
                        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

                        xhr.onreadystatechange = function () {
                            if (xhr.readyState === 4) {
                                if (xhr.status === 200) {
                                    try {
                                        const tiposFato = JSON.parse(xhr.responseText);
                                        preencherSelectTiposFato(tiposFato);
                                    } catch (e) {
                                        console.error('Erro ao processar tipos de fato:', e);
                                        document.getElementById('sel_tipo_fato').innerHTML = '<option value="">Erro ao carregar tipos de fato</option>';
                                    }
                                } else {
                                    console.error('Erro ao buscar tipos de fato:', xhr.status);
                                    document.getElementById('sel_tipo_fato').innerHTML = '<option value="">Erro ao carregar tipos de fato</option>';
                                }
                            }
                        };

                        xhr.send('acao=buscarTiposFato');
                    }

                    function preencherSelectTiposFato(tipos) {
                        const select = document.getElementById('sel_tipo_fato');
                        select.innerHTML = '<option value="">Selecione um tipo de fato</option>';
                        
                        tipos.forEach(function(tipo) {
                            const option = document.createElement('option');
                            option.value = tipo.id;
                            option.textContent = tipo.tipo_desc;
                            select.appendChild(option);
                        });
                    }
                </script>
            </body>
            </html>