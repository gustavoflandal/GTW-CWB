<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<c:url value="/" 	var="root" />

<script type="text/javascript">
	var urlRoot = "${root}";
</script>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />

<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script src="js/modal-alerta-acao.js"></script>
	
<div class="modal fade" id="modalAlertaAcaoProcedimento" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="2005" aria-labelledby="modalAlertaAcaoProcedimentoLabel" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered modal-lg ">
		<div class="modal-content">
			<div class="modal-header">
					<h5 class="modal-title" id="staticBackdropLabel">Ações e Procedimentos</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			</div>
			<div class="modal-body">
				<div id="error_container_modal_acao"></div>
<!-- 				<div class="border-top border-bottom border-1"> -->
				<div class="row">
					<div class="col-sm-12">
						<div class="mb-3">
							<h5 class="text-center">Configurar Envio de Notificação da Ocorrência</h5>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-sm-6">
						<div class="mb-3">
							<div class="form-group">
								<label for="selAcaoGrupoEmailCad" class='form-label'>E-mail:</label>
								<select id="selAcaoGrupoEmailCad" class="selectpicker form-control border" multiple data-actions-box="true" multiple data-size="5" data-style="btn-white" 
										data-none-selected-text="Nenhum item selecionado" data-none-selected-text="Nenhum item selecionado" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos">
								</select>
							</div>
						</div>
					</div>
					<div class="col-sm-6">
						<div class="mb-3">
							<div class="form-group">
								<label for="selAcaoGrupoSmsCad" class='form-label'>SMS:</label>
								<select id="selAcaoGrupoSmsCad" class="selectpicker form-control border" multiple data-actions-box="true" multiple data-size="5" data-style="btn-white"
										data-none-selected-text="Nenhum item selecionado" data-none-selected-text="Nenhum item selecionado" data-select-all-text="Marcar todos" data-deselect-all-text="Desmarcar todos">
								</select>
							</div>
						</div>
					</div>
<!-- 				</div> -->
				</div>
				
				<div class="row mt-4">
					<div class="col-sm-12">
						<div class="mb-3">
							<h5 class="text-center">Configuração de Atendimento da Ocorrência</h5>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<div class="mb-3">
							<div class="row">
								<div class="form-group col-md-6">
									<input class="form-check-input" type="checkbox" id="chkPermitirAtendimento">
									<label class="form-check-label" for="chkPermitirAtendimento">Permitir abertura de atendimento</label>
								</div>
								<div class="form-group col-md-6">
									<input class="form-check-input" type="checkbox" id="chkAssinado">
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
	
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
				<button id="btnLimparCamposAcaoOcorrencia" type="button" class="btn btn-warning" onclick="LimparCamposAcaoProcedimento()">Limpar</button>
				<button id="btnSalvarAcaoOcorrencia" type="button" class="btn btn-primary" onclick="SalvarConfigNotificacao()">Salvar</button>
			</div>
		</div>
	</div>
</div>
