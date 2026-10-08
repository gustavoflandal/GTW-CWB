function initMap() 
{	
	var myLatlng = {
    	lat: -25.432947,
    	lng: -49.270591
  	};

  	var map = new google.maps.Map(document.getElementById('map'), {zoom: 11,center: myLatlng});

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

		console.log("Latitude: " + coordinates[0])
		console.log("Longitude: " + coordinates[1])
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
	
	var latitude = parseFloat(lat);
	var longitude = parseFloat(lon);
	
	return [latitude,longitude];
}

function getTypes()
{
	var urlPesquisa = urlRoot + "MuralhaDigital/PontoInteresse";
	
	console.log("[ponto-interesse.js] -> getTypes: Funcao chamada");
	
	$ajax({
		type: 		"GET",
		url: 		urlPesquisa,
		dataType: 	"xml",
		
		success:	function(data, textStatus,jqXHR)
		{
			console.log("[ponto-interesse.js] -> getTypes(ajax): sucesso");
			
			var event = jqXHR.responseText;
			varxmlDoc = $.parseXML(event);
			vat $xml = $(xmlDoc);
			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgresposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');
			
			if (sucesso)
			{
				popTypeCombo()
			}
		},
		
		error:	function(jqXHR, textStatus, errorThrown)
		{
			console.log("[ponto-interesse.js] -> getTypes(ajax): erro");
		},
		
		beforeSend: function(jqXHR, settings){},
		complete:	function(jqXHR, textStatus){}
	});
}

/*
 * @brief Popula a combobox de tipos
 */
function popTypeCombo()
{
	console.log("[ponto-interesse.js] -> popTypeCombo: Funcao chamada");
}

/*
 * @brief Popula a combobox de equipamentos
 */
function popEquipCombo()
{
	console.log("[ponto-interesse.js] -> popEquipCombo: Funcao chamada")
}
}
