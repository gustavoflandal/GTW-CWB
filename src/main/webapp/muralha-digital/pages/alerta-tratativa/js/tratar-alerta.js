var ID_ALERTA 							= "";
var ID_TIPO_ALERTA 						= "";
var ID_TIPO_REGISTRO 					= "";
var DESCARTADO 							= false;
var ID_MOTIVO_DESCARTE 					= "";
var MOTIVO_DESCARTE 					= "";
var OBS_DESCARTE 						= "";
var OCORRENCIA_GERADA 					= false;
var ID_OCORRENCIA 						= "";
var OCORRENCIA_COM_NOTIFICACAO 			= false;
var OCORRENCIA_FINALIZADA 				= false;
var ID_STATUS_FINALIZAR_OCORRENCIA 		= "";
var STATUS_FINALIZAR_OCORRENCIA 		= "";
var OBS_FINALIZAR_OCORRENCIA 			= "";
var ORIGEM_CONSULTA_ALERTAS_VINCULADOS	= false;
var ALERTA_VINCULADO 					= false;
var ID_ALERTA_VINCULADO 				= "";
var ID_CAD_VEIC_MONITORADO 				= "";
var ORIGEM_CONSULTA_VISUALIZAR_ALERTAS	= false;
var PERMITE_ATENDIMENTO					= false;
var PERMITE_ALTERAR_ATENDIMENTO			= true;
var IGNORE_PRIVADO = "";
var PLACA_PERFIL_COMPORTAMENTAL = "";
var EH_ALERTA_BLITZ = false;
window.SUPERVISIONADO = "0";

// Variáveis de ajuste de imagem 
let scale = 1;
let brightness = 100;
let contrast = 100;

// Funções de ajuste de imagem 
function applyImageAdjustments() {
	const activeImage = $('#galeriaImagensAlerta .carousel-item.active img');
	if (activeImage.length) {
		activeImage.css({
			'filter': `brightness(${brightness}%) contrast(${contrast}%)`,
			'transform': `scale(${scale})`,
			'transition': 'transform 0.2s ease-out',
			'cursor': 'grab'
		});
	}
}

function resetImageAdjustments() {
	scale = 1;
	brightness = 100;
	contrast = 100;

	$('#brightness').val(99);
	$('#contrast').val(99);

    // Reseta todas as imagens no carrossel para o caso de alguma ter ficado com zoom
	$('#galeriaImagensAlerta img').css({
		'filter': '',
		'transform': ''
	});
}


$(document).ready(function()
{
	try
	{
		const valor = sessionStorage.getItem('ignore-privado');
		if (valor) {
			IGNORE_PRIVADO = valor;
		}

		// Adicionado os listeners de evento para os ajustes de imagem
		$('#brightness').on('input', function() {
			brightness = $(this).val();
			applyImageAdjustments();
		});

		$('#contrast').on('input', function() {
			contrast = $(this).val();
			applyImageAdjustments();
		});

		$('#galeriaImagensAlerta').on('wheel', function(event) {
			event.preventDefault();
			const delta = event.originalEvent.deltaY > 0 ? -0.1 : 0.1;
			scale += delta;
			scale = Math.max(0.1, scale); // Impede que o zoom seja menor que 0.1
			applyImageAdjustments();
		});

		// Reseta os ajustes da imagem anterior e aplica na nova ao trocar de slide
		$('#galeriaImagensAlerta').on('slid.bs.carousel', function () {
		  resetImageAdjustments(); // Reseta tudo para o estado inicial no novo slide
		  applyImageAdjustments(); // Aplica o estado (que agora é o default)
		});


		var funcoes =
		[
			AddLoading,
			ConfigCarroselImg,
			ObterDadosURL,
			ObterAlerta,
			HabilitarTooltips,
			ObterGruposEmail,
			ObterGruposSMS,
			RemoveLoading
		];
		
		var d = $.Deferred().resolve();
		while (funcoes.length > 0) {
			d = d.then(funcoes.shift());
		}
	}
	catch (ex)
	{
		RemoveLoading();
		AlertCsx_E_TimeOut_8000ms('Erro ao carregar a página!!');
	}
});

function ConfigCarroselImg()
{
	$('.carousel').carousel({
		interval: false,
	});
}

function AddLoading()
{
	$("body").addClass("loading");
}
function RemoveLoading()
{
	$("body").removeClass("loading");
}

function AbrirUltimasPassagens()
{
    var placaVeiculo = PLACA_PERFIL_COMPORTAMENTAL;
    
    if (!placaVeiculo) {
        // Se não tiver a placa do perfil comportamental, tenta obter da tabela
        var primeiraLinha = document.querySelector("#tabelaInfoAlerta tbody tr");
        if (primeiraLinha) {
            var celulaPlaca = primeiraLinha.cells[3];
            if (celulaPlaca) {
                // Extrai apenas o texto da placa (remove HTML de comparação)
                var textoPlaca = celulaPlaca.textContent || celulaPlaca.innerText;
                placaVeiculo = textoPlaca.trim();
            }
        }
    }
    
    if (!placaVeiculo) {
        AlertCsx_E_TimeOut_8000ms('Não foi possível identificar a placa do veículo!');
        return;
    }
    
    var urlConsulta = '/muralha-digital/pages/consulta-veiculo/consulta.jsp?' + 
                     'quantidadeItensPorPagina=10&' + 
                     'placa=' + encodeURIComponent(placaVeiculo);
    
    // Abrir em nova aba
    window.open(urlConsulta, '_blank');
}

function HabilitarTooltips()
{
	if (!OCORRENCIA_GERADA)
	{
		var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
		var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl)
		{
			return new bootstrap.Tooltip(tooltipTriggerEl)
		});
	}
}

function DesabilitarTooltips()
{
	$('[data-toggle="tooltip"]').tooltip('disable')
}

function DesabilitarTooltipAcaoProcedimento()
{
	var tooltipAcaoProcedimento = document.getElementById("tooltipAcaoProcedimento");
	if (tooltipAcaoProcedimento)
		$('#tooltipAcaoProcedimento').tooltip('disable');
}

function ObterDadosURL()
{
	try
	{
		//Obtendo id da evidencia
		var params 		= window.location.href.substring(window.location.href.indexOf('?')+1);	
		params 			= params.split('&');
		var bruto 		= params[0].split('=');
		var idAlerta 	= bruto[1];
		
		if(idAlerta == null)
		{
			alert("Não é possível acessar a pagina sem ID de Alerta!!");
			return false;
		}
		
		ID_ALERTA = idAlerta;
		
		if(params.length > 1)
			document.getElementById("btnVoltar").style.display = '';
		
		if (params.length > 2)
		{
			var param2Bruto	= params[2].split('=');
			var itemParam2 	= param2Bruto[0];
			var valorParam2 = param2Bruto[1];
			
			if(itemParam2 == "AlertasVinculados" && valorParam2 == "1")
				ORIGEM_CONSULTA_ALERTAS_VINCULADOS = true;
				
			if(itemParam2 == "VisualizarAlertas" && valorParam2 == "1")
				ORIGEM_CONSULTA_VISUALIZAR_ALERTAS = true;
		}
	}	
	catch(e)
	{
		console.log("Falha ObterDadosURL():: " + e);
	}			
}

function ObterAlerta() {
	if (ID_ALERTA === "") return;

	resetImageAdjustments(); // Reseta os ajustes antes de carregar um novo alerta
	limparTabelaInfoAlerta();
	AddLoading();

	var dataString = "acao=obterAlertaPorId" +
                     "&idAlerta=" + ID_ALERTA +
                     (typeof IGNORE_PRIVADO !== "undefined" && IGNORE_PRIVADO ? "&ignorePrivado=" + encodeURIComponent(IGNORE_PRIVADO) : "");

	var url = urlRoot + "MuralhaDigital/Alerta";

	$.ajax({
		type: "GET",
		url: url,
		data: dataString,
		dataType: "xml",
		success: function(xml, textStatus, jqXHR) {
            let sucessoNode = xml.getElementsByTagName("sucesso")[0];
            if (sucessoNode) {
					let sucesso = sucessoNode.textContent.trim().toLowerCase() === "true";
					if (!sucesso) {
						tratarErroAlerta(xml);
						return;
					}
            }
			sessionStorage.removeItem('ignore-privado');
			const supervisionado = xml.getElementsByTagName("supervisionado")[0];
			if (supervisionado) {
			    window.SUPERVISIONADO = supervisionado.textContent;
			}
            tratarSucessoAlerta(jqXHR);
		},
		error: function(jqXHR, textStatus, errorThrown) {
            alert("Erro técnico na comunicação com o servidor.");
		},
		async: false
	});
}

function tratarSucessoAlerta(jqXHR) {
	desbloquearAcoes();
	PopulaTela(jqXHR.responseText);
}

function tratarErroAlerta(xml) {
	bloquearAcoes();
	let msgNode = xml.getElementsByTagName("msgResposta")[0];
	let msg = msgNode && msgNode.textContent ? msgNode.textContent.trim() : "Erro ao obter alerta.";

	Swal.fire({
		icon: 'error',
		title: 'Erro',
		text: msg,
		confirmButtonText: 'OK'
	});
}

function bloquearAcoes() {
	const ids = [
		"btnGerarOcorrencia",
		"btnAbrirModalFinalizarOcorrencia",
		"btnAbrirModalDescartar",
		"btnAbrirModalAcaoProcedimento",
		"btnAbrirModalAnotacaoContrib",
		"btnAbrirModalVeiculoMonitorado",
		"btnAbrirLinhaTempo",
		"btnAbrirMapaPassagens"
	];

	ids.forEach(id => {
		const btn = document.getElementById(id);
		if (btn) btn.disabled = true;
	});
}

function desbloquearAcoes() {
	const ids = [
		"btnGerarOcorrencia",
		"btnAbrirModalFinalizarOcorrencia",
		"btnAbrirModalDescartar",
		"btnAbrirModalAcaoProcedimento",
		"btnAbrirModalAnotacaoContrib",
		"btnAbrirModalVeiculoMonitorado",
		"btnAbrirLinhaTempo",
		"btnAbrirMapaPassagens"
	];

	ids.forEach(id => {
		const btn = document.getElementById(id);
		if (btn) btn.disabled = false;
	});
}

/*
	Aqui na função abaixo é feita a comparação entre a placa digitada pelo
	usuário e a placa lida pelo sistema, caso haja algum caracatere diferente
	ele mudará a cor desse caractere para vermelho (para ambas as placas) e
	aumentará a contagem de diferenças.
*/
function compararPlacas(placaVeiculo, placaCadastro) {
	let diffs = 0;
	let resultadoLida = "";
	let resultadoCadastro = "";
	let exatidao = false;

	const maxLen = Math.max(placaVeiculo?.length || 0, placaCadastro?.length || 0);

	for(let i = 0; i < maxLen; i++) {
		const charLida = placaVeiculo?.[i] ?? "";
		const charCadastro = placaCadastro?.[i] ?? "";

		if(charLida !== charCadastro) {
			resultadoLida += `<span class="text-danger"><b>${charLida || " "}</b></span>`
			resultadoCadastro += `<span class="text-danger"><b>${charCadastro || " "}</b></span>`
			diffs++;
			exatidao = true;
		} else {
			resultadoLida += charLida;
			resultadoCadastro += charCadastro;
		}
	}

	return {
		quantidade: diffs,
		placaVeiculo: resultadoLida,
		placaCadastro: resultadoCadastro,
		exatidao: exatidao,
	};
}

function PopulaTela(event)
{
	var listaVeiculos 			= null;
	var idMotivoDescarte 		= null;
	var motivoDescarte 			= null;
	var observacao 				= null;
	var tipoAlerta 				= null;
	var dataAlertaFormatada 	= null;
	var htmlStatus 				= null;
	var idStatusOcorrencia 		= null;
	var statusOcorrencia 		= null;
	var obsFinalizarOcorrencia 	= null;
	var placaCadastro 			= null;
	var nomePontoInteresse 		= null;
	var isAssinado 				= false;
		
	var table = document.getElementById("tabelaInfoAlerta");
	var tableRef = table.getElementsByTagName('tbody')[0];
	tableRef.innerHTML = "";
	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var encontrouAlerta = false;

	$xml.find('alerta').each(function()
	{
		encontrouAlerta = true;
		
		var $alerta = $(this);

		var idTipoAlerta 		= $alerta.find('idTipoAlerta'		).text();
		var statusAlertaDesc 	= $alerta.find('statusAlertaDesc'	).text();
		tipoAlerta 				= $alerta.find('tipoAlerta'			).text();
		dataAlertaFormatada 	= $alerta.find('dataAlertaFormatada').text() + ' ' + $alerta.find('horaAlertaFormatada').text();

		var equipamento 			= $alerta.find('equipamento'			).text();
		var faixa 					= $alerta.find('faixa'					).text();
		var placaVeiculo 			= $alerta.find('placaVeiculo'			).text();
		var dataVeiculoFormatada 	= $alerta.find('dataVeiculoFormatada'	).text() + ' ' + $alerta.find('horaVeiculoFormatada').text();
		var valorAssinado = $alerta.find('assinado').text();
		isAssinado = (valorAssinado === 'true');		
		
		ID_CAD_VEIC_MONITORADO 	= $alerta.find('idCadVeicMonitorado').text();
		placaCadastro 			= $alerta.find('placaCadastro'		).text();
		nomePontoInteresse 		= $alerta.find('nomePontoInteresse'	).text();
		
		var idTipoRegistro = $alerta.find('idTipoRegistro').text();
		
		ID_OCORRENCIA 		= $alerta.find('idOcorrencia'		).text();
		idStatusOcorrencia 	= $alerta.find('idStatusOcorrencia'	).text();
		statusOcorrencia 	= $alerta.find('statusOcorrencia'	).text();
		
		
		ID_TIPO_ALERTA 		= idTipoAlerta;
		ID_TIPO_REGISTRO	= idTipoRegistro;
		PLACA_PERFIL_COMPORTAMENTAL		= placaVeiculo;
		
		if (ID_TIPO_ALERTA != null && ID_TIPO_ALERTA != "")
			ObterStatusFinalizacaoOcorrencia();
		
		DESCARTADO 			= ($alerta.find('descartado'		).text() === "true");
		idMotivoDescarte 	=  $alerta.find('idMotivoDescarte'	).text();
		motivoDescarte 		=  $alerta.find('motivoDescarte'	).text();
		observacao 			=  $alerta.find('observacao'		).text();
		
		OCORRENCIA_GERADA 			= ($alerta.find('ocorrenciaGerada'			).text() === "true");
		OCORRENCIA_COM_NOTIFICACAO 	= ($alerta.find('ocorrenciaComNotificacao'	).text() === "true");
		OCORRENCIA_FINALIZADA 		= ($alerta.find('ocorrenciaFinalizada'		).text() === "true");
		obsFinalizarOcorrencia 		=  $alerta.find('obsFinalizarOcorrencia'	).text();
		
		PERMITE_ATENDIMENTO 		= ($alerta.find('permiteAtendimento'		).text() === "true");
		PERMITE_ALTERAR_ATENDIMENTO	= ($alerta.find('permiteAlterarAtendimento'	).text() === "true");
		
		ALERTA_VINCULADO 	= ($alerta.find('alertaVinculado'	).text() === "true");
		ID_ALERTA_VINCULADO =  $alerta.find('idAlertaVinculado'	).text();
				
		
		if (OCORRENCIA_FINALIZADA)
		{
			ID_STATUS_FINALIZAR_OCORRENCIA 	= idStatusOcorrencia;
			STATUS_FINALIZAR_OCORRENCIA 	= statusOcorrencia;
			OBS_FINALIZAR_OCORRENCIA 		= obsFinalizarOcorrencia;
		}
		
		htmlStatus = "<small>" + statusAlertaDesc + "</small>";
		if (DESCARTADO)
		{
			ID_MOTIVO_DESCARTE 	= idMotivoDescarte;
			MOTIVO_DESCARTE 	= motivoDescarte;
			OBS_DESCARTE 		= observacao;
			
			htmlStatus = "<h6><strong class='text-danger'>" + statusAlertaDesc + "</strong></h6>";
		}
		else if (OCORRENCIA_GERADA)
		{
			htmlStatus = "<h6><strong class='text-success'>" + statusAlertaDesc + "</strong></h6>";
		}
		else if (ALERTA_VINCULADO)
		{
			htmlStatus = "<h6><strong class='text-warning'>" + statusAlertaDesc + "</strong></h6>";
		}

		/*
			Aqui abaixo está chamando a função do compararPlacas fazendo então a
			população dos valores da tabela com:
			equipamento;
			dataVeiculoFormatada;
			Faixa;
			e placaVeiculo (com a comparação de caracteres diferentes em vermelho).
			Terá uma validação no placaVeiculo, se tiver algum caractere divergente entre as placas ele populará normal, caso tenha algum caractere divergente, populará em vermelho em um popover.
		*/
		const comparacao = compararPlacas(placaVeiculo, placaCadastro);
		var row = tableRef.insertRow(tableRef.rows.length);
		row.insertCell(0).innerHTML = `${equipamento}`;
		row.insertCell(1).innerHTML = `${dataVeiculoFormatada}`;
		row.insertCell(2).innerHTML = `${faixa}`;

		if (comparacao.exatidao === true) {
			row.insertCell(3).innerHTML = `
				<small class="d-inline-flex align-items-center gap-1">
					<span>${comparacao.placaVeiculo}</span>
					<button type="button"
						class="btn btn-link p-0 m-0 align-baseline"
						data-bs-toggle="popover"
						data-bs-html="true"
						data-bs-container="body"
						title="Semelhança de Placa"
						data-bs-content='
								Quantidade: ${comparacao.quantidade} caractere(s) <br><br>
								<pre style="margin:0;">Placa Lida:       ${comparacao.placaVeiculo}<br>Placa Cadastrada: ${comparacao.placaCadastro}</pre>'>
						<img class="espec-placa" src="./../../assets/images/alertas/Vector.svg" alt="Especificações Placa">
					</button>
				</small>`;
		} else {
			row.insertCell(3).innerHTML = `
				<small class="d-inline-flex align-items-center gap-1">
					<span>${comparacao.placaVeiculo}</span>
				</small>`
		}
		
		if (listaVeiculos == null)
			listaVeiculos = $alerta.find('listaVeiculos');
	});

	if (encontrouAlerta)
	{
		var infoTipoAlerta 		= document.getElementById("infoTipoAlerta");
		var infoDataAlerta 		= document.getElementById("infoDataAlerta");
		var infoPlacaMonitorada = document.getElementById("infoPlacaMonitorada");
		var infoPontoInteresse 	= document.getElementById("infoPontoInteresse");
		var infoStatusAlerta 	= document.getElementById("infoStatusAlerta");
		var checkboxAssinatura  = document.getElementById('checkCienciaTratativa');

		if (infoTipoAlerta && tipoAlerta != null)
			infoTipoAlerta.innerHTML 		= "<small>" + tipoAlerta 							+ "</small>";
		if (infoDataAlerta && dataAlertaFormatada != null)
			infoDataAlerta.innerHTML 		= "<small>" + dataAlertaFormatada.substring(0, 16) 	+ "</small>";
		if (infoPlacaMonitorada && placaCadastro != null)
			infoPlacaMonitorada.innerHTML 	= "<small>" + placaCadastro 						+ "</small>";
		if (infoPontoInteresse && nomePontoInteresse != null)
			infoPontoInteresse.innerHTML 	= "<small>" + nomePontoInteresse					+ "</small>";
		if (infoStatusAlerta && htmlStatus != null)
			infoStatusAlerta.innerHTML 		= "<small>" + htmlStatus 							+ "</small>";
		if (checkboxAssinatura) {
		    checkboxAssinatura.checked = isAssinado;
		}
	}

	if (encontrouAlerta)
		AtualizarControles();
	else
		DesabilitarAcoesTela();
	
	criarGaleriaImagens(listaVeiculos);

	verificarSeEhAlertaBlitz();

	const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
	popoverTriggerList.forEach(el => new bootstrap.Popover(el));
}

function criarGaleriaImagens(listaVeiculos)
{
	var galeriaImagensAlerta = document.getElementById("galeriaImagensAlerta");
	var inicioGaleriaImg = "<div class='carousel-inner'>";
	var fimGaleriaImg = "</div>";
	var controlesGaleriaImg =
				"<button class='carousel-control-prev' type='button' data-bs-target='#galeriaImagensAlerta' data-bs-slide='prev'>" +
					"<span class='carousel-control-prev-icon' aria-hidden='true'></span>" +
					"<span class='visually-hidden'>Previous</span>" +
				"</button>" +
				"<button class='carousel-control-next' type='button' data-bs-target='#galeriaImagensAlerta' data-bs-slide='next'>" +
					"<span class='carousel-control-next-icon' aria-hidden='true'></span>" +
					"<span class='visually-hidden'>Next</span>" +
				"</button>";
	var imagensGaleria = "";

	if (listaVeiculos != null)
	{
		listaVeiculos.find('veiculo').each(function()
		{
			var $veiculo = $(this);
			var idImagemTempoReal = $veiculo.find('idImagemTempoReal').text();
			
//			console.log($veiculo);
			
			var url = urlRoot + "MuralhaDigital/Veiculo/Imagem?" + "acao=ImagemByIdImg&" + "id=" + idImagemTempoReal;
			
			var itemAtivo = (imagensGaleria === "" ? "active" : "");
			
			imagensGaleria = imagensGaleria +
				"<div class='carousel-item " + itemAtivo + "'>" +
					"<img src='" + url + "' class='d-block w-100 img-responsive' alt='Imagem 1'>" +
				"</div>";
				
		});
	}
	
	if (imagensGaleria === "")
	{
		imagensGaleria =
			"<div class='carousel-item active'>" +
				"<img src='/muralha-digital/assets/images/consilux_grande_transparent.png' class='d-block w-100' alt='Imagem 1'>" +
			"</div>";
	}
	
	if (MOBILE)
	{
		galeriaImagensAlerta.innerHTML = inicioGaleriaImg + imagensGaleria + fimGaleriaImg + controlesGaleriaImg;
	}
	else
	{
		galeriaImagensAlerta.innerHTML = inicioGaleriaImg + imagensGaleria + fimGaleriaImg;// + controlesGaleriaImg;
		
		var controleGaleriaPrev = document.getElementById("controleGaleriaPrev");
		var controleGaleriaNext = document.getElementById("controleGaleriaNext");
		
		controleGaleriaPrev.innerHTML =
				"<div style='height:100%;' class='d-flex align-items-center justify-content-center img-responsive'> " +
					"<a style='color: #000099;' class='seta-troca-imagem d-flex align-items-center justify-content-center img-responsive' type='button' data-bs-target='#galeriaImagensAlerta' data-bs-slide='prev'> " +
						"<svg aria-hidden='true' focusable='false' data-prefix='fas' data-icon='chevron-left' role='img' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 320 512' class='svg-inline--fa fa-chevron-left fa-w-10 fa-3x'><path fill='currentColor' d='M34.52 239.03L228.87 44.69c9.37-9.37 24.57-9.37 33.94 0l22.67 22.67c9.36 9.36 9.37 24.52.04 33.9L131.49 256l154.02 154.75c9.34 9.38 9.32 24.54-.04 33.9l-22.67 22.67c-9.37 9.37-24.57 9.37-33.94 0L34.52 272.97c-9.37-9.37-9.37-24.57 0-33.94z' class=''></path></svg> " +
					"</a> " +
				"</div>";
				
		controleGaleriaNext.innerHTML =
				"<div style='height:100%;' class='d-flex align-items-center justify-content-center img-responsive'> " +
					"<a style='color: #000099;' class='seta-troca-imagem d-flex align-items-center justify-content-center img-responsive' type='button' data-bs-target='#galeriaImagensAlerta' data-bs-slide='next'> " +
						"<svg aria-hidden='true' focusable='false' data-prefix='fas' data-icon='chevron-right' role='img' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 320 512' class='svg-inline--fa fa-chevron-right fa-w-10 fa-3x'><path fill='currentColor' d='M285.476 272.971L91.132 467.314c-9.373 9.373-24.569 9.373-33.941 0l-22.667-22.667c-9.357-9.357-9.375-24.522-.04-33.901L188.505 256 34.484 101.255c-9.335-9.379-9.317-24.544.04-33.901l22.667-22.667c9.373-9.373 24.569-9.373 33.941 0L285.475 239.03c9.373 9.372 9.373 24.568.001 33.941z' class=''></path></svg> " +
					"</a> " +
				"</div>";
	}

    // Aplica os ajustes na imagem ativa (geralmente a primeira)
	applyImageAdjustments();
}

function limparTabelaInfoAlerta()
{
	var tableHeaderRowCount = 1;
	var table = document.getElementById("tabelaInfoAlerta");
	var rowCount = table.rows.length;
	
	for (var i = tableHeaderRowCount; i < rowCount; i++) 
		table.deleteRow(tableHeaderRowCount);
}

function GerarOcorrencia()
{
	var dataString = "acao=gerarOcorrencia" +
						"&idAlerta=" 		+ ID_ALERTA +
						"&idTipoAlerta=" 	+ ID_TIPO_ALERTA +
						"&idCadMonitorado=" + ID_CAD_VEIC_MONITORADO;
	var url = urlRoot + "MuralhaDigital/Ocorrencia";

	if (confirm("Deseja realmente confirmar a irregularidade?"))
	{
		$.ajax({
			type: 		"POST",
			url: 		url,
			data: 		dataString,
			dataType:	"xml",
				
			success: 	function( data, textStatus, jqXHR) 
			{
			var event 	= jqXHR.responseText;
			var xmlDoc 	= $.parseXML( event );
				var $xml 	= $(xmlDoc);

				var sucesso 				= $xml.find('sucesso'				).text();
				var msgResposta 			= $xml.find('msgResposta'			).text();
				var idOcorrencia 			= $xml.find('id'					).text();
				var possuiAlertaPendente 	= $xml.find('possuiAlertaPendente'	).text();
				
				sucesso = (sucesso === 'true' || sucesso === '');
				possuiAlertaPendente = (possuiAlertaPendente === 'true');
				
				if (sucesso) {
					
					OCORRENCIA_GERADA = true;
					ID_OCORRENCIA = idOcorrencia;
					
					AtualizarVariaveis(null, null, null);
					AtualizarControles();
					DesabilitarTooltipAcaoProcedimento();
					var textoAnotacao = GerarTextoIniciarOcorrencia();
					CadastrarAnotacaoComTexto(textoAnotacao);
					SuccessCsx_E_TimeOut_8000ms(msgResposta);
					
					if (possuiAlertaPendente)
					{
						AbrirTratamentoAlertasVinculados();
					}
						
				} else {
					WarningCsx_E_TimeOut_8000ms(msgResposta);
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
}

function GerarTextoIniciarOcorrencia() {
	let texto = `<strong>Confirmado Irregularidade</strong> `;

	if (texto.length > 290) {
		const limite = 290 - 3; // para os "..."
		const textoSemTags = texto.replace(/<[^>]+>/g, '');
		const cortado = textoSemTags.substring(0, limite).trim();
		return `<strong>${cortado}...</strong>`;
	}

	return texto;
}

function AbrirTratamentoAlertasVinculados()
{
	var url = 	'/muralha-digital/pages/alerta-tratativa/tratar-alertas-vinculados.jsp?idCadMonitorado=' + ID_CAD_VEIC_MONITORADO + "&idAlerta=" + ID_ALERTA;
	CentralizaWindow(url, 'Alertas Vinculados', window, (screen.width/1.4), (screen.height/1.4));
}

function CentralizaWindow(url, windowName, win, w, h) 
{
	const y = win.top.outerHeight / 2 + win.top.screenY - ( h / 2);
	const x = win.top.outerWidth  / 2 + win.top.screenX - ( w / 2);
	return win.open(url, windowName, `toolbar=yes, location=yes, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, copyhistory=no, width=${w}, height=${h}, top=${y}, left=${x}`);
}

function AtualizarVariaveis(idMotivoDescarte, motivoDescarte, obsDescarteAlerta)
{
	var htmlStatus = "";
	if (DESCARTADO)
	{
		ID_MOTIVO_DESCARTE 	= idMotivoDescarte;
		MOTIVO_DESCARTE 	= motivoDescarte;
		OBS_DESCARTE 		= obsDescarteAlerta;
		
		htmlStatus = "<h6><strong class='text-danger'>DESCARTADO</strong></h6>";
	}
	else if (OCORRENCIA_GERADA)
	{
		htmlStatus = "<h6><strong class='text-success'>IRREGULARIDADE</strong></h6>";
	}

	//Atualizar Status na tela
	if (htmlStatus != "")
	{
		var infoStatusAlerta = document.getElementById("infoStatusAlerta");
		infoStatusAlerta.innerHTML = htmlStatus;
	}
}

function DesabilitarAcoesTela()
{
	document.getElementById("btnAbrirModalAcaoProcedimento").disabled = true;
	document.getElementById("btnAbrirModalAnotacaoContrib").disabled = true;
	document.getElementById("btnAbrirModalDescartar").disabled = true;
	document.getElementById("btnGerarOcorrencia").disabled = true;
	document.getElementById("btnAbrirModalFinalizarOcorrencia").disabled = true;
}

function AtualizarControles()
{
//	if (ORIGEM_CONSULTA_ALERTAS_VINCULADOS)
//	{
//		//Limpar campos modal anotação e descarte
//		LimparCamposAnotacao();
//		
//		//Desabilita controles da tela
//		document.getElementById("btnAbrirModalAcaoProcedimento").disabled = true;
//		document.getElementById("btnAbrirModalDescartar").disabled = true;
//		document.getElementById("btnGerarOcorrencia").disabled = true;
//		document.getElementById("btnAbrirModalFinalizarOcorrencia").disabled = true;
//	}
	if (EH_ALERTA_BLITZ && !DESCARTADO) {
		document.getElementById("btnCriarAbordagem").style.display = "block";
	} else {
		document.getElementById("btnCriarAbordagem").style.display = "none";
	}

	document.getElementById("btnAbrirModalAcaoProcedimento").disabled = true;
	if (ALERTA_VINCULADO && !DESCARTADO && !OCORRENCIA_GERADA)
	{
		//Limpar campos modal anotação e descarte
		LimparCamposDescarte();
		LimparCamposAnotacao();
		
		//Desabilita controles dos modais
		DesabilitarControlesModalAnotacao();
		DesabilitarControlesModalAcao();
		DesabilitarControlesModalDescarte();
		DesabilitarControlesModalFinalizarOcorrencia();
		
		//Desabilita controles da tela
		document.getElementById("btnAbrirModalAcaoProcedimento"		).disabled = true;
		document.getElementById("btnAbrirModalDescartar"			).disabled = true;
		document.getElementById("btnGerarOcorrencia"				).disabled = true;
		document.getElementById("btnAbrirModalFinalizarOcorrencia"	).disabled = true;
		document.getElementById("btnAbrirModalAcaoProcedimento").disabled = true;
	}
	else if(OCORRENCIA_FINALIZADA)
	{
		//Limpar campos modal anotação e descarte
		LimparCamposDescarte();
		LimparCamposAnotacao();
		
		//Desabilita controles dos modais
		DesabilitarControlesModalAnotacao();
		DesabilitarControlesModalAcao();
		DesabilitarControlesModalDescarte();
		DesabilitarControlesModalFinalizarOcorrencia();
		
		//Desabilita controles da tela
		if (window.SUPERVISIONADO === "1") {
			document.getElementById("btnAbrirModalAcaoProcedimento").disabled = false;
		} else {
			document.getElementById("btnAbrirModalAcaoProcedimento").disabled = true;
		}
		document.getElementById("btnAbrirModalDescartar"		).disabled = true;
		document.getElementById("btnGerarOcorrencia"			).disabled = true;
		// document.getElementById("btnAbrirModalFinalizarOcorrencia").disabled = true;
	}
	else if(DESCARTADO)
	{
		//Limpar campos modal anotação
		LimparCamposAnotacao();
		
		//Desabilita controles dos modais
		DesabilitarControlesModalDescarte();
		DesabilitarControlesModalAnotacao();
		
		//Desabilita controles da tela
		document.getElementById("btnAbrirModalAcaoProcedimento"	).disabled = true;
		document.getElementById("btnGerarOcorrencia"			).disabled = true;
	}
	else if(OCORRENCIA_GERADA)
	{
		//Limpar campos modal anotação e descarte
		LimparCamposDescarte();
		LimparCamposAnotacao();		
		
		//Desabilita controles da tela
		document.getElementById("btnAbrirModalDescartar"		).disabled = true;
		document.getElementById("btnGerarOcorrencia"			).disabled = true;

		if (window.SUPERVISIONADO === "1") {
			document.getElementById("btnAbrirModalAcaoProcedimento").disabled = false;
		} else {
			document.getElementById("btnAbrirModalAcaoProcedimento").disabled = true;
		}
	}
	var finalizar = (OCORRENCIA_GERADA || OCORRENCIA_FINALIZADA);
	AtualizarBotaoOcorrencia(finalizar);
}

function AtualizarBotaoOcorrencia(finalizar)
{
	document.getElementById("btnGerarOcorrencia"				).style.display = (finalizar ? "none" : "");
	document.getElementById("btnAbrirModalFinalizarOcorrencia"	).style.display = (finalizar ? "" : "none");
}

function Voltar()
{
	if (ORIGEM_CONSULTA_VISUALIZAR_ALERTAS)
	{
		sessionStorage.setItem("VOLTAR_VISUALIZAR_ALERTAS", ORIGEM_CONSULTA_VISUALIZAR_ALERTAS);
	}
	window.history.back();
}

function AbrePerfilComportamental()
{
	window.open(
        "/muralha-digital/pages/perfil-comportamental/perfil-comportamental.jsp?placa=" 
        + PLACA_PERFIL_COMPORTAMENTAL + "&intervalo=365",
        "_blank" // abre em nova aba
    );   
}

function AbrirLinhaTempoAlarme()
{
	var tituloModalLinhaTempo = "Linha do Tempo - Alarme"
	
	ExecutarPesquisaLinhaTempo(tituloModalLinhaTempo, ID_ALERTA, null);
}

async function AbrirDetalharVeiculoMonitorado()
{	
	// modal editar veiculo monitorado
	
	await AbrirEditarMonitorado(ID_CAD_VEIC_MONITORADO, false, false);
	$('#idMonitoradoHidden').val(ID_CAD_VEIC_MONITORADO); 

    // imprime imediatamente
    console.log('Valor no hidden depois do set:', $('#idMonitoradoHidden').val());
	$('#modalEditarMonitorado').modal('show');
}

function confirmarAssinatura(checkbox) {
    // Pega o estado ATUAL do checkbox (true se foi marcado, false se foi desmarcado)
    const querAssinar = checkbox.checked;

    // Define o texto da pergunta baseado na ação do usuário
    const acaoTexto = querAssinar ? "assinar esta tratativa" : "remover a assinatura desta tratativa";

    Swal.fire({
        title: 'Confirmação',
        text: `Você deseja realmente ${acaoTexto}?`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sim, confirmar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            executaAtualizacaoAssinatura(querAssinar);
        } else {
            checkbox.checked = !querAssinar; 
        }
    });
}
function executaAtualizacaoAssinatura(assinar) {
	var url = urlRoot + "MuralhaDigital/Alerta";
	var dataString = 
		"acao=atualizarAlertaModalAcao" +
		"&idAlerta=" + encodeURIComponent(ID_ALERTA) +
		"&assinado=" + assinar; // Usamos o parâmetro 'assinar' (true ou false)

	$.ajax({
		type: "POST",
		url: url,
		data: dataString,
		dataType: "xml",

		success: function(data, textStatus, jqXHR) {
			var xmlDoc = $.parseXML(jqXHR.responseText);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

            // Opcional: Notificar o usuário do sucesso
            if (sucesso === 'true') {
                Swal.fire(
                    'Sucesso!',
                    'A assinatura da tratativa foi atualizada.',
                    'success'
                );
            } else {
                 Swal.fire(
                    'Erro!',
                    msgResposta || 'Não foi possível atualizar a assinatura.',
                    'error'
                );
            }
		},

		error: function(jqXHR, textStatus, errorThrown) {
			AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', DESC_ERROR_CONTAINER_MODAL_ACAO);
		},
		complete: function(jqXHR, textStatus) {
			ALERTA_ASSINADO = assinar;
		}
	});
}

function abreMapa(){
	ExecutarPesquisaMapaPorIdAlerta(ID_ALERTA)
}

function criarAbordagem() {
    if (!EH_ALERTA_BLITZ) {
        Swal.fire({
            title: 'Atenção',
            text: 'Esta funcionalidade está disponível apenas para alertas de blitz.',
            icon: 'warning',
            confirmButtonText: 'OK'
        });
        return;
    }
    
    verificarSeExisteAbordagem().then(existeAbordagem => {
        if (existeAbordagem) {
            Swal.fire({
                title: 'Abordagem já registrada',
                text: 'Já existe uma abordagem registrada para este alerta.',
                icon: 'warning',
                confirmButtonText: 'OK'
            });
        } else {
            abrirPaginaCriarAbordagem();
        }
    }).catch(error => {
        console.error('Erro ao verificar abordagem:', error);
        abrirPaginaCriarAbordagem();
    });
}

function abrirPaginaCriarAbordagem() {
    // if (!PERMITE_ATENDIMENTO) {
    //     Swal.fire({
    //         icon: 'warning',
    //         title: 'Atenção',
    //         text: 'Este alerta não permite atendimento.',
    //         confirmButtonText: 'OK'
    //     });
    //     return;
    // }
    
    if (DESCARTADO) {
        Swal.fire({
            icon: 'warning',
            title: 'Atenção',
            text: 'Este alerta foi descartado.',
            confirmButtonText: 'OK'
        });
        return;
    }
    
    const url = '/muralha-digital/pages/blitz-abordagem/criar-abordagem.jsp?idAlerta=' + ID_ALERTA;
    window.open(url, '_blank');
}

function verificarSeExisteAbordagem() {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/MuralhaDigital/Blitz',
            type: 'GET',
            data: {
                acao: 'verificarAbordagemPorAlerta',
                idAlerta: ID_ALERTA
            },
            dataType: 'json',
            success: function(response) {
                if (response && response.existe) {
                    resolve(true);
                } else {
                    resolve(false);
                }
            },
            error: function(error) {
                reject(error);
            }
        });
    });
}

function verificarSeEhAlertaBlitz() {
    if (!ID_ALERTA) return;
    
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { 
            acao: 'verificarAlertaBlitz',
            idAlerta: ID_ALERTA 
        },
        dataType: 'json',
        success: function(response) {
            if (response.ehAlertaBlitz) {
                EH_ALERTA_BLITZ = true;
                if (!DESCARTADO && !OCORRENCIA_GERADA && !ALERTA_VINCULADO) {
                    document.getElementById("btnCriarAbordagem").style.display = "block";
                }
            } else {
                EH_ALERTA_BLITZ = false;
                document.getElementById("btnCriarAbordagem").style.display = "none";
            }
        },
        error: function(error) {
            console.error('Erro ao verificar alerta de blitz:', error);
            EH_ALERTA_BLITZ = false;
            document.getElementById("btnCriarAbordagem").style.display = "none";
        }
    });
}