<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:url value="/" 	var="root" />

<script type="text/javascript">
	var urlRoot = "${root}";
</script>
	
<script type="text/javascript" src="js/modal-anotacao-contributiva.js"></script>
<link rel="stylesheet" href="css/modal-alerta-anotacao.css">

<div class="modal fade" id="modalAlertaAnotacao" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="2005" aria-labelledby="modalAlertaAnotacaoLabel" aria-hidden="true">
   	<div class="modal-dialog modal-dialog-centered modal-lg ">
		<div class="modal-content">
			<div class="modal-header">
               	<h5 class="modal-title" id="staticBackdropLabel">Anotações Contributivas</h5>
               	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
       		</div>
			<div class="modal-body">
				<div id="error_container_modal"></div>	
				<div class="row">
					<div class="col-sm-12">
	      				<div class="mb-3">
				            <div class="form-group">
				            	<label for="anotacaoAlertaCad" class='form-label'>Anotação:</label>
  								<textarea id="anotacaoAlertaCad" class="form-control" maxlength="200" aria-label="With textarea"></textarea>
				            </div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
	      				<div class="mb-3">
				            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
				            	<button id="btnLimparAnotacaoModal" type="button" class="btn btn-warning" onclick="LimparCamposAnotacao()">Limpar</button>
				            	<button id="btnAdicionarAnotacaoModal" type="button" class="btn btn-primary" onclick="CadastrarAnotacao()">Adicionar</button>
				            </div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<div class="table-wrapper">
						  	<div class="table-responsive">         
								<table id="tabelaAnotacaoContributiva" class="table table-bordered table-hover">
								    <thead class="table-secondary">
								      <tr>
								        <th scope="col"><small>Anotação</small></th>
								        <th scope="col"><small>Data</small></th>
								        <th scope="col"><small>Usuário</small></th>
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
