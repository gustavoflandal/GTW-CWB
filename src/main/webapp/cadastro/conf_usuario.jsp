<%@page import="com.consilux.model.Mensagem"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="java.util.regex.Pattern"%>
<jsp:useBean id="usuario" class="com.consilux.model.beans.UsuarioBean" scope="session"/>
<%
    String sID = request.getParameter("id");
	String sNome = request.getParameter("nome");
	String sEmail = request.getParameter("email");
	String sUsuario = request.getParameter("usuario");
	String sSenha = request.getParameter("senha");
	
    if (sNome == null || sNome.length() < 3 || sNome.length() > 60) {
    	new Mensagem(response).showErro("Nome enviado invalido!");
        return;
    }
    else if (sEmail == null || !Pattern.matches("^[a-z0-9][a-z0-9._-]{0,49}@[a-z0-9][a-z0-9._-]*\\.[a-z0-9]{2,3}$", sEmail)) {
        new Mensagem(response).showErro("Email enviado invalido!");
        return;
    }
    else if (sUsuario == null || sUsuario.length() < 3 || sUsuario.length() > 25) {
		new Mensagem(response).showErro("Login enviado invalido!");
		return;
	}
	else if ((sID == null) && (sSenha == null || sSenha.length() < 3 || sSenha.length() > 20)) { //Verifica se esta incluindo um novo, então testa a senha...
		new Mensagem(response).showErro("Senha enviada invalida!");
		return;
	}
    
    usuario.setAlterarSenha(request.getParameter("alterarSenha") != null);
    usuario.setAtivo(request.getParameter("ativo") != null);
		
%>
<jsp:setProperty name="usuario" property="*" /> 
<c:set var="frm_action" value="grava_usuario.jsp" />
<%@ include file="/WEB-INF/templates/cadastro/vis_usuario.jsp" %>
<%@ include file="/includes/rodape.jsp" %>
