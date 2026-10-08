<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>GTW - Relatório de Velocidade Média</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
		
		<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
		<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	</head>
	
	<body class="homepage">

		<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 
		
		<div class="container">
			<div class="row gy-3">
				<div class="col-sm-12">
      				<div class="mb-3">
						<h2 id="tituloTelaConsulta" class="text-center"><strong>Relatório de Velocidade Média</strong></h2>
					</div>
				</div>
			</div>
		</div>
		
		<div id="divFiltrosConsulta" class="container">
		
			<form action="/Relatorio/VelocidadeMedia" method="get" target="_blank">
				<div class="row gy-3">
	           		<div class="col-sm-1"></div>
	           		<div class="col-sm-1">
	      				<div class="mb-3">
				            <div class="form-group">
				            	<label for="dataInicio" class='form-label'>Mês:</label>
								<div class='input-group' id='mesFiltro' data-td-target-input='nearest' data-td-target-toggle='nearest'>
								  	<input id='mes' name='mes' type='text' class='form-control mes' maxlength="2"/>
								</div>
				            </div>
			            </div>
			        </div>
			        <div class="col-sm-1">
	      				<div class="mb-3">
				            <div class="form-group">
				            	<label for="dataFim" class='form-label'>Ano:</label>
								<div class='input-group log-event' id='anoFiltro' data-td-target-input='nearest' data-td-target-toggle='nearest'>
								  	<input id='ano' name='ano' type='text' class='form-control ano' maxlength="4"/>
								</div>
				            </div>
			            </div>
			        </div>
			        <div class="col-sm-7">
			        	<div class="mb-3">
		           			<div class="form-group">
				                <label for="selEquipamento" class='form-label'>Equipamento:</label>
				                <select class="selectpicker form-control border" data-live-search="true" id="selEquipamento" name="equipamento" data-style="btn-white" data-size="20" data-none-selected-text="--Todos os equipamentos--">
				                    <option value="0" selected="selected">--Todos os Equipamentos--</option>
				                </select>
			                </div>
		                </div>
			        </div>
			        <div class="col-sm-1">
	      				<div class="mb-3">
							<div class="d-grid gap-4">
								<label for="gerarExcel" class='form-label'></label>
								<button id="gerarExcel" type="submit" class="btn btn-success">Processar</button>
							</div>
						</div>
					</div>
			        <div class="col-sm-1"></div>
				</div>
           	</form>
        </div>

	</body>
	
	
	<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
	<script src="js/carregar-combos-filtros-relatorio.js"></script>
	<script src="js/distribuicao-faixa-velocidade.js"></script>

</html>