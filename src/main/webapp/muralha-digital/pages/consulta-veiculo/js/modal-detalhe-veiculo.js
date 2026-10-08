// Carregar AssinaturaImagemClient para assinatura digital
if (typeof AssinaturaImagemClient === 'undefined') {
    const script = document.createElement('script');
    script.src = '/muralha-digital/pages/assinatura/js/client.js';
    document.head.appendChild(script);
}

var DESC_ERROR_CONTAINER_MODAL = "#error_container_modal_detalhe_veiculo";
dados_orig = '';
var VEICULO_POSSUI_PLACA = false;
var PLACA = '';
var LATITUDE = '';
var LONGITUDE = '';
var NOME_USUARIO = "Usuário";
var ID_USUARIO = -1;
var ID_LOCAL = -1;
var DATA_HORA_PASSAGEM = '';

let scale = 1;
let brightness = 100;
let contrast = 100;
let idVeiculoGlobal = null;

function applyImageAdjustments() {
    // Seleciona apenas a imagem dentro do item ativo do carrossel
    const activeImage = $('#galeriaImagensVeiculo .carousel-item.active img');
    if (activeImage.length) {
        activeImage.css({
            'filter': `brightness(${brightness}%) contrast(${contrast}%)`,
            'transform': `scale(${scale})`,
            'transition': 'transform 0.2s ease-out', // Adiciona uma transição suave para o zoom
            'cursor': 'grab'
        });
    }
}

function exportarImagem() {
    const adicionarTextoCopyright = true;
    const adicionarLogoMarca = true;
    
    const imagemAtiva = $('#galeriaImagensVeiculo .carousel-item.active img');
    
    if (!imagemAtiva.length) {
        AlertModalCsx_E_TimeOut_8000ms_v2('Nenhuma imagem disponível para exportar!', DESC_ERROR_CONTAINER_MODAL);
        return;
    }

    const urlImagem = imagemAtiva.attr('src');
    const placa = document.getElementById("placaModal").value || 'veiculo';
    const data = document.getElementById("dataModal").value || new Date().toLocaleDateString('pt-BR');
    
    // Criar data/hora atual formatada UMA ÚNICA VEZ para usar na imagem e no banco
    const dataAtual = new Date();
    const dataFormatada = dataAtual.toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
    
    const nomeArquivo = `veiculo_${placa}_${data.replace(/\//g, '-')}.jpg`;

    // Registrar a exportação no banco de dados com a MESMA data/hora que será usada na imagem
    RegistrarExportacaoImagem(dataFormatada);

    const imagem = new Image();
    imagem.crossOrigin = 'Anonymous';
    imagem.src = urlImagem;
    
    imagem.onload = function() {
        const canvas = document.createElement('canvas');
        const contexto = canvas.getContext('2d');
        
        canvas.width = imagem.width;
        canvas.height = imagem.height;
        
        contexto.drawImage(imagem, 0, 0, imagem.width, imagem.height);
        
        contexto.font = 'bold 20px Arial';
        contexto.fillStyle = 'rgba(255, 255, 255, 0.8)';
        contexto.strokeStyle = 'black';
        contexto.lineWidth = 1;
        
        // Texto de coordenadas
        if (LATITUDE && LONGITUDE && LATITUDE !== 'N/D' && LONGITUDE !== 'N/D') {
            const textoCoordenadas = `Lat: ${LATITUDE} | Long: ${LONGITUDE}`;
            const medidaTexto = contexto.measureText(textoCoordenadas);
            const x = 20;
            const y = 40;
            const larguraFundo = medidaTexto.width + 20;
            const alturaFundo = 80;
            
            contexto.fillRect(x - 10, y - 25, larguraFundo, alturaFundo);
            
            contexto.fillStyle = 'black';
            contexto.fillText(textoCoordenadas, x, y);
            
            // Texto do usuário
            contexto.font = '16px Arial';
            const textoUsuario = `Exportado por: ${NOME_USUARIO}`;
            contexto.fillText(textoUsuario, x, y + 25);
            
            // Texto da data - USA A MESMA dataFormatada do banco
            const textoData = `Exportado em: ${dataFormatada}`;
            contexto.fillText(textoData, x, y + 50);
        }
        
        if (adicionarTextoCopyright) {
            contexto.font = '16px Arial';
            contexto.fillStyle = 'rgba(0, 0, 0, 0.4)';
            const textoCopyright = 'Anel de Segurança © ' + new Date().getFullYear();
            contexto.fillText(textoCopyright, 30, canvas.height - 30);
        }
        
        if (adicionarLogoMarca) {
            const logo = new Image();
            logo.src = '/muralha-digital/assets/images/consilux_medio_transparent_ipatinga_mg.png';
            logo.onload = function() {
                contexto.globalAlpha = 0.3;
                const larguraLogo = 120;
                const alturaLogo = 60;
                const margem = 20;
                contexto.drawImage(logo, canvas.width - larguraLogo - margem, canvas.height - alturaLogo - margem, larguraLogo, alturaLogo);
                contexto.globalAlpha = 1.0;
                
                finalizarExportacao(canvas, nomeArquivo);
            };
            
            logo.onerror = function() {
                finalizarExportacao(canvas, nomeArquivo);
            };
        } else {
            finalizarExportacao(canvas, nomeArquivo);
        }
    };
    
    imagem.onerror = function() {
        const link = document.createElement('a');
        link.href = urlImagem;
        link.download = nomeArquivo;
        link.target = '_blank';
        
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };
}

function finalizarExportacao(canvas, nomeArquivo) {
    canvas.toBlob(async function(blob) {
        try {
            const formData = new FormData();
            formData.append('imagem', new File([blob], nomeArquivo, { type: 'image/jpeg' }));
            
            const assinaturaClient = new AssinaturaImagemClient();
            const htmlResponse = await assinaturaClient.enviarRequisicao('/muralha-digital/assinatura/assinar', formData);
            const resultado = assinaturaClient.processarRespostaServlet(htmlResponse);
            
            if (resultado.status === 'sucesso' && resultado.imagemAssinada) {
                const byteCharacters = atob(resultado.imagemAssinada);
                const byteArray = new Uint8Array(byteCharacters.length);
                
                for (let i = 0; i < byteCharacters.length; i++) {
                    byteArray[i] = byteCharacters.charCodeAt(i);
                }
                
                blob = new Blob([byteArray], { type: 'image/jpeg' });
            }

			const url = URL.createObjectURL(blob);
			const link = document.createElement('a');
			link.href = url;
			link.download = nomeArquivo;
			
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
			
			setTimeout(() => URL.revokeObjectURL(url), 100);
        } catch (error) {
            AlertModalCsx_E_TimeOut_8000ms_v2('Erro na assinatura digital. Usando imagem original.', DESC_ERROR_CONTAINER_MODAL);
        }
        
    }, 'image/jpeg', 0.9);
}
function resetImageAdjustments() {
    scale = 1;
    brightness = 100;
    contrast = 100;

    $('#brightness').val(100);
    $('#contrast').val(100);

    $('#galeriaImagensVeiculo img').css({
        'filter': '',
        'transform': ''
    });
}

$().ready(function()
{
	ObterDadosUsuario();

	$('.placa-modal').on('keypress', function (e)
	{
        var input = $(this);
        var value = input.val();
        var key = e.originalEvent.key;
        
//        console.log("value: " + value);
//        console.log("key: " + key);
        
        value += key;

        try 
        {
			var pattern = /^(([A-Z]{0,3}))([0-9]{1}[A-Z0-9]{1})?([0-9]{0,2})$/i;
			var ok = pattern.test(value);
			
			if (!ok) e.preventDefault();
		} 
		catch (e) 
		{
//			HandleErrorMessages(e);
		}
	});

    $('#brightness').on('input', function() {
        brightness = $(this).val();
        applyImageAdjustments();
    });

    $('#contrast').on('input', function() {
        contrast = $(this).val();
        applyImageAdjustments();
    });

    $('#galeriaImagensVeiculo').on('wheel', function(event) {
        event.preventDefault();

        const delta = event.originalEvent.deltaY > 0 ? -0.1 : 0.1;
        scale += delta;

        scale = Math.max(0.1, scale);

        applyImageAdjustments();
    });

    $('#galeriaImagensVeiculo').on('slid.bs.carousel', function () {
      $('#galeriaImagensVeiculo .carousel-item:not(.active) img').css({
          'transform': 'scale(1)', // Reseta o zoom da imagem anterior
          'cursor': 'default'
      });
      applyImageAdjustments();
    });
});

function AbrirDetalhesVeiculo(idVeiculo) {
    
    limparTodosCamposModalVeiculo();
    
    idVeiculoGlobal = null;
    idVeiculoGlobal = idVeiculo;
  
    const modalDetalhe = bootstrap.Modal.getInstance(document.getElementById('modalDetalheVeiculo'));
    if (modalDetalhe) {
        modalDetalhe.hide();
    }
    
    const novaModalDetalhe = new bootstrap.Modal(document.getElementById('modalDetalheVeiculo'));
    novaModalDetalhe.show();

    ObterVeiculo(idVeiculo);
}

function ObterVeiculo(idVeiculo)
{	
	idVeiculoGlobal = null;
	idVeiculoGlobal = idVeiculo;	

	var idVeiculoModal = document.getElementById("id_veic");
	if (idVeiculoModal)
		idVeiculoModal.innerHTML = idVeiculo;
	
	var dataString = "acao=obterVeiculoPorId" +
						"&idVeiculo=" + idVeiculo;
	console.log(dataString);

	var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo";
	
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
			console.log("Erro ao obter Veículo Monitorado por Id." + e.respone);
			AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao obter Veículo Monitorado!!', DESC_ERROR_CONTAINER_MODAL);
	    },
		async: false
	});
}

function TratarRetornoVeiculo(jqXHR)
{
	var xmlDoc = $.parseXML( jqXHR.responseText );
	var $xml = $(xmlDoc);
	
//	console.log('TratarRetornoVeiculo()');

	var veiculo = $xml.find('veiculo');

	var placa = veiculo.find('placa').text();
	var marca = veiculo.find('marca').text();
	var modelo = veiculo.find('modelo').text();
	var dataFormatada = veiculo.find('dataVeicFormatada').text();
	var serieEquipamento = veiculo.find('serieEquipamento').text();
	var codigoEquipamento = ( (veiculo.find('codigoEquipamento').text() == null || veiculo.find('codigoEquipamento').text() == '') ? "N/D" : veiculo.find('codigoEquipamento').text() );
	var nomeEquipamento = veiculo.find('descLocal').text();
	var equipamento = codigoEquipamento + ' - ' + serieEquipamento + ' - ' + nomeEquipamento;
	var classificacao = veiculo.find('classificacao').text();
	var faixa = veiculo.find('faixa').text();
	var velocidade = veiculo.find('velocidade').text();
	var latitude = veiculo.find('latitude').text();
	var longitude = veiculo.find('longitude').text();
	var idLocal = veiculo.find('idLocal').text();

	let idLocalFormatado = parseInt(idLocal, 10);

	LATITUDE = latitude;
	LONGITUDE = longitude;
	PLACA = placa;
	ID_LOCAL = isNaN(idLocalFormatado) ? -1 : idLocalFormatado;
	DATA_HORA_PASSAGEM = dataFormatada;

	if (placa != null && placa.trim() != '')
	{
		VEICULO_POSSUI_PLACA = true;
		PLACA = placa;
	}

	PopulaModalDetalheVeiculo(equipamento, faixa, dataFormatada, placa, velocidade, marca, modelo, classificacao)

	var listaImagens = veiculo.find('listaImagens');
	criarGaleriaImagens(listaImagens);
}

function PopulaModalDetalheVeiculo(equipamento, faixa, dataFormatada, placa, velocidade, marca, modelo, classificacao)
{
	var equipamentoModal = document.getElementById("equipamentoModal");
	var pistaModal = document.getElementById("pistaModal");
	var classificacaoModal = document.getElementById("classificacaoModal");
	var dataModal = document.getElementById("dataModal");
	var placaModal = document.getElementById("placaModal");
	var velocidadeModal = document.getElementById("velocidadeModal");
	var marcaModal = document.getElementById("marcaModal");
	var modeloModal = document.getElementById("modeloModal");
	
	if (equipamentoModal)
		equipamentoModal.value = equipamento;
		
	if (pistaModal)
		pistaModal.value = faixa;
		
	if (classificacaoModal)
		classificacaoModal.value = classificacao;
		
	if (dataModal)
		dataModal.value = dataFormatada;
		
	if (placaModal)
		placaModal.value = placa;
		
	if (velocidadeModal)
		velocidadeModal.value = velocidade;
		
	if (marcaModal)
		marcaModal.value = marca;
		
	if (modeloModal)
		modeloModal.value = modelo;
}

function criarGaleriaImagens(listaImagens)
{
	var galeriaImagensVeiculo = document.getElementById("galeriaImagensVeiculo");
	var inicioGaleriaImg = "<div class='carousel-inner'>";
	var fimGaleriaImg = "</div>";
	var controlesGaleriaImg =
				  	"<button class='carousel-control-prev' type='button' data-bs-target='#galeriaImagensVeiculo' data-bs-slide='prev'>" +
				    	"<span class='carousel-control-prev-icon' aria-hidden='true'></span>" +
				    	"<span class='visually-hidden'>Previous</span>" +
				  	"</button>" +
				  	"<button class='carousel-control-next' type='button' data-bs-target='#galeriaImagensVeiculo' data-bs-slide='next'>" +
				    	"<span class='carousel-control-next-icon' aria-hidden='true'></span>" +
				    	"<span class='visually-hidden'>Next</span>" +
					"</button>";
					
	var imagensGaleria = "";

	listaImagens.find('imagem').each(function()
	{
		var $imagem = $(this);
		var idImagemTempoReal = $imagem.find('id').text();
		
		var url = urlRoot + "MuralhaDigital/Veiculo/Imagem?" + "acao=ImagemByIdImg&" + "id=" + idImagemTempoReal;
		
		var itemAtivo = (imagensGaleria === "" ? "active" : "");
		
		imagensGaleria = imagensGaleria +
			"<div class='carousel-item " + itemAtivo + "'>" +
				"<img src='" + url + "' class='d-block w-100 img-responsive' alt='Imagem 1'>" +
			"</div>";
			
	});
	
	if (imagensGaleria === "")
	{
		imagensGaleria =
			"<div class='carousel-item active'>" +
				"<img src='/muralha-digital/assets/images/consilux_grande_transparent.png' class='d-block w-100' alt='Imagem 1'>" +
			"</div>";
	}
	
	if (MOBILE)
	{
		galeriaImagensVeiculo.innerHTML = inicioGaleriaImg + imagensGaleria + fimGaleriaImg + controlesGaleriaImg;
	}
	else
	{
		galeriaImagensVeiculo.innerHTML = inicioGaleriaImg + imagensGaleria + fimGaleriaImg;// + controlesGaleriaImg;
		
		var controleGaleriaPrev = document.getElementById("controleGaleriaPrev");
		var controleGaleriaNext = document.getElementById("controleGaleriaNext");
		
		controleGaleriaPrev.innerHTML =
				"<div style='height:100%;' class='d-flex align-items-center justify-content-center img-responsive'> " +
					"<a style='color: #000099;' class='seta-troca-imagem d-flex align-items-center justify-content-center img-responsive' type='button' data-bs-target='#galeriaImagensVeiculo' data-bs-slide='prev'> " +
						"<svg aria-hidden='true' focusable='false' data-prefix='fas' data-icon='chevron-left' role='img' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 320 512' class='svg-inline--fa fa-chevron-left fa-w-10 fa-3x'><path fill='currentColor' d='M34.52 239.03L228.87 44.69c9.37-9.37 24.57-9.37 33.94 0l22.67 22.67c9.36 9.36 9.37 24.52.04 33.9L131.49 256l154.02 154.75c9.34 9.38 9.32 24.54-.04 33.9l-22.67 22.67c-9.37 9.37-24.57 9.37-33.94 0L34.52 272.97c-9.37-9.37-9.37-24.57 0-33.94z' class=''></path></svg> " +
					"</a> " +
				"</div>";
				
		controleGaleriaNext.innerHTML =
				"<div style='height:100%;' class='d-flex align-items-center justify-content-center img-responsive'> " +
					"<a style='color: #000099;' class='seta-troca-imagem d-flex align-items-center justify-content-center img-responsive' type='button' data-bs-target='#galeriaImagensVeiculo' data-bs-slide='next'> " +
						"<svg aria-hidden='true' focusable='false' data-prefix='fas' data-icon='chevron-right' role='img' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 320 512' class='svg-inline--fa fa-chevron-right fa-w-10 fa-3x'><path fill='currentColor' d='M285.476 272.971L91.132 467.314c-9.373 9.373-24.569 9.373-33.941 0l-22.667-22.667c-9.357-9.357-9.375-24.522-.04-33.901L188.505 256 34.484 101.255c-9.335-9.379-9.317-24.544.04-33.901l22.667-22.667c9.373-9.373 24.569-9.373 33.941 0L285.475 239.03c9.373 9.372 9.373 24.568.001 33.941z' class=''></path></svg> " +
					"</a> " +
				"</div>";
	}

    //Após criar a galeria, aplicamos os ajustes na primeira imagem. ---
    applyImageAdjustments();
}


function limparTodosCamposModalVeiculo()
{
	PLACA = null;	
	
	//Chamar a função de reset aqui ---
    resetImageAdjustments();

	var equipamentoModal = document.getElementById("equipamentoModal");
	var pistaModal = document.getElementById("pistaModal");
	var dataModal = document.getElementById("dataModal");
	var placaModal = document.getElementById("placaModal");
	var velocidadeModal = document.getElementById("velocidadeModal");
	var marcaModal = document.getElementById("marcaModal");
	var modeloModal = document.getElementById("modeloModal");
	var galeriaImagensVeiculo = document.getElementById("galeriaImagensVeiculo");
	var controleGaleriaPrev = document.getElementById("controleGaleriaPrev");
	var controleGaleriaNext = document.getElementById("controleGaleriaNext");
	var idVeiculoModal = document.getElementById("id_veic");
	
	if (galeriaImagensVeiculo)
		galeriaImagensVeiculo.innerHTML = "";
		
	if (controleGaleriaPrev)
		controleGaleriaPrev.innerHTML = "";
		
	if (controleGaleriaNext)
		controleGaleriaNext.innerHTML = "";
	
	if (equipamentoModal)
		equipamentoModal.value = "";
		
	if (pistaModal)
		pistaModal.value = "";
		
	if (dataModal)
		dataModal.value = "";
		
	if (placaModal)
		placaModal.value = "";
		
	if (velocidadeModal)
		velocidadeModal.value = "";
		
	if (marcaModal)
		marcaModal.value = "";
		
	if (modeloModal)
		modeloModal.value = "";
		
	if (idVeiculoModal)
		idVeiculoModal.innerHTML = "";
		
	DesabilitarAlteracaoPlaca();
	dados_orig = '';
}

function alterar_placa()
{
	if (document.getElementById("btnPlaca").innerHTML == 'SALVAR')
		AlteraPlaca();
	else
		HabilitarAlteracaoPlaca();
}

function AlteraPlaca()
{
	placa_alt = document.getElementById("placaModal").value;
	placa_alt = placa_alt.toUpperCase();
	
	if (placa_alt.length != 7){
		alert('Quantidade de caracteres incorretos. Favor revisar!')
		return;
	}
	
	alert('Alterando placa do veículo:: ' + dados_orig + ' ==> ' + placa_alt);
	
    var dataString = "placa=" 		+ placa_alt + 
					"&placa_orig=" 	+ dados_orig + 
					"&id_veiculo=" 	+ document.getElementById("id_veic").innerHTML +
					"&acao=alterarPlaca";
    console.log(dataString);
    
    var urlExecuta = urlRoot + "MuralhaDigital/Veiculo";

    $.ajax({
        type: 		"POST",
        url: 		urlExecuta,
        data: 		dataString,
        dataType:	"xml",
           
        //if received a response from the server
        success: 	function( data, textStatus, jqXHR) 
        {
        	var event = jqXHR.responseText;
        	var xmlDoc = $.parseXML( event );
			var $xml = $(xmlDoc);
    			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				DesabilitarAlteracaoPlaca();
				if (window.parent && window.parent !== window) {
					window.parent.postMessage({ type: 'placaAlterada', placaAntiga: dados_orig, placaNova: placa_alt }, window.location.origin);
				}
				VEICULO_POSSUI_PLACA = true;
				PLACA = placa_alt;
			} else {
				alert("ERRO::> " + msgResposta);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
    });
}

function HabilitarAlteracaoPlaca()
{
	document.getElementById("placaModal").disabled = false;
	document.getElementById("btnPlaca").innerHTML = 'SALVAR';
	document.getElementById("btnPlaca").className = "btn btn-sm btn-success";
	dados_orig = document.getElementById("placaModal").value.toUpperCase();
}

function DesabilitarAlteracaoPlaca()
{
	document.getElementById("placaModal").disabled = true;
	document.getElementById("btnPlaca").innerHTML = 'ALTERAR';
	document.getElementById("btnPlaca").className = "btn btn-sm btn-warning";
}

function AbrirCadastroMonitorado()
{
	if (ValidarDadosCadMonitorado())
		CadMonitoradoExterno('#modalDetalheVeiculo', PLACA);
}

function ValidarDadosCadMonitorado()
{
	if (!VEICULO_POSSUI_PLACA)
	{
		alert("Veículo não possui placa! Informe a placa do veículo para monitorá-lo.");
		return false;
	}
	
	var placaModal = document.getElementById("placaModal");
	if (placaModal)
	{
		if (PLACA != placaModal.value.toUpperCase())
		{
			alert("Placa informada (" + placaModal.value.toUpperCase() + ") diverge da placa do veículo (" + PLACA + ")! Se houve alteração da placa, é necessário salvar antes de monitorar o veículo!");
			return false;
		}
	}
	else
	{
		alert("Placa não encontrada!");
		return false;
	}
	
	return true;
}

function ObterVeiculoAnterior()
{
	PLACA = null;
	var idVeiculo = document.getElementById("id_veic").innerHTML;
	var link_veiculo = null;
	
	if (IS_MODO_GRADE_IMAGENS)
		link_veiculo = document.querySelector('img[alt="'+idVeiculo+'"]');
	else
		link_veiculo = document.getElementById("btnDetalhar_" + idVeiculo);
	
	
	if (link_veiculo)
	{
		var idVeiculoAlvo = link_veiculo.getAttribute("id-veiculo-anterior");
//		console.log("idVeiculoAnteior: " + idVeiculoAlvo);
		
		if (idVeiculoAlvo != '' && idVeiculoAlvo != null)
		{
			limparTodosCamposModalVeiculo();
			ObterVeiculo(idVeiculoAlvo);
		}
	}
}

function ObterVeiculoProximo()
{
	PLACA = null;
	var idVeiculo = document.getElementById("id_veic").innerHTML;
	var link_veiculo = null;
	
	if (IS_MODO_GRADE_IMAGENS)
		link_veiculo = document.querySelector('img[alt="'+idVeiculo+'"]');
	else
		link_veiculo = document.getElementById("btnDetalhar_" + idVeiculo);
		
	
	if (link_veiculo)
	{
		var idVeiculoAlvo = link_veiculo.getAttribute("id-veiculo-proximo");
		
		if (idVeiculoAlvo != '' && idVeiculoAlvo != null)
		{
			limparTodosCamposModalVeiculo();
			ObterVeiculo(idVeiculoAlvo);
		}
	}
}

function AbrirCadastroMonitorado(placaExterna = '')
{
	if(placaExterna != ''){
		CadMonitoradoExterno('#modalDetalheVeiculo', placaExterna);
	}
	
	if (ValidarDadosCadMonitorado())
		CadMonitoradoExterno('#modalDetalheVeiculo', PLACA);
}

function AbrirLinhaTempoVeiculo()
{
	var tituloModalLinhaTempo = "Linha do Tempo - Veiculo"
	var idVeiculo = null;

	var idVeiculoModal = document.getElementById("id_veic");
	if (idVeiculoModal)
		idVeiculo = idVeiculoModal.innerHTML;

	LinhaTempoVeiculoExterno('#modalDetalheVeiculo', tituloModalLinhaTempo, idVeiculo);
}

function capturarVeiculoId(){

	const modalDetalheVeiculo = bootstrap.Modal.getInstance(document.getElementById('modalDetalheVeiculo'));	
	
	if (modalDetalheVeiculo) {
    	modalDetalheVeiculo.hide();
	}

	abrirModalPassagemComId(idVeiculoGlobal);
}

function abrirPerfilComportamental(){
	
	if(!VEICULO_POSSUI_PLACA || PLACA === null){
		alert("É preciso ter uma placa cadastrada para acessar o perfil comportamental do veículo.")
		return;
	}
	
	window.open(
        "/muralha-digital/pages/perfil-comportamental/perfil-comportamental.jsp?placa=" 
        + PLACA + "&intervalo=365", 
        "_blank",
        "width=800,height=600"
    );
}

$(document).ready(function() {
    $('#modalDetalheVeiculo').on('hidden.bs.modal', function () {
        $('.modal-backdrop').remove();
        $('body').removeClass('modal-open');
        $('body').removeAttr('style');
    });
});

function ObterDadosUsuario() {
    var dataString = "acao=obterDadosUsuario";
    var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo";
    
    $.ajax({
        type: "GET",
        url: urlPesquisa,
        data: dataString,
        dataType: "xml",
        success: function(xml, textStatus, jqXHR) {
            try {
                var xmlDoc = $.parseXML(jqXHR.responseText);
                var $xml = $(xmlDoc);

                var nome = $xml.find('nomeUsuario').text();
                if (nome && nome.trim() !== '') {
                    NOME_USUARIO = nome;
                }

				var idUsuario = $xml.find('idUsuario').text();
				ID_USUARIO = parseInt(idUsuario, 10) || 0;

				console.log("Nome do usuário obtido: " + NOME_USUARIO + " | ID Usuário: " + ID_USUARIO);
            } catch(e) {
                console.log("Erro ao processar nome do usuário: " + e);
            }
        },
        error: function(e, b, error) {
            console.log("Erro ao obter nome do usuário: " + error);
        }
    });
}

function RegistrarExportacaoImagem(dataHoraExportacao) {
	var dataString = "acao=registrarExportacaoImagem" +
					"&idUsuario=" + ID_USUARIO +
					"&idLocal=" + ID_LOCAL +
					"&placa=" + encodeURIComponent(PLACA) +
					"&dataHoraExportacao=" + encodeURIComponent(dataHoraExportacao) +
					"&dataHoraPassagem=" + encodeURIComponent(DATA_HORA_PASSAGEM) +
					"&idVeiculoTempoReal=" + idVeiculoGlobal;
	
	var urlExecuta = urlRoot + "MuralhaDigital/Veiculo";

	$.ajax({
		type: "POST",
		url: urlExecuta,
		data: dataString,
		dataType: "xml",
		
		success: function(data, textStatus, jqXHR) {
			var xmlDoc = $.parseXML(jqXHR.responseText);
			var $xml = $(xmlDoc);
			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');
			
			if (!sucesso) {
				console.log("Erro ao registrar exportação: " + msgResposta);
			} else {
				console.log("Exportação registrada com sucesso no banco de dados.");
			}
		},
		
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("Erro ao registrar exportação no banco de dados: " + errorThrown);
		},
		async: true
	});
}