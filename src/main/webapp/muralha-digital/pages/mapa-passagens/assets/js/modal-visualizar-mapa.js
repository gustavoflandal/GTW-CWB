var MSG_CONTAINER_MODAL = "#msg_container_modal_mapa_passagens";

var markerImg_red_new = new google.maps.MarkerImage('images/icones-equipamento/radar_red_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30));   	

var markerImg_green_new = new google.maps.MarkerImage('images/icones-equipamento/radar_green_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30));   	

var markerImg_yellow_new = new google.maps.MarkerImage('images/icones-equipamento/radar_amarelo_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30)); 

var markerImg_gray_new = new google.maps.MarkerImage('images/icones-equipamento/radar_gray.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30   , 30)); 
        
var passagens = [];
var markersArray = [];
var mapa_google = null;

function ExibirSpinnerModalMapa()
{
	var spinAtualizaMapaPassagens = document.getElementById('spinAtualizaMapaPassagens');
	if (spinAtualizaMapaPassagens)
		spinAtualizaMapaPassagens.style.visibility='visible';
}
function OcultarSpinnerModalMapa()
{
	var spinAtualizaMapaPassagens = document.getElementById('spinAtualizaMapaPassagens');
	if (spinAtualizaMapaPassagens)
		spinAtualizaMapaPassagens.style.visibility='hidden';	
}

function IniciarMapa()
{
	console.log("IniciarMapa");
	
	passagens = [];
	markersArray = [];
	mapa_google = null;
	
	var myOptions = {
	  	zoom: ZOOM_PADRAO,
		center: LatLngPadrao
	};
	
	// standard map
	mapa_google = new google.maps.Map(document.getElementById("map"), myOptions);

//	ExecutarPesquisaMapa();
}

function ExecutarPesquisaMapa()
{
	IniciarMapa();
	
	console.log("ExecutarPesquisaMapa");
	ExibirSpinnerModalMapa();
	
	try
	{
		LimparTabelaPassagensMapa();
		
		var placa = document.getElementById("placaConsulta").value;
		
	  	var dataIni = TratarDataHora($("#dataInicio").find("input").val());
	  	var dataFim = TratarDataHora($("#dataFim").find("input").val());
	  	
		var dataStringPesquisa = "placa=" + placa + 
									"&dataIni=" + dataIni + 
	    							"&dataFim=" + dataFim +
	    							(typeof PAGINACAO_ITENS_POR_PAGINA !== 'undefined' ? ("&paginacaoItensPorPagina=" + PAGINACAO_ITENS_POR_PAGINA) : "") +
	    							(typeof PAGINACAO_ITENS_POR_PAGINA !== 'undefined' ? ("&paginacaoOffset=" + PAGINACAO_OFFSET): "") +
									"&acao=consultaPorFiltrosTelaMapa";
									
		var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo";
		
	    $.ajax({
	        type: 		"GET",
	        url: 		urlPesquisa,
	        data: 		dataStringPesquisa,
	        dataType:	"xml",
	           
	        //if received a response from the server
	        success: 	function( data, textStatus, jqXHR) 
	        {
	        	var event = jqXHR.responseText;
	        	var xmlDoc = $.parseXML( event );
				var $xml = $(xmlDoc);
	    			
				var sucesso = $xml.find('sucesso').text();
				var msgResposta = $xml.find('msgResposta').text();
				
				sucesso = (sucesso === 'true' || sucesso === '');
				
				if (sucesso) {
					ProcessaDadosMapa( jqXHR.responseText );
				} else {
					WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, MSG_CONTAINER_MODAL);
				}
				
				OcultarSpinnerModalMapa();
	        },
	
	        error: 		function(jqXHR, textStatus, errorThrown)
	        {
	        	AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', MSG_CONTAINER_MODAL);
				OcultarSpinnerModalMapa();
	        },
	        beforeSend: function(jqXHR, settings){},
	        complete: 	function(jqXHR, textStatus){}
	 
	    });
    }
    catch(e)
    {
		console.log("Erro ao executar pesquisa de passagens para o mapa!!");
		OcultarSpinnerModalMapa();
	}
}

function ExecutarPesquisaMapaPorIdAlvo(idVeiculoAlvo)
{
	IniciarMapa();
	
	console.log("ExecutarPesquisaMapaPorIdAlvo");
	ExibirSpinnerModalMapa();
	
	try
	{
		LimparTabelaPassagensMapa();
		
		var dataStringPesquisa = "acao=obterVeiculoMapaPorIdAlvo" +
									"&idVeiculoAlvo=" + idVeiculoAlvo;
									
		var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo";
	
	    $.ajax({
	        type: 		"GET",
	        url: 		urlPesquisa,
	        data: 		dataStringPesquisa,
	        dataType:	"xml",
	           
	        //if received a response from the server
	        success: 	function( data, textStatus, jqXHR) 
	        {
	        	var event = jqXHR.responseText;
	        	var xmlDoc = $.parseXML( event );
				var $xml = $(xmlDoc);
	    			
				var sucesso = $xml.find('sucesso').text();
				var msgResposta = $xml.find('msgResposta').text();
				
				sucesso = (sucesso === 'true' || sucesso === '');
				
				if (sucesso) {
					ProcessaDadosMapa( jqXHR.responseText );
				} else {
					WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, MSG_CONTAINER_MODAL);
				}
				
				OcultarSpinnerModalMapa();
	        },
	
	        error: 		function(jqXHR, textStatus, errorThrown)
	        {
	        	AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', MSG_CONTAINER_MODAL);
				OcultarSpinnerModalMapa();
	        },
	        beforeSend: function(jqXHR, settings){},
	        complete: 	function(jqXHR, textStatus){}
	 
	    });
    }
    catch(e)
    {
		console.log("Erro ao executar pesquisa de passagens para o mapa!!");
		OcultarSpinnerModalMapa();
	}
}

function ExecutarPesquisaMapaPorIdAlerta(idAlerta)
{
	IniciarMapa();
	
	console.log("ExecutarPesquisaMapaPorIdAlerta");
	ExibirSpinnerModalMapa();
	
	try
	{
		LimparTabelaPassagensMapa();
		
		var dataStringPesquisa = "acao=obterCoordAlerta" +
									"&idAlerta=" + idAlerta;
									
		var urlPesquisa = urlRoot + "MuralhaDigital/Alerta";
	
	    $.ajax({
	        type: 		"GET",
	        url: 		urlPesquisa,
	        data: 		dataStringPesquisa,
	        dataType:	"xml",
	           
	        //if received a response from the server
	        success: 	function( data, textStatus, jqXHR) 
	        {
	        	var event = jqXHR.responseText;
	        	var xmlDoc = $.parseXML( event );
				var $xml = $(xmlDoc);
	    			
				var sucesso = $xml.find('sucesso').text();
				var msgResposta = $xml.find('msgResposta').text();
				
				sucesso = (sucesso === 'true' || sucesso === '');
				
				if (sucesso) {
					ProcessaDadosMapa( jqXHR.responseText );
				} else {
					WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, MSG_CONTAINER_MODAL);
				}
				
				OcultarSpinnerModalMapa();
	        },
	
	        error: 		function(jqXHR, textStatus, errorThrown)
	        {
	        	AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', MSG_CONTAINER_MODAL);
				OcultarSpinnerModalMapa();
	        },
	        beforeSend: function(jqXHR, settings){},
	        complete: 	function(jqXHR, textStatus){}
	 
	    });
    }
    catch(e)
    {
		console.log("Erro ao executar pesquisa de passagens para o mapa!!");
		OcultarSpinnerModalMapa();
	}
}

function ProcessaDadosMapa(event) 
{
	console.log("ProcessaDadosMapa");
	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var i = 0;
	
	$xml.find('Veiculo').each(function()
	{
		
		var $item = $(this);
		
		var idLocal = $item.find('idLocal').text();
		var serieEquipamento = $item.find('serieEquipamento').text();
		var idPista = $item.find('idPista').text();
		var descLocal = $item.find('descLocal').text();
		var equipamento = serieEquipamento + ' - ' + descLocal;
		var latitude = $item.find('latitude').text();
		var longitude = $item.find('longitude').text();
		var placa = $item.find('placa').text();
		var data = $item.find('dataVeicFormatada').text();
		var velocidade = $item.find('velocidade').text();
		
		var tableRef = document.getElementById("tabelaPassagensMapa").getElementsByTagName('tbody')[0];

		var row   = tableRef.insertRow(tableRef.rows.length);
		row.insertCell(0).innerHTML = "<small>" + equipamento + "</small>";
		row.insertCell(1).innerHTML = "<small>" + idPista + "</small>";
		row.insertCell(2).innerHTML = "<small>" + data + "</small>";
		row.insertCell(3).innerHTML = "<small>" + placa + "</small>";
		row.insertCell(4).innerHTML = "<small>" + velocidade + "</small>";
		
		console.log("idLocal: " + idLocal + 
						" serieEquipamento: " + serieEquipamento + 
						" latitude: " + latitude + 
						" longitude: " + longitude +
						" placa: " + placa +
						" data: " + data +
						" velocidade: " + velocidade);
		
		var passagem = {
							idDispositivo: idLocal,
							serieEquipamento: serieEquipamento,
							descDispositivo: descLocal, 
							latitude: latitude, 
							longitude: longitude, 
							placa: placa, 
							data: data, 
							velocidade: velocidade};
		
		passagens[i] = passagem;
		i++;
				
	});
	
	console.log(passagens);	
	
	PopulaMarkers();
}	

function PopulaMarkers()
{
	console.log("PopulaMarkers");
	var marker, i;
	var arrayPassagens = [];
	var idLocalAux = null;
	var apenasUmLocal = true;
	
	for (i = 0; i < passagens.length; i++) 
	{
		console.log('Marker:: ' + passagens[i].idDispositivo + ' lat: ' + passagens[i].latitude + ' long: ' + passagens[i].longitude + " data: " + passagens[i].data);
		
		if (idLocalAux != null && idLocalAux != passagens[i].idDispositivo)
			apenasUmLocal = false;
		
		try
		{
			arrayPassagens.push({
	            lat: parseFloat(passagens[i].latitude), 
	            lng:  parseFloat(passagens[i].longitude)
	        });
	        }
		catch(e)
		{
			console.log("Erro ao preparar array de passagens para ligar pontos no mapa!!");
		}
		
    	marker = new google.maps.Marker(
    	{
      		position:	 			new google.maps.LatLng(passagens[i].latitude, passagens[i].longitude),
      		map: 					mapa_google,
//      		icon: 					markerImg_gray_new,
      		infowindow: 			new google.maps.InfoWindow({ content: ''  }),
      		idDispositivo:			passagens[i].idDispositivo,
      		serieEquipamento:		passagens[i].serieEquipamento,
      		descDispositivo: 		passagens[i].descDispositivo,
      		placa: 					passagens[i].placa,
      		data: 					passagens[i].data,
      		velocidade: 			passagens[i].velocidade
    	});
		mapa_google.setCenter(marker.getPosition());
  		mapa_google.setZoom(13);

		markersArray.push(marker);    	

        google.maps.event.addListener(marker, 'click', function() 
        {
			mark = this;
	
		 	var dados = '<div id="idDispositivo_"' + mark.idDispositivo + '>'+
						'	<div><strong><h8 id="descDispositivo_"'	+ mark.idDispositivo + '>' + mark.serieEquipamento + ' - ' + mark.descDispositivo + '</h8></strong></div>'+
						'	</br>' +
						'	<div class="container" id="bodyContent">'+
						'  		<div><h8  id="placa_'+ mark.idDispositivo + '"> Placa: <small>' + mark.placa + '</small></h8></div>' +					
						'  		<div><h8  id="data_'+ mark.idDispositivo + '"> Data: <small>' + mark.data + '</small></h8></div>' +					
						'  		<div><h7  id="velocidade_'+ mark.idDispositivo + '"> Velocidade: ' + mark.velocidade + '</h7></div>' +					
						'  		</br>' +
						'	</div>' +
						'</div>';
										
			marker.infowindow.setContent(dados);
        	marker.infowindow.open(mapa_google, mark);
        });
        
        idLocalAux = passagens[i].idDispositivo;
	}
	
	const lineSymbolArrow =
	{ 
		path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
	};
	
	const lineSymbolCircle =
	{
	    path: google.maps.SymbolPath.CIRCLE,
	    scale: 8,
	    strokeColor: "#393",
  	};
  	
  	
  	var car = "M17.402,0H5.643C2.526,0,0,3.467,0,6.584v34.804c0,3.116,2.526,5.644,5.643,5.644h11.759c3.116,0,5.644-2.527,5.644-5.644 V6.584C23.044,3.467,20.518,0,17.402,0z M22.057,14.188v11.665l-2.729,0.351v-4.806L22.057,14.188z M20.625,10.773 c-1.016,3.9-2.219,8.51-2.219,8.51H4.638l-2.222-8.51C2.417,10.773,11.3,7.755,20.625,10.773z M3.748,21.713v4.492l-2.73-0.349 V14.502L3.748,21.713z M1.018,37.938V27.579l2.73,0.343v8.196L1.018,37.938z M2.575,40.882l2.218-3.336h13.771l2.219,3.336H2.575z M19.328,35.805v-7.872l2.729-0.355v10.048L19.328,35.805z";
	const lineSymbolCar =
	{
	    path: car,
	    scale: .6,
		strokeColor: "#393",
		strokeWeight: .10,
		fillOpacity: 1,
		fillColor: '#393',
		offset: '5%',
		// rotation: parseInt(heading[i]),
		anchor: new google.maps.Point(12, 25)
  	};

	try
	{
		if (arrayPassagens.length > 0)
		{
			if(apenasUmLocal)
			{
				new google.maps.Polyline(
					{
						path:	arrayPassagens,
						map: 	mapa_google,
					});
			}
			else
			{
				const line = new google.maps.Polyline(
					{
						path:	arrayPassagens,
						icons:	[ { icon: lineSymbolArrow, offset: "100%", repeat: "40%", }, { icon: lineSymbolCar, offset: "100%",  } ],
						map: 	mapa_google,
					});
					
				AnimateIcon(line);
			}
		}
	}
	catch(e)
	{
		console.log("Erro ao adicionar seta de direção entre os pontos do mapa!!");
	}
}

// Use the DOM setInterval() function to change the offset of the symbol
// at fixed intervals.
function AnimateIcon(line)
{
  let count = 0;

  window.setInterval(() =>
  {
    count = (count + 1) % 200;

    const icons = line.get("icons");

    icons[1].offset = count / 2 + "%";
    line.set("icons", icons);
  }, 20);
}

function LimparTabelaPassagensMapa()
{
	var tableHeaderRowCount = 1;
	var table = document.getElementById("tabelaPassagensMapa");
	var rowCount = table.rows.length;
	
	for (var i = tableHeaderRowCount; i < rowCount; i++) 
	    table.deleteRow(tableHeaderRowCount);
}
