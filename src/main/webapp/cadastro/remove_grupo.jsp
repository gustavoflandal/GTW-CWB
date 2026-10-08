<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.Grupo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="com.consilux.model.beans.GrupoBean"%>
<jsp:useBean id="grupo" class="com.consilux.model.beans.GrupoBean" scope="session"/>
<%
    GrupoBean bean = (GrupoBean)session.getAttribute("grupo");
	if (bean.getId() > 0) {
		Grupo usu = Grupo.buscaGrupoPorIdGrupo(bean.getId());
		if (usu == null) {
	        new Mensagem(response).showErro("Grupo não cadastrado!");
	        return;
		}
		usu.removeGrupo();
        session.removeAttribute("grupo");
        new Mensagem(response).showSucesso("Grupo removido!","/cadastro/excluir_grupo.jsp");
    }
%>
<%@ include file="/includes/rodape.jsp" %>
