<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="com.consilux.model.Grupo"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<jsp:useBean id="grupo" class="com.consilux.model.beans.GrupoBean" scope="session"/>
<%
	String sIdGrupo = (String)request.getParameter("id_grupo");
	if (sIdGrupo == null || !Pattern.matches("[0-9]{1,8}",sIdGrupo)) {
	    Map<String,Object> mFiltro = new HashMap<String,Object>();
	    mFiltro.put("descricao","%"); //Lista todos.
	    List<Grupo> grupos = Grupo.buscaGrupoPor(mFiltro);
%>
		<c:set var="grupos" value="<%=grupos%>" />
		<c:set var="frm_action" value="excluir_grupo.jsp" />
		<c:set var="frm_text" value="Excluir" />
		<%@ include file="/WEB-INF/templates/cadastro/list_grupo.jsp" %>
<%
	}
	else {
	    Grupo gru = Grupo.buscaGrupoPorIdGrupo(Integer.valueOf(sIdGrupo));
	    gru.getToGrupoBean(grupo);
%>
	<c:set var="frm_action" value="remove_grupo.jsp" />
	<%@ include file="/WEB-INF/templates/cadastro/vis_grupo.jsp" %>
<%
	}
%>
<%@ include file="/includes/rodape.jsp" %>
