<meta charset="UTF-8">
<link rel="stylesheet"
	href="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-veiculo/css/tab-veiculo.css">
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-veiculo/js/tab-veiculo.js"></script>
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-veiculo/js/consulta.js"></script>
	<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/js/modal-registroDeFato.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-car" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-comBoletim">Cadastro de Veículos</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-comBoletim">

	<form id="formVeiculoComBoletim"
		onsubmit="event.preventDefault(); adicionarVeiculoComBoletim();">
		<div class="row mb-3">
			<div class="col-md-6">
				<label for="placaComBoletim" class="form-label">Placa</label> <input
					type="text" class="form-control" id="placaComBoletim" name="placa"
					maxlength="7" required>
			</div>
			<div class="col-md-6">
				<label for="corComBoletim" class="form-label">Cor</label> <input
					type="text" class="form-control" id="corComBoletim" name="cor"
					maxlength="50" required>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-md-6">
				<label for="marcaComBoletim" class="form-label">Marca</label> <input
					type="text" class="form-control" id="marcaComBoletim" name="marca"
					maxlength="20" required>
			</div>
			<div class="col-md-6">
				<label for="modeloComBoletim" class="form-label">Modelo</label> <input
					type="text" class="form-control" id="modeloComBoletim"
					name="modelo" maxlength="60" required>
			</div>
		</div>

		<!-- Checkbox para habilitar a nova seção -->
		<div class="form-check mb-3">
			<input class="form-check-input" type="checkbox"
				id="habilitarMonitoramentoComBoletim"> <label
				class="form-check-label" for="habilitarMonitoramentoComBoletim">
				Cadastrar como Veículo Monitorado </label>
		</div>

		<!-- Seção de Veículo Monitorado (inicialmente oculta) -->
		<div id="secaoMonitoramentoComBoletim"
			style="display: none; border: 1px solid #ccc; padding: 15px; border-radius: 8px; background: #f8f9fa;">
			<h6>Cadastro Veículo Monitorado</h6>
			<div class="mb-3">
				<label for="nomeComBoletim" class="form-label">Nome
					(Opcional)</label> <input type="text" id="nomeMonitorado" name="nome"
					class="form-control" maxlength="200">
			</div>
			<div class="row mb-3">
				<div class="col-md-6">
					<label for="tipoAlertaComBoletim" class="form-label">Tipo
						do Alerta <span class="text-danger">*</span>
					</label> <select id="tipoAlertaComBoletim" name="tipoAlerta"
						class="form-control" required>
						<option value="">Selecione...</option>
					</select>
				</div>
				<div class="col-md-6">
					<label for="dataFimComBoletim" class="form-label">Data Início
						(Opcional)</label> <input type="date" id="dataInicioComBoletim"
						name="dataFim" class="form-control" disabled>
				</div>
			</div>

			<div class="mb-3">
				<label for="descricaoComBoletim" class="form-label">Descrição
					<span class="text-danger">*</span>
				</label>
				<textarea id="descricaoComBoletim" name="descricao"
					class="form-control" maxlength="300" rows="3" disabled required></textarea>
			</div>
		</div>
		<div class="text-end mt-3">
			<button type="submit" class="btn btn-primary">Adicionar</button>
			<button type="button" class="btn btn-primary" onclick="AbrirListaAlertasCadMonitorado()">Alertas relacionados</button>
		</div>
	</form>

	<hr class="my-4">

	<div class="table-responsive">
		<table class="table table-bordered" id="tabelaVeiculosComBoletim">
			<thead class="table-light">
				<tr>
					<th>Placa</th>
					<th>Cor</th>
					<th>Marca</th>
					<th>Modelo</th>
					<th class="text-center">Ação</th>
				</tr>
			</thead>
			<tbody id="listVeiculosComBoletim"></tbody>
		</table>
	</div>
</div>