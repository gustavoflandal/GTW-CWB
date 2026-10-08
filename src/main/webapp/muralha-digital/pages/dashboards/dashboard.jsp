<!DOCTYPE html>

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">
  <head>
	  	<title>Dashboard</title> 	
	  	
	  	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  	<meta http-equiv="x-ua-compatible" content="ie=edge">
	  	
    	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">    
    	<link rel="stylesheet" href="assets/css/dashboard-init.css">
    	<link rel="stylesheet" href="assets/css/calendario-intensidade.css">
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    	
    	<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>    
		
		<!-- ------------------------------------------------------------------------------------------------------ -->
    	<!-- Mapas Mapas Mapas --> 	
		<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk"></script>	    
    	<script src="assets/heatmap/heatmap.js"></script>
    	<script src="assets/heatmap/gmaps-heatmap.js"></script>  
    	<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    	<!-- ------------------------------------------------------------------------------------------------------ -->
  </head>
  
  <body>
  
  	<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 
  	
  
    <div style="margin-top: 10px; margin-left: 10px; margin-right: 10px;">
           <div class="row">    
	           <div class="col-4">
	           		<div class="input-group mb-3">
						<span class="p-2 bg-success text-white rounded"><strong>Filtros</strong></span>
						<span class="p-2"></span>
		                <select class="form-select form-select-md rounded" id="selectMunMapa" onchange="obterEquipamentos();AtualizarLocalizacaoMapas();">
						  <option value="0" selected>-- Municipio --</option>
						</select>
						<span class="p-1"></span>
		                <select class="form-select form-select-md rounded" id="selectRegMapa" onchange="obterEquipamentos()">
						  <option value="0" selected>-- Regiao --</option>
						</select>
					</div>                    
               </div>        
               <div class="col-4">
<!-- 					<select class="form-select form-select-md" id="selEquipamento" name="equipamento"> -->
<!-- 	                    <option value="0" selected="selected">--Todos os Equipamentos--</option> -->
<!-- 	                </select> -->
	                <select class="selectpicker form-control border" data-live-search="true" id="selEquipamento" name="equipamento" data-style="btn-white" data-size="20" data-none-selected-text="--Todos os equipamentos--">
	                    <option value="0" selected="selected">--Todos os Equipamentos--</option>
	                </select>
               </div> 
               <div class="col-3">
	               <div class="input-group">
						<span class="input-group-text">Periodo</span>
					  	<input id='dataInicio' type='text' class='form-control' data-td-target='#dataInicio' readonly/>
						<label class="p-2"> - </label>
					  	<input id='dataFim' type='text' class='form-control' data-td-target='#dataFim' readonly/>
					</div>
				</div>
				<div class="col-1">
					<button id="btnProcessar" class="btn btn-success" onclick="Analisar()">PROCESSAR</button>
				</div>
           </div>      		
      </div>	
      
      <!-- --------------------------------------------------------------------------------- -->
      <!-- Exemplos de Charts.js -->
      <!-- https://ordinarycoders.com/blog/article/11-chart-js-examples -->
      <!-- https://tobiasahlin.com/blog/chartjs-charts-to-get-you-started/#9-mixed-chart -->
      <!-- --------------------------------------------------------------------------------- -->
      <div style="margin-top: 10px; margin-left: 10px; margin-right: 10px;">
	      <div class="row">       
	      	<div class="col-3">
	      	
	      		<div class="mb-2">
		      		<div class="thumbnail fundo">
		      			<canvas id="chartPassagensInfracoes"></canvas>
		      		</div> 
	      		</div>     		
	      		
	      		<div class="mb-2">
					<div id="alertaAnomaliaDiv" class="alert alert-success alert-dismissible fade show" role="alert">
					  <strong><small>ALERTAS DE ANOMALIAS</small> </strong>
					  <div id="alertaAnomalia1"><small><strong>Trafego:</strong> Nao ha</small> </div>
					  <div id="alertaAnomalia2"><small><strong>Infracao:</strong> Nao ha</small></div>
					  <div id="alertaAnomalia3"><small><strong>ocorrencias:</strong> Nao ha</small></div>
<!-- 					  <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button> -->
					</div> 
				</div> 
				
				<div id="mapcss"  class="mb-2">
					
					
			      	<div class="row">       
			      		<div class="col-7">					
							<h8><strong>Mapa de concentracao veicular</strong></h8>
						</div>
						<div class="col-5">
							<button id="spinAtualizaMapaConc" class="btn btn-warning btn-sm pull-right" disabled>
								<span class="spinner-grow text-muted spinner-grow-sm"></span>
								<span class="spinner-grow text-primary spinner-grow-sm"></span>
		   					    <span class="spinner-grow spinner-grow-sm"></span>
							  Atualizando
							</button>							
		      			</div>
		      		</div>
		      		
		      		
		      		<div id="mapcss" class="thumbnail fundo">
		      			<div id="map-canvas"></div>  		      			
		      		</div> 
		      	</div>  
	      	</div>
	      	
	      	<div class="col-9">
	      		<div class="thumbnail fundo">
	      			<div style="margin-left: 10px; margin-right: 10px;">
			     		<div class="row">     
			     		    <div class="col-12">
							  <div class="input-group mb-3 my-1">
								  <span class="p-2 bg-warning text-dark rounded-start"><strong>Categoria</strong></span>
								  <select class="form-select" id="selTipoRelatorio" aria-label="Example select with button addon">
								    <option value="1" selected>Fluxo Veicular</option>
								    <option value="2">Infracoes</option>
								    <option value="3">Ocorrencias</option>
								  </select>
								  <span class="p-2 bg-warning text-dark rounded-end">
					  	   				<small><strong id="totalizador">Totalizador</strong></small>
					  	   		  </span>
								</div>
					  	   </div>
					  	   <!-- <div class="col-4">
					  	   	<div>
					  	   		<small><strong id="periodo">Período atual: 01/10/2021 a 01/01/2022 (abertura com data pre-definida)</strong></small>
					  	   	</div>
					  	   	<div>
					  	   		<small><strong id="totalizador">Totalizador: 123.587 Passag/Inf/Irreg</strong></small>
					  	   	</div>
					  	   </div> 
					  	    -->  
			            </div>
		            </div>	
		            
		            <div class="row">       
		     		    <div class="col-4">
							<canvas id="chartCompAnoAnterior"></canvas>
						</div>
     
		     		    <div class="col-4">
							<canvas id="chartCompMesAnterior"></canvas>
						</div>
						
		     		    <div class="col-4">
							<canvas id="chartEvolucaoClassificacao"></canvas>
						</div>		
						
				    </div>	
				    
				    <hr class="bg-secondary border-2 border-top border-secondary">
				    
				   	<div class="row">       							
						<div class="col-4">
							<canvas id="chartDistribuicaoFaixa"></canvas>
						</div>	
						
						<div class="col-4">
							<canvas id="chartPrevisaoFuturo"></canvas>
						</div>		
						
						<div class="col-4">
							<canvas id="chartRankingFaixa"></canvas>
						</div>	
				    </div>	
				    
				    <hr class="bg-secondary border-2 border-top border-secondary">
				    
				   	<div class="row">       							
						<div class="col-2">						
							<figure class="figure">
	  							<img src="assets/images/calendar-intensidade-icon-2-transp.png" 
								  	   class="rounded mx-auto d-block" 
								  	   style="max-width:100%;"
								  	   alt="Clique para visualizar o Calendário de Intensidade"
								  	   data-bs-toggle="modal" data-bs-target="#calendarioIntensid">
	  							<figcaption class="figure-caption text-center"><strong>Calendario de Intensidade</strong></figcaption>
							</figure>							
				    	</div>	
				    	
				    	<div class="col-10">	
							<div id="mapcss"  class="mb-2">
								<h8><strong><div class="col-md-12 text-center">Mapa 3D de Incidencia</div></strong></h8>
					      		<div id="mapcss" class="thumbnail fundo">
					      			<div id="map-canvas-3d"></div>   
					      		</div> 
					      	</div> 	
				      	</div>			    	
				    					    
					</div>				    
			    </div>	            		  
	      	</div>

	      </div>  
      </div>    


		<!-- Modal -->
		<div class="modal fade" id="calendarioIntensid" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
		  <div class="modal-dialog modal-xl">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title" id="staticBackdropLabel">Calendario de Intensidade</h5>
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		      </div>
		      <div class="modal-body">
		      
		        <%@ include file="/muralha-digital/pages/dashboards/calendario-intensidade.jsp"%>
		      
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
		      </div>
		    </div>
		  </div>
		</div>      
	      
      
	 	<div class="overlay"></div>
  </body>
  
  <script src="/muralha-digital/assets/js/componente-data-hora.js"></script>
  <script src="/muralha-digital/utils/maps-config.js"></script>
  <script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
  <script src="assets/js/mapa-concentracao.js"></script>
  <script src="assets/js/alerta-anomalia.js"></script>
  <script src="assets/js/grafico-comp-diario-fluxo-X-infracoes.js"></script>
  <script src="assets/js/grafico-comp-diario-ano-anterior.js"></script>
  <script src="assets/js/grafico-comp-diario-mes-anterior.js"></script>  
  <script src="assets/js/grafico-evolucao-por-classificacao.js"></script>
  <script src="assets/js/grafico-distribuicao-faixa.js"></script>  
  <script src="assets/js/grafico-previsao-futuro.js"></script>  
  <script src="assets/js/ranking-quantidade-faixa.js"></script>    
  <script src="assets/js/mapa-3d.js"></script>
  <script src="assets/js/dashboard-init.js"></script>
  <script src="assets/js/grafico-utils.js"></script>

</html>