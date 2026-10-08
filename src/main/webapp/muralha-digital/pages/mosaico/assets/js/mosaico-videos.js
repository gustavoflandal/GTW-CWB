var SEGUNDOS_RODIZIO_CAMERAS = 600;
var MSG_CONTAINER_MODAL = "#error_container_modal_mosaico_config";
var TEMPORIZADOR = 0;
var GRUPO_CAMERAS_EM_EXIBICAO = 1;

$(document).ready(function () 
{
	console.log("Tela de monitoramento ao vivo carregada!");
	ObterConfiguracaoVigente();
	
	console.log("Iniciando timer.");
	IniciarTemporizador();
});

function AbrirModalConfig()
{
	ObterConfiguracaoVigente();
}

function ObterConfiguracaoVigente()
{
    var urlPesquisa = urlRoot + "MuralhaDigital/ConfigMonAoVivo";
    var dataString = "acao=obterConfigVigente";
        
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

			if (sucesso) {
				CarregarConfiguracaoMonitoramento( jqXHR.responseText );
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });      
}

function CarregarConfiguracaoMonitoramento(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var $config = $xml.find('ConfigMonitoramentoAoVivo');
	SEGUNDOS_RODIZIO_CAMERAS = $config.find('segundos').text();
	console.log("Tempo para rodízio de câmeras: " + SEGUNDOS_RODIZIO_CAMERAS);
	
	var selTempoRodizioMosaico = document.getElementById("selTempoRodizioMosaico");
	
//	alert(selTempoRodizioMosaico.value);
	
	if (selTempoRodizioMosaico)
		selTempoRodizioMosaico.value = SEGUNDOS_RODIZIO_CAMERAS;
		
//	alert(selTempoRodizioMosaico.value);

	GRUPO_CAMERAS_EM_EXIBICAO = $config.find('grupoCamerasEmExibicao').text();
	console.log("Grupo de câmeras em exibição: " + GRUPO_CAMERAS_EM_EXIBICAO);
	ObterCamerasMonAoVivo();
}


function SalvarConfiguracoes()
{
	var selTempoRodizioMosaico = document.getElementById("selTempoRodizioMosaico");
	var segundos = 0;

	if (selTempoRodizioMosaico)
		segundos = selTempoRodizioMosaico.value;
		
	var url = urlRoot + "MuralhaDigital/ConfigMonAoVivo";
    var dataString = "acao=salvar&segundos=" + segundos;
        
    $.ajax({
        type: 		"POST",
        url: 		url,
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

			if (sucesso) {
				SEGUNDOS_RODIZIO_CAMERAS = segundos;
				ReiniciarTemporizador(true, false)
				SuccessModalCsx_E_TimeOut_8000ms_v2(msgResposta, MSG_CONTAINER_MODAL);
			} else {
				WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, MSG_CONTAINER_MODAL);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', MSG_CONTAINER_MODAL);
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });
}

function IniciarTemporizador(){
    setInterval(function(){
        TEMPORIZADOR++;
        console.log("Tempo decorrido: " + TEMPORIZADOR);
        if (TEMPORIZADOR > SEGUNDOS_RODIZIO_CAMERAS) {
//            TEMPORIZADOR = 0;
			RotacionarCameras(false, false);
        }
    }, 1000);
}

function RotacionarCameras(novaConfiguracao, solicitacaoManual)
{
	console.log("novaConfiguracao: ");
	console.log(novaConfiguracao);
	console.log("solicitacaoManual: ");
	console.log(solicitacaoManual);
	
	if (solicitacaoManual)
    	console.log("Alterando câmeras em exibição por solicitação do usuário!");
    else if (!novaConfiguracao)
    	console.log("Alterando câmeras em exibição por atigir tempo configurado!");
    
    GRUPO_CAMERAS_EM_EXIBICAO++;	
//    AtualizarGrupoEmExibicao();

	AtualizarGrupoEmExibicaoV2().then( response => 
	  ObterCamerasMonAoVivo()
	);
	
	ReiniciarTemporizador(novaConfiguracao, solicitacaoManual);
}

function ReiniciarTemporizador(novaConfiguracao, solicitacaoManual){
    TEMPORIZADOR = 0;
    
    if (solicitacaoManual)
    	console.log("Temporizador reinciado devido a solcitação do usuário!");
    else if (novaConfiguracao)
    	console.log("Temporizador reinciado devido a atualização das configurações!");
    else
    	console.log("Temporizador reinciado por atigir tempo configurado!");
}

function AtualizarGrupoEmExibicao()
{
	var url = urlRoot + "MuralhaDigital/ConfigMonAoVivo";
    var dataString = "acao=atualizarGrupoExibicao&idGrupoExibicao=" + GRUPO_CAMERAS_EM_EXIBICAO;
        
    $.ajax({
        type: 		"POST",
        url: 		url,
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
			var idGrupoExibicao = $xml.find('valorInt').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');
			
			GRUPO_CAMERAS_EM_EXIBICAO = (idGrupoExibicao === '' || idGrupoExibicao === 'null' ? 1 : parseInt(idGrupoExibicao));

//			console.log("Atualização do grupo de câmeras em exibição:");
//			console.log("Sucesso: " + sucesso);
//			console.log("Mensagem: " + msgResposta);
//			console.log("Grupo: " + GRUPO_CAMERAS_EM_EXIBICAO);
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	console.log('Erro ao processar requisição ao servidor!!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });
}

function AtualizarGrupoEmExibicaoV2()
{
	var url = urlRoot + "MuralhaDigital/ConfigMonAoVivo";
    var dataString = "acao=atualizarGrupoExibicao&idGrupoExibicao=" + GRUPO_CAMERAS_EM_EXIBICAO;
        
    return $.ajax({
        type: 		"POST",
        url: 		url,
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
			var idGrupoExibicao = $xml.find('valorInt1').text();
			var segundos = $xml.find('valorInt2').text();
			
//			console.log("XXXX - segundos: " + segundos);
			
			sucesso = (sucesso === 'true' || sucesso === '');
			
			GRUPO_CAMERAS_EM_EXIBICAO = (idGrupoExibicao === '' || idGrupoExibicao === '0' || idGrupoExibicao === 'null' ? 1 : parseInt(idGrupoExibicao));
			SEGUNDOS_RODIZIO_CAMERAS = (segundos === '' || segundos === '0' || segundos === 'null' ? 600 : parseInt(segundos));

//			console.log("Atualização do grupo de câmeras em exibição:");
//			console.log("Sucesso: " + sucesso);
//			console.log("Mensagem: " + msgResposta);
//			console.log("Grupo: " + GRUPO_CAMERAS_EM_EXIBICAO);
//			console.log("Tempo para rodízio de câmeras: " + SEGUNDOS_RODIZIO_CAMERAS);
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	console.log('Erro ao processar requisição ao servidor!!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });
}

function ObterCamerasMonAoVivo()
{
    var urlPesquisa = urlRoot + "MuralhaDigital/ConfigMonAoVivo";
    var dataString = "acao=obterCamerasMonAoVivo&idGrupoExibicao=" + GRUPO_CAMERAS_EM_EXIBICAO;
        
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
	LimparVideos();
	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);

//	console.log("Câmeras de monitoramento ao vivo: ");
	
	var contador = 1;
	$xml.find('cameras').each(function()
	{
		
		var $item = $(this);
		
		var serieEquipamento = $item.find('serieEquipamento').text();
		var nomeEquipamento = $item.find('nomeEquipamento').text();
		var idGrupoExibicao = $item.find('idGrupoExibicao').text();
		var descricaoCam = $item.find('descricao').text();
		var ip = $item.find('ip').text();
		var ipLocal = $item.find('ipLocal').text();
		var urlStream = $item.find('urlStream').text();
		
//		console.log("Equipamento: " + serieEquipamento + " - " + nomeEquipamento);
//		console.log("Câmera: " + descricaoCam + " | IP: " + ip + " | urlStream: " + urlStream + " | Grupo: " + idGrupoExibicao);
		
		var div_video = document.getElementById("video_cam_"+contador);
		
		if (div_video)
			div_video.src = urlStream; //"http://"+ip+"/api/mjpegvideo.cgi?Quality=50";
			
		var legenda_video = document.getElementById("legenda_video_"+contador);
		if (legenda_video)
			legenda_video.innerHTML = serieEquipamento.trim() + " - " + nomeEquipamento.trim() + " (" + descricaoCam + ")";
			
		
		contador++;
	});
}

function LimparVideos()
{
	for (i = 1; i < 10; i++)
	{
		var div_video = document.getElementById("video_cam_"+i);
		var legenda_video = document.getElementById("legenda_video_"+i);
		
		if (div_video)
			div_video.src = "assets/images/ImgFundo4.png";
			
		if (legenda_video)
			legenda_video.innerHTML = "";
	}
}
