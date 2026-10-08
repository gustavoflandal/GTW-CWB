$().ready(function () 
{
	CarregaComponenteDataHoraSemMinuto(false);
});

function GerarRelatorio()
{
	var dataIni = TratarDataHora($("#dataInicio").find("input").val());
  	var dataFim = TratarDataHora($("#dataFim").find("input").val());

	var urlPesquisa = urlRoot + "relatorio/RelatorioAcompanhamento";
	
	var dataStringPesquisa = "dataIni=" + dataIni +
								"&dataFim=" + dataFim;

    var parametros = {"dataIni" : dataIni, "dataFim" : dataFim};

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
