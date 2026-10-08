<%@page import="com.consilux.model.Mensagem"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="com.consilux.model.InfracaoNotificacao"%>
<jsp:useBean id="nai" class="com.consilux.model.beans.NAIBean" scope="session"/>
<%
	if (InfracaoNotificacao.indentificaCondutor(nai)) {
		session.removeAttribute("nai");
		new Mensagem(response).showSucesso("Condutor Identificado!");
	}
	else
		new Mensagem(response).showErro("Infração não alterada!");
%>
<%@ include file="/includes/rodape.jsp" %>
