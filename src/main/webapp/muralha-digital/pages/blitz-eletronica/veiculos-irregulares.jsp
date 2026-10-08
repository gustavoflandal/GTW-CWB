<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>
<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 


<html lang="en">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Muralha Digital</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<link rel="stylesheet" href="/muralha-digital/pages/blitz-eletronica/assets/css/veiculos-irregulares.css">
		
		<%@ include file="../consulta-veiculo/modal-detalhe-veiculo.jsp" %>
		<%@ include file="/muralha-digital/pages/consulta-alerta-ocorrencia/modal-alertas-veiculo.jsp" %>
	</head>
	
	<script>
		var urlRoot = "${root}";
	</script>

	<body class="homepage">
	
         <div class="col-auto" hidden> 
            <div id="toggle-disable-id" role="group"  class="btn-group my-1 ml-2">
                <input type="radio" class="btn-check" name="optionsED" id="optionEnable" value="ON" checked>
                <label class="btn btn-outline-secondary" for="optionEnable">Enable</label>                        
                <input type="radio" class="btn-check" name="optionsED" id="optionDisable" value="OFF">
                <label class="btn btn-outline-secondary" for="optionDisable">Disable</label>
            </div>
        </div>

	    <div class="container-fluid">
            <div class="row pt-1">
            	<div class="col-md-12 d-grid">
            		<span class="p-1 badge bg-secondary text-white text-center rounded"><p class="h5 pt-1"><strong>BLITZ ELETRÔNICA - VEÍCULOS IRREGULARES</strong></p></span>
            	</div>
			</div>
			<div class="row pt-1 mb-3">            	        	
                <div class="col-md-9 d-grid">
                    <div id="bsMultiSelectJson" class="input-group">
                    	<button type="button" class="btn btn-outline-success" onclick="MarcarTodos()" title="Marcar todos">
			                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-check-all" viewBox="0 0 16 16">
							  	<path d="M8.97 4.97a.75.75 0 0 1 1.07 1.05l-3.99 4.99a.75.75 0 0 1-1.08.02L2.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093L8.95 4.992a.252.252 0 0 1 .02-.022zm-.92 5.14.92.92a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 1 0-1.091-1.028L9.477 9.417l-.485-.486-.943 1.179z"></path>
							</svg>
              			</button>
                    	<button type="button" class="btn btn-outline-danger" onclick="DesmarcarTodos()" title="Desmarcar todos">
			                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-x-lg" viewBox="0 0 16 16">
							  	<path d="M2.146 2.854a.5.5 0 1 1 .708-.708L8 7.293l5.146-5.147a.5.5 0 0 1 .708.708L8.707 8l5.147 5.146a.5.5 0 0 1-.708.708L8 8.707l-5.146 5.147a.5.5 0 0 1-.708-.708L7.293 8 2.146 2.854Z"></path>
							</svg>
		              	</button>
                    </div>                        
                </div>
                <div class="col-md-1 d-grid">
                   <button type="button" class="btn btn-primary" title="Configurações de monitoramento de Ocorrências" id="configMonitoramento" onclick="AbrirConfiguracaoMonitoramento()"><small>CONFIGURAÇÕES</small></button>
                </div>
                <div class="col-md-1 d-grid">
                   <button type="button" class="btn btn-warning" title="Cadastro de veículos monitorados" id="configVeicMonitorado" onclick="AbrirCadVeiculosMonitorados()"><small>MONITORADOS</small></button>
                </div>
                <div class="col-md-1 d-grid">
                   <button type="button" class="btn btn-success" id="iniciar" onclick="DivInit_WebSocketInit()">INICIAR</button>
                </div>
            </div>
       </div>
			
		<div class="container-fluid ">
			<div id="locais_1" class="row d-flex justify-content-center">
				<c:forEach var="i" begin="1" end="8">
					<div id='local_${i}' class='col-md-3 col-container pb-2 div-img-blitz'>
<!-- 						<div class='thumbnail img-thumbnail bg-light-custom col'> -->
						<div class='thumbnail img-thumbnail col'>
							<div class='row'>
								<div class='col-md-12'>
									<div class='control'>
										<figure class='figure mb-1'>
										  	<img id='imgMaior_${i}'
										         name='idVeiculo_uuid'
										         alt=''
										         style='max-width: 100%'
										         class='thumbnail img-thumbnail img-responsive' src='/muralha-digital/assets/images/consilux_imagem_sem_veiculo.png'
										         onclick='abrir_alertas_veic(this)'>
											<figcaption class='figure-caption text-center'><p id="infoVeic_${i}"><strong>Placa: </strong>XXXXXXX<strong> - Vel.: </strong>XX Km/h<strong> - Data: </strong><small>01/01/1901 00:00:00</small><strong> - Faixa: </strong>X </p></figcaption>
										</figure>
										<ul id="infoAdicional_${i}" class="list-unstyled">
  											<li><strong id='tipoAlerta_${i}' class="text-uppercase"  style='font-size:0.6vw'>Irregularidade(s)</strong></li>
  											<li><strong id='localDesc_${i}' style='font-size:0.8vw'>Endereço</strong></li>
  										</ul>
									</div>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
		
	    <script src="/muralha-digital/pages/blitz-eletronica/assets/js/BsMultiSelect.min.js"></script>
		<script src="/muralha-digital/pages/blitz-eletronica/assets/js/tempo-real-multi-select.js"></script>
		<script src="/muralha-digital/pages/blitz-eletronica/assets/js/veiculos-irregulares.js"></script>
	</body>
</html>