var markerImg_red_new = new google.maps.MarkerImage('assets/images/3d-bar/vermelho_menor2_transp.png',
        new google.maps.Size(30, 95), 
        new google.maps.Point(0, 0),
        new google.maps.Point(0, 30));   	

var markerImg_green_new = new google.maps.MarkerImage('assets/images/3d-bar/verde_maior2_transp.png',
        new google.maps.Size(30, 95), 
        new google.maps.Point(0, 0),
        new google.maps.Point(0, 95));   	

var markerImg_yellow_new = new google.maps.MarkerImage('assets/images/3d-bar/amarelo_mediamenor2_transp.png',
        new google.maps.Size(30, 95), 
        new google.maps.Point(0, 0),
        new google.maps.Point(0, 67)); 

var markerImg_blue_new = new google.maps.MarkerImage('assets/images/3d-bar/azul_mediamaior2_transp.png',
        new google.maps.Size(30, 95), 
        new google.maps.Point(0, 0),
        new google.maps.Point(0, 80)); 

var MIN_VALOR_3D		= -1;
var MAX_VALOR_3D		= -1;
var DIF_3D				= 0;
var ESCALA_MED_3D		= 0;
var	ESCALA_VERMELHO_3D 	= 0;
var	ESCALA_AMARELO_3D	= 0;
var	ESCALA_AZUL_3D		= 0;
var	ESCALA_VERDE_3D		= 0;


var dispositivos_3d 	= [];
var markersArray_3d 	= [];
var mapa_google_3d 		= null;

$(document).ready(function() 
{
//	console.log('Mapa 3D');
	var myLatlng = LatLngPadrao;
	
	// map options,
	var myOptions = {
	  zoom: ZOOM_PADRAO,
	  center: myLatlng,
	  mapTypeId: 'satellite'
	};
	
	
	// standard map
	mapa_google_3d = new google.maps.Map(document.getElementById("map-canvas-3d"), myOptions);
	
//	ObterDadosDispositivosEquipamentos3D();
	
});		


function ObterDadosDispositivosEquipamentos3D()
{
//  	var equipamento = document.getElementById("selEquipamento").value;
	var municipio = document.getElementById("selectMunMapa").value;
	var regiao = document.getElementById("selectRegMapa").value;
  	var tipoRelatorio = document.getElementById("selTipoRelatorio").value;
  	var dataIni = $("#dataInicio").val();
  	var dataFim = $("#dataFim").val();
	
	var dataString = "acao=obterMapaDispositivos3D" + 
						"&dataIni=" + dataIni + 
						"&dataFim=" + dataFim +
//						"&equipamento=" + equipamento +
						"&municipio=" + municipio +
						"&regiao=" + regiao +
						"&tipoRelatorio=" + tipoRelatorio
	
	var url = urlRoot + "MuralhaDigital/MapaDispositivos3D";

	$.ajax(
    {
        type: 		"GET",
        url: 		url,
		data: 		dataString,
        dataType:	"xml",
       
        //if received a response from the server
        success: 	function( data, textStatus, jqXHR) 
        {
			processaDados3D(jqXHR.responseText);
        },
        
        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	console.log("(ObterDadosDispositivosEquipamentos3D) Erro ao processar requisição ao servidor)!!");
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
    });    			
}


function processaDados3D(event) 
{
	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var i = 0;

	var lista = $xml.find('DispositivosEquipamentos').find('ListaDispositivos');
	
//	console.log(lista);
	
	dispositivos_3d = [];
	
	lista.find('Dispositivo').each(function()
	{
		
		var $dispositivo = $(this);
		
		var idLocal 				= $dispositivo.find('idDispositivo').text();
		var descLocal 				= $dispositivo.find('descDispositivo').text();
		var latitude 				= $dispositivo.find('latitude').text();
		var longitude				= $dispositivo.find('longitude').text();
		var quantitativo		 	= $dispositivo.find('quantitativo').text();

//		var quantis = getRndInteger(100, 800);
//		var quantitativo = "" + quantis;
		
//		console.log("quantitativos: " + quantitativo);

//		console.log("idLocal: " + idLocal + "latitude: " + latitude + " longitude: " + longitude);
		
		var dispositivo = {
							idDispositivo: idLocal, 
							descDispositivo: descLocal, 
							latitude: latitude, 
							longitude:longitude,
							quantitativo: quantitativo};
		
		dispositivos_3d[i] = dispositivo;
		i++;
				
	});
	
//	console.log(dispositivos_3d);	
	
	setValorMinMax3D();
	
	// Apaga os markers antigos
	for (var i = 0; i < markersArray_3d.length; i++)
	{
		markersArray_3d[i].setMap(null);
	}
	markersArray_3d = [];
	
	PopulaMarkers3D();
}	

// TMP TODO - remover function tmp
function getRndInteger(min, max) {
  return Math.floor(Math.random() * (max - min + 1) ) + min;
}

function setValorMinMax3D()
{
	MIN_VALOR_3D = -1;
	MAX_VALOR_3D = -1;
	for (i = 0; i < dispositivos_3d.length; i++) 
	{
		valor = parseInt(dispositivos_3d[i].quantitativo);
		
		if (MIN_VALOR_3D == -1) MIN_VALOR_3D = valor;
		else
		{
			if (MIN_VALOR_3D > valor) MIN_VALOR_3D = valor;
		}
		
		if (MAX_VALOR_3D == -1) MAX_VALOR_3D = valor;
		else
		{
			if (MAX_VALOR_3D < valor) MAX_VALOR_3D = valor;
		}
	}
	
//	console.log('Mapa 3D:: Valor Minimo: ' + MIN_VALOR_3D + ' Maximo: ' + MAX_VALOR_3D);
	
	DIF_3D = MAX_VALOR_3D - MIN_VALOR_3D;
	
	ESCALA_MED_3D = DIF_3D / 4;
	
	ESCALA_VERMELHO_3D 	= MIN_VALOR_3D + ESCALA_MED_3D;
	ESCALA_AMARELO_3D	= ESCALA_MED_3D + ESCALA_VERMELHO_3D;
	ESCALA_AZUL_3D		= ESCALA_MED_3D + ESCALA_AMARELO_3D;
	ESCALA_VERDE_3D		= ESCALA_MED_3D + ESCALA_AZUL_3D;
	
//	console.log('Escalas definidas:: ' + ESCALA_VERMELHO_3D + '-' +  ESCALA_AMARELO_3D + '-' + ESCALA_AZUL_3D + '-' + ESCALA_VERDE_3D);
}

function PopulaMarkers3D()
{
	var marker, markerImg, i;
	
	for (var i = 0; i < dispositivos_3d.length; i++) 
	{
//		console.log('Marker:: ' + dispositivos_3d[i].idDispositivo + ' lat: ' + dispositivos_3d[i].latitude + ' long: ' + dispositivos_3d[i].longitude);
  			 			
		markerImg = IdentificaCorMarker3D(dispositivos_3d[i].quantitativo);
		
    	marker = new google.maps.Marker(
    	{
      		position:	 			new google.maps.LatLng(dispositivos_3d[i].latitude, dispositivos_3d[i].longitude),
      		map: 					mapa_google_3d,
      		icon: 					markerImg,
      		infowindow: 			new google.maps.InfoWindow({ content: ''  }),
      		idDispositivo:			dispositivos_3d[i].idDispositivo,
      		descDispositivo: 		dispositivos_3d[i].descDispositivo,
			quantitativo: 			dispositivos_3d[i].quantitativo
    	});
    	
    	markersArray_3d.push(marker);

    	/////////////////////////////////////////////////////////////////////////////////////
    	/////////////////////////////////////////////////////////////////////////////////////	    
    	// 1) Faz a criação dos campos escondidos
    	// 2) Quando o usuário clica sobre o marker, então é copiado as informações destes campos escondidos para o infowindow do marker
  		            		 	
		createDivEnquadramentosTemporario3D (dispositivos_3d[i].idDispositivo, dispositivos_3d[i].descLocal);		
    	
        google.maps.event.addListener(marker, 'click', function() 
        {
        	
		 	var dados = 		'<div id="idDispositivo_"' 	+ this.idDispositivo + '>'+
								'<div><strong><h8 id="descDispositivo">' + this.idDispositivo + ' - ' + this.descDispositivo + '</h8></strong></div>'+
								'</br>' +
								'<div id="bodyContent">'+
								'  	<div><h8  id="quantidade_'+ this.idDispositivo + '"> Quantidade no período: <strong>' + this.quantitativo + '</strong></h8></div>' +
								'</div>';    	        	

		 	this.infowindow.setContent(dados);	        	
        	this.infowindow.open(mapa_google_3d, this);
         });

	}
}

function IdentificaCorMarker3D(valor)
{
	if 		(valor <= ESCALA_VERMELHO_3D) 	return markerImg_red_new;
	else if (valor <= ESCALA_AMARELO_3D) 	return markerImg_yellow_new;
	else if (valor <= ESCALA_AZUL_3D) 		return markerImg_blue_new;
	else  return markerImg_green_new;
}

function createDivEnquadramentosTemporario3D(idLocal, info)
{	
    var dynDiv = document.createElement("div");
    dynDiv.id = 'enquadramentosTemp_'+ idLocal;
    dynDiv.innerHTML = info;
    dynDiv.style.display = "none";
    document.body.appendChild(dynDiv);	    	
}




