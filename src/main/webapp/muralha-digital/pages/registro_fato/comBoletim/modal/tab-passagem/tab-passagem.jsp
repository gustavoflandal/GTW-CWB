<meta charset="UTF-8">
<link rel="stylesheet"
	href="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-passagem/css/tab-passagem.css">
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/modal/tab-passagem/js/tab-passagem.js"></script>
<%@ include
	file="/muralha-digital/pages/consulta-veiculo/modal-detalhe-veiculo.jsp"%>
<script type="text/javascript"
	src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
<div class="container">
	<div class="row pt-3">
		<div class="container-sub-tittle">
			<i class="fa fa-road" aria-hidden="true"></i>
			<h6 class="sub-tittle-modal-comBoletim">Passagens</h6>
		</div>
	</div>
	<hr class="sub-diviver-modal-comBoletim">

	<div class="row pb-3">
		<div class="form-group input-group">
			<span class="input-group-text"> <i class="fas fa-search"></i>
			</span> <input name="consulta" id="txt_consulta_passagemComBoletim" placeholder="Consultar"
				type="text" class="form-control">
		</div>
	</div>
	<div class="table-responsive">
		<table class="table table-bordered table-sm"
			id="tabelaPassagensComBoletim">
			<thead class="table-light">
				<tr>
					<th>Placa</th>
					<th>Data da passagem</th>
					<th>Data do vínculo da passagem</th>
					<th class="text-center">Ação</th>
				</tr>
			</thead>
			<tbody id="tabelaPassagensBodyComBoletim"></tbody>
		</table>
	</div>
</div>
