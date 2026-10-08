

$(document).ready(function() 
{
	sleep(1000).then(() => { obterDadosAnomalias() });
	
	setInterval(function()
	{
		obterDadosAnomalias();
		
	}, 30000); // 30 segundos
});


//sleep time expects milliseconds
function sleep (time) {
  return new Promise((resolve) => setTimeout(resolve, time));
}

function obterDadosAnomalias()
{
	var url = urlRoot + "MuralhaDigital/Anomalia";
	
	$.ajax(
	{
	    type: 		"GET",
	    url: 		url,
	    dataType:	"xml",
		
        success: function( xml, textStatus, jqXHR ) 
        {
        	var event = jqXHR.responseText;
        	var xmlDoc = $.parseXML( event );
			var $xml = $(xmlDoc);
    			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');
			
			if (sucesso)
			{
				// Se o gráfico já existir, destroi o objeto e cria novamente.
				processarDados(jqXHR.responseText);
			}
			else
			{
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },
		
	    error: function(e, b, error) {
	        console.log("Erro ao obter dados de anomalias." + e.respone);
	    },
		async: false
	});
}

function processarDados(event)
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	anomaliaEncontrada = false;
	
	$xml.find('AlertaAnomalia').each(function()
	{
		var $item = $(this);
		
		var id = $item.find('id').text();
		var tipo = $item.find('tipo').text();
		var possuiAnomalia = $item.find('possuiAnomalia').text();
		var descAnomalia = $item.find('descAnomalia').text();
		
		var anomaliaId = document.getElementById("alertaAnomalia" + id);
		
		if (possuiAnomalia == "1")
		{
			anomaliaEncontrada = true;
			anomaliaId.innerHTML = "<small><strong>" + tipo + ":</strong> " + descAnomalia + "</small>";
		}
		else if (possuiAnomalia == "0")
		{
			anomaliaId.innerHTML = "<small><strong>" + tipo + ":</strong> " + "Não há" + "</small>";
		}
	});
	
	anomaliaDiv = document.getElementById("alertaAnomaliaDiv");
	
	if (anomaliaEncontrada) anomaliaDiv.classList.replace('alert-success', 'alert-danger');
	else anomaliaDiv.classList.replace('alert-danger', 'alert-success');
}