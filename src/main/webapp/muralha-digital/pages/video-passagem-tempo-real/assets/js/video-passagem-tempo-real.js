
var dispositivoSelecionado		= 0;
var IMG_OBJETIVA				= 0;
var IMG_PANORAMICA				= 1;

$(document).ready(function() 
{	
	obterEquipamentosGenerico("selEquipamento");
//	ObterListaDispositivos();	
});		


function DivInit_WebSocketInit()
{		
	try 
	{	
		var o = document.getElementById("selEquipamento");
		dispositivoSelecionado = o.value;
		o.disabled = true;
		$("#selEquipamento").selectpicker("refresh");
		
		document.getElementById("mainDiv").hidden = false;
		
		// Verifica SE
		// usário deseja recarregar os locais
		if ( IsReiniciaTela() ) return;
		
		// Carrega o video da camera do local escolhido
		ObterCamerasPorIdLocal();		
		
		// PASSO 1
		//Adiciona na base de dados os equipamentos a serem
		//monitorados pelo usuario
		AdicionaEquipamentosTempoReal(ObterTextoEqptos());		
		
		// PASSO 2
		//Aguarda 1000ms para que os equipamentos selecionados
		//sejam atualizados na base de dados 
		setTimeout( function() { ObterHistoricoVeiculos(); }, 1000 );

		// PASSO 3
		//Aguarda 1800ms para que o histórico seja demonstrado
		//num tempo que faça sentido ao usuario 		
		setTimeout( function() { IniciaWebSocketVideoPassagemTempoReal(); }, 1800 );
		
		// PASSO 4
		// Loop para atualizar data de visualização do equipamento em tela a cada 30s
		// Regra: Quando cliente fecha a tela, então procedure do BD remarca Flag 
		//        para não enviar mais veiculos novos (em tempo real)
		setInterval(function(){ AtualizaEquipamentosTempoReal(ObterTextoEqptos()); }, 30000);
						
  	}
  	catch(err) {
		var erro = "Erro na inicialização do Socket de video e passagem em tempo real. Motivo ==> " + err.name + ":" + err.message;
    	console.log(erro);
		AlertCsx_E_TimeOut_8000ms(erro);
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


function AdicionaEquipamentosTempoReal(listaEqtoSelec)
{	
//	console.log('AdicionaEquipamentosTempoReal');
	
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


function ObterHistoricoVeiculos()
{
	
	$.ajax(
    {
        type: 		"GET",
        url: 		"/MuralhaDigital/VeiculoTempoReal",
		data:		"acao=HistoricoVeiculos",
        dataType:	"xml",
       
        success: 	function( data, textStatus, jqXHR) 
        {
        	ProcessaHistoricoVeiculos( jqXHR.responseText );            	        	
    		return false;
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
			var erro = 'Erro ao processar requisição ao servidor)';         	
			console.log("ObterHistoricoVeiculos:: " + erro);
			AlertCsx_E_TimeOut_8000ms(erro + '. Sockets de imagens em tempo real não inicializadas!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
    });    			
}


function ProcessaHistoricoVeiculos(event) 
{
  	try 
	{
//		console.log('ProcessaHistoricoVeiculos():: Iniciando');
		
		var xmlDoc = $.parseXML( event );
		var $xml = $(xmlDoc);
		
		var lista = $xml.find('Veiculos').find('ListaVeiculos');
		
		lista.find('Veiculo').each(function()
		{
			
			var $veic = $(this);
			
			var idVeic 			= $veic.find('id').text();
			var idPista			= $veic.find('idPista').text();
			var idLocal 		= $veic.find('idLocal').text();
			var descLocal 		= $veic.find('descLocal').text();
			var data			= $veic.find('dataVeicFormatada').text();
			var placa			= $veic.find('placa').text();
			var velocidade		= $veic.find('velocidade').text();
	
			var txtVeic =  	" Pista " + idPista + " - " +
							" Placa " + placa + " - " +
							            velocidade + "Km/h";						
			var txtVeicData = " - " + data;
												
			if( ! ConstainsLocal(idLocal) ) {
				console.log('XXX ==> Local não selecionado para carregar imagens em tempo real:: ' + idLocal);
				return;				
			}
			else
			{				
				//Atualizando as imagens nas novas posições
				document.getElementById('imgMenor_0_2').src = document.getElementById('imgMenor_0_1').src;
				document.getElementById('imgMenor_0_1').src = document.getElementById('imgMaior_0').src;				
				
				//Carrega nova imagem
				var urlObj = urlRoot + "MuralhaDigital/Veiculo/Imagem?" + "acao=ImagemByIdVeic&" + "idVeic=" + idVeic + "&tpImagem=" + IMG_OBJETIVA;
				var urlPan = urlRoot + "MuralhaDigital/Veiculo/Imagem?" + "acao=ImagemByIdVeic&" + "idVeic=" + idVeic + "&tpImagem=" + IMG_PANORAMICA;
				
				document.getElementById('imgMaior_0').src 			= urlObj;
				document.getElementById('imgPan_0').src 			= urlPan;
				document.getElementById('infoVeic_0').innerHTML  	= txtVeic;
				document.getElementById('infoVeicDt_0').innerHTML  = txtVeicData;
				document.getElementById('localDesc_0').innerHTML  = "Local: " + idLocal + " - " + descLocal;
			}
			
		});
		
		document.getElementById('imgMaior_0').className = '';
		document.getElementById('imgMaior_0').classList.add('imgMaior2', 'img-thumbnail');
		
  	}
  	catch(err) {
		var erro = 'Erro ao processar requisição no browser.';         	
		console.log("ProcessaDadosDispositivos:: " + erro + '. ' + err.message);
		AlertCsx_E_TimeOut_8000ms(erro + '. Sockets de imagens em tempo real não inicializadas! ' + err.message);
 	}
  	finally {
		///
  	}	
}

////////////////////////////////////////////////////////////////////////////////////
//Parte de Video Online
////////////////////////////////////////////////////////////////////////////////////

function ObterCamerasPorIdLocal()
{
    var urlPesquisa = urlRoot + "MuralhaDigital/ConfigMonAoVivo";
    var dataString = "acao=obterCamerasPorIdLocal&idLocal=" + dispositivoSelecionado;
        
    $.ajax({
        type: 		"GET",
        url: 		urlPesquisa,
        data: 		dataString,
        dataType:	"xml",
           
        //if received a response from the server
        success: 	function( data, textStatus, jqXHR) 
        {
        	var event = jqXHR.responseText;
        	var xmlDoc = $.parseXML( event );
			var $xml = $(xmlDoc);
    			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso)
				AtualizarVideosCameras( jqXHR.responseText );
			else
				WarningCsx_E_TimeOut_8000ms(msgResposta);
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });      
}

function AtualizarVideosCameras(event) 
{
	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);

//	console.log("Câmeras de monitoramento ao vivo: ");
	
	var contador = 0;
	var cameras = [];
	$xml.find('cameras').each(function()
	{
		
		var $item = $(this);
		
		var idLocal = $item.find('idLocal').text();
		var serieEquipamento = $item.find('serieEquipamento').text();
		var nomeEquipamento = $item.find('nomeEquipamento').text();
		var descricaoCam = $item.find('descricao').text();
		var ip = $item.find('ip').text();
		var ipLocal = $item.find('ipLocal').text();
		var tipoCam = $item.find('tipoCam').text();
		var urlStream = $item.find('urlStream').text();
		
//		console.log("Equipamento: " + serieEquipamento + " - " + nomeEquipamento);
//		console.log("Câmera: " + descricaoCam + " | IP: " + ip + " | urlStream: " + urlStream + " | Grupo: " + idGrupoExibicao);
		
		var camera = {
						idLocal: idLocal,
						serieEquipamento: serieEquipamento,
						nomeEquipamento: nomeEquipamento,
						descricaoCam: descricaoCam,
						ip: ip,
						ipLocal: ipLocal,
						tipoCam, tipoCam,
						urlStream: urlStream
					};
					
		cameras[contador] = camera;
		
		// Utiliza a primeira camera do equipamento
		if (contador == 0)
		{
			var div_video = document.getElementById("video_cam_0");
			
			if (div_video)
				div_video.src = cameras[0].urlStream; //"http://"+cameras[0].ip+"/api/mjpegvideo.cgi";
				
			var legenda_video = document.getElementById("legenda_video_"+contador);
			if (legenda_video)
				legenda_video.innerHTML = cameras[0].serieEquipamento.trim() + " - " + cameras[0].nomeEquipamento.trim() + " (" + cameras[0].descricaoCam + ")";
			}
		
		contador++;
	});
}


function ConstainsLocal(idLocal)
{
	var ret = false;
	
	if( dispositivoSelecionado == idLocal)
	{
		ret = true;
	}

	return ret;
}

////////////////////////////////////////////////////////////////////////////////////
//Parte de WebSocket para recepção das imagens em tempo real
////////////////////////////////////////////////////////////////////////////////////

function onOpenSockVideoPassagemTempoReal(event) 
{
	try{
		// Utiliza o mesmo do veiculo tempo real, caso algum parâmetro dos dados recebidos
		// seja alterado, precisa ser corrigido nos dois locais
		console.log("ID USUÁRIO VEIC TEMPO REAL: " + usuarioID);
		socketVideoPassagemTempoReal.send("Cliente_Consilux_VEICULO-TEMPOREAL-EQUIPAMENTOS-" + ObterTextoEqptos() +"-USUARIO-"+usuarioID);
		
	}
	catch(e){
		console.log('Erro:: onOpenSockVideoPassagemTempoReal()' + e.message)
	}		
}

function onRecebeDadosSockVideoPassagemTempoReal(event) 
{
	try
	{	
		
		console.log('Novo Veiculo Tempo real');
		//console.log(event.data);		
		
		var xmlDoc = $.parseXML( event.data );
		var $xml = $(xmlDoc);
		
		var lista = $xml.find('Veiculos').find('ListaVeiculos');
		
		lista.find('Veiculo').each(function()
		{
			
			var $veic = $(this);
			
			var idVeic 			= $veic.find('id').text();
			var idPista			= $veic.find('idPista').text();
			var idLocal 		= $veic.find('idLocal').text();
			var descLocal 		= $veic.find('descLocal').text();
			var data			= $veic.find('dataVeicFormatada').text();
			var placa			= $veic.find('placa').text();
			var velocidade		= $veic.find('velocidade').text();
	
			var txtVeic =  	" Pista " + idPista + " - " +
							" Placa " + placa + " - " +
							            velocidade + "Km/h";						
			var txtVeicData = " - " + data;
												
			if( ! ConstainsLocal(idLocal) ) {
				console.log('XXX ==> Local não selecionado para carregar imagens em tempo real:: ' + idLocal);	
				return;							
			}
			else
			{				
				//Atualizando as imagens nas novas posições
				document.getElementById('imgMenor_0_2').src = document.getElementById('imgMenor_0_1').src;
				document.getElementById('imgMenor_0_1').src = document.getElementById('imgMaior_0').src;				
				
				//Carrega nova imagem
				var urlObj = urlRoot + "MuralhaDigital/Veiculo/Imagem?" + "acao=ImagemByIdVeic&" + "idVeic=" + idVeic + "&tpImagem=" + IMG_OBJETIVA;
				var urlPan = urlRoot + "MuralhaDigital/Veiculo/Imagem?" + "acao=ImagemByIdVeic&" + "idVeic=" + idVeic + "&tpImagem=" + IMG_PANORAMICA;
				
				document.getElementById('imgMaior_0').src 			= urlObj;
				document.getElementById('imgPan_0').src 			= urlPan;
				document.getElementById('infoVeic_0').innerHTML  	= txtVeic;
				document.getElementById('infoVeicDt_0').innerHTML  = txtVeicData;
				document.getElementById('localDesc_0').innerHTML  = "Local: " + idLocal + " - " + descLocal;
			}
			
		});
			
		document.getElementById('imgMaior_0').className = '';
		document.getElementById('imgMaior_0').classList.add('imgMaior2', 'img-thumbnail');

	}
	catch(e){
		var erro = 'Erro ao receber novo veículo: ' + e.message;
		console.log(erro); AlertCsx_E_TimeOut_8000ms(erro);
	}		
}

function onErrorSockVideoPassagemTempoReal(event) 
{
      alert("onErrorSockVideoPassagemTempoReal(): " + event.data);
}	

function ObterTextoEqptos()
{
	return dispositivoSelecionado + ';';
}	   