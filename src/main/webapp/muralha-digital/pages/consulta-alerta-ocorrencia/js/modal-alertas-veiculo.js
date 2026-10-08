var ID_VEICULO = null;

$(document).ready(function () 
{
	OcultarSpinnerModalAlertaVeic();
});

function FecharModal()
{
	$('#modalAlertaVeiculo').modal('hide');
}

function AbrirModalAlertaVeiculo(idVeiculo)
{
	ID_VEICULO = idVeiculo;
	
	if (ID_VEICULO == null || ID_VEICULO == "")
	{
		alert("Veículo não encontrado!");
		FecharModal();
		return;
	}
	else
	{
		try
		{
			var funcoes =
			[
				ExibirSpinnerModalAlertaVeic,
				OcultarListaAlertas,
				ObterDetalhesVeiculo,
				ObterAlertasVeiculo
			];
			
			var d = $.Deferred().resolve();
			while (funcoes.length > 0) {
			   d = d.then(funcoes.shift());
			}
		}
		catch(e)
		{
			console.log("Erro ao obter alertas do veículo." + e.respone);
			AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao obter alertas do veículo!!', "#error_container_modal_alerta_veiculo");
			OcultarSpinnerModalAlertaVeic();
		}
	}
}

function ExibirSpinnerModalAlertaVeic()
{
	var spinAtualizaAlertaVeiculo = document.getElementById('spinAtualizaAlertaVeiculo');
	if (spinAtualizaAlertaVeiculo)
		spinAtualizaAlertaVeiculo.style.visibility = 'visible';
}
function OcultarSpinnerModalAlertaVeic()
{
	var spinAtualizaAlertaVeiculo = document.getElementById('spinAtualizaAlertaVeiculo');
	if (spinAtualizaAlertaVeiculo)
		spinAtualizaAlertaVeiculo.style.visibility = 'hidden';
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

function ObterDetalhesVeiculo()
{
	var dataString = "acao=obterVeiculoPorId" +
						"&idVeiculo=" + ID_VEICULO;
	
	var urlPesquisa = "/MuralhaDigital/Veiculo";
	
	ExibirSpinnerModalAlertaVeic();
	
	$.ajax(
	{
	    type: 		"GET",
	    url: 		urlPesquisa,
		data: 		dataString,
	    dataType:	"xml",
		
        success: function( xml, textStatus, jqXHR ) 
        {
			TratarRetornoDetalhesVeiculo(jqXHR);
        },
		
	    error: function(e, b, error) {
			console.log("Erro ao obter detalhes do veículo." + e.respone);
			AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao obter detalhes do veículo!!', "#error_container_modal_alerta_veiculo");
			OcultarSpinnerModalAlertaVeic();
	    }
	});
}

function TratarRetornoDetalhesVeiculo(jqXHR)
{
	var xmlDoc = $.parseXML( jqXHR.responseText );
	var $xml = $(xmlDoc);
	
//	console.log('TratarRetornoVeiculo()');

	var veiculo = $xml.find('veiculo');

	var id = veiculo.find('idVeiculo').text();
	var placa = veiculo.find('placa').text();
	var dataFormatada = veiculo.find('dataVeicFormatada').text();
	var serieEquipamento = veiculo.find('serieEquipamento').text();
	var nomeEquipamento = veiculo.find('descLocal').text();
	var equipamento = serieEquipamento + ' - ' + nomeEquipamento;
	var faixa = veiculo.find('faixa').text();
	var velocidade = veiculo.find('velocidade').text();
	
	PopulaModalDetalhesVeiculo(id, equipamento, faixa, dataFormatada, placa, velocidade);
}

function PopulaModalDetalhesVeiculo(id, equipamento, faixa, dataFormatada, placa, velocidade)
{
	var localVeiculo = document.getElementById("localVeiculo");
	var faixaLocalVeiculo = document.getElementById("faixaLocalVeiculo");
	var dataVeiculo = document.getElementById("dataVeiculo");
	var placaVeiculo = document.getElementById("placaVeiculo");
	var velocidadeVeiculo = document.getElementById("velocidadeVeiculo");
	var imgVeiculoIrregular = document.getElementById("imgVeiculoIrregular");
	
	if (localVeiculo)
		localVeiculo.value = equipamento;
		
	if (faixaLocalVeiculo)
		faixaLocalVeiculo.value = faixa;
		
	if (dataVeiculo)
		dataVeiculo.value = dataFormatada;
		
	if (placaVeiculo)
		placaVeiculo.value = placa;
		
	if (velocidadeVeiculo)
		velocidadeVeiculo.value = velocidade;
		
	if (imgVeiculoIrregular)
	{
		var src = urlRoot + "MuralhaDigital/Veiculo/Imagem?" + "acao=ImagemByIdVeic&" + "idVeic=" + id + "&tpImagem=" + 0;
//		console.log(src);
		imgVeiculoIrregular.src = src;
	}
}

function ObterAlertasVeiculo()
{
	var dataString = "acao=consultaAlertasPorVeiculo" +
						"&idVeiculo=" + ID_VEICULO;

    var url = urlRoot + "MuralhaDigital/AlertaOcorrencia";
        
//    console.log("dataStringPesquisa: " + dataString + " urlPesquisa: " + url);

	ExibirSpinnerModalAlertaVeic();

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
			
			OcultarSpinnerModalAlertaVeic();
        },
        error: 		function(jqXHR, textStatus, errorThrown)
        {
			AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', "#error_container_modal_alerta_veiculo");
        	OcultarSpinnerModalAlertaVeic();
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
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
		
		$xml.find('AlertaOcorrencia').each(function()
		{
			var $item = $(this);
			
			ExibirListaAlertas();
			
			var id = $item.find('id').text();
			var idTipoAlertaOcorrencia = $item.find('idTipoAlertaOcorrencia').text();
			var tipoAlertaOcorrencia = $item.find('tipoAlertaOcorrencia').text();
			var nomeCadMonitorado = $item.find('nomeCadMonitorado').text();
			var statusAlerta = $item.find('status').text()
			var placa = $item.find('placa').text();
			var dataFormatada = $item.find('dataFormatada').text() + " " + $item.find('horaFormatada').text();
			
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
			
			innerHTML = innerHTML + "	<div class='row'> ";
			innerHTML = innerHTML + "		<div class='container-fluid g-1 g-md-1 bg-light bg-gradient border border-2'> ";
			innerHTML = innerHTML + "			<div class='row'> ";
			innerHTML = innerHTML + "				<div class='col-sm-12 text-start'> ";
			innerHTML = innerHTML + "				    <div class='row pe-1'> ";
			innerHTML = innerHTML + "			    		<div class='col-sm-12 text-center'> ";
			innerHTML = innerHTML + "				    		<h6><strong>" + tipoAlertaOcorrencia + "</strong></h6> ";
			innerHTML = innerHTML + "			    		</div> ";
			innerHTML = innerHTML + "			    	</div> ";
			innerHTML = innerHTML + "				    <div class='row'> ";
			innerHTML = innerHTML + "				    	<div class='col-sm-5'> ";
			innerHTML = innerHTML + "				    		<label><small><strong class='ps-1'>Data:</strong> " + dataFormatada + "</small></label> ";
			innerHTML = innerHTML + "				    	</div> ";
			innerHTML = innerHTML + "				    	<div class='col-sm-4'> ";
			innerHTML = innerHTML + "				    		<label><small><strong>Placa monitorada:</strong> " + placa + "</small></label> ";
			innerHTML = innerHTML + "				    	</div> ";
			innerHTML = innerHTML + "				    	<div class='col-sm-3 text-end'> ";
			innerHTML = innerHTML + "				    		<label><small> <strong class='"+corTextoStatusAlerta+" pe-1'>" + statusAlerta + "</strong></small></label> ";
			innerHTML = innerHTML + "				    	</div> ";
			innerHTML = innerHTML + "				    </div> ";
			innerHTML = innerHTML + "				    <div class='row'> ";
			innerHTML = innerHTML + "				    	<div class='col-sm-12'> ";
			innerHTML = innerHTML + "				    		<label><small><strong class='ps-1'>Nome:</strong> " + nomeCadMonitorado + "</small></label> ";
			innerHTML = innerHTML + "				    	</div> ";
			innerHTML = innerHTML + "				    </div> ";
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

function LimparModalAlertasVeiculo()
{
	var localVeiculo = document.getElementById("localVeiculo");
	var faixaLocalVeiculo = document.getElementById("faixaLocalVeiculo");
	var dataVeiculo = document.getElementById("dataVeiculo");
	var placaVeiculo = document.getElementById("placaVeiculo");
	var velocidadeVeiculo = document.getElementById("velocidadeVeiculo");
	var listaAlertaItens = document.getElementById("listaAlertaItens");
	
	if (localVeiculo)
		localVeiculo.value = "";
		
	if (faixaLocalVeiculo)
		faixaLocalVeiculo.value = "";
		
	if (dataVeiculo)
		dataVeiculo.value = "";
		
	if (placaVeiculo)
		placaVeiculo.value = "";
		
	if (velocidadeVeiculo)
		velocidadeVeiculo.value = "";
	
	if (listaAlertaItens)
		listaAlertaItens.innerHTML = "";
}

function ImprimirVeicIrregular()
{
	var dataString = "idVeiculo=" + ID_VEICULO;
    var url = urlRoot + "MuralhaDigital/BlitzEletronica/Imprimir";
    
    var parametros = {"idVeiculo" : ID_VEICULO};
    
    console.log("ID_VEICULO: " + ID_VEICULO);
    console.log("dataString: " + dataString);
    console.log("url: " + url);
    console.log("parametros: " + parametros);
        
    $.ajax({
        type: 		"GET",
        url: 		url,
        data: 		dataString,
        dataType:	"xml",
           
        //if received a response from the server
        success: function( data, textStatus, jqXHR) 
        {
        	var event = jqXHR.responseText;
        	var xmlDoc = $.parseXML( event );
			var $xml = $(xmlDoc);
    			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');

			//console.log("sucesso: " + sucesso);

			if (sucesso) {
				GerarArquivoDownload(url, parametros);
			} else {
				console.log(msgResposta);
				alert("Erro ao imprimir Ocorrências do veículo!");
			}
        },
        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	console.log("Erro ao processar requisição ao servidor!!");
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
    });
}