<%@page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@page import="com.consilux.model.Acesso"%>

<%@page import="com.consilux.model.MensagemJS"%><html>
<head>
	<meta name="gwt:property" content="locale=pt_BR">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>GTW</title>
	<link rel="stylesheet" href="/css/gtw.css" media="screen" type="text/css">
</head>
<body onunload="if (window.aoFechar) aoFechar();" onload="if (window.aoAbrir) aoAbrir();">
<%
	Boolean emSessao = (request.getSession(false) != null);

	if (emSessao) {
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new MensagemJS(response).showErro("Usuário não atenticado!");
			return; //O usuário não tem acesso...então cai fora!
		}
		%>
		<c:set var="jsessionid" value="<%=request.getSession(false).getId()%>" scope="request"/>
		<script type="text/javascript" language="javascript">
			var jsessionid = "${jsessionid}"; 
			var usuarioLogado = true; 
		</script>
		<%		
	}
%>
