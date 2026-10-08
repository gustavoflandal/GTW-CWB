<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- Rodapé inicia o html e também o body -->        
<%@ include file="/includes/cabecalho_gwt.jsp" %>

	<!-- Injeta o widget -->
	<div id="EVENTOS_PESQUISA_PLACE_HOLDER"></div>

<script>
	window.onload = function() {
		setWidthDataHora();
	};
	
	function setWidthDataHora() {
		var dataInicial = document.getElementById("x-auto-41-input");
		var dataFinal = document.getElementById("x-auto-48-input");
		var horaInicial = document.getElementById("x-auto-43-input");
		var horaFinal = document.getElementById("x-auto-50-input");
		
		dataInicial.style.width = "183px";
		dataFinal.style.width = "183px";
		horaInicial.style.width = "183px";
		horaFinal.style.width = "183px";
	}
</script>

<%-- Rodapé fecha o body e também o html --%>
<%@ include file="/includes/rodape.jsp" %>
