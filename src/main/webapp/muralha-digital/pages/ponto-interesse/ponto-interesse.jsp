<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html>
  <head>
    <title>Cadastro de Ponto de Interesse</title> 	
    <script src="https://polyfill.io/v3/polyfill.min.js?features=default"></script>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">   
    <link rel="stylesheet" href="assets/css/ponto-interesse.css">
    
  </head>

  
  <body>
  
 	 <div  class="row">
  
		<div class="col-md-12 text-center">
			<h2><strong>Cadastro de Ponto de Interesse</strong></h2>
		</div>
			
	</div>
	
	<div id="map"></div>
	
	<br></br>
	
	<div  class="row">
		
		<div class="col-md-1"></div>
	
		<div class="col-md-10">
			<h6><strong id="latlng"></strong></h6>
			
			<div  class="row">		
				
				<div class="col-md-4">			
			
					<div class="input-group mb-3">
		  				<span class="input-group-text" id="inputGroup-sizing-default">Nome</span>
		  				<input type="text" class="form-control">
					</div>
				</div>
				
				<div class="col-md-8">			
					<div class="input-group mb-3">
		  				<span class="input-group-text" id="inputGroup-sizing-default">Descrição</span>
		  				<input type="text" class="form-control">
					</div>
				</div>
				
				<div class="input-group">
					<select class="form-select" id="inputTypeSelect">
						<option selected>Tipo de ponto...</option>
				    	<option value="1">A</option>
				    	<option value="2">B</option>
				    	<option value="3">C</option>
					</select>
				</div>
					
			</div>
			
			<div class="input-group">
			  <select class="form-select" id="inputGroupSelect04">
			    <option selected>Equipamentos...</option>
			    <option value="1">9901</option>
			    <option value="2">9902</option>
			    <option value="3">9903</option>
			  </select>
			  <button class="btn btn-success" type="button">Adicionar</button>
			</div>	
			
			<div class="input-group">
			  <span class="input-group-text">Selecionados</span>
			  <textarea class="form-control" aria-label="With textarea"></textarea>
			</div>
			
			<br></br>
			
			<div class="d-grid gap-2 col-6 mx-auto">
			  <button class="btn btn-primary" type="button">SALVAR</button>
			</div>			
						
		</div>	
		
		<div class="col-md-1"></div>
		

			
	</div>

	<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk&callback=initMap">
	</script>
    
    <script src="assets/js/ponto-interesse.js"></script>
  </body>
</html>