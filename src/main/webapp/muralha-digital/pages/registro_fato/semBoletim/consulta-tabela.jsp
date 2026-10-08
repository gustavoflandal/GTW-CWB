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
				<th>Tipo Registro de Fato</th>
				<th>Nome / CPF</th>
				<th>Veículo</th>
				<th>Situação</th>
				<th>Data Cadastro</th>
				<th>Ação</th>
			</tr>
		</thead>
		<tbody id="resultadoTabela">
			<!-- Linhas dinâmicas aqui -->
		</tbody>
	</table>
</div>
<%@ include
	file="/muralha-digital/utils/paginacao/componente-paginacao.jsp"%>
<script type="text/javascript"
	src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>