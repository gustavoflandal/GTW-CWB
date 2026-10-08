var heatmap;
var mapa_concentracao;


$(document).ready(function() 
{
	var myLatlng = LatLngPadrao;
	var myOptions = {
	  zoom: ZOOM_PADRAO,
	  center: myLatlng,	  
	  fullscreenControl: false,
	};
	// standard map
	mapa_concentracao = new google.maps.Map(document.getElementById("map-canvas"), myOptions);
	// heatmap layer
	heatmap = new HeatmapOverlay(mapa_concentracao, 
	  {
	    // radius should be small ONLY if scaleRadius is true (or small radius is intended)
	    "radius": 0.01,
	    "maxOpacity": 1, 
	    // scales the radius based on map zoom
	    "scaleRadius": true, 
	    // if set to false the heatmap uses the global maximum for colorization
	    // if activated: uses the data maximum within the current map boundaries 
	    //   (there will always be a red spot with useLocalExtremas true)
	    "useLocalExtrema": true,
	    // which field name in your data represents the latitude - default "lat"
	    latField: 'lat',
	    // which field name in your data represents the longitude - default "lng"
	    lngField: 'lng',
	    // which field name in your data represents the data value - default "value"
	    valueField: 'count'
	  }
	);
	
	sleep(5000).then(() => { ObterQuantitativosMapaCalor() });
	
	setInterval(function()
	{
		var dataIni = $("#dataInicio").val();
		
		if (dataIni == '')  ObterQuantitativosMapaCalor();
		
	}, 40000);	
	
});		

//sleep time expects milliseconds
function sleep (time) {
  return new Promise((resolve) => setTimeout(resolve, time));
}


function ObterQuantitativosMapaCalor()
{
	
	document.getElementById('spinAtualizaMapaConc').style.visibility='visible';
	
//	console.log('XXX ObterQuantitativosMapaCalor()');
	
  	var dataIni = $("#dataInicio").val();
  	var dataFim = $("#dataFim").val();
	
	var dataString = "acao=obterCalendarioIntensidade" + 
						"&dataIni=" + dataIni + 
						"&dataFim=" + dataFim;

	$.ajax(
    {
        type: 		"GET",
        url: 		"/MuralhaDigital/MapaCalor",
        dataType:	"xml",
		data: 		dataString,
       
        //if received a response from the server
        success: 	function( data, textStatus, jqXHR) 
        {

        	processaDados( jqXHR.responseText );            	
//        	console.log("obterDadosRadares()::Chegou dados");
			document.getElementById('spinAtualizaMapaConc').style.visibility='hidden';
        	
    		return false;
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	console.log("(obterDadosRadares) Erro ao processar requisição ao servidor)!!");
        }
    });    			
}

var VALOR = 0;

function processaDados(event) 
{
	
//	console.log("processaDados()::Chegou dados");
	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var pontos = [];
	var i = 0;

	$xml.find('itemPontoCalor').each(function()
	{		
		var $equipamento = $(this);
		
		var idLocal 		= $equipamento.find('id').text();
		var latitude 		= $equipamento.find('latitude').text();
		var longitude		= $equipamento.find('longitude').text();
		var qtde			= $equipamento.find('qtde').text();

//		console.log("XXXX idLocal: " + idLocal + "latitude: " + latitude + " longitude: " + longitude + " Qtde: " + qtde);
		
		var intQtde = parseInt(qtde)
		
		var ponto = {lat: latitude, lng:longitude, count: intQtde };
		pontos[i] = ponto;
		i++;	
	});
	
	populaPontosCalor(pontos);
	
}	

function populaPontosCalor(pontos)
{
	var dadosMapa = 
	{
		max: pontos.length,
      	data: pontos
    };

    heatmap.setData(dadosMapa);
      	
//    console.log('O Mapa de calor foi carregado com sucesso!');
}





