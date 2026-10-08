var markerImg_red_new = new google.maps.MarkerImage('assets/images/iconesTransparentes/radar_red_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30, 30));   	

var markerImg_green_new = new google.maps.MarkerImage('assets/images/iconesTransparentes/radar_green_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30, 30));   	

var markerImg_yellow_new = new google.maps.MarkerImage('assets/images/iconesTransparentes/radar_amarelo_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30, 30)); 

var markerImg_orange_new = new google.maps.MarkerImage('assets/images/iconesTransparentes/radar_orange_bola_transp.png',
        new google.maps.Size(45, 45), 
        new google.maps.Point(0, 0),
        new google.maps.Point(30, 30)); 


conjunto_linhas 	= []
conjunto_markers 	= []
var mapa_google 	= null;
  

$(document).ready(function() 
{	
	var myLatlng = LatLngPadrao;

	var myOptions = {
	  zoom: ZOOM_PADRAO,
	  center: myLatlng
	};
	// standard map
	mapa_google = new google.maps.Map(document.getElementById("map-canvas"), myOptions);
	
	atualiza_mapa();
	setInterval(atualiza_mapa, 60000);
});

function atualiza_mapa(){
	limpa_info_tabela();
	limpar_linhas_markers();
	obter_situacao_transito();
}	

function obter_situacao_transito()
{
	$.ajax(
    {
        type: 		"GET",
        url: 		"http://" + IP_API_MONITORAMENTO_TRECHO + "/obter-situacao-transito",
        dataType:	"json",
        success: function( data, a, b) {
			
			processa(data);        	
    		return false;

        },error: function(a, b, c){
        	console.log("(obter_situacao_transito) Erro ao processar requisição ao servidor)!!");
        }
    });    			
}

function processa(data) 
{
	try
	{	
		
		var obj = JSON.stringify(data);
		let trechos = JSON.parse(obj);	
		
		for (let i = 0; i < trechos.length; i++) 
		{
			trecho = trechos[i];
			id_trecho = trecho.id;
		 	nome_trecho = trecho.trecho;
			data = trecho.data;
			situacao = trecho.situacao_transito;
			velocidade_media = trecho.indicadores.vel_media_transito;
			coordenadas = trecho.coordenadas;
			
			console.log("id:: " + id_trecho + " --> " + nome_trecho);
								
			adiciona_info_tabela(trecho);
			
			desenha_retas(coordenadas);
			desenha_markers(trecho);			
		}	
	}
	catch(err) {
	  console.log('Erro processa():: ' + err.message);
	}	
}	

function desenha_retas(coords)
{
	try
	{	
		for (let i = 0; i < coords.length-1; i++) 
		{
			
			pos1 = coords[i].coordenada.split(',');
			pos2 = coords[i+1].coordenada.split(',');
			cor = seleciona_cor(trecho);
			var points0 = [new google.maps.LatLng(pos1[0].trim(), pos1[1].trim()), new google.maps.LatLng(pos2[0].trim(), pos2[1].trim())];		
			var polyline = new google.maps.Polyline({
													    map: mapa_google,
													    path: points0,
													    strokeColor: cor,
													    strokeWeight: 8,
													    strokeOpacity: 0.8
												});			
			conjunto_linhas.push(polyline);
		};
	}
	catch(err) {
	  console.log('Erro desenha_retas():: ' + err.message);
	}		
}

function desenha_markers(trecho)
{
	id_trecho = trecho.id;
 	nome_trecho = trecho.trecho;
	data = trecho.data;
	situacao = trecho.situacao_transito;
	velocidade_media = trecho.indicadores.vel_media_transito;
	velocidade_esperada = trecho.indicadores.vel_media_simples;
	coords = trecho.coordenadas;
	
	try
	{	
		for (let i = 0; i <= coords.length-1; i++) 
		{
			pos = coords[i].coordenada.split(',');
			
			if (i == 0 || i == (coords.length-1))	
			{		
		    	var marker = new google.maps.Marker({
											      		position:	 			new google.maps.LatLng(pos[0].trim(), pos[1].trim()),
											      		map: 					mapa_google,
											      		icon: 					seleciona_marker(trecho),
											      		infowindow: 			new google.maps.InfoWindow({ content: ''  }),
											      		id_trecho:				id_trecho,
											      		nome_trecho: 			nome_trecho,
														data: 					data,
														situacao: 				situacao,
														velocidade_esperada: 	velocidade_esperada,
														velocidade_media: 		velocidade_media
											    	});											
				conjunto_markers.push(marker);
		    	
		        google.maps.event.addListener(marker, 'click', function() 
		        {		        	
				 	var dados = 		'<div id="id_trecho_"' 	+ this.id_trecho + '>'+
										'<div><strong><h8 id="desc_trecho">' + this.nome_trecho + '</h8></strong></div>'+
										'</br>' +
										'<div id="bodyContent">'+
										'  	<div><h8  id="data_' 		+ this.id_trechoo + '"> Data: <strong>' + this.data + '</strong></h8></div>' +
										'   </br>' +
										'  	<div><h8  id="situacao_' 	+ this.situacao + '"> Situação: <strong>' + this.situacao + '</strong></h8></div>' +
										'   </br>' +
										'  	<div><h8  id="vel_esperada_' 	+ this.velocidade_esperada + '"> Vel. esp.: <strong>' + this.velocidade_esperada + '</strong></h8></div>' +										
										'  	<div><h8  id="vel_media_' 	+ this.velocidade_media + '"> Vel. Atual: <strong>' + this.velocidade_media + '</strong></h8></div>' + 										
										'</div>';    	        	
										
		
				 	this.infowindow.setContent(dados);	        	
		        	this.infowindow.open(mapa_google, this);
		         });
				
			}			
		}
	}
	catch(err) {
	  console.log('Erro desenha_markers():: ' + err.message);
	}		
}

function seleciona_marker(trecho)
{	
	data = trecho.data;
	situacao = trecho.situacao_transito;
	
	if (situacao == 'RAPIDO') 					return markerImg_green_new;
	else if (situacao == 'MODERADO') 			return markerImg_yellow_new;
	else if (situacao == 'LENTO') 				return markerImg_orange_new;	
	else if (situacao == 'CONGESTIONADO')		return markerImg_red_new;	
}

function seleciona_cor(trecho)
{	
	data = trecho.data;
	situacao = trecho.situacao_transito;
	
	if (situacao == 'RAPIDO') 				return "green";
	else if (situacao == 'MODERADO') 		return "yellow";
	else if (situacao == 'LENTO') 			return "orange";
	else if (situacao == 'CONGESTIONADO')	return "red";
	else 									return "black";
}

function limpar_linhas_markers()
{
	for (i=0; i<conjunto_linhas.length; i++)                           
	  conjunto_linhas[i].setMap(null); 
	
	for (i=0; i<conjunto_markers.length; i++) 
	  conjunto_markers[i].setMap(null); 
}

function adiciona_info_tabela(trecho) 
{
 	nome_trecho = trecho.trecho;
	data = trecho.data;
	situacao = trecho.situacao_transito;
	velocidade_media = trecho.indicadores.vel_media_transito;
	velocidade_esperada = trecho.indicadores.vel_media_simples;
   
	var tableRef = document.getElementById('tabela_trecho').getElementsByTagName('tbody')[0];
	var row = tableRef.insertRow(tableRef.rows.length);
	row.insertCell(0).innerText = nome_trecho;
    row.insertCell(1).innerText = velocidade_esperada;
	row.insertCell(2).innerText = velocidade_media;
	row.insertCell(3).innerText = situacao;
	
	if (situacao == 'RAPIDO') 				row.className = "table-success";
	else if (situacao == 'MODERADO') 		row.className = "table-warning";
	else if (situacao == 'LENTO') 			row.className = "orange_line";
	else if (situacao == 'CONGESTIONADO')	row.className = "table-danger";
}

function limpa_info_tabela()
{	
	var myTable = document.getElementById("tabela_trecho");
	var rowCount = myTable.rows.length;
	for (var x=rowCount-1; x>0; x--) {
	   myTable.deleteRow(x);
	}
}
