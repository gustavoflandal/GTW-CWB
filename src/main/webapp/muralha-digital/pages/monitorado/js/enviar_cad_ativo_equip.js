var ERROR_CONTAINER_MODAL_ENVIAR_CAD_ATIVO_EQUIP = "#error_container_modal_enviar_cad_ativo_equip";

$(document).ready(function () 
{
	OcultarSpinnerModalEnviarCadAtivo();
});

function EnviarCadAtivoEquip()
{
	var urlRequisicao = "/MuralhaDigital/Monitorado";
	var dataParam = "acao=exportarCadastrosAtivos";
	
	ExibirSpinnerModalEnviarCadAtivo();

    $.ajax({
        type: 		"POST",
        url: 		urlRequisicao,
        data:		dataParam,
        dataType:	"xml",
           
        success: 	function( data, textStatus, jqXHR) 
        {
        	var event = jqXHR.responseText;
        	var xmlDoc = $.parseXML( event );
			var $xml = $(xmlDoc);
    			
			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();
			
			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso)
				SuccessModalCsx_E_TimeOut_8000ms_v2(msgResposta, ERROR_CONTAINER_MODAL_ENVIAR_CAD_ATIVO_EQUIP);
			else
				WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, ERROR_CONTAINER_MODAL_ENVIAR_CAD_ATIVO_EQUIP);
				
			OcultarSpinnerModalEnviarCadAtivo();
        },

        error: function(jqXHR, textStatus, errorThrown)
        {
        	AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', ERROR_CONTAINER_MODAL_ENVIAR_CAD_ATIVO_EQUIP);
        	OcultarSpinnerModalEnviarCadAtivo();
        },
        beforeSend: function(jqXHR, settings){},
        complete: function(jqXHR, textStatus){}
    });
}

function FecharModalEnviarCadAtivoEquip()
{
	$("#modalEnviarCadAtivoEquip").modal('hide');
}

function ExibirSpinnerModalEnviarCadAtivo()
{
	var spinLoadingEnviarCadAtivo = document.getElementById('spinLoadingEnviarCadAtivo');
	if (spinLoadingEnviarCadAtivo)
		spinLoadingEnviarCadAtivo.style.visibility = 'visible';
}
function OcultarSpinnerModalEnviarCadAtivo()
{
	var spinLoadingEnviarCadAtivo = document.getElementById('spinLoadingEnviarCadAtivo');
	if (spinLoadingEnviarCadAtivo)
		spinLoadingEnviarCadAtivo.style.visibility = 'hidden';
}