var listaObliteracao = new Array();
var obliteracaoAtual = null;
var posIniMouseObliteracao = null;
var tamanhoTarja = null;
var contDiv_lista_imagens;
var lstMultiplaObliteracao = new Array();
var sMultiplasObliteracoes = "";
var qtdeObliteracoes;

function Obliteracao(idImagem, sequenciaObliteracao, x, y, largura, altura) {
	this.idImagem = idImagem; 
	this.sequenciaObliteracao = sequenciaObliteracao; 
	this.x = x; 
	this.y = y; 
	this.largura = largura; 
	this.altura = altura; 
}
//Autor: 		Luiz Fernando Amaral
//Data:			22/07/2014
//Descrição:	Obrigar a inclusão da obiteração na digitação e com um tamanho minimo.
function obrigaObliteracaoMinima(){
	
	if(lstMultiplaObliteracao.length == 0){
		alert("A OBLITERAÇÃO da imagem é necessária para prosseguir.");
		return false;
	}else{
		var max_altura = 0;
		var max_largura = 0;
		var at_altura = 0;
		var at_largura = 0;
		
		for ( var i = 0; i < lstMultiplaObliteracao.length; i++ ) {
			at_altura = parseInt(lstMultiplaObliteracao[i].altura);
			at_largura = parseInt(lstMultiplaObliteracao[i].largura);
			if (max_altura < at_altura)
				max_altura = at_altura;
			if (max_largura < at_largura)
				max_largura = at_largura;
		}
		
		if(max_altura < 10 || max_largura < 10){
			alert("Uma das OBLITERAÇÕES deve ter largura mínima de 10px e altura de 10px. " + 	
				  "\nLargura Atual: " + max_largura + "px e Altura Atual: " + max_altura + "px." +
				  "\n\nFavor informar corretamente pra prosseguir.");
			return false;
		}
	}
	return true;
}

function ajustaObliteracaoAtual(obliteracao) {
	obliteracaoAtual = obliteracao;
	if (obliteracaoAtual != null) {
		idImagem = obliteracao.idImagem;
		listaObliteracao[idImagem] = obliteracao;
	}
}

function confirmaObliteracao() {
    var com_captura_frontal = document.getElementById("com_captura_frontal");
    if ((obliteracaoAtual == null || !obliteracaoAtual.largura > 0) && com_captura_frontal.value == 'true') {
        return confirm("Imagem sem OBLITERAÇÃO, prosseguir assim mesmo?");
    } 
    return true;
}

function obliterar(x, y, largura, altura) {
    var div_obliteracao = document.getElementById("div_obliteracao");
    div_obliteracao.style.left = x+'px';
    div_obliteracao.style.top = y+'px';
    div_obliteracao.style.width = largura+'px';
    div_obliteracao.style.height = altura+'px';
}
function obliterarMiniatura(ind, x, y, largura, altura) {
	var img_veiculo = document.getElementById("img_veiculo");
	var img_panoramica = document.getElementById("img_panoramica_"+ind);
    var div_obliteracao_panoramica = document.getElementById("div_obliteracao_panoramica_"+ind);
    
    if (!img_panoramica)
    	return;
    
    var relacao = img_panoramica.height / img_veiculo.height;
    var offsetX = img_panoramica.offsetLeft;
    
    div_obliteracao_panoramica.style.left = ((x*relacao)+offsetX)+'px';
    div_obliteracao_panoramica.style.top = (y*relacao)+'px';
    div_obliteracao_panoramica.style.width = (largura*relacao)+'px';
    div_obliteracao_panoramica.style.height = (altura*relacao)+'px';
}
function escondeObliteracao() {
    if (posIniMouseObliteracao != null) //Se está expandindo não faz nada.
        return;
    var div_obliteracao = document.getElementById("div_obliteracao");
    div_obliteracao.style.backgroundColor = '';
    div_obliteracao.style.border = '1px dashed black';
}
function mostraObliteracao() {
    var div_obliteracao = document.getElementById("div_obliteracao");
    div_obliteracao.style.backgroundColor = 'black';
    div_obliteracao.style.border = '1px dashed black';
}
function mouseObliteracao(event,acao) {
	event.preventDefault();
	
	if (event.button == 0) {
		scrollTop = $(window).scrollTop();
	    switch (acao) {
			case 'down':
				iniciaObliteracao(event.pageX, event.pageY - scrollTop);
	            expandeObliteracao(event.pageX, event.pageY - scrollTop);
				break;
	        case 'up':
	        	fixaObliteracao();
	            break;
	        case 'move':
	        	expandeObliteracao(event.pageX, event.pageY - scrollTop);
	            break;
		}        
	}
	
	event.preventDefault();
}
function iniciaObliteracao(x, y) {
    var div_obliteracao = document.getElementById("div_obliteracao");

    if (div_obliteracao && div_obliteracao.style.display == "none")
        return;
    
    //getIdImagemSel está definida no JS multiplas_imagens.js
	if (!getIdImagemSel)
		alert('Função getIdImagemSel requerido.')

    var img_veiculo = document.getElementById("img_veiculo");
    var iniXY = getPos(img_veiculo);
    var mouseX = (x - iniXY["x"]);
    var mouseY = (y - iniXY["y"]);
    
    if (!obliteracaoAtual) {
    	idImagemSel = getIdImagemSel(); //Definido em multiplas_imagens.js
    	ajustaObliteracaoAtual(new Obliteracao(idImagemSel, 1, 0, 0, 0, 0));
    }

    posIniMouseObliteracao = {x: Math.max(0, mouseX), y: Math.max(0, mouseY)};
}
function expandeObliteracao(x, y) {
    if (posIniMouseObliteracao == null)
        return;

    var img_veiculo = document.getElementById("img_veiculo");
    var iniXY = getPos(img_veiculo);
    var mouseX = Math.max(0, (x - iniXY["x"]));
    var mouseY = Math.max(0, (y - iniXY["y"]));

    var posX = Math.min(mouseX, posIniMouseObliteracao["x"]);
    var posY = Math.min(mouseY, posIniMouseObliteracao["y"]);
    var larg = Math.abs(mouseX - posIniMouseObliteracao["x"]);
    var alt = Math.abs(mouseY - posIniMouseObliteracao["y"]);

    if ((larg+posX) > img_veiculo.width)
    	larg = img_veiculo.width - posX;
          
    if ((alt+posY) > img_veiculo.height)
        alt = img_veiculo.height - posY;

    obliterar(posX, posY, larg, alt);
}

function fixaObliteracao() {
    var txt_inconsistencia = document.getElementById("txt_inconsistencia");
    var txt_placa = document.getElementById("txt_placa");
	var div_obliteracao = document.getElementById("div_obliteracao");
    var img_veiculo = document.getElementById("img_veiculo");
    var div_erro_oblit = document.getElementById("div_erro_oblit");

    if (!tamanhoTarja)
		alert("A variável global 'tamanhoTarja' deve ser ajustada.");

    if (posIniMouseObliteracao == null)
        return;
    
    if (txt_placa)
        txt_placa.focus();
    else if (txt_inconsistencia)
        txt_inconsistencia.focus();

    var x = parseInt(div_obliteracao.style.left.substring(0, div_obliteracao.style.left.length-2));
    var y = parseInt(div_obliteracao.style.top.substring(0, div_obliteracao.style.top.length-2));
    var largura = div_obliteracao.style.width.substring(0, div_obliteracao.style.width.length-2);
    var altura = div_obliteracao.style.height.substring(0, div_obliteracao.style.height.length-2);
    var obliteracao = null;

    if (largura > 0 && altura > 0) {
		obliteracao = new Obliteracao (obliteracaoAtual.idImagem, obliteracaoAtual.sequenciaObliteracao, x, y, largura, altura);
    }
    else {
		obliteracao = new Obliteracao (obliteracaoAtual.idImagem, obliteracaoAtual.sequenciaObliteracao, 0, 0, 0, 0);
    } 
    
    ajustaObliteracaoAtual(obliteracao);
    
    lstMultiplaObliteracao.push(obliteracao);
    montaMultiplasObliteracoes(lstMultiplaObliteracao);
    
    posIniMouseObliteracao = null;
    
    if(div_erro_oblit) {
    	div_erro_oblit.style.visibility = "visible";
    }
    
    //Luiz Amaral 30/08/2014
    //Adiciona valores na DIV para ter o desenho final fixado na tela
    var div_obliteracaoFinal =  document.getElementById("div_obliteracao_"+contDiv_lista_imagens);
    div_obliteracaoFinal.style.left = x+'px';
    div_obliteracaoFinal.style.top = y+'px';
    div_obliteracaoFinal.style.width = largura+'px';
    div_obliteracaoFinal.style.height = altura+'px';
}

function verifObliteracao() {

	if (document.getElementById("cbObliteracao") && document.getElementById("cbObliteracao").checked == false) {
    	obliterar(0,0,0,0);
        return;
    }
    
    var img_veiculo = document.getElementById("img_veiculo");
    var idImagemSel = getIdImagemSel(); //Definido em multiplas_imagens.js
    var obliteracao = obliteracaoAtual;
    var iniXY = getPos(img_veiculo);

    if (obliteracao == null || (idImagemSel != obliteracao.idImagem)) {
		obliteracao = listaObliteracao[idImagemSel];
		ajustaObliteracaoAtual(obliteracao);
    }

    if (obliteracao != null) {
        obliterar(
                obliteracao.x,
                obliteracao.y,
                obliteracao.largura,
                obliteracao.altura
        );
    }
    else {
        obliterar(0,0,0,0);
    }
    verifObliteracaoMiniatura();
}
function verifObliteracaoMiniatura() {
	var lista = getListaImagens(); //Definido em multiplas_imagens.js
	
	for (i=0;i<lista.length;i++) {
		infoImagem = lista[i]; 
		if (infoImagem.tipoImagem == 'OBJ')
			continue;
		else {
			obliteracao = listaObliteracao[infoImagem.idImagem];
			if (obliteracao)
				obliterarMiniatura(i, obliteracao.x, obliteracao.y, obliteracao.largura, obliteracao.altura);
			else
				obliterarMiniatura(i, 0, 0, 0, 0);
		}
	}
}
function buscaObliteracaoProcesso(idInfracaoProcesso) {
    if (!(idInfracaoProcesso >= 0)) {
        return;
    }
	
    var params = {
    		id_infracao_processo: idInfracaoProcesso
        };
    
    buscaObliteracao(params);
}

function buscaObliteracaoInfracao(idInfracao) {
    if (!(idInfracao >= 0)) {
        return;
    }
    var params = {
    		id_infracao: idInfracao
        };
    
    buscaObliteracao(params);
}
function buscaObliteracao(params) {
    
   	listaObliteracao = new Array();
   	obliteracaoAtual = null;
   	limparDIVs();
   	
    verifObliteracao();
    
    buscaObliteracaoImagem(params, listaImagens[imagemSel].idImagem);
    
}
function buscaObliteracaoImagem(params, idImagem) {
	if (getIdImagemSel) {
    	params["id_imagem"] = idImagem;
    }
	
    var numero_obliteracoes = 0;
    
    $.ajax({url: '/ajax/InfoInfracaoObliteracao', data: params, success: function(xml) {
        if ($("ID_IMAGEM",xml).text()) {
        	idImagem = parseInt($("ID_IMAGEM",xml).text());
        	numero_obliteracoes = parseInt($("NUMERO_OBLITERACOES",xml).text());
        	
        	for ( var i = 1; i <= numero_obliteracoes; i++ ) {
        		
        		var x = parseInt($("X_" + i,xml).text());
        		var y = parseInt($("Y_" + i,xml).text());
        		var largura = parseInt($("LARGURA_" + i,xml).text());
        		var altura = parseInt($("ALTURA_" + i,xml).text());
        		
        		lstMultiplaObliteracao.push(new Obliteracao(idImagem, i, x, y, largura, altura));
        		
        		var t = i - 1;
        		
        		var div_imagem = $("#div_imagem_sub");
        		if(div_imagem.length == 0) {
        			div_imagem = $("#div_imagem");
        		}
        		div_imagem.append(createDiv_Lista_imagens(t));
        		
        	    var div_obliteracao = document.getElementById("div_obliteracao_" + t);
        	    div_obliteracao.style.left = x+'px';
        	    div_obliteracao.style.top = y+'px';
        	    div_obliteracao.style.width = largura+'px';
        	    div_obliteracao.style.height = altura+'px';
        	}
        	
        	montaMultiplasObliteracoes(lstMultiplaObliteracao);
        	
            ajustaObliteracaoAtual(new Obliteracao(idImagem, 1, 0, 0, 0, 0));
        	verifObliteracao();
        }
    }, async: false});
}

function enviaObliteracao(idInfracaoProcesso) {
	
	for ( var idImagem in listaObliteracao) {
		enviaObliteracaoImagem(idInfracaoProcesso, 
							   listaObliteracao[idImagem],
							   sMultiplasObliteracoes, 
							   qtdeObliteracoes);
	}
}
function enviaObliteracaoImagem(idInfracaoProcesso, 
								obliteracao, 
								sMultiplasObliteracoes, 
								qtdeObliteracoes) 
{
	
	var div_img_veiculo = document.getElementById("img_veiculo");
	var img_veiculo_altura = div_img_veiculo.attributes["height"].value;
	
    var params = {
    		permanente: 0,
    		id_infracao_processo: idInfracaoProcesso,
    		id_imagem: obliteracao.idImagem,
    		sequencia_obliteracao: obliteracao.sequenciaObliteracao,
    		x: obliteracao.x,
    		y: obliteracao.y,
    		largura: obliteracao.largura,
    		altura: obliteracao.altura,
    		img_veiculo_altura: img_veiculo_altura,
    		sMultiplasObliteracoes: sMultiplasObliteracoes, 
    		qtdeObliteracoes: qtdeObliteracoes
        };

	$.ajax({url: '/ajax/processamento/ProcessarObliteracao', data: params, success: function(xml) {
		trataRetorno(xml);
    }, async: false});
}
function enviaObliteracaoValidacao(idInfracaoProcesso) {
	for ( var idImagem in listaObliteracao) {
		enviaObliteracaoImagemValidacao(idInfracaoProcesso, 
										listaObliteracao[idImagem],
										sMultiplasObliteracoes,
									    qtdeObliteracoes);
	}
}
function enviaObliteracaoImagemValidacao(idInfracaoProcesso, 
										 obliteracao, 
										 sMultiplasObliteracoes, 
										 qtdeObliteracoes) {
	
	var div_img_veiculo = document.getElementById("img_veiculo");
	var img_veiculo_altura = div_img_veiculo.attributes["height"].value;
	
	var chk_erro_oblit = document.getElementById("chk_erro_oblit");
	var erro_oblit = false;
	
	if(chk_erro_oblit)
		erro_oblit = chk_erro_oblit.checked;
	
	var div_infracao_des = document.getElementById("div_infracao_des");
	var infracao_des = '0';
	if(div_infracao_des && div_infracao_des.innerHTML != '')
		infracao_des = div_infracao_des.innerHTML;
	
    var params = {
    		permanente: 1,
    		id_infracao_processo: idInfracaoProcesso,
    		id_imagem: obliteracao.idImagem,
    		sequencia_obliteracao: obliteracao.sequenciaObliteracao,
    		x: obliteracao.x,
    		y: obliteracao.y,
    		largura: obliteracao.largura,
    		altura: obliteracao.altura,
    		img_veiculo_altura: img_veiculo_altura,
    		erro_oblit: erro_oblit,
    		sMultiplasObliteracoes: sMultiplasObliteracoes,
    		qtdeObliteracoes: qtdeObliteracoes,
    		infracao_des: infracao_des 
        };

	$.ajax({url: '/ajax/processamento/ProcessarObliteracao', data: params, success: function(xml) {
		trataRetorno(xml);
    }, async: false});
}


//Autor: 		Luiz Fernando Amaral
//Data:			29/08/2014
//Descrição:	Monsta o texto com as obliterações para enviar para a Servlet
function montaMultiplasObliteracoes(MultOBL){
	
	sMultiplasObliteracoes = "";
	
    for(var i=0; i < MultOBL.length; i++ ){
    	
    	qtdeObliteracoes = lstMultiplaObliteracao.length;
    	sMultiplasObliteracoes = sMultiplasObliteracoes +
    							 MultOBL[i].x + "," + 
    							 MultOBL[i].y + "," + 
    							 MultOBL[i].largura + "," + 
    							 MultOBL[i].altura + "," + 
    							 MultOBL[i].sequenciaObliteracao + ";";
    }
}

function createDiv_Lista_imagens(num){
    /*
     Criamos a variavel, e atribuimos os campos que serão criados;
     Utilizamos o colchetes nos nomes do campos para informar que os dados em forma de array;
     Adiciona uma div, para que nela seja criado novos campos extras;
    */
    var html  =  '<div class="items" id="div_obliteracao_'+num+'"';
    	html +=  'style="background-color: #4F4F4F; position: absolute;" >';
        html +=  '<div>';
        
        return html;

}

//Autor: 		Luiz Fernando Amaral
//Data:			30/08/2014
//Descrição:	Criar DIV´s dinamica para Multiplas Obliterações
$(function(){
  
  //cria uma função para conta os campos criados
  function getTotalItems(){
      //Contamos o total de campos, e diminuimos 1
      //Porque o array é iniciado seu indice com 0
      return $(".items").length;
  }
  
  //Evento para chamar função de DIV´s dinâmicas
  $("#div_imagem").mousedown(function(){
      contDiv_lista_imagens = getTotalItems();
      $("#div_imagem").append(createDiv_Lista_imagens(contDiv_lista_imagens));

      return false;
  });
  
  //Limpa as DIVs que já existem sem precisar atualizar a pagina
  $("#bt_LimparDIVs").click(function(){
	  
	  for ( var i = 0; i <= contDiv_lista_imagens; i++ ){
		  $("#div_obliteracao_" + i).remove();
	  }
	  zerarInfo();
  });

});

//Limpa as DIVs que já existem sem precisar atualizar a pagina
function limparDIVs(){
	  for ( var i = 0; i <= contDiv_lista_imagens; i++ ){
		  $("#div_obliteracao_" + i).remove();
	  }
	  zerarInfo();
}

//Limpa todas as informações das multiplas obliterações
function zerarInfo(){
    contDiv_lista_imagens = 0;
    qtdeObliteracoes = 0;
    sMultiplasObliteracoes = "";
    
    while(lstMultiplaObliteracao.length > 0) {
    	lstMultiplaObliteracao.pop();
    }
    
    var div_obliteracao = document.getElementById("div_obliteracao");
    div_obliteracao.style.left = 0+'px';
    div_obliteracao.style.top = 0+'px';
    div_obliteracao.style.width = 0+'px';
    div_obliteracao.style.height = 0+'px';
}


function escondeObliteracaoPAN() {
	
//	alert("qtdeObliteracoes: " + qtdeObliteracoes);
//	alert("lstMultiplaObliteracao: " + lstMultiplaObliteracao.length);
	
	for (var i = 0; i <= qtdeObliteracoes; i++) {
		var div_obliteracao_n = document.getElementById("div_obliteracao_" + i);
		if (div_obliteracao_n) {
			div_obliteracao_n.style.visibility = 'hidden';
		}
	}
}

function mostraObliteracaoOBJ() {
	
//	alert("qtdeObliteracoes: " + qtdeObliteracoes);
//	alert("lstMultiplaObliteracao: " + lstMultiplaObliteracao.length);
	
	for (var i = 0; i <= qtdeObliteracoes; i++) {
		var div_obliteracao_n = document.getElementById("div_obliteracao_" + i);
		if (div_obliteracao_n) {
			div_obliteracao_n.style.visibility = 'visible';
		}
	}
}


