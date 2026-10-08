var MY_CHART_COMP_MES_ANTERIOR = null;

$(document).ready(function() 
{
	CriarGraficoCompMesAnterior();
});

function CriarGraficoCompMesAnterior()
{
	var ctx = document.getElementById('chartCompMesAnterior').getContext('2d');
  	
  	if (MY_CHART_COMP_MES_ANTERIOR != null)
  	{
		MY_CHART_COMP_MES_ANTERIOR.destroy();
	}
  	
	MY_CHART_COMP_MES_ANTERIOR = new Chart(ctx, 
	{
		type: 'bar',
	    data: 
		{
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
		    plugins: {
		      	title: {
		        	display: true,
		        	text: 'Comparativo diário com 1 mês de deslocamento'
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

function ObterGraficoCompMesAnterior()
{
	var municipio = document.getElementById("selectMunMapa").value;
	var regiao = document.getElementById("selectRegMapa").value;
  	var equipamento = document.getElementById("selEquipamento").value;
  	var tipoRelatorio = document.getElementById("selTipoRelatorio").value;
  	var dataIni = $("#dataInicio").val();
  	var dataFim = $("#dataFim").val();
	
	var dataString = "acao=comparativoMesAnterior" + 
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
				CriarGraficoCompMesAnterior();
				TratarResultado(jqXHR.responseText, MY_CHART_COMP_MES_ANTERIOR);
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
