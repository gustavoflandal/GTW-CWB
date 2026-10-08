<%@page import="com.consilux.model.Mensagem"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Usuario"%>
<jsp:useBean id="usuario" class="com.consilux.model.beans.UsuarioBean" scope="session"/>
<%
    String sIdUsuario = (String)request.getParameter("id_usuario");
    if (sIdUsuario == null || !Pattern.matches("[0-9]{1,8}",sIdUsuario)) {
        new Mensagem(response).showErro("Identificador do usuário invalido!");
        return;
    }
    
    Usuario usu = Usuario.buscaUsuarioPorIdUsuario(Integer.valueOf(sIdUsuario));
    usu.getToUsuarioBean(usuario);
%>
<jsp:setProperty name="usuario" property="*" /> 
<%@ include file="/WEB-INF/templates/cadastro/vis_usuario.jsp" %>
<%@ include file="/includes/rodape.jsp" %>
