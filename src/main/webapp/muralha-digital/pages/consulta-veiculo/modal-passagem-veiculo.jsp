<html>	
	<head>
		<meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
		<title>Modal para associação de passagens de veículos a um fato</title>											
	</head>
	
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
	<link rel="stylesheet" href="/muralha-digital/pages/consulta-veiculo/css/modal-passagem-veiculo.css">
	
	<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
	<script src="/muralha-digital/pages/consulta-veiculo/js/modal-passagem-veiculo.js"></script>	
	
	<body>
		<!-- Modal para cadastrar nova ocorrência -->
			<div class="modal fade" id="modalPassagemVeiculo" tabindex="-1" aria-labelledby="labelModalPassagemVeiculo" aria-hidden="true">
			  <div class="modal-dialog modal-lg">
			    <div class="modal-content">
			    
			    <div class="modal-header pt-2 pb-2">
	               	<h5 class="modal-title" id="staticBackdropLabel">Associação de passagens ao fato</h5>
	               	 <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button> 
       			</div>
			    
			      <div class="modal-body">	
			      	<div class="row">			      		
						<div class="mb-3 col-md-4">			
							<label for="placaPassagem" class='form-label col-form-label col-form-label-sm'>Placa:</label>				
		  					<input placeholder="ABC1234" id="placaPassagem" class="form-control form-control-sm" maxlength="8"></input>
						</div>						
						<div class="mb-3 col-md-4">			
							<label for="nomeEnvolvidoFato" class='form-label col-form-label col-form-label-sm'>Nome:</label>				
		  					<input placeholder="Nome de algum envolvido" id="nomeEnvolvidoFato" class="form-control form-control-sm" maxlength="75"></input>
						</div>						
						<div class="mb-3 col-md-4">			
							<label for="cpfFato" class='form-label col-form-label col-form-label-sm'>CPF:</label>				
		  					<input id="cpfFato" class="form-control form-control-sm cpf-mask" type="text" placeholder="Ex.: 123.456.789-11" maxlength="14"></input>
						</div>
			      	</div>
			      	<div class="row">
						<div class="mb-3 col-md-4">	
							<label for="dataInicioFato" class='form-label col-form-label col-form-label-sm'>Data Início:</label>						
		  					<input id="dataInicioFato" placeholder="dd/mm/yyyy" class="form-control form-control-sm" type="date"></input>
						</div>						
						<div class="mb-3 col-md-4">					
							<label for="dataFimFato" class='form-label col-form-label col-form-label-sm'>Data Fim:</label>		
		  					<input id="dataFimFato" placeholder="dd/mm/yyyy" class="form-control form-control-sm" type="date"></input>
						</div>
						<div class="mb-3 col-md-2">
							<label class="form-label col-form-label col-form-label-sm">&nbsp;</label>
				      		<button type="button" class="btn btn-primary btn-sm" id="btnPesquisarFatos" onClick="buscarFatos(event, true)">
				      			<i class="fas fa-search"></i> PESQUISAR
							</button>
			      		</div>
			      		<div class="mb-3 col-md-2">
							<label class="form-label col-form-label col-form-label-sm">&nbsp;</label>
							<button type="button" class="btn btn-warning btn-sm" onClick="limparCampos()" style="margin-top: 30px">LIMPAR</button>
			      		</div>
			      	</div>
			      	<div class="table-responsive mt-2">
						<table
							class="table table-bordered table-hover align-middle text-center">
							<thead class="table-light">
								<tr>
									<th>Tipo Registro de Fato</th>
									<th>Nome / CPF</th>
									<th>Veículo</th>
									<th>Endereço</th>
									<th>Situação</th>
									<th>Data Cadastro</th>
									<th>Ação</th>
								</tr>
							</thead>
							<tbody id="resultadoRegistrosFato">
								<!-- Linhas dinâmicas aqui -->
							</tbody>
						</table>
						<div>
							<ul id="componente_paginacao_passagens" class="pagination justify-content-center"></ul>
						</div>
					</div>
			      </div>		
			    </div>
			  </div>
			</div>
	</body>
</html>