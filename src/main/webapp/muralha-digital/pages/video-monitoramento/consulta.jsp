<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->
<!DOCTYPE html>

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Muralha Digital</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<link href="/muralha-digital/assets/datepicker/css/tempus-dominus.css" rel="stylesheet">
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
		
		<script type="text/javascript" src="/muralha-digital/assets/datepicker/js/moment.min.js"></script>
		<script type="text/javascript" src="/muralha-digital/assets/datepicker/js/moment-with-locales.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	</head>
	
	<script>
		var urlRoot = "${root}";
	</script>

	<body class="homepage">

		<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 
		
		<div class="container">
			<div class="row gy-3">
				<div class="col-sm-12">
      				<div class="mb-3">
						<h2 id="tituloTelaConsulta" class="text-center"><strong>Consulta de Histórico de Videos</strong></h2>
					</div>
				</div>
			</div>
		</div>
		
		<div id="divFiltrosConsulta" class="container">
			<div class="row gy-3">
		        <div class="col-sm-2">
      				<div class="mb-3">
			            <div class="form-group">
			            	<label for="dataInicio" class='form-label'>Data Inicio:</label>
							<div class='input-group' id='dataInicio' data-td-target-input='nearest' data-td-target-toggle='nearest'>
							  	<input id='dataInicioInput' type='text' class='form-control' data-td-target='#dataInicio' readonly/>
							   	<span class='input-group-text' data-td-target='#dataInicio' data-td-toggle='datetimepicker'>
							     	<span class='fas fa-calendar'></span>
							   	</span>
							</div>
			            </div>
		            </div>
		        </div>
		        <div class="col-sm-2">
      				<div class="mb-3">
			            <div class="form-group">
			            	<label for="dataFim" class='form-label'>Data Fim:</label>
							<div class='input-group log-event' id='dataFim' data-td-target-input='nearest' data-td-target-toggle='nearest'>
							  	<input id='dataFimInput' type='text' class='form-control' data-td-target='#dataFim' readonly/>
							   	<span class='input-group-text' data-td-target='#dataFim' data-td-toggle='datetimepicker'>
							     	<span class='fas fa-calendar'></span>
							   	</span>
							</div>
			            </div>
		            </div>
		        </div>
		        <div class="col-sm-8">
      				<div class="mb-3">
	           			<div class="form-group">
			                <label for="selEquipamento" class='form-label'>Equipamento:</label>
<!-- 			                <select class="form-select" id="selEquipamento" name="equipamento"> -->
<!-- 			                    <option value="0" selected="selected">--Todos os Equipamentos--</option> -->
<!-- 			                </select> -->
			                <select class="selectpicker form-control border" data-live-search="true" id="selEquipamento" name="equipamento" data-style="btn-white" data-size="20" data-none-selected-text="--Todos os equipamentos--">
			                    <option value="0" selected="selected">--Todos os Equipamentos--</option>
			                </select>
		                </div>
	                </div>
                </div>
			</div>
			<div class="row gy-3">
                <div class="col-sm-9">
      				<div class="mb-3">
						<div class="d-grid gap-2">
							<button id="pesquisar" class="btn btn-primary" type="button" onclick="ExecutaPesquisa()">PESQUISAR</button>
						</div>
					</div>
				</div>
				<div class="col-sm-3">
      				<div class="mb-3">
						<div class="d-grid gap-2">
							<button id="limparFiltros" class="btn btn-warning" type="button" onclick="LimparFiltros()">LIMPAR FILTROS</button>
						</div>
					</div>
				</div>
           	</div>
        </div>
		
		<div class="container">
			<div class="row" id="listaVideosExibicao">
				<div class="col-md-12 d-flex justify-content-center">
				</div>
			</div>
		</div>
	 	<div class="overlay"></div>
	</body>
	
	<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>
	<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
	<script src="assets/js/consulta.js"></script>
</html>