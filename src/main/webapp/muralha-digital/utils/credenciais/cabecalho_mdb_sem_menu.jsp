<%@page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="root" value="/" />
<%@page import="com.consilux.model.Menu"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.consilux.model.Acesso"%>
<%@page import="com.consilux.lib.Conexao"%>

<%@page import="com.consilux.model.Usuario"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.infra.exception.ConexaoException"%>

<html lang="en" data-bs-theme="light">
	<head>
	
	  <title>GTW</title>
	
	  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  <meta http-equiv="x-ua-compatible" content="ie=edge">
	  
	  	<script type="text/javascript">
			var urlRoot = "${root}";
		</script>
	  
	
	  <!-- Bootstrap core CSS -->
	  <link rel="stylesheet" href="/muralha-digital/assets/MDB-Free/css/bootstrap.min.css">
	  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
	  <!-- Material Design Bootstrap -->
	  <link rel="stylesheet" href="/muralha-digital/assets/MDB-Free/css/mdb.css">
	  <!-- Your custom styles (optional) -->
	  <link rel="stylesheet" href="/muralha-digital/assets/MDB-Free/css/style.css">
	  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
	  
	  <script type="text/javascript" src="/muralha-digital/assets/jquery/jquery-3.6.0.min.js"></script>
	  <script type="text/javascript" src="/muralha-digital/utils/credenciais/assets/js/cabecalho.js"></script>
<!-- 	  	<script type="text/javascript" src="/muralha-digital/assets/js/modo-escuro.js"></script> -->
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
			loginAtual = acesso.getUsuario().getNome();
			tempoMaxSessao = request.getSession(false).getMaxInactiveInterval();
			usuarioLogado = true;
			%>
				<c:set var="jsessionid" value="<%=request.getSession(false).getId()%>" scope="request"/>
				<c:set var="usuarioNome" value="<%=acesso.getUsuario().getNome()%>" scope="request"/>
				<c:set var="usuarioID" value="<%=acesso.getUsuario().getId()%>" scope="request"/>
				<c:set var="manterConectado" value='<%= request.getSession(false) != null  && request.getSession(false).getAttribute("manterConectado") != null  ? "true"  : "false" %>'/>
			<%
		}
	%>
	<c:set var="emSessao" value="<%=emSessao%>" />
		
	<script type="text/javascript" language="javascript">
	
		var usuarioLogado 	= <%=usuarioLogado%>; 		
		var tempoIni 		= (new Date()).getTime();		
		var jsessionid 		= "${jsessionid}"; 
		var usuarioNome 	= "${usuarioNome}";
		var usuarioID 		= "${usuarioID}";	
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
		function iniciaRestanteSessao() 
		{
			tempoIni = (new Date()).getTime();
		}
	
		function openWindowCenter(url,name,w,h) {
			leftVal = (screen.availWidth/2) - (w/2);
			topVal = (screen.availHeight/2) - (h/2);
			newWindow = window.open(url, name, 'toolbar=no,location=no,personalbar=no,status=no,menubar=no,scrollbars=no,resizable=no,width=' + w + ',height=' + h + ',left=' + leftVal + ',top=' + topVal);
			newWindow.focus();
		}

	</script>
	
	<script type="text/javascript" language="javascript">
		if (usuarioLogado && manterConectado) 
		{
			iniciaRestanteSessao();
			setInterval("calculaRestanteSessao()", 1000);
		}else{
			var	tempo_restante = document.getElementById("tempo_restante");
			tempo_restante.innerHTML = "∞";
		}
	</script>
	
	
	
	<body>
	
		<table id="topheader" border=0 cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td width="100%"><img src="/muralha-digital/assets/images/cabecalho_gct.png" class="img-fluid" alt="Responsive image"></td>
				<%
					if (loginAtual != null) {
				%>
				<td class="valor_campo" style="text-align: center; font-size: x-small; font-weight: bold;">
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
		
		
	
	  <!-- jQuery -->
	  <script type="text/javascript" src="/muralha-digital/assets/MDB-Free/js/jquery.min.js"></script>
	  <!-- Bootstrap tooltips -->
	  <script type="text/javascript" src="/muralha-digital/assets/MDB-Free/js/popper.min.js"></script>
	  <!-- Bootstrap core JavaScript -->
	  <script type="text/javascript" src="/muralha-digital/assets/MDB-Free/js/bootstrap.min.js"></script>
	  <!-- MDB core JavaScript -->
	  <script type="text/javascript" src="/muralha-digital/assets/MDB-Free/js/mdb.min.js"></script>
	
	</body>

</html>
