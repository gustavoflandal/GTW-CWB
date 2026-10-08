var DESC_ERROR_CONTAINER_MODAL_ACAO = "#error_container_modal_acao";
var ALERTA_ASSINADO = false;
function ObterGruposEmail()
{
	var dataString = "acao=obterGruposEmail" +
						"&tipoRegistro=" + ID_TIPO_REGISTRO +
						"&tipoAlertaOcorrencia=" + ID_TIPO_ALERTA;
						
	var url = urlRoot + "MuralhaDigital/GrupoNotificacao";

		$.ajax({
		type: 		"GET",
		url: 		url,
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
				carregaComboGruposEmail( jqXHR.responseText );
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

function carregaComboGruposEmail(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	$('#selAcaoGrupoEmailCad').empty();
	
	$xml.find('GrupoNotificacao').each(function()
	{
		var $item = $(this);
		
		var idGrupo = $item.find('idGrupo').text();
		var grupo = $item.find('grupo').text();
		
		$('#selAcaoGrupoEmailCad').append($('<option>', {
			value: idGrupo,
			text: grupo
		}));
	});
	
	$('#selAcaoGrupoEmailCad').selectpicker('refresh');
}

function ObterGruposSMS()
{
	var dataString = "acao=obterGruposSMS" +
						"&tipoRegistro=" + ID_TIPO_REGISTRO +
						"&tipoAlertaOcorrencia=" + ID_TIPO_ALERTA;
						
	var url = urlRoot + "MuralhaDigital/GrupoNotificacao";
		
	$.ajax({
		type: 		"GET",
		url: 		url,
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
				carregaComboGruposSMS( jqXHR.responseText );
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

function carregaComboGruposSMS(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	$('#selAcaoGrupoSmsCad').empty();
	
	$xml.find('GrupoNotificacao').each(function()
	{
		var $item = $(this);
		
		var idGrupo = $item.find('idGrupo').text();
		var grupo = $item.find('grupo').text();
		
		$('#selAcaoGrupoSmsCad').append($('<option>', {
			value: idGrupo,
			text: grupo
		}));
	});
	
	$('#selAcaoGrupoSmsCad').selectpicker('refresh');
}

function SalvarConfigNotificacao()
{
	AtualizaConfigNotificacao();
}

function AtualizaConfigNotificacao()
{
	var gruposEmail = $('#selAcaoGrupoEmailCad').val();
	var gruposSMS = $('#selAcaoGrupoSmsCad').val();
	var permiteAtendimento = document.getElementById("chkPermitirAtendimento").checked;
	var dataString = "acao=salvarConfigAcaoProcedimento" +
						"&idOcorrencia=" + ID_OCORRENCIA +
						"&gruposEmail=" + gruposEmail +
						"&gruposSMS=" + gruposSMS +
						"&permiteAtendimento=" + permiteAtendimento +
						"&permiteAlterarAtendimento=" + PERMITE_ALTERAR_ATENDIMENTO;

	var url = urlRoot + "MuralhaDigital/Ocorrencia";

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
				OCORRENCIA_COM_NOTIFICACAO = true;
				PERMITE_ATENDIMENTO = permiteAtendimento;
				SuccessModalCsx_E_TimeOut_8000ms_v2(msgResposta, DESC_ERROR_CONTAINER_MODAL_ACAO);
				DesabilitarControlesModalAcao();
				
				// Chamada com anotação personalizada
				var textoAnotacao = GerarTextoAcaoProcedimento(
					gruposEmail,
					gruposSMS,
					permiteAtendimento,
					PERMITE_ALTERAR_ATENDIMENTO
				);

				CadastrarAnotacaoComTexto(textoAnotacao);
			} else {
				WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, DESC_ERROR_CONTAINER_MODAL_ACAO);
			}
		},

		error: 		function(jqXHR, textStatus, errorThrown)
		{
		AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', DESC_ERROR_CONTAINER_MODAL_ACAO);
		},
		beforeSend: function(jqXHR, settings){},
		complete: 	function(jqXHR, textStatus){}

	});
}

function GerarTextoAcaoProcedimento(gruposEmail, gruposSMS, permiteAtendimento, permiteAlterarAtendimento) {
	const nomesEmail = obterNomesGruposSelecionados('#selAcaoGrupoEmailCad', gruposEmail);
	const nomesSMS = obterNomesGruposSelecionados('#selAcaoGrupoSmsCad', gruposSMS);

	const email = nomesEmail.length ? nomesEmail.join(', ') : "nenhum";
	const sms = nomesSMS.length ? nomesSMS.join(', ') : "nenhum";
	const atend = permiteAtendimento ? "Sim" : "Não";
	const altAtend = permiteAlterarAtendimento ? "Sim" : "Não";

	// Obtém valor do checkbox assinado
	const assinadoCheckbox = document.getElementById("chkAssinado");
	const ass = assinadoCheckbox && assinadoCheckbox.checked ? "Sim" : "Não";

	let texto = `<strong>Ação e Procedimento:</strong>`;
	texto += `<br>Email: ${email}`;
	texto += `<br>SMS: ${sms}`;
	texto += `<br>Permitir Atend.: ${atend}`;
	texto += `<br>Permitir Alt. Atend.: ${altAtend}`;
	texto += `<br>Assinado: ${ass}`;

	// Trunca sem cortar tags HTML (mantém abertura/fechamento do <strong>)
	if (texto.length > 280) {
		const limite = 280 - 3; // para "..."
		const textoSemTag = texto.replace(/<[^>]+>/g, '');
		let cortado = textoSemTag.substring(0, limite).trim();
		return `<strong>${cortado}...</strong>`;
	}

	return texto;
}

function obterNomesGruposSelecionados(selector, valoresSelecionados) {
	if (!valoresSelecionados || valoresSelecionados.length === 0) return [];

	// Garante array (pode vir string separada por vírgulas ou array mesmo)
	const ids = Array.isArray(valoresSelecionados)
		? valoresSelecionados
		: valoresSelecionados.toString().split(',');

	const nomes = [];

	$(selector + ' option').each(function () {
		const val = $(this).val();
		if (ids.includes(val)) {
			nomes.push($(this).text());
		}
	});

	return nomes;
}

function PopulaModalAcaoProcedimento()
{
	validarAberturaAtendimento();
	document.getElementById("chkAssinado").checked = false;
	if (ID_OCORRENCIA != null && ID_OCORRENCIA != "")
	{
		try
		{
			var funcoes =
			[				
				ObterGruposEmailOcorrencia,
				ObterGruposSmsOcorrencia,
				ObterAssinadoAlertaModal,
				PopularConfigAtendimento,
				DesabilitarControlesModalAcao,
				HabilitarControlesModalAcao,
			];
			
			var d = $.Deferred().resolve();
			while (funcoes.length > 0) {
				d = d.then(funcoes.shift());
			}
		}
		catch (ex)
		{
			RemoveLoading();
			AlertCsx_E_TimeOut_8000ms('Erro ao carregar a ações e procedimentos!!');
		}
	}	
}

function ObterGruposEmailOcorrencia()
{
	var dataString = "acao=obterGruposEmailPorIdOcorrencia" +
						"&idOcorrencia=" + ID_OCORRENCIA;
						
	var url = urlRoot + "MuralhaDigital/GrupoNotificacao";

		$.ajax({
		type: 		"GET",
		url: 		url,
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
				PopulaComboGrupoEmail( jqXHR.responseText );
			} else {
				WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, DESC_ERROR_CONTAINER_MODAL_ACAO);
			}
		},

		error: 		function(jqXHR, textStatus, errorThrown)
		{
		AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', DESC_ERROR_CONTAINER_MODAL_ACAO);
		},
		beforeSend: function(jqXHR, settings){},
		complete: 	function(jqXHR, textStatus){}
	});
}

function PopulaComboGrupoEmail(event)
{	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var grupos = [];
	
	$xml.find('GrupoNotificacao').each(function()
	{
		var $item = $(this);
		
		var idGrupo = $item.find('idGrupo').text();
		var grupo = $item.find('grupo').text();
		
		grupos.push(idGrupo.toString());
	});
	
	$('#selAcaoGrupoEmailCad').selectpicker('refresh');
	
	$('#selAcaoGrupoEmailCad').selectpicker('val', grupos);
	$('#selAcaoGrupoEmailCad').selectpicker('render');
}

function ObterGruposSmsOcorrencia()
{
	var dataString = "acao=obterGruposSmsPorIdOcorrencia" +
						"&idOcorrencia=" + ID_OCORRENCIA;
						
	var url = urlRoot + "MuralhaDigital/GrupoNotificacao";

		$.ajax({
		type: 		"GET",
		url: 		url,
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
				PopulaComboGrupoSMS( jqXHR.responseText );
			} else {
				WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, DESC_ERROR_CONTAINER_MODAL_ACAO);
			}
		},

		error: 		function(jqXHR, textStatus, errorThrown)
		{
			AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', DESC_ERROR_CONTAINER_MODAL_ACAO);
		},
		beforeSend: function(jqXHR, settings){},
		complete: 	function(jqXHR, textStatus){}
	});
}

function PopulaComboGrupoSMS(event)
{	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var grupos = [];
	
	$xml.find('GrupoNotificacao').each(function()
	{
		var $item = $(this);
		
		var idGrupo = $item.find('idGrupo').text();
		var grupo = $item.find('grupo').text();
		
		grupos.push(idGrupo.toString());
	});
	
	$('#selAcaoGrupoSmsCad').selectpicker('refresh');
	
	$('#selAcaoGrupoSmsCad').selectpicker('val', grupos);
	$('#selAcaoGrupoSmsCad').selectpicker('render');
}

function PopularConfigAtendimento()
{
	var chkPermitirAtendimento = document.getElementById("chkPermitirAtendimento");
	
	if (chkPermitirAtendimento)
		chkPermitirAtendimento.checked = PERMITE_ATENDIMENTO;
}


function LimparCamposAcaoProcedimento()
{
	var selAcaoGrupoEmailCad = document.getElementById("selAcaoGrupoEmailCad");
	var selAcaoGrupoSmsCad = document.getElementById("selAcaoGrupoSmsCad");
	var chkAssinado = document.getElementById("chkAssinado");
	if (selAcaoGrupoEmailCad)
		$('#selAcaoGrupoEmailCad').selectpicker('deselectAll');
		
	if (selAcaoGrupoSmsCad)
		$('#selAcaoGrupoSmsCad').selectpicker('deselectAll');
	
	if(chkAssinado)
		document.getElementById("chkAssinado").checked = false;
	$('#selAcaoGrupoEmailCad').selectpicker('refresh');
	$('#selAcaoGrupoSmsCad').selectpicker('refresh');
}

function DesabilitarControlesModalAcao()
{
	if(OCORRENCIA_FINALIZADA)
	{
		$('#selAcaoGrupoEmailCad').prop('disabled', true);
		$('#selAcaoGrupoEmailCad').selectpicker('refresh');
	
		$('#selAcaoGrupoSmsCad').prop('disabled', true);
		$('#selAcaoGrupoSmsCad').selectpicker('refresh');

		document.getElementById("btnLimparCamposAcaoOcorrencia").disabled = true;
		document.getElementById("btnSalvarAcaoOcorrencia").disabled = true;
	}
	
	if (OCORRENCIA_FINALIZADA || !PERMITE_ALTERAR_ATENDIMENTO)
		document.getElementById("chkPermitirAtendimento").disabled = true;
}

function HabilitarControlesModalAcao()
{
	if(!OCORRENCIA_FINALIZADA)
	{
		$('#selAcaoGrupoEmailCad').prop('disabled', false);
		$('#selAcaoGrupoEmailCad').selectpicker('refresh');
	
		$('#selAcaoGrupoSmsCad').prop('disabled', false);
		$('#selAcaoGrupoSmsCad').selectpicker('refresh');

		document.getElementById("btnLimparCamposAcaoOcorrencia").disabled = false;
		document.getElementById("btnSalvarAcaoOcorrencia").disabled = false;
		
		if (PERMITE_ALTERAR_ATENDIMENTO)
			document.getElementById("chkPermitirAtendimento").disabled = false;
	}
}

function ObterAssinadoAlertaModal() {
	var dataString = 
		"acao=obterAssinadoAlertaPorId" +
		"&idAlerta=" + encodeURIComponent(ID_ALERTA);

	var url = urlRoot + "MuralhaDigital/Alerta";

	$.ajax({
		type: "GET",
		url: url,
		data: dataString,
		dataType: "xml",

		success: function(data, textStatus, jqXHR) {
			var xmlDoc = $.parseXML(jqXHR.responseText);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				// Pegando o valor do campo <assinado>
				var assinadoText = $xml.find('alerta > assinado').first().text().trim();
				var assinado = (assinadoText === "true");

				// Preenche o checkbox
				document.getElementById("chkAssinado").checked = ALERTA_ASSINADO = assinado;
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
		},

		error: function(jqXHR, textStatus, errorThrown) {
			AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
		},

		beforeSend: function(jqXHR, settings) {},
		complete: function(jqXHR, textStatus) {}
	});
}

/*
	Nessa função abaixo, estarei fazendo uma chamada á servlet (banco) para popular o valor da dataString com o valor do idOcorrencia,
	após isso pegamos a url (com uma '/' inicial) e pegamos a rota do mesmo arquivo da servlet, então fazemos o ajax, chamando os 2 valores
	dito acima, no success, pegamos o valor retornado do banco (true ou false) e então desabilitamos ou não o checkbox de atendimento.
*/
function validarAberturaAtendimento() {
	var dataString = 
		"acao=obterAberturaAtendimento" +
		"&idOcorrencia=" + encodeURIComponent(ID_ALERTA);

	var url = urlRoot + "MuralhaDigital/Alerta";

	$.ajax({
		type: "GET",
		url: url,
		data: dataString,
		dataType: "text",
		
		success: function(data) {
			var valor = data.trim().toLowerCase();
			document.getElementById("chkPermitirAtendimento").disabled = (valor === "false");
		},
		
		error: function(jqXHR, textStatus, errorThrown) {
			console.error("Erro AJAX:", textStatus, errorThrown, jqXHR.status, jqXHR.responseText);
			AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
		}
	});
}