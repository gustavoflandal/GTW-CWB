<meta charset="UTF-8">
<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-shopping-basket" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-boletim">Cadastro de Apreensões</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-boletim">
	<form id="formApreensao"
		onsubmit="event.preventDefault(); adicionarApreensao();">
		<div class="row">
			<div class="mb-3 col-sm-5">
				<label for="tipo" class="form-label">Tipo</label> <input type="text"
					 placeholder="Ex.: Dinheiro" class="form-control" id="tipo" name="tipo" maxlength="20" required>
			</div>
		</div>
		<div class="row">
			<div class="mb-3 col-sm-12">
				<label for="descricao" class="form-label">Descrição</label>
				<textarea class="form-control" id="descricao" name="descricao"
					maxlength="200" rows="3" required></textarea>
			</div>
		</div>
		<div class="text-end">
			<button type="submit" class="btn btn-primary">Adicionar</button>
		</div>
	</form>
	<hr class="my-4">
	<div class="table-responsive">
		<table class="table table-bordered" id="tabelaApreensoes">
			<thead class="table-light">
				<tr>
					<th>Tipo</th>
					<th>Descrição</th>
					<th class="text-center">Ação</th>
				</tr>
			</thead>
			<tbody id="listApreensoes"></tbody>
		</table>
	</div>
</div>
<script
	src="/muralha-digital/pages/boletim/modal/tab-apreencoes/js/tab-apreencoes.js"></script>