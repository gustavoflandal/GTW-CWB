function mostraDIVImagemPerfil(idDIV) {
	var lista_divs = document.getElementsByName("div_conteudo");
	
    for(var i=0; i<lista_divs.length;i++) {
        var div = lista_divs[i];
        if (div.id != idDIV) {
        	if (idDIV == "div_imagem" && div.id == "div_lista_imagens") {
        		div.style.display = "";
        	} else {
            	div.style.display = "none";
        	}
        }else{
        	div.style.display = "";
        }
    }

	atual();
}

function mostraDIVImagemPerfil1(idDIV) {
	var lista_divs = document.getElementsByName("div_conteudo");
	
    for(var i=0; i<lista_divs.length;i++) {
        var div = lista_divs[i];
        if (div.id != idDIV) {
        	if (idDIV == "div_imagem" && div.id == "div_lista_imagens") {
        		div.style.display = "";
        	} else {
            	div.style.display = "none";
        	}
        } else {
            div.style.display = "";
        }
    }
}

function carregaImagemPerfil(id, idImagemObj, tipo, idVeiculo) {
	var div_imagem 				= document.getElementById("div_imagem");
	var div_perfil 				= document.getElementById("div_perfil");
	var div_video 				= document.getElementById("div_video");
	var div_target_velocidade 	= document.getElementById("div_target_velocidade");
	var div_target_ponto		= document.getElementById("div_target_ponto");
	var div_imagem_des 			= document.getElementById("div_imagem_des");
	var div_zoom 				= document.getElementById("div_zoom");
	
	var link_imagem 			= document.getElementById("link_imagem");
	var link_perfil 			= document.getElementById("link_perfil");
	var link_video 				= document.getElementById("link_video");
	var link_target_velocidade	= document.getElementById("link_target_velocidade");
	var link_target_ponto		= document.getElementById("link_target_ponto");
	var link_desob 				= document.getElementById("link_desob");
	var link_acerto     		= document.getElementById("link_acerto");
	var link_zoom 				= document.getElementById("link_zoom");
	
    if (div_imagem && div_imagem.style.display != "none") {
    	if (tipo == 'V')
    		carregaVeiculoImagem(id, idImagemObj);
    	else if (tipo == 'I')
    		carregaInfracaoImagem(id, idImagemObj);
    	else if (tipo == 'ID')
    		carregaInfracaoImagemDesob(id, idImagemObj);
    	
    	if(link_perfil)
    		link_perfil.style.textDecoration = "none";	
    	if(link_imagem) 
    		link_imagem.style.textDecoration = "underline";
    	if(link_video)
    		link_video.style.textDecoration = "none";
    	if(link_target_velocidade)
    		link_target_velocidade.style.textDecoration = "none";
    	if(link_target_ponto)
    		link_target_ponto.style.textDecoration = "none";
    	if(link_desob)
    		link_desob.style.textDecoration = "none";
    	if(div_zoom)
    		link_zoom.style.textDecoration = "none";
    }
    else if (div_perfil && div_perfil.style.display != "none") {
    	if (tipo == 'V')
    		carregaVeiculoPerfil(id);
    	else if (tipo == 'I')
    		carregaVeiculoPerfil(idVeiculo);
        
    	if(link_perfil)
    		link_perfil.style.textDecoration = "underline";
    	if(link_imagem)
    		link_imagem.style.textDecoration = "none";
    	if(link_video)
    		link_video.style.textDecoration = "none";
    	if(link_target_velocidade)
    		link_target_velocidade.style.textDecoration = "none";
    	if(link_target_ponto)
    		link_target_ponto.style.textDecoration = "none";
    	if(link_desob)
    		link_desob.style.textDecoration = "none";
    	if(div_zoom)
    		link_zoom.style.textDecoration = "none";
    }	
    else if (div_video && div_video.style.display != "none") {
    	if (tipo == 'V')
    		carregaVeiculoVideo(id);
    	else if (tipo == 'I')
    		carregaVeiculoVideo(idVeiculo);
    	
    	if(link_perfil)
    		link_perfil.style.textDecoration = "none";
    	if(link_imagem)
    		link_imagem.style.textDecoration = "none";
    	if(link_video)
    		link_video.style.textDecoration = "underline";
    	if(link_target_velocidade)
    		link_target_velocidade.style.textDecoration = "none";
    	if(link_target_ponto)
    		link_target_ponto.style.textDecoration = "none";
    	if(link_desob)
    		link_desob.style.textDecoration = "none";
    	if(div_zoom)
    		link_zoom.style.textDecoration = "none";
    }
    else if (div_target_velocidade && div_target_velocidade.style.display != "none") {
    	
    	var tipoGrafico = 1; //Velocidade
    	
    	if (tipo == 'V')
    		carregaVeiculoTarget(id, tipoGrafico);
    	else if (tipo == 'I')
    		carregaVeiculoTarget(idVeiculo, tipoGrafico);
        
    	if(link_perfil)
    		link_perfil.style.textDecoration = "none";
    	if(link_imagem)
    		link_imagem.style.textDecoration = "none";
    	if(link_video)
    		link_video.style.textDecoration = "none";
    	if(link_target_velocidade)
    		link_target_velocidade.style.textDecoration = "underline";
    	if(link_target_ponto)
    		link_target_ponto.style.textDecoration = "none";
    	if(link_desob)
    		link_desob.style.textDecoration = "none";
    	if(div_zoom)
    		link_zoom.style.textDecoration = "none";
    }
    else if (div_target_ponto && div_target_ponto.style.display != "none") {
    	
    	var tipoGrafico = 2; //Pontos e borda das faixas
    	
    	if (tipo == 'V')
    		carregaVeiculoTarget(id, tipoGrafico);
    	else if (tipo == 'I')
    		carregaVeiculoTarget(idVeiculo, tipoGrafico);
        
    	if(link_perfil)
    		link_perfil.style.textDecoration = "none";
    	if(link_imagem)
    		link_imagem.style.textDecoration = "none";
    	if(link_video)
    		link_video.style.textDecoration = "none";
    	if(link_target_velocidade)
    		link_target_velocidade.style.textDecoration = "none";
    	if(link_target_ponto)
    		link_target_ponto.style.textDecoration = "underline";
    	if(link_desob)
    		link_desob.style.textDecoration = "none";
    	if(div_zoom)
    		link_zoom.style.textDecoration = "none";
    }
    else if (div_imagem_des && div_imagem_des.style.display != "none") {
    	if (tipo == 'V')
    		carregaVeiculoImagemDesob(id, idImagemObj);
    	else if (tipo == 'I' || tipo == 'ID')
    		carregaInfracaoImagemDesob(id, idImagemObj);
    	
    	if(link_perfil)
    		link_perfil.style.textDecoration = "none";
    	if (link_imagem)
    		link_imagem.style.textDecoration = "none";
    	if (link_video)
    		link_video.style.textDecoration = "none";
    	if(link_target_velocidade)
    		link_target_velocidade.style.textDecoration = "none";
    	if(link_target_ponto)
    		link_target_ponto.style.textDecoration = "none";
    	if (link_desob)
    		link_desob.style.textDecoration = "underline";
    	if (div_zoom)
    		link_zoom.style.textDecoration = "none";
    }
    else if (div_zoom && div_zoom.style.display != "none") {
    	
    	if (tipo == 'I')
    		carregaInfracaoImagemZoom(id, idImagemObj);

    	$('#sp_zoom').zoom();
    	
    	if(link_perfil)
    		link_perfil.style.textDecoration = "none";
    	if(link_imagem)
    		link_imagem.style.textDecoration = "none";
    	if(link_video)
    		link_video.style.textDecoration = "none";
    	if(link_target_velocidade)
    		link_target_velocidade.style.textDecoration = "none";
    	if(link_target_ponto)
    		link_target_ponto.style.textDecoration = "none";
    	if(link_desob)
    		link_desob.style.textDecoration = "none";
    	if(div_zoom)
    		link_zoom.style.textDecoration = "underline";
    }
}

function carregaVeiculoImagem(idVeiculo, idImagemObj) {
	if (idImagemObj > 0)
		carregaImagem(idImagemObj)
	else
		carregaImagem(0)
		
    carregaListaImagensVeiculo(idVeiculo);
}

function carregaInfracaoImagem(idInfracao, idImagemObj) {
	if (idImagemObj > 0)
		carregaImagem(idImagemObj)
	else
		carregaImagem(0)
		
    carregaListaImagensInfracao(idInfracao);
}

function carregaVeiculoImagemZoom(idVeiculo, idImagemObj) {
	if (idImagemObj > 0)
		carregaImagemZoom(idImagemObj)
	else
		carregaImagemZoom(0)
		
    carregaListaImagensVeiculo(idVeiculo);
}

function carregaInfracaoImagemZoom(idInfracao, idImagemObj) {
	if (idImagemObj > 0)
		carregaImagemZoom(idImagemObj)
	else
		carregaImagemZoom(0)
		
    carregaListaImagensInfracao(idInfracao);
}

function carregaVeiculoImagemDesob(idVeiculo, idImagemObj) {
	if (idImagemObj > 0)
		carregaImagemDesob(idImagemObj)
	else
		carregaImagemDesob(0)
		
    carregaListaImagensVeiculo(idVeiculo);
}

function carregaInfracaoImagemDesob(idInfracao, idImagemObj) {
	if (idImagemObj > 0)
		carregaImagemDesob(idImagemObj)
	else
		carregaImagemDesob(0)
		
    carregaListaImagensInfracao(idInfracao);
}

function carregaVeiculoPerfil(idVeiculo) {
	var if_perfil = document.getElementById("if_perfil");
	
	if_perfil.src = '/servlet/PerfilVeiculo?id_veiculo='+idVeiculo;
	if_perfil.onload = function() {
		if_perfil.onload = "";
        $.unblockUI();
	}
	$.blockUI({ message: '<h1 id="blocked"><img src="/images/busy.gif" /> Aguarde...</h1>'});
}
function carregaVeiculoUnicPerfil(idVeiculoUnic) {
	var if_perfil = document.getElementById("if_perfil");
	
	if_perfil.src = '/servlet/PerfilVeiculo?id_veiculo_unic='+idVeiculoUnic;
	if_perfil.onload = function() {
		if_perfil.onload = "";
        $.unblockUI();
	}
	$.blockUI({ message: '<h1 id="blocked"><img src="/images/busy.gif" /> Aguarde...</h1>'});
}

function carregaVeiculoVideo(idVeiculo) {
	var if_video = document.getElementById("if_video");
	var id_video_sel = 1;
	var id_video_type = 0;
	var video_sel = document.getElementById("video_sel");
	var video_type = document.getElementById("video_type");
	if (video_sel)
		id_video_sel = parseInt(video_sel.value);
	if (video_type)
		id_video_type = parseInt(video_type.value);
	
	if (id_video_type == 1)
		if_video.src = '/video/player/Player.jsp?id_veiculo='+idVeiculo+'&video_sel='+id_video_sel;
	else
		if_video.src = '/video/player/PlayerEmb.jsp?id_veiculo='+idVeiculo+'&video_sel='+id_video_sel;
	
	if_video.onload = function() {
		if_perfil.onload = "";
        $.unblockUI();
	}
	$.blockUI({ message: '<h1 id="blocked"><img src="/images/busy.gif" /> Aguarde...</h1>'});
}

function carregaVeiculoTarget(idVeiculo, tipoGrafico) {
	var aux = "if_target" + (tipoGrafico == 1 ? "_velocidade" : "_ponto");
	var if_target = document.getElementById(aux);
	
	if_target.src = '/servlet/VeiculoTarget?id_veiculo='+idVeiculo+'&tipo_grafico='+tipoGrafico;
	if_target.onload = function() {
		if_target.onload = "";
        $.unblockUI();
	}
	$.blockUI({ message: '<h1 id="blocked"><img src="/images/busy.gif" /> Aguarde...</h1>'});
}
