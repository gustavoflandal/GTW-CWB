<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="pt-BR">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>GTW - Relatório de Pendências nos Registros de Fato</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
		<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>
		
		<!-- Bootstrap Icons -->
		<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
		<!-- DataTables CSS -->
		<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.11.5/css/dataTables.bootstrap5.min.css">
		
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

			.table-responsive {
				margin-top: 20px;
			}
			.badge-sim {
				background-color: #28a745;
			}
			.badge-nao {
				background-color: #dc3545;
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
				padding: 10px;
				background: white !important;
			}

			.container {
				max-width: 100% !important;
				padding: 0 10px !important;
			}

			h2, h5 {
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

			.badge-sim {
				background-color: #c6f6d5 !important;
				color: #000 !important;
				border: 1px solid #28a745 !important;
				font-size: 6pt !important;
			}

			.badge-nao {
				background-color: #fed7d7 !important;
				color: #000 !important;
				border: 1px solid #dc3545 !important;
				font-size: 6pt !important;
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
							<i class="bi bi-exclamation-triangle"></i> Relatório de Pendências nos Registros de Fato
						</h2>
						<p class="text-center text-muted">Análise de pendências nos registros de fatos por período</p>
					</div>
				</div>

				<!-- Formulário de Parâmetros -->
				<div class="form-container no-print">
					<form id="formRelatorio" onsubmit="carregarDados(); return false;">
						<div class="row justify-content-center">
							<div class="col-md-6 mb-3">
								<label for="dataInicio" class="form-label fw-bold">
									<i class="bi bi-calendar"></i> Data Início
								</label>
								<input type="date" class="form-control" id="dataInicio" name="dataInicio">
								<div class="form-text">Deixe em branco para buscar todas</div>
							</div>
							<div class="col-md-6 mb-3">
								<label for="dataFim" class="form-label fw-bold">
									<i class="bi bi-calendar"></i> Data Final
								</label>
								<input type="date" class="form-control" id="dataFim" name="dataFim">
								<div class="form-text">Deixe em branco para buscar todas</div>
							</div>
						</div>
						
						<div class="row">
							<div class="col-12 text-center">
								<button type="submit" class="btn btn-primary-custom" id="atualizarDados">
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
										<i class="bi bi-exclamation-triangle"></i>
										Pendências nos Registros de Fato
									</h5>
								</div>
								<div class="card-body">
									<div class="table-responsive">
										<table id="tabelaPendencias" class="table table-striped table-hover" style="width:100%">
											<thead class="table-dark">
												<tr>
													<th>ID</th>
													<th>Tipo</th>
													<th>Status</th>
													<th>Usuário</th>
													<th>Data Criação</th>
													<th>Data Encerramento</th>
													<th>Privado</th>
													<th>Faltas</th>
												</tr>
											</thead>
											<tbody id="corpoTabela">
												<tr>
													<td colspan="8" class="text-center">
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
						<button type="button" class="btn btn-outline-secondary me-2" onclick="imprimirRelatorio()" id="btnImprimir">
							<i class="bi bi-printer"></i> Imprimir
						</button>
						<button type="button" class="btn btn-outline-success me-2" onclick="exportarExcel()">
							<i class="bi bi-file-earmark-excel"></i> Excel
						</button>
					</div>
				</div>

			</div><!-- Fecha container -->
		</div><!-- Fecha dashboard-container -->

	</body>
	
	<!-- DataTables JS -->
	<script type="text/javascript" src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="https://cdn.datatables.net/1.11.5/js/dataTables.bootstrap5.min.js"></script>
	
	<script>
		let tabelaDataTable;
		let dadosOriginais = [];
		
		// Intercepta erros de outros scripts para não quebrar nossa funcionalidade
		window.addEventListener('error', function(e) {
			console.log('Erro interceptado de script externo:', e.filename, e.message);
			// Não propaga o erro se for de notificacao.js ou outros scripts externos
			if (e.filename && (e.filename.includes('notificacao.js') || e.filename.includes('all.js'))) {
				e.preventDefault();
				return true;
			}
		});
		
		$(document).ready(function() {
			try {
				// Definir datas padrão (últimos 30 dias)
				const hoje = new Date();
				const trintaDiasAtras = new Date();
				trintaDiasAtras.setDate(hoje.getDate() - 30);
				
				$('#dataFim').val(hoje.toISOString().split('T')[0]);
				$('#dataInicio').val(trintaDiasAtras.toISOString().split('T')[0]);
				
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
			
			// Obter valores dos campos de data
			const dataInicio = $('#dataInicio').val();
			const dataFim = $('#dataFim').val();
			
			// Validar datas se ambas estiverem preenchidas
			if (dataInicio && dataFim && dataInicio > dataFim) {
				alert('A data de início deve ser anterior à data final.');
				$('#atualizarDados').prop('disabled', false);
				$('#atualizarDados').html('<i class="fas fa-sync-alt"></i> Buscar Dados');
				return;
			}
			
			let data = { 
				acao: 'buscarDados',
				dataInicio: dataInicio,
				dataFim: dataFim
			};
			
			console.log('Dados enviados para servlet:', data);
			
			$.ajax({
				url: '/MuralhaDigital/RelatorioPendenciasRegistroFato',
				type: 'POST',
				data: data,
				dataType: 'json',
				beforeSend: function(xhr) {
					console.log('=== ENVIANDO REQUISIÇÃO ===');
					console.log('URL:', '/MuralhaDigital/RelatorioPendenciasRegistroFato');
					console.log('Dados:', data);
					console.log('==========================');
				},
				success: function(dados, textStatus, xhr) {
					console.log('=== RESPOSTA RECEBIDA ===');
					console.log('Status HTTP:', xhr.status);
					console.log('Content-Type:', xhr.getResponseHeader('Content-Type'));
					console.log('Dados recebidos:', dados);
					console.log('Tipo dos dados:', typeof dados);
					console.log('É array?', Array.isArray(dados));
					console.log('Quantidade de registros:', dados ? dados.length : 0);
					console.log('========================');
					
					dadosOriginais = dados;
					preencherTabela(dados);
					
					// Mostrar botões de ação se houver dados
					if (dados && dados.length > 0) {
						document.getElementById('action-buttons').style.display = 'block';
					} else {
						document.getElementById('action-buttons').style.display = 'none';
					}
				},
				error: function(xhr, status, error) {
					console.error('=== ERRO NA REQUISIÇÃO ===');
					console.error('Status HTTP:', xhr.status);
					console.error('Status Text:', xhr.statusText);
					console.error('Error:', error);
					console.error('Response Text:', xhr.responseText);
					console.error('Content-Type:', xhr.getResponseHeader('Content-Type'));
					console.error('==========================');
					
					// Tentar parsear a resposta como JSON para ver se há erro do servidor
					try {
						let errorResponse = JSON.parse(xhr.responseText);
						console.error('Erro parseado do servidor:', errorResponse);
						alert('Erro do servidor: ' + (errorResponse.erro || 'Erro desconhecido'));
					} catch (e) {
						alert('Erro ao carregar dados do relatório. Status: ' + xhr.status + '. Verifique o console e os logs do servidor.');
					}
				},
				complete: function() {
					$('#atualizarDados').prop('disabled', false);
					$('#atualizarDados').html('<i class="fas fa-sync-alt"></i> Buscar Dados');
				}
			});
		}
		
		function preencherTabela(dados) {
			console.log('Preenchendo tabela com dados:', dados);
			
			// Destruir DataTable existente se houver
			if ($.fn.DataTable.isDataTable('#tabelaPendencias')) {
				console.log('Destruindo DataTable existente...');
				$('#tabelaPendencias').DataTable().clear().destroy();
			}
			
			let tableHtml = '<thead><tr>';
			tableHtml += '<th>ID</th>';
			tableHtml += '<th>Tipo</th>';
			tableHtml += '<th>Status</th>';
			tableHtml += '<th>Usuário</th>';
			tableHtml += '<th>Data Criação</th>';
			tableHtml += '<th>Data Encerramento</th>';
			tableHtml += '<th>Privado</th>';
			tableHtml += '<th>Faltas</th>';
			tableHtml += '</tr></thead><tbody>';
			
			if (dados && dados.length > 0) {
				console.log('Processando', dados.length, 'registros');
				dados.forEach(function(item, index) {
					console.log('Processando item', index, ':', item);
					tableHtml += '<tr>';
					tableHtml += '<td>' + (item.id || '') + '</td>';
					tableHtml += '<td>' + (item.tipo || '') + '</td>';
					tableHtml += '<td>' + (item.status || '') + '</td>';
					tableHtml += '<td>' + (item.usuario || '') + '</td>';
					tableHtml += '<td>' + formatarData(item.dataCriacao) + '</td>';
					tableHtml += '<td>' + formatarData(item.dataEncerramento) + '</td>';
					tableHtml += '<td>' + formatarPrivado(item.privado) + '</td>';
					tableHtml += '<td>' + (item.faltas || '') + '</td>';
					tableHtml += '</tr>';
				});
				
				tableHtml += '</tbody>';
				$('#tabelaPendencias').html(tableHtml);
				
				console.log('Tabela HTML criada com dados, inicializando DataTable...');
				
				// Aguardar um pouco antes de inicializar o DataTable
				setTimeout(function() {
					try {
						// Inicializa DataTable apenas quando há dados
						tabelaDataTable = $('#tabelaPendencias').DataTable({
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
							order: [[0, 'desc']],
							pageLength: 25,
							lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, "Todos"]],
							responsive: true
						});
						console.log('DataTable inicializado com sucesso');
					} catch (error) {
						console.error('Erro ao inicializar DataTable:', error);
					}
				}, 100);
			} else {
				console.log('Nenhum dado encontrado, exibindo tabela simples sem DataTable');
				tableHtml += '<tr><td colspan="8" class="text-center">Nenhum dado encontrado</td></tr>';
				tableHtml += '</tbody>';
				$('#tabelaPendencias').html(tableHtml);
				
				// Não inicializar DataTable quando não há dados
				console.log('Tabela simples criada sem DataTable para evitar erros');
			}
		}
		
		function formatarData(dataString) {
			if (!dataString) return '';
			
			try {
				let data = new Date(dataString);
				return data.toLocaleDateString('pt-BR') + ' ' + data.toLocaleTimeString('pt-BR');
			} catch (e) {
				return dataString;
			}
		}
		
		function formatarPrivado(privado) {
			if (privado === 1 || privado === '1' || privado === true || privado === 'true') {
				return '<span class="badge badge-sim">Sim</span>';
			} else if (privado === 0 || privado === '0' || privado === false || privado === 'false') {
				return '<span class="badge badge-nao">Não</span>';
			}
			return '';
		}
		
		function exportarExcel() {
			// Obter valores dos campos de data
			const dataInicio = $('#dataInicio').val();
			const dataFim = $('#dataFim').val();
			
			// Validar datas se ambas estiverem preenchidas
			if (dataInicio && dataFim && dataInicio > dataFim) {
				alert('A data de início deve ser anterior à data final.');
				return;
			}
			
			// Criar formulário temporário para enviar os parâmetros
			const form = document.createElement('form');
			form.method = 'POST';
			form.action = '/MuralhaDigital/RelatorioPendenciasRegistroFato';
			form.target = '_blank';
			form.style.display = 'none';
			
			// Adicionar parâmetro de ação
			const inputAcao = document.createElement('input');
			inputAcao.type = 'hidden';
			inputAcao.name = 'acao';
			inputAcao.value = 'exportarExcel';
			form.appendChild(inputAcao);
			
			// Adicionar parâmetros de data se preenchidos
			if (dataInicio) {
				const inputDataInicio = document.createElement('input');
				inputDataInicio.type = 'hidden';
				inputDataInicio.name = 'dataInicio';
				inputDataInicio.value = dataInicio;
				form.appendChild(inputDataInicio);
			}
			
			if (dataFim) {
				const inputDataFim = document.createElement('input');
				inputDataFim.type = 'hidden';
				inputDataFim.name = 'dataFim';
				inputDataFim.value = dataFim;
				form.appendChild(inputDataFim);
			}
			
			// Adicionar formulário ao DOM, submeter e remover
			document.body.appendChild(form);
			form.submit();
			document.body.removeChild(form);
		}
		
		// Função para imprimir relatório
		function imprimirRelatorio() {
			window.print();
		}
	</script>

</html>
