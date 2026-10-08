<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">
  <head>
	  	<title>Mapa de Dispositivos</title> 	

	  	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  	<meta http-equiv="x-ua-compatible" content="ie=edge">
	  	
	  	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">    
    	<link rel="stylesheet" href="assets/css/mapa-situacao-transito.css">
    	
		<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk"></script>	    
  </head>
  
  
  <body>
	
	<div  class="row">
		<div class="col-md-9">
		    <div id="map-canvas" style="width:100%; height:38em;"></div>
		    
		</div>	
		<div class="col-md-3">
			<div  class="row">
				<div class="col-md-12 text-center">
					<br>
					<h4><strong>Mapa de Monitoramento de trechos</strong></h4>
					<br>
				</div>
			</div>
			<table id= "tabela_trecho" class="table table-hover table-responsive table-sm">
			    <thead>
			      <tr>
			        <th><small>Trecho</small></th>
			        <th><small>Vel. Esperada</small></th>
			        <th><small>Vel. Atual</small></th>			        
			        <th><small>Situacao</small></th>
			      </tr>
			    </thead>
			    <tbody>   
			    </tbody>
			  </table>
		</div>
	</div>


	
    	
    
  </body>
    
  <script src="/muralha-digital/utils/maps-config.js"></script>
  <script src="assets/js/mapa-situacao-transito.js"></script>
  
</html>