<meta charset="UTF-8">
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-grupo/js/consulta.js"></script>
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-grupo/js/tab-grupo.js"></script>
<link rel="stylesheet"
	href="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-grupo/css/tab-grupo.css">

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-users" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-comBoletim">Cadastro de Grupos</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-comBoletim" />
	<small id="avisoSelectGrupos" class="aviso-permissao d-none">
		Funcionalidade disponivel apenas para usuarios com permissao. </small>

	<form id="formGrupoComBoletim" onsubmit="event.preventDefault();">
		<div class="row mb-3">
			<div class="col-6">
				<label for="selectGruposComBoletim" class="form-label">Grupos</label>
				<select id="selectGruposComBoletim"
					onchange="atualizarGruposSelecionadosComBoletim()"
					class="selectpicker form-control form-control-sm bg-light border-dark text-dark fw-bold"
					multiple data-live-search="true" data-actions-box="true"
					data-size="5" data-style="form-select-sm"
					data-none-selected-text="Nenhum Grupo selecionado"
					data-select-all-text="Marcar todos"
					data-deselect-all-text="Desmarcar todos" data-container="body"
					style="z-index: 2000">
				</select> <small class="form-text text-muted mt-2"> Selecione os
					grupos desejados </small>
			</div>

			<div class="col-6">
				<label for="selectUsuariosComBoletim" class="form-label">Usuários</label>
				<select id="selectUsuariosComBoletim"
					onchange="atualizarUsuariosSelecionadosComBoletim()"
					class="selectpicker form-control form-control-sm bg-light border-dark text-dark fw-bold"
					multiple data-live-search="true" data-actions-box="true"
					data-size="5" data-style="form-select-sm"
					data-none-selected-text="Nenhum usuário selecionado"
					data-select-all-text="Marcar todos"
					data-deselect-all-text="Desmarcar todos" data-container="body"
					style="z-index: 2000">
				</select> <small class="form-text text-muted mt-2"> Selecione os
					usuários desejados </small>
			</div>
		</div>
	</form>
</div>
