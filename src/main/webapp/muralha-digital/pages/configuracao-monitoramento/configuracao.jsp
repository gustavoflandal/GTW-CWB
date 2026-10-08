<%@page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"
%>

<!DOCTYPE html>

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html>

<head>
	<title>Configuração de Monitoramento</title>
	
	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />

	<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
</head>

<body>
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-12 text-center">
				<h2>
					<strong>Configuração de Monitoramento</strong>
				</h2>
			</div>
		</div>
		<div class="m-1"></div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-5">
				<div class="accordion" id="accordionClonado">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionClonado">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseClonado" aria-expanded="true" aria-controls="collapseClonado">Veículo Clonado</button>
						</h2>
						<div id="collapseClonado" class="accordion-collapse collapse" aria-labelledby="accordionClonado" data-bs-parent="#accordionClonado">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-1">
										<label class="form-check-label" for="statusClonado">Ativo</label>
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="statusClonado">
										</div>
									</div>
									<div class="col-md-9">
									    <div class="form-check p-0">
							            	<label for="selGrupoEnvioAlertaClonado" class='form-label'>Grupos para envio de alerta:</label>
							            	<select id="selGrupoEnvioAlertaClonado" class="selectpicker form-control border" multiple data-actions-box="true" multiple data-size="5" data-style="btn-white" 
													data-none-selected-text="Enviar para todos" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"
													data-tipo-alerta="CF6EBC36-56CA-430B-9D3D-F7D9FF1E82F3">
											</select>
											<input type="hidden" name="selGrupoEnvioAlertaClonado" id="idGrupoEnvioAlertaClonado" class="tipo-alerta" value="CF6EBC36-56CA-430B-9D3D-F7D9FF1E82F3">
							            </div>
									</div>
									<div class="col-md-2 text-end">
										<label for="btnConfigClonado" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigClonado" class="btn btn-success" onclick="configClonado()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-5">
				<div class="accordion" id="accordionRoubado">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionRoubado">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseRoubado" aria-expanded="true" aria-controls="collapseRoubado">Veículo Roubado</button>
						</h2>
						<div id="collapseRoubado" class="accordion-collapse collapse" aria-labelledby="accordionRoubado" data-bs-parent="#accordionRoubado">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-1">
										<label class="form-check-label" for="statusRoubado">Ativo</label>
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="statusRoubado">
										</div>
									</div>
									<div class="col-md-9">
									    <div class="form-check p-0">
							            	<label for="selGrupoEnvioAlertaRoubado" class='form-label'>Grupos para envio de alerta:</label>
							            	<select id="selGrupoEnvioAlertaRoubado" class="selectpicker form-control border" multiple data-actions-box="true" multiple data-size="5" data-style="btn-white" 
													data-none-selected-text="Enviar para todos" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"
													data-tipo-alerta="95631582-96B2-4220-9612-12131BE4923C">
											</select>
											<input type="hidden" name="selGrupoEnvioAlertaRoubado" id="idGrupoEnvioAlertaRoubado" class="tipo-alerta" value="95631582-96B2-4220-9612-12131BE4923C">
							            </div>
									</div>
									<div class="col-md-2 text-end">
										<label for="btnConfigRoubado" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigRoubado" class="btn btn-success" onclick="configRoubado()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-1"></div>
		</div>
		<div class="m-1"></div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-5">
				<div class="accordion" id="accordionRelampago">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionRelampago">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseRelampago" aria-expanded="true" aria-controls="collapseRelampago">Sequestro Relâmpago</button>
						</h2>
						<div id="collapseRelampago" class="accordion-collapse collapse" aria-labelledby="accordionRelampago" data-bs-parent="#accordionRelampago">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-1">
										<label class="form-check-label" for="statusSequestro">Ativo</label>
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="statusSequestro">
										</div>
									</div>
									<div class="col-md-9">
									    <div class="form-check p-0">
							            	<label for="selGrupoEnvioAlertaSequestro" class='form-label'>Grupos para envio de alerta:</label>
							            	<select id="selGrupoEnvioAlertaSequestro" class="selectpicker form-control border" multiple data-actions-box="true" multiple data-size="5" data-style="btn-white" 
													data-none-selected-text="Enviar para todos" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"
													data-tipo-alerta="CB8D5C4B-1822-4868-A2F9-0153B50212DA">
											</select>
											<input type="hidden" name="selGrupoEnvioAlertaSequestro" id="idGrupoEnvioAlertaSequestro" class="tipo-alerta" value="CB8D5C4B-1822-4868-A2F9-0153B50212DA">
							            </div>
									</div>
									<div class="col-md-2 text-end">
										<label for="btnConfigSequestro" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigSequestro" class="btn btn-success" onclick="configSequestro()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-5">
				<div class="accordion" id="accordionFurtado">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionFurtado">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseFurtado" aria-expanded="true" aria-controls="collapseFurtado">Veículo Furtado</button>
						</h2>
						<div id="collapseFurtado" class="accordion-collapse collapse" aria-labelledby="accordionFurtado" data-bs-parent="#accordionFurtado">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-1">
										<label class="form-check-label" for="statusFurtado">Ativo</label>
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="statusFurtado">
										</div>
									</div>
									<div class="col-md-9">
									    <div class="form-check p-0">
							            	<label for="selGrupoEnvioAlertaFurtado" class='form-label'>Grupos para envio de alerta:</label>
							            	<select id="selGrupoEnvioAlertaFurtado" class="selectpicker form-control border" multiple data-actions-box="true" multiple data-size="5" data-style="btn-white" 
													data-none-selected-text="Enviar para todos" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"
													data-tipo-alerta="0349F722-DFDE-4080-9E3B-D65F1C058EDC">
											</select>
											<input type="hidden" name="selGrupoEnvioAlertaFurtado" id="idGrupoEnvioAlertaFurtado" class="tipo-alerta" value="0349F722-DFDE-4080-9E3B-D65F1C058EDC">
							            </div>
									</div>
									<div class="col-md-2 text-end">
										<label for="btnConfigFurtado" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigFurtado" class="btn btn-success" onclick="configFurtado()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-1"></div>
		</div>
		<div class="m-1"></div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-5">
				<div class="accordion" id="accordionLicenciamento">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionLicenciamento">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseLicenciamento" aria-expanded="true" aria-controls="collapseLicenciamento">Atraso de Licenciamento</button>
						</h2>
						<div id="collapseLicenciamento" class="accordion-collapse collapse" aria-labelledby="accordionLicenciamento" data-bs-parent="#accordionLicenciamento">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-1">
										<label class="form-check-label" for="statusLicenciamento">Ativo</label>
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="statusLicenciamento">
										</div>
									</div>
									<div class="col-md-9">
									    <div class="form-check p-0">
							            	<label for="selGrupoEnvioAlertaLicenciamento" class='form-label'>Grupos para envio de alerta:</label>
							            	<select id="selGrupoEnvioAlertaLicenciamento" class="selectpicker form-control border" multiple data-actions-box="true" multiple data-size="5" data-style="btn-white" 
													data-none-selected-text="Enviar para todos" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"
													data-tipo-alerta="6631DC43-779F-4BFF-A329-B9653D056708">
											</select>
											<input type="hidden" name="selGrupoEnvioAlertaLicenciamento" id="idGrupoEnvioAlertaLicenciamento" class="tipo-alerta" value="6631DC43-779F-4BFF-A329-B9653D056708">
							            </div>
									</div>
									<div class="col-md-2 text-end">
										<label for="btnConfigLicenciamento" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigLicenciamento" class="btn btn-success" onclick="configLicenciamento()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-5">
				<div class="accordion" id="accordionMonitorado">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionMonitorado">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseMonitorado" aria-expanded="true" aria-controls="collapseMonitorado">Monitorado</button>
						</h2>
						<div id="collapseMonitorado" class="accordion-collapse collapse" aria-labelledby="accordionMonitorado" data-bs-parent="#accordionMonitorado">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-1">
										<label class="form-check-label" for="statusMonitorado">Ativo</label>
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="statusMonitorado">
										</div>
									</div>
									<div class="col-md-9">
									    <div class="form-check p-0">
							            	<label for="selGrupoEnvioAlertaMonitorado" class='form-label'>Grupos para envio de alerta:</label>
							            	<select id="selGrupoEnvioAlertaMonitorado" class="selectpicker form-control border" multiple data-actions-box="true" multiple data-size="5" data-style="btn-white" 
													data-none-selected-text="Enviar para todos" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"
													data-tipo-alerta="9D31A265-A663-4836-BF00-2309FC0D5E33">
											</select>
											<input type="hidden" name="selGrupoEnvioAlertaMonitorado" id="idGrupoEnvioAlertaMonitorado" class="tipo-alerta" value="9D31A265-A663-4836-BF00-2309FC0D5E33">
							            </div>
									</div>
									<div class="col-md-2 text-end">
										<label for="btnConfigMonitorado" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigMonitorado" class="btn btn-success" onclick="configMonitorado()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-1"></div>
		</div>
		<div class="m-1"></div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-5">
				<div class="accordion" id="accordionComboio">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionComboio">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseComboio" aria-expanded="true" aria-controls="collapseComboio">Comboio</button>
						</h2>
						<div id="collapseComboio" class="accordion-collapse collapse" aria-labelledby="accordionComboio" data-bs-parent="#accordionComboio">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-1">
										<label class="form-check-label" for="statusComboio">Ativo</label>
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="statusComboio">
										</div>
									</div>
									<div class="col-md-3">
										<label class="form-check-label" for="tempoComboio">Intervalo de tempo</label>
										<select class="form-select mt-2" aria-label="Default select example" id="tempoComboio" data-tipo-alerta="cf6ebc36-56ca-430b-9d3d-f7d9ff1e82f3">
											<option selected>Intervalo de tempo</option>
											<option value="30">00:30</option>
											<option value="60">01:00</option>
											<option value="90">01:30</option>
											<option value="120">02:00</option>
										</select>
									</div>
									<div class="col-md-6">
									    <div class="form-check p-0">
							            	<label for="selGrupoEnvioAlertaComboio" class='form-label'>Grupos para envio de alerta:</label>
							            	<select id="selGrupoEnvioAlertaComboio" class="selectpicker form-control border" multiple data-actions-box="true" multiple data-size="5" data-style="btn-white" 
													data-none-selected-text="Enviar para todos" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"
													data-tipo-alerta="19B86A23-2CD6-43ED-A596-62935EA3980A">
											</select>
											<input type="hidden" name="selGrupoEnvioAlertaComboio" id="idGrupoEnvioAlertaComboio" class="tipo-alerta" value="19B86A23-2CD6-43ED-A596-62935EA3980A">
							            </div>
									</div>
									<div class="col-md-2 text-end">
										<label for="btnConfigComboio" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigComboio" class="btn btn-success" onclick="configComboio()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-5">
				<div class="accordion" id="accordionBanco">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionBanco">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseBanco" aria-expanded="true" aria-controls="collapseBanco">Roubo a Banco</button>
						</h2>
						<div id="collapseBanco" class="accordion-collapse collapse" aria-labelledby="accordionBanco" data-bs-parent="#accordionBanco">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-1">
										<label class="form-check-label" for="statusPonto">Ativo</label>
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="statusPonto">
										</div>
									</div>
									<div class="col-md-3">
										<label class="form-check-label" for="tempoPonto">Intervalo de tempo</label>
										<select class="form-select mt-2" aria-label="Default select example" id="tempoPonto" data-tipo-alerta="FEEF9500-83C0-4942-A8AA-AED77E20BA5B">
											<option selected>Intervalo de tempo</option>
											<option value="30">00:30</option>
											<option value="60">01:00</option>
											<option value="90">01:30</option>
											<option value="120">02:00</option>
											<option value="150">02:30</option>
											<option value="180">03:00</option>
											<option value="210">03:30</option>
											<option value="240">04:00</option>
											<option value="270">04:30</option>
											<option value="300">05:00</option>
										</select>
									</div>
									<div class="col-md-6">
									    <div class="form-check p-0">
							            	<label for="selGrupoEnvioAlertaPonto" class='form-label'>Grupos para envio de alerta:</label>
							            	<select id="selGrupoEnvioAlertaPonto" class="selectpicker form-control border" multiple data-actions-box="true" multiple data-size="5" data-style="btn-white" 
													data-none-selected-text="Enviar para todos" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"
													data-tipo-alerta="FEEF9500-83C0-4942-A8AA-AED77E20BA5B">
											</select>
											<input type="hidden" name="selGrupoEnvioAlertaPonto" id="idGrupoEnvioAlertaPonto" class="tipo-alerta" value="FEEF9500-83C0-4942-A8AA-AED77E20BA5B">
							            </div>
									</div>
									<div class="col-md-2 text-end">
										<label for="btnConfigPonto" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigPonto" class="btn btn-success" onclick="configPonto()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-1"></div>
		</div>
		<div class="m-1"></div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-5">
				<div class="accordion" id="accordionSemelhanca">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionSemelhanca">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseSemelhanca" aria-expanded="true" aria-controls="collapseSemelhanca">Semelhança de Placas</button>
						</h2>
						<div id="collapseSemelhanca" class="accordion-collapse collapse" aria-labelledby="accordionSemelhanca" data-bs-parent="#accordionSemelhanca">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-4">
										<label>Caracteres divergentes:</label>
									</div>									
									<div class="col-md-6">
										<select class="form-select" aria-label="Default select example" id="caracteresSemelhantes">
											<option selected>Quantidade de caracteres divergentes</option>
											<option value="0">0</option>
											<option value="1">1</option>
											<option value="2">2</option>
											<option value="3">3</option>
											<option value="3">4</option>
										</select>
									</div>
								</div>
								<div class="m-2"></div>
								<div class="row">
									<div class="col-md-12 text-end">
										<button class="btn btn-success" onclick="configSemelhanca()">SALVAR</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>		
			<div class="col-md-5">
				<div class="accordion" id="accordionClandestino">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionClandestino">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseClandestino" aria-expanded="true" aria-controls="collapseClandestino">Veículo Clandestino</button>
						</h2>
						<div id="collapseClandestino" class="accordion-collapse collapse" aria-labelledby="accordionClandestino" data-bs-parent="#accordionClandestino">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-2">
										<label class="form-check-label" for="statusClandestino">Ativo</label>
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="statusClandestino">
										</div>
									</div>
									<div class="col-md-8">
									    <div class="form-check p-0">
							            	<label for="selGrupoEnvioAlertaClandestino" class='form-label'>Grupos para envio de alerta:</label>
							            	<select id="selGrupoEnvioAlertaClandestino" class="selectpicker form-control border" multiple data-actions-box="true" multiple data-size="5" data-style="btn-white" 
													data-none-selected-text="Enviar para todos" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos"
													data-tipo-alerta="AE93F81A-DF6D-41B5-AFC6-99B3438C291D">
											</select>
											<input type="hidden" name="selGrupoEnvioAlertaClandestino" id="idGrupoEnvioAlertaClandestino" class="tipo-alerta" value="AE93F81A-DF6D-41B5-AFC6-99B3438C291D">
							            </div>
									</div>
									<div class="col-md-2"></div>
								</div>
								<div class="m-2"></div>
								<div class="row">
									<div class="col-md-2">
										<label>Manhã</label>
									</div>
									<div class="col-md-4">
										<select class="form-select" aria-label="Default select example" id="iniManha">
											<option selected value="inicio">Início</option>
											<option value="05:00">05:00</option>
											<option value="05:30">05:30</option>
											<option value="06:00">06:00</option>
											<option value="06:30">06:30</option>
											<option value="07:00">07:00</option>
											<option value="07:30">07:30</option>
											<option value="08:00">08:00</option>
											<option value="08:30">08:30</option>
											<option value="09:00">09:00</option>
											<option value="09:30">09:30</option>
										</select>
									</div>
									<div class="col-md-4">
										<select class="form-select" aria-label="Default select example" id="fimManha">
											<option selected value="fim">Fim</option>
											<option value="10:00">10:00</option>
											<option value="10:30">10:30</option>
											<option value="11:00">11:00</option>
											<option value="11:30">11:30</option>
											<option value="12:00">12:00</option>
										</select>
									</div>
									<div class="col-md-2"></div>
								</div>
								<div class="m-2"></div>
								<div class="row">
									<div class="col-md-2">
										<label>Tarde</label>
									</div>
									<div class="col-md-4">
										<select class="form-select" aria-label="Default select example" id="iniTarde">
											<option selected value="inicio">Início</option>
											<option value="12:00">12:00</option>
											<option value="12:30">12:30</option>
											<option value="13:00">13:00</option>
											<option value="13:30">13:30</option>
											<option value="14:00">14:00</option>
											<option value="14:30">14:30</option>
											<option value="15:00">15:00</option>
											<option value="15:30">15:30</option>
										</select>
									</div>
									<div class="col-md-4">
										<select class="form-select" aria-label="Default select example" id="fimTarde">
											<option selected value="fim">Fim</option>
											<option value="16:00">16:00</option>
											<option value="16:30">16:30</option>
											<option value="17:00">17:00</option>
											<option value="17:30">17:30</option>
											<option value="18:00">18:00</option>
											<option value="18:30">18:30</option>
											<option value="19:00">19:00</option>
											<option value="19:30">19:30</option>
											<option value="20:00">20:00</option>
										</select>
									</div>
									<div class="col-md-2"></div>
								</div>
								<div class="m-2"></div>
								<div class="row">
									<div class="col-md-2">
										<label>Passagens</label>
									</div>
									<div class="col-md-8">
										<select class="form-select" aria-label="Default select example" id="passagens">
											<option selected value="0">Quantidade</option>
											<option value="1">1</option>
											<option value="2">2</option>
											<option value="3">3</option>
											<option value="4">4</option>
											<option value="5">5</option>
											<option value="6">6</option>
											<option value="7">7</option>
											<option value="8">8</option>
											<option value="9">9</option>
											<option value="0">10</option>
										</select>
									</div>
									<div class="col-md-2"></div>
								</div>
								<div class="m-2"></div>
								<div class="row">
									<div class="col-md-2">
										<label>Tipo</label>
									</div>
									<div class="col-md-8">
										<select class="form-select" aria-label="Default select example" id="tipoVeiculo">
											<option selected value="0">Tipo de veículo</option>
											<option value="1">Moto</option>
											<option value="2">Pequeno</option>
											<option value="3">Médio</option>
											<option value="4">Grande</option>
										</select>
									</div>
									<div class="col-md-2 text-end">
										<label for="btnConfigClandestino" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigClandestino" class="btn btn-success" onclick="configClandestino()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-1"></div>
		</div>
		<div class="m-1"></div>
		<input type="hidden" name="tipoRegistro" id="idtipoRegistro" value="E7D115B9-E6B3-4E86-9083-F347A1917045">
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-5">
				<div class="accordion" id="accordionAlertaSom">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionAlertaSom">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseAlertaSom" aria-expanded="true" aria-controls="collapseAlertaSom">Ajuste de Tempo de Alertas</button>
						</h2>
						<div id="collapseAlertaSom" class="accordion-collapse collapse" aria-labelledby="accordionAlertaSom" data-bs-parent="#accordionAlertaSom">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-3">
										<label class="form-check-label" for="ativoAlerta">Alarme Ativo</label>
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="ativoAlerta">
										</div>
									</div>
									<div class="col-md-3">
										<label class="form-check-label" for="ativoAlertaContinuo">Alerta Continuo</label>
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="ativoAlertaContinuo">
										</div>
									</div>
									<div class="col-md-5">
										<label class="form-check-label" for="tempoAlerta">Intervalo de tempo do Alarme</label>
										<select class="form-select mt-2" aria-label="Default select example" id="tempoAlerta" data-tipo-alerta="">
											<option value="5">5 segs</option>
											<option value="10">10 seg</option>
											<option value="15">15 seg</option>
											<option value="20">20 seg</option>
											<option value="25">25 seg</option>
											<option value="30">30 seg</option>
										</select>
									</div>
									<div class="col-md-12 text-end">
										<label for="btnConfigAlertaSom" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigAlertaSom" class="btn btn-success" onclick="configAlertaSonoro()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-5">
				<div class="accordion" id="accordionVeiculosCorrelacionados">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionVeiculosCorrelacionadosHeader">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseVeiculosCorrelacionados" aria-expanded="true" aria-controls="collapseVeiculosCorrelacionados">Veículos Correlacionados</button>
						</h2>
						<div id="collapseVeiculosCorrelacionados" class="accordion-collapse collapse" aria-labelledby="accordionVeiculosCorrelacionadosHeader" data-bs-parent="#accordionVeiculosCorrelacionados">
							<div class="accordion-body">
								<div class="alert alert-info py-2" role="alert">
									Regra: a quantidade de passagens da correlação alta deve ser maior que a da média, e a da correlação média deve ser maior que a da baixa.
								</div>
								<div class="row">
									<div class="col-md-4">
										<label for="qtdPasCorrelacaoBaixa">Passagens correlação baixa:</label>
										<input type="number" class="form-control mt-2" id="qtdPasCorrelacaoBaixa" min="1" value="3" oninput="atualizarEstadoVeiculosCorrelacionados()">
										<small id="msgQtdPasCorrelacaoBaixa" class="text-danger d-none"></small>
									</div>
									<div class="col-md-4">
										<label for="qtdPasCorrelacaoMedia">Passagens correlação média:</label>
										<input type="number" class="form-control mt-2" id="qtdPasCorrelacaoMedia" min="1" value="4" oninput="atualizarEstadoVeiculosCorrelacionados()">
										<small id="msgQtdPasCorrelacaoMedia" class="text-danger d-none"></small>
									</div>
									<div class="col-md-4">
										<label for="qtdPasCorrelacaoAlta">Passagens correlação alta:</label>
										<input type="number" class="form-control mt-2" id="qtdPasCorrelacaoAlta" min="1" value="5" oninput="atualizarEstadoVeiculosCorrelacionados()">
										<small id="msgQtdPasCorrelacaoAlta" class="text-danger d-none"></small>
									</div>
								</div>
								<div class="m-2"></div>
								<div class="row">
									<div class="col-md-12 text-end">
										<button id="btnConfigVeiculosCorrelacionados" class="btn btn-success" onclick="configVeiculosCorrelacionados()">SALVAR</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-1"></div>			
			<!-- <button id="btnConfigAlertaSom" class="btn btn-success" onclick="AlarmeSonoro('Veículo Roubado')">Teste Alerta Contínuo</button> -->
		</div>
		<div class="m-1"></div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-5">
				<div class="accordion" id="accordionTempoMaximo">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionTempoMaximo">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseTempoMaximo" aria-expanded="true" aria-controls="collapseTempoMaximo">Tempo máximo para emissão do alerta</button>
						</h2>
						<div id="collapseTempoMaximo" class="accordion-collapse collapse" aria-labelledby="accordionTempoMaximo" data-bs-parent="#accordionTempoMaximo">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-8">
										<label class="form-check-label" for="tempoMaximoEmissao">Tempo máximo (minutos):</label>
										<input type="number" class="form-control mt-2" id="tempoMaximoEmissao" min="1" value="1">
									</div>
									<div class="col-md-4 text-end">
										<label for="btnConfigTempoMaximo" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigTempoMaximo" class="btn btn-success" onclick="configTempoMaximo()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="m-1"></div>
		<div class="m-1"></div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-5">
				<div class="accordion" id="accordionPrioridade">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionPrioridade">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapsePrioridade" aria-expanded="true" aria-controls="collapsePrioridade">Classificação de Prioridade</button>
						</h2>
						<div id="collapsePrioridade" class="accordion-collapse collapse" aria-labelledby="accordionPrioridade" data-bs-parent="#accordionPrioridade">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-5">								
										<label for="tipoAlerta" class='form-label'>Tipos de Alertas:</label>
						            	<select id="tipoAlerta" class="selectpicker form-control border"  data-actions-box="true" data-size="5" data-style="btn-white"></select>
					            	</div>
					            	<div class="col-md-2">								
										<label for="tipoAlerta" class='form-label'>Prioridade:</label>
						            	<input id="valorPrioridade" class="form-control border" type="number" min="0" max="99">
					            	</div>
					            	<div class="col-md-5 text-end">
										<label for="btnPrioridade" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnPrioridade" class="btn btn-success" onclick="salvarPrioridade()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="m-1"></div>
		<div class="row">
			<div class="col-md-1"></div>
			<div id="blocoTempoInatividade" class="col-md-5" style="display:none;" >
				<div class="accordion" id="accordionTempoMaximoInatividade">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionTempoMaximoInatividade">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseTempoMaximoInatividade" aria-expanded="true" aria-controls="collapseTempoMaximo">Tempo máximo de inatividade</button>
						</h2>
						<div id="collapseTempoMaximoInatividade" class="accordion-collapse collapse" aria-labelledby="accordionTempoMaximoInatividade" data-bs-parent="#accordionTempoMaximoInatividade">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-12">
										<label class="form-check-label" for="ativoAlerta">Tempo indefinido</label>
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="tempoIndefinido">
										</div>
									</div>
									<div class="col-md-8">
										<label class="form-check-label" for="tempoMaximoInatividade">Tempo máximo (minutos):</label>
										<input type="number" class="form-control mt-2" id="tempoMaximoInatividade" min="1" value="1">
									</div>
									<div class="col-md-2 text-end">
										<label for="btntempoMaximoInatividade" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigTempoMaximo" class="btn btn-success" onclick="configTempoMaximoInatividade()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="m-1"></div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-5">
				<div class="accordion" id="accordionRaioRadares">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionRaioRadares">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseRaioRadares" aria-expanded="true" aria-controls="collapseRaioRadares">
								Raio de Busca para Radares no Mapa
							</button>
						</h2>
						<div id="collapseRaioRadares" class="accordion-collapse collapse" aria-labelledby="accordionRaioRadares" data-bs-parent="#accordionRaioRadares">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-8">
										<label class="form-check-label" for="raioRadaresMapa">
											Raio em metros para busca de radares:
										</label>
										<div class="form-text">
											Este valor será usado quando selecionar radares por ponto no mapa.
										</div>
										<input type="number" class="form-control mt-2" id="raioRadaresMapa" min="100" value="1000">
									</div>
									<div class="col-md-4 text-end">
										<label for="btnConfigRaioRadares" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigRaioRadares" class="btn btn-success" onclick="configRaioRadares()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="m-1"></div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-5">
				<div class="accordion" id="accordionTempoOCRBlitz">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionTempoOCRBlitz">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseTempoOCRBlitz" aria-expanded="true" aria-controls="collapseTempoOCRBlitz">
								Tempo entre captura de OCR blitz
							</button>
						</h2>
						<div id="collapseTempoOCRBlitz" class="accordion-collapse collapse" aria-labelledby="accordionTempoOCRBlitz" data-bs-parent="#accordionTempoOCRBlitz">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-8">
										<label class="form-check-label" for="tempoOCRBlitz">
											Tempo entre capturas de OCR blitz (segundos):
										</label>
										<div class="form-text">
											Este valor será usado para definir o intervalo entre as capturas de OCR blitz.
										</div>
										<input type="number" class="form-control mt-2" id="tempoOCRBlitz" min="0.1" step="0.1" value="0.5">
									</div>
									<div class="col-md-4 text-end">
										<label for="btnConfigTempoOCRBlitz" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigTempoOCRBlitz" class="btn btn-success" onclick="configTempoOCRBlitz()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="m-1"></div>
		<div class="row">
			<div class="col-md-1"></div>
			<div class="col-md-5">
				<div class="accordion" id="accordionAgenteGuarnicao">
					<div class="accordion-item">
						<h2 class="accordion-header" id="accordionAgenteGuarnicao">
							<button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseAgenteGuarnicao" aria-expanded="true" aria-controls="collapseAgenteGuarnicao">
								Agente de Guarnição - Notificações Mobile
							</button>
						</h2>
						<div id="collapseAgenteGuarnicao" class="accordion-collapse collapse" aria-labelledby="accordionAgenteGuarnicao" data-bs-parent="#accordionAgenteGuarnicao">
							<div class="accordion-body">
								<div class="row">
									<div class="col-md-8">
										<label class="form-check-label" for="statusAgenteGuarnicao">
											Habilitar recebimento de notificações de alertas
										</label>
										<div class="form-text">
											Quando habilitado, os agentes de guarnição receberão notificações push de alertas além das notificações de atendimento.
										</div>
									</div>
									<div class="col-md-2">
										<div class="form-check form-switch mt-2">
											<input class="form-check-input" type="checkbox" id="statusAgenteGuarnicao">
										</div>
									</div>
									<div class="col-md-2 text-end">
										<label for="btnConfigAgenteGuarnicao" class='form-check-label'></label>
										<div class="form-check mt-2">
											<button id="btnConfigAgenteGuarnicao" class="btn btn-success" onclick="configAgenteGuarnicao()">SALVAR</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-1"></div>
		</div>
	</div>
	<script src="assets/js/configuracao.js"></script>
</body>

</html>