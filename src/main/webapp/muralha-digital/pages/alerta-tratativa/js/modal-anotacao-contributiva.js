function CadastrarAnotacao()
{
	var anotacaoContributiva = document.getElementById("anotacaoAlertaCad").value;
	
	var dataString = "idAlerta=" + ID_ALERTA +
						"&anotacaoContributiva=" + anotacaoContributiva;
        
    var url = urlRoot + "MuralhaDigital/Alerta/AnotacaoContributiva";

    $.ajax({
        type: 		"POST",
        url: 		url,
        data: 		dataString,
        dataType:	"xml",
           
        success: 	function( data, textStatus, jqXHR) 
        {
        	var event = jqXHR.responseText;
        	var xmlDoc = $.parseXML( event );
			var $xml = $(xmlDoc);
    			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				SuccessModalCsx_E_TimeOut_8000ms(msgResposta);
				LimparCamposAnotacao();
				ObterAnotacaoContributiva();
			} else {
				WarningModalCsx_E_TimeOut_8000ms(msgResposta);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertModalCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });
}

function CadastrarAnotacaoComTexto(texto) {
	document.getElementById("anotacaoAlertaCad").value = texto;
	CadastrarAnotacao();
}

function LimparCamposAnotacao()
{
	var anotacaoAlertaCad = document.getElementById("anotacaoAlertaCad");
	
	if (anotacaoAlertaCad)
		anotacaoAlertaCad.value = "";
		
}

function ObterAnotacaoContributiva()
{
	LimparTabelaAnotacaoContributiva();
	
	var dataString = "acao=obterAnotacaoContributivaPorIdAlerta" +
						"&idAlerta=" + ID_ALERTA;
	
	var url = urlRoot + "MuralhaDigital/Alerta/AnotacaoContributiva";
	
//	console.log('ObterAnotacaoContributiva():: URL: ' + url + ' --> ' + dataString);
	
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
				PopulaTabelaAnotacaoContributiva(jqXHR.responseText);
			} else {
				WarningModalCsx_E_TimeOut_8000ms(msgResposta);
			}
        },
		
	    error: function(e, b, error) {
			AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
	        console.log("Erro ao obter Anotação Contributiva por Id Alerta." + e.respone);
	    },
		async: false
	});
}

function LimparTabelaAnotacaoContributiva()
{
	var tableHeaderRowCount = 1;
	var table = document.getElementById("tabelaAnotacaoContributiva");
	var rowCount = table.rows.length;
	
	for (var i = tableHeaderRowCount; i < rowCount; i++) 
	    table.deleteRow(tableHeaderRowCount);
		
}

function PopulaTabelaAnotacaoContributiva(event)
{	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);

	var table = document.getElementById("tabelaAnotacaoContributiva");
	var tableRef = table.getElementsByTagName('tbody')[0];
	
	$xml.find('AnotacaoContributiva').each(function()
	{
		var $item = $(this);
		
		var id = $item.find('id').text();
//		var idAlerta = $item.find('idAlerta').text();
		var descricao = $item.find('descricao').text();
		var dataCadastroFormatada = $item.find('dataCadastroFormatada').text() + " " + $item.find('horaCadastroFormatada').text();
//		var idUsuario = $item.find('idUsuario').text();
//		var usuario = $item.find('usuario').text();
		var nomeUsuario = $item.find('nomeUsuario').text();

		var row = tableRef.insertRow(tableRef.rows.length);
		row.insertCell(0).innerHTML = "<small>" + descricao + "</small>";
		row.insertCell(1).innerHTML = "<small>" + dataCadastroFormatada + "</small>";
		row.insertCell(2).innerHTML = "<small>" + nomeUsuario + "</small>";	
	});
}

function DesabilitarControlesModalAnotacao()
{
	if( DESCARTADO || OCORRENCIA_FINALIZADA || (ALERTA_VINCULADO && !OCORRENCIA_GERADA) )
	{
		document.getElementById("btnAdicionarAnotacaoModal").disabled = true;
		document.getElementById("btnLimparAnotacaoModal").disabled = true;
		document.getElementById("anotacaoAlertaCad").disabled = true;
	}
}