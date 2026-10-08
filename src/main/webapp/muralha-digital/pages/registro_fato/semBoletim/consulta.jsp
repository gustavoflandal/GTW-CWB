<%@page import="muralha.digital._ini.Inicializacao"%>
<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<%@ include
	file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Consulta de Registros de Fato sem Boletim</title>
<link rel="stylesheet"
	href="/muralha-digital/pages/registro_fato/semBoletim/css/consulta.css">
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
					page="/muralha-digital/pages/registro_fato/semBoletim/consulta-filtros.jsp" />
			</div>
			<div class="section-table mt-3">
				<jsp:include
					page="/muralha-digital/pages/registro_fato/semBoletim/consulta-tabela.jsp" />
			</div>
		</div>
	</div>
	<script src="/muralha-digital/pages/registro_fato/semBoletim/js/consulta.js"></script>
	<div class="overlay"></div>
</body>
</html>
