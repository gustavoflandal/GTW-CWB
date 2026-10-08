<%@page import="com.consilux.model.Mensagem"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="com.consilux.model.beans.GrupoBean"%>
<%@page import="com.consilux.model.Grupo"%>
<jsp:useBean id="grupo" class="com.consilux.model.beans.GrupoBean" scope="session"/>
<%
    GrupoBean bean = (GrupoBean)session.getAttribute("grupo");
	if (bean.getId() == 0 && Grupo.incluiGrupo(bean) != null) { //INCLUINDO
   		session.removeAttribute("grupo");
		new Mensagem(response).showSucesso("Grupo cadastrado!","/cadastro/listar_grupo.jsp");
	}
	else if (bean.getId() > 0) { //ALTERANDO
		Grupo gru = Grupo.buscaGrupoPorIdGrupo(bean.getId());
		if (gru == null) {
	        new Mensagem(response).showErro("Grupo não cadastrado!","/cadastro/editar_grupo.jsp");
	        return;
		}
		gru.setFromGrupoBean(bean);
		gru.alteraGrupo();
        session.removeAttribute("grupo");
        new Mensagem(response).showSucesso("Grupo alterado!","/cadastro/editar_grupo.jsp");
    }
%>
<%@ include file="/includes/rodape.jsp" %>
