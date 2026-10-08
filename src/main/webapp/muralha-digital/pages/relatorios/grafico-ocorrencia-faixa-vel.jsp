<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">
  	<head>
		<title>Gráfico de Distribuição Ocorrência por Faixa de Velocidade</title> 	
	  	
	  	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  	<meta http-equiv="x-ua-compatible" content="ie=edge">
	  	
    	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">    
  	</head>
  
	<body>
	
		<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 
		
		<div class="container">
			<div class="row gy-3">
				<div class="col-sm-12">
      				<div class="mb-3">
						<h2 id="tituloTelaConsulta" class="text-center"><strong>Gráfico de Distribuição Ocorrência por Faixa de Velocidade</strong></h2>
					</div>
				</div>
			</div>
		</div>
		
		<div id="divFiltrosConsulta" class="container">
		
			<div class="row gy-3">
           		<%@ include file="_filtros_graficos.jsp" %>
		        <div class="col-sm-1">
      				<div class="mb-3">
						<div class="d-grid gap-4">
							<label for="btnProcessar" class='form-label'></label>
							<button id="btnProcessar" type="button" class="btn btn-success" onclick="ObterDadosGrafico('ocorrenciaFaixaVel')">PROCESSAR</button>
						</div>
					</div>
		        </div>
			</div>
        </div>
  
	    <!-- --------------------------------------------------------------------------------- -->
	    <!-- Exemplos de Charts.js -->
	    <!-- https://ordinarycoders.com/blog/article/11-chart-js-examples -->
	    <!-- https://tobiasahlin.com/blog/chartjs-charts-to-get-you-started/#9-mixed-chart -->
	    <!-- --------------------------------------------------------------------------------- -->
		<div class="container">
			<div class="row">
	      		<div class="col-md-1"></div>
				<div class="col-md-10">
		      		<div class="thumbnail">
		      			<canvas id="chartGrafico"></canvas>
		      		</div>
	      		</div>
	      		<div class="col-md-1"></div>
			</div>
		</div>

  	</body>
  
  	<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>
	<script src="js/carregar-combos-filtros-relatorio.js"></script>
   	<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
   	<script src="js/grafico-utilidades.js"></script>
  	<script src="js/grafico-ocorrencia-faixa-vel.js"></script>

</html>