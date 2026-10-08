<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include
	file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<title>Dashboard - Gestão Muralha</title>

<!-- Select -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css"
	integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q=="
	crossorigin="anonymous" referrerpolicy="no-referrer" />

<!-- Flatpickr -->
<link
	href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css"
	rel="stylesheet">

<!-- Ícones Bootstrap -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

<!-- CSS Custom -->
<link href="css/painel-informacoes.css" rel="stylesheet">
</head>
<body>

	<header
		class="dashboard-header d-flex flex-column flex-md-row align-items-center justify-content-between p-3 mb-4 shadow-sm">
		<div class="d-flex align-items-center">
			<i class="bi bi-speedometer2 fs-2 me-3 text-primary"></i>
			<div>
				<h1 class="h4 mb-0 fw-bold">Painel de Informações</h1>
				<small class="text-muted">Dashboard</small>
			</div>
		</div>
	</header>

	<div class="container my-4">

		<!-- ROW 1: Percentual de Leitura de Placas + Recursos do Servidor -->
		<div class="row g-4 d-flex align-items-stretch mb-4">

			<!-- Percentual de Leitura de Placas -->
			<div class="col-md-6 d-flex">
				<div id="cardLeituraPlacas"
					class="card shadow-sm p-3 flex-fill d-flex flex-column">

					<!-- Overlay de Loading -->
					<div
						class="loading-overlay justify-content-center align-items-center">
						<div class="spinner-border text-primary" role="status">
							<span class="sr-only">Carregando...</span>
						</div>
					</div>

					<h5>Percentual de Leitura de Placas</h5>
					<form id="filtroIndicadores" class="row g-2 mb-3 align-items-end">
						<div class="col-md-6">
							<label class="form-label">Data e Hora Inicial</label> <input
								type="text" id="datetimepickerInicial" class="form-control"
								placeholder="DD/MM/YYYY HH:mm">
						</div>
						<div class="col-md-6">
							<label class="form-label">Data e Hora Final</label> <input
								type="text" id="datetimepickerFinal" class="form-control"
								placeholder="DD/MM/YYYY HH:mm">
						</div>
						<div class="col-md-7">
							<label class="form-label">Câmeras</label> <select id="cameras"
								class="selectpicker form-control form-control-sm bg-light border-dark text-dark fw-bold"
								multiple data-live-search="true" data-actions-box="true"
								data-size="5" data-style="form-select-sm"
								data-none-selected-text="Nenhuma câmera selecionada"
								data-select-all-text="Marcar todas"
								data-deselect-all-text="Desmarcar todas" data-container="body"
								style="z-index: 2000">
							</select>
						</div>
						<div class="col-md-5 d-flex">
							<button type="button" onclick="buscarPercentualPlacas()"
								id="btnFiltrar" class="btn btn-primary me-2 w-50">Filtrar</button>
							<button type="button" onclick="limparFiltrosPlacas()"
								id="btnLimpar" class="btn btn-warning w-50">Limpar</button>
						</div>
					</form>
					<div style="overflow-x: auto; height: 300px;">
						<canvas id="chartPlacas"></canvas>
					</div>
					<ul id="listaPercentuais" class="mt-3 list-group"
						style="max-height: 160px; overflow-y: auto;"></ul>
				</div>
			</div>

			<!-- Recursos do Servidor -->
			<div class="col-md-6 d-flex">
				<div
					class="card shadow-sm p-3 flex-fill d-flex flex-column position-relative"
					id="recursoServidorCard">
					<!-- Overlay de Loading -->
					<div
						class="loading-overlay justify-content-center align-items-center">
						<div class="spinner-border text-primary" role="status">
							<span class="sr-only">Carregando...</span>
						</div>
					</div>

					<h5>Gestão de Armazenamento do Servidor</h5>

					<!-- Capacidade de Armazenamento -->
					<div class="mb-2 d-flex justify-content-between">
						<span>Capacidade de Armazenamento</span> <span
							id="capacidadeArmazenamento">--</span>
					</div>
					<div class="progress mb-3">
						<div class="progress-bar bg-primary" id="progressCapacidade"
							style="width: 0%">0%</div>
					</div>

					<!-- Quantidade de Imagens -->
					<div class="mb-2 d-flex justify-content-between">
						<span>Quantidade de Imagens</span> <span id="totalImagens">--</span>
					</div>

					<!-- Quantidade de Dias Armazenados -->
					<div class="mb-2 d-flex justify-content-between">
						<span>Quantidade de Dias Armazenados</span> <span
							id="diasArmazenados">--</span>
					</div>

					<!-- Passagens Veiculares Registradas -->
					<div class="mb-2 d-flex justify-content-between">
						<span>Passagens Veiculares Registradas</span> <span
							id="totalPassagens">--</span>
					</div>
				</div>
			</div>

		</div>

		<!-- ROW 2: Total de Informações + Notificações -->
		<div class="row g-4 d-flex align-items-stretch mb-4">

			<!-- Total de Informações -->
			<div class="col-12 d-flex flex-column">
				<div id="cardTotalInforacoes"
					class="card shadow-sm p-3 flex-fill d-flex flex-column">

					<!-- Overlay de Loading -->
					<div
						class="loading-overlay justify-content-center align-items-center">
						<div class="spinner-border text-primary" role="status">
							<span class="sr-only">Carregando...</span>
						</div>
					</div>

					<h5>Total de Informações</h5>
					<div class="btn-group mb-3" role="group">
						<button type="button"
							class="btn btn-outline-primary filtro-periodo active"
							data-periodo="24">Últimas 24h</button>
						<button type="button"
							class="btn btn-outline-primary filtro-periodo" data-periodo="48">Últimas
							48h</button>
					</div>
					<div style="height: 300px;">
						<canvas id="chartPainelInfo"></canvas>
					</div>

					<!-- Painel de Totalizações -->
					<div class="row row-cols-1 row-cols-md-3 row-cols-xl-4 g-3 mt-2"
						id="painelTotalizacoes"></div>
				</div>
			</div>

		</div>

		<!-- Notificações Importantes -->
		<div id="cardNotificacaoImportante" class="card shadow-sm p-3 flex-fill mt-3">
			<!-- Overlay de Loading -->
			<div
				class="loading-overlay justify-content-center align-items-center">
				<div class="spinner-border text-primary" role="status">
					<span class="sr-only">Carregando...</span>
				</div>
			</div>
			<div class="d-flex justify-content-between align-items-center mb-2">
				<h5 class="mb-0">Notificações Importantes</h5>
				<!--<button class="btn btn-sm btn-outline-secondary"
								onclick="abrirHistoricoNotificacoes()">Ver todas</button>-->
			</div>
			<div id="notificacoesContainer" class="list-group"
				style="max-height: 250px; overflow-y: auto;">
				<!-- JS insere notificações aqui -->
			</div>
		</div>
	</div>

	<!-- Scripts -->
	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js"
		integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw=="
		crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
	<script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/pt.js"></script>
	<script src="js/painel-informacoes.js"></script>
	<script src="js/consulta.js"></script>
	<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>

	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</body>
</html>