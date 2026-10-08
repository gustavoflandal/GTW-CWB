var drawingManager;
var map;
var markersArray = [];
var polygonArray = [];

//Variaveis para cadastro de nova area monitorada
var gravar_AreaMonitoradaDesc 			= '';
var gravar_AreaMonitoradaPolygonPontos 	= [];
var gravar_AreaMonitoradaPolygon;
var gravar_AreaMonitoradaRadares 		= [];

$(document).ready(function() 
{
	console.log("Documento HTML inicializado!!");	
	
	$("#modalAreaSelecionada").on('hide.bs.modal', function(){ deletarAreaMonitorada() });
});		

function initMap() 
{
		
	  //Inicializa centralizado no mapa de Curitiba
	  var iniLocation = {lat: -25.432947, lng: -49.270591};
	  
	  // The map
	  map = new google.maps.Map(document.getElementById('map'), {
		  															zoom: 12, center: iniLocation, 
		  															mapTypeId: google.maps.MapTypeId.ROADMAP,
		  															styles: 	novoStyle 
		  														 });  

	  //Obtem os radares e plota no mapa
	  obterRadaresServidor(map);
	  
	  // Add the control to the map at a designated control position
	  // by pushing it on the position's array. This code will
	  // implicitly add the control to the DOM, through the Map
	  // object. You should not attach the control manually.
	  map.controls[google.maps.ControlPosition.TOP_RIGHT].push(document.getElementById("modalAreaSelecionada"));
	  
	  ////////////////////////////////////////////
	  ////////////////////////////////
	  //Habilita as funções de desenho de area monitorada no mapa
	  drawingManager = new google.maps.drawing.DrawingManager(
	  {
			drawingMode: google.maps.drawing.OverlayType.POLYGON,
			drawingControl: true,
			drawingControlOptions: 
			{
				position: google.maps.ControlPosition.TOP_CENTER,
				drawingModes: [google.maps.drawing.OverlayType.POLYGON]
			},
			polygonOptions: 
			{
			           
	            strokeWeight: 2,
	            fillOpacity: 0.2,
	            editable: true,
	            fillColor: '#FF0000',
	            strokeColor: '#FF0000',
	            geodesic: true,
	            suppressUndo: true
			}
		});
	  
	  drawingManager.setMap(map);
		

	  google.maps.event.addListener(drawingManager, "overlaycomplete", function(event)
	  {
		    overlayClickListener(event.overlay);
		    $('#vertices').val(event.overlay.getPath().getArray());
	  });  

	  //Faz o comparativo do poligono desenhado com os 
	  ///radares existentes dentro da area monitorada
	  google.maps.event.addListener(drawingManager, "polygoncomplete", function(polygon)
	  {
		  console.log('::::Polygon completo - radares e pontos concluidos::::');
		  gravar_AreaMonitoradaPolygon = polygon;
		  
		  //Limpando as variaveis de gravação
		  gravar_AreaMonitoradaDesc 				= '';
		  gravar_AreaMonitoradaPolygonPontos 		= [];
		  gravar_AreaMonitoradaRadares 				= [];
		  
		  //Limpando a tabela de informações
		  var tabelaRadares = document.getElementById("tabelaRadares");		  
		  while(tabelaRadares.rows.length > 1) {tabelaRadares.deleteRow(1);}
		  document.getElementById("txtDescAreamonitorada").value = "";
		  ////////////////////////////////////

		  for (var i = 0 ; i < polygon.getPath().length ; i++) 		  
			  gravar_AreaMonitoradaPolygonPontos.push({ latitude: polygon.getPath().getAt(i).lat(), longitude: polygon.getPath().getAt(i).lng() });
		  	
		  		  	      
			for (var i = 0; i < markersArray.length; i++) 
			{
				point = new google.maps.LatLng(markersArray[i].getPosition().lat(), markersArray[i].getPosition().lng());
				
				if(google.maps.geometry.poly.containsLocation(point, polygon) == true) 
				{
					console.log('Radares Area Monitorada' +
							' idLocal: ' 	+ markersArray[i].idRadar +
							' Lat: ' 		+ markersArray[i].getPosition().lat() + 
							' Long: ' 		+ markersArray[i].getPosition().lng());
					
					var newRow = tabelaRadares.insertRow(tabelaRadares.rows.length);
					newRow.insertCell(0).innerHTML = markersArray[i].idRadar;
					newRow.insertCell(1).innerHTML = markersArray[i].descRadar;
					
					gravar_AreaMonitoradaRadares.push(markersArray[i].idRadar);
				}				
			}
			
			$('#modalAreaSelecionada').modal('toggle');
			$('#modalAreaSelecionada').modal('show');
			
			console.log('::::::::');
			
			//Add the click listener
		    google.maps.event.addListener(polygon, 'click', function (event) { polygon.setOptions({strokeWeight: 2.0, fillColor: 'yellow', strokeColor: 'black'}); });
		    google.maps.event.addListener(polygon,"mouseover",function(){this.setOptions({fillColor: "yellow"});});		    	 
		    google.maps.event.addListener(polygon,"mouseout",function(){this.setOptions({fillColor: "red"});});
		});  
	  

}

function deletarAreaMonitorada()
{
	vertices = gravar_AreaMonitoradaPolygon.getPath();
 	for (var i = vertices.length-1; i > -1; i--) { vertices.removeAt(i); }	 	
}


function overlayClickListener(overlay) {
    google.maps.event.addListener(overlay, "mouseup", function(event){
        $('#vertices').val(overlay.getPath().getArray());
    });
}

function obterRadaresServidor(map)
{

	$.ajax(
    {
        type: 		"GET",
        url: 		"/MuralhaDigital/AreaMonitorada",
        dataType:	"xml",
       
        //if received a response from the server
        success: 	function( data, textStatus, jqXHR) 
        {

        	processaDados( jqXHR.responseText, map);            	
        	console.log("obterRadaresServidor()::Chegou dados");
        	
    		return false;
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	console.log("(obterRadaresServidor) Erro ao processar requisição ao servidor)!!");
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
    });    			
}


function processaDados(event, map) 
{
	
	console.log("processaDados()::Chegou dados");
	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var pontos = [];

	$xml.find('equipamento').each(function()
	{
		
		var $interacao = $(this);
		
		var idLocal 		= $interacao.find('idEquipamento').text();
		var descLocal 		= $interacao.find('descEquipamento').text();
		var latitude 		= $interacao.find('latitude').text();
		var longitude		= $interacao.find('longitude').text();

		console.log("idLocal: " + idLocal + "latitude: " + latitude + " longitude: " + longitude);
		
		populaRadaresMapa(map, idLocal, descLocal, latitude, longitude);
				
	});	
	
}	


function populaRadaresMapa(map, idR, descR, latR, longR)
{

	var markerImg = new google.maps.MarkerImage('/Analise_Monitoramento/pages/configuracoes/assets/images/radar_green_bola_min_transp.png',
    		new google.maps.Size(45, 45), 
    		new google.maps.Point(0, 0),
    		new google.maps.Point(30   , 30));   	

	marker = new google.maps.Marker(
	{
  		position:	 new google.maps.LatLng(latR, longR),
  		map: 		map,
  		icon: 		markerImg,
  		infowindow: new google.maps.InfoWindow({ content: ''  }),
  		idRadar:	idR,
  		descRadar:	descR
	});
	
	markersArray.push(marker);

	console.log(idR + " - " + latR + " - " + longR);
	
    google.maps.event.addListener(marker, 'click', function() 
    {
    	
	 	var dados = 		'<div id="idRadar_"' 	+ idR + '>'+
							'<div><b><h8 id="descRadar">' 	+ idR + ' - ' + descR + '</h8></b></div>' +
							'<div><b><h8 id="latitude_' 	+ idR + '"> Latitude: ' + latR + '</h8></b></div>' +
							'<div><b><h8 id="longitude_' 	+ idR + '"> Longitude: ' + longR +  '</h8></b></div>' +
							'<br></br>' +
							'<button type="button" class="btn btn-default btn-block btn-sm" id="btnPontoRadar_' + idR + '">Adicionar Radar</button>';

	 	this.infowindow.setContent(dados);	        	
    	this.infowindow.open(map, this);
     });	    	

}


function gravarAreaMonitorada()
{
	
	console.log('Iniciando gravação de Área Monitorada');
	console.log('********************************');
	gravar_AreaMonitoradaDesc = document.getElementById("txtDescAreamonitorada").value;
	
	var valida = validarGravacao (
									gravar_AreaMonitoradaDesc, 
									gravar_AreaMonitoradaPolygonPontos, 
									gravar_AreaMonitoradaRadares
								  );

	
	if ( ! valida) { console.log('Falha validação. Gravação abortada'); return; }
	
	var strGravar = montarProtocoloGravacao(
												gravar_AreaMonitoradaDesc, 
												gravar_AreaMonitoradaPolygonPontos, 
												gravar_AreaMonitoradaRadares
										    );

	if ( ! validaString("#", strGravar)) { console.log('Falha validação. Não pode haver # na descrição da Área Monitorada. Gravação abortada'); return; };
	if ( ! validaString("&", strGravar)) { console.log('Falha validação. Não pode haver & na descrição da Área Monitorada. Gravação abortada'); return; };
	
	console.log('strGravar: ' + strGravar);
	console.log('********************************');
	
	var dataString = "acao=insereDados" + "&dados=" + strGravar; 
	
	$.ajax(
		    {
		        type: 		"POST",
		        url: 		"/MuralhaDigital/AreaMonitorada",
		        dataType:	"json",
		        data: 		dataString,
		       
		        //if received a response from the server
		        success: 	function( data, textStatus, jqXHR) 
		        {

		        	//processaDados( jqXHR.responseText, map);            	
		        	console.log("gravarAreaMonitorada()::Chegou dados:: " + data.resultado);
		        	
		    		return false;
		        },

		        error: 		function(jqXHR, textStatus, errorThrown)
		        {
		        	console.log("(gravarAreaMonitorada) Erro ao processar requisição ao servidor)!!");
		        },
		        beforeSend: function(jqXHR, settings){},
		        complete: 	function(jqXHR, textStatus){}
		    });    			
	
}

function montarProtocoloGravacao(desc, areaPolygonPontos, radares)
{
	var strSaida 	= '';
	var strRadares 	= '#RADAR';
	var strPontos 	= '#POLYGON';
	var strVertice  = '#Vertice;';
	var separador	= '#';
	
	for (var i = 0; i < radares.length; i++) 
		strRadares = strRadares + separador + radares[i];
	
	for (var i = 0; i < areaPolygonPontos.length; i++) 
		strPontos = strPontos + strVertice + areaPolygonPontos[i].latitude + ';' + areaPolygonPontos[i].longitude;
	
	strSaida = desc + strRadares + strPontos;
	
	return strSaida;	
}

function validarGravacao(desc, areaPolygonPontos, radares)
{
	if ( desc 						== '' 	) 	{ alert('Favor informar uma descrição para Área monitorada'); return false; }
	if (radares.length	 			== 0	) 	{ alert('Essa Área escolhida não possui radares.'); return false; }
	if (areaPolygonPontos.length 	== 0 	) 	{ alert('Não há vertíces para o cadastro.'); return false; }
	
	return true;	
}

function validaString(charact, info)
{
	var n = info.search(charact);
	
	if ( n > 0 ) return true; else return false;
}
