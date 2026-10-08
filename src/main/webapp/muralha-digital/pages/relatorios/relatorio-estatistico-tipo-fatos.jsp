S<%@ page language="java" pageEncoding="utf-8" %>
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

        <%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

            <html lang="pt-BR">

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <title>GTW - Relatório Estatístico por Tipo de Fatos</title>
                <meta name="viewport" content="width=device-width, initial-scale=1">

                <!-- Chart.js -->
                <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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

                    .stat-card-cyan {
                        background: linear-gradient(135deg, #0891b2, #06b6d4);
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

                    .filter-card {
                        background: white;
                        border-radius: 10px;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        padding: 25px;
                        margin-bottom: 30px;
                    }

                    .filter-title {
                        color: #1e40af;
                        font-weight: 600;
                        font-size: 1.2rem;
                        margin-bottom: 20px;
                        text-align: center;
                    }

                    #map-canvas {
                        height: 400px;
                        width: 100%;
                        border-radius: 8px;
                    }

                    /* Estilo para impressão */
                    @media print {
                        /* Scroll containers */
                        .chart-card .p-3,
                        .p-3,
                        #legenda-mapa {
                            max-height: none !important;
                            overflow: visible !important;
                            padding: 10px !important;
                        }

                        .filter-card, .btn, .no-print, button, form, #loading_msg, #map-canvas {
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

                        .stat-card, .chart-card {
                            box-shadow: none !important;
                            background: white !important;
                            border: 1px solid #ddd !important;
                            page-break-inside: avoid;
                        }

                        h1, h2, h3, h4, h5 {
                            color: #000 !important;
                        }
                    }
                </style>
            </head>

            <body>
                <div class="container mt-4">
                    <!-- Filtros -->
                    <div class="row justify-content-center">
                        <div class="col-md-6">
                            <div class="filter-card">
                                <h4 class="filter-title">Relatório Estatístico por Tipo de Fatos</h4>
                                <form id="frm_filtro_relatorio">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <label for="txt_data_inicio" class="form-label">Data Início:</label>
                                            <input id="txt_data_inicio" type="date" name="dataInicio"
                                                class="form-control" required>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="txt_data_fim" class="form-label">Data Final:</label>
                                            <input id="txt_data_fim" type="date" name="dataFim" class="form-control"
                                                required>
                                        </div>
                                    </div>
                                    <div class="row mt-3">
                                        <div class="col-12 text-center">
                                            <button type="button" class="btn btn-primary-custom"
                                                onclick="gerarEstatistica();" id="btn_gerar">
                                                <i class="bi bi-bar-chart-line"></i> Gerar Relatório
                                            </button>
                                            <div id="loading_msg" class="mt-2" style="display: none;">
                                                <div class="spinner-border text-primary" role="status">
                                                    <span class="visually-hidden">Carregando...</span>
                                                </div>
                                                <p class="mt-2">Carregando dados...</p>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row mt-3">
                                        <div class="col-12">
                                            <div id="div_mensagem"></div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <!-- Dashboard -->
                    <div id="dashboard-section" style="display: none;">
                        <div class="container dashboard-container">
                            <div id="dashboard-container">
                                <!-- Conteúdo será carregado dinamicamente -->
                            </div>
                            <div class="row mt-4">
                                <div class="col-12 text-center">
                                    <button type="button" class="btn btn-outline-secondary me-2" onclick="imprimirDashboard()">
                                        <i class="bi bi-printer me-2"></i>Imprimir
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <script type="text/javascript">
                    // Variável global para armazenar os dados
                    let dadosEstatistica = null;
                    let mapaGoogle = null;

                    // Usar configurações do maps-config.js
                    // LatLngPadrao e ZOOM_PADRAO já estão definidos em maps-config.js

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

                    // Função para obter cores distintas para cada tipo (24 tipos diferentes)
                    function obterCorTipo(idTipo) {
                        const coresTipos = {
                            // Crimes contra o patrimônio
                            1: '#e74c3c',   // Furto - Vermelho vibrante
                            2: '#8e44ad',   // Roubo - Roxo
                            4: '#f39c12',   // Dano - Laranja
                            5: '#e67e22',   // Estelionato - Laranja escuro
                            3: '#2ecc71',   // Perda ou Extravio - Verde

                            // Crimes contra a pessoa
                            6: '#c0392b',   // Ameaça - Vermelho escuro
                            7: '#d32f2f',   // Lesão Corporal - Vermelho
                            10: '#ad1457',  // Violência Doméstica - Rosa escuro
                            11: '#7b1fa2', // Descumprimento de Medida Protetiva - Roxo escuro
                            12: '#4a148c', // Abandono de Incapaz - Roxo muito escuro

                            // Trânsito e veículos
                            13: '#ff9800',  // Acidente de Trânsito - Âmbar
                            16: '#3498db',  // Veículo Roubado - Azul
                            17: '#2196f3',  // Veículo Monitorado - Azul claro
                            18: '#03a9f4',  // Comboio de Veículos - Azul ciano
                            19: '#00bcd4',  // Transporte Clandestino - Ciano
                            20: '#009688',  // Veículo Suspeito de Roubo à Banco - Verde azulado
                            21: '#4caf50',  // Veículo com Atraso de Licenciamento - Verde
                            22: '#8bc34a',  // Veículo Furtado - Verde claro
                            23: '#cddc39',  // Veículo Clonado - Verde lima
                            15: '#ffeb3b',  // Veículo Suspeito de Sequestro Relâmpago - Amarelo

                            // Outros tipos
                            8: '#795548',   // Desaparecimento de Pessoa - Marrom
                            9: '#607d8b',   // Localização de Pessoa Desaparecida - Azul cinza
                            14: '#9c27b0', // Pichação - Magenta
                            24: '#e91e63', // Suspeita de Atividade Criminosa - Rosa
                        };

                        return coresTipos[idTipo] || '#6b7280'; // Cor padrão cinza
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
                        xhr.open('POST', '/relatorio/RelatorioEstatisticoTipoFatos', true);
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

                        const params = 'dataInicio=' + encodeURIComponent(dataInicio) + '&dataFim=' + encodeURIComponent(dataFim);
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
                        html += '<div class="p-3" id="legenda-mapa" style="max-height: 400px; overflow-y: auto;"></div>';
                        html += '</div></div>';
                        html += '</div>';

                        // Gráficos com Bootstrap
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

                        // Criar mapa e gráficos
                        setTimeout(() => {
                            inicializarMapa(dados);
                            criarGraficos(dados);
                            preencherTabelasHistogramas(dados);
                            criarLegendaMapa(dados);
                        }, 100);
                    }

                    // Função para inicializar o mapa
                    function inicializarMapa(dados) {
                        console.log('DEBUG: inicializarMapa chamada com dados:', dados);

                        // Usar coordenadas de Curitiba onde estão os marcadores
                        const centroMapa = { lat: -25.4284, lng: -49.2733 }; // Curitiba
                        const zoomMapa = 12;

                        const mapOptions = {
                            zoom: zoomMapa,
                            center: centroMapa,
                            mapTypeId: google.maps.MapTypeId.ROADMAP
                        };

                        mapaGoogle = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
                        console.log('DEBUG: Mapa Google criado centrado em Curitiba:', mapaGoogle);

                        // Obter dados compatíveis com ambos os formatos
                        const ocorrenciasPorTipo = dados.ocorrencias_por_tipo || dados.estatisticas_por_tipo;
                        const tiposEventos = dados.tipo_eventos || dados.tipo_ocorrencia;

                        console.log('DEBUG: ocorrenciasPorTipo na função inicializarMapa:', ocorrenciasPorTipo);
                        console.log('DEBUG: tiposEventos na função inicializarMapa:', tiposEventos);

                        let totalMarcadores = 0;

                        // Adicionar marcadores para cada tipo de fato
                        ocorrenciasPorTipo.forEach((tipoFato, indexTipo) => {
                            console.log('DEBUG: Processando tipoFato ' + indexTipo + ':', tipoFato);
                            const tipoInfo = tiposFatos[tipoFato.id_tipo_evento] || { nome: 'Desconhecido', simbolo: '?', cor: '#6b7280' };
                            console.log('DEBUG: tipoInfo encontrada:', tipoInfo);

                            tipoFato.geolocalizacao.forEach((ponto, indexPonto) => {
                                console.log('DEBUG: Processando ponto ' + indexPonto + ' do tipo ' + tipoFato.id_tipo_evento + ':', ponto);
                                console.log('DEBUG: ponto.latitude =', ponto.latitude, 'typeof =', typeof ponto.latitude);
                                console.log('DEBUG: ponto.longitude =', ponto.longitude, 'typeof =', typeof ponto.longitude);

                                if (ponto.latitude && ponto.longitude && ponto.latitude !== 0 && ponto.longitude !== 0) {
                                    console.log('DEBUG: Criando marcador em lat: ' + ponto.latitude + ', lng: ' + ponto.longitude);

                                    // Obter cor específica para este tipo
                                    const corTipo = obterCorTipo(tipoFato.id_tipo_evento);

                                    // Criar marcador simples com cor distinta
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
                                    console.log('DEBUG: Marcador criado! Total: ' + totalMarcadores + ' - Cor: ' + corTipo);

                                    // Buscar nome do tipo de evento
                                    const tipoEvento = tiposEventos ? tiposEventos.find(t => t.id === tipoFato.id_tipo_evento) : null;
                                    const nomeEvento = tipoEvento ? (tipoEvento.tipo_evento || tipoEvento.tipo_desc) : tipoInfo.nome;

                                    const infoWindow = new google.maps.InfoWindow({
                                        content: '<div style="min-width: 250px; padding: 10px;">' +
                                            '<div style="display: flex; align-items: center; margin-bottom: 12px; border-bottom: 1px solid #eee; padding-bottom: 8px;">' +
                                            '<i class="' + tipoInfo.icone + '" style="color: ' + tipoInfo.cor + '; font-size: 24px; margin-right: 12px;"></i>' +
                                            '<div>' +
                                            '<strong style="color: ' + tipoInfo.cor + '; font-size: 14px;">' + nomeEvento + '</strong>' +
                                            '<div style="font-size: 11px; color: #888;">ID: ' + tipoFato.id_tipo_evento + '</div>' +
                                            '</div>' +
                                            '</div>' +
                                            '<div style="font-size: 12px; color: #555; line-height: 1.4;">' +
                                            '<div style="margin-bottom: 6px;"><strong>📍 Localização:</strong></div>' +
                                            '<div style="margin-left: 16px;">Latitude: ' + ponto.latitude.toFixed(6) + '</div>' +
                                            '<div style="margin-left: 16px;">Longitude: ' + ponto.longitude.toFixed(6) + '</div>' +
                                            '<div style="margin-top: 8px;"><strong>📊 Total no Tipo:</strong> ' + tipoFato.total_ocorrencias + ' ocorrências</div>' +
                                            '</div>' +
                                            '</div>'
                                    });

                                    marker.addListener('click', function () {
                                        infoWindow.open(mapaGoogle, marker);
                                    });
                                } else {
                                    console.log(`DEBUG: Ponto inválido ignorado:`, ponto);
                                }
                            });
                        });

                        console.log('DEBUG: inicializarMapa finalizada. Total de marcadores criados: ' + totalMarcadores);
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
                        if (ocorrenciasPorTipo && ocorrenciasPorTipo.length > 0) {
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

                        // Obter dados compatíveis e tipos únicos
                        const ocorrenciasPorTipo = dados.ocorrencias_por_tipo || dados.estatisticas_por_tipo;
                        const tiposEventos = dados.tipo_eventos || dados.tipo_ocorrencia;
                        const tiposPresentes = ocorrenciasPorTipo.map(item => item.id_tipo_evento);

                        tiposPresentes.forEach(tipoId => {
                            const tipoInfo = tiposFatos[tipoId];
                            if (tipoInfo) {
                                const tipoData = ocorrenciasPorTipo.find(item => item.id_tipo_evento === tipoId);
                                const totalOcorrencias = tipoData ? tipoData.total_ocorrencias : 0;

                                // Usar a mesma cor dos marcadores no mapa
                                const corTipo = obterCorTipo(tipoId);

                                htmlLegenda += '<div style="display: flex; align-items: center; margin-bottom: 8px; padding: 5px; border-radius: 4px; background-color: #f8f9fa;">';
                                htmlLegenda += '<div style="width: 24px; height: 24px; margin-right: 8px; border-radius: 50%; background-color: ' + corTipo + '; border: 3px solid #ffffff; box-shadow: 0 2px 4px rgba(0,0,0,0.2); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; font-size: 10px;">';
                                htmlLegenda += '●'; // Usar símbolo de círculo para combinar com marcadores
                                htmlLegenda += '</div>';
                                htmlLegenda += '<div style="flex: 1;">';
                                htmlLegenda += '<div style="font-size: 11px; font-weight: bold; color: #333; line-height: 1.2;">' + tipoInfo.nome + '</div>';
                                htmlLegenda += '<div style="font-size: 10px; color: #666;">' + totalOcorrencias + ' ocorrência' + (totalOcorrencias !== 1 ? 's' : '') + '</div>';
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