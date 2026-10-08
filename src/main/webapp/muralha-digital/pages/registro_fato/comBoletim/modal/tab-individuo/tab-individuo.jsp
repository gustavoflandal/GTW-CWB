<meta charset="UTF-8">
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-individuo/js/consulta.js"></script>
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-individuo/js/tab-individuo.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<link rel="stylesheet"
	href="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-individuo/css/tab-individuo.css">

<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-address-card" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-comBoletim">Cadastro de Envolvidos</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-comBoletim">

	<form id="formEnvolvidoComBoletim"
		onsubmit="event.preventDefault(); adicionarEnvolvidoComBoletim();">
		<div class="row">
			<div class="col-sm-5">
				<div class="mb-3">
					<label for="tipoEnvolvimentoComBoletim" class="form-label">Tipo
						de Envolvimento</label> <select class="form-select"
						id="tipoEnvolvimentoComBoletim" required>
						<option value="">Selecione</option>
					</select>
				</div>
			</div>

			<div class="col-sm-5">
				<div class="mb-3">
					<label for="cpfComBoletim" class="form-label">CPF</label> <input
						type="text" class="form-control" id="cpfComBoletim"
						placeholder="000.000.000-00" maxlength="20" required>
				</div>
			</div>
		</div>

		<div class="mb-3">
			<label for="nomeComBoletim" class="form-label">Nome</label> <input
				type="text" class="form-control" id="nomeComBoletim"
				placeholder="Nome do Envolvido" maxlength="200" required>
		</div>

		<div class="row">
			<div class="col-sm-2">
				<div class="mb-3">
					<label for="dddComBoletim" class="form-label">DDD</label> <input
						type="number" class="form-control" id="dddComBoletim"
						maxlength="2">
				</div>
			</div>

			<div class="col-sm-3">
				<div class="mb-3">
					<label for="telefoneComBoletim" class="form-label">Telefone</label>
					<input type="text" class="form-control" id="telefoneComBoletim" maxlength="11">
				</div>
			</div>

			<div class="col-sm-6">
				<div class="mb-3">
					<label for="emailComBoletim" class="form-label">E-mail</label> <input
						type="email" class="form-control" id="emailComBoletim"
						maxlength="50">
				</div>
			</div>
		</div>

		<div class="mb-3">
			<label for="detalheEnvolvimentoComBoletim" class="form-label">Detalhe
				do Envolvimento</label>
			<textarea class="form-control" id="detalheEnvolvimentoComBoletim"
				rows="3" maxlength="1000"></textarea>
		</div>

		<div class="d-flex flex-row-reverse">
			<button type="submit" class="btn btn-primary">Adicionar</button>
		</div>
	</form>

	<hr class="my-4">

	<h5>Lista de Envolvidos</h5>
	<table class="table table-bordered table-hover mt-3"
		id="tabelaEnvolvidosComBoletim">
		<thead class="table-light">
			<tr>
				<th>Tipo</th>
				<th>Nome</th>
				<th>CPF</th>
				<th>DDD</th>
				<th>Telefone</th>
				<th>Email</th>
				<th>Detalhes</th>
				<th>Ação</th>
			</tr>
		</thead>
		<tbody id="listIndividuosComBoletim">
			<!-- Populado via JS -->
		</tbody>
	</table>
</div>
