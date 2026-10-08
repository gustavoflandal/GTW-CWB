<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%-- Rodapé inicia o html e também o body --%>    
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="gwt:property" content="locale=pt_BR">
		<title>GTW</title>
		<link rel="stylesheet" href="/css/gtw.css" media="screen" type="text/css">
		
		<%-- Injeta a chave do GoogleMaps --%> 
		<%
			String key;
			Configuracao configuracao = ConfiguracaoProvider.getInstance();
			ConfiguracaoMapa configuracaoMapa = configuracao.getConfiguracaoMapa();
			key = configuracaoMapa.getGoogleMapsKey();
		%>
	    <% if ("localhost".equals(request.getServerName())) { %>
	    	<script src="http://maps.google.com/maps?gwt=1&amp;file=api&amp;v=2" ></script>
	    <% } else { %>
			<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;sensor=false&amp;key=<%=key%>" type="text/javascript"></script>
		<% } %>
	</head>
	
	<%@page import="com.consilux.conf.Configuracao"%>
	<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
	<%@page import="com.consilux.conf.ConfiguracaoMapa"%>

	<!-- Injeta o widget -->
	<script type="text/javascript" language="javascript" src="/GtwWidgets/GtwWidgets.nocache.js"></script>
	<div id="MONITORAMENTO_MAP_PLACE_HOLDER"></div>

<%-- Rodapé fecha o body e também o html --%>
	<%@ include file="/includes/rodape.jsp" %>