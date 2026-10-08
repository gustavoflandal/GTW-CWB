<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<link rel="stylesheet"
	href="https://unpkg.com/leaflet@1.9.3/dist/leaflet.css" />
<script src="https://unpkg.com/leaflet@1.9.3/dist/leaflet.js"></script>
<!-- Scripts -->
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/js/modal-registroDeFato.js"></script>
<link rel="stylesheet"
	href="/muralha-digital/assets/css/pagina-carregando.css">
<div class="modal fade modal-lg modal-boletim"
	id="modalRegistroDeFatoComBoletim" tabindex="-1" role="dialog"
	aria-labelledby="modalRegistroDeFatoComBoletim" aria-hidden="true">
	<div class="modal-dialog modal-dialog-scrollable" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="modalPadraoLabel">Cadastro de Fato C/ Boletim</h5>
				<button type="button" class="btn-close" onclick="fecharModal()"
					aria-label="Close"></button>
			</div>
			<div class="modal-body">

				<!-- TABS -->
				<ul class="nav nav-tabs" id="myTab" role="tablist">
					<li class="nav-item" role="presentation">
						<button class="nav-link active" id="registroDeFato-comBoletim-tab"
							data-bs-toggle="tab"
							data-bs-target="#registroDeFato-comBoletim-tab-pane"
							type="button" role="tab"
							aria-controls="registroDeFato-comBoletim-tab-pane"
							aria-selected="true">Fato</button>
					</li>
					<!-- Boletim -->
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="boletim-comBoletim-tab"
							data-bs-toggle="tab"
							data-bs-target="#boletim-comBoletim-tab-pane" type="button"
							role="tab" aria-controls="boletim-comBoletim-tab-pane"
							aria-selected="false">Boletim</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="individuos-comBoletim-tab"
							data-bs-toggle="tab"
							data-bs-target="#individuos-comBoletim-tab-pane" type="button"
							role="tab" aria-controls="individuos-comBoletim-tab-pane"
							aria-selected="false">Indivíduos</button>
					</li>
					<!-- Objetos -->
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="objetos-comBoletim-tab"
							data-bs-toggle="tab"
							data-bs-target="#objetos-comBoletim-tab-pane" type="button"
							role="tab" aria-controls="objetos-comBoletim-tab-pane"
							aria-selected="false">Objetos</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="enderecos-comBoletim-tab"
							data-bs-toggle="tab"
							data-bs-target="#enderecos-comBoletim-tab-pane" type="button"
							role="tab" aria-controls="enderecos-comBoletim-tab-pane"
							aria-selected="false">Endereço</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="veiculos-comBoletim-tab"
							data-bs-toggle="tab"
							data-bs-target="#veiculos-comBoletim-tab-pane" type="button"
							role="tab" aria-controls="veiculos-comBoletim-tab-pane"
							aria-selected="false">Veículos</button>
					</li>
					<!--Documento -->
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="documento-comBoletim-tab"
							data-bs-toggle="tab"
							data-bs-target="#documento-comBoletim-tab-pane" type="button"
							role="tab" aria-controls="documento-comBoletim-tab-pane"
							aria-selected="false">Documento</button>
					</li>
					<!-- Link -->
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="link-comBoletim-tab"
							data-bs-toggle="tab" data-bs-target="#link-comBoletim-tab-pane"
							type="button" role="tab" aria-controls="link-comBoletim-tab-pane"
							aria-selected="false">Link</button>
					</li>
					<!-- Passagens -->
					<li class="nav-item" role="presentation" id="abaPassagensTab">
						<button class="nav-link" id="passagens-comBoletim-tab"
							data-bs-toggle="tab"
							data-bs-target="#passagens-comBoletim-tab-pane" type="button"
							role="tab" aria-controls="passagens-comBoletim-tab-pane"
							aria-selected="false">Passagens</button>
					</li>
					<!-- Grupo -->
					<li class="nav-item" role="presentation" id="abaGrupoTab">
						<button class="nav-link" id="grupo-comBoletim-tab"
							data-bs-toggle="tab" data-bs-target="#grupo-comBoletim-tab-pane"
							type="button" role="tab"
							aria-controls="grupo-comBoletim-tab-pane" aria-selected="false">Permissões</button>
					</li>
				</ul>

				<!-- TAB CONTENT -->
				<div class="tab-content tab-overflow-y pt-3" id="myTabContent">

					<!-- Registro de Fato -->
					<div class="tab-pane fade show active"
						id="registroDeFato-comBoletim-tab-pane" role="tabpanel"
						aria-labelledby="registroDeFato-comBoletim-tab">
						<jsp:include
							page="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-registroDeFato/tab-registroDeFato.jsp" />
					</div>
					<!-- Boletim -->
					<div class="tab-pane fade" id="boletim-comBoletim-tab-pane"
						role="tabpanel" aria-labelledby="boletim-comBoletim-tab">
						<jsp:include
							page="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-boletim/tab-boletim.jsp" />
					</div>
					<!-- Indivíduos -->
					<div class="tab-pane fade" id="individuos-comBoletim-tab-pane"
						role="tabpanel" aria-labelledby="individuos-comBoletim-tab">
						<jsp:include
							page="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-individuo/tab-individuo.jsp" />
					</div>
					<!-- Objetos -->
					<div class="tab-pane fade" id="objetos-comBoletim-tab-pane"
						role="tabpanel" aria-labelledby="objetos-comBoletim-tab">
						<jsp:include
							page="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-objeto/tab-objeto.jsp" />
					</div>
					<!-- Endereços -->
					<div class="tab-pane fade" id="enderecos-comBoletim-tab-pane"
						role="tabpanel" aria-labelledby="enderecos-comBoletim-tab">
						<jsp:include
							page="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-endereco/tab-endereco.jsp" />
					</div>

					<!-- Veículos -->
					<div class="tab-pane fade" id="veiculos-comBoletim-tab-pane"
						role="tabpanel" aria-labelledby="veiculos-comBoletim-tab">
						<jsp:include
							page="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-veiculo/tab-veiculo.jsp" />
					</div>

					<!-- Documento -->
					<div class="tab-pane fade" id="documento-comBoletim-tab-pane"
						role="tabpanel" aria-labelledby="documento-comBoletim-tab">
						<jsp:include
							page="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-documento/tab-documento.jsp" />
					</div>

					<!-- Link -->
					<div class="tab-pane fade" id="link-comBoletim-tab-pane"
						role="tabpanel" aria-labelledby="link-comBoletim-tab">
						<jsp:include
							page="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-link/tab-link.jsp" />
					</div>
					<!-- Passagens -->
					<div class="tab-pane fade" id="passagens-comBoletim-tab-pane"
						role="tabpanel" aria-labelledby="passagens-comBoletim-tab">
						<jsp:include
							page="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-passagem/tab-passagem.jsp" />
					</div>
					<!-- Grupo -->
					<div class="tab-pane fade" id="grupo-comBoletim-tab-pane"
						role="tabpanel" aria-labelledby="grupo-comBoletim-tab">
						<jsp:include
							page="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-grupo/tab-grupo.jsp" />
					</div>
				</div>

				<!-- Footer -->
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						onclick="fecharModal()">Fechar</button>
					<button id="idRegistroComBoletimModalSalvar" type="button"
						class="btn btn-primary" onClick="salvarRegistroDeFatoComBoletim()">Salvar</button>
				</div>

			</div>
			<!-- modal-body -->
		</div>
		<!-- modal-content -->
	</div>
	<!-- modal-dialog -->
</div>
<!-- modal -->
