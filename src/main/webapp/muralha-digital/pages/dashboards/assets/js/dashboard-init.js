$(document).ready(function() 
{
	CarregarComponenteData(true);
	
//	obterEquipamentos();
	obterMunicipios();
	obterRegioes();
	sleep(300).then(() =>
	{
		obterEquipamentos();
	});
	
	CarregarFiltroInicial();
	
	sleep(600).then(() =>
	{
		Analisar();
		AtualizarLocalizacaoMapas();
	});
});

function CarregarFiltroInicial()
{
//	console.log("CarregarFiltroInicial()");
	
	const dataFim = new Date();
	var dataInicio = new Date();
	dataInicio.setDate(dataFim.getDate() - 10);
	
//	console.log("Data inicio: " + dataInicio.toLocaleDateString("pt-BR"));
//	console.log("Data fim: " + dataFim.toLocaleDateString("pt-BR"));
	
	document.getElementById("dataInicio").value = dataInicio.toLocaleDateString("pt-BR");
	document.getElementById("dataFim").value = dataFim.toLocaleDateString("pt-BR");
}

function AddLoading()
{
	$("body").addClass("loading");
}
function RemoveLoading()
{
	$("body").removeClass("loading");
}

function Analisar()
{
	try
	{
		var funcoes =
		[
			AddLoading,
			ObterGraficoCompPassagensInfracoes,
			ObterGraficoCompMesAnterior,
			ObterGraficoCompAnoAnterior,
			ObterGraficoEvolucaoClassificacao,
			ObterGraficoDistribuicaoPorFaixa,
			ObterGraficoCompPrevisaoFuturo,
			ObterGraficoRankingPorFaixa,
			ObterTotalizadorCategoria,
			ObterCalendarioIntensidade,
			ObterQuantitativosMapaCalor,
			ObterDadosDispositivosEquipamentos3D,
			RemoveLoading
		];
		
		var d = $.Deferred().resolve();
		while (funcoes.length > 0) {
		   d = d.then(funcoes.shift());
		}
	}
	catch (ex)
	{
		AlertCsx_E_TimeOut_8000ms('Erro ao carregar dados dos gráficos!!');
		RemoveLoading();
	}
}

function obterEquipamentos()
{
	obterEquipamentosMunReg("selEquipamento");
}

function obterMunicipios()
{
    var urlPesquisa = urlRoot + "MuralhaDigital/Equipamento";

	var dataStringPesquisa = "acao=obterListaMunicipiosEquipamentos";
								
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
				carregaComboMunicipios( jqXHR.responseText );
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

function carregaComboMunicipios(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	$xml.find('Equipamento').each(function()
	{
		
		var $item = $(this);
		
		var idMunicipio = $item.find('idMunicipio').text();
		var municipio = $item.find('municipio').text();
		var ufMunicipio = $item.find('ufMunicipio').text();
		var textoCombo = (municipio + " - " + ufMunicipio);

		var selected = (idMunicipio == ID_MUNICIPIO_PADRAO)
		
//		console.log("idMunicipio: " + idMunicipio);
//		console.log("ID_MUNICIPIO_PADRAO: " + ID_MUNICIPIO_PADRAO);
//		console.log("selected: " + selected);

		$('#selectMunMapa').append($('<option>', {
		    value: idMunicipio,
		    text: textoCombo,
		    selected: selected
		}));
	});
}

function obterRegioes()
{
    var urlPesquisa = urlRoot + "MuralhaDigital/Equipamento";

	var dataStringPesquisa = "acao=obterListaRegioesEquipamentos";
								
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
				carregaComboRegioes( jqXHR.responseText );
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

function carregaComboRegioes(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	$xml.find('Equipamento').each(function()
	{
		
		var $item = $(this);
		
		var idRegiao = $item.find('idRegiao').text();
		var regiao = $item.find('regiao').text();

		$('#selectRegMapa').append($('<option>', {
		    value: idRegiao,
		    text: regiao
		}));
	});
}


function ObterTotalizadorCategoria()
{
	var municipio = document.getElementById("selectMunMapa").value;
	var regiao = document.getElementById("selectRegMapa").value;
  	var equipamento = document.getElementById("selEquipamento").value;
  	var tipoRelatorio = document.getElementById("selTipoRelatorio").value;
  	var dataIni = $("#dataInicio").val();
  	var dataFim = $("#dataFim").val();
	
	var dataString = "acao=obterTotalizadorCategoria" + 
						"&dataIni=" + dataIni + 
						"&dataFim=" + dataFim +
						"&equipamento=" + equipamento +
						"&municipio=" + municipio +
						"&regiao=" + regiao +
						"&tipoRelatorio=" + tipoRelatorio
        
    var url = urlRoot + "MuralhaDigital/Dashboard";
    
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
				AtualizarInfoTotalizador( jqXHR.responseText );
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

function AtualizarInfoTotalizador(event)
{	
	var categoria = null;
	var total = null;
	
	var infoTotalizador = document.getElementById("totalizador");
	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);

	categoria = $xml.find('categoria').text();
	total = $xml.find('total').text();
	
	if (infoTotalizador)
		infoTotalizador.innerHTML = "Totalizador: " + new Intl.NumberFormat('pt-BR').format(total) + " " + categoria;
}