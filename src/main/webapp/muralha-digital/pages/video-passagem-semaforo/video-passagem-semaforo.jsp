<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Muralha Digital</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
		<link rel="stylesheet" href="assets/css/veiculo-passagem.css">
		
		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
						
	</head>
	
	<script>
		var urlRoot 	= "${root}";
	</script>

	<body class="homepage">
	
		<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 
	
         <div class="col-auto" hidden> 
            <div id="toggle-disable-id" role="group"  class="btn-group my-1 ml-2">
                <input type="radio" class="btn-check" name="optionsED" id="optionEnable" value="ON" checked>
                <label class="btn btn-outline-secondary" for="optionEnable">Enable</label>                        
                <input type="radio" class="btn-check" name="optionsED" id="optionDisable" value="OFF">
                <label class="btn btn-outline-secondary" for="optionDisable">Disable</label>
            </div>
        </div>

	    <div class="container-fluid mt-3 mb-3">
            <div class="row">
            	<div class="col-md-1"></div>
            	<div class="col-md-3 d-grid">
            		<span class="p-2 bg-secondary text-white text-center rounded"><strong>OPERAÇÃO DO EQUIPAMENTO</strong></span>
            	</div>      	
                <div class="col-md-6 d-flex justify-content-center">
					<select class="selectpicker form-control border" data-live-search="true" id="selEquipamento" name="equipamento" data-style="btn-white" data-size="20" data-none-selected-text="-- Equipamento --">
	                    <option value="0" selected="selected">-- Equipamento --</option>
	                </select>
                </div>
                <div class="col-md-1 d-grid gap-2">
                   <button type="button" class="btn btn-success" id="iniciar" onclick="DivInit_WebSocketInit()">INICIAR</button>
                </div>
                <div class="col-md-1"></div>                    
            </div>
       </div>
			
		<div id="mainDiv" class="container-fluid" hidden>
			<div class="row" >
				<div class="col-md-4 d-flex justify-content-start">
					<div class='thumbnail fundo img-thumbnail fundo'>
						<figure class="figure">
							<img id='video_cam_0' class='thumbnail img-thumbnail img-responsive' src="http://admin:C0ns1lux@189.42.79.131:5000/cgi-bin/mjpg/video.cgi?channel=0&subtype=1">
							<figcaption class="figure-caption text-center"><p><strong id="legenda_video_0" class="text-justify">VIDEO MONITORAMENTO</strong></p></figcaption>
						</figure>
					</div>
				</div>
				<div class="col-md-1">
					<img id='img_semaforo' class='thumbnail img-thumbnail img-responsive' src="assets/images/semaforo/semaforo_desligado.png">
					<figcaption class='figure-caption text-center'><strong id='semaforo_info'>DESLIGADO</strong></figcaption>
				</div>
								
				<div id='local_0' class='col-md-7 d-flex justify-content-end'>
					<div class='thumbnail fundo img-thumbnail fundo'>
						<div class='row'>
							<div class='col-md-6'>
								<div class='control'>
									<figure class='figure'>
										<img  id='imgMaior_0'
											name='idVeiculo_uuid'
											style='max-width: 100%' 
											class=' thumbnail img-thumbnail img-responsive' src='assets/images/consilux_imagem_sem_veiculo.png'
											onclick='window.open(this.src)'
											alt='Clique sobre a imagem para expandir'>
										<strong><div id="placa1" class='figure-caption text-center'>PLACA</div></strong>
									</figure>
								</div>
							</div>
							
							<div class='col-md-6'>
								<div>
									<img id='imgPan_0' name='idVeiculo_' style='max-width: 100%'  class='thumbnail img-thumbnail img-responsive' src='assets/images/consilux_imagem_sem_veiculo.png' onclick='window.open(this.src)'>
									<strong><div id="placa2" class='figure-caption text-center'>PLACA</div></strong>
								</div>
							</div>
						</div>
						<figcaption class='figure-caption text-center'><strong id='infoVeic_1' > Pista: x -- Placa: xxxxxx -- Vel.: xx Km/h </strong></figcaption>
						<figcaption class='figure-caption text-center'><strong id='infoVeic_2' > xxxx </strong></figcaption> 
					</div>			
				</div>
			</div>
		</div>


		<div class="row" >			
			<div class="col-md-7 d-flex justify-content-start"></div>
			<div class="col-md-4 d-flex justify-content-start">				
				<canvas id="canvas_perfil_magnetico"></canvas>		    		
			</div>
			<div class="col-md-1"></div>
		</div>
					
		<br/>	
	    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
	    <script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>      		       
		<script src="/muralha-digital/pages/video-passagem-semaforo/assets/js/video-passagem-semaforo.js"></script>
		<script src="/muralha-digital/pages/video-passagem-semaforo/assets/js/chart_perfil.js"></script>
			
	</body>
	
</html>