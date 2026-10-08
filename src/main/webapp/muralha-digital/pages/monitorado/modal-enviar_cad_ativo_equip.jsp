<!DOCTYPE html>

<div class="modal fade" id="modalEnviarCadAtivoEquip" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="2005" aria-labelledby="modalEnviarCadAtivoEquipLabel" aria-hidden="true">
   	<div class="modal-dialog modal-dialog-centered">
		<div class="modal-content">
			<div class="modal-header">
               	<h5 class="modal-title" id="staticBackdropLabel">Sincronizar cadastros</h5>
               	<button id="spinLoadingEnviarCadAtivo" class="btn btn-warning btn-sm pull-right ms-2" disabled>
					<span class="spinner-grow text-muted spinner-grow-sm"></span>
					<span class="spinner-grow text-primary spinner-grow-sm"></span>
  					<span class="spinner-grow spinner-grow-sm"></span>
				  	Atualizando
				</button>
               	<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
       		</div>
			<div class="modal-body">
				<div id="error_container_modal_enviar_cad_ativo_equip"></div>
				<div class="row">
					<div class="col-sm-12">
						<div class="mb-3">
							<p class="h6">Deseja enviar os cadastros de monitoramento ativos para os equipamentos de fiscalização?</p>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-6 d-grid">
						<button type="button" class="btn btn-success" onclick="EnviarCadAtivoEquip()">SIM</button>
	        		</div>
	        		<div class="col-sm-6 d-grid">
						<button type="button" class="btn btn-danger" onclick="FecharModalEnviarCadAtivoEquip()">NÃO</button>
	        		</div>
				</div>
			</div>
	
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
	      	</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/muralha-digital/pages/monitorado/js/cadastro-monitorado.js"></script>
<script type="text/javascript" src="/muralha-digital/pages/monitorado/js/enviar_cad_ativo_equip.js"></script>