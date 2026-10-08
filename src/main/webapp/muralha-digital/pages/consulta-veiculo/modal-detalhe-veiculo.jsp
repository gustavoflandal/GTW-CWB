<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:url value="/" 	var="root" />

<script type="text/javascript">
	var urlRoot = "${root}";
</script>

<link rel="stylesheet" href="/muralha-digital/pages/consulta-veiculo/css/modal-detalhe-veiculo.css">
<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>
<script type="text/javascript" src="/muralha-digital/pages/consulta-veiculo/js/modal-detalhe-veiculo.js"></script>

<%@ include file="/muralha-digital/pages/monitorado/modal-cadastro-monitorado.jsp" %>
<%@ include file="/muralha-digital/pages/linha-tempo/modal-linha-tempo.jsp" %>
<%@ include file="/muralha-digital/pages/consulta-veiculo/modal-passagem-veiculo.jsp" %>

<div class="modal fade" id="modalDetalheVeiculo" tabindex="-1" aria-labelledby="modalDetalheVeiculoLabel" aria-hidden="true">
   	<div class="modal-dialog modal-dialog-centered modal-xl ">
		<div class="modal-content">
			
			<div class="modal-header pt-2 pb-2">
               	<h5 class="modal-title" id="staticBackdropLabel">Detalhes do Veículo</h5>
               	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
       		</div>
       		
			<div class="modal-body pt-1 pb-1">
				<div id="error_container_modal_detalhe_veiculo"></div>
				<div class="row">
					<div class="col-sm-12">
						<div class="container border border-2">
							<div class="row mb-2 mt-2">
								<div class="col-sm-1"></div>
								<div id="controleGaleriaPrev" class="col-sm-1"></div>
								<div class="col-sm-8">
									<div class="text-center">
										<div id="galeriaImagensVeiculo" class="carousel slide carousel-fade" data-bs-interval="false">
										</div>
									</div>
								</div>
								<div id="controleGaleriaNext" class="col-sm-1"></div>
								<div class="col-sm-1"></div>
								<div class="controls">
						        <div class="slider-container">
						            <label for="brightness">Brilho</label>
						            <input type="range" id="brightness" min="0" max="200" value="100">
						        </div>
						        <div class="slider-container">
						            <label for="contrast">Contraste</label>
						            <input type="range" id="contrast" min="0" max="200" value="100";>
						        </div>
						    	</div>
							</div>
						</div>
						<div class="container border border-2 pb-2">
							<div class="row">
								<div class="col-sm-10">
									<h5 class="text-center"><strong>Informações</strong></h5>
								</div>
								<div class="col-sm-2 text-end">		
									<button type="button" class="btn btn-primary mt-1 pt-0" onclick="capturarVeiculoId()" title="Associação de passagens">
										<i class="fa fa-road" aria-hidden="true"></i>
									</button>
									<button id="btnAbrirLinhaTempo" type="button" class="btn btn-secondary mt-1 pt-0" data-bs-toggle="modal" data-bs-target="#linhaTempo" title="Ver linha do tempo do veículo" onclick="AbrirLinhaTempoVeiculo()" >
										<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-clock-history" viewBox="0 0 16 16">
										  <path d="M8.515 1.019A7 7 0 0 0 8 1V0a8 8 0 0 1 .589.022zm2.004.45a7 7 0 0 0-.985-.299l.219-.976q.576.129 1.126.342zm1.37.71a7 7 0 0 0-.439-.27l.493-.87a8 8 0 0 1 .979.654l-.615.789a7 7 0 0 0-.418-.302zm1.834 1.79a7 7 0 0 0-.653-.796l.724-.69q.406.429.747.91zm.744 1.352a7 7 0 0 0-.214-.468l.893-.45a8 8 0 0 1 .45 1.088l-.95.313a7 7 0 0 0-.179-.483m.53 2.507a7 7 0 0 0-.1-1.025l.985-.17q.1.58.116 1.17zm-.131 1.538q.05-.254.081-.51l.993.123a8 8 0 0 1-.23 1.155l-.964-.267q.069-.247.12-.501m-.952 2.379q.276-.436.486-.908l.914.405q-.24.54-.555 1.038zm-.964 1.205q.183-.183.35-.378l.758.653a8 8 0 0 1-.401.432z"/>
										  <path d="M8 1a7 7 0 1 0 4.95 11.95l.707.707A8.001 8.001 0 1 1 8 0z"/>
										  <path d="M7.5 3a.5.5 0 0 1 .5.5v5.21l3.248 1.856a.5.5 0 0 1-.496.868l-3.5-2A.5.5 0 0 1 7 9V3.5a.5.5 0 0 1 .5-.5"/>
										</svg>
									</button>
									<button id="btnPerfilComportamental" style="color: black; background-color: #0dcaf0; border-color: #0dcaf0;" type="button" class="btn btn-secondary mt-1 pt-0" title="Perfil Comportamental" onclick="abrirPerfilComportamental()">
							            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-map" viewBox="0 0 16 16">
							                <path fill-rule="evenodd" d="M15.817.113A.5.5 0 0 1 16 .5v14a.5.5 0 0 1-.402.49l-5 1a.502.502 0 0 1-.196 0L5.5 15.01l-4.902.98A.5.5 0 0 1 0 15.5v-14a.5.5 0 0 1 .402-.49l5-1a.5.5 0 0 1 .196 0L10.5.99l4.902-.98a.5.5 0 0 1 .415.103zM10 1.91l-4-.8v12.98l4 .8V1.91zm1 12.98 4-.8V1.11l-4 .8v12.98zm-6-.8V1.11l-4 .8v12.98l4-.8z"></path>
							            </svg>
									</button>
								</div>
							</div>
							<div class="row">
								<div class="col-sm-9">
									<div class="form-group">
						            	<label for="equipamentoModal" class='form-label col-form-label col-form-label-sm'>Equipamento:</label>
		  								<input id="equipamentoModal" class="form-control form-control-sm" disabled></input>
						            </div>
					            </div>
								<div class="col-sm-1">
									<div class="form-group">
						            	<label for="pistaModal" class='form-label col-form-label col-form-label-sm'>Pista:</label>
		  								<input id="pistaModal" class="form-control form-control-sm" disabled></input>
						            </div>
					            </div>
								<div class="col-sm-2">
									<div class="form-group">
						            	<label for="classificacaoModal" class='form-label col-form-label col-form-label-sm'>Classificação:</label>
		  								<input id="classificacaoModal" class="form-control form-control-sm" disabled></input>
						            </div>
					            </div>
							</div>
							<div class="row">
								<div class="col-sm-3">
									<div class="form-group">
						            	<label for="dataModal" class='form-label col-form-label col-form-label-sm'>Data:</label>
		  								<input id="dataModal" class="form-control form-control-sm" disabled></input>
						            </div>
					            </div>
					            <div class="col-sm-2">
									<div class="form-group">
										<label for="placaModal" class="form-label col-form-label col-form-label-sm">Placa:</label>
										<div class="input-group">
											<input id="placaModal" class="form-control form-control-sm text-uppercase placa-modal" maxlength="7" disabled>
											<button id="btnPlaca" type="button" class="btn btn-sm btn-warning form-control-sm" onclick="alterar_placa()">ALTERAR</button>
										</div>
									</div>
					            </div><div class="col-sm-3">
									<div class="form-group">
						            	<label for="marcaModal" class='form-label col-form-label col-form-label-sm'>Marca:</label>
		  								<input id="marcaModal" class="form-control form-control-sm" disabled></input>
						            </div>
					            </div>
					            <div class="col-sm-3">
									<div class="form-group">
						            	<label for="modeloModal" class='form-label col-form-label col-form-label-sm'>Modelo:</label>
		  								<input id="modeloModal" class="form-control form-control-sm" disabled></input>
						            </div>
					            </div>
					            <div class="col-sm-1">
									<div class="form-group">
						            	<label for="velocidadeModal" class='form-label col-form-label col-form-label-sm'>Velocidade:</label>
		  								<input id="velocidadeModal" class="form-control form-control-sm" disabled></input>
						            </div>
					            </div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div id="id_veic" hidden></div>
	
			<div class="modal-footer pt-2 pb-2">
				<button type="button" class="btn btn-secondary me-1" onclick="ObterVeiculoAnterior()">Anterior</button>
  				<button type="button" class="btn btn-secondary me-auto ms-1" onclick="ObterVeiculoProximo()">Próximo</button>
				<button type="button" class="btn btn-primary me-1" onclick="exportarImagem()">Exportar</button>
				<button type="button" class="btn btn-warning" onclick="AbrirCadastroMonitorado()">Monitorar</button>
	        	<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
	      	</div>
		</div>
	</div>
</div>