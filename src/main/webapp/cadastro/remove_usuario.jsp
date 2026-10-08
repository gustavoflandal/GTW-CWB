<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="com.consilux.model.beans.UsuarioBean"%>
<jsp:useBean id="usuario" class="com.consilux.model.beans.UsuarioBean" scope="session"/>
<%
    UsuarioBean bean = (UsuarioBean)session.getAttribute("usuario");
	if (bean.getId() > 0) {
		Usuario usu = Usuario.buscaUsuarioPorIdUsuario(bean.getId());
		if (usu == null) {
	        new Mensagem(response).showErro("Usuário não cadastrado!");
	        return;
		}
		usu.removeUsuario();
        session.removeAttribute("usuario");
        new Mensagem(response).showSucesso("Usuário removido!","/cadastro/excluir_usuario.jsp");
    }
%>
<%@ include file="/includes/rodape.jsp" %>
