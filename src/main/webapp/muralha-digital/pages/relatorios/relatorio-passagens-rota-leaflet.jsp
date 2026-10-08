<%@ page language="java" pageEncoding="utf-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<html lang="pt-BR">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>GTW - Relatório de Passagens por Rota</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- DataTables CSS + JS -->
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css">
    <script src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.4/js/dataTables.bootstrap5.min.js"></script>
    <!-- SheetJS para exportação Excel -->
    <script src="https://cdn.sheetjs.com/xlsx-0.20.1/package/dist/xlsx.full.min.js"></script>
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Leaflet CSS -->
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <!-- Leaflet JS -->
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>

    <style>
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
            box-shadow: 0 4px 6px rgba(0,0,0,.1);
            overflow: hidden;
        }

        .stat-card-blue   { background: linear-gradient(135deg, #1e40af, #3b82f6); }
        .stat-card-green  { background: linear-gradient(135deg, #059669, #10b981); }
        .stat-card-yellow { background: linear-gradient(135deg, #d97706, #f59e0b); }
        .stat-card-cyan   { background: linear-gradient(135deg, #0891b2, #06b6d4); }
        .stat-card-purple { background: linear-gradient(135deg, #7c3aed, #a78bfa); }
        .stat-card-pink   { background: linear-gradient(135deg, #ec4899, #f472b6); }
        .stat-card-teal   { background: linear-gradient(135deg, #14b8a6, #5eead4); }
        .stat-card-orange { background: linear-gradient(135deg, #f97316, #fb923c); }

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
            box-shadow: 0 2px 4px rgba(0,0,0,.1);
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
            box-shadow: 0 2px 4px rgba(0,0,0,.1);
            padding: 25px;
            margin-bottom: 30px;
            max-width: 700px;
            margin-left: auto;
            margin-right: auto;
        }

        .table-card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,.1);
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

        #mapa-rota {
            height: 500px;
            width: 100%;
            border-radius: 0 0 8px 8px;
        }

        /* Marcadores Leaflet personalizados */
        .marcador-camera {
            width: 36px; height: 36px;
            border-radius: 6px;
            display: flex; align-items: center; justify-content: center;
            background: #6b7280;
            color: #fff;
            font-size: 17px;
            border: 2px solid rgba(255,255,255,.85);
            box-shadow: 0 2px 6px rgba(0,0,0,.45);
            cursor: pointer;
            position: relative;
        }
        .marcador-camera.cam-cinza   { background: #6b7280; }
        .marcador-camera.cam-verde   { background: #198754; }
        .marcador-camera.cam-azul    { background: #0d6efd; }
        .marcador-camera.cam-vermelho{ background: #dc3545; }
        .marcador-camera .cam-seq {
            position: absolute;
            top: -7px; right: -7px;
            width: 16px; height: 16px;
            background: #fff;
            color: #333;
            border-radius: 50%;
            font-size: 9px; font-weight: 700;
            display: flex; align-items: center; justify-content: center;
            border: 1px solid #ccc;
            line-height: 1;
        }

        /* Pontos selecionados */
        .ponto-item {
            display: flex; align-items: center;
            padding: 6px 4px;
            border-bottom: 1px solid #f1f5f9;
            font-size: 0.82rem;
        }
        .ponto-item:last-child { border-bottom: none; }
        .ponto-badge {
            width: 22px; height: 22px; border-radius: 50%;
            display: inline-flex; align-items: center; justify-content: center;
            color: #fff; font-size: 10px; font-weight: 700; flex-shrink: 0;
            margin-right: 6px;
        }
        .badge-inicio { background: #198754; }
        .badge-meio   { background: #0d6efd; }
        .badge-fim    { background: #dc3545; }

        .print-title { display: none; }

        @media print {
            .print-title {
                display: block !important;
                text-align: center;
                font-size: 18pt; font-weight: bold; color: #000;
                margin: 0 0 20px 0; padding: 10px 0;
                border-bottom: 2px solid #333;
            }
            .form-container, .btn, nav, .no-print,
            .dataTables_length, .dataTables_filter,
            .dataTables_info, .dataTables_paginate { display: none !important; }
            #dashboard-section { display: block !important; }
            * { -webkit-print-color-adjust: exact !important; print-color-adjust: exact !important; }
            body { margin: 0; padding: 0; background: white !important; }
            .dashboard-container { padding: 0 !important; margin: 0 !important; }
            @page { size: A4 landscape; margin: 1cm; }
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
                    <i class="bi bi-geo-alt-fill"></i> Relatório de Passagens por Rota
                </h2>
                <p class="text-center text-muted">
                    Selecione o período e os pontos de passagem no mapa. O relatório apresenta
                    a contagem de passagens e a interseção de placas entre os locais selecionados.
                </p>
            </div>
        </div>

        <!-- Formulário -->
        <div class="form-container">
            <form id="frm_filtro_relatorio">
                <div class="row justify-content-center">
                    <div class="col-md-4 mb-3">
                        <label for="txt_data_inicio" class="form-label fw-bold">
                            <i class="bi bi-calendar"></i> Data Início
                        </label>
                        <input id="txt_data_inicio" type="date" name="dataIni" class="form-control" required>
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
                        <button type="button" class="btn btn-outline-secondary ms-2" onclick="limparSelecao();">
                            <i class="bi bi-arrow-counterclockwise"></i> Limpar Seleção
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

        <!-- Mapa + Pontos (sempre visível) -->
        <div class="row mb-4">
            <div class="col-md-9">
                <div class="chart-card">
                    <div class="chart-header">
                        <i class="bi bi-map me-2"></i>Mapa de Pontos de Passagem
                    </div>
                    <div id="mapa-rota"></div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="chart-card" style="height: calc(500px + 52px);">
                    <div class="chart-header">
                        <i class="bi bi-pin-map me-2"></i>Pontos Selecionados
                    </div>
                    <div class="p-3" style="max-height: 500px; overflow-y: auto;">
                        <div id="lista-pontos">
                            <p class="text-muted small text-center mt-3">
                                <i class="bi bi-hand-index-thumb me-1"></i>
                                Clique nos marcadores do mapa para selecionar os pontos de passagem.
                            </p>
                        </div>
                        <div id="contador-pontos" class="mt-2 text-end" style="display:none;">
                            <span class="badge bg-secondary" id="badge-total">0 ponto(s)</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Dashboard (exibido após gerar relatório) -->
        <div id="dashboard-section" style="display: none;">
            <h2 class="print-title">Relatório de Passagens por Rota</h2>

            <!-- Stat Cards Linha 1 -->
            <div class="row mb-2" id="cards-linha1"></div>
            <!-- Stat Cards Linha 2 -->
            <div class="row mb-4" id="cards-linha2"></div>

            <!-- Tabela 1: Contagem por Local -->
            <div class="row mb-4">
                <div class="col-12">
                    <div class="table-card">
                        <div class="table-header">
                            <i class="bi bi-table me-2"></i>Contagem por Local.
                        </div>
                        <div class="p-3">
                            <div class="table-responsive">
                                <table class="table table-striped table-hover table-sm" id="tbl-passagens-por-local">
                                    <thead class="table-dark">
                                        <tr>
                                            <th>ID Local</th>
                                            <th>Nome</th>
                                            <th>Total de Passagens</th>
                                        </tr>
                                    </thead>
                                    <tbody id="tbody-passagens-por-local"></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Tabela 2: Placas em Comum -->
            <div class="row mb-4">
                <div class="col-12">
                    <div class="table-card">
                        <div class="table-header">
                            <i class="bi bi-intersect me-2"></i>Passagem entre pares de locais.
                        </div>
                        <div class="p-3">
                            <div class="table-responsive">
                                <table class="table table-striped table-hover table-sm" id="tbl-placas-em-comum">
                                    <thead class="table-dark">
                                        <tr>
                                            <th>ID Local A</th>
                                            <th>Nome Local A</th>
                                            <th>ID Local B</th>
                                            <th>Nome Local B</th>
                                            <th title="Quantidade total de passagens coincidentes identificadas entre os dois locais do par, no período informado.">Total de Passagens Coincidentes no Par de Locais</th>
                                            <th title="Quantidade de placas distintas que aparecem em ambos os locais do par, no período informado.">Placas Distintas no Par de Locais</th>
                                        </tr>
                                    </thead>
                                    <tbody id="tbody-placas-em-comum"></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Tabela 3: Passagens Sequenciais -->
            <div class="row mb-4">
                <div class="col-12">
                    <div class="table-card">
                        <div class="table-header">
                            <i class="bi bi-arrow-right-circle me-2"></i>Passagens Sequenciais (Rota Completa).
                        </div>
                        <div class="p-3">
                            <div class="table-responsive">
                                <table class="table table-striped table-hover table-sm" id="tbl-passagens-sequenciais">
                                    <thead class="table-dark">
                                        <tr>
                                            <th title="Sequência completa de locais que compõem a rota analisada.">Rotas</th>
                                            <th title="Quantidade total de passagens registradas percorrendo a rota completa, na ordem dos locais selecionados, no período informado.">Total de Passagens</th>
                                            <th title="Quantidade de placas distintas que percorreram a rota completa, na ordem dos locais selecionados, no período informado.">Placas Distintas</th>
                                        </tr>
                                    </thead>
                                    <tbody id="tbody-passagens-sequenciais"></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Botões de ação -->
            <div class="row mt-2 mb-4">
                <div class="col-12 text-center">
                    <button type="button" class="btn btn-outline-secondary me-2" onclick="imprimirRelatorio()">
                        <i class="bi bi-printer me-2"></i>Imprimir
                    </button>
                    <button type="button" class="btn btn-outline-success" onclick="exportarExcel()">
                        <i class="bi bi-file-earmark-excel me-2"></i>Excel
                    </button>
                </div>
            </div>
        </div>

    </div><!-- /container -->
</div><!-- /dashboard-container -->

<script type="text/javascript">
    // ─── Estado Global ────────────────────────────────────────────────────────
    var mapa              = null;
    var locaisDisponiveis = [];   // [{id_local, nome, latitude, longitude}]
    var marcadores        = {};   // {id_local: L.Marker}
    var rotaSelecionada   = [];   // [{id_local, nome, lat, lon}]
    var linhaRota         = null;
    var dtPassagens       = null;
    var dtPlacas          = null;
    var dtSequencial      = null;
    var dadosUltimaConsulta = null;
    var dtLanguagePtBr = {
        decimal: ',',
        thousands: '.',
        emptyTable: 'Nenhum dado disponível na tabela',
        info: 'Mostrando _START_ até _END_ de _TOTAL_ registros',
        infoEmpty: 'Mostrando 0 até 0 de 0 registros',
        infoFiltered: '(filtrado de _MAX_ registros no total)',
        lengthMenu: 'Mostrar _MENU_ registros',
        loadingRecords: 'Carregando...',
        processing: 'Processando...',
        search: 'Buscar:',
        zeroRecords: 'Nenhum registro encontrado',
        paginate: {
            first: 'Primeiro',
            last: 'Último',
            next: 'Próximo',
            previous: 'Anterior'
        },
        aria: {
            sortAscending: ': ativar para ordenar a coluna de forma ascendente',
            sortDescending: ': ativar para ordenar a coluna de forma descendente'
        }
    };

    // ─── Inicialização ────────────────────────────────────────────────────────
    document.addEventListener('DOMContentLoaded', function () {
        inicializarMapa();
        carregarLocais();
    });

    function inicializarMapa() {
        mapa = L.map('mapa-rota').setView([-25.4284, -49.2733], 12);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors',
            maxZoom: 19
        }).addTo(mapa);
    }

    function carregarLocais() {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', '/relatorio/ContagemPassagensPorLocal', true);
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                try {
                    var data = JSON.parse(xhr.responseText);
                    if (data.erro) {
                        mostrarMensagem('Erro ao carregar locais: ' + data.erro, 'error');
                        return;
                    }
                    locaisDisponiveis = data.locais || [];
                    adicionarMarcadores();
                } catch (e) {
                    mostrarMensagem('Erro ao processar locais do mapa.', 'error');
                }
            }
        };
        xhr.send();
    }

    // ─── Mapa ─────────────────────────────────────────────────────────────────
    function adicionarMarcadores() {
        locaisDisponiveis.forEach(function (local) {
            var icone = criarIcone(-1, 0);
            var marker = L.marker([local.latitude, local.longitude], { icon: icone });
            marker.bindTooltip('Local ' + local.id_local + (local.nome ? ' – ' + local.nome : ''), {
                permanent: false, direction: 'top'
            });
            marker.on('click', function () { clicarNoPonto(local.id_local); });
            marker.addTo(mapa);
            marcadores[local.id_local] = marker;
        });
    }

    function criarIcone(sequencia, total) {
        var cls, badge;
        if (sequencia === -1) {
            cls = 'cam-cinza'; badge = '';
        } else if (sequencia === 0) {
            cls = 'cam-verde'; badge = '<span class="cam-seq">' + (sequencia + 1) + '</span>';
        } else if (sequencia === total - 1) {
            cls = 'cam-vermelho'; badge = '<span class="cam-seq">' + (sequencia + 1) + '</span>';
        } else {
            cls = 'cam-azul'; badge = '<span class="cam-seq">' + (sequencia + 1) + '</span>';
        }
        return L.divIcon({
            className: '',
            html: '<div class="marcador-camera ' + cls + '"><i class="fa fa-camera"></i>' + badge + '</div>',
            iconSize: [36, 36],
            iconAnchor: [18, 18]
        });
    }

    function clicarNoPonto(idLocal) {
        var idx = rotaSelecionada.findIndex(function (p) { return p.id_local === idLocal; });
        if (idx >= 0) {
            rotaSelecionada.splice(idx, 1);
        } else {
            var local = locaisDisponiveis.find(function (l) { return l.id_local === idLocal; });
            if (local) {
                rotaSelecionada.push({
                    id_local: local.id_local,
                    nome: local.nome || ('Local ' + local.id_local),
                    lat: parseFloat(local.latitude),
                    lon: parseFloat(local.longitude)
                });
            }
        }
        atualizarVisualizacao();
    }

    function limparSelecao() {
        rotaSelecionada = [];
        atualizarVisualizacao();
        document.getElementById('dashboard-section').style.display = 'none';
        document.getElementById('div_mensagem').innerHTML = '';
    }

    function atualizarVisualizacao() {
        // Atualizar ícones
        var total = rotaSelecionada.length;
        locaisDisponiveis.forEach(function (local) {
            var idx = rotaSelecionada.findIndex(function (p) { return p.id_local === local.id_local; });
            var icone = criarIcone(idx, total);
            if (marcadores[local.id_local]) {
                marcadores[local.id_local].setIcon(icone);
            }
        });
        // Atualizar linha
        if (linhaRota) { mapa.removeLayer(linhaRota); linhaRota = null; }
        if (total >= 2) {
            var latlngs = rotaSelecionada.map(function (p) { return [p.lat, p.lon]; });
            linhaRota = L.polyline(latlngs, {
                color: '#7c3aed', weight: 3, dashArray: '8,6', opacity: 0.85
            }).addTo(mapa);
        }
        // Atualizar tabela lateral
        atualizarListaPontos();
    }

    function atualizarListaPontos() {
        var lista = document.getElementById('lista-pontos');
        var contador = document.getElementById('contador-pontos');
        var badge = document.getElementById('badge-total');
        var n = rotaSelecionada.length;

        if (n === 0) {
            lista.innerHTML = '<p class="text-muted small text-center mt-3">'
                + '<i class="bi bi-hand-index-thumb me-1"></i>'
                + 'Clique nos marcadores do mapa para selecionar os pontos de passagem.</p>';
            contador.style.display = 'none';
            return;
        }

        var html = '';
        rotaSelecionada.forEach(function (p, i) {
            var badgeCls = (i === 0) ? 'badge-inicio' : (i === n - 1 ? 'badge-fim' : 'badge-meio');
            html += '<div class="ponto-item">'
                + '<span class="ponto-badge ' + badgeCls + '">' + (i + 1) + '</span>'
                + '<span class="flex-grow-1">' + escapeHtml(p.nome) + '</span>'
                + '<button type="button" class="btn btn-link btn-sm p-0 ms-1 text-danger" '
                + 'onclick="clicarNoPonto(' + p.id_local + ')" title="Remover">'
                + '<i class="bi bi-x-circle"></i></button>'
                + '</div>';
        });
        lista.innerHTML = html;
        badge.textContent = n + ' ponto' + (n !== 1 ? 's' : '');
        contador.style.display = 'block';
    }

    // ─── Geração do Relatório ─────────────────────────────────────────────────
    function gerarRelatorio() {
        var dataIni = document.getElementById('txt_data_inicio').value;
        var dataFim = document.getElementById('txt_data_fim').value;

        if (!dataIni || !dataFim) {
            mostrarMensagem('Preencha a Data Início e a Data Fim antes de gerar o relatório.', 'error');
            return;
        }
        if (new Date(dataIni) > new Date(dataFim)) {
            mostrarMensagem('A Data Início deve ser anterior ou igual à Data Fim.', 'error');
            return;
        }
        if (rotaSelecionada.length < 2) {
            mostrarMensagem('Selecione pelo menos 2 pontos de passagem no mapa.', 'error');
            return;
        }

        var lista = rotaSelecionada.map(function (p) { return p.id_local; }).join(',');

        document.getElementById('btn_gerar').disabled = true;
        document.getElementById('loading_msg').style.display = 'block';
        document.getElementById('dashboard-section').style.display = 'none';

        var xhr = new XMLHttpRequest();
        xhr.open('POST', '/relatorio/ContagemPassagensPorLocal', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                document.getElementById('btn_gerar').disabled = false;
                document.getElementById('loading_msg').style.display = 'none';

                if (xhr.status === 200) {
                    try {
                        var dados = JSON.parse(xhr.responseText);
                        if (dados.erro) {
                            mostrarMensagem('Erro: ' + dados.erro, 'error');
                            return;
                        }
                        dadosUltimaConsulta = dados;
                        criarDashboard(dados);
                        document.getElementById('dashboard-section').style.display = 'block';
                        document.getElementById('dashboard-section').scrollIntoView({ behavior: 'smooth' });
                        mostrarMensagem('Relatório gerado com sucesso!', 'success');
                    } catch (e) {
                        mostrarMensagem('Erro ao processar os dados do relatório.', 'error');
                    }
                } else {
                    mostrarMensagem('Erro ao consultar dados. Código: ' + xhr.status, 'error');
                }
            }
        };

        xhr.onerror = function () {
            document.getElementById('btn_gerar').disabled = false;
            document.getElementById('loading_msg').style.display = 'none';
            mostrarMensagem('Erro de conexão ao gerar relatório.', 'error');
        };

        xhr.send('dataIni=' + encodeURIComponent(dataIni) +
                 '&dataFim=' + encodeURIComponent(dataFim) +
                 '&lista='   + encodeURIComponent(lista));
    }

    // ─── Carregar Passagens Sequenciais ────────────────────────────────────────
    function carregarPassagensSequenciais(dataIni, dataFim, lista) {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', '/relatorio/PassagensSequenciais', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                try {
                    var dados = JSON.parse(xhr.responseText);
                    if (dados.erro) {
                        console.error('Erro ao carregar passagens sequenciais:', dados.erro);
                        return;
                    }
                    popularTabelaSequencial(dados);
                } catch (e) {
                    console.error('Erro ao processar passagens sequenciais:', e);
                }
            }
        };

        xhr.send('dataIni=' + encodeURIComponent(dataIni) +
                 '&dataFim=' + encodeURIComponent(dataFim) +
                 '&lista='   + encodeURIComponent(lista));
    }

    // ─── Dashboard ────────────────────────────────────────────────────────────
    function criarDashboard(dados) {
        // Destruir DataTables anteriores
        if (dtPassagens) { try { dtPassagens.destroy(); } catch(e){} dtPassagens = null; }
        if (dtPlacas)    { try { dtPlacas.destroy();    } catch(e){} dtPlacas    = null; }
        if (dtSequencial) { try { dtSequencial.destroy(); } catch(e){} dtSequencial = null; }

        // Carregar passagens sequenciais
        var dataIni = document.getElementById('txt_data_inicio').value;
        var dataFim = document.getElementById('txt_data_fim').value;
        var lista = rotaSelecionada.map(function (p) { return p.id_local; }).join(',');
        carregarPassagensSequenciais(dataIni, dataFim, lista);

        var passagens = dados.passagens_por_local || [];
        var pares     = dados.placas_em_comum    || [];
        var n         = rotaSelecionada.length;

        // ── Calcular métricas ──────────────────────────────────────────────
        var totalPassagens = passagens.reduce(function (s, r) { return s + (r.total_passagens || 0); }, 0);
        var maxPassagens   = passagens.length ? passagens.reduce(function (a, b) { return a.total_passagens > b.total_passagens ? a : b; }) : null;
        var minPassagens   = passagens.length ? passagens.reduce(function (a, b) { return a.total_passagens < b.total_passagens ? a : b; }) : null;
        var maxPar         = pares.length ? pares.reduce(function (a, b) { return a.total_placas_em_ambos > b.total_placas_em_ambos ? a : b; }) : null;
        var minPar         = pares.length ? pares.reduce(function (a, b) { return a.total_placas_em_ambos < b.total_placas_em_ambos ? a : b; }) : null;

        // ── Stat Cards linha 1 ─────────────────────────────────────────────
        var l1 = document.getElementById('cards-linha1');
        l1.innerHTML =
            statCard('stat-card-blue',   'bi-signpost-split', totalPassagens.toLocaleString('pt-BR'), 'Total de Passagens') +
            statCard('stat-card-green',  'bi-geo-alt',        n,                                       'Pontos Analisados');

        // ── Stat Cards linha 2 (removidos) ─────────────────────────────────
        var l2 = document.getElementById('cards-linha2');
        l2.innerHTML = '';

        // ── Tabela 1: Passagens por Local ──────────────────────────────────
        var tbody1 = document.getElementById('tbody-passagens-por-local');
        var nomeMap = {};
        rotaSelecionada.forEach(function (p) { nomeMap[p.id_local] = p.nome; });

        // Preenche tbody com os dados (ou vazio se não houver - DataTables mostrará mensagem)
        tbody1.innerHTML = passagens.map(function (r) {
            var nome = nomeMap[r.id_local] || '—';
            return '<tr>'
                + '<td>' + r.id_local + '</td>'
                + '<td>' + escapeHtml(nome) + '</td>'
                + '<td>' + (r.total_passagens || 0).toLocaleString('pt-BR') + '</td>'
                + '</tr>';
        }).join('');

        if (dtPassagens) { try { dtPassagens.destroy(); } catch(e){} }
        dtPassagens = $('#tbl-passagens-por-local').DataTable({
            language: dtLanguagePtBr,
            pageLength: 25,
            order: [[2, 'desc']],
            destroy: true
        });

        // ── Tabela 2: Placas em Comum ──────────────────────────────────────
        var tbody2 = document.getElementById('tbody-placas-em-comum');
        
        // Preenche tbody com os dados (ou vazio se não houver - DataTables mostrará mensagem)
        tbody2.innerHTML = pares.map(function (r) {
            var nomeA = nomeMap[r.id_local_a] || '—';
            var nomeB = nomeMap[r.id_local_b] || '—';
            return '<tr>'
                + '<td>' + r.id_local_a + '</td>'
                + '<td>' + escapeHtml(nomeA) + '</td>'
                + '<td>' + r.id_local_b + '</td>'
                + '<td>' + escapeHtml(nomeB) + '</td>'
                + '<td>' + (r.total_placas_em_ambos || 0).toLocaleString('pt-BR') + '</td>'
                + '<td>' + (r.total_placas_distintas || 0).toLocaleString('pt-BR') + '</td>'
                + '</tr>';
        }).join('');

        if (dtPlacas) { try { dtPlacas.destroy(); } catch(e){} }
        dtPlacas = $('#tbl-placas-em-comum').DataTable({
            language: dtLanguagePtBr,
            pageLength: 25,
            order: [[4, 'desc']],
            destroy: true
        });
    }

    // ─── Popular Tabela Sequencial ─────────────────────────────────────────────
    function popularTabelaSequencial(dados) {
        var tbody = document.getElementById('tbody-passagens-sequenciais');
        var rotas = dados.rotas || dados.sequencia_locais || '';
        var totalPassagens = dados.total_passagens || 0;
        var placasDistintas = dados.placas_distintas || 0;

        tbody.innerHTML = '<tr>'
            + '<td>' + escapeHtml(rotas) + '</td>'
            + '<td>' + totalPassagens.toLocaleString('pt-BR') + '</td>'
            + '<td>' + placasDistintas.toLocaleString('pt-BR') + '</td>'
            + '</tr>';

        if (dtSequencial) { try { dtSequencial.destroy(); } catch(e){} }
        dtSequencial = $('#tbl-passagens-sequenciais').DataTable({
            language: dtLanguagePtBr,
            pageLength: 25,
            searching: false,
            paging: false,
            info: false,
            destroy: true
        });
    }

    function statCard(cls, icon, valor, titulo) {
        return '<div class="col-md-3">'
            + '<div class="stat-card ' + cls + '">'
            + '<i class="bi ' + icon + ' stat-icon"></i>'
            + '<div class="stat-value">' + valor + '</div>'
            + '<div class="stat-title">' + titulo + '</div>'
            + '</div></div>';
    }

    // ─── Exportar Excel ───────────────────────────────────────────────────────
    function exportarExcel() {
        if (!dadosUltimaConsulta) {
            mostrarMensagem('Gere o relatório antes de exportar.', 'error');
            return;
        }
        var wb = XLSX.utils.book_new();
        
        // Mapa de nomes dos locais
        var nomeMap = {};
        rotaSelecionada.forEach(function (p) { nomeMap[p.id_local] = p.nome; });

        // Aba 1: Pontos Selecionados
        var pontos = rotaSelecionada.map(function (p, i) {
            return { Sequencia: i + 1, ID_Local: p.id_local, Nome: p.nome };
        });
        XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(pontos), 'Pontos');

        // Aba 2: Contagem por Local
        var passagens = (dadosUltimaConsulta.passagens_por_local || []).map(function (r) {
            return {
                ID_Local: r.id_local,
                Nome: nomeMap[r.id_local] || '—',
                Total_Passagens: r.total_passagens
            };
        });
        XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(passagens), 'Contagem_por_Local');

        // Aba 3: Placas em Comum
        var pares = (dadosUltimaConsulta.placas_em_comum || []).map(function (r) {
            return {
                ID_Local_A: r.id_local_a,
                Nome_Local_A: nomeMap[r.id_local_a] || '—',
                ID_Local_B: r.id_local_b,
                Nome_Local_B: nomeMap[r.id_local_b] || '—',
                Total_Passagens: r.total_placas_em_ambos,
                Placas_Distintas: r.total_placas_distintas
            };
        });
        XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(pares), 'Placas_em_Comum');

        // Aba 4: Passagens Sequenciais
        var tbody = document.getElementById('tbody-passagens-sequenciais');
        if (tbody && tbody.rows.length > 0) {
            var row = tbody.rows[0];
            var sequenciais = [{
                Rotas: row.cells[0].textContent,
                Total_Passagens: row.cells[1].textContent.replace(/\./g, ''),
                Placas_Distintas: row.cells[2].textContent.replace(/\./g, '')
            }];
            XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(sequenciais), 'Passagens_Sequenciais');
        }

        var dataIni = document.getElementById('txt_data_inicio').value || 'data';
        var dataFim = document.getElementById('txt_data_fim').value || 'data';
        XLSX.writeFile(wb, 'Passagens_Rota_' + dataIni + '_' + dataFim + '.xlsx');
    }

    // ─── Imprimir ─────────────────────────────────────────────────────────────
    function imprimirRelatorio() {
        window.print();
    }

    // ─── Utilitários ──────────────────────────────────────────────────────────
    function mostrarMensagem(msg, tipo) {
        var div = document.getElementById('div_mensagem');
        var cls = (tipo === 'error') ? 'alert-danger' : 'alert-success';
        div.innerHTML = '<div class="alert ' + cls + ' alert-dismissible fade show" role="alert">'
            + escapeHtml(msg)
            + '<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>';
        if (tipo !== 'error') {
            setTimeout(function () { div.innerHTML = ''; }, 6000);
        }
    }

    function escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;');
    }
</script>

</body>
</html>
