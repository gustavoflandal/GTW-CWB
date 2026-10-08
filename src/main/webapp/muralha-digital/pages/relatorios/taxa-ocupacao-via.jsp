<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>GTW - Taxa de Ocupação da Via</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
		<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>
	</head>
	
	<body class="homepage">

		<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 
		
		<div class="container">
			<div class="row gy-3">
				<div class="col-sm-12">
      				<div class="mb-3">
						<h2 id="tituloTelaConsulta" class="text-center"><strong>Relatório de Taxa de Ocupação da Via</strong></h2>
					</div>
				</div>
			</div>
		</div>
		
		<div id="divFiltrosConsulta" class="container">
		
			<div class="row gy-3">
           		<div class="col-sm-4"></div>
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
		        <div class="col-sm-4"></div>
			</div>

			<div class="row gy-3">
				<div class="col-sm-4"></div>
				<div class="col-sm-2">
      				<div class="mb-3">
						<div class="d-grid gap-2">
							<button id="gerarExcel" type="button" class="btn btn-outline-success" onclick="GerarRelatorio('xls')">
				                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-earmark-excel-fill" viewBox="0 0 16 16">
							  		<path d="M9.293 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.707A1 1 0 0 0 13.707 4L10 .293A1 1 0 0 0 9.293 0zM9.5 3.5v-2l3 3h-2a1 1 0 0 1-1-1zM5.884 6.68 8 9.219l2.116-2.54a.5.5 0 1 1 .768.641L8.651 10l2.233 2.68a.5.5 0 0 1-.768.64L8 10.781l-2.116 2.54a.5.5 0 0 1-.768-.641L7.349 10 5.116 7.32a.5.5 0 1 1 .768-.64z"></path>
								</svg>
				                Excel
							</button>
						</div>
					</div>
				</div>
				<div class="col-sm-2">
      				<div class="mb-3">
						<div class="d-grid gap-2">
							<button id="gerarPDF" type="button" class="btn btn-outline-danger" onclick="GerarRelatorio('pdf')">
				                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-earmark-pdf-fill" viewBox="0 0 16 16">
								  	<path d="M5.523 12.424c.14-.082.293-.162.459-.238a7.878 7.878 0 0 1-.45.606c-.28.337-.498.516-.635.572a.266.266 0 0 1-.035.012.282.282 0 0 1-.026-.044c-.056-.11-.054-.216.04-.36.106-.165.319-.354.647-.548zm2.455-1.647c-.119.025-.237.05-.356.078a21.148 21.148 0 0 0 .5-1.05 12.045 12.045 0 0 0 .51.858c-.217.032-.436.07-.654.114zm2.525.939a3.881 3.881 0 0 1-.435-.41c.228.005.434.022.612.054.317.057.466.147.518.209a.095.095 0 0 1 .026.064.436.436 0 0 1-.06.2.307.307 0 0 1-.094.124.107.107 0 0 1-.069.015c-.09-.003-.258-.066-.498-.256zM8.278 6.97c-.04.244-.108.524-.2.829a4.86 4.86 0 0 1-.089-.346c-.076-.353-.087-.63-.046-.822.038-.177.11-.248.196-.283a.517.517 0 0 1 .145-.04c.013.03.028.092.032.198.005.122-.007.277-.038.465z"/>
								  	<path fill-rule="evenodd" d="M4 0h5.293A1 1 0 0 1 10 .293L13.707 4a1 1 0 0 1 .293.707V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2zm5.5 1.5v2a1 1 0 0 0 1 1h2l-3-3zM4.165 13.668c.09.18.23.343.438.419.207.075.412.04.58-.03.318-.13.635-.436.926-.786.333-.401.683-.927 1.021-1.51a11.651 11.651 0 0 1 1.997-.406c.3.383.61.713.91.95.28.22.603.403.934.417a.856.856 0 0 0 .51-.138c.155-.101.27-.247.354-.416.09-.181.145-.37.138-.563a.844.844 0 0 0-.2-.518c-.226-.27-.596-.4-.96-.465a5.76 5.76 0 0 0-1.335-.05 10.954 10.954 0 0 1-.98-1.686c.25-.66.437-1.284.52-1.794.036-.218.055-.426.048-.614a1.238 1.238 0 0 0-.127-.538.7.7 0 0 0-.477-.365c-.202-.043-.41 0-.601.077-.377.15-.576.47-.651.823-.073.34-.04.736.046 1.136.088.406.238.848.43 1.295a19.697 19.697 0 0 1-1.062 2.227 7.662 7.662 0 0 0-1.482.645c-.37.22-.699.48-.897.787-.21.326-.275.714-.08 1.103z"/>
								</svg>
				                PDF
							</button>
						</div>
					</div>
				</div>
				<div class="col-sm-4"></div>
           	</div>
        </div>

	</body>
	
	<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>
 	<script src="js/taxa-ocupacao-via.js"></script>
 	<script src="js/gerar-arquivo-download.js"></script>
		
</html>