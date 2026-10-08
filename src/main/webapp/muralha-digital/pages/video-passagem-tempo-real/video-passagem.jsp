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
            		<span class="p-2 bg-secondary text-white text-center rounded"><strong>VÍDEO E PASSAGEM EM TEMPO REAL</strong></span>
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
				<div class="col-md-1"></div>
				<div class="col-md-5 d-flex justify-content-start">
					<div class='thumbnail fundo img-thumbnail fundo'>
						<figure class="figure">
							<img id='video_cam_0' class='thumbnail img-thumbnail img-responsive' onerror='this.src="assets/images/ImgFundo2.png"' src="assets/images/ImgFundo2.png">
							<figcaption class="figure-caption text-center"><p><strong id="legenda_video_0" class="text-justify"></strong></p></figcaption>
						</figure>
					</div>
				</div>
				<div id='local_0' class='col-md-5 d-flex justify-content-end'>
					<div class='thumbnail fundo img-thumbnail fundo'>
						<div class='row'>
							<div class='col-md-9'>
								<div class='control'>
									<figure class='figure'>
										<img  id='imgMaior_0'
											name='idVeiculo_uuid'
											style='max-width: 100%' 
											class=' thumbnail img-thumbnail img-responsive' src='assets/images/consilux_imagem_sem_veiculo.png'
											onclick='window.open(this.src)'
											alt='Clique sobre a imagem para expandir'>
										<figcaption class='figure-caption text-center'><strong id='infoVeic_0' > Pista: x -- Placa: xxxxxx -- Vel.: xx Km/h </strong><small id='infoVeicDt_0' >  -- Data: 01/01/1901 00:00:00</small></figcaption> </br>
										<p><strong id='localDesc_0' class='text-justify' style='font-size:1vw'>Local: 0 </strong></p>
									</figure>
								</div>
							</div>
							
							<div class='col-md-3'>
								<div>
									<img id='imgPan_0' name='idVeiculo_' class='thumbnail img-thumbnail img-responsive' src='assets/images/consilux_imagem_sem_veiculo.png' onclick='window.open(this.src)'>
									<strong><div class='figure-caption text-center'>Panorâmica</div></strong>
									</br>
									
									<img id='imgMenor_0_1' name='idVeiculo_' class='thumbnail img-thumbnail img-responsive' src='assets/images/consilux_imagem_sem_veiculo.png' onclick='window.open(this.src)'>
									<img id='imgMenor_0_2' name='idVeiculo_' class='thumbnail img-thumbnail img-responsive' src='assets/images/consilux_imagem_sem_veiculo.png' onclick='window.open(this.src)'>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-md-1"></div>
			</div>
		</div>
		
		<br/>	
	                
		<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
		<script src="/muralha-digital/pages/video-passagem-tempo-real/assets/js/video-passagem-tempo-real.js"></script>
			
	</body>
	
</html>