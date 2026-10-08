var LISTA_VIDEOS_EXIBICAO = [];
var VIDEO_EM_EXIBICAO = [];
var VIDEOS_POR_CAMERA = [];
var LISTA_CAMERAS = [];

$(document).on({
    ajaxStart: function(){
        $("body").addClass("loading"); 
    },
    ajaxStop: function(){ 
        $("body").removeClass("loading"); 
    }    
});

$().ready(function () 
{
	CarregarComponenteData(false);
	obterEquipamentosGenerico("selEquipamento");
});

function ExecutaPesquisa()
{
	var equipamento = document.getElementById("selEquipamento").value;
  	var dataIni = TratarDataHora($("#dataInicio").find("input").val());
  	var dataFim = TratarDataHora($("#dataFim").find("input").val());

    var dataStringPesquisa = "dataIni=" + dataIni + 
    							"&dataFim=" + dataFim +
								"&equipamento=" + equipamento +
								"&acao=consultaVideosMonitoramento";
        
    var urlPesquisa = urlRoot + "MuralhaDigital/VideoMonitoramento";
    
	LimparVariaveisControleVideos();
	LimparElementoHtmlVideos();

    $.ajax({
        type: 		"GET",
        url: 		urlPesquisa,
        data: 		dataStringPesquisa,
        dataType:	"xml",
           
        success: 	function( data, textStatus, jqXHR) 
        {
        	var event = jqXHR.responseText;
        	var xmlDoc = $.parseXML( event );
			var $xml = $(xmlDoc);
    			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');
			
			if (sucesso)
				ProcessaDadosConcatenaVideos( jqXHR.responseText );
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


function ProcessaDadosConcatenaVideos(event) 
{
	var listaVideosExibicao = document.getElementById("listaVideosExibicao");

	if (listaVideosExibicao)
	{
		var xmlDoc = $.parseXML( event );
		var $xml = $(xmlDoc);
	
		var qtdeCameras = $xml.find('VideosMonitoramento').find('quantidadeCameras').text();
		var corpo = "";
		var cssTamanhoVideo = qtdeCameras == 1 ? "col-md-12" : "col-md-6";
		
		listaVideosExibicao.innerHTML = "";
		
		$xml.find('VideoMonitoramento').each(function()
		{
			var $item = $(this);
			var serieEquipamento = $item.find('serieEquipamento').text();
			var ipCamera = $item.find('ipCamera').text();
			var tipoCamera = $item.find('tipoCamera').text();
			var listaEnderecoVideos = encodeURIComponent($item.find('listaEnderecoVideos').text());
			
			var elementoHtml = "video_cam_"+ipCamera;
			var url = urlRoot + "MuralhaDigital/VideoMonitoramento/Video?acao=verVideoPorListaEnderecoTemp&listaEnderecosVideos=" + listaEnderecoVideos; //FFMPEG com comando windows
//			var url = urlRoot + "MuralhaDigital/VideoMonitoramento/Video?acao=verVideoPorListaEndereco&listaEnderecosVideos=" + listaEnderecoVideos; //Wrapper FFMPEG

			var legendaVideo = `${serieEquipamento} - ${tipoCamera}`;
			
			corpo = corpo + " <div class=\""+cssTamanhoVideo+" d-flex justify-content-center\"> ";		
			corpo = corpo + "	<figure class=\"figure\"> ";
			corpo = corpo + "		<video id='"+elementoHtml+"' name='"+elementoHtml+"' class='embed-responsive thumbnail img-thumbnail' src='"+url+"' controls autoplay loop type='video/ogg'></video> ";
			corpo = corpo + "		<figcaption id=\"legenda_"+elementoHtml+"\" class=\"figure-caption text-center\"><small>"+legendaVideo+"</small></figcaption> ";
			corpo = corpo + " 	</figure> ";
			corpo = corpo + " </div> ";
		});
		
		listaVideosExibicao.innerHTML = corpo;
	}
}


function ProcessaDados(event) 
{
	var listaVideosExibicao = document.getElementById("listaVideosExibicao");

	if (listaVideosExibicao)
	{
		var xmlDoc = $.parseXML( event );
		var $xml = $(xmlDoc);
	
		var qtdeCameras = $xml.find('VideosMonitoramento').find('quantidadeCameras').text();
		var corpo = "";
		var cssTamanhoVideo = qtdeCameras == 1 ? "col-md-12" : "col-md-6";
		var item = 1
		
		listaVideosExibicao.innerHTML = "";
		
		$xml.find('VideoMonitoramento').each(function()
		{
			var $item = $(this);
			var serieEquipamento = $item.find('serieEquipamento').text();
			var ipCamera = $item.find('ipCamera').text();
			var tipoCamera = $item.find('tipoCamera').text();
			var listaEnderecoVideos = $item.find('listaEnderecoVideos').text();
			
			LISTA_CAMERAS[item] = ipCamera;

			var indiceVideo = 1;
			listaEnderecoVideos.split(";").forEach(function(video)
			{
			    var urlVideo = urlRoot + "MuralhaDigital/VideoMonitoramento/Video?acao=verVideoPorEndereco&enderecoVideo=" + encodeURIComponent(video);
			    LISTA_VIDEOS_EXIBICAO[ipCamera + "_" + indiceVideo] = urlVideo;
			    VIDEOS_POR_CAMERA[ipCamera] = indiceVideo;
			    indiceVideo++;
			});
			
			var elementoHtml = "video_cam_"+ipCamera;
			var loopTag = VIDEOS_POR_CAMERA[ipCamera] > 1 ? "" : "loop";
			var url = LISTA_VIDEOS_EXIBICAO[ipCamera + "_1"];
			VIDEO_EM_EXIBICAO[ipCamera] = 1;
			
			var legendaVideo = `${serieEquipamento} - ${tipoCamera}`;
			
			corpo = corpo + " <div class=\""+cssTamanhoVideo+" d-flex justify-content-center\"> ";		
			corpo = corpo + "	<figure class=\"figure\"> ";
			corpo = corpo + "		<video id='"+elementoHtml+"' name='"+elementoHtml+"' class='embed-responsive thumbnail img-thumbnail' src='"+url+"' controls autoplay "+loopTag+" type='video/ogg'> </video> ";
			corpo = corpo + "		<figcaption id=\"legenda_"+elementoHtml+"\" class=\"figure-caption text-center\"><small>"+legendaVideo+"</small></figcaption> ";
			corpo = corpo + " 	</figure> ";
			corpo = corpo + " </div> ";
			
			item++;
		});
		
		listaVideosExibicao.innerHTML = corpo;
		for (cam = 1; cam < LISTA_CAMERAS.length; cam++)
		{
			var ipCameraAux = LISTA_CAMERAS[cam];
			if (VIDEOS_POR_CAMERA[ipCameraAux] > 1)
				CriarEventoAtualizarVideoEmExibicao(ipCameraAux);
		}
	}
}

function CriarEventoAtualizarVideoEmExibicao(ipCamera)
{
	console.log("CriarEventoAtualizarVideoEmExibicao --> ipCamera: " + ipCamera + " | elementoHtml: video_cam_" + ipCamera);
	var elementoHtml = document.getElementById("video_cam_" + ipCamera);
	if (elementoHtml)
		elementoHtml.onended = function(){ AtualizarVideoEmExibicao(elementoHtml, ipCamera); }
}

function AtualizarVideoEmExibicao(elementoHtml, ipCamera)
{
	var item = VIDEO_EM_EXIBICAO[ipCamera] < VIDEOS_POR_CAMERA[ipCamera] ? VIDEO_EM_EXIBICAO[ipCamera] + 1 : 1;
	var src = LISTA_VIDEOS_EXIBICAO[ipCamera + "_" + item];
	
	console.log("AtualizarVideoEmExibicao --> ipCamera: " + ipCamera + " | src: " + src);
	
	elementoHtml.src = LISTA_VIDEOS_EXIBICAO[ipCamera + "_" + item];
	VIDEO_EM_EXIBICAO[ipCamera] = item;
}

function LimparVariaveisControleVideos()
{
	LISTA_VIDEOS_EXIBICAO = [];
	VIDEO_EM_EXIBICAO = [];
	VIDEOS_POR_CAMERA = [];
	
	for (cam = 1; cam < LISTA_CAMERAS.length; cam++)
	{
		var elementoHtml = document.getElementById("video_cam_" + LISTA_CAMERAS[cam]);
		if (elementoHtml)
			elementoHtml.onended = function(){ console.log("Encerrando exibição de videos da câmera " + LISTA_CAMERAS[cam]); }
	}
	
	LISTA_CAMERAS = [];
}

function LimparElementoHtmlVideos()
{
	var listaVideosExibicao = document.getElementById("listaVideosExibicao");
	if (listaVideosExibicao)
		listaVideosExibicao.innerHTML = "";
}

function LimparFiltros()
{
	document.getElementById("selEquipamento").value = "0";
	
  	$dataIni = $("#dataInicio").find("input");
    $dataIni.val('');
    $dataIni.removeData();
    
    $dataFim = $("#dataFim").find("input");
    $dataFim.val('');
    $dataFim.removeData();
}
