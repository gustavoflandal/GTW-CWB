var socketBlitzDigital = null;
var init = false;
var cardsVeiculos = [];
var blitzSelecionada = null;
var IMG_OBJETIVA = 0;
var IMG_PANORAMICA = 1;
var exibirAcoes = false;
var filtroSomenteIrregulares = false;
var TIPOS_ALERTA_MOCK = [
    'Veículo Roubado',
    'Veículo Furtado',
    'Veículo Clonado',
    'Transporte Clandestino',
    'Veículo Suspeito de Sequestro Relâmpago',
    'Veículo Suspeito de Roubo à Banco'
];
var filtroSomenteInteresse = false;
var veiculosInteresse = [];
var passagensRelacionadas = [];
var indicePassagemAtual = 0;
var idsLocaisBlitzSelecionada = [];

var urlWebSocket = location.origin.replace(/^http/, 'ws') + "/ClientesWebSocket";

$(document).ready(function () {
    ObterListaBlitz();

    $('#btnVoltar').on('click', function () {
        if (document.referrer) {
            window.history.back();
        } else {
            window.location.href = '/login/muralha_principal.jsp';
        }
    });
});

$('#filtroIrregulares').on('change', function () {
    filtroSomenteIrregulares = true;
    filtroSomenteInteresse = false;
    atualizarCardsTela();
});

$('#filtroInteresse').on('change', function () {
    filtroSomenteInteresse = true;
    filtroSomenteIrregulares = false;
    atualizarCardsTela();
});

$('#filtroTodos').on('change', function () {
    filtroSomenteIrregulares = false;
    filtroSomenteInteresse = false;
    atualizarCardsTela();
});

function ObterListaBlitz() {

    var select = document.getElementById('selectBlitz');
    select.innerHTML = '<option value="">Carregando blitz...</option>';
    select.disabled = true;

    $.ajax({
        type: "GET",
        url: urlRoot + "MuralhaDigital/Blitz",
        data: { acao: 'listarBlitzAtivasAutomaticas' },
        dataType: "json",

        success: function (data) {
            ProcessaDadosBlitz(data);
        },

        error: function (e) {
            AlertCsx_E_TimeOut_8000ms("Erro ao carregar lista de blitz digitais!");
            select.innerHTML = '<option value="">Erro ao carregar</option>';
            select.disabled = true;
        }
    });
}

function ProcessaDadosBlitz(data) {
    var select = document.getElementById('selectBlitz');
    select.innerHTML = '';

    if (data && data.blitzes && data.blitzes.length > 0) {
        data.blitzes.forEach(function (blitz) {
            var option = document.createElement('option');
            option.value = blitz.id;
            option.text = blitz.nome_blitz;
            select.appendChild(option);
        });

        select.disabled = false;
        select.selectedIndex = 0;
        $(select).trigger('change');

    } else {
        select.innerHTML = '<option value="">Nenhuma blitz ativa</option>';
        select.disabled = true;
    }
}

$('#selectBlitz').on('change', function () {
    var id = $(this).val();
    blitzSelecionada = id ? { id: parseInt(id) } : null;

    cardsVeiculos = [];
    veiculosInteresse = [];
    
    if (blitzSelecionada) {
        carregarLocaisBlitz(blitzSelecionada.id);
    }
    
    atualizarCardsTela();
});

function DivInit_WebSocketInit() {

    if (!blitzSelecionada) {
        AlertCsx_E_TimeOut_8000ms("Selecione uma blitz");
        return;
    }

    if (init) return;

    init = true;
    exibirAcoes = false;

    abrirWebSocket();
    atualizarBotaoIniciar();
    atualizarCardsTela();
}

function pararWebSocket() {

    init = false;
    exibirAcoes = true;

    if (socketBlitzDigital) {
        socketBlitzDigital.close();
        socketBlitzDigital = null;
    }

    atualizarBotaoParar();
    atualizarCardsTela();
}

function abrirWebSocket() {

    socketBlitzDigital = new WebSocket(urlWebSocket);

    socketBlitzDigital.onopen = function () {
        socketBlitzDigital.send(
            "INIT_WS_BLITZ-DIGITAL-USUARIOID-" + usuarioID
        );
    };

    socketBlitzDigital.onmessage = function (event) {

        if (!init) return;

        try {
            var payload = JSON.parse(event.data);
            processarPassagens(payload);
        } catch (e) {
            console.error("Erro JSON", e);
        }
    };

    socketBlitzDigital.onclose = function () {
        socketBlitzDigital = null;
    };
}

function processarPassagens(payload) {

    if (!payload || !payload.passagens) return;

    payload.passagens.forEach(function (passagem) {

        var blitzes = Array.isArray(passagem.blitzes)
            ? passagem.blitzes
            : passagem.blitzes.toString().split(',').map(Number);

        if (!blitzes.includes(blitzSelecionada.id)) return;

        if (existePassagem(passagem.idVeiculoTempoReal)) return;

        cardsVeiculos.unshift(passagem);

        if (cardsVeiculos.length > 50) {
            cardsVeiculos = cardsVeiculos.slice(0, 50);
        }
    });

    cardsVeiculos = cardsVeiculos.filter((v, index, self) =>
        index === self.findIndex(x => x.idVeiculoTempoReal === v.idVeiculoTempoReal)
    );

    atualizarCardsTela();
}

function existePassagem(id) {
    return cardsVeiculos.some(v => v.idVeiculoTempoReal === id);
}

function atualizarCardsTela() {
    var container = $("#cardsVeiculos");
    container.empty();

    var listaFinal;

    if (filtroSomenteInteresse) {
        // Usar diretamente o array de interesse
        listaFinal = veiculosInteresse.slice().sort((a, b) => new Date(b.data) - new Date(a.data));
    } else {
        var listaOrdenada = cardsVeiculos
            .slice()
            .sort((a, b) => new Date(b.data) - new Date(a.data));
            
        if (filtroSomenteIrregulares) {
            listaFinal = listaOrdenada.filter(isIrregular).slice(0, 12);
        } else {
            listaFinal = listaOrdenada.slice(0, 12);
        }
    }

    if (listaFinal.length === 0) {
        container.append(`
            <div class="col-12 text-center mt-4">
                <h5>Nenhuma passagem encontrada</h5>
            </div>
        `);
        return;
    }

    listaFinal.forEach(function (v) {
        container.append(montarCardVeiculo(v));
    });
}

function montarCardVeiculo(v) {
    var irregular = isIrregular(v);
    var corAlerta = irregular ? obterCorPorTipoAlerta(v.tipo_alerta) : null;
    var isInteresse = veiculosInteresse.some(item => item.idVeiculoTempoReal === v.idVeiculoTempoReal);

    var data = new Date(v.data).toLocaleString("pt-BR");

    var botaoInteresse = `
        <button class="btn ${isInteresse ? 'btn-warning' : 'btn-outline-warning'} btn-sm btn-interesse"
                onclick="event.stopPropagation(); toggleInteresse('${v.idVeiculoTempoReal}')"
                title="Veículo de interesse">
            <i class="bi ${isInteresse ? 'bi-star-fill' : 'bi-star'}"></i>
        </button>
    `;

    var botoesAcao = exibirAcoes ? `
        <div class="blitz-acoes">
            <button class="btn btn-primary btn-sm"
                    onclick="event.stopPropagation(); criarAbordagem('${v.idVeiculoTempoReal}')">
                Realizar Abordagem
            </button>

            ${irregular ? `
                <button class="btn btn-danger btn-sm"
                        onclick="event.stopPropagation(); abrirAlerta('${v.id_alerta}')">
                    Ver Alerta
                </button>
            ` : ''}
        </div>
    ` : '';

    return `
        <div class="col-12 col-sm-6 col-lg-3">
            <div class="blitz-card ${irregular ? 'blitz-card-irregular toast-' + corAlerta : ''}" 
                 data-id="${v.idVeiculoTempoReal}"
                 onclick="abrirModalPassagens('${v.idVeiculoTempoReal}')">
                <div class="blitz-card-header">
                    <span class="blitz-placa">${v.placa || 'SEM PLACA'}</span>
                    <span class="blitz-data">${data}</span>
                </div>

                <div class="blitz-info">
                    <span>Velocidade: <strong>${v.velocidade} km/h</strong></span>
                    ${botaoInteresse}
                </div>

                ${irregular ? `
                    <div class="blitz-alerta-info">
                        <strong>${v.tipo_alerta}</strong>
                        ${v.observacao_alerta ? '<br>' + v.observacao_alerta : ''}
                    </div>
                ` : ''}

                <img class="blitz-imagem"
                     src="${montarUrlImagem(v.idVeiculoTempoReal, IMG_OBJETIVA)}"
                     alt="${v.idVeiculoTempoReal}">

                ${botoesAcao}
            </div>
        </div>
    `;
}

function montarUrlImagem(idVeiculo, tipoImagem) {
    return urlRoot +
        "MuralhaDigital/Veiculo/Imagem?" +
        "acao=ImagemByIdVeic" +
        "&idVeic=" + idVeiculo +
        "&tpImagem=" + tipoImagem;
}

function atualizarBotaoIniciar() {
    $("#iniciar")
        .text("PARAR")
        .removeClass("btn-success")
        .addClass("btn-warning")
        .attr("onclick", "pararWebSocket()");
}

function atualizarBotaoParar() {
    $("#iniciar")
        .text("INICIAR")
        .removeClass("btn-warning")
        .addClass("btn-success")
        .attr("onclick", "DivInit_WebSocketInit()");
}

window.addEventListener("beforeunload", function () {
    if (socketBlitzDigital) socketBlitzDigital.close();
});

function criarAbordagem(idVeiculo) {

    if (!blitzSelecionada) return;

    var url =
        urlRoot +
        "muralha-digital/pages/blitz-abordagem/criar-abordagem.jsp" +
        "?idVeiculoTempoReal=" + idVeiculo +
        "&idBlitz=" + blitzSelecionada.id;

    window.open(url, "_blank");
}

// --------------------- Geração de mocks ---------------------

function simularMensagemWebSocket() {
    if (!init) {
        console.warn("Inicie o monitoramento antes de simular");
        return;
    }

    var passagens = [];

    for (var i = 0; i < 10; i++) {

        var irregular = i < 6;
        var tipo_alerta = irregular ? TIPOS_ALERTA_MOCK[i % TIPOS_ALERTA_MOCK.length] : null;

        passagens.push({
            idVeiculoTempoReal: crypto.randomUUID(),
            placa: gerarPlacaRandom(),
            data: Date.now() - (i * 3000),
            idLocal: 102,
            idPista: 1,
            velocidade: Math.floor(Math.random() * 60) + 10,
            classificacao: "P",
            blitzes: "2,7",

            id_alerta: irregular ? crypto.randomUUID() : null,
            tipo_alerta: tipo_alerta,
            observacao_alerta: irregular ? "Alerta identificado automaticamente" : null
        });
    }

    processarPassagens({ passagens });
}

function simularMensagemWebSocketRandom() {
    if (!init) {
        console.warn("Inicie o monitoramento antes de simular");
        return;
    }

    var passagens = [];
    var total = Math.floor(Math.random() * 8) + 8;
    var percentualIrregular = Math.floor(Math.random() * 50) + 10;
    var qtdIrregulares = Math.round((total * percentualIrregular) / 100);

    for (var i = 0; i < total; i++) {

        var irregular = i < qtdIrregulares;
        var tipo_alerta = irregular
            ? TIPOS_ALERTA_MOCK[Math.floor(Math.random() * TIPOS_ALERTA_MOCK.length)]
            : null;

        passagens.push({
            idVeiculoTempoReal: crypto.randomUUID(),
            placa: gerarPlacaRandom(),
            data: Date.now() - Math.floor(Math.random() * 30000),
            idLocal: 102,
            idPista: Math.floor(Math.random() * 3) + 1,
            velocidade: Math.floor(Math.random() * 60) + 10,
            classificacao: "P",
            blitzes: "2,7",

            id_alerta: irregular ? crypto.randomUUID() : null,
            tipo_alerta: tipo_alerta,
            observacao_alerta: irregular ? "Ocorrência associada ao alerta" : null
        });
    }

    processarPassagens({ passagens });
}

function gerarPlacaRandom() {
    var letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    var numeros = "0123456789";

    return (
        letras.charAt(Math.floor(Math.random() * letras.length)) +
        letras.charAt(Math.floor(Math.random() * letras.length)) +
        letras.charAt(Math.floor(Math.random() * letras.length)) +
        numeros.charAt(Math.floor(Math.random() * numeros.length)) +
        numeros.charAt(Math.floor(Math.random() * numeros.length)) +
        letras.charAt(Math.floor(Math.random() * letras.length)) +
        numeros.charAt(Math.floor(Math.random() * numeros.length))
    );
}

function isIrregular(passagem) {
    return passagem.tipo_alerta != null && passagem.tipo_alerta !== '';
}

function abrirAlerta(idAlerta) {
    if (!idAlerta) return;

    var url =
        "/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=" +
        idAlerta;

    window.open(url, "_blank");
}

function toggleInteresse(idVeiculoTempoReal) {
    // Primeiro verificar se já existe no array de interesse
    var index = veiculosInteresse.findIndex(v => v.idVeiculoTempoReal === idVeiculoTempoReal);
    
    if (index === -1) {
        // Se não existe, tentar encontrar no cardsVeiculos
        var veiculo = cardsVeiculos.find(v => v.idVeiculoTempoReal === idVeiculoTempoReal);
        
        if (veiculo) {
            // Se encontrou no buffer, adicionar cópia
            veiculosInteresse.push({...veiculo});
        }
    } else {
        // Se já existe, remover independente de estar no buffer ou não
        veiculosInteresse.splice(index, 1);
    }
    
    // Atualizar a tela baseado no filtro atual
    atualizarCardsTela();
}

function carregarLocaisBlitz(idBlitz) {
    $.ajax({
        type: "GET",
        url: urlRoot + "MuralhaDigital/Blitz",
        data: { 
            acao: 'listarIdsLocaisBlitz',
            idBlitz: idBlitz
        },
        dataType: "json",
        success: function(data) {
            if (data && data.idsLocais && data.idsLocais.length > 0) {
                idsLocaisBlitzSelecionada = data.idsLocais;
            } else {
                idsLocaisBlitzSelecionada = [];
            }
        },
        error: function(e) {
            console.error("Erro ao carregar IDs dos locais da blitz:", e);
            idsLocaisBlitzSelecionada = [];
        }
    });
}

function abrirModalPassagens(idVeiculo) {
    $("body").addClass("loading");

    // Verificar se é veículo de interesse
    var isInteresse = veiculosInteresse.some(v => v.idVeiculoTempoReal === idVeiculo);
    
    if (!isInteresse) {
        return; // Não abre o modal se não for de interesse
    }
    
    $.ajax({
        type: "GET",
        url: urlRoot + "MuralhaDigital/Veiculo",
        data: { 
            acao: 'obterPassagensRelacionadas',
            idVeiculo: idVeiculo,
            idsLocais: idsLocaisBlitzSelecionada.join(','),
            quantidade: 10
        },
        dataType: "xml",
        success: function(xml) {
            processarRespostaPassagensRelacionadas(xml, idVeiculo);
            $("body").removeClass("loading");
        },
        error: function(e) {
            console.error("Erro ao carregar passagens relacionadas:", e);
            AlertCsx_E_TimeOut_8000ms("Erro ao carregar passagens relacionadas!");
            $("body").removeClass("loading");
        }
    });
}

function processarRespostaPassagensRelacionadas(xml, idVeiculoAtual) {
    passagensRelacionadas = [];
    
    // 1. Pegar todas as anteriores
    var anteriores = [];
    $(xml).find('PassagensAnteriores Passagem').each(function() {
        anteriores.push(extrairVeiculoDoXML($(this)));
    });
    
    // 2. Pegar a passagem atual
    var passagemAtual = null;
    var atualElement = $(xml).find('passagemAtual');
    if (atualElement.length > 0) {
        passagemAtual = extrairVeiculoDoXML(atualElement);
    }
    
    // 3. Pegar todas as posteriores
    var posteriores = [];
    $(xml).find('PassagensPosteriores Passagem').each(function() {
        posteriores.push(extrairVeiculoDoXML($(this)));
    });
    
    // 4. ORDENAR as anteriores por data (crescente - mais antiga primeiro)
    anteriores.sort(function(a, b) {
        return new Date(a.data) - new Date(b.data);
    });
    
    // 5. ORDENAR as posteriores por data (crescente - mais antiga primeiro)
    posteriores.sort(function(a, b) {
        return new Date(a.data) - new Date(b.data);
    });
    
    // 6. Montar array na ordem: ANTERIORES + ATUAL + POSTERIORES
    passagensRelacionadas = [
        ...anteriores,      // anteriores ordenadas (mais antiga -> mais recente)
        passagemAtual,      // a atual no MEIO
        ...posteriores      // posteriores ordenadas (mais antiga -> mais recente)
    ];
    
    // 7. Índice da atual = quantidade de anteriores
    indicePassagemAtual = anteriores.length;

    atualizarModalPassagem();
    $('#modalPassagensRelacionadas').modal('show');
}

function extrairVeiculoDoXML(elemento) {
    var veiculo = {
        id: elemento.find('id').text(),
        placa: elemento.find('placa').text(),
        data: elemento.find('dataVeic').text(),
        dataVeicFormatada: elemento.find('dataVeicFormatada').text(),
        idLocal: elemento.find('idLocal').text(),
        descLocal: elemento.find('descLocal').text(),
        serieEquipamento: elemento.find('serieEquipamento').text(),
        codigoEquipamento: elemento.find('codigoEquipamento').text(),
        idPista: elemento.find('idPista').text(),
        faixa: elemento.find('faixa').text(),
        latitude: elemento.find('latitude').text(),
        longitude: elemento.find('longitude').text(),
        velocidade: elemento.find('velocidade').text(),
        classificacao: elemento.find('classificacao').text(),
        tipoVeiculo: elemento.find('tipoVeiculo').text(),
        comImagem: elemento.find('comImagem').text() === 'true',
        possuiAlerta: elemento.find('possuiAlerta').text() === 'true',
        marca: elemento.find('marca').text(),
        modelo: elemento.find('modelo').text(),
        tipo_alerta: elemento.find('tipo_alerta').text(),
        observacao_alerta: elemento.find('observacao_alerta').text(),
    };

    var irregular = isIrregular(veiculo);
    var corAlerta = irregular ? obterCorPorTipoAlerta(veiculo.tipo_alerta) : null;
    veiculo = { ...veiculo, irregular, cor: corAlerta };
    
    return veiculo;
}

function atualizarModalPassagem() {
    if (passagensRelacionadas.length === 0) return;
    
    var v = passagensRelacionadas[indicePassagemAtual];
    
    $('#placaPassagemAtual').text(v.placa || 'SEM PLACA');
    $('#indicePassagem').text((indicePassagemAtual + 1) + ' / ' + passagensRelacionadas.length);
    
    $('#btnAnterior').prop('disabled', indicePassagemAtual === 0);
    $('#btnProximo').prop('disabled', indicePassagemAtual === passagensRelacionadas.length - 1);
    
    var urlImagem = v.comImagem ? montarUrlImagem(v.id, IMG_OBJETIVA) : '/muralha-digital/assets/img/sem-imagem.png';
    $('#imagemPassagem').attr('src', urlImagem);
    
    $('#infoData').text(v.dataVeicFormatada || '');
    $('#infoLocal').text(v.descLocal || '');
    $('#infoEquipamento').text(v.codigoEquipamento || v.serieEquipamento || '');
    $('#infoFaixa').text(v.faixa || '');
    $('#infoVelocidade').text(v.velocidade ? v.velocidade + ' km/h' : '');
    $('#infoClassificacao').text(v.classificacao || '');
    $('#infoPlacaModal').text(v.placa || 'SEM PLACA');
    
    var possuiAlerta = v.possuiAlerta === true || v.tipo_alerta != null && v.tipo_alerta !== '';
    var corAlerta = possuiAlerta ? obterCorPorTipoAlerta(v.tipo_alerta) : null;
    
    var modalBody = $('.modal-body');
    var alertaDiv = $('#alertaPassagem');
    
    if (alertaDiv.length === 0) {
        var infoRow = $('#conteudoPassagemAtual');
        var cardBody = infoRow.find('.card-body');
        
        if (possuiAlerta) {
            var alertaHtml = '<div id="alertaPassagem" class="alert alert-warning mt-2 mb-0" style="border-left: 4px solid ' + corAlerta + ';">' +
                                '<strong>' + (v.tipo_alerta || 'Alerta') + '</strong>' +
                                (v.observacao_alerta ? '<br>' + v.observacao_alerta : '') +
                             '</div>';
            cardBody.append(alertaHtml);
        }
    } else {
        if (possuiAlerta) {
            alertaDiv.show();
            alertaDiv.html('<strong>' + (v.tipo_alerta || 'Alerta') + '</strong>' +
                          (v.observacao_alerta ? '<br>' + v.observacao_alerta : ''));
            alertaDiv.css('border-left-color', corAlerta);
        } else {
            alertaDiv.hide();
        }
    }
    
    if (possuiAlerta) {
        var cardBody = $('#conteudoPassagemAtual').find('.card-body');
        if (!cardBody.hasClass('border-alerta')) {
            cardBody.addClass('border-alerta');
            cardBody.css('border', '2px solid ' + corAlerta);
            cardBody.css('border-radius', '4px');
        }
        cardBody.css('border-color', corAlerta);
    } else {
        var cardBody = $('#conteudoPassagemAtual').find('.card-body');
        cardBody.removeClass('border-alerta');
        cardBody.css('border', '');
    }
    
    $('#modalPassagensRelacionadas').data('idVeiculoAtual', v.id);
}

function navegarPassagem(direcao) {
    if (direcao === 'anterior' && indicePassagemAtual > 0) {
        indicePassagemAtual--;
    } else if (direcao === 'proximo' && indicePassagemAtual < passagensRelacionadas.length - 1) {
        indicePassagemAtual++;
    }
    atualizarModalPassagem();
}

function criarAbordagemDoModal() {
    var idVeiculo = $('#modalPassagensRelacionadas').data('idVeiculoAtual');
    if (idVeiculo && blitzSelecionada) {
        var url = urlRoot + "muralha-digital/pages/blitz-abordagem/criar-abordagem.jsp" +
                  "?idVeiculoTempoReal=" + idVeiculo +
                  "&idBlitz=" + blitzSelecionada.id;
        window.open(url, "_blank");
    }
}

function simularPassagensReais() {
    if (!init) {
        console.warn("Inicie o monitoramento antes de simular");
        return;
    }

    if (!blitzSelecionada) {
        AlertCsx_E_TimeOut_8000ms("Selecione uma blitz");
        return;
    }

    // Pega a data mais antiga atualmente nos cards como referência inicial
    var dataReferencia = new Date();
    if (cardsVeiculos.length > 0) {
        var datasCards = cardsVeiculos.map(v => new Date(v.data));
        var dataMaisAntiga = new Date(Math.min.apply(null, datasCards));
        dataReferencia = dataMaisAntiga;
    }

    $.ajax({
        type: "GET",
        url: urlRoot + "MuralhaDigital/Blitz",
        data: { 
            acao: 'obterPassagensReaisBlitz',
            idBlitz: blitzSelecionada.id,
            dataReferencia: dataReferencia.toISOString()
        },
        dataType: "json",
        success: function(data) {
            if (data && data.passagens && data.passagens.length > 0) {
                processarPassagensReais(data.passagens);
            } else {
                AlertCsx_E_TimeOut_8000ms("Nenhuma passagem real encontrada para este período");
            }
        },
        error: function(e) {
            console.error("Erro ao simular passagens reais:", e);
            AlertCsx_E_TimeOut_8000ms("Erro ao buscar passagens reais!");
        }
    });
}

function processarPassagensReais(passagens) {
    if (!passagens || passagens.length === 0) return;

    passagens.forEach(function(passagem) {
        if (existePassagem(passagem.idVeiculoTempoReal)) return;

        cardsVeiculos.unshift(passagem);

        if (cardsVeiculos.length > 50) {
            cardsVeiculos = cardsVeiculos.slice(0, 50);
        }
    });

    cardsVeiculos = cardsVeiculos.filter((v, index, self) =>
        index === self.findIndex(x => x.idVeiculoTempoReal === v.idVeiculoTempoReal)
    );

    atualizarCardsTela();
}