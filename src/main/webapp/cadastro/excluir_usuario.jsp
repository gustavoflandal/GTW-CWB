<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="com.consilux.model.Usuario"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<jsp:useBean id="usuario" class="com.consilux.model.beans.UsuarioBean" scope="session"/>
<%
	String sIdUsuario = (String)request.getParameter("id_usuario");
	if (sIdUsuario == null || !Pattern.matches("[0-9]{1,8}",sIdUsuario)) {
	    Map<String,Object> mFiltro = new HashMap<String,Object>();
	    mFiltro.put("nome","%"); //Lista todos.
	    List<Usuario> usuarios = Usuario.buscaUsuarioPor(mFiltro);
%>
		<c:set var="usuarios" value="<%=usuarios%>" />
		<c:set var="frm_action" value="excluir_usuario.jsp" />
		<c:set var="frm_text" value="Excluir" />
		<%@ include file="/WEB-INF/templates/cadastro/list_usuario.jsp" %>
<%
	}
	else {
	    Usuario usu = Usuario.buscaUsuarioPorIdUsuario(Integer.valueOf(sIdUsuario));
	    usu.getToUsuarioBean(usuario);
%>
	<c:set var="frm_action" value="remove_usuario.jsp" />
	<%@ include file="/WEB-INF/templates/cadastro/vis_usuario.jsp" %>
<%
	}
%>
<%@ include file="/includes/rodape.jsp" %>
