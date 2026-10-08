<div class="mb-1">
	<h5>
		<small>Lista de Registros de Fatos:</small>
	</h5>

	<div class="row">
		<div class="col-sm-12">
			<div class="form-group input-group">
				<span class="input-group-text"> <i class="fas fa-search"></i>
				</span> <input name="consulta" id="txt_consulta" placeholder="Consultar"
					type="text" class="form-control">
			</div>
		</div>
<!--
		<div class="col-sm-1">
			<div
				class="form-group input-group d-grid d-md-flex justify-content-md-end h-100">
				<button id="gerarPDF" type="button" class="btn btn-outline-danger"
					title="Exportar PDF" onclick="GerarRelatorio('pdf')">
					<i class="fas fa-file-pdf"></i>
				</button>
				<button id="gerarExcel" type="button"
					class="btn btn-outline-success" title="Exportar Excel"
					onclick="GerarRelatorio('xls')">
					<i class="fa fa-file-excel" aria-hidden="true"></i>
				</button>
			</div>
		</div>
 -->
	</div>
</div>
<div class="table-responsive mt-2">
	<table
		class="table table-bordered table-hover align-middle text-center">
			<thead class="table-light">
			  <tr>
			    <th data-sort="tipoDescricao" class="sortable">Tipo Registro de Fato <i class="fa fa-sort ms-1 sort-icon"></i></th>
			    <th data-sort="nome_cpf" class="sortable">Nome / CPF <i class="fa fa-sort ms-1 sort-icon"></i></th>
			    <th data-sort="veiculo_placa" class="sortable">Veículo <i class="fa fa-sort ms-1 sort-icon"></i></th>
			    <th data-sort="endereco" class="sortable">Endereço <i class="fa fa-sort ms-1 sort-icon"></i></th>
			    <th data-sort="statusDescricao" class="sortable">R. de Fato Situação <i class="fa fa-sort ms-1 sort-icon"></i></th>
			    <th data-sort="boletimSituacao" class="sortable">Boletim Situação <i class="fa fa-sort ms-1 sort-icon"></i></th>
			    <th data-sort="naturezaFato" class="sortable">Natureza do Fato <i class="fa fa-sort ms-1 sort-icon"></i></th>
			    <th data-sort="dataEvento" class="sortable">Data Evento <i class="fa fa-sort ms-1 sort-icon"></i></th>
			    <th data-sort="dataCriacao" class="sortable">Data Cadastro <i class="fa fa-sort ms-1 sort-icon"></i></th>
			    <th data-sort="dataModificacao" class="sortable">Data Alteração <i class="fa fa-sort ms-1 sort-icon"></i></th>
			    <th data-sort="dataEncerramento" class="sortable">Data Encerramento <i class="fa fa-sort ms-1 sort-icon"></i></th>
			    <th data-sort="privadoFato" class="sortable">Visibilidade <i class="fa fa-sort ms-1 sort-icon"></i></th>
			    <th>Ação</th>
			  </tr>
			</thead>
		<tbody id="resultadoTabelaComBoletim">
			<!-- Linhas dinâmicas aqui -->
		</tbody>
	</table>
</div>
<%@ include
	file="/muralha-digital/utils/paginacao/componente-paginacao.jsp"%>
<script type="text/javascript"
	src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>