<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/includes/cabecalho.jsp" %>
<%@page import="com.consilux.model.Processamento.EtapaProcesso"%>
<%@page import="com.consilux.model.Processo"%>
<c:set var="id_processo" value="<%=EtapaProcesso.LIBERACAO.getId()%>"/>
<%@ include file="/WEB-INF/templates/processo/processar_direto.jsp" %>
<%@ include file="/includes/rodape.jsp" %>
