var MY_CHART_DISTRIBUICAO_POR_FAIXA = null;

$(document).ready(function() 
{
	CriarGraficoDistribuicaoPorFaixa();
});

function CriarGraficoDistribuicaoPorFaixa()
{
	var ctx = document.getElementById('chartDistribuicaoFaixa').getContext('2d');
  	
  	if (MY_CHART_DISTRIBUICAO_POR_FAIXA != null)
  	{
		MY_CHART_DISTRIBUICAO_POR_FAIXA.destroy();
	}
  	
	MY_CHART_DISTRIBUICAO_POR_FAIXA = new Chart(ctx, 
	{
		type: 'line',
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
		      mode: 'index',
		      intersect: false,
		    },
		    stacked: false,
		    plugins: {
		      title: {
		        display: true,
		        text: 'Distribuição por faixa de rolagem'
		      }
		    },
		    scales: {
		      x: {
		        stacked: false
		      },
		      y: {
		        stacked: false,
		        beginAtZero: true
		      }
		    }
  		},
    });
}

function ObterGraficoDistribuicaoPorFaixa()
{
	var municipio = document.getElementById("selectMunMapa").value;
	var regiao = document.getElementById("selectRegMapa").value;
  	var equipamento = document.getElementById("selEquipamento").value;
  	var tipoRelatorio = document.getElementById("selTipoRelatorio").value;
  	var dataIni = $("#dataInicio").val();
  	var dataFim = $("#dataFim").val();
	
	var dataString = "acao=distribuicaoPorFaixa" + 
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
				CriarGraficoDistribuicaoPorFaixa();
				TratarResultado(jqXHR.responseText, MY_CHART_DISTRIBUICAO_POR_FAIXA);
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
