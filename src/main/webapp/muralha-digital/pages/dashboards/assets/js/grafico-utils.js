var POSSUI_DATASET_LABEL = false;
var BAR_COLORS = [
  "#00cc66",
  "#ff9900",
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
  "#b76d47"
];


function TratarResultado(event, myChart)
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
	
	PopularGrafico(labels, dataset, myChart);
}

function PopularGrafico(labels, dataset, myChart)
{
	for (var i = 0; i < labels.length; i++)
	{
		myChart.data.labels.push(labels[i]);
	}
	
	for (var i = 0; i < dataset.length; i++)
	{
		var data = dataset[i];
		if ((i+1) > myChart.data.datasets.length)
		{
			myChart.data.datasets.push({
			  	data: []
			});
		}
		
		if (POSSUI_DATASET_LABEL)
		{
			myChart.data.datasets[i].backgroundColor = BAR_COLORS[i];
			myChart.data.datasets[i].borderColor = BAR_COLORS[i];
		}
		else
		{	
			myChart.data.datasets[i].backgroundColor = BAR_COLORS;
			myChart.data.datasets[i].borderColor =BAR_COLORS;
		}
		
		for (var j = 0; j < data.length; j++)
		{
			if (POSSUI_DATASET_LABEL && j == 0)
			{
				myChart.data.datasets[i].label = data[j];
			}
			else
			{	
				myChart.data.datasets[i].data.push(data[j]);
			}
		}
	}
	
	myChart.update();
}
