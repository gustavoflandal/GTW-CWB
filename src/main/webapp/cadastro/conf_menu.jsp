<%@page import="com.consilux.model.Mensagem"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="java.util.regex.Pattern"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<jsp:useBean id="menu" class="com.consilux.model.beans.MenuBean" scope="session"/>
<%
    String sID = request.getParameter("id");
	String sDescricao = request.getParameter("descricao");
	
    if (sDescricao == null || sDescricao.length() < 3 || sDescricao.length() > 50) {
    	new Mensagem(response).showErro("Descricao enviada invalida!");
        return;
    }
%>
<jsp:setProperty name="menu" property="*" /> 
<c:set var="frm_action" value="grava_menu.jsp" />
<%@ include file="/WEB-INF/templates/cadastro/vis_menu.jsp" %>
<%@ include file="/includes/rodape.jsp" %>
