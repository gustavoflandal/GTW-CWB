$(document).ready(function(){
	obterMotivosDescarteAlerta();
});

function obterMotivosDescarteAlerta()
{
	var dataString = "acao=obterListaMotivosDescarte";
    var urlPesquisa = urlRoot + "MuralhaDigital/Alerta/MotivoDescarte";
        
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
				carregaComboMotivosDescarte( jqXHR.responseText );
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta, descErrorContainerModal);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', descErrorContainerModal);
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){}
 
    });      
}

function carregaComboMotivosDescarte(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);

	$xml.find('MotivoDescarte').each(function()
	{
		
		var $item = $(this);
		
		var id = $item.find('id').text();
		var tipo = $item.find('descricao').text();

		$('#selMotivoDescarteCad').append($('<option>', {
		    value: id,
		    text: tipo
		}));
				
	});
}

function DescartarAlerta()
{
	var idMotivoDescarte = document.getElementById("selMotivoDescarteCad").value;
	var obsDescarteAlerta = document.getElementById("obsDescarteAlertaCad").value;
	var motivoDescarte = $("#selMotivoDescarteCad option:selected").text();
	var errorContainerModal = document.getElementById("error_container_modal_descarte");
	var descErrorContainerModal = "#error_container_modal_descarte";
	
	var dataString = "acao=descartarAlerta" +
						"&idAlerta=" + ID_ALERTA +
						"&idMotivoDescarte=" + idMotivoDescarte +
						"&obsDescarteAlerta=" + obsDescarteAlerta;
        
    var url = urlRoot + "MuralhaDigital/Alerta";

	if (confirm("Deseja realmente descartar o alerta?"))
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
					
					DESCARTADO = true;
					AtualizarVariaveis(idMotivoDescarte, motivoDescarte, obsDescarteAlerta);
					AtualizarControles();
					if (errorContainerModal)
						SuccessModalCsx_E_TimeOut_8000ms_v2(msgResposta, descErrorContainerModal);

					var textoAnotacao = GerarTextoAnotacaoDescarte(motivoDescarte, obsDescarteAlerta);
					CadastrarAnotacaoComTexto(textoAnotacao);
					fecharModalAlertaDescartar();
								
				} else {
					WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, descErrorContainerModal);
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

function GerarTextoAnotacaoDescarte(motivoDescarte, obsDescarteAlerta) {
	let texto = `<strong>Alerta Descartado:</strong> `;
	texto += `Motivo: <em>${motivoDescarte}</em>`;

	if (obsDescarteAlerta && obsDescarteAlerta.trim() !== "") {
		texto += ` | Obs: ${obsDescarteAlerta.trim()}`;
	}

	// Se ultrapassar o limite, cortar apenas o conteúdo de texto
	if (texto.length > 290) {
		const limite = 290 - 3; // espaço para "..."
		// Remove tags para cortar só o conteúdo textual
		const textoSemTags = texto.replace(/<[^>]+>/g, '');
		let cortado = textoSemTags.substring(0, limite).trim();
		// Reaplica <strong> na frente e fecha com "..."
		return `<strong>${cortado}...</strong>`;
	}

	return texto;
}

function DesabilitarControlesModalDescarte()
{
	if(DESCARTADO)
	{
		document.getElementById("btnDescartarAlerta").disabled 				= true;
		document.getElementById("btnLimparCamposDescartarAlerta").disabled	= true;
		document.getElementById("selMotivoDescarteCad").disabled			= true;
		document.getElementById("obsDescarteAlertaCad").disabled			= true;
	}
}

function PopulaModalMotivoDescarte()
{
	if (!(ID_MOTIVO_DESCARTE === ""))
	{
		document.getElementById("selMotivoDescarteCad").value = ID_MOTIVO_DESCARTE;
	}
	
	document.getElementById("obsDescarteAlertaCad").value = OBS_DESCARTE;
	
	DesabilitarControlesModalDescarte();
}

function LimparCamposDescarte()
{
	var idMotivoDescarte = document.getElementById("selMotivoDescarteCad");
	var obsDescarteAlerta = document.getElementById("obsDescarteAlertaCad");
	
	if (idMotivoDescarte)
		idMotivoDescarte.value = 0;
		
	if (obsDescarteAlerta)
		obsDescarteAlerta.value = "";
	
}

function fecharModalAlertaDescartar()
{
	$('#modalAlertaDescartar').modal('hide');
}