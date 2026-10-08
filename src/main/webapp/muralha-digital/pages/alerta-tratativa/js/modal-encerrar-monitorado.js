var DESC_ERROR_CONTAINER_MODAL = "#error_container_modal_encerrar_mon";

function AbrirModalEncerrarMonitoramento()
{
	var elmModalEncerrarMonitorado = document.getElementById("modalEncerrarMonitorado");
	
	if (elmModalEncerrarMonitorado)
	{
		var modalEncerrarMonitorado = new bootstrap.Modal(elmModalEncerrarMonitorado);
		modalEncerrarMonitorado.show();
		
		LimparCamposModalEncerrarMonitorado();
		ObterInfoCadMonitorado();
	}
}

function ObterInfoCadMonitorado()
{
	var dataString = "acao=obterPorId" +
						"&id=" + ID_CAD_MONITORADO;
	
	var urlPesquisa = "/MuralhaDigital/Monitorado";
	
	$.ajax(
	{
	    type: 		"GET",
	    url: 		urlPesquisa,
		data: 		dataString,
	    dataType:	"xml",
		
        success: function( xml, textStatus, jqXHR ) 
        {
			TratarRetornoInfoCadMonitorado(jqXHR);
        },
		
	    error: function(e, b, error) {
			console.log("Erro ao obter Cadastro de Monitoramento por Id." + e.respone);
			AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao obter Cadastro de Monitoramento!!', DESC_ERROR_CONTAINER_MODAL);
	    },
		async: false
	});
}

function TratarRetornoInfoCadMonitorado(jqXHR)
{
	var xmlDoc = $.parseXML( jqXHR.responseText );
	var $xml = $(xmlDoc);

	var monitorado = $xml.find('VeiculoMonitorado');

	var tipoAlertaOcorrencia = monitorado.find('tipoAlertaOcorrencia').text();
	var placa = monitorado.find('placa').text();
	var dataInicioFormatada = monitorado.find('dataInicioFormatada').text();
	var dataFimFormatada = monitorado.find('dataFimFormatada').text();
	var descricao = monitorado.find('descricao').text();

	PopulaModaEncerarCadMonitorado(tipoAlertaOcorrencia, placa, dataInicioFormatada, dataFimFormatada, descricao)
}

function PopulaModaEncerarCadMonitorado(tipoAlertaOcorrencia, placa, dataInicioFormatada, dataFimFormatada, descricao)
{
	var tipoAlertaEncerrarMon = document.getElementById("tipoAlertaEncerrarMon");
	var placaEncerrarMon = document.getElementById("placaEncerrarMon");
	var dataInicioEncerrarMon = document.getElementById("dataInicioEncerrarMon");
	var dataFimEncerrarMon = document.getElementById("dataFimEncerrarMon");
	var descricaoEncerrarMon = document.getElementById("descricaoEncerrarMon");
	
	if (tipoAlertaEncerrarMon)
		tipoAlertaEncerrarMon.value = tipoAlertaOcorrencia;
		
	if (placaEncerrarMon)
		placaEncerrarMon.value = placa;
		
	if (dataInicioEncerrarMon)
		dataInicioEncerrarMon.value = dataInicioFormatada;
		
	if (dataFimEncerrarMon)
		dataFimEncerrarMon.value = dataFimFormatada;
		
	if (descricaoEncerrarMon)
		descricaoEncerrarMon.value = descricao;

}

function LimparCamposModalEncerrarMonitorado()
{
	var tipoAlertaEncerrarMon = document.getElementById("tipoAlertaEncerrarMon");
	var placaEncerrarMon = document.getElementById("placaEncerrarMon");
	var dataInicioEncerrarMon = document.getElementById("dataInicioEncerrarMon");
	var dataFimEncerrarMon = document.getElementById("dataFimEncerrarMon");
	var descricaoEncerrarMon = document.getElementById("descricaoEncerrarMon");
	
	if (tipoAlertaEncerrarMon)
		tipoAlertaEncerrarMon.value = "";
		
	if (placaEncerrarMon)
		placaEncerrarMon.value = "";
		
	if (dataInicioEncerrarMon)
		dataInicioEncerrarMon.value = "";
		
	if (dataFimEncerrarMon)
		dataFimEncerrarMon.value = "";
		
	if (descricaoEncerrarMon)
		descricaoEncerrarMon.value = "";
	
}

function EncerrarMonitoramento()
{
  	var dataStringPesquisa = "acao=encerrar" +
  								"&id=" + ID_CAD_MONITORADO;
        
    var urlPesquisa = urlRoot + "MuralhaDigital/Monitorado";

//    console.log("dataStringPesquisa: " + dataStringPesquisa + " urlPesquisa: " + urlPesquisa);

    $.ajax({
        type: 		"POST",
        url: 		urlPesquisa,
        data: 		dataStringPesquisa,
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
				DesabilitarBotaoEncerrarMon();
				AlterarBotaoCancelar();
				SuccessModalCsx_E_TimeOut_8000ms_v2(msgResposta, DESC_ERROR_CONTAINER_MODAL);
			} else {
				WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, DESC_ERROR_CONTAINER_MODAL);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', DESC_ERROR_CONTAINER_MODAL);
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });
}

function DesabilitarBotaoEncerrarMon()
{
	var btnEncerrarMon = document.getElementById("btnEncerrarMon");
	
	if (btnEncerrarMon)
		btnEncerrarMon.disabled = true;
}

function AlterarBotaoCancelar()
{
	var btnCancelarEncerrarMon = document.getElementById("btnCancelarEncerrarMon");
	
	if (btnCancelarEncerrarMon)
		btnCancelarEncerrarMon.innerHTML = "Fechar";
}