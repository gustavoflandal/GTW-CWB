<%@page import="muralha.digital._ini.Inicializacao"%>
<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<%@ include
	file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Consulta de Registros de Fato</title>
<link rel="stylesheet"
	href="/muralha-digital/pages/registro_fato/comBoletim/css/consulta.css">
<link rel="stylesheet"
	href="/muralha-digital/assets/css/pagina-carregando.css">
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<jsp:include
	page="/muralha-digital/pages/registro_fato/historico/registro-fato-modal-historico.jsp" />
</head>
<body class="homepage">
	<div class="container mt-2">
		<div class="row">
			<div class="section-filtros">
				<jsp:include
					page="/muralha-digital/pages/registro_fato/comBoletim/consulta-filtros.jsp" />
			</div>
			<div class="section-table mt-3">
				<jsp:include
					page="/muralha-digital/pages/registro_fato/comBoletim/consulta-tabela.jsp" />
			</div>
		</div>
	</div>
	<script
		src="/muralha-digital/pages/registro_fato/comBoletim/js/consulta.js"></script>
	<div class="overlay"></div>
</body>
</html>
