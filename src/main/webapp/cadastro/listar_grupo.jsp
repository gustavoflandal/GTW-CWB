<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	Map<String,Object> mFiltro = new HashMap<String,Object>();
    mFiltro.put("descricao","%"); //Lista todos.
	List<Grupo> grupos = Grupo.buscaGrupoPor(mFiltro);
%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.consilux.model.Grupo"%>
<c:set var="grupos" value="<%=grupos%>" />
<c:set var="frm_action" value="visualizar_grupo.jsp" />
<c:set var="frm_text" value="Visualizar" />
<%@ include file="/WEB-INF/templates/cadastro/list_grupo.jsp" %>
<%@ include file="/includes/rodape.jsp" %>
