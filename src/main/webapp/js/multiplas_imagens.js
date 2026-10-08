var listaImagens = new Array();
var imagemSel = 0;
var ref_log_processamento = null;
var idInfracao = null;

function Imagem(idImagem, tipoImagem, descarga, pasta) {
	this.idImagem = idImagem; 
	this.tipoImagem = tipoImagem; 
	this.descarga = descarga; 
	this.pasta = pasta; 
}

function getIdImagemSel() {
	if (listaImagens.length == 0)
		return null;
	else
		return listaImagens[imagemSel].idImagem;
}

function getListaImagens() {
	return listaImagens;
}

function selecionaImagem() {
	var id_imagem = document.getElementById("id_imagem");
	if (!id_imagem)
		alert('Elemento id_imagem requerido.')

	if (listaImagens.length == 0)
		return;
	
	if (listaImagens[imagemSel].tipoImagem == 'OBJ')
		id_imagem.value = listaImagens[imagemSel].idImagem;
	
	atualizaListaImagens();
}

function proximaListaImagens() {
    imagemSel++;
    if (imagemSel >= listaImagens.length)
    	imagemSel = 0;
     
    carregaImagem(listaImagens[imagemSel].idImagem, listaImagens[imagemSel].descarga > 0);
}
function anteriorListaImagens() {
    imagemSel--;
    if (imagemSel < 0)
    	imagemSel = listaImagens.length-1;
     
    carregaImagem(listaImagens[imagemSel].idImagem, listaImagens[imagemSel].descarga > 0);
}
function mostraImagem(numImagem) {
	if (numImagem < 0 || numImagem >= listaImagens.length)
		return;
     
	imagemSel = numImagem;

//	if (listaImagens[imagemSel].descarga > 0)
//		mostraDIVEnviaImagemDescarregada(listaImagens[imagemSel].idImagem, listaImagens[imagemSel].pasta);

	carregaImagem(listaImagens[imagemSel].idImagem, listaImagens[imagemSel].descarga > 0, listaImagens[imagemSel].tipoImagem);
}

function atualizaListaImagens() {
	var id_imagem = document.getElementById("id_imagem");
	var div_lista_imagens = document.getElementById("div_lista_imagens");
    var div_obliteracao = document.getElementById("div_obliteracao");
	var cbReadOnly = "";
	var cbSel = "";

	if (!id_imagem)
		alert('Elemento id_imagem requerido.')
	if (!div_lista_imagens)
		alert('Elemento div_lista_imagens requerido.');
	if (!id_imagem)
		alert('Elemento id_imagem requerido.');
	
    div_lista_imagens.innerHTML = "";

	if (listaImagens.length < 2)
        return;
    
    var sHTML = "<table class='tabela_branca' width='100%'>";
    sHTML += "<tr>";
    for (var i=0; i<listaImagens.length; i++) {
        sHTML += "<td class='visualiza_campo'>";
        var marca = "text-decoration: none;";
        if (i == this.imagemSel) {
            marca = "text-decoration: underline;"
        }
        sHTML += "<a style='"+marca+"' href='javascript:mostraImagem("+i+")'>"+(i+1)+"</a>";
        sHTML += "</td>";
    }
    
    if (id_imagem.readOnly)
    	cbReadOnly = "disabled";
    if (listaImagens[imagemSel].idImagem == id_imagem.value)
        cbSel = "checked";
    
    sHTML += "<td class='label_campo' style='text-align: right' width='80%'>";
    sHTML += "<input type='checkbox' onchange='selecionaImagem()' "+cbSel+" "+cbReadOnly+" style='vertical-align: text-bottom'/>Imagem&nbsp;selecionada.";
    sHTML += "</td>";
    sHTML += "</tr>";
    sHTML += "</table>";
    div_lista_imagens.innerHTML = sHTML;
    
    if (div_obliteracao) {
	/*	if (listaImagens[imagemSel].tipoImagem == 'OBJ')
			div_obliteracao.style.display = "";
		else if (listaImagens[imagemSel].tipoImagem == 'PAN')
			div_obliteracao.style.display = "none";
	 */
    	verifObliteracao(); //Definido em obliteracao.js
    }
    
}
function atualizaListaImagensMiniatura() {
	var div_miniaturas = document.getElementById("div_miniaturas");
	if (!div_miniaturas)
		return;

	var sHTML = "";

    for (var i=0; i<listaImagens.length; i++) {
		sHTML += "<div id='div_panoramica_"+i+"' style='position: relative;'>";
		sHTML += "<div id='div_obliteracao_panoramica_"+i+"' style='background-color: black; position: absolute;'></div>";
	    sHTML += "<img id='img_panoramica_"+i+"' width='240' height='180' onload='this.src = \"/ajax/ImgVeiculo?id_imagem="+listaImagens[i].idImagem+"&largura=240&altura=180\";this.onload = "+
	    "function() {div_infracao = document.getElementById(\"div_infracao\"); id_infracao = parseInt(div_infracao.innerHTML); if (ref_log_processamento == null || (getIdentificador(ref_log_processamento) != id_infracao)) ref_log_processamento = iniciaTempoProcessamento(id_infracao,1,\"PROCESSAMENTO\");};' src='/images/ajax-loader.gif' onclick='carregaImagem("+listaImagens[i].idImagem+",true,"+"\""+listaImagens[i].tipoImagem+"\""+")'>";
	    sHTML += "</div>";
	}
	
	div_miniaturas.innerHTML = sHTML;
}
function limpaListaImagens() {
    listaImagens = new Array();
    imagemSel = 0;
    atualizaListaImagens(); 
    atualizaListaImagensMiniatura();
}
function adicionaListaImagem(idImagem, tipoImagem, descarga, pasta) {
	listaImagens.push(new Imagem(idImagem, tipoImagem, descarga, pasta)) 
}
function carregaListaImagensInfracao(idInfracao, idImagemSel) {
	limpaListaImagens();

	var ref_log_lista_imagem_sel = iniciaTempoProcessamento(idInfracao,0,'LISTA_IMAGEM [ID_IMAGEM_SEL:'+idImagemSel+']');
	$.ajax({url: '/ajax/InfoVeiculoImagem?id_infracao='+idInfracao, success: function(xml) {
		
       var contaImagem = $("CONTA_IMAGEM",xml).text();
       var selecionou = false;
        
       if (contaImagem.length > 0 && contaImagem > 0) {
	        for (var i=0; i<contaImagem; i++) {
	        	var idImagem = $("ID_IMAGEM_"+i,xml).text();
	            adicionaListaImagem(idImagem, $("TIPO_IMAGEM_"+i,xml).text(), $("DESCARGA_"+i,xml).text(), $("PASTA_"+i,xml).text());
	
	            if (idImagemSel && idImagem == idImagemSel) {
	            	mostraImagem(i);
	            	selecionaImagem();
	            	selecionou = true;
	            }
	        }
	        this.idInfracao = idInfracao; 
	        //Caso não tenho encontrado uma na seleção, seleciona a primeira por padrão.
	        if (!selecionou) {
//            	mostraImagem(0);
            	selecionaImagem();
	        }
	        atualizaListaImagens();
	        if (contaImagem > 1)
	        {
	        	atualizaListaImagensMiniatura();
	        }
       }
       else {
    	   var img_veiculo = document.getElementById("img_veiculo");
	       if (!img_veiculo)
	    	   alert('Elemento img_veiculo requerido.');
            img_veiculo.src = "/images/img_indisponivel.jpg";
       }
       finalizaTempoProcessamento(ref_log_lista_imagem_sel);
       
    }, async: false}); //Só pode carregar a imagem depois que carregou a lista de imagens
	
}
function carregaListaImagensVeiculo(idVeiculo) {
	limpaListaImagens();

	var ref_log_lista_imagem = iniciaTempoProcessamento(idVeiculo,0,'LISTA_IMAGEM');
	$.get('/ajax/InfoVeiculoImagem', { id_veiculo: idVeiculo }, function(xml) {
		limpaListaImagens();
        var contaImagem = $("CONTA_IMAGEM",xml).text();
        for (var i=0; i<contaImagem; i++) {
            adicionaListaImagem($("ID_IMAGEM_"+i,xml).text(), $("TIPO_IMAGEM_"+i,xml).text(), $("DESCARGA_"+i,xml).text(), $("PASTA_"+i,xml).text());
        }
        
        atualizaListaImagens();
        if (contaImagem > 1)
        {
        	atualizaListaImagensMiniatura();
        }
        finalizaTempoProcessamento(ref_log_lista_imagem);
    });
}

function carregaImagem(idImagem, mataCache, tipoImagem) {
	var cachekiller = Math.floor(Math.random()*10000);
	var div_infracao = document.getElementById("div_infracao");
	
    if (div_infracao)
    	id = parseInt(div_infracao.innerHTML);
    else
        var id = parseInt(idImagem); 

	var img_veiculo = document.getElementById("img_veiculo");
	if (!img_veiculo)
		alert('Elemento img_veiculo requerido.');

	// XXX: X2: Cancela Ajuste
	if (img_veiculo.nodeName == "CANVAS") {
		CancelaAjuste();
		img_veiculo = document.getElementById("img_veiculo");
	}
	
    img_veiculo.src = "";
    
    console.log('IMAGEM ['+idImagem+']');

	var ref_log_imagem = iniciaTempoProcessamento(id,0,'IMAGEM ['+idImagem+']');
	img_veiculo.onload = function() {

	    img_veiculo.src = "/ajax/ImgVeiculo?id_imagem="+idImagem+"&matacache="+cachekiller;
	    img_veiculo.onload = function() {
            finalizaTempoProcessamento(ref_log_imagem);
            	
            try {
            	buscaImagemAjuste();
            }
            catch(e) {}
            
            if (ref_log_processamento == null || (getIdentificador(ref_log_processamento) != id))
            	ref_log_processamento = iniciaTempoProcessamento(id,1,'PROCESSAMENTO');
	    }
	}
    img_veiculo.src = "/images/ajax-loader.gif";
    
    for (var i=0; i<listaImagens.length; i++) {
    	if (listaImagens[i].idImagem == idImagem) {
    		this.imagemSel = i;
    		break;
    	}
    }
    
	atualizaListaImagens();
	
    if (tipoImagem == 'PAN') {
    	escondeObliteracaoPAN();
    } else {
    	mostraObliteracaoOBJ();
    }
}

function carregaImagemZoom(idImagem, mataCache) {
	var cachekiller = Math.floor(Math.random()*10000);
	var div_infracao = document.getElementById("div_infracao");
	
    if (div_infracao)
    	id = parseInt(div_infracao.innerHTML);
    else
        var id = parseInt(idImagem); 

	var img_veiculo = document.getElementById("img_veiculo_zoom");
	if (!img_veiculo)
		alert('Elemento img_veiculo requerido.');

	img_veiculo.src = "/ajax/ImgVeiculo?id_imagem="+idImagem+"&matacache="+cachekiller+"&zoom=3";
	
    for (var i=0; i<listaImagens.length; i++) {
    	if (listaImagens[i].idImagem == idImagem) {
    		this.imagemSel = i;
    		break;
    	}
    }
    
	atualizaListaImagens();
}

function carregaImagemDesob(idImagem, mataCache) {
	var cachekiller = Math.floor(Math.random()*10000);
	var div_infracao = document.getElementById("div_infracao");
	
    if (div_infracao)
    	id = parseInt(div_infracao.innerHTML);
    else
        var id = parseInt(idImagem); 

    var div_infracao_des = document.getElementById("div_infracao_des");
    
	var img_veiculo 	= document.getElementById("img_veiculo_des");
	if(div_infracao_des && div_infracao_des.innerHTML != '')
		img_veiculo		= document.getElementById("img_veiculo");
	
	if (!img_veiculo)
		alert('Elemento img_veiculo requerido.');

    img_veiculo.src = "";

	var ref_log_imagem = iniciaTempoProcessamento(id,0,'IMAGEM ['+idImagem+']');
	img_veiculo.onload = function() {

	    img_veiculo.src = "/ajax/ImgVeiculo?id_imagem="+idImagem+"&matacache="+cachekiller+"&desobliterado=1";
	    img_veiculo.onload = function() {
            finalizaTempoProcessamento(ref_log_imagem);
            	
            if (ref_log_processamento == null || (getIdentificador(ref_log_processamento) != id))
            	ref_log_processamento = iniciaTempoProcessamento(id,1,'PROCESSAMENTO');
	    }
	}
    img_veiculo.src = "/images/ajax-loader.gif";
}

function mostraDIVEnviaImagemDescarregada(idImagem, pasta) {
	var larguraDIV = 300;
	var alturaDIV = 30;
	var img_veiculo = document.getElementById("img_veiculo");
	if (!img_veiculo)
		alert('Elemento img_veiculo requerido.');

	var newdiv = document.createElement('div');
    newdiv.setAttribute('id', 'div_envia_imagem_descarregada');
    newdiv.style.width = larguraDIV+'px';
    newdiv.style.height = alturaDIV+'px';;
    newdiv.style.position = "absolute";
    newdiv.style.left = ((img_veiculo.width-larguraDIV) / 2)+'px';
    newdiv.style.top = ((img_veiculo.height-alturaDIV+80) / 2)+'px';
    newdiv.innerHTML = "<a href='javascript:enviaImagemDescarregada("+idImagem+","+pasta+")'>Enviar imagem descarregada...</a>";
    img_veiculo.parentNode.appendChild(newdiv);
}
function recarregaImagemDescarregada(idImagem) {
	var applet_CSXFileUpload = document.getElementById("applet_CSXFileUpload");
	document.body.removeChild(applet_CSXFileUpload);
	carregaImagem(idImagem, true);	
}
function enviaImagemDescarregada(idImagem, pasta) {
	var applet = document.createElement("applet");
	applet.setAttribute("id", "applet_CSXFileUpload");
	applet.setAttribute("code", "com.consilux.init.CSXFileUpload");
	applet.setAttribute("archive", "/csxfileupload/CSXFileUpload-0.0.1-SNAPSHOT-jar-with-dependencies.jar");
	applet.setAttribute("width", "0");
	applet.setAttribute("height", "0");
	
	var p1 = document.createElement("param");
	p1.setAttribute("name", "url_destino");
	//Varíavel jsessionid ajustada no cabecalho.jsp.
	p1.setAttribute("value", "/servlet/UpImgVeiculo;jsessionid="+jsessionid+"?id_imagem="+idImagem);
	applet.appendChild(p1);

	var p2 = document.createElement("param");
	p2.setAttribute("name", "sub_diretorio");
	p2.setAttribute("value", "imagens/"+pasta);
	applet.appendChild(p2);

	var p3 = document.createElement("param");
	p3.setAttribute("name", "mascara");
	p3.setAttribute("value", "*_"+idImagem+".jpg");
	applet.appendChild(p3);

	var p4 = document.createElement("param");
	p4.setAttribute("name", "js_saida");
	p4.setAttribute("value", "recarregaImagemDescarregada("+idImagem+")");
	applet.appendChild(p4);
	
	document.body.appendChild(applet);
}

