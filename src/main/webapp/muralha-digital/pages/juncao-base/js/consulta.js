/*
// Roda ao iniciar a pagina
$(document).ready(function ()
{
	console.log("[$().ready] -> Função chamada");
});
*/

function importaCSV()
{
	if (document.getElementById("inputImportaCsv").value.length == 0)
	{
		document.getElementById("divStatusCSV").innerHTML = "<strong>Favor selecionar um arquivo CSV</strong>";
	}
	else
	{
		document.getElementById("divStatusCSV").innerHTML = "Carregando...";
		
		var urlPesquisa = urlRoot + "MuralhaDigital/JuncaoBase";
		
		$.ajax({
	        type: 		"GET",
	        url: 		urlPesquisa,
	        data: 		"importaCSV",
	        dataType:	"text",
	           
	        //if received a response from the server
	        success: 	function( data, textStatus, jqXHR) 
	        {
				console.log("[importaCSV()] -> Ajax - sucesso");
	        	
				var event = jqXHR.responseText;
	        	var xmlDoc = $.parseXML( event );
				var $xml = $(xmlDoc);
	    			
				var sucesso = $xml.find('sucesso').text();
				var msgResposta = $xml.find('msgResposta').text();
				
				console.log("[importaCSV()] -> OK - " + msgResposta);
				
				sucesso = (sucesso === 'true' || sucesso === '');
				
	        	if (sucesso) {
					//console.log("success - response: " + textStatus);
					document.getElementById("divStatusCSV").innerHTML = "Arquivo carregado.";
				} else {
					WarningCsx_E_TimeOut_8000ms(msgResposta);
					document.getElementById("divStatusCSV").innerHTML = "Excedido o tempo da requisição.";
				}
	        },
	
	        error: 		function(jqXHR, textStatus, errorThrown)
	        {
				console.log("[importaCSV()] -> Ajax - erro");
	        	document.getElementById("divStatusCSV").innerHTML = "Problema ao carregar arquivo.";
	//        	alert("(executaPesquisa) Erro ao processar requisição ao servidor!!");
	        },
	        beforeSend: function(jqXHR, settings){},
	        complete: 	function(jqXHR, textStatus){}
	    });
	}
}
