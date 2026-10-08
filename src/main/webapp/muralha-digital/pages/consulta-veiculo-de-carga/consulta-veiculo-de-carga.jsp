<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="keywords" content="javascript, dynamic, grid, layout, jquery plugin, flex layouts, normal grid layouts"/>
		
		<title>GTW - Consulta de Veículos de Carga</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<link rel="stylesheet" href="/muralha-digital/pages/consulta-veiculo-de-carga/css/consulta.css">
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
		<link rel="stylesheet" href="/muralha-digital/pages/consulta-veiculo-de-carga/freewall/css/style.css" />
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
		
		<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>
		<script type="text/javascript" src="/muralha-digital/pages/consulta-veiculo-de-carga/freewall/js/jquery-1.10.2.min.js"></script>
		<script type="text/javascript" src="/muralha-digital/pages/consulta-veiculo-de-carga/freewall/js/freewall.js"></script>
		<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery-3.6.0.min.js"></script>
		<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
		
	</head>
	
	<body class="homepage loading">

		<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %>
		<%@ include file="/muralha-digital/pages/mapa-passagens/modal-visualizar-mapa.jsp" %> 
		<%@ include file="/muralha-digital/pages/consulta-veiculo-de-carga/modal-detalhe-veiculo-de-carga.jsp" %>
		
		<div class="container">
			<div class="row gy-3">
				<div class="col-sm-12">
      				<div class="mb-3">
						<h2 id="tituloTelaConsulta" class="text-center"><strong>Consulta de Veículos de Carga</strong></h2>
					</div>
				</div>
			</div>
		</div>
		
		<div id="divFiltrosConsulta" class="container">
			<div class="row gy-3 mb-2">
           		<div class="col-sm-2">
      				<div>
			    		<div class="form-group">
		                	<label for="placaConsulta" class="form-label">Placa:</label>
							<input name="placa" id="placaConsulta" placeholder="Ex.: AAA1111" type="text" maxlength="7" class="form-control text-uppercase placa-caracter-coringa-2"
									data-bs-toggle="tooltip" data-bs-placement="left" data-bs-original-title="É possível utilizar caracter coringa para substituir algum carcater da placa, por exemplo AA*11*1">
						</div>
					</div>
                </div>
		        <div class="col-sm-2">
      				<div>
			            <div class="form-group">
			            	<label for="dataInicio" class='form-label'>Data Inicio:</label>
							<div class='input-group' id='dataInicio' data-td-target-input='nearest' data-td-target-toggle='nearest'>
							  	<input id='dataInicioInput' type='text' class='form-control' data-td-target='#dataInicio' readonly/>
							   	<span class='input-group-text' data-td-target='#dataInicio' data-td-toggle='datetimepicker'>
							     	<span class='fas fa-calendar'></span>
							   	</span>
							</div>
			            </div>
		            </div>
		        </div>
		        <div class="col-sm-2">
      				<div>
			            <div class="form-group">
			            	<label for="dataFim" class='form-label'>Data Fim:</label>
							<div class='input-group log-event' id='dataFim' data-td-target-input='nearest' data-td-target-toggle='nearest'>
							  	<input id='dataFimInput' type='text' class='form-control' data-td-target='#dataFim' readonly/>
							   	<span class='input-group-text' data-td-target='#dataFim' data-td-toggle='datetimepicker'>
							     	<span class='fas fa-calendar'></span>
							   	</span>
							</div>
			            </div>
		            </div>
		        </div>
		        <div class="col-sm-4">
      				<div>
						<div class="form-group">
			                <label for="selEquipamento" class="form-label">Equipamento (PCL):</label>
			                <select id="selEquipamento" class="selectpicker form-control border" data-live-search="true" data-actions-box="true" multiple data-style="btn-white" data-size="20" data-none-selected-text="--Todos os equipamentos--"
								data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"></select>
		                </div>
	                </div>
                </div>

				<div class="col-sm-1">
      				<div>
		                <div class="form-group">
			                <label for="selPista" class="form-label">Faixa:</label>
			                <select id="selPista" class="selectpicker form-control border" multiple data-style="btn-white" data-size="10" data-none-selected-text="Todas"
								data-actions-box="true"
								data-select-all-text="<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='currentColor' class='bi bi-check-all' viewBox='0 0 16 16'>
														<path d='M8.97 4.97a.75.75 0 0 1 1.07 1.05l-3.99 4.99a.75.75 0 0 1-1.08.02L2.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093L8.95 4.992a.252.252 0 0 1 .02-.022zm-.92 5.14.92.92a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 1 0-1.091-1.028L9.477 9.417l-.485-.486-.943 1.179z'></path>
													  </svg>"
								data-deselect-all-text="<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='currentColor' class='bi bi-x-lg' viewBox='0 0 16 16'>
															<path d='M2.146 2.854a.5.5 0 1 1 .708-.708L8 7.293l5.146-5.147a.5.5 0 0 1 .708.708L8.707 8l5.147 5.146a.5.5 0 0 1-.708.708L8 8.707l-5.146 5.147a.5.5 0 0 1-.708-.708L7.293 8 2.146 2.854Z'></path>
														</svg>"
								>
			                    <option value="1">1</option>
			                    <option value="2">2</option>
			                    <option value="3">3</option>
			                    <option value="4">4</option>
			                    <option value="5">5</option>
			                    <option value="6">6</option>
			                    <option value="7">7</option>
			                    <option value="8">8</option>
							</select>
		                </div>
	                </div>
                </div>
			</div>

			<div class="row gy-3 mb-3 align-items-center bottom-distance">
				<div class="col-sm-4">
					<div class="form-group">
						<label for="selClassificacao" class="form-label">Classificação:</label>
						<select id="selClassificacao" class="selectpicker form-control border" multiple
								data-style="btn-white" data-size="5" data-none-selected-text="--Todas as classificações--"
								data-actions-box="true" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos">
						</select>
					</div>
				</div>

				<div class="col-sm-2">
					<div class="form-group">
						<label for="selTipoPlaca" class="form-label">Tipo de Placa:</label>
						<select id="selTipoPlaca" class="form-select">
							<option value="">Todos</option>
							<option value="MERCOSUL">Mercosul</option>
							<option value="PADRAO">Antigo</option>
						</select>
					</div>
				</div>

				<div class="col-sm-2">
					<div class="form-group">
						<label for="selFiltroPlaca" class="form-label">Filtro de Placa:</label>
						<select id="selFiltroPlaca" class="form-select">
							<option value="0">Com ou sem leitura</option>
							<option value="1">Somente com leitura</option>
							<option value="2">Somente sem leitura</option>
						</select>
					</div>
				</div>

				<div class="col-sm-3 d-flex flex-column">
					<div class="form-check mb-2">
						<input class="form-check-input" type="checkbox" value="1" id="chkComImagem">
						<label class="form-check-label" for="chkComImagem">Somente com imagem</label>
					</div>
					<div class="form-check">
						<input class="form-check-input" type="checkbox" value="1" id="chkUltimaPassagem">
						<label class="form-check-label" for="chkUltimaPassagem">Somente a última passagem por placa</label>
					</div>
				</div>
				
			</div>

			<div id="filtrosAdicionais" style="display: none;">
				<div class="row gy-3 mb-3">
					<div class="col-12">
						<div class="border rounded p-3 bg-light">
							<h6 class="mb-0"><i class="fas fa-car"></i> Filtros do Cadastro Único de Veículos</h6>
							<hr class="mt-0 mb-1"/>
							
							<div class="row gy-3">
								<div class="col-sm-2">
									<label for="corVeiculo" class="form-label">Cor:</label>
									<select id="corVeiculo" class="form-select">
										<option value="">Todas</option>
									</select>
								</div>
								<div class="col-sm-2">
									<label for="anoFabricacao" class="form-label">Ano Fabricação:</label>
									<input type="number" id="anoFabricacao" class="form-control" placeholder="Ex.: 2020" min="1900" max="2099">
								</div>
								<div class="col-sm-2">
									<label for="anoModelo" class="form-label">Ano Modelo:</label>
									<input type="number" id="anoModelo" class="form-control" placeholder="Ex.: 2021" min="1900" max="2099">
								</div>
								<div class="col-sm-2">
									<label for="renavam" class="form-label">RENAVAM:</label>
									<input type="text" id="renavam" class="form-control" maxlength="11" placeholder="Ex.: 12345678901">
								</div>
								<div class="col-sm-2">
									<label for="chassi" class="form-label">Chassi:</label>
									<input type="text" id="chassi" class="form-control text-uppercase" maxlength="17" placeholder="Ex.: 9BWZZZ377VT004251">
								</div>
								<div class="col-sm-2">
									<label for="tiposVeiculo" class="form-label">Tipo de Veículo:</label>
									<select id="tiposVeiculo" class="selectpicker form-control border" multiple 
											data-live-search="true" data-style="btn-white" data-size="10" 
											data-none-selected-text="Todos os tipos"
											data-actions-box="true" data-select-all-text="Marcar todos" 
											data-deselect-all-text="Desmarcar todos">
									</select>
								</div>
							</div>

							<div class="row gy-3">
								<div class="col-sm-2">
									<label for="marcaVeiculo" class="form-label">Marca:</label>
									<select id="marcaVeiculo" class="selectpicker form-control border" 
											data-live-search="true" data-style="btn-white" data-size="10" 
											data-none-selected-text="Todas as marcas">
										<option value="">Todas</option>
									</select>
								</div>

								<div class="col-sm-2">
									<label for="modeloVeiculo" class="form-label">Modelo:</label>
									<select id="modeloVeiculo" class="selectpicker form-control border" 
											data-live-search="true" data-style="btn-white" data-size="10" 
											data-none-selected-text="Todos os modelos">
										<option value="">Todos</option>
									</select>
								</div>

								<div class="col-sm-3">
									<label for="selLocalidade" class="form-label">Localidade:</label>
									<select id="selLocalidade" class="selectpicker form-control" data-live-search="true" data-style="btn-white" data-size="10" data-none-selected-text="--Todas as localidades--">
										<option value="">Todas</option>
									</select>
								</div>

								<div class="col-sm-2">
									<div class="form-group">
										<label for="restricao" class="form-label">Restrição:</label>
										<div class="d-flex align-items-center">
											<select id="restricao" class="form-control selectpicker" data-style="btn-white">
												<option value="">Todas</option>
												<option value="1">Sim</option>
												<option value="0">Não</option>
											</select>
											<span class="ms-2" data-bs-toggle="tooltip" data-bs-placement="top" 
												title="Para o funcionamento correto destes filtros é necessária a integração com base única de cadastro de dados sobre veículos.">
												<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-info-circle" viewBox="0 0 16 16">
													<path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
													<path d="m8.93 6.588-2.29.287-.082.38.45.083c.294.07.352.176.288.469l-.738 3.468c-.194.897.105 1.319.808 1.319.545 0 1.178-.252 1.465-.598l.088-.416c-.2.176-.492.246-.686.246-.275 0-.375-.193-.304-.533L8.93 6.588zM9 4.5a1 1 0 1 1-2 0 1 1 0 0 1 2 0z"/>
												</svg>
											</span>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
            </div>
	           	
			<div class="row mb-2">
                <div class="col-sm-2">
      				<div>
						<div class="d-grid gap-2">
							<button id="pesquisar" class="btn btn-primary" type="button" onclick="executaPesquisa(true)">PESQUISAR</button>
						</div>
					</div>
				</div>
				<div class="col-sm-1">
      				<div>
						<div class="d-grid gap-2">
							<button id="limparFiltros" class="btn btn-warning" type="button" onclick="limparFiltros()">LIMPAR</button>
						</div>
					</div>
				</div>
				<div class="col-sm-4"></div>
				
           	</div>
        </div>
        
        
		<div class="container-fluid">
			<div class="row">
				<div class="col-sm-12 container">
      				<div class="mb-3">
				  		<h5 id="tituloListaResultado"><small>Lista de veículos de carga:</small></h5>
		
						<div class="row">
							<div class="col-xxl-8 col-xl-7 col-lg-7 col-md-5 col-12">
					            <div class="form-group input-group">
									<span class="input-group-text">
										<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
					  						<path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"></path>
										</svg>
									</span>
									<input name="consulta" id="txt_consulta" placeholder="Consultar" type="text" class="form-control">
								</div>
							</div>
							<div class="col-12 col-md-auto">
								<div class="form-group input-group d-grid d-md-flex justify-content-md-end h-100">
									<button id="gerarExcel" type="button" class="btn btn-outline-success" title="Exportar Excel" onclick="abrirModal('xls')">
						                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-earmark-excel-fill" viewBox="0 0 16 16">
									  		<path d="M9.293 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.707A1 1 0 0 0 13.707 4L10 .293A1 1 0 0 0 9.293 0zM9.5 3.5v-2l3 3h-2a1 1 0 0 1-1-1zM5.884 6.68 8 9.219l2.116-2.54a.5.5 0 1 1 .768.641L8.651 10l2.233 2.68a.5.5 0 0 1-.768.64L8 10.781l-2.116 2.54a.5.5 0 0 1-.768-.641L7.349 10 5.116 7.32a.5.5 0 1 1 .768-.64z"></path>
										</svg>
									</button>
								</div>
							</div>
						</div>
					</div>
					
				  	<div class="table-responsive" id="lista_resultado" style="overflow-x: auto;">         
						  <table id="tabela" class="table table-bordered table-hover align-middle">
						    
						    <thead class="table-secondary">
								<tr>
									<th scope="col" class="col-sm-2"><small>Equipamento</small></th>
									<th scope="col" class="col-sm-1"><small>Classificação</small></th>
									<th scope="col" class="col-sm-1"><small>Placa</small></th>
									<th scope="col" class="col-sm-1"><small>Data</small></th>
									<th scope="col" class="col-sm-1"><small>PBT (kg)</small></th>
									<th scope="col" class="col-sm-1"><small>PBTC (kg)</small></th>
									<th scope="col" class="col-sm-1"><small>Nº Eixos</small></th>
									<th scope="col" class="col-sm-1"><small>Ações</small></th>
								</tr>
						    </thead>
						    
						    <tbody></tbody>
						    
						  </table>
					</div>
					
					
					<!-- <div id="grade_itens">
						<div id="freewall" class="free-wall"></div>
						<div><canvas class="col-xs-12" id="canvas_temp" width="640" height="480"></canvas></div>					
					</div> -->
						
					<%@ include file="/muralha-digital/utils/paginacao/componente-paginacao.jsp" %>
				</div>
			</div>
		</div>		
		<div class="overlay"></div>
		
		<!-- Modal -->
		<div class="modal fade" id="motivoModal" tabindex="-1" aria-labelledby="motivoModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="motivoModalLabel">Informe o motivo da solicitação</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
				</div>
				<div class="modal-body">
					<input type="hidden" id="formatoArquivo" value="">
					<input type="hidden" id="veiculosSelecionados" value="">
					<textarea id="motivoText" class="form-control" rows="3" placeholder="Digite o motivo aqui..."></textarea>
					<div id="motivoErro" class="text-danger mt-1" style="display:none;">O motivo é obrigatório.</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
					<button type="button" class="btn btn-primary" id="btnEnviarMotivo" onClick="registrarSolicitacaoRelatorio()">Gerar Relatório</button>
				</div>
				</div>
			</div>
		</div>
	</body>
	
	<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>
	<script src="/muralha-digital/assets/js/placa-caracter-coringa.js"></script>
	<script src="/muralha-digital/pages/consulta-veiculo-de-carga/js/consulta.js"></script>
	<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js" integrity="sha512-qZvrmS2ekKPF2mSznTQsxqPgnpkI4DNTlrdUmTzrDgektczlKNRRhy5X5AAOnx5S09ydFYWWNSfcEqDTTHgtNA==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.5.23/jspdf.plugin.autotable.min.js" integrity="sha512-P3z5YHtqjIxRAu1AjkWiIPWmMwO9jApnCMsa5s0UTgiDDEjTBjgEqRK0Wn0Uo8Ku3IDa1oer1CIBpTWAvqbmCA==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.core.min.js" integrity="sha512-UhlYw//T419BPq/emC5xSZzkjjreRfN3426517rfsg/XIEC02ggQBb680V0VvP+zaDZ78zqse3rqnnI5EJ6rxA==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	<script src="/muralha-digital/utils/export-table.js"></script>
	<script src="/muralha-digital/pages/relatorios/js/gerar-arquivo-download.js"></script>
		
</html>