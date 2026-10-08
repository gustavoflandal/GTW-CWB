
var MIN_VALOR	= -1;
var MAX_VALOR	= -1;

var DIF				= 0;
var ESCALA_MED		= 0;
var	ESCALA_VERMELHO = 0;
var	ESCALA_LARANJA	= 0;
var	ESCALA_AMARELO	= 0;
var	ESCALA_LIMAO	= 0;
var	ESCALA_VERDE	= 0;

var VERMELHO 	= '#F09376';
var LARANJA 	= '#FCAB1B';
var AMARELO 	= '#F2E810';
var LIMAO 		= '#AFFC31';
var VERDE 		= '#1AA130';


function ObterCalendarioIntensidade()
{
	LimparTabelaCalendarioIntensidade();
	
	var municipio = document.getElementById("selectMunMapa").value;
	var regiao = document.getElementById("selectRegMapa").value;
  	var equipamento = document.getElementById("selEquipamento").value;
  	var tipoRelatorio = document.getElementById("selTipoRelatorio").value;
  	var dataIni = $("#dataInicio").val();
  	var dataFim = $("#dataFim").val();
	
	var dataString = "acao=obterCalendarioIntensidade" + 
						"&dataIni=" + dataIni + 
						"&dataFim=" + dataFim +
						"&equipamento=" + equipamento +
						"&municipio=" + municipio +
						"&regiao=" + regiao +
						"&tipoRelatorio=" + tipoRelatorio
	
	var url = urlRoot + "MuralhaDigital/Dashboard";
	
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
				PopulaTabelaCalendarioIntensidade(jqXHR.responseText);
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

function LimparTabelaCalendarioIntensidade()
{
	var tableHeaderRowCount = 1;
	var table = document.getElementById("tabelaCalendarioIntensidade");
	var rowCount = table.rows.length;
	
	for (var i = tableHeaderRowCount; i < rowCount; i++) 
	    table.deleteRow(tableHeaderRowCount);
		
}

function PopulaTabelaCalendarioIntensidade(event)
{	
	MIN_VALOR = -1;
	MAX_VALOR = -1;
	
	LimparTabelaCalendarioIntensidade();
	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);

	var table = document.getElementById("tabelaCalendarioIntensidade");
	var tableRef = table.getElementsByTagName('tbody')[0];
	
	var values = [];
	
	$xml.find('CalendarioIntensidade').each(function()
	{
		var $item = $(this);
		
		var semana = $item.find('semana').text();
		
		var domingo = $item.find('domingo').text();
		var segunda = $item.find('segunda').text();
		var terca = $item.find('terca').text();
		var quarta = $item.find('quarta').text();
		var quinta = $item.find('quinta').text();
		var sexta = $item.find('sexta').text();
		var sabado = $item.find('sabado').text();
		
		if (domingo.split(";").length > 1) values.push(parseInt(domingo.split(";")[1]));
		if (segunda.split(";").length > 1) values.push(parseInt(segunda.split(";")[1]));
		if (terca.split(";").length > 1) values.push(parseInt(terca.split(";")[1]));
		if (quarta.split(";").length > 1) values.push(parseInt(quarta.split(";")[1]));
		if (quinta.split(";").length > 1) values.push(parseInt(quinta.split(";")[1]));
		if (sexta.split(";").length > 1) values.push(parseInt(sexta.split(";")[1]));
		if (sabado.split(";").length > 1) values.push(parseInt(sabado.split(";")[1]));

		var row = tableRef.insertRow(tableRef.rows.length);
		
		row.insertCell(0).innerHTML = domingo;
		row.insertCell(1).innerHTML = segunda;
		row.insertCell(2).innerHTML = terca;
		row.insertCell(3).innerHTML = quarta;
		row.insertCell(4).innerHTML = quinta;
		row.insertCell(5).innerHTML = sexta;
		row.insertCell(6).innerHTML = sabado;
	
	});
	
//	console.log(values.length);
	setValorMinMax(values);
	
	for (var i = 0; i < tableRef.rows.length; i++)
	{
		for (j = 0; j <= 6; j++)
		{
			var cell = tableRef.rows[i].cells[j];
			
			if (cell.innerHTML.split(";").length > 1)
			{
				datac = cell.innerHTML.split(";")[0];
				valor = parseInt(cell.innerHTML.split(";")[1]);
				var str =  "<small>" + datac + "</small><br><small><strong>" + new Intl.NumberFormat('pt-BR').format(valor) + "</strong></small>";
				cell.innerHTML = str;
				
				if 		(valor <= ESCALA_VERMELHO) cell.style.backgroundColor = VERMELHO;
				else if (valor <= ESCALA_LARANJA) cell.style.backgroundColor = LARANJA;
				else if (valor <= ESCALA_AMARELO) cell.style.backgroundColor = AMARELO;
				else if (valor <= ESCALA_LIMAO) cell.style.backgroundColor = LIMAO;
				else cell.style.backgroundColor = VERDE;
			}
		}
	}
	
	 $('.table tr > td').addClass('text-dark');
}

function setValorMinMax(values)
{
	for (var i = 0; i <= values.length; i++) 
	{
		var valor = values[i];
		
		if (MIN_VALOR == -1) MIN_VALOR = valor;
		else
		{
			if (MIN_VALOR > valor) MIN_VALOR = valor;
		}
		
		if (MAX_VALOR == -1) MAX_VALOR = valor;
		else
		{
			if (MAX_VALOR < valor) MAX_VALOR = valor;
		}
	}	
	
//	console.log('Valor Celulas:: Minimo: ' + MIN_VALOR + ' Maximo: ' + MAX_VALOR);
	
	DIF = MAX_VALOR - MIN_VALOR;
	
	ESCALA_MED = DIF / 5;
	
	ESCALA_VERMELHO = MIN_VALOR + ESCALA_MED;
	ESCALA_LARANJA	= ESCALA_MED + ESCALA_VERMELHO;
	ESCALA_AMARELO	= ESCALA_MED + ESCALA_LARANJA;
	ESCALA_LIMAO	= ESCALA_MED + ESCALA_AMARELO;
	ESCALA_VERDE	= ESCALA_MED + ESCALA_LIMAO;
	
//	console.log('Escalas definidas:: ' + ESCALA_VERMELHO + '-' + ESCALA_LARANJA + '-' + ESCALA_AMARELO + '-' + ESCALA_LIMAO + '-' + ESCALA_VERDE);
}
