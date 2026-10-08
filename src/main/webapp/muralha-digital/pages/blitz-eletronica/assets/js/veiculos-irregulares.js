var init 						= false;
var dispositivosConfig 			= [];
var dispositivosSelecionados 	= [];
var options 					= [];
var IMG_OBJETIVA				= 0;
var IMG_PANORAMICA				= 1;
var QTDE_DIVS					= 0;

$(document).ready(function() 
{
	console.log('Iniciando tela de Blitz Eletronica (v1)');
	document.title = 'Blitz Eletrônica';
    ObterListaDispositivos();
    HabilitaAlarmeSonoro();	
});		


function ObterListaDispositivos()
{
	var dataStringPesquisa = "acao=obterListaEquipamentos";
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
	var i = 0;	
	
	lista.find('Dispositivo').each(function()
	{
		var $dispositivo = $(this);
		
		var idLocal 				= $dispositivo.find('idDispositivo').text();
		var descLocal 				= $dispositivo.find('descDispositivo').text();
		var latitude 				= $dispositivo.find('latitude').text();
		var longitude				= $dispositivo.find('longitude').text();

//		console.log("idLocal: " + idLocal + " descLocal: " + descLocal);
		
		var dispositivo = {
							idDispositivo: idLocal, 
							descDispositivo: descLocal, 
							latitude: latitude, 
							longitude:longitude
						  };
		
		dispositivosConfig[i] = dispositivo;
				
		// Popula lista de opções do Select/Checkbox de locais
		// A variavel "options[]" fica no arquivo "tempo-real-multi-select.js"
		/////////////////////////////////////////////////////////////////////////////////////
		var opt = 
		{			
			text: dispositivosConfig[i].idDispositivo + ' - ' + dispositivosConfig[i].descDispositivo,
			value:dispositivosConfig[i].idDispositivo,
			hidden:false,
			disabled:false,
			selected:false
		};
		options[i] = opt;
		//////////////////////////////////////////////////////////////////////////////////////
		i++;
	});
	PopulaSelectLocais();
}	

function DivInit_WebSocketInit()
{		
	try 
	{
		// Verifica SE usário deseja recarregar os locais
		if ( IsReiniciaTela() ) return;
		
		// PASSO 1
		// Inicia os DIVs de acordo com a seleção de equipamentos do usuário		
		if ( ! DivInit() ) return;
		
		// PASSO 2
		// Adiciona na base de dados os equipamentos a serem monitorados pelo usuario
		AdicionaEquipamentosTempoReal(ObterTextoEqptos());
		
		// PASSO 3
		// Aguarda 1800ms para que o histórico seja demonstrado num tempo que faça sentido ao usuario 		
		setTimeout( function() { IniciaWebSocketBlitzEletronica(); }, 50 );
		
		// PASSO 4
		// Loop para atualizar data de visualização do equipamento em tela a cada 30s
		// Regra: Quando cliente fecha a tela, então procedure do BD remarca Flag para não enviar mais veiculos novos (em tempo real)
		setInterval(function(){ AtualizaEquipamentosTempoReal(ObterTextoEqptos()); }, 30000);
  	}
  	catch(err) {
		var erro = "Erro na inicialização do Socket de Blitz Eletronica";
    	console.log(erro + " Motivo ==> " + err.name + ":" + err.message);
		AlertCsx_E_TimeOut_8000ms(erro);
  	}	
}

function IsReiniciaTela()
{
	btn = document.getElementById('iniciar'); 
	
	if ( btn.textContent == 'ENCERRAR')
	{
		WarningCsx_E_TimeOut_8000ms('Encerrando blitz eletrônica ... ');
		setTimeout(function () { location.reload() }, 3000);
		return true;
	}
	
	btn.textContent = 'ENCERRAR';
	btn.classList.remove('btn-success');
	btn.classList.add('btn-warning');
	
	return false;
}


function DivInit()
{
	var qtdeLocais = dispositivosSelecionados.length;	
	
	if (qtdeLocais == 0){
		AlertCsx_E_TimeOut_8000ms('Favor selecionar ao menos 1(um) local para visualização!');
		return false;		
	}
	
	//A seleção só pode ocorrer uma unica vez
	if(init == true) return false;
	
	init = true;	
	console.log("Quantidade de Locais:: " + qtdeLocais);
	
	var divImgBlitz = document.getElementsByClassName("div-img-blitz");
	QTDE_DIVS = divImgBlitz.length;
	console.log("QTDE_DIVS: " + QTDE_DIVS);
	
	return true;				
}

function AdicionaEquipamentosTempoReal(listaEqtoSelec)
{
	$.ajax(
    {
        type: 		"GET",
        url: 		"/MuralhaDigital/VeiculoTempoReal",
		data:		"acao=AddEqtosTempoReal&ListaEqptosTReal=" + listaEqtoSelec,
        dataType:	"xml",
       
        success: 	function( data, textStatus, jqXHR) { /**/ },
        error: 		function(jqXHR, textStatus, errorThrown)
        {
			var erro = 'Erro ao processar requisição ao servidor)';         	
			console.log("AdicionaEquipamentosTempoReal:: " + erro);
			AlertCsx_E_TimeOut_8000ms("AdicionaEquipamentosTempoReal:: " + erro);
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
    });    			
}

function AtualizaEquipamentosTempoReal(listaEqtoSelec)
{	
	console.log('AtualizaEquipamentosTempoReal');
	$.ajax(
    {
        type: 		"GET",
        url: 		"/MuralhaDigital/VeiculoTempoReal",
		data:		"acao=AtualizaEqptosTempoReal&ListaEqptosTReal=" + listaEqtoSelec,
        dataType:	"xml",
       
        success: 	function( data, textStatus, jqXHR) { /**/ },
        error: 		function(jqXHR, textStatus, errorThrown)
        {
			var erro = 'Erro ao processar requisição ao servidor)';         	
			console.log("AtualizaEquipamentosTempoReal:: " + erro);
			AlertCsx_E_TimeOut_8000ms("AtualizaEquipamentosTempoReal:: " + erro);
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
    });    			
}
function ConstainsLocal(idLocal)
{
	var ret = false;
	
	for (var i = 0; i < dispositivosSelecionados.length; i++) 
	{
		if( dispositivosSelecionados[i].idLocal == idLocal)
		{
			ret = true;
		}					
	}

	return ret;
}

////////////////////////////////////////////////////////////////////////////////////
//Parte de WebSocket para recepção das imagens em tempo real
////////////////////////////////////////////////////////////////////////////////////

function onOpenSockBlitzEletronica(event) 
{
	try
	{
		console.log("ID USUÁRIO BLITZ ELETRONICA: " + usuarioID);
		socketBlitzEletronica.send("Cliente_Consilux_BLITZ-ELETRONICA-EQUIPAMENTOS-" + ObterTextoEqptos() +"-USUARIO-"+usuarioID);
	}
	catch(e)
	{
		console.log('Erro:: onOpenSockBlitzEletronica(): ' + e.message)
	}		
}

function onRecebeDadosSockBlitzEletronica(event) 
{
	try
	{	
		console.log('Novo Veiculo Blitz Eletronica');
		//console.log(event.data);		
		
		var xmlDoc = $.parseXML( event.data );
		var $xml = $(xmlDoc);
		
		var lista = $xml.find('Veiculos').find('ListaVeiculos');
		
		lista.find('Veiculo').each(function()
		{
			var $veic = $(this);
			
			var idVeic 			= $veic.find('id').text();
			var faixa			= $veic.find('faixa').text();
			var idLocal 		= $veic.find('idLocal').text();
			var descLocal 		= $veic.find('descLocal').text();
			var data			= $veic.find('dataVeicFormatada').text();
			var placa			= $veic.find('placa').text();
			var velocidade		= $veic.find('velocidade').text();
			var tituloAlerta	= $veic.find('tiposAlertas').text();
	
			var txtInfoVeic = `<strong>Placa:</strong> ${placa} <strong>- Vel.:</strong> ${velocidade} Km/h <strong>- Data:</strong> <small>${data}</small> <strong>- Faixa:</strong> ${faixa} `;
			var txtLocalDesc = `Endereço: ${descLocal}`;
			
			if( ! ConstainsLocal(idLocal) ) {
				console.log('XXX ==> Local não selecionado para carregar imagens de blitz eletrônica:: ' + idLocal);	
				return;							
			}
			else
			{
				// Atualizando imagens existentes na tela para novas posições
				AtualizarDivsNovoVeiculo();
				
				//Carrega nova imagem
				var urlObj = urlRoot + "MuralhaDigital/Veiculo/Imagem?" + "acao=ImagemByIdVeic&" + "idVeic=" + idVeic + "&tpImagem=" + IMG_OBJETIVA;

				document.getElementById(`imgMaior_1`).src = urlObj;
				document.getElementById(`imgMaior_1`).alt = idVeic;
				document.getElementById(`infoVeic_1`).innerHTML = txtInfoVeic;
				document.getElementById(`tipoAlerta_1`).innerHTML = tituloAlerta;
				document.getElementById(`localDesc_1`).innerHTML = txtLocalDesc;
			}
			var tiposAlertas = tituloAlerta.split("|");
			var tipoAlerta = tiposAlertas[0].trim();
//			console.log(tiposAlertas);
//			console.log("tipoAlerta: " + tipoAlerta);
//			VerificaAlertaHabilitado(tipoAlerta);
		});
	}
	catch(e)
	{
		var erro = 'Erro ao receber novo veículo: ' + e.message;
		console.log(erro);
		AlertCsx_E_TimeOut_8000ms(erro);
	}		
}

function AtualizarDivsNovoVeiculo()
{
	for (var i = QTDE_DIVS; i > 1; i--)
	{
		var itemMover = (i-1);
		//Atualizando as imagens nas novas posições
		document.getElementById(`imgMaior_${i}`).src = document.getElementById(`imgMaior_${itemMover}`).src;
		document.getElementById(`infoVeic_${i}`).innerHTML = document.getElementById(`infoVeic_${itemMover}`).innerHTML;
		document.getElementById(`tipoAlerta_${i}`).innerHTML = document.getElementById(`tipoAlerta_${itemMover}`).innerHTML;
		document.getElementById(`localDesc_${i}`).innerHTML = document.getElementById(`localDesc_${itemMover}`).innerHTML;
		
		//Atualizando IDs de veiculos na imagem
		document.getElementById(`imgMaior_${i}`).alt = document.getElementById(`imgMaior_${itemMover}`).alt;
	}
}

function onErrorSockBlitzEletronica(event) 
{
//	alert("onErrorSockBlitzEletronica(): " + event.data);
	console.log("onErrorSockBlitzEletronica(): " + event.data);
	AlertCsx_E_TimeOut_8000ms("Erro ao receber novo veículo para blitz eletrônica!");
}	

function ObterTextoEqptos()
{
	var eqptos = '';			
	for (var i = 0; i < dispositivosSelecionados.length; i++) {
		eqptos = eqptos + dispositivosSelecionados[i].idLocal + ';';
	}
	
	return eqptos;
}

function abrir_alertas_veic(obj)
{
	id_veic = obj.alt;
	if (id_veic != null && id_veic != "")
	{
		var modalAlertaVeiculo = new bootstrap.Modal(document.getElementById('modalAlertaVeiculo'))
		modalAlertaVeiculo.show();
		AbrirModalAlertaVeiculo(id_veic);
	}
}	   

function abrir_detalhes_veic(obj)
{
	id_veic = obj.alt;
	AbrirDetalhesVeiculo(id_veic);
}

function AbrirConfiguracaoMonitoramento()
{
	var url = '/muralha-digital/pages/configuracao-monitoramento/configuracao.jsp';
	CentralizaWindow(url, 'Configurações de Monitoramento', window, (screen.width/1.3), (screen.height/1.4));
}

function AbrirCadVeiculosMonitorados()
{
	var url = '/muralha-digital/pages/monitorado/consulta.jsp';
	CentralizaWindow(url, 'Cadastro de Veículos Monitorados', window, (screen.width/1.3), (screen.height/1.3));
}

function CentralizaWindow(url, windowName, win, w, h) 
{
    const y = win.top.outerHeight / 2 + win.top.screenY - ( h / 2);
    const x = win.top.outerWidth  / 2 + win.top.screenX - ( w / 2);
    return win.open(url, windowName, `toolbar=yes, location=yes, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, copyhistory=no, width=${w}, height=${h}, top=${y}, left=${x}`);
}