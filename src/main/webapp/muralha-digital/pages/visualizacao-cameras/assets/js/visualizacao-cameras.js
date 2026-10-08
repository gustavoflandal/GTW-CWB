
var dispositivoSelecionado		= 0;

$(document).ready(function() 
{	
//	console.log('Iniciando tela de Veiculos em Tempo Real (v1)');	
	ObterListaDispositivos();	
});		


function ObterListaDispositivos()
{
	var dataStringPesquisa = "acao=obterListaEquipamentosSentido";
    var urlPesquisa = urlRoot + "MuralhaDigital/DispositivoEquipamento";
	
	$.ajax(
    {
        type: 		"GET",
        url: 		urlPesquisa,
        data: 		dataStringPesquisa,
        dataType:	"xml",
       
        //if received a response from the server
        success: 	function( data, textStatus, jqXHR) 
        {

			ProcessaDadosDispositivos( jqXHR.responseText );
    		return false;
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	console.log("(obterDadosRadares) Erro ao processar requisição ao servidor)!!");
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
    });    			
}


function ProcessaDadosDispositivos(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);			
	var lista = $xml.find('DispositivosEquipamentos').find('ListaDispositivos');
	
	$('#selEquipamento').empty();
	
	$('#selEquipamento').append($('<option>', {
	    value: 0,
	    text: "--Equipamento--"
	}));
	
	lista.find('Dispositivo').each(function()
	{
		
		var $dispositivo = $(this);
		
		var descLocal 				= $dispositivo.find('descDispositivo').text();
		var serieEquipamento 		= $dispositivo.find('serieEquipamento').text();
		var pistaSentido 			= $dispositivo.find('pistaSentido').text();
		var codigoEquipamento 		= ( ($dispositivo.find('codigosEquipamentos').text() == null || $dispositivo.find('codigosEquipamentos').text() == '') ? "N/D" : $dispositivo.find('codigosEquipamentos').text() );
//		var textoCombo 				= (codigoEquipamento + " - " + serieEquipamento + " - " + descLocal);
		var textoCombo 				= (serieEquipamento + " - " + descLocal);

		//console.log("serieEquipamento: " + serieEquipamento + " textoCombo: " + textoCombo);
		
		$('#selEquipamento').append($('<option>', {
		    value: serieEquipamento,
		    text: textoCombo,
			'serie-equipamento': serieEquipamento,
			'pista-sentido': pistaSentido
		}));
	});
	
	$('#selEquipamento').selectpicker('refresh');
}	

function DivInit_WebSocketInit()
{		
	try 
	{	
		var o = document.getElementById("selEquipamento");
		dispositivoSelecionado = o.value;
        o.disabled = true;

        var opcaoSelecionada = o.options[o.selectedIndex];         
        var serieEquipamento = opcaoSelecionada.getAttribute('serie-equipamento');
		var pistaSentido = opcaoSelecionada.getAttribute('pista-sentido');

		document.getElementById("mainDiv").hidden = false;
		
		// Verifica SE
		// usário deseja recarregar os locais
		if ( IsReiniciaTela() ) return;
		
		ajustaCameraEquipamento(serieEquipamento, pistaSentido);		
		
						
  	}
  	catch(err) {
		var erro = "Erro na inicialização do Socket de video e passagem em tempo real. Motivo ==> " + err.name + ":" + err.message;
    	console.log(erro);
		AlertCsx_E_TimeOut_8000ms(erro);
  	}	
}

function ajustaCameraEquipamento(serieEquipamento, pistaSentido) {
    const streamDiv = document.querySelector('.h5-menu-list li'); 
    
	// Verifica se o número de série tem exatamente 7 dígitos
    if (serieEquipamento.length !== 7) {
        alert("O número de série deve ter exatamente 7 dígitos.");
        return;
    }

    // Obter os primeiros 2 dígitos, os 2 dígitos do meio e os 3 últimos dígitos
    var primeirosDigitos = serieEquipamento.slice(0, 2);
    var zerosMeio = serieEquipamento.slice(2, 4);
	var sentido = serieEquipamento.slice(4, 5);
    var ultimosDigitos = serieEquipamento.slice(5);
	
    // Verifica se os dois dígitos do meio são exatamente '00'
    if (zerosMeio !== '00') {
        alert("Os dígitos do meio devem ser '00'. Série inválida: " + serieEquipamento);
        return;
    }

	// pistaSentido = 1 sempre para sentido principal 
	// pistaSentido = 2, 3 , 4 quando é o sentido oposto
	// ex: equip 5800155 - 4 faixas sendo pista 1, 2 e 3 no sentido principal e pista 4 no sentido oposto
	// nesse caso será pistaSentido = 1 para sentido principal e pistaSentido = 4 para sentido oposto
	// Câmera do sentido principal será 127.0.0.1:58155
	// Câmera do sentido oposto será    127.0.0.1:58455, ou seja soma 300 nos ultimos 3 dígitos
	if (pistaSentido !== '1') {
		sentido = 3 + parseInt(sentido);	
	}
	
    // Remover os dois zeros do meio e formar a porta
    var porta = primeirosDigitos + sentido + ultimosDigitos;
	var ip = '127.0.0.1';

	console.log("Iniciando câmera equipamento: " + serieEquipamento + ", pistaSentido: " + pistaSentido + " no endereço ip: " + ip + " porta: " + porta);

	if (streamDiv) {
		streamDiv.setAttribute('data-ip', ip);
        streamDiv.setAttribute('data-port', porta);
		streamDiv.setAttribute('data-user', 'admin');
		streamDiv.setAttribute('data-pswd', 'C0ns1lux');
        streamDiv.click();
    } else {
        console.log('Nenhum item encontrado na lista.');
    }
}

function IsReiniciaTela()
{
	btn = document.getElementById('iniciar'); 
	
	if ( btn.textContent == 'REINICIAR')
	{
		WarningCsx_E_TimeOut_8000ms('Reiniciando para escolher novo Local ... ');
		setTimeout(function () { location.reload() }, 3000);
		return true;
	}
	
	if (dispositivoSelecionado == 0)
	{
		AlertCsx_E_TimeOut_8000ms('Favor selecionar um local para visualização!');
		return true;
	}
	
	btn.textContent = 'REINICIAR';
	btn.classList.remove('btn-success');
	btn.classList.add('btn-warning');
	
	return false;
}
