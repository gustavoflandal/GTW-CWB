<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>
<%@ include file="/muralha-digital/pages/linha-tempo/modal-linha-tempo.jsp" %>
<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %>
<%@ include file="/muralha-digital/pages/mapa-passagens/modal-visualizar-mapa.jsp" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>

<html lang="pt-br">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Perfil Comportamental do Veículo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <link rel="stylesheet" href="assets/css/style.css" />
</head>

<body>
    <div aria-live="polite" aria-atomic="true" class="position-relative">
        <div id="toast-container" class="toast-container position-absolute top-0 end-0 p-3" style="z-index: 1100;"></div>
    </div>

    <div class="container"> 
        
 <header>
            <h1>PERFIL COMPORTAMENTAL DO VEÍCULO</h1>
        </header>

        <section class="filters-section">
		    <div class="d-flex flex-wrap">
		        <div class="col-md-4">
		            <label for="data-inicial" class="form-label fw-bold">Data Inicial</label>
		            <div class="input-group mb-2">
		                <input type="date" id="data-inicial" class="form-control">
		    
              <input type="time" id="hora-inicial" class="form-control">
		            </div>
		            <div class="d-flex gap-2">
		                <button id="btn-limpar" class="btn btn-warning">Limpar</button>
		                <button id="btn-analisar" class="btn btn-primary">Analisar</button>
		            </div>
		        </div>
		
		 
         <div class="col-md-3">
		            <label for="data-final" class="form-label fw-bold">Data Final</label>
		            <div class="input-group">
		                <input type="date" id="data-final" class="form-control">
		                <input type="time" id="hora-final" class="form-control">
		            </div>
		        </div>
		
		     
     <div class="col-md-2">
		            <label for="intervalo" class="form-label fw-bold">Intervalo</label>
		            <select id="intervalo" class="form-select">
		                <option value="">Selecionar...</option>
		                <option value="7">Últimos 7 dias</option>
		                <option value="30">Últimos 30 dias</option>
		            
      <option value="365">Últimos 12 meses</option>
		            </select>
		        </div>
		
		        <div class="col-md-2">
		            <label for="placa" class="form-label fw-bold">Placa</label>
		            <input type="text" id="placa" class="form-control" autocomplete="off" placeholder="Digite a placa" maxlength="7">
		        </div>
		    </div>
		</section>

        <div id="message-container" class="message-container hidden">
      
        <p></p>
        </div>

        <main id="main-content" class="hidden">
            <div class="top-section">
                <div class="card vehicle-info">
			        <h3>Informações do Veículo</h3>
			        <div class="vehicle-info-body">
			            <div class="vehicle-image-container">
			             
     <img alt="Foto do Veículo">
			            </div>
			            <div class="vehicle-details-container">
			                <p><strong>PLACA:</strong> <span id="placa-veiculo"></span></p>
			                <p><strong>MARCA:</strong> <span id="marca-veiculo"></span></p>
			                <p><strong>MODELO:</strong> <span id="modelo-veiculo"></span></p>
			               
   <p><strong>COR:</strong> <span id="cor-veiculo"></span></p>
			                <p><strong>ANO:</strong> <span id="ano-veiculo"></span></p>
			            </div>
			        </div>
			        <div class="vehicle-info-buttons">
				        <button id="btnAbrirLinhaTempo" type="button" class="btn btn-secondary vehicle-info-button" data-bs-toggle="modal" data-bs-target="#linhaTempo" title="Ver linha do tempo do veículo" >
				            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-clock-history" viewBox="0 0 16 16"><path d="M8.515 1.019A7 7 0 0 
 0 8 1V0a8 8 0 0 1 .589.022zm2.004.45a7 7 0 0 0-.985-.299l.219-.976q.576.129 1.126.342zm1.37.71a7 7 0 0 0-.439-.27l.493-.87a8 8 0 0 1 .979.654l-.615.789a7 7 0 0 0-.418-.302zm1.834 1.79a7 7 0 0 0-.653-.796l.724-.69q.406.429.747.91zm.744 1.352a7 7 0 0 0-.214-.468l.893-.45a8 8 0 0 1 .45 1.088l-.95.313a7 7 0 0 0-.179-.483m.53 2.507a7 7 0 0 0-.1-1.025l.985-.17q.1.58.116 1.17zm-.131 1.538q.05-.254.081-.51l.993.123a8 8 0 0 1-.23 1.155l-.964-.267q.069-.247.12-.501m-.952 2.379q.276-.436.486-.908l.914.405q-.24.54-.555 1.038zm-.964 1.205q.183-.183.35-.378l.758.653a8 8 0 0 1-.401.432z"/><path d="M8 1a7 7 0 1 0 4.95 11.95l.707.707A8.001 8.001 0 1 1 8 0z"/><path d="M7.5 3a.5.5 0 0 1 .5.5v5.21l3.248 1.856a.5.5 0 0 1-.496.868l-3.5-2A.5.5 0 0 1 7 9V3.5a.5.5 0 0 1 .5-.5"/></svg>
				   
       </button>
				        <button id="btnMostrarMapa" type="button" class="btn btn-info vehicle-info-button" title="Ver no mapa">
				            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-map" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M15.817.113A.5.5 0 0 1 16 .5v14a.5.5 0 0 1-.402.49l-5 1a.502.502 0 0 1-.196 0L5.5 15.01l-4.902.98A.5.5 0 0 1 0 15.5v-14a.5.5 0 0 1 .402-.49l5-1a.5.5 0 0 1 .196 0L10.5.99l4.902-.98a.5.5 0 0 1 .415.103zM10 1.91l-4-.8v12.98l4 .8V1.91zm1 12.98 4-.8V1.11l-4 .8v12.98zm-6-.8V1.11l-4 .8v12.98l4-.8z"></path></svg>
				        </button>
				        <button 
 id="btnManchaMonitorada" type="button" class="btn btn-success vehicle-info-button" data-bs-toggle="modal" data-bs-target="#modalManchaMonitorada" title="Ver Mancha Monitorada">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bullseye" viewBox="0 0 16 16"><path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/><path d="M8 13A5 5 0 1 1 8 3a5 5 0 0 1 0 10zm0 1A6 6 0 1 0 8 2a6 6 0 0 0 
 0 12z"/><path d="M8 11a3 3 0 1 1 0-6 3 3 0 0 1 0 6zm0 1a4 4 0 1 0 0-8 4 4 0 0 0 0 8z"/><path d="M9.5 8a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z"/></svg>
                        </button>
				    </div>
			    </div>
            </div>

            <div class="card 
 full-width-card">
                <h3>Filtro por Mancha / Permanência</h3>
                <div style="max-width: 400px;"> 
                    <select id="filtro-mancha-geral" class="form-select d-none"></select>
                </div>
            </div>

          
    <div class="dashboard">
                <div class="card" style="flex: 3;">
                    <h3>Passagens por Dia da Semana</h3>
                    <div class="chart-container">
                        <canvas id="passagens-semana-chart"></canvas>
        
              </div>
                </div>
                <div class="card dwell-time-display" style="flex: 1;">
                    <h3>Tempo de Permanência</h3>
                    <p class="time" id="tempo-permanencia-display">-</p> 
       
           </div>
            </div>

            <div class="card full-width-card">
                <h3>Mapa de Calor</h3>
                <div id="map-canvas" style="height: 400px;"></div>
            </div>

            <div class="card full-width-card">
  
                <h3>Gráfico de Calor - Probabilidade de Presença</h3>
                <div class="heatmap-wrapper">
                    <div id="probabilidade-heatmap-container"></div>
                    <div id="heatmap-legend-container"></div>
                </div>
     
         </div>

            <div class="card full-width-card">
                <h3>Passagens por PCL</h3>
                <div class="table-container">
                    <table class="pcl-table">
                       
   <thead>
                            <tr>
                                <th>Local</th>
                                <th>Número de Passagens</th>
     
                         </tr>
                        </thead>
                        <tbody id="pcl-table-body"></tbody>
                    </table>
        
          </div>
            </div>
        </main>
    </div>
    
    <div class="modal fade" id="modalManchaMonitorada" tabindex="-1" aria-labelledby="modalManchaMonitoradaLabel" aria-hidden="true">
	    <div class="modal-dialog" id="manchaMonitoradaDialog">
	        <div class="modal-content">
	            <div class="modal-header">
	                <h5 class="modal-title" id="modalManchaMonitoradaLabel">Mancha Monitorada</h5>
	       
           <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	            </div>
	            <div class="modal-body p-0">
	                <iframe src="/muralha-digital/pages/mancha-monitorada/mancha-monitorada.jsp" style="width: 100%;
 height: 85vh; border: none;"></iframe>
	            </div>
	        </div>
	    </div>
	</div>
</body>

<script src="assets/js/client.js"></script>
<script src="assets/js/utils.js"></script>
<script src="assets/js/script.js"></script>
<script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk&libraries=visualization&callback=initMapGlobal"></script>

</html>

<%@ include file="/includes/rodape.jsp" %>