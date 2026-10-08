<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css"
	xintegrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q=="
	crossorigin="anonymous" referrerpolicy="no-referrer" />

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js"
	xintegrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script src="/muralha-digital/assets/js/placa-caracter-coringa.js"></script>
<script type="text/javascript"
	src="/muralha-digital/pages/monitorado/js/cadastro-monitorado.js"></script>
<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<link rel="stylesheet"
	href="/muralha-digital/pages/monitorado/css/consulta.css">
<div id="loading-overlay" style="display: none;">
	<div class="loading-spinner"></div>
</div>
<div class="modal fade" id="modalCadMonitorado"
	data-bs-backdrop="static" data-bs-keyboard="false" tabindex="2005"
	aria-labelledby="modalCadMonitoradoLabel" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered modal-lg ">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="staticBackdropLabel">Cadastrar
					Monitorado</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div class="modal-body">
				<div id="error_container_modal_cad_veiculo_mon"></div>

				<!-- Nav Tabs -->
				<ul class="nav nav-tabs" id="tabMonitorado" role="tablist">
					<li class="nav-item" role="presentation">
						<button class="nav-link active" id="tab-dados-tab"
							data-bs-toggle="tab" data-bs-target="#tab-dados" type="button"
							role="tab" aria-controls="tab-dados" aria-selected="true">Dados
							Base</button>
					</li>
					<li class="nav-item d-none" role="presentation" id="li-tab-grupos">
						<button class="nav-link" id="tab-grupos-tab" data-bs-toggle="tab"
							data-bs-target="#tab-grupos" type="button" role="tab"
							aria-controls="tab-grupos" aria-selected="false">Aba do
							Supervisionado</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="tab-config-avancada-tab"
							data-bs-toggle="tab" data-bs-target="#tab-config-avancada"
							type="button" role="tab" aria-controls="tab-config-avancada"
							aria-selected="false">Configurações Avançadas</button>
					</li>
				</ul>

				<!-- Tab Content -->
				<div class="tab-content mt-3" style="min-height: 500px;"
					id="tabMonitoradoContent">
					<!-- Primeira Tab -->
					<div class="tab-pane fade show active" id="tab-dados"
						role="tabpanel" aria-labelledby="tab-dados-tab">

						<!-- ===================================================== -->
						<!-- INFORMAÇÕES DO MONITORAMENTO                          -->
						<!-- ===================================================== -->
						<div class="card border-0 shadow-sm">
							<div class="card-body">

								<div class="d-flex align-items-center gap-2 pb-1">
									<i class="fa fa-bell fs-5"></i>

									<h6 class="sub-tittle-modal-boletim mb-0">Informações do
										Monitorado</h6>
								</div>

								<hr class="mt-2 mb-3">

								<!-- Nome / Ativo -->
								<div class="row">

									<div class="col-sm-11">
										<div class="mb-3">
											<div class="form-group">

												<label for="idNomeMonitorado" class="form-label">
													Nome: </label> <input name="nomeMonitorado" id="idNomeMonitorado"
													placeholder="Ex.: Nome" type="text" maxlength="85"
													class="form-control text nomemonitorado">

											</div>
										</div>
									</div>

									<div class="col-sm-1">
										<div class="mb-3">
											<div class="form-group">

												<label for="cadastroAtivoCad" class="form-label">
													Ativo </label> <input type="checkbox" checked
													class="form-check-input item form-control"
													id="cadastroAtivoCad" disabled>

											</div>
										</div>
									</div>

								</div>

								<!-- Tipo Alerta/Ocorrência -->
								<div class="row">

									<div class="col-sm-12">
										<div class="mb-3">
											<div class="form-group">

												<label for="selTipoAlertaOcorrenciaCad" class="form-label">

													Tipo Alerta/Ocorrência: </label> <select class="form-select"
													id="selTipoAlertaOcorrenciaCad"
													name="selTipoAlertaOcorrenciaCad"
													onchange="onGerenciarAbaSupervisionadoCad()">

													<option value="0" selected="selected">--Selecione
														o Tipo Alerta/Ocorrência--</option>

												</select>

											</div>
										</div>
									</div>

								</div>

								<!-- Data Início / Data Fim -->
								<div class="row">

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="dataInicioCad" class="form-label"> Data
													Inicio: </label> <input type="text" class="form-control item date"
													id="dataInicioCad">

											</div>
										</div>
									</div>

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="dataFimCad" class="form-label"> Data
													Fim: </label> <input type="text" class="form-control item date"
													id="dataFimCad">

											</div>
										</div>
									</div>

								</div>

								<!-- Descrição -->
								<div class="row">

									<div class="col-sm-12">
										<div class="mb-3">
											<div class="form-group">

												<label for="descricaoCad" class="form-label">
													Descrição: </label>

												<textarea id="descricaoCad" class="form-control"
													maxlength="200" aria-label="With textarea"></textarea>

											</div>
										</div>
									</div>

								</div>

							</div>
						</div>


						<!-- ===================================================== -->
						<!-- DADOS DO VEÍCULO                                     -->
						<!-- ===================================================== -->
						<div class="card border-0 shadow-sm">
							<div class="card-body">

								<div class="d-flex align-items-center gap-2 pb-1">
									<i class="fa fa-car fs-5"></i>

									<h6 class="sub-tittle-modal-boletim mb-0">Dados do Veículo
									</h6>
								</div>

								<hr class="mt-2 mb-3">

								<!-- Placa / Classe -->
								<div class="row">

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="idPlacaCad" class="form-label"> Placa: </label>

												<input name="placaCad" id="idPlacaCad"
													placeholder="Ex.: AAA1111" type="text" maxlength="7"
													class="form-control item text-uppercase placa-caracter-coringa-4">

											</div>
										</div>
									</div>

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="selClasseVeiculoCad" class="form-label">
													Classe: </label> <select class="form-select"
													id="selClasseVeiculoCad" name="selClasseVeiculoCad">

													<option value="0" selected="selected">-- Selecione
														a Classe --</option>

												</select>

											</div>
										</div>
									</div>

								</div>

								<!-- Marca / Modelo -->
								<div class="row">

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="selMarcaCad" class="form-label"> Marca:
												</label> <select class="selectpicker form-control" id="selMarcaCad"
													name="selMarcaCad" data-live-search="true" data-size="8"
													data-width="100%"
													data-none-selected-text="-- Selecione a Marca --"
													data-live-search-placeholder="Pesquisar marca...">

													<option value="0">-- Selecione a Marca --</option>

												</select>

											</div>
										</div>
									</div>

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="selModeloCad" class="form-label">
													Modelo: </label> <select class="selectpicker form-control"
													id="selModeloCad" name="selModeloCad"
													data-live-search="true" data-size="8" data-width="100%"
													data-none-selected-text="-- Selecione o Modelo --"
													data-live-search-placeholder="Pesquisar modelo...">

													<option value="0">-- Selecione o Modelo --</option>

												</select>

											</div>
										</div>
									</div>

								</div>

								<!-- Cor / Texto Adesivo -->
								<div class="row">

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="selCorCad" class="form-label"> Cor: </label> <select
													class="form-select" id="selCorCad" name="selCorCad">

													<option value="0" selected="selected">-- Selecione
														a Cor --</option>

												</select>

											</div>
										</div>
									</div>

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="textoAdesivoCad" class="form-label">
													Texto Adesivo: </label> <input name="textoAdesivoCad"
													id="textoAdesivoCad"
													placeholder="Ex.: Identificação do veículo" type="text"
													maxlength="25" class="form-control item">

											</div>
										</div>
									</div>

								</div>

							</div>
						</div>


						<!-- ===================================================== -->
						<!-- CONTROLES DO MONITORADO                              -->
						<!-- ===================================================== -->
						<div class="card border-0 shadow-sm">
							<div class="card-body">

								<div class="d-flex align-items-center gap-2 pb-1">
									<i class="fa fa-cog fs-5"></i>

									<h6 class="sub-tittle-modal-boletim mb-0">Controles do
										Monitorado</h6>
								</div>

								<hr class="mt-2 mb-3">

								<div class="row g-4 mb-2">

									<!-- Controles -->
									<div class="col-md-6">

										<!-- Privado -->
										<div class="form-check form-switch form-check-lg mb-4">

											<input class="form-check-input" type="checkbox"
												id="checkboxPrivado" role="switch"> <label
												class="form-check-label fw-medium" for="checkboxPrivado">

												<i class="bi bi-lock-fill me-2 text-muted"></i> Privado

											</label>

										</div>

										<!-- Supervisionado -->
										<div class="form-check form-switch form-check-lg mb-4">

											<input class="form-check-input" type="checkbox"
												id="checkboxSupervisionado" role="switch"
												onchange="toggleSupervisionadoExtrasCad(this)"> <label
												class="form-check-label fw-medium"
												for="checkboxSupervisionado"> <i
												class="bi bi-eye-fill me-2 text-muted"></i> Supervisionado

											</label>

										</div>

										<!-- Monitorar somente este -->
										<div class="form-check form-switch form-check-lg">

											<input class="form-check-input" type="checkbox"
												id="checkboxMonitorarSomenteEste" role="switch"
												onchange="onMonitorarSomenteEsteChange(this)"> <label
												class="form-check-label fw-medium"
												for="checkboxMonitorarSomenteEste"
												title="Ao marcar esta opção, você passará a monitorar somente este veículo e demais veículos supervisionados. Desta forma, não receberá outros alertas de Monitoramento Simples">

												<i class="bi bi-bell-fill me-2 text-muted"></i> Receber
												somente Alertas deste veiculo

											</label>

										</div>

									</div>

									<!-- Usuário Responsável -->
									<div class="col-md-6">

										<div id="containerUsuarioResponsavel">

											<div class="form-group">

												<label for="selUsuarioResponsavel" class="form-label">

													Usuário Responsável: </label> <select
													class="form-select selUsuarioResponsavel"
													id="selUsuarioResponsavel" name="selUsuarioResponsavel"
													onchange="onGerenciarAbaSupervisionadoCad()">

													<option value="0" selected="selected">-- Selecione
														--</option>

												</select>

											</div>

										</div>

									</div>

								</div>

							</div>
						</div>

					</div>


					<!-- Segunda Tab (Aba do Supervisionado) -->
					<div class="tab-pane fade" id="tab-grupos" role="tabpanel"
						aria-labelledby="tab-grupos-tab">
						<div class="row container">
							<div class="row pb-1">
								<div
									style="display: flex; align-items: center; column-gap: 10px;">
									<i class="fa fa-users fs-5 d-flex align-items-center"
										aria-hidden="true"></i>
									<h6 style="margin-bottom: 0px;"
										class="sub-tittle-modal-boletim">Detalhes do
										Supervisionado</h6>
								</div>
							</div>
							<hr class="mb-0">
							<!-- Campos extras de Supervisionado -->
							<div id="supervisionadoExtras" class="row g-3 mt-2 d-none">

								<div class="col-md-6">
									<div class="card border-1 shadow-sm h-100">
										<div class="card-body">
											<h6 class="card-title mb-2">
												<i class="fas fa-bell me-1"></i> Gerenciamento de Acesso
											</h6>

											<label for="selAcaoGrupoPopupCad" class="form-label">Grupos</label>
											<select id="selAcaoGrupoPopupCad"
												class="selectpicker form-control form-control-sm bg-light border-dark text-dark fw-bold"
												multiple data-actions-box="true" data-size="5"
												data-style="form-select-sm"
												data-none-selected-text="Nenhum item"
												data-select-all-text="Marcar todos"
												data-deselect-all-text="Desmarcar todos">
											</select> <small class="form-text text-muted mt-2">Configure
												grupos para notificações por canal.</small>
										</div>
									</div>
								</div>

							</div>
						</div>
					</div>

					<!-- Terceira Tab (Configurações Avançadas) -->
					<div class="tab-pane fade" id="tab-config-avancada" role="tabpanel"
						aria-labelledby="tab-config-avancada-tab">
						<div class="container-fluid">
							<div class="row">
								<div class="col-12">
									<div
										style="display: flex; align-items: center; column-gap: 10px;">
										<i class="fa fa-cogs fs-5 d-flex align-items-center"
											aria-hidden="true"></i>
										<h6 style="margin-bottom: 0px;"
											class="sub-tittle-modal-boletim">Configurações Avançadas</h6>
									</div>
									<hr class="mt-2 mb-3">
								</div>
							</div>
							<div class="row g-3">
								<!-- Cards Semelhança de Placas -->
								<div class="col-md-6">
									<div class="card border-1 shadow-sm h-100">
										<div class="card-body">
											<h6 class="card-title mb-2">
												<i class="fas fa-search me-1"></i> Semelhança de Placas
											</h6>
											<select id="nivelSemelhancaPlaca"
												class="form-select form-select-sm">
												<option value="0">0 - Exatamente igual</option>
												<option value="1">1 caractere divergente</option>
												<option value="2">2 caracteres divergentes</option>
												<option value="3">3 caracteres divergentes</option>
												<option value="4">4 caracteres divergentes</option>
											</select> <small class="form-text text-muted mt-2">Quantidade
												de caracteres divergentes permitidos.</small>
										</div>
										<div class="card-body">
											<h6 class="card-title mb-2">
												<i class="fas fa-clock me-1"></i> Intervalo de Tempo
											</h6>
											<div>
												<input type="checkbox" class="form-check-input"
													id="checkboxTempoCadastro" role="switch"
													onclick="verificacaoIntervaloTempo()"> <label
													class="form-check-label fw-medium"
													for="checkboxTempoCadastro"> Ativar Intervalo de
													Tempo </label>
											</div>
											<div class="row">
												<div class="col-md-6">
													<h7 class="card-title">Hora Início</h7>
													<select id="intervaloTempoInicioCadastro"
														class="form-select form-select-sm" disabled="true"
														onchange="verificacaoIntervaloTempo()">
														<option value="00">00:00</option>
														<option value="01">01:00</option>
														<option value="02">02:00</option>
														<option value="03">03:00</option>
														<option value="04">04:00</option>
														<option value="05">05:00</option>
														<option value="06">06:00</option>
														<option value="07">07:00</option>
														<option value="08">08:00</option>
														<option value="09">09:00</option>
														<option value="10">10:00</option>
														<option value="11">11:00</option>
														<option value="12">12:00</option>
														<option value="13">13:00</option>
														<option value="14">14:00</option>
														<option value="15">15:00</option>
														<option value="16">16:00</option>
														<option value="17">17:00</option>
														<option value="18">18:00</option>
														<option value="19">19:00</option>
														<option value="20">20:00</option>
														<option value="21">21:00</option>
														<option value="22">22:00</option>
														<option value="23">23:00</option>
													</select>
												</div>
												<div class="col-md-6">
													<h7 class="card-title">Hora Fim</h7>
													<select id="intervaloTempoFimCadastro"
														class="form-select form-select-sm" disabled="true"
														onchange="verificacaoIntervaloTempo()">
														<option value="00">00:59</option>
														<option value="01">01:59</option>
														<option value="02">02:59</option>
														<option value="03">03:59</option>
														<option value="04">04:59</option>
														<option value="05">05:59</option>
														<option value="06">06:59</option>
														<option value="07">07:59</option>
														<option value="08">08:59</option>
														<option value="09">09:59</option>
														<option value="10">10:59</option>
														<option value="11">11:59</option>
														<option value="12">12:59</option>
														<option value="13">13:59</option>
														<option value="14">14:59</option>
														<option value="15">15:59</option>
														<option value="16">16:59</option>
														<option value="17">17:59</option>
														<option value="18">18:59</option>
														<option value="19">19:59</option>
														<option value="20">20:59</option>
														<option value="21">21:59</option>
														<option value="22">22:59</option>
														<option value="23">23:59</option>
													</select>
												</div>
												<small id="avisoIntervaloTempoCadastro"
													class="form-text text-danger mt-2" hidden>A hora de
													Início não pode ser maior ou igual que a hora de Fim.</small>
											</div>
										</div>
									</div>
								</div>

								<!-- Cards Equipamentos -->
								<div class="col-md-6">
									<div class="card border-1 shadow-sm h-100">
										<div class="card-body">
											<h6 class="card-title mb-2">
												<i class="fas fa-map-marker-alt me-1"></i> Locais
											</h6>
											<select id="selEquipamentoCad"
												class="selectpicker form-control form-control-sm bg-light border-dark text-dark fw-bold"
												multiple data-live-search="true" data-actions-box="true"
												data-size="5" data-style="form-select-sm"
												data-none-selected-text="Nenhum Local selecionado"
												data-select-all-text="Marcar todos"
												data-deselect-all-text="Desmarcar todos"
												style="z-index: 2000">
												<!-- opções carregadas via JS -->
											</select> <small class="form-text text-muted mt-2">Selecione
												os locais específicos para monitorar ou deixe sem seleção
												para incluir todos.</small>
										</div>
									</div>
								</div>
							</div>

							<!-- Tabela de Horários Permitidos -->
							<div class="row mt-4">
								<div class="col-12">
									<div
										class="d-flex justify-content-between align-items-center mb-2">
										<h6 class="mb-0">
											<i class="fas fa-clock me-1"></i> Horários Permitidos
										</h6>
										<button title="Adicionar Horário" type="button"
											class="btn btn-sm btn-outline-primary"
											onclick="adicionarLinhaHorarioCad()">
											<i class="fa fa-plus"></i>
										</button>
									</div>

									<div class="table-responsive" style="max-height: 260px;">
										<table
											class="table table-sm table-striped table-hover align-middle mb-0"
											id="tabelaHorariosPermitidos">
											<thead class="table-light sticky-top" style="z-index: 500">
												<tr>
													<th style="width: 35%;">Dia da Semana</th>
													<th style="width: 25%;">Hora Início</th>
													<th style="width: 25%;">Hora Fim</th>
													<th style="width: 15%;" class="text-center">Ações</th>
												</tr>
											</thead>
											<tbody>
												<!-- Linhas adicionadas dinamicamente -->
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>

				</div>
			</div>

			<div class="modal-footer">
				<button type="button" class="btn btn-secondary"
					onclick="fecharModalCad()">Fechar</button>
				<button id="btnLimparCamposModal" type="button"
					class="btn btn-warning" onclick="limparCampos()">Limpar</button>
				<button id="btnSalvarModalCadastro" type="button"
					class="btn btn-primary" onclick="cadastrarMonitorado()">Salvar</button>
			</div>
		</div>
	</div>
</div>