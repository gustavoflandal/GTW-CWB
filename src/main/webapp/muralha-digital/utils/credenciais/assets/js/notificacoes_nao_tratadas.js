var LEMBRETE_NAO_VISUALIZADO = 0;
var LEMBRETE_NAO_TRATADO = 1; 
var lembrete_tmp = -1;

$(document).ready(function() 
{

	ObterNotificacoesNaoTratadas();
	
});


function ObterNotificacoesNaoTratadas()
{
	$.ajax(
    {
        type: 		"GET",
        url: 		" /MuralhaDigital/Alerta",
        dataType:	"xml",
       	data: 		"acao=obterAlertasNaoTratadas",
        success: 	function( data, textStatus, jqXHR) 
        {
        	ProcessaDados( jqXHR.responseText );            	       	
    		return false;
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	console.log("(ObterNotificacoesNaoTratadas) Erro ao processar requisição ao servidor)!!");
        }
    });    			
}


function ProcessaDados(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);

	var lista = $xml.find('alertas').find('listaAlertas');
	var qtde = 0;
	
	var qtdeAlertasNaoTratados = document.getElementById("qtdeAlertasNaoTratados");
	
	lista.find('alerta').each(function()
	{
		var $alerta = $(this);
		
		if (qtde == 0)
		{
			qtde = $alerta.find('totalRegistros').text();
			if (qtdeAlertasNaoTratados)
				qtdeAlertasNaoTratados.innerHTML = qtde;
			//console.log("qtde alertas: " + qtde);
		}
		
		var idAlerta 				= $alerta.find('id').text();
		var tipoAlerta				= $alerta.find('tipoAlerta').text();
		var dataAlerta				= $alerta.find('dtAlertaStr').text();
		var placa					= $alerta.find('placaVeiculo').text();
		var lembrete				= $alerta.find('lembrete').text();
		
		var texto = tipoAlerta + " - " + placa + ' - ' + dataAlerta;
				
		//console.log("Alerta: " + texto);
		
		PopulaLembretes(idAlerta, texto, lembrete);
				
	});
	
//	console.log("qtde alertas: " + qtde);
//	document.getElementById("qtdeAlertasNaoTratados").innerHTML = qtde;
}

function PopulaLembretes(id, texto, lembrete)
{
	if (lembrete_tmp != lembrete)
	{
		TituloLembrete(lembrete);
		lembrete_tmp = lembrete;	
	}	
	
    var a = document.createElement('a');
	var texto = document.createTextNode(texto);
	a.appendChild(texto);
    a.setAttribute('id', 'btnLogoutxx');
	a.setAttribute('name', 'btnLogoutxx');	
    a.href 	= "/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=" + id;
    a.classList.add("dropdown-item");
   	a.style.backgroundColor = "gray";
    a.style.color = "white";

    document.getElementById("navbarDropdown_lembretes").appendChild(a);
}	

function TituloLembrete(lembrete)
{
	var texto = 'ERRO';
	if(lembrete == LEMBRETE_NAO_VISUALIZADO)
		texto = document.createTextNode('ALERTAS PENDENTES (Não visualizado)');
	else 
		texto = document.createTextNode('ALERTAS PENDENTES (Não tratado)');
		
	var a = document.createElement('a');
	a.appendChild(texto);
    a.classList.add("dropdown-item");
	a.style.backgroundColor = "orange";
    a.style.color = "white";
	a.style.fontWeight="bold";

    document.getElementById("navbarDropdown_lembretes").appendChild(a);
}
