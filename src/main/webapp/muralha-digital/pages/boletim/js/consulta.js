$(document).ready(function() {
	const boletimIds = window.boletimIds || [];

	if (boletimIds.length === 0 || (boletimIds.length === 1 && boletimIds[0].trim() === "")) {
		executaPesquisa();
	} else {
		executaPesquisaPorIds(boletimIds);
	}

	CarregarPametrosPaginacao();
	carregarCombos();
	CarregarComponenteData(true);
});

async function carregarCombos() {
	$("body").addClass("loading");

	await obterTiposBoletins();
	await obterSituacao();
	await obterCidade();

	$("body").removeClass("loading");
}

function limparTabelaResultado() {
	$("#resultadoTabela").empty();
}

function executaPesquisaPorIds(boletimIds) {
	limparTabelaResultado();
	if (!Array.isArray(boletimIds) || boletimIds.length === 0) {
		console.warn("Nenhum boletimId foi informado.");
		return;
	}

	// Montar os parâmetros da requisição
	const params = new URLSearchParams();
	params.append("acao", "obterListaPorIds");
	params.append("boletimIds", boletimIds.join(",")); // separa os GUIDs com vírgula

	// Construir a URL base da API ou do endpoint
		const url = "/MuralhaDigital/Boletim";

	// Exibir loading, se necessário
	$("body").addClass("loading");

	$.ajax({
		type: "GET",
		url: url,
		data: params.toString(),
		dataType: "xml",

		success: (data, textStatus, jqXHR) => {
			const responseText = jqXHR.responseText;
			const $xml = $($.parseXML(responseText));

			const sucesso = $xml.find("sucesso").text();
			const msg = $xml.find("msgResposta").text();

			if (sucesso === "true" || sucesso === "") {
				processaDados(responseText);
			} else {
				ErrorNotification(msg, "");
			}

			$("body").removeClass("loading");
		},

		error: () => {
			$("body").removeClass("loading");
			ErrorNotification("Erro ao processar requisição ao servidor!!", "");
		},

		complete: (jqXHR) => {
			AtualizarParametrosComponentePaginacao(jqXHR.responseText);
			CriarComponentePaginacao();
		}
	});
}


function executaPesquisa(reiniciarPaginaAtual) {
	limparTabelaResultado();
	const getValue = (id) => document.getElementById(id)?.value?.trim() || "";

	const tipoBoletim = getValue("tipoBoletim");
	const placa = getValue("placaBoletim");
	const situacao = getValue("situacaoBoletim");
	let cpf = getValue("cpfBoletim").replace(/[^a-zA-Z0-9]/g, '');
	const cidade = getValue("cidadeBoletim");
	const dataIni = getValue("dataInicioBoletim");
	const dataFim = getValue("dataFimBoletim");

	const formatarDataSQL = (dataPtBr) => {
		if (!dataPtBr) return null;
		const momentDate = moment(dataPtBr, 'DD/MM/YYYY', true);
		return momentDate.isValid() ? momentDate.format('YYYY-MM-DD') : null;
	};

	const dataIniSQL = formatarDataSQL(dataIni);
	const dataFimSQL = formatarDataSQL(dataFim);

	if (reiniciarPaginaAtual) {
		ReiniciaPaginaAtualComponentePaginacao();
	}

	const params = new URLSearchParams({
		acao: 'obterLista',
		tipoOcorrencia: tipoBoletim,
		placa,
		situacao,
		cpf,
		cidade,
		paginacaoItensPorPagina: PAGINACAO_ITENS_POR_PAGINA,
		paginacaoOffset: PAGINACAO_OFFSET
	});

	if (dataIniSQL) params.append('dataIni', dataIniSQL);
	if (dataFimSQL) params.append('dataFim', dataFimSQL);

	const url = "/MuralhaDigital/Boletim";

	$("body").addClass("loading");

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
				processaDados(responseText);
			} else {
				ErrorNotification(msg, "");
			}

			$("body").removeClass("loading");
		},

		error: () => {
			$("body").removeClass("loading");
			ErrorNotification("Erro ao processar requisição ao servidor!!", "");
		},

		complete: (jqXHR) => {
			AtualizarParametrosComponentePaginacao(jqXHR.responseText);
			CriarComponentePaginacao();
		}
	});
}

function AtualizarItensPaginacao() {
	executaPesquisa(false);
}

function processaDados(event) {
	const parser = new DOMParser();
	const xmlDoc = parser.parseFromString(event, "application/xml");
	const boletins = xmlDoc.getElementsByTagName("Boletim");
	const tabela = document.getElementById("resultadoTabela");
	tabela.innerHTML = "";

	// Objeto para agrupar boletins por ID
	const boletinsAgrupados = {};

	// 1. Agrupa os boletins por ID
	for (let i = 0; i < boletins.length; i++) {
		const boletim = boletins[i];
		const id = boletim.getElementsByTagName("id")[0]?.textContent || "";

		// Se o boletim ainda não foi registrado, cria uma entrada no mapa
		if (!boletinsAgrupados[id]) {
			boletinsAgrupados[id] = {
				id,
				tipo: boletim.getElementsByTagName("tipo")[0]?.textContent || "",
				individuos: [],
				veiculos: [], // Armazena objetos {placa, marca}
				ruaCidade: boletim.getElementsByTagName("ruaCidade")[0]?.textContent || "",
				situacao: boletim.getElementsByTagName("situacao")[0]?.textContent || "",
				dataCriacao: boletim.getElementsByTagName("dataCriacao")[0]?.textContent || "",
			};
		}

		// Adiciona indivíduos (sem duplicatas)
		const individuoNodes = boletim.getElementsByTagName("individuos");
		for (let j = 0; j < individuoNodes.length; j++) {
			const nome = individuoNodes[j].getElementsByTagName("nome")[0]?.textContent?.trim();
			const cpf = individuoNodes[j].getElementsByTagName("cpf")[0]?.textContent?.trim();
			if (nome && !boletinsAgrupados[id].individuos.some(ind => ind.cpf === cpf)) {
				boletinsAgrupados[id].individuos.push({
					nome,
					cpf: cpf || "-"
				});
			}
		}

		// Adiciona veículos (verificando duplicidade por placa)
		const veiculoNodes = boletim.getElementsByTagName("veiculos");
		for (let j = 0; j < veiculoNodes.length; j++) {
			const placa = veiculoNodes[j].getElementsByTagName("placa")[0]?.textContent?.trim();
			const marca = veiculoNodes[j].getElementsByTagName("marca")[0]?.textContent?.trim();
			if (placa && !boletinsAgrupados[id].veiculos.some(v => v.placa === placa)) {
				boletinsAgrupados[id].veiculos.push({
					placa,
					marca: marca || "-"
				});
			}
		}
	}

	// 2. Gera as linhas da tabela (agrupadas por ID)
	for (const id in boletinsAgrupados) {
		const boletim = boletinsAgrupados[id];

		// Formata nomes e CPFs
		const individuosFormatados = boletim.individuos.map(ind =>
			`${ind.nome}${ind.cpf !== "-" ? ` (CPF: ${ind.cpf})` : ""}`
		).join("<br> ") || "-";

		// Formata veículos (sem duplicatas)
		const veiculosFormatados = boletim.veiculos.map(veiculo =>
			`${veiculo.placa}${veiculo.marca !== "-" ? ` (${veiculo.marca})` : ""}`
		).join("<br> ") || "-";

		// Formata data
		const dataFormatada = formatarDataBrasileira(boletim.dataCriacao);

		const linha = `
            <tr>
                <td>${boletim.tipo}</td>
                <td>${individuosFormatados}</td>
                <td>${veiculosFormatados}</td>
                <td>${boletim.ruaCidade}</td>
                <td>${boletim.situacao}</td>
                <td>${dataFormatada}</td>
                <td>
                    <button onclick="abrirModalVisualizarBoletim(${id})" title='Detalhar' class="btn btn-primary">
                        <i class="fas fa-eye"></i>
                    </button>
                </td>
            </tr>`;
		tabela.innerHTML += linha;
	}

	// Ativa o quicksearch (se necessário)
	$('#txt_consulta').quicksearch('#resultadoTabela tr');
}

function formatarDataBrasileira(dataISO) {
	if (!dataISO) return ""; // Se a data estiver vazia, retorna vazio

	const data = new Date(dataISO);

	// Extrai dia, mês, ano, horas e minutos
	const dia = String(data.getDate()).padStart(2, '0');
	const mes = String(data.getMonth() + 1).padStart(2, '0');
	const ano = data.getFullYear();
	const horas = String(data.getHours()).padStart(2, '0');
	const minutos = String(data.getMinutes()).padStart(2, '0');

	return `${dia}/${mes}/${ano} ${horas}:${minutos}`;
}

function limparFiltros() {
	// Zerar campos de texto
	document.getElementById("placaBoletim").value = "";
	document.getElementById("cpfBoletim").value = "";

	// Resetar selects para o valor vazio (que representa "Todos")
	document.getElementById("tipoBoletim").value = "";
	document.getElementById("cidadeBoletim").value = "";
	document.getElementById("situacaoBoletim").value = "";

	// Limpar campos de data (se estiver usando datetimepicker do Tempus Dominus)
	const dataInicioInput = document.getElementById("dataInicioBoletim");
	const dataFimInput = document.getElementById("dataFimBoletim");

	dataInicioInput.value = "";
	dataFimInput.value = "";

	// Se estiver usando Tempus Dominus: limpar o valor do picker também
	if (dataInicioInput._td) dataInicioInput._td.clear();
	if (dataFimInput._td) dataFimInput._td.clear();
}

function obterTiposBoletins() {
	var urlPesquisa = "/MuralhaDigital/Boletim/Tipo";

	return $.ajax({
		type: "GET",
		url: urlPesquisa,
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				const parser = new DOMParser();
				const xmlDoc = parser.parseFromString(jqXHR.responseText, "application/xml");
				const tipos = xmlDoc.getElementsByTagName('OcorrenciaTipo');
				const select = document.getElementById('tipoBoletim');
				// Adiciona cada tipo na ordem recebida
				for (let i = 0; i < tipos.length; i++) {
					const tipo = tipos[i];
					const id = tipo.getElementsByTagName('id')[0].textContent;
					const descricao = tipo.getElementsByTagName('descricao')[0].textContent;

					const option = document.createElement('option');
					option.value = id;
					option.textContent = descricao;
					select.appendChild(option);
				}
			} else {
				ErrorNotification(msgResposta, "");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("body").removeClass("loading");
			ErrorNotification('Erro ao processar requisição ao servidor!!', "");
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
}

function obterSituacao() {
	var urlPesquisa = "/MuralhaDigital/Boletim/Situacao";

	return $.ajax({
		type: "GET",
		url: urlPesquisa,
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				const parser = new DOMParser();
				const listaSituacoes = xmlDoc.getElementsByTagName('ListaSituacoes')[0];
				const situacoes = listaSituacoes.getElementsByTagName('Situacao');
				const select = document.getElementById('situacaoBoletim');
				// Adiciona cada situação como uma opção
				for (let i = 0; i < situacoes.length; i++) {
					const situacao = situacoes[i];
					const id = situacao.getElementsByTagName('id')[0].textContent;
					const descricao = situacao.getElementsByTagName('descricao')[0].textContent;

					const option = document.createElement('option');
					option.value = id;
					option.textContent = descricao;
					select.appendChild(option);
				}
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("body").removeClass("loading");
			ErrorNotification('Erro ao processar requisição ao servidor!!', "");
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
}



function obterCidade() {
	var urlPesquisa = "/MuralhaDigital/Boletim/Cidade";

	return $.ajax({
		type: "GET",
		url: urlPesquisa,
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				const parser = new DOMParser();
				const listaSituacoes = xmlDoc.getElementsByTagName('ListaCidades')[0];
				const situacoes = listaSituacoes.getElementsByTagName('Cidade');
				const select = document.getElementById('cidadeBoletim');
				// Adiciona cada situação como uma opção
				for (let i = 0; i < situacoes.length; i++) {
					const situacao = situacoes[i];
					const id = situacao.getElementsByTagName('id')[0].textContent;
					const descricao = situacao.getElementsByTagName('nome')[0].textContent;

					const option = document.createElement('option');
					option.value = id;
					option.textContent = descricao;
					select.appendChild(option);
				}
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("body").removeClass("loading");
			ErrorNotification('Erro ao processar requisição ao servidor!!', "");
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
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

document.addEventListener("DOMContentLoaded", function() {
	const cpfInput = document.getElementById("cpfBoletim");

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

/**
 * Script responsável por ler parâmetros da URL
 * e preencher automaticamente os campos do filtro
 * na tela de consulta de boletins.
 *
 * Parâmetros esperados na URL:
 * ----------------------------------------
 * - dataInicio : Data inicial no formato 'yyyy-MM-dd'
 * - dataFim    : Data final no formato 'yyyy-MM-dd'
 * - placa      : Placa do veículo (com ou sem traço)
 *
 * Caso encontre esses parâmetros:
 * - Preenche os inputs correspondentes na tela:
 *     • #dataInicioBoletim
 *     • #dataFimBoletim
 *     • #placaBoletim
 *
 * Além disso:
 * - Converte datas do formato 'yyyy-MM-dd' para 'dd/MM/yyyy'
 *   para compatibilidade com os campos da tela.
 *
 * - Executa a pesquisa automaticamente chamando executaPesquisa(true)
 */
document.addEventListener('DOMContentLoaded', () => {
    // Cria objeto para ler parâmetros da URL (query string)
    const urlParams = new URLSearchParams(window.location.search);

    // Recupera cada parâmetro individualmente
    const dataInicio = urlParams.get('dataInicio');
    const dataFim = urlParams.get('dataFim');
    const placa = urlParams.get('placa');

    // Se existir dataInicio, preenche o campo na tela
    if (dataInicio) {
        // Converte de yyyy-MM-dd → dd/MM/yyyy caso venha nesse formato
        const partes = dataInicio.split("-");
        if (partes.length === 3) {
            document.getElementById('dataInicioBoletim').value = `${partes[2]}/${partes[1]}/${partes[0]}`;
        } else {
            // Caso já venha no formato dd/MM/yyyy
            document.getElementById('dataInicioBoletim').value = dataInicio;
        }
    }

    // Mesmo processo para dataFim
    if (dataFim) {
        const partes = dataFim.split("-");
        if (partes.length === 3) {
            document.getElementById('dataFimBoletim').value = `${partes[2]}/${partes[1]}/${partes[0]}`;
        } else {
            document.getElementById('dataFimBoletim').value = dataFim;
        }
    }

    // Preenche o campo da placa, caso venha pela URL
    if (placa) {
        document.getElementById('placaBoletim').value = placa;
    }

    // Executa a pesquisa automaticamente após preencher os filtros
    executaPesquisa(true);
});