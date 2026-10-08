<%@ page language="java" pageEncoding="utf-8" %>
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

        <%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

            <html lang="pt-BR">

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <title>GTW - Relatório de Evolução Semanal</title>
                <meta name="viewport" content="width=device-width, initial-scale=1">

                <script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
                <script type="text/javascript" src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>

                <!-- DataTables CSS -->
                <link rel="stylesheet" type="text/css"
                    href="https://cdn.datatables.net/1.11.5/css/dataTables.bootstrap5.min.css">
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

                    .form-container {
                        background: white;
                        border-radius: 10px;
                        padding: 25px;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        margin-bottom: 20px;
                        max-width: 700px;
                        margin-left: auto;
                        margin-right: auto;
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

                    .table-responsive {
                        margin-top: 20px;
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

                        .btn, .no-print, button, form, .form-container,
                        .dataTables_length, .dataTables_filter, .dataTables_info, .dataTables_paginate {
                            display: none !important;
                        }

                        .dashboard-container {
                            background: white !important;
                            padding: 0 10px !important;
                            min-height: auto !important;
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
                            padding: 0 !important;
                        }

                        h2 {
                            font-size: 16pt !important;
                            color: #000 !important;
                        }

                        .table-responsive {
                            overflow: visible !important;
                        }

                        /* Tabelas - ajuste para não cortar colunas */
                        table {
                            font-size: 7pt !important;
                            border-collapse: collapse !important;
                            width: 100% !important;
                            table-layout: fixed !important;
                        }

                        table thead {
                            display: table-header-group;
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

                        * {
                            box-shadow: none !important;
                            text-shadow: none !important;
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
                        <%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %>

                        <!-- Cabeçalho -->
                        <div class="row mb-4">
                            <div class="col-12">
                                <h2 class="text-center text-dark mb-1">
                                    <i class="bi bi-graph-up"></i> Relatório de Evolução Semanal por Tipos de Fatos
                                </h2>
                                <p class="text-center text-muted">Acompanhamento da evolução semanal dos registros de fatos</p>
                            </div>
                        </div>

                        <!-- Formulário de Parâmetros -->
                        <div class="form-container no-print">
                            <form id="formRelatorio" onsubmit="carregarDados(); return false;">
                                <div class="row justify-content-center">
                                    <div class="col-md-6 mb-3">
                                        <label for="filtroAno" class="form-label fw-bold">
                                            <i class="bi bi-calendar-date"></i> Ano (opcional)
                                        </label>
                                        <input type="number" class="form-control" id="filtroAno" placeholder="Ex: 2025"
                                            min="2020" max="2030">
                                        <div class="form-text">Deixe em branco para todos</div>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label for="filtroSemana" class="form-label fw-bold">
                                            <i class="bi bi-calendar-week"></i> Semana (opcional)
                                        </label>
                                        <input type="number" class="form-control" id="filtroSemana" placeholder="1-53"
                                            min="1" max="53">
                                        <div class="form-text">Deixe em branco para todas</div>
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

                    <!-- Tabela de Dados -->
                    <div class="mt-4">
                        <div class="row">
                            <div class="col-12">
                                <div class="card">
                                    <div class="card-header">
                                        <h5 class="card-title mb-0">
                                            <i class="bi bi-graph-up"></i>
                                            Evolução Semanal Detalhada
                                        </h5>
                                    </div>
                                    <div class="card-body">
                                        <div class="table-responsive">
                                            <table id="tabelaEvolucao" class="table table-striped table-hover"
                                                style="width:100%">
                                                <thead class="table-dark">
                                                    <tr>
                                                        <th>Ano</th>
                                                        <th>Semana</th>
                                                        <th>Tipo Fato</th>
                                                        <th>Quantidade</th>
                                                        <th>Distribuição %</th>
                                                    </tr>
                                                </thead>
                                                <tbody id="corpoTabela">
                                                    <tr>
                                                        <td colspan="5" class="text-center">
                                                            <div class="spinner-border" role="status">
                                                                <span class="visually-hidden">Carregando...</span>
                                                            </div>
                                                            Carregando dados...
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Botões de Ação -->
                    <div class="row mt-4 no-print" id="action-buttons" style="display: none;">
                        <div class="col-12 text-center">
                            <button type="button" class="btn btn-outline-success me-2" onclick="exportarExcel()">
                                <i class="bi bi-file-earmark-excel"></i> Excel
                            </button>
                            <button type="button" class="btn btn-outline-secondary me-2" onclick="imprimirRelatorio()" id="btnImprimir">
                                <i class="bi bi-printer"></i> Imprimir
                            </button>
                        </div>
                    </div>

                    </div><!-- Fecha container -->
                </div><!-- Fecha dashboard-container -->

            </body>

            <!-- DataTables JS -->
            <script type="text/javascript" src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
            <script type="text/javascript"
                src="https://cdn.datatables.net/1.11.5/js/dataTables.bootstrap5.min.js"></script>

            <script>
                let tabelaDataTable;
                let dadosOriginais = [];

                // Intercepta erros de outros scripts para não quebrar nossa funcionalidade
                window.addEventListener('error', function (e) {
                    console.log('Erro interceptado de script externo:', e.filename, e.message);
                    // Não propaga o erro se for de notificacao.js ou outros scripts externos
                    if (e.filename && (e.filename.includes('notificacao.js') || e.filename.includes('all.js'))) {
                        e.preventDefault();
                        return true;
                    }
                });

                $(document).ready(function () {
                    try {
                        // Adicionar validação de ano
                        $('#filtroAno').on('input', function () {
                            const ano = parseInt($(this).val());
                            if (ano && (ano < 2020 || ano > 2030)) {
                                $(this).addClass('is-invalid');
                                $(this).next('.form-text').text('Ano deve estar entre 2020 e 2030');
                            } else {
                                $(this).removeClass('is-invalid');
                                $(this).next('.form-text').text('Deixe em branco para todos');
                            }
                        });

                        // Adicionar validação de semana
                        $('#filtroSemana').on('input', function () {
                            const semana = parseInt($(this).val());
                            if (semana && (semana < 1 || semana > 53)) {
                                $(this).addClass('is-invalid');
                                $(this).next('.form-text').text('Semana deve estar entre 1 e 53');
                            } else {
                                $(this).removeClass('is-invalid');
                                $(this).next('.form-text').text('Deixe em branco para todas');
                            }
                        });

                        // Carregar dados iniciais (sem filtros)
                        carregarDados();
                    } catch (e) {
                        console.error('Erro ao inicializar página:', e);
                        alert('Erro ao inicializar a página. Recarregue e tente novamente.');
                    }
                });

                function carregarDados() {
                    // Mostra loading
                    $('#atualizarDados').prop('disabled', true);
                    $('#atualizarDados').html('<i class="fas fa-spinner fa-spin"></i> Carregando...');

                    // Obtém os valores dos filtros
                    let ano = $('#filtroAno').val();
                    let semana = $('#filtroSemana').val();

                    // Limpar e validar os valores
                    ano = ano ? ano.toString().trim() : '';
                    semana = semana ? semana.toString().trim() : '';

                    // Validar ano se informado
                    if (ano !== '' && (isNaN(ano) || parseInt(ano) < 2020 || parseInt(ano) > 2030)) {
                        console.error('Ano inválido:', ano);
                        alert('Ano deve ser um número entre 2020 e 2030');
                        $('#atualizarDados').prop('disabled', false);
                        $('#atualizarDados').html('<i class="fas fa-sync-alt"></i> Buscar Dados');
                        return;
                    }

                    // Validar semana se informada
                    if (semana !== '' && (isNaN(semana) || parseInt(semana) < 1 || parseInt(semana) > 53)) {
                        alert('Semana deve ser um número entre 1 e 53');
                        $('#atualizarDados').prop('disabled', false);
                        $('#atualizarDados').html('<i class="fas fa-sync-alt"></i> Buscar Dados');
                        return;
                    }

                    let data = {
                        acao: 'buscarDados',
                        _timestamp: new Date().getTime() // Anti-cache
                    };

                    // Adiciona os parâmetros se foram informados
                    if (ano !== '') {
                        data.ano = ano;
                        console.log('Enviando parâmetro ano:', ano);
                    }

                    if (semana !== '') {
                        data.semana = semana;
                        console.log('Enviando parâmetro semana:', semana);
                    }

                    if (ano === '' && semana === '') {
                        console.log('Carregando sem filtros');
                    }

                    $.ajax({
                        url: '/MuralhaDigital/RelatorioEvolucaoSemanal',
                        type: 'POST',
                        data: data,
                        dataType: 'json',
                        cache: false,
                        timeout: 30000,
                        success: function (dados) {
                            console.log('Dados recebidos:', dados ? dados.length : 0, 'registros');

                            dadosOriginais = dados;
                            preencherTabela(dados);

                            // Mostra botões de ação
                            if (dados && dados.length > 0) {
                                document.getElementById('action-buttons').style.display = 'block';
                            } else {
                                document.getElementById('action-buttons').style.display = 'none';
                            }

                            // Mostra mensagem de filtro aplicado
                            let titulo = 'Relatório de evolução semanal por tipos de fatos';
                            let filtros = [];

                            if (ano !== '') {
                                filtros.push('Ano ' + ano);
                            }

                            if (semana !== '') {
                                filtros.push('Semana ' + semana);
                            }

                            if (filtros.length > 0) {
                                titulo += ' - ' + filtros.join(', ');
                            }

                            $('#tituloTelaConsulta').html('<strong>' + titulo + '</strong>');
                        },
                        error: function (xhr, status, error) {
                            console.error('Erro ao carregar dados:', error);
                            console.error('Status:', status);
                            console.error('Response:', xhr.responseText);
                            console.error('ReadyState:', xhr.readyState);

                            if (status === 'timeout') {
                                alert('Timeout na requisição. Tente novamente.');
                            } else if (xhr.status === 0) {
                                alert('Erro de conexão. Verifique sua conexão com o servidor.');
                            } else {
                                alert('Erro ao carregar dados do relatório. Status: ' + xhr.status + '. Verifique o console para mais detalhes.');
                            }
                        },
                        complete: function () {
                            $('#atualizarDados').prop('disabled', false);
                            $('#atualizarDados').html('<i class="fas fa-sync-alt"></i> Buscar Dados');
                        }
                    });
                }

                function limparFiltro() {
                    console.log('Iniciando limpeza de filtros...');

                    // Desabilitar botão e mostrar loading
                    $('#limparFiltro').prop('disabled', true);
                    $('#limparFiltro').html('<i class="fas fa-spinner fa-spin"></i> Limpando...');

                    // Limpar campos
                    $('#filtroAno').val('');
                    $('#filtroSemana').val('');

                    // Remover classes de validação
                    $('#filtroAno').removeClass('is-invalid');
                    $('#filtroSemana').removeClass('is-invalid');

                    // Restaurar textos de ajuda
                    $('#filtroAno').next('.form-text').text('Deixe em branco para todos');
                    $('#filtroSemana').next('.form-text').text('Deixe em branco para todas');

                    // Restaurar título
                    $('#tituloTelaConsulta').html('<strong>Relatório de evolução semanal por tipos de fatos</strong>');

                    // Limpar cache local
                    dadosOriginais = [];

                    // Destruir DataTable se existir de forma mais robusta
                    if ($.fn.DataTable.isDataTable('#tabelaEvolucao')) {
                        console.log('DEBUG limparFiltro - DataTable detectado, destruindo...');
                        $('#tabelaEvolucao').DataTable().destroy();
                    }

                    if (tabelaDataTable) {
                        tabelaDataTable = null;
                    }

                    // Mostrar loading na tabela
                    $('#corpoTabela').html('<tr><td colspan="5" class="text-center"><div class="spinner-border" role="status"><span class="visually-hidden">Carregando...</span></div> Buscando todos os dados no banco...</td></tr>');

                    // Forçar nova consulta ao banco com parâmetros vazios
                    console.log('Forçando nova consulta ao banco sem filtros...');

                    $.ajax({
                        url: '/MuralhaDigital/RelatorioEvolucaoSemanal',
                        type: 'POST',
                        data: {
                            acao: 'buscarDados',
                            ano: '',
                            semana: '',
                            _timestamp: new Date().getTime(),
                            _forceRefresh: 'true'
                        },
                        dataType: 'json',
                        cache: false,
                        timeout: 30000,
                        success: function (dados) {
                            console.log('Dados recebidos após limpeza:', dados);
                            console.log('Quantidade de registros:', dados ? dados.length : 0);

                            dadosOriginais = dados;
                            preencherTabela(dados);

                            // Mostra botões de ação
                            if (dados && dados.length > 0) {
                                document.getElementById('action-buttons').style.display = 'block';
                            } else {
                                document.getElementById('action-buttons').style.display = 'none';
                            }

                            $('#tituloTelaConsulta').html('<strong>Relatório de evolução semanal por tipos de fatos</strong>');

                            // Restaurar botão limpar
                            $('#limparFiltro').prop('disabled', false);
                            $('#limparFiltro').html('<i class="fas fa-eraser"></i> Limpar');
                        },
                        error: function (xhr, status, error) {
                            console.error('Erro ao limpar filtros e recarregar dados:', error);
                            console.error('Status:', status);
                            console.error('Response:', xhr.responseText);

                            if (status === 'timeout') {
                                alert('Timeout na requisição. Tente novamente.');
                            } else if (xhr.status === 0) {
                                alert('Erro de conexão. Verifique sua conexão com o servidor.');
                            } else {
                                alert('Erro ao recarregar dados. Status: ' + xhr.status + '. Tente novamente.');
                            }

                            // Restaurar botão limpar mesmo em caso de erro
                            $('#limparFiltro').prop('disabled', false);
                            $('#limparFiltro').html('<i class="fas fa-eraser"></i> Limpar');

                            // Em caso de erro, tentar carregarDados() como fallback
                            setTimeout(function () {
                                carregarDados();
                            }, 1000);
                        }
                    });
                }


                function preencherTabela(dados) {
                    console.log('Preenchendo tabela com', dados ? dados.length : 0, 'registros');

                    // Verificar e destruir DataTable existente de forma mais robusta
                    if ($.fn.DataTable.isDataTable('#tabelaEvolucao')) {
                        $('#tabelaEvolucao').DataTable().destroy();
                    }

                    if (tabelaDataTable) {
                        tabelaDataTable = null;
                    }

                    let html = '';

                    if (dados && dados.length > 0) {
                        dados.forEach(function (item, index) {
                            html += '<tr>';
                            html += '<td>' + (item.ano || 'N/A') + '</td>';
                            html += '<td>' + (item.semana || 'N/A') + '</td>';
                            html += '<td>' + (item.tipoFato || 'N/A') + '</td>';
                            html += '<td>' + ((item.quantidade || 0).toLocaleString ? (item.quantidade || 0).toLocaleString('pt-BR') : (item.quantidade || 0)) + '</td>';
                            html += '<td>' + (item.distribuicaoPercentual || 0) + '%</td>';
                            html += '</tr>';
                        });
                    } else {
                        html = '<tr><td colspan="5" class="text-center">Nenhum dado encontrado</td></tr>';
                    }

                    $('#corpoTabela').html(html);

                    // Inicializa DataTable
                    try {
                        tabelaDataTable = $('#tabelaEvolucao').DataTable({
                            language: {
                                "decimal": ",",
                                "thousands": ".",
                                "info": "Mostrando _START_ a _END_ de _TOTAL_ registros",
                                "infoEmpty": "Mostrando 0 a 0 de 0 registros",
                                "infoFiltered": "(filtrado de _MAX_ registros no total)",
                                "lengthMenu": "Mostrar _MENU_ registros por página",
                                "loadingRecords": "Carregando...",
                                "processing": "Processando...",
                                "search": "Buscar:",
                                "zeroRecords": "Nenhum registro encontrado",
                                "paginate": {
                                    "first": "Primeiro",
                                    "last": "Último",
                                    "next": "Próximo",
                                    "previous": "Anterior"
                                }
                            },
                            order: [[0, 'desc'], [1, 'desc']],
                            pageLength: 25,
                            lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, "Todos"]],
                            responsive: true
                        });
                    } catch (e) {
                        console.error('Erro ao inicializar DataTable:', e);
                    }
                }

                function exportarExcel() {
                    try {
                        // Obter os filtros atuais
                        var anoFiltro = $('#filtroAno').val();
                        var semanaFiltro = $('#filtroSemana').val();

                        // Construir URL para exportação com caminho absoluto
                        var url = '/MuralhaDigital/RelatorioEvolucaoSemanal?acao=exportarExcel';

                        if (anoFiltro && anoFiltro.trim() !== '') {
                            url += '&ano=' + encodeURIComponent(anoFiltro.trim());
                        }

                        if (semanaFiltro && semanaFiltro.trim() !== '') {
                            url += '&semana=' + encodeURIComponent(semanaFiltro.trim());
                        }

                        console.log('Iniciando exportação Excel com URL:', url);

                        // Mostrar indicador de carregamento (opcional)
                        var btnExportar = $('#exportarExcel');
                        var textoOriginal = btnExportar.html();
                        btnExportar.html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Exportando...');
                        btnExportar.prop('disabled', true);

                        // Criar link temporário para download
                        var link = document.createElement('a');
                        link.href = url;
                        link.style.display = 'none';

                        // Adicionar ao DOM temporariamente e clicar
                        document.body.appendChild(link);
                        link.click();

                        // Remover do DOM
                        setTimeout(function () {
                            document.body.removeChild(link);
                            // Restaurar botão
                            btnExportar.html(textoOriginal);
                            btnExportar.prop('disabled', false);
                        }, 1000);

                        console.log('Exportação Excel iniciada');

                    } catch (error) {
                        console.error('Erro ao exportar para Excel:', error);
                        alert('Erro ao exportar relatório para Excel: ' + error.message);

                        // Restaurar botão em caso de erro
                        var btnExportar = $('#exportarExcel');
                        btnExportar.html('<i class="fas fa-file-excel"></i> Exportar Excel');
                        btnExportar.prop('disabled', false);
                    }
                }

                function imprimirRelatorio() {
                    window.print();
                }
            </script>

            </html>