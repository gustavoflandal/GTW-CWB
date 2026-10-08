<%@page import="com.consilux.model.Inconsistencia"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
    EtapaProcesso etapa = EtapaProcesso.IMAGENS_TESTE;
	List<Inconsistencia> inconsistencias = Inconsistencia.buscaTodasInconsistenciaPorEtapaProcesso(etapa);
%>
<%@page import="com.consilux.model.Processamento.EtapaProcesso"%>
<%@page import="com.consilux.model.Processamento"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
<c:set var="forca_sem_obliteracao" value="1"/>
<c:set var="strEtapaProcesso" value="Digitação de Imagens Teste" scope="request"/>
<c:set var="id_processo" value="<%=etapa.getId()%>" scope="request"/>
<c:set var="inconsistencias" value="<%=inconsistencias%>" scope="request"/>
<c:set var="ent_dados" value="/WEB-INF/templates/processo/ent_dados_imgteste.jsp"/>
<%@ include file="/WEB-INF/templates/processo/proc_infracao.jsp"%>
<%@ include file="/includes/rodape.jsp" %>