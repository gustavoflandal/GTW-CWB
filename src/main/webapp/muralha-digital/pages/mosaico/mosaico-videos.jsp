<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Muralha Digital</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
	</head>
	
	<script>
		var urlRoot = "${root}";
	</script>

	<body class="homepage">

		<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 
		<%@ include file="modal-config-mosaico.jsp" %>
		
		<div class="container">
			<div class="row pt-1">    
				<div class="col-md-1"></div>
				<div class="col-md-10">
					<div class="row pb-1">
						<div class="col-md-8 d-grid">
		           			<span class="p-2 bg-secondary text-white text-center rounded"><strong>MONITORAMENTO AO VIVO</strong></span>                    
						</div>
						<div class="col-md-4 d-grid">
							<div class="btn-group" role="group" aria-label="CONFIGURAÇÕES / ATUALIZAR">
							  	<button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalMosaicoConfiguracao" onclick="AbrirModalConfig()">CONFIGURAÇÕES</button>
							  	<button id="btnProcessar" class="btn btn-success" onclick="RotacionarCameras(false, true)">ATUALIZAR</button>
							</div>
						</div>
		        	</div>
				</div>
				<div class="col-md-1"></div>
			</div>
		</div>
		
		<div class="container">
			<div class="row pt-1">
				<div class="col-md-1"></div>
				<div class="col-md-10">
					<div class="row">
						<div class="col-md-4 d-flex justify-content-center">
							<figure class="figure">
								<img id='video_cam_1' name='idVeiculo_uuid' class='img-responsive thumbnail img-thumbnail' onerror='this.src="assets/images/ImgFundo4.png"' src='assets/images/ImgFundo4.png'>
								<figcaption id="legenda_video_1" class="figure-caption text-center"><small></small></figcaption>
							</figure>
						</div>
						<div class="col-md-4 d-flex justify-content-center">
							<figure class="figure">
								<img id='video_cam_2' name='idVeiculo_uuid' class='img-responsive thumbnail img-thumbnail' onerror='this.src="assets/images/ImgFundo4.png"' src='assets/images/ImgFundo4.png'>
								<figcaption id="legenda_video_2" class="figure-caption text-center"><small></small></figcaption>
							</figure>
						</div>
						<div class="col-md-4 d-flex justify-content-center">
							<figure class="figure">
								<img id='video_cam_3' name='idVeiculo_uuid' class='img-responsive thumbnail img-thumbnail' onerror='this.src="assets/images/ImgFundo4.png"' src='assets/images/ImgFundo4.png'>
								<figcaption id="legenda_video_3" class="figure-caption text-center"><small></small></figcaption>
							</figure>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4 d-flex justify-content-center">
							<figure class="figure">
								<img id='video_cam_4' name='idVeiculo_uuid' class='img-responsive thumbnail img-thumbnail' onerror='this.src="assets/images/ImgFundo4.png"' src='assets/images/ImgFundo4.png'>
								<figcaption id="legenda_video_4" class="figure-caption text-center"><small></small></figcaption>
							</figure>
						</div>
						<div class="col-md-4 d-flex justify-content-center">
							<figure class="figure">
								<img id='video_cam_5' name='idVeiculo_uuid' class='img-responsive thumbnail img-thumbnail' onerror='this.src="assets/images/ImgFundo4.png"' src='assets/images/ImgFundo4.png'>
								<figcaption id="legenda_video_5" class="figure-caption text-center"><small></small></figcaption>
							</figure>
						</div>
						<div class="col-md-4 d-flex justify-content-center">
							<figure class="figure">
								<img id='video_cam_6' name='idVeiculo_uuid' class='img-responsive thumbnail img-thumbnail' onerror='this.src="assets/images/ImgFundo4.png"' src='assets/images/ImgFundo4.png'>
								<figcaption id="legenda_video_6" class="figure-caption text-center"><small></small></figcaption>
							</figure>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4 d-flex justify-content-center">
							<figure class="figure">
								<img id='video_cam_7' name='idVeiculo_uuid' class='img-responsive thumbnail img-thumbnail' onerror='this.src="assets/images/ImgFundo4.png"' src='assets/images/ImgFundo4.png'>
								<figcaption id="legenda_video_7" class="figure-caption text-center"><small></small></figcaption>
							</figure>
						</div>
						<div class="col-md-4 d-flex justify-content-center">
							<figure class="figure">
								<img id='video_cam_8' name='idVeiculo_uuid' class='img-responsive thumbnail img-thumbnail' onerror='this.src="assets/images/ImgFundo4.png"' src='assets/images/ImgFundo4.png'>
								<figcaption id="legenda_video_8" class="figure-caption text-center"><small></small></figcaption>
							</figure>
						</div>
						<div class="col-md-4 d-flex justify-content-center">
							<figure class="figure">
								<img id='video_cam_9' name='idVeiculo_uuid' class='img-responsive thumbnail img-thumbnail' onerror='this.src="assets/images/ImgFundo4.png"' src='assets/images/ImgFundo4.png'>
								<figcaption id="legenda_video_9" class="figure-caption text-center"><small></small></figcaption>
							</figure>
						</div>
					</div>
				</div>
				<div class="col-md-1"></div>
			</div>
		</div>
		<script src="assets/js/mosaico-videos.js"></script>
	</body>
</html>