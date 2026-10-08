<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css"
	integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q=="
	crossorigin="anonymous" referrerpolicy="no-referrer" />
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.16/jquery.mask.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js"
	integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script src="/muralha-digital/assets/js/placa-caracter-coringa.js"></script>
<script type="text/javascript"
	src="/muralha-digital/pages/monitorado/js/editar-monitorado.js"></script>
<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<link rel="stylesheet"
	href="/muralha-digital/pages/monitorado/css/consulta.css" />
<%@page import="com.consilux.model.Acesso"%>
<% Acesso acesso = new
Acesso(request, response, true); if (!acesso.verificaAcesso()) return; String
usuarioID = acesso.getUsuario().getId().toString(); %>

<script>
  window.usuarioID = "<%=usuarioID%>";
</script>
<div id="loading-overlay" style="display: none">
	<div class="loading-spinner"></div>
</div>
<br />
<div class="modal fade" id="modalEditarMonitorado"
	data-bs-backdrop="static" data-bs-keyboard="false" tabindex="2005"
	aria-labelledby="modalEditarMonitoradoLabel" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered modal-lg">
		<div class="modal-content">
			<input type="hidden" id="idMonitoradoHidden">
			<div class="modal-header">
				<h5 id="tituloModalEditarMonitorado" class="modal-title">
					Editar Monitorado</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"
					aria-label="Close"></button>
			</div>
			<div class="modal-body">
				<div id="error_container_modal_editar_veiculo_mon"></div>

				<!-- Nav Tabs -->
				<ul class="nav nav-tabs" id="tabMonitoradoEdit" role="tablist">
					<li class="nav-item" role="presentation">
						<button class="nav-link active" id="tab-dados-tab-edit"
							data-bs-toggle="tab" data-bs-target="#tab-dados-edit"
							type="button" role="tab" aria-controls="tab-dados-edit"
							aria-selected="true">Dados Base</button>
					</li>
					<li class="nav-item d-none" role="presentation"
						id="li-tab-grupos-edit">
						<button class="nav-link" id="tab-grupos-tab-edit"
							data-bs-toggle="tab" data-bs-target="#tab-grupos-edit"
							type="button" role="tab" aria-controls="tab-grupos-edit"
							aria-selected="false">Aba do Supervisionado</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="tab-config-avancada-tab-edit"
							data-bs-toggle="tab" data-bs-target="#tab-config-avancada-edit"
							type="button" role="tab" aria-controls="tab-config-avancada-edit"
							aria-selected="false">Configurações Avançadas</button>
					</li>
				</ul>

				<!-- Tab Content -->
				<div class="tab-content mt-3" style="min-height: 500px"
					id="tabMonitoradoContentEdit">
					<!-- Primeira Tab -->
					<div class="tab-pane fade show active" id="tab-dados-edit"
						role="tabpanel" aria-labelledby="tab-dados-tab-edit">
						<!-- ===================================================== -->
						<!-- INFORMAÇÕES DO MONITORAMENTO                          -->
						<!-- ===================================================== -->
						<div class="card border-0 shadow-sm">
							<div class="card-body">

								<div class="d-flex align-items-center gap-2 pt-3 pb-1">
									<i class="fa fa-info-circle fs-5"></i>
									<h6 class="sub-tittle-modal-boletim mb-0">Informações do
										Monitorado</h6>
								</div>

								<hr class="mt-0" />

								<!-- Nome + Ativo -->
								<div class="row">

									<div class="col-sm-11">
										<div class="mb-3">
											<div class="form-group">
												<label for="idNomeEdit" class="form-label"> Nome: </label> <input
													name="nomeMonitorado" id="idNomeEdit"
													placeholder="Ex.: Nome" type="text" maxlength="85"
													class="form-control text nomemonitorado">
											</div>
										</div>
									</div>

									<div class="col-sm-1">
										<div class="mb-3">
											<div class="form-group">

												<label for="cadastroAtivoEdit" class="form-label">
													Ativo </label> <input type="checkbox" checked
													class="form-check-input item form-control"
													id="cadastroAtivoEdit" onchange="AtivarInativarEdit()">

											</div>
										</div>
									</div>

								</div>

								<!-- Tipo Alerta/Ocorrência -->
								<div class="row">

									<div class="col-sm-12">
										<div class="mb-3">
											<div class="form-group">

												<label for="selTipoAlertaOcorrenciaEdit" class="form-label">
													Tipo Alerta/Ocorrência: </label> <select class="form-select"
													id="selTipoAlertaOcorrenciaEdit"
													name="selTipoAlertaOcorrenciaEdit"
													onchange="onGerenciarAbaSupervisionadoEdit()">

													<option value="0" selected="selected">--Selecione
														o Tipo Alerta/Ocorrência--</option>

												</select>

											</div>
										</div>
									</div>

								</div>

								<!-- Datas -->
								<div class="row">

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="dataInicioEdit" class="form-label"> Data
													Inicio: </label> <input type="text" class="form-control item date"
													id="dataInicioEdit">

											</div>
										</div>
									</div>

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="dataFimEdit" class="form-label"> Data
													Fim: </label> <input type="text" class="form-control item date"
													id="dataFimEdit">

											</div>
										</div>
									</div>

								</div>

								<!-- Descrição -->
								<div class="row">

									<div class="col-sm-12">
										<div class="mb-3">
											<div class="form-group">

												<label for="descricaoEdit" class="form-label">
													Descrição: </label>

												<textarea id="descricaoEdit" class="form-control"
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

								<div class="d-flex align-items-center gap-2 pt-3 pb-1">
									<i class="fa fa-car fs-5"></i>
									<h6 class="sub-tittle-modal-boletim mb-0">Dados do Veículo
									</h6>
								</div>

								<hr class="mt-0" />

								<!-- Placa + Classe -->
								<div class="row">

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="idPlacaEdit" class="form-label"> Placa:
												</label> <input name="placaEdit" id="idPlacaEdit"
													placeholder="Ex.: AAA1111" type="text" maxlength="7"
													class="form-control item text-uppercase placa-caracter-coringa-4">

											</div>
										</div>
									</div>

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="selClasseVeiculoEdit" class="form-label">
													Classe: </label> <select class="form-select"
													id="selClasseVeiculoEdit" name="selClasseVeiculoEdit">

													<option value="0">-- Selecione a Classe --</option>

												</select>

											</div>
										</div>
									</div>

								</div>

								<!-- Marca + Modelo -->
								<div class="row">

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="selMarcaEdit" class="form-label"> Marca:
												</label> <select id="selMarcaEdit" name="selMarcaEdit"
													class="selectpicker form-control" data-live-search="true"
													data-size="8" data-width="100%"
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

												<label for="selModeloEdit" class="form-label">
													Modelo: </label> <select id="selModeloEdit" name="selModeloEdit"
													class="selectpicker form-control" data-live-search="true"
													data-size="8" data-width="100%"
													data-none-selected-text="-- Selecione o Modelo --"
													data-live-search-placeholder="Pesquisar modelo...">

													<option value="0">-- Selecione o Modelo --</option>

												</select>

											</div>
										</div>
									</div>

								</div>

								<!-- Cor + Texto Adesivo -->
								<div class="row">

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="selCorEdit" class="form-label"> Cor: </label> <select
													class="form-select" id="selCorEdit" name="selCorEdit">

													<option value="0">-- Selecione a Cor --</option>

												</select>

											</div>
										</div>
									</div>

									<div class="col-sm-6">
										<div class="mb-3">
											<div class="form-group">

												<label for="textoAdesivoEdit" class="form-label">
													Texto Adesivo: </label> <input type="text" id="textoAdesivoEdit"
													name="textoAdesivoEdit" maxlength="25"
													placeholder="Ex.: Identificação do veículo"
													class="form-control item">

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

								<div class="d-flex align-items-center gap-2 pt-3 pb-1">
									<i class="fa fa-cog fs-5"></i>

									<h6 class="sub-tittle-modal-boletim mb-0">Controles do
										Monitorado</h6>
								</div>

								<hr class="mt-0" />

								<div class="row g-4 mb-2">

									<div class="col-md-6">

										<div class="form-check form-switch form-check-lg mb-4">

											<input class="form-check-input" type="checkbox"
												id="checkboxPrivadoEdit" role="switch"> <label
												class="form-check-label fw-medium" for="checkboxPrivadoEdit">

												<i class="bi bi-lock-fill me-2 text-muted"></i> Privado

											</label>

										</div>


										<div class="form-check form-switch form-check-lg mb-4">

											<input class="form-check-input" type="checkbox"
												id="checkboxSupervisionadoEdit" role="switch"
												onchange="toggleSupervisionadoExtrasEdit(this)"> <label
												class="form-check-label fw-medium"
												for="checkboxSupervisionadoEdit"> <i
												class="bi bi-eye-fill me-2 text-muted"></i> Supervisionado

											</label>

										</div>


										<div class="form-check form-switch form-check-lg">

											<input class="form-check-input" type="checkbox"
												id="checkboxMonitorarSomenteEsteEdit" role="switch"
												onchange="onMonitorarSomenteEsteChangeEdit(this)"> <label
												class="form-check-label fw-medium"
												for="checkboxMonitorarSomenteEsteEdit"
												title="Ao marcar esta opção, você passará a monitorar somente este veículo e demais veículos supervisionados. Desta forma, não receberá outros alertas de Monitoramento Simples">

												<i class="bi bi-bell-fill me-2 text-muted"></i> Receber
												somente Alertas deste veiculo

											</label>

										</div>

									</div>


									<div class="col-md-6">

										<div id="containerUsuarioResponsavelEdit">

											<div class="form-group">

												<label for="selUsuarioResponsavelEdit" class="form-label">

													Usuário Responsável: </label> <select
													class="form-select selUsuarioResponsavel"
													id="selUsuarioResponsavelEdit"
													name="selUsuarioResponsavelEdit">

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

					<!-- Segunda Tab (Config Avançada) -->
					<div class="tab-pane fade" id="tab-grupos-edit" role="tabpanel"
						aria-labelledby="tab-grupos-tab-edit">
						<div class="row container">
							<div class="row pb-1">
								<div class="d-flex align-items-center gap-2">
									<i class="fa fa-users fs-5"></i>
									<h6 class="sub-tittle-modal-boletim mb-0">Detalhes do
										Supervisionado</h6>
								</div>
							</div>
							<hr class="mb-0">
							<div id="supervisionadoExtrasEdit" class="row g-3 mt-2">
								<div class="col-md-6">
									<div class="card border-1 shadow-sm h-100">
										<div class="card-body">
											<h6 class="card-title mb-2">
												<i class="fas fa-bell me-1"></i> Gerenciamento de Acesso
											</h6>
											<label for="selAcaoGrupoPopupEdit" class="form-label">
												Grupos </label> <select id="selAcaoGrupoPopupEdit"
												class="selectpicker form-control form-control-sm bg-light border-dark text-dark fw-bold"
												multiple data-actions-box="true" data-size="5"
												data-style="form-select-sm"
												data-none-selected-text="Nenhum item"
												data-select-all-text="Marcar todos"
												data-deselect-all-text="Desmarcar todos"></select> <small
												class="form-text text-muted mt-2"> Configure grupos
												para notificações por canal. </small>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>

					<div class="tab-pane fade" id="tab-config-avancada-edit"
						role="tabpanel" aria-labelledby="tab-config-avancada-tab-edit">
						<div class="container-fluid">
							<div class="row">
								<div class="col-12">
									<div class="d-flex align-items-center gap-2">
										<i class="fa fa-cogs fs-5"></i>
										<h6 class="sub-tittle-modal-boletim mb-0">Configurações
											Avançadas</h6>
									</div>
									<hr class="mt-2 mb-3">
								</div>
							</div>
							<div class="row g-3">
								<div class="col-md-6">
									<div class="col-md-12">
										<div class="card border-1 shadow-sm h-100">
											<div class="card-body">
												<h6 class="card-title mb-2">
													<i class="fas fa-search me-1"></i> Semelhança de Placas
												</h6>
												<select id="nivelSemelhancaPlacaEdit"
													class="form-select form-select-sm">
													<option value="0">0 - Exatamente igual</option>
													<option value="1">1 caractere divergente</option>
													<option value="2">2 caracteres divergentes</option>
													<option value="3">3 caracteres divergentes</option>
													<option value="4">4 caracteres divergentes</option>
												</select> <small class="form-text text-muted mt-2">
													Quantidade de caracteres divergentes permitidos. </small>
											</div>
											<div class="card-body">
												<h6 class="card-title mb-2">
													<i class="fas fa-clock me-1"></i> Intervalo de Tempo
												</h6>
												<div>
													<input type="checkbox" class="form-check-input"
														id="checkboxTempoEdit" role="switch"
														onclick="verificacaoIntervaloTempoEdit()"> <label
														class="form-check-label fw-medium" for="checkboxTempoEdit">
														Editar Intervalo de Tempo </label>
												</div>
												<div class="row">
													<div class="col-md-6">
														<h7 class="card-title">Hora Início</h7>
														<select id="intervaloTempoInicioEdit"
															class="form-select form-select-sm"
															onchange="verificacaoIntervaloTempoEdit()"
															disabled="true">
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
														<select id="intervaloTempoFimEdit"
															class="form-select form-select-sm" disabled="true"
															onchange="verificacaoIntervaloTempoEdit()">
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
													<small id="dateTimeAdvise"
														class="form-text text-danger mt-2" hidden> A hora
														de Início não pode ser maior ou igual que a hora de Fim. </small>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="col-md-6">
									<div class="card border-1 shadow-sm h-100">
										<div class="card-body">
											<h6 class="card-title mb-2">
												<i class="fas fa-map-marker-alt me-1"></i> Locais
											</h6>
											<select id="selEquipamentoEdit"
												class="selectpicker form-control form-control-sm bg-light border-dark text-dark fw-bold"
												multiple data-live-search="true" data-actions-box="true"
												data-size="5" data-style="form-select-sm"
												data-none-selected-text="Nenhum Local selecionado"
												data-select-all-text="Marcar todos"
												data-deselect-all-text="Desmarcar todos"
												style="z-index: 2000"></select> <small
												class="form-text text-muted mt-2"> Selecione os
												locais específicos ou deixe sem seleção para todos. </small>
										</div>
									</div>
								</div>

								<div class="row mt-4">
									<div class="col-12">
										<div
											class="d-flex justify-content-between align-items-center mb-2">
											<h6 class="mb-0">
												<i class="fas fa-clock me-1"></i> Horários Permitidos
											</h6>
											<button id="btLinhaHorarioEdit" title="Adicionar Horário"
												type="button" class="btn btn-sm btn-outline-primary"
												onclick="adicionarLinhaHorarioEdit()">
												<i class="fa fa-plus"></i>
											</button>
										</div>
										<div class="table-responsive" style="max-height: 260px">
											<table
												class="table table-sm table-striped table-hover align-middle mb-0"
												id="tabelaHorariosPermitidosEdit">
												<thead class="table-light sticky-top" style="z-index: 500">
													<tr>
														<th style="width: 35%">Dia da Semana</th>
														<th style="width: 25%">Hora Início</th>
														<th style="width: 25%">Hora Fim</th>
														<th style="width: 15%" class="text-center">Ações</th>
													</tr>
												</thead>
												<tbody></tbody>
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
						data-bs-dismiss="modal">Fechar</button>
					<button id="btnVerAlertasModalEditar" type="button"
						class="btn btn-warning" title="Ver alertas"
						onclick="AbrirListaAlertasCadMonitorado()">Alertas</button>
					<button id="btnLimparCamposModalEditar" type="button"
						class="btn btn-warning" onclick="limparCamposEdit()">
						Limpar</button>
					<button id="btnSalvarModalEditar" type="button"
						class="btn btn-primary" onclick="editarMonitorado()">
						Salvar</button>
				</div>
			</div>
		</div>
	</div>
</div>
