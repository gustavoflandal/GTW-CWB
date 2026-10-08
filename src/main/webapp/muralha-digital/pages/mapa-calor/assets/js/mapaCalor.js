var map;
var heatmap;

function initMap() {
	
	console.log('Iniciando tela de mapa de calor');
	var opcoesMapa = {
        zoom: ZOOM_PADRAO,
        //center: LatLngPadrao
        center: { lat: -25.4289541, lng: -49.267137 }
	};
    // Cria uma nova instância do mapa no elemento com id "map-canvas"
    map = new google.maps.Map(document.getElementById("map-canvas"), opcoesMapa);

    // Inicializa a HeatmapLayer (camada de calor)
    heatmap = new google.maps.visualization.HeatmapLayer({
        data: [], // Dados iniciais vazios
        map: map,
        radius: 30,
        opacity: 0.9,
        dissipating: true,
        maxIntensity: 5,
        gradient: null
    });
    heatmap.setMap(map); // Associa a camada de calor ao mapa
}

/*
    Essa função abaixo faz uma estilização do botão range do input,
    para seguir com a mesa cor após mudar o valor.
*/
document.getElementById("heatmap").oninput = function() {
    var value = (this.value-this.min)/(this.max-this.min)*100
    this.style.background = 'linear-gradient(to right, #0d75bf 0%, #0d75bf ' + value + '%, #fff ' + value + '%, white 100%)'
};

/*
    Essa função pega o valor do input range e com base no que o usuário
    escolher, altera a opacidade do mapa de calor.
*/
function inputRangeOpacity() {
    const sliderOpacity = document.getElementById("heatmap");
    const valueOpacity = parseInt(sliderOpacity.value);

    heatmap.set('opacity', valueOpacity / 100);
}

$(document).ready(function () {
    ObterListaPermissoesFuncionalidades();

    //Listener para atualizar a cor do polígono em tempo real.
    document.getElementById("heatmap").addEventListener("input", function () {
        inputRangeOpacity(this.value);
    });
});

// Função auxiliar para criar um atraso (delay)
function sleep(time) {
    return new Promise((resolve) => setTimeout(resolve, time));
}

// Função para obter os dados dos radares/alertas do backend
function obterDadosRadares(dataIni = null, dataFim = null, tipoAlerta = null, categoria = null) {
    let parametros = {}; // Objeto para armazenar os parâmetros da requisição
    if (dataIni && dataFim) {
        parametros.dataIni = dataIni;
        parametros.dataFim = dataFim;
    }
    if (tipoAlerta && tipoAlerta !== '0') { 
        parametros.tipoAlerta = tipoAlerta;
    }
    if (categoria && categoria !== '0') { 
        parametros.categoria = categoria;
    }

    if (!map || !heatmap) {
        console.warn("Mapa ou Heatmap não inicializado ao tentar obter dados de radares.");
        return;
    }

    // Requisição AJAX para buscar os dados
    $.ajax({
        type: "GET", 
        url: "/MuralhaDigital/MapaCalor", 
        data: parametros,
        dataType: "xml", 
        success: function (data, textStatus, jqXHR) {
            processaDados(jqXHR.responseText); 
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("(obterDadosRadares) Erro ao buscar dados!", textStatus, errorThrown);
        }
    });
}


function processaDados(xmlString) {
    var xmlDoc = $.parseXML(xmlString);
    var $xml = $(xmlDoc);

    var pontos = [];

    $xml.find('itemPontoCalor').each(function () {
        var $interacao = $(this);

        var idLocal = $interacao.find('id').text();
        var descLocal = $interacao.find('descLocal').text();
        var latitude = $interacao.find('latitude').text();
        var longitude = $interacao.find('longitude').text();
        var alertas = $interacao.find('qtdeAlertas').text();

        var latLng = new google.maps.LatLng(parseFloat(latitude), parseFloat(longitude));

        var ponto = {
            location: latLng,
            weight: parseInt(alertas) 
        };
        pontos.push(ponto);
    });

    if (pontos.length === 0) {
        $('#aviso-sem-ocorrencias').text('Não há ocorrências ou alertas para os filtros selecionados.').show();
        console.log("Nenhuma ocorrência encontrada.");
        populaPontosCalor([]);
    } else {
        $('#aviso-sem-ocorrencias').hide();
        populaPontosCalor(pontos);
    }
}



function populaPontosCalor(pontos) {
    if (heatmap) {
        heatmap.setData(pontos);
        console.log('O Mapa de calor foi carregado com sucesso com a HeatmapLayer!');
    } else {
        console.error("A camada de calor (HeatmapLayer) não está inicializada para popular os pontos.");
    }
}


function ProcessarConsulta() {
    var tipoAlerta = document.getElementById("selectTpAlertaOcorr").value; 
    var tempo = parseInt(document.getElementById("selectTempo").value); 
    var select = document.getElementById("selectTpAlertaOcorr");
    var descricaoAlerta = select.options[select.selectedIndex].text; 
    
    var partes = descricaoAlerta.split("--").map(function(p) {
        return p.trim();
    });
    var categoria = partes[0].trim(); 

    let dataFim = new Date(); 
    let dataIni = new Date(dataFim); 

    if (tempo > 0) { 
        dataIni.setHours(dataFim.getHours() - (tempo * 24));
    } else {
        console.log('Nenhum intervalo de tempo selecionado, usando data atual como início e fim.');
    }
    console.log('Tipo de alerta (categoria): ', categoria );

    const formatarData = (data) => {
        const dia = String(data.getDate()).padStart(2, '0');
        const mes = String(data.getMonth() + 1).padStart(2, '0'); 
        const ano = data.getFullYear();
        return `${dia}/${mes}/${ano}`;
    };

    console.log("Processando consulta com intervalo:", formatarData(dataIni), "-", formatarData(dataFim));

    obterDadosRadares(formatarData(dataIni), formatarData(dataFim), tipoAlerta, categoria);
}

function ObterListaPermissoesFuncionalidades() {
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/PermissoesFuncionalidade", 
        data: "acao=ListaFuncionalidadesByUsuarioId", 
        dataType: "xml",
        success: function (data, textStatus, jqXHR) {
            console.log("ObterListaPermissoesFuncionalidades()::Chegou dados");
            CarregaSelectTipos(jqXHR.responseText); 
            
            setTimeout(() => {
                if (map && heatmap) {
                    $('#selectTempo').val('1'); 
                    ProcessarConsulta(); 
                } else { 
                    console.warn("Mapa não pronto, adiando ProcessarConsulta inicial.");

                    let mapReadyInterval = setInterval(function() {
                        if (map && heatmap) {
                            clearInterval(mapReadyInterval); 
                            $('#selectTempo').val('1'); 
                            ProcessarConsulta();
                        }
                    }, 500); 
                }
            }, 300); 
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log("(ObterListaPermissoesFuncionalidades) Erro ao processar requisição ao servidor)!!", textStatus, errorThrown);
        }
    });
}

function CarregaSelectTipos(xmlString) {
    var xmlDoc = $.parseXML(xmlString);
    var $xml = $(xmlDoc);

    var lista = $xml.find('PermissoesFuncionalidade').find('ListaPermissoesFuncionalidade');
    var selectElement = $('#selectTpAlertaOcorr');

    lista.find('PermissaoFuncionalidade').each(function () {
        var $permissao = $(this);

        var id = $permissao.find('idPermFunc').text(); 
        var descricao = $permissao.find('descricao').text();
        selectElement.append($('<option>', {
            value: id,
            text: descricao
        }));
    });
}

// Isso é necessário para o callback da API do Google Maps
window.initMap = initMap;
