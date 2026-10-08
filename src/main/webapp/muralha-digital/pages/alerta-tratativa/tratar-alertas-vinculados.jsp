<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>GTW - Alertas Vinculados</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
	</head>
	
	<body class="homepage">
	
		<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 
		<%@ include file="modal-encerrar-monitorado.jsp" %>

		<div id="divSemAlertasPendentes" class="container">
			<div class="container">
				<div class="row">
					<div class="col-sm-12">
	      				<div class="mb-3">
							<h3 class="text-center"><strong>Alertas Vinculados</strong></h3>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-1"></div>
					<div class="col-sm-10">
						<p class="text-success text-center fs-4 p-4 fw-bold">
							Todos os alertas vinculados foram processados com sucesso!
						</p>
					</div>
					<div class="col-sm-1"></div>
				</div>
				<div class="row">
					<div class="col-sm-5"></div>
					<div class="col-sm-2">
						<div class="d-grid gap-2">
							<button id="btnFecharJanelaTratarVinculado" class="btn btn-success" type="button" onclick="FecharJanelaTratarAlertaVinculado();">FECHAR</button>
						</div>
					</div>
					<div class="col-sm-5"></div>
				</div>
			</div>
		</div>
		
		<div id="divTratamentoAlertasVinculados" class="container">
			<div class="container">
				<div class="row">
					<div class="col-sm-12">
	      				<div class="mb-3">
							<h3 class="text-center"><strong>Alertas Vinculados</strong></h3>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-1"></div>
					<div class="col-sm-10">
						<p class="text-danger" style="text-align: justify;">
							Alertas vinculados são alertas gerados através do mesmo cadastro de monitoramento da irregularidade confirmada. Através desta tela, você poderá tratar os alertas pendentes vinculados, alterando o status de forma centralizada, sem necessidade de tratar um a um todos os alertas.
						</p>
					</div>
					<div class="col-sm-1"></div>
				</div>
			</div>
			
			<div class="container">
				<div class="row">
					<div class="col-sm-1"></div>
					<div class="col-sm-10">
						<div class='container-fluid g-1 g-md-2 bg-gradient border border-2'>
							<div class="row">
								<div id="alertaVinculadoItens" class="col-sm-12 ps-4 pe-4"></div>
							</div>
							<div class="row">
								<div class="col-sm-12 ps-4 pe-4">
									<div class="row">
										<div class="col-sm-10"></div>
										<div class="col-sm-2 mt-2 p-0 pb-2">
											<div class="d-grid gap-2">
<!-- 												<button id="btnSalvarVinculados" class="btn btn-success" type="button" onclick="AbrirModalEncerrarMonitoramento();">TESTE</button> -->
												<button id="btnSalvarVinculados" class="btn btn-success" type="button" onclick="ProcessarAlertasVinculados();">SALVAR</button>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-sm-1"></div>
				</div>
			</div>
		</div>
		<div class="overlay"></div>
	</body>
	
	<script src="js/tratar-alertas-vinculados.js"></script>
	<script src="js/modal-encerrar-monitorado.js"></script>

</html>