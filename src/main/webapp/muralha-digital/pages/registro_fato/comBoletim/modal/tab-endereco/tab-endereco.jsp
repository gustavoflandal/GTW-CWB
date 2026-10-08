<meta charset="UTF-8">
<link rel="stylesheet"
	href="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-endereco/css/tab-endereco.css">
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-endereco/js/consulta.js"></script>
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-endereco/js/tab-endereco.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-map-marker" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-comBoletim">Cadastro de Endereço</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-comBoletim">
	<!-- Caixa de instrução -->
	<div class="alert alert-info mb-3" role="alert"
		style="font-size: 0.9rem;">
		<strong>Importante:</strong> Para informar o endereço, use uma das
		opções abaixo:<br>
		<ul style="margin: 5px 0 0 20px; padding: 0;">
			<li>Informe o CEP para preencher automaticamente os campos.</li>
			<li>Ou clique no mapa para selecionar a localização desejada.</li>
		</ul>
	</div>
	<div id="mapComBoletim" style="width: 100%; height: 250px;"></div>
	<form id="formEnderecoComBoletim" onsubmit="event.preventDefault();">
		<div class="row mb-3">
			<div class="col-md-6">
				<label for="idTipoEventoComBoletim" class="form-label">Tipo
					de Evento</label> <select class="form-select" id="idTipoEventoComBoletim"
					required>
					<option value="">Selecione</option>
					<!-- Adiciona os tipos reais dinamicamente -->
				</select>
			</div>
			<div class="col-md-6">
				<label for="idCidadeComBoletim" class="form-label">Cidade</label> <select
					class="form-select" id="idCidadeComBoletim" required>
					<option value="">Selecione</option>
					<!-- Adiciona os tipos reais dinamicamente -->
				</select>
			</div>
		</div>

		<div class="row mb-3">
			<div class="col-md-3">
				<label for="cepComBoletim" class="form-label">CEP</label> <input
					type="text" class="form-control" id="cepComBoletim" maxlength="9"
					required>
			</div>
			<div class="col-md-4">
				<label for="bairroComBoletim" class="form-label">Bairro</label> <input
					type="text" class="form-control" id="bairroComBoletim"
					maxlength="100" required>
			</div>
			<div class="col-md-5">
				<label for="complementoComBoletim" class="form-label">Complemento</label>
				<input type="text" class="form-control" id="complementoComBoletim"
					maxlength="100">
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-md-10">
				<label for="ruaComBoletim" class="form-label">Rua</label> <input
					type="text" class="form-control" id="ruaComBoletim" maxlength="500"
					required>
			</div>
			<div class="col-md-2">
				<label for="numeroComBoletim" class="form-label">Número</label> <input
					type="number" class="form-control" id="numeroComBoletim" required>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-md-6">
				<label for="latitudeComBoletim" class="form-label">Latitude</label>
				<input type="text" id="latitudeComBoletim" class="form-control">
			</div>
			<div class="col-md-6">
				<label for="longitudeComBoletim" class="form-label">Longitude</label>
				<input type="text" id="longitudeComBoletim" class="form-control">
			</div>
		</div>
	</form>

	<div id="containerTabelaEnderecosComBoletim" class="row mt-4">
		<div class="col-12">
			<h6>Endereços adicionais</h6>
			<hr class="sub-diviver-modal-comBoletim">
			<table class="table table-bordered table-sm"
				id="tabelaEnderecosAdicionaisComBoletim">
				<thead class="table-light">
					<tr>
						<th>Tipo do Endereço</th>
						<th>Endereço</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
	</div>
</div>
