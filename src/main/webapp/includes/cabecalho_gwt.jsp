<%@page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@page import="com.consilux.model.Menu"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.consilux.model.Acesso"%>
<%@page import="com.consilux.lib.Conexao"%>

<%@page import="com.consilux.model.Usuario"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.infra.exception.ConexaoException"%>
<%-- <html manifest="offline.manifest"> --%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="gwt:property" content="locale=pt_BR">
		<title>GTW</title>
		<link rel="stylesheet" href="/css/gtw.css" media="screen" type="text/css">
		<script type="text/javascript" language="javascript" src="/GtwWidgets/GtwWidgets.nocache.js"></script>
		<link rel="stylesheet" href="/css/GtwWidgets.css" media="screen" type="text/css">
</head>
<%
	Boolean emSessao = request.getSession(false) != null;
	Boolean usuarioLogado = false;
	String loginAtual = null;
	Integer tempoMaxSessao = null;
	
	if (emSessao) {
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso()) {
			return; //O usuário não tem acesso...então cai fora!
		}
		loginAtual = acesso.getUsuario().getUsuario();
		tempoMaxSessao = request.getSession(false).getMaxInactiveInterval();
		usuarioLogado = true;
	}
%>
<c:set var="emSessao" value="<%=emSessao%>" />
<c:set var="manterConectado" value='<%= request.getSession(false) != null  && request.getSession(false).getAttribute("manterConectado") != null  ? "true"  : "false" %>'/>

<script type="text/javascript" language="javascript">
	var usuarioLogado = <%=usuarioLogado%>; 
	var tempoIni = (new Date()).getTime();
	var manterConectado = "${manterConectado}";
		
	function calculaRestanteSessao() 
	{
	    var tempo_restante = document.getElementById("tempo_restante");

	    if (manterConectado === "true") {
	        tempo_restante.innerHTML = "";
	        return;
	    }

	    let tempo = (new Date()).getTime() - tempoIni;
	    tempo = tempo / 1000;
	    tempo = <%=tempoMaxSessao%> - tempo;

	    var minutos = parseInt(tempo / 60);
	    var segundos = parseInt(tempo - (minutos * 60));

	    if (tempo >= 0) {
	        tempo_restante.innerHTML = minutos + "min&nbsp;" + segundos + "s.";
	    } else {
	        tempo_restante.innerHTML = "<font color='red'>0</font>";
	    }
	}
	function iniciaRestanteSessao() {
		tempoIni = (new Date()).getTime();
	}

	function openWindowCenter(url,name,w,h) {
		leftVal = (screen.availWidth/2) - (w/2);
		topVal = (screen.availHeight/2) - (h/2);
		newWindow = window.open(url, name, 'toolbar=no,location=no,personalbar=no,status=no,menubar=no,scrollbars=no,resizable=no,width=' + w + ',height=' + h + ',left=' + leftVal + ',top=' + topVal);
		newWindow.focus();
	}
</script>
<body onunload="if (window.aoFechar) aoFechar();" onload="if (window.aoAbrir) aoAbrir();">
<div id="header">
	<table id="topheader" border=0 cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td width="100%"><img src="/images/cabecalho_gct.png"></td>
			<%
				if (loginAtual != null) {
			%>
			<td class="valor_campo" style="text-align: center;">
				Usuário&nbsp;Atual
				<div class="visualiza_campo" style="text-align: center;"><%=loginAtual%></div>
				Tempo&nbsp;restante
				<div id="tempo_restante" class="visualiza_campo" style="text-align: center;"></div>
			</td>
			<%
				}
			%>
		</tr>
	</table>
	<div id="BARRA_MENU_GTW"></div>
</div>
<script type="text/javascript" language="javascript">
	if (usuarioLogado) {
		iniciaRestanteSessao();
		setInterval("calculaRestanteSessao()", 1000);
	}
</script>