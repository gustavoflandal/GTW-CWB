<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>GTW - Alertas</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
	</head>
	
	<body class="homepage">
	
		<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 

		<div id="divListaAlertas" class="container">
			<div class="container">
				<div class="row">
					<div class="col-sm-12">
	      				<div class="mt-1">
							<h4 class="text-center"><strong>Alertas do Veículo Monitorado</strong></h4>
						</div>
					</div>
				</div>
			</div>
			
			<div class="container">
				<div class="row">
					<div class="col-sm-1"></div>
					<div class="col-sm-10">
						<div class='container-fluid g-1 g-md-2 bg-gradient border border-2'>
							<div class="row mt-1">
								<div class="col-sm-10">
									<div class="input-group input-group-sm">
										<span class="input-group-text bg-transparent">Nome</span>
										<input id="nomeMonitoradoVerAlerta" type="text" maxlength="85" class="form-control text" disabled>
									</div>
								</div>
						        <div class="col-sm-2">
						            <div class="btn-group d-flex justify-content-center">
						            	<button id="cadastroAtivoVerAlerta" disabled class="btn btn-sm btn-light">SITUAÇÃO</button>
						            </div>
						        </div>
							</div>
							<div class="row mt-1">
								<div class="col-sm-3">
			      					<div class="input-group input-group-sm">
					                	<span class="input-group-text bg-transparent">Placa</span>
										<input id="placaVerAlerta" type="text" maxlength="7" class="form-control item text-uppercase" disabled>
									</div>
				                </div>
				           		<div class="col-sm-9">
				           			<div class="input-group input-group-sm">
						                <span class="input-group-text bg-transparent">Tipo Alerta/Ocorrência</span>
						                <input id="tipoAlertaVerAlerta" type="text" class="form-control item" disabled>
					                </div>
				                </div>
							</div>
							<div class="row">
								<div id="listaAlertaItens" class="col-sm-12 ps-4 pe-4"></div>
							</div>
							<div class="row">
								<div class="col-sm-12 pb-0">
									<div class="row mt-2 mb-0">
										<div class="col-sm-1"></div>
										<div class="col-sm-10">
											<%@ include file="/muralha-digital/utils/paginacao/componente-paginacao.jsp" %>
										</div>
										<div class="col-sm-1"></div>
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
	
	<script src="js/visualizar-alertas.js"></script>

</html>