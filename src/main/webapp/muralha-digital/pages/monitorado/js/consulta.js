//$(document).on({
//    ajaxStart: function(){
//        $("body").addClass("loading"); 
//    },
//    ajaxStop: function(){ 
//        $("body").removeClass("loading"); 
//    }    
//});
let placasComAlerta = []

$(document).ready(async function () {
	consultarPlacasComAlerta();
	CarregarComponenteData(true);
	CarregarPametrosPaginacao();

	// 🔹 Preencher o campo placa se tiver na URL
	const urlParams = new URLSearchParams(window.location.search);
	const placaParam = urlParams.get("placa");
	if (placaParam) {
		$("#placaConsulta").val(placaParam.trim().toUpperCase());

		// 🔹 Remover o parâmetro "placa" da URL sem recarregar
		urlParams.delete("placa");
		const newUrl = window.location.pathname + (urlParams.toString() ? "?" + urlParams.toString() : "");
		window.history.replaceState({}, document.title, newUrl);
	}

	const permitido = await verificarPermissaoGrupoUsuarioConsulta();
	if (permitido) {
		$("#divChkSupervisionado").show();
	}
	
	$('.placa').on('keypress', function (e) {
		var input = $(this);
		var value = input.val();
		var key = e.originalEvent.key;
		
		value += key;
		
		try {
			var pattern = /^(([A-Z]{0,3}))([0-9]{1}[A-Z0-9]{1})?([0-9]{0,2})$/i;
			var ok = pattern.test(value);
			if (!ok) e.preventDefault();
		} catch (e) {
			// HandleErrorMessages(e);
		}
	});

	try {
		$("body").addClass("loading");
		carregarCombos();
  		executaPesquisa(false);
	} catch (e) {
		console.log("Erro ao carregar a página!!");
		WarningCsx_E_TimeOut_8000ms("Erro ao carregar a página!!");
		$("body").removeClass("loading");
	}
});

function AtualizarItensPaginacao()
{
	executaPesquisa(false);
}

function carregarCombos()
{
	obterTiposAlertasOcorrencias();
}

function consultarPlacasComAlerta() {
  fetch('/MuralhaDigital/Monitorado?acao=ObterPlacasComAlerta', {
    method: 'GET',
    headers: {
      'Accept': 'text/xml'
    }
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Erro na requisição');
    }
    return response.text();
  })
  .then(xmlText => {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlText, 'application/xml');
    placasComAlerta = Array.from(xmlDoc.getElementsByTagName('placa')).map(el => el.textContent);
  })
  .catch(error => {
    console.error('Erro ao consultar placas repetidas:', error);
  });
}

function obterTiposAlertasOcorrencias()
{

    var urlPesquisa = "/MuralhaDigital/AlertaOcorrencia/Tipo";
        
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
				CarregaComboTiposAlertasOcorrenciasModalCad( jqXHR.responseText );
				carregaComboTiposAlertasOcorrenciasModalEdit( jqXHR.responseText );
//				console.log("executaPesquisa()::Chegou dados");
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
//        	alert("(executaPesquisa) Erro ao processar requisição ao servidor!!");
			$("body").removeClass("loading");
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

function executaPesquisa(reiniciarPaginaAtual)
{
	var tipoAlertaOcorrencia 		= document.getElementById("selTipoAlertaOcorrencia"			).value;
	var placa 						= document.getElementById("placaConsulta"					).value;
	var buscarApenasCadAtivo 		= document.getElementById("chkCadAtivo"						).checked;
	var buscarApenasPlacaComCoringa = document.getElementById("chkApenasPlacaCaracterCoringa"	).checked;
	
	var supervisionadoCheckbox = document.getElementById("chkSupervisionado");
	var supervisionado = supervisionadoCheckbox ? supervisionadoCheckbox.checked : false;
	
	var privadoCheckbox = document.getElementById("chkPrivado");
	var privado = privadoCheckbox ? privadoCheckbox.checked : false;

  	var dataIni = $("#dataInicio"	).find("input").val();
  	var dataFim = $("#dataFim"		).find("input").val();

	limparTabelaResultado();
	
	if (reiniciarPaginaAtual)
		ReiniciaPaginaAtualComponentePaginacao();
	
	var dataStringPesquisa = "acao=obterLista" +
								"&tipoAlertaOcorrencia="		+ tipoAlertaOcorrencia +
    							"&placa=" 						+ placa +
								"&dataIni=" 					+ dataIni +
    							"&dataFim=" 					+ dataFim +
    							"&buscarApenasCadAtivo=" 		+ buscarApenasCadAtivo + 
    							"&buscarApenasPlacaComCoringa="	+ buscarApenasPlacaComCoringa +
								"&supervisionado="				+ supervisionado + 
								"&privado="						+ privado + 
    							"&paginacaoItensPorPagina="		+ PAGINACAO_ITENS_POR_PAGINA +
    							"&paginacaoOffset="				+ PAGINACAO_OFFSET;
    							
        
    var urlPesquisa = "/MuralhaDigital/Monitorado";
        
//    console.log("dataStringPesquisa: " + dataStringPesquisa + " urlPesquisa: " + urlPesquisa);

	$("body").addClass("loading");
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
				processaDados( jqXHR.responseText );
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
			$("body").removeClass("loading");
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
        	$("body").removeClass("loading");
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus)
        {
			AtualizarParametrosComponentePaginacao( jqXHR.responseText );
			CriarComponentePaginacao();
		}
 
    });      
}
		
function limparTabelaResultado()
{
//	console.log("limparTabelaResultado()::Executando...");
	
	var tableHeaderRowCount = 1;
	var table = document.getElementById("tabela");
	var rowCount = table.rows.length;
	
	for (var i = tableHeaderRowCount; i < rowCount; i++) 
	    table.deleteRow(tableHeaderRowCount);
		
}
		
function limparFiltros()
{
	document.getElementById("selTipoAlertaOcorrencia"	).value = "0";
	document.getElementById("placaConsulta"				).value = "";
	
  	$dataIni = $("#dataInicio").find("input");
    $dataIni.val('');
    $dataIni.removeData();
    
    $dataFim = $("#dataFim").find("input");
    $dataFim.val('');
    $dataFim.removeData();
	
	// Limpar os checkboxes
	document.getElementById("chkSupervisionado").checked = false;
	document.getElementById("chkPrivado").checked = false;
}


function GerarRelatorio(formato)
{
	var tabela = 'tabela'; // id da tabela
	var listaIgnoreColumns = [7]; // index das colunas que devem ser ignoradas
	var filename = 'Relatorio' + '.' + formato;
	
	if (formato == 'pdf')
		GerarRelatorioPDF(tabela, filename, listaIgnoreColumns);
	else if (formato == 'xls')
		GerarRelatorioXLS(tabela, filename, listaIgnoreColumns);
	else if (formato == 'xlsx')
		GerarRelatorioXLSX(tabela, filename, listaIgnoreColumns);
}

	
function processaDados(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	$xml.find('VeiculoMonitorado').each(function()
	{
		var $item = $(this);
		
		var nome					= $item.find('nome'					).text();
		var id 						= $item.find('id'					).text();
		var tipoAlertaOcorrencia 	= $item.find('tipoAlertaOcorrencia'	).text();
		var placa 					= $item.find('placa'				).text();
		var dataInicioFormatada 	= $item.find('dataInicioFormatada'	).text() + " " + $item.find('horaInicioFormatada'	).text();
		var dataFimFormatada 		= $item.find('dataFimFormatada'		).text() + " " + $item.find('horaFimFormatada'		).text();
		var nomeUsuarioCadastro 	= $item.find('nomeUsuario'			).text();
		var dataCadastroFormatada	= $item.find('dataCadastroFormatada').text() + " " + $item.find('horaCadastroFormatada'	).text();
		var editavel 				= $item.find('editavel'				).text() === 'true';
		var possuiAlerta			= $item.find('possuiAlerta'			).text() === 'true';
		
		var params = "\'"+id+"\'";
		
		var onclickDetalhar = 'onclick="AbrirEditarMonitorado(' + params + ',' + false + ',' + possuiAlerta + ')"';
		var onclickEditar = editavel ? ('onclick="AbrirEditarMonitorado(' + params + ',' + true + ',' + possuiAlerta + ')"') : '';
		var onclickVisualizarAlertas = 'onclick="AbrirListaAlertasCadMonitorado(' + params + ')"';
		
		var disabled = !editavel ? 'disabled' : '';
		var disabledListaAlerta = !possuiAlerta ? 'disabled' : '';
		
		var htmlBotoesAcao = "<div class='btn-group d-flex justify-content-center align-items-center' role='group'>" + 
									"<button id='btnListaAlertas_" + id +  "' " + "type='button' class='btn btn-sm btn-warning' title='Ver alertas' " + disabledListaAlerta + " " +
									onclickVisualizarAlertas +
									">" +
						                "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='currentColor' class='bi bi-exclamation-triangle' viewBox='0 0 16 16'>" +
						  					"<path d='M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.146.146 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.163.163 0 0 1-.054.06.116.116 0 0 1-.066.017H1.146a.115.115 0 0 1-.066-.017.163.163 0 0 1-.054-.06.176.176 0 0 1 .002-.183L7.884 2.073a.147.147 0 0 1 .054-.057zm1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566z'/>" +
						  					"<path d='M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995z'/>" +
										"</svg>" +
									"</button>" +
									"<button id='btnDetalhar_" + id +  "' " + "type='button' class='btn btn-sm btn-primary'  title='Detalhar'" +
									onclickDetalhar +
									">" +
						                "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='currentColor' class='bi bi-search' viewBox='0 0 16 16'>" +
						  					"<path d='M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z'></path>" +
										"</svg>" +
									"</button>" + 
									"<button id='btnEditar_" + id +  "' " + "type='button' class='btn btn-sm btn-secondary' title='Editar' " + disabled + " " +
									onclickEditar +
									">" +
						                "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='currentColor' class='bi bi-pen-fill' viewBox='0 0 16 16'>" +
						  					"<path d='m13.498.795.149-.149a1.207 1.207 0 1 1 1.707 1.708l-.149.148a1.5 1.5 0 0 1-.059 2.059L4.854 14.854a.5.5 0 0 1-.233.131l-4 1a.5.5 0 0 1-.606-.606l1-4a.5.5 0 0 1 .131-.232l9.642-9.642a.5.5 0 0 0-.642.056L6.854 4.854a.5.5 0 1 1-.708-.708L9.44.854A1.5 1.5 0 0 1 11.5.796a1.5 1.5 0 0 1 1.998-.001z'/>" +
										"</svg>" +
									"</button>" +
								"</div>";
								
		var tableRef = document.getElementById("tabela").getElementsByTagName('tbody')[0];

		var row = tableRef.insertRow(tableRef.rows.length);
		
		let alertaRegistro = placasComAlerta.includes(placa)
		    ? "<a href=\"/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?placa="+placa+"\" target=\"_blank\"><span class=\"badge\" title=\"placa em mais de um registro de fato\">!</span></a>"
		    : "";
		
		row.insertCell(0).innerHTML = "<small>" + tipoAlertaOcorrencia 	+ "</small>";
		row.insertCell(1).innerHTML = "<small>" + nome 					+ "</small>";
		row.insertCell(2).innerHTML = "<small>" + placa 				+ " " + alertaRegistro + "</small>";
		row.insertCell(3).innerHTML = "<small>" + dataInicioFormatada 	+ "</small>";
		row.insertCell(4).innerHTML = "<small>" + dataFimFormatada 		+ "</small>";
		row.insertCell(5).innerHTML = "<small>" + nomeUsuarioCadastro 	+ "</small>";
		row.insertCell(6).innerHTML = "<small>" + dataCadastroFormatada	+ "</small>";
		row.insertCell(7).innerHTML = htmlBotoesAcao;
		
		$('input#txt_consulta').quicksearch('table#tabela tbody tr');
				
	});
}
		
function AbrirListaAlertasCadMonitorado(idCadMonitorado)
{
	var url = 	'/muralha-digital/pages/consulta-alerta-ocorrencia/visualizar-alertas.jsp?idCadMonitorado=' + idCadMonitorado;
	CentralizaWindow(url, 'Visualizar Alertas', window, (screen.width/1.3), (screen.height/1.2));
}

function CentralizaWindow(url, windowName, win, w, h) 
{
    const y = win.top.outerHeight / 2 + win.top.screenY - ( h / 2);
    const x = win.top.outerWidth  / 2 + win.top.screenX - ( w / 2);
    return win.open(url, windowName, `toolbar=yes, location=yes, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, copyhistory=no, width=${w}, height=${h}, top=${y}, left=${x}`);
}

async function verificarPermissaoGrupoUsuarioConsulta() {
	try {
		const xml = await $.ajax({
			type: "GET",
			url: "/MuralhaDigital/PermissoesFuncionalidade",
			data: {
				acao: "verificarPermissaoGrupo"
			},
			dataType: "xml"
		});

		const permitido = $(xml).find("permitido").text().toLowerCase() === "true";
		return permitido;

	} catch (error) {
		console.error("Erro ao verificar permissão de grupo!", error);
		return false; // segurança: nega se erro
	}
}
