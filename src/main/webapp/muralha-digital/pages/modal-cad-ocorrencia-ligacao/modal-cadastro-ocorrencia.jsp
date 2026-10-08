<html>	
	<head>
		<meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
		<title>Modal para cadastro de ocorrências</title>
		<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
			<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>
			
			<link rel="stylesheet" href="/muralha-digital/pages/modal-cad-ocorrencia-ligacao/assets/css/modal-cadastro-ocorrencia.css">
			<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
			<link rel="stylesheet" href="/muralha-digital/pages/atendimento-ocorrencias/assets/css/atendimento-ocorrencias.css">
			<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
			<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
			
			<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
			<script src="/muralha-digital/pages/modal-cad-ocorrencia-ligacao/assets/js/modal-cadastro-ocorrencia.js"></script>
			<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
			
			<link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
			<script src="https://cdn.jsdelivr.net/npm/jquery@3.6.4/dist/jquery.min.js"></script>
			<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
			<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.16/jquery.mask.min.js"></script>			
			<link href="https://cdn.jsdelivr.net/npm/@ttskch/select2-bootstrap4-theme@1.5.2/dist/select2-bootstrap4.min.css" rel="stylesheet" />									
	</head>
	
	<body>
		<!-- Modal para cadastrar nova ocorrência -->
		<form id="formModalCadastrarOcorrencia">
			<div class="modal fade" id="modalCadastrarOcorrencia" tabindex="-1" aria-labelledby="modalCadastrarOcorrenciaLabel" aria-hidden="true">
			  <div class="modal-dialog modal-lg modal-dialog-centered">
			    <div class="modal-content">
			      <div class="modal-body">
			
			        <!-- NAV-TABS -->
			        <ul class="nav nav-tabs mb-3" id="tabsOcorrencia" role="tablist">
			          <li class="nav-item" role="presentation">
			            <button class="nav-link active" id="tab1-tab" data-bs-toggle="tab" data-bs-target="#tab1" type="button" role="tab">Solicitante e Vítima</button>
			          </li>
			          <li class="nav-item" role="presentation">
			            <button class="nav-link" id="tab2-tab" data-bs-toggle="tab" data-bs-target="#tab2" type="button" role="tab">Ocorrência</button>
			          </li>
			          <li class="nav-item" role="presentation">
			            <button class="nav-link" id="tab3-tab" data-bs-toggle="tab" data-bs-target="#tab3" type="button" role="tab">Endereço</button>
			          </li>
			        </ul>
			
			        <!-- CONTEÚDO DAS ABAS -->
			        <div class="tab-content" id="tabsOcorrenciaContent">
			
			          <!-- ABA 1 - Solicitante e Vítima-->
			          <div class="tab-pane fade show active" id="tab1" role="tabpanel">
			          <div class="d-flex" style="gap: 3px">
			          	<div class="mb-3 col-5">
				             <label for="selSolicitante"><strong>Tipo de solicitante:</strong></label>
				             <select class="form-select" name="selSolicitante" id="selSolicitante">
				             	<option value="0" selected disabled>Selecione o tipo de solicitante</option>
				             </select>
			            </div>
			            <div class="col-1"></div>
			            <div class="mb-3 col-5">
				             <label><strong>CPF do solicitante:</strong></label>
				             <input class="form-control" type="text" placeholder="Ex.: 111.111.111-11" maxlength="14" name="cpf" id="cpf">
			            </div>
			          </div>
			            <div class="mb-3">
			              <label><strong>Nome do solicitante:</strong></label>
			              <input class="form-control" type="text" placeholder="Ex.: Fulaninho da silva" name="nomeSolicitante">
			            </div>
			            <div class="mb-3">
			              <label><strong>Nome da vítima:</strong></label>
			              <input class="form-control" type="text" placeholder="Ex.: Ciclano de souza" name="nomeVitima">
			            </div>
			          </div>
			
			          <!-- ABA 2 - Ocorrência -->
			          <div class="tab-pane fade" id="tab2" role="tabpanel">
			          <div class="d-flex" style="gap: 3px">
			          	<div class="mb-3 col-5">
			              <label><strong>Houve envolvimento de armas?</strong></label>
			              <select class="form-select" name="selEnvolvArmas" id="selEnvolvArmas" data-minimum-results-for-search="Infinity">
			              	<option value="0" selected disabled></option>
			                <option value="1">Não</option>
			                <option value="2">Sim</option>
			              </select>
			            </div>
			            <div class="col-1">
			            </div>
			            <div class="mb-3 col-5">
			              <label><strong>Data e hora do ocorrido:</strong></label>
							<div class='input-group' id='grupoDataInicioModal' data-td-target-input='nearest' data-td-target-toggle='nearest'>
  								<input name="dataHoraOcorrido" id='dataInicioModal' type='text' class='form-control' data-td-target='dataInicioModal' readonly/>
								   	<span class='input-group-text' data-td-target='dataInicioModal' data-td-toggle='datetimepicker'>
								     	<span class='fas fa-calendar'></span>
								   	</span>
							</div>
			            </div>
			          </div>
			            <div class="mb-3">
			              <label><strong>Tipos de ocorrência:</strong></label>
			              <select class="form-select" name="selTipoOcorrencia" id="selTipoOcorrencia">
			                <option value="0">Selecione o tipo da ocorrência</option>
			              </select>
			            </div>
			            <div class="mb-3">
			              <label><strong>Observações:</strong></label>
			              <textarea class="form-control" rows="4" placeholder="Detalhamento do ocorrido." name="observacoes"></textarea>
			            </div>
			          </div>
			
			          <!-- ABA 3 - Endereço -->
			          <div class="tab-pane fade" id="tab3" role="tabpanel">
			            <div class="mb-3">
			              <label><strong>Cidade:</strong></label>
			              <select class="form-select" id="selCidades" data-minimum-results-for-search="8" name="selCidades">
			                <option value="0" selected disabled>Selecione a cidade</option>
			              </select>
			            </div>
			            <div class="mb-3">
			              <label><strong>Bairro:</strong></label>
			              <input class="form-control" type="text" placeholder="Ex.: Centro" name="bairro">
			            </div>
			            <div class="mb-3">
			              <label><strong>Rua:</strong></label>
			              <input class="form-control" type="text" placeholder="Ex.: Rua Principal" name="rua">
			            </div>
			            <div class="mb-3">
			              <label><strong>Número:</strong></label>
			              <input class="form-control" type="text" placeholder="Ex.: 123" name="numero">
			            </div>
			            <div class="mb-3">
			              <label><strong>Complemento:</strong></label>
			              <input class="form-control" type="text" placeholder="Ex.: Apartamento 101" name="complemento">
			            </div>
			          </div>		
			        </div>
			      </div>		
			      <div class="modal-footer">
			      	<button type="button" class="btn btn-warning" onClick="limparCampos()">Limpar</button>
			        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" onClick="fecharModal()">Fechar</button>
			        <button type="button" class="btn btn-success" onClick="cadastrarOcorrenciaLigacao()">Cadastrar</button>
			      </div>
			    </div>
			  </div>
			</div>
			<!-- Fim da modal -->
		</form>	
	</body>
</html>