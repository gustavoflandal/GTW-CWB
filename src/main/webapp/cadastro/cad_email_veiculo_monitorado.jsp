<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!-- Rodapé inicia o html e também o body -->    
<%@ include file="/includes/cabecalho.jsp" %>

	<meta name="gwt:property" content="locale=pt_BR">

	<%--                                            --%>
	<%-- This script is required bootstrap stuff.   --%>
	<%-- You can put it in the HEAD, but startup    --%>
	<%-- is slightly faster if you include it here. --%>
	<%--                                            --%>
    <script type="text/javascript" language="javascript" src="/GtwWidgets/GtwWidgets.nocache.js"></script>

	<div id="EMAIL_VEICULO_MONITORADO"></div>

	<%-- OPTIONAL: include this if you want history support --%>
	<iframe id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>


<%-- Rodapé fecha o body e também o html --%>
<%@ include file="/includes/rodape.jsp" %>