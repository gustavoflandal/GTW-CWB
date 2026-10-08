<%@page import="com.consilux.model.Mensagem"%><%@page import="com.consilux.model.Inconsistencia"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
	new Mensagem(response).showConfirma("Deseja realmente liberar as infrações?","/processo/status_processa_direto.jsp");
%>
<%@ include file="/includes/rodape.jsp" %>