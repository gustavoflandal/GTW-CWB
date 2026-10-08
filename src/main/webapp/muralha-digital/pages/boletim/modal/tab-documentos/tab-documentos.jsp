<meta charset="UTF-8">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script
	src="/muralha-digital/pages/boletim/modal/tab-documentos/js/tab-documentos.js"></script>
	<script
	src="/muralha-digital/pages/boletim/modal/tab-documentos/js/consulta.js"></script>
<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-file" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-boletim">Anexo de Documentos</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-boletim">
	<form id="formDocumentos"
		onsubmit="event.preventDefault(); adicionarDocumento();"
		class="row g-3 mb-4">
		<div class="col-md-4 d-none" id="grupoTipoDocumento">
			<label for="tipoDocumento" class="form-label">Tipo do
				Documento</label> <input type="text" class="form-control" id="tipoDocumento"
				maxlength="20" readonly>
		</div>
		<div class="col-md-6">
			<label for="arquivoDocumento" class="form-label">Importar
				Arquivo</label> <input type="file" class="form-control"
				id="arquivoDocumento" onchange="atualizarNomeArquivo()"
				accept=".pdf,.jpg,.png,.jpeg" required>
		</div>
		<div class="col-md-12">
			<label for="detalhamentoDocumento" class="form-label">Detalhamento</label>
			<textarea class="form-control" id="detalhamentoDocumento"
				name="detalhamentoDocumento" maxlength="300" rows="3"
				placeholder="Descreva os detalhes do documento" required></textarea>
		</div>
		<div class="d-flex flex-row-reverse">
			<button type="submit" class="btn btn-primary">Adicionar</button>
		</div>
	</form>
	<hr class="my-4">
	<div class="table-responsive">
		<table class="table table-bordered align-middle text-center"
			id="tabelaDocumentos">
			<thead class="table-light">
				<tr>
					<th>#</th>
					<th>Tipo</th>
					<th>Nome do Arquivo</th>
					<th>Detalhamento</th>
					<th>Ações</th>
				</tr>
			</thead>
			<tbody id="listaDocumentos">
				<!-- Itens adicionados via JS -->
			</tbody>
		</table>
	</div>
</div>