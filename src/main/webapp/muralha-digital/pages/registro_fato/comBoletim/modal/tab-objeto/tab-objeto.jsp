<meta charset="UTF-8">
<link rel="stylesheet"
	href="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-objeto/css/tab-objeto.css">
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-objeto/js/tab-objeto.js"></script>

<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-archive" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-comBoletim">Cadastro de Objetos</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-comBoletim">

	<form id="formObjetoComBoletim"
		onsubmit="event.preventDefault(); adicionarObjetoComBoletim();">
		<div class="row mb-3">
			<div class="col-md-6 pb-2">
				<label for="tipoObjetoComBoletim" class="form-label">Tipo</label> <select
					class="form-select" id="tipoObjetoComBoletim" name="tipo" required>
					<option value="">Selecione...</option>
					<option value="Celular">Celular</option>
					<option value="Carteira">Carteira</option>
					<option value="Bolsa">Bolsa</option>
					<option value="Mochila">Mochila</option>
					<option value="Joias">Joias</option>
					<option value="Relógios">Relógios</option>
					<option value="Eletrônicos">Eletrônicos (notebook, tablet,
						câmera)</option>
					<option value="Chaves">Chaves</option>
					<option value="Óculos">Óculos</option>
					<option value="Armas">Armas</option>
					<option value="Munições">Munições</option>
					<option value="Explosivos">Explosivos</option>
					<option value="Medicamentos">Medicamentos</option>
				</select>
			</div>
			<div class="col-md-12">
				<label for="descricaoObjetoComBoletim" class="form-label">Descrição</label>
				<textarea class="form-control" id="descricaoObjetoComBoletim"
					rows="3" required></textarea>
			</div>
		</div>

		<div class="text-end">
			<button type="submit" class="btn btn-primary">Adicionar</button>
		</div>
	</form>

	<hr class="my-4">

	<div class="table-responsive">
		<table class="table table-bordered" id="tabelaObjetosComBoletim">
			<thead class="table-light">
				<tr>
					<th>Tipo</th>
					<th>Descrição</th>
					<th class="text-center">Ação</th>
				</tr>
			</thead>
			<tbody id="listObjetosComBoletim"></tbody>
		</table>
	</div>
</div>
