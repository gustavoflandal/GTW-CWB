<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:url value="/" 	var="root" />

<script type="text/javascript">
	var urlRoot = "${root}";
</script>
	
<!-- <script type="text/javascript" src="js/encerrar-monitorado.js"></script> -->

<div class="modal fade" id="modalEncerrarMonitorado" name="modalEncerrarMonitorado" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="2005" aria-labelledby="modalEncerrarMonitoradoLabel" aria-hidden="true">
   	<div class="modal-dialog modal-dialog-centered modal-lg ">
		<div class="modal-content">
			<div class="modal-header">
               	<h5 id="tituloModalEncerrarMonitorado" class="modal-title" id="staticBackdropLabel">
               		<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-exclamation-triangle-fill text-warning flex-shrink-0 me-2" viewBox="0 0 16 16" role="img" aria-label="Danger:">
						<path d="M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"/>
					</svg>
					Encerrar Monitoramento?</h5>
               	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
       		</div>
			<div class="modal-body">
				<div id="error_container_modal_encerrar_mon"></div>
				<div class="row">
					<div class="col-sm-6">
	      				<div class="mb-3">
		           			<div class="form-group">
				                <label for="tipoAlertaEncerrarMon" class='form-label col-form-label col-form-label-sm'>Tipo:</label>
 									<input id="tipoAlertaEncerrarMon" class="form-control form-control-sm" disabled></input>
			                </div>
		                </div>
	                </div>
					<div class="col-sm-2">
	      				<div class="mb-3">
				    		<div class="form-group">
			                	<label for="placaEncerrarMon" class='form-label col-form-label col-form-label-sm'>Placa:</label>
 									<input id="placaEncerrarMon" class="form-control form-control-sm" disabled></input>
							</div>
						</div>
	                </div>
					<div class="col-sm-2">
	      				<div class="mb-3">
				            <div class="form-group">
				            	<label for="dataInicioEncerrarMon" class='form-label col-form-label col-form-label-sm'>Data Inicio:</label>
 									<input id="dataInicioEncerrarMon" class="form-control form-control-sm" disabled></input>
				            </div>
			            </div>
			        </div>
			        <div class="col-sm-2">
	      				<div class="mb-3">
				            <div class="form-group">
				            	<label for="dataFimEncerrarMon" class='form-label col-form-label col-form-label-sm'>Data Fim:</label>
 									<input id="dataFimEncerrarMon" class="form-control form-control-sm" disabled></input>
				            </div>
			            </div>
			        </div>
				</div>
				<div class="row">
					<div class="col-sm-12">
	      				<div class="mb-3">
				            <div class="form-group">
			                	<label for="descricaoEncerrarMon" class='form-label col-form-label col-form-label-sm'>Descrição:</label>
 									<textarea id="descricaoEncerrarMon" class="form-control" maxlength="200" aria-label="With textarea" disabled></textarea>
				            </div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<p class="text-danger" style="text-align: justify;">
							Ao encerrar o cadastro de monitoramento, não serão mais gerados alertas deste tipo e placa. <br>
							Para continuar o monitoramento deste tipo e placa, utilize a opção Cancelar.
						</p>
					</div>
				</div>
			</div>
	
			<div class="modal-footer">
	        	<button id="btnCancelarEncerrarMon" type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
	        	<button id="btnEncerrarMon" type="button" class="btn btn-warning" onclick="EncerrarMonitoramento()">Encerrar</button>
	      	</div>
		</div>
	</div>
</div>
