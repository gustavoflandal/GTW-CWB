<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>GTW - Junção de Bases</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<script src="js/consulta.js"></script>
	</head>
	
	<body class="homepage">

		<div class="container">
			<div class="row gy-3">
				<div class="col-sm-12">
      				<div class="mb-3">
						<h2 id="tituloTelaConsulta" class="text-center"><strong>Importador de Dados</strong></h2>
					</div>
				</div>
			</div>
		</div>
		
		<div id="divCarregaCsv" class="container">
			<div class="row gy-3">
				<div class="col-sm-5">
					<div class="mb-3">
						<div class="form-group">
							
							<label id="labelImportaCsv" class="form-label" for="customFile">Selecionar arquivo CSV para importar dados</label>
							<input id="inputImportaCsv" type="file" class="form-control" id="carregaCsv" accept=".csv" />
							
						</div>
					</div>
				</div>
			</div>
			<div class="row gy-3">
				<div class="col-sm-1">
					<div class="py-0">
						<div class="form-group">
							
							<button id="btnImportaCsv" class="btn btn-primary" type="button" onclick='importaCSV()'>IMPORTAR</button>
							
						</div>
					</div>
				</div>
				<div id="divStatusCSV">Aguardando arquivo.</div>
			</div>
		</div>
		
		<div class="overlay"></div>
	</body>

</html>