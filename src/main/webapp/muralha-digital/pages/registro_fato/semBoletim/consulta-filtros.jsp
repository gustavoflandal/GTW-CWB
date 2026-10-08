<h2 class="mb-4 text-center">
	<strong>Consulta de Registros de Fato sem Boletim</strong>
</h2>

<form id="consultaForm" class="row g-3">
	<div class="col-md-3">
		<label for="dataInicio" class='form-label'>Data Inicio:</label>
		<div class='input-group' id='dataInicio'
			data-td-target-input='nearest' data-td-target-toggle='nearest'>
			<input id='dataInicioRegistroDeFato' type='text' class='form-control'
				placeholder="Selecionar..." data-td-target='#dataInicio' readonly />
			<span class='input-group-text' data-td-target='#dataInicio'
				data-td-toggle='datetimepicker'> <span
				class='fas fa-calendar'></span>
			</span>
		</div>
	</div>
	<div class="col-md-3">
		<label for="dataFim" class="form-label">Data Fim:</label>
		<div class="input-group" id="dataFim" data-td-target-input="nearest"
			data-td-target-toggle="nearest">
			<input id="dataFimRegistroDeFato" name="dataFim" type="text"
				placeholder="Selecionar..." class="form-control"
				data-td-target="#dataFim" readonly /> <span
				class="input-group-text" data-td-target="#dataFim"
				data-td-toggle="datetimepicker"> <i class="fas fa-calendar"></i>
			</span>
		</div>
	</div>
	<div class="col-md-3">
		<label for="tipoRegistroDeFato" class="form-label">Tipo Registro de Fato:</label> <select
			class="form-select" id="tipoRegistroDeFato" name="tipoRegistroDeFato">
			<option value="">Todos</option>
		</select>
	</div>
	<div class="col-sm-3">
		<label for="placa" class="form-label">Placa:</label> <input
			type="text" class="form-control" id="placaRegistroDeFato"
			name="placaRegistroDeFato" placeholder="Ex.: AAA1111" maxlength="7">
	</div>
	<div class="col-sm-4 col-md-3">
		<label for="cpf" class="form-label">CPF:</label> <input type="text"
			class="form-control" id="cpfRegistroDeFato" name="cpfRegistroDeFato" maxlength="14"
			placeholder="000.000.000-00">
	</div>
	<div class="col-sm-3 col-md-3">
		<label for="cidaderegistroDeFato" class="form-label">Cidade:</label> <select
			class="form-select" id="cidadeRegistroDeFato" name="cidadeRegistroDeFato">
			<option value="">Todas</option>
		</select>
	</div>
	<div class="col-sm-3 col-md-3">
		<label for="situacao" class="form-label">Situação:</label> <select
			class="form-select" id=situacaoRegistroDeFato name="situacaoRegistroDeFato">
			<option value="">Todos</option>
		</select>
	</div>
</form>
<%@ include file="/muralha-digital/pages/registro_fato/semBoletim/modal/modal-registroDeFato.jsp"%>
<%@ include file="/muralha-digital/pages/consulta-veiculo/modal-detalhe-veiculo.jsp"%>
<div
	class="col-sm-12 col-md-12 d-flex justify-content-end align-items-end gap-2 pt-3">
	<button type="button" class="btn btn-primary"
		onclick="executaPesquisa(true)">
		<i class="fas fa-search"></i> PESQUISAR
	</button>
	<button type="button" class="btn btn-warning" onclick="limparFiltros()">
		<i class="fas fa-eraser"></i> Limpar Filtros
	</button>
	<button type="button" class="btn btn-success"
		onclick="abrirModal(true)">
		<i class="fas fa-search"></i> Cadastrar
	</button>
</div>
<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>