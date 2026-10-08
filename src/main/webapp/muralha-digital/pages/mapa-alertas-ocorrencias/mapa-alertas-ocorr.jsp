<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">
  <head>
	  	<title>Mapa de alertas e ocorrÃªncias</title> 	

	  	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	  	<meta http-equiv="x-ua-compatible" content="ie=edge">	  	
	  	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">  
	  	<link rel="stylesheet" href="assets/css/mapa-alertas-ocorr.css">  
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
		
		<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk"></script>	    
  </head>
  
  <body>
  
	 <%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %> 
	    <div style="margin-top: 10px !important; margin-left: 10px !important;">
            <div class="row mb-2">    
            		<h3><span class="badge bg-success w-100" style="width: auto">Alertas e ocorrências em camadas</span></h3>
            	<div class="col-3" style="width: auto">
            	</div>        	
                <div class="col-4">
                    <select class="form-select form-select-md" id="selectTpAlertaOcorr">
					  <option value="0" selected>Tipo de alertas ou ocorrências</option>
					</select>                     
                </div>     
                <div class="col-2">
                    <select class="form-select form-select-md" id="selectTempo">
                      <option value="0" selected>Tempo da consulta</option>
					  <option value="1">48 horas</option>
					  <option value="2">72 horas</option>
					  <option value="3">10 dias</option>
					  <option value="4">30 dias</option>					  
					</select>                     
                </div> 
                
                <div class="col-2">
                    <select class="form-select form-select-md" id="selectTpMapa">
					  <option value="0" selected>Tipo de visualização</option>
					  <option value="1">Terreno</option>
					  <option value="2">Satelite</option>
					  <option value="3">Rodovia</option>
					  <option value="6">Trafego</option>
					  <option value="7">Ciclovia</option>
					</select>                     
                </div>   
                <div class="col-2">
                    <select class="form-select form-select-md" id="selectTpMapa">
					  <option value="0" selected>Tipo de visualização</option>
					  <option value="1">Terreno</option>
					  <option value="2">Satelite</option>
					  <option value="3">Rodovia</option>
					  <option value="6">Trafego</option>
					  <option value="7">Ciclovia</option>
					</select>                     
                </div> 
                <div class="col-1">
                	<button id="btnProcessar" class="btn btn-success" onclick="ProcessarConsulta()">PROCESSAR</button>
                </div>         	                        
            </div>
       		
       </div>	
       
       <div id="map-canvas"></div>	
       
       <div class="container">       
       		<strong><small id="txtFiltrosSelecionados">Filtros Selecionados: </small></strong>
       </div>
       
       <div class="overlay"></div>
	
  </body>
   
  <script src="/muralha-digital/utils/maps-config.js"></script>
  <script src="assets/js/mapa-alertas-ocorr.js"></script>
  
</html>