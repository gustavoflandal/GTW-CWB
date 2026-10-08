<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">
  	<head>
		<title>Gráfico de Veículos por Período</title> 	
	  	
	  	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  	<meta http-equiv="x-ua-compatible" content="ie=edge">
	  	
    	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
    	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	
		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
  	</head>
  
	<body>
	
		<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 
		
		<div class="container">
			<div class="row gy-3">
				<div class="col-sm-12">
      				<div class="mb-3">
						<h2 id="tituloTelaConsulta" class="text-center"><strong>Gráfico de Veículos por Período</strong></h2>
					</div>
				</div>
			</div>
		</div>
		
		<div id="divFiltrosConsulta" class="container">
		
			<div class="row gy-3">
				<div class="col-sm-2">
      				<div class="mb-3">
			            <div class="form-group">
			            	<label for="dataInicio" class='form-label'>Data Inicio:</label>
							<div class='input-group' id='dataInicio' data-td-target-input='nearest' data-td-target-toggle='nearest'>
							  	<input id='dataInicioInput' type='text' class='form-control' data-td-target='#dataInicio' readonly/>
							   	<span class='input-group-text' data-td-target='#dataInicio' data-td-toggle='datetimepicker'>
							     	<span class='fas fa-calendar'></span>
							   	</span>
							</div>
			            </div>
		            </div>
		        </div>
		        <div class="col-sm-2">
      				<div class="mb-3">
			            <div class="form-group">
			            	<label for="dataFim" class='form-label'>Data Fim:</label>
							<div class='input-group log-event' id='dataFim' data-td-target-input='nearest' data-td-target-toggle='nearest'>
							  	<input id='dataFimInput' type='text' class='form-control' data-td-target='#dataFim' readonly/>
							   	<span class='input-group-text' data-td-target='#dataFim' data-td-toggle='datetimepicker'>
							     	<span class='fas fa-calendar'></span>
							   	</span>
							</div>
			            </div>
		            </div>
		        </div>
		        <div class="col-sm-5">
		        	<div class="mb-3">
	           			<div class="form-group">
			                <label for="selEquipamento" class='form-label'>Equipamento:</label>
			                <select class="selectpicker form-control border" data-live-search="true" id="selEquipamento" name="equipamento" data-style="btn-white" data-size="20" data-none-selected-text="--Todos os equipamentos--">
			                    <option value="0" selected="selected">--Todos os Equipamentos--</option>
			                </select>
		                </div>
	                </div>
		        </div>
		        <div class="col-sm-2">
		        	<div class="mb-3">
	           			<div class="form-group">
			                <label for="selTipo" class='form-label'>Tipo:</label>
			                <select class="form-select" id="selTipo" name="tipoPeriodo">
			                    <option value="1" selected="selected">Minuto</option>
			                    <option value="2">Hora</option>
			                    <option value="3">Dia</option>
			                </select>
		                </div>
	                </div>
		        </div>
		        <div class="col-sm-1">
      				<div class="mb-3">
						<div class="d-grid gap-4">
							<label for="btnProcessar" class='form-label'></label>
							<button id="btnProcessar" type="button" class="btn btn-success" onclick="ValidarObterDadosGraficoVeiculos();">PROCESSAR</button>
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
  
	<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
	<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>
	<script src="js/carregar-combos-filtros-relatorio.js"></script>
   	<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.6.2/chart.min.js"></script>
 	<script src="https://cdnjs.cloudflare.com/ajax/libs/chartjs-plugin-datalabels/2.0.0/chartjs-plugin-datalabels.min.js"></script>
  	<script src="js/grafico-utilidades.js"></script>
  	<script src="js/grafico-veiculos.js"></script>

</html>