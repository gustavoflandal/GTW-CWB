<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:url value="/" var="root" />

<script type="text/javascript">
	var urlRoot = "${root}";
</script>
	
<!-- <script type="text/javascript" src="js/cadastro-monitorado.js"></script> -->

<div class="modal fade" id="modalMosaicoConfiguracao" name="modalMosaicoConfiguracao" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="2005" aria-labelledby="modalMosaicoConfiguracaoLabel" aria-hidden="true">
   	<div class="modal-dialog modal-dialog-centered">
		<div class="modal-content">
			<div class="modal-header">
               	<h5 class="modal-title" id="staticBackdropLabel">Configurações de Monitoramento ao Vivo</h5>
               	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
       		</div>
			<div class="modal-body">
				<div id="error_container_modal_mosaico_config"></div>
				<div class="row">
	           		<div class="col-sm-2"></div>
					<div class="col-sm-8">
	      				<div class="mb-3">
		           			<div class="form-group">
				                <label for="selTempoRodizioMosaico" class="form-label">Tempo de rodízio entre câmeras (minutos):</label>
				                <select class="form-select" id="selTempoRodizioMosaico" name="selTempoRodizioMosaico">
				                    <option value="60" selected>1</option>
				                    <option value="120">2</option>
				                    <option value="180">3</option>
				                    <option value="240">4</option>
				                    <option value="300">5</option>
				                    <option value="360">6</option>
				                    <option value="420">7</option>
				                    <option value="480">8</option>
				                    <option value="540">9</option>
				                    <option value="600">10</option>
				                </select>
			                </div>
		                </div>
	                </div>
					<div class="col-sm-2"></div>
				</div>
			</div>
	
			<div class="modal-footer">
	        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
	        	<button id="btnSalvarModal" type="button" class="btn btn-success" onclick="SalvarConfiguracoes();">Salvar</button>
	      	</div>
		</div>
	</div>
</div>
