$(document).on({
    ajaxStart: function(){
        $("body").addClass("loading"); 
    }    
});

var atendimento_lista 	= [];
var guarnicao_lista = [];
var registro_fato = [];
var registro_fato_filtrado = [];
var dados_equipamento;
var markersArray 	= [];
var rotas = [];
var marcadores = [];
var selectTipos = [];
var allMarkers = [];
var tiposRegistroFato = [];
var mapa_google 	= null;
let primeiraCarga = true;
const estado = {};
let heatmap = null;
transitLayer = new google.maps.TransitLayer(); 
bicycleLayer = new google.maps.BicyclingLayer();
traficLayer = new google.maps.TrafficLayer();
const iconesDisponiveis = [
    'assets/images/icones-atendimento/guarnicao_1.png',
    'assets/images/icones-atendimento/guarnicao_2.png',
    'assets/images/icones-atendimento/guarnicao_3.png'
];
const guarnicaoIconesMap = new Map();

function MapaTerreno() 		{ mapa_google.setMapTypeId('terrain'); 													}
function MapaSatelite() 	{ mapa_google.setMapTypeId('satellite'); 												}
function MapaRoadMap() 		{ mapa_google.setMapTypeId('roadmap'); 													}
function MapaHibrido() 		{ mapa_google.setMapTypeId('hybrid '); 													}
function MapaTransito() 	{ traficLayer.setMap(null); transitLayer.setMap(mapa_google);		}
function MapaBicicleta() 	{ bicycleLayer.setMap(mapa_google);									}
function MapaTrafego() 		{ transitLayer.setMap(null); traficLayer.setMap(mapa_google);		}

function atualizaMapa(){
	let tipo = $("#selectTypeMap").val()
	
	switch (tipo) {
		case "noMarking":
			return traficLayer?.setMap(null), transitLayer?.setMap(null)
		case "roadNetwork":
			return MapaTransito()
		case "busStation":
			return MapaTrafego()
	}
}

$(document).ready(function() {
    mapa_google = new google.maps.Map(document.getElementById("map-canvas"), {
        zoom: ZOOM_PADRAO,
        center: LatLngPadrao,
        tilt: 45,
        mapTypeId: 'roadmap'
    });

    MapaRoadMap();

    google.maps.event.addListenerOnce(mapa_google, "idle", function() {
        ObterDadosAtendimentos();
        carregarTiposRegistroFato();
        obterDispositivosEquipamentosMisto();
        ObterDadosGuarnicao();
        obterAtendimentos();

    });
});

document.getElementById("heatmap").oninput = function() {
    var value = (this.value-this.min)/(this.max-this.min)*100
    this.style.background = 'linear-gradient(to right, #0d75bf 0%, #0d75bf ' + value + '%, #fff ' + value + '%, white 100%)'
};

function inputRangeOpacity() {
    const sliderOpacity = document.getElementById("heatmap");
    const valueOpacity = parseInt(sliderOpacity.value);

    heatmap.set('opacity', valueOpacity / 100);
}

$(document).ready(function () {
    document.getElementById("heatmap").addEventListener("input", function () {
        inputRangeOpacity(this.value);
    });
});

function ativarMapaCalor(ativo) {
    let tipo = $("#selectType").val();

    let pontosDeCalor = [];

    function extrairLatLng(lista, nomeLista) {
        return lista.map(item => {
            if (item.lat && item.lng) {
                return new google.maps.LatLng(item.lat, item.lng);
            } else if (item.latitude && item.longitude) {
                return new google.maps.LatLng(item.latitude, item.longitude);
            } else {
                return null;
            }
        }).filter(p => p !== null);
    }

    if (tipo === "treatment") {
        pontosDeCalor = extrairLatLng(atendimento_lista, "atendimento_lista");
    } else if (tipo === "garrison") {
        pontosDeCalor = extrairLatLng(guarnicao_lista, "guarnicao_lista");
    } else if (tipo === "fact") {
        pontosDeCalor = extrairLatLng(registro_fato, "registro_fato");
    } else if (tipo === "pcl") {
        pontosDeCalor = extrairLatLng(dados_equipamento, "dados_equipamento");
    } else if (tipo === "all") {
        pontosDeCalor = [
            ...extrairLatLng(atendimento_lista, "atendimento_lista"),
            ...extrairLatLng(guarnicao_lista, "guarnicao_lista"),
            ...extrairLatLng(registro_fato, "registro_fato"),
            ...extrairLatLng(dados_equipamento, "dados_equipamento")
        ];
    }

    if (!heatmap) {
        heatmap = new google.maps.visualization.HeatmapLayer({
            data: pontosDeCalor,
            radius: 30,
            opacity: 0.9,
            dissipating: true,
            maxIntensity: 5,
            gradient: null
        });
    } else {
        heatmap.setData(pontosDeCalor);
    }

    if (ativo) {
        PopulaMarkers([]);
        removerMarkers(); 
        heatmap.setMap(mapa_google);
        $("#map-wrapper").show();
        $("#btn-atendimento").prop("disabled", true);
        $("#btn-guarnicao").prop("disabled", true);
        $("#btn-registro-fato").prop("disabled", true);
    } else {
        heatmap.setMap(null);
        exibeMarkers();
        $("#map-wrapper").hide();
        $("#btn-atendimento").prop("disabled", false);
        $("#btn-guarnicao").prop("disabled", false);
        $("#btn-registro-fato").prop("disabled", false);
    }
}

function parsePCLXml(xmlString) {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlString, "application/xml");
    const dispositivos = xmlDoc.getElementsByTagName("Dispositivo");

    const resultado = [];

    for (let i = 0; i < dispositivos.length; i++) {
        const dispositivo = dispositivos[i];
        const lat = dispositivo.getElementsByTagName("latitude")[0]?.textContent;
        const lng = dispositivo.getElementsByTagName("longitude")[0]?.textContent;

        if (lat && lng) {
            resultado.push({
                latitude: parseFloat(lat),
                longitude: parseFloat(lng)
            });
        }
    }

    return resultado;
}


function switchRotas(checked) {
  if (checked) {
    ProcessaRotas(registro_fato_filtrado);
  } else {
    rotas.forEach(r => r.setMap(null));
    rotas = [];

    marcadores.forEach(m => m.setMap(null));
    marcadores = [];

    registro_fato_filtrado.forEach(function (r) {
      const lat = parseFloat(r.latitude);
      const lng = parseFloat(r.longitude);

      if (!isFinite(lat) || !isFinite(lng)) return;

      const pos = { lat, lng };

      const eventoTipo = `${parseInt(r.idEvento)}-${parseInt(r.idTipo)}`;
      let iconeUrl;

      switch (eventoTipo) {
    	case "1-17":
        case "1-21":
          iconeUrl = "assets/images/icones-atendimento/veiculo_monitorado.png";
          break;
        
        case "1-13":
          iconeUrl = "assets/images/icones-atendimento/acidente_transito.png";
          break;
	
        case "1-2":
        case "1-4":
          iconeUrl = "assets/images/icones-atendimento/veiculo_sinistrado.png";
          break;
        
        case "1-15":
        case "1-16":
        case "1-18":
        case "1-19":
        case "1-20":
        case "1-22":
        case "1-23":
          iconeUrl = "assets/images/icones-atendimento/veiculo_sinistrado.png";
          break;
          
      	case "3-2":
          iconeUrl = "assets/images/icones-atendimento/veiculo_sinistrado.png";
          break;

        case "3-17":
        case "3-21":
          iconeUrl = "assets/images/icones-atendimento/veiculo_monitorado.png";
          break;
        
        case "3-15":
        case "3-16":
        case "3-18":
        case "3-19":
        case "3-20":
        case "3-22":
        case "3-23":
          iconeUrl = "assets/images/icones-atendimento/veiculo_recuperado.png";
          break;

        default:
          iconeUrl = "assets/images/icones-atendimento/registro_fato.png";
          break;
      }

      const marker = new google.maps.Marker({
        position: pos,
        map: mapa_google,
        icon: {
          url: iconeUrl,
          scaledSize: new google.maps.Size(45, 45)
        },
        title: r.descricaoEvento || r.placa || `Registro ${r.id}`
      });
      
      const tipoDescricao = tiposRegistroFato.find(t => parseInt(t.id) === parseInt(r.idTipo))?.tipo || "Tipo não identificado";

      const conteudoCard = `
        <div class="card shadow-sm" style="width: 20rem; font-size: 0.9rem;">
          <div class="card-body">
            <h5 class="card-title">Tipo: ${tipoDescricao}</h5>
            <p class="card-text mb-1">
              ${r.placa && r.placa.trim() !== "" && r.placa.trim().toLowerCase() !== "sem placa"
                ? `<strong>Placa:</strong> ${r.placa}<br>`
                : ""}
              <strong>Local:</strong> ${r.rua || ""}, ${r.numero || ""} - ${r.bairro || "N/D"}<br>
              <strong>Data:</strong> ${
                r.dataCriacao
                  ? r.dataCriacao.replace(
                      /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}).*$/,
                      "$3/$2/$1 $4:$5"
                    )
                  : "N/D"
              }
            </p>
      <button 
        onclick='abrirModalRegistroFato(${JSON.stringify({id: r.id})})' 
        class="btn btn-link p-0 m-0 align-baseline text-decoration-underline text-primary">
        Abrir ocorrência
      </button>

          </div>
        </div>
      `;

      marker.infowindow = new google.maps.InfoWindow({ content: conteudoCard });

      marker.addListener("click", function () {
        marker.infowindow.open(mapa_google, marker);
      });

      marcadores.push(marker);
    });
  }
}

function abrirModalRegistroFato(id){
	abrirModalEditarRegistroDeFatoAbos(id)
}

function switchPolilyne(){
	const isChecked = $("#recuperacao-veiculo").prop("checked");
	registro_fato = [];
    let lista = registro_fato_filtrado.length == 0 ? registro_fato : registro_fato_filtrado
	if(isChecked){
		ProcessaRotas(lista);
	} else {
		removerSomenteRotas();
	}
}

function ProcessaRotas(registroFatoLista) {
  marcadores.forEach(m => m.setMap(null));
  marcadores = [];

  rotas.forEach(r => r.setMap(null));
  rotas = [];

  const registrosPorId = {};

  registroFatoLista.forEach(function (item) {
    if (!registrosPorId[item.id]) {
      registrosPorId[item.id] = [];
    }
    registrosPorId[item.id].push(item);
  });

  Object.keys(registrosPorId).forEach(function (id) {
    const registros = registrosPorId[id];

    const pontoOrigem = registros.find(r => r.idEvento == 1);
    const pontoDestino = registros.find(r => r.idEvento == 3);

    if (
      pontoOrigem &&
      pontoDestino &&
      (pontoOrigem.latitude !== pontoDestino.latitude || pontoOrigem.longitude !== pontoDestino.longitude)
    ) {
      const pA = { lat: parseFloat(pontoOrigem.latitude), lng: parseFloat(pontoOrigem.longitude) };
      const pB = { lat: parseFloat(pontoDestino.latitude), lng: parseFloat(pontoDestino.longitude) };

      criarRota(pA, pB, "alta", mapa_google, { descricao: `Rota do registro ${id}` });
    }

    registros.forEach(function (r) {
      const pos = { lat: parseFloat(r.latitude), lng: parseFloat(r.longitude) };

      const eventoTipo = `${r.idEvento}-${r.idTipo}`;
      let iconeUrl;

      switch (eventoTipo) {
    	case "1-17":
        case "1-21":
          iconeUrl = "assets/images/icones-atendimento/veiculo_monitorado.png";
          break;
        
        case "1-13":
          iconeUrl = "assets/images/icones-atendimento/acidente_transito.png";
          break;
	
        case "1-2":
        case "1-4":
          iconeUrl = "assets/images/icones-atendimento/veiculo_sinistrado.png";
          break;
        
        case "1-15":
        case "1-16":
        case "1-18":
        case "1-19":
        case "1-20":
        case "1-22":
        case "1-23":
          iconeUrl = "assets/images/icones-atendimento/veiculo_sinistrado.png";
          break;
          
      	case "3-2":
          iconeUrl = "assets/images/icones-atendimento/veiculo_sinistrado.png";
          break;

        case "3-17":
        case "3-21":
          iconeUrl = "assets/images/icones-atendimento/veiculo_monitorado.png";
          break;
        
        case "3-15":
        case "3-16":
        case "3-18":
        case "3-19":
        case "3-20":
        case "3-22":
        case "3-23":
          iconeUrl = "assets/images/icones-atendimento/veiculo_recuperado.png";
          break;

        default:
          iconeUrl = "assets/images/icones-atendimento/registro_fato.png";
          break;
      }

      const marker = new google.maps.Marker({
        position: pos,
        map: mapa_google,
        icon: {
          url: iconeUrl,
          scaledSize: new google.maps.Size(45, 45)
        },
        title: r.descricaoEvento || `Registro ${id}`
      });

	const tipoDescricao = tiposRegistroFato.find(t => parseInt(t.id) === parseInt(r.idTipo))?.tipo || "Tipo não identificado";

      const conteudoCard = `
        <div class="card shadow-sm" style="width: 20rem; font-size: 0.9rem;">
          <div class="card-body">
            <h5 class="card-title">Tipo: ${tipoDescricao}</h5>
            <p class="card-text mb-1">
              ${r.placa && r.placa.trim() !== "" && r.placa.trim().toLowerCase() !== "sem placa"
                ? `<strong>Placa:</strong> ${r.placa}<br>`
                : ""}
              <strong>Local:</strong> ${r.rua || ""}, ${r.numero || ""} - ${r.bairro || "N/D"}<br>
              <strong>Data:</strong> ${
                r.dataCriacao
                  ? r.dataCriacao.replace(
                      /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}).*$/,
                      "$3/$2/$1 $4:$5"
                    )
                  : "N/D"
              }
            </p>
                  <button 
        onclick='abrirModalRegistroFato(${r.id})' 
        class="btn btn-link p-0 m-0 align-baseline text-decoration-underline text-primary">
        Abrir ocorrência
      </button>
          </div>
        </div>
      `;

      marker.infowindow = new google.maps.InfoWindow({ content: conteudoCard });

      marker.addListener("click", function () {
        marker.infowindow.open(mapa_google, marker);
      });

      marcadores.push(marker);
    });
  });
}

function removerMarkers() {
    marcadores.forEach(function(marker) {
        marker.setMap(null); 
    });
    removerSomenteRotas()
    marcadores = []; 
}


function removerSomenteRotas() {
    rotas.forEach(function(rota) {
        rota.setMap(null);
    });
    rotas = [];
}

function criarRota(pontoA, pontoB, intensidade, map, dados) {
  const cor = "#276FF5";
  const peso = 5;

  const rota = new google.maps.Polyline({
    path: [pontoA, pontoB],
    geodesic: true,
    strokeColor: cor,
    strokeOpacity: 0.9,
    strokeWeight: peso
  });

  rota.setMap(map);
  rotas.push(rota);

  const markerA = new google.maps.Marker({
    position: pontoA,
    map: map,
    icon: {
      url: "assets/images/icones-atendimento/veiculo_sinistrado.png",
      scaledSize: new google.maps.Size(45, 45)
    },
    title: dados?.descricao || "Ponto A"
  });
  marcadores.push(markerA);

  const markerB = new google.maps.Marker({
    position: pontoB,
    map: map,
    icon: {
      url: "assets/images/icones-atendimento/veiculo_recuperado.png",
      scaledSize: new google.maps.Size(45, 45)
    },
    title: dados?.descricao || "Ponto B"
  });
  marcadores.push(markerB);
}

function ObterDadosAtendimentos() {
	let TIPO_EVENTO_SELECIONADO = 1
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/MapaInterativo",
        data: { acao: "obterAtendimentos", idTipoEvento: TIPO_EVENTO_SELECIONADO }, 
        dataType: "json",
        success: function(data) {
		$("body").removeClass("loading"); 
            ProcessaDadosAtendimentos(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
			$("body").removeClass("loading"); 
            console.log("Erro ao processar requisição ao servidor: " + textStatus);
        }
    });
}

function ObterDadosGuarnicao() {
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/MapaInterativo",
        data: { acao: "obterGuarnicaoLocalizacao" }, 
        dataType: "json",
        success: function(data) {
		$("body").removeClass("loading"); 
            ProcessaDadosGuarnicao(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
			$("body").removeClass("loading"); 
            console.log("Erro ao processar requisição ao servidor: " + textStatus);
        }
    });
}

function ProcessaDadosGuarnicao(guarnicaoLocalizao) {

	guarnicaoLocalizao.forEach(function(guarnicao) {
	    var guarncaoResposta = {
	        idUsuario: guarnicao.idUsuario,
	        idGuarnicao: guarnicao.idGuarnicao,
	        nome: guarnicao.nome,
	        nomeGuarnicao: guarnicao.nomeGuarnicao,
	        telefone: guarnicao.telefone,
			disponivel: guarnicao.disponivel,
			latitude: guarnicao?.latitude,
			longitude: guarnicao?.longitude,
			responsavel: guarnicao?.responsavel
	    };
	    guarnicao_lista.push(guarncaoResposta);
	});

	allMarkers = [...new Set([...allMarkers, ...guarnicao_lista])];
    PopulaMarkers(atendimento_lista);
}

function ProcessaDadosAtendimentos(atendimentos) {

	atendimentos.forEach(function(atendimento) {
	    var atendimentoResposta = {
	        idAtendimento: atendimento.idAtendimento,
	        protocolo: atendimento.protocolo,
	        idOrigem: atendimento.idOrigem,
	        idSituacao: atendimento.idSituacao,
	        dataCriacaoAtendimento: atendimento.dataCriacaoAtendimento,
	        dataEncerramentoAtendimento: atendimento.dataEncerramentoAtendimento,
	        idUsuarioCriacao: atendimento.idUsuarioCriacao,
	        idRegistroFato: atendimento.idRegistroFato,
	        idTipoRegistro: atendimento.idTipoRegistro,
	        idStatusRegistro: atendimento.idStatusRegistro,
	        tipo: atendimento.tipo,
	        status: atendimento.status,
	        temBoletim: atendimento.temBoletim,
	        idUsuarioRegistro: atendimento.idUsuarioRegistro,
	        dataCriacaoRegistro: atendimento.dataCriacaoRegistro,
	        privado: atendimento.privado,
	        idEndereco: atendimento.idEndereco,
	        idTipoEvento: atendimento.idTipoEvento,
	        idCidade: atendimento.idCidade,
	        cidade: atendimento.cidade,
	        cep: atendimento.cep.replace(/^(\d{5})(\d{3})$/, "$1-$2"),
	        bairro: atendimento.bairro,
	        rua: atendimento.rua,
	        numero: atendimento.numero,
	        complemento: atendimento.complemento,
	        latitude: atendimento.latitude,
	        longitude: atendimento.longitude
	    };
	
	    atendimento_lista.push(atendimentoResposta);
	});

	allMarkers = [...new Set([...allMarkers, ...atendimento_lista])];
    PopulaMarkers(atendimento_lista);
}

function retornaMarker(tipo){
	switch (tipo) {
		case 1:
			return  new google.maps.MarkerImage('assets/images/icones-atendimento/atraso.png',
			        new google.maps.Size(45, 45), 
			        new google.maps.Point(0, 0),
			        new google.maps.Point(30   , 30)); 
		case 2:
			return  new google.maps.MarkerImage('assets/images/icones-atendimento/progresso.png',
			        new google.maps.Size(45, 45), 
			        new google.maps.Point(0, 0),
			        new google.maps.Point(30   , 30)); 
		case 3:
			return  new google.maps.MarkerImage('assets/images/icones-atendimento/liberado.png',
			        new google.maps.Size(45, 45), 
			        new google.maps.Point(0, 0),
			        new google.maps.Point(30   , 30)); 
		case 4: 
			return 	new google.maps.MarkerImage('assets/images/icones-atendimento/finalizado.png',
			        new google.maps.Size(45, 45), 
			        new google.maps.Point(0, 0),
			        new google.maps.Point(30   , 30)); 
        case 5:
        	return  new google.maps.MarkerImage('assets/images/icones-atendimento/guarnicao.png',
			        new google.maps.Size(45, 45), 
			        new google.maps.Point(0, 0),
			        new google.maps.Point(30   , 30)); 
		default:
			return  new google.maps.MarkerImage('assets/images/icones-atendimento/guarnicao.png',
			        new google.maps.Size(45, 45), 
			        new google.maps.Point(0, 0),
			        new google.maps.Point(30   , 30)); 
	}
}

function getIconeGuarnicao(point) {
    if (point.hasOwnProperty("protocolo")) {
        return retornaMarker(point.idSituacao);
    }

    if (!guarnicaoIconesMap.has(point.idGuarnicao)) {
        const index = guarnicaoIconesMap.size % iconesDisponiveis.length;
        const url = iconesDisponiveis[index];

        guarnicaoIconesMap.set(point.idGuarnicao, new google.maps.MarkerImage(
            url,
            new google.maps.Size(45, 45),
            new google.maps.Point(0, 0),
            new google.maps.Point(30, 30)
        ));
    }

    return guarnicaoIconesMap.get(point.idGuarnicao);
}

function exibeMarkers(){
	let tipo = $("#selectType").val();
	let textoSelecionado = $("#selectType option:selected").text();
		
	if (tipo == "treatment"){
		$("#situacao").show();		
		$("#dataInicio").show();
		$("#dataFim").show();
		$("#btn-atendimento").show();
		$("#btn-guarnicao").hide();
		$("#btn-registro-fato").hide();
		$("#dataFimRegistroFato").hide();
		$("#dataInicioRegistroFato").hide();
		$("#divSituacaoGuarnicao").hide();
		$('#recuperacaoVeiculo').hide();
		$('#recuperacaoVeiculo').css('visibility', 'hidden');
		$('#divTipo').hide();
		$("#div-mapa-calor")[0].style.setProperty("display", "none", "important");
    	PopulaMarkers([]);
		removerMarkers();
		PopulaMarkers(atendimento_lista)
	} else if (tipo == "garrison") {
		$("#divSituacaoGuarnicao").show();
		$("#situacao").hide();
		$("#btn-atendimento").hide();
		$("#btn-guarnicao").show();
		$("#btn-registro-fato").hide();
		$("#dataInicio").hide();
		$("#dataFim").hide();
		$("#dataFimRegistroFato").hide();
		$("#dataInicioRegistroFato").hide();
		$('#recuperacaoVeiculo').css('visibility', 'hidden');
		$('#divTipo').hide();
		$("#div-mapa-calor")[0].style.setProperty("display", "none", "important");
    	PopulaMarkers([]);
		removerMarkers();
		PopulaMarkers(guarnicao_lista)
	} else if (tipo == "fact"){
		$("#divSituacaoGuarnicao").hide();
		$("#situacao").hide();
		$("#dataInicio").hide();
		$("#btn-atendimento").hide();
		$("#btn-guarnicao").hide();
		$("#btn-registro-fato").show();
		$("#dataFimRegistroFato").show();
		$("#dataInicioRegistroFato").show();
		$("#dataFim").hide();
		$('#recuperacaoVeiculo').css('visibility', 'visible');
		$('#divTipo').show();
		$('#div-mapa-calor').show();
		$("#label-mapa-calor").text("Mapa de Calor " + textoSelecionado);
    	PopulaMarkers([]);
		removerMarkers();
		ProcessaRotas(registro_fato);
	}
	  else if (tipo == "pcl"){
    $("#situacao").hide();
		$("#dataInicio").hide();
		$("#dataFim").hide();
		$("#dataFimRegistroFato").hide();
		$("#btn-atendimento").hide();
		$("#btn-guarnicao").hide();
		$("#btn-registro-fato").hide();
		$("#dataInicioRegistroFato").hide();
		$("#divSituacaoGuarnicao").hide();
		$('#recuperacaoVeiculo').css('visibility', 'hidden');
    	$('#divTipo').hide();
    	$("#div-mapa-calor")[0].style.setProperty("display", "none", "important");
    	PopulaMarkers([]);
    	removerMarkers();
    	populaMarkersDispositivos(dados_equipamento, false);
	} 
	   else {
		$("#situacao").hide();
		$("#dataInicio").hide();
		$("#dataFim").hide();
		$("#dataFimRegistroFato").hide();
		$("#btn-atendimento").hide();
		$("#btn-guarnicao").hide();
		$("#btn-registro-fato").hide();
		$("#dataInicioRegistroFato").hide();
		$("#divSituacaoGuarnicao").hide();
		$('#recuperacaoVeiculo').css('visibility', 'hidden');
		$('#divTipo').hide();
		$("#div-mapa-calor")[0].style.setProperty("display", "none", "important");
		ProcessaRotas(registro_fato);
    	PopulaMarkers(allMarkers);
    	populaMarkersDispositivos(dados_equipamento, true);
	}
	
}

function filtraStatus(){
		let tipo = $("#selectSituation").val()
		let atendimento_lista_filtrada =[]
	
	switch (tipo) {
		case "1":
			atendimento_lista_filtrada = atendimento_lista.filter(x => x.idSituacao == "1")
			break;
		case "2":
			atendimento_lista_filtrada = atendimento_lista.filter(x => x.idSituacao == "2")
			break;
		case "3":
			atendimento_lista_filtrada = atendimento_lista.filter(x => x.idSituacao == "3")
			break;
		case "4":
			atendimento_lista_filtrada = atendimento_lista.filter(x => x.idSituacao == "4")
			break;
		default:
			atendimento_lista_filtrada = atendimento_lista
			break;
	}
	PopulaMarkers(atendimento_lista_filtrada)
}

function filtraPorData() {
    const dataInicio = $("#dataInicio").val();
    const dataFim = $("#dataFim").val();
    let atendimento_lista_filtrada = atendimento_lista;

    if (dataInicio) {
        atendimento_lista_filtrada = atendimento_lista_filtrada.filter(x => {
            if (!x.dataCriacaoAtendimento) return false;
            const dataAtendimento = new Date(x.dataCriacaoAtendimento.split(" ")[0].split("/").reverse().join("-"));
            return dataAtendimento >= new Date(dataInicio);
        });
    }

    if (dataFim) {
        atendimento_lista_filtrada = atendimento_lista_filtrada.filter(x => {
            if (!x.dataCriacaoAtendimento) return false;
            const dataAtendimento = new Date(x.dataCriacaoAtendimento.split(" ")[0].split("/").reverse().join("-"));
            return dataAtendimento <= new Date(dataFim);
        });
    }

    PopulaMarkers(atendimento_lista_filtrada);
}

function filtraStatusGuarnicao() {
    let tipo = $("#situacaoGuarnicao").val();
    let guarnicao_lista_filtrada = [];

    switch (tipo) {
        case "sim": 
            guarnicao_lista_filtrada = guarnicao_lista.filter(x => x.disponivel === true);
            break;
        case "nao": 
            guarnicao_lista_filtrada = guarnicao_lista.filter(x => x.disponivel === false);
            break;
        default: 
            guarnicao_lista_filtrada = guarnicao_lista;
            break;
    }

    PopulaMarkers(guarnicao_lista_filtrada);
}

function filtraTipoRegistroFato() {
  const tipo = $("#tipo").val();
  if (tipo === "todos") {
    registro_fato_filtrado = registro_fato;
  } else {
    const tipoInt = parseInt(tipo);
    registro_fato_filtrado = registro_fato.filter(r => parseInt(r.idTipo) === tipoInt);
  }

  ProcessaRotas(registro_fato_filtrado);
}

function filtraDataRegistroFato() {
  const dataInicio = $("#dataInicioRegistroFatoInput").val();
  const dataFim = $("#dataFimRegistroFatoInput").val();

  if (!dataInicio && !dataFim) {
    registro_fato_filtrado = registro_fato;
  } else {
    const inicio = dataInicio ? new Date(dataInicio) : null;
    const fim = dataFim ? new Date(dataFim) : null;

    registro_fato_filtrado = registro_fato.filter(r => {
      const data = new Date(r.dataCriacao);

      if (inicio && !fim) {
        return data >= inicio;
      }

      if (!inicio && fim) {
        return data <= fim;
      }

      if (inicio && fim) {
        return data >= inicio && data <= fim;
      }

      return true;
    });
  }

  ProcessaRotas(registro_fato_filtrado);
}

function PopulaMarkers(markersLista) {
    markersArray.forEach(function(marker) {
        marker.setMap(null); 
    });

    var bounds = new google.maps.LatLngBounds();

    markersLista.forEach(function(point) {
        var pos = new google.maps.LatLng(point?.latitude, point?.longitude);

        var marker = new google.maps.Marker({
            position: pos,
            map: mapa_google,
            icon: getIconeGuarnicao(point),
            infowindow: new google.maps.InfoWindow({ content: '' }),
            idDispositivo: point?.idDispositivo,
            serieEquipamento: point?.serieEquipamento,
            codOrgao: point?.codOrgao,
            descDispositivo: point?.descDispositivo
        });

		if(point.latitude != undefined){
        	markersArray.push(marker);			
		}

		if(point.latitude != undefined){
        	bounds.extend(pos);
        }

        google.maps.event.addListener(marker, 'click', function() {
			var dados = "";
			if(point.hasOwnProperty("protocolo")){
				dados = `
	            <div class="card shadow-sm" style="width: 20rem; font-size: 0.9rem;">
	                <div class="card-body">
	                    <h5 class="card-title">${point.protocolo}</h5>
	                    <p class="card-text mb-1">
	                        <strong>Endereço:</strong> ${point.rua}, ${point.numero} - ${point.bairro || 'N/D'}<br>
	                        <strong>Cidade:</strong> ${point.cidade || 'N/D'}<br>
	                        <strong>CEP:</strong> ${point.cep || 'N/D'}<br>
	                        <strong>Complemento:</strong> ${point.complemento || 'N/D'}
	                    </p>
	                    <p class="card-text mb-1">
	                        <span class="badge bg-info text-dark">Tipo Evento: ${point.tipo}</span>
	                        <span class="badge bg-${point.temBoletim ? 'success' : 'warning'}">
	                            Boletim: ${point.temBoletim ? 'Sim' : 'Não'}
	                        </span>
	                        <span class="badge bg-${point.status == 'Ativo' ? 'success' : 'warning'}">
	                            Situação: ${point.status}
	                        </span>
	                    </p>
	                    <p class="card-text mb-2">
	                        <strong>Data Criação:</strong> ${point.dataCriacaoAtendimento || 'N/D'}<br>
	                        <strong>Data Encerramento:</strong> ${point.dataEncerramentoAtendimento || 'Em andamento'}
	                    </p>
	                    <button 
						  onClick='modalDetalhes(${JSON.stringify({id: point.idAtendimento, protocolo: point.protocolo})})' 
						  class="btn btn-primary w-100" 
						  data-bs-dismiss="modal">
						  Detalhes
						</button>
	                </div>
	            </div>`;
			}
			else {
				dados = `
				<div class="card shadow-sm border-success" style="width: 20rem; font-size: 0.9rem;">
				  <div class="card-header ${point.disponivel ? 'bg-success text-white' : 'bg-danger text-white'} d-flex justify-content-between align-items-center">
				    <span>
				      <i class="bi ${point.disponivel ? 'bi-shield-check' : 'bi-shield-exclamation'}"></i>
				      ${point.nomeGuarnicao}
				    </span>
				    <span class="badge ${point.disponivel ? 'bg-light text-success' : 'bg-light text-danger'}">
				      ${point.disponivel ? 'Liberada' : 'Em Atendimento'}
				    </span>
				  </div>
				  <div class="card-body">
				    <h5 class="card-title mb-2">${point.nome}</h5>
				    <p class="card-text mb-2">
				      <strong>Responsável:</strong> ${point.responsavel}<br>
				      <strong>Status:</strong> ${point.disponivel ? 'Pronta para atendimento' : 'Em atendimento'}<br>
				      <strong>Telefone: </strong> ${point.telefone}
				    </p>
				  </div>
				</div>
				`;
			}

            marker.infowindow.setContent(dados);
            marker.infowindow.open(mapa_google, marker);
        });
    });

}

function modalDetalhes(obj){
	obterOcorrencias(obj)
}

function obterAtendimentos() {
  $.ajax({
    type: "GET",
    url: "/MuralhaDigital/Atendimento",
    dataType: "xml",
    data: { acao: "obterVeiculosSinistradosRecuperados" },
    success: function (data) {
      const $xml = $(data);
      const lista = $xml.find("registrosFato").find("registroFato");

      lista.each(function () {
        const $registro = $(this);

        const atendimento = {
          id:               $registro.find("id").text(),
          placa:            $registro.find("placa").text(),
          descricaoEvento:  $registro.find("descricaoEvento").text(),
          idEvento:         $registro.find("idEvento").text(),
          idTipo:           $registro.find("idTipo").text(),
          latitude:         $registro.find("latitude").text(),
          longitude:        $registro.find("longitude").text(),
          rua:              $registro.find("rua").text(),
          numero:           $registro.find("numero").text(),
          bairro:           $registro.find("bairro").text(),
          dataCriacao:      $registro.find("dataCriacao").text(),
          dataEncerramento: $registro.find("dataEncerramento").text()
        };

        registro_fato.push(atendimento);
      });
		registro_fato_filtrado = [...registro_fato];
    },

    error: function (jqXHR, textStatus, errorThrown) {
      console.error("Erro ao obter registros:", errorThrown);
      alert("Não foi possível carregar os atendimentos.");
    }
  });
}

function obterOcorrencias(obj) {    
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/Atendimento",
        dataType: "xml",
        data: "acao=obterOcorrencias",
        success: function (data) {
            const $xml = $(data);        
            const lista = $xml.find("listaAtendimentos").find("atendimento");
            const ocorrencias = [];

            lista.each(function () {
                const $alerta = $(this);
				
                const id 				= $alerta.find("id_ocorrencia").text();
                const origem 			= $alerta.find("origem").text();
                const tipo 				= $alerta.find("tipoAlerta").text();
                const dataHora 			= $alerta.find("dataAlertaFormatada").text();
                const placa 			= $alerta.find("placaVeiculo").text();
                const descricao 		= $alerta.find("descricao").text();
                const origem_registro 	= $alerta.find("origemRegistro").text();
                const prioridade 		= $alerta.find("prioridade").text();
                const status 			= $alerta.find("statusOcorrencia").text();
                const dataCriacao 		= $alerta.find("dataAtendimentoFormatada").text();
                const dataEncerramento 	= $alerta.find("dataEncerramentoFormatada").text();
                const idSituacaoEnvio 	= $alerta.find("idSituacaoEnvio").text();
                const idAtendimento 	= $alerta.find("idAtendimento").text();
                const idGuarnicao 		= $alerta.find("idGuarnicao").text();
                const protocolo 		= $alerta.find("protocolo").text();
                const idAlerta 			= $alerta.find("idAlerta").text();
                const idRegistroFato	= $alerta.find("idRegistroFato").text();
                
                ocorrencias.push({
                    id: 				id,  
                    origem: 			origem,                  
                    placaVeiculo: 		placa || "Sem placa",
                    data: 				dataHora,
                    tipo: 				tipo, 
                    descricao: 			descricao, 
                    origem_registro: 	origem_registro,
                    prioridade: 		prioridade,
                    status: 			status,
                    dataCriacao: 		dataCriacao,
                    dataEncerramento: 	dataEncerramento,
                    idSituacaoEnvio: 	idSituacaoEnvio,
                    idAtendimento: 		idAtendimento,
                    protocolo: 			protocolo,
                    idGuarnicao: 		idGuarnicao,
                    idAlerta: 			idAlerta,
                    idRegistroFato:		idRegistroFato,
                                     
                });
            }); 
            abrirModalDetalhes(obj, ocorrencias)
        },

        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao obter alertas:", errorThrown);
        }
    });
}

async function abrirModalDetalhes(obj, ocorrencias) {
	obterHistorico(obj.id); 

	try {
        const historicos = await obterHistorico(obj.id);
        const docs		 = await obterDocumentos(obj.id);
        
        const ocorrenciaSelecionada = ocorrencias.find(o => o.protocolo === obj.protocolo);

        preencherModalDetalhes(ocorrenciaSelecionada, historicos);
        preencherDocumentos(docs)

        const modalElement = document.getElementById('modalDetalhes');
        const modal = new bootstrap.Modal(modalElement);
        modal.show();

    } catch (error) {
        console.error("Erro ao abrir modal com histórico:", error);
    }
}

function obterHistorico(idAtendimento) {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            url: "/MuralhaDigital/Atendimento",
            dataType: "xml",
            data: {
                acao: "obterHistorico",
                idAtendimento: idAtendimento
            },
            success: function (data) {
                const $xml = $(data);
                const lista = $xml.find("listaHistoricos").find("historico");
                
                const historicos = [];
                lista.each(function () {
                    const $historico = $(this);
                    historicos.push({
                        descricao: $historico.find("tipoHistorico").text(),
                        evento: $historico.find("evento").text(),
                        data: $historico.find("dataFormatada").text(),
                        nome: $historico.find("usuario").text()
                    });
                });         
                
                resolve(historicos);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Erro ao obter histórico:", errorThrown);
                reject(errorThrown);
            }
        });
    });
}

function obterDocumentos(idAtendimento) {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            url: "/MuralhaDigital/Anexo",
            dataType: "xml",
            data: {
                acao: "obterDocumentos",
                idAtendimento: idAtendimento
            },
            success: function (data) {
			const $xml = $(data);
            const lista = $xml.find("listaDocumentos").find("documento");
            const documentos = [];
                lista.each(function () {
                    const $documento = $(this);
                    documentos.push({
						id: $documento.find("id").text(),
                        detalhamento: $documento.find("detalhamento").text(),
                        tipo: $documento.find("tipo").text(), 
                        dirArquivo: $documento.find("dirArquivo").text(),                       
                    });
                });
                
                resolve(documentos);               
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Erro ao obter documentos:", errorThrown);
                reject(errorThrown);
            }
        });
    });
}

function preencherModalDetalhes(ocorrencia, historicos) {
    const conteudoDetalhes = document.getElementById("conteudoModalDetalhes");
    conteudoDetalhes.innerHTML = `
        <p><strong>Protocolo:</strong> ${ocorrencia.protocolo === "" ? 'Atendimento não iniciado' : ocorrencia.protocolo}</p>
        <p><strong>Origem da ocorrência:</strong> ${ocorrencia.origem}</p>
        <p><strong>Placa:</strong> ${ocorrencia.placaVeiculo && ocorrencia.placaVeiculo.trim() ? ocorrencia.placaVeiculo : 'N/D'}</p>
        <p><strong>Tipo:</strong> ${ocorrencia.tipo}</p>
        <p><strong>Data da ocorrência:</strong> ${ocorrencia.data}</p>
        <p><strong>Observação:</strong> ${ocorrencia.descricao && ocorrencia.descricao.trim() ? ocorrencia.descricao : 'N/D'}</p>
    `;
    

    const conteudoHistorico = document.getElementById("conteudoModalHistorico");
    if (historicos.length === 0) {
        conteudoHistorico.innerHTML = "<p>Nenhum histórico encontrado.</p>";
    } else {
        let htmlHistorico = "";
        historicos.forEach(h => {
            htmlHistorico += `
                <div class="mb-3">
                    <p><strong>Tipo:</strong> ${h.descricao}</p>
                    <p><strong>Evento:</strong> ${h.evento}</p>
                    <p><strong>Data:</strong> ${h.data}</p>
                    <p><strong>Usuário:</strong> ${h.nome}</p>
                    <hr>
                </div>
            `;
        });
        conteudoHistorico.innerHTML = htmlHistorico;
    }
}

function preencherDocumentos(documentos) {
    const lista = document.getElementById('conteudoModalDocumentos');
    lista.innerHTML = '';

    if (documentos.length === 0) {
        lista.innerHTML = '<p>Nenhum documento encontrado.</p>';
    } else {
       
	    documentos.forEach(doc => {
		    const container = document.createElement('div');
		    container.className = 'd-flex justify-content-between align-items-center mb-2 p-2 border rounded bg-light shadow-sm';
		
		    const infoDiv = document.createElement('div');
		    infoDiv.className = 'd-flex align-items-center';
		
		    const icon = document.createElement('span');
		    icon.innerHTML = `
		        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="#0d6efd" class="bi bi-file-earmark-text me-2" viewBox="0 0 16 16">
		          <path d="M14 4.5V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2h6.5L14 4.5z"/>
		          <path d="M6 5h4v1H6V5zm0 2h4v1H6V7zm0 2h2v1H6V9z"/>
		        </svg>
		    `;		
		    const texto = document.createElement('span');
		    texto.textContent = `${doc.detalhamento} (${doc.tipo})`;
		
		    infoDiv.appendChild(icon);
		    infoDiv.appendChild(texto);
		
		    const btn = document.createElement('a');
		    btn.href = `/MuralhaDigital/Anexo?acao=downloadDocumento&id=${doc.id}`;
		    btn.className = 'btn btn-sm btn-outline-success';
		    btn.target = '_blank';
		    btn.innerHTML = `
		        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download me-1" viewBox="0 0 16 16">
		          <path d="M.5 9.9V11a1 1 0 0 0 1 1h13a1 1 0 0 0 1-1V9.9h-1V11H1V9.9H.5z"/>
		          <path d="M7.646 10.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 1 0-.708-.708L8.5 9.293V1.5a.5.5 0 0 0-1 0v7.793L5.354 7.146a.5.5 0 1 0-.708.708l3 3z"/>
		        </svg>
		        Baixar
		    `;
		    container.appendChild(infoDiv);
		    container.appendChild(btn);
		    lista.appendChild(container);
		});
    }
}

function ReiniciarMarkersIcons()
{
	for (i = 0; i < markersArray.length; i++)
	{
		markersArray[i].setIcon(off_with_alert);
	} 	
}

function carregarTiposRegistroFato() {
  $.ajax({
    type: "GET",
    url: "/MuralhaDigital/Atendimento",
    data: { acao: "obterTiposRegistroFato" },
    dataType: "xml",
    success: function (xml) {
      const $select = $("#tipo");
      $select.empty();

      $select.append('<option value="todos">Todos</option>');

      $(xml).find("tipo").each(function () {
        const id = $(this).find("id").text();
        const descricao = $(this).find("descricao").text();
        tiposRegistroFato.push({ id: parseInt(id), tipo: descricao });
        $select.append(`<option value="${id}">${descricao}</option>`);
      });
      $select.select2({
        placeholder: "Selecione pelo menos um tipo",
        allowClear: true,
        width: 'resolve'
      });
      $select.val(["todos"]).trigger("change");
    },
    error: function (xhr, status, error) {
      console.error("Erro ao carregar tipos de registro:", error);
      alert("Não foi possível carregar os tipos de registro.");
    }
  });
}

function aplicarFiltrosRegistroFato() {
  const dataInicio = $("#dataInicioRegistroFatoInput").val();
  const dataFim = $("#dataFimRegistroFatoInput").val();
  const tiposSelecionados = $("#tipo").val(); 
  const mostrarRotas = $("#recuperacao-veiculo").is(":checked");

  let resultado = registro_fato;

  if (dataInicio || dataFim) {
    const inicio = dataInicio ? new Date(dataInicio) : null;
    const fim = dataFim ? new Date(dataFim) : null;

    resultado = resultado.filter(r => {
      const data = new Date(r.dataCriacao);
      if (inicio && !fim) return data >= inicio;
      if (!inicio && fim) return data <= fim;
      if (inicio && fim) return data >= inicio && data <= fim;
      return true;
    });
  }

  if (tiposSelecionados && !tiposSelecionados.includes("todos")) {
    resultado = resultado.filter(r => tiposSelecionados.includes(r.idTipo.toString()));
  }

  registro_fato_filtrado = resultado;

  switchRotas(mostrarRotas);
}


function aplicarFiltrosGuarnicao(){
	filtraStatusGuarnicao();

}

function aplicarFiltrosAtendimento() {
  const tipo = $("#selectSituation").val();
  const dataInicio = $("#dataInicioAtendimentoInput").val();
  const dataFim = $("#dataFimAtendimentoInput").val();

  let resultado = atendimento_lista;

  // Filtro por data
  resultado = resultado.filter(x => {
    if (!x.dataCriacaoAtendimento) return false;

    const [dataStr] = x.dataCriacaoAtendimento.split(" ");
    const [dia, mes, ano] = dataStr.split("/").map(Number);
    const dataAtendimento = new Date(ano, mes - 1, dia);

    let passou = true;

    if (dataInicio) {
      const [anoI, mesI, diaI] = dataInicio.split("-").map(Number);
      const inicio = new Date(anoI, mesI - 1, diaI);
      if (dataAtendimento < inicio) passou = false;
    }

    if (dataFim) {
      const [anoF, mesF, diaF] = dataFim.split("-").map(Number);
      const fim = new Date(anoF, mesF - 1, diaF, 23, 59, 59);
      if (dataAtendimento > fim) passou = false;
    }

    return passou;
  });

  if (tipo && tipo !== "todos" && tipo !== "0" && tipo !== "5") {
    const tipoInt = parseInt(tipo);
    resultado = resultado.filter(x => parseInt(x.idSituacao) === tipoInt);
  }

  PopulaMarkers(resultado);
}

function obterDispositivosEquipamentosMisto() {
  $.ajax({
    type: "GET",
    url: "/MuralhaDigital/MapaDispositivosEquipamentos",
    data: {
      acao: "obterDispositivosEquipamentosMisto",
      equipamentos: ""
    },
    dataType: "xml",
    success: function(data, textStatus, jqXHR) {
      const dispositivos = data.getElementsByTagName("Dispositivo");
      dados_equipamento = [];

      for (let i = 0; i < dispositivos.length; i++) {
        const dispositivo = dispositivos[i];
        const lat = dispositivo.getElementsByTagName("latitude")[0]?.textContent;
        const lng = dispositivo.getElementsByTagName("longitude")[0]?.textContent;
        const desc = dispositivo.getElementsByTagName("descDispositivo")[0]?.textContent;

        if (lat && lng) {
          const latNum = parseFloat(lat);
          const lngNum = parseFloat(lng);

          if (!isNaN(latNum) && !isNaN(lngNum)) {
            dados_equipamento.push({
              latitude: latNum,
              longitude: lngNum,
              descricao: desc
            });
            allMarkers = [...new Set([...allMarkers, ...dados_equipamento])];
          } else {
            console.warn("⚠️ Coordenadas inválidas:", lat, lng);
          }
        } else {
          console.warn("⚠️ Dispositivo sem coordenadas:", dispositivo);
        }
      }
      exibeMarkers();
    },
    error: function(jqXHR, textStatus, errorThrown) {
      console.error("❌ Erro ao obter dispositivos:", errorThrown);
    }
  });
}


function populaMarkersDispositivos(lista, all) {
	
	if(all == false){
		removerMarkers();
		marcadores = [];	
	}

  lista.forEach(item => {
    const { latitude, longitude, descricao } = item;

    if (!isFinite(latitude) || !isFinite(longitude)) {
      console.warn("⚠️ Coordenadas inválidas:", latitude, longitude);
      return;
    }

    const marker = new google.maps.Marker({
      position: { lat: latitude, lng: longitude },
      map: mapa_google,
      icon: {
        url: "assets/images/icones-atendimento/radar_transp.png",
        scaledSize: new google.maps.Size(40, 40)
      }
    });

	const conteudoCard = `
	  <div style="
	        max-width:300px; 
	        min-width:260px; 
	        font-family: Arial, sans-serif; 
	        font-size:14px; 
	        border:1px solid #ddd; 
	        border-radius:6px; 
	        box-shadow:0 0.125rem 0.25rem rgba(0,0,0,0.075); 
	        padding:10px; 
	        word-wrap:break-word;">
	    <h6 style="margin:0; font-weight:bold; line-height:1.3;">
	      ${descricao || "Dispositivo sem descrição"}
	    </h6>
	    <p style="margin:2px 0;">
	      <a href="/muralha-digital/pages/mapa-equipamento/mapa-dispositivos.jsp">
	        Tela de dispositivos
	      </a>
	    </p>
	  </div>
	`;

    const infoWindow = new google.maps.InfoWindow({
      content: conteudoCard
    });

    marker.addListener("click", function () {
      infoWindow.open(mapa_google, marker);
    });

    marcadores.push(marker);
  });
}

document.addEventListener('disparaEventoAlteracao', function (e) {
	window.location.reload();
});

function inputRangeOpacity() {
    const sliderOpacity = document.getElementById("heatmap");
    const valueOpacity = parseInt(sliderOpacity.value);

    heatmap.set('opacity', valueOpacity / 100);
}
