var TITULO_GRAFICO = "";
var GRAFICO_BARRA = false;
var GRAFICO_PIZZA = false;
var GRAFICO_LINHA = false;
var GRAFICO_MAPA_DISPOSITIVO = false;
var GRAFICO_BARRA_MAPA = [];
var GRAFICO_LINHA_MAPA = [];
var ELEMENTO_GRAFICO_MAPA = "";
var POSSUI_DATASET_LABEL = false;
var MY_CHART = null;
var MY_CHART_MAPAS = [];
var CHART_MAPAS_CARREGADO = [];
var LABELS = [];
var BAR_COLORS = [
  "#b91d47",
  "#00aba9",
  "#2b5797",
  "#e8c3b9",
  "#1e7145",
  "#2b5655",
  "#00ad86",
  "#b35d47",
  "#00ab14",
  "#1e7196",
  "#e8j7b9",
  "#00e148",
  "#2b8855",
  "#2b5702",
  "#00cb86",
  "#b35d22",
  "#e8j7b9",
  "#b76d47",
  "#1e7465",
  "#55ad86"
];

var SCALES = ["a1","b1","c1","d1","e1","f1","g1","h1","i1","j1"];



var DATALABELS =
{
    formatter: (value, ctx) => {
        let sum = 0;
        let dataArr = ctx.chart.data.datasets[0].data;
        dataArr.map(data => {
            sum += data;
        });
        let percentage = (value*100 / sum).toFixed(1)+"%";
        return percentage;
    },
    color: '#fff',
};

function CriarGraficoBarra(tituloGrafico)
{
	GRAFICO_BARRA = true;
	TITULO_GRAFICO = tituloGrafico;
	
	var ctx = document.getElementById('chartGrafico').getContext('2d');
  	
  	if (MY_CHART != null)
  	{
		MY_CHART.destroy();
	}
  	
	MY_CHART = new Chart(ctx, 
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
      	options: {
			responsive: true,
	    	interaction:
	    	{
	      		mode: 'index',
	      		intersect: false,
	    	},
	    	stacked: false,
	    	plugins:
	    	{
	      		title:
	      		{
	        		display: true,
	        		text: TITULO_GRAFICO
	      		}
	    	},
		    scales:
		    {
		    	x:
	    		{
		        	stacked: true,
		      	},
		      	y:
		      	{
		        	stacked: true
		      	}
		    }
		},
    });
}

function CriarGraficoPizza(tituloGrafico)
{
	GRAFICO_PIZZA = true;
	TITULO_GRAFICO = tituloGrafico;
	
	var ctx = document.getElementById('chartGrafico').getContext('2d');
  	
  	if (MY_CHART != null)
  	{
		MY_CHART.destroy();
	}
  	
	MY_CHART = new Chart(ctx, 
	{
		type: 'pie',
	    data: 
		{
			labels: [],
	        datasets: [{ 
				data: []
          	}]
	    },
		options: 
		{
			responsive: true,
		    tooltips: {
			    enabled: false
			},
  			plugins:
		    {
				datalabels: DATALABELS,
				title:
				{
		        	display: true,
		        	text: TITULO_GRAFICO
		      	}
		    },
		    scales:
		    {
		    	x: { stacked: true, },
		      	y: { stacked: true }
		    }
		},
		plugins: [ChartDataLabels],
 	});
}

function CriarGraficoLinha(tituloGrafico)
{
	GRAFICO_LINHA = true;
	TITULO_GRAFICO = tituloGrafico;
	
	var ctx = document.getElementById('chartGrafico').getContext('2d');
  	
  	if (MY_CHART != null)
  	{
		MY_CHART.destroy();
	}
  	
	MY_CHART = new Chart(ctx, 
	{
		type: 'line',
	    data:
      	{
        	labels: [],
        	datasets: [{ 
            	data: [],
            	label: []
          	}]
      	},
      	options: {
			responsive: true,
	    	interaction:
	    	{
	      		mode: 'index',
	      		intersect: false,
	    	},
	    	stacked: false,
	    	plugins:
	    	{
	      		title:
	      		{
	        		display: true,
	        		text: TITULO_GRAFICO
	      		}
	    	},
		    scales:
		    {
		    	x:
	    		{
		        	stacked: false,
		      	},
		      	y:
		      	{
		        	stacked: false
		      	}
		    }
		},
    });
}

function CriarGraficoLinhaParaMapa(elementoGrafico, tituloGrafico)
{
	GRAFICO_MAPA_DISPOSITIVO = true;
	ELEMENTO_GRAFICO_MAPA = elementoGrafico;
	GRAFICO_LINHA_MAPA[ELEMENTO_GRAFICO_MAPA] = true;
	TITULO_GRAFICO = tituloGrafico;
	
	var ctx = document.getElementById(ELEMENTO_GRAFICO_MAPA).getContext('2d');
  	
  	if (MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA] != null)
  	{
//		console.log("CriarGraficoLinhaParaMapa --> destruindo grafico " + ELEMENTO_GRAFICO_MAPA);
		MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].destroy();
	}
  	
	MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA] = new Chart(ctx, 
	{
		type: 'line',
	    data:
      	{
        	labels: [],
        	datasets: [{ 
            	data: [],
            	label: [],
            	yAxisID: 'a1'
          	}]
      	},
      	options: {
			responsive: true,
	    	interaction:
	    	{
	      		mode: 'index',
	      		intersect: false,
	    	},
	    	stacked: false,
	    	plugins:
	    	{
	      		title:
	      		{
	        		display: true,
	        		text: TITULO_GRAFICO
	      		}
	    	},
		    scales:
		    {
		    	a1:
	    		{
					stacked: false,
		        	type: 'linear',
        			display: true,
        			position: 'left',
        			ticks: {
		                color: '#000000',
		                precision: 0
		            }
		      	},
		      	b1:
		      	{
					stacked: false,
		        	type: 'linear',
        			display: true,
        			position: 'right',
        			ticks: {
		                color: '#000000',
		                precision: 0
		            }
		      	}
		    }
		},
    });
    
    CHART_MAPAS_CARREGADO[ELEMENTO_GRAFICO_MAPA] = true;
}

function CriarGraficoBarraParaMapa(elementoGrafico, tituloGrafico)
{
	GRAFICO_MAPA_DISPOSITIVO = true;
	ELEMENTO_GRAFICO_MAPA = elementoGrafico;
	GRAFICO_BARRA_MAPA[ELEMENTO_GRAFICO_MAPA] = true;
	TITULO_GRAFICO = tituloGrafico;
	
	var ctx = document.getElementById(ELEMENTO_GRAFICO_MAPA).getContext('2d');
  	
  	if (MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA] != null)
  	{
//		console.log("CriarGraficoBarraParaMapa --> destruindo grafico " + ELEMENTO_GRAFICO_MAPA);
		MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].destroy();
	}
  	
	MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA] = new Chart(ctx, 
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
      	options: {
			responsive: true,
	    	interaction:
	    	{
	      		mode: 'index',
	      		intersect: false,
	    	},
	    	stacked: false,
	    	plugins:
	    	{
	      		title:
	      		{
	        		display: true,
	        		text: TITULO_GRAFICO
	      		}
	    	},
		    scales:
		    {
		    	a1:
	    		{
					stacked: true,
		        	type: 'linear',
        			display: true,
        			position: 'left',
        			ticks: {
		                color: '#000000',
		                precision: 0
		            }
		      	},
		      	b1:
		      	{
					stacked: true,
		        	type: 'linear',
        			display: true,
        			position: 'right',
        			ticks: {
		                color: '#000000',
		                precision: 0
		            }
		      	}
		    }
		},
    });
    
    CHART_MAPAS_CARREGADO[ELEMENTO_GRAFICO_MAPA] = true;
}

function stringToDate(_date,_format,_delimiter)
{
            var formatLowerCase=_format.toLowerCase();
            var formatItems=formatLowerCase.split(_delimiter);
            var dateItems=_date.split(_delimiter);
            var monthIndex=formatItems.indexOf("mm");
            var dayIndex=formatItems.indexOf("dd");
            var yearIndex=formatItems.indexOf("yyyy");
            var month=parseInt(dateItems[monthIndex]);
            month-=1;
            var formatedDate = new Date(dateItems[yearIndex],month,dateItems[dayIndex]);
            return formatedDate;
}
function parseTime(date, t ) {
   var d = date;
   var time = t.match( /(\d+)(?::(\d\d))?\s*(p?)/ );
   d.setHours( parseInt( time[1]) + (time[3] ? 12 : 0) );
   d.setMinutes( parseInt( time[2]) || 0 );
   return d;
}

function obterDataHora(str_data_hora) {
	var str_data = str_data_hora.substring(0,10);
	var str_hora = str_data_hora.substring(11,16);
	var data = stringToDate(str_data,"dd/MM/yyyy","/");
	var data_hora = parseTime(data,str_hora);
  return data_hora;
}

function obterDiferencaMinutos(str_data_hora_ini,str_data_hora_fim) {
var data_hora_ini = obterDataHora(str_data_hora_ini);
var data_hora_fim = obterDataHora(str_data_hora_fim);
var diff_ms = data_hora_fim - data_hora_ini;
var diff_min = (diff_ms/1000)/60;
return diff_min;
}
function ValidarObterDadosGrafico(acao,tempo_maximo,unidade)
{
	var dataIni = TratarDataHora($("#dataInicio").find("input").val());
  	var dataFim = TratarDataHora($("#dataFim").find("input").val());
	
	var diff_min =   obterDiferencaMinutos(dataIni,dataFim);
	if (diff_min > tempo_maximo)
	{
		WarningCsx_E_TimeOut_8000ms("Intervalo Máximo entre Inicio e Fim deve ser de " + unidade);
	}
	else 
	{
		ObterDadosGrafico(acao);
	}	
}

function ObterDadosGrafico(acao)
{
	var dataIni = TratarDataHora($("#dataInicio").find("input").val());
  	var dataFim = TratarDataHora($("#dataFim").find("input").val());
  	var equipamento = document.getElementById("selEquipamento").value;
  	var tipoPeriodo = document.getElementById("selTipo");
  	var idTipoPeriodo = "1";
  	if (tipoPeriodo)
  		idTipoPeriodo = tipoPeriodo.value;
	
	var dataString = "acao=" + acao + 
						"&dataIni=" + dataIni + 
						"&dataFim=" + dataFim +
						"&equipamento=" + equipamento + 
						"&tipoRelatorio="+idTipoPeriodo;
	
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
			
			if (sucesso) {
				TratarResultado(jqXHR.responseText);
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },
		
	    error: function(e, b, error) {
	        console.log("Erro ao obter dados do gráfico." + e.respone);
	    },
		async: false
	});
}

function ObterDadosGraficoMapas(acao, dataIni, dataFim, equipamento, elementoSpinner)
{
	var dataString = "acao=" + acao + 
						"&dataIni=" + dataIni + 
						"&dataFim=" + dataFim +
						"&equipamento=" + equipamento + 
						"&tipoRelatorio=1";
	
	var url = urlRoot + "MuralhaDigital/Grafico";
	
	DesabilitarBotoesGraficos();
	ExibirSpinner(elementoSpinner);
	
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
			
			if (sucesso) {
				TratarResultado(jqXHR.responseText);
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },
	
        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	console.log("Erro ao obter dados do gráfico." + e.respone);
			OcultarSpinner(elementoSpinner);
			HabilitarBotoesGraficos();
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus)
        {
			OcultarSpinner(elementoSpinner);
			HabilitarBotoesGraficos();
		}
	});
}

function TratarResultado(event)
{	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);

	var labels = [];
	
	$xml.find('labels').each(function()
	{
		var $label = $(this);
		labels.push($label.text());
	});
	
	var dataset =[];
	var cont = 0;
	
	$xml.find('datasets').each(function()
	{
		var $dataset = $(this);
		var valores  = [];

		var labelDataset = $dataset.find('labelDataset').text();
		POSSUI_DATASET_LABEL = (!labelDataset == ''); 
		
		if (POSSUI_DATASET_LABEL)
			valores.push(labelDataset);
		
		$dataset.find('valor').each(function()
		{
			var $valor = $(this);
			valores.push(parseInt($valor.text()));
		});
		
		dataset[cont] = valores;
		cont++;
	});
	
	if (GRAFICO_MAPA_DISPOSITIVO)
		PopularGraficoMapas(labels, dataset);
	else
		PopularGrafico(labels, dataset);
}

function PopularGrafico(labels, dataset)
{
	// Se o gráfico já existir, destroi o objeto e cria novamente.
	if (GRAFICO_BARRA)
		CriarGraficoBarra(TITULO_GRAFICO);
	else if (GRAFICO_PIZZA)
		CriarGraficoPizza(TITULO_GRAFICO);
	else if (GRAFICO_LINHA)
		CriarGraficoLinha(TITULO_GRAFICO);
		
	for (var i = 0; i < labels.length; i++)
	{
		MY_CHART.data.labels.push(labels[i]);
	}
	
	for (var i = 0; i < dataset.length; i++)
	{
		var data = dataset[i];
		if ((i+1) > MY_CHART.data.datasets.length)
		{
			MY_CHART.data.datasets.push({
			  	data: []
			});
		}
		
		if (POSSUI_DATASET_LABEL)
		{
			MY_CHART.data.datasets[i].backgroundColor = BAR_COLORS[i];
			MY_CHART.data.datasets[i].borderColor = BAR_COLORS[i];
		}
		else
		{	
			MY_CHART.data.datasets[i].backgroundColor = BAR_COLORS;
			MY_CHART.data.datasets[i].borderColor =BAR_COLORS;
		}
		
		for (var j = 0; j < data.length; j++)
		{
			if (POSSUI_DATASET_LABEL && j == 0)
			{
				MY_CHART.data.datasets[i].label = data[j];
			}
			else
			{	
				MY_CHART.data.datasets[i].data.push(data[j]);
			}
		}
	}
	
	MY_CHART.update();
}

function PopularGraficoMapas(labels, dataset)
{
	// Se o gráfico já existir, destroi o objeto e cria novamente.
	if (GRAFICO_BARRA_MAPA[ELEMENTO_GRAFICO_MAPA])
		CriarGraficoBarraParaMapa(ELEMENTO_GRAFICO_MAPA, TITULO_GRAFICO);
	else if (GRAFICO_LINHA_MAPA[ELEMENTO_GRAFICO_MAPA])
		CriarGraficoLinhaParaMapa(ELEMENTO_GRAFICO_MAPA, TITULO_GRAFICO);
	
		
	for (var i = 0; i < labels.length; i++)
	{
		MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].data.labels.push(labels[i]);
	}
	
	for (var i = 0; i < dataset.length; i++)
	{
		var data = dataset[i];
		if ((i+1) > MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].data.datasets.length)
		{
			MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].data.datasets.push({
			  	data: []
			});
		}
		
		if (POSSUI_DATASET_LABEL)
		{
			MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].data.datasets[i].backgroundColor = BAR_COLORS[i];
			MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].data.datasets[i].borderColor = BAR_COLORS[i];
			MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].data.datasets[i].yAxisID = SCALES[i];
			MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].options.scales[SCALES[i]].ticks.color = BAR_COLORS[i]
		}
		else
		{	
			MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].data.datasets[i].backgroundColor = BAR_COLORS;
			MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].data.datasets[i].borderColor = BAR_COLORS;
		}
		
		for (var j = 0; j < data.length; j++)
		{
			if (POSSUI_DATASET_LABEL && j == 0)
			{
				MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].data.datasets[i].label = data[j];
			}
			else
			{	
				MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].data.datasets[i].data.push(data[j]);
			}
		}
	}
	
	MY_CHART_MAPAS[ELEMENTO_GRAFICO_MAPA].update();
}
