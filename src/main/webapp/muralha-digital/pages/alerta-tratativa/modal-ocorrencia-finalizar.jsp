<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:url value="/" 	var="root" />

<script type="text/javascript">
	var urlRoot = "${root}";
</script>
	
<script type="text/javascript" src="js/modal-ocorrencia-finalizar.js"></script>

<div class="modal fade" id="modalOcorrenciaFinalizar" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="2005" aria-labelledby="modalOcorrenciaFinalizarLabel" aria-hidden="true">
   	<div class="modal-dialog modal-dialog-centered modal-lg ">
		<div class="modal-content">
			
			<div class="modal-header">
               	<h5 class="modal-title" id="staticBackdropLabel">
					<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-check-square-fill text-success flex-shrink-0 me-2" viewBox="0 0 16 16">
					  	<path d="M2 0a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2H2zm10.03 4.97a.75.75 0 0 1 .011 1.05l-3.992 4.99a.75.75 0 0 1-1.08.02L4.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093 3.473-4.425a.75.75 0 0 1 1.08-.022z"/>
					</svg>
               		Finalizar Irregularidade
           		</h5>
               	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
       		</div>
       		
			<div class="modal-body">
				<div id="msg_container_modal_finalizar_ocorrencia"></div>	
				<div class="row">
					<div class="col-sm-12">
	      				<div class="mb-3">
		           			<div class="form-group">
				                <label for="selStatusFinalizacaoOcorrenciaCad" class='form-label'>Motivo:</label>
				                <select class="form-select" id="selStatusFinalizacaoOcorrenciaCad" name="selStatusFinalizacaoOcorrenciaCad">
				                    <option value="0" selected="selected">--Selecione o Motivo--</option>
				                </select>
			                </div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
	      				<div class="mb-3">
				            <div class="form-group">
				            	<label for="obsFinalizacaoOcorrenciaCad" class='form-label'>Observação:</label>
  								<textarea id="obsFinalizacaoOcorrenciaCad" class="form-control" maxlength="200" aria-label="With textarea"></textarea>
				            </div>
						</div>
					</div>
				</div>
			</div>
	
			<div class="modal-footer">
	        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
	        	<button id="btnLimparCamposFinalizarOcorrencia" type="button" class="btn btn-warning" onclick="LimparCamposFinalizarOcorrencia()">Limpar</button>
	        	<button id="btnFinalizarOcorrencia" type="button" class="btn btn-success" onclick="FinalizarOcorrencia()">Finalizar</button>
	      	</div>
		</div>
	</div>
</div>
