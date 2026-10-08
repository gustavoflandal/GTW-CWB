var MY_CHART_PREVISAO_FUTURO = null;

$(document).ready(function() 
{
	CriarGraficoCompPrevisaoFuturo();
});

function CriarGraficoCompPrevisaoFuturo()
{
	var ctx = document.getElementById('chartPrevisaoFuturo').getContext('2d');
	
	var tipoRelatorio = document.getElementById("selTipoRelatorio").value;

	var tituloRelatorio = 'Previsão/tendência futura';
	if (tipoRelatorio == 1)
		tituloRelatorio = 'Previsão de fluxo futuro';
	else if (tipoRelatorio == 2)
		tituloRelatorio = 'Previsão de infrações a serem geradas';
	else if (tipoRelatorio == 3)
		tituloRelatorio = 'Tendência de ocorrências a serem detectadas';
	
	if (MY_CHART_PREVISAO_FUTURO != null)
  	{
		MY_CHART_PREVISAO_FUTURO.destroy();
	}
	
	MY_CHART_PREVISAO_FUTURO = new Chart(ctx,
	{
      	type: 'bar',
      	data: {
			labels: [],
	        datasets: [{ 
				data: [],
	            label: []
	        }]
	    },
		options: 
		{
		    responsive: true,
		    interaction: {
		      mode: 'point',
		      intersect: false,
		    },
		    stacked: false,
		    plugins: {
		      title: {
		        display: true,
		        text: tituloRelatorio
		      }
		    },
		    scales: {
		      x: {
		        stacked: true,
		      },
		      y: {
		        stacked: true
		      }
		    }
  		},
	});
}

function ObterGraficoCompPrevisaoFuturo()
{
	var municipio = document.getElementById("selectMunMapa").value;
	var regiao = document.getElementById("selectRegMapa").value;
  	var equipamento = document.getElementById("selEquipamento").value;
  	var tipoRelatorio = document.getElementById("selTipoRelatorio").value;
  	var dataIni = $("#dataInicio").val();
  	var dataFim = $("#dataFim").val();
	
	var dataString = "acao=comparativoPrevisaoFuturo" + 
						"&dataIni=" + dataIni + 
						"&dataFim=" + dataFim +
						"&equipamento=" + equipamento +
						"&municipio=" + municipio +
						"&regiao=" + regiao +
						"&tipoRelatorio=" + tipoRelatorio
	
	var url = urlRoot + "MuralhaDigital/Grafico";
	
	$.ajax(
	{
	    type: 		"GET",
	    url: 		url,
		data: 		dataString,
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
				CriarGraficoCompPrevisaoFuturo();
				TratarResultado(jqXHR.responseText, MY_CHART_PREVISAO_FUTURO);
			}
			else
			{
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },
		
	    error: function(e, b, error) {
	        console.log("Erro ao obter dados do gráfico." + e.respone);
	    },
		async: false
	});
}
