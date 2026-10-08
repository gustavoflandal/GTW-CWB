<meta charset="UTF-8">
<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-car" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-boletim">Cadastro de Veículos</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-boletim">
	<form id="formVeiculo"
		onsubmit="event.preventDefault(); adicionarVeiculo();">
		<div class="row mb-3">
			<div class="col-md-6">
				<label for="placa" class="form-label">Placa</label> <input
					type="text" class="form-control" id="placa" name="placa"
					maxlength="7" required>
			</div>
			<div class="col-md-6">
				<label for="cor" class="form-label">Cor</label> <input type="text"
					class="form-control" id="cor" name="cor" maxlength="50" required>
			</div>
		</div>
		<div class="row mb-3">
			<div class="col-md-6">
				<label for="marca" class="form-label">Marca</label> <input
					type="text" class="form-control" id="marca" name="marca"
					maxlength="20" required>
			</div>
			<div class="col-md-6">
				<label for="modelo" class="form-label">Modelo</label> <input
					type="text" class="form-control" id="modelo" name="modelo"
					maxlength="50" required>
			</div>
		</div>
		<div class="text-end">
			<button type="submit" class="btn btn-primary">Adicionar</button>
		</div>
	</form>
	<hr class="my-4">
	<div class="table-responsive">
		<table class="table table-bordered" id="tabelaVeiculos">
			<thead class="table-light">
				<tr>
					<th>Placa</th>
					<th>Cor</th>
					<th>Marca</th>
					<th>Modelo</th>
					<th class="text-center">Ação</th>
				</tr>
			</thead>
			<tbody id="listVeiculos"></tbody>
		</table>
	</div>
</div>
<script
	src="/muralha-digital/pages/boletim/modal/tab-veiculos/js/tab-veiculos.js"></script>