<script
	src="/muralha-digital/pages/registro_fato/historico/js/registro-fato-modal-historico.js"></script>
<script
	src="/muralha-digital/pages/registro_fato/historico/js/consulta.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<div class="modal fade" id="modalHistorico" tabindex="-1"
	aria-labelledby="modalHistoricoLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg modal-dialog-scrollable">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="modalHistoricoLabel">Histórico de
					Alterações</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div class="modal-body" id="modalHistoricoBody">

				<div id="historicoLoading" class="text-center p-4">
					<div class="spinner-border text-primary" role="status">
						<span class="visually-hidden">Carregando...</span>
					</div>
					<p class="mt-2">Buscando histórico...</p>
				</div>

				<div id="historicoVazio" class="alert alert-info"
					style="display: none;">Nenhum histórico de alterações foi
					encontrado para este registro.</div>

				<div class="accordion" id="historicoConteudo"></div>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary"
					data-bs-dismiss="modal">Fechar</button>
			</div>
		</div>
	</div>
</div>