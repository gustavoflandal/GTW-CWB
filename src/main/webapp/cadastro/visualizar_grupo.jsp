<%@page import="com.consilux.model.Mensagem"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Grupo"%>
<jsp:useBean id="grupo" class="com.consilux.model.beans.GrupoBean" scope="session"/>
<%
    String sIdGrupo = (String)request.getParameter("id_grupo");
    if (sIdGrupo == null || !Pattern.matches("[0-9]{1,8}",sIdGrupo)) {
        new Mensagem(response).showErro("Identificador do usuário invalido!");
        return;
    }
    
    Grupo gru = Grupo.buscaGrupoPorIdGrupo(Integer.valueOf(sIdGrupo));
    gru.getToGrupoBean(grupo);
%>
<jsp:setProperty name="grupo" property="*" /> 
<%@ include file="/WEB-INF/templates/cadastro/vis_grupo.jsp" %>
<%@ include file="/includes/rodape.jsp" %>
