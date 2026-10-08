<%@ page language="java" pageEncoding="utf-8" %>
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

        <%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

            <html lang="pt-BR">

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <title>GTW - Relatório Estatístico por Tipo de Fatos com Mapa</title>
                <meta name="viewport" content="width=device-width, initial-scale=1">

                <!-- Chart.js -->
                <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
                <!-- DataTables -->
                <link rel="stylesheet" href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css">
                <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
                <script src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
                <script src="https://cdn.datatables.net/1.13.4/js/dataTables.bootstrap5.min.js"></script>
                <!-- SheetJS para exportação Excel -->
                <script src="https://cdn.sheetjs.com/xlsx-0.20.1/package/dist/xlsx.full.min.js"></script>
                <!-- Bootstrap Icons -->
                <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css"
                    rel="stylesheet">
                <!-- Font Awesome -->
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
                <!-- Google Maps API -->
                <script
                    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk&libraries=geometry,visualization"></script>
                <!-- Maps Config -->
                <script src="/muralha-digital/utils/maps-config.js"></script>

                <style>
                    /* Dashboard customizado com Bootstrap */
                    .dashboard-container {
                        background: #f8f9fa;
                        min-height: 100vh;
                        padding: 20px;
                        margin-left: 0;
                        margin-right: 0;
                    }

                    .stat-card {
                        border-radius: 10px;
                        padding: 20px 15px;
                        color: white;
                        position: relative;
                        margin-bottom: 20px;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                        overflow: hidden;
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

                    .stat-card-cyan {
                        background: linear-gradient(135deg, #0891b2, #06b6d4);
                    }

                    .stat-icon {
                        position: absolute;
                        top: 15px;
                        right: 15px;
                        font-size: 24px;
                        opacity: 0.8;
                    }

                    .stat-value {
                        font-size: 2rem;
                        font-weight: bold;
                        margin-bottom: 5px;
                        word-break: break-word;
                    }

                    .stat-title {
                        font-size: 0.85rem;
                        opacity: 0.9;
                        margin-bottom: 10px;
                        line-height: 1.2;
                    }

                    /* Garantir que os cards não cortem na margem */
                    .row {
                        margin-left: 0;
                        margin-right: 0;
                    }
                    
                    .row > [class*="col-"] {
                        padding-left: 10px;
                        padding-right: 10px;
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

                    #map-canvas {
                        height: 500px;
                        width: 100%;
                        border-radius: 8px;
                    }

                    .table-card {
                        background: white;
                        border-radius: 10px;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        margin-bottom: 20px;
                    }

                    .table-header {
                        background: #1e40af;
                        color: white;
                        padding: 15px 20px;
                        border-radius: 10px 10px 0 0;
                        font-weight: 600;
                        font-size: 1rem;
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

                        .filter-card, .btn, .no-print, button, form, #loading_msg, #frm_filtro_relatorio, #map,
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

                        .container, .container-fluid {
                            max-width: 100% !important;
                            width: 100% !important;
                            padding: 0 !important;
                            margin: 0 !important;
                        }
                        
                        .dashboard-container {
                            padding: 0 !important;
                            margin: 0 !important;
                        }

                        h2, h4 {
                            font-size: 16pt !important;
                            color: #000 !important;
                        }

                        .row {
                            display: flex !important;
                            flex-wrap: wrap !important;
                            page-break-inside: avoid;
                            margin-left: 0 !important;
                            margin-right: 0 !important;
                        }

                        .col-md-3, .col-lg-3 {
                            width: 24% !important;
                            flex: 0 0 24% !important;
                            max-width: 24% !important;
                            padding: 0 5px !important;
                        }

                        .col-md-6, .col-lg-6 {
                            width: 49% !important;
                            flex: 0 0 49% !important;
                            max-width: 49% !important;
                            padding: 0 5px !important;
                        }

                        .stat-card {
                            box-shadow: none !important;
                            border: 1.5px solid #333 !important;
                            padding: 8px 10px !important;
                            page-break-inside: avoid;
                            margin-bottom: 8px !important;
                            margin-left: 0 !important;
                            margin-right: 0 !important;
                        }

                        .stat-card-blue { background: #e3f2fd !important; border-color: #1e40af !important; }
                        .stat-card-green { background: #e8f5e9 !important; border-color: #059669 !important; }
                        .stat-card-yellow { background: #fff3e0 !important; border-color: #d97706 !important; }
                        .stat-card-purple { background: #f3e5f5 !important; border-color: #7c3aed !important; }

                        .stat-value {
                            font-size: 14pt !important;
                            color: #000 !important;
                        }

                        .stat-title {
                            font-size: 8pt !important;
                            color: #333 !important;
                        }
                        
                        .stat-icon {
                            font-size: 16px !important;
                            top: 8px !important;
                            right: 8px !important;
                        }

                        .chart-card, .table-card {
                            box-shadow: none !important;
                            border: 1px solid #999 !important;
                            page-break-inside: avoid;
                            margin-bottom: 15px !important;
                        }

                        .chart-header, .table-header {
                            background: #f5f5f5 !important;
                            color: #000 !important;
                            border-bottom: 2px solid #333 !important;
                            padding: 8px 12px !important;
                        }

                        table {
                            font-size: 8pt !important;
                            border-collapse: collapse !important;
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

                        /* DataTables - forçar exibição de todas as linhas na impressão */
                        #tabelaDetalhes tbody tr {
                            display: table-row !important;
                        }

                        /* Ocultar controles de paginação do DataTables */
                        .dataTables_wrapper .dataTables_paginate,
                        .dataTables_wrapper .dataTables_info,
                        .dataTables_wrapper .dataTables_length,
                        .dataTables_wrapper .dataTables_filter {
                            display: none !important;
                        }

                        /* Garantir que o tbody não tenha altura limitada */
                        #tabelaDetalhes tbody,
                        .dataTables_wrapper .dataTables_scrollBody,
                        .table-responsive {
                            max-height: none !important;
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
                                    <i class="bi bi-geo-alt-fill"></i> Relatório Estatístico por Tipo de Fatos com Mapa
                                </h2>
                                <p class="text-center text-muted">Visualização geográfica das ocorrências por tipo de fato</p>
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
                        <h2 class="print-title">Relatório Estatístico por Tipo de Fatos com Mapa</h2>
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
                    let mapaGoogle = null;

                    // Mapeamento de tipos de fatos para ícones
                    const tiposFatos = {
                        1: { nome: 'Furto', icone: 'fas fa-hand-paper', cor: '#ef4444', simbolo: 'F' },
                        2: { nome: 'Roubo', icone: 'fas fa-mask', cor: '#dc2626', simbolo: 'R' },
                        3: { nome: 'Perda ou Extravio', icone: 'fas fa-question-circle', cor: '#f59e0b', simbolo: '?' },
                        4: { nome: 'Dano', icone: 'fas fa-bolt', cor: '#f97316', simbolo: 'D' },
                        5: { nome: 'Estelionato', icone: 'fas fa-handshake-slash', cor: '#8b5cf6', simbolo: 'E' },
                        6: { nome: 'Ameaça', icone: 'fas fa-skull-crossbones', cor: '#ef4444', simbolo: 'A' },
                        7: { nome: 'Lesão Corporal', icone: 'fas fa-band-aid', cor: '#dc2626', simbolo: 'L' },
                        8: { nome: 'Desaparecimento de Pessoa', icone: 'fas fa-user-slash', cor: '#6b7280', simbolo: 'DP' },
                        9: { nome: 'Localização de Pessoa Desaparecida', icone: 'fas fa-user-check', cor: '#10b981', simbolo: 'LP' },
                        10: { nome: 'Violência Doméstica', icone: 'fas fa-home', cor: '#dc2626', simbolo: 'VD' },
                        11: { nome: 'Descumprimento de Medida Protetiva', icone: 'fas fa-unlock', cor: '#ef4444', simbolo: 'MP' },
                        12: { nome: 'Abandono de Incapaz', icone: 'fas fa-baby', cor: '#f59e0b', simbolo: 'AI' },
                        13: { nome: 'Acidente de Trânsito', icone: 'fas fa-car-crash', cor: '#f97316', simbolo: 'AT' },
                        14: { nome: 'Pichação', icone: 'fas fa-spray-can', cor: '#8b5cf6', simbolo: 'P' },
                        15: { nome: 'Veículo Suspeito de Sequestro Relâmpago', icone: 'fas fa-car-burst', cor: '#dc2626', simbolo: 'SR' },
                        16: { nome: 'Veículo Roubado', icone: 'fas fa-car', cor: '#ef4444', simbolo: 'VR' },
                        17: { nome: 'Veículo Monitorado', icone: 'fas fa-map-marker-alt', cor: '#3b82f6', simbolo: 'VM' },
                        18: { nome: 'Comboio de Veículos', icone: 'fas fa-truck', cor: '#6b7280', simbolo: 'CV' },
                        19: { nome: 'Transporte Clandestino', icone: 'fas fa-van-shuttle', cor: '#f59e0b', simbolo: 'TC' },
                        20: { nome: 'Veículo Suspeito de Roubo à Banco', icone: 'fas fa-university', cor: '#dc2626', simbolo: 'RB' },
                        21: { nome: 'Veículo com Atraso de Licenciamento', icone: 'fas fa-file-invoice', cor: '#f59e0b', simbolo: 'AL' },
                        22: { nome: 'Veículo Furtado', icone: 'fas fa-car', cor: '#ef4444', simbolo: 'VF' },
                        23: { nome: 'Veículo Clonado', icone: 'fas fa-clone', cor: '#8b5cf6', simbolo: 'VC' },
                        24: { nome: 'Suspeita de Atividade Criminosa', icone: 'fas fa-eye', cor: '#6b7280', simbolo: 'SAC' }
                    };

                    // Função para obter cores distintas para cada tipo
                    function obterCorTipo(idTipo) {
                        const coresTipos = {
                            1: '#e74c3c', 2: '#8e44ad', 4: '#f39c12', 5: '#e67e22', 3: '#2ecc71',
                            6: '#c0392b', 7: '#d32f2f', 10: '#ad1457', 11: '#7b1fa2', 12: '#4a148c',
                            13: '#ff9800', 16: '#3498db', 17: '#2196f3', 18: '#03a9f4', 19: '#00bcd4',
                            20: '#009688', 21: '#4caf50', 22: '#8bc34a', 23: '#cddc39', 15: '#ffeb3b',
                            8: '#795548', 9: '#607d8b', 14: '#9c27b0', 24: '#e91e63'
                        };
                        return coresTipos[idTipo] || '#6b7280';
                    }

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

                        setTimeout(() => {
                            divMensagem.innerHTML = '';
                        }, 5000);
                    }

                    // Função para gerar estatística
                    function gerarEstatistica() {
                        if (!validarDatas()) {
                            return;
                        }

                        document.getElementById('btn_gerar').disabled = true;
                        document.getElementById('loading_msg').style.display = 'block';

                        const dataInicio = document.getElementById('txt_data_inicio').value;
                        const dataFim = document.getElementById('txt_data_fim').value;

                        const xhr = new XMLHttpRequest();
                        xhr.open('POST', '/relatorio/RelatorioEstatisticoTipoFatoMapa', true);
                        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

                        xhr.onreadystatechange = function () {
                            if (xhr.readyState === 4) {
                                document.getElementById('btn_gerar').disabled = false;
                                document.getElementById('loading_msg').style.display = 'none';

                                if (xhr.status === 200) {
                                    try {
                                        dadosEstatistica = JSON.parse(xhr.responseText);

                                        console.log('Dados recebidos:', dadosEstatistica);

                                        if (dadosEstatistica.erro) {
                                            mostrarMensagem('Erro: ' + dadosEstatistica.erro, 'error');
                                            return;
                                        }

                                        criarDashboard(dadosEstatistica);

                                        document.getElementById('dashboard-section').style.display = 'block';

                                        document.getElementById('dashboard-section').scrollIntoView({
                                            behavior: 'smooth'
                                        });

                                        mostrarMensagem('Estatística gerada com sucesso!', 'success');
                                    } catch (e) {
                                        console.error('Erro ao processar resposta:', e);
                                        console.log('Resposta raw:', xhr.responseText);
                                        mostrarMensagem('Erro ao processar os dados da estatística.', 'error');
                                    }
                                } else {
                                    mostrarMensagem('Erro ao gerar estatística. Código: ' + xhr.status, 'error');
                                }
                            }
                        };

                        xhr.onerror = function () {
                            document.getElementById('btn_gerar').disabled = false;
                            document.getElementById('loading_msg').style.display = 'none';
                            mostrarMensagem('Erro de conexão ao gerar estatística.', 'error');
                        };

                        const params = 'dataInicio=' + encodeURIComponent(dataInicio) + '&dataFim=' + encodeURIComponent(dataFim);
                        xhr.send(params);
                    }

                    // Função para criar o dashboard
                    function criarDashboard(dados) {
                        console.log('criarDashboard chamada com dados:', dados);

                        const container = document.getElementById('dashboard-container');

                        const ocorrenciasPorTipo = dados.ocorrencias_por_tipo || [];
                        const detalhesOcorrencias = dados.detalhes_ocorrencias || [];

                        if (!dados || !ocorrenciasPorTipo) {
                            console.log('Dados inválidos ou ausentes');
                            container.innerHTML = '<div class="alert alert-danger text-center">Erro: Dados não encontrados ou inválidos</div>';
                            return;
                        }

                        const totalOcorrencias = ocorrenciasPorTipo.reduce((sum, item) => sum + (item.total_ocorrencias || 0), 0);
                        const tiposComOcorrencias = ocorrenciasPorTipo.length;

                        let html = '';

                        // Cards de Estatísticas
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

                        // Segunda linha de cards com totais dos histogramas
                        const totalSemanas = dados.histograma_semanal ? dados.histograma_semanal.length : 0;
                        const totalDias = dados.histograma_diario ? dados.histograma_diario.length : 0;
                        const totalHoras = dados.histograma_por_hora ? dados.histograma_por_hora.length : 0;
                        
                        // Calcular pico de ocorrências por hora
                        let picoHora = { hora: 0, total: 0 };
                        if (dados.histograma_por_hora && dados.histograma_por_hora.length > 0) {
                            picoHora = dados.histograma_por_hora.reduce((max, item) => 
                                item.total_ocorrencias > max.total ? { hora: item.hora, total: item.total_ocorrencias } : max,
                                { hora: 0, total: 0 }
                            );
                        }

                        html += '<div class="row mb-4">';
                        
                        html += '<div class="col-md-3">';
                        html += '<div class="stat-card" style="background: linear-gradient(135deg, #7c3aed, #a78bfa);">';
                        html += '<i class="bi bi-calendar-range stat-icon"></i>';
                        html += '<div class="stat-value">' + totalSemanas + '</div>';
                        html += '<div class="stat-title">Semanas com Ocorrências</div>';
                        html += '</div></div>';

                        html += '<div class="col-md-3">';
                        html += '<div class="stat-card" style="background: linear-gradient(135deg, #ec4899, #f472b6);">';
                        html += '<i class="bi bi-calendar-day stat-icon"></i>';
                        html += '<div class="stat-value">' + totalDias + '</div>';
                        html += '<div class="stat-title">Dias da Semana Ativos</div>';
                        html += '</div></div>';

                        html += '<div class="col-md-3">';
                        html += '<div class="stat-card" style="background: linear-gradient(135deg, #14b8a6, #5eead4);">';
                        html += '<i class="bi bi-clock-history stat-icon"></i>';
                        html += '<div class="stat-value">' + totalHoras + '</div>';
                        html += '<div class="stat-title">Horas com Registro</div>';
                        html += '</div></div>';

                        html += '<div class="col-md-3">';
                        html += '<div class="stat-card" style="background: linear-gradient(135deg, #f97316, #fb923c);">';
                        html += '<i class="bi bi-alarm stat-icon"></i>';
                        html += '<div class="stat-value">' + picoHora.hora + 'h</div>';
                        html += '<div class="stat-title">Pico de Ocorrências (' + picoHora.total + ')</div>';
                        html += '</div></div>';
                        
                        html += '</div>';

                        // Mapa
                        html += '<div class="row mb-4">';
                        html += '<div class="col-md-9">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Mapa de Ocorrências por Tipo</div>';
                        html += '<div class="p-3"><div id="map-canvas"></div></div>';
                        html += '</div></div>';

                        html += '<div class="col-md-3">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Legenda dos Tipos</div>';
                        html += '<div class="p-3" id="legenda-mapa" style="max-height: 500px; overflow-y: auto;"></div>';
                        html += '</div></div>';
                        html += '</div>';

                        // Grid de Detalhes de Ocorrências
                        html += '<div class="row mb-4">';
                        html += '<div class="col-12">';
                        html += '<div class="table-card">';
                        html += '<div class="table-header">Detalhes das Ocorrências</div>';
                        html += '<div class="p-3">';
                        html += '<div class="table-responsive">';
                        html += '<table class="table table-striped table-hover table-sm" id="tabelaDetalhes">';
                        html += '<thead class="table-dark">';
                        html += '<tr>';
                        html += '<th>Tipo de Evento</th>';
                        html += '<th>Rua</th>';
                        html += '<th>Número</th>';
                        html += '<th>Bairro</th>';
                        html += '</tr>';
                        html += '</thead>';
                        html += '<tbody id="corpoTabelaDetalhes">';
                        html += '</tbody>';
                        html += '</table>';
                        html += '</div></div></div></div></div>';

                        // Gráficos
                        html += '<div class="row mb-4">';
                        html += '<div class="col-md-6">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Distribuição Semanal</div>';
                        html += '<div class="p-3"><canvas id="chartSemanal" style="height: 300px;"></canvas></div>';
                        html += '</div></div>';

                        html += '<div class="col-md-6">';
                        html += '<div class="chart-card">';
                        html += '<div class="chart-header">Distribuição Diária</div>';
                        html += '<div class="p-3"><canvas id="chartDiario" style="height: 300px;"></canvas></div>';
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
                        html += '<div class="chart-header">Tipos de Fatos</div>';
                        html += '<div class="p-3"><canvas id="chartTipos" style="height: 300px;"></canvas></div>';
                        html += '</div></div>';
                        html += '</div>';

                        // Cards com dados dos histogramas (tabelas)
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

                        setTimeout(() => {
                            inicializarMapa(dados);
                            criarGraficos(dados);
                            preencherTabelaDetalhes(dados);
                            preencherTabelasHistogramas(dados);
                            criarLegendaMapa(dados);
                        }, 100);
                    }

                    // Função para inicializar o mapa
                    function inicializarMapa(dados) {
                        console.log('inicializarMapa chamada');

                        const centroMapa = { lat: -25.4284, lng: -49.2733 };
                        const zoomMapa = 12;

                        const mapOptions = {
                            zoom: zoomMapa,
                            center: centroMapa,
                            mapTypeId: google.maps.MapTypeId.ROADMAP
                        };

                        mapaGoogle = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

                        const ocorrenciasPorTipo = dados.ocorrencias_por_tipo || [];

                        let totalMarcadores = 0;

                        ocorrenciasPorTipo.forEach((tipoFato) => {
                            const tipoInfo = tiposFatos[tipoFato.id_tipo_evento] || { nome: 'Desconhecido', simbolo: '?', cor: '#6b7280' };

                            if (tipoFato.geolocalizacao && Array.isArray(tipoFato.geolocalizacao)) {
                                tipoFato.geolocalizacao.forEach((ponto) => {
                                    // Validação robusta de coordenadas
                                    if (!Number.isFinite(ponto.latitude) || !Number.isFinite(ponto.longitude) || 
                                        ponto.latitude === 0 || ponto.longitude === 0) {
                                        console.log('Ponto inválido ignorado:', ponto);
                                        return;
                                    }

                                    const corTipo = obterCorTipo(tipoFato.id_tipo_evento);

                                    const marker = new google.maps.Marker({
                                        position: { lat: ponto.latitude, lng: ponto.longitude },
                                        map: mapaGoogle,
                                        title: tipoInfo.nome + ' (ID: ' + tipoFato.id_tipo_evento + ')',
                                        icon: {
                                            path: google.maps.SymbolPath.CIRCLE,
                                            scale: 12,
                                            fillColor: corTipo,
                                            fillOpacity: 0.9,
                                            strokeColor: '#ffffff',
                                            strokeWeight: 3
                                        }
                                    });

                                    totalMarcadores++;

                                    // Montar endereço completo
                                    let endereco = '';
                                    if (ponto.rua) {
                                        endereco = ponto.rua;
                                        if (ponto.numero) {
                                            endereco += ', ' + ponto.numero;
                                        }
                                        if (ponto.bairro) {
                                            endereco += ' - ' + ponto.bairro;
                                        }
                                    } else {
                                        endereco = 'Endereço não disponível';
                                    }

                                    const infoWindow = new google.maps.InfoWindow({
                                        content: '<div style="min-width: 250px; padding: 10px;">' +
                                            '<div style="display: flex; align-items: center; margin-bottom: 12px; border-bottom: 2px solid ' + corTipo + '; padding-bottom: 8px;">' +
                                            '<i class="' + tipoInfo.icone + '" style="color: ' + corTipo + '; font-size: 24px; margin-right: 12px;"></i>' +
                                            '<div>' +
                                            '<strong style="color: ' + corTipo + '; font-size: 14px;">' + (tipoFato.nome_tipo_evento || tipoInfo.nome) + '</strong>' +
                                            '<div style="font-size: 11px; color: #888;">ID: ' + tipoFato.id_tipo_evento + '</div>' +
                                            '</div>' +
                                            '</div>' +
                                            '<div style="font-size: 12px; color: #555; line-height: 1.4;">' +
                                            '<div style="margin-bottom: 6px;"><strong style="color: ' + corTipo + ';">📍 Endereço:</strong></div>' +
                                            '<div style="margin-left: 16px; margin-bottom: 8px;">' + endereco + '</div>' +
                                            '<div><strong style="color: ' + corTipo + ';">📊 Total no Tipo:</strong> ' + tipoFato.total_ocorrencias + ' ocorrências</div>' +
                                            '</div>' +
                                            '</div>'
                                    });

                                    marker.addListener('click', function () {
                                        infoWindow.open(mapaGoogle, marker);
                                    });
                                });
                            }
                        });

                        console.log('=== DEBUG MAPA ===');
                        console.log('Total de marcadores criados: ' + totalMarcadores);
                        console.log('Total de tipos de evento: ' + ocorrenciasPorTipo.length);
                        console.log('Total de detalhes no grid: ' + (dados.detalhes_ocorrencias ? dados.detalhes_ocorrencias.length : 0));
                        console.log('==================');
                    }

                    // Função para preencher tabela de detalhes
                    function preencherTabelaDetalhes(dados) {
                        const detalhes = dados.detalhes_ocorrencias || [];

                        // Destruir DataTable existente se houver
                        if ($.fn.DataTable.isDataTable('#tabelaDetalhes')) {
                            $('#tabelaDetalhes').DataTable().destroy();
                        }

                        const tbody = document.getElementById('corpoTabelaDetalhes');
                        let html = '';

                        if (detalhes.length === 0) {
                            html = '<tr><td colspan="4" class="text-center text-muted">Nenhum detalhe encontrado</td></tr>';
                            tbody.innerHTML = html;
                        } else {
                            detalhes.forEach((item) => {
                                html += '<tr>';
                                html += '<td>' + (item.nome_tipo_evento || 'N/D') + '</td>';
                                html += '<td>' + (item.rua || 'N/D') + '</td>';
                                html += '<td>' + (item.numero !== null && item.numero !== undefined ? item.numero : 'S/N') + '</td>';
                                html += '<td>' + (item.bairro || 'N/D') + '</td>';
                                html += '</tr>';
                            });
                            tbody.innerHTML = html;

                            // Inicializar DataTables com paginação de 10
                            $('#tabelaDetalhes').DataTable({
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
                        const cores = ['#1e40af', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#84cc16', '#f97316'];

                        // Gráfico semanal
                        if (dados.histograma_semanal && dados.histograma_semanal.length > 0) {
                            const ctxSemanal = document.getElementById('chartSemanal').getContext('2d');
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

                        // Gráfico diário
                        if (dados.histograma_diario && dados.histograma_diario.length > 0) {
                            const diasSemana = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
                            const ctxDiario = document.getElementById('chartDiario').getContext('2d');
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

                        // Gráfico por hora
                        if (dados.histograma_por_hora && dados.histograma_por_hora.length > 0) {
                            const ctxHora = document.getElementById('chartHora').getContext('2d');
                            new Chart(ctxHora, {
                                type: 'line',
                                data: {
                                    labels: dados.histograma_por_hora.map(h => h.hora + 'h'),
                                    datasets: [{
                                        label: 'Ocorrências por Hora',
                                        data: dados.histograma_por_hora.map(h => h.total_ocorrencias),
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
                                    scales: { y: { beginAtZero: true } }
                                }
                            });
                        }

                        // Gráfico de tipos
                        const ocorrenciasPorTipo = dados.ocorrencias_por_tipo || [];
                        if (ocorrenciasPorTipo.length > 0) {
                            const ctxTipos = document.getElementById('chartTipos').getContext('2d');
                            new Chart(ctxTipos, {
                                type: 'bar',
                                data: {
                                    labels: ocorrenciasPorTipo.map(t => {
                                        const tipoInfo = tiposFatos[t.id_tipo_evento];
                                        return tipoInfo ? tipoInfo.nome : 'Tipo ' + t.id_tipo_evento;
                                    }),
                                    datasets: [{
                                        label: 'Quantidade',
                                        data: ocorrenciasPorTipo.map(t => t.total_ocorrencias),
                                        backgroundColor: ocorrenciasPorTipo.map(t => {
                                            const tipoInfo = tiposFatos[t.id_tipo_evento];
                                            return tipoInfo ? tipoInfo.cor : '#6b7280';
                                        }),
                                        borderColor: '#ffffff',
                                        borderWidth: 1
                                    }]
                                },
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    indexAxis: 'y',
                                    plugins: { legend: { display: false } },
                                    scales: { x: { beginAtZero: true } }
                                }
                            });
                        }
                    }

                    // Função para preencher tabelas dos histogramas
                    function preencherTabelasHistogramas(dados) {
                        // Tabela Semanal
                        const tabelaSemanal = document.getElementById('tabelaSemanal');
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

                        // Tabela Diária
                        const tabelaDiaria = document.getElementById('tabelaDiaria');
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

                        // Tabela Horária
                        const tabelaHoraria = document.getElementById('tabelaHoraria');
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

                    // Função para criar legenda do mapa
                    function criarLegendaMapa(dados) {
                        const legendaContainer = document.getElementById('legenda-mapa');
                        let htmlLegenda = '';

                        const ocorrenciasPorTipo = dados.ocorrencias_por_tipo || [];

                        ocorrenciasPorTipo.forEach(tipoData => {
                            const tipoInfo = tiposFatos[tipoData.id_tipo_evento];
                            if (tipoInfo) {
                                const corTipo = obterCorTipo(tipoData.id_tipo_evento);

                                htmlLegenda += '<div style="display: flex; align-items: center; margin-bottom: 8px; padding: 5px; border-radius: 4px; background-color: #f8f9fa;">';
                                htmlLegenda += '<div style="width: 24px; height: 24px; margin-right: 8px; border-radius: 50%; background-color: ' + corTipo + '; border: 3px solid #ffffff; box-shadow: 0 2px 4px rgba(0,0,0,0.2); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; font-size: 10px;">';
                                htmlLegenda += '●';
                                htmlLegenda += '</div>';
                                htmlLegenda += '<div style="flex: 1;">';
                                htmlLegenda += '<div style="font-size: 11px; font-weight: bold; color: #333; line-height: 1.2;">' + tipoInfo.nome + '</div>';
                                htmlLegenda += '<div style="font-size: 10px; color: #666;">' + tipoData.total_ocorrencias + ' ocorrência' + (tipoData.total_ocorrencias !== 1 ? 's' : '') + '</div>';
                                htmlLegenda += '</div>';
                                htmlLegenda += '</div>';
                            }
                        });

                        if (htmlLegenda === '') {
                            htmlLegenda = '<div class="text-muted text-center" style="font-size: 12px;">Nenhum tipo encontrado</div>';
                        }

                        legendaContainer.innerHTML = htmlLegenda;
                    }

                    // Função para imprimir dashboard
                    function imprimirDashboard() {
                        window.print();
                    }

                    // Função para exportar dados para Excel
                    function exportarParaExcel() {
                        if (!dadosEstatistica) {
                            mostrarMensagem('Nenhum dado disponível para exportação.', 'error');
                            return;
                        }

                        try {
                            // Criar workbook
                            const wb = XLSX.utils.book_new();

                            // Aba 1: Resumo Estatístico
                            const ocorrenciasPorTipo = dadosEstatistica.ocorrencias_por_tipo || [];
                            const totalOcorrencias = ocorrenciasPorTipo.reduce((sum, item) => sum + (item.total_ocorrencias || 0), 0);
                            const tiposComOcorrencias = ocorrenciasPorTipo.length;
                            
                            let picoHora = { hora: 0, total: 0 };
                            if (dadosEstatistica.histograma_por_hora && dadosEstatistica.histograma_por_hora.length > 0) {
                                picoHora = dadosEstatistica.histograma_por_hora.reduce((max, item) => 
                                    item.total_ocorrencias > max.total ? { hora: item.hora, total: item.total_ocorrencias } : max,
                                    { hora: 0, total: 0 }
                                );
                            }

                            const resumoData = [
                                ['Relatório Estatístico por Tipo de Fatos com Mapa'],
                                [''],
                                ['Período', document.getElementById('txt_data_inicio').value + ' a ' + document.getElementById('txt_data_fim').value],
                                ['Data de Geração', new Date().toLocaleString('pt-BR')],
                                [''],
                                ['RESUMO ESTATÍSTICO'],
                                ['Métrica', 'Valor'],
                                ['Total de Ocorrências', totalOcorrencias],
                                ['Tipos de Fatos', tiposComOcorrencias],
                                ['Semanas com Dados', dadosEstatistica.histograma_semanal ? dadosEstatistica.histograma_semanal.length : 0],
                                ['Média por Tipo', (totalOcorrencias / Math.max(tiposComOcorrencias, 1)).toFixed(2)],
                                ['Semanas com Ocorrências', dadosEstatistica.histograma_semanal ? dadosEstatistica.histograma_semanal.length : 0],
                                ['Dias da Semana Ativos', dadosEstatistica.histograma_diario ? dadosEstatistica.histograma_diario.length : 0],
                                ['Horas com Registro', dadosEstatistica.histograma_por_hora ? dadosEstatistica.histograma_por_hora.length : 0],
                                ['Pico de Ocorrências (Hora)', picoHora.hora + 'h (' + picoHora.total + ' ocorrências)']
                            ];
                            const wsResumo = XLSX.utils.aoa_to_sheet(resumoData);
                            wsResumo['!cols'] = [{ wch: 30 }, { wch: 40 }];
                            XLSX.utils.book_append_sheet(wb, wsResumo, 'Resumo');

                            // Aba 2: Detalhes das Ocorrências
                            const detalhesData = [['Tipo de Evento', 'Rua', 'Número', 'Bairro']];
                            const detalhes = dadosEstatistica.detalhes_ocorrencias || [];
                            detalhes.forEach(item => {
                                detalhesData.push([
                                    item.nome_tipo_evento || 'N/D',
                                    item.rua || 'N/D',
                                    item.numero !== null && item.numero !== undefined ? item.numero : 'S/N',
                                    item.bairro || 'N/D'
                                ]);
                            });
                            const wsDetalhes = XLSX.utils.aoa_to_sheet(detalhesData);
                            wsDetalhes['!cols'] = [{ wch: 35 }, { wch: 40 }, { wch: 10 }, { wch: 25 }];
                            XLSX.utils.book_append_sheet(wb, wsDetalhes, 'Detalhes Ocorrências');

                            // Aba 3: Ocorrências por Tipo
                            const tiposData = [['ID Tipo', 'Nome do Tipo', 'Total de Ocorrências']];
                            ocorrenciasPorTipo.forEach(item => {
                                const tipoInfo = tiposFatos[item.id_tipo_evento];
                                tiposData.push([
                                    item.id_tipo_evento,
                                    tipoInfo ? tipoInfo.nome : (item.nome_tipo_evento || 'Tipo ' + item.id_tipo_evento),
                                    item.total_ocorrencias || 0
                                ]);
                            });
                            const wsTipos = XLSX.utils.aoa_to_sheet(tiposData);
                            wsTipos['!cols'] = [{ wch: 10 }, { wch: 40 }, { wch: 20 }];
                            XLSX.utils.book_append_sheet(wb, wsTipos, 'Por Tipo de Fato');

                            // Aba 4: Histograma Semanal
                            const semanalData = [['Semana', 'Total de Ocorrências']];
                            if (dadosEstatistica.histograma_semanal) {
                                dadosEstatistica.histograma_semanal.forEach(item => {
                                    semanalData.push([
                                        'Semana ' + (item.semana || 'N/A'),
                                        item.total_ocorrencias || 0
                                    ]);
                                });
                            }
                            const wsSemanal = XLSX.utils.aoa_to_sheet(semanalData);
                            wsSemanal['!cols'] = [{ wch: 15 }, { wch: 20 }];
                            XLSX.utils.book_append_sheet(wb, wsSemanal, 'Histograma Semanal');

                            // Aba 5: Histograma Diário
                            const diasSemana = ['Domingo', 'Segunda-feira', 'Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sábado'];
                            const diarioData = [['Dia da Semana', 'Total de Ocorrências']];
                            if (dadosEstatistica.histograma_diario) {
                                dadosEstatistica.histograma_diario.forEach(item => {
                                    diarioData.push([
                                        diasSemana[item.dia_semana] || 'N/A',
                                        item.total_ocorrencias || 0
                                    ]);
                                });
                            }
                            const wsDiario = XLSX.utils.aoa_to_sheet(diarioData);
                            wsDiario['!cols'] = [{ wch: 20 }, { wch: 20 }];
                            XLSX.utils.book_append_sheet(wb, wsDiario, 'Histograma Diário');

                            // Aba 6: Histograma por Hora
                            const horaData = [['Hora', 'Total de Ocorrências']];
                            if (dadosEstatistica.histograma_por_hora) {
                                dadosEstatistica.histograma_por_hora.forEach(item => {
                                    horaData.push([
                                        (item.hora || 0) + ':00',
                                        item.total_ocorrencias || 0
                                    ]);
                                });
                            }
                            const wsHora = XLSX.utils.aoa_to_sheet(horaData);
                            wsHora['!cols'] = [{ wch: 10 }, { wch: 20 }];
                            XLSX.utils.book_append_sheet(wb, wsHora, 'Histograma por Hora');

                            // Gerar nome do arquivo com data
                            const dataInicio = document.getElementById('txt_data_inicio').value.replace(/-/g, '');
                            const dataFim = document.getElementById('txt_data_fim').value.replace(/-/g, '');
                            const nomeArquivo = 'RelatorioEstatistico_TipoFatos_' + dataInicio + '_' + dataFim + '.xlsx';

                            // Baixar arquivo
                            XLSX.writeFile(wb, nomeArquivo);

                            mostrarMensagem('Arquivo Excel exportado com sucesso!', 'success');
                        } catch (e) {
                            console.error('Erro ao exportar Excel:', e);
                            mostrarMensagem('Erro ao exportar arquivo Excel: ' + e.message, 'error');
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
