<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="pt-BR">

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>GTW - Relatório de Distribuição de Fatos</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        
        <!-- DataTables CSS -->
        <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.11.5/css/dataTables.bootstrap5.min.css">
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

            .form-container {
                background: white;
                border-radius: 10px;
                padding: 25px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                margin-bottom: 20px;
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

            /* Estilo para impressão */
            @media print {
                .form-container, .btn, .no-print, button {
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
                            <i class="bi bi-pie-chart"></i> Relatório Estatístico de Fatos por Tipo
                        </h2>
                        <p class="text-center text-muted">Análise estatística da distribuição de fatos registrados</p>
                    </div>
                </div>

                <!-- Formulário de Parâmetros -->
                <div class="form-container no-print">
                    <div class="row">
                        <div class="col-12 text-center">
                            <button id="btnBuscar" type="button" class="btn btn-primary-custom me-2" onclick="carregarDados()">
                                <i class="bi bi-search"></i> Buscar
                            </button>
                            <button type="button" class="btn btn-outline-secondary me-2" onclick="limparFiltro()">
                                <i class="bi bi-eraser"></i> Limpar
                            </button>
                            <button type="button" class="btn btn-outline-success me-2" onclick="exportarExcel()">
                                <i class="bi bi-file-earmark-excel"></i> Excel
                            </button>
                        </div>
                    </div>
                </div>
        
                <!-- Tabela de Dados -->
                <div class="mt-4">
                    <div class="row">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-pie-chart"></i>
                                        Distribuição de Fatos
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <div class="table-responsive">
                                        <table id="tabelaDistribuicao" class="table table-striped table-hover" style="width:100%">
                                            <thead class="table-dark">
                                                <tr>
                                                    <th>Tipo de Fato</th>
                                                    <th>Total de Registros</th>
                                                    <th>Primeira Ocorrência</th>
                                                    <th>Última Ocorrência</th>
                                                    <th>% sobre o Total</th>
                                                </tr>
                                            </thead>
                                            <tbody id="corpoTabela">
                                                <!-- Dados serão carregados via AJAX -->
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div><!-- Fecha container -->
        </div><!-- Fecha dashboard-container -->

        <!-- jQuery e DataTables -->
        <script type="text/javascript" src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/1.11.5/js/dataTables.bootstrap5.min.js"></script>
        
        <script>
            let tabelaDataTable;
            let dadosOriginais = [];
            
            // Inicialização da página
            $(function() {
                console.log('=== INICIALIZANDO PÁGINA ===');
                
                // Carrega os dados iniciais
                setTimeout(function() {
                    console.log('Chamando carregarDados...');
                    carregarDados();
                }, 500);
            });
            
            // Carrega os dados da tabela via AJAX
            function carregarDados() {
                console.log('=== INÍCIO carregarDados ===');
                
                // Mostra loading
                $('#btnBuscar').prop('disabled', true);
                $('#btnBuscar').html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Carregando...');
                
                console.log('Enviando requisição para buscar dados...');
                
                // Faz a requisição AJAX
                $.ajax({
                    url: '/MuralhaDigital/RelatorioDistribuicaoFatos',
                    type: 'POST',
                    data: {
                        acao: 'buscarDados'
                    },
                    dataType: 'json',
                    success: function(dados) {
                        console.log('Dados recebidos:', dados);
                        console.log('Primeiro item (se existir):', dados[0]);
                        dadosOriginais = dados;
                        preencherTabela(dados);
                    },
                    error: function(xhr, status, error) {
                        console.error('=== ERRO AJAX ===');
                        console.error('Status HTTP:', xhr.status);
                        console.error('Status Text:', xhr.statusText);
                        console.error('Ready State:', xhr.readyState);
                        console.error('Response Text:', xhr.responseText);
                        console.error('Error:', error);
                        console.error('Status:', status);
                        
                        let mensagemErro = 'Erro ao carregar dados.';
                        
                        try {
                            if (xhr.responseText) {
                                const response = JSON.parse(xhr.responseText);
                                if (response.erro) {
                                    mensagemErro = 'Erro: ' + response.erro;
                                }
                            }
                        } catch (e) {
                            console.error('Erro ao parsear resposta JSON:', e);
                            mensagemErro = 'Erro na comunicação com o servidor. Status: ' + xhr.status;
                        }
                        
                        alert(mensagemErro + ' Verifique o console para mais detalhes.');
                    },
                    complete: function() {
                        $('#btnBuscar').prop('disabled', false);
                        $('#btnBuscar').html('<i class="fas fa-search"></i> Buscar');
                    }
                });
            }
            
            // Preenche a tabela com os dados recebidos
            function preencherTabela(dados) {
                // Destrói a tabela DataTable se já existir
                if ($.fn.DataTable.isDataTable('#tabelaDistribuicao')) {
                    tabelaDataTable.destroy();
                }
                
                // Limpa o corpo da tabela
                const tbody = $('#corpoTabela');
                tbody.empty();
                
                // Se não houver dados, exibe mensagem
                if (!dados || dados.length === 0) {
                    tbody.append('<tr><td colspan="5" class="text-center">Nenhum registro encontrado.</td></tr>');
                    return;
                }
                
                // Preenche a tabela com os dados - usando apenas valores diretos sem funções
                dados.forEach(function(item) {
                    const tr = $('<tr>');
                    
                    // Tipo de Fato - valor direto
                    tr.append('<td>' + (item.tipoFato || '-') + '</td>');
                    
                    // Total de Registros - formatação simples
                    const totalRegistros = item.totalRegistros || 0;
                    tr.append('<td class="text-end">' + totalRegistros.toLocaleString('pt-BR') + '</td>');
                    
                    // Data Primeira Ocorrência - conversão com data e hora
                    let dataPrimeira = '-';
                    if (item.dataPrimeiraOcorrencia) {
                        const dataStr = item.dataPrimeiraOcorrencia.toString();
                        if (dataStr.includes('-')) {
                            // Se tem formato YYYY-MM-DD HH:mm:ss.SSS ou YYYY-MM-DD
                            const partes = dataStr.split(' ');
                            const dataPartes = partes[0].split('-');
                            if (dataPartes.length === 3) {
                                const dataFormatada = dataPartes[2] + '/' + dataPartes[1] + '/' + dataPartes[0];
                                if (partes.length > 1) {
                                    // Tem hora, pega só HH:mm:ss (remove milissegundos se existir)
                                    const horaCompleta = partes[1].split('.')[0];
                                    dataPrimeira = dataFormatada + ' ' + horaCompleta;
                                } else {
                                    dataPrimeira = dataFormatada;
                                }
                            }
                        } else {
                            dataPrimeira = dataStr;
                        }
                    }
                    tr.append('<td class="text-center">' + dataPrimeira + '</td>');
                    
                    // Data Última Ocorrência - conversão com data e hora
                    let dataUltima = '-';
                    if (item.dataUltimaOcorrencia) {
                        const dataStr = item.dataUltimaOcorrencia.toString();
                        if (dataStr.includes('-')) {
                            // Se tem formato YYYY-MM-DD HH:mm:ss.SSS ou YYYY-MM-DD
                            const partes = dataStr.split(' ');
                            const dataPartes = partes[0].split('-');
                            if (dataPartes.length === 3) {
                                const dataFormatada = dataPartes[2] + '/' + dataPartes[1] + '/' + dataPartes[0];
                                if (partes.length > 1) {
                                    // Tem hora, pega só HH:mm:ss (remove milissegundos se existir)
                                    const horaCompleta = partes[1].split('.')[0];
                                    dataUltima = dataFormatada + ' ' + horaCompleta;
                                } else {
                                    dataUltima = dataFormatada;
                                }
                            }
                        } else {
                            dataUltima = dataStr;
                        }
                    }
                    tr.append('<td class="text-center">' + dataUltima + '</td>');
                    
                    // Percentual sobre o Total - formatação simples
                    let percentual = '0,00%';
                    if (item.percentualSobreTotal !== null && item.percentualSobreTotal !== undefined) {
                        const valor = parseFloat(item.percentualSobreTotal);
                        if (!isNaN(valor)) {
                            percentual = valor.toFixed(2).replace('.', ',') + '%';
                        }
                    }
                    tr.append('<td class="text-end">' + percentual + '</td>');
                    
                    tbody.append(tr);
                });
                
                // Inicializa a DataTable
                tabelaDataTable = $('#tabelaDistribuicao').DataTable({
                    language: {
                        "sEmptyTable": "Nenhum registro encontrado",
                        "sInfo": "Mostrando de _START_ até _END_ de _TOTAL_ registros",
                        "sInfoEmpty": "Mostrando 0 até 0 de 0 registros",
                        "sInfoFiltered": "(Filtrados de _MAX_ registros)",
                        "sInfoThousands": ".",
                        "sLengthMenu": "_MENU_ resultados por página",
                        "sLoadingRecords": "Carregando...",
                        "sProcessing": "Processando...",
                        "sZeroRecords": "Nenhum registro encontrado",
                        "sSearch": "Pesquisar",
                        "oPaginate": {
                            "sNext": "Próximo",
                            "sPrevious": "Anterior",
                            "sFirst": "Primeiro",
                            "sLast": "Último"
                        },
                        "oAria": {
                            "sSortAscending": ": Ordenar colunas de forma ascendente",
                            "sSortDescending": ": Ordenar colunas de forma descendente"
                        },
                        decimal: ',',
                        thousands: '.'
                    },
                    order: [[1, 'desc']], // Ordena pelo total de registros (coluna 1) em ordem decrescente
                    pageLength: 25,
                    responsive: true,
                    dom: '<"top"f>rt<"bottom"lip><"clear">',
                    columnDefs: [
                        { 
                            targets: 1, 
                            className: 'dt-body-right',
                            type: 'num' // Ordenação numérica para a coluna de total
                        },
                        { 
                            targets: [2, 3], // Colunas de data
                            type: 'date-eu', // Usa o tipo de data europeu (dd/mm/yyyy) para ordenação
                            render: function(data, type, row) {
                                if (type === 'sort') {
                                    // Para ordenação, converte para timestamp
                                    return data ? new Date(data.split('/').reverse().join('-')).getTime() : 0;
                                }
                                // Para exibição, mantém o formato dd/mm/yyyy
                                return data;
                            }
                        },
                        { 
                            targets: 4, 
                            className: 'dt-body-right',
                            type: 'num',
                            render: function(data, type) {
                                if (type === 'sort') {
                                    // Remove o % e converte para número para ordenação
                                    return parseFloat(data.replace('%', '').replace('.', '').replace(',', '.'));
                                }
                                return data;
                            }
                        }
                    ]
                });
            }
            
            // Funções de formatação simplificadas
            window.formatarNumero = function(numero) {
                if (numero === null || numero === undefined) return '0';
                return numero.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
            };
            
            window.formatarPercentual = function(valor) {
                if (valor === null || valor === undefined) return '0,00%';
                const numero = parseFloat(valor);
                return isNaN(numero) ? '0,00%' : numero.toLocaleString('pt-BR', { 
                    minimumFractionDigits: 2, 
                    maximumFractionDigits: 2 
                }) + '%';
            };
            
            // Formata data para exibição (dd/mm/yyyy) a partir de uma string ISO (YYYY-MM-DD)
            window.formatarDataExibicao = function(dataString) {
                if (!dataString) return '-';
                try {
                    // Se a data já estiver no formato dd/mm/yyyy, retorna direto
                    if (/^\d{2}\/\d{2}\/\d{4}$/.test(dataString)) {
                        return dataString;
                    }
                    // Se tem formato YYYY-MM-DD HH:mm:ss.SSS, extrai só a parte da data
                    if (dataString.includes(' ')) {
                        dataString = dataString.split(' ')[0];
                    }
                    // Converte de YYYY-MM-DD para Date e depois para dd/mm/yyyy
                    if (dataString.includes('-')) {
                        const [ano, mes, dia] = dataString.split('-');
                        return `${dia.padStart(2, '0')}/${mes.padStart(2, '0')}/${ano}`;
                    }
                    return dataString;
                } catch (e) {
                    console.error('Erro ao formatar data:', dataString, e);
                    return dataString; // Retorna o valor original em caso de erro
                }
            };
            
            // Limpa os filtros e recarrega os dados
            function limparFiltro() {
                // Recarrega os dados após um pequeno delay
                setTimeout(function() {
                    carregarDados();
                }, 100);
            }
            
            // Exporta os dados para Excel
            function exportarExcel() {
                // Cria um formulário temporário para enviar os parâmetros
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '/MuralhaDigital/RelatorioDistribuicaoFatos';
                form.target = '_blank';
                
                // Adiciona os parâmetros
                const params = {
                    acao: 'exportarExcel'
                };
                
                for (const key in params) {
                    if (params.hasOwnProperty(key)) {
                        const input = document.createElement('input');
                        input.type = 'hidden';
                        input.name = key;
                        input.value = params[key];
                        form.appendChild(input);
                    }
                }
                
                // Adiciona o formulário ao corpo do documento e o submete
                document.body.appendChild(form);
                form.submit();
                
                // Remove o formulário após o envio
                setTimeout(() => {
                    document.body.removeChild(form);
                }, 100);
            }
        </script>
    </body>
</html>
