<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<link rel="stylesheet" href="/muralha-digital/pages/linha-tempo/assets/css/modal-linha-tempo.css">

<div class="modal fade" id="linhaTempo" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="linhaTempoLabel" aria-hidden="true">
<!--     <div class="modal-dialog"> -->
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="linhaTempoLabel">Linha do Tempo</h5>
                <button id="spinAtualizaLinhaTempo" class="btn btn-warning btn-sm pull-right ms-2" disabled>
					<span class="spinner-grow text-muted spinner-grow-sm"></span>
					<span class="spinner-grow text-primary spinner-grow-sm"></span>
  					<span class="spinner-grow spinner-grow-sm"></span>
				  	Atualizando
				</button>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body" id="modalLinhaTempoBody"></div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="fecharModalLinhaTempo()">Fechar</button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="/muralha-digital/pages/linha-tempo/assets/js/modal-linha-tempo.js"></script>