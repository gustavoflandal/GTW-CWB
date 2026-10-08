$(document).on({
    ajaxStart: function(){
        $("body").addClass("loading"); 
    },
    ajaxStop: function(){ 
        $("body").removeClass("loading"); 
    }    
});

var markerImg_red_new = new google.maps.MarkerImage('assets/images/icones-equipamento/radar_red_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30));   	

var markerImg_green_new = new google.maps.MarkerImage('assets/images/icones-equipamento/radar_green_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30));   	

var markerImg_yellow_new = new google.maps.MarkerImage('assets/images/icones-equipamento/radar_amarelo_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30)); 

var markerImg_gray_new = new google.maps.MarkerImage('assets/images/icones-equipamento/radar_gray.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30)); 


var dispositivos 	= [];
var markersArray 	= [];
var mapa_google 	= null;

var ID_TIPO_ALERTA_SELECIONADO		= '';
var TIPO_ALERTA_SELECIONADO			= '';
var ID_TEMPO_CONSULTA_SELECIONADO 	= '';
var TEMPO_CONSULTA_SELECIONADO 		= '';
var TIPO_REGISTRO_SELECIONADO		= '';

function MapaTerreno() 		{ mapa_google.setMapTypeId('terrain'); 													}
function MapaSatelite() 	{ mapa_google.setMapTypeId('satellite'); 												}
function MapaRoadMap() 		{ mapa_google.setMapTypeId('roadmap'); 													}
function MapaHibrido() 		{ mapa_google.setMapTypeId('hybrid '); 													}
function MapaTransito() 	{ transitLayer = new google.maps.TransitLayer(); transitLayer.setMap(mapa_google);		}
function MapaBicicleta() 	{ bicycleLayer = new google.maps.BicyclingLayer(); bicycleLayer.setMap(mapa_google);	}
function MapaTrafego() 		{ traficLayer = new google.maps.TrafficLayer(); traficLayer.setMap(mapa_google);		}
function SetaTipoMapa(valor)
{  
	switch (valor) {
	  case '1':
	    MapaTerreno();
	    break;
	  case '2':
		MapaSatelite();
	    break;
	  case '3':
		MapaRoadMap();
	    break;
	  case '4':
		MapaHibrido();
	    break;
	  case '5':
		MapaTransito();
	    break;
	  case '6':
		MapaTrafego();
	    break;
	  case '7':
		MapaBicicleta();
	    break;

	  default:
	    MapaRoadMap();
	}
}

$(document).ready(function() 
{	
	console.log('Iniciando mapa de alertas e ocorrencias');
	ObterListaPermissoesFuncionalidades();
	
	mapa_google = new google.maps.Map(document.getElementById("map-canvas"), 
	{
    	zoom: ZOOM_PADRAO,
    	center: LatLngPadrao,
		tilt: 45,
		mapTypeId: 'terrain'
  	}); MapaTrafego();

	ObterDadosDispositivosEquipamentos();
	
	CarregarValoresRefreshTela();
	
	
	$("#selectTpMapa").change(function(){
		SetaTipoMapa(this.options[this.selectedIndex].value);
	});	
	
	$("#selectTpAlertaOcorr").change(function(){
		ID_TIPO_ALERTA_SELECIONADO	= this.options[this.selectedIndex].value;      
		TIPO_ALERTA_SELECIONADO		= this.options[this.selectedIndex].text;   
	});
	
	$("#selectTempo").change(function(){
		ID_TEMPO_CONSULTA_SELECIONADO	= this.options[this.selectedIndex].value;
		TEMPO_CONSULTA_SELECIONADO 		= this.options[this.selectedIndex].text;         
	});
});	

function ObterListaPermissoesFuncionalidades()
{
	$.ajax(
    {
        type: 		"GET",
        url: 		"/MuralhaDigital/PermissoesFuncionalidade",
		data:		"acao=ListaFuncionalidadesByUsuarioId",
        dataType:	"xml",
       
        success: 	function( data, textStatus, jqXHR) 
        {
			console.log("ObterListaPermissoesFuncionalidades()::Chegou dados");
        	CarregaSelectTipos( jqXHR.responseText );            	        	
        	
    		return false;
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	console.log("(ObterListaPermissoesFuncionalidades) Erro ao processar requisição ao servidor)!!");
        },
    });    			
}

function CarregarValoresRefreshTela()
{
	//Tempo da consulta
	var selectTempo = document.getElementById("selectTempo");
	if (selectTempo)
	{
		var valorTempo = parseInt(selectTempo.options[selectTempo.selectedIndex].value)
		if (valorTempo > 0)
		{
			ID_TEMPO_CONSULTA_SELECIONADO = selectTempo.options[selectTempo.selectedIndex].value;
			TEMPO_CONSULTA_SELECIONADO = selectTempo.options[selectTempo.selectedIndex].text;
		}
	}
}

function CarregaSelectTipos(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var lista = $xml.find('PermissoesFuncionalidade').find('ListaPermissoesFuncionalidade');
	
	console.log(lista);
	
	lista.find('PermissaoFuncionalidade').each(function()
	{
		var $permissao = $(this);
		
		var id 			= $permissao.find('idPermFunc').text();
		var descricao 	= $permissao.find('descricao').text();
		
		$('#selectTpAlertaOcorr').append($('<option>', 
		{
		    value: id,
		    text: descricao
		}));	
			
	});
}

function ObterDadosDispositivosEquipamentos()
{
	var dataString = "acao=obterDispositivosEquipamentos";
	
	$.ajax(
    {
        type: 		"GET",
        url: 		"/MuralhaDigital/MapaDispositivosEquipamentos",
		data: 		dataString,
        dataType:	"xml",
       
        success: 	function( data, textStatus, jqXHR) 
        {
        	ProcessaDadosDispositivosEquipamentos( jqXHR.responseText );            	
    		return false;
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	console.log("(obterDadosRadares) Erro ao processar requisição ao servidor)!!");
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
    });    			
}


function ProcessaDadosDispositivosEquipamentos(event) 
{
	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var i = 0;

	var lista = $xml.find('DispositivosEquipamentos').find('ListaDispositivos');
	
	console.log(lista);
	
	lista.find('Dispositivo').each(function()
	{
		
		var $dispositivo = $(this);
		
		var idLocal 				= $dispositivo.find('idDispositivo').text();
		var serieEquipamento		= $dispositivo.find('serieEquipamento').text();
		var codOrgao				= ( ($dispositivo.find('codigosEquipamentos').text() == null || $dispositivo.find('codigosEquipamentos').text() == '') ? "N/D" : $dispositivo.find('codigosEquipamentos').text() );
		var descLocal 				= $dispositivo.find('descDispositivo').text();
		var latitude 				= $dispositivo.find('latitude').text();
		var longitude				= $dispositivo.find('longitude').text();
		var passagensDiasRecentes 	= $dispositivo.find('passagensDiasRecentes').text();
		var passagensUltimaHora 	= $dispositivo.find('passagensUltimaHora').text();

		console.log("idLocal: " + idLocal + "latitude: " + latitude + " longitude: " + longitude);
		
		var dispositivo = {
							idDispositivo: idLocal,
							serieEquipamento: serieEquipamento,
							codOrgao: codOrgao,
							descDispositivo: descLocal, 
							latitude: latitude, 
							longitude:longitude,
							passagensDiasRecentes: passagensDiasRecentes,
							passagensUltimaHora: passagensUltimaHora};
		
		dispositivos[i] = dispositivo;
		i++;
				
	});
	
	console.log(dispositivos);	
	
	PopulaMarkers();
}	

function PopulaMarkers()
{
	var marker, i;
	
	for (i = 0; i < dispositivos.length; i++) 
	{
		console.log('Marker:: ' + dispositivos[i].idDispositivo + ' lat: ' +
			dispositivos[i].latitude + ' long: ' + dispositivos[i].longitude);
    	
    	marker = new google.maps.Marker(
    	{
      		position:	 			new google.maps.LatLng(dispositivos[i].latitude, dispositivos[i].longitude),
      		map: 					mapa_google,
      		icon: 					markerImg_gray_new,
      		infowindow: 			new google.maps.InfoWindow({ content: ''  }),
      		idDispositivo:			dispositivos[i].idDispositivo,
      		serieEquipamento:		dispositivos[i].serieEquipamento,
      		codOrgao: 				dispositivos[i].codOrgao,
      		descDispositivo: 		dispositivos[i].descDispositivo,
			tipo: 					'',
			tipoRegistro:			'',
			quantidade: 			''
    	});

		markersArray.push(marker);    	

        google.maps.event.addListener(marker, 'click', function() 
        {
			mark = this;
			
		 	var dados = '<div id="idDispositivo_"' 	+ mark.idDispositivo + '>'+
						'	<div><strong><h8 id="descDispositivo">' + mark.codOrgao + ' - ' + mark.serieEquipamento + ' - ' + mark.descDispositivo + '</h8></strong></div>'+
						'	</br>' +
						'	<div class="container" id="bodyContent">'+
						'  		<div><h8  id="tipoRegistro_'+ mark.idDispositivo + '"> Tempo: últimas <small>' + TEMPO_CONSULTA_SELECIONADO + ' horas</small></h8></div>' +
						'  		<div><h8  id="tipoRegistro_'+ mark.idDispositivo + '"> Tipo: <small>' + mark.tipoRegistro + '</small></h8></div>' +					
						'  		<div><h8  id="tipo_'+ mark.idDispositivo + '"> Consulta: <small>' + mark.tipo + '</small></h8></div>' +					
						'  		<div><strong><h7  id="quantidade_'+ mark.idDispositivo + '"> Quantitativo:' + mark.quantidade + '</h7></strong></div>' +					
						'  		</br>' +
						'	</div>';
			if(mark.quantidade > 0)
			{
				dados +='   <div class="d-grid gap-2">' +
						'		<button type="button" class="btn-primary btn-block btn-sm" ' + 							
						' 				id="btnDetalhar_'+ mark.idDispositivo + '" name="'+ mark.idDispositivo + '" onclick="Detalhar(this)">DETALHAR</button>' +			
						'   </div>';					
			}
				dados +='</div>';
										
			marker.infowindow.setContent(dados);
        	marker.infowindow.open(mapa_google, mark);
        });
	}
}

function ProcessarConsulta()
{
	console.log('Iniciando processamento v2');
	
	if(ID_TIPO_ALERTA_SELECIONADO == '' || ID_TIPO_ALERTA_SELECIONADO == '0')
	{
		AlertCsx_E_TimeOut_8000ms("Favor selecionar Tipo de ocorrência/alerta");
		return;
	}

	if(ID_TEMPO_CONSULTA_SELECIONADO == '' || ID_TEMPO_CONSULTA_SELECIONADO == '0')
	{
		AlertCsx_E_TimeOut_8000ms("Favor selecionar Tempo de Consulta");
		return;
	}
	
	var itensSelecionados = 'Itens selecionados:: ' + TIPO_ALERTA_SELECIONADO + ' --> Tempo: ' 	+ TEMPO_CONSULTA_SELECIONADO;
	document.getElementById("txtFiltrosSelecionados").innerHTML =  itensSelecionados;    
		
	TIPO_REGISTRO_SELECIONADO = TIPO_ALERTA_SELECIONADO.split(" ")[0].replace('ê', 'e').toUpperCase();
	TEMPO_CONSULTA_SELECIONADO = ObterTempoPesquisa();
	
	ObterPesquisaQuantitativo();
}

function ObterPesquisaQuantitativo()
{
	console.log('Executando consulta');
	
	ReiniciarMarkersIcons();

    var urlPesquisa = urlRoot + "MuralhaDigital/AlertaOcorrencia";

	var dataString = 'acao=QuantitativosAlertasOcorrencias' +
					 '&tipoRegistro=' + TIPO_REGISTRO_SELECIONADO +
					 '&tipoAlertaOcorrencia=' + ID_TIPO_ALERTA_SELECIONADO +
					 '&tempo=' + TEMPO_CONSULTA_SELECIONADO;

    $.ajax({
        type: 		"GET",
        url: 		urlPesquisa,
		data: 		dataString,
        dataType:	"xml",
           
        success: 	function( data, textStatus, jqXHR) 
        {
        	ProcessaRespostaQuantitativo ( jqXHR.responseText );
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('ObterPesquisaQuantitativo():: Erro ao processar requisição ao servidor!!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });    
}

function ProcessaRespostaQuantitativo(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	var lista = $xml.find('DispositivosEquipamentos').find('ListaDispositivos');
	
	lista.find('Dispositivo').each(function()
	{
		
		var $dispositivo = $(this);
		
		var idLocal 			= $dispositivo.find('idDispositivo'	).text();
		var tipo 				= $dispositivo.find('tipo'			).text();
		var tipoRegistro 		= $dispositivo.find('tipoRegistro'	).text();
		var quantitativo 		= $dispositivo.find('quantitativo'	).text();
		
		console.log("idLocal: " + idLocal + 'tipo: ' + tipo + ' tipoReg.: ' + tipoRegistro + ' quant.: ' + quantitativo);
		
		var marker;	
		for (i = 0; i < markersArray.length; i++){
			if(markersArray[i].idDispositivo == idLocal)
				marker = markersArray[i];
		} 		
		
		marker.tipo = tipo;
		marker.tipoRegistro = tipoRegistro;
		marker.quantidade = quantitativo;
		marker.setIcon(markerImg_yellow_new);
	});
}

function ReiniciarMarkersIcons()
{
	for (i = 0; i < markersArray.length; i++)
	{
		markersArray[i].setIcon(markerImg_gray_new);
	} 	
}

function ObterTempoPesquisa()
{
	if (ID_TEMPO_CONSULTA_SELECIONADO == 1) return 48;
	if (ID_TEMPO_CONSULTA_SELECIONADO == 2) return 72;
	if (ID_TEMPO_CONSULTA_SELECIONADO == 3) return (10*24);
	if (ID_TEMPO_CONSULTA_SELECIONADO == 4) return (30*24);
}


function Detalhar(local)
{
	var url = 	'/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?' + 
			  	'idEquipamento=' 		+ local.name + 
				'&tempo=' 				+ TEMPO_CONSULTA_SELECIONADO +  
				'&TipoReg=' 			+ TIPO_REGISTRO_SELECIONADO + 
				'&Tipo=' 				+ ID_TIPO_ALERTA_SELECIONADO;
								
	console.log(url);
	
	CentralizaWindow(url, 'detalhes', window, (screen.width/1.3), (screen.height/1.5));
}

function CentralizaWindow(url, windowName, win, w, h) 
{
    const y = win.top.outerHeight / 2 + win.top.screenY - ( h / 2);
    const x = win.top.outerWidth / 2 + win.top.screenX - ( w / 2);
    return win.open(url, windowName, `toolbar=yes, location=yes, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, copyhistory=no, width=${w}, height=${h}, top=${y}, left=${x}`);
}