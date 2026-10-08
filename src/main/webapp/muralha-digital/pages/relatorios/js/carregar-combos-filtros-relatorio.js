function carregarCombos()
{
	obterTiposRegistro();
	obterTiposAlertasOcorrencias();
	obterStatusAlertaOcorrencia();
	obterEquipamentos();
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
		
		var id = $item.find('id').text();
		var tipo = $item.find('tipo').text();

		$('#selTipoAlertaOcorrencia').append($('<option>', {
		    value: id,
		    text: tipo
		}));
				
	});
}

function obterStatusAlertaOcorrencia()
{

	var tipoRegistro = document.getElementById("selTipo").value;
	
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
				carregaComboStatusAlertaOcorrencia( jqXHR.responseText );
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

function carregaComboStatusAlertaOcorrencia(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);

	$('#selStatus').empty();
	
	$('#selStatus').append($('<option>', {
	    value: 0,
	    text: "--Selecione o Status--"
	}));
	
	$xml.find('StatusAlertaOcorrencia').each(function()
	{
		
		var $item = $(this);
		
		var id = $item.find('id').text();
		var tipo = $item.find('descricao').text();

		$('#selStatus').append($('<option>', {
		    value: id,
		    text: tipo
		}));
				
	});
}


function obterEquipamentos()
{
	obterEquipamentosGenerico("selEquipamento");

}
