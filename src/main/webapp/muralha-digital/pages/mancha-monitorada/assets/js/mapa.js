let rota = [];
let center = {lat: -25.432947, lng: -49.270591 }; //Local de inicialização do mapa (Curitiba)
let map;
let pathCoordinates = [];
let markers = [];
let markersEquipamentos = [];
let rotaPath;
let polygon;
let equipamentos = [];
let polygons = [];
let markersEquipamentosPorPoligono = [];
let debounceTimeout = null;
let poligonoInvalido = false;
let infoWindow = new google.maps.InfoWindow();

$(document).ready(function () 
{	
	obterEquipamentos();
	montarListaAreas();

	//Listener usado para filtrar a área monitorada quando o usuário digitar.
	document.getElementById("nome-mancha").addEventListener("input", function () {
		clearTimeout(debounceTimeout);
    	debounceTimeout = setTimeout(() => {
        	montarListaAreas();
    	}, 500);
	});
	
	//Listener para atualizar a cor do polígono em tempo real.
	document.getElementById("corPoligono").addEventListener("input", function () {
    const novaCor = this.value;

	    // Atualiza o polígono se ele já existir
	    if (polygon) {
	        polygon.setOptions({ fillColor: novaCor });
	    }
	});
	
	//CONFIGURAÇÕES DO MAPA ABAIXO
	
	// map center
	var myLatlng = new google.maps.LatLng(-25.432947, -49.270591);
	// map options,
	var myOptions = {
	  zoom: 11,
	  center: myLatlng
	};
	// standard map
	map = new google.maps.Map(document.getElementById("map-canvas"), myOptions);
	
});

function initMap() {
  map = new google.maps.Map(document.getElementById('map-canvas'), {
    zoom: 11,
    center: center // 
  });
 
  	rotaPath = new google.maps.Polyline({
    path: pathCoordinates,
    geodesic: true,
    strokeColor: '#000000',
    strokeOpacity: 1.0,
    strokeWeight: 2
  });
  
  rotaPath.setMap(map)

	google.maps.event.addListener(map, 'click', function (event) {
	  const latlng = event.latLng;
	  const latitude = latlng.lat();
	  const longitude = latlng.lng();
	  const novoPonto = { lat: latitude, lng: longitude };
	
	  // recria rotaPath se estiver null (após limpar)
	  if (!rotaPath) {
	    rotaPath = new google.maps.Polyline({
	      path: pathCoordinates,
	      geodesic: true,
	      strokeColor: '#000000',
	      strokeOpacity: 1.0,
	      strokeWeight: 2
	    });
	    rotaPath.setMap(map);
	  }
	
	  // verifica fechamento automático da região
	  const primeiroPonto = pathCoordinates[0];
	  if (primeiroPonto && pathCoordinates.length >= 3) {
	    const distancia = calcularDistancia(novoPonto, primeiroPonto); 
	    if (distancia < 500) {
	      pathCoordinates.push(primeiroPonto);
	      rotaPath.setPath(pathCoordinates);
	      fecharRegiao();
	      return;
	    }
	  }
	
	  if (!polygon) {
	    const marker = new google.maps.Marker({
	      position: latlng,
	      map: map,
	      title: "Coordenadas: " + latitude + ", " + longitude,
	      draggable: true
	    });
	
	    markers.push(marker);
	    pathCoordinates.push(novoPonto);
	    rotaPath.setPath(pathCoordinates);
	
	    google.maps.event.addListener(marker, 'dragend', function(event) {
	      const newLat = event.latLng.lat();
	      const newLng = event.latLng.lng();
	
	      const index = markers.indexOf(marker);
	      if (index > -1) {
	        pathCoordinates[index] = { lat: newLat, lng: newLng };
	      }
	
	      if (polygon) {
		
			verificarAreaPoligono();		 		
			
			polygon.setPaths(pathCoordinates);
			inserirEquipamentos(polygon);					
	      }
	    });
	  }
	}); 
	
	//Atualiza a rota após toda a conclusão do método.
	rotaPath.setPath(pathCoordinates);
}

//Carrega o mapa ao carregar a tela
window.onload = initMap;

function atualizarRegiaoMancha() {
  if (polygon && polygon.getMap()) {
    // Se o polígono estiver no mapa, não exibe a rota (polyline)
    rotaPath.setMap(null);
  } else {
    // Caso contrário, mostra a linha conectando os pontos
    rotaPath.setPath(pathCoordinates);
    rotaPath.setMap(map);
  }
}

//Fecha o polígono
function fecharRegiao() {
		
  if(polygon != null){
	  //Remove o polígono antes de criar. Garante que não haverá um polígono sobrepondo outro ou que haverá mais de um polígono simultaneamente
  	  polygon.setMap(null);	
  }
	
  // Remove linha anterior (opcional)
  rotaPath.setMap(null);
  
  const corSelecionada = document.getElementById("corPoligono").value;

  // Fecha a região (gera o polígono)
  polygon = new google.maps.Polygon({
    paths: pathCoordinates,
    strokeColor: "#000000",
    strokeOpacity: 0.8,
    strokeWeight: 2,
    fillColor: corSelecionada,
    fillOpacity: 0.35
  });

  polygon.setMap(map);
  
  //Verificar se o novo polígono está dentro de outro polígono
  verificarAreaPoligono();
  
  //Atualiza/insere os equipamentos no polígono
  inserirEquipamentos(polygon); 
}

//Atualiza/insere os equipamentos no polígono
function inserirEquipamentos(polygon, nomeMancha) {
	
	markersEquipamentos.forEach(m => m.setMap(null));
	markersEquipamentos = [];
	
	equipamentos.forEach(eq => {
	  if (!isNaN(eq.lat) && !isNaN(eq.lng)) {
		
	    const position = new google.maps.LatLng(eq.lat, eq.lng);	
	    const dentroPoligono = google.maps.geometry.poly.containsLocation(position, polygon);
	
	if (dentroPoligono) {		
		const marker = new google.maps.Marker({
			position: position,
			map: map,
			title: "Endereço: " + eq.nome,
			draggable: false,
			icon:{
				url: 'assets/images/radar_green_bola_transp.png',
				scaledSize: new google.maps.Size(32, 32),
				//origin: new google.maps.Point(0, 0),      
    			//anchor: new google.maps.Point(16, 32) 
				}
			});	
			
		markersEquipamentos.push(marker);
		
			marker.addListener("click", () => {
				infoWindow.setContent(`
				  <div style="
				    white-space: nowrap;
				    width: auto;
				    max-width: none;
				    padding: 4px 8px;
				    font-size: 13px;
				    height: auto;
				  ">
				  	<strong>Área Monitorada:</strong> ${nomeMancha}<br>
				    <strong>Endereço:</strong> ${eq.nome}<br>
				    <strong>Sequência:</strong> ${eq.sequencia}<br>
				    <strong>Latitude:</strong> ${eq.lat}<br>
				    <strong>Longitude:</strong> ${eq.lng}
				  </div>
				`);
	        	infoWindow.open(map, marker);
      		});		
		}
	  }
	});
}

//Realiza a "limpeza" de tudo relacionado à tela do mapa.
function limparMapa() {
	
  mostrarLoading();

  //"Limpa" os markers do mapa.
  markers.forEach(m => m.setMap(null));
  
  //"Limpa" os equipamentos dentro de um polígono individual.
  markersEquipamentos.forEach(m => m.setMap(null));
  
  //Reseta a variável markers.
  markers = [];
  
  //Reseta a variável que contém os equipamentos de todos os polígonos criados no mapa.
  markersEquipamentosPorPoligono = [];
  
  //"Limpa" as polylines do polígono ou dos markers.
  pathCoordinates = [];
  
  //"Limpa" o campo do nome da mancha monitorada.
  document.getElementById("nomeManchaMonitorada").value = "";
  
  //Reseta o botão "Exibir" que expõe um marker específico no mapa.
  document.querySelectorAll('#lista-manchas li').forEach(li => {
		li.classList.remove('selecionado');
		li.querySelector("button").classList.remove('btn-success');
		li.querySelector("button").classList.add('btn-primary');
	})
	
	//"Limpa" todosos polígonos no mapa.
	removerPoligono();

  //"Limpa" a rota dos markers/polígono.
  if (rotaPath) {
    rotaPath.setMap(null);
    rotaPath = null; // ESSENCIAL
  }

  //"Limpa" um polígono individual do mapa.
  if (polygon) {
    polygon.setMap(null);
    polygon = null;
  }
  
  //Reseta o mapa para o início.
  map.setCenter(center);
  map.setZoom(11);1
  
  esconderLoading();
}

async function cadastrarManchaMonitorada() {
	
	if(poligonoInvalido){
		alert("Atenção: o polígono desenhado sobrepõe uma área já cadastrada. O cadastro será impedido.");
	}
	
    const nomeManchaMonitorada = document.getElementById("nomeManchaMonitorada").value.toUpperCase();
    const corSelecionada = document.getElementById("corPoligono").value;

    if (!nomeManchaMonitorada || !polygon) {
        alert("Informe um nome válido e uma região parametrizável fechada.");
        return;
    }

    rotaPath.setPath(pathCoordinates);
    const idsEquipamentos = obterIdsEquipamentosNoPoligono();	
    const coordenadasAtuais = obterCoordenadasDoPoligono();

    let payload = {
        dadosMancha: {
            cor: corSelecionada,
            coordenadas: coordenadasAtuais,
        },
        equipamentos: idsEquipamentos
    };

    mostrarLoading();

    $.ajax({
        url: `/MuralhaDigital/AreaMonitorada?acao=cadastrar&nome=${nomeManchaMonitorada}`,
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify(payload),
        success: function(res) {
            alert("Mancha cadastrada com sucesso!");
            montarListaAreas();
        },
        error: function(err) {
            console.error("Erro ao cadastrar mancha:", err);
            alert("Erro ao cadastrar mancha.");
        },
        complete: function () {
            esconderLoading(); 
        }
    });
}

//Calcula a distância entre o marker do click e o marker inicial
function calcularDistancia(p1, p2) {
  const R = 6371e3; // raio da Terra em metros
  const toRad = deg => deg * Math.PI / 180;

  const lat1 = toRad(p1.lat);
  const lat2 = toRad(p2.lat);
  const deltaLat = toRad(p2.lat - p1.lat);
  const deltaLng = toRad(p2.lng - p1.lng);

  const a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
            Math.cos(lat1) * Math.cos(lat2) *
            Math.sin(deltaLng/2) * Math.sin(deltaLng/2);

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

  return R * c; // em metros
}

//Traz todos os equipamentos que tenham lat/long
function obterEquipamentos() {	
    const url = `/MuralhaDigital/AreaMonitorada?acao=obterEquipamentos`;    

    fetch(url)
    .then(response => response.text())
    .then(xmlString => {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlString, "application/xml");

        const parseError = xmlDoc.querySelector("parsererror");
        if (parseError) {
            console.error("Erro ao interpretar XML:", parseError.textContent);
            alert("Erro ao interpretar a resposta do servidor.");
            return;
        }

        const equipamentosXml = xmlDoc.getElementsByTagName("equipamento");

        for (let i = 0; i < equipamentosXml.length; i++) {
            const eq = equipamentosXml[i];

            const idLocal       = parseInt(eq.getElementsByTagName("id_local")[0]?.textContent);
            const sequencia     = parseInt(eq.getElementsByTagName("sequencia_local")[0]?.textContent);
            const nome          = eq.getElementsByTagName("nome")[0]?.textContent;
            const idLocalidade  = parseInt(eq.getElementsByTagName("id_localidade")[0]?.textContent);
            const lat           = parseFloat(eq.getElementsByTagName("posicao_lat")[0]?.textContent);
            const lng           = parseFloat(eq.getElementsByTagName("posicao_lon")[0]?.textContent);

            equipamentos.push({
                idLocal,
                sequencia,
                nome,
                idLocalidade,
                lat,
                lng
            });
        }
    })
    .catch(error => {
        console.error("Erro ao obter equipamentos:", error);
        alert("Ocorreu um erro ao obter os equipamentos.");
    });
}

function obterIdsEquipamentosNoPoligono() {
  const ids = [];

  // Garante que está usando as coordenadas reais do polígono
  const coordenadasAtuais = obterCoordenadasDoPoligono();
  polygon.setPaths(coordenadasAtuais); // Não relevante mas útil para visualização do polígono atual

  equipamentos.forEach(eq => {
    if (!isNaN(eq.lat) && !isNaN(eq.lng)) {
      const position = new google.maps.LatLng(eq.lat, eq.lng);
      if (google.maps.geometry.poly.containsLocation(position, polygon)) {
        ids.push(eq.idLocal);
      }
    }
  });

  return ids;
}

function obterCoordenadasDoPoligono() {
  return polygon.getPath().getArray().map(p => ({
    lat: p.lat(),
    lng: p.lng()
  }));
}


//Busca todas as áres que não estejam deletadas e monsta a listagem
function montarListaAreas() {	
	
    const nome = document.getElementById("nome-mancha").value.toUpperCase();  
        
    const url = `/MuralhaDigital/AreaMonitorada?acao=buscarAreas&nome=${nome}`;       

    fetch(url)
    .then(response => response.text())
    .then(xmlString => {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlString, "application/xml");   
        
        const parseError = xmlDoc.querySelector("parsererror");
      		if (parseError) {
	            console.error("Erro ao interpretar XML:", parseError.textContent);
	            alert("Erro ao interpretar a resposta do servidor.");
	            return;
	        }
        
        //Componente usado na listagem
        const listaUl = document.getElementById("lista-manchas");
      	listaUl.innerHTML = "";  
      	const areas = xmlDoc.getElementsByTagName("areasMonitoradas");   
      	
      	dadosTodasManchas = areas;
      	
      	//Listagem das áreas
      	for (let i = 0; i < areas.length; i++) {
			const area = areas[i];
		
		        const id = area.getElementsByTagName("id")[0].textContent;
		        const nome = area.getElementsByTagName("nome")[0].textContent;
		        const data = area.getElementsByTagName("data_cadastro")[0].textContent;
		        const dados_json = area.getElementsByTagName("dados_json")[0].textContent;
		        
		        const li = document.createElement("li");
		        li.style = "border-bottom: solid; padding: 4px; display: flex;"
		        //li.className = "d-flex;";
		        		
		        const div = document.createElement("div");
		        div.className = "ms-2 me-auto";	
		        div.style = "flex-direction: column; min-width: 265px;"	        
		        div.innerHTML = `<div class="fw-bold">${nome}</div><small>${new Date(data).toLocaleString()}</small>`;
		        
		        //Botão "Excluir"
		        const btnExcluir = document.createElement("button");
		        btnExcluir.className = "btn btn-sm btn-danger";
		        btnExcluir.style = "min-width: 76px;";
		        btnExcluir.innerText = "Excluir";
		        btnExcluir.setAttribute("data-id", id);		        
		        btnExcluir.onclick = function () {
			    const idMancha = this.getAttribute("data-id");		
				    if (confirm("Tem certeza que deseja excluir esta área monitorada?")) {
				        excluirAreaMonitorada(idMancha);
				    }
				};
		        
		        const botoes = document.createElement("div");
				//botoes.className = "display: flex !important;";
				botoes.style = "display: flex !important; flex-wrap: wrap; justify-content: flex-end; min-width: 72px; gap: 6px;";
		
				//Botão "Exibir"
		        const btn = document.createElement("button");
		        btn.className = "btn btn-sm btn-primary";
		        btn.style = "min-width: 76px;";
		        btn.innerText = "Exibir";
		        
		        //Verifica se o botão já está selecionado. Se estiver, limpa o mapa e a reseta a classe do botão
		        btn.onclick = () => {			
					const jaSelecionado = li.classList.contains('selecionado');					
					if (jaSelecionado) {
						li.classList.remove('selecionado');
						btn.classList.remove('btn-success');
						btn.classList.add('btn-primary');

						limparMapa(); 
						return;
				  	}			
				  document.querySelectorAll('#lista-manchas li').forEach(li => {															
				    li.classList.remove('selecionado');
				    li.querySelector("button").classList.remove('btn-success');
				    li.querySelector("button").classList.add('btn-primary');				    
				  });
				
				  li.classList.add('selecionado');
				  btn.classList.remove('btn-primary');
				  btn.classList.add('btn-success');
				
				  desenharPoligonoDaMancha(dados_json, nome);
				};
		
		        botoes.appendChild(btn);
				botoes.appendChild(btnExcluir);
				
				li.appendChild(div);
				li.appendChild(botoes);
				listaUl.appendChild(li);		        
		      }
		    })
		    .catch(err => {
		      console.error("Erro ao carregar áreas monitoradas:", err);
		    });			     
}

//Cria o polígono no mapa quando o usuário clicar no botão "Exibir"
async function desenharPoligonoDaMancha(dadosJsonStr, nomeMancha) {
  try {		
		
	 //"Limpa" os markers do mapa antes de criar o outro polígono.
 	 markers.forEach(m => m.setMap(null));  
	
    const dados = JSON.parse(dadosJsonStr);
       
    const cor = dados.dadosMancha.cor;
    const coordenadas = dados.dadosMancha.coordenadas;

    if (polygon) polygon.setMap(null); // remove anterior

    const novaRegiao = new google.maps.Polygon({
      paths: coordenadas,
      strokeColor: "#000",
      strokeOpacity: 0.8,
      strokeWeight: 2,
      fillColor: cor,
      fillOpacity: 0.35,
      zoom: 11
    });
    
    novaRegiao.setMap(map);
    polygon = novaRegiao;
    
    //Insere os equipamentos dentro do polígono
    inserirEquipamentosNoPoligono(polygon, nomeMancha);

    // Ajusta o mapa para caber no polígono
    const bounds = new google.maps.LatLngBounds();
    coordenadas.forEach(coord => bounds.extend(coord));
    map.fitBounds(bounds);

  } catch (e) {
    console.error("Erro ao desenhar polígono:", e);
  }
}

async function verificarManchaExiste() {
    const nomeManchaMonitorada = document.getElementById("nomeManchaMonitorada").value.toUpperCase(); 
    const url = `/MuralhaDigital/AreaMonitorada?acao=buscarPorNome&nome=${nomeManchaMonitorada}`;

    mostrarLoading(); // Início do loading

    fetch(url)
        .then(response => response.text())
        .then(xmlString => {
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlString, "application/xml");   
            const area = xmlDoc.querySelector("AreaMonitorada");
            const id = area?.querySelector("id")?.textContent;

            if (id > 0) {
                alert("Já existe uma área monitorada cadastrada com esse nome.");
                return;
            }

            cadastrarManchaMonitorada();
            limparMapa();
        })
        .catch(err => {
            console.error("Erro ao verificar mancha:", err);
            alert("Erro ao verificar mancha.");
        })
        .finally(() => {
            esconderLoading();
        });
}

//Busca todos os polígonos, desenha todos no mapa e busca por equipamentos para inserir nos polígonos
function desenharTodasManchas(){   
    const url = `/MuralhaDigital/AreaMonitorada?acao=buscarAreas&nome=`;      
    
    mostrarLoading(); 

    fetch(url)
    .then(response => response.text())
    .then(xmlString => {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlString, "application/xml");   
        
        const parseError = xmlDoc.querySelector("parsererror");
      		if (parseError) {
	            console.error("Erro ao interpretar XML:", parseError.textContent);
	            alert("Erro ao interpretar a resposta do servidor.");
	            return;
	        }
        
      	const areas = xmlDoc.getElementsByTagName("areasMonitoradas");         	
      	
      	for (let i = 0; i < areas.length; i++) {
			const area = areas[i];
			
			const jsonStr = area.getElementsByTagName("dados_json")[0].textContent;		
			const dados = JSON.parse(jsonStr);
  			const cor = dados.dadosMancha.cor;
  			const coordenadas = dados.dadosMancha.coordenadas;  
  			const nomeArea = area.getElementsByTagName("nome")[0].textContent
  			
  			const poly = new google.maps.Polygon({
			    paths: coordenadas,
			    strokeColor: "#000000",
			    strokeOpacity: 0.8,
			    strokeWeight: 2,
			    fillColor: cor,
			    fillOpacity: 0.35,
			    map: map
			  });    	
			  
			  polygons.push(poly);	
			  
			  // Ajusta o mapa para caber no polígono
			  const bounds = new google.maps.LatLngBounds();
			  coordenadas.forEach(coord => bounds.extend(coord));
			  
			  //Inere os equipamentos nos polígonos
			  const markers = inserirEquipamentosNoPoligono(poly, nomeArea);
			  markersEquipamentosPorPoligono.push(markers);
		}		
	})
	
	esconderLoading();
}

//Função que verifica se há equipamentos para inserir nos polígonos
function inserirEquipamentosNoPoligono(polygon, nomeMancha) {

  equipamentos.forEach(eq => {
    if (!isNaN(eq.lat) && !isNaN(eq.lng)) {
      const position = new google.maps.LatLng(eq.lat, eq.lng);
      if (google.maps.geometry.poly.containsLocation(position, polygon)) {
        const marker = new google.maps.Marker({
          position: position,
          map: map,
          title: eq.nome,
          icon: {
            url: 'assets/images/radar_green_bola_transp.png',
            scaledSize: new google.maps.Size(32, 32)
          }
        });
        markers.push(marker);

        marker.addListener("click", () => {
          infoWindow.setContent(`
            <div>
              <strong>Área monitorada:</strong> ${nomeMancha}<br>
              <strong>Endereço:</strong> ${eq.nome}<br>
              <strong>Sequência:</strong> ${eq.sequencia}<br>
              <strong>Latitude:</strong> ${eq.lat}<br>
              <strong>Longitude:</strong> ${eq.lng}
            </div>
          `);
          infoWindow.open(map, marker);
        });
      }
    }
  });

  return markers;
}

//Remove a lista de polígonos e seus equipamentos
function removerPoligono() {
  // Remove todos os polígonos do mapa
  polygons.forEach(p => p.setMap(null));
  polygons = [];

  // Remove os markers de equipamentos individuais (polígono atual)
  markersEquipamentos.forEach(m => m.setMap(null));
  markersEquipamentos = [];

  // Remove os markers de equipamentos por polígono (todos)
  markersEquipamentosPorPoligono.forEach(markerArray => {
    markerArray.forEach(marker => marker.setMap(null));
  });
  markersEquipamentosPorPoligono = [];
}

function excluirAreaMonitorada(id){
	
	const url = `/MuralhaDigital/AreaMonitorada?acao=excluir&id=${id}`;       

	mostrarLoading();

	$.ajax({
        url: url,
        method: "POST",
        contentType: "application/json",
        success: function(res) {    
			montarListaAreas();        
			alert("Área monitorada excluída com sucesso!");			  
        },
        error: function(err) {
            console.error("Erro ao excluir mancha:", err);
            alert("Erro ao excluir mancha.");
        },
        complete: function () {
            esconderLoading(); 
        }
    });
}

function verificarAreaPoligono() {
    poligonoInvalido = false;

    const url = `/MuralhaDigital/AreaMonitorada?acao=buscarAreas&nome=`;

    fetch(url)
        .then(response => response.text())
        .then(xmlString => {
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlString, "application/xml");

            const parseError = xmlDoc.querySelector("parsererror");
            if (parseError) {
                console.error("Erro ao interpretar XML:", parseError.textContent);
                alert("Erro ao interpretar a resposta do servidor.");
                return;
            }

            const areas = xmlDoc.getElementsByTagName("areasMonitoradas");
            const novoPath = polygon.getPath();
            const novoPontos = [];

            for (let i = 0; i < novoPath.getLength(); i++) {
                novoPontos.push(novoPath.getAt(i));
            }

            for (let i = 0; i < areas.length; i++) {
                const area = areas[i];
                const jsonStr = area.getElementsByTagName("dados_json")[0].textContent;
                const dados = JSON.parse(jsonStr);
                const coordenadas = dados.dadosMancha.coordenadas;

                const poligonoExistente = new google.maps.Polygon({
                    paths: coordenadas
                });

                // Verifica se qualquer ponto do novo polígono está dentro do existente
                const novoDentroDeExistente = novoPontos.some(ponto =>
                    google.maps.geometry.poly.containsLocation(ponto, poligonoExistente)
                );

                // Verifica se qualquer ponto do existente está dentro do novo polígono
                const existenteDentroDeNovo = coordenadas.some(coord =>
                    google.maps.geometry.poly.containsLocation(
                        new google.maps.LatLng(coord.lat, coord.lng),
                        polygon
                    )
                );

                if (novoDentroDeExistente || existenteDentroDeNovo) {
                    poligonoInvalido = true;
                    alert("Atenção: o polígono desenhado sobrepõe uma área já cadastrada. O cadastro será impedido.");
                    break;
                }
            }

            console.log("Resultado da validação:", poligonoInvalido);
        })
        .catch(error => {
            console.error("Erro ao buscar áreas monitoradas:", error);
        });
}

function mostrarLoading() {
  $("#loading-overlay").fadeIn(200);
}

function esconderLoading() {
  $("#loading-overlay").fadeOut(200);
}