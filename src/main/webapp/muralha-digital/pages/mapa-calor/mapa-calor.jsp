<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>GTW - Mapa de Calor</title>


    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
    <link rel="stylesheet" href="assets/css/mapaCalor.css">
</head>
<body>
    <div class="container-fluid mt-3 ms-3">
        <div class="row align-items-center">
            <div class="col-auto">
                <h3><span class="badge bg-success">Mapa de Calor</span></h3>
            </div>
            <div class="col-4">
                <select class="form-select form-select-md" id="selectTpAlertaOcorr">
                    <option value="0" selected>Fluxo -- Fluxo normal de veiculos</option>
                </select>
            </div>
            <div class="col-md-2 col-sm-12">
                <select class="form-select form-select-md" id="selectTempo" aria-label="Tempo da consulta">
                    <option value="0" selected>Tempo da consulta</option>
                    <option value="1">24 horas</option>
                    <option value="2">48 horas</option>
                    <option value="3">72 horas</option>
                </select>
            </div>
            <div class="col-auto">
                <button id="btnProcessar" class="btn btn-success" onclick="ProcessarConsulta()">PROCESSAR</button>
            </div>
        </div>

        <div id="aviso-sem-ocorrencias" class="alert alert-warning mt-3" role="alert" style="display: none;">
            Não há ocorrências ou alertas para os filtros selecionados.
        </div>
    </div>

    <div id="map-wrapper" class="mt-3 mx-3">
        <div class="input-overlay">
            <label for="heatmap">Densidade do Mapa de Calor</label>
            <input class="custom-range" id="heatmap" type="range" min="20" max="100" value="60" onchange="inputRangeOpacity(this)">
        </div>
    </div>
    <div id="map-canvas" class="mt-3 mx-3">

    </div>

    <script src="/muralha-digital/utils/maps-config.js"></script>
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDQi61F3m8zpsCy-opaJxYr2MlpXxzM0Kk&libraries=visualization&callback=initMap"></script>
    <script src="assets/js/mapaCalor.js"></script>

</body>
</html>