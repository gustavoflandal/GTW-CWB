<meta charset="UTF-8">
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-boletim/js/consulta.js"></script>
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-boletim/js/tab-boletim.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<link rel="stylesheet"
	href="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-boletim/css/tab-boletim.css">

<div class="container">
	<!-- SITUAÇÃO DO BOLETIM -->
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-info-circle" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-comBoletim">Situação do Boletim</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-comBoletim">

	<form id="formSituacaoComBoletim">
		<div class="row mb-3">
			<div class="col-md-6">
				<label for="idSituacaoComBoletim" class="form-label">Situação</label>
				<select class="form-select" id="idSituacaoComBoletim" required>
					<option value="">Selecione</option>
					<!-- opções devem ser preenchidas via backend ou JS -->
				</select>
			</div>
		</div>
		<div class="mb-3">
			<label for="detalhamentoSituacaoComBoletim" class="form-label">Detalhamento</label>
			<textarea class="form-control" id="detalhamentoSituacaoComBoletim"
				rows="4" maxlength="5000"
				placeholder="Descreva a situação do boletim"></textarea>
		</div>
	</form>

	<!-- APREENSÕES -->
	<div class="row pt-4">
		<div class="container-sub-tittle">
			<i class="fa fa-archive" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-comBoletim">Apreensões</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-comBoletim">

	<form id="formApreensaoComBoletim"
		onsubmit="event.preventDefault(); adicionarApreensaoComBoletim();">
		<div class="row mb-3">
			<div class="col-md-6">
				<label for="tipoApreensaoComBoletim" class="form-label">Tipo</label>
				<input type="text" class="form-control" id="tipoApreensaoComBoletim"
					maxlength="20" required>
			</div>
			<div class="col-md-12">
				<label for="descricaoApreensaoComBoletim" class="form-label">Descrição</label>
				<textarea class="form-control" id="descricaoApreensaoComBoletim"
					placeholder="Descreva o item apreendido"	maxlength="200" rows="3" required></textarea>
			</div>
		</div>
		<div class="text-end">
			<button type="submit" class="btn btn-primary">Adicionar</button>
		</div>
	</form>

	<hr class="my-4">

	<div class="table-responsive">
		<table class="table table-bordered" id="tabelaApreensoesComBoletim">
			<thead class="table-light">
				<tr>
					<th>Tipo</th>
					<th>Descrição</th>
					<th class="text-center">Ação</th>
				</tr>
			</thead>
			<tbody id="listApreensoesComBoletim"></tbody>
		</table>
	</div>
</div>
