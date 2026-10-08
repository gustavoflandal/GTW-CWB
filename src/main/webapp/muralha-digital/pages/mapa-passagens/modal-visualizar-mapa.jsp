
<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:url value="/" 	var="root" />

<script type="text/javascript">
	var urlRoot = "${root}";
</script>

<link rel="stylesheet" href="/muralha-digital/pages/mapa-passagens/assets/css/modal-visualizar-mapa.css">
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">

<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk"></script>

<div class="modal fade" id="modalVisualizarMapa" tabindex="-1" aria-labelledby="modalVisualizarMapaLabel" aria-hidden="true">
   	<div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-xl modal-dialog-mapa-passagens">
		<div class="modal-content modal-content-mapa-passagens">
			
			<div class="modal-header">
               	<h5 class="modal-title" id="staticBackdropLabel">Mapa de Passagens</h5>
               	<button id="spinAtualizaMapaPassagens" class="btn btn-warning btn-sm pull-right ms-2" disabled>
					<span class="spinner-grow text-muted spinner-grow-sm"></span>
					<span class="spinner-grow text-primary spinner-grow-sm"></span>
  					<span class="spinner-grow spinner-grow-sm"></span>
				  	Atualizando
				</button>
               	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
       		</div>
       		
			<div class="modal-body modal-body-mapa-passagens">
				<div id="msg_container_modal_mapa_passagens"></div>
				<div id="map" class="mb-2"></div>
				<div class="row">
					<div class="col-sm-12">
						<div class="mb-1">
					  		<h5 id="tituloListaResultado"><small>Lista de passagens:</small></h5>
						</div>
						<div class="table-wrapper-modal">
						  	<div class="table-responsive">         
								<table id="tabelaPassagensMapa" class="table table-bordered table-hover">
								    <thead class="table-secondary">
								      <tr>
								        <th scope="col"><small>Equipamento</small></th>
								        <th scope="col"><small>Pista</small></th>
								        <th scope="col"><small>Data</small></th>
								        <th scope="col"><small>Placa</small></th>
								        <th scope="col"><small>Velocidade</small></th>
								      </tr>
								    </thead>
									<tbody></tbody>
								</table>
							</div>
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

<script type="text/javascript" src="/muralha-digital/utils/maps-config.js"></script>
<script type="text/javascript" src="/muralha-digital/pages/mapa-passagens/assets/js/modal-visualizar-mapa.js"></script>