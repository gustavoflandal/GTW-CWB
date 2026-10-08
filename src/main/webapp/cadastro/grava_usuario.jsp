<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="com.consilux.model.beans.UsuarioBean"%>
<jsp:useBean id="usuario" class="com.consilux.model.beans.UsuarioBean" scope="session"/>
<%
    UsuarioBean bean = (UsuarioBean)session.getAttribute("usuario");
	if (bean.getId() == 0 && Usuario.incluiUsuario(bean) != null) { //INCLUINDO
   		session.removeAttribute("usuario");
		new Mensagem(response).showSucesso("Usuário cadastrado!","/cadastro/listar_usuario.jsp");
	}
	else if (bean.getId() > 0) { //ALTERANDO
		Usuario usu = Usuario.buscaUsuarioPorIdUsuario(bean.getId());
		if (usu == null) {
	        new Mensagem(response).showErro("Usuário não cadastrado!","/cadastro/editar_usuario.jsp");
	        return;
		}
		usu.setFromUsuarioBean(bean);
		usu.alteraUsuario();
        session.removeAttribute("usuario");
        new Mensagem(response).showSucesso("Usuário alterado!","/cadastro/editar_usuario.jsp");
    }
%>
<%@ include file="/includes/rodape.jsp" %>
