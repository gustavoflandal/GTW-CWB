<meta charset="UTF-8">
<link rel="stylesheet"
	href="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-registroDeFato/css/tab-registroDeFato.css">
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-registroDeFato/js/tab-registroDeFato.js"></script>
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-registroDeFato/js/consulta.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-clipboard" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-registroDeFato">Dados do Registro de
				Fato</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-registroDeFato">

	<form id="formRegistroFatoComBoletim" onsubmit="event.preventDefault()">

		<div id="containerInfoDatas" class="row pt-2">
			<div class="col-4">
				<label class="form-label fw-bold">Data de Criação:</label>
				<p id="infoDataCriacao" class="form-text">--/--/----</p>
			</div>
			<div class="col-4">
				<label class="form-label fw-bold">Data de Encerramento:</label>
				<p id="infoDataEncerramento" class="form-text">--/--/----</p>
			</div>
			<div class="col-4">
				<label class="form-label fw-bold">Prazo Estimado:</label>
				<p id="infoPrazoEncerramento" class="form-text text-danger">--/--/----</p>
			</div>
		</div>

		<div class="row">
			<div class="col-6 col-sm-6">
				<div class="mb-3">
					<label for="id_tipoComBoletim" class="form-label">Tipo:</label> <select
						class="form-select" id="id_tipoComBoletim"
						name="id_tipoComBoletim"
						onchange="consultaTipoNatureza(this.value)">
						<option value="0" selected>Selecione</option>
					</select>
				</div>
			</div>

			<div class="col-6 col-sm-6">
				<div class="mb-3">
					<label for="id_naturezaTipo" class="form-label">Natureza do
						Tipo:</label> <select class="form-select" id="id_naturezaTipo"
						name="id_naturezaTipo" disabled>
						<option value="0" selected>Selecione o Tipo primeiro</option>
					</select>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-4 col-sm-4">
				<div class="mb-3">
					<label for="id_statusComBoletim" class="form-label">Situação:</label>
					<select class="form-select" id="id_statusComBoletim"
						name="id_statusComBoletim">
						<option value="0" selected>Selecione</option>
					</select>
				</div>
			</div>

			<div class="col-4 col-sm-4">
				<div class="mb-3">
					<label for="dataEvento" class="form-label">Data e Hora do
						Evento:</label> <input type="datetime-local" class="form-control"
						id="dataEvento" name="dataEvento">
				</div>
			</div>

			<div class="col-4 col-sm-4 d-flex align-items-end"
				id='containerEnvolvimentoArma'>
				<div class="mb-3">
					<div class="form-check">
						<input class="form-check-input" type="checkbox"
							id="envolvimentoArma" name="envolvimentoArma"> <label
							class="form-check-label" for="envolvimentoArma"> Existe
							Arma Envolvida </label>
					</div>
				</div>
			</div>
		</div>

		<div class="row" id='containerDetalhamentoFato'>
			<div class="col-12">
				<div class="mb-3">
					<label for="detalhamentoFato" class="form-label">Detalhamento:</label>
					<textarea class="form-control" id="detalhamentoFato"
						name="detalhamentoFato" rows="3"></textarea>
				</div>
			</div>
		</div>

		<div class="row pt-3">
			<div class="container-sub-tittle">
				<i class="fa fa-lock" aria-hidden="true"></i>
				<h6 class="sub-tittle-modal-registroDeFato">Configurações</h6>
			</div>
		</div>
		<hr class="sub-diviver-modal-registroDeFato">

		<div class="row">
			<div class="col-4 col-sm-4">
				<div class="mb-3">
					<label class="form-label d-block">Possui Boletim:</label>
					<div class="form-check">
						<input class="form-check-input" type="checkbox"
							id="tem_boletimComBoletim" name="tem_boletimComBoletim" checked
							disabled> <label class="form-check-label"
							for="tem_boletimComBoletim">Marcado</label>
					</div>
				</div>
			</div>

			<div class="col-4 col-sm-4">
				<div class="mb-3">
					<label class="form-label d-block">Privado:</label>
					<div class="form-check">
						<input class="form-check-input" type="checkbox"
							id="privadoComBoletim" name="privadoComBoletim"> <label
							class="form-check-label" for="privadoComBoletim">Marcar
							como privado</label>
					</div>
				</div>
			</div>

			<div class="col-4 col-sm-4">
				<div class="mb-3">
					<label class="form-label d-block">Permitir Atendimento:</label>
					<div class="form-check">
						<input class="form-check-input" type="checkbox"
							id="permitirAtendimento" name="permitirAtendimento"> <label
							class="form-check-label" for="permitirAtendimento">
							Permitir </label>
					</div>
				</div>
			</div>
		</div>

		<!-- Anotações -->
		<div class="row pt-3">
			<div class="container-sub-tittle">
				<i class="fa fa-sticky-note" aria-hidden="true"></i>
				<h6 class="sub-tittle-modal-registroDeFato">Anotações</h6>
			</div>
		</div>
		<hr class="sub-diviver-modal-registroDeFato">

		<div class="row mb-3">
			<div class="col-12 col-md-8">
				<label for="txtAnotacao" class="form-label">Nova anotação
					(max 300 caracteres):</label>
				<textarea id="txtAnotacao" class="form-control" rows="3"
					maxlength="300" placeholder="Digite a anotação..."
					aria-describedby="anotacaoHelp"></textarea>
				<div class="d-flex justify-content-between mt-1">
					<small id="anotacaoHelp" class="form-text text-muted">Máx.
						300 caracteres.</small> <small id="contadorAnotacao"
						class="form-text text-muted">300</small>
				</div>
			</div>

			<div
				class="col-12 col-md-4 d-flex align-items-end justify-content-md-end">
				<div class="w-100 w-md-auto">
					<button id="btnAddAnotacao" type="button"
						class="btn btn-primary btn-add-anotacao"
						onclick="adicionarAnotacao()">Adicionar anotação</button>
				</div>
			</div>
		</div>

		<!-- Tabela de anotações -->
		<div class="row">
			<div class="col-12">
				<div class="table-responsive">
					<table class="table table-sm table-bordered" id="tableAnotacoes">
						<thead class="table-light">
							<tr>
								<th style="width: 6%;">#</th>
								<th style="width: 58%;">Texto</th>
								<th style="width: 18%;">Usuário</th>
								<th style="width: 18%;">Data</th>
								<th style="width: 10%;">Ações</th>
							</tr>
						</thead>
						<tbody>
							<!-- Linhas serão geradas por JS -->
						</tbody>
					</table>
				</div>
			</div>
		</div>

	</form>
</div>