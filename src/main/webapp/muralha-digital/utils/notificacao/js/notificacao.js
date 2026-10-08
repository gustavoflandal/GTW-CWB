var ID_USUARIO 						= '';
var toasts 							= [];
var listaSonoro 					= [];
var ALERTA_SONORO_HABILITADO 		= 0;
var __notificacao_quem_me_importou	= '';
var supervisionado = false;
var tempo_exec = '';
var SOM_ATIVO = false;
var ALARME_CONTINUO = false;
var alertaContinuo = '';
var currentPath = window.location.pathname;
var possui_bo_alerta = '';
var listaVeiculosMonitorarSomenteEste = [];
var TEMPO_MAXIMO_EMISSAO = 1; // Valor padrão em minutos

// Função helper para detectar e tratar redirecionamentos de autenticação
function verificarRedirecionamento(jqXHR, textStatus) {
	// Detecta: status 0 (error/redirect) com resposta HTML, ou 302
	if ((jqXHR.status === 0 || jqXHR.status === 302) && jqXHR.responseText && jqXHR.responseText.indexOf('<') === 0) {
		if (typeof FlutterReauthChannel !== 'undefined') {
			FlutterReauthChannel.postMessage('reauth');
			return true;
		}
	}
	return false;
}

// Interceptador AJAX global para detectar redirecionamentos de sessão expirada (apenas mobile)
$.ajaxSetup({
	statusCode: {
		401: function() {
			if (typeof FlutterReauthChannel !== 'undefined') {
				FlutterReauthChannel.postMessage('reauth');
			}
		}
	}
});

$().ready(function ()
{
	// Aqui são declarados as 3 funções de get do banco para os alertas.
	obterTempoExec();
	obterSomAtivo();
	obterAlertaContinuo();
	obterVeiculosMonitorarSomenteEste();
	obterTempoMaximoEmissao();
	//Tempo de espera para que a jsp menu_simples2.jsp
	//consiga obter os dados da unidade movel do usuário logado
	
	setTimeout
   (
      function()
      {
			ID_USUARIO = usuarioID;			
			Ini();
			iniciaWebSocketAlertas();
			IniciarAlarmeSonoroAuto();
      },
      1000
   );
	
});

// Função para exibir a notificação de DESKTOP 
function exibirNotificacaoDesktop(titulo, idAlerta, placa, data, equipamento) {
    // Função interna para criar e exibir a notificação
    function criarNotificacao() {
        const corpoFormatado = `Placa: ${placa}\n` +
                               `Data: ${data}\n` +
                               `Eqpto: ${equipamento}`;

        const options = {
            body: corpoFormatado,
            icon: "/muralha-digital/assets/images/consilux_grande_transparent.png",
            tag: idAlerta + '_' + Date.now()  
        };

        const notificacao = new Notification(titulo, options);

        notificacao.onclick = function(event) {
            event.preventDefault();
            const url = `/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=${idAlerta}`;
            window.open(url, '_blank');
            notificacao.close();
        };
    }

    // 1. Verifica se o navegador suporta a API de Notificação
    if (!("Notification" in window)) {
        console.error("Este navegador não suporta notificações de desktop.");
        return;
    }

    // 2. Verifica o status da permissão
    if (Notification.permission === "granted") {
        // Se a permissão já foi concedida, cria a notificação
        criarNotificacao();
    } else if (Notification.permission !== "denied") {
        // Se a permissão não foi negada (está como 'default'), solicita ao usuário
        Notification.requestPermission().then(function (permission) {
            // Se o usuário conceder a permissão
            if (permission === "granted") {
                criarNotificacao();
            }
        });
    }
}

// ESTA É A FUNÇÃO PRINCIPAL QUE FOI MODIFICADA PARA CONTER A LÓGICA DE DECISÃO
function onRecebeDadosSockAlertasNova(event) 
{
	try
	{	
		console.log('Nova Lista de Alertas');
		
		var xmlDoc = $.parseXML( event.data );
		var $xml = $(xmlDoc);
		
		var alertas = $xml.find('alertas').find('listaAlertas');		
		//console.log("Alertas: ", xmlDoc)	
	
		alertas.find('alerta').each(function()
		{
			var alerta = $(this);
			
			var idAlerta				= alerta.find('id').text();
			var tipoAlerta				= alerta.find('tipoAlerta').text();
			var placa					= alerta.find('placaCadastro').text();
			var dataAlerta				= alerta.find('dataAlerta').text();
			var equipamento				= alerta.find('equipamento').text();
			// Garante booleano: assume false se tag estiver ausente ou diferente de "true"
			var supervisionadoo 		= alerta.find('supervisionado').text();
			var com_semelhanca			= alerta.find('com_semelhanca').text();
			var com_semelhanca_erros	= alerta.find('com_semelhanca_erros').text();
			var com_semelhanca_desc		= alerta.find('com_semelhanca_desc').text();
			var som						= alerta.find('som').text();
			var usuarioResponsavel		= alerta.find('idUsuarioResponsavel').text();
			
			//console.log('Novo Alerta:: ' + idAlerta + ' Placa: ' + placa + ' Tipo: ' + tipoAlerta + ' supervisionado ' + supervisionadoo + ' semelhante ' + com_semelhanca + ' numero de erros ' + com_semelhanca_erros + ' descrição ' + com_semelhanca_desc);
			console.log('usuario responsavel '+ usuarioResponsavel + 'usuario logado ' + ID_USUARIO);
			if (__notificacao_quem_me_importou != '')
				console.log('Notificações:: Quem importou:: ' + __notificacao_quem_me_importou);				
			
			//Formatando a data completa
			var dataFormat 	= new Date(Date.parse(dataAlerta)).toLocaleDateString("pt-BR");
			var timeFormat 	= new Date(Date.parse(dataAlerta)).toLocaleTimeString("pt-BR");
			var dataFinal 	= dataFormat + ' ' + timeFormat;
			
            // --- LÓGICA DE DECISÃO CENTRALIZADA ---
            // Se a página estiver minimizada ou em outra aba, mostre a notificação de desktop (cinza)
            if (document.visibilityState === 'hidden') {
                console.log("NOTIFICACAO.JS: Página em segundo plano. Exibindo notificação de DESKTOP.");
                exibirNotificacaoDesktop(tipoAlerta, idAlerta, placa, dataFinal, equipamento);
            } 
            // Senão, se a página estiver visível, executa a lógica original para mostrar a notificação na página (amarela)
            else {
                console.log("NOTIFICACAO.JS: Página em primeiro plano. Exibindo notificação IN-PAGE.");
                if (usuarioResponsavel != null && usuarioResponsavel !== "" && usuarioResponsavel != 0) {
    				// tem usuário responsável definido
    				if (usuarioResponsavel == ID_USUARIO) {
    					// é o usuário logado -> monta
    					listaSonoro.push({ alerta: tipoAlerta, sound: som, tempo: tempo_exec });
    					MontarMensagem(idAlerta, placa, dataFinal, equipamento, tipoAlerta, supervisionadoo, com_semelhanca);
    				}
    				// se for diferente do usuário logado, não faz nada
    			} else {
    				// não tem usuário responsável definido -> monta para todos
    				listaSonoro.push({ alerta: tipoAlerta, sound: som, tempo: tempo_exec });
    				MontarMensagem(idAlerta, placa, dataFinal, equipamento, tipoAlerta, supervisionadoo, com_semelhanca);
    			}
            }
		});
		
		//Quando uma tela importa as notificações, 
		//é possivel executar uma função bem especifica
		if (__notificacao_quem_me_importou != '') setTimeout(ProcessoEspecificoPorTela, 10000);
				
		
	}catch(e){
		console.log('Erro:: onRecebeDadosSockAlertasNova()')
		console.log(e);
	}		
	
}

/*
	Essa função abaixo fará a chamada do banco, pegando os dados para
	obter o Som Ativo, e fará uma validação para ver se está na página
	de configuração, caso esteja, pega o valor true ou false do ativoAlerta.
*/
function obterSomAtivo() {
	$.ajax({
		type: "GET",
		url: "/MuralhaDigital/ConfiguracaoTempo",
		dataType: "xml",
		data: {
			acao: "obterSonsAtivo"
		},
		success: function (data) {
			SOM_ATIVO = parseInt($(data).find('configuracao').first().find('valor').text());

			if(currentPath == '/muralha-digital/pages/configuracao-monitoramento/configuracao.jsp') {
				if(SOM_ATIVO == '1') {
					document.getElementById('ativoAlerta').checked = SOM_ATIVO;
				}
				else {
					document.getElementById('ativoAlerta').checked = false;
				}
			}
		}
	})
}

/*
	Essa função abaixo fará a chamada do banco, pegando os dados para
	obter o Alerta Contínuo, e fará uma validação para ver se está na página
	de configuração, caso esteja, pega o valor true ou false do ativoAlertaContinuo.
*/
function obterAlertaContinuo() {
	$.ajax({
		type: "GET",
		url: "/MuralhaDigital/ConfiguracaoTempo",
		dataType: "xml",
		data: {
			acao: "obterAlertaContinuo"
		},
		success: function (data) {
			ALARME_CONTINUO = parseInt($(data).find('configuracao').first().find('valor').text());
			
			if(currentPath == '/muralha-digital/pages/configuracao-monitoramento/configuracao.jsp') {
				if(ALARME_CONTINUO == '1') {
					document.getElementById('ativoAlertaContinuo').checked = ALARME_CONTINUO;
				} else {
					document.getElementById('ativoAlertaContinuo').checked = false;
				}
			}
		}
	})
}

/*
	Essa função abaixo fará a chamada do banco, pegando os dados para
	obter o Tempo do Alerta, e fará uma validação para ver se está na página
	de configuração, caso esteja, pega o valor true ou false do tempoAlerta.
*/
function obterTempoExec() {
	var currentPath = window.location.pathname;
	$.ajax({
		type: "GET",
		url: "/MuralhaDigital/ConfiguracaoTempo",
		dataType: "xml",
		data: {
			acao: "obterConfigTempos"
		},
		success: function (data) {
			tempo_exec = parseInt($(data).find('configuracao').first().find('valor').text());
		
			if(currentPath == '/muralha-digital/pages/configuracao-monitoramento/configuracao.jsp') {
				document.getElementById('tempoAlerta').value = tempo_exec;
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			if (!verificarRedirecionamento(jqXHR, textStatus)) {
				toastr.error("Erro ao salvar configuração de tempo.");
			}
		},
	});
}

/*
	Essa função abaixo fará a chamada do post do banco, enviando os dados
	que o usuário escolheu na página de configuração para salvar no banco.
*/
function configAlertaSonoro() {
	var tempo = document.getElementById('tempoAlerta').value;
	var ativo = document.getElementById('ativoAlerta').checked;
	var alertaContinuo = document.getElementById('ativoAlertaContinuo').checked;
	let configAtualContinua = '';
   let novaConfigAlarmeContinuo = '';
	let configAtual = '';
	var novaConfig = '';
	var novoTempo = '';

	tempo_exec;
	SOM_ATIVO;
	ALARME_CONTINUO;

	if(SOM_ATIVO == '1') {
		configAtual = true;
	} else {
		configAtual = false;
	}

	if(ALARME_CONTINUO == '1') {
		configAtualContinua = true;
	} else {
		configAtualContinua = false;
	}

	if(configAtual != ativo) {
		novaConfig = ativo;
	}

	if(tempo_exec != tempo) {
		novoTempo = tempo;
	}

   if (configAtualContinua != alertaContinuo) {
		novaConfigAlarmeContinuo = alertaContinuo;
   }

	$.ajax({
		type: "POST",
		url: "/MuralhaDigital/ConfiguracaoTempo",
		dataType: "xml",
		data: {
			acao: "configurarAlertaSonoro",
			tempo: novoTempo,
			ativo: novaConfig,
			alarme_continuo: novaConfigAlarmeContinuo
		},
		success: function (data) {
			toastr.success("Configuração de alerta salva com sucesso.");
			SOM_ATIVO = novaConfig ? '1' : '0';
			document.getElementById('ativoAlerta').checked = novaConfig;
			ALARME_CONTINUO = novaConfigAlarmeContinuo ? '1' : '0';
			document.getElementById('ativoAlertaContinuo').checked = novaConfigAlarmeContinuo;
			location.reload();
		},
		error: function () {
			toastr.error("Erro ao salvar configuração de alerta.");
		}	
	})
}

function Ini()
{
	toastr.options = 
	{
		"closeButton": true,
		"debug": true,
		"newestOnTop": true,
		"progressBar": true,
		"positionClass": "toast-bottom-right",
		"preventDuplicates": false,
		"showDuration": "20000",
		"hideDuration": "5000",
		"timeOut":ALARME_CONTINUO === 1 ? 0 : tempo_exec * 1000,
		"extendedTimeOut": "0",
		"showEasing": "swing",
		"hideEasing": "linear",
		"showMethod": "fadeIn",
		"hideMethod": "fadeOut",
		"tapToDismiss": false,
		"onclick": function() {
			PauseAlarme();
		}
	}	
}

function IniciarAlarmeSonoroAuto()
{
	let interacaoDetectada = false;

	if(SOM_ATIVO == '1'){
		document.addEventListener('mousemove', ativaAlarmeComInteracao);
		document.addEventListener('click', ativaAlarmeComInteracao);
		document.addEventListener('scroll', ativaAlarmeComInteracao);
	
		// Timer de 10 segundos para verificar se houve interação
		const timeoutInteracao = setTimeout(function () {
			if (!interacaoDetectada) {
				Swal.fire("O alarme sonoro está sendo ativado.");
			}
		}, 10000);

		function ativaAlarmeComInteracao() {
			if (interacaoDetectada) return; // evita duplicidade

			interacaoDetectada = true;
			clearTimeout(timeoutInteracao); // cancela o alert
			HabilitaAlarmeSonoro();			

			// Remove os ouvintes após primeira interação
			document.removeEventListener('mousemove', ativaAlarmeComInteracao);
			document.removeEventListener('click', ativaAlarmeComInteracao);
			document.removeEventListener('scroll', ativaAlarmeComInteracao);
		}

	}
}

function onOpenSockAlertas(event) 
{

	//Tempo de espera para 
	//consiga obter os dados do usuário logado	
	setTimeout
	(
		function()
		{
			if (ID_USUARIO != '')
				socketAlertas.send("Cliente_Consilux_ALERTA-NOTIFICACAO-USUARIOID-" + ID_USUARIO);
			else
				alert('Falha:: Id de usuário não identificado ao inicar webSocket de Alertas');
		},
		2000
	);
}

function onErrorSockAlertas(event) 
{
	console.error("Erro no WebSocket de Alertas:", event);
	//alert("onErrorSockAlertas(): " + event.data);
}

function onCloseSocketAlertas(event)
{
	var reason;
	if (event.code == 1000)
		reason = "Normal closure, meaning that the purpose for which the connection was established has been fulfilled.";
	else if(event.code == 1001)
		reason = "An endpoint is \"going away\", such as a server going down or a browser having navigated away from a page.";
	else if(event.code == 1002)
		reason = "An endpoint is terminating the connection due to a protocol error";
	else if(event.code == 1003)
		reason = "An endpoint is terminating the connection because it has received a type of data it cannot accept (e.g., an endpoint that understands only text data MAY send this if it receives a binary message).";
	else if(event.code == 1004)
		reason = "Reserved. The specific meaning might be defined in the future.";
	else if(event.code == 1005)
		reason = "No status code was actually present.";
	else if(event.code == 1006)
		reason = "The connection was closed abnormally, e.g., without sending or receiving a Close control frame";
	else if(event.code == 1007)
		reason = "An endpoint is terminating the connection because it has received data within a message that was not consistent with the type of the message (e.g., non-UTF-8 [https://www.rfc-editor.org/rfc/rfc3629] data within a text message).";
	else if(event.code == 1008)
		reason = "An endpoint is terminating the connection because it has received a message that \"violates its policy\". This reason is given either if there is no other sutible reason, or if there is a need to hide specific details about the policy.";
	else if(event.code == 1009)
		reason = "An endpoint is terminating the connection because it has received a message that is too big for it to process.";
    else if(event.code == 1010) // Note that this status code is not used by the server, because it can fail the WebSocket handshake instead.
		reason = "An endpoint (client) is terminating the connection because it has expected the server to negotiate one or more extension, but the server didn't return them in the response message of the WebSocket handshake. <br /> Specifically, the extensions that are needed are: " + event.reason;
	else if(event.code == 1011)
		reason = "A server is terminating the connection because it encountered an unexpected condition that prevented it from fulfilling the request.";
	else if(event.code == 1015)
		reason = "The connection was closed due to a failure to perform a TLS handshake (e.g., the server certificate can't be verified).";
	else
		reason = "Unknown reason";

//    console.log("onCloseSocketAlertas() - event: " + event);
//    console.log("onCloseSocketAlertas() - event.code: " + event.code);
//    console.log("onCloseSocketAlertas() - reason: " + reason);

	if (event.code == 1006)
	{
		console.log("Web Socket de Alertas encerrado prematuramente. Iniciando novamente...")
		iniciaWebSocketAlertas();
	}
}



var mensagem 				= '';
var tituloAlerta 			= '';
var comSemelhanca   		= '';
var supervisionadoBanco 	= '';

/**
 * Monta o conteúdo visual da notificação do alerta e inclui sua miniatura.
 * @param {string} id Identificador do alerta usado para localizar a imagem.
 * @param {string} placa Placa cadastrada para o alerta.
 * @param {string} data Data formatada do alerta.
 * @param {string} equipamento Identificação do equipamento.
 * @param {string} tipoAlarme Título e tipo visual do alerta.
 * @param {string|number} supervisionadoo Indica se o alerta é supervisionado.
 * @param {string|number} com_semelhanca Indica o grau de semelhança do alerta.
 * @param {number} [minutos] Minutos entre a passagem e a emissão do alerta.
 * @param {number} [segundos] Segundos entre a passagem e a emissão do alerta.
 * @returns {void}
 */
function MontarMensagem(id, placa, data, equipamento, tipoAlarme, supervisionadoo, com_semelhanca, minutos, segundos)
{
    var tempoTexto = '';
    
    if (minutos === 0 && segundos === 0) {
        tempoTexto = 'imediatamente';
    } else if (minutos === 0) {
        tempoTexto = segundos + ' segundo' + (segundos !== 1 ? 's' : '');
    } else if (segundos === 0) {
        tempoTexto = minutos + ' minuto' + (minutos !== 1 ? 's' : '');
    } else {
        tempoTexto = minutos + ' minuto' + (minutos !== 1 ? 's' : '') + ' e ' + segundos + ' segundo' + (segundos !== 1 ? 's' : '');
    }
    
    var miniatura = '';
    if (id) {
        var urlImagem = "/MuralhaDigital/Veiculo/Imagem?acao=ImagemByIdAlerta&idAlerta=" + encodeURIComponent(id);
        miniatura = "<img class='notificacao-alerta-miniatura' src='" + urlImagem + "' alt='Miniatura do veículo' onerror=\"this.style.display='none'\" />";
    }

    var msg = "<div>" +
        miniatura +
        "		<div   id='placa_" 	+ id + 	"'><strong><small> 	Placa: " + 	placa + " 		</small></strong></div>" +
        "		<div   id='data_" 	+ id + 	"'><small> 			Data: " + 	data + "  		</small></div>" +
        "		<div   id='eqpto_" 	+ id + 	"'><small> 			Eqpto: " + 	equipamento + " </small></div>" +
        "		<div   id='tempo_" 	+ id + 	"'><small style='font-size: 10px; color: #999;'>Alerta emitido " + tempoTexto + " após a passagem</small></div>" +
        "     <div class='row justify-content-center margin-button-alert'>" +
        "			<button id='" 	+ id + 	"' type='button' class='btn btn-dark btn-sm' onclick='NotificacaoDetalhada(this)'>Validar Alerta</button>" +
        "		</div>" +
        "</div>";
		
	mensagem 		= msg;
	tituloAlerta 	= tipoAlarme;
	comSemelhanca	= com_semelhanca
	supervisionadoBanco = supervisionadoo
	// Aguarda o toast ser adicionado ao DOM
		setTimeout(function () {
		// Seleciona o botão de fechar do último toast e adiciona o evento
			let closeBtn = document.querySelector('.toast .toast-close-button');
			if (closeBtn) {
				closeBtn.addEventListener('click', function () {
					PauseAlarme();
				});
			}
		}, 100);

	ConfiguraAlerta();
}

function obterCorPorTipoAlerta(tituloAlerta) {
	const mapa = {
		'Veículo Roubado': 'orange',
		'Veículo Furtado': 'orange',
		'Veículo Clonado': 'warning',
		'Transporte Clandestino': 'info',
		'Veículo Suspeito de Sequestro Relâmpago': 'success',
		'Veículo Suspeito de Roubo à Banco': 'success',
		'Veiculo Monitorado': 'success',
		'Veículo com Atraso de Licenciamento': 'info'
	};

	return mapa[tituloAlerta];
}

function ConfiguraAlerta()
{
	if (supervisionado === 1) {
		showAlertaVermelho();
		VerificaAlertaHabilitado(tituloAlerta);
		return;
	}

	const cor = obterCorPorTipoAlerta(tituloAlerta);

	/*
		Aqui abaixo foi criado a validação para saber se o alerta possui BO e tem BO registrado, caso ele possua BO porém não tenha registrado,
		cairá no if === 'NAO' mostrando o alerta destacado, caso possua BO e tenha registrado OU não possua BO, cairá no else mostrando o alerta normal.
	*/
	if (cor) {
		showAlerta(mensagem, cor, tituloAlerta);
		VerificaAlertaHabilitado(tituloAlerta);
	}
}

function showAlertaVerde() 		{ showAlerta(mensagem, "success", tituloAlerta); }
function showAlertaVermelho() 	{ showAlerta(mensagem, "error", tituloAlerta); }
function showAlertaAmarelo() 	{ showAlerta(mensagem, "warning", tituloAlerta); }
function showAlertaInfo() 		{ showAlerta(mensagem, "info", tituloAlerta); }
function showAlertaOrange() 	{ showAlerta(mensagem, "orange", tituloAlerta); }
function showAlertaPurple() 	{ showAlerta(mensagem, "purple", tituloAlerta); }
function showAlertaBrown() 	    { showAlerta(mensagem, "brown", tituloAlerta); }
function showAlertaPink() 	    { showAlerta(mensagem, "pink", tituloAlerta); }

function showAlerta(msg, tipoAlertaNotificacao, tituloAlerta) {
	let toastClass = 'toast';

	/*
		Aqui abaixo foi criado a validação para o tipo do alerta, caso o alerta for de uma respectiva cor (digitado acima) ele será chamado,
		dentro do switch case é feito a validação se o alerta possui_bo_alerta possui o alerta porém não está registrado, caso seja 'NAO' (não registrado),
		o alerta terá uma borda destacada piscando e uma fonte maior, caso ele possua o alerta e esteja registrado OU não possua alerta, mostrará somente a cor do alerta normal.
	*/
	switch (tipoAlertaNotificacao) {
		case 'orange':
			toastClass += ' toast-orange';

			if(possui_bo_alerta === 'NAO') {
				toastClass += ' toast-dark-red';
			}

			tipoAlertaNotificacao = 'info';
			break;
		case 'purple':
			toastClass += ' toast-purple';

			if(possui_bo_alerta === 'NAO') {
				toastClass += ' toast-dark-red';
			}

			tipoAlertaNotificacao = 'info';
			break;
		case 'brown':
			toastClass += ' toast-brown';

			if(possui_bo_alerta === 'NAO') {
				toastClass += ' toast-dark-red';
			}

			tipoAlertaNotificacao = 'info';
			break;
		case 'pink':
			toastClass += ' toast-pink';

			if(possui_bo_alerta === 'NAO') {
				toastClass += ' toast-dark-red';
			}

			tipoAlertaNotificacao = 'info';
			break;
		case 'warning':
			toastClass += ' toast-warning';

			if(possui_bo_alerta === 'NAO') {
				toastClass += ' toast-dark-red';
			}

			break;
		case 'success':
			toastClass += ' toast-success';

			if(possui_bo_alerta === 'NAO') {
				toastClass += ' toast-dark-red';
			}

			break;
		case 'error':
			toastClass += ' toast-error';

			if(possui_bo_alerta === 'NAO') {
				toastClass += ' toast-dark-red';
			}

			break;
		case 'info':
		default:
			toastClass += ' toast-info';

			if(possui_bo_alerta === 'NAO') {
				toastClass += ' toast-dark-red';
			}
			
		break;
   }

   const tt = toastr[tipoAlertaNotificacao](msg, tituloAlerta, { toastClass });
   toasts.push(tt);
}

function NotificacaoDetalhada(obj) {
	verificarQuestionarioObrigatorio(obj.id, function(questionarioObrigatorio) {
		console.log("questionarioObrigatorio: ", questionarioObrigatorio);

		if (questionarioObrigatorio) {
			return; // não abre a nova aba
		} else {
			var url = '/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=' + obj.id;
			window.open(url, '_blank').focus();
		}
	});
}

function VerificaAlertaHabilitado(tituloAlerta)
{
	
	//Usuario não aceitou
	if ( ALERTA_SONORO_HABILITADO == 1) return;
	
	//A primeira vez que abre
	//ou se usuario já aceitou
	if ( ALERTA_SONORO_HABILITADO == 0){
		//HabilitaAlertaSonoroAutomatico();
	}else{
		AlarmeSonoro(tituloAlerta);
	}
	
}

function HabilitaAlarmeSonoro()
{	
	ALERTA_SONORO_HABILITADO = 2;
	console.log('Alerta Sonoro habilitado pelo usuário (Automatico)');
}

/*
	Essa função abaixo faz tocar o alarme sonoro, com base no tipo de alerta
	que chegou, se tiver ativo, ele entra no if, se for supervisionado toca
	um som diferente e caso for outros tipos, ele entra num for, fazendo a
	validação se é contínuo ou não.
*/
function AlarmeSonoro(tituloAlerta)
{	
	if(SOM_ATIVO) {
		console.log(SOM_ATIVO)
		const alarme = document.getElementById('alarme_sonoro');
		alarme.muted = false;

		if (supervisionado === true) {
			const item = listaSonoro.find(x => x.alerta === 'Supervisionado');
			if (item) {
				alarme.src = item.sound;
				//alarme.currentTime = item.tempo;
				alarme.play();
				if(alarme < tempo_exec * 1000)
				{
					alarme.loop = true;
				}
				setTimeout(PauseAlarme, tempo_exec * 1000);
				return;
			}
		}

		for (var i = 0; i < listaSonoro.length; i++) {
			if(alertaContinuo == 1) {
				alarme.loop = true;
				alarme.src = listaSonoro[i].sound;
				alarme.play();
				return;
			}
			if(listaSonoro[i].alerta == tituloAlerta)
			{
				alarme.src = listaSonoro[i].sound;

				alarme.onloadedmetadata = function() {
					const duracaoAudio = alarme.duration;

					if(duracaoAudio < tempo_exec * 1000)
					{
						alarme.loop = true;
					} else {
						alarme.loop = false;
					}
					alarme.play();
				}
				setTimeout(PauseAlarme, tempo_exec * 1000);
			}
		}
	}
}

function PauseAlarme()
{
	document.getElementById('alarme_sonoro').pause();
}

function ProcessoEspecificoPorTela()
{
	if (typeof __notificacao_quem_me_importou == "undefined") return;
	
	//Tela de Consulta de AlertaOcorrencia
	//atualiza toda a listagem, refazem a consulta 
	//com filtros da tela
	if (__notificacao_quem_me_importou == 'consulta-alerta-ocorrencia') AtualizaListagem();
}

function obterVeiculosMonitorarSomenteEste() {
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/Monitorado",
        dataType: "json",
        data: {
            acao: "obterIdsMonitorarSomenteEste"
        },
        success: function (data) {
			if (data.length === 0) {
				listaVeiculosMonitorarSomenteEste = [];
			}
			else {
				listaVeiculosMonitorarSomenteEste = [...data];
			}

			// console.log("Veículos para monitorar somente este: ", listaVeiculosMonitorarSomenteEste);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            if (!verificarRedirecionamento(jqXHR, textStatus)) {
                console.error("Erro ao carregar veículos para monitorar somente este.");
            }
        }
    });
}

function onRecebeDadosSockAlertas(event) 
{
    try
    {	
        console.log('Nova Lista de Alertas');
		obterVeiculosMonitorarSomenteEste();
		obterTempoMaximoEmissao();
        
        var xmlDoc = $.parseXML( event.data );
        var $xml = $(xmlDoc);
        
        var alertas = $xml.find('alertas').find('listaAlertas');		
        //console.log("Alertas: ", xmlDoc)	
    
        alertas.find('alerta').each(function()
        {
            var alerta = $(this);
            
            var idAlerta				= alerta.find('id').text();
            var tipoAlerta				= alerta.find('tipoAlerta').text();
            var placa					= alerta.find('placaCadastro').text();
            var dataAlerta				= alerta.find('dataAlerta').text();
            var dataPassagem			= alerta.find('dataPassagem').text();
            var equipamento				= alerta.find('equipamento').text();
            var supervisionadoo 		= alerta.find('supervisionado').text();
            var com_semelhanca			= alerta.find('com_semelhanca').text();
            var com_semelhanca_erros	= alerta.find('com_semelhanca_erros').text();
            var com_semelhanca_desc		= alerta.find('com_semelhanca_desc').text();
            var som						= alerta.find('som').text();
            var usuarioResponsavel		= alerta.find('idUsuarioResponsavel').text();
            var idVeiculoMonitorado		= alerta.find('idVeiculoMonitorado').text();
            possui_bo_alerta		    = alerta.find('possui_bo_alerta').text();
            
            console.log('Novo Alerta:: ' + idAlerta + ' Placa: ' + placa + ' Tipo: ' + tipoAlerta + ' supervisionado ' + supervisionadoo + ' semelhante ' + com_semelhanca + ' numero de erros ' + com_semelhanca_erros + ' descrição ' + com_semelhanca_desc + 'possui bo ' + possui_bo_alerta);
            console.log('usuario responsavel '+ usuarioResponsavel + 'usuario logado ' + ID_USUARIO);
            if (__notificacao_quem_me_importou != '')
                console.log('Notificações:: Quem importou:: ' + __notificacao_quem_me_importou);				
            
            //Formatando a data completa
            var dataFormat 	= new Date(Date.parse(dataAlerta)).toLocaleDateString("pt-BR");
            var timeFormat 	= new Date(Date.parse(dataAlerta)).toLocaleTimeString("pt-BR");
            var dataFinal 	= dataFormat + ' ' + timeFormat;
            
            // --- NOVA VALIDAÇÃO: TEMPO MÁXIMO DE EMISSÃO ---
            var dataPassagemObj = new Date(Date.parse(dataPassagem));
			var dataAlertaObj = new Date(Date.parse(dataAlerta));
			var diferencaMs = dataAlertaObj - dataPassagemObj;
			var diferencaMinutos = Math.floor(diferencaMs / (1000 * 60));
			var diferencaSegundos = Math.floor((diferencaMs % (1000 * 60)) / 1000);
			var diferencaTotalMinutos = diferencaMs / (1000 * 60);
            
            console.log('Tempo entre passagem e alerta: ' + diferencaMinutos + ' minutos | Configuração: ' + TEMPO_MAXIMO_EMISSAO + ' minutos');

			// Se passou mais tempo que o configurado, não exibe o alerta
			if (diferencaTotalMinutos > TEMPO_MAXIMO_EMISSAO) {
				console.log('Alerta descartado: tempo de emissão excedido (' + diferencaMinutos + ' minutos e ' + diferencaSegundos + ' segundos)');
				return;
			}
            
            // --- LÓGICA DE DECISÃO CENTRALIZADA ---
            // Se a página estiver minimizada ou em outra aba, mostra a notificação de desktop
            if (document.visibilityState === 'hidden') {
                console.log("NOTIFICACAO.JS: Página em segundo plano. Exibindo notificação de DESKTOP.");
                // Chamando a função correta para notificações de desktop
                exibirNotificacaoDesktop(tipoAlerta, idAlerta, placa, dataFinal, equipamento);
            } 
            // Senão, se a página estiver visível, executa a lógica para mostrar a notificação na página
            else {
                console.log("NOTIFICACAO.JS: Página em primeiro plano. Exibindo notificação IN-PAGE.");
                
                var deveExibir = false;

                // 1. Lógica do usuário responsável
                if (usuarioResponsavel != null && usuarioResponsavel !== "" && usuarioResponsavel != 0) {
                    // tem usuário responsável definido
                    if (usuarioResponsavel == ID_USUARIO) {
                        // é o usuário logado -> pode exibir
                        deveExibir = true;
                    }
                    // se for diferente do usuário logado, não faz nada
                } else {
                    // não tem usuário responsável definido -> todos podem ver
                    deveExibir = true;
                }

                // 2. Se deve exibir, aplica a lógica do monitorar_somente_este
                if (deveExibir) {
                    // Supervisionados sempre são exibidos
                    if (supervisionadoo === 'true' || supervisionadoo === '1') {
                        listaSonoro.push({ alerta: tipoAlerta, sound: som, tempo: tempo_exec });
                        MontarMensagem(idAlerta, placa, dataFinal, equipamento, tipoAlerta, supervisionadoo, com_semelhanca, diferencaMinutos, diferencaSegundos);
                    } 
                    // Não supervisionados: verifica a flag monitorar_somente_este
                    else {
                        if (listaVeiculosMonitorarSomenteEste.length === 0) {
                            // Usuário não tem a flag ativa → exibe todos
                            listaSonoro.push({ alerta: tipoAlerta, sound: som, tempo: tempo_exec });
                            MontarMensagem(idAlerta, placa, dataFinal, equipamento, tipoAlerta, supervisionadoo, com_semelhanca, diferencaMinutos, diferencaSegundos);
                        } else {
                            // Usuário tem a flag ativa → exibe apenas veículos da lista
                            if (idVeiculoMonitorado && listaVeiculosMonitorarSomenteEste.includes(idVeiculoMonitorado)) {
                                listaSonoro.push({ alerta: tipoAlerta, sound: som, tempo: tempo_exec });
                                MontarMensagem(idAlerta, placa, dataFinal, equipamento, tipoAlerta, supervisionadoo, com_semelhanca, diferencaMinutos, diferencaSegundos);
                            }
                        }
                    }
                }
            }
        });
        
        //Quando uma tela importa as notificações, 
        //é possivel executar uma função bem especifica
        if (__notificacao_quem_me_importou != '') setTimeout(ProcessoEspecificoPorTela, 10000); 
    }catch(e){
        console.log('Erro:: onRecebeDadosSockAlertas()')
        console.log(e);
    }		
    
}

function obterTempoMaximoEmissao() {
	$.ajax({
		type: "GET",
		url: "/MuralhaDigital/ConfiguracaoTempo",
		dataType: "xml",
		data: {
			acao: "obterTempoMaximoEmissao"
		},
		success: function (data) {
			var tempo = $(data).find('configuracao').first().find('valor').text();
			if (tempo) {
				TEMPO_MAXIMO_EMISSAO = parseInt(tempo);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			if (!verificarRedirecionamento(jqXHR, textStatus)) {
				console.error("Erro ao carregar tempo máximo de emissão.");
			}
		}
	});
}