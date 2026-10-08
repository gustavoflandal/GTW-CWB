var PAGINACAO_TOTAL_REGISTROS = 0;
var PAGINACAO_ITENS_POR_PAGINA = 5;
var PAGINACAO_TOTAL_PAGINAS_VISIVEIS_LIMITE = 5;
var PAGINACAO_TOTAL_PAGINAS_VISIVEIS = PAGINACAO_TOTAL_PAGINAS_VISIVEIS_LIMITE;
var PAGINACAO_OFFSET = 0;
var PAGINACAO_PAGINA_ATUAL = 1;
var PAGINACAO_TOTAL_PAGINAS = 1;
var PAGINACAO_ITENS_POR_PAGINA_OPCOES = uniq = [...new Set([PAGINACAO_ITENS_POR_PAGINA,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20].sort((a,b)=>a-b))];
var idVeiculoPassagem = null;

$(document).ready(function() {		
	
	$('#cpfFato').mask('000.000.000-00');
});

function limparTabelaResultado() {
	$("#resultadoRegistrosFato").empty();
}

document.addEventListener("DOMContentLoaded", function() {
	const cpfInput = document.getElementById("cpfFato");

	cpfInput.addEventListener("input", function() {
		let value = cpfInput.value.replace(/\D/g, "");
		if (value.length > 11) value = value.slice(0, 11);

		let formatted = value;
		if (value.length > 9) {
			formatted = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
		} else if (value.length > 6) {
			formatted = value.replace(/(\d{3})(\d{3})(\d{1,3})/, "$1.$2.$3");
		} else if (value.length > 3) {
			formatted = value.replace(/(\d{3})(\d{1,3})/, "$1.$2");
		}

		cpfInput.value = formatted;
	});
});

function buscarFatos(event, reiniciarPaginaAtual) {

	const placa = document.getElementById("placaPassagem").value || "";
	const nome = document.getElementById("nomeEnvolvidoFato").value || "";
	const cpf = document.getElementById("cpfFato").value || "";
	const cpfFormatado = cpf.replace("-","").replace(".",'').replace(".",'').replace(".","").replace(" ","");
	const dataIni = document.getElementById("dataInicioFato").value || "";
	const dataFim = document.getElementById("dataFimFato").value || "";

	if (reiniciarPaginaAtual) {
		ReiniciaPaginaAtualComponentePaginacaoPassagens();
	}

	const params = new URLSearchParams({
		acao: 'obterLista',
		paginacaoItensPorPagina: PAGINACAO_ITENS_POR_PAGINA,
		paginacaoOffset: PAGINACAO_OFFSET
	});

	const url = `/MuralhaDigital/RegistroDeFato?acao=obterTodos&nome=${nome}&cpf=${cpfFormatado}&dataIni=${dataIni}&dataFim=${dataFim}&placa=${placa}`;

	$.ajax({
		type: "GET",
		url,
		data: params.toString(),
		dataType: "xml",

		success: (data, textStatus, jqXHR) => {
			const responseText = jqXHR.responseText;
			const $xml = $($.parseXML(responseText));

			const sucesso = $xml.find("sucesso").text();
			const msg = $xml.find("msgResposta").text();

			if (sucesso === "true" || sucesso === "") {
				processaDadosFatos(responseText);
			} else {
				ErrorNotification(msg, "");
			}
		},

		error: () => {
			ErrorNotification("Erro ao processar requisição ao servidor!!", "");
		},

		complete: (jqXHR) => {
			AtualizarParametrosComponentePaginacaoPassagens(jqXHR.responseText);
			CriarComponentePaginacaoPassagens();
		}
	});
}

function AtualizarItensPaginacaoPassagens() {
	buscarFatos(false);
}

function adicionaUnicos(array, item, chaveFunc) {
  const chave = chaveFunc(item);
  if (!array.some(e => chaveFunc(e) === chave)) {
    array.push(item);
  }
}

function processaIndividuos(individuosNode) {
    if (!individuosNode) return "-";
    const individuos = individuosNode.getElementsByTagName("individuo");
    return Array.from(individuos).map(ind => {
        const nome = ind.getElementsByTagName("nome")[0]?.textContent?.trim() || "-";
        const cpf = ind.getElementsByTagName("cpf")[0]?.textContent?.trim() || "-";
        return `${nome}${cpf !== "-" ? ` (CPF: ${cpf})` : ""}`;
    }).join("<br> ") || "-";
}

function processaVeiculos(veiculosNode) {
    if (!veiculosNode) return "-";
    const veiculos = veiculosNode.getElementsByTagName("veiculo");
    return Array.from(veiculos).map(veic => {
        const placa = veic.getElementsByTagName("placa")[0]?.textContent?.trim() || "-";
        const marca = veic.getElementsByTagName("marca")[0]?.textContent?.trim() || "-";
        const modelo = veic.getElementsByTagName("modelo")[0]?.textContent?.trim() || "-";
        const cor = veic.getElementsByTagName("cor")[0]?.textContent?.trim() || "-";
        return `${placa}${marca !== "-" ? ` (${marca}, ${modelo}, ${cor})` : ""}`;
    }).join("<br> ") || "-";
}

function processaEnderecos(enderecosNode) {
    if (!enderecosNode) return "-";
    const enderecos = enderecosNode.getElementsByTagName("endereco");
    return Array.from(enderecos).map(end => {
        const rua = end.getElementsByTagName("rua")[0]?.textContent?.trim() || "-";
        const numero = end.getElementsByTagName("numero")[0]?.textContent?.trim() || "-";
        const bairro = end.getElementsByTagName("bairro")[0]?.textContent?.trim() || "-";
        const cidade = end.getElementsByTagName("cidade")[0]?.getElementsByTagName("nome")[0]?.textContent?.trim() || "-";
        return `${rua}, ${numero} - ${bairro} (${cidade})`;
    }).join("<br> ") || "-";
}

function processaDadosFatos(event) {

    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(event, "application/xml");
    const registros = xmlDoc.getElementsByTagName("RegistroDeFato");    
    
    const tabela = document.getElementById("resultadoRegistrosFato");
    tabela.innerHTML = ""; // Limpa a tabela antes de preencher
    
    if(registros.length < 1){
		alert("Nenhum resultado encontrado!")
	}

    const registrosOrdenados = Array.from(registros).sort((a, b) => {
        const dataA = new Date(a.getElementsByTagName("dataCriacao")[0]?.textContent);
        const dataB = new Date(b.getElementsByTagName("dataCriacao")[0]?.textContent);
        return dataB - dataA;
    });
    
    let html = "";
    for (const reg of registrosOrdenados) {
        const id = reg.getElementsByTagName("id")[0]?.textContent || "-";
        const tipo = reg.getElementsByTagName("tipoDescricao")[0]?.textContent?.trim() || "-";
        const situacao = reg.getElementsByTagName("statusDescricao")[0]?.textContent?.trim() || "-";
        const dataCriacao = reg.getElementsByTagName("dataCriacao")[0]?.textContent || "";

        // Processa indivíduos
        const individuosNode = reg.getElementsByTagName("individuos")[0];
        const individuosFormatados = processaIndividuos(individuosNode);

        // Processa veículos
        const veiculosNode = reg.getElementsByTagName("veiculos")[0];
        const veiculosFormatados = processaVeiculos(veiculosNode);
        
        // Processa endereços
        const enderecosNode = reg.getElementsByTagName("enderecos")[0];
        const enderecosFormatados = processaEnderecos(enderecosNode);

        const dataFormatada = formatarDataBrasileira(dataCriacao);

        html += `
            <tr>
                <td>${tipo}</td>
                <td>${individuosFormatados}</td>
                <td>${veiculosFormatados}</td>
                <td>${enderecosFormatados}</td>
                <td>${situacao}</td>
                <td>${dataFormatada}</td>
                <td>
                    <button onclick="inserirPassagemVeiculo(event, ${id})" title='Detalhar' class="btn btn-primary" id="btnInserirPassagem">
            			<i class="fa fa-paperclip"></i>
          			</button>
                </td>
            </tr>`;
    }

    tabela.innerHTML = html;      
}

function formatarDataBrasileira(dataISO) {
    if (!dataISO) return "-";
    const date = new Date(dataISO);
    return date.toLocaleDateString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function limparFiltros() {
	document.getElementById("placaPassagem").value = "";
}

function SuccessNotification(title, text) {
	//necessario <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	Swal.fire({
		title: title,
		icon: "success",
		text: text,
	});
}

function ErrorNotification(title, text) {
	if (!title) {
		title = 'Erro ao processar requisição ao servidor!!';
	}
	//necessario <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	Swal.fire({
		title: title,
		icon: "error",
		text: text,
	});
}

function removerParametroUrl(nomeParametro) {
	const url = new URL(window.location.href);
	url.searchParams.delete(nomeParametro);
	history.replaceState({}, document.title, url.toString());
}

function CarregarPametrosPaginacaoPassagens()
{
	PAGINACAO_TOTAL_REGISTROS = 0;
	PAGINACAO_ITENS_POR_PAGINA = 5;
	PAGINACAO_TOTAL_PAGINAS_VISIVEIS_LIMITE = 5;
	PAGINACAO_TOTAL_PAGINAS_VISIVEIS = PAGINACAO_TOTAL_PAGINAS_VISIVEIS_LIMITE;
	PAGINACAO_OFFSET = 0;
	PAGINACAO_PAGINA_ATUAL = 1;
	PAGINACAO_TOTAL_PAGINAS = 1;

//	console.log("CarregarPametrosPaginacao");
//	console.log("PAGINACAO_TOTAL_REGISTROS: " + PAGINACAO_TOTAL_REGISTROS);
//	console.log("PAGINACAO_TOTAL_PAGINAS: " + PAGINACAO_TOTAL_PAGINAS);
//	console.log("PAGINACAO_PAGINA_ATUAL: " + PAGINACAO_PAGINA_ATUAL);
//	console.log("PAGINACAO_TOTAL_PAGINAS_VISIVEIS: " + PAGINACAO_TOTAL_PAGINAS_VISIVEIS);
}


function CriarComponentePaginacaoPassagens()
{
	$('#componente_paginacao_passagens').empty();
	$('#componente_paginacao_passagens').removeData("twbs-pagination");
	$('#componente_paginacao_passagens').unbind("page");
		
	if (PAGINACAO_TOTAL_REGISTROS > 0)
	{
		PAGINACAO_TOTAL_PAGINAS = Math.ceil(PAGINACAO_TOTAL_REGISTROS / PAGINACAO_ITENS_POR_PAGINA);
		PAGINACAO_PAGINA_ATUAL = PAGINACAO_PAGINA_ATUAL > PAGINACAO_TOTAL_PAGINAS ? PAGINACAO_TOTAL_PAGINAS : PAGINACAO_PAGINA_ATUAL;
		PAGINACAO_TOTAL_PAGINAS_VISIVEIS = PAGINACAO_TOTAL_PAGINAS < PAGINACAO_TOTAL_PAGINAS_VISIVEIS ? PAGINACAO_TOTAL_PAGINAS : PAGINACAO_TOTAL_PAGINAS_VISIVEIS;
	
		$('#componente_paginacao_passagens').twbsPagination({
			startPage: PAGINACAO_PAGINA_ATUAL,
			totalPages: PAGINACAO_TOTAL_PAGINAS,
            visiblePages: PAGINACAO_TOTAL_PAGINAS_VISIVEIS,
            first: "&larr;",
            prev: "&laquo;",
            next: "&raquo;",
            last: "&rarr;",
            pageVariable: '{{page}}',
			totalPagesVariable: '{{total_pages}}',
        }).on('page', function (event, page) {
            PAGINACAO_OFFSET = PAGINACAO_ITENS_POR_PAGINA * (page-1);
            PAGINACAO_PAGINA_ATUAL = page;
            AtualizarItensPaginacaoPassagens();
        });
    }
}

function ReiniciarComponentePaginacaoPassagens()
{
	PAGINACAO_TOTAL_REGISTROS = 0;
	CriarComponentePaginacao();
}

function ReiniciaPaginaAtualComponentePaginacaoPassagens()
{
	PAGINACAO_PAGINA_ATUAL = 1;
	PAGINACAO_OFFSET = PAGINACAO_ITENS_POR_PAGINA * (PAGINACAO_PAGINA_ATUAL-1);
}

function AtualizarParametrosComponentePaginacaoPassagens(event)
{
	var xmlDoc = $.parseXML( event );
	var $xml = $(xmlDoc);
		
	var totalRegistros = $xml.find('paginacao totalRegistros').text();
	
	if (totalRegistros != "")
		PAGINACAO_TOTAL_REGISTROS = parseInt(totalRegistros);
	else
		ReiniciarComponentePaginacao();
		
	PAGINACAO_TOTAL_PAGINAS = Math.ceil(PAGINACAO_TOTAL_REGISTROS / PAGINACAO_ITENS_POR_PAGINA);
	PAGINACAO_PAGINA_ATUAL = PAGINACAO_PAGINA_ATUAL > PAGINACAO_TOTAL_PAGINAS ? PAGINACAO_TOTAL_PAGINAS : PAGINACAO_PAGINA_ATUAL;
	PAGINACAO_TOTAL_PAGINAS_VISIVEIS = PAGINACAO_TOTAL_PAGINAS > PAGINACAO_TOTAL_PAGINAS_VISIVEIS_LIMITE ? PAGINACAO_TOTAL_PAGINAS_VISIVEIS_LIMITE : PAGINACAO_TOTAL_PAGINAS;
}

function AlterarRegistrosPorPaginaPassagens()
{
	var registrosPorPagina = document.getElementById("registrosPorPagina");
	
	if (registrosPorPagina)
	{
		var quantidade = (registrosPorPagina.value != "" ? parseInt(registrosPorPagina.value) : 0);
		if (quantidade > 0) PAGINACAO_ITENS_POR_PAGINA = quantidade;
	}
}

function inserirPassagemVeiculo(event, idRegistroFato){
	
	if(confirm("Deseja vincular essa passagem à esse registro de fato?")){
		
		event.preventDefault();
	
		const url = `/MuralhaDigital/RegistroDeFato?acao=cadastrarPassagem&idRegistroFato=${idRegistroFato}&idVeiculo=${idVeiculoPassagem}`;
	
		$.ajax({
			type: "POST",
			url,
			dataType: "xml",
			success: (data, textStatus, jqXHR) => {
				const responseText = jqXHR.responseText;
				const $xml = $($.parseXML(responseText));
	
				const sucesso = $xml.find("sucesso").text();
				const msg = $xml.find("msgResposta").text();
	
				if (sucesso === "true" || sucesso === "") {
					alert("Passagem de veículo vinculada com sucesso!")
				} else {
					ErrorNotification(msg, "");
				}
			},
	
			error: () => {
				$("body").removeClass("loading");
				ErrorNotification("Erro ao processar requisição ao servidor!!", "");
			}
		});		
	}else{
		return;
	}
}

function abrirModalPassagemComId(idVeiculo) {
	
	idVeiculoPassagem = null;
	idVeiculoPassagem = idVeiculo;
	
	const novaModal = new bootstrap.Modal(document.getElementById('modalPassagemVeiculo'),{
		backdrop: 'static',
		keyboard: false
	});
	novaModal.show();
}

function limparCampos(){
	document.getElementById("placaPassagem").value = "";
	document.getElementById("nomeEnvolvidoFato").value = "";
	document.getElementById("cpfFato").value = "";
	document.getElementById("dataInicioFato").value = "";
	document.getElementById("dataFimFato").value = "";
}