<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html lang="en">
  <head>
	  	<title>Muralha Digital</title> 	

	  	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  	<meta http-equiv="x-ua-compatible" content="ie=edge">
 		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	  	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
 
 
  		
  		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>

    	<link rel="stylesheet" href="assets/css/parametros.css">   
    	
    	 	    	
  </head>
  
  <body>
  	  
	<div class="row">
	
	
	
		
		<div class="col-md-12">
		
			<div class="card border-info">
			  <div class="card-header text-center"><b>Cadastro de Pontos de Interesse</b></div>
			  <div class="card-body">

					<h6 style="font-style: italic;"><small><span>PASSOS:   1) Fazer as marcações dos pontos até criar uma área completa.   2) Informar descrição da área monitorada   3) Clicar em Salvar</span></small></h6>
			    
			  </div>
			</div>	
		
          	<div id="map"></div>
          
		</div>
		
		
		
	</div>



	<!-- Modal para gravação dos dados -->
	<div class="modal fade right" id="modalAreaSelecionada" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="false">
	
	  <div class="modal-dialog modal-full-height modal-right" role="document">
	
	
	    <div class="modal-content">
	      <div class="modal-header">
	        <h4 class="modal-title w-100" id="myModalLabel">Cadastro de área Monitorada</h4>
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	          	<span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <div class="modal-body">
	      
				<!-- Small input -->
				<input class="form-control form-control-sm" id="txtDescAreamonitorada" type="text" placeholder="Nome da área Monitorada">
	      
	      
				<div  class="table-responsive text-nowrap">				
				  <table id="tabelaRadares" class="table">
				    <thead>
				      <tr>
				        <th scope="col">RADAR</th>
				        <th scope="col">Descrição</th>
				      </tr>
				    </thead>
				    <tbody>
				    </tbody>
				  </table>
				</div>	      	
	        
	      </div>
	      <div class="modal-footer justify-content-center">
	        <button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="deletarAreaMonitorada()">Cancelar</button>
	        <button type="button" class="btn btn-primary" onclick="gravarAreaMonitorada()">Salvar</button>
	      </div>
	    </div>
	  </div>
	</div>
	<!-- Full Height Modal Right -->
	
	<div id="teste">OK</div>
	
    
    <!--Load the API from the specified URL
    * The async attribute allows the browser to render the page while the API loads
    * The key parameter will contain your own API key (which is not needed for this tutorial)
    * The callback parameter executes the initMap() function
    -->
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk&libraries=geometry,drawing&callback=initMap"></script>    
    <script src="assets/js/estiloMapas.js" type="text/javascript" ></script>
    <script src="assets/js/parametros.js"></script>    
    
  </body>
  
  
  
  

  
</html>