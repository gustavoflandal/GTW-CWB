var heatmap;

$(document).ready(function() 
{
	// map center
	var myLatlng = new google.maps.LatLng(-25.432947, -49.270591);
	// map options,
	var myOptions = {
	  zoom: 12,
	  center: myLatlng
	};
	// standard map
	map = new google.maps.Map(document.getElementById("map-canvas"), myOptions);


	
	
});		

//sleep time expects milliseconds
function sleep (time) {
  return new Promise((resolve) => setTimeout(resolve, time));
}




function populaPontosCalor(pontos)
{
	var testData = 
	{
		max: pontos.length,
      	data: pontos
    };

    heatmap.setData(testData);
      	
    console.log('O Mapa de calor foi carregado com sucesso!');
}





