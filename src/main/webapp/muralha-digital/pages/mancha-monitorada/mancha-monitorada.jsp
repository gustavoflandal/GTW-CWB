<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">
	<head>
		<title>Mapa de rota do veículo</title> 	
    	<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk&libraries=geometry"></script>

		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta http-equiv="x-ua-compatible" content="ie=edge">
	  	
		<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
		<link rel="stylesheet" href="/muralha-digital/pages/mancha-monitorada/assets/css/perfil-comportamental-mapa.css">
		<link rel="stylesheet" href="/muralha-digital/pages/mancha-monitorada/assets/css/mancha-monitorada.css">
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />

		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    	<script src="/muralha-digital/pages/mancha-monitorada/assets/js/mapa.js"></script>
    	
    	<link rel="stylesheet" href="/muralha-digital/pages/consulta-alerta-ocorrencia/css/consulta.css">
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.47/css/bootstrap-datetimepicker.min.css">
		<link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css" />
    	<style>
	      #map-canvas {
	        height: 100vh;
	        width: 100%;
	      }
   		</style>
	</head>
	
	<body>	
         <div class="col-auto" hidden> 
            <div id="toggle-disable-id" role="group"  class="btn-group my-1 ml-2">
                <input type="radio" class="btn-check" name="optionsED" id="optionEnable" value="ON" checked>
                <label class="btn btn-outline-secondary" for="optionEnable">Enable</label>                        
                <input type="radio" class="btn-check" name="optionsED" id="optionDisable" value="OFF">
                <label class="btn btn-outline-secondary" for="optionDisable">Disable</label>
            </div>
        </div>		
		<div class="container-fluid">
		  <div class="row pt-1">
		    <div class="col-md-12 d-grid">
		      <span class="p-1 badge bg-secondary text-white text-center rounded">
		        <p class="h5 pt-1"><strong>ÁREA MONITORADA</strong></p>
		      </span>
		    </div>
		  </div>
		
		  <div class="row pt-1 pb-2 align-items-end g-2">
		    
		    <!-- Nome da mancha (campo grande) -->
		    <div class="col-md-7">
		      <input id="nomeManchaMonitorada" placeholder="Digite o nome da mancha monitorada para cadastrar. Ex: Mancha monitorada de Curitiba" type="text" maxlength="200" class="form-control text-uppercase placa">
		    </div>
		
		    <!-- Cor da região (compacto) -->
		    <div class="col-md-2 d-flex align-items-center">
		      <label for="corPoligono" class="mb-0"><strong>Cor da região parametrizável:</strong></label>&nbsp;&nbsp;&nbsp;&nbsp;
		      <input type="color" id="corPoligono" class="form-control form-control-color p-1" value="#FF0000" title="Escolha a cor">
		    </div>		
			<div class="col-md-1">
		      <button type="button" class="btn btn-info w-100" onclick="desenharTodasManchas()">TODAS ÁREAS</button>
		    </div>		
		    <div class="col-md-1">
		      <button type="button" class="btn btn-success w-100" onclick="verificarManchaExiste()">CADASTRAR</button>
		    </div>
		    <div class="col-md-1">
		      <button type="button" class="btn btn-warning w-100" onclick="limparMapa()">LIMPAR</button>
		    </div>
		
		  </div>
		</div>
    	<div class="container-fluid">
			  <div class="row">			  
			    <!-- Painel lateral de manchas -->
			    <div class="col-md-3 pt-2">
			      <div class="card">
			        <div class="card-header bg-dark text-white">
			          <strong>Manchas cadastradas</strong>
			        </div>
			        <div class="card-body p-2" style="max-height: 80vh; overflow-y: auto;">
			        <div class="mb-2">
					  <input type="text" id="nome-mancha" class="form-control" placeholder="Filtrar manchas por nome">
					</div>			        
			          <ul id="lista-manchas" class="list-group list-group-flush" style="display: flex; gap: 6px;">
			          </ul>
			        </div>
			      </div>
			    </div>			
			    <!-- Mapa -->
			    <div class="col-md-9 p-2">
			      <div id="map-canvas" style="height: 100vh; width: 100%;"></div>
			    </div>			
			  </div>
			</div>
		<div class="overlay"></div>	
		<div id="loading-overlay" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(255,255,255,0.7); z-index:9999; text-align:center; padding-top:20%;">
	  <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
	    <span class="visually-hidden">Carregando...</span>
	  </div>
	  <div style="margin-top: 1rem;">Carregando...</div>
	</div>	
	</body>    
   	<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.6.2/chart.min.js"></script>
 	<script src="https://cdnjs.cloudflare.com/ajax/libs/chartjs-plugin-datalabels/2.0.0/chartjs-plugin-datalabels.min.js"></script>
	<script src="/muralha-digital/pages/relatorios/js/grafico-utilidades.js"></script>
</html>