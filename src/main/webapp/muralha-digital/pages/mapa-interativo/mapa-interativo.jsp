<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<%@ include file="/muralha-digital/pages/registro_fato/comBoletim/modal/modal-registroDeFato.jsp"%>

<html lang="en">
  <head>
	  	<title>Mapa de alertas e ocorrÃªncias</title> 	

	  	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  	<meta http-equiv="x-ua-compatible" content="ie=edge">	  	
	  	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">  
	  	<link rel="stylesheet" href="assets/css/mapa-interativo.css">
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
		
		<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk&libraries=visualization&callback=initMap"></script>	    
  </head>
  
  <body>
  
	 <%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 
 	    <div class="m-2">
 	    	<div class="row">
            	<div class="col-12 w-100">
            		<h3><span class="badge bg-success w-100">Mapa Interativo</span></h3>
            	</div>   
 	    	</div>
            <div class="row mb-2">                	
               	<div class="col-md-2">
                <label class="legenda">Camada de exibição</label>
		 	    	<select id="selectType" onChange="exibeMarkers()" class="form-select form-select-md" data-minimum-results-for-search="8">
			    		<option value="all">Todos</option>
			    		<option value="treatment">Atendimento</option>
			    		<option value="garrison">Guarnição</option>
			    		<option value="fact">Registro de fato</option>
			    		<option value="pcl">Pontos de captura</option>
			    	</select>                   
                </div>      	   
                
                <div class="col-md-2">
                <label class="legenda">Transito</label>
		 	    	<select id="selectTypeMap" onChange="atualizaMapa()" class="form-select form-select-md" data-minimum-results-for-search="8">
			    		<option value="noMarking">Sem marcação</option>
			    		<option value="roadNetwork">Malha viaria</option>
			    		<option value="busStation">Rodoviaria</option>
			    	</select>                   
                </div>      
                
                <div class="col-md-2" style="display: block;" id="situacao">
                <label class="legenda">Situação</label>
		 	    	<select id="selectSituation" class="form-select form-select-md" placeholder="situação" data-minimum-results-for-search="8">
		    			<option value="5">Todos</option>
			    		<option value="1">Em atraso</option>
			    		<option value="2">Em andamento</option>
			    		<option value="3">Guarnição liberada</option>
			    		<option value="4">Encerramento</option>
			    	</select>                   
                </div>  
                
		        <div class="col-md-2" style="display: block;" id="dataInicio">
		            <label class="legenda">Data Início</label>
		            <input type="date" id="dataInicioAtendimentoInput" class="form-control form-control-md">
		        </div>
		
		        <div class="col-md-2" style="display: block;" id="dataFim">
		            <label class="legenda">Data Fim</label>
		            <input type="date" id="dataFimAtendimentoInput" class="form-control form-control-md">
		        </div>
		        
   		        <div class="col-md-2" style="display: none;" id="dataInicioRegistroFato">
		            <label class="legenda">Data Início</label>
		            <input type="date" id="dataInicioRegistroFatoInput" class="form-control form-control-md">
		        </div>
		
		        <div class="col-md-2" style="display: none;" id="dataFimRegistroFato">
		            <label class="legenda">Data Fim</label>
		            <input type="date" id="dataFimRegistroFatoInput" class="form-control form-control-md">
		        </div>
		        
				<div class="col-md-2" style="display: none;" id="divSituacaoGuarnicao">
				    <label class="legenda">Disponível</label>
				    <select id="situacaoGuarnicao" 
				            class="form-select form-select-md" data-minimum-results-for-search="8">
				        <option value="todos">Todos</option>
				        <option value="sim">Sim</option>
				        <option value="nao">Não</option>
				    </select>                   
				</div>  
				
				<div class="col-md-2" id="divTipo" style="display: none;">
				  <label class="legenda">Tipo</label>
				  <select id="tipo" multiple class="form-select" style="width: 100%;">
				    <!-- opções serão preenchidas via JS -->
				  </select>
				</div>
				
				<div class="col-md-2">
					<label> </label>
					<button class="btn btn-success mt-4" id="btn-atendimento" onClick="aplicarFiltrosAtendimento()">FILTRAR</button>
					<button class="btn btn-success mt-4"  style="display: none;" id="btn-guarnicao" onClick="aplicarFiltrosGuarnicao()">FILTRAR</button>
					<button class="btn btn-success mt-4"  style="display: none;" id="btn-registro-fato" onClick="aplicarFiltrosRegistroFato()">FILTRAR</button>
				</div>

				<div class="row mt-1" style="height: 45px;">
	   				<div id="div-mapa-calor" class="col-md-2 d-flex align-items-center gap-2" style="display: none !important;">
					  <input type="checkbox" id="input-mapa-calor" name="horns" onChange="ativarMapaCalor(this.checked)"/>
					  <label for="input-mapa-calor" id="label-mapa-calor" class="legenda">Mapa de Calor Atendimento</label>
					</div>
					  <div class="col-md-2 d-flex align-items-center gap-2" id="recuperacaoVeiculo">
					  <input type="checkbox" id="recuperacao-veiculo" onchange="switchPolilyne()" name="horns" checked />
					  <label for="recuperacao-veiculo" class="legenda">Recuperação de veículos</label>
					</div>
				</div>
                        
                <div id="map-wrapper" class="mt-3 mx-3" style="display: none;">
			        <div class="input-overlay">
			            <label for="heatmap">Densidade do Mapa de Calor</label>
			            <input class="custom-range" id="heatmap" type="range" min="20" max="100" value="60" onchange="inputRangeOpacity(this)">
			        </div>
			    </div>
                        
            </div>
       </div>	
       
       <div id="map-canvas"></div>	
       
       	<!-- Modal Detalhes -->
	<div class="modal fade" id="modalDetalhes" tabindex="-1" aria-labelledby="modalDetalhesLabel" aria-hidden="true">
	  <div class="modal-dialog modal-lg modal-dialog-centered">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="modalDetalhesLabel">Detalhes da Ocorrência</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
	      </div>
	
	      <div class="modal-body" style="height: 500px; overflow-y: auto;">
	        <!-- Nav Tabs -->
	        <ul class="nav nav-tabs" id="tabDetalhesHistorico" role="tablist">
	          <li class="nav-item" role="presentation">
	            <button class="nav-link active" id="tab-detalhes" data-bs-toggle="tab" data-bs-target="#detalhes" type="button" role="tab" aria-controls="detalhes" aria-selected="true">Detalhes</button>
	          </li>
	          <li class="nav-item" role="presentation">
	            <button class="nav-link" id="tab-historico" data-bs-toggle="tab" data-bs-target="#historico" type="button" role="tab" aria-controls="historico" aria-selected="false">Histórico</button>
	          </li>
	          <li class="nav-item" role="presentation">
	            <button class="nav-link" id="tab-documentos" data-bs-toggle="tab" data-bs-target="#documento" type="button" role="tab" aria-controls="documento" aria-selected="false">Documentos</button>
	          </li>
	        </ul>
	
	        <!-- Aba detalhes -->
	        <div class="tab-content pt-3">
	          <!-- Aba Detalhes -->
	          <div class="tab-pane fade show active" id="detalhes" role="tabpanel" aria-labelledby="tab-detalhes">
	            <div id="conteudoModalDetalhes">
	              Carregando detalhes...
	            </div>
	          </div>
	
	          <!-- Aba Hist�rico -->
	          <div class="tab-pane fade" id="historico" role="tabpanel" aria-labelledby="tab-historico">
	            <div id="conteudoModalHistorico">
	              Carregando histórico...
	            </div>
	          </div>
	           <!-- Aba Documentos -->
	          <div class="tab-pane fade" id="documento" role="tabpanel" aria-labelledby="tab-documentos">
	            <div id="conteudoModalDocumentos">
	              Carregando documentos...
	            </div>
	          </div>
	        </div>
	      </div>
	
	      <div class="modal-footer">
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
	      </div>
	    </div>
	  </div>
	</div>
	
  </body>
   
<link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
  <script src="assets/js/mapa-interativo.js"></script>
  <script src="/muralha-digital/utils/maps-config.js"></script>
  
</html>