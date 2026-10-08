try
{
	$(document).on({
	    ajaxStart: function(){
	        $("body").addClass("loading"); 
	    },
	    ajaxStop: function(){ 
	        $("body").removeClass("loading"); 
	    }
	});
}
catch (ex)
{
	AlertCsx_E_TimeOut_8000ms('Erro ao carregar a página!!');
}
finally
{
	$("body").removeClass("loading");
}

var HTML_STATUS = "";
var ID_CAD_MONITORADO = null;
var ID_ALERTA_ORIGEM = null;
var CADASTRO_ATIVO = true;

$(document).ready(function()
{
	OcultarMensagemSucesso();
	OcultarAlertasPendentes();
	
	ObterDadosURL();
	
	ObterStatusAlertaVinculado();
	
	sleep(500).then(() =>
	{
		if (ID_CAD_MONITORADO != null)
			ObterAlertasVinculados(ID_CAD_MONITORADO);
	});
});

//sleep time expects milliseconds
function sleep (time) {
  return new Promise((resolve) => setTimeout(resolve, time));
}

function OcultarAlertasPendentes()
{
	divTratamentoAlertasVinculados = document.getElementById("divTratamentoAlertasVinculados");
	
	if (divTratamentoAlertasVinculados)
		divTratamentoAlertasVinculados.style.display = 'none';
}

function ExibirAlertasPendentes()
{
	divTratamentoAlertasVinculados = document.getElementById("divTratamentoAlertasVinculados");
	
	if (divTratamentoAlertasVinculados)
		divTratamentoAlertasVinculados.style.display = '';
}

function OcultarMensagemSucesso()
{
	divSemAlertasPendentes = document.getElementById("divSemAlertasPendentes");
	
	if (divSemAlertasPendentes)
		divSemAlertasPendentes.style.display = 'none';
}

function ExibirMensagemSucesso()
{
	divSemAlertasPendentes = document.getElementById("divSemAlertasPendentes");
	
	if (divSemAlertasPendentes)
		divSemAlertasPendentes.style.display = '';
}

function DesabilitarControlesAlertaVinculado()
{
	var status = document.querySelectorAll("[id='selStatus']");

	for(var i = 0; i < status.length; i++)
	{ 
		status[i].disabled = true;
	}
	
	btnSalvarVinculados = document.getElementById("btnSalvarVinculados");
	
	if (btnSalvarVinculados)
		btnSalvarVinculados.disabled = true;
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
		
		if (params.length > 0)
		{
//			console.log("params: " + params);
			
			var cadMonitoradoBruto = params[0].split('=');
			ID_CAD_MONITORADO = cadMonitoradoBruto[1];
			if (ID_CAD_MONITORADO === "")
			{
				console.log("Parâmetro ID_CAD_MONITORADO não informado!");
				return;
			}
//			console.log("ID_CAD_MONITORADO: " + ID_CAD_MONITORADO);
			
			var alertaBruto = params[1].split('=');
			ID_ALERTA_ORIGEM = alertaBruto[1];
			if (ID_ALERTA_ORIGEM === "")
			{
				console.log("Parâmetro ID_ALERTA_ORIGEM não informado!");
				return;
			}
//			console.log("ID_ALERTA_ORIGEM: " + ID_ALERTA_ORIGEM);
		}
	}	
	catch(e)
	{
		console.log("Falha ObterDadosURL():: " + e);
	}
}

function ObterStatusAlertaVinculado()
{
	var dataStringPesquisa = "acao=obterListaStatusVinculado";
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

			if (sucesso) {
				CriarComboStatusVinculado( jqXHR.responseText );
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

function CriarComboStatusVinculado(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var itensCombo = "";

	$xml.find('StatusAlertaOcorrencia').each(function()
	{
		var $item = $(this);
		
		var id = $item.find('id').text();
		var descricao = $item.find('descricao').text();
		var statusPadrao = $item.find('statusPadrao').text();
		
		statusPadrao = (statusPadrao === 'true');
		itensCombo = itensCombo + "<option value='" + id + "' " + (statusPadrao ? "selected='selected'" : "") + ">"+descricao+"</option> ";
	});
	
	HTML_STATUS = itensCombo;
}

function ObterAlertasVinculados(idCadMonitorado)
{
	var dataString = "acao=consultaAlertasPendCadMonitorado" +
						"&idCadMonitorado=" + idCadMonitorado;

    var url = urlRoot + "MuralhaDigital/AlertaOcorrencia";
        
//    console.log("dataStringPesquisa: " + dataString + " urlPesquisa: " + url);
        
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
				CarregarPagina( jqXHR.responseText );
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

function CarregarPagina(event) 
{
	var alertaVinculadoItens = document.getElementById("alertaVinculadoItens");
	var innerHTML = "";
	
	if (alertaVinculadoItens)
	{
		var xmlDoc = $.parseXML( event );
		var $xml = $(xmlDoc);
		
		var cont = 0;
	
		$xml.find('AlertaOcorrencia').each(function()
		{
			var $item = $(this);
			
			ExibirAlertasPendentes();
			cont++;
			
			var id = $item.find('id').text();
			var idTipoAlertaOcorrencia = $item.find('idTipoAlertaOcorrencia').text();
			var placa = $item.find('placa').text();
			var placaLida = $item.find('placaLida').text();
			var dataFormatada = $item.find('dataFormatada').text() + " " + $item.find('horaFormatada').text();
			var nomeLocal = $item.find('nomeLocal').text();
			var idImgObj1 = $item.find('idImgObj1').text();
			var idImgObj2 = $item.find('idImgObj2').text();
			var possuiImgObj1 = (!idImgObj1 == "");
			var possuiImgObj2 = (!idImgObj2 == "");
			
			var urlImgObj1 = urlRoot + "MuralhaDigital/Veiculo/Imagem?acao=ImagemByIdImg" + (possuiImgObj1 ? "&id=" + idImgObj1 : "");
			var urlImgObj2 = urlRoot + "MuralhaDigital/Veiculo/Imagem?acao=ImagemByIdImg" + (possuiImgObj2 ? "&id=" + idImgObj2 : "");
			
			innerHTML = innerHTML + "	<div class='row'> ";
			innerHTML = innerHTML + "		<div class='container-fluid g-1 g-md-2 bg-light bg-gradient border border-2'> ";
			innerHTML = innerHTML + "			<div class='row'> ";
			innerHTML = innerHTML + "				<div class='col-sm-2 d-flex align-items-center'> ";
			innerHTML = innerHTML + "			    	<div class='col-sm-12 text-center'> ";
			innerHTML = innerHTML + "			    		<img class='img-fluid img-thumbnail' src='" + urlImgObj1 + "'></img> ";
			innerHTML = innerHTML + "		    		</div> ";
			innerHTML = innerHTML + "				</div> ";
			innerHTML = innerHTML + "				<div class='col-sm-2 d-flex align-items-center'> ";
			innerHTML = innerHTML + "			    	<div class='col-sm-12 text-center'> ";
			innerHTML = innerHTML + "			    		<img class='img-fluid img-thumbnail' src='" + urlImgObj2 + "'></img> ";
			innerHTML = innerHTML + "		    		</div> ";
			innerHTML = innerHTML + "				</div> ";
			innerHTML = innerHTML + "				<div class='col-sm-5 p-2 text-start'> ";
			innerHTML = innerHTML + "				    <div class='row'> ";
			innerHTML = innerHTML + "				    	<label><small><strong>Placa Monitorada:</strong> "+placa+"</small></label> ";
			innerHTML = innerHTML + "			    	</div> ";
			innerHTML = innerHTML + "				    <div class='row'> ";
			innerHTML = innerHTML + "				    	<label><small><strong>Placa Lida:</strong> "+placaLida+"</small></label> ";
			innerHTML = innerHTML + "			    	</div> ";
			innerHTML = innerHTML + "				    <div class='row'> ";
			innerHTML = innerHTML + "				    	<label><small><strong>Data:</strong> " + dataFormatada + "</small></label> ";
			innerHTML = innerHTML + "			    	</div> ";
			innerHTML = innerHTML + "				    <div class='row'> ";
			innerHTML = innerHTML + "				    	<label><small><strong>Local:</strong> " + nomeLocal + "</small></label> ";
			innerHTML = innerHTML + "			    	</div> ";
			innerHTML = innerHTML + "			    	<input id='idAlerta' type='hidden' value="+id+"> ";
			innerHTML = innerHTML + "			    	<input id='idTipoAlerta' type='hidden' value="+idTipoAlertaOcorrencia+"> ";
			innerHTML = innerHTML + "				</div> ";
			innerHTML = innerHTML + "				<div class='col-sm-3'> ";
			innerHTML = innerHTML + "				    <div class='row d-flex align-items-center h-50'> ";
			innerHTML = innerHTML + "				    	<div class='col-sm-12 p-2 pe-3'> ";
			innerHTML = innerHTML + "					    	<select class='form-select' id='selStatus' name='status'>" + HTML_STATUS + "</select> ";
			innerHTML = innerHTML + "		                </div> ";
			innerHTML = innerHTML + "			    	</div> ";
			innerHTML = innerHTML + "				    <div class='row d-flex align-items-center h-50'> ";
			innerHTML = innerHTML + "				    	<div class='col-sm-12 p-2 pe-3'> ";
			innerHTML = innerHTML + "					    	<div class='d-grid gap-2'> ";
			innerHTML = innerHTML + "								<button id='btnConsultarVinculado' class='btn btn-primary' type='button' onclick=DetalharAlertaPorId(\'" + id + "\')>CONSULTAR</button> ";
			innerHTML = innerHTML + "							</div> ";
			innerHTML = innerHTML + "						</div> ";
			innerHTML = innerHTML + "			    	</div> ";
			innerHTML = innerHTML + "				</div> ";
			innerHTML = innerHTML + "			</div> ";
			innerHTML = innerHTML + "		</div> ";
			innerHTML = innerHTML + "	</div> ";
		});
	}
	
//	console.log("innerHTML: " + innerHTML);
//	console.log("alertaVinculadoItens: " + alertaVinculadoItens.innerHTML);
	
	alertaVinculadoItens.innerHTML = innerHTML;
	
	if (cont == 0)
	{
		OcultarAlertasPendentes();
		ExibirMensagemSucesso();
	}
}

function DetalharAlertaPorId(idAlerta)
{
	var linkHTML = "/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=" + idAlerta + "&Voltar=1&AlertasVinculados=1";
	window.location.assign(linkHTML);
}

function MontarXmlAlertasVinculados()
{
	var alertas = document.querySelectorAll("[id='idAlerta']");
	var tiposAlertas = document.querySelectorAll("[id='idTipoAlerta']");
	var status = document.querySelectorAll("[id='selStatus']");

	var xmlItens = "";
	
	for(var i = 0; i < alertas.length; i++)
	{ 
//  		console.log("Item " + (i+1) + ": Alerta " + alertas[i].value + " | Status: " + status[i].value);
  		xmlItens = xmlItens + "<alertaVinculadoAtualizar><idAlerta>"+alertas[i].value+"</idAlerta><idTipoAlerta>"+tiposAlertas[i].value+"</idTipoAlerta><idStatusAlerta>"+status[i].value+"</idStatusAlerta></alertaVinculadoAtualizar>"
	}
	
	var xmlAlertasVinculados = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?><alertasVinculadosAtualizar>"+xmlItens+"</alertasVinculadosAtualizar>"
//	console.log(xmlAlertasVinculados);
	
	return xmlAlertasVinculados; 
}

function ProcessarAlertasVinculados()
{
	try
	{
		var xmlAlertasVinculados = MontarXmlAlertasVinculados();
		
		var dataString = "acao=processarAlertasVinculados" +
							"&idCadMonitorado=" + ID_CAD_MONITORADO +
							"&idAlertaOrigem=" + ID_ALERTA_ORIGEM +
							"&xmlAlertasVinculados=" + xmlAlertasVinculados;
	        
	    var url = urlRoot + "MuralhaDigital/Alerta";
	
		if (confirm("Confirma o tratamento escolhido para todos os alertas?"))
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
					var cadastroAtivo = $xml.find('cadastroAtivo').text();
					
					sucesso = (sucesso === 'true' || sucesso === '');
					CADASTRO_ATIVO = (cadastroAtivo === 'true');
					
					console.log("CADASTRO_ATIVO: ");
					console.log(CADASTRO_ATIVO);
					
					if (sucesso)
					{
						SuccessCsx_E_TimeOut_8000ms(msgResposta);
						OcultarAlertasPendentes();
						ExibirMensagemSucesso();
//						DesabilitarControlesAlertaVinculado();
						
						if (CADASTRO_ATIVO)
							AbrirModalEncerrarMonitoramento();
							
					}
					else
					{
						WarningCsx_E_TimeOut_8000ms(msgResposta);
					}
		        },
		        error: 		function(jqXHR, textStatus, errorThrown)
		        {
		        	AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', descErrorContainerModal);
		        },
		        beforeSend: function(jqXHR, settings){},
		        complete: 	function(jqXHR, textStatus){}
		    });
		}
	}
	catch (ex)
	{
		AlertCsx_E_TimeOut_8000ms('Erro ao processar os alertas vinculados!!');
		$("body").removeClass("loading");
	}
}

function FecharJanelaTratarAlertaVinculado()
{
	window.close();
}