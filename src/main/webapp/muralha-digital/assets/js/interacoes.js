   	var EQUIPAMENTO_PESAGEM_VIA_EXPRESSA 		= 1005;
   	var EQUIPAMENTO_PESAGEM_PRECISAO_FUGA 		= 1002;
   	var PISTA_EQUIPAMENTO_PESAGEM_PRECISAO 		= 4;
   	var EQUIPAMENTO_BLOQUEIOVIARIO 				= 1003;
   	var PISTA_EQUIPAMENTO_PESAGEM_EXPRESSA		= 2;  

	url_voltar = "../";   

   $(document).ready(function() 
   {    
	   iniciaConexao();
	   
	   aguardarCarregamento();
	   
	   setTimeout(paginaCarregada, 1800);
   });    	

   function aguardarCarregamento(){
	   console.log("Aguardando carregamento...");
	   var carregando = document.getElementById("carregando");
	   carregando.style.display = "block";
	   var interacoes = document.getElementById("interacoes");
	   interacoes.style.display = "none";
   }
   
   function paginaCarregada(){
	   console.log("Pagina carregada!");
	   var carregando = document.getElementById("carregando");
	   carregando.style.display = "none";
	   var interacoes = document.getElementById("interacoes");
	   interacoes.style.display = "block";
   }
   
   function onOpen(event) 
   {
	   socket.send("Cliente_Consilux_VEICULOS-INTERACAOREAL");
   }

   function onError(event) 
   {
          alert(event.data);
   }		   
	
	function onRecebeDados(event) 
	{
		var xmlDoc = $.parseXML( event.data );
		var $xml = $(xmlDoc);
		
		var idInteracao = $xml.find('idInteracaoBD').text();
		var placaComum 	= $xml.find('placaEmComum').text();

		console.log("NOVA INTERAÇÃO. ID: " + idInteracao);

		idTela = 1;
		atualizaCampos();	
		
		document.getElementById("img_" 				+ 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + idTela).src 			= "../images/consilux_fundo_geral.jpg";
		document.getElementById("img_" 				+ 		EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + idTela).src 	= "../images/consilux_fundo_geral.jpg"
		document.getElementById("img_" 				+ 		EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + idTela).src 	= "../images/consilux_fundo_geral.jpg"
	   	document.getElementById("titulo1_" 			+ 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + idTela).innerHTML 	= "-";
   		document.getElementById("titulo2_" 			+ 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + idTela).innerHTML 	= "-";
   		document.getElementById("placa_" 			+ 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + idTela).innerHTML 	= "";
   		document.getElementById("data_" 			+ 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + idTela).innerHTML 	= "";
   		document.getElementById("situacao_" 		+ 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + idTela).innerHTML 	= "";
		
		$("#veiculo_iteracao_" + idTela).fadeToggle("fast");
		document.getElementById("idInteracao_" + idTela).innerHTML 		= idInteracao;

		$xml.find('veiculoLista').find('veiculo').each(function()
		{
			
			var $veiculo 	= $(this);
			
			var idLocal 	= parseInt($veiculo.find('idLocal').text());	
			var idInterno 	= parseInt($veiculo.find('idInterno').text());				
			var pista 		= parseInt($veiculo.find('pista').text());
			var velocidade 	= parseInt($veiculo.find('velocidade').text());
			var pbt 		= parseInt($veiculo.find('pesagem').find('PBT').text());

			//console.log("idLocal: " + idLocal + " idVeiculo: " + idInterno);
			
			populaTitulos(idLocal, pista, idTela);

			$veiculo.find('imagens').find('listaImagens').find('imagem').each(function() 
			{
				
				var tpImagem = $(this).find('tpImagem').text();
				
				if(tpImagem == 'pan')
				{
					var imagem = _base64ToArrayBuffer($(this).find('strImagemBuffer').text());			
					atualizaImagem(imagem, 'img_' + idLocal + "_" + idTela);	
				}
									
			});		

			
			var data 						= $veiculo.find('data').text();
			var placa 						= $veiculo.find('placa').text();
			var situacaoPMVSinaleiroSaida 	= $veiculo.find('situacao_PMV_Sinaleiro_Saida').text();			
			
			//Informações gerais
			document.getElementById("placa_" 			+ idLocal + "_" + idTela).innerHTML = placaComum;
			document.getElementById("data_" 			+ idLocal + "_" + idTela).innerHTML = data;
			document.getElementById("situacao_" 		+ idLocal + "_" + idTela).innerHTML = AjustaSituacaoSinalizacao(situacaoPMVSinaleiroSaida);
			
		});
		
		$("#veiculo_iteracao_" + idTela).fadeIn("fast");
	}	
	
	function atualizaImagem(imagem, obj) 
	{

		var img = document.getElementById(obj);
		var blob     = new Blob([imagem.buffer], {type: 'image/png'});
		
		var reader = new FileReader();
	  	reader.onload = function(e) {img.src = e.target.result;};
	  	reader.readAsDataURL(blob); 
	  	
	  	blob = null;
	  	reader = null;
	}
	
	function _base64ToArrayBuffer(base64) {
	    var binary_string =  window.atob(base64);
	    var len = binary_string.length;
	    var bytes = new Uint8Array( len );
	    for (var i = 0; i < len; i++)        {
	        bytes[i] = binary_string.charCodeAt(i);
	    }
	    
	    return bytes;
	}
	
	//Quando houver menos imagens porque não passou no ultimo radar
	//então imprime a imagem original
	function imprimeImagensPadrao(qtdeImagens, mainId)
	{
		for (var i = 1; i <= 4; i++) 
		{
			if(qtdeImagens < i)
			{
				document.getElementById("img_" + i + "_" + mainId).src					=	"../images/consilux_fundo_geral.jpg";
				document.getElementById("infracoes_" + (i-1) + "_" + mainId).innerHTML 	= 	"Infração: -";
			}
		}
	}
	
	function atualizaCampos()
	{
		
		for (var i = 4; i > 1; i--) 
		{
			
			//Informações gerais
	   		document.getElementById("idInteracao_"  + i).innerHTML 		= document.getElementById("idInteracao_"  + (i-1)).innerHTML;

	   		//Atualizando campos do equipamento de Pesagem Via Expressa
	   		document.getElementById("titulo1_" 			+ 		EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + i).innerHTML 	= document.getElementById("titulo1_" + 		EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + (i-1)).innerHTML;
	   		document.getElementById("titulo2_" 			+ 		EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + i).innerHTML 	= document.getElementById("titulo2_" + 		EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + (i-1)).innerHTML;
	   		document.getElementById("img_" 				+ 		EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + i).src 		= document.getElementById("img_" + 			EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + (i-1)).src;
	   		document.getElementById("placa_" 			+ 		EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + i).innerHTML 	= document.getElementById("placa_" + 		EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + (i-1)).innerHTML ;
	   		document.getElementById("data_" 			+ 		EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + i).innerHTML 	= document.getElementById("data_" +			EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + (i-1)).innerHTML;
	   		document.getElementById("situacao_" 		+ 		EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + i).innerHTML 	= document.getElementById("situacao_" +		EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + (i-1)).innerHTML;

	   		//Atualizando campos Balança de Precisão
	   		document.getElementById("titulo1_" 			+ 		EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + i).innerHTML 	= document.getElementById("titulo1_" + 		EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + (i-1)).innerHTML;
	   		document.getElementById("titulo2_" 			+ 		EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + i).innerHTML 	= document.getElementById("titulo2_" + 		EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + (i-1)).innerHTML;
	   		document.getElementById("img_" 				+ 		EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + i).src 		= document.getElementById("img_" + 			EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + (i-1)).src;
	   		document.getElementById("placa_" 			+ 		EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + i).innerHTML 	= document.getElementById("placa_" + 		EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + (i-1)).innerHTML ;
	   		document.getElementById("data_" 			+ 		EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + i).innerHTML 	= document.getElementById("data_" +			EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + (i-1)).innerHTML;
	   		document.getElementById("situacao_" 		+ 		EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + i).innerHTML 	= document.getElementById("situacao_" +		EQUIPAMENTO_PESAGEM_PRECISAO_FUGA + "_" + (i-1)).innerHTML;
	   		
	   		//Atualizando campos Bloqueio Viario
	   		document.getElementById("titulo1_" 			+ 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + i).innerHTML 	= document.getElementById("titulo1_" + 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + (i-1)).innerHTML;
	   		document.getElementById("titulo2_" 			+ 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + i).innerHTML 	= document.getElementById("titulo2_" + 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + (i-1)).innerHTML;
	   		document.getElementById("img_" 				+ 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + i).src 			= document.getElementById("img_" + 			EQUIPAMENTO_BLOQUEIOVIARIO + "_" + (i-1)).src;
	   		document.getElementById("placa_" 			+ 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + i).innerHTML 	= document.getElementById("placa_" + 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + (i-1)).innerHTML ;
	   		document.getElementById("data_" 			+ 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + i).innerHTML 	= document.getElementById("data_" +			EQUIPAMENTO_BLOQUEIOVIARIO + "_" + (i-1)).innerHTML;
	   		document.getElementById("situacao_" 		+ 		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + i).innerHTML 	= document.getElementById("situacao_" +		EQUIPAMENTO_BLOQUEIOVIARIO + "_" + (i-1)).innerHTML;
			
		}

	}
	
	function validaInteracaoJaExistente(placaServidor)
	{
		var ret = 0;
		
		for (var i = 4; i > 1; i--) 
		{
			placaTela = document.getElementById("placa_" + i).innerHTML;
			
			console.log("Executando comparativos de placas. PlacaTela: " + placaTela + " placaServidor: " + placaServidor);
			
			if(placaTela == placaServidor)
			{
				console.log("Placas iguais. Atualizando interação em tela. Placa: " + placaServidor + " Posição: " + i);
				ret = i;
				break;
			}		
		}
		
		return ret;
	}	
	
	function populaTitulos(idLocal, pista, idTela)
	{
	   	
	   	if(idLocal == EQUIPAMENTO_PESAGEM_VIA_EXPRESSA)
	   	{
	   		document.getElementById("titulo1_" + EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + idTela).innerHTML = 'Estação Controle em Pista';
	   		document.getElementById("titulo2_" + EQUIPAMENTO_PESAGEM_VIA_EXPRESSA + "_" + idTela).innerHTML = 'Estação Controle em Pista';
	   	}
	   	else if(idLocal == EQUIPAMENTO_PESAGEM_PRECISAO_FUGA)
	   	{
	   		if(pista != 4)
	   		{
	   			document.getElementById("titulo1_1002_" + idTela).innerHTML = 'Controle Fuga em Pista';
	   			document.getElementById("titulo2_1002_" + idTela).innerHTML = 'Controle Fuga em Pista';
	   		}
	   		else
	   		{
	   			document.getElementById("titulo1_1002_" + idTela).innerHTML = 'Estação Pesagem Precisão';
	   			document.getElementById("titulo2_1002_" + idTela).innerHTML = 'Estação Pesagem Precisão';
	   		
	   		}
	   	}	   		
	   	else
	   		document.getElementById("titulo1_1003_" + idTela).innerHTML = 'Controle Fuga no Posto';
	   	
	}
	
	function abrePaginaDetalhada(idLista)
	{
		var idVeic =  document.getElementById("idInteracao_"+idLista).innerHTML;
		
		if( parseInt (idVeic) > 0 )
			var w = window.open('interacoesDetalhado.jsp?idInteracao='+idVeic,'_blank');		
	}	
	
	function detalharInteracaoById(idRegistroBD)
	{
		console.log("Chamando tela para detalhar interação. ID: " + idRegistroBD);		
		var w = window.open('interacoesDetalhado.jsp?idInteracao='+idRegistroBD,'_blank');
	}
	


