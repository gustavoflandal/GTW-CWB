<%@ page language="java" pageEncoding="utf-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<html lang="pt-BR">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>GTW - Relatório de Alertas Detalhado</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

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

        .btn-success-custom {
            background-color: #059669;
            border-color: #059669;
            border-radius: 8px;
            padding: 10px 25px;
            font-weight: 600;
            color: white !important;
            transition: all 0.3s ease;
        }

        .btn-success-custom:hover {
            background-color: #047857;
            border-color: #047857;
            color: white !important;
            transform: translateY(-1px);
        }

        .btn-danger-custom {
            background-color: #dc2626;
            border-color: #dc2626;
            border-radius: 8px;
            padding: 10px 25px;
            font-weight: 600;
            color: white !important;
            transition: all 0.3s ease;
        }

        .btn-danger-custom:hover {
            background-color: #b91c1c;
            border-color: #b91c1c;
            color: white !important;
            transform: translateY(-1px);
        }

        .form-label {
            font-weight: 600;
            color: #1e3a5f;
            margin-bottom: 8px;
        }

        .form-control {
            border-radius: 6px;
            border: 1px solid #d1d5db;
            padding: 10px 12px;
        }

        .form-control:focus {
            border-color: #1e40af;
            box-shadow: 0 0 0 0.2rem rgba(30, 64, 175, 0.25);
        }



        /* Estilo para impressão */
        @media print {
            .form-container, .no-print, button, .btn {
                display: none !important;
            }
            
            body {
                background: white !important;
            }

            .dashboard-container {
                background: white !important;
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
                        <i class="bi bi-bell"></i> Relatório de Alertas Detalhado
                    </h2>
                    <p class="text-center text-muted">Análise detalhada de alertas gerados pelo sistema de monitoramento</p>
                </div>
            </div>

            <!-- Formulário de Parâmetros -->
            <div class="form-container">
                <form id="frm_filtro_relatorio">
                    <div class="row justify-content-center">
                        <div class="col-md-5 mb-3">
                            <label for="dataInicio" class="form-label">
                                <i class="bi bi-calendar"></i> Data Início
                            </label>
                            <div class='input-group' id='dataInicio' data-td-target-input='nearest' data-td-target-toggle='nearest'>
                                <input id='dataInicioInput' type='text' class='form-control' data-td-target='#dataInicio' readonly/>
                                <span class='input-group-text' data-td-target='#dataInicio' data-td-toggle='datetimepicker'>
                                    <span class='fas fa-calendar'></span>
                                </span>
                            </div>
                        </div>
                        <div class="col-md-5 mb-3">
                            <label for="dataFim" class="form-label">
                                <i class="bi bi-calendar"></i> Data Fim
                            </label>
                            <div class='input-group log-event' id='dataFim' data-td-target-input='nearest' data-td-target-toggle='nearest'>
                                <input id='dataFimInput' type='text' class='form-control' data-td-target='#dataFim' readonly/>
                                <span class='input-group-text' data-td-target='#dataFim' data-td-toggle='datetimepicker'>
                                    <span class='fas fa-calendar'></span>
                                </span>
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-12 text-center">
                            <button id="gerarExcel" type="button" class="btn btn-success-custom me-2" onclick="GerarRelatorio('xls')">
                                <i class="bi bi-file-earmark-excel"></i> Gerar Excel
                            </button>
                            <button id="gerarPDF" type="button" class="btn btn-danger-custom" onclick="GerarRelatorio('pdf')">
                                <i class="bi bi-file-earmark-pdf"></i> Gerar PDF
                            </button>
                        </div>
                    </div>
                </form>
                <div id="loading_msg" class="mt-3 text-center" style="display: none;">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Carregando...</span>
                    </div>
                    <p class="mt-2">Gerando relatório...</p>
                </div>
                <div class="row mt-3">
                    <div class="col-12">
                        <div id="div_mensagem"></div>
                    </div>
                </div>
            </div>


        </div>
    </div>

    <%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %>

</body>

<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>
<script src="js/alertas-detalhado.js"></script>
<script src="js/gerar-arquivo-download.js"></script>

</html>