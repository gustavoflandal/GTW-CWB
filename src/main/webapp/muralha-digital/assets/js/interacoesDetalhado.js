   url_voltar = "../";
   var idInteracao = 0;
   var EQUIPAMENTO_PESAGEM_VIA_EXPRESSA 	= 1005;
   var EQUIPAMENTO_PESAGEM_PRECISAO_FUGA 	= 1002;
   var EQUIPAMENTO_BLOQUEIOVIARIO 			= 1003;
   var PISTA_PESAGEM_PRECISAO				= 4;
   var PISTA_PESAGEM_EXPRESSA				= 2;
   
   var qfv_vazio_cinza_Expressa				= "";
   var qfv_vazio_cinza_Precisao				= "";

   $(document).ready(function() 
   {    
	   iniciaConexao();
	   carregaInfoInicial();
   }); 
   
   function carregaInfoInicial()
   {
		//Obtendo a placa enviada de uma pagina para outra
		var params = window.location.href.substring(window.location.href.indexOf('?')+1);
		params = params.split('|');
		
		for(var i=0; i<params.length; i++) 
		{
		  var pair = params[i].split('=');
		  var key = pair[0];
		  var value = pair[1];
		}	
		
		idInteracao = value;
   }

   
   	function onOpen(event) 
   	{
   		if(idInteracao != "")
   			socket.send("Cliente_Consilux_VEICULOS-INTERACAOREAL-ID-" + idInteracao);
   	}

  	function onError(event) 
   	{
          alert(event.data);
   	}		 
  	
  	var pesosComparaExpr;
  	var pesosComparaPrec
  	
	function onRecebeDados(event) 
	{
	  	pesosComparaExpr = '';
	  	pesosComparaPrec = '';
		
		var xmlDoc = $.parseXML( event.data );
		var $xml = $(xmlDoc);

		$xml.find('veiculoLista').find('veiculo').each(function()
		{
			
			var $veiculo 	= $(this);
			var idLocal 	= parseInt($veiculo.find('idLocal').text());	
			var idInterno 	= parseInt($veiculo.find('idInterno').text());				
			var pista 		= parseInt($veiculo.find('pista').text());
			var velocidade 	= parseInt($veiculo.find('velocidade').text());
			var pbt 		= parseInt($veiculo.find('pesagem').find('PBT').text());

			console.log("idLocal: " + idLocal + " idVeiculo: " + idInterno);
			
			populaTitulos(idLocal, pista);
					
			$veiculo.find('imagens').find('listaImagens').find('imagem').each(function() 
			{
				
				var tpImagem = $(this).find('tpImagem').text();
				
				console.log("Tipo de Imagem: " + tpImagem);
				
				if(tpImagem == 'pan')
				{
					var imagem = _base64ToArrayBuffer($(this).find('strImagemBuffer').text());			
					atualizaImagem(imagem, 'img_' + idLocal);	
				}		
				
				else if (
							tpImagem == 'pan2' && 
							parseInt(idLocal) == EQUIPAMENTO_PESAGEM_VIA_EXPRESSA &&
					   		(pista == PISTA_PESAGEM_EXPRESSA)
						)
				{
					var imagem = _base64ToArrayBuffer($(this).find('strImagemBuffer').text());			
					atualizaImagem(imagem, 'img_' + idLocal + '_PMV');	
				}					
									
			});		
			
			//Informações gerais
			if(
						parseInt(idLocal) == EQUIPAMENTO_PESAGEM_VIA_EXPRESSA 
											||
						parseInt(idLocal) == EQUIPAMENTO_PESAGEM_PRECISAO_FUGA
			  )
			{
				var idLocal 					= parseInt($veiculo.find('idLocal').text());
				var placa 						= $veiculo.find('placa').text();
				var velocidade  				= parseInt($veiculo.find('velocidade').text());
				var data 						= $veiculo.find('data').text();
				var classificacao 				= $veiculo.find('pesagem').find('classificacoesPesado').text();
				var pbt 						= parseInt($veiculo.find('pesagem').find('PBT').text());
				var excessoPBT					= parseInt($veiculo.find('pesagem').find('excessoPBTComTolerancia').text());
				var situacaoPMVSinaleiroSaida 	= $veiculo.find('situacao_PMV_Sinaleiro_Saida').text();	
				
				document.getElementById("placa_" 		+ idLocal).innerHTML = placa;
				document.getElementById("data_" 		+ idLocal).innerHTML = data;
				document.getElementById("velocidade_" 	+ idLocal).innerHTML = velocidade + " km/h";
				document.getElementById("qfv_" 			+ idLocal).innerHTML = classificacao;
				document.getElementById("pbt_" 			+ idLocal).innerHTML = pbt;
				document.getElementById("excessoPBT_" 	+ idLocal).innerHTML = excessoPBT + " Kg";
				document.getElementById("situacao_" 	+ idLocal).innerHTML = AjustaSituacaoSinalizacao(situacaoPMVSinaleiroSaida);
			
				/// 
				var strExcessosGrupos 	= '';
				var isExisteExcesso 	= false;
				var pesos 				= '';
				
				$veiculo.find('pesagem').find('listaGruposPesagem').find('grupoPesagem').each(function() 
				{
					var excesso = parseInt($(this).find('excesso').text());
					var peso 	= parseInt($(this).find('pesoGrupo').text());
					var grupo 	= $(this).find('descGrupo').text();
					
					
					pesos = pesos + excesso + "-" + peso + "-" + grupo + ";" ;
					
					if(excesso > 0)
					{
						strExcessosGrupos = strExcessosGrupos + grupo + '  ' + '[' + excesso + ']' + '  ';
						isExisteExcesso = true;
					}
				});
				
				document.getElementById("excessosGrupos_" 	+ idLocal).innerHTML = strExcessosGrupos;
				
				//alert("idLocal: " + idLocal + " Excessos grupos: " + strExcessosGrupos);
				
				if (classificacao == "")
				{
					document.getElementById("img_" + idLocal).style.border = "thick solid  #D8D8D8"; 
					document.getElementById("img_" + idLocal).style.border = "thick solid #D8D8D8"; 
				}
				else if( isExisteExcesso || excessoPBT > 0)
				{
					document.getElementById("img_" + idLocal).style.border = "thick solid  red"; 
					document.getElementById("img_" + idLocal).style.border = "thick solid red"; 
				}
				else
				{
					document.getElementById("img_" + idLocal).style.border = "thick solid  green"; 
					document.getElementById("img_" + idLocal).style.border = "thick solid  green"; 
				}

				
				///////
				//Se o PBT for zero, então limpa
				if ( pbt <= 0)
				{					
					document.getElementById("qfv_" 				+ idLocal).innerHTML = "-";
					document.getElementById("pbt_" 				+ idLocal).innerHTML = "-";
					document.getElementById("excessoPBT_" 		+ idLocal).innerHTML = "-";
					document.getElementById("excessosGrupos_" 	+ idLocal).innerHTML = "-";
				}
				
				if (excessoPBT > 0)
					document.getElementById("excessoPBT_" + idLocal).style.border = "thick solid red";
				
			}
			
			if ( parseInt(idLocal) == EQUIPAMENTO_PESAGEM_VIA_EXPRESSA )
			{
				if  ( classificacao.trim() === "" )  
					qfv_vazio_cinza_Expressa = "#D8D8D8";
				
				formataInfoGraficoPeso(pesos, 'canvas_peso_grupo_via_expressa', 'Pesagem na Estação Controle em Pista', classificacao);				
				pesosComparaExpr = pesos;
			}
			
			
			if(parseInt(idLocal) == EQUIPAMENTO_PESAGEM_PRECISAO_FUGA)
			{
				if  ( classificacao.trim() === "" )  
					qfv_vazio_cinza_Precisao = "#D8D8D8";
				
				formataInfoGraficoPeso(pesos, 'canvas_peso_grupo_precisao', 'Pesagem na Balança de Precisão', classificacao);
				pesosComparaPrec = pesos;
				formataGraficoComparativoPeso(pesosComparaExpr, pesosComparaPrec, 'canvas_peso_grupo_comparativo', 'Comparativo de Pesagem');
			}			
			
			
			
			
		});
	}	
	
	function atualizaImagem(imagem, obj) 
	{
		console.log("Atualizando imagens. obj: " + obj);
		
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
	

	
	function populaTitulos(idLocal, pista)
	{
	   	if(idLocal == EQUIPAMENTO_PESAGEM_VIA_EXPRESSA)
	   		document.getElementById("titulo_" + EQUIPAMENTO_PESAGEM_VIA_EXPRESSA).innerHTML = 'Estação Controle em Pista';
	   	
	   	else if(idLocal == EQUIPAMENTO_PESAGEM_PRECISAO_FUGA)
	   	{
	   		if(pista != PISTA_PESAGEM_PRECISAO)
	   			document.getElementById("titulo_" + EQUIPAMENTO_PESAGEM_PRECISAO_FUGA).innerHTML = 'Controle Fuga em Pista';

	   		else
	   			document.getElementById("titulo_" + EQUIPAMENTO_PESAGEM_PRECISAO_FUGA).innerHTML = 'Posto de Fiscalização - Balança Lenta';
	   		
	   	}	   		
	   	else
	   		document.getElementById("titulo_" + EQUIPAMENTO_BLOQUEIOVIARIO).innerHTML = 'Controle Fuga no Posto';
	   	
	} 
	
/////////////////////////////////////////////////////////////////////////////////////////////////////		/
/////////////////////////////////////////////////////////////////////////////////////////////////////		/
/////////////////////////////////////////////////////////////////////////////////////////////////////		/
	
	var chartPesosViaExpressa;
	var chartPesosPrecisao;
	var chartPesosComparativo;
	
	var chartBordaExpressa 	= '#ffb31a';
	var chartBordaPrecisao 	= '#b3b3ff';
	var chartCorAcimaPeso 	= '#ff6666';
	var chartCorAbaixoPeso 	= '#00e6ac';
		
/////////////////////////////////////////////////////////////////////////////////////////////////////		/
/////////////////////////////////////////////////////////////////////////////////////////////////////		/
/////////////////////////////////////////////////////////////////////////////////////////////////////		/

	function formataInfoGraficoPeso(pesoGrupos, id, titulo, qfv)
	{
		var grupo = pesoGrupos.split(";");
		
		var arrayGrupos			= [];
		var arrayPesos 			= [];		
		var arrayExcessos 		= [];
		var arrayCores			= [];
		var arrayBorda			= [];
		
		for (var i = 0; i < grupo.length-1; i++) 
		{
			var grupoDetail = grupo[i].split("-");

			arrayExcessos[i]	= grupoDetail[0];
			arrayPesos[i] 		= grupoDetail[1];
			arrayGrupos[i] 		= grupoDetail[2];
			
			if (id == 'canvas_peso_grupo_via_expressa')
				arrayBorda[i]	= chartBordaExpressa;
			else if (id == 'canvas_peso_grupo_precisao')
				arrayBorda[i]	= chartBordaPrecisao;
			
			if ( qfv.trim() === "" )
				arrayCores[i] = "#D8D8D8"; //CINZA
			
			else
			{
				if(parseInt(arrayExcessos[i]) > 0)
					arrayCores[i] = chartCorAcimaPeso;
				else
					arrayCores[i] = chartCorAbaixoPeso;
			}
			
			console.log(id + "::Grupo: " + arrayGrupos[i]);				
			console.log(id + "::Peso do Grupo: " + arrayPesos[i]);
			console.log(id + "::Excesso do Grupo: " + arrayExcessos[i]);
		}	
					
		 var canvas = document.getElementById(id);
		 var context = canvas.getContext('2d');
		
		if (typeof(arrayPesos[0]) =='undefined')
		{
			arrayEixos 		= null;
			arrayPesos 		= null;
			arrayExcessos 	= null;
			arrayCores		= null;
			
			return;
		}
		
		if (id == 'canvas_peso_grupo_via_expressa')
			chartPesos = chartPesosViaExpressa;
		else if (id == 'canvas_peso_grupo_precisao')
			chartPesos = chartPesosPrecisao;

		//Limpando o chart
		 if (chartPesos) {
		    	chartPesos.destroy();
		 }
		 
		//Imprimindo os dados no grafico
		var config = createConfigPesos(arrayPesos, arrayGrupos, arrayCores, arrayBorda, titulo);

	    chartPesos = new Chart(context, config);
	    
	    arrayGrupos 	= null;
		arrayPesos 		= null;
		arrayExcessos 	= null;
		arrayCores		= null;
	}
	
	function formataGraficoComparativoPeso(pesosComparaExpr, pesosComparaPrec, id, titulo)
	{
		
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////		
		var grupoExpr = pesosComparaExpr.split(";");
		
		var arrayGruposExpr			= [];
		var arrayPesosExpr 			= [];		
		var arrayExcessosExpr 		= [];
		var arrayCoresExpr			= [];	
		var arrayBordaExpr			= [];
		
		for (var i = 0; i < grupoExpr.length-1; i++) 
		{
			var grupoDetailExpr = grupoExpr[i].split("-");

			arrayExcessosExpr[i]	= grupoDetailExpr[0];
			arrayPesosExpr[i] 		= grupoDetailExpr[1];
			arrayGruposExpr[i] 		= grupoDetailExpr[2];
			arrayBordaExpr[i]		= chartBordaExpressa;
			
			if (qfv_vazio_cinza_Expressa.trim() != "" )
				arrayCoresExpr[i] = "#D8D8D8"; //CINZA
			else
			{
				if(parseInt(arrayExcessosExpr[i]) > 0)
					arrayCoresExpr[i] = chartCorAcimaPeso;
				else
					arrayCoresExpr[i] = chartCorAbaixoPeso;
			}
			
			console.log(id + "::GrupoExpr: " + arrayGruposExpr[i]);				
			console.log(id + "::Peso do GrupoExpr: " + arrayPesosExpr[i]);
			console.log(id + "::Excesso do GrupoExpr: " + arrayExcessosExpr[i]);
		}	
				
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		var grupoPrec = pesosComparaPrec.split(";");
		
		var arrayGruposPrec		= [];
		var arrayPesosPrec 		= [];		
		var arrayExcessosPrec 	= [];
		var arrayCoresPrec		= [];
		var arrayBordaPrec		= [];
		
		for (var i = 0; i < grupoPrec.length-1; i++) 
		{
			var grupoDetailPrec = grupoPrec[i].split("-");

			arrayExcessosPrec[i]	= grupoDetailPrec[0];
			arrayPesosPrec[i] 		= grupoDetailPrec[1];
			arrayGruposPrec[i] 		= grupoDetailPrec[2];
			arrayBordaPrec[i]		= chartBordaPrecisao;
			
			if (qfv_vazio_cinza_Precisao.trim() != "" )
				arrayCoresPrec[i] = "#D8D8D8"; //CINZA
			else
			{
				if(parseInt(arrayExcessosPrec[i]) > 0)
					arrayCoresPrec[i] = chartCorAcimaPeso;
				else
					arrayCoresPrec[i] = chartCorAbaixoPeso;
			}
			
			console.log(id + "::GrupoPrec: " 			+ arrayGruposPrec[i]);				
			console.log(id + "::Peso do GrupoPrec: " 	+ arrayPesosPrec[i]);
			console.log(id + "::Excesso do GrupoPrec: " + arrayExcessosPrec[i]);
		}	
		
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		
		
		var qtdeGrupos = getMaxLength(grupoExpr, grupoPrec);
		
		console.log ('qtdeGrupos::' + qtdeGrupos);
		
		
		var arrayGruposFinal		= [];
		var arrayPesosFinal 		= [];		
		var arrayExcessosFinal 		= [];
		var arrayCoresFinal			= [];
		var arrayCoresBorderFinal	= [];
		
		var j = 0;
		for (var i = 0; i < qtdeGrupos; i++)  
		{

			arrayExcessosFinal[j]		= arrayExcessosExpr[i];
			arrayPesosFinal[j] 			= arrayPesosExpr[i];
			arrayGruposFinal[j] 		= arrayGruposExpr[i];
			arrayCoresFinal[j] 			= arrayCoresExpr[i];
			arrayCoresBorderFinal[j]	= arrayBordaExpr[i];
			
			arrayGruposFinal[j] = arrayGruposFinal[j] + '-ECP';

			console.log(id + "::GrupoFinal: " 			+ arrayGruposFinal[j]);				
			console.log(id + "::Peso do GrupoFinal: " 	+ arrayPesosFinal[j]);
			console.log(id + "::Excesso do GrupoFinal: " + arrayExcessosFinal[j]);			
			
			j++;
			
			arrayExcessosFinal[j]		= arrayExcessosPrec[i];
			arrayPesosFinal[j] 			= arrayPesosPrec[i];
			arrayGruposFinal[j] 		= arrayGruposPrec[i];
			arrayCoresFinal[j] 			= arrayCoresPrec[i];
			arrayCoresBorderFinal[j]	= arrayBordaPrec[i];
			
			arrayGruposFinal[j] = arrayGruposFinal[j] + '-Precisão';

			console.log(id + "::GrupoFinal: " 			+ arrayGruposFinal[j]);				
			console.log(id + "::Peso do GrupoFinal: " 	+ arrayPesosFinal[j]);
			console.log(id + "::Excesso do GrupoFinal: " + arrayExcessosFinal[j]);			
			
			j++;		
			
			arrayExcessosFinal[j]		= '';
			arrayPesosFinal[j] 			= '';
			arrayGruposFinal[j] 		= '';
			arrayCoresFinal[j] 			= '';
			arrayCoresBorderFinal[j]	= '-';

			j++;				
			
		}
		
		var canvas = document.getElementById(id);
		var context = canvas.getContext('2d');
		
		if (typeof(arrayPesosFinal[0]) =='undefined')
		{
			arrayEixosFinal 		= null;
			arrayPesosFinal 		= null;
			arrayExcessosFinal 		= null;
			arrayCoresFinal			= null;
			
			return;
		}
		
		//Limpando o chart
		 if (chartPesosComparativo) {
			 chartPesosComparativo.destroy();
		 }
		 
		//Imprimindo os dados no grafico
		var config = createConfigPesos(arrayPesosFinal, arrayGruposFinal, arrayCoresFinal, arrayCoresBorderFinal, titulo);

		chartPesosComparativo = new Chart(context, config);
		
		
		arrayGruposExpr		= null;
		arrayPesosExpr 		= null;		
		arrayExcessosExpr 	= null;
		arrayCoresExpr		= null;
		arrayGruposPrec		= null;
		arrayPesosPrec 		= null;		
		arrayExcessosPrec 	= null;
		arrayCoresPrec		= null;
		arrayGruposFinal	= null;
		arrayPesosFinal 	= null;		
		arrayExcessosFinal 	= null;
		arrayCoresFinal		= null;
	}	
	
	
	function getMaxLength(gruposExpr, gruposPrec)
	{
		var qtdeEx = gruposExpr.length;
		var qtdePr = gruposPrec.length;
		
		var saida = 0;
		
		if ( qtdeEx < qtdePr )
			saida = qtdeEx;
		else
			saida = qtdePr;
		
		return saida - 1;
		
	}

	 	
	var color = Chart.helpers.color;

	function createConfigPesos(pesos, eixos, cores, coresBorda, titulo) 
	{
		return {
			type: 'bar',
			data: {
				labels: eixos, 
				datasets: [
					{
						label: "Pesagem",
						data: pesos,
						backgroundColor: cores,
						borderColor: coresBorda,
						borderWidth: 2                        
					}
					]
			},
			options: 
			{
				responsive: true,
				legend: {
							position: 'top',
							display: false,
							labels: {
								fontSize: 10,
								boxWidth: 10
						}
				},
				scales: 
				{
					xAxes: [{
						display: true,
						ticks: {
							fontSize: 11
						},
						scaleLabel: {
							display: true,
							fontSize: 11
						},
						categoryPercentage:2,
						barPercentage:2,
						barThickness : 30
					}],
					yAxes: [{
						display: true,
						ticks: {
							fontSize: 11,
							beginAtZero: true
						},
						scaleLabel: {
							display: true,
							fontSize: 11,
							labelString: 'Peso por Grupo'
						}
					}]
				}, 
				title: 
				{
					display: true,
					text: titulo,
					fontSize: 17
				}
			},
			plugins: 
			{	          
	            afterDatasetsDraw: function (context, easing) 
	            {
	              var ctx = context.chart.ctx;
	              context.data.datasets.forEach(function (dataset) 
	              {
	                for (var i = 0; i < dataset.data.length; i++) {
	                  if (dataset.data[i] != 0) {
	                    var model = dataset._meta[Object.keys(dataset._meta)[0]].data[i]._model;
	                    var textY = model.y + (dataset.type == "line" ? -3 : 15);

	                    ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize, 'normal', Chart.defaults.global.defaultFontFamily);
	                    ctx.textAlign = 'start';
	                    ctx.textBaseline = 'middle';
	                    ctx.fillStyle = dataset.type == "line" ? "black" : "black";
	                    ctx.save();
	                    ctx.translate(model.x-10, textY+25);	                    
	                    ctx.rotate(11.7);
	                    ctx.fillText(dataset.data[i], 0, 0);
	                    ctx.restore();
	                  }
	                }
	              });
	            }

	        }
		};
	}
