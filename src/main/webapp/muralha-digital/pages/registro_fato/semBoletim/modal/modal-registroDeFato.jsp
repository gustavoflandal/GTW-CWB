<html>	
	<head>
		<meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
		<title>Modal para cadastro de ocorrências</title>
		<link rel="stylesheet" href="/muralha-digital/pages/registro_fato/semBoletim/modal/css/modal-RegistroDeFato.css">
		<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
		<link rel="stylesheet" href="/muralha-digital/pages/atendimento-ocorrencias/assets/css/atendimento-ocorrencias.css">
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
		<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.3/dist/leaflet.css" />
		<link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
		<link href="https://cdn.jsdelivr.net/npm/@ttskch/select2-bootstrap4-theme@1.5.2/dist/select2-bootstrap4.min.css" rel="stylesheet" />
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
		
		<script src="https://cdn.jsdelivr.net/npm/jquery@3.6.4/dist/jquery.min.js"></script>
		<script src="https://unpkg.com/leaflet@1.9.3/dist/leaflet.js"></script>
		<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
		<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.16/jquery.mask.min.js"></script>
		<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
		<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>
		
		<script src="/muralha-digital/pages/registro_fato/semBoletim/modal/js/modal-registroDeFato.js"></script>													
	</head>
	
	<body>		
		<!-- Modal para cadastrar nova ocorrência -->
		<form id="formModalCadastrarFato">
			<div class="modal fade" id="modalCadastrarFato" tabindex="-1" aria-labelledby="labelModalCadastrarFato" aria-hidden="true">
			  <div class="modal-dialog modal-lg">
			    <div class="modal-content">
			      <div class="modal-body">
			
			        <!-- NAV-TABS -->
			        <ul class="nav nav-tabs mb-3" id="tabsOcorrencia" role="tablist">
			          <li class="nav-item" role="presentation">
			            <button class="nav-link active" id="tab1-tab" data-bs-toggle="tab" data-bs-target="#tab1" type="button" role="tab">Fato Ocorrido</button>
			          </li>
			          <li class="nav-item" role="presentation">
			            <button class="nav-link" id="tab2-tab" data-bs-toggle="tab" data-bs-target="#tab2" type="button" role="tab">Envolvidos</button>
			          </li>
			          <li class="nav-item" role="presentation">
			            <button class="nav-link" id="tab3-tab" data-bs-toggle="tab" data-bs-target="#tab3" type="button" role="tab">Veículos</button>
			          </li>
			          <li class="nav-item" role="presentation">
			            <button class="nav-link" id="tab4-tab" data-bs-toggle="tab" data-bs-target="#tab4" type="button" role="tab">Objetos relacionados</button>
			          </li>
			          <li class="nav-item" role="presentation">
			            <button class="nav-link" id="tab5-tab" data-bs-toggle="tab" data-bs-target="#tab5" type="button" role="tab">Endereços</button>
			          </li>
				      <li class="nav-item" role="presentation" id="liTab6">
				        <button class="nav-link" id="tab6-tab" data-bs-toggle="tab" data-bs-target="#tab6" type="button" role="tab">Grupos</button>
				      </li>
				      <li class="nav-item" role="presentation" id="liTab7">
				        <button class="nav-link" id="tab7-tab" data-bs-toggle="tab" data-bs-target="#tab7" type="button" role="tab">Passagens</button>
				      </li>
			        </ul>
			
			        <!-- CONTEÚDO DAS ABAS -->
			        <div class="tab-content" id="tabsOcorrenciaContent">			
				          <!-- ABA 1 - Solicitante e Vítima-->
						  <div class="tab-pane active" id="tab1" role="tabpanel">
						    
						    <!-- SEÇÃO: CLASSIFICAÇÃO -->
						    <div class="row">
						      <div class="mb-3 col-md-4">
						        <label><strong>Permite atendimento?</strong></label>
						        <select class="form-select" name="selAtendimento" id="selAtendimento" data-minimum-results-for-search="8">
						          <option value="10" selected disabled>Obrigatório</option> <%-- Valor 10 serve apenas como placeholder para valor invï¿½lido --%>
						          <option value="0">Não</option>
						          <option value="1">Sim</option>
						        </select>
						      </div>
						      <div class="mb-3 col-md-4">
						        <label><strong>Fato privado?</strong></label>
						        <select class="form-select" name="selPrivado" id="selPrivado" data-minimum-results-for-search="8">
						          <option value="10" selected disabled>Obrigatório</option> <%-- Valor 10 serve apenas como placeholder para valor invï¿½lido --%>
						          <option value="0">Não</option>
						          <option value="1">Sim</option>
						        </select>
						      </div>
						      <div class="mb-3 col-md-4">
						        <label><strong>Houve envolvimento de armas?</strong></label>
						        <select class="form-select" name="selEnvolvArmas" id="selEnvolvArmas">
						          <option value="10" selected disabled>Obrigatório</option> <%-- Valor 10 serve apenas como placeholder para valor invï¿½lido --%>
						          <option value="0">Não</option>
						          <option value="1">Sim</option>
						        </select>
						      </div>
						    </div>
						
						    <!-- SEï¿½ï¿½O: Detalhes do Fato -->
						    <div class="row">
						 	  <div class="mb-3 col-md-6">
						        <label><strong>Tipo:</strong></label>
						        <select class="form-select" name="selTipo" id="selTipo" data-minimum-results-for-search="8">
						          <option value='' selected disabled>Obrigatório</option>
						        </select>
						      </div>
							<div class="mb-3 col-md-6">
							  <label><strong>Data e hora do ocorrido:</strong></label>
							  <div class="input-group" id="grupoDataInicioModal">
							    <input name="dataHoraOcorrido" id="dataInicioModal" type="text" class="form-control" readonly />
							    <span class="input-group-text" data-td-target="dataInicioModal" data-td-toggle="datetimepicker">
							      <span class="fas fa-calendar"></span>
							    </span>
							  </div>
							  <!-- A mensagem de erro deve vir aqui fora da input-group -->
							  <div class="error-message" style="display: none;">Campo obrigatório</div>
							</div>
						    </div>					
						    <!-- SEï¿½ï¿½O: Observaï¿½ï¿½es -->
						    <div class="mb-3">
						      <label><strong>Detalhamento:</strong></label>
						      <textarea class="form-control" rows="4" name="observacoes" id="observacoes" placeholder="Detalhamento do ocorrido."></textarea>
						    </div>	    
						  </div>
					  <!-- ABA 2 - Cadastro dos indivï¿½duos -->
			          <div class="tab-pane" id="tab2" role="tabpanel">
			          	<hr>
			          	<div id="envolvidosContainer"></div>
			          	<button type="button" class="btn btn-outline-primary" onclick="adicionarEnvolvido()">+ Adicionar Envolvido</button>
			          </div>		          		
			          <!-- ABA 3 - Cadastro dos veï¿½culos -->
			          <div class="tab-pane" id="tab3" role="tabpanel">
			          	<hr>
			          	<div id="veiculosContainer"></div>
			          		<button type="button" class="btn btn-outline-primary" onclick="adicionarVeiculo()">+ Adicionar Veículo</button>
			          		<button type="button" class="btn btn-primary" onclick="AbrirListaAlertasCadMonitorado()">Alertas relacionados</button>
			          </div>
			         
			          <!-- ABA 4 - Cadastro dos objetos -->
			          <div class="tab-pane" id="tab4" role="tabpanel">
			          	<hr>
				          <div id="objetosContainer"></div>
				          	<button type="button" class="btn btn-outline-primary" onclick="adicionarObjeto()">+ Adicionar Objeto</button>				          	          
			          </div>			
			          <!-- ABA 5 - Endereï¿½o -->
			          <div class="tab-pane fade" id="tab5" role="tabpanel">
			          	<div class="row">
			          	    <div class="mb-3">
						        <label class="form-label fw-bold">Localização</label>
						        <div id="map" style="height: 300px; border: 1px solid #ccc; border-radius: 5px;"></div>
						    </div>
			          		<div class="mb-3 col-md-6">
			          			<label><strong>Cidade:</strong></label>
			          			<select class="form-select" name="selCidades" id="selCidades" data-minimum-results-for-search="8">
							          <option value="" selected disabled>Obrigatório</option>
							    </select>
			          		</div>
			          		<div class="mb-3 col-md-6">
			          			<label><strong>Tipo de evento:</strong></label>
			          			<select class="form-select" name="selTipoEvento" id="selTipoEvento" data-minimum-results-for-search="8" disabled>
							          <option value="1" selected>Local do Evento</option>
							    </select>
			          		</div>
			          	</div>
			          	<div class="row">
			          		<div class="mb-3 col-md-3">
			          			<label><strong>CEP:</strong></label>
						  		<input class="form-control" type="text" placeholder="Ex.: 12345678" name="cep" id="cep" maxlength="9">
			          		</div>
			          		<div class="mb-3 col-md-3">
			          			<label><strong>Bairro:</strong></label>
						  		<input class="form-control" type="text" placeholder="Ex.: Busca por CEP ou clique no mapa" name="bairro" id="bairro" maxlength="255">
			          		</div>
			          		<div class="mb-3 col-md-6">	
							  <label><strong>Rua e número:</strong></label>						  
							  <div class="d-flex gap-2">							  								    
							    <input class="form-control flex-grow-1" type="text" placeholder="Ex.: Busca por CEP ou clique no mapa" name="rua" id="rua" maxlength="255">
							    <input class="form-control" style="max-width: 70px;" type="number" placeholder="12345" name="numeroRua" id="numeroRua" maxlength="9">
							  </div>
							</div>
			          	</div>
			          	<div class="row">
			          		<div class="mb-3 col-md-6">
			          			<label><strong>Latitude:</strong></label>
						  		<input class="form-control" type="text" placeholder="Busca por CEP ou clique no mapa" name="lat" id="lat" maxlength="255">
			          		</div>
			          		<div class="mb-3 col-md-6">
			          			<label><strong>Longitude:</strong></label>
						  		<input class="form-control" type="text" placeholder="Busca por CEP ou clique no mapa" name="long" id="long" maxlength="255">
			          		</div>			          		
			          	</div>
			          	<div class="row">
			          		<div class="mb-3 col-md-12">
			          		<label><strong>Complemento:</strong></label>
			          		<textarea class="form-control" rows="4" name="complemento" id="complemento" placeholder="Ex.: Residencia própria da vítima, portão branco, casa azul. Próximo ao supermercado Condor."></textarea>
			          		</div>
			          	</div>
						<div class="row d-flex">
						    <label style="font-size: 20px" class="mb-2 w-100" id="tituloOutrosEnderecos"><strong>Outros Endereços:</strong></label>
						    <div class="mb-2 col-md-4">
						    	<label style="font-size: 18px" id="tituloTipoEndereco"><strong>Tipo:</strong></label>
						    </div>
						    <div class="mb-2 col-md-8">
						    	<label style="font-size: 18px" id="tituloEnderecoDinamico"><strong>Endereço:</strong></label>
						    </div>
						    <div id="containerEnderecosDinamicosGeral" class="w-100">
						    </div>
						</div>
			          </div>	
			          <!-- ABA 6: Grupos -->
			          <div class="tab-pane fade" id="tab6" role="tabpanel">
			          	<div class="row">
			          		<div class="mb-3 col-md-6">
			          			<label><strong>Grupos:</strong></label>
			          			<select class="form-control select2-multiple" name="selGrupos" id="selGrupos" data-minimum-results-for-search="8" multiple="multiple">
						        </select>
			          		</div>
			          		<div class="mb-3 col-md-6">
			          			<label><strong>Usuários:</strong></label>
			          			<select class="form-select" name="selUsuarios" id="selUsuarios" data-minimum-results-for-search="8" multiple="multiple">
						        </select>
			          		</div>
			          	</div>
			          </div>	
			          <div class="tab-pane fade" id="tab7" role="tabpanel">
			          	<div class="row">
			          		<div class="mb-2 col-md-12">
								<div class="form-group input-group">
									<span class="input-group-text"> <i class="fas fa-search"></i>
									</span> <input name="consulta" id="txt_consulta_passagemSemBoletim" placeholder="Consultar"
										type="text" class="form-control">
								</div>
							</div>
			          		<div class="mb-3 col-md-12">
		          			    <table class="table table-bordered table-sm" id="tabelaPassagensSemBoletim">
							      <thead class="table-light">
							        <tr>
							          <th>Placa</th>
							          <th>Data</th>
							          <th class="text-center">Ação</th>
							        </tr>
							      </thead>
							    </table>
			          		</div>
			          	</div>
			          </div>
			        </div>
			      </div>		
			      <div class="modal-footer">
			      	<button type="button" class="btn btn-warning" onClick="limparCampos()">Limpar</button>
			        <button type="button" class="btn btn-secondary" onClick="fecharModal()">Fechar</button>
			        <button type="button" id="btnCadastrarModalRegistroFato" class="btn btn-success" onClick="cadastrarCompleto()">Cadastrar</button>
			        <button type="button" id="btnAtualizar" class="btn btn-success" onClick="salvarEdicaoCompleta()">Atualizar</button>
			      </div>
			    </div>
			  </div>
			</div>
			<!-- Fim da modal -->
		</form>	
	</body>
</html>