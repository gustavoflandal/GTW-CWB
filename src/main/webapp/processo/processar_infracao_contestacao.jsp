<%@page import="com.consilux.model.Grupo"%>
<%@page import="com.consilux.model.beans.GrupoBean"%>
<%@page import="com.consilux.model.Usuario"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.consilux.model.Inconsistencia"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%
	EtapaProcesso etapa = EtapaProcesso.CONTESTACAO;
	int cav = 0;
	
	Acesso acesso = new Acesso(request, response, true);
	Usuario usr = acesso.getUsuario();
	List<GrupoBean> grupos = Grupo.buscaGruposPorIdUsuario(usr.getId());
	for (GrupoBean g : grupos) {
		if (g.getDescricao().trim().equals("Contestação CAV")) // Contestação CAV
		{
			etapa = EtapaProcesso.CONTESTACAO_CAV;
			cav = 1;
		}
	}
    
	Inconsistencia inc1 = new Inconsistencia(1, "Erro CAI");
	Inconsistencia inc2 = new Inconsistencia(2, "Erro CAV");
	Inconsistencia inc3 = new Inconsistencia(3, "Erro Sistema");

    List<Inconsistencia> inconsistencias = new ArrayList<Inconsistencia>();
    inconsistencias.add(inc1);
    inconsistencias.add(inc2);
    inconsistencias.add(inc3);
%>
<%@page import="com.consilux.model.Processamento.EtapaProcesso"%>
<%@page import="com.consilux.model.Processamento"%>
<c:set var="cav" value="<%=cav %>" />
<c:set var="strEtapaProcesso" value="Contestação" scope="request"/>
<c:set var="id_processo" value="<%=etapa.getId()%>" scope="request"/>
<c:set var="inconsistencias" value="<%=inconsistencias%>" scope="request"/>
<c:set var="ent_dados" value="/WEB-INF/templates/processo/ent_dados_contestacao.jsp"/>
<%@ include file="/WEB-INF/templates/processo/proc_infracao_contestacao.jsp"%>

<%@ include file="/includes/rodape.jsp" %>