let mapaBlitz;
let drawingManagerBlitz;
let poligonoBlitz = null;
let circuloBlitz = null;
let pontoRaioBlitz = null;
let markersLocaisBlitz = [];
let locaisMapa = [];
let mapaInicializado = false;
let geocodingEmExecucao = false;

$(document).on('click', '#btnSelecionarViaMapa', function () {
    const container = $('#containerMapaLocais');
    const botao = $(this);

    if (container.is(':visible')) {
        container.slideUp();
        botao.text('Selecionar via mapa');
    } else {
        $('#mapaBlitz').show();
        
        container.slideDown();

        if (!mapaInicializado) {
            inicializarMapaBlitz();
            carregarLocaisMapa();
            mapaInicializado = true;
        }

        carregarRaioRadares();

        setTimeout(function () {
            google.maps.event.trigger(mapaBlitz, "resize");
        }, 300);

        botao.text('Ocultar mapa');
    }
});

$(document).on('click', '#btnCentralizarMapa', function () {
    if (geocodingEmExecucao) return;

    const endereco = $('#inputEnderecoMapa').val();
    if (!endereco) return;

    geocodingEmExecucao = true;

    if (poligonoBlitz) poligonoBlitz.setMap(null);
    if (circuloBlitz) circuloBlitz.setMap(null);

    $('#mapaBlitz').hide();
    $('#loadingGeocoding').css('display', 'flex');

    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/Blitz",
        dataType: "json",
        data: {
            acao: "geocodingMapa",
            endereco: endereco
        },
        success: function (response) {
            if (response && response.lat && response.lon) {
                const novaPosicao = new google.maps.LatLng(
                    parseFloat(response.lat),
                    parseFloat(response.lon)
                );

                mapaBlitz.setCenter(novaPosicao);
                mapaBlitz.setZoom(16);
            }
        },
        complete: function () {
            geocodingEmExecucao = false;
            $('#loadingGeocoding').hide();
            $('#mapaBlitz').show();
        }
    });
});

$(document).on('click', '#btnAplicarLocaisMapa', function () {
    let idsSelecionados = [];

    if ($('input[name="modoSelecaoMapa"]:checked').val() === 'poligono') {
        if (!poligonoBlitz) return;

        markersLocaisBlitz.forEach(function (marker) {
            if (google.maps.geometry.poly.containsLocation(
                marker.getPosition(),
                poligonoBlitz
            )) {
                idsSelecionados.push(marker.id_local);
            }
        });

    } else {
        if (!circuloBlitz) return;

        markersLocaisBlitz.forEach(function (marker) {
            const distancia =
                google.maps.geometry.spherical.computeDistanceBetween(
                    marker.getPosition(),
                    circuloBlitz.getCenter()
                );
            if (distancia <= circuloBlitz.getRadius()) {
                idsSelecionados.push(marker.id_local);
            }
        });
    }

    const select = $('#selectLocais');
    const todosLocais = $('#todosLocais').val();

    if (todosLocais.length === idsSelecionados.length) {
        select.selectpicker('val', ["TODOS"]);
    } else {
        select.selectpicker('val', idsSelecionados);
    }

    $('#btnSelecionarViaMapa').text('Selecionar via mapa');
    $('#containerMapaLocais').slideUp();
});

function inicializarMapaBlitz() {
    mapaBlitz = new google.maps.Map(document.getElementById("mapaBlitz"), {
        center: new google.maps.LatLng(-25.432947, -49.270591),
        zoom: 12
    });

    drawingManagerBlitz = new google.maps.drawing.DrawingManager({
        drawingMode: google.maps.drawing.OverlayType.POLYGON,
        drawingControl: false,
        polygonOptions: {
            editable: true
        }
    });

    drawingManagerBlitz.setMap(mapaBlitz);

    google.maps.event.addListener(drawingManagerBlitz, 'overlaycomplete', function (event) {
        if (poligonoBlitz) poligonoBlitz.setMap(null);
        if (circuloBlitz) circuloBlitz.setMap(null);

        poligonoBlitz = event.overlay;
    });

    mapaBlitz.addListener("click", function (event) {
        if ($('input[name="modoSelecaoMapa"]:checked').val() !== 'raio') return;

        if (circuloBlitz) circuloBlitz.setMap(null);

        const raioM = parseFloat($('#raioRadaresMapa').val());

        circuloBlitz = new google.maps.Circle({
            center: event.latLng,
            radius: raioM,
            map: mapaBlitz,
            editable: true
        });

        pontoRaioBlitz = event.latLng;
    });

    $('input[name="modoSelecaoMapa"]').on('change', function () {
        if ($(this).val() === 'poligono') {
            drawingManagerBlitz.setDrawingMode(google.maps.drawing.OverlayType.POLYGON);
        } else {
            drawingManagerBlitz.setDrawingMode(null);
        }

        if (poligonoBlitz) poligonoBlitz.setMap(null);
        if (circuloBlitz) circuloBlitz.setMap(null);
    });
}

function carregarLocaisMapa() {
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/Blitz",
        dataType: "json",
        data: { acao: "listarLocais" },
        success: function (response) {
            markersLocaisBlitz.forEach(m => m.setMap(null));
            markersLocaisBlitz = [];
            
            locaisMapa = response.locais;

            locaisMapa.forEach(function (local) {
                if (!local.posicao_lat || !local.posicao_lon) return;

                const marker = new google.maps.Marker({
                    position: {
                        lat: parseFloat(local.posicao_lat),
                        lng: parseFloat(local.posicao_lon)
                    },
                    map: mapaBlitz,
                    title: local.nome
                });

                marker.id_local = local.id_local + '_' + local.sequencia_local;
                markersLocaisBlitz.push(marker);
            });
        }
    });
}

function carregarRaioRadares() {
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/ConfiguracaoRadares",
        dataType: "xml",
        data: {
            acao: "obterRaioRadaresMapa"
        },
        success: function (data) {
            var raio = $(data).find('raio_radares_mapa').text();
            document.getElementById('raioRadaresMapa').value = raio;
        },
        error: function () {
            console.log("Erro ao carregar raio de radares");
        }
    });
}

function resetarMapaBlitz() {
    if (poligonoBlitz) {
        poligonoBlitz.setMap(null);
        poligonoBlitz = null;
    }

    if (circuloBlitz) {
        circuloBlitz.setMap(null);
        circuloBlitz = null;
    }

    if (mapaBlitz) {
        mapaBlitz.setCenter(new google.maps.LatLng(-25.432947, -49.270591));
        mapaBlitz.setZoom(12);
    }

    $('#mapaBlitz').hide();

    pontoRaioBlitz = null;

    $('#containerMapaLocais').hide();
    $('#btnSelecionarViaMapa').text('Selecionar via mapa');
}
