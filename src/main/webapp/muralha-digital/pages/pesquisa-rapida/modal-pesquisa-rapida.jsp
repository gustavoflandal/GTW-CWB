<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<link
	href="https://cdn.jsdelivr.net/npm/lightbox2@2/dist/css/lightbox.min.css"
	rel="stylesheet" />
<script
	src="https://cdn.jsdelivr.net/npm/lightbox2@2/dist/js/lightbox.min.js"></script>
<link rel="stylesheet"
	href="/muralha-digital/assets/css/pagina-carregando.css">
<script src="/muralha-digital/pages/pesquisa-rapida/js/consulta.js"></script>
<link rel="stylesheet"
	href="/muralha-digital/pages/pesquisa-rapida/css/modal-pesquisa-rapida.css">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<div class="modal fade" id="modalPesquisaRapida" tabindex="-1"
	aria-labelledby="modalPesquisaRapidaLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg modal-dialog-scrollable">
		<div class="modal-content">
			<div class="modal-header bg-light">
				<h5 class="modal-title" id="modalPesquisaRapidaLabel">Pesquisa
					Rápida</h5>
				<button type="button" class="btn-close"
					onclick="document.getElementById('modalPesquisaRapidaClose').click()"
					aria-label="Fechar"></button>
			</div>
			<div class="modal-body bg-light">
				<div id="resultadoPesquisa" style="display: block;">
					<!-- Bloco de informações do cidadão -->
					<div class="mb-3 px-2 small text-light-emphasis">
						<div class="d-flex flex-wrap gap-4 mb-2">
							<div>
								<strong><i class="fa fa-user" aria-hidden="true"></i>
									Nome:</strong> <span id="infoNome">-</span>
							</div>
							<div>
								<strong><i class="fa fa-id-card" aria-hidden="true"></i>
									CPF:</strong> <span id="infoCPF">-</span>
							</div>
						</div>

						<div>
							<strong><i class="fa fa-car" aria-hidden="true"></i>
								Placas:</strong> <span id="infoVeiculos"
								class="d-inline-flex flex-wrap gap-1 mt-1"></span>
						</div>
					</div>
					<hr />
					<div class="row g-2 text-center small">
						<div class="col-3">
							<div class="card p-2 shadow-sm d-flex flex-column h-100">
								<strong>Registro de Fato S/ Boletins</strong>
								<p id="registroDeFatoSemboletins" class="mb-1">0 ocorrências</p>
								<p id="abordagensSemBoletins" class="mb-3">0 abordagens</p>
								<div class="btn-group mt-auto" role="group"
									aria-label="Registro de Fato S/ Boletins">
									<!-- Botão Detalhes -->
									<button id="btnRegistroDeFatoSemBoletins"
										class="btn btn-outline-primary btn-sm"
										onclick="abrirTodosRegistroDeFatoBoletins('SemBoletim')">
										<i class="fa fa-file me-1"></i> Detalhes
									</button>
									<!-- Botão Cadastrar (apenas ícone) -->
									<button id="btnCadastrarRegistroDeFatoSemBoletins"
										class="btn btn-success btn-sm"
										title="Cadastrar Registro de Fato sem Boletim"
										onclick="cadastrarRegistroDeFato('SemBoletim')">
										<i class="fa fa-plus"></i>
									</button>
								</div>
							</div>
						</div>
						<div class="col-3">
							<div class="card p-2 shadow-sm d-flex flex-column h-100">
								<strong>Registro de Fato C/ Boletins</strong>
								<p id="registroDeFatoComboletins" class="mb-1">0 ocorrências</p>
								<p id="abordagensComBoletins" class="mb-3">0 abordagens</p>
								<div class="btn-group mt-auto" role="group"
									aria-label="Registro de Fato C/ Boletins">
									<!-- Botão Detalhes -->
									<button id="btnRegistroDeFatoComBoletins"
										class="btn btn-outline-info btn-sm"
										onclick="abrirTodosRegistroDeFatoBoletins('ComBoletim')">
										<i class="fa fa-file-alt me-1"></i> Detalhes
									</button>
									<!-- Botão Cadastrar (apenas ícone) -->
									<button id="btnCadastrarRegistroDeFatoComBoletins"
										class="btn btn-success btn-sm"
										title="Cadastrar Registro de Fato com Boletim"
										onclick="cadastrarRegistroDeFato('ComBoletim')">
										<i class="fa fa-plus"></i>
									</button>
								</div>
							</div>
						</div>
						<div class="col-3">
							<div class="card p-2 shadow-sm d-flex flex-column h-100">
								<strong>Alertas</strong>
								<p id="alarmesSupervisionado" class="mb-1">SUPERVISIONADO: 0</p>
								<p id="alarmesSimples" class="mb-3">SIMPLES: 0</p>
								<button id="btnDetalhesAlertas"
									class="btn btn-outline-danger btn-sm mt-auto"
									onclick="abrirTodosAlertas()">Detalhes</button>
							</div>
						</div>
						<div class="col-3">
							<div class="card p-2 shadow-sm d-flex flex-column h-100">
								<strong>Veículos Monitorados</strong>
								<p id="veiculosMonitoradoPesquisaRapida" class="mb-1">0
									Veículos</p>
								<button id="btnDetalhesVeiculoMonitorado"
									class="btn btn-outline-warning btn-sm mt-auto"
									onclick="abrirTodosVeiculosMonitorado()">Detalhes</button>
							</div>
						</div>
					</div>

					<div class="my-2">
						<h6 class="small">Imagens de Passagens Veiculares</h6>
						<div id="galeriaImagensCarouselContainer"></div>
					</div>
				</div>

				<div id="formCadastroFato" style="display: none;" class="mt-3">
					<div class="card p-3 shadow-sm">
						<h6 class="mb-3">
							Novo Registro de Fato para a Placa: <strong
								id="placaParaCadastro"></strong>
						</h6>
						<div class="mb-3">
							<label for="naturezaFato" class="form-label small"><strong>Natureza
									do Fato (Obrigatório)</strong></label> <select
								class="form-select form-select-sm" id="naturezaFato">
								<option value="" selected>Selecione a natureza...</option>
								<option value="furto">Furto de Veículo</option>
								<option value="roubo">Roubo de Veículo</option>
								<option value="suspeita">Veículo Suspeito</option>
								<option value="outros">Outros</option>
							</select>
						</div>
						<div class="d-flex justify-content-end gap-2">
							<button type="button" class="btn btn-secondary btn-sm"
								onclick="document.getElementById('formCadastroFato').style.display='none'">Cancelar</button>
							<button type="button" class="btn btn-primary btn-sm"
								onclick="handleSaveFato()">Salvar</button>
						</div>
					</div>
				</div>
				<button type="button" id="modalPesquisaRapidaClose" class="d-none"
					data-bs-dismiss="modal"></button>
			</div>
		</div>
	</div>
</div>

<script
	src="/muralha-digital/pages/pesquisa-rapida/js/modal-pesquisa-rapida.js"></script>
