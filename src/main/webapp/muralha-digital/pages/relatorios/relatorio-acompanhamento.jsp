<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="pt-BR">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>GTW - Relatório de Acompanhamento</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
		<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>
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
							<i class="bi bi-clipboard-data"></i> Relatório de Acompanhamento
						</h2>
						<p class="text-center text-muted">Acompanhamento detalhado de irregularidades por período</p>
					</div>
				</div>

				<!-- Formulário de Parâmetros -->
				<div class="form-container no-print">
					<form id="formRelatorio" onsubmit="GerarRelatorio(); return false;">
						<div class="row justify-content-center">
							<div class="col-md-4 mb-3">
								<label for="dataInicioInput" class="form-label fw-bold">
									<i class="bi bi-calendar"></i> Data/Hora Início
								</label>
								<div class='input-group' id='dataInicio' data-td-target-input='nearest' data-td-target-toggle='nearest'>
									<input id='dataInicioInput' type='text' class='form-control' data-td-target='#dataInicio' readonly/>
									<span class='input-group-text' data-td-target='#dataInicio' data-td-toggle='datetimepicker'>
										<span class='fas fa-calendar'></span>
									</span>
								</div>
							</div>
							<div class="col-md-4 mb-3">
								<label for="dataFimInput" class="form-label fw-bold">
									<i class="bi bi-calendar"></i> Data/Hora Fim
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
								<button type="submit" class="btn btn-outline-success me-2">
									<i class="bi bi-file-earmark-excel"></i> Gerar Relatório
								</button>
							</div>
						</div>
					</form>
				</div>

			</div><!-- Fecha container -->
		</div><!-- Fecha dashboard-container -->

	</body>
	
	<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>
 	<script src="js/relatorio-acompanhamento.js"></script>
 	<script src="js/gerar-arquivo-download.js"></script>

</html>