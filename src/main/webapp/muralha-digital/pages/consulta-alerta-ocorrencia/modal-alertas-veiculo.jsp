<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>

<script type="text/javascript" src="/muralha-digital/pages/consulta-alerta-ocorrencia/js/modal-alertas-veiculo.js"></script>
<script type="text/javascript" src="/muralha-digital/pages/relatorios/js/gerar-arquivo-download.js"></script>

<div class="modal fade" id="modalAlertaVeiculo" tabindex="-1" aria-labelledby="modalAlertaVeiculoLabel" aria-hidden="true">
   	<div class="modal-dialog modal-dialog-centered modal-lg">
		<div class="modal-content">
			
			<div class="modal-header">
               	<h5 class="modal-title" id="staticBackdropLabel">Ocorrências do Veículo</h5>
               	<button id="spinAtualizaAlertaVeiculo" class="btn btn-warning btn-sm pull-right ms-2" disabled>
					<span class="spinner-grow text-muted spinner-grow-sm"></span>
					<span class="spinner-grow text-primary spinner-grow-sm"></span>
  					<span class="spinner-grow spinner-grow-sm"></span>
				  	Atualizando
				</button>
               	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
       		</div>
       		
			<div class="modal-body">
				<div id="error_container_modal_alerta_veiculo"></div>
				<div class="row">
					<div class="col-sm-12">
						<div class='container-fluid g-1 g-md-2 bg-gradient border border-2'>
							<div class="row ms-1 me-1">
								<div class="container-fluid g-1 g-md-1">
									<div class="col-md-12 d-grid">
					            		<span class="badge badge-sm bg-secondary text-white text-center rounded"><h6><strong>VEÍCULO</strong></h6></span>
					            	</div>
				            	</div>
							</div>
							<div class="row ms-1 me-1">
								<div class="container-fluid g-1 g-md-1">
									<div class="row">
										<div class="col-sm-3">
											<div class="input-group input-group-sm">
												<span class="input-group-text bg-transparent">Placa</span>
												<input id="placaVeiculo" type="text" class="form-control" disabled>
											</div>
										</div>
								        <div class="col-sm-4">
								            <div class="input-group input-group-sm">
												<span class="input-group-text bg-transparent">Data</span>
												<input id="dataVeiculo" type="text" class="form-control" disabled>
								            </div>
								        </div>
								        <div class="col-sm-3">
								            <div class="input-group input-group-sm">
												<span class="input-group-text bg-transparent">Velocidade</span>
												<input id="velocidadeVeiculo" type="text" class="form-control" disabled>
								            </div>
								        </div>
						           		<div class="col-sm-2">
						           			<div class="input-group input-group-sm">
								                <span class="input-group-text bg-transparent">Faixa</span>
								                <input id="faixaLocalVeiculo" type="text" class="form-control" disabled>
							                </div>
						                </div>
									</div>
								</div>
							</div>
							<div class="row ms-1 me-1">
								<div class="container-fluid g-1 g-md-1">
									<div class="row">
										<div class="col-sm-12">
					      					<div class="input-group input-group-sm">
							                	<span class="input-group-text bg-transparent">Local</span>
												<input id="localVeiculo" type="text" class="form-control" disabled>
											</div>
						                </div>
									</div>
								</div>
							</div>
							<div class="row ms-1 me-1">
								<div class="container-fluid g-1 g-md-1 bg-light bg-gradient border border-2">
									<div class="row">
										<div class="col-sm-3"></div>
										<div class="col-sm-6 text-center">
								    		<img id="imgVeiculoIrregular" class="img-fluid img-thumbnail" src="/muralha-digital/assets/images/consilux_grande_transparent.png" onerror="this.src=&quot;/muralha-digital/assets/images/consilux_grande_transparent.png&quot;">
							    		</div>
							    		<div class="col-sm-3"></div>
							    	</div>
							    </div>
							</div>
							<div class="row mb-2 mt-1">
								<div class="col-md-12 d-grid">
				            		<span class="badge bg-secondary text-white text-center rounded pt-2 ms-1 me-1"><h6><strong>Ocorrências</strong></h6></span>
				            		<div id="listaAlertaItens" class="ps-3 pe-3"></div>
				            	</div>
							</div>
						</div>
					</div>
				</div>
			</div>
	
			<div class="modal-footer">
	        	<button type="button" class="btn btn-primary" onclick="ImprimirVeicIrregular()">Imprimir</button>
	        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal" onclick="LimparModalAlertasVeiculo()">Fechar</button>
	      	</div>
		</div>
	</div>
</div>
