<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.consilux.model.Acesso"%>
<%@page import="muralha.digital.acessos.UsuarioServlet"%>
<%@page import="muralha.digital.acessos.Menu"%>

<c:url var="root" value="/" />

<html lang="en" data-bs-theme="light">
	<head>
	
		<title>Cinturão de Segurança</title>
	
	  	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  	<meta http-equiv="x-ua-compatible" content="ie=edge">
	  
	  	<script type="text/javascript">
	  		var urlRoot = "${root}";
	  	</script>
	  
	  	<!-- Bootstrap core CSS -->
		<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
	  	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" integrity="sha512-1ycn6IcaQQ40/MKBW2W4Rhis/DbILU74C1vSrLJxCq57o941Ym01SwNsOMqvEBFlcgUa6xLiPY/NS5R+E6ztJQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	  	<link rel="stylesheet" href="/muralha-digital/utils/credenciais/assets/css/muralha_abertura_bootstrap.css">
	  
  		<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery-3.6.0.min.js"></script>
  		<script type="text/javascript" src="/muralha-digital/assets/bootstrap5/js/popper.min.js"></script>
		<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
  		<script type="text/javascript" src="/muralha-digital/utils/credenciais/assets/js/cabecalho.js"></script>
    	<script type="text/javascript" src="/login/assets/js/login_logout.js"></script>
    	<script type="text/javascript" src="/muralha-digital/utils/credenciais/assets/js/notificacoes_nao_tratadas.js"></script>
    	
<!--     	<script type="text/javascript" src="/muralha-digital/assets/js/modo-escuro.js"></script> -->
	</head>
	
	<%
		Boolean emSessao = request.getSession(false) != null;
		Boolean usuarioLogado = false;
		String loginAtual = null;
		Integer tempoMaxSessao = null;
		String desc_modulo = null;
		Integer idUsuario = null;
				
		if (emSessao) 
		{
			Acesso acesso = new Acesso(request, response, true); 
			if (!acesso.verificaAcesso()) { return; }
			loginAtual = acesso.getUsuario().getNome();
			idUsuario = acesso.getUsuario().getId();
			tempoMaxSessao = request.getSession(false).getMaxInactiveInterval();
			usuarioLogado = true;
			
			//Obtendo o parâmetro menu_info_pai da URL com validação
			String menuInfoPaiParam = request.getParameter("menu_info_pai");
			desc_modulo = request.getParameter("desc");
			int ID_menu_info_pai = 0;
			if (menuInfoPaiParam != null && !menuInfoPaiParam.isEmpty()) {
				try {
					ID_menu_info_pai = Integer.parseInt(menuInfoPaiParam);
				} catch (NumberFormatException e) {
					// Handle invalid number format, default to null
					ID_menu_info_pai = 0;
				}
			}
			
			//Obtendo a lista de Menus da Muralha Digital
			List<Menu> menus	= UsuarioServlet.ObterListaMenusAcesso(acesso.getUsuario().getId(), ID_menu_info_pai);
			List<Menu> menusRel = UsuarioServlet.ObterListaMenusRelatorioAcesso(acesso.getUsuario().getId());
			List<Menu> menusGra = UsuarioServlet.ObterListaMenusGraficosAcesso(acesso.getUsuario().getId());
			
			%>
				<c:set var="jsessionid" value="<%=request.getSession(false).getId()%>" scope="request"/>
				<c:set var="usuarioNome" value="<%=acesso.getUsuario().getNome()%>" scope="request"/>
				<c:set var="usuarioID" value="<%=acesso.getUsuario().getId()%>" scope="request"/>
				<c:set var="emSessao" value="<%=emSessao%>" />
				<c:set var="menus" value="<%=menus%>" />
				<c:set var="menusRel" value="<%=menusRel%>" />
				<c:set var="menusGra" value="<%=menusGra%>" />
				<c:set var="manterConectado" value='<%= request.getSession(false) != null  && request.getSession(false).getAttribute("manterConectado") != null  ? "true"  : "false" %>'/>
			<%
		}
	%>
		
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
		
		function iniciaRestanteSessao()  { tempoIni = (new Date()).getTime(); }
	
		function openWindowCenter(url,name,w,h) 
		{
			leftVal = (screen.availWidth/2) - (w/2);
			topVal = (screen.availHeight/2) - (h/2);
			newWindow = window.open(url, name, 'toolbar=no,location=no,personalbar=no,status=no,menubar=no,scrollbars=no,resizable=no,width=' + w + ',height=' + h + ',left=' + leftVal + ',top=' + topVal);
			newWindow.focus();
		}
		
		console.log('Tela da Muralha Digital carregada');

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
	
		<!--Navbar -->
		<nav class="navbar navbar-expand-lg navbar-dark" style="background-color: #0d75bf;">
			<div class="container-fluid">
				<a class="navbar-brand" href="${root}login/abertura-sistemas.jsp">
					<img src="/muralha-digital/assets/images/gct_logo.png" height="30" alt="Consilux logo" style="background-color: #0d75bf;">
			  	</a>
				<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarTogglerDemo01" aria-controls="navbarTogglerDemo01" aria-expanded="false" aria-label="Toggle navigation">
				  	<span class="navbar-toggler-icon"></span>
				</button>
				<div class="collapse navbar-collapse" id="navbarTogglerDemo01">
				  	<ul class="navbar-nav me-auto mb-2 mb-lg-0">
						<li class="nav-item">
							<a class="nav-link active" aria-current="page" href="${root}login/abertura-sistemas.jsp"><b>Sistemas GCT</b></a>
						</li>	
						<li class="nav-item">
			          		<a class="nav-link active" aria-current="page" href="${root}login/muralha_principal.jsp">&nbsp;&nbsp;||&nbsp;&nbsp;&nbsp; Home</a>
			        	</li>
					
		      		    <li class="nav-item dropdown">
					        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false" style="color:white;">
					        	Processamento
				        	</a>
					        <div class="dropdown-menu dropdown-menu-dark" style="background-color: #0d75bf;" aria-labelledby="navbarDropdownMenuLink">
					        	
					        	<c:forEach var="menu" items="${menus}" >
					          		<a class="dropdown-item" href="${menu.href}" style="color:white;">${menu.descricao}</a>
					          	</c:forEach>
					          	
				        	</div>
			      		</li>
		      		    <li class="nav-item dropdown">
					        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false" style="color:white;">
					        	Relatórios
				        	</a>
					        <div class="dropdown-menu dropdown-menu-dark" style="background-color: #0d75bf;" aria-labelledby="navbarDropdownMenuLink">
					        	
					        	<c:forEach var="menuRel" items="${menusRel}" >
					          		<a class="dropdown-item" href="${menuRel.href}" style="color:white;">${menuRel.descricao}</a>
					          	</c:forEach>
					          	
				        	</div>
			      		</li>
			      		<li class="nav-item dropdown">
					        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false" style="color:white;">
					        	Gráficos
				        	</a>
					        <div class="dropdown-menu dropdown-menu-dark" style="background-color: #0d75bf;" aria-labelledby="navbarDropdownMenuLink">
					        	
					        	<c:forEach var="menuGra" items="${menusGra}" >
					          		<a class="dropdown-item" href="${menuGra.href}" style="color:white;">${menuGra.descricao}</a>
					          	</c:forEach>
					          	
				        	</div>
			      		</li>
				    </ul>
				    <div class="pe-3 mb-2 mb-lg-0"><%@ include file="/muralha-digital/pages/pesquisa-rapida/dropdown-pesquisa-rapida.jsp" %></div>
				    <div id="status_notificacoes" class="text-warning"></div>
	      			
				    			    
			    	<ul class="navbar-nav ml-auto nav-flex-icons">
			      		
		      		    <li class="nav-item dropdown">
					        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false" style="color:orange;">
			        			<small><strong id="qtdeAlertasNaoTratados">-</strong></small>
			          			<i class="fas fa-envelope"></i>
				        	</a>
					        <div class="dropdown-menu dropdown-menu-dark" id="navbarDropdown_lembretes" style="background-color: orange;" aria-labelledby="navbarDropdownMenuLink">
				        	</div>
			      		</li>
			      		<li class="nav-item">
			      			<a class="nav-link position-relative" 
			      			href="/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?usuario=<%=idUsuario%>&assinado=0&statusAlerta=15ebba5f-c805-449e-83cc-227ed3b3ad3c" 
			      			id="linkAlertas" role="button"
							   aria-haspopup="true" aria-expanded="false" 
							   style="color:orange;" title="Alertas sem assinatura">
							    <i class="fas fa-bell"></i>
							    <span id="qtdeAlertasNaoAssinados"
							          class="badge-custom" style="display: none;">							        
							    </span>
							</a>
										      		
			      		</li>			      		
			      				      		

			      		<li class="nav-item dropdown">
			        		<a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false" style="color:white;">
			          			<i class="fas fa-user"></i>
			        		</a>
					        <%
								Cookie[] cookies = request.getCookies();
								boolean isMobileApp = false;
								if (cookies != null) {
									for (Cookie cookie : cookies) {
										if ("isMobileApp".equals(cookie.getName()) && "true".equals(cookie.getValue())) {
											isMobileApp = true;
											break;
										}
									}
								}
								boolean showLogout = !isMobileApp;
							%>

							<div class="dropdown-menu dropdown-menu-dark" style="background-color: #0d75bf;" aria-labelledby="navbarDropdownMenuLink">
								<% if (showLogout) { %>
									<a id="btnLogout" class="dropdown-item" href="#" style="color:white;">Encerrar Sessão</a>
								<% } %>
								<a class="dropdown-item" href="/login/login_change.jsp" style="color:white;">Alterar Senha</a>
							</div>
			      		</li>
			      		
			      		
			    	</ul>
			    	<div><span>&nbsp;&nbsp;</span></div>
					<span>
						<%
							if (loginAtual != null) 
							{
						%>
								<small><div class="text-light" style="background-color: #0d75bf;"><small>&nbsp;<%=loginAtual%>&nbsp;&nbsp;</small></div></small>
								<small><div class="text-light"  style="background-color: #0d75bf;" id="tempo_restante"></div></small>
						<%
							}
						%>
					</span>
					
					<div class="col-sm-1"></div>
				</div>
			</div>
		</nav>
		
 		<div class="row">
	       <div class="col-md-1"></div>		 	     
		   <div class="col-md-2">
				<div style="height: 100%">
					<div class="h-100 d-inline-block d-flex flex-column max-vh-100 justify-content-center align-items-center">
				    	<div class="d-flex flex-column">
					   	 	<img src="/muralha-digital/assets/images/consilux_medio_transparent_ipatinga_mg.png" class="img-fluid img-responsive mb-2">
					      	<h3 class="text-center mb-3"><strong>Cinturão de Segurança</strong></h3>
					      </div>
					</div>
				</div>
			</div>
			<div class="col-md-1"></div>	  
			
  		    <div class="col-md-8">
				<div class="container-fluid mt-2">
					
					<%
						if (desc_modulo != null) 
						{
					%>
							<br>
							<h2>Módulo de <%=desc_modulo%></h2>
							<br>
					<%
						}
					%>					
			
			        <!-- Main docs tabs -->
			        <div class="main-tabs-docs">
			            <div class="tab-content">
			                 <!--Overview-->
			                <div class="tab-pane fade in show active" id="docsTabsOverview" role="tabpanel">
			
			                    <!--Grid row-->
			                    <div class="row">
			
			                        <!--Grid column: Content-->
			                        <div class="col-lg-10 col-md-12">
							
										<hr class="mt-2 mb-3 text-muted">
										
										<!--Section: hover-->
										<section id="hover-effects">
	
										
										  <!--Section: Live preview-->
										  <section class="section-preview">
										
										    <!--Grid row-->
										    <div class="row">
										 
										    
												<c:forEach var="menu" items="${menus}" >
											        
												 	<div class="col-lg-3 col-md-12 mb-2">
														<div 	id="menu_${menu.idMenuInfo}"
																class="hover-blur"
																onclick='AbrirPagina("${menu.href}")'>
													        <a>
													          	<img src="${menu.src}" class="img-fluid img-responsive" alt="placeholder Responsive image">
													          	<span class="text-center fw-light text-white"><h6><b>${menu.descricao}</b></h6></span>
													        </a>
															<p class="text-center fw-light"><b>${menu.descDetalhada}</b></p>
														</div>
											      	</div>
											        
											     </c:forEach>   
												
											 </div>
										    </div>
										  </section>										  
										</section>
			                           </div>
			                        </div>
			                     </div>
			                   </div>
			               </div>
			            </div>
		       	</div>
	</body>	

	<%@ include file="/muralha-digital/utils/notificacoes-browser/notificacoes.jsp" %>
	<%@ include file="/muralha-digital/utils/notificacao/notificacao.jsp"%>
	
	
</html>
<style>
  .badge-custom {
    background-color: #dc3545;
    color: white;
    font-size: 9px;          /* tamanho do número */
    font-weight: bold;
    min-width: 18px;          /* largura mínima */
    height: 18px;             /* altura igual à largura */
    border-radius: 50%;       /* bolinha perfeita */
    display: flex;            /* flex para centralizar */
    align-items: center;      /* centraliza vertical */
    justify-content: center;  /* centraliza horizontal */
    position: absolute;
    top: 3px;                 /* ajusta posição vertical */
    right: -5px;             /* ajusta posição horizontal */
    box-shadow: 0 0 3px rgba(0,0,0,0.4);
    line-height: 1;
}
</style>