var LATITUDE = null;
var LONGITUDE = null;
const EQUIPS = [];
var LISTA_EQUIPS = "";

$(document).ready(function ()
{
	obterEquipamentosGenerico("inputEquip");
});

function initMap() 
{	
	
	var myLatlng = LatLngPadrao;

  	var map = new google.maps.Map(document.getElementById('map'), {zoom: ZOOM_PADRAO,center: myLatlng});

  	// Create the initial InfoWindow.
 	var infoWindow = new google.maps.InfoWindow({
    	content: 'Clique no mapa para marcar o local do Ponto de Interesse',
    	position: myLatlng
  	});
  	infoWindow.open(map);

  	var marker;

	// Configure the click listener.
  	map.addListener('click', function(mapsMouseEvent) 
	{
	    // Close the current InfoWindow.
	    infoWindow.close();
	
	    if (marker && marker.setPosition)
	      marker.setPosition(mapsMouseEvent.latLng);
	    else
	      marker = new google.maps.Marker({
	        position: mapsMouseEvent.latLng,
	        map: map,
	        icon: "http://maps.google.com/mapfiles/ms/micons/blue.png"
	      });
	
	    //Show latitude and longtitude on the sites
	    var l = mapsMouseEvent.latLng.toString();
	    document.getElementById("latlng").innerHTML = "Localização Lat/Long: " + l;

		var coordinates = coordStringToFloat(l)
		
		//LATITUDE = coordinates[0];
		//LONGITUDE = coordinates[1];

		//console.log("Latitude: " + LATITUDE);
		//console.log("Longitude: " + LONGITUDE);
  });
}

/*
 * @brief Recebe uma string de coordenadas no formado (x.abcd, y.efgh)
 *		  e devolve um vetor com a latitude e longitude como float
 *
 * @param String com as coordenadas
 * @return Vetor com latitude e longitude
 *
 */
function coordStringToFloat(coordinate)
{
	var coord = coordinate.split(",");
	var lat = coord[0].replace(/[^0-9-.]/g,"");
	var lon = coord[1].replace(/[^0-9-.]/g,"");
	
	//var latitude = parseFloat(lat);
	//var longitude = parseFloat(lon);
	
	LATITUDE = parseFloat(lat);
	LONGITUDE = parseFloat(lon);
	
	//console.log(LATITUDE);
	//console.log(LONGITUDE);
	
	//return [latitude,longitude];
}

/*
 * @brief Salva o ponto configurado
 */
function salvaPonto()
{
	// Busca as variaveis a armazenar
	var nome = document.getElementById("inputName").value;
	var desc = document.getElementById("inputDesc").value;
	var tipo = document.getElementById("inputType").value;
	
	console.clear();
	
	// Verifica se todos os campos foram preenchidos
	if(LATITUDE == null || LONGITUDE == null || !nome || 
		!desc || !tipo || EQUIPS.length == 0)
	{
		console.log("salvaPonto: Campo faltante:");
		
		if(LATITUDE == null)
			console.log("\tLatitude");
		if(LONGITUDE == null)
			console.log("\tLongitude");
		if(!nome)
			console.log("\tNome");
		if(!desc)
			console.log("\tDescricao");
		if(!tipo)
			console.log("\tTipo");
		if(EQUIPS.length == 0)
			console.log("\tEquipamentos");
	}
	else
	{
		console.log("salvaPonto: Nome p/ inserir:                  " + nome         );
		console.log("salvaPonto: Descricao p/ inserir:             " + desc         );
		console.log("salvaPonto: Tipo p/ inserir:                  " + tipo         );
		console.log("salvaPonto: Num de equipamentos p/ inserir:   " + EQUIPS.length);
		console.log("salvaPonto: Latitude p/ inserir:              " + LATITUDE     );
		console.log("salvaPonto: Longitude p/ inserir:             " + LONGITUDE    );
		
		// Monta o dataString para envio
		var stringEquip = "";
		for(let i = 0; i < EQUIPS.length - 1; i++)
		{
			stringEquip += EQUIPS[i] + ";"
		}
		stringEquip += EQUIPS[EQUIPS.length-1]
		
		var dataString = ("acao=salvaPontoInteresse"
			+ "&nome=" + nome
			+ "&desc=" + desc
			+ "&tipo=" + tipo
			+ "&lati=" + LATITUDE
			+ "&long=" + LONGITUDE
			+ "&equi=" + stringEquip);
			
		console.log("salvaPonto: dataString -> " + dataString);
		
		insereDados(dataString);
	}
	//window.location.replace('consulta-ponto-interesse.jsp');
}

function insereDados(dataString)
{
	var urlPesquisa = urlRoot + "MuralhaDigital/PontoInteresse";
	
	$.ajax({
		type: "GET",
		url: urlPesquisa,
		data: dataString,
		dataType: "xml",
		
		success: function(data, textStatus, jqXHR)
		{
			var event = jqXHR.responseText;
        	var xmlDoc = $.parseXML( event );
			var $xml = $(xmlDoc);
    			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');
			
        	if (sucesso) {
				console.log("insereDados: Sucesso");
				alert(msgResposta);
				window.location.replace('consulta-ponto-interesse.jsp');
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
				alert("Tempo de resposta excedido")
			}
		},
		
		error: function(jqXHR, textStatus, errorThrown)
		{
			alert("Erro no cadastro do ponto de interesse");
		},
		
		beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
	});
}

/*
 * @brief Adiciona os equipamentos selecionados no combo a lista
 * 		  de equipamentos relacionados ao ponto
 */
function adicionaEquip()
{
	var repetido = false;
	
	// Verifica se o equipamento selecionado ja foi inserido
	for(let equip of EQUIPS)
	{
		if(equip == document.getElementById("inputEquip").value)
		{
			console.log("adicionaEquip: Equipamento ja adicionado ");
			repetido = true;
		}
	}
	
	// Insere o equipamento na lista
	if(!repetido)
	{
		EQUIPS[EQUIPS.length] = document.getElementById("inputEquip").value;
		var nome = $("#inputEquip option[value=" + EQUIPS[EQUIPS.length - 1] + "]").text();
		LISTA_EQUIPS += nome + "\n";
		document.getElementById("inputNameEquip").innerHTML = LISTA_EQUIPS;
		console.log(nome);
	}
	
	// Remove o item adicionado do combo
	var strRemove = "#inputEquip option[value=" + EQUIPS[EQUIPS.length - 1] + "]";
	$(strRemove).remove();
	
	// Lista os equipamentos selecionados
	console.log("adicionaEquip: Numero de equipamentos listados: " + EQUIPS.length);
	for(let equip of EQUIPS)
	{
		console.log("\t" + equip);
	}
}

