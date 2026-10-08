<link rel="stylesheet"
	href="/muralha-digital/pages/pesquisa-rapida/css/dropdown-pesquisa-rapida.css">
<!-- Script do componente -->
<script
	src="/muralha-digital/pages/pesquisa-rapida/js/dropdown-pesquisa-rapida.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<!-- Modal de resultado -->
<%@ include
	file="/muralha-digital/pages/pesquisa-rapida/modal-pesquisa-rapida.jsp"%>
<div class="position-relative ms-auto" style="z-index: 1050;">
	<!-- Seletor -->
	<div class="dropdown d-inline-block position-relative"
		style="z-index: 1060;">
		<button class="btn btn-sm btn-white border dropdown-toggle w-100"
			type="button" id="dropdownTipoBusca" data-bs-toggle="dropdown"
			aria-expanded="false" style="min-width: 155px;">
			<span id="dropdownTipoBuscaLabel">Pesquisa Rápida</span>
		</button>
		<ul class="dropdown-menu" aria-labelledby="dropdownTipoBusca">
			<li><a class="dropdown-item active" href="#"
				data-value="PesquisaRapida">Pesquisa Rápida</a></li>
			<li><a class="dropdown-item" href="#" data-value="CPF">CPF</a></li>
			<li><a class="dropdown-item" href="#" data-value="Nome">Nome</a></li>
			<li><a class="dropdown-item" href="#" data-value="Veiculo">Veículo</a></li>
		</ul>
	</div>

	<!-- Input flutuante como já está -->
	<div id="grupoInputBusca"
		class="dropdown-menu p-2 shadow show d-none position-absolute"
		style="min-width: 240px; top: 100%; left: 0;">
		<button type="button" id="btnCancelarBusca" class="btn-fechar-custom"
			title="Cancelar busca"></button>

		<div class="input-group input-group-sm mt-4">
			<input type="text" class="form-control" placeholder="Digite aqui"
				id="valorBusca" autocomplete="off">
			<button class="btn btn-primary" id="btnBuscarDropdown">
				<i class="fa fa-search" aria-hidden="true"></i>
			</button>
			<ul id="autocomplete-results"
				class="list-group position-absolute top-100 start-0 w-100"></ul>
		</div>
	</div>

	<!-- Dropdown flutuante com input e botão cancelar -->
	<div id="grupoInputBusca"
		class="dropdown-menu p-2 shadow show d-none position-absolute"
		style="min-width: 240px; top: 100%; left: 0;">
		<!-- Botão X no canto superior direito -->
		<button type="button" id="btnCancelarBusca" class="btn-fechar-custom"
			title="Cancelar busca"></button>

		<!-- Campo input + botão buscar -->
		<div class="input-group input-group-sm mt-4">
			<input type="text" class="form-control" placeholder="Digite aqui"
				id="valorBusca">
			<button class="btn btn-primary" id="btnBuscarDropdown">
				<i class="fa fa-search" aria-hidden="true"></i>
			</button>
		</div>
	</div>
</div>
