<%@page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"
%>
<!DOCTYPE html>
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
	
	  <title>Sistemas de Trânsito GCT</title>
	
	  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  <meta http-equiv="x-ua-compatible" content="ie=edge">
	  
	  	<script type="text/javascript">
			var urlRoot = "${root}";
		</script>
	  
	
	  <!-- Bootstrap core CSS -->
	  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
	  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" integrity="sha512-1ycn6IcaQQ40/MKBW2W4Rhis/DbILU74C1vSrLJxCq57o941Ym01SwNsOMqvEBFlcgUa6xLiPY/NS5R+E6ztJQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	  <link rel="stylesheet" href="/login/assets/css/abertura-sistemas-corpo.css">
	  
	  <script type="text/javascript" src="/login/assets/js/abertura-sistemas.js"></script>
<!-- 	  <script type="text/javascript" src="/muralha-digital/assets/js/modo-escuro.js"></script> -->
	</head>
	
	<%
		Boolean emSessao = request.getSession(false) != null;
		Boolean usuarioLogado = false;
		String loginAtual = null;
		Integer tempoMaxSessao = null;
		String manterConectado = request.getSession(false).getAttribute("manterConectado") != null  ? "true"  : "false";
		
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
				<%
					if (loginAtual != null) {
				%>
				<td class="valor_campo" style="text-align: center; font-size: x-small; font-weight: bold;">
					Usuário&nbsp;Atual
					<div class="visualiza_campo" style="text-align: center;"><%=loginAtual%></div>
				<%
					}
				%>
				<%
					if (manterConectado != "true") {
				%>
					Tempo&nbsp;restante
					<div id="tempo_restante" class="visualiza_campo" style="text-align: center;"></div>
				<%
					}
				%>
				</td>
			</tr>
		</table>
		
 		<div class="row min-vh-100">
			<div class="col-md-1"></div>		 	     
		    <div class="col-md-2">
				<div class="h-75 d-inline-block d-flex flex-column justify-content-center align-items-center">
			    	<div class="d-flex flex-column">
				   	 	<img src="/muralha-digital/assets/images/consilux_medio_transparent_ipatinga_mg.png" class="img-fluid img-responsive mb-2">
				      	<h3 class="text-center mb-3"><strong>Sistemas de Trânsito</strong></h3>
				      </div>
				</div>
			</div>
			<div class="col-md-1"></div>	  
  
  		    <div class="col-md-8">
				<div class="container-fluid mt-2">
			
			    	<!-- Main docs tabs -->
			        <div class="main-tabs-docs">
			            <div class="tab-content">
			                 <!--Overview-->
							<div class="tab-pane fade in show active" id="docsTabsOverview" role="tabpanel">
			
			                	<!--Grid row-->
			                    <div class="row">
			
			                        <!--Grid column: Content-->
			                        <div class="col-lg-10 col-md-12 mb-4">
										<hr class="mt-4 mb-5 text-muted">
										
										<!--Section: hover-->
										<section id="hover-effects">
										

										  
										<div id="txt_sem_softwares">Não há sistemas configurados na base de dados!</div>
										  
										<!--Section: Live preview-->
										<section class="section-preview">
											<!--Grid row-->
										    <div class="row">
												<!--Grid column-->
											    <div class="col-lg-4 col-md-12 mb-4">
													<div class="hover-blur" onclick='abrirMuralhaDigital()' id="muralha_digital">
														<a>
													    	<img src="/login/images/abertura_muralha_1.jpg" class="img-fluid img-responsive" alt="placeholder Responsive image">
													        <span class="text-center fw-light text-white"><h6><b id="txt_muralha_digital">Cinturão de Segurança</b></h6></span>
													    </a>
														<p class="text-center fw-light"><b>Cinturão de Segurança</b></p>
													</div>
												</div>

												<div class="col-lg-4 col-md-12 mb-4">
													<div class="hover-blur" onclick='abrirGTWAntigo()' id="gtw">
														<a>
													    	<img src="/login/images/abertura_gtw_1.jpg" class="img-fluid img-responsive" alt="placeholder Responsive image">
													        <span class="text-center fw-light text-white"><h6><b id="txt_gtw">Gestão de Trânsito Web</b></h6></span>
												        </a>
														<p class="text-center fw-light"><b>Gestão de Trânsito</b></p>
													</div>
												</div>
											      
											    <div class="col-lg-4 col-md-12 mb-4">
													<div class="hover-blur" onclick="abrirMobilidadeUrbana()" id="mobilidade_urbana">
														<a>
													    	<img src="/login/images/mobilidade1.jpg" class="img-fluid img-responsive" alt="placeholder Responsive image">
													        <span class="text-center fw-light text-white"><h6><b id="txt_mobilidade_urbana">Mobilidade Urbana</b></h6></span>
													    </a>
														<p class="text-center fw-light"><b>Mobilidade Urbana</b></p>
													</div>
												</div>												
											      
											    <div class="col-lg-4 col-md-12 mb-4">
													<div class="hover-blur" onclick='abrirJARI()' id="jari">
														<a>
													       	<img src="/login/images/abertura_jari_1.png" class="img-fluid img-responsive" alt="placeholder Responsive image">
												          	<span class="text-center fw-light text-white"><h6><b>J.A.R.I.</b></h6></span>
												        </a>
														<p class="text-center fw-light"><b id="txt_jari">J.A.R.I.</b></p>
													</div>
												</div>
											</div>
										    <!--Grid row-->
										</section>										  
										</section>
		                        	</div>
		                        </div>
		                    </div>
	                	</div>
	            	</div>
	        	</div>
			</div>
		</div>
	  	<!-- Your custom scripts (optional) -->
	  	<script type="text/javascript"></script>
	</body>

</html>
