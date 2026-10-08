<%@page import="com.consilux.model.Mensagem"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<jsp:useBean id="grupo" class="com.consilux.model.beans.GrupoBean" scope="session"/>
<%
  //  String sID = request.getParameter("id");
	String sDescricao = request.getParameter("descricao");
	
    if (sDescricao == null || sDescricao.length() < 3 || sDescricao.length() > 20) {
    	new Mensagem(response).showErro("Descricao enviada invalida!");
        return;
    }
%>
<jsp:setProperty name="grupo" property="*" /> 
<c:set var="frm_action" value="grava_grupo.jsp" />
<%@ include file="/WEB-INF/templates/cadastro/vis_grupo.jsp" %>
<%@ include file="/includes/rodape.jsp" %>

