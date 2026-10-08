<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include
	file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>
<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<link rel="stylesheet" href="/muralha-digital/pages/consulta-veiculo/css/consulta.css">
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
		<link rel="stylesheet" href="/muralha-digital/pages/consulta-veiculo/freewall/css/style.css" />
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
		
		<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>
		<script type="text/javascript" src="/muralha-digital/pages/consulta-veiculo/freewall/js/jquery-1.10.2.min.js"></script>
		<script type="text/javascript" src="/muralha-digital/pages/consulta-veiculo/freewall/js/freewall.js"></script>
		<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery-3.6.0.min.js"></script>
		<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
		<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
		<link rel="stylesheet" href="css/style.css">
<%@page import="java.util.Collection"%>



<div id="loading-overlay" style="display: none;">
    <div class="loading-spinner"></div>
</div>
<br />

<div class="container">
 	<div class="row">
 		<div class="col-md-2"></div>
    	<div class="col-md-8">
			
    		<h2 class="text-center"><strong>Consulta de Logs</strong></h2>
    	</div>
		<div class="col-md-2"></div>
   </div>
</div>

<div class="container">
 	<div class="row">
    	
    	<div class="col-md-12">
			   	
	    		<div class="container mt-3">
					  <div class="tab-content">
					    <div class="container tab-pane active"><br>										    	
				    	
				    		<div class="row">
								
														
								<div class="col-md-4 input-group-sm">
									<span class="col-md-4 input-group-sm"><strong>Data</strong></span>
							    		 <div class="input-group mb-3 input-group">
							    			<span class="input-group-text"><i class="bi bi-calendar"></i></span>
							    			<input id="data_ini" name="data_ini" class="form-control" type="date" />
										</div>
				
									
								</div>
								
								<div class="col-md-6 input-group-sm" id="descricaoLocal">	
									<span class="col-md-4 input-group-sm"><strong> Equipamento</strong></span>				                
					                <select id="selEquipamento" class="selectpicker form-control border" 
					                data-live-search="true" data-actions-box="true" data-style="btn-white" data-size="20"
					                 data-none-selected-text="--Todos os equipamentos--"
										></select>												
				                       
								
								</div>
								
							</div>	
										
							<br>
						</div>
					</div>			
				</div>
					
				<div class="row">
					<div class="col-md-6">
						<div class="d-grid gap-2">
							<button id="consultarLog" style="margin-left: auto; width: 150px;" type="button" class="btn btn-primary btn-sm" onclick="gerarExeclLog();"><strong>BAIXAR</strong></button>							
						</div>								
					</div>
					<div  class="col-md-3">
						<div class="d-grid gap-2">
							<button id="exportar"  style=" width: 150px;" type="button" class="btn btn-warning btn-sm" onclick="limpar_campos();"><strong>LIMPAR CAMPOS</strong></button>
						</div>								
					</div>																				
				</div>
							
		</div>
	</div>
</div>
<style>
	.placeholder-option {
	    color: #999; /* Exemplo de estilo */	    
	}
</style>
	

<script type="text/javascript" src="js/consulta_log.js"></script>
<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>


<%@ include file="/includes/rodape.jsp" %>
