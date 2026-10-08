<%@page import="com.consilux.model.Inconsistencia"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
    EtapaProcesso etapa = EtapaProcesso.TRIAGEM;
    List<Inconsistencia> inconsistencias = Inconsistencia.buscaTodasInconsistenciaPorEtapaProcesso(etapa);
%>
<%@page import="com.consilux.model.Processamento.EtapaProcesso"%>
<%@page import="com.consilux.model.Processamento"%>
<c:set var="strEtapaProcesso" value="Triagem" scope="request"/>
<c:set var="id_processo" value="<%=etapa.getId()%>" scope="request"/>
<c:set var="inconsistencias" value="<%=inconsistencias%>" scope="request"/>
<%@ include file="/WEB-INF/templates/processo/proc_infracao2.jsp"%>
<%@ include file="/includes/rodape.jsp" %>