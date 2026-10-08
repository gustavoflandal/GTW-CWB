<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<h2 class="mb-4 text-center">
	<strong>Consultar Registros de Fato</strong>
</h2>

<form id="consultaFormComBoletim" class="row g-3">
	<div class="col-md-4">
		<label for="dataInicioComBoletim" class='form-label'>Data
			Inicio:</label>
		<div class='input-group' id='dataInicio'
			data-td-target-input='nearest' data-td-target-toggle='nearest'>
			<input id='dataInicioRegistroDeFatoComBoletim' type='text'
				class='form-control' placeholder="Selecionar..."
				data-td-target='#dataInicioComBoletim' readonly /> <span
				class='input-group-text' data-td-target='#dataInicioComBoletim'
				data-td-toggle='datetimepicker'> <span
				class='fas fa-calendar'></span>
			</span>
		</div>
	</div>
	<div class="col-md-4">
		<label for="dataFimComBoletim" class="form-label">Data Fim:</label>
		<div class="input-group" id="dataFim" data-td-target-input="nearest"
			data-td-target-toggle="nearest">
			<input id="dataFimRegistroDeFatoComBoletim" name="dataFimComBoletim"
				type="text" placeholder="Selecionar..." class="form-control"
				data-td-target="#dataFimComBoletim" readonly /> <span
				class="input-group-text" data-td-target="#dataFimComBoletim"
				data-td-toggle="datetimepicker"> <i class="fas fa-calendar"></i>
			</span>
		</div>
	</div>
	<div class="col-sm-4">
		<label for="placaRegistroDeFatoComBoletim" class="form-label">Placa:</label>
		<input type="text" class="form-control"
			id="placaRegistroDeFatoComBoletim"
			name="placaRegistroDeFatoComBoletim" placeholder="Ex.: AAA1111"
			maxlength="7">
	</div>
	<div class="col-sm-4 col-md-4">
		<label for="cpfRegistroDeFatoComBoletim" class="form-label">CPF:</label>
		<input type="text" class="form-control"
			id="cpfRegistroDeFatoComBoletim" name="cpfRegistroDeFatoComBoletim"
			maxlength="14" placeholder="000.000.000-00">
	</div>
	<div class="col-sm-3 col-md-4">
		<label for="cidadeRegistroDeFatoComBoletim" class="form-label">Cidade:</label>
		<select class="form-select" id="cidadeRegistroDeFatoComBoletim"
			name="cidadeRegistroDeFatoComBoletim">
			<option value="">Todas</option>
		</select>
	</div>
	<div class="col-sm-3 col-md-4">
		<label for="situacaoRegistroDeFatoComBoletim" class="form-label">Situação
			R. de Fato:</label> <select class="form-select"
			id="situacaoRegistroDeFatoComBoletim"
			name="situacaoRegistroDeFatoComBoletim">
			<option value="">Todos</option>
			<option value="1">Ativo</option>
			<option value="2">Encerrado</option>
		</select>
	</div>
	<div id="filtrosAdicionais">
		<div class="row gy-3 mb-3">
			<div class="col-12">
				<div class="border rounded p-3 bg-light">
					<h6 class="mb-0">
						<i class="fas fa-filter"></i> Filtros de Registro de Fato
					</h6>
					<hr class="mt-0 mb-1" />
					<div class="form-check mb-3">
						<input class="form-check-input" type="checkbox" value="1"
							id="chkFiltrosAvancados"> <label class="form-check-label"
							for="chkFiltrosAvancados"> Filtros avançados do registro
							de fato </label>
					</div>
					<div id="divFiltrosRegistroFato" style="display: none;">
						<div class="row gy-3">
							<div class="col-md-3">
								<div class="form-group">
									<label for="origemBoletim" class="form-label">Origem do
										boletim:</label> <select id="origemBoletim" class="form-select">
										<option value="">Todos</option>
										<option value="1">ORIGEM_BOLETIM_ATENDIMENTO</option>
										<option value="2">ORIGEM_BOLETIM_DIRETO</option>
									</select>
								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label for="infoFaltante" class="form-label">Informações
										faltantes?</label> <select id="infoFaltante" class="form-select">
										<option value="">Não</option>
										<option value="1">Sim</option>
									</select>
								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label for="incluirVeiculos" class="form-label">Veículos:</label>
									<select id="incluirVeiculos" class="form-select">
										<option value="">Todos</option>
										<option value="0">Buscar Registros sem veículos</option>
										<option value="1">Buscar Registros com veículos</option>
									</select>
								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label for="incluirMonitorados" class="form-label">Incluir
										veículos monitorados?</label> <select id="incluirMonitorados"
										class="form-select">
										<option value="0">Não</option>
										<option value="1">Sim</option>
									</select>
								</div>
							</div>
						</div>

						<%-- Distanciamento dos filtros --%>
						<div style="padding: 10px"></div>

						<div class="row gy-3">
							<div class="col-md-3">
								<div class="form-group">
									<label for="filtroObjetoEnvolvido" class="form-label">Objeto
										Envolvido: </label> <select class="form-select" id="filtroObjeto"
										name="tipo" required>
										<option value="">Todos</option>
										<option value="Celular">Celular</option>
										<option value="Carteira">Carteira</option>
										<option value="Bolsa">Bolsa</option>
										<option value="Mochila">Mochila</option>
										<option value="Joias">Joias</option>
										<option value="Relógios">Relógios</option>
										<option value="Eletrï¿½nicos">Eletrônicos (notebook,
											tablet, câmera)</option>
										<option value="Chaves">Chaves</option>
										<option value="Óculos">Óculos</option>
										<option value="Armas">Armas</option>
										<option value="Munições">Munições</option>
										<option value="Explosivos">Explosivos</option>
										<option value="Medicamentos">Medicamentos</option>
									</select>
								</div>
							</div>

							<div class="col-md-3">
								<div class="form-group">
									<label for="comBoletim" class="form-label">Característica:</label>
									<select id="comBoletim" class="form-select">
										<option value="">Todos</option>
										<option value="COM_BOLETIM">Com Boletim</option>
										<option value="SEM_BOLETIM">Sem Boletim</option>
									</select>
								</div>
							</div>

							<div class="col-md-3">
								<div class="form-group">
									<label for="tipoRegistro" class="form-label">Tipo de
										Registro:</label> <select id="tipoRegistro" class="form-select"
										disabled>
										<option value="">Carregando...</option>
									</select>
								</div>
							</div>

							<div class="col-md-3">
								<div class="form-group">
									<label for="naturezaRegistro" class="form-label">Natureza:</label>
									<select id="naturezaRegistro" class="form-select" disabled>
										<option value="">Carregando...</option>
									</select>
								</div>
							</div>
							<div class="col-md-3">
								<label for="dataInicioAlteracao" class="form-label">Data
									início da última alteração:</label> <input id="dataInicioAlteracao"
									type="datetime-local" class="form-control" />
							</div>
							<div class="col-md-3">
								<label for="dataFimAlteracao" class="form-label">Data
									fim da última alteração:</label> <input id="dataFimAlteracao"
									type="datetime-local" class="form-control" />
							</div>
							<div class="col-md-3">
								<label for="dataInicioFato" class="form-label">Data
									início do evento do fato:</label> <input id="dataInicioFato"
									type="datetime-local" class="form-control" />
							</div>
							<div class="col-md-3">
								<label for="dataFimFato" class="form-label">Data fim do
									evento do fato:</label> <input id="dataFimFato" type="datetime-local"
									class="form-control" />
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label for="tipoAcesso" class="form-label">Tipo de
										acesso:</label> <select id="tipoAcesso" class="form-select">
										<option value="" selected>Todos</option>
										<option value="0">Público</option>
										<option value="1">Privado</option>
									</select>
								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label for="nomeOperador" class="form-label">Operador
										que cadastrou:</label> <select id="nomeOperador" class="form-select">
										<option value="" selected>Todos</option>
									</select>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="nomeEnvolvido" class="form-label">Pessoas
										envolvidas (nome): </label> <input type="text" class="form-control"
										id="nomeEnvolvido" name="nomeEnvolvido" />
								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label for="acessoPermitido" class="form-label">Acesso
										Permitido: </label> <select class="form-select"
										id="acessoPermitido" name="acessoPermitido">
										<option value="">Todos</option>
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
</form>

<div
	class="col-sm-12 col-md-12 d-flex justify-content-end align-items-end gap-2 pt-3">
	<%@ include
		file="/muralha-digital/pages/registro_fato/comBoletim/botao-cadastro/botao-cadastro-registroDeFato.jsp"%>
	<button type="button" class="btn btn-primary"
		onclick="executaPesquisaComBoletimBotao(true)">
		<i class="fas fa-search"></i> PESQUISAR
	</button>
	<button type="button" class="btn btn-warning"
		onclick="limparFiltrosComBoletim()">
		<i class="fas fa-eraser"></i> Limpar Filtros
	</button>
</div>

<div id="global-loading" class="global-loading d-none">
    <div class="loading-box">
        <div class="spinner-border text-primary" role="status"></div>
        <div class="mt-2 fw-semibold">Carregando...</div>
    </div>
</div>

<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>