<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	Map<String,Object> mFiltro = new HashMap<String,Object>();
    mFiltro.put("nome","%"); //Lista todos.
	List<Usuario> usuarios = Usuario.buscaUsuarioPor(mFiltro);
%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.consilux.model.Usuario"%>
<c:set var="usuarios" value="<%=usuarios%>" />
<c:set var="frm_action" value="visualizar_usuario.jsp" />
<c:set var="frm_text" value="Visualizar" />
<%@ include file="/WEB-INF/templates/cadastro/list_usuario.jsp" %>
<%@ include file="/includes/rodape.jsp" %>
