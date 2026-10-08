<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.Menu"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="com.consilux.model.beans.MenuBean"%>
<jsp:useBean id="menu" class="com.consilux.model.beans.MenuBean" scope="session"/>
<%
    MenuBean bean = (MenuBean)session.getAttribute("menu");
	if (bean.getId() == 0 && Menu.incluiUsuario(bean) != null) { //INCLUINDO
   		session.removeAttribute("menu");
		new Mensagem(response).showSucesso("Menu cadastrado!","/cadastro/listar_usuario.jsp");
	}
	else if (bean.getId() > 0) { //ALTERANDO
		Menu menu_now = Menu.buscaMenuPorIdMenu(bean.getId());
		if (menu_now == null) {
	        new Mensagem(response).showErro("Usuário não cadastrado!","/cadastro/editar_usuario.jsp");
	        return;
		}
		//menu_now.setFromUsuarioBean(bean);
		//menu_now.alteraUsuario();
        session.removeAttribute("menu");
        new Mensagem(response).showSucesso("Menu alterado!","/cadastro/editar_menu.jsp");
    }
%>
<%@ include file="/includes/rodape.jsp" %>
