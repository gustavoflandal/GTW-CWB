var CONSULTA_PREDEFINIDA = false;
var ID_EQUIPAMENTO = "";
var TEMPO = "";
var TIPO_REGISTRO = "";
var ID_TIPO_ALERTA_OCORRENCIA = "";
var ATUALIZAR_TITULO = false;

const CAD_VEICULO_MONITORADO = getUrlParam("cad-veiculo-monitorado");
const ID_REGISTRO_FATO_ALERTA = getUrlParam("registro-fato");
const ID_USUARIO_ALERTA = getUrlParam("usuario");
const ASSINADO = getUrlParam("assinado");
const STATUS = getUrlParam("statusAlerta");

$().ready(function () 
{
	const permitido = verificarPermissaoGrupoUsuarioConsulta();
	if (permitido) {
		$("#divChkSupervisionado").show();
	}
	CarregarPametrosPaginacao();
	ObterDadosURL();
	
	// 🔹 Preencher o campo placa se tiver na URL
	const urlParams = new URLSearchParams(window.location.search);
	const placaParam = urlParams.get("placa");
	if (placaParam) {
		$("#placaConsulta").val(placaParam.trim().toUpperCase());

		// 🔹 Remover o parâmetro "placa" da URL sem recarregar
		urlParams.delete("placa");
		const newUrl = window.location.pathname + (urlParams.toString() ? "?" + urlParams.toString() : "");
		window.history.replaceState({}, document.title, newUrl);
	}
	
	const dataInicio = urlParams.get("dataInicio");
	const chkSupervisionadoParam = urlParams.get("supervisionado");
	const chkAssinadosPendentesParam = urlParams.get("pendente")
	if (dataInicio) {
		const partes = dataInicio.split("-");
		document.getElementById('dataInicioInput').value =
			partes.length === 3 ? `${partes[2]}/${partes[1]}/${partes[0]} 00:00` : dataInicio;
			
		const hoje = new Date();
		const dia = String(hoje.getDate()).padStart(2, '0');
		const mes = String(hoje.getMonth() + 1).padStart(2, '0');
		const ano = hoje.getFullYear();
		const hora = String(hoje.getHours()).padStart(2, '0');
		const minuto = String(hoje.getMinutes()).padStart(2, '0');
		
		document.getElementById('dataFimInput').value = `${dia}/${mes}/${ano}  ${hora}:${minuto}`;
	}

	if(chkSupervisionadoParam){
	const chk = document.getElementById("chkSupervisionado");
		if (chk) {
			chk.checked = true;
		}
	}

	if(chkAssinadosPendentesParam){
	const chk = document.getElementById("chkAssinadosPendentes");
		if (chk) {
			chk.checked = true;
		}
	}
	
	if (typeof __notificacao_quem_me_importou !== "undefined") __notificacao_quem_me_importou = 'consulta-alerta-ocorrencia';
	
	if (CONSULTA_PREDEFINIDA)
	{
		ocultarFiltrosConsulta();
		executaPesquisaPredefinida();
		
	}
	else
	{
		CarregarComponenteData(false);
		
		$('.placa').on('keypress', function (e) 
		{
	        var input = $(this);
	        var value = input.val();
	        var key = e.originalEvent.key;
	        
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
	
		carregarCombos();
		//executaPesquisa(false);		
	  	setTimeout(function() {
			executaPesquisa(false);
		}, 500);
	}
});
function getUrlParam(param) {
    const url = new URL(window.location.href);
    return url.searchParams.get(param);
}

// deixa global

function AddLoading()
{
	$("body").addClass("loading");
}
function RemoveLoading()
{
	$("body").removeClass("loading");
}

function AtualizarItensPaginacao()
{
	if (CONSULTA_PREDEFINIDA)
		executaPesquisaPredefinida();
	else
		executaPesquisa(false);
}

function ObterDadosURL()
{
	try
	{
		var params = window.location.href.substring(window.location.href.indexOf('?')+1);	
		params = params.split('&');
		
//		console.log("params.length: " + params.length);
//		params.forEach((item, indice) => { 
//    		console.log(item)});

		if (params.length > 3)
		{
			
			var equipamentoBruto = params[0].split('=');
			ID_EQUIPAMENTO = equipamentoBruto[1];
			if (ID_EQUIPAMENTO === "")
			{
				console.log("Parâmetro ID_EQUIPAMENTO não informado! Redirecionando para tela de consulta com filtros...");
				window.location.replace("/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp");
				return;
			}
			console.log("ID_EQUIPAMENTO: " + ID_EQUIPAMENTO);
			
			var tempoBruto = params[1].split('=');
			TEMPO = tempoBruto[1];
			if (TEMPO === "")
			{
				console.log("Parâmetro TEMPO não informado! Redirecionando para tela de consulta com filtros...");
				window.location.replace("/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp");
				return;
			}
			console.log("TEMPO: " + TEMPO);
			
			var tipoRegistroBruto = params[2].split('=');
			TIPO_REGISTRO = tipoRegistroBruto[1];
			if (TIPO_REGISTRO === "")
			{
				console.log("Parâmetro TIPO_REGISTRO não informado! Redirecionando para tela de consulta com filtros...");
				window.location.replace("/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp");
				return;
			}
			console.log("TIPO_REGISTRO: " + TIPO_REGISTRO);
			
			var tipoAlertaOcorrenciaBruto = params[3].split('=');
			ID_TIPO_ALERTA_OCORRENCIA = tipoAlertaOcorrenciaBruto[1];
			if (ID_TIPO_ALERTA_OCORRENCIA === "")
			{
				console.log("Parâmetro ID_TIPO_ALERTA_OCORRENCIA não informado! Redirecionando para tela de consulta com filtros...");
				window.location.replace("/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp");
				return;
			}
			console.log("ID_TIPO_ALERTA_OCORRENCIA: " + ID_TIPO_ALERTA_OCORRENCIA);
		}
		
		if (ID_EQUIPAMENTO != "" && TEMPO != "" && TIPO_REGISTRO != "" && ID_TIPO_ALERTA_OCORRENCIA != "")
			CONSULTA_PREDEFINIDA = true;
	}	
	catch(e)
	{
		CONSULTA_PREDEFINIDA = false;
		console.log("Falha ObterDadosURL():: " + e);
		console.log("Redirecionando para tela de consulta com filtros...");
		window.location.replace("/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp");
	}			
}

function ocultarFiltrosConsulta()
{
	var divFiltrosConsulta = document.getElementById("divFiltrosConsulta");
	
	if (divFiltrosConsulta)
		divFiltrosConsulta.style.display = 'none';
}

function exibirFiltrosConsulta()
{
	var divFiltrosConsulta = document.getElementById("divFiltrosConsulta");
	
	if (divFiltrosConsulta)
		divFiltrosConsulta.style.display = '';
}

function carregarCombos()
{
	try 
    {
		AddLoading();
//		obterTiposRegistro();
		obterEquipamentosGenerico("selEquipamento");
		obterTiposAlertasOcorrencias();
		obterStatusAlertaOcorrencia(); 
	}
	catch (e) 
	{
		console.log("Erro ao carregar combos para filtros!!");
		console.log(e);
		RemoveLoading();
	}
	finally
	{
		RemoveLoading();
	}
}

function obterTiposRegistro()
{

    var urlPesquisa = urlRoot + "MuralhaDigital/AlertaOcorrencia/TipoRegistro";
        
    $.ajax({
        type: 		"GET",
        url: 		urlPesquisa,
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

//        	console.log("response: " + jqXHR.responseText);]
			if (sucesso) {
				carregaComboTiposRegistro( jqXHR.responseText );
//				console.log("executaPesquisa()::Chegou dados");
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
//        	alert("(executaPesquisa) Erro ao processar requisição ao servidor!!");
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });      
}

function carregaComboTiposRegistro(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);

	var item = 1;

	$xml.find('TipoRegistro').each(function()
	{
		
		var $item = $(this);
		
		var id = $item.find('id').text();
		var descricao = $item.find('descricao').text();

		$('#selTipo').append($('<option>', {
		    value: id,
		    text: descricao
		}));
		
		if (item === 1)
		{
			$("#selTipo > [value=" + id + "]").attr("selected", "true");
		}
		
		item = item + 1;
				
	});
}

function obterTiposAlertasOcorrencias()
{

    var urlPesquisa = urlRoot + "MuralhaDigital/AlertaOcorrencia/Tipo";
        
    $.ajax({
        type: 		"GET",
        url: 		urlPesquisa,
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

//        	console.log("response: " + jqXHR.responseText);]
			if (sucesso) {
				carregaComboTiposAlertasOcorrencias( jqXHR.responseText );
				carregaComboTiposAlertasOcorrenciasModalEdit( jqXHR.responseText );
//				console.log("executaPesquisa()::Chegou dados");
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
//        	alert("(executaPesquisa) Erro ao processar requisição ao servidor!!");
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });      
}

function carregaComboTiposAlertasOcorrencias(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);

	$xml.find('TipoAlertaOcorrencia').each(function()
	{
		
		var $item = $(this);
		
		var id 		= $item.find('id'	).text();
		var tipo 	= $item.find('tipo'	).text();

		$('#selTipoAlertaOcorrencia').append($('<option>', {
		    value: id,
		    text: tipo
		}));
				
	});
}

function obterStatusAlertaOcorrencia()
{

	var tipoRegistro = document.getElementById("selTipo").value;
	var descricaoRegistro = $("#selTipo option:selected").text();
	
	var dataStringPesquisa = "acao=obterListaStatus" +
								"&tipoRegistro=" + tipoRegistro;

    var urlPesquisa = urlRoot + "MuralhaDigital/AlertaOcorrencia/Status";
        
    $.ajax({
        type: 		"GET",
        url: 		urlPesquisa,
        data: 		dataStringPesquisa,
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

//        	console.log("response: " + jqXHR.responseText);]
			if (sucesso) {
				carregaComboStatusAlertaOcorrencia(descricaoRegistro, jqXHR.responseText );
//				console.log("executaPesquisa()::Chegou dados");
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
//        	alert("(executaPesquisa) Erro ao processar requisição ao servidor!!");
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });      
}

function carregaComboStatusAlertaOcorrencia( descricaoRegistro, event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	$('#selStatus').empty();
	
    $('#selStatus').append($('<option>', {
        value: 0,
        text: "--Selecione o Status--"
    }));
	
	 var idSelecionado = null;
    if (descricaoRegistro === "ALERTA") {
        idSelecionado = "5479c6d9-7381-4492-99be-442ef2e741b0"; // coloque aqui o ID correto
    } else if (descricaoRegistro === "OCORRÊNCIA") {
        idSelecionado = "3c0612d6-3950-4861-8ca4-2a261ae787af"; // coloque aqui o ID correto
    }
    
    if (STATUS !== null && STATUS !== "") {
    	idSelecionado = "15ebba5f-c805-449e-83cc-227ed3b3ad3c";
	}
	
	$xml.find('StatusAlertaOcorrencia').each(function()
	{
		
		var $item = $(this);
		
		var id = $item.find('id').text();
		var tipo = $item.find('descricao').text();

		$('#selStatus').append($('<option>', {
		    value: id,
		    text: tipo,
		    selected: (idSelecionado !== null && id === idSelecionado)
		}));
				
	});
}

function executaPesquisa(reiniciarPaginaAtual)
{
	if (reiniciarPaginaAtual && (new URLSearchParams(window.location.search).has("cad-veiculo-monitorado") || new URLSearchParams(window.location.search).has("registro-fato"))) {
        WarningCsx_E_TimeOut_8000ms("Clique em Limpar Filtros para realizar a proxima pesquisa..");
        return;
    }
		
	ATUALIZAR_TITULO = false;
	
	var tipoRegistro 			= document.getElementById("selTipo"					).value;
	var tipoAlertaOcorrencia 	= document.getElementById("selTipoAlertaOcorrencia"	).value;
	var status 					= document.getElementById("selStatus"				).value;
	var placa 					= document.getElementById("placaConsulta"			).value;
	var equipamento = document.getElementById("selEquipamento").value;
	
	var supervisionadoCheckbox = document.getElementById("chkSupervisionado");
	var supervisionado = supervisionadoCheckbox ? supervisionadoCheckbox.checked : false;

	var privadoCheckbox = document.getElementById("chkPrivado");
	var privado = privadoCheckbox ? privadoCheckbox.checked : false;
	
	var assinadosPendentesCheckbox = document.getElementById("chkAssinadosPendentes");
	var assinadosPendentes = assinadosPendentesCheckbox ? assinadosPendentesCheckbox.checked : false;
	
  	var dataIni = TratarDataHora($("#dataInicio").find("input").val());
  	var dataFim = TratarDataHora($("#dataFim").find("input").val());

	limparTabelaResultadoAlertas();
	
	if (reiniciarPaginaAtual)
		ReiniciaPaginaAtualComponentePaginacao();
	
    var dataStringPesquisa = 	"tipoRegistro=" 				+ tipoRegistro 					+
								"&tipoAlertaOcorrencia=" 		+ tipoAlertaOcorrencia 			+
    							"&status=" 						+ status 						+
								"&equipamento=" 				+ equipamento 					+
    							"&placa=" 						+ placa 						+ 
								"&privado=" 					+ privado 						+
								"&supervisionado=" 				+ supervisionado 				+
								"&assinadosPendentes=" 			+ assinadosPendentes 			+								
    							"&dataIni=" 					+ dataIni 						+ 
    							"&dataFim=" 					+ dataFim 						+
    							"&paginacaoItensPorPagina="		+ PAGINACAO_ITENS_POR_PAGINA	+
    							"&paginacaoOffset="				+ PAGINACAO_OFFSET				+
								"&acao=consultaPorFiltrosTela";
							if (typeof CAD_VEICULO_MONITORADO !== "undefined" && CAD_VEICULO_MONITORADO !== null && CAD_VEICULO_MONITORADO !== "") {
							    dataStringPesquisa += "&cadVeiculoMonitorado=" + encodeURIComponent(CAD_VEICULO_MONITORADO);
							} if (typeof ID_REGISTRO_FATO_ALERTA !== "undefined" && ID_REGISTRO_FATO_ALERTA !== null && ID_REGISTRO_FATO_ALERTA !== "") {
							    dataStringPesquisa += "&idFato=" + encodeURIComponent(ID_REGISTRO_FATO_ALERTA);
							} if(typeof ID_USUARIO_ALERTA !== "undefined" && ID_USUARIO_ALERTA !== null && ID_USUARIO_ALERTA !== "") {
							    dataStringPesquisa += "&usuario=" + encodeURIComponent(ID_USUARIO_ALERTA);
							} if (typeof ASSINADO !== "undefined" && ASSINADO !== null && ASSINADO !== "") {
							    dataStringPesquisa += "&assinado=" + encodeURIComponent(ASSINADO);
							}						
							
        
    var urlPesquisa = urlRoot + "MuralhaDigital/AlertaOcorrencia";
        
    //console.log("dataStringPesquisa: " + dataStringPesquisa + " urlPesquisa: " + urlPesquisa);
        
    AddLoading();
    $.ajax({
        type: 		"GET",
        url: 		urlPesquisa,
        data: 		dataStringPesquisa,
        dataType:	"xml",
           
        //if received a response from the server
        success: 	function( data, textStatus, jqXHR) 
        {	
        	var event = jqXHR.responseText;
        	var xmlDoc = $.parseXML( event );
			var $xml = $(xmlDoc);
    			
			var sucesso 	= $xml.find('sucesso'		).text();
			var msgResposta = $xml.find('msgResposta'	).text();
			
			sucesso = (sucesso === 'true' || sucesso === '');
			
			if (sucesso) {
				processaDados( jqXHR.responseText );
				if(CAD_VEICULO_MONITORADO != null){								
					document.getElementById("placaConsulta").value = $xml.find('placa').first().text();					
				}
				
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
//        	alert("(executaPesquisa) Erro ao processar requisição ao servidor!!");
			RemoveLoading();
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus)
        {
			AtualizarParametrosComponentePaginacao( jqXHR.responseText );
			CriarComponentePaginacao();
			RemoveLoading();
		}
    });      
}
		
function limparTabelaResultadoAlertas()
{
//	console.log("limparTabelaResultado()::Executando...");
	
	var tableHeaderRowCount = 1;
	var table = document.getElementById("tabela");
	var rowCount = table.rows.length;
	
	for (var i = tableHeaderRowCount; i < rowCount; i++) 
	    table.deleteRow(tableHeaderRowCount);
		
}
		
function limparFiltrosAlerta()
{
	window.location.href = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp";
	document.getElementById("selTipoAlertaOcorrencia"	).value = "0";
	document.getElementById("selStatus"					).value = "0";
	document.getElementById("placaConsulta"				).value = "";
	document.getElementById("selEquipamento").value = "0";
	
  	$dataIni = $("#dataInicio").find("input");
    $dataIni.val('');
    $dataIni.removeData();
    
    $dataFim = $("#dataFim").find("input");
    $dataFim.val('');
    $dataFim.removeData();
}


function GerarRelatorio(formato)
{
	var tabela = 'tabela'; // id da tabela
	var listaIgnoreColumns = [5, 6]; // index das colunas que devem ser ignoradas
	var filename = 'Relatorio' + '.' + formato;
	
	if (formato == 'pdf')
		GerarRelatorioPDF(tabela, filename, listaIgnoreColumns);
	else if (formato == 'xls')
		GerarRelatorioXLS(tabela, filename, listaIgnoreColumns);
	else if (formato == 'xlsx')
		GerarRelatorioXLSX(tabela, filename, listaIgnoreColumns);
}


function processaDados(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	$xml.find('AlertaOcorrencia').each(function()
	{
		
		var $item = $(this);
		
		var id = $item.find('id').text();
		var tipoRegistro = $item.find('tipoRegistro').text();
		var tipoAlertaOcorrencia = $item.find('tipoAlertaOcorrencia').text();
		var status = $item.find('status').text();
		var placa = $item.find('placa').text();
		var dataFormatada = $item.find('dataFormatada').text() + " " + $item.find('horaFormatada').text();
		var idAlerta = $item.find('idAlerta').text();
		var idCadVeiculoMonitorado = $item.find('idCadVeiculoMonitorado').text();
		var cadMonitoradoAtivo = $item.find('cadMonitoradoAtivo').text() === 'true';
		var idRegistroFato = $item.find('idRegistroFato').text();
		
		var onclickVerCadMonitorado = 'onclick="AbrirEditarMonitorado(\'' + idCadVeiculoMonitorado + '\',' + cadMonitoradoAtivo + ',' + false + ')"';
		
		var htmlBotoesAcao = "<div class='btn-group d-flex justify-content-center align-items-center' role='group'>" +
									"<button id='btnVerCadMonitorado_" + id +  "' type='button' class='btn btn-sm btn-warning' data-bs-toggle='modal' data-bs-target='#modalEditarMonitorado' title='Ver cadastro de monitoramento'" +
									onclickVerCadMonitorado + 
									">" +
						                "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='currentColor' class='bi bi-camera-video' viewBox='0 0 16 16'> " +
  						                	"<path fill-rule='evenodd' d='M0 5a2 2 0 0 1 2-2h7.5a2 2 0 0 1 1.983 1.738l3.11-1.382A1 1 0 0 1 16 4.269v7.462a1 1 0 0 1-1.406.913l-3.111-1.382A2 2 0 0 1 9.5 13H2a2 2 0 0 1-2-2V5zm11.5 5.175 3.5 1.556V4.269l-3.5 1.556v4.35zM2 4a1 1 0 0 0-1 1v6a1 1 0 0 0 1 1h7.5a1 1 0 0 0 1-1V5a1 1 0 0 0-1-1H2z'/> " +
										"</svg>" +
									"</button>" + 
									"<button id='btnDetalhar_" + id +  "' type='button' class='btn btn-sm btn-primary' title='Detalhar' onclick=detalharAlertaPorId(\'" + idAlerta + "\')>" +
						                "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='currentColor' class='bi bi-search' viewBox='0 0 16 16'>" +
						  					"<path d='M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z'></path>" +
										"</svg>" +
									"</button>" + 
									"<button  type='button' class='btn btn-sm btn-secondary' title='Registro de Fato' "+ (idRegistroFato === '0' ? "disabled " : " ") + "onclick=abrirModalBO(" + idRegistroFato + ")>" +
									    "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='currentColor' viewBox='0 0 24 24'>" +
									        "<path d='M4 4h16v2H4V4zm0 4h10v2H4V8zm0 4h16v2H4v-2zm0 4h10v2H4v-2z'/>" +
									        "<path d='M18 14l3 3-3 3v-2h-4v-2h4v-2z'/>" +
									    "</svg>" +
									"</button>" +
								"</div>";
								
		var htmlMiniatura = "<div><center>" +
								"<img src='/MuralhaDigital/Veiculo/Imagem?acao=ImagemByIdAlerta&idAlerta=" + idAlerta + "' " +
//								"class='mh-35' style='height: 40px' alt='Imagem'>" +	
								"class='h-75 w-75 img-responsive' alt='Imagem'>" +
							"</center></div>";
		
		if (ATUALIZAR_TITULO)
		{
			var tituloTelaConsulta		= document.getElementById("tituloTelaConsulta"	);
			var tituloListaResultado	= document.getElementById("tituloListaResultado");
			
			var tipoRegistroAlt = tipoRegistro.toLowerCase();
			tipoRegistroAlt = capitalizeFirstLetter(tipoRegistroAlt);
			
			if (tituloTelaConsulta)
				tituloTelaConsulta.innerHTML = "Consulta de " + tipoRegistroAlt + "s de " + tipoAlertaOcorrencia;
			
			if (tituloListaResultado)
				tituloListaResultado.innerHTML = "Lista de " + tipoRegistroAlt + "s:";
				
			ATUALIZAR_TITULO = false;
		}
			

		var tableRef = document.getElementById("tabela").getElementsByTagName('tbody')[0];

		var row   = tableRef.insertRow(tableRef.rows.length);
		row.insertCell(0).innerHTML = "<small>" + tipoRegistro 			+ "</small>";
		row.insertCell(1).innerHTML = "<small>" + tipoAlertaOcorrencia 	+ "</small>";
		row.insertCell(2).innerHTML = "<small>" + status 				+ "</small>";
		row.insertCell(3).innerHTML = "<small>" + placa 				+ "</small>";
		row.insertCell(4).innerHTML = "<small>" + dataFormatada 		+ "</small>";
		row.insertCell(5).innerHTML = htmlMiniatura;
		row.insertCell(6).innerHTML = htmlBotoesAcao;
		
		$('input#txt_consulta').quicksearch('table#tabela tbody tr');
				
	});
}

function detalharAlertaPorId(idAlerta)
{
	
	var linkHTML = "/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=" + idAlerta;
	
	if (CONSULTA_PREDEFINIDA){
		window.location.assign(linkHTML + '&Voltar=1');
	}else{
		window.open(linkHTML,"_blank");
	}
}

function executaPesquisaPredefinida()
{
	limparTabelaResultado();
	
    var dataStringPesquisa = "idLocal=" 					+ ID_EQUIPAMENTO 				+
    							"&tempo=" 					+ TEMPO 						+
								"&tipoRegistro=" 			+ TIPO_REGISTRO 				+
								"&tipoAlertaOcorrencia=" 	+ ID_TIPO_ALERTA_OCORRENCIA 	+
    							"&paginacaoItensPorPagina="	+ PAGINACAO_ITENS_POR_PAGINA	+
    							"&paginacaoOffset="			+ PAGINACAO_OFFSET				+
								"&acao=consultaPredefinida";
	if (typeof ID_ALERTA_VEICULO !== "undefined" && ID_ALERTA_VEICULO !== null && ID_ALERTA_VEICULO !== "") {
        dataStringPesquisa += "&idAlertaVeiculo=" + ID_ALERTA_VEICULO;
    }
            
    var urlPesquisa = urlRoot + "MuralhaDigital/AlertaOcorrencia";
        
//    console.log("dataStringPesquisa: " + dataStringPesquisa + " urlPesquisa: " + urlPesquisa);
        
    $.ajax({
        type: 		"GET",
        url: 		urlPesquisa,
        data: 		dataStringPesquisa,
        dataType:	"xml",
           
        //if received a response from the server
        success: 	function( data, textStatus, jqXHR) 
        {
        	var event = jqXHR.responseText;
        	var xmlDoc = $.parseXML( event );
			var $xml = $(xmlDoc);
    			
			var sucesso 	= $xml.find('sucesso'		).text();
			var msgResposta = $xml.find('msgResposta'	).text();
			
			sucesso = (sucesso === 'true' || sucesso === '');
			
			if (sucesso) {
				ATUALIZAR_TITULO = true;
				processaDados( jqXHR.responseText );
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus)
        {
			AtualizarParametrosComponentePaginacao( jqXHR.responseText );
			CriarComponentePaginacao();
		}
 
    });      
}

function capitalizeFirstLetter(valor) {
    return valor[0].toUpperCase() + valor.slice(1);
}

function AtualizaListagem()
{
	executaPesquisa();
}

async function verificarPermissaoGrupoUsuarioConsulta() {
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

function abrirModalBO(idRegistro){
	id = idRegistro;	
	abrirModalEditarRegistroDeFatoAbos(id)
}

function ReiniciaPaginaAtualComponentePaginacao()
{
	PAGINACAO_PAGINA_ATUAL = 1;
	PAGINACAO_OFFSET = PAGINACAO_ITENS_POR_PAGINA * (PAGINACAO_PAGINA_ATUAL-1);
}


/*function obterAlertasPendentesAssinatura()
{

    var urlPesquisa = urlRoot + "MuralhaDigital/Alerta";
    var dataStringPesquisa = "acao=obterAlertasPendentesAssinatura"
    console.log('AQUI ', dataStringPesquisa)
        
    $.ajax({
        type: 		"GET",
        url: 		urlPesquisa,
        data:		dataStringPesquisa,
        dataType:	"text",
           
        //if received a response from the server
        success: 	function( data, textStatus, jqXHR) 
        {
        	var qtdeAlertas = parseInt(data);
        	console.log("Quantidade ",qtdeAlertas)
        	if(qtdeAlertas > 0){
        	 	badge.style.display = "inline-block";		      
		    } else {
		        badge.style.display = "none";		       
		    }
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!reree');
//        	alert("(executaPesquisa) Erro ao processar requisição ao servidor!!");
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });      
}*/
