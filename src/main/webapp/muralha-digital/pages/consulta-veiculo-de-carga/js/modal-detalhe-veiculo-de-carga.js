var DESC_ERROR_CONTAINER_MODAL = "#error_container_modal_detalhe_veiculo";
var VEICULO_POSSUI_PLACA = false;
var PLACA = '';
var ID_LOCAL = -1;
var DATA_HORA_PASSAGEM = '';

var MOBILE = /Mobi/i.test(window.navigator.userAgent);

$(document).ready(function() {
    $('#modalDetalheVeiculo').on('shown.bs.modal', function () {
        // Re-inicializar o carousel quando o modal abrir
        var galeriaImagensVeiculo = document.getElementById("galeriaImagensVeiculo");
        if (galeriaImagensVeiculo) {
            var carousel = new bootstrap.Carousel(galeriaImagensVeiculo, {
            });
        }
    });

    $('#modalDetalheVeiculo').on('hidden.bs.modal', function () {
        $('.modal-backdrop').remove();
        $('body').removeClass('modal-open');
        $('body').removeAttr('style');
    });
});

function AbrirDetalhesVeiculo(idPassagem, idVeiculo) {
    
    limparTodosCamposModalVeiculo();
    
    idVeiculoGlobal = null;
    idVeiculoGlobal = idVeiculo;
  
    const modalDetalhe = bootstrap.Modal.getInstance(document.getElementById('modalDetalheVeiculo'));
    if (modalDetalhe) {
        modalDetalhe.hide();
    }
    
    const novaModalDetalhe = new bootstrap.Modal(document.getElementById('modalDetalheVeiculo'));
    novaModalDetalhe.show();

    ObterVeiculo(idPassagem);
    buscarImagensVeiculo(idVeiculo);
}

function ObterVeiculo(idPassagem)
{	
    var idVeiculoModal = document.getElementById("id_veic");
    if (idVeiculoModal)
        idVeiculoModal.innerHTML = idPassagem;
    
    var dataString = "acao=obterVeiculoPorId" +
                        "&idVeiculo=" + idPassagem;
    console.log(dataString);

    var urlPesquisa = urlRoot + "MuralhaDigital/VeiculoDeCarga";
    
    $.ajax(
    {
        type: 		"GET",
        url: 		urlPesquisa,
        data: 		dataString,
        dataType:	"xml",
        
        success: function( xml, textStatus, jqXHR ) 
        {			
            TratarRetornoVeiculo(jqXHR);
        },
        
        error: function(e, b, error) {
            AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao obter Veículo de Carga!!', DESC_ERROR_CONTAINER_MODAL);
        },
        async: false
    });
}

function TratarRetornoVeiculo(jqXHR)
{
    var xmlDoc = $.parseXML( jqXHR.responseText );
    var $xml = $(xmlDoc);
    
    var veiculo = $xml.find('VeiculoDeCargaDetalhes');

    var placa = veiculo.find('placa').text();
    var marca = veiculo.find('marca').text();
    var modelo = veiculo.find('modelo').text();
    var data = veiculo.find('data').text();
    var serieEquipamento = veiculo.find('serieEquipamento').text();
    var codigoEquipamento = veiculo.find('codigoEquipamento').text();
    var nomeEquipamento = veiculo.find('nome').text();
    var sentido = veiculo.find('sentido').text();
    var equipamento = codigoEquipamento + ' - ' + serieEquipamento + ' - ' + nomeEquipamento;
    var classificacao = veiculo.find('classificacao').text();
    var classificacaoArt96 = veiculo.find('classificacaoArt96').text();
    var faixa = veiculo.find('faixa').text();
    var velocidade = veiculo.find('velocidade').text();
    var comprimento = veiculo.find('comprimento').text();
    var latitude = veiculo.find('latitude').text();
    var longitude = veiculo.find('longitude').text();
    var idLocal = veiculo.find('idLocal').text();
    var comPesagem = veiculo.find('comPesagem').text();
    var comImagem = veiculo.find('comImagem').text();
    
    // Dados de pesagem
    var pbt = veiculo.find('pbt').text();
    var pbtc = veiculo.find('pbtc').text();
    var numeroEixos = veiculo.find('numeroEixos').text();
    
    // Pesos por eixo
    var e1 = veiculo.find('e1').text();
    var e2 = veiculo.find('e2').text();
    var e3 = veiculo.find('e3').text();
    var e4 = veiculo.find('e4').text();
    var e5 = veiculo.find('e5').text();
    var e6 = veiculo.find('e6').text();
    var e7 = veiculo.find('e7').text();
    var e8 = veiculo.find('e8').text();
    var e9 = veiculo.find('e9').text();
    
    // Distâncias entre eixos
    var distanciaE1E2 = veiculo.find('distanciaE1E2').text();
    var distanciaE2E3 = veiculo.find('distanciaE2E3').text();
    var distanciaE3E4 = veiculo.find('distanciaE3E4').text();
    var distanciaE4E5 = veiculo.find('distanciaE4E5').text();
    var distanciaE5E6 = veiculo.find('distanciaE5E6').text();
    var distanciaE6E7 = veiculo.find('distanciaE6E7').text();
    var distanciaE7E8 = veiculo.find('distanciaE7E8').text();
    var distanciaE8E9 = veiculo.find('distanciaE8E9').text();

    let idLocalFormatado = parseInt(idLocal, 10);

    PLACA = placa;
    ID_LOCAL = isNaN(idLocalFormatado) ? -1 : idLocalFormatado;
    DATA_HORA_PASSAGEM = data;

    if (placa != null && placa.trim() != '')
    {
        VEICULO_POSSUI_PLACA = true;
        PLACA = placa;
    }

    PopulaModalDetalheVeiculo(
        equipamento, faixa, sentido, data, placa, velocidade, comprimento, 
        marca, modelo, classificacao, classificacaoArt96, latitude, longitude,
        comPesagem, comImagem, pbt, pbtc, numeroEixos,
        e1, e2, e3, e4, e5, e6, e7, e8, e9,
        distanciaE1E2, distanciaE2E3, distanciaE3E4, distanciaE4E5, 
        distanciaE5E6, distanciaE6E7, distanciaE7E8, distanciaE8E9
    );
}

function PopulaModalDetalheVeiculo(
    equipamento, faixa, sentido, dataFormatada, placa, velocidade, comprimento,
    marca, modelo, classificacao, classificacaoArt96, latitude, longitude,
    comPesagem, comImagem, pbt, pbtc, numeroEixos,
    e1, e2, e3, e4, e5, e6, e7, e8, e9,
    distanciaE1E2, distanciaE2E3, distanciaE3E4, distanciaE4E5,
    distanciaE5E6, distanciaE6E7, distanciaE7E8, distanciaE8E9
)
{
    // Função auxiliar para formatar valores
    function formatarValor(valor, sufixo = '') {
        if (!valor || valor === 'null' || valor.trim() === '') return '-';
        return valor + sufixo;
    }

    function formatarBooleano(valor) {
        if (valor === 'true') return 'Sim';
        if (valor === 'false') return 'Não';
        return valor;
    }

    // Dados básicos
    document.getElementById("equipamentoModal").value = formatarValor(equipamento);
    document.getElementById("pistaModal").value = formatarValor(faixa);
    document.getElementById("sentidoModal").value = formatarValor(sentido);
    document.getElementById("classificacaoModal").value = formatarValor(classificacao);
    document.getElementById("classificacaoArt96Modal").value = formatarValor(classificacaoArt96);
    document.getElementById("dataModal").value = formatarValor(dataFormatada);
    document.getElementById("placaModal").value = formatarValor(placa);
    document.getElementById("velocidadeModal").value = formatarValor(velocidade, ' km/h');
    document.getElementById("comprimentoModal").value = formatarValor(comprimento, ' m');
    document.getElementById("marcaModal").value = formatarValor(marca);
    document.getElementById("modeloModal").value = formatarValor(modelo);
    
    // Coordenadas e status
    document.getElementById("latitudeModal").value = formatarValor(latitude);
    document.getElementById("longitudeModal").value = formatarValor(longitude);
    document.getElementById("comPesagemModal").value = formatarBooleano(comPesagem);
    document.getElementById("comImagemModal").value = formatarBooleano(comImagem);
    
    // Dados de pesagem
    document.getElementById("pbtModal").value = formatarValor(pbt, ' kg');
    document.getElementById("pbtcModal").value = formatarValor(pbtc, ' kg');
    document.getElementById("numeroEixosModal").value = formatarValor(numeroEixos);
    
    // Pesos por eixo
    document.getElementById("e1Modal").value = formatarValor(e1, ' kg');
    document.getElementById("e2Modal").value = formatarValor(e2, ' kg');
    document.getElementById("e3Modal").value = formatarValor(e3, ' kg');
    document.getElementById("e4Modal").value = formatarValor(e4, ' kg');
    document.getElementById("e5Modal").value = formatarValor(e5, ' kg');
    document.getElementById("e6Modal").value = formatarValor(e6, ' kg');
    document.getElementById("e7Modal").value = formatarValor(e7, ' kg');
    document.getElementById("e8Modal").value = formatarValor(e8, ' kg');
    document.getElementById("e9Modal").value = formatarValor(e9, ' kg');
    
    // Distâncias entre eixos
    document.getElementById("distanciaE1E2Modal").value = formatarValor(distanciaE1E2, ' m');
    document.getElementById("distanciaE2E3Modal").value = formatarValor(distanciaE2E3, ' m');
    document.getElementById("distanciaE3E4Modal").value = formatarValor(distanciaE3E4, ' m');
    document.getElementById("distanciaE4E5Modal").value = formatarValor(distanciaE4E5, ' m');
    document.getElementById("distanciaE5E6Modal").value = formatarValor(distanciaE5E6, ' m');
    document.getElementById("distanciaE6E7Modal").value = formatarValor(distanciaE6E7, ' m');
    document.getElementById("distanciaE7E8Modal").value = formatarValor(distanciaE7E8, ' m');
    document.getElementById("distanciaE8E9Modal").value = formatarValor(distanciaE8E9, ' m');
}

function limparTodosCamposModalVeiculo()
{
    PLACA = null;	
    
    var campos = [
        "equipamentoModal", "pistaModal", "sentidoModal", "classificacaoModal", "classificacaoArt96Modal",
        "dataModal", "placaModal", "velocidadeModal", "comprimentoModal", "marcaModal", "modeloModal",
        "latitudeModal", "longitudeModal", "comPesagemModal", "comImagemModal", "pbtModal", "pbtcModal", 
        "numeroEixosModal", "e1Modal", "e2Modal", "e3Modal", "e4Modal", "e5Modal", "e6Modal", "e7Modal", 
        "e8Modal", "e9Modal", "distanciaE1E2Modal", "distanciaE2E3Modal", "distanciaE3E4Modal", 
        "distanciaE4E5Modal", "distanciaE5E6Modal", "distanciaE6E7Modal", "distanciaE7E8Modal", "distanciaE8E9Modal"
    ];
    
    campos.forEach(function(campoId) {
        var campo = document.getElementById(campoId);
        if (campo) campo.value = "";
    });
    
    var galeriaImagensVeiculo = document.getElementById("galeriaImagensVeiculo");
    
    if (galeriaImagensVeiculo)
        galeriaImagensVeiculo.innerHTML = "";
    
    var idVeiculoModal = document.getElementById("id_veic");
    if (idVeiculoModal) idVeiculoModal.innerHTML = "";
}

function processaDados(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var idsVeiculos = []; // Array para armazenar todos os IDs em ordem
	var indexAtual = 0;

	// Primeiro, coletar todos os IDs
	$xml.find('VeiculoDeCarga').each(function()
	{
		var $item = $(this);
		var idVeiculo = $item.find('id').text();
		idsVeiculos.push(idVeiculo);
	});

	// Agora processar cada veículo
	$xml.find('VeiculoDeCarga').each(function(index)
	{
		var $item = $(this);
		
		var idVeiculo = $item.find('id').text();
        var idVeiculoParaImagens = $item.find('idVeiculo').text();
        idVeiculoParaImagens = (idVeiculoParaImagens && idVeiculoParaImagens !== "0") ? idVeiculoParaImagens : "null";
		var placa = $item.find('placa').text();
		var data = $item.find('data').text();
		var nomeEquipamento = $item.find('nome').text();
		var serieEquipamento = $item.find('serieEquipamento').text();
		var equipamento = serieEquipamento + ' - ' + nomeEquipamento;
		var classificacao = $item.find('classificacao').text();
		var faixa = $item.find('faixa').text();
		
		// Campos específicos de veículos de carga
		var pbt = $item.find('pbt').text();
		var pbtc = $item.find('pbtc').text();
		var numeroEixos = $item.find('numeroEixos').text();
		var e1 = $item.find('e1').text();
		var e2 = $item.find('e2').text();
		var e3 = $item.find('e3').text();
		var e4 = $item.find('e4').text();
		var e5 = $item.find('e5').text();
		var distanciaE1E2 = $item.find('distanciaE1E2').text();
		var distanciaE2E3 = $item.find('distanciaE2E3').text();
		var classificacaoArt96 = $item.find('classificacaoArt96').text();
		
		// Formatar data
		var dataFormatada = formatarData(data);
		
		// Formatar pesos por eixo
		var pesosEixos = "";
		if (e1) pesosEixos += "E1: " + e1 + "kg ";
		if (e2) pesosEixos += "E2: " + e2 + "kg ";
		if (e3) pesosEixos += "E3: " + e3 + "kg ";
		if (e4) pesosEixos += "E4: " + e4 + "kg ";
		if (e5) pesosEixos += "E5: " + e5 + "kg ";
		
		// Formatar distâncias entre eixos
		var distanciasEixos = "";
		if (distanciaE1E2) distanciasEixos += "E1-E2: " + distanciaE1E2 + "m ";
		if (distanciaE2E3) distanciasEixos += "E2-E3: " + distanciaE2E3 + "m ";
		
		// Calcular IDs anterior e próximo
		var idVeiculoAnterior = (index > 0) ? idsVeiculos[index - 1] : "";
		var idVeiculoProximo = (index < idsVeiculos.length - 1) ? idsVeiculos[index + 1] : "";
		
		// Botão de detalhes com navegação
		var htmlBotoesAcao = "<div class='d-flex justify-content-center align-items-center gap-1' role='group'>" + 
                    "<button id='btnDetalhar_" + idVeiculo +  "' type='button' class='btn btn-sm btn-primary d-flex align-items-center justify-content-center' data-bs-toggle='modal' data-bs-target='#modalDetalheVeiculo' title='Abrir detalhes do veículo' " +
                    "onclick='AbrirDetalhesVeiculo(\"" + idVeiculo + "\", " + idVeiculoParaImagens + ")' " +
                    "data-id-anterior='" + idVeiculoAnterior + "' " +
                    "data-id-proximo='" + idVeiculoProximo + "' " +
                    "style='width: 32px; height: 32px;'>" +
                    "<svg xmlns='http://www.w3.org/2000/svg' width='14' height='14' fill='currentColor' class='bi bi-search' viewBox='0 0 16 16'>" +
                    "<path d='M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z'></path>" +
                    "</svg>" +
                    "</button>" + 
                    "</div>";

		if ( ! IS_MODO_GRADE_IMAGENS )
		{
			var tableRef = document.getElementById("tabela").getElementsByTagName('tbody')[0];
	
			var row   = tableRef.insertRow(tableRef.rows.length);
			row.insertCell(0).innerHTML = "<small>" + equipamento + "</small>";
			row.insertCell(1).innerHTML = "<small>" + classificacao + "</small>";
			row.insertCell(2).innerHTML = "<small>" + placa + "</small>";
			row.insertCell(3).innerHTML = "<small>" + dataFormatada + "</small>";
			row.insertCell(4).innerHTML = "<small>" + (pbt ? pbt + " kg" : "-") + "</small>";
			row.insertCell(5).innerHTML = "<small>" + (pbtc ? pbtc + " kg" : "-") + "</small>";
			row.insertCell(6).innerHTML = "<small>" + (numeroEixos ? numeroEixos : "-") + "</small>";
			row.insertCell(7).innerHTML = "<small>" + (pesosEixos ? pesosEixos : "-") + "</small>";
			row.insertCell(8).innerHTML = "<small>" + (distanciasEixos ? distanciasEixos : "-") + "</small>";
			row.insertCell(9).innerHTML = htmlBotoesAcao;
			
			$('input#txt_consulta').quicksearch('table#tabela tbody tr');
		}
	});
}

// Funções de navegação atualizadas
function ObterVeiculoAnterior()
{
    var idVeiculoAtual = document.getElementById("id_veic").innerHTML;
    
    var btnAtual = document.getElementById("btnDetalhar_" + idVeiculoAtual);
    
    if (btnAtual) {
        var idAnterior = btnAtual.getAttribute("data-id-anterior");
        
        if (idAnterior && idAnterior !== "") {
            limparTodosCamposModalVeiculo();
            ObterVeiculo(idAnterior);
        } else {
            alert("Este é o primeiro veículo da lista.");
        }
    } else {
        NavegacaoAlternativaAnterior();
    }
}

function ObterVeiculoProximo()
{
    var idVeiculoAtual = document.getElementById("id_veic").innerHTML;
    
    var btnAtual = document.getElementById("btnDetalhar_" + idVeiculoAtual);
    
    if (btnAtual) {
        var idProximo = btnAtual.getAttribute("data-id-proximo");
        
        if (idProximo && idProximo !== "") {
            limparTodosCamposModalVeiculo();
            ObterVeiculo(idProximo);
        } else {
            alert("Este é o último veículo da lista.");
        }
    } else {
        NavegacaoAlternativaProximo();
    }
}

// Funções de navegação alternativa (fallback)
function NavegacaoAlternativaAnterior() {
    var idAtual = document.getElementById("id_veic").innerHTML;
    
    var todosIds = [];
    $('#tabela tbody tr').each(function() {
        var btn = $(this).find('button[id^="btnDetalhar_"]');
        if (btn.length) {
            var id = btn.attr('id').replace('btnDetalhar_', '');
            todosIds.push(id);
        }
    });
    
    
    var posicaoAtual = todosIds.indexOf(idAtual);
    
    if (posicaoAtual > 0) {
        var anteriorId = todosIds[posicaoAtual - 1];
        limparTodosCamposModalVeiculo();
        ObterVeiculo(anteriorId);
    } else {
        alert("Este é o primeiro veículo da lista.");
    }
}

function NavegacaoAlternativaProximo() {
    var idAtual = document.getElementById("id_veic").innerHTML;
    
    var todosIds = [];
    $('#tabela tbody tr').each(function() {
        var btn = $(this).find('button[id^="btnDetalhar_"]');
        if (btn.length) {
            var id = btn.attr('id').replace('btnDetalhar_', '');
            todosIds.push(id);
        }
    });
    
    
    var posicaoAtual = todosIds.indexOf(idAtual);
    
    if (posicaoAtual !== -1 && posicaoAtual < todosIds.length - 1) {
        var proximoId = todosIds[posicaoAtual + 1];
        limparTodosCamposModalVeiculo();
        ObterVeiculo(proximoId);
    } else {
        alert("Este é o último veículo da lista.");
    }
}

function buscarImagensVeiculo(idVeiculo) {
    if (!(idVeiculo > 0)) {
        criarGaleriaImagens([]);
        return;
    }
    
    var dataString = "id_veiculo=" + idVeiculo;
    var urlPesquisa = "/ajax/InfoVeiculoImagem";
    
    $.ajax({
        type: "GET",
        url: urlPesquisa,
        data: dataString,
        dataType: "xml",
        success: function(xml, textStatus, jqXHR) {
            try {
                var contaImagem = $(xml).find('CONTA_IMAGEM').text();
                var idsImagens = [];
                
                console.log("Total de imagens encontradas:", contaImagem);
                
                for (var i = 0; i < parseInt(contaImagem); i++) {
                    var idImagem = $(xml).find('ID_IMAGEM_' + i).text();
                    if (idImagem && idImagem !== "" && idImagem !== "0") {
                        idsImagens.push(idImagem);
                    }
                }
                
                if (idsImagens.length > 0) {
                    criarGaleriaImagens(idsImagens);
                } else {
                    criarGaleriaImagens([]);
                }
            } catch(e) {
                console.log("Erro ao processar imagens do veículo: " + e);
                criarGaleriaImagens([]);
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("Erro ao obter imagens do veículo. Status:", jqXHR.status, "Response:", jqXHR.responseText);
            criarGaleriaImagens([]);
        }
    });
}

function criarGaleriaImagens(idsImagens) {
    var galeriaImagensVeiculo = document.getElementById("galeriaImagensVeiculo");
    var controleGaleriaPrev = document.getElementById("controleGaleriaPrev");
    var controleGaleriaNext = document.getElementById("controleGaleriaNext");
    
    var inicioGaleriaImg = "<div class='carousel-inner'>";
    var fimGaleriaImg = "</div>";
                
    var imagensGaleria = "";

    if (idsImagens && idsImagens.length > 0) {
        idsImagens.forEach(function(idImagem, index) {
            var url = "/ajax/ImgVeiculo?id_imagem=" + idImagem;
            
            var itemAtivo = (index === 0 ? "active" : "");
            
            imagensGaleria = imagensGaleria +
                "<div class='carousel-item " + itemAtivo + "'>" +
                    "<img src='" + url + "' class='d-block w-100 img-responsive' alt='Imagem do veículo' " +
                    "onerror=\"this.src='/muralha-digital/assets/images/consilux_grande_transparent.png'\">" +
                "</div>";
        });
    }
    
    // Se não tiver imagens, mostrar o logo
    if (imagensGaleria === "") {
        imagensGaleria =
            "<div class='carousel-item active'>" +
                "<img src='/muralha-digital/assets/images/consilux_grande_transparent.png' class='d-block w-100' alt='Imagem não disponível'>" +
            "</div>";
    }

    galeriaImagensVeiculo.innerHTML = inicioGaleriaImg + imagensGaleria + fimGaleriaImg;
    
    if (idsImagens && idsImagens.length > 1 && !MOBILE) {
        // Tem múltiplas imagens - mostrar setas
        if (controleGaleriaPrev) {
            controleGaleriaPrev.innerHTML =
                    "<div style='height:20px;width:20px' class='align-items-center justify-content-center'> " +
                        "<a style='color: #000099;' class='seta-troca-imagem d-flex align-items-center justify-content-center' type='button' data-bs-target='#galeriaImagensVeiculo' data-bs-slide='prev'> " +
                            "<svg aria-hidden='true' focusable='false' data-prefix='fas' data-icon='chevron-left' role='img' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 320 512' class='svg-inline--fa fa-chevron-left fa-w-10 fa-3x'><path fill='currentColor' d='M34.52 239.03L228.87 44.69c9.37-9.37 24.57-9.37 33.94 0l22.67 22.67c9.36 9.36 9.37 24.52.04 33.9L131.49 256l154.02 154.75c9.34 9.38 9.32 24.54-.04 33.9l-22.67 22.67c-9.37 9.37-24.57 9.37-33.94 0L34.52 272.97c-9.37-9.37-9.37-24.57 0-33.94z' class=''></path></svg> " +
                        "</a> " +
                    "</div>";
        }
                
        if (controleGaleriaNext) {
            controleGaleriaNext.innerHTML =
                    "<div style='height:20px;width:20px' class='align-items-center justify-content-center'> " +
                        "<a style='color: #000099;' class='seta-troca-imagem d-flex align-items-center justify-content-center' type='button' data-bs-target='#galeriaImagensVeiculo' data-bs-slide='next'> " +
                            "<svg aria-hidden='true' focusable='false' data-prefix='fas' data-icon='chevron-right' role='img' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 320 512' class='svg-inline--fa fa-chevron-right fa-w-10 fa-3x'><path fill='currentColor' d='M285.476 272.971L91.132 467.314c-9.373 9.373-24.569 9.373-33.941 0l-22.667-22.667c-9.357-9.357-9.375-24.522-.04-33.901L188.505 256 34.484 101.255c-9.335-9.379-9.317-24.544.04-33.901l22.667-22.667c9.373-9.373 24.569-9.373 33.941 0L285.475 239.03c9.373 9.372 9.373 24.568.001 33.941z' class=''></path></svg> " +
                        "</a> " +
                    "</div>";
        }
        
        var carousel = new bootstrap.Carousel(galeriaImagensVeiculo, {});
    } else {
        // Só tem uma imagem ou nenhuma - esconder setas
        if (controleGaleriaPrev) {
            controleGaleriaPrev.innerHTML = "";
        }
        if (controleGaleriaNext) {
            controleGaleriaNext.innerHTML = "";
        }
    }
}