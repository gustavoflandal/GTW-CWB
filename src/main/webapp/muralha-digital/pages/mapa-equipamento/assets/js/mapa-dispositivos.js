var TEMPO_ATUALIZAR_PAGINA = 900;
var TEMPORIZADOR = 0;
var ELEMENTO_GRAFICO_HORARIO_DISPOSITIVO = 'chartGraficoFluxoPorHorario_';
var ELEMENTO_GRAFICO_MINUTO_DISPOSITIVO = 'chartGraficoFluxoPorMinuto_';
var ELEMENTO_GRAFICO_INFRACAO_DIA_DISPOSITIVO = 'chartGraficoInfracaoPorDia_';
//var ACAO_CONSULTA_MAPA = "obterDispositivosEquipamentos";
//var ACAO_CONSULTA_MAPA = "obterDispositivosEquipamentosPorCategoria";
//var ACAO_CONSULTA_MAPA = "obterDispositivosEquipamentosSimplificado";
var ACAO_CONSULTA_MAPA = "obterDispositivosEquipamentosMisto";

var markerImg_grey_new = new google.maps.MarkerImage('assets/images/iconesTransparentes/radar_grey_light_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30));
        
var markerImg_red_new = new google.maps.MarkerImage('assets/images/iconesTransparentes/radar_red_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30));

var markerImg_green_new = new google.maps.MarkerImage('assets/images/iconesTransparentes/radar_green_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30));   	

var markerImg_yellow_new = new google.maps.MarkerImage('assets/images/iconesTransparentes/radar_amarelo_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30)); 

var markerImg_alerta1 = new google.maps.MarkerImage('assets/images/iconesTransparentes/alerta_vermelho.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30)); 
        
var markerImg_alerta2 = new google.maps.MarkerImage('assets/images/iconesTransparentes/alerta_azul.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30)); 


var popoverLegenda;
var dispositivos 	= [];
var markersArray 	= [];
var mapa_google 	= null;
const markersEquipamentos = {};
transitLayer = new google.maps.TransitLayer(); 
traficLayer = new google.maps.TrafficLayer();

function MapaTransito() 	{ traficLayer.setMap(null); transitLayer.setMap(mapa_google);		}
function MapaTrafego() 		{ transitLayer.setMap(null); traficLayer.setMap(mapa_google);		}

function atualizaMapa(){
	let tipo = $("#selectTypeMap").val()
	
	switch (tipo) {
		case "noMarking":
			return traficLayer?.setMap(null), transitLayer?.setMap(null)
		case "roadNetwork":
			return MapaTransito()
		case "busStation":
			return MapaTrafego()
	}
}
  
$(document).ready(function() {
    console.log('Iniciando tela de mapa de dispositivos e equipamentos (v2)');

    var opcoesMapa = {
        zoom: ZOOM_PADRAO,
        center: LatLngPadrao
    };
    mapa_google = new google.maps.Map(document.getElementById("map-canvas"), opcoesMapa);

    try {
        var funcoes = [
            ObterEquipamentosCombo,
            ObterDadosDispositivosEquipamentos,
            InicializarPopover,
            IniciarTemporizador,
            AplicarEstiloBotoesBootstrapSelect
        ];

        var d = $.Deferred().resolve();
        while (funcoes.length > 0) {
            d = d.then(funcoes.shift());
        }
    } catch (ex) {
        AlertCsx_E_TimeOut_8000ms('Erro ao carregar dados dos dispositivos!!');
        RemoveLoading();
    }

    setTimeout(function() {
        if (window.socketAlertas) {
            const originalHandler = socketAlertas.onmessage;
            socketAlertas.onmessage = function(event) {
                console.log("Recebi alerta via WebSocket:", event.data);
                processarAlerta(event.data);
                if (originalHandler) originalHandler(event);
            };
        }
    }, 1000);

    document.addEventListener("novoAlerta", function(event) {
        processarAlerta(event.detail);
    });
});

function processarAlerta(xmlString) {
    try {
        const xmlDoc = $.parseXML(xmlString);
        const $xml = $(xmlDoc);

        $xml.find("alerta").each(function() {
            const $alerta = $(this);
            const equipamentoFull = $alerta.find("equipamento").text();
            const equipamentoCodigo = equipamentoFull.split("-")[0].trim();

            console.log("Alerta recebido para equipamento:", equipamentoCodigo);

            const marker = markersArray.find(m => m.serieEquipamento === equipamentoCodigo);
            if (marker) {
                console.log("Fazendo o marker piscar:", equipamentoCodigo);
                piscarMarker(marker, 10000, 500); 
            }
        });
    } catch (e) {
        console.error("Erro ao processar alerta XML:", e);
    }
}

function piscarMarker(marker, duracao = 10000, intervalo = 500) {
    if (!marker) return;

    const corOriginal = marker.getIcon(); 
    const imagensAlerta = [markerImg_alerta1, markerImg_alerta2];
    let indice = 0;
    let contador = 0;
    const maxTicks = Math.floor(duracao / intervalo);

    const blinkInterval = setInterval(() => {
        if (!marker.getMap()) {
            clearInterval(blinkInterval);
            return;
        }

        marker.setIcon(imagensAlerta[indice]);
        indice = (indice + 1) % imagensAlerta.length;
        contador++;

        if (contador >= maxTicks) {
            clearInterval(blinkInterval);
            marker.setIcon(corOriginal);
        }
    }, intervalo);
}

function ObterEquipamentosCombo()
{
	obterEquipamentosGenerico("selEquipamento");
}

function ObterDadosDispositivosEquipamentos()
{
	var categoria = $('#categoria').val();
	var equipamentos = $('#selEquipamento').val();
	var dataString = "acao="+ACAO_CONSULTA_MAPA+
						"&equipamentos="+equipamentos;
						
	categoria != null ? dataString += '&categoria=' + categoria : null
	
	DeleteMarkers();
	AddLoading();
	$.ajax(
    {
        type: 		"GET",
        url: 		"/MuralhaDigital/MapaDispositivosEquipamentos",
		data: 		dataString,
        dataType:	"xml",
        success: 	function( data, textStatus, jqXHR) 
        {
        	processaDados( jqXHR.responseText );   	
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
			WarningCsx_E_TimeOut_8000ms("Ocorreu um ao obter dispositivos para o mapa!");
        	console.log("(obterDadosRadares) Erro ao processar requisição ao servidor)!!");
        	RemoveLoading();
        },
        beforeSend: function(jqXHR, settings){},
        complete: function(jqXHR, textStatus)
        {
			RemoveLoading();
		}
    });    			
}

function processaDados(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var i = 0;

	var lista = $xml.find('DispositivosEquipamentos').find('ListaDispositivos');

	lista.find('Dispositivo').each(function()
	{
		var $dispositivo = $(this);
		var cameras = [];
		
		var idLocal 				= $dispositivo.find('idDispositivo').text();
		var serieEquipamento		= $dispositivo.find('serieEquipamento').text();
		var descLocal 				= $dispositivo.find('descDispositivo').text();
		var latitude 				= $dispositivo.find('latitude').text();
		var longitude				= $dispositivo.find('longitude').text();
		var codOrgao				= ( ($dispositivo.find('codigosEquipamentos').text() == null || $dispositivo.find('codigosEquipamentos').text() == '') ? "N/D" : $dispositivo.find('codigosEquipamentos').text() );
		var conectado				= $dispositivo.find('conectado').text();
		var statusTrafego			= $dispositivo.find('statusTrafego').text();
		var passagensDiasRecentes 	= $dispositivo.find('passagensDiasRecentes').text();
		var passagensUltimaHora 	= $dispositivo.find('passagensUltimaHora').text();
		var passagensUltimos15Min 	= $dispositivo.find('passagensUltimos15Min').text();
		var velMediaUltimos15Min 	= $dispositivo.find('velMediaUltimos15Min').text();
		var infracoesRegistradas 	= $dispositivo.find('infracoesRegistradas').text();
		var jsonFluxoVelMediaDiario	= $dispositivo.find('jsonFluxoVelMediaDiario').text();
	    $dispositivo.find('cameras').each(function() {
	        var $camera = $(this);
	        cameras.push({
	            idCamera: $camera.find('idCamera').text(),
	            idLocal: $camera.find('idLocal').text(),
	            ipCamera: $camera.find('ipCamera').text().trim(),
	            relevante: $camera.find('relevante').text(),
	            tipoCamera: $camera.find('tipoCamera').text()
	        });
	    });
	    
		var dispositivo = {
							idDispositivo: idLocal,
							serieEquipamento: serieEquipamento,
							descDispositivo: descLocal, 
							latitude: latitude, 
							longitude:longitude,
							codOrgao: codOrgao,
							conectado:conectado,
							statusTrafego:statusTrafego,
							passagensDiasRecentes: passagensDiasRecentes,
							passagensUltimaHora: passagensUltimaHora,
							passagensUltimos15Min: passagensUltimos15Min,
							velMediaUltimos15Min: velMediaUltimos15Min,
							infracoesRegistradas: infracoesRegistradas,
							jsonFluxoVelMediaDiario: jsonFluxoVelMediaDiario,
							cameras: cameras
							};
		
		dispositivos[i] = dispositivo;
		i++;
				
	});
	
//	console.log(dispositivos);	
	
	PopulaMarkers();
}	

function IdentificaCorMarker(dispositivo)
{
	if(((/false/).test(dispositivo.conectado))) return markerImg_grey_new;
	if(dispositivo.statusTrafego == 3) return markerImg_red_new;
	if(dispositivo.statusTrafego == 2) return markerImg_yellow_new;
	if(dispositivo.statusTrafego == 1) return markerImg_green_new;
	
	return markerImg_grey_new;
}

function PopulaMarkers()
{
	var marker, markerImg, i;
	
	for (i = 0; i < dispositivos.length; i++) 
	{
//		console.log('Marker:: ' + dispositivos[i].serieEquipamento + ' lat: ' + dispositivos[i].latitude + ' long: ' + dispositivos[i].longitude);
  			 			
		markerImg = IdentificaCorMarker(dispositivos[i]);
		
		marker = new google.maps.Marker(
    	{
      		position:	 				new google.maps.LatLng(dispositivos[i].latitude, dispositivos[i].longitude),
      		map: 						mapa_google,
      		icon: 						markerImg,
      		infowindow: 				new google.maps.InfoWindow({ content: ''  }),
      		idDispositivo:				dispositivos[i].idDispositivo,
      		serieEquipamento:			dispositivos[i].serieEquipamento,
      		descDispositivo: 			dispositivos[i].descDispositivo,
      		codOrgao: 					dispositivos[i].codOrgao,
      		conectado: 					dispositivos[i].conectado,
      		statusTrafego:				dispositivos[i].statusTrafego,
			passagensDiasRecentes: 		dispositivos[i].passagensDiasRecentes,
			passagensUltimaHora:		dispositivos[i].passagensUltimaHora,
			passagensUltimos15Min:		dispositivos[i].passagensUltimos15Min,
			velMediaUltimos15Min:		dispositivos[i].velMediaUltimos15Min,
			infracoesRegistradas:		dispositivos[i].infracoesRegistradas,
			jsonFluxoVelMedia:			dispositivos[i].jsonFluxoVelMediaDiario,
			cameras: 					dispositivos[i].cameras
    	});
    	
    	markersArray.push(marker);

    	/////////////////////////////////////////////////////////////////////////////////////
    	/////////////////////////////////////////////////////////////////////////////////////	    
    	// 1) Faz a criação dos campos escondidos
    	// 2) Criar componente collapse para exibir tabela com fluxo e velocidade média do dia, por hora
    	// 3) Quando o usuário clica sobre o marker, então é copiado as informações destes campos escondidos para o infowindow do marker
  		            		 	
		createDivEnquadramentosTemporario (dispositivos[i].idDispositivo, dispositivos[i].descLocal);
		
        google.maps.event.addListener(marker, 'click', function() 
        {
			switch (ACAO_CONSULTA_MAPA)
			{
				case 'obterDispositivosEquipamentos':
					var dados = CriarListenerMarkersInfo(this.idDispositivo, this.serieEquipamento, this.descDispositivo, this.codOrgao,
															this.passagensUltimos15Min, this.velMediaUltimos15Min, this.infracoesRegistradas, this.jsonFluxoVelMedia);
			    	break;
			  	case 'obterDispositivosEquipamentosSimplificado':
					GerenciarGraficosMapa(this.idDispositivo);
					var dados = CriarListenerMarkersInfoGrafico(this.idDispositivo, this.serieEquipamento, this.descDispositivo, this.codOrgao);
			    	break;
			  	case 'obterDispositivosEquipamentosMisto':
					GerenciarGraficosMapa(this.idDispositivo);
					var dados = CriarListenerMarkersInfoMisto(this.idDispositivo, this.serieEquipamento, this.descDispositivo, this.codOrgao,
																this.passagensUltimos15Min, this.velMediaUltimos15Min, this.infracoesRegistradas, this.cameras, this.conectado);
			    	break;
			  	case 'obterDispositivosEquipamentosPorCategoria':
					GerenciarGraficosMapa(this.idDispositivo);
					var dados = CriarListenerMarkersInfoMisto(this.idDispositivo, this.serieEquipamento, this.descDispositivo, this.codOrgao,
																this.passagensUltimos15Min, this.velMediaUltimos15Min, this.infracoesRegistradas, this.cameras);
			    	break;
			  default:
			    	console.log(`A ação ${ACAO_CONSULTA_MAPA} não foi encontrada!.`);
			}

			this.infowindow.setContent(dados);
        	this.infowindow.open(mapa_google, this);
         });
	}
}

function createDivEnquadramentosTemporario(idLocal, info)
{	
    var dynDiv = document.createElement("div");
    dynDiv.id = 'enquadramentosTemp_'+ idLocal;
    dynDiv.innerHTML = info;
    dynDiv.style.display = "none";
    document.body.appendChild(dynDiv);	    	
}


function CriarListenerMarkersInfo(idDispositivo, serieEquipamento, descDispositivo, codOrgao, passagensUltimos15Min, velMediaUltimos15Min, infracoesRegistradas, jsonFluxoVelMedia)
{
	var dados = '';
	var divFluxoVelMediaDiario = CriarDivFluxoVelMediaDiario(idDispositivo, jsonFluxoVelMedia);
	
 	var dados = 		'<div id="idDispositivo_' + idDispositivo + '">'+
						'	<div class="mt-1 mb-2"><strong id="descDispositivo_' + idDispositivo + '">' + codOrgao + ' - ' + serieEquipamento + ' - ' + descDispositivo + '</strong></div>' +
							divFluxoVelMediaDiario +
						'	<div id="bodyContentDispositivo_' + idDispositivo + '">'+
						'  		<ul id="listInline15MinDispositivo_'+ idDispositivo + '" class="list-inline mt-0 mb-0">' +
						'  			<li id="itemInline15Min1Dispositivo_'+ idDispositivo + '" class="list-inline-item">Últimos 15 min.:</li>' +
						'  			<li id="itemInline15Min2Dispositivo_'+ idDispositivo + '" class="list-inline-item"><strong>' + passagensUltimos15Min + '</strong> veículos</li>' +
						'  			<li id="itemInline15Min3Dispositivo_'+ idDispositivo + '" class="list-inline-item">|</li>' +
						'  			<li id="itemInline15Min4Dispositivo_'+ idDispositivo + '" class="list-inline-item">vel. média <strong>' + velMediaUltimos15Min + '</strong> km/h</li>' +
						'  		</ul>' +
						'  		<div id="infracoes_registradas_'+ idDispositivo + '">Infrações registradas: <strong>' + infracoesRegistradas + '</strong></div>' +
						'	</div>' +
						'</div>';

 	return dados;	
}



function CriarListenerMarkersInfoGrafico(idDispositivo, serieEquipamento, descDispositivo, codOrgao)
{
	CHART_MAPAS_CARREGADO.forEach
	
	var dados = '';
	var divGraficoFluxoVelMediaPorHorario = CriarDivGraficoFluxoVelMediaPorHorario(idDispositivo);
	var divGraficoFluxoVelMediaPorMinuto = CriarDivGraficoFluxoVelMediaPorMinuto(idDispositivo);
	var divGraficoInfracaoPorDia = CriarDivGraficoInfracaoPorDia(idDispositivo);
	
 	var dados = '<div id="idDispositivo_' + idDispositivo + '">'+
				'	<div class="mt-1 mb-2"><strong id="descDispositivo_' + idDispositivo + '">' + codOrgao + ' - ' + serieEquipamento + ' - ' + descDispositivo + '</strong></div>' +
					divGraficoFluxoVelMediaPorHorario +
					divGraficoFluxoVelMediaPorMinuto +
					divGraficoInfracaoPorDia +
				'</div>';

 	return dados;	
}

function CriarListenerMarkersInfoMisto(idDispositivo, serieEquipamento, descDispositivo, codOrgao,
                                       passagensUltimos15Min, velMediaUltimos15Min, infracoesRegistradas, cameras, status) {
    var divGraficoFluxoVelMediaPorHorario;
    try {
        divGraficoFluxoVelMediaPorHorario = CriarDivGraficoFluxoVelMediaPorHorario(idDispositivo);
    } catch (e) {
        console.error("Erro ao criar grafico em CriarDivGraficoFluxoVelMediaPorHorario:", e);
        divGraficoFluxoVelMediaPorHorario = "<div style='color:red'>Erro ao carregar gráfico</div>";
    }

    let htmlCameras = "";
    if (cameras && cameras.length > 0) {
        htmlCameras = "<ul>";
        cameras.forEach(function(cam) {
            htmlCameras += "<li>" +
                "<strong>" + (cam.tipoCamera?.trim() || "Câmera simples") + "</strong> - " +
                (status == "true" ? "Em operação" : "Inativa") +
                " (" + cam.ipCamera.trim() + ")" +
                "</li>";
        });
        htmlCameras += "</ul>";
    } else {
        htmlCameras = "";
    }

    var dados = `
        <div id="idDispositivo_${idDispositivo}">
            <div class="mt-1 mb-2">
                <strong>${codOrgao} - ${serieEquipamento} - ${descDispositivo}</strong>
            </div>
            ${divGraficoFluxoVelMediaPorHorario}
            <div id="bodyContentDispositivo_${idDispositivo}">
                <ul class="list-inline mt-0 mb-0">
                    <li class="list-inline-item">Últimos 15 min.:</li>
                    <li class="list-inline-item"><strong>${passagensUltimos15Min}</strong> veículos</li>
                    <li class="list-inline-item">|</li>
                    <li class="list-inline-item">vel. média <strong>${velMediaUltimos15Min}</strong> km/h</li>
                </ul>
                <div>Infrações registradas: <strong>${infracoesRegistradas}</strong></div>
                <div class="mt-2">
                    ${htmlCameras != "" ? "<strong>Câmeras:</strong>" : ""}
                    ${htmlCameras}
                </div>
            </div>
        </div>
    `;


    $("#cameras_dispositivo_" + idDispositivo).html(htmlCameras);

    return dados;
}

function CriarDivFluxoVelMediaDiario(idLocal, jsonFluxoVelMedia)
{
	var data = new Date();
	
	const jsonFluxoVelMediaDiario = JSON.parse(jsonFluxoVelMedia);
	
	var divFluxoVelMediaDiario =
	`<div class="mb-2">
		<p id="pDispositivo_`+ idLocal +`" class="d-grid gap-2 mb-2">
			<button id="btnCollapseDispositivo_`+ idLocal +`" class="btn btn-sm btn-primary" type="button" data-bs-toggle="collapse" data-bs-target="#collapseDispositivo_`+ idLocal +`" aria-expanded="false" aria-controls="collapseDispositivo_`+ idLocal +`">
				Fluxo e velocidade média do dia ` + data.toLocaleDateString("pt-BR") + `
			</button>
		</p>
		<div class="collapse mb-2" id="collapseDispositivo_`+ idLocal +`">
			<div class="card card-body">
				<div class="table-responsive" id="lista_resultado_dispositivo_`+ idLocal +`">         
					<table id="tabelaDispositivo_`+ idLocal +`" class="table table-sm table-bordered table-hover mb-0">
						<thead class="table-secondary">
							<tr>
								<th scope="col"><small>Horário</small></th>
								<th scope="col"><small>Fluxo</small></th>
								<th scope="col"><small>Velocidade média</small></th>
							</tr>
						</thead>
						<tbody>`
	
	jsonFluxoVelMediaDiario.forEach((fluxoVelMedia) => {
		var horario = fluxoVelMedia["hora_desc"];
		var fluxo = fluxoVelMedia["fluxo"];
		var vel_media = fluxoVelMedia["vel_media"];

		divFluxoVelMediaDiario +=
		`<tr>
			<td><small>${horario}</small></td>
			<td><small>${fluxo}</small></td>
			<td><small>${vel_media}</small></td>
		</tr>`
	});
	
	divFluxoVelMediaDiario +=
	`	</tbody></table></div></div></div></div>`
	
	return divFluxoVelMediaDiario;
}

function CriarDivGraficoFluxoVelMediaPorHorario(idLocal)
{
	var data = new Date();
	var elementoGrafico = (ELEMENTO_GRAFICO_HORARIO_DISPOSITIVO+idLocal);
	var elementoSpinner = "spinnerGraficoHorario_"+ idLocal;
	
	var divGrafico =
	`<div class="mb-2">
		<p id="pGraficoHorario_`+ idLocal +`" class="d-grid gap-2 mb-2">
			<button id="btnCollapseGraficoHorario_`+ idLocal +`" onclick="CriarGraficoFluxoVelMediaPorHorario('`+elementoGrafico+`','fluxoVelMediaPorHorarioMapa',`+ idLocal +`,'`+elementoSpinner+`')" class="btn btn-sm btn-primary btn-grafico" type="button" data-bs-toggle="collapse" data-bs-target="#collapseGraficoHorario_`+ idLocal +`" aria-expanded="false" aria-controls="collapseGraficoHorario_`+ idLocal +`">
				<span id="`+ elementoSpinner +`" class="spinner-border spinner-border-sm" role="status" aria-hidden="true" style="visibility: hidden;"></span>
				Fluxo e vel. média por hora do dia ` + data.toLocaleDateString("pt-BR") + `
			</button>
		</p>
		<div class="collapse mb-2" id="collapseGraficoHorario_`+ idLocal +`">
			<div class="card card-body">
				<div id="containerGraficoHorario_`+ idLocal +`" class="container">
					<div class="row">
			      		<div class="col-md-12">
				      		<div class="thumbnail">
				      			<canvas id="`+elementoGrafico+`"></canvas>
				      		</div> 
			      		</div>
					</div>
				</div>
			</div>
		</div>
	</div>`

	return divGrafico;
}

function CriarGraficoFluxoVelMediaPorHorario(elementoGrafico, acao, equipamento, elementoSpinner)
{
	if (!((/true/).test(CHART_MAPAS_CARREGADO[elementoGrafico])))
	{
		console.log("Carregando gráfico de  fluxo e velocidade média por hora do equipamento " + equipamento);
		var dtDataIni = new Date();
		dtDataIni.setHours(0,0,0,0);
		var dtDataFim = new Date();
		
		var dataIni = dtDataIni.toLocaleString("pt-BR").replace(",", "").substring(0, 16);
		var dataFim = dtDataFim.toLocaleString("pt-BR").replace(",", "").substring(0, 16);
		
		CriarGraficoLinhaParaMapa(elementoGrafico, 'Fluxo e vel. média por hora');
		ObterDadosGraficoMapas(acao, dataIni, dataFim, equipamento, elementoSpinner);
	}
}

function CriarDivGraficoFluxoVelMediaPorMinuto(idLocal)
{
	var elementoGrafico = (ELEMENTO_GRAFICO_MINUTO_DISPOSITIVO+idLocal);
	var elementoSpinner = "spinnerGraficoMinuto_"+ idLocal;
	
	var divGrafico =
	`<div class="mb-2">
		<p id="pGraficoMinuto_`+ idLocal +`" class="d-grid gap-2 mb-2">
			<button id="btnCollapseGraficoMinuto_`+ idLocal +`" onclick="CriarGraficoFluxoVelMediaPorMinuto('`+elementoGrafico+`','fluxoVelMediaPorMinutoMapa',`+ idLocal +`,'`+elementoSpinner+`')" class="btn btn-sm btn-primary btn-grafico" type="button" data-bs-toggle="collapse" data-bs-target="#collapseGraficoMinuto_`+ idLocal +`" aria-expanded="false" aria-controls="collapseGraficoMinuto_`+ idLocal +`">
				<span id="`+ elementoSpinner +`" class="spinner-border spinner-border-sm" role="status" aria-hidden="true" style="visibility: hidden;"></span>
				Fluxo e vel. média dos últimos 15 min.
			</button>
		</p>
		<div class="collapse mb-2" id="collapseGraficoMinuto_`+ idLocal +`">
			<div class="card card-body">
				<div id="containerGraficoMinuto_`+ idLocal +`" class="container">
					<div class="row">
			      		<div class="col-md-12">
				      		<div class="thumbnail">
				      			<canvas id="`+elementoGrafico+`"></canvas>
				      		</div> 
			      		</div>
					</div>
				</div>
			</div>
		</div>
	</div>`

	return divGrafico;
}

function CriarGraficoFluxoVelMediaPorMinuto(elementoGrafico, acao, equipamento, elementoSpinner)
{
	if (!((/true/).test(CHART_MAPAS_CARREGADO[elementoGrafico])))
	{
		console.log("Carregando gráfico de  fluxo e velocidade média por minuto do equipamento " + equipamento);
		var dtDataIni = new Date();
		var minutoAtual = dtDataIni.getMinutes();
		dtDataIni.setMinutes((minutoAtual - 15), 0, 0);
		var dtDataFim = new Date();
		
		var dataIni = dtDataIni.toLocaleString("pt-BR").replace(",", "").substring(0, 16);
		var dataFim = dtDataFim.toLocaleString("pt-BR").replace(",", "").substring(0, 16);
		
		CriarGraficoLinhaParaMapa(elementoGrafico, 'Fluxo e vel. média por minuto');
		ObterDadosGraficoMapas(acao, dataIni, dataFim, equipamento, elementoSpinner);
	}
}

function CriarDivGraficoInfracaoPorDia(idLocal)
{
	var data = new Date();
	var elementoGrafico = (ELEMENTO_GRAFICO_INFRACAO_DIA_DISPOSITIVO+idLocal);
	var elementoSpinner = "spinnerGraficoInfracaoDia_"+ idLocal;
	
	var divGrafico =
	`<div class="mb-2">
		<p id="pGraficoInfracaoDia_`+ idLocal +`" class="d-grid gap-2 mb-2">
			<button id="btnCollapseGraficoInfracaoDia_`+ idLocal +`" onclick="CriarGraficoInfracaoPorDia('`+elementoGrafico+`','infracoesPorDiaMapa',`+ idLocal +`,'`+elementoSpinner+`', this)" class="btn btn-sm btn-primary btn-grafico" type="button" data-bs-toggle="collapse" data-bs-target="#collapseGraficoInfracaoDia_`+ idLocal +`" aria-expanded="false" aria-controls="collapseGraficoInfracaoDia_`+ idLocal +`">
				<span id="`+ elementoSpinner +`" class="spinner-border spinner-border-sm" role="status" aria-hidden="true" style="visibility: hidden;"></span>
				Infrações do dia ` + data.toLocaleDateString("pt-BR") + `
			</button>
		</p>
		<div class="collapse mb-2" id="collapseGraficoInfracaoDia_`+ idLocal +`">
			<div class="card card-body">
				<div id="containerGraficoInfracaoDia_`+ idLocal +`" class="container">
					<div class="row">
			      		<div class="col-md-12">
				      		<div class="thumbnail">
				      			<canvas id="`+elementoGrafico+`"></canvas>
				      		</div> 
			      		</div>
					</div>
				</div>
			</div>
		</div>
	</div>`

	return divGrafico;
}

function CriarGraficoInfracaoPorDia(elementoGrafico, acao, equipamento, elementoSpinner, btnGraficoInfracaoDia)
{
	if (!((/true/).test(CHART_MAPAS_CARREGADO[elementoGrafico])))
	{
		console.log("Carregando gráfico de infrações por dia do equipamento " + equipamento);
		
		var dtDataIni = new Date();
		dtDataIni.setHours(0,0,0,0);
		var dtDataFim = new Date();
		dtDataFim.setHours(23,59,59,0);
		
		var dataIni = dtDataIni.toLocaleString("pt-BR").replace(",", "").substring(0, 16);
		var dataFim = dtDataFim.toLocaleString("pt-BR").replace(",", "").substring(0, 16);
		
		CriarGraficoBarraParaMapa(elementoGrafico, 'Infrações por dia');
		ObterDadosGraficoMapas(acao, dataIni, dataFim, equipamento, elementoSpinner);
	}
}

function GerenciarGraficosMapa(idDispositivo)
{
	var elementoGrafico = ELEMENTO_GRAFICO_HORARIO_DISPOSITIVO + idDispositivo;
	CHART_MAPAS_CARREGADO[elementoGrafico] = false;
	elementoGrafico = ELEMENTO_GRAFICO_MINUTO_DISPOSITIVO + idDispositivo;
	CHART_MAPAS_CARREGADO[elementoGrafico] = false;
	elementoGrafico = ELEMENTO_GRAFICO_INFRACAO_DIA_DISPOSITIVO + idDispositivo;
	CHART_MAPAS_CARREGADO[elementoGrafico] = false;	
}

function ExibirSpinner(elementoSpinner)
{
	var spinner = document.getElementById(elementoSpinner);
	if (spinner)
		spinner.style.visibility = 'visible';
}
function OcultarSpinner(elementoSpinner)
{
	var spinner = document.getElementById(elementoSpinner);
	if (spinner)
		spinner.style.visibility='hidden';
}

function HabilitarBotoesGraficos()
{
	Array.from(document.getElementsByClassName("btn-grafico")).forEach(
    	function(element, index, array) {
        	element.disabled = false;
    	}
	);
}
function DesabilitarBotoesGraficos()
{
	Array.from(document.getElementsByClassName("btn-grafico")).forEach(
    	function(element, index, array) {
        	element.disabled = true;
    	}
	);
}
function AplicarEstiloBotoesBootstrapSelect()
{
	Array.from(document.getElementsByClassName("bs-select-all")).forEach(
    	function(element, index, array) {
        	element.classList.remove('btn-light');
        	element.classList.add('btn-outline-success');
        	element.title = "Marcar todos";
    	}
	);
	
	Array.from(document.getElementsByClassName("bs-deselect-all")).forEach(
    	function(element, index, array) {
        	element.classList.remove('btn-light');
        	element.classList.add('btn-outline-danger');
        	element.title = "Desmarcar todos";
    	}
	);
}

function InicializarPopover()
{
    var options = {
	    html: true,
	    content: $('[data-name="popover-content"]')
    }
    var popoverLegenda = document.getElementById('popoverLegenda')
    var popover = new bootstrap.Popover(popoverLegenda, options)
}

function AddLoading()
{
	$("body").addClass("loading");
}
function RemoveLoading()
{
	$("body").removeClass("loading");
}

function IniciarTemporizador(){
    setInterval(function(){
        TEMPORIZADOR++;
        console.log("Tempo decorrido: " + TEMPORIZADOR);
        if (TEMPORIZADOR > TEMPO_ATUALIZAR_PAGINA) {
            TEMPORIZADOR = 0;
			ObterDadosDispositivosEquipamentos();
        }
    }, 1000);
}

function SetMapOnAll(map)
{
	for (let i = 0; i < markersArray.length; i++)
  	{
		markersArray[i].setMap(map);
  	}
}

// Removes the markers from the map, but keeps them in the array.
function HideMarkers()
{
	SetMapOnAll(null);
}

// Shows any markers currently in the array.
function ShowMarkers()
{
	SetMapOnAll(map);
}

// Deletes all markers in the array by removing references to them.
function DeleteMarkers()
{
	HideMarkers();
	dispositivos = [];
	markersArray = [];
}

function obterCamerasPorNumeroSerie(numeroSerie, callback) {
    $.ajax({
        url: '/MuralhaDigital/DispositivoEquipamento',
        type: 'GET',
        data: { acao: 'ObterCamerasPorNumeroSerie', numeroSerie: numeroSerie },
        dataType: 'xml',
        success: function(xml) {
            let cameras = [];
            
            $(xml).find("camera").each(function() {
                let cam = {
                    serieEquipamento: $(this).find("serieEquipamento").text(),
                    tipoCamera: $(this).find("tipoCamera").text(),
                    desativado: $(this).find("desativado").text() === "true",
                    emOperacao: $(this).find("emOperacao").text() === "true",
                    posicaoLat: parseFloat($(this).find("posicaoLat").text()),
                    posicaoLon: parseFloat($(this).find("posicaoLon").text()),
                    tipo: $(this).find("tipo").text(),
                    usuario: $(this).find("usuario").text()
                };
                cameras.push(cam);
            });
            
            console.log(cameras)

            if (callback) callback(cameras);
        },
        error: function(xhr, status, error) {
            console.error("Erro ao buscar câmeras:", error);
            if (callback) callback([]);
        }
    });
}