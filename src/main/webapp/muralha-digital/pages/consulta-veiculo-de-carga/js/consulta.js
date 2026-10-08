// var IS_MODO_GRADE_IMAGENS 	= false;
// var QUANTIDADE_REGISTROS_GRADE = 0;
// var RETORNAR_IMAGENS_MODO_GRADE = false;
var REQUEST_DB = null;
var REQUEST_CANCELADO = false;
var FILTRO_REGISTRO_FATO_ATIVO = false;
var IMAGEM_ORIGINAL = false;
let temRegistro = false;

$().ready(function() 
{
	// document.getElementById("grade").className = "btn btn-secondary";
	// document.getElementById("grade").innerText = "GRADE IMAGENS INATIVO";

	const $btnToggle = $("#btnToggleFiltros");
	const $filtrosAdicionais = $("#filtrosAdicionais");

	let visivel = false;

	$btnToggle.on("click", function () {
		visivel = !visivel;
		$filtrosAdicionais.css("display", visivel ? "block" : "none");
		$btnToggle.text(visivel ? "- FILTROS" : "+ FILTROS");
	});

	const urlParams = new URLSearchParams(window.location.search);
    const placa = urlParams.get('placa');
    const quantidadeItensPorPagina = urlParams.get('quantidadeItensPorPagina');

	 if (placa) {
        document.getElementById("placaConsulta").value = placa.toUpperCase();
    }

	$('#marcaVeiculo').selectpicker();
    $('#modeloVeiculo').selectpicker();
    $('#tiposVeiculo').selectpicker();

	HabilitarTooltips();
	CarregarComponenteData(false);
	CarregarPametrosPaginacao();
	ObterListaCores();
	ObterListaLocalidades();
	ObterListaUfs();
	ObterListaTiposVeiculo();
	ObterListaMarcas();
	ObterListaModelos();
	
	// GerenciarBotaoModoGrade();

    if (quantidadeItensPorPagina && !isNaN(quantidadeItensPorPagina)) {
        PAGINACAO_ITENS_POR_PAGINA = parseInt(quantidadeItensPorPagina);
    } else {
        PAGINACAO_ITENS_POR_PAGINA = 8;
    }

	try
	{
		obterEquipamentosGenerico("selEquipamento");
		ObterClassificacoesVeiculos();
		if (placa) {
			executaPesquisa(true);
		}
		
		sleep(300).then(() =>
		{
			AplicarEstiloBotoesBootstrapSelect();

			setTimeout(function() {
				$("body").removeClass("loading");
			}, 800);
		});
	}
	catch (ex)
	{
		AlertCsx_E_TimeOut_8000ms('Erro ao carregar página de consulta de veículos de carga!!');
		$("body").removeClass("loading");
	}
});

function AtualizarItensPaginacao()
{
	executaPesquisa(false);
}

function HabilitarTooltips()
{
	var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
	var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
	  return new bootstrap.Tooltip(tooltipTriggerEl)
	});
}

function ObterClassificacoesVeiculos()
{
    var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo/Classificacao";
	var dataStringPesquisa = "acao=obterListaClassificacoes";
    
    $.ajax({
        type: 		"GET",
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
				CarregaComboClassificacoes( jqXHR.responseText );
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
        }
    });      
}

function CarregaComboClassificacoes(event) 
{
    var xmlDoc = $.parseXML( event );
    var $xml = $(xmlDoc);
    
    $('#selClassificacao').empty();
    
    $xml.find('ClasseVeiculo').each(function()
    {
        var $item = $(this);
        
        var idClasse = $item.find('idClasse').text();
        var classe = $item.find('classe').text();
        
        $('#selClassificacao').append($('<option>', {
            value: idClasse,
            text: classe.trim()
        }));
    });
    
    $('#selClassificacao').selectpicker('refresh');
}

function executaPesquisa(reiniciarPaginaAtual) {
    // limpar_imagens_grade();
    var placa = document.getElementById("placaConsulta").value;
    var equipamento = $('#selEquipamento').val();
    var pista = $('#selPista').val();
    var classificacao = $('#selClassificacao').val();
    var somenteUltimaPassagem = document.getElementById("chkUltimaPassagem").checked;
    var deveAplicarFiltroDeImagens = document.getElementById("chkComImagem").checked;
    
    var dataIni = TratarDataHora($("#dataInicio").find("input").val());
    var dataFim = TratarDataHora($("#dataFim").find("input").val());

    var corVeiculo = $('#corVeiculo').val();
    var anoFabricacao = $('#anoFabricacao').val();
    var anoModelo = $('#anoModelo').val();
    var renavam = $('#renavam').val();
    var chassi = $('#chassi').val();
    var tipoVeiculo = $('#tiposVeiculo').val();
    var restricao = $('#restricao').val();
    var tipoPlaca = $('#selTipoPlaca').val();
    var filtroPlaca = $('#selFiltroPlaca').val();
    var localidade = $('#selLocalidade').val();
    var marca = $('#marcaVeiculo').val();
    var modelo = $('#modeloVeiculo').val();

    RegistrarPesquisaTela(placa, dataIni, dataFim);
    limparTabelaResultado();
    
    if (reiniciarPaginaAtual)
        ReiniciaPaginaAtualComponentePaginacao();
        
    var dataStringPesquisa = "placa=" + placa + 
                                "&dataIni=" + dataIni + 
                                "&dataFim=" + dataFim +
                                "&equipamento=" + equipamento +
                                "&pista=" + pista +
                                "&classificacao=" + classificacao +
                                "&deveAplicarFiltroDeImagens=" + deveAplicarFiltroDeImagens +
                                // "&modoGrade=" + IS_MODO_GRADE_IMAGENS +
                                // "&retornarImagens=" + RETORNAR_IMAGENS_MODO_GRADE +
                                "&paginacaoItensPorPagina=" + PAGINACAO_ITENS_POR_PAGINA +
                                "&paginacaoOffset=" + PAGINACAO_OFFSET +
                                "&corVeiculo=" + corVeiculo +
                                "&anoFabricacao=" + encodeURIComponent(anoFabricacao) +
                                "&anoModelo=" + encodeURIComponent(anoModelo) +
                                "&renavam=" + encodeURIComponent(renavam) +
                                "&chassi=" + encodeURIComponent(chassi) +
                                "&tipoVeiculo=" + encodeURIComponent(tipoVeiculo) +
                                "&marca=" + encodeURIComponent(marca) +
                                "&modelo=" + encodeURIComponent(modelo) +
                                "&restricao=" + encodeURIComponent(restricao) +
                                "&tipoPlaca=" + tipoPlaca +
                                "&filtroPlaca=" + filtroPlaca +
                                "&idLocalidade=" + localidade +
                                "&somenteUltimaPassagem=" + somenteUltimaPassagem +
                                "&acao=consultaPorFiltrosTela";

    var urlPesquisa = urlRoot + "MuralhaDigital/VeiculoDeCarga";

    $("body").addClass("loading");
    REQUEST_DB = $.ajax({
        type: "GET",
        url: urlPesquisa,
        data: dataStringPesquisa,
        dataType: "xml",
           
        success: function( data, textStatus, jqXHR) 
        {
            var event = jqXHR.responseText;
            var xmlDoc = $.parseXML( event );
            var $xml = $(xmlDoc);
            var sucesso = $xml.find('sucesso').text();
            var msgResposta = $xml.find('msgResposta').text();
            sucesso = (sucesso === 'true' || sucesso === '');
            var listaVeiculos = $xml.find('ListaVeiculos > VeiculoDeCarga');

            if (sucesso) {
                processaDados( jqXHR.responseText );
            } else {
                WarningCsx_E_TimeOut_8000ms(msgResposta);
            }
        },

        error: function(jqXHR, textStatus, errorThrown)
        {
            if (REQUEST_CANCELADO)
                WarningCsx_E_TimeOut_8000ms('Pesquisa cancelada!');
            else
                AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: function(jqXHR, textStatus)
        {
            try
            {
                AtualizarParametrosComponentePaginacao( jqXHR.responseText );
                CriarComponentePaginacao();
                REQUEST_CANCELADO = false;
                REQUEST_DB = null;
            }
            catch (e) 
            {
                console.log("Erro ao carregar dados!!");
                console.log(e);
            }
            finally
            {
                $("body").removeClass("loading");
            }
        }
    });
}

function ObterListaLocalidades() {
    var urlPesquisa = urlRoot + "MuralhaDigital/VeiculoDeCarga";
    var dataStringPesquisa = "acao=obterLocalidades";

    $.ajax({
        type: "GET",
        url: urlPesquisa,
        data: dataStringPesquisa,
        dataType: "json",

        success: function (data) {
            CarregaComboLocalidades(data);
        },
        error: function () {
            AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!');
        },
    });
}

function CarregaComboLocalidades(listaLocalidades) {
    var $combo = $("#selLocalidade");
    $combo.empty();
    $combo.append('<option value="">Todas</option>');

    $.each(listaLocalidades, function (i, localidade) {
        var id = localidade.id_localidade;
        var nome = localidade.nome;
        var uf = localidade.uf;
        var texto = nome + ' - ' + uf;
        $combo.append('<option value="' + id + '">' + texto + '</option>');
    });
    
    $combo.selectpicker('refresh');
}

function abrirModal(formato){
	// Coletar todos os IDs dos registros da tabela
	var todosIds = coletarTodosIdsTabela();
	
	document.getElementById('formatoArquivo').value = formato;
	document.getElementById('motivoText').value = '';
	document.getElementById('veiculosSelecionados').value = todosIds || '';
	
	const modalElement = document.getElementById('motivoModal');
	const modal = bootstrap.Modal.getInstance(modalElement);
	
	if(modal){
		modal.show()
	}else{
		const myModal = new bootstrap.Modal(document.getElementById('motivoModal'), {
	        backdrop: 'static',
	        keyboard: false
    	});   
    	myModal.show();
	}	
}

function fecharModal(){
	const modalElement = document.getElementById('motivoModal');
	const modal = bootstrap.Modal.getInstance(modalElement);
	
	if(modal){
		modal.hide();
	}
}

function registrarSolicitacaoRelatorio(){
    const motivo = document.getElementById('motivoText').value;
    const formato = document.getElementById('formatoArquivo').value;

    const deveSalvarMotivo = false;
    const usarRelatorioSPU = true;

    if(motivo === null || motivo === ''){
        alert("É obrigatório informar o motivo da geração do relatório.")
        return;
    }

    if ($('#tabela tbody tr').length === 0) {
        WarningCsx_E_TimeOut_8000ms("Realize uma pesquisa antes de exportar!");
        fecharModal();
        return;
    }

    if (deveSalvarMotivo) {
        const tipoSolicitacao = formato.toUpperCase() === 'PDF' ? '2' : '3';
        const motivoCompleto = (formato.toUpperCase() === 'PDF' ? 'PDF: ' : 'Excel: ') + motivo;
        const placa = document.getElementById("placaConsulta").value;
        
        var url = `/MuralhaDigital/MotivoSolicitacaoRelatorio?acao=registrarMotivoSolicitacaoRelatorio&motivo=${encodeURIComponent(motivoCompleto)}&placa=${encodeURIComponent(placa)}&tipoSolicitacao=${tipoSolicitacao}`;
        
        $.ajax({
            type: "POST",
            url: url,
            dataType: "xml",         
            success: function( data, textStatus, jqXHR) 
            {
                var event = jqXHR.responseText;
                var xmlDoc = $.parseXML( event );
                var $xml = $(xmlDoc);
                    
                var sucesso = $xml.find('result').text();
                
                sucesso = (sucesso === 'true' || sucesso === '');
                if (sucesso) {
                    if (usarRelatorioSPU) {
                        gerarRelatorioSPU(formato, motivo);
                    } else {
                        gerarRelatorioManual(formato, motivo);
                    }         
                } else {
                    fecharModal();
                    WarningCsx_E_TimeOut_8000ms("Ocorreu um erro!");
                }
            },
            error: function(jqXHR, textStatus, errorThrown)
            {
                AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
            },
            beforeSend: function(jqXHR, settings){},
            complete: function(jqXHR, textStatus)
            {
                fecharModal();
                $("body").removeClass("loading");
            }
        });
    } else {
        if (usarRelatorioSPU) {
            gerarRelatorioSPU(formato, motivo);
        } else {
            gerarRelatorioManual(formato, motivo);
        }
        fecharModal();
    }
}

function gerarRelatorioSPU(formato, motivo) {
    var placa = document.getElementById("placaConsulta").value;
    var equipamento = $('#selEquipamento').val();
    var pista = $('#selPista').val();
    var classificacao = $('#selClassificacao').val();
    var somenteUltimaPassagem = document.getElementById("chkUltimaPassagem").checked;
    
    var dataIni = TratarDataHora($("#dataInicio").find("input").val());
    var dataFim = TratarDataHora($("#dataFim").find("input").val());

    var corVeiculo = $('#corVeiculo').val();
    var anoFabricacao = $('#anoFabricacao').val();
    var anoModelo = $('#anoModelo').val();
    var renavam = $('#renavam').val();
    var chassi = $('#chassi').val();
    var tipoVeiculo = $('#tiposVeiculo').val();
    var restricao = $('#restricao').val();
    var tipoPlaca = $('#selTipoPlaca').val();
    var filtroPlaca = $('#selFiltroPlaca').val();
    var localidade = $('#selLocalidade').val();
    var marca = $('#marcaVeiculo').val();
    var modelo = $('#modeloVeiculo').val();

    var url = urlRoot + "MuralhaDigital/VeiculoDeCarga?acao=exportarConsultaSPU" +
              "&formato=" + formato + 
              "&motivo=" + encodeURIComponent(motivo) +
              "&placa=" + encodeURIComponent(placa) +
              "&dataIni=" + encodeURIComponent(dataIni) +
              "&dataFim=" + encodeURIComponent(dataFim) +
              "&equipamento=" + encodeURIComponent(equipamento) +
              "&pista=" + encodeURIComponent(pista) +
              "&classificacao=" + encodeURIComponent(classificacao) +
              "&corVeiculo=" + encodeURIComponent(corVeiculo) +
              "&anoFabricacao=" + encodeURIComponent(anoFabricacao) +
              "&anoModelo=" + encodeURIComponent(anoModelo) +
              "&renavam=" + encodeURIComponent(renavam) +
              "&chassi=" + encodeURIComponent(chassi) +
              "&tipoVeiculo=" + encodeURIComponent(tipoVeiculo) +
              "&marca=" + encodeURIComponent(marca) +
              "&modelo=" + encodeURIComponent(modelo) +
              "&restricao=" + encodeURIComponent(restricao) +
              "&tipoPlaca=" + encodeURIComponent(tipoPlaca) +
              "&filtroPlaca=" + filtroPlaca +
              "&idLocalidade=" + localidade +
              "&somenteUltimaPassagem=" + somenteUltimaPassagem;
    
    $("body").addClass("loading");
    
    window.open(url, '_blank');
    
    setTimeout(function() {
        $("body").removeClass("loading");
    }, 1000);
}


function processaDados(event) 
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var idsVeiculos = []; // Array para armazenar todos os IDs em ordem
	var indexAtual = 0;

	// Primeiro, coletar todos os IDs
	$xml.find('VeiculoDeCarga').each(function()
	{
		var $item = $(this);
		var idVeiculo = $item.find('id').text();
		idsVeiculos.push(idVeiculo);
	});

	// Agora processar cada veículo
	$xml.find('VeiculoDeCarga').each(function(index)
	{
		var $item = $(this);
		
		var id = $item.find('id').text();
		var idVeiculo = $item.find('idVeiculo').text();
        var idVeiculoParaImagens = (idVeiculo && idVeiculo !== "0") ? idVeiculo : null;

		var placa = $item.find('placa').text();
		var data = $item.find('data').text();
		var nomeEquipamento = $item.find('nome').text();
		var serieEquipamento = $item.find('serieEquipamento').text();
		var equipamento = serieEquipamento + ' - ' + nomeEquipamento;
		var classificacao = $item.find('classificacao').text();
		var faixa = $item.find('faixa').text();
		
		// Campos específicos de veículos de carga
		var pbt = $item.find('pbt').text();
		var pbtc = $item.find('pbtc').text();
		var numeroEixos = $item.find('numeroEixos').text();
		var e1 = $item.find('e1').text();
		var e2 = $item.find('e2').text();
		var e3 = $item.find('e3').text();
		var e4 = $item.find('e4').text();
		var e5 = $item.find('e5').text();
		var distanciaE1E2 = $item.find('distanciaE1E2').text();
		var distanciaE2E3 = $item.find('distanciaE2E3').text();
		var classificacaoArt96 = $item.find('classificacaoArt96').text();
		
		// Formatar data
		var dataFormatada = formatarData(data);
		
		// Formatar pesos por eixo
		var pesosEixos = "";
		if (e1) pesosEixos += "E1: " + e1 + "kg ";
		if (e2) pesosEixos += "E2: " + e2 + "kg ";
		if (e3) pesosEixos += "E3: " + e3 + "kg ";
		if (e4) pesosEixos += "E4: " + e4 + "kg ";
		if (e5) pesosEixos += "E5: " + e5 + "kg ";
		
		// Formatar distâncias entre eixos
		var distanciasEixos = "";
		if (distanciaE1E2) distanciasEixos += "E1-E2: " + distanciaE1E2 + "m ";
		if (distanciaE2E3) distanciasEixos += "E2-E3: " + distanciaE2E3 + "m ";
		
		// Calcular IDs anterior e próximo
		var idVeiculoAnterior = (index > 0) ? idsVeiculos[index - 1] : "";
		var idVeiculoProximo = (index < idsVeiculos.length - 1) ? idsVeiculos[index + 1] : "";
		
		// Botão de detalhes com navegação
		var htmlBotoesAcao = "<div class='d-flex justify-content-center align-items-center gap-1' role='group'>" + 
                    "<button id='btnDetalhar_" + id +  "' type='button' class='btn btn-sm btn-primary d-flex align-items-center justify-content-center' data-bs-toggle='modal' data-bs-target='#modalDetalheVeiculo' title='Abrir detalhes do veículo' " +
                    "onclick='AbrirDetalhesVeiculo(\"" + id + "\", " + idVeiculoParaImagens + ")' " +
                    "data-id-anterior='" + idVeiculoAnterior + "' " +
                    "data-id-proximo='" + idVeiculoProximo + "' " +
                    "style='width: 32px; height: 32px;'>" +
                    "<svg xmlns='http://www.w3.org/2000/svg' width='14' height='14' fill='currentColor' class='bi bi-search' viewBox='0 0 16 16'>" +
                    "<path d='M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z'></path>" +
                    "</svg>" +
                    "</button>" + 
                    "</div>";

		// if ( ! IS_MODO_GRADE_IMAGENS )
		if ( true )
		{
			var tableRef = document.getElementById("tabela").getElementsByTagName('tbody')[0];
	
			var row   = tableRef.insertRow(tableRef.rows.length);
			row.insertCell(0).innerHTML = "<small>" + equipamento + "</small>";
			row.insertCell(1).innerHTML = "<small>" + classificacao + "</small>";
			row.insertCell(2).innerHTML = "<small>" + placa + "</small>";
			row.insertCell(3).innerHTML = "<small>" + dataFormatada + "</small>";
			row.insertCell(4).innerHTML = "<small>" + (pbt ? pbt + " kg" : "-") + "</small>";
			row.insertCell(5).innerHTML = "<small>" + (pbtc ? pbtc + " kg" : "-") + "</small>";
			row.insertCell(6).innerHTML = "<small>" + (numeroEixos ? numeroEixos : "-") + "</small>";
			row.insertCell(7).innerHTML = htmlBotoesAcao;
			
			$('input#txt_consulta').quicksearch('table#tabela tbody tr');
		}
	});
}

function formatarData(dataString) {
    if (!dataString) return '-';
    try {
        var data = new Date(dataString);
        return data.toLocaleString('pt-BR');
    } catch (e) {
        return dataString;
    }
}

function limparTabelaResultado()
{
	var tableHeaderRowCount = 1;
	var table = document.getElementById("tabela");
	var rowCount = table.rows.length;
	
	for (var i = tableHeaderRowCount; i < rowCount; i++) 
	    table.deleteRow(tableHeaderRowCount);
}
		
function limparFiltros()
{
	document.getElementById("placaConsulta").value = "";
	$('#selEquipamento').selectpicker('deselectAll');
	$('#selPista').selectpicker('deselectAll');
	$('#selClassificacao').selectpicker('deselectAll');
	$('#selTipoPlaca').val('');
	$('#tiposVeiculo').selectpicker('deselectAll');
	$('#marcaVeiculo').selectpicker('val', '');
    $('#modeloVeiculo').selectpicker('val', '');

	document.getElementById("selFiltroPlaca").value = "0";
	document.getElementById("chkUltimaPassagem").checked = false;
	ObterClassificacoesVeiculos();
	
  	$dataIni = $("#dataInicio").find("input");
    $dataIni.val('');
    $dataIni.removeData();
    
    $dataFim = $("#dataFim").find("input");
    $dataFim.val('');
    $dataFim.removeData();
}

// function modo_grade()
// {
// 	if (IS_MODO_GRADE_IMAGENS){
// 		IS_MODO_GRADE_IMAGENS = false;
// 		PAGINACAO_ITENS_POR_PAGINA = 8;
// 	}
// 	else{	
// 		IS_MODO_GRADE_IMAGENS = true;
// 	}
	
// 	fecharModalParametros();
// 	GerenciarBotaoModoGrade();
// 	limpar_imagens_grade();
// 	ReiniciarComponentePaginacao();
// }

// function GerenciarBotaoModoGrade()
// {
// 	if (IS_MODO_GRADE_IMAGENS)
// 	{
// 		document.getElementById("lista_resultado").style.display = "none";
// 		document.getElementById("canvas_temp").style.display = "none";
// 		document.getElementById("grade").className = "btn btn-info";
// 		document.getElementById("grade").innerText = "VER EM GRADE";		
// 		document.getElementById("grade_itens").style.display = "block"; 	
// 	}
// 	else
// 	{
// 		document.getElementById("lista_resultado").style.display = "block";
// 		document.getElementById("canvas_temp").style.display = "block";
// 		document.getElementById("grade").className = "btn btn-secondary";
// 		document.getElementById("grade").innerText = "VER EM GRADE";
// 		document.getElementById("grade_itens").style.display = "none";				
// 	}	
// }

// function limpar_imagens_grade()
// {
// 	if (IS_MODO_GRADE_IMAGENS)
// 	{
// 		var freewall = document.getElementById("freewall");
// 		if (freewall)
// 		{
// 			freewall.innerHTML = "";
// 		}
// 	}
// }

function AplicarEstiloBotoesBootstrapSelect()
{
	Array.from(document.getElementsByClassName("bs-select-all")).forEach(
    	function(element, index, array) {
        	element.classList.remove('btn-light');
        	element.classList.add('btn-outline-success');
        	element.title = "Marcar todos";
    	}
	);
	
	Array.from(document.getElementsByClassName("bs-deselect-all")).forEach(
    	function(element, index, array) {
        	element.classList.remove('btn-light');
        	element.classList.add('btn-outline-danger');
        	element.title = "Desmarcar todos";
    	}
	);
}

function sleep (time) {
  return new Promise((resolve) => setTimeout(resolve, time));
}

function CancelarRequisicao()
{
	try
	{
		if (REQUEST_DB != null)
		{
			REQUEST_DB.abort();
		}
	}
	catch (ex)
	{
		AlertCsx_E_TimeOut_8000ms('Erro ao cancelar requisição!!');
		$("body").removeClass("loading");
	}
}

function ObterListaCores() {
	var urlPesquisa = urlRoot + "MuralhaDigital/VeiculoDeCarga";
	var dataStringPesquisa = "acao=obterListaCores";

	$.ajax({
		type: "GET",
		url: urlPesquisa,
		data: dataStringPesquisa,
		dataType: "json", 

		success: function (data) {
			CarregaComboCores(data); 
		},

		error: function () {
			AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!');
		},

	});
}

function ObterListaUfs() {
	var urlPesquisa = urlRoot + "MuralhaDigital/VeiculoDeCarga";
	var dataStringPesquisa = "acao=obterListaUf";

	$.ajax({
		type: "GET",
		url: urlPesquisa,
		data: dataStringPesquisa,
		dataType: "json", 

		success: function (data) {
			CarregaComboUfs(data); 
		},

		error: function () {
			AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!');
		},

	});
}

function ObterListaTiposVeiculo() {
    var urlPesquisa = urlRoot + "MuralhaDigital/VeiculoDeCarga";
    var dataStringPesquisa = "acao=obterListaTiposVeiculo";

    $.ajax({
        type: "GET",
        url: urlPesquisa,
        data: dataStringPesquisa,
        dataType: "json",

        success: function (data) {
            CarregaComboTiposVeiculo(data);
        },
        error: function () {
            AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!');
        },
    });
}

function CarregaComboCores(listaCores) {
	var $combo = $("#corVeiculo");
	$combo.empty();
	$combo.append('<option value="">Todas</option>');

	$.each(listaCores, function (i, cor) {
		var id = cor.id_cor;
		var descricao = cor.descricao;
		$combo.append('<option value="' + id + '">' + descricao + '</option>');
	});
}

function CarregaComboUfs(listaUf) {
	var $combo = $("#ufs");
	$combo.empty();
	$combo.append('<option value="">Todas</option>');

	$.each(listaUf, function (i, uf) {
		var uf = uf.uf;
		$combo.append('<option value="' + uf + '">' + uf + '</option>');
	});
}

function CarregaComboTiposVeiculo(listaTipos) {
    var $combo = $("#tiposVeiculo");
    $combo.empty();

    $.each(listaTipos, function (i, tipo) {
        var id_tipo = tipo.id_tipo;
        var descricao = tipo.descricao;
        $combo.append('<option value="' + id_tipo + '">' + descricao + '</option>');
    });

    $combo.selectpicker('refresh');
    $combo.selectpicker('render');
}

function abrirValidacaoImagem() {
	window.open('/muralha-digital/pages/assinatura/validacao-imagem.jsp', '_blank');
}

function ObterListaMarcas() {
    var urlPesquisa = urlRoot + "MuralhaDigital/VeiculoDeCarga";
    var dataStringPesquisa = "acao=obterListaMarcas";

    $.ajax({
        type: "GET",
        url: urlPesquisa,
        data: dataStringPesquisa,
        dataType: "json",

        success: function (data) {
            CarregaComboMarcas(data);
        },
        error: function () {
            AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!');
        },
    });
}

function ObterListaModelos() {
    var urlPesquisa = urlRoot + "MuralhaDigital/VeiculoDeCarga";
    var dataStringPesquisa = "acao=obterListaModelos";

    $.ajax({
        type: "GET",
        url: urlPesquisa,
        data: dataStringPesquisa,
        dataType: "json",

        success: function (data) {
            CarregaComboModelos(data);
        },
        error: function () {
            AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!');
        },
    });
}

function CarregaComboMarcas(listaMarcas) {
    var $combo = $("#marcaVeiculo");
    $combo.empty();
    $combo.append('<option value="">Todas</option>');

    $.each(listaMarcas, function (i, marca) {
        var id = marca.id_marca_cet;
        var descricao = marca.descricao;
        $combo.append('<option value="' + id + '">' + descricao + '</option>');
    });

    $combo.selectpicker('refresh');
}

function CarregaComboModelos(listaModelos) {
    var $combo = $("#modeloVeiculo");
    $combo.empty();
    $combo.append('<option value="">Todos</option>');

    $.each(listaModelos, function (i, modelo) {
        var id = modelo.id_marca;
        var descricao = modelo.descricao;
        $combo.append('<option value="' + id + '">' + descricao + '</option>');
    });

    $combo.selectpicker('refresh');
}

function RegistrarPesquisaTela(placa, dataIni, dataFim) {
    var motivo = "Pesquisa em tela - Veículos de Carga - ";
    
    if (dataIni && dataFim) {
        motivo += "Período: " + dataIni + " a " + dataFim;
    } else if (dataIni) {
        motivo += "Data início: " + dataIni;
    } else if (dataFim) {
        motivo += "Data fim: " + dataFim;
    } else {
        motivo += "Sem filtro de data";
    }
    
    if (placa) {
        motivo += " | Placa: " + placa;
    }

    $.ajax({
        type: "POST",
        url: "/MuralhaDigital/MotivoSolicitacaoRelatorio",
        data: {
            acao: "registrarMotivoSolicitacaoRelatorio",
            motivo: motivo,
            placa: placa,
			tipoSolicitacao: "1"
        },
        dataType: "xml",
        success: function(data, textStatus, jqXHR) {
            // Sucesso silencioso
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error('Erro ao registrar pesquisa:', errorThrown);
        }
    });
}

function coletarTodosIdsTabela() {
    var ids = [];
    
    $('#tabela tbody tr').each(function() {
        var id = $(this).attr('data-id');
        if (id) {
            ids.push(id);
        }
    });
    
    console.log("IDs coletados da tabela:", ids.length);
    
    if (ids.length === 0) {
        return null;
    }
    
    return ids.join(',');
}

function gerarRelatorioManual(formato, motivo) {
    var dadosRelatorio = coletarDadosTabela();
    
    var dadosUrl = encodeURIComponent(JSON.stringify(dadosRelatorio));
    
    var url = urlRoot + "MuralhaDigital/VeiculoDeCarga?acao=exportarConsultaSelecionadosManual" +
              "&formato=" + formato + 
              "&motivo=" + encodeURIComponent(motivo) +
              "&dados=" + dadosUrl;
    
    $("body").addClass("loading");
    
    window.open(url, '_blank');
    
    setTimeout(function() {
        $("body").removeClass("loading");
    }, 1000);
}

function coletarDadosTabela() {
    var dados = [];
    
    $('#tabela tbody tr').each(function() {
        var $linha = $(this);
        var $celulas = $linha.find('td');
        
        var dado = {
            placa: $celulas.eq(2).find('small').text().trim(),
            data: $celulas.eq(3).find('small').text().trim(),
            pbt: $celulas.eq(4).find('small').text().trim(),
            pbtc: $celulas.eq(5).find('small').text().trim(),
            numeroEixos: $celulas.eq(6).find('small').text().trim(),
            pesosEixos: $celulas.eq(7).find('small').text().trim(),
            distanciasEixos: $celulas.eq(8).find('small').text().trim(),
            equipamento: $celulas.eq(0).find('small').text().trim(),
            classificacao: $celulas.eq(1).find('small').text().trim()
        };
        
        dados.push(dado);
    });
    
    return dados;
}