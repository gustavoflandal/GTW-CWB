<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">
  <head>
	  	<title>GTW - Mapa de Calor</title> 	

	  	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  	<meta http-equiv="x-ua-compatible" content="ie=edge">
	  	
	  	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">    
    	<link rel="stylesheet" href="assets/css/correlacao.css">
    	
		<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk"></script>	    
  </head>
  
  
  <body>
  
  
	<!-- Grid row -->
	<div class="row">

	  <!-- Grid column -->
	  <div class="col-md-2 my-3">  		
  		<h3><span class="badge badge-info">Veículos Correlacionados</span></h3>
	  </div>
		  

		<div class="col-md-10">  			
				<!-- Grid row -->
				<div class="row">
				
					  <!-- Grid column -->
					  <div class="col-md-12">
					
						    <!--Panel-->
						    <div class="card card-body">
								 <div  class="row">
										<div class="col-md-12">
										
											<div class="input-group ">
										
												<h3><span class="badge badge-light my-2">Filtros:</span></h3>	
												
												<select class="custom-select custom-select-sm my-2" id="tipos_alertas">
												  <option value="" disabled selected>Tipo de Alertas</option>
												  <option value="1">Todos</option>
												  <option value="2">Veículos clonados</option>
												  <option value="3">Veículos monitorados com placa semelhante</option>
												  <option value="4">Veículos monitorados com placa idêntica</option>
												</select>
												
												
												<button class="btn btn-success btn-sm" >Aplicar</button>
						
										
												<select class="custom-select custom-select-sm my-2" id="mancha">
												  <option value="" disabled selected>Mancha Monitorada</option>
												  <option value="1">Todas</option>
												  <option value="2">Mancha XXX</option>
												  <option value="3">Mancha YYY</option>
												  <option value="4">Mancha ZZZ</option>
												</select>
												
													  
												<label class="sr-only" for="qtdePassagens"></label>
												<input type="text" class="form-control form-control-sm my-2" id="qtdePassagens" placeholder="Passagens">					    
													  							
												<label class="sr-only" for="tempo"></label>
												<input type="text" class="form-control form-control-sm my-2" id="tempo" placeholder="Tempo">					    
												
												
												<button class="btn btn-success btn-sm" >Aplicar</button>
				
							
													<select class="custom-select custom-select-sm my-2" id="tipo_remocao">
													  <option value="" disabled selected>Tipo de Veículos a ser removidos</option>
													  <option value="1">Moto</option>
													  <option value="2">Passeio</option>
													  <option value="3">Caminhão</option>
													  <option value="4">Onibus</option>
													</select>	
																	  				
													<select class="custom-select custom-select-sm my-2" id="placa">
													  <option value="" disabled selected>Leitura de OCR</option>
													  <option value="1">Com placa</option>
													  <option value="2">Sem placa</option>
													</select>	
													
													
													<button class="btn btn-success btn-sm" >Aplicar</button>
												</div>		
											</div>
										</div>						
										
						
						    </div>
					
					  </div>
		
				</div>
			</div>
		</div>
	
				
										
					  
		<div id="map-canvas"></div>
			  	
			  	
			 
	
	
    
  </body>
  
   <script src="assets/js/correlacao.js"></script>
  
</html>