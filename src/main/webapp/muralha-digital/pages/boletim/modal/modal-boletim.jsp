<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>

<link rel="stylesheet"
	href="/muralha-digital/pages/boletim/modal/css/modal-cadastro-boletim.css">
<!-- Importações específicas deste modal -->
<script src="/muralha-digital/pages/boletim/modal/js/modal-boletim.js"></script>
<script
	src="/muralha-digital/pages/boletim/modal/js/modal-cadastro-boletim.js"></script>
<script
	src="/muralha-digital/pages/boletim/modal/js/modal-visualizar-boletim.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script
	src="/muralha-digital/pages/boletim/modal/tab-individuos/js/tab-individuos.js"></script>
<script
	src="/muralha-digital/pages/boletim/modal/tab-boletim/js/tab-boletim.js"></script>
<script
	src="/muralha-digital/pages/boletim/modal/tab-apreencoes/js/tab-apreencoes.js"></script>
<script
	src="/muralha-digital/pages/boletim/modal/tab-documentos/js/tab-documentos.js"></script>
<div class="modal fade modal-lg modal-boletim" id="modalBoletim"
	tabindex="-1" role="dialog" aria-labelledby="modalBoletim"
	aria-hidden="true">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="modalPadraoLabel">Cadastro de
					Boletim</h5>
				<button type="button" class="btn-close" onClick="fecharModal()"
					aria-label="Close"></button>
			</div>
			<div class="modal-body">
				<ul class="nav nav-tabs" id="myTab" role="tablist">
					<li class="nav-item" role="presentation">
						<button class="nav-link active" id="boletim-tab"
							data-bs-toggle="tab" data-bs-target="#boletim-tab-pane"
							type="button" role="tab" aria-controls="boletim-tab-pane"
							aria-selected="true">Boletim</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="documento-tab" data-bs-toggle="tab"
							data-bs-target="#documento-tab-pane" type="button" role="tab"
							aria-controls="documento-tab-pane" aria-selected="false">Documentos</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="individuo-tab" data-bs-toggle="tab"
							data-bs-target="#individuo-tab-pane" type="button" role="tab"
							aria-controls="individuo-tab-pane" aria-selected="false">Indivíduos</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="veiculo-tab" data-bs-toggle="tab"
							data-bs-target="#veiculo-tab-pane" type="button" role="tab"
							aria-controls="veiculo-tab-pane" aria-selected="false">Veículos</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="apreensao-tab" data-bs-toggle="tab"
							data-bs-target="#apreensao-tab-pane" type="button" role="tab"
							aria-controls="apreensao-tab-pane" aria-selected="false">Apreensões</button>
					</li>
				</ul>

				<div class="tab-content tab-overflow-y" id="myTabContent">
					<div class="tab-pane fade show active" id="boletim-tab-pane"
						role="tabpanel" aria-labelledby="boletim-tab" tabindex="0">
						<jsp:include
							page="/muralha-digital/pages/boletim/modal/tab-boletim/tab-boletim.jsp" />
					</div>

					<div class="tab-pane fade" id="documento-tab-pane" role="tabpanel"
						aria-labelledby="documento-tab" tabindex="0">
						<jsp:include
							page="/muralha-digital/pages/boletim/modal/tab-documentos/tab-documentos.jsp" />
					</div>

					<div class="tab-pane fade" id="individuo-tab-pane" role="tabpanel"
						aria-labelledby="individuo-tab" tabindex="0">
						<jsp:include
							page="/muralha-digital/pages/boletim/modal/tab-individuos/tab-individuos.jsp" />
					</div>
					<div class="tab-pane fade" id="veiculo-tab-pane" role="tabpanel"
						aria-labelledby="veiculo-tab" tabindex="0">
						<jsp:include
							page="/muralha-digital/pages/boletim/modal/tab-veiculos/tab-veiculos.jsp" />
					</div>
					<div class="tab-pane fade" id="apreensao-tab-pane" role="tabpanel"
						aria-labelledby="apreensao-tab" tabindex="0">
						<jsp:include
							page="/muralha-digital/pages/boletim/modal/tab-apreencoes/tab-apreencoes.jsp" />
					</div>
				</div>
				<!-- fim tab-content -->

				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						onclick="fecharModal()">Fechar</button>
					<button id="idBoletimModalSalvar" type="button" class="btn btn-primary"
						onclick="salvarBoletim()">Salvar</button>
				</div>

			</div>
			<!-- fim modal-body -->
		</div>
		<!-- fim modal-content -->
	</div>
	<!-- fim modal-dialog -->
</div>
<!-- fim modal -->