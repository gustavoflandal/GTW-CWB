$().ready(function () 
{
	CarregarComponenteData(true);
	// carregarCombos();
});

function GerarRelatorio(formato)
{
	var dataIni = TratarDataHora($("#dataInicio").find("input").val());
  	var dataFim = TratarDataHora($("#dataFim").find("input").val());

	var urlPesquisa = urlRoot + "Relatorio/AlertasDetalhado";
	
	var dataStringPesquisa = "dataIni=" + dataIni + 
    							"&dataFim=" + dataFim +
								"&formato=" + formato;

    var parametros = {"dataIni" : dataIni, "dataFim" : dataFim, "formato" : formato};

    //console.log("dataStringPesquisa: " + dataStringPesquisa + " urlPesquisa: " + urlPesquisa);
        
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

			//console.log("sucesso: " + sucesso);

			if (sucesso) {
				GerarArquivoDownload(urlPesquisa, parametros);
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
