<meta charset="UTF-8">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<link rel="stylesheet" href="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-documento/css/tab-documento.css">
<!-- Ajustar para o contexto correto -->
<script src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-documento/js/tab-documento.js"></script>
<script src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-documento/js/consulta.js"></script>

<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-file" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-comBoletim">Anexo de Documentos</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-comBoletim">

	<form id="formDocumentosComBoletim"
		onsubmit="event.preventDefault(); adicionarDocumentoComBoletim();"
		class="row g-3 mb-4">

		<div class="col-md-4 d-none" id="grupoTipoDocumentoComBoletim">
			<label for="tipoDocumentoComBoletim" class="form-label">Tipo do Documento</label>
			<input type="text" class="form-control" id="tipoDocumentoComBoletim"
				maxlength="20" readonly>
		</div>

		<div class="col-md-6">
			<label for="arquivoDocumentoComBoletim" class="form-label">Importar Arquivo</label>
			<input type="file" class="form-control"
				id="arquivoDocumentoComBoletim"
				onchange="atualizarNomeArquivoComBoletim()"
				accept=".pdf,.jpg,.png,.jpeg" required>
		</div>

		<div class="col-md-12">
			<label for="detalhamentoDocumentoComBoletim" class="form-label">Detalhamento</label>
			<textarea class="form-control" id="detalhamentoDocumentoComBoletim"
				name="detalhamentoDocumentoComBoletim" maxlength="300" rows="3"
				placeholder="Descreva os detalhes do documento" required></textarea>
		</div>

		<div class="d-flex flex-row-reverse">
			<button type="submit" class="btn btn-primary">Adicionar</button>
		</div>
	</form>

	<hr class="my-4">

	<div class="table-responsive">
		<table class="table table-bordered align-middle text-center" id="tabelaDocumentosComBoletim">
			<thead class="table-light">
				<tr>
					<th>#</th>
					<th>Tipo</th>
					<th>Nome do Arquivo</th>
					<th>Detalhamento</th>
					<th>Ações</th>
				</tr>
			</thead>
			<tbody id="listaDocumentosComBoletim">
				<!-- Itens adicionados via JS -->
			</tbody>
		</table>
	</div>
</div>
