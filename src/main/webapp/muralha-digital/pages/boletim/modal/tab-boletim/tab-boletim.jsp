<meta charset="UTF-8">
<script
	src="/muralha-digital/pages/boletim/modal/tab-boletim/js/tab-boletim.js"></script>
<script
	src="/muralha-digital/pages/boletim/modal/tab-boletim/js/consulta.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-sticky-note" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-boletim">Dados do Boletim</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-boletim">
	<form id="formBoletim" onsubmit="event.preventDefault()">
		<div class="row">
			<div class="col-6 col-sm-6">
				<div class="mb-3">
					<div class="form-group">
						<label for="selTipoBoletim" class='form-label'>Tipo:</label> <select
							class="form-select" id="selTipoBoletim" name="selTipoBoletim">
							<option value="0" selected="selected">Selecione</option>
						</select>
					</div>
				</div>
			</div>
			<div class="col-3 col-sm-3">
				<div class="mb-3">
					<div class="form-group">
						<label for="selSituacaoBoletim" class='form-label'>Situação:</label>
						<select class="form-select" id="selSituacaoBoletim"
							name="selSituacaoBoletim">
							<option value="0" selected="selected">Selecione</option>
						</select>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-12 col-sm-12">
				<div class="mb-3">
					<label for="detalhamento" class="form-label">Detalhamento:</label>
					<textarea class="form-control" id="idDetalhamentoBoletim"
						oninput="atualizarContador()" name="detalhamento" rows="4"
						maxlength="5000" placeholder="Descreva os detalhes aqui..."></textarea>
					<div class="form-text">
						<span id="idContadorBoletim">0</span>/5000 caracteres
					</div>
				</div>
			</div>
		</div>
		<div class="row pt-3">
			<div class="container-sub-tittle">
				<i class="fa fa-map-pin" aria-hidden="true"></i>
				<h6 class="sub-tittle-modal-boletim">Dados do Local</h6>
			</div>
		</div>
		<hr class="sub-diviver-modal-boletim">
		<div class="row">
			<div class="col-sm-12 col-12">
				<div class="mb-3">
					<div class="form-group">
						<label for="idRuaBoletim" class="form-label">Rua:</label> <input
							name="ruaBoletim" id="idRuaBoletim"
							placeholder="Ex.: Nome da Rua" type="text" maxlength="500"
							class="form-control text">
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-4 col-4">
				<div class="mb-3">
					<div class="form-group">
						<label for="idBairroBoletim" class="form-label">Bairro:</label> <input
							name="bairroBoletim" id="idBairroBoletim"
							placeholder="Ex.: Nome do Bairro" type="text" maxlength="100"
							class="form-control text">
					</div>
				</div>
			</div>
			<div class="col-sm-4 col-4">
				<div class="mb-3">
					<div class="form-group">
						<label for="idNumeroBoletim" class="form-label">Número:</label> <input
							name="bairroBoletim" id="idNumeroBoletim" placeholder="Ex.: 1234"
							type="number" class="form-control text"
							oninput="this.value = this.value.slice(0, 4);">
					</div>
				</div>
			</div>
			<div class="col-4 col-sm-4">
				<div class="mb-3">
					<div class="form-group">
						<label for="selCidadeBoletim" class='form-label'>Cidade:</label> <select
							class="form-select" id="selCidadeBoletim" name="selCidadeBoletim">
							<option value="0" selected="selected">Selecione</option>
						</select>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-12 col-sm-12">
				<div class="mb-3">
					<div class="form-group">
						<label for="idComplementoBoletim" class='form-label'>Complemento:</label>
						<input name="complementoBoletim" id="complementoBoletim"
							placeholder="Ex.: Complemento do local" type="text"
							maxlength="100" class="form-control text">
					</div>
				</div>
			</div>
		</div>
		<div class="row pt-3" id="ocultar">
			<div class="container-sub-tittle">
				<i class="fa fa-check" aria-hidden="true"></i>
				<h6 class="sub-tittle-modal-boletim">Configurações para
					Atendimento</h6>
			</div>
		</div>
		<hr class="sub-diviver-modal-boletim" id="ocultar1" >
		<div class="row" id="ocultar2">
			<div class="form-group mt-3">
				<div class="form-check">
					<input class="form-check-input" type="checkbox"
						id="permitirAtendimento" name="permitirAtendimento"> <label
						class="form-check-label" for="permitirAtendimento">
						Permitir abertura de Atendimento </label>
				</div>
			</div>
		</div>
	</form>
</div>