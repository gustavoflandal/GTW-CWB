var HTML_STATUS = "";
var ID_CAD_MONITORADO = null;
var ID_ALERTA_ORIGEM = null;
var CADASTRO_ATIVO = true;
var VOLTAR_VISUALIZAR_ALERTAS = false;

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

$(document).ready(function()
{
	CarregarPametrosPaginacao();
	OcultarListaAlertas();
	ObterDadosURL();
	ObterDadosSessionStorage();
	
	sleep(500).then(() =>
	{
		if (ID_CAD_MONITORADO != null)
			ObterVeicMonitoradoPorIdListaAlerta();
			ObterAlertasCadMonitorado();
	});
});

function AtualizarItensPaginacao()
{
	ObterAlertasCadMonitorado();
}

//sleep time expects milliseconds
function sleep (time) {
  return new Promise((resolve) => setTimeout(resolve, time));
}

function OcultarListaAlertas()
{
	divListaAlertas = document.getElementById("divListaAlertas");
	
	if (divListaAlertas)
		divListaAlertas.style.display = 'none';
}

function ExibirListaAlertas()
{
	divListaAlertas = document.getElementById("divListaAlertas");
	
	if (divListaAlertas)
		divListaAlertas.style.display = '';
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
		}
	}	
	catch(e)
	{
		console.log("Falha ObterDadosURL():: " + e);
	}
}

function ObterDadosSessionStorage()
{
	var voltarVisualizarAlertas = sessionStorage.getItem("VOLTAR_VISUALIZAR_ALERTAS");
	if (voltarVisualizarAlertas != null)
		VOLTAR_VISUALIZAR_ALERTAS = voltarVisualizarAlertas;
	
	if (VOLTAR_VISUALIZAR_ALERTAS)
	{
		PAGINACAO_ITENS_POR_PAGINA = parseInt(sessionStorage.getItem("PAGINACAO_ITENS_POR_PAGINA"));
		PAGINACAO_OFFSET = parseInt(sessionStorage.getItem("PAGINACAO_OFFSET"));
		PAGINACAO_PAGINA_ATUAL = parseInt(sessionStorage.getItem("PAGINACAO_PAGINA_ATUAL"));
	}
	LimparSessionStorage();
}

function LimparSessionStorage()
{
	Object.keys(sessionStorage).forEach(function(key)
	{
   		sessionStorage.removeItem(key);
	});
}

function ObterVeicMonitoradoPorIdListaAlerta()
{
	var dataString = "acao=obterPorId" +
						"&id=" + ID_CAD_MONITORADO;
	
	var urlPesquisa = urlRoot + "MuralhaDigital/Monitorado";
	
//	console.log('ObterAlerta():: URL: ' + url + ' --> ' + dataString);
	
	$.ajax(
	{
	    type: 		"GET",
	    url: 		urlPesquisa,
		data: 		dataString,
	    dataType:	"xml",
		
		success: function(xml, textStatus, jqXHR) {
		    var xmlDoc = $.parseXML(jqXHR.responseText);
		    var $xml = $(xmlDoc);

		    var sucessoNode = $xml.find('sucesso');
		    var isErro = sucessoNode.length > 0;

		    if (isErro) {
		        var sucesso = sucessoNode.text().trim().toLowerCase() === "true";
		        if (!sucesso) {
		            var msg = $xml.find('msgResposta').text().trim() || "Erro ao obter veículo monitorado.";
		            WarningCsx_E_TimeOut_8000ms(msg);
		            return;
		        }
		    }

		    TratarVeicMonitoradoListaAlerta(jqXHR);
		},		
	    error: function(e, b, error) {
			console.log("Erro ao obter Veículo Monitorado por Id." + e.respone);
			AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao obter Veículo Monitorado!!', "#error_container_modal_editar_veiculo_mon");
	    },
		async: false
	});
}

function TratarVeicMonitoradoListaAlerta(jqXHR)
{	
	var xmlDoc = $.parseXML( jqXHR.responseText );
	var $xml = $(xmlDoc);
	
	var monitorado = $xml.find('VeiculoMonitorado');

	var tipoAlertaOcorrencia = monitorado.find('tipoAlertaOcorrencia').text();
	var placa = monitorado.find('placa').text();
	var nome = monitorado.find('nome').text();
	var cadAtivo = monitorado.find('ativo').text() === 'true';
	
	var nomeMonitoradoVerAlerta = document.getElementById("nomeMonitoradoVerAlerta");
	var cadastroAtivoVerAlerta = document.getElementById("cadastroAtivoVerAlerta");
	var placaVerAlerta	= document.getElementById("placaVerAlerta");
	var tipoAlertaVerAlerta = document.getElementById("tipoAlertaVerAlerta");
	
	if (nomeMonitoradoVerAlerta)
		nomeMonitoradoVerAlerta.value = nome;
		
	if (cadastroAtivoVerAlerta)
	{
		cadastroAtivoVerAlerta.innerHTML = (cadAtivo ? "ATIVO" : "INATIVO");
		$("#cadastroAtivoVerAlerta").removeClass("btn-light"); 
		$("#cadastroAtivoVerAlerta").addClass((cadAtivo ? "btn-success" : "btn-danger"));
	}
		
	if (placaVerAlerta)
		placaVerAlerta.value = placa;
		
	if (tipoAlertaVerAlerta)
		tipoAlertaVerAlerta.value = tipoAlertaOcorrencia;
}

function ObterAlertasCadMonitorado()
{
	var dataString = "acao=consultaAlertasPorCadMonitorado" +
						"&idCadMonitorado=" + ID_CAD_MONITORADO +
    					"&paginacaoItensPorPagina="	+ PAGINACAO_ITENS_POR_PAGINA +
    					"&paginacaoOffset="	+ PAGINACAO_OFFSET;

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
        complete: 	function(jqXHR, textStatus)
        {
			AtualizarParametrosComponentePaginacao( jqXHR.responseText );
			CriarComponentePaginacao();
		}
    });
}

function CarregarPagina(event) 
{
	var listaAlertaItens = document.getElementById("listaAlertaItens");
	var innerHTML = "";
	
	if (listaAlertaItens)
	{
		var xmlDoc = $.parseXML( event );
		var $xml = $(xmlDoc);
		
		var cont = 0;
	
		$xml.find('AlertaOcorrencia').each(function()
		{
			var $item = $(this);
			
			ExibirListaAlertas();
			cont++;
			
			var id = $item.find('id').text();
			var idTipoAlertaOcorrencia = $item.find('idTipoAlertaOcorrencia').text();
			var statusAlerta = $item.find('status').text()
			var placaLida = $item.find('placaLida').text();
			var dataFormatada = $item.find('dataFormatada').text() + " " + $item.find('horaFormatada').text();
//			var nomeLocal = $item.find('nomeLocal').text();
			var equipamento = $item.find('equipamento').text();
			
			var descartado = ($item.find('descartado').text() === "true");
			var ocorrenciaGerada = ($item.find('ocorrenciaGerada').text() === "true");
			var alertaVinculado = ($item.find('alertaVinculado').text() === "true");
			
			var corTextoStatusAlerta = 'text-dark';
			if (descartado)
				corTextoStatusAlerta = 'text-danger';
			else if (ocorrenciaGerada)
				corTextoStatusAlerta = 'text-success';
			else if (alertaVinculado)
				corTextoStatusAlerta = 'text-warning';
			
			var idImgObj1 = $item.find('idImgObj1').text();
			var idImgObj2 = $item.find('idImgObj2').text();
			var possuiImgObj1 = (!idImgObj1 == "");
			var possuiImgObj2 = (!idImgObj2 == "");
			
			var urlImgObj1 = urlRoot + "MuralhaDigital/Veiculo/Imagem?acao=ImagemByIdImg" + (possuiImgObj1 ? "&id=" + idImgObj1 : "");
			var urlImgObj2 = urlRoot + "MuralhaDigital/Veiculo/Imagem?acao=ImagemByIdImg" + (possuiImgObj2 ? "&id=" + idImgObj2 : "");
			
			innerHTML = innerHTML + "	<div class='row'> ";
			innerHTML = innerHTML + "		<div class='container-fluid g-1 g-md-1 bg-light bg-gradient border border-2'> ";
			innerHTML = innerHTML + "			<div class='row'> ";
			innerHTML = innerHTML + "				<div class='col-sm-2 d-flex align-items-center'> ";
			innerHTML = innerHTML + "			    	<div class='col-sm-12 text-center mt-1 mb-1'> ";
			innerHTML = innerHTML + "			    		<img class='img-fluid img-thumbnail' src='" + urlImgObj1 + "'></img> ";
			innerHTML = innerHTML + "		    		</div> ";
			innerHTML = innerHTML + "				</div> ";
			innerHTML = innerHTML + "				<div class='col-sm-2 d-flex align-items-center'> ";
			innerHTML = innerHTML + "			    	<div class='col-sm-12 text-center mt-1 mb-1'> ";
			innerHTML = innerHTML + "			    		<img class='img-fluid img-thumbnail' src='" + urlImgObj2 + "'></img> ";
			innerHTML = innerHTML + "		    		</div> ";
			innerHTML = innerHTML + "				</div> ";
			innerHTML = innerHTML + "				<div class='col-sm-8 text-start'> ";
			innerHTML = innerHTML + "				    <div class='row pt-1'> ";
			innerHTML = innerHTML + "				    	<div class='col-sm-9'> ";
			innerHTML = innerHTML + "				    		<label><small><strong>Placa Lida:</strong> "+placaLida+"</small></label> ";
			innerHTML = innerHTML + "			    		</div> ";
			innerHTML = innerHTML + "				    	<div class='col-sm-3 text-end'> ";
			innerHTML = innerHTML + "				    		<label><small><strong class='"+corTextoStatusAlerta+"'>"+statusAlerta+"</strong></small></label> ";
			innerHTML = innerHTML + "			    		</div> ";
			innerHTML = innerHTML + "			    	</div> ";
			innerHTML = innerHTML + "				    <div class='row'> ";
			innerHTML = innerHTML + "				    	<label><small><strong>Data:</strong> " + dataFormatada + "</small></label> ";
			innerHTML = innerHTML + "			    	</div> ";
			innerHTML = innerHTML + "				    <div class='row'> ";
			innerHTML = innerHTML + "				    	<label><small><strong>Local:</strong> " + equipamento + "</small></label> ";
			innerHTML = innerHTML + "			    	</div> ";
			innerHTML = innerHTML + "				    <div class='row align-items-start pt-1'> ";
			innerHTML = innerHTML + "				    	<div class='col-sm-12'> ";
			innerHTML = innerHTML + "					    	<div class='btn-group justify-content-start' role='group'> ";
			innerHTML = innerHTML + "								<button class='btn btn-sm btn-primary' type='button' title='Detalhar' onclick=DetalharAlertaPorId(\'" + id + "\')> ";
			innerHTML = innerHTML + "									<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='currentColor' class='bi bi-search' viewBox='0 0 16 16'> ";
			innerHTML = innerHTML + "										<path d='M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z'></path> ";
			innerHTML = innerHTML + "									</svg> ";
			innerHTML = innerHTML + "								</button> ";
			innerHTML = innerHTML + "							</div> ";
			innerHTML = innerHTML + "						</div> ";
			innerHTML = innerHTML + "			    	</div> ";
			innerHTML = innerHTML + "			    	<input id='idAlerta' type='hidden' value="+id+"> ";
			innerHTML = innerHTML + "			    	<input id='idTipoAlerta' type='hidden' value="+idTipoAlertaOcorrencia+"> ";
			innerHTML = innerHTML + "				</div> ";
			innerHTML = innerHTML + "			</div> ";
			innerHTML = innerHTML + "		</div> ";
			innerHTML = innerHTML + "	</div> ";
		});
	}
	
	listaAlertaItens.innerHTML = innerHTML;
}

function DetalharAlertaPorId(idAlerta)
{
	sessionStorage.setItem("PAGINACAO_ITENS_POR_PAGINA", PAGINACAO_ITENS_POR_PAGINA);
	sessionStorage.setItem("PAGINACAO_OFFSET", PAGINACAO_OFFSET);
	sessionStorage.setItem("PAGINACAO_PAGINA_ATUAL", PAGINACAO_PAGINA_ATUAL);
	
	var linkHTML = "/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=" + idAlerta + "&Voltar=1&VisualizarAlertas=1";
	window.location.assign(linkHTML);
}

function FecharJanelaVisualizarAlerta()
{
	window.close();
}