var MSG_CONTAINER_MODAL_FINALIZAR_OCORRENCIA = "#msg_container_modal_finalizar_ocorrencia";
var COMBO_STATUS_FINALIZACAO_OCORRENCIA_CARREGADO = false;

function AbrirModalFinalizarOcorrencia()
{
	PopulaModalFinalizarOcorrencia();
}

function ObterStatusFinalizacaoOcorrencia()
{
	var dataString = "acao=obterListaStatusFinalizacao" +
						"&tipoOcorrencia=" + ID_TIPO_ALERTA;
	
	var urlPesquisa = urlRoot + "MuralhaDigital/Ocorrencia/Status";
        
    $.ajax({
        type: 		"GET",
        url: 		urlPesquisa,
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
				CarregaComboStatusFinalizacao( jqXHR.responseText );
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta, MSG_CONTAINER_MODAL_FINALIZAR_OCORRENCIA);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', MSG_CONTAINER_MODAL_FINALIZAR_OCORRENCIA);
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });      
}

function CarregaComboStatusFinalizacao(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);

	$xml.find('StatusOcorrencia').each(function()
	{
		
		var $item = $(this);
		
		var id = $item.find('id').text();
		var tipo = $item.find('descricao').text();

		$('#selStatusFinalizacaoOcorrenciaCad').append($('<option>', {
		    value: id,
		    text: tipo
		}));
				
	});
}




async function FinalizarOcorrencia()
{
	try
	{
		$("body").addClass("loading");
		
		const permitirFinalizar = await verificarPermissaoFinalizarOcorrencia();
		let mensagemFechamento = "";
		if(!permitirFinalizar)
		{
			mensagemFechamento += "Usuário Logado não tem permissão para Finalizar Tratativa de alerta <br>";
		}
		const alertaAssinado = await ObterAssinadoAlerta();
		if(!alertaAssinado)
		{
			mensagemFechamento += "Tratativa de alerta não pode ser finalizado porque não foi Assinado em 'AÇÕES E PROCEDIMENTOS'";
		}
		
		if(mensagemFechamento.length > 0)			
		{
			WarningModalCsx_E_TimeOut_8000ms_v2(mensagemFechamento, MSG_CONTAINER_MODAL_FINALIZAR_OCORRENCIA);
			$("body").removeClass("loading");
			return
		}
			
		var idStatusFinalizarOcorrencia = document.getElementById("selStatusFinalizacaoOcorrenciaCad").value;
		var obsFinalizarOcorrencia = document.getElementById("obsFinalizacaoOcorrenciaCad").value;
		var statusFinalizarOcorrencia = $("#selStatusFinalizacaoOcorrenciaCad option:selected").text();
		
		var dataString = "acao=finalizarOcorrencia" +
							"&idOcorrencia=" + ID_OCORRENCIA +
							"&idTipoOcorrencia=" + ID_TIPO_ALERTA +
							"&idStatusOcorrencia=" + idStatusFinalizarOcorrencia +
							"&obsFinalizarOcorrencia=" + obsFinalizarOcorrencia;
	        
		var url = urlRoot + "MuralhaDigital/Ocorrencia";
	
		if (confirm("Deseja realmente finalizar a ocorrência?"))
		{
		    $.ajax({
		        type: 		"POST",
		        url: 		url,
		        data: 		dataString,
		        dataType:	"xml",
		           
		        success: 	function( data, textStatus, jqXHR) 
		        {
		        	var event = jqXHR.responseText;
		        	var xmlDoc = $.parseXML( event );
					var $xml = $(xmlDoc);
		    			
					var sucesso = $xml.find('sucesso').text();
					var msgResposta = $xml.find('msgResposta').text();
					
					sucesso = (sucesso === 'true' || sucesso === '');
		
					if (sucesso) {
						
						OCORRENCIA_FINALIZADA = true;
						AtualizarVariaveisFinalizarOcorrencia(idStatusFinalizarOcorrencia, statusFinalizarOcorrencia, obsFinalizarOcorrencia);
						AtualizarControles();
						
						SuccessModalCsx_E_TimeOut_8000ms_v2(msgResposta, MSG_CONTAINER_MODAL_FINALIZAR_OCORRENCIA);
						var textoAnotacao = GerarTextoFinalizarOcorrencia(statusFinalizarOcorrencia, obsFinalizarOcorrencia);
						CadastrarAnotacaoComTexto(textoAnotacao);
						// fecharModalFinalizarOcorrencia();
												
					} else {
						WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, MSG_CONTAINER_MODAL_FINALIZAR_OCORRENCIA);
					}
		        },
		
		        error: 		function(jqXHR, textStatus, errorThrown)
		        {
		        	AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', MSG_CONTAINER_MODAL_FINALIZAR_OCORRENCIA);
		        },
		        beforeSend: function(jqXHR, settings){},
		        complete: 	function(jqXHR, textStatus){}
		 
		    });
		}
	}
	catch (ex)
	{
		AlertCsx_E_TimeOut_8000ms('Erro ao carregar a página!!');
	}
	finally
	{
		$("body").removeClass("loading");
	}
}

function GerarTextoFinalizarOcorrencia(statusFinalizarOcorrencia, obsFinalizarOcorrencia) {
	let texto = `<strong>Alerta Finalizado:</strong> `;
	texto += `Status: <em>${statusFinalizarOcorrencia}</em>`;

	if (obsFinalizarOcorrencia && obsFinalizarOcorrencia.trim() !== "") {
		texto += ` | Obs: ${obsFinalizarOcorrencia.trim()}`;
	}

	if (texto.length > 290) {
		const limite = 290 - 3; // para os "..."
		const textoSemTags = texto.replace(/<[^>]+>/g, '');
		const cortado = textoSemTags.substring(0, limite).trim();
		return `<strong>${cortado}...</strong>`;
	}

	return texto;
}

function AtualizarVariaveisFinalizarOcorrencia(idStatusFinalizarOcorrencia, statusFinalizarOcorrencia, obsFinalizarOcorrencia)
{
	if (OCORRENCIA_FINALIZADA)
	{
		ID_STATUS_FINALIZAR_OCORRENCIA = idStatusFinalizarOcorrencia;
		STATUS_FINALIZAR_OCORRENCIA = statusFinalizarOcorrencia;
		OBS_FINALIZAR_OCORRENCIA = obsFinalizarOcorrencia;
	}
}

function DesabilitarControlesModalFinalizarOcorrencia()
{
	if(OCORRENCIA_FINALIZADA)
	{
		document.getElementById("btnFinalizarOcorrencia").disabled 				= true;
		document.getElementById("btnLimparCamposFinalizarOcorrencia").disabled	= true;
		document.getElementById("selStatusFinalizacaoOcorrenciaCad").disabled	= true;
		document.getElementById("obsFinalizacaoOcorrenciaCad").disabled			= true;
	}
}

async function PopulaModalFinalizarOcorrencia()
{
	if (!(ID_STATUS_FINALIZAR_OCORRENCIA === ""))
	{
		document.getElementById("selStatusFinalizacaoOcorrenciaCad").value = ID_STATUS_FINALIZAR_OCORRENCIA;
	}
	
	let podeFinalizar = true;
	let mensagemFechamento = "";
	document.getElementById("btnFinalizarOcorrencia").disabled = podeFinalizar;
	// Verifica se o usuário tem permissão
	const permitirFinalizar = await verificarPermissaoFinalizarOcorrencia();
	if (!permitirFinalizar) {
		mensagemFechamento += "Usuário logado não tem permissão para finalizar tratativa de alerta.<br>";
		podeFinalizar = false;
	}

	// Verifica se o alerta foi assinado
	const alertaAssinado = await ObterAssinadoAlerta(); // corrigido nome da variável
	if (!alertaAssinado) {
		mensagemFechamento += "Tratativa de alerta não foi assinada.";
		podeFinalizar = false;
	}

	// Atualiza o estado do botão
	document.getElementById("btnFinalizarOcorrencia").disabled = !podeFinalizar;

	if(mensagemFechamento.length > 0)			
	{
		WarningModalCsx_E_TimeOut_8000ms_v2(mensagemFechamento, MSG_CONTAINER_MODAL_FINALIZAR_OCORRENCIA);
	}

	document.getElementById("obsFinalizacaoOcorrenciaCad").value = OBS_FINALIZAR_OCORRENCIA;
	
	DesabilitarControlesModalFinalizarOcorrencia();
}

function LimparCamposFinalizarOcorrencia()
{
	var idStatusFinalizacaoOcorrencia = document.getElementById("selStatusFinalizacaoOcorrenciaCad");
	var obsFinalizacaoOcorrencia = document.getElementById("obsFinalizacaoOcorrenciaCad");
	
	if (idStatusFinalizacaoOcorrencia)
		idStatusFinalizacaoOcorrencia.value = 0;
		
	if (obsFinalizacaoOcorrencia)
		obsFinalizacaoOcorrencia.value = "";
	
}

function fecharModalFinalizarOcorrencia()
{
	$('#modalOcorrenciaFinalizar').modal('hide');
}

async function verificarPermissaoFinalizarOcorrencia() {
	try {
		const xml = await $.ajax({
			type: "GET",
			url: "/MuralhaDigital/PermissoesFuncionalidade",
			data: {
				acao: "verificarPermissaoGrupo"
			},
			dataType: "xml"
		});

		const permitido = $(xml).find("permitido").text().toLowerCase() === "true";
		return permitido;

	} catch (error) {
		console.error("Erro ao verificar permissão de grupo!", error);
		return false; // segurança: nega se erro
	}
}

async function ObterAssinadoAlerta() {
	try {
		const xmlDoc = await $.ajax({
			type: "GET",
			url: urlRoot + "MuralhaDigital/Alerta",
			data: {
				acao: "obterAssinadoAlertaPorId",
				idAlerta: ID_ALERTA
			},
			dataType: "xml"
		});

		const permitido = $(xmlDoc).find("alerta > assinado").text().toLowerCase() === "true";
		return permitido;

	} catch (error) {
		AlertCsx_E_TimeOut_8000ms("Erro ao processar requisição ao servidor!!");
		return false;
	}
}