<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html>
  <head>
    <title>Cadastro de Ponto de Interesse</title> 	
    <script src="https://polyfill.io/v3/polyfill.min.js?features=default"></script>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />   
    <link rel="stylesheet" href="assets/css/cadastro-ponto-interesse.css">

    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    
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
		  				<input id="inputName" type="text" class="form-control">
					</div>
				</div>
				
				<div class="col-md-8">			
					<div class="input-group mb-3">
		  				<span class="input-group-text" id="inputGroup-sizing-default">Descrição</span>
		  				<input id="inputDesc" type="text" class="form-control">
					</div>
				</div>
				
				<div class="input-group">
					<select class="form-select" id="inputType">
						<option selected>Tipo de ponto...</option>
				    	<option value="1">Banco</option>
				    	<option value="2">Órgão Governamental</option>
				    	<option value="3">Estabelecimento</option>
				    	<option value="3">Outros</option>
					</select>
				</div>
					
			</div>
			
			<div class="input-group">
			  <select class="selectpicker form-control border" data-live-search="true" id="inputEquip" name="inputEquip" data-style="btn-white" data-size="20" data-none-selected-text="Equipamentos...">
                   <option value="0" selected="selected">Equipamentos...</option>
               </select>
			  <button class="btn btn-success" type="button" onclick="adicionaEquip()">Adicionar</button>
			</div>	
			
			<div class="input-group">
			  <span class="input-group-text">Selecionados</span>
			  <textarea class="form-control" id=inputNameEquip></textarea>
			</div>
			
			<br></br>
			
			<div class="d-grid gap-2 col-6 mx-auto">
			  <button class="btn btn-primary" onclick="salvaPonto()" type="button">SALVAR</button>
			</div>			
						
		</div>	
			
	</div>

	<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk&callback=initMap">
	</script>
    
    <script src="/muralha-digital/utils/maps-config.js"></script>
    <script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
    <script src="assets/js/cadastro-ponto-interesse.js"></script>
  </body>
</html>