var init 						= false;
var dispositivosConfig 			= [];
var dispositivosSelecionados 	= [];
var options 					= [];
var IMG_OBJETIVA				= 0;
var IMG_PANORAMICA				= 1;
var VEICULOS_CARGA				= false;

$(document).ready(function() 
{	
	console.log('Iniciando tela de Veiculos em Tempo Real (v1)');
	ObterDadosURL();	
	ObterListaDispositivos();	
});		

function ObterDadosURL()
{
	try
	{
		//Obtendo id da evidencia
		var params 		= window.location.href.substring(window.location.href.indexOf('?')+1);	
		params 			= params.split('&');

		if (params.length > 0)
		{
			var paramBruto	= params[0].split('=');
			var itemParam 	= paramBruto[0];
			var valorParam = paramBruto[1];
			
			if(itemParam == "VeiculoCarga" && valorParam == "1")
				VEICULOS_CARGA = true;
				
			console.log("VeiculoCarga: " + VEICULOS_CARGA);
			
			if (VEICULOS_CARGA)
			{
				var tituloPagina = document.getElementById('tituloPagina');
				
				if (tituloPagina)
					tituloPagina.textContent = "VEÍCULOS DE CARGA EM TEMPO REAL"; 
			}
				
		}
	}	
	catch(e)
	{
		console.log("Falha ObterDadosURL():: " + e);
	}			
}

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
		var serieEquipamento 		= $dispositivo.find('serieEquipamento').text();
		var descLocal 				= $dispositivo.find('descDispositivo').text();
		var latitude 				= $dispositivo.find('latitude').text();
		var longitude				= $dispositivo.find('longitude').text();
		var codOrgao				= ( ($dispositivo.find('codigosEquipamentos').text() == null || $dispositivo.find('codigosEquipamentos').text() == '') ? "N/D" : $dispositivo.find('codigosEquipamentos').text() );

//		console.log("idLocal: " + idLocal + "descLocal: " + descLocal);
		
		var dispositivo = {
							idDispositivo: idLocal,
							serieDispositivo: serieEquipamento,
							descDispositivo: descLocal, 
							latitude: latitude, 
							longitude: longitude,
							codOrgao: codOrgao
						  };
		
		dispositivosConfig[i] = dispositivo;
				
		// Popula lista de opções do Select/Checkbox de locais
		// A variavel "options[]" fica no arquivo "tempo-real-multi-select.js"
		/////////////////////////////////////////////////////////////////////////////////////
		var opt = 
		{			
			text: dispositivosConfig[i].codOrgao + ' - ' + dispositivosConfig[i].serieDispositivo + ' - ' + dispositivosConfig[i].descDispositivo,
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
		// Verifica SE
		// usário deseja recarregar os locais
		if ( IsReiniciaTela() ) return;
		
		// PASSO 1
		//Inicia os DIVs de acordo com a 
		//seleção de equipamentos do usuário		
		if ( ! DivInit() ) return;			
		
		// PASSO 2
		//Adiciona na base de dados os equipamentos a serem
		//monitorados pelo usuario
		AdicionaEquipamentosTempoReal(ObterTextoEqptos());		
		
		// PASSO 3
		//Aguarda 1000ms para que os equipamentos selecionados
		//sejam atualizados na base de dados 
		setTimeout( function() { ObterHistoricoVeiculos(); }, 1000 );

		// PASSO 4
		//Aguarda 1800ms para que o histórico seja demonstrado
		//num tempo que faça sentido ao usuario 		
		setTimeout( function() { IniciaWebSocketVeicTempoReal(); }, 1800 );
		
		// PASSO 5
		// Loop para atualizar data de visualização do equipamento em tela a cada 30s
		// Regra: Quando cliente fecha a tela, então procedure do BD remarca Flag 
		//        para não enviar mais veiculos novos (em tempo real)
		setInterval(function(){ AtualizaEquipamentosTempoReal(ObterTextoEqptos()); }, 30000);
						
  	}
  	catch(err) {
		var erro = "Erro na inicialização do Socket de veiculos em tempo real. Motivo ==> " + err.name + ":" + err.message;
    	console.log(erro);
		AlertCsx_E_TimeOut_8000ms(erro);
  	}	
}

function IsReiniciaTela()
{
	btn = document.getElementById('iniciar'); 
	
	if ( btn.textContent == 'REINICIAR')
	{
		WarningCsx_E_TimeOut_8000ms('Reiniciando para escolher novos Locais ... ');
		setTimeout(function () { location.reload() }, 3000);
		return true;
	}
	
	btn.textContent = 'REINICIAR';
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
	
	//Adiciona o corpo de acordo com os equipamentos
	addCorpo(qtdeLocais);	
	
	//Redimensaiona de acordo com a quantidade que 
	//que o cliente escolheu
	for (var i = 1; i <= qtdeLocais; i++) 
	{
		var item = "local_" + dispositivosSelecionados[i-1].idLocal;
		
		document.getElementById(item).classList.remove('col-md-3');

		if(qtdeLocais == 1)	
			document.getElementById(item).classList.add('col-md-7');		
		else if(qtdeLocais == 2)
			document.getElementById(item).classList.add('col-md-6');
		else
			document.getElementById(item).classList.add('col-md-4');
	}
	
	return true;				
}

function addCorpo(qtdeLocais)
{
	var corpo = "";
	
	for (var xxx = 1; xxx <= qtdeLocais; xxx++) 
	{
		
		console.log(dispositivosSelecionados[xxx-1].idLocal);
		
		
		corpo = corpo + "  	<div id='local_" + dispositivosSelecionados[xxx-1].idLocal + "' class='col-md-3 col-container pb-2'>	";		
		corpo = corpo + "  		<div class='thumbnail fundo img-thumbnail fundo col'> " ;
		corpo = corpo + "  			<div class='row'> " ;
		corpo = corpo + "  				<div class='col-md-9'>  " ;
		corpo = corpo + "  					<div class='control'> " ;
		corpo = corpo + "  						<figure class='figure'> " ;
		corpo = corpo + "  						  <img  id='imgMaior_" + dispositivosSelecionados[xxx-1].idLocal + "'";
		corpo = corpo + "  						        name='idVeiculo_uuid'";
		corpo = corpo + "  						        alt='teste'";
		corpo = corpo + "  						        style='max-width: 100%' ";		
		corpo = corpo + "  						  		class=' thumbnail img-thumbnail img-responsive' src='assets/images/consilux_imagem_sem_veiculo.png'  data-bs-toggle='modal' data-bs-target='#modalDetalheVeiculo' " ;
		corpo = corpo + "  						  		onclick='abrir_detalhes_veic(this)'> " ;
		corpo = corpo + "  						  <figcaption class='figure-caption text-center'><strong id='infoVeic_" + dispositivosSelecionados[xxx-1].idLocal + "' > Pista: x -- Placa: xxxxxx -- Vel.: xx Km/h </strong><small id='infoVeicDt_" + dispositivosSelecionados[xxx-1].idLocal + "' >  -- Data: 01/01/1901 00:00:00</small></figcaption>" ;
		corpo = corpo + "  						</figure>" ;
		corpo = corpo + "  					</div> " ;
		corpo = corpo + "  				</div> " ;
		corpo = corpo + "  " ;
		corpo = corpo + "  				<div class='col-md-3'>	 " ;
		corpo = corpo + "  						<div> " ;
		corpo = corpo + "  							<img id='imgPan_" + dispositivosSelecionados[xxx-1].idLocal +  "' name='idVeiculo_' class='thumbnail img-thumbnail img-responsive' src='assets/images/consilux_imagem_sem_veiculo.png' onclick='abrir_detalhes_veic(this)' data-bs-toggle='modal' data-bs-target='#modalDetalheVeiculo' alt='img-menor'> " ;
		corpo = corpo + " 							<strong><div class='figure-caption text-center'>Panorâmica</div></strong> ";
		corpo = corpo + "  							</br>" ;
		corpo = corpo + "  " ;
		corpo = corpo + "  							<img id='imgMenor_" + dispositivosSelecionados[xxx-1].idLocal +  "_1' name='idVeiculo_' class='thumbnail img-thumbnail img-responsive' src='assets/images/consilux_imagem_sem_veiculo.png' onclick='abrir_detalhes_veic(this)' data-bs-toggle='modal' data-bs-target='#modalDetalheVeiculo' alt='img-menor'> " ;
		corpo = corpo + "  							<img id='imgMenor_" + dispositivosSelecionados[xxx-1].idLocal +  "_2' name='idVeiculo_' class='thumbnail img-thumbnail img-responsive' src='assets/images/consilux_imagem_sem_veiculo.png' onclick='abrir_detalhes_veic(this)' data-bs-toggle='modal' data-bs-target='#modalDetalheVeiculo' alt='img-menor'> " ;
		corpo = corpo + "  						</div>  " ;
		corpo = corpo + "  				</div> " ;
		corpo = corpo + "  			</div> " ;
		corpo = corpo + "  			<div class='row'> " ;
		corpo = corpo + "  				<div class='col-md-12'>  " ;
		corpo = corpo + "  					<p><strong id='localDesc_" + dispositivosSelecionados[xxx-1].idLocal + "' class='text-justify' style='font-size:1vw'>Local: " + dispositivosSelecionados[xxx-1].descLocal + " </strong></p>  " ;
		corpo = corpo + "  				</div> " ;
		corpo = corpo + "  			</div> " ;
		corpo = corpo + "  		</div> ";
		corpo = corpo + "  	</div> " ;
		
		var locais = document.getElementById("locais");
		locais.innerHTML = corpo;		
	}	
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
		console.log('ProcessaHistoricoVeiculos():: Iniciando');
		
		var xmlDoc = $.parseXML( event );
		var $xml = $(xmlDoc);
		
		var lista = $xml.find('Veiculos').find('ListaVeiculos');
		
		lista.find('Veiculo').each(function()
		{
			
			var $veic = $(this);
			
			var idVeic 				= $veic.find('id').text();
			var idPista				= $veic.find('idPista').text();
			var faixa				= $veic.find('faixa').text();
			var idLocal 			= $veic.find('idLocal').text();
			var serieEquipamento	= $veic.find('serieEquipamento').text();
			var codigoEquipamento	= ( ($veic.find('codigoEquipamento').text() == null || $veic.find('codigoEquipamento').text() == '') ? "N/D" : $veic.find('codigoEquipamento').text() );
			var descLocal 			= $veic.find('descLocal').text();
			var data				= $veic.find('dataVeicFormatada').text();
			var placa				= $veic.find('placa').text();
			var velocidade			= $veic.find('velocidade').text();
			var classificacao		= $veic.find('classificacao').text();
			
			var txtVeic =  	" Pista " + faixa + " - " +
							" Placa " + placa + " - " +
							            velocidade + "Km/h" + " - " +
							" Classificação " + classificacao;						
			var txtVeicData = " - " + data;
												
			if( ! ConstainsLocal(idLocal) ) {
				console.log('XXX ==> Local não selecionado para carregar imagens em tempo real:: ' + idLocal);
				return;				
			}
			else if (VEICULOS_CARGA && !["C", "O", "Q"].includes(classificacao))
			{
				console.log('XXX ==> Veículos de Carga:: ' + VEICULOS_CARGA + ' -  Classificação não pertence a um veículo de carga:: ' + classificacao);
				return;
			}
			else
			{				
				//Atualizando as imagens nas novas posições
				document.getElementById('imgMenor_'+ idLocal +  '_2').src = document.getElementById('imgMenor_'+ idLocal +  '_1').src;
				document.getElementById('imgMenor_'+ idLocal +  '_1').src = document.getElementById('imgMaior_' + idLocal).src;	
				document.getElementById('imgMenor_'+ idLocal +  '_1').src = document.getElementById('imgMaior_' + idLocal).src;				


				//Atualizando IDs de veiculos na imagem
				document.getElementById('imgMenor_'+ idLocal +  '_2').alt = document.getElementById('imgMenor_'+ idLocal +  '_1').alt;
				document.getElementById('imgMenor_'+ idLocal +  '_1').alt = document.getElementById('imgMaior_' + idLocal).alt;
				document.getElementById('imgMenor_'+ idLocal +  '_1').alt = document.getElementById('imgMaior_' + idLocal).alt;

				
				//Carrega nova imagem
				var urlObj = urlRoot + "MuralhaDigital/Veiculo/Imagem?" + "acao=ImagemByIdVeic&" + "idVeic=" + idVeic + "&tpImagem=" + IMG_OBJETIVA;
				var urlPan = urlRoot + "MuralhaDigital/Veiculo/Imagem?" + "acao=ImagemByIdVeic&" + "idVeic=" + idVeic + "&tpImagem=" + IMG_PANORAMICA;
				
				document.getElementById("localDesc_" + idLocal).innerHTML = "Local: " + codigoEquipamento + " - " + serieEquipamento + " - " + descLocal;
				
				document.getElementById('imgMaior_' + idLocal).src 			= urlObj;
				document.getElementById('imgPan_' + idLocal).src 			= urlPan;
				document.getElementById('infoVeic_' + idLocal).innerHTML  	= txtVeic;
				document.getElementById('infoVeicDt_' + idLocal).innerHTML  = txtVeicData;
				
				document.getElementById('imgMaior_' + idLocal).alt 			= idVeic;
				document.getElementById('imgPan_' + idLocal).alt 			= idVeic;
			}
			
		});
		
		if(dispositivosSelecionados.length == 1){		
			document.getElementById('imgMaior_' + dispositivosSelecionados[0].idLocal).className = '';
			document.getElementById('imgMaior_' + dispositivosSelecionados[0].idLocal).classList.add('imgMaior2', 'img-thumbnail');
		}
		
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

function onOpenSockVeicsTempoReal(event) 
{
	try
	{
		console.log("ID USUÁRIO VEIC TEMPO REAL: " + usuarioID);
		socketVeicsTempoReal.send("Cliente_Consilux_VEICULO-TEMPOREAL-EQUIPAMENTOS-" + ObterTextoEqptos() +"-USUARIO-"+usuarioID);
	}
	catch(e)
	{
		console.log('Erro:: onOpenSockVeicsTempoReal()' + e.message)
	}		
}

function onRecebeDadosSockVeicsTempoReal(event) 
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
			
			var idVeic 				= $veic.find('id').text();
			var idPista				= $veic.find('idPista').text();
			var faixa				= $veic.find('faixa').text();
			var idLocal 			= $veic.find('idLocal').text();
			var serieEquipamento	= $veic.find('serieEquipamento').text();
			var codigoEquipamento	= ( ($veic.find('codigoEquipamento').text() == null || $veic.find('codigoEquipamento').text() == '') ? "N/D" : $veic.find('codigoEquipamento').text() );
			var descLocal 			= $veic.find('descLocal').text();
			var data				= $veic.find('dataVeicFormatada').text();
			var placa				= $veic.find('placa').text();
			var velocidade			= $veic.find('velocidade').text();
			var classificacao		= $veic.find('classificacao').text();
			
	
			var txtVeic =  	" Pista " + faixa + " - " +
							" Placa " + placa + " - " +
							            velocidade + "Km/h"
							" Classificação " + classificacao;		
			var txtVeicData = " - " + data;
												
			if( ! ConstainsLocal(idLocal) ) {
				console.log('XXX ==> Local não selecionado para carregar imagens em tempo real:: ' + idLocal);	
				return;							
			}
			else if (VEICULOS_CARGA && !["C", "O", "Q"].includes(classificacao))
			{
				console.log('XXX ==> Veículos de Carga:: ' + VEICULOS_CARGA + ' -  Classificação não pertence a um veículo de carga:: ' + classificacao);
				return;
			}
			else
			{				
				//Atualizando as imagens nas novas posições
				document.getElementById('imgMenor_'+ idLocal +  '_2').src = document.getElementById('imgMenor_'+ idLocal +  '_1').src;
				document.getElementById('imgMenor_'+ idLocal +  '_1').src = document.getElementById('imgMaior_' + idLocal).src;
				
				//Atualizando IDs de veiculos na imagem
				document.getElementById('imgMenor_'+ idLocal +  '_2').alt = document.getElementById('imgMenor_'+ idLocal +  '_1').alt;
				document.getElementById('imgMenor_'+ idLocal +  '_1').alt = document.getElementById('imgMaior_' + idLocal).alt;
				document.getElementById('imgMenor_'+ idLocal +  '_1').alt = document.getElementById('imgMaior_' + idLocal).alt;
				
				//Carrega nova imagem
				var urlObj = urlRoot + "MuralhaDigital/Veiculo/Imagem?" + "acao=ImagemByIdVeic&" + "idVeic=" + idVeic + "&tpImagem=" + IMG_OBJETIVA;
				var urlPan = urlRoot + "MuralhaDigital/Veiculo/Imagem?" + "acao=ImagemByIdVeic&" + "idVeic=" + idVeic + "&tpImagem=" + IMG_PANORAMICA;
				
				document.getElementById("localDesc_" + idLocal).innerHTML = "Local: "  + codigoEquipamento + " - " + serieEquipamento + " - " + descLocal;
				
				document.getElementById('imgMaior_' + idLocal).src 			= urlObj;
				document.getElementById('imgPan_' + idLocal).src 			= urlPan;
				document.getElementById('infoVeic_' + idLocal).innerHTML  	= txtVeic;
				document.getElementById('infoVeicDt_' + idLocal).innerHTML  = txtVeicData;
				
				document.getElementById('imgMaior_' + idLocal).alt 			= idVeic;
				document.getElementById('imgPan_' + idLocal).alt 			= idVeic;
			}
			
		});
		
		if(dispositivosSelecionados.length == 1){		
			document.getElementById('imgMaior_' + dispositivosSelecionados[0].idLocal).className = '';
			document.getElementById('imgMaior_' + dispositivosSelecionados[0].idLocal).classList.add('imgMaior2', 'img-thumbnail');
		}

	}
	catch(e){
		var erro = 'Erro ao receber novo veículo: ' + e.message;
		console.log(erro); AlertCsx_E_TimeOut_8000ms(erro);
	}		
}

function onErrorSockVeicsTempoReal(event) 
{
      alert("onErrorSockVeicsTempoReal(): " + event.data);
}	

function ObterTextoEqptos()
{
	var eqptos = '';			
	for (var i = 0; i < dispositivosSelecionados.length; i++) {
		eqptos = eqptos + dispositivosSelecionados[i].idLocal + ';';
	}
	
	return eqptos;
}	   

function abrir_detalhes_veic(obj)
{
	id_veic = obj.alt;
	AbrirDetalhesVeiculo(id_veic);
}