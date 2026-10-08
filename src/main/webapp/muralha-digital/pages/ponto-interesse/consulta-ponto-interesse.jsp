<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>GTW - Consulta de Pontos de Interesse</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
	</head>
	
	<body class="homepage">
		<div class="container">
			<div class="row gy-3">
				<div class="col-sm-12">
      				<div class="mb-3">
						<h2 class="text-center"><strong>Consulta de Pontos de Interesse</strong></h2>
					</div>
				</div>
			</div>
		</div>

		<div class="container">
			<div class="row gy-3">
				<div class="col-sm-2">
      				<div class="mb-3">
						<div class="d-grid gap-2">
							<button type="button" class="btn btn-success" onClick="javascript:window.location.href='cadastro-ponto-interesse.jsp'">CADASTRAR</button>
						</div>
					</div>
				</div>
           	</div>
           
			<br>
		
			<div class="row">
				<div class="col-sm-12">
      				<div class="mb-3">
				  		<h5><small>Lista de pontos de interesse:</small></h5>
			            <div class="form-group input-group">
							<span class="input-group-text">
								<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
			  						<path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"></path>
								</svg>
							</span>
							<input name="consulta" id="txt_consulta" placeholder="Consultar" type="text" class="form-control">
						</div>
					</div>
				  	<div class="table-responsive">         
						<table id="tabela" class="table table-bordered table-hover">
							<thead class="table-secondary">
								<tr>
							        <th scope="col"><small>Nome</small></th>
							        <th scope="col"><small>Tipo</small></th>
							        <th scope="col"><small>Latitude</small></th>
							        <th scope="col"><small>Longitude</small></th>
							        <th scope="col"><small>Equipamentos</small></th>
							        <th scope="col"><small>Cód. Órgão</small></th>
								</tr>
						    </thead>
							<tbody></tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		
		<script src="assets/js/consulta-ponto-interesse.js"></script>
	</body>

</html>