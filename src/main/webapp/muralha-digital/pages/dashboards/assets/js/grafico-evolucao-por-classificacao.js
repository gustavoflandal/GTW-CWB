var MY_CHART_EVOLUCAO_CLASSIFICACAO = null;

$(document).ready(function() 
{
	CriarGraficoCompEvolucaoClassificacao();
});

function CriarGraficoCompEvolucaoClassificacao()
{
	var ctx = document.getElementById('chartEvolucaoClassificacao').getContext('2d');
	
	var tipoRelatorio = document.getElementById("selTipoRelatorio").value;

	var tituloRelatorio = 'Evolução';
	if (tipoRelatorio == 1)
		tituloRelatorio = 'Evolução por classificação';
	else if (tipoRelatorio == 2)
		tituloRelatorio = 'Evolução por enquadramento';
	else if (tipoRelatorio == 3)
		tituloRelatorio = 'Evolução por tipo de problema';
	
	if (MY_CHART_EVOLUCAO_CLASSIFICACAO != null)
  	{
		MY_CHART_EVOLUCAO_CLASSIFICACAO.destroy();
	}
	
	MY_CHART_EVOLUCAO_CLASSIFICACAO = new Chart(ctx,
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
		        text: tituloRelatorio
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

function ObterGraficoEvolucaoClassificacao()
{
	var municipio = document.getElementById("selectMunMapa").value;
	var regiao = document.getElementById("selectRegMapa").value;
  	var equipamento = document.getElementById("selEquipamento").value;
  	var tipoRelatorio = document.getElementById("selTipoRelatorio").value;
  	var dataIni = $("#dataInicio").val();
  	var dataFim = $("#dataFim").val();
	
	var dataString = "acao=comparativoEvolucaoClassificacao" + 
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
				CriarGraficoCompEvolucaoClassificacao();
				TratarResultado(jqXHR.responseText, MY_CHART_EVOLUCAO_CLASSIFICACAO);
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
