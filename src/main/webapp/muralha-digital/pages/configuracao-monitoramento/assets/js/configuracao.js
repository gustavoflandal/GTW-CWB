var ID_TIPO_REGISTRO = null;
var gruposSelect;

const mapaTipoAlertaParaSelectId = {
  "CF6EBC36-56CA-430B-9D3D-F7D9FF1E82F3": "selGrupoEnvioAlertaClonado",
  "95631582-96B2-4220-9612-12131BE4923C": "selGrupoEnvioAlertaRoubado",
  "CB8D5C4B-1822-4868-A2F9-0153B50212DA": "selGrupoEnvioAlertaSequestro",
  "0349F722-DFDE-4080-9E3B-D65F1C058EDC": "selGrupoEnvioAlertaFurtado",
  "feef9500-83c0-4942-a8aa-aed77e20ba5b": "selGrupoEnvioAlertaLicenciamento",
  "0349f722-dfde-4080-9e3b-d65f1c058edc": "selGrupoEnvioAlertaMonitorado",
  "cf6ebc36-56ca-430b-9d3d-f7d9ff1e82f3": "selGrupoEnvioAlertaComboio",
  "FEEF9500-83C0-4942-A8AA-AED77E20BA5B": "selGrupoEnvioAlertaPonto",
  "AE93F81A-DF6D-41B5-AFC6-99B3438C291D": "selGrupoEnvioAlertaClandestino"
};

$(document).ready(function () {
    PopularCombosGruposEnvioAlertas(); // primeiro popular os selects
   
    iniciaConfigs(); // Popular os ativos/inativos
   
    // depois de populado, buscar e marcar os grupos configurados
   setTimeout(() => {
      buscarGruposSelecionados();
   }, 500);

   carregarTempoMaximoEmissao();
   validarAcessoUsuario();
   obtemTipoAlerta();
   
   $('#tempoIndefinido').on('change', function () {
        atualizarComponenteTempoInatividade();
    });

	carregarRaioRadares();
    carregarTempoOCRBlitz();
});

function ObterGruposAlerta(callback) {
    var dataString = "acao=buscarGruposView";
    var url = urlRoot + "MuralhaDigital/GrupoNotificacao";

    $.ajax({
        type: "GET",
        url: url,
        data: dataString,
        dataType: "xml",
        success: function(xml) {
            if (typeof callback === "function") {
                callback(xml);
            }
        },
        error: function() {
            alert('Erro ao processar requisição ao servidor!!');
        }
    });
}

function PopularCombosGruposEnvioAlertas() {
    ObterGruposAlerta(function(xml) {
	
        var grupos = $(xml).find("ListaGrupos > ViewGrupoAlertas");
       	
       	gruposSelect = grupos;
       	
        $("input.tipo-alerta").each(function() {
            var nomeSelect = $(this).attr("name"); 
            var select = $("#" + nomeSelect); 

            if (select.length === 0) return; 

            select.empty(); 

            grupos.each(function() {
                var grupo = $(this);
                var id = grupo.find("id_grupo").text().trim();
                var descricao = grupo.find("descricao").text().trim();

                select.append($('<option>', {
                    value: id,
                    text: descricao
                }));
            });

            if (select.hasClass("selectpicker")) {
                select.selectpicker('refresh');
            }
        });
    });
}

function obterTipoRegistro()
{
	ID_TIPO_REGISTRO = document.getElementById("idtipoRegistro").value;
}

function iniciaConfigs()
{	
	var urlPesquisa = urlRoot + "MuralhaDigital/ConfigurarEquipamento";
	var dataString = "acao=obterConfigs";
	
	$.ajax({
		type: "GET",
		url: urlPesquisa,
		data: dataString,
		dataType: "xml",
		
		success: function(data, textStatus, jqXHR)
		{
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);
			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				preenchePagina( event );
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
		},
		
		error: function(jqXHR, textStatus, errorThrown)
		{
			console.log("[iniciaConfigs] Erro");
		},
		
		beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
	});
}

function preenchePagina(event)
{
	var xmlDoc = $.parseXML(event);
	var $xml = $(xmlDoc);
	
	var clonado_status 			= $xml.find("clonado_status").text();
	var roubado_status 			= $xml.find("roubado_status").text();
	var sequestro_status 		= $xml.find("sequestro_status").text();
	var furtado_status 			= $xml.find("furtado_status").text();
	var comboio_status 			= $xml.find("comboio_status").text();
	var banco_status 			= $xml.find("banco_status").text();
	var clandestino_status		= $xml.find("clandestino_status").text();
	var licenciamento_status	= $xml.find("licenciamento_status").text();
	var monitorado_status		= $xml.find("monitorado_status").text();
	var tempo_maximo_emissao 	= $xml.find("tempo_maximo_emissao").text();
	
	document.getElementById("statusClonado").checked 		= clonado_status 		== "1" ? true : false;
	document.getElementById("statusRoubado").checked 		= roubado_status 		== "1" ? true : false;
	document.getElementById("statusSequestro").checked 		= sequestro_status 		== "1" ? true : false;
	document.getElementById("statusFurtado").checked 		= furtado_status 		== "1" ? true : false;
	document.getElementById("statusComboio").checked 		= comboio_status 		== "1" ? true : false;
	document.getElementById("statusPonto").checked 			= banco_status 			== "1" ? true : false;
	document.getElementById("statusClandestino").checked	= clandestino_status	== "1" ? true : false;
	document.getElementById("statusLicenciamento").checked	= licenciamento_status	== "1" ? true : false;
	document.getElementById("statusMonitorado").checked		= monitorado_status		== "1" ? true : false;
	
	var comboio_intervalo	= $xml.find("comboio_intervalo").text();
	var banco_intervalo 	= $xml.find("banco_intervalo").text();
	
	document.getElementById("tempoComboio").value	= comboio_intervalo;
	document.getElementById("tempoPonto").value		= banco_intervalo;
	
	var clandestino_init_manha	= $xml.find("clandestino_init_manha").text().slice(0,5); 
	var clandestino_fim_manha	= $xml.find("clandestino_fim_manha").text().slice(0,5); 
	var clandestino_init_tarde	= $xml.find("clandestino_init_tarde").text().slice(0,5); 
	var clandestino_fim_tarde	= $xml.find("clandestino_fim_tarde").text().slice(0,5); 
	var clandestino_passagens	= $xml.find("clandestino_passagens").text(); 
	var clandestino_tipo		= $xml.find("clandestino_tipo").text();
	var qtd_pas_correlacao_baixa = $xml.find("qtd_pas_correlacao_baixa").text() || $xml.find("passagens_correlacao_baixa").text();
	var qtd_pas_correlacao_media = $xml.find("qtd_pas_correlacao_media").text() || $xml.find("passagens_correlacao_media").text();
	var qtd_pas_correlacao_alta = $xml.find("qtd_pas_correlacao_alta").text() || $xml.find("passagens_correlacao_alta").text();
	
	document.getElementById("iniManha").value		= clandestino_init_manha;
	document.getElementById("fimManha").value		= clandestino_fim_manha;
	document.getElementById("iniTarde").value		= clandestino_init_tarde;
	document.getElementById("fimTarde").value		= clandestino_fim_tarde;
	document.getElementById("passagens").value		= clandestino_passagens;
	document.getElementById("tipoVeiculo").value	= clandestino_tipo;
	document.getElementById("qtdPasCorrelacaoBaixa").value = qtd_pas_correlacao_baixa ? qtd_pas_correlacao_baixa : 3;
	document.getElementById("qtdPasCorrelacaoMedia").value = qtd_pas_correlacao_media ? qtd_pas_correlacao_media : 4;
	document.getElementById("qtdPasCorrelacaoAlta").value = qtd_pas_correlacao_alta ? qtd_pas_correlacao_alta : 5;
	
	var erros_permitidos_semelhanca					= $xml.find("QtdeErrosSemelhanca").text();
	document.getElementById("caracteresSemelhantes").value	= erros_permitidos_semelhanca;
	atualizarEstadoVeiculosCorrelacionados();

	var raio_radares = $xml.find("raio_radares_mapa").text();
	if(raio_radares) {
		document.getElementById("raioRadaresMapa").value = raio_radares;
	}

	carregarStatusAgenteGuarnicao();
}

/**
 * Avalia as faixas de passagens configuradas para veículos correlacionados.
 * @returns {{valido:boolean,qtd_pas_correlacao_baixa:number,qtd_pas_correlacao_media:number,qtd_pas_correlacao_alta:number,erros:{baixa:string,media:string,alta:string}}} Estado calculado da validação.
 */
function avaliarQtdPasCorrelacao()
{
	var qtdPasCorrelacaoBaixa = parseInt(document.getElementById("qtdPasCorrelacaoBaixa").value, 10);
	var qtdPasCorrelacaoMedia = parseInt(document.getElementById("qtdPasCorrelacaoMedia").value, 10);
	var qtdPasCorrelacaoAlta = parseInt(document.getElementById("qtdPasCorrelacaoAlta").value, 10);
	var baixaValida = !isNaN(qtdPasCorrelacaoBaixa) && qtdPasCorrelacaoBaixa >= 1;
	var mediaValida = !isNaN(qtdPasCorrelacaoMedia) && qtdPasCorrelacaoMedia >= 1;
	var altaValida = !isNaN(qtdPasCorrelacaoAlta) && qtdPasCorrelacaoAlta >= 1;
	var erros = {
		baixa: "",
		media: "",
		alta: ""
	};

	if (!baixaValida)
	{
		erros.baixa = "Informe um valor maior ou igual a 1.";
	}

	if (!mediaValida)
	{
		erros.media = "Informe um valor maior ou igual a 1.";
	}

	if (!altaValida)
	{
		erros.alta = "Informe um valor maior ou igual a 1.";
	}

	if (baixaValida && mediaValida && qtdPasCorrelacaoMedia <= qtdPasCorrelacaoBaixa)
	{
		erros.baixa = "Baixa deve ser menor que média.";
		erros.media = "Média deve ser maior que baixa.";
	}

	if (mediaValida && altaValida && qtdPasCorrelacaoAlta <= qtdPasCorrelacaoMedia)
	{
		erros.media = erros.media ? erros.media : "Média deve ser menor que alta.";
		erros.alta = "Alta deve ser maior que média.";
	}

	return {
		valido: !erros.baixa && !erros.media && !erros.alta,
		qtd_pas_correlacao_baixa: qtdPasCorrelacaoBaixa,
		qtd_pas_correlacao_media: qtdPasCorrelacaoMedia,
		qtd_pas_correlacao_alta: qtdPasCorrelacaoAlta,
		erros: erros
	};
}

/**
 * Aplica o estado visual de validação a um campo da configuração de veículos correlacionados.
 * @param {string} idCampo Identificador do input.
 * @param {string} idMensagem Identificador do elemento de mensagem.
 * @param {string} mensagem Mensagem de erro do campo.
 */
function aplicarEstadoCampoVeiculosCorrelacionados(idCampo, idMensagem, mensagem)
{
	var campo = document.getElementById(idCampo);
	var mensagemEl = document.getElementById(idMensagem);
	var possuiErro = !!mensagem;

	if (campo)
	{
		campo.classList.toggle("is-invalid", possuiErro);
		campo.classList.toggle("text-danger", possuiErro);
	}

	if (mensagemEl)
	{
		mensagemEl.textContent = mensagem;
		mensagemEl.classList.toggle("d-none", !possuiErro);
	}
}

/**
 * Atualiza o estado visual dos campos e do botão de salvar da configuração de veículos correlacionados.
 * @returns {{valido:boolean,qtd_pas_correlacao_baixa:number,qtd_pas_correlacao_media:number,qtd_pas_correlacao_alta:number,erros:{baixa:string,media:string,alta:string}}} Resultado da validação atual.
 */
function atualizarEstadoVeiculosCorrelacionados()
{
	var avaliacao = avaliarQtdPasCorrelacao();
	var btnSalvar = document.getElementById("btnConfigVeiculosCorrelacionados");

	aplicarEstadoCampoVeiculosCorrelacionados("qtdPasCorrelacaoBaixa", "msgQtdPasCorrelacaoBaixa", avaliacao.erros.baixa);
	aplicarEstadoCampoVeiculosCorrelacionados("qtdPasCorrelacaoMedia", "msgQtdPasCorrelacaoMedia", avaliacao.erros.media);
	aplicarEstadoCampoVeiculosCorrelacionados("qtdPasCorrelacaoAlta", "msgQtdPasCorrelacaoAlta", avaliacao.erros.alta);

	if (btnSalvar)
	{
		btnSalvar.disabled = !avaliacao.valido;
		btnSalvar.classList.toggle("btn-success", avaliacao.valido);
		btnSalvar.classList.toggle("btn-dark", !avaliacao.valido);
	}

	return avaliacao;
}

/**
 * Obtém e valida as quantidades de passagens para correlação na tela de configuração.
 * @returns {{qtd_pas_correlacao_baixa:number, qtd_pas_correlacao_media:number, qtd_pas_correlacao_alta:number}|null} Quantidades válidas ou null quando houver inconsistência.
 */
function obterQtdPasCorrelacaoValidadas()
{
	var avaliacao = atualizarEstadoVeiculosCorrelacionados();

	if (!avaliacao.valido)
	{
		return null;
	}

	return {
		qtd_pas_correlacao_baixa: avaliacao.qtd_pas_correlacao_baixa,
		qtd_pas_correlacao_media: avaliacao.qtd_pas_correlacao_media,
		qtd_pas_correlacao_alta: avaliacao.qtd_pas_correlacao_alta
	};
}

function CarregaComboGruposPopup(event, nomeComboPopupar) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	$(nomeComboPopupar).empty();
	
	$xml.find('GrupoNotificacao').each(function()
	{
		var $item = $(this);
		
		var idGrupo = $item.find('idGrupo').text();
		var grupo = $item.find('grupo').text();
		
		$(nomeComboPopupar).append($('<option>', {
		    value: idGrupo,
		    text: grupo
		}));
	});
	
	$(nomeComboPopupar).selectpicker('refresh');
}

async function configComboio() {
	var tempo = document.getElementById("tempoComboio").value;
	var status = document.getElementById("statusComboio").checked;

	if (isNaN(tempo)) {
		alert("Configuração de comboio: Por favor selecione o intervalo de tempo desejado");
		return;
	}

	const idTipoAlertaOcorrencia = "19B86A23-2CD6-43ED-A596-62935EA3980A";

	try {
		await realizarPersistenciasBanco(idTipoAlertaOcorrencia, "selGrupoEnvioAlertaComboio");

		var dataString = (
			"acao=comboio" +
			"&tempo=" + tempo +
			"&status=" + status
		);
		
		insereConfig(dataString);
	} catch (erro) {
		alert("Erro ao salvar as configurações");
	}
}

async function configClonado()
{		
	var status = document.getElementById("statusClonado").checked;
	
	const idTipoAlertaOcorrencia = "CF6EBC36-56CA-430B-9D3D-F7D9FF1E82F3";
	
	try{
		await realizarPersistenciasBanco(idTipoAlertaOcorrencia, "selGrupoEnvioAlertaClonado");
		
			const dataString = (
				"acao=clonado" +
				"&status=" + status +
				"&idTipoAlertaOcorrencia=" + idTipoAlertaOcorrencia
			);
			
		insereConfig(dataString);
	}catch(erro){
		console.log(erro);
		alert("Erro ao salvar as configurações." );
	}
}

async function configRoubado()
{		
	var status = document.getElementById("statusRoubado").checked;
	
	const idTipoAlertaOcorrencia = "95631582-96B2-4220-9612-12131BE4923C";
	
	try{
		await realizarPersistenciasBanco(idTipoAlertaOcorrencia, "selGrupoEnvioAlertaRoubado");
		
			const dataString = (
				"acao=roubado" +
				"&status=" + status +
				"&idTipoAlertaOcorrencia=" + idTipoAlertaOcorrencia
			);
			
		insereConfig(dataString);
	}catch(erro){
		alert("Erro ao salvar as configurações");
	}
}

async function configFurtado()
{	
	var status = document.getElementById("statusFurtado").checked;
	
	const idTipoAlertaOcorrencia = "0349F722-DFDE-4080-9E3B-D65F1C058EDC";
	
	try{
		await realizarPersistenciasBanco(idTipoAlertaOcorrencia, "selGrupoEnvioAlertaFurtado");
		
			const dataString = (
				"acao=furtado" +
				"&status=" + status +
				"&idTipoAlertaOcorrencia=" + idTipoAlertaOcorrencia
			);
			
		insereConfig(dataString);
	}catch(erro){
		alert("Erro ao salvar as configurações");
	}
}

async function configLicenciamento()
{	
	var status = document.getElementById("statusLicenciamento").checked;
	
	const idTipoAlertaOcorrencia = "6631DC43-779F-4BFF-A329-B9653D056708";	
	
	try{
		await realizarPersistenciasBanco(idTipoAlertaOcorrencia, "selGrupoEnvioAlertaLicenciamento");
		
			const dataString = (
				"acao=licenciamento" +
				"&status=" + status +
				"&idTipoAlertaOcorrencia=" + idTipoAlertaOcorrencia
			);
			
		insereConfig(dataString);
	}catch(erro){
		alert("Erro ao salvar as configurações");
	}
}

async function configMonitorado()
{	
	var status = document.getElementById("statusMonitorado").checked;
	
	const idTipoAlertaOcorrencia = "9D31A265-A663-4836-BF00-2309FC0D5E33";	
	
	try{
		await realizarPersistenciasBanco(idTipoAlertaOcorrencia,"selGrupoEnvioAlertaMonitorado");
		
			const dataString = (
				"acao=monitorado" +
				"&status=" + status +
				"&idTipoAlertaOcorrencia=" + idTipoAlertaOcorrencia
			);
			
		insereConfig(dataString);
	}catch(erro){
		alert("Erro ao salvar as configurações");
	}
}

async function configSequestro()
{	
	var status = document.getElementById("statusSequestro").checked;
	
	const idTipoAlertaOcorrencia = "CB8D5C4B-1822-4868-A2F9-0153B50212DA";
	
	try{
		await realizarPersistenciasBanco(idTipoAlertaOcorrencia, "selGrupoEnvioAlertaSequestro");
		
			const dataString = (
				"acao=sequestro" +
				"&status=" + status +
				"&idTipoAlertaOcorrencia=" + idTipoAlertaOcorrencia 
			);
			
		insereConfig(dataString);
	}catch(erro){
		alert("Erro ao salvar as configurações");
	}
}

async function configClandestino()
{
	var status = document.getElementById("statusClandestino").checked;
	
	var init_manha	= document.getElementById("iniManha").value;
	var fim_manha	= document.getElementById("fimManha").value;
	var init_tarde	= document.getElementById("iniTarde").value;
	var fim_tarde	= document.getElementById("fimTarde").value;
	
	var passagens	= parseInt(document.getElementById("passagens").value);
	var tipo		= document.getElementById("tipoVeiculo").value
	
	if(init_manha == "inicio")
		alert("Configuração de veículo clandestino: Por favor selecione o horário de início de monitoramento do período da manhã");
	else if(fim_manha == "fim")
		alert("Configuração de veículo clandestino: Por favor selecione o horário de fim de monitoramento do período da manhã");
	else if(init_tarde == "inicio")
		alert("Configuração de veículo clandestino: Por favor selecione o horário de início de monitoramento do período da tarde");
	else if(fim_tarde == "fim")
		alert("Configuração de veículo clandestino: Por favor selecione o horário de fim de monitoramento do período da tarde");
	else if(passagens == "0")
		alert("Configuração de veículo clandestino: Por favor selecione a quantidade de passagens");
	else if(tipo == "0")
		alert("Configuração de veículo clandestino: Por favor selecione o tipo de veículo");
	else
	{	
		const idTipoAlertaOcorrencia = "AE93F81A-DF6D-41B5-AFC6-99B3438C291D";		
		
		try{
			await realizarPersistenciasBanco(idTipoAlertaOcorrencia, "selGrupoEnvioAlertaClandestino");
			
			var dataString = (
				"acao=clandestino"
				+ "&status="	+ status
				+ "&initManha=" + init_manha
				+ "&fimManha=" 	+ fim_manha
				+ "&initTarde=" + init_tarde
				+ "&fimTarde=" 	+ fim_tarde
				+ "&passagens=" + passagens
				+ "&tipo="		+ tipo
			);
				
			insereConfig(dataString);
		}catch(erro){
			alert("Erro ao salvar as configurações");
		}
	}
}

async function configPonto()
{	
	//var selecionado = document.getElementById("tempoPonto").value;
	//var tempo = (parseInt(selecionado) + 1) * 30;
	var tempo = document.getElementById("tempoPonto").value;
	var status = document.getElementById("statusPonto").checked;
	
	if(isNaN(tempo))
	{
		alert("Roubo a banco: Por favor selecione o intervalo de tempo desejado")
	}
	else
	{
		const idTipoAlertaOcorrencia = "FEEF9500-83C0-4942-A8AA-AED77E20BA5B";		
		
		try{
			await realizarPersistenciasBanco(idTipoAlertaOcorrencia, "selGrupoEnvioAlertaPonto");
			
			var dataString = (
				"acao=ponto"
				+ "&tempo="
				+ tempo
				+ "&status="
				+ status
			);
		
			insereConfig(dataString);
		}catch(erro){
			alert("Erro ao salvar as configurações");
		}
	}
}

async function configSemelhanca()
{		
	var erros_permitidos = document.getElementById("caracteresSemelhantes").value;
	var dataString =  "acao=Semelhanca"
		+ "&qtde_erros=" + erros_permitidos;
	
	insereConfig(dataString);
}

/**
 * Salva apenas as faixas configuradas para a análise de veículos correlacionados.
 */
async function configVeiculosCorrelacionados()
{
	var qtdPasCorrelacao = obterQtdPasCorrelacaoValidadas();

	if (qtdPasCorrelacao == null)
	{
		return;
	}

	var dataString =  "acao=VeiculosCorrelacionados"
		+ "&qtd_pas_correlacao_baixa=" + qtdPasCorrelacao.qtd_pas_correlacao_baixa
		+ "&qtd_pas_correlacao_media=" + qtdPasCorrelacao.qtd_pas_correlacao_media
		+ "&qtd_pas_correlacao_alta=" + qtdPasCorrelacao.qtd_pas_correlacao_alta;

	insereConfig(dataString);
}

function insereConfig(dataString)
{
	var urlPesquisa = urlRoot + "MuralhaDigital/ConfigurarEquipamento";
	
	$.ajax({
		type: "GET",
		url: urlPesquisa,
		data: dataString,
		dataType: "xml",
		
		success: function(data, textStatus, jqXHR)
		{
			var event = jqXHR.responseText;
        	var xmlDoc = $.parseXML( event );
			var $xml = $(xmlDoc);
    			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');
			
        	if (sucesso) {
				alert("Sucesso ao atualizar configurações");
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
				alert("Tempo de resposta excedido")
			}
		},
		
		error: function(jqXHR, textStatus, errorThrown)
		{
			alert("Erro ao salvar configurações");
		},
		
		beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
	});
}

function realizarPersistenciasBanco(idTipoAlertaOcorrencia, selectName) {
	return new Promise(function (resolve, reject) {
			
		const select = document.getElementById(selectName);
		
		let valoresSelecionados = Array.from(select.selectedOptions).map(option => option.value);
		if (valoresSelecionados.length === 0) {
			valoresSelecionados = Array.from(select.options).map(option => option.value);
		}

		$.ajax({
			type: "GET",
			url: "/MuralhaDigital/GrupoNotificacao",
			dataType: "xml",
			traditional: true,
			data: {
				acao: "obterGruposPorIdGrupoETipoNotificacao",
				idTipoAlertaOcorrencia: idTipoAlertaOcorrencia,
			},
			success: function (data) {
				let json = xmlToJson(data.documentElement);
				let jsonString = JSON.stringify(json);

				$.ajax({
					type: "POST",
					url: "/MuralhaDigital/HistoricoConfiguracaoEquipamento",
					dataType: "xml",
					traditional: true,
					data: {
						acao: "atualizarConfigsEquipamentos",
						dadosJson: jsonString,
						idsGrupos: valoresSelecionados,
						idTipoAlertaOcorrencia: idTipoAlertaOcorrencia,
					},
					success: function () {
						resolve(jsonString); 
					},
					error: function () {
						console.log("Erro ao salvar os dados.");
						reject("Erro ao salvar os dados.");
					}
				});
			},
			error: function () {
				console.log("Erro ao buscar dados.");
				reject("Erro ao buscar dados.");
			}
		});
	});
}

function buscarGruposSelecionados() {
    const url = urlRoot + "MuralhaDigital/GrupoNotificacao";
    const dataString = "acao=buscarGruposSelecionados";

    $.ajax({
        type: "GET",
        url: url,
        data: dataString,
        dataType: "text",
        success: function (xmlStr) {
	
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlStr, "application/xml");

            const gruposXml = xmlDoc.getElementsByTagName("GrupoNotificacao");

            const mapaGruposPorAlerta = {};

            // Mapeia os grupos por ID de alerta (em lowercase)
            for (let i = 0; i < gruposXml.length; i++) {
                const grupo = gruposXml[i];
                const idAlerta = grupo.getElementsByTagName("idTipoAlertaOcorrencia")[0]?.textContent.trim().toLowerCase();
                const idGrupo = grupo.getElementsByTagName("idGrupo")[0]?.textContent.trim();

                if (!idAlerta || !idGrupo) continue;

                if (!mapaGruposPorAlerta[idAlerta]) {
                    mapaGruposPorAlerta[idAlerta] = new Set();
                }

                mapaGruposPorAlerta[idAlerta].add(idGrupo);                         
            }
             
            $("select.selectpicker").each(function () {
                const select = $(this);
                const idAlerta = select.data("tipo-alerta")?.toLowerCase();

                if (!idAlerta) {
                    console.warn("Select sem data-tipo-alerta:", select.attr("id"));
                    return;
                }

                const grupos = mapaGruposPorAlerta[idAlerta];
                if (!grupos) {
                    console.log("Nenhum grupo mapeado para este alerta:", idAlerta);
                    return;
                }

                // Limpa seleções antigas
                select.find("option").prop("selected", false);

                // Marca os grupos retornados
                grupos.forEach(idGrupo => {
                    const option = select.find(`option[value='${idGrupo}']`);
                    if (option.length > 0) {
                        option.prop("selected", true);
                    }
                });
            });

            $(".selectpicker").selectpicker("refresh");
        },
        error: function () {
            alert("Erro ao buscar grupos selecionados!");
        }
    });
}

function xmlToJson(xml) {
    if (xml.nodeType === 1) { 
        const obj = {};

        // Se tiver apenas um filho e for texto, retorna só o valor
        if (xml.childNodes.length === 1 && xml.firstChild.nodeType === 3) {
            return xml.firstChild.nodeValue.trim();
        }

        // Percorre os filhos
        for (let i = 0; i < xml.childNodes.length; i++) {
            const item = xml.childNodes.item(i);
            const nodeName = item.nodeName;
            const value = xmlToJson(item);

            if (value === "") continue;

            if (obj[nodeName]) {
                // Se já existe, transforma em array
                if (!Array.isArray(obj[nodeName])) {
                    obj[nodeName] = [obj[nodeName]];
                }
                obj[nodeName].push(value);
            } else {
                obj[nodeName] = value;
            }
        }

        return obj;
    } else if (xml.nodeType === 3) {
        return xml.nodeValue.trim();
    }
}

async function configTempoMaximo()
{
    var tempo = document.getElementById("tempoMaximoEmissao").value;
    
    if(isNaN(tempo) || tempo < 1)
    {
        alert("Tempo máximo para emissão do alerta: Por favor informe um valor válido em minutos (mínimo 1)");
        return;
    }
    
    $.ajax({
        type: "POST",
        url: "/MuralhaDigital/ConfiguracaoTempo",
        dataType: "xml",
        data: {
            acao: "configurarTempoMaximoEmissao",
            tempo: tempo
        },
        success: function (data) {
            alert("Sucesso ao atualizar configurações");
        },
        error: function () {
            alert("Erro ao salvar configurações");
        }
    });
}

function carregarTempoMaximoEmissao() {
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
                document.getElementById('tempoMaximoEmissao').value = tempo;
            }
        }
    });
}

function configAgenteGuarnicao() {
    var status = document.getElementById("statusAgenteGuarnicao").checked;
    
    $.ajax({
        type: "POST",
        url: urlRoot + "MuralhaDigital/GrupoNotificacao",
        data: {
            acao: "configAgenteGuarnicao",
            status: status
        },
        dataType: "xml",
        
        success: function(data, textStatus, jqXHR) {
            var event = jqXHR.responseText;
            var xmlDoc = $.parseXML(event);
            var $xml = $(xmlDoc);
            
            var sucesso = $xml.find('sucesso').text();
            var msgResposta = $xml.find('msgResposta').text();
            
            sucesso = (sucesso === 'true' || sucesso === '');
            
            if (sucesso) {
                alert("Sucesso ao atualizar configurações");
                // Recarrega os selects de grupos
                PopularCombosGruposEnvioAlertas();
                setTimeout(() => {
                    buscarGruposSelecionados();
                }, 500);
            } else {
                alert("Erro ao atualizar configurações: " + msgResposta);
            }
        },
        
        error: function(jqXHR, textStatus, errorThrown) {
            alert("Erro ao salvar configurações");
        }
    });
}

function carregarStatusAgenteGuarnicao() {
    $.ajax({
        type: "GET",
        url: urlRoot + "MuralhaDigital/GrupoNotificacao",
        data: {
            acao: "obterStatusAgenteGuarnicao"
        },
        dataType: "xml",
        
        success: function(data, textStatus, jqXHR) {
            var event = jqXHR.responseText;
            var xmlDoc = $.parseXML(event);
            var $xml = $(xmlDoc);
            
            var status = $xml.find('status_agente_guarnicao').text();
            document.getElementById("statusAgenteGuarnicao").checked = status == "1" ? true : false;
        },
        
        error: function(jqXHR, textStatus, errorThrown) {
            console.log("Erro ao carregar status do agente de guarnição");
        }
    });
}

async function configTempoMaximoInatividade()
{
    var tempo = document.getElementById("tempoMaximoInatividade").value;
    var tempoIndefinido = document.getElementById("tempoIndefinido").value;
    
    if(isNaN(tempo) || tempo < 1 && tempoIndefinido != true)
    {
        alert("Tempo máximo para inatividade: Por favor informe um valor válido em minutos (mínimo 1)");
        return;
    }  
    
    tempo = tempo * 60;
    
    $.ajax({
        type: "POST",
        url: "/MuralhaDigital/ConfiguracaoInatividade",
        dataType: "xml",
        data: {
            acao: "atualizarTempoInatividade",
            tempo: tempo,
            loginIndefinido: tempoIndefinido
        },
        success: function (data) {
            alert("Sucesso ao atualizar configurações");
        },
        error: function () {
            alert("Erro ao salvar configurações");
        }
    });
}

async function validarAcessoUsuario() {
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/PermissoesFuncionalidade",
        dataType: "xml",
        data: {
            acao: "verificarPermissaoGrupoEspecifico",
            idGrupo: 11 
        },
        success: function (data) {

            // Lê o XML: <permitido>true</permitido>
            let permitido = $(data).find("permitido").text();

            if (permitido === "true") {
                $("#blocoTempoInatividade").show();  // mostra o bloco
            } else {
                $("#blocoTempoInatividade").hide(); // mantém oculto
            }
        },
        error: function () {
            console.log("Erro ao verificar acesso");
        }
    });
    
    iniciarConfigTempoInatividade();
}

function atualizarComponenteTempoInatividade() {
        const indefinidoAtivo = $('#tempoIndefinido').is(':checked');

        if (indefinidoAtivo) {
            $('#tempoMaximoInatividade')
                .prop('disabled', true);
        } else {
            $('#tempoMaximoInatividade')
                .prop('disabled', false);
        }
}

async function iniciarConfigTempoInatividade() {
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/ConfiguracaoInatividade",
        dataType: "xml",
        data: {
            acao: "buscarConfigsInatividade",
        },
        success: function (data) {

            const tempoInatividade = parseInt(
                $(data).find('LoginTempoInatividade > valor').text()
            );

            const nuncaBloqueia = parseInt(
                $(data).find('LoginNuncaBloqueia > valor').text()
            );

            $('#tempoMaximoInatividade').val(tempoInatividade / 60);

            $('#tempoIndefinido').prop('checked', nuncaBloqueia === 1);
            
            if(nuncaBloqueia === 1){
				atualizarComponenteTempoInatividade();
			}
        },
        error: function () {
            console.log("Erro ao buscar as configurações de inatividade");
        }
    });
}

async function configRaioRadares()
{
    var raio = document.getElementById("raioRadaresMapa").value;
    
    if(isNaN(raio) || raio < 100)
    {
        alert("Raio de busca para radares: Por favor informe um valor válido em metros (mínimo 100)");
        return;
    }
    
    $.ajax({
        type: "POST",
        url: "/MuralhaDigital/ConfiguracaoRadares",
        dataType: "xml",
        data: {
            acao: "configurarRaioRadaresMapa",
            raio: raio
        },
        success: function (data) {
            alert("Sucesso ao atualizar configurações");
        },
        error: function () {
            alert("Erro ao salvar configurações");
        }
    });
}

async function configTempoOCRBlitz()
{
    var tempo = document.getElementById("tempoOCRBlitz").value;

    if(isNaN(tempo) || tempo < 0.1)
    {
        alert("Tempo de OCR Blitz: Por favor informe um valor válido em segundos (mínimo 0.1)");
        return;
    }
    
    $.ajax({
        type: "POST",
        url: "/MuralhaDigital/ConfiguracaoTempoOcrBlitz",
        dataType: "xml",
        data: {
            acao: "configurarTempoOCRBlitz",
            tempo: tempo
        },
        success: function (data) {
            alert("Sucesso ao atualizar configurações");
        },
        error: function () {
            alert("Erro ao salvar configurações");
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
            if (raio) {
                document.getElementById('raioRadaresMapa').value = raio;
            }
        },
        error: function () {
            console.log("Erro ao carregar raio de radares");
        }
    });
}

function carregarTempoOCRBlitz() {
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/ConfiguracaoTempoOcrBlitz",
        dataType: "xml",
        data: {
            acao: "obterTempoOCRBlitz"
        },
        success: function (data) {
            var tempo = $(data).find('tempo_ocr_blitz').text();
            if (tempo) {
                document.getElementById('tempoOCRBlitz').value = tempo;
            }
        },
        error: function () {
            console.log("Erro ao carregar tempo de OCR Blitz");
        }
    });
}

function obtemTipoAlerta() {
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/ConfiguracaoTempo",
        dataType: "xml",
        data: {
            acao: "obterTiposAlertas"
        },
        success: function (data) {

            var $select = $("#tipoAlerta");
            $select.empty();

            $(data).find("alerta").each(function () {

                var id = $(this).find("id").text();
                var tipo = $(this).find("tipo").text();

                $select.append(
                    $("<option></option>")
                        .val(id)
                        .text(tipo)
                );
            });
        },
        error: function (xhr, status, error) {
            console.log("Erro ao carregar tipos de alerta:", error);
        }
    });
}

function obterPrioridade(id, callback){	
	 $.ajax({
        type: "GET",
        url: "/MuralhaDigital/ConfiguracaoTempo",
        dataType: "text",
        data: {
            acao: "obterPrioridade",
            id: id
        },
        success: function (data) {	
	console.log(data)		 
            callback(data);           
        },
        error: function (xhr, status, error) {
            console.log("Erro ao carregar tipos de alerta:", error);
        }
    });
}

function salvarPrioridade() {

    var idTipo = document.getElementById("tipoAlerta").value;
    var prioridadeInput = document.getElementById("valorPrioridade").value;

    if (!prioridadeInput || prioridadeInput.trim() === "") {
        alert("Informe a prioridade.");
        document.getElementById("valorPrioridade").focus();
        return;
    }    

    obterPrioridade(prioridadeInput, function(prioridadeCadastrada) {

        console.log("Prioridade cadastrada:", prioridadeCadastrada);
        if(prioridadeCadastrada == prioridadeInput){
			alert("Valor desejado de prioridade ja cadastrado para outro tipo de alerta.");
	        document.getElementById("valorPrioridade").focus();
	        return;
			}

        $.ajax({
            type: "POST",
            url: "/MuralhaDigital/ConfiguracaoTempo",
            dataType: "xml",
            data: {
                acao: "cadastrarPrioridade",
                idTipo: idTipo,
                prioridade: prioridadeInput
            },
            success: function () {
                alert("Sucesso ao atualizar configurações");
            },
            error: function () {
                alert("Erro ao salvar configurações");
            }
        });

    });
}