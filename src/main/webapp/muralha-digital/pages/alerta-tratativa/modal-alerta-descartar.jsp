<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:url value="/" 	var="root" />

<script type="text/javascript">
	var urlRoot = "${root}";
</script>
	
<script type="text/javascript" src="js/modal-alerta-descartar.js"></script>

<div class="modal fade" id="modalAlertaDescartar" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="2005" aria-labelledby="modalAlertaDescartarLabel" aria-hidden="true">
   	<div class="modal-dialog modal-dialog-centered modal-lg ">
		<div class="modal-content">
			
			<div class="modal-header">
               	<h5 class="modal-title" id="staticBackdropLabel">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-exclamation-triangle-fill text-warning flex-shrink-0 me-2" viewBox="0 0 16 16" role="img" aria-label="Danger:">
						<path d="M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"/>
					</svg>
               		Descartar Alerta
           		</h5>
               	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
       		</div>
       		
			<div class="modal-body">
				<div id="error_container_modal_descarte"></div>	
				<div class="row">
					<div class="col-sm-12">
	      				<div class="mb-3">
		           			<div class="form-group">
				                <label for="selMotivoDescarteCad" class='form-label'>Motivo do Descarte:</label>
				                <select class="form-select" id="selMotivoDescarteCad" name="selMotivoDescarteCad">
				                    <option value="0" selected="selected">--Selecione o Motivo do Descarte--</option>
				                </select>
			                </div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
	      				<div class="mb-3">
				            <div class="form-group">
				            	<label for="obsDescarteAlertaCad" class='form-label'>Observação:</label>
  								<textarea id="obsDescarteAlertaCad" class="form-control" maxlength="200" aria-label="With textarea"></textarea>
				            </div>
						</div>
					</div>
				</div>
			</div>
	
			<div class="modal-footer">
	        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
	        	<button id="btnLimparCamposDescartarAlerta" type="button" class="btn btn-warning" onclick="LimparCamposDescarte()">Limpar</button>
	        	<button id="btnDescartarAlerta" type="button" class="btn btn-danger" onclick="DescartarAlerta()">Descartar</button>
	      	</div>
		</div>
	</div>
</div>
