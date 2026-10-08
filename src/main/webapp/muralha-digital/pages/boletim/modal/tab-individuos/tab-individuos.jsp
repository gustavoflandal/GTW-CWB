<meta charset="UTF-8">
<script
	src="/muralha-digital/pages/boletim/modal/tab-individuos/js/consulta.js"></script>
<script
	src="/muralha-digital/pages/boletim/modal/tab-individuos/js/tab-individuos.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-address-card" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-boletim">Cadastro de Envolvidos</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-boletim">
	<form id="formEnvolvido"
		onsubmit="event.preventDefault(); adicionarEnvolvido();">
		<div class="row">
			<div class="col-sm-5">
				<div class="mb-3">
					<label for="tipoEnvolvimento" class="form-label">Tipo de
						Envolvimento</label> <select class="form-select" id="tipoEnvolvimento"
						required>
						<option value="">Selecione</option>
					</select>
				</div>
			</div>

			<div class="col-sm-5">
				<div class="mb-3">
					<label for="cpf" class="form-label">CPF</label> <input type="text"
					placeholder="000.000.000-00" maxlength="20" class="form-control" id=cpfModalBoletim required>
				</div>
			</div>
		</div>

		<div class="mb-3">
			<label for="nome" class="form-label">Nome</label> <input type="text"
			placeholder="Nome do Envolvido"	maxlength="200" class="form-control" id="nome" required>
		</div>

		<div class="mb-3">
			<label for="detalheEnvolvimento" class="form-label">Detalhe
				do Envolvimento</label>
			<textarea class="form-control" id="detalheEnvolvimento" rows="3"
				maxlength="1000"></textarea>
		</div>

		<div class="d-flex flex-row-reverse">
			<button type="submit" class="btn btn-primary">Adicionar</button>
		</div>
	</form>

	<hr class="my-4">

	<h5>Lista de Envolvidos</h5>
	<table class="table table-bordered table-hover mt-3"
		id="tabelaEnvolvidos">
		<thead class="table-light">
			<tr>
				<th>Tipo</th>
				<th>Nome</th>
				<th>CPF</th>
				<th>Detalhes</th>
				<th>Ação</th>
			</tr>
		</thead>
		<tbody id="listIndividuos">
			<!-- Itens adicionados via JS -->
		</tbody>
	</table>
</div>