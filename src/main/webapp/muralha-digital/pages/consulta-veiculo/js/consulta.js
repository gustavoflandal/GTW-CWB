var IS_MODO_GRADE_IMAGENS 	= false;
var QUANTIDADE_REGISTROS_GRADE = 0;
var RETORNAR_IMAGENS_MODO_GRADE = false; //true: retorna imagens base64 para montar no javascript | false: retornar ID das imagens, para buscar a imagem já montada no backend
var REQUEST_DB = null;
var REQUEST_CANCELADO = false;
var FILTRO_REGISTRO_FATO_ATIVO = false;
var IMAGEM_ORIGINAL = false;
let temRegistro = false;

$().ready(function() 
{
	document.getElementById("grade").className = "btn btn-secondary";
	document.getElementById("grade").innerText = "GRADE IMAGENS INATIVO";

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
    const idVeiculoTempoReal = urlParams.get('idVeiculoTempoReal');

	 if (placa) {
        document.getElementById("placaConsulta").value = placa.toUpperCase();
    }

    if (idVeiculoTempoReal) {
        setTimeout(function() {
            AbrirDetalhesVeiculo(idVeiculoTempoReal);
        }, 500);
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
	ObterNaturezasDelituosas();
	ObterListaMarcas();
	ObterListaModelos();
	InicializarFiltrosRegistroFato();
	
	GerenciarBotaoModoGrade();

    if (quantidadeItensPorPagina && !isNaN(quantidadeItensPorPagina)) {
        PAGINACAO_ITENS_POR_PAGINA = parseInt(quantidadeItensPorPagina);
    } else {
        PAGINACAO_ITENS_POR_PAGINA = 8; // Valor padrão original
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
		AlertCsx_E_TimeOut_8000ms('Erro ao carregar página de consulta de veículos!!');
		$("body").removeClass("loading");
	}
});

function InicializarFiltrosRegistroFato() {
    const chkRegistroFato = document.getElementById('chkRegistroFato');
    const divFiltrosRegistroFato = document.getElementById('divFiltrosRegistroFato');
    const selCaracteristica = document.getElementById('selCaracteristicaRegistro');
    const selTipoRegistro = document.getElementById('selTipoRegistro');
    const selNatureza = document.getElementById('selNaturezaRegistro');
    
    chkRegistroFato.addEventListener('change', function() {
        FILTRO_REGISTRO_FATO_ATIVO = this.checked;
        divFiltrosRegistroFato.style.display = this.checked ? 'block' : 'none';
        
        if (!this.checked) {
            selCaracteristica.value = '';
            selTipoRegistro.value = '';
            selNatureza.innerHTML = '<option value="">Todos</option>';
            selNatureza.disabled = true;
        } else {
            CarregarTodosTiposRegistro();
            CarregarTodasNaturezas();
        }
    });
    
    selCaracteristica.addEventListener('change', function() {
    });
    
    selTipoRegistro.addEventListener('change', function() {
        const tipoId = this.value;
        selNatureza.innerHTML = '<option value="">Carregando...</option>';
        
        if (tipoId) {
            $.ajax({
                url: '/MuralhaDigital/RegistroDeFato/Natureza',
                data: { idTipo: tipoId },
                type: 'GET',
                dataType: 'xml',
                success: function(xml) {
                    selNatureza.innerHTML = '';
                    
                    $(xml).find('RegistroDeFatoNatureza').each(function() {
                        const id = $(this).find('id').text();
                        const descricao = $(this).find('naturezaDesc').text();
                        
                        const option = document.createElement('option');
                        option.value = id;
                        option.textContent = descricao;
                        selNatureza.appendChild(option);
                    });
                    
                    selNatureza.disabled = false;
                },
                error: function() {
                    selNatureza.innerHTML = '<option value="">Erro ao carregar</option>';
                }
            });
        } else {
            CarregarTodasNaturezas();
        }
    });
    
    function CarregarTodosTiposRegistro() {
        $.ajax({
            url: '/MuralhaDigital/RegistroDeFato/Tipo',
            type: 'GET',
            dataType: 'xml',
            success: function(xml) {
                selTipoRegistro.innerHTML = '<option value="">Todos</option>';
                
                $(xml).find('RegistroDeFatoTipo').each(function() {
                    const id = $(this).find('id').text();
                    const descricao = $(this).find('descricao').text();
                    
                    const option = document.createElement('option');
                    option.value = id;
                    option.textContent = descricao;
                    selTipoRegistro.appendChild(option);
                });
                
                selTipoRegistro.disabled = false;
            },
            error: function() {
                selTipoRegistro.innerHTML = '<option value="">Erro ao carregar</option>';
            }
        });
    }
    
    function CarregarTodasNaturezas() {
        $.ajax({
            url: '/MuralhaDigital/RegistroDeFato/Natureza',
            type: 'GET',
            dataType: 'xml',
            success: function(xml) {
                selNatureza.innerHTML = '<option value="">Todos</option>';
                
                $(xml).find('RegistroDeFatoNatureza').each(function() {
                    const id = $(this).find('id').text();
                    const descricao = $(this).find('naturezaDesc').text();
                    
                    const option = document.createElement('option');
                    option.value = id;
                    option.textContent = descricao;
                    selNatureza.appendChild(option);
                });
                
                selNatureza.disabled = false;
            },
            error: function() {
                selNatureza.innerHTML = '<option value="">Erro ao carregar</option>';
            }
        });
    }
    
    CarregarTodosTiposRegistro();
    CarregarTodasNaturezas();
}

function ObterNaturezasDelituosas() {
    return $.ajax({
        type: "GET",
        url: "/MuralhaDigital/RegistroDeFatoNaturezaDelituosa",
        data: { acao: "obterLista" },
        dataType: "xml",
        success: function(data) {
            const $xml = $(data);
            const select = $('#selNaturezaDelituosa');
            select.empty();

            $xml.find('RegistroDeFatoNaturezaDelituosa').each(function() {
                const id = $(this).find('id').text();
                const descricao = $(this).find('natureza_delituosa_desc').text();

                const option = $('<option>', {
                    value: id,
                    text: descricao
                });

                select.append(option);
            });

            select.selectpicker('refresh');
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error('Erro ao carregar naturezas delituosas: ', errorThrown);
        }
    });
}

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
	
//	console.log(tooltipList);
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
				CarregaComboClassificacoes( jqXHR.responseText );
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
        	AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus){},
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
		    text: classe
		}));
	});
	
	$('#selClassificacao').selectpicker('refresh');
}


function executaPesquisa(reiniciarPaginaAtual) {
    limpar_imagens_grade();
    var placa = document.getElementById("placaConsulta").value;
    var equipamento = $('#selEquipamento').val();
    var pista = $('#selPista').val();
    var classificacao = $('#selClassificacao').val();
    var naturezasDelituosas = $('#selNaturezaDelituosa').val();
    var deveAplicarFiltroDeImagens = document.getElementById("chkComImagem").checked;
	var somenteUltimaPassagem = document.getElementById("chkUltimaPassagem").checked;
    
    var dataIni = TratarDataHora($("#dataInicio").find("input").val());
    var dataFim = TratarDataHora($("#dataFim").find("input").val());

    var corVeiculo = $('#corVeiculo').val();
    var anoFabricacao = $('#anoFabricacao').val();
    var anoModelo = $('#anoModelo').val();
    var renavam = $('#renavam').val();
    var chassi = $('#chassi').val();
    var tipoVeiculo = $('#tiposVeiculo').val();
    var municipio = $('#municipio').val();
    var estado = $('#ufs').val();
    var restricao = $('#restricao').val();
	var tipoPlaca = $('#selTipoPlaca').val();
    var filtroPlaca = $('#selFiltroPlaca').val();
	var localidade = $('#selLocalidade').val();
	var marca = $('#marcaVeiculo').val();
    var modelo = $('#modeloVeiculo').val();

    // Novos parâmetros para registro de fato
    var filtrarPorRegistroFato = FILTRO_REGISTRO_FATO_ATIVO;
    var caracteristicaRegistro = $('#selCaracteristicaRegistro').val();
    var tipoRegistro = $('#selTipoRegistro').val();
    var naturezaRegistro = $('#selNaturezaRegistro').val();

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
                                "&modoGrade=" + IS_MODO_GRADE_IMAGENS +
                                "&retornarImagens=" + RETORNAR_IMAGENS_MODO_GRADE +
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
                                "&naturezasDelituosas=" + naturezasDelituosas +
                                "&filtrarPorRegistroFato=" + filtrarPorRegistroFato +
                                "&caracteristicaRegistro=" + encodeURIComponent(caracteristicaRegistro) +
                                "&tipoRegistro=" + encodeURIComponent(tipoRegistro) +
                                "&naturezaRegistro=" + encodeURIComponent(naturezaRegistro) +
								"&tipoPlaca=" + tipoPlaca +
                            	"&filtroPlaca=" + filtroPlaca +
                            	"&idLocalidade=" + localidade +
								"&somenteUltimaPassagem=" + somenteUltimaPassagem +
                                "&acao=consultaPorFiltrosTela";

    var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo";

    verificarPlaca(placa);

	$("body").addClass("loading");
    REQUEST_DB = $.ajax({
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
		    var listaVeiculos = $xml.find('ListaVeiculos > Veiculo');
		    var nenhumRegistro = listaVeiculos.length === 0;

			if(temRegistro && nenhumRegistro){
				Swal.fire({
				title: "Existem registros de fato cadastrados para esta placa"
			});
			}


			if (sucesso) {
				processaDados( jqXHR.responseText );
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
        },

        error: 		function(jqXHR, textStatus, errorThrown)
        {
//			console.log("error:: requisição cancelada:  " + REQUEST_CANCELADO);
			if (REQUEST_CANCELADO)
				WarningCsx_E_TimeOut_8000ms('Pesquisa cancelada!');
			else
        		AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: 	function(jqXHR, textStatus)
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
    
//    REQUEST_CANCELADO = true;
//    CancelarRequisicao();
}

function ObterListaLocalidades() {
    var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo";
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

function GerarRelatorioTabela(formato)
{
	var tabela = 'tabela'; // id da tabela
	var listaIgnoreColumns = [5]; // index das colunas que devem ser ignoradas
	var filename = 'Relatorio' + '.' + formato;
	
	if (formato == 'pdf')
		GerarRelatorioPDF(tabela, filename, listaIgnoreColumns);
	else if (formato == 'xls')
		GerarRelatorioXLS(tabela, filename, listaIgnoreColumns);
	else if (formato == 'xlsx')
		GerarRelatorioXLSX(tabela, filename, listaIgnoreColumns);
}

function abrirModal(formato){
	var veiculosSelecionados = coletarVeiculosSelecionados();

	document.getElementById('formatoArquivo').value = formato;
	document.getElementById('motivoText').value = '';
	document.getElementById('veiculosSelecionados').value = veiculosSelecionados || '';
	
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
    const placa = document.getElementById("placaConsulta").value;
    const veiculosSelecionados = document.getElementById('veiculosSelecionados').value;

    if(motivo === null || motivo === ''){
        alert("É obrigatório informar o motivo da geração do relatório.")
        return;
    }

    const tipoSolicitacao = formato.toUpperCase() === 'PDF' ? '2' : '3';
    const motivoCompleto = (formato.toUpperCase() === 'PDF' ? 'PDF: ' : 'Excel: ') + motivo;
    
    // Se há veículos selecionados, muda a ação para exportar apenas os selecionados
    var acao = veiculosSelecionados ? "exportarConsultaSelecionados" : "exportarConsulta";
    
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
                GerarRelatorio(formato, veiculosSelecionados, acao);			
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
}

function GerarRelatorio(formato, veiculosSelecionados, acao)
{	
    const motivo = document.getElementById('motivoText').value;

	// Se há veículos selecionados, usar os dados da tabela HTML
    if (veiculosSelecionados) {
        gerarRelatorioManual(formato, veiculosSelecionados, motivo);
        return;
    }

    var placa = document.getElementById("placaConsulta").value;
    var equipamento = $('#selEquipamento').val();
    var pista = $('#selPista').val();
    var classificacao = $('#selClassificacao').val();
    var buscarApenasVeiculoComImagem = document.getElementById("chkComImagem").checked;
    
    var dataIni = TratarDataHora($("#dataInicio").find("input").val());
    var dataFim = TratarDataHora($("#dataFim").find("input").val());
    
	var acaoFinal = veiculosSelecionados ? "exportarConsultaSelecionados" : acao;

    var dataStringPesquisa = "placa=" + placa + 
                                "&dataIni=" + dataIni + 
                                "&dataFim=" + dataFim +
                                "&equipamento=" + equipamento +
                                "&pista=" + pista +
                                "&classificacao=" + classificacao +
                                "&buscarApenasVeiculoComImagem=" + buscarApenasVeiculoComImagem +
                                "&formato=" + formato + 
                                "&motivo= " + motivo +
                                "&acao=" + acao;

    // Se há veículos selecionados, adiciona parâmetro específico
    if (veiculosSelecionados) {
        dataStringPesquisa += "&veiculosSelecionados=" + veiculosSelecionados;
    }

    var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo";
    
    var parametros = {
        "placa" : placa, 
        "dataIni" : dataIni, 
        "dataFim" : dataFim, 
        "equipamento" : equipamento, 
        "pista" : pista, 
        "classificacao" : classificacao,
        "buscarApenasVeiculoComImagem" : buscarApenasVeiculoComImagem, 
        "motivo" : motivo, 
        "formato" : formato, 
        "acao" : acaoFinal
    };

    if (veiculosSelecionados) {
        parametros.veiculosSelecionados = veiculosSelecionados;
    }

    $("body").addClass("loading");
    $.ajax({
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

            if (sucesso) {
                GerarArquivoDownload(urlPesquisa, parametros);
            } else {
                WarningCsx_E_TimeOut_8000ms(msgResposta);
            }
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
            AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
        },
        beforeSend: function(jqXHR, settings){},
        complete: function(jqXHR, textStatus)
        {
            $("body").removeClass("loading");
        }
    });
}


function processaDados(event) 
{
	var cont_img = 0;
	var array_completo = [];
	
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
	
	var idVeiculoUltimo = null;
	var primeiroVeiculo = true;
	var filtroTipoVeiculo = $('#tiposVeiculo').val();
	var deveUsarTipoVeiculo = filtroTipoVeiculo.length > 0;

	if (deveUsarTipoVeiculo) {
		document.getElementById("coluna-dinamica").innerText = "Tipo Veic";
	} else {
		document.getElementById("coluna-dinamica").innerText = "Classificação";
	}

	$xml.find('Veiculo').each(function()
	{
		var $item = $(this);
		
		var idVeiculo = $item.find('id').text();
//		console.log('idVeiculo ' + idVeiculo.length)
		
		//Foi identificado que juntou id do veiculo id da imagem, 
		//com isso estava buscando os detalhes incorretamente
		//Para resolver, fizemos um corte de ID (UUID) de 36 posições
		if (idVeiculo.length > 36){
			idVeiculo = idVeiculo.slice(0, 36);
		}
		
		idVeiculoUltimo = idVeiculo;
		
		var idVeiculoAnterior = (primeiroVeiculo ? '' : $item.find('idVeiculoAnterior').text());
		var idVeiculoProximo = $item.find('idVeiculoProximo').text();

		var placa = $item.find('placa').text();
		var marca = $item.find('marca').text();
		var modelo = $item.find('modelo').text();
		var dataFormatada = $item.find('dataVeicFormatada').text();
//		var dataImportadoFormatada = $item.find('dataImportadoFormatada').text();
		var serieEquipamento = $item.find('serieEquipamento').text();
		var codigoEquipamento = ( ($item.find('codigoEquipamento').text() == null || $item.find('codigoEquipamento').text() == '') ? "N/D" : $item.find('codigoEquipamento').text() );
		var nomeEquipamento = $item.find('descLocal').text();
		var equipamento = serieEquipamento + ' - ' + nomeEquipamento;
		var classificacao = $item.find('classificacao').text();
		var tipoVeiculo = $item.find('tipoVeiculo').text();
		var faixa = $item.find('faixa').text();
		var velocidade = $item.find('velocidade').text();
		var categoria = $item.find('categoria').text();
		var numeroEixos = $item.find('numeroEixos').text();
		var rodagemDupla = $item.find('rodagemDupla').text();
		var comImagem = $item.find('comImagem').text() === "true";
		var possuiCoordenadas = $item.find('possuiCoordenadas').text() === "true";
		var listaImagens = $item.find('listaImagens');
		var possuiAlerta = $item.find('possuiAlerta').text() === "true";
        var placaMercosul = $item.find('placaMercosul').text() === "true";
        var corPlaca = $item.find('corPlaca').text();
        //console.log('corPlaca ' + corPlaca);
        var tipoPlaca = placaMercosul ? "Mercosul" : "Antigo";
		
		var params = "\'"+idVeiculo+"\'";
		
		var onclickDetalhar = comImagem ? ('onclick="AbrirDetalhesVeiculo(' + params + ')"') : '';
		var onclickMostrarMapa = possuiCoordenadas ? ('onclick="ExecutarPesquisaMapaPorIdAlvo(' + params + ')"') : '';
		var onclickVisualizarAlertas = possuiAlerta ? ('onclick="AbrirModalAlertaVeiculo(' + params + ')"') : '';
		var disabledDetalhar = !comImagem ? 'disabled' : '';
		var disabledMapa = !possuiCoordenadas ? 'disabled' : '';
		var disabledListaAlerta = !possuiAlerta ? 'disabled' : '';
		
		var htmlBotoesAcao = "<div class='d-flex justify-content-center align-items-center gap-1' role='group'>" + 
                            "<button id='btnListaAlertas_" + idVeiculo +  "' " + "type='button' class='btn btn-sm btn-warning d-flex align-items-center justify-content-center' data-bs-toggle='modal' data-bs-target='#modalAlertaVeiculo' title='Ver alertas' " + disabledListaAlerta + " " +
                            onclickVisualizarAlertas +
                            " style='width: 32px; height: 32px;'>" +
                            "<svg xmlns='http://www.w3.org/2000/svg' width='14' height='14' fill='currentColor' class='bi bi-exclamation-triangle' viewBox='0 0 16 16'>" +
                            "<path d='M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.146.146 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.163.163 0 0 1-.054.06.116.116 0 0 1-.066.017H1.146a.115.115 0 0 1-.066-.017.163.163 0 0 1-.054-.06.176.176 0 0 1 .002-.183L7.884 2.073a.147.147 0 0 1 .054-.057zm1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566z'/>" +
                            "<path d='M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995z'/>" +
                            "</svg>" +
                            "</button>" +
                            "<button id='btnDetalhar_" + idVeiculo +  "' type='button' class='btn btn-sm btn-primary d-flex align-items-center justify-content-center' data-bs-toggle='modal' data-bs-target='#modalDetalheVeiculo' title='Abrir detalhes do veículo' " +
                            "id-veiculo-anterior='"+idVeiculoAnterior+"' id-veiculo-proximo='"+idVeiculoProximo+"' " + 
                            disabledDetalhar + " " +
                            onclickDetalhar +
                            " style='width: 32px; height: 32px;'>" +
                            "<svg xmlns='http://www.w3.org/2000/svg' width='14' height='14' fill='currentColor' class='bi bi-search' viewBox='0 0 16 16'>" +
                            "<path d='M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z'></path>" +
                            "</svg>" +
                            "</button>" + 
                            "<button id='btnMostrarMapa_" + idVeiculo +  "' " + "type='button' class='btn btn-sm btn-info d-flex align-items-center justify-content-center' data-bs-toggle='modal' data-bs-target='#modalVisualizarMapa' title='Ver no mapa' " + disabledMapa + " " +
                            onclickMostrarMapa +
                            " style='width: 32px; height: 32px;'>" +
                            "<svg xmlns='http://www.w3.org/2000/svg' width='14' height='14' fill='currentColor' class='bi bi-map' viewBox='0 0 16 16'>" +
                            "<path fill-rule='evenodd' d='M15.817.113A.5.5 0 0 1 16 .5v14a.5.5 0 0 1-.402.49l-5 1a.502.502 0 0 1-.196 0L5.5 15.01l-4.902.98A.5.5 0 0 1 0 15.5v-14a.5.5 0 0 1 .402-.49l5-1a.5.5 0 0 1 .196 0L10.5.99l4.902-.98a.5.5 0 0 1 .415.103zM10 1.91l-4-.8v12.98l4 .8V1.91zm1 12.98 4-.8V1.11l-4 .8v12.98zm-6-.8V1.11l-4 .8v12.98l4-.8z'/>" +
                            "</svg>" +
                            "</button>" +
                            "<div class='form-check ms-2 mb-0 d-flex align-items-center'>" +
                            "<input class='form-check-input checkbox-exportar m-0' type='checkbox' value='" + idVeiculo + "' id='check_" + idVeiculo + "' style='transform: scale(1.2);'>" +
                            "<label class='form-check-label ms-1' for='check_" + idVeiculo + "'></label>" +
                            "</div>" +
                            "</div>";

		if ( ! IS_MODO_GRADE_IMAGENS )
		{
			var tableRef = document.getElementById("tabela").getElementsByTagName('tbody')[0];
	
			var row   = tableRef.insertRow(tableRef.rows.length);
			row.insertCell(0).innerHTML = "<small>" + equipamento + "</small>";
			row.insertCell(1).innerHTML = "<small>" + faixa + "</small>";
			row.insertCell(2).innerHTML = "<small>" + dataFormatada + "</small>";
//			row.insertCell(3).innerHTML = "<small>" + dataImportadoFormatada + "</small>";
			row.insertCell(3).innerHTML = "<small>" + placa + "</small>";
			if (deveUsarTipoVeiculo) {
				row.insertCell(4).innerHTML = "<small>" + tipoVeiculo + "</small>";
			} 
			else {
				row.insertCell(4).innerHTML = "<small>" + classificacao + "</small>";
			}
			row.insertCell(5).innerHTML = "<small>" + marca + "</small>";
			row.insertCell(6).innerHTML = "<small>" + modelo + "</small>";
			row.insertCell(7).innerHTML = "<small>" + velocidade + "</small>";
			row.insertCell(8).innerHTML = "<small>" + (numeroEixos == 0 ? '' : 'CAT '+categoria) + "</small>";
			row.insertCell(9).innerHTML = "<small>" + (numeroEixos == 0 ? '' : numeroEixos) + "</small>";
			row.insertCell(10).innerHTML = "<small>" + (numeroEixos == 0 ? '' : rodagemDupla == 0 ? 'NÃO' : 'SIM') + "</small>";
            row.insertCell(11).innerHTML = "<small>" + tipoPlaca + "</small>";
            row.insertCell(12).innerHTML = "<small>" + corPlaca + "</small>";
			row.insertCell(13).innerHTML = htmlBotoesAcao;
			
			$('input#txt_consulta').quicksearch('table#tabela tbody tr');
			
		}
		else
		{
			if(equipamento.length > 40)
				equipamento = equipamento.slice(0, 40);
			
			array_juncao 	= [];
			array_txt 		= [];
			array_imagem 	= []; 
			
			if(IMAGEM_ORIGINAL === false){
				array_txt[0] = equipamento + ' - Pista ' + faixa;
				array_txt[1] = 'Placa: ' + placa;
				array_txt[2] = dataFormatada; 				
			}
													
			var x = 0;
			listaImagens.find('imagem').each(function()
			{
				var $imagem = $(this);
				if (RETORNAR_IMAGENS_MODO_GRADE)
					array_imagem[x] = $imagem.find('imgBase64').text();
				else
					array_imagem[x] = $imagem.find('id').text();
					  			
				x = x + 1;
			});	
			
			array_juncao[0] = array_txt;
			array_juncao[1] = array_imagem;
			array_juncao[2] = idVeiculo;
			array_juncao[3] = idVeiculoAnterior;
			array_juncao[4] = idVeiculoProximo;
				
			array_completo[cont_img] = array_juncao; 
			cont_img = cont_img + 1;
		}
		
		primeiroVeiculo = false;
	});
	
	if (!IS_MODO_GRADE_IMAGENS)
	{
		var link_ultimo_veiculo = document.getElementById("btnDetalhar_"+idVeiculoUltimo);
		if (link_ultimo_veiculo)
			link_ultimo_veiculo.setAttribute('id-veiculo-proximo', '');
	}
	
	if (  IS_MODO_GRADE_IMAGENS && array_completo.length > 0)
	{
		if(array_completo.length > PAGINACAO_ITENS_POR_PAGINA){		
			alert('Quantidade de registros: ' + array_completo.length + '. Será apresentado somente os <b>primeiros ' + PAGINACAO_ITENS_POR_PAGINA + ' registros!');
			QUANTIDADE_REGISTROS_GRADE = PAGINACAO_ITENS_POR_PAGINA;
		}else{
			QUANTIDADE_REGISTROS_GRADE = array_completo.length; 
		}
		
		var array_ult_veic = array_completo[cont_img - 1];
		array_ult_veic[4] = '';
		array_completo[cont_img - 1] = array_ult_veic;
		
		cria_imagens_grade();
		tempo_draw_in_canvas 	= 100;
		tempo_transfere_to_img 	= 50; 
		
		for(let i=1; i <= QUANTIDADE_REGISTROS_GRADE; i++)
		{
			if (RETORNAR_IMAGENS_MODO_GRADE)
			{
				setTimeout(function() 
				{
					manipula_imagens(array_completo[i-1], i, tempo_transfere_to_img);
				}, tempo_draw_in_canvas)
				
				tempo_draw_in_canvas = tempo_draw_in_canvas + 300;
			}
			else
			{
				manipula_imagens(array_completo[i-1], i, null);
			}
		}	
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
	document.getElementById("chkComImagem").checked = true;
	document.getElementById("chkRegistroFato").checked = false;
	document.getElementById("chkUltimaPassagem").checked = false;
    document.getElementById("divFiltrosRegistroFato").style.display = "none";
    document.getElementById("selCaracteristicaRegistro").value = "";
    document.getElementById("selTipoRegistro").innerHTML = '<option value="">Selecione a característica primeiro</option>';
    document.getElementById("selTipoRegistro").disabled = true;
    document.getElementById("selNaturezaRegistro").innerHTML = '<option value="">Selecione o tipo primeiro</option>';
    document.getElementById("selNaturezaRegistro").disabled = true;
    FILTRO_REGISTRO_FATO_ATIVO = false;
	
  	$dataIni = $("#dataInicio").find("input");
    $dataIni.val('');
    $dataIni.removeData();
    
    $dataFim = $("#dataFim").find("input");
    $dataFim.val('');
    $dataFim.removeData();
}

function modo_grade()
{
	if (IS_MODO_GRADE_IMAGENS){
		IS_MODO_GRADE_IMAGENS = false;
		PAGINACAO_ITENS_POR_PAGINA = 8;
	}
	else{	
		IS_MODO_GRADE_IMAGENS = true;
	}
	
	fecharModalParametros();
	GerenciarBotaoModoGrade();
	limpar_imagens_grade();
	ReiniciarComponentePaginacao();
}

function GerenciarBotaoModoGrade()
{
	if (IS_MODO_GRADE_IMAGENS)
	{
		document.getElementById("lista_resultado").style.display = "none";
		document.getElementById("canvas_temp").style.display = "none";
		document.getElementById("grade").className = "btn btn-info";
		document.getElementById("grade").innerText = "VER EM GRADE";		
		document.getElementById("grade_itens").style.display = "block"; 	
	}
	else
	{
		document.getElementById("lista_resultado").style.display = "block";
		document.getElementById("canvas_temp").style.display = "block";
		document.getElementById("grade").className = "btn btn-secondary";
		document.getElementById("grade").innerText = "VER EM GRADE";
		document.getElementById("grade_itens").style.display = "none";				
	}	
}

function limpar_imagens_grade()
{
	if (IS_MODO_GRADE_IMAGENS)
	{
		var freewall = document.getElementById("freewall");
		if (freewall)
		{
			freewall.innerHTML = "";
		}
	}
}

function cria_imagens_grade()
{
	var w = 1, h = 1, html = '', limitItem = (QUANTIDADE_REGISTROS_GRADE < PAGINACAO_ITENS_POR_PAGINA ? QUANTIDADE_REGISTROS_GRADE : PAGINACAO_ITENS_POR_PAGINA) + 1;
	for (var i = 1; i < limitItem; ++i) 
	{
		var temp = "<div class='brick' style='width:{width}px;'><img id='imagem_" + i + "' src='' width='100%' class='img-thumbnail' onclick='mostra_detalhe(this);'  data-bs-toggle='modal' data-bs-target='#modalDetalheVeiculo'></div>";
		w = 1;
		html += temp.replace(/\{width\}/g, w*300).replace("{index}", i + 1);
	}
	$("#freewall").html(html);

	var wall = new Freewall("#freewall");
	wall.reset({
		selector: '.brick',
		animate: true,
		cellW: 150,
		cellH: 'auto',
		onResize: function() {
			wall.fitWidth();
		}
	});

	var images = wall.container.find('.brick');
	images.find('img').load(function() {
		wall.fitWidth();
	});	
}

function manipula_imagens(array_juncao, id, tempo_ini)
{
	var textos = array_juncao[0];
	var imagens = array_juncao[1];
	var idVeiculo = array_juncao[2];
	var idVeiculoAnterior = array_juncao[3];
	var idVeiculoProximo = array_juncao[4];
	var idImagem = imagens[0];
	
	if (RETORNAR_IMAGENS_MODO_GRADE)
		PrepararImagensGradeJS (id, idVeiculo, idVeiculoAnterior, idVeiculoProximo, textos, imagens, tempo_ini);
	else
		PrepararImagensGrade(id, idImagem, idVeiculo, idVeiculoAnterior, idVeiculoProximo, textos.join(";"));
}


function PrepararImagensGrade(id, idImagem, idVeiculo, idVeiculoAnterior, idVeiculoProximo, textos)
{
	var src = "/MuralhaDigital/Veiculo/Imagem?acao=prepararImgGrade&id=" + idImagem + "&texto=" + textos;
	var img = document.getElementById('imagem_' + id);
	img.src = src;    
	img.alt = idVeiculo;
	img.setAttribute("id-veiculo-anterior", idVeiculoAnterior);
	img.setAttribute("id-veiculo-proximo", idVeiculoProximo);
//	console.log(img);
}

function PrepararImagensGradeJS (id, idVeiculo, idVeiculoAnterior, idVeiculoProximo, textos, imagens, tempo_ini)
{
	var canvas = document.getElementById('canvas_temp');
	var context = canvas.getContext('2d');
	imprime_texto_na_imagem(textos, imagens, context);
	
	//É preciso deste tempo para que o onload do canvas seja finalizado
	//E então a imagem do canvas é transferido pra imagem final
	setTimeout(function() {
		transfere_canvas_tag_img(canvas, id, idVeiculo, idVeiculoAnterior, idVeiculoProximo);
	}, tempo_ini);
}

function imprime_texto_na_imagem(textos, imagens, context)
{
	base_image = new Image();
	base_image.src = "data:image/jpg;base64," + imagens[0]; 	  
	base_image.onload = function()
	{
		rect_height = 25 * textos.length;
	    context.drawImage(base_image, 0, 0);
		context.beginPath();
		context.rect(4, 4, 550, rect_height);
		context.fillStyle = 'white';
		context.fill();
		context.lineWidth = 1;
		context.strokeStyle = 'blue';
		context.fillStyle = 'black';
		context.stroke();
	  	context.font = 'bold 12pt Verdana';
		
		var pos_x = 6;
		var pos_y = 20;
		var acrescenta = 25;
		for (let i = 0; i < textos.length; i++) 
		{
			context.fillText(textos[i], pos_x, pos_y);
			pos_y = pos_y + acrescenta;
		}	
	}
	
//	console.log("imprime_texto_na_imagem | idVeiculo: " + idVeiculo + " | Data: " + date_to_string_with_milliseconds(new Date(Date.now())).toString());
}	

function transfere_canvas_tag_img(canvas1, id, idVeiculo, idVeiculoAnterior, idVeiculoProximo)
{
	var imageObject = new Image();
	imageObject.src = canvas1.toDataURL("image/jpeg");      

	var img = document.getElementById('imagem_' + id);
	img.src = imageObject.src;    
	img.alt = idVeiculo;
	img.setAttribute("id-veiculo-anterior", idVeiculoAnterior);
	img.setAttribute("id-veiculo-proximo", idVeiculoProximo);
//	console.log(img);
//	console.log("transfere_canvas_tag_img | idVeiculo: " + idVeiculo + " | Data: " + date_to_string_with_milliseconds(new Date(Date.now())).toString());
}

function date_to_string_with_milliseconds(date){
  let date_str = date.toString() 
  let date_without_milliseconds = new Date(date_str) // truncated date since milliseconds are not included
  let milliseconds_delta = date - date_without_milliseconds
  let date_str_with_milliseconds = date_str.replace(/(^.*:\d\d:\d\d)(.*$)/, `$1:${milliseconds_delta}$2`)
  return date_str_with_milliseconds
}

function mostra_detalhe(obj)
{
	idVeiculo = obj.alt;
	AbrirDetalhesVeiculo(idVeiculo);
}


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
//		console.log("CancelarRequisicao")
//		console.log(REQUEST_DB);
		if (REQUEST_DB != null)
		{
//			console.log("Cancelando requisição")
			REQUEST_DB.abort();
//			console.log(REQUEST_DB);
		}
	}
	catch (ex)
	{
		AlertCsx_E_TimeOut_8000ms('Erro ao cancelar requisição!!');
		$("body").removeClass("loading");
	}

}

function ObterListaCores() {
	var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo";
	var dataStringPesquisa = "acao=obterListaCores";

	$.ajax({
		type: "GET",
		url: urlPesquisa,
		data: dataStringPesquisa,
		dataType: "json", 

		success: function (data) {
//			console.log(data); 
			CarregaComboCores(data); 
		},

		error: function () {
			AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!');
		},

	});
}

function ObterListaUfs() {
	var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo";
	var dataStringPesquisa = "acao=obterListaUf";

	$.ajax({
		type: "GET",
		url: urlPesquisa,
		data: dataStringPesquisa,
		dataType: "json", 

		success: function (data) {
//			console.log(data); 
			CarregaComboUfs(data); 
		},

		error: function () {
			AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!');
		},

	});
}


function ObterListaTiposVeiculo() {
    var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo";
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

/**
 * Abre a página de validação de imagem em uma nova aba
 */
function abrirValidacaoImagem() {
	window.open('/muralha-digital/pages/assinatura/validacao-imagem.jsp', '_blank');
}

function ObterListaMarcas() {
    var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo";
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
    var urlPesquisa = urlRoot + "MuralhaDigital/Veiculo";
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

function verificarPlaca(placa) {
    $.ajax({
        url: '/MuralhaDigital/Veiculo',
        method: 'GET',
        data: {
            acao: 'verificarPlaca',
            placa: placa
        },
        dataType: 'xml',
        success: function(response) {
            if (response.existe) {
         		temRegistro = true;
            } else {
                temRegistro = false;
            }
        },
        error: function(xhr, status, error) {
            console.error("Erro na requisição:", error);
        }
    });
}

function RegistrarPesquisaTela(placa, dataIni, dataFim) {
    var motivo = "Pesquisa em tela - ";
    
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
            // Sucesso silencioso - não precisa mostrar mensagem para o usuário
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error('Erro ao registrar pesquisa:', errorThrown);
        }
    });
}

function coletarVeiculosSelecionados() {
    var checkboxes = document.querySelectorAll('.checkbox-exportar:checked');
    var ids = Array.from(checkboxes).map(function(checkbox) {
        return checkbox.value;
    });
    
   // console.log("Checkboxes selecionados:", checkboxes.length);
   // console.log("IDs coletados:", ids);
    
    if (ids.length === 0) {
        return null;
    }
    
    return ids.join(',');
}

function gerarRelatorioManual(formato, veiculosSelecionados, motivo) {
    var dadosRelatorio = coletarDadosTabela(veiculosSelecionados);
    
    $("body").addClass("loading");
    
    var form = document.createElement('form');
    form.method = 'POST';
    form.action = urlRoot + 'MuralhaDigital/Veiculo';
    form.target = '_blank'; 
    form.style.display = 'none';
    
    function addCampo(nome, valor) {
        var input = document.createElement('input');
        input.type = 'hidden';
        input.name = nome;
        input.value = valor;
        form.appendChild(input);
    }
    
    addCampo('acao', 'exportarConsultaSelecionadosManual');
    addCampo('formato', formato);
    addCampo('motivo', motivo);
    addCampo('dados', JSON.stringify(dadosRelatorio));
    
    document.body.appendChild(form);
    form.submit();
    document.body.removeChild(form);
    
    setTimeout(function() {
        $("body").removeClass("loading");
    }, 1000);
}

function coletarDadosTabela(veiculosSelecionados) {
    var dados = [];
    var idsSelecionados = veiculosSelecionados.split(',');
    
    // Mapear IDs selecionados para facilitar busca
    var idsMap = {};
    idsSelecionados.forEach(function(id) {
        idsMap[id] = true;
    });
    
    // Percorrer todas as linhas da tabela
    $('#tabela tbody tr').each(function() {
        var $linha = $(this);
        var checkbox = $linha.find('.checkbox-exportar');
        var idVeiculo = checkbox.val();
        
        // Se este veículo está selecionado
        if (idsMap[idVeiculo]) {
            var celulas = $linha.find('td');
            
            var dado = {
                id: idVeiculo,
                equipamento: $(celulas[0]).text().trim(),
                faixa: $(celulas[1]).text().trim(),
                data: $(celulas[2]).text().trim(),
                placa: $(celulas[3]).text().trim(),
                classificacao: $(celulas[4]).text().trim(),
                marca: $(celulas[5]).text().trim(),
                modelo: $(celulas[6]).text().trim(),
                velocidade: $(celulas[7]).text().trim(),
                categoria: $(celulas[8]).text().trim(),
                numeroEixos: $(celulas[9]).text().trim(),
                rodagemDupla: $(celulas[10]).text().trim()
            };
            
            dados.push(dado);
        }
    });
    
    return dados;
}