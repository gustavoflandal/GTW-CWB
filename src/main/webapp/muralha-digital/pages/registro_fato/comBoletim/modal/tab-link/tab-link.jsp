<meta charset="UTF-8">
<link rel="stylesheet"
	href="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-link/css/tab-link.css">
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-link/js/tab-link.js"></script>

<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-link" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-comBoletim">Cadastro de Links</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-comBoletim">

	<form id="formLinksComBoletim"
		onsubmit="event.preventDefault(); adicionarLinkComBoletim();">
		<div class="row mb-3">
			<div class="col-md-6">
				<label for="urlLinkComBoletim" class="form-label">URL *</label> <input
					type="url" class="form-control" id="urlLinkComBoletim" name="url"
					maxlength="300" placeholder="https://exemplo.com" required>
			</div>
			<div class="col-md-12">
				<label for="detalhamentoLinkComBoletim" class="form-label">Detalhamento</label>
				<textarea class="form-control" id="detalhamentoLinkComBoletim"
					name="detalhamento" rows="3" maxlength="500"></textarea>
			</div>
		</div>

		<div class="text-end">
			<button type="submit" class="btn btn-primary">Adicionar</button>
		</div>
	</form>

	<hr class="my-4">

	<div class="table-responsive">
		<table class="table table-bordered" id="tabelaLinksComBoletim">
			<thead class="table-light">
				<tr>
					<th>#</th>
					<th>URL</th>
					<th>Detalhamento</th>
					<th class="text-center">Ação</th>
				</tr>
			</thead>
			<tbody id="listLinksComBoletim"></tbody>
		</table>
	</div>
</div>
