<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!-- Rodapé inicia o html e também o body -->    
<%@ include file="/includes/cabecalho_gwt.jsp" %>

	<%--                                           --%>
	<%-- Corrige erros causados pelo css do GTW    --%>
	<%--                                           --%>
	<style type="text/css">
	input[type="text"], input[type="password"], textarea, select {
		font-family: verdana, serif;
		font-size: 11px;
		font-weight: normal;
		width: 50px;
		color: #6b81a6;
		background-color: #ffffff;
		border: #b5b5b5 1pt solid;
	}
	</style>
	
	<!-- Injeta o widget -->   
	<div id="CADASTRO_USUARIO_PLACE_HOLDER"></div>

<%-- Rodapé fecha o body e também o html --%>
<%@ include file="/includes/rodape.jsp" %>