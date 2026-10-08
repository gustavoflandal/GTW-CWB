<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">
	<head>
		<title>Mapa de Dispositivos</title> 	

		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta http-equiv="x-ua-compatible" content="ie=edge">
	  	
		<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
		<link rel="stylesheet" href="assets/css/mapa-dispositivos.css">
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />

		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    	<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk"></script>
	</head>
	
	<body>
	
         <div class="col-auto" hidden> 
            <div id="toggle-disable-id" role="group"  class="btn-group my-1 ml-2">
                <input type="radio" class="btn-check" name="optionsED" id="optionEnable" value="ON" checked>
                <label class="btn btn-outline-secondary" for="optionEnable">Enable</label>                        
                <input type="radio" class="btn-check" name="optionsED" id="optionDisable" value="OFF">
                <label class="btn btn-outline-secondary" for="optionDisable">Disable</label>
            </div>
        </div>
        
		<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %>
		<%@ include file="/muralha-digital/pages/mapa-equipamento/popover-legenda-mapa.jsp" %>
		
<div class="container-fluid">
	<div class="row align-items-center pt-1 pb-2 g-2">
		<div class="col-md-12 d-grid">
			<span class="p-1 badge bg-secondary text-white text-center rounded">
				<p class="h5 pt-1 mb-0"><strong>MAPA DE DISPOSITIVOS (PCL)</strong></p>
			</span>
		</div>

		<div class="col-md-8">
			<select id="selEquipamento" class="selectpicker form-control border"
					data-live-search="true" multiple data-style="btn-white" data-size="20"
					data-none-selected-text="Todos os equipamentos"
					data-actions-box="true" data-select-all-text="Marcar todos"
					data-deselect-all-text="Desmarcar todos"></select>
		</div>

		<div class="col-md-2">
			<select id="selectTypeMap" onChange="atualizaMapa()"
					class="selectpicker form-control border"
					data-style="btn-white" data-none-selected-text="Transito">
				<option value="noMarking">Sem marcação (mapa)</option>
				<option value="roadNetwork">Malha viaria</option>
				<option value="busStation">Rodoviaria</option>
			</select>
		</div>

		<div class="col-md-2 d-grid">
			<div class="btn-group">
				<button type="button" class="btn btn-primary" onclick="ObterDadosDispositivosEquipamentos()">PESQUISAR</button>
				<button id="popoverLegenda" type="button" class="btn btn-info"
						data-bs-placement="bottom" data-bs-toggle="popover">LEGENDA</button>
			</div>
		</div>
	</div>
</div>

		
    	<div id="map-canvas"></div>
		<div class="overlay"></div>
	</body>
    
	<script src="/muralha-digital/utils/maps-config.js"></script>
   	<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.6.2/chart.min.js"></script>
 	<script src="https://cdnjs.cloudflare.com/ajax/libs/chartjs-plugin-datalabels/2.0.0/chartjs-plugin-datalabels.min.js"></script>
	<script src="/muralha-digital/pages/relatorios/js/grafico-utilidades.js"></script>
	<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
	<script src="assets/js/mapa-dispositivos.js"></script>
</html>