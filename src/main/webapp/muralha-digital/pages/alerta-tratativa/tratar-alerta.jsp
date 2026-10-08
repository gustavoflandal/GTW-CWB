<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include
	file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GTW - Alerta</title>
<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="stylesheet" href="css/tratar-alerta.css">
<link rel="stylesheet"
	href="/muralha-digital/assets/css/pagina-carregando.css">

<script src="js/tratar-alerta.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>

<body class="homepage">

	<%@ include file="/muralha-digital/utils/modal-info-alert.jsp"%>
	<%@ include file="modal-alerta-anotacao.jsp"%>
	<%@ include file="modal-alerta-acao.jsp"%>
	<%@ include file="modal-alerta-descartar.jsp"%>
	<%@ include file="modal-ocorrencia-finalizar.jsp"%>
	<%@ include
		file="/muralha-digital/pages/linha-tempo/modal-linha-tempo.jsp"%>
	<%@ include
		file="/muralha-digital/pages/mapa-passagens/modal-visualizar-mapa.jsp"%>
	<%@ include
		file="/muralha-digital/pages/monitorado/modal-editar-monitorado.jsp"%>
	<%@ include
		file="/muralha-digital/pages/perguntasRespostas/historico-perguntas-respostas/modal-historico-questionario.jsp"%>
	<div class="container">
		<div class="row">
			<div class="col-sm-12">
				<!--       				<div class="mb-3"> -->
				<h3 class="text-center">
					<strong>Tratativa de Alerta</strong>
				</h3>
				<!-- 					</div> -->
			</div>
		</div>
	</div>

	<div class="container">
		<div class="row">
			<div class="col-sm-1">
				<div id="btnVoltar" class="col-sm-12 text-center"
					style="display: none;">
					<button class="btn btn-sm btn-warning" onclick="Voltar()">Voltar</button>
				</div>
			</div>
			<div class="col-sm-10">
				<div class="container bg-light bg-gradient border border-2">
					<div class="row">
						<div class="p-1"></div>
					</div>
					<div class="row mb-2 mt-2">
						<div class="col-sm-1"></div>
						<div id="controleGaleriaPrev" class="col-sm-1"></div>
						<div class="col-sm-8">
							<div class="text-center">
								<div id="galeriaImagensAlerta"
									class="carousel slide carousel-fade" data-bs-interval="false">
								</div>
							</div>
							<div class="controls mt-2">
								<div class="slider-container">
									<label for="brightness">Brilho</label> <input type="range"
										id="brightness" min="0" max="200" value="100">
								</div>
								<div class="slider-container">
									<label for="contrast">Contraste</label> <input type="range"
										id="contrast" min="0" max="200" value="100";>
								</div>
							</div>

						</div>
						<div id="controleGaleriaNext" class="col-sm-1"></div>
						<div
							class="col-sm-1 pr-0 d-flex align-items-end jus position-relative justify-content-end">
							<div id="containerAssinatura"
								class="conteudo-flutuante slider-container border-warning rounded nao-assinado position-relative">
								<div class="form-check d-inline-flex align-items-center">
									<input class="form-check-input" type="checkbox"
										onchange="confirmarAssinatura(this)"
										id="checkCienciaTratativa"> <label
										class="form-check-label fw-bold label-outside"
										for="checkCienciaTratativa"> <i
										class="bi bi-pen-fill me-1"></i> Assinar Tratativa
									</label>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div
					class="container g-1 g-md-1 bg-light bg-gradient border border-2">
					<div class="row">
						<div class="col-sm-4">
							<h5 class="mx-1">
								<strong>Informações dos Veículos</strong>
							</h5>
						</div>
						<div class="col-sm-8 text-end">
							<button id="btnAbrirModalVeiculoMonitorado" type="button"
								title='Detalhar Monitoramento' class="btn btn-warning mt-1"
								onclick="AbrirDetalharVeiculoMonitorado()">
								<i class="fa fa-list-alt" aria-hidden="true"></i>
							</button>
							<button id="btnAbrirLinhaTempo" type="button"
								class="btn btn-secondary mt-1" data-bs-toggle="modal"
								data-bs-target="#linhaTempo"
								title="Ver linha do tempo do alarme"
								onclick="AbrirLinhaTempoAlarme()">
								<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
									fill="currentColor" class="bi bi-clock-history"
									viewBox="0 0 16 16"> <path
									d="M8.515 1.019A7 7 0 0 0 8 1V0a8 8 0 0 1 .589.022zm2.004.45a7 7 0 0 0-.985-.299l.219-.976q.576.129 1.126.342zm1.37.71a7 7 0 0 0-.439-.27l.493-.87a8 8 0 0 1 .979.654l-.615.789a7 7 0 0 0-.418-.302zm1.834 1.79a7 7 0 0 0-.653-.796l.724-.69q.406.429.747.91zm.744 1.352a7 7 0 0 0-.214-.468l.893-.45a8 8 0 0 1 .45 1.088l-.95.313a7 7 0 0 0-.179-.483m.53 2.507a7 7 0 0 0-.1-1.025l.985-.17q.1.58.116 1.17zm-.131 1.538q.05-.254.081-.51l.993.123a8 8 0 0 1-.23 1.155l-.964-.267q.069-.247.12-.501m-.952 2.379q.276-.436.486-.908l.914.405q-.24.54-.555 1.038zm-.964 1.205q.183-.183.35-.378l.758.653a8 8 0 0 1-.401.432z" />
								<path
									d="M8 1a7 7 0 1 0 4.95 11.95l.707.707A8.001 8.001 0 1 1 8 0z" />
								<path
									d="M7.5 3a.5.5 0 0 1 .5.5v5.21l3.248 1.856a.5.5 0 0 1-.496.868l-3.5-2A.5.5 0 0 1 7 9V3.5a.5.5 0 0 1 .5-.5" />
								</svg>
							</button>
							<button id="btnAbrirMapaPassagens" type="button"
								class="btn btn-info mt-1" title="Perfil Comportamental"
								onclick="AbrePerfilComportamental()">
								<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
									fill="currentColor" class="bi bi-graph-up" viewBox="0 0 16 16">
									<path fill-rule="evenodd"
										d="M0 0h1v15h15v1H0V0zm10.293 3.707a1 1 0 0 1
										1.414 0l2.5 2.5a1 1 0 0 1-1.414 1.414L11
										6.414l-2.793 2.793a1 1 0 0 1-1.414
										0L4 6.414l-.793.793a1 1 0 0 1-1.414-1.414l2-2a1
										1 0 0 1 1.414 0L8 6.586l2.293-2.879z"/>
								</svg>
							</button>
							<button id="btnAbrirHistorico" type="button"
								class="btn btn-secondary mt-1"
								title="Histórico do Questionário"
								onclick="buscarRespostasQuestionarios()">
								<i class="fa fa-clipboard-list"></i>
							</button>
							<button id="btnAbrirMapaPassagens" type="button" class="btn btn-success mt-1" data-bs-toggle="modal" data-bs-target="#modalVisualizarMapa"title="Ver passagem no mapa" onclick="abreMapa()">
					            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-map" viewBox="0 0 16 16">
					                <path fill-rule="evenodd" d="M15.817.113A.5.5 0 0 1 16 .5v14a.5.5 0 0 1-.402.49l-5 1a.502.502 0 0 1-.196 0L5.5 15.01l-4.902.98A.5.5 0 0 1 0 15.5v-14a.5.5 0 0 1 .402-.49l5-1a.5.5 0 0 1 .196 0L10.5.99l4.902-.98a.5.5 0 0 1 .415.103zM10 1.91l-4-.8v12.98l4 .8V1.91zm1 12.98 4-.8V1.11l-4 .8v12.98zm-6-.8V1.11l-4 .8v12.98l4-.8z"></path>
					            </svg>
				            </button>
						</div>
					</div>
					<div class="row">
						<div class="table-responsive">
							<table id="tabelaInfoAlerta"
								class="table table-borderless table-light">
								<thead>
									<tr>
										<th scope="col"><small>Local</small></th>
										<th scope="col"><small>Data da Passagem</small></th>
										<th scope="col"><small>Faixa</small></th>
										<th scope="col"><small>Placa</small></th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
						</div>
					</div>
				</div>

				<div
					class="container-fluid g-1 g-md-2 bg-light bg-gradient border border-2">
					<div class="row">
						<div class="col-sm-3 text-start">
							<div class="row">
								<label class="col-form-label"><small><strong>Tipo
											do Alerta</strong></small></label>
							</div>
							<div class="row">
								<label id="infoTipoAlerta" class="col-form-label"></label>
							</div>
						</div>
						<div class="col-sm-2 text-center">
							<div class="row">
								<label class="col-form-label"><small><strong>Data
											do Alerta</strong></small></label>
							</div>
							<div class="row">
								<label id="infoDataAlerta" class="col-form-label"></label>
							</div>
						</div>
						<div class="col-sm-2 text-center">
							<div class="row">
								<label class="col-form-label"><small><strong>Placa
											Monitorada</strong></small></label>
							</div>
							<div class="row">
								<label id="infoPlacaMonitorada" class="col-form-label"></label>
							</div>
						</div>
						<div class="col-sm-3 text-center">
							<div class="row">
								<label class="col-form-label"><small><strong>Ponto
											de Interesse</strong></small></label>
							</div>
							<div class="row">
								<label id="infoPontoInteresse" class="col-form-label"></label>
							</div>
						</div>
						<div class="col-sm-2 text-center">
							<div class="row">
								<label class="col-form-label"><small><strong>Status</strong></small></label>
							</div>
							<div class="row">
								<label id="infoStatusAlerta" class="col-form-label"></label>
							</div>
						</div>
					</div>
				</div>

				<div class="container bg-light bg-gradient border border-2">
					<div class="row">
						<div class="col-sm-12">
							<h5 class="text-center">
								<strong>Ações</strong>
							</h5>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-4">
							<div class="mb-2">
								<div class="d-grid gap-2">
									<button id="btnGerarOcorrencia" class="btn btn-success"
										type="button" onclick="GerarOcorrencia()">CONFIRMAR
										IRREGULARIDADE</button>
									<button id="btnAbrirModalFinalizarOcorrencia"
										style="display: none;" class="btn btn-success" type="button"
										data-bs-toggle="modal"
										data-bs-target="#modalOcorrenciaFinalizar"
										onclick="AbrirModalFinalizarOcorrencia()">FINALIZAR
										IRREGULARIDADE</button>
								</div>
							</div>
						</div>
						<div class="col-sm-2">
							<div class="mb-2">
								<div class="d-grid gap-2">
									<button id="btnAbrirModalDescartar" type="button"
										class="btn btn-danger" data-bs-toggle="modal"
										data-bs-target="#modalAlertaDescartar"
										onclick="PopulaModalMotivoDescarte()">DESCARTAR</button>
								</div>
							</div>
						</div>
						<!-- 						</div> -->
						<!-- 						<div class="row"> -->
						<div class="col-sm-3">
							<div class="mb-2">
								<div id="tooltipAcaoProcedimento" class="d-grid gap-2"
									data-bs-toggle="tooltip" data-bs-placement="auto"
									data-bs-original-title="A inclusão de Ações e Procedimentos será habilitada após a Confirmar a Irregularidade">
									<button id="btnAbrirModalAcaoProcedimento" type="button"
										class="btn btn-primary" data-bs-toggle="modal"
										data-bs-target="#modalAlertaAcaoProcedimento"
										onclick="PopulaModalAcaoProcedimento()" disabled>AÇÕES
										DO SUPERVISIONADO</button>
								</div>
							</div>
						</div>
						<div class="col-sm-3">
							<div class="mb-2">
								<div class="d-grid gap-2">
									<button id="btnAbrirModalAnotacaoContrib" type="button"
										class="btn btn-primary" data-bs-toggle="modal"
										data-bs-target="#modalAlertaAnotacao"
										onclick="ObterAnotacaoContributiva()">ANOTAÇÕES
										CONTRIBUTIVAS</button>
								</div>
							</div>
						</div>
					</div>
					<div class="row mt-2">
						<div class="col-sm-12">
							<div class="mb-2">
								<div class="d-grid gap-2">
									<button id="btnVerUltimasPassagens" type="button"
										class="btn btn-info" onclick="AbrirUltimasPassagens()">
										<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
											fill="currentColor" class="bi bi-car-front-fill"
											viewBox="0 0 16 16"> <path
											d="M2.52 3.515A2.5 2.5 0 0 1 4.82 2h6.362c1 0 1.904.596 2.298 1.515l.792 1.848c.075.175.21.319.38.404.5.25.855.715.965 1.262l.335 1.679c.033.161.049.325.049.49v.413c0 .814-.39 1.543-1 1.997V13.5a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1-.5-.5v-1.338c-1.292.048-2.745.088-4 .088s-2.708-.04-4-.088V13.5a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1-.5-.5v-1.892c-.61-.454-1-1.183-1-1.997v-.413a2.5 2.5 0 0 1 .049-.49l.335-1.68c.11-.546.465-1.012.964-1.261a.807.807 0 0 0 .381-.404l.792-1.848ZM3 10a1 1 0 1 0 0-2 1 1 0 0 0 0 2Zm10 0a1 1 0 1 0 0-2 1 1 0 0 0 0 2ZM6 8a1 1 0 0 0 0 2h4a1 1 0 1 0 0-2H6ZM2.906 5.189a.51.51 0 0 0 .497.731c.91-.073 3.35-.17 4.597-.17 1.247 0 3.688.097 4.597.17a.51.51 0 0 0 .497-.731l-.956-1.913A.5.5 0 0 0 11.691 3H4.309a.5.5 0 0 0-.447.276L2.906 5.19Z" />
										</svg>
										VER ÚLTIMAS 10 PASSAGENS
									</button>
								</div>
							</div>
						</div>
					</div>

					<div class="row mt-2">
						<div class="col-sm-12">
							<div class="mb-2">
								<div class="d-grid gap-2">
									<button id="btnCriarAbordagem" type="button"
										class="btn btn-warning" onclick="criarAbordagem()" style="display: none;">
										CRIAR ABORDAGEM
									</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-sm-1"></div>
		</div>
	</div>
	<div class="overlay"></div>

	<script>
			document.addEventListener("DOMContentLoaded", function(){
				const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
				const popoverList = [...popoverTriggerList].map(el => new bootstrap.Popover(el));
			});
		</script>
</body>

</html>