function obterEquipamentosGenerico(nomeElemento)
{
    var urlPesquisa = urlRoot + "MuralhaDigital/Equipamento";
	var dataStringPesquisa = "acao=obterListaEquipamentos";
        
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
				carregaComboEquipamentosGenerico( jqXHR.responseText, nomeElemento );
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

function obterEquipamentosMunReg(nomeElemento)
{
	var municipio = document.getElementById("selectMunMapa").value;
	var regiao = document.getElementById("selectRegMapa").value;

    var urlPesquisa = urlRoot + "MuralhaDigital/Equipamento";

	var dataStringPesquisa = "acao=obterListaEquipamentosMunReg" +
								"&municipio=" + municipio +
								"&regiao=" + regiao;
								
	var obterEquipamentos = !(municipio == 0);
	
	if (obterEquipamentos)
	{
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
					carregaComboEquipamentosGenerico( jqXHR.responseText, nomeElemento );
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
}

function carregaComboEquipamentosGenerico(event, nomeElemento) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	$("#" + nomeElemento).selectpicker();
	$("#" + nomeElemento).empty();
	
	if (!$("#" + nomeElemento).prop("multiple"))
	{
		$("#" + nomeElemento).append($('<option>', {
		    value: 0,
		    text: "--Todos os Equipamentos--"
		}));
	}
	
	$xml.find('Equipamento').each(function()
	{
		
		var $item = $(this);
		
		var idLocal = $item.find('idLocal').text();
		var serieEquipamento = $item.find('serieEquipamento').text();
		var nome = $item.find('nome').text();
		var codigoEquipamento = ( ($item.find('codigoEquipamento').text() == null || $item.find('codigoEquipamento').text() == '') ? "N/D" : $item.find('codigoEquipamento').text() );
//		var textoCombo = (codigoEquipamento + " - " + serieEquipamento + " - " + nome);
		var textoCombo = (serieEquipamento + " - " + nome);
		
		$("#" + nomeElemento).append($('<option>', {
		    value: idLocal,
		    text: textoCombo
		}));
	});
	
	$("#" + nomeElemento).selectpicker("refresh");
}