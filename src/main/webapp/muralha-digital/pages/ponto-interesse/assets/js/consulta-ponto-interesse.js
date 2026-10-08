$(document).ready(function ()
{
	console.log("Iniciando pagina");
	initList();
});

function initList() 
{	
	console.log("initList: Funcao chamada");
	obterPontosDeInteresse();
}

function obterPontosDeInteresse()
{
	console.log("obterPontosDeInteresse: Funcao chamada");
	
	var urlPesquisa = urlRoot + "MuralhaDigital/PontoInteresse";
	var dataString = "acao=obterPontosInteresse";
	
	$.ajax({
		type: "GET",
		url: urlPesquisa,
		data: dataString,
		dataType: "xml",
		
		success: function(data, textStatus, jqXHR)
		{
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);
			
			console.log(event);
			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				popTabela( event );
			} else {
				//WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
			
			console.log("obterPontosDeInteresse: Sucesso");
		},
		
		error: function(jqXHR, textStatus, errorThrown)
		{
			console.log("obterPontosDeInteresse: Erro");
		},
		
		beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
	});
}

function popTabela(event)
{
	var xmlDoc = $.parseXML(event);
	var $xml = $(xmlDoc);
	
	console.log(xmlDoc);
	
	$xml.find('PontoInteresse').each(function()
	{
		var $item = $(this);
		
		var nome = $item.find('nome').text();
		var tipo = $item.find('descricao').text();
		var lati = $item.find('latitude').text();
		var long = $item.find('longitude').text();
		var equi = $item.find('equipamentos').text();
		var codOrgao = $item.find('codigosEquipamentos').text();
		
		console.log(nome + "-" + tipo + "-" + equi + "-" + codOrgao);

		var tableRef = document.getElementById("tabela").getElementsByTagName('tbody')[0];
		var row   = tableRef.insertRow(tableRef.rows.length);
		
		row.insertCell(0).innerHTML = "<small>" + nome + "</small>";
		row.insertCell(1).innerHTML = "<small>" + tipo + "</small>";
		row.insertCell(2).innerHTML = "<small>" + lati + "</small>";
		row.insertCell(3).innerHTML = "<small>" + long + "</small>";
		row.insertCell(4).innerHTML = "<small>" + equi + "</small>";
		row.insertCell(5).innerHTML = "<small>" + codOrgao + "</small>";
		
		$('input#txt_consulta').quicksearch('table#tabela tbody tr');
		
	});
}