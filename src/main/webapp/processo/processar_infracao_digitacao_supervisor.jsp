<%@page import="com.consilux.model.Inconsistencia"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%
    EtapaProcesso etapa = EtapaProcesso.DIGITACAO_SUPERVISOR;
	List<Inconsistencia> inconsistencias = Inconsistencia.buscaTodasInconsistenciaPorEtapaProcesso(etapa);
	Boolean marcaCET = ConfiguracaoProvider.getInstance().getComMarcaProcesso();
	Boolean especie = ConfiguracaoProvider.getInstance().getComEspecieProcesso();
	List<MarcaCET> marcasCET = null;
	if (marcaCET) {
		marcasCET = MarcaCET.buscaTodasMarcasCET();
	}
	List<Especie> especies = null;
	if (especie) {
		especies = Especie.buscaTodasEspecies();
	}
%>
<%@page import="com.consilux.model.Processamento.EtapaProcesso"%>
<%@page import="com.consilux.model.Processamento"%>
<%@page import="com.consilux.model.MarcaCET"%>
<%@page import="com.consilux.model.Especie"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
<c:set var="strEtapaProcesso" value="Digitação" scope="request"/>
<c:set var="id_processo" value="<%=etapa.getId()%>" scope="request"/>
<c:set var="inconsistencias" value="<%=inconsistencias%>" scope="request"/>
<c:set var="marcas_CET" value="<%=marcasCET%>" scope="request"/>
<c:set var="especies" value="<%=especies%>" scope="request"/>
<c:set var="showInfo" value="1" scope="request"/>
<c:set var="ent_dados" value="/WEB-INF/templates/processo/ent_dados_digitacao.jsp"/>
<%@ include file="/WEB-INF/templates/processo/proc_infracao.jsp"%>
<%@ include file="/includes/rodape.jsp" %>