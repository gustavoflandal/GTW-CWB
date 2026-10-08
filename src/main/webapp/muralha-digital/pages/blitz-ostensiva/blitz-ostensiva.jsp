<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>
<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 
<%@ include file="modal-passagens-relacionadas.jsp" %>

<html lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Muralha Digital - Blitz Ostensiva</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">

		<link rel="stylesheet" href="/muralha-digital/pages/blitz-ostensiva/assets/css/blitz-ostensiva.css">
		<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">

		<%@ include file="../consulta-veiculo/modal-detalhe-veiculo.jsp" %>
	</head>

	<%@page import="com.consilux.model.Acesso"%> 
	<%
		Acesso acesso = new Acesso(request, response, true);
		if (!acesso.verificaAcesso()) return;
		String usuarioID = acesso.getUsuario().getId().toString();
		boolean MODO_FAKE_WEBSOCKET = false;
	%>

	<script>
		var urlRoot 	= "${root}";
		var usuarioID 	= "<%= usuarioID %>";
	</script>

	<body class="homepage">

		<div class="container-fluid mb-2">

			<div class="row pt-1 align-items-center">
				<div class="col-2 col-md-1">
					<button class="btn btn-outline-secondary btn-sm w-100" id="btnVoltar">
						<i class="bi bi-arrow-left"></i>
					</button>
				</div>

				<div class="col-10 col-md-11">
					<span class="p-1 badge bg-secondary text-white text-center rounded w-100 d-block">
						<p class="h5 pt-1 mb-0">
							<strong id="tituloPagina">BLITZ OSTENSIVA</strong>
						</p>
					</span>
				</div>
			</div>

			<div class="row pt-2 align-items-end">

				<div class="col-md-4">
					<p class="mb-1">
						<strong>Escolha a blitz:</strong>
					</p>
					<select id="selectBlitz" class="form-select form-select-sm">
						<option value="">Selecione uma blitz</option>
					</select>
				</div>

				<div class="col-md-4">
					<p class="mb-1">
						<strong>Filtro:</strong>
					</p>
					<div class="btn-group w-100" role="group" id="filtroIrregularidade">
						<input type="radio"
							class="btn-check"
							name="filtroPassagens"
							id="filtroTodos"
							autocomplete="off"
							checked>
						<label class="btn btn-outline-secondary btn-sm" for="filtroTodos">
							Mostrar todos
						</label>

						<input type="radio"
							class="btn-check"
							name="filtroPassagens"
							id="filtroIrregulares"
							autocomplete="off">
						<label class="btn btn-outline-danger btn-sm" for="filtroIrregulares">
							Somente irregulares
						</label>

						<input type="radio"
							class="btn-check"
							name="filtroPassagens"
							id="filtroInteresse"
							autocomplete="off">
						<label class="btn btn-outline-warning btn-sm" for="filtroInteresse">
							<i class="bi bi-star-fill"></i> Veículos de interesse
						</label>
					</div>
				</div>

				<div class="col-md-2 d-grid">
					<button type="button"
							class="btn btn-success btn-sm mt-4"
							id="iniciar"
							onclick="DivInit_WebSocketInit()">
						INICIAR
					</button>
				</div>

				<% if (MODO_FAKE_WEBSOCKET) { %>
					<div class="row mt-2">
						<div class="col-md-12 d-grid">
							<button class="btn btn-outline-danger"
									type="button"
									onclick="simularMensagemWebSocketRandom()">
								SIMULAR PASSAGENS (FAKE)
							</button>
						</div>
					</div>
					<div class="row mt-2">
						<div class="col-md-12 d-grid">
							<button class="btn btn-outline-primary"
									type="button"
									onclick="simularPassagensReais()">
								SIMULAR PASSAGENS REAIS
							</button>
						</div>
					</div>
				<% } %>
			</div>

			<div id="mensagemAguardando"
				 class="text-center mt-4"
				 style="display:none;">
				<h5>Aguardando passagens veiculares...</h5>
				<p>Os veículos aparecerão aqui automaticamente</p>
			</div>

		</div>

		<div class="overlay"></div>

		<div class="container-fluid">
			<div id="cardsVeiculos"
				 class="row d-flex justify-content-center">
			</div>
		</div>

		<script src="/muralha-digital/pages/blitz-ostensiva/assets/js/blitz-ostensiva.js"></script>

		<div class="modal fade"
			 id="modalAcoesBlitz"
			 tabindex="-1"
			 aria-labelledby="modalAcoesBlitzLabel"
			 aria-hidden="true">
	</body>
</html>
