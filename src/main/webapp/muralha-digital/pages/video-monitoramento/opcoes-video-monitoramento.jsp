<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>
<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 


<html lang="en">
	<head>
	
		<title>Cinturão de Segurança</title>
	
	  	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  	<meta http-equiv="x-ua-compatible" content="ie=edge">
	  
	  	<script type="text/javascript">
	  		var urlRoot = "${root}";
	  	</script>
	  
	  	<link rel="stylesheet" href="/muralha-digital/utils/credenciais/assets/css/muralha_abertura_bootstrap.css">
	</head>

	
	<body>
	
		
 		<div class="row">

			<div class="col-md-2"></div>	  
  
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
			                        <div class="col-lg-12 col-md-12">
							
										<hr class="mt-4 mb-5 text-muted">
										
										<!--Section: hover-->
										<section id="hover-effects">
										
										  <!--Title-->
										  <h5 class="section-heading mb-4">
										   <span class="fs-4">Escolha qual opção deseja:</span>
										  </h5>
										
										  <!--Section: Live preview-->
										  <section class="section-preview">
										
										    <!--Grid row-->
										    <div class="row">
										    
											 	<div class="col-lg-6 col-md-12 mb-3">
													<div 	id="menu_1"
															class="hover-blur"
															onclick='AbrirPagina("/muralha-digital/pages/mosaico/mosaico-videos.jsp")'>
												        <a>
												          	<img src="/muralha-digital/assets/images/video-wall/video-wall-final.jpg" class="img-fluid img-responsive" alt="placeholder Responsive image">
												          	<span class="text-center fw-light text-white"><h6><b>Mosaico de Vídeos</b></h6></span>
												        </a>
														<p class="text-center fw-light"><b>Mosaico de Vídeos</b></p>
													</div>
										      	</div>

											 	<div class="col-lg-6 col-md-12 mb-3">
													<div 	id="menu_1"
															class="hover-blur"
															onclick='AbrirPagina("/muralha-digital/pages/video-monitoramento/consulta.jsp")'>
												        <a>
												          	<img src="/muralha-digital/assets/images/video-wall/video-wall-final.jpg" class="img-fluid img-responsive" alt="placeholder Responsive image">
												          	<span class="text-center fw-light text-white"><h6><b>Consulta de Histórico de Vídeos</b></h6></span>
												        </a>
														<p class="text-center fw-light"><b>Consulta de Histórico de Vídeos</b></p>
													</div>
										      	</div>

												
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
			            
			            <div class="col-md-2"></div>
		       	</div>
	</body>	

	
	
</html>
